package com.demo.adslibsss.mylib

import android.app.Activity
import android.util.Log
import com.demo.adslibsss.mylib.fb.AudienceNetworkInitializeHelper
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.AdapterStatus
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.UnityAds
import com.unity3d.ads.UnityAds.UnityAdsInitializationError


public class AdNetwork {
    companion object{
        private const val TAG = "AdNetwork"
    }


    class Initialize(var activity: Activity) {
        private var adStatus = ""
        private var adNetwork = ""
        private var backupAdNetwork = ""
        private var adMobAppId = ""
        private var unityGameId = ""
        private var debug = true
        fun setAdMobAppId(adMobAppId: String): Initialize {
            this.adMobAppId = adMobAppId
            return this
        }

        fun setAdNetwork(adNetwork: String): Initialize {
            this.adNetwork = adNetwork
            return this
        }

        fun setAdStatus(adStatus: String): Initialize {
            this.adStatus = adStatus
            return this
        }
        fun setBackupAdNetwork(backupAdNetwork: String): Initialize {
            this.backupAdNetwork = backupAdNetwork
            return this
        }

        fun setDebug(isdebug: Boolean): Initialize {
            this.debug = isdebug
            return this
        }

        fun setUnityGameId(unityGameId: String): Initialize {
            this.unityGameId = unityGameId
            return this
        }


        fun build(): Initialize {
            initAds()
            initBackupAds()
            return this
        }

        fun initAds() {
            if (adStatus == Constant.AD_STATUS_ON) {
                when (adNetwork) {
                    Constant.ADMOB ->{
                        MobileAds.initialize(activity) {
                            val statusMap: Map<String, AdapterStatus> = it.getAdapterStatusMap()
                            for (adapterClass in statusMap.keys) {
                                val adapterStatus = statusMap[adapterClass]!!
                                Log.d(TAG, String.format("Adapter name: %s, Description: %s, Latency: %d", adapterClass, adapterStatus!!.description, adapterStatus!!.latency))
                            }
                        }
                    }
                    Constant.FACEBOOK ->{
                        AudienceNetworkInitializeHelper.initializeAd(activity, debug)
                    }
                    Constant.UNITY ->{
                        UnityAds.initialize(activity.applicationContext, unityGameId, debug, object : IUnityAdsInitializationListener {
                            override fun onInitializationComplete() {
                                Log.e(TAG, "Unity Ads Initialization Complete with ID : $unityGameId")
                            }

                            override fun onInitializationFailed(error: UnityAdsInitializationError, message: String) {
                                Log.e(TAG, "Unity Ads Initialization Failed: [$error] $message")
                            }
                        })
                    }
                }
                Log.d(TAG, "[$adNetwork] is selected as Primary Ads")
            }
        }

        fun initBackupAds() {
            if (adStatus == Constant.AD_STATUS_ON) {
                when (backupAdNetwork) {
                    Constant.ADMOB ->{
                        MobileAds.initialize(activity) {
                            val statusMap: Map<String, AdapterStatus> = it.getAdapterStatusMap()
                            for (adapterClass in statusMap.keys) {
                                val adapterStatus = statusMap[adapterClass]!!
                                Log.d(TAG, String.format("Adapter name: %s, Description: %s, Latency: %d", adapterClass, adapterStatus!!.description, adapterStatus!!.latency))
                            }
                        }

                    }
                    Constant.FACEBOOK ->{
                        AudienceNetworkInitializeHelper.initializeAd(activity, debug)
                    }
                    Constant.UNITY ->{
                        UnityAds.initialize(activity.applicationContext, unityGameId, debug, object : IUnityAdsInitializationListener {
                            override fun onInitializationComplete() {
                                Log.e(TAG, "Unity Ads Initialization Complete with ID : $unityGameId")
                            }
                            override fun onInitializationFailed(error: UnityAdsInitializationError, message: String) {
                                Log.e(TAG, "Unity Ads Initialization Failed: [$error] $message")
                            }
                        })
                    }
                }
                Log.d(TAG, "[$backupAdNetwork] is selected as Backup Ads")
            }
        }
    }

}