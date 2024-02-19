package com.example.admob2;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.common.util.IOUtils;

import java.io.IOException;


public class AdsHandler {
    public static SharedPreferences.Editor editor = null;
    public static AdsHandler instance = null;

    public static String interstitialId = "";
    public static String bannerId = "";
    public static String nativeId = "";
    public static String openAds = "";
    public static String rewardedId = "";
//
//    public static String interstitialId = "/6499/example/interstitial";
//    public static String bannerId = "/6499/example/banner";
//    public static String nativeId = "/6499/example/native";
//    public static String openAds = "/6499/example/app-open";
//    public static String rewardedId = "/6499/example/rewarded";


    public static SharedPreferences sharedPreferences;
    private static Activity activity = null;

    public static byte[] getByte(Context context, int i) throws IOException {
        return IOUtils.toByteArray(context.getResources().openRawResource(i));
    }

    public static boolean isAdsOn() {
        SharedPreferences sharedPreferences2 = sharedPreferences;
        return sharedPreferences2 != null && sharedPreferences2.getBoolean("ads", true);
    }

    public static void setAdsOn(boolean z) {
        editor.putBoolean("ads", z);
        editor.apply();
    }

    public static synchronized AdsHandler getInstance(Activity activity2) {
        AdsHandler adsHandler;
        synchronized (AdsHandler.class) {
            activity = activity2;
            SharedPreferences sharedPreferences2 = activity2.getSharedPreferences("AdmobPref", 0);
            sharedPreferences = sharedPreferences2;
            editor = sharedPreferences2.edit();
            if (instance == null) {
                instance = new AdsHandler();
            }
            adsHandler = instance;
        }
        return adsHandler;
    }
}
