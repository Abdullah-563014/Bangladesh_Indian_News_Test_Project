package com.easysoftbd.bangladeshindiannews.ui.fragments.bangladesh.breaking_news;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.easysoftbd.bangladeshindiannews.data.local.NewsDatabase;

public class BreakingNewsViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private NewsDatabase newsDatabase;

    public BreakingNewsViewModelFactory(NewsDatabase newsDatabase) {
        this.newsDatabase = newsDatabase;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new BreakingNewsFragmentViewModel(newsDatabase);
    }
}
