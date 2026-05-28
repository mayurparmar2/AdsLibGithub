package com.ads.adslib.admob.rewarded

import android.app.Activity
import android.content.Context
import com.ads.adslib.core.base.NetworkAdLoader
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
    private val onShownCb: (AdNetwork) -> Unit,
    private val onDismissedCb: (AdNetwork) -> Unit,
    private val onClickedCb: (AdNetwork) -> Unit,
    private val onImpressionCb: (AdNetwork) -> Unit,
    private val onFailedToShowCb: (AdError) -> Unit,
    private val onRewardEarnedCb: (type: String, amount: Int) -> Unit
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
                onShownCb(AdNetwork.ADMOB)
            }

            override fun onAdDismissedFullScreenContent() {
                state = AdLoadState.DISMISSED
                ad = null
                onDismissedCb(AdNetwork.ADMOB)
            }

            override fun onAdClicked() = onClickedCb(AdNetwork.ADMOB)
            override fun onAdImpression() = onImpressionCb(AdNetwork.ADMOB)

            override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) {
                state = AdLoadState.FAILED
                ad = null
                onFailedToShowCb(AdError(AdNetwork.ADMOB, error.code, error.message))
            }
        }
    }

    override fun show(activity: Activity) {
        val rewardedAd = ad ?: run {
            onFailedToShowCb(AdError(AdNetwork.ADMOB, -3, "Rewarded ad not ready at show()"))
            return
        }
        // RewardItem: type = currency name (e.g. "coins"), amount = reward count
        rewardedAd.show(activity) { rewardItem ->
            onRewardEarnedCb(rewardItem.type, rewardItem.amount)
        }
    }

    override fun isReady(): Boolean = ad != null && state == AdLoadState.LOADED

    override fun destroy() {
        ad?.fullScreenContentCallback = null
        ad = null
        state = AdLoadState.IDLE
    }
}
