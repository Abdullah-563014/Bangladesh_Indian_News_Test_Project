package com.easysoftbd.bangladeshindiannews.data.model;


import java.util.List;

public class RecyclerItemModel {
    private String title;
    private List<NewsAndLinkModel> newsAndLinkModelList;

    public RecyclerItemModel() {
    }

    public RecyclerItemModel(String title, List<NewsAndLinkModel> newsAndLinkModelList) {
        this.title = title;
        this.newsAndLinkModelList = newsAndLinkModelList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<NewsAndLinkModel> getNewsAndLinkModelList() {
        return newsAndLinkModelList;
    }

    public void setNewsAndLinkModelList(List<NewsAndLinkModel> newsAndLinkModelList) {
        this.newsAndLinkModelList = newsAndLinkModelList;
    }
}
