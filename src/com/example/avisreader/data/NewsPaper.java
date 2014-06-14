package com.example.avisreader.data;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import com.example.avisreader.R;

public class NewsPaper implements Parcelable {

    private String title, url;
    private Drawable icon;
    private boolean isFavorite;

    public NewsPaper(String title, String url, Drawable icon) {
        this.title = title;
        this.url = url;
        this.icon = icon;
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

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
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
    public static final Parcelable.Creator<NewsPaper> CREATOR = new Parcelable.Creator<NewsPaper>() {
        public NewsPaper createFromParcel(Parcel source) {
            return new NewsPaper(source);
        }

        public NewsPaper[] newArray(int size) {
            return new NewsPaper[size];
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
        dest.writeValue(((BitmapDrawable) icon).getBitmap());
    }

    public NewsPaper(Parcel in) {
        title = in.readString();
        url = in.readString();
        isFavorite = in.readByte() != 0;
        icon = new BitmapDrawable((Bitmap) in.readValue(Bitmap.class.getClassLoader()));
    }
}