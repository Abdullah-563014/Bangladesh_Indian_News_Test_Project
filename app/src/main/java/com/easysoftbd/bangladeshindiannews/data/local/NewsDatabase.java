package com.easysoftbd.bangladeshindiannews.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdBreaking;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdBreakingDao;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdEntertainment;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdEntertainmentDao;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdFinance;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdFinanceDao;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdInternational;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdInternationalDao;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdSports;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdSportsDao;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdTvChannel;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdTvChannelDao;

@Database(entities = {BdBreaking.class, BdSports.class, BdFinance.class, BdInternational.class, BdTvChannel.class, BdEntertainment.class}, version = 1, exportSchema = false)
public abstract class NewsDatabase extends RoomDatabase {
    public abstract BdBreakingDao bdBreakingDao();
    public abstract BdSportsDao bdSportsDao();
    public abstract BdFinanceDao bdFinanceDao();
    public abstract BdInternationalDao bdInternationalDao();
    public abstract BdTvChannelDao bdTvChannelDao();
    public abstract BdEntertainmentDao bdEntertainmentDao();



}
