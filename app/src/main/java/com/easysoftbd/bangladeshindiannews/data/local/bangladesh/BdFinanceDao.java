package com.easysoftbd.bangladeshindiannews.data.local.bangladesh;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BdFinanceDao {

    @Query("SELECT * FROM bd_finance")
    List<BdFinance> getAllNews();

    @Query("SELECT * FROM bd_finance WHERE serial=:serial")
    BdFinance getNews(int serial);

    @Insert
    void insertNews(BdFinance bdFinance);

    @Update
    void updateNews(BdFinance bdFinance);

    @Update
    void updateNews(BdFinance first, BdFinance second);
}
