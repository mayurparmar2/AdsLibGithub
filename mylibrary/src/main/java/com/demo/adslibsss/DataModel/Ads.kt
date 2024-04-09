package com.demo.adslibsss.DataModel

data class Ads(
    val ads_unit_id: AdsUnitId,
    val backup_ads: String,
    val policy_link: String,
    val counter: Int,
    val main_ads: String,
    val enable: Boolean
)