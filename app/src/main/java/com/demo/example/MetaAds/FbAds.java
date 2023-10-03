package com.demo.example.MetaAds;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

public class FbAds {
    public static boolean isAds = true;
    public static String AD_Banner_ID = "1378441163022708_1378480329685458";
    public static String AD_Interstitial_ID = "1378441163022708_1378632936336864";
    public static InterstitialAd interstitialAd;

    static ProgressDialog mDialog;
    public interface onDismiss {
        void OnDismiss();
    }

    public FbAds(Activity activity) {
        AudienceNetworkAds.initialize(activity);
    }

    public void Interstitial_Show(final Activity activity) {
        if (isAds) {
            try {
                Ad_Popup(activity);
                interstitialAd = new InterstitialAd(activity, AD_Interstitial_ID);
                InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
                    @Override
                    public void onInterstitialDisplayed(Ad ad) {
                        mDialog.dismiss();

                    }
                    @Override
                    public void onInterstitialDismissed(Ad ad) {
                        interstitialAd = null;
                        FacebookAds.loadInterstitial(activity);
                    }

                    @Override
                    public void onError(Ad ad, AdError adError) {
                        mDialog.dismiss();

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


    private static final String Counter_Ads = "Counter_Ads";


    public static void setCounter_Ads(Context mContext, int string) {
        mContext.getSharedPreferences(mContext.getPackageName(), 0).edit()
                .putInt(Counter_Ads, string).commit();
    }

    public static int getCounter_Ads(Context mContext) {
        return mContext.getSharedPreferences(mContext.getPackageName(), 0).getInt(Counter_Ads, 1);
    }

    public static void Interstitial_Show_Counter(final Activity activity) {
        if (isAds) {
            int counter_ads = getCounter_Ads(activity);
            Log.e("MTAG", "FbAds.class,counter_ads():" +counter_ads);
            if (counter_ads >= 3) {
                try {
                    setCounter_Ads(activity, 1);
                    Ad_Popup(activity);
                    interstitialAd = new InterstitialAd(activity, AD_Interstitial_ID);
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
                            loadInterstitial(activity);
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
                setCounter_Ads(activity, counter_ads);
            }
        }
    }

    private static void loadInterstitial(Context context) {
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
                    FacebookAds.loadInterstitial(context);
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
    private static void Ad_Popup(Context mContext) {
        mDialog = mDialog.show(mContext, "Please Wait...", "Loading Ads", true);
        mDialog.setCancelable(true);
        mDialog.show();
    }

    public void Banner_Show(final RelativeLayout Ad_Layout, Activity activity) {

        if (isAds) {
            AdView adView = new AdView(activity, AD_Banner_ID, AdSize.BANNER_HEIGHT_50);
            Ad_Layout.addView(adView);

            AdListener adListener = new AdListener() {
                @Override
                public void onError(Ad ad, AdError adError) {
                    adView.destroy();
                    Ad_Layout.setVisibility(View.INVISIBLE);
                    Log.e("Banner", "dddd" + adError.getErrorMessage());
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


}
