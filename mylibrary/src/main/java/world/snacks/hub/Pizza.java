package world.snacks.hub;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.browser.trusted.sharing.ShareTarget;
import androidx.core.internal.view.SupportMenu;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdBase;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeAdView;
import com.facebook.ads.NativeAdViewAttributes;
import com.facebook.ads.NativeBannerAd;
import com.facebook.ads.NativeBannerAdView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.ads.banner.Mrec;
import com.startapp.sdk.adsbase.StartAppAd;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;
import com.unity3d.services.banners.IUnityBannerListener;
import com.unity3d.services.banners.UnityBanners;
import com.unity3d.services.banners.view.BannerPosition;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pl.droidsonroids.gif.GifImageView;
import world.snacks.hub.NativeStyle;

/* loaded from: classes2.dex */
public class Pizza {
    public static String Ac_Reward = "id";
    public static String BGColor1 = "#292929";
    public static String Exit_Pop_Setup = "0";
    public static String Extra1 = "0";
    public static String Extra2 = "0";
    public static String Extra3 = "0";
    public static String Extra4 = "0";
    public static String Extra5 = "0";
    public static InterstitialAd FB_Inter = null;
    public static String GL_Setup_Ads = "L";
    public static com.google.android.gms.ads.interstitial.InterstitialAd Google_Inter = null;
    public static Boolean Inter_Loading = null;
    public static int Loading_Data = 0;
    public static String Native_custom = "0";
    public static Boolean Splesh_Timer = null;
    public static int Text_Color_main = 0;
    public static String ac_App = "id";
    public static String ac_Banner = "id";
    public static String ac_Inter = "id";
    public static String admob_ao = "id";
    public static String admob_b1 = "id";
    public static String admob_b11 = "id";
    public static String admob_b2 = "id";
    public static String admob_b22 = "id";
    public static String admob_b3 = "id";
    public static String admob_b33 = "id";
    public static String admob_i1 = "id";
    public static String admob_i11 = "id";
    public static String admob_i2 = "id";
    public static String admob_i22 = "id";
    public static String admob_i3 = "id";
    public static String admob_i33 = "id";
    public static String admob_n1 = "id";
    public static String admob_n11 = "id";
    public static String admob_n2 = "id";
    public static String admob_n22 = "id";
    public static String admob_n3 = "id";
    public static String admob_n33 = "id";
    public static String ao_ads = "1";

    /* renamed from: b  reason: collision with root package name */
    static OnJsonCallBackListner f9816b = null;
    public static String b_n_ex = "0";
    public static String b_nooff = "1";
    public static Context code_context = null;
    public static String counter_ads = "3";
    public static int counter_ads_tot = 3;
    public static Boolean doubleBackToExitPressedOnce = null;
    public static String fb_b = "id";
    public static String fb_i = "id";
    public static String fb_mr = "id";
    public static String fb_n = "id";
    public static String fb_ns = "id";
    public static String for_Approval = "0";
    public static int gogole_banner_id_count = 0;
    public static int gogole_native_id_count = 0;
    public static int gogole_splesh_inter_id_count = 0;
    public static int google_banner_id_exchange_H = 0;
    public static int google_banner_id_exchange_L = 0;
    public static int google_banner_id_exchange_M = 0;
    public static int google_id_exchange_H = 0;
    public static int google_id_exchange_L = 0;
    public static int google_id_exchange_M = 0;
    public static int google_native_id_exchange_H = 0;
    public static int google_native_id_exchange_L = 0;
    public static int google_native_id_exchange_M = 0;
    public static Handler handler_splesh_counter = null;
    public static String i_nooff = "1";
    public static String increase_ads = "1";
    public static String n_nooff = "1";
    public static String only_fb_inter = "0";
    public static Runnable runnable_splesh_counter = null;
    public static String show_ads = "1";
    public static String splash_anim = "0";
    public static String splesh_ads = "1";
    public static OnSpleshJsonCallBackListner splesh_callbck = null;
    public static String tappx = "id";

    /* renamed from: a  reason: collision with root package name */
    GifImageView f9817a = null;
    public static int BGColor = Color.parseColor("#292929");
    public static String TitleTextColor1 = "#ffffff";
    public static int TitleTextColor = Color.parseColor(TitleTextColor1);
    public static String DescriptionTextColor1 = "#b3b3b3";
    public static int DescriptionTextColor = Color.parseColor(DescriptionTextColor1);
    public static String ButtonColor1 = "#474747";
    public static int ButtonColor = Color.parseColor(ButtonColor1);
    public static String ButtonTextColor1 = "#ffffff";
    public static int ButtonTextColor = Color.parseColor(ButtonTextColor1);
    public static String ButtonBorderColor1 = "#ffffff";
    public static int ButtonBorderColor = Color.parseColor(ButtonBorderColor1);
    public static String Link_URL = "";
    public static ArrayList<String> Google_SetUp_List_Custom = new ArrayList<>(Arrays.asList("L"));

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: world.snacks.hub.Pizza$7  reason: invalid class name */
    /* loaded from: classes2.dex */
    public static class AnonymousClass7 implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        final /* synthetic */ Context f9878a;

        /* renamed from: b  reason: collision with root package name */
        final /* synthetic */ Dialog f9879b;

        AnonymousClass7(Context context, Dialog dialog) {
            this.f9878a = context;
            this.f9879b = dialog;
        }

        @Override // java.lang.Runnable
        public void run() {
            UnityAds.load(Pizza.ac_Inter, new IUnityAdsLoadListener() { // from class: world.snacks.hub.Pizza.7.1
                @Override // com.unity3d.ads.IUnityAdsLoadListener
                public void onUnityAdsAdLoaded(String str) {
                    IUnityAdsShowListener iUnityAdsShowListener = new IUnityAdsShowListener() { // from class: world.snacks.hub.Pizza.7.1.1
                        @Override // com.unity3d.ads.IUnityAdsShowListener
                        public void onUnityAdsShowClick(String str2) {
                            Log.e("UnityAdsExample i", "onUnityAdsShowClick: " + str2);
                        }

                        @Override // com.unity3d.ads.IUnityAdsShowListener
                        public void onUnityAdsShowComplete(String str2, UnityAds.UnityAdsShowCompletionState unityAdsShowCompletionState) {
                            Log.e("UnityAdsExample i", "onUnityAdsShowComplete: " + str2);
                            Pizza.Interstial_Load(AnonymousClass7.this.f9878a);
                        }

                        @Override // com.unity3d.ads.IUnityAdsShowListener
                        public void onUnityAdsShowFailure(String str2, UnityAds.UnityAdsShowError unityAdsShowError, String str3) {
                            Pizza.Interstial_Load(AnonymousClass7.this.f9878a);
                            Log.e("UnityAdsExample i", "Unity Ads failed to show ad for " + str2 + " with error: [" + unityAdsShowError + "] " + str3);
                        }

                        @Override // com.unity3d.ads.IUnityAdsShowListener
                        public void onUnityAdsShowStart(String str2) {
                            Log.e("UnityAdsExample i", "onUnityAdsShowStart: " + str2);
                        }
                    };
                    if (!Pizza.Splesh_Timer.booleanValue()) {
                        try {
                            UnityAds.show((Activity) AnonymousClass7.this.f9878a, Pizza.ac_Inter, new UnityAdsShowOptions(), iUnityAdsShowListener);
                        } catch (Exception unused) {
                        }
                    }
                    Pizza.handler_splesh_counter.removeCallbacks(Pizza.runnable_splesh_counter);
                    try {
                        Dialog dialog = AnonymousClass7.this.f9879b;
                        if (dialog != null && dialog.isShowing()) {
                            AnonymousClass7.this.f9879b.dismiss();
                        }
                    } catch (Exception unused2) {
                    }
                }

                @Override // com.unity3d.ads.IUnityAdsLoadListener
                public void onUnityAdsFailedToLoad(String str, UnityAds.UnityAdsLoadError unityAdsLoadError, String str2) {
                    Log.e("onUnityAdsFailedToLoad", "" + str2);
                    Pizza.handler_splesh_counter.removeCallbacks(Pizza.runnable_splesh_counter);
                    try {
                        Dialog dialog = AnonymousClass7.this.f9879b;
                        if (dialog != null && dialog.isShowing()) {
                            AnonymousClass7.this.f9879b.dismiss();
                        }
                    } catch (Exception unused) {
                    }
                    Pizza.Interstial_Load(AnonymousClass7.this.f9878a);
                }
            });
        }
    }

    /* loaded from: classes2.dex */
    public class GetData extends AsyncTask<Void, Void, Void> {
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        /* renamed from: a */
        public Void doInBackground(Void... voidArr) {
            try {
                HttpHandler httpHandler = new HttpHandler();
                String makeServiceCall = httpHandler.makeServiceCall("" + Pizza.Link_URL);
                if (makeServiceCall != null) {
                    try {
                        JSONObject jSONObject = new JSONObject(makeServiceCall);
                        JSONArray jSONArray = jSONObject.getJSONArray("" + Pizza.code_context.getPackageName());
                        for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                            JSONObject jSONObject2 = jSONArray.getJSONObject(i2);
                            Pizza.show_ads = jSONObject2.getString("show_ads");
                            Pizza.Exit_Pop_Setup = jSONObject2.getString("exit_pop");
                            Pizza.counter_ads = jSONObject2.getString("counter_ads");
                            Pizza.splesh_ads = jSONObject2.getString("splesh_ads");
                            Pizza.fb_b = jSONObject2.getString("fb_b1");
                            Pizza.fb_mr = jSONObject2.getString("fb_mr1");
                            Pizza.fb_i = jSONObject2.getString("fb_i1");
                            Pizza.fb_n = jSONObject2.getString("fb_n1");
                            Pizza.fb_ns = jSONObject2.getString("fb_ns1");
                            Pizza.GL_Setup_Ads = jSONObject2.getString("GL_setup_ads");
                            Pizza.admob_i1 = jSONObject2.getString("admob_i1");
                            Pizza.admob_i11 = jSONObject2.getString("admob_i11");
                            Pizza.admob_i2 = jSONObject2.getString("admob_i2");
                            Pizza.admob_i22 = jSONObject2.getString("admob_i22");
                            Pizza.admob_i3 = jSONObject2.getString("admob_i3");
                            Pizza.admob_i33 = jSONObject2.getString("admob_i33");
                            Pizza.admob_b1 = jSONObject2.getString("admob_b1");
                            Pizza.admob_b11 = jSONObject2.getString("admob_b11");
                            Pizza.admob_b2 = jSONObject2.getString("admob_b2");
                            Pizza.admob_b22 = jSONObject2.getString("admob_b22");
                            Pizza.admob_b3 = jSONObject2.getString("admob_b3");
                            Pizza.admob_b33 = jSONObject2.getString("admob_b33");
                            Pizza.admob_n1 = jSONObject2.getString("admob_n1");
                            Pizza.admob_n11 = jSONObject2.getString("admob_n11");
                            Pizza.admob_n2 = jSONObject2.getString("admob_n2");
                            Pizza.admob_n22 = jSONObject2.getString("admob_n22");
                            Pizza.admob_n3 = jSONObject2.getString("admob_n3");
                            Pizza.admob_n33 = jSONObject2.getString("admob_n33");
                            Pizza.admob_ao = jSONObject2.getString("admob_ao1");
                            Pizza.tappx = jSONObject2.getString("tappx");
                            Pizza.ac_App = jSONObject2.getString("ac_app");
                            SharPerf.setAC_App(Pizza.code_context, Pizza.ac_App);
                            Pizza.ac_Banner = jSONObject2.getString("ac_b");
                            SharPerf.setAC_Banner(Pizza.code_context, Pizza.ac_Banner);
                            Pizza.ac_Inter = jSONObject2.getString("ac_i");
                            SharPerf.setAC_Inter(Pizza.code_context, Pizza.ac_Inter);
                            Pizza.Ac_Reward = jSONObject2.getString("ac_r");
                            SharPerf.setAC_Reward(Pizza.code_context, Pizza.Ac_Reward);
                            Pizza.splash_anim = jSONObject2.getString("splash_anim");
                            Pizza.i_nooff = jSONObject2.getString("i_nooff");
                            Pizza.b_nooff = jSONObject2.getString("b_nooff");
                            Pizza.n_nooff = jSONObject2.getString("n_nooff");
                            Pizza.b_n_ex = jSONObject2.getString("b_n_ex");
                            Pizza.increase_ads = jSONObject2.getString("increase_ads");
                            Pizza.for_Approval = jSONObject2.getString("for_Approval");
                            Pizza.ao_ads = jSONObject2.getString("ao_ads");
                            Pizza.Native_custom = jSONObject2.getString("Native_custom");
                            Pizza.only_fb_inter = jSONObject2.getString("only_fb_inter");
                            Pizza.BGColor = Color.parseColor(jSONObject2.getString("BGColor"));
                            Pizza.TitleTextColor = Color.parseColor(jSONObject2.getString("TitleTextColor"));
                            Pizza.DescriptionTextColor = Color.parseColor(jSONObject2.getString("DescriptionTextColor"));
                            Pizza.ButtonColor = Color.parseColor(jSONObject2.getString("ButtonColor"));
                            Pizza.ButtonTextColor = Color.parseColor(jSONObject2.getString("ButtonTextColor"));
                            Pizza.ButtonBorderColor = Color.parseColor(jSONObject2.getString("ButtonBorderColor"));
                            Pizza.Extra1 = jSONObject2.getString("ex1");
                            Pizza.Extra2 = jSONObject2.getString("ex2");
                            Pizza.Extra3 = jSONObject2.getString("ex3");
                            Pizza.Extra4 = jSONObject2.getString("ex4");
                            Pizza.Extra5 = jSONObject2.getString("ex5");
                            SharPerf.set_show_ads(Pizza.code_context, Pizza.show_ads);
                            SharPerf.setExit_Pop(Pizza.code_context, Pizza.Exit_Pop_Setup);
                            SharPerf.set_counter_ads(Pizza.code_context, Pizza.counter_ads);
                            SharPerf.set_splesh_ads(Pizza.code_context, Pizza.splesh_ads);
                            SharPerf.set_fb_b(Pizza.code_context, Pizza.fb_b);
                            SharPerf.set_fb_mr(Pizza.code_context, Pizza.fb_mr);
                            SharPerf.set_fb_i(Pizza.code_context, Pizza.fb_i);
                            SharPerf.set_fb_n(Pizza.code_context, Pizza.fb_n);
                            SharPerf.set_fb_ns(Pizza.code_context, Pizza.fb_ns);
                            SharPerf.setonly_fb_inter(Pizza.code_context, Pizza.only_fb_inter);
                            SharPerf.setGL_Setup_Ads(Pizza.code_context, Pizza.GL_Setup_Ads);
                            SharPerf.set_admob_i1(Pizza.code_context, Pizza.admob_i1);
                            SharPerf.set_admob_i11(Pizza.code_context, Pizza.admob_i11);
                            SharPerf.set_admob_i2(Pizza.code_context, Pizza.admob_i2);
                            SharPerf.set_admob_i2(Pizza.code_context, Pizza.admob_i22);
                            SharPerf.set_admob_i3(Pizza.code_context, Pizza.admob_i3);
                            SharPerf.set_admob_i33(Pizza.code_context, Pizza.admob_i33);
                            SharPerf.set_admob_b1(Pizza.code_context, Pizza.admob_b1);
                            SharPerf.set_admob_b11(Pizza.code_context, Pizza.admob_b11);
                            SharPerf.set_admob_b2(Pizza.code_context, Pizza.admob_b2);
                            SharPerf.set_admob_b22(Pizza.code_context, Pizza.admob_b22);
                            SharPerf.set_admob_b3(Pizza.code_context, Pizza.admob_b3);
                            SharPerf.set_admob_b33(Pizza.code_context, Pizza.admob_b33);
                            SharPerf.set_admob_n1(Pizza.code_context, Pizza.admob_n1);
                            SharPerf.set_admob_n11(Pizza.code_context, Pizza.admob_n11);
                            SharPerf.set_admob_n2(Pizza.code_context, Pizza.admob_n2);
                            SharPerf.set_admob_n22(Pizza.code_context, Pizza.admob_n22);
                            SharPerf.set_admob_n3(Pizza.code_context, Pizza.admob_n3);
                            SharPerf.set_admob_n33(Pizza.code_context, Pizza.admob_n33);
                            SharPerf.set_Tappx(Pizza.code_context, Pizza.tappx);
                            SharPerf.set_admob_ao(Pizza.code_context, Pizza.admob_ao);
                            SharPerf.setNative_custom(Pizza.code_context, Pizza.Native_custom);
                            SharPerf.seti_nooff(Pizza.code_context, Pizza.i_nooff);
                            SharPerf.setb_nooff(Pizza.code_context, Pizza.b_nooff);
                            SharPerf.setn_nooff(Pizza.code_context, Pizza.n_nooff);
                            SharPerf.setb_n_ex(Pizza.code_context, Pizza.b_n_ex);
                            SharPerf.setFor_Approval(Pizza.code_context, Pizza.for_Approval);
                            SharPerf.setIncrease_Ads(Pizza.code_context, Pizza.increase_ads);
                            SharPerf.setAO_Setup(Pizza.code_context, Pizza.ao_ads);
                            SharPerf.setBGColor(Pizza.code_context, jSONObject2.getString("BGColor"));
                            SharPerf.setTitleTextColor(Pizza.code_context, jSONObject2.getString("TitleTextColor"));
                            SharPerf.setDescriptionTextColor(Pizza.code_context, jSONObject2.getString("DescriptionTextColor"));
                            SharPerf.setButtonColor(Pizza.code_context, jSONObject2.getString("ButtonColor"));
                            SharPerf.setButtonTextColor(Pizza.code_context, jSONObject2.getString("ButtonTextColor"));
                            SharPerf.setButtonBorderColor(Pizza.code_context, jSONObject2.getString("ButtonBorderColor"));
                            SharPerf.setExtra1(Pizza.code_context, Pizza.Extra1);
                            SharPerf.setExtra2(Pizza.code_context, Pizza.Extra2);
                            SharPerf.setExtra3(Pizza.code_context, Pizza.Extra3);
                            SharPerf.setExtra4(Pizza.code_context, Pizza.Extra4);
                            SharPerf.setExtra5(Pizza.code_context, Pizza.Extra5);
                            Log.e("json = ", "done");
                        }
                        Pizza.Google_SetUp_List_Custom = new ArrayList<>();
                        for (int i3 = 0; i3 < Pizza.GL_Setup_Ads.length(); i3++) {
                            ArrayList<String> arrayList = Pizza.Google_SetUp_List_Custom;
                            arrayList.add("" + Pizza.GL_Setup_Ads.charAt(i3));
                        }
                        Pizza.Loading_Data = 1;
                    } catch (JSONException e2) {
                        Pizza.error(e2.getMessage());
                    }
                } else {
                    Pizza.error("null json");
                }
            } catch (Exception unused) {
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        /* renamed from: b */
        public void onPostExecute(Void r1) {
            super.onPostExecute(r1);
            Pizza.f9816b.OnJsonDone();
            Pizza.splesh_callbck.OnSpleshJsonDone();
            Pizza.counter_ads_tot = Integer.parseInt(SharPerf.get_counter_ads(Pizza.code_context));
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    public class HttpHandler {

        public String makeServiceCall(String url) {
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
                httpURLConnection.setRequestMethod("GET");

                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    try (BufferedInputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                         BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

                        StringBuilder sb = new StringBuilder();
                        String readLine;
                        while ((readLine = bufferedReader.readLine()) != null) {
                            sb.append(readLine);
                            sb.append('\n');
                        }
                        return sb.toString();
                    }
                } else {
                    // Handle non-OK response code, if needed
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
    /* loaded from: classes2.dex */
//    public static class HttpHandler {
//        private String convertStreamToString(InputStream inputStream) {
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//            StringBuilder sb = new StringBuilder();
//            while (true) {
//                try {
//                    try {
//                        try {
//                            String readLine = bufferedReader.readLine();
//                            if (readLine == null) {
//                                break;
//                            }
//                            sb.append(readLine);
//                            sb.append('\n');
//                        } catch (IOException e2) {
//                            e2.printStackTrace();
//                        }
//                    } catch (IOException e3) {
//                        e3.printStackTrace();
//                        inputStream.close();
//                    }
//                } catch (Throwable th) {
//                    try {
//                        inputStream.close();
//                    } catch (IOException e4) {
//                        e4.printStackTrace();
//                    }
//                    throw th;
//                }
//            }
//            inputStream.close();
//            return sb.toString();
//        }
//
//        public String makeServiceCall(String str) {
//            try {
//                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
//                httpURLConnection.setRequestMethod(ShareTarget.METHOD_GET);
//                return convertStreamToString(new BufferedInputStream(httpURLConnection.getInputStream()));
//            } catch (Exception unused) {
//                return null;
//            }
//        }
//    }

    static {
        Boolean bool = Boolean.FALSE;
        Splesh_Timer = bool;
        Loading_Data = 999;
        Text_Color_main = 0xffff0000;
        gogole_splesh_inter_id_count = 0;
        google_id_exchange_H = 1;
        google_id_exchange_M = 1;
        google_id_exchange_L = 1;
        Inter_Loading = bool;
        FB_Inter = null;
        Google_Inter = null;
        gogole_native_id_count = 0;
        google_native_id_exchange_H = 1;
        google_native_id_exchange_M = 1;
        google_native_id_exchange_L = 1;
        gogole_banner_id_count = 0;
        google_banner_id_exchange_H = 1;
        google_banner_id_exchange_M = 1;
        google_banner_id_exchange_L = 1;
        doubleBackToExitPressedOnce = bool;
    }

    public Pizza(Context context, OnJsonCallBackListner onJsonCallBackListner, String str, String url, String str3, int i2, int i3, String str4, int i4, int i5, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18, String str19, String str20, String str21, String str22, String str23, String str24, String str25, String str26, String str27, String str28, String str29, String str30, String str31, String str32, String str33, String str34, String str35, String str36, String str37, String str38, String str39, String str40, String str41, String str42, String str43, String str44, String str45, String str46, String str47, String str48, String str49, String str50, String str51, String str52, String str53, String str54, String str55, String str56, String str57, String str58, String str59, String str60, String str61, String str62, String str63, String str64, String str65, String str66) {
        Link_URL = url;
        f9816b = onJsonCallBackListner;
        code_context = context;
        show_ads = "" + i2;
        splesh_ads = "" + i4;
        fb_b = str6;
        fb_mr = str11;
        fb_i = str16;
        fb_n = str21;
        fb_ns = str26;
        admob_i1 = str37;
        admob_i11 = str38;
        admob_i2 = str39;
        admob_i22 = str40;
        admob_i3 = str41;
        admob_i33 = str42;
        admob_b1 = str43;
        admob_b11 = str44;
        admob_b2 = str45;
        admob_b22 = str46;
        admob_b3 = str47;
        admob_b33 = str48;
        admob_n1 = str49;
        admob_n11 = str50;
        admob_n2 = str51;
        admob_n22 = str52;
        admob_n3 = str53;
        admob_n33 = str54;
        admob_ao = str55;
        ac_App = str31;
        ac_Inter = str32;
        ac_Banner = str33;
        Ac_Reward = str34;
        tappx = str35;
        splash_anim = str5;
        if (SharPerf.get_dwnld(code_context) == 0) {
            Count_Download(code_context.getPackageName());
            SharPerf.set_dwnld(code_context, 1);
        }
        if (SharPerf.getFirst_ads(code_context) == 0) {
            SharPerf.set_show_ads(code_context, show_ads);
            SharPerf.set_counter_ads(code_context, counter_ads);
            SharPerf.set_splesh_ads(code_context, splesh_ads);
            SharPerf.set_fb_b(code_context, fb_b);
            SharPerf.set_fb_mr(code_context, fb_mr);
            SharPerf.set_fb_i(code_context, fb_i);
            SharPerf.set_fb_n(code_context, fb_n);
            SharPerf.set_fb_ns(code_context, fb_ns);
            SharPerf.set_admob_i1(code_context, admob_i1);
            SharPerf.set_admob_i11(code_context, admob_i11);
            SharPerf.set_admob_i2(code_context, admob_i2);
            SharPerf.set_admob_i2(code_context, admob_i22);
            SharPerf.set_admob_i3(code_context, admob_i3);
            SharPerf.set_admob_i33(code_context, admob_i33);
            SharPerf.set_admob_b1(code_context, admob_b1);
            SharPerf.set_admob_b11(code_context, admob_b11);
            SharPerf.set_admob_b2(code_context, admob_b2);
            SharPerf.set_admob_b22(code_context, admob_b22);
            SharPerf.set_admob_b3(code_context, admob_b3);
            SharPerf.set_admob_b33(code_context, admob_b33);
            SharPerf.set_admob_n1(code_context, admob_n1);
            SharPerf.set_admob_n11(code_context, admob_n11);
            SharPerf.set_admob_n2(code_context, admob_n2);
            SharPerf.set_admob_n22(code_context, admob_n22);
            SharPerf.set_admob_n3(code_context, admob_n3);
            SharPerf.set_admob_n33(code_context, admob_n33);
            SharPerf.set_Tappx(code_context, tappx);
            SharPerf.set_admob_ao(code_context, admob_ao);
            SharPerf.setAC_App(code_context, ac_App);
            SharPerf.setAC_Inter(code_context, ac_Inter);
            SharPerf.setAC_Banner(code_context, ac_Banner);
            SharPerf.setAC_Reward(code_context, Ac_Reward);
            SharPerf.setsplash_anim(code_context, splash_anim);
            SharPerf.setIncrease_Ads(code_context, increase_ads);
            SharPerf.setFor_Approval(code_context, for_Approval);
            SharPerf.setFirst_ads(code_context, 1);
        }
    }

    public static void AppOpen_Show(Activity activity) {
        if (!ao_ads.equals("0")) {
            Interstial_Show(activity);
        }
    }

    public static void Banner(Context context, RelativeLayout relativeLayout, int i2) {
        if (isNetworkConnected(context) && !show_ads.equals("0") && b_nooff.equals("1")) {
            if (b_n_ex.equals("2")) {
                Native_FB(context, relativeLayout, i2);
            } else {
                Banner_FB(context, relativeLayout, i2);
            }
        }
    }

    public static void Banner_FB(final Context context, final RelativeLayout relativeLayout, final int i2) {
        AdSize adSize;
        final AdView adView;
        if (fb_b.equals("id")) {
            Banner_GL(context, relativeLayout, i2);
        } else if (only_fb_inter.equals("1")) {
            Banner_GL(context, relativeLayout, i2);
        } else {
            if (i2 == 3) {
                adSize = AdSize.RECTANGLE_HEIGHT_250;
            } else if (i2 == 2) {
                adSize = AdSize.BANNER_HEIGHT_90;
            } else {
                adSize = AdSize.BANNER_HEIGHT_50;
            }
            if (i2 == 3) {
                adView = new AdView(context, "" + fb_mr, adSize);
            } else {
                adView = new AdView(context, "" + fb_b, adSize);
            }
            adView.loadAd(adView.buildLoadAdConfig().withAdListener(new AdListener() { // from class: world.snacks.hub.Pizza.18
                @Override // com.facebook.ads.AdListener
                public void onAdClicked(Ad ad) {
                }

                @Override // com.facebook.ads.AdListener
                public void onAdLoaded(Ad ad) {
                    try {
                        relativeLayout.setVisibility(View.VISIBLE);
                        relativeLayout.removeAllViews();
                        relativeLayout.addView(adView);
                    } catch (Exception unused) {
                    }
                }

                @Override // com.facebook.ads.AdListener
                public void onError(Ad ad, AdError adError) {
                    Pizza.Banner_GL(context, relativeLayout, i2);
                }

                @Override // com.facebook.ads.AdListener
                public void onLoggingImpression(Ad ad) {
                }
            }).build());
        }
    }

    public static void Banner_GL(final Context context, final RelativeLayout relativeLayout, final int i2) {
        String str;
        if (admob_b3.equals("id")) {
            Banner_SA(context, relativeLayout, i2);
            return;
        }
        AdRequest build = new AdRequest.Builder().build();
        String str2 = Google_SetUp_List_Custom.get(gogole_banner_id_count);
        if (str2.equals("H")) {
            if (google_banner_id_exchange_H == 1) {
                str = admob_b1;
                google_banner_id_exchange_H = 11;
            } else {
                str = admob_b11;
                google_banner_id_exchange_H = 1;
            }
        } else if (str2.equals("M")) {
            if (google_banner_id_exchange_M == 1) {
                str = admob_b2;
                google_banner_id_exchange_M = 11;
            } else {
                str = admob_b22;
                google_banner_id_exchange_M = 1;
            }
        } else if (google_banner_id_exchange_L == 1) {
            str = admob_b3;
            google_banner_id_exchange_L = 11;
        } else {
            str = admob_b33;
            google_banner_id_exchange_L = 1;
        }
        final com.google.android.gms.ads.AdView adView = new com.google.android.gms.ads.AdView(context);
        if (i2 == 3) {
            adView.setAdSize(com.google.android.gms.ads.AdSize.MEDIUM_RECTANGLE);
        } else if (i2 == 2) {
            adView.setAdSize(com.google.android.gms.ads.AdSize.LARGE_BANNER);
        } else {
            adView.setAdSize(com.google.android.gms.ads.AdSize.BANNER);
        }
        adView.setAdUnitId(str);
        adView.setAdListener(new com.google.android.gms.ads.AdListener() { // from class: world.snacks.hub.Pizza.19
            @Override // com.google.android.gms.ads.AdListener
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                if (Pizza.gogole_banner_id_count + 1 == Pizza.Google_SetUp_List_Custom.size()) {
                    Pizza.gogole_banner_id_count = 0;
                    Pizza.Banner_SA(context, relativeLayout, i2);
                    return;
                }
                Pizza.gogole_banner_id_count++;
                Pizza.Banner_GL(context, relativeLayout, i2);
            }

            @Override // com.google.android.gms.ads.AdListener
            public void onAdLoaded() {
                super.onAdLoaded();
                Pizza.gogole_banner_id_count = 0;
                try {
                    relativeLayout.removeAllViews();
                    relativeLayout.addView(adView);
                    relativeLayout.setVisibility(View.VISIBLE);
                } catch (Exception unused) {
                }
            }
        });
        adView.loadAd(build);
    }

    public static void Banner_SA(final Context context, final RelativeLayout relativeLayout, int i2) {
        if (show_ads.equals("0")) {
            return;
        }
        if (ac_Banner.equals("id")) {
            if (!tappx.equals("id")) {
                try {
                    Banner banner = new Banner(context);
                    relativeLayout.setVisibility(View.VISIBLE);
                    relativeLayout.removeAllViews();
                    relativeLayout.addView(banner);
                    return;
                } catch (Exception unused) {
                    return;
                }
            }
            return;
        }
        int i3 = 100;
        if (!UnityAds.isInitialized()) {
            i3 = AdError.SERVER_ERROR_CODE;
        }
        try {
            UnityBanners.destroy();
        } catch (Exception unused2) {
        }
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { // from class: world.snacks.hub.Pizza.20
            @Override // java.lang.Runnable
            public void run() {
                UnityBanners.setBannerListener(new IUnityBannerListener() { // from class: world.snacks.hub.Pizza.20.1
                    @Override // com.unity3d.services.banners.IUnityBannerListener
                    public void onUnityBannerClick(String str) {
                    }

                    @Override // com.unity3d.services.banners.IUnityBannerListener
                    public void onUnityBannerError(String str) {
                        if (!Pizza.tappx.equals("id")) {
                            try {
                                Banner banner2 = new Banner(context);
                                relativeLayout.setVisibility(View.VISIBLE);
                                relativeLayout.removeAllViews();
                                relativeLayout.addView(banner2);
                            } catch (Exception unused3) {
                            }
                        }
                    }

                    @Override // com.unity3d.services.banners.IUnityBannerListener
                    public void onUnityBannerHide(String str) {
                    }

                    @Override // com.unity3d.services.banners.IUnityBannerListener
                    public void onUnityBannerLoaded(String str, View view) {
                        try {
                            relativeLayout.removeAllViews();
                            relativeLayout.setVisibility(View.VISIBLE);
                            relativeLayout.addView(view);
                        } catch (Exception unused3) {
                        }
                    }

                    @Override // com.unity3d.services.banners.IUnityBannerListener
                    public void onUnityBannerShow(String str) {
                    }

                    @Override // com.unity3d.services.banners.IUnityBannerListener
                    public void onUnityBannerUnloaded(String str) {
                        if (!Pizza.tappx.equals("id")) {
                            try {
                                Banner banner2 = new Banner(context);
                                relativeLayout.setVisibility(View.VISIBLE);
                                relativeLayout.removeAllViews();
                                relativeLayout.addView(banner2);
                            } catch (Exception unused3) {
                            }
                        }
                    }
                });
                UnityBanners.setBannerPosition(BannerPosition.BOTTOM_CENTER);
                UnityBanners.loadBanner((Activity) context, Pizza.ac_Banner);
            }
        }, i3);
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    public static void Count_Download(String str) {
        if (SharPerf.getExtra4(code_context).equals("1")) {
            try {
                WebView webView = new WebView(code_context);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setLoadWithOverviewMode(true);
                webView.getSettings().setUseWideViewPort(true);
                webView.loadUrl("https://elveevocsy.click/ElveeVocsy/api/download.php?package_name=" + str);
            } catch (Exception unused) {
            }
        }
    }

    public static void Increase_Ads(Context context) {
        if (increase_ads.equals("1")) {
            Pre_Interstial_Show(context);
        }
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    public static void Inter_Request(String str) {
        if (SharPerf.getExtra4(code_context).equals("1")) {
            try {
                WebView webView = new WebView(code_context);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setLoadWithOverviewMode(true);
                webView.getSettings().setUseWideViewPort(true);
                webView.loadUrl("https://elveevocsy.click/ElveeVocsy/api/interstitial.php?package_name=" + str + "&interstitial_request");
            } catch (Exception unused) {
            }
        }
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    public static void Inter_Show(String str) {
        if (SharPerf.getExtra4(code_context).equals("1")) {
            try {
                WebView webView = new WebView(code_context);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setLoadWithOverviewMode(true);
                webView.getSettings().setUseWideViewPort(true);
                webView.loadUrl("https://elveevocsy.click/ElveeVocsy/api/interstitial.php?package_name=" + str + "&interstitial_show");
            } catch (Exception unused) {
            }
        }
    }

    public static void Interstial_Counted(Context context) {
        try {
            if (show_ads.equals("0")) {
                return;
            }
            int count_Ads = SharPerf.getCount_Ads(context);
            if (count_Ads >= counter_ads_tot) {
                SharPerf.setCount_Ads(context, 1);
                Interstial_Show(context);
            } else {
                SharPerf.setCount_Ads(context, count_Ads + 1);
            }
        } catch (Exception unused) {
        }
    }

    public static void Interstial_FB(final Context context) {
        if (fb_i.equals("id")) {
            Interstial_GL(context);
            return;
        }
        FB_Inter = new InterstitialAd(context, "" + fb_i);
        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() { // from class: world.snacks.hub.Pizza.8
            @Override // com.facebook.ads.AdListener
            public void onAdClicked(Ad ad) {
            }

            @Override // com.facebook.ads.AdListener
            public void onAdLoaded(Ad ad) {
                Pizza.Inter_Loading = Boolean.FALSE;
            }

            @Override // com.facebook.ads.AdListener
            public void onError(Ad ad, AdError adError) {
                Pizza.FB_Inter = null;
                Pizza.Interstial_GL(context);
            }

            @Override // com.facebook.ads.InterstitialAdListener
            public void onInterstitialDismissed(Ad ad) {
                Pizza.Interstial_Load(context);
            }

            @Override // com.facebook.ads.InterstitialAdListener
            public void onInterstitialDisplayed(Ad ad) {
                Pizza.Inter_Show(Pizza.code_context.getPackageName());
            }

            @Override // com.facebook.ads.AdListener
            public void onLoggingImpression(Ad ad) {
            }
        };
        Inter_Request(code_context.getPackageName());
        InterstitialAd interstitialAd = FB_Inter;
        interstitialAd.loadAd(interstitialAd.buildLoadAdConfig().withAdListener(interstitialAdListener).build());
    }

    public static void Interstial_FB_LoadShow(final CustomProgressDialogueInter customProgressDialogueInter, final Context context) {
        try {
            if (show_ads.equals("1")) {
                if (fb_i.equals("id")) {
                    Interstial_Gl_LoadShow(customProgressDialogueInter, context);
                    return;
                }
                final InterstitialAd interstitialAd = new InterstitialAd(context, "" + fb_i);
                InterstitialAdListener interstitialAdListener = new InterstitialAdListener() { // from class: world.snacks.hub.Pizza.11
                    @Override // com.facebook.ads.AdListener
                    public void onAdClicked(Ad ad) {
                    }

                    @Override // com.facebook.ads.AdListener
                    public void onAdLoaded(Ad ad) {
                        try {
                            interstitialAd.show();
                            if (customProgressDialogueInter != null && customProgressDialogueInter.isShowing()) {
                                customProgressDialogueInter.dismiss();
                            }
                        } catch (Exception unused) {
                        }
                    }

                    @Override // com.facebook.ads.AdListener
                    public void onError(Ad ad, AdError adError) {
                        Pizza.Interstial_Gl_LoadShow(customProgressDialogueInter, context);
                    }

                    @Override // com.facebook.ads.InterstitialAdListener
                    public void onInterstitialDismissed(Ad ad) {
                    }

                    @Override // com.facebook.ads.InterstitialAdListener
                    public void onInterstitialDisplayed(Ad ad) {
                        Pizza.Inter_Show(Pizza.code_context.getPackageName());
                    }

                    @Override // com.facebook.ads.AdListener
                    public void onLoggingImpression(Ad ad) {
                    }
                };
                Inter_Request(code_context.getPackageName());
                interstitialAd.loadAd(interstitialAd.buildLoadAdConfig().withAdListener(interstitialAdListener).build());
            }
        } catch (Exception unused) {
        }
    }

    public static void Interstial_GL(final Context context) {
        String str;
        if (admob_i3.equals("id")) {
            Inter_Loading = Boolean.FALSE;
            return;
        }
        AdRequest build = new AdRequest.Builder().build();
        String str2 = Google_SetUp_List_Custom.get(gogole_splesh_inter_id_count);
        if (str2.equals("H")) {
            if (google_id_exchange_H == 1) {
                str = admob_i1;
                google_id_exchange_H = 11;
            } else {
                str = admob_i11;
                google_id_exchange_H = 1;
            }
        } else if (str2.equals("M")) {
            if (google_id_exchange_M == 1) {
                str = admob_i2;
                google_id_exchange_M = 11;
            } else {
                str = admob_i22;
                google_id_exchange_M = 1;
            }
        } else if (google_id_exchange_L == 1) {
            str = admob_i3;
            google_id_exchange_L = 11;
        } else {
            str = admob_i33;
            google_id_exchange_L = 1;
        }
        com.google.android.gms.ads.interstitial.InterstitialAd.load(context, str, build, new InterstitialAdLoadCallback() { // from class: world.snacks.hub.Pizza.9
            @Override // com.google.android.gms.ads.AdLoadCallback
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Pizza.Google_Inter = null;
                Pizza.Inter_Loading = Boolean.FALSE;
                if (Pizza.gogole_splesh_inter_id_count + 1 == Pizza.Google_SetUp_List_Custom.size()) {
                    Pizza.gogole_splesh_inter_id_count = 0;
                    return;
                }
                Pizza.gogole_splesh_inter_id_count++;
                Pizza.Interstial_GL(context);
            }

            @Override // com.google.android.gms.ads.AdLoadCallback
            public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                Pizza.gogole_splesh_inter_id_count = 0;
                Pizza.Inter_Loading = Boolean.FALSE;
                Pizza.Google_Inter = interstitialAd;
                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() { // from class: world.snacks.hub.Pizza.9.1
                    @Override // com.google.android.gms.ads.FullScreenContentCallback
                    public void onAdClicked() {
                        super.onAdClicked();
                    }

                    @Override // com.google.android.gms.ads.FullScreenContentCallback
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        Pizza.Interstial_Load(context);
                    }

                    @Override // com.google.android.gms.ads.FullScreenContentCallback
                    public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                        Pizza.Interstial_Load(context);
                    }

                    @Override // com.google.android.gms.ads.FullScreenContentCallback
                    public void onAdImpression() {
                        super.onAdImpression();
                    }

                    @Override // com.google.android.gms.ads.FullScreenContentCallback
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();
                    }
                });
            }
        });
    }

    public static void Interstial_Gl_LoadShow(final CustomProgressDialogueInter customProgressDialogueInter, final Context context) {
        String str;
        if (show_ads.equals("1")) {
            if (admob_i3.equals("id")) {
                Interstial_SA_LoadShow(customProgressDialogueInter, context);
                return;
            }
            AdRequest build = new AdRequest.Builder().build();
            String str2 = Google_SetUp_List_Custom.get(gogole_splesh_inter_id_count);
            if (str2.equals("H")) {
                if (google_id_exchange_H == 1) {
                    str = admob_i1;
                    google_id_exchange_H = 11;
                } else {
                    str = admob_i11;
                    google_id_exchange_H = 1;
                }
            } else if (str2.equals("M")) {
                if (google_id_exchange_M == 1) {
                    str = admob_i2;
                    google_id_exchange_M = 11;
                } else {
                    str = admob_i22;
                    google_id_exchange_M = 1;
                }
            } else if (google_id_exchange_L == 1) {
                str = admob_i3;
                google_id_exchange_L = 11;
            } else {
                str = admob_i33;
                google_id_exchange_L = 1;
            }
            com.google.android.gms.ads.interstitial.InterstitialAd.load(context, str, build, new InterstitialAdLoadCallback() { // from class: world.snacks.hub.Pizza.12
                @Override // com.google.android.gms.ads.AdLoadCallback
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    if (Pizza.gogole_splesh_inter_id_count + 1 == Pizza.Google_SetUp_List_Custom.size()) {
                        Pizza.gogole_splesh_inter_id_count = 0;
                        Pizza.Interstial_SA_LoadShow(customProgressDialogueInter, context);
                        return;
                    }
                    Pizza.gogole_splesh_inter_id_count++;
                    Pizza.Interstial_GL(context);
                }

                @Override // com.google.android.gms.ads.AdLoadCallback
                public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                    try {
                        try {
                            Pizza.gogole_splesh_inter_id_count = 0;
                            interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() { // from class: world.snacks.hub.Pizza.12.1
                                @Override // com.google.android.gms.ads.FullScreenContentCallback
                                public void onAdClicked() {
                                    super.onAdClicked();
                                }

                                @Override // com.google.android.gms.ads.FullScreenContentCallback
                                public void onAdDismissedFullScreenContent() {
                                    super.onAdDismissedFullScreenContent();
                                }

                                @Override // com.google.android.gms.ads.FullScreenContentCallback
                                public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                                    super.onAdFailedToShowFullScreenContent(adError);
                                    Pizza.Interstial_SA_LoadShow(customProgressDialogueInter, context);
                                }

                                @Override // com.google.android.gms.ads.FullScreenContentCallback
                                public void onAdImpression() {
                                    super.onAdImpression();
                                }

                                @Override // com.google.android.gms.ads.FullScreenContentCallback
                                public void onAdShowedFullScreenContent() {
                                    super.onAdShowedFullScreenContent();
                                }
                            });
                            interstitialAd.show((Activity) context);
                            CustomProgressDialogueInter customProgressDialogueInter2 =customProgressDialogueInter;
                            if (customProgressDialogueInter2 != null && customProgressDialogueInter2.isShowing()) {
                               customProgressDialogueInter.dismiss();
                            }
                        } catch (Exception unused) {
                            CustomProgressDialogueInter customProgressDialogueInter3 =customProgressDialogueInter;
                            if (customProgressDialogueInter3 != null && customProgressDialogueInter3.isShowing()) {
                               customProgressDialogueInter.dismiss();
                            }
                        }
                    } catch (Exception unused2) {
                    }
                }
            });
        }
    }

    public static void Interstial_Load(Context context) {
        if (!isNetworkConnected(context) || show_ads.equals("0") || Extra5.equals("1") || Inter_Loading.booleanValue()) {
            return;
        }
        Inter_Loading = Boolean.TRUE;
        Interstial_FB(context);
    }

    public static void Interstial_LoadShow(Context context) {
        if (!isNetworkConnected(context) || show_ads.equals("0") || i_nooff.equals("0")) {
            return;
        }
        CustomProgressDialogueInter customProgressDialogueInter = new CustomProgressDialogueInter(context, "Loading Ads . . .", "Wait While Loading Ads, Sorry for Inconvenience and Thank You for Support and Waiting.", BGColor, TitleTextColor);
        customProgressDialogueInter.show();
        Interstial_FB_LoadShow(customProgressDialogueInter, context);
    }

    public static void Interstial_SA_LoadShow(final CustomProgressDialogueInter customProgressDialogueInter, final Context context) {
        if (show_ads.equals("0")) {
            return;
        }
        if (ac_Inter.equals("id")) {
            if (!tappx.equals("id")) {
                StartAppAd.showAd(context);
            }
            if (customProgressDialogueInter != null) {
                try {
                    if (customProgressDialogueInter.isShowing()) {
                        customProgressDialogueInter.dismiss();
                        return;
                    }
                    return;
                } catch (Exception unused) {
                    return;
                }
            }
            return;
        }
        UnityAds.load(ac_Inter, new IUnityAdsLoadListener() { // from class: world.snacks.hub.Pizza.13
            @Override // com.unity3d.ads.IUnityAdsLoadListener
            public void onUnityAdsAdLoaded(String str) {
                IUnityAdsShowListener iUnityAdsShowListener = new IUnityAdsShowListener() { // from class: world.snacks.hub.Pizza.13.1
                    @Override // com.unity3d.ads.IUnityAdsShowListener
                    public void onUnityAdsShowClick(String str2) {
                        Log.e("UnityAdsExample i", "onUnityAdsShowClick: " + str2);
                    }

                    @Override // com.unity3d.ads.IUnityAdsShowListener
                    public void onUnityAdsShowComplete(String str2, UnityAds.UnityAdsShowCompletionState unityAdsShowCompletionState) {
                        Log.e("UnityAdsExample i", "onUnityAdsShowComplete: " + str2);
                    }

                    @Override // com.unity3d.ads.IUnityAdsShowListener
                    public void onUnityAdsShowFailure(String str2, UnityAds.UnityAdsShowError unityAdsShowError, String str3) {
                        try {
                            CustomProgressDialogueInter customProgressDialogueInter2 =customProgressDialogueInter;
                            if (customProgressDialogueInter2 != null && customProgressDialogueInter2.isShowing()) {
                               customProgressDialogueInter.dismiss();
                            }
                        } catch (Exception unused2) {
                        }
                        if (!Pizza.tappx.equals("id")) {
                            StartAppAd.showAd(context);
                        }
                        Log.e("UnityAdsExample i", "Unity Ads failed to show ad for " + str2 + " with error: [" + unityAdsShowError + "] " + str3);
                    }

                    @Override // com.unity3d.ads.IUnityAdsShowListener
                    public void onUnityAdsShowStart(String str2) {
                        Log.e("UnityAdsExample i", "onUnityAdsShowStart: " + str2);
                    }
                };
                try {
                    CustomProgressDialogueInter customProgressDialogueInter2 =customProgressDialogueInter;
                    if (customProgressDialogueInter2 != null && customProgressDialogueInter2.isShowing()) {
                       customProgressDialogueInter.dismiss();
                    }
                } catch (Exception unused2) {
                }
                try {
                    UnityAds.show((Activity) context, Pizza.ac_Inter, new UnityAdsShowOptions(), iUnityAdsShowListener);
                } catch (Exception unused3) {
                }
            }

            @Override // com.unity3d.ads.IUnityAdsLoadListener
            public void onUnityAdsFailedToLoad(String str, UnityAds.UnityAdsLoadError unityAdsLoadError, String str2) {
                Log.e("onUnityAdsFailedToLoad", "" + str2);
                try {
                    CustomProgressDialogueInter customProgressDialogueInter2 =customProgressDialogueInter;
                    if (customProgressDialogueInter2 != null && customProgressDialogueInter2.isShowing()) {
                       customProgressDialogueInter.dismiss();
                    }
                } catch (Exception unused2) {
                }
                if (!Pizza.tappx.equals("id")) {
                    StartAppAd.showAd(context);
                }
            }
        });
    }

    public static void Interstial_Show(Context context) {
        if (!isNetworkConnected(context) || show_ads.equals("0") || i_nooff.equals("0")) {
            return;
        }
        if (Extra5.equals("1")) {
            Interstial_LoadShow(context);
            return;
        }
        try {
            InterstitialAd interstitialAd = FB_Inter;
            if (interstitialAd != null && interstitialAd.isAdLoaded()) {
                FB_Inter.show();
                return;
            }
        } catch (Exception unused) {
        }
        try {
            com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd2 = Google_Inter;
            if (interstitialAd2 != null) {
                interstitialAd2.show((Activity) context);
                return;
            }
        } catch (Exception unused2) {
        }
        Interstial_UN(context);
    }

    public static void Interstial_UN(final Context context) {
        if (ac_Inter.equals("id")) {
            if (!tappx.equals("id")) {
                StartAppAd.showAd(context);
                return;
            }
            return;
        }
        final CustomProgressDialogueInter customProgressDialogueInter = new CustomProgressDialogueInter(context, "Ad", "Please wait Ads is loading...", BGColor, Text_Color_main);
        customProgressDialogueInter.show();
        UnityAds.load(ac_Inter, new IUnityAdsLoadListener() { // from class: world.snacks.hub.Pizza.10
            @Override // com.unity3d.ads.IUnityAdsLoadListener
            public void onUnityAdsAdLoaded(String str) {
                IUnityAdsShowListener iUnityAdsShowListener = new IUnityAdsShowListener() { // from class: world.snacks.hub.Pizza.10.1
                    @Override // com.unity3d.ads.IUnityAdsShowListener
                    public void onUnityAdsShowClick(String str2) {
                        Log.e("UnityAdsExample i", "onUnityAdsShowClick: " + str2);
                    }

                    @Override // com.unity3d.ads.IUnityAdsShowListener
                    public void onUnityAdsShowComplete(String str2, UnityAds.UnityAdsShowCompletionState unityAdsShowCompletionState) {
                        Log.e("UnityAdsExample i", "onUnityAdsShowComplete: " + str2);
                    }

                    @Override // com.unity3d.ads.IUnityAdsShowListener
                    public void onUnityAdsShowFailure(String str2, UnityAds.UnityAdsShowError unityAdsShowError, String str3) {
                        try {
                            CustomProgressDialogueInter customProgressDialogueInter2 =customProgressDialogueInter;
                            if (customProgressDialogueInter2 != null && customProgressDialogueInter2.isShowing()) {
                               customProgressDialogueInter.dismiss();
                            }
                        } catch (Exception unused) {
                        }
                        if (!Pizza.tappx.equals("id")) {
                            StartAppAd.showAd(context);
                        }
                        Log.e("UnityAdsExample i", "Unity Ads failed to show ad for " + str2 + " with error: [" + unityAdsShowError + "] " + str3);
                    }

                    @Override // com.unity3d.ads.IUnityAdsShowListener
                    public void onUnityAdsShowStart(String str2) {
                        Log.e("UnityAdsExample i", "onUnityAdsShowStart: " + str2);
                    }
                };
                try {
                    CustomProgressDialogueInter customProgressDialogueInter2 =customProgressDialogueInter;
                    if (customProgressDialogueInter2 != null && customProgressDialogueInter2.isShowing()) {
                       customProgressDialogueInter.dismiss();
                    }
                } catch (Exception unused) {
                }
                try {
                    UnityAds.show((Activity) context, Pizza.ac_Inter, new UnityAdsShowOptions(), iUnityAdsShowListener);
                } catch (Exception unused2) {
                }
            }

            @Override // com.unity3d.ads.IUnityAdsLoadListener
            public void onUnityAdsFailedToLoad(String str, UnityAds.UnityAdsLoadError unityAdsLoadError, String str2) {
                Log.e("onUnityAdsFailedToLoad", "" + str2);
                try {
                    CustomProgressDialogueInter customProgressDialogueInter2 =customProgressDialogueInter;
                    if (customProgressDialogueInter2 != null && customProgressDialogueInter2.isShowing()) {
                       customProgressDialogueInter.dismiss();
                    }
                } catch (Exception unused) {
                }
                if (!Pizza.tappx.equals("id")) {
                    StartAppAd.showAd(context);
                }
            }
        });
    }

    public static void Native(Context context, RelativeLayout relativeLayout, int i2) {
        if (isNetworkConnected(context) && !show_ads.equals("0") && n_nooff.equals("1")) {
            if (b_n_ex.equals("1")) {
                Banner_FB(context, relativeLayout, i2);
            } else {
                Native_FB(context, relativeLayout, i2);
            }
        }
    }

    public static void Native_FB(final Context context, final RelativeLayout relativeLayout, final int i2) {
        if (i2 == 2) {
            if (fb_n.equals("id")) {
                Native_GL(context, relativeLayout, i2);
            } else if (only_fb_inter.equals("1")) {
                Native_GL(context, relativeLayout, i2);
            } else {
                final NativeAd nativeAd = new NativeAd(context, fb_n);
                NativeAdListener nativeAdListener = new NativeAdListener() { // from class: world.snacks.hub.Pizza.14
                    @Override // com.facebook.ads.AdListener
                    public void onAdClicked(Ad ad) {
                    }

                    @Override // com.facebook.ads.AdListener
                    public void onAdLoaded(Ad ad) {
                        try {
                            View render = NativeAdView.render(context, nativeAd, new NativeAdViewAttributes().setBackgroundColor(Pizza.BGColor).setTitleTextColor(Pizza.TitleTextColor).setDescriptionTextColor(Pizza.DescriptionTextColor).setButtonColor(Pizza.ButtonColor).setButtonTextColor(Pizza.ButtonTextColor).setButtonBorderColor(Pizza.ButtonBorderColor));
                            relativeLayout.setVisibility(View.VISIBLE);
                            relativeLayout.removeAllViews();
                            relativeLayout.addView(render, new RecyclerView.LayoutParams(-1, 800));
                            Pizza.Native_Show(Pizza.code_context.getPackageName());
                        } catch (Exception unused) {
                        }
                    }

                    @Override // com.facebook.ads.AdListener
                    public void onError(Ad ad, AdError adError) {
                        Pizza.Native_GL(context, relativeLayout, i2);
                    }

                    @Override // com.facebook.ads.AdListener
                    public void onLoggingImpression(Ad ad) {
                    }

                    @Override // com.facebook.ads.NativeAdListener
                    public void onMediaDownloaded(Ad ad) {
                    }
                };
                Native_Request(code_context.getPackageName());
                nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).withMediaCacheFlag(NativeAdBase.MediaCacheFlag.ALL).build());
            }
        } else if (fb_ns.equals("id")) {
            Native_GL(context, relativeLayout, i2);
        } else if (only_fb_inter.equals("1")) {
            Native_GL(context, relativeLayout, i2);
        } else {
            final NativeBannerAd nativeBannerAd = new NativeBannerAd(context, fb_ns);
            NativeAdListener nativeAdListener2 = new NativeAdListener() { // from class: world.snacks.hub.Pizza.15
                @Override // com.facebook.ads.AdListener
                public void onAdClicked(Ad ad) {
                }

                @Override // com.facebook.ads.AdListener
                public void onAdLoaded(Ad ad) {
                    try {
                        View render = NativeBannerAdView.render(context, nativeBannerAd, NativeBannerAdView.Type.HEIGHT_120, new NativeAdViewAttributes().setBackgroundColor(Pizza.BGColor).setTitleTextColor(Pizza.TitleTextColor).setDescriptionTextColor(Pizza.DescriptionTextColor).setButtonColor(Pizza.ButtonColor).setButtonTextColor(Pizza.ButtonTextColor).setButtonBorderColor(Pizza.ButtonBorderColor));
                        relativeLayout.setVisibility(View.VISIBLE);
                        relativeLayout.removeAllViews();
                        relativeLayout.addView(render);
                        Pizza.Native_Show(Pizza.code_context.getPackageName());
                    } catch (Exception unused) {
                    }
                }

                @Override // com.facebook.ads.AdListener
                public void onError(Ad ad, AdError adError) {
                    Pizza.Native_GL(context, relativeLayout, i2);
                }

                @Override // com.facebook.ads.AdListener
                public void onLoggingImpression(Ad ad) {
                }

                @Override // com.facebook.ads.NativeAdListener
                public void onMediaDownloaded(Ad ad) {
                }
            };
            Native_Request(code_context.getPackageName());
            nativeBannerAd.loadAd(nativeBannerAd.buildLoadAdConfig().withAdListener(nativeAdListener2).build());
        }
    }

    public static void Native_GL(final Context context, final RelativeLayout relativeLayout, final int i2) {
        String str;
        if (admob_n3.equals("id")) {
            Native_SA(context, relativeLayout, i2);
            return;
        }
        String str2 = Google_SetUp_List_Custom.get(gogole_native_id_count);
        if (str2.equals("H")) {
            if (google_native_id_exchange_H == 1) {
                str = admob_n1;
                google_native_id_exchange_H = 11;
            } else {
                str = admob_n11;
                google_native_id_exchange_H = 1;
            }
        } else if (str2.equals("M")) {
            if (google_native_id_exchange_M == 1) {
                str = admob_n2;
                google_native_id_exchange_M = 11;
            } else {
                str = admob_n22;
                google_native_id_exchange_M = 1;
            }
        } else if (google_native_id_exchange_L == 1) {
            str = admob_n3;
            google_native_id_exchange_L = 11;
        } else {
            str = admob_n33;
            google_native_id_exchange_L = 1;
        }
        new AdLoader.Builder(context, str).forNativeAd(new com.google.android.gms.ads.nativead.NativeAd.OnNativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(@NonNull com.google.android.gms.ads.nativead.NativeAd nativeAd) {
                Pizza.gogole_native_id_count = 0;
                NativeStyle build = new NativeStyle.Builder().build();
                TemplateView templateView = new TemplateView(context, i2);
                templateView.setStyles(build);
                templateView.setNativeAd(nativeAd);
                templateView.setVisibility(View.VISIBLE);
                try {
                    relativeLayout.removeAllViews();
                    relativeLayout.setVisibility(View.VISIBLE);
                    relativeLayout.addView(templateView);
                } catch (Exception unused) {
                }
            } // from class: world.snacks.hub.Pizza.17

        }).withAdListener(new com.google.android.gms.ads.AdListener() { // from class: world.snacks.hub.Pizza.16
            @Override // com.google.android.gms.ads.AdListener
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                if (Pizza.gogole_native_id_count + 1 == Pizza.Google_SetUp_List_Custom.size()) {
                    Pizza.gogole_native_id_count = 0;
                    Pizza.Native_SA(context, relativeLayout, i2);
                    return;
                }
                Pizza.gogole_native_id_count++;
                Pizza.Native_GL(context, relativeLayout, i2);
            }
        }).withNativeAdOptions(new NativeAdOptions.Builder().build()).build().loadAd(new AdRequest.Builder().build());
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    public static void Native_Request(String str) {
        if (SharPerf.getExtra4(code_context).equals("1")) {
            try {
                WebView webView = new WebView(code_context);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setLoadWithOverviewMode(true);
                webView.getSettings().setUseWideViewPort(true);
                webView.loadUrl("https://elveevocsy.click/ElveeVocsy/api/native.php?package_name=" + str + "&native_request");
            } catch (Exception unused) {
            }
        }
    }

    public static void Native_SA(Context context, RelativeLayout relativeLayout, int i2) {
        if (show_ads.equals("0")) {
            return;
        }
        if (i2 == 2) {
            if (!tappx.equals("id")) {
                try {
                    Mrec mrec = new Mrec(context);
                    relativeLayout.setVisibility(View.VISIBLE);
                    relativeLayout.removeAllViews();
                    relativeLayout.addView(mrec);
                    return;
                } catch (Exception unused) {
                    return;
                }
            }
            return;
        }
        Banner_SA(context, relativeLayout, 1);
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    public static void Native_Show(String str) {
        if (SharPerf.getExtra4(code_context).equals("1")) {
            try {
                WebView webView = new WebView(code_context);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setLoadWithOverviewMode(true);
                webView.getSettings().setUseWideViewPort(true);
                webView.loadUrl("https://elveevocsy.click/ElveeVocsy/api/native.php?package_name=" + str + "&native_show");
            } catch (Exception unused) {
            }
        }
    }

    public static void Pre_Interstial_Show(Context context) {
        if (for_Approval.equals("1")) {
            Interstial_Counted(context);
        } else {
            Interstial_Show(context);
        }
    }

    public static void Splash_Interstial(Dialog dialog, Context context) {
        try {
            if (isNetworkConnected(context)) {
                if (show_ads.equals("1")) {
                    if (splesh_ads.equals("1")) {
                        if (i_nooff.equals("0")) {
                            try {
                                handler_splesh_counter.removeCallbacks(runnable_splesh_counter);
                            } catch (Exception unused) {
                            }
                            if (dialog != null) {
                                try {
                                    if (dialog.isShowing()) {
                                        dialog.dismiss();
                                        return;
                                    }
                                    return;
                                } catch (Exception unused2) {
                                    return;
                                }
                            }
                            return;
                        }
                        Splash_Interstial_FB(dialog, context);
                        return;
                    }
                    try {
                        handler_splesh_counter.removeCallbacks(runnable_splesh_counter);
                    } catch (Exception unused3) {
                    }
                    Interstial_Load(context);
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } else if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            } else if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Exception unused4) {
        }
    }

    public static void Splash_Interstial_FB(final Dialog dialog, final Context context) {
        try {
            if (show_ads.equals("1")) {
                if (fb_i.equals("id")) {
                    Splash_Interstial_Google(dialog, context);
                    return;
                }
                final InterstitialAd interstitialAd = new InterstitialAd(context, "" + fb_i);
                InterstitialAdListener interstitialAdListener = new InterstitialAdListener() { // from class: world.snacks.hub.Pizza.5
                    @Override // com.facebook.ads.AdListener
                    public void onAdClicked(Ad ad) {
                    }

                    @Override // com.facebook.ads.AdListener
                    public void onAdLoaded(Ad ad) {
                        try {
                            Pizza.handler_splesh_counter.removeCallbacks(Pizza.runnable_splesh_counter);
                            if (!Pizza.Splesh_Timer.booleanValue()) {
                                interstitialAd.show();
                                Dialog dialog2 = dialog;
                                if (dialog2 != null && dialog2.isShowing()) {
                                    dialog.dismiss();
                                }
                            }
                        } catch (Exception unused) {
                        }
                    }

                    @Override // com.facebook.ads.AdListener
                    public void onError(Ad ad, AdError adError) {
                        Pizza.Splash_Interstial_Google(dialog, context);
                    }

                    @Override // com.facebook.ads.InterstitialAdListener
                    public void onInterstitialDismissed(Ad ad) {
                        Pizza.Interstial_Load(context);
                    }

                    @Override // com.facebook.ads.InterstitialAdListener
                    public void onInterstitialDisplayed(Ad ad) {
                        Pizza.Splash_Show(Pizza.code_context.getPackageName());
                    }

                    @Override // com.facebook.ads.AdListener
                    public void onLoggingImpression(Ad ad) {
                    }
                };
                Splash_Request(code_context.getPackageName());
                interstitialAd.loadAd(interstitialAd.buildLoadAdConfig().withAdListener(interstitialAdListener).build());
            }
        } catch (Exception unused) {
        }
    }

    public static void Splash_Interstial_Google(final Dialog dialog, final Context context) {
        String str;
        if (show_ads.equals("1")) {
            if (admob_i3.equals("id")) {
                Splash_Interstial_SA(dialog, context);
                return;
            }
            AdRequest build = new AdRequest.Builder().build();
            String str2 = Google_SetUp_List_Custom.get(gogole_splesh_inter_id_count);
            if (str2.equals("H")) {
                if (google_id_exchange_H == 1) {
                    str = admob_i1;
                    google_id_exchange_H = 11;
                } else {
                    str = admob_i11;
                    google_id_exchange_H = 1;
                }
            } else if (str2.equals("M")) {
                if (google_id_exchange_M == 1) {
                    str = admob_i2;
                    google_id_exchange_M = 11;
                } else {
                    str = admob_i22;
                    google_id_exchange_M = 1;
                }
            } else if (google_id_exchange_L == 1) {
                str = admob_i3;
                google_id_exchange_L = 11;
            } else {
                str = admob_i33;
                google_id_exchange_L = 1;
            }
            com.google.android.gms.ads.interstitial.InterstitialAd.load(context, str, build, new InterstitialAdLoadCallback() { // from class: world.snacks.hub.Pizza.6
                @Override // com.google.android.gms.ads.AdLoadCallback
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    if (Pizza.gogole_splesh_inter_id_count + 1 == Pizza.Google_SetUp_List_Custom.size()) {
                        Pizza.gogole_splesh_inter_id_count = 0;
                        Pizza.Splash_Interstial_SA(dialog, context);
                        return;
                    }
                    Pizza.gogole_splesh_inter_id_count++;
                    Pizza.Splash_Interstial_Google(dialog, context);
                }

                @Override // com.google.android.gms.ads.AdLoadCallback
                public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                    try {
                        try {
                            Pizza.gogole_splesh_inter_id_count = 0;
                            Pizza.handler_splesh_counter.removeCallbacks(Pizza.runnable_splesh_counter);
                        } catch (Exception unused) {
                            Dialog dialog2 = dialog;
                            if (dialog2 != null && dialog2.isShowing()) {
                                dialog.dismiss();
                            }
                        }
                        if (Pizza.Splesh_Timer.booleanValue()) {
                            return;
                        }
                        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() { // from class: world.snacks.hub.Pizza.6.1
                            @Override // com.google.android.gms.ads.FullScreenContentCallback
                            public void onAdClicked() {
                                super.onAdClicked();
                            }

                            @Override // com.google.android.gms.ads.FullScreenContentCallback
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                Pizza.Interstial_Load(context);
                            }

                            @Override // com.google.android.gms.ads.FullScreenContentCallback
                            public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                                super.onAdFailedToShowFullScreenContent(adError);
                                Splash_Interstial_SA(dialog, context);
                            }

                            @Override // com.google.android.gms.ads.FullScreenContentCallback
                            public void onAdImpression() {
                                super.onAdImpression();
                            }

                            @Override // com.google.android.gms.ads.FullScreenContentCallback
                            public void onAdShowedFullScreenContent() {
                                super.onAdShowedFullScreenContent();
                            }
                        });
                        interstitialAd.show((Activity) context);
                        Dialog dialog3 = dialog;
                        if (dialog3 != null && dialog3.isShowing()) {
                            dialog.dismiss();
                        }
                    } catch (Exception unused2) {
                    }
                }
            });
        }
    }

    public static void Splash_Interstial_SA(Dialog dialog, Context context) {
        if (ac_Inter.equals("id")) {
            if (!Splesh_Timer.booleanValue()) {
                try {
                    handler_splesh_counter.removeCallbacks(runnable_splesh_counter);
                    if (dialog != null) {
                        try {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        } catch (Exception unused) {
                        }
                    }
                    if (!tappx.equals("id")) {
                        StartAppAd.showAd(context);
                    } else {
                        return;
                    }
                } catch (Exception unused2) {
                    return;
                }
            }
            return;
        }
        Log.e("ac_Intersssssssssss", "" + ac_Inter);
        int i2 = 100;
        if (!UnityAds.isInitialized()) {
            i2 = 2500;
        }
        new Handler(Looper.getMainLooper()).postDelayed(new AnonymousClass7(context, dialog), i2);
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    public static void Splash_Request(String str) {
        if (SharPerf.getExtra4(code_context).equals("1")) {
            try {
                WebView webView = new WebView(code_context);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setLoadWithOverviewMode(true);
                webView.getSettings().setUseWideViewPort(true);
                webView.loadUrl("https://elveevocsy.click/ElveeVocsy/api/splash.php?package_name=" + str + "&splash_request");
            } catch (Exception unused) {
            }
        }
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    public static void Splash_Show(String str) {
        if (SharPerf.getExtra4(code_context).equals("1")) {
            try {
                WebView webView = new WebView(code_context);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setLoadWithOverviewMode(true);
                webView.getSettings().setUseWideViewPort(true);
                webView.loadUrl("https://elveevocsy.click/ElveeVocsy/api/splash.php?package_name=" + str + "&splash_show");
            } catch (Exception unused) {
            }
        }
    }

    @SuppressLint("Range")
    public void Splesh_Screen(final Context context, int i2, int i3, String str, String str2) {
        Text_Color_main = i2;
        try {
            splash_anim = str2;
            if (SharPerf.getFirst_ads_splesh(code_context) == 0) {
                SharPerf.setsplash_anim(code_context, splash_anim);
                SharPerf.setFirst_ads_splesh(code_context, 1);
            }
            splash_anim = SharPerf.getsplash_anim(code_context);
            if (isNetworkConnected(context)) {
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(1);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(ViewCompat.MEASURED_STATE_MASK));
                dialog.getWindow().setFlags(1024, 1024);
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                layoutParams.width = -1;
                layoutParams.height = -1;
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: world.snacks.hub.Pizza.1
                    @Override // android.content.DialogInterface.OnDismissListener
                    public void onDismiss(DialogInterface dialogInterface) {
                    }
                });
                DisplayMetrics displayMetrics = new DisplayMetrics();
                dialog.getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int i4 = displayMetrics.widthPixels;
                int i5 = displayMetrics.heightPixels;
                float[] r5 = new float[0];
                Color.colorToHSV(i2, r5);
                float[] fArr = new float[0];
                fArr = new float[]{0.0f, 0.0f, fArr[2] * 0.175f};
                int HSVToColor = Color.HSVToColor(fArr);
                final RelativeLayout relativeLayout = new RelativeLayout(context);
                ValueAnimator ofObject = ValueAnimator.ofObject(new ArgbEvaluator(), Integer.valueOf(context.getResources().getColor(R.color.black)), Integer.valueOf(HSVToColor));
                ofObject.setDuration(2000L);
                ofObject.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: world.snacks.hub.Pizza.2
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        relativeLayout.setBackgroundColor(((Integer) valueAnimator.getAnimatedValue()).intValue());
                    }
                });
                ofObject.start();
                relativeLayout.setGravity(81);
                dialog.addContentView(relativeLayout, new RelativeLayout.LayoutParams(-1, -1));
                ImageView imageView = new ImageView(context);
                imageView.setImageResource(i3);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                int i6 = i4 / 5;
                imageView.setPadding(i6, i6, i6, i6);
                imageView.setTranslationY(-(i5 / 7));
                dialog.addContentView(imageView, new RelativeLayout.LayoutParams(-1, -1));
                TextView textView = new TextView(context);
                textView.setText("" + str);
                textView.setTextColor(i2);
                textView.setTypeface(Typeface.DEFAULT_BOLD);
                textView.setTextSize((float) (i5 / (i4 / 9)));
                double d2 = i5;
                Double.isNaN(d2);
                textView.setTranslationY(-((int) (d2 / 5.5d)));
                textView.setGravity(81);
                dialog.addContentView(textView, new RelativeLayout.LayoutParams(-1, -1));
                ProgressBar progressBar = new ProgressBar(context);
                progressBar.getIndeterminateDrawable().setColorFilter(i2, PorterDuff.Mode.MULTIPLY);
                double d3 = i4;
                Double.isNaN(d3);
                int i7 = (int) (d3 / 2.5d);
                progressBar.setPadding(i7, i7, i7, i7);
                Double.isNaN(d2);
                progressBar.setTranslationY((float) (d2 / 2.5d));
                dialog.addContentView(progressBar, new RelativeLayout.LayoutParams(-1, -1));
                try {
                    dialog.show();
                } catch (Exception unused) {
                }
                dialog.getWindow().setAttributes(layoutParams);
                dialog.setCancelable(false);
                Splesh_Timer = Boolean.FALSE;
                handler_splesh_counter = new Handler();
                Runnable runnable = new Runnable() { // from class: world.snacks.hub.Pizza.3
                    @Override // java.lang.Runnable
                    public void run() {
                        Pizza.Splesh_Timer = Boolean.TRUE;
                        try {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        } catch (Exception unused2) {
                        }
                        Pizza.Interstial_Load(context);
                    }
                };
                runnable_splesh_counter = runnable;
                handler_splesh_counter.postDelayed(runnable, 15000L);
                splesh_callbck = new OnSpleshJsonCallBackListner() { // from class: world.snacks.hub.Pizza.4
                    @Override // world.snacks.hub.OnSpleshJsonCallBackListner
                    public void OnSpleshJsonDone() {
                        Log.e("cccc", "OnSpleshJsonDone");
                        try {
                            if (Pizza.show_ads == "0") {
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            } else {
                                Pizza.Splash_Interstial(dialog, context);
                            }
                        } catch (Exception unused2) {
                        }
                    }
                };
                new GetData().execute(new Void[0]);
                return;
            }
            error("No internet");
        } catch (Exception unused2) {
        }
    }

    public static void error(String str) {
        Log.e("json error = ", str);
        try {
            show_ads = SharPerf.get_show_ads(code_context);
            Exit_Pop_Setup = SharPerf.getExit_Pop(code_context);
            counter_ads = SharPerf.get_counter_ads(code_context);
            splesh_ads = SharPerf.get_splesh_ads(code_context);
            fb_b = SharPerf.get_fb_b(code_context);
            fb_mr = SharPerf.get_fb_mr(code_context);
            fb_i = SharPerf.get_fb_i(code_context);
            fb_n = SharPerf.get_fb_n(code_context);
            fb_ns = SharPerf.get_fb_ns(code_context);
            GL_Setup_Ads = SharPerf.getGL_Setup_Ads(code_context);
            admob_i1 = SharPerf.get_admob_i1(code_context);
            admob_i11 = SharPerf.get_admob_i11(code_context);
            admob_i2 = SharPerf.get_admob_i2(code_context);
            admob_i22 = SharPerf.get_admob_i22(code_context);
            admob_i3 = SharPerf.get_admob_i3(code_context);
            admob_i33 = SharPerf.get_admob_i33(code_context);
            admob_b1 = SharPerf.get_admob_b1(code_context);
            admob_b11 = SharPerf.get_admob_b11(code_context);
            admob_b2 = SharPerf.get_admob_b2(code_context);
            admob_b22 = SharPerf.get_admob_b22(code_context);
            admob_b3 = SharPerf.get_admob_b3(code_context);
            admob_b33 = SharPerf.get_admob_b33(code_context);
            admob_n1 = SharPerf.get_admob_n1(code_context);
            admob_n11 = SharPerf.get_admob_n11(code_context);
            admob_n2 = SharPerf.get_admob_n2(code_context);
            admob_n22 = SharPerf.get_admob_n22(code_context);
            admob_n3 = SharPerf.get_admob_n3(code_context);
            admob_n33 = SharPerf.get_admob_n33(code_context);
            admob_ao = SharPerf.get_admob_ao(code_context);
            ac_App = SharPerf.getAC_App(code_context);
            ac_Banner = SharPerf.getAC_Banner(code_context);
            ac_Inter = SharPerf.getAC_Inter(code_context);
            Ac_Reward = SharPerf.getAC_Reward(code_context);
            tappx = SharPerf.get_Tappx(code_context);
            i_nooff = SharPerf.geti_nooff(code_context);
            b_nooff = SharPerf.getb_nooff(code_context);
            n_nooff = SharPerf.getn_nooff(code_context);
            b_n_ex = SharPerf.getb_n_ex(code_context);
            increase_ads = SharPerf.getIncrease_Ads(code_context);
            for_Approval = SharPerf.getFor_Approval(code_context);
            ao_ads = SharPerf.getAO_Setup(code_context);
            Native_custom = SharPerf.getNative_custom(code_context);
            only_fb_inter = SharPerf.getonly_fb_inter(code_context);
            BGColor = Color.parseColor(SharPerf.getBGColor(code_context));
            TitleTextColor = Color.parseColor(SharPerf.getTitleTextColor(code_context));
            DescriptionTextColor = Color.parseColor(SharPerf.getDescriptionTextColor(code_context));
            ButtonColor = Color.parseColor(SharPerf.getButtonColor(code_context));
            ButtonTextColor = Color.parseColor(SharPerf.getButtonTextColor(code_context));
            ButtonBorderColor = Color.parseColor(SharPerf.getButtonBorderColor(code_context));
            Extra1 = SharPerf.getExtra1(code_context);
            Extra2 = SharPerf.getExtra2(code_context);
            Extra3 = SharPerf.getExtra3(code_context);
            Extra4 = SharPerf.getExtra4(code_context);
            Extra5 = SharPerf.getExtra5(code_context);
            Google_SetUp_List_Custom = new ArrayList<>();
            for (int i2 = 0; i2 < GL_Setup_Ads.length(); i2++) {
                ArrayList<String> arrayList = Google_SetUp_List_Custom;
                arrayList.add("" + GL_Setup_Ads.charAt(i2));
            }
            Loading_Data = 0;
        } catch (Exception unused) {
        }
    }

    public static boolean isNetworkConnected(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static void onDobuleBackPressed(Context context) {
        try {
            if (doubleBackToExitPressedOnce.booleanValue()) {
                System.exit(0);
            } else {
                Toast.makeText(context, "Press Back Again to Exit", Toast.LENGTH_SHORT).show();
                doubleBackToExitPressedOnce = Boolean.TRUE;
                new Handler().postDelayed(new Runnable() { // from class: world.snacks.hub.Pizza.27
                    @Override // java.lang.Runnable
                    public void run() {
                        Pizza.doubleBackToExitPressedOnce = Boolean.FALSE;
                    }
                }, 2000L);
            }
        } catch (Exception unused) {
        }
    }

    @SuppressLint({"SetTextI18n"})
    public void Exit_Popup_With_Ads_Native(final Context context) {
        try {
            @SuppressLint("ResourceType") final Dialog dialog = new Dialog(context, 16973834);
            dialog.requestWindowFeature(1);
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.without_ads_exit_layout);
            ((TextView) dialog.findViewById(R.id.title)).setText("Do You Want To Exit ?");
            Button button = (Button) dialog.findViewById(R.id.Btn_Yes);
            Button button2 = (Button) dialog.findViewById(R.id.Btn_Rate);
            Button button3 = (Button) dialog.findViewById(R.id.Btn_No);
            Native(context, (RelativeLayout) dialog.findViewById(R.id.banner), 2);
            button.setText("Yes");
            button2.setText("Rate Us");
            button3.setText("No");
            button.setOnClickListener(new View.OnClickListener() { // from class: world.snacks.hub.Pizza.24
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    try {
                        dialog.dismiss();
                        System.exit(0);
                    } catch (Exception unused) {
                    }
                }
            });
            button2.setOnClickListener(new View.OnClickListener() { // from class: world.snacks.hub.Pizza.25
                @SuppressLint("WrongConstant")
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + Pizza.code_context.getPackageName()));
                    intent.addFlags(1207959552);
                    try {
                        context.startActivity(intent);
                    } catch (ActivityNotFoundException unused) {
                        Context context2 = context;
                        context2.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://play.google.com/OneForAll/apps/details?id=" + Pizza.code_context.getPackageName())));
                    }
                }
            });
            button3.setOnClickListener(new View.OnClickListener() { // from class: world.snacks.hub.Pizza.26
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

    @SuppressLint({"SetTextI18n"})
    public void Exit_Popup_Without_Ads(final Context context) {
        try {
            @SuppressLint("ResourceType") final Dialog dialog = new Dialog(context, 16973834);
            dialog.requestWindowFeature(1);
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.without_ads_exit_layout);
            ((TextView) dialog.findViewById(R.id.title)).setText("Do You Want To Exit ?");
            Button button = (Button) dialog.findViewById(R.id.Btn_Yes);
            Button button2 = (Button) dialog.findViewById(R.id.Btn_Rate);
            Button button3 = (Button) dialog.findViewById(R.id.Btn_No);
            Native(context, (RelativeLayout) dialog.findViewById(R.id.banner), 1);
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
                    Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + Pizza.code_context.getPackageName()));
                    intent.addFlags(1207959552);
                    try {
                        context.startActivity(intent);
                    } catch (ActivityNotFoundException unused) {
                        Context context2 = context;
                        context2.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://play.google.com/OneForAll/apps/details?id=" + Pizza.code_context.getPackageName())));
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

    public void Exit_With_Ads_Native(Context context) {
        try {
            if (show_ads.equals("0")) {
                Exit_Popup_Without_Ads(context);
            } else if (Exit_Pop_Setup.equals("0")) {
                onDobuleBackPressed(context);
            } else if (Exit_Pop_Setup.equals("1")) {
                Exit_Popup_Without_Ads(context);
            } else {
                Exit_Popup_With_Ads_Native(context);
            }
        } catch (Exception unused) {
        }
    }

    public Pizza(Context context, OnJsonCallBackListner onJsonCallBackListner, String str, String str2) {
        Link_URL = str2;
        f9816b = onJsonCallBackListner;
        code_context = context;
        show_ads = "0";
        splesh_ads = "0";
        fb_b = "id";
        fb_mr = "id";
        fb_i = "id";
        fb_n = "id";
        fb_ns = "id";
        admob_i1 = "id";
        admob_i11 = "id";
        admob_i2 = "id";
        admob_i22 = "id";
        admob_i3 = "id";
        admob_i33 = "id";
        admob_b1 = "id";
        admob_b11 = "id";
        admob_b2 = "id";
        admob_b22 = "id";
        admob_b3 = "id";
        admob_b33 = "id";
        admob_n1 = "id";
        admob_n11 = "id";
        admob_n2 = "id";
        admob_n22 = "id";
        admob_n3 = "id";
        admob_n33 = "id";
        admob_ao = "id";
        ac_App = "id";
        ac_Inter = "id";
        ac_Banner = "id";
        Ac_Reward = "id";
        tappx = "id";
        splash_anim = "0";
        if (SharPerf.get_dwnld(context) == 0) {
            Count_Download(code_context.getPackageName());
            SharPerf.set_dwnld(code_context, 1);
        }
        if (SharPerf.getFirst_ads(code_context) == 0) {
            SharPerf.set_show_ads(code_context, show_ads);
            SharPerf.set_counter_ads(code_context, counter_ads);
            SharPerf.set_splesh_ads(code_context, splesh_ads);
            SharPerf.set_fb_b(code_context, fb_b);
            SharPerf.set_fb_mr(code_context, fb_mr);
            SharPerf.set_fb_i(code_context, fb_i);
            SharPerf.set_fb_n(code_context, fb_n);
            SharPerf.set_fb_ns(code_context, fb_ns);
            SharPerf.set_admob_i1(code_context, admob_i1);
            SharPerf.set_admob_i11(code_context, admob_i11);
            SharPerf.set_admob_i2(code_context, admob_i2);
            SharPerf.set_admob_i2(code_context, admob_i22);
            SharPerf.set_admob_i3(code_context, admob_i3);
            SharPerf.set_admob_i33(code_context, admob_i33);
            SharPerf.set_admob_b1(code_context, admob_b1);
            SharPerf.set_admob_b11(code_context, admob_b11);
            SharPerf.set_admob_b2(code_context, admob_b2);
            SharPerf.set_admob_b22(code_context, admob_b22);
            SharPerf.set_admob_b3(code_context, admob_b3);
            SharPerf.set_admob_b33(code_context, admob_b33);
            SharPerf.set_admob_n1(code_context, admob_n1);
            SharPerf.set_admob_n11(code_context, admob_n11);
            SharPerf.set_admob_n2(code_context, admob_n2);
            SharPerf.set_admob_n22(code_context, admob_n22);
            SharPerf.set_admob_n3(code_context, admob_n3);
            SharPerf.set_admob_n33(code_context, admob_n33);
            SharPerf.set_Tappx(code_context, tappx);
            SharPerf.set_admob_ao(code_context, admob_ao);
            SharPerf.setAC_App(code_context, ac_App);
            SharPerf.setAC_Inter(code_context, ac_Inter);
            SharPerf.setAC_Banner(code_context, ac_Banner);
            SharPerf.setAC_Reward(code_context, Ac_Reward);
            SharPerf.setsplash_anim(code_context, splash_anim);
            SharPerf.setIncrease_Ads(code_context, increase_ads);
            SharPerf.setFor_Approval(code_context, for_Approval);
            SharPerf.setFirst_ads(code_context, 1);
        }
    }
}
