package com.demo.adslibsss.mylib.utils

import android.app.Activity
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest

class Tools {

    companion object{
        @JvmStatic
        fun getAdRequest(activity: Activity?, bool: Boolean): AdRequest {
            return if (bool) {
                AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter::class.java, Utils.getBundleAd(activity)).build()
            } else {
                AdRequest.Builder().build()
            }
        }
//        @JvmStatic
//        fun setNativeAdStyle(activity: Activity, nativeAdView: LinearLayout, style: String) {
//
//        }

    }
}