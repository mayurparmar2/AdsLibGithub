package com.demo.adslibsss

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ads.adslib.admob.banner.BannerAdManager
import com.ads.adslib.core.callback.AdCallback
import com.ads.adslib.core.callback.RewardCallback
import com.ads.adslib.core.model.AdError
import com.ads.adslib.core.model.AdFormat
import com.ads.adslib.core.model.AdNetwork
import com.ads.adslib.core.model.AdUnitConfig
import com.ads.adslib.core.model.BannerAdSize
import com.ads.adslib.core.model.NetworkAdUnit
import com.ads.adslib.smart.SmartAdManager

class MainActivity : AppCompatActivity() {

    private val BANNER_TEST_ID = "ca-app-pub-3940256099942544/6300978111"

    // Banner still uses direct manager (inline view — not a preloader concern)
    private lateinit var bannerManager: BannerAdManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        // SmartAdManager handles load/reload automatically
        findViewById<Button>(R.id.showInterstitial).setOnClickListener {
            val shown = SmartAdManager.tryShowInterstitial(this)
            if (!shown) toast("⏳ Interstitial not ready or interval active")
        }

        findViewById<Button>(R.id.showRewarded).setOnClickListener {
            SmartAdManager.showRewarded(
                activity  = this,
                callback  = object : RewardCallback {
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

        // Consent → SDK init → SmartAdManager.init() → Banner + Native load
        AdsInitializer.init(
            activity  = this,
            onReady   = {
                toast("✅ AdsSdk ready!")
                loadBanner()
                // Native ads preloaded by SmartAdManager — attach when needed
                SmartAdManager.attachNative(
                    container = findViewById(R.id.native_ad),
                    onEmpty   = { /* optional: show placeholder */ }
                )
            },
            onBlocked = { toast("⚠️ Consent na madyo — ads load nahi thay") }
        )
    }

    // Rewarded: prepare on this screen, release when leaving
    override fun onResume() {
        super.onResume()
        if (::bannerManager.isInitialized) bannerManager.resume()
        SmartAdManager.prepareRewarded(this)
    }

    override fun onPause() {
        if (::bannerManager.isInitialized) bannerManager.pause()
        SmartAdManager.releaseRewarded()
        super.onPause()
    }

    override fun onDestroy() {
        if (::bannerManager.isInitialized) bannerManager.destroy()
        super.onDestroy()
        // SmartAdManager survives rotation — do NOT call destroy() here
    }

    // ---------------------------------------------------------------
    // Banner — direct manager (inline view, not a fullscreen preloader)
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

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
