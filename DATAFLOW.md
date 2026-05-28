# AdsLib — Complete Data Flow Documentation

## 1. Architecture Layers (Top → Bottom)

```
┌─────────────────────────────────────────────────────────────────┐
│  APP LAYER  (com.demo.adslibsss)                                │
│  MainActivity.kt — App entry point                              │
│  ConsentManager → AdsSdk.initialize() → load() → show()        │
└────────────────────────────┬────────────────────────────────────┘
                             │ calls
┌────────────────────────────▼────────────────────────────────────┐
│  MANAGER LAYER  (per-format, one per placement)                 │
│  InterstitialAdManager / RewardedAdManager                      │
│  BannerAdManager / NativeAdManager                              │
│  All extend BaseAdManager — waterfall logic lives here ONLY     │
└────────────────────────────┬────────────────────────────────────┘
                             │ creates & drives
┌────────────────────────────▼────────────────────────────────────┐
│  LOADER LAYER  (per-network × per-format)                       │
│  AdMobInterstitialLoader / MetaInterstitialLoader / Unity...    │
│  AdMobBannerLoader / MetaBannerLoader / UnityBannerLoader       │
│  AdMobRewardedLoader / MetaRewardedLoader / UnityRewardedLoader │
│  AdMobNativeLoader / MetaNativeLoader                           │
│  All extend NetworkAdLoader — touches ONE SDK only              │
└────────────────────────────┬────────────────────────────────────┘
                             │ calls
┌────────────────────────────▼────────────────────────────────────┐
│  SDK LAYER  (third-party)                                        │
│  Google AdMob  │  Meta Audience Network  │  Unity Ads           │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. Core Data Models

### `AdNetwork` — Network identity
```
ADMOB  →  Google AdMob
META   →  Meta Audience Network (FAN)
UNITY  →  Unity Ads
```

### `AdFormat` — Format type
```
BANNER        →  Inline view (320×50 to adaptive)
INTERSTITIAL  →  Full-screen, user can dismiss
REWARDED      →  Full-screen video, reward on completion
NATIVE        →  Custom layout, app assembles the view
APP_OPEN      →  (defined, not yet wired)
SPLASH        →  (defined, not yet wired)
```

### `AdLoadState` — Lifecycle state machine
```
IDLE ──load()──► LOADING ──success──► LOADED ──show()──► SHOWING
  ▲                  │                               │
  │              onFailed                     onDismissed
  │                  │                               │
  │             (next rung)                      DISMISSED
  │              or FAILED                          │
  └──────────────destroy()──────────────────────────┘
```

### `AdUnitConfig` — Placement configuration (app provides this)
```kotlin
AdUnitConfig(
    placementKey = "home_banner",       // unique placement name (for logging)
    format       = AdFormat.BANNER,     // which ad type
    bannerSize   = BannerAdSize.ADAPTIVE,  // only for BANNER
    waterfall    = listOf(              // ordered fallback list
        NetworkAdUnit(AdNetwork.ADMOB,  "admob_unit_id",  enabled = true),
        NetworkAdUnit(AdNetwork.META,   "meta_placement", enabled = true),
        NetworkAdUnit(AdNetwork.UNITY,  "unity_placement",enabled = true),
    )
)
```

### `NetworkAdUnit` — One rung in the waterfall
```
network   → which SDK to use
adUnitId  → the placement ID for that SDK
enabled   → can be toggled via Remote Config
```

### `AdError` — Normalized error (no network-specific types leak out)
```
network → which network failed
code    → network's own error code
message → human-readable description
```

---

## 3. Full Data Flow — Step by Step

### Step 1 — Consent (GDPR / CCPA)

```
Activity.onCreate()
    │
    ▼
ConsentManager(activity)
    │
    ▼
consentInformation.requestConsentInfoUpdate()   ← Google UMP SDK
    │
    ├── [EEA user / first run]
    │       ▼
    │   UserMessagingPlatform.loadAndShowConsentFormIfRequired()
    │       ▼
    │   User taps Accept / Decline
    │       ▼
    │   onComplete() fires
    │
    └── [non-EEA / already consented]
            ▼
        onComplete() fires immediately
            │
            ▼
    consent.canRequestAds  ──true──►  Step 2
                           ──false──► STOP (no ads loaded)
```

### Step 2 — SDK Initialization

```
AdsSdk.initialize(context, debug, testDeviceIds) { onComplete }
    │
    ├── AdLog.enabled = debug            ← logging on/off
    ├── [if already initialized] → onComplete() immediately
    │
    ├── [debug + testDeviceIds not empty]
    │       MobileAds.setRequestConfiguration(testDeviceIds)
    │
    ▼
MobileAds.initialize(context)           ← AdMob init
    .apply {
        initialized = true
        onComplete()                    ← fires on main thread
    }
    │
    │  (Meta / Unity init hooks go here when those modules are added)
    ▼
App: loadInterstitial() + loadRewarded() + loadBanner() + loadNative()
```

### Step 3 — Load: Waterfall Engine (`BaseAdManager.load()`)

```
manager.load(context, callback)
    │
    ├── [state == LOADING]  →  ignore (already loading)
    ├── [state == LOADED]   →  callback.onLoaded() immediately
    │
    ▼
config.activeUnits  =  waterfall.filter { enabled && adUnitId.isNotBlank() }
    │
    ▼
loaders = activeUnits.mapNotNull { createLoader(it) }
    │
    ├── [loaders empty]  →  fail() → callback.onFailedToLoad()
    │
    ▼
state = LOADING
attempt(context, index = 0)
```

### Step 4 — Waterfall Attempt (recursive)

```
attempt(context, index)
    │
    ├── [index >= loaders.size]
    │       ▼
    │   fail(AdError("Waterfall exhausted"))
    │   callback.onFailedToLoad()  ← STOP
    │
    ▼
loader = loaders[index]          ← e.g. AdMobInterstitialLoader
AdLog: "trying ADMOB (1/3)"
    │
loader.load(context,
    onLoaded = {
        main.post {
            loadedLoader = loader
            state = LOADED
            callback.onLoaded(loader.network)   ← SUCCESS ✓
        }
    },
    onFailed = { error ->
        main.post {
            AdLog: "fallback: ADMOB failed → error"
            loader.destroy()
            attempt(context, index + 1)         ← TRY NEXT ↓
        }
    }
)
```

### Step 5 — Loader: Network SDK Call

Each loader touches exactly one SDK. Example — `AdMobInterstitialLoader`:

```
loader.load(context, onLoaded, onFailed)
    │
    ▼
InterstitialAd.load(context, adUnitId, AdRequest, callback)
    │
    ├── SDK: onAdLoaded(ad)
    │       ad = loaded
    │       state = LOADED
    │       attachFullScreenCallbacks()   ← wire shown/dismissed/clicked
    │       onLoaded()                   → BaseAdManager records winner
    │
    └── SDK: onAdFailedToLoad(error)
            state = FAILED
            onFailed(AdError(ADMOB, code, msg)) → BaseAdManager tries next
```

### Step 6 — Show

```
manager.show(activity)
    │
    ├── [loadedLoader == null || !isReady()]
    │       AdLog: "show() called with no ready ad"
    │       return false
    │
    ▼
state = SHOWING
loadedLoader.show(activity)
    │
    ▼ (AdMob example)
ad.show(activity)               ← triggers SDK overlay
    │
    ├── onAdShowedFullScreenContent → onShownCb → dispatch { onShown(net) }
    ├── onAdClicked                 → onClickedCb
    ├── onAdImpression              → onImpressionCb
    ├── onAdDismissedFullScreenContent
    │       state = DISMISSED
    │       ad = null
    │       onDismissedCb → dispatch { onDismissed(net) }
    │           ↓
    │       App: manager.destroy(); load() again   ← reload cycle
    │
    └── onAdFailedToShowFullScreenContent
            state = FAILED
            onFailedToShowCb → dispatch { onFailedToShow(err) }
```

---

## 4. Format-Specific Flows

### 4A. Interstitial

```
App: InterstitialAdManager(config).load(context, AdCallback)
         ↓ waterfall
     AdMobInterstitialLoader  OR  MetaInterstitialLoader  OR  UnityInterstitialLoader
         ↓ on success
     isReady() == true
         ↓ user action (button click, level complete)
     manager.show(activity)
         ↓
     Full-screen overlay shown
         ↓ user dismisses
     onDismissed() → app reloads
```

### 4B. Rewarded

```
App: RewardedAdManager(config).load(context, RewardCallback)
         ↓ waterfall
     AdMobRewardedLoader  OR  MetaRewardedLoader  OR  UnityRewardedLoader
         ↓ on success
     isReady() == true
         ↓ user taps "Watch Ad" button
     manager.show(activity)
         ↓
     Video plays
         ├── [User watches to end]
         │       onRewardEarned(type, amount)   ← GIVE REWARD HERE
         │       onDismissed()
         │
         └── [User skips (Unity only)]
                 onDismissed()   ← NO reward (SKIPPED state)
```

**Reward values per network:**
| Network | type | amount |
|---------|------|--------|
| AdMob   | From ad unit config (e.g. "coins") | From ad unit config (e.g. 10) |
| Meta FAN | `"reward"` (fixed) | `1` (fixed — FAN no dynamic) |
| Unity   | `"reward"` (fixed) | `1` only when COMPLETED |

### 4C. Banner

```
App: BannerAdManager(config).load(context, AdCallback)
         ↓ waterfall
     AdMobBannerLoader  OR  MetaBannerLoader  OR  UnityBannerLoader
         ↓ on success (view is already built)
     AdCallback.onLoaded(network)
         ↓
     manager.attach(container: ViewGroup)
         ↓
     container.removeAllViews()
     container.addView(loadedLoader.getView())  ← AdView/FanAdView/BannerView

Lifecycle (IMPORTANT — banner must be paused/resumed):
     Activity.onPause()  → manager.pause()  → adView.pause()
     Activity.onResume() → manager.resume() → adView.resume()
     Activity.onDestroy()→ manager.destroy()→ adView.destroy()
```

**BannerAdSize mapping:**
| `BannerAdSize` | AdMob | Meta FAN | Unity |
|---|---|---|---|
| BANNER | 320×50 | BANNER_HEIGHT_50 | 320×50 |
| LARGE_BANNER | 320×100 | BANNER_HEIGHT_90 | 320×100 |
| MEDIUM_RECTANGLE | 300×250 | RECTANGLE_HEIGHT_250 | 300×250 |
| ADAPTIVE | device-width adaptive | BANNER_HEIGHT_50 | 320×50 |

### 4D. Native

```
App: NativeAdManager(config).load(context, AdCallback)
         ↓ waterfall
     AdMobNativeLoader  OR  MetaNativeLoader  (Unity: not supported)
         ↓ on success
     Loader inflates template XML + populates all asset views
         ↓
     AdCallback.onLoaded(network)
         ↓
     manager.attach(container: ViewGroup)
         ↓
     container.removeAllViews()
     container.addView(populatedNativeAdView)

AdMob native view population:
     NativeAdView.headlineView   ← ad.headline
     NativeAdView.bodyView       ← ad.body
     NativeAdView.iconView       ← ad.icon.drawable
     NativeAdView.mediaView      ← ad.mediaContent
     NativeAdView.callToActionView ← ad.callToAction
     NativeAdView.starRatingView ← ad.starRating
     NativeAdView.setNativeAd(ad)  ← MUST be last

Meta native view population:
     nativeAd.advertiserName     → native_ad_title
     nativeAd.adBodyText         → native_ad_body
     nativeAd.adSocialContext    → native_ad_social_context
     nativeAd.adCallToAction     → native_ad_call_to_action
     AdOptionsView               → ad_choices_container (policy required)
     nativeAd.registerViewForInteraction(view, mediaView, iconView, clicks)

Teardown (IMPORTANT — prevents memory leak):
     AdMob: nativeAd.destroy()
     Meta:  nativeAd.unregisterView() + nativeAd.destroy()
```

---

## 5. Callback Flow (Thread Safety)

All public callbacks fire on the **main thread**. The flow:

```
SDK callback (any thread)
    │
    ▼
NetworkAdLoader: onLoaded() / onFailed() lambdas called
    │
    ▼
BaseAdManager: main.post { ... }        ← Handler(Looper.getMainLooper())
    │
    ▼
BaseAdManager.dispatch { callback.onXxx() }
    │
    ▼
App: AdCallback / RewardCallback methods  ← always on main thread ✓
```

**`dispatch {}` helper (BaseAdManager):**
```kotlin
protected fun dispatch(block: AdCallback.() -> Unit) {
    callback?.let { cb -> main.post { cb.block() } }
}
```

---

## 6. Waterfall Fallback — Visual Example

Config with 3 networks:
```
waterfall = [ADMOB, META, UNITY]
```

Scenario: AdMob fails, Meta fails, Unity succeeds:

```
attempt(0) → AdMobInterstitialLoader.load()
                 ↓ SDK: onAdFailedToLoad (e.g. no fill)
             loader.destroy()
             attempt(1)
                 ↓
             MetaInterstitialLoader.load()
                 ↓ SDK: onError (e.g. network error)
             loader.destroy()
             attempt(2)
                 ↓
             UnityInterstitialLoader.load()
                 ↓ SDK: onUnityAdsAdLoaded ✓
             loadedLoader = UnityInterstitialLoader
             state = LOADED
             callback.onLoaded(AdNetwork.UNITY)
```

If ALL fail:
```
attempt(3) → index >= loaders.size
             fail(AdError(lastNetwork, -2, "Waterfall exhausted"))
             callback.onFailedToLoad(error)
```

---

## 7. Package Structure & Responsibilities

```
com.ads.adslib
│
├── AdsSdk.kt                          Entry point — init once, gate on consent
│
├── core/
│   ├── model/
│   │   ├── AdEnums.kt                 AdNetwork, AdFormat, AdLoadState, BannerAdSize
│   │   └── AdModels.kt                AdError, NetworkAdUnit, AdUnitConfig
│   ├── callback/
│   │   └── AdCallback.kt             AdCallback (base), RewardCallback
│   └── base/
│       ├── BaseAdManager.kt           ALL waterfall logic — never duplicate
│       └── NetworkAdLoader.kt         Abstract loader — 1 network × 1 format
│
├── admob/
│   ├── interstitial/
│   │   ├── AdMobInterstitialLoader    InterstitialAd SDK calls
│   │   └── InterstitialAdManager     createLoader() only
│   ├── rewarded/
│   │   ├── AdMobRewardedLoader        RewardedAd + OnUserEarnedRewardListener
│   │   └── RewardedAdManager         load(RewardCallback), dispatchReward()
│   ├── banner/
│   │   ├── AdMobBannerLoader          AdView, BannerAdSize→AdSize conversion
│   │   └── BannerAdManager           attach(container), pause(), resume()
│   └── native_ad/
│       ├── AdMobNativeLoader          AdLoader, inflate+populate NativeAdView
│       └── NativeAdManager           attach(container)
│
├── meta/
│   ├── interstitial/MetaInterstitialLoader    FAN InterstitialAd
│   ├── rewarded/MetaRewardedLoader            FAN RewardedVideoAd
│   ├── banner/MetaBannerLoader                FAN AdView, FanAdSize conversion
│   └── native_ad/MetaNativeLoader             FAN NativeAd, registerViewForInteraction
│
├── unity/
│   ├── interstitial/UnityInterstitialLoader   UnityAds.load + UnityAds.show
│   ├── rewarded/UnityRewardedLoader           COMPLETED→reward, SKIPPED→no reward
│   └── banner/UnityBannerLoader               BannerView (requires Activity context)
│
├── consent/ConsentManager.kt          Google UMP wrapper — canRequestAds gate
├── config/RemoteConfigManager.kt      Firebase Remote Config — toggle ads remotely
└── util/AdLog.kt                      Logging — off in release
```

---

## 8. State Ownership Rules

| State | Owner |
|-------|-------|
| `AdLoadState` per loader | `NetworkAdLoader.state` |
| `AdLoadState` per placement | `BaseAdManager.state` |
| `loadedLoader` reference | `BaseAdManager` (private) |
| `callback` reference | `BaseAdManager` (private) |
| `rewardCallback` reference | `RewardedAdManager` (private) |
| SDK ad object (e.g. `InterstitialAd`) | `NetworkAdLoader` subclass |
| SDK view object (e.g. `AdView`) | `NetworkAdLoader` subclass |

---

## 9. How to Add a New Network (e.g. AppLovin)

```
1. Create loader:
   applovin/interstitial/AppLovinInterstitialLoader : NetworkAdLoader
       → implement load(), show(), destroy()
       → call onLoaded() / onFailed() on main thread

2. Add network enum value:
   AdEnums.kt → enum class AdNetwork { ADMOB, META, UNITY, APPLOVIN }

3. Wire into manager:
   InterstitialAdManager.createLoader():
       AdNetwork.APPLOVIN -> AppLovinInterstitialLoader(unit, ...)

4. App usage:
   waterfall = listOf(
       NetworkAdUnit(AdNetwork.ADMOB,    "admob_id"),
       NetworkAdUnit(AdNetwork.APPLOVIN, "applovin_id"),
   )
```

---

## 10. How to Add a New Format (e.g. App Open)

```
1. Create AdMob loader:
   admob/appopen/AdMobAppOpenLoader : NetworkAdLoader
       → use AppOpenAd.load() + AppOpenAd.show()

2. Create manager:
   admob/appopen/AppOpenAdManager : BaseAdManager
       override fun createLoader(unit) = when(unit.network) {
           AdNetwork.ADMOB -> AdMobAppOpenLoader(...)
           else -> null
       }

3. App usage:
   val mgr = AppOpenAdManager(config)
   mgr.load(context, callback)
   mgr.show(activity)   ← in onStart() or after splash
```

---

## 11. Lifecycle Checklist

| Ad Type | onPause | onResume | onDestroy |
|---------|---------|----------|-----------|
| Interstitial | — | — | `manager.destroy()` |
| Rewarded | — | — | `manager.destroy()` |
| Banner | `manager.pause()` | `manager.resume()` | `manager.destroy()` |
| Native | — | — | `manager.destroy()` ← calls `nativeAd.destroy()` |

---

## 12. Remote Config Integration

```
AdsSdk.remoteConfig.init(
    defaults = mapOf(
        "ads_enabled"           to true,
        "interstitial_enabled"  to true,
        "interstitial_interval" to 60L,
    )
)

// Before loading:
if (!AdsSdk.remoteConfig.isAdsEnabled()) return

// Disable a specific network dynamically:
NetworkAdUnit(AdNetwork.META, "meta_id", enabled = remoteConfig.getBoolean("meta_enabled"))
```

---

## 13. Quick Reference — Complete Boot Sequence

```
App Launch
    │
    ▼
ConsentManager.gatherConsent()
    │ canRequestAds == true
    ▼
AdsSdk.initialize(debug = BuildConfig.DEBUG)
    │ MobileAds ready
    ▼
┌───────────────────────────────────────────┐
│  Parallel load:                           │
│  InterstitialAdManager.load()             │
│  RewardedAdManager.load()                 │
│  BannerAdManager.load() → attach()        │
│  NativeAdManager.load() → attach()        │
└───────────────────────────────────────────┘
    │ each walks its waterfall independently
    ▼
AdCallback.onLoaded(network)   ← per format
    │
    ▼ [user action]
manager.show(activity)          ← interstitial / rewarded only
    │
    ▼
onDismissed() → manager.destroy() → load() again
```
