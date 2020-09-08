package com.easysoftbd.bangladeshindiannews.ui.fragments.bangladesh.breaking_news;



import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.easysoftbd.bangladeshindiannews.data.local.DatabaseClient;
import com.easysoftbd.bangladeshindiannews.data.local.NewsDatabase;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdBreaking;
import com.easysoftbd.bangladeshindiannews.data.model.NewsAndLinkModel;
import com.easysoftbd.bangladeshindiannews.data.model.RecyclerItemModel;
import com.easysoftbd.bangladeshindiannews.data.network.MyUrl;
import com.easysoftbd.bangladeshindiannews.data.repositories.MyResponse;
import com.easysoftbd.bangladeshindiannews.utils.Constants;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class BreakingNewsFragmentViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable;
    private NewsDatabase newsDatabase;
    private MyResponse myResponse;
    private Observer<List<BdBreaking>> allBreakingNewsObserver;
    private LiveData<List<BdBreaking>> bdBreakingLiveData;
    private MutableLiveData<List<RecyclerItemModel>> itemList;
    private List<RecyclerItemModel> temporaryList=new ArrayList<>();
    private boolean insertingDataFlag=false;




    public BreakingNewsFragmentViewModel(NewsDatabase newsDatabase) {
        this.newsDatabase=newsDatabase;
        if (myResponse == null) {
            myResponse = new MyResponse();
        }
        if (compositeDisposable==null) {
            compositeDisposable=new CompositeDisposable();
        }
    }

    public void loadPageDocument(String pageUrl) {
        myResponse.getPageDocument(pageUrl)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.rxjava3.core.Observer<Document>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Document document) {
                        if (document.baseUri().equalsIgnoreCase(MyUrl.bdProtidin)){
                            setBdProtidinBreekingNews(document);
                        }else if (document.baseUri().equalsIgnoreCase(MyUrl.prothomAlo)){
                            setProthomAloBreekingNews(document);
                        }else if (document.baseUri().equalsIgnoreCase(MyUrl.kalerKhanto)){
                            setKalerKhantoBreekingNews(document);
                        }else if (document.baseUri().equalsIgnoreCase(MyUrl.somokal)){
                            setSomokalBreekingNews(document);
                        }else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyJonoKhanto)){
                            setDailyJanaKhantoBreekingNews(document);
                        }else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyInqilab)){
                            setDailyInqilabBreekingNews(document);
                        }else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyNayaDiganta)){
                            setDailyNayaDigantaBreekingNews(document);
                        }else if (document.baseUri().equalsIgnoreCase(MyUrl.amarDesh24)){
                            setAmarDesh24BreekingNews(document);
                        }else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyIttefaq)){
                            setDailyIttefaqBreekingNews(document);
                        }else if (document.baseUri().equalsIgnoreCase(MyUrl.songbadProtidin)){
                            setSongbadProtidinBreekingNews(document);
                        }else if (document.baseUri().equalsIgnoreCase(MyUrl.manobKantha)){
                            setManobKanthaBreekingNews(document);
                        }else if (document.baseUri().equalsIgnoreCase(MyUrl.bangladeshJournal)){
                            setBangladeshJournalBreekingNews(document);
                        }else if (document.baseUri().equalsIgnoreCase(MyUrl.theDailyVorerPata)){
                            setTheDailyVorerPataBreekingNews(document);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

//====================================Primary method staying in above========================================


    public void checkBreakingNewsDataInDb(List<String> nameList, List<String> urlList) {
        if (allBreakingNewsObserver==null) {
            allBreakingNewsObserver= bdBreakings -> {
                Log.d(Constants.TAG,"db data size is:- "+bdBreakings.size());
                if (bdBreakings.size()>0 && !insertingDataFlag) {
                    for (int i=0; i<bdBreakings.size(); i++) {
                        if (bdBreakings.get(i).getVisibilityStatus().equalsIgnoreCase("visible")) {
                            loadPageDocument(bdBreakings.get(i).getPaperUrl());
                            Log.d(Constants.TAG,"url call:- "+i);
                        }
                    }
                } else {
                    insertingDataFlag=true;
                    for (int i=0; i<urlList.size(); i++) {
                        BdBreaking bdBreaking=new BdBreaking();
                        bdBreaking.setSerial(i);
                        bdBreaking.setVisibilityStatus("visible");
                        bdBreaking.setPaperUrl(urlList.get(i));
                        bdBreaking.setPaperName(nameList.get(i));
                        Log.d(Constants.TAG,"data insert:- "+i);
                        Completable.fromAction(()->{
                            newsDatabase.bdBreakingDao().insertNews(bdBreaking);
                        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe();

                    }
                    insertingDataFlag=false;
                }
            };
        }
        bdBreakingLiveData=newsDatabase.bdBreakingDao().getAllNews();
        bdBreakingLiveData.observeForever(allBreakingNewsObserver);

    }


    public void shortingList(List<RecyclerItemModel> recyclerItemModelList) {
        for (int i=0; i<recyclerItemModelList.size(); i++) {

        }
    }


    public LiveData<List<RecyclerItemModel>> getItemList() {
        if (itemList==null) {
            itemList=new MutableLiveData<>();
        }
        return itemList;
    }




/*
From below all necessary newspaper's document load and set to temporary list for shoring.
 */
    private void setProthomAloBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList = document.select("div.custom-story-card-4-data a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.prothomAlo);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("প্রথম আলো (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
//====================================ProthomAlo method staying in above========================================



    private void setBdProtidinBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList=document.select(".container > .jctkr-scroll > .js-conveyor-example ul li");
            for (int i=0; i<allList.size(); i++) {
                String temporaryLink=allList.get(i).select("a").attr("href");
                String news=allList.get(i).select("a").text();
                String link=MyUrl.bdProtidin+temporaryLink;
                NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(news,link);
                list.add(newsAndLinkModel);
            }
        }catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.bdProtidin);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("বাংলাদেশ প্রতিদিন (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
//====================================BdProtidin method staying in above========================================



    private void setKalerKhantoBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList=document.select("ul.content.mCustomScrollbar li");
            for (int i=0; i<allList.size(); i++) {
                String temporaryLink=allList.get(i).select("a").attr("href");
                String news=allList.get(i).select("a").text();
                String link=MyUrl.kalerKhanto+temporaryLink;
                if (news.length()>=15){
                    NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(news,link);
                    list.add(newsAndLinkModel);
                }
            }
        }catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.kalerKhanto);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG,"error is "+e.getMessage());
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("কালের কণ্ঠ (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
//====================================KalerKhanto method staying in above========================================



    private void setSomokalBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList=document.select("marquee a");
            for (int i=0; i<allList.size(); i++) {
                String link=allList.get(i).attr("href");
                String news=allList.get(i).select("a").text();
                NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(news,link);
                list.add(newsAndLinkModel);
            }
        }catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.somokal);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG,"error is "+e.getMessage());
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("সমকাল (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
//====================================Somokal method staying in above========================================



    private void setDailyJanaKhantoBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList=document.select("marquee a");
            for (int i=0; i<allList.size(); i++) {
                String temporaryLink=allList.get(i).attr("href");
                String news=allList.get(i).select("a").text();
                String link=MyUrl.dailyJonoKhanto+temporaryLink;
                NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(news,link);
                list.add(newsAndLinkModel);
            }
        }catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.somokal);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG,"error is "+e.getMessage());
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("দৈনিক জনকন্ঠ (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
//====================================DailyJanaKhanto method staying in above========================================




//====================================DailyJanaKhanto method staying in above========================================




    private void setDailyInqilabBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList=document.select("marquee ul li a");
            for (int i=0; i<allList.size(); i++) {
                String link=allList.get(i).attr("href");
                String news=allList.get(i).select("a").text();
                NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(news,link);
                list.add(newsAndLinkModel);
            }
        }catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.dailyInqilab);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG,"error is "+e.getMessage());
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("দৈনিক ইনকিলাব (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
//====================================DailyJanaKhanto method staying in above========================================



    private void setDailyNayaDigantaBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList=document.select("div.col-lg-7.col-md-5.col-sm-5.column-no-left-padding a");
            for (int i=0; i<allList.size(); i++) {
                String link=allList.get(i).attr("href");
                String news=allList.get(i).select("a").text();
                NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(news,link);
                list.add(newsAndLinkModel);
            }
        }catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.dailyNayaDiganta);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG,"error is "+e.getMessage());
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("নয়া দিগন্ত (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
//====================================DailyNayaDiganta method staying in above========================================



    private void setAmarDesh24BreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList=document.select("div.panel-body div.focus-item.clearfix h2 a");
            for (int i=0; i<allList.size(); i++) {
                String link=allList.get(i).attr("href");
                String news=allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(news,link);
                list.add(newsAndLinkModel);
            }
        }catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.amarDesh24);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG,"error is "+e.getMessage());
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("আমার দেশ 24 (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
//====================================DailyNayaDiganta method staying in above========================================


    private void setDailyIttefaqBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList=document.select("a[href]");
            for (int i=0; i<allList.size(); i++) {
                String link=allList.get(i).attr("href");
                String news=allList.get(i).text();
                if (news.length()>=20){
                    NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(news,link);
                    list.add(newsAndLinkModel);
                }
            }
        }catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.dailyIttefaq);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG,"error is "+e.getMessage());
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("দৈনিক ইত্তেফাক (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
//====================================DailyNayaDiganta method staying in above========================================



//====================================DailyNayaDiganta method staying in above========================================


    private void setSongbadProtidinBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList=document.select("div.bn-news ul li a");
            for (int i=0; i<allList.size(); i++) {
                String link=allList.get(i).attr("href");
                String news=allList.get(i).text();
                if (news.length()>=20){
                    NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(news,link);
                    list.add(newsAndLinkModel);
                }
            }
        }catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.songbadProtidin);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG,"error is "+e.getMessage());
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("সংবাদ প্রতিদিন (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
//====================================DailyNayaDiganta method staying in above========================================


    private void setManobKanthaBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList=document.select("div.ticker-container ul div li a[href]");
            for (int i=0; i<allList.size(); i++) {
                String link=allList.get(i).attr("href");
                String news=allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(news,link);
                list.add(newsAndLinkModel);
            }
        }catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.manobKantha);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG,"error is "+e.getMessage());
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("মানবকণ্ঠ (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
//====================================ManobKantha method staying in above========================================



    private void setBangladeshJournalBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList=document.select("marquee ul li a");
            for (int i=0; i<allList.size(); i++) {
                String link=allList.get(i).attr("href");
                String news=allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(news,link);
                list.add(newsAndLinkModel);
            }
        }catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.bangladeshJournal);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG,"error is "+e.getMessage());
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("বাংলাদেশ জার্নাল (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
//====================================BangladeshJournal method staying in above========================================


    private void setTheDailyVorerPataBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList=document.select("ul li[data-category=শিরোনাম] a");
            for (int i=0; i<allList.size(); i++) {
                String temporaryLink=allList.get(i).attr("href");
                String news=allList.get(i).text();
                String link=MyUrl.theDailyVorerPata+temporaryLink;
                NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(news,link);
                list.add(newsAndLinkModel);
            }
        }catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.theDailyVorerPata);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG,"error is "+e.getMessage());
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("ভোরের পাতা (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
//====================================BangladeshJournal method staying in above========================================
/*
From above all necessary newspaper's document load and set to temporary list for shoring.
 */




    @Override
    protected void onCleared() {
        bdBreakingLiveData.removeObserver(allBreakingNewsObserver);
        super.onCleared();
        compositeDisposable.dispose();
    }



}
