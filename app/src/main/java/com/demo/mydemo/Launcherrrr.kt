package com.demo.mydemo

import android.os.Bundle
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import world.snacks.hub.OnJsonCallBackListner
import world.snacks.hub.Pizza


class Launcherrrr : AppCompatActivity() , OnJsonCallBackListner {
    override fun OnJsonDone() {
        if (Pizza.Extra1.equals("1")) {
            Pizza.Native(this,  findViewById(R.id.banner), 2);
        } else {
            Pizza.Native(this,  findViewById(R.id.banner), 1);
        }
    }

    var pizza: Pizza? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.unity_activity)

        pizza = Pizza(
            this,
            this,
            resources.getString(R.string.app_name),
            idddd.Ads_Link,
            packageName,
            1,
            1,
            "L",
            1,
            1,
            "4",
            idddd.FB_Banner1,
            idddd.FB_Banner2,
            idddd.FB_Banner3,
            idddd.FB_Banner4,
            idddd.FB_Banner5,
            idddd.FB_MR1,
            idddd.FB_MR2,
            idddd.FB_MR3,
            idddd.FB_MR4,
            idddd.FB_MR5,
            idddd.FB_Inter1,
            idddd.FB_Inter2,
            idddd.FB_Inter3,
            idddd.FB_Inter4,
            idddd.FB_Inter5,
            idddd.FB_Native1,
            idddd.FB_Native2,
            idddd.FB_Native3,
            idddd.FB_Native4,
            idddd.FB_Native5,
            idddd.FB_NativeSmall1,
            idddd.FB_NativeSmall2,
            idddd.FB_NativeSmall3,
            idddd.FB_NativeSmall4,
            idddd.FB_NativeSmall5,
            idddd.AC_App_ID,
            idddd.AC_Inter_ID,
            idddd.AC_Banner_ID,
            idddd.AC_reward_ID,
            idddd.Tappx,
            idddd.Admob_App_ID,
            idddd.Admob_Inter_ID1,
            idddd.Admob_Inter_ID11,
            idddd.Admob_Inter_ID2,
            idddd.Admob_Inter_ID22,
            idddd.Admob_Inter_ID3,
            idddd.Admob_Inter_ID33,
            idddd.Admob_Banner_ID1,
            idddd.Admob_Banner_ID11,
            idddd.Admob_Banner_ID2,
            idddd.Admob_Banner_ID22,
            idddd.Admob_Banner_ID3,
            idddd.Admob_Banner_ID33,
            idddd.Admob_Native_ID1,
            idddd.Admob_Native_ID11,
            idddd.Admob_Native_ID2,
            idddd.Admob_Native_ID22,
            idddd.Admob_Native_ID3,
            idddd.Admob_Native_ID33,
            idddd.Admob_AppOpen_ID1,
            idddd.Admob_AppOpen_ID11,
            idddd.Admob_AppOpen_ID2,
            idddd.Admob_AppOpen_ID22,
            idddd.Admob_AppOpen_ID3,
            idddd.Admob_AppOpen_ID33,
            idddd.Admob_rvi_ID1,
            idddd.Admob_rvi_ID11,
            idddd.Admob_rvi_ID2,
            idddd.Admob_rvi_ID22,
            idddd.Admob_rvi_ID3,
            idddd.Admob_rvi_ID33
        )
        pizza!!.Splesh_Screen(
            this,
            ContextCompat.getColor(this, R.color.colorSecondary),
            R.drawable.icon200,
            resources.getString(R.string.app_name),
            "0"
        )


    }

}