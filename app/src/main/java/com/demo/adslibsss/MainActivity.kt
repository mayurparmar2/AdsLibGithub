package com.demo.adslibsss

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ads.adslib.AdsSdk
import com.ads.adslib.admob.banner.BannerAdManager
import com.ads.adslib.admob.interstitial.InterstitialAdManager
import com.ads.adslib.consent.ConsentManager
import com.ads.adslib.core.callback.AdCallback
import com.ads.adslib.core.model.AdError
import com.ads.adslib.core.model.AdFormat
import com.ads.adslib.core.model.AdNetwork
import com.ads.adslib.core.model.AdUnitConfig
import com.ads.adslib.core.model.BannerAdSize
import com.ads.adslib.core.model.NetworkAdUnit

class MainActivity : AppCompatActivity() {

    // ---- AdMob Official Test IDs ----
    private val INTERSTITIAL_TEST_ID = "ca-app-pub-3940256099942544/1033173712"
    private val BANNER_TEST_ID       = "ca-app-pub-3940256099942544/6300978111"

    private lateinit var interstitialManager: InterstitialAdManager
    private lateinit var bannerManager: BannerAdManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        findViewById<Button>(R.id.showInterstitial).setOnClickListener {
            if (::interstitialManager.isInitialized && interstitialManager.isReady()) {
                interstitialManager.show(this)
            } else {
                toast("⏳ Ad loading... thodi var raho")
            }
        }

        initAds()
    }

    // Consent → SDK init → load ads
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
                toast("🎉 Interstitial loaded via $network — Button dabaavo!")
            override fun onFailedToLoad(error: AdError) =
                toast("❌ Interstitial fail: ${error.message}")
            override fun onShown(network: AdNetwork) =
                toast("👁 Ad shown via $network")
            override fun onDismissed(network: AdNetwork) {
                interstitialManager.destroy()
                loadInterstitial()          // reload after dismiss
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
                val container = findViewById<LinearLayout>(R.id.banner_container)
                bannerManager.attach(container)
                toast("📢 Banner loaded via $network")
            }
            override fun onFailedToLoad(error: AdError) =
                toast("❌ Banner fail: ${error.message}")
        })
    }

    // ---------------------------------------------------------------
    // Lifecycle — banner pause/resume/destroy
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
        if (::bannerManager.isInitialized) bannerManager.destroy()
        if (::interstitialManager.isInitialized) interstitialManager.destroy()
        super.onDestroy()
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
