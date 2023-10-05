package com.demo.example;

import android.app.Activity;
import android.content.Context;
import android.media.tv.AdRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.ads.AdListener;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;




public class UnityDemo extends AppCompatActivity {

    String AD_Banner_ID = "test";
    String AD_Interstitial_ID = "Interstitial1";
    String AD_Rewarded_ID = "Rewarded_Android";
    Button showInterstitial;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unity_activity);
//        FbAds fbAds = new FbAds(this);
//        fbAds.Banner_Show((RelativeLayout) findViewById(R.id.banner), this);
         showInterstitial = findViewById(R.id.showInterstitial);
        showInterstitial.setOnClickListener(v -> {
            ShowRewardedAd(this);
            showInterstitial.setEnabled(false);
        });
//        Interstitial_Show(this);
        Banner_Show((RelativeLayout) findViewById(R.id.banner), this);
//        Interstitial_Show_Counter(this);


    }




    public void ShowRewardedAd(Activity activity){
         IUnityAdsShowListener showListener = new IUnityAdsShowListener() {
            @Override
            public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
                Log.e("Rewarded", "Unity Ads failed to show ad for " + placementId + " with error: [" + error + "] " + message);
                // Re-enable the button if the user should be allowed to watch another rewarded ad
                showInterstitial.setEnabled(true);
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
                showInterstitial.setEnabled(true);
            }
        };

        UnityAds.load(AD_Rewarded_ID, new IUnityAdsLoadListener() {
            @Override
            public void onUnityAdsAdLoaded(String placementId) {
                Log.e("Rewarded", "onUnityAdsAdLoaded" +placementId);
                UnityAds.show(activity, AD_Rewarded_ID, new UnityAdsShowOptions(), showListener);
            }

            @Override
            public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                Log.e("Rewarded", "Unity Ads failed to load ad for " + placementId + " with error: [" + error + "] " + message);
            }
        });
    }
    public void Interstitial_Show(Activity activity) {
         IUnityAdsShowListener showListener = new IUnityAdsShowListener() {
            @Override
            public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
                Log.e("Interstitial", "Unity Ads failed to show ad for " + placementId + " with error: [" + error + "] " + message);
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

            UnityAds.load(AD_Interstitial_ID, new IUnityAdsLoadListener() {
                @Override
                public void onUnityAdsAdLoaded(String placementId) {
                    Log.e("MTAG", "onUnityAdsAdLoaded" +placementId);
                    UnityAds.show(activity, AD_Interstitial_ID, new UnityAdsShowOptions(), showListener);
                }

                @Override
                public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                    Log.e("Interstitial", "Unity Ads failed to load ad for " + placementId + " with error: [" + error + "] " + message);
                }
            });
    }
    public void Banner_Show(final RelativeLayout Ad_Layout, Activity activity) {
        BannerView mAdView = new BannerView(activity, AD_Banner_ID, new UnityBannerSize(320, 50));
        // Set the listener for banner lifecycle events:
        mAdView.load();
        Ad_Layout.addView(mAdView);
        mAdView.setListener(new BannerView.IListener() {
            @Override
            public void onBannerLoaded(BannerView bannerAdView) {
                // Called when the banner is loaded.
                Log.e("UnityAdsExample", "onBannerLoaded: " + bannerAdView.getPlacementId());
                // Enable the correct button to hide the ad
            }

            @Override
            public void onBannerFailedToLoad(BannerView bannerAdView, BannerErrorInfo errorInfo) {
                Log.e("UnityAdsExample", "Unity Ads failed to load banner for " + bannerAdView.getPlacementId() + " with error: [" + errorInfo.errorCode + "] " + errorInfo.errorMessage);
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

}
