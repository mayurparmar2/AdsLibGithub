package com.ads.adslib.admob.native_ad

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
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
import com.ads.adslib.core.model.NativeAdStyle
import com.ads.adslib.core.model.NativeType
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
    private val style: NativeAdStyle? = null,
    private val nativeType: NativeType = NativeType.NATIVE,
    private val onClickedCb: (AdNetwork) -> Unit,
    private val onImpressionCb: (AdNetwork) -> Unit
) : NetworkAdLoader(unit) {

    /** Compact, self-themed premium banner template (no media, no host recolor). */
    private val isBanner: Boolean = nativeType == NativeType.NATIVE_BANNER

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


        val layoutRes = if (isBanner) R.layout.admob_native_banner_premium
                        else R.layout.admob_native_large_premium
        val view = LayoutInflater.from(context)
            .inflate(layoutRes, null) as NativeAdView

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
            // Both premium templates ship a placeholder src, so keep the advertiser
            // icon slot visible for trust and only swap in the real icon when the ad
            // provides one (otherwise the XML placeholder stays).
            it.visibility = View.VISIBLE
            ad.icon?.drawable?.let { d -> (it as ImageView).setImageDrawable(d) }
        }
        view.mediaView?.let {
            it.visibility = if (ad.mediaContent != null) View.VISIBLE else View.GONE
            (it as MediaView).apply {
                // Fill the fixed-height media box edge-to-edge (no empty letterbox
                // space around the creative).
                setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                mediaContent = ad.mediaContent
            }
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

        // Apply optional host theme colors so the template adapts (esp. dark mode).
        // The compact banner is transparent by design (the host card supplies the
        // surface), so the background override is a no-op there but text/CTA colors
        // still let it match the app's accent and dark/light text.
        style?.let { s ->
            s.backgroundColor?.let { bg ->
                // The white background lives on the inner LinearLayout, not the
                // NativeAdView root — clear both so the host card shows through.
                view.setBackgroundColor(bg)
                (view.getChildAt(0))?.setBackgroundColor(bg)
            }
            s.titleColor?.let { (view.headlineView as? TextView)?.setTextColor(it) }
            s.bodyColor?.let { c ->
                (view.bodyView as? TextView)?.setTextColor(c)
                (view.advertiserView as? TextView)?.setTextColor(c)
                (view.priceView as? TextView)?.setTextColor(c)
                (view.storeView as? TextView)?.setTextColor(c)
            }
            s.ctaTextColor?.let { (view.callToActionView as? Button)?.setTextColor(it) }
            // Tint (don't replace) the CTA background so rounded corners survive.
            s.ctaBackgroundColor?.let {
                (view.callToActionView as? Button)?.backgroundTintList = ColorStateList.valueOf(it)
            }
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
