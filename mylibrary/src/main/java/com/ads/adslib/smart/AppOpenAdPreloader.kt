package com.ads.adslib.smart

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.ads.adslib.config.remote.AdsConfigRepository
import com.ads.adslib.core.callback.AdEvents
import com.ads.adslib.core.model.AdFormat
import com.ads.adslib.core.model.AdNetwork
import com.ads.adslib.util.AdLog
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd

/**
 * Preloads an App Open ad and shows it when the app returns to the
 * foreground from the background.
 *
 * **Why ProcessLifecycleOwner (not per-Activity counting)?**
 * Counting started activities cannot distinguish "user returned from
 * background" from "user just closed an interstitial" — closing a
 * full-screen ad restarts the host Activity and would wrongly trigger an
 * App Open ad on top of / right after the interstitial.
 * [ProcessLifecycleOwner] fires ON_START only on a *real* app foreground,
 * so ad-activity transitions within the app never trigger it.
 *
 * A second guard ([FullScreenAdState]) ensures an App Open ad is never
 * shown while another full-screen ad is visible or just dismissed.
 *
 * App Open ads expire after 4 hours — stale ads are discarded before show.
 */
internal class AppOpenAdPreloader(
    private val application: Application,
    private val config: SmartAdConfig,
) : Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {

    private var appOpenAd: AppOpenAd? = null
    private var loadedAt: Long = 0L
    private var isShowingAppOpen = false
    private var currentActivity: Activity? = null
    private var isFirstForeground = true

    /** Timestamp (ms) of the last App Open ad actually shown — frequency cap. */
    private var lastShownAt: Long = 0L

    /** Pending cold-start show, fired once the in-flight load settles. */
    private var pendingColdStart: (() -> Unit)? = null

    private val retry = AdRetryScheduler(
        tag         = "appopen_preloader",
        maxRetries  = config.maxRetries,
        baseDelayMs = config.retryBaseDelayMs,
        maxDelayMs  = config.retryMaxDelayMs,
    )

    // ── Public ───────────────────────────────────────────────────────

    /** Call once from [SmartAdManager.init]. */
    fun start() {
        application.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        loadAd()
    }

    /** Called when Remote Config (re)loads — attempt a load if we hold none. */
    fun onConfigLoaded() {
        retry.reset()
        if (appOpenAd == null) loadAd()
        // A cold-start request may be waiting on a unit that only just became
        // available — kick the load above and let onAdLoaded fire the pending show.
    }

    /**
     * Cold-start show: show the App Open ad on app launch (over the given
     * [activity], typically the splash) before proceeding. Shows immediately if
     * an ad is ready; otherwise waits up to [timeoutMs] for the in-flight load,
     * then shows it or gives up. [onComplete] always runs exactly once — on ad
     * dismiss, on timeout, or when ads are unavailable — so the caller can
     * continue (e.g. navigate to the home screen).
     *
     * Unlike the automatic background→foreground path, this does NOT skip the
     * first foreground; it is the opt-in launch trigger the host calls explicitly.
     */
    fun showOnColdStart(
        activity: Activity,
        timeoutMs: Long = 4_000L,
        onComplete: () -> Unit,
    ) {
        if (!adsAllowed()) {
            AdLog.d("appopen_preloader", "cold-start skipped — ads disabled")
            onComplete(); return
        }
        if (isShowingAppOpen || FullScreenAdState.isShowing || FullScreenAdState.closedRecently()) {
            onComplete(); return
        }
        if (frequencyCapActive()) {
            AdLog.d("appopen_preloader", "cold-start skipped — within frequency cap")
            onComplete(); return
        }
        // Ready now — show straight away.
        if (appOpenAd != null && isAdFresh()) {
            present(activity, onComplete); return
        }
        // Not ready — ensure a load is running and wait for it (bounded).
        AdLog.d("appopen_preloader", "cold-start waiting up to ${timeoutMs}ms for load")
        loadAd()
        val handler = Handler(Looper.getMainLooper())
        var done = false
        val timeout = Runnable {
            if (!done) {
                done = true
                pendingColdStart = null
                AdLog.d("appopen_preloader", "cold-start timed out — proceeding")
                onComplete()
            }
        }
        pendingColdStart = {
            if (!done) {
                done = true
                handler.removeCallbacks(timeout)
                if (appOpenAd != null && isAdFresh()) present(activity, onComplete) else onComplete()
            }
        }
        handler.postDelayed(timeout, timeoutMs)
    }

    fun destroy() {
        retry.cancel()
        application.unregisterActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
        appOpenAd?.fullScreenContentCallback = null
        appOpenAd = null
        currentActivity = null
    }

    // ── App foreground (real background → foreground only) ────────────

    override fun onStart(owner: LifecycleOwner) {
        // Skip the very first foreground (cold start) — showing an App Open
        // on launch competes with the splash/first screen and is rarely
        // ready in time anyway.
        // Re-arm retry on every real foreground so a previously exhausted
        // backoff sequence can try loading again.
        retry.reset()
        if (isFirstForeground) {
            isFirstForeground = false
            return
        }
        showIfAvailable()
    }

    // ── Track the visible activity (needed to call ad.show) ──────────

    override fun onActivityResumed(activity: Activity) {
        if (!isShowingAppOpen) currentActivity = activity
    }
    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivity == activity) currentActivity = null
    }
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityCreated(activity: Activity, b: Bundle?) {}
    override fun onActivitySaveInstanceState(activity: Activity, b: Bundle) {}

    // ── Internal ─────────────────────────────────────────────────────

    private fun isAdFresh(): Boolean {
        val fourHoursMs = 4 * 60 * 60 * 1_000L
        return System.currentTimeMillis() - loadedAt < fourHoursMs
    }

    /** Remote Config kill-switch — fail-open until RC has actually loaded. */
    private fun adsAllowed(): Boolean =
        !AdsConfigRepository.isLoaded || AdsConfigRepository.adsEnabled

    /** True while inside the App Open frequency-cap window since the last show. */
    private fun frequencyCapActive(): Boolean {
        val capMs = config.appOpenMinIntervalSec * 1_000L
        return capMs > 0L && System.currentTimeMillis() - lastShownAt < capMs
    }

    private fun showIfAvailable() {
        // GUARD 0: Remote Config disabled ads entirely.
        if (!adsAllowed()) {
            AdLog.d("appopen_preloader", "skipped — ads disabled in Remote Config")
            return
        }
        // GUARD 1: never stack on another full-screen ad, or show right
        // after one was dismissed (the re-foreground race).
        if (FullScreenAdState.isShowing || FullScreenAdState.closedRecently()) {
            AdLog.d("appopen_preloader", "skipped — another full-screen ad active")
            return
        }
        // GUARD 2: frequency cap — don't show two App Open ads too close together.
        if (frequencyCapActive()) {
            AdLog.d("appopen_preloader", "skipped — within frequency cap")
            return
        }

        val ad = appOpenAd
        if (ad == null) {
            loadAd()
            return
        }
        if (!isAdFresh()) {
            AdLog.d("appopen_preloader", "ad expired — reloading")
            appOpenAd = null
            loadAd()
            return
        }
        val activity = currentActivity ?: return
        if (isShowingAppOpen) return

        present(activity) {}
    }

    /**
     * Wires the full-screen callbacks and shows [appOpenAd] on [activity].
     * [onComplete] runs once the ad is dismissed or fails to show.
     * Caller must guarantee a fresh ad is held.
     */
    private fun present(activity: Activity, onComplete: () -> Unit) {
        val ad = appOpenAd
        if (ad == null) { onComplete(); return }

        isShowingAppOpen = true
        FullScreenAdState.onShown()
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                AdLog.d("appopen_preloader", "shown")
                lastShownAt = System.currentTimeMillis()
                AdEvents.onAppOpenShown?.invoke()
                AdEvents.onAdShown?.invoke(AdNetwork.ADMOB, AdFormat.APP_OPEN)
            }
            override fun onAdClicked() {
                AdEvents.onAdClicked?.invoke(AdNetwork.ADMOB, AdFormat.APP_OPEN)
            }
            override fun onAdDismissedFullScreenContent() {
                AdLog.d("appopen_preloader", "dismissed — reloading")
                appOpenAd = null
                isShowingAppOpen = false
                FullScreenAdState.onClosed()
                loadAd()
                onComplete()
            }
            override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) {
                AdLog.w("appopen_preloader", "show failed: ${error.message}")
                appOpenAd = null
                isShowingAppOpen = false
                FullScreenAdState.onClosed()
                loadAd()
                onComplete()
            }
        }
        ad.show(activity)
    }

    private fun loadAd() {
        if (!adsAllowed()) {
            AdLog.d("appopen_preloader", "load skipped — ads disabled in Remote Config")
            return
        }
        // Unit id comes entirely from Remote Config (null => disabled / not
        // configured / RC not loaded yet — re-kicked via onConfigLoaded()).
        val unitId = AdsConfigRepository.appOpenUnitId()
        if (unitId.isNullOrBlank()) {
            AdLog.d("appopen_preloader", "no app-open unit configured — skipping")
            return
        }
        AppOpenAd.load(
            application,
            unitId,
            AdRequest.Builder().build(),
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    AdLog.d("appopen_preloader", "loaded")
                    appOpenAd = ad
                    loadedAt = System.currentTimeMillis()
                    retry.reset()
                    // Fulfil a waiting cold-start request, if any.
                    pendingColdStart?.also { pendingColdStart = null; it() }
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    AdLog.w("appopen_preloader", "load failed: ${error.message}")
                    // Release a waiting cold-start request so the caller proceeds.
                    pendingColdStart?.also { pendingColdStart = null; it() }
                    retry.schedule { loadAd() }
                }
            }
        )
    }
}
