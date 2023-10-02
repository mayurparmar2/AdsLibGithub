package com.demo.example;

import android.app.Application;

import com.facebook.ads.AudienceNetworkAds;

//import com.facebook.ads.AudienceNetworkAds;

public class App extends Application {
    private final String TAG = "App";

    @Override
    public void onCreate() {
        super.onCreate();
        AudienceNetworkAds.initialize(this);
    }
}
