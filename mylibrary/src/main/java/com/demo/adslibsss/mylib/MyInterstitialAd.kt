package com.demo.adslibsss.mylib

import android.app.Activity
import android.util.Log
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.InterstitialAdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.unity3d.ads.IUnityAdsLoadListener
import com.unity3d.ads.IUnityAdsShowListener
import com.unity3d.ads.UnityAds
import com.unity3d.ads.UnityAds.UnityAdsLoadError
import com.unity3d.ads.UnityAds.UnityAdsShowCompletionState
import com.unity3d.ads.UnityAds.UnityAdsShowError
import com.unity3d.ads.UnityAdsShowOptions
import java.util.Locale


class MyInterstitialAd private constructor(private val activity: Activity) {
    init {
        MobileAds.initialize(activity) {
        }
    }

    companion object {
        const val TAG = "MyInterstitialAd"
//        @JvmStatic
//        fun Builder(activity: Activity) = MyInterstitialAd.Builder(activity)

    }

    class Builder(private val activity: Activity) {
        private var isUnityLoaded: Boolean = false
        private var counter = 1
        private var interval = 3

        private var adStatus = ""
        private var adNetwork = ""
        private var backupAdNetwork = ""

        private var fanInterstitialId = ""
        private var adMobInterstitialId = ""
        private var unityInterstitialId = ""

        private var unityAdsLoadListener: IUnityAdsLoadListener? = null
        private var adMobInterstitialAd: InterstitialAd? = null
        private var fanInterstitialAd: com.facebook.ads.InterstitialAd? = null

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

        fun setAdMobInterstitialId(str: String): Builder {
            adMobInterstitialId = str
            return this
        }

        fun setFanInterstitialId(str: String): Builder {
            fanInterstitialId = str
            return this
        }

        fun setUnityInterstitialId(str: String): Builder {
            unityInterstitialId = str
            return this
        }
        fun setInterval(interval: Int): Builder {
            this.interval = interval
            return this
        }

        fun build(): Builder {
            loadInterstitialAd()
            return this
        }
        fun show() {
            showInterstitialAd()
        }



        private fun loadInterstitialAd() {
            if (adStatus == "AD_STATUS_ON") {
                when (adNetwork) {
                    Constant.ADMOB -> {
                        val adRequest = AdRequest.Builder().build()
//                        adMobInterstitialId = "ca-app-pub-4546362878369139/8641992830"
                        InterstitialAd.load(activity, adMobInterstitialId, adRequest, object : InterstitialAdLoadCallback() {
                            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                                adMobInterstitialAd = interstitialAd
                                adMobInterstitialAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
                                    override fun onAdDismissedFullScreenContent() {
                                        loadInterstitialAd()
                                    }

                                    override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                                        Log.e(TAG, "The ad failed to show.")
                                    }

                                    override fun onAdShowedFullScreenContent() {
                                        adMobInterstitialAd = null
                                        Log.e(TAG, "The ad was shown.")
                                    }
                                }
                                Log.e(TAG, "onAdLoaded")
                            }

                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                Log.e(TAG, loadAdError.message)
                                adMobInterstitialAd = null
                                loadBackupInterstitialAd()
                                Log.e(TAG, "Failed load AdMob Interstitial Ad")
                            }
                        })
                    }

                    Constant.FACEBOOK -> {
//                        fanInterstitialId = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID"
                        this.fanInterstitialAd = com.facebook.ads.InterstitialAd(activity, this.fanInterstitialId)
                        this.fanInterstitialAd!!.loadAd(this.fanInterstitialAd!!.buildLoadAdConfig().withAdListener(object : InterstitialAdListener {
                            override fun onAdClicked(ad: Ad) {}
                            override fun onInterstitialDisplayed(ad: Ad) {}
                            override fun onLoggingImpression(ad: Ad) {}
                            override fun onInterstitialDismissed(ad: Ad) {
                                this@Builder.fanInterstitialAd!!.loadAd()
                            }

                            override fun onError(ad: Ad, adError: AdError) {
                                Log.e(TAG, "facebook Interstitial is onError: ${adError.errorMessage}")
                                fanInterstitialAd = null
                                this@Builder.loadBackupInterstitialAd()
                            }

                            override fun onAdLoaded(ad: Ad) {
                                Log.d(TAG, "facebook Interstitial is loaded")
                            }
                        }).build())

                    }

                    Constant.UNITY -> {
                        unityAdsLoadListener = object : IUnityAdsLoadListener {
                            override fun onUnityAdsAdLoaded(placementId: String) {
                                Log.e(TAG, "unity interstitial ad loaded")
                            }

                            override fun onUnityAdsFailedToLoad(placementId: String, error: UnityAdsLoadError, message: String) {
                                Log.e(TAG, "Unity Ads failed to load ad : $unityInterstitialId : error : $message")
                                loadBackupInterstitialAd()
                            }
                        }
                        try {
                            UnityAds.load(unityInterstitialId, unityAdsLoadListener)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Log.e(TAG, "Unity : error : $e")

                        }
                    }
                }
            }

        }

        private fun loadBackupInterstitialAd() {
            if (adStatus == "AD_STATUS_ON") {
                when (backupAdNetwork) {
                    Constant.ADMOB -> {
                        val adRequest = AdRequest.Builder().build()
                        InterstitialAd.load(activity, adMobInterstitialId, adRequest, object : InterstitialAdLoadCallback() {
                            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                                adMobInterstitialAd = interstitialAd
                                adMobInterstitialAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
                                    override fun onAdDismissedFullScreenContent() {
                                        loadInterstitialAd()
                                    }

                                    override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                                        Log.e(TAG, "The ad failed to show.")
                                    }

                                    override fun onAdShowedFullScreenContent() {
                                        adMobInterstitialAd = null
                                        Log.e(TAG, "The ad was shown.")
                                    }
                                }
                                Log.e(TAG, "onAdLoaded")
                            }

                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                Log.e(TAG, loadAdError.message)
                                adMobInterstitialAd = null
//                                loadBackupInterstitialAd()
                                Log.e(TAG, "Failed load AdMob Interstitial Ad")
                            }
                        })
                    }

                    Constant.FACEBOOK -> {
                        this.fanInterstitialAd = com.facebook.ads.InterstitialAd(activity, this.fanInterstitialId)
                        this.fanInterstitialAd!!.loadAd(this.fanInterstitialAd!!.buildLoadAdConfig().withAdListener(object : InterstitialAdListener {
                            override fun onAdClicked(ad: Ad) {}
                            override fun onInterstitialDisplayed(ad: Ad) {}
                            override fun onLoggingImpression(ad: Ad) {}
                            override fun onInterstitialDismissed(ad: Ad) {
                                this@Builder.fanInterstitialAd!!.loadAd()
                            }
                            override fun onError(ad: Ad, adError: AdError) {
                                Log.d(TAG, "FAN Interstitial is onError")
                                fanInterstitialAd = null
                            }
                            override fun onAdLoaded(ad: Ad) {
                                Log.d(TAG, "loadBackupInterstitialAd(),$adNetwork Interstitial is loaded")
                            }
                        }).build())

                    }

                    Constant.UNITY -> {
                        unityAdsLoadListener = object : IUnityAdsLoadListener {
                            override fun onUnityAdsAdLoaded(placementId: String) {
                                isUnityLoaded = true
                                Log.e(TAG, "unity interstitial ad loaded")
                            }

                            override fun onUnityAdsFailedToLoad(placementId: String, error: UnityAdsLoadError, message: String) {
                                Log.e(TAG, "Unity Ads failed to load ad : $unityInterstitialId : error : $message")
                            }
                        }
                        try {
//                            unityInterstitialId = ""
                            UnityAds.load(unityInterstitialId, unityAdsLoadListener)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Log.e(TAG, "Unity : error : $e")

                        }
                    }
                }

            }
        }



        private fun showInterstitialAd() {
            if (adStatus != Constant.AD_STATUS_ON) {
                return
            }
            if (counter == interval) {
                when (adNetwork) {
                    Constant.ADMOB -> {
                        if (adMobInterstitialAd != null) {
                            adMobInterstitialAd!!.show(activity)
                            Log.e(TAG, "admob interstitial not null")
                        } else {
                            showBackupInterstitialAd()
                            Log.e(TAG, "admob interstitial null")
                        }
                    }
                    Constant.FACEBOOK -> {
                        if(fanInterstitialAd==null){
                            showBackupInterstitialAd()
                            Log.d(TAG, "fan interstitial null")
                        }else{
                            if(fanInterstitialAd!!.isAdLoaded){
                                fanInterstitialAd!!.show()
                            }
                        }
                    }
                    Constant.UNITY -> {
                        val showListener: IUnityAdsShowListener = object : IUnityAdsShowListener {
                            override fun onUnityAdsShowFailure(placementId: String, error: UnityAdsShowError, message: String) {
                                Log.d(TAG, "unity ads show failure")
                                showBackupInterstitialAd()
                            }
                            override fun onUnityAdsShowStart(placementId: String) {}
                            override fun onUnityAdsShowClick(placementId: String) {}
                            override fun onUnityAdsShowComplete(placementId: String, state: UnityAdsShowCompletionState) {
                                loadInterstitialAd()
                                isUnityLoaded = false
                            }
                        }
                        try {
                            if(isUnityLoaded) {
                                UnityAds.show(activity, unityInterstitialId, UnityAdsShowOptions(), showListener)
                            }
                        }catch (e:Exception){
                            Log.e(TAG, "Exception: showBackupInterstitialAd(): UNITY :  $e")
                        }
                    }
                }
                counter = 1
            } else {
                counter += 1
            }

        }

        private fun showBackupInterstitialAd() {
            if (adStatus != Constant.AD_STATUS_ON) {
                return
            }
            Log.e(TAG, "Show Backup Interstitial Ad [" + backupAdNetwork.uppercase(Locale.getDefault()) + "]")
            when (backupAdNetwork) {
                Constant.ADMOB -> {
                    adMobInterstitialAd?.apply {
                        show(activity)
                    }
                }

                Constant.FACEBOOK -> {
                    fanInterstitialAd?.isAdLoaded.apply {
                        show()
                    }
                }

                Constant.UNITY -> {
                    val showListener: IUnityAdsShowListener = object : IUnityAdsShowListener {
                        override fun onUnityAdsShowFailure(placementId: String, error: UnityAdsShowError, message: String) {
                            Log.e(TAG, "unity ads show failure: $message, UnityAdsShowError: $error, placementId : $placementId")
                        }
                        override fun onUnityAdsShowStart(placementId: String) {}
                        override fun onUnityAdsShowClick(placementId: String) {}
                        override fun onUnityAdsShowComplete(placementId: String, state: UnityAdsShowCompletionState) {
                            loadInterstitialAd()
                            isUnityLoaded = false
                        }
                    }
                    try {
                        if(isUnityLoaded) {
                            UnityAds.show(activity, unityInterstitialId, UnityAdsShowOptions(), showListener)
                        }
                    }catch (e:Exception){
                        Log.e(TAG, "Exception: showBackupInterstitialAd(): UNITY :  $e")
                    }
                }
            }
        }

    }
}