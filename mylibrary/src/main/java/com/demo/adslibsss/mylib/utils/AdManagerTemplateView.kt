package com.demo.adslibsss.mylib.utils

import android.content.Context
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
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import android.graphics.Typeface
class AdManagerTemplateView : FrameLayout {
    private var templateType = 0
    private var styles: NativeTemplateStyle? = null
    private var nativeAd: NativeAd? = null
    private var nativeAdView: NativeAdView? = null

    private var primaryView: TextView? = null
    private var secondaryView: TextView? = null
    private var ratingBar: RatingBar? = null
    private var tertiaryView: TextView? = null
    private var iconView: ImageView? = null
    private var mediaView: MediaView? = null
    private var callToActionView: Button? = null
    private var background: LinearLayout? = null

    private val MEDIUM_TEMPLATE = "medium_template"
    private val SMALL_TEMPLATE = "small_template"

    constructor(context: Context) : super(context)

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

    fun setStyles(styles: NativeTemplateStyle) {
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

        var secondaryText: String?

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

        // Set the secondary view to be the star rating if available.
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

        if (icon != null) {
            iconView?.visibility = VISIBLE
            iconView?.setImageDrawable(icon.drawable)
        } else {
            iconView?.visibility = GONE
        }

        tertiaryView?.setText(body)
        nativeAdView?.bodyView = tertiaryView

        nativeAdView?.setNativeAd(nativeAd)
    }

    fun destroyNativeAd() {
        nativeAd?.destroy()
    }

    private fun initView(context: Context, attributeSet: AttributeSet?) {
        val attributes = context.theme.obtainStyledAttributes(attributeSet, R.styleable.TemplateView, 0, 0)
        templateType = try {
            attributes.getResourceId(R.styleable.TemplateView_gnt_template_type, R.layout.gnt_ad_manager_medium_template_view)
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
        mediaView = findViewById(R.id.ad_manager_media_view)
        background = findViewById(R.id.ad_manager_background)
    }
}


//
///**
// * Base class for a template view. *
// */
//class AdManagerTemplateView : FrameLayout {
//    private var templateType = 0
//    private var styles: NativeTemplateStyle? = null
//    private var nativeAd: NativeAd? = null
//    var nativeAdView: NativeAdView? = null
//        private set
//    private var primaryView: TextView? = null
//    private var secondaryView: TextView? = null
//    private var ratingBar: RatingBar? = null
//    private var tertiaryView: TextView? = null
//    private var iconView: ImageView? = null
//    private var mediaView: MediaView? = null
//    private var callToActionView: Button? = null
//    private var background: LinearLayout? = null
//
//    constructor(context: Context?) : super(context!!)
//    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
//        initView(context, attrs)
//    }
//
//    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
//        initView(context, attrs)
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
//        initView(context, attrs)
//    }
//
//    fun setStyles(styles: NativeTemplateStyle?) {
//        this.styles = styles
//        applyStyles()
//    }
//
//    private fun applyStyles() {
//        val mainBackground: Drawable? = styles!!.mainBackgroundColor
//        if (mainBackground != null) {
//            background!!.background = mainBackground
//            if (primaryView != null) {
//                primaryView!!.background = mainBackground
//            }
//            if (secondaryView != null) {
//                secondaryView!!.background = mainBackground
//            }
//            if (tertiaryView != null) {
//                tertiaryView!!.background = mainBackground
//            }
//        }
//        val primary = styles!!.primaryTextTypeface
//        if (primary != null && primaryView != null) {
//            primaryView!!.setTypeface(primary)
//        }
//        val secondary = styles!!.secondaryTextTypeface
//        if (secondary != null && secondaryView != null) {
//            secondaryView!!.setTypeface(secondary)
//        }
//        val tertiary = styles!!.tertiaryTextTypeface
//        if (tertiary != null && tertiaryView != null) {
//            tertiaryView!!.setTypeface(tertiary)
//        }
//        val ctaTypeface = styles!!.callToActionTextTypeface
//        if (ctaTypeface != null && callToActionView != null) {
//            callToActionView!!.setTypeface(ctaTypeface)
//        }
//        val primaryTypefaceColor = styles!!.primaryTextTypefaceColor
//        if (primaryTypefaceColor > 0 && primaryView != null) {
//            primaryView!!.setTextColor(primaryTypefaceColor)
//        }
//        val secondaryTypefaceColor = styles!!.secondaryTextTypefaceColor
//        if (secondaryTypefaceColor > 0 && secondaryView != null) {
//            secondaryView!!.setTextColor(secondaryTypefaceColor)
//        }
//        val tertiaryTypefaceColor = styles!!.tertiaryTextTypefaceColor
//        if (tertiaryTypefaceColor > 0 && tertiaryView != null) {
//            tertiaryView!!.setTextColor(tertiaryTypefaceColor)
//        }
//        val ctaTypefaceColor = styles!!.callToActionTypefaceColor
//        if (ctaTypefaceColor > 0 && callToActionView != null) {
//            callToActionView!!.setTextColor(ctaTypefaceColor)
//        }
//        val ctaTextSize = styles!!.callToActionTextSize
//        if (ctaTextSize > 0 && callToActionView != null) {
//            callToActionView!!.textSize = ctaTextSize
//        }
//        val primaryTextSize = styles!!.primaryTextSize
//        if (primaryTextSize > 0 && primaryView != null) {
//            primaryView!!.textSize = primaryTextSize
//        }
//        val secondaryTextSize = styles!!.secondaryTextSize
//        if (secondaryTextSize > 0 && secondaryView != null) {
//            secondaryView!!.textSize = secondaryTextSize
//        }
//        val tertiaryTextSize = styles!!.tertiaryTextSize
//        if (tertiaryTextSize > 0 && tertiaryView != null) {
//            tertiaryView!!.textSize = tertiaryTextSize
//        }
//        val ctaBackground: Drawable? = styles!!.callToActionBackgroundColor
//        if (ctaBackground != null && callToActionView != null) {
//            callToActionView!!.background = ctaBackground
//        }
//        val primaryBackground: Drawable? = styles!!.primaryTextBackgroundColor
//        if (primaryBackground != null && primaryView != null) {
//            primaryView!!.background = primaryBackground
//        }
//        val secondaryBackground: Drawable? = styles!!.secondaryTextBackgroundColor
//        if (secondaryBackground != null && secondaryView != null) {
//            secondaryView!!.background = secondaryBackground
//        }
//        val tertiaryBackground: Drawable? = styles!!.tertiaryTextBackgroundColor
//        if (tertiaryBackground != null && tertiaryView != null) {
//            tertiaryView!!.background = tertiaryBackground
//        }
//        invalidate()
//        requestLayout()
//    }
//
//    private fun adHasOnlyStore(nativeAd: NativeAd): Boolean {
//        val store = nativeAd.store
//        val advertiser = nativeAd.advertiser
//        return !TextUtils.isEmpty(store) && TextUtils.isEmpty(advertiser)
//    }
//
//    fun setNativeAd(nativeAd: NativeAd) {
//        this.nativeAd = nativeAd
//        val store = nativeAd.store
//        val advertiser = nativeAd.advertiser
//        val headline = nativeAd.headline
//        val body = nativeAd.body
//        val cta = nativeAd.callToAction
//        val starRating = nativeAd.starRating
//        val icon = nativeAd.icon
//        val secondaryText: String?
//        nativeAdView!!.callToActionView = callToActionView
//        nativeAdView!!.headlineView = primaryView
//        nativeAdView!!.mediaView = mediaView
//        secondaryView!!.visibility = VISIBLE
//        if (adHasOnlyStore(nativeAd)) {
//            nativeAdView!!.storeView = secondaryView
//            secondaryText = store
//        } else if (!TextUtils.isEmpty(advertiser)) {
//            nativeAdView!!.advertiserView = secondaryView
//            secondaryText = advertiser
//        } else {
//            secondaryText = ""
//        }
//        primaryView!!.text = headline
//        callToActionView!!.text = cta
//
//        //  Set the secondary view to be the star rating if available.
//        if (starRating != null && starRating > 0) {
//            secondaryView!!.visibility = GONE
//            ratingBar!!.visibility = VISIBLE
//            ratingBar!!.setMax(5)
//            nativeAdView!!.starRatingView = ratingBar
//        } else {
//            secondaryView!!.text = secondaryText
//            secondaryView!!.visibility = VISIBLE
//            ratingBar!!.visibility = GONE
//        }
//        if (icon != null) {
//            iconView!!.setVisibility(VISIBLE)
//            iconView!!.setImageDrawable(icon.drawable)
//        } else {
//            iconView!!.setVisibility(GONE)
//        }
//        if (tertiaryView != null) {
//            tertiaryView!!.text = body
//            nativeAdView!!.bodyView = tertiaryView
//        }
//        nativeAdView!!.setNativeAd(nativeAd)
//    }
//
//    /**
//     * To prevent memory leaks, make sure to destroy your ad when you don't need it anymore. This
//     * selectCategory does not destroy the template view.
//     * https://developers.google.com/admob/android/native-unified#destroy_ad
//     */
//    fun destroyNativeAd() {
//        nativeAd!!.destroy()
//    }
//
//    private fun initView(context: Context, attributeSet: AttributeSet?) {
//        val attributes = context.theme.obtainStyledAttributes(attributeSet, R.styleable.TemplateView, 0, 0)
//        templateType = try {
//            attributes.getResourceId(R.styleable.TemplateView_gnt_template_type, R.layout.gnt_ad_manager_medium_template_view)
//        } finally {
//            attributes.recycle()
//        }
//        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        inflater.inflate(templateType, this)
//    }
//
//    public override fun onFinishInflate() {
//        super.onFinishInflate()
//        nativeAdView = findViewById(R.id.native_ad_view)
//        primaryView = findViewById(R.id.primary)
//        secondaryView = findViewById(R.id.secondary)
//        tertiaryView = findViewById(R.id.body)
//        ratingBar = findViewById(R.id.rating_bar)
//        ratingBar.setEnabled(false)
//        callToActionView = findViewById(R.id.cta)
//        iconView = findViewById(R.id.icon)
//        mediaView = findViewById(R.id.ad_manager_media_view)
//        //mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
//        background = findViewById(R.id.ad_manager_background)
//    }
//
//    companion object {
//        private const val MEDIUM_TEMPLATE = "medium_template"
//        private const val SMALL_TEMPLATE = "small_template"
//    }
//}
