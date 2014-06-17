package com.example.avisreader.data;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Newspaper implements Parcelable, Comparable {

    private String title, url;
    private int id;
    private String icon;
    private boolean isFavorite;

    // Constructor used when creating new Newspapers from resources
    public Newspaper(String title, String url, String icon) {
        this.title = title;
        this.url = url;
        this.icon = icon;
        Log.d("APP", icon);
    }

    // Constructor used when creating new Newspapers from database
    public Newspaper(int id, String title, String url, boolean isFavorite, String icon) {
        this.title = title;
        this.url = url;
        this.id = id;
        this.isFavorite = isFavorite;
        this.icon = icon;
        Log.d("APP", icon);
    }

    public Newspaper() {}

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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "NewsPaper{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", icon=" + icon +
                ", isFavorite=" + isFavorite +
                '}';
    }

    // Parcelable methods
    public static final Parcelable.Creator<Newspaper> CREATOR = new Parcelable.Creator<Newspaper>() {
        public Newspaper createFromParcel(Parcel source) {
            return new Newspaper(source);
        }

        public Newspaper[] newArray(int size) {
            return new Newspaper[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(url);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
        dest.writeString(icon);
        //dest.writeValue(((BitmapDrawable) icon).getBitmap());
    }

    public Newspaper(Parcel in) {
        title = in.readString();
        url = in.readString();
        isFavorite = in.readByte() != 0;
        icon = in.readString();
        //icon = new BitmapDrawable((Bitmap) in.readValue(Bitmap.class.getClassLoader()));
    }

    // Comparable method

    @Override
    public int compareTo(Object another) {
        Newspaper np = (Newspaper) another;
        return title.compareTo(np.getTitle());
    }
}