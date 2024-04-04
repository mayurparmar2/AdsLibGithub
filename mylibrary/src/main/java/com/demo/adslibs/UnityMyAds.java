package com.demo.adslibs;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.demo.adslibs.DataModel.Unity;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

public class UnityMyAds implements IUnityAdsInitializationListener {
//    String AD_Banner_ID = "bannerHome";
    Unity unity;

    private Boolean testMode = false;
    public UnityMyAds(Context context) {
        unity = Utils.getAdsData().getAds().getAds_unit_id().getUnity();
        UnityAds.initialize(context, unity.getGame_id(), testMode, this);
    }
    @Override
    public void onInitializationComplete() {
        Log.e("MTAG", "onInitializationComplete 1");
    }

    @Override
    public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
//        Log.e("MTAG", "onInitializationComplete 1");
        Log.e("MTAG", "onInitializationFailed" );
    }
    public void Banner_Show(final RelativeLayout Ad_Layout, Activity activity) {
        BannerView mAdView = new BannerView(activity, unity.getBanner_id(), new UnityBannerSize(320, 50));
        // Set the listener for banner lifecycle events:
        mAdView.load();
        Ad_Layout.addView(mAdView);
        mAdView.setListener(new BannerView.IListener() {
            @Override
            public void onBannerLoaded(BannerView bannerAdView) {
                Ad_Layout.setVisibility(View.VISIBLE);
                // Called when the banner is loaded.
                Log.e("UnityAdsExample", "onBannerLoaded: " + bannerAdView.getPlacementId());
                // Enable the correct button to hide the ad
            }

            @Override
            public void onBannerFailedToLoad(BannerView bannerAdView, BannerErrorInfo errorInfo) {
                mAdView.destroy();
                Ad_Layout.setVisibility(View.INVISIBLE);
                Log.e("UnityAdsExample", "UnityAds Ads failed to load banner for " + bannerAdView.getPlacementId() + " with error: [" + errorInfo.errorCode + "] " + errorInfo.errorMessage);
                // Note that the BannerErrorInfo object can indicate a no fill (refer to the API documentation).
            }

            @Override
            public void onBannerClick(BannerView bannerAdView) {
                // Called when a banner is clicked.
                Log.e("UnityAdsExample", "onBannerClick: " + bannerAdView.getPlacementId());
            }

            @Override
            public void onBannerLeftApplication(BannerView bannerAdView) {
                // Called when the banner links out of the application.
                Log.e("UnityAdsExample", "onBannerLeftApplication: " + bannerAdView.getPlacementId());
            }
        });
    }
    public void Interstitial_Show_Counter(Activity activity) {
        int counter_ads = AdsManager.getCounter_Ads(activity);
        if (counter_ads >= Utils.getAdsData().getAds().getCounter()) {
            AdsManager.setCounter_Ads(activity, 1);
            Interstitial_Show(activity);
        } else {
            counter_ads = counter_ads + 1;
            AdsManager.setCounter_Ads(activity, counter_ads);
        }
    }
    public void Interstitial_Show(Activity activity) {
            try {
                IUnityAdsShowListener showListener = new IUnityAdsShowListener() {
                    @Override
                    public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {

                        Log.e("Interstitial", "UnityAds Ads failed to show ad for " + placementId + " with error: [" + error + "] " + message);
                    }

                    @Override
                    public void onUnityAdsShowStart(String placementId) {
                        Log.e("Interstitial", "onUnityAdsShowStart: " + placementId);
                    }

                    @Override
                    public void onUnityAdsShowClick(String placementId) {
                        Log.e("Interstitial", "onUnityAdsShowClick: " + placementId);
                    }

                    @Override
                    public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
                        Log.e("Interstitial", "onUnityAdsShowComplete: " + placementId);
                    }
                };
                UnityAds.load(unity.getInterstitial_id(), new IUnityAdsLoadListener() {
                    @Override
                    public void onUnityAdsAdLoaded(String placementId) {
                        Log.e("MTAG", "onUnityAdsAdLoaded" +placementId);
                        UnityAds.show(activity, unity.getInterstitial_id(), new UnityAdsShowOptions(), showListener);
                    }
                    @Override
                    public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                        Log.e("Interstitial", "UnityAds Ads failed to load ad for " + placementId + " with error: [" + error + "] " + message);
                    }
                });
            }catch (Exception e) {
                Log.e("MTAG", "Interstitial_Show" +e);
            }

    }
    public void ShowRewardedAd(Activity activity){
        IUnityAdsShowListener showListener = new IUnityAdsShowListener() {
            @Override
            public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
                Log.e("Rewarded", "UnityAds Ads failed to show ad for " + placementId + " with error: [" + error + "] " + message);
                // Re-enable the button if the user should be allowed to watch another rewarded ad
//                showInterstitial.setEnabled(true);
            }

            @Override
            public void onUnityAdsShowStart(String placementId) {
                Log.e("Rewarded", "onUnityAdsShowStart: " + placementId);
            }

            @Override
            public void onUnityAdsShowClick(String placementId) {
                Log.e("Rewarded", "onUnityAdsShowClick: " + placementId);
            }

            @Override
            public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
                Log.e("Rewarded", "onUnityAdsShowComplete: " + placementId);
                if (state.equals(UnityAds.UnityAdsShowCompletionState.COMPLETED)) {
                    // Reward the user for watching the ad to completion
                } else {
                    // Do not reward the user for skipping the ad
                }
                // Re-enable the button if the user should be allowed to watch another rewarded ad
//                showInterstitial.setEnabled(true);
            }
        };

        UnityAds.load(unity.getRewarded_id(), new IUnityAdsLoadListener() {
            @Override
            public void onUnityAdsAdLoaded(String placementId) {
                Log.e("Rewarded", "onUnityAdsAdLoaded" +placementId);
                UnityAds.show(activity, unity.getRewarded_id(), new UnityAdsShowOptions(), showListener);
            }

            @Override
            public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                Log.e("Rewarded", "UnityAds Ads failed to load ad for " + placementId + " with error: [" + error + "] " + message);
            }
        });
    }
}