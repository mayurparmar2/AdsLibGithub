package world.snacks.hub;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

/* loaded from: classes2.dex */
public class CustomProgressDialogueInter extends Dialog {
    @SuppressLint("ResourceType")
    public CustomProgressDialogueInter(Context context, String str, String str2, int i2, int i3) {
        super(context, 16973841);
        getWindow().setFlags(1024, 1024);
        setTitle((CharSequence) null);
        setCancelable(false);
        setOnCancelListener(null);
        View inflate = LayoutInflater.from(context).inflate(R.layout.loading_ads_full, (ViewGroup) null);
        setContentView(inflate);
        TextView textView = (TextView) inflate.findViewById(R.id.title);
        textView.setText("" + str);
        TextView textView2 = (TextView) inflate.findViewById(R.id.message);
        textView2.setText("" + str2);
        ((RelativeLayout) inflate.findViewById(R.id.mainrl)).setBackgroundColor(i2);
        textView.setTextColor(i3);
        textView2.setTextColor(i3);
    }
}
