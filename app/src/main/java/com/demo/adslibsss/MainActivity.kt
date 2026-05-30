package com.demo.adslibsss

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ads.adslib.AdsLib
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

    // Banner uses its own manager (inline view — not a preloader concern)
    private lateinit var bannerManager: BannerAdManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        // Buttons — SmartAdManager handles load/reload/retry automatically
        findViewById<Button>(R.id.showInterstitial).setOnClickListener {
            if (!SmartAdManager.tryShowInterstitial(this))
                toast("⏳ Interstitial not ready or interval active")
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

        // Step 1: Consent → Step 2: SDK init (in library via AdsLib)
        // Step 3: SmartAdManager.init() (called inside MyLibrary.onAdsReady)
        AdsLib.initWithActivity(
            activity  = this,
            onReady   = {
                toast("✅ Ads ready!")
                loadBanner()
                SmartAdManager.attachNative(
                    container = findViewById(R.id.native_ad),
                    onEmpty   = { /* cache still loading — ignored */ }
                )
            },
            onBlocked = { toast("⚠️ Consent denied — ads disabled") }
        )
    }

    // Rewarded: prepare when this screen is active, release when leaving
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

    // ── Banner ───────────────────────────────────────────────────────
    private fun loadBanner() {
        val config = AdUnitConfig(
            placementKey = "main_banner",
            format       = AdFormat.BANNER,
            bannerSize   = BannerAdSize.ADAPTIVE,
            waterfall    = listOf(
                NetworkAdUnit(AdNetwork.ADMOB, BANNER_TEST_ID),
                // NetworkAdUnit(AdNetwork.META,  "META_BANNER"),
                // NetworkAdUnit(AdNetwork.UNITY, "UNITY_BANNER"),
            )
        )
        bannerManager = BannerAdManager(config)
        bannerManager.load(this, object : AdCallback {
            override fun onLoaded(network: AdNetwork) {
                bannerManager.attach(findViewById<LinearLayout>(R.id.banner_container))
                toast("📢 Banner via $network")
            }
            override fun onFailedToLoad(error: AdError) =
                toast("❌ Banner fail: ${error.message}")
        })
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
