package com.ads.adslib.smart

import android.os.Handler
import android.os.Looper
import com.ads.adslib.util.AdLog
import kotlin.math.min
import kotlin.math.pow

/**
 * Per-loader exponential backoff retry scheduler.
 *
 * Delay sequence (baseMs=5000, maxMs=300000):
 *   attempt 1 →   5s
 *   attempt 2 →  10s
 *   attempt 3 →  20s
 *   attempt 4 →  40s
 *   attempt 5 →  80s
 *   attempt 6 → 160s  (last if maxRetries=6)
 *
 * After [maxRetries] failures the scheduler goes silent — call [reset]
 * (e.g. when the app returns to foreground) to try again.
 */
internal class AdRetryScheduler(
    private val tag: String,
    private val maxRetries: Int = 6,
    private val baseDelayMs: Long = 5_000L,
    private val maxDelayMs: Long = 300_000L,
) {
    private val handler = Handler(Looper.getMainLooper())
    private var retryCount = 0
    private var pending: Runnable? = null

    /** Schedule [action] after the next backoff delay. Returns false if exhausted. */
    fun schedule(action: () -> Unit): Boolean {
        if (retryCount >= maxRetries) {
            AdLog.w(tag, "retry exhausted ($retryCount/$maxRetries) — giving up")
            return false
        }
        val delay = min((baseDelayMs * 2.0.pow(retryCount)).toLong(), maxDelayMs)
        retryCount++
        AdLog.d(tag, "retry $retryCount/$maxRetries in ${delay / 1000}s")
        val r = Runnable { action() }
        pending = r
        handler.postDelayed(r, delay)
        return true
    }

    /** Cancel any pending retry and reset the counter (e.g. after a successful load). */
    fun reset() {
        cancel()
        retryCount = 0
    }

    /** Cancel any pending retry without resetting the counter. */
    fun cancel() {
        pending?.let { handler.removeCallbacks(it) }
        pending = null
    }

    val isExhausted: Boolean get() = retryCount >= maxRetries
}
