package com.ads.adslib.meta.rewarded

import android.app.Activity
import android.content.Context
import android.util.Log
import com.ads.adslib.core.base.NetworkAdLoader
import com.ads.adslib.core.callback.FullScreenCallbacks
import com.ads.adslib.core.model.AdError
import com.ads.adslib.core.model.AdLoadState
import com.ads.adslib.core.model.AdNetwork
import com.ads.adslib.core.model.NetworkAdUnit
import com.ads.adslib.util.AdLog
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

        // In debug, force a Meta TEST rewarded ad by prefixing the real placement
        // id with a test-creative type ("<TYPE>#<placementId>"). This serves a
        // guaranteed test ad without needing addTestDevice/test-mode and avoids
        // hammering the live placement. In release, use the real placement id.
        // AdLog.enabled mirrors the `debug` flag passed to AdsSdk.initialize().
        val placementId = if (AdLog.enabled) {
            unit.adUnitId
//            "VID_HD_16_9_46S_APP_INSTALL#YOUR_PLACEMENT_ID"
        } else {
            unit.adUnitId
        }
        Log.d("TAG", "load() returned unit.adUnitId: ${unit.adUnitId}")


        val ad = RewardedInterstitialAd(context, placementId)
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
