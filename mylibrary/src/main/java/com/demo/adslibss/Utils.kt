package com.demo.adslibss

import android.app.Activity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import com.demo.adslibss.DataModel.Admob
import com.demo.adslibss.DataModel.Ads
import com.demo.adslibss.DataModel.AdsData
import com.demo.adslibss.DataModel.AdsUnitId
import com.demo.adslibss.DataModel.Facebook
import com.demo.adslibss.DataModel.Unity
import com.demo.adslibss.mylib.Constant
import com.google.ads.consent.ConsentInformation
import com.google.ads.consent.ConsentStatus
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Utils {
    interface OnAdsJsonLoadListener {
        fun onLoaded(adsData: AdsData)
        fun onFailure(th:Throwable)
    }
    companion object{
        @JvmStatic
        var adsData: AdsData = AdsData(ads = Ads(AdsUnitId(Admob(), Facebook(), Unity()),"facebook",3,"admob",false))
        @JvmStatic
        fun UpdateTask(path : String,listener:OnAdsJsonLoadListener){
            val adsApi = RetrofitHalper.getInstance().create(AdsApi::class.java)
                val result = adsApi.getAds(path)
                result.enqueue(object : Callback<AdsData> {
                    override fun onResponse(p0: Call<AdsData>, data: Response<AdsData>) {
                        data.body()?.let {
                            adsData = it
                            listener.onLoaded(it)
                        }
                        Log.e("TAG", "readJsonFromUrl: " + data.body())
                    }
                    override fun onFailure(p0: Call<AdsData>, p1: Throwable) {
                        listener.onFailure(p1)
                    }
                })
        }

        @JvmStatic
        fun getAdSize(activity: Activity): AdSize {
            val defaultDisplay = activity.windowManager.defaultDisplay
            val displayMetrics = DisplayMetrics()
            defaultDisplay.getMetrics(displayMetrics)
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, (displayMetrics.widthPixels / displayMetrics.density).toInt())
        }
        @JvmStatic
        fun getAdRequest(activity: Activity?, bool: Boolean): AdRequest {
            return if (bool) {
                AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter::class.java, getBundleAd(activity)).build()
            } else {
                AdRequest.Builder().build()
            }
        }

        fun getBundleAd(activity: Activity?): Bundle {
            val bundle = Bundle()
            if (ConsentInformation.getInstance(activity).consentStatus == ConsentStatus.NON_PERSONALIZED) {
                bundle.putString("npa", Constant.AD_STATUS_ON)
            }
            return bundle
        }
    }



}
