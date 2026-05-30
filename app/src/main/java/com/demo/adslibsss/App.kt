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

    override fun isDebugBuild(): Boolean = BuildConfig.DEBUG

    override fun provideAdsConfig(): SmartAdConfig = SmartAdConfig(
        // Fallback config for SmartAdManager (app-open / preloaders) when the
        // structured RC JSON is absent. Real per-screen IDs come from ads_config.
        interstitialUnitId = "ca-app-pub-3940256099942544/1033173712",
        rewardedUnitId     = "ca-app-pub-3940256099942544/5224354917",
        appOpenUnitId      = "ca-app-pub-3940256099942544/9257395921",
        nativeUnitId       = "ca-app-pub-3940256099942544/2247696110",
        nativeCacheSize        = 3,
        nativeExpiryMinutes    = 60,
        maxRetries             = 6,
    )

    override fun provideRemoteConfigDefaults(): Map<String, Any> = mapOf(
        // Structured per-screen ads config. In production this same key is
        // overridden from Firebase Remote Config; this default lets the demo
        // run offline. See [AdsConfigDefaults] for the JSON.
        "ads_config" to AdsConfigDefaults.ADS_CONFIG,
        // Minimum seconds between two interstitial shows (read by MyLibrary.onAdsReady).
        "interstitial_interval" to 30L,
    )
}
