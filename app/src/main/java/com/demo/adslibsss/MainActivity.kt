package com.demo.adslibsss

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ads.adslib.AdsLib
import com.ads.adslib.admob.banner.BannerAdManager
import com.ads.adslib.admob.native_ad.NativeAdManager
import com.ads.adslib.config.remote.AdsConfigRepository
import com.ads.adslib.core.callback.AdCallback
import com.ads.adslib.core.callback.RewardCallback
import com.ads.adslib.core.model.AdError
import com.ads.adslib.core.model.AdNetwork
import com.ads.adslib.core.model.BannerAdSize
import com.ads.adslib.smart.SmartAdManager
import com.demo.adslibsss.databinding.MainActivityBinding

/**
 * Demo — every ad is now driven by the Remote Config JSON via
 * [AdsConfigRepository]. No hardcoded ad unit IDs here; the screen name
 * (e.g. "home", "search", "detail") selects the placement + waterfall.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding

    private lateinit var inlineBannerManager: BannerAdManager
    private lateinit var bottomBannerManager: BannerAdManager
    private var nativeAdManager: NativeAdManager? = null

    private var adsReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtons()
        initAds()
    }

    override fun onStart() {
        super.onStart()
        // Prepare rewarded on onStart/onStop (not onResume/onPause) so transient
        // pauses (dialogs, permission prompts) don't tear down & reload the ad.
        if (adsReady) SmartAdManager.prepareRewarded(this)
    }

    override fun onResume() {
        super.onResume()
        if (::inlineBannerManager.isInitialized) inlineBannerManager.resume()
        if (::bottomBannerManager.isInitialized) bottomBannerManager.resume()
    }

    override fun onPause() {
        if (::inlineBannerManager.isInitialized) inlineBannerManager.pause()
        if (::bottomBannerManager.isInitialized) bottomBannerManager.pause()
        super.onPause()
    }

    override fun onStop() {
        if (adsReady) SmartAdManager.releaseRewarded()
        super.onStop()
    }

    override fun onDestroy() {
        if (::inlineBannerManager.isInitialized) inlineBannerManager.destroy()
        if (::bottomBannerManager.isInitialized) bottomBannerManager.destroy()
        nativeAdManager?.destroy()
        super.onDestroy()
        // SmartAdManager survives rotation — do NOT destroy() here
    }

    // ── Buttons ───────────────────────────────────────────────────────
    private fun setupButtons() {
        // Interstitial for the "search" screen — frequency cap applied by RC.
        binding.showInterstitial.setOnClickListener {
            if (!adsReady) { toast("⏳ SDK initializing..."); return@setOnClickListener }
            when {
                AdsConfigRepository.interstitialConfig("search") == null ->
                    toast("ℹ️ Interstitial disabled in config")
                // Check readiness BEFORE consuming the frequency counter, so a
                // not-ready tap never burns a frequency slot.
                !SmartAdManager.isInterstitialReady ->
                    toast("⏳ Interstitial not ready yet")
                !AdsConfigRepository.shouldShowInterstitial("search") ->
                    toast("⏳ Frequency cap — skipped this time")
                else ->
                    if (!SmartAdManager.tryShowInterstitial(this))
                        toast("⏳ Interstitial not ready yet")
            }
        }

        // Rewarded for the "search" screen.
        binding.showRewarded.setOnClickListener {
            if (!adsReady) { toast("⏳ SDK initializing..."); return@setOnClickListener }
            if (AdsConfigRepository.rewardedConfig("search") == null) {
                toast("ℹ️ Rewarded disabled in config"); return@setOnClickListener
            }
            SmartAdManager.showRewarded(
                activity = this,
                callback = object : RewardCallback {
                    override fun onRewardEarned(type: String, amount: Int) =
                        toast("🏆 Reward: $amount $type")
                    override fun onDismissed(network: AdNetwork) =
                        setStatus("✅ Rewarded done via $network")
                    override fun onFailedToShow(error: AdError) =
                        toast("❌ Show fail: ${error.message}")
                },
                onNotReady = { toast("⏳ Rewarded loading...") }
            )
        }

        // Native for the "detail" screen (RC waterfall: admob → meta).
        binding.showNative.setOnClickListener {
            if (!adsReady) { toast("⏳ SDK initializing..."); return@setOnClickListener }
            loadNativeFromConfig("detail")
        }
    }

    // ── Init ──────────────────────────────────────────────────────────
    private fun initAds() {
        setStatus("Initializing ads...")
        AdsLib.initWithActivity(
            activity  = this,
            onReady   = { onAdsReady() },
            onBlocked = {
                setStatus("⚠️ Consent denied — ads disabled")
                toast("⚠️ Consent denied")
            }
        )
    }

    private fun onAdsReady() {
        // Init is async (UMP consent + MobileAds). If the user left or rotated
        // away during init, this Activity (and its binding) may be gone — bail
        // before touching any view.
        if (isFinishing || isDestroyed) return
        adsReady = true

        // Defensive: if the RC fetch callback didn't populate the repository
        // (e.g. Firebase absent), load the bundled default JSON so the demo
        // still has per-screen config.
        if (!AdsConfigRepository.isLoaded) {
            AdsConfigRepository.load(this, defaultAdsConfigJson())
        }

        if (!AdsConfigRepository.adsEnabled) {
            setStatus("ℹ️ Ads disabled in Remote Config")
            return
        }

        setStatus("✅ Ads ready (RC-driven) — tap a button!")
        SmartAdManager.prepareRewarded(this)

        loadBannerFromConfig("home")     // inline banner — "home" screen
        loadBottomBannerFromConfig("search")  // bottom banner — "search" screen
    }

    // ── Banner (inline) — RC-driven ───────────────────────────────────
    private fun loadBannerFromConfig(screen: String) {
        val config = AdsConfigRepository.bannerConfig(screen, BannerAdSize.BANNER)
        if (config == null) {
            setStatus("ℹ️ Banner '$screen' disabled in config")
            return
        }
        inlineBannerManager = BannerAdManager(config)
        inlineBannerManager.load(this, object : AdCallback {
            override fun onLoaded(network: AdNetwork) {
                inlineBannerManager.attach(binding.bannerContainer)
                setStatus("✅ Banner '$screen' via $network")
            }
            override fun onFailedToLoad(error: AdError) =
                setStatus("❌ Banner fail: ${error.message}")
        })
    }

    // ── Banner (bottom) — RC-driven ───────────────────────────────────
    private fun loadBottomBannerFromConfig(screen: String) {
        val config = AdsConfigRepository.bannerConfig(screen, BannerAdSize.ADAPTIVE)
        if (config == null) {
            binding.bannerBottom.visibility = View.GONE
            return
        }
        bottomBannerManager = BannerAdManager(config)
        bottomBannerManager.load(this, object : AdCallback {
            override fun onLoaded(network: AdNetwork) {
                binding.bannerBottom.visibility = View.VISIBLE
                bottomBannerManager.attach(binding.bannerBottom)
            }
            override fun onFailedToLoad(error: AdError) {
                binding.bannerBottom.visibility = View.GONE
            }
        })
    }



    // ── Native — RC-driven ────────────────────────────────────────────
    private fun loadNativeFromConfig(screen: String) {
        // Prefer the SmartAdManager prefetch cache first.
        if (SmartAdManager.nativeReadyCount > 0) {
            SmartAdManager.attachNative(binding.nativeAd)
            setStatus("✅ Native from cache")
            return
        }
        val config = AdsConfigRepository.nativeConfig(screen)
        if (config == null) {
            setStatus("ℹ️ Native '$screen' disabled in config")
            return
        }
        setStatus("⏳ Loading native '$screen'...")
        nativeAdManager?.destroy()
        nativeAdManager = NativeAdManager(config).also { mgr ->
            mgr.load(this, object : AdCallback {
                override fun onLoaded(network: AdNetwork) {
                    mgr.attach(binding.nativeAd)
                    setStatus("✅ Native '$screen' via $network")
                }
                override fun onFailedToLoad(error: AdError) =
                    setStatus("❌ Native fail: ${error.message}")
            })
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────
    private fun setStatus(msg: String) { binding.tvStatus.text = msg }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    /** Shared default ads_config — used only as an offline fallback. */
    private fun defaultAdsConfigJson(): String =
        AdsConfigDefaults.ADS_CONFIG
}
