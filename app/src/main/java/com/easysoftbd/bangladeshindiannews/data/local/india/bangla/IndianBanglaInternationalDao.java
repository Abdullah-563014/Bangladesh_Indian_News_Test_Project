package com.easysoftbd.bangladeshindiannews.data.local.india.bangla;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface IndianBanglaInternationalDao {

    @Query("SELECT * FROM indian_bangla_international ORDER BY serial ASC")
    LiveData<List<IndianBanglaInternational>> getAllNews();

    @Query("SELECT * FROM indian_bangla_international WHERE serial=:serial")
    IndianBanglaInternational getNews(int serial);

    @Insert
    void insertNews(IndianBanglaInternational indianBanglaInternational);

    @Update
    void updateNews(IndianBanglaInternational indianBanglaInternational);

    @Update
    void updateNews(IndianBanglaInternational first, IndianBanglaInternational second);
}
