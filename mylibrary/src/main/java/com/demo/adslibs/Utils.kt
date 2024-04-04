package com.demo.adslibs

import android.util.Log
import com.demo.adslibs.DataModel.Ads
import com.demo.adslibs.DataModel.AdsData
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
        var adsData: AdsData = AdsData(ads = Ads(null,"facebook",3,"admob",false))
        @JvmStatic
        fun UpdateTask(path : String,listener:OnAdsJsonLoadListener){
            val adsApi = RetrofitHalper.getInstance().create(AdsApi::class.java)
                val result = adsApi.getAds(path)
                result.enqueue(object : Callback<AdsData> {
                    override fun onResponse(p0: Call<AdsData>, p1: Response<AdsData>) {
                        adsData = p1.body()!!
                        Log.e("TAG", "readJsonFromUrl: " + p1.body())
                        listener.onLoaded(adsData)
                    }
                    override fun onFailure(p0: Call<AdsData>, p1: Throwable) {
                        listener.onFailure(p1)
                    }
                })
        }
    }
}
