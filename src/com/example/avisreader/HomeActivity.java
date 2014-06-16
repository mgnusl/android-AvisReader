package com.example.avisreader;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import com.example.avisreader.adapter.MainListAdapter;
import com.example.avisreader.data.Newspaper;
import com.example.avisreader.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HomeActivity extends ActionBarActivity implements SearchView.OnQueryTextListener {

    private List<Newspaper> newsPapersList;
    private ListView listView;
    private DatabaseHelper dbHelper;
    private MainListAdapter adapter;
    private AvisReaderApplication globalApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        listView = (ListView) findViewById(R.id.listView);

        dbHelper = DatabaseHelper.getInstance(this);
        globalApp = (AvisReaderApplication) getApplicationContext();
        newsPapersList = new ArrayList<Newspaper>();

        // If first time launch, fill newsPaperList with default Newspapers
        Log.d("APP", "IS FIRST LAUNCH BEFORE IF: " + Boolean.toString(globalApp.isFirstLaunch()));
        if (globalApp.isFirstLaunch()) {
            List<String> tempList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.newspapers)));
            for (String s : tempList) {
                String[] temp = s.split(",");
                int resID = getResources().getIdentifier(((String) temp[2].trim()), "drawable", HomeActivity.this.getPackageName());
                // If no valid icon was found
                if (resID == 0)
                    resID = getResources().getIdentifier("no_icon", "drawable", HomeActivity.this.getPackageName());

                Drawable icon = getResources().getDrawable(resID);
                Newspaper np = new Newspaper(temp[1], temp[0], icon);
                int id = dbHelper.addNewspaper(np);
                np.setId(id);
                newsPapersList.add(np);
            }
            globalApp.setIsFirstLaunch(false);
        } else {
            newsPapersList = dbHelper.getAllNewspapers();
        }


        // Sort the array alphabetically
        Collections.sort(newsPapersList);

        adapter = new MainListAdapter(this, R.layout.newspaper_rowitem, newsPapersList);
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(HomeActivity.this, WebViewActivity.class).putExtra("url",
                        newsPapersList.get(position)));
            }
        });


        Log.d("APP", "DATABASE SIZE " + dbHelper.getAllNewspapers().size());
        Log.d("APP", "LIST SIZE " + newsPapersList.size());

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
                                if (!url.contains("http://")) {
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
                                int id = dbHelper.addNewspaper(np);
                                np.setId(id);
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

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            case R.id.action_add:
                showAddPopupDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (TextUtils.isEmpty(s)) {
            listView.clearTextFilter();
        } else {
            listView.setFilterText(s.toString());
        }

        return true;
    }
}
