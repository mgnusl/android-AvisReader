package com.example.avisreader.utils;

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

        return favorites;
    }
}
