package com.easysoftbd.bangladeshindiannews.ui.fragments.international;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.easysoftbd.bangladeshindiannews.data.local.NewsDatabase;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdFinance;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdInternational;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdTvChannel;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaFinance;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaInternational;
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

public class InternationalFragmentViewModel extends ViewModel {

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

    private Observer<List<BdInternational>> bangladeshiAllInternationalNewsObserver;
    private LiveData<List<BdInternational>> bdInternationalLiveData;
    private MutableLiveData<List<BdInternational>> bdInternationalUnVisibleList;
    private List<BdInternational> bdInternationalList=new ArrayList<>();
    private List<BdInternational> bdInternationalUnVisibleTemporaryList=new ArrayList<>();

    private Observer<List<IndianBanglaInternational>> indianBanglaAllInternationalNewsObserver;
    private LiveData<List<IndianBanglaInternational>> indianBanglaInternationalLiveData;
    private MutableLiveData<List<IndianBanglaInternational>> indianBanglaInternationalUnVisibleList;
    private List<IndianBanglaInternational> indianBanglaInternationalList=new ArrayList<>();
    private List<IndianBanglaInternational> indianBanglaInternationalUnVisibleTemporaryList=new ArrayList<>();



    public InternationalFragmentViewModel(NewsDatabase newsDatabase, String countryName, String languageName) {
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
                        if (document.baseUri().equalsIgnoreCase(MyUrl.kalerKhanto)){
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
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.manobZaminInternational)){
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
            BdInternational bdInternationalCurrentItem = null, bdInternationalUpperItem = null;
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                bdInternationalCurrentItem = bdInternationalList.get(serialNumber);
                bdInternationalUpperItem = bdInternationalList.get(serialNumber - 1);

                bdInternationalCurrentItem.setSerial(serialNumber - 1);
                bdInternationalUpperItem.setSerial(serialNumber);
            }
            BdInternational finalBdInternationalCurrentItem = bdInternationalCurrentItem;
            BdInternational finalBdInternationalUpperItem = bdInternationalUpperItem;


            IndianBanglaInternational indianBanglaInternationalCurrentItem = null, indianBanglaInternationalUpperItem = null;
            if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                indianBanglaInternationalCurrentItem = indianBanglaInternationalList.get(serialNumber);
                indianBanglaInternationalUpperItem = indianBanglaInternationalList.get(serialNumber - 1);

                indianBanglaInternationalCurrentItem.setSerial(serialNumber - 1);
                indianBanglaInternationalUpperItem.setSerial(serialNumber);
            }
            IndianBanglaInternational finalIndianBanglaInternationalCurrentItem = indianBanglaInternationalCurrentItem;
            IndianBanglaInternational finalIndianBanglaInternationalUpperItem = indianBanglaInternationalUpperItem;



            insertingDataFlag=true;
            dataStatusFlagInDb=true;

            Completable.fromAction(()->{
                if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                    newsDatabase.bdInternationalDao().updateNews(finalBdInternationalCurrentItem,finalBdInternationalUpperItem);
                } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                    newsDatabase.indianBanglaInternationalDao().updateNews(finalIndianBanglaInternationalCurrentItem, finalIndianBanglaInternationalUpperItem);
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
        BdInternational bdInternationalCurrentItem = null, bdInternationalDownItem = null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            if (serialNumber < (bdInternationalList.size() - 1)) {
                bdInternationalCurrentItem = bdInternationalList.get(serialNumber);
                bdInternationalDownItem = bdInternationalList.get(serialNumber + 1);

                bdInternationalCurrentItem.setSerial(serialNumber + 1);
                bdInternationalDownItem.setSerial(serialNumber);
            }
        }
        BdInternational finalBdInternationalCurrentItem = bdInternationalCurrentItem;
        BdInternational finalBdInternationalDownItem = bdInternationalDownItem;


        IndianBanglaInternational indianBanglaInternationalCurrentItem = null, indianBanglaInternationalDownItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            if (serialNumber < (indianBanglaInternationalList.size() - 1)) {
                indianBanglaInternationalCurrentItem = indianBanglaInternationalList.get(serialNumber);
                indianBanglaInternationalDownItem = indianBanglaInternationalList.get(serialNumber + 1);

                indianBanglaInternationalCurrentItem.setSerial(serialNumber + 1);
                indianBanglaInternationalDownItem.setSerial(serialNumber);
            }
        }
        IndianBanglaInternational finalIndianBanglaInternationalCurrentItem = indianBanglaInternationalCurrentItem;
        IndianBanglaInternational finalIndianBanglaInternationalDownItem = indianBanglaInternationalDownItem;


        insertingDataFlag=true;
        dataStatusFlagInDb=true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdInternationalDao().updateNews(finalBdInternationalCurrentItem, finalBdInternationalDownItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaInternationalDao().updateNews(finalIndianBanglaInternationalCurrentItem, finalIndianBanglaInternationalDownItem);
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
        BdInternational bdInternationalCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            if (serialNumber <= (bdInternationalList.size() - 1) && serialNumber >= 0) {
                bdInternationalCurrentItem = bdInternationalList.get(serialNumber);
                bdInternationalCurrentItem.setVisibilityStatus("hidden");
            }
        }
        BdInternational finalBdInternationalCurrentItem = bdInternationalCurrentItem;


        IndianBanglaInternational indianBanglaInternationalCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            if (serialNumber <= (indianBanglaInternationalList.size() - 1) && serialNumber >= 0) {
                indianBanglaInternationalCurrentItem = indianBanglaInternationalList.get(serialNumber);
                indianBanglaInternationalCurrentItem.setVisibilityStatus("hidden");
            }
        }
        IndianBanglaInternational finalIndianBanglaInternationalCurrentItem = indianBanglaInternationalCurrentItem;


        insertingDataFlag = false;
        dataStatusFlagInDb = true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdInternationalDao().updateNews(finalBdInternationalCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaInternationalDao().updateNews(finalIndianBanglaInternationalCurrentItem);
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
        BdInternational bdInternationalCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            for (int i = 0; i < bdInternationalUnVisibleTemporaryList.size(); i++) {
                if (paperName.equalsIgnoreCase(bdInternationalUnVisibleTemporaryList.get(i).getPaperName())) {
                    bdInternationalCurrentItem = bdInternationalUnVisibleTemporaryList.get(i);
                    bdInternationalCurrentItem.setVisibilityStatus("visible");
                }
            }
        }
        BdInternational finalBdInternationalCurrentItem = bdInternationalCurrentItem;


        IndianBanglaInternational indianBanglaInternationalCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            for (int i = 0; i < indianBanglaInternationalUnVisibleTemporaryList.size(); i++) {
                if (paperName.equalsIgnoreCase(indianBanglaInternationalUnVisibleTemporaryList.get(i).getPaperName())) {
                    indianBanglaInternationalCurrentItem = indianBanglaInternationalUnVisibleTemporaryList.get(i);
                    indianBanglaInternationalCurrentItem.setVisibilityStatus("visible");
                }
            }
        }
        IndianBanglaInternational finalIndianBanglaInternationalCurrentItem = indianBanglaInternationalCurrentItem;

        insertingDataFlag = false;
        dataStatusFlagInDb = true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdInternationalDao().updateNews(finalBdInternationalCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaInternationalDao().updateNews(finalIndianBanglaInternationalCurrentItem);
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
        BdInternational bdInternationalCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            bdInternationalCurrentItem = bdInternationalList.get(serialNumber);
            bdInternationalCurrentItem.setBackgroundColor(colorName);
        }
        BdInternational finalBdInternationalCurrentItem = bdInternationalCurrentItem;


        IndianBanglaInternational indianBanglaInternationalCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            indianBanglaInternationalCurrentItem = indianBanglaInternationalList.get(serialNumber);
            indianBanglaInternationalCurrentItem.setBackgroundColor(colorName);
        }
        IndianBanglaInternational finalIndianBanglaInternationalCurrentItem = indianBanglaInternationalCurrentItem;



        insertingDataFlag=false;
        dataStatusFlagInDb=true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdInternationalDao().updateNews(finalBdInternationalCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaInternationalDao().updateNews(finalIndianBanglaInternationalCurrentItem);
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
        BdInternational bdInternationalCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            bdInternationalCurrentItem = bdInternationalList.get(serialNumber);
            bdInternationalCurrentItem.setTextColor(colorName);
        }
        BdInternational finalBdInternationalCurrentItem = bdInternationalCurrentItem;


        IndianBanglaInternational indianBanglaInternationalCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            indianBanglaInternationalCurrentItem = indianBanglaInternationalList.get(serialNumber);
            indianBanglaInternationalCurrentItem.setTextColor(colorName);
        }
        IndianBanglaInternational finalIndianBanglaInternationalCurrentItem = indianBanglaInternationalCurrentItem;



        insertingDataFlag=false;
        dataStatusFlagInDb=true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdInternationalDao().updateNews(finalBdInternationalCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaInternationalDao().updateNews(finalIndianBanglaInternationalCurrentItem);
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



    public void shortingBdInternationalList(List<RecyclerItemModel> recyclerItemModelList) {
        if (shortedList==null) {
            shortedList=new MutableLiveData<>();
        }
        temporaryShortingList.clear();
        String title;
        RecyclerItemModel recyclerItemModel;
        for (int i=0; i<bdInternationalList.size(); i++) {
            title=bdInternationalList.get(i).getPaperName();
            for (int j=0; j<recyclerItemModelList.size(); j++) {
                if (title.equalsIgnoreCase(recyclerItemModelList.get(j).getTitle())) {
                    recyclerItemModel=recyclerItemModelList.get(j);
                    recyclerItemModel.setSerialNumber(bdInternationalList.get(i).getSerial());
                    recyclerItemModel.setBackgroundColor(bdInternationalList.get(i).getBackgroundColor());
                    recyclerItemModel.setTextColor(bdInternationalList.get(i).getTextColor());
                    temporaryShortingList.add(recyclerItemModel);
                }
            }
        }
        shortedList.setValue(temporaryShortingList);
    }
    public LiveData<List<BdInternational>> getBdInternationalUnVisibleList() {
        if (bdInternationalUnVisibleList==null) {
            bdInternationalUnVisibleList=new MutableLiveData<>();
        }
        return bdInternationalUnVisibleList;
    }
    public void checkBangladeshInternationalNewsDataInDb(List<String> nameList, List<String> urlList) {
        if (bangladeshiAllInternationalNewsObserver==null) {
            bangladeshiAllInternationalNewsObserver= bdInternationals -> {
                bdInternationalList.clear();
                bdInternationalList.addAll(bdInternationals);
                bdInternationalUnVisibleTemporaryList.clear();
                if (dataStatusFlagInDb && itemList.getValue()!=null && itemList.getValue().size()>0) {
                    itemList.setValue(itemList.getValue());
                }
                Log.d(Constants.TAG,"bd international data size is:- "+bdInternationals.size());
                if (bdInternationals.size()>0 && !insertingDataFlag) {
                    temporaryList.clear();
                    itemList.setValue(temporaryList);
                    for (int i=0; i<bdInternationals.size(); i++) {
                        if (bdInternationals.get(i).getVisibilityStatus().equalsIgnoreCase("visible")) {
                            loadPageDocument(bdInternationals.get(i).getPaperUrl());
                            Log.d(Constants.TAG,"bd sports url call:- "+i);
                        } else {
                            bdInternationalUnVisibleTemporaryList.add(bdInternationals.get(i));
                        }
                    }
                    if (bdInternationalUnVisibleList==null) {
                        bdInternationalUnVisibleList=new MutableLiveData<>();
                    }
                    bdInternationalUnVisibleList.setValue(bdInternationalUnVisibleTemporaryList);
                    insertingDataFlag=true;
                } else {
                    insertingDataFlag=true;
                    if (nameList!=null && urlList!=null && !dataStatusFlagInDb) {
                        for (int i=0; i<urlList.size(); i++) {
                            BdInternational bdInternational=new BdInternational();
                            bdInternational.setSerial(i);
                            bdInternational.setVisibilityStatus("visible");
                            bdInternational.setPaperUrl(urlList.get(i));
                            bdInternational.setPaperName(nameList.get(i));
                            bdInternational.setBackgroundColor("SkyBlue");
                            bdInternational.setTextColor("White");
                            Log.d(Constants.TAG,"data insert:- "+i);
                            Completable.fromAction(()->{
                                newsDatabase.bdInternationalDao().insertNews(bdInternational);
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
        bdInternationalLiveData=newsDatabase.bdInternationalDao().getAllNews();
        bdInternationalLiveData.observeForever(bangladeshiAllInternationalNewsObserver);
    }


    public void shortingIndianBanglaInternationalList(List<RecyclerItemModel> recyclerItemModelList) {
        if (shortedList==null) {
            shortedList=new MutableLiveData<>();
        }
        temporaryShortingList.clear();
        String title;
        RecyclerItemModel recyclerItemModel;
        for (int i=0; i<indianBanglaInternationalList.size(); i++) {
            title=indianBanglaInternationalList.get(i).getPaperName();
            for (int j=0; j<recyclerItemModelList.size(); j++) {
                if (title.equalsIgnoreCase(recyclerItemModelList.get(j).getTitle())) {
                    recyclerItemModel=recyclerItemModelList.get(j);
                    recyclerItemModel.setSerialNumber(indianBanglaInternationalList.get(i).getSerial());
                    recyclerItemModel.setBackgroundColor(indianBanglaInternationalList.get(i).getBackgroundColor());
                    recyclerItemModel.setTextColor(indianBanglaInternationalList.get(i).getTextColor());
                    temporaryShortingList.add(recyclerItemModel);
                }
            }
        }
        shortedList.setValue(temporaryShortingList);
    }
    public LiveData<List<IndianBanglaInternational>> getIndianBanglaInternationalUnVisibleList() {
        if (indianBanglaInternationalUnVisibleList==null) {
            indianBanglaInternationalUnVisibleList=new MutableLiveData<>();
        }
        return indianBanglaInternationalUnVisibleList;
    }
    public void checkIndianBanglaInternationalNewsDataInDb(List<String> nameList, List<String> urlList) {
        if (indianBanglaAllInternationalNewsObserver==null) {
            indianBanglaAllInternationalNewsObserver= indianBanglaInternationals -> {
                indianBanglaInternationalList.clear();
                indianBanglaInternationalList.addAll(indianBanglaInternationals);
                indianBanglaInternationalUnVisibleTemporaryList.clear();
                if (dataStatusFlagInDb && itemList.getValue()!=null && itemList.getValue().size()>0) {
                    itemList.setValue(itemList.getValue());
                }
                if (indianBanglaInternationals.size()>0 && !insertingDataFlag) {
                    temporaryList.clear();
                    itemList.setValue(temporaryList);
                    for (int i=0; i<indianBanglaInternationals.size(); i++) {
                        if (indianBanglaInternationals.get(i).getVisibilityStatus().equalsIgnoreCase("visible")) {
                            loadPageDocument(indianBanglaInternationals.get(i).getPaperUrl());
                            Log.d(Constants.TAG,"bd sports url call:- "+i);
                        } else {
                            indianBanglaInternationalUnVisibleTemporaryList.add(indianBanglaInternationals.get(i));
                        }
                    }
                    if (indianBanglaInternationalUnVisibleList==null) {
                        indianBanglaInternationalUnVisibleList=new MutableLiveData<>();
                    }
                    indianBanglaInternationalUnVisibleList.setValue(indianBanglaInternationalUnVisibleTemporaryList);
                    insertingDataFlag=true;
                } else {
                    insertingDataFlag=true;
                    if (nameList!=null && urlList!=null && !dataStatusFlagInDb) {
                        for (int i=0; i<urlList.size(); i++) {
                            IndianBanglaInternational indianBanglaInternational=new IndianBanglaInternational();
                            indianBanglaInternational.setSerial(i);
                            indianBanglaInternational.setVisibilityStatus("visible");
                            indianBanglaInternational.setPaperUrl(urlList.get(i));
                            indianBanglaInternational.setPaperName(nameList.get(i));
                            indianBanglaInternational.setBackgroundColor("SkyBlue");
                            indianBanglaInternational.setTextColor("White");
                            Completable.fromAction(()->{
                                newsDatabase.indianBanglaInternationalDao().insertNews(indianBanglaInternational);
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
        indianBanglaInternationalLiveData=newsDatabase.indianBanglaInternationalDao().getAllNews();
        indianBanglaInternationalLiveData.observeForever(indianBanglaAllInternationalNewsObserver);
    }



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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setSamakalInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a.link-overlay[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setBanglaTribuneInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h2.title_holder a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link="https://www.banglatribune.com"+temporaryLink;
                String news = allList.get(i).text();
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setSangbadPratidinInternationalNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.sangbadpratidinInternational);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("সংবাদ প্রতিদিন (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }



    @Override
    protected void onCleared() {
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            bdInternationalLiveData.removeObserver(bangladeshiAllInternationalNewsObserver);
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            indianBanglaInternationalLiveData.removeObserver(indianBanglaAllInternationalNewsObserver);
        }
        super.onCleared();
        compositeDisposable.dispose();
    }



}
