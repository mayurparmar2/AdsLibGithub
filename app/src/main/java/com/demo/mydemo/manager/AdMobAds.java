package com.demo.mydemo.manager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;

import com.demo.mydemo.App;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
public class AdMobAds {
    public  static String AD_Banner_ID = "/6499/example/banner";
    public  static String AD_Interstitial_ID = "/6499/example/interstitial";
    static ProgressDialog progressDialog;
    public AdMobAds(Context context) {
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
    }
    public void Interstitial_Show(final Activity activity) {
        Ad_Popup(activity);

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(activity, AD_Interstitial_ID, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {

                        interstitialAd.show(activity);
                        progressDialog.dismiss();

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                        progressDialog.dismiss();
                    }
                });
    }



    public static void Interstitial_Show_Counter(final Activity activity) {
        int counter_ads = AdsManager.getCounter_Ads(activity);
        if (counter_ads >= App.adsData.getCounter()) {
            AdsManager.setCounter_Ads(activity, 1);
            try {
                Ad_Popup(activity);
                AdRequest adRequest = new AdRequest.Builder().build();
                InterstitialAd.load(activity, AD_Interstitial_ID, adRequest,
                        new InterstitialAdLoadCallback() {
                            @Override
                            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                interstitialAd.show(activity);
                                progressDialog.dismiss();
                            }
                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                                progressDialog.dismiss();
                            }
                        });

            } catch (Exception e) {
            }
        } else {
            counter_ads = counter_ads + 1;
            AdsManager.setCounter_Ads(activity, counter_ads);
        }
    }
    private static void Ad_Popup(Context mContext) {
        progressDialog = progressDialog.show(mContext, "Please Wait...", "Loading Ads", true);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }
    public void Banner_Show(final RelativeLayout Ad_Layout, Context context) {
        AdView mAdView = new AdView(context);
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(AD_Banner_ID);
        AdRequest adore = new AdRequest.Builder().build();
        mAdView.loadAd(adore);
        Ad_Layout.addView(mAdView);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Ad_Layout.setVisibility(View.VISIBLE);
                super.onAdLoaded();
                Log.e("ddddd", "dddd");
            }
            @Override
            public void onAdOpened() {
                super.onAdOpened();
                Ad_Layout.setVisibility(View.INVISIBLE);
                Log.e("ddddd1", "dddd");

            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                mAdView.destroy();
                Ad_Layout.setVisibility(View.INVISIBLE);
                Log.e("ddddd2", "dddd" + loadAdError.getMessage());

            }
        });
    }
}
