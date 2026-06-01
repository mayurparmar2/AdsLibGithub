package com.ads.adslib.core.callback

import com.ads.adslib.core.model.AdFormat
import com.ads.adslib.core.model.AdNetwork

/**
 * Optional app-wide ad event hooks for analytics. The host app may set these
 * once (e.g. in Application.onCreate) to forward library ad events to its own
 * analytics. All callbacks fire on the main thread; null = ignored.
 *
 * These are deliberately analytics-only and decoupled from the per-placement
 * [AdCallback] flow so the host doesn't have to wire a callback into every
 * manager just to count impressions/clicks.
 */
object AdEvents {

    /** A full-screen App Open ad was shown. */
    @Volatile
    var onAppOpenShown: (() -> Unit)? = null

    /** Any ad ([format] via [network]) was clicked. */
    @Volatile
    var onAdClicked: ((network: AdNetwork, format: AdFormat) -> Unit)? = null

    /** Any full-screen ad ([format] via [network]) was shown. */
    @Volatile
    var onAdShown: ((network: AdNetwork, format: AdFormat) -> Unit)? = null
}
