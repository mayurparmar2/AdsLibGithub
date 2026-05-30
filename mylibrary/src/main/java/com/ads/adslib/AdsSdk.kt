package com.ads.adslib

import android.content.Context
import com.ads.adslib.config.RemoteConfigManager
import com.ads.adslib.util.AdLog
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.UnityAds

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
     * @param testDeviceIds AdMob test device IDs (debug only).
     * @param unityGameId Unity Ads game id (from Remote Config). Null/blank skips Unity.
     * @param onComplete called on the main thread when AdMob initialization finishes.
     */
    fun initialize(
        context: Context,
        debug: Boolean = false,
        testDeviceIds: List<String> = emptyList(),
        unityGameId: String? = null,
        onComplete: () -> Unit = {}
    ) {
        AdLog.enabled = debug

        // Already done — fire immediately.
        if (initialized) {
            onComplete()
            return
        }

        // Init in flight — queue this callback, don't start a second init.
        if (initializing) {
            pendingCallbacks.add(onComplete)
            return
        }

        initializing = true
        pendingCallbacks.add(onComplete)

        if (debug && testDeviceIds.isNotEmpty()) {
            MobileAds.setRequestConfiguration(
                RequestConfiguration.Builder()
                    .setTestDeviceIds(testDeviceIds)
                    .build()
            )
        }

        // Unity Ads inits independently of AdMob (its own SDK).
        initUnity(context, unityGameId, debug)

        // BUG FIX: use the listener overload so callbacks fire only AFTER
        // MobileAds is actually initialized (the previous `.apply {}` ran the
        // block synchronously, before init had completed).
        MobileAds.initialize(context) {
            initialized = true
            initializing = false
            AdLog.d("sdk", "MobileAds initialized")
            val callbacks = pendingCallbacks.toList()
            pendingCallbacks.clear()
            callbacks.forEach { it() }
        }
    }

    fun isInitialized(): Boolean = initialized

    /** Initialize Unity Ads once, if a game id is configured. */
    private fun initUnity(context: Context, gameId: String?, debug: Boolean) {
        if (gameId.isNullOrBlank()) return
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
