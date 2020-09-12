package com.easysoftbd.bangladeshindiannews.data.local.favourite_list;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavouriteListDao {

    @Query("SELECT * FROM favourite_list")
    LiveData<List<FavouriteList>> getAll();

    @Insert
    void insert(FavouriteList favouriteList);

    @Delete
    void delete(FavouriteList favouriteList);
}
