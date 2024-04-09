package com.demo.adslibsss.mylib.fb

import android.content.Context
import android.util.Log
import com.facebook.ads.AdSettings
import com.facebook.ads.AudienceNetworkAds
import com.facebook.ads.AudienceNetworkAds.InitListener
import com.facebook.ads.AudienceNetworkAds.InitResult

class AudienceNetworkInitializeHelper : InitListener {
    override fun onInitialized(initResult: InitResult) {
        Log.d(AudienceNetworkAds.TAG, initResult.message)
    }

    companion object {
        fun initialize(context: Context?) {
            if (AudienceNetworkAds.isInitialized(context)) {
                return
            }
            AudienceNetworkAds.buildInitSettings(context).withInitListener(AudienceNetworkInitializeHelper()).initialize()
        }
        fun initializeAd(context: Context?, debug: Boolean) {
            if (AudienceNetworkAds.isInitialized(context)) {
                return
            }
            if (debug) {
                AdSettings.turnOnSDKDebugger(context)
                AdSettings.setTestMode(true)
                AdSettings.setIntegrationErrorMode(AdSettings.IntegrationErrorMode.INTEGRATION_ERROR_CRASH_DEBUG_MODE)
            }
            AudienceNetworkAds.buildInitSettings(context).withInitListener(AudienceNetworkInitializeHelper()).initialize()
        }
    }
}
