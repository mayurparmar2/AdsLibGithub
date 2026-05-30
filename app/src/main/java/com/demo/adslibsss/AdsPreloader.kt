package com.demo.adslibsss

import android.app.Activity
import android.content.Context
import com.ads.adslib.AdsSdk
import com.ads.adslib.admob.interstitial.InterstitialAdManager
import com.ads.adslib.admob.rewarded.RewardedAdManager
import com.ads.adslib.core.callback.AdCallback
import com.ads.adslib.core.callback.RewardCallback
import com.ads.adslib.core.model.AdError
import com.ads.adslib.core.model.AdFormat
import com.ads.adslib.core.model.AdNetwork
import com.ads.adslib.core.model.AdUnitConfig
import com.ads.adslib.core.model.NetworkAdUnit
import com.ads.adslib.util.AdLog

/**
 * Keeps one Interstitial and one Rewarded ad loaded at all times.
 *
 * How it works:
 *  - preload() is called once (from AdsInitializer.onReady).
 *  - Each manager auto-reloads immediately after dismiss.
 *  - Interstitial has a configurable interval — same user won't see it
 *    twice within [interstitialIntervalMs] milliseconds.
 *  - tryShowInterstitial() returns false silently if interval not passed
 *    or ad is not ready yet (caller can choose to show something else).
 *  - showRewarded() always requires explicit user intent — no interval.
 */
object AdsPreloader {

    // ---- Ad Unit IDs (replace with your real IDs) ----
    private const val INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"
    private const val REWARDED_ID     = "ca-app-pub-3940256099942544/5224354917"

    // ---- Minimum time between two interstitial shows (ms) ----
    private var interstitialIntervalMs: Long = 30_000L   // 30 seconds default

    private var interstitialManager: InterstitialAdManager? = null
    private var rewardedManager: RewardedAdManager? = null
    private var lastInterstitialShownAt: Long = 0L
    private var isPreloading = false

    // ----------------------------------------------------------------
    // Public API
    // ----------------------------------------------------------------

    /**
     * Start preloading both ads. Call once from AdsInitializer.onReady.
     * Safe to call multiple times — only runs if not already preloading.
     *
     * @param context         Application context.
     * @param intervalSeconds Min seconds between two interstitial shows
     *                        (read from Remote Config or hardcoded).
     */
    fun preload(context: Context, intervalSeconds: Long = 30L) {
        if (isPreloading) return
        isPreloading = true
        interstitialIntervalMs = intervalSeconds * 1000L
        loadInterstitial(context)
        loadRewarded(context)
    }

    /**
     * Show interstitial if:
     *  (a) ad is loaded, AND
     *  (b) [interstitialIntervalMs] has passed since the last show.
     *
     * Returns true if the ad was shown, false otherwise (caller decides
     * what to do — e.g. proceed without an ad).
     */
    fun tryShowInterstitial(activity: Activity): Boolean {
        val mgr = interstitialManager ?: return false
        if (!mgr.isReady()) {
            AdLog.d("preloader", "interstitial not ready yet")
            return false
        }
        val elapsed = System.currentTimeMillis() - lastInterstitialShownAt
        if (elapsed < interstitialIntervalMs) {
            AdLog.d("preloader", "interstitial skipped — interval ${interstitialIntervalMs / 1000}s not passed")
            return false
        }
        lastInterstitialShownAt = System.currentTimeMillis()
        mgr.show(activity)
        return true
    }

    /**
     * Show rewarded ad. Always explicit user intent — no interval check.
     *
     * @param onNotReady Called when the ad is not loaded yet (show a
     *                   "loading…" indicator or disable the button).
     */
    fun showRewarded(
        activity: Activity,
        callback: RewardCallback,
        onNotReady: () -> Unit = {}
    ) {
        val mgr = rewardedManager
        if (mgr == null || !mgr.isReady()) {
            AdLog.d("preloader", "rewarded not ready yet")
            onNotReady()
            return
        }
        mgr.show(activity)
    }

    val isInterstitialReady: Boolean get() = interstitialManager?.isReady() == true
    val isRewardedReady: Boolean     get() = rewardedManager?.isReady()     == true

    /** Call from Application or top-level onDestroy to clean up. */
    fun destroy() {
        interstitialManager?.destroy()
        rewardedManager?.destroy()
        interstitialManager = null
        rewardedManager = null
        isPreloading = false
    }

    // ----------------------------------------------------------------
    // Internal — auto-reload on dismiss
    // ----------------------------------------------------------------

    private fun loadInterstitial(context: Context) {
        if (!AdsSdk.remoteConfig.getBoolean("interstitial_enabled")) {
            AdLog.d("preloader", "interstitial disabled via remote config")
            return
        }

        val config = AdUnitConfig(
            placementKey = "preload_interstitial",
            format       = AdFormat.INTERSTITIAL,
            waterfall    = listOf(
                NetworkAdUnit(AdNetwork.ADMOB, INTERSTITIAL_ID),
                // NetworkAdUnit(AdNetwork.META,  "META_INTERSTITIAL_ID"),
                // NetworkAdUnit(AdNetwork.UNITY, "UNITY_INTERSTITIAL_ID"),
            )
        )

        val mgr = InterstitialAdManager(config)
        interstitialManager = mgr

        mgr.load(context, object : AdCallback {
            override fun onLoaded(network: AdNetwork) {
                AdLog.d("preloader", "interstitial ready via $network")
            }
            override fun onFailedToLoad(error: AdError) {
                AdLog.w("preloader", "interstitial failed: $error — retry in 30s")
                // Retry after delay to avoid hammering the server on no-fill
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    if (isPreloading) loadInterstitial(context)
                }, 30_000L)
            }
            override fun onDismissed(network: AdNetwork) {
                // Ad shown and dismissed — destroy and reload immediately
                mgr.destroy()
                loadInterstitial(context)
            }
            override fun onFailedToShow(error: AdError) {
                AdLog.w("preloader", "interstitial show failed: $error")
                mgr.destroy()
                loadInterstitial(context)
            }
        })
    }

    private fun loadRewarded(context: Context) {
        if (!AdsSdk.remoteConfig.getBoolean("rewarded_enabled")) {
            AdLog.d("preloader", "rewarded disabled via remote config")
            return
        }

        val config = AdUnitConfig(
            placementKey = "preload_rewarded",
            format       = AdFormat.REWARDED,
            waterfall    = listOf(
                NetworkAdUnit(AdNetwork.ADMOB, REWARDED_ID),
                // NetworkAdUnit(AdNetwork.META,  "META_REWARDED_ID"),
                // NetworkAdUnit(AdNetwork.UNITY, "UNITY_REWARDED_ID"),
            )
        )

        val mgr = RewardedAdManager(config)
        rewardedManager = mgr

        mgr.load(context, object : RewardCallback {
            override fun onLoaded(network: AdNetwork) {
                AdLog.d("preloader", "rewarded ready via $network")
            }
            override fun onFailedToLoad(error: AdError) {
                AdLog.w("preloader", "rewarded failed: $error — retry in 30s")
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    if (isPreloading) loadRewarded(context)
                }, 30_000L)
            }
            override fun onRewardEarned(type: String, amount: Int) {
                // Reward dispatched to showRewarded() caller via RewardCallback
            }
            override fun onDismissed(network: AdNetwork) {
                // Immediately reload so next show is instant
                mgr.destroy()
                loadRewarded(context)
            }
            override fun onFailedToShow(error: AdError) {
                AdLog.w("preloader", "rewarded show failed: $error")
                mgr.destroy()
                loadRewarded(context)
            }
        })
    }
}
