package com.easysoftbd.bangladeshindiannews.ui.fragments.finance;

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
import com.easysoftbd.bangladeshindiannews.adapter.MarqueeItemRecyclerAdapter;
import com.easysoftbd.bangladeshindiannews.data.local.DatabaseClient;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdEntertainment;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdFinance;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaFinance;
import com.easysoftbd.bangladeshindiannews.data.local.india.english.IndianEnglishFinance;
import com.easysoftbd.bangladeshindiannews.data.local.india.hindi.IndianHindiFinance;
import com.easysoftbd.bangladeshindiannews.data.model.NewsAndLinkModel;
import com.easysoftbd.bangladeshindiannews.data.model.RecyclerItemModel;
import com.easysoftbd.bangladeshindiannews.databinding.FragmentEntertainmentBinding;
import com.easysoftbd.bangladeshindiannews.databinding.FragmentFinanceBinding;
import com.easysoftbd.bangladeshindiannews.databinding.MarqueeItemRecyclerViewLayoutBinding;
import com.easysoftbd.bangladeshindiannews.ui.activities.my_webview.WebViewActivity;
import com.easysoftbd.bangladeshindiannews.ui.fragments.entertainment.EntertainmentFragmentViewModel;
import com.easysoftbd.bangladeshindiannews.ui.fragments.entertainment.EntertainmentNewsAdapter;
import com.easysoftbd.bangladeshindiannews.ui.fragments.entertainment.EntertainmentNewsViewModelFactory;
import com.easysoftbd.bangladeshindiannews.utils.CommonMethods;
import com.easysoftbd.bangladeshindiannews.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FinanceFragment extends Fragment {

    private AlertDialog alertDialog;
    private Intent intent;
    private FinanceFragmentViewModel viewModel;
    private FragmentFinanceBinding binding;
    private FinanceNewsAdapter adapter;
    private LinearLayoutManager linearLayoutManager,marqueeItemRecyclerViewLayoutManager;
    private MarqueeItemRecyclerAdapter marqueeItemRecyclerAdapter;
    private MarqueeItemRecyclerViewLayoutBinding marqueeItemRecyclerViewLayoutBinding;
    private List<RecyclerItemModel> list=new ArrayList<>();
    private String countryName,languageName;

    private List<BdFinance> bdFinanceUnVisibleList=new ArrayList<>();
    private List<IndianBanglaFinance> indianBanglaFinanceUnVisibleList=new ArrayList<>();
    private List<IndianHindiFinance> indianHindiFinanceUnVisibleList=new ArrayList<>();
    private List<IndianEnglishFinance> indianEnglishFinanceUnVisibleList=new ArrayList<>();


    public FinanceFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        countryName= CommonMethods.getStringFromSharedPreference(getContext(),Constants.selectedCountryTag,Constants.bangladesh);
        languageName=CommonMethods.getStringFromSharedPreference(getContext(),Constants.selectedLanguageTag,Constants.bangla);
        FinanceNewsViewModelFactory factory=new FinanceNewsViewModelFactory(DatabaseClient.getInstance(getContext().getApplicationContext()).getAppDatabase(),countryName,languageName);
        viewModel = new ViewModelProvider(this,factory).get(FinanceFragmentViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_finance, container, false);
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
            List<String> urlList= new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.bd_finance_url_list)));
            List<String> nameList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.bd_finance_news_list)));
            viewModel.checkBangladeshFinanceNewsDataInDb(nameList,urlList);
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            List<String> urlList= new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.indian_bangla_finance_url_list)));
            List<String> nameList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.indian_bangla_finance_news_list)));
            viewModel.checkIndianBanglaFinanceNewsDataInDb(nameList,urlList);
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            List<String> urlList= new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.indian_hindi_finance_url_list)));
            List<String> nameList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.indian_hindi_finance_news_list)));
            viewModel.checkIndianHindiFinanceNewsDataInDb(nameList,urlList);
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            List<String> urlList= new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.indian_english_finance_url_list)));
            List<String> nameList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.indian_english_finance_news_list)));
            viewModel.checkIndianEnglishFinanceNewsDataInDb(nameList,urlList);
        }
    }

    private void initRecyclerView() {
        adapter=new FinanceNewsAdapter(getContext(),list);
        linearLayoutManager=new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.setAdapter(adapter);
        viewModel.getItemList().observe(this, recyclerItemModels -> {
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                viewModel.shortingBdFinanceList(recyclerItemModels);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                viewModel.shortingIndianBanglaFinanceList(recyclerItemModels);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                viewModel.shortingIndianHindiFinanceList(recyclerItemModels);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                viewModel.shortingIndianEnglishFinanceList(recyclerItemModels);
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
            viewModel.getBdFinanceUnVisibleList().observe(this, bdFinance -> {
                bdFinanceUnVisibleList.clear();
                bdFinanceUnVisibleList.addAll(bdFinance);
            });
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            viewModel.getIndianBanglaFinanceUnVisibleList().observe(this, indianBanglaFinance -> {
                indianBanglaFinanceUnVisibleList.clear();
                indianBanglaFinanceUnVisibleList.addAll(indianBanglaFinance);
            });
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            viewModel.getIndianHindiFinanceUnVisibleList().observe(this, indianHindiFinances -> {
                indianHindiFinanceUnVisibleList.clear();
                indianHindiFinanceUnVisibleList.addAll(indianHindiFinances);
            });
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            viewModel.getIndianEnglishFinanceUnVisibleList().observe(this, indianEnglishFinances -> {
                indianEnglishFinanceUnVisibleList.clear();
                indianEnglishFinanceUnVisibleList.addAll(indianEnglishFinances);
            });
        }
        viewModel.getItemMovedPosition().observe(this,(position) -> {
            Toast.makeText(getContext(), "Current item moved to position:- "+position, Toast.LENGTH_SHORT).show();
        });
    }

    private void showUnVisibleList() {

        String[] list=null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            list=new String[bdFinanceUnVisibleList.size()];
            for (int i=0; i<bdFinanceUnVisibleList.size(); i++) {
                list[i]=bdFinanceUnVisibleList.get(i).getPaperName();
            }
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            list=new String[indianBanglaFinanceUnVisibleList.size()];
            for (int i=0; i<indianBanglaFinanceUnVisibleList.size(); i++) {
                list[i]=indianBanglaFinanceUnVisibleList.get(i).getPaperName();
            }
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            list=new String[indianHindiFinanceUnVisibleList.size()];
            for (int i=0; i<indianHindiFinanceUnVisibleList.size(); i++) {
                list[i]=indianHindiFinanceUnVisibleList.get(i).getPaperName();
            }
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            list=new String[indianEnglishFinanceUnVisibleList.size()];
            for (int i=0; i<indianEnglishFinanceUnVisibleList.size(); i++) {
                list[i]=indianEnglishFinanceUnVisibleList.get(i).getPaperName();
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