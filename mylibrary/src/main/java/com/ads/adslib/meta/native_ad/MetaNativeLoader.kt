package com.ads.adslib.meta.native_ad

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.ads.adslib.R
import com.ads.adslib.core.base.NetworkAdLoader
import com.ads.adslib.core.model.AdError
import com.ads.adslib.core.model.AdLoadState
import com.ads.adslib.core.model.AdNetwork
import com.ads.adslib.core.model.NetworkAdUnit
import com.facebook.ads.Ad
import com.facebook.ads.AdError as FanAdError
import com.facebook.ads.AdOptionsView
import com.facebook.ads.MediaView
import com.facebook.ads.NativeAd as FanNativeAd
import com.facebook.ads.NativeAdListener

class MetaNativeLoader(
    unit: NetworkAdUnit,
    private val onClickedCb: (AdNetwork) -> Unit,
    private val onImpressionCb: (AdNetwork) -> Unit
) : NetworkAdLoader(unit) {

    private var nativeAd: FanNativeAd? = null
    private var adView: View? = null

    override fun load(context: Context, onLoaded: () -> Unit, onFailed: (AdError) -> Unit) {
        state = AdLoadState.LOADING
        val ad = FanNativeAd(context, unit.adUnitId)
        nativeAd = ad

        val config = ad.buildLoadAdConfig()
            .withAdListener(object : NativeAdListener {
                override fun onAdLoaded(a: Ad) {
                    adView = inflateAndPopulate(context, nativeAd!!)
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

    private fun inflateAndPopulate(context: Context, ad: FanNativeAd): View {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.native_ad_layout_1, null)

        val iconView     = view.findViewById<MediaView>(R.id.native_ad_icon)
        val titleView    = view.findViewById<TextView>(R.id.native_ad_title)
        val sponsoredView= view.findViewById<TextView>(R.id.native_ad_sponsored_label)
        val choicesBox   = view.findViewById<LinearLayout>(R.id.ad_choices_container)
        val mediaView    = view.findViewById<MediaView>(R.id.native_ad_media)
        val socialView   = view.findViewById<TextView>(R.id.native_ad_social_context)
        val bodyView     = view.findViewById<TextView>(R.id.native_ad_body)
        val ctaButton    = view.findViewById<Button>(R.id.native_ad_call_to_action)

        // Populate text
        titleView.text     = ad.advertiserName ?: ""
        sponsoredView.text = "Sponsored"
        bodyView.text      = ad.adBodyText ?: ""
        socialView.text    = ad.adSocialContext ?: ""

        // CTA
        ctaButton.text       = ad.adCallToAction ?: ""
        ctaButton.visibility = if (ad.hasCallToAction()) View.VISIBLE else View.GONE

        // AdChoices icon (required by Meta policy)
        val adChoicesView = AdOptionsView(context, ad, null)
        choicesBox.removeAllViews()
        choicesBox.addView(adChoicesView)

        // Register view for interaction — enables click + impression tracking
        ad.registerViewForInteraction(
            view,
            mediaView,
            iconView,
            listOf(ctaButton, titleView, iconView, view)
        )

        return view
    }

    override fun show(activity: Activity) {} // no-op — native is a View

    override fun getView(): View? = adView

    override fun isReady(): Boolean = adView != null && state == AdLoadState.LOADED

    override fun destroy() {
        nativeAd?.unregisterView()
        nativeAd?.destroy()
        nativeAd = null
        adView = null
        state = AdLoadState.IDLE
    }
}
