package com.ads.adslib.config.remote

import com.ads.adslib.core.model.AdNetwork

/**
 * Parsed representation of the `ads` section of the app's Remote Config JSON.
 * Produced by [RemoteConfigParser], consumed by [AdsConfigRepository].
 *
 * Non-ads sections (unlock / update / privacy / app) are intentionally ignored —
 * this library is ads-only.
 */
data class AdsRemoteConfig(
    val enabled: Boolean,
    /** Waterfall order, e.g. [ADMOB, META, UNITY]. Unknown names dropped. */
    val providerPriority: List<AdNetwork>,
    val providers: Map<AdNetwork, ProviderConfig>,
) {
    companion object {
        /** Safe empty config — ads disabled. Used when parsing fails. */
        val EMPTY = AdsRemoteConfig(
            enabled = false,
            providerPriority = emptyList(),
            providers = emptyMap(),
        )
    }
}

/**
 * One ad network's full config.
 *
 * The JSON shape is irregular per network (Meta has no rewarded/app-open,
 * Unity has no native, etc.), so absent formats are simply empty maps / nulls.
 */
data class ProviderConfig(
    val network: AdNetwork,
    val enabled: Boolean,

    /** Unity only — required by UnityAds.initialize(). Null for AdMob/Meta. */
    val gameId: String? = null,

    /** AdMob only — single App Open placement. */
    val appOpen: PlacementEntry? = null,

    /** screen key -> banner placement (per-screen enabled). */
    val banner: Map<String, PlacementEntry> = emptyMap(),

    /** screen key -> native placement (per-screen enabled). */
    val native: Map<String, PlacementEntry> = emptyMap(),

    /** Format-level interstitial switch + count-based frequency. */
    val interstitialEnabled: Boolean = false,
    val interstitialFrequency: Int = 1,
    /** screen key -> interstitial placement (enabled = [interstitialEnabled]). */
    val interstitial: Map<String, PlacementEntry> = emptyMap(),

    /** Format-level rewarded switch. */
    val rewardedEnabled: Boolean = false,
    /** screen key -> rewarded placement (enabled = [rewardedEnabled]). */
    val rewarded: Map<String, PlacementEntry> = emptyMap(),
)

/**
 * A single placement.
 *
 * @param enabled whether this placement may serve. For banner/native this is
 *   the per-screen flag; for interstitial/rewarded the parser copies the
 *   format-level flag here so callers can treat every entry uniformly.
 * @param adId AdMob `ad_unit_id` OR Meta/Unity `placement_id` (whichever present).
 */
data class PlacementEntry(
    val enabled: Boolean,
    val adId: String,
)
