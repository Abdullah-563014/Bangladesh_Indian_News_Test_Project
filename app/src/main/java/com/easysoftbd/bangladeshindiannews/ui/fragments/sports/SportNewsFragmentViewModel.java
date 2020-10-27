package com.easysoftbd.bangladeshindiannews.ui.fragments.sports;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.easysoftbd.bangladeshindiannews.data.local.NewsDatabase;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdBreaking;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdSports;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaBreaking;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaSport;
import com.easysoftbd.bangladeshindiannews.data.local.india.english.IndianEnglishSports;
import com.easysoftbd.bangladeshindiannews.data.local.india.hindi.IndianHindiSports;
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

public class SportNewsFragmentViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable;
    private NewsDatabase newsDatabase;
    private MyResponse myResponse;
    private MutableLiveData<List<RecyclerItemModel>> itemList;
    private MutableLiveData<List<RecyclerItemModel>> shortedList;
    private MutableLiveData<Integer> itemMovePosition;
    private List<RecyclerItemModel> temporaryList = new ArrayList<>();
    private List<RecyclerItemModel> temporaryShortingList = new ArrayList<>();
    private boolean insertingDataFlag = false;
    private boolean dataStatusFlagInDb = false;
    private String countryName, languageName;

    private Observer<List<BdSports>> bangladeshiAllSportsNewsObserver;
    private LiveData<List<BdSports>> bdSportsLiveData;
    private MutableLiveData<List<BdSports>> bdSportsUnVisibleList;
    private List<BdSports> bdSportsList = new ArrayList<>();
    private List<BdSports> bdSportsUnVisibleTemporaryList = new ArrayList<>();

    private Observer<List<IndianBanglaSport>> indianBanglaAllSportsNewsObserver;
    private LiveData<List<IndianBanglaSport>> indianBanglaSportsLiveData;
    private MutableLiveData<List<IndianBanglaSport>> indianBanglaSportsUnVisibleList;
    private List<IndianBanglaSport> indianBanglaSportsList = new ArrayList<>();
    private List<IndianBanglaSport> indianBanglaSportsUnVisibleTemporaryList = new ArrayList<>();

    private Observer<List<IndianHindiSports>> indianHindiAllSportsNewsObserver;
    private LiveData<List<IndianHindiSports>> indianHindiSportsLiveData;
    private MutableLiveData<List<IndianHindiSports>> indianHindiSportsUnVisibleList;
    private List<IndianHindiSports> indianHindiSportsList = new ArrayList<>();
    private List<IndianHindiSports> indianHindiSportsUnVisibleTemporaryList = new ArrayList<>();


    private Observer<List<IndianEnglishSports>> indianEnglishAllSportsNewsObserver;
    private LiveData<List<IndianEnglishSports>> indianEnglishSportsLiveData;
    private MutableLiveData<List<IndianEnglishSports>> indianEnglishSportsUnVisibleList;
    private List<IndianEnglishSports> indianEnglishSportsList = new ArrayList<>();
    private List<IndianEnglishSports> indianEnglishSportsUnVisibleTemporaryList = new ArrayList<>();


    public SportNewsFragmentViewModel(NewsDatabase newsDatabase, String countryName, String languageName) {
        this.newsDatabase = newsDatabase;
        this.countryName = countryName;
        this.languageName = languageName;
        if (myResponse == null) {
            myResponse = new MyResponse();
        }
        if (compositeDisposable == null) {
            compositeDisposable = new CompositeDisposable();
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
                        if (document.baseUri().equalsIgnoreCase(MyUrl.kalerKhanto)) {
                            setKalerkhanthoSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.samakalSports)) {
                            setSamakalSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyJanakanthaSports)) {
                            setDailyJanakanthaSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bdNews24Sports)) {
                            setBdNews24SportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.banglaTribunesSports)) {
                            setBanglaTribuneSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bhorerKagojSports)) {
                            setBhorerKagojSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyInqilabSports)) {
                            setDailyInqilabSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyNayaDigantaSports)) {
                            setDailyNayaDigantaSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.amarDesh24Sports)) {
                            setAmarDesh24SportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyIttefaqSports)) {
                            setDailyIttefaqSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyManobJominSports)) {
                            setDailyManobJominSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.sangbadPratidinSports)) {
                            setSangbadPratidinSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.manobKantha)) {
                            setManobKanthaSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bangladeshJournal)) {
                            setBdJournalSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailySangramSports)) {
                            setDailySangramSportNews(document);
                        }// bangladesh sport news papers list are staying above.
                        else if (document.baseUri().equalsIgnoreCase(MyUrl.anandaBazarSportsNews)) {
                            setAnandaBazarSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.sangbadPratidinSportsNews)) {
                            setSangbadPratidinIndianSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bartamanPatrikaSportsNews)) {
                            setBartamanPatrikaSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.ganashaktiSportsNews)) {
                            setGanaShaktiSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.uttarBangaSambadSportsNews)) {
                            setUttarBangaSambadSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.ebelaSportsNews)) {
                            setEbelaSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.asomiyaPratidinSportsNews)) {
                            setAsomiyaPratidinSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.aajKaalSportsNews)) {
                            setAajKaalSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.khaborOnlineSportsNews)) {
                            setKhaborOnlineSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.jugasankhaSportsNews)) {
                            setJugaSankhaSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.jagaranTripuraSportsNews)) {
                            setJagaranTripuraSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.oneIndiaSportsNews)) {
                            setOneIndiaSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.kolkata247SportsNews)) {
                            setKolkata247SportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.khabor24SportsNews)) {
                            setKhabor24SportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bengal2DaySportsNews)) {
                            setBengal2DaySportNews(document);
                        }// Indian bangla sports news papers list are staying above.
                        else if (document.baseUri().equalsIgnoreCase(MyUrl.jagranSportsNews)) {
                            setJagranSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bhaskarSportsNews)) {
                            setBhaskarSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.amarUjalaSportsNews)) {
                            setAmarUjalaSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.liveHindustanSportsNews)) {
                            setLiveHindustanSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.navBharatTimesSportsNews)) {
                            setNavBharatTimesSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.janSattaSportsNews)) {
                            setJanSattaSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.punjabKesariSportsNews)) {
                            setPunjabKesariSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.hariBhoomiSportsNews)) {
                            setHariBhoomiSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.khasKhabarSportsNews)) {
                            setKhasKhabarSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.divyaHimachalSportsNews)) {
                            setDivyaHimachalSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.prabhaSakshiSportsNews)) {
                            setPrabhaSakshiSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dainikTribuneOnlineSportsNews)) {
                            setDainikTribuneOnlineSportNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.samacharJagatSportsNews)) {
                            setSamacharJagatSportNews(document);
                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(Constants.TAG, "document loading failed in loadPageDocument function for " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

//====================================Primary method staying in above========================================


    public LiveData<List<RecyclerItemModel>> getShortedList() {
        if (shortedList == null) {
            shortedList = new MutableLiveData<>();
        }
        return shortedList;
    }
    public LiveData<List<RecyclerItemModel>> getItemList() {
        if (itemList == null) {
            itemList = new MutableLiveData<>();
        }
        return itemList;
    }
    public LiveData<Integer> getItemMovedPosition() {
        if (itemMovePosition == null) {
            itemMovePosition = new MutableLiveData<>();
        }
        return itemMovePosition;
    }
    public void itemMoveToUp(int serialNumber) {
        if (serialNumber > 0) {
            BdSports bdSportsCurrentItem = null, bdSportsUpperItem = null;
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                bdSportsCurrentItem = bdSportsList.get(serialNumber);
                bdSportsUpperItem = bdSportsList.get(serialNumber - 1);

                bdSportsCurrentItem.setSerial(serialNumber - 1);
                bdSportsUpperItem.setSerial(serialNumber);
            }
            BdSports finalBdSportsCurrentItem = bdSportsCurrentItem;
            BdSports finalBdSportsUpperItem = bdSportsUpperItem;


            IndianBanglaSport indianBanglaSportCurrentItem = null, indianBanglaSportUpperItem = null;
            if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                indianBanglaSportCurrentItem = indianBanglaSportsList.get(serialNumber);
                indianBanglaSportUpperItem = indianBanglaSportsList.get(serialNumber - 1);

                indianBanglaSportCurrentItem.setSerial(serialNumber - 1);
                indianBanglaSportUpperItem.setSerial(serialNumber);
            }
            IndianBanglaSport finalIndianBanglaSportCurrentItem = indianBanglaSportCurrentItem;
            IndianBanglaSport finalIndianBanglaSportUpperItem = indianBanglaSportUpperItem;


            IndianHindiSports indianHindiSportsCurrentItem = null, indianHindiSportsUpperItem = null;
            if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                indianHindiSportsCurrentItem = indianHindiSportsList.get(serialNumber);
                indianHindiSportsUpperItem = indianHindiSportsList.get(serialNumber - 1);

                indianHindiSportsCurrentItem.setSerial(serialNumber - 1);
                indianHindiSportsUpperItem.setSerial(serialNumber);
            }
            IndianHindiSports finalIndianHindiSportCurrentItem = indianHindiSportsCurrentItem;
            IndianHindiSports finalIndianHindiSportUpperItem = indianHindiSportsUpperItem;


            IndianEnglishSports indianEnglishSportsCurrentItem = null, indianEnglishSportsUpperItem = null;
            if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                indianEnglishSportsCurrentItem = indianEnglishSportsList.get(serialNumber);
                indianEnglishSportsUpperItem = indianEnglishSportsList.get(serialNumber - 1);

                indianEnglishSportsCurrentItem.setSerial(serialNumber - 1);
                indianEnglishSportsUpperItem.setSerial(serialNumber);
            }
            IndianEnglishSports finalIndianEnglishSportCurrentItem = indianEnglishSportsCurrentItem;
            IndianEnglishSports finalIndianEnglishSportUpperItem = indianEnglishSportsUpperItem;


            insertingDataFlag = true;
            dataStatusFlagInDb = true;

            Completable.fromAction(() -> {
                if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                    newsDatabase.bdSportsDao().updateNews(finalBdSportsCurrentItem, finalBdSportsUpperItem);
                } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                    newsDatabase.indianBanglaSportDao().updateNews(finalIndianBanglaSportCurrentItem, finalIndianBanglaSportUpperItem);
                } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                    newsDatabase.indianHindiSportsDao().updateNews(finalIndianHindiSportCurrentItem, finalIndianHindiSportUpperItem);
                } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                    newsDatabase.indianEnglishSportsDao().updateNews(finalIndianEnglishSportCurrentItem, finalIndianEnglishSportUpperItem);
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
        BdSports bdSportsCurrentItem = null, bdSportsDownItem = null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            if (serialNumber < (bdSportsList.size() - 1)) {
                bdSportsCurrentItem = bdSportsList.get(serialNumber);
                bdSportsDownItem = bdSportsList.get(serialNumber + 1);

                bdSportsCurrentItem.setSerial(serialNumber + 1);
                bdSportsDownItem.setSerial(serialNumber);
            }
        }
        BdSports finalBdSportCurrentItem = bdSportsCurrentItem;
        BdSports finalBdSportDownItem = bdSportsDownItem;


        IndianBanglaSport indianBanglaSportCurrentItem = null, indianBanglaSportDownItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            if (serialNumber < (indianBanglaSportsList.size() - 1)) {
                indianBanglaSportCurrentItem = indianBanglaSportsList.get(serialNumber);
                indianBanglaSportDownItem = indianBanglaSportsList.get(serialNumber + 1);

                indianBanglaSportCurrentItem.setSerial(serialNumber + 1);
                indianBanglaSportDownItem.setSerial(serialNumber);
            }
        }
        IndianBanglaSport finalIndianBanglaSportCurrentItem = indianBanglaSportCurrentItem;
        IndianBanglaSport finalIndianBanglaSportDownItem = indianBanglaSportDownItem;


        IndianHindiSports indianHindiSportsCurrentItem = null, indianHindiSportsDownItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            if (serialNumber < (indianHindiSportsList.size() - 1)) {
                indianHindiSportsCurrentItem = indianHindiSportsList.get(serialNumber);
                indianHindiSportsDownItem = indianHindiSportsList.get(serialNumber + 1);

                indianHindiSportsCurrentItem.setSerial(serialNumber + 1);
                indianHindiSportsDownItem.setSerial(serialNumber);
            }
        }
        IndianHindiSports finalIndianHindiSportCurrentItem = indianHindiSportsCurrentItem;
        IndianHindiSports finalIndianHindiSportDownItem = indianHindiSportsDownItem;


        IndianEnglishSports indianEnglishSportsCurrentItem = null, indianEnglishSportsDownItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            if (serialNumber < (indianEnglishSportsList.size() - 1)) {
                indianEnglishSportsCurrentItem = indianEnglishSportsList.get(serialNumber);
                indianEnglishSportsDownItem = indianEnglishSportsList.get(serialNumber + 1);

                indianEnglishSportsCurrentItem.setSerial(serialNumber + 1);
                indianEnglishSportsDownItem.setSerial(serialNumber);
            }
        }
        IndianEnglishSports finalIndianEnglishSportCurrentItem = indianEnglishSportsCurrentItem;
        IndianEnglishSports finalIndianEnglishSportDownItem = indianEnglishSportsDownItem;


        insertingDataFlag = true;
        dataStatusFlagInDb = true;

        Completable.fromAction(() -> {
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdSportsDao().updateNews(finalBdSportCurrentItem, finalBdSportDownItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaSportDao().updateNews(finalIndianBanglaSportCurrentItem, finalIndianBanglaSportDownItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiSportsDao().updateNews(finalIndianHindiSportCurrentItem, finalIndianHindiSportDownItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                newsDatabase.indianEnglishSportsDao().updateNews(finalIndianEnglishSportCurrentItem, finalIndianEnglishSportDownItem);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onComplete() {
                itemMovePosition.setValue(serialNumber + 2);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }
    public void hideItem(int serialNumber) {
        BdSports bdSportsCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            if (serialNumber <= (bdSportsList.size() - 1) && serialNumber >= 0) {
                bdSportsCurrentItem = bdSportsList.get(serialNumber);
                bdSportsCurrentItem.setVisibilityStatus("hidden");
            }
        }
        BdSports finalBdSportCurrentItem = bdSportsCurrentItem;


        IndianBanglaSport indianBanglaSportCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            if (serialNumber <= (indianBanglaSportsList.size() - 1) && serialNumber >= 0) {
                indianBanglaSportCurrentItem = indianBanglaSportsList.get(serialNumber);
                indianBanglaSportCurrentItem.setVisibilityStatus("hidden");
            }
        }
        IndianBanglaSport finalIndianBanglaSportCurrentItem = indianBanglaSportCurrentItem;


        IndianHindiSports indianHindiSportsCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            if (serialNumber <= (indianHindiSportsList.size() - 1) && serialNumber >= 0) {
                indianHindiSportsCurrentItem = indianHindiSportsList.get(serialNumber);
                indianHindiSportsCurrentItem.setVisibilityStatus("hidden");
            }
        }
        IndianHindiSports finalIndianHindiSportCurrentItem = indianHindiSportsCurrentItem;


        IndianEnglishSports indianEnglishSportsCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            if (serialNumber <= (indianEnglishSportsList.size() - 1) && serialNumber >= 0) {
                indianEnglishSportsCurrentItem = indianEnglishSportsList.get(serialNumber);
                indianEnglishSportsCurrentItem.setVisibilityStatus("hidden");
            }
        }
        IndianEnglishSports finalIndianEnglishSportCurrentItem = indianEnglishSportsCurrentItem;


        insertingDataFlag = false;
        dataStatusFlagInDb = true;

        Completable.fromAction(() -> {
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdSportsDao().updateNews(finalBdSportCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaSportDao().updateNews(finalIndianBanglaSportCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiSportsDao().updateNews(finalIndianHindiSportCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                newsDatabase.indianEnglishSportsDao().updateNews(finalIndianEnglishSportCurrentItem);
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
        BdSports bdSportsCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            for (int i = 0; i < bdSportsUnVisibleTemporaryList.size(); i++) {
                if (paperName.equalsIgnoreCase(bdSportsUnVisibleTemporaryList.get(i).getPaperName())) {
                    bdSportsCurrentItem = bdSportsUnVisibleTemporaryList.get(i);
                    bdSportsCurrentItem.setVisibilityStatus("visible");
                }
            }
        }
        BdSports finalBdSportCurrentItem = bdSportsCurrentItem;


        IndianBanglaSport indianBanglaSportCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            for (int i = 0; i < indianBanglaSportsUnVisibleTemporaryList.size(); i++) {
                if (paperName.equalsIgnoreCase(indianBanglaSportsUnVisibleTemporaryList.get(i).getPaperName())) {
                    indianBanglaSportCurrentItem = indianBanglaSportsUnVisibleTemporaryList.get(i);
                    indianBanglaSportCurrentItem.setVisibilityStatus("visible");
                }
            }
        }
        IndianBanglaSport finalIndianBanglaSportCurrentItem = indianBanglaSportCurrentItem;


        IndianHindiSports indianHindiSportsCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            for (int i = 0; i < indianHindiSportsUnVisibleTemporaryList.size(); i++) {
                if (paperName.equalsIgnoreCase(indianHindiSportsUnVisibleTemporaryList.get(i).getPaperName())) {
                    indianHindiSportsCurrentItem = indianHindiSportsUnVisibleTemporaryList.get(i);
                    indianHindiSportsCurrentItem.setVisibilityStatus("visible");
                }
            }
        }
        IndianHindiSports finalIndianHindiSportCurrentItem = indianHindiSportsCurrentItem;


        IndianEnglishSports indianEnglishSportsCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            for (int i = 0; i < indianEnglishSportsUnVisibleTemporaryList.size(); i++) {
                if (paperName.equalsIgnoreCase(indianEnglishSportsUnVisibleTemporaryList.get(i).getPaperName())) {
                    indianEnglishSportsCurrentItem = indianEnglishSportsUnVisibleTemporaryList.get(i);
                    indianEnglishSportsCurrentItem.setVisibilityStatus("visible");
                }
            }
        }
        IndianEnglishSports finalIndianEnglishSportCurrentItem = indianEnglishSportsCurrentItem;


        insertingDataFlag = false;
        dataStatusFlagInDb = true;

        Completable.fromAction(() -> {
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdSportsDao().updateNews(finalBdSportCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaSportDao().updateNews(finalIndianBanglaSportCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiSportsDao().updateNews(finalIndianHindiSportCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                newsDatabase.indianEnglishSportsDao().updateNews(finalIndianEnglishSportCurrentItem);
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
        BdSports bdSportsCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            bdSportsCurrentItem = bdSportsList.get(serialNumber);
            bdSportsCurrentItem.setBackgroundColor(colorName);
        }
        BdSports finalBdSportCurrentItem = bdSportsCurrentItem;


        IndianBanglaSport indianBanglaSportCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            indianBanglaSportCurrentItem = indianBanglaSportsList.get(serialNumber);
            indianBanglaSportCurrentItem.setBackgroundColor(colorName);
        }
        IndianBanglaSport finalIndianBanglaSportCurrentItem = indianBanglaSportCurrentItem;


        IndianHindiSports indianHindiSportsCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            indianHindiSportsCurrentItem = indianHindiSportsList.get(serialNumber);
            indianHindiSportsCurrentItem.setBackgroundColor(colorName);
        }
        IndianHindiSports finalIndianHindiSportCurrentItem = indianHindiSportsCurrentItem;


        IndianEnglishSports indianEnglishSportsCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            indianEnglishSportsCurrentItem = indianEnglishSportsList.get(serialNumber);
            indianEnglishSportsCurrentItem.setBackgroundColor(colorName);
        }
        IndianEnglishSports finalIndianEnglishSportCurrentItem = indianEnglishSportsCurrentItem;



        insertingDataFlag=false;
        dataStatusFlagInDb=true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdSportsDao().updateNews(finalBdSportCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaSportDao().updateNews(finalIndianBanglaSportCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiSportsDao().updateNews(finalIndianHindiSportCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                newsDatabase.indianEnglishSportsDao().updateNews(finalIndianEnglishSportCurrentItem);
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
        BdSports bdSportsCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            bdSportsCurrentItem = bdSportsList.get(serialNumber);
            bdSportsCurrentItem.setTextColor(colorName);
        }
        BdSports finalBdSportCurrentItem = bdSportsCurrentItem;


        IndianBanglaSport indianBanglaSportCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            indianBanglaSportCurrentItem = indianBanglaSportsList.get(serialNumber);
            indianBanglaSportCurrentItem.setTextColor(colorName);
        }
        IndianBanglaSport finalIndianBanglaSportCurrentItem = indianBanglaSportCurrentItem;


        IndianHindiSports indianHindiSportsCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            indianHindiSportsCurrentItem = indianHindiSportsList.get(serialNumber);
            indianHindiSportsCurrentItem.setTextColor(colorName);
        }
        IndianHindiSports finalIndianHindiSportCurrentItem = indianHindiSportsCurrentItem;


        IndianEnglishSports indianEnglishSportsCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            indianEnglishSportsCurrentItem = indianEnglishSportsList.get(serialNumber);
            indianEnglishSportsCurrentItem.setTextColor(colorName);
        }
        IndianEnglishSports finalIndianEnglishSportCurrentItem = indianEnglishSportsCurrentItem;



        insertingDataFlag=false;
        dataStatusFlagInDb=true;

        Completable.fromAction(()->{
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdSportsDao().updateNews(finalBdSportCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaSportDao().updateNews(finalIndianBanglaSportCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiSportsDao().updateNews(finalIndianHindiSportCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                newsDatabase.indianEnglishSportsDao().updateNews(finalIndianEnglishSportCurrentItem);
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


    public void shortingBdSportsList(List<RecyclerItemModel> recyclerItemModelList) {
        if (shortedList==null) {
            shortedList=new MutableLiveData<>();
        }
        temporaryShortingList.clear();
        String title;
        RecyclerItemModel recyclerItemModel;
        for (int i=0; i<bdSportsList.size(); i++) {
            title=bdSportsList.get(i).getPaperName();
            for (int j=0; j<recyclerItemModelList.size(); j++) {
                if (title.equalsIgnoreCase(recyclerItemModelList.get(j).getTitle())) {
                    recyclerItemModel=recyclerItemModelList.get(j);
                    recyclerItemModel.setSerialNumber(bdSportsList.get(i).getSerial());
                    recyclerItemModel.setBackgroundColor(bdSportsList.get(i).getBackgroundColor());
                    recyclerItemModel.setTextColor(bdSportsList.get(i).getTextColor());
                    temporaryShortingList.add(recyclerItemModel);
                }
            }
        }
        shortedList.setValue(temporaryShortingList);
    }
    public LiveData<List<BdSports>> getBdSportsUnVisibleList() {
        if (bdSportsUnVisibleList==null) {
            bdSportsUnVisibleList=new MutableLiveData<>();
        }
        return bdSportsUnVisibleList;
    }
    public void checkBangladeshSportsNewsDataInDb(List<String> nameList, List<String> urlList) {
        if (bangladeshiAllSportsNewsObserver==null) {
            bangladeshiAllSportsNewsObserver= bdSports -> {
                bdSportsList.clear();
                bdSportsList.addAll(bdSports);
                bdSportsUnVisibleTemporaryList.clear();
                if (dataStatusFlagInDb && itemList.getValue()!=null && itemList.getValue().size()>0) {
                    itemList.setValue(itemList.getValue());
                }
                if (bdSports.size()>0 && !insertingDataFlag) {
                    temporaryList.clear();
                    itemList.setValue(temporaryList);
                    for (int i=0; i<bdSports.size(); i++) {
                        if (bdSports.get(i).getVisibilityStatus().equalsIgnoreCase("visible")) {
                            loadPageDocument(bdSports.get(i).getPaperUrl());
                        } else {
                            bdSportsUnVisibleTemporaryList.add(bdSports.get(i));
                        }
                    }
                    if (bdSportsUnVisibleList==null) {
                        bdSportsUnVisibleList=new MutableLiveData<>();
                    }
                    bdSportsUnVisibleList.setValue(bdSportsUnVisibleTemporaryList);
                    insertingDataFlag=true;
                } else {
                    insertingDataFlag=true;
                    if (nameList!=null && urlList!=null && !dataStatusFlagInDb) {
                        for (int i=0; i<urlList.size(); i++) {
                            BdSports bdSport=new BdSports();
                            bdSport.setSerial(i);
                            bdSport.setVisibilityStatus("visible");
                            bdSport.setPaperUrl(urlList.get(i));
                            bdSport.setPaperName(nameList.get(i));
                            bdSport.setBackgroundColor("SkyBlue");
                            bdSport.setTextColor("White");
                            Completable.fromAction(()->{
                                newsDatabase.bdSportsDao().insertNews(bdSport);
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
        bdSportsLiveData=newsDatabase.bdSportsDao().getAllNews();
        bdSportsLiveData.observeForever(bangladeshiAllSportsNewsObserver);
    }


    public void shortingIndianBanglaSportsList(List<RecyclerItemModel> recyclerItemModelList) {
        if (shortedList==null) {
            shortedList=new MutableLiveData<>();
        }
        temporaryShortingList.clear();
        String title;
        RecyclerItemModel recyclerItemModel;
        for (int i=0; i<indianBanglaSportsList.size(); i++) {
            title=indianBanglaSportsList.get(i).getPaperName();
            for (int j=0; j<recyclerItemModelList.size(); j++) {
                if (title.equalsIgnoreCase(recyclerItemModelList.get(j).getTitle())) {
                    recyclerItemModel=recyclerItemModelList.get(j);
                    recyclerItemModel.setSerialNumber(indianBanglaSportsList.get(i).getSerial());
                    recyclerItemModel.setBackgroundColor(indianBanglaSportsList.get(i).getBackgroundColor());
                    recyclerItemModel.setTextColor(indianBanglaSportsList.get(i).getTextColor());
                    temporaryShortingList.add(recyclerItemModel);
                }
            }
        }
        shortedList.setValue(temporaryShortingList);
    }
    public LiveData<List<IndianBanglaSport>> getIndianBanglaSportsUnVisibleList() {
        if (indianBanglaSportsUnVisibleList==null) {
            indianBanglaSportsUnVisibleList=new MutableLiveData<>();
        }
        return indianBanglaSportsUnVisibleList;
    }
    public void checkIndianBanglaSportsNewsDataInDb(List<String> nameList, List<String> urlList) {
        if (indianBanglaAllSportsNewsObserver==null) {
            indianBanglaAllSportsNewsObserver= indianBanglaSports -> {
                indianBanglaSportsList.clear();
                indianBanglaSportsList.addAll(indianBanglaSports);
                indianBanglaSportsUnVisibleTemporaryList.clear();
                if (dataStatusFlagInDb && itemList.getValue()!=null && itemList.getValue().size()>0) {
                    itemList.setValue(itemList.getValue());
                }
                if (indianBanglaSports.size()>0 && !insertingDataFlag) {
                    temporaryList.clear();
                    itemList.setValue(temporaryList);
                    for (int i=0; i<indianBanglaSports.size(); i++) {
                        if (indianBanglaSports.get(i).getVisibilityStatus().equalsIgnoreCase("visible")) {
                            loadPageDocument(indianBanglaSports.get(i).getPaperUrl());
                        } else {
                            indianBanglaSportsUnVisibleTemporaryList.add(indianBanglaSports.get(i));
                        }
                    }
                    if (indianBanglaSportsUnVisibleList==null) {
                        indianBanglaSportsUnVisibleList=new MutableLiveData<>();
                    }
                    indianBanglaSportsUnVisibleList.setValue(indianBanglaSportsUnVisibleTemporaryList);
                    insertingDataFlag=true;
                } else {
                    insertingDataFlag=true;
                    if (nameList!=null && urlList!=null && !dataStatusFlagInDb) {
                        for (int i=0; i<urlList.size(); i++) {
                            IndianBanglaSport indianBanglaSport=new IndianBanglaSport();
                            indianBanglaSport.setSerial(i);
                            indianBanglaSport.setVisibilityStatus("visible");
                            indianBanglaSport.setPaperUrl(urlList.get(i));
                            indianBanglaSport.setPaperName(nameList.get(i));
                            indianBanglaSport.setBackgroundColor("SkyBlue");
                            indianBanglaSport.setTextColor("White");
                            Completable.fromAction(()->{
                                newsDatabase.indianBanglaSportDao().insertNews(indianBanglaSport);
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
        indianBanglaSportsLiveData=newsDatabase.indianBanglaSportDao().getAllNews();
        indianBanglaSportsLiveData.observeForever(indianBanglaAllSportsNewsObserver);
    }


    public void shortingIndianHindiSportsList(List<RecyclerItemModel> recyclerItemModelList) {
        if (shortedList==null) {
            shortedList=new MutableLiveData<>();
        }
        temporaryShortingList.clear();
        String title;
        RecyclerItemModel recyclerItemModel;
        for (int i=0; i<indianHindiSportsList.size(); i++) {
            title=indianHindiSportsList.get(i).getPaperName();
            for (int j=0; j<recyclerItemModelList.size(); j++) {
                if (title.equalsIgnoreCase(recyclerItemModelList.get(j).getTitle())) {
                    recyclerItemModel=recyclerItemModelList.get(j);
                    recyclerItemModel.setSerialNumber(indianHindiSportsList.get(i).getSerial());
                    recyclerItemModel.setBackgroundColor(indianHindiSportsList.get(i).getBackgroundColor());
                    recyclerItemModel.setTextColor(indianHindiSportsList.get(i).getTextColor());
                    temporaryShortingList.add(recyclerItemModel);
                }
            }
        }
        shortedList.setValue(temporaryShortingList);
    }
    public LiveData<List<IndianHindiSports>> getIndianHindiSportsUnVisibleList() {
        if (indianHindiSportsUnVisibleList==null) {
            indianHindiSportsUnVisibleList=new MutableLiveData<>();
        }
        return indianHindiSportsUnVisibleList;
    }
    public void checkIndianHindiSportsNewsDataInDb(List<String> nameList, List<String> urlList) {
        if (indianHindiAllSportsNewsObserver==null) {
            indianHindiAllSportsNewsObserver= indianHindiSports -> {
                indianHindiSportsList.clear();
                indianHindiSportsList.addAll(indianHindiSports);
                indianHindiSportsUnVisibleTemporaryList.clear();
                if (dataStatusFlagInDb && itemList.getValue()!=null && itemList.getValue().size()>0) {
                    itemList.setValue(itemList.getValue());
                }
                if (indianHindiSports.size()>0 && !insertingDataFlag) {
                    temporaryList.clear();
                    itemList.setValue(temporaryList);
                    for (int i=0; i<indianHindiSports.size(); i++) {
                        if (indianHindiSports.get(i).getVisibilityStatus().equalsIgnoreCase("visible")) {
                            loadPageDocument(indianHindiSports.get(i).getPaperUrl());
                        } else {
                            indianHindiSportsUnVisibleTemporaryList.add(indianHindiSports.get(i));
                        }
                    }
                    if (indianHindiSportsUnVisibleList==null) {
                        indianHindiSportsUnVisibleList=new MutableLiveData<>();
                    }
                    indianHindiSportsUnVisibleList.setValue(indianHindiSportsUnVisibleTemporaryList);
                    insertingDataFlag=true;
                } else {
                    insertingDataFlag=true;
                    if (nameList!=null && urlList!=null && !dataStatusFlagInDb) {
                        for (int i=0; i<urlList.size(); i++) {
                            IndianHindiSports indianHindiSport=new IndianHindiSports();
                            indianHindiSport.setSerial(i);
                            indianHindiSport.setVisibilityStatus("visible");
                            indianHindiSport.setPaperUrl(urlList.get(i));
                            indianHindiSport.setPaperName(nameList.get(i));
                            indianHindiSport.setBackgroundColor("SkyBlue");
                            indianHindiSport.setTextColor("White");
                            Completable.fromAction(()->{
                                newsDatabase.indianHindiSportsDao().insertNews(indianHindiSport);
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
        indianHindiSportsLiveData=newsDatabase.indianHindiSportsDao().getAllNews();
        indianHindiSportsLiveData.observeForever(indianHindiAllSportsNewsObserver);
    }


    public void shortingIndianEnglishSportsList(List<RecyclerItemModel> recyclerItemModelList) {
        if (shortedList==null) {
            shortedList=new MutableLiveData<>();
        }
        temporaryShortingList.clear();
        String title;
        RecyclerItemModel recyclerItemModel;
        for (int i=0; i<indianEnglishSportsList.size(); i++) {
            title=indianEnglishSportsList.get(i).getPaperName();
            for (int j=0; j<recyclerItemModelList.size(); j++) {
                if (title.equalsIgnoreCase(recyclerItemModelList.get(j).getTitle())) {
                    recyclerItemModel=recyclerItemModelList.get(j);
                    recyclerItemModel.setSerialNumber(indianEnglishSportsList.get(i).getSerial());
                    recyclerItemModel.setBackgroundColor(indianEnglishSportsList.get(i).getBackgroundColor());
                    recyclerItemModel.setTextColor(indianEnglishSportsList.get(i).getTextColor());
                    temporaryShortingList.add(recyclerItemModel);
                }
            }
        }
        shortedList.setValue(temporaryShortingList);
    }
    public LiveData<List<IndianEnglishSports>> getIndianEnglishSportsUnVisibleList() {
        if (indianEnglishSportsUnVisibleList==null) {
            indianEnglishSportsUnVisibleList=new MutableLiveData<>();
        }
        return indianEnglishSportsUnVisibleList;
    }
    public void checkIndianEnglishSportsNewsDataInDb(List<String> nameList, List<String> urlList) {
        if (indianEnglishAllSportsNewsObserver==null) {
            indianEnglishAllSportsNewsObserver= indianEnglishSport -> {
                indianEnglishSportsList.clear();
                indianEnglishSportsList.addAll(indianEnglishSport);
                indianEnglishSportsUnVisibleTemporaryList.clear();
                if (dataStatusFlagInDb && itemList.getValue()!=null && itemList.getValue().size()>0) {
                    itemList.setValue(itemList.getValue());
                }
                if (indianEnglishSport.size()>0 && !insertingDataFlag) {
                    temporaryList.clear();
                    itemList.setValue(temporaryList);
                    for (int i=0; i<indianEnglishSport.size(); i++) {
                        if (indianEnglishSport.get(i).getVisibilityStatus().equalsIgnoreCase("visible")) {
                            loadPageDocument(indianEnglishSport.get(i).getPaperUrl());
                        } else {
                            indianEnglishSportsUnVisibleTemporaryList.add(indianEnglishSport.get(i));
                        }
                    }
                    if (indianEnglishSportsUnVisibleList==null) {
                        indianEnglishSportsUnVisibleList=new MutableLiveData<>();
                    }
                    indianEnglishSportsUnVisibleList.setValue(indianEnglishSportsUnVisibleTemporaryList);
                    insertingDataFlag=true;
                } else {
                    insertingDataFlag=true;
                    if (nameList!=null && urlList!=null && !dataStatusFlagInDb) {
                        for (int i=0; i<urlList.size(); i++) {
                            IndianEnglishSports indianEnglishSports=new IndianEnglishSports();
                            indianEnglishSports.setSerial(i);
                            indianEnglishSports.setVisibilityStatus("visible");
                            indianEnglishSports.setPaperUrl(urlList.get(i));
                            indianEnglishSports.setPaperName(nameList.get(i));
                            indianEnglishSports.setBackgroundColor("SkyBlue");
                            indianEnglishSports.setTextColor("White");
                            Completable.fromAction(()->{
                                newsDatabase.indianEnglishSportsDao().insertNews(indianEnglishSports);
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
        indianEnglishSportsLiveData=newsDatabase.indianEnglishSportsDao().getAllNews();
        indianEnglishSportsLiveData.observeForever(indianEnglishAllSportsNewsObserver);
    }




    private void setDailySangramSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a#news-detail[title]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailySangramSports);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("দৈনিক সংগ্রাম (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setBdJournalSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.col-md-4.col-sm-4.khela_news_block a[href^=https://www.bd-journal.com/sports/]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bangladeshJournal);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("বাংলাদেশ জার্নাল (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setManobKanthaSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("section.special div.recent-news.margin-bottom-10 h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                if (link.startsWith("https://www.manobkantha.com.bd/cricket/") || link.startsWith("https://www.manobkantha.com.bd/football/")) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.manobKantha);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("মানবকণ্ঠ (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setSangbadPratidinSportNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.sangbadPratidinSports);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("সংবাদ প্রতিদিন (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setDailyManobJominSportNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyManobJominSports);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("মানবজমিন (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setDailyIttefaqSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a[href^=https://www.ittefaq.com.bd/sports/]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                if (news.length()>=15) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyIttefaqSports);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("দৈনিক ইত্তেফাক (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setAmarDesh24SportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h3 a.default[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.amarDesh24Sports);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("আমার দেশ 24 (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setDailyNayaDigantaSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.row.sub-lead-list div.col-md-5.column-no-left-padding a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyNayaDigantaSports);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("নয়া দিগন্ত (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setDailyInqilabSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.row.news_list div.col-xs-12.col-sm-6 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).select("h2").text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyInqilabSports);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("দৈনিক ইনকিলাব (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setBhorerKagojSportNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bhorerKagojSports);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("ভোরের কাগজ (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setBanglaTribuneSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.title_time_author_holder h2.title_holder a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.banglaTribunesSports+temporaryLink.substring(6);
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.banglaTribunesSports);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("বাংলা ট্রিবিউন (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setBdNews24SportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.text h6 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bdNews24Sports);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("বিডি নিউস ২৪ (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setDailyJanakanthaSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.list-article");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).select("a+a").attr("href");
                String link=MyUrl.dailyJonoKhanto+temporaryLink;
                String news = allList.get(i).select("a+a h2").text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyJanakanthaSports);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("দৈনিক জনকণ্ঠ (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setSamakalSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.media.news-content.child-cat-list");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).select("a").attr("href");
                String news = allList.get(i).select("h4.heading").text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.samakalSports);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("সমকাল (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setKalerkhanthoSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.col-xs-12.col-sm-6.col-md-6.n_row a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.kalerKhanto+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.kalerkhanthoSports);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("কালেরকণ্ঠ (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }


    private void setAnandaBazarSportNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.anandaBazarSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("আনন্দবাজার পত্রিকা (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setSangbadPratidinIndianSportNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.sangbadPratidinSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("সংবাদ প্রতিদিন (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setBartamanPatrikaSportNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bartamanPatrikaSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("বর্তমান (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setGanaShaktiSportNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.ganashaktiSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("গণশক্তি (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setUttarBangaSambadSportNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.uttarBangaSambadSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("উত্তরবঙ্গ সংবাদ (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setEbelaSportNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.ebelaSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("এবেলা (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setAsomiyaPratidinSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h2.title a.post-title.post-url[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.asomiyaPratidinSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("অসমীয়া প্রতিদিন (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setAajKaalSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.text-box h6 a[href^=https://aajkaal.in/news/sports/]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.aajKaalSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("আজকাল (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setKhaborOnlineSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul li.mvp-blog-story-wrap.left.relative.infinite-post a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).select("h2").text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.khaborOnlineSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("খবর অনলাইন (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setJugaSankhaSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h3.post-title a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.jugasankhaSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("যুগশঙ্ক (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setJagaranTripuraSportNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.jagaranTripuraSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("জাগরণত্রিপুরা (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setOneIndiaSportNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.oneIndiaSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("ওয়ান ইন্ডিয়া (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setKolkata247SportNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.kolkata247SportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("কলকাতা ২৪*৭ (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setKhabor24SportNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.khabor24SportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("খবর ২৪ ঘন্টা (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setBengal2DaySportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("article h2.entry-title a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bengal2DaySportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("বাংলা টু ডে (খেলার খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }


    private void setJagranSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.h3 a[href^=/cricket/]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.jagranBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.jagranSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("जागरण (खेल समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setBhaskarSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul li._24e83f49 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.bhaskarBreakingNews+temporaryLink;
                String news = allList.get(i).select("h3").text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bhaskarSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("देनिक भास्कर (खेल समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setAmarUjalaSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("section h3 a[href^=/sports/]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.amarUjalaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.amarUjalaSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("अमर उजाला (खेल समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setLiveHindustanSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul li p a[href^=/sports/]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.liveHindustanBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.liveHindustanSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("लाइव हिन्दुस्तान (खेल समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setNavBharatTimesSportNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.navBharatTimesSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("नव भारत टाइम्स (खेल समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setJanSattaSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul.links li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.janSattaSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("जनसत्ता (खेल समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setPunjabKesariSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h3.media-heading a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.punjabKesariSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("पंजाब केसरी (खेल समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setHariBhoomiSportNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.hariBhoomiSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("हरिभूमि (खेल समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setKhasKhabarSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul li > a[href^=https://www.khaskhabar.com/sports/]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).select("p").text();
                if (news.length()>=15) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.khasKhabarSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("अच्छी खबर (खेल समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setDivyaHimachalSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.newslistbx h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.divyaHimachalSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("दिव्य हिमाचल (खेल समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setPrabhaSakshiSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a[href^=/sports/]");
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.prabhaSakshiSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("प्रभा साक्षी (खेल समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setDainikTribuneOnlineSportNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a[href^=/news/sports/]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.dainikTribuneOnlineBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dainikTribuneOnlineSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("दैनिक ट्रिब्यून (खेल समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setSamacharJagatSportNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.samacharJagatSportsNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("SamacharJagat (खेल समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }




    @Override
    protected void onCleared() {
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            bdSportsLiveData.removeObserver(bangladeshiAllSportsNewsObserver);
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            indianBanglaSportsLiveData.removeObserver(indianBanglaAllSportsNewsObserver);
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            indianHindiSportsLiveData.removeObserver(indianHindiAllSportsNewsObserver);
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            indianEnglishSportsLiveData.removeObserver(indianEnglishAllSportsNewsObserver);
        }
        super.onCleared();
        compositeDisposable.dispose();
    }





}
