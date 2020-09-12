package com.easysoftbd.bangladeshindiannews.ui.activities.favourite_list;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.easysoftbd.bangladeshindiannews.R;
import com.easysoftbd.bangladeshindiannews.data.local.favourite_list.FavouriteList;
import com.easysoftbd.bangladeshindiannews.databinding.FavouriteListCustomLayoutBinding;
import com.easysoftbd.bangladeshindiannews.ui.activities.my_webview.WebViewActivity;

import java.util.List;

public class FavouriteListRecyclerAdapter extends RecyclerView.Adapter<FavouriteListRecyclerAdapter.MyViewHolder> {

    private Context context;
    private List<FavouriteList> list;
    private OnFavouriteItemClickListener onFavouriteItemClickListener;

    public FavouriteListRecyclerAdapter(Context context, List<FavouriteList> list, OnFavouriteItemClickListener onFavouriteItemClickListener) {
        this.context = context;
        this.list = list;
        this.onFavouriteItemClickListener = onFavouriteItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FavouriteListCustomLayoutBinding binding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.favourite_list_custom_layout,parent,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.favouriteListTitleTextView.setText(list.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private FavouriteListCustomLayoutBinding binding;

        public MyViewHolder(@NonNull FavouriteListCustomLayoutBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
            binding.favouriteListRootLayout.setOnClickListener(this);
            binding.favouriteListDeleteImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            switch (view.getId()){
                case R.id.favouriteListRootLayout:
                    onFavouriteItemClickListener.onRootLayoutClicked(list.get(getAdapterPosition()).getUrl());
                    break;

                case R.id.favouriteListDeleteImageView:
                    onFavouriteItemClickListener.onDeleteClicked(getAdapterPosition());
                    break;
            }
        }
    }


}

