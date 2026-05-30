package com.ads.adslib.smart

import android.app.Activity
import android.content.Context
import com.ads.adslib.admob.interstitial.InterstitialAdManager
import com.ads.adslib.core.callback.AdCallback
import com.ads.adslib.core.model.AdError
import com.ads.adslib.core.model.AdFormat
import com.ads.adslib.core.model.AdNetwork
import com.ads.adslib.core.model.AdUnitConfig
import com.ads.adslib.util.AdLog

/**
 * Keeps exactly one interstitial ad pre-loaded at all times.
 *
 * Key optimisation — **preload-on-show (double buffer):**
 * The moment an ad becomes visible ([AdCallback.onShown]), the next ad
 * starts loading in a separate manager. By the time the user dismisses
 * the current ad, the next one is already loaded — zero gap.
 *
 *   readyManager   → loaded, waiting to be shown
 *   showingManager → currently on screen (destroyed on dismiss)
 *
 * - Exponential backoff on no-fill.
 * - Interval guard prevents showing twice in quick succession.
 */
internal class InterstitialPreloader(private val config: SmartAdConfig) {

    private var readyManager: InterstitialAdManager? = null
    private var showingManager: InterstitialAdManager? = null
    private var lastShownAt = 0L
    private var appContext: Context? = null

    private val retry = AdRetryScheduler(
        tag          = "interstitial_preloader",
        maxRetries   = config.maxRetries,
        baseDelayMs  = config.retryBaseDelayMs,
        maxDelayMs   = config.retryMaxDelayMs,
    )

    // ── Public ───────────────────────────────────────────────────────

    fun start(context: Context) {
        appContext = context.applicationContext
        load()
    }

    /**
     * Shows the preloaded interstitial if ready and the interval has passed.
     * Triggers the next preload automatically via [AdCallback.onShown].
     */
    fun tryShow(activity: Activity): Boolean {
        val mgr = readyManager
        if (mgr == null || !mgr.isReady()) {
            AdLog.d("interstitial_preloader", "not ready — still loading")
            return false
        }
        val elapsed = System.currentTimeMillis() - lastShownAt
        val intervalMs = config.interstitialIntervalSec * 1_000L
        if (elapsed < intervalMs) {
            AdLog.d("interstitial_preloader",
                "interval not passed — ${(intervalMs - elapsed) / 1000}s remaining")
            return false
        }
        lastShownAt = System.currentTimeMillis()

        // Promote ready → showing; readyManager freed for the next preload
        showingManager = mgr
        readyManager = null
        mgr.show(activity)
        return true
    }

    val isReady: Boolean get() = readyManager?.isReady() == true

    fun destroy() {
        retry.cancel()
        readyManager?.destroy()
        showingManager?.destroy()
        readyManager = null
        showingManager = null
        appContext = null
    }

    // ── Internal ─────────────────────────────────────────────────────

    /** Loads a fresh interstitial into [readyManager]. */
    private fun load() {
        val context = appContext ?: return
        val adConfig = AdUnitConfig(
            placementKey = "smart_interstitial",
            format       = AdFormat.INTERSTITIAL,
            waterfall    = config.interstitialUnits(),
        )
        val mgr = InterstitialAdManager(adConfig)

        mgr.load(context, object : AdCallback {
            override fun onLoaded(network: AdNetwork) {
                AdLog.d("interstitial_preloader", "ready via $network")
                readyManager = mgr
                retry.reset()
            }
            override fun onFailedToLoad(error: AdError) {
                AdLog.w("interstitial_preloader", "failed: $error")
                retry.schedule { load() }
            }
            override fun onShown(network: AdNetwork) {
                // Block App Open from stacking on top of this ad.
                FullScreenAdState.onShown()
                // ✅ Ad is now visible — preload the NEXT ad immediately
                // so it's ready before the user dismisses this one.
                AdLog.d("interstitial_preloader", "shown via $network — preloading next")
                load()
            }
            override fun onDismissed(network: AdNetwork) {
                FullScreenAdState.onClosed()
                // Current ad finished — free the manager that just showed.
                showingManager?.destroy()
                showingManager = null
                // Safety net: if the on-show preload somehow failed, retry.
                if (readyManager == null && !retry.isExhausted) load()
            }
            override fun onFailedToShow(error: AdError) {
                AdLog.w("interstitial_preloader", "show failed: $error")
                FullScreenAdState.onClosed()
                showingManager?.destroy()
                showingManager = null
                if (readyManager == null) load()
            }
        })
    }
}
