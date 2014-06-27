package com.example.avisreader.data;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import org.apache.commons.lang3.text.WordUtils;


public class Newspaper implements Parcelable, Comparable {

    private String title, url;
    private int id;
    private String icon;
    private boolean isFavorite;
    private Bitmap iconBitmap;


    // Constructor used when creating new Newspapers from resources
    public Newspaper(String title, String url) {
        setTitle(title);
        this.url = url;

        String tempIcon = getUrlDomainName(url);
        if (tempIcon.contains("-")) {
            tempIcon = tempIcon.replace("-", "_");
        }
        this.icon = tempIcon;
    }


    // Constructor used when creating new Newspapers from database
    public Newspaper(int id, String title, String url, boolean isFavorite, String icon, Bitmap ibm) {
        setTitle(title);
        this.url = url;
        this.id = id;
        this.isFavorite = isFavorite;
        this.icon = icon;
        this.iconBitmap = ibm;
    }

    public Newspaper(int id, String title, String url, boolean isFavorite, String icon) {
        setTitle(title);
        this.url = url;
        this.id = id;
        this.isFavorite = isFavorite;
        this.icon = icon;
    }

    public Newspaper() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        // Capitalize first letter in every word/after dash
        if (title.trim().contains("-") && (title.indexOf("-") != 0)) {
            int dashIndex = title.indexOf("-");
            String firstPart = WordUtils.capitalize(title.substring(0, dashIndex));
            String secondPart = WordUtils.capitalize(title.substring(dashIndex, title.length()));
            this.title = firstPart.trim() + secondPart;
            return;
        }

        if (title.trim().toLowerCase().equals("vg") || title.toLowerCase().equals("nrk")) {
            this.title = title.toUpperCase().trim();
            return;
        } else if (title.trim().toLowerCase().equals("itromsø")) {
            this.title = "iTromsø";
            return;
        } else if (title.trim().toLowerCase().equals("gbnett")) {
            this.title = "GBnett";
            return;
        } else {
            this.title = WordUtils.capitalizeFully(title).trim();
        }
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

    public Bitmap getIconBitmap() {
        return iconBitmap;
    }

    public void setIconBitmap(Bitmap iconBitmap) {
        this.iconBitmap = iconBitmap;
    }

    @Override
    public String toString() {
        return "Newspaper{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", id=" + id +
                ", icon='" + icon + '\'' +
                ", isFavorite=" + isFavorite +
                ", iconBitmap=" + iconBitmap +
                '}';
    }

    // Helper methods
    public String getUrlDomainName(String url) throws StringIndexOutOfBoundsException {
        String domainName = new String(url);

        int index = domainName.indexOf("://");

        if (index != -1) {
            // keep everything after the "://"
            domainName = domainName.substring(index + 3);
        }

        index = domainName.indexOf('/');

        if (index != -1) {
            // keep everything before the '/'
            domainName = domainName.substring(0, index);
        }

        // check for and remove a preceding 'www'
        domainName = domainName.replaceFirst("^www.*?\\.", "");
        int indexOfDot = domainName.lastIndexOf(".");
        if(indexOfDot > 0)
            domainName = domainName.substring(0, indexOfDot);
        else
            return "noicon";

        return domainName;
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
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(url);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
        dest.writeString(icon);
        //dest.writeValue(((BitmapDrawable) iconBitmap).getBitmap());
        if (iconBitmap != null) {
            //iconBitmap.writeToParcel(dest, 0);
            dest.writeValue(iconBitmap);
        }

    }

    public Newspaper(Parcel in) {
        id = in.readInt();
        setTitle(in.readString());
        url = in.readString();
        isFavorite = in.readByte() != 0;
        icon = in.readString();

        Parcel parcel = Parcel.obtain();
        Bitmap sourceBitmap;
        Bitmap destinationBitmap;
        sourceBitmap = Bitmap.createBitmap(200, 400, Bitmap.Config.ARGB_8888);

        sourceBitmap.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        destinationBitmap = Bitmap.CREATOR.createFromParcel(parcel);
        iconBitmap = destinationBitmap;
        //iconBitmap = in.readParcelable(Bitmap.class.getClassLoader());
        //icon = new BitmapDrawable((Bitmap) in.readValue(Bitmap.class.getClassLoader()));
    }

    // Comparable method

    @Override
    public int compareTo(Object another) {
        Newspaper np = (Newspaper) another;
        return title.compareTo(np.getTitle());
    }
}