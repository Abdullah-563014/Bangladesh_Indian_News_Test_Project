package com.easysoftbd.bangladeshindiannews.data.local.india.english;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdBreaking;

import java.util.List;

@Dao
public interface IndianEnglishEntertainmentDao {

    @Query("SELECT * FROM indian_english_entertainment ORDER BY serial ASC")
    LiveData<List<IndianEnglishEntertainment>> getAllNews();

    @Query("SELECT * FROM indian_english_entertainment WHERE serial=:serial")
    IndianEnglishEntertainment getNews(int serial);

    @Query("SELECT * FROM indian_english_entertainment WHERE notificationStatus=:tag")
    List<IndianEnglishEntertainment> getAllNotificationNews(String tag);

    @Insert
    void insertNews(IndianEnglishEntertainment indianEnglishEntertainment);

    @Update
    void updateNews(IndianEnglishEntertainment indianEnglishEntertainment);

    @Update
    void updateNews(IndianEnglishEntertainment first, IndianEnglishEntertainment second);

}
