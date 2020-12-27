package com.easysoftbd.bangladeshindiannews.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.easysoftbd.bangladeshindiannews.services.MyForgroundService;
import com.easysoftbd.bangladeshindiannews.services.NewsLoaderService;
import com.easysoftbd.bangladeshindiannews.ui.activities.main.MainActivity;
import com.easysoftbd.bangladeshindiannews.utils.Constants;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class MyBootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        startForgroundService(context);
    }


    private void startBackgroundService(Context context) {
        String workerTag="Abdullah";
        Constraints constraints=new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        PeriodicWorkRequest request=new PeriodicWorkRequest.Builder(NewsLoaderService.class,15, TimeUnit.MINUTES)
                .addTag(workerTag)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(workerTag, ExistingPeriodicWorkPolicy.REPLACE, request);
    }

    private boolean isWorkScheduled(String tag) {
        WorkManager instance = WorkManager.getInstance();
        ListenableFuture<List<WorkInfo>> statuses = instance.getWorkInfosByTag(tag);
        try {
            boolean running = false;
            List<WorkInfo> workInfoList = statuses.get();
            for (WorkInfo workInfo : workInfoList) {
                WorkInfo.State state = workInfo.getState();
                running = state == WorkInfo.State.RUNNING | state == WorkInfo.State.ENQUEUED;
            }
            return running;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void startForgroundService(Context context) {
        if (!isWorkScheduled(Constants.TAG)) {
            Intent intent = new Intent(context, MyForgroundService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
//            startService(intent);
                startBackgroundService(context);
            }
        }
    }
}