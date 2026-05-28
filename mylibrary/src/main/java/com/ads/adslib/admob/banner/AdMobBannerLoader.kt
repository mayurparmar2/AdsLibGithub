package com.ads.adslib.admob.banner

import android.app.Activity
import android.content.Context
import android.view.View
import com.ads.adslib.core.base.NetworkAdLoader
import com.ads.adslib.core.model.AdError
import com.ads.adslib.core.model.AdLoadState
import com.ads.adslib.core.model.AdNetwork
import com.ads.adslib.core.model.BannerAdSize
import com.ads.adslib.core.model.NetworkAdUnit
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

class AdMobBannerLoader(
    unit: NetworkAdUnit,
    private val bannerSize: BannerAdSize,
    private val onClickedCb: (AdNetwork) -> Unit,
    private val onImpressionCb: (AdNetwork) -> Unit
) : NetworkAdLoader(unit) {

    private var adView: AdView? = null

    override fun load(context: Context, onLoaded: () -> Unit, onFailed: (AdError) -> Unit) {
        state = AdLoadState.LOADING
        adView = AdView(context).apply {
            setAdSize(bannerSize.toAdMobSize(context))
            adUnitId = unit.adUnitId
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    state = AdLoadState.LOADED
                    onLoaded()
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    state = AdLoadState.FAILED
                    onFailed(AdError(AdNetwork.ADMOB, error.code, error.message))
                }
                override fun onAdClicked() = onClickedCb(AdNetwork.ADMOB)
                override fun onAdImpression() = onImpressionCb(AdNetwork.ADMOB)
            }
            loadAd(AdRequest.Builder().build())
        }
    }

    override fun show(activity: Activity) {} // no-op — banner is a View, not full-screen

    override fun getView(): View? = adView

    override fun isReady(): Boolean = adView != null && state == AdLoadState.LOADED

    override fun pause()   { adView?.pause() }
    override fun resume()  { adView?.resume() }

    override fun destroy() {
        adView?.destroy()
        adView = null
        state = AdLoadState.IDLE
    }
}

// ----- BannerAdSize → AdMob AdSize conversion -----
private fun BannerAdSize.toAdMobSize(context: Context): AdSize = when (this) {
    BannerAdSize.BANNER           -> AdSize.BANNER
    BannerAdSize.LARGE_BANNER     -> AdSize.LARGE_BANNER
    BannerAdSize.MEDIUM_RECTANGLE -> AdSize.MEDIUM_RECTANGLE
    BannerAdSize.ADAPTIVE         -> {
        val displayMetrics = context.resources.displayMetrics
        val widthDp = (displayMetrics.widthPixels / displayMetrics.density).toInt()
        AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, widthDp)
    }
}
