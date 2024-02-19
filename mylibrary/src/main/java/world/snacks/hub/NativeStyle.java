package world.snacks.hub;

import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;

/* loaded from: classes2.dex */
public class NativeStyle {
    private ColorDrawable callToActionBackgroundColor;
    private float callToActionTextSize;
    private Typeface callToActionTextTypeface;
    private int callToActionTypefaceColor;
    private ColorDrawable mainBackgroundColor;
    private ColorDrawable primaryTextBackgroundColor;
    private float primaryTextSize;
    private Typeface primaryTextTypeface;
    private int primaryTextTypefaceColor;
    private ColorDrawable secondaryTextBackgroundColor;
    private float secondaryTextSize;
    private Typeface secondaryTextTypeface;
    private int secondaryTextTypefaceColor;
    private ColorDrawable tertiaryTextBackgroundColor;
    private float tertiaryTextSize;
    private Typeface tertiaryTextTypeface;
    private int tertiaryTextTypefaceColor;

    /* loaded from: classes2.dex */
    public static class Builder {
        private NativeStyle styles = new NativeStyle();

        public NativeStyle build() {
            return this.styles;
        }

        public Builder withCallToActionBackgroundColor(ColorDrawable colorDrawable) {
            this.styles.callToActionBackgroundColor = colorDrawable;
            return this;
        }

        public Builder withCallToActionTextSize(float f2) {
            this.styles.callToActionTextSize = f2;
            return this;
        }

        public Builder withCallToActionTextTypeface(Typeface typeface) {
            this.styles.callToActionTextTypeface = typeface;
            return this;
        }

        public Builder withCallToActionTypefaceColor(int i2) {
            this.styles.callToActionTypefaceColor = i2;
            return this;
        }

        public Builder withMainBackgroundColor(ColorDrawable colorDrawable) {
            this.styles.mainBackgroundColor = colorDrawable;
            return this;
        }

        public Builder withPrimaryTextBackgroundColor(ColorDrawable colorDrawable) {
            this.styles.primaryTextBackgroundColor = colorDrawable;
            return this;
        }

        public Builder withPrimaryTextSize(float f2) {
            this.styles.primaryTextSize = f2;
            return this;
        }

        public Builder withPrimaryTextTypeface(Typeface typeface) {
            this.styles.primaryTextTypeface = typeface;
            return this;
        }

        public Builder withPrimaryTextTypefaceColor(int i2) {
            this.styles.primaryTextTypefaceColor = i2;
            return this;
        }

        public Builder withSecondaryTextBackgroundColor(ColorDrawable colorDrawable) {
            this.styles.secondaryTextBackgroundColor = colorDrawable;
            return this;
        }

        public Builder withSecondaryTextSize(float f2) {
            this.styles.secondaryTextSize = f2;
            return this;
        }

        public Builder withSecondaryTextTypeface(Typeface typeface) {
            this.styles.secondaryTextTypeface = typeface;
            return this;
        }

        public Builder withSecondaryTextTypefaceColor(int i2) {
            this.styles.secondaryTextTypefaceColor = i2;
            return this;
        }

        public Builder withTertiaryTextBackgroundColor(ColorDrawable colorDrawable) {
            this.styles.tertiaryTextBackgroundColor = colorDrawable;
            return this;
        }

        public Builder withTertiaryTextSize(float f2) {
            this.styles.tertiaryTextSize = f2;
            return this;
        }

        public Builder withTertiaryTextTypeface(Typeface typeface) {
            this.styles.tertiaryTextTypeface = typeface;
            return this;
        }

        public Builder withTertiaryTextTypefaceColor(int i2) {
            this.styles.tertiaryTextTypefaceColor = i2;
            return this;
        }
    }

    public ColorDrawable getCallToActionBackgroundColor() {
        return this.callToActionBackgroundColor;
    }

    public float getCallToActionTextSize() {
        return this.callToActionTextSize;
    }

    public Typeface getCallToActionTextTypeface() {
        return this.callToActionTextTypeface;
    }

    public int getCallToActionTypefaceColor() {
        return this.callToActionTypefaceColor;
    }

    public ColorDrawable getMainBackgroundColor() {
        return this.mainBackgroundColor;
    }

    public ColorDrawable getPrimaryTextBackgroundColor() {
        return this.primaryTextBackgroundColor;
    }

    public float getPrimaryTextSize() {
        return this.primaryTextSize;
    }

    public Typeface getPrimaryTextTypeface() {
        return this.primaryTextTypeface;
    }

    public int getPrimaryTextTypefaceColor() {
        return this.primaryTextTypefaceColor;
    }

    public ColorDrawable getSecondaryTextBackgroundColor() {
        return this.secondaryTextBackgroundColor;
    }

    public float getSecondaryTextSize() {
        return this.secondaryTextSize;
    }

    public Typeface getSecondaryTextTypeface() {
        return this.secondaryTextTypeface;
    }

    public int getSecondaryTextTypefaceColor() {
        return this.secondaryTextTypefaceColor;
    }

    public ColorDrawable getTertiaryTextBackgroundColor() {
        return this.tertiaryTextBackgroundColor;
    }

    public float getTertiaryTextSize() {
        return this.tertiaryTextSize;
    }

    public Typeface getTertiaryTextTypeface() {
        return this.tertiaryTextTypeface;
    }

    public int getTertiaryTextTypefaceColor() {
        return this.tertiaryTextTypefaceColor;
    }
}
