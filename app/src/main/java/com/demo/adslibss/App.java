package com.demo.adslibss;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import com.demo.adslibss.DataModel.AdsData;

public class App extends Application {
    public static final String TAG = "App";
    private static Context context;
    public static App mInstance;

    public static boolean isLoaded = false;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        mInstance = this;

        Utils.UpdateTask("/AdsCC/testConfig.json",new Utils.OnAdsJsonLoadListener(){
            @Override
            public void onFailure(@NonNull Throwable th) {
                isLoaded =true;
            }

            @Override
            public void onLoaded(@NonNull AdsData adsData) {

            }
        });
//        new Utils.UpdateTask().execute();
    }

    public static synchronized App getInstance() {
        App appApplication;
        synchronized (App.class) {
            appApplication = mInstance;
        }
        return appApplication;
    }

  }