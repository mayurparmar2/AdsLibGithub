package com.ads.adslib.ironsource.rewarded

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
import com.unity3d.mediation.rewarded.LevelPlayReward
import com.unity3d.mediation.rewarded.LevelPlayRewardedAd
import com.unity3d.mediation.rewarded.LevelPlayRewardedAdListener

/**
 * ironSource / Unity LevelPlay rewarded — one waterfall rung.
 *
 * [FullScreenCallbacks.onRewardEarned] fires from LevelPlay's [onAdRewarded].
 * Note: onAdRewarded/onAdClosed are async and may arrive in either order, so the
 * reward is delivered independently of dismissal (matching LevelPlay guidance).
 */
class IronSourceRewardedLoader(
    unit: NetworkAdUnit,
    private val cb: FullScreenCallbacks
) : NetworkAdLoader(unit) {

    private var ad: LevelPlayRewardedAd? = null

    override fun load(context: Context, onLoaded: () -> Unit, onFailed: (AdError) -> Unit) {
        state = AdLoadState.LOADING
        // Defer until LevelPlay init has completed (see IronSourceInterstitialLoader).
        AdsSdk.whenLevelPlayReady(
            onReady = { startLoad(onLoaded, onFailed) },
            onFailed = {
                state = AdLoadState.FAILED
                onFailed(AdError(AdNetwork.IRONSOURCE, 508, "LevelPlay not initialized"))
            }
        )
    }

    private fun startLoad(onLoaded: () -> Unit, onFailed: (AdError) -> Unit) {
        val rewarded = LevelPlayRewardedAd(unit.adUnitId)
        ad = rewarded

        rewarded.setListener(object : LevelPlayRewardedAdListener {
            override fun onAdLoaded(adInfo: LevelPlayAdInfo) {
                state = AdLoadState.LOADED
                onLoaded()
            }

            override fun onAdLoadFailed(error: LevelPlayAdError) {
                state = AdLoadState.FAILED
                onFailed(AdError(AdNetwork.IRONSOURCE, error.errorCode, error.errorMessage))
            }

            override fun onAdDisplayed(adInfo: LevelPlayAdInfo) = cb.onShown(AdNetwork.IRONSOURCE)

            override fun onAdRewarded(reward: LevelPlayReward, adInfo: LevelPlayAdInfo) {
                cb.onRewardEarned(reward.name, reward.amount)
            }

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

        rewarded.loadAd()
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
