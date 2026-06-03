package com.ads.adslib.ironsource.native_ad

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.ads.adslib.AdsSdk
import com.ads.adslib.core.base.NetworkAdLoader
import com.ads.adslib.core.model.AdError
import com.ads.adslib.core.model.AdLoadState
import com.ads.adslib.core.model.AdNetwork
import com.ads.adslib.core.model.NativeAdStyle
import com.ads.adslib.core.model.NetworkAdUnit
import com.ads.adslib.util.AdLog
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo
import com.ironsource.mediationsdk.ads.nativead.LevelPlayMediaView
import com.ironsource.mediationsdk.ads.nativead.LevelPlayNativeAd
import com.ironsource.mediationsdk.ads.nativead.LevelPlayNativeAdListener
import com.ironsource.mediationsdk.ads.nativead.NativeAdLayout
import com.ironsource.mediationsdk.logger.IronSourceError

/**
 * ironSource / Unity LevelPlay native — one waterfall rung (the last fallback,
 * after AdMob and Meta native).
 *
 * LevelPlay native has no XML template like AdMob/Meta; the host registers its
 * own views via [NativeAdLayout]. This loader builds a simple, self-contained
 * native layout programmatically (icon + title/advertiser + media + body + CTA),
 * themed with the optional [NativeAdStyle], and exposes it via [getView] so it
 * slots into the existing [com.ads.adslib.admob.native_ad.NativeAdManager] flow.
 */
class IronSourceNativeLoader(
    unit: NetworkAdUnit,
    private val style: NativeAdStyle?,
    private val onClickedCb: (AdNetwork) -> Unit,
    private val onImpressionCb: (AdNetwork) -> Unit,
) : NetworkAdLoader(unit) {

    private var nativeAd: LevelPlayNativeAd? = null
    private var adView: NativeAdLayout? = null

    override fun load(context: Context, onLoaded: () -> Unit, onFailed: (AdError) -> Unit) {
        val placement = unit.adUnitId.ifBlank { "<default>" }
        AdLog.d(TAG, "load requested — placement='$placement'")
        // LevelPlayNativeAd.Builder requires an Activity context.
        val activity = context as? Activity ?: run {
            AdLog.w(TAG, "FAILED — needs Activity context, got ${context.javaClass.simpleName}")
            onFailed(AdError(AdNetwork.IRONSOURCE, -1, "IronSourceNativeLoader requires Activity context"))
            return
        }

        state = AdLoadState.LOADING
        // Defer until LevelPlay init has completed (see IronSourceInterstitialLoader).
        AdLog.d(TAG, "waiting for LevelPlay init…")
        AdsSdk.whenLevelPlayReady(
            onReady = { startLoad(activity, onLoaded, onFailed) },
            onFailed = {
                AdLog.w(TAG, "FAILED — LevelPlay not initialized (placement='$placement')")
                state = AdLoadState.FAILED
                onFailed(AdError(AdNetwork.IRONSOURCE, 508, "LevelPlay not initialized"))
            }
        )
    }

    private fun startLoad(activity: Activity, onLoaded: () -> Unit, onFailed: (AdError) -> Unit) {
        AdLog.d(TAG, "init ready → loadAd() placement='${unit.adUnitId.ifBlank { "<default>" }}'")
        val ad = LevelPlayNativeAd.Builder()
            .withActivity(activity)
            // unit.adUnitId carries the configured placement name (LevelPlay native
            // uses placement names); blank → default placement.
            .apply { if (unit.adUnitId.isNotBlank()) withPlacementName(unit.adUnitId) }
            .withListener(object : LevelPlayNativeAdListener {
                override fun onAdLoaded(loaded: LevelPlayNativeAd?, adInfo: AdInfo?) {
                    val resolved = loaded ?: nativeAd
                    if (resolved == null) {
                        AdLog.w(TAG, "FAILED — onAdLoaded gave a null native ad")
                        state = AdLoadState.FAILED
                        onFailed(AdError(AdNetwork.IRONSOURCE, -1, "native loaded null"))
                        return
                    }
                    AdLog.d(TAG, "LOADED — title='${resolved.title}' net='${adInfo?.adNetwork}'")
                    nativeAd = resolved
                    adView = buildView(activity, resolved)
                    state = AdLoadState.LOADED
                    onLoaded()
                }

                override fun onAdLoadFailed(failed: LevelPlayNativeAd?, error: IronSourceError?) {
                    AdLog.w(TAG, "FAILED — code=${error?.errorCode} msg='${error?.errorMessage}'")
                    state = AdLoadState.FAILED
                    onFailed(
                        AdError(
                            AdNetwork.IRONSOURCE,
                            error?.errorCode ?: -1,
                            error?.errorMessage ?: "native load failed",
                        )
                    )
                }

                override fun onAdClicked(clicked: LevelPlayNativeAd?, adInfo: AdInfo?) {
                    AdLog.d(TAG, "clicked")
                    onClickedCb(AdNetwork.IRONSOURCE)
                }

                override fun onAdImpression(shown: LevelPlayNativeAd?, adInfo: AdInfo?) {
                    AdLog.d(TAG, "impression")
                    onImpressionCb(AdNetwork.IRONSOURCE)
                }
            })
            .build()

        nativeAd = ad
        ad.loadAd()
    }

    /** Build + populate + register a minimal themed native layout. */
    private fun buildView(context: Context, ad: LevelPlayNativeAd): NativeAdLayout {
        val density = context.resources.displayMetrics.density
        fun dp(v: Int) = (v * density).toInt()

        val bg = style?.backgroundColor ?: Color.WHITE
        val titleColor = style?.titleColor ?: Color.parseColor("#111111")
        val bodyColor = style?.bodyColor ?: Color.parseColor("#666666")
        val ctaText = style?.ctaTextColor ?: Color.WHITE
        val ctaBg = style?.ctaBackgroundColor ?: Color.parseColor("#2962FF")

        val layout = NativeAdLayout(context).apply {
            setBackgroundColor(bg)
            setPadding(dp(12), dp(12), dp(12), dp(12))
        }
        val content = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(MATCH, WRAP)
        }

        // Top row: icon + (title / advertiser)
        val topRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(MATCH, WRAP)
        }
        val iconView = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(dp(40), dp(40)).also { it.rightMargin = dp(10) }
            ad.icon?.drawable?.let { setImageDrawable(it) }
        }
        val titleCol = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, WRAP, 1f)
        }
        val titleView = TextView(context).apply {
            text = ad.title
            setTextColor(titleColor)
            textSize = 15f
            setTypeface(typeface, Typeface.BOLD)
            maxLines = 1
        }
        val advertiserView = TextView(context).apply {
            text = ad.advertiser
            setTextColor(bodyColor)
            textSize = 12f
            maxLines = 1
        }
        titleCol.addView(titleView)
        titleCol.addView(advertiserView)
        topRow.addView(iconView)
        topRow.addView(titleCol)

        // Media
        val mediaView = LevelPlayMediaView(context).apply {
            layoutParams = LinearLayout.LayoutParams(MATCH, dp(140)).also { it.topMargin = dp(10) }
        }

        // Body
        val bodyView = TextView(context).apply {
            text = ad.body
            setTextColor(bodyColor)
            textSize = 13f
            maxLines = 2
            layoutParams = LinearLayout.LayoutParams(MATCH, WRAP).also { it.topMargin = dp(8) }
        }

        // CTA
        val ctaView = Button(context).apply {
            text = ad.callToAction
            setTextColor(ctaText)
            setBackgroundColor(ctaBg)
            isAllCaps = false
            layoutParams = LinearLayout.LayoutParams(MATCH, WRAP).also { it.topMargin = dp(10) }
        }

        content.addView(topRow)
        content.addView(mediaView)
        content.addView(bodyView)
        content.addView(ctaView)
        layout.addView(content)

        // Register views so LevelPlay wires impression/click tracking + media.
        layout.setTitleView(titleView)
        layout.setAdvertiserView(advertiserView)
        layout.setIconView(iconView)
        layout.setMediaView(mediaView)
        layout.setBodyView(bodyView)
        layout.setCallToActionView(ctaView)
        layout.registerNativeAdViews(ad)
        return layout
    }

    override fun show(activity: Activity) {} // no-op — native is a View

    override fun getView(): View? = adView

    override fun isReady(): Boolean = adView != null && state == AdLoadState.LOADED

    override fun destroy() {
        nativeAd?.destroyAd()
        nativeAd = null
        adView = null
        state = AdLoadState.IDLE
    }

    private companion object {
        const val TAG = "is_native"
        const val MATCH = ViewGroup.LayoutParams.MATCH_PARENT
        const val WRAP = ViewGroup.LayoutParams.WRAP_CONTENT
    }
}
