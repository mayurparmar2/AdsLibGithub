package com.demo.mydemo.manager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.demo.mydemo.R;
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
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.util.ArrayList;
import java.util.List;

public class FacebookAds {
    private String TAG = "FacebookAds";
    private  final String Counter_Ads = "Counter_Ads";
    public static String AD_Banner_ID = "IMG_16_9_LINK";
    public static String AD_Interstitial_ID = "VID_HD_9_16_39S_LINK";
    public static InterstitialAd interstitialAd;
    static ProgressDialog mDialog;
    public FacebookAds(Context activity) {
        AudienceNetworkAds.initialize(activity);
    }
    public void Interstitial_Show(final Context context) {
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

    public void Interstitial_Show_Counter(final Context context) {
            int counter_ads = AdsManager.getCounter_Ads(context);
            Log.e("MTAG", "FbAds.class,counter_ads():" +counter_ads);
            if (counter_ads >= 3) {
                try {
                    AdsManager.setCounter_Ads(context, 1);
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
                    if (mDialog!=null) {
                        mDialog.dismiss();
                    }
                    e.printStackTrace();
                    Log.e("MTAG", "FbAds.class,Interstitial_Show_Counter():" +e);
                }
            }else {
                counter_ads = counter_ads + 1;
                AdsManager.setCounter_Ads(context, counter_ads);
            }
    }

    private void loadInterstitial(Context context) {
            AudienceNetworkAds.initialize(context);
            interstitialAd = new InterstitialAd(context, AD_Interstitial_ID);
            InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
                @Override
                public void onInterstitialDisplayed(Ad ad) {

                }
                @Override
                public void onInterstitialDismissed(Ad ad) {
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
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Ad impression logged callback
                }
            };
            interstitialAd.loadAd(interstitialAd.buildLoadAdConfig()
                    .withAdListener(interstitialAdListener)
                    .build());
    }
    private void Ad_Popup(Context mContext) {
        mDialog = mDialog.show(mContext, "Please Wait...", "Loading Ads", true);
        mDialog.setCancelable(true);
        mDialog.show();
    }

    public void Banner_Show(final RelativeLayout Ad_Layout, Context context) {
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




    private NativeAdLayout nativeAdLayout;
    private LinearLayout adView;
    private NativeAd nativeAd;
    public void loadNativeAd(Context context, ViewGroup adContainer) {
        NativeAd nativeAd = new NativeAd(context, "YOUR_NATIVE_AD_PLACEMENT_ID");
        nativeAd.loadAd(nativeAd.buildLoadAdConfig()
                        .withAdListener(new NativeAdListener() {
                            @Override
                            public void onMediaDownloaded(Ad ad) {

                            }

                            @Override
                            public void onError(Ad ad, AdError adError) {

                            }

                            @Override
                            public void onAdLoaded(Ad ad) {
//                                if (nativeAd == null || nativeAd != ad) {
//                                    return;
//                                }
//                                inflateAd(nativeAd);
//                                adContainer.removeAllViews();
//                                adContainer.addView(adView);

                            }

                            @Override
                            public void onAdClicked(Ad ad) {

                            }

                            @Override
                            public void onLoggingImpression(Ad ad) {

                            }
                        }).build());
        }
    private void inflateAd(NativeAd nativeAd) {

//        nativeAd.unregisterView();
//
//        // Add the Ad view into the ad container.
//        nativeAdLayout = findViewById(R.id.native_ad_container);
//        LayoutInflater inflater = LayoutInflater.from(NativeAdActivity.this);
//        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
//        adView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout_1, nativeAdLayout, false);
//        nativeAdLayout.addView(adView);
//
//        // Add the AdOptionsView
//        LinearLayout adChoicesContainer = findViewById(R.id.ad_choices_container);
//        AdOptionsView adOptionsView = new AdOptionsView(NativeAdActivity.this, nativeAd, nativeAdLayout);
//        adChoicesContainer.removeAllViews();
//        adChoicesContainer.addView(adOptionsView, 0);
//
//        // Create native UI using the ad metadata.
//        MediaView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
//        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
//        MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
//        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
//        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
//        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
//        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);
//
//        // Set the Text.
//        nativeAdTitle.setText(nativeAd.getAdvertiserName());
//        nativeAdBody.setText(nativeAd.getAdBodyText());
//        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
//        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
//        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
//        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());
//
//        // Create a list of clickable views
//        List<View> clickableViews = new ArrayList<>();
//        clickableViews.add(nativeAdTitle);
//        clickableViews.add(nativeAdCallToAction);
//
//        // Register the Title and CTA button to listen for clicks.
//        nativeAd.registerViewForInteraction(
//                adView, nativeAdMedia, nativeAdIcon, clickableViews);
    }
}
