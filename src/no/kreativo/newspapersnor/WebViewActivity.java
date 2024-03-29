package no.kreativo.newspapersnor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.*;
import android.webkit.*;
import android.widget.ProgressBar;
import no.kreativo.newspapersnor.data.Newspaper;
import no.kreativo.newspapersnor.database.DatabaseHelper;
import no.kreativo.newspapersnor.preferences.SettingsActivity;
import no.kreativo.newspapersnor.util.IabHelper;
import no.kreativo.newspapersnor.util.IabResult;
import no.kreativo.newspapersnor.util.Inventory;
import no.kreativo.newspapersnor.utils.Utils;
import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.ktwaxqztxlujp.AdController;

import java.net.URI;
import java.net.URISyntaxException;

public class WebViewActivity extends ActionBarActivity {

    private WebView webView;
    private Newspaper newspaper;
    private ShareActionProvider shareActionProvider;
    private DatabaseHelper dbHelper;
    private AdController ad;
    private SuperActivityToast superToast;
    private IabHelper iabHelper;
    private AvisReaderApplication globalApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        // Setup in-app billing
        iabHelper = new IabHelper(this, Utils.PUBLIC_KEY);
        iabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                iabHelper.queryInventoryAsync(gotInventoryListener);
            }
        });

        globalApp = (AvisReaderApplication) getApplicationContext();


        // SuperToast style
        superToast = new SuperActivityToast(WebViewActivity.this);
        superToast.setDuration(SuperToast.Duration.LONG);
        superToast.setTextColor(Color.WHITE);
        superToast.setTouchToDismiss(true);

        webView = (WebView) findViewById(R.id.webview);
        dbHelper = DatabaseHelper.getInstance(this);
        WebIconDatabase.getInstance().open(getDir("icons", MODE_PRIVATE).getPath());

        // Settings
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        // Set font size from preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int fontSize = 100;
        String fontSizeString = prefs.getString(SettingsActivity.KEY_FONT_SIZE, "empty");
        if (!fontSizeString.equals("empty")) {
            fontSize = Integer.parseInt(fontSizeString);
        }

        if (Utils.hasIceCreamSandwich()) {
            settings.setTextZoom(fontSize);
        } else {
            settings.setTextSize(Utils.resolveTextSize(fontSize));
        }


        ad = new AdController(this, "692563668");
        //ad.loadAd();

        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                superToast.setBackground(SuperToast.Background.RED);
                superToast.setText(getResources().getString(R.string.could_not_find));
                superToast.show();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        final ProgressBar progressBar1;
        progressBar1 = (ProgressBar) findViewById(R.id.google_now);
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100)
                    progressBar1.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                getActionBar().setTitle(title);
                super.onReceivedTitle(view, title);
                if (Utils.hasIceCreamSandwich())
                    getActionBar().setIcon(new BitmapDrawable(getResources(), webView.getFavicon()));
                else
                    getSupportActionBar().setIcon(new BitmapDrawable(getResources(), webView.getFavicon()));

            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                BitmapDrawable iconDrawable = new BitmapDrawable(getResources(), icon);
                if (Utils.hasIceCreamSandwich()) {
                    getActionBar().setIcon(iconDrawable);
                } else
                    getSupportActionBar().setIcon(iconDrawable);

                // Check if the current website url is part of the original url
                try {
                    URI uri = new URI(webView.getUrl());
                    String uriHost = uri.getHost();

                    // Strip the url
                    if (uriHost.contains("www."))
                        uriHost = uriHost.replace("www.", "");

                    if (newspaper.getUrl().contains(uriHost)) {
                        // current url is part of the original url
                        newspaper.setIconBitmap(icon);
                        dbHelper.updateNewspaper(newspaper);
                    }

                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    Log.d("APP", "something happened when checking url...");
                }

                super.onReceivedIcon(view, icon);

            }
        });

        // Load webpage
        newspaper = getIntent().getParcelableExtra("selected_newspaper");
        webView.loadUrl(newspaper.getUrl());

        // Actionbar
        getActionBar().setTitle(newspaper.getTitle());
        getActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_navigation_previous_item);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    private void refreshWebPage(String url) {
        webView.loadUrl(url);
    }

    public IabHelper.QueryInventoryFinishedListener gotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            if (result.isFailure()) {
                // handle error here
            } else {
                // does the user have the premium upgrade?
                boolean hasRemoveAds = inventory.hasPurchase(Utils.SKU_REMOVEADS);

                // Ads
                int count = globalApp.getGlobalCounter();
                if (!hasRemoveAds) {
                    if (((count % 2) == 0) && (count > 0)) {
                        // vis reklame
                        ad = new AdController(WebViewActivity.this, "688020490");
                        ad.loadAd();
                    }
                }
                globalApp.incrementGlobalCounter();
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_refresh:
                refreshWebPage(webView.getUrl());
                return true;
            case R.id.action_share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, webView.getUrl());
                startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_this)));
                return true;
            case R.id.action_removeads:
                startActivity(new Intent(WebViewActivity.this, InAppActivity.class));
                return true;
            case R.id.action_settings:
                startActivity(new Intent(WebViewActivity.this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.webview_menu, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SuperActivityToast.onSaveState(outState);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (iabHelper != null) iabHelper.dispose();
        iabHelper = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!iabHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}