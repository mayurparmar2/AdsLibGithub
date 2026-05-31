package com.ads.adslib.smart

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.ads.adslib.R
import com.ads.adslib.config.remote.AdsConfigRepository
import com.ads.adslib.util.AdLog
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView

/**
 * Pre-loads and caches up to [SmartAdConfig.nativeCacheSize] native ads.
 *
 * - [prefetch] fills empty cache slots proactively.
 * - [attachNext] pops one cached ad into a ViewGroup (inflates + populates view).
 * - Ads older than [SmartAdConfig.nativeExpiryMinutes] are auto-discarded.
 * - [destroy] must be called to avoid NativeAd memory leaks.
 */
internal class NativeAdCache(private val config: SmartAdConfig) {

    private data class Slot(val ad: NativeAd, val loadedAt: Long)

    private val cache = ArrayDeque<Slot>()
    private val expiryMs = config.nativeExpiryMinutes * 60_000L
    private var activeLoads = 0
    private val retry = AdRetryScheduler(
        tag         = "native_cache",
        maxRetries  = config.maxRetries,
        baseDelayMs = config.retryBaseDelayMs,
        maxDelayMs  = config.retryMaxDelayMs,
    )

    // ── Public ───────────────────────────────────────────────────────

    /**
     * Fill empty cache slots up to [SmartAdConfig.nativeCacheSize].
     * Safe to call frequently — respects the cache size limit.
     */
    fun prefetch(context: Context) {
        // Unit id comes from Remote Config; null = AdMob native disabled / not
        // configured for this screen / RC not loaded yet (re-kicked on load).
        val unitId = AdsConfigRepository.admobNativeUnitId(config.nativeScreen) ?: run {
            AdLog.d("native_cache", "no RC AdMob native for '${config.nativeScreen}' — skipping prefetch")
            return
        }
        pruneExpired()
        val needed = config.nativeCacheSize - cache.size - activeLoads
        if (needed <= 0) return
        AdLog.d("native_cache", "prefetching $needed ads (have ${cache.size})")
        repeat(needed) { loadOne(context.applicationContext, unitId) }
    }

    /**
     * Attach the next available native ad to [container].
     * Inflates [R.layout.admob_native_ad_template] and populates all views.
     *
     * @param onEmpty Called when the cache is empty — caller may show a
     *                placeholder. Cache will refill automatically.
     */
    fun attachNext(container: ViewGroup, onEmpty: () -> Unit = {}) {
        pruneExpired()
        val slot = cache.removeFirstOrNull()
        if (slot == null) {
            AdLog.d("native_cache", "cache empty")
            onEmpty()
            // Trigger a refill so the next call succeeds
            prefetch(container.context)
            return
        }
        val view = buildView(container.context, slot.ad)
        container.removeAllViews()
        container.addView(view)
        AdLog.d("native_cache", "attached from cache (${cache.size} remaining)")
        // Refill the slot we just consumed
        prefetch(container.context)
    }

    /** How many fresh (non-expired) ads are ready. */
    val readyCount: Int get() { pruneExpired(); return cache.size }

    /** Re-arm the backoff and refill the cache when the app returns to foreground. */
    fun onForeground(context: Context) {
        retry.reset()
        prefetch(context)
    }

    /** Destroy all cached NativeAd objects. MUST call to avoid memory leaks. */
    fun destroy() {
        retry.cancel()
        cache.forEach { it.ad.destroy() }
        cache.clear()
        activeLoads = 0
    }

    // ── Internal ─────────────────────────────────────────────────────

    private fun loadOne(context: Context, unitId: String) {
        activeLoads++
        AdLoader.Builder(context, unitId)
            .forNativeAd { ad ->
                if (cache.size < config.nativeCacheSize) {
                    cache.addLast(Slot(ad, System.currentTimeMillis()))
                    AdLog.d("native_cache", "cached ad (total=${cache.size})")
                    retry.reset()
                } else {
                    ad.destroy() // cache is full — discard
                }
                activeLoads = (activeLoads - 1).coerceAtLeast(0)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    activeLoads = (activeLoads - 1).coerceAtLeast(0)
                    AdLog.w("native_cache", "load failed: ${error.message}")
                    retry.schedule { loadOne(context, unitId) }
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()
            .loadAd(AdRequest.Builder().build())
    }

    private fun pruneExpired() {
        val now = System.currentTimeMillis()
        val expired = cache.filter { now - it.loadedAt > expiryMs }
        if (expired.isNotEmpty()) {
            AdLog.d("native_cache", "pruning ${expired.size} expired ads")
            expired.forEach { it.ad.destroy() }
            cache.removeAll(expired.toSet())
        }
    }

    private fun buildView(context: Context, ad: NativeAd): View {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.admob_native_ad_template, null) as NativeAdView

        // Restore full-width dropped by inflating with a null root.
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )

        view.headlineView     = view.findViewById(R.id.ad_headline)
        view.bodyView         = view.findViewById(R.id.ad_body)
        view.callToActionView = view.findViewById(R.id.ad_call_to_action)
        view.iconView         = view.findViewById(R.id.ad_app_icon)
        view.mediaView        = view.findViewById(R.id.ad_media)
        view.starRatingView   = view.findViewById(R.id.ad_stars)
        view.advertiserView   = view.findViewById(R.id.ad_advertiser)
        view.priceView        = view.findViewById(R.id.ad_price)
        view.storeView        = view.findViewById(R.id.ad_store)

        (view.headlineView as TextView).text = ad.headline

        fun <T : View> setVisible(v: View?, value: Any?, apply: (T) -> Unit) {
            if (value == null) { v?.visibility = View.GONE } else {
                v?.visibility = View.VISIBLE
                @Suppress("UNCHECKED_CAST")
                apply(v as T)
            }
        }

        setVisible<TextView>(view.bodyView, ad.body)         { it.text = ad.body }
        setVisible<Button>(view.callToActionView, ad.callToAction) { it.text = ad.callToAction }
        setVisible<ImageView>(view.iconView, ad.icon)         { it.setImageDrawable(ad.icon?.drawable) }
        setVisible<MediaView>(view.mediaView, ad.mediaContent){ it.mediaContent = ad.mediaContent }
        setVisible<RatingBar>(view.starRatingView, ad.starRating) { it.rating = ad.starRating!!.toFloat() }
        setVisible<TextView>(view.advertiserView, ad.advertiser) { it.text = ad.advertiser }
        setVisible<TextView>(view.priceView, ad.price)        { it.text = ad.price }
        setVisible<TextView>(view.storeView, ad.store)        { it.text = ad.store }

        view.setNativeAd(ad)
        return view
    }
}
