package com.easysoftbd.bangladeshindiannews.data.local.india.hindi;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface IndianHindiEntertainmentDao {

    @Query("SELECT * FROM indian_hindi_entertainment ORDER BY serial ASC")
    LiveData<List<IndianHindiEntertainment>> getAllNews();

    @Query("SELECT * FROM indian_hindi_entertainment WHERE serial=:serial")
    IndianHindiEntertainment getNews(int serial);

    @Insert
    void insertNews(IndianHindiEntertainment indianHindiEntertainment);

    @Update
    void updateNews(IndianHindiEntertainment indianHindiEntertainment);

    @Update
    void updateNews(IndianHindiEntertainment first, IndianHindiEntertainment second);
}
