package com.ads.adslib.meta.banner

import android.app.Activity
import android.content.Context
import android.view.View
import com.ads.adslib.core.base.NetworkAdLoader
import com.ads.adslib.core.model.AdError
import com.ads.adslib.core.model.AdLoadState
import com.ads.adslib.core.model.AdNetwork
import com.ads.adslib.core.model.BannerAdSize
import com.ads.adslib.core.model.NetworkAdUnit
import com.facebook.ads.Ad
import com.facebook.ads.AdListener
import com.facebook.ads.AdSize as FanAdSize
import com.facebook.ads.AdView as FanAdView

class MetaBannerLoader(
    unit: NetworkAdUnit,
    private val bannerSize: BannerAdSize,
    private val onClickedCb: (AdNetwork) -> Unit,
    private val onImpressionCb: (AdNetwork) -> Unit
) : NetworkAdLoader(unit) {

    private var adView: FanAdView? = null

    override fun load(context: Context, onLoaded: () -> Unit, onFailed: (AdError) -> Unit) {
        state = AdLoadState.LOADING
        val fanView = FanAdView(context, unit.adUnitId, bannerSize.toFanSize())
        adView = fanView

        val config = fanView.buildLoadAdConfig()
            .withAdListener(object : AdListener {
                override fun onAdLoaded(ad: Ad) {
                    state = AdLoadState.LOADED
                    onLoaded()
                }
                override fun onError(ad: Ad?, error: com.facebook.ads.AdError) {
                    state = AdLoadState.FAILED
                    onFailed(AdError(AdNetwork.META, error.errorCode, error.errorMessage))
                }
                override fun onAdClicked(ad: Ad) = onClickedCb(AdNetwork.META)
                override fun onLoggingImpression(ad: Ad) = onImpressionCb(AdNetwork.META)
            })
            .build()

        fanView.loadAd(config)
    }

    override fun show(activity: Activity) {} // no-op — banner is a View

    override fun getView(): View? = adView

    override fun isReady(): Boolean = adView != null && state == AdLoadState.LOADED

    override fun destroy() {
        adView?.destroy()
        adView = null
        state = AdLoadState.IDLE
    }
}

// ----- BannerAdSize → Meta FAN AdSize conversion -----
private fun BannerAdSize.toFanSize(): FanAdSize = when (this) {
    BannerAdSize.BANNER,
    BannerAdSize.ADAPTIVE     -> FanAdSize.BANNER_HEIGHT_50
    BannerAdSize.LARGE_BANNER -> FanAdSize.BANNER_HEIGHT_90
    BannerAdSize.MEDIUM_RECTANGLE -> FanAdSize.RECTANGLE_HEIGHT_250
}
