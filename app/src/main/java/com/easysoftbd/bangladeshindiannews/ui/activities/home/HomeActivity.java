package com.easysoftbd.bangladeshindiannews.ui.activities.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.easysoftbd.bangladeshindiannews.R;
import com.easysoftbd.bangladeshindiannews.databinding.ActivityHomeBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private HomeActivityFragmentStateAdapter activityFragmentStateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        binding.setLifecycleOwner(this);


        initViewPager2();

    }


    private void initViewPager2() {
        activityFragmentStateAdapter = new HomeActivityFragmentStateAdapter(this);
        binding.homeViewPager2.setAdapter(activityFragmentStateAdapter);
        new TabLayoutMediator(binding.homeTabLayout, binding.homeViewPager2,
                (tab, position) -> tab.setText(activityFragmentStateAdapter.getTitle(position))).attach();
    }
}