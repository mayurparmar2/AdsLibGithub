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

    private var rewardCallback: RewardCallback? = null

    /**
     * Load with a [RewardCallback]. [RewardCallback] extends [AdCallback] so all base
     * lifecycle events (onLoaded, onShown, onDismissed, onFailedToLoad) fire normally.
     */
    fun load(context: Context, callback: RewardCallback?) {
        rewardCallback = callback
        super.load(context, callback)   // RewardCallback IS-A AdCallback ✓
    }

    override fun createLoader(unit: NetworkAdUnit): NetworkAdLoader? = when (unit.network) {
        AdNetwork.ADMOB -> AdMobRewardedLoader(
            unit             = unit,
            onShownCb        = { net -> dispatch { onShown(net) } },
            onDismissedCb    = { net -> dispatch { onDismissed(net) } },
            onClickedCb      = { net -> dispatch { onClicked(net) } },
            onImpressionCb   = { net -> dispatch { onImpression(net) } },
            onFailedToShowCb = { err -> dispatch { onFailedToShow(err) } },
            onRewardEarnedCb = { type, amount ->
                main.post { rewardCallback?.onRewardEarned(type, amount) }
            }
        )
        AdNetwork.META -> MetaRewardedLoader(
            unit             = unit,
            onShownCb        = { net -> dispatch { onShown(net) } },
            onDismissedCb    = { net -> dispatch { onDismissed(net) } },
            onClickedCb      = { net -> dispatch { onClicked(net) } },
            onImpressionCb   = { net -> dispatch { onImpression(net) } },
            onFailedToShowCb = { err -> dispatch { onFailedToShow(err) } },
            onRewardEarnedCb = { type, amount ->
                main.post { rewardCallback?.onRewardEarned(type, amount) }
            }
        )
        AdNetwork.UNITY -> UnityRewardedLoader(
            unit             = unit,
            onShownCb        = { net -> dispatch { onShown(net) } },
            onDismissedCb    = { net -> dispatch { onDismissed(net) } },
            onClickedCb      = { net -> dispatch { onClicked(net) } },
            onFailedToShowCb = { err -> dispatch { onFailedToShow(err) } },
            onRewardEarnedCb = { type, amount ->
                main.post { rewardCallback?.onRewardEarned(type, amount) }
            }
        )
        else -> null
    }

    override fun destroy() {
        rewardCallback = null
        super.destroy()
    }
}
