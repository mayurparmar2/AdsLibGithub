package com.demo.adslibss;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.demo.adslibss.DataModel.AdsData;
import com.demo.adslibss.mylib.MyAdsManager;

public class MainActivity extends AppCompatActivity {
    private FrameLayout adContainerNative;
    private InternetReceiver internetReceiver;
    MyAdsManager adsManager;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unity_activity);
        adContainerNative = (FrameLayout)findViewById(R.id.mm1);
        Button button = findViewById(R.id.showInterstitial);
        button.setOnClickListener(v -> {
            if(adsManager!=null){
                adsManager.showInterAd();
            }
        });
        Log.e("MTAG", "onCreate" +adContainerNative);



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