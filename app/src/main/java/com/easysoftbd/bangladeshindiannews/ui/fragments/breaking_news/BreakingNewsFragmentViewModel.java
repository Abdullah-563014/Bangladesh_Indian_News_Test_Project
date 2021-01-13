package com.easysoftbd.bangladeshindiannews.ui.fragments.breaking_news;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.easysoftbd.bangladeshindiannews.data.local.NewsDatabase;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdBreaking;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaBreaking;
import com.easysoftbd.bangladeshindiannews.data.local.india.english.IndianEnglishBreaking;
import com.easysoftbd.bangladeshindiannews.data.local.india.hindi.IndianHindiBreaking;
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


public class BreakingNewsFragmentViewModel extends ViewModel {

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

    private Observer<List<BdBreaking>> bangladeshiAllBreakingNewsObserver;
    private LiveData<List<BdBreaking>> bdBreakingLiveData;
    private MutableLiveData<List<BdBreaking>> bdBreakingUnVisibleList;
    private List<BdBreaking> bdBreakingList = new ArrayList<>();
    private List<BdBreaking> bdBreakingUnVisibleTemporaryList = new ArrayList<>();

    private Observer<List<IndianBanglaBreaking>> indianBanglaAllBreakingNewsObserver;
    private LiveData<List<IndianBanglaBreaking>> indianBanglaBreakingLiveData;
    private MutableLiveData<List<IndianBanglaBreaking>> indianBanglaBreakingUnVisibleList;
    private List<IndianBanglaBreaking> indianBanglaBreakingList = new ArrayList<>();
    private List<IndianBanglaBreaking> indianBanglaBreakingUnVisibleTemporaryList = new ArrayList<>();

    private Observer<List<IndianHindiBreaking>> indianHindiAllBreakingNewsObserver;
    private LiveData<List<IndianHindiBreaking>> indianHindiBreakingLiveData;
    private MutableLiveData<List<IndianHindiBreaking>> indianHindiBreakingUnVisibleList;
    private List<IndianHindiBreaking> indianHindiBreakingList = new ArrayList<>();
    private List<IndianHindiBreaking> indianHindiBreakingUnVisibleTemporaryList = new ArrayList<>();

    private Observer<List<IndianEnglishBreaking>> indianEnglishAllBreakingNewsObserver;
    private LiveData<List<IndianEnglishBreaking>> indianEnglishBreakingLiveData;
    private MutableLiveData<List<IndianEnglishBreaking>> indianEnglishBreakingUnVisibleList;
    private List<IndianEnglishBreaking> indianEnglishBreakingList = new ArrayList<>();
    private List<IndianEnglishBreaking> indianEnglishBreakingUnVisibleTemporaryList = new ArrayList<>();


    public BreakingNewsFragmentViewModel(NewsDatabase newsDatabase, String countryName, String languageName) {
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
                        if (document.baseUri().equalsIgnoreCase(MyUrl.bdProtidin)) {
                            setBdProtidinBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.prothomAlo)) {
                            setProthomAloBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.kalerKhanto)) {
                            setKalerKhantoBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.somokal)) {
                            setSomokalBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyJonoKhanto)) {
                            setDailyJanaKhantoBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bhorerKagoj)) {
                            setBhorerKagojBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyInqilab)) {
                            setDailyInqilabBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyNayaDiganta)) {
                            setDailyNayaDigantaBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.amarDesh24)) {
                            setAmarDesh24BreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyIttefaq)) {
                            setDailyIttefaqBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase("https://mzamin.com/")) {
                            setManobJominBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.songbadProtidin)) {
                            setSongbadProtidinBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.manobKantha)) {
                            setManobKanthaBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bangladeshJournal)) {
                            setBangladeshJournalBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.theDailyVorerPata)) {
                            setTheDailyVorerPataBreekingNews(document);
                        }//Bangladeshi breaking news method called above.
                        else if (document.baseUri().equalsIgnoreCase(MyUrl.anandaBazarBreakingNews)) {
                            setAnandaBazarBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.sangbadPratidinBreakingNews)) {
                            setSangbadPratidinBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bartamanPatrikaBreakingNews)) {
                            setBartamanPatrikaBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.ganashaktiBreakingNews)) {
                            setGanaShaktiBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.uttarBangaSambadBreakingNews)) {
                            setUttarBangaSambadBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.ebelaBreakingNews)) {
                            setEbelaBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.asomiyaPratidinBreakingNews)) {
                            setAsomiyaPratidinBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.aajkaalBreakingNews)) {
                            setAajKaalBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.khaborOnlinBreakingNews)) {
                            setKhaborOnlineBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.jugasankhaBreakingNews)) {
                            setJugaShankaBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.jagarantripuraBreakingNews)) {
                            setJagaranTripuraBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.ganadabiBreakingNews)) {
                            setGanadabiBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.oneIndiaBanglaBreakingNews)) {
                            setOneIndiaBreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.kolkata247BreakingNews)) {
                            setKolkata247BreekingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.khabor24BreakingNews)) {
                            setKhabor24GhontaBreekingNews(document);
                        }//Indian Bangla breaking news method called above.
                        else if (document.baseUri().equalsIgnoreCase(MyUrl.jagranBreakingNews)) {
                            setJagranBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.bhaskarBreakingNews)) {
                            setBhaskarBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.amarUjalaBreakingNews)) {
                            setAmarUjalaBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.liveHindustanBreakingNews)) {
                            setLiveHindustanBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.navBharatTimesBreakingNews)) {
                            setNavBharatTimesBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.janSattaBreakingNews)) {
                            setJanSattaBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.punjabKeSariBreakingNews)) {
                            setPunjabKeSariBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.hariBhoomiBreakingNews)) {
                            setHariBhoomiBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.khasKhabarBreakingNews)) {
                            setKhasKhabarBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.divyaHimachalBreakingNews)) {
                            setDivyaHimachalBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.prabhaSakshiBreakingNews)) {
                            setPrabhaSakshiBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.deshDootBreakingNews)) {
                            setDeshDootBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dainikTribuneOnlineBreakingNews)) {
                            setDainikTribuneOnlineBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.samacharJagatBreakingNews)) {
                            setSamacharJagatBreakingNews(document);
                        }//Indian english breaking news method called above.
                        else if (document.baseUri().equalsIgnoreCase(MyUrl.hindustanTimesBreakingNews)) {
                            setHindustanTimesBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.indianExpressEnglishBreakingNews)) {
                            setIndianExpressBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyPioneerBreakingNews)) {
                            setDailyPioneerBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.deccanHeraldBreakingNews)) {
                            setDeccanHeraldBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.dnaIndiaHeadlinesBreakingNews)) {
                            setDnaIndiaBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.deccanChronicleBreakingNews)) {
                            setDeccanChronicleBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.asianAgeBreakingNews)) {
                            setAsianAgeBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.economicTimesBreakingNews)) {
                            setEconomicTimesBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.businessStandardBreakingNews)) {
                            setBusinessStandardBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.financialExpressBreakingNews)) {
                            setFinancialExpressBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.newIndianExpressBreakingNews)) {
                            setNewIndianExpressBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.tribuneIndiaBreakingNews)) {
                            setTribuneIndiaBreakingNews(document);
                        } else if (document.baseUri().equalsIgnoreCase(MyUrl.liveMintBreakingNews)) {
                            setLiveMintBreakingNews(document);
                        }
//                        Log.d(Constants.TAG,"base url:- "+document.baseUri());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

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
            BdBreaking bdBreakingCurrentItem = null, bdBreakingUpperItem = null;
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                bdBreakingCurrentItem = bdBreakingList.get(serialNumber);
                bdBreakingUpperItem = bdBreakingList.get(serialNumber - 1);

                bdBreakingCurrentItem.setSerial(serialNumber - 1);
                bdBreakingUpperItem.setSerial(serialNumber);
            }
            BdBreaking finalBdBreakingCurrentItem = bdBreakingCurrentItem;
            BdBreaking finalBdBreakingUpperItem = bdBreakingUpperItem;


            IndianBanglaBreaking indianBanglaBreakingCurrentItem = null, indianBanglaBreakingUpperItem = null;
            if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                indianBanglaBreakingCurrentItem = indianBanglaBreakingList.get(serialNumber);
                indianBanglaBreakingUpperItem = indianBanglaBreakingList.get(serialNumber - 1);

                indianBanglaBreakingCurrentItem.setSerial(serialNumber - 1);
                indianBanglaBreakingUpperItem.setSerial(serialNumber);
            }
            IndianBanglaBreaking finalIndianBanglaBreakingCurrentItem = indianBanglaBreakingCurrentItem;
            IndianBanglaBreaking finalIndianBanglaBreakingUpperItem = indianBanglaBreakingUpperItem;


            IndianHindiBreaking indianHindiBreakingCurrentItem = null, indianHindiBreakingUpperItem = null;
            if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                indianHindiBreakingCurrentItem = indianHindiBreakingList.get(serialNumber);
                indianHindiBreakingUpperItem = indianHindiBreakingList.get(serialNumber - 1);

                indianHindiBreakingCurrentItem.setSerial(serialNumber - 1);
                indianHindiBreakingUpperItem.setSerial(serialNumber);
            }
            IndianHindiBreaking finalIndianHindiBreakingCurrentItem = indianHindiBreakingCurrentItem;
            IndianHindiBreaking finalIndianHindiBreakingUpperItem = indianHindiBreakingUpperItem;


            IndianEnglishBreaking indianEnglishBreakingCurrentItem = null, indianEnglishBreakingUpperItem = null;
            if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                indianEnglishBreakingCurrentItem = indianEnglishBreakingList.get(serialNumber);
                indianEnglishBreakingUpperItem = indianEnglishBreakingList.get(serialNumber - 1);

                indianEnglishBreakingCurrentItem.setSerial(serialNumber - 1);
                indianEnglishBreakingUpperItem.setSerial(serialNumber);
            }
            IndianEnglishBreaking finalIndianEnglishBreakingCurrentItem = indianEnglishBreakingCurrentItem;
            IndianEnglishBreaking finalIndianEnglishBreakingUpperItem = indianEnglishBreakingUpperItem;


            insertingDataFlag = true;
            dataStatusFlagInDb = true;

            Completable.fromAction(() -> {
                if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                    newsDatabase.bdBreakingDao().updateNews(finalBdBreakingCurrentItem, finalBdBreakingUpperItem);
                } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                    newsDatabase.indianBanglaBreakingDao().updateNews(finalIndianBanglaBreakingCurrentItem, finalIndianBanglaBreakingUpperItem);
                } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                    newsDatabase.indianHindiBreakingDao().updateNews(finalIndianHindiBreakingCurrentItem, finalIndianHindiBreakingUpperItem);
                } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                    newsDatabase.indianEnglishBreakingDao().updateNews(finalIndianEnglishBreakingCurrentItem, finalIndianEnglishBreakingUpperItem);
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
            BdBreaking bdBreakingCurrentItem = null, bdBreakingDownItem = null;
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                if (serialNumber < (bdBreakingList.size() - 1)) {
                    bdBreakingCurrentItem = bdBreakingList.get(serialNumber);
                    bdBreakingDownItem = bdBreakingList.get(serialNumber + 1);

                    bdBreakingCurrentItem.setSerial(serialNumber + 1);
                    bdBreakingDownItem.setSerial(serialNumber);
                }
            }
            BdBreaking finalBdBreakingCurrentItem = bdBreakingCurrentItem;
            BdBreaking finalBdBreakingDownItem = bdBreakingDownItem;


            IndianBanglaBreaking indianBanglaBreakingCurrentItem = null, indianBanglaBreakingDownItem = null;
            if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                if (serialNumber < (indianBanglaBreakingList.size() - 1)) {
                    indianBanglaBreakingCurrentItem = indianBanglaBreakingList.get(serialNumber);
                    indianBanglaBreakingDownItem = indianBanglaBreakingList.get(serialNumber + 1);

                    indianBanglaBreakingCurrentItem.setSerial(serialNumber + 1);
                    indianBanglaBreakingDownItem.setSerial(serialNumber);
                }
            }
            IndianBanglaBreaking finalIndianBanglaBreakingCurrentItem = indianBanglaBreakingCurrentItem;
            IndianBanglaBreaking finalIndianBanglaBreakingDownItem = indianBanglaBreakingDownItem;


        IndianHindiBreaking indianHindiBreakingCurrentItem = null, indianHindiBreakingDownItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            if (serialNumber < (indianHindiBreakingList.size() - 1)) {
                indianHindiBreakingCurrentItem = indianHindiBreakingList.get(serialNumber);
                indianHindiBreakingDownItem = indianHindiBreakingList.get(serialNumber + 1);

                indianHindiBreakingCurrentItem.setSerial(serialNumber + 1);
                indianHindiBreakingDownItem.setSerial(serialNumber);
            }
        }
        IndianHindiBreaking finalIndianHindiBreakingCurrentItem = indianHindiBreakingCurrentItem;
        IndianHindiBreaking finalIndianHindiBreakingDownItem = indianHindiBreakingDownItem;


        IndianEnglishBreaking indianEnglishBreakingCurrentItem = null, indianEnglishBreakingDownItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            if (serialNumber < (indianEnglishBreakingList.size() - 1)) {
                indianEnglishBreakingCurrentItem = indianEnglishBreakingList.get(serialNumber);
                indianEnglishBreakingDownItem = indianEnglishBreakingList.get(serialNumber + 1);

                indianEnglishBreakingCurrentItem.setSerial(serialNumber + 1);
                indianEnglishBreakingDownItem.setSerial(serialNumber);
            }
        }
        IndianEnglishBreaking finalIndianEnglishBreakingCurrentItem = indianEnglishBreakingCurrentItem;
        IndianEnglishBreaking finalIndianEnglishBreakingDownItem = indianEnglishBreakingDownItem;


            insertingDataFlag = true;
            dataStatusFlagInDb = true;

            Completable.fromAction(() -> {
                if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                    newsDatabase.bdBreakingDao().updateNews(finalBdBreakingCurrentItem, finalBdBreakingDownItem);
                } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                    newsDatabase.indianBanglaBreakingDao().updateNews(finalIndianBanglaBreakingCurrentItem, finalIndianBanglaBreakingDownItem);
                } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                    newsDatabase.indianHindiBreakingDao().updateNews(finalIndianHindiBreakingCurrentItem, finalIndianHindiBreakingDownItem);
                } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                    newsDatabase.indianEnglishBreakingDao().updateNews(finalIndianEnglishBreakingCurrentItem, finalIndianEnglishBreakingDownItem);
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
        BdBreaking bdBreakingCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            if (serialNumber <= (bdBreakingList.size() - 1) && serialNumber >= 0) {
                bdBreakingCurrentItem = bdBreakingList.get(serialNumber);
                bdBreakingCurrentItem.setVisibilityStatus("hidden");
            }
        }
        BdBreaking finalBdBreakingCurrentItem = bdBreakingCurrentItem;


        IndianBanglaBreaking indianBanglaBreakingCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            if (serialNumber <= (indianBanglaBreakingList.size() - 1) && serialNumber >= 0) {
                indianBanglaBreakingCurrentItem = indianBanglaBreakingList.get(serialNumber);
                indianBanglaBreakingCurrentItem.setVisibilityStatus("hidden");
            }
        }
        IndianBanglaBreaking finalIndianBanglaBreakingCurrentItem = indianBanglaBreakingCurrentItem;


        IndianHindiBreaking indianHindiBreakingCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            if (serialNumber <= (indianHindiBreakingList.size() - 1) && serialNumber >= 0) {
                indianHindiBreakingCurrentItem = indianHindiBreakingList.get(serialNumber);
                indianHindiBreakingCurrentItem.setVisibilityStatus("hidden");
            }
        }
        IndianHindiBreaking finalIndianHindiBreakingCurrentItem = indianHindiBreakingCurrentItem;


        IndianEnglishBreaking indianEnglishBreakingCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            if (serialNumber <= (indianEnglishBreakingList.size() - 1) && serialNumber >= 0) {
                indianEnglishBreakingCurrentItem = indianEnglishBreakingList.get(serialNumber);
                indianEnglishBreakingCurrentItem.setVisibilityStatus("hidden");
            }
        }
        IndianEnglishBreaking finalIndianEnglishBreakingCurrentItem = indianEnglishBreakingCurrentItem;


        insertingDataFlag = false;
        dataStatusFlagInDb = true;

        Completable.fromAction(() -> {
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdBreakingDao().updateNews(finalBdBreakingCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaBreakingDao().updateNews(finalIndianBanglaBreakingCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiBreakingDao().updateNews(finalIndianHindiBreakingCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                newsDatabase.indianEnglishBreakingDao().updateNews(finalIndianEnglishBreakingCurrentItem);
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
        BdBreaking bdBreakingCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            for (int i = 0; i < bdBreakingUnVisibleTemporaryList.size(); i++) {
                if (paperName.equalsIgnoreCase(bdBreakingUnVisibleTemporaryList.get(i).getPaperName())) {
                    bdBreakingCurrentItem = bdBreakingUnVisibleTemporaryList.get(i);
                    bdBreakingCurrentItem.setVisibilityStatus("visible");
                }
            }
        }
        BdBreaking finalBdBreakingCurrentItem = bdBreakingCurrentItem;


        IndianBanglaBreaking indianBanglaBreakingCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            for (int i = 0; i < indianBanglaBreakingUnVisibleTemporaryList.size(); i++) {
                if (paperName.equalsIgnoreCase(indianBanglaBreakingUnVisibleTemporaryList.get(i).getPaperName())) {
                    indianBanglaBreakingCurrentItem = indianBanglaBreakingUnVisibleTemporaryList.get(i);
                    indianBanglaBreakingCurrentItem.setVisibilityStatus("visible");
                }
            }
        }
        IndianBanglaBreaking finalIndianBanglaBreakingCurrentItem = indianBanglaBreakingCurrentItem;


        IndianHindiBreaking indianHindiBreakingCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            for (int i = 0; i < indianHindiBreakingUnVisibleTemporaryList.size(); i++) {
                if (paperName.equalsIgnoreCase(indianHindiBreakingUnVisibleTemporaryList.get(i).getPaperName())) {
                    indianHindiBreakingCurrentItem = indianHindiBreakingUnVisibleTemporaryList.get(i);
                    indianHindiBreakingCurrentItem.setVisibilityStatus("visible");
                }
            }
        }
        IndianHindiBreaking finalIndianHindiBreakingCurrentItem = indianHindiBreakingCurrentItem;


        IndianEnglishBreaking indianEnglishBreakingCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            for (int i = 0; i < indianEnglishBreakingUnVisibleTemporaryList.size(); i++) {
                if (paperName.equalsIgnoreCase(indianEnglishBreakingUnVisibleTemporaryList.get(i).getPaperName())) {
                    indianEnglishBreakingCurrentItem = indianEnglishBreakingUnVisibleTemporaryList.get(i);
                    indianEnglishBreakingCurrentItem.setVisibilityStatus("visible");
                }
            }
        }
        IndianEnglishBreaking finalIndianEnglishBreakingCurrentItem = indianEnglishBreakingCurrentItem;


        insertingDataFlag = false;
        dataStatusFlagInDb = true;

        Completable.fromAction(() -> {
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdBreakingDao().updateNews(finalBdBreakingCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaBreakingDao().updateNews(finalIndianBanglaBreakingCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiBreakingDao().updateNews(finalIndianHindiBreakingCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                newsDatabase.indianEnglishBreakingDao().updateNews(finalIndianEnglishBreakingCurrentItem);
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
    public void changeItemBackgroundColor(int serialNumber, String colorName) {
        BdBreaking bdBreakingCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            bdBreakingCurrentItem = bdBreakingList.get(serialNumber);
            bdBreakingCurrentItem.setBackgroundColor(colorName);
        }
        BdBreaking finalBdBreakingCurrentItem = bdBreakingCurrentItem;


        IndianBanglaBreaking indianBanglaBreakingCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            indianBanglaBreakingCurrentItem = indianBanglaBreakingList.get(serialNumber);
            indianBanglaBreakingCurrentItem.setBackgroundColor(colorName);
        }
        IndianBanglaBreaking finalIndianBanglaBreakingCurrentItem = indianBanglaBreakingCurrentItem;

        IndianHindiBreaking indianHindiBreakingCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            indianHindiBreakingCurrentItem = indianHindiBreakingList.get(serialNumber);
            indianHindiBreakingCurrentItem.setBackgroundColor(colorName);
        }
        IndianHindiBreaking finalIndianHindiBreakingCurrentItem = indianHindiBreakingCurrentItem;


        IndianEnglishBreaking indianEnglishBreakingCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            indianEnglishBreakingCurrentItem = indianEnglishBreakingList.get(serialNumber);
            indianEnglishBreakingCurrentItem.setBackgroundColor(colorName);
        }
        IndianEnglishBreaking finalIndianEnglishBreakingCurrentItem = indianEnglishBreakingCurrentItem;



        insertingDataFlag = false;
        dataStatusFlagInDb = true;


        Completable.fromAction(() -> {
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdBreakingDao().updateNews(finalBdBreakingCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaBreakingDao().updateNews(finalIndianBanglaBreakingCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiBreakingDao().updateNews(finalIndianHindiBreakingCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                newsDatabase.indianEnglishBreakingDao().updateNews(finalIndianEnglishBreakingCurrentItem);
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
    public void changeItemTextColor(int serialNumber, String colorName) {
        BdBreaking bdBreakingCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            bdBreakingCurrentItem = bdBreakingList.get(serialNumber);
            bdBreakingCurrentItem.setTextColor(colorName);
        }
        BdBreaking finalBdBreakingCurrentItem = bdBreakingCurrentItem;


        IndianBanglaBreaking indianBanglaBreakingCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            indianBanglaBreakingCurrentItem = indianBanglaBreakingList.get(serialNumber);
            indianBanglaBreakingCurrentItem.setTextColor(colorName);
        }
        IndianBanglaBreaking finalIndianBanglaBreakingCurrentItem = indianBanglaBreakingCurrentItem;


        IndianHindiBreaking indianHindiBreakingCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            indianHindiBreakingCurrentItem = indianHindiBreakingList.get(serialNumber);
            indianHindiBreakingCurrentItem.setTextColor(colorName);
        }
        IndianHindiBreaking finalIndianHindiBreakingCurrentItem = indianHindiBreakingCurrentItem;


        IndianEnglishBreaking indianEnglishBreakingCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            indianEnglishBreakingCurrentItem = indianEnglishBreakingList.get(serialNumber);
            indianEnglishBreakingCurrentItem.setTextColor(colorName);
        }
        IndianEnglishBreaking finalIndianEnglishBreakingCurrentItem = indianEnglishBreakingCurrentItem;


        insertingDataFlag = false;
        dataStatusFlagInDb = true;


        Completable.fromAction(() -> {
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdBreakingDao().updateNews(finalBdBreakingCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaBreakingDao().updateNews(finalIndianBanglaBreakingCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiBreakingDao().updateNews(finalIndianHindiBreakingCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                newsDatabase.indianEnglishBreakingDao().updateNews(finalIndianEnglishBreakingCurrentItem);
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
        BdBreaking bdBreakingCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            if (serialNumber <= (bdBreakingList.size() - 1) && serialNumber >= 0) {
                bdBreakingCurrentItem = bdBreakingList.get(serialNumber);
                bdBreakingCurrentItem.setNotificationStatus("on");
            }
        }
        BdBreaking finalBdBreakingCurrentItem = bdBreakingCurrentItem;


        IndianBanglaBreaking indianBanglaBreakingCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            if (serialNumber <= (indianBanglaBreakingList.size() - 1) && serialNumber >= 0) {
                indianBanglaBreakingCurrentItem = indianBanglaBreakingList.get(serialNumber);
                indianBanglaBreakingCurrentItem.setNotificationStatus("on");
            }
        }
        IndianBanglaBreaking finalIndianBanglaBreakingCurrentItem = indianBanglaBreakingCurrentItem;


        IndianHindiBreaking indianHindiBreakingCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            if (serialNumber <= (indianHindiBreakingList.size() - 1) && serialNumber >= 0) {
                indianHindiBreakingCurrentItem = indianHindiBreakingList.get(serialNumber);
                indianHindiBreakingCurrentItem.setNotificationStatus("on");
            }
        }
        IndianHindiBreaking finalIndianHindiBreakingCurrentItem = indianHindiBreakingCurrentItem;


        IndianEnglishBreaking indianEnglishBreakingCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            if (serialNumber <= (indianEnglishBreakingList.size() - 1) && serialNumber >= 0) {
                indianEnglishBreakingCurrentItem = indianEnglishBreakingList.get(serialNumber);
                indianEnglishBreakingCurrentItem.setNotificationStatus("on");
            }
        }
        IndianEnglishBreaking finalIndianEnglishBreakingCurrentItem = indianEnglishBreakingCurrentItem;


        insertingDataFlag = false;
        dataStatusFlagInDb = true;

        Completable.fromAction(() -> {
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdBreakingDao().updateNews(finalBdBreakingCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaBreakingDao().updateNews(finalIndianBanglaBreakingCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiBreakingDao().updateNews(finalIndianHindiBreakingCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                newsDatabase.indianEnglishBreakingDao().updateNews(finalIndianEnglishBreakingCurrentItem);
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
        BdBreaking bdBreakingCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            if (serialNumber <= (bdBreakingList.size() - 1) && serialNumber >= 0) {
                bdBreakingCurrentItem = bdBreakingList.get(serialNumber);
                bdBreakingCurrentItem.setNotificationStatus("off");
            }
        }
        BdBreaking finalBdBreakingCurrentItem = bdBreakingCurrentItem;


        IndianBanglaBreaking indianBanglaBreakingCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            if (serialNumber <= (indianBanglaBreakingList.size() - 1) && serialNumber >= 0) {
                indianBanglaBreakingCurrentItem = indianBanglaBreakingList.get(serialNumber);
                indianBanglaBreakingCurrentItem.setNotificationStatus("off");
            }
        }
        IndianBanglaBreaking finalIndianBanglaBreakingCurrentItem = indianBanglaBreakingCurrentItem;


        IndianHindiBreaking indianHindiBreakingCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            if (serialNumber <= (indianHindiBreakingList.size() - 1) && serialNumber >= 0) {
                indianHindiBreakingCurrentItem = indianHindiBreakingList.get(serialNumber);
                indianHindiBreakingCurrentItem.setNotificationStatus("off");
            }
        }
        IndianHindiBreaking finalIndianHindiBreakingCurrentItem = indianHindiBreakingCurrentItem;


        IndianEnglishBreaking indianEnglishBreakingCurrentItem = null;
        if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            if (serialNumber <= (indianEnglishBreakingList.size() - 1) && serialNumber >= 0) {
                indianEnglishBreakingCurrentItem = indianEnglishBreakingList.get(serialNumber);
                indianEnglishBreakingCurrentItem.setNotificationStatus("off");
            }
        }
        IndianEnglishBreaking finalIndianEnglishBreakingCurrentItem = indianEnglishBreakingCurrentItem;


        insertingDataFlag = false;
        dataStatusFlagInDb = true;

        Completable.fromAction(() -> {
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                newsDatabase.bdBreakingDao().updateNews(finalBdBreakingCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                newsDatabase.indianBanglaBreakingDao().updateNews(finalIndianBanglaBreakingCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                newsDatabase.indianHindiBreakingDao().updateNews(finalIndianHindiBreakingCurrentItem);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                newsDatabase.indianEnglishBreakingDao().updateNews(finalIndianEnglishBreakingCurrentItem);
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


    public void shortingBdBreakingList(List<RecyclerItemModel> recyclerItemModelList) {
        if (shortedList == null) {
            shortedList = new MutableLiveData<>();
        }
        temporaryShortingList.clear();
        String title;
        RecyclerItemModel recyclerItemModel;
        for (int i = 0; i < bdBreakingList.size(); i++) {
            title = bdBreakingList.get(i).getPaperName();
            for (int j = 0; j < recyclerItemModelList.size(); j++) {
                if (title.equalsIgnoreCase(recyclerItemModelList.get(j).getTitle())) {
                    recyclerItemModel = recyclerItemModelList.get(j);
                    recyclerItemModel.setSerialNumber(bdBreakingList.get(i).getSerial());
                    recyclerItemModel.setBackgroundColor(bdBreakingList.get(i).getBackgroundColor());
                    recyclerItemModel.setTextColor(bdBreakingList.get(i).getTextColor());
                    recyclerItemModel.setNotificationStatus(bdBreakingList.get(i).getNotificationStatus());
                    temporaryShortingList.add(recyclerItemModel);
                }
            }
        }
        shortedList.setValue(temporaryShortingList);
    }
    public LiveData<List<BdBreaking>> getBdBreakingUnVisibleList() {
        if (bdBreakingUnVisibleList == null) {
            bdBreakingUnVisibleList = new MutableLiveData<>();
        }
        return bdBreakingUnVisibleList;
    }
    public void checkBangladeshBreakingNewsDataInDb(List<String> nameList, List<String> urlList) {
        if (bangladeshiAllBreakingNewsObserver == null) {
            bangladeshiAllBreakingNewsObserver = bdBreakings -> {
                bdBreakingList.clear();
                bdBreakingList.addAll(bdBreakings);
                bdBreakingUnVisibleTemporaryList.clear();
                if (dataStatusFlagInDb && itemList.getValue() != null && itemList.getValue().size() > 0) {
                    itemList.setValue(itemList.getValue());
                }
                if (bdBreakings.size() > 0 && !insertingDataFlag) {
                    temporaryList.clear();
                    itemList.setValue(temporaryList);
                    for (int i = 0; i < bdBreakings.size(); i++) {
                        if (bdBreakings.get(i).getVisibilityStatus().equalsIgnoreCase("visible")) {
                            loadPageDocument(bdBreakings.get(i).getPaperUrl());
                        } else {
                            bdBreakingUnVisibleTemporaryList.add(bdBreakings.get(i));
                        }
                    }
                    if (bdBreakingUnVisibleList == null) {
                        bdBreakingUnVisibleList = new MutableLiveData<>();
                    }
                    bdBreakingUnVisibleList.setValue(bdBreakingUnVisibleTemporaryList);
                    insertingDataFlag = true;
                } else {
                    insertingDataFlag = true;
                    if (nameList != null && urlList != null && !dataStatusFlagInDb) {
                        for (int i = 0; i < urlList.size(); i++) {
                            BdBreaking bdBreaking = new BdBreaking();
                            bdBreaking.setSerial(i);
                            bdBreaking.setVisibilityStatus("visible");
                            bdBreaking.setPaperUrl(urlList.get(i));
                            bdBreaking.setPaperName(nameList.get(i));
                            bdBreaking.setBackgroundColor("SkyBlue");
                            bdBreaking.setTextColor("White");
                            if (i==0) {
                                bdBreaking.setNotificationStatus("on");
                            } else {
                                bdBreaking.setNotificationStatus("off");
                            }
                            Completable.fromAction(() -> {
                                newsDatabase.bdBreakingDao().insertNews(bdBreaking);
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
                        dataStatusFlagInDb = true;
                    }
                    insertingDataFlag = false;
                }
            };
        }
        bdBreakingLiveData = newsDatabase.bdBreakingDao().getAllNews();
        bdBreakingLiveData.observeForever(bangladeshiAllBreakingNewsObserver);
    }


    public void shortingIndianBanglaBreakingList(List<RecyclerItemModel> recyclerItemModelList) {
        if (shortedList == null) {
            shortedList = new MutableLiveData<>();
        }
        temporaryShortingList.clear();
        String title;
        RecyclerItemModel recyclerItemModel;
        for (int i = 0; i < indianBanglaBreakingList.size(); i++) {
            title = indianBanglaBreakingList.get(i).getPaperName();
            for (int j = 0; j < recyclerItemModelList.size(); j++) {
                if (title.equalsIgnoreCase(recyclerItemModelList.get(j).getTitle())) {
                    recyclerItemModel = recyclerItemModelList.get(j);
                    recyclerItemModel.setSerialNumber(indianBanglaBreakingList.get(i).getSerial());
                    recyclerItemModel.setBackgroundColor(indianBanglaBreakingList.get(i).getBackgroundColor());
                    recyclerItemModel.setTextColor(indianBanglaBreakingList.get(i).getTextColor());
                    recyclerItemModel.setNotificationStatus(indianBanglaBreakingList.get(i).getNotificationStatus());
                    temporaryShortingList.add(recyclerItemModel);
                }
            }
        }
        shortedList.setValue(temporaryShortingList);
    }
    public LiveData<List<IndianBanglaBreaking>> getIndianBanglaBreakingUnVisibleList() {
        if (indianBanglaBreakingUnVisibleList == null) {
            indianBanglaBreakingUnVisibleList = new MutableLiveData<>();
        }
        return indianBanglaBreakingUnVisibleList;
    }
    public void checkIndianBanglaBreakingNewsDataInDb(List<String> nameList, List<String> urlList) {
        if (indianBanglaAllBreakingNewsObserver == null) {
            indianBanglaAllBreakingNewsObserver = indianBanglaBreakings -> {
                indianBanglaBreakingList.clear();
                indianBanglaBreakingList.addAll(indianBanglaBreakings);
                indianBanglaBreakingUnVisibleTemporaryList.clear();
                if (dataStatusFlagInDb && itemList.getValue() != null && itemList.getValue().size() > 0) {
                    itemList.setValue(itemList.getValue());
                }
                if (indianBanglaBreakings.size() > 0 && !insertingDataFlag) {
                    temporaryList.clear();
                    itemList.setValue(temporaryList);
                    for (int i = 0; i < indianBanglaBreakings.size(); i++) {
                        if (indianBanglaBreakings.get(i).getVisibilityStatus().equalsIgnoreCase("visible")) {
                            loadPageDocument(indianBanglaBreakings.get(i).getPaperUrl());
                        } else {
                            indianBanglaBreakingUnVisibleTemporaryList.add(indianBanglaBreakings.get(i));
                        }
                    }
                    if (indianBanglaBreakingUnVisibleList == null) {
                        indianBanglaBreakingUnVisibleList = new MutableLiveData<>();
                    }
                    indianBanglaBreakingUnVisibleList.setValue(indianBanglaBreakingUnVisibleTemporaryList);
                    insertingDataFlag = true;
                } else {
                    insertingDataFlag = true;
                    if (nameList != null && urlList != null && !dataStatusFlagInDb) {
                        for (int i = 0; i < urlList.size(); i++) {
                            IndianBanglaBreaking indianBanglaBreaking = new IndianBanglaBreaking();
                            indianBanglaBreaking.setSerial(i);
                            indianBanglaBreaking.setVisibilityStatus("visible");
                            indianBanglaBreaking.setPaperUrl(urlList.get(i));
                            indianBanglaBreaking.setPaperName(nameList.get(i));
                            indianBanglaBreaking.setBackgroundColor("SkyBlue");
                            indianBanglaBreaking.setTextColor("White");
                            if (i==0) {
                                indianBanglaBreaking.setNotificationStatus("on");
                            } else {
                                indianBanglaBreaking.setNotificationStatus("off");
                            }
                            Completable.fromAction(() -> {
                                newsDatabase.indianBanglaBreakingDao().insertNews(indianBanglaBreaking);
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
                        dataStatusFlagInDb = true;
                    }
                    insertingDataFlag = false;
                }
            };
        }
        indianBanglaBreakingLiveData = newsDatabase.indianBanglaBreakingDao().getAllNews();
        indianBanglaBreakingLiveData.observeForever(indianBanglaAllBreakingNewsObserver);
    }


    public void shortingIndianHindiBreakingList(List<RecyclerItemModel> recyclerItemModelList) {
        if (shortedList == null) {
            shortedList = new MutableLiveData<>();
        }
        temporaryShortingList.clear();
        String title;
        RecyclerItemModel recyclerItemModel;
        for (int i = 0; i < indianHindiBreakingList.size(); i++) {
            title = indianHindiBreakingList.get(i).getPaperName();
            for (int j = 0; j < recyclerItemModelList.size(); j++) {
                if (title.equalsIgnoreCase(recyclerItemModelList.get(j).getTitle())) {
                    recyclerItemModel = recyclerItemModelList.get(j);
                    recyclerItemModel.setSerialNumber(indianHindiBreakingList.get(i).getSerial());
                    recyclerItemModel.setBackgroundColor(indianHindiBreakingList.get(i).getBackgroundColor());
                    recyclerItemModel.setTextColor(indianHindiBreakingList.get(i).getTextColor());
                    recyclerItemModel.setNotificationStatus(indianHindiBreakingList.get(i).getNotificationStatus());
                    temporaryShortingList.add(recyclerItemModel);
                }
            }
        }
        shortedList.setValue(temporaryShortingList);
    }
    public LiveData<List<IndianHindiBreaking>> getIndianHindiBreakingUnVisibleList() {
        if (indianHindiBreakingUnVisibleList == null) {
            indianHindiBreakingUnVisibleList = new MutableLiveData<>();
        }
        return indianHindiBreakingUnVisibleList;
    }
    public void checkIndianHindiBreakingNewsDataInDb(List<String> nameList, List<String> urlList) {
        if (indianHindiAllBreakingNewsObserver == null) {
            indianHindiAllBreakingNewsObserver = indianHindiBreakings -> {
                indianHindiBreakingList.clear();
                indianHindiBreakingList.addAll(indianHindiBreakings);
                indianHindiBreakingUnVisibleTemporaryList.clear();
                if (dataStatusFlagInDb && itemList.getValue() != null && itemList.getValue().size() > 0) {
                    itemList.setValue(itemList.getValue());
                }
                if (indianHindiBreakings.size() > 0 && !insertingDataFlag) {
                    temporaryList.clear();
                    itemList.setValue(temporaryList);
                    for (int i = 0; i < indianHindiBreakings.size(); i++) {
                        if (indianHindiBreakings.get(i).getVisibilityStatus().equalsIgnoreCase("visible")) {
                            loadPageDocument(indianHindiBreakings.get(i).getPaperUrl());
                        } else {
                            indianHindiBreakingUnVisibleTemporaryList.add(indianHindiBreakings.get(i));
                        }
                    }
                    if (indianHindiBreakingUnVisibleList == null) {
                        indianHindiBreakingUnVisibleList = new MutableLiveData<>();
                    }
                    indianHindiBreakingUnVisibleList.setValue(indianHindiBreakingUnVisibleTemporaryList);
                    insertingDataFlag = true;
                } else {
                    insertingDataFlag = true;
                    if (nameList != null && urlList != null && !dataStatusFlagInDb) {
                        for (int i = 0; i < urlList.size(); i++) {
                            IndianHindiBreaking indianHindiBreaking = new IndianHindiBreaking();
                            indianHindiBreaking.setSerial(i);
                            indianHindiBreaking.setVisibilityStatus("visible");
                            indianHindiBreaking.setPaperUrl(urlList.get(i));
                            indianHindiBreaking.setPaperName(nameList.get(i));
                            indianHindiBreaking.setBackgroundColor("SkyBlue");
                            indianHindiBreaking.setTextColor("White");
                            if (i==0) {
                                indianHindiBreaking.setNotificationStatus("on");
                            } else {
                                indianHindiBreaking.setNotificationStatus("off");
                            }
                            Completable.fromAction(() -> {
                                newsDatabase.indianHindiBreakingDao().insertNews(indianHindiBreaking);
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
                        dataStatusFlagInDb = true;
                    }
                    insertingDataFlag = false;
                }
            };
        }
        indianHindiBreakingLiveData = newsDatabase.indianHindiBreakingDao().getAllNews();
        indianHindiBreakingLiveData.observeForever(indianHindiAllBreakingNewsObserver);
    }


    public void shortingIndianEnglishBreakingList(List<RecyclerItemModel> recyclerItemModelList) {
        if (shortedList == null) {
            shortedList = new MutableLiveData<>();
        }
        temporaryShortingList.clear();
        String title;
        RecyclerItemModel recyclerItemModel;
        for (int i = 0; i < indianEnglishBreakingList.size(); i++) {
            title = indianEnglishBreakingList.get(i).getPaperName();
            for (int j = 0; j < recyclerItemModelList.size(); j++) {
                if (title.equalsIgnoreCase(recyclerItemModelList.get(j).getTitle())) {
                    recyclerItemModel = recyclerItemModelList.get(j);
                    recyclerItemModel.setSerialNumber(indianEnglishBreakingList.get(i).getSerial());
                    recyclerItemModel.setBackgroundColor(indianEnglishBreakingList.get(i).getBackgroundColor());
                    recyclerItemModel.setTextColor(indianEnglishBreakingList.get(i).getTextColor());
                    recyclerItemModel.setNotificationStatus(indianEnglishBreakingList.get(i).getNotificationStatus());
                    temporaryShortingList.add(recyclerItemModel);
                }
            }
        }
        shortedList.setValue(temporaryShortingList);
    }
    public LiveData<List<IndianEnglishBreaking>> getIndianEnglishBreakingUnVisibleList() {
        if (indianEnglishBreakingUnVisibleList == null) {
            indianEnglishBreakingUnVisibleList = new MutableLiveData<>();
        }
        return indianEnglishBreakingUnVisibleList;
    }
    public void checkIndianEnglishBreakingNewsDataInDb(List<String> nameList, List<String> urlList) {
        if (indianEnglishAllBreakingNewsObserver == null) {
            indianEnglishAllBreakingNewsObserver = indianEnglishBreakings -> {
                indianEnglishBreakingList.clear();
                indianEnglishBreakingList.addAll(indianEnglishBreakings);
                indianEnglishBreakingUnVisibleTemporaryList.clear();
                if (dataStatusFlagInDb && itemList.getValue() != null && itemList.getValue().size() > 0) {
                    itemList.setValue(itemList.getValue());
                }
                if (indianEnglishBreakings.size() > 0 && !insertingDataFlag) {
                    temporaryList.clear();
                    itemList.setValue(temporaryList);
                    for (int i = 0; i < indianEnglishBreakings.size(); i++) {
                        if (indianEnglishBreakings.get(i).getVisibilityStatus().equalsIgnoreCase("visible")) {
                            loadPageDocument(indianEnglishBreakings.get(i).getPaperUrl());
                        } else {
                            indianEnglishBreakingUnVisibleTemporaryList.add(indianEnglishBreakings.get(i));
                        }
                    }
                    if (indianEnglishBreakingUnVisibleList == null) {
                        indianEnglishBreakingUnVisibleList = new MutableLiveData<>();
                    }
                    indianEnglishBreakingUnVisibleList.setValue(indianEnglishBreakingUnVisibleTemporaryList);
                    insertingDataFlag = true;
                } else {
                    insertingDataFlag = true;
                    if (nameList != null && urlList != null && !dataStatusFlagInDb) {
                        for (int i = 0; i < urlList.size(); i++) {
                            IndianEnglishBreaking indianEnglishBreaking = new IndianEnglishBreaking();
                            indianEnglishBreaking.setSerial(i);
                            indianEnglishBreaking.setVisibilityStatus("visible");
                            indianEnglishBreaking.setPaperUrl(urlList.get(i));
                            indianEnglishBreaking.setPaperName(nameList.get(i));
                            indianEnglishBreaking.setBackgroundColor("SkyBlue");
                            indianEnglishBreaking.setTextColor("White");
                            if (i==0) {
                                indianEnglishBreaking.setNotificationStatus("on");
                            } else {
                                indianEnglishBreaking.setNotificationStatus("off");
                            }
                            Completable.fromAction(() -> {
                                newsDatabase.indianEnglishBreakingDao().insertNews(indianEnglishBreaking);
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
                        dataStatusFlagInDb = true;
                    }
                    insertingDataFlag = false;
                }
            };
        }
        indianEnglishBreakingLiveData = newsDatabase.indianEnglishBreakingDao().getAllNews();
        indianEnglishBreakingLiveData.observeForever(indianEnglishAllBreakingNewsObserver);
    }





    private void setProthomAloBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.custom-story-card-4-data a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.prothomAlo);
            list.add(newsAndLinkModel);
        }

        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("প্রথম আলো (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setBdProtidinBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select(".container > .jctkr-scroll > .js-conveyor-example ul li");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).select("a").attr("href");
                String news = allList.get(i).select("a").text();
                String link = MyUrl.bdProtidin + temporaryLink;
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bdProtidin);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("বাংলাদেশ প্রতিদিন (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setKalerKhantoBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul.content.mCustomScrollbar li");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).select("a").attr("href");
                String news = allList.get(i).select("a").text();
                String link = MyUrl.kalerKhanto + temporaryLink;
                if (news.length() >= 15) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.kalerKhanto);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG, "error is " + e.getMessage());
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("কালের কণ্ঠ (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setSomokalBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.bn-title+ul li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.somokal);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG, "error is " + e.getMessage());
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("সমকাল (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setDailyJanaKhantoBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("marquee a");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String news = allList.get(i).select("a").text();
                String link = MyUrl.dailyJonoKhanto + temporaryLink;
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.somokal);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG, "error is " + e.getMessage());
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("দৈনিক জনকন্ঠ (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setBhorerKagojBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.podan10-news h4.news-title a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bhorerKagoj);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG, "error is " + e.getMessage());
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("ভোরের কাগজ (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setDailyInqilabBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("marquee ul li a");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).select("a").text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyInqilab);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG, "error is " + e.getMessage());
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("দৈনিক ইনকিলাব (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setDailyNayaDigantaBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.col-lg-7.col-md-5.col-sm-5.column-no-left-padding a");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).select("a").text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyNayaDiganta);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG, "error is " + e.getMessage());
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("নয়া দিগন্ত (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setAmarDesh24BreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.panel-body div.focus-item.clearfix h2 a");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.amarDesh24);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG, "error is " + e.getMessage());
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("আমার দেশ 24 (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setDailyIttefaqBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                if (news.length() >= 20) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyIttefaq);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG, "error is " + e.getMessage());
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("দৈনিক ইত্তেফাক (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setManobJominBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.sec-box-home h4 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.manobJomin+temporaryLink;
                String news = allList.get(i).text();
                if (news.length() >= 20) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.manobJomin);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG, "error is " + e.getMessage());
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("মানবজমিন (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setSongbadProtidinBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.bn-news ul li a");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                if (news.length() >= 20) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.songbadProtidin);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG, "error is " + e.getMessage());
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("সংবাদ প্রতিদিন (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setManobKanthaBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.ticker-container ul div li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.manobKantha);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG, "error is " + e.getMessage());
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("মানবকণ্ঠ (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setBangladeshJournalBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("marquee ul li a");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bangladeshJournal);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG, "error is " + e.getMessage());
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("বাংলাদেশ জার্নাল (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setTheDailyVorerPataBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul li[data-category=শিরোনাম] a");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String news = allList.get(i).text();
                String link = MyUrl.theDailyVorerPata + temporaryLink;
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.theDailyVorerPata);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG, "error is " + e.getMessage());
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("ভোরের পাতা (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
//====================================Bangladesh Breaking News method staying in above========================================


    private void setAnandaBazarBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.abp-atf-left-story-block a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.anandaBazarBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.anandaBazarBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("আনন্দবাজার পত্রিকা (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setSangbadPratidinBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul.clearfix.tatka_update_list li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                if (news.length() >= 15) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.sangbadPratidinBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("সংবাদ প্রতিদিন (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setBartamanPatrikaBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h5 center a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link = "https://bartamanpatrika.com/" + temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bartamanPatrikaBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("বর্তমান (সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setGanaShaktiBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.img-text-all a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).select("p").text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.ganashaktiBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("গণশক্তি (আজকের খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setUttarBangaSambadBreekingNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.uttarBangaSambadBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("উত্তরবঙ্গ সংবাদ (উত্তরবঙ্গ)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setEbelaBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a.Readerchoice[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link = MyUrl.ebelaBreakingNews + temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.ebelaBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("এবেলা (সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setAsomiyaPratidinBreekingNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.asomiyaPratidinBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("অসমীয়া প্রতিদিন (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setAajKaalBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h6 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.aajkaalBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("আজকাল (আকর্ষনীয় খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setKhaborOnlineBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.mvp-feat5-side-list.left.relative a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).select("h2").text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.khaborOnlinBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("খবর অনলাইন (নজরে)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setJugaShankaBreekingNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.jugasankhaBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("যুগশঙ্ক (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setJagaranTripuraBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul.newsticker li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.jagarantripuraBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("জাগরণত্রিপুরা (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setGanadabiBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div#breaking-news ul li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.ganadabiBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("গণদাবী (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setOneIndiaBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul#hp-top-news-left li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link = MyUrl.oneIndiaBanglaBreakingNews + temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.oneIndiaBanglaBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("ওয়ান ইন্ডিয়া (সাধারণ খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setKolkata247BreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.td-trending-now-display-area h3.entry-title.td-module-title a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.kolkata247BreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("কলকাতা ২৪*৭ (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setKhabor24GhontaBreekingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.exclusive-slides a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.khabor24BreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("খবর ২৪ ঘন্টা (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
//====================================Indian Bangla Breaking News method staying in above========================================



    private void setJagranBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ol.p1Box li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.jagranBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.jagranBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("जागरण (ट्रेंडिंग न्यूज़)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setBhaskarBreakingNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.bhaskarBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("देनिक भास्कर (मुख्य समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setAmarUjalaBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.toptrending section h2 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.amarUjalaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.amarUjalaBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("अमर उजाला (शीर्ष ट्रेंडिंग)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setLiveHindustanBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul.left-top-news.no-pad.n-tazakhbre li h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.liveHindustanBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.liveHindustanBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("लाइव हिन्दुस्तान (ताज़ा खबर)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setNavBharatTimesBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul.col12.most-read-stroies li a.table_row[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.navBharatTimesBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("नव भारत टाइम्स (सबसे ज़्यादा पढ़ा हुआ)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setJanSattaBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.topnewsbx.left.MB30 ul li span a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                if (news.length()>=15) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.janSattaBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("जनसत्ता (बड़ी खबर)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setPunjabKeSariBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul#ContentPlaceHolder1_dv_middle_section_top li h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.punjabKeSariBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("पंजाब केसरी (शीर्ष आलेख)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setHariBhoomiBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div#home_top_right_level_1 h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.hariBhoomiBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.hariBhoomiBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("हरिभूमि (वायरल समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setDivyaHimachalBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h2.ntitle a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.divyaHimachalBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("दिव्य हिमाचल (सामान्य समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setPrabhaSakshiBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("marquee a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.prabhaSakshiBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.prabhaSakshiBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("प्रभा साक्षी (ताज़ा खबर)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setDeshDootBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a[href^=https://www.deshdoot.com/local-news/]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                if (news.length()>=15) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.deshDootBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("देशदूत (स्थानीय समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setDainikTribuneOnlineBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.opinion-news-block h4 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.dainikTribuneOnlineBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dainikTribuneOnlineBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("दैनिक ट्रिब्यून (विचार खबर)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setSamacharJagatBreakingNews(Document document) {
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
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.samacharJagatBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("SamacharJagat (ताज़ा खबर)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setKhasKhabarBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul.slides li div.slide-txt2 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.khasKhabarBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("अच्छी खबर (मुख्य समाचार)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
//====================================Indian Hindi Breaking News method staying in above========================================



    private void setHindustanTimesBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.storyShortDetail h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String linkWithExtras = allList.get(i).attr("href");
                String temporaryLink=linkWithExtras.substring(1);
                String link=MyUrl.hindustanTimesBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                if (news.length()>=15) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.hindustanTimesBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("Hindustan Times (Top News)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setIndianExpressBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("ul.breaking li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.indianExpressEnglishBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("The Indian Express (Breaking News)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setDailyPioneerBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.breakingNewsSlider div.swiper-slide h2 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.dailyPioneerBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dailyPioneerBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("The Pioneer (Breaking News)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setDeccanHeraldBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("section#home-top-stories ul li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.deccanHeraldBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.deccanHeraldBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("Deccan Herald (General News)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setDnaIndiaBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h3.headlines_pg a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.dnaIndiaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.dnaIndiaHeadlinesBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("DNA India (Headlines)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setDeccanChronicleBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div#topStory div.col-sm-12.col-xs-12.tstry-feed-sml-a a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.deccanChronicleBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.deccanChronicleBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("Deccan Chronicle (Top Stories)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setAsianAgeBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.single_left_coloum_wrapper.other-top-stories h3 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String linkWithExtras = allList.get(i).attr("href");
                String temporaryLink=linkWithExtras.substring(1);
                String link=MyUrl.asianAgeBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.asianAgeBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("The Asian Age (Top Stories)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setEconomicTimesBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div#topNewsTabs ul.newsList.clearfix li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.economicTimesBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.economicTimesBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("Economic Times (Top News)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setBusinessStandardBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.latestnews-txt  div.mt-news ul li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.businessStandardBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.businessStandardBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("Business Standard (Just In)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setFinancialExpressBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div.topblock-left h2 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.financialExpressBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("Financial Express (Top News)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setNewIndianExpressBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("a[href^=https://www.newindianexpress.com/good-news/]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                if (news.length()>=15) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.newIndianExpressBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("The New Indian Express (Good News)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setTribuneIndiaBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("div[title=Top Stories] h4 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.tribuneIndiaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                if (news.length()>=11) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.tribuneIndiaBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("The Tribune (Top Stories)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setLiveMintBreakingNews(Document document) {
        List<NewsAndLinkModel> list = new ArrayList<>();
        try {
            Elements allList = document.select("h2.headline a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String linkWithExtras = allList.get(i).attr("href");
                String temporaryLink=linkWithExtras.substring(1);
                String link=MyUrl.liveMintBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(e.getMessage(), MyUrl.liveMintBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel = new RecyclerItemModel();
        itemModel.setTitle("Mint (General News)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
//====================================Indian English Breaking News method staying in above========================================






    @Override
    protected void onCleared() {
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            bdBreakingLiveData.removeObserver(bangladeshiAllBreakingNewsObserver);
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            indianBanglaBreakingLiveData.removeObserver(indianBanglaAllBreakingNewsObserver);
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            indianHindiBreakingLiveData.removeObserver(indianHindiAllBreakingNewsObserver);
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            indianEnglishBreakingLiveData.removeObserver(indianEnglishAllBreakingNewsObserver);
        }
        super.onCleared();
        compositeDisposable.dispose();
    }


}
