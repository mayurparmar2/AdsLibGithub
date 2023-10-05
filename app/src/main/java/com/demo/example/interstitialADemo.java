package com.demo.example;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.demo.example.MetaAds.FbAds;
import com.facebook.ads.InterstitialAd;


public class interstitialADemo extends AppCompatActivity {
    private final String TAG = "interstitialADemo";
    InterstitialAd interstitialAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        FbAds fbAds = new FbAds(this);
        fbAds.Banner_Show((RelativeLayout) findViewById(R.id.banner), this);
        Button showInterstitial = findViewById(R.id.showInterstitial);
        showInterstitial.setOnClickListener(v -> {
            fbAds.Interstitial_Show(this);
        });
    }
}
