package com.easysoftbd.bangladeshindiannews.data.model;

public class NewsAndLinkModel {

    private String news;
    private String link;

    public NewsAndLinkModel(String news, String link) {
        this.news = news;
        this.link = link;
    }

    public String getNews() {
        return news;
    }

    public void setNews(String news) {
        this.news = news;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
