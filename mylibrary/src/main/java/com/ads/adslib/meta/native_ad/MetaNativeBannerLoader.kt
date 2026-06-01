package com.ads.adslib.meta.native_ad

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.ads.adslib.R
import com.ads.adslib.core.base.NetworkAdLoader
import com.ads.adslib.core.model.AdError
import com.ads.adslib.core.model.AdLoadState
import com.ads.adslib.core.model.AdNetwork
import com.ads.adslib.core.model.NativeAdStyle
import com.ads.adslib.core.model.NetworkAdUnit
import com.facebook.ads.Ad
import com.facebook.ads.AdError as FanAdError
import com.facebook.ads.AdOptionsView
import com.facebook.ads.MediaView
import com.facebook.ads.NativeAdListener
import com.facebook.ads.NativeBannerAd

/**
 * Meta (Audience Network) **Native Banner** loader — a compact native ad
 * (icon/logo + title + CTA, no main media). The placement in Monetisation
 * Manager must be created with format "Native banner".
 *
 * Selected via the `type: "native_banner"` field on a native entry in Remote
 * Config (see [com.ads.adslib.core.model.NativeType]).
 */
class MetaNativeBannerLoader(
    unit: NetworkAdUnit,
    private val style: NativeAdStyle? = null,
    private val onClickedCb: (AdNetwork) -> Unit,
    private val onImpressionCb: (AdNetwork) -> Unit
) : NetworkAdLoader(unit) {

    private var nativeBannerAd: NativeBannerAd? = null
    private var adView: View? = null

    override fun load(context: Context, onLoaded: () -> Unit, onFailed: (AdError) -> Unit) {
        state = AdLoadState.LOADING
        val ad = NativeBannerAd(context, unit.adUnitId)
        nativeBannerAd = ad

        val config = ad.buildLoadAdConfig()
            .withAdListener(object : NativeAdListener {
                override fun onAdLoaded(a: Ad) {
                    val current = nativeBannerAd ?: return
                    adView = inflateAndPopulate(context, current)
                    state = AdLoadState.LOADED
                    onLoaded()
                }
                override fun onError(a: Ad?, error: FanAdError) {
                    state = AdLoadState.FAILED
                    onFailed(AdError(AdNetwork.META, error.errorCode, error.errorMessage))
                }
                override fun onAdClicked(a: Ad) = onClickedCb(AdNetwork.META)
                override fun onLoggingImpression(a: Ad) = onImpressionCb(AdNetwork.META)
                override fun onMediaDownloaded(a: Ad) {}
            })
            .build()

        ad.loadAd(config)
    }

    private fun inflateAndPopulate(context: Context, ad: NativeBannerAd): View {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.native_banner_layout, null)
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )

        val iconView     = view.findViewById<MediaView>(R.id.native_banner_icon)
        val titleView    = view.findViewById<TextView>(R.id.native_banner_title)
        val sponsoredView= view.findViewById<TextView>(R.id.native_banner_sponsored)
        val choicesBox   = view.findViewById<LinearLayout>(R.id.native_banner_adchoices)
        val ctaButton    = view.findViewById<Button>(R.id.native_banner_cta)

        titleView.text     = ad.advertiserName ?: ""
        sponsoredView.text = "Sponsored"
        ctaButton.text       = ad.adCallToAction ?: ""
        ctaButton.visibility = if (ad.hasCallToAction()) View.VISIBLE else View.GONE

        // Optional host theme colors.
        style?.let { s ->
            s.backgroundColor?.let { view.setBackgroundColor(it) }
            s.titleColor?.let { titleView.setTextColor(it) }
            s.bodyColor?.let { sponsoredView.setTextColor(it) }
            s.ctaTextColor?.let { ctaButton.setTextColor(it) }
            s.ctaBackgroundColor?.let { ctaButton.setBackgroundColor(it) }
        }

        val adChoicesView = AdOptionsView(context, ad, null)
        choicesBox.removeAllViews()
        choicesBox.addView(adChoicesView)

        // Native banner registers the icon MediaView (no main media view).
        ad.registerViewForInteraction(view, iconView, listOf(ctaButton, titleView, view))

        return view
    }

    override fun show(activity: Activity) {} // no-op — native is a View

    override fun getView(): View? = adView

    override fun isReady(): Boolean = adView != null && state == AdLoadState.LOADED

    override fun destroy() {
        nativeBannerAd?.unregisterView()
        nativeBannerAd?.destroy()
        nativeBannerAd = null
        adView = null
        state = AdLoadState.IDLE
    }
}
