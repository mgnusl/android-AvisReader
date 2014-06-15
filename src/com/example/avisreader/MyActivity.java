package com.example.avisreader;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.*;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import com.example.avisreader.adapter.MainListAdapter;
import com.example.avisreader.data.Newspaper;
import com.example.avisreader.database.DatabaseHelper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MyActivity extends ActionBarActivity {

    private List<Newspaper> newsPapersList;
    private ListView listView;
    private DatabaseHelper dbHelper;
    private MainListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        listView = (ListView) findViewById(R.id.listView);

        dbHelper = DatabaseHelper.getInstance(this);


        newsPapersList = new ArrayList<Newspaper>();

        // Fill newsPaperList with NewsPapers
        List<String> tempList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.newspapers)));
        for (String s : tempList) {
            String[] temp = s.split(",");
            int resID = getResources().getIdentifier(((String) temp[2].trim()), "drawable", MyActivity.this.getPackageName());
            // If no valid icon was found
            if (resID == 0)
                resID = getResources().getIdentifier("no_icon", "drawable", MyActivity.this.getPackageName());

            Drawable icon = getResources().getDrawable(resID);
            newsPapersList.add(new Newspaper(temp[1], temp[0], icon));
            dbHelper.addNewspaper(new Newspaper(temp[1], temp[0], icon));
        }

        // Sort the array alphabetically
        Collections.sort(newsPapersList);

        adapter = new MainListAdapter(this, R.layout.newspaper_rowitem, newsPapersList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(MyActivity.this, WebViewActivity.class).putExtra("url",
                        newsPapersList.get(position)));
            }
        });


        Log.d("APP", dbHelper.getNewspaper(2).toString());

    }

    private void showAddPopupDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getResources().getString(R.string.add_newspaper));

        final View inflator = this.getLayoutInflater().inflate(getResources().getLayout(R.layout.dialog_add), null);

        alertDialogBuilder
                .setView(inflator)

                .setPositiveButton(getResources().getString(R.string.add),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText titleEditText = (EditText) inflator.findViewById(R.id.titleEditText);
                                EditText urlEditText = (EditText) inflator.findViewById(R.id.urlEditText);

                                // Validate the url
                                String url = urlEditText.getText().toString();
                                if(!url.contains("http://")) {
                                    StringBuilder sb = new StringBuilder(url);
                                    sb.insert(0, "http://");
                                    url = sb.toString();
                                }

                                Log.d("APP", url);

                                Newspaper np = new Newspaper(
                                        titleEditText.getText().toString(),
                                        url,
                                        null
                                );
                                dbHelper.addNewspaper(np);
                                newsPapersList.add(np);
                                adapter.notifyDataSetChanged();
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
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
                showAddPopupDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
