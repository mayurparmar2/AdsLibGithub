package world.snacks.hub;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

/* loaded from: classes2.dex */
public class TemplateView extends FrameLayout {
    private static final String MEDIUM_TEMPLATE = "medium_template";
    private static final String SMALL_TEMPLATE = "small_template";

    /* renamed from: a  reason: collision with root package name */
    int f9885a;

    /* renamed from: b  reason: collision with root package name */
    Context f9886b;
    private ConstraintLayout background;

    /* renamed from: c  reason: collision with root package name */
    TextView f9887c;
    private Button callToActionView;

    /* renamed from: d  reason: collision with root package name */
    CardView f9888d;

    /* renamed from: e  reason: collision with root package name */
    CardView f9889e;
    private ImageView iconView;
    private MediaView mediaView;
    private NativeAd nativeAd;
    private NativeAdView nativeAdView;
    private TextView primaryView;
    private RatingBar ratingBar;
    private TextView secondaryView;
    private NativeStyle styles;
    private int templateType;
    private TextView tertiaryView;

    public TemplateView(Context context, int i2) {
        super(context);
        this.f9885a = i2;
        this.f9886b = context;
        initView(context, null);
    }

    private boolean adHasOnlyStore(NativeAd nativeAd) {
        String store = nativeAd.getStore();
        String advertiser = nativeAd.getAdvertiser();
        if (!TextUtils.isEmpty(store) && TextUtils.isEmpty(advertiser)) {
            return true;
        }
        return false;
    }

//    private void applyStyles() {
//        TextView textView;
//        TextView textView2;
//        TextView textView3;
//        Button button;
//        TextView textView4;
//        TextView textView5;
//        TextView textView6;
//        Button button2;
//        Button button3;
//        TextView textView7;
//        TextView textView8;
//        TextView textView9;
//        Button button4;
//        TextView textView10;
//        TextView textView11;
//        TextView textView12;
//        ColorDrawable mainBackgroundColor = this.styles.getMainBackgroundColor();
//        if (mainBackgroundColor != null) {
//            this.background.setBackground(mainBackgroundColor);
//            TextView textView13 = this.primaryView;
//            if (textView13 != null) {
//                textView13.setBackground(mainBackgroundColor);
//            }
//            TextView textView14 = this.secondaryView;
//            if (textView14 != null) {
//                textView14.setBackground(mainBackgroundColor);
//            }
//            TextView textView15 = this.tertiaryView;
//            if (textView15 != null) {
//                textView15.setBackground(mainBackgroundColor);
//            }
//        }
//        Typeface primaryTextTypeface = this.styles.getPrimaryTextTypeface();
//        if (primaryTextTypeface != null && (textView12 = this.primaryView) != null) {
//            textView12.setTypeface(primaryTextTypeface);
//        }
//        Typeface secondaryTextTypeface = this.styles.getSecondaryTextTypeface();
//        if (secondaryTextTypeface != null && (textView11 = this.secondaryView) != null) {
//            textView11.setTypeface(secondaryTextTypeface);
//        }
//        Typeface tertiaryTextTypeface = this.styles.getTertiaryTextTypeface();
//        if (tertiaryTextTypeface != null && (textView10 = this.tertiaryView) != null) {
//            textView10.setTypeface(tertiaryTextTypeface);
//        }
//        Typeface callToActionTextTypeface = this.styles.getCallToActionTextTypeface();
//        if (callToActionTextTypeface != null && (button4 = this.callToActionView) != null) {
//            button4.setTypeface(callToActionTextTypeface);
//        }
//        int primaryTextTypefaceColor = this.styles.getPrimaryTextTypefaceColor();
//        if (primaryTextTypefaceColor > 0 && (textView9 = this.primaryView) != null) {
//            textView9.setTextColor(primaryTextTypefaceColor);
//        }
//        int secondaryTextTypefaceColor = this.styles.getSecondaryTextTypefaceColor();
//        if (secondaryTextTypefaceColor > 0 && (textView8 = this.secondaryView) != null) {
//            textView8.setTextColor(secondaryTextTypefaceColor);
//        }
//        int tertiaryTextTypefaceColor = this.styles.getTertiaryTextTypefaceColor();
//        if (tertiaryTextTypefaceColor > 0 && (textView7 = this.tertiaryView) != null) {
//            textView7.setTextColor(tertiaryTextTypefaceColor);
//        }
//        int callToActionTypefaceColor = this.styles.getCallToActionTypefaceColor();
//        if (callToActionTypefaceColor > 0 && (button3 = this.callToActionView) != null) {
//            button3.setTextColor(callToActionTypefaceColor);
//        }
//        float callToActionTextSize = this.styles.getCallToActionTextSize();
//        if (callToActionTextSize > 0.0f && (button2 = this.callToActionView) != null) {
//            button2.setTextSize(callToActionTextSize);
//        }
//        float primaryTextSize = this.styles.getPrimaryTextSize();
//        if (primaryTextSize > 0.0f && (textView6 = this.primaryView) != null) {
//            textView6.setTextSize(primaryTextSize);
//        }
//        float secondaryTextSize = this.styles.getSecondaryTextSize();
//        if (secondaryTextSize > 0.0f && (textView5 = this.secondaryView) != null) {
//            textView5.setTextSize(secondaryTextSize);
//        }
//        float tertiaryTextSize = this.styles.getTertiaryTextSize();
//        if (tertiaryTextSize > 0.0f && (textView4 = this.tertiaryView) != null) {
//            textView4.setTextSize(tertiaryTextSize);
//        }
//        ColorDrawable callToActionBackgroundColor = this.styles.getCallToActionBackgroundColor();
//        if (callToActionBackgroundColor != null && (button = this.callToActionView) != null) {
//            button.setBackground(callToActionBackgroundColor);
//        }
//        ColorDrawable primaryTextBackgroundColor = this.styles.getPrimaryTextBackgroundColor();
//        if (primaryTextBackgroundColor != null && (textView3 = this.primaryView) != null) {
//            textView3.setBackground(primaryTextBackgroundColor);
//        }
//        ColorDrawable secondaryTextBackgroundColor = this.styles.getSecondaryTextBackgroundColor();
//        if (secondaryTextBackgroundColor != null && (textView2 = this.secondaryView) != null) {
//            textView2.setBackground(secondaryTextBackgroundColor);
//        }
//        ColorDrawable tertiaryTextBackgroundColor = this.styles.getTertiaryTextBackgroundColor();
//        if (tertiaryTextBackgroundColor != null && (textView = this.tertiaryView) != null) {
//            textView.setBackground(tertiaryTextBackgroundColor);
//        }
//        if (SharPerf.getBGColor(this.f9886b).length() >= 9) {
//            try {
//                if (Build.VERSION.SDK_INT >= 21) {
//                    this.f9888d.setElevation(0.0f);
//                }
//            } catch (Exception unused) {
//            }
//        }
//        try {
//            this.f9888d.setCardBackgroundColor(Color.parseColor(SharPerf.getBGColor(this.f9886b)));
//        } catch (Exception unused2) {
//        }
//        try {
//            this.primaryView.setBackgroundColor(Color.parseColor(SharPerf.getBGColor(this.f9886b)));
//            this.primaryView.setTextColor(Color.parseColor(SharPerf.getTitleTextColor(this.f9886b)));
//        } catch (Exception unused3) {
//        }
//        try {
//            this.tertiaryView.setTextColor(Color.parseColor(SharPerf.getDescriptionTextColor(this.f9886b)));
//        } catch (Exception unused4) {
//        }
//        try {
//            this.secondaryView.setTextColor(Color.parseColor(SharPerf.getDescriptionTextColor(this.f9886b)));
//            this.f9887c.setTextColor(Color.parseColor(SharPerf.getButtonColor(this.f9886b)));
//        } catch (Exception unused5) {
//        }
//        try {
//            GradientDrawable gradientDrawable = new GradientDrawable();
//            gradientDrawable.setColor(0);
//            gradientDrawable.setStroke(3, Color.parseColor(SharPerf.getButtonColor(this.f9886b)));
//            int i2 = Build.VERSION.SDK_INT;
//            this.f9887c.setBackground(gradientDrawable);
//            LayerDrawable layerDrawable = (LayerDrawable) this.ratingBar.getProgressDrawable();
//            if (i2 >= 21) {
//                b.a(layerDrawable.getDrawable(2), Color.parseColor(SharPerf.getButtonColor(this.f9886b)));
//            } else {
//                layerDrawable.getDrawable(2).setColorFilter(Color.parseColor(SharPerf.getButtonColor(this.f9886b)), PorterDuff.Mode.SRC_ATOP);
//            }
//        } catch (Exception unused6) {
//        }
//        if (SharPerf.getNative_custom(this.f9886b).equals("2")) {
//            try {
//                if (Build.VERSION.SDK_INT >= 21) {
//                    this.f9889e.setElevation(0.0f);
//                }
//            } catch (Exception unused7) {
//            }
//            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.callToActionView, "scaleX", 0.9f, 1.1f);
//            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this.callToActionView, "scaleY", 0.9f, 1.1f);
//            ofFloat.setRepeatCount(-1);
//            ofFloat.setRepeatMode(ValueAnimator.REVERSE);
//            ofFloat2.setRepeatCount(-1);
//            ofFloat2.setRepeatMode(ValueAnimator.REVERSE);
//            AnimatorSet animatorSet = new AnimatorSet();
//            animatorSet.setDuration(1000L);
//            animatorSet.play(ofFloat).with(ofFloat2);
//            animatorSet.start();
//        }
//        try {
//            this.callToActionView.setBackgroundColor(Color.parseColor(SharPerf.getButtonColor(this.f9886b)));
//            this.callToActionView.setTextColor(Color.parseColor(SharPerf.getButtonTextColor(this.f9886b)));
//        } catch (Exception unused8) {
//        }
//        invalidate();
//        requestLayout();
//    }

    private void applyStyles() {
        TextView textView;
        TextView textView2;
        TextView textView3;
        Button button;
        TextView textView4;
        TextView textView5;
        TextView textView6;
        Button button2;
        Button button3;
        TextView textView7;
        TextView textView8;
        TextView textView9;
        Button button4;
        TextView textView10;
        TextView textView11;
        TextView textView12;
        ColorDrawable mainBackgroundColor = this.styles.getMainBackgroundColor();
        if (mainBackgroundColor != null) {
            this.background.setBackground(mainBackgroundColor);
            TextView textView13 = this.primaryView;
            if (textView13 != null) {
                textView13.setBackground(mainBackgroundColor);
            }
            TextView textView14 = this.secondaryView;
            if (textView14 != null) {
                textView14.setBackground(mainBackgroundColor);
            }
            TextView textView15 = this.tertiaryView;
            if (textView15 != null) {
                textView15.setBackground(mainBackgroundColor);
            }
        }
        Typeface primaryTextTypeface = this.styles.getPrimaryTextTypeface();
        if (primaryTextTypeface != null && (textView12 = this.primaryView) != null) {
            textView12.setTypeface(primaryTextTypeface);
        }
        Typeface secondaryTextTypeface = this.styles.getSecondaryTextTypeface();
        if (secondaryTextTypeface != null && (textView11 = this.secondaryView) != null) {
            textView11.setTypeface(secondaryTextTypeface);
        }
        Typeface tertiaryTextTypeface = this.styles.getTertiaryTextTypeface();
        if (tertiaryTextTypeface != null && (textView10 = this.tertiaryView) != null) {
            textView10.setTypeface(tertiaryTextTypeface);
        }
        Typeface callToActionTextTypeface = this.styles.getCallToActionTextTypeface();
        if (callToActionTextTypeface != null && (button4 = this.callToActionView) != null) {
            button4.setTypeface(callToActionTextTypeface);
        }
        int primaryTextTypefaceColor = this.styles.getPrimaryTextTypefaceColor();
        if (primaryTextTypefaceColor > 0 && (textView9 = this.primaryView) != null) {
            textView9.setTextColor(primaryTextTypefaceColor);
        }
        int secondaryTextTypefaceColor = this.styles.getSecondaryTextTypefaceColor();
        if (secondaryTextTypefaceColor > 0 && (textView8 = this.secondaryView) != null) {
            textView8.setTextColor(secondaryTextTypefaceColor);
        }
        int tertiaryTextTypefaceColor = this.styles.getTertiaryTextTypefaceColor();
        if (tertiaryTextTypefaceColor > 0 && (textView7 = this.tertiaryView) != null) {
            textView7.setTextColor(tertiaryTextTypefaceColor);
        }
        int callToActionTypefaceColor = this.styles.getCallToActionTypefaceColor();
        if (callToActionTypefaceColor > 0 && (button3 = this.callToActionView) != null) {
            button3.setTextColor(callToActionTypefaceColor);
        }
        float callToActionTextSize = this.styles.getCallToActionTextSize();
        if (callToActionTextSize > 0.0f && (button2 = this.callToActionView) != null) {
            button2.setTextSize(callToActionTextSize);
        }
        float primaryTextSize = this.styles.getPrimaryTextSize();
        if (primaryTextSize > 0.0f && (textView6 = this.primaryView) != null) {
            textView6.setTextSize(primaryTextSize);
        }
        float secondaryTextSize = this.styles.getSecondaryTextSize();
        if (secondaryTextSize > 0.0f && (textView5 = this.secondaryView) != null) {
            textView5.setTextSize(secondaryTextSize);
        }
        float tertiaryTextSize = this.styles.getTertiaryTextSize();
        if (tertiaryTextSize > 0.0f && (textView4 = this.tertiaryView) != null) {
            textView4.setTextSize(tertiaryTextSize);
        }
        ColorDrawable callToActionBackgroundColor = this.styles.getCallToActionBackgroundColor();
        if (callToActionBackgroundColor != null && (button = this.callToActionView) != null) {
            button.setBackground(callToActionBackgroundColor);
        }
        ColorDrawable primaryTextBackgroundColor = this.styles.getPrimaryTextBackgroundColor();
        if (primaryTextBackgroundColor != null && (textView3 = this.primaryView) != null) {
            textView3.setBackground(primaryTextBackgroundColor);
        }
        ColorDrawable secondaryTextBackgroundColor = this.styles.getSecondaryTextBackgroundColor();
        if (secondaryTextBackgroundColor != null && (textView2 = this.secondaryView) != null) {
            textView2.setBackground(secondaryTextBackgroundColor);
        }
        ColorDrawable tertiaryTextBackgroundColor = this.styles.getTertiaryTextBackgroundColor();
        if (tertiaryTextBackgroundColor != null && (textView = this.tertiaryView) != null) {
            textView.setBackground(tertiaryTextBackgroundColor);
        }
        if (SharPerf.getBGColor(this.f9886b).length() >= 9) {
            try {
                if (Build.VERSION.SDK_INT >= 21) {
                    this.f9888d.setElevation(0.0f);
                }
            } catch (Exception unused) {
            }
        }
        try {
            this.f9888d.setCardBackgroundColor(Color.parseColor(SharPerf.getBGColor(this.f9886b)));
        } catch (Exception unused2) {
        }
        try {
            this.primaryView.setBackgroundColor(Color.parseColor(SharPerf.getBGColor(this.f9886b)));
            this.primaryView.setTextColor(Color.parseColor(SharPerf.getTitleTextColor(this.f9886b)));
        } catch (Exception unused3) {
        }
        try {
            this.tertiaryView.setTextColor(Color.parseColor(SharPerf.getDescriptionTextColor(this.f9886b)));
        } catch (Exception unused4) {
        }
        try {
            this.secondaryView.setTextColor(Color.parseColor(SharPerf.getDescriptionTextColor(this.f9886b)));
            this.f9887c.setTextColor(Color.parseColor(SharPerf.getButtonColor(this.f9886b)));
        } catch (Exception unused5) {
        }
        try {
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColor(0);
            gradientDrawable.setStroke(3, Color.parseColor(SharPerf.getButtonColor(this.f9886b)));
            int i2 = Build.VERSION.SDK_INT;
            this.f9887c.setBackground(gradientDrawable);
            LayerDrawable layerDrawable = (LayerDrawable) this.ratingBar.getProgressDrawable();
            if (i2 >= 21) {
                layerDrawable.getDrawable(2).setTint(Color.parseColor(SharPerf.getButtonColor(this.f9886b)));
            } else {
                layerDrawable.getDrawable(2).setColorFilter(Color.parseColor(SharPerf.getButtonColor(this.f9886b)), PorterDuff.Mode.SRC_ATOP);
            }
        } catch (Exception unused6) {
        }
        if (SharPerf.getNative_custom(this.f9886b).equals("2")) {
            try {
                if (Build.VERSION.SDK_INT >= 21) {
                    this.f9889e.setElevation(0.0f);
                }
            } catch (Exception unused7) {
            }
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.callToActionView, "scaleX", 0.9f, 1.1f);
            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this.callToActionView, "scaleY", 0.9f, 1.1f);
            ofFloat.setRepeatCount(-1);
            ofFloat.setRepeatMode(ValueAnimator.REVERSE);
            ofFloat2.setRepeatCount(-1);
            ofFloat2.setRepeatMode(ValueAnimator.REVERSE);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(1000L);
            animatorSet.play(ofFloat).with(ofFloat2);
            animatorSet.start();
        }
        try {
            this.callToActionView.setBackgroundColor(Color.parseColor(SharPerf.getButtonColor(this.f9886b)));
            this.callToActionView.setTextColor(Color.parseColor(SharPerf.getButtonTextColor(this.f9886b)));
        } catch (Exception unused8) {
        }
        invalidate();
        requestLayout();
    }

    private void initView(Context context, AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.TemplateView, 0, 0);
        try {



            Log.e("getNative_custom", "" + SharPerf.getNative_custom(this.f9886b));
            if (!SharPerf.getNative_custom(this.f9886b).equals("1") && !SharPerf.getNative_custom(this.f9886b).equals("2")) {
                int i2 = this.f9885a;
                if (i2 == 2) {
                    this.templateType = obtainStyledAttributes.getResourceId(R.styleable.TemplateView_gnt_template_type, R.layout.gnt_medium_template_view);
                } else if (i2 == 0) {
                    this.templateType = obtainStyledAttributes.getResourceId(R.styleable.TemplateView_gnt_template_type, R.layout.gnt_start_template_view);
                } else {
                    this.templateType = obtainStyledAttributes.getResourceId(R.styleable.TemplateView_gnt_template_type, R.layout.gnt_small_template_view);
                }
                obtainStyledAttributes.recycle();
                ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(this.templateType, this);
                onFinishInflate();
            }
            Log.e("Native_Ad_Size11", "" + this.f9885a);
            int i3 = this.f9885a;
            if (i3 == 2) {
                this.templateType = obtainStyledAttributes.getResourceId(R.styleable.TemplateView_gnt_template_type, R.layout.gnt_medium_template_view_custom);
            } else if (i3 == 0) {
                this.templateType = obtainStyledAttributes.getResourceId(R.styleable.TemplateView_gnt_template_type, R.layout.gnt_start_template_view_custom);
            } else {
                    this.templateType = obtainStyledAttributes.getResourceId(R.styleable.TemplateView_gnt_template_type, R.layout.gnt_small_template_view_custom);
            }
            obtainStyledAttributes.recycle();
            ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(this.templateType, this);
            onFinishInflate();
        } catch (Throwable th) {
            obtainStyledAttributes.recycle();
            throw th;
        }
    }

    public void destroyNativeAd() {
        this.nativeAd.destroy();
    }

    public NativeAdView getNativeAdView() {
        return this.nativeAdView;
    }

    public String getTemplateTypeName() {
        int i2 = this.templateType;
        if (i2 == R.layout.gnt_medium_template_view) {
            return MEDIUM_TEMPLATE;
        }
        if (i2 == R.layout.gnt_small_template_view) {
            return SMALL_TEMPLATE;
        }
        return "";
    }

    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this.nativeAdView = (NativeAdView) findViewById(R.id.native_ad_view);
        this.primaryView = (TextView) findViewById(R.id.primary);
        this.secondaryView = (TextView) findViewById(R.id.secondary);
        try {
            this.tertiaryView = (TextView) findViewById(R.id.body);
        } catch (Exception unused) {
        }
        try {
            this.f9888d = (CardView) findViewById(R.id.card_main_background);
        } catch (Exception unused2) {
        }
        try {
            this.f9889e = (CardView) findViewById(R.id.cardad);
        } catch (Exception unused3) {
        }
        this.f9887c = (TextView) findViewById(R.id.ad_notification_view);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        this.ratingBar = ratingBar;
        ratingBar.setEnabled(false);
        this.callToActionView = (Button) findViewById(R.id.cta);
        this.iconView = (ImageView) findViewById(R.id.icon);
        this.mediaView = (MediaView) findViewById(R.id.media_view);
        this.background = (ConstraintLayout) findViewById(R.id.background);
    }

    public void setNativeAd(NativeAd nativeAd) {
        this.nativeAd = nativeAd;
        String store = nativeAd.getStore();
        String advertiser = nativeAd.getAdvertiser();
        String headline = nativeAd.getHeadline();
        String body = nativeAd.getBody();
        String callToAction = nativeAd.getCallToAction();
        Double starRating = nativeAd.getStarRating();
        NativeAd.Image icon = nativeAd.getIcon();
        this.nativeAdView.setCallToActionView(this.callToActionView);
        this.nativeAdView.setHeadlineView(this.primaryView);
        this.nativeAdView.setMediaView(this.mediaView);
        this.secondaryView.setVisibility(View.VISIBLE);
        if (adHasOnlyStore(nativeAd)) {
            this.nativeAdView.setStoreView(this.secondaryView);
        } else if (!TextUtils.isEmpty(advertiser)) {
            this.nativeAdView.setAdvertiserView(this.secondaryView);
            store = advertiser;
        } else {
            store = "";
        }
        this.primaryView.setText(headline);
        this.callToActionView.setText(callToAction);
        if (starRating != null && starRating.doubleValue() > 0.0d) {
            this.secondaryView.setVisibility(View.GONE);
            this.ratingBar.setVisibility(View.VISIBLE);
            this.ratingBar.setRating(starRating.floatValue());
            this.nativeAdView.setStarRatingView(this.ratingBar);
        } else {
            this.secondaryView.setText(store);
            this.secondaryView.setVisibility(View.VISIBLE);
            this.ratingBar.setVisibility(View.GONE);
        }
        if (icon != null) {
            this.iconView.setVisibility(View.VISIBLE);
            this.iconView.setImageDrawable(icon.getDrawable());
        } else {
            this.iconView.setVisibility(View.GONE);
        }
        try {
            TextView textView = this.tertiaryView;
            if (textView != null) {
                textView.setText(body);
                this.nativeAdView.setBodyView(this.tertiaryView);
            }
        } catch (Exception unused) {
        }
        this.nativeAdView.setNativeAd(nativeAd);
    }

    public void setStyles(NativeStyle nativeStyle) {
        this.styles = nativeStyle;
        applyStyles();
    }

    public TemplateView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f9885a = 0;
        initView(context, attributeSet);
    }

    public TemplateView(Context context, @Nullable AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        this.f9885a = 0;
        initView(context, attributeSet);
    }

    @RequiresApi(api = 21)
    public TemplateView(Context context, AttributeSet attributeSet, int i2, int i3) {
        super(context, attributeSet, i2, i3);
        this.f9885a = 0;
        initView(context, attributeSet);
    }
}
