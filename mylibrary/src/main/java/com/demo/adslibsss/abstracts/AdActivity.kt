package com.demo.adslibsss.abstracts

import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import com.demo.adslibsss.network.InternetReceiver

abstract class AdActivity :AppCompatActivity() {
lateinit var internetReceiver: InternetReceiver
    abstract fun isConnected(isConnected: Boolean)
    override fun onResume() {
        super.onResume()
        internetReceiver = InternetReceiver(callback = {
            isConnected(it)
        })
        registerReceiver(internetReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(internetReceiver);
    }
}