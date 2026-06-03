package com.ads.adslib

import android.content.Context
import com.ads.adslib.config.RemoteConfigManager
import com.ads.adslib.util.AdLog
import com.facebook.ads.AdSettings
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.unity3d.mediation.LevelPlay
import com.unity3d.mediation.LevelPlayConfiguration
import com.unity3d.mediation.LevelPlayInitError
import com.unity3d.mediation.LevelPlayInitListener
import com.unity3d.mediation.LevelPlayInitRequest

/**
 * Single entry point for the Ads library — initialize once, then create per-placement managers.
 *
 * Typical splash-screen flow:
 * ```
 * val consent = ConsentManager(this)
 * consent.gatherConsent(this) {
 *     if (consent.canRequestAds) {
 *         AdsSdk.initialize(this) {
 *             // SDKs ready — start loading ads / dismiss splash
 *         }
 *     }
 * }
 * ```
 */
object AdsSdk {

    @Volatile
    private var initialized = false

    @Volatile
    private var initializing = false

    /** Callbacks waiting for an in-flight initialization to finish. */
    private val pendingCallbacks = mutableListOf<() -> Unit>()

    /**
     * LevelPlay init is asynchronous and independent of AdMob's init. Ad loads are
     * kicked once AdMob (MobileAds) is ready, which can happen BEFORE LevelPlay's
     * onInitSuccess — so an ironSource rung reached on first launch would fail with
     * "load before init success". These track LevelPlay's own readiness so the
     * ironSource loaders can defer until it is genuinely ready.
     */
    private enum class LevelPlayState { PENDING, READY, FAILED }

    @Volatile
    private var levelPlayState = LevelPlayState.PENDING

    /** Waiters queued while LevelPlay init is still PENDING: (onReady, onFailed). */
    private val levelPlayWaiters = mutableListOf<Pair<() -> Unit, () -> Unit>>()

    /** App context + consent captured at init, so LevelPlay can be (re)kicked when
     *  a late Remote Config delivers the app key. */
    @Volatile
    private var appContextRef: Context? = null

    @Volatile
    private var lastConsent: Boolean = true

    /** True once LevelPlay.init() was actually invoked with a non-blank app key. */
    @Volatile
    private var levelPlayInitAttempted = false

    /** Optional remote-config handle, exposed for app-side checks. */
    val remoteConfig: RemoteConfigManager by lazy { RemoteConfigManager() }

    /**
     * Initialize LevelPlay with [appKey] if it hasn't been attempted yet. Safe to
     * call repeatedly — only the first non-blank key actually triggers init.
     *
     * This exists because the app key arrives from Remote Config, which on first
     * launch may not be fetched/activated yet when [initialize] runs (the ads_config
     * read is synchronous). When the fresh config later lands,
     * [com.ads.adslib.config.remote.AdsConfigRepository.load] calls this so LevelPlay
     * inits with the real key instead of staying uninitialized until the next launch.
     */
    fun ensureLevelPlayInit(appKey: String?) {
        val ctx = appContextRef ?: return        // initialize() not run yet
        if (appKey.isNullOrBlank()) return        // no real key yet — may arrive later
        val proceed = synchronized(this) {
            if (levelPlayInitAttempted) false
            else { levelPlayInitAttempted = true; true }
        }
        if (proceed) initLevelPlay(ctx, appKey, lastConsent)
    }

    /**
     * Run [onReady] once LevelPlay/ironSource init has succeeded, or [onFailed] if
     * it failed (or no app key was configured). Fires immediately if the state is
     * already known; otherwise queues until LevelPlay's init callback arrives.
     *
     * ironSource loaders call this before loading so they never request an ad
     * before LevelPlay is initialized (fixes "ad only on second app open").
     */
    fun whenLevelPlayReady(onReady: () -> Unit, onFailed: () -> Unit) {
        val state = synchronized(this) {
            if (levelPlayState == LevelPlayState.PENDING) {
                levelPlayWaiters.add(onReady to onFailed)
            }
            levelPlayState
        }
        when (state) {
            LevelPlayState.READY   -> onReady()
            LevelPlayState.FAILED  -> onFailed()
            LevelPlayState.PENDING -> { /* queued — fires from setLevelPlayState */ }
        }
    }

    /** Resolve LevelPlay readiness and flush queued waiters (main thread). */
    private fun setLevelPlayState(ready: Boolean) {
        val waiters = synchronized(this) {
            levelPlayState = if (ready) LevelPlayState.READY else LevelPlayState.FAILED
            levelPlayWaiters.toList().also { levelPlayWaiters.clear() }
        }
        waiters.forEach { (onReady, onFailed) -> if (ready) onReady() else onFailed() }
    }

    /**
     * Initialize underlying ad SDKs. Safe to call multiple times — initialization
     * runs only once; concurrent callers all get notified when it completes.
     *
     * @param context application context.
     * @param debug enables verbose logging and registers test devices.
     * @param hasConsent the user's GDPR/CCPA consent decision. MUST reflect the
     *   real UMP result — it is forwarded to Meta and Unity so they comply too.
     * @param testDeviceIds AdMob test device IDs (debug only).
     * @param ironSourceAppKey ironSource / Unity LevelPlay app key (from Remote
     *   Config). Null/blank skips LevelPlay. Unlike AdMob, LevelPlay has no test
     *   ad-unit ids — a real app key is required even to initialize.
     * @param onComplete called on the main thread when AdMob initialization finishes.
     */
    fun initialize(
        context: Context,
        debug: Boolean = false,
        hasConsent: Boolean = true,
        testDeviceIds: List<String> = emptyList(),
        metaTestDeviceHashes: List<String> = emptyList(),
        ironSourceAppKey: String? = null,
        onComplete: () -> Unit = {}
    ) {
        // Guard the check-then-act atomically — @Volatile alone does NOT make
        // this compound action thread-safe.
        synchronized(this) {
            AdLog.enabled = debug

            // Already done — fire immediately.
            if (initialized) {
                onComplete()
                return
            }

            // Init in flight — queue this callback, don't start a second init.
            pendingCallbacks.add(onComplete)
            if (initializing) return

            initializing = true
        }

        val appContext = context.applicationContext
        appContextRef = appContext
        lastConsent = hasConsent

        if (debug && testDeviceIds.isNotEmpty()) {
            MobileAds.setRequestConfiguration(
                RequestConfiguration.Builder()
                    .setTestDeviceIds(testDeviceIds)
                    .build()
            )
        }

        // Meta Audience Network — init + forward consent BEFORE any FAN ad loads.
        initMeta(appContext, hasConsent, if (debug) metaTestDeviceHashes else emptyList())
        if(debug){
            AdSettings.setTestMode(false)
        }

        // ironSource / Unity LevelPlay inits independently of AdMob (its own SDK).
        // Routed through ensureLevelPlayInit so a blank key now (Remote Config not
        // yet activated on first launch) can still init later when the real key
        // arrives via AdsConfigRepository.load(), instead of waiting a whole launch.
        ensureLevelPlayInit(ironSourceAppKey)

        // BUG FIX: use the listener overload so callbacks fire only AFTER
        // MobileAds is actually initialized (the previous `.apply {}` ran the
        // block synchronously, before init had completed).
        MobileAds.initialize(appContext) {
            AdLog.d("sdk", "MobileAds initialized")
            val callbacks = synchronized(this) {
                initialized = true
                initializing = false
                pendingCallbacks.toList().also { pendingCallbacks.clear() }
            }
            callbacks.forEach { it() }
        }
    }

    /**
     * Initialize Meta Audience Network and forward the consent decision.
     * Wrapped in runCatching so a missing/incompatible FAN build never crashes
     * host SDK init — the Meta waterfall rung simply fails later if absent.
     */
    private fun initMeta(context: Context, hasConsent: Boolean, testDeviceHashes: List<String>) {
        runCatching {
            // setDataProcessingOptions(emptyArray) = no restriction (consent given);
            // ["LDU"] = Limited Data Use for opted-out / CCPA users.
            if (hasConsent) {
                AdSettings.setDataProcessingOptions(arrayOf())
            } else {
                AdSettings.setDataProcessingOptions(arrayOf("LDU"), 0, 0)
            }
            // Debug only — register Meta test devices so FAN serves test ads
            // instead of "No fill". Each device's hash is printed in logcat by the
            // Meta SDK on first request ("add AdSettings.addTestDevice(...)").
            if (testDeviceHashes.isNotEmpty()) {
                AdSettings.addTestDevices(testDeviceHashes)
            }
            if (!AudienceNetworkAds.isInitialized(context)) {
                AudienceNetworkAds.initialize(context)
            }
            AdLog.d("sdk", "Meta Audience Network initialized (consent=$hasConsent)")
        }.onFailure { AdLog.w("sdk", "Meta init skipped: ${it.message}") }
    }

    fun isInitialized(): Boolean = initialized

    /**
     * Initialize ironSource / Unity LevelPlay once, if an app key is configured.
     * The LevelPlay rung internally mediates ironSource + Unity demand, so this
     * replaces the former standalone Unity Ads init.
     */
    private fun initLevelPlay(context: Context, appKey: String?, hasConsent: Boolean) {
        // Defensive — callers (ensureLevelPlayInit) already guard blank keys. We do
        // NOT mark FAILED here: a real key may still arrive via a later Remote Config
        // load, and FAILED would make ironSource rungs give up permanently.
        if (appKey.isNullOrBlank()) return

        // Forward GDPR/CCPA consent to LevelPlay BEFORE init (ironSource policy).
        // From ironSource SDK 7.7.0+ Google UMP consent is auto-forwarded too, but
        // we set it explicitly so non-UMP paths still comply.
        runCatching {
            LevelPlay.setConsent(hasConsent)
            // CCPA / US state privacy: opted-out users restrict sale/sharing.
            LevelPlay.setMetaData("do_not_sell", if (hasConsent) "false" else "true")
        }.onFailure { AdLog.w("sdk", "LevelPlay consent metadata failed: ${it.message}") }

        runCatching {
            val initRequest = LevelPlayInitRequest.Builder(appKey).build()
            LevelPlay.init(
                context.applicationContext,
                initRequest,
                object : LevelPlayInitListener {
                    override fun onInitSuccess(configuration: LevelPlayConfiguration) {
                        AdLog.d("sdk", "LevelPlay initialized")
                        setLevelPlayState(true)
                    }

                    override fun onInitFailed(error: LevelPlayInitError) {
                        AdLog.w("sdk", "LevelPlay init failed: $error")
                        setLevelPlayState(false)
                    }
                }
            )
        }.onFailure {
            AdLog.w("sdk", "LevelPlay init skipped: ${it.message}")
            setLevelPlayState(false)
        }
    }
}
