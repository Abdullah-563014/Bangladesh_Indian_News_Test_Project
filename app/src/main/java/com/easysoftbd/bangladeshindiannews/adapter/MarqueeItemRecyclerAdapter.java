package com.easysoftbd.bangladeshindiannews.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.easysoftbd.bangladeshindiannews.R;
import com.easysoftbd.bangladeshindiannews.data.model.NewsAndLinkModel;
import com.easysoftbd.bangladeshindiannews.databinding.MarqueeItemRecyclerViewModelLayoutBinding;
import com.easysoftbd.bangladeshindiannews.databinding.RecyclerViewModelLayoutBinding;
import com.easysoftbd.bangladeshindiannews.ui.activities.my_webview.WebViewActivity;
import com.easysoftbd.bangladeshindiannews.utils.Constants;

import java.util.List;

public class MarqueeItemRecyclerAdapter extends RecyclerView.Adapter<MarqueeItemRecyclerAdapter.MarqueeItemRecyclerViewHolder> {

    private Context context;
    private List<NewsAndLinkModel> list;

    public MarqueeItemRecyclerAdapter(Context context, List<NewsAndLinkModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MarqueeItemRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MarqueeItemRecyclerViewModelLayoutBinding binding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.marquee_item_recycler_view_model_layout,parent,false);
        return new MarqueeItemRecyclerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MarqueeItemRecyclerViewHolder holder, int position) {
        holder.binding.marqueeItemRecyclerTextView.setText(list.get(position).getNews());
        holder.binding.getRoot().setOnClickListener(view -> {
            openUrl(list.get(position).getLink());
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MarqueeItemRecyclerViewHolder extends RecyclerView.ViewHolder {

        private MarqueeItemRecyclerViewModelLayoutBinding binding;

        public MarqueeItemRecyclerViewHolder(@NonNull MarqueeItemRecyclerViewModelLayoutBinding binding) {
            super(binding.getRoot());

            this.binding=binding;
        }
    }


    private void openUrl(String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(Constants.UrlTag, url);
        context.startActivity(intent);
    }



}
