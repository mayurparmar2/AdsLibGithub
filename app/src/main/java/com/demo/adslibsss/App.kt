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
        // ── Replace with your real ad unit IDs in production ──
        interstitialUnitId = "ca-app-pub-3940256099942544/1033173712",
        rewardedUnitId     = "ca-app-pub-3940256099942544/5224354917",
        appOpenUnitId      = "ca-app-pub-3940256099942544/9257395921",
        nativeUnitId       = "ca-app-pub-3940256099942544/2247696110",
        bannerUnitId       = "ca-app-pub-3940256099942544/6300978111",
        // ── Tuning ──
        nativeCacheSize        = 3,
        nativeExpiryMinutes    = 60,
        maxRetries             = 6,
    )

    override fun provideRemoteConfigDefaults(): Map<String, Any> = mapOf(
        "ads_enabled"           to true,
        "interstitial_enabled"  to true,
        "rewarded_enabled"      to true,
        "banner_enabled"        to true,
        "native_enabled"        to true,
        "interstitial_interval" to 60L,
    )
}
