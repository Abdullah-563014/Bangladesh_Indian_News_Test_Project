package com.easysoftbd.bangladeshindiannews.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
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
import com.easysoftbd.bangladeshindiannews.data.model.NewsAndLinkModel;
import com.easysoftbd.bangladeshindiannews.data.model.RecyclerItemModel;
import com.easysoftbd.bangladeshindiannews.data.network.MyUrl;
import com.easysoftbd.bangladeshindiannews.data.repositories.MyResponse;
import com.easysoftbd.bangladeshindiannews.ui.fragments.breaking_news.BreakingNewsFragment;
import com.easysoftbd.bangladeshindiannews.utils.CommonMethods;
import com.easysoftbd.bangladeshindiannews.utils.Constants;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

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
        displayNotification("Test title","Test description. Test description. Test description.");
//        newsPaperUrl="https://www.kalerkantho.com/";
//        loadPageDocument(newsPaperUrl);
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            loadBangladeshiNews();
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            loadIndianBanglaNews();
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            loadIndianHindiNews();
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            loadIndianEnglishNews();
        }
        return null;
    }

    @Override
    public void onStopped() {
        super.onStopped();
        compositeDisposable.dispose();
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
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyInqilab)) {
                            setDailyInqilabBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyNayaDiganta)) {
                            setDailyNayaDigantaBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.amarDesh24)) {
                            setAmarDesh24BreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyIttefaq)) {
                            setDailyIttefaqBreekingNews(document);
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

    private void displayNotification(String title, String description) {
        NotificationManager notificationManager= (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel=new NotificationChannel(notificationId,notificationId,NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder=new NotificationCompat.Builder(getApplicationContext(),notificationId)
                .setContentTitle(title)
                .setContentText(description)
                .setSmallIcon(R.mipmap.ic_launcher);

        notificationManager.notify(1,builder.build());
    }

    private void loadBangladeshiNews() {
        int categoryNumber=CommonMethods.getRandomNumber(6);
        switch (categoryNumber) {
            case 0:
                loadBangladeshBreakingNewsDataFromDb();
                break;

            case 1:
                loadBangladeshSportsNewsDataFromDb();
                break;

            case 2:
                loadBangladeshEntertainmentNewsDataFromDb();
                break;

            case 3:
                loadBangladeshFinanceNewsDataFromDb();
                break;

            case 4:
                loadBangladeshTvChannelNewsDataFromDb();
                break;

            case 5:
                loadBangladeshInternationalNewsDataFromDb();
                break;
        }
    }
    private void loadIndianBanglaNews() {
        int categoryNumber=CommonMethods.getRandomNumber(6);
        switch (categoryNumber) {
            case 0:
                loadBangladeshBreakingNewsDataFromDb();
                break;

            case 1:
                loadBangladeshSportsNewsDataFromDb();
                break;

            case 2:
                loadBangladeshEntertainmentNewsDataFromDb();
                break;

            case 3:
                loadBangladeshFinanceNewsDataFromDb();
                break;

            case 4:
                loadBangladeshTvChannelNewsDataFromDb();
                break;

            case 5:
                loadBangladeshInternationalNewsDataFromDb();
                break;
        }
    }
    private void loadIndianHindiNews() {
        int categoryNumber=CommonMethods.getRandomNumber(6);
        switch (categoryNumber) {
            case 0:
                loadBangladeshBreakingNewsDataFromDb();
                break;

            case 1:
                loadBangladeshSportsNewsDataFromDb();
                break;

            case 2:
                loadBangladeshEntertainmentNewsDataFromDb();
                break;

            case 3:
                loadBangladeshFinanceNewsDataFromDb();
                break;

            case 4:
                loadBangladeshTvChannelNewsDataFromDb();
                break;

            case 5:
                loadBangladeshInternationalNewsDataFromDb();
                break;
        }
    }
    private void loadIndianEnglishNews() {
        int categoryNumber=CommonMethods.getRandomNumber(6);
        switch (categoryNumber) {
            case 0:
                loadBangladeshBreakingNewsDataFromDb();
                break;

            case 1:
                loadBangladeshSportsNewsDataFromDb();
                break;

            case 2:
                loadBangladeshEntertainmentNewsDataFromDb();
                break;

            case 3:
                loadBangladeshFinanceNewsDataFromDb();
                break;

            case 4:
                loadBangladeshTvChannelNewsDataFromDb();
                break;

            case 5:
                loadBangladeshInternationalNewsDataFromDb();
                break;
        }
    }



    public void loadBangladeshBreakingNewsDataFromDb() {
        bdBreakingList.clear();
        bdBreakingList = newsDatabase.bdBreakingDao().getAllNotificationNews("on");
        if (bdBreakingList.size()>0) {
            int targetedNewsPaperIndex=CommonMethods.getRandomNumber(bdBreakingList.size());
            newsPaperUrl=bdBreakingList.get(targetedNewsPaperIndex).getPaperUrl();
            loadPageDocument(newsPaperUrl);
        }
        Log.d(Constants.TAG,"bd breaking list size is:- "+bdBreakingList.size());
    }
    public void loadBangladeshEntertainmentNewsDataFromDb() {
        bdEntertainmentList.clear();
        bdEntertainmentList = newsDatabase.bdEntertainmentDao().getAllNotificationNews("on");
        if (bdEntertainmentList.size()>0) {
            int targetedNewsPaperIndex=CommonMethods.getRandomNumber(bdEntertainmentList.size());
            newsPaperUrl=bdEntertainmentList.get(targetedNewsPaperIndex).getPaperUrl();
            loadPageDocument(newsPaperUrl);
        }
        Log.d(Constants.TAG,"bd entertainment list size is:- "+bdEntertainmentList.size());
    }
    public void loadBangladeshFinanceNewsDataFromDb() {
        bdFinanceList.clear();
        bdFinanceList = newsDatabase.bdFinanceDao().getAllNotificationNews("on");
        if (bdFinanceList.size()>0) {
            int targetedNewsPaperIndex=CommonMethods.getRandomNumber(bdFinanceList.size());
            newsPaperUrl=bdFinanceList.get(targetedNewsPaperIndex).getPaperUrl();
            loadPageDocument(newsPaperUrl);
        }
        Log.d(Constants.TAG,"bd finance list size is:- "+bdFinanceList.size());
    }
    public void loadBangladeshInternationalNewsDataFromDb() {
        bdInternationalList.clear();
        bdInternationalList = newsDatabase.bdInternationalDao().getAllNotificationNews("on");
        if (bdInternationalList.size()>0) {
            int targetedNewsPaperIndex=CommonMethods.getRandomNumber(bdInternationalList.size());
            newsPaperUrl=bdInternationalList.get(targetedNewsPaperIndex).getPaperUrl();
            loadPageDocument(newsPaperUrl);
        }
        Log.d(Constants.TAG,"bd international list size is:- "+bdInternationalList.size());
    }
    public void loadBangladeshSportsNewsDataFromDb() {
        bdSportsList.clear();
        bdSportsList = newsDatabase.bdSportsDao().getAllNotificationNews("on");
        if (bdSportsList.size()>0) {
            int targetedNewsPaperIndex=CommonMethods.getRandomNumber(bdSportsList.size());
            newsPaperUrl=bdSportsList.get(targetedNewsPaperIndex).getPaperUrl();
            loadPageDocument(newsPaperUrl);
        }
        Log.d(Constants.TAG,"bd sports list size is:- "+bdSportsList.size());
    }
    public void loadBangladeshTvChannelNewsDataFromDb() {
        bdTvChannelList.clear();
        bdTvChannelList = newsDatabase.bdTvChannelDao().getAllNotificationNews("on");
        if (bdTvChannelList.size()>0) {
            int targetedNewsPaperIndex=CommonMethods.getRandomNumber(bdTvChannelList.size());
            newsPaperUrl=bdTvChannelList.get(targetedNewsPaperIndex).getPaperUrl();
            loadPageDocument(newsPaperUrl);
        }
        Log.d(Constants.TAG,"bd tv channel list size is:- "+bdTvChannelList.size());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
        }
    }
//====================================Bangladesh Breaking News method staying in above========================================


    private void setAnandaBazarBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.abp-atf-left-story-block a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
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

        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
        }
    }
    private void setHariBhoomiBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div#home_top_right_level_1 ul.news-post.news-feature-mb li h4 a[href]");
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
        }
    }
    private void setDeshDootBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div[data-infinite-scroll=1] a[href^=https://www.deshdoot.com/]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.deshDootBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("देशदूत (स्थानीय समाचार)");
        itemModel.setNewsAndLinkModelList(list);
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
        }
    }
//====================================Indian Hindi Breaking News method staying in above========================================



    private void setHindustanTimesBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.new-topnews-left ul li h2 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
        }
    }
    private void setAsianAgeBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.single_left_coloum_wrapper.other-top-stories h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
        }
    }
    private void setLiveMintBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h2.headline a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
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
        if (list.size()>=2) {
            displayNotification(list.get(0).getNews(),list.get(1).getNews());
        }
    }
//====================================Indian English Breaking News method staying in above========================================



}
