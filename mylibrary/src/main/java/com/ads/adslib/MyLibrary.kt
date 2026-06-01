package com.ads.adslib

import android.app.Application
import android.content.Context
import android.util.Log
import com.ads.adslib.config.remote.AdsConfigRepository
import com.ads.adslib.core.model.AdError
import com.ads.adslib.smart.SmartAdConfig
import com.ads.adslib.smart.SmartAdManager
import com.facebook.ads.Ad
import com.facebook.ads.AdSettings
import com.facebook.ads.RewardedInterstitialAd
import com.facebook.ads.RewardedInterstitialAdListener
import com.facebook.ads.RewardedVideoAd
import com.facebook.ads.RewardedVideoAdListener

/**
 * Base Application class for any app that uses AdsLib.
 *
 * Extend this instead of [Application] to get automatic Remote Config
 * initialization on startup. ConsentManager + SDK init still require an
 * Activity — wire those via [AdsLib.initWithActivity] from your first Activity.
 *
 * ## Minimal setup
 * ```kotlin
 * class App : MyLibrary() {
 *
 *     override fun isDebugBuild() = BuildConfig.DEBUG
 *
 *     override fun provideRemoteConfigDefaults() = mapOf(
 *         "ads_enabled"           to true,
 *         "interstitial_interval" to 60L,
 *     )
 *
 *     // OPTIONAL — only override if you want the app-wide SmartAdManager
 *     // preloaders (interstitial / rewarded / app-open / native cache).
 *     // Omit it (return null) to use only the Remote-Config per-screen
 *     // managers (BannerAdManager / NativeAdManager / InterstitialAdManager / …).
 *     // Ad unit IDs come from Remote Config — only name the RC screen keys here.
 *     override fun provideAdsConfig() = SmartAdConfig(
 *         interstitialScreen = "search",
 *         rewardedScreen     = "search",
 *         nativeScreen       = "detail",
 *     )
 * }
 * ```
 *
 * ## AndroidManifest
 * ```xml
 * <application android:name=".App" ...>
 * ```
 */
abstract class MyLibrary : Application() {

    // ── Abstract — consuming app must implement ──────────────────────

    /** Return [BuildConfig.DEBUG] from the app module. */
    abstract fun isDebugBuild(): Boolean

    /**
     * Firebase Remote Config default values.
     * These are used immediately (before any fetch) so ads behave
     * correctly even on first launch with no network.
     */
    abstract fun provideRemoteConfigDefaults(): Map<String, Any>

    // ── Optional — override to enable app-wide preloading ────────────

    /**
     * Ad unit IDs + tuning for the app-wide [SmartAdManager] preloaders
     * (interstitial / rewarded / app-open / native cache).
     *
     * **Optional.** The default returns null, which disables [SmartAdManager]
     * entirely — the app then uses only the Remote-Config-driven per-screen
     * managers ([com.ads.adslib.admob.banner.BannerAdManager],
     * [com.ads.adslib.admob.native_ad.NativeAdManager], etc.). Override it only
     * if you want background preloading.
     */
    open fun provideAdsConfig(): SmartAdConfig? = null

    // ── Lifecycle ────────────────────────────────────────────────────

    override fun onCreate() {
        super.onCreate()
        initRemoteConfig()
    }

    // ── Internal API (called by AdsLib after consent + SDK init) ─────

    /**
     * Called by [AdsLib.initWithActivity] once MobileAds is ready.
     * Starts the app-wide preloaders via [SmartAdManager] — but only if the app
     * opted in by overriding [provideAdsConfig]. Otherwise it's a no-op and the
     * app relies solely on the Remote-Config per-screen managers.
     */
    internal fun onAdsReady() {
        // App-wide preloading is opt-in.
        val smartConfig = provideAdsConfig() ?: run {
            Log.d("MyLibrary", "No SmartAdConfig — app-wide preloading disabled")
            return
        }

        // Remote Config kill-switch: if RC has loaded and ads are disabled,
        // do NOT start any preloader (interstitial / app-open / native / rewarded).
        if (AdsConfigRepository.isLoaded && !AdsConfigRepository.adsEnabled) {
            Log.w("MyLibrary", "ads_enabled=false in Remote Config — preloaders not started")
            return
        }

        val intervalSec = AdsSdk.remoteConfig
            .getLong("interstitial_interval")
            .takeIf { it > 0 } ?: 30L

        SmartAdManager.init(
            application = this,
            config      = smartConfig.copy(interstitialIntervalSec = intervalSec)
        )
    }

    // ── Companion ────────────────────────────────────────────────────

    companion object {
        /**
         * Retrieve the [MyLibrary] instance from any Context.
         * Throws if [Application] does not extend [MyLibrary].
         */
        fun from(context: Context): MyLibrary =
            context.applicationContext as? MyLibrary
                ?: error(
                    "Application class must extend MyLibrary. " +
                    "Check your AndroidManifest android:name attribute."
                )
    }

    // ── Private ──────────────────────────────────────────────────────

    private fun initRemoteConfig() {
        try {
            AdsSdk.remoteConfig.init(
                defaults                = provideRemoteConfigDefaults(),
                minFetchIntervalSeconds = if (isDebugBuild()) 0L else 3600L
            ) {
                // After fetch: parse the structured ads JSON (if provided) into
                // AdsConfigRepository so screen-wise configs become available.
                val json = AdsSdk.remoteConfig.getString(remoteConfigJsonKey())
                if (json.isNotBlank()) {
                    AdsConfigRepository.load(this, json)
                }
            }
        } catch (e: IllegalStateException) {
            // FirebaseApp not initialized — google-services plugin or
            // google-services.json missing. Hardcoded defaults will be used.
            Log.w("MyLibrary", "Remote Config unavailable: ${e.message}")
        }
    }

    /**
     * Remote Config key holding the structured ads JSON (the object with the
     * `ads` section). Override if your key differs. Default: `ads_config`.
     * Provide its default value via [provideRemoteConfigDefaults].
     */
    open fun remoteConfigJsonKey(): String = "ads_config"


    private var rewardedVideoAd: RewardedInterstitialAd? = null

    public fun showRewardedAd() {

        if (rewardedVideoAd?.isAdLoaded == true &&
            !rewardedVideoAd!!.isAdInvalidated
        ) {
            rewardedVideoAd?.show()
        } else {
            Log.d("META_AD", "Ad Not Ready")
        }
    }

    public fun loadRewardedAd() {
        AdSettings.setTestMode(true)
        AdSettings.addTestDevice("B4B4E16E557B7233F5CFFED447976829")

        rewardedVideoAd = RewardedInterstitialAd(
            this,
            "VID_HD_16_9_46S_APP_INSTALL#YOUR_PLACEMENT_ID"
        )

        rewardedVideoAd?.loadAd(
            rewardedVideoAd?.buildLoadAdConfig()
                ?.withAdListener(object : RewardedInterstitialAdListener {
                    override fun onError(p0: Ad?, p1: com.facebook.ads.AdError?) {
                        Log.e("META_AD","AdError: "+p1?.errorMessage  )

                    }

                    override fun onAdLoaded(ad: Ad) {
                        Log.d("META_AD", "Rewarded Loaded")
                    }
                    override fun onLoggingImpression(ad: Ad) {}

                    override fun onAdClicked(ad: Ad) {}


                    override fun onRewardedInterstitialCompleted() {
                        Log.e("META_AD","onRewardedInterstitialCompleted: " )

                    }

                    override fun onRewardedInterstitialClosed() {
                        Log.e("META_AD","onRewardedInterstitialClosed: "  )

                    }
                })
                ?.build()
        )
    }
}
