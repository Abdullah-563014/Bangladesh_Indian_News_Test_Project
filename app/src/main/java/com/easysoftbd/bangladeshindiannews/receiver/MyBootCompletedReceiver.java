package com.easysoftbd.bangladeshindiannews.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.easysoftbd.bangladeshindiannews.services.MyForgroundService;
import com.easysoftbd.bangladeshindiannews.services.NewsLoaderService;
import com.easysoftbd.bangladeshindiannews.ui.activities.main.MainActivity;
import com.easysoftbd.bangladeshindiannews.utils.CommonMethods;
import com.easysoftbd.bangladeshindiannews.utils.Constants;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class MyBootCompletedReceiver extends BroadcastReceiver {

    private NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (notificationManager==null) {
            notificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        startForgroundService(context);
    }

    private boolean isWorkScheduled(Context context, String tag) {
        WorkManager instance = WorkManager.getInstance(context.getApplicationContext());
        ListenableFuture<List<WorkInfo>> statuses = instance.getWorkInfosByTag(tag);
        try {
            boolean running = false;
            List<WorkInfo> workInfoList = statuses.get();
            for (WorkInfo workInfo : workInfoList) {
                WorkInfo.State state = workInfo.getState();
                running = state == WorkInfo.State.RUNNING | state == WorkInfo.State.ENQUEUED;
            }
            return running;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isForgroundServiceVisible() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            StatusBarNotification[] notifications = notificationManager.getActiveNotifications();
            if (notifications.length > 0) {
                for (StatusBarNotification notification : notifications) {
                    if (notification.getId() == 11111) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void startForgroundService(Context context) {
        if (CommonMethods.getBooleanFromSharedPreference(context.getApplicationContext(),Constants.notificationStatusSwitchKey,true)) {
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
                if (!isWorkScheduled(context,Constants.TAG) || !isForgroundServiceVisible()) {
                    Intent intent = new Intent(context.getApplicationContext(), MyForgroundService.class);
                    ContextCompat.startForegroundService(context.getApplicationContext(),intent);
                }
            } else {
                if (!isWorkScheduled(context,Constants.TAG)) {
                    Intent intent = new Intent(context.getApplicationContext(), MyForgroundService.class);
                    ContextCompat.startForegroundService(context.getApplicationContext(),intent);
                }
            }
        }
    }


}