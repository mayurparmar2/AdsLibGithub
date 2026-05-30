package com.ads.adslib.smart

/**
 * Process-wide flag tracking whether ANY full-screen ad (interstitial,
 * rewarded, or app-open) is currently on screen.
 *
 * The [AppOpenAdPreloader] checks this before showing so an App Open ad
 * never stacks on top of an interstitial / rewarded ad (and vice-versa).
 *
 * Set on [com.ads.adslib.core.callback.AdCallback.onShown] and cleared on
 * dismiss / failed-to-show.
 */
internal object FullScreenAdState {

    @Volatile
    var isShowing: Boolean = false

    /** Timestamp (ms) of the last full-screen ad dismissal. */
    @Volatile
    var lastDismissedAt: Long = 0L

    fun onShown() {
        isShowing = true
    }

    fun onClosed() {
        isShowing = false
        lastDismissedAt = System.currentTimeMillis()
    }

    /** True if a full-screen ad closed within [windowMs] (guards the re-foreground race). */
    fun closedRecently(windowMs: Long = 1_000L): Boolean =
        System.currentTimeMillis() - lastDismissedAt < windowMs
}
