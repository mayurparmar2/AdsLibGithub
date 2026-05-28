# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

**AdsLib** — a plug-and-play Android ads library in **Kotlin**. Provides a manual
**waterfall fallback** across AdMob, Meta Audience Network, and Unity Ads for Banner,
Interstitial, Native, Rewarded, App Open, and Splash formats.

**Current status:** Core architecture + AdMob Interstitial fully implemented. Other formats
and the Meta/Unity loaders plug into the same base classes but are not yet wired up.

## Modules

| Module | Package | Purpose |
|--------|---------|---------|
| `mylibrary` | `com.ads.adslib` | The publishable AAR library |
| `app` | `com.demo.adslibsss` | Demo/test app that consumes `:mylibrary` |

The `app` module has no Kotlin source files currently — only layouts and resources for manual testing.

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
├── AdsSdk.kt                      # singleton init entry-point (facade)
├── core/
│   ├── model/                     # AdNetwork, AdFormat, BannerAdSize, AdLoadState (enums)
│   │                              # AdError, NetworkAdUnit, AdUnitConfig (data classes)
│   ├── callback/                  # AdCallback, RewardCallback interfaces
│   └── base/
│       ├── NetworkAdLoader.kt     # abstract — 1 network × 1 format (one waterfall rung)
│       └── BaseAdManager.kt       # abstract — owns ALL waterfall/fallback/threading logic
├── admob/interstitial/
│   ├── AdMobInterstitialLoader.kt # concrete NetworkAdLoader for AdMob interstitial
│   └── InterstitialAdManager.kt  # concrete BaseAdManager — only implements createLoader()
├── consent/ConsentManager.kt      # Google UMP (GDPR) wrapper
├── config/RemoteConfigManager.kt  # Firebase Remote Config wrapper
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
- Meta and Unity loader implementations are not yet written; their `when` branches are commented out in `InterstitialAdManager`.

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

## Known issues / notes

- Meta Audience Network is only valid for **in-app** formats; mobile-web/in-stream were discontinued in 2020.
- Firebase requires `google-services.json` in the consuming app module — not in the library module. Do NOT add `com.google.gms.google-services` plugin to `:mylibrary`.
- The `app` module previously had a Manifest merger conflict (`AD_SERVICES_CONFIG` property) between Unity Ads and AdMob — resolved with `tools:replace="android:resource"` in the app manifest.
