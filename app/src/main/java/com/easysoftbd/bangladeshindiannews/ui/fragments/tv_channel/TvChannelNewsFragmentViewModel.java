package com.easysoftbd.bangladeshindiannews.ui.fragments.tv_channel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.easysoftbd.bangladeshindiannews.data.local.NewsDatabase;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdFinance;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdInternational;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdTvChannel;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaInternational;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaTvChannel;
import com.easysoftbd.bangladeshindiannews.data.local.india.hindi.IndianHindiTvChannel;
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
    private String countryName, languageName;

    private Observer<List<BdTvChannel>> bangladeshiAllTvChannelNewsObserver;
    private LiveData<List<BdTvChannel>> bdTvChannelLiveData;
    private MutableLiveData<List<BdTvChannel>> bdTvChannelUnVisibleList;
    private List<BdTvChannel> bdTvChannelList=new ArrayList<>();
    private List<BdTvChannel> bdTvChannelUnVisibleTemporaryList=new ArrayList<>();

    private Observer<List<IndianBanglaTvChannel>> indianBanglaAllTvChannelNewsObserver;
    private LiveData<List<IndianBanglaTvChannel>> indianBanglaTvChannelLiveData;
    private MutableLiveData<List<IndianBanglaTvChannel>> indianBanglaTvChannelUnVisibleList;
    private List<IndianBanglaTvChannel> indianBanglaTvChannelList=new ArrayList<>();
    private List<IndianBanglaTvChannel> indianBanglaTvChannelUnVisibleTemporaryList=new ArrayList<>();

    private Observer<List<IndianHindiTvChannel>> indianHindiAllTvChannelNewsObserver;
    private LiveData<List<IndianHindiTvChannel>> indianHindiTvChannelLiveData;
    private MutableLiveData<List<IndianHindiTvChannel>> indianHindiTvChannelUnVisibleList;
    private List<IndianHindiTvChannel> indianHindiTvChannelList=new ArrayList<>();
    private List<IndianHindiTvChannel> indianHindiTvChannelUnVisibleTemporaryList=new ArrayList<>();




    public TvChannelNewsFragmentViewModel(NewsDatabase newsDatabase, String countryName, String languageName) {
        this.newsDatabase=newsDatabase;
        this.countryName=countryName;
        this.languageName=languageName;

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
    public LiveData<Integer> getItemMovedPosition() {
        if (itemMovePosition==null) {
            itemMovePosition=new MutableLiveData<>();
        }
        return itemMovePosition;
    }
    public void itemMoveToUp(int serialNumber) {
        if (serialNumber>0) {
            BdTvChannel bdTvChannelCurrentItem = null, bdTvChannelUpperItem = null;
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                bdTvChannelCurrentItem = bdTvChannelList.get(serialNumber);
                bdTvChannelUpperItem = bdTvChannelList.get(serialNumber - 1);

                bdTvChannelCurrentItem.setSerial(serialNumber - 1);
                bdTvChannelUpperItem.setSerial(serialNumber);
            }
            BdTvChannel finalBdTvChannelCurrentItem = bdTvChannelCurrentItem;
            BdTvChannel finalBdTvChannelUpperItem = bdTvChannelUpperItem;


            IndianBanglaTvChannel indianBanglaTvChannelCurrentItem = null, indianBanglaTvChannelUpperItem = null;
            if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                indianBanglaTvChannelCurrentItem = indianBanglaTvChannelList.get(serialNumber);
                indianBanglaTvChannelUpperItem = indianBanglaTvChannelList.get(serialNumber - 1);

                indianBanglaTvChannelCurrentItem.setSerial(serialNumber - 1);
                indianBanglaTvChannelUpperItem.setSerial(serialNumber);
            }
            IndianBanglaTvChannel finalIndianBanglaTvChannelCurrentItem = indianBanglaTvChannelCurrentItem;
            IndianBanglaTvChannel finalIndianBanglaTvChannelUpperItem = indianBanglaTvChannelUpperItem;


            IndianHindiTvChannel indianHindiTvChannelCurrentItem = null, indianHindiTvChannelUpperItem = null;
            if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                indianHindiTvChannelCurrentItem = indianHindiTvChannelList.get(serialNumber);
                indianHindiTvChannelUpperItem = indianHindiTvChannelList.get(serialNumber - 1);

                indianHindiTvChannelCurrentItem.setSerial(serialNumber - 1);
                indianHindiTvChannelUpperItem.setSerial(serialNumber);
            }
            IndianHindiTvChannel finalIndianHindiTvChannelCurrentItem = indianHindiTvChannelCurrentItem;
            IndianHindiTvChannel finalIndianHindiTvChannelUpperItem = indianHindiTvChannelUpperItem;



            insertingDataFlag=true;
            dataStatusFlagInDb=true;

            Completable.fromAction(()->{
                if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                    newsDatabase.bdTvChannelDao().updateNews(finalBdTvChannelCurrentItem,finalBdTvChannelUpperItem);
                } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                    newsDatabase.indianBanglaTvChannelDao().updateNews(finalIndianBanglaTvChannelCurrentItem, finalIndianBanglaTvChannelUpperItem);
                } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                    newsDatabase.indianHindiTvChannelDao().updateNews(finalIndianHindiTvChannelCurrentItem, finalIndianHindiTvChannelUpperItem);
                }
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
        BdTvChannel bdTvChannelCurrentItem = null, bdTvChannelDownItem = null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            if (serialNumber < (bdTvChannelList.size() - 1)) {
                bdTvChannelCurrentItem = bdTvChannelList.get(serialNumber);
                bdTvChannelDownItem = bdTvChannelList.get(serialNumber + 1);

                bdTvChannelCurrentItem.setSerial(serialNumber + 1);
                bdTvChannelDownItem.setSerial(serialNumber);
            }
        }
        BdTvChannel finalBdTvChannelCurrentItem = bdTvChannelCurrentItem;
        BdTvChannel finalBdTvChannelDownItem = bdTvChannelDownItem;


        IndianBanglaTvChannel indianBanglaTvChannelCurrentItem = null, indianBanglaTvChannelDownItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            if (serialNumber < (indianBanglaTvChannelList.size() - 1)) {
                indianBanglaTvChannelCurrentItem = indianBanglaTvChannelList.get(serialNumber);
                indianBanglaTvChannelDownItem = indianBanglaTvChannelList.get(serialNumber + 1);

                indianBanglaTvChannelCurrentItem.setSerial(serialNumber + 1);
                indianBanglaTvChannelDownItem.setSerial(serialNumber);
            }
        }
        IndianBanglaTvChannel finalIndianBanglaTvChannelCurrentItem = indianBanglaTvChannelCurrentItem;
        IndianBanglaTvChannel finalIndianBanglaTvChannelDownItem = indianBanglaTvChannelDownItem;


        IndianHindiTvChannel indianHindiTvChannelCurrentItem = null, indianHindiTvChannelDownItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            if (serialNumber < (indianHindiTvChannelList.size() - 1)) {
                indianHindiTvChannelCurrentItem = indianHindiTvChannelList.get(serialNumber);
                indianHindiTvChannelDownItem = indianHindiTvChannelList.get(serialNumber + 1);

                indianHindiTvChannelCurrentItem.setSerial(serialNumber + 1);
                indianHindiTvChannelDownItem.setSerial(serialNumber);
            }
        }
        IndianHindiTvChannel finalIndianHindiTvChannelCurrentItem = indianHindiTvChannelCurrentItem;
        IndianHindiTvChannel finalIndianHindiTvChannelDownItem = indianHindiTvChannelDownItem;


        insertingDataFlag=true;
        dataStatusFlagInDb=true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdTvChannelDao().updateNews(finalBdTvChannelCurrentItem, finalBdTvChannelDownItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaTvChannelDao().updateNews(finalIndianBanglaTvChannelCurrentItem, finalIndianBanglaTvChannelDownItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiTvChannelDao().updateNews(finalIndianHindiTvChannelCurrentItem, finalIndianHindiTvChannelDownItem);
            }
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
    public void hideItem(int serialNumber) {
        BdTvChannel bdTvChannelCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            if (serialNumber <= (bdTvChannelList.size() - 1) && serialNumber >= 0) {
                bdTvChannelCurrentItem = bdTvChannelList.get(serialNumber);
                bdTvChannelCurrentItem.setVisibilityStatus("hidden");
            }
        }
        BdTvChannel finalBdTvChannelCurrentItem = bdTvChannelCurrentItem;


        IndianBanglaTvChannel indianBanglaTvChannelCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            if (serialNumber <= (indianBanglaTvChannelList.size() - 1) && serialNumber >= 0) {
                indianBanglaTvChannelCurrentItem = indianBanglaTvChannelList.get(serialNumber);
                indianBanglaTvChannelCurrentItem.setVisibilityStatus("hidden");
            }
        }
        IndianBanglaTvChannel finalIndianBanglaTvChannelCurrentItem = indianBanglaTvChannelCurrentItem;


        IndianHindiTvChannel indianHindiTvChannelCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            if (serialNumber <= (indianHindiTvChannelList.size() - 1) && serialNumber >= 0) {
                indianHindiTvChannelCurrentItem = indianHindiTvChannelList.get(serialNumber);
                indianHindiTvChannelCurrentItem.setVisibilityStatus("hidden");
            }
        }
        IndianHindiTvChannel finalIndianHindiTvChannelCurrentItem = indianHindiTvChannelCurrentItem;


        insertingDataFlag = false;
        dataStatusFlagInDb = true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdTvChannelDao().updateNews(finalBdTvChannelCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaTvChannelDao().updateNews(finalIndianBanglaTvChannelCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiTvChannelDao().updateNews(finalIndianHindiTvChannelCurrentItem);
            }
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
    public void visibleItem(String paperName) {
        BdTvChannel bdTvChannelCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            for (int i = 0; i < bdTvChannelUnVisibleTemporaryList.size(); i++) {
                if (paperName.equalsIgnoreCase(bdTvChannelUnVisibleTemporaryList.get(i).getPaperName())) {
                    bdTvChannelCurrentItem = bdTvChannelUnVisibleTemporaryList.get(i);
                    bdTvChannelCurrentItem.setVisibilityStatus("visible");
                }
            }
        }
        BdTvChannel finalBdTvChannelCurrentItem = bdTvChannelCurrentItem;


        IndianBanglaTvChannel indianBanglaTvChannelCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            for (int i = 0; i < indianBanglaTvChannelUnVisibleTemporaryList.size(); i++) {
                if (paperName.equalsIgnoreCase(indianBanglaTvChannelUnVisibleTemporaryList.get(i).getPaperName())) {
                    indianBanglaTvChannelCurrentItem = indianBanglaTvChannelUnVisibleTemporaryList.get(i);
                    indianBanglaTvChannelCurrentItem.setVisibilityStatus("visible");
                }
            }
        }
        IndianBanglaTvChannel finalIndianBanglaTvChannelCurrentItem = indianBanglaTvChannelCurrentItem;


        IndianHindiTvChannel indianHindiTvChannelCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            for (int i = 0; i < indianHindiTvChannelUnVisibleTemporaryList.size(); i++) {
                if (paperName.equalsIgnoreCase(indianHindiTvChannelUnVisibleTemporaryList.get(i).getPaperName())) {
                    indianHindiTvChannelCurrentItem = indianHindiTvChannelUnVisibleTemporaryList.get(i);
                    indianHindiTvChannelCurrentItem.setVisibilityStatus("visible");
                }
            }
        }
        IndianHindiTvChannel finalIndianHindiTvChannelCurrentItem = indianHindiTvChannelCurrentItem;


        insertingDataFlag = false;
        dataStatusFlagInDb = true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdTvChannelDao().updateNews(finalBdTvChannelCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaTvChannelDao().updateNews(finalIndianBanglaTvChannelCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiTvChannelDao().updateNews(finalIndianHindiTvChannelCurrentItem);
            }
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
    public void changeItemBackgroundColor(int serialNumber,String colorName) {
        BdTvChannel bdTvChannelCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            bdTvChannelCurrentItem = bdTvChannelList.get(serialNumber);
            bdTvChannelCurrentItem.setBackgroundColor(colorName);
        }
        BdTvChannel finalBdTvChannelCurrentItem = bdTvChannelCurrentItem;


        IndianBanglaTvChannel indianBanglaTvChannelCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            indianBanglaTvChannelCurrentItem = indianBanglaTvChannelList.get(serialNumber);
            indianBanglaTvChannelCurrentItem.setBackgroundColor(colorName);
        }
        IndianBanglaTvChannel finalIndianBanglaTvChannelCurrentItem = indianBanglaTvChannelCurrentItem;


        IndianHindiTvChannel indianHindiTvChannelCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            indianHindiTvChannelCurrentItem = indianHindiTvChannelList.get(serialNumber);
            indianHindiTvChannelCurrentItem.setBackgroundColor(colorName);
        }
        IndianHindiTvChannel finalIndianHindiTvChannelCurrentItem = indianHindiTvChannelCurrentItem;



        insertingDataFlag=false;
        dataStatusFlagInDb=true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdTvChannelDao().updateNews(finalBdTvChannelCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaTvChannelDao().updateNews(finalIndianBanglaTvChannelCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiTvChannelDao().updateNews(finalIndianHindiTvChannelCurrentItem);
            }
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
        BdTvChannel bdTvChannelCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            bdTvChannelCurrentItem = bdTvChannelList.get(serialNumber);
            bdTvChannelCurrentItem.setTextColor(colorName);
        }
        BdTvChannel finalBdTvChannelCurrentItem = bdTvChannelCurrentItem;


        IndianBanglaTvChannel indianBanglaTvChannelCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            indianBanglaTvChannelCurrentItem = indianBanglaTvChannelList.get(serialNumber);
            indianBanglaTvChannelCurrentItem.setTextColor(colorName);
        }
        IndianBanglaTvChannel finalIndianBanglaTvChannelCurrentItem = indianBanglaTvChannelCurrentItem;


        IndianHindiTvChannel indianHindiTvChannelCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            indianHindiTvChannelCurrentItem = indianHindiTvChannelList.get(serialNumber);
            indianHindiTvChannelCurrentItem.setTextColor(colorName);
        }
        IndianHindiTvChannel finalIndianHindiTvChannelCurrentItem = indianHindiTvChannelCurrentItem;



        insertingDataFlag=false;
        dataStatusFlagInDb=true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdTvChannelDao().updateNews(finalBdTvChannelCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaTvChannelDao().updateNews(finalIndianBanglaTvChannelCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiTvChannelDao().updateNews(finalIndianHindiTvChannelCurrentItem);
            }
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
    public LiveData<List<BdTvChannel>> getBdTvChannelUnVisibleList() {
        if (bdTvChannelUnVisibleList==null) {
            bdTvChannelUnVisibleList=new MutableLiveData<>();
        }
        return bdTvChannelUnVisibleList;
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


    public void shortingIndianBanglaTvChannelList(List<RecyclerItemModel> recyclerItemModelList) {
        if (shortedList==null) {
            shortedList=new MutableLiveData<>();
        }
        temporaryShortingList.clear();
        String title;
        RecyclerItemModel recyclerItemModel;
        for (int i=0; i<indianBanglaTvChannelList.size(); i++) {
            title=indianBanglaTvChannelList.get(i).getPaperName();
            for (int j=0; j<recyclerItemModelList.size(); j++) {
                if (title.equalsIgnoreCase(recyclerItemModelList.get(j).getTitle())) {
                    recyclerItemModel=recyclerItemModelList.get(j);
                    recyclerItemModel.setSerialNumber(indianBanglaTvChannelList.get(i).getSerial());
                    recyclerItemModel.setBackgroundColor(indianBanglaTvChannelList.get(i).getBackgroundColor());
                    recyclerItemModel.setTextColor(indianBanglaTvChannelList.get(i).getTextColor());
                    temporaryShortingList.add(recyclerItemModel);
                }
            }
        }
        shortedList.setValue(temporaryShortingList);
    }
    public LiveData<List<IndianBanglaTvChannel>> getIndianBanglaTvChannelUnVisibleList() {
        if (indianBanglaTvChannelUnVisibleList==null) {
            indianBanglaTvChannelUnVisibleList=new MutableLiveData<>();
        }
        return indianBanglaTvChannelUnVisibleList;
    }
    public void checkIndianBanglaTvChannelNewsDataInDb(List<String> nameList, List<String> urlList) {
        if (indianBanglaAllTvChannelNewsObserver==null) {
            indianBanglaAllTvChannelNewsObserver= indianBanglaTvChannels -> {
                indianBanglaTvChannelList.clear();
                indianBanglaTvChannelList.addAll(indianBanglaTvChannels);
                indianBanglaTvChannelUnVisibleTemporaryList.clear();
                if (dataStatusFlagInDb && itemList.getValue()!=null && itemList.getValue().size()>0) {
                    itemList.setValue(itemList.getValue());
                }
                if (indianBanglaTvChannels.size()>0 && !insertingDataFlag) {
                    temporaryList.clear();
                    itemList.setValue(temporaryList);
                    for (int i=0; i<indianBanglaTvChannels.size(); i++) {
                        if (indianBanglaTvChannels.get(i).getVisibilityStatus().equalsIgnoreCase("visible")) {
                            loadPageDocument(indianBanglaTvChannels.get(i).getPaperUrl());
                        } else {
                            indianBanglaTvChannelUnVisibleTemporaryList.add(indianBanglaTvChannels.get(i));
                        }
                    }
                    if (indianBanglaTvChannelUnVisibleList==null) {
                        indianBanglaTvChannelUnVisibleList=new MutableLiveData<>();
                    }
                    indianBanglaTvChannelUnVisibleList.setValue(indianBanglaTvChannelUnVisibleTemporaryList);
                    insertingDataFlag=true;
                } else {
                    insertingDataFlag=true;
                    if (nameList!=null && urlList!=null && !dataStatusFlagInDb) {
                        for (int i=0; i<urlList.size(); i++) {
                            IndianBanglaTvChannel indianBanglaTvChannel=new IndianBanglaTvChannel();
                            indianBanglaTvChannel.setSerial(i);
                            indianBanglaTvChannel.setVisibilityStatus("visible");
                            indianBanglaTvChannel.setPaperUrl(urlList.get(i));
                            indianBanglaTvChannel.setPaperName(nameList.get(i));
                            indianBanglaTvChannel.setBackgroundColor("SkyBlue");
                            indianBanglaTvChannel.setTextColor("White");
                            Completable.fromAction(()->{
                                newsDatabase.indianBanglaTvChannelDao().insertNews(indianBanglaTvChannel);
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
        indianBanglaTvChannelLiveData=newsDatabase.indianBanglaTvChannelDao().getAllNews();
        indianBanglaTvChannelLiveData.observeForever(indianBanglaAllTvChannelNewsObserver);
    }


    public void shortingIndianHindiTvChannelList(List<RecyclerItemModel> recyclerItemModelList) {
        if (shortedList==null) {
            shortedList=new MutableLiveData<>();
        }
        temporaryShortingList.clear();
        String title;
        RecyclerItemModel recyclerItemModel;
        for (int i=0; i<indianHindiTvChannelList.size(); i++) {
            title=indianHindiTvChannelList.get(i).getPaperName();
            for (int j=0; j<recyclerItemModelList.size(); j++) {
                if (title.equalsIgnoreCase(recyclerItemModelList.get(j).getTitle())) {
                    recyclerItemModel=recyclerItemModelList.get(j);
                    recyclerItemModel.setSerialNumber(indianHindiTvChannelList.get(i).getSerial());
                    recyclerItemModel.setBackgroundColor(indianHindiTvChannelList.get(i).getBackgroundColor());
                    recyclerItemModel.setTextColor(indianHindiTvChannelList.get(i).getTextColor());
                    temporaryShortingList.add(recyclerItemModel);
                }
            }
        }
        shortedList.setValue(temporaryShortingList);
    }
    public LiveData<List<IndianHindiTvChannel>> getIndianHindiTvChannelUnVisibleList() {
        if (indianHindiTvChannelUnVisibleList==null) {
            indianHindiTvChannelUnVisibleList=new MutableLiveData<>();
        }
        return indianHindiTvChannelUnVisibleList;
    }
    public void checkIndianHindiTvChannelNewsDataInDb(List<String> nameList, List<String> urlList) {
        if (indianHindiAllTvChannelNewsObserver==null) {
            indianHindiAllTvChannelNewsObserver= indianHindiTvChannels -> {
                indianHindiTvChannelList.clear();
                indianHindiTvChannelList.addAll(indianHindiTvChannels);
                indianHindiTvChannelUnVisibleTemporaryList.clear();
                if (dataStatusFlagInDb && itemList.getValue()!=null && itemList.getValue().size()>0) {
                    itemList.setValue(itemList.getValue());
                }
                if (indianHindiTvChannels.size()>0 && !insertingDataFlag) {
                    temporaryList.clear();
                    itemList.setValue(temporaryList);
                    for (int i=0; i<indianHindiTvChannels.size(); i++) {
                        if (indianHindiTvChannels.get(i).getVisibilityStatus().equalsIgnoreCase("visible")) {
                            loadPageDocument(indianHindiTvChannels.get(i).getPaperUrl());
                        } else {
                            indianHindiTvChannelUnVisibleTemporaryList.add(indianHindiTvChannels.get(i));
                        }
                    }
                    if (indianHindiTvChannelUnVisibleList==null) {
                        indianHindiTvChannelUnVisibleList=new MutableLiveData<>();
                    }
                    indianHindiTvChannelUnVisibleList.setValue(indianHindiTvChannelUnVisibleTemporaryList);
                    insertingDataFlag=true;
                } else {
                    insertingDataFlag=true;
                    if (nameList!=null && urlList!=null && !dataStatusFlagInDb) {
                        for (int i=0; i<urlList.size(); i++) {
                            IndianHindiTvChannel indianHindiTvChannel=new IndianHindiTvChannel();
                            indianHindiTvChannel.setSerial(i);
                            indianHindiTvChannel.setVisibilityStatus("visible");
                            indianHindiTvChannel.setPaperUrl(urlList.get(i));
                            indianHindiTvChannel.setPaperName(nameList.get(i));
                            indianHindiTvChannel.setBackgroundColor("SkyBlue");
                            indianHindiTvChannel.setTextColor("White");
                            Completable.fromAction(()->{
                                newsDatabase.indianHindiTvChannelDao().insertNews(indianHindiTvChannel);
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
        indianHindiTvChannelLiveData=newsDatabase.indianHindiTvChannelDao().getAllNews();
        indianHindiTvChannelLiveData.observeForever(indianHindiAllTvChannelNewsObserver);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setCalcuttaNewsTvChannelNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.td_module_trending_now.td-trending-now-post h3.entry-title.td-module-title a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }



    @Override
    protected void onCleared() {
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            bdTvChannelLiveData.removeObserver(bangladeshiAllTvChannelNewsObserver);
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            indianBanglaTvChannelLiveData.removeObserver(indianBanglaAllTvChannelNewsObserver);
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            indianHindiTvChannelLiveData.removeObserver(indianHindiAllTvChannelNewsObserver);
        }
        super.onCleared();
        compositeDisposable.dispose();
    }


}
