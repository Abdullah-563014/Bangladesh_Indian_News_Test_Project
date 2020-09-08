package com.easysoftbd.bangladeshindiannews.data.local;

import android.content.Context;

import androidx.room.Room;

public class DatabaseClient {
    private Context context;
    private static DatabaseClient databaseClient;
    private NewsDatabase newsDatabase;


    private DatabaseClient(Context context) {
        this.context = context;
        newsDatabase = Room.databaseBuilder(context, NewsDatabase.class, "NewsDatabase").build();
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (databaseClient == null) {
            databaseClient = new DatabaseClient(context);
        }
        return databaseClient;
    }

    public NewsDatabase getAppDatabase() {
        return newsDatabase;
    }
}
