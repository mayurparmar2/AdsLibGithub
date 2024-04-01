package com.demo.adslib;


public class DataModel {
    private String banner = "/6499/example/banner";
    private String interstitial = "/6499/example/interstitial";
    private String rewardedInterstitial;
    private String rewarded;
    private String nativeAdvanced;
    private String appId;
    private Boolean isEnable = false;
    private int counter;
    private String ad_platform;

    public DataModel(String banner, String interstitial, String rewardedInterstitial, String rewarded, String nativeAdvanced, String appId, Boolean isEnable, int counter, String ad_platform) {
        this.banner = banner;
        this.interstitial = interstitial;
        this.rewardedInterstitial = rewardedInterstitial;
        this.rewarded = rewarded;
        this.nativeAdvanced = nativeAdvanced;
        this.appId = appId;
        this.isEnable = isEnable;
        this.counter = counter;
        this.ad_platform = ad_platform;
    }

    public DataModel() {
    }

    // Getters and setters

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getInterstitial() {
        return interstitial;
    }

    public void setInterstitial(String interstitial) {
        this.interstitial = interstitial;
    }

    public String getRewardedInterstitial() {
        return rewardedInterstitial;
    }

    public void setRewardedInterstitial(String rewardedInterstitial) {
        this.rewardedInterstitial = rewardedInterstitial;
    }

    public String getRewarded() {
        return rewarded;
    }

    public void setRewarded(String rewarded) {
        this.rewarded = rewarded;
    }

    public String getNativeAdvanced() {
        return nativeAdvanced;
    }

    public void setNativeAdvanced(String nativeAdvanced) {
        this.nativeAdvanced = nativeAdvanced;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Boolean getEnable() {
        return isEnable;
    }

    public void setEnable(Boolean enable) {
        isEnable = enable;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public String getAdPlatform() {
        return ad_platform;
    }

    public void setAd_platform(String ad_platform) {
        this.ad_platform = ad_platform;
    }
}