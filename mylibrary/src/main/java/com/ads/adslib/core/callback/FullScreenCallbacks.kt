package com.ads.adslib.core.callback

import com.ads.adslib.core.model.AdError
import com.ads.adslib.core.model.AdNetwork

/**
 * Bundle of show-time callbacks a full-screen [com.ads.adslib.core.base.NetworkAdLoader]
 * (interstitial / rewarded) reports back to its manager.
 *
 * Replaces the previous 5–6 individual lambda constructor parameters that were
 * duplicated across every AdMob / Meta / Unity full-screen loader. Banner/native
 * loaders don't use this — they expose a View instead.
 *
 * All members default to no-ops so a loader (e.g. Unity, which has no impression
 * callback) can ignore the ones it doesn't support.
 */
class FullScreenCallbacks(
    val onShown: (AdNetwork) -> Unit = {},
    val onDismissed: (AdNetwork) -> Unit = {},
    val onClicked: (AdNetwork) -> Unit = {},
    val onImpression: (AdNetwork) -> Unit = {},
    val onFailedToShow: (AdError) -> Unit = {},
    val onRewardEarned: (type: String, amount: Int) -> Unit = { _, _ -> },
)
