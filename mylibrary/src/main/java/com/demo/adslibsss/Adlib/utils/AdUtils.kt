package com.demo.adslibsss.Adlib.utils

import android.app.Activity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import com.demo.adslibsss.network.AdsApi
import com.demo.adslibsss.model.Admob
import com.demo.adslibsss.model.Ads
import com.demo.adslibsss.model.AdsData
import com.demo.adslibsss.model.AdsUnitId
import com.demo.adslibsss.model.Facebook
import com.demo.adslibsss.model.Unity
import com.demo.adslibsss.network.RetrofitHalper
import com.demo.adslibsss.Adlib.Constant
import com.google.ads.consent.ConsentInformation
import com.google.ads.consent.ConsentStatus
import com.google.android.gms.ads.AdSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdUtils {
    interface OnLoadedListener {
        fun isLoaded(adsData: AdsData)
    }
    interface OnAdsJsonLoadListener {
        fun onLoaded(adsData: AdsData)
        fun onFailure(th:Throwable)
    }
    companion object{
        @JvmStatic
        var adsData: AdsData = AdsData(ads = Ads(AdsUnitId(Admob(), Facebook(), Unity()),"facebook",3,"admob",false,"https://www.google.com"))
        suspend fun loadJsonCoroutine(path : String, listener: OnAdsJsonLoadListener){
            withContext(Dispatchers.IO) {
                val result = RetrofitHalper.apiService.getAds2(path)
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
        }

        @JvmStatic
        fun getAdSize(activity: Activity): AdSize {
            val defaultDisplay = activity.windowManager.defaultDisplay
            val displayMetrics = DisplayMetrics()
            defaultDisplay.getMetrics(displayMetrics)
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, (displayMetrics.widthPixels / displayMetrics.density).toInt())
        }

        fun getBundleAd(activity: Activity?): Bundle {
            val bundle = Bundle()
            if (ConsentInformation.getInstance(activity).consentStatus == ConsentStatus.NON_PERSONALIZED) {
                bundle.putString("npa", Constant.AD_STATUS_ON)
            }
            return bundle
        }

        @JvmStatic
        fun loadJson(path : String, listener: OnAdsJsonLoadListener){
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    loadJsonCoroutine(path,listener)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("TAG", "funLoadFun: $e")
//                    $Mayur697@sbi.com
//                    $Ankit697@sbi.com
                }
            }
        }

        @JvmStatic
        fun UpdateTask(path : String,listener: OnAdsJsonLoadListener){
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


    }
}