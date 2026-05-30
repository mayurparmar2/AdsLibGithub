package com.ads.adslib.smart

import android.app.Activity
import android.content.Context
import com.ads.adslib.admob.rewarded.RewardedAdManager
import com.ads.adslib.core.callback.AdCallback
import com.ads.adslib.core.callback.RewardCallback
import com.ads.adslib.core.model.AdError
import com.ads.adslib.core.model.AdFormat
import com.ads.adslib.core.model.AdNetwork
import com.ads.adslib.core.model.AdUnitConfig
import com.ads.adslib.util.AdLog

/**
 * Loads a rewarded ad on demand — only when a specific screen needs it.
 *
 * Pattern:
 * ```
 * // In the screen that uses rewarded ads (e.g. GameActivity):
 * override fun onResume() {
 *     super.onResume()
 *     SmartAdManager.rewarded.prepare(this)
 * }
 * override fun onPause() {
 *     SmartAdManager.rewarded.release()
 *     super.onPause()
 * }
 * // On user action:
 * SmartAdManager.rewarded.show(activity, callback, onNotReady = { showLoadingUI() })
 * ```
 *
 * Why not always loaded?  Rewarded video assets are heavy (several MB).
 * Loading them on every screen wastes bandwidth and memory on screens
 * that will never show a rewarded ad.
 */
internal class RewardedAdPreloader(private val config: SmartAdConfig) {

    private var manager: RewardedAdManager? = null
    private var isPrepared = false
    private val retry = AdRetryScheduler(
        tag         = "rewarded_preloader",
        maxRetries  = config.maxRetries,
        baseDelayMs = config.retryBaseDelayMs,
        maxDelayMs  = config.retryMaxDelayMs,
    )

    // ── Public ───────────────────────────────────────────────────────

    /**
     * Start loading a rewarded ad for the upcoming screen.
     * Call from the screen's onStart / onResume.
     * Safe to call multiple times — only loads if not already prepared.
     */
    fun prepare(context: Context) {
        if (isPrepared) return
        isPrepared = true
        load(context.applicationContext)
    }

    /**
     * Show the loaded rewarded ad.
     *
     * @param onNotReady Called when the ad isn't loaded yet — caller should
     *                   disable the "Watch Ad" button or show a spinner.
     */
    fun show(
        activity: Activity,
        callback: RewardCallback,
        onNotReady: () -> Unit = {}
    ) {
        val mgr = manager
        if (mgr == null || !mgr.isReady()) {
            AdLog.d("rewarded_preloader", "not ready")
            onNotReady()
            return
        }
        mgr.show(activity)
    }

    /**
     * Release the loaded ad. Call from the screen's onStop / onPause.
     * Destroys the manager to free memory.
     */
    fun release() {
        retry.cancel()
        manager?.destroy()
        manager = null
        isPrepared = false
        AdLog.d("rewarded_preloader", "released")
    }

    val isReady: Boolean get() = manager?.isReady() == true

    // ── Internal ─────────────────────────────────────────────────────

    private fun load(context: Context) {
        val adConfig = AdUnitConfig(
            placementKey = "smart_rewarded",
            format       = AdFormat.REWARDED,
            waterfall    = config.rewardedUnits(),
        )
        val mgr = RewardedAdManager(adConfig)
        manager = mgr

        mgr.load(context, object : RewardCallback {
            override fun onLoaded(network: AdNetwork) {
                AdLog.d("rewarded_preloader", "ready via $network")
                retry.reset()
            }
            override fun onFailedToLoad(error: AdError) {
                AdLog.w("rewarded_preloader", "failed: $error")
                retry.schedule { load(context) }
            }
            override fun onRewardEarned(type: String, amount: Int) {
                // Forwarded to caller's RewardCallback via manager.show()
            }
            override fun onDismissed(network: AdNetwork) {
                // Reload immediately — user might watch another ad on same screen
                mgr.destroy()
                manager = null
                load(context)
            }
            override fun onFailedToShow(error: AdError) {
                AdLog.w("rewarded_preloader", "show failed: $error")
                mgr.destroy()
                manager = null
                load(context)
            }
        })
    }
}
