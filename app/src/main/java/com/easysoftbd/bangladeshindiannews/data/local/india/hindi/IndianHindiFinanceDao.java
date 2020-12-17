package com.easysoftbd.bangladeshindiannews.data.local.india.hindi;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdBreaking;

import java.util.List;

@Dao
public interface IndianHindiFinanceDao {

    @Query("SELECT * FROM indian_hindi_finance ORDER BY serial ASC")
    LiveData<List<IndianHindiFinance>> getAllNews();

    @Query("SELECT * FROM indian_hindi_finance WHERE serial=:serial")
    IndianHindiFinance getNews(int serial);

    @Query("SELECT * FROM indian_hindi_finance WHERE notificationStatus=:tag")
    List<IndianHindiFinance> getAllNotificationNews(String tag);

    @Insert
    void insertNews(IndianHindiFinance indianHindiFinance);

    @Update
    void updateNews(IndianHindiFinance indianHindiFinance);

    @Update
    void updateNews(IndianHindiFinance first, IndianHindiFinance second);
}
