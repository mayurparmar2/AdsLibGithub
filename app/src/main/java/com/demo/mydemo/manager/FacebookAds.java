package com.demo.mydemo.manager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;

import java.util.ArrayList;
import java.util.List;

public class FacebookAds {
    private String TAG = "FacebookAds";

    private  final String Counter_Ads = "Counter_Ads";
    public static boolean isAds = true;
//    public static String AD_Banner_ID = "1378441163022708_1378480329685458";
//    public static String AD_Banner_ID = "1378441163022708_1388693431997481";
    public static String AD_Banner_ID = "IMG_16_9_APP_INSTALL";
    public static String AD_Interstitial_ID = "1378441163022708_1378632936336864";
    public static InterstitialAd interstitialAd;

    static ProgressDialog mDialog;

    public interface onDismiss {
        void OnDismiss();
    }

    public FacebookAds(Context activity) {
        AudienceNetworkAds.initialize(activity);
    }
    public void Interstitial_Show(final Context context) {
        if (isAds) {
            try {
                Ad_Popup(context);
                interstitialAd = new InterstitialAd(context, AD_Interstitial_ID);
                InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
                    @Override
                    public void onInterstitialDisplayed(Ad ad) {
                        mDialog.dismiss();

                    }
                    @Override
                    public void onInterstitialDismissed(Ad ad) {
                        interstitialAd = null;
                        loadInterstitial(context);
                    }

                    @Override
                    public void onError(Ad ad, AdError adError) {
                        mDialog.dismiss();
                        Log.e("MTAG", "FbAds.class,onError():" +adError.getErrorMessage());

                    }
                    @Override
                    public void onAdLoaded(Ad ad) {
                        interstitialAd.show();
                    }
                    @Override
                    public void onAdClicked(Ad ad) {
                    }
                    @Override
                    public void onLoggingImpression(Ad ad) {
                    }
                };
                interstitialAd.loadAd(interstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());
            }catch (Exception e){
                e.printStackTrace();
                if (mDialog!=null) {
                    mDialog.dismiss();
                }
                Log.e("MTAG", "FbAds.class,Interstitial_Show():" +e.getMessage());
            }
        }
    }
    public  void setCounter_Ads(Context mContext, int string) {
        mContext.getSharedPreferences(mContext.getPackageName(), 0).edit()
                .putInt(Counter_Ads, string).commit();
    }

    public int getCounter_Ads(Context mContext) {
        return mContext.getSharedPreferences(mContext.getPackageName(), 0).getInt(Counter_Ads, 1);
    }

    public void Interstitial_Show_Counter(final Context context) {
        if (isAds) {
            int counter_ads = getCounter_Ads(context);
            Log.e("MTAG", "FbAds.class,counter_ads():" +counter_ads);
            if (counter_ads >= 3) {
                try {
                    setCounter_Ads(context, 1);
                    Ad_Popup(context);
                    interstitialAd = new InterstitialAd(context, AD_Interstitial_ID);
                    InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
                        @Override
                        public void onInterstitialDisplayed(Ad ad) {
                            mDialog.dismiss();
                            // Interstitial ad displayed callback
                        }

                        @Override
                        public void onInterstitialDismissed(Ad ad) {
                            // Interstitial dismissed callback
                            interstitialAd = null;
                            loadInterstitial(context);
                        }

                        @Override
                        public void onError(Ad ad, AdError adError) {
                            mDialog.dismiss();
                            Log.e("MTAG", "FbAds.class,onError():" +adError.getErrorMessage());

                            // Ad error callback
                            //

                        }

                        @Override
                        public void onAdLoaded(Ad ad) {
                            interstitialAd.show();
                            // Interstitial ad is loaded and ready to be displayed
                        }

                        @Override
                        public void onAdClicked(Ad ad) {
                            // Ad clicked callback
                        }

                        @Override
                        public void onLoggingImpression(Ad ad) {
                            // Ad impression logged callback
                        }
                    };

                    // For auto play video ads, it's recommended to load the ad
                    // at least 30 seconds before it is shown
                    interstitialAd.loadAd(interstitialAd.buildLoadAdConfig()
                            .withAdListener(interstitialAdListener)
                            .build());
                }catch (Exception e){
                    if (mDialog!=null) {
                        mDialog.dismiss();
                    }
                    e.printStackTrace();
                    Log.e("MTAG", "FbAds.class,Interstitial_Show_Counter():" +e);
                }
            }else {
                counter_ads = counter_ads + 1;
                setCounter_Ads(context, counter_ads);
            }
        }
    }

    private void loadInterstitial(Context context) {
        if (isAds) {
            // Initialize the Audience Network SDK
            AudienceNetworkAds.initialize(context);
            interstitialAd = new InterstitialAd(context, AD_Interstitial_ID);
            InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
                @Override
                public void onInterstitialDisplayed(Ad ad) {
                    // Interstitial ad displayed callback

                }
                @Override
                public void onInterstitialDismissed(Ad ad) {
                    // Interstitial dismissed callback
                    interstitialAd = null;
                    loadInterstitial(context);
                }

                @Override
                public void onError(Ad ad, AdError adError) {
                }

                @Override
                public void onAdLoaded(Ad ad) {

                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Ad clicked callback
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Ad impression logged callback
                }
            };

            // For auto play video ads, it's recommended to load the ad
            // at least 30 seconds before it is shown
            interstitialAd.loadAd(interstitialAd.buildLoadAdConfig()
                    .withAdListener(interstitialAdListener)
                    .build());
        }
    }
    private void Ad_Popup(Context mContext) {
        mDialog = mDialog.show(mContext, "Please Wait...", "Loading Ads", true);
        mDialog.setCancelable(true);
        mDialog.show();
    }

    public void Banner_Show(final RelativeLayout Ad_Layout, Context context) {

        if (isAds) {
            AdView adView = new AdView(context, AD_Banner_ID, AdSize.BANNER_HEIGHT_50);
            Ad_Layout.addView(adView);

            AdListener adListener = new AdListener() {
                @Override
                public void onError(Ad ad, AdError adError) {
                    adView.destroy();
                    Ad_Layout.setVisibility(View.INVISIBLE);
                    Log.e(TAG, "Banner Error: " + adError.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    Ad_Layout.setVisibility(View.VISIBLE);
                    Log.e("Banner", "onAdLoaded");
                }

                @Override
                public void onAdClicked(Ad ad) {
//                    Ad_Layout.setVisibility(View.INVISIBLE);
                    Log.e("Banner", "clicked");
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    Log.e("Banner", "onLoggingImpression");
                    // Ad impression logged callback
                }
            };

            // Request an ad
            adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build());
            // load ad
            adView.loadAd();
        }
    }


    NativeAd nativeAd;
    private void loadNativeAd(Context context) {
        nativeAd = new NativeAd(context, "1378441163022708_1379129396287218");

        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Race condition, load() called again before last ad was displayed
                if (nativeAd == null || nativeAd != ad) {
                    return;
                }
//                inflateAd(nativeAd);
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };

        // Request an ad
        nativeAd.loadAd(
                nativeAd.buildLoadAdConfig()
                        .withAdListener(nativeAdListener)
                        .build());
    }

}
