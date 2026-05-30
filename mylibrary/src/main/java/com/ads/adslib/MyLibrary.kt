package com.ads.adslib

import android.app.Application
import android.content.Context
import android.util.Log
import com.ads.adslib.config.remote.AdsConfigRepository
import com.ads.adslib.smart.SmartAdConfig
import com.ads.adslib.smart.SmartAdManager

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
 *     override fun provideAdsConfig() = SmartAdConfig(
 *         interstitialUnitId = "ca-app-pub-xxx/111",
 *         rewardedUnitId     = "ca-app-pub-xxx/222",
 *         appOpenUnitId      = "ca-app-pub-xxx/333",
 *         nativeUnitId       = "ca-app-pub-xxx/444",
 *     )
 *
 *     override fun provideRemoteConfigDefaults() = mapOf(
 *         "ads_enabled"           to true,
 *         "interstitial_enabled"  to true,
 *         "rewarded_enabled"      to true,
 *         "interstitial_interval" to 60L,
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

    /** All ad unit IDs and tuning parameters for [SmartAdManager]. */
    abstract fun provideAdsConfig(): SmartAdConfig

    /**
     * Firebase Remote Config default values.
     * These are used immediately (before any fetch) so ads behave
     * correctly even on first launch with no network.
     */
    abstract fun provideRemoteConfigDefaults(): Map<String, Any>

    // ── Lifecycle ────────────────────────────────────────────────────

    override fun onCreate() {
        super.onCreate()
        initRemoteConfig()
    }

    // ── Internal API (called by AdsLib after consent + SDK init) ─────

    /**
     * Called by [AdsLib.initWithActivity] once MobileAds is ready.
     * Starts all ad preloaders via [SmartAdManager].
     */
    internal fun onAdsReady() {
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
            config      = provideAdsConfig().copy(
                interstitialIntervalSec = intervalSec
            )
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
}
