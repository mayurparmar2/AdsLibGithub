package com.demo.adslibsss.model

import com.google.gson.annotations.SerializedName

data class Unity(
    @SerializedName("banner_id")val banner_id: String="",
    @SerializedName("game_id")val game_id: String="",
    @SerializedName("interstitial_id")val interstitial_id: String="",
    @SerializedName("rewarded_id")val rewarded_id: String=""
)