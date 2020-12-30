package com.easysoftbd.bangladeshindiannews.ui.fragments.tv_channel;

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
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdTvChannel;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaTvChannel;
import com.easysoftbd.bangladeshindiannews.data.local.india.english.IndianEnglishTvChannel;
import com.easysoftbd.bangladeshindiannews.data.local.india.hindi.IndianHindiTvChannel;
import com.easysoftbd.bangladeshindiannews.data.model.NewsAndLinkModel;
import com.easysoftbd.bangladeshindiannews.data.model.RecyclerItemModel;
import com.easysoftbd.bangladeshindiannews.databinding.FragmentTvChannelNewsBinding;
import com.easysoftbd.bangladeshindiannews.databinding.MarqueeItemRecyclerViewLayoutBinding;
import com.easysoftbd.bangladeshindiannews.ui.activities.my_webview.WebViewActivity;
import com.easysoftbd.bangladeshindiannews.ui.fragments.finance.FinanceFragmentViewModel;
import com.easysoftbd.bangladeshindiannews.ui.fragments.finance.FinanceNewsAdapter;
import com.easysoftbd.bangladeshindiannews.ui.fragments.finance.FinanceNewsViewModelFactory;
import com.easysoftbd.bangladeshindiannews.utils.CommonMethods;
import com.easysoftbd.bangladeshindiannews.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TvChannelNewsFragment extends Fragment {

    private AlertDialog alertDialog;
    private Intent intent;
    private TvChannelNewsFragmentViewModel viewModel;
    private FragmentTvChannelNewsBinding binding;
    private TvChannelNewsAdapter adapter;
    private LinearLayoutManager linearLayoutManager,marqueeItemRecyclerViewLayoutManager;
    private MarqueeItemRecyclerAdapter marqueeItemRecyclerAdapter;
    private MarqueeItemRecyclerViewLayoutBinding marqueeItemRecyclerViewLayoutBinding;
    private List<RecyclerItemModel> list=new ArrayList<>();
    private String countryName,languageName;

    private List<BdTvChannel> bdTvChannelUnVisibleList=new ArrayList<>();
    private List<IndianBanglaTvChannel> indianBanglaTvChannelUnVisibleList=new ArrayList<>();
    private List<IndianHindiTvChannel> indianHindiTvChannelUnVisibleList=new ArrayList<>();
    private List<IndianEnglishTvChannel> indianEnglishTvChannelUnVisibleList=new ArrayList<>();

    public TvChannelNewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        countryName= CommonMethods.getStringFromSharedPreference(getContext(),Constants.selectedCountryTag,Constants.bangladesh);
        languageName=CommonMethods.getStringFromSharedPreference(getContext(),Constants.selectedLanguageTag,Constants.bangla);
        TvChannelNewsViewModelFactory factory=new TvChannelNewsViewModelFactory(DatabaseClient.getInstance(getContext().getApplicationContext()).getAppDatabase(),countryName,languageName);
        viewModel = new ViewModelProvider(this,factory).get(TvChannelNewsFragmentViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tv_channel_news, container, false);
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
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            List<String> urlList= new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.bd_tv_channel_url_list)));
            List<String> nameList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.bd_tv_channel_news_list)));
            viewModel.checkBangladeshTvChannelNewsDataInDb(nameList,urlList);
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            List<String> urlList= new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.indian_bangla_tv_channel_url_list)));
            List<String> nameList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.indian_bangla_tv_channel_news_list)));
            viewModel.checkIndianBanglaTvChannelNewsDataInDb(nameList,urlList);
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            List<String> urlList= new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.indian_hindi_tv_channel_url_list)));
            List<String> nameList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.indian_hindi_tv_channel_news_list)));
            viewModel.checkIndianHindiTvChannelNewsDataInDb(nameList,urlList);
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            List<String> urlList= new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.indian_english_tv_channel_url_list)));
            List<String> nameList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.indian_english_tv_channel_news_list)));
            viewModel.checkIndianEnglishTvChannelNewsDataInDb(nameList,urlList);
        }
    }

    private void initRecyclerView() {
        adapter=new TvChannelNewsAdapter(getContext(),list);
        linearLayoutManager=new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.setAdapter(adapter);
        viewModel.getItemList().observe(this, recyclerItemModels -> {
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                viewModel.shortingBdTvChannelList(recyclerItemModels);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                viewModel.shortingIndianBanglaTvChannelList(recyclerItemModels);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                viewModel.shortingIndianHindiTvChannelList(recyclerItemModels);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                viewModel.shortingIndianEnglishTvChannelList(recyclerItemModels);
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
            viewModel.getBdTvChannelUnVisibleList().observe(this, tvChannel -> {
                bdTvChannelUnVisibleList.clear();
                bdTvChannelUnVisibleList.addAll(tvChannel);
            });
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            viewModel.getIndianBanglaTvChannelUnVisibleList().observe(this, indianBanglaTvChannels -> {
                indianBanglaTvChannelUnVisibleList.clear();
                indianBanglaTvChannelUnVisibleList.addAll(indianBanglaTvChannels);
            });
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            viewModel.getIndianHindiTvChannelUnVisibleList().observe(this, indianHindiTvChannels -> {
                indianHindiTvChannelUnVisibleList.clear();
                indianHindiTvChannelUnVisibleList.addAll(indianHindiTvChannels);
            });
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            viewModel.getIndianEnglishTvChannelUnVisibleList().observe(this, indianEnglishTvChannels -> {
                indianEnglishTvChannelUnVisibleList.clear();
                indianEnglishTvChannelUnVisibleList.addAll(indianEnglishTvChannels);
            });
        }
        viewModel.getItemMovedPosition().observe(this,(position) -> {
            Toast.makeText(getContext(), "Current item moved to position:- "+position, Toast.LENGTH_SHORT).show();
        });
    }

    private void showUnVisibleList() {
        String[] list=null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            list=new String[bdTvChannelUnVisibleList.size()];
            for (int i=0; i<bdTvChannelUnVisibleList.size(); i++) {
                list[i]=bdTvChannelUnVisibleList.get(i).getPaperName();
            }
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            list=new String[indianBanglaTvChannelUnVisibleList.size()];
            for (int i=0; i<indianBanglaTvChannelUnVisibleList.size(); i++) {
                list[i]=indianBanglaTvChannelUnVisibleList.get(i).getPaperName();
            }
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            list=new String[indianHindiTvChannelUnVisibleList.size()];
            for (int i=0; i<indianHindiTvChannelUnVisibleList.size(); i++) {
                list[i]=indianHindiTvChannelUnVisibleList.get(i).getPaperName();
            }
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            list=new String[indianEnglishTvChannelUnVisibleList.size()];
            for (int i=0; i<indianEnglishTvChannelUnVisibleList.size(); i++) {
                list[i]=indianEnglishTvChannelUnVisibleList.get(i).getPaperName();
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