package com.ads.adslib.admob.native_ad

import android.view.ViewGroup
import com.ads.adslib.core.base.BaseAdManager
import com.ads.adslib.core.base.NetworkAdLoader
import com.ads.adslib.core.model.AdNetwork
import com.ads.adslib.core.model.AdUnitConfig
import com.ads.adslib.core.model.NetworkAdUnit
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
class NativeAdManager(config: AdUnitConfig) : BaseAdManager(config) {

    override fun createLoader(unit: NetworkAdUnit): NetworkAdLoader? = when (unit.network) {
        AdNetwork.ADMOB -> AdMobNativeLoader(
            unit           = unit,
            onClickedCb    = { net -> dispatch { onClicked(net) } },
            onImpressionCb = { net -> dispatch { onImpression(net) } }
        )
        // AdNetwork.META -> MetaNativeLoader(...)   // meta module ma add karjo
        else -> null
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
        container.removeAllViews()
        container.addView(view)
        AdLog.d(config.placementKey, "native ad attached via ${readyLoader?.network}")
    }
}
