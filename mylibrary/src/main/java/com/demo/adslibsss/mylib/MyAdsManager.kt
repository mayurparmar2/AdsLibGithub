package com.demo.adslibsss.mylib

import android.app.Activity
import android.view.ViewGroup
import com.ads.adslib.R
import com.demo.adslibsss.DataModel.Ads
import com.demo.adslibsss.mylib.utils.Utils

class MyAdsManager(val activity: Activity) {
    var appOpenAdBuilder: AppOpenAd.Builder? = null
    val adsData: Ads = Utils.adsData.ads
    val adNetwork: AdNetwork.Initialize = AdNetwork.Initialize(activity)
    val bannerAd: BannerAd.Builder = BannerAd.Builder(activity)
    var interstitialAd: MyInterstitialAd.Builder = MyInterstitialAd.Builder(activity)
    var nativeAd: NativeAd.Builder = NativeAd.Builder(activity)


    fun initializationAd() {
        if (adsData.enable) {
            this.adNetwork.setAdStatus(Constant.AD_STATUS_ON)
                    .setAdNetwork(this.adsData.main_ads)
                    .setBackupAdNetwork(this.adsData.backup_ads)
                    .setUnityGameId(this.adsData.ads_unit_id.unity.game_id)
                    .setDebug(true)
                    .build()
        }
    }

    fun loadBannerAd() {
        if (adsData.enable) {
            bannerAd.setAdStatus(Constant.AD_STATUS_ON)
                    .setAdNetwork(this.adsData.main_ads)
                    .setBackupAdNetwork(this.adsData.backup_ads)
                    .setAdMobBannerId(this.adsData.ads_unit_id.admob.banner_id)
                    .setFanBannerId(this.adsData.ads_unit_id.facebook.banner_id)
                    .setUnityBannerId(this.adsData.ads_unit_id.unity.banner_id)
                    .setDarkTheme(false)
                    .build()
        }
    }

    @JvmOverloads
    fun loadInterAd(interval: Int = this.adsData.counter) {
        if (adsData.enable) {
            this.interstitialAd.setAdStatus(Constant.AD_STATUS_ON)
                    .setAdNetwork(this.adsData.main_ads)
                    .setBackupAdNetwork(this.adsData.backup_ads)
                    .setAdMobInterstitialId(this.adsData.ads_unit_id.admob.interstitial_id)
                    .setFanInterstitialId(this.adsData.ads_unit_id.facebook.interstitial_id)
                    .setUnityInterstitialId(this.adsData.ads_unit_id.unity.interstitial_id)
                    .setInterval(interval)
                    .build()
        }
    }
    fun showInterAd() {
        if (adsData.enable) {
            interstitialAd.show()
        }
    }


    fun loadNativeAdView(nativeAdViewContainer: ViewGroup) {
        if (adsData.enable) {
            this.nativeAd.setAdStatus(Constant.AD_STATUS_ON)
                    .setAdNetwork(this.adsData.main_ads)
                    .setBackupAdNetwork(this.adsData.backup_ads)
                    .setAdMobNativeId(this.adsData.ads_unit_id.admob.native_id)
                    .setFanNativeId(this.adsData.ads_unit_id.facebook.native_id)
                    .setNativeAdBackgroundColor(R.color.colorNativeBackgroundLight, R.color.colorNativeBackgroundDark)
                    .setNativeAdStyle(nativeAdViewContainer,Constant.STYLE_VIDEO_SMALL)
                    .build()
        }
    }

    fun loadOpenAds() {
//        if (Constant.OPEN_ADS_ON_START) {
            appOpenAdBuilder = AppOpenAd.Builder(activity)
                    .setAdStatus(Constant.AD_STATUS_ON)
                    .setAdNetwork(this.adsData.main_ads)
                    .setBackupAdNetwork(this.adsData.backup_ads)
                    .setAdMobAppOpenId(Constant.ADMOB_APP_OPEN_AD_ID)
                    .setAdManagerAppOpenId(Constant.GOOGLE_AD_MANAGER_APP_OPEN_AD_ID)
                    .build {
//                        this.startMainActivity()

                    }
//        }
    }
    companion object{

    }

}