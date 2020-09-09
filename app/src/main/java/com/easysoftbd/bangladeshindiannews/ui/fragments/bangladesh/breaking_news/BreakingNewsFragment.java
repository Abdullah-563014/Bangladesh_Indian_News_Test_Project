package com.easysoftbd.bangladeshindiannews.ui.fragments.bangladesh.breaking_news;

import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easysoftbd.bangladeshindiannews.R;
import com.easysoftbd.bangladeshindiannews.data.local.DatabaseClient;
import com.easysoftbd.bangladeshindiannews.data.model.NewsAndLinkModel;
import com.easysoftbd.bangladeshindiannews.data.model.RecyclerItemModel;
import com.easysoftbd.bangladeshindiannews.databinding.FragmentBreakingNewsBinding;
import com.easysoftbd.bangladeshindiannews.ui.activities.my_webview.WebViewActivity;
import com.easysoftbd.bangladeshindiannews.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BreakingNewsFragment extends Fragment {

    private AlertDialog alertDialog;
    private Intent intent;
    private BreakingNewsFragmentViewModel viewModel;
    private FragmentBreakingNewsBinding binding;
    private List<NewsAndLinkModel> prothomAloBreakingNewsList = new ArrayList<>();
    private BreakingNewsAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private List<RecyclerItemModel> list=new ArrayList<>();


    public BreakingNewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BreakingNewsViewModelFactory factory=new BreakingNewsViewModelFactory(DatabaseClient.getInstance(getContext().getApplicationContext()).getAppDatabase());
        viewModel = new ViewModelProvider(this,factory).get(BreakingNewsFragmentViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_breaking_news, container, false);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



        loadAllUrl();

        initRecyclerView();

    }

    private void loadAllUrl() {
        List<String> urlList= new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.bd_breaking_url_list)));
        List<String> nameList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.bd_breaking_news_list)));
        viewModel.checkBangladeshBreakingNewsDataInDb(nameList,urlList);
    }

    private void initRecyclerView() {
        adapter=new BreakingNewsAdapter(getContext(),list);
        linearLayoutManager=new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.setAdapter(adapter);
        viewModel.getItemList().observe(this, recyclerItemModels -> {
            viewModel.shortingList(recyclerItemModels);
            for (int i=0; i<recyclerItemModels.size();i++) {
                Log.d(Constants.TAG,"Item serial:- "+recyclerItemModels.get(i).getSerialNumber());
            }
        });

        viewModel.getShortedList().observe(this, recyclerItemModelList -> {
            list.clear();
            list.addAll(recyclerItemModelList);
            adapter.notifyDataSetChanged();
        });
    }

    private void openUrl(String url) {
        intent = new Intent(getContext(), WebViewActivity.class);
        intent.putExtra(Constants.UrlTag, url);
        startActivity(intent);
    }

    public void showItemChooseAlertDialog(List<NewsAndLinkModel> list) {
        if (list != null && list.size() > 0) {
            String[] items = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                items[i] = list.get(i).getNews();
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                    .setTitle("Select an Item.")
                    .setSingleChoiceItems(items, 0, (DialogInterface.OnClickListener) (dialog, which) -> {
                        openUrl(list.get(which).getLink());
                        dialog.dismiss();
                    });
            alertDialog = builder.create();
            if (!isRemoving()) {
                alertDialog.show();
            }
        }
    }

    public void showMoreOptionAlertDialog(int position) {
        String[] items=getResources().getStringArray(R.array.more_option_item_list);
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                    .setTitle("Select an Item.")
                    .setItems(items, (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        switch (i) {
                            case 0:
                                viewModel.increaseSerialNumber(position);
                                break;

                            case 1:
                                viewModel.decreaseSerialNumber(position);
                                break;

                            case 2:
                                break;

                            case 3:
                                break;
                        }
                    });
            alertDialog = builder.create();
            if (!isRemoving()) {
                alertDialog.show();
            }

    }



    @Override
    public void onDestroyView() {
        binding.unbind();
        super.onDestroyView();
    }
}