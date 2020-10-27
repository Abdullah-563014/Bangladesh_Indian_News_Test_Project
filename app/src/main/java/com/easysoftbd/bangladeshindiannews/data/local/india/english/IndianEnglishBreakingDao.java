package com.easysoftbd.bangladeshindiannews.data.local.india.english;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface IndianEnglishBreakingDao {

    @Query("SELECT * FROM indian_english_breaking ORDER BY serial ASC")
    LiveData<List<IndianEnglishBreaking>> getAllNews();

    @Query("SELECT * FROM indian_english_breaking WHERE serial=:serial")
    IndianEnglishBreaking getNews(int serial);

    @Insert
    void insertNews(IndianEnglishBreaking indianEnglishBreaking);

    @Update
    void updateNews(IndianEnglishBreaking indianEnglishBreaking);

    @Update
    void updateNews(IndianEnglishBreaking first, IndianEnglishBreaking second);


}

