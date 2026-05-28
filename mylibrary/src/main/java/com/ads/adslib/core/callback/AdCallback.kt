package com.ads.adslib.core.callback

import com.ads.adslib.core.model.AdError
import com.ads.adslib.core.model.AdNetwork

/**
 * Base lifecycle callbacks common to all full-screen / loadable ads.
 * All callbacks are invoked on the main thread.
 */
interface AdCallback {
    /** Called when an ad loads successfully from [network]. */
    fun onLoaded(network: AdNetwork) {}

    /** Called when the entire waterfall has been exhausted without a successful load. */
    fun onFailedToLoad(error: AdError) {}

    /** Called when a full-screen ad is shown. */
    fun onShown(network: AdNetwork) {}

    /** Called when a full-screen ad is dismissed and control returns to the app. */
    fun onDismissed(network: AdNetwork) {}

    /** Called when showing the ad fails after a successful load. */
    fun onFailedToShow(error: AdError) {}

    /** Called when the ad is clicked. */
    fun onClicked(network: AdNetwork) {}

    /** Called when an impression is recorded. */
    fun onImpression(network: AdNetwork) {}
}

/**
 * Reward-specific callback. Implemented in addition to the standard show flow.
 */
interface RewardCallback : AdCallback {
    /**
     * Called when the user earns a reward.
     * @param type reward currency/name as defined in the ad unit.
     * @param amount reward amount.
     */
    fun onRewardEarned(type: String, amount: Int)
}
