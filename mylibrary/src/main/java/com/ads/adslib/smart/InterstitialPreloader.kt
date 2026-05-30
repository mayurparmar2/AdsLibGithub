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
 * - Auto-reloads immediately after show/fail.
 * - Exponential backoff on no-fill.
 * - Interval guard prevents showing twice in quick succession.
 */
internal class InterstitialPreloader(private val config: SmartAdConfig) {

    private var manager: InterstitialAdManager? = null
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
        load(context.applicationContext)
    }

    /**
     * Shows the preloaded interstitial if:
     *  (a) an ad is ready, AND
     *  (b) [SmartAdConfig.interstitialIntervalSec] seconds have elapsed.
     *
     * Returns true if the ad was shown, false otherwise.
     */
    fun tryShow(activity: Activity): Boolean {
        val mgr = manager ?: return false.also {
            AdLog.d("interstitial_preloader", "not ready — still loading")
        }
        if (!mgr.isReady()) return false.also {
            AdLog.d("interstitial_preloader", "not ready — waterfall in progress")
        }
        val elapsed = System.currentTimeMillis() - lastShownAt
        val intervalMs = config.interstitialIntervalSec * 1_000L
        if (elapsed < intervalMs) return false.also {
            AdLog.d("interstitial_preloader",
                "interval not passed — ${(intervalMs - elapsed) / 1000}s remaining")
        }
        lastShownAt = System.currentTimeMillis()
        mgr.show(activity)
        return true
    }

    val isReady: Boolean get() = manager?.isReady() == true

    fun destroy() {
        retry.cancel()
        manager?.destroy()
        manager = null
        appContext = null
    }

    // ── Internal ─────────────────────────────────────────────────────

    private fun load(context: Context) {
        val adConfig = AdUnitConfig(
            placementKey = "smart_interstitial",
            format       = AdFormat.INTERSTITIAL,
            waterfall    = config.interstitialUnits(),
        )
        val mgr = InterstitialAdManager(adConfig)
        manager = mgr
        mgr.load(context, object : AdCallback {
            override fun onLoaded(network: AdNetwork) {
                AdLog.d("interstitial_preloader", "ready via $network")
                retry.reset()
            }
            override fun onFailedToLoad(error: AdError) {
                AdLog.w("interstitial_preloader", "failed: $error")
                // Exponential backoff retry
                retry.schedule { load(context) }
            }
            override fun onDismissed(network: AdNetwork) {
                // Reload immediately after dismiss so next show is instant
                mgr.destroy()
                manager = null
                load(context)
            }
            override fun onFailedToShow(error: AdError) {
                AdLog.w("interstitial_preloader", "show failed: $error")
                mgr.destroy()
                manager = null
                load(context)
            }
        })
    }
}
