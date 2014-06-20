package com.example.avisreader.preferences;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import com.example.avisreader.R;
import com.tundem.aboutlibraries.Libs;
import com.tundem.aboutlibraries.ui.LibsActivity;

public class SettingsActivity extends ActionBarActivity {

    public static final String KEY_FONT_SIZE = "key_text_size";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_navigation_previous_item);

        Intent i = new Intent(getApplicationContext(), LibsActivity.class);
        i.putExtra(Libs.BUNDLE_FIELDS, Libs.toStringArray(R.string.class.getFields()));
        i.putExtra(Libs.BUNDLE_LIBS, new String[]{"smoothprogressbar", "androidstyleddialogs"});
        i.putExtra(Libs.BUNDLE_VERSION, true);
        i.putExtra(Libs.BUNDLE_LICENSE, true);
        i.putExtra(Libs.BUNDLE_TITLE, "Open Source");
        i.putExtra(Libs.BUNDLE_ACCENT_COLOR, getResources().getColor(R.color.actionbar_color4));
        i.putExtra(Libs.BUNDLE_TRANSLUCENT_DECOR, true);

        startActivity(i);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
