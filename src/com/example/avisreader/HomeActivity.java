package com.example.avisreader;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.example.avisreader.preferences.SettingsActivity;
import com.example.avisreader.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeActivity extends ActionBarActivity implements SearchView.OnQueryTextListener {

    private List<Newspaper> newsPaperList;
    private ListView listView;
    private DatabaseHelper dbHelper;
    private MainListAdapter adapter;
    private AvisReaderApplication globalApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Setup preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        listView = (ListView) findViewById(R.id.listView);

        dbHelper = DatabaseHelper.getInstance(this);
        globalApp = (AvisReaderApplication) getApplicationContext();
        newsPaperList = new ArrayList<Newspaper>();

        // If first time launch, fill newsPaperList with default Newspapers
        if (globalApp.isFirstLaunch()) {
            List<String> tempList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.newspapers)));
            for (String s : tempList) {
                String[] temp = s.split(",");
                Newspaper np = new Newspaper(temp[1], temp[0]);
                int id = dbHelper.addNewspaper(np);
                np.setId(id);
                newsPaperList.add(np);
            }
            globalApp.setIsFirstLaunch(false);
        } else {
            newsPaperList = dbHelper.getAllNewspapers();
        }


        adapter = new MainListAdapter(this, R.layout.newspaper_rowitem, newsPaperList);
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(HomeActivity.this, WebViewActivity.class).putExtra("url",
                        newsPaperList.get(position)));
            }
        });

        registerForContextMenu(listView);

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

                                Newspaper np = new Newspaper(
                                        titleEditText.getText().toString(),
                                        url
                                );
                                int id = dbHelper.addNewspaper(np);
                                np.setId(id);
                                newsPaperList.add(np);
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

        // Search
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
            case R.id.action_settings:
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
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

    @Override
    protected void onRestart() {
        super.onRestart();

        // Refresh the list (may have been downloaded new favicons)
        List<Newspaper> tempList = Utils.sortDataset(dbHelper.getAllNewspapers());
        newsPaperList.clear();
        newsPaperList.addAll(tempList);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if(v.getId() == R.id.listView) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.listview_context_menu, menu);
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.ctxmenu_delete:
                Log.d("APP", "DELETE " + newsPaperList.get(info.position).getTitle());
                dbHelper.deleteNewspaper(newsPaperList.get(info.position));
                newsPaperList.remove(info.id);
                adapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
