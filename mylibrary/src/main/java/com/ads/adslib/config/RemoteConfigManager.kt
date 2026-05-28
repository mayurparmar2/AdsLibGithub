package com.ads.adslib.config

import com.ads.adslib.util.AdLog
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

/**
 * Wraps Firebase Remote Config to let you toggle ads and tune behaviour remotely
 * without an app update.
 *
 * Recommended keys (define matching defaults in your app):
 *  - "ads_enabled"          : Boolean master switch
 *  - "interstitial_enabled" : Boolean per-format switch
 *  - "interstitial_interval": Long, min seconds between interstitials
 */
class RemoteConfigManager(
    private val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
) {

    /**
     * @param defaults map of key -> default value applied before fetch.
     * @param minFetchIntervalSeconds lower this only in debug builds.
     * @param onReady invoked after fetchAndActivate completes (or fails gracefully).
     */
    fun init(
        defaults: Map<String, Any>,
        minFetchIntervalSeconds: Long = 3600,
        onReady: () -> Unit = {}
    ) {
        remoteConfig.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(minFetchIntervalSeconds)
                .build()
        )
        remoteConfig.setDefaultsAsync(defaults)
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                AdLog.d("remote_config", "fetchAndActivate success=${task.isSuccessful}")
                onReady()
            }
    }

    fun isAdsEnabled(): Boolean = remoteConfig.getBoolean("ads_enabled")
    fun getBoolean(key: String): Boolean = remoteConfig.getBoolean(key)
    fun getLong(key: String): Long = remoteConfig.getLong(key)
    fun getString(key: String): String = remoteConfig.getString(key)
}
