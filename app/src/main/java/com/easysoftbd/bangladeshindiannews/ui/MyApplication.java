package com.easysoftbd.bangladeshindiannews.ui;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class MyApplication  extends Application {

    public static final String MY_NOTIFICATION_CHANNEL_ID="BangladeshIndianNews";
    private String notificationName="BangladeshIndianNews";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
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
