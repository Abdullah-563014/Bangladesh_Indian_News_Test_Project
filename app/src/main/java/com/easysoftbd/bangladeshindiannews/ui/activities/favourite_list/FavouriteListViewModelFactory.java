package com.easysoftbd.bangladeshindiannews.ui.activities.favourite_list;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.easysoftbd.bangladeshindiannews.data.local.NewsDatabase;

public class FavouriteListViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private NewsDatabase newsDatabase;

    public FavouriteListViewModelFactory(NewsDatabase newsDatabase) {
        this.newsDatabase = newsDatabase;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new FavouriteListActivityViewModel(newsDatabase);
    }
}
