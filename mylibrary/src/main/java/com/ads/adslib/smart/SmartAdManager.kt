package com.ads.adslib.smart

import android.app.Activity
import android.app.Application
import android.content.Context
import android.view.ViewGroup
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.ads.adslib.core.callback.RewardCallback
import com.ads.adslib.util.AdLog

/**
 * Production-ready smart ad preloading system.
 *
 * | Format       | Strategy                                           |
 * |-------------|-----------------------------------------------------|
 * | Interstitial | Always one loaded; auto-reload; interval guard     |
 * | App Open     | Loaded in Application; shown on foreground return  |
 * | Rewarded     | On-demand; loaded per screen; released on exit     |
 * | Native       | LRU cache of N ads; expiry-aware; auto-prefetch    |
 *
 * ## Setup — call once in Application.onCreate after AdsSdk.initialize
 * ```kotlin
 * SmartAdManager.init(
 *     application = this,
 *     config = SmartAdConfig(
 *         interstitialUnitId      = "ca-app-pub-.../...",
 *         rewardedUnitId          = "ca-app-pub-.../...",
 *         appOpenUnitId           = "ca-app-pub-.../...",
 *         nativeUnitId            = "ca-app-pub-.../...",
 *         interstitialIntervalSec = 30,
 *         nativeCacheSize         = 3,
 *     )
 * )
 * ```
 *
 * ## Interstitial
 * ```kotlin
 * SmartAdManager.tryShowInterstitial(activity)   // on level complete etc.
 * ```
 *
 * ## App Open
 * Shown automatically when the app returns from background. Nothing extra needed.
 *
 * ## Rewarded  (call in the screen that needs rewarded ads)
 * ```kotlin
 * override fun onResume() { SmartAdManager.prepareRewarded(this) }
 * override fun onPause()  { SmartAdManager.releaseRewarded() }
 * SmartAdManager.showRewarded(activity, rewardCallback,
 *     onNotReady = { showSpinner() }
 * )
 * ```
 *
 * ## Native
 * ```kotlin
 * SmartAdManager.prefetchNative(context)               // screen entry
 * SmartAdManager.attachNative(container,               // when slot is visible
 *     onEmpty = { container.visibility = View.GONE }
 * )
 * ```
 */
object SmartAdManager {

    private var interstitialPreloader: InterstitialPreloader? = null
    private var appOpenPreloader: AppOpenAdPreloader? = null
    private var rewardedPreloader: RewardedAdPreloader? = null
    private var nativeCache: NativeAdCache? = null
    private var appRef: Application? = null

    /** Re-arms every preloader's backoff each time the app returns to foreground. */
    private val foregroundObserver = object : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            interstitialPreloader?.onForeground()
            rewardedPreloader?.onForeground()
            appRef?.let { nativeCache?.onForeground(it) }
        }
    }

    @Volatile
    private var initialized = false

    // ── Init ─────────────────────────────────────────────────────────

    /**
     * Initialize the smart ad system. Call once from [Application.onCreate]
     * AFTER [com.ads.adslib.AdsSdk.initialize] completes.
     */
    fun init(application: Application, config: SmartAdConfig) {
        if (initialized) {
            AdLog.d("smart_ad_manager", "already initialized — skipping")
            return
        }
        initialized = true
        appRef = application

        interstitialPreloader = InterstitialPreloader(config).also { it.start(application) }
        appOpenPreloader      = AppOpenAdPreloader(application, config).also { it.start() }
        rewardedPreloader     = RewardedAdPreloader(config)
        nativeCache           = NativeAdCache(config).also { it.prefetch(application) }

        ProcessLifecycleOwner.get().lifecycle.addObserver(foregroundObserver)

        AdLog.d("smart_ad_manager", "initialized — preloading started")
    }

    // ── Interstitial ─────────────────────────────────────────────────

    /**
     * Show the preloaded interstitial if ready and interval has passed.
     * @return true if the ad was shown.
     */
    fun tryShowInterstitial(activity: Activity): Boolean =
        interstitialPreloader?.tryShow(activity) ?: false

    val isInterstitialReady: Boolean
        get() = interstitialPreloader?.isReady == true

    // ── Rewarded ─────────────────────────────────────────────────────

    /**
     * Begin loading a rewarded ad for this screen.
     * Call from the screen's onStart / onResume.
     */
    fun prepareRewarded(context: Context) {
        rewardedPreloader?.prepare(context)
            ?: AdLog.w("smart_ad_manager", "prepareRewarded: not initialized")
    }

    /**
     * Destroy the current rewarded ad and stop loading.
     * Call from the screen's onStop / onPause.
     */
    fun releaseRewarded() {
        rewardedPreloader?.release()
    }

    /**
     * Show the loaded rewarded ad.
     * @param onNotReady Called when the ad is not ready yet.
     */
    fun showRewarded(
        activity: Activity,
        callback: RewardCallback,
        onNotReady: () -> Unit = {}
    ) {
        val preloader = rewardedPreloader
        if (preloader == null) {
            AdLog.w("smart_ad_manager", "showRewarded: not initialized")
            onNotReady()
            return
        }
        preloader.show(activity, callback, onNotReady)
    }

    val isRewardedReady: Boolean
        get() = rewardedPreloader?.isReady == true

    // ── Native ───────────────────────────────────────────────────────

    /**
     * Pre-load native ads into the cache. Call when entering a screen
     * that shows native ads.
     */
    fun prefetchNative(context: Context) {
        nativeCache?.prefetch(context)
            ?: AdLog.w("smart_ad_manager", "prefetchNative: not initialized")
    }

    /**
     * Inflate + populate the next cached native ad into [container].
     * @param onEmpty Called when the cache is empty — show a placeholder.
     */
    fun attachNative(container: ViewGroup, onEmpty: () -> Unit = {}) {
        nativeCache?.attachNext(container, onEmpty)
            ?: onEmpty()
    }

    val nativeReadyCount: Int
        get() = nativeCache?.readyCount ?: 0

    // ── Teardown ─────────────────────────────────────────────────────

    /**
     * Release all resources. Call only on full process shutdown.
     * Do NOT call from Activity.onDestroy — preloaders survive rotation.
     */
    fun destroy() {
        ProcessLifecycleOwner.get().lifecycle.removeObserver(foregroundObserver)
        interstitialPreloader?.destroy()
        appOpenPreloader?.destroy()
        rewardedPreloader?.release()
        nativeCache?.destroy()
        interstitialPreloader = null
        appOpenPreloader      = null
        rewardedPreloader     = null
        nativeCache           = null
        appRef                = null
        initialized           = false
        AdLog.d("smart_ad_manager", "destroyed")
    }
}
