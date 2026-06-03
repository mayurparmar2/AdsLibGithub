package com.ads.adslib.config.remote

import com.ads.adslib.core.model.AdNetwork
import com.ads.adslib.util.AdLog
import org.json.JSONObject

/**
 * Parses the app's Remote Config JSON (the `ads` object) into [AdsRemoteConfig]
 * using the built-in `org.json` (zero extra dependency).
 *
 * Fully null-safe: any malformed / missing field falls back to a sensible
 * default, and a parse failure returns [AdsRemoteConfig.EMPTY] (ads disabled).
 */
object RemoteConfigParser {

    // Keys inside a format object that are NOT screen names.
    private val NON_SCREEN_KEYS = setOf("enabled", "frequency", "ad_unit_id", "placement_id")

    fun parse(json: String): AdsRemoteConfig = runCatching {
        val root = JSONObject(json)
        val ads = root.optJSONObject("ads") ?: return AdsRemoteConfig.EMPTY

        val priority = ads.optJSONArray("provider_priority")
            ?.let { arr -> (0 until arr.length()).mapNotNull { arr.optString(it).toNetwork() } }
            ?: emptyList()

        val providersJson = ads.optJSONObject("providers")
        val providers = mutableMapOf<AdNetwork, ProviderConfig>()
        if (providersJson != null) {
            for (key in providersJson.keys()) {
                val net = key.toNetwork() ?: continue
                val pj = providersJson.optJSONObject(key) ?: continue
                providers[net] = parseProvider(net, pj)
            }
        }

        AdsRemoteConfig(
            enabled = ads.optBoolean("enabled", false),
            providerPriority = priority,
            providers = providers,
        )
    }.getOrElse { e ->
        AdLog.w("rc_parser", "parse failed: ${e.message}")
        AdsRemoteConfig.EMPTY
    }

    // ── Per-provider ──────────────────────────────────────────────────

    private fun parseProvider(net: AdNetwork, pj: JSONObject): ProviderConfig {
        val interObj = pj.optJSONObject("interstitial")
        val rewardObj = pj.optJSONObject("rewarded")

        return ProviderConfig(
            network = net,
            enabled = pj.optBoolean("enabled", false),
            // LevelPlay/ironSource uses `app_key`; legacy `game_id` kept as fallback.
            gameId  = pj.optString("app_key").ifBlank { pj.optString("game_id") }.ifBlank { null },

            appOpen = pj.optJSONObject("app_open")?.let { parseEntry(it, it.optBoolean("enabled", true)) },

            banner = parseScreenMap(pj.optJSONObject("banner"), perScreenEnabled = true),
            native = parseScreenMap(pj.optJSONObject("native"), perScreenEnabled = true),

            interstitialEnabled   = interObj?.optBoolean("enabled", false) ?: false,
            interstitialFrequency = interObj?.optInt("frequency", 1)?.coerceAtLeast(1) ?: 1,
            interstitial = parseScreenMap(
                interObj,
                perScreenEnabled = false,
                inheritedEnabled = interObj?.optBoolean("enabled", false) ?: false,
            ),

            rewardedEnabled = rewardObj?.optBoolean("enabled", false) ?: false,
            rewarded = parseScreenMap(
                rewardObj,
                perScreenEnabled = false,
                inheritedEnabled = rewardObj?.optBoolean("enabled", false) ?: false,
            ),
        )
    }

    /**
     * Parse a format object whose keys are screen names.
     *
     * @param perScreenEnabled true → each screen entry has its own `enabled`
     *   (banner/native). false → entries have no `enabled`, so [inheritedEnabled]
     *   (the format-level flag) is applied to all (interstitial/rewarded).
     */
    private fun parseScreenMap(
        obj: JSONObject?,
        perScreenEnabled: Boolean,
        inheritedEnabled: Boolean = true,
    ): Map<String, PlacementEntry> {
        if (obj == null) return emptyMap()
        val out = mutableMapOf<String, PlacementEntry>()
        for (key in obj.keys()) {
            if (key in NON_SCREEN_KEYS) continue
            val entryJson = obj.optJSONObject(key) ?: continue
            val enabled = if (perScreenEnabled) entryJson.optBoolean("enabled", false) else inheritedEnabled
            out[key] = parseEntry(entryJson, enabled)
        }
        return out
    }

    private fun parseEntry(obj: JSONObject, enabled: Boolean): PlacementEntry {
        // AdMob uses ad_unit_id; Meta/Unity use placement_id.
        val id = obj.optString("ad_unit_id").ifBlank { obj.optString("placement_id") }
        // Native-only optional render style: "native" | "native_banner" | "medium_rectangle".
        val nativeType = com.ads.adslib.core.model.NativeType.fromString(
            obj.optString("type").ifBlank { null }
        )
        return PlacementEntry(enabled = enabled, adId = id, nativeType = nativeType)
    }

    private fun String.toNetwork(): AdNetwork? = when (lowercase().trim()) {
        "admob" -> AdNetwork.ADMOB
        "meta", "facebook", "fan" -> AdNetwork.META
        // "unity" kept for backward-compat — the LevelPlay rung mediates Unity too.
        "ironsource", "levelplay", "unity" -> AdNetwork.IRONSOURCE
        else -> null
    }
}
