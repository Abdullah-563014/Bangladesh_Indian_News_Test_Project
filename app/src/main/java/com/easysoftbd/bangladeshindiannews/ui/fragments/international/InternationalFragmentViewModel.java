package com.easysoftbd.bangladeshindiannews.ui.fragments.international;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.easysoftbd.bangladeshindiannews.data.local.NewsDatabase;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdInternational;
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
    private Observer<List<BdInternational>> bangladeshiAllInternationalNewsObserver;
    private LiveData<List<BdInternational>> bdInternationalLiveData;
    private MutableLiveData<List<BdInternational>> bdInternationalUnVisibleList;
    private List<BdInternational> bdInternationalList=new ArrayList<>();
    private List<BdInternational> bdInternationalUnVisibleTemporaryList=new ArrayList<>();


    public InternationalFragmentViewModel(NewsDatabase newsDatabase) {
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
                        if (document.baseUri().equalsIgnoreCase(MyUrl.kalerKhanto)){
                            setKalerKanthoInternationalNews(document);
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

    public LiveData<List<BdInternational>> getBdInternationalUnVisibleList() {
        if (bdInternationalUnVisibleList==null) {
            bdInternationalUnVisibleList=new MutableLiveData<>();
        }
        return bdInternationalUnVisibleList;
    }

    public LiveData<Integer> getItemMovedPosition() {
        if (itemMovePosition==null) {
            itemMovePosition=new MutableLiveData<>();
        }
        return itemMovePosition;
    }

    public void itemMoveToUp(int serialNumber) {
        if (serialNumber>0) {
            BdInternational currentItem=bdInternationalList.get(serialNumber);
            BdInternational upperItem=bdInternationalList.get(serialNumber-1);

            currentItem.setSerial(serialNumber-1);
            upperItem.setSerial(serialNumber);
            insertingDataFlag=true;
            dataStatusFlagInDb=true;



            Completable.fromAction(()->{
                newsDatabase.bdInternationalDao().updateNews(currentItem,upperItem);
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
        if (serialNumber<(bdInternationalList.size()-1)) {
            BdInternational currentItem=bdInternationalList.get(serialNumber);
            BdInternational downItem=bdInternationalList.get(serialNumber+1);

            currentItem.setSerial(serialNumber+1);
            downItem.setSerial(serialNumber);
            insertingDataFlag=true;
            dataStatusFlagInDb=true;



            Completable.fromAction(()->{
                newsDatabase.bdInternationalDao().updateNews(currentItem,downItem);
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
        if (serialNumber<=(bdInternationalList.size()-1) && serialNumber>=0) {
            BdInternational currentItem=bdInternationalList.get(serialNumber);

            currentItem.setVisibilityStatus("hidden");
            insertingDataFlag=false;
            dataStatusFlagInDb=true;



            Completable.fromAction(()->{
                newsDatabase.bdInternationalDao().updateNews(currentItem);
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
        for (int i=0; i<bdInternationalUnVisibleTemporaryList.size(); i++) {
            if (paperName.equalsIgnoreCase(bdInternationalUnVisibleTemporaryList.get(i).getPaperName())) {
                BdInternational currentItem=bdInternationalUnVisibleTemporaryList.get(i);

                currentItem.setVisibilityStatus("visible");
                insertingDataFlag=false;
                dataStatusFlagInDb=true;



                Completable.fromAction(()->{
                    newsDatabase.bdInternationalDao().updateNews(currentItem);
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
        BdInternational currentItem=bdInternationalList.get(serialNumber);

        currentItem.setBackgroundColor(colorName);
        insertingDataFlag=false;
        dataStatusFlagInDb=true;



        Completable.fromAction(()->{
            newsDatabase.bdInternationalDao().updateNews(currentItem);
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
        BdInternational currentItem=bdInternationalList.get(serialNumber);

        currentItem.setTextColor(colorName);
        insertingDataFlag=false;
        dataStatusFlagInDb=true;



        Completable.fromAction(()->{
            newsDatabase.bdInternationalDao().updateNews(currentItem);
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



    @Override
    protected void onCleared() {
        bdInternationalLiveData.removeObserver(bangladeshiAllInternationalNewsObserver);
        super.onCleared();
        compositeDisposable.dispose();
    }



}
