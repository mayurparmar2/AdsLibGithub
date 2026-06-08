package com.ads.adslib.core.model

/**
 * Supported ad networks. Order in waterfall is controlled separately via [AdUnitConfig].
 */
enum class AdNetwork {
    ADMOB,
    META,

    /**
     * ironSource / Unity LevelPlay. A single waterfall rung that internally
     * mediates ironSource's own demand plus Unity Ads (the two merged into the
     * LevelPlay SDK). Replaced the former standalone `UNITY` value — LevelPlay
     * bundles Unity, so a separate standalone-Unity rung is no longer possible.
     */
    IRONSOURCE
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

/**
 * Render style for a NATIVE placement, selectable per-screen from Remote Config
 * via the `type` field on a native entry.
 *
 * - [NATIVE] — full native ad (media/large image + headline + body + CTA).
 *   Meta: NativeAd; AdMob: native template.
 * - [NATIVE_BANNER] — compact native (icon/logo + title + CTA, no media).
 *   Meta: NativeBannerAd; AdMob: premium glassmorphism banner template
 *   (`admob_native_banner_premium.xml`).
 * - [MEDIUM_RECTANGLE] — a 300x250 banner (not a true native); served by the
 *   banner pipeline. The host renders it via the banner manager.
 */
enum class NativeType {
    NATIVE,
    NATIVE_BANNER,
    MEDIUM_RECTANGLE;

    companion object {
        fun fromString(value: String?): NativeType = when (value?.lowercase()?.trim()) {
            "native_banner", "nativebanner", "banner" -> NATIVE_BANNER
            "medium_rectangle", "mrec", "rectangle"   -> MEDIUM_RECTANGLE
            else -> NATIVE
        }
    }
}
