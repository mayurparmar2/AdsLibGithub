package com.ads.adslib.smart

import android.app.Activity
import android.content.Context
import com.ads.adslib.admob.rewarded.RewardedAdManager
import com.ads.adslib.core.callback.RewardCallback
import com.ads.adslib.core.model.AdError
import com.ads.adslib.core.model.AdFormat
import com.ads.adslib.core.model.AdNetwork
import com.ads.adslib.core.model.AdUnitConfig
import com.ads.adslib.util.AdLog

/**
 * Loads a rewarded ad on demand — only when a specific screen needs it.
 *
 * Key optimisation — **preload-on-show (double buffer):**
 * When a rewarded ad becomes visible ([RewardCallback.onShown]), the next
 * ad starts loading in a separate manager. The user can watch a second
 * rewarded ad on the same screen with no wait.
 *
 *   readyManager   → loaded, waiting to be shown
 *   showingManager → currently on screen (destroyed on dismiss)
 *   activeCallback → the caller's RewardCallback for the current show
 *
 * Pattern:
 * ```
 * override fun onResume() { SmartAdManager.prepareRewarded(this) }
 * override fun onPause()  { SmartAdManager.releaseRewarded() }
 * SmartAdManager.showRewarded(activity, callback, onNotReady = { ... })
 * ```
 *
 * Why not always loaded?  Rewarded video assets are heavy (several MB),
 * so they are loaded per-screen, not app-wide.
 */
internal class RewardedAdPreloader(private val config: SmartAdConfig) {

    private var readyManager: RewardedAdManager? = null
    private var showingManager: RewardedAdManager? = null
    private var activeCallback: RewardCallback? = null
    private var isPrepared = false
    private var appContext: Context? = null

    private val retry = AdRetryScheduler(
        tag         = "rewarded_preloader",
        maxRetries  = config.maxRetries,
        baseDelayMs = config.retryBaseDelayMs,
        maxDelayMs  = config.retryMaxDelayMs,
    )

    // ── Public ───────────────────────────────────────────────────────

    /**
     * Start loading a rewarded ad for the upcoming screen.
     * Call from the screen's onStart / onResume. Idempotent.
     */
    fun prepare(context: Context) {
        appContext = context.applicationContext
        if (isPrepared) return
        isPrepared = true
        load()
    }

    /**
     * Show the loaded rewarded ad and forward all events to [callback].
     *
     * @param onNotReady Called when no ad is ready — disable the button or
     *                   show a spinner. The ad keeps loading in background.
     */
    fun show(
        activity: Activity,
        callback: RewardCallback,
        onNotReady: () -> Unit = {}
    ) {
        val mgr = readyManager
        if (mgr == null || !mgr.isReady()) {
            AdLog.d("rewarded_preloader", "not ready")
            onNotReady()
            return
        }
        activeCallback = callback           // forward events for THIS show
        showingManager = mgr
        readyManager = null                 // freed for the next preload
        mgr.show(activity)
    }

    /** Release everything. Call from the screen's onStop / onPause. */
    fun release() {
        retry.cancel()
        readyManager?.destroy()
        showingManager?.destroy()
        readyManager = null
        showingManager = null
        activeCallback = null
        isPrepared = false
        AdLog.d("rewarded_preloader", "released")
    }

    val isReady: Boolean get() = readyManager?.isReady() == true

    // ── Internal ─────────────────────────────────────────────────────

    /** Loads a fresh rewarded ad into [readyManager]. */
    private fun load() {
        val context = appContext ?: return
        val adConfig = AdUnitConfig(
            placementKey = "smart_rewarded",
            format       = AdFormat.REWARDED,
            waterfall    = config.rewardedUnits(),
        )
        val mgr = RewardedAdManager(adConfig)

        mgr.load(context, object : RewardCallback {
            override fun onLoaded(network: AdNetwork) {
                AdLog.d("rewarded_preloader", "ready via $network")
                readyManager = mgr
                retry.reset()
            }
            override fun onFailedToLoad(error: AdError) {
                AdLog.w("rewarded_preloader", "failed: $error")
                retry.schedule { load() }
            }
            override fun onRewardEarned(type: String, amount: Int) {
                activeCallback?.onRewardEarned(type, amount)
            }
            override fun onShown(network: AdNetwork) {
                // Block App Open from stacking on top of this ad.
                FullScreenAdState.onShown()
                // ✅ Ad visible — preload the NEXT rewarded ad immediately.
                AdLog.d("rewarded_preloader", "shown via $network — preloading next")
                activeCallback?.onShown(network)
                load()
            }
            override fun onClicked(network: AdNetwork) {
                activeCallback?.onClicked(network)
            }
            override fun onImpression(network: AdNetwork) {
                activeCallback?.onImpression(network)
            }
            override fun onDismissed(network: AdNetwork) {
                FullScreenAdState.onClosed()
                activeCallback?.onDismissed(network)
                showingManager?.destroy()
                showingManager = null
                activeCallback = null
                if (readyManager == null && !retry.isExhausted) load()
            }
            override fun onFailedToShow(error: AdError) {
                AdLog.w("rewarded_preloader", "show failed: $error")
                FullScreenAdState.onClosed()
                activeCallback?.onFailedToShow(error)
                showingManager?.destroy()
                showingManager = null
                activeCallback = null
                if (readyManager == null) load()
            }
        })
    }
}
