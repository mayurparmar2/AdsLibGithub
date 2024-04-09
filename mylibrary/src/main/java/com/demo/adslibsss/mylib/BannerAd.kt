package com.demo.adslibsss.mylib

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.ads.adslib.R
import com.demo.adslibsss.mylib.utils.Utils
import com.demo.adslibsss.mylib.Constant.UNITY_ADS_BANNER_HEIGHT_MEDIUM
import com.demo.adslibsss.mylib.Constant.UNITY_ADS_BANNER_WIDTH_MEDIUM
import com.demo.adslibsss.mylib.utils.Tools
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

//        @JvmStatic
//        fun Builder(activity: Activity) = BannerAd.Builder(activity)
    }

    class Builder(private val activity: Activity) {
        private var adView: AdView? = null
//        private var adManagerAdView: AdManagerAdView? = null

        private var fanAdView: com.facebook.ads.AdView? = null
        private var adStatus = ""
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

        fun setAdStatus(adStatus: String): Builder {
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
            if (adStatus == "AD_STATUS_ON") {
                when (adNetwork) {
                    Constant.ADMOB->{
                        val adContainerView = activity.findViewById<FrameLayout>(R.id.admob_banner_view_container)
                        adContainerView.post {
                            adView = AdView(activity)
                            adView!!.adUnitId = adMobBannerId
                            adContainerView.removeAllViews()
                            adContainerView.addView(adView)
                            adView!!.setAdSize(Utils.getAdSize(activity))
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
            if (adStatus == "AD_STATUS_ON") {
                when (backupAdNetwork) {
                    Constant.ADMOB->{
                        val adContainerView = activity.findViewById<FrameLayout>(R.id.admob_banner_view_container)
                        adContainerView.post {
                            adView = AdView(activity)
                            adView!!.adUnitId = adMobBannerId
                            adContainerView.removeAllViews()
                            adContainerView.addView(adView)
                            adView!!.setAdSize(Utils.getAdSize(activity))
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


//
//import android.app.Activity
//import android.util.Log
//import android.view.View
//import android.widget.FrameLayout
//import com.ads.adslib.R
//import com.google.android.gms.ads.AdListener
//import com.google.android.gms.ads.AdRequest
//import com.google.android.gms.ads.AdSize
//import com.google.android.gms.ads.AdView
//import com.google.android.gms.ads.LoadAdError
//import com.google.android.gms.ads.admanager.AdManagerAdView
//
//class BannerAd private constructor(private val activity: Activity) {
//
//    private var adView: AdView? = null
//    private var adManagerAdView: AdManagerAdView? = null
//    private lateinit var adStatus: String
//    private lateinit var adNetwork: String
//    private lateinit var backupAdNetwork: String
//    private lateinit var adMobBannerId: String
//    private lateinit var googleAdManagerBannerId: String
//    private lateinit var fanBannerId: String
//    private lateinit var unityBannerId: String
//    private lateinit var appLovinBannerId: String
//    private lateinit var appLovinBannerZoneId: String
//    private lateinit var mopubBannerId: String
//    private lateinit var ironSourceBannerId: String
//    private lateinit var wortiseBannerId: String
//    private lateinit var alienAdsBannerId: String
//    private var placementStatus = 1
//    private var darkTheme = false
//    private var legacyGDPR = false
//
//    class Builder(private val activity: Activity) {
//
//        private val bannerAd = BannerAd(activity)
//
//        fun build(): Builder {
//            bannerAd.loadBannerAd()
//            return this
//        }
//
//        fun setAdStatus(adStatus: String): Builder {
//            bannerAd.adStatus = adStatus
//            return this
//        }
//
//        fun setAdNetwork(adNetwork: String): Builder {
//            bannerAd.adNetwork = adNetwork
//            return this
//        }
//
//        fun setBackupAdNetwork(backupAdNetwork: String): Builder {
//            bannerAd.backupAdNetwork = backupAdNetwork
//            return this
//        }
//
//        fun setAdMobBannerId(adMobBannerId: String): Builder {
//            bannerAd.adMobBannerId = adMobBannerId
//            return this
//        }
//
//        fun setGoogleAdManagerBannerId(googleAdManagerBannerId: String): Builder {
//            bannerAd.googleAdManagerBannerId = googleAdManagerBannerId
//            return this
//        }
//
//        fun setFanBannerId(fanBannerId: String): Builder {
//            bannerAd.fanBannerId = fanBannerId
//            return this
//        }
//
//        fun setUnityBannerId(unityBannerId: String): Builder {
//            bannerAd.unityBannerId = unityBannerId
//            return this
//        }
//
//        fun setAppLovinBannerId(appLovinBannerId: String): Builder {
//            bannerAd.appLovinBannerId = appLovinBannerId
//            return this
//        }
//
//        fun setAppLovinBannerZoneId(appLovinBannerZoneId: String): Builder {
//            bannerAd.appLovinBannerZoneId = appLovinBannerZoneId
//            return this
//        }
//
//        fun setMopubBannerId(mopubBannerId: String): Builder {
//            bannerAd.mopubBannerId = mopubBannerId
//            return this
//        }
//
//        fun setIronSourceBannerId(ironSourceBannerId: String): Builder {
//            bannerAd.ironSourceBannerId = ironSourceBannerId
//            return this
//        }
//
//        fun setWortiseBannerId(wortiseBannerId: String): Builder {
//            bannerAd.wortiseBannerId = wortiseBannerId
//            return this
//        }
//
//        fun setAlienAdsBannerId(alienAdsBannerId: String): Builder {
//            bannerAd.alienAdsBannerId = alienAdsBannerId
//            return this
//        }
//
//        fun setPlacementStatus(placementStatus: Int): Builder {
//            bannerAd.placementStatus = placementStatus
//            return this
//        }
//
//        fun setDarkTheme(darkTheme: Boolean): Builder {
//            bannerAd.darkTheme = darkTheme
//            return this
//        }
//
//        fun setLegacyGDPR(legacyGDPR: Boolean): Builder {
//            bannerAd.legacyGDPR = legacyGDPR
//            return this
//        }
//
//        fun loadBannerAd() {
//            bannerAd.loadBannerAd()
//        }
//
//        fun loadBackupBannerAd() {
//            bannerAd.loadBackupBannerAd()
//        }
//
//        fun destroyAndDetachBanner() {
//            bannerAd.destroyAndDetachBanner()
//        }
//
//        fun buildBannerAd(): BannerAd {
//            return bannerAd
//        }
//    }
//
//    private fun loadBannerAd() {
//        if (::adStatus.isInitialized && adStatus == "AD_STATUS_ON" && placementStatus != 0) {
//            when (adNetwork) {
//                "ADMOB", "FAN_BIDDING_ADMOB" -> {
//                    val adContainerView = activity.findViewById<FrameLayout>(R.id.admob_banner_view_container)
//                    adContainerView.post {
//                        adView = AdView(activity)
//                        adView?.let {
//                            it.adUnitId = adMobBannerId
//                            adContainerView.removeAllViews()
//                            adContainerView.addView(it)
//                            it.adSize = AdSize.BANNER
//                            it.loadAd(AdRequest.Builder().build())
//                            it.adListener = object : AdListener() {
//                                override fun onAdLoaded() {
//                                    adContainerView.visibility = View.VISIBLE
//                                }
//
//                                override fun onAdFailedToLoad(adError: LoadAdError) {
//                                    adContainerView.visibility = View.GONE
//                                    loadBackupBannerAd()
//                                }
//                            }
//                        }
//                    }
//                }
//                "GOOGLE_AD_MANAGER", "FAN_BIDDING_AD_MANAGER" -> {
//                    val googleAdContainerView = activity.findViewById<FrameLayout>(R.id.google_ad_banner_view_container)
//                    googleAdContainerView.post {
//                        adManagerAdView = AdManagerAdView(activity)
//                        adManagerAdView?.let {
//                            it.adUnitId = googleAdManagerBannerId
//                            googleAdContainerView.removeAllViews()
//                            googleAdContainerView.addView(it)
//                            it.adSize = AdSize.BANNER
//                            it.loadAd(AdRequest.Builder().build())
//                            it.adListener = object : AdListener() {
//                                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
//                                    googleAdContainerView.visibility = View.GONE
//                                    loadBackupBannerAd()
//                                }
//
//                                override fun onAdLoaded() {
//                                    googleAdContainerView.visibility = View.VISIBLE
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            Log.e(TAG, "Banner Ad is enabled")
//        } else {
//            Log.e(TAG, "Banner Ad is disabled")
//        }
//    }
//
//    private fun loadBackupBannerAd() {
//        if (::adStatus.isInitialized && adStatus == "AD_STATUS_ON" && placementStatus != 0) {
//            when (backupAdNetwork) {
//                "ADMOB", "FAN_BIDDING_ADMOB" -> {
//                    val adContainerView = activity.findViewById<FrameLayout>(R.id.admob_banner_view_container)
//                    adContainerView.post {
//                        adView = AdView(activity)
//                        adView?.let {
//                            it.adUnitId = adMobBannerId
//                            adContainerView.removeAllViews()
//                            adContainerView.addView(it)
//                            it.adSize = AdSize.BANNER
//                            it.loadAd(AdRequest.Builder().build())
//                            it.adListener = object : AdListener() {
//                                override fun onAdLoaded() {
//                                    adContainerView.visibility = View.VISIBLE
//                                }
//
//                                override fun onAdFailedToLoad(adError: LoadAdError) {
//                                    adContainerView.visibility = View.GONE
//                                }
//                            }
//                        }
//                    }
//                }
//                "GOOGLE_AD_MANAGER", "FAN_BIDDING_AD_MANAGER" -> {
//                    val googleAdContainerView = activity.findViewById<FrameLayout>(R.id.google_ad_banner_view_container)
//                    googleAdContainerView.post {
//                        adManagerAdView = AdManagerAdView(activity)
//                        adManagerAdView?.let {
//                            it.adUnitId = googleAdManagerBannerId
//                            googleAdContainerView.removeAllViews()
//                            googleAdContainerView.addView(it)
//                            it.adSize = AdSize.BANNER
//                            it.loadAd(AdRequest.Builder().build())
//                            it.adListener = object : AdListener() {
//                                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
//                                    googleAdContainerView.visibility = View.GONE
//                                }
//
//                                override fun onAdLoaded() {
//                                    googleAdContainerView.visibility = View.VISIBLE
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            Log.e(TAG, "Banner Ad is enabled")
//        } else {
//            Log.e(TAG, "Banner Ad is disabled")
//        }
//    }
//
//    private fun destroyAndDetachBanner() {
//        // Implement your logic to destroy and detach banner ad views here
//    }
//
//    companion object {
//        private const val TAG = "AdNetwork"
//    }
//}
//
