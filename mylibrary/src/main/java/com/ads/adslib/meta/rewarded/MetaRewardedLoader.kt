package com.ads.adslib.meta.rewarded

import android.app.Activity
import android.content.Context
import com.ads.adslib.core.base.NetworkAdLoader
import com.ads.adslib.core.model.AdError
import com.ads.adslib.core.model.AdLoadState
import com.ads.adslib.core.model.AdNetwork
import com.ads.adslib.core.model.NetworkAdUnit
import com.facebook.ads.Ad
import com.facebook.ads.AdError as FanAdError
import com.facebook.ads.RewardedVideoAd
import com.facebook.ads.RewardedVideoAdListener

class MetaRewardedLoader(
    unit: NetworkAdUnit,
    private val onShownCb: (AdNetwork) -> Unit,
    private val onDismissedCb: (AdNetwork) -> Unit,
    private val onClickedCb: (AdNetwork) -> Unit,
    private val onImpressionCb: (AdNetwork) -> Unit,
    private val onFailedToShowCb: (AdError) -> Unit,
    private val onRewardEarnedCb: (type: String, amount: Int) -> Unit
) : NetworkAdLoader(unit) {

    private var rewardedAd: RewardedVideoAd? = null

    override fun load(context: Context, onLoaded: () -> Unit, onFailed: (AdError) -> Unit) {
        state = AdLoadState.LOADING
        val ad = RewardedVideoAd(context, unit.adUnitId)
        rewardedAd = ad

        val config = ad.buildLoadAdConfig()
            .withAdListener(object : RewardedVideoAdListener {
                override fun onAdLoaded(a: Ad) {
                    state = AdLoadState.LOADED
                    onLoaded()
                }
                override fun onError(a: Ad?, error: FanAdError) {
                    state = AdLoadState.FAILED
                    onFailed(AdError(AdNetwork.META, error.errorCode, error.errorMessage))
                }
                override fun onRewardedVideoCompleted() {
                    // User watched the full video — grant reward
                    // Meta FAN does not provide reward type/amount; use fixed values
                    onRewardEarnedCb("reward", 1)
                }
                override fun onRewardedVideoClosed() {
                    state = AdLoadState.DISMISSED
                    rewardedAd = null
                    onDismissedCb(AdNetwork.META)
                }
                override fun onAdClicked(a: Ad) = onClickedCb(AdNetwork.META)
                override fun onLoggingImpression(a: Ad) {
                    state = AdLoadState.SHOWING
                    onShownCb(AdNetwork.META)
                    onImpressionCb(AdNetwork.META)
                }
            })
            .build()

        ad.loadAd(config)
    }

    override fun show(activity: Activity) {
        val ad = rewardedAd
        if (ad != null && ad.isAdLoaded) {
            ad.show()
        } else {
            onFailedToShowCb(AdError(AdNetwork.META, -3, "Meta rewarded not ready at show()"))
        }
    }

    override fun isReady(): Boolean =
        rewardedAd?.isAdLoaded == true && state == AdLoadState.LOADED

    override fun destroy() {
        rewardedAd?.destroy()
        rewardedAd = null
        state = AdLoadState.IDLE
    }
}
