package com.demo.mydemo.manager;

import android.app.Activity;
import android.content.Context;
import android.widget.RelativeLayout;


import com.demo.mydemo.App;

import java.util.Map;

public class AdsManager {
    private Activity context;
    private RelativeLayout adContainer;

    AdMobAds adMobAds;
    FacebookAds facebookAds;
    UnityMyAds unityAds;


    public AdsManager(Activity context, RelativeLayout adContainer) {
        this.context = context;
        this.adContainer = adContainer;
    }

    public void initializeAds(Activity activity) {
        if(App.adsData.getEnable()){
            switch (App.adsData.getAdPlatform()) {
                case "AdMobAds":
                    adMobAds=new AdMobAds(activity);
                    break;
                case "facebook":
                    facebookAds = new FacebookAds(activity);
                    break;
                case "unity":
                    unityAds = new UnityMyAds(activity);
                    break;
                default:
                    // Handle unsupported platform
            }
        }
    }
    public void showInterstitialAd(Activity activity) {
        if(App.adsData.getEnable()){
            switch (App.adsData.getAdPlatform()) {
                case "AdMobAds":
                    new AdMobAds(context).Interstitial_Show_Counter(activity);
                    // Handle interstitial ad for AdMob
                    break;
                case "unity":
                    unityAds.Interstitial_Show_Counter(activity);
//                new UnityAds().showInterstitialAd();
                    break;
                // Add cases for other platforms if needed
            }
        }
    }
    public void loadBannerAd() {
        if(App.adsData.getEnable()){
            switch (App.adsData.getAdPlatform()) {
                case "AdMobAds":
                    adMobAds.Banner_Show(adContainer, context);
                    break;
                case "facebook":
                    facebookAds.Banner_Show(adContainer,context);
                    break;
                case "unity":
                    unityAds.Banner_Show(adContainer,context);
                    break;
                // Add cases for other platforms if needed
            }
        }
    }

    public void loadNativeAd() {
        if(App.adsData.getEnable()){
            switch (App.adsData.getAdPlatform()) {
                case "AdMobAds":
                    // Handle native ad for AdMob
                    break;
                case "facebook":
//                new FacebookAds().loadNativeAd();
                    break;
                case "unity":
//                    unityAds.(adContainer,context);
                    break;
                // Add cases for other platforms if needed
            }
        }
    }

    private static final String Counter_Ads = "Counter_Ads";
    public static void setCounter_Ads(Context mContext, int string) {
        mContext.getSharedPreferences(mContext.getPackageName(), 0).edit()
                .putInt(Counter_Ads, string).commit();
    }
    public static int getCounter_Ads(Context mContext) {
        return mContext.getSharedPreferences(mContext.getPackageName(), 0)
                .getInt(Counter_Ads, 1);
    }
}
