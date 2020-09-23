package com.easysoftbd.bangladeshindiannews.ui.fragments.breaking_news;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.easysoftbd.bangladeshindiannews.data.local.NewsDatabase;

public class BreakingNewsViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private NewsDatabase newsDatabase;
    private String countryName,languageName;

    public BreakingNewsViewModelFactory(NewsDatabase newsDatabase, String countryName, String languageName) {
        this.newsDatabase = newsDatabase;
        this.countryName=countryName;
        this.languageName=languageName;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new BreakingNewsFragmentViewModel(newsDatabase,countryName,languageName);
    }
}
