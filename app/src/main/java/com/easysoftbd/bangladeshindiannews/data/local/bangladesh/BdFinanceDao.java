package com.easysoftbd.bangladeshindiannews.data.local.bangladesh;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BdFinanceDao {

    @Query("SELECT * FROM bd_finance ORDER BY serial ASC")
    LiveData<List<BdFinance>> getAllNews();

    @Query("SELECT * FROM bd_finance WHERE serial=:serial")
    BdFinance getNews(int serial);

    @Query("SELECT * FROM bd_finance WHERE notificationStatus=:tag")
    List<BdFinance> getAllNotificationNews(String tag);

    @Insert
    void insertNews(BdFinance bdFinance);

    @Update
    void updateNews(BdFinance bdFinance);

    @Update
    void updateNews(BdFinance first, BdFinance second);
}
