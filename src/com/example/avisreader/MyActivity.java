package com.example.avisreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import com.example.avisreader.adapter.MainListAdapter;
import com.example.avisreader.data.NewsPaper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyActivity extends Activity {

    private List<NewsPaper> newsPapersList;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        listView = (ListView)findViewById(R.id.listView);

        newsPapersList = new ArrayList<NewsPaper>();

        // Fill newsPaperList with NewsPapers
        List<String> tempList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.newspapers)));
        for(String s : tempList) {
            String[] temp = s.split(",");
            newsPapersList.add(new NewsPaper(temp[1], temp[0]));
        }

        listView.setAdapter(new MainListAdapter(this, R.layout.newspaper_rowitem, newsPapersList));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(MyActivity.this, WebViewActivity.class).putExtra("url",
                        newsPapersList.get(position).getUrl()));
            }
        });

    }
}
