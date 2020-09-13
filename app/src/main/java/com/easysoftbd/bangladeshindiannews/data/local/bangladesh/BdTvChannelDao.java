package com.easysoftbd.bangladeshindiannews.data.local.bangladesh;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BdTvChannelDao {

    @Query("SELECT * FROM bd_tv_channel")
    LiveData<List<BdTvChannel>> getAllNews();

    @Query("SELECT * FROM bd_tv_channel WHERE serial=:serial")
    BdTvChannel getNews(int serial);

    @Insert
    void insertNews(BdTvChannel bdTvChannel);

    @Update
    void updateNews(BdTvChannel bdTvChannel);

    @Update
    void updateNews(BdTvChannel first, BdTvChannel second);
}
