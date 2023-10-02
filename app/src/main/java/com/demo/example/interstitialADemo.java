package com.demo.example;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.demo.example.MetaAds.AdsUnit;
import com.demo.example.MetaAds.FacebookAds;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;


public class interstitialADemo extends AppCompatActivity {
    private final String TAG = "interstitialADemo";
    InterstitialAd interstitialAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
//        AdsUnit.BANNER = "IMG_16_9_APP_INSTALL";
//        AdsUnit.INTERSTITIAL = "IMG_16_9_APP_INSTALL";
//        FacebookAds.loadInterstitial(interstitialADemo.this);
//
//        FacebookAds.setBanner(findViewById(R.id.banner_container), interstitialADemo.this);
//
//        Button showInterstitial = findViewById(R.id.showInterstitial);
//        showInterstitial.setOnClickListener(v -> {
//            // Code here
//            new FacebookAds(() -> {
//                // Next Action
//                Toast.makeText(interstitialADemo.this, "Ads Closed", Toast.LENGTH_SHORT).show();
//            }).showInterstitial();
//        });


        interstitialAd = new InterstitialAd(this, "1378441163022708_1378632936336864");
        // Create listeners for the Interstitial Ad
        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        };

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd(
                interstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());

    }
}
