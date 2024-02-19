package com.example.admob2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;


public class GoogleNativeAdAdapter extends RecyclerViewAdapterWrapper {
    private static final int DEFAULT_AD_ITEM_INTERVAL = 3;
    private static final int TYPE_FB_NATIVE_ADS = 900;
    static Context mContext;
    private final Param mParam;
    LinearLayout frameLayout;
    private NativeAd nativeAdmain;

    private GoogleNativeAdAdapter(Param param) {
        super(param.adapter);
        this.mParam = param;
        assertConfig();
        setSpanAds();
    }

    private void assertConfig() {
        if (this.mParam.gridLayoutManager != null) {
            int spanCount = this.mParam.gridLayoutManager.getSpanCount();
            if (this.mParam.adItemInterval % spanCount != 0) {
                throw new IllegalArgumentException(String.format("The adItemInterval (%d) is not divisible by number of columns in GridLayoutManager (%d)", Integer.valueOf(this.mParam.adItemInterval), Integer.valueOf(spanCount)));
            }
        }
    }

    private int convertAdPosition2OrgPosition(int i) {
        return i - ((i + 1) / (this.mParam.adItemInterval + 1));
    }

    @Override

    public int getItemCount() {
        int itemCount = super.getItemCount();
        return itemCount + (itemCount / this.mParam.adItemInterval);
    }

    @Override

    public int getItemViewType(int i) {
        if (isAdPosition(i)) {
            return 900;
        }
        return super.getItemViewType(convertAdPosition2OrgPosition(i));
    }


    public boolean isAdPosition(int i) {
        return (i + 1) % (this.mParam.adItemInterval + 1) == 0;
    }

    private void onBindAdViewHolder(RecyclerView.ViewHolder viewHolder) {
        AdViewHolder adViewHolder = (AdViewHolder) viewHolder;
        if (this.mParam.forceReloadAdOnBind || !adViewHolder.loaded) {
            refreshAd(this.mParam.facebookPlacementId, this.mParam.itemContainerId, adViewHolder.frameLayout);
        }
    }

    @Override

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (getItemViewType(i) == 900) {
            onBindAdViewHolder(viewHolder);
        } else {
            super.onBindViewHolder(viewHolder, convertAdPosition2OrgPosition(i));
        }
    }

    private RecyclerView.ViewHolder onCreateAdViewHolder(ViewGroup viewGroup) {
        return new AdViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(this.mParam.itemContainerLayoutRes, viewGroup, false));
    }

    @Override

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == 900) {
            return onCreateAdViewHolder(viewGroup);
        }
        return super.onCreateViewHolder(viewGroup, i);
    }

    private void setSpanAds() {
        if (this.mParam.gridLayoutManager == null) {
            return;
        }
        final GridLayoutManager.SpanSizeLookup spanSizeLookup = this.mParam.gridLayoutManager.getSpanSizeLookup();
        this.mParam.gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i) {
                if (GoogleNativeAdAdapter.this.isAdPosition(i)) {
                    return spanSizeLookup.getSpanSize(i);
                }
                return 1;
            }
        });
    }

    private void refreshAd(String str, int i, final LinearLayout linearLayout) {
        AdLoader.Builder builder = new AdLoader.Builder(mContext, str);
        builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(NativeAd nativeAd) {
                Log.e("ContentValues", "onNativeAdLoaded: " + nativeAd);
                if (GoogleNativeAdAdapter.this.nativeAdmain != null) {
                    GoogleNativeAdAdapter.this.nativeAdmain.destroy();
                }
                GoogleNativeAdAdapter.this.nativeAdmain = nativeAd;
                NativeAdView nativeAdView = (NativeAdView) ((LayoutInflater) GoogleNativeAdAdapter.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.ad_unified, (ViewGroup) null);
                GoogleNativeAdAdapter.this.populateNativeAdView(nativeAd, nativeAdView);
                linearLayout.setVisibility(View.VISIBLE);
                linearLayout.removeAllViews();
                linearLayout.addView(nativeAdView);
            }
        });
        builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                linearLayout.setVisibility(View.GONE);
            }
        }).build().loadAd(new AdRequest.Builder().build());
    }


    public void populateNativeAdView(NativeAd nativeAd, NativeAdView nativeAdView) {
        nativeAdView.setMediaView((MediaView) nativeAdView.findViewById(R.id.ad_media));
        nativeAdView.setHeadlineView(nativeAdView.findViewById(R.id.ad_headline));
        nativeAdView.setBodyView(nativeAdView.findViewById(R.id.ad_body));
        nativeAdView.setCallToActionView(nativeAdView.findViewById(R.id.ad_call_to_action));
        nativeAdView.setIconView(nativeAdView.findViewById(R.id.ad_app_icon));
        nativeAdView.setPriceView(nativeAdView.findViewById(R.id.ad_price));
        nativeAdView.setStarRatingView(nativeAdView.findViewById(R.id.ad_stars));
        nativeAdView.setStoreView(nativeAdView.findViewById(R.id.ad_store));
        nativeAdView.setAdvertiserView(nativeAdView.findViewById(R.id.ad_advertiser));
        ((TextView) nativeAdView.getHeadlineView()).setText(nativeAd.getHeadline());
        nativeAdView.getMediaView().setMediaContent(nativeAd.getMediaContent());
        if (nativeAd.getBody() == null) {
            nativeAdView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            nativeAdView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) nativeAdView.getBodyView()).setText(nativeAd.getBody());
        }
        if (nativeAd.getCallToAction() == null) {
            nativeAdView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            nativeAdView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) nativeAdView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }
        if (nativeAd.getIcon() == null) {
            nativeAdView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) nativeAdView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
            nativeAdView.getIconView().setVisibility(View.VISIBLE);
        }
        if (nativeAd.getPrice() == null) {
            nativeAdView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            nativeAdView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) nativeAdView.getPriceView()).setText(nativeAd.getPrice());
        }
        if (nativeAd.getStore() == null) {
            nativeAdView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            nativeAdView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) nativeAdView.getStoreView()).setText(nativeAd.getStore());
        }
        if (nativeAd.getStarRating() == null) {
            nativeAdView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) nativeAdView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
            nativeAdView.getStarRatingView().setVisibility(View.VISIBLE);
        }
        if (nativeAd.getAdvertiser() == null) {
            nativeAdView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) nativeAdView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            nativeAdView.getAdvertiserView().setVisibility(View.VISIBLE);
        }
        nativeAdView.setNativeAd(nativeAd);
    }


    public static class Param {
        int adItemInterval;
        RecyclerView.Adapter adapter;
        String facebookPlacementId;
        boolean forceReloadAdOnBind;
        GridLayoutManager gridLayoutManager;
        int itemContainerId;
        int itemContainerLayoutRes;

        private Param() {
        }
    }


    public static class Builder {
        private final Param mParam;

        private Builder(Param param) {
            this.mParam = param;
        }

        public static Builder with(Context context, String str, RecyclerView.Adapter adapter) {
            GoogleNativeAdAdapter.mContext = context;
            MobileAds.initialize(GoogleNativeAdAdapter.mContext, new OnInitializationCompleteListener() {
                @Override

                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });
            Param param = new Param();
            param.facebookPlacementId = str;
            param.adapter = adapter;
            param.adItemInterval = 3;
            param.itemContainerLayoutRes = R.layout.item_google_native_ad_outline;
            param.itemContainerId = R.id.ad_container;
            param.forceReloadAdOnBind = true;
            return new Builder(param);
        }

        public Builder adItemInterval(int i) {
            this.mParam.adItemInterval = i;
            return this;
        }

        public Builder adLayout(int i, int i2) {
            this.mParam.itemContainerLayoutRes = i;
            this.mParam.itemContainerId = i2;
            return this;
        }

        public GoogleNativeAdAdapter build() {
            return new GoogleNativeAdAdapter(this.mParam);
        }

        public Builder enableSpanRow(GridLayoutManager gridLayoutManager) {
            this.mParam.gridLayoutManager = gridLayoutManager;
            return this;
        }

        public Builder forceReloadAdOnBind(boolean z) {
            this.mParam.forceReloadAdOnBind = z;
            return this;
        }
    }


    public static class AdViewHolder extends RecyclerView.ViewHolder {
        LinearLayout frameLayout;
        boolean loaded;

        AdViewHolder(View view) {
            super(view);
            this.loaded = false;
            this.frameLayout = (LinearLayout) view.findViewById(R.id.ad_container);
        }
    }
}
