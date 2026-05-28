package com.ads.adslib

import android.content.Context
import com.ads.adslib.config.RemoteConfigManager
import com.ads.adslib.util.AdLog
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration

/**
 * Single entry point for the Ads library — initialize once, then create per-placement managers.
 *
 * Typical splash-screen flow:
 * ```
 * val consent = ConsentManager(this)
 * consent.gatherConsent(this) {
 *     if (consent.canRequestAds) {
 *         AdsSdk.initialize(this) {
 *             // SDKs ready — start loading ads / dismiss splash
 *         }
 *     }
 * }
 * ```
 */
object AdsSdk {

    @Volatile
    private var initialized = false

    /** Optional remote-config handle, exposed for app-side checks. */
    val remoteConfig: RemoteConfigManager by lazy { RemoteConfigManager() }

    /**
     * Initialize underlying ad SDKs. Safe to call multiple times; only the first runs.
     *
     * @param context application context.
     * @param debug enables verbose logging and registers test devices.
     * @param testDeviceIds AdMob test device IDs (debug only).
     * @param onComplete called on the main thread when initialization finishes.
     */
    fun initialize(
        context: Context,
        debug: Boolean = false,
        testDeviceIds: List<String> = emptyList(),
        onComplete: () -> Unit = {}
    ) {
        AdLog.enabled = debug
        if (initialized) {
            onComplete()
            return
        }

        if (debug && testDeviceIds.isNotEmpty()) {
            MobileAds.setRequestConfiguration(
                RequestConfiguration.Builder()
                    .setTestDeviceIds(testDeviceIds)
                    .build()
            )
        }

        MobileAds.initialize(context).apply {
            initialized = true
            AdLog.d("sdk", "MobileAds initialized")
            // Meta/Unity SDK init hooks go here when those modules are added.
            onComplete()
        }
    }

    fun isInitialized(): Boolean = initialized
}
