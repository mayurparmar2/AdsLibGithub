package com.ads.adslib.admob.interstitial

import com.ads.adslib.core.base.BaseAdManager
import com.ads.adslib.core.base.NetworkAdLoader
import com.ads.adslib.core.model.AdNetwork
import com.ads.adslib.core.model.AdUnitConfig
import com.ads.adslib.core.model.NetworkAdUnit
import com.ads.adslib.ironsource.interstitial.IronSourceInterstitialLoader
import com.ads.adslib.meta.interstitial.MetaInterstitialLoader

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
        AdNetwork.ADMOB -> AdMobInterstitialLoader(unit, fullScreenCallbacks())
        AdNetwork.META  -> MetaInterstitialLoader(unit, fullScreenCallbacks())
        AdNetwork.IRONSOURCE -> IronSourceInterstitialLoader(unit, fullScreenCallbacks())
    }
}
