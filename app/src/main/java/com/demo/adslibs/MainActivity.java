package com.demo.adslibs;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.demo.adslibs.DataModel.AdsData;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout adContainer;
    private FrameLayout adContainerNative;
    private InternetReceiver internetReceiver;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unity_activity);
        adContainer = findViewById(R.id.banner1);
        adContainerNative = (FrameLayout)findViewById(R.id.mm1);
        Log.e("MTAG", "onCreate" +adContainerNative);



        internetReceiver = new InternetReceiver(isConnected -> {
            if (isConnected) {
                reloadAds();
//                showToast("Internet is connected");
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
        if(App.isLoaded){
            AdsManager adsManager = new AdsManager(MainActivity.this, adContainer);
            adsManager.initializeAds(MainActivity.this);
            adsManager.loadBannerAd();
            adsManager.showInterstitialAd(MainActivity.this);
        }else {
            Utils.UpdateTask("/AdsCC/config.json",new Utils.OnAdsJsonLoadListener() {
                @Override
                public void onLoaded(@NonNull AdsData adsData) {
                    AdsManager adsManager = new AdsManager(MainActivity.this, adContainer);
                    adsManager.initializeAds(MainActivity.this);
                    adsManager.loadBannerAd();
                    adsManager.showInterstitialAd(MainActivity.this);
                }
                @Override
                public void onFailure(@NonNull Throwable th) {

                }
            });
        }
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