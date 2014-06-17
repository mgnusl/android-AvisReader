package com.example.avisreader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.*;
import android.webkit.*;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.avisreader.data.Newspaper;
import com.example.avisreader.database.DatabaseHelper;

public class WebViewActivity extends ActionBarActivity {

    private WebView webView;
    private Newspaper newspaper;
    private ShareActionProvider shareActionProvider;
    private DatabaseHelper dbHelper;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        webView = (WebView) findViewById(R.id.webview);

        WebIconDatabase.getInstance().open(getDir("icons", MODE_PRIVATE).getPath());

        dbHelper = DatabaseHelper.getInstance(this);

        newspaper = getIntent().getParcelableExtra("url");
        webView.loadUrl(newspaper.getUrl());
        getActionBar().setTitle(newspaper.getTitle());
        getActionBar().setDisplayHomeAsUpEnabled(true);

        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(WebViewActivity.this, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        final ProgressBar mProgressBar1;
        mProgressBar1 = (ProgressBar) findViewById(R.id.google_now);

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100)
                    mProgressBar1.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                getActionBar().setTitle(title);
                super.onReceivedTitle(view, title);
                getActionBar().setIcon(new BitmapDrawable(getResources(), webView.getFavicon()));

            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                BitmapDrawable iconDrawable  =
                        new BitmapDrawable(getResources(), icon);

                getActionBar().setIcon(iconDrawable);
                getSupportActionBar().setIcon(iconDrawable);
                newspaper.setIconBitmap(icon);
                dbHelper.updateNewspaper(newspaper);
                super.onReceivedIcon(view, icon);

            }
        });


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

}
