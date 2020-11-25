package com.easysoftbd.bangladeshindiannews.ui.fragments.international;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.easysoftbd.bangladeshindiannews.data.local.NewsDatabase;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdBreaking;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdFinance;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdInternational;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdTvChannel;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaBreaking;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaFinance;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaInternational;
import com.easysoftbd.bangladeshindiannews.data.local.india.english.IndianEnglishBreaking;
import com.easysoftbd.bangladeshindiannews.data.local.india.english.IndianEnglishFinance;
import com.easysoftbd.bangladeshindiannews.data.local.india.english.IndianEnglishInternational;
import com.easysoftbd.bangladeshindiannews.data.local.india.hindi.IndianHindiBreaking;
import com.easysoftbd.bangladeshindiannews.data.local.india.hindi.IndianHindiInternational;
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

    private Observer<List<IndianHindiInternational>> indianHindiAllInternationalNewsObserver;
    private LiveData<List<IndianHindiInternational>> indianHindiInternationalLiveData;
    private MutableLiveData<List<IndianHindiInternational>> indianHindiInternationalUnVisibleList;
    private List<IndianHindiInternational> indianHindiInternationalList=new ArrayList<>();
    private List<IndianHindiInternational> indianHindiInternationalUnVisibleTemporaryList=new ArrayList<>();


    private Observer<List<IndianEnglishInternational>> indianEnglishAllInternationalNewsObserver;
    private LiveData<List<IndianEnglishInternational>> indianEnglishInternationalLiveData;
    private MutableLiveData<List<IndianEnglishInternational>> indianEnglishInternationalUnVisibleList;
    private List<IndianEnglishInternational> indianEnglishInternationalList=new ArrayList<>();
    private List<IndianEnglishInternational> indianEnglishInternationalUnVisibleTemporaryList=new ArrayList<>();



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
                        }// bangladeshi international news link are staying above.
                        else if (document.baseUri().equalsIgnoreCase(MyUrl.anandaBazarInternationalNews)){
                            setAnandaBazarInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.sangbadPratidinInternationalNews)){
                            setSangbadPratidinIndiaInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bartamanPatrikaInternationalNews)){
                            setBartamanPatrikaInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.ganaShaktiInternationalNews)){
                            setGanaShaktiInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.uttarBangaSambadInternationalNews)){
                            setUttarBangaSambadInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.ebelaInternationalNews)){
                            setEbelaInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.aajKaalInternationalNews)){
                            setAajKaalInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.khaborOnlineInternationalNews)){
                            setKhaborOnlineInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.jugaSankhaInternationalNews)){
                            setJugaSankhaInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.jagaranTripuraInternationalNews)){
                            setJagaranTripuraInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.oneIndiaInternationalNews)){
                            setOneIndiaInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.kolkata247InternationalNews)){
                            setKolkata247InternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bengal2DayInternationalNews)){
                            setBengal2DayInternationalNews(document);
                        }// Indian bangla news link are staying above.
                        else if (document.baseUri().equalsIgnoreCase(MyUrl.jagranInternationalNews)){
                            setJagranInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bhaskarInternationalNews)){
                            setBhaskarInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.amarUjalaInternationalNews)){
                            setAmarUjalaInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.liveHindustanInternationalNews)){
                            setLiveHindustanInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.navBharatTimesInternationalNews)){
                            setNavBharatTimesInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.janSattaInternationalNews)){
                            setJanSattaInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.punjabKesariInternationalNews)){
                            setPunjabKesariInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.khasKhabarInternationalNews)){
                            setKhasKhabarInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.prabhaSakshiInternationalNews)){
                            setPrabhaSakshiInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dainikTribuneOnlineInternationalNews)){
                            setDainikTribuneOnlineInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.samacharJagatInternationalNews)){
                            setSamacharJagatInternationalNews(document);
                        }// Indian hindi international news link are staying above.
                        else if (document.baseUri().equalsIgnoreCase(MyUrl.hindustanTimesInternationalNews)){
                            setHindustanTimesInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.indianExpressInternationalNews)){
                            setIndianExpressInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyPioneerInternationalNews)){
                            setDailyPioneerInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.deccanHeraldInternationalNews)){
                            setDeccanHeraldInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dnaIndiaInternationalNews)){
                            setDnaIndiaInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.deccanChronicleInternationalNews)){
                            setDeccanChronicleInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.asianAgeInternationalNews)){
                            setAsianAgeInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.economicTimesInternationalNews)){
                            setEconomicTimesInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.businessStandardInternationalNews)){
                            setBusinessStandardInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.financialExpressInternationalNews)){
                            setFinancialExpressInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.newIndianExpressInternationalNews)){
                            setNewIndianExpressInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.tribuneIndiaInternationalNews)){
                            setTribuneIndiaInternationalNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.liveMintInternationalNews)){
                            setLiveMintInternationalNews(document);
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


            IndianHindiInternational indianHindiInternationalCurrentItem = null, indianHindiInternationalUpperItem = null;
            if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                indianHindiInternationalCurrentItem = indianHindiInternationalList.get(serialNumber);
                indianHindiInternationalUpperItem = indianHindiInternationalList.get(serialNumber - 1);

                indianHindiInternationalCurrentItem.setSerial(serialNumber - 1);
                indianHindiInternationalUpperItem.setSerial(serialNumber);
            }
            IndianHindiInternational finalIndianHindiInternationalCurrentItem = indianHindiInternationalCurrentItem;
            IndianHindiInternational finalIndianHindiInternationalUpperItem = indianHindiInternationalUpperItem;


            IndianEnglishInternational indianEnglishInternationalCurrentItem = null, indianEnglishInternationalUpperItem = null;
            if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                indianEnglishInternationalCurrentItem = indianEnglishInternationalList.get(serialNumber);
                indianEnglishInternationalUpperItem = indianEnglishInternationalList.get(serialNumber - 1);

                indianEnglishInternationalCurrentItem.setSerial(serialNumber - 1);
                indianEnglishInternationalUpperItem.setSerial(serialNumber);
            }
            IndianEnglishInternational finalIndianEnglishInternationalCurrentItem = indianEnglishInternationalCurrentItem;
            IndianEnglishInternational finalIndianEnglishInternationalUpperItem = indianEnglishInternationalUpperItem;



            insertingDataFlag=true;
            dataStatusFlagInDb=true;

            Completable.fromAction(()->{
                if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                    newsDatabase.bdInternationalDao().updateNews(finalBdInternationalCurrentItem,finalBdInternationalUpperItem);
                } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                    newsDatabase.indianBanglaInternationalDao().updateNews(finalIndianBanglaInternationalCurrentItem, finalIndianBanglaInternationalUpperItem);
                } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                    newsDatabase.indianHindiInternationalDao().updateNews(finalIndianHindiInternationalCurrentItem, finalIndianHindiInternationalUpperItem);
                } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                    newsDatabase.indianEnglishInternationalDao().updateNews(finalIndianEnglishInternationalCurrentItem, finalIndianEnglishInternationalUpperItem);
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


        IndianHindiInternational indianHindiInternationalCurrentItem = null, indianHindiInternationalDownItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            if (serialNumber < (indianHindiInternationalList.size() - 1)) {
                indianHindiInternationalCurrentItem = indianHindiInternationalList.get(serialNumber);
                indianHindiInternationalDownItem = indianHindiInternationalList.get(serialNumber + 1);

                indianHindiInternationalCurrentItem.setSerial(serialNumber + 1);
                indianHindiInternationalDownItem.setSerial(serialNumber);
            }
        }
        IndianHindiInternational finalIndianHindiInternationalCurrentItem = indianHindiInternationalCurrentItem;
        IndianHindiInternational finalIndianHindiInternationalDownItem = indianHindiInternationalDownItem;


        IndianEnglishInternational indianEnglishInternationalCurrentItem = null, indianEnglishInternationalDownItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            if (serialNumber < (indianEnglishInternationalList.size() - 1)) {
                indianEnglishInternationalCurrentItem = indianEnglishInternationalList.get(serialNumber);
                indianEnglishInternationalDownItem = indianEnglishInternationalList.get(serialNumber + 1);

                indianEnglishInternationalCurrentItem.setSerial(serialNumber + 1);
                indianEnglishInternationalDownItem.setSerial(serialNumber);
            }
        }
        IndianEnglishInternational finalIndianEnglishInternationalCurrentItem = indianEnglishInternationalCurrentItem;
        IndianEnglishInternational finalIndianEnglishInternationalDownItem = indianEnglishInternationalDownItem;


        insertingDataFlag=true;
        dataStatusFlagInDb=true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdInternationalDao().updateNews(finalBdInternationalCurrentItem, finalBdInternationalDownItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaInternationalDao().updateNews(finalIndianBanglaInternationalCurrentItem, finalIndianBanglaInternationalDownItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiInternationalDao().updateNews(finalIndianHindiInternationalCurrentItem, finalIndianHindiInternationalDownItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                newsDatabase.indianEnglishInternationalDao().updateNews(finalIndianEnglishInternationalCurrentItem, finalIndianEnglishInternationalDownItem);
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


        IndianHindiInternational indianHindiInternationalCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            if (serialNumber <= (indianHindiInternationalList.size() - 1) && serialNumber >= 0) {
                indianHindiInternationalCurrentItem = indianHindiInternationalList.get(serialNumber);
                indianHindiInternationalCurrentItem.setVisibilityStatus("hidden");
            }
        }
        IndianHindiInternational finalIndianHindiInternationalCurrentItem = indianHindiInternationalCurrentItem;


        IndianEnglishInternational indianEnglishInternationalCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            if (serialNumber <= (indianEnglishInternationalList.size() - 1) && serialNumber >= 0) {
                indianEnglishInternationalCurrentItem = indianEnglishInternationalList.get(serialNumber);
                indianEnglishInternationalCurrentItem.setVisibilityStatus("hidden");
            }
        }
        IndianEnglishInternational finalIndianEnglishInternationalCurrentItem = indianEnglishInternationalCurrentItem;


        insertingDataFlag = false;
        dataStatusFlagInDb = true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdInternationalDao().updateNews(finalBdInternationalCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaInternationalDao().updateNews(finalIndianBanglaInternationalCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiInternationalDao().updateNews(finalIndianHindiInternationalCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                newsDatabase.indianEnglishInternationalDao().updateNews(finalIndianEnglishInternationalCurrentItem);
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


        IndianHindiInternational indianHindiInternationalCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            for (int i = 0; i < indianHindiInternationalUnVisibleTemporaryList.size(); i++) {
                if (paperName.equalsIgnoreCase(indianHindiInternationalUnVisibleTemporaryList.get(i).getPaperName())) {
                    indianHindiInternationalCurrentItem = indianHindiInternationalUnVisibleTemporaryList.get(i);
                    indianHindiInternationalCurrentItem.setVisibilityStatus("visible");
                }
            }
        }
        IndianHindiInternational finalIndianHindiInternationalCurrentItem = indianHindiInternationalCurrentItem;


        IndianEnglishInternational indianEnglishInternationalCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            for (int i = 0; i < indianEnglishInternationalUnVisibleTemporaryList.size(); i++) {
                if (paperName.equalsIgnoreCase(indianEnglishInternationalUnVisibleTemporaryList.get(i).getPaperName())) {
                    indianEnglishInternationalCurrentItem = indianEnglishInternationalUnVisibleTemporaryList.get(i);
                    indianEnglishInternationalCurrentItem.setVisibilityStatus("visible");
                }
            }
        }
        IndianEnglishInternational finalIndianEnglishInternationalCurrentItem = indianEnglishInternationalCurrentItem;


        insertingDataFlag = false;
        dataStatusFlagInDb = true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdInternationalDao().updateNews(finalBdInternationalCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaInternationalDao().updateNews(finalIndianBanglaInternationalCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiInternationalDao().updateNews(finalIndianHindiInternationalCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                newsDatabase.indianEnglishInternationalDao().updateNews(finalIndianEnglishInternationalCurrentItem);
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


        IndianHindiInternational indianHindiInternationalCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            indianHindiInternationalCurrentItem = indianHindiInternationalList.get(serialNumber);
            indianHindiInternationalCurrentItem.setBackgroundColor(colorName);
        }
        IndianHindiInternational finalIndianHindiInternationalCurrentItem = indianHindiInternationalCurrentItem;


        IndianEnglishInternational indianEnglishInternationalCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            indianEnglishInternationalCurrentItem = indianEnglishInternationalList.get(serialNumber);
            indianEnglishInternationalCurrentItem.setBackgroundColor(colorName);
        }
        IndianEnglishInternational finalIndianEnglishInternationalCurrentItem = indianEnglishInternationalCurrentItem;



        insertingDataFlag=false;
        dataStatusFlagInDb=true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdInternationalDao().updateNews(finalBdInternationalCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaInternationalDao().updateNews(finalIndianBanglaInternationalCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiInternationalDao().updateNews(finalIndianHindiInternationalCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                newsDatabase.indianEnglishInternationalDao().updateNews(finalIndianEnglishInternationalCurrentItem);
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


        IndianHindiInternational indianHindiInternationalCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            indianHindiInternationalCurrentItem = indianHindiInternationalList.get(serialNumber);
            indianHindiInternationalCurrentItem.setTextColor(colorName);
        }
        IndianHindiInternational finalIndianHindiInternationalCurrentItem = indianHindiInternationalCurrentItem;


        IndianEnglishInternational indianEnglishInternationalCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            indianEnglishInternationalCurrentItem = indianEnglishInternationalList.get(serialNumber);
            indianEnglishInternationalCurrentItem.setTextColor(colorName);
        }
        IndianEnglishInternational finalIndianEnglishInternationalCurrentItem = indianEnglishInternationalCurrentItem;



        insertingDataFlag=false;
        dataStatusFlagInDb=true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdInternationalDao().updateNews(finalBdInternationalCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaInternationalDao().updateNews(finalIndianBanglaInternationalCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiInternationalDao().updateNews(finalIndianHindiInternationalCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                newsDatabase.indianEnglishInternationalDao().updateNews(finalIndianEnglishInternationalCurrentItem);
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
        BdInternational bdInternationalCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            if (serialNumber <= (bdInternationalList.size() - 1) && serialNumber >= 0) {
                bdInternationalCurrentItem = bdInternationalList.get(serialNumber);
                bdInternationalCurrentItem.setNotificationStatus("on");
            }
        }
        BdInternational finalBdInternationalCurrentItem = bdInternationalCurrentItem;


        IndianBanglaInternational indianBanglaInternationalCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            if (serialNumber <= (indianBanglaInternationalList.size() - 1) && serialNumber >= 0) {
                indianBanglaInternationalCurrentItem = indianBanglaInternationalList.get(serialNumber);
                indianBanglaInternationalCurrentItem.setNotificationStatus("on");
            }
        }
        IndianBanglaInternational finalIndianBanglaInternationalCurrentItem = indianBanglaInternationalCurrentItem;


        IndianHindiInternational indianHindiInternationalCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            if (serialNumber <= (indianHindiInternationalList.size() - 1) && serialNumber >= 0) {
                indianHindiInternationalCurrentItem = indianHindiInternationalList.get(serialNumber);
                indianHindiInternationalCurrentItem.setNotificationStatus("on");
            }
        }
        IndianHindiInternational finalIndianHindiInternationalCurrentItem = indianHindiInternationalCurrentItem;


        IndianEnglishInternational indianEnglishInternationalCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            if (serialNumber <= (indianEnglishInternationalList.size() - 1) && serialNumber >= 0) {
                indianEnglishInternationalCurrentItem = indianEnglishInternationalList.get(serialNumber);
                indianEnglishInternationalCurrentItem.setNotificationStatus("on");
            }
        }
        IndianEnglishInternational finalIndianEnglishInternationalCurrentItem = indianEnglishInternationalCurrentItem;


        insertingDataFlag = false;
        dataStatusFlagInDb = true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdInternationalDao().updateNews(finalBdInternationalCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaInternationalDao().updateNews(finalIndianBanglaInternationalCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiInternationalDao().updateNews(finalIndianHindiInternationalCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                newsDatabase.indianEnglishInternationalDao().updateNews(finalIndianEnglishInternationalCurrentItem);
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
        BdInternational bdInternationalCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            if (serialNumber <= (bdInternationalList.size() - 1) && serialNumber >= 0) {
                bdInternationalCurrentItem = bdInternationalList.get(serialNumber);
                bdInternationalCurrentItem.setNotificationStatus("off");
            }
        }
        BdInternational finalBdInternationalCurrentItem = bdInternationalCurrentItem;


        IndianBanglaInternational indianBanglaInternationalCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            if (serialNumber <= (indianBanglaInternationalList.size() - 1) && serialNumber >= 0) {
                indianBanglaInternationalCurrentItem = indianBanglaInternationalList.get(serialNumber);
                indianBanglaInternationalCurrentItem.setNotificationStatus("off");
            }
        }
        IndianBanglaInternational finalIndianBanglaInternationalCurrentItem = indianBanglaInternationalCurrentItem;


        IndianHindiInternational indianHindiInternationalCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            if (serialNumber <= (indianHindiInternationalList.size() - 1) && serialNumber >= 0) {
                indianHindiInternationalCurrentItem = indianHindiInternationalList.get(serialNumber);
                indianHindiInternationalCurrentItem.setNotificationStatus("off");
            }
        }
        IndianHindiInternational finalIndianHindiInternationalCurrentItem = indianHindiInternationalCurrentItem;


        IndianEnglishInternational indianEnglishInternationalCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            if (serialNumber <= (indianEnglishInternationalList.size() - 1) && serialNumber >= 0) {
                indianEnglishInternationalCurrentItem = indianEnglishInternationalList.get(serialNumber);
                indianEnglishInternationalCurrentItem.setNotificationStatus("off");
            }
        }
        IndianEnglishInternational finalIndianEnglishInternationalCurrentItem = indianEnglishInternationalCurrentItem;


        insertingDataFlag = false;
        dataStatusFlagInDb = true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdInternationalDao().updateNews(finalBdInternationalCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaInternationalDao().updateNews(finalIndianBanglaInternationalCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiInternationalDao().updateNews(finalIndianHindiInternationalCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                newsDatabase.indianEnglishInternationalDao().updateNews(finalIndianEnglishInternationalCurrentItem);
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
                    recyclerItemModel.setNotificationStatus(bdInternationalList.get(i).getNotificationStatus());
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
                if (bdInternationals.size()>0 && !insertingDataFlag) {
                    temporaryList.clear();
                    itemList.setValue(temporaryList);
                    for (int i=0; i<bdInternationals.size(); i++) {
                        if (bdInternationals.get(i).getVisibilityStatus().equalsIgnoreCase("visible")) {
                            loadPageDocument(bdInternationals.get(i).getPaperUrl());
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
                            if (i==0) {
                                bdInternational.setNotificationStatus("on");
                            } else {
                                bdInternational.setNotificationStatus("off");
                            }
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
                    recyclerItemModel.setNotificationStatus(indianBanglaInternationalList.get(i).getNotificationStatus());
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
                            if (i==0) {
                                indianBanglaInternational.setNotificationStatus("on");
                            } else {
                                indianBanglaInternational.setNotificationStatus("off");
                            }
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


    public void shortingIndianHindiInternationalList(List<RecyclerItemModel> recyclerItemModelList) {
        if (shortedList==null) {
            shortedList=new MutableLiveData<>();
        }
        temporaryShortingList.clear();
        String title;
        RecyclerItemModel recyclerItemModel;
        for (int i=0; i<indianHindiInternationalList.size(); i++) {
            title=indianHindiInternationalList.get(i).getPaperName();
            for (int j=0; j<recyclerItemModelList.size(); j++) {
                if (title.equalsIgnoreCase(recyclerItemModelList.get(j).getTitle())) {
                    recyclerItemModel=recyclerItemModelList.get(j);
                    recyclerItemModel.setSerialNumber(indianHindiInternationalList.get(i).getSerial());
                    recyclerItemModel.setBackgroundColor(indianHindiInternationalList.get(i).getBackgroundColor());
                    recyclerItemModel.setTextColor(indianHindiInternationalList.get(i).getTextColor());
                    recyclerItemModel.setNotificationStatus(indianHindiInternationalList.get(i).getNotificationStatus());
                    temporaryShortingList.add(recyclerItemModel);
                }
            }
        }
        shortedList.setValue(temporaryShortingList);
    }
    public LiveData<List<IndianHindiInternational>> getIndianHindiInternationalUnVisibleList() {
        if (indianHindiInternationalUnVisibleList==null) {
            indianHindiInternationalUnVisibleList=new MutableLiveData<>();
        }
        return indianHindiInternationalUnVisibleList;
    }
    public void checkIndianHindiInternationalNewsDataInDb(List<String> nameList, List<String> urlList) {
        if (indianHindiAllInternationalNewsObserver==null) {
            indianHindiAllInternationalNewsObserver= indianBanglaInternationals -> {
                indianHindiInternationalList.clear();
                indianHindiInternationalList.addAll(indianBanglaInternationals);
                indianHindiInternationalUnVisibleTemporaryList.clear();
                if (dataStatusFlagInDb && itemList.getValue()!=null && itemList.getValue().size()>0) {
                    itemList.setValue(itemList.getValue());
                }
                if (indianBanglaInternationals.size()>0 && !insertingDataFlag) {
                    temporaryList.clear();
                    itemList.setValue(temporaryList);
                    for (int i=0; i<indianBanglaInternationals.size(); i++) {
                        if (indianBanglaInternationals.get(i).getVisibilityStatus().equalsIgnoreCase("visible")) {
                            loadPageDocument(indianBanglaInternationals.get(i).getPaperUrl());
                        } else {
                            indianHindiInternationalUnVisibleTemporaryList.add(indianBanglaInternationals.get(i));
                        }
                    }
                    if (indianHindiInternationalUnVisibleList==null) {
                        indianHindiInternationalUnVisibleList=new MutableLiveData<>();
                    }
                    indianHindiInternationalUnVisibleList.setValue(indianHindiInternationalUnVisibleTemporaryList);
                    insertingDataFlag=true;
                } else {
                    insertingDataFlag=true;
                    if (nameList!=null && urlList!=null && !dataStatusFlagInDb) {
                        for (int i=0; i<urlList.size(); i++) {
                            IndianHindiInternational indianHindiInternational=new IndianHindiInternational();
                            indianHindiInternational.setSerial(i);
                            indianHindiInternational.setVisibilityStatus("visible");
                            indianHindiInternational.setPaperUrl(urlList.get(i));
                            indianHindiInternational.setPaperName(nameList.get(i));
                            indianHindiInternational.setBackgroundColor("SkyBlue");
                            indianHindiInternational.setTextColor("White");
                            if (i==0) {
                                indianHindiInternational.setNotificationStatus("on");
                            } else {
                                indianHindiInternational.setNotificationStatus("off");
                            }
                            Completable.fromAction(()->{
                                newsDatabase.indianHindiInternationalDao().insertNews(indianHindiInternational);
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
        indianHindiInternationalLiveData=newsDatabase.indianHindiInternationalDao().getAllNews();
        indianHindiInternationalLiveData.observeForever(indianHindiAllInternationalNewsObserver);
    }


    public void shortingIndianEnglishInternationalList(List<RecyclerItemModel> recyclerItemModelList) {
        if (shortedList==null) {
            shortedList=new MutableLiveData<>();
        }
        temporaryShortingList.clear();
        String title;
        RecyclerItemModel recyclerItemModel;
        for (int i=0; i<indianEnglishInternationalList.size(); i++) {
            title=indianEnglishInternationalList.get(i).getPaperName();
            for (int j=0; j<recyclerItemModelList.size(); j++) {
                if (title.equalsIgnoreCase(recyclerItemModelList.get(j).getTitle())) {
                    recyclerItemModel=recyclerItemModelList.get(j);
                    recyclerItemModel.setSerialNumber(indianEnglishInternationalList.get(i).getSerial());
                    recyclerItemModel.setBackgroundColor(indianEnglishInternationalList.get(i).getBackgroundColor());
                    recyclerItemModel.setTextColor(indianEnglishInternationalList.get(i).getTextColor());
                    recyclerItemModel.setNotificationStatus(indianEnglishInternationalList.get(i).getNotificationStatus());
                    temporaryShortingList.add(recyclerItemModel);
                }
            }
        }
        shortedList.setValue(temporaryShortingList);
    }
    public LiveData<List<IndianEnglishInternational>> getIndianEnglishInternationalUnVisibleList() {
        if (indianEnglishInternationalUnVisibleList==null) {
            indianEnglishInternationalUnVisibleList=new MutableLiveData<>();
        }
        return indianEnglishInternationalUnVisibleList;
    }
    public void checkIndianEnglishInternationalNewsDataInDb(List<String> nameList, List<String> urlList) {
        if (indianEnglishAllInternationalNewsObserver==null) {
            indianEnglishAllInternationalNewsObserver= indianEnglishInternationals -> {
                indianEnglishInternationalList.clear();
                indianEnglishInternationalList.addAll(indianEnglishInternationals);
                indianEnglishInternationalUnVisibleTemporaryList.clear();
                if (dataStatusFlagInDb && itemList.getValue()!=null && itemList.getValue().size()>0) {
                    itemList.setValue(itemList.getValue());
                }
                if (indianEnglishInternationals.size()>0 && !insertingDataFlag) {
                    temporaryList.clear();
                    itemList.setValue(temporaryList);
                    for (int i=0; i<indianEnglishInternationals.size(); i++) {
                        if (indianEnglishInternationals.get(i).getVisibilityStatus().equalsIgnoreCase("visible")) {
                            loadPageDocument(indianEnglishInternationals.get(i).getPaperUrl());
                        } else {
                            indianEnglishInternationalUnVisibleTemporaryList.add(indianEnglishInternationals.get(i));
                        }
                    }
                    if (indianEnglishInternationalUnVisibleList==null) {
                        indianEnglishInternationalUnVisibleList=new MutableLiveData<>();
                    }
                    indianEnglishInternationalUnVisibleList.setValue(indianEnglishInternationalUnVisibleTemporaryList);
                    insertingDataFlag=true;
                } else {
                    insertingDataFlag=true;
                    if (nameList!=null && urlList!=null && !dataStatusFlagInDb) {
                        for (int i=0; i<urlList.size(); i++) {
                            IndianEnglishInternational indianEnglishInternational=new IndianEnglishInternational();
                            indianEnglishInternational.setSerial(i);
                            indianEnglishInternational.setVisibilityStatus("visible");
                            indianEnglishInternational.setPaperUrl(urlList.get(i));
                            indianEnglishInternational.setPaperName(nameList.get(i));
                            indianEnglishInternational.setBackgroundColor("SkyBlue");
                            indianEnglishInternational.setTextColor("White");
                            if (i==0) {
                                indianEnglishInternational.setNotificationStatus("on");
                            } else {
                                indianEnglishInternational.setNotificationStatus("off");
                            }
                            Completable.fromAction(()->{
                                newsDatabase.indianEnglishInternationalDao().insertNews(indianEnglishInternational);
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
        indianEnglishInternationalLiveData=newsDatabase.indianEnglishInternationalDao().getAllNews();
        indianEnglishInternationalLiveData.observeForever(indianEnglishAllInternationalNewsObserver);
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
            Elements allList = document.select("ul.other_news_list.mar-btp-10 li p.news_title a[href]");
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



    private void setAnandaBazarInternationalNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.anandaBazarInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("আনন্দবাজার পত্রিকা (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setSangbadPratidinIndiaInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul.other_news_list.mar-btp-10 li p.news_title a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.sangbadPratidinInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("সংবাদ প্রতিদিন (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setBartamanPatrikaInternationalNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bartamanPatrikaInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("বর্তমান (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setGanaShaktiInternationalNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.ganaShaktiInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("গণশক্তি (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setUttarBangaSambadInternationalNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.uttarBangaSambadInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("উত্তরবঙ্গ সংবাদ (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setEbelaInternationalNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.ebelaInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("এবেলা (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setAajKaalInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h6 a[href^=https://aajkaal.in/news/international/]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.aajKaalInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("আজকাল (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setKhaborOnlineInternationalNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.khaborOnlineInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("খবর অনলাইন (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setJugaSankhaInternationalNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.jugaSankhaInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("যুগশঙ্ক (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setJagaranTripuraInternationalNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.jagaranTripuraInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("জাগরণত্রিপুরা (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setOneIndiaInternationalNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.oneIndiaInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("ওয়ান ইন্ডিয়া (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setKolkata247InternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.td-ss-main-content h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.kolkata247InternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("কলকাতা ২৪*৭ (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setBengal2DayInternationalNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bengal2DayInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("বাংলা টু ডে (আন্তর্জাতিক খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }



    private void setJagranInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.jagranBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.jagranInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("जागरण (अंतरराष्ट्रीय समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setBhaskarInternationalNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bhaskarInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("देनिक भास्कर (अंतरराष्ट्रीय समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setAmarUjalaInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h5+h3 a[href^=/world/]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.amarUjalaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.amarUjalaInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("अमर उजाला (अंतरराष्ट्रीय समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setLiveHindustanInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h3 a[href^=/international/]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.liveHindustanBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.liveHindustanInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("लाइव हिन्दुस्तान (अंतरराष्ट्रीय समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setNavBharatTimesInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul li a.table_row[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.navBharatTimesInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("नव भारत टाइम्स (अंतरराष्ट्रीय समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setJanSattaInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.newslistbx span.head a[href^=https://www.jansatta.com/international/]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.janSattaInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("जनसत्ता (अंतरराष्ट्रीय समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setPunjabKesariInternationalNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.punjabKesariInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("पंजाब केसरी (अंतरराष्ट्रीय समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setKhasKhabarInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div#container ul li a+span a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.khasKhabarInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("खास खबर (अंतरराष्ट्रीय समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setPrabhaSakshiInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.position-absolute a[href^=/international/]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.prabhaSakshiBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.prabhaSakshiInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("प्रभा साक्षी (अंतरराष्ट्रीय समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setDainikTribuneOnlineInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div#top-news-grid a.card-top-align[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.dainikTribuneOnlineBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dainikTribuneOnlineInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("दैनिक ट्रिब्यून (अंतरराष्ट्रीय समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setSamacharJagatInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.cat_page h2 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.samacharJagatInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Samachar Jagat (अंतरराष्ट्रीय समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }



    private void setHindustanTimesInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div#scroll-container ul li div.media-heading.headingfour a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.hindustanTimesInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Hindustan Times (International News)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setIndianExpressInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul li h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.indianExpressInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("The Indian Express (International News)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setDailyPioneerInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.highLightedNews h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.dailyPioneerBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyPioneerInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("The Pioneer (International News)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setDeccanHeraldInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul.sm-hr-card-list li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.deccanHeraldBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.deccanHeraldInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Deccan Herald (International News)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setDnaIndiaInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h2+h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.dnaIndiaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dnaIndiaInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("DNA India (International News)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setDeccanChronicleInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.deccanChronicleBreakingNews+temporaryLink;
                String news = allList.get(i).select("h3").text();
                if (news.length()>= 15) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.deccanChronicleInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Deccan Chronicle (International News)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setAsianAgeInternationalNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.asianAgeInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("The Asian Age (International News)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setEconomicTimesInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.eachStory h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.economicTimesBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.economicTimesInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Economic Times (International News)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setBusinessStandardInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h2 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.businessStandardBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                if (news.length()>=15) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.businessStandardInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Business Standard (International News)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setFinancialExpressInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.listitembx h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.financialExpressInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Financial Express (International News)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setNewIndianExpressInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("table#example h4 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.newIndianExpressInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("The New Indian Express (International News)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setTribuneIndiaInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div#top-news-grid h4 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.tribuneIndiaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.tribuneIndiaInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("The Tribune (International News)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setLiveMintInternationalNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h2 a[href^=https://www.livemint.com/news/world/]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.liveMintInternationalNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("Mint (International News)");
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
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            indianHindiInternationalLiveData.removeObserver(indianHindiAllInternationalNewsObserver);
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            indianEnglishInternationalLiveData.removeObserver(indianEnglishAllInternationalNewsObserver);
        }
        super.onCleared();
        compositeDisposable.dispose();
    }



}
