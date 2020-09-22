package com.easysoftbd.bangladeshindiannews.ui.fragments.breaking_news;



import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.easysoftbd.bangladeshindiannews.data.local.NewsDatabase;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdBreaking;
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
    private List<RecyclerItemModel> temporaryList=new ArrayList<>();
    private List<RecyclerItemModel> temporaryShortingList=new ArrayList<>();
    private boolean insertingDataFlag=false;
    private boolean dataStatusFlagInDb=false;
    private Observer<List<BdBreaking>> bangladeshiAllBreakingNewsObserver;
    private LiveData<List<BdBreaking>> bdBreakingLiveData;
    private MutableLiveData<List<BdBreaking>> bdBreakingUnVisibleList;
    private List<BdBreaking> bdBreakingList=new ArrayList<>();
    private List<BdBreaking> bdBreakingUnVisibleTemporaryList=new ArrayList<>();




    public BreakingNewsFragmentViewModel(NewsDatabase newsDatabase) {
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
                        if (document.baseUri().equalsIgnoreCase(MyUrl.bdProtidin)){
                            setBdProtidinBreekingNews(document);
                        }else if (document.baseUri().equalsIgnoreCase(MyUrl.prothomAlo)){
                            setProthomAloBreekingNews(document);
                        }else if (document.baseUri().equalsIgnoreCase(MyUrl.kalerKhanto)){
                            setKalerKhantoBreekingNews(document);
                        }else if (document.baseUri().equalsIgnoreCase(MyUrl.somokal)){
                            setSomokalBreekingNews(document);
                        }else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyJonoKhanto)){
                            setDailyJanaKhantoBreekingNews(document);
                        }else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyInqilab)){
                            setDailyInqilabBreekingNews(document);
                        }else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyNayaDiganta)){
                            setDailyNayaDigantaBreekingNews(document);
                        }else if (document.baseUri().equalsIgnoreCase(MyUrl.amarDesh24)){
                            setAmarDesh24BreekingNews(document);
                        }else if (document.baseUri().equalsIgnoreCase(MyUrl.dailyIttefaq)){
                            setDailyIttefaqBreekingNews(document);
                        }else if (document.baseUri().equalsIgnoreCase(MyUrl.songbadProtidin)){
                            setSongbadProtidinBreekingNews(document);
                        }else if (document.baseUri().equalsIgnoreCase(MyUrl.manobKantha)){
                            setManobKanthaBreekingNews(document);
                        }else if (document.baseUri().equalsIgnoreCase(MyUrl.bangladeshJournal)){
                            setBangladeshJournalBreekingNews(document);
                        }else if (document.baseUri().equalsIgnoreCase(MyUrl.theDailyVorerPata)){
                            setTheDailyVorerPataBreekingNews(document);
                        }
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


    public void shortingBdBreakingList(List<RecyclerItemModel> recyclerItemModelList) {
        if (shortedList==null) {
            shortedList=new MutableLiveData<>();
        }
        temporaryShortingList.clear();
        String title;
        RecyclerItemModel recyclerItemModel;
        for (int i=0; i<bdBreakingList.size(); i++) {
            title=bdBreakingList.get(i).getPaperName();
            for (int j=0; j<recyclerItemModelList.size(); j++) {
                if (title.equalsIgnoreCase(recyclerItemModelList.get(j).getTitle())) {
                    recyclerItemModel=recyclerItemModelList.get(j);
                    recyclerItemModel.setSerialNumber(bdBreakingList.get(i).getSerial());
                    recyclerItemModel.setBackgroundColor(bdBreakingList.get(i).getBackgroundColor());
                    recyclerItemModel.setTextColor(bdBreakingList.get(i).getTextColor());
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

    public LiveData<List<BdBreaking>> getBdBreakingUnVisibleList() {
        if (bdBreakingUnVisibleList==null) {
            bdBreakingUnVisibleList=new MutableLiveData<>();
        }
        return bdBreakingUnVisibleList;
    }

    public LiveData<Integer> getItemMovedPosition() {
        if (itemMovePosition==null) {
            itemMovePosition=new MutableLiveData<>();
        }
        return itemMovePosition;
    }

    public void itemMoveToUp(int serialNumber) {
        if (serialNumber>0) {
            BdBreaking currentItem=bdBreakingList.get(serialNumber);
            BdBreaking upperItem=bdBreakingList.get(serialNumber-1);

            currentItem.setSerial(serialNumber-1);
            upperItem.setSerial(serialNumber);
            insertingDataFlag=true;
            dataStatusFlagInDb=true;



            Completable.fromAction(()->{
                newsDatabase.bdBreakingDao().updateNews(currentItem,upperItem);
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
        if (serialNumber<(bdBreakingList.size()-1)) {
            BdBreaking currentItem=bdBreakingList.get(serialNumber);
            BdBreaking downItem=bdBreakingList.get(serialNumber+1);

            currentItem.setSerial(serialNumber+1);
            downItem.setSerial(serialNumber);
            insertingDataFlag=true;
            dataStatusFlagInDb=true;



            Completable.fromAction(()->{
                newsDatabase.bdBreakingDao().updateNews(currentItem,downItem);
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
        if (serialNumber<=(bdBreakingList.size()-1) && serialNumber>=0) {
            BdBreaking currentItem=bdBreakingList.get(serialNumber);

            currentItem.setVisibilityStatus("hidden");
            insertingDataFlag=false;
            dataStatusFlagInDb=true;



            Completable.fromAction(()->{
                newsDatabase.bdBreakingDao().updateNews(currentItem);
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
        for (int i=0; i<bdBreakingUnVisibleTemporaryList.size(); i++) {
            if (paperName.equalsIgnoreCase(bdBreakingUnVisibleTemporaryList.get(i).getPaperName())) {
                BdBreaking currentItem=bdBreakingUnVisibleTemporaryList.get(i);

                currentItem.setVisibilityStatus("visible");
                insertingDataFlag=false;
                dataStatusFlagInDb=true;



                Completable.fromAction(()->{
                    newsDatabase.bdBreakingDao().updateNews(currentItem);
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
        BdBreaking currentItem=bdBreakingList.get(serialNumber);

        currentItem.setBackgroundColor(colorName);
        insertingDataFlag=false;
        dataStatusFlagInDb=true;



        Completable.fromAction(()->{
            newsDatabase.bdBreakingDao().updateNews(currentItem);
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
        BdBreaking currentItem=bdBreakingList.get(serialNumber);

        currentItem.setTextColor(colorName);
        insertingDataFlag=false;
        dataStatusFlagInDb=true;



        Completable.fromAction(()->{
            newsDatabase.bdBreakingDao().updateNews(currentItem);
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




    public void checkBangladeshBreakingNewsDataInDb(List<String> nameList, List<String> urlList) {
        if (bangladeshiAllBreakingNewsObserver==null) {
            bangladeshiAllBreakingNewsObserver= bdBreakings -> {
                bdBreakingList.clear();
                bdBreakingList.addAll(bdBreakings);
                bdBreakingUnVisibleTemporaryList.clear();
                if (dataStatusFlagInDb && itemList.getValue()!=null && itemList.getValue().size()>0) {
                    itemList.setValue(itemList.getValue());
                }
                if (bdBreakings.size()>0 && !insertingDataFlag) {
                    temporaryList.clear();
                    itemList.setValue(temporaryList);
                    for (int i=0; i<bdBreakings.size(); i++) {
                        if (bdBreakings.get(i).getVisibilityStatus().equalsIgnoreCase("visible")) {
                            loadPageDocument(bdBreakings.get(i).getPaperUrl());
                        } else {
                            bdBreakingUnVisibleTemporaryList.add(bdBreakings.get(i));
                        }
                    }
                    if (bdBreakingUnVisibleList==null) {
                        bdBreakingUnVisibleList=new MutableLiveData<>();
                    }
                    bdBreakingUnVisibleList.setValue(bdBreakingUnVisibleTemporaryList);
                    insertingDataFlag=true;
                } else {
                    insertingDataFlag=true;
                    if (nameList!=null && urlList!=null && !dataStatusFlagInDb) {
                        for (int i=0; i<urlList.size(); i++) {
                            BdBreaking bdBreaking=new BdBreaking();
                            bdBreaking.setSerial(i);
                            bdBreaking.setVisibilityStatus("visible");
                            bdBreaking.setPaperUrl(urlList.get(i));
                            bdBreaking.setPaperName(nameList.get(i));
                            bdBreaking.setBackgroundColor("SkyBlue");
                            bdBreaking.setTextColor("White");
                            Completable.fromAction(()->{
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
                        dataStatusFlagInDb=true;
                    }
                    insertingDataFlag=false;
                }
            };
        }
        bdBreakingLiveData=newsDatabase.bdBreakingDao().getAllNews();
        bdBreakingLiveData.observeForever(bangladeshiAllBreakingNewsObserver);
    }






    private void setProthomAloBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList = document.select("div.custom-story-card-4-data a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.prothomAlo);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("প্রথম আলো (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setBdProtidinBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList=document.select(".container > .jctkr-scroll > .js-conveyor-example ul li");
            for (int i=0; i<allList.size(); i++) {
                String temporaryLink=allList.get(i).select("a").attr("href");
                String news=allList.get(i).select("a").text();
                String link=MyUrl.bdProtidin+temporaryLink;
                NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(news,link);
                list.add(newsAndLinkModel);
            }
        }catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.bdProtidin);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("বাংলাদেশ প্রতিদিন (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setKalerKhantoBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList=document.select("ul.content.mCustomScrollbar li");
            for (int i=0; i<allList.size(); i++) {
                String temporaryLink=allList.get(i).select("a").attr("href");
                String news=allList.get(i).select("a").text();
                String link=MyUrl.kalerKhanto+temporaryLink;
                if (news.length()>=15){
                    NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(news,link);
                    list.add(newsAndLinkModel);
                }
            }
        }catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.kalerKhanto);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG,"error is "+e.getMessage());
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("কালের কণ্ঠ (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setSomokalBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList=document.select("div.bn-title+ul li a[href]");
            for (int i=0; i<allList.size(); i++) {
                String link=allList.get(i).attr("href");
                String news=allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(news,link);
                list.add(newsAndLinkModel);
            }
        }catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.somokal);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG,"error is "+e.getMessage());
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("সমকাল (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setDailyJanaKhantoBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList=document.select("marquee a");
            for (int i=0; i<allList.size(); i++) {
                String temporaryLink=allList.get(i).attr("href");
                String news=allList.get(i).select("a").text();
                String link=MyUrl.dailyJonoKhanto+temporaryLink;
                NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(news,link);
                list.add(newsAndLinkModel);
            }
        }catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.somokal);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG,"error is "+e.getMessage());
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("দৈনিক জনকন্ঠ (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setDailyInqilabBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList=document.select("marquee ul li a");
            for (int i=0; i<allList.size(); i++) {
                String link=allList.get(i).attr("href");
                String news=allList.get(i).select("a").text();
                NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(news,link);
                list.add(newsAndLinkModel);
            }
        }catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.dailyInqilab);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG,"error is "+e.getMessage());
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("দৈনিক ইনকিলাব (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setDailyNayaDigantaBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList=document.select("div.col-lg-7.col-md-5.col-sm-5.column-no-left-padding a");
            for (int i=0; i<allList.size(); i++) {
                String link=allList.get(i).attr("href");
                String news=allList.get(i).select("a").text();
                NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(news,link);
                list.add(newsAndLinkModel);
            }
        }catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.dailyNayaDiganta);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG,"error is "+e.getMessage());
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("নয়া দিগন্ত (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setAmarDesh24BreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList=document.select("div.panel-body div.focus-item.clearfix h2 a");
            for (int i=0; i<allList.size(); i++) {
                String link=allList.get(i).attr("href");
                String news=allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(news,link);
                list.add(newsAndLinkModel);
            }
        }catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.amarDesh24);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG,"error is "+e.getMessage());
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("আমার দেশ 24 (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setDailyIttefaqBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList=document.select("a[href]");
            for (int i=0; i<allList.size(); i++) {
                String link=allList.get(i).attr("href");
                String news=allList.get(i).text();
                if (news.length()>=20){
                    NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(news,link);
                    list.add(newsAndLinkModel);
                }
            }
        }catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.dailyIttefaq);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG,"error is "+e.getMessage());
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("দৈনিক ইত্তেফাক (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setSongbadProtidinBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList=document.select("div.bn-news ul li a");
            for (int i=0; i<allList.size(); i++) {
                String link=allList.get(i).attr("href");
                String news=allList.get(i).text();
                if (news.length()>=20){
                    NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(news,link);
                    list.add(newsAndLinkModel);
                }
            }
        }catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.songbadProtidin);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG,"error is "+e.getMessage());
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("সংবাদ প্রতিদিন (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setManobKanthaBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList=document.select("div.ticker-container ul div li a[href]");
            for (int i=0; i<allList.size(); i++) {
                String link=allList.get(i).attr("href");
                String news=allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(news,link);
                list.add(newsAndLinkModel);
            }
        }catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.manobKantha);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG,"error is "+e.getMessage());
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("মানবকণ্ঠ (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setBangladeshJournalBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList=document.select("marquee ul li a");
            for (int i=0; i<allList.size(); i++) {
                String link=allList.get(i).attr("href");
                String news=allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(news,link);
                list.add(newsAndLinkModel);
            }
        }catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.bangladeshJournal);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG,"error is "+e.getMessage());
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("বাংলাদেশ জার্নাল (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setTheDailyVorerPataBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList=document.select("ul li[data-category=শিরোনাম] a");
            for (int i=0; i<allList.size(); i++) {
                String temporaryLink=allList.get(i).attr("href");
                String news=allList.get(i).text();
                String link=MyUrl.theDailyVorerPata+temporaryLink;
                NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(news,link);
                list.add(newsAndLinkModel);
            }
        }catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.theDailyVorerPata);
            list.add(newsAndLinkModel);
            Log.d(Constants.TAG,"error is "+e.getMessage());
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("ভোরের পাতা (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);
        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
//====================================Bangladesh Breaking News method staying in above========================================


    private void setAnandaBazarBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList = document.select("div.abp-atf-left-story-block a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.anandaBazarBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("আনন্দবাজার পত্রিকা (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setSangbadPratidinBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList = document.select("ul.clearfix.tatka_update_list li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                if (news.length()>=15) {
                    NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                    list.add(newsAndLinkModel);
                }
            }
        } catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.sangbadPratidinBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("সংবাদ প্রতিদিন (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setBartamanPatrikaBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList = document.select("h5 center a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link="https://bartamanpatrika.com/"+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.bartamanPatrikaBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("বর্তমান (সর্বশেষ নিউজ)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setGanaShaktiBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList = document.select("div.img-text-all a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).select("p").text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.ganashaktiBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("গণশক্তি (আজকের খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setUttarBangaSambadBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList = document.select("h3.entry-title.td-module-title a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.uttarBangaSambadBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("উত্তরবঙ্গ সংবাদ (উত্তরবঙ্গ)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setEbelaBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList = document.select("a.Readerchoice[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.ebelaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.ebelaBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("এবেলা (সর্বশেষ খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setAsomiyaPratidinBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList = document.select("marquee a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.asomiyaPratidinBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("অসমীয়া প্রতিদিন (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setAajKaalBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList = document.select("h6 a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.aajkaalBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("আজকাল (আকর্ষনীয় খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setKhaborOnlineBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList = document.select("div.mvp-feat5-side-list.left.relative a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).select("h2").text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.khaborOnlinBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("খবর অনলাইন (নজরে)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setJugaShankaBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList = document.select("h3.post-title a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.jugasankhaBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("যুগশঙ্ক (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setJagaranTripuraBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList = document.select("ul.newsticker li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.jagarantripuraBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("জাগরণত্রিপুরা (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setGanadabiBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList = document.select("div#breaking-news ul li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.ganadabiBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("গণদাবী (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setOneIndiaBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList = document.select("ul#hp-top-news-left li a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String temporaryLink = allList.get(i).attr("href");
                String link=MyUrl.oneIndiaBanglaBreakingNews+temporaryLink;
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.oneIndiaBanglaBreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("ওয়ান ইন্ডিয়া (সাধারণ খবর)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setKolkata247BreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList = document.select("div.td-trending-now-display-area h3.entry-title.td-module-title a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.kolkata247BreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("কলকাতা ২৪*৭ (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
    private void setKhabor24GhontaBreekingNews(Document document) {
        List<NewsAndLinkModel> list=new ArrayList<>();
        try {
            Elements allList = document.select("div.exclusive-slides a[href]");
            for (int i = 0; i < allList.size(); i++) {
                String link = allList.get(i).attr("href");
                String news = allList.get(i).text();
                NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel(news, link);
                list.add(newsAndLinkModel);
            }
        } catch (Exception e){
            NewsAndLinkModel newsAndLinkModel=new NewsAndLinkModel(e.getMessage(),MyUrl.khabor24BreakingNews);
            list.add(newsAndLinkModel);
        }
        RecyclerItemModel itemModel=new RecyclerItemModel();
        itemModel.setTitle("খবর ২৪ ঘন্টা (ব্রেকিং নিউজ)");
        itemModel.setNewsAndLinkModelList(list);

        temporaryList.add(itemModel);
        itemList.setValue(temporaryList);
    }
//====================================Indian Bangla Breaking News method staying in above========================================








    @Override
    protected void onCleared() {
        bdBreakingLiveData.removeObserver(bangladeshiAllBreakingNewsObserver);
        super.onCleared();
        compositeDisposable.dispose();
    }



}
