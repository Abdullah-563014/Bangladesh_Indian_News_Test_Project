package com.easysoftbd.bangladeshindiannews.data.local.india.hindi;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface IndianHindiTvChannelDao {

    @Query("SELECT * FROM indian_hindi_tv_channel ORDER BY serial ASC")
    LiveData<List<IndianHindiTvChannel>> getAllNews();

    @Query("SELECT * FROM indian_hindi_tv_channel WHERE serial=:serial")
    IndianHindiTvChannel getNews(int serial);

    @Insert
    void insertNews(IndianHindiTvChannel indianHindiTvChannel);

    @Update
    void updateNews(IndianHindiTvChannel indianHindiTvChannel);

    @Update
    void updateNews(IndianHindiTvChannel first, IndianHindiTvChannel second);
}
