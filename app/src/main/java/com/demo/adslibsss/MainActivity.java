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

import com.demo.adslibsss.DataModel.AdsData;
import com.demo.adslibsss.mylib.AppOpenAd;
import com.demo.adslibsss.mylib.Constant;
import com.demo.adslibsss.mylib.MyAdsManager;
import com.demo.adslibsss.mylib.utils.Utils;

public class MainActivity extends AppCompatActivity {
    private LinearLayout nativeAdViewContainer;
    private InternetReceiver internetReceiver;
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

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unity_activity);
        Button button = findViewById(R.id.showInterstitial);
        button.setOnClickListener(v -> {
            if(adsManager!=null){
                adsManager.showInterAd();
            }
        });
        Log.e("MTAG", "onCreate" + nativeAdViewContainer);



        internetReceiver = new InternetReceiver(isConnected -> {
            if (isConnected) {
                reloadAds();
                showToast("Internet is connected");
            } else {
                showToast("No internet connection");
            }
        });
        reloadAds();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetReceiver, filter);
    }
    protected void reloadAds(){
        if(App.isLoaded){
            initAds();
        }else {
            Utils.UpdateTask("/AdsCC/testConfig.json",new Utils.OnAdsJsonLoadListener() {
                @Override
                public void onLoaded(@NonNull AdsData adsData) {
                    initAds();
                }
                @Override
                public void onFailure(@NonNull Throwable th) {
                }
            });
        }
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
        adsManager.loadOpenAds();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(lifecycleObserver);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(internetReceiver);
        ProcessLifecycleOwner.get().getLifecycle().removeObserver(lifecycleObserver);

    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}