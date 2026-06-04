package com.ads.adslib.meta.rewarded

import android.app.Activity
import android.content.Context
import com.ads.adslib.core.base.NetworkAdLoader
import com.ads.adslib.core.callback.FullScreenCallbacks
import com.ads.adslib.core.model.AdError
import com.ads.adslib.core.model.AdLoadState
import com.ads.adslib.core.model.AdNetwork
import com.ads.adslib.core.model.NetworkAdUnit
import com.facebook.ads.Ad
import com.facebook.ads.AdError as FanAdError
import com.facebook.ads.RewardedInterstitialAd
import com.facebook.ads.RewardedInterstitialAdListener

/**
 * Meta (Audience Network) rewarded loader.
 *
 * Uses Meta's **Rewarded Interstitial** format (`RewardedInterstitialAd`) — the
 * placement in Monetisation Manager must be created with format
 * "Rewarded interstitial". (For the older "Rewarded video" placements use
 * `RewardedVideoAd` instead.)
 */
class MetaRewardedLoader(
    unit: NetworkAdUnit,
    private val cb: FullScreenCallbacks
) : NetworkAdLoader(unit) {

    private var rewardedAd: RewardedInterstitialAd? = null

    override fun load(context: Context, onLoaded: () -> Unit, onFailed: (AdError) -> Unit) {
        state = AdLoadState.LOADING

        val ad = RewardedInterstitialAd(context, unit.adUnitId)
        rewardedAd = ad

        val config = ad.buildLoadAdConfig()
            .withAdListener(object : RewardedInterstitialAdListener {
                override fun onAdLoaded(a: Ad) {
                    state = AdLoadState.LOADED
                    onLoaded()
                }
                override fun onError(a: Ad?, error: FanAdError) {
                    state = AdLoadState.FAILED
                    onFailed(AdError(AdNetwork.META, error.errorCode, error.errorMessage))
                }
                override fun onRewardedInterstitialCompleted() {
                    // User watched fully — grant reward. Meta FAN does not provide
                    // reward type/amount; use fixed values.
                    cb.onRewardEarned("reward", 1)
                }
                override fun onRewardedInterstitialClosed() {
                    state = AdLoadState.DISMISSED
                    rewardedAd = null
                    cb.onDismissed(AdNetwork.META)
                }
                override fun onAdClicked(a: Ad) = cb.onClicked(AdNetwork.META)
                override fun onLoggingImpression(a: Ad) {
                    state = AdLoadState.SHOWING
                    cb.onShown(AdNetwork.META)
                    cb.onImpression(AdNetwork.META)
                }
            })
            .build()

        ad.loadAd(config)
    }

    override fun show(activity: Activity) {
        val ad = rewardedAd
        if (ad != null && ad.isAdLoaded && !ad.isAdInvalidated) {
            ad.show()
        } else {
            cb.onFailedToShow(AdError(AdNetwork.META, -3, "Meta rewarded not ready at show()"))
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
