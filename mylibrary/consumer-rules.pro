# ============================================================================
# AdsLib consumer ProGuard / R8 rules.
# These are applied automatically in any app that depends on this AAR.
#
# The library itself is reflection-free Kotlin, so we only keep the public
# API surface that consumers reference by name. The AdMob / Meta / Unity SDKs
# ship their own consumer rules inside their AARs, so they are NOT repeated here.
# ============================================================================

# --- Public entry points (referenced from app code / AndroidManifest) -------
-keep public class com.ads.adslib.AdsSdk { public *; }
-keep public class com.ads.adslib.AdsLib { public *; }
-keep public class com.ads.adslib.MyLibrary { public *; }
-keep public class com.ads.adslib.smart.SmartAdManager { public *; }
-keep public class com.ads.adslib.smart.SmartAdConfig { public *; }

# --- Public managers consumers instantiate directly --------------------------
-keep public class com.ads.adslib.admob.banner.BannerAdManager { public *; }
-keep public class com.ads.adslib.admob.interstitial.InterstitialAdManager { public *; }
-keep public class com.ads.adslib.admob.native_ad.NativeAdManager { public *; }
-keep public class com.ads.adslib.admob.rewarded.RewardedAdManager { public *; }
-keep public class com.ads.adslib.consent.ConsentManager { public *; }
-keep public class com.ads.adslib.config.** { public *; }

# --- Public model + callback API (used in app callbacks / when-branches) -----
-keep public class com.ads.adslib.core.model.** { *; }
-keep public interface com.ads.adslib.core.callback.** { *; }

# Keep the no-arg default methods on the AdCallback interface so apps that
# override only some callbacks don't lose the defaults under R8.
-keepclassmembers interface com.ads.adslib.core.callback.AdCallback { *; }
-keepclassmembers interface com.ads.adslib.core.callback.RewardCallback { *; }
