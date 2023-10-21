package com.demo.mydemo;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
public class App extends Application {
    public static final String TAG = "App";
    private static Context context;
    private static App mInstance;
    public static DataModel adsData = null;
    public static final String AdsUrl = "https://mayurparmar2.github.io/AlarmDemo/FlightTracker.json";
    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        mInstance = this;
        new UpdateTask().execute();
    }

    public static synchronized App getInstance() {
        App appApplication;
        synchronized (App.class) {
            appApplication = mInstance;
        }
        return appApplication;
    }

    public DataModel readJsonFromUrl() {
        DataModel dataModel= new DataModel();
        try {
            URL url = new URL(AdsUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            int responseCode = connection.getResponseCode();
            Log.e("MYTAG", "ErrorNo: doInBackground:" + responseCode);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            Log.e(TAG, "readJsonFromUrl: "+response.toString());
            if (responseCode != 200) {
                return null;
            }
            connection.disconnect();
            try {
                JSONObject jsonObject = new JSONObject(response.toString());
                JSONArray adsArray = jsonObject.getJSONArray("ads");
                for (int i = 0; i < adsArray.length(); i++) {
                    JSONObject adObject = adsArray.getJSONObject(i);
                    dataModel.setEnable(adObject.getBoolean("isEnable"));
                    dataModel.setCounter(adObject.getInt("Counter"));
                    dataModel.setAd_platform(adObject.getString("ad_platform"));
                    dataModel.setAppId(adObject.getString("App_id"));
                    dataModel.setBanner(adObject.getString("Banner"));
                    dataModel.setInterstitial(adObject.getString("Interstitial"));
                    dataModel.setRewardedInterstitial(adObject.getString("Rewarded_interstitial"));
                    dataModel.setRewarded(adObject.getString("Rewarded"));
                    dataModel.setNativeAdvanced(adObject.getString("Native_advanced"));


                    Log.e(TAG, "Counter: " + dataModel.getCounter());
                    Log.e(TAG, "App_id: " + dataModel.getAppId());
                    Log.e(TAG, "ad_platform: " + dataModel.getAdPlatform());
                    Log.e(TAG, "isEnable: " + dataModel.getEnable());
                    Log.e(TAG, "Banner: " + dataModel.getBanner());
                    Log.e(TAG, "Interstitial: " + dataModel.getInterstitial());
                    Log.e(TAG, "Rewarded Interstitial: " + dataModel.getRewardedInterstitial());
                    Log.e(TAG, "Rewarded: " + dataModel.getRewarded());
                    Log.e(TAG, "Native Advanced: " + dataModel.getNativeAdvanced());
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("MYTAG", "ErrorNo: onResponse:" +e.getMessage());
            }
        }catch (Exception e){
            Log.d(TAG, "readJsonFromUrl: "+e);

        }
        return dataModel;
    }
    private class UpdateTask extends AsyncTask<String, String,String> {
        protected String doInBackground(String... urls) {
            adsData=readJsonFromUrl();
            return null;
        }
    }
}