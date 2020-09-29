package com.easysoftbd.bangladeshindiannews.data.local.india.hindi;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface IndianHindiBreakingDao {

    @Query("SELECT * FROM indian_hindi_breaking ORDER BY serial ASC")
    LiveData<List<IndianHindiBreaking>> getAllNews();

    @Query("SELECT * FROM indian_hindi_breaking WHERE serial=:serial")
    IndianHindiBreaking getNews(int serial);

    @Insert
    void insertNews(IndianHindiBreaking indianHindiBreaking);

    @Update
    void updateNews(IndianHindiBreaking indianHindiBreaking);

    @Update
    void updateNews(IndianHindiBreaking first, IndianHindiBreaking second);
}
