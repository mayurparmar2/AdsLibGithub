package com.ads.adslib.smart

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.ads.adslib.core.model.AdError
import com.ads.adslib.util.AdLog
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd

/**
 * Preloads an App Open ad in the Application class and shows it automatically
 * whenever the app returns to the foreground from background.
 *
 * App Open ads expire after 4 hours — this class tracks load time and
 * discards stale ads before attempting to show.
 *
 * Register via [SmartAdManager.init] — do NOT call directly from Activities.
 */
internal class AppOpenAdPreloader(
    private val application: Application,
    private val config: SmartAdConfig,
) : Application.ActivityLifecycleCallbacks {

    private var appOpenAd: AppOpenAd? = null
    private var loadedAt: Long = 0L
    private var isShowingAd = false
    private var currentActivity: Activity? = null
    private var startedCount = 0          // tracks foreground/background state

    private val retry = AdRetryScheduler(
        tag         = "appopen_preloader",
        maxRetries  = config.maxRetries,
        baseDelayMs = config.retryBaseDelayMs,
        maxDelayMs  = config.retryMaxDelayMs,
    )

    // ── Lifecycle callbacks ──────────────────────────────────────────

    override fun onActivityStarted(activity: Activity) {
        if (!isShowingAd) currentActivity = activity
        startedCount++
        if (startedCount > 1) return  // already in foreground
        // App came to foreground — show if ready
        showIfAvailable()
    }

    override fun onActivityStopped(activity: Activity) { startedCount-- }
    override fun onActivityResumed(activity: Activity) { if (!isShowingAd) currentActivity = activity }
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityCreated(activity: Activity, b: Bundle?) {}
    override fun onActivitySaveInstanceState(activity: Activity, b: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivity == activity) currentActivity = null
    }

    // ── Public ───────────────────────────────────────────────────────

    /** Call once from [SmartAdManager.init]. */
    fun start() {
        application.registerActivityLifecycleCallbacks(this)
        loadAd()
    }

    fun destroy() {
        retry.cancel()
        application.unregisterActivityLifecycleCallbacks(this)
        appOpenAd?.fullScreenContentCallback = null
        appOpenAd = null
        currentActivity = null
    }

    // ── Internal ─────────────────────────────────────────────────────

    private fun isAdFresh(): Boolean {
        val fourHoursMs = 4 * 60 * 60 * 1_000L
        return System.currentTimeMillis() - loadedAt < fourHoursMs
    }

    private fun showIfAvailable() {
        val ad = appOpenAd ?: return
        if (!isAdFresh()) {
            AdLog.d("appopen_preloader", "ad expired — reloading")
            appOpenAd = null
            loadAd()
            return
        }
        val activity = currentActivity ?: return
        if (isShowingAd) return

        isShowingAd = true
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                AdLog.d("appopen_preloader", "shown")
            }
            override fun onAdDismissedFullScreenContent() {
                AdLog.d("appopen_preloader", "dismissed — reloading")
                appOpenAd = null
                isShowingAd = false
                loadAd()
            }
            override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) {
                AdLog.w("appopen_preloader", "show failed: ${error.message}")
                appOpenAd = null
                isShowingAd = false
                loadAd()
            }
        }
        ad.show(activity)
    }

    private fun loadAd() {
        AppOpenAd.load(
            application,
            config.appOpenUnitId,
            AdRequest.Builder().build(),
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    AdLog.d("appopen_preloader", "loaded")
                    appOpenAd = ad
                    loadedAt = System.currentTimeMillis()
                    retry.reset()
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    AdLog.w("appopen_preloader", "load failed: ${error.message}")
                    retry.schedule { loadAd() }
                }
            }
        )
    }
}
