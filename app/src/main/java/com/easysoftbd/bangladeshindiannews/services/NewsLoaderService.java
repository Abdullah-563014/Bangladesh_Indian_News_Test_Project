package com.easysoftbd.bangladeshindiannews.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.easysoftbd.bangladeshindiannews.R;
import com.easysoftbd.bangladeshindiannews.data.local.DatabaseClient;
import com.easysoftbd.bangladeshindiannews.data.local.NewsDatabase;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdBreaking;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdEntertainment;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdFinance;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdInternational;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdSports;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdTvChannel;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaBreaking;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaEntertainment;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaFinance;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaInternational;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaSport;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaTvChannel;
import com.easysoftbd.bangladeshindiannews.data.local.india.english.IndianEnglishBreaking;
import com.easysoftbd.bangladeshindiannews.data.local.india.english.IndianEnglishEntertainment;
import com.easysoftbd.bangladeshindiannews.data.local.india.english.IndianEnglishFinance;
import com.easysoftbd.bangladeshindiannews.data.local.india.english.IndianEnglishInternational;
import com.easysoftbd.bangladeshindiannews.data.local.india.english.IndianEnglishSports;
import com.easysoftbd.bangladeshindiannews.data.local.india.english.IndianEnglishTvChannel;
import com.easysoftbd.bangladeshindiannews.data.local.india.hindi.IndianHindiBreaking;
import com.easysoftbd.bangladeshindiannews.data.local.india.hindi.IndianHindiEntertainment;
import com.easysoftbd.bangladeshindiannews.data.local.india.hindi.IndianHindiFinance;
import com.easysoftbd.bangladeshindiannews.data.local.india.hindi.IndianHindiInternational;
import com.easysoftbd.bangladeshindiannews.data.local.india.hindi.IndianHindiSports;
import com.easysoftbd.bangladeshindiannews.data.local.india.hindi.IndianHindiTvChannel;
import com.easysoftbd.bangladeshindiannews.data.model.NewsAndLinkModel;
import com.easysoftbd.bangladeshindiannews.data.model.RecyclerItemModel;
import com.easysoftbd.bangladeshindiannews.data.network.MyUrl;
import com.easysoftbd.bangladeshindiannews.data.repositories.MyResponse;
import com.easysoftbd.bangladeshindiannews.ui.activities.my_webview.WebViewActivity;
import com.easysoftbd.bangladeshindiannews.ui.fragments.breaking_news.BreakingNewsFragment;
import com.easysoftbd.bangladeshindiannews.utils.CommonMethods;
import com.easysoftbd.bangladeshindiannews.utils.Constants;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.easysoftbd.bangladeshindiannews.ui.MyApplication.MY_NOTIFICATION_CHANNEL_ID;

public class NewsLoaderService extends Worker {

    private String notificationId="MyNotification";
    private MyResponse myResponse;
    private NewsDatabase newsDatabase;
    private CompositeDisposable compositeDisposable;
    private String newsPaperUrl,countryName,languageName;

    private List<BdBreaking> bdBreakingList = new ArrayList<>();
    private List<BdEntertainment> bdEntertainmentList = new ArrayList<>();
    private List<BdFinance> bdFinanceList = new ArrayList<>();
    private List<BdInternational> bdInternationalList = new ArrayList<>();
    private List<BdSports> bdSportsList = new ArrayList<>();
    private List<BdTvChannel> bdTvChannelList = new ArrayList<>();
    private List<IndianBanglaBreaking> indianBanglaBreakingList = new ArrayList<>();
    private List<IndianBanglaEntertainment> indianBanglaEntertainmentList = new ArrayList<>();
    private List<IndianBanglaFinance> indianBanglaFinanceList = new ArrayList<>();
    private List<IndianBanglaInternational> indianBanglaInternationalList = new ArrayList<>();
    private List<IndianBanglaSport> indianBanglaSportList = new ArrayList<>();
    private List<IndianBanglaTvChannel> indianBanglaTvChannelList = new ArrayList<>();
    private List<IndianHindiBreaking> indianHindiBreakingList = new ArrayList<>();
    private List<IndianHindiEntertainment> indianHindiEntertainmentList = new ArrayList<>();
    private List<IndianHindiFinance> indianHindiFinanceList = new ArrayList<>();
    private List<IndianHindiInternational> indianHindiInternationalList = new ArrayList<>();
    private List<IndianHindiSports> indianHindiSportsList = new ArrayList<>();
    private List<IndianHindiTvChannel> indianHindiTvChannelList = new ArrayList<>();
    private List<IndianEnglishBreaking> indianEnglishBreakingList = new ArrayList<>();
    private List<IndianEnglishEntertainment> indianEnglishEntertainmentList = new ArrayList<>();
    private List<IndianEnglishFinance> indianEnglishFinanceList = new ArrayList<>();
    private List<IndianEnglishInternational> indianEnglishInternationalList = new ArrayList<>();
    private List<IndianEnglishSports> indianEnglishSportsList = new ArrayList<>();
    private List<IndianEnglishTvChannel> indianEnglishTvChannelList = new ArrayList<>();


    public NewsLoaderService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        if (myResponse==null) {
            myResponse=new MyResponse();
        }
        if (compositeDisposable==null) {
            compositeDisposable=new CompositeDisposable();
        }
        if (newsDatabase==null) {
            newsDatabase=DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();
        }
        countryName=Constants.bangladesh;
        languageName=Constants.bangla;
    }

    @NonNull
    @Override
    public Result doWork() {
        getCountryAndLanName();
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            loadBangladeshBreakingNewsDataFromDb();
            loadBangladeshSportsNewsDataFromDb();
            loadBangladeshEntertainmentNewsDataFromDb();
            loadBangladeshFinanceNewsDataFromDb();
            loadBangladeshTvChannelNewsDataFromDb();
            loadBangladeshInternationalNewsDataFromDb();
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            loadIndianBanglaBreakingNewsDataFromDb();
            loadIndianBanglaSportsNewsDataFromDb();
            loadIndianBanglaEntertainmentNewsDataFromDb();
            loadIndianBanglaFinanceNewsDataFromDb();
            loadIndianBanglaTvChannelNewsDataFromDb();
            loadIndianBanglaInternationalNewsDataFromDb();
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            loadIndianHindiBreakingNewsDataFromDb();
            loadIndianHindiSportsNewsDataFromDb();
            loadIndianHindiEntertainmentNewsDataFromDb();
            loadIndianHindiFinanceNewsDataFromDb();
            loadIndianHindiTvChannelNewsDataFromDb();
            loadIndianHindiInternationalNewsDataFromDb();
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            loadIndianEnglishBreakingNewsDataFromDb();
            loadIndianEnglishSportsNewsDataFromDb();
            loadIndianEnglishEntertainmentNewsDataFromDb();
            loadIndianEnglishFinanceNewsDataFromDb();
            loadIndianEnglishTvChannelNewsDataFromDb();
            loadIndianEnglishInternationalNewsDataFromDb();
        }
        return Result.success();
    }

    @Override
    public void onStopped() {
        super.onStopped();
        compositeDisposable.dispose();
    }

    private void getCountryAndLanName() {
        countryName=CommonMethods.getStringFromSharedPreference(getApplicationContext(),Constants.countryNameKey,Constants.bangladesh);
        languageName=CommonMethods.getStringFromSharedPreference(getApplicationContext(),Constants.languageNameKey,Constants.bangla);
    }

    public void loadPageDocument(String pageUrl) {
        myResponse.getPageDocument(pageUrl)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.rxjava3.core.Observer<Document>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull Document document) {
                        if (document.baseUri().equalsIgnoreCase(MyUrl.bdProtidin)) {
                            setBdProtidinBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.prothomAlo)) {
                            setProthomAloBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.kalerKhanto)) {
                            setKalerKhantoBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.somokal)) {
                            setSomokalBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyJonoKhanto)) {
                            setDailyJanaKhantoBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bhorerKagoj)) {
                            setBhorerKagojBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyInqilab)) {
                            setDailyInqilabBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyNayaDiganta)) {
                            setDailyNayaDigantaBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.amarDesh24)) {
                            setAmarDesh24BreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyIttefaq)) {
                            setDailyIttefaqBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase("https://mzamin.com/")) {
                            setManobJominBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.songbadProtidin)) {
                            setSongbadProtidinBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.manobKantha)) {
                            setManobKanthaBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bangladeshJournal)) {
                            setBangladeshJournalBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.theDailyVorerPata)) {
                            setTheDailyVorerPataBreekingNews(document);
                        }//Bangladeshi breaking news method called above.
                        else if (document.baseUri().equalsIgnoreCase(MyUrl.anandaBazarBreakingNews)) {
                            setAnandaBazarBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.sangbadPratidinBreakingNews)) {
                            setSangbadPratidinBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bartamanPatrikaBreakingNews)) {
                            setBartamanPatrikaBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.ganashaktiBreakingNews)) {
                            setGanaShaktiBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.uttarBangaSambadBreakingNews)) {
                            setUttarBangaSambadBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.ebelaBreakingNews)) {
                            setEbelaBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.asomiyaPratidinBreakingNews)) {
                            setAsomiyaPratidinBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.aajkaalBreakingNews)) {
                            setAajKaalBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.khaborOnlinBreakingNews)) {
                            setKhaborOnlineBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.jugasankhaBreakingNews)) {
                            setJugaShankaBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.jagarantripuraBreakingNews)) {
                            setJagaranTripuraBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.ganadabiBreakingNews)) {
                            setGanadabiBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.oneIndiaBanglaBreakingNews)) {
                            setOneIndiaBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.kolkata247BreakingNews)) {
                            setKolkata247BreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.khabor24BreakingNews)) {
                            setKhabor24GhontaBreekingNews(document);
                        }//Indian Bangla breaking news method called above.
                        else if (document.baseUri().equalsIgnoreCase(MyUrl.jagranBreakingNews)) {
                            setJagranBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bhaskarBreakingNews)) {
                            setBhaskarBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.amarUjalaBreakingNews)) {
                            setAmarUjalaBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.liveHindustanBreakingNews)) {
                            setLiveHindustanBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.navBharatTimesBreakingNews)) {
                            setNavBharatTimesBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.janSattaBreakingNews)) {
                            setJanSattaBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.punjabKeSariBreakingNews)) {
                            setPunjabKeSariBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.hariBhoomiBreakingNews)) {
                            setHariBhoomiBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.khasKhabarBreakingNews)) {
                            setKhasKhabarBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.divyaHimachalBreakingNews)) {
                            setDivyaHimachalBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.prabhaSakshiBreakingNews)) {
                            setPrabhaSakshiBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.deshDootBreakingNews)) {
                            setDeshDootBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dainikTribuneOnlineBreakingNews)) {
                            setDainikTribuneOnlineBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.samacharJagatBreakingNews)) {
                            setSamacharJagatBreakingNews(document);
                        }//Indian english breaking news method called above.
                        else if (document.baseUri().equalsIgnoreCase(MyUrl.hindustanTimesBreakingNews)) {
                            setHindustanTimesBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.indianExpressEnglishBreakingNews)) {
                            setIndianExpressBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyPioneerBreakingNews)) {
                            setDailyPioneerBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.deccanHeraldBreakingNews)) {
                            setDeccanHeraldBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dnaIndiaHeadlinesBreakingNews)) {
                            setDnaIndiaBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.deccanChronicleBreakingNews)) {
                            setDeccanChronicleBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.asianAgeBreakingNews)) {
                            setAsianAgeBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.economicTimesBreakingNews)) {
                            setEconomicTimesBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.businessStandardBreakingNews)) {
                            setBusinessStandardBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.financialExpressBreakingNews)) {
                            setFinancialExpressBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.newIndianExpressBreakingNews)) {
                            setNewIndianExpressBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.tribuneIndiaBreakingNews)) {
                            setTribuneIndiaBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.liveMintBreakingNews)) {
                            setLiveMintBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.kalerKhanto)){
                            setKalerkanthoEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.samakalEntertainment)){
                            setSamakalEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyJanakanthaEntertainment)){
                            setDailyJanakanthaEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bdNews24Entertainment)){
                            setBdNews24EntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.banglaTribuneEntertainment)){
                            setBanglaTribuneEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bhorerKagojEntertainment)){
                            setBhorerKagojEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyInqilabEntertainment)){
                            setDailyInqilabEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyNayaDigantaEntertainment)){
                            setDailyNayaDigantaEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.amarDesh24Entertainment)){
                            setAmarDesh24EntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyIttefaqEntertainment)){
                            setDailyIttefaqEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase("https://mzamin.com/category.php?cid=5")){
                            setDailyManobJominEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.sangbadpratidinEntertainment)){
                            setSangbadPratidinEntertainmentNews(document);
                            setSangbadPratidinIndianEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.manobkanthaEntertainment)){
                            setManobKanthaEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bdJournalEntertainment)){
                            setBdJournalEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dainikAmaderShomoyEntertainment)){
                            setDailyAmaderShomoyEntertainmentNews(document);
                        }// Bangladeshi entertainment news papers link are staying above.
                        else if (document.baseUri().equalsIgnoreCase(MyUrl.anandaBazarEntertainmentNews)){
                            setAnandaBazarEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.sangbadPratidinEntertainmentNews)){
                            setSangbadPratidinIndianEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bartamanPatrikaEntertainmentNews)){
                            setBartamanPatrikaEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.uttarBangaSambadEntertainmentNews)){
                            setUttarBangaSambadEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.ebelaEntertainmentNews)){
                            setEbelaEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.asomiyaPratidinEntertainmentNews)){
                            setAsomiyaPratidinEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.aajKaalEntertainmentNews)){
                            setAajKaalEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.khaborOnlineEntertainmentNews)){
                            setKhaborOnlineEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.jugaSankhaEntertainmentNews)){
                            setJugaSankhaEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.jagaranTripuraEntertainmentNews)){
                            setJagaranTripuraEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.oneIndiaEntertainmentNews)){
                            setOneIndiaEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.kolkata247EntertainmentNews)){
                            setKolkata247EntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.khabor24EntertainmentNews)){
                            setKhabor24EntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bengal2DayEntertainmentNews)){
                            setBengal2DayEntertainmentNews(document);
                        }// Indian Bangla entertainment news papers link are staying above.
                        else if (document.baseUri().equalsIgnoreCase(MyUrl.jagranEntertainmentNews)){
                            setJagranEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bhaskarEntertainmentNews)){
                            setBhaskarEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.amarUjalaEntertainmentNews)){
                            setAmarUjalaEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.liveHindustanEntertainmentNews)){
                            setLiveHindustanEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.navBharatTimesEntertainmentNews)){
                            setNavBharatTimesEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.janSattaEntertainmentNews)){
                            setJanSattaEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bollywoodTadkaEntertainmentNews)){
                            setBollywoodTadkaEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.hariBhoomiEntertainmentNews)){
                            setHariBhoomiEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.khasKhabarEntertainmentNews)){
                            setKhasKhabarEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.divyaHimachalEntertainmentNews)){
                            setDivyaHimachalEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.prabhaSakshiEntertainmentNews)){
                            setPrabhaSakshiEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dainikTribuneOnlineEntertainmentNews)){
                            setDainikTribuneOnlineEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.samacharJagatEntertainmentNews)){
                            setSamacharJagatEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.patrikaEntertainmentNews)){
                            setPatrikaEntertainmentNews(document);
                        }// Indian Hindi entertainment news papers link are staying above.
                        else if (MyUrl.hindustanTimesEntertainmentNews.contains(document.baseUri())){
                            setHindustanTimesEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.indianExpressEntertainmentNews)){
                            setIndianExpressEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyPioneerEntertainmentNews)){
                            setDailyPioneerEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.deccanHeraldEntertainmentNews)){
                            setDeccanHeraldEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dnaIndiaEntertainmentNews)){
                            setDnaIndiaEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.deccanChronicleEntertainmentNews)){
                            setDeccanChronicleEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.asianAgeEntertainmentNews)){
                            setTheAsianAgeEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.financialExpressEntertainmentNews)){
                            setFinancialExpressEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.newIndianExpressEntertainmentNews)){
                            setNewIndianExpressEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.tribuneIndiaEntertainmentNews)){
                            setTribuneIndiaEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.kalerKhanto)){
                            setKalerKanthoFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.samakalFinance)){
                            setSamakalFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyJanakanthaFinance)){
                            setDailyJanakanthaFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bdNews24Finance)){
                            setBdNews24FinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.banglaTribuneFinance)){
                            setBanglaTribuneFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bhorerKagojFinance)){
                            setBhorerKagojFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyIttefaqFinance)){
                            setDailyIttefaqFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.manobKanthaFinance)){
                            setManobKanthaFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bdJournalFinance)){
                            setBdJournalFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyVorerPataFinance)){
                            setDailyVorerPataFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.jaiJaiDinBdFinance)){
                            setJaiJaiDinBdFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dainikAmaderShomoyFinance)){
                            setDainikAmaderShomoyFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailySangramFinance)){
                            setDailySangramFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.amarDesh24Finance)){
                            setAmarDesh24FinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.jugantorFinance)){
                            setJugantorFinanceNews(document);
                        }// bangladeshi finance news link are staying above.
                        else if (document.baseUri().equalsIgnoreCase(MyUrl.anandaBazarFinanceNews)){
                            setAnandaBazarFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bartamanPatrikaFinanceNews)){
                            setBartamanPatrikaFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.ganashaktiFinanceNews)){
                            setGanashaktiFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.uttarBangaSambadFinanceNews)){
                            setUttarBangaSambadFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.ebelaFinanceNews)){
                            setEbelaFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.asomiyaPratidinFinanceNews)){
                            setAsomiyaPratidinFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.aajKaalFinanceNews)){
                            setAajKaalFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.khaborOnlineFinanceNews)){
                            setKhaborOnlineFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.jugasankhaFinanceNews)){
                            setJugasankhaFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.jagaranTripuraFinanceNews)){
                            setJagaranTripuraFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.kolkata247FinanceNews)){
                            setKolkata247FinanceNews(document);
                        }// Indian bangla finance news link are staying above.
                        else if (document.baseUri().equalsIgnoreCase(MyUrl.jagranFinanceNews)){
                            setJagranFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bhaskarFinanceNews)){
                            setBhaskarFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.amarUjalaFinanceNews)){
                            setAmarUjalaFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.liveHindustanFinanceNews)){
                            setLiveHindustanFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.navBharatTimesFinanceNews)){
                            setNavBharatTimesFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.janSattaFinanceNews)){
                            setJanSattaFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.punjabKesariFinanceNews)){
                            setPunjabKesariFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.khasKhabarFinanceNews)){
                            setKhasKhabarFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.divyaHimachalFinanceNews)){
                            setDivyaHimachalFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.prabhaSakshiFinanceNews)){
                            setPrabhaSakshiFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dainikTribuneOnlineFinanceNews)){
                            setDainikTribuneOnlineFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.samacharJagatFinanceNews)){
                            setSamacharJagatFinanceNews(document);
                        }// Indian hindi finance news link are staying above.
                        else if (MyUrl.hindustanTimesFinanceNews.contains(document.baseUri())){
                            setHindustanTimesFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.indianExpressFinanceNews)){
                            setIndianExpressFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyPioneerFinanceNews)){
                            setDailyPioneerFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.deccanHeraldFinanceNews)){
                            setDeccanHeraldFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dnaIndiaFinanceNews)){
                            setDnaIndiaFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.deccanChronicleFinanceNews)){
                            setDeccanChronicleFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.asianAgeFinanceNews)){
                            setAsianAgeFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.economicTimesFinanceNews)){
                            setEconomicsTimesFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.businessStandardFinanceNews)){
                            setBusinessStandardFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.financialExpressFinanceNews)){
                            setFinancialExpressFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.newIndianExpressFinanceNews)){
                            setNewIndianExpressFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.tribuneIndiaFinanceNews)){
                            setTribuneIndiaFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.liveMintFinanceNews)){
                            setLiveMintFinanceNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.kalerKhanto)){
                            setKalerKanthoInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.samakalInternational)){
                            setSamakalInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyJanakanthaInternational)){
                            setDailyJanakanthaInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bdNews24International)){
                            setBdNews24InternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.banglaTribuneInternational)){
                            setBanglaTribuneInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bhorerkagojInternational)){
                            setBhorerKagojInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyInqilabInternational)){
                            setDailyInqilabInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyNayadigantaInternational)){
                            setDailyNayadigantaInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.amarDesh24International)){
                            setAmarDesh24InternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyIttefaqInternational)){
                            setDailyIttefaqInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase("https://mzamin.com/category.php?cid=8")){
                            setManobZaminInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.sangbadpratidinInternational)){
                            setSangbadPratidinInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.manobKanthaInternational)){
                            setManobKanthaInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bdJournalInternational)){
                            setBdJournalInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyVorerPataInternational)){
                            setDailyVorerPataInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dainikAmaderShomoyInternational)){
                            setDailyAmaderShomoyInternationalNews(document);
                        }// bangladeshi international news link are staying above.
                        else if (document.baseUri().equalsIgnoreCase(MyUrl.anandaBazarInternationalNews)){
                            setAnandaBazarInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.sangbadPratidinInternationalNews)){
                            setSangbadPratidinIndiaInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bartamanPatrikaInternationalNews)){
                            setBartamanPatrikaInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.ganaShaktiInternationalNews)){
                            setGanaShaktiInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.uttarBangaSambadInternationalNews)){
                            setUttarBangaSambadInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.ebelaInternationalNews)){
                            setEbelaInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.aajKaalInternationalNews)){
                            setAajKaalInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.khaborOnlineInternationalNews)){
                            setKhaborOnlineInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.jugaSankhaInternationalNews)){
                            setJugaSankhaInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.jagaranTripuraInternationalNews)){
                            setJagaranTripuraInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.oneIndiaInternationalNews)){
                            setOneIndiaInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.kolkata247InternationalNews)){
                            setKolkata247InternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bengal2DayInternationalNews)){
                            setBengal2DayInternationalNews(document);
                        }// Indian bangla news link are staying above.
                        else if (document.baseUri().equalsIgnoreCase(MyUrl.jagranInternationalNews)){
                            setJagranInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bhaskarInternationalNews)){
                            setBhaskarInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.amarUjalaInternationalNews)){
                            setAmarUjalaInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.liveHindustanInternationalNews)){
                            setLiveHindustanInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.navBharatTimesInternationalNews)){
                            setNavBharatTimesInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.janSattaInternationalNews)){
                            setJanSattaInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.punjabKesariInternationalNews)){
                            setPunjabKesariInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.khasKhabarInternationalNews)){
                            setKhasKhabarInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.prabhaSakshiInternationalNews)){
                            setPrabhaSakshiInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dainikTribuneOnlineInternationalNews)){
                            setDainikTribuneOnlineInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.samacharJagatInternationalNews)){
                            setSamacharJagatInternationalNews(document);
                        }// Indian hindi international news link are staying above.
                        else if (MyUrl.hindustanTimesInternationalNews.contains(document.baseUri())){
                            setHindustanTimesInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.indianExpressInternationalNews)){
                            setIndianExpressInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyPioneerInternationalNews)){
                            setDailyPioneerInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.deccanHeraldInternationalNews)){
                            setDeccanHeraldInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dnaIndiaInternationalNews)){
                            setDnaIndiaInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.deccanChronicleInternationalNews)){
                            setDeccanChronicleInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.asianAgeInternationalNews)){
                            setAsianAgeInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.economicTimesInternationalNews)){
                            setEconomicTimesInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.businessStandardInternationalNews)){
                            setBusinessStandardInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.financialExpressInternationalNews)){
                            setFinancialExpressInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.newIndianExpressInternationalNews)){
                            setNewIndianExpressInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.tribuneIndiaInternationalNews)){
                            setTribuneIndiaInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.liveMintInternationalNews)){
                            setLiveMintInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.kalerKhanto)) {
                            setKalerkhanthoSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.samakalSports)) {
                            setSamakalSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyJanakanthaSports)) {
                            setDailyJanakanthaSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bdNews24Sports)) {
                            setBdNews24SportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.banglaTribunesSports)) {
                            setBanglaTribuneSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bhorerKagojSports)) {
                            setBhorerKagojSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyInqilabSports)) {
                            setDailyInqilabSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyNayaDigantaSports)) {
                            setDailyNayaDigantaSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.amarDesh24Sports)) {
                            setAmarDesh24SportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyIttefaqSports)) {
                            setDailyIttefaqSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase("https://mzamin.com/category.php?cid=4")) {
                            setDailyManobJominSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.sangbadPratidinSports)) {
                            setSangbadPratidinSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.manobKantha)) {
                            setManobKanthaSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bangladeshJournal)) {
                            setBdJournalSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailySangramSports)) {
                            setDailySangramSportNews(document);
                        }// bangladesh sport news papers list are staying above.
                        else if (document.baseUri().equalsIgnoreCase(MyUrl.anandaBazarSportsNews)) {
                            setAnandaBazarSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.sangbadPratidinSportsNews)) {
                            setSangbadPratidinIndianSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bartamanPatrikaSportsNews)) {
                            setBartamanPatrikaSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.ganashaktiSportsNews)) {
                            setGanaShaktiSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.uttarBangaSambadSportsNews)) {
                            setUttarBangaSambadSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.ebelaSportsNews)) {
                            setEbelaSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.asomiyaPratidinSportsNews)) {
                            setAsomiyaPratidinSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.aajKaalSportsNews)) {
                            setAajKaalSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.khaborOnlineSportsNews)) {
                            setKhaborOnlineSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.jugasankhaSportsNews)) {
                            setJugaSankhaSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.jagaranTripuraSportsNews)) {
                            setJagaranTripuraSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.oneIndiaSportsNews)) {
                            setOneIndiaSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.kolkata247SportsNews)) {
                            setKolkata247SportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.khabor24SportsNews)) {
                            setKhabor24SportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bengal2DaySportsNews)) {
                            setBengal2DaySportNews(document);
                        }// Indian bangla sports news papers list are staying above.
                        else if (document.baseUri().equalsIgnoreCase(MyUrl.jagranSportsNews)) {
                            setJagranSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bhaskarSportsNews)) {
                            setBhaskarSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.amarUjalaSportsNews)) {
                            setAmarUjalaSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.liveHindustanSportsNews)) {
                            setLiveHindustanSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.navBharatTimesSportsNews)) {
                            setNavBharatTimesSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.janSattaSportsNews)) {
                            setJanSattaSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.punjabKesariSportsNews)) {
                            setPunjabKesariSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.hariBhoomiSportsNews)) {
                            setHariBhoomiSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.khasKhabarSportsNews)) {
                            setKhasKhabarSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.divyaHimachalSportsNews)) {
                            setDivyaHimachalSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.prabhaSakshiSportsNews)) {
                            setPrabhaSakshiSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dainikTribuneOnlineSportsNews)) {
                            setDainikTribuneOnlineSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.samacharJagatSportsNews)) {
                            setSamacharJagatSportNews(document);
                        }// Indian hindi sports news papers list are staying above.
                        else if (MyUrl.hindustanTimesSportsNews.contains(document.baseUri())) {
                            setHindustanTimesSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.indianExpressSportsNews)) {
                            setIndianExpressSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyPioneerSportsNews)) {
                            setDailyPioneerSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.deccanHeraldSportsNews)) {
                            setDeccanHeraldSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dnaIndiaSportsNews)) {
                            setDnaIndiaSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.deccanChronicleSportsNews)) {
                            setDeccanChronicleSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.asianAgeSportsNews)) {
                            setAsianAgeSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.economicTimesSportsNews)) {
                            setEconomicTimesSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.businessStandardSportsNews)) {
                            setBusinessStandardSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.financialExpressSportsNews)) {
                            setFinancialExpressSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.newIndianExpressSportsNews)) {
                            setNewIndianExpressSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.tribuneIndiaSportsNews)) {
                            setTribuneIndiaSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.ntvBd)){
                            setNtvBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.ekusheyTelevision)){
                            setEkusheyTvBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.shomoyNews)){
                            setShomoyBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.channelI)){
                            setChannelIBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.banglaVision)){
                            setBanglaVisionBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.rtvNews)){
                            setRtvBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.boishakhiTv)){
                            setBoishakhiBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.mohonaTv)){
                            setMohonaTvBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.myTvBd)){
                            setMyTvBdBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase("https://www.jamuna.tv/")){
                            setJamunaTvBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.news24Bd)){
                            setNews24BdBreakingNews(document);
                        }// bangladeshi tv chanel news link are staying above.
                        else if (document.baseUri().equalsIgnoreCase(MyUrl.zeeNews24HoursTvChannelNews)){
                            setZee24HoursTvChannelNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.abpLiveTvChannelNews)){
                            setAbpLiveTvChannelNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.news18BengaliTvChannelNews)){
                            setNews18BengaliTvChannelNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.newsTimeBanglaTvChannelNews)){
                            setNewsTimeBanglaTvChannelNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.calcuttaNewsTvChannelNews)){
                            setCalcuttaNewsTvChannelNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.kolkataTvTvChannelNews)){
                            setKolkataTvTvChannelNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.rPlusTvChannelNews)){
                            setRPlusNewsTvChannelNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.indianExpressTvChannelNews)){
                            setIndianExpressBanglaTvChannelNews(document);
                        }// indian bangla tv chanel news link are staying above.
                        else if (document.baseUri().equalsIgnoreCase(MyUrl.abpLiveHindiTvChannelNews)){
                            setAbpHindiTvChannelNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.inkhabarTvChannelNews)){
                            setInKhabarTvChannelNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.indiaTvTvChannelNews)){
                            setIndiaTvTvChannelNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.khabarNdTvTvChannelNews)){
                            setKhabarNdTvTvChannelNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.hindiNews24OnlineTvChannelNews)){
                            setHindiNews24OnlineTvChannelNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.hindiMoneyControlTvChannelNews)){
                            setHindiMoneyControlTvChannelNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.ibc24TvChannelNews)){
                            setIbc24TvChannelNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.hindiNews18TvChannelNews)){
                            setHindiNews18TvChannelNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.ddiNewsTvChannelNews)){
                            setDdiNewsTvChannelNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.newsNationTvTvChannelNews)){
                            setNewsNationTvTvChannelNews(document);
                        }// indian hindi tv chanel news link are staying above.
                        else if (document.baseUri().equalsIgnoreCase(MyUrl.bloombergQuintTvChannelNews)){
                            setBloombergQuintTvChannelNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.cnbcTv18TvChannelNews)){
                            setCnbcTv18TvChannelNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.news18TvChannelNews)){
                            setNews18TvChannelNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.timesNowNewsTvChannelNews)){
                            setTimesNowNewsTvChannelNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.ndtvTvChannelNews)){
                            setNdtvTvChannelNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.republicWorldTvChannelNews)){
                            setRepublicWorldTvChannelNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.mirrorNowTvChannelNews)){
                            setMirrorNowTvChannelNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.wioNewsTvChannelNews)){
                            setWioNewsTvChannelNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.romedyNowTvChannelNews)){
                            setRomedyNowTvChannelNews(document);
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void displayNotification(String title, String description, String targetUrl) {
        NotificationManager notificationManager= (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
        intent.putExtra(Constants.UrlTag,targetUrl);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), CommonMethods.getRandomNumber(99999), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder= null;
        if (Build.VERSION.SDK_INT >= 26) {
            builder = new NotificationCompat.Builder(getApplicationContext(),MY_NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(title)
                    .setTicker(title)
                    .setContentText(description)
                    .setColor(ContextCompat.getColor(getApplicationContext(),R.color.colorAccent))
                    .setPriority(CommonMethods.getBooleanFromSharedPreference(getApplicationContext(),Constants.notificationSoundSwitchKey,true) ? NotificationManager.IMPORTANCE_DEFAULT : NotificationManager.IMPORTANCE_MIN)
                    .setDefaults(CommonMethods.getBooleanFromSharedPreference(getApplicationContext(),Constants.notificationSoundSwitchKey,true) ? Notification.DEFAULT_ALL : Notification.DEFAULT_LIGHTS)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher);
        } else {
            builder = new NotificationCompat.Builder(getApplicationContext(),MY_NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(title)
                    .setTicker(title)
                    .setContentText(description)
                    .setColor(ContextCompat.getColor(getApplicationContext(),R.color.colorAccent))
                    .setDefaults(CommonMethods.getBooleanFromSharedPreference(getApplicationContext(),Constants.notificationSoundSwitchKey,true) ? Notification.DEFAULT_ALL : Notification.DEFAULT_LIGHTS)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher);
        }

        Notification notification=builder.build();
        if (!Constants.isUserActive && CommonMethods.getBooleanFromSharedPreference(getApplicationContext(),Constants.notificationStatusSwitchKey,true)) {
            notificationManager.notify(CommonMethods.getRandomNumber(99999),notification);
        }

    }



    public void loadBangladeshBreakingNewsDataFromDb() {
        bdBreakingList.clear();
        bdBreakingList = newsDatabase.bdBreakingDao().getAllNotificationNews("on");
        if (bdBreakingList.size()>0) {
            for (int i=0; i<bdBreakingList.size(); i++) {
                newsPaperUrl=bdBreakingList.get(i).getPaperUrl();
                loadPageDocument(newsPaperUrl);
            }
        }
    }
    public void loadBangladeshEntertainmentNewsDataFromDb() {
        bdEntertainmentList.clear();
        bdEntertainmentList = newsDatabase.bdEntertainmentDao().getAllNotificationNews("on");
        if (bdEntertainmentList.size()>0) {
            for (int i=0; i<bdEntertainmentList.size(); i++) {
                newsPaperUrl=bdEntertainmentList.get(i).getPaperUrl();
                loadPageDocument(newsPaperUrl);
            }
        }
    }
    public void loadBangladeshFinanceNewsDataFromDb() {
        bdFinanceList.clear();
        bdFinanceList = newsDatabase.bdFinanceDao().getAllNotificationNews("on");
        if (bdFinanceList.size()>0) {
            for (int i=0; i<bdFinanceList.size(); i++) {
                newsPaperUrl=bdFinanceList.get(i).getPaperUrl();
                loadPageDocument(newsPaperUrl);
            }
        }
    }
    public void loadBangladeshInternationalNewsDataFromDb() {
        bdInternationalList.clear();
        bdInternationalList = newsDatabase.bdInternationalDao().getAllNotificationNews("on");
        if (bdInternationalList.size()>0) {
            for (int i=0; i<bdInternationalList.size(); i++) {
                newsPaperUrl=bdInternationalList.get(i).getPaperUrl();
                loadPageDocument(newsPaperUrl);
            }
        }
    }
    public void loadBangladeshSportsNewsDataFromDb() {
        bdSportsList.clear();
        bdSportsList = newsDatabase.bdSportsDao().getAllNotificationNews("on");
        if (bdSportsList.size()>0) {
            for (int i=0; i<bdSportsList.size(); i++) {
                newsPaperUrl=bdSportsList.get(i).getPaperUrl();
                loadPageDocument(newsPaperUrl);
            }
        }
    }
    public void loadBangladeshTvChannelNewsDataFromDb() {
        bdTvChannelList.clear();
        bdTvChannelList = newsDatabase.bdTvChannelDao().getAllNotificationNews("on");
        if (bdTvChannelList.size()>0) {
            for (int i=0; i<bdTvChannelList.size(); i++) {
                newsPaperUrl=bdTvChannelList.get(i).getPaperUrl();
                loadPageDocument(newsPaperUrl);
            }
        }
    }

    public void loadIndianBanglaBreakingNewsDataFromDb() {
        indianBanglaBreakingList.clear();
        indianBanglaBreakingList = newsDatabase.indianBanglaBreakingDao().getAllNotificationNews("on");
        if (indianBanglaBreakingList.size()>0) {
            for (int i=0; i<indianBanglaBreakingList.size(); i++) {
                newsPaperUrl=indianBanglaBreakingList.get(i).getPaperUrl();
                loadPageDocument(newsPaperUrl);
            }
        }
    }
    public void loadIndianBanglaEntertainmentNewsDataFromDb() {
        indianBanglaEntertainmentList.clear();
        indianBanglaEntertainmentList = newsDatabase.indianBanglaEntertainmentDao().getAllNotificationNews("on");
        if (indianBanglaEntertainmentList.size()>0) {
            for (int i=0; i<indianBanglaEntertainmentList.size(); i++) {
                newsPaperUrl=indianBanglaEntertainmentList.get(i).getPaperUrl();
                loadPageDocument(newsPaperUrl);
            }
        }
    }
    public void loadIndianBanglaFinanceNewsDataFromDb() {
        indianBanglaFinanceList.clear();
        indianBanglaFinanceList = newsDatabase.indianBanglaFinanceDao().getAllNotificationNews("on");
        if (indianBanglaFinanceList.size()>0) {
            for (int i=0; i<indianBanglaFinanceList.size(); i++) {
                newsPaperUrl=indianBanglaFinanceList.get(i).getPaperUrl();
                loadPageDocument(newsPaperUrl);
            }
        }
    }
    public void loadIndianBanglaInternationalNewsDataFromDb() {
        indianBanglaInternationalList.clear();
        indianBanglaInternationalList = newsDatabase.indianBanglaInternationalDao().getAllNotificationNews("on");
        if (indianBanglaInternationalList.size()>0) {
            for (int i=0; i<indianBanglaInternationalList.size(); i++) {
                newsPaperUrl=indianBanglaInternationalList.get(i).getPaperUrl();
                loadPageDocument(newsPaperUrl);
            }
        }
    }
    public void loadIndianBanglaSportsNewsDataFromDb() {
        indianBanglaSportList.clear();
        indianBanglaSportList = newsDatabase.indianBanglaSportDao().getAllNotificationNews("on");
        if (indianBanglaSportList.size()>0) {
            for (int i=0; i<indianBanglaSportList.size(); i++) {
                newsPaperUrl=indianBanglaSportList.get(i).getPaperUrl();
                loadPageDocument(newsPaperUrl);
            }
        }
    }
    public void loadIndianBanglaTvChannelNewsDataFromDb() {
        indianBanglaTvChannelList.clear();
        indianBanglaTvChannelList = newsDatabase.indianBanglaTvChannelDao().getAllNotificationNews("on");
        if (indianBanglaTvChannelList.size()>0) {
            for (int i=0; i<indianBanglaTvChannelList.size(); i++) {
                newsPaperUrl=indianBanglaTvChannelList.get(i).getPaperUrl();
                loadPageDocument(newsPaperUrl);
            }
        }
    }

    public void loadIndianHindiBreakingNewsDataFromDb() {
        indianHindiBreakingList.clear();
        indianHindiBreakingList = newsDatabase.indianHindiBreakingDao().getAllNotificationNews("on");
        if (indianHindiBreakingList.size()>0) {
            for (int i=0; i<indianHindiBreakingList.size(); i++) {
                newsPaperUrl=indianHindiBreakingList.get(i).getPaperUrl();
                loadPageDocument(newsPaperUrl);
            }
        }
    }
    public void loadIndianHindiEntertainmentNewsDataFromDb() {
        indianHindiEntertainmentList.clear();
        indianHindiEntertainmentList = newsDatabase.indianHindiEntertainmentDao().getAllNotificationNews("on");
        if (indianHindiEntertainmentList.size()>0) {
            for (int i=0; i<indianHindiEntertainmentList.size(); i++) {
                newsPaperUrl=indianHindiEntertainmentList.get(i).getPaperUrl();
                loadPageDocument(newsPaperUrl);
            }
        }
    }
    public void loadIndianHindiFinanceNewsDataFromDb() {
        indianHindiFinanceList.clear();
        indianHindiFinanceList = newsDatabase.indianHindiFinanceDao().getAllNotificationNews("on");
        if (indianHindiFinanceList.size()>0) {
            for (int i=0; i<indianHindiFinanceList.size(); i++) {
                newsPaperUrl=indianHindiFinanceList.get(i).getPaperUrl();
                loadPageDocument(newsPaperUrl);
            }
        }
    }
    public void loadIndianHindiInternationalNewsDataFromDb() {
        indianHindiInternationalList.clear();
        indianHindiInternationalList = newsDatabase.indianHindiInternationalDao().getAllNotificationNews("on");
        if (indianHindiInternationalList.size()>0) {
            for (int i=0; i<indianHindiInternationalList.size(); i++) {
                newsPaperUrl=indianHindiInternationalList.get(i).getPaperUrl();
                loadPageDocument(newsPaperUrl);
            }
        }
    }
    public void loadIndianHindiSportsNewsDataFromDb() {
        indianHindiSportsList.clear();
        indianHindiSportsList = newsDatabase.indianHindiSportsDao().getAllNotificationNews("on");
        if (indianHindiSportsList.size()>0) {
            for (int i=0; i<indianHindiSportsList.size(); i++) {
                newsPaperUrl=indianHindiSportsList.get(i).getPaperUrl();
                loadPageDocument(newsPaperUrl);
            }
        }
    }
    public void loadIndianHindiTvChannelNewsDataFromDb() {
        indianHindiTvChannelList.clear();
        indianHindiTvChannelList = newsDatabase.indianHindiTvChannelDao().getAllNotificationNews("on");
        if (indianHindiTvChannelList.size()>0) {
            for (int i=0; i<indianHindiTvChannelList.size(); i++) {
                newsPaperUrl=indianHindiTvChannelList.get(i).getPaperUrl();
                loadPageDocument(newsPaperUrl);
            }
        }
    }

    public void loadIndianEnglishBreakingNewsDataFromDb() {
        indianEnglishBreakingList.clear();
        indianEnglishBreakingList = newsDatabase.indianEnglishBreakingDao().getAllNotificationNews("on");
        if (indianEnglishBreakingList.size()>0) {
            for (int i=0; i<indianEnglishBreakingList.size(); i++) {
                newsPaperUrl=indianEnglishBreakingList.get(i).getPaperUrl();
                loadPageDocument(newsPaperUrl);
            }
        }
    }
    public void loadIndianEnglishEntertainmentNewsDataFromDb() {
        indianEnglishEntertainmentList.clear();
        indianEnglishEntertainmentList = newsDatabase.indianEnglishEntertainmentDao().getAllNotificationNews("on");
        if (indianEnglishEntertainmentList.size()>0) {
            for (int i=0; i<indianEnglishEntertainmentList.size(); i++) {
                newsPaperUrl=indianEnglishEntertainmentList.get(i).getPaperUrl();
                loadPageDocument(newsPaperUrl);
            }
        }
    }
    public void loadIndianEnglishFinanceNewsDataFromDb() {
        indianEnglishFinanceList.clear();
        indianEnglishFinanceList = newsDatabase.indianEnglishFinanceDao().getAllNotificationNews("on");
        if (indianEnglishFinanceList.size()>0) {
            for (int i=0; i<indianEnglishFinanceList.size(); i++) {
                newsPaperUrl=indianEnglishFinanceList.get(i).getPaperUrl();
                loadPageDocument(newsPaperUrl);
            }
        }
    }
    public void loadIndianEnglishInternationalNewsDataFromDb() {
        indianEnglishInternationalList.clear();
        indianEnglishInternationalList = newsDatabase.indianEnglishInternationalDao().getAllNotificationNews("on");
        if (indianEnglishInternationalList.size()>0) {
            for (int i=0; i<indianEnglishInternationalList.size(); i++) {
                newsPaperUrl=indianEnglishInternationalList.get(i).getPaperUrl();
                loadPageDocument(newsPaperUrl);
            }
        }
    }
    public void loadIndianEnglishSportsNewsDataFromDb() {
        indianEnglishSportsList.clear();
        indianEnglishSportsList = newsDatabase.indianEnglishSportsDao().getAllNotificationNews("on");
        if (indianEnglishSportsList.size()>0) {
            for (int i=0; i<indianEnglishSportsList.size(); i++) {
                newsPaperUrl=indianEnglishSportsList.get(i).getPaperUrl();
                loadPageDocument(newsPaperUrl);
            }
        }
    }
    public void loadIndianEnglishTvChannelNewsDataFromDb() {
        indianEnglishTvChannelList.clear();
        indianEnglishTvChannelList = newsDatabase.indianEnglishTvChannelDao().getAllNotificationNews("on");
        if (indianEnglishTvChannelList.size()>0) {
            for (int i=0; i<indianEnglishTvChannelList.size(); i++) {
                newsPaperUrl=indianEnglishTvChannelList.get(i).getPaperUrl();
                loadPageDocument(newsPaperUrl);
            }
        }
    }


    private void setProthomAloBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.custom-story-card-4-data a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.prothomAlo);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("প্রথম আলো (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("প্রথম আলো (ব্রেকিং নিউজ)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("প্রথম আলো (ব্রেকিং নিউজ)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("প্রথম আলো (ব্রেকিং নিউজ)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBdProtidinBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select(".container > .jctkr-scroll > .js-conveyor-example ul li");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).select("a").attr("href");
                String news = allList.get(i).select("a").text();
                String link = MyUrl.bdProtidin + temporaryLink;
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bdProtidin);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("বাংলাদেশ প্রতিদিন (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("বাংলাদেশ প্রতিদিন (ব্রেকিং নিউজ)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("বাংলাদেশ প্রতিদিন (ব্রেকিং নিউজ)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("বাংলাদেশ প্রতিদিন (ব্রেকিং নিউজ)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setKalerKhantoBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul.content.mCustomScrollbar li");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).select("a").attr("href");
                String news = allList.get(i).select("a").text();
                String link = MyUrl.kalerKhanto + temporaryLink;
                if (news.length() >= 15) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.kalerKhanto);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG, "error is " + e.getMessage());
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("কালের কণ্ঠ (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("কালের কণ্ঠ (ব্রেকিং নিউজ)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("কালের কণ্ঠ (ব্রেকিং নিউজ)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("কালের কণ্ঠ (ব্রেকিং নিউজ)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setSomokalBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.bn-title+ul li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.somokal);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG, "error is " + e.getMessage());
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("সমকাল (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("সমকাল (ব্রেকিং নিউজ)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("সমকাল (ব্রেকিং নিউজ)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("সমকাল (ব্রেকিং নিউজ)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDailyJanaKhantoBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("marquee a");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String news = allList.get(i).select("a").text();
                String link = MyUrl.dailyJonoKhanto + temporaryLink;
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.somokal);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG, "error is " + e.getMessage());
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("দৈনিক জনকন্ঠ (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("দৈনিক জনকন্ঠ (ব্রেকিং নিউজ)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("দৈনিক জনকন্ঠ (ব্রেকিং নিউজ)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("দৈনিক জনকন্ঠ (ব্রেকিং নিউজ)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBhorerKagojBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.podan10-news h4.news-title a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bhorerKagoj);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG, "error is " + e.getMessage());
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("ভোরের কাগজ (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("ভোরের কাগজ (ব্রেকিং নিউজ)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("ভোরের কাগজ (ব্রেকিং নিউজ)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("ভোরের কাগজ (ব্রেকিং নিউজ)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDailyInqilabBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("marquee ul li a");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).select("a").text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyInqilab);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG, "error is " + e.getMessage());
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("দৈনিক ইনকিলাব (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("দৈনিক ইনকিলাব (ব্রেকিং নিউজ)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("দৈনিক ইনকিলাব (ব্রেকিং নিউজ)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("দৈনিক ইনকিলাব (ব্রেকিং নিউজ)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDailyNayaDigantaBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.col-lg-7.col-md-5.col-sm-5.column-no-left-padding a");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).select("a").text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyNayaDiganta);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG, "error is " + e.getMessage());
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("নয়া দিগন্ত (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("নয়া দিগন্ত (ব্রেকিং নিউজ)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("নয়া দিগন্ত (ব্রেকিং নিউজ)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("নয়া দিগন্ত (ব্রেকিং নিউজ)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setAmarDesh24BreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.panel-body div.focus-item.clearfix h2 a");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.amarDesh24);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG, "error is " + e.getMessage());
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("আমার দেশ 24 (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("আমার দেশ 24 (ব্রেকিং নিউজ)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("আমার দেশ 24 (ব্রেকিং নিউজ)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("আমার দেশ 24 (ব্রেকিং নিউজ)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDailyIttefaqBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                if (news.length() >= 20) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyIttefaq);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG, "error is " + e.getMessage());
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("দৈনিক ইত্তেফাক (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("দৈনিক ইত্তেফাক (ব্রেকিং নিউজ)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("দৈনিক ইত্তেফাক (ব্রেকিং নিউজ)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("দৈনিক ইত্তেফাক (ব্রেকিং নিউজ)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setManobJominBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.sec-box-home h4 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.manobJomin+temporaryLink;
                String news = allList.get(i).text();
                if (news.length() >= 20) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.manobJomin);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG, "error is " + e.getMessage());
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("মানবজমিন (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("মানবজমিন (ব্রেকিং নিউজ)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("মানবজমিন (ব্রেকিং নিউজ)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("মানবজমিন (ব্রেকিং নিউজ)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setSongbadProtidinBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.bn-news ul li a");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                if (news.length() >= 20) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.songbadProtidin);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG, "error is " + e.getMessage());
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("সংবাদ প্রতিদিন (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("সংবাদ প্রতিদিন (ব্রেকিং নিউজ)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("সংবাদ প্রতিদিন (ব্রেকিং নিউজ)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("সংবাদ প্রতিদিন (ব্রেকিং নিউজ)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setManobKanthaBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.ticker-container ul div li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.manobKantha);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG, "error is " + e.getMessage());
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("মানবকণ্ঠ (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("মানবকণ্ঠ (ব্রেকিং নিউজ)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("মানবকণ্ঠ (ব্রেকিং নিউজ)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("মানবকণ্ঠ (ব্রেকিং নিউজ)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBangladeshJournalBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("marquee ul li a");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bangladeshJournal);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG, "error is " + e.getMessage());
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("বাংলাদেশ জার্নাল (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("বাংলাদেশ জার্নাল (ব্রেকিং নিউজ)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("বাংলাদেশ জার্নাল (ব্রেকিং নিউজ)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("বাংলাদেশ জার্নাল (ব্রেকিং নিউজ)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setTheDailyVorerPataBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul li[data-category=শিরোনাম] a");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String news = allList.get(i).text();
                String link = MyUrl.theDailyVorerPata + temporaryLink;
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.theDailyVorerPata);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG, "error is " + e.getMessage());
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("ভোরের পাতা (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("ভোরের পাতা (ব্রেকিং নিউজ)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("ভোরের পাতা (ব্রেকিং নিউজ)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("ভোরের পাতা (ব্রেকিং নিউজ)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
//====================================Bangladesh Breaking News method staying in above========================================


    private void setAnandaBazarBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.abp-atf-left-story-block a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.anandaBazarBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.anandaBazarBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("আনন্দবাজার পত্রিকা (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);

        if (list.size()>=1) {
            displayNotification("আনন্দবাজার পত্রিকা (ব্রেকিং নিউজ)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("আনন্দবাজার পত্রিকা (ব্রেকিং নিউজ)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("আনন্দবাজার পত্রিকা (ব্রেকিং নিউজ)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setSangbadPratidinBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul.clearfix.tatka_update_list li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                if (news.length() >= 15) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.sangbadPratidinBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("সংবাদ প্রতিদিন (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("সংবাদ প্রতিদিন (ব্রেকিং নিউজ)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("সংবাদ প্রতিদিন (ব্রেকিং নিউজ)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("সংবাদ প্রতিদিন (ব্রেকিং নিউজ)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBartamanPatrikaBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h5 center a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link = "https://bartamanpatrika.com/" + temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bartamanPatrikaBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("বর্তমান (সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("বর্তমান (সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("বর্তমান (সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("বর্তমান (সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setGanaShaktiBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.img-text-all a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).select("p").text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.ganashaktiBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("গণশক্তি (আজকের খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("গণশক্তি (আজকের খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("গণশক্তি (আজকের খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("গণশক্তি (আজকের খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setUttarBangaSambadBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h3.entry-title.td-module-title a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.uttarBangaSambadBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("উত্তরবঙ্গ সংবাদ (উত্তরবঙ্গ)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("উত্তরবঙ্গ সংবাদ (উত্তরবঙ্গ)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("উত্তরবঙ্গ সংবাদ (উত্তরবঙ্গ)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("উত্তরবঙ্গ সংবাদ (উত্তরবঙ্গ)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setEbelaBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a.Readerchoice[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link = MyUrl.ebelaBreakingNews + temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.ebelaBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("এবেলা (সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("এবেলা (সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("এবেলা (সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("এবেলা (সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setAsomiyaPratidinBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("marquee a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.asomiyaPratidinBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("অসমীয়া প্রতিদিন (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("অসমীয়া প্রতিদিন (ব্রেকিং নিউজ)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("অসমীয়া প্রতিদিন (ব্রেকিং নিউজ)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("অসমীয়া প্রতিদিন (ব্রেকিং নিউজ)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setAajKaalBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h6 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.aajkaalBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("আজকাল (আকর্ষনীয় খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("আজকাল (আকর্ষনীয় খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("আজকাল (আকর্ষনীয় খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("আজকাল (আকর্ষনীয় খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setKhaborOnlineBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.mvp-feat5-side-list.left.relative a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).select("h2").text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.khaborOnlinBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("খবর অনলাইন (নজরে)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("খবর অনলাইন (নজরে)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("খবর অনলাইন (নজরে)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("খবর অনলাইন (নজরে)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setJugaShankaBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h3.post-title a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.jugasankhaBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("যুগশঙ্ক (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("যুগশঙ্ক (ব্রেকিং নিউজ)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("যুগশঙ্ক (ব্রেকিং নিউজ)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("যুগশঙ্ক (ব্রেকিং নিউজ)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setJagaranTripuraBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul.newsticker li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.jagarantripuraBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("জাগরণত্রিপুরা (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("জাগরণত্রিপুরা (ব্রেকিং নিউজ)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("জাগরণত্রিপুরা (ব্রেকিং নিউজ)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("জাগরণত্রিপুরা (ব্রেকিং নিউজ)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setGanadabiBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div#breaking-news ul li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.ganadabiBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("গণদাবী (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("গণদাবী (ব্রেকিং নিউজ)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("গণদাবী (ব্রেকিং নিউজ)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("গণদাবী (ব্রেকিং নিউজ)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setOneIndiaBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul#hp-top-news-left li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link = MyUrl.oneIndiaBanglaBreakingNews + temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.oneIndiaBanglaBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("ওয়ান ইন্ডিয়া (সাধারণ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("ওয়ান ইন্ডিয়া (সাধারণ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("ওয়ান ইন্ডিয়া (সাধারণ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("ওয়ান ইন্ডিয়া (সাধারণ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setKolkata247BreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.td-trending-now-display-area h3.entry-title.td-module-title a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.kolkata247BreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("কলকাতা ২৪*৭ (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("কলকাতা ২৪*৭ (ব্রেকিং নিউজ)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("কলকাতা ২৪*৭ (ব্রেকিং নিউজ)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("কলকাতা ২৪*৭ (ব্রেকিং নিউজ)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setKhabor24GhontaBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.exclusive-slides a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.khabor24BreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("খবর ২৪ ঘন্টা (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("খবর ২৪ ঘন্টা (ব্রেকিং নিউজ)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("খবর ২৪ ঘন্টা (ব্রেকিং নিউজ)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("খবর ২৪ ঘন্টা (ব্রেকিং নিউজ)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
//====================================Indian Bangla Breaking News method staying in above========================================



    private void setJagranBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ol.p1Box li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.jagranBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.jagranBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("जागरण (ट्रेंडिंग न्यूज़)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("जागरण (ट्रेंडिंग न्यूज़)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("जागरण (ट्रेंडिंग न्यूज़)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("जागरण (ट्रेंडिंग न्यूज़)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBhaskarBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul li._24e83f49 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.bhaskarBreakingNews+temporaryLink;
                String news = allList.get(i).select("h3").text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bhaskarBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("देनिक भास्कर (मुख्य समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("देनिक भास्कर (मुख्य समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("देनिक भास्कर (मुख्य समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("देनिक भास्कर (मुख्य समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setAmarUjalaBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.toptrending section h2 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.amarUjalaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.amarUjalaBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("अमर उजाला (शीर्ष ट्रेंडिंग)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("अमर उजाला (शीर्ष ट्रेंडिंग)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("अमर उजाला (शीर्ष ट्रेंडिंग)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("अमर उजाला (शीर्ष ट्रेंडिंग)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setLiveHindustanBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul.left-top-news.no-pad.n-tazakhbre li h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.liveHindustanBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.liveHindustanBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("लाइव हिन्दुस्तान (ताज़ा खबर)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("लाइव हिन्दुस्तान (ताज़ा खबर)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("लाइव हिन्दुस्तान (ताज़ा खबर)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("लाइव हिन्दुस्तान (ताज़ा खबर)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setNavBharatTimesBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul.col12.most-read-stroies li a.table_row[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.navBharatTimesBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("नव भारत टाइम्स (सबसे ज़्यादा पढ़ा हुआ)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("नव भारत टाइम्स (सबसे ज़्यादा पढ़ा हुआ)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("नव भारत टाइम्स (सबसे ज़्यादा पढ़ा हुआ)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("नव भारत टाइम्स (सबसे ज़्यादा पढ़ा हुआ)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setJanSattaBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.topnewsbx.left.MB30 ul li span a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                if (news.length()>=15) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.janSattaBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("जनसत्ता (बड़ी खबर)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("जनसत्ता (बड़ी खबर)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("जनसत्ता (बड़ी खबर)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("जनसत्ता (बड़ी खबर)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setPunjabKeSariBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul#ContentPlaceHolder1_dv_middle_section_top li h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.punjabKeSariBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("पंजाब केसरी (शीर्ष आलेख)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("पंजाब केसरी (शीर्ष आलेख)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("पंजाब केसरी (शीर्ष आलेख)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("पंजाब केसरी (शीर्ष आलेख)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setHariBhoomiBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div#home_top_right_level_1 h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.hariBhoomiBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.hariBhoomiBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("हरिभूमि (वायरल समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("हरिभूमि (वायरल समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("हरिभूमि (वायरल समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("हरिभूमि (वायरल समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDivyaHimachalBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h2.ntitle a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.divyaHimachalBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("दिव्य हिमाचल (सामान्य समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("दिव्य हिमाचल (सामान्य समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("दिव्य हिमाचल (सामान्य समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("दिव्य हिमाचल (सामान्य समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setPrabhaSakshiBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("marquee a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.prabhaSakshiBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.prabhaSakshiBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("प्रभा साक्षी (ताज़ा खबर)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("प्रभा साक्षी (ताज़ा खबर)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("प्रभा साक्षी (ताज़ा खबर)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("प्रभा साक्षी (ताज़ा खबर)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDeshDootBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a[href^=https://www.deshdoot.com/local-news/]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                if (news.length()>=15) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.deshDootBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("देशदूत (स्थानीय समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("देशदूत (स्थानीय समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("देशदूत (स्थानीय समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("देशदूत (स्थानीय समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDainikTribuneOnlineBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.opinion-news-block h4 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.dainikTribuneOnlineBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dainikTribuneOnlineBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("दैनिक ट्रिब्यून (विचार खबर)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("दैनिक ट्रिब्यून (विचार खबर)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("दैनिक ट्रिब्यून (विचार खबर)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("दैनिक ट्रिब्यून (विचार खबर)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setSamacharJagatBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("marquee a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.samacharJagatBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("SamacharJagat (ताज़ा खबर)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("SamacharJagat (ताज़ा खबर)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("SamacharJagat (ताज़ा खबर)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("SamacharJagat (ताज़ा खबर)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setKhasKhabarBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul.slides li div.slide-txt2 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.khasKhabarBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("अच्छी खबर (मुख्य समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("अच्छी खबर (मुख्य समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("अच्छी खबर (मुख्य समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("अच्छी खबर (मुख्य समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
//====================================Indian Hindi Breaking News method staying in above========================================



    private void setHindustanTimesBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.storyShortDetail h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String linkWithExtras = allList.get(i).attr("href");
                String temporaryLink=linkWithExtras.substring(1);
                String link=MyUrl.hindustanTimesBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                if (news.length()>=15) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.hindustanTimesBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("Hindustan Times (Top News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Hindustan Times (Top News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Hindustan Times (Top News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Hindustan Times (Top News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setIndianExpressBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul.breaking li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.indianExpressEnglishBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("The Indian Express (Breaking News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("The Indian Express (Breaking News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("The Indian Express (Breaking News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("The Indian Express (Breaking News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDailyPioneerBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.breakingNewsSlider div.swiper-slide h2 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.dailyPioneerBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyPioneerBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("The Pioneer (Breaking News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("The Pioneer (Breaking News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("The Pioneer (Breaking News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("The Pioneer (Breaking News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDeccanHeraldBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("section#home-top-stories ul li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.deccanHeraldBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.deccanHeraldBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("Deccan Herald (General News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Deccan Herald (General News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Deccan Herald (General News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Deccan Herald (General News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDnaIndiaBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h3.headlines_pg a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.dnaIndiaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dnaIndiaHeadlinesBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("DNA India (Headlines)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("DNA India (Headlines)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("DNA India (Headlines)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("DNA India (Headlines)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDeccanChronicleBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div#topStory div.col-sm-12.col-xs-12.tstry-feed-sml-a a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.deccanChronicleBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.deccanChronicleBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("Deccan Chronicle (Top Stories)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Deccan Chronicle (Top Stories)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Deccan Chronicle (Top Stories)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Deccan Chronicle (Top Stories)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setAsianAgeBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.single_left_coloum_wrapper.other-top-stories h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String linkWithExtras = allList.get(i).attr("href");
                String temporaryLink=linkWithExtras.substring(1);
                String link=MyUrl.asianAgeBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.asianAgeBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("The Asian Age (Top Stories)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("The Asian Age (Top Stories)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("The Asian Age (Top Stories)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("The Asian Age (Top Stories)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setEconomicTimesBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div#topNewsTabs ul.newsList.clearfix li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.economicTimesBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.economicTimesBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("Economic Times (Top News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Economic Times (Top News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Economic Times (Top News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Economic Times (Top News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBusinessStandardBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.latestnews-txt  div.mt-news ul li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.businessStandardBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.businessStandardBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("Business Standard (Just In)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Business Standard (Just In)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Business Standard (Just In)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Business Standard (Just In)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setFinancialExpressBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.topblock-left h2 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.financialExpressBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("Financial Express (Top News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Financial Express (Top News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Financial Express (Top News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Financial Express (Top News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setNewIndianExpressBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a[href^=https://www.newindianexpress.com/good-news/]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                if (news.length()>=15) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.newIndianExpressBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("The New Indian Express (Good News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("The New Indian Express (Good News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("The New Indian Express (Good News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("The New Indian Express (Good News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setTribuneIndiaBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div[title=Top Stories] h4 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.tribuneIndiaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                if (news.length()>=11) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.tribuneIndiaBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("The Tribune (Top Stories)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("The Tribune (Top Stories)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("The Tribune (Top Stories)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("The Tribune (Top Stories)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setLiveMintBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h2.headline a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String linkWithExtras = allList.get(i).attr("href");
                String temporaryLink=linkWithExtras.substring(1);
                String link=MyUrl.liveMintBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.liveMintBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("Mint (General News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Mint (General News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Mint (General News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Mint (General News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
//====================================Indian English Breaking News method staying in above========================================



    private void setDailyAmaderShomoyEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements newsList = document.select("div.w3-col.m4 a[href]");
            for (int i = 0; i < newsList.size(); i++) {
                String news = newsList.get(i).text();
                String link=newsList.get(i).attr("href");
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dainikAmaderShomoyEntertainment);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("আমাদের সময় (সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("আমাদের সময় (সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("আমাদের সময় (সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("আমাদের সময় (সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBdJournalEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements newsList = document.select("h4 a[href]");
            for (int i = 0; i < newsList.size(); i++) {
                String news = newsList.get(i).text();
                String link=newsList.get(i).attr("href");
                if (news.length()>=15) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bdJournalEntertainment);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("বাংলাদেশ জার্নাল (সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("বাংলাদেশ জার্নাল (সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("বাংলাদেশ জার্নাল (সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("বাংলাদেশ জার্নাল (সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setManobKanthaEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements newsList = document.select("h2 a[href]");
            for (int i = 0; i < newsList.size(); i++) {
                String news = newsList.get(i).text();
                String link=newsList.get(i).attr("href");
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.manobkanthaEntertainment);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("মানবকণ্ঠ (সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("মানবকণ্ঠ (সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("মানবকণ্ঠ (সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("মানবকণ্ঠ (সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setSangbadPratidinEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements newsList = document.select("p.news_title a[href]");
            for (int i = 0; i < newsList.size(); i++) {
                String news = newsList.get(i).text();
                String link=newsList.get(i).attr("href");
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.sangbadpratidinEntertainment);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("সংবাদ প্রতিদিন (সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("সংবাদ প্রতিদিন (সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("সংবাদ প্রতিদিন (সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("সংবাদ প্রতিদিন (সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDailyManobJominEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements newsList = document.select("div.col-sm-6 h4 > a[href]");
            for (int i = 0; i < newsList.size(); i++) {
                String news = newsList.get(i).text();
                String temporaryLink=newsList.get(i).attr("href");
                String link=MyUrl.manobJomin+temporaryLink;
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyManobJominEntertainment);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("মানবজমিন (সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("মানবজমিন (সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("মানবজমিন (সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("মানবজমিন (সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDailyIttefaqEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements newsList = document.select("a[href^=https://www.ittefaq.com.bd/entertainment/]");
            for (int i = 0; i < newsList.size(); i++) {
                String news = newsList.get(i).text();
                String link=newsList.get(i).attr("href");
                if (news.length()>=15) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyIttefaqEntertainment);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("দৈনিক ইত্তেফাক (সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("দৈনিক ইত্তেফাক (সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("দৈনিক ইত্তেফাক (সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("দৈনিক ইত্তেফাক (সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setAmarDesh24EntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements newsList = document.select("a.default[href]");
            for (int i = 0; i < newsList.size(); i++) {
                String news = newsList.get(i).text();
                String link=newsList.get(i).attr("href");
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.amarDesh24Entertainment);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("আমার দেশ 24 (সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("আমার দেশ 24 (সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("আমার দেশ 24 (সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("আমার দেশ 24 (সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDailyNayaDigantaEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements newsList = document.select("div.col-md-5.column-no-left-padding a[href]");
            for (int i = 0; i < newsList.size(); i++) {
                String news = newsList.get(i).text();
                String link=newsList.get(i).attr("href");
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyNayaDigantaEntertainment);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("নয়া দিগন্ত (সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("নয়া দিগন্ত (সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("নয়া দিগন্ত (সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("নয়া দিগন্ত (সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDailyInqilabEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements newsList = document.select("div.row.news_list a[href]");
            for (int i = 0; i < newsList.size(); i++) {
                String news = newsList.select("h2").get(i).text();
                String link=newsList.get(i).attr("href");
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyInqilabEntertainment);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("দৈনিক ইনকিলাব (সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("দৈনিক ইনকিলাব (সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("দৈনিক ইনকিলাব (সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("দৈনিক ইনকিলাব (সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBhorerKagojEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements newsList = document.select("div.cat-normal-content-other-item.col-sm-3.col-xs-6 a[href]");
            for (int i = 0; i < newsList.size(); i++) {
                String news = newsList.get(i).text();
                String link=newsList.get(i).attr("href");
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bhorerKagojEntertainment);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("ভোরের কাগজ (সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("ভোরের কাগজ (সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("ভোরের কাগজ (সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("ভোরের কাগজ (সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBanglaTribuneEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements newsList = document.select("a.link_overlay[href]");
            for (int i = 0; i < newsList.size(); i++) {
                String news = newsList.get(i).attr("title");
                String link=newsList.get(i).attr("href");
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.banglaTribuneEntertainment);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("বাংলা ট্রিবিউন (বিনোদনের খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("বাংলা ট্রিবিউন (বিনোদনের খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("বাংলা ট্রিবিউন (বিনোদনের খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("বাংলা ট্রিবিউন (বিনোদনের খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBdNews24EntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements newsList = document.select("div.text h3 a[href]");
            for (int i = 0; i < newsList.size(); i++) {
                String news = newsList.get(i).text();
                String link=newsList.get(i).attr("href");
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bdNews24Entertainment);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("বিডি নিউস ২৪ (সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("বিডি নিউস ২৪ (সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("বিডি নিউস ২৪ (সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("বিডি নিউস ২৪ (সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDailyJanakanthaEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements newsList = document.select("div.list-article a[href]+a[href]");
            for (int i = 0; i < newsList.size(); i++) {
                String news = newsList.get(i).text();
                String temporaryLink=newsList.get(i).attr("href");
                String link = "https://www.dailyjanakantha.com"+temporaryLink;
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyJanakanthaEntertainment);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("দৈনিক জনকন্ঠ (সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("দৈনিক জনকন্ঠ (সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("দৈনিক জনকন্ঠ (সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("দৈনিক জনকন্ঠ (সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setSamakalEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements newsList = document.select("div.info h3");
            Elements linkList = document.select("div.info+a.link-overlay[href]");
            String news,link;
            for (int i = 0; i < newsList.size(); i++) {
                news = newsList.get(i).text();
                if (i<linkList.size()) {
                    link = linkList.get(i).attr("href");
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.samakalEntertainment);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("সমকাল (সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("সমকাল (সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("সমকাল (সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("সমকাল (সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setKalerkanthoEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a.title.hidden-xs[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.kalerKhanto+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.kalerKanthoEntertainment);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("কালের কণ্ঠ (সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("কালের কণ্ঠ (সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("কালের কণ্ঠ (সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("কালের কণ্ঠ (সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
//    ===============================================================================================


    private void setAnandaBazarEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.sectionstoryheading.toppadding10 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.anandaBazarBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.anandaBazarEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("আনন্দবাজার পত্রিকা (বিনোদনের শেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("আনন্দবাজার পত্রিকা (বিনোদনের শেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("আনন্দবাজার পত্রিকা (বিনোদনের শেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("আনন্দবাজার পত্রিকা (বিনোদনের শেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setSangbadPratidinIndianEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("p.news_title a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.sangbadPratidinEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("সংবাদ প্রতিদিন (বিনোদনের শেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("সংবাদ প্রতিদিন (বিনোদনের শেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("সংবাদ প্রতিদিন (বিনোদনের শেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("সংবাদ প্রতিদিন (বিনোদনের শেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBartamanPatrikaEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h5 center a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.bartamanPatrikaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bartamanPatrikaEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("বর্তমান (বিনোদনের শেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("বর্তমান (বিনোদনের শেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("বর্তমান (বিনোদনের শেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("বর্তমান (বিনোদনের শেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setUttarBangaSambadEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h3.entry-title.td-module-title a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.uttarBangaSambadEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("উত্তরবঙ্গ সংবাদ (বিনোদনের শেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("উত্তরবঙ্গ সংবাদ (বিনোদনের শেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("উত্তরবঙ্গ সংবাদ (বিনোদনের শেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("উত্তরবঙ্গ সংবাদ (বিনোদনের শেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setEbelaEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.black_conetent_text_large a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.ebelaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.ebelaEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("এবেলা (বিনোদনের শেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("এবেলা (বিনোদনের শেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("এবেলা (বিনোদনের শেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("এবেলা (বিনোদনের শেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setAsomiyaPratidinEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h2.title a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.asomiyaPratidinEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("অসমীয়া প্রতিদিন (বিনোদনের শেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("অসমীয়া প্রতিদিন (বিনোদনের শেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("অসমীয়া প্রতিদিন (বিনোদনের শেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("অসমীয়া প্রতিদিন (বিনোদনের শেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setAajKaalEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.col-md-12.col-sm-12.col-xs-12 h6 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.aajKaalEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("আজকাল (বিনোদনের শেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("আজকাল (বিনোদনের শেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("আজকাল (বিনোদনের শেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("আজকাল (বিনোদনের শেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setKhaborOnlineEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul.mvp-blog-story-list.left.relative.infinite-content li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).select("h2").text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.khaborOnlineEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("খবর অনলাইন (বিনোদনের শেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("খবর অনলাইন (বিনোদনের শেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("খবর অনলাইন (বিনোদনের শেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("খবর অনলাইন (বিনোদনের শেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setJugaSankhaEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul#posts-container li h3.post-title a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.jugaSankhaEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("যুগশঙ্ক (বিনোদনের শেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("যুগশঙ্ক (বিনোদনের শেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("যুগশঙ্ক (বিনোদনের শেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("যুগশঙ্ক (বিনোদনের শেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setJagaranTripuraEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h2.entry-title a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.jagaranTripuraEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("জাগরণত্রিপুরা (বিনোদনের শেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("জাগরণত্রিপুরা (বিনোদনের শেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("জাগরণত্রিপুরা (বিনোদনের শেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("জাগরণত্রিপুরা (বিনোদনের শেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setOneIndiaEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul li div.cityblock-title.news-desc a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String linkWithExtras = allList.get(i).attr("href");
                String temporaryLink=linkWithExtras.substring(1);
                String link=MyUrl.oneIndiaBanglaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.oneIndiaEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("ওয়ান ইন্ডিয়া (বিনোদনের শেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("ওয়ান ইন্ডিয়া (বিনোদনের শেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("ওয়ান ইন্ডিয়া (বিনোদনের শেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("ওয়ান ইন্ডিয়া (বিনোদনের শেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setKolkata247EntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.td-ss-main-content h3.entry-title.td-module-title a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.kolkata247EntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("কলকাতা ২৪*৭ (বিনোদনের শেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("কলকাতা ২৪*৭ (বিনোদনের শেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("কলকাতা ২৪*৭ (বিনোদনের শেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("কলকাতা ২৪*৭ (বিনোদনের শেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setKhabor24EntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("article h4 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.khabor24EntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("খবর ২৪ ঘন্টা (বিনোদনের শেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("খবর ২৪ ঘন্টা (বিনোদনের শেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("খবর ২৪ ঘন্টা (বিনোদনের শেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("খবর ২৪ ঘন্টা (বিনোদনের শেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBengal2DayEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("article h2 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bengal2DayEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("বাংলা টু ডে (বিনোদনের শেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("বাংলা টু ডে (বিনোদনের শেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("বাংলা টু ডে (বিনোদনের শেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("বাংলা টু ডে (বিনোদনের শেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
//    ===============================================================================================


    private void setJagranEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div#1517983345706 ul li div.h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.jagranEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("जागरण (मनोरंजन समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("जागरण (मनोरंजन समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("जागरण (मनोरंजन समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("जागरण (मनोरंजन समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBhaskarEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul li._24e83f49 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.bhaskarEntertainmentNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bhaskarEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("देनिक भास्कर (मनोरंजन समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("देनिक भास्कर (मनोरंजन समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("देनिक भास्कर (मनोरंजन समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("देनिक भास्कर (मनोरंजन समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setAmarUjalaEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("section h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.amarUjalaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.amarUjalaEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("अमर उजाला (मनोरंजन समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("अमर उजाला (मनोरंजन समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("अमर उजाला (मनोरंजन समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("अमर उजाला (मनोरंजन समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setLiveHindustanEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul li p a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.liveHindustanBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                if (news.length()>=10) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.liveHindustanEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("लाइव हिन्दुस्तान (मनोरंजन समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("लाइव हिन्दुस्तान (मनोरंजन समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("लाइव हिन्दुस्तान (मनोरंजन समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("लाइव हिन्दुस्तान (मनोरंजन समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setNavBharatTimesEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a.table_row[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.navBharatTimesEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("नव भारत टाइम्स (फिल्म और मनोरंजन समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("नव भारत टाइम्स (फिल्म और मनोरंजन समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("नव भारत टाइम्स (फिल्म और मनोरंजन समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("नव भारत टाइम्स (फिल्म और मनोरंजन समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setJanSattaEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul li a+h2 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.janSattaEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("जनसत्ता (मनोरंजन समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("जनसत्ता (मनोरंजन समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("जनसत्ता (मनोरंजन समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("जनसत्ता (मनोरंजन समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBollywoodTadkaEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul li > div+a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bollywoodTadkaEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("बॉलीवुड तड़का (बॉलीवुड नेवस)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("बॉलीवुड तड़का (बॉलीवुड नेवस)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("बॉलीवुड तड़का (बॉलीवुड नेवस)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("बॉलीवुड तड़का (बॉलीवुड नेवस)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setHariBhoomiEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.hariBhoomiBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.hariBhoomiEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("हरिभूमि (मनोरंजन समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("हरिभूमि (मनोरंजन समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("हरिभूमि (मनोरंजन समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("हरिभूमि (मनोरंजन समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setKhasKhabarEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div#other-news-list ul li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.khasKhabarEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("खास खबर (मनोरंजन समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("खास खबर (मनोरंजन समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("खास खबर (मनोरंजन समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("खास खबर (मनोरंजन समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDivyaHimachalEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.n-listing h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.divyaHimachalEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("दिव्य हिमाचल (मनोरंजन समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("दिव्य हिमाचल (मनोरंजन समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("दिव्य हिमाचल (मनोरंजन समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("दिव्य हिमाचल (मनोरंजन समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setPrabhaSakshiEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.prabhaSakshiBreakingNews+temporaryLink;
                String news = allList.get(i).select("h3").text();
                if (news.length()>=15) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.prabhaSakshiEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("प्रभा साक्षी (मनोरंजन समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("प्रभा साक्षी (मनोरंजन समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("प्रभा साक्षी (मनोरंजन समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("प्रभा साक्षी (मनोरंजन समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDainikTribuneOnlineEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div#dontmiss-slider ul li h4 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.dainikTribuneOnlineBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dainikTribuneOnlineEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("दैनिक ट्रिब्यून (मनोरंजन समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("दैनिक ट्रिब्यून (मनोरंजन समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("दैनिक ट्रिब्यून (मनोरंजन समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("दैनिक ट्रिब्यून (मनोरंजन समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setSamacharJagatEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h2 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.samacharJagatEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Samachar Jagat (मनोरंजन समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Samachar Jagat (मनोरंजन समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Samachar Jagat (मनोरंजन समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Samachar Jagat (मनोरंजन समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setPatrikaEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.cNews-text a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.patrikaEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("पत्रिका (मनोरंजन समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("पत्रिका (मनोरंजन समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("पत्रिका (मनोरंजन समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("पत्रिका (मनोरंजन समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
//    ===============================================================================================


    private void setHindustanTimesEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.storyShortDetail h2 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String linkWithExtras = allList.get(i).attr("href");
                String temporaryLink=linkWithExtras.substring(1);
                String link=MyUrl.hindustanTimesBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.hindustanTimesEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Hindustan Times (Entertainment News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Hindustan Times (Entertainment News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Hindustan Times (Entertainment News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Hindustan Times (Entertainment News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setIndianExpressEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.title a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.indianExpressEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("The Indian Express (Entertainment News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("The Indian Express (Entertainment News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("The Indian Express (Entertainment News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("The Indian Express (Entertainment News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDailyPioneerEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul li h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.dailyPioneerBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyPioneerEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("The Pioneer (Entertainment News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("The Pioneer (Entertainment News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("The Pioneer (Entertainment News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("The Pioneer (Entertainment News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDeccanHeraldEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul li a.card-cta[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.deccanHeraldBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.deccanHeraldEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Deccan Herald (Entertainment News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Deccan Herald (Entertainment News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Deccan Herald (Entertainment News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Deccan Herald (Entertainment News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDnaIndiaEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.dnaIndiaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dnaIndiaEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("DNA India (Entertainment News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("DNA India (Entertainment News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("DNA India (Entertainment News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("DNA India (Entertainment News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDeccanChronicleEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div#topStory a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.deccanChronicleBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.deccanChronicleEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Deccan Chronicle (Entertainment News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Deccan Chronicle (Entertainment News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Deccan Chronicle (Entertainment News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Deccan Chronicle (Entertainment News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setTheAsianAgeEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String linkWithExtras = allList.get(i).attr("href");
                String temporaryLink=linkWithExtras.substring(1);
                String link=MyUrl.asianAgeBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.asianAgeEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("The Asian Age (Entertainment News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("The Asian Age (Entertainment News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("The Asian Age (Entertainment News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("The Asian Age (Entertainment News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setFinancialExpressEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.financialExpressBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.financialExpressEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Financial Express (Entertainment News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Financial Express (Entertainment News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Financial Express (Entertainment News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Financial Express (Entertainment News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setNewIndianExpressEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("p a.article_click[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                if (news.length()>=11) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.newIndianExpressEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("The New Indian Express (Entertainment News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("The New Indian Express (Entertainment News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("The New Indian Express (Entertainment News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("The New Indian Express (Entertainment News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setTribuneIndiaEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h4 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.tribuneIndiaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.tribuneIndiaEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("The Tribune (Entertainment News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("The Tribune (Entertainment News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("The Tribune (Entertainment News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("The Tribune (Entertainment News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
//    ============================================================================================



    private void setJugantorFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.leadmorehl2 h2 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.jugantorFinance);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("যুগান্তর (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("যুগান্তর (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("যুগান্তর (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("যুগান্তর (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setAmarDesh24FinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a.default[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.amarDesh24Finance);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("আমার দেশ 24 (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("আমার দেশ 24 (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("আমার দেশ 24 (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("আমার দেশ 24 (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDailySangramFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("li h2 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailySangramFinance);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("দৈনিক সংগ্রাম (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("দৈনিক সংগ্রাম (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("দৈনিক সংগ্রাম (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("দৈনিক সংগ্রাম (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDainikAmaderShomoyFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.w3-col.m4 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dainikAmaderShomoyFinance);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("আমাদের সময় (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("আমাদের সময় (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("আমাদের সময় (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("আমাদের সময় (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setJaiJaiDinBdFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div#newsHl2 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.jaiJaiDinBdFinance);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("যায়যায় দিন (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("যায়যায় দিন (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("যায়যায় দিন (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("যায়যায় দিন (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDailyVorerPataFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.title_inner a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.theDailyVorerPata+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyVorerPataFinance);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("ভোরের পাতা (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("ভোরের পাতা (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("ভোরের পাতা (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("ভোরের পাতা (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBdJournalFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h4 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bdJournalFinance);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("বাংলাদেশ জার্নাল (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("বাংলাদেশ জার্নাল (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("বাংলাদেশ জার্নাল (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("বাংলাদেশ জার্নাল (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setManobKanthaFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.col-md-8.col-sm-8 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.manobKanthaFinance);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("মানবকণ্ঠ (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("মানবকণ্ঠ (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("মানবকণ্ঠ (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("মানবকণ্ঠ (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDailyIttefaqFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a[href^=https://www.ittefaq.com.bd/economy/]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                if (news.length()>=15) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyIttefaqFinance);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("দৈনিক ইত্তেফাক (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("দৈনিক ইত্তেফাক (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("দৈনিক ইত্তেফাক (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("দৈনিক ইত্তেফাক (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBhorerKagojFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.cat-normal-content-other-item.col-sm-3.col-xs-6 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bhorerKagojFinance);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("ভোরের কাগজ (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("ভোরের কাগজ (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("ভোরের কাগজ (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("ভোরের কাগজ (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBanglaTribuneFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.each_tab.tab_latest.oh.db ul li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=temporaryLink.substring(2);
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.banglaTribuneFinance);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("বাংলা ট্রিবিউন (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("বাংলা ট্রিবিউন (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("বাংলা ট্রিবিউন (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("বাংলা ট্রিবিউন (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBdNews24FinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h6.default a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bdNews24Finance);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("বিডি নিউস ২৪ (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("বিডি নিউস ২৪ (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("বিডি নিউস ২৪ (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("বিডি নিউস ২৪ (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDailyJanakanthaFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.list-article a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link="https://www.dailyjanakantha.com"+temporaryLink;
                String news = allList.get(i).select("h2").text();
                if (news.length()>=15) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyJanakanthaFinance);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("দৈনিক জনকন্ঠ (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("দৈনিক জনকন্ঠ (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("দৈনিক জনকন্ঠ (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("দৈনিক জনকন্ঠ (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setSamakalFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.news-content.xs-100.cpItemMarginB");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).select("a[href]").attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.samakalFinance);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("সমকাল (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("সমকাল (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("সমকাল (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("সমকাল (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setKalerKanthoFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a.title.hidden-xs[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.kalerKhanto+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.kalerKanthoFinance);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("কালের কণ্ঠ (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("কালের কণ্ঠ (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("কালের কণ্ঠ (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("কালের কণ্ঠ (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
//    ============================================================================================


    private void setAnandaBazarFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.sectionstoryheading.toppadding10 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.anandaBazarBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.anandaBazarFinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("আনন্দবাজার পত্রিকা (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("আনন্দবাজার পত্রিকা (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("আনন্দবাজার পত্রিকা (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("আনন্দবাজার পত্রিকা (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBartamanPatrikaFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h5 center a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.bartamanPatrikaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bartamanPatrikaFinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("বর্তমান (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("বর্তমান (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("বর্তমান (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("বর্তমান (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setGanashaktiFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.col-md-8 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).select("h3").text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.ganashaktiFinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("গণশক্তি (শিল্প কারখানার খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("গণশক্তি (শিল্প কারখানার খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("গণশক্তি (শিল্প কারখানার খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("গণশক্তি (শিল্প কারখানার খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setUttarBangaSambadFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h3.entry-title.td-module-title a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.uttarBangaSambadFinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("উত্তরবঙ্গ সংবাদ (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("উত্তরবঙ্গ সংবাদ (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("উত্তরবঙ্গ সংবাদ (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("উত্তরবঙ্গ সংবাদ (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setEbelaFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.black_conetent_text_large a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.ebelaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.ebelaFinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("এবেলা (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("এবেলা (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("এবেলা (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("এবেলা (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setAsomiyaPratidinFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("article a.post-title.post-url[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.asomiyaPratidinFinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("অসমীয়া প্রতিদিন (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("অসমীয়া প্রতিদিন (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("অসমীয়া প্রতিদিন (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("অসমীয়া প্রতিদিন (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setAajKaalFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.post-style1.clearfix h6 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.aajKaalFinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("আজকাল (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("আজকাল (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("আজকাল (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("আজকাল (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setKhaborOnlineFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul.mvp-blog-story-list.left.relative.infinite-content li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).select("h2").text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.khaborOnlineFinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("খবর অনলাইন (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("খবর অনলাইন (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("খবর অনলাইন (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("খবর অনলাইন (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setJugasankhaFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul#posts-container li h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.jugasankhaFinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("যুগশঙ্ক (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("যুগশঙ্ক (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("যুগশঙ্ক (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("যুগশঙ্ক (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setJagaranTripuraFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("article h2 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.jagaranTripuraFinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("জাগরণত্রিপুরা (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("জাগরণত্রিপুরা (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("জাগরণত্রিপুরা (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("জাগরণত্রিপুরা (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setKolkata247FinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.td-block-row h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.kolkata247FinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("কলকাতা ২৪*৭ (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("কলকাতা ২৪*৭ (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("কলকাতা ২৪*৭ (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("কলকাতা ২৪*৭ (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
//    ============================================================================================


    private void setJagranFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul.topicList li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.jagranBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.jagranFinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("जागरण (व्यापार समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("जागरण (व्यापार समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("जागरण (व्यापार समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("जागरण (व्यापार समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBhaskarFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul li._24e83f49 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.bhaskarBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bhaskarFinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("देनिक भास्कर (व्यापार समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("देनिक भास्कर (व्यापार समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("देनिक भास्कर (व्यापार समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("देनिक भास्कर (व्यापार समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setAmarUjalaFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h3 a[href^=/business]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.amarUjalaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.amarUjalaFinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("अमर उजाला (व्यापार समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("अमर उजाला (व्यापार समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("अमर उजाला (व्यापार समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("अमर उजाला (व्यापार समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setLiveHindustanFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul li p a[href^=/business/]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.liveHindustanBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.liveHindustanFinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("लाइव हिन्दुस्तान (व्यापार समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("लाइव हिन्दुस्तान (व्यापार समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("लाइव हिन्दुस्तान (व्यापार समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("लाइव हिन्दुस्तान (व्यापार समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setNavBharatTimesFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a.table_row[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.navBharatTimesFinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("नव भारत टाइम्स (व्यापार समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("नव भारत टाइम्स (व्यापार समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("नव भारत टाइम्स (व्यापार समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("नव भारत टाइम्स (व्यापार समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setJanSattaFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("span.head a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.janSattaFinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("जनसत्ता (व्यापार समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("जनसत्ता (व्यापार समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("जनसत्ता (व्यापार समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("जनसत्ता (व्यापार समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setPunjabKesariFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul#ContentPlaceHolder1_dv_section_middle li h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.punjabKesariFinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("पंजाब केसरी (व्यापार समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("पंजाब केसरी (व्यापार समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("पंजाब केसरी (व्यापार समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("पंजाब केसरी (व्यापार समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setKhasKhabarFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul li a[href^=https://business.khaskhabar.com/business-news/]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.khasKhabarFinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("खास खबर (व्यापार समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("खास खबर (व्यापार समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("खास खबर (व्यापार समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("खास खबर (व्यापार समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDivyaHimachalFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.divyaHimachalFinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("दिव्य हिमाचल (व्यापार समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("दिव्य हिमाचल (व्यापार समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("दिव्य हिमाचल (व्यापार समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("दिव्य हिमाचल (व्यापार समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setPrabhaSakshiFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a[href^=/business/]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.prabhaSakshiBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                if (news.length()>=15) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.prabhaSakshiFinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("प्रभा साक्षी (व्यापार समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("प्रभा साक्षी (व्यापार समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("प्रभा साक्षी (व्यापार समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("प्रभा साक्षी (व्यापार समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDainikTribuneOnlineFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h4 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.dainikTribuneOnlineBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dainikTribuneOnlineFinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("दैनिक ट्रिब्यून (व्यापार समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("दैनिक ट्रिब्यून (व्यापार समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("दैनिक ट्रिब्यून (व्यापार समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("दैनिक ट्रिब्यून (व्यापार समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setSamacharJagatFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h2 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.samacharJagatFinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Samachar Jagat (व्यापार समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Samachar Jagat (व्यापार समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Samachar Jagat (व्यापार समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Samachar Jagat (व्यापार समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
//    ============================================================================================


    private void setHindustanTimesFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.storyShortDetail h2 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String linkWithExtras = allList.get(i).attr("href");
                String temporaryLink=linkWithExtras.substring(1);
                String link=MyUrl.hindustanTimesBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                if (news.length()>=11) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.hindustanTimesFinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Hindustan Times (Finance News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Hindustan Times (Finance News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Hindustan Times (Finance News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Hindustan Times (Finance News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setIndianExpressFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.leftpanel h2 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.indianExpressFinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("The Indian Express (Finance News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("The Indian Express (Finance News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("The Indian Express (Finance News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("The Indian Express (Finance News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDailyPioneerFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.col-12.col-sm a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                if (news.length()>=11) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyPioneerFinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("The Pioneer (Finance News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("The Pioneer (Finance News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("The Pioneer (Finance News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("The Pioneer (Finance News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDeccanHeraldFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a.card-cta[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.deccanHeraldFinanceNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.deccanHeraldFinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Deccan Herald (Finance News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Deccan Herald (Finance News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Deccan Herald (Finance News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Deccan Herald (Finance News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDnaIndiaFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.dnaIndiaFinanceNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dnaIndiaFinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("DNA India (Finance News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("DNA India (Finance News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("DNA India (Finance News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("DNA India (Finance News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDeccanChronicleFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a[href^=/business/]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.deccanChronicleBreakingNews+temporaryLink;
                String news = allList.get(i).select("h3").text();
                if (news.length()>=11) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.deccanChronicleFinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Deccan Chronicle (Finance News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Deccan Chronicle (Finance News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Deccan Chronicle (Finance News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Deccan Chronicle (Finance News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setAsianAgeFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.col-lg-9.col-md-8.noPadding.leftCol h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String linkWithExtras = allList.get(i).attr("href");
                String temporaryLink=linkWithExtras.substring(1);
                String link=MyUrl.asianAgeBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.asianAgeFinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("The Asian Age (Finance News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("The Asian Age (Finance News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("The Asian Age (Finance News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("The Asian Age (Finance News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setEconomicsTimesFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("section#marketsWidget a+a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.economicTimesBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.economicTimesFinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Economic Times (Finance News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Economic Times (Finance News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Economic Times (Finance News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Economic Times (Finance News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBusinessStandardFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.row-inner h2 a[href^=/article/finance/]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.businessStandardBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.businessStandardFinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Business Standard (Finance News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Business Standard (Finance News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Business Standard (Finance News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Business Standard (Finance News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setFinancialExpressFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.content-list h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.financialExpressFinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Financial Express (Finance News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Financial Express (Finance News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Financial Express (Finance News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Financial Express (Finance News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setNewIndianExpressFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.search-row_type h4 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.newIndianExpressFinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("The New Indian Express (Finance News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("The New Indian Express (Finance News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("The New Indian Express (Finance News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("The New Indian Express (Finance News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setTribuneIndiaFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div#pagination-news-block h4 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.tribuneIndiaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.tribuneIndiaFinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("The Tribune (Finance News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("The Tribune (Finance News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("The Tribune (Finance News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("The Tribune (Finance News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setLiveMintFinanceNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("article a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.liveMintBreakingNews+temporaryLink;
                String news = allList.get(i).select("h2").text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.liveMintFinanceNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Mint (Finance News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Mint (Finance News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Mint (Finance News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Mint (Finance News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
//    ============================================================================================


    private void setKalerKanthoInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a.title.hidden-xs[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.kalerKhanto+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.kalerKanthoInternational);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("কালের কণ্ঠ (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("কালের কণ্ঠ (আন্তর্জাতিক খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("কালের কণ্ঠ (আন্তর্জাতিক খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("কালের কণ্ঠ (আন্তর্জাতিক খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setSamakalInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.news-content.xs-100.cpItemMarginB");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).select("a[href]").attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.samakalInternational);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("সমকাল (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("সমকাল (আন্তর্জাতিক খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("সমকাল (আন্তর্জাতিক খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("সমকাল (আন্তর্জাতিক খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDailyJanakanthaInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.list-article a+a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link="https://www.dailyjanakantha.com"+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyJanakanthaInternational);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("দৈনিক জনকন্ঠ (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("দৈনিক জনকন্ঠ (আন্তর্জাতিক খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("দৈনিক জনকন্ঠ (আন্তর্জাতিক খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("দৈনিক জনকন্ঠ (আন্তর্জাতিক খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBdNews24InternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.text h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bdNews24International);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("বিডি নিউস ২৪ (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("বিডি নিউস ২৪ (আন্তর্জাতিক খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("বিডি নিউস ২৪ (আন্তর্জাতিক খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("বিডি নিউস ২৪ (আন্তর্জাতিক খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBanglaTribuneInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a.link_overlay[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=temporaryLink.substring(2);
                String news = allList.get(i).attr("title");
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.banglaTribuneInternational);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("বাংলা ট্রিবিউন (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("বাংলা ট্রিবিউন (আন্তর্জাতিক খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("বাংলা ট্রিবিউন (আন্তর্জাতিক খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("বাংলা ট্রিবিউন (আন্তর্জাতিক খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBhorerKagojInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.cat-normal-content-other-item.col-sm-3.col-xs-6 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).select("h4").text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bhorerkagojInternational);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("ভোরের কাগজ (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("ভোরের কাগজ (আন্তর্জাতিক খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("ভোরের কাগজ (আন্তর্জাতিক খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("ভোরের কাগজ (আন্তর্জাতিক খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDailyInqilabInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.col-xs-12.col-sm-6 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).select("h2").text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyInqilabInternational);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("দৈনিক ইনকিলাব (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("দৈনিক ইনকিলাব (আন্তর্জাতিক খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("দৈনিক ইনকিলাব (আন্তর্জাতিক খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("দৈনিক ইনকিলাব (আন্তর্জাতিক খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDailyNayadigantaInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.col-md-5.column-no-left-padding a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyNayadigantaInternational);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("নয়া দিগন্ত (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("নয়া দিগন্ত (আন্তর্জাতিক খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("নয়া দিগন্ত (আন্তর্জাতিক খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("নয়া দিগন্ত (আন্তর্জাতিক খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setAmarDesh24InternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.amarDesh24International);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("আমার দেশ 24 (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("আমার দেশ 24 (আন্তর্জাতিক খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("আমার দেশ 24 (আন্তর্জাতিক খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("আমার দেশ 24 (আন্তর্জাতিক খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDailyIttefaqInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a[href^=https://www.ittefaq.com.bd/worldnews/]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                if (news.length()>=15) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyIttefaqInternational);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("দৈনিক ইত্তেফাক (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("দৈনিক ইত্তেফাক (আন্তর্জাতিক খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("দৈনিক ইত্তেফাক (আন্তর্জাতিক খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("দৈনিক ইত্তেফাক (আন্তর্জাতিক খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setManobZaminInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h4 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link="https://www.mzamin.com/"+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.manobZaminInternational);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("মানবজমিন (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("মানবজমিন (আন্তর্জাতিক খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("মানবজমিন (আন্তর্জাতিক খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("মানবজমিন (আন্তর্জাতিক খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setSangbadPratidinInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul.other_news_list.mar-btp-10 li p.news_title a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.sangbadpratidinInternational);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("সংবাদ প্রতিদিন (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("সংবাদ প্রতিদিন (আন্তর্জাতিক খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("সংবাদ প্রতিদিন (আন্তর্জাতিক খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("সংবাদ প্রতিদিন (আন্তর্জাতিক খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setManobKanthaInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.manobKanthaInternational);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("মানবকণ্ঠ (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("মানবকণ্ঠ (আন্তর্জাতিক খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("মানবকণ্ঠ (আন্তর্জাতিক খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("মানবকণ্ঠ (আন্তর্জাতিক খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBdJournalInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.more_hl a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bdJournalInternational);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("বাংলাদেশ জার্নাল (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("বাংলাদেশ জার্নাল (আন্তর্জাতিক খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("বাংলাদেশ জার্নাল (আন্তর্জাতিক খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("বাংলাদেশ জার্নাল (আন্তর্জাতিক খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDailyVorerPataInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.title_inner a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link="https://www.dailyvorerpata.com/"+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyVorerPataInternational);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("ভোরের পাতা (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("ভোরের পাতা (আন্তর্জাতিক খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("ভোরের পাতা (আন্তর্জাতিক খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("ভোরের পাতা (আন্তর্জাতিক খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDailyAmaderShomoyInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.w3-col.m4 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dainikAmaderShomoyInternational);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("আমাদের সময় (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("আমাদের সময় (আন্তর্জাতিক খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("আমাদের সময় (আন্তর্জাতিক খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("আমাদের সময় (আন্তর্জাতিক খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
//    ============================================================================================


    private void setAnandaBazarInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.sectionstoryheading.toppadding10 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.anandaBazarBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.anandaBazarInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("আনন্দবাজার পত্রিকা (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("আনন্দবাজার পত্রিকা (আন্তর্জাতিক খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("আনন্দবাজার পত্রিকা (আন্তর্জাতিক খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("আনন্দবাজার পত্রিকা (আন্তর্জাতিক খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setSangbadPratidinIndiaInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul.other_news_list.mar-btp-10 li p.news_title a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.sangbadPratidinInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("সংবাদ প্রতিদিন (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("সংবাদ প্রতিদিন (আন্তর্জাতিক খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("সংবাদ প্রতিদিন (আন্তর্জাতিক খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("সংবাদ প্রতিদিন (আন্তর্জাতিক খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBartamanPatrikaInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h5 center a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.bartamanPatrikaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bartamanPatrikaInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("বর্তমান (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("বর্তমান (আন্তর্জাতিক খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("বর্তমান (আন্তর্জাতিক খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("বর্তমান (আন্তর্জাতিক খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setGanaShaktiInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.col-md-8 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).select("h3").text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.ganaShaktiInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("গণশক্তি (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("গণশক্তি (আন্তর্জাতিক খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("গণশক্তি (আন্তর্জাতিক খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("গণশক্তি (আন্তর্জাতিক খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setUttarBangaSambadInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h3.entry-title.td-module-title a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.uttarBangaSambadInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("উত্তরবঙ্গ সংবাদ (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("উত্তরবঙ্গ সংবাদ (আন্তর্জাতিক খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("উত্তরবঙ্গ সংবাদ (আন্তর্জাতিক খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("উত্তরবঙ্গ সংবাদ (আন্তর্জাতিক খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setEbelaInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.black_conetent_text_large a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.ebelaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.ebelaInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("এবেলা (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("এবেলা (আন্তর্জাতিক খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("এবেলা (আন্তর্জাতিক খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("এবেলা (আন্তর্জাতিক খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setAajKaalInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h6 a[href^=https://aajkaal.in/news/international/]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.aajKaalInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("আজকাল (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("আজকাল (আন্তর্জাতিক খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("আজকাল (আন্তর্জাতিক খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("আজকাল (আন্তর্জাতিক খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setKhaborOnlineInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul.mvp-blog-story-list.left.relative.infinite-content li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).select("h2").text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.khaborOnlineInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("খবর অনলাইন (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("খবর অনলাইন (আন্তর্জাতিক খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("খবর অনলাইন (আন্তর্জাতিক খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("খবর অনলাইন (আন্তর্জাতিক খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setJugaSankhaInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul#posts-container li h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.jugaSankhaInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("যুগশঙ্ক (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("যুগশঙ্ক (আন্তর্জাতিক খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("যুগশঙ্ক (আন্তর্জাতিক খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("যুগশঙ্ক (আন্তর্জাতিক খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setJagaranTripuraInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("article h2 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.jagaranTripuraInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("জাগরণত্রিপুরা (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("জাগরণত্রিপুরা (আন্তর্জাতিক খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("জাগরণত্রিপুরা (আন্তর্জাতিক খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("জাগরণত্রিপুরা (আন্তর্জাতিক খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setOneIndiaInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul li div.cityblock-title.news-desc a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String linkWithExtras = allList.get(i).attr("href");
                String temporaryLink=linkWithExtras.substring(1);
                String link=MyUrl.oneIndiaBanglaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.oneIndiaInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("ওয়ান ইন্ডিয়া (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("ওয়ান ইন্ডিয়া (আন্তর্জাতিক খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("ওয়ান ইন্ডিয়া (আন্তর্জাতিক খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("ওয়ান ইন্ডিয়া (আন্তর্জাতিক খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setKolkata247InternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.td-ss-main-content h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.kolkata247InternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("কলকাতা ২৪*৭ (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("কলকাতা ২৪*৭ (আন্তর্জাতিক খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("কলকাতা ২৪*৭ (আন্তর্জাতিক খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("কলকাতা ২৪*৭ (আন্তর্জাতিক খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBengal2DayInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("article h2 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bengal2DayInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("বাংলা টু ডে (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("বাংলা টু ডে (আন্তর্জাতিক খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("বাংলা টু ডে (আন্তর্জাতিক খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("বাংলা টু ডে (আন্তর্জাতিক খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
//    ============================================================================================


    private void setJagranInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.jagranBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.jagranInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("जागरण (अंतरराष्ट्रीय समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("जागरण (अंतरराष्ट्रीय समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("जागरण (अंतरराष्ट्रीय समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("जागरण (अंतरराष्ट्रीय समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBhaskarInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul li._24e83f49 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.bhaskarBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bhaskarInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("देनिक भास्कर (अंतरराष्ट्रीय समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("देनिक भास्कर (अंतरराष्ट्रीय समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("देनिक भास्कर (अंतरराष्ट्रीय समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("देनिक भास्कर (अंतरराष्ट्रीय समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setAmarUjalaInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h5+h3 a[href^=/world/]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.amarUjalaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.amarUjalaInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("अमर उजाला (अंतरराष्ट्रीय समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("अमर उजाला (अंतरराष्ट्रीय समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("अमर उजाला (अंतरराष्ट्रीय समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("अमर उजाला (अंतरराष्ट्रीय समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setLiveHindustanInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h3 a[href^=/international/]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.liveHindustanBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.liveHindustanInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("लाइव हिन्दुस्तान (अंतरराष्ट्रीय समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("लाइव हिन्दुस्तान (अंतरराष्ट्रीय समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("लाइव हिन्दुस्तान (अंतरराष्ट्रीय समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("लाइव हिन्दुस्तान (अंतरराष्ट्रीय समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setNavBharatTimesInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul li a.table_row[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.navBharatTimesInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("नव भारत टाइम्स (अंतरराष्ट्रीय समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("नव भारत टाइम्स (अंतरराष्ट्रीय समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("नव भारत टाइम्स (अंतरराष्ट्रीय समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("नव भारत टाइम्स (अंतरराष्ट्रीय समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setJanSattaInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.newslistbx span.head a[href^=https://www.jansatta.com/international/]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.janSattaInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("जनसत्ता (अंतरराष्ट्रीय समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("जनसत्ता (अंतरराष्ट्रीय समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("जनसत्ता (अंतरराष्ट्रीय समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("जनसत्ता (अंतरराष्ट्रीय समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setPunjabKesariInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul#ContentPlaceHolder1_dv_section_middle li h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.punjabKesariInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("पंजाब केसरी (अंतरराष्ट्रीय समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("पंजाब केसरी (अंतरराष्ट्रीय समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("पंजाब केसरी (अंतरराष्ट्रीय समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("पंजाब केसरी (अंतरराष्ट्रीय समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setKhasKhabarInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div#container ul li a+span a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.khasKhabarInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("खास खबर (अंतरराष्ट्रीय समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("खास खबर (अंतरराष्ट्रीय समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("खास खबर (अंतरराष्ट्रीय समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("खास खबर (अंतरराष्ट्रीय समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setPrabhaSakshiInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.position-absolute a[href^=/international/]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.prabhaSakshiBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.prabhaSakshiInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("प्रभा साक्षी (अंतरराष्ट्रीय समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("प्रभा साक्षी (अंतरराष्ट्रीय समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("प्रभा साक्षी (अंतरराष्ट्रीय समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("प्रभा साक्षी (अंतरराष्ट्रीय समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDainikTribuneOnlineInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div#top-news-grid a.card-top-align[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.dainikTribuneOnlineBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dainikTribuneOnlineInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("दैनिक ट्रिब्यून (अंतरराष्ट्रीय समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("दैनिक ट्रिब्यून (अंतरराष्ट्रीय समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("दैनिक ट्रिब्यून (अंतरराष्ट्रीय समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("दैनिक ट्रिब्यून (अंतरराष्ट्रीय समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setSamacharJagatInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.cat_page h2 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.samacharJagatInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Samachar Jagat (अंतरराष्ट्रीय समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Samachar Jagat (अंतरराष्ट्रीय समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Samachar Jagat (अंतरराष्ट्रीय समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Samachar Jagat (अंतरराष्ट्रीय समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
//    ============================================================================================


    private void setHindustanTimesInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.storyShortDetail h2 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String linkWithExtras = allList.get(i).attr("href");
                String temporaryLink=linkWithExtras.substring(1);
                String link=MyUrl.hindustanTimesBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.hindustanTimesInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Hindustan Times (International News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Hindustan Times (International News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Hindustan Times (International News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Hindustan Times (International News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setIndianExpressInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul li h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.indianExpressInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("The Indian Express (International News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("The Indian Express (International News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("The Indian Express (International News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("The Indian Express (International News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDailyPioneerInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.highLightedNews h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.dailyPioneerBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyPioneerInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("The Pioneer (International News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("The Pioneer (International News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("The Pioneer (International News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("The Pioneer (International News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDeccanHeraldInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul.sm-hr-card-list li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.deccanHeraldBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.deccanHeraldInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Deccan Herald (International News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Deccan Herald (International News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Deccan Herald (International News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Deccan Herald (International News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDnaIndiaInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h2+h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.dnaIndiaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dnaIndiaInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("DNA India (International News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("DNA India (International News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("DNA India (International News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("DNA India (International News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDeccanChronicleInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.deccanChronicleBreakingNews+temporaryLink;
                String news = allList.get(i).select("h3").text();
                if (news.length()>= 15) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.deccanChronicleInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Deccan Chronicle (International News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Deccan Chronicle (International News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Deccan Chronicle (International News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Deccan Chronicle (International News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setAsianAgeInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String linkWithExtras = allList.get(i).attr("href");
                String temporaryLink=linkWithExtras.substring(1);
                String link=MyUrl.asianAgeBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.asianAgeInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("The Asian Age (International News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("The Asian Age (International News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("The Asian Age (International News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("The Asian Age (International News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setEconomicTimesInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.eachStory h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.economicTimesBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.economicTimesInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Economic Times (International News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Economic Times (International News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Economic Times (International News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Economic Times (International News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBusinessStandardInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h2 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.businessStandardBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                if (news.length()>=15) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.businessStandardInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Business Standard (International News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Business Standard (International News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Business Standard (International News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Business Standard (International News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setFinancialExpressInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.listitembx h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.financialExpressInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Financial Express (International News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Financial Express (International News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Financial Express (International News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Financial Express (International News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setNewIndianExpressInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("table#example h4 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.newIndianExpressInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("The New Indian Express (International News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("The New Indian Express (International News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("The New Indian Express (International News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("The New Indian Express (International News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setTribuneIndiaInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div#top-news-grid h4 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.tribuneIndiaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.tribuneIndiaInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("The Tribune (International News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("The Tribune (International News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("The Tribune (International News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("The Tribune (International News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setLiveMintInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h2 a[href^=https://www.livemint.com/news/world/]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.liveMintInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Mint (International News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Mint (International News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Mint (International News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Mint (International News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
//    ============================================================================================


    private void setDailySangramSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a#news-detail[title]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailySangramSports);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("দৈনিক সংগ্রাম (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("দৈনিক সংগ্রাম (খেলার খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("দৈনিক সংগ্রাম (খেলার খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("দৈনিক সংগ্রাম (খেলার খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBdJournalSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.col-md-4.col-sm-4.khela_news_block a[href^=https://www.bd-journal.com/sports/]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bangladeshJournal);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("বাংলাদেশ জার্নাল (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("বাংলাদেশ জার্নাল (খেলার খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("বাংলাদেশ জার্নাল (খেলার খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("বাংলাদেশ জার্নাল (খেলার খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setManobKanthaSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("section.special div.recent-news.margin-bottom-10 h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                if (link.startsWith("https://www.manobkantha.com.bd/cricket/") || link.startsWith("https://www.manobkantha.com.bd/football/")) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.manobKantha);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("মানবকণ্ঠ (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("মানবকণ্ঠ (খেলার খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("মানবকণ্ঠ (খেলার খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("মানবকণ্ঠ (খেলার খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setSangbadPratidinSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("p.news_title a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.sangbadPratidinSports);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("সংবাদ প্রতিদিন (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("সংবাদ প্রতিদিন (খেলার খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("সংবাদ প্রতিদিন (খেলার খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("সংবাদ প্রতিদিন (খেলার খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDailyManobJominSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h4 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link="https://www.mzamin.com/"+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyManobJominSports);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("মানবজমিন (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("মানবজমিন (খেলার খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("মানবজমিন (খেলার খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("মানবজমিন (খেলার খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDailyIttefaqSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a[href^=https://www.ittefaq.com.bd/sports/]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                if (news.length()>=15) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyIttefaqSports);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("দৈনিক ইত্তেফাক (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("দৈনিক ইত্তেফাক (খেলার খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("দৈনিক ইত্তেফাক (খেলার খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("দৈনিক ইত্তেফাক (খেলার খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setAmarDesh24SportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h3 a.default[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.amarDesh24Sports);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("আমার দেশ 24 (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("আমার দেশ 24 (খেলার খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("আমার দেশ 24 (খেলার খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("আমার দেশ 24 (খেলার খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDailyNayaDigantaSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.row.sub-lead-list div.col-md-5.column-no-left-padding a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyNayaDigantaSports);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("নয়া দিগন্ত (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("নয়া দিগন্ত (খেলার খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("নয়া দিগন্ত (খেলার খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("নয়া দিগন্ত (খেলার খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDailyInqilabSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.row.news_list div.col-xs-12.col-sm-6 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).select("h2").text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyInqilabSports);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("দৈনিক ইনকিলাব (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("দৈনিক ইনকিলাব (খেলার খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("দৈনিক ইনকিলাব (খেলার খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("দৈনিক ইনকিলাব (খেলার খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBhorerKagojSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.cat-normal-content-other-item.col-sm-3.col-xs-6 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).select("h4").text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bhorerKagojSports);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("ভোরের কাগজ (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("ভোরের কাগজ (খেলার খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("ভোরের কাগজ (খেলার খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("ভোরের কাগজ (খেলার খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBanglaTribuneSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.col.col3 a.link_overlay[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).attr("title");
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.banglaTribunesSports);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("বাংলা ট্রিবিউন (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("বাংলা ট্রিবিউন (খেলার খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("বাংলা ট্রিবিউন (খেলার খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("বাংলা ট্রিবিউন (খেলার খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBdNews24SportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.text h6 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bdNews24Sports);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("বিডি নিউস ২৪ (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("বিডি নিউস ২৪ (খেলার খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("বিডি নিউস ২৪ (খেলার খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("বিডি নিউস ২৪ (খেলার খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDailyJanakanthaSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.list-article");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).select("a+a").attr("href");
                String link=MyUrl.dailyJonoKhanto+temporaryLink;
                String news = allList.get(i).select("a+a h2").text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyJanakanthaSports);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("দৈনিক জনকণ্ঠ (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("দৈনিক জনকণ্ঠ (খেলার খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("দৈনিক জনকণ্ঠ (খেলার খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("দৈনিক জনকণ্ঠ (খেলার খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setSamakalSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.media.news-content.child-cat-list");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).select("a").attr("href");
                String news = allList.get(i).select("h4.heading").text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.samakalSports);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("সমকাল (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("সমকাল (খেলার খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("সমকাল (খেলার খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("সমকাল (খেলার খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setKalerkhanthoSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.col-xs-12.col-sm-6.col-md-6.n_row a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.kalerKhanto+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.kalerkhanthoSports);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("কালেরকণ্ঠ (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("কালেরকণ্ঠ (খেলার খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("কালেরকণ্ঠ (খেলার খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("কালেরকণ্ঠ (খেলার খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
//    ============================================================================================


    private void setAnandaBazarSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.sectionstoryheading.toppadding10 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.anandaBazarBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.anandaBazarSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("আনন্দবাজার পত্রিকা (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("আনন্দবাজার পত্রিকা (খেলার খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("আনন্দবাজার পত্রিকা (খেলার খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("আনন্দবাজার পত্রিকা (খেলার খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setSangbadPratidinIndianSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("p.news_title a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.sangbadPratidinSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("সংবাদ প্রতিদিন (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("সংবাদ প্রতিদিন (খেলার খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("সংবাদ প্রতিদিন (খেলার খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("সংবাদ প্রতিদিন (খেলার খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBartamanPatrikaSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h5 center a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.bartamanPatrikaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bartamanPatrikaSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("বর্তমান (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("বর্তমান (খেলার খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("বর্তমান (খেলার খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("বর্তমান (খেলার খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setGanaShaktiSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.col-md-8 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).select("h3").text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.ganashaktiSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("গণশক্তি (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("গণশক্তি (খেলার খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("গণশক্তি (খেলার খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("গণশক্তি (খেলার খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setUttarBangaSambadSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h3.entry-title.td-module-title a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.uttarBangaSambadSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("উত্তরবঙ্গ সংবাদ (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("উত্তরবঙ্গ সংবাদ (খেলার খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("উত্তরবঙ্গ সংবাদ (খেলার খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("উত্তরবঙ্গ সংবাদ (খেলার খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setEbelaSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.black_conetent_text_large a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.ebelaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.ebelaSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("এবেলা (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("এবেলা (খেলার খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("এবেলা (খেলার খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("এবেলা (খেলার খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setAsomiyaPratidinSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h2.title a.post-title.post-url[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.asomiyaPratidinSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("অসমীয়া প্রতিদিন (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("অসমীয়া প্রতিদিন (খেলার খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("অসমীয়া প্রতিদিন (খেলার খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("অসমীয়া প্রতিদিন (খেলার খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setAajKaalSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.text-box h6 a[href^=https://aajkaal.in/news/sports/]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.aajKaalSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("আজকাল (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("আজকাল (খেলার খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("আজকাল (খেলার খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("আজকাল (খেলার খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setKhaborOnlineSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul li.mvp-blog-story-wrap.left.relative.infinite-post a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).select("h2").text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.khaborOnlineSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("খবর অনলাইন (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("খবর অনলাইন (খেলার খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("খবর অনলাইন (খেলার খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("খবর অনলাইন (খেলার খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setJugaSankhaSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h3.post-title a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.jugasankhaSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("যুগশঙ্ক (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("যুগশঙ্ক (খেলার খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("যুগশঙ্ক (খেলার খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("যুগশঙ্ক (খেলার খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setJagaranTripuraSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h2.entry-title a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.jagaranTripuraSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("জাগরণত্রিপুরা (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("জাগরণত্রিপুরা (খেলার খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("জাগরণত্রিপুরা (খেলার খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("জাগরণত্রিপুরা (খেলার খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setOneIndiaSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h2.collection-heading a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.oneIndiaBanglaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.oneIndiaSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("ওয়ান ইন্ডিয়া (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("ওয়ান ইন্ডিয়া (খেলার খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("ওয়ান ইন্ডিয়া (খেলার খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("ওয়ান ইন্ডিয়া (খেলার খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setKolkata247SportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.td-ss-main-content h3.entry-title.td-module-title a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.kolkata247SportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("কলকাতা ২৪*৭ (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("কলকাতা ২৪*৭ (খেলার খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("কলকাতা ২৪*৭ (খেলার খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("কলকাতা ২৪*৭ (খেলার খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setKhabor24SportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("article h4 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.khabor24SportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("খবর ২৪ ঘন্টা (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("খবর ২৪ ঘন্টা (খেলার খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("খবর ২৪ ঘন্টা (খেলার খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("খবর ২৪ ঘন্টা (খেলার খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBengal2DaySportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("article h2.entry-title a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bengal2DaySportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("বাংলা টু ডে (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("বাংলা টু ডে (খেলার খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("বাংলা টু ডে (খেলার খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("বাংলা টু ডে (খেলার খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
//    ============================================================================================


    private void setJagranSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.h3 a[href^=/cricket/]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.jagranBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.jagranSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("जागरण (खेल समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("जागरण (खेल समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("जागरण (खेल समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("जागरण (खेल समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBhaskarSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul li._24e83f49 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.bhaskarBreakingNews+temporaryLink;
                String news = allList.get(i).select("h3").text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bhaskarSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("देनिक भास्कर (खेल समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("देनिक भास्कर (खेल समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("देनिक भास्कर (खेल समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("देनिक भास्कर (खेल समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setAmarUjalaSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("section h3 a[href^=/sports/]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.amarUjalaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.amarUjalaSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("अमर उजाला (खेल समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("अमर उजाला (खेल समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("अमर उजाला (खेल समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("अमर उजाला (खेल समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setLiveHindustanSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul li p a[href^=/sports/]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.liveHindustanBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.liveHindustanSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("लाइव हिन्दुस्तान (खेल समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("लाइव हिन्दुस्तान (खेल समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("लाइव हिन्दुस्तान (खेल समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("लाइव हिन्दुस्तान (खेल समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setNavBharatTimesSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul li a.table_row[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.navBharatTimesSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("नव भारत टाइम्स (खेल समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("नव भारत टाइम्स (खेल समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("नव भारत टाइम्स (खेल समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("नव भारत टाइम्स (खेल समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setJanSattaSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul.links li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.janSattaSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("जनसत्ता (खेल समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("जनसत्ता (खेल समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("जनसत्ता (खेल समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("जनसत्ता (खेल समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setPunjabKesariSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h3.media-heading a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.punjabKesariSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("पंजाब केसरी (खेल समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("पंजाब केसरी (खेल समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("पंजाब केसरी (खेल समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("पंजाब केसरी (खेल समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setHariBhoomiSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.hariBhoomiBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.hariBhoomiSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("हरिभूमि (खेल समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("हरिभूमि (खेल समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("हरिभूमि (खेल समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("हरिभूमि (खेल समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setKhasKhabarSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul li > a[href^=https://www.khaskhabar.com/sports/]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).select("p").text();
                if (news.length()>=15) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.khasKhabarSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("अच्छी खबर (खेल समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("अच्छी खबर (खेल समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("अच्छी खबर (खेल समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("अच्छी खबर (खेल समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDivyaHimachalSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.newslistbx h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.divyaHimachalSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("दिव्य हिमाचल (खेल समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("दिव्य हिमाचल (खेल समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("दिव्य हिमाचल (खेल समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("दिव्य हिमाचल (खेल समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setPrabhaSakshiSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a[href^=/sports/]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.prabhaSakshiBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                if (news.length()>=15) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.prabhaSakshiSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("प्रभा साक्षी (खेल समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("प्रभा साक्षी (खेल समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("प्रभा साक्षी (खेल समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("प्रभा साक्षी (खेल समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDainikTribuneOnlineSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a[href^=/news/sports/]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.dainikTribuneOnlineBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dainikTribuneOnlineSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("दैनिक ट्रिब्यून (खेल समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("दैनिक ट्रिब्यून (खेल समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("दैनिक ट्रिब्यून (खेल समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("दैनिक ट्रिब्यून (खेल समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setSamacharJagatSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h2 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.samacharJagatSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("SamacharJagat (खेल समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("SamacharJagat (खेल समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("SamacharJagat (खेल समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("SamacharJagat (खेल समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
//    ============================================================================================


    private void setHindustanTimesSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.storyShortDetail h2 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String linkWithExtras = allList.get(i).attr("href");
                String temporaryLink=linkWithExtras.substring(1);
                String link=MyUrl.hindustanTimesBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.hindustanTimesSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Hindustan Times (Sports News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Hindustan Times (Sports News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Hindustan Times (Sports News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Hindustan Times (Sports News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setIndianExpressSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h2.title a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.indianExpressSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("The Indian Express (Sports News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("The Indian Express (Sports News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("The Indian Express (Sports News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("The Indian Express (Sports News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDailyPioneerSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul.list-unstyled li h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.dailyPioneerBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyPioneerSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("The Pioneer (Sports News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("The Pioneer (Sports News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("The Pioneer (Sports News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("The Pioneer (Sports News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDeccanHeraldSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul li a.card-cta[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.deccanHeraldBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.deccanHeraldSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Deccan Herald (Sports News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Deccan Herald (Sports News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Deccan Herald (Sports News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Deccan Herald (Sports News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDnaIndiaSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.dnaIndiaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dnaIndiaSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("DNA India (Sports News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("DNA India (Sports News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("DNA India (Sports News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("DNA India (Sports News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDeccanChronicleSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.col-sm-5.tsSmall a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.deccanChronicleBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.deccanChronicleSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Deccan Chronicle (Sports News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Deccan Chronicle (Sports News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Deccan Chronicle (Sports News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Deccan Chronicle (Sports News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setAsianAgeSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.top-stories-box.col-lg-6.col-md-6.col-sm-6 h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String linkWithExtras = allList.get(i).attr("href");
                String temporaryLink=linkWithExtras.substring(1);
                String link=MyUrl.asianAgeBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.asianAgeSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("The Asian Age (Sports News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("The Asian Age (Sports News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("The Asian Age (Sports News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("The Asian Age (Sports News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setEconomicTimesSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.eachStory h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.economicTimesBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.economicTimesSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Economic Times (Sports News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Economic Times (Sports News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Economic Times (Sports News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Economic Times (Sports News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBusinessStandardSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h2 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.businessStandardBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.businessStandardSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Business Standard (Sports News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Business Standard (Sports News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Business Standard (Sports News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Business Standard (Sports News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setFinancialExpressSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.financialExpressSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Financial Express (Sports News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Financial Express (Sports News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Financial Express (Sports News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Financial Express (Sports News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setNewIndianExpressSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h4 a+a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.newIndianExpressSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("The New Indian Express (Sports News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("The New Indian Express (Sports News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("The New Indian Express (Sports News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("The New Indian Express (Sports News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setTribuneIndiaSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h4 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.tribuneIndiaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.tribuneIndiaSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("The Tribune (Sports News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("The Tribune (Sports News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("The Tribune (Sports News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("The Tribune (Sports News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
//    ============================================================================================


    private void setNews24BdBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h2.post-title.title-small a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.news24Bd+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.news24Bd);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("নিউস ২৪ (সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("নিউস ২৪ (সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("নিউস ২৪ (সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("নিউস ২৪ (সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setJamunaTvBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h2.headline a.headline-link[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.jamunaTv);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("যমুনা টিভি (সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("যমুনা টিভি (সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("যমুনা টিভি (সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("যমুনা টিভি (সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setMyTvBdBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.td-block-row div.td-block-span6 div.item-details h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.myTvBd);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("মাই টিভি (সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("মাই টিভি (সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("মাই টিভি (সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("মাই টিভি (সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setMohonaTvBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul.news.slides li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.mohonaTv);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("মোহনা টিভি (সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("মোহনা টিভি (সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("মোহনা টিভি (সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("মোহনা টিভি (সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBoishakhiBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("marquee a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.boishakhiTv);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("বৈশাখী টিভি (সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("বৈশাখী টিভি (সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("বৈশাখী টিভি (সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("বৈশাখী টিভি (সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setRtvBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.editor_picks_list a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.rtvNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("আরটিভি নিউস (সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("আরটিভি নিউস (সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("আরটিভি নিউস (সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("আরটিভি নিউস (সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setBanglaVisionBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.breakingNews ul li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.banglaVision);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("বাংলা ভিশন (সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("বাংলা ভিশন (সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("বাংলা ভিশন (সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("বাংলা ভিশন (সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setChannelIBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h2.title a.post-url");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.channelI);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("চ্যানেল আই (সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("চ্যানেল আই (সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("চ্যানেল আই (সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("চ্যানেল আই (সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setShomoyBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a.spark");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.shomoyNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("সময় টিভি (সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("সময় টিভি (সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("সময় টিভি (সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("সময় টিভি (সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setNtvBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul.number-list.popular-news.pt-20 li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String linkWithExtras = allList.get(i).attr("href");
                String news = allList.get(i).text();
                String link=MyUrl.ntvBd+linkWithExtras.substring(1);
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.ntvBd);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("এনটিভি (সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("এনটিভি (সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("এনটিভি (সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("এনটিভি (সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setEkusheyTvBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.DTopNews2 a");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.ekusheyTelevision);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("একুশে টেলিভশন (সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("একুশে টেলিভশন (সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("একুশে টেলিভশন (সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("একুশে টেলিভশন (সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
//    ============================================================================================


    private void setZee24HoursTvChannelNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.mini-list-story.clearfix h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link="https://zeenews.india.com"+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.zeeNews24HoursTvChannelNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("জি ২৪ ঘণ্টা (সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("জি ২৪ ঘণ্টা (সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("জি ২৪ ঘণ্টা (সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("জি ২৪ ঘণ্টা (সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setAbpLiveTvChannelNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.uk-width-1-2.uk-first-column a.other_news[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.abpLiveTvChannelNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("আনন্দ (সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("আনন্দ (সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("আনন্দ (সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("আনন্দ (সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setNews18BengaliTvChannelNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul.top-kharein a.events_ana[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.news18BengaliTvChannelNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("নিউস ১৮ বাংলা (শীর্ষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("নিউস ১৮ বাংলা (শীর্ষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("নিউস ১৮ বাংলা (শীর্ষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("নিউস ১৮ বাংলা (শীর্ষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setNewsTimeBanglaTvChannelNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div#tabbed-2-recent ul li h4 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.newsTimeBanglaTvChannelNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("নিউস টাইম বাংলা (সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("নিউস টাইম বাংলা (সর্বশেষ খবর)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("নিউস টাইম বাংলা (সর্বশেষ খবর)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("নিউস টাইম বাংলা (সর্বশেষ খবর)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setCalcuttaNewsTvChannelNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a.brklink[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.calcuttaNewsTvChannelNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.calcuttaNewsTvChannelNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("কলকাতা নিউস (ব্রেকিং নিউস)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("কলকাতা নিউস (ব্রেকিং নিউস)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("কলকাতা নিউস (ব্রেকিং নিউস)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("কলকাতা নিউস (ব্রেকিং নিউস)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setKolkataTvTvChannelNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul.marque1 li");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).select("a[href]").attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.kolkataTvTvChannelNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("কলকাতা টিভি (ব্রেকিং নিউস)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("কলকাতা টিভি (ব্রেকিং নিউস)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("কলকাতা টিভি (ব্রেকিং নিউস)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("কলকাতা টিভি (ব্রেকিং নিউস)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setRPlusNewsTvChannelNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div#news-ticker ul li h2 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.rPlusTvChannelNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("আর প্লাস নিউস (এক নজরে)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("আর প্লাস নিউস (এক নজরে)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("আর প্লাস নিউস (এক নজরে)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("আর প্লাস নিউস (এক নজরে)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setIndianExpressBanglaTvChannelNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.gridnewsbox ul li p a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.indianExpressTvChannelNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("ইন্ডিয়ান এক্সপ্রেস বাংলা (ট্রেডিং নিউস)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("ইন্ডিয়ান এক্সপ্রেস বাংলা (ট্রেডিং নিউস)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("ইন্ডিয়ান এক্সপ্রেস বাংলা (ট্রেডিং নিউস)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("ইন্ডিয়ান এক্সপ্রেস বাংলা (ট্রেডিং নিউস)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
//    ============================================================================================


    private void setAbpHindiTvChannelNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.uk-grid-small.margThree.uk-grid div.uk-width-1-2.uk-first-column a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                if (news.length()>=11) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.abpLiveHindiTvChannelNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("एबीपी लाइव (खबर अभी)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("एबीपी लाइव (खबर अभी)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("एबीपी लाइव (खबर अभी)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("एबीपी लाइव (खबर अभी)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setInKhabarTvChannelNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.col-md-4.colm-lp.home-top-news ul li h3 a");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.inkhabarTvChannelNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("इनखबर (मुख्य समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("इनखबर (मुख्य समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("इनखबर (मुख्य समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("इनखबर (मुख्य समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setIndiaTvTvChannelNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.row.latest_news ul li h2 a");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.indiaTvTvChannelNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("इंडिया टीवी (ताज़ा खबर)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("इंडिया टीवी (ताज़ा खबर)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("इंडिया टीवी (ताज़ा खबर)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("इंडिया टीवी (ताज़ा खबर)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setKhabarNdTvTvChannelNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.featured_cont ul li a.item-title[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.khabarNdTvTvChannelNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("खबर इंडिया (अन्य बड़ी खबरें)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("खबर इंडिया (अन्य बड़ी खबरें)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("खबर इंडिया (अन्य बड़ी खबरें)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("खबर इंडिया (अन्य बड़ी खबरें)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setHindiNews24OnlineTvChannelNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h4 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.hindiNews24OnlineTvChannelNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.hindiNews24OnlineTvChannelNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("समाचार 24 ऑनलाइन (ट्रेंडिंग न्यूज़)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("समाचार 24 ऑनलाइन (ट्रेंडिंग न्यूज़)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("समाचार 24 ऑनलाइन (ट्रेंडिंग न्यूज़)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("समाचार 24 ऑनलाइन (ट्रेंडिंग न्यूज़)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setHindiMoneyControlTvChannelNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.todaynews.FR div.todnews_in ul li p a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.hindiMoneyControlTvChannelNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("धन नियंत्रण (समाचार सुर्खियों)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("धन नियंत्रण (समाचार सुर्खियों)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("धन नियंत्रण (समाचार सुर्खियों)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("धन नियंत्रण (समाचार सुर्खियों)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setIbc24TvChannelNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("owl-carousel-child ul.small-post.item li h3 a");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.ibc24TvChannelNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("IBC 24 (general news)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("IBC 24 (general news)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("IBC 24 (general news)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("IBC 24 (general news)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setHindiNews18TvChannelNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul.top-kharein li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.hindiNews18TvChannelNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.hindiNews18TvChannelNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("न्यूज 18 हिंदी (मुख्य समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("न्यूज 18 हिंदी (मुख्य समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("न्यूज 18 हिंदी (मुख्य समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("न्यूज 18 हिंदी (मुख्य समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setDdiNewsTvChannelNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.panel-panel.panel-col-first div.view-content ul.slides li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.ddiNewsTvChannelNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.ddiNewsTvChannelNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("DD न्यूज़ (ताज़ा खबर)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("DD न्यूज़ (ताज़ा खबर)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("DD न्यूज़ (ताज़ा खबर)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("DD न्यूज़ (ताज़ा खबर)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setNewsNationTvTvChannelNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.row.tab-top-story ul+ul li h2 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.newsNationTvTvChannelNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("समाचार राष्ट्र टीवी (सामान्य समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("समाचार राष्ट्र टीवी (सामान्य समाचार)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("समाचार राष्ट्र टीवी (सामान्य समाचार)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("समाचार राष्ट्र टीवी (सामान्य समाचार)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
//    ============================================================================================


    private void setBloombergQuintTvChannelNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.stories-scroller-m__story-headline__2gLve a.stories-scroller-m__story-link__3qVir[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.bloombergQuintTvChannelNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bloombergQuintTvChannelNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Bloomberg (Breaking News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Bloomberg (Breaking News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Bloomberg (Breaking News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Bloomberg (Breaking News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setCnbcTv18TvChannelNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.live_left ul li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                if (news.length()>=15) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.cnbcTv18TvChannelNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("CNBC TV 18 (Live Feed)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("CNBC TV 18 (Live Feed)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("CNBC TV 18 (Live Feed)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("CNBC TV 18 (Live Feed)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setNews18TvChannelNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul.lead-mstory li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.news18TvChannelNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("News 18 (General News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("News 18 (General News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("News 18 (General News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("News 18 (General News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setTimesNowNewsTvChannelNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.section-ten a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.timesNowNewsTvChannelNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.timesNowNewsTvChannelNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Times Now (Latest News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Times Now (Latest News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Times Now (Latest News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Times Now (Latest News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setNdtvTvChannelNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div[data-tb-region=top-stories] ul li h2 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                if (news.length()>=15) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.ndtvTvChannelNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("NDTV (Top Stories)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("NDTV (Top Stories)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("NDTV (Top Stories)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("NDTV (Top Stories)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setRepublicWorldTvChannelNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a[href^=https://www.republicworld.com/india-news/general-news/]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.republicWorldTvChannelNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Republic World (General News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Republic World (General News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Republic World (General News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Republic World (General News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setMirrorNowTvChannelNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.frame-one.more-articles a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.mirrorNowTvChannelNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Mirror Now (More Stories)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Mirror Now (More Stories)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Mirror Now (More Stories)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Mirror Now (More Stories)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setWioNewsTvChannelNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div#block-views-block-homepage-latestnews-block-6 h5 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.wioNewsTvChannelNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.wioNewsTvChannelNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Wionews (Trending News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Wionews (Trending News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Wionews (Trending News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Wionews (Trending News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
    private void setRomedyNowTvChannelNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.food-story a.story[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.romedyNowTvChannelNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Romedy Now (Food News)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=1) {
            displayNotification("Romedy Now (Food News)",list.get(0).getNews(),list.get(0).getLink());
        }
        if (list.size()>=2) {
            displayNotification("Romedy Now (Food News)",list.get(1).getNews(),list.get(1).getLink());
        }
        if (list.size()>=3) {
            displayNotification("Romedy Now (Food News)",list.get(2).getNews(),list.get(2).getLink());
        }
    }
//    ============================================================================================





}
