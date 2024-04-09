
package com.demo.adslibsss.mylib.view

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.ads.adslib.R
import com.demo.adslibsss.mylib.utils.NativeTemplateStyle
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class TemplateView : FrameLayout {
    private var templateType = 0
    private var styles: NativeTemplateStyle? = null
    private var nativeAd: NativeAd? = null
    var nativeAdView: NativeAdView? = null
        private set
    private var primaryView: TextView? = null
    private var secondaryView: TextView? = null
    private var ratingBar: RatingBar? = null
    private var tertiaryView: TextView? = null
    private var iconView: ImageView? = null
    private var mediaView: MediaView? = null
    private var callToActionView: Button? = null
    private var background: LinearLayout? = null

    constructor(context: Context?) : super(context!!)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context, attrs)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView(context, attrs)
    }

    fun setStyles(styles: NativeTemplateStyle?) {
        this.styles = styles
        applyStyles()
    }

    private fun applyStyles() {
        val mainBackground: Drawable? = styles?.mainBackgroundColor
        if (mainBackground != null) {
            background?.background = mainBackground
            primaryView?.background = mainBackground
            secondaryView?.background = mainBackground
            tertiaryView?.background = mainBackground
        }
        val primary: Typeface? = styles?.primaryTextTypeface
        primaryView?.typeface = primary
        val secondary: Typeface? = styles?.secondaryTextTypeface
        secondaryView?.typeface = secondary
        val tertiary: Typeface? = styles?.tertiaryTextTypeface
        tertiaryView?.typeface = tertiary
        val ctaTypeface: Typeface? = styles?.callToActionTextTypeface
        callToActionView?.typeface = ctaTypeface
        val primaryTypefaceColor: Int = styles?.primaryTextTypefaceColor ?: 0
        primaryView?.setTextColor(primaryTypefaceColor)
        val secondaryTypefaceColor: Int = styles?.secondaryTextTypefaceColor ?: 0
        secondaryView?.setTextColor(secondaryTypefaceColor)
        val tertiaryTypefaceColor: Int = styles?.tertiaryTextTypefaceColor ?: 0
        tertiaryView?.setTextColor(tertiaryTypefaceColor)
        val ctaTypefaceColor: Int = styles?.callToActionTypefaceColor ?: 0
        callToActionView?.setTextColor(ctaTypefaceColor)
        val ctaTextSize: Float = styles?.callToActionTextSize ?: 0f
        callToActionView?.textSize = ctaTextSize
        val primaryTextSize: Float = styles?.primaryTextSize ?: 0f
        primaryView?.textSize = primaryTextSize
        val secondaryTextSize: Float = styles?.secondaryTextSize ?: 0f
        secondaryView?.textSize = secondaryTextSize
        val tertiaryTextSize: Float = styles?.tertiaryTextSize ?: 0f
        tertiaryView?.textSize = tertiaryTextSize
        val ctaBackground: Drawable? = styles?.callToActionBackgroundColor
        callToActionView?.background = ctaBackground
        val primaryBackground: Drawable? = styles?.primaryTextBackgroundColor
        primaryView?.background = primaryBackground
        val secondaryBackground: Drawable? = styles?.secondaryTextBackgroundColor
        secondaryView?.background = secondaryBackground
        val tertiaryBackground: Drawable? = styles?.tertiaryTextBackgroundColor
        tertiaryView?.background = tertiaryBackground
        invalidate()
        requestLayout()
    }

    private fun adHasOnlyStore(nativeAd: NativeAd): Boolean {
        val store = nativeAd.store
        val advertiser = nativeAd.advertiser
        return !TextUtils.isEmpty(store) && TextUtils.isEmpty(advertiser)
    }

    fun setNativeAd(nativeAd: NativeAd) {
        this.nativeAd = nativeAd
        val store = nativeAd.store
        val advertiser = nativeAd.advertiser
        val headline = nativeAd.headline
        val body = nativeAd.body
        val cta = nativeAd.callToAction
        val starRating = nativeAd.starRating
        val icon = nativeAd.icon
        val secondaryText: String?
        nativeAdView?.callToActionView = callToActionView
        nativeAdView?.headlineView = primaryView
        nativeAdView?.mediaView = mediaView
        secondaryView?.visibility = VISIBLE
        if (adHasOnlyStore(nativeAd)) {
            nativeAdView?.storeView = secondaryView
            secondaryText = store
        } else if (!TextUtils.isEmpty(advertiser)) {
            nativeAdView?.advertiserView = secondaryView
            secondaryText = advertiser
        } else {
            secondaryText = ""
        }
        primaryView?.text = headline
        callToActionView?.text = cta

        //  Set the secondary view to be the star rating if available.
        if (starRating != null && starRating > 0) {
            secondaryView?.visibility = GONE
            ratingBar?.visibility = VISIBLE
            ratingBar?.max = 5
            nativeAdView?.starRatingView = ratingBar
        } else {
            secondaryView?.text = secondaryText
            secondaryView?.visibility = VISIBLE
            ratingBar?.visibility = GONE
        }
        iconView?.visibility = if (icon != null) VISIBLE else GONE
        iconView?.setImageDrawable(icon?.drawable)
        tertiaryView?.text = body
        nativeAdView?.setNativeAd(nativeAd)
    }

    /**
     * To prevent memory leaks, make sure to destroy your ad when you don't need it anymore. This
     * selectCategory does not destroy the template view.
     * https://developers.google.com/admob/android/native-unified#destroy_ad
     */
    fun destroyNativeAd() {
        nativeAd?.destroy()
    }

    private fun initView(context: Context, attributeSet: AttributeSet?) {
        val attributes = context.theme.obtainStyledAttributes(attributeSet, R.styleable.TemplateView, 0, 0)
        templateType = try {
            attributes.getResourceId(R.styleable.TemplateView_gnt_template_type, R.layout.gnt_admob_medium_template_view)
        } finally {
            attributes.recycle()
        }
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(templateType, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        nativeAdView = findViewById(R.id.native_ad_view)
        primaryView = findViewById(R.id.primary)
        secondaryView = findViewById(R.id.secondary)
        tertiaryView = findViewById(R.id.body)
        ratingBar = findViewById(R.id.rating_bar)
        ratingBar?.isEnabled = false
        callToActionView = findViewById(R.id.cta)
        iconView = findViewById(R.id.icon)
        mediaView = findViewById(R.id.media_view)
        //mediaView?.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
        background = findViewById(R.id.background)
    }

    companion object {
        private const val MEDIUM_TEMPLATE = "medium_template"
        private const val SMALL_TEMPLATE = "small_template"
    }
}
