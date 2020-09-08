package com.easysoftbd.bangladeshindiannews.data.local.bangladesh;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BdSportsDao {

    @Query("SELECT * FROM bd_sports")
    List<BdSports> getAllNews();

    @Query("SELECT * FROM bd_sports WHERE serial=:serial")
    BdSports getNews(int serial);

    @Insert
    void insertNews(BdSports bdSports);

    @Delete
    void deleteNews(BdSports bdSports);

    @Update
    void updateNews(BdSports bdSports);
}

