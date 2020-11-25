package com.easysoftbd.bangladeshindiannews.ui.fragments.entertainment;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.easysoftbd.bangladeshindiannews.data.local.NewsDatabase;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdBreaking;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdEntertainment;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdSports;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaBreaking;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaEntertainment;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaSport;
import com.easysoftbd.bangladeshindiannews.data.local.india.english.IndianEnglishBreaking;
import com.easysoftbd.bangladeshindiannews.data.local.india.english.IndianEnglishEntertainment;
import com.easysoftbd.bangladeshindiannews.data.local.india.hindi.IndianHindiBreaking;
import com.easysoftbd.bangladeshindiannews.data.local.india.hindi.IndianHindiEntertainment;
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

    private Observer<List<IndianHindiEntertainment>> indianHindiAllEntertainmentNewsObserver;
    private LiveData<List<IndianHindiEntertainment>> indianHindiEntertainmentLiveData;
    private MutableLiveData<List<IndianHindiEntertainment>> indianHindiEntertainmentUnVisibleList;
    private List<IndianHindiEntertainment> indianHindiEntertainmentList=new ArrayList<>();
    private List<IndianHindiEntertainment> indianHindiEntertainmentUnVisibleTemporaryList=new ArrayList<>();

    private Observer<List<IndianEnglishEntertainment>> indianEnglishAllEntertainmentNewsObserver;
    private LiveData<List<IndianEnglishEntertainment>> indianEnglishEntertainmentLiveData;
    private MutableLiveData<List<IndianEnglishEntertainment>> indianEnglishEntertainmentUnVisibleList;
    private List<IndianEnglishEntertainment> indianEnglishEntertainmentList=new ArrayList<>();
    private List<IndianEnglishEntertainment> indianEnglishEntertainmentUnVisibleTemporaryList=new ArrayList<>();




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
                        }// Indian Bangla entertainment news papers link are staying above.
                        else if (document.baseUri().equalsIgnoreCase(MyUrl.jagranEntertainmentNews)){
                            setJagranEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bhaskarEntertainmentNews)){
                            setBhaskarEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.amarUjalaEntertainmentNews)){
                            setAmarUjalaEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.liveHindustanEntertainmentNews)){
                            setLiveHindustanEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.navBharatTimesEntertainmentNews)){
                            setNavBharatTimesEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.janSattaEntertainmentNews)){
                            setJanSattaEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bollywoodTadkaEntertainmentNews)){
                            setBollywoodTadkaEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.hariBhoomiEntertainmentNews)){
                            setHariBhoomiEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.khasKhabarEntertainmentNews)){
                            setKhasKhabarEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.divyaHimachalEntertainmentNews)){
                            setDivyaHimachalEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.prabhaSakshiEntertainmentNews)){
                            setPrabhaSakshiEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dainikTribuneOnlineEntertainmentNews)){
                            setDainikTribuneOnlineEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.samacharJagatEntertainmentNews)){
                            setSamacharJagatEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.patrikaEntertainmentNews)){
                            setPatrikaEntertainmentNews(document);
                        }// Indian Hindi entertainment news papers link are staying above.
                        else if (document.baseUri().equalsIgnoreCase(MyUrl.hindustanTimesEntertainmentNews)){
                            setHindustanTimesEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.indianExpressEntertainmentNews)){
                            setIndianExpressEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyPioneerEntertainmentNews)){
                            setDailyPioneerEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.deccanHeraldEntertainmentNews)){
                            setDeccanHeraldEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dnaIndiaEntertainmentNews)){
                            setDnaIndiaEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.deccanChronicleEntertainmentNews)){
                            setDeccanChronicleEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.asianAgeEntertainmentNews)){
                            setTheAsianAgeEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.financialExpressEntertainmentNews)){
                            setFinancialExpressEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.newIndianExpressEntertainmentNews)){
                            setNewIndianExpressEntertainmentNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.tribuneIndiaEntertainmentNews)){
                            setTribuneIndiaEntertainmentNews(document);
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


            IndianHindiEntertainment indianHindiEntertainmentCurrentItem = null, indianHindiEntertainmentUpperItem = null;
            if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                indianHindiEntertainmentCurrentItem = indianHindiEntertainmentList.get(serialNumber);
                indianHindiEntertainmentUpperItem = indianHindiEntertainmentList.get(serialNumber - 1);

                indianHindiEntertainmentCurrentItem.setSerial(serialNumber - 1);
                indianHindiEntertainmentUpperItem.setSerial(serialNumber);
            }
            IndianHindiEntertainment finalIndianHindiEntertainmentCurrentItem = indianHindiEntertainmentCurrentItem;
            IndianHindiEntertainment finalIndianHindiEntertainmentUpperItem = indianHindiEntertainmentUpperItem;


            IndianEnglishEntertainment indianEnglishEntertainmentCurrentItem = null, indianEnglishEntertainmentUpperItem = null;
            if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                indianEnglishEntertainmentCurrentItem = indianEnglishEntertainmentList.get(serialNumber);
                indianEnglishEntertainmentUpperItem = indianEnglishEntertainmentList.get(serialNumber - 1);

                indianEnglishEntertainmentCurrentItem.setSerial(serialNumber - 1);
                indianEnglishEntertainmentUpperItem.setSerial(serialNumber);
            }
            IndianEnglishEntertainment finalIndianEnglishEntertainmentCurrentItem = indianEnglishEntertainmentCurrentItem;
            IndianEnglishEntertainment finalIndianEnglishEntertainmentUpperItem = indianEnglishEntertainmentUpperItem;



            insertingDataFlag=true;
            dataStatusFlagInDb=true;

            Completable.fromAction(()->{
                if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                    newsDatabase.bdEntertainmentDao().updateNews(finalBdEntertainmentCurrentItem,finalBdEntertainmentUpperItem);
                } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                    newsDatabase.indianBanglaEntertainmentDao().updateNews(finalIndianBanglaEntertainmentCurrentItem, finalIndianBanglaEntertainmentUpperItem);
                } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                    newsDatabase.indianHindiEntertainmentDao().updateNews(finalIndianHindiEntertainmentCurrentItem, finalIndianHindiEntertainmentUpperItem);
                } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                    newsDatabase.indianEnglishEntertainmentDao().updateNews(finalIndianEnglishEntertainmentCurrentItem, finalIndianEnglishEntertainmentUpperItem);
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


        IndianBanglaEntertainment indianBanglaEntertainmentCurrentItem = null, indianBanglaEntertainmentDownItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            if (serialNumber < (indianBanglaEntertainmentList.size() - 1)) {
                indianBanglaEntertainmentCurrentItem = indianBanglaEntertainmentList.get(serialNumber);
                indianBanglaEntertainmentDownItem = indianBanglaEntertainmentList.get(serialNumber + 1);

                indianBanglaEntertainmentCurrentItem.setSerial(serialNumber + 1);
                indianBanglaEntertainmentDownItem.setSerial(serialNumber);
            }
        }
        IndianBanglaEntertainment finalIndianBanglaEntertainmentCurrentItem = indianBanglaEntertainmentCurrentItem;
        IndianBanglaEntertainment finalIndianBanglaEntertainmentDownItem = indianBanglaEntertainmentDownItem;


        IndianHindiEntertainment indianHindiEntertainmentCurrentItem = null, indianHindiEntertainmentDownItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            if (serialNumber < (indianHindiEntertainmentList.size() - 1)) {
                indianHindiEntertainmentCurrentItem = indianHindiEntertainmentList.get(serialNumber);
                indianHindiEntertainmentDownItem = indianHindiEntertainmentList.get(serialNumber + 1);

                indianHindiEntertainmentCurrentItem.setSerial(serialNumber + 1);
                indianHindiEntertainmentDownItem.setSerial(serialNumber);
            }
        }
        IndianHindiEntertainment finalIndianHindiEntertainmentCurrentItem = indianHindiEntertainmentCurrentItem;
        IndianHindiEntertainment finalIndianHindiEntertainmentDownItem = indianHindiEntertainmentDownItem;


        IndianEnglishEntertainment indianEnglishEntertainmentCurrentItem = null, indianEnglishEntertainmentDownItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            if (serialNumber < (indianEnglishEntertainmentList.size() - 1)) {
                indianEnglishEntertainmentCurrentItem = indianEnglishEntertainmentList.get(serialNumber);
                indianEnglishEntertainmentDownItem = indianEnglishEntertainmentList.get(serialNumber + 1);

                indianEnglishEntertainmentCurrentItem.setSerial(serialNumber + 1);
                indianEnglishEntertainmentDownItem.setSerial(serialNumber);
            }
        }
        IndianEnglishEntertainment finalIndianEnglishEntertainmentCurrentItem = indianEnglishEntertainmentCurrentItem;
        IndianEnglishEntertainment finalIndianEnglishEntertainmentDownItem = indianEnglishEntertainmentDownItem;


            insertingDataFlag=true;
            dataStatusFlagInDb=true;

            Completable.fromAction(()->{
                if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                    newsDatabase.bdEntertainmentDao().updateNews(finalBdEntertainmentCurrentItem, finalBdEntertainmentDownItem);
                } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                    newsDatabase.indianBanglaEntertainmentDao().updateNews(finalIndianBanglaEntertainmentCurrentItem, finalIndianBanglaEntertainmentDownItem);
                } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                    newsDatabase.indianHindiEntertainmentDao().updateNews(finalIndianHindiEntertainmentCurrentItem, finalIndianHindiEntertainmentDownItem);
                } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                    newsDatabase.indianEnglishEntertainmentDao().updateNews(finalIndianEnglishEntertainmentCurrentItem, finalIndianEnglishEntertainmentDownItem);
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


        IndianHindiEntertainment indianHindiEntertainmentCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            if (serialNumber <= (indianHindiEntertainmentList.size() - 1) && serialNumber >= 0) {
                indianHindiEntertainmentCurrentItem = indianHindiEntertainmentList.get(serialNumber);
                indianHindiEntertainmentCurrentItem.setVisibilityStatus("hidden");
            }
        }
        IndianHindiEntertainment finalIndianHindiEntertainmentCurrentItem = indianHindiEntertainmentCurrentItem;


        IndianEnglishEntertainment indianEnglishEntertainmentCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            if (serialNumber <= (indianEnglishEntertainmentList.size() - 1) && serialNumber >= 0) {
                indianEnglishEntertainmentCurrentItem = indianEnglishEntertainmentList.get(serialNumber);
                indianEnglishEntertainmentCurrentItem.setVisibilityStatus("hidden");
            }
        }
        IndianEnglishEntertainment finalIndianEnglishEntertainmentCurrentItem = indianEnglishEntertainmentCurrentItem;


        insertingDataFlag = false;
        dataStatusFlagInDb = true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdEntertainmentDao().updateNews(finalBdEntertainmentCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaEntertainmentDao().updateNews(finalIndianBanglaEntertainmentCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiEntertainmentDao().updateNews(finalIndianHindiEntertainmentCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                newsDatabase.indianEnglishEntertainmentDao().updateNews(finalIndianEnglishEntertainmentCurrentItem);
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


        IndianHindiEntertainment indianHindiEntertainmentCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            for (int i = 0; i < indianHindiEntertainmentUnVisibleTemporaryList.size(); i++) {
                if (paperName.equalsIgnoreCase(indianHindiEntertainmentUnVisibleTemporaryList.get(i).getPaperName())) {
                    indianHindiEntertainmentCurrentItem = indianHindiEntertainmentUnVisibleTemporaryList.get(i);
                    indianHindiEntertainmentCurrentItem.setVisibilityStatus("visible");
                }
            }
        }
        IndianHindiEntertainment finalIndianHindiEntertainmentCurrentItem = indianHindiEntertainmentCurrentItem;


        IndianEnglishEntertainment indianEnglishEntertainmentCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            for (int i = 0; i < indianEnglishEntertainmentUnVisibleTemporaryList.size(); i++) {
                if (paperName.equalsIgnoreCase(indianEnglishEntertainmentUnVisibleTemporaryList.get(i).getPaperName())) {
                    indianEnglishEntertainmentCurrentItem = indianEnglishEntertainmentUnVisibleTemporaryList.get(i);
                    indianEnglishEntertainmentCurrentItem.setVisibilityStatus("visible");
                }
            }
        }
        IndianEnglishEntertainment finalIndianEnglishEntertainmentCurrentItem = indianEnglishEntertainmentCurrentItem;


        insertingDataFlag = false;
        dataStatusFlagInDb = true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdEntertainmentDao().updateNews(finalBdEntertainmentCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaEntertainmentDao().updateNews(finalIndianBanglaEntertainmentCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiEntertainmentDao().updateNews(finalIndianHindiEntertainmentCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                newsDatabase.indianEnglishEntertainmentDao().updateNews(finalIndianEnglishEntertainmentCurrentItem);
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


        IndianHindiEntertainment indianHindiEntertainmentCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            indianHindiEntertainmentCurrentItem = indianHindiEntertainmentList.get(serialNumber);
            indianHindiEntertainmentCurrentItem.setBackgroundColor(colorName);
        }
        IndianHindiEntertainment finalIndianHindiEntertainmentCurrentItem = indianHindiEntertainmentCurrentItem;


        IndianEnglishEntertainment indianEnglishEntertainmentCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            indianEnglishEntertainmentCurrentItem = indianEnglishEntertainmentList.get(serialNumber);
            indianEnglishEntertainmentCurrentItem.setBackgroundColor(colorName);
        }
        IndianEnglishEntertainment finalIndianEnglishEntertainmentCurrentItem = indianEnglishEntertainmentCurrentItem;



        insertingDataFlag=false;
        dataStatusFlagInDb=true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdEntertainmentDao().updateNews(finalBdEntertainmentCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaEntertainmentDao().updateNews(finalIndianBanglaEntertainmentCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiEntertainmentDao().updateNews(finalIndianHindiEntertainmentCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                newsDatabase.indianEnglishEntertainmentDao().updateNews(finalIndianEnglishEntertainmentCurrentItem);
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


        IndianHindiEntertainment indianHindiEntertainmentCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            indianHindiEntertainmentCurrentItem = indianHindiEntertainmentList.get(serialNumber);
            indianHindiEntertainmentCurrentItem.setTextColor(colorName);
        }
        IndianHindiEntertainment finalIndianHindiEntertainmentCurrentItem = indianHindiEntertainmentCurrentItem;


        IndianEnglishEntertainment indianEnglishEntertainmentCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            indianEnglishEntertainmentCurrentItem = indianEnglishEntertainmentList.get(serialNumber);
            indianEnglishEntertainmentCurrentItem.setTextColor(colorName);
        }
        IndianEnglishEntertainment finalIndianEnglishEntertainmentCurrentItem = indianEnglishEntertainmentCurrentItem;



        insertingDataFlag=false;
        dataStatusFlagInDb=true;



        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdEntertainmentDao().updateNews(finalBdEntertainmentCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaEntertainmentDao().updateNews(finalIndianBanglaEntertainmentCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiEntertainmentDao().updateNews(finalIndianHindiEntertainmentCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                newsDatabase.indianEnglishEntertainmentDao().updateNews(finalIndianEnglishEntertainmentCurrentItem);
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
    public void turnOnNotificationStatus(int serialNumber) {
        BdEntertainment bdEntertainmentCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            if (serialNumber <= (bdEntertainmentList.size() - 1) && serialNumber >= 0) {
                bdEntertainmentCurrentItem = bdEntertainmentList.get(serialNumber);
                bdEntertainmentCurrentItem.setNotificationStatus("on");
            }
        }
        BdEntertainment finalBdEntertainmentCurrentItem = bdEntertainmentCurrentItem;


        IndianBanglaEntertainment indianBanglaEntertainmentCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            if (serialNumber <= (indianBanglaEntertainmentList.size() - 1) && serialNumber >= 0) {
                indianBanglaEntertainmentCurrentItem = indianBanglaEntertainmentList.get(serialNumber);
                indianBanglaEntertainmentCurrentItem.setNotificationStatus("on");
            }
        }
        IndianBanglaEntertainment finalIndianBanglaEntertainmentCurrentItem = indianBanglaEntertainmentCurrentItem;


        IndianHindiEntertainment indianHindiEntertainmentCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            if (serialNumber <= (indianHindiEntertainmentList.size() - 1) && serialNumber >= 0) {
                indianHindiEntertainmentCurrentItem = indianHindiEntertainmentList.get(serialNumber);
                indianHindiEntertainmentCurrentItem.setNotificationStatus("on");
            }
        }
        IndianHindiEntertainment finalIndianHindiEntertainmentCurrentItem = indianHindiEntertainmentCurrentItem;


        IndianEnglishEntertainment indianEnglishEntertainmentCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            if (serialNumber <= (indianEnglishEntertainmentList.size() - 1) && serialNumber >= 0) {
                indianEnglishEntertainmentCurrentItem = indianEnglishEntertainmentList.get(serialNumber);
                indianEnglishEntertainmentCurrentItem.setNotificationStatus("on");
            }
        }
        IndianEnglishEntertainment finalIndianEnglishEntertainmentCurrentItem = indianEnglishEntertainmentCurrentItem;


        insertingDataFlag = false;
        dataStatusFlagInDb = true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdEntertainmentDao().updateNews(finalBdEntertainmentCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaEntertainmentDao().updateNews(finalIndianBanglaEntertainmentCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiEntertainmentDao().updateNews(finalIndianHindiEntertainmentCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                newsDatabase.indianEnglishEntertainmentDao().updateNews(finalIndianEnglishEntertainmentCurrentItem);
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
    public void turnOffNotificationStatus(int serialNumber) {
        BdEntertainment bdEntertainmentCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            if (serialNumber <= (bdEntertainmentList.size() - 1) && serialNumber >= 0) {
                bdEntertainmentCurrentItem = bdEntertainmentList.get(serialNumber);
                bdEntertainmentCurrentItem.setNotificationStatus("off");
            }
        }
        BdEntertainment finalBdEntertainmentCurrentItem = bdEntertainmentCurrentItem;


        IndianBanglaEntertainment indianBanglaEntertainmentCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            if (serialNumber <= (indianBanglaEntertainmentList.size() - 1) && serialNumber >= 0) {
                indianBanglaEntertainmentCurrentItem = indianBanglaEntertainmentList.get(serialNumber);
                indianBanglaEntertainmentCurrentItem.setNotificationStatus("off");
            }
        }
        IndianBanglaEntertainment finalIndianBanglaEntertainmentCurrentItem = indianBanglaEntertainmentCurrentItem;


        IndianHindiEntertainment indianHindiEntertainmentCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            if (serialNumber <= (indianHindiEntertainmentList.size() - 1) && serialNumber >= 0) {
                indianHindiEntertainmentCurrentItem = indianHindiEntertainmentList.get(serialNumber);
                indianHindiEntertainmentCurrentItem.setNotificationStatus("off");
            }
        }
        IndianHindiEntertainment finalIndianHindiEntertainmentCurrentItem = indianHindiEntertainmentCurrentItem;


        IndianEnglishEntertainment indianEnglishEntertainmentCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            if (serialNumber <= (indianEnglishEntertainmentList.size() - 1) && serialNumber >= 0) {
                indianEnglishEntertainmentCurrentItem = indianEnglishEntertainmentList.get(serialNumber);
                indianEnglishEntertainmentCurrentItem.setNotificationStatus("off");
            }
        }
        IndianEnglishEntertainment finalIndianEnglishEntertainmentCurrentItem = indianEnglishEntertainmentCurrentItem;


        insertingDataFlag = false;
        dataStatusFlagInDb = true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdEntertainmentDao().updateNews(finalBdEntertainmentCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaEntertainmentDao().updateNews(finalIndianBanglaEntertainmentCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiEntertainmentDao().updateNews(finalIndianHindiEntertainmentCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                newsDatabase.indianEnglishEntertainmentDao().updateNews(finalIndianEnglishEntertainmentCurrentItem);
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
                    recyclerItemModel.setNotificationStatus(bdEntertainmentList.get(i).getNotificationStatus());
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
                            if (i==0) {
                                bdEntertainment.setNotificationStatus("on");
                            } else {
                                bdEntertainment.setNotificationStatus("off");
                            }
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
                    recyclerItemModel.setNotificationStatus(indianBanglaEntertainmentList.get(i).getNotificationStatus());
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
                            if (i==0) {
                                indianBanglaEntertainment.setNotificationStatus("on");
                            } else {
                                indianBanglaEntertainment.setNotificationStatus("off");
                            }
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


    public void shortingIndianHindiEntertainmentList(List<RecyclerItemModel> recyclerItemModelList) {
        if (shortedList==null) {
            shortedList=new MutableLiveData<>();
        }
        temporaryShortingList.clear();
        String title;
        RecyclerItemModel recyclerItemModel;
        for (int i=0; i<indianHindiEntertainmentList.size(); i++) {
            title=indianHindiEntertainmentList.get(i).getPaperName();
            for (int j=0; j<recyclerItemModelList.size(); j++) {
                if (title.equalsIgnoreCase(recyclerItemModelList.get(j).getTitle())) {
                    recyclerItemModel=recyclerItemModelList.get(j);
                    recyclerItemModel.setSerialNumber(indianHindiEntertainmentList.get(i).getSerial());
                    recyclerItemModel.setBackgroundColor(indianHindiEntertainmentList.get(i).getBackgroundColor());
                    recyclerItemModel.setTextColor(indianHindiEntertainmentList.get(i).getTextColor());
                    recyclerItemModel.setNotificationStatus(indianHindiEntertainmentList.get(i).getNotificationStatus());
                    temporaryShortingList.add(recyclerItemModel);
                }
            }
        }
        shortedList.setValue(temporaryShortingList);
    }
    public LiveData<List<IndianHindiEntertainment>> getIndianHindiEntertainmentUnVisibleList() {
        if (indianHindiEntertainmentUnVisibleList==null) {
            indianHindiEntertainmentUnVisibleList=new MutableLiveData<>();
        }
        return indianHindiEntertainmentUnVisibleList;
    }
    public void checkIndianHindiEntertainmentNewsDataInDb(List<String> nameList, List<String> urlList) {
        if (indianHindiAllEntertainmentNewsObserver==null) {
            indianHindiAllEntertainmentNewsObserver= indianHindiEntertainments -> {
                indianHindiEntertainmentList.clear();
                indianHindiEntertainmentList.addAll(indianHindiEntertainments);
                indianHindiEntertainmentUnVisibleTemporaryList.clear();
                if (dataStatusFlagInDb && itemList.getValue()!=null && itemList.getValue().size()>0) {
                    itemList.setValue(itemList.getValue());
                }
                if (indianHindiEntertainments.size()>0 && !insertingDataFlag) {
                    temporaryList.clear();
                    itemList.setValue(temporaryList);
                    for (int i=0; i<indianHindiEntertainments.size(); i++) {
                        if (indianHindiEntertainments.get(i).getVisibilityStatus().equalsIgnoreCase("visible")) {
                            loadPageDocument(indianHindiEntertainments.get(i).getPaperUrl());
                        } else {
                            indianHindiEntertainmentUnVisibleTemporaryList.add(indianHindiEntertainments.get(i));
                        }
                    }
                    if (indianHindiEntertainmentUnVisibleList==null) {
                        indianHindiEntertainmentUnVisibleList=new MutableLiveData<>();
                    }
                    indianHindiEntertainmentUnVisibleList.setValue(indianHindiEntertainmentUnVisibleTemporaryList);
                    insertingDataFlag=true;
                } else {
                    insertingDataFlag=true;
                    if (nameList!=null && urlList!=null && !dataStatusFlagInDb) {
                        for (int i=0; i<urlList.size(); i++) {
                            IndianHindiEntertainment indianHindiEntertainment=new IndianHindiEntertainment();
                            indianHindiEntertainment.setSerial(i);
                            indianHindiEntertainment.setVisibilityStatus("visible");
                            indianHindiEntertainment.setPaperUrl(urlList.get(i));
                            indianHindiEntertainment.setPaperName(nameList.get(i));
                            indianHindiEntertainment.setBackgroundColor("SkyBlue");
                            indianHindiEntertainment.setTextColor("White");
                            if (i==0) {
                                indianHindiEntertainment.setNotificationStatus("on");
                            } else {
                                indianHindiEntertainment.setNotificationStatus("off");
                            }
                            Completable.fromAction(()->{
                                newsDatabase.indianHindiEntertainmentDao().insertNews(indianHindiEntertainment);
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
        indianHindiEntertainmentLiveData=newsDatabase.indianHindiEntertainmentDao().getAllNews();
        indianHindiEntertainmentLiveData.observeForever(indianHindiAllEntertainmentNewsObserver);
    }


    public void shortingIndianEnglishEntertainmentList(List<RecyclerItemModel> recyclerItemModelList) {
        if (shortedList==null) {
            shortedList=new MutableLiveData<>();
        }
        temporaryShortingList.clear();
        String title;
        RecyclerItemModel recyclerItemModel;
        for (int i=0; i<indianEnglishEntertainmentList.size(); i++) {
            title=indianEnglishEntertainmentList.get(i).getPaperName();
            for (int j=0; j<recyclerItemModelList.size(); j++) {
                if (title.equalsIgnoreCase(recyclerItemModelList.get(j).getTitle())) {
                    recyclerItemModel=recyclerItemModelList.get(j);
                    recyclerItemModel.setSerialNumber(indianEnglishEntertainmentList.get(i).getSerial());
                    recyclerItemModel.setBackgroundColor(indianEnglishEntertainmentList.get(i).getBackgroundColor());
                    recyclerItemModel.setTextColor(indianEnglishEntertainmentList.get(i).getTextColor());
                    recyclerItemModel.setNotificationStatus(indianEnglishEntertainmentList.get(i).getNotificationStatus());
                    temporaryShortingList.add(recyclerItemModel);
                }
            }
        }
        shortedList.setValue(temporaryShortingList);
    }
    public LiveData<List<IndianEnglishEntertainment>> getIndianEnglishEntertainmentUnVisibleList() {
        if (indianEnglishEntertainmentUnVisibleList==null) {
            indianEnglishEntertainmentUnVisibleList=new MutableLiveData<>();
        }
        return indianEnglishEntertainmentUnVisibleList;
    }
    public void checkIndianEnglishEntertainmentNewsDataInDb(List<String> nameList, List<String> urlList) {
        if (indianEnglishAllEntertainmentNewsObserver==null) {
            indianEnglishAllEntertainmentNewsObserver= indianEnglishEntertainments -> {
                indianEnglishEntertainmentList.clear();
                indianEnglishEntertainmentList.addAll(indianEnglishEntertainments);
                indianEnglishEntertainmentUnVisibleTemporaryList.clear();
                if (dataStatusFlagInDb && itemList.getValue()!=null && itemList.getValue().size()>0) {
                    itemList.setValue(itemList.getValue());
                }
                if (indianEnglishEntertainments.size()>0 && !insertingDataFlag) {
                    temporaryList.clear();
                    itemList.setValue(temporaryList);
                    for (int i=0; i<indianEnglishEntertainments.size(); i++) {
                        if (indianEnglishEntertainments.get(i).getVisibilityStatus().equalsIgnoreCase("visible")) {
                            loadPageDocument(indianEnglishEntertainments.get(i).getPaperUrl());
                        } else {
                            indianEnglishEntertainmentUnVisibleTemporaryList.add(indianEnglishEntertainments.get(i));
                        }
                    }
                    if (indianEnglishEntertainmentUnVisibleList==null) {
                        indianEnglishEntertainmentUnVisibleList=new MutableLiveData<>();
                    }
                    indianEnglishEntertainmentUnVisibleList.setValue(indianEnglishEntertainmentUnVisibleTemporaryList);
                    insertingDataFlag=true;
                } else {
                    insertingDataFlag=true;
                    if (nameList!=null && urlList!=null && !dataStatusFlagInDb) {
                        for (int i=0; i<urlList.size(); i++) {
                            IndianEnglishEntertainment indianEnglishEntertainment=new IndianEnglishEntertainment();
                            indianEnglishEntertainment.setSerial(i);
                            indianEnglishEntertainment.setVisibilityStatus("visible");
                            indianEnglishEntertainment.setPaperUrl(urlList.get(i));
                            indianEnglishEntertainment.setPaperName(nameList.get(i));
                            indianEnglishEntertainment.setBackgroundColor("SkyBlue");
                            indianEnglishEntertainment.setTextColor("White");
                            if (i==0) {
                                indianEnglishEntertainment.setNotificationStatus("on");
                            } else {
                                indianEnglishEntertainment.setNotificationStatus("off");
                            }
                            Completable.fromAction(()->{
                                newsDatabase.indianEnglishEntertainmentDao().insertNews(indianEnglishEntertainment);
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
        indianEnglishEntertainmentLiveData=newsDatabase.indianEnglishEntertainmentDao().getAllNews();
        indianEnglishEntertainmentLiveData.observeForever(indianEnglishAllEntertainmentNewsObserver);
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



    private void setJagranEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div#1517983345706 ul li div.h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.jagranEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("जागरण (मनोरंजन समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setBhaskarEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul li._24e83f49 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.bhaskarEntertainmentNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bhaskarEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("देनिक भास्कर (मनोरंजन समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setAmarUjalaEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("section h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.amarUjalaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.amarUjalaEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("अमर उजाला (मनोरंजन समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setLiveHindustanEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul li p a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.liveHindustanBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                if (news.length()>=10) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.liveHindustanEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("लाइव हिन्दुस्तान (मनोरंजन समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setNavBharatTimesEntertainmentNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.navBharatTimesEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("नव भारत टाइम्स (फिल्म और मनोरंजन समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setJanSattaEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul li a+h2 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.janSattaEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("जनसत्ता (मनोरंजन समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setBollywoodTadkaEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul li > div+a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bollywoodTadkaEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("बॉलीवुड तड़का (बॉलीवुड नेवस)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setHariBhoomiEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.hariBhoomiBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.hariBhoomiEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("हरिभूमि (मनोरंजन समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setKhasKhabarEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div#other-news-list ul li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.khasKhabarEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("खास खबर (मनोरंजन समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setDivyaHimachalEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.n-listing h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.divyaHimachalEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("दिव्य हिमाचल (मनोरंजन समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setPrabhaSakshiEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.prabhaSakshiBreakingNews+temporaryLink;
                String news = allList.get(i).select("h3").text();
                if (news.length()>=15) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.prabhaSakshiEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("प्रभा साक्षी (मनोरंजन समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setDainikTribuneOnlineEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div#dontmiss-slider ul li h4 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.dainikTribuneOnlineBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dainikTribuneOnlineEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("दैनिक ट्रिब्यून (मनोरंजन समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setSamacharJagatEntertainmentNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.samacharJagatEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Samachar Jagat (मनोरंजन समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setPatrikaEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.cNews-text a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.patrikaEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("पत्रिका (मनोरंजन समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }




    private void setHindustanTimesEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a[href^=https://www.hindustantimes.com/]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                if (news.length()>=21) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.hindustanTimesEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Hindustan Times (Entertainment News)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setIndianExpressEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.title a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.indianExpressEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("The Indian Express (Entertainment News)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setDailyPioneerEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul li h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.dailyPioneerBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyPioneerEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("The Pioneer (Entertainment News)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setDeccanHeraldEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul li a.card-cta[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.deccanHeraldBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.deccanHeraldEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Deccan Herald (Entertainment News)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setDnaIndiaEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.dnaIndiaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dnaIndiaEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("DNA India (Entertainment News)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setDeccanChronicleEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div#topStory a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.deccanChronicleBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.deccanChronicleEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Deccan Chronicle (Entertainment News)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setTheAsianAgeEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.asianAgeBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.asianAgeEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("The Asian Age (Entertainment News)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setFinancialExpressEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.financialExpressBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.financialExpressEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Financial Express (Entertainment News)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setNewIndianExpressEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("p a.article_click[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                if (news.length()>=11) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.newIndianExpressEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("The New Indian Express (Entertainment News)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setTribuneIndiaEntertainmentNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h4 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.tribuneIndiaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.tribuneIndiaEntertainmentNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("The Tribune (Entertainment News)");
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
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            indianHindiEntertainmentLiveData.removeObserver(indianHindiAllEntertainmentNewsObserver);
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            indianEnglishEntertainmentLiveData.removeObserver(indianEnglishAllEntertainmentNewsObserver);
        }
        super.onCleared();
        compositeDisposable.dispose();
    }




}
