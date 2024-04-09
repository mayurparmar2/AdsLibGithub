package com.demo.adslibsss.mylib

import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.ads.adslib.R
import com.demo.adslibsss.mylib.Constant.AD_STATUS_ON
import com.demo.adslibsss.mylib.utils.NativeTemplateStyle
import com.demo.adslibsss.mylib.utils.Tools
import com.demo.adslibsss.mylib.view.TemplateView
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.AdOptionsView
import com.facebook.ads.MediaView
import com.facebook.ads.NativeAdBase.NativeLoadAdConfig
import com.facebook.ads.NativeAdLayout
import com.facebook.ads.NativeAdListener
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.LoadAdError

class NativeAd {
    class Builder(val activity: Activity) {
        companion object {
            private const val TAG = "NativeAd.class"
        }

        lateinit var fanNativeAd: com.facebook.ads.NativeAd
        lateinit var fanNativeAdLayout: NativeAdLayout
        lateinit var nativeAdViewContainer: LinearLayout
        lateinit var admobNativeAd: TemplateView
        lateinit var admobNativeBackground: LinearLayout
        lateinit var mediaView: com.google.android.gms.ads.nativead.MediaView

        private var adStatus = ""
        private var adNetwork = "facebook"
        private var backupAdNetwork = "admob"
        private var adMobNativeId = "ca-app-pub-3940256099942544/2247696110"
//        private var fanNativeId = "VID_HD_9_16_39S_APP_INSTALL#YOUR_PLACEMENT_ID"
        private var fanNativeId = "YOUR_PLACEMENT_ID"
        private var nativeAdStyle = ""

        private var nativeBackgroundLight = R.color.color_native_background_light
        private var nativeBackgroundDark = R.color.color_native_background_dark

        private var darkTheme = false
        private val legacyGDPR = false

        fun setAdStatus(adStatus: String): NativeAd.Builder {
            this.adStatus = adStatus
            return this
        }

        fun setAdNetwork(adNetwork: String): Builder {
//            this.adNetwork = adNetwork
            return this
        }

        fun setBackupAdNetwork(backupAdNetwork: String): Builder {
//            this.backupAdNetwork = backupAdNetwork
            return this
        }

        fun setAdMobNativeId(adMobNativeId: String): Builder {
//            this.adMobNativeId = adMobNativeId
            return this
        }

        fun setFanNativeId(fanNativeId: String): Builder {
//            this.fanNativeId = fanNativeId
            return this
        }

        fun setNativeAdBackgroundColor(colorLight: Int, colorDark: Int): Builder {
            this.nativeBackgroundLight = colorLight
            this.nativeBackgroundDark = colorDark
            return this
        }

        fun setNativeAdStyle(nativeAdView: ViewGroup, nativeAdStyle: String): Builder {
            when (nativeAdStyle) {
                Constant.STYLE_NEWS -> nativeAdView.addView(View.inflate(activity, R.layout.view_native_ad_news, null))
                Constant.STYLE_RADIO -> nativeAdView.addView(View.inflate(activity, R.layout.view_native_ad_radio, null))
                Constant.STYLE_VIDEO_SMALL -> nativeAdView.addView(View.inflate(activity, R.layout.view_native_ad_video_small, null))
                Constant.STYLE_VIDEO_LARGE -> nativeAdView.addView(View.inflate(activity, R.layout.view_native_ad_video_large, null))
                else -> nativeAdView.addView(View.inflate(activity, R.layout.view_native_ad_medium, null))
            }
            this.nativeAdStyle = nativeAdStyle
            return this
        }
        fun setDarkTheme(darkTheme: Boolean):Builder {
            this.darkTheme = darkTheme
            return this
        }
        fun build(): Builder {
            loadNativeAd()
            return this
        }

        fun loadNativeAd() {
            if (adStatus == AD_STATUS_ON) {
                admobNativeAd = activity.findViewById(R.id.admob_native_ad_container)
                mediaView = activity.findViewById(R.id.media_view)
                fanNativeAdLayout = activity.findViewById<NativeAdLayout>(R.id.fan_native_ad_container)
                nativeAdViewContainer = activity.findViewById<LinearLayout>(R.id.native_ad_view_container)
                admobNativeBackground = activity.findViewById<LinearLayout>(R.id.background)
                when (adNetwork) {


                    Constant.ADMOB -> {

                    }

                    Constant.FACEBOOK -> {
                        fanNativeAd = com.facebook.ads.NativeAd(activity, fanNativeId)
                        val nativeAdListener: NativeAdListener = object : NativeAdListener {
                            override fun onMediaDownloaded(ad: Ad) {}
                            override fun onError(ad: Ad, adError: AdError) {
                                Log.e(TAG, "loadNativeAd(): onAdLoaded:")

                                loadBackupNativeAd()
                            }
                            override fun onAdLoaded(ad: Ad) {
                                Log.e(TAG, "loadNativeAd(): onAdLoaded:")

                                // Race condition, load() called again before last ad was displayed
                                fanNativeAdLayout.setVisibility(View.VISIBLE)
                                nativeAdViewContainer.setVisibility(View.VISIBLE)
                                if (fanNativeAd !== ad) {
                                    return
                                }
                                // Inflate Native Ad into Container
                                //inflateAd(nativeAd);
                                fanNativeAd.unregisterView()
                                // Add the Ad view into the ad container.
                                val inflater = LayoutInflater.from(activity)
                                // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
//                                val nativeAdView: LinearLayout
                                val nativeAdView = when (nativeAdStyle) {
                                    Constant.STYLE_NEWS, Constant.STYLE_MEDIUM -> inflater.inflate(R.layout.gnt_fan_news_template_view, fanNativeAdLayout, false) as LinearLayout
                                    Constant.STYLE_VIDEO_SMALL -> inflater.inflate(R.layout.gnt_fan_video_small_template_view, fanNativeAdLayout, false) as LinearLayout
                                    Constant.STYLE_VIDEO_LARGE -> inflater.inflate(R.layout.gnt_fan_video_large_template_view, fanNativeAdLayout, false) as LinearLayout
                                    Constant.STYLE_RADIO, Constant.STYLE_SMALL -> inflater.inflate(R.layout.gnt_fan_radio_template_view, fanNativeAdLayout, false) as LinearLayout
                                    else -> inflater.inflate(R.layout.gnt_fan_medium_template_view, fanNativeAdLayout, false) as LinearLayout
                                }
                                fanNativeAdLayout.addView(nativeAdView)

                                // Add the AdOptionsView
                                val adChoicesContainer = nativeAdView.findViewById<LinearLayout>(R.id.ad_choices_container)
                                val adOptionsView = AdOptionsView(activity, fanNativeAd, fanNativeAdLayout)
                                adChoicesContainer.removeAllViews()
                                adChoicesContainer.addView(adOptionsView, 0)

                                // Create native UI using the ad metadata.
                                val nativeAdTitle = nativeAdView.findViewById<TextView>(R.id.native_ad_title)
                                val nativeAdMedia = nativeAdView.findViewById<MediaView>(R.id.native_ad_media)
                                val nativeAdIcon = nativeAdView.findViewById<MediaView>(R.id.native_ad_icon)
                                val nativeAdSocialContext = nativeAdView.findViewById<TextView>(R.id.native_ad_social_context)
                                val nativeAdBody = nativeAdView.findViewById<TextView>(R.id.native_ad_body)
                                val sponsoredLabel = nativeAdView.findViewById<TextView>(R.id.native_ad_sponsored_label)
                                val nativeAdCallToAction = nativeAdView.findViewById<Button>(R.id.native_ad_call_to_action)
                                val fanNativeBackground = nativeAdView.findViewById<LinearLayout>(R.id.ad_unit)
                                if (darkTheme) {
                                    nativeAdTitle.setTextColor(ContextCompat.getColor(activity, R.color.applovin_dark_primary_text_color))
                                    nativeAdSocialContext.setTextColor(ContextCompat.getColor(activity, R.color.applovin_dark_primary_text_color))
                                    sponsoredLabel.setTextColor(ContextCompat.getColor(activity, R.color.applovin_dark_secondary_text_color))
                                    nativeAdBody.setTextColor(ContextCompat.getColor(activity, R.color.applovin_dark_secondary_text_color))
                                    fanNativeBackground.setBackgroundResource(nativeBackgroundDark)
                                } else {
                                    fanNativeBackground.setBackgroundResource(nativeBackgroundLight)
                                }

                                // Set the Text.
                                nativeAdTitle.setText(fanNativeAd.getAdvertiserName())
                                nativeAdBody.setText(fanNativeAd.getAdBodyText())
                                nativeAdSocialContext.setText(fanNativeAd.getAdSocialContext())
                                nativeAdCallToAction.visibility = if (fanNativeAd.hasCallToAction()) View.VISIBLE else View.INVISIBLE
                                nativeAdCallToAction.setText(fanNativeAd.getAdCallToAction())
                                sponsoredLabel.setText(fanNativeAd.getSponsoredTranslation())

                                // Create a list of clickable views
                                val clickableViews: MutableList<View> = ArrayList()
                                clickableViews.add(nativeAdTitle)
                                clickableViews.add(sponsoredLabel)
                                clickableViews.add(nativeAdIcon)
                                clickableViews.add(nativeAdMedia)
                                clickableViews.add(nativeAdBody)
                                clickableViews.add(nativeAdSocialContext)
                                clickableViews.add(nativeAdCallToAction)

                                // Register the Title and CTA button to listen for clicks.
                                fanNativeAd.registerViewForInteraction(nativeAdView, nativeAdIcon, nativeAdMedia, clickableViews)
                            }
                            override fun onAdClicked(ad: Ad) {}
                            override fun onLoggingImpression(ad: Ad) {}
                        }

                        val loadAdConfig: NativeLoadAdConfig = fanNativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build()
                        fanNativeAd.loadAd(loadAdConfig)
                    }
                }
            }


        }

        private fun loadBackupNativeAd() {

            when (backupAdNetwork) {
                Constant.ADMOB -> {
                    if (admobNativeAd.getVisibility() != View.VISIBLE) {
//                        val adMobNativeId = "/6499/example/native"

                        val adLoader = AdLoader.Builder(activity, adMobNativeId)
                                .forNativeAd { NativeAd: com.google.android.gms.ads.nativead.NativeAd? ->
                                    if (darkTheme) {
                                        val colorDrawable = ColorDrawable(ContextCompat.getColor(activity, nativeBackgroundDark))
                                        val styles: NativeTemplateStyle = NativeTemplateStyle.Builder().withMainBackgroundColor(colorDrawable).build()
                                        admobNativeAd.setStyles(styles)
                                        admobNativeBackground.setBackgroundResource(nativeBackgroundDark)
                                    } else {
                                        val colorDrawable = ColorDrawable(ContextCompat.getColor(activity, nativeBackgroundLight))
                                        val styles: NativeTemplateStyle = NativeTemplateStyle.Builder().withMainBackgroundColor(colorDrawable).build()
                                        admobNativeAd.setStyles(styles)
                                        admobNativeBackground.setBackgroundResource(nativeBackgroundLight)
                                    }
                                    mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                                    if (NativeAd != null) {
                                        admobNativeAd.setNativeAd(NativeAd)
                                    }
                                    admobNativeAd.setVisibility(View.VISIBLE)
                                    nativeAdViewContainer.visibility = View.VISIBLE
                                }
                                .withAdListener(object : AdListener() {
                                    override fun onAdFailedToLoad(adError: LoadAdError) {
                                        Log.e(TAG, "AdMob onAdFailedToLoad : $adError")
                                        admobNativeAd.setVisibility(View.GONE)
                                        nativeAdViewContainer.visibility = View.GONE
                                    }

                                    override fun onAdLoaded() {
                                        super.onAdLoaded()
                                        Log.e(TAG, "AdMob onAdLoaded :")
                                    }
                                }).build()
                        adLoader.loadAd(Tools.getAdRequest(activity, legacyGDPR))
                    }else {
                        Log.e(TAG, "AdMob Native Ad has been loaded")
                    }
                }
                Constant.FACEBOOK -> {

                }
                Constant.NONE -> {
                    nativeAdViewContainer.visibility = View.GONE
                }
            }
        }

        fun setNativeAdPadding(left: Int, top: Int, right: Int, bottom: Int) {
            nativeAdViewContainer = activity.findViewById(R.id.native_ad_view_container)
            nativeAdViewContainer.setPadding(left, top, right, bottom)
            if (darkTheme) {
                nativeAdViewContainer.setBackgroundColor(ContextCompat.getColor(activity, nativeBackgroundDark))
            } else {
                nativeAdViewContainer.setBackgroundColor(ContextCompat.getColor(activity, nativeBackgroundLight))
            }
        }
        fun setNativeAdMargin(left: Int, top: Int, right: Int, bottom: Int) {
            nativeAdViewContainer = activity.findViewById(R.id.native_ad_view_container)
            setMargins(nativeAdViewContainer, left, top, right, bottom)
        }
        fun setMargins(view: View, left: Int, top: Int, right: Int, bottom: Int) {
            if (view.layoutParams is MarginLayoutParams) {
                val p = view.layoutParams as MarginLayoutParams
                p.setMargins(left, top, right, bottom)
                view.requestLayout()
            }
        }
        fun setNativeAdBackgroundResource(drawableBackground: Int) {
            nativeAdViewContainer = activity.findViewById(R.id.native_ad_view_container)
            nativeAdViewContainer.setBackgroundResource(drawableBackground)
        }
    }
}
