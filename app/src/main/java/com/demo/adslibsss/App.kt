package com.demo.adslibsss

import android.app.Application
import android.util.Log
import com.ads.adslib.AdsSdk

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initRemoteConfig()
    }

    private fun initRemoteConfig() {
        // FirebaseApp is auto-initialized by FirebaseInitProvider (ContentProvider)
        // before Application.onCreate() when google-services plugin is applied.
        // The try-catch is a safety net for misconfigured builds.
        try {
            AdsSdk.remoteConfig.init(
                defaults = mapOf(
                    "ads_enabled"           to true,
                    "interstitial_enabled"  to true,
                    "rewarded_enabled"      to true,
                    "banner_enabled"        to true,
                    "native_enabled"        to true,
                    "interstitial_interval" to 60L,   // seconds between interstitials
                ),
                minFetchIntervalSeconds = if (BuildConfig.DEBUG) 0L else 3600L
            )
        } catch (e: IllegalStateException) {
            // FirebaseApp not yet initialized — google-services.json missing or
            // google-services plugin not applied. Hardcoded defaults will be used.
            Log.w("AdsLib", "Remote Config unavailable: ${e.message}")
        }
    }
}
