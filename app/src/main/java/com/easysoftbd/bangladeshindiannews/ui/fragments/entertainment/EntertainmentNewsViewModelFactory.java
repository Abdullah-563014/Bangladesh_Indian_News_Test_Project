package com.easysoftbd.bangladeshindiannews.ui.fragments.entertainment;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.easysoftbd.bangladeshindiannews.data.local.NewsDatabase;
import com.easysoftbd.bangladeshindiannews.ui.fragments.sports.SportNewsFragmentViewModel;

public class EntertainmentNewsViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private NewsDatabase newsDatabase;
    private String countryName, languageName;

    public EntertainmentNewsViewModelFactory(NewsDatabase newsDatabase, String countryName, String languageName) {
        this.newsDatabase = newsDatabase;
        this.countryName = countryName;
        this.languageName = languageName;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new EntertainmentFragmentViewModel(newsDatabase,countryName,languageName);
    }
}
