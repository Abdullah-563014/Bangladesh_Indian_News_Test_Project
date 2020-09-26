package com.easysoftbd.bangladeshindiannews.ui.fragments.finance;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.easysoftbd.bangladeshindiannews.data.local.NewsDatabase;

public class FinanceNewsViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private NewsDatabase newsDatabase;
    private String countryName,languageName;

    public FinanceNewsViewModelFactory(NewsDatabase newsDatabase, String countryName, String languageName) {
        this.newsDatabase = newsDatabase;
        this.countryName = countryName;
        this.languageName = languageName;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new FinanceFragmentViewModel(newsDatabase,countryName,languageName);
    }
}
