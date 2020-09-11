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
    private io.reactivex.rxjava3.disposables.CompositeDisposable anotherCompositeDisposable;
    private NewsDatabase newsDatabase;
    private MyResponse myResponse;
    private Observer<List<BdBreaking>> bangladeshiAllBreakingNewsObserver;
    private LiveData<List<BdBreaking>> bdBreakingLiveData;
    private MutableLiveData<List<RecyclerItemModel>> itemList;
    private MutableLiveData<List<RecyclerItemModel>> shortedList;
    private MutableLiveData<List<BdBreaking>> bdBreakingUnVisibleList;
    private List<BdBreaking> bdBreakingUnVisibleTemporaryList=new ArrayList<>();
    private List<RecyclerItemModel> temporaryList=new ArrayList<>();
    private boolean insertingDataFlag=false;
    private boolean dataStatusFlagInDb=false;
    private List<BdBreaking> bdBreakingList=new ArrayList<>();
    List<RecyclerItemModel> temporaryShortingList=new ArrayList<>();




    public BreakingNewsFragmentViewModel(NewsDatabase newsDatabase) {
        this.newsDatabase=newsDatabase;
        if (myResponse == null) {
            myResponse = new MyResponse();
        }
        if (compositeDisposable==null) {
            compositeDisposable=new CompositeDisposable();
        }
        if (anotherCompositeDisposable==null) {
            anotherCompositeDisposable=new io.reactivex.rxjava3.disposables.CompositeDisposable();
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


    public void shortingList(List<RecyclerItemModel> recyclerItemModelList) {
        if (shortedList==null) {
            shortedList=new MutableLiveData<>();
        }
        temporaryShortingList.clear();
        String title;
        RecyclerItemModel recyclerItemModel;
        for (int i=0; i<bdBreakingList.size(); i++) {
            title=bdBreakingList.get(i).getPaperName();
            for (int j=0; j<recyclerItemModelList.size(); j++) {
                if (title.equalsIgnoreCase(recyclerItemModelList.get(j).getTitle())) {
                    recyclerItemModel=recyclerItemModelList.get(j);
                    recyclerItemModel.setSerialNumber(bdBreakingList.get(i).getSerial());
                    temporaryShortingList.add(recyclerItemModelList.get(j));
                }
            }
        }
        shortedList.setValue(temporaryShortingList);
    }

    public LiveData<List<RecyclerItemModel>> getShortedList() {
        if (shortedList==null) {
            shortedList=new MutableLiveData<>();
        }
        return shortedList;
    }

    public LiveData<List<RecyclerItemModel>> getItemList() {
        if (itemList==null) {
            itemList=new MutableLiveData<>();
        }
        return itemList;
    }

    public LiveData<List<BdBreaking>> getBdBreakingUnVisibleList() {
        if (bdBreakingUnVisibleList==null) {
            bdBreakingUnVisibleList=new MutableLiveData<>();
        }
        return bdBreakingUnVisibleList;
    }

    public void increaseSerialNumber(int serialNumber) {
        if (serialNumber>0) {
            BdBreaking currentItem=bdBreakingList.get(serialNumber);
            BdBreaking upperItem=bdBreakingList.get(serialNumber-1);

            currentItem.setSerial(serialNumber-1);
            upperItem.setSerial(serialNumber);
            insertingDataFlag=true;
            dataStatusFlagInDb=true;



            Completable.fromAction(()->{
                newsDatabase.bdBreakingDao().updateNews(currentItem,upperItem);
            }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {
                    anotherCompositeDisposable.add(d);
                }

                @Override
                public void onComplete() {

                }

                @Override
                public void onError(@NonNull Throwable e) {

                }
            });
        }
    }

    public void decreaseSerialNumber(int serialNumber) {
        if (serialNumber<(bdBreakingList.size()-1)) {
            BdBreaking currentItem=bdBreakingList.get(serialNumber);
            BdBreaking downItem=bdBreakingList.get(serialNumber+1);

            currentItem.setSerial(serialNumber+1);
            downItem.setSerial(serialNumber);
            insertingDataFlag=true;
            dataStatusFlagInDb=true;



            Completable.fromAction(()->{
                newsDatabase.bdBreakingDao().updateNews(currentItem,downItem);
            }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {
                    anotherCompositeDisposable.add(d);
                }

                @Override
                public void onComplete() {

                }

                @Override
                public void onError(@NonNull Throwable e) {

                }
            });
        }
    }

    public void hideItem(int serialNumber) {
        if (serialNumber<=(bdBreakingList.size()-1) && serialNumber>=0) {
            BdBreaking currentItem=bdBreakingList.get(serialNumber);

            currentItem.setVisibilityStatus("hidden");
            insertingDataFlag=false;
            dataStatusFlagInDb=true;



            Completable.fromAction(()->{
                newsDatabase.bdBreakingDao().updateNews(currentItem);
            }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {
                    anotherCompositeDisposable.add(d);
                }

                @Override
                public void onComplete() {

                }

                @Override
                public void onError(@NonNull Throwable e) {

                }
            });
        }
    }

    public void visibleItem(String paperName) {
        for (int i=0; i<bdBreakingUnVisibleTemporaryList.size(); i++) {
            if (paperName.equalsIgnoreCase(bdBreakingUnVisibleTemporaryList.get(i).getPaperName())) {
                BdBreaking currentItem=bdBreakingUnVisibleTemporaryList.get(i);

                currentItem.setVisibilityStatus("visible");
                insertingDataFlag=false;
                dataStatusFlagInDb=true;



                Completable.fromAction(()->{
                    newsDatabase.bdBreakingDao().updateNews(currentItem);
                }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        anotherCompositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });
            }
        }
    }




    public void checkBangladeshBreakingNewsDataInDb(List<String> nameList, List<String> urlList) {
        if (bangladeshiAllBreakingNewsObserver==null) {
            bangladeshiAllBreakingNewsObserver= bdBreakings -> {
                bdBreakingList.clear();
                bdBreakingList.addAll(bdBreakings);
                bdBreakingUnVisibleTemporaryList.clear();
                if (dataStatusFlagInDb && itemList.getValue()!=null && itemList.getValue().size()>0) {
                    itemList.setValue(itemList.getValue());
                }
                Log.d(Constants.TAG,"db data size is:- "+bdBreakings.size());
                if (bdBreakings.size()>0 && !insertingDataFlag) {
                    temporaryList.clear();
                    itemList.setValue(temporaryList);
                    for (int i=0; i<bdBreakings.size(); i++) {
                        if (bdBreakings.get(i).getVisibilityStatus().equalsIgnoreCase("visible")) {
                            loadPageDocument(bdBreakings.get(i).getPaperUrl());
                            Log.d(Constants.TAG,"url call:- "+i);
                        } else {
                            bdBreakingUnVisibleTemporaryList.add(bdBreakings.get(i));
                        }
                    }
                    if (bdBreakingUnVisibleList==null) {
                        bdBreakingUnVisibleList=new MutableLiveData<>();
                    }
                    bdBreakingUnVisibleList.setValue(bdBreakingUnVisibleTemporaryList);
                    insertingDataFlag=true;
                } else {
                    insertingDataFlag=true;
                    if (nameList!=null && urlList!=null && !dataStatusFlagInDb) {
                        for (int i=0; i<urlList.size(); i++) {
                            BdBreaking bdBreaking=new BdBreaking();
                            bdBreaking.setSerial(i);
                            bdBreaking.setVisibilityStatus("visible");
                            bdBreaking.setPaperUrl(urlList.get(i));
                            bdBreaking.setPaperName(nameList.get(i));
                            Log.d(Constants.TAG,"data insert:- "+i);
                            Completable.fromAction(()->{
                                newsDatabase.bdBreakingDao().insertNews(bdBreaking);
                            }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
                                @Override
                                public void onSubscribe(@NonNull Disposable d) {
                                    anotherCompositeDisposable.add(d);
                                }

                                @Override
                                public void onComplete() {

                                }

                                @Override
                                public void onError(@NonNull Throwable e) {

                                }
                            });
                        }
                        dataStatusFlagInDb=true;
                    }
                    insertingDataFlag=false;
                }
            };
        }
        bdBreakingLiveData=newsDatabase.bdBreakingDao().getAllNews();
        bdBreakingLiveData.observeForever(bangladeshiAllBreakingNewsObserver);
    }






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
//====================================Bangladesh Breaking News method staying in above========================================




    @Override
    protected void onCleared() {
        bdBreakingLiveData.removeObserver(bangladeshiAllBreakingNewsObserver);
        super.onCleared();
        compositeDisposable.dispose();
        anotherCompositeDisposable.dispose();
    }



}
