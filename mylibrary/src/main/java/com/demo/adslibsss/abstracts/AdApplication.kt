package com.demo.adslibsss.abstracts

import android.app.Application
import android.util.Log
import android.widget.Toast
import com.demo.adslibsss.model.AdsData
import com.demo.adslibsss.Adlib.utils.AdUtils.Companion.UpdateTask
import com.demo.adslibsss.Adlib.utils.AdUtils.Companion.adsData
import com.demo.adslibsss.Adlib.utils.AdUtils.OnAdsJsonLoadListener
import com.demo.adslibsss.Adlib.utils.AdUtils.OnLoadedListener
import java.util.Calendar

abstract class AdApplication : Application() {
    abstract fun onCreated()
    override fun onCreate() {
        super.onCreate()
        mInstance = this
        val counter = Calendar.getInstance().getTimeInMillis()
        UpdateTask("/AdsCC/testConfig.json", object : OnAdsJsonLoadListener {
            override fun onFailure(th: Throwable) {
                isLoaded = true
            }

            override fun onLoaded(adsData: AdsData) {
                val loadTime = Calendar.getInstance().getTimeInMillis() - counter
                Log.e("MTAG", "onLoaded:$loadTime")
            }
        })
        onCreated()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        var mInstance: AdApplication? = null
        var isLoaded = false

//        @Synchronized
//        fun getInstance(): AdApplication {
//            var appApplication: AdApplication
//            synchronized(AdApplication::class.java) {
//                appApplication = mInstance
//            }
//            return appApplication
//        }

        @get:Synchronized
        val instance: AdApplication? get() {
                var appApplication: AdApplication?
                synchronized(AdApplication::class.java) {
                    appApplication = mInstance
                }
                return appApplication
            }

        @JvmStatic
        fun loadedAdsJson(listener: OnLoadedListener) {
            if (isLoaded) {
                listener.isLoaded(adsData)
                isLoaded = true
            } else {
                UpdateTask("/AdsCC/testConfig.json", object : OnAdsJsonLoadListener {
                    override fun onLoaded(adsData: AdsData) {
                        listener.isLoaded(adsData)
                        isLoaded = true
                    }

                    override fun onFailure(th: Throwable) {
                        isLoaded = false
                    }
                })
            }
        }
    }
}