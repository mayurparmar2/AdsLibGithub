package com.demo.adslibsss

import com.ads.adslib.MyLibrary
import com.ads.adslib.smart.SmartAdConfig

/**
 * Application class — extends [MyLibrary] so all initialization is handled
 * by the library. This class only provides app-specific configuration.
 *
 * No initialization logic lives here — everything is in [MyLibrary.onCreate].
 */
class App : MyLibrary() {

    companion object{
        var instance :App? = null
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
    override fun isDebugBuild(): Boolean = BuildConfig.DEBUG

    // OPTIONAL — enable the app-wide SmartAdManager preloaders. Ad unit IDs come
    // from the Remote Config ads_config JSON; we only name the RC screen keys.
    // Uncomment to turn on interstitial/rewarded/app-open/native preloading.
//    override fun provideAdsConfig(): SmartAdConfig = SmartAdConfig(
//        interstitialScreen = "search",
//        rewardedScreen     = "search",
//        nativeScreen       = "detail",
//    )

    override fun provideRemoteConfigDefaults(): Map<String, Any> = mapOf(
        // Structured per-screen ads config. In production this same key is
        // overridden from Firebase Remote Config; this default lets the demo
        // run offline. See [AdsConfigDefaults] for the JSON.
        "ads_config" to AdsConfigDefaults.ADS_CONFIG,
        // Minimum seconds between two interstitial shows (read by MyLibrary.onAdsReady).
        "interstitial_interval" to 30L,
    )
}
