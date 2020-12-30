package com.easysoftbd.bangladeshindiannews.ui.activities.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.easysoftbd.bangladeshindiannews.R;
import com.easysoftbd.bangladeshindiannews.data.model.NewsAndLinkModel;
import com.easysoftbd.bangladeshindiannews.databinding.ActivityHomeBinding;
import com.easysoftbd.bangladeshindiannews.ui.fragments.breaking_news.BreakingNewsFragment;
import com.easysoftbd.bangladeshindiannews.ui.fragments.entertainment.EntertainmentFragment;
import com.easysoftbd.bangladeshindiannews.ui.fragments.finance.FinanceFragment;
import com.easysoftbd.bangladeshindiannews.ui.fragments.international.InternationalFragment;
import com.easysoftbd.bangladeshindiannews.ui.fragments.sports.SportsNewsFragment;
import com.easysoftbd.bangladeshindiannews.ui.fragments.tv_channel.TvChannelNewsFragment;
import com.easysoftbd.bangladeshindiannews.utils.Constants;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

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

    public void showBreakingAlertDialog(List<NewsAndLinkModel> newsAndLinkModels) {
        BreakingNewsFragment breakingNewsFragment= (BreakingNewsFragment) activityFragmentStateAdapter.createFragment(0);
        breakingNewsFragment.showItemChooseAlertDialog(newsAndLinkModels);
    }

    public void showBreakingMoreOption(int serialNo) {
        BreakingNewsFragment breakingNewsFragment= (BreakingNewsFragment) activityFragmentStateAdapter.createFragment(0);
        breakingNewsFragment.showMoreOptionAlertDialog(serialNo);
    }

    public void showSportsAlertDialog(List<NewsAndLinkModel> newsAndLinkModels) {
        SportsNewsFragment sportsNewsFragment= (SportsNewsFragment) activityFragmentStateAdapter.createFragment(1);
        sportsNewsFragment.showItemChooseAlertDialog(newsAndLinkModels);
    }

    public void showSportsMoreOption(int serialNo) {
        SportsNewsFragment sportsNewsFragment= (SportsNewsFragment) activityFragmentStateAdapter.createFragment(1);
        sportsNewsFragment.showMoreOptionAlertDialog(serialNo);
    }

    public void showEntertainmentAlertDialog(List<NewsAndLinkModel> newsAndLinkModels) {
        EntertainmentFragment entertainmentFragment= (EntertainmentFragment) activityFragmentStateAdapter.createFragment(2);
        entertainmentFragment.showItemChooseAlertDialog(newsAndLinkModels);
    }

    public void showEntertainmentMoreOption(int serialNo) {
        EntertainmentFragment entertainmentFragment= (EntertainmentFragment) activityFragmentStateAdapter.createFragment(2);
        entertainmentFragment.showMoreOptionAlertDialog(serialNo);
    }

    public void showFinanceAlertDialog(List<NewsAndLinkModel> newsAndLinkModels) {
        FinanceFragment financeFragment= (FinanceFragment) activityFragmentStateAdapter.createFragment(3);
        financeFragment.showItemChooseAlertDialog(newsAndLinkModels);
    }

    public void showFinanceMoreOption(int serialNo) {
        FinanceFragment financeFragment= (FinanceFragment) activityFragmentStateAdapter.createFragment(3);
        financeFragment.showMoreOptionAlertDialog(serialNo);
    }

    public void showTvChannelAlertDialog(List<NewsAndLinkModel> newsAndLinkModels) {
        TvChannelNewsFragment tvChannelNewsFragment= (TvChannelNewsFragment) activityFragmentStateAdapter.createFragment(4);
        tvChannelNewsFragment.showItemChooseAlertDialog(newsAndLinkModels);
    }

    public void showTvChannelMoreOption(int serialNo) {
        TvChannelNewsFragment tvChannelNewsFragment= (TvChannelNewsFragment) activityFragmentStateAdapter.createFragment(4);
        tvChannelNewsFragment.showMoreOptionAlertDialog(serialNo);
    }

    public void showInternationalAlertDialog(List<NewsAndLinkModel> newsAndLinkModels) {
        InternationalFragment internationalFragment= (InternationalFragment) activityFragmentStateAdapter.createFragment(5);
        internationalFragment.showItemChooseAlertDialog(newsAndLinkModels);
    }

    public void showInternationalMoreOption(int serialNo) {
        InternationalFragment internationalFragment= (InternationalFragment) activityFragmentStateAdapter.createFragment(5);
        internationalFragment.showMoreOptionAlertDialog(serialNo);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.isUserActive=true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Constants.isUserActive=false;
    }



}