package com.easysoftbd.bangladeshindiannews.ui.fragments.entertainment;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.easysoftbd.bangladeshindiannews.data.local.NewsDatabase;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdEntertainment;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdSports;
import com.easysoftbd.bangladeshindiannews.data.model.NewsAndLinkModel;
import com.easysoftbd.bangladeshindiannews.data.model.RecyclerItemModel;
import com.easysoftbd.bangladeshindiannews.data.network.MyUrl;
import com.easysoftbd.bangladeshindiannews.data.repositories.MyResponse;
import com.easysoftbd.bangladeshindiannews.utils.Constants;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class EntertainmentFragmentViewModel extends ViewModel {


    private CompositeDisposable compositeDisposable;
    private NewsDatabase newsDatabase;
    private MyResponse myResponse;
    private MutableLiveData<List<RecyclerItemModel>> itemList;
    private MutableLiveData<List<RecyclerItemModel>> shortedList;
    private MutableLiveData<Integer> itemMovePosition;
    private List<RecyclerItemModel> temporaryList=new ArrayList<>();
    private List<RecyclerItemModel> temporaryShortingList=new ArrayList<>();
    private boolean insertingDataFlag=false;
    private boolean dataStatusFlagInDb=false;
    private Observer<List<BdEntertainment>> bangladeshiAllEntertainmentNewsObserver;
    private LiveData<List<BdEntertainment>> bdEntertainmentLiveData;
    private MutableLiveData<List<BdEntertainment>> bdEntertainmentUnVisibleList;
    private List<BdEntertainment> bdEntertainmentList=new ArrayList<>();
    private List<BdEntertainment> bdEntertainmentUnVisibleTemporaryList=new ArrayList<>();




    public EntertainmentFragmentViewModel(NewsDatabase newsDatabase) {
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
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull Document document) {
                        if (document.baseUri().equalsIgnoreCase(MyUrl.kalerKhanto)){
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
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyManobJominEntertainment)){
                            setDailyManobJominEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.sangbadpratidinEntertainment)){
                            setSangbadPratidinEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.manobkanthaEntertainment)){
                            setManobKanthaEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bdJournalEntertainment)){
                            setBdJournalEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dainikAmaderShomoyEntertainment)){
                            setDailyAmaderShomoyEntertainmentNews(document);
                        }
//                        Log.d(Constants.TAG,"sports:- "+document.baseUri());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(Constants.TAG,"document loading failed in loadPageDocument function for "+e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

//====================================Primary method staying in above========================================


    public void shortingBdEntertainmentList(List<RecyclerItemModel> recyclerItemModelList) {
        if (shortedList==null) {
            shortedList=new MutableLiveData<>();
        }
        temporaryShortingList.clear();
        String title;
        RecyclerItemModel recyclerItemModel;
        for (int i=0; i<bdEntertainmentList.size(); i++) {
            title=bdEntertainmentList.get(i).getPaperName();
            for (int j=0; j<recyclerItemModelList.size(); j++) {
                if (title.equalsIgnoreCase(recyclerItemModelList.get(j).getTitle())) {
                    recyclerItemModel=recyclerItemModelList.get(j);
                    recyclerItemModel.setSerialNumber(bdEntertainmentList.get(i).getSerial());
                    recyclerItemModel.setBackgroundColor(bdEntertainmentList.get(i).getBackgroundColor());
                    recyclerItemModel.setTextColor(bdEntertainmentList.get(i).getTextColor());
                    temporaryShortingList.add(recyclerItemModel);
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

    public LiveData<List<BdEntertainment>> getBdEntertainmentUnVisibleList() {
        if (bdEntertainmentUnVisibleList==null) {
            bdEntertainmentUnVisibleList=new MutableLiveData<>();
        }
        return bdEntertainmentUnVisibleList;
    }

    public LiveData<Integer> getItemMovedPosition() {
        if (itemMovePosition==null) {
            itemMovePosition=new MutableLiveData<>();
        }
        return itemMovePosition;
    }

    public void itemMoveToUp(int serialNumber) {
        if (serialNumber>0) {
            BdEntertainment currentItem=bdEntertainmentList.get(serialNumber);
            BdEntertainment upperItem=bdEntertainmentList.get(serialNumber-1);

            currentItem.setSerial(serialNumber-1);
            upperItem.setSerial(serialNumber);
            insertingDataFlag=true;
            dataStatusFlagInDb=true;



            Completable.fromAction(()->{
                newsDatabase.bdEntertainmentDao().updateNews(currentItem,upperItem);
            }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {
                    compositeDisposable.add(d);
                }

                @Override
                public void onComplete() {
                    itemMovePosition.setValue(serialNumber);
                }

                @Override
                public void onError(@NonNull Throwable e) {

                }
            });
        }
    }

    public void itemMoveToDown(int serialNumber) {
        if (serialNumber<(bdEntertainmentList.size()-1)) {
            BdEntertainment currentItem=bdEntertainmentList.get(serialNumber);
            BdEntertainment downItem=bdEntertainmentList.get(serialNumber+1);

            currentItem.setSerial(serialNumber+1);
            downItem.setSerial(serialNumber);
            insertingDataFlag=true;
            dataStatusFlagInDb=true;



            Completable.fromAction(()->{
                newsDatabase.bdEntertainmentDao().updateNews(currentItem,downItem);
            }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {
                    compositeDisposable.add(d);
                }

                @Override
                public void onComplete() {
                    itemMovePosition.setValue(serialNumber+2);
                }

                @Override
                public void onError(@NonNull Throwable e) {

                }
            });
        }
    }

    public void hideItem(int serialNumber) {
        if (serialNumber<=(bdEntertainmentList.size()-1) && serialNumber>=0) {
            BdEntertainment currentItem=bdEntertainmentList.get(serialNumber);

            currentItem.setVisibilityStatus("hidden");
            insertingDataFlag=false;
            dataStatusFlagInDb=true;



            Completable.fromAction(()->{
                newsDatabase.bdEntertainmentDao().updateNews(currentItem);
            }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {
                    compositeDisposable.add(d);
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
        for (int i=0; i<bdEntertainmentUnVisibleTemporaryList.size(); i++) {
            if (paperName.equalsIgnoreCase(bdEntertainmentUnVisibleTemporaryList.get(i).getPaperName())) {
                BdEntertainment currentItem=bdEntertainmentUnVisibleTemporaryList.get(i);

                currentItem.setVisibilityStatus("visible");
                insertingDataFlag=false;
                dataStatusFlagInDb=true;



                Completable.fromAction(()->{
                    newsDatabase.bdEntertainmentDao().updateNews(currentItem);
                }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
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

    public void changeItemBackgroundColor(int serialNumber,String colorName) {
        BdEntertainment currentItem=bdEntertainmentList.get(serialNumber);

        currentItem.setBackgroundColor(colorName);
        insertingDataFlag=false;
        dataStatusFlagInDb=true;



        Completable.fromAction(()->{
            newsDatabase.bdEntertainmentDao().updateNews(currentItem);
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }

    public void changeItemTextColor(int serialNumber,String colorName) {
        BdEntertainment currentItem=bdEntertainmentList.get(serialNumber);

        currentItem.setTextColor(colorName);
        insertingDataFlag=false;
        dataStatusFlagInDb=true;



        Completable.fromAction(()->{
            newsDatabase.bdEntertainmentDao().updateNews(currentItem);
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }




    public void checkBangladeshEntertainmentNewsDataInDb(List<String> nameList, List<String> urlList) {
        if (bangladeshiAllEntertainmentNewsObserver==null) {
            bangladeshiAllEntertainmentNewsObserver= bdEntertainments -> {
                bdEntertainmentList.clear();
                bdEntertainmentList.addAll(bdEntertainments);
                bdEntertainmentUnVisibleTemporaryList.clear();
                if (dataStatusFlagInDb && itemList.getValue()!=null && itemList.getValue().size()>0) {
                    itemList.setValue(itemList.getValue());
                }
                Log.d(Constants.TAG,"bd sports data size is:- "+bdEntertainments.size());
                if (bdEntertainments.size()>0 && !insertingDataFlag) {
                    temporaryList.clear();
                    itemList.setValue(temporaryList);
                    for (int i=0; i<bdEntertainments.size(); i++) {
                        if (bdEntertainments.get(i).getVisibilityStatus().equalsIgnoreCase("visible")) {
                            loadPageDocument(bdEntertainments.get(i).getPaperUrl());
                            Log.d(Constants.TAG,"bd sports url call:- "+i);
                        } else {
                            bdEntertainmentUnVisibleTemporaryList.add(bdEntertainments.get(i));
                        }
                    }
                    if (bdEntertainmentUnVisibleList==null) {
                        bdEntertainmentUnVisibleList=new MutableLiveData<>();
                    }
                    bdEntertainmentUnVisibleList.setValue(bdEntertainmentUnVisibleTemporaryList);
                    insertingDataFlag=true;
                } else {
                    insertingDataFlag=true;
                    if (nameList!=null && urlList!=null && !dataStatusFlagInDb) {
                        for (int i=0; i<urlList.size(); i++) {
                            BdEntertainment bdEntertainment=new BdEntertainment();
                            bdEntertainment.setSerial(i);
                            bdEntertainment.setVisibilityStatus("visible");
                            bdEntertainment.setPaperUrl(urlList.get(i));
                            bdEntertainment.setPaperName(nameList.get(i));
                            bdEntertainment.setBackgroundColor("SkyBlue");
                            bdEntertainment.setTextColor("White");
                            Log.d(Constants.TAG,"data insert:- "+i);
                            Completable.fromAction(()->{
                                newsDatabase.bdEntertainmentDao().insertNews(bdEntertainment);
                            }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
                                @Override
                                public void onSubscribe(@NonNull Disposable d) {
                                    compositeDisposable.add(d);
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
        bdEntertainmentLiveData=newsDatabase.bdEntertainmentDao().getAllNews();
        bdEntertainmentLiveData.observeForever(bangladeshiAllEntertainmentNewsObserver);
    }



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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setBanglaTribuneEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements newsList = document.select("h2.title_holder a[href]");
            for (int i = 0; i < newsList.size(); i++) {
                String news = newsList.get(i).text();
                String temporaryLink=newsList.get(i).attr("href");
                String link="https://www.banglatribune.com"+temporaryLink;
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.banglaTribuneEntertainment);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("বাংলা ট্রিবিউন (সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }



    @Override
    protected void onCleared() {
        bdEntertainmentLiveData.removeObserver(bangladeshiAllEntertainmentNewsObserver);
        super.onCleared();
        compositeDisposable.dispose();
    }

}
