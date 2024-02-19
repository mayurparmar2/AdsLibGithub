package com.example.admob2;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;


public class NetworkManager {
    private static Activity activity;
    private static OnMonitorListener listener;
    private static NetworkRequest networkRequest = new NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).addTransportType(NetworkCapabilities.TRANSPORT_WIFI).addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR).build();
    private static ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            if (NetworkManager.listener != null) {
                NetworkManager.listener.onConnectivityChanged(true);
            }
        }

        @Override
        public void onLost(Network network) {
            super.onLost(network);
            if (NetworkManager.listener != null) {
                NetworkManager.listener.onConnectivityChanged(false);
            }
        }

        @Override
        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED);
        }
    };

    public static void Monitoring(Activity activity2, OnMonitorListener onMonitorListener) {
        activity = activity2;
        listener = onMonitorListener;
        ConnectivityManager connectivityManager = (ConnectivityManager) activity2.getSystemService(ConnectivityManager.class);
        if (connectivityManager != null) {
            connectivityManager.requestNetwork(networkRequest, networkCallback);
        }
    }


    public interface OnMonitorListener {
        void onConnectivityChanged(boolean z);
    }
}
