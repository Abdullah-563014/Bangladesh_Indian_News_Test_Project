package com.easysoftbd.bangladeshindiannews.ui;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.easysoftbd.bangladeshindiannews.utils.CommonMethods;
import com.easysoftbd.bangladeshindiannews.utils.Constants;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class MyApplication  extends Application {

    public static final String MY_NOTIFICATION_CHANNEL_ID="BangladeshIndianNews";
    private String notificationName="BangladeshIndianNews";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        Constants.platformName= CommonMethods.getStringFromSharedPreference(this, Constants.platformNameKey,"unity");
        String ipSwitchStatus= CommonMethods.getStringFromSharedPreference(this, Constants.ipCheckSwitchKey,"true");
        if (ipSwitchStatus.equalsIgnoreCase("true")) {
            Constants.willCheckIpAddress=true;
        } else {
            Constants.willCheckIpAddress=false;
        }

        FirebaseAnalytics.getInstance(this);
        FirebaseCrashlytics.getInstance();
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);

        AudienceNetworkAds.initialize(this);

        AdSettings.setDebugBuild(Constants.facebookDebugBuild);

        MobileAds.initialize(this, (InitializationStatus initializationStatus) -> {
        });



    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT>=26){
            NotificationChannel notificationChannel=new NotificationChannel(
                    MY_NOTIFICATION_CHANNEL_ID
                    ,notificationName
                    , NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
        }
    }


}
