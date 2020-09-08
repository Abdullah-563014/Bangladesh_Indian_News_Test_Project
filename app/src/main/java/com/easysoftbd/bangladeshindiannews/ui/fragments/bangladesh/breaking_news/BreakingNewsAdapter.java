package com.easysoftbd.bangladeshindiannews.ui.fragments.bangladesh.breaking_news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.acoder.itemclickable.itemclickablemarqueeview.ItemClickAbleMarqueeView;
import com.easysoftbd.bangladeshindiannews.R;
import com.easysoftbd.bangladeshindiannews.data.model.NewsAndLinkModel;
import com.easysoftbd.bangladeshindiannews.data.model.RecyclerItemModel;
import com.easysoftbd.bangladeshindiannews.databinding.RecyclerViewModelLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class BreakingNewsAdapter extends RecyclerView.Adapter<BreakingNewsAdapter.BreakingViewHolder> {

    private Context context;
    private List<RecyclerItemModel> list;

    public BreakingNewsAdapter(Context context, List<RecyclerItemModel> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public BreakingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerViewModelLayoutBinding binding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.recycler_view_model_layout,parent,false);
        return new BreakingViewHolder(binding);
    }




    @Override
    public void onBindViewHolder(@NonNull BreakingViewHolder holder, int position) {
        holder.binding.titleTextView.setText(list.get(position).getTitle());
        List<String> contents=new ArrayList<>();
        List<NewsAndLinkModel> newsAndLinkModels=list.get(position).getNewsAndLinkModelList();
        for (int i=0; i<newsAndLinkModels.size(); i++) {
            contents.add(newsAndLinkModels.get(i).getNews());
        }
        holder.binding.marqueeView.setContent(contents);
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    static class BreakingViewHolder extends RecyclerView.ViewHolder{

        private RecyclerViewModelLayoutBinding binding;

        public BreakingViewHolder(@NonNull RecyclerViewModelLayoutBinding binding) {
            super(binding.getRoot());

            this.binding=binding;
        }
    }
}
