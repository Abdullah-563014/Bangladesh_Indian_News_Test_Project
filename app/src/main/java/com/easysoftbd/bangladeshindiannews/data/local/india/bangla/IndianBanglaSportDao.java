package com.easysoftbd.bangladeshindiannews.data.local.india.bangla;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface IndianBanglaSportDao {

    @Query("SELECT * FROM indian_bangla_sport ORDER BY serial ASC")
    LiveData<List<IndianBanglaSport>> getAllNews();

    @Query("SELECT * FROM indian_bangla_sport WHERE serial=:serial")
    IndianBanglaSport getNews(int serial);

    @Insert
    void insertNews(IndianBanglaSport indianBanglaSport);

    @Update
    void updateNews(IndianBanglaSport indianBanglaSport);

    @Update
    void updateNews(IndianBanglaSport first, IndianBanglaSport second);
}
