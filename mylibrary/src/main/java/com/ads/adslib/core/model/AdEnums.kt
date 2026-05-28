package com.ads.adslib.core.model

/**
 * Supported ad networks. Order in waterfall is controlled separately via [AdUnitConfig].
 */
enum class AdNetwork {
    ADMOB,
    META,
    UNITY
}

/**
 * Supported ad formats.
 */
enum class AdFormat {
    BANNER,
    INTERSTITIAL,
    NATIVE,
    REWARDED,
    APP_OPEN,
    SPLASH
}

/**
 * Internal lifecycle state of a single ad request.
 */
enum class AdLoadState {
    IDLE,
    LOADING,
    LOADED,
    SHOWING,
    FAILED,
    DISMISSED
}

/**
 * Banner sizes abstracted away from any single SDK's enum.
 */
enum class BannerAdSize {
    BANNER,           // 320x50
    LARGE_BANNER,     // 320x100
    MEDIUM_RECTANGLE, // 300x250
    ADAPTIVE          // full-width adaptive
}
