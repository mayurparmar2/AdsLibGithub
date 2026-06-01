package com.ads.adslib

import android.content.Context
import com.ads.adslib.config.RemoteConfigManager
import com.ads.adslib.util.AdLog
import com.facebook.ads.AdSettings
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.UnityAds
import com.unity3d.ads.metadata.MetaData

/**
 * Single entry point for the Ads library — initialize once, then create per-placement managers.
 *
 * Typical splash-screen flow:
 * ```
 * val consent = ConsentManager(this)
 * consent.gatherConsent(this) {
 *     if (consent.canRequestAds) {
 *         AdsSdk.initialize(this) {
 *             // SDKs ready — start loading ads / dismiss splash
 *         }
 *     }
 * }
 * ```
 */
object AdsSdk {

    @Volatile
    private var initialized = false

    @Volatile
    private var initializing = false

    /** Callbacks waiting for an in-flight initialization to finish. */
    private val pendingCallbacks = mutableListOf<() -> Unit>()

    /** Optional remote-config handle, exposed for app-side checks. */
    val remoteConfig: RemoteConfigManager by lazy { RemoteConfigManager() }

    /**
     * Initialize underlying ad SDKs. Safe to call multiple times — initialization
     * runs only once; concurrent callers all get notified when it completes.
     *
     * @param context application context.
     * @param debug enables verbose logging and registers test devices.
     * @param hasConsent the user's GDPR/CCPA consent decision. MUST reflect the
     *   real UMP result — it is forwarded to Meta and Unity so they comply too.
     * @param testDeviceIds AdMob test device IDs (debug only).
     * @param unityGameId Unity Ads game id (from Remote Config). Null/blank skips Unity.
     * @param onComplete called on the main thread when AdMob initialization finishes.
     */
    fun initialize(
        context: Context,
        debug: Boolean = false,
        hasConsent: Boolean = true,
        testDeviceIds: List<String> = emptyList(),
        metaTestDeviceHashes: List<String> = emptyList(),
        unityGameId: String? = null,
        onComplete: () -> Unit = {}
    ) {
        // Guard the check-then-act atomically — @Volatile alone does NOT make
        // this compound action thread-safe.
        synchronized(this) {
            AdLog.enabled = debug

            // Already done — fire immediately.
            if (initialized) {
                onComplete()
                return
            }

            // Init in flight — queue this callback, don't start a second init.
            pendingCallbacks.add(onComplete)
            if (initializing) return

            initializing = true
        }

        val appContext = context.applicationContext

        if (debug && testDeviceIds.isNotEmpty()) {
            MobileAds.setRequestConfiguration(
                RequestConfiguration.Builder()
                    .setTestDeviceIds(testDeviceIds)
                    .build()
            )
        }

        // Meta Audience Network — init + forward consent BEFORE any FAN ad loads.
        initMeta(appContext, hasConsent, if (debug) metaTestDeviceHashes else emptyList())
        if(debug){
            AdSettings.setTestMode(false)
        }

        // Unity Ads inits independently of AdMob (its own SDK).
        initUnity(appContext, unityGameId, debug, hasConsent)

        // BUG FIX: use the listener overload so callbacks fire only AFTER
        // MobileAds is actually initialized (the previous `.apply {}` ran the
        // block synchronously, before init had completed).
        MobileAds.initialize(appContext) {
            AdLog.d("sdk", "MobileAds initialized")
            val callbacks = synchronized(this) {
                initialized = true
                initializing = false
                pendingCallbacks.toList().also { pendingCallbacks.clear() }
            }
            callbacks.forEach { it() }
        }
    }

    /**
     * Initialize Meta Audience Network and forward the consent decision.
     * Wrapped in runCatching so a missing/incompatible FAN build never crashes
     * host SDK init — the Meta waterfall rung simply fails later if absent.
     */
    private fun initMeta(context: Context, hasConsent: Boolean, testDeviceHashes: List<String>) {
        runCatching {
            // setDataProcessingOptions(emptyArray) = no restriction (consent given);
            // ["LDU"] = Limited Data Use for opted-out / CCPA users.
            if (hasConsent) {
                AdSettings.setDataProcessingOptions(arrayOf())
            } else {
                AdSettings.setDataProcessingOptions(arrayOf("LDU"), 0, 0)
            }
            // Debug only — register Meta test devices so FAN serves test ads
            // instead of "No fill". Each device's hash is printed in logcat by the
            // Meta SDK on first request ("add AdSettings.addTestDevice(...)").
            if (testDeviceHashes.isNotEmpty()) {
                AdSettings.addTestDevices(testDeviceHashes)
            }
            if (!AudienceNetworkAds.isInitialized(context)) {
                AudienceNetworkAds.initialize(context)
            }
            AdLog.d("sdk", "Meta Audience Network initialized (consent=$hasConsent)")
        }.onFailure { AdLog.w("sdk", "Meta init skipped: ${it.message}") }
    }

    fun isInitialized(): Boolean = initialized

    /** Initialize Unity Ads once, if a game id is configured. */
    private fun initUnity(context: Context, gameId: String?, debug: Boolean, hasConsent: Boolean) {
        if (gameId.isNullOrBlank()) return

        // Forward GDPR consent to Unity BEFORE any ad is loaded (Unity policy).
        runCatching {
            MetaData(context).apply {
                set("gdpr.consent", hasConsent)
                commit()
            }
        }.onFailure { AdLog.w("sdk", "Unity consent metadata failed: ${it.message}") }

        if (UnityAds.isInitialized()) return
        UnityAds.initialize(
            context.applicationContext,
            gameId,
            debug,                       // testMode = debug build
            object : IUnityAdsInitializationListener {
                override fun onInitializationComplete() {
                    AdLog.d("sdk", "UnityAds initialized")
                }
                override fun onInitializationFailed(
                    error: UnityAds.UnityAdsInitializationError?,
                    message: String?
                ) {
                    AdLog.w("sdk", "UnityAds init failed: $message")
                }
            }
        )
    }
}
