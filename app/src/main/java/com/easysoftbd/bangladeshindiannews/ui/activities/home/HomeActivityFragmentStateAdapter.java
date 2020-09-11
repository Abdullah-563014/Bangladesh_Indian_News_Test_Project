package com.easysoftbd.bangladeshindiannews.ui.activities.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.easysoftbd.bangladeshindiannews.ui.fragments.breaking_news.BreakingNewsFragment;

import java.util.ArrayList;
import java.util.List;

public class HomeActivityFragmentStateAdapter extends FragmentStateAdapter {

    private List<Fragment> fragmentList=new ArrayList<>();
    private List<String> titleList=new ArrayList<>();

    public HomeActivityFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);

        addAllFragmentForTabLayout();
        addAllTitleForTabLayout();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return titleList.size();
    }

    private void addAllFragmentForTabLayout() {
        fragmentList.clear();
        fragmentList.add(new BreakingNewsFragment());
    }

    private void addAllTitleForTabLayout() {
        titleList.clear();
        titleList.add("Breaking News");
    }

    public String getTitle(int position) {
        return titleList.get(position);
    }



}