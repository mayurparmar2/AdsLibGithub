package com.demo.adslibsss.Adlib

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import com.demo.adslibsss.Adlib.Constant.ADMOB
import com.demo.adslibsss.Adlib.Constant.FAN_BIDDING_ADMOB
import com.demo.adslibsss.Adlib.Constant.FAN_BIDDING_AD_MANAGER
import com.demo.adslibsss.Adlib.Constant.GOOGLE_AD_MANAGER
import com.demo.adslibsss.Adlib.utils.OnShowAdCompleteListener
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback

class AppOpenAd {

    companion object{

        @JvmField
        var isAppOpenAdLoaded: Boolean = false

        @JvmField
        var appOpenAd: AppOpenAd? = null
    }

    class Builder(activity: Activity) {
        private val activity: Activity
        private var adStatus = true
//        private var adNetwork = "google_ad_manager"
        private var adNetwork = ""
//        private var backupAdNetwork = "admob"
        private var backupAdNetwork = ""
//        private var adMobAppOpenId = "ca-app-pub-3940256099942544/9257395921"
        private var adMobAppOpenId = ""
//        private var adManagerAppOpenId = "/6499/example/app-open"
        private var adManagerAppOpenId = ""

        init {
            this.activity = activity
        }

        fun build(): Builder {
            loadAppOpenAd()
            return this
        }

        fun build(onShowAdCompleteListener: OnShowAdCompleteListener): Builder {
            loadAppOpenAd(onShowAdCompleteListener)
            return this
        }

        fun show(): Builder {
            showAppOpenAd()
            return this
        }

        fun show(onShowAdCompleteListener: OnShowAdCompleteListener): Builder {
            showAppOpenAd(onShowAdCompleteListener)
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

        fun setAdMobAppOpenId(adMobAppOpenId: String): Builder {
            this.adMobAppOpenId = adMobAppOpenId
            return this
        }

        fun setAdManagerAppOpenId(adManagerAppOpenId: String): Builder {
            this.adManagerAppOpenId = adManagerAppOpenId
            return this
        }
        fun destroyOpenAd() {
            isAppOpenAdLoaded = false
            if (adStatus) {
                when (adNetwork) {
                    ADMOB-> if (appOpenAd != null) {
                        appOpenAd = null
                    }
                    else -> {

                    }
                }
            }
        }


        fun loadAppOpenAd() {
            if (adStatus) {
                when (adNetwork) {
                    ADMOB, FAN_BIDDING_ADMOB -> {
                        val adRequest = AdRequest.Builder().build()
                        AppOpenAd.load(activity, adMobAppOpenId, adRequest, object : AppOpenAdLoadCallback() {
                            override fun onAdLoaded(ad: AppOpenAd) {
                                appOpenAd = ad
                                isAppOpenAdLoaded = true
                                Log.e(Builder.TAG, "[$adNetwork] [on resume] app open ad loaded")
                            }

                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                appOpenAd = null
                                isAppOpenAdLoaded = false
                                loadBackupAppOpenAd()
                                Log.e(Builder.TAG, "[" + adNetwork + "] " + "[on resume] failed to load app open ad : " + loadAdError.message)
                            }
                        })
                    }

                    GOOGLE_AD_MANAGER, FAN_BIDDING_AD_MANAGER -> {
                        @SuppressLint("VisibleForTests") val adManagerAdRequest = AdManagerAdRequest.Builder().build()
                        AppOpenAd.load(activity, adManagerAppOpenId, adManagerAdRequest, object : AppOpenAdLoadCallback() {
                            override fun onAdLoaded(ad: AppOpenAd) {
                                appOpenAd = ad
                                isAppOpenAdLoaded = true
                                Log.e(Builder.TAG, "[$adNetwork] [on resume] app open ad loaded")
                            }

                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                appOpenAd = null
                                isAppOpenAdLoaded = false
                                loadBackupAppOpenAd()
                                Log.e(TAG, "[" + adNetwork + "] " + "[on resume] failed to load app open ad : " + loadAdError.message)
                            }
                        })
                    }else -> {}
                }
            }
        }

        //main ads
        fun loadAppOpenAd(onShowAdCompleteListener: OnShowAdCompleteListener) {
            if (adStatus) {
                when (adNetwork) {
                    ADMOB, FAN_BIDDING_ADMOB -> {
                        val adRequest = AdRequest.Builder().build()
                        AppOpenAd.load(activity, adMobAppOpenId, adRequest, object : AppOpenAdLoadCallback() {
                            override fun onAdLoaded(ad: AppOpenAd) {
                                appOpenAd = ad
                                showAppOpenAd(onShowAdCompleteListener)
                                Log.e(Builder.TAG, "[$adNetwork] [on start] app open ad loaded")
                            }

                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                appOpenAd = null
                                loadBackupAppOpenAd(onShowAdCompleteListener)
                                Log.e(Builder.TAG, "[" + adNetwork + "] " + "[on start] failed to load app open ad: " + loadAdError.message)
                            }
                        })
                    }

                    GOOGLE_AD_MANAGER, FAN_BIDDING_AD_MANAGER -> {
                        @SuppressLint("VisibleForTests") val adManagerAdRequest = AdManagerAdRequest.Builder().build()
                        AppOpenAd.load(activity, adManagerAppOpenId, adManagerAdRequest, object : AppOpenAdLoadCallback() {
                            override fun onAdLoaded(ad: AppOpenAd) {
                                appOpenAd = ad
                                showAppOpenAd(onShowAdCompleteListener)
                                Log.e(Builder.TAG, "[$adNetwork] [on start] app open ad loaded")
                            }

                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                appOpenAd = null
                                loadBackupAppOpenAd(onShowAdCompleteListener)
                                Log.e(Builder.TAG, "[" + adNetwork + "] " + "[on start] failed to load app open ad: " + loadAdError.message)
                            }
                        })
                    }
                    else -> onShowAdCompleteListener.onShowAdComplete()
                }
            } else {
                onShowAdCompleteListener.onShowAdComplete()
            }
        }
        fun showAppOpenAd() {
            when (adNetwork) {
                ADMOB, FAN_BIDDING_ADMOB, GOOGLE_AD_MANAGER, FAN_BIDDING_AD_MANAGER -> if (appOpenAd != null) {
                    appOpenAd?.setFullScreenContentCallback(object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            appOpenAd = null
                            loadAppOpenAd()
                            Log.e(Builder.TAG, "[$adNetwork] [on resume] close app open ad")
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            appOpenAd = null
                            loadAppOpenAd()
                            Log.e(Builder.TAG, "[" + adNetwork + "] " + "[on resume] failed to show app open ad: " + adError.message)
                        }

                        override fun onAdShowedFullScreenContent() {
                            Log.e(Builder.TAG, "[$adNetwork] [on resume] show app open ad")
                        }
                    })
                    appOpenAd?.show(activity)
                } else {
                    showBackupAppOpenAd()
                }
                else -> {}
            }
        }

        fun showAppOpenAd(onShowAdCompleteListener: OnShowAdCompleteListener) {
            when (adNetwork) {
                ADMOB, FAN_BIDDING_ADMOB, GOOGLE_AD_MANAGER, FAN_BIDDING_AD_MANAGER -> if (appOpenAd != null) {
                    appOpenAd?.setFullScreenContentCallback(object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            appOpenAd = null
                            onShowAdCompleteListener.onShowAdComplete()
                            Log.e(Builder.TAG, "[$adNetwork] [on start] close app open ad")
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            appOpenAd = null
                            onShowAdCompleteListener.onShowAdComplete()
                            Log.e(Builder.TAG, "[" + adNetwork + "] " + "[on start] failed to show app open ad: " + adError.message)
                        }

                        override fun onAdShowedFullScreenContent() {
                            Log.e(Builder.TAG, "[$adNetwork] [on start] show app open ad")
                        }
                    })
                    appOpenAd?.show(activity)
                } else {
                    onShowAdCompleteListener.onShowAdComplete()
                }
                else -> onShowAdCompleteListener.onShowAdComplete()
            }
        }

        fun loadBackupAppOpenAd() {
            if (adStatus) {
                when (backupAdNetwork) {
                    ADMOB, FAN_BIDDING_ADMOB -> {
                        val adRequest = AdRequest.Builder().build()
                        AppOpenAd.load(activity, adMobAppOpenId, adRequest, object : AppOpenAdLoadCallback() {
                            override fun onAdLoaded(ad: AppOpenAd) {
                                appOpenAd = ad
                                isAppOpenAdLoaded = true
                                Log.e(Builder.TAG, "[$backupAdNetwork] [on resume] [backup] app open ad loaded")
                            }

                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                appOpenAd = null
                                isAppOpenAdLoaded = false
                                loadBackupAppOpenAd()
                                Log.e(Builder.TAG, "[" + backupAdNetwork + "] " + "[on resume] [backup] failed to load app open ad : " + loadAdError.message)
                            }
                        })
                    }

                    GOOGLE_AD_MANAGER, FAN_BIDDING_AD_MANAGER -> {
                        @SuppressLint("VisibleForTests") val adManagerAdRequest = AdManagerAdRequest.Builder().build()
                        AppOpenAd.load(activity, adManagerAppOpenId, adManagerAdRequest, object : AppOpenAdLoadCallback() {
                            override fun onAdLoaded(ad: AppOpenAd) {
                                appOpenAd = ad
                                isAppOpenAdLoaded = true
                                Log.e(Builder.TAG, "[$backupAdNetwork] [on resume] [backup] app open ad loaded")
                            }

                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                appOpenAd = null
                                isAppOpenAdLoaded = false
                                loadBackupAppOpenAd()
                                Log.e(Builder.TAG, "[" + backupAdNetwork + "] " + "[on resume] [backup] failed to load app open ad : " + loadAdError.message)
                            }
                        })
                    } else -> {}
                }
            }
        }
        fun showBackupAppOpenAd() {
            when (backupAdNetwork) {
                ADMOB, FAN_BIDDING_ADMOB, GOOGLE_AD_MANAGER, FAN_BIDDING_AD_MANAGER -> if (appOpenAd != null) {
                    appOpenAd?.setFullScreenContentCallback(object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            appOpenAd = null
                            loadBackupAppOpenAd()
                            Log.e(Builder.TAG, "[$backupAdNetwork] [on resume] [backup] close app open ad")
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            appOpenAd = null
                            loadBackupAppOpenAd()
                            Log.e(Builder.TAG, "[" + backupAdNetwork + "] " + "[on resume] [backup] failed to show app open ad: " + adError.message)
                        }

                        override fun onAdShowedFullScreenContent() {
                            Log.e(Builder.TAG, "[$backupAdNetwork] [on resume] [backup] show app open ad")
                        }
                    })
                    appOpenAd?.show(activity)
                }
                else -> {}
            }
        }

        //backup ads
        fun loadBackupAppOpenAd(onShowAdCompleteListener: OnShowAdCompleteListener) {
            if (adStatus) {
                when (backupAdNetwork) {
                    ADMOB, FAN_BIDDING_ADMOB -> {
                        val adRequest = AdRequest.Builder().build()
                        AppOpenAd.load(activity, adMobAppOpenId, adRequest, object : AppOpenAdLoadCallback() {
                            override fun onAdLoaded(ad: AppOpenAd) {
                                appOpenAd = ad
                                showBackupAppOpenAd(onShowAdCompleteListener)
                                Log.e(Builder.TAG, "[$backupAdNetwork] [on start] [backup] app open ad loaded")
                            }

                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                appOpenAd = null
                                showBackupAppOpenAd(onShowAdCompleteListener)
                                Log.e(Builder.TAG, "[" + backupAdNetwork + "] " + "[on start] [backup] failed to load app open ad: " + loadAdError.message)
                            }
                        })
                    }

                    GOOGLE_AD_MANAGER, FAN_BIDDING_AD_MANAGER -> {
                        @SuppressLint("VisibleForTests") val adManagerAdRequest = AdManagerAdRequest.Builder().build()
                        AppOpenAd.load(activity, adManagerAppOpenId, adManagerAdRequest, object : AppOpenAdLoadCallback() {
                            override fun onAdLoaded(ad: AppOpenAd) {
                                appOpenAd = ad
                                showBackupAppOpenAd(onShowAdCompleteListener)
                                Log.e(Builder.TAG, "[$backupAdNetwork] [on start] [backup] app open ad loaded")
                            }

                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                appOpenAd = null
                                showBackupAppOpenAd(onShowAdCompleteListener)
                                Log.e(Builder.TAG, "[" + backupAdNetwork + "] " + "[on start] [backup] failed to load app open ad: " + loadAdError.message)
                            }
                        })
                    }
                    else -> onShowAdCompleteListener.onShowAdComplete()
                }
            } else {
                onShowAdCompleteListener.onShowAdComplete()
            }
        }
        fun showBackupAppOpenAd(onShowAdCompleteListener: OnShowAdCompleteListener) {
            when (backupAdNetwork) {
                ADMOB, FAN_BIDDING_ADMOB, GOOGLE_AD_MANAGER, FAN_BIDDING_AD_MANAGER -> if (appOpenAd != null) {
                    appOpenAd?.setFullScreenContentCallback(object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            appOpenAd = null
                            onShowAdCompleteListener.onShowAdComplete()
                            Log.e(Builder.TAG, "[$backupAdNetwork] [on start] [backup] close app open ad")
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            appOpenAd = null
                            onShowAdCompleteListener.onShowAdComplete()
                            Log.e(Builder.TAG, "[" + backupAdNetwork + "] " + "[on start] [backup] failed to show app open ad: " + adError.message)
                        }

                        override fun onAdShowedFullScreenContent() {
                            Log.e(Builder.TAG, "[$backupAdNetwork] [on start] [backup] show app open ad")
                        }
                    })
                    appOpenAd?.show(activity)
                } else {
                    onShowAdCompleteListener.onShowAdComplete()
                }
                else -> onShowAdCompleteListener.onShowAdComplete()
            }
        }


        companion object {
            private const val TAG = "AppOpenAd"
        }
    }
}
