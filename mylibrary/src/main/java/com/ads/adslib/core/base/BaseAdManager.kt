package com.ads.adslib.core.base

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.ads.adslib.core.callback.AdCallback
import com.ads.adslib.core.callback.AdEvents
import com.ads.adslib.core.callback.FullScreenCallbacks
import com.ads.adslib.core.callback.RewardCallback
import com.ads.adslib.core.model.AdError
import com.ads.adslib.core.model.AdLoadState
import com.ads.adslib.core.model.AdNetwork
import com.ads.adslib.core.model.AdUnitConfig
import com.ads.adslib.core.model.NetworkAdUnit
import com.ads.adslib.util.AdLog

/**
 * Orchestrates a waterfall of [NetworkAdLoader]s for a single placement.
 *
 * Subclasses (one per format) only need to provide [createLoader], which maps a
 * [NetworkAdUnit] to the correct concrete loader. All fallback, state, and threading
 * logic lives here so it is written once and reused across every format.
 */
abstract class BaseAdManager(
    protected val config: AdUnitConfig
) {
    protected val main = Handler(Looper.getMainLooper())

    private var loaders: List<NetworkAdLoader> = emptyList()
    private var loadedLoader: NetworkAdLoader? = null
    private var callback: AdCallback? = null

    @Volatile
    var state: AdLoadState = AdLoadState.IDLE
        private set

    /** Factory: subclass returns the concrete loader for a given network unit, or null if unsupported. */
    protected abstract fun createLoader(unit: NetworkAdUnit): NetworkAdLoader?

    /** The network/loader that successfully loaded, if any. */
    protected val readyLoader: NetworkAdLoader? get() = loadedLoader

    /**
     * Load the placement by walking the waterfall. The first network that loads wins.
     * If all fail, [AdCallback.onFailedToLoad] fires with the last error.
     */
    fun load(context: Context, callback: AdCallback? = null) {
        if (state == AdLoadState.LOADING) {
            AdLog.d(config.placementKey, "load() ignored, already loading")
            return
        }
        if (state == AdLoadState.LOADED) {
            AdLog.d(config.placementKey, "already loaded, skipping")
            val net = loadedLoader?.network
            if (net != null) callback?.let { main.post { it.onLoaded(net) } }
            return
        }

        this.callback = callback
        val activeUnits = config.activeUnits

        if (activeUnits.isEmpty()) {
            AdLog.e(config.placementKey, "No networks configured")
            fail(AdError(activeUnits.firstOrNull()?.network ?: AdNetwork.ADMOB,
                -1, "No active/supported networks for ${config.placementKey}"))
            return
        }

        loaders = activeUnits.mapNotNull { createLoader(it) }
        if (loaders.isEmpty()) {
            fail(AdError(activeUnits.first().network,
                -1, "No supported loader for ${config.placementKey}"))
            return
        }

        state = AdLoadState.LOADING
        attempt(context, 0)
    }

    /** Recursive waterfall step: try loader at [index], fall through on failure. */
    private fun attempt(context: Context, index: Int) {
        // The manager may have been destroyed mid-waterfall — async loader
        // callbacks can fire after destroy() emptied [loaders]. Bail instead of
        // touching an empty list (was crashing on loaders.last()).
        if (loaders.isEmpty()) {
            AdLog.d(config.placementKey, "attempt() after destroy — aborting")
            return
        }
        if (index >= loaders.size) {
            fail(AdError(loaders.last().network, -2,
                "Waterfall exhausted for ${config.placementKey}"))
            return
        }
        val loader = loaders[index]
        AdLog.d(config.placementKey, "trying ${loader.network} (${index + 1}/${loaders.size})")

        loader.load(
            context = context,
            onLoaded = {
                main.post {
                    // Ignore a late success on a destroyed manager.
                    if (loaders.isEmpty()) return@post
                    loadedLoader = loader
                    state = AdLoadState.LOADED
                    AdLog.d(config.placementKey, "loaded via ${loader.network}")
                    callback?.onLoaded(loader.network)
                }
            },
            onFailed = { error ->
                main.post {
                    // Ignore a late failure on a destroyed manager.
                    if (loaders.isEmpty()) return@post
                    AdLog.w(config.placementKey, "fallback: ${loader.network} failed -> $error")
                    loader.destroy()
                    attempt(context, index + 1)
                }
            }
        )
    }

    /** Show the loaded full-screen ad. Returns false if nothing is ready. */
    open fun show(activity: Activity): Boolean {
        val loader = loadedLoader
        if (loader == null || !loader.isReady()) {
            AdLog.w(config.placementKey, "show() called with no ready ad")
            return false
        }
        state = AdLoadState.SHOWING
        loader.show(activity)
        return true
    }

    private fun fail(error: AdError) {
        state = AdLoadState.FAILED
        AdLog.e(config.placementKey, "all networks failed: $error")
        main.post { callback?.onFailedToLoad(error) }
    }

    /** True if a usable ad is loaded right now. */
    fun isReady(): Boolean = loadedLoader?.isReady() == true

    /** Release all loaders and reset state. Call from lifecycle onDestroy. */
    open fun destroy() {
        loaders.forEach { it.destroy() }
        loaders = emptyList()
        loadedLoader = null
        callback = null
        state = AdLoadState.IDLE
    }

    /** Internal helper for subclasses/loaders to surface show-time results to the caller. */
    protected fun dispatch(block: AdCallback.() -> Unit) {
        callback?.let { cb -> main.post { cb.block() } }
    }

    /**
     * Reset to a reloadable state after a full-screen ad is dismissed or fails
     * to show. The consumed loader can no longer serve, so clear it and return
     * [state] to IDLE so a subsequent [load] starts fresh.
     */
    protected fun onFullScreenClosed() {
        loadedLoader = null
        if (state == AdLoadState.SHOWING || state == AdLoadState.LOADED) {
            state = AdLoadState.IDLE
        }
    }

    /**
     * Standard show-time callback wiring for full-screen loaders: every event is
     * marshalled to the main thread via [dispatch], and dismiss / show-failure
     * also reset the manager state through [onFullScreenClosed].
     *
     * Rewards are forwarded only when the active callback is a [RewardCallback].
     */
    protected fun  fullScreenCallbacks(): FullScreenCallbacks = FullScreenCallbacks(
        onShown        = { net -> AdEvents.onAdShown?.invoke(net, config.format); dispatch { onShown(net) } },
        onDismissed    = { net -> onFullScreenClosed(); dispatch { onDismissed(net) } },
        onClicked      = { net -> AdEvents.onAdClicked?.invoke(net, config.format); dispatch { onClicked(net) } },
        onImpression   = { net -> dispatch { onImpression(net) } },
        onFailedToShow = { err -> onFullScreenClosed(); dispatch { onFailedToShow(err) } },
        onRewardEarned = { type, amount ->
            dispatch { (this as? RewardCallback)?.onRewardEarned(type, amount) }
        },
    )
}
