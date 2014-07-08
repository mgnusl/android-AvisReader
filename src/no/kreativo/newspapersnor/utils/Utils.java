package no.kreativo.newspapersnor.utils;

import android.webkit.WebSettings;
import no.kreativo.newspapersnor.data.Newspaper;
import no.kreativo.newspapersnor.util.IabHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utils {

    public static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApdCJK4xM08" +
                                            "rnNBcWnJNPzYtJtpNvCx+0Gw0bBUigYPql8fx2KTTCgzPhvSsbrEqf" +
                                            "GiEwlvDPRAPSTCqwkN6VR1Kxw/iqL77s/GPIsdvA/l2br/n23ZKhKB" +
                                            "5OoZVVrOliy1mykhAlDdIM8V6ZQ/FUt8OjCWgSB8QOTKPe5bNBbYxM" +
                                            "m2Ho7ka4IhfRTy1JYoTCPXm9ZnGXVOLbZ2glSiuWpm7PwNA8kZKhS+" +
                                            "xA0dfZTU4m9kFgeMQVkGvJYWrz1TtEioLfQiMiohT/Aa0ScdgphdhB" +
                                            "efM/OXZAZtGlMbUd2404FYDZY07taEgmj2IUotkCFZUel15CgJmMiq" +
                                            "ZOIlXaKQIDAQAB";
    public static final String SKU_REMOVEADS = "removeads";


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
