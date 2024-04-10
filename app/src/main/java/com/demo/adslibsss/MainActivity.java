package com.demo.adslibsss;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.demo.adslibsss.abstracts.AdActivity;
import com.demo.adslibsss.Adlib.AppOpenAd;
import com.demo.adslibsss.Adlib.Constant;
import com.demo.adslibsss.abstracts.AdApplication;
import com.demo.adslibsss.network.InternetReceiver;

public class MainActivity extends AdActivity {
    private LinearLayout nativeAdViewContainer;
    MyAdsManager adsManager;
    AppOpenAd.Builder appOpenAdBuilder;
    LifecycleObserver lifecycleObserver = new DefaultLifecycleObserver() {
        @Override
        public void onStart(@NonNull LifecycleOwner owner) {
            DefaultLifecycleObserver.super.onStart(owner);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (Constant.OPEN_ADS_ON_RESUME) {
                    if (AppOpenAd.isAppOpenAdLoaded) {
                        appOpenAdBuilder.show();
                    }
                }
            }, 100);
        }
    };
    private InternetReceiver internetReceiver;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unity_activity);
        Button button = findViewById(R.id.showInterstitial);
        button.setOnClickListener(v -> {
            if (adsManager != null) {
                adsManager.showInterAd();
            }
        });
        Log.e("MTAG", "onCreate" + nativeAdViewContainer);
    }
    private void initAds() {
        adsManager = new MyAdsManager(MainActivity.this);
        adsManager.initializationAd();
        adsManager.loadBannerAd();
        adsManager.loadInterAd();

        nativeAdViewContainer = findViewById(R.id.native_ad);
//        Tools.setNativeAdStyle(this, nativeAdViewContainer, medium);
//        Tools.setNativeAdStyle(nativeAdViewContainer)
        adsManager.loadNativeAdView(nativeAdViewContainer);
        //appOpen
        adsManager.loadOpenAds(() -> {
            Log.e("TAG", "initAds:12345  ");

            return null;
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(internetReceiver);
        ProcessLifecycleOwner.get().getLifecycle().removeObserver(lifecycleObserver);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void isConnected(boolean isConnected) {
        if (isConnected) {
            AdApplication.loadedAdsJson(adsData -> {
                initAds();
            });
            showToast("Internet is connected");
        } else {
            showToast("No internet connection");
        }
    }


}