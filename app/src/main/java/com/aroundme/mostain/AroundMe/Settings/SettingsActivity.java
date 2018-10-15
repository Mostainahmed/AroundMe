package com.aroundme.mostain.AroundMe.Settings;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.aroundme.mostain.App.DispatchActivity;
import com.aroundme.mostain.App.NavigationDrawer;
import com.aroundme.mostain.R;
import com.aroundme.mostain.Utils.Helper.ActivityWithToolbar;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikepenz.materialdrawer.Drawer;

public class SettingsActivity extends AppCompatActivity implements ActivityWithToolbar, View.OnClickListener {

    private static final String TAG = "SettingsActivity";

    LinearLayout mTermsButton, mPrivacyButton, mPressButton, mSupportButton, mAboutButton, mShareButton, mInviteButton, mAccount;

    Toolbar mToolbar;

    final Context context = this;

    Button mLogOutButton;


    private Drawer drawer;

    //Intents
    Intent mLoginIntent;

    private Dialog progressDialog;

    RelativeLayout mSignUpActivity;

    private RelativeLayout mSettingsLayout;



    private FirebaseAuth mAuth;

    protected void showMessage(String message) {
        Snackbar.make(mSignUpActivity, message, Snackbar.LENGTH_SHORT).show();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        mToolbar = (Toolbar)findViewById(R.id.toolbar);

        mTermsButton = (LinearLayout)findViewById(R.id.button_terms);
        mPrivacyButton = (LinearLayout)findViewById(R.id.button_privacy);
        mAboutButton = (LinearLayout)findViewById(R.id.button_about);
        mShareButton = (LinearLayout)findViewById(R.id.button_share);
        mAccount = (LinearLayout)findViewById(R.id.button_account);

        mTermsButton.setOnClickListener(this);
        mPrivacyButton.setOnClickListener(this);
        mAboutButton.setOnClickListener(this);
        mShareButton.setOnClickListener(this);
        mAccount.setOnClickListener(this);

        mLogOutButton = (Button)findViewById(R.id.button_logout);
        mSettingsLayout = (RelativeLayout) findViewById(R.id.layout_settings);

        mLogOutButton.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.drawer_item_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawer = NavigationDrawer.createDrawer(this);


    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {


            case R.id.button_logout:
            {


                if (isInternetAvailable()) {

                    android.support.v7.app.AlertDialog.Builder notifyLocationServices = new android.support.v7.app.AlertDialog.Builder(SettingsActivity.this, R.style.DialogStyle);
                    notifyLocationServices.setTitle(getString(R.string.settings_loging));
                    notifyLocationServices.setCancelable(false);
                    notifyLocationServices.setMessage(getString(R.string.settings_log_are));
                    notifyLocationServices.setPositiveButton(getString(R.string.settings_log_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                                showProgressBar(getString(R.string.settings_log_yes2));


                            mAuth.signOut();
                            LoginManager.getInstance().logOut();

                            startLoginActivity();

                            dismissProgressBar();

                            }

                        });
                    notifyLocationServices.setNegativeButton(getString(R.string.conf_4), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //finish();
                        }
                    });
                    notifyLocationServices.show();


                }else {

                    showInternetConnectionLostMessage();
                }



            }
            break;
            case R.id.button_terms:
            {
                Intent termsIntent = new Intent(this, TermsActivity.class);
                startActivity(termsIntent);
            }
            break;
            case R.id.button_privacy:
            {
                Intent privacyIntent = new Intent(this, PrivacyActivity.class);
                startActivity(privacyIntent);
            }
            
            break;
            case R.id.button_about:
            {
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
            }
            break;

            case R.id.button_share:
            {
                final Intent intent = new Intent(context, ShareActivity.class);
                startActivity(intent);
            }
            break;

            case R.id.button_account:
            {
                final Intent intent = new Intent(context, MyAccountActivity.class);
                startActivity(intent);
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
        return 8;
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {

    }

    @Override
    public void onSizeChanged(int height) {

    }

    @Override
    public void onBackPressed()
    {

        //super.onBackPressed();
        // Comment this super call to avoid calling finish()
    }
    public void showProgressBar(String message){
        progressDialog = ProgressDialog.show(this, "", message, true);
    }
    public void dismissProgressBar() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }
    public void showInternetConnectionLostMessage(){
        Snackbar.make(mSettingsLayout, R.string.settings_no_inte, Snackbar.LENGTH_SHORT).show();

    }

    public boolean isInternetAvailable(){
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, DispatchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("main111", "onPause");
    }

    // Verify every 45s if user still online tell the server.


}
