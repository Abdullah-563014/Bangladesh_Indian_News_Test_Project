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
import com.easysoftbd.bangladeshindiannews.data.local.india.english.IndianEnglishTvChannel;
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


    private Observer<List<IndianEnglishTvChannel>> indianEnglishAllTvChannelNewsObserver;
    private LiveData<List<IndianEnglishTvChannel>> indianEnglishTvChannelLiveData;
    private MutableLiveData<List<IndianEnglishTvChannel>> indianEnglishTvChannelUnVisibleList;
    private List<IndianEnglishTvChannel> indianEnglishTvChannelList=new ArrayList<>();
    private List<IndianEnglishTvChannel> indianEnglishTvChannelUnVisibleTemporaryList=new ArrayList<>();






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


            IndianEnglishTvChannel indianEnglishTvChannelCurrentItem = null, indianEnglishTvChannelUpperItem = null;
            if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                indianEnglishTvChannelCurrentItem = indianEnglishTvChannelList.get(serialNumber);
                indianEnglishTvChannelUpperItem = indianEnglishTvChannelList.get(serialNumber - 1);

                indianEnglishTvChannelCurrentItem.setSerial(serialNumber - 1);
                indianEnglishTvChannelUpperItem.setSerial(serialNumber);
            }
            IndianEnglishTvChannel finalIndianEnglishTvChannelCurrentItem = indianEnglishTvChannelCurrentItem;
            IndianEnglishTvChannel finalIndianEnglishTvChannelUpperItem = indianEnglishTvChannelUpperItem;



            insertingDataFlag=true;
            dataStatusFlagInDb=true;

            Completable.fromAction(()->{
                if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                    newsDatabase.bdTvChannelDao().updateNews(finalBdTvChannelCurrentItem,finalBdTvChannelUpperItem);
                } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                    newsDatabase.indianBanglaTvChannelDao().updateNews(finalIndianBanglaTvChannelCurrentItem, finalIndianBanglaTvChannelUpperItem);
                } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                    newsDatabase.indianHindiTvChannelDao().updateNews(finalIndianHindiTvChannelCurrentItem, finalIndianHindiTvChannelUpperItem);
                } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                    newsDatabase.indianEnglishTvChannelDao().updateNews(finalIndianEnglishTvChannelCurrentItem, finalIndianEnglishTvChannelUpperItem);
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


        IndianEnglishTvChannel indianEnglishTvChannelCurrentItem = null, indianEnglishTvChannelDownItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            if (serialNumber < (indianEnglishTvChannelList.size() - 1)) {
                indianEnglishTvChannelCurrentItem = indianEnglishTvChannelList.get(serialNumber);
                indianEnglishTvChannelDownItem = indianEnglishTvChannelList.get(serialNumber + 1);

                indianEnglishTvChannelCurrentItem.setSerial(serialNumber + 1);
                indianEnglishTvChannelDownItem.setSerial(serialNumber);
            }
        }
        IndianEnglishTvChannel finalIndianEnglishTvChannelCurrentItem = indianEnglishTvChannelCurrentItem;
        IndianEnglishTvChannel finalIndianEnglishTvChannelDownItem = indianEnglishTvChannelDownItem;


        insertingDataFlag=true;
        dataStatusFlagInDb=true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdTvChannelDao().updateNews(finalBdTvChannelCurrentItem, finalBdTvChannelDownItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaTvChannelDao().updateNews(finalIndianBanglaTvChannelCurrentItem, finalIndianBanglaTvChannelDownItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiTvChannelDao().updateNews(finalIndianHindiTvChannelCurrentItem, finalIndianHindiTvChannelDownItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                newsDatabase.indianEnglishTvChannelDao().updateNews(finalIndianEnglishTvChannelCurrentItem, finalIndianEnglishTvChannelDownItem);
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


        IndianEnglishTvChannel indianEnglishTvChannelCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            if (serialNumber <= (indianEnglishTvChannelList.size() - 1) && serialNumber >= 0) {
                indianEnglishTvChannelCurrentItem = indianEnglishTvChannelList.get(serialNumber);
                indianEnglishTvChannelCurrentItem.setVisibilityStatus("hidden");
            }
        }
        IndianEnglishTvChannel finalIndianEnglishTvChannelCurrentItem = indianEnglishTvChannelCurrentItem;


        insertingDataFlag = false;
        dataStatusFlagInDb = true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdTvChannelDao().updateNews(finalBdTvChannelCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaTvChannelDao().updateNews(finalIndianBanglaTvChannelCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiTvChannelDao().updateNews(finalIndianHindiTvChannelCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                newsDatabase.indianEnglishTvChannelDao().updateNews(finalIndianEnglishTvChannelCurrentItem);
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


        IndianEnglishTvChannel indianEnglishTvChannelCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            for (int i = 0; i < indianEnglishTvChannelUnVisibleTemporaryList.size(); i++) {
                if (paperName.equalsIgnoreCase(indianEnglishTvChannelUnVisibleTemporaryList.get(i).getPaperName())) {
                    indianEnglishTvChannelCurrentItem = indianEnglishTvChannelUnVisibleTemporaryList.get(i);
                    indianEnglishTvChannelCurrentItem.setVisibilityStatus("visible");
                }
            }
        }
        IndianEnglishTvChannel finalIndianEnglishTvChannelCurrentItem = indianEnglishTvChannelCurrentItem;


        insertingDataFlag = false;
        dataStatusFlagInDb = true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdTvChannelDao().updateNews(finalBdTvChannelCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaTvChannelDao().updateNews(finalIndianBanglaTvChannelCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiTvChannelDao().updateNews(finalIndianHindiTvChannelCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                newsDatabase.indianEnglishTvChannelDao().updateNews(finalIndianEnglishTvChannelCurrentItem);
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


        IndianEnglishTvChannel indianEnglishTvChannelCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            indianEnglishTvChannelCurrentItem = indianEnglishTvChannelList.get(serialNumber);
            indianEnglishTvChannelCurrentItem.setBackgroundColor(colorName);
        }
        IndianEnglishTvChannel finalIndianEnglishTvChannelCurrentItem = indianEnglishTvChannelCurrentItem;



        insertingDataFlag=false;
        dataStatusFlagInDb=true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdTvChannelDao().updateNews(finalBdTvChannelCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaTvChannelDao().updateNews(finalIndianBanglaTvChannelCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiTvChannelDao().updateNews(finalIndianHindiTvChannelCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                newsDatabase.indianEnglishTvChannelDao().updateNews(finalIndianEnglishTvChannelCurrentItem);
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


        IndianEnglishTvChannel indianEnglishTvChannelCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            indianEnglishTvChannelCurrentItem = indianEnglishTvChannelList.get(serialNumber);
            indianEnglishTvChannelCurrentItem.setTextColor(colorName);
        }
        IndianEnglishTvChannel finalIndianEnglishTvChannelCurrentItem = indianEnglishTvChannelCurrentItem;



        insertingDataFlag=false;
        dataStatusFlagInDb=true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdTvChannelDao().updateNews(finalBdTvChannelCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaTvChannelDao().updateNews(finalIndianBanglaTvChannelCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiTvChannelDao().updateNews(finalIndianHindiTvChannelCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                newsDatabase.indianEnglishTvChannelDao().updateNews(finalIndianEnglishTvChannelCurrentItem);
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


    public void shortingIndianEnglishTvChannelList(List<RecyclerItemModel> recyclerItemModelList) {
        if (shortedList==null) {
            shortedList=new MutableLiveData<>();
        }
        temporaryShortingList.clear();
        String title;
        RecyclerItemModel recyclerItemModel;
        for (int i=0; i<indianEnglishTvChannelList.size(); i++) {
            title=indianEnglishTvChannelList.get(i).getPaperName();
            for (int j=0; j<recyclerItemModelList.size(); j++) {
                if (title.equalsIgnoreCase(recyclerItemModelList.get(j).getTitle())) {
                    recyclerItemModel=recyclerItemModelList.get(j);
                    recyclerItemModel.setSerialNumber(indianEnglishTvChannelList.get(i).getSerial());
                    recyclerItemModel.setBackgroundColor(indianEnglishTvChannelList.get(i).getBackgroundColor());
                    recyclerItemModel.setTextColor(indianEnglishTvChannelList.get(i).getTextColor());
                    temporaryShortingList.add(recyclerItemModel);
                }
            }
        }
        shortedList.setValue(temporaryShortingList);
    }
    public LiveData<List<IndianEnglishTvChannel>> getIndianEnglishTvChannelUnVisibleList() {
        if (indianEnglishTvChannelUnVisibleList==null) {
            indianEnglishTvChannelUnVisibleList=new MutableLiveData<>();
        }
        return indianEnglishTvChannelUnVisibleList;
    }
    public void checkIndianEnglishTvChannelNewsDataInDb(List<String> nameList, List<String> urlList) {
        if (indianEnglishAllTvChannelNewsObserver==null) {
            indianEnglishAllTvChannelNewsObserver= indianEnglishTvChannels -> {
                indianEnglishTvChannelList.clear();
                indianEnglishTvChannelList.addAll(indianEnglishTvChannels);
                indianEnglishTvChannelUnVisibleTemporaryList.clear();
                if (dataStatusFlagInDb && itemList.getValue()!=null && itemList.getValue().size()>0) {
                    itemList.setValue(itemList.getValue());
                }
                if (indianEnglishTvChannels.size()>0 && !insertingDataFlag) {
                    temporaryList.clear();
                    itemList.setValue(temporaryList);
                    for (int i=0; i<indianEnglishTvChannels.size(); i++) {
                        if (indianEnglishTvChannels.get(i).getVisibilityStatus().equalsIgnoreCase("visible")) {
                            loadPageDocument(indianEnglishTvChannels.get(i).getPaperUrl());
                        } else {
                            indianEnglishTvChannelUnVisibleTemporaryList.add(indianEnglishTvChannels.get(i));
                        }
                    }
                    if (indianEnglishTvChannelUnVisibleList==null) {
                        indianEnglishTvChannelUnVisibleList=new MutableLiveData<>();
                    }
                    indianEnglishTvChannelUnVisibleList.setValue(indianEnglishTvChannelUnVisibleTemporaryList);
                    insertingDataFlag=true;
                } else {
                    insertingDataFlag=true;
                    if (nameList!=null && urlList!=null && !dataStatusFlagInDb) {
                        for (int i=0; i<urlList.size(); i++) {
                            IndianEnglishTvChannel indianEnglishTvChannel=new IndianEnglishTvChannel();
                            indianEnglishTvChannel.setSerial(i);
                            indianEnglishTvChannel.setVisibilityStatus("visible");
                            indianEnglishTvChannel.setPaperUrl(urlList.get(i));
                            indianEnglishTvChannel.setPaperName(nameList.get(i));
                            indianEnglishTvChannel.setBackgroundColor("SkyBlue");
                            indianEnglishTvChannel.setTextColor("White");
                            Completable.fromAction(()->{
                                newsDatabase.indianEnglishTvChannelDao().insertNews(indianEnglishTvChannel);
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
        indianEnglishTvChannelLiveData=newsDatabase.indianEnglishTvChannelDao().getAllNews();
        indianEnglishTvChannelLiveData.observeForever(indianEnglishAllTvChannelNewsObserver);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setHindiNews24OnlineTvChannelNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.container div.row div.col-lg-2.col-md-2.col-sm-2.col-xs-6 h2 a[href]");
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }




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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setCnbcTv18TvChannelNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.live_left ul li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).select("div.list_des p strong").text();
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            indianEnglishTvChannelLiveData.removeObserver(indianEnglishAllTvChannelNewsObserver);
        }
        super.onCleared();
        compositeDisposable.dispose();
    }


}
