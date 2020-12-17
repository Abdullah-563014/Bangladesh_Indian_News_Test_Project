package com.easysoftbd.bangladeshindiannews.data.local.india.bangla;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdBreaking;

import java.util.List;

@Dao
public interface IndianBanglaEntertainmentDao {

    @Query("SELECT * FROM indian_bangla_entertainment ORDER BY serial ASC")
    LiveData<List<IndianBanglaEntertainment>> getAllNews();

    @Query("SELECT * FROM indian_bangla_entertainment WHERE serial=:serial")
    IndianBanglaEntertainment getNews(int serial);

    @Query("SELECT * FROM indian_bangla_entertainment WHERE notificationStatus=:tag")
    List<IndianBanglaEntertainment> getAllNotificationNews(String tag);

    @Insert
    void insertNews(IndianBanglaEntertainment indianBanglaEntertainment);

    @Update
    void updateNews(IndianBanglaEntertainment indianBanglaEntertainment);

    @Update
    void updateNews(IndianBanglaEntertainment first, IndianBanglaEntertainment second);
}
