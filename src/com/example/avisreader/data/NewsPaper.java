package com.example.avisreader.data;

import android.graphics.Bitmap;

public class NewsPaper {

    private String title, url;
    private Bitmap icon;
    private boolean isFavorite;


    public NewsPaper(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
