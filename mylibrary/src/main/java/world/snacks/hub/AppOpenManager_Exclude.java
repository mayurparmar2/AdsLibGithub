package world.snacks.hub;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

/* loaded from: classes.dex */
public class AppOpenManager_Exclude implements Application.ActivityLifecycleCallbacks, LifecycleObserver {
    private static boolean isShowingAd = false;

    /* renamed from: a  reason: collision with root package name */
    String f9809a = "";

    /* renamed from: b  reason: collision with root package name */
    String f9810b;

    /* renamed from: c  reason: collision with root package name */
    String f9811c;
    private Activity currentActivity;

    /* renamed from: d  reason: collision with root package name */
    String f9812d;

    /* renamed from: e  reason: collision with root package name */
    String f9813e;

    /* renamed from: f  reason: collision with root package name */
    String f9814f;
    private final Application myApplication;

    public AppOpenManager_Exclude(Application application, String str, String str2, String str3, String str4, String str5) {
        this.myApplication = application;
        this.f9810b = str;
        this.f9811c = str2;
        this.f9812d = str3;
        this.f9813e = str4;
        this.f9814f = str5;
        application.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityCreated(Activity activity, Bundle bundle) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityDestroyed(Activity activity) {
        this.currentActivity = null;
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityPaused(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityPreCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityResumed(Activity activity) {
        this.currentActivity = activity;
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityStarted(Activity activity) {
        this.currentActivity = activity;
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityStopped(Activity activity) {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        Log.e("onStart", "onStart");
        Log.e("onStart", "" + this.currentActivity.getLocalClassName());
        Log.e("onStart", "" + this.f9810b);
        if (!this.currentActivity.getLocalClassName().equals(this.f9810b) && !this.currentActivity.getLocalClassName().equals(this.f9811c) && !this.currentActivity.getLocalClassName().equals(this.f9812d) && !this.currentActivity.getLocalClassName().equals(this.f9813e) && !this.currentActivity.getLocalClassName().equals(this.f9814f)) {
            showAdIfAvailable();
        }
    }

    public void showAdIfAvailable() {
        Pizza.AppOpen_Show(this.currentActivity);
    }
}
