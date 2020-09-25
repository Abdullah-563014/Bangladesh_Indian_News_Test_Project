package com.easysoftbd.bangladeshindiannews.data.local.india.bangla;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;


@Dao
public interface IndianBanglaFinanceDao {

    @Query("SELECT * FROM indian_bangla_finance ORDER BY serial ASC")
    LiveData<List<IndianBanglaFinance>> getAllNews();

    @Query("SELECT * FROM indian_bangla_finance WHERE serial=:serial")
    IndianBanglaFinance getNews(int serial);

    @Insert
    void insertNews(IndianBanglaFinance indianBanglaFinance);

    @Update
    void updateNews(IndianBanglaFinance indianBanglaFinance);

    @Update
    void updateNews(IndianBanglaFinance first, IndianBanglaFinance second);
}
