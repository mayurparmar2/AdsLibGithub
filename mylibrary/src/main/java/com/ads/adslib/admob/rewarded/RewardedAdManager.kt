package com.ads.adslib.admob.rewarded

import android.content.Context
import com.ads.adslib.core.base.BaseAdManager
import com.ads.adslib.core.base.NetworkAdLoader
import com.ads.adslib.core.callback.RewardCallback
import com.ads.adslib.core.model.AdNetwork
import com.ads.adslib.core.model.AdUnitConfig
import com.ads.adslib.core.model.NetworkAdUnit
import com.ads.adslib.meta.rewarded.MetaRewardedLoader
import com.ads.adslib.unity.rewarded.UnityRewardedLoader

/**
 * Waterfall manager for rewarded ad placements.
 *
 * Identical flow to [com.ads.adslib.admob.interstitial.InterstitialAdManager] except
 * [load] accepts a [RewardCallback] so [RewardCallback.onRewardEarned] fires when the
 * user completes the rewarded video.
 *
 * Usage:
 * ```
 * val config = AdUnitConfig(
 *     placementKey = "level_complete_reward",
 *     format       = AdFormat.REWARDED,
 *     waterfall    = listOf(
 *         NetworkAdUnit(AdNetwork.ADMOB, "ca-app-pub-xxx/rewarded_id"),
 *         // NetworkAdUnit(AdNetwork.META,  "META_PLACEMENT"),   // future
 *         // NetworkAdUnit(AdNetwork.UNITY, "UNITY_PLACEMENT"),  // future
 *     )
 * )
 * val mgr = RewardedAdManager(config)
 * mgr.load(context, object : RewardCallback {
 *     override fun onLoaded(network: AdNetwork) { /* ready to show */ }
 *     override fun onRewardEarned(type: String, amount: Int) { /* give reward */ }
 *     override fun onDismissed(network: AdNetwork) { mgr.destroy(); mgr.load(...) }
 * })
 * if (mgr.isReady()) mgr.show(activity)
 * mgr.destroy() // in onDestroy
 * ```
 */
class RewardedAdManager(config: AdUnitConfig) : BaseAdManager(config) {

    /**
     * Load with a [RewardCallback]. It extends AdCallback so all base lifecycle
     * events (onLoaded, onShown, onDismissed, onFailedToLoad) fire normally, and
     * [RewardCallback.onRewardEarned] is delivered via [fullScreenCallbacks].
     */
    fun load(context: Context, callback: RewardCallback?) {
        super.load(context, callback)   // RewardCallback IS-A AdCallback ✓
    }

    override fun createLoader(unit: NetworkAdUnit): NetworkAdLoader? = when (unit.network) {
        AdNetwork.ADMOB -> AdMobRewardedLoader(unit, fullScreenCallbacks())
        AdNetwork.META  -> MetaRewardedLoader(unit, fullScreenCallbacks())
        AdNetwork.UNITY -> UnityRewardedLoader(unit, fullScreenCallbacks())
    }
}
