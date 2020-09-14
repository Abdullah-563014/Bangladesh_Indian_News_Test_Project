package com.easysoftbd.bangladeshindiannews.ui.fragments.sports;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.easysoftbd.bangladeshindiannews.R;
import com.easysoftbd.bangladeshindiannews.data.local.DatabaseClient;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdBreaking;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdSports;
import com.easysoftbd.bangladeshindiannews.data.model.NewsAndLinkModel;
import com.easysoftbd.bangladeshindiannews.data.model.RecyclerItemModel;
import com.easysoftbd.bangladeshindiannews.databinding.FragmentBreakingNewsBinding;
import com.easysoftbd.bangladeshindiannews.databinding.FragmentSportsNewsBinding;
import com.easysoftbd.bangladeshindiannews.ui.activities.my_webview.WebViewActivity;
import com.easysoftbd.bangladeshindiannews.ui.fragments.breaking_news.BreakingNewsAdapter;
import com.easysoftbd.bangladeshindiannews.ui.fragments.breaking_news.BreakingNewsFragmentViewModel;
import com.easysoftbd.bangladeshindiannews.ui.fragments.breaking_news.BreakingNewsViewModelFactory;
import com.easysoftbd.bangladeshindiannews.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SportsNewsFragment extends Fragment {


    private AlertDialog alertDialog;
    private Intent intent;
    private SportNewsFragmentViewModel viewModel;
    private FragmentSportsNewsBinding binding;
    private SportsNewsAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private List<RecyclerItemModel> list=new ArrayList<>();
    private List<BdSports> bdSportsUnVisibleList=new ArrayList<>();


    public SportsNewsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SportsNewsViewModelFactory factory=new SportsNewsViewModelFactory(DatabaseClient.getInstance(getContext().getApplicationContext()).getAppDatabase());
        viewModel = new ViewModelProvider(this,factory).get(SportNewsFragmentViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sports_news, container, false);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



        initAll();

        loadAllUrl();

        initRecyclerView();



    }

    private void loadAllUrl() {
        List<String> urlList= new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.bd_sports_url_list)));
        List<String> nameList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.bd_sports_news_list)));
        viewModel.checkBangladeshSportsNewsDataInDb(nameList,urlList);
    }

    private void initRecyclerView() {
        adapter=new SportsNewsAdapter(getContext(),list);
        linearLayoutManager=new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.setAdapter(adapter);
        viewModel.getItemList().observe(this, recyclerItemModels -> {
            viewModel.shortingBdSportsList(recyclerItemModels);
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
                .setTitle("Current Item Position:- "+(position+1))
                .setItems(items, (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    switch (i) {
                        case 0:
                            viewModel.itemMoveToUp(position);
                            break;

                        case 1:
                            viewModel.itemMoveToDown(position);
                            break;

                        case 2:
                            showUnVisibleList();
                            break;

                        case 3:
                            viewModel.hideItem(position);
                            break;

                        case 4:
                            showColorChooseAlertDialog(position,"background");
                            break;

                        case 5:
                            showColorChooseAlertDialog(position,"text");
                            break;
                    }
                });
        alertDialog = builder.create();
        if (!isRemoving()) {
            alertDialog.show();
        }

    }

    public void showColorChooseAlertDialog(int itemPosition, String colorType) {
        String[] list=getResources().getStringArray(R.array.color_list);
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext())
                .setCancelable(true)
                .setTitle("Please choose a color")
                .setItems(list, (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    if (colorType.equalsIgnoreCase("background")) {
                        viewModel.changeItemBackgroundColor(itemPosition,list[i]);
                    } else {
                        viewModel.changeItemTextColor(itemPosition,list[i]);
                    }
                });
        AlertDialog alertDialog=builder.create();
        if (!isRemoving()) {
            alertDialog.show();
        }
    }

    private void initAll() {
        viewModel.getBdSportsUnVisibleList().observe(this, bdSports -> {
            bdSportsUnVisibleList.clear();
            bdSportsUnVisibleList.addAll(bdSports);
        });
        viewModel.getItemMovedPosition().observe(this,(position) -> {
            Toast.makeText(getContext(), "Current item moved to position:- "+position, Toast.LENGTH_SHORT).show();
        });
    }

    private void showUnVisibleList() {
        String[] list=new String[bdSportsUnVisibleList.size()];
        for (int i=0; i<bdSportsUnVisibleList.size(); i++) {
            list[i]=bdSportsUnVisibleList.get(i).getPaperName();
        }
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext())
                .setCancelable(true)
                .setTitle("Please choose an item")
                .setItems(list, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        viewModel.visibleItem(list[i]);
                    }
                });
        AlertDialog alertDialog=builder.create();
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