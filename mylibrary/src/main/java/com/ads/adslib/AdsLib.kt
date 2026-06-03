package com.ads.adslib

import android.app.Activity
import com.ads.adslib.config.remote.AdsConfigRepository
import com.ads.adslib.consent.ConsentManager
import com.ads.adslib.smart.SmartAdConfig
import com.ads.adslib.smart.SmartAdManager

/**
 * Activity-level entry point for the second half of initialization.
 *
 * There are two ways to use AdsLib:
 *
 * 1. **MyLibrary path** — extend [MyLibrary] in your Application and call the
 *    no-config [initWithActivity] overload. The library handles Remote Config
 *    bootstrap and (optional) [SmartAdManager] preloading for you.
 *
 * 2. **Standalone path** — for apps whose Application class already extends
 *    something else (so they can't extend [MyLibrary]). Call the
 *    [initWithActivity] overload that takes [debug] / [adsConfigJson] /
 *    [smartConfig] directly. Nothing requires [MyLibrary]; the host app owns
 *    Remote Config and just hands AdsLib the parsed `ads_config` JSON.
 *
 * ```kotlin
 * // Standalone (host app already has its own Application + Remote Config):
 * AdsLib.initWithActivity(
 *     activity      = this,
 *     debug         = BuildConfig.DEBUG,
 *     adsConfigJson = myRemoteConfig.getString("ads_config"), // optional
 *     smartConfig   = SmartAdConfig(
 *         interstitialScreen = "search",
 *         rewardedScreen     = "search",
 *         nativeScreen       = "detail",
 *     ),
 *     onReady   = { loadYourAds() },
 *     onBlocked = { /* consent denied — no ads */ },
 * )
 * ```
 */
object AdsLib {

    /** True once MobileAds.initialize() has completed successfully. */
    val isReady: Boolean get() = AdsSdk.isInitialized()

    /**
     * MyLibrary path — gather consent then initialize all ad SDKs, deriving the
     * debug flag and preloading config from the [MyLibrary] Application subclass.
     * Requires the host Application to extend [MyLibrary].
     */
    fun initWithActivity(
        activity: Activity,
        onReady: () -> Unit,
        onBlocked: () -> Unit = {}
    ) {
        val myLibrary = MyLibrary.from(activity)
        initWithActivity(
            activity      = activity,
            debug         = myLibrary.isDebugBuild(),
            adsConfigJson = null, // MyLibrary already loaded RC into AdsConfigRepository
            ironSourceAppKey = null,
            smartConfig   = null, // MyLibrary.onAdsReady() starts SmartAdManager
            onReady       = { myLibrary.onAdsReady(); onReady() },
            onBlocked     = onBlocked,
        )
    }

    /**
     * Standalone path — no [MyLibrary] required. Gather consent then initialize
     * all ad SDKs.
     *
     * Safe to call multiple times — no-ops if already initialized.
     *
     * @param activity      Hosts the UMP consent form if one must be shown.
     * @param debug         Enables verbose logging + AdMob test devices.
     * @param adsConfigJson Optional `ads_config` JSON. If non-blank and the
     *   repository isn't loaded yet, it is parsed before SDK init so a Unity
     *   game id / waterfall is available immediately. The host app may also load
     *   it itself via [AdsConfigRepository.load]; passing it here is just a
     *   convenience / offline fallback.
     * @param ironSourceAppKey Explicit ironSource / LevelPlay app key. If null, it
     *   is read from the loaded `ads_config` (`AdsConfigRepository.ironSourceAppKey()`).
     * @param smartConfig   When non-null, the app-wide [SmartAdManager]
     *   preloaders (interstitial / rewarded / app-open / native cache) start once
     *   the SDKs are ready. Null = use only the per-screen managers.
     * @param onReady       Called on the main thread when everything is ready.
     * @param onBlocked     Called when the user denied consent — do not load ads.
     */
    fun initWithActivity(
        activity: Activity,
        debug: Boolean,
        adsConfigJson: String? = null,
        ironSourceAppKey: String? = null,
        testDeviceIds: List<String> = emptyList(),
        metaTestDeviceHashes: List<String> = emptyList(),
        smartConfig: SmartAdConfig? = null,
        onReady: () -> Unit,
        onBlocked: () -> Unit = {}
    ) {
        if (isReady) {
            maybeStartSmartManager(activity, smartConfig)
            onReady()
            return
        }

        // Parse the provided ads_config up front so Unity / waterfall units are
        // available the moment we initialize (host RC fetch can still override
        // it later via AdsConfigRepository.load).
        if (!adsConfigJson.isNullOrBlank() && !AdsConfigRepository.isLoaded) {
            AdsConfigRepository.load(activity.applicationContext, adsConfigJson)
        }

        val consent = ConsentManager(activity)
        consent.gatherConsent(activity = activity, testDeviceId = null) {
            if (consent.canRequestAds) {
                AdsSdk.initialize(
                    context              = activity.applicationContext,
                    debug                = debug,
                    hasConsent           = consent.canRequestAds,
                    testDeviceIds        = testDeviceIds,
                    metaTestDeviceHashes = metaTestDeviceHashes,
                    ironSourceAppKey     = ironSourceAppKey ?: AdsConfigRepository.ironSourceAppKey(),
                ) {
                    maybeStartSmartManager(activity, smartConfig)
                    onReady()
                }
            } else {
                onBlocked()
            }
        }
    }

    private fun maybeStartSmartManager(activity: Activity, smartConfig: SmartAdConfig?) {
        val config = smartConfig ?: return
        // Always initialize when a SmartAdConfig is provided — do NOT gate on
        // adsEnabled here. Remote Config may still be fetching at SDK-init time
        // (so adsEnabled could read the offline default), and SmartAdManager.init
        // is what registers the AdsConfigRepository config-loaded listener that
        // re-kicks the preloaders once RC arrives. The kill-switch is still
        // honoured downstream: every preloader resolves its units from
        // AdsConfigRepository, which returns null while ads are disabled, so no
        // ad loads or shows until ads_config.enabled becomes true.
        SmartAdManager.init(activity.application, config)
    }
}
