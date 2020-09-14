package com.easysoftbd.bangladeshindiannews.ui.fragments.sports;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.easysoftbd.bangladeshindiannews.data.local.NewsDatabase;

public class SportsNewsViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private NewsDatabase newsDatabase;

    public SportsNewsViewModelFactory(NewsDatabase newsDatabase) {
        this.newsDatabase = newsDatabase;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new SportNewsFragmentViewModel(newsDatabase);
    }
}