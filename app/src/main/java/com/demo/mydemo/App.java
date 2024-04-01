package com.demo.mydemo;

import android.app.Application;
import android.content.Context;

public class App extends Application {
    public static final String TAG = "App";
    private static Context context;
    public static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        mInstance = this;
        new Utils.UpdateTask().execute();
    }

    public static synchronized App getInstance() {
        App appApplication;
        synchronized (App.class) {
            appApplication = mInstance;
        }
        return appApplication;
    }

  }