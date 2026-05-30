package com.demo.adslibsss

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ads.adslib.AdsLib
import com.ads.adslib.admob.banner.BannerAdManager
import com.ads.adslib.admob.native_ad.NativeAdManager
import com.ads.adslib.core.callback.AdCallback
import com.ads.adslib.core.callback.RewardCallback
import com.ads.adslib.core.model.AdError
import com.ads.adslib.core.model.AdFormat
import com.ads.adslib.core.model.AdNetwork
import com.ads.adslib.core.model.AdUnitConfig
import com.ads.adslib.core.model.BannerAdSize
import com.ads.adslib.core.model.NetworkAdUnit
import com.ads.adslib.smart.SmartAdManager
import com.demo.adslibsss.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {

    // ── View Binding ──────────────────────────────────────────────────
    private lateinit var binding: MainActivityBinding

    // ── AdMob official test IDs ───────────────────────────────────────
    private val BANNER_INLINE_ID = "ca-app-pub-3940256099942544/6300978111"
    private val BANNER_BOTTOM_ID = "ca-app-pub-3940256099942544/6300978111"
    private val NATIVE_TEST_ID   = "ca-app-pub-3940256099942544/2247696110"

    // ── Managers for view-based formats ──────────────────────────────
    private lateinit var inlineBannerManager: BannerAdManager
    private lateinit var bottomBannerManager: BannerAdManager
    private lateinit var nativeAdManager: NativeAdManager

    private var adsReady = false

    // ── Lifecycle ─────────────────────────────────────────────────────
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtons()
        initAds()
    }

    override fun onResume() {
        super.onResume()
        if (::inlineBannerManager.isInitialized) inlineBannerManager.resume()
        if (::bottomBannerManager.isInitialized) bottomBannerManager.resume()
        // FIX: guard — SmartAdManager might not be ready on first launch
        if (adsReady) SmartAdManager.prepareRewarded(this)
    }

    override fun onPause() {
        if (::inlineBannerManager.isInitialized) inlineBannerManager.pause()
        if (::bottomBannerManager.isInitialized) bottomBannerManager.pause()
        if (adsReady) SmartAdManager.releaseRewarded()
        super.onPause()
    }

    override fun onDestroy() {
        if (::inlineBannerManager.isInitialized) inlineBannerManager.destroy()
        if (::bottomBannerManager.isInitialized) bottomBannerManager.destroy()
        if (::nativeAdManager.isInitialized)     nativeAdManager.destroy()
        super.onDestroy()
        // SmartAdManager survives rotation — do NOT destroy() here
    }

    // ── Button setup ──────────────────────────────────────────────────
    private fun setupButtons() {
        binding.showInterstitial.setOnClickListener {
            if (!adsReady) { toast("⏳ SDK initializing..."); return@setOnClickListener }
            if (!SmartAdManager.tryShowInterstitial(this))
                toast("⏳ Not ready / wait 30s between shows")
        }

        binding.showRewarded.setOnClickListener {
            if (!adsReady) { toast("⏳ SDK initializing..."); return@setOnClickListener }
            SmartAdManager.showRewarded(
                activity  = this,
                callback  = object : RewardCallback {
                    override fun onRewardEarned(type: String, amount: Int) =
                        toast("🏆 Reward: $amount $type")
                    override fun onShown(network: AdNetwork) =
                        setStatus("Rewarded showing via $network")
                    override fun onDismissed(network: AdNetwork) =
                        setStatus("✅ Rewarded done — reloading next ad...")
                    override fun onFailedToShow(error: AdError) =
                        toast("❌ Show fail: ${error.message}")
                },
                onNotReady = { toast("⏳ Rewarded loading...") }
            )
        }

        binding.showNative.setOnClickListener {
            if (!adsReady) { toast("⏳ SDK initializing..."); return@setOnClickListener }
            loadAndShowNative()
        }
    }

    // ── Ads initialization ────────────────────────────────────────────
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
        adsReady = true
        setStatus("✅ Ads ready — tap a button to test!")

        // FIX: prepareRewarded AFTER SmartAdManager.init() completes
        SmartAdManager.prepareRewarded(this)

        loadInlineBanner()
        loadBottomBanner()
    }

    // ── Inline banner (inside scroll area) ───────────────────────────
    private fun loadInlineBanner() {
        val config = AdUnitConfig(
            placementKey = "inline_banner",
            format       = AdFormat.BANNER,
            bannerSize   = BannerAdSize.BANNER,
            waterfall    = listOf(NetworkAdUnit(AdNetwork.ADMOB, BANNER_INLINE_ID))
        )
        inlineBannerManager = BannerAdManager(config)
        inlineBannerManager.load(this, object : AdCallback {
            override fun onLoaded(network: AdNetwork) {
                inlineBannerManager.attach(binding.bannerContainer)
                setStatus("✅ Banner loaded via $network")
            }
            override fun onFailedToLoad(error: AdError) =
                setStatus("❌ Banner fail: ${error.message}")
        })
    }

    // ── Bottom adaptive banner ────────────────────────────────────────
    private fun loadBottomBanner() {
        val config = AdUnitConfig(
            placementKey = "bottom_banner",
            format       = AdFormat.BANNER,
            bannerSize   = BannerAdSize.ADAPTIVE,
            waterfall    = listOf(NetworkAdUnit(AdNetwork.ADMOB, BANNER_BOTTOM_ID))
        )
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

    // ── Native ad ────────────────────────────────────────────────────
    private fun loadAndShowNative() {
        // Try cache first (SmartAdManager pre-loaded)
        if (SmartAdManager.nativeReadyCount > 0) {
            SmartAdManager.attachNative(binding.nativeAd)
            setStatus("✅ Native ad from cache")
            return
        }

        // Cache empty — direct load
        setStatus("⏳ Loading native ad...")
        val config = AdUnitConfig(
            placementKey = "test_native",
            format       = AdFormat.NATIVE,
            waterfall    = listOf(NetworkAdUnit(AdNetwork.ADMOB, NATIVE_TEST_ID))
        )
        nativeAdManager = NativeAdManager(config)
        nativeAdManager.load(this, object : AdCallback {
            override fun onLoaded(network: AdNetwork) {
                nativeAdManager.attach(binding.nativeAd)
                setStatus("✅ Native ad loaded via $network")
            }
            override fun onFailedToLoad(error: AdError) =
                setStatus("❌ Native fail: ${error.message}")
        })
    }

    // ── Helpers ───────────────────────────────────────────────────────
    private fun setStatus(msg: String) {
        binding.tvStatus.text = msg
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
