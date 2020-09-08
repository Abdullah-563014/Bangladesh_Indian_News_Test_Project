package com.easysoftbd.bangladeshindiannews.data.local.bangladesh;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

@Dao
public interface BdEntertainmentDao {

    @Query("SELECT * FROM bd_entertainment")
    LiveData<List<BdEntertainment>> getAllNews();

    @Query("SELECT * FROM bd_entertainment WHERE serial=:serial")
    LiveData<BdEntertainment> getNews(int serial);

    @Insert
    void insertNews(BdEntertainment bdEntertainment);

    @Delete
    void deleteNews(BdEntertainment bdEntertainment);

    @Update
    void updateNews(BdEntertainment bdEntertainment);
}
