package com.easysoftbd.bangladeshindiannews.ui.fragments.tv_channel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.easysoftbd.bangladeshindiannews.data.local.NewsDatabase;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdFinance;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdTvChannel;
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

public class TvChannelNewsFragmentViewModel extends ViewModel {


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
    private Observer<List<BdTvChannel>> bangladeshiAllTvChannelNewsObserver;
    private LiveData<List<BdTvChannel>> bdTvChannelLiveData;
    private MutableLiveData<List<BdTvChannel>> bdTvChannelUnVisibleList;
    private List<BdTvChannel> bdTvChannelList=new ArrayList<>();
    private List<BdTvChannel> bdTvChannelUnVisibleTemporaryList=new ArrayList<>();




    public TvChannelNewsFragmentViewModel(NewsDatabase newsDatabase) {
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
                        if (document.baseUri().equalsIgnoreCase(MyUrl.ntvBd)){
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
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.jamunaTv)){
                            setJamunaTvBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.news24Bd)){
                            setNews24BdBreakingNews(document);
                        }

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


    public void shortingBdTvChannelList(List<RecyclerItemModel> recyclerItemModelList) {
        if (shortedList==null) {
            shortedList=new MutableLiveData<>();
        }
        temporaryShortingList.clear();
        String title;
        RecyclerItemModel recyclerItemModel;
        for (int i=0; i<bdTvChannelList.size(); i++) {
            title=bdTvChannelList.get(i).getPaperName();
            for (int j=0; j<recyclerItemModelList.size(); j++) {
                if (title.equalsIgnoreCase(recyclerItemModelList.get(j).getTitle())) {
                    recyclerItemModel=recyclerItemModelList.get(j);
                    recyclerItemModel.setSerialNumber(bdTvChannelList.get(i).getSerial());
                    recyclerItemModel.setBackgroundColor(bdTvChannelList.get(i).getBackgroundColor());
                    recyclerItemModel.setTextColor(bdTvChannelList.get(i).getTextColor());
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

    public LiveData<List<BdTvChannel>> getBdTvChannelUnVisibleList() {
        if (bdTvChannelUnVisibleList==null) {
            bdTvChannelUnVisibleList=new MutableLiveData<>();
        }
        return bdTvChannelUnVisibleList;
    }

    public LiveData<Integer> getItemMovedPosition() {
        if (itemMovePosition==null) {
            itemMovePosition=new MutableLiveData<>();
        }
        return itemMovePosition;
    }

    public void itemMoveToUp(int serialNumber) {
        if (serialNumber>0) {
            BdTvChannel currentItem=bdTvChannelList.get(serialNumber);
            BdTvChannel upperItem=bdTvChannelList.get(serialNumber-1);

            currentItem.setSerial(serialNumber-1);
            upperItem.setSerial(serialNumber);
            insertingDataFlag=true;
            dataStatusFlagInDb=true;



            Completable.fromAction(()->{
                newsDatabase.bdTvChannelDao().updateNews(currentItem,upperItem);
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
        if (serialNumber<(bdTvChannelList.size()-1)) {
            BdTvChannel currentItem=bdTvChannelList.get(serialNumber);
            BdTvChannel downItem=bdTvChannelList.get(serialNumber+1);

            currentItem.setSerial(serialNumber+1);
            downItem.setSerial(serialNumber);
            insertingDataFlag=true;
            dataStatusFlagInDb=true;



            Completable.fromAction(()->{
                newsDatabase.bdTvChannelDao().updateNews(currentItem,downItem);
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
        if (serialNumber<=(bdTvChannelList.size()-1) && serialNumber>=0) {
            BdTvChannel currentItem=bdTvChannelList.get(serialNumber);

            currentItem.setVisibilityStatus("hidden");
            insertingDataFlag=false;
            dataStatusFlagInDb=true;



            Completable.fromAction(()->{
                newsDatabase.bdTvChannelDao().updateNews(currentItem);
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
        for (int i=0; i<bdTvChannelUnVisibleTemporaryList.size(); i++) {
            if (paperName.equalsIgnoreCase(bdTvChannelUnVisibleTemporaryList.get(i).getPaperName())) {
                BdTvChannel currentItem=bdTvChannelUnVisibleTemporaryList.get(i);

                currentItem.setVisibilityStatus("visible");
                insertingDataFlag=false;
                dataStatusFlagInDb=true;



                Completable.fromAction(()->{
                    newsDatabase.bdTvChannelDao().updateNews(currentItem);
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
        BdTvChannel currentItem=bdTvChannelList.get(serialNumber);

        currentItem.setBackgroundColor(colorName);
        insertingDataFlag=false;
        dataStatusFlagInDb=true;



        Completable.fromAction(()->{
            newsDatabase.bdTvChannelDao().updateNews(currentItem);
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
        BdTvChannel currentItem=bdTvChannelList.get(serialNumber);

        currentItem.setTextColor(colorName);
        insertingDataFlag=false;
        dataStatusFlagInDb=true;



        Completable.fromAction(()->{
            newsDatabase.bdTvChannelDao().updateNews(currentItem);
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




    public void checkBangladeshTvChannelNewsDataInDb(List<String> nameList, List<String> urlList) {
        if (bangladeshiAllTvChannelNewsObserver==null) {
            bangladeshiAllTvChannelNewsObserver= bdTvChannels -> {
                bdTvChannelList.clear();
                bdTvChannelList.addAll(bdTvChannels);
                bdTvChannelUnVisibleTemporaryList.clear();
                if (dataStatusFlagInDb && itemList.getValue()!=null && itemList.getValue().size()>0) {
                    itemList.setValue(itemList.getValue());
                }
                if (bdTvChannels.size()>0 && !insertingDataFlag) {
                    temporaryList.clear();
                    itemList.setValue(temporaryList);
                    for (int i=0; i<bdTvChannels.size(); i++) {
                        if (bdTvChannels.get(i).getVisibilityStatus().equalsIgnoreCase("visible")) {
                            loadPageDocument(bdTvChannels.get(i).getPaperUrl());
                        } else {
                            bdTvChannelUnVisibleTemporaryList.add(bdTvChannels.get(i));
                        }
                    }
                    if (bdTvChannelUnVisibleList==null) {
                        bdTvChannelUnVisibleList=new MutableLiveData<>();
                    }
                    bdTvChannelUnVisibleList.setValue(bdTvChannelUnVisibleTemporaryList);
                    insertingDataFlag=true;
                } else {
                    insertingDataFlag=true;
                    if (nameList!=null && urlList!=null && !dataStatusFlagInDb) {
                        for (int i=0; i<urlList.size(); i++) {
                            BdTvChannel bdTvChannel=new BdTvChannel();
                            bdTvChannel.setSerial(i);
                            bdTvChannel.setVisibilityStatus("visible");
                            bdTvChannel.setPaperUrl(urlList.get(i));
                            bdTvChannel.setPaperName(nameList.get(i));
                            bdTvChannel.setBackgroundColor("SkyBlue");
                            bdTvChannel.setTextColor("White");
                            Completable.fromAction(()->{
                                newsDatabase.bdTvChannelDao().insertNews(bdTvChannel);
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
        bdTvChannelLiveData=newsDatabase.bdTvChannelDao().getAllNews();
        bdTvChannelLiveData.observeForever(bangladeshiAllTvChannelNewsObserver);
    }



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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setNtvBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.news-tracker-content.marquee.overflow-hidden ul li a");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String news = allList.get(i).text();
                String link=MyUrl.ntvBd+temporaryLink;
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }



    @Override
    protected void onCleared() {
        bdTvChannelLiveData.removeObserver(bangladeshiAllTvChannelNewsObserver);
        super.onCleared();
        compositeDisposable.dispose();
    }


}
