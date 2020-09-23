package com.easysoftbd.bangladeshindiannews.data.local.india.bangla;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdTvChannel;

import java.util.List;

@Dao
public interface IndianBanglaBreakingDao {

    @Query("SELECT * FROM indian_bangla_breaking ORDER BY serial ASC")
    LiveData<List<IndianBanglaBreaking>> getAllNews();

    @Query("SELECT * FROM indian_bangla_breaking WHERE serial=:serial")
    IndianBanglaBreaking getNews(int serial);

    @Insert
    void insertNews(IndianBanglaBreaking indianBanglaBreaking);

    @Update
     void updateNews(IndianBanglaBreaking indianBanglaBreaking);

    @Update
    void updateNews(IndianBanglaBreaking first, IndianBanglaBreaking second);
}