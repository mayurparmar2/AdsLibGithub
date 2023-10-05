package com.demo.example;

import android.app.Application;
import android.util.Log;

import com.facebook.ads.AudienceNetworkAds;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.UnityAds;

//import com.facebook.ads.AudienceNetworkAds;

public class App extends Application implements IUnityAdsInitializationListener {
    private final String TAG = "App";
    private String unityGameID = "5331426";
    private Boolean testMode = true;
    @Override
    public void onCreate() {
        super.onCreate();
        UnityAds.initialize(getApplicationContext(), unityGameID, testMode, this);

//        AudienceNetworkAds.initialize(this);
    }
    @Override
    public void onInitializationComplete() {
        Log.e("MTAG", "onInitializationComplete 1");
    }

    @Override
    public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
//        Log.e("MTAG", "onInitializationComplete 1");

        Log.e("MTAG", "onInitializationFailed" );

    }
}
