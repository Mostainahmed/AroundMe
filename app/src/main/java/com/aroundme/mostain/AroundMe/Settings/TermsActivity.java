package com.aroundme.mostain.AroundMe.Settings;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.angopapo.aroundme2.App.BaseActivity;
import com.angopapo.aroundme2.R;
import com.angopapo.aroundme2.Utils.Helper.ActivityWithToolbar;
import com.angopapo.aroundme2.Utils.Internet.CheckServer;

public class TermsActivity extends BaseActivity implements ActivityWithToolbar, View.OnClickListener {

    Toolbar mToolbar;

    private CheckServer mCheckServer;

    final Context context = this;

    private WebView mWebView;

    String URL = "https://www.angopapo.com/terms.html";

    ProgressBar loadingProgressBar, loadingTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        mToolbar = (Toolbar)findViewById(R.id.toolbar);

        mCheckServer = (CheckServer)findViewById(R.id.wait_for_internet_connection);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.terms_title2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mWebView = (WebView) findViewById(R.id.WebView_Terms);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebView.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        //mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // load online by default

        mWebView.setWebViewClient(new MyWebViewClient());

        mWebView.loadUrl(URL);

        loadingProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mWebView.setWebChromeClient(new WebChromeClient() {

            // this will be called on page loading progress

            @Override

            public void onProgressChanged(WebView view, int newProgress) {

                super.onProgressChanged(view, newProgress);


                loadingProgressBar.setProgress(newProgress);
                //loadingTitle.setProgress(newProgress);
                // hide the progress bar if the loading is complete

                if (newProgress == 100) {
                    loadingProgressBar.setVisibility(View.GONE);

                } else {
                    loadingProgressBar.setVisibility(View.VISIBLE);

                }

            }

        });
        mCheckServer.checkInternetConnection(new CheckServer.OnConnectionIsAvailableListener() {
            @Override
            public void onConnectionIsAvailable() {

                mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                findUserDialogs();
            }
        });

    }
    public void findUserDialogs(){



        mCheckServer.close();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private class MyWebViewClient extends WebViewClient {


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            view.loadUrl(url);
            return true;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    @Override
    public Toolbar getToolbar() {
        return null;
    }

    @Override
    public Activity getActivity() {
        return null;
    }

    @Override
    public int getDriwerId() {
        return 0;
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {

    }

    @Override
    public void onSizeChanged(int height) {

    }

    @Override
    public void onClick(View v) {

    }

}
