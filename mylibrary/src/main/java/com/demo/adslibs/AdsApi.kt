package com.demo.adslibs

import com.demo.adslibs.DataModel.AdsData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface AdsApi {
    @GET
    fun getAds(@Url path: String): Call<AdsData>
}