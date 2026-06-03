package com.ads.adslib.ironsource.interstitial

import android.app.Activity
import android.content.Context
import com.ads.adslib.AdsSdk
import com.ads.adslib.core.base.NetworkAdLoader
import com.ads.adslib.core.callback.FullScreenCallbacks
import com.ads.adslib.core.model.AdError
import com.ads.adslib.core.model.AdLoadState
import com.ads.adslib.core.model.AdNetwork
import com.ads.adslib.core.model.NetworkAdUnit
import com.unity3d.mediation.LevelPlayAdError
import com.unity3d.mediation.LevelPlayAdInfo
import com.unity3d.mediation.interstitial.LevelPlayInterstitialAd
import com.unity3d.mediation.interstitial.LevelPlayInterstitialAdListener

/**
 * ironSource / Unity LevelPlay interstitial — one waterfall rung.
 *
 * The [unit] adUnitId is a LevelPlay **ad unit id** (multi-ad-unit API), not a
 * legacy placement name. LevelPlay mediates ironSource + Unity demand internally.
 */
class IronSourceInterstitialLoader(
    unit: NetworkAdUnit,
    private val cb: FullScreenCallbacks
) : NetworkAdLoader(unit) {

    private var ad: LevelPlayInterstitialAd? = null

    override fun load(context: Context, onLoaded: () -> Unit, onFailed: (AdError) -> Unit) {
        state = AdLoadState.LOADING
        // Defer until LevelPlay init has actually completed — loading before
        // onInitSuccess fails with "load before init success" on first app launch.
        AdsSdk.whenLevelPlayReady(
            onReady = { startLoad(onLoaded, onFailed) },
            onFailed = {
                state = AdLoadState.FAILED
                onFailed(AdError(AdNetwork.IRONSOURCE, 508, "LevelPlay not initialized"))
            }
        )
    }

    private fun startLoad(onLoaded: () -> Unit, onFailed: (AdError) -> Unit) {
        val interstitial = LevelPlayInterstitialAd(unit.adUnitId)
        ad = interstitial

        interstitial.setListener(object : LevelPlayInterstitialAdListener {
            override fun onAdLoaded(adInfo: LevelPlayAdInfo) {
                state = AdLoadState.LOADED
                onLoaded()
            }

            override fun onAdLoadFailed(error: LevelPlayAdError) {
                state = AdLoadState.FAILED
                onFailed(AdError(AdNetwork.IRONSOURCE, error.errorCode, error.errorMessage))
            }

            override fun onAdDisplayed(adInfo: LevelPlayAdInfo) = cb.onShown(AdNetwork.IRONSOURCE)

            override fun onAdDisplayFailed(error: LevelPlayAdError, adInfo: LevelPlayAdInfo) {
                state = AdLoadState.FAILED
                cb.onFailedToShow(AdError(AdNetwork.IRONSOURCE, error.errorCode, error.errorMessage))
            }

            override fun onAdClicked(adInfo: LevelPlayAdInfo) = cb.onClicked(AdNetwork.IRONSOURCE)

            override fun onAdClosed(adInfo: LevelPlayAdInfo) {
                state = AdLoadState.DISMISSED
                cb.onDismissed(AdNetwork.IRONSOURCE)
            }
        })

        interstitial.loadAd()
    }

    override fun show(activity: Activity) {
        state = AdLoadState.SHOWING
        ad?.showAd(activity)
    }

    override fun isReady(): Boolean = ad?.isAdReady() == true && state == AdLoadState.LOADED

    override fun destroy() {
        ad = null
        state = AdLoadState.IDLE
    }
}
