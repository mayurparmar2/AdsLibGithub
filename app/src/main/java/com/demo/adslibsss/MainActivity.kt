package com.demo.adslibsss

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ads.adslib.AdsSdk
import com.ads.adslib.admob.interstitial.InterstitialAdManager
import com.ads.adslib.consent.ConsentManager
import com.ads.adslib.core.callback.AdCallback
import com.ads.adslib.core.model.AdError
import com.ads.adslib.core.model.AdFormat
import com.ads.adslib.core.model.AdNetwork
import com.ads.adslib.core.model.AdUnitConfig
import com.ads.adslib.core.model.NetworkAdUnit
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

class MainActivity : AppCompatActivity() {

    // ---- AdMob Official Test IDs (real device pe test karo) ----
    private val INTERSTITIAL_TEST_ID = "ca-app-pub-3940256099942544/1033173712"
    private val BANNER_TEST_ID       = "ca-app-pub-3940256099942544/6300978111"

    private lateinit var interstitialManager: InterstitialAdManager
    private var bannerAdView: AdView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        // Button click — ad ready hoy to show karo
        findViewById<Button>(R.id.showInterstitial).setOnClickListener {
            if (::interstitialManager.isInitialized && interstitialManager.isReady()) {
                interstitialManager.show(this)
            } else {
                toast("⏳ Ad loading... thodi var raho")
            }
        }

        initAds()
    }

    // ---------------------------------------------------------------
    // Step 1: Consent → Step 2: SDK Init → Step 3: Load ads
    // ---------------------------------------------------------------
    private fun initAds() {
        val consent = ConsentManager(this)
        consent.gatherConsent(
            activity      = this,
            testDeviceId  = null   // EEA debug test mate: device hash ID aapjo
        ) {
            if (consent.canRequestAds) {
                AdsSdk.initialize(
                    context       = this,
                    debug         = true,
                    testDeviceIds = listOf("EMULATOR")   // real device hash ID yahan muko
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
    // Interstitial — library na InterstitialAdManager thi
    // ---------------------------------------------------------------
    private fun loadInterstitial() {
        val config = AdUnitConfig(
            placementKey = "main_interstitial",
            format       = AdFormat.INTERSTITIAL,
            waterfall    = listOf(
                NetworkAdUnit(AdNetwork.ADMOB, INTERSTITIAL_TEST_ID)
                // NetworkAdUnit(AdNetwork.META,  "META_PLACEMENT_ID"),   // future
                // NetworkAdUnit(AdNetwork.UNITY, "UNITY_PLACEMENT_ID"),  // future
            )
        )

        interstitialManager = InterstitialAdManager(config)
        interstitialManager.load(this, object : AdCallback {

            override fun onLoaded(network: AdNetwork) {
                toast("🎉 Interstitial loaded via $network — Button dabaavo!")
            }

            override fun onFailedToLoad(error: AdError) {
                toast("❌ Interstitial fail: ${error.message}")
            }

            override fun onShown(network: AdNetwork) {
                toast("👁 Ad shown via $network")
            }

            override fun onDismissed(network: AdNetwork) {
                // Ad band thayu — reload karo
                interstitialManager.destroy()
                loadInterstitial()
            }

            override fun onFailedToShow(error: AdError) {
                toast("❌ Show fail: ${error.message}")
            }
        })
    }

    // ---------------------------------------------------------------
    // Banner — AdMob AdView seedha (BannerAdManager haju banyun nathi)
    // ---------------------------------------------------------------
    private fun loadBanner() {
        bannerAdView = AdView(this).apply {
            setAdSize(AdSize.BANNER)
            adUnitId = BANNER_TEST_ID
            loadAd(AdRequest.Builder().build())
        }
        findViewById<LinearLayout>(R.id.banner_container).apply {
            removeAllViews()
            addView(bannerAdView)
        }
    }

    // ---------------------------------------------------------------
    // Lifecycle
    // ---------------------------------------------------------------
    override fun onPause() {
        bannerAdView?.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        bannerAdView?.resume()
    }

    override fun onDestroy() {
        bannerAdView?.destroy()
        if (::interstitialManager.isInitialized) {
            interstitialManager.destroy()
        }
        super.onDestroy()
    }

    // ---------------------------------------------------------------
    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
