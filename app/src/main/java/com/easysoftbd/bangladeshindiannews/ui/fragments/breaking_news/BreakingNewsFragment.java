package com.easysoftbd.bangladeshindiannews.ui.fragments.breaking_news;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.easysoftbd.bangladeshindiannews.R;
import com.easysoftbd.bangladeshindiannews.adapter.MarqueeItemRecyclerAdapter;
import com.easysoftbd.bangladeshindiannews.data.local.DatabaseClient;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdBreaking;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaBreaking;
import com.easysoftbd.bangladeshindiannews.data.local.india.english.IndianEnglishBreaking;
import com.easysoftbd.bangladeshindiannews.data.local.india.hindi.IndianHindiBreaking;
import com.easysoftbd.bangladeshindiannews.data.model.NewsAndLinkModel;
import com.easysoftbd.bangladeshindiannews.data.model.RecyclerItemModel;
import com.easysoftbd.bangladeshindiannews.databinding.FragmentBreakingNewsBinding;
import com.easysoftbd.bangladeshindiannews.databinding.MarqueeItemRecyclerViewLayoutBinding;
import com.easysoftbd.bangladeshindiannews.ui.activities.my_webview.WebViewActivity;
import com.easysoftbd.bangladeshindiannews.utils.CommonMethods;
import com.easysoftbd.bangladeshindiannews.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BreakingNewsFragment extends Fragment {

    private AlertDialog alertDialog;
    private Intent intent;
    private BreakingNewsFragmentViewModel viewModel;
    private FragmentBreakingNewsBinding binding;
    private BreakingNewsAdapter adapter;
    private LinearLayoutManager linearLayoutManager,marqueeItemRecyclerViewLayoutManager;
    private MarqueeItemRecyclerAdapter marqueeItemRecyclerAdapter;
    private MarqueeItemRecyclerViewLayoutBinding marqueeItemRecyclerViewLayoutBinding;
    private List<RecyclerItemModel> list=new ArrayList<>();
    private String countryName,languageName;

    private List<BdBreaking> bdBreakingUnVisibleList=new ArrayList<>();
    private List<IndianBanglaBreaking> indianBanglaBreakingUnVisibleList=new ArrayList<>();
    private List<IndianHindiBreaking> indianHindiBreakingUnVisibleList=new ArrayList<>();
    private List<IndianEnglishBreaking> indianEnglishBreakingUnVisibleList=new ArrayList<>();


    public BreakingNewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        countryName=CommonMethods.getStringFromSharedPreference(getContext(),Constants.selectedCountryTag,Constants.bangladesh);
        languageName=CommonMethods.getStringFromSharedPreference(getContext(),Constants.selectedLanguageTag,Constants.bangla);
        BreakingNewsViewModelFactory factory=new BreakingNewsViewModelFactory(DatabaseClient.getInstance(getContext().getApplicationContext()).getAppDatabase(),countryName,languageName);
        viewModel = new ViewModelProvider(this,factory).get(BreakingNewsFragmentViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_breaking_news, container, false);
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
        if (countryName==null) {
            countryName=Constants.bangladesh;
            languageName=Constants.bangla;
        }
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            List<String> urlList= new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.bd_breaking_url_list)));
            List<String> nameList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.bd_breaking_news_list)));
            viewModel.checkBangladeshBreakingNewsDataInDb(nameList,urlList);
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            List<String> urlList= new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.indian_bangla_breaking_url_list)));
            List<String> nameList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.indian_bangla_breaking_news_list)));
            viewModel.checkIndianBanglaBreakingNewsDataInDb(nameList,urlList);
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            List<String> urlList= new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.indian_hindi_breaking_url_list)));
            List<String> nameList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.indian_hindi_breaking_news_list)));
            viewModel.checkIndianHindiBreakingNewsDataInDb(nameList,urlList);
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            List<String> urlList= new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.indian_english_breaking_url_list)));
            List<String> nameList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.indian_english_breaking_news_list)));
            viewModel.checkIndianEnglishBreakingNewsDataInDb(nameList,urlList);
        }
    }

    private void initRecyclerView() {
        adapter=new BreakingNewsAdapter(getContext(),list);
        linearLayoutManager=new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.setAdapter(adapter);
        viewModel.getItemList().observe(this, recyclerItemModels -> {
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                viewModel.shortingBdBreakingList(recyclerItemModels);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                viewModel.shortingIndianBanglaBreakingList(recyclerItemModels);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                viewModel.shortingIndianHindiBreakingList(recyclerItemModels);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                viewModel.shortingIndianEnglishBreakingList(recyclerItemModels);
            }
        });

        viewModel.getShortedList().observe(this, recyclerItemModelList -> {
            list.clear();
            list.addAll(recyclerItemModelList);
            adapter.notifyDataSetChanged();
        });
    }

    public void showItemChooseAlertDialog(List<NewsAndLinkModel> list) {
        if (list != null && list.size() > 0) {
            marqueeItemRecyclerViewLayoutBinding=DataBindingUtil.inflate(getLayoutInflater(),R.layout.marquee_item_recycler_view_layout,null,false);
            marqueeItemRecyclerViewLayoutManager=new LinearLayoutManager(getContext());
            marqueeItemRecyclerAdapter=new MarqueeItemRecyclerAdapter(getContext(),list);
            marqueeItemRecyclerViewLayoutBinding.marqueeItemRecyclerView.setLayoutManager(marqueeItemRecyclerViewLayoutManager);
            marqueeItemRecyclerViewLayoutBinding.marqueeItemRecyclerView.setAdapter(marqueeItemRecyclerAdapter);
            AlertDialog.Builder builder=new AlertDialog.Builder(getContext())
                    .setCancelable(true)
                    .setView(marqueeItemRecyclerViewLayoutBinding.getRoot());
            AlertDialog alertDialog=builder.create();
            int width = (int)(getResources().getDisplayMetrics().widthPixels*0.98);
            int height = (int)(getResources().getDisplayMetrics().heightPixels*0.80);
            if (!isRemoving()) {
                alertDialog.show();
                alertDialog.getWindow().setLayout(width,height);
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

                            case 6:
                                viewModel.turnOnNotificationStatus(position);
                                break;

                            case 7:
                                viewModel.turnOffNotificationStatus(position);
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
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            viewModel.getBdBreakingUnVisibleList().observe(this, bdBreakings -> {
                bdBreakingUnVisibleList.clear();
                bdBreakingUnVisibleList.addAll(bdBreakings);
            });
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            viewModel.getIndianBanglaBreakingUnVisibleList().observe(this, indianBanglaBreakings -> {
                indianBanglaBreakingUnVisibleList.clear();
                indianBanglaBreakingUnVisibleList.addAll(indianBanglaBreakings);
            });
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            viewModel.getIndianHindiBreakingUnVisibleList().observe(this, indianHindiBreakings -> {
                indianHindiBreakingUnVisibleList.clear();
                indianHindiBreakingUnVisibleList.addAll(indianHindiBreakings);
            });
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            viewModel.getIndianEnglishBreakingUnVisibleList().observe(this, indianEnglishBreakings -> {
                indianEnglishBreakingUnVisibleList.clear();
                indianEnglishBreakingUnVisibleList.addAll(indianEnglishBreakings);
            });
        }
        viewModel.getItemMovedPosition().observe(this,(position) -> {
            Toast.makeText(getContext(), "Current item moved to position:- "+position, Toast.LENGTH_SHORT).show();
        });
    }

    private void showUnVisibleList() {
        String[] list=null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            list=new String[bdBreakingUnVisibleList.size()];
            for (int i=0; i<bdBreakingUnVisibleList.size(); i++) {
                list[i]=bdBreakingUnVisibleList.get(i).getPaperName();
            }
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            list=new String[indianBanglaBreakingUnVisibleList.size()];
            for (int i=0; i<indianBanglaBreakingUnVisibleList.size(); i++) {
                list[i]=indianBanglaBreakingUnVisibleList.get(i).getPaperName();
            }
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            list=new String[indianHindiBreakingUnVisibleList.size()];
            for (int i=0; i<indianHindiBreakingUnVisibleList.size(); i++) {
                list[i]=indianHindiBreakingUnVisibleList.get(i).getPaperName();
            }
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            list=new String[indianEnglishBreakingUnVisibleList.size()];
            for (int i=0; i<indianEnglishBreakingUnVisibleList.size(); i++) {
                list[i]=indianEnglishBreakingUnVisibleList.get(i).getPaperName();
            }
        }


        String[] finalList=list;
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext())
                .setCancelable(true)
                .setTitle("Please choose an item")
                .setItems(finalList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        viewModel.visibleItem(finalList[i]);
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