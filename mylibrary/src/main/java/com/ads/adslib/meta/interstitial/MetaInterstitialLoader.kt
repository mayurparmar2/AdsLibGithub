package com.ads.adslib.meta.interstitial

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
import com.facebook.ads.InterstitialAd as FanInterstitialAd
import com.facebook.ads.InterstitialAdListener

class MetaInterstitialLoader(
    unit: NetworkAdUnit,
    private val cb: FullScreenCallbacks
) : NetworkAdLoader(unit) {

    private var interstitialAd: FanInterstitialAd? = null

    override fun load(context: Context, onLoaded: () -> Unit, onFailed: (AdError) -> Unit) {
        state = AdLoadState.LOADING
        val ad = FanInterstitialAd(context, unit.adUnitId)
        interstitialAd = ad

        val config = ad.buildLoadAdConfig()
            .withAdListener(object : InterstitialAdListener {
                override fun onAdLoaded(a: Ad) {
                    state = AdLoadState.LOADED
                    onLoaded()
                }
                override fun onError(a: Ad?, error: FanAdError) {
                    state = AdLoadState.FAILED
                    onFailed(AdError(AdNetwork.META, error.errorCode, error.errorMessage))
                }
                override fun onInterstitialDisplayed(a: Ad) {
                    state = AdLoadState.SHOWING
                    cb.onShown(AdNetwork.META)
                }
                override fun onInterstitialDismissed(a: Ad) {
                    state = AdLoadState.DISMISSED
                    interstitialAd = null
                    cb.onDismissed(AdNetwork.META)
                }
                override fun onAdClicked(a: Ad) = cb.onClicked(AdNetwork.META)
                override fun onLoggingImpression(a: Ad) = cb.onImpression(AdNetwork.META)
            })
            .build()

        ad.loadAd(config)
    }

    override fun show(activity: Activity) {
        val ad = interstitialAd
        if (ad != null && ad.isAdLoaded) {
            ad.show()
        } else {
            cb.onFailedToShow(AdError(AdNetwork.META, -3, "Meta interstitial not ready at show()"))
        }
    }

    override fun isReady(): Boolean =
        interstitialAd?.isAdLoaded == true && state == AdLoadState.LOADED

    override fun destroy() {
        interstitialAd?.destroy()
        interstitialAd = null
        state = AdLoadState.IDLE
    }
}
