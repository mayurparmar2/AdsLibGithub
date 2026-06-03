package com.ads.adslib.ironsource.banner

import android.app.Activity
import android.content.Context
import android.view.View
import com.ads.adslib.AdsSdk
import com.ads.adslib.core.base.NetworkAdLoader
import com.ads.adslib.core.model.AdError
import com.ads.adslib.core.model.AdLoadState
import com.ads.adslib.core.model.AdNetwork
import com.ads.adslib.core.model.BannerAdSize
import com.ads.adslib.core.model.NetworkAdUnit
import com.unity3d.mediation.LevelPlayAdError
import com.unity3d.mediation.LevelPlayAdInfo
import com.unity3d.mediation.LevelPlayAdSize
import com.unity3d.mediation.banner.LevelPlayBannerAdView
import com.unity3d.mediation.banner.LevelPlayBannerAdViewListener

/**
 * ironSource / Unity LevelPlay banner — one waterfall rung.
 *
 * [LevelPlayBannerAdView] is itself a View, so [getView] returns it directly.
 * Auto-refresh is paused/resumed via the lifecycle hooks.
 */
class IronSourceBannerLoader(
    unit: NetworkAdUnit,
    private val bannerSize: BannerAdSize,
    private val onClickedCb: (AdNetwork) -> Unit
) : NetworkAdLoader(unit) {

    private var bannerView: LevelPlayBannerAdView? = null

    override fun load(context: Context, onLoaded: () -> Unit, onFailed: (AdError) -> Unit) {
        state = AdLoadState.LOADING
        // Defer until LevelPlay init has completed (see IronSourceInterstitialLoader).
        AdsSdk.whenLevelPlayReady(
            onReady = { startLoad(context, onLoaded, onFailed) },
            onFailed = {
                state = AdLoadState.FAILED
                onFailed(AdError(AdNetwork.IRONSOURCE, 508, "LevelPlay not initialized"))
            }
        )
    }

    private fun startLoad(context: Context, onLoaded: () -> Unit, onFailed: (AdError) -> Unit) {
        val adSize = bannerSize.toLevelPlaySize(context)
        val config = LevelPlayBannerAdView.Config.Builder().setAdSize(adSize).build()
        val view = LevelPlayBannerAdView(context, unit.adUnitId, config)
        bannerView = view

        view.setBannerListener(object : LevelPlayBannerAdViewListener {
            override fun onAdLoaded(adInfo: LevelPlayAdInfo) {
                state = AdLoadState.LOADED
                onLoaded()
            }

            override fun onAdLoadFailed(error: LevelPlayAdError) {
                state = AdLoadState.FAILED
                onFailed(AdError(AdNetwork.IRONSOURCE, error.errorCode, error.errorMessage))
            }

            override fun onAdClicked(adInfo: LevelPlayAdInfo) = onClickedCb(AdNetwork.IRONSOURCE)
        })

        view.loadAd()
    }

    override fun show(activity: Activity) {} // no-op — banner is a View

    override fun getView(): View? = bannerView

    override fun isReady(): Boolean = bannerView != null && state == AdLoadState.LOADED

    override fun pause() { bannerView?.pauseAutoRefresh() }

    override fun resume() { bannerView?.resumeAutoRefresh() }

    override fun destroy() {
        bannerView?.destroy()
        bannerView = null
        state = AdLoadState.IDLE
    }
}

// ----- BannerAdSize → LevelPlayAdSize conversion -----
private fun BannerAdSize.toLevelPlaySize(context: Context): LevelPlayAdSize = when (this) {
    BannerAdSize.BANNER           -> LevelPlayAdSize.BANNER
    BannerAdSize.LARGE_BANNER     -> LevelPlayAdSize.LARGE
    BannerAdSize.MEDIUM_RECTANGLE -> LevelPlayAdSize.MEDIUM_RECTANGLE
    // createAdaptiveAdSize is @Nullable — fall back to a standard banner.
    BannerAdSize.ADAPTIVE         -> LevelPlayAdSize.createAdaptiveAdSize(context)
        ?: LevelPlayAdSize.BANNER
}
