package com.demo.adslibsss.model

import com.google.gson.annotations.SerializedName

data class Ads(
    @SerializedName("ads_unit_id")val ads_unit_id: AdsUnitId,
    @SerializedName("backup_ads")val backup_ads: String,
    @SerializedName("counter")val counter: Int,
    @SerializedName("main_ads")val main_ads: String,
    @SerializedName("enable")val enable: Boolean,
    @SerializedName("policy_link")val policy_link: String

)