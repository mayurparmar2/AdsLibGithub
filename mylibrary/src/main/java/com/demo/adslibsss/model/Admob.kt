package com.demo.adslibsss.model

import com.google.gson.annotations.SerializedName

data class Admob(
    @SerializedName("app_open_id")val app_open_id: String="",
    @SerializedName("banner_id")val banner_id: String="",
    @SerializedName("interstitial_id")val interstitial_id: String="",
    @SerializedName("native_id")val native_id: String="",
    @SerializedName("publisher_id")val publisher_id: String="",
    @SerializedName("rewarded_id")val rewarded_id: String=""
)