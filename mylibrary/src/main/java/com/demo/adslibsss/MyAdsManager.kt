package com.demo.adslibsss

import android.app.Activity
import android.view.ViewGroup
import com.ads.adslib.R
import com.demo.adslibsss.Adlib.AdNetwork
import com.demo.adslibsss.Adlib.AppOpenAd
import com.demo.adslibsss.Adlib.BannerAd
import com.demo.adslibsss.Adlib.Constant
import com.demo.adslibsss.Adlib.MyInterstitialAd
import com.demo.adslibsss.Adlib.NativeAd
import com.demo.adslibsss.model.Ads
import com.demo.adslibsss.Adlib.utils.AdUtils

class MyAdsManager(val activity: Activity) {
    var appOpenAdBuilder: AppOpenAd.Builder? = null
    val adsData: Ads = AdUtils.adsData.ads
    val adNetwork: AdNetwork.Initialize = AdNetwork.Initialize(activity)
    val bannerAd: BannerAd.Builder = BannerAd.Builder(activity)
    var interstitialAd: MyInterstitialAd.Builder = MyInterstitialAd.Builder(activity)
    var nativeAd: NativeAd.Builder = NativeAd.Builder(activity)


    fun initializationAd() {
        if (adsData.enable) {
            this.adNetwork.setAdStatus(this.adsData.enable)
                    .setAdNetwork(this.adsData.main_ads)
                    .setBackupAdNetwork(this.adsData.backup_ads)
                    .setUnityGameId(this.adsData.ads_unit_id.unity.game_id)
//                    .setUnityGameId("5331426")
                    .setDebug(true)
                    .build()
        }
    }

    fun loadBannerAd() {
        if (adsData.enable) {
            bannerAd.setAdStatus(this.adsData.enable)
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
            this.interstitialAd.setAdStatus(this.adsData.enable)
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
            this.nativeAd.setAdStatus(this.adsData.enable)
                    .setAdNetwork(this.adsData.main_ads)
                    .setBackupAdNetwork(this.adsData.backup_ads)
                    .setAdMobNativeId(this.adsData.ads_unit_id.admob.native_id)
                    .setFanNativeId(this.adsData.ads_unit_id.facebook.native_id)
                    .setNativeAdBackgroundColor(R.color.colorNativeBackgroundLight, R.color.colorNativeBackgroundDark)
                    .setNativeAdStyle(nativeAdViewContainer, Constant.STYLE_VIDEO_SMALL)
                    .build()
        }
    }

    @JvmOverloads
    fun loadOpenAds(callback: () -> Unit) {
        if (adsData.enable) {
            appOpenAdBuilder = AppOpenAd.Builder(activity)
                    .setAdStatus(this.adsData.enable)
                    .setAdNetwork(this.adsData.main_ads)
                    .setBackupAdNetwork(this.adsData.backup_ads)
                    .setAdMobAppOpenId(Constant.ADMOB_APP_OPEN_AD_ID)
                    .setAdManagerAppOpenId(Constant.GOOGLE_AD_MANAGER_APP_OPEN_AD_ID)
                    .build {
                        callback()
//                        this.startMainActivity()
                    }
        }
    }


    companion object{

    }

}