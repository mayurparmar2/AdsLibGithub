package com.example.admob2;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;


public class ExitScreen extends AppCompatActivity {
    private ImageView txt_no;
    private ImageView txt_rate;
    private ImageView txt_yes;


    @Override

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.exit_screen);
        this.txt_rate = (ImageView) findViewById(R.id.txt_rate);
        this.txt_yes = (ImageView) findViewById(R.id.txt_yes);
        this.txt_no = (ImageView) findViewById(R.id.txt_no);
        this.txt_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                ExitScreen.this.m5766lambda$onCreate$0$vocsyadsExitScreen(view);
            }
        });
        this.txt_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                ExitScreen.this.m5767lambda$onCreate$1$vocsyadsExitScreen(view);
            }
        });
        this.txt_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                ExitScreen.this.m5768lambda$onCreate$2$vocsyadsExitScreen(view);
            }
        });
        GoogleAds.getInstance().addNativeView(this, findViewById(R.id.nativeLay));
    }


    public void m5766lambda$onCreate$0$vocsyadsExitScreen(View view) {
        rate();
    }


    public void m5767lambda$onCreate$1$vocsyadsExitScreen(View view) {
        yes();
    }


    public void m5768lambda$onCreate$2$vocsyadsExitScreen(View view) {
        no();
    }

    private void rate() {
        AppUtil.shareApp(this);
    }

    private void yes() {
        finishAffinity();
    }

    private void no() {
        finish();
    }
}
