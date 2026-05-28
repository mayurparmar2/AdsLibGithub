package com.ads.adslib.unity.rewarded

import android.app.Activity
import android.content.Context
import com.ads.adslib.core.base.NetworkAdLoader
import com.ads.adslib.core.model.AdError
import com.ads.adslib.core.model.AdLoadState
import com.ads.adslib.core.model.AdNetwork
import com.ads.adslib.core.model.NetworkAdUnit
import com.unity3d.ads.IUnityAdsLoadListener
import com.unity3d.ads.IUnityAdsShowListener
import com.unity3d.ads.UnityAds
import com.unity3d.ads.UnityAdsShowOptions

class UnityRewardedLoader(
    unit: NetworkAdUnit,
    private val onShownCb: (AdNetwork) -> Unit,
    private val onDismissedCb: (AdNetwork) -> Unit,
    private val onClickedCb: (AdNetwork) -> Unit,
    private val onFailedToShowCb: (AdError) -> Unit,
    private val onRewardEarnedCb: (type: String, amount: Int) -> Unit
) : NetworkAdLoader(unit) {

    override fun load(context: Context, onLoaded: () -> Unit, onFailed: (AdError) -> Unit) {
        state = AdLoadState.LOADING
        UnityAds.load(unit.adUnitId, object : IUnityAdsLoadListener {
            override fun onUnityAdsAdLoaded(placementId: String) {
                state = AdLoadState.LOADED
                onLoaded()
            }
            override fun onUnityAdsFailedToLoad(
                placementId: String,
                error: UnityAds.UnityAdsLoadError,
                message: String
            ) {
                state = AdLoadState.FAILED
                onFailed(AdError(AdNetwork.UNITY, error.ordinal, message))
            }
        })
    }

    override fun show(activity: Activity) {
        state = AdLoadState.SHOWING
        UnityAds.show(activity, unit.adUnitId, UnityAdsShowOptions(),
            object : IUnityAdsShowListener {
                override fun onUnityAdsShowStart(placementId: String) =
                    onShownCb(AdNetwork.UNITY)

                override fun onUnityAdsShowComplete(
                    placementId: String,
                    completionState: UnityAds.UnityAdsShowCompletionState
                ) {
                    state = AdLoadState.DISMISSED
                    // COMPLETED = user watched full video → reward earned
                    // SKIPPED   = user skipped       → no reward
                    if (completionState == UnityAds.UnityAdsShowCompletionState.COMPLETED) {
                        onRewardEarnedCb("reward", 1)
                    }
                    onDismissedCb(AdNetwork.UNITY)
                }

                override fun onUnityAdsShowFailure(
                    placementId: String,
                    error: UnityAds.UnityAdsShowError,
                    message: String
                ) {
                    state = AdLoadState.FAILED
                    onFailedToShowCb(AdError(AdNetwork.UNITY, error.ordinal, message))
                }

                override fun onUnityAdsShowClick(placementId: String) =
                    onClickedCb(AdNetwork.UNITY)
            }
        )
    }

    override fun isReady(): Boolean = state == AdLoadState.LOADED

    override fun destroy() {
        state = AdLoadState.IDLE
    }
}
