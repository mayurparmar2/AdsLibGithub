package com.ads.adslib.config.remote

import android.content.Context

/**
 * Count-based frequency capping, persisted across app restarts.
 *
 * `frequency = N` → an opportunity is granted on every Nth call.
 * Example with N = 3:  call 1 → false, 2 → false, 3 → true, 4 → false, 5 → false, 6 → true…
 *
 * `frequency <= 1` → always true (no capping).
 */
internal class FrequencyCapManager(context: Context) {

    private val prefs =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    /**
     * Increments the counter for [key] and returns whether the ad may show now.
     */
    fun shouldShow(key: String, frequency: Int): Boolean {
        if (frequency <= 1) return true
        val next = prefs.getInt(key, 0) + 1
        prefs.edit().putInt(key, next).apply()
        return next % frequency == 0
    }

    /** Reset the counter for one key (e.g. after a manual show). */
    fun reset(key: String) {
        prefs.edit().remove(key).apply()
    }

    /** Reset all frequency counters. */
    fun resetAll() {
        prefs.edit().clear().apply()
    }

    private companion object {
        const val PREFS = "adslib_freq_cap"
    }
}
