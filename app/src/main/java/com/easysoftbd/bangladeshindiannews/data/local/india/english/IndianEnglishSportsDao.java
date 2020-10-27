package com.easysoftbd.bangladeshindiannews.data.local.india.english;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface IndianEnglishSportsDao {

    @Query("SELECT * FROM indian_english_sports ORDER BY serial ASC")
    LiveData<List<IndianEnglishSports>> getAllNews();

    @Query("SELECT * FROM indian_english_sports WHERE serial=:serial")
    IndianEnglishSports getNews(int serial);

    @Insert
    void insertNews(IndianEnglishSports indianEnglishSports);

    @Update
    void updateNews(IndianEnglishSports indianEnglishSports);

    @Update
    void updateNews(IndianEnglishSports first, IndianEnglishSports second);


}

