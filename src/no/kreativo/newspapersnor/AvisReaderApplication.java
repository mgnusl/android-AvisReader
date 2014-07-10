package no.kreativo.newspapersnor;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AvisReaderApplication extends Application {

    public static final String PREFS_NAME = "PrefsFile";
    public static final String GLOBAL_COUNTER = "globalteller";

    @Override
    public void onCreate() {
        super.onCreate();

        // Restore preferences. Sets the boolean value to true if it doesnt already exist in sharedprefs
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean isFirstLaunch = settings.getBoolean("isFirstLaunch", true);
        setIsFirstLaunch(isFirstLaunch);

        // Setup globalCounter
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        int globalCounter = preferences.getInt(GLOBAL_COUNTER, -1);
        if (globalCounter == -1) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(GLOBAL_COUNTER, 0);
            editor.commit();
        }
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

    public void incrementGlobalCounter() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        int current = preferences.getInt(GLOBAL_COUNTER, -1);
        current++;
        editor.putInt(GLOBAL_COUNTER, current);
        editor.commit();
    }

    public int getGlobalCounter() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        return preferences.getInt(GLOBAL_COUNTER, -1);
    }
}
