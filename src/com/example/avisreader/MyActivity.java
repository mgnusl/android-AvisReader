package com.example.avisreader;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.avisreader.adapter.MainListAdapter;
import com.example.avisreader.data.NewsPaper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MyActivity extends ActionBarActivity {

    private List<NewsPaper> newsPapersList;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        listView = (ListView) findViewById(R.id.listView);

        newsPapersList = new ArrayList<NewsPaper>();

        // Fill newsPaperList with NewsPapers
        List<String> tempList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.newspapers)));
        for (String s : tempList) {
            String[] temp = s.split(",");
            int resID = getResources().getIdentifier(((String) temp[2].trim()), "drawable", MyActivity.this.getPackageName());
            // Hvis ikke avisen har et gyldig ikon
            if(resID == 0)
                resID = getResources().getIdentifier("no_icon", "drawable", MyActivity.this.getPackageName());

            Drawable icon = getResources().getDrawable(resID);
            newsPapersList.add(new NewsPaper(temp[1], temp[0], icon));
        }

        // Sort the array alphabetically
        Collections.sort(newsPapersList);

        listView.setAdapter(new MainListAdapter(this, R.layout.newspaper_rowitem, newsPapersList));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(MyActivity.this, WebViewActivity.class).putExtra("url",
                        newsPapersList.get(position)));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Log.d("APP", "SØK");
                return true;
            case R.id.action_add:
                Log.d("APP", "ADD");
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
