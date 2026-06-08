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
     * Whether a "privacy options" entry point is required for this user (i.e. the
     * UMP form offers an ongoing way to change consent — typically EU/EEA users).
     * Only meaningful after [gatherConsent] / a consent-info update has completed.
     * Use this to show/hide a "Manage consent" button so it only appears where
     * it's actually required.
     */
    val isPrivacyOptionsRequired: Boolean
        get() = consentInformation.privacyOptionsRequirementStatus ==
            ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

    /**
     * Show the UMP privacy-options form so the user can change/withdraw consent.
     * This is the correct action for a "Manage consent" button (no [reset] needed).
     * [onComplete] runs once the form is dismissed (or immediately on error).
     */
    fun showPrivacyOptionsForm(activity: Activity, onComplete: () -> Unit = {}) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { formError ->
            if (formError != null) AdLog.w("consent", "privacy options error: ${formError.message}")
            onComplete()
        }
    }

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
