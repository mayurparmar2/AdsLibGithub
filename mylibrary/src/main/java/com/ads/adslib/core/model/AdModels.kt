package com.ads.adslib.core.model

/**
 * Normalized error wrapper so callers don't depend on any network-specific error type.
 */
data class AdError(
    val network: AdNetwork,
    val code: Int,
    val message: String,
    val cause: Throwable? = null
) {
    override fun toString(): String = "[$network] code=$code, msg=$message"
}

/**
 * Per-network ad unit ID. e.g. ADMOB -> "ca-app-pub-xxx/yyy".
 */
data class NetworkAdUnit(
    val network: AdNetwork,
    val adUnitId: String,
    val enabled: Boolean = true
)

/**
 * Configuration for a single logical ad placement.
 *
 * [waterfall] defines fallback order: the manager tries each entry top-to-bottom
 * until one loads successfully. Disabled units are skipped.
 *
 * @param placementKey developer-facing key, e.g. "home_banner".
 * @param format the ad format this placement serves.
 * @param waterfall ordered list of network units to try.
 * @param bannerSize only relevant for BANNER format.
 */
data class AdUnitConfig(
    val placementKey: String,
    val format: AdFormat,
    val waterfall: List<NetworkAdUnit>,
    val bannerSize: BannerAdSize = BannerAdSize.ADAPTIVE
) {
    /** Networks that are currently enabled, preserving waterfall order. */
    val activeUnits: List<NetworkAdUnit>
        get() = waterfall.filter { it.enabled && it.adUnitId.isNotBlank() }
}
