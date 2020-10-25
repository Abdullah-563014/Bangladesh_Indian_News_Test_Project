package com.easysoftbd.bangladeshindiannews.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;

public class CommonMethods {

    private static SharedPreferences sharedPreferences;

    public static boolean haveInternet(ConnectivityManager connectivityManager) {

        if (Build.VERSION.SDK_INT >= 23) {
            Network network = connectivityManager.getActiveNetwork();
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities == null) {
                return false;
            }
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true;
            } else {
                return false;
            }
        } else {
            if (connectivityManager.getActiveNetworkInfo() != null
                    && connectivityManager.getActiveNetworkInfo().isAvailable()
                    && connectivityManager.getActiveNetworkInfo().isConnected()) {
                return true;
            } else {
                return false;
            }

        }
    }

    private static SharedPreferences getSharedInstance(Context context) {
        if (sharedPreferences==null) {
            sharedPreferences=context.getSharedPreferences(Constants.sharedPreferenceName,Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    public static void setStringToSharedPreference(Context context, String key, String value) {
        SharedPreferences.Editor editor=getSharedInstance(context).edit();
        editor.putString(key,value);
        editor.apply();
    }

    public static String getStringFromSharedPreference(Context context, String key, String defaultValue) {
        return getSharedInstance(context).getString(key,defaultValue);
    }






}
