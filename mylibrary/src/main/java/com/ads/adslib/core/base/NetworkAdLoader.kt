package com.ads.adslib.core.base

import android.app.Activity
import android.content.Context
import com.ads.adslib.core.model.AdError
import com.ads.adslib.core.model.AdLoadState
import com.ads.adslib.core.model.AdNetwork
import com.ads.adslib.core.model.NetworkAdUnit

/**
 * One concrete network's implementation for one ad format (e.g. AdMob interstitial).
 *
 * The [BaseAdManager] orchestrates a list of these as a waterfall. Each loader is
 * responsible only for its own SDK calls and reporting load/show results back through
 * the supplied lambdas. It must never reference other networks.
 */
abstract class NetworkAdLoader(
    protected val unit: NetworkAdUnit
) {
    val network: AdNetwork get() = unit.network

    @Volatile
    var state: AdLoadState = AdLoadState.IDLE
        protected set

    /**
     * Begin loading. Implementations MUST eventually call exactly one of [onLoaded]
     * or [onFailed], on the main thread.
     */
    abstract fun load(
        context: Context,
        onLoaded: () -> Unit,
        onFailed: (AdError) -> Unit
    )

    /**
     * Show a previously loaded ad. Only called when [state] == LOADED.
     * Non-full-screen formats (banner/native) may no-op here and expose a view instead.
     */
    abstract fun show(activity: Activity)

    /** Release SDK references. Called on lifecycle teardown. */
    abstract fun destroy()

    /** Whether this loader currently holds a usable, non-expired ad. */
    open fun isReady(): Boolean = state == AdLoadState.LOADED
}
