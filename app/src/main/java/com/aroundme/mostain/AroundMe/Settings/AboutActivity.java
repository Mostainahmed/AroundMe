package com.aroundme.mostain.AroundMe.Settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.angopapo.aroundme2.App.BaseActivity;
import com.angopapo.aroundme2.R;
import com.angopapo.aroundme2.Utils.Helper.ActivityWithToolbar;


public class AboutActivity extends BaseActivity implements ActivityWithToolbar, GridView.OnItemClickListener, View.OnClickListener {

    private Toolbar mToolbar;

    Button mUpadte, mRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_about);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mRate = (Button)findViewById(R.id.button_rate);
        mRate.setOnClickListener(this);

        //mUpadte = (Button)findViewById(R.id.button_update);
        //mUpadte.setOnClickListener(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.about_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
    public void onClick(View v) {

        switch (v.getId()) {


            case R.id.button_rate:
            {

                // Any action here


            }
            break;

        }




    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    @Override
    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public int getDriwerId() {
        return 7;
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {

    }

    @Override
    public void onSizeChanged(int height) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

}
