package com.easysoftbd.bangladeshindiannews.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;

import com.squareup.picasso.Transformation;

import java.util.Date;
import java.util.Random;

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

    public static void setBooleanToSharedPreference(Context context, String key, boolean value) {
        SharedPreferences.Editor editor=getSharedInstance(context).edit();
        editor.putBoolean(key,value);
        editor.apply();
    }

    public static boolean getBooleanFromSharedPreference(Context context, String key, boolean defaultValue) {
        return getSharedInstance(context).getBoolean(key,defaultValue);
    }

    public static int getRandomNumber(int range) {
        Random random=new Random();
        return random.nextInt(range);
    }

    public static String getCurrentTime() {
        Date date=new Date();
        return String.valueOf(date.getTime());
    }

    public static String getTimeDifBetweenToTime(String startTime, String endTime) {
        try {

            long date1=Long.parseLong(startTime);
            long date2=Long.parseLong(endTime);

            long difference = date2 - date1;
            long differenceDates = difference / (60 * 60 * 1000);

            return Long.toString(differenceDates);

        } catch (Exception exception) {
            return null;
        }
    }

    public static String getMinDifBetweenToTime(String startTime, String endTime) {
        try {

            long date1=Long.parseLong(startTime);
            long date2=Long.parseLong(endTime);

            long difference = date2 - date1;
            long differenceDates = difference / (60 * 1000);

            return Long.toString(differenceDates);

        } catch (Exception exception) {
            return null;
        }
    }

    public static class PicassoTransform implements Transformation {
        int targetWidth;
        public PicassoTransform(int targetWidth) {
            this.targetWidth=targetWidth;
        }

        @Override
        public Bitmap transform(Bitmap source) {
            double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
            int targetHeight = (int) (targetWidth * aspectRatio);
            Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
            if (result != source) {
                // Same bitmap is returned if sizes are the same
                source.recycle();
            }
            return result;
        }

        @Override
        public String key() {
            return "transformation" + " desiredWidth";
        }
    };




}
