package com.ads.adslib.admob.native_ad

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.ads.adslib.R
import com.ads.adslib.core.base.NetworkAdLoader
import com.ads.adslib.core.model.AdError
import com.ads.adslib.core.model.AdLoadState
import com.ads.adslib.core.model.AdNetwork
import com.ads.adslib.core.model.NetworkAdUnit
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView

class AdMobNativeLoader(
    unit: NetworkAdUnit,
    private val onClickedCb: (AdNetwork) -> Unit,
    private val onImpressionCb: (AdNetwork) -> Unit
) : NetworkAdLoader(unit) {

    private var nativeAd: NativeAd? = null
    private var adView: NativeAdView? = null

    override fun load(context: Context, onLoaded: () -> Unit, onFailed: (AdError) -> Unit) {
        state = AdLoadState.LOADING

        AdLoader.Builder(context, unit.adUnitId)
            .forNativeAd { ad ->
                nativeAd = ad
                adView = inflateAndPopulate(context, ad)
                state = AdLoadState.LOADED
                onLoaded()
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    state = AdLoadState.FAILED
                    onFailed(AdError(AdNetwork.ADMOB, error.code, error.message))
                }
                override fun onAdClicked()    = onClickedCb(AdNetwork.ADMOB)
                override fun onAdImpression() = onImpressionCb(AdNetwork.ADMOB)
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()
            .loadAd(AdRequest.Builder().build())
    }

    private fun inflateAndPopulate(context: Context, ad: NativeAd): NativeAdView {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.admob_native_ad_template, null) as NativeAdView

        // Inflated with a null root, so the XML root width/height are dropped —
        // restore full-width so the ad fills its container instead of wrapping.
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )

        // Register each asset view with NativeAdView before setting content
        view.headlineView      = view.findViewById(R.id.ad_headline)
        view.bodyView          = view.findViewById(R.id.ad_body)
        view.callToActionView  = view.findViewById(R.id.ad_call_to_action)
        view.iconView          = view.findViewById(R.id.ad_app_icon)
        view.mediaView         = view.findViewById(R.id.ad_media)
        view.starRatingView    = view.findViewById(R.id.ad_stars)
        view.advertiserView    = view.findViewById(R.id.ad_advertiser)
        view.priceView         = view.findViewById(R.id.ad_price)
        view.storeView         = view.findViewById(R.id.ad_store)

        // Populate — hide optional views when content is null
        (view.headlineView as TextView).text = ad.headline

        view.bodyView?.let {
            it.visibility = if (ad.body != null) View.VISIBLE else View.GONE
            (it as TextView).text = ad.body
        }
        view.callToActionView?.let {
            it.visibility = if (ad.callToAction != null) View.VISIBLE else View.GONE
            (it as Button).text = ad.callToAction
        }
        view.iconView?.let {
            it.visibility = if (ad.icon != null) View.VISIBLE else View.GONE
            (it as ImageView).setImageDrawable(ad.icon?.drawable)
        }
        view.mediaView?.let {
            it.visibility = if (ad.mediaContent != null) View.VISIBLE else View.GONE
            (it as MediaView).mediaContent = ad.mediaContent
        }
        view.starRatingView?.let {
            it.visibility = if (ad.starRating != null) View.VISIBLE else View.GONE
            (it as RatingBar).rating = ad.starRating?.toFloat() ?: 0f
        }
        view.advertiserView?.let {
            it.visibility = if (ad.advertiser != null) View.VISIBLE else View.GONE
            (it as TextView).text = ad.advertiser
        }
        view.priceView?.let {
            it.visibility = if (ad.price != null) View.VISIBLE else View.GONE
            (it as TextView).text = ad.price
        }
        view.storeView?.let {
            it.visibility = if (ad.store != null) View.VISIBLE else View.GONE
            (it as TextView).text = ad.store
        }

        // Must be called after all views are registered and populated
        view.setNativeAd(ad)
        return view
    }

    override fun show(activity: Activity) {} // no-op — native is a View, not full-screen

    override fun getView(): View? = adView

    override fun isReady(): Boolean = adView != null && state == AdLoadState.LOADED

    override fun destroy() {
        nativeAd?.destroy()
        nativeAd = null
        adView = null
        state = AdLoadState.IDLE
    }
}
