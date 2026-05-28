package com.ads.adslib.unity.banner

import android.app.Activity
import android.content.Context
import android.view.View
import com.ads.adslib.core.base.NetworkAdLoader
import com.ads.adslib.core.model.AdError
import com.ads.adslib.core.model.AdLoadState
import com.ads.adslib.core.model.AdNetwork
import com.ads.adslib.core.model.BannerAdSize
import com.ads.adslib.core.model.NetworkAdUnit
import com.unity3d.services.banners.BannerErrorInfo
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize

class UnityBannerLoader(
    unit: NetworkAdUnit,
    private val bannerSize: BannerAdSize,
    private val onClickedCb: (AdNetwork) -> Unit
) : NetworkAdLoader(unit) {

    private var bannerView: BannerView? = null

    override fun load(context: Context, onLoaded: () -> Unit, onFailed: (AdError) -> Unit) {
        // Unity BannerView constructor requires Activity — fail gracefully if not provided
        val activity = context as? Activity ?: run {
            onFailed(AdError(AdNetwork.UNITY, -1, "UnityBannerLoader requires Activity context"))
            return
        }

        state = AdLoadState.LOADING
        val view = BannerView(activity, unit.adUnitId, bannerSize.toUnitySize())
        bannerView = view

        view.listener = object : BannerView.IListener {
            override fun onBannerLoaded(banner: BannerView) {
                state = AdLoadState.LOADED
                onLoaded()
            }

            override fun onBannerFailedToLoad(banner: BannerView, errorInfo: BannerErrorInfo) {
                state = AdLoadState.FAILED
                onFailed(AdError(AdNetwork.UNITY, errorInfo.errorCode.ordinal, errorInfo.errorMessage))
            }

            override fun onBannerClick(banner: BannerView) = onClickedCb(AdNetwork.UNITY)

            override fun onBannerLeftApplication(banner: BannerView) {}
        }

        view.load()
    }

    override fun show(activity: Activity) {} // no-op — banner is a View

    override fun getView(): View? = bannerView

    override fun isReady(): Boolean = bannerView != null && state == AdLoadState.LOADED

    override fun destroy() {
        bannerView?.destroy()
        bannerView = null
        state = AdLoadState.IDLE
    }
}

// ----- BannerAdSize → Unity UnityBannerSize conversion -----
private fun BannerAdSize.toUnitySize(): UnityBannerSize = when (this) {
    BannerAdSize.BANNER,
    BannerAdSize.ADAPTIVE     -> UnityBannerSize(320, 50)
    BannerAdSize.LARGE_BANNER -> UnityBannerSize(320, 100)
    BannerAdSize.MEDIUM_RECTANGLE -> UnityBannerSize(300, 250)
}
