package com.ads.adslib.smart

import com.ads.adslib.core.model.AdNetwork
import com.ads.adslib.core.model.BannerAdSize
import com.ads.adslib.core.model.NetworkAdUnit

/**
 * Single configuration object for SmartAdManager.
 * Pass one of these to [SmartAdManager.init].
 */
data class SmartAdConfig(

    // ── Ad Unit IDs ────────────────────────────────────────────────
    val interstitialUnitId: String,
    val rewardedUnitId: String,
    val appOpenUnitId: String,
    val nativeUnitId: String,
    val bannerUnitId: String = "",

    // ── Interstitial ───────────────────────────────────────────────
    /** Minimum seconds between two interstitial shows. */
    val interstitialIntervalSec: Long = 30L,

    // ── Native Cache ───────────────────────────────────────────────
    /** Max pre-loaded native ads to keep in memory at once. */
    val nativeCacheSize: Int = 3,
    /** Minutes before a cached native ad is considered expired. */
    val nativeExpiryMinutes: Int = 60,

    // ── Retry / Backoff ────────────────────────────────────────────
    /** Max load attempts before giving up (reset on next preload call). */
    val maxRetries: Int = 6,
    /** First retry delay in ms — doubles each attempt (exponential backoff). */
    val retryBaseDelayMs: Long = 5_000L,
    /** Ceiling on retry delay. */
    val retryMaxDelayMs: Long = 300_000L,   // 5 min

    // ── Waterfalls (optional extra networks per format) ────────────
    val interstitialWaterfall: List<NetworkAdUnit> = emptyList(),
    val rewardedWaterfall: List<NetworkAdUnit> = emptyList(),
    val nativeWaterfall: List<NetworkAdUnit> = emptyList(),
    val bannerSize: BannerAdSize = BannerAdSize.ADAPTIVE,
) {
    /** Full interstitial waterfall with primary unit at the front. */
    internal fun interstitialUnits(): List<NetworkAdUnit> =
        listOf(NetworkAdUnit(AdNetwork.ADMOB, interstitialUnitId)) + interstitialWaterfall

    /** Full rewarded waterfall with primary unit at the front. */
    internal fun rewardedUnits(): List<NetworkAdUnit> =
        listOf(NetworkAdUnit(AdNetwork.ADMOB, rewardedUnitId)) + rewardedWaterfall

    /** Full native waterfall with primary unit at the front. */
    internal fun nativeUnits(): List<NetworkAdUnit> =
        listOf(NetworkAdUnit(AdNetwork.ADMOB, nativeUnitId)) + nativeWaterfall
}
