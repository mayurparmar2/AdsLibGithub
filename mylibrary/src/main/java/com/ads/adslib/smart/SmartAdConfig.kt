package com.ads.adslib.smart

/**
 * Tuning for the app-wide [SmartAdManager] preloaders.
 *
 * Ad unit IDs are NOT here — they come from Remote Config via
 * [com.ads.adslib.config.remote.AdsConfigRepository]. This object only says
 * *which Remote-Config screen* each preloader should use and *how* to cache /
 * retry. Pass one to [SmartAdManager.init].
 *
 * The screen keys must exist in the `ads_config` JSON for that format, e.g. a
 * `"search"` interstitial / `"detail"` native placement.
 */
data class SmartAdConfig(

    // ── Remote-Config screen keys (which placement each preloader serves) ──
    /** RC screen key whose interstitial placement is kept preloaded. */
    val interstitialScreen: String = "default",
    /** RC screen key whose rewarded placement is loaded on demand. */
    val rewardedScreen: String = "default",
    /** RC screen key whose native placement fills the cache. */
    val nativeScreen: String = "default",

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
)
