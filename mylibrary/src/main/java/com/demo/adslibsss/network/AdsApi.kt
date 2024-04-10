package com.demo.adslibsss.network

import com.demo.adslibsss.model.AdsData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Url

interface AdsApi {
    @GET
    @Headers("Content-Type: application/json;charset=UTF-8")
    suspend fun getAds2(@Url path: String): Call<AdsData>

    @GET
    fun getAds(@Url path: String): Call<AdsData>


}