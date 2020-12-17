package com.easysoftbd.bangladeshindiannews.data.local.india.english;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdBreaking;

import java.util.List;

@Dao
public interface IndianEnglishTvChannelDao {

    @Query("SELECT * FROM indian_english_tv_channel ORDER BY serial ASC")
    LiveData<List<IndianEnglishTvChannel>> getAllNews();

    @Query("SELECT * FROM indian_english_tv_channel WHERE serial=:serial")
    IndianEnglishTvChannel getNews(int serial);

    @Query("SELECT * FROM indian_english_tv_channel WHERE notificationStatus=:tag")
    List<IndianEnglishTvChannel> getAllNotificationNews(String tag);

    @Insert
    void insertNews(IndianEnglishTvChannel indianEnglishBreaking);

    @Update
    void updateNews(IndianEnglishTvChannel indianEnglishBreaking);

    @Update
    void updateNews(IndianEnglishTvChannel first, IndianEnglishTvChannel second);


}

