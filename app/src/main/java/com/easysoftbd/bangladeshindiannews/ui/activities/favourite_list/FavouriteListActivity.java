package com.easysoftbd.bangladeshindiannews.ui.activities.favourite_list;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.easysoftbd.bangladeshindiannews.R;
import com.easysoftbd.bangladeshindiannews.data.local.DatabaseClient;
import com.easysoftbd.bangladeshindiannews.data.local.favourite_list.FavouriteList;
import com.easysoftbd.bangladeshindiannews.databinding.ActivityFavouriteListBinding;
import com.easysoftbd.bangladeshindiannews.ui.activities.my_webview.WebViewActivity;
import com.easysoftbd.bangladeshindiannews.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class FavouriteListActivity extends AppCompatActivity implements OnFavouriteItemClickListener {


    private FavouriteListActivityViewModel viewModel;
    private ActivityFavouriteListBinding binding;
    private FavouriteListRecyclerAdapter favouriteListRecyclerAdapter;
    private List<FavouriteList> list=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_favourite_list);
        FavouriteListViewModelFactory factory=new FavouriteListViewModelFactory(DatabaseClient.getInstance(getApplicationContext()).getAppDatabase());
        viewModel=new ViewModelProvider(this,factory).get(FavouriteListActivityViewModel.class);
        binding.setLifecycleOwner(this);



        initializeRecyclerView();

    }


    private void initializeRecyclerView() {
        viewModel.searchFavouriteList();
        favouriteListRecyclerAdapter=new FavouriteListRecyclerAdapter(FavouriteListActivity.this,list,this);
        binding.favouriteListRecyclerView.setLayoutManager(new LinearLayoutManager(FavouriteListActivity.this));
        binding.favouriteListRecyclerView.setHasFixedSize(true);
        binding.favouriteListRecyclerView.setAdapter(favouriteListRecyclerAdapter);
        viewModel.getFavouriteList().observe(this, favouriteLists -> {
            list.clear();
            list.addAll(favouriteLists);
            favouriteListRecyclerAdapter.notifyDataSetChanged();
            if (list.size()>0){
                binding.favouriteListActivityNoItemTextView.setVisibility(View.GONE);
            } else {
                binding.favouriteListActivityNoItemTextView.setVisibility(View.VISIBLE);
            }
        });
    }



    @Override
    public void onRootLayoutClicked(String url) {
        Intent intent=new Intent(this, WebViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(Constants.UrlTag,url);
        startActivity(intent);
    }

    @Override
    public void onDeleteClicked(int position) {
        viewModel.removeFavouriteItem(position).observe(this, integer -> {
            Toast.makeText(this, "Successfully item deleted from position "+(position+1), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onDestroy() {
        binding.unbind();
        super.onDestroy();
    }


}