package com.ads.adslib.admob.native_ad

import android.view.ViewGroup
import com.ads.adslib.core.base.BaseAdManager
import com.ads.adslib.core.base.NetworkAdLoader
import com.ads.adslib.core.model.AdNetwork
import com.ads.adslib.core.model.AdUnitConfig
import com.ads.adslib.core.model.NativeAdStyle
import com.ads.adslib.core.model.NativeType
import com.ads.adslib.core.model.NetworkAdUnit
import com.ads.adslib.ironsource.native_ad.IronSourceNativeLoader
import com.ads.adslib.meta.native_ad.MetaNativeBannerLoader
import com.ads.adslib.meta.native_ad.MetaNativeLoader
import com.ads.adslib.util.AdLog

/**
 * Waterfall manager for native ad placements.
 *
 * The library inflates and populates [R.layout.admob_native_ad_template] automatically.
 * After [load] completes, call [attach] to insert the populated view into any container.
 *
 * Usage:
 * ```
 * val config = AdUnitConfig(
 *     placementKey = "home_native",
 *     format       = AdFormat.NATIVE,
 *     waterfall    = listOf(
 *         NetworkAdUnit(AdNetwork.ADMOB, "ca-app-pub-xxx/native_id"),
 *         // NetworkAdUnit(AdNetwork.META,  "META_PLACEMENT"),   // future
 *     )
 * )
 * val mgr = NativeAdManager(config)
 * mgr.load(context, object : AdCallback {
 *     override fun onLoaded(network: AdNetwork) { mgr.attach(nativeContainer) }
 *     override fun onFailedToLoad(error: AdError) { nativeContainer.visibility = View.GONE }
 * })
 * mgr.destroy() // in onDestroy
 * ```
 */
class NativeAdManager(
    config: AdUnitConfig,
    private val style: NativeAdStyle? = null,
) : BaseAdManager(config) {

    override fun createLoader(unit: NetworkAdUnit): NetworkAdLoader? = when (unit.network) {
        AdNetwork.ADMOB -> AdMobNativeLoader(
            unit           = unit,
            style          = style,
            onClickedCb    = { net -> dispatch { onClicked(net) } },
            onImpressionCb = { net -> dispatch { onImpression(net) } }
        )
        AdNetwork.META -> if (config.nativeType == NativeType.NATIVE_BANNER) {
            MetaNativeBannerLoader(
                unit           = unit,
                style          = style,
                onClickedCb    = { net -> dispatch { onClicked(net) } },
                onImpressionCb = { net -> dispatch { onImpression(net) } }
            )
        } else {
            MetaNativeLoader(
                unit           = unit,
                style          = style,
                onClickedCb    = { net -> dispatch { onClicked(net) } },
                onImpressionCb = { net -> dispatch { onImpression(net) } }
            )
        }
        AdNetwork.IRONSOURCE -> IronSourceNativeLoader(
            unit           = unit,
            style          = style,
            onClickedCb    = { net -> dispatch { onClicked(net) } },
            onImpressionCb = { net -> dispatch { onImpression(net) } }
        )
    }

    /**
     * Insert the loaded native ad view into [container].
     * Call inside [AdCallback.onLoaded] or anytime after [isReady] is true.
     * Replaces any existing child in the container.
     */
    fun attach(container: ViewGroup) {
        val view = readyLoader?.getView()
        if (view == null) {
            AdLog.w(config.placementKey, "attach() called but no native ad view ready")
            return
        }
        // Detach from any previous parent first so the same loaded view can be
        // re-attached into a new container (e.g. when a Compose LazyColumn item
        // is recycled on scroll) without an "already has a parent" crash.
        (view.parent as? ViewGroup)?.removeView(view)
        container.removeAllViews()
        container.addView(view)
        AdLog.d(config.placementKey, "native ad attached via ${readyLoader?.network}")
    }
}
