package com.demo.example;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;

import java.util.ArrayList;
import java.util.List;


public class Native extends AppCompatActivity {
    private final String TAG = "interstitialADemo";
    InterstitialAd interstitialAd;
    private NativeAd nativeAd;
    private NativeAdLayout nativeAdLayout;
    private LinearLayout adView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.native_ads);
        nativeAdLayout = findViewById(R.id.native_ad_container);
        loadNativeAd();
    }

    private void loadNativeAd() {
        nativeAd = new NativeAd(this, "1378441163022708_1379129396287218");
        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                Log.e("MTAG", "Native.class,onMediaDownloaded():");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.e("MTAG", "Native.class,adError():" + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Race condition, load() called again before last ad was displayed
                if (nativeAd == null || nativeAd != ad) {
                    return;
                }
                // Inflate Native Ad into Container
                Log.e("MTAG", "Native.class,onAdLoaded():" + ad);
                nativeAdLayout.removeAllViews();
                inflateAd(nativeAd);
                handler.postDelayed(reloadAdRunnable, AD_RELOAD_INTERVAL);
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

//        if showNativeAdWithDelay
//        showNativeAdWithDelay();
    }

    private void inflateAd(NativeAd nativeAd) {

        nativeAd.unregisterView();

        // Add the Ad view into the ad container.
        LayoutInflater inflater = LayoutInflater.from(Native.this);
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        adView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout_1, nativeAdLayout, false);
        nativeAdLayout.addView(adView);

        // Add the AdOptionsView
        LinearLayout adChoicesContainer = findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(Native.this, nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        MediaView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
                adView, nativeAdMedia, nativeAdIcon, clickableViews);

    }

    private Handler handler = new Handler(Looper.getMainLooper());
    private static final int AD_RELOAD_INTERVAL = 20; // 20 seconds


    private Runnable reloadAdRunnable = new Runnable() {
        @Override
        public void run() {
            Log.e("MTAG", "Native.class,reloadAdRunnable:");
//            if (nativeAd == null || !nativeAd.isAdLoaded()) {
//                return;
//            }
//            // Check if ad is already expired or invalidated, and do not show ad if that is the case. You will not get paid to show an invalidated ad.
//            if (nativeAd.isAdInvalidated()) {
//                return;
//            }

            if (nativeAd.isAdLoaded()){
                loadNativeAd();
                // Schedule the next ad reload.
            }
            handler.postDelayed(this, AD_RELOAD_INTERVAL);

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(reloadAdRunnable);
    }

//    private void showNativeAdWithDelay() {
//        /**
//         * Here is an example for displaying the ad with delay;
//         * Please do not copy the Handler into your project
//         */
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            public void run() {
//
//                // Check if nativeAd has been loaded successfully
//                if(nativeAd == null || !nativeAd.isAdLoaded()) {
//                    return;
//                }
//                // Check if ad is already expired or invalidated, and do not show ad if that is the case. You will not get paid to show an invalidated ad.
//                if(nativeAd.isAdInvalidated()) {
//                    return;
//                }
//                loadNativeAd();
////              inflateAd(nativeAd); // Inflate NativeAd into a container, same as in previous code examples
//            }
//        }, 1000 * 6); // Show the ad after 15 minutes
//    }


}
