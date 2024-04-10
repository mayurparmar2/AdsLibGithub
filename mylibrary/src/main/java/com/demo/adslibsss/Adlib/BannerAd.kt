package com.demo.adslibsss.Adlib

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.ads.adslib.R
import com.demo.adslibsss.Adlib.utils.AdUtils
import com.demo.adslibsss.Adlib.Constant.UNITY_ADS_BANNER_HEIGHT_MEDIUM
import com.demo.adslibsss.Adlib.Constant.UNITY_ADS_BANNER_WIDTH_MEDIUM
import com.demo.adslibsss.Adlib.utils.Tools
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.AdView.AdViewLoadConfig
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.unity3d.services.banners.BannerErrorInfo
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.BannerView.IListener
import com.unity3d.services.banners.UnityBannerSize

class BannerAd{

    companion object {
        private const val TAG = "AdNetwork:BannerAd"
    }

    class Builder(private val activity: Activity) {
        private var adView: AdView? = null
//        private var adManagerAdView: AdManagerAdView? = null
        private var fanAdView: com.facebook.ads.AdView? = null
        private var adStatus = true
        private var adNetwork = ""
        private var backupAdNetwork = ""
        private var adMobBannerId = ""
        private var googleAdManagerBannerId = ""
        private var fanBannerId = ""
        private var unityBannerId = ""
        private var placementStatus = 1
        private var darkTheme = false
        private var legacyGDPR = false
        fun build(): Builder {
            loadBannerAd()
            return this
        }
        fun setDarkTheme(booleanValue: Boolean): Builder {
            darkTheme = booleanValue
            return this
        }

        fun setAdStatus(adStatus: Boolean): Builder {
            this.adStatus = adStatus
            return this
        }

        fun setAdNetwork(adNetwork: String): Builder {
            this.adNetwork = adNetwork
            return this
        }

        fun setBackupAdNetwork(backupAdNetwork: String): Builder {
            this.backupAdNetwork = backupAdNetwork
            return this
        }

        fun setAdMobBannerId(adMobBannerId: String): Builder {
            this.adMobBannerId = adMobBannerId
            return this
        }

        fun setFanBannerId(fanBannerId: String): Builder {
            this.fanBannerId = fanBannerId
            return this
        }

        fun setUnityBannerId(unityBannerId: String): Builder {
            this.unityBannerId = unityBannerId
            return this
        }
        fun loadBannerAd() {
            if (adStatus) {
                when (adNetwork) {
                    Constant.ADMOB->{
                        val adContainerView = activity.findViewById<FrameLayout>(R.id.admob_banner_view_container)
                        adContainerView.post {
                            adView = AdView(activity)
                            adView!!.adUnitId = adMobBannerId
                            adContainerView.removeAllViews()
                            adContainerView.addView(adView)
                            adView!!.setAdSize(AdUtils.getAdSize(activity))
                            adView!!.loadAd(Tools.getAdRequest(activity, legacyGDPR))
                            adView!!.adListener = object : AdListener() {
                                override fun onAdLoaded() {
                                    Log.e(TAG, "loadBannerAd() : $adNetwork Banner onAdLoaded:")
                                    adContainerView.visibility = View.VISIBLE
                                }
                                override fun onAdFailedToLoad(adError: LoadAdError) {
                                    Log.e(TAG, "$adNetwork Banner onAdFailedToLoad : ${adError.message}")
                                    adContainerView.visibility = View.GONE
                                    loadBackupBannerAd()
                                }
                                override fun onAdOpened() {}
                                override fun onAdClicked() {}
                                override fun onAdClosed() {}
                            }
                        }
                        Log.e(TAG, "$adNetwork Banner Ad unit Id : $adMobBannerId")
                    }
                    Constant.FACEBOOK->{
//                        fanBannerId = "IMG_16_9_LINK#YOUR_PLACEMENT_ID"
                        fanAdView = com.facebook.ads.AdView(activity, fanBannerId, com.facebook.ads.AdSize.BANNER_HEIGHT_50)
                        fanAdView?.let {
                            val fanAdViewContainer = activity.findViewById<RelativeLayout>(R.id.fan_banner_view_container)
                            fanAdViewContainer.addView(fanAdView)
                            val adListener: com.facebook.ads.AdListener = object : com.facebook.ads.AdListener {
                                override fun onError(ad: Ad, adError: AdError) {
                                    Log.e(TAG, "loadBannerAd() onError : ${adError.errorMessage}")
                                    fanAdViewContainer.visibility = View.GONE
                                    loadBackupBannerAd()
                                    Log.e(TAG, "Error load FAN : " + adError.errorMessage)
                                }

                                override fun onAdLoaded(ad: Ad) {
                                    fanAdViewContainer.visibility = View.VISIBLE
                                }

                                override fun onAdClicked(ad: Ad) {}
                                override fun onLoggingImpression(ad: Ad) {}
                            }
                            val loadAdConfig: AdViewLoadConfig = it.buildLoadAdConfig().withAdListener(adListener).build()
                            it.loadAd(loadAdConfig)
                        }
                    }
                    Constant.UNITY->{
                        val unityAdView = activity.findViewById<RelativeLayout>(R.id.unity_banner_view_container)
                        val bottomBanner = BannerView(activity, unityBannerId, UnityBannerSize(UNITY_ADS_BANNER_WIDTH_MEDIUM, UNITY_ADS_BANNER_HEIGHT_MEDIUM))
                        bottomBanner.listener = object : IListener {
                            override fun onBannerLoaded(bannerView: BannerView) {
                                unityAdView.visibility = View.VISIBLE
                                Log.e("Unity_banner", "ready")
                            }
                            override fun onBannerClick(bannerView: BannerView) {}
                            override fun onBannerFailedToLoad(bannerView: BannerView, bannerErrorInfo: BannerErrorInfo) {
                                Log.e("SupportTest", "Banner Error$bannerErrorInfo")
                                unityAdView.visibility = View.GONE
                                loadBackupBannerAd()
                            }

                            override fun onBannerLeftApplication(bannerView: BannerView) {}
                        }
                        unityAdView.addView(bottomBanner)
                        bottomBanner.load()
                        Log.e(TAG, "$adNetwork Banner Ad unit Id : $unityBannerId")

                    }
                }
                Log.e(TAG, "Banner Ad is enabled")
            } else {
                Log.e(TAG, "Banner Ad is disabled")
            }
        }

        fun loadBackupBannerAd() {
            if (adStatus) {
                when (backupAdNetwork) {
                    Constant.ADMOB->{
                        val adContainerView = activity.findViewById<FrameLayout>(R.id.admob_banner_view_container)
                        adContainerView.post {
                            adView = AdView(activity)
                            adView!!.adUnitId = adMobBannerId
                            adContainerView.removeAllViews()
                            adContainerView.addView(adView)
                            adView!!.setAdSize(AdUtils.getAdSize(activity))
                            adView!!.loadAd(Tools.getAdRequest(activity, legacyGDPR))
                            adView!!.adListener = object : AdListener() {
                                override fun onAdLoaded() {
                                    Log.e(TAG, "loadBackupBannerAd() : $backupAdNetwork Banner onAdLoaded:")
                                    adContainerView.visibility = View.VISIBLE
                                }
                                override fun onAdFailedToLoad(adError: LoadAdError) {
                                    Log.e(TAG, "loadBackupBannerAd() : $backupAdNetwork Banner LoadAdError:${adError.message}")

                                    adContainerView.visibility = View.GONE

                                }
                                override fun onAdOpened() {}
                                override fun onAdClicked() {}
                                override fun onAdClosed() {}
                            }
                        }
                        Log.e(TAG, "$adNetwork Banner Ad unit Id : $adMobBannerId")
                    }
                    Constant.FACEBOOK->{
                        fanAdView = com.facebook.ads.AdView(activity, fanBannerId, com.facebook.ads.AdSize.BANNER_HEIGHT_50)
                        fanAdView?.let {
                            val fanAdViewContainer = activity.findViewById<RelativeLayout>(R.id.fan_banner_view_container)
                            fanAdViewContainer.addView(fanAdView)
                            val adListener: com.facebook.ads.AdListener = object : com.facebook.ads.AdListener {
                                override fun onError(ad: Ad, adError: AdError) {
                                    fanAdViewContainer.visibility = View.GONE
                                    Log.e(TAG, "Error load FAN : " + adError.errorMessage)
                                }

                                override fun onAdLoaded(ad: Ad) {
                                    fanAdViewContainer.visibility = View.VISIBLE
                                }

                                override fun onAdClicked(ad: Ad) {}
                                override fun onLoggingImpression(ad: Ad) {}
                            }
                            val loadAdConfig: AdViewLoadConfig = it.buildLoadAdConfig().withAdListener(adListener).build()
                            it.loadAd(loadAdConfig)
                        }
                    }
                    Constant.UNITY->{
                        val unityAdView = activity.findViewById<RelativeLayout>(R.id.unity_banner_view_container)
                        val bottomBanner = BannerView(activity, unityBannerId, UnityBannerSize(UNITY_ADS_BANNER_WIDTH_MEDIUM, UNITY_ADS_BANNER_HEIGHT_MEDIUM))
                        bottomBanner.listener = object : IListener {
                            override fun onBannerLoaded(bannerView: BannerView) {
                                unityAdView.visibility = View.VISIBLE
                                Log.e("Unity_banner", "ready")
                            }
                            override fun onBannerClick(bannerView: BannerView) {}
                            override fun onBannerFailedToLoad(bannerView: BannerView, bannerErrorInfo: BannerErrorInfo) {
                                Log.e(TAG, "Failed loadBackupBannerAd Error :${bannerErrorInfo.errorMessage}")
                                unityAdView.visibility = View.GONE
                            }

                            override fun onBannerLeftApplication(bannerView: BannerView) {}
                        }
                        unityAdView.addView(bottomBanner)
                        bottomBanner.load()
                        Log.e(TAG, "$adNetwork loadBackupBannerAdBanner Ad unit Id : $unityBannerId")

                    }
                }
                Log.e(TAG, "Banner Ad is enabled")
            } else {
                Log.e(TAG, "Banner Ad is disabled")
            }
        }


    }
}
