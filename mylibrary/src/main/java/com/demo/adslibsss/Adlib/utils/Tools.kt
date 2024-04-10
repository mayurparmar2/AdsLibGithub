package com.demo.adslibsss.Adlib.utils

import android.app.Activity
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest

class Tools {

    companion object{
        @JvmStatic
        fun getAdRequest(activity: Activity?, bool: Boolean): AdRequest {
            return if (bool) {
                AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter::class.java, AdUtils.getBundleAd(activity)).build()
            } else {
                AdRequest.Builder().build()
            }
        }
    }
}