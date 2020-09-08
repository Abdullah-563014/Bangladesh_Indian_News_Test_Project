package com.easysoftbd.bangladeshindiannews.ui.activities.home;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private HomeActivityFragmentStateAdapter adapter;

    public HomeViewModel(HomeActivityFragmentStateAdapter adapter) {
        this.adapter=adapter;
    }

    private MutableLiveData<String> tabTitle;


}
