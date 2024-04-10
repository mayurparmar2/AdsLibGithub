package com.demo.adslibsss.model

import com.google.gson.annotations.SerializedName

data class AdsUnitId(
    @SerializedName("admob")val admob: Admob,
    @SerializedName("facebook")val facebook: Facebook,
    @SerializedName("unity")val unity: Unity
)