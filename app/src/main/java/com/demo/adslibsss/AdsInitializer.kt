package com.demo.adslibsss

import android.app.Activity
import android.app.Application
import com.ads.adslib.AdsSdk
import com.ads.adslib.consent.ConsentManager
import com.ads.adslib.smart.SmartAdConfig
import com.ads.adslib.smart.SmartAdManager

/**
 * Single entry point for consent + SDK + SmartAdManager initialisation.
 *
 * Why not in Application?
 *   ConsentManager needs an Activity so UMP can show the GDPR form.
 *   AdsSdk.initialize() must run AFTER consent is confirmed.
 *
 * Usage (call once from SplashActivity or MainActivity.onCreate):
 *   AdsInitializer.init(this, onReady = { ... }, onBlocked = { ... })
 */
object AdsInitializer {

    val isReady: Boolean get() = AdsSdk.isInitialized()

    fun init(
        activity: Activity,
        onReady: () -> Unit,
        onBlocked: () -> Unit = {}
    ) {
        if (isReady) { onReady(); return }

        val consent = ConsentManager(activity)
        consent.gatherConsent(activity = activity, testDeviceId = null) {
            if (consent.canRequestAds) {
                AdsSdk.initialize(
                    context       = activity.applicationContext,
                    debug         = BuildConfig.DEBUG,
                    testDeviceIds = if (BuildConfig.DEBUG) listOf("EMULATOR") else emptyList(),
                ) {
                    initSmartAdManager(activity.applicationContext as Application)
                    onReady()
                }
            } else {
                onBlocked()
            }
        }
    }

    private fun initSmartAdManager(application: Application) {
        val intervalSec = AdsSdk.remoteConfig.getLong("interstitial_interval")
            .takeIf { it > 0 } ?: 30L

        SmartAdManager.init(
            application = application,
            config      = SmartAdConfig(
                // ── Replace with your real ad unit IDs ──
                interstitialUnitId = "ca-app-pub-3940256099942544/1033173712",
                rewardedUnitId     = "ca-app-pub-3940256099942544/5224354917",
                appOpenUnitId      = "ca-app-pub-3940256099942544/9257395921",
                nativeUnitId       = "ca-app-pub-3940256099942544/2247696110",
                bannerUnitId       = "ca-app-pub-3940256099942544/6300978111",
                // ── Behaviour ──
                interstitialIntervalSec = intervalSec,
                nativeCacheSize         = 3,
                nativeExpiryMinutes     = 60,
            )
        )
    }
}
