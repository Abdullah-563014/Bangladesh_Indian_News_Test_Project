package com.easysoftbd.bangladeshindiannews.ui.activities.favourite_list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.easysoftbd.bangladeshindiannews.data.local.NewsDatabase;
import com.easysoftbd.bangladeshindiannews.data.local.favourite_list.FavouriteList;
import com.easysoftbd.bangladeshindiannews.data.repositories.MyResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FavouriteListActivityViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable;
    private NewsDatabase newsDatabase;
    private LiveData<List<FavouriteList>> favouriteListLiveData;
    private MutableLiveData<List<FavouriteList>> favouriteList;
    private MutableLiveData<Integer> removedItemPosition;
    private Observer<List<FavouriteList>> listObserver;
    private List<FavouriteList> allFavouriteList=new ArrayList<>();


    public FavouriteListActivityViewModel(NewsDatabase newsDatabase) {
        this.newsDatabase=newsDatabase;
        if (compositeDisposable==null) {
            compositeDisposable=new CompositeDisposable();
        }
    }

    public MutableLiveData<List<FavouriteList>> getFavouriteList(){
        if (favouriteList==null){
            favouriteList=new MutableLiveData<>();
        }
        return favouriteList;
    }

    public void searchFavouriteList() {
        if (listObserver==null) {
            listObserver= allList->{
                favouriteList.setValue(allList);
                allFavouriteList.clear();
                allFavouriteList.addAll(allList);
            };
        }
        favouriteListLiveData=newsDatabase.favouriteListDao().getAll();
        favouriteListLiveData.observeForever(listObserver);
    }

    public LiveData<Integer> removeFavouriteItem(int position) {
        if (removedItemPosition==null) {
            removedItemPosition=new MutableLiveData<>();
        }
        FavouriteList favouriteList=allFavouriteList.get(position);
        Completable.fromAction(()->{
            newsDatabase.favouriteListDao().delete(favouriteList);
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onComplete() {
                removedItemPosition.setValue(position);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
        return removedItemPosition;
    }




    @Override
    protected void onCleared() {
        favouriteListLiveData.removeObserver(listObserver);
        super.onCleared();
        compositeDisposable.dispose();
    }


}
