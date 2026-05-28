package com.ads.adslib.admob.interstitial

import com.ads.adslib.core.base.BaseAdManager
import com.ads.adslib.core.base.NetworkAdLoader
import com.ads.adslib.core.model.AdNetwork
import com.ads.adslib.core.model.AdUnitConfig
import com.ads.adslib.core.model.NetworkAdUnit
import com.ads.adslib.meta.interstitial.MetaInterstitialLoader
import com.ads.adslib.unity.interstitial.UnityInterstitialLoader

/**
 * Public manager for interstitial placements. Builds AdMob loaders for now;
 * extend [createLoader] with Meta/Unity branches when those modules are added.
 *
 * Usage:
 * ```
 * val mgr = InterstitialAdManager(config)
 * mgr.load(context, callback)
 * // later, on a user action:
 * if (mgr.isReady()) mgr.show(activity)
 * ```
 */
class InterstitialAdManager(config: AdUnitConfig) : BaseAdManager(config) {

    override fun createLoader(unit: NetworkAdUnit): NetworkAdLoader? = when (unit.network) {
        AdNetwork.ADMOB -> AdMobInterstitialLoader(
            unit             = unit,
            onShownCb        = { net -> dispatch { onShown(net) } },
            onDismissedCb    = { net -> dispatch { onDismissed(net) } },
            onClickedCb      = { net -> dispatch { onClicked(net) } },
            onImpressionCb   = { net -> dispatch { onImpression(net) } },
            onFailedToShowCb = { err -> dispatch { onFailedToShow(err) } }
        )
        AdNetwork.META -> MetaInterstitialLoader(
            unit             = unit,
            onShownCb        = { net -> dispatch { onShown(net) } },
            onDismissedCb    = { net -> dispatch { onDismissed(net) } },
            onClickedCb      = { net -> dispatch { onClicked(net) } },
            onImpressionCb   = { net -> dispatch { onImpression(net) } },
            onFailedToShowCb = { err -> dispatch { onFailedToShow(err) } }
        )
        AdNetwork.UNITY -> UnityInterstitialLoader(
            unit             = unit,
            onShownCb        = { net -> dispatch { onShown(net) } },
            onDismissedCb    = { net -> dispatch { onDismissed(net) } },
            onClickedCb      = { net -> dispatch { onClicked(net) } },
            onFailedToShowCb = { err -> dispatch { onFailedToShow(err) } }
        )
    }
}
