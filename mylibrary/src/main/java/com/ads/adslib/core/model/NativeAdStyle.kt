package com.ads.adslib.core.model

import androidx.annotation.ColorInt

/**
 * Optional theming for the library's bundled native ad templates so they match
 * the host app's theme (esp. dark mode). Each color is an ARGB int; null leaves
 * the template's default for that element.
 *
 * The library templates ship with fixed light colors (white background, black
 * text). Apps with their own theme — especially a manual (non-system) dark mode —
 * should pass a [NativeAdStyle] built from their theme colors to
 * [com.ads.adslib.admob.native_ad.NativeAdManager] so the native ad's
 * background/text adapt instead of rendering a white card with dark text.
 */
data class NativeAdStyle(
    /** Root background of the native template. */
    @ColorInt val backgroundColor: Int? = null,
    /** Headline / title text. */
    @ColorInt val titleColor: Int? = null,
    /** Body / advertiser / price / store / social text. */
    @ColorInt val bodyColor: Int? = null,
    /** Call-to-action button text. */
    @ColorInt val ctaTextColor: Int? = null,
    /** Call-to-action button background. */
    @ColorInt val ctaBackgroundColor: Int? = null,
)
