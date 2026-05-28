package com.ads.adslib.consent

import android.app.Activity
import com.ads.adslib.util.AdLog
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

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
     * @param onComplete invoked when the flow finishes (success or handled error);
     *                   inspect [canRequestAds] afterwards.
     */
    fun gatherConsent(
        activity: Activity,
        testDeviceId: String? = null,
        onComplete: () -> Unit
    ) {
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
                    onComplete()
                }
            },
            { requestError ->
                AdLog.w("consent", "info update failed: ${requestError.message}")
                onComplete()
            }
        )
    }

    /** Resets consent state. Useful for a "privacy options" button. */
    fun reset() = consentInformation.reset()
}
