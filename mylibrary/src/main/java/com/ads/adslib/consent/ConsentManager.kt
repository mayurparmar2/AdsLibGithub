package com.ads.adslib.consent

import android.app.Activity
import android.os.Handler
import android.os.Looper
import com.ads.adslib.util.AdLog
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Thin wrapper over Google's User Messaging Platform (UMP) SDK for GDPR/consent.
 *
 * Call [gatherConsent] early (e.g. in the splash screen) before loading ads.
 * Ads should only be requested once [canRequestAds] returns true.
 */
class ConsentManager(activity: Activity) {

    private val consentInformation: ConsentInformation =
        UserMessagingPlatform.getConsentInformation(activity)

    /** Whether ads may be requested under current consent state. */
    val canRequestAds: Boolean get() = consentInformation.canRequestAds()

    /**
     * Request consent info and show the consent form if required.
     *
     * @param activity current activity (form host).
     * @param testDeviceId optional hashed device id to force EEA geography in debug.
     * @param timeoutMs safety net — if the UMP SDK never calls back (network hang),
     *   [onComplete] is still invoked after this many ms so the app is never stuck.
     * @param onComplete invoked exactly once when the flow finishes (success, handled
     *   error, or timeout); inspect [canRequestAds] afterwards.
     */
    fun gatherConsent(
        activity: Activity,
        testDeviceId: String? = null,
        timeoutMs: Long = 8_000L,
        onComplete: () -> Unit
    ) {
        // Ensure onComplete runs exactly once (UMP success/error OR our timeout).
        val done = AtomicBoolean(false)
        val handler = Handler(Looper.getMainLooper())
        val finish = {
            if (done.compareAndSet(false, true)) {
                handler.removeCallbacksAndMessages(null)
                onComplete()
            }
        }
        handler.postDelayed({
            AdLog.w("consent", "UMP timed out after ${timeoutMs}ms — proceeding")
            finish()
        }, timeoutMs)

        val paramsBuilder = ConsentRequestParameters.Builder()
        if (testDeviceId != null) {
            val debugSettings = com.google.android.ump.ConsentDebugSettings.Builder(activity)
                .setDebugGeography(com.google.android.ump.ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                .addTestDeviceHashedId(testDeviceId)
                .build()
            paramsBuilder.setConsentDebugSettings(debugSettings)
        }

        consentInformation.requestConsentInfoUpdate(
            activity,
            paramsBuilder.build(),
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
                    if (formError != null) {
                        AdLog.w("consent", "form error: ${formError.message}")
                    }
                    finish()
                }
            },
            { requestError ->
                AdLog.w("consent", "info update failed: ${requestError.message}")
                finish()
            }
        )
    }

    /** Resets consent state. Useful for a "privacy options" button. */
    fun reset() = consentInformation.reset()
}
