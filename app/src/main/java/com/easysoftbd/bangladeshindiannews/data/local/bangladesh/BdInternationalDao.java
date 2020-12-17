package com.easysoftbd.bangladeshindiannews.data.local.bangladesh;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BdInternationalDao {

    @Query("SELECT * FROM bd_international ORDER BY serial ASC")
    LiveData<List<BdInternational>> getAllNews();

    @Query("SELECT * FROM bd_international WHERE serial=:serial")
    BdInternational getNews(int serial);

    @Query("SELECT * FROM bd_international WHERE notificationStatus=:tag")
    List<BdInternational> getAllNotificationNews(String tag);

    @Insert
    void insertNews(BdInternational bdInternational);

    @Update
    void updateNews(BdInternational bdInternational);

    @Update
    void updateNews(BdInternational first, BdInternational second);
}
