package com.easysoftbd.bangladeshindiannews.data.local.india.english;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdBreaking;

import java.util.List;

@Dao
public interface IndianEnglishInternationalDao {

    @Query("SELECT * FROM indian_english_international ORDER BY serial ASC")
    LiveData<List<IndianEnglishInternational>> getAllNews();

    @Query("SELECT * FROM indian_english_international WHERE serial=:serial")
    IndianEnglishInternational getNews(int serial);

    @Query("SELECT * FROM indian_english_international WHERE notificationStatus=:tag")
    List<IndianEnglishInternational> getAllNotificationNews(String tag);

    @Insert
    void insertNews(IndianEnglishInternational indianEnglishInternational);

    @Update
    void updateNews(IndianEnglishInternational indianEnglishInternational);

    @Update
    void updateNews(IndianEnglishInternational first, IndianEnglishInternational second);


}

