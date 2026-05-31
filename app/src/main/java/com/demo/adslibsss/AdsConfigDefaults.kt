package com.demo.adslibsss

/**
 * Default `ads_config` JSON used as an offline fallback when Firebase Remote
 * Config is unavailable. In production this same JSON lives in the Firebase
 * console under the `ads_config` key and overrides this default.
 *
 * Uses AdMob official test IDs so every screen fills during the demo.
 * Meta/Unity entries are included to demonstrate the waterfall ordering.
 */
object AdsConfigDefaults {

    const val ADS_CONFIG = """
    {
      "ads": {
        "enabled": true,
        "provider_priority": ["admob", "meta", "unity"],
        "providers": {
          "admob": {
            "enabled": true,
            "app_open": {
              "enabled": true,
              "ad_unit_id": "ca-app-pub-3940256099942544/9257395921"
            },
            "banner": {
              "home":   { "enabled": true,  "ad_unit_id": "ca-app-pub-3940256099942544/6300978111" },
              "search": { "enabled": true,  "ad_unit_id": "ca-app-pub-3940256099942544/6300978111" },
              "detail": { "enabled": true,  "ad_unit_id": "ca-app-pub-3940256099942544/6300978111" },
              "history":{ "enabled": false, "ad_unit_id": "ca-app-pub-3940256099942544/6300978111" }
            },
            "interstitial": {
              "enabled": true,
              "frequency": 1,
              "search": { "ad_unit_id": "ca-app-pub-3940256099942544/1033173712" },
              "detail": { "ad_unit_id": "ca-app-pub-3940256099942544/1033173712" }
            },
            "rewarded": {
              "enabled": true,
              "search": { "ad_unit_id": "ca-app-pub-3940256099942544/5224354917" }
            },
            "native": {
              "home":   { "enabled": false, "ad_unit_id": "ca-app-pub-3940256099942544/2247696110" },
              "detail": { "enabled": true,  "ad_unit_id": "ca-app-pub-3940256099942544/2247696110" }
            }
          },
          "meta": {
            "enabled": true,
            "banner": {
              "home": { "enabled": true, "placement_id": "META_BANNER_HOME" }
            },
            "interstitial": {
              "enabled": true,
              "frequency": 3,
              "search": { "placement_id": "META_INTERSTITIAL_SEARCH" }
            },
            "native": {
              "detail": { "enabled": true, "placement_id": "META_NATIVE_DETAIL" }
            }
          },
          "unity": {
            "enabled": false,
            "game_id": "1234567",
            "banner": {
              "home": { "enabled": true, "placement_id": "Banner_Android" }
            }
          }
        }
      }
    }
    """
}
