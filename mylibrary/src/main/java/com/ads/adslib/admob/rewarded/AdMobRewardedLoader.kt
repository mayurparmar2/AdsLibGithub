package com.ads.adslib.admob.rewarded

import android.app.Activity
import android.content.Context
import com.ads.adslib.core.base.NetworkAdLoader
import com.ads.adslib.core.callback.FullScreenCallbacks
import com.ads.adslib.core.model.AdError
import com.ads.adslib.core.model.AdLoadState
import com.ads.adslib.core.model.AdNetwork
import com.ads.adslib.core.model.NetworkAdUnit
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class AdMobRewardedLoader(
    unit: NetworkAdUnit,
    private val cb: FullScreenCallbacks
) : NetworkAdLoader(unit) {

    private var ad: RewardedAd? = null

    override fun load(context: Context, onLoaded: () -> Unit, onFailed: (AdError) -> Unit) {
        state = AdLoadState.LOADING
        RewardedAd.load(
            context,
            unit.adUnitId,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(loaded: RewardedAd) {
                    ad = loaded
                    state = AdLoadState.LOADED
                    attachFullScreenCallbacks()
                    onLoaded()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    ad = null
                    state = AdLoadState.FAILED
                    onFailed(AdError(AdNetwork.ADMOB, error.code, error.message))
                }
            }
        )
    }

    private fun attachFullScreenCallbacks() {
        ad?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                state = AdLoadState.SHOWING
                cb.onShown(AdNetwork.ADMOB)
            }

            override fun onAdDismissedFullScreenContent() {
                state = AdLoadState.DISMISSED
                ad = null
                cb.onDismissed(AdNetwork.ADMOB)
            }

            override fun onAdClicked() = cb.onClicked(AdNetwork.ADMOB)
            override fun onAdImpression() = cb.onImpression(AdNetwork.ADMOB)

            override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) {
                state = AdLoadState.FAILED
                ad = null
                cb.onFailedToShow(AdError(AdNetwork.ADMOB, error.code, error.message))
            }
        }
    }

    override fun show(activity: Activity) {
        val rewardedAd = ad ?: run {
            cb.onFailedToShow(AdError(AdNetwork.ADMOB, -3, "Rewarded ad not ready at show()"))
            return
        }
        // RewardItem: type = currency name (e.g. "coins"), amount = reward count
        rewardedAd.show(activity) { rewardItem ->
            cb.onRewardEarned(rewardItem.type, rewardItem.amount)
        }
    }

    override fun isReady(): Boolean = ad != null && state == AdLoadState.LOADED

    override fun destroy() {
        ad?.fullScreenContentCallback = null
        ad = null
        state = AdLoadState.IDLE
    }
}
