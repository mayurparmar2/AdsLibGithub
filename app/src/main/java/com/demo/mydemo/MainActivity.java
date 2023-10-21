package com.demo.mydemo;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.demo.mydemo.manager.AdMobAds;
import com.demo.mydemo.manager.AdsManager;
import com.demo.mydemo.manager.InternetReceiver;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout adContainer;
    private InternetReceiver internetReceiver;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unity_activity);
        adContainer = findViewById(R.id.banner1);
        internetReceiver = new InternetReceiver(isConnected -> {
            if (isConnected) {
                reloadAds();
                showToast("Internet is connected");
            } else {
                showToast("No internet connection");
            }
        });
        reloadAds();
        // Register the receiver to listen for connectivity changes
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetReceiver, filter);
    }
    protected void reloadAds(){
        new App.UpdateTask(new App.UpdateTask.OnAdsJsonLoadListener() {
            @Override
            public void onLoaded() {
                AdsManager adsManager = new AdsManager(MainActivity.this, adContainer);
                adsManager.initializeAds(MainActivity.this);
                adsManager.loadBannerAd();
                adsManager.showInterstitialAd(MainActivity.this);
                adsManager.loadNativeAd();
            }
        }).execute();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(internetReceiver);
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}