package com.demo.adslibsss

import android.app.Activity
import com.ads.adslib.AdsSdk
import com.ads.adslib.consent.ConsentManager

/**
 * Single entry point for consent + SDK initialisation.
 *
 * Why not in Application?
 *   ConsentManager needs an Activity so UMP can show the GDPR form.
 *   AdsSdk.initialize() must run AFTER consent is confirmed.
 *   Both steps must happen in Activity context — so they live here, not in App.
 *
 * Usage (call once, e.g. from SplashActivity or MainActivity.onCreate):
 *   AdsInitializer.init(this) { loadYourAds() }
 */
object AdsInitializer {

    /** True after consent gathered + MobileAds.initialize() completes. */
    val isReady: Boolean get() = AdsSdk.isInitialized()

    /**
     * Gather consent (shows GDPR form if needed), then initialize all ad SDKs.
     * Safe to call multiple times — runs only once if already initialized.
     *
     * @param activity  Activity hosting the consent form.
     * @param onReady   Called on main thread when ads are ready to load.
     * @param onBlocked Called when user denies consent — ads must not load.
     */
    fun init(
        activity: Activity,
        onReady: () -> Unit,
        onBlocked: () -> Unit = {}
    ) {
        // Already initialised — skip straight to ready
        if (isReady) {
            onReady()
            return
        }

        val consent = ConsentManager(activity)
        consent.gatherConsent(activity = activity, testDeviceId = null) {
            if (consent.canRequestAds) {
                AdsSdk.initialize(
                    context       = activity.applicationContext,
                    debug         = BuildConfig.DEBUG,
                    testDeviceIds = if (BuildConfig.DEBUG) listOf("EMULATOR") else emptyList(),
                ) {
                    // Start preloading interstitial + rewarded immediately
                    val intervalSec = AdsSdk.remoteConfig.getLong("interstitial_interval")
                        .takeIf { it > 0 } ?: 30L
                    AdsPreloader.preload(activity.applicationContext, intervalSec)
                    onReady()
                }
            } else {
                onBlocked()
            }
        }
    }
}
