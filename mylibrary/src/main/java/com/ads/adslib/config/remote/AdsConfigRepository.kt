package com.ads.adslib.config.remote

import android.content.Context
import com.ads.adslib.core.model.AdFormat
import com.ads.adslib.core.model.AdNetwork
import com.ads.adslib.core.model.AdUnitConfig
import com.ads.adslib.core.model.BannerAdSize
import com.ads.adslib.core.model.NativeType
import com.ads.adslib.core.model.NetworkAdUnit
import com.ads.adslib.util.AdLog

/**
 * Single source of truth for Remote-Config-driven ad configuration.
 *
 * Flow:
 * ```
 * // After Remote Config fetch:
 * AdsConfigRepository.load(context, jsonString)
 *
 * // Per screen, get a ready-to-use AdUnitConfig (waterfall built from
 * // provider_priority, filtered by every enabled flag). Null = don't show.
 * AdsConfigRepository.bannerConfig("home")?.let { BannerAdManager(it).load(...) }
 * AdsConfigRepository.nativeConfig("detail")
 * AdsConfigRepository.rewardedConfig("search")
 * AdsConfigRepository.appOpenConfig()
 *
 * // Interstitial respects count-based frequency:
 * if (AdsConfigRepository.shouldShowInterstitial("search")) { ...show... }
 * ```
 */
object AdsConfigRepository {

    @Volatile
    private var config: AdsRemoteConfig = AdsRemoteConfig.EMPTY
    private var freqCap: FrequencyCapManager? = null

    /** Notified after each successful [load] so late-arriving RC can kick preloaders. */
    @Volatile
    private var onConfigLoaded: (() -> Unit)? = null

    /** Register a callback invoked every time the config is (re)loaded. */
    fun setConfigLoadedListener(listener: (() -> Unit)?) {
        onConfigLoaded = listener
    }

    /** Parse and store the Remote Config JSON. Safe to call again on refresh. */
    fun load(context: Context, json: String) {
        config = RemoteConfigParser.parse(json)
        if (freqCap == null) freqCap = FrequencyCapManager(context)
        AdLog.d("ads_config", "loaded: enabled=${config.enabled}, priority=${config.providerPriority}")
        onConfigLoaded?.invoke()
    }

    val isLoaded: Boolean get() = config !== AdsRemoteConfig.EMPTY
    val adsEnabled: Boolean get() = config.enabled

    /** Unity game id (for UnityAds.initialize), or null. */
    fun unityGameId(): String? = config.providers[AdNetwork.UNITY]?.gameId

    // ── Per-format config builders ───────────────────────────────────

    fun bannerConfig(screen: String, size: BannerAdSize = BannerAdSize.ADAPTIVE): AdUnitConfig? =
        buildConfig(screen, AdFormat.BANNER, size) { it.banner[screen] }

    fun nativeConfig(screen: String): AdUnitConfig? {
        if (!config.enabled) return null
        // Collect (network, entry) so we can also read the native render type.
        val picked = config.providerPriority.mapNotNull { net ->
            val provider = config.providers[net]?.takeIf { it.enabled } ?: return@mapNotNull null
            val entry = provider.native[screen]?.takeIf { it.enabled && it.adId.isNotBlank() }
                ?: return@mapNotNull null
            net to entry
        }
        if (picked.isEmpty()) {
            AdLog.d("ads_config", "NATIVE/$screen — no enabled units")
            return null
        }
        // Render type comes from the highest-priority provider that has this screen.
        val type = picked.first().second.nativeType
        return AdUnitConfig(
            placementKey = screen,
            format       = AdFormat.NATIVE,
            waterfall    = picked.map { (net, entry) -> NetworkAdUnit(net, entry.adId) },
            bannerSize   = if (type == NativeType.MEDIUM_RECTANGLE) BannerAdSize.MEDIUM_RECTANGLE
                           else BannerAdSize.ADAPTIVE,
            nativeType   = type,
        )
    }

    fun interstitialConfig(screen: String): AdUnitConfig? =
        buildConfig(screen, AdFormat.INTERSTITIAL) { p ->
            if (!p.interstitialEnabled) null else p.interstitial[screen]
        }

    fun rewardedConfig(screen: String): AdUnitConfig? =
        buildConfig(screen, AdFormat.REWARDED) { p ->
            if (!p.rewardedEnabled) null else p.rewarded[screen]
        }

    /** App Open is AdMob-only and single (no screen). */
    fun appOpenConfig(): AdUnitConfig? {
        if (!config.enabled) return null
        val units = config.providerPriority.mapNotNull { net ->
            val p = config.providers[net]?.takeIf { it.enabled } ?: return@mapNotNull null
            val entry = p.appOpen?.takeIf { it.enabled && it.adId.isNotBlank() } ?: return@mapNotNull null
            NetworkAdUnit(net, entry.adId)
        }
        return units.takeIf { it.isNotEmpty() }
            ?.let { AdUnitConfig("app_open", AdFormat.APP_OPEN, it) }
    }

    /** Highest-priority enabled App Open ad unit id, or null if none/disabled. */
    fun appOpenUnitId(): String? = appOpenConfig()?.waterfall?.firstOrNull()?.adUnitId

    /**
     * AdMob native ad unit id for [screen], or null if AdMob native is disabled
     * there. Used by the AdMob-only [com.ads.adslib.smart.NativeAdCache] prefetch.
     */
    fun admobNativeUnitId(screen: String): String? {
        if (!config.enabled) return null
        val admob = config.providers[AdNetwork.ADMOB]?.takeIf { it.enabled } ?: return null
        return admob.native[screen]?.takeIf { it.enabled && it.adId.isNotBlank() }?.adId
    }

    // ── Interstitial frequency ───────────────────────────────────────

    /** Highest-priority enabled provider's interstitial frequency for the build. */
    fun interstitialFrequency(): Int =
        config.providerPriority
            .firstNotNullOfOrNull { net ->
                config.providers[net]?.takeIf { it.enabled && it.interstitialEnabled }?.interstitialFrequency
            } ?: 1

    /**
     * True if an interstitial for [screen] is configured AND the count-based
     * frequency allows a show this time. Increments the counter as a side effect.
     */
    fun shouldShowInterstitial(screen: String): Boolean {
        if (interstitialConfig(screen) == null) return false
        val freq = interstitialFrequency()
        val cap = freqCap ?: return true
        return cap.shouldShow("inter_$screen", freq)
    }

    // ── Core mapping: JSON → waterfall AdUnitConfig ──────────────────

    private inline fun buildConfig(
        screen: String,
        format: AdFormat,
        bannerSize: BannerAdSize = BannerAdSize.ADAPTIVE,
        pick: (ProviderConfig) -> PlacementEntry?,
    ): AdUnitConfig? {
        if (!config.enabled) return null
        val units = config.providerPriority.mapNotNull { net ->
            val provider = config.providers[net]?.takeIf { it.enabled } ?: return@mapNotNull null
            val entry = pick(provider)?.takeIf { it.enabled && it.adId.isNotBlank() } ?: return@mapNotNull null
            NetworkAdUnit(net, entry.adId)
        }
        if (units.isEmpty()) {
            AdLog.d("ads_config", "$format/$screen — no enabled units")
            return null
        }
        return AdUnitConfig(
            placementKey = screen,
            format       = format,
            waterfall    = units,
            bannerSize   = bannerSize,
        )
    }
}
