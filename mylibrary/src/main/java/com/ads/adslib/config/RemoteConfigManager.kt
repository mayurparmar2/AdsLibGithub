package com.ads.adslib.config

import com.ads.adslib.util.AdLog
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

/**
 * Thin, null-safe wrapper over Firebase Remote Config.
 *
 * If FirebaseApp is not initialized (no google-services.json / plugin),
 * [FirebaseRemoteConfig.getInstance] throws. This wrapper swallows that and
 * falls back to the in-memory defaults supplied via [init], so the rest of
 * the library keeps working without Remote Config.
 */
class RemoteConfigManager {

    // Obtained safely — null when FirebaseApp is not initialized.
    private val remoteConfig: FirebaseRemoteConfig? = runCatching {
        FirebaseRemoteConfig.getInstance()
    }.getOrNull()

    /** Local copy of defaults so getters work even when RC is unavailable. */
    private val localDefaults = mutableMapOf<String, Any>()

    fun init(
        defaults: Map<String, Any>,
        minFetchIntervalSeconds: Long = 3600,
        onReady: () -> Unit = {}
    ) {
        localDefaults.putAll(defaults)

        val rc = remoteConfig
        if (rc == null) {
            AdLog.w("remote_config", "FirebaseApp unavailable — using local defaults")
            onReady()
            return
        }

        rc.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(minFetchIntervalSeconds)
                .build()
        )
        rc.setDefaultsAsync(defaults)
        rc.fetchAndActivate()
            .addOnCompleteListener { task ->
                AdLog.d("remote_config", "fetchAndActivate success=${task.isSuccessful}")
                onReady()
            }
    }

    fun isAdsEnabled(): Boolean = getBoolean("ads_enabled")

    fun getBoolean(key: String): Boolean =
        remoteConfig?.getBoolean(key) ?: (localDefaults[key] as? Boolean ?: false)

    fun getLong(key: String): Long =
        remoteConfig?.getLong(key) ?: (localDefaults[key] as? Long ?: 0L)

    fun getString(key: String): String {
        // If RC is present but the value is still blank (e.g. defaults not yet
        // applied / key not set remotely), fall back to the local default.
        val rcValue = remoteConfig?.getString(key)
        if (!rcValue.isNullOrBlank()) return rcValue
        return localDefaults[key] as? String ?: ""
    }
}
