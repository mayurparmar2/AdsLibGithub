package com.demo.adslibsss;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ads.adslib.R;
import com.demo.adslibsss.mylib.utils.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.util.Objects;

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
        if (counter_ads >= Utils.getAdsData().getAds().getCounter()) {
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
    public void Banner_Show(final RelativeLayout Ad_Layout, Activity activity) {
        AdView mAdView = new AdView(activity);
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
    private static final String AD_MANAGER_AD_UNIT_ID = "/6499/example/native";
    public static void loadAdMobNativeAd(FrameLayout viewGroup,Context context) {
        try {
            AdLoader.Builder builder = new AdLoader.Builder(context, AD_MANAGER_AD_UNIT_ID);
            builder.forNativeAd(nativeAd -> {
                @SuppressLint("InflateParams") NativeAdView adView = (NativeAdView) ((Activity) context).getLayoutInflater()
                        .inflate(R.layout.admob_native_ad_template, null);
                populateNativeAdView(nativeAd, adView);
                viewGroup.removeAllViews();
                viewGroup.addView(adView);
            });



            VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();
            NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
            builder.withNativeAdOptions(adOptions);
            AdLoader adLoader = builder.withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    Log.e("adload", "adload faild $error");

                }
            }).build();
            adLoader.loadAd(new AdRequest.Builder().build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        try {
            // Set the media view.
            adView.setMediaView(adView.findViewById(R.id.ad_media));

            // Set other ad assets.
            adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
            adView.setBodyView(adView.findViewById(R.id.ad_body));
            adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
            adView.setIconView(adView.findViewById(R.id.ad_app_icon));
            adView.setPriceView(adView.findViewById(R.id.ad_price));
            adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
            adView.setStoreView(adView.findViewById(R.id.ad_store));
            adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

            // The headline and media content are guaranteed to be in every UnifiedNativeAd.
            ((TextView) Objects.requireNonNull(adView.getHeadlineView())).setText(nativeAd.getHeadline());
            Objects.requireNonNull(adView.getMediaView()).setMediaContent(Objects.requireNonNull(nativeAd.getMediaContent()));

            // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
            // check before trying to display them.
            if (nativeAd.getBody() == null) {
                Objects.requireNonNull(adView.getBodyView()).setVisibility(View.INVISIBLE);
            } else {
                Objects.requireNonNull(adView.getBodyView()).setVisibility(View.VISIBLE);
                ((TextView) adView.getBodyView()).setText(nativeAd.getBody());

            }

            if (nativeAd.getCallToAction() == null) {
                Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.INVISIBLE);

            } else {
                Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.VISIBLE);
                ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
            }

            if (nativeAd.getIcon() == null) {
                Objects.requireNonNull(adView.getIconView()).setVisibility(View.GONE);

            } else {
                ((ImageView) Objects.requireNonNull(adView.getIconView())).setImageDrawable(nativeAd.getIcon().getDrawable());
                adView.getIconView().setVisibility(View.VISIBLE);

            }

            if (nativeAd.getPrice() == null) {
                Objects.requireNonNull(adView.getPriceView()).setVisibility(View.INVISIBLE);

            } else {
                Objects.requireNonNull(adView.getPriceView()).setVisibility(View.VISIBLE);
                ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());

            }

            if (nativeAd.getStore() == null) {
                Objects.requireNonNull(adView.getStoreView()).setVisibility(View.INVISIBLE);

            } else {
                Objects.requireNonNull(adView.getStoreView()).setVisibility(View.VISIBLE);
                ((TextView) adView.getStoreView()).setText(nativeAd.getStore());

            }

            if (nativeAd.getStarRating() == null) {
                Objects.requireNonNull(adView.getStarRatingView()).setVisibility(View.INVISIBLE);

            } else {
                ((RatingBar) Objects.requireNonNull(adView.getStarRatingView())).setRating(nativeAd.getStarRating().floatValue());

                adView.getStarRatingView().setVisibility(View.VISIBLE);

            }

            if (nativeAd.getAdvertiser() == null) {
                Objects.requireNonNull(adView.getAdvertiserView()).setVisibility(View.INVISIBLE);

            } else {
                ((TextView) Objects.requireNonNull(adView.getAdvertiserView())).setText(nativeAd.getAdvertiser());
                adView.getAdvertiserView().setVisibility(View.VISIBLE);
            }

            // This method tells the Google Mobile Ads SDK that you have finished populating your
            // native ad view with this native ad.
            adView.setNativeAd(nativeAd);

            // Get the video controller for the ad. One will always be provided, even if the ad doesn't
            // have a video asset.
            VideoController vc = nativeAd.getMediaContent().getVideoController();

            // Updates the UI to say whether or not this ad has a video asset.
            if (vc.hasVideoContent()) {
                // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
                // VideoController will call methods on this object when events occur in the video
                // lifecycle.
                vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                    @Override
                    public void onVideoEnd() {
                        super.onVideoEnd();
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void loadAdMobNativeAd(RelativeLayout relativeLayout, Context context) {
//        AdLoader adLoader = new AdLoader.Builder(context, AD_MANAGER_AD_UNIT_ID)
//                .forNativeAd(nativeAd -> {
//                    // Handle the loaded native ad
//                    NativeAdView adView = (NativeAdView) ((Activity) context).getLayoutInflater()
//                            .inflate(R.layout.admob_native_ad_template, null);
//
//                    // Set the ad components
//                    adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
//                    adView.setBodyView(adView.findViewById(R.id.ad_body));
//                    adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));
//                    adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
//                    adView.setIconView(adView.findViewById(R.id.ad_icon));
//                    adView.setAdChoicesView((AdChoicesView) adView.findViewById(R.id.ad_choices));
//
//                    // Populate the ad data
//                    ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
//                    ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
//                    ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
//
//                    NativeAd.Image icon = nativeAd.getIcon();
//                    if (icon != null) {
//                        ((ImageView) adView.getIconView()).setImageDrawable(icon.getDrawable());
//                    }
//
//                    MediaView mediaView = adView.getMediaView();
//                    if (mediaView != null) {
//                        mediaView.setMediaContent(nativeAd.getMediaContent());
//                    }
//
//                    // Register the ad view with the native ad
//                    adView.setNativeAd(nativeAd);
//
//                    // Add the ad view to the RelativeLayout
//                    relativeLayout.removeAllViews();
//                    relativeLayout.addView(adView);
//                })
//                .withNativeAdOptions(new NativeAdOptions.Builder().build())
//                .build();
//
//        adLoader.loadAd(new AdRequest.Builder().build());
//    }


}
