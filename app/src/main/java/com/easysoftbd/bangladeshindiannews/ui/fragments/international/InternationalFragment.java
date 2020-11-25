package com.easysoftbd.bangladeshindiannews.ui.fragments.international;

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
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdInternational;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdTvChannel;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaInternational;
import com.easysoftbd.bangladeshindiannews.data.local.india.english.IndianEnglishInternational;
import com.easysoftbd.bangladeshindiannews.data.local.india.hindi.IndianHindiInternational;
import com.easysoftbd.bangladeshindiannews.data.model.NewsAndLinkModel;
import com.easysoftbd.bangladeshindiannews.data.model.RecyclerItemModel;
import com.easysoftbd.bangladeshindiannews.databinding.FragmentInternationalBinding;
import com.easysoftbd.bangladeshindiannews.databinding.FragmentTvChannelNewsBinding;
import com.easysoftbd.bangladeshindiannews.ui.activities.my_webview.WebViewActivity;
import com.easysoftbd.bangladeshindiannews.ui.fragments.tv_channel.TvChannelNewsAdapter;
import com.easysoftbd.bangladeshindiannews.ui.fragments.tv_channel.TvChannelNewsFragmentViewModel;
import com.easysoftbd.bangladeshindiannews.ui.fragments.tv_channel.TvChannelNewsViewModelFactory;
import com.easysoftbd.bangladeshindiannews.utils.CommonMethods;
import com.easysoftbd.bangladeshindiannews.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class InternationalFragment extends Fragment {


    private AlertDialog alertDialog;
    private Intent intent;
    private InternationalFragmentViewModel viewModel;
    private FragmentInternationalBinding binding;
    private InternationalFragmentAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private List<RecyclerItemModel> list=new ArrayList<>();
    private String countryName,languageName;

    private List<BdInternational> bdInternationalUnVisibleList=new ArrayList<>();
    private List<IndianBanglaInternational> indianBanglaInternationalUnVisibleList=new ArrayList<>();
    private List<IndianHindiInternational> indianHindiInternationalUnVisibleList=new ArrayList<>();
    private List<IndianEnglishInternational> indianEnglishInternationalUnVisibleList=new ArrayList<>();


    public InternationalFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        countryName= CommonMethods.getStringFromSharedPreference(getContext(),Constants.selectedCountryTag,Constants.bangladesh);
        languageName=CommonMethods.getStringFromSharedPreference(getContext(),Constants.selectedLanguageTag,Constants.bangla);
        InternationalFragmentViewModelFactory factory=new InternationalFragmentViewModelFactory(DatabaseClient.getInstance(getContext().getApplicationContext()).getAppDatabase(),countryName,languageName);
        viewModel = new ViewModelProvider(this,factory).get(InternationalFragmentViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_international, container, false);
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
            List<String> urlList= new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.bd_international_url_list)));
            List<String> nameList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.bd_international_news_list)));
            viewModel.checkBangladeshInternationalNewsDataInDb(nameList,urlList);
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            List<String> urlList= new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.indian_bangla_international_url_list)));
            List<String> nameList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.indian_bangla_international_news_list)));
            viewModel.checkIndianBanglaInternationalNewsDataInDb(nameList,urlList);
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            List<String> urlList= new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.indian_hindi_international_url_list)));
            List<String> nameList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.indian_hindi_international_news_list)));
            viewModel.checkIndianHindiInternationalNewsDataInDb(nameList,urlList);
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            List<String> urlList= new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.indian_english_international_url_list)));
            List<String> nameList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.indian_english_international_news_list)));
            viewModel.checkIndianEnglishInternationalNewsDataInDb(nameList,urlList);
        }
    }

    private void initRecyclerView() {
        adapter=new InternationalFragmentAdapter(getContext(),list);
        linearLayoutManager=new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.setAdapter(adapter);
        viewModel.getItemList().observe(this, recyclerItemModels -> {
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                viewModel.shortingBdInternationalList(recyclerItemModels);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                viewModel.shortingIndianBanglaInternationalList(recyclerItemModels);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                viewModel.shortingIndianHindiInternationalList(recyclerItemModels);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                viewModel.shortingIndianEnglishInternationalList(recyclerItemModels);
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
            viewModel.getBdInternationalUnVisibleList().observe(this, internationalNews -> {
                bdInternationalUnVisibleList.clear();
                bdInternationalUnVisibleList.addAll(internationalNews);
            });
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            viewModel.getIndianBanglaInternationalUnVisibleList().observe(this, indianBanglaInternationals -> {
                indianBanglaInternationalUnVisibleList.clear();
                indianBanglaInternationalUnVisibleList.addAll(indianBanglaInternationals);
            });
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            viewModel.getIndianHindiInternationalUnVisibleList().observe(this, indianHindiInternationals -> {
                indianHindiInternationalUnVisibleList.clear();
                indianHindiInternationalUnVisibleList.addAll(indianHindiInternationals);
            });
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            viewModel.getIndianEnglishInternationalUnVisibleList().observe(this, indianEnglishInternationals -> {
                indianEnglishInternationalUnVisibleList.clear();
                indianEnglishInternationalUnVisibleList.addAll(indianEnglishInternationals);
            });
        }
        viewModel.getItemMovedPosition().observe(this,(position) -> {
            Toast.makeText(getContext(), "Current item moved to position:- "+position, Toast.LENGTH_SHORT).show();
        });
    }

    private void showUnVisibleList() {

        String[] list=null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            list=new String[bdInternationalUnVisibleList.size()];
            for (int i=0; i<bdInternationalUnVisibleList.size(); i++) {
                list[i]=bdInternationalUnVisibleList.get(i).getPaperName();
            }
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            list=new String[indianBanglaInternationalUnVisibleList.size()];
            for (int i=0; i<indianBanglaInternationalUnVisibleList.size(); i++) {
                list[i]=indianBanglaInternationalUnVisibleList.get(i).getPaperName();
            }
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            list=new String[indianHindiInternationalUnVisibleList.size()];
            for (int i=0; i<indianHindiInternationalUnVisibleList.size(); i++) {
                list[i]=indianHindiInternationalUnVisibleList.get(i).getPaperName();
            }
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            list=new String[indianEnglishInternationalUnVisibleList.size()];
            for (int i=0; i<indianEnglishInternationalUnVisibleList.size(); i++) {
                list[i]=indianEnglishInternationalUnVisibleList.get(i).getPaperName();
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