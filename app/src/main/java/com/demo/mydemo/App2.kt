package com.demo.mydemo

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import world.snacks.hub.AppOpenManager_Exclude
import world.snacks.hub.MyAppPizza
class App2 : Application() , Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {
    private var mInstance: App2? = null
    var f8266a: xgcvgc? = null
    private val currentActivity: Activity? = null
    var isOpenAd = 0

    @Synchronized
    fun getInstance(): App2? {
        var appopennnn: App2?
        synchronized(App2::class.java) {
            synchronized(App2::class.java) {
                synchronized(App2::class.java) { appopennnn = mInstance }
                return appopennnn
            }
            return appopennnn
        }
        return appopennnn
    }

    override fun onCreate() {
        super<Application>.onCreate()

        mInstance = this
        xzhvchx.instance!!.init(mInstance)
        MyAppPizza.initialize_ads(this, idddd.AC_App_ID, idddd.Tappx)
        AppOpenManager_Exclude(this, "launcherrrr", "", "", "", "")
        val xgcvgcVar = xgcvgc(applicationContext)
        this.f8266a = xgcvgcVar
        this.isOpenAd = xgcvgcVar.getIntSharedPreferences("1")

    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }
}