package com.easysoftbd.bangladeshindiannews.data.local.india.english;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface IndianEnglishFinanceDao {

    @Query("SELECT * FROM indian_english_finance ORDER BY serial ASC")
    LiveData<List<IndianEnglishFinance>> getAllNews();

    @Query("SELECT * FROM indian_english_finance WHERE serial=:serial")
    IndianEnglishFinance getNews(int serial);

    @Insert
    void insertNews(IndianEnglishFinance indianEnglishFinance);

    @Update
    void updateNews(IndianEnglishFinance indianEnglishFinance);

    @Update
    void updateNews(IndianEnglishFinance first, IndianEnglishFinance second);


}

