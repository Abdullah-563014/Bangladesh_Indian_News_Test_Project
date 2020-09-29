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
import com.easysoftbd.bangladeshindiannews.data.local.favourite_list.FavouriteList;
import com.easysoftbd.bangladeshindiannews.data.local.favourite_list.FavouriteListDao;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaBreaking;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaBreakingDao;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaEntertainment;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaEntertainmentDao;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaFinance;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaFinanceDao;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaInternational;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaInternationalDao;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaSport;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaSportDao;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaTvChannel;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaTvChannelDao;
import com.easysoftbd.bangladeshindiannews.data.local.india.hindi.IndianHindiBreaking;
import com.easysoftbd.bangladeshindiannews.data.local.india.hindi.IndianHindiBreakingDao;
import com.easysoftbd.bangladeshindiannews.data.local.india.hindi.IndianHindiEntertainment;
import com.easysoftbd.bangladeshindiannews.data.local.india.hindi.IndianHindiEntertainmentDao;
import com.easysoftbd.bangladeshindiannews.data.local.india.hindi.IndianHindiFinance;
import com.easysoftbd.bangladeshindiannews.data.local.india.hindi.IndianHindiFinanceDao;
import com.easysoftbd.bangladeshindiannews.data.local.india.hindi.IndianHindiInternational;
import com.easysoftbd.bangladeshindiannews.data.local.india.hindi.IndianHindiInternationalDao;
import com.easysoftbd.bangladeshindiannews.data.local.india.hindi.IndianHindiSports;
import com.easysoftbd.bangladeshindiannews.data.local.india.hindi.IndianHindiSportsDao;
import com.easysoftbd.bangladeshindiannews.data.local.india.hindi.IndianHindiTvChannel;
import com.easysoftbd.bangladeshindiannews.data.local.india.hindi.IndianHindiTvChannelDao;

@Database(entities = {BdBreaking.class, BdSports.class, BdFinance.class, BdInternational.class, BdTvChannel.class, BdEntertainment.class, IndianBanglaBreaking.class, IndianBanglaSport.class, IndianBanglaEntertainment.class, IndianBanglaFinance.class, IndianBanglaInternational.class, IndianBanglaTvChannel.class, IndianHindiBreaking.class, IndianHindiSports.class, IndianHindiFinance.class, IndianHindiEntertainment.class, IndianHindiInternational.class, IndianHindiTvChannel.class, FavouriteList.class}, version = 1, exportSchema = false)
public abstract class NewsDatabase extends RoomDatabase {
    public abstract BdBreakingDao bdBreakingDao();
    public abstract BdSportsDao bdSportsDao();
    public abstract BdFinanceDao bdFinanceDao();
    public abstract BdInternationalDao bdInternationalDao();
    public abstract BdTvChannelDao bdTvChannelDao();
    public abstract BdEntertainmentDao bdEntertainmentDao();
    public abstract IndianBanglaBreakingDao indianBanglaBreakingDao();
    public abstract IndianBanglaSportDao indianBanglaSportDao();
    public abstract IndianBanglaEntertainmentDao indianBanglaEntertainmentDao();
    public abstract IndianBanglaFinanceDao indianBanglaFinanceDao();
    public abstract IndianBanglaInternationalDao indianBanglaInternationalDao();
    public abstract IndianBanglaTvChannelDao indianBanglaTvChannelDao();
    public abstract IndianHindiBreakingDao indianHindiBreakingDao();
    public abstract IndianHindiSportsDao indianHindiSportsDao();
    public abstract IndianHindiEntertainmentDao indianHindiEntertainmentDao();
    public abstract IndianHindiFinanceDao indianHindiFinanceDao();
    public abstract IndianHindiInternationalDao indianHindiInternationalDao();
    public abstract IndianHindiTvChannelDao indianHindiTvChannelDao();


    public abstract FavouriteListDao favouriteListDao();



}
