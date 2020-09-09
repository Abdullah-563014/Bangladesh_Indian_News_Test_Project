package com.easysoftbd.bangladeshindiannews.data.model;


import java.util.List;

public class RecyclerItemModel {
    private String title;
    private int serialNumber;
    private List<NewsAndLinkModel> newsAndLinkModelList;

    public RecyclerItemModel() {
    }

    public RecyclerItemModel(String title, int serialNumber, List<NewsAndLinkModel> newsAndLinkModelList) {
        this.title = title;
        this.serialNumber = serialNumber;
        this.newsAndLinkModelList = newsAndLinkModelList;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public List<NewsAndLinkModel> getNewsAndLinkModelList() {
        return newsAndLinkModelList;
    }

    public void setNewsAndLinkModelList(List<NewsAndLinkModel> newsAndLinkModelList) {
        this.newsAndLinkModelList = newsAndLinkModelList;
    }
}
