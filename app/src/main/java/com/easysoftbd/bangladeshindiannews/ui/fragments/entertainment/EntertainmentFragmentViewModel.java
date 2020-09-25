package com.easysoftbd.bangladeshindiannews.ui.fragments.entertainment;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.easysoftbd.bangladeshindiannews.data.local.NewsDatabase;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdEntertainment;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdSports;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaEntertainment;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaSport;
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
    private String countryName, languageName;

    private Observer<List<BdEntertainment>> bangladeshiAllEntertainmentNewsObserver;
    private LiveData<List<BdEntertainment>> bdEntertainmentLiveData;
    private MutableLiveData<List<BdEntertainment>> bdEntertainmentUnVisibleList;
    private List<BdEntertainment> bdEntertainmentList=new ArrayList<>();
    private List<BdEntertainment> bdEntertainmentUnVisibleTemporaryList=new ArrayList<>();

    private Observer<List<IndianBanglaEntertainment>> indianBanglaAllEntertainmentNewsObserver;
    private LiveData<List<IndianBanglaEntertainment>> indianBanglaEntertainmentLiveData;
    private MutableLiveData<List<IndianBanglaEntertainment>> indianBanglaEntertainmentUnVisibleList;
    private List<IndianBanglaEntertainment> indianBanglaEntertainmentList=new ArrayList<>();
    private List<IndianBanglaEntertainment> indianBanglaEntertainmentUnVisibleTemporaryList=new ArrayList<>();




    public EntertainmentFragmentViewModel(NewsDatabase newsDatabase, String countryName, String languageName) {
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
                        }
                        Log.d(Constants.TAG,"entertainment:- "+document.baseUri());
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
            BdEntertainment bdEntertainmentCurrentItem = null, bdEntertainmentUpperItem = null;
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                bdEntertainmentCurrentItem = bdEntertainmentList.get(serialNumber);
                bdEntertainmentUpperItem = bdEntertainmentList.get(serialNumber - 1);

                bdEntertainmentCurrentItem.setSerial(serialNumber - 1);
                bdEntertainmentUpperItem.setSerial(serialNumber);
            }
            BdEntertainment finalBdEntertainmentCurrentItem = bdEntertainmentCurrentItem;
            BdEntertainment finalBdEntertainmentUpperItem = bdEntertainmentUpperItem;


            IndianBanglaEntertainment indianBanglaEntertainmentCurrentItem = null, indianBanglaEntertainmentUpperItem = null;
            if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                indianBanglaEntertainmentCurrentItem = indianBanglaEntertainmentList.get(serialNumber);
                indianBanglaEntertainmentUpperItem = indianBanglaEntertainmentList.get(serialNumber - 1);

                indianBanglaEntertainmentCurrentItem.setSerial(serialNumber - 1);
                indianBanglaEntertainmentUpperItem.setSerial(serialNumber);
            }
            IndianBanglaEntertainment finalIndianBanglaEntertainmentCurrentItem = indianBanglaEntertainmentCurrentItem;
            IndianBanglaEntertainment finalIndianBanglaEntertainmentUpperItem = indianBanglaEntertainmentUpperItem;



            insertingDataFlag=true;
            dataStatusFlagInDb=true;

            Completable.fromAction(()->{
                if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                    newsDatabase.bdEntertainmentDao().updateNews(finalBdEntertainmentCurrentItem,finalBdEntertainmentUpperItem);
                } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                    newsDatabase.indianBanglaEntertainmentDao().updateNews(finalIndianBanglaEntertainmentCurrentItem, finalIndianBanglaEntertainmentUpperItem);
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

        BdEntertainment bdEntertainmentCurrentItem = null, bdEntertainmentDownItem = null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            if (serialNumber < (bdEntertainmentList.size() - 1)) {
                bdEntertainmentCurrentItem = bdEntertainmentList.get(serialNumber);
                bdEntertainmentDownItem = bdEntertainmentList.get(serialNumber + 1);

                bdEntertainmentCurrentItem.setSerial(serialNumber + 1);
                bdEntertainmentDownItem.setSerial(serialNumber);
            }
        }
        BdEntertainment finalBdEntertainmentCurrentItem = bdEntertainmentCurrentItem;
        BdEntertainment finalBdEntertainmentDownItem = bdEntertainmentDownItem;


        IndianBanglaEntertainment indianBanglaEntertainmentCurrentItem = null, indianBanglaEntertainmentEntertainmentDownItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            if (serialNumber < (indianBanglaEntertainmentList.size() - 1)) {
                indianBanglaEntertainmentCurrentItem = indianBanglaEntertainmentList.get(serialNumber);
                indianBanglaEntertainmentEntertainmentDownItem = indianBanglaEntertainmentList.get(serialNumber + 1);

                indianBanglaEntertainmentCurrentItem.setSerial(serialNumber + 1);
                indianBanglaEntertainmentEntertainmentDownItem.setSerial(serialNumber);
            }
        }
        IndianBanglaEntertainment finalIndianBanglaEntertainmentCurrentItem = indianBanglaEntertainmentCurrentItem;
        IndianBanglaEntertainment finalIndianBanglaEntertainmentDownItem = indianBanglaEntertainmentEntertainmentDownItem;


            insertingDataFlag=true;
            dataStatusFlagInDb=true;

            Completable.fromAction(()->{
                if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                    newsDatabase.bdEntertainmentDao().updateNews(finalBdEntertainmentCurrentItem, finalBdEntertainmentDownItem);
                } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                    newsDatabase.indianBanglaEntertainmentDao().updateNews(finalIndianBanglaEntertainmentCurrentItem, finalIndianBanglaEntertainmentDownItem);
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
        BdEntertainment bdEntertainmentCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            if (serialNumber <= (bdEntertainmentList.size() - 1) && serialNumber >= 0) {
                bdEntertainmentCurrentItem = bdEntertainmentList.get(serialNumber);
                bdEntertainmentCurrentItem.setVisibilityStatus("hidden");
            }
        }
        BdEntertainment finalBdEntertainmentCurrentItem = bdEntertainmentCurrentItem;


        IndianBanglaEntertainment indianBanglaEntertainmentCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            if (serialNumber <= (indianBanglaEntertainmentList.size() - 1) && serialNumber >= 0) {
                indianBanglaEntertainmentCurrentItem = indianBanglaEntertainmentList.get(serialNumber);
                indianBanglaEntertainmentCurrentItem.setVisibilityStatus("hidden");
            }
        }
        IndianBanglaEntertainment finalIndianBanglaEntertainmentCurrentItem = indianBanglaEntertainmentCurrentItem;


        insertingDataFlag = false;
        dataStatusFlagInDb = true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdEntertainmentDao().updateNews(finalBdEntertainmentCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaEntertainmentDao().updateNews(finalIndianBanglaEntertainmentCurrentItem);
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
        BdEntertainment bdEntertainmentCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            for (int i = 0; i < bdEntertainmentUnVisibleTemporaryList.size(); i++) {
                if (paperName.equalsIgnoreCase(bdEntertainmentUnVisibleTemporaryList.get(i).getPaperName())) {
                    bdEntertainmentCurrentItem = bdEntertainmentUnVisibleTemporaryList.get(i);
                    bdEntertainmentCurrentItem.setVisibilityStatus("visible");
                }
            }
        }
        BdEntertainment finalBdEntertainmentCurrentItem = bdEntertainmentCurrentItem;


        IndianBanglaEntertainment indianBanglaEntertainmentCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            for (int i = 0; i < indianBanglaEntertainmentUnVisibleTemporaryList.size(); i++) {
                if (paperName.equalsIgnoreCase(indianBanglaEntertainmentUnVisibleTemporaryList.get(i).getPaperName())) {
                    indianBanglaEntertainmentCurrentItem = indianBanglaEntertainmentUnVisibleTemporaryList.get(i);
                    indianBanglaEntertainmentCurrentItem.setVisibilityStatus("visible");
                }
            }
        }
        IndianBanglaEntertainment finalIndianBanglaEntertainmentCurrentItem = indianBanglaEntertainmentCurrentItem;

        insertingDataFlag = false;
        dataStatusFlagInDb = true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdEntertainmentDao().updateNews(finalBdEntertainmentCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaEntertainmentDao().updateNews(finalIndianBanglaEntertainmentCurrentItem);
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
        BdEntertainment bdEntertainmentCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            bdEntertainmentCurrentItem = bdEntertainmentList.get(serialNumber);
            bdEntertainmentCurrentItem.setBackgroundColor(colorName);
        }
        BdEntertainment finalBdEntertainmentCurrentItem = bdEntertainmentCurrentItem;


        IndianBanglaEntertainment indianBanglaEntertainmentCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            indianBanglaEntertainmentCurrentItem = indianBanglaEntertainmentList.get(serialNumber);
            indianBanglaEntertainmentCurrentItem.setBackgroundColor(colorName);
        }
        IndianBanglaEntertainment finalIndianBanglaEntertainmentCurrentItem = indianBanglaEntertainmentCurrentItem;



        insertingDataFlag=false;
        dataStatusFlagInDb=true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdEntertainmentDao().updateNews(finalBdEntertainmentCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaEntertainmentDao().updateNews(finalIndianBanglaEntertainmentCurrentItem);
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
        BdEntertainment bdEntertainmentCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            bdEntertainmentCurrentItem = bdEntertainmentList.get(serialNumber);
            bdEntertainmentCurrentItem.setTextColor(colorName);
        }
        BdEntertainment finalBdEntertainmentCurrentItem = bdEntertainmentCurrentItem;


        IndianBanglaEntertainment indianBanglaEntertainmentCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            indianBanglaEntertainmentCurrentItem = indianBanglaEntertainmentList.get(serialNumber);
            indianBanglaEntertainmentCurrentItem.setTextColor(colorName);
        }
        IndianBanglaEntertainment finalIndianBanglaEntertainmentCurrentItem = indianBanglaEntertainmentCurrentItem;



        insertingDataFlag=false;
        dataStatusFlagInDb=true;



        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdEntertainmentDao().updateNews(finalBdEntertainmentCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaEntertainmentDao().updateNews(finalIndianBanglaEntertainmentCurrentItem);
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
    public LiveData<List<BdEntertainment>> getBdEntertainmentUnVisibleList() {
        if (bdEntertainmentUnVisibleList==null) {
            bdEntertainmentUnVisibleList=new MutableLiveData<>();
        }
        return bdEntertainmentUnVisibleList;
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
                if (bdEntertainments.size()>0 && !insertingDataFlag) {
                    temporaryList.clear();
                    itemList.setValue(temporaryList);
                    for (int i=0; i<bdEntertainments.size(); i++) {
                        if (bdEntertainments.get(i).getVisibilityStatus().equalsIgnoreCase("visible")) {
                            loadPageDocument(bdEntertainments.get(i).getPaperUrl());
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


    public void shortingIndianBanglaEntertainmentList(List<RecyclerItemModel> recyclerItemModelList) {
        if (shortedList==null) {
            shortedList=new MutableLiveData<>();
        }
        temporaryShortingList.clear();
        String title;
        RecyclerItemModel recyclerItemModel;
        for (int i=0; i<indianBanglaEntertainmentList.size(); i++) {
            title=indianBanglaEntertainmentList.get(i).getPaperName();
            for (int j=0; j<recyclerItemModelList.size(); j++) {
                if (title.equalsIgnoreCase(recyclerItemModelList.get(j).getTitle())) {
                    recyclerItemModel=recyclerItemModelList.get(j);
                    recyclerItemModel.setSerialNumber(indianBanglaEntertainmentList.get(i).getSerial());
                    recyclerItemModel.setBackgroundColor(indianBanglaEntertainmentList.get(i).getBackgroundColor());
                    recyclerItemModel.setTextColor(indianBanglaEntertainmentList.get(i).getTextColor());
                    temporaryShortingList.add(recyclerItemModel);
                }
            }
        }
        shortedList.setValue(temporaryShortingList);
    }
    public LiveData<List<IndianBanglaEntertainment>> getIndianBanglaEntertainmentUnVisibleList() {
        if (indianBanglaEntertainmentUnVisibleList==null) {
            indianBanglaEntertainmentUnVisibleList=new MutableLiveData<>();
        }
        return indianBanglaEntertainmentUnVisibleList;
    }
    public void checkIndianBanglaEntertainmentNewsDataInDb(List<String> nameList, List<String> urlList) {
        if (indianBanglaAllEntertainmentNewsObserver==null) {
            indianBanglaAllEntertainmentNewsObserver= indianBanglaEntertainments -> {
                indianBanglaEntertainmentList.clear();
                indianBanglaEntertainmentList.addAll(indianBanglaEntertainments);
                indianBanglaEntertainmentUnVisibleTemporaryList.clear();
                if (dataStatusFlagInDb && itemList.getValue()!=null && itemList.getValue().size()>0) {
                    itemList.setValue(itemList.getValue());
                }
                if (indianBanglaEntertainments.size()>0 && !insertingDataFlag) {
                    temporaryList.clear();
                    itemList.setValue(temporaryList);
                    for (int i=0; i<indianBanglaEntertainments.size(); i++) {
                        if (indianBanglaEntertainments.get(i).getVisibilityStatus().equalsIgnoreCase("visible")) {
                            loadPageDocument(indianBanglaEntertainments.get(i).getPaperUrl());
                        } else {
                            indianBanglaEntertainmentUnVisibleTemporaryList.add(indianBanglaEntertainments.get(i));
                        }
                    }
                    if (indianBanglaEntertainmentUnVisibleList==null) {
                        indianBanglaEntertainmentUnVisibleList=new MutableLiveData<>();
                    }
                    indianBanglaEntertainmentUnVisibleList.setValue(indianBanglaEntertainmentUnVisibleTemporaryList);
                    insertingDataFlag=true;
                } else {
                    insertingDataFlag=true;
                    if (nameList!=null && urlList!=null && !dataStatusFlagInDb) {
                        for (int i=0; i<urlList.size(); i++) {
                            IndianBanglaEntertainment indianBanglaEntertainment=new IndianBanglaEntertainment();
                            indianBanglaEntertainment.setSerial(i);
                            indianBanglaEntertainment.setVisibilityStatus("visible");
                            indianBanglaEntertainment.setPaperUrl(urlList.get(i));
                            indianBanglaEntertainment.setPaperName(nameList.get(i));
                            indianBanglaEntertainment.setBackgroundColor("SkyBlue");
                            indianBanglaEntertainment.setTextColor("White");
                            Completable.fromAction(()->{
                                newsDatabase.indianBanglaEntertainmentDao().insertNews(indianBanglaEntertainment);
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
        indianBanglaEntertainmentLiveData=newsDatabase.indianBanglaEntertainmentDao().getAllNews();
        indianBanglaEntertainmentLiveData.observeForever(indianBanglaAllEntertainmentNewsObserver);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setOneIndiaEntertainmentNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.oneIndiaEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("ওয়ান ইন্ডিয়া (বিনোদনের শেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }



    @Override
    protected void onCleared() {
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            bdEntertainmentLiveData.removeObserver(bangladeshiAllEntertainmentNewsObserver);
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            indianBanglaEntertainmentLiveData.removeObserver(indianBanglaAllEntertainmentNewsObserver);
        }
        super.onCleared();
        compositeDisposable.dispose();
    }

}
