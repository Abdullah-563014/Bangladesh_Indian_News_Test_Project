package com.easysoftbd.bangladeshindiannews.ui.fragments.sports;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easysoftbd.bangladeshindiannews.R;



public class SportsNewsFragment extends Fragment {




    public SportsNewsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sports_news, container, false);
    }
}