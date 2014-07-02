package no.kreativo.newspapersnor;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.ktwaxqztxlujp.AdController;
import fr.nicolaspomepuy.discreetapprate.AppRate;
import fr.nicolaspomepuy.discreetapprate.RetryPolicy;
import no.kreativo.newspapersnor.adapter.MainListAdapter;
import no.kreativo.newspapersnor.data.Newspaper;
import no.kreativo.newspapersnor.database.DatabaseHelper;
import no.kreativo.newspapersnor.preferences.SettingsActivity;
import no.kreativo.newspapersnor.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeActivity extends ActionBarActivity implements SearchView.OnQueryTextListener {

    private List<Newspaper> newsPaperList;
    private ListView listView;
    private DatabaseHelper dbHelper;
    private MainListAdapter adapter;
    private AvisReaderApplication globalApp;
    private AdController ad;
    private SuperActivityToast superToast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        globalApp = (AvisReaderApplication) getApplicationContext();

        AppRate.with(this).initialLaunchCount(3).retryPolicy(RetryPolicy.EXPONENTIAL)
                .checkAndShow();

        // SuperToast style
        superToast = new SuperActivityToast(HomeActivity.this);
        superToast.setDuration(SuperToast.Duration.LONG);
        superToast.setTextColor(Color.WHITE);
        superToast.setTouchToDismiss(true);

        // Ads
        int count = globalApp.getGlobalCounter();
        if (((count % 18) == 0) && (count > 0)) {
            // vis reklame
            ad = new AdController(this, "692563668");
            //ad.loadAd();
        }
        globalApp.incrementGlobalCounter();

        // Setup preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        listView = (ListView) findViewById(R.id.listView);
        dbHelper = DatabaseHelper.getInstance(this);
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
                Newspaper entry = (Newspaper) parent.getItemAtPosition(position);
                startActivity(new Intent(HomeActivity.this, WebViewActivity.class).putExtra("selected_newspaper",
                entry));
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

                                // Validate the url. Add it if no exceptions are thrown
                                boolean successful = true;
                                String url = "";
                                String title = "";
                                try {
                                    url = urlEditText.getText().toString();
                                    title = titleEditText.getText().toString();
                                    if (url.equals("") || title.equals(""))
                                        throw new IllegalArgumentException();
                                    if (!url.contains("http://")) {
                                        StringBuilder sb = new StringBuilder(url);
                                        sb.insert(0, "http://");
                                        url = sb.toString();
                                    }
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                    successful = false;
                                    superToast.setBackground(SuperToast.Background.RED);
                                    superToast.setText(getResources().getString(R.string.empty_input));
                                    superToast.show();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    successful = false;
                                    superToast.setBackground(SuperToast.Background.RED);
                                    superToast.setText(getResources().getString(R.string.invalid));
                                    superToast.show();
                                } finally {
                                    if (successful) {
                                        Newspaper np = new Newspaper(
                                                " " + title,
                                                url
                                        );
                                        int id = dbHelper.addNewspaper(np);
                                        np.setId(id);
                                        newsPaperList.add(np);

                                        // Sort the dataset and notify the view of updates
                                        List<Newspaper> tempList = Utils.sortDataset(newsPaperList);
                                        newsPaperList.clear();
                                        newsPaperList.addAll(tempList);
                                        adapter.notifyDataSetChanged();

                                        superToast.setText(getResources().getString(R.string.success));
                                        superToast.setBackground(SuperToast.Background.GREEN);
                                        superToast.show();
                                    }

                                }

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

        if (v.getId() == R.id.listView) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.listview_context_menu, menu);
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.ctxmenu_delete:
                dbHelper.deleteNewspaper(newsPaperList.get(info.position));
                newsPaperList.remove(info.position);
                adapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void onDestroy() {
        ad.destroyAd();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SuperActivityToast.onSaveState(outState);

    }

}
