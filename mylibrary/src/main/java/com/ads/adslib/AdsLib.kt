package com.ads.adslib

import android.app.Activity
import com.ads.adslib.consent.ConsentManager

/**
 * Activity-level entry point for the second half of initialization.
 *
 * [MyLibrary.onCreate] handles Application-level setup (Remote Config).
 * [initWithActivity] handles what requires an Activity context:
 *  - Gathering GDPR/CCPA consent (UMP shows a form in the Activity)
 *  - Initializing MobileAds (must happen after consent)
 *  - Starting [com.ads.adslib.smart.SmartAdManager] preloaders
 *
 * Call this once from your first Activity (SplashActivity or MainActivity):
 * ```kotlin
 * AdsLib.initWithActivity(
 *     activity  = this,
 *     onReady   = { loadYourAds() },
 *     onBlocked = { /* consent denied — no ads */ }
 * )
 * ```
 */
object AdsLib {

    /** True once MobileAds.initialize() has completed successfully. */
    val isReady: Boolean get() = AdsSdk.isInitialized()

    /**
     * Gather consent then initialize all ad SDKs.
     * Safe to call multiple times — no-ops if already initialized.
     *
     * @param activity  Hosts the UMP consent form if one must be shown.
     * @param onReady   Called on the main thread when everything is ready.
     * @param onBlocked Called when the user denied consent — do not load ads.
     */
    fun initWithActivity(
        activity: Activity,
        onReady: () -> Unit,
        onBlocked: () -> Unit = {}
    ) {
        if (isReady) {
            onReady()
            return
        }

        val myLibrary = MyLibrary.from(activity)

        val consent = ConsentManager(activity)
        consent.gatherConsent(activity = activity, testDeviceId = null) {
            if (consent.canRequestAds) {
                AdsSdk.initialize(
                    context       = activity.applicationContext,
                    debug         = myLibrary.isDebugBuild(),
                    testDeviceIds = if (myLibrary.isDebugBuild()) listOf("EMULATOR") else emptyList(),
                ) {
                    myLibrary.onAdsReady()   // starts SmartAdManager preloaders
                    onReady()
                }
            } else {
                onBlocked()
            }
        }
    }
}
