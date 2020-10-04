package com.easysoftbd.bangladeshindiannews.ui.fragments.finance;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.easysoftbd.bangladeshindiannews.data.local.NewsDatabase;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdEntertainment;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdFinance;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaEntertainment;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaFinance;
import com.easysoftbd.bangladeshindiannews.data.local.india.hindi.IndianHindiFinance;
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

public class FinanceFragmentViewModel extends ViewModel {


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

    private Observer<List<BdFinance>> bangladeshiAllFinanceNewsObserver;
    private LiveData<List<BdFinance>> bdFinanceLiveData;
    private MutableLiveData<List<BdFinance>> bdFinanceUnVisibleList;
    private List<BdFinance> bdFinanceList=new ArrayList<>();
    private List<BdFinance> bdFinanceUnVisibleTemporaryList=new ArrayList<>();

    private Observer<List<IndianBanglaFinance>> indianBanglaAllFinanceNewsObserver;
    private LiveData<List<IndianBanglaFinance>> indianBanglaFinanceLiveData;
    private MutableLiveData<List<IndianBanglaFinance>> indianBanglaFinanceUnVisibleList;
    private List<IndianBanglaFinance> indianBanglaFinanceList=new ArrayList<>();
    private List<IndianBanglaFinance> indianBanglaFinanceUnVisibleTemporaryList=new ArrayList<>();

    private Observer<List<IndianHindiFinance>> indianHindiAllFinanceNewsObserver;
    private LiveData<List<IndianHindiFinance>> indianHindiFinanceLiveData;
    private MutableLiveData<List<IndianHindiFinance>> indianHindiFinanceUnVisibleList;
    private List<IndianHindiFinance> indianHindiFinanceList=new ArrayList<>();
    private List<IndianHindiFinance> indianHindiFinanceUnVisibleTemporaryList=new ArrayList<>();




    public FinanceFragmentViewModel(NewsDatabase newsDatabase, String countryName, String languageName) {
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
            BdFinance bdFinanceCurrentItem = null, bdFinanceUpperItem = null;
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                bdFinanceCurrentItem = bdFinanceList.get(serialNumber);
                bdFinanceUpperItem = bdFinanceList.get(serialNumber - 1);

                bdFinanceCurrentItem.setSerial(serialNumber - 1);
                bdFinanceUpperItem.setSerial(serialNumber);
            }
            BdFinance finalBdFinanceCurrentItem = bdFinanceCurrentItem;
            BdFinance finalBdFinanceUpperItem = bdFinanceUpperItem;


            IndianBanglaFinance indianBanglaFinanceCurrentItem = null, indianBanglaFinanceUpperItem = null;
            if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                indianBanglaFinanceCurrentItem = indianBanglaFinanceList.get(serialNumber);
                indianBanglaFinanceUpperItem = indianBanglaFinanceList.get(serialNumber - 1);

                indianBanglaFinanceCurrentItem.setSerial(serialNumber - 1);
                indianBanglaFinanceUpperItem.setSerial(serialNumber);
            }
            IndianBanglaFinance finalIndianBanglaFinanceCurrentItem = indianBanglaFinanceCurrentItem;
            IndianBanglaFinance finalIndianBanglaFinanceUpperItem = indianBanglaFinanceUpperItem;


            IndianHindiFinance indianHindiFinanceCurrentItem = null, indianHindiFinanceUpperItem = null;
            if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                indianHindiFinanceCurrentItem = indianHindiFinanceList.get(serialNumber);
                indianHindiFinanceUpperItem = indianHindiFinanceList.get(serialNumber - 1);

                indianHindiFinanceCurrentItem.setSerial(serialNumber - 1);
                indianHindiFinanceUpperItem.setSerial(serialNumber);
            }
            IndianHindiFinance finalIndianHindiFinanceCurrentItem = indianHindiFinanceCurrentItem;
            IndianHindiFinance finalIndianHindiFinanceUpperItem = indianHindiFinanceUpperItem;



            insertingDataFlag=true;
            dataStatusFlagInDb=true;

            Completable.fromAction(()->{
                if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                    newsDatabase.bdFinanceDao().updateNews(finalBdFinanceCurrentItem,finalBdFinanceUpperItem);
                } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                    newsDatabase.indianBanglaFinanceDao().updateNews(finalIndianBanglaFinanceCurrentItem, finalIndianBanglaFinanceUpperItem);
                } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                    newsDatabase.indianHindiFinanceDao().updateNews(finalIndianHindiFinanceCurrentItem, finalIndianHindiFinanceUpperItem);
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
        BdFinance bdFinanceCurrentItem = null, bdFinanceDownItem = null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            if (serialNumber < (bdFinanceList.size() - 1)) {
                bdFinanceCurrentItem = bdFinanceList.get(serialNumber);
                bdFinanceDownItem = bdFinanceList.get(serialNumber + 1);

                bdFinanceCurrentItem.setSerial(serialNumber + 1);
                bdFinanceDownItem.setSerial(serialNumber);
            }
        }
        BdFinance finalBdFinanceCurrentItem = bdFinanceCurrentItem;
        BdFinance finalBdFinanceDownItem = bdFinanceDownItem;


        IndianBanglaFinance indianBanglaFinanceCurrentItem = null, indianBanglaFinanceDownItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            if (serialNumber < (indianBanglaFinanceList.size() - 1)) {
                indianBanglaFinanceCurrentItem = indianBanglaFinanceList.get(serialNumber);
                indianBanglaFinanceDownItem = indianBanglaFinanceList.get(serialNumber + 1);

                indianBanglaFinanceCurrentItem.setSerial(serialNumber + 1);
                indianBanglaFinanceDownItem.setSerial(serialNumber);
            }
        }
        IndianBanglaFinance finalIndianBanglaFinanceCurrentItem = indianBanglaFinanceCurrentItem;
        IndianBanglaFinance finalIndianBanglaFinanceDownItem = indianBanglaFinanceDownItem;


        IndianHindiFinance indianHindiFinanceCurrentItem = null, indianHindiFinanceDownItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            if (serialNumber < (indianHindiFinanceList.size() - 1)) {
                indianHindiFinanceCurrentItem = indianHindiFinanceList.get(serialNumber);
                indianHindiFinanceDownItem = indianHindiFinanceList.get(serialNumber + 1);

                indianHindiFinanceCurrentItem.setSerial(serialNumber + 1);
                indianHindiFinanceDownItem.setSerial(serialNumber);
            }
        }
        IndianHindiFinance finalIndianHindiFinanceCurrentItem = indianHindiFinanceCurrentItem;
        IndianHindiFinance finalIndianHindiFinanceDownItem = indianHindiFinanceDownItem;


        insertingDataFlag=true;
        dataStatusFlagInDb=true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdFinanceDao().updateNews(finalBdFinanceCurrentItem, finalBdFinanceDownItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaFinanceDao().updateNews(finalIndianBanglaFinanceCurrentItem, finalIndianBanglaFinanceDownItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiFinanceDao().updateNews(finalIndianHindiFinanceCurrentItem, finalIndianHindiFinanceDownItem);
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
        BdFinance bdFinanceCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            if (serialNumber <= (bdFinanceList.size() - 1) && serialNumber >= 0) {
                bdFinanceCurrentItem = bdFinanceList.get(serialNumber);
                bdFinanceCurrentItem.setVisibilityStatus("hidden");
            }
        }
        BdFinance finalBdFinanceCurrentItem = bdFinanceCurrentItem;


        IndianBanglaFinance indianBanglaFinanceCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            if (serialNumber <= (indianBanglaFinanceList.size() - 1) && serialNumber >= 0) {
                indianBanglaFinanceCurrentItem = indianBanglaFinanceList.get(serialNumber);
                indianBanglaFinanceCurrentItem.setVisibilityStatus("hidden");
            }
        }
        IndianBanglaFinance finalIndianBanglaFinanceCurrentItem = indianBanglaFinanceCurrentItem;


        IndianHindiFinance indianHindiFinanceCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            if (serialNumber <= (indianHindiFinanceList.size() - 1) && serialNumber >= 0) {
                indianHindiFinanceCurrentItem = indianHindiFinanceList.get(serialNumber);
                indianHindiFinanceCurrentItem.setVisibilityStatus("hidden");
            }
        }
        IndianHindiFinance finalIndianHindiFinanceCurrentItem = indianHindiFinanceCurrentItem;


        insertingDataFlag = false;
        dataStatusFlagInDb = true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdFinanceDao().updateNews(finalBdFinanceCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaFinanceDao().updateNews(finalIndianBanglaFinanceCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiFinanceDao().updateNews(finalIndianHindiFinanceCurrentItem);
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
        BdFinance bdFinanceCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            for (int i = 0; i < bdFinanceUnVisibleTemporaryList.size(); i++) {
                if (paperName.equalsIgnoreCase(bdFinanceUnVisibleTemporaryList.get(i).getPaperName())) {
                    bdFinanceCurrentItem = bdFinanceUnVisibleTemporaryList.get(i);
                    bdFinanceCurrentItem.setVisibilityStatus("visible");
                }
            }
        }
        BdFinance finalBdFinanceCurrentItem = bdFinanceCurrentItem;


        IndianBanglaFinance indianBanglaFinanceCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            for (int i = 0; i < indianBanglaFinanceUnVisibleTemporaryList.size(); i++) {
                if (paperName.equalsIgnoreCase(indianBanglaFinanceUnVisibleTemporaryList.get(i).getPaperName())) {
                    indianBanglaFinanceCurrentItem = indianBanglaFinanceUnVisibleTemporaryList.get(i);
                    indianBanglaFinanceCurrentItem.setVisibilityStatus("visible");
                }
            }
        }
        IndianBanglaFinance finalIndianBanglaFinanceCurrentItem = indianBanglaFinanceCurrentItem;


        IndianHindiFinance indianHindiFinanceCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            for (int i = 0; i < indianHindiFinanceUnVisibleTemporaryList.size(); i++) {
                if (paperName.equalsIgnoreCase(indianHindiFinanceUnVisibleTemporaryList.get(i).getPaperName())) {
                    indianHindiFinanceCurrentItem = indianHindiFinanceUnVisibleTemporaryList.get(i);
                    indianHindiFinanceCurrentItem.setVisibilityStatus("visible");
                }
            }
        }
        IndianHindiFinance finalIndianHindiFinanceCurrentItem = indianHindiFinanceCurrentItem;


        insertingDataFlag = false;
        dataStatusFlagInDb = true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdFinanceDao().updateNews(finalBdFinanceCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaFinanceDao().updateNews(finalIndianBanglaFinanceCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiFinanceDao().updateNews(finalIndianHindiFinanceCurrentItem);
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
        BdFinance bdFinanceCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            bdFinanceCurrentItem = bdFinanceList.get(serialNumber);
            bdFinanceCurrentItem.setBackgroundColor(colorName);
        }
        BdFinance finalBdFinanceCurrentItem = bdFinanceCurrentItem;


        IndianBanglaFinance indianBanglaFinanceCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            indianBanglaFinanceCurrentItem = indianBanglaFinanceList.get(serialNumber);
            indianBanglaFinanceCurrentItem.setBackgroundColor(colorName);
        }
        IndianBanglaFinance finalIndianBanglaFinanceCurrentItem = indianBanglaFinanceCurrentItem;


        IndianHindiFinance indianHindiFinanceCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            indianHindiFinanceCurrentItem = indianHindiFinanceList.get(serialNumber);
            indianHindiFinanceCurrentItem.setBackgroundColor(colorName);
        }
        IndianHindiFinance finalIndianHindiFinanceCurrentItem = indianHindiFinanceCurrentItem;



        insertingDataFlag=false;
        dataStatusFlagInDb=true;



        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdFinanceDao().updateNews(finalBdFinanceCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaFinanceDao().updateNews(finalIndianBanglaFinanceCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiFinanceDao().updateNews(finalIndianHindiFinanceCurrentItem);
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
        BdFinance bdFinanceCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            bdFinanceCurrentItem = bdFinanceList.get(serialNumber);
            bdFinanceCurrentItem.setTextColor(colorName);
        }
        BdFinance finalBdFinanceCurrentItem = bdFinanceCurrentItem;


        IndianBanglaFinance indianBanglaFinanceCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            indianBanglaFinanceCurrentItem = indianBanglaFinanceList.get(serialNumber);
            indianBanglaFinanceCurrentItem.setTextColor(colorName);
        }
        IndianBanglaFinance finalIndianBanglaFinanceCurrentItem = indianBanglaFinanceCurrentItem;


        IndianHindiFinance indianHindiFinanceCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            indianHindiFinanceCurrentItem = indianHindiFinanceList.get(serialNumber);
            indianHindiFinanceCurrentItem.setTextColor(colorName);
        }
        IndianHindiFinance finalIndianHindiFinanceCurrentItem = indianHindiFinanceCurrentItem;



        insertingDataFlag=false;
        dataStatusFlagInDb=true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdFinanceDao().updateNews(finalBdFinanceCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaFinanceDao().updateNews(finalIndianBanglaFinanceCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiFinanceDao().updateNews(finalIndianHindiFinanceCurrentItem);
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



    public void shortingBdFinanceList(List<RecyclerItemModel> recyclerItemModelList) {
        if (shortedList==null) {
            shortedList=new MutableLiveData<>();
        }
        temporaryShortingList.clear();
        String title;
        RecyclerItemModel recyclerItemModel;
        for (int i=0; i<bdFinanceList.size(); i++) {
            title=bdFinanceList.get(i).getPaperName();
            for (int j=0; j<recyclerItemModelList.size(); j++) {
                if (title.equalsIgnoreCase(recyclerItemModelList.get(j).getTitle())) {
                    recyclerItemModel=recyclerItemModelList.get(j);
                    recyclerItemModel.setSerialNumber(bdFinanceList.get(i).getSerial());
                    recyclerItemModel.setBackgroundColor(bdFinanceList.get(i).getBackgroundColor());
                    recyclerItemModel.setTextColor(bdFinanceList.get(i).getTextColor());
                    temporaryShortingList.add(recyclerItemModel);
                }
            }
        }
        shortedList.setValue(temporaryShortingList);
    }
    public LiveData<List<BdFinance>> getBdFinanceUnVisibleList() {
        if (bdFinanceUnVisibleList==null) {
            bdFinanceUnVisibleList=new MutableLiveData<>();
        }
        return bdFinanceUnVisibleList;
    }
    public void checkBangladeshFinanceNewsDataInDb(List<String> nameList, List<String> urlList) {
        if (bangladeshiAllFinanceNewsObserver==null) {
            bangladeshiAllFinanceNewsObserver= bdFinances -> {
                bdFinanceList.clear();
                bdFinanceList.addAll(bdFinances);
                bdFinanceUnVisibleTemporaryList.clear();
                if (dataStatusFlagInDb && itemList.getValue()!=null && itemList.getValue().size()>0) {
                    itemList.setValue(itemList.getValue());
                }
                if (bdFinances.size()>0 && !insertingDataFlag) {
                    temporaryList.clear();
                    itemList.setValue(temporaryList);
                    for (int i=0; i<bdFinances.size(); i++) {
                        if (bdFinances.get(i).getVisibilityStatus().equalsIgnoreCase("visible")) {
                            loadPageDocument(bdFinances.get(i).getPaperUrl());
                        } else {
                            bdFinanceUnVisibleTemporaryList.add(bdFinances.get(i));
                        }
                    }
                    if (bdFinanceUnVisibleList==null) {
                        bdFinanceUnVisibleList=new MutableLiveData<>();
                    }
                    bdFinanceUnVisibleList.setValue(bdFinanceUnVisibleTemporaryList);
                    insertingDataFlag=true;
                } else {
                    insertingDataFlag=true;
                    if (nameList!=null && urlList!=null && !dataStatusFlagInDb) {
                        for (int i=0; i<urlList.size(); i++) {
                            BdFinance bdFinance=new BdFinance();
                            bdFinance.setSerial(i);
                            bdFinance.setVisibilityStatus("visible");
                            bdFinance.setPaperUrl(urlList.get(i));
                            bdFinance.setPaperName(nameList.get(i));
                            bdFinance.setBackgroundColor("SkyBlue");
                            bdFinance.setTextColor("White");
                            Completable.fromAction(()->{
                                newsDatabase.bdFinanceDao().insertNews(bdFinance);
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
        bdFinanceLiveData=newsDatabase.bdFinanceDao().getAllNews();
        bdFinanceLiveData.observeForever(bangladeshiAllFinanceNewsObserver);
    }


    public void shortingIndianBanglaFinanceList(List<RecyclerItemModel> recyclerItemModelList) {
        if (shortedList==null) {
            shortedList=new MutableLiveData<>();
        }
        temporaryShortingList.clear();
        String title;
        RecyclerItemModel recyclerItemModel;
        for (int i=0; i<indianBanglaFinanceList.size(); i++) {
            title=indianBanglaFinanceList.get(i).getPaperName();
            for (int j=0; j<recyclerItemModelList.size(); j++) {
                if (title.equalsIgnoreCase(recyclerItemModelList.get(j).getTitle())) {
                    recyclerItemModel=recyclerItemModelList.get(j);
                    recyclerItemModel.setSerialNumber(indianBanglaFinanceList.get(i).getSerial());
                    recyclerItemModel.setBackgroundColor(indianBanglaFinanceList.get(i).getBackgroundColor());
                    recyclerItemModel.setTextColor(indianBanglaFinanceList.get(i).getTextColor());
                    temporaryShortingList.add(recyclerItemModel);
                }
            }
        }
        shortedList.setValue(temporaryShortingList);
    }
    public LiveData<List<IndianBanglaFinance>> getIndianBanglaFinanceUnVisibleList() {
        if (indianBanglaFinanceUnVisibleList==null) {
            indianBanglaFinanceUnVisibleList=new MutableLiveData<>();
        }
        return indianBanglaFinanceUnVisibleList;
    }
    public void checkIndianBanglaFinanceNewsDataInDb(List<String> nameList, List<String> urlList) {
        if (indianBanglaAllFinanceNewsObserver==null) {
            indianBanglaAllFinanceNewsObserver= indianBanglaFinances -> {
                indianBanglaFinanceList.clear();
                indianBanglaFinanceList.addAll(indianBanglaFinances);
                indianBanglaFinanceUnVisibleTemporaryList.clear();
                if (dataStatusFlagInDb && itemList.getValue()!=null && itemList.getValue().size()>0) {
                    itemList.setValue(itemList.getValue());
                }
                if (indianBanglaFinances.size()>0 && !insertingDataFlag) {
                    temporaryList.clear();
                    itemList.setValue(temporaryList);
                    for (int i=0; i<indianBanglaFinances.size(); i++) {
                        if (indianBanglaFinances.get(i).getVisibilityStatus().equalsIgnoreCase("visible")) {
                            loadPageDocument(indianBanglaFinances.get(i).getPaperUrl());
                        } else {
                            indianBanglaFinanceUnVisibleTemporaryList.add(indianBanglaFinances.get(i));
                        }
                    }
                    if (indianBanglaFinanceUnVisibleList==null) {
                        indianBanglaFinanceUnVisibleList=new MutableLiveData<>();
                    }
                    indianBanglaFinanceUnVisibleList.setValue(indianBanglaFinanceUnVisibleTemporaryList);
                    insertingDataFlag=true;
                } else {
                    insertingDataFlag=true;
                    if (nameList!=null && urlList!=null && !dataStatusFlagInDb) {
                        for (int i=0; i<urlList.size(); i++) {
                            IndianBanglaFinance indianBanglaFinance=new IndianBanglaFinance();
                            indianBanglaFinance.setSerial(i);
                            indianBanglaFinance.setVisibilityStatus("visible");
                            indianBanglaFinance.setPaperUrl(urlList.get(i));
                            indianBanglaFinance.setPaperName(nameList.get(i));
                            indianBanglaFinance.setBackgroundColor("SkyBlue");
                            indianBanglaFinance.setTextColor("White");
                            Completable.fromAction(()->{
                                newsDatabase.indianBanglaFinanceDao().insertNews(indianBanglaFinance);
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
        indianBanglaFinanceLiveData=newsDatabase.indianBanglaFinanceDao().getAllNews();
        indianBanglaFinanceLiveData.observeForever(indianBanglaAllFinanceNewsObserver);
    }


    public void shortingIndianHindiFinanceList(List<RecyclerItemModel> recyclerItemModelList) {
        if (shortedList==null) {
            shortedList=new MutableLiveData<>();
        }
        temporaryShortingList.clear();
        String title;
        RecyclerItemModel recyclerItemModel;
        for (int i=0; i<indianHindiFinanceList.size(); i++) {
            title=indianHindiFinanceList.get(i).getPaperName();
            for (int j=0; j<recyclerItemModelList.size(); j++) {
                if (title.equalsIgnoreCase(recyclerItemModelList.get(j).getTitle())) {
                    recyclerItemModel=recyclerItemModelList.get(j);
                    recyclerItemModel.setSerialNumber(indianHindiFinanceList.get(i).getSerial());
                    recyclerItemModel.setBackgroundColor(indianHindiFinanceList.get(i).getBackgroundColor());
                    recyclerItemModel.setTextColor(indianHindiFinanceList.get(i).getTextColor());
                    temporaryShortingList.add(recyclerItemModel);
                }
            }
        }
        shortedList.setValue(temporaryShortingList);
    }
    public LiveData<List<IndianHindiFinance>> getIndianHindiFinanceUnVisibleList() {
        if (indianHindiFinanceUnVisibleList==null) {
            indianHindiFinanceUnVisibleList=new MutableLiveData<>();
        }
        return indianHindiFinanceUnVisibleList;
    }
    public void checkIndianHindiFinanceNewsDataInDb(List<String> nameList, List<String> urlList) {
        if (indianHindiAllFinanceNewsObserver==null) {
            indianHindiAllFinanceNewsObserver= indianHindiFinances -> {
                indianHindiFinanceList.clear();
                indianHindiFinanceList.addAll(indianHindiFinances);
                indianHindiFinanceUnVisibleTemporaryList.clear();
                if (dataStatusFlagInDb && itemList.getValue()!=null && itemList.getValue().size()>0) {
                    itemList.setValue(itemList.getValue());
                }
                if (indianHindiFinances.size()>0 && !insertingDataFlag) {
                    temporaryList.clear();
                    itemList.setValue(temporaryList);
                    for (int i=0; i<indianHindiFinances.size(); i++) {
                        if (indianHindiFinances.get(i).getVisibilityStatus().equalsIgnoreCase("visible")) {
                            loadPageDocument(indianHindiFinances.get(i).getPaperUrl());
                        } else {
                            indianHindiFinanceUnVisibleTemporaryList.add(indianHindiFinances.get(i));
                        }
                    }
                    if (indianHindiFinanceUnVisibleList==null) {
                        indianHindiFinanceUnVisibleList=new MutableLiveData<>();
                    }
                    indianHindiFinanceUnVisibleList.setValue(indianHindiFinanceUnVisibleTemporaryList);
                    insertingDataFlag=true;
                } else {
                    insertingDataFlag=true;
                    if (nameList!=null && urlList!=null && !dataStatusFlagInDb) {
                        for (int i=0; i<urlList.size(); i++) {
                            IndianHindiFinance indianHindiFinance=new IndianHindiFinance();
                            indianHindiFinance.setSerial(i);
                            indianHindiFinance.setVisibilityStatus("visible");
                            indianHindiFinance.setPaperUrl(urlList.get(i));
                            indianHindiFinance.setPaperName(nameList.get(i));
                            indianHindiFinance.setBackgroundColor("SkyBlue");
                            indianHindiFinance.setTextColor("White");
                            Completable.fromAction(()->{
                                newsDatabase.indianHindiFinanceDao().insertNews(indianHindiFinance);
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
        indianHindiFinanceLiveData=newsDatabase.indianHindiFinanceDao().getAllNews();
        indianHindiFinanceLiveData.observeForever(indianHindiAllFinanceNewsObserver);
    }



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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setBanglaTribuneFinanceNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.banglaTribuneFinance);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("বাংলা ট্রিবিউন (ব্যবসা ও অর্থনীতির সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }


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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }



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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
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

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }


    @Override
    protected void onCleared() {
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            bdFinanceLiveData.removeObserver(bangladeshiAllFinanceNewsObserver);
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            indianBanglaFinanceLiveData.removeObserver(indianBanglaAllFinanceNewsObserver);
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            indianHindiFinanceLiveData.removeObserver(indianHindiAllFinanceNewsObserver);
        }
        super.onCleared();
        compositeDisposable.dispose();
    }


}
