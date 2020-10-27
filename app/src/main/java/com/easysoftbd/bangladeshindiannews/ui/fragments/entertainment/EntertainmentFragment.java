package com.easysoftbd.bangladeshindiannews.ui.fragments.entertainment;

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
import com.easysoftbd.bangladeshindiannews.data.local.DatabaseClient;
import com.easysoftbd.bangladeshindiannews.data.local.bangladesh.BdEntertainment;
import com.easysoftbd.bangladeshindiannews.data.local.india.bangla.IndianBanglaEntertainment;
import com.easysoftbd.bangladeshindiannews.data.local.india.english.IndianEnglishEntertainment;
import com.easysoftbd.bangladeshindiannews.data.local.india.hindi.IndianHindiEntertainment;
import com.easysoftbd.bangladeshindiannews.data.model.NewsAndLinkModel;
import com.easysoftbd.bangladeshindiannews.data.model.RecyclerItemModel;
import com.easysoftbd.bangladeshindiannews.databinding.FragmentEntertainmentBinding;
import com.easysoftbd.bangladeshindiannews.ui.activities.my_webview.WebViewActivity;
import com.easysoftbd.bangladeshindiannews.utils.CommonMethods;
import com.easysoftbd.bangladeshindiannews.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class EntertainmentFragment extends Fragment {


    private AlertDialog alertDialog;
    private Intent intent;
    private EntertainmentFragmentViewModel viewModel;
    private FragmentEntertainmentBinding binding;
    private EntertainmentNewsAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private List<RecyclerItemModel> list=new ArrayList<>();
    private String countryName,languageName;

    private List<BdEntertainment> bdEntertainmentUnVisibleList=new ArrayList<>();
    private List<IndianBanglaEntertainment> indianBanglaEntertainmentUnVisibleList=new ArrayList<>();
    private List<IndianHindiEntertainment> indianHindiEntertainmentUnVisibleList=new ArrayList<>();
    private List<IndianEnglishEntertainment> indianEnglishEntertainmentUnVisibleList=new ArrayList<>();


    public EntertainmentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        countryName= CommonMethods.getStringFromSharedPreference(getContext(),Constants.selectedCountryTag,Constants.bangladesh);
        languageName=CommonMethods.getStringFromSharedPreference(getContext(),Constants.selectedLanguageTag,Constants.bangla);
        EntertainmentNewsViewModelFactory factory=new EntertainmentNewsViewModelFactory(DatabaseClient.getInstance(getContext().getApplicationContext()).getAppDatabase(),countryName,languageName);
        viewModel = new ViewModelProvider(this,factory).get(EntertainmentFragmentViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_entertainment, container, false);
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
            List<String> urlList= new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.bd_entertainment_url_list)));
            List<String> nameList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.bd_entertainment_news_list)));
            viewModel.checkBangladeshEntertainmentNewsDataInDb(nameList,urlList);
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            List<String> urlList= new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.indian_bangla_entertainment_url_list)));
            List<String> nameList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.indian_bangla_entertainment_news_list)));
            viewModel.checkIndianBanglaEntertainmentNewsDataInDb(nameList,urlList);
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            List<String> urlList= new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.indian_hindi_entertainment_url_list)));
            List<String> nameList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.indian_hindi_entertainment_news_list)));
            viewModel.checkIndianHindiEntertainmentNewsDataInDb(nameList,urlList);
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            List<String> urlList= new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.indian_english_entertainment_url_list)));
            List<String> nameList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.indian_english_entertainment_news_list)));
            viewModel.checkIndianEnglishEntertainmentNewsDataInDb(nameList,urlList);
        }
    }

    private void initRecyclerView() {
        adapter=new EntertainmentNewsAdapter(getContext(),list);
        linearLayoutManager=new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.setAdapter(adapter);
        viewModel.getItemList().observe(this, recyclerItemModels -> {
            if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
                viewModel.shortingBdEntertainmentList(recyclerItemModels);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
                viewModel.shortingIndianBanglaEntertainmentList(recyclerItemModels);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
                viewModel.shortingIndianHindiEntertainmentList(recyclerItemModels);
            } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
                viewModel.shortingIndianEnglishEntertainmentList(recyclerItemModels);
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
            viewModel.getBdEntertainmentUnVisibleList().observe(this, bdSports -> {
                bdEntertainmentUnVisibleList.clear();
                bdEntertainmentUnVisibleList.addAll(bdSports);
            });
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            viewModel.getIndianBanglaEntertainmentUnVisibleList().observe(this, indianBanglaEntertainments -> {
                indianBanglaEntertainmentUnVisibleList.clear();
                indianBanglaEntertainmentUnVisibleList.addAll(indianBanglaEntertainments);
            });
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            viewModel.getIndianHindiEntertainmentUnVisibleList().observe(this, indianHindiEntertainments -> {
                indianHindiEntertainmentUnVisibleList.clear();
                indianHindiEntertainmentUnVisibleList.addAll(indianHindiEntertainments);
            });
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            viewModel.getIndianEnglishEntertainmentUnVisibleList().observe(this, indianEnglishEntertainments -> {
                indianEnglishEntertainmentUnVisibleList.clear();
                indianEnglishEntertainmentUnVisibleList.addAll(indianEnglishEntertainments);
            });
        }
        viewModel.getItemMovedPosition().observe(this,(position) -> {
            Toast.makeText(getContext(), "Current item moved to position:- "+position, Toast.LENGTH_SHORT).show();
        });
    }

    private void showUnVisibleList() {
        String[] list=null;
        if (countryName.equalsIgnoreCase(Constants.bangladesh)) {
            list=new String[bdEntertainmentUnVisibleList.size()];
            for (int i=0; i<bdEntertainmentUnVisibleList.size(); i++) {
                list[i]=bdEntertainmentUnVisibleList.get(i).getPaperName();
            }
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.bangla)) {
            list=new String[indianBanglaEntertainmentUnVisibleList.size()];
            for (int i=0; i<indianBanglaEntertainmentUnVisibleList.size(); i++) {
                list[i]=indianBanglaEntertainmentUnVisibleList.get(i).getPaperName();
            }
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.hindi)) {
            list=new String[indianHindiEntertainmentUnVisibleList.size()];
            for (int i=0; i<indianHindiEntertainmentUnVisibleList.size(); i++) {
                list[i]=indianHindiEntertainmentUnVisibleList.get(i).getPaperName();
            }
        } else if (countryName.equalsIgnoreCase(Constants.india) && languageName.equalsIgnoreCase(Constants.english)) {
            list=new String[indianEnglishEntertainmentUnVisibleList.size()];
            for (int i=0; i<indianEnglishEntertainmentUnVisibleList.size(); i++) {
                list[i]=indianEnglishEntertainmentUnVisibleList.get(i).getPaperName();
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