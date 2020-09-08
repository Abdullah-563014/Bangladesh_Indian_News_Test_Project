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
public interface BdBreakingDao {

    @Query("SELECT * FROM bd_breaking ORDER BY serial ASC")
    LiveData<List<BdBreaking>> getAllNews();

    @Query("SELECT * FROM bd_breaking WHERE serial=:serial")
    BdBreaking getNews(int serial);

    @Insert
    void insertNews(BdBreaking bdBreaking);

    @Delete
    void deleteNews(BdBreaking bdBreaking);

    @Update
    void updateNews(BdBreaking bdBreaking);
}
