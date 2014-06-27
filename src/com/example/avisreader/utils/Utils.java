package com.example.avisreader.utils;

import android.util.Log;
import android.webkit.WebSettings;
import com.example.avisreader.data.Newspaper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utils {

    public static List<Newspaper> sortDataset(List<Newspaper> initialList) {
        List<Newspaper> nonFavorites = new ArrayList<Newspaper>();
        List<Newspaper> favorites = new ArrayList<Newspaper>();

        for (Newspaper np : initialList) {
            if (np.isFavorite())
                favorites.add(np);
            else
                nonFavorites.add(np);
        }

        Collections.sort(favorites);
        Collections.sort(nonFavorites);

        favorites.addAll(nonFavorites);

        for(Newspaper lol : favorites)
            Log.d("APP", lol.getTitle());

        return favorites;
    }

    public static boolean hasIceCreamSandwich() {
        int sdkCode = Integer.valueOf(android.os.Build.VERSION.SDK);

        if (sdkCode >= 14)
            return true;
        else
            return false;

    }

    public static WebSettings.TextSize resolveTextSize(int integerTextSize) {

        // 60, 80, 100, 150, 170
       switch (integerTextSize) {
           case 60:
               return WebSettings.TextSize.SMALLEST;
           case 80:
               return WebSettings.TextSize.SMALLER;
           case 100:
               return WebSettings.TextSize.NORMAL;
           case 150:
               return WebSettings.TextSize.LARGER;
           case 170:
               return WebSettings.TextSize.LARGEST;
           default:
               return WebSettings.TextSize.NORMAL;

       }
    }
}
