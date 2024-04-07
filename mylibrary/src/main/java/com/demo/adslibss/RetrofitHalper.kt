package com.demo.adslibss

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHalper {
    val BASE_URL = "https://mayurparmar2.github.io"

    fun getInstance():Retrofit{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}