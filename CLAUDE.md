# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

**AdsLib** — a plug-and-play Android ads library in **Kotlin**. Provides a manual
**waterfall fallback** across AdMob, Meta Audience Network, and Unity Ads for Banner,
Interstitial, Native, Rewarded, App Open, and Splash formats.

**Current status:** Fully implemented across all three networks (AdMob, Meta, Unity) and all
formats (Banner, Interstitial, Native, Rewarded, App Open). Includes a `smart/` preloading
layer (double-buffered interstitial/rewarded, app-open on foreground, native LRU cache) and a
Remote-Config-driven `config/remote/` layer (`AdsConfigRepository`) that builds per-screen
waterfalls. SDK init (incl. Meta/Unity consent forwarding) lives in `AdsSdk.initialize()`.

## Modules

| Module | Package | Purpose |
|--------|---------|---------|
| `mylibrary` | `com.ads.adslib` | The publishable AAR library |
| `app` | `com.demo.adslibsss` | Demo/test app that consumes `:mylibrary` |

The `app` module demonstrates the full flow: `App.kt` (extends `MyLibrary`), `MainActivity.kt`
(RC-driven per-screen banner/native/interstitial/rewarded), and `AdsConfigDefaults.kt` (offline
fallback `ads_config` JSON).

## Build commands

```bash
# Build the library AAR
./gradlew :mylibrary:assembleDebug

# Build the demo app
./gradlew :app:assembleDebug

# Full project build
./gradlew assembleDebug

# Publish library locally (maven-publish)
./gradlew :mylibrary:publishToMavenLocal
```

**Java requirement:** AGP 8.3.2 requires Java 17+. The project is configured to use Android
Studio's bundled JDK via `gradle.properties`:
```
org.gradle.java.home=/Applications/Android Studio.app/Contents/jbr/Contents/Home
```
If this path changes (e.g. Android Studio update), update `gradle.properties` accordingly.

## Tech stack

- **Language:** Kotlin only (`jvmTarget = "11"`, `sourceCompatibility = VERSION_11`)
- `compileSdk 36`, `minSdk 21`
- `play-services-ads:23.3.0`, UMP `3.1.0`, Firebase BoM `33.7.0`
- Unity Ads `4.7.0`, Meta Audience Network `6.17.0` (both active in dependencies)
- Published as `com.demo.mydemo:ads-libs:1.2`

## Package layout — `mylibrary`

```
com.ads.adslib
├── AdsSdk.kt                      # SDK init facade (AdMob + Meta + Unity, consent forwarding)
├── AdsLib.kt                      # Activity-level entry: consent → init → preloaders
├── MyLibrary.kt                   # Application base class (Remote Config bootstrap)
├── core/
│   ├── model/                     # AdNetwork, AdFormat, BannerAdSize, AdLoadState (enums)
│   │                              # AdError, NetworkAdUnit, AdUnitConfig (data classes)
│   ├── callback/                  # AdCallback, RewardCallback, FullScreenCallbacks
│   └── base/
│       ├── NetworkAdLoader.kt     # abstract — 1 network × 1 format (one waterfall rung)
│       └── BaseAdManager.kt       # abstract — owns ALL waterfall/fallback/threading logic
├── admob/{banner,interstitial,native_ad,rewarded}/   # AdMob loaders + per-format managers
├── meta/{banner,interstitial,native_ad,rewarded}/    # Meta (FAN) loaders
├── unity/{banner,interstitial,rewarded}/             # Unity loaders (no native)
├── smart/                         # preloading layer:
│   ├── SmartAdManager.kt          #   facade — wires the preloaders, re-arms on foreground
│   ├── SmartAdConfig.kt           #   RC screen keys + cache/retry tuning (NO unit IDs)
│   ├── InterstitialPreloader.kt   #   double-buffered, expiry-aware
│   ├── RewardedAdPreloader.kt     #   on-demand per screen, expiry-aware
│   ├── AppOpenAdPreloader.kt      #   ProcessLifecycle foreground show, RC-gated
│   ├── NativeAdCache.kt           #   LRU cache, prune-on-expiry
│   ├── AdRetryScheduler.kt        #   exponential backoff (re-armed on foreground)
│   └── FullScreenAdState.kt       #   guards App Open stacking on other full-screen ads
├── config/
│   ├── RemoteConfigManager.kt     # Firebase Remote Config wrapper (null-safe)
│   └── remote/                    # JSON → per-screen waterfall:
│       ├── RemoteConfigParser.kt  #   org.json parser (unit-tested)
│       ├── AdsRemoteConfig.kt     #   parsed model
│       ├── AdsConfigRepository.kt #   source of truth: per-screen AdUnitConfig builders
│       └── FrequencyCapManager.kt #   count-based, SharedPreferences-persisted
├── consent/ConsentManager.kt      # Google UMP (GDPR) wrapper, with timeout fallback
└── util/AdLog.kt                  # logging (off by default, gated on debug flag)
```

## Core architectural rule

`BaseAdManager` owns **all** waterfall, fallback, state, and threading logic — written once,
reused for every format. Per-format managers only implement `createLoader(unit: NetworkAdUnit)`.
`NetworkAdLoader` subclasses touch exactly one SDK and never reference other networks.

**To add a new format** (e.g. Rewarded):
1. Create `AdMobRewardedLoader : NetworkAdLoader`
2. Create `RewardedAdManager : BaseAdManager` implementing `createLoader`
3. Use `RewardCallback` for reward-specific callbacks

**To add a network to an existing format** (e.g. Meta for interstitial):
1. Create `MetaInterstitialLoader : NetworkAdLoader`
2. Add `AdNetwork.META -> MetaInterstitialLoader(...)` in `InterstitialAdManager.createLoader`
3. Uncomment the Meta dependency in `mylibrary/build.gradle.kts`

## Conventions

- A `NetworkAdLoader` MUST call exactly one of `onLoaded` / `onFailed`, on the main thread.
- All public `AdCallback` callbacks fire on the main thread — use `BaseAdManager.dispatch {}` or the `main` Handler.
- Always null-out SDK references in `destroy()` to prevent leaks.
- Use `AdLog` for all logging (never `Log.d` directly). Logging is off in release unless `AdsSdk.initialize(debug = true)`.
- Never leak network-specific types into public APIs — use `AdError`, `AdNetwork`, `AdUnitConfig`.
- Never call SDK init from individual loaders — initialization belongs in `AdsSdk.initialize()`.
- Never request ads before `ConsentManager.canRequestAds` is `true`.
- Never hardcode ad unit IDs in library source — they come from `AdUnitConfig`.
- Full-screen loaders (AdMob/Meta/Unity interstitial & rewarded) take a single `FullScreenCallbacks` holder, wired once via `BaseAdManager.fullScreenCallbacks()` — do not reintroduce per-callback constructor params.
- `BaseAdManager.onFullScreenClosed()` resets state to IDLE on dismiss/show-fail so a manager can be reloaded.
- SDK init order in `AdsSdk.initialize()`: forward consent to Meta (`AdSettings.setDataProcessingOptions`) and Unity (`MetaData("gdpr.consent")`) BEFORE initializing them; never init a network SDK from a loader.
- `SmartAdManager` (app-wide preloading) is **opt-in**: `MyLibrary.provideAdsConfig()` returns `SmartAdConfig?` and defaults to null. Returning null skips `SmartAdManager.init` entirely; the app then uses only the RC per-screen managers. All `SmartAdManager` public methods are null-safe when it was never initialized.
- `SmartAdConfig` holds **no ad unit IDs** — every preloader resolves its units from `AdsConfigRepository` (Remote Config) by the screen key named in the config (`interstitialScreen`/`rewardedScreen`/`nativeScreen`); App Open uses `appOpenUnitId()`. RC may load after `SmartAdManager.init`, so preloaders are re-kicked via `AdsConfigRepository.setConfigLoadedListener`. `NativeAdCache` is AdMob-only (`admobNativeUnitId`).

## Usage flow (reference for demo app / docs)

```kotlin
// 1. Consent (splash screen)
val consent = ConsentManager(this)
consent.gatherConsent(this) {
    if (consent.canRequestAds) {
        // 2. Initialize
        AdsSdk.initialize(this, debug = BuildConfig.DEBUG) {
            // 3. Configure placement
            val config = AdUnitConfig(
                placementKey = "level_complete",
                format = AdFormat.INTERSTITIAL,
                waterfall = listOf(
                    NetworkAdUnit(AdNetwork.ADMOB, "ca-app-pub-xxx/yyy")
                )
            )
            // 4. Load
            val mgr = InterstitialAdManager(config)
            mgr.load(context, object : AdCallback {
                override fun onLoaded(network: AdNetwork) { /* ready */ }
                override fun onDismissed(network: AdNetwork) { /* resume */ }
            })
            // 5. Show
            if (mgr.isReady()) mgr.show(activity)
            // 6. Teardown
            mgr.destroy() // in onDestroy
        }
    }
}
```

## Play Store compliance — AdMob / Meta / Unity

Violating any rule below can cause **app suspension or removal** from Google Play.

### Consent (GDPR / CCPA) — mandatory for all 3 networks

- Always call `ConsentManager.gatherConsent()` **before** `AdsSdk.initialize()`. Never skip or short-circuit the consent flow.
- Only call `AdsSdk.initialize()` and load ads when `ConsentManager.canRequestAds == true`.
- The app **must expose a "Privacy Options / Manage Consent" entry point** in its settings screen so users can update consent at any time. Wire it to `ConsentManager.reset()` followed by `gatherConsent()`.
- For CCPA (California): Meta requires `AudienceNetworkAds.setDataProcessingOptions(arrayOf())` (no restriction) or `arrayOf("LDU")` (limit data use) based on user opt-out. Pass this **before** `AudienceNetworkAds.initialize()`.
- Unity: call `UnityAds.setPrivacyConsent(true/false)` after the user's consent decision before any Unity ad is loaded.

### Google AdMob — policy rules

- **Never click ads programmatically** and never incentivise users to click ads. This is permanent ban territory.
- **Never show ads during natural app loading** (splash screen, level loading). Show interstitials only on deliberate user transitions (e.g. after completing a level, before navigating to a new screen).
- **Test ads in debug only.** `testDeviceIds` must never be populated in release builds. Use `BuildConfig.DEBUG` to gate them.
- Declare `com.google.android.gms.permission.AD_ID` in the app's `AndroidManifest.xml` (already present). Required on Android 13+ for ad personalisation.
- The consuming app **must include AdMob App ID** as a `<meta-data>` entry in its manifest: `com.google.android.gms.ads.APPLICATION_ID`. Never put a test App ID in a production build.
- If the app targets children (Families Policy): set `RequestConfiguration.Builder().setTagForChildDirectedTreatment(TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)` in `AdsSdk.initialize()` and use only certified family-safe ad units.

### Meta Audience Network — policy rules

- Meta ads **require the app to have a Facebook App ID** registered in the Meta developer portal and linked in `strings.xml` (`facebook_app_id`). The app ID in the portal must match the production signing certificate's hash.
- Never show Meta ads to users under 13. If the app targets children, do not integrate FAN at all.
- FAN is only valid for **in-app** formats (banner, interstitial, native, rewarded). Do not attempt in-stream/web formats.
- Call `AudienceNetworkAds.initialize(context)` only inside `AdsSdk.initialize()`, gated on consent. Never call it from a `NetworkAdLoader`.
- After receiving GDPR consent, forward it to FAN: `AdSettings.setDataProcessingOptions(arrayOf())` (non-EEA / consent given) or keep LDU for opted-out users.

### Unity Ads — policy rules

- Unity requires `com.unity3d.ads.metadata.MediationMetaData` GDPR consent to be set before any ad is loaded. Set `MetaData(context).apply { set("gdpr.consent", true/false); commit() }`.
- Unity's `AD_SERVICES_CONFIG` property conflicts with AdMob's in the manifest. The fix (`tools:replace="android:resource"`) is already in the app manifest — **do not remove it**.
- Never initialise Unity Ads more than once per process. Guard with `UnityAds.isInitialized()`.

### Google Play Store — Data Safety section

When the app is published, the **Data Safety form in Play Console must accurately declare** all data collected by the ad SDKs. Minimum required disclosures:

| SDK | Data type | Purpose |
|-----|-----------|---------|
| AdMob | Device/app identifiers, location (approximate), diagnostics | Advertising, Analytics |
| Meta FAN | Device identifiers, usage data, location (approximate) | Advertising |
| Unity Ads | Device identifiers, usage data, crash data | Advertising, Analytics |

- The app **must link to a privacy policy** from both the Play Store listing and from within the app (e.g. Settings screen). The privacy policy must mention all three ad SDKs by name.
- Do not use `AD_ID` permission without declaring its purpose in the Data Safety form.

### What will get the app suspended — never do these

- Showing ads before consent is gathered.
- Clicking, refreshing, or interacting with ads in code.
- Displaying ads to users in regions where consent was denied.
- Using production ad unit IDs in debug/test builds (causes invalid traffic flags).
- Using test ad unit IDs in production builds (zero revenue, policy violation).
- Showing AdMob ads in apps that also contain sexual, violent, or dangerous content.
- Showing Meta or Unity ads without completing their respective developer portal setup (App ID, placement ID registration).

## Known issues / notes

- Meta Audience Network is only valid for **in-app** formats; mobile-web/in-stream were discontinued in 2020.
- Firebase requires `google-services.json` in the consuming app module — not in the library module. Do NOT add `com.google.gms.google-services` plugin to `:mylibrary`.
- The `app` module previously had a Manifest merger conflict (`AD_SERVICES_CONFIG` property) between Unity Ads and AdMob — resolved with `tools:replace="android:resource"` in the app manifest.
