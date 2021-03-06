package com.easysoftbd.bangladeshindiannews.ui.fragments.international;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.acoder.itemclickable.itemclickablemarqueeview.interfaces.ItemClickListener;
import com.easysoftbd.bangladeshindiannews.R;
import com.easysoftbd.bangladeshindiannews.data.model.NewsAndLinkModel;
import com.easysoftbd.bangladeshindiannews.data.model.RecyclerItemModel;
import com.easysoftbd.bangladeshindiannews.databinding.RecyclerViewModelLayoutBinding;
import com.easysoftbd.bangladeshindiannews.ui.activities.home.HomeActivity;
import com.easysoftbd.bangladeshindiannews.ui.fragments.tv_channel.TvChannelNewsAdapter;

import java.util.ArrayList;
import java.util.List;

public class InternationalFragmentAdapter extends RecyclerView.Adapter<InternationalFragmentAdapter.InternationalNewsViewHolder> implements ItemClickListener {

    private Context context;
    private List<RecyclerItemModel> list;
    private HomeActivity homeActivity;

    public InternationalFragmentAdapter(Context context, List<RecyclerItemModel> list) {
        this.context = context;
        this.list = list;
        if (homeActivity==null) {
            homeActivity= (HomeActivity) context;
        }
    }



    @NonNull
    @Override
    public InternationalNewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerViewModelLayoutBinding binding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.recycler_view_model_layout,parent,false);
        return new InternationalNewsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull InternationalNewsViewHolder holder, int position) {
        if (list.get(position).getNotificationStatus().equalsIgnoreCase("on")) {
            holder.binding.titleTextView.setText(list.get(position).getTitle()+" -> (Notification on)");
            holder.binding.titleTextView.setTextColor(ContextCompat.getColor(context,R.color.colorAccent));
        } else {
            holder.binding.titleTextView.setText(list.get(position).getTitle());
            holder.binding.titleTextView.setTextColor(ContextCompat.getColor(context,getColorCode(list.get(position).getTextColor())));
        }

        if (list.get(position).getNewsAndLinkModelList().size()<=0) {
            NewsAndLinkModel newsAndLinkModel = new NewsAndLinkModel("No data found, need to update.", "https://www.google.com/");
            List<NewsAndLinkModel> newsAndLinkModels=new ArrayList<>();
            newsAndLinkModels.add(newsAndLinkModel);
            newsAndLinkModels.add(newsAndLinkModel);
            newsAndLinkModels.add(newsAndLinkModel);
            list.get(position).setNewsAndLinkModelList(newsAndLinkModels);
        }
        List<String> contents=new ArrayList<>();
        List<NewsAndLinkModel> newsAndLinkModels=list.get(position).getNewsAndLinkModelList();
        for (int i=0; i<newsAndLinkModels.size(); i++) {
            contents.add(newsAndLinkModels.get(i).getNews());
        }
        holder.binding.marqueeView.setContent(contents);

        holder.binding.marqueeView.setTextColor(getColorCode(list.get(position).getTextColor()));
//        holder.binding.titleTextView.setTextColor(ContextCompat.getColor(context,getColorCode(list.get(position).getTextColor())));
        holder.binding.recyclerViewModelRootLayout.setBackgroundColor(ContextCompat.getColor(context,getColorCode(list.get(position).getBackgroundColor())));


        holder.binding.marqueeMoreOptionImageView.setOnClickListener(view -> {
            homeActivity.showInternationalMoreOption(list.get(position).getSerialNumber());
        });
        holder.binding.marqueeView.setOnMarqueeItemClickListener(list.get(position).getTitle(),this);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class InternationalNewsViewHolder extends RecyclerView.ViewHolder{

        private RecyclerViewModelLayoutBinding binding;

        public InternationalNewsViewHolder(@NonNull RecyclerViewModelLayoutBinding binding) {
            super(binding.getRoot());

            this.binding=binding;
        }
    }



    @Override
    public void onMarqueeItemClickListener(String tag) {
        for (int i=0; i<list.size(); i++) {
            if (tag.equalsIgnoreCase(list.get(i).getTitle())) {
                homeActivity.showInternationalAlertDialog(list.get(i).getNewsAndLinkModelList());
            }
        }
    }

    private int getColorCode(String colorName) {
        int color;
        switch (colorName) {
            case "White":
                color= R.color.colorWhite;
                break;

            case "Silver":
                color= R.color.colorSilver;
                break;

            case "Gray":
                color= R.color.colorGray;
                break;

            case "Black":
                color= R.color.colorBlack;
                break;

            case "Red":
                color= R.color.colorRed;
                break;

            case "Maroon":
                color= R.color.colorMaroon;
                break;

            case "Yellow":
                color= R.color.colorYellow;
                break;

            case "Olive":
                color= R.color.colorOlive;
                break;

            case "Lime":
                color= R.color.colorLime;
                break;

            case "Green":
                color= R.color.colorGreen;
                break;

            case "Aqua":
                color= R.color.colorAqua;
                break;

            case "Teal":
                color= R.color.colorTeal;
                break;

            case "Blue":
                color= R.color.colorBlue;
                break;

            case "Navy":
                color= R.color.colorNavy;
                break;

            case "Fuchsia":
                color= R.color.colorFuchsia;
                break;

            case "Purple":
                color= R.color.colorPurple;
                break;

            case "SkyBlue":
                color= R.color.colorSkyBlue;
                break;

            case "TealGreen":
                color= R.color.colorTealGreen;
                break;

            case "TealGreenDark":
                color= R.color.colorTealGreenDark;
                break;

            default:
                color= R.color.colorWhite;
                break;
        }
        return color;
    }


}
