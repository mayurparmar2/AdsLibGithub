package com.demo.adslibss.mylib

import android.app.Activity
import com.demo.adslibss.DataModel.Ads
import com.demo.adslibss.Utils

class MyAdsManager(val activity: Activity) {
    val adsData: Ads = Utils.adsData.ads
    val adNetwork: AdNetwork.Initialize = AdNetwork.Initialize(activity)
    val bannerAd: BannerAd.Builder = BannerAd.Builder(activity)
    var interstitialAd: MyInterstitialAd.Builder = MyInterstitialAd.Builder(activity)

    fun initializationAd() {
        if (adsData.enable) {
            this.adNetwork.setAdStatus(Constant.AD_STATUS_ON)
                    .setAdNetwork(this.adsData.main_ads)
                    .setBackupAdNetwork(this.adsData.backup_ads)
                    .setUnityGameId(this.adsData.ads_unit_id.unity.game_id)
                    .setDebug(false)
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


}