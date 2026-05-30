package com.demo.adslibsss

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

class MainActivity : AppCompatActivity() {

    // ---- AdMob Official Test IDs ----
    private val BANNER_TEST_ID = "ca-app-pub-3940256099942544/6300978111"
    private val NATIVE_TEST_ID = "ca-app-pub-3940256099942544/2247696110"

    // Interstitial + Rewarded are managed by AdsPreloader (always preloaded)
    private lateinit var bannerManager: BannerAdManager
    private lateinit var nativeAdManager: NativeAdManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        // AdsPreloader handles load/reload automatically
        findViewById<Button>(R.id.showInterstitial).setOnClickListener {
            val shown = AdsPreloader.tryShowInterstitial(this)
            if (!shown) toast("⏳ Interstitial not ready or interval active")
        }

        findViewById<Button>(R.id.showRewarded).setOnClickListener {
            AdsPreloader.showRewarded(
                activity = this,
                callback = object : RewardCallback {
                    override fun onRewardEarned(type: String, amount: Int) =
                        toast("🏆 Reward: $amount $type")
                    override fun onDismissed(network: AdNetwork) =
                        toast("Rewarded dismissed via $network")
                    override fun onFailedToShow(error: AdError) =
                        toast("❌ Show fail: ${error.message}")
                },
                onNotReady = { toast("⏳ Rewarded not ready yet") }
            )
        }

        // Consent + SDK init → AdsPreloader.preload() → loadBanner + loadNative
        AdsInitializer.init(
            activity  = this,
            onReady   = {
                toast("✅ AdsSdk ready!")
                // Preloader already started in AdsInitializer.init()
                loadBanner()
                loadNative()
            },
            onBlocked = { toast("⚠️ Consent na madyo — ads load nahi thay") }
        )
    }

    // ---------------------------------------------------------------
    // Banner — waterfall: AdMob → Meta → Unity
    // ---------------------------------------------------------------
    private fun loadBanner() {
        val config = AdUnitConfig(
            placementKey = "main_banner",
            format       = AdFormat.BANNER,
            bannerSize   = BannerAdSize.ADAPTIVE,
            waterfall    = listOf(
                NetworkAdUnit(AdNetwork.ADMOB, BANNER_TEST_ID),
                // NetworkAdUnit(AdNetwork.META,  "META_BANNER_PLACEMENT"),
                // NetworkAdUnit(AdNetwork.UNITY, "UNITY_BANNER_PLACEMENT"),
            )
        )
        bannerManager = BannerAdManager(config)
        bannerManager.load(this, object : AdCallback {
            override fun onLoaded(network: AdNetwork) {
                bannerManager.attach(findViewById<LinearLayout>(R.id.banner_container))
                toast("📢 Banner loaded via $network")
            }
            override fun onFailedToLoad(error: AdError) =
                toast("❌ Banner fail: ${error.message}")
        })
    }

    // ---------------------------------------------------------------
    // Native — waterfall: AdMob → Meta
    // ---------------------------------------------------------------
    private fun loadNative() {
        val config = AdUnitConfig(
            placementKey = "main_native",
            format       = AdFormat.NATIVE,
            waterfall    = listOf(
                NetworkAdUnit(AdNetwork.ADMOB, NATIVE_TEST_ID),
                // NetworkAdUnit(AdNetwork.META, "META_NATIVE_PLACEMENT"),
            )
        )
        nativeAdManager = NativeAdManager(config)
        nativeAdManager.load(this, object : AdCallback {
            override fun onLoaded(network: AdNetwork) {
                nativeAdManager.attach(findViewById<LinearLayout>(R.id.native_ad))
                toast("🖼 Native loaded via $network")
            }
            override fun onFailedToLoad(error: AdError) =
                toast("❌ Native fail: ${error.message}")
        })
    }

    // ---------------------------------------------------------------
    // Lifecycle
    // ---------------------------------------------------------------
    override fun onPause() {
        if (::bannerManager.isInitialized) bannerManager.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (::bannerManager.isInitialized) bannerManager.resume()
    }

    override fun onDestroy() {
        if (::bannerManager.isInitialized)   bannerManager.destroy()
        if (::nativeAdManager.isInitialized) nativeAdManager.destroy()
        super.onDestroy()
        // Note: AdsPreloader.destroy() only in Application.onTerminate()
        // — preloader should survive Activity recreations (rotation, etc.)
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
