package com.easysoftbd.bangladeshindiannews.data.local.india.hindi;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdBreaking;

import java.util.List;

@Dao
public interface IndianHindiInternationalDao {

    @Query("SELECT * FROM indian_hindi_international ORDER BY serial ASC")
    LiveData<List<IndianHindiInternational>> getAllNews();

    @Query("SELECT * FROM indian_hindi_international WHERE serial=:serial")
    IndianHindiInternational getNews(int serial);

    @Query("SELECT * FROM indian_hindi_international WHERE notificationStatus=:tag")
    List<IndianHindiInternational> getAllNotificationNews(String tag);

    @Insert
    void insertNews(IndianHindiInternational indianHindiInternational);

    @Update
    void updateNews(IndianHindiInternational indianHindiInternational);

    @Update
    void updateNews(IndianHindiInternational first, IndianHindiInternational second);
}
