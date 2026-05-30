package com.demo.adslibsss

import android.app.Application
import com.ads.adslib.AdsSdk

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initRemoteConfig()
    }

    private fun initRemoteConfig() {
        AdsSdk.remoteConfig.init(
            defaults = mapOf(
                "ads_enabled"           to true,
                "interstitial_enabled"  to true,
                "rewarded_enabled"      to true,
                "banner_enabled"        to true,
                "native_enabled"        to true,
                "interstitial_interval" to 60L,   // seconds between interstitials
            ),
            // debug build ma 0 rakhjo — production ma 3600 (1 hour)
            minFetchIntervalSeconds = if (BuildConfig.DEBUG) 0L else 3600L
        )
    }
}
