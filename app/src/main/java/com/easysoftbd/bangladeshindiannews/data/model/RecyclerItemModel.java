package com.easysoftbd.bangladeshindiannews.data.model;


import java.util.List;

public class RecyclerItemModel {
    private String title;
    private int serialNumber;
    private String backgroundColor;
    private String textColor;
    private List<NewsAndLinkModel> newsAndLinkModelList;

    public RecyclerItemModel() {
    }



    public RecyclerItemModel(String title, int serialNumber, String backgroundColor, String textColor, List<NewsAndLinkModel> newsAndLinkModelList) {
        this.title = title;
        this.serialNumber = serialNumber;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
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

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public List<NewsAndLinkModel> getNewsAndLinkModelList() {
        return newsAndLinkModelList;
    }

    public void setNewsAndLinkModelList(List<NewsAndLinkModel> newsAndLinkModelList) {
        this.newsAndLinkModelList = newsAndLinkModelList;
    }
}
