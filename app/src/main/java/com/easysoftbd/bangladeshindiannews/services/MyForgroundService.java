package com.easysoftbd.bangladeshindiannews.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.easysoftbd.bangladeshindiannews.R;
import com.easysoftbd.bangladeshindiannews.ui.activities.main.MainActivity;
import com.easysoftbd.bangladeshindiannews.utils.Constants;

import java.util.concurrent.TimeUnit;

import static com.easysoftbd.bangladeshindiannews.ui.MyApplication.MY_NOTIFICATION_CHANNEL_ID;

public class MyForgroundService extends Service {

    private int notificationId=1111;
    private String notificationTitle="Checking Update News";
    private String notificationContent="We Are Trying To Retrieve Update News";
    private String notificationTicker="Checking Update News";
    private NotificationManager notificationManager;
    private boolean restartForgroundService=false;
    private int RC_SIGN_IN=10010;



    public MyForgroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent serviceIntent, int flags, int startId) {
        if (notificationManager==null){
            notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }

        if (!Constants.startedForGroundService) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    getBaseContext(), 0, intent, 0);
            Notification notification;
            if (Build.VERSION.SDK_INT >= 26) {
                notification = new NotificationCompat.Builder(
                        getBaseContext(), MY_NOTIFICATION_CHANNEL_ID)
                        .setContentTitle(notificationTitle)
                        .setTicker(notificationTicker)
                        .setContentText(notificationContent)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setPriority(NotificationCompat.DEFAULT_ALL)
                        .setContentIntent(pendingIntent)
                        .setOngoing(true)
                        .build();
            } else {
                notification = new NotificationCompat.Builder(
                        getBaseContext())
                        .setContentTitle(notificationTitle)
                        .setTicker(notificationTicker)
                        .setContentText(notificationContent)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setPriority(NotificationCompat.DEFAULT_ALL)
                        .setContentIntent(pendingIntent)
                        .setOngoing(true)
                        .build();
            }
            notification.flags = Notification.FLAG_NO_CLEAR;
            startForeground(notificationId, notification);
            Constants.startedForGroundService=true;
        }
//        registerReceiver(myReceiver, new IntentFilter("android.intent.action.PHONE_STATE"));
        startBackgroundService();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        Constants.startedForGroundService=false;
        super.onDestroy();
    }


    private void startBackgroundService() {
        Constraints constraints=new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        PeriodicWorkRequest request=new PeriodicWorkRequest.Builder(NewsLoaderService.class,15, TimeUnit.MINUTES)
                .addTag(Constants.TAG)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(getApplicationContext())
                .enqueueUniquePeriodicWork(Constants.TAG, ExistingPeriodicWorkPolicy.REPLACE, request);
    }



}