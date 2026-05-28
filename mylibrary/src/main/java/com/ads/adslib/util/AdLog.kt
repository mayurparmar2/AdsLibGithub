package com.ads.adslib.util

import android.util.Log

/**
 * Tiny logging wrapper. Disabled by default in release; toggle via [enabled].
 * All library logs share the "AdsLib" tag with the placement key as a prefix.
 */
object AdLog {
    private const val TAG = "AdsLib"

    @JvmStatic
    var enabled: Boolean = false

    fun d(key: String, msg: String) { if (enabled) Log.d(TAG, "[$key] $msg") }
    fun w(key: String, msg: String) { if (enabled) Log.w(TAG, "[$key] $msg") }
    fun e(key: String, msg: String) { if (enabled) Log.e(TAG, "[$key] $msg") }
}
