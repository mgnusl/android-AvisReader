package no.kreativo.newspapersnor.preferences;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import no.kreativo.newspapersnor.R;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}
