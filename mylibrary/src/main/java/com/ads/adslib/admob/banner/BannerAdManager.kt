package com.ads.adslib.admob.banner

import android.view.ViewGroup
import com.ads.adslib.core.base.BaseAdManager
import com.ads.adslib.core.base.NetworkAdLoader
import com.ads.adslib.core.model.AdNetwork
import com.ads.adslib.core.model.AdUnitConfig
import com.ads.adslib.core.model.NetworkAdUnit
import com.ads.adslib.meta.banner.MetaBannerLoader
import com.ads.adslib.unity.banner.UnityBannerLoader
import com.ads.adslib.util.AdLog

/**
 * Waterfall manager for banner placements.
 *
 * Differs from full-screen managers: instead of [show], call [attach] to add the
 * loaded banner view into a container. Waterfall order is driven by [AdUnitConfig.waterfall].
 *
 * Usage:
 * ```
 * val config = AdUnitConfig(
 *     placementKey = "home_banner",
 *     format       = AdFormat.BANNER,
 *     bannerSize   = BannerAdSize.ADAPTIVE,
 *     waterfall    = listOf(
 *         NetworkAdUnit(AdNetwork.ADMOB, "ca-app-pub-xxx/banner_id"),
 *         NetworkAdUnit(AdNetwork.META,  "YOUR_META_BANNER_PLACEMENT_ID"),
 *         NetworkAdUnit(AdNetwork.UNITY, "YOUR_UNITY_BANNER_PLACEMENT_ID"),
 *     )
 * )
 * val mgr = BannerAdManager(config)
 * mgr.load(context, object : AdCallback {
 *     override fun onLoaded(network: AdNetwork) { mgr.attach(bannerContainer) }
 *     override fun onFailedToLoad(error: AdError) { /* hide container */ }
 * })
 * // In onPause / onResume / onDestroy:
 * mgr.pause() / mgr.resume() / mgr.destroy()
 * ```
 */
class BannerAdManager(config: AdUnitConfig) : BaseAdManager(config) {

    override fun createLoader(unit: NetworkAdUnit): NetworkAdLoader? = when (unit.network) {
        AdNetwork.ADMOB -> AdMobBannerLoader(
            unit           = unit,
            bannerSize     = config.bannerSize,
            onClickedCb    = { net -> dispatch { onClicked(net) } },
            onImpressionCb = { net -> dispatch { onImpression(net) } }
        )
        AdNetwork.META -> MetaBannerLoader(
            unit           = unit,
            bannerSize     = config.bannerSize,
            onClickedCb    = { net -> dispatch { onClicked(net) } },
            onImpressionCb = { net -> dispatch { onImpression(net) } }
        )
        AdNetwork.UNITY -> UnityBannerLoader(
            unit        = unit,
            bannerSize  = config.bannerSize,
            onClickedCb = { net -> dispatch { onClicked(net) } }
        )
        else -> null
    }

    /**
     * Add the loaded banner View into [container].
     * Call inside [AdCallback.onLoaded] or anytime after [isReady] is true.
     * Replaces any existing child in the container.
     */
    fun attach(container: ViewGroup) {
        val view = readyLoader?.getView()
        if (view == null) {
            AdLog.w(config.placementKey, "attach() called but no banner view ready")
            return
        }
        container.removeAllViews()
        container.addView(view)
        AdLog.d(config.placementKey, "banner attached via ${readyLoader?.network}")
    }

    /** Forward to active banner loader — call from Activity.onPause(). */
    fun pause() { readyLoader?.pause() }

    /** Forward to active banner loader — call from Activity.onResume(). */
    fun resume() { readyLoader?.resume() }
}
