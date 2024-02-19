package world.snacks.hub;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.webkit.WebView;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.UnityAds;


/* loaded from: classes.dex */
public class MyAppPizza {
    public static void initialize_ads(Context context, String str, String str2) {
        String processName;
        try {
            if (Build.VERSION.SDK_INT >= 28) {
                processName = Application.getProcessName();
                if (!context.getPackageName().equals(processName)) {
                    WebView.setDataDirectorySuffix(processName);
                }
            }
        } catch (Exception unused) {
        }
        MobileAds.initialize(context);
        AudienceNetworkAds.initialize(context);
        UnityAds.initialize(context, str, false, new IUnityAdsInitializationListener() { // from class: world.snacks.hub.MyAppPizza.1
            @Override // com.unity3d.ads.IUnityAdsInitializationListener
            public void onInitializationComplete() {
            }

            @Override // com.unity3d.ads.IUnityAdsInitializationListener
            public void onInitializationFailed(UnityAds.UnityAdsInitializationError unityAdsInitializationError, String str3) {
            }
        });
        StartAppSDK.init(context, str2, false);
        StartAppAd.disableSplash();
    }
}
