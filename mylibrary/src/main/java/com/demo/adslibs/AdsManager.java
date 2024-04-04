package com.demo.adslibs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ads.adslib.R;


public class AdsManager {
    private Activity activity;
    private RelativeLayout adContainer;

    AdMobAds adMobAds;
    FacebookAds facebookAds;
    UnityMyAds unityAds;
    private Handler adRefreshHandler;


    public AdsManager(Activity context, RelativeLayout adContainer) {
        this.activity = context;
        this.adContainer = adContainer;
        adRefreshHandler = new Handler();
    }

    public void initializeAds(Activity activity) {

        if(Utils.getAdsData().getAds().getEnable()){
            switch (Utils.getAdsData().getAds().getMain_ads()) {
                case "admob":
                    adMobAds=new AdMobAds(activity);
                    break;
                case "facebook":
                    facebookAds = new FacebookAds(activity);
                    break;
                case "unity":
                    unityAds = new UnityMyAds(activity);
                    break;
                default:
                    // Handle unsupported platform
            }
        }
    }
    public void showInterstitialAd(Activity activity) {
        if(Utils.getAdsData().getAds().getEnable()){
            switch (Utils.getAdsData().getAds().getMain_ads()) {
                case "admob":
                    new AdMobAds(activity).Interstitial_Show_Counter(activity);
                    // Handle interstitial ad for AdMob
                    break;
                case "facebook":
                    facebookAds.Interstitial_Show_Counter(activity);
                    break;
                case "unity":
                    unityAds.Interstitial_Show_Counter(activity);
//                new UnityAds().showInterstitialAd();
                    break;
                // Add cases for other platforms if needed
            }
        }
    }
    public void loadBannerAd() {
        if(Utils.getAdsData().getAds().getEnable()){
            switch (Utils.getAdsData().getAds().getMain_ads()) {
                case "admob":
                    adMobAds.Banner_Show(adContainer, activity);
                    break;
                case "facebook":
                    facebookAds.Banner_Show(adContainer,activity);
                    break;
                case "unity":
                    unityAds.Banner_Show(adContainer,activity);
                    break;
                // Add cases for other platforms if needed
            }
        }
    }

    public void loadNativeAd(FrameLayout layout) {
        if(Utils.getAdsData().getAds().getEnable()){
            switch (Utils.getAdsData().getAds().getMain_ads()) {
                case "admob":
                    adMobAds.loadAdMobNativeAd(layout,activity);
                    break;
                case "facebook":
//                new FacebookAds().loadNativeAd();
                    break;
                case "unity":
//                    unityAds.(adContainer,context);
                    break;
                // Add cases for other platforms if needed
            }
        }
    }
    public void startAdRefresh(int AD_REFRESH_INTERVAL,FrameLayout viewGroup) {
        adRefreshHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadNativeAd(viewGroup);
                adRefreshHandler.postDelayed(this, AD_REFRESH_INTERVAL);
            }
        }, AD_REFRESH_INTERVAL);
    }
    private static final String Counter_Ads = "Counter_Ads";
    public static void setCounter_Ads(Context mContext, int string) {
        mContext.getSharedPreferences(mContext.getPackageName(), 0).edit()
                .putInt(Counter_Ads, string).commit();
    }
    public static int getCounter_Ads(Context mContext) {
        return mContext.getSharedPreferences(mContext.getPackageName(), 0)
                .getInt(Counter_Ads, 1);
    }




    public void Exit_Popup_Without_Ads(final Context context) {
        try {
            @SuppressLint("ResourceType") final Dialog dialog = new Dialog(context, 16973834);
            dialog.requestWindowFeature(1);
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.without_ads_exit);
            ((TextView) dialog.findViewById(R.id.title)).setText("Do You Want To Exit ?");
            Button button = (Button) dialog.findViewById(R.id.Btn_Yes);
            Button button2 = (Button) dialog.findViewById(R.id.Btn_Rate);
            Button button3 = (Button) dialog.findViewById(R.id.Btn_No);
//            Native(context, (RelativeLayout) dialog.findViewById(R.id.banner), 1);
            button.setText("Yes");
            button2.setText("Rate Us");
            button3.setText("No");
            button.setOnClickListener(new View.OnClickListener() { // from class: world.snacks.hub.Pizza.21
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    try {
                        dialog.dismiss();
                        System.exit(0);
                    } catch (Exception unused) {
                    }
                }
            });
            button2.setOnClickListener(new View.OnClickListener() { // from class: world.snacks.hub.Pizza.22
                @SuppressLint("WrongConstant")
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + context.getPackageName()));
                    intent.addFlags(1207959552);
                    try {
                        context.startActivity(intent);
                    } catch (ActivityNotFoundException unused) {
                        Context context2 = context;
                        context2.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://play.google.com/OneForAll/apps/details?id=" + context.getPackageName())));
                    }
                }
            });
            button3.setOnClickListener(new View.OnClickListener() { // from class: world.snacks.hub.Pizza.23
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    try {
                        dialog.dismiss();
                    } catch (Exception unused) {
                    }
                }
            });
            dialog.show();
        } catch (Exception unused) {
        }
    }
//    public static void Native(Context context, RelativeLayout relativeLayout, int i2) {
//        if (isNetworkConnected(context) && !show_ads.equals("0") && n_nooff.equals("1")) {
//            if (b_n_ex.equals("1")) {
//                Banner_FB(context, relativeLayout, i2);
//            } else {
//                Native_FB(context, relativeLayout, i2);
//            }
//        }
//    }
    public static boolean isNetworkConnected(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

}
