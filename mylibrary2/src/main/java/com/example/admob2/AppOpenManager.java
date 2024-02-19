package com.example.admob2;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;


public class AppOpenManager implements Application.ActivityLifecycleCallbacks, LifecycleObserver {
    private static final String LOG_TAG = "AppOpenManager";
    private static boolean isShowingAd = false;
    private final AdsApplication myApplication;
    private AppOpenAd appOpenAd = null;
    private Activity currentActivity;
    private AppOpenAd.AppOpenAdLoadCallback loadCallback;

    public AppOpenManager(AdsApplication adsApplication) {
        this.myApplication = adsApplication;
        adsApplication.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityPreCreated(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    public void fetchAd() {
        if (isAdAvailable()) {
            return;
        }
        this.loadCallback = new AppOpenAd.AppOpenAdLoadCallback() {
            @Override
            public void onAdLoaded(AppOpenAd appOpenAd) {
                AppOpenManager.this.appOpenAd = appOpenAd;
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }
        };
        AppOpenAd.load(this.myApplication, AdsHandler.openAds, getAdRequest(), 1, this.loadCallback);
    }

    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    public boolean isAdAvailable() {
        return this.appOpenAd != null;
    }

    @Override
    public void onActivityStarted(Activity activity) {
        this.currentActivity = activity;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        this.currentActivity = activity;
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        this.currentActivity = null;
    }

    public void showAdIfAvailable() {
        if (!isShowingAd && isAdAvailable()) {
            this.appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    AppOpenManager.this.appOpenAd = null;
                    boolean unused = AppOpenManager.isShowingAd = false;
                    AppOpenManager.this.fetchAd();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    boolean unused = AppOpenManager.isShowingAd = true;
                }
            });
            this.appOpenAd.show(this.currentActivity);
            return;
        }
        Log.d(LOG_TAG, "Can not show ad.");
        fetchAd();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        showAdIfAvailable();
        Log.d(LOG_TAG, "onStart");
    }
}
