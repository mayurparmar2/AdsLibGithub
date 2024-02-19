package com.example.admob2;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.example.admob2.R;

public class AppUtil {
    public static void shareApp(Context context) {
        try {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");

            intent.putExtra("android.intent.extra.SUBJECT", R.string.app_name);
            intent.putExtra("android.intent.extra.TEXT", "\nLet me recommend you this application\n\nhttps://play.google.com/store/apps/details?id=" + context.getPackageName() + "\n\n");
            context.startActivity(Intent.createChooser(intent, "choose one"));
        } catch (Exception unused) {
        }
    }

    public static void moreApp(Context context, String str) {
        context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/developer?id=" + str)));
    }

    public static void rateApp(Context context) {
        try {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + context.getPackageName())));
        } catch (ActivityNotFoundException unused) {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

    public static void privacyPolicy(Context context, String str) {
        if (!str.startsWith("http://") && !str.startsWith("https://")) {
            str = "http://" + str;
        }
        try {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No application can handle this request. Please install a webbrowser", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
