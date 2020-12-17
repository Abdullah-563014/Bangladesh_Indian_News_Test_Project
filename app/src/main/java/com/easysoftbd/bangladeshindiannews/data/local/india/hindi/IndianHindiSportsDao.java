package com.easysoftbd.bangladeshindiannews.data.local.india.hindi;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdBreaking;

import java.util.List;

@Dao
public interface IndianHindiSportsDao {

    @Query("SELECT * FROM indian_hindi_sports ORDER BY serial ASC")
    LiveData<List<IndianHindiSports>> getAllNews();

    @Query("SELECT * FROM indian_hindi_sports WHERE serial=:serial")
    IndianHindiSports getNews(int serial);

    @Query("SELECT * FROM indian_hindi_sports WHERE notificationStatus=:tag")
    List<IndianHindiSports> getAllNotificationNews(String tag);

    @Insert
    void insertNews(IndianHindiSports indianHindiSports);

    @Update
    void updateNews(IndianHindiSports indianHindiSports);

    @Update
    void updateNews(IndianHindiSports first, IndianHindiSports second);
}
