package world.snacks.hub;

import android.content.Context;

/* loaded from: classes2.dex */
public class SharPerf {
    private static final String AC_App = "AC_App";
    private static final String AC_Banner = "AC_Banner";
    private static final String AC_Inter = "AC_Inter";
    private static final String AC_Reward = "AC_Reward";
    private static final String AO_Setup = "ao_ads";
    private static final String BGColor = "BGColor";
    private static final String ButtonBorderColor = "ButtonBorderColor";
    private static final String ButtonColor = "ButtonColor";
    private static final String ButtonTextColor = "ButtonTextColor";
    private static final String Count_Ads = "Count_Ads";
    private static final String DescriptionTextColor = "DescriptionTextColor";
    private static final String Exit_Pop = "Exit_Pop";
    private static final String Extra1 = "Extra1";
    private static final String Extra2 = "Extra2";
    private static final String Extra3 = "Extra3";
    private static final String Extra4 = "Extra4";
    private static final String Extra5 = "Extra5";
    private static final String First_ads = "First_ads";
    private static final String First_ads_splesh = "First_ads_splesh";
    private static final String For_Approval = "For_Approval";
    private static final String GL_Setup_Ads = "GL_Setup_Ads";
    private static final String Increase_Ads = "Increase_Ads";
    private static final String Native_custom = "Native_custom";
    private static final String Tappx = "Tappx";
    private static final String TitleTextColor = "TitleTextColor";
    public static String admob_ao = "admob_ao";
    public static String admob_b1 = "admob_b1";
    public static String admob_b11 = "admob_b11";
    public static String admob_b2 = "admob_b2";
    public static String admob_b22 = "admob_b22";
    public static String admob_b3 = "admob_b3";
    public static String admob_b33 = "admob_b33";
    public static String admob_i1 = "admob_i1";
    public static String admob_i11 = "admob_i11";
    public static String admob_i2 = "admob_i2";
    public static String admob_i22 = "admob_i22";
    public static String admob_i3 = "admob_i3";
    public static String admob_i33 = "admob_i33";
    public static String admob_n1 = "admob_n1";
    public static String admob_n11 = "admob_n11";
    public static String admob_n2 = "admob_n2";
    public static String admob_n22 = "admob_n22";
    public static String admob_n3 = "admob_n3";
    public static String admob_n33 = "admob_n33";
    private static final String b_n_ex = "b_n_ex";
    private static final String b_nooff = "b_nooff";
    public static String counter_ads = "counter_ads";
    public static String dwnld = "dwnld";
    public static String fb_b = "fb_b";
    public static String fb_i = "fb_i";
    public static String fb_mr = "fb_mr";
    public static String fb_n = "fb_n";
    public static String fb_ns = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID";
    private static final String i_nooff = "i_nooff";
    private static final String n_nooff = "n_nooff";
    private static final String only_fb_inter = "only_fb_inter";
    public static String show_ads = "show_ads";
    private static final String splash_anim = "splash_anim";
    public static String splesh_ads = "splesh_ads";

    public static String getAC_App(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(AC_App, "");
    }

    public static String getAC_Banner(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(AC_Banner, "");
    }

    public static String getAC_Inter(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(AC_Inter, "");
    }

    public static String getAC_Reward(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(AC_Reward, "");
    }

    public static String getAO_Setup(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(AO_Setup, "1");
    }

    public static String getBGColor(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(BGColor, "#292929");
    }

    public static String getButtonBorderColor(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(ButtonBorderColor, "#ffffff");
    }

    public static String getButtonColor(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(ButtonColor, "#474747");
    }

    public static String getButtonTextColor(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(ButtonTextColor, "#ffffff");
    }

    public static int getCount_Ads(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getInt(Count_Ads, 1);
    }

    public static String getDescriptionTextColor(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(DescriptionTextColor, "#b3b3b3");
    }

    public static String getExit_Pop(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(Exit_Pop, "0");
    }

    public static String getExtra1(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(Extra1, "0");
    }

    public static String getExtra2(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(Extra2, "0");
    }

    public static String getExtra3(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(Extra3, "0");
    }

    public static String getExtra4(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(Extra4, "0");
    }

    public static String getExtra5(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(Extra5, "0");
    }

    public static int getFirst_ads(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getInt(First_ads, 0);
    }

    public static int getFirst_ads_splesh(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getInt(First_ads_splesh, 0);
    }

    public static String getFor_Approval(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(For_Approval, "0");
    }

    public static String getGL_Setup_Ads(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(GL_Setup_Ads, "L");
    }

    public static String getIncrease_Ads(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(Increase_Ads, "0");
    }

    public static String getNative_custom(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(Native_custom, "0");
    }

    public static String getTitleTextColor(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(TitleTextColor, "#ffffff");
    }

    public static String get_Tappx(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(Tappx, "");
    }

    public static String get_admob_ao(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(admob_ao, Pizza.admob_ao);
    }

    public static String get_admob_b1(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(admob_b1, Pizza.admob_b1);
    }

    public static String get_admob_b11(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(admob_b11, Pizza.admob_b11);
    }

    public static String get_admob_b2(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(admob_b2, Pizza.admob_b2);
    }

    public static String get_admob_b22(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(admob_b22, Pizza.admob_b22);
    }

    public static String get_admob_b3(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(admob_b3, Pizza.admob_b3);
    }

    public static String get_admob_b33(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(admob_b33, Pizza.admob_b33);
    }

    public static String get_admob_i1(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(admob_i1, Pizza.admob_i1);
    }

    public static String get_admob_i11(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(admob_i11, Pizza.admob_i11);
    }

    public static String get_admob_i2(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(admob_i2, Pizza.admob_i2);
    }

    public static String get_admob_i22(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(admob_i22, Pizza.admob_i22);
    }

    public static String get_admob_i3(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(admob_i3, Pizza.admob_i3);
    }

    public static String get_admob_i33(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(admob_i33, Pizza.admob_i33);
    }

    public static String get_admob_n1(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(admob_n1, Pizza.admob_n1);
    }

    public static String get_admob_n11(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(admob_n11, Pizza.admob_n11);
    }

    public static String get_admob_n2(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(admob_n2, Pizza.admob_n2);
    }

    public static String get_admob_n22(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(admob_n22, Pizza.admob_n22);
    }

    public static String get_admob_n3(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(admob_n3, Pizza.admob_n3);
    }

    public static String get_admob_n33(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(admob_n33, Pizza.admob_n33);
    }

    public static String get_counter_ads(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(counter_ads, "3");
    }

    public static int get_dwnld(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getInt(dwnld, 0);
    }

    public static String get_fb_b(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(fb_b, Pizza.fb_b);
    }

    public static String get_fb_i(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(fb_i, Pizza.fb_i);
    }

    public static String get_fb_mr(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(fb_mr, Pizza.fb_mr);
    }

    public static String get_fb_n(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(fb_n, Pizza.fb_n);
    }

    public static String get_fb_ns(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(fb_ns, Pizza.fb_ns);
    }

    public static String get_show_ads(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(show_ads, "1");
    }

    public static String get_splesh_ads(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(splesh_ads, "3");
    }

    public static String getb_n_ex(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(b_n_ex, "0");
    }

    public static String getb_nooff(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(b_nooff, "1");
    }

    public static String geti_nooff(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(i_nooff, "1");
    }

    public static String getn_nooff(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(n_nooff, "1");
    }

    public static String getonly_fb_inter(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(only_fb_inter, "0");
    }

    public static String getsplash_anim(Context context) {
        return context.getSharedPreferences(context.getPackageName(), 0).getString(splash_anim, "0");
    }

    public static void setAC_App(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(AC_App, str).commit();
    }

    public static void setAC_Banner(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(AC_Banner, str).commit();
    }

    public static void setAC_Inter(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(AC_Inter, str).commit();
    }

    public static void setAC_Reward(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(AC_Reward, str).commit();
    }

    public static void setAO_Setup(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(AO_Setup, str).commit();
    }

    public static void setBGColor(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(BGColor, str).commit();
    }

    public static void setButtonBorderColor(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(ButtonBorderColor, str).commit();
    }

    public static void setButtonColor(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(ButtonColor, str).commit();
    }

    public static void setButtonTextColor(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(ButtonTextColor, str).commit();
    }

    public static void setCount_Ads(Context context, int i2) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putInt(Count_Ads, i2).commit();
    }

    public static void setDescriptionTextColor(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(DescriptionTextColor, str).commit();
    }

    public static void setExit_Pop(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(Exit_Pop, str).commit();
    }

    public static void setExtra1(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(Extra1, str).commit();
    }

    public static void setExtra2(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(Extra2, str).commit();
    }

    public static void setExtra3(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(Extra3, str).commit();
    }

    public static void setExtra4(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(Extra4, str).commit();
    }

    public static void setExtra5(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(Extra5, str).commit();
    }

    public static void setFirst_ads(Context context, int i2) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putInt(First_ads, i2).commit();
    }

    public static void setFirst_ads_splesh(Context context, int i2) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putInt(First_ads_splesh, i2).commit();
    }

    public static void setFor_Approval(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(For_Approval, str).commit();
    }

    public static void setGL_Setup_Ads(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(GL_Setup_Ads, str).commit();
    }

    public static void setIncrease_Ads(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(Increase_Ads, str).commit();
    }

    public static void setNative_custom(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(Native_custom, str).commit();
    }

    public static void setTitleTextColor(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(TitleTextColor, str).commit();
    }

    public static void set_Tappx(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(Tappx, str).commit();
    }

    public static void set_admob_ao(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(admob_ao, str).commit();
    }

    public static void set_admob_b1(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(admob_b1, str).commit();
    }

    public static void set_admob_b11(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(admob_b11, str).commit();
    }

    public static void set_admob_b2(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(admob_b2, str).commit();
    }

    public static void set_admob_b22(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(admob_b22, str).commit();
    }

    public static void set_admob_b3(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(admob_b3, str).commit();
    }

    public static void set_admob_b33(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(admob_b33, str).commit();
    }

    public static void set_admob_i1(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(admob_i1, str).commit();
    }

    public static void set_admob_i11(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(admob_i11, str).commit();
    }

    public static void set_admob_i2(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(admob_i2, str).commit();
    }

    public static void set_admob_i22(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(admob_i22, str).commit();
    }

    public static void set_admob_i3(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(admob_i3, str).commit();
    }

    public static void set_admob_i33(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(admob_i33, str).commit();
    }

    public static void set_admob_n1(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(admob_n1, str).commit();
    }

    public static void set_admob_n11(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(admob_n11, str).commit();
    }

    public static void set_admob_n2(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(admob_n2, str).commit();
    }

    public static void set_admob_n22(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(admob_n22, str).commit();
    }

    public static void set_admob_n3(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(admob_n3, str).commit();
    }

    public static void set_admob_n33(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(admob_n33, str).commit();
    }

    public static void set_counter_ads(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(counter_ads, str).commit();
    }

    public static void set_dwnld(Context context, Integer num) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putInt(dwnld, num.intValue()).commit();
    }

    public static void set_fb_b(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(fb_b, str).commit();
    }

    public static void set_fb_i(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(fb_i, str).commit();
    }

    public static void set_fb_mr(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(fb_mr, str).commit();
    }

    public static void set_fb_n(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(fb_n, str).commit();
    }

    public static void set_fb_ns(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(fb_ns, str).commit();
    }

    public static void set_show_ads(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(show_ads, str).commit();
    }

    public static void set_splesh_ads(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(splesh_ads, str).commit();
    }

    public static void setb_n_ex(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(b_n_ex, str).commit();
    }

    public static void setb_nooff(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(b_nooff, str).commit();
    }

    public static void seti_nooff(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(i_nooff, str).commit();
    }

    public static void setn_nooff(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(n_nooff, str).commit();
    }

    public static void setonly_fb_inter(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(only_fb_inter, str).commit();
    }

    public static void setsplash_anim(Context context, String str) {
        context.getSharedPreferences(context.getPackageName(), 0).edit().putString(splash_anim, str).commit();
    }
}
