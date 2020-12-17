package com.easysoftbd.bangladeshindiannews.data.local.india.bangla;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdBreaking;

import java.util.List;

@Dao
public interface IndianBanglaTvChannelDao {

    @Query("SELECT * FROM indian_bangla_tv_channel ORDER BY serial ASC")
    LiveData<List<IndianBanglaTvChannel>> getAllNews();

    @Query("SELECT * FROM indian_bangla_tv_channel WHERE serial=:serial")
    IndianBanglaTvChannel getNews(int serial);

    @Query("SELECT * FROM indian_bangla_tv_channel WHERE notificationStatus=:tag")
    List<IndianBanglaTvChannel> getAllNotificationNews(String tag);

    @Insert
    void insertNews(IndianBanglaTvChannel indianBanglaTvChannel);

    @Update
    void updateNews(IndianBanglaTvChannel indianBanglaTvChannel);

    @Update
    void updateNews(IndianBanglaTvChannel first, IndianBanglaTvChannel second);
}
