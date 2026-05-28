package com.demo.adslibsss

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ads.adslib.AdsSdk
import com.ads.adslib.admob.banner.BannerAdManager
import com.ads.adslib.admob.interstitial.InterstitialAdManager
import com.ads.adslib.admob.rewarded.RewardedAdManager
import com.ads.adslib.consent.ConsentManager
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
    private val INTERSTITIAL_TEST_ID = "ca-app-pub-3940256099942544/1033173712"
    private val REWARDED_TEST_ID     = "ca-app-pub-3940256099942544/5224354917"
    private val BANNER_TEST_ID       = "ca-app-pub-3940256099942544/6300978111"

    private lateinit var interstitialManager: InterstitialAdManager
    private lateinit var rewardedManager: RewardedAdManager
    private lateinit var bannerManager: BannerAdManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        findViewById<Button>(R.id.showInterstitial).setOnClickListener {
            if (::interstitialManager.isInitialized && interstitialManager.isReady()) {
                interstitialManager.show(this)
            } else {
                toast("⏳ Interstitial loading...")
            }
        }

        findViewById<Button>(R.id.showRewarded).setOnClickListener {
            if (::rewardedManager.isInitialized && rewardedManager.isReady()) {
                rewardedManager.show(this)
            } else {
                toast("⏳ Rewarded loading...")
            }
        }

        initAds()
    }

    // Consent → SDK init → load all ads
    private fun initAds() {
        val consent = ConsentManager(this)
        consent.gatherConsent(activity = this, testDeviceId = null) {
            if (consent.canRequestAds) {
                AdsSdk.initialize(
                    context       = this,
                    debug         = true,
                    testDeviceIds = listOf("EMULATOR")
                ) {
                    toast("✅ AdsSdk ready!")
                    loadInterstitial()
                    loadRewarded()
                    loadBanner()
                }
            } else {
                toast("⚠️ Consent na madyo — ads load nahi thay")
            }
        }
    }

    // ---------------------------------------------------------------
    // Interstitial — waterfall: AdMob → Meta → Unity (future)
    // ---------------------------------------------------------------
    private fun loadInterstitial() {
        val config = AdUnitConfig(
            placementKey = "main_interstitial",
            format       = AdFormat.INTERSTITIAL,
            waterfall    = listOf(
                NetworkAdUnit(AdNetwork.ADMOB, INTERSTITIAL_TEST_ID)
                // NetworkAdUnit(AdNetwork.META,  "META_PLACEMENT_ID"),
                // NetworkAdUnit(AdNetwork.UNITY, "UNITY_PLACEMENT_ID"),
            )
        )
        interstitialManager = InterstitialAdManager(config)
        interstitialManager.load(this, object : AdCallback {
            override fun onLoaded(network: AdNetwork) =
                toast("🎉 Interstitial loaded via $network")
            override fun onFailedToLoad(error: AdError) =
                toast("❌ Interstitial fail: ${error.message}")
            override fun onDismissed(network: AdNetwork) {
                interstitialManager.destroy()
                loadInterstitial()
            }
            override fun onFailedToShow(error: AdError) =
                toast("❌ Show fail: ${error.message}")
        })
    }

    // ---------------------------------------------------------------
    // Rewarded — waterfall: AdMob → Meta → Unity (future)
    // ---------------------------------------------------------------
    private fun loadRewarded() {
        val config = AdUnitConfig(
            placementKey = "main_rewarded",
            format       = AdFormat.REWARDED,
            waterfall    = listOf(
                NetworkAdUnit(AdNetwork.ADMOB, REWARDED_TEST_ID)
                // NetworkAdUnit(AdNetwork.META,  "META_REWARDED_PLACEMENT"),
                // NetworkAdUnit(AdNetwork.UNITY, "UNITY_REWARDED_PLACEMENT"),
            )
        )
        rewardedManager = RewardedAdManager(config)
        rewardedManager.load(this, object : RewardCallback {
            override fun onLoaded(network: AdNetwork) =
                toast("🎁 Rewarded loaded via $network")
            override fun onFailedToLoad(error: AdError) =
                toast("❌ Rewarded fail: ${error.message}")
            override fun onRewardEarned(type: String, amount: Int) =
                toast("🏆 Reward earned: $amount $type")   // yahan game currency aapjo
            override fun onDismissed(network: AdNetwork) {
                rewardedManager.destroy()
                loadRewarded()
            }
            override fun onFailedToShow(error: AdError) =
                toast("❌ Show fail: ${error.message}")
        })
    }

    // ---------------------------------------------------------------
    // Banner — waterfall: AdMob → Meta → Unity (future)
    // ---------------------------------------------------------------
    private fun loadBanner() {
        val config = AdUnitConfig(
            placementKey = "main_banner",
            format       = AdFormat.BANNER,
            bannerSize   = BannerAdSize.ADAPTIVE,
            waterfall    = listOf(
                NetworkAdUnit(AdNetwork.ADMOB, BANNER_TEST_ID)
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
        if (::bannerManager.isInitialized)      bannerManager.destroy()
        if (::interstitialManager.isInitialized) interstitialManager.destroy()
        if (::rewardedManager.isInitialized)     rewardedManager.destroy()
        super.onDestroy()
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
