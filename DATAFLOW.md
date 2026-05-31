# AdsLib — Complete Data Flow Documentation

> Reflects the current architecture: three networks (AdMob / Meta / Unity), all
> formats (Banner, Interstitial, Native, Rewarded, App Open), the **opt-in
> SmartAdManager** preloading layer, and the **Remote-Config-driven**
> `AdsConfigRepository`.

## 1. Architecture Layers (Top → Bottom)

```
┌─────────────────────────────────────────────────────────────────┐
│  APP LAYER  (com.demo.adslibsss)                                  │
│  App : MyLibrary()          — Application; bootstraps Remote Config│
│  MainActivity               — AdsLib.initWithActivity(...) then    │
│                               per-screen load()/show()/attach()    │
└───────────────┬───────────────────────────────┬──────────────────┘
                │ entry points                   │
┌───────────────▼───────────────┐   ┌────────────▼─────────────────┐
│  ENTRY / INIT                  │   │  SMART PRELOADING (opt-in)    │
│  AdsLib.initWithActivity       │   │  SmartAdManager               │
│  → ConsentManager (UMP)        │   │  ├ InterstitialPreloader      │
│  → AdsSdk.initialize           │   │  ├ RewardedAdPreloader        │
│    (AdMob + Meta + Unity)      │   │  ├ AppOpenAdPreloader         │
│  MyLibrary (Application base)  │   │  └ NativeAdCache              │
└───────────────┬────────────────┘   └────────────┬─────────────────┘
                │                                  │ units from RC
┌───────────────▼──────────────────────────────────▼──────────────┐
│  CONFIG LAYER  (config/remote)                                    │
│  RemoteConfigManager → RemoteConfigParser → AdsRemoteConfig       │
│  AdsConfigRepository — per-screen AdUnitConfig builders           │
│  FrequencyCapManager — count-based interstitial cap               │
└────────────────────────────┬──────────────────────────────────────┘
                             │ produces AdUnitConfig
┌────────────────────────────▼────────────────────────────────────┐
│  MANAGER LAYER  (per-format, one per placement)                  │
│  InterstitialAdManager / RewardedAdManager                       │
│  BannerAdManager / NativeAdManager                               │
│  All extend BaseAdManager — waterfall + state logic lives here   │
└────────────────────────────┬────────────────────────────────────┘
                             │ creates & drives
┌────────────────────────────▼────────────────────────────────────┐
│  LOADER LAYER  (per-network × per-format)                        │
│  AdMob*  Loader / Meta*  Loader / Unity*  Loader                 │
│  All extend NetworkAdLoader — touches ONE SDK only               │
└────────────────────────────┬────────────────────────────────────┘
                             │ calls
┌────────────────────────────▼────────────────────────────────────┐
│  SDK LAYER  (third-party)                                         │
│  Google AdMob  │  Meta Audience Network  │  Unity Ads            │
└─────────────────────────────────────────────────────────────────┘
```

There are **two parallel ways** to serve ads, both fed by Remote Config:

- **Per-screen managers** (used directly by the app, e.g. in `MainActivity`):
  `BannerAdManager` / `NativeAdManager` / `InterstitialAdManager` /
  `RewardedAdManager`, configured by `AdsConfigRepository.<format>Config(screen)`.
- **SmartAdManager** (optional, app-wide background preloading): keeps full-screen
  ads warm and shows App Open on foreground. Enabled only if the app overrides
  `MyLibrary.provideAdsConfig()`.

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
APP_OPEN      →  Shown on app foreground (via SmartAdManager / AppOpenAdPreloader)
SPLASH        →  (defined, not wired)
```

### `AdLoadState` — Lifecycle state machine
```
IDLE ──load()──► LOADING ──success──► LOADED ──show()──► SHOWING
  ▲                  │                                       │
  │              onFailed                            onDismissed /
  │                  │                               onFailedToShow
  │             (next rung)                                  │
  │              or FAILED          onFullScreenClosed() ────┘
  └───────────── destroy() / onFullScreenClosed() ───────────┘
```
`BaseAdManager.onFullScreenClosed()` returns the manager to `IDLE` after a
full-screen ad is dismissed or fails to show, so it can be reloaded.

### `AdUnitConfig` — Placement configuration (built by AdsConfigRepository or the app)
```kotlin
AdUnitConfig(
    placementKey = "home_banner",       // unique placement name (for logging)
    format       = AdFormat.BANNER,     // which ad type
    bannerSize   = BannerAdSize.ADAPTIVE,  // only for BANNER
    waterfall    = listOf(              // ordered fallback list (provider priority)
        NetworkAdUnit(AdNetwork.ADMOB,  "admob_unit_id",  enabled = true),
        NetworkAdUnit(AdNetwork.META,   "meta_placement", enabled = true),
        NetworkAdUnit(AdNetwork.UNITY,  "unity_placement",enabled = true),
    )
)
// AdUnitConfig.activeUnits = waterfall.filter { it.enabled && it.adUnitId.isNotBlank() }
```

### `SmartAdConfig` — SmartAdManager tuning (NO ad unit IDs)
```kotlin
SmartAdConfig(
    interstitialScreen = "search",   // RC ads_config screen keys
    rewardedScreen     = "search",
    nativeScreen       = "detail",
    interstitialIntervalSec = 30,    // min seconds between interstitial shows
    nativeCacheSize    = 3,
    nativeExpiryMinutes = 60,
    maxRetries = 6, retryBaseDelayMs = 5000, retryMaxDelayMs = 300000,
)
```
Unit IDs are resolved from Remote Config by these screen keys — never hardcoded.

### `NetworkAdUnit` / `AdError`
```
NetworkAdUnit:  network → which SDK · adUnitId → placement id · enabled → toggle
AdError:        network · code (SDK's own) · message · cause?
```

---

## 3. Boot & Initialization Flow

### Step 1 — Application start (Remote Config bootstrap)

```
App : MyLibrary()  (Application.onCreate)
    │
    ▼
MyLibrary.initRemoteConfig()
    │
    ▼
AdsSdk.remoteConfig.init(defaults = provideRemoteConfigDefaults())   ← Firebase RC
    │  (minFetchInterval 0 in debug, 3600s release)
    ▼
fetchAndActivate() → onReady
    │
    ▼
json = remoteConfig.getString("ads_config")        ← structured per-screen JSON
    │  (falls back to local default when blank / Firebase absent)
    ▼
AdsConfigRepository.load(context, json)
    │  → RemoteConfigParser.parse(json) → AdsRemoteConfig
    │  → notifies onConfigLoaded listener (re-kicks SmartAdManager if running)
    ▼
Per-screen config now available
```

### Step 2 — First Activity: consent → SDK init (`AdsLib.initWithActivity`)

```
MainActivity: AdsLib.initWithActivity(activity, onReady, onBlocked)
    │
    ▼
[already initialized?] → onReady() immediately
    │
    ▼
ConsentManager(activity).gatherConsent(timeoutMs = 8000) { … }   ← Google UMP
    │  requestConsentInfoUpdate → loadAndShowConsentFormIfRequired
    │  (8s timeout safety net guarantees the callback fires exactly once)
    │
    ├── consent.canRequestAds == false → onBlocked()  → STOP (no ads)
    │
    └── consent.canRequestAds == true
            ▼
        AdsSdk.initialize(
            context, debug, hasConsent = canRequestAds,
            unityGameId = AdsConfigRepository.unityGameId()
        ) { onComplete }
            ▼
        MyLibrary.onAdsReady()      ← starts SmartAdManager IF opted in
        onReady()                   ← app loads its per-screen ads
```

### Step 3 — SDK Initialization internals (`AdsSdk.initialize`)

```
AdsSdk.initialize(context, debug, hasConsent, testDeviceIds, unityGameId, onComplete)
    │
    ├── synchronized: atomic check-then-set of initialized / initializing
    │     [already initialized] → onComplete() now
    │     [initializing]        → queue onComplete, return
    │
    ├── Meta:  AdSettings.setDataProcessingOptions( hasConsent ? [] : ["LDU"] )
    │          AudienceNetworkAds.initialize(context)          ← consent FIRST
    │
    ├── Unity: MetaData("gdpr.consent" = hasConsent).commit()  ← consent FIRST
    │          UnityAds.initialize(gameId, testMode = debug)
    │
    ▼
MobileAds.initialize(context) { listener }      ← AdMob (real completion listener)
    │   initialized = true; initializing = false
    │   drain queued callbacks → onComplete()    ← all on main thread
```
> Consent is forwarded to Meta and Unity **before** their init — a Play-policy
> requirement. SDK init never happens inside a loader.

---

## 4. Load: Waterfall Engine (`BaseAdManager`)

### Step 4a — `load()`

```
manager.load(context, callback)
    │
    ├── [state == LOADING] → ignore
    ├── [state == LOADED]  → callback.onLoaded(loadedLoader.network) and return
    │
    ▼
activeUnits = config.activeUnits        // enabled & non-blank, waterfall order
    │
    ├── [empty] → fail() → callback.onFailedToLoad()   ← always fires now
    │
    ▼
loaders = activeUnits.mapNotNull { createLoader(it) }
    ├── [empty] → fail() → callback.onFailedToLoad()
    ▼
state = LOADING ; attempt(context, 0)
```

### Step 4b — `attempt()` (recursive waterfall)

```
attempt(context, index)
    ├── [index >= loaders.size] → fail("Waterfall exhausted") → onFailedToLoad()
    ▼
loader = loaders[index]
loader.load(context,
    onLoaded = { main.post { loadedLoader = loader; state = LOADED
                             callback.onLoaded(loader.network) } }   ← WIN
    onFailed = { main.post { loader.destroy(); attempt(index + 1) } }← NEXT RUNG
)
```

### Step 4c — Loader: single-SDK call (AdMob interstitial example)

```
InterstitialAd.load(context, adUnitId, AdRequest, callback)
    ├── onAdLoaded(ad)        → state = LOADED; wire FullScreenCallbacks; onLoaded()
    └── onAdFailedToLoad(err) → state = FAILED; onFailed(AdError(ADMOB, code, msg))
```

---

## 5. Show & Full-Screen Callbacks

```
manager.show(activity)
    ├── [no ready ad] → return false
    ▼
state = SHOWING ; loadedLoader.show(activity)
    ▼  (AdMob example) ad.show(activity)
    ├── onAdShowedFullScreenContent      → cb.onShown   → dispatch { onShown(net) }
    ├── onAdClicked                      → cb.onClicked → dispatch { onClicked(net) }
    ├── onAdImpression                   → cb.onImpression
    ├── onAdDismissedFullScreenContent   → cb.onDismissed
    │        → onFullScreenClosed() (state→IDLE, loadedLoader=null)
    │        → dispatch { onDismissed(net) }
    └── onAdFailedToShowFullScreenContent→ cb.onFailedToShow
             → onFullScreenClosed() → dispatch { onFailedToShow(err) }
```

### `FullScreenCallbacks` holder (replaces the old per-lambda constructors)

Every full-screen loader (AdMob/Meta/Unity interstitial & rewarded) takes one
`FullScreenCallbacks`. The manager builds it once via
`BaseAdManager.fullScreenCallbacks()`:

```kotlin
protected fun fullScreenCallbacks() = FullScreenCallbacks(
    onShown        = { net -> dispatch { onShown(net) } },
    onDismissed    = { net -> onFullScreenClosed(); dispatch { onDismissed(net) } },
    onClicked      = { net -> dispatch { onClicked(net) } },
    onImpression   = { net -> dispatch { onImpression(net) } },
    onFailedToShow = { err -> onFullScreenClosed(); dispatch { onFailedToShow(err) } },
    onRewardEarned = { type, amount ->
        dispatch { (this as? RewardCallback)?.onRewardEarned(type, amount) }
    },
)
```
So `InterstitialAdManager.createLoader()` is just:
`AdNetwork.ADMOB -> AdMobInterstitialLoader(unit, fullScreenCallbacks())`.

All public callbacks are delivered on the **main thread** via `dispatch {}` /
`Handler(Looper.getMainLooper())`.

---

## 6. Format-Specific Flows

### 6A. Interstitial / 6B. Rewarded (per-screen)
```
InterstitialAdManager(config).load(ctx, AdCallback)      // RewardedAdManager: RewardCallback
    ↓ waterfall (AdMob → Meta → Unity)
isReady() == true
    ↓ user action
manager.show(activity)
    ↓ dismiss → onFullScreenClosed() → onDismissed() → app reloads
Rewarded also: onRewardEarned(type, amount) when the video completes.
```

**Reward values per network:**
| Network | type | amount |
|---------|------|--------|
| AdMob   | from ad unit (e.g. "coins") | from ad unit (e.g. 10) |
| Meta FAN | `"reward"` (fixed) | `1` (FAN has no dynamic reward) |
| Unity   | `"reward"` (fixed) | `1`, only when COMPLETED (SKIPPED → none) |

### 6C. Banner
```
BannerAdManager(config).load(ctx, AdCallback)
    ↓ onLoaded → manager.attach(container)  // removeAllViews + addView(getView())
Lifecycle: onResume→resume(), onPause→pause(), onDestroy→destroy()
```
**BannerAdSize mapping:**
| `BannerAdSize` | AdMob | Meta FAN | Unity |
|---|---|---|---|
| BANNER | 320×50 | BANNER_HEIGHT_50 | 320×50 |
| LARGE_BANNER | 320×100 | BANNER_HEIGHT_90 | 320×100 |
| MEDIUM_RECTANGLE | 300×250 | RECTANGLE_HEIGHT_250 | 300×250 |
| ADAPTIVE | device-width adaptive | BANNER_HEIGHT_50 | 320×50 |

### 6D. Native
```
NativeAdManager(config).load(ctx, AdCallback)     // AdMob or Meta (Unity: unsupported)
    ↓ loader inflates template + populates assets (full-width layout params)
    ↓ onLoaded → manager.attach(container)
Teardown: AdMob nativeAd.destroy(); Meta unregisterView()+destroy()
```

---

## 7. SmartAdManager — App-Wide Preloading (opt-in)

Enabled only when the app overrides `MyLibrary.provideAdsConfig()` (returns
non-null). `MyLibrary.onAdsReady()` then calls `SmartAdManager.init(app, config)`.
All units come from `AdsConfigRepository` (by the screen keys in `SmartAdConfig`);
if RC loads later, `setConfigLoadedListener` re-kicks the preloaders.

```
SmartAdManager.init(application, config)
    ├── InterstitialPreloader.start()   double-buffer; preload next on onShown;
    │                                    proactive ~55m expiry; interval guard
    ├── AppOpenAdPreloader.start()       ProcessLifecycle foreground show;
    │                                    4h freshness; FullScreenAdState guard
    ├── RewardedAdPreloader              on-demand (prepare/show/release per screen)
    ├── NativeAdCache.prefetch()         AdMob-only LRU cache, prune on expiry
    └── ProcessLifecycleOwner observer   re-arms retry backoff on foreground
        + AdsConfigRepository.setConfigLoadedListener { re-kick all }
```

Public API (all null-safe when SmartAdManager was never initialized):
`tryShowInterstitial(activity)` · `isInterstitialReady` ·
`prepareRewarded/showRewarded/releaseRewarded` · `isRewardedReady` ·
`prefetchNative/attachNative/nativeReadyCount`.

`FullScreenAdState` is a process-wide flag so App Open never stacks on top of an
interstitial/rewarded ad (set on `onShown`, cleared on dismiss; 1s re-foreground
guard).

---

## 8. Remote Config Integration

```
ads_config JSON (Firebase RC key)
    │  RemoteConfigParser.parse()  (org.json, fully null-safe → EMPTY on error)
    ▼
AdsRemoteConfig { enabled, providerPriority, providers{ADMOB/META/UNITY → ProviderConfig} }
    │
    ▼  AdsConfigRepository builders (return null = disabled / not configured):
    ├── bannerConfig(screen, size)
    ├── nativeConfig(screen)           admobNativeUnitId(screen) for the cache
    ├── interstitialConfig(screen)     + shouldShowInterstitial(screen) (freq cap)
    ├── rewardedConfig(screen)
    ├── appOpenConfig() / appOpenUnitId()
    └── unityGameId()                  → AdsSdk.initialize
```

Example `ads_config` shape:
```json
{ "ads": {
    "enabled": true,
    "provider_priority": ["meta", "admob", "unity"],
    "providers": {
      "admob": { "enabled": true,
        "interstitial": { "enabled": true, "frequency": 1,
                          "search": { "ad_unit_id": "ca-app-pub-…/…" } },
        "rewarded":     { "enabled": true, "search": { "ad_unit_id": "…" } },
        "banner":       { "home": { "enabled": true, "ad_unit_id": "…" } },
        "native":       { "detail": { "enabled": true, "ad_unit_id": "…" } },
        "app_open":     { "enabled": true, "ad_unit_id": "…" } },
      "meta":  { "enabled": true, "interstitial": { "enabled": true,
                 "search": { "placement_id": "…" } } },
      "unity": { "enabled": false, "game_id": "…",
                 "rewarded": { "enabled": true, "search": { "placement_id": "Rewarded_Android" } } }
} } }
```

- **Kill-switch:** `enabled:false` (global) or a format/screen disabled → the
  matching builder returns null, so nothing loads. SmartAdManager also skips init
  when `ads_enabled` is false.
- **Provider priority** defines waterfall order; per-screen `enabled` + presence
  of a placement decide which rungs are included.
- **Frequency cap:** count-based per screen, persisted in SharedPreferences;
  `frequency <= 1` means no cap.

---

## 9. Callback Threading

```
SDK callback (any thread)
   → NetworkAdLoader onLoaded/onFailed or FullScreenCallbacks member
   → BaseAdManager main.post { … }            (Handler on main Looper)
   → dispatch { callback.onXxx() }
   → App AdCallback / RewardCallback           ← always main thread ✓
```

---

## 10. Package Structure & Responsibilities

```
com.ads.adslib
├── AdsSdk.kt                    SDK init facade (AdMob + Meta + Unity, consent fwd)
├── AdsLib.kt                    Activity entry: consent → init → preloaders
├── MyLibrary.kt                 Application base; RC bootstrap; provideAdsConfig()?
│
├── core/
│   ├── model/  AdEnums.kt (AdNetwork, AdFormat, AdLoadState, BannerAdSize)
│   │           AdModels.kt (AdError, NetworkAdUnit, AdUnitConfig)
│   ├── callback/ AdCallback, RewardCallback, FullScreenCallbacks
│   └── base/   BaseAdManager (waterfall+state), NetworkAdLoader (1 SDK × 1 format)
│
├── admob/  {banner,interstitial,native_ad,rewarded}/  loader + manager
├── meta/   {banner,interstitial,native_ad,rewarded}/  FAN loaders
├── unity/  {banner,interstitial,rewarded}/            Unity loaders (no native)
│
├── smart/  SmartAdManager, SmartAdConfig, InterstitialPreloader,
│           RewardedAdPreloader, AppOpenAdPreloader, NativeAdCache,
│           AdRetryScheduler, FullScreenAdState
│
├── config/ RemoteConfigManager
│   └── remote/ RemoteConfigParser, AdsRemoteConfig, AdsConfigRepository,
│               FrequencyCapManager
├── consent/ ConsentManager (UMP + timeout)
└── util/   AdLog
```

---

## 11. State Ownership Rules

| State | Owner |
|-------|-------|
| `AdLoadState` per loader | `NetworkAdLoader.state` |
| `AdLoadState` per placement | `BaseAdManager.state` (reset to IDLE by `onFullScreenClosed`) |
| `loadedLoader` / `callback` | `BaseAdManager` (private) |
| SDK ad object (`InterstitialAd`, `RewardedAd`, `NativeAd`, …) | `NetworkAdLoader` subclass |
| SDK view object (`AdView`, `NativeAdView`, `BannerView`) | `NetworkAdLoader` subclass |
| `ready/showing/loadingManager`, `loadedAt` | each preloader (smart/) |
| Process-wide full-screen flag | `FullScreenAdState` |
| Parsed RC config | `AdsConfigRepository.config` (@Volatile) |

> Rewards flow through `FullScreenCallbacks.onRewardEarned` → `dispatch` cast to
> `RewardCallback`. `RewardedAdManager` no longer keeps a separate
> `rewardCallback` field.

---

## 12. Lifecycle Checklist

| Ad Type | onResume | onPause | onStart/onStop | onDestroy |
|---------|----------|---------|----------------|-----------|
| Interstitial (per-screen) | — | — | — | `manager.destroy()` |
| Rewarded (per-screen) | — | — | — | `manager.destroy()` |
| Banner | `resume()` | `pause()` | — | `destroy()` |
| Native | — | — | — | `destroy()` (→ `nativeAd.destroy()`) |
| SmartAdManager rewarded | — | — | `prepareRewarded`/`releaseRewarded` | (survives rotation) |
| SmartAdManager (whole) | — | — | re-armed on app foreground | `SmartAdManager.destroy()` only on process shutdown |

> Do NOT call `SmartAdManager.destroy()` from `Activity.onDestroy()` — the
> preloaders survive rotation.

---

## 13. Extending the Library

### Add a network (e.g. AppLovin)
```
1. applovin/interstitial/AppLovinInterstitialLoader : NetworkAdLoader
   → load()/show()/destroy(); report via the FullScreenCallbacks it's given.
2. AdEnums.kt: enum AdNetwork { ADMOB, META, UNITY, APPLOVIN }
3. InterstitialAdManager.createLoader(): AdNetwork.APPLOVIN -> AppLovinInterstitialLoader(unit, fullScreenCallbacks())
4. RemoteConfigParser.toNetwork(): "applovin" -> APPLOVIN
5. Init the SDK (with consent) in AdsSdk.initialize().
```

### Add a format
```
1. admob/<format>/AdMob<Format>Loader : NetworkAdLoader
2. admob/<format>/<Format>AdManager : BaseAdManager (createLoader only)
3. (optional) wire a preloader into SmartAdManager + an AdsConfigRepository builder.
```

---

## 14. Quick Reference — Complete Boot Sequence

```
App.onCreate (MyLibrary)
    └─ RemoteConfigManager.init → fetch → AdsConfigRepository.load(ads_config)
MainActivity
    └─ AdsLib.initWithActivity
         ├─ ConsentManager.gatherConsent (UMP, 8s timeout)
         │     canRequestAds == false → onBlocked (no ads)
         └─ canRequestAds == true
              └─ AdsSdk.initialize (Meta+Unity consent → init; then AdMob)
                   ├─ MyLibrary.onAdsReady → SmartAdManager.init  [if provideAdsConfig != null]
                   └─ onReady (app)
                        ├─ per-screen: BannerAdManager / NativeAdManager
                        │              / InterstitialAdManager / RewardedAdManager
                        │              .load() → waterfall → onLoaded → show()/attach()
                        └─ SmartAdManager.tryShowInterstitial / showRewarded / attachNative
    └─ dismiss → onFullScreenClosed() → onDismissed() → reload
```
