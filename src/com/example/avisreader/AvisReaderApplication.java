package com.example.avisreader;

import android.app.Application;
import android.content.SharedPreferences;

public class AvisReaderApplication extends Application {

    public static final String PREFS_NAME = "PrefsFile";

    @Override
    public void onCreate() {
        super.onCreate();

        // Restore preferences. Sets the boolean value to true if it doesnt already exist in sharedprefs
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean isFirstLaunch = settings.getBoolean("isFirstLaunch", true);
        setIsFirstLaunch(isFirstLaunch);

    }

    public boolean isFirstLaunch() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        return settings.getBoolean("isFirstLaunch", true);
    }

    public void setIsFirstLaunch(boolean value) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("isFirstLaunch", value);
        editor.commit();
    }
}
