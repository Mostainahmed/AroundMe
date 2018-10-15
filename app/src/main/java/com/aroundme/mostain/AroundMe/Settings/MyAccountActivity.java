package com.aroundme.mostain.AroundMe.Settings;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.aroundme.mostain.App.BaseActivity;
import com.aroundme.mostain.App.DispatchActivity;
import com.aroundme.mostain.AroundMe.Location.LocationActivity;
import com.aroundme.mostain.AroundMe.VipAccount.VipAccountActivity;
import com.aroundme.mostain.R;
import com.aroundme.mostain.Utils.Helper.ActivityWithToolbar;
import com.aroundme.mostain.Utils.service.Constants;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MyAccountActivity extends BaseActivity implements ActivityWithToolbar, GridView.OnItemClickListener, View.OnClickListener {

    private Toolbar mToolbar;

    LinearLayout mLocationButton, mVipMember, mPrivateModde;
    RelativeLayout mSettingsLayout;
    Button DeleteAccount;
    Dialog progressDialog;
    FirebaseAuth mAuth;

    String UID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        mToolbar =  findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.my_account);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLocationButton = findViewById(R.id.button_vip_features);
        mVipMember = findViewById(R.id.button_vip_store);
        mPrivateModde = findViewById(R.id.button_private_active);
        DeleteAccount = findViewById(R.id.button_logout);

        mSettingsLayout = findViewById(R.id.layout_settings);


        mAuth = FirebaseAuth.getInstance();

        UID = mAuth.getUid();





        mLocationButton.setOnClickListener(this);
        mVipMember.setOnClickListener(this);
        mPrivateModde.setOnClickListener(this);

        DeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isInternetAvailable()){

                    deleteAccount();

                } else {


                    showInternetConnectionLostMessage();
                }
            }
        });
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


            case R.id.button_vip_features:
            {
                Intent termsIntent = new Intent(this, LocationActivity.class);
                startActivity(termsIntent);
            }
            break;
            case R.id.button_vip_store:
            {
                Intent privacyIntent = new Intent(this, VipAccountActivity.class);
                startActivity(privacyIntent);
            }

            break;

            case R.id.button_private_active:
            {
                Intent privacyIntent = new Intent(this, PrivateModeActivity.class);
                startActivity(privacyIntent);
            }

            break;

        }




    }

    public void deleteAccount(){


        android.support.v7.app.AlertDialog.Builder block = new android.support.v7.app.AlertDialog.Builder(MyAccountActivity.this, R.style.DialogStyle);
        block.setTitle("Are you sure about this ?");
        block.setCancelable(false);
        block.setMessage("You realy want to delete your account ?, you will not be able to recover it.");
        block.setPositiveButton("Yes, delete", (dialog13, which13) -> {

            showProgressBar("Deleting your account");
            DatabaseReference users = FirebaseDatabase.getInstance().getReference(Constants.ARG_USERS).child(UID);
            users.removeValue((DatabaseError databaseError, DatabaseReference databaseReference) -> {

                if (databaseError == null){

                    DatabaseReference geofire = FirebaseDatabase.getInstance().getReference(Constants.ARG_USERS_GEOFIRE).child(UID);
                    geofire.removeValue((DatabaseError databaseError1, DatabaseReference databaseReference1) -> {

                        if (databaseError1 == null){

                            DatabaseReference blocker = FirebaseDatabase.getInstance().getReference(Constants.ARG_USERS).child(UID).child(Constants.ARG_USERS_BLOCK);
                            blocker.removeValue((DatabaseError databaseError2, DatabaseReference databaseReference2) -> {

                                if (databaseError2 == null){

                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                    user.delete()
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    //Log.d(TAG, "User account deleted.");
                                                    mAuth.signOut();
                                                    LoginManager.getInstance().logOut();

                                                    startLoginActivity();

                                                    dismissProgressBar();
                                                    Toast.makeText(MyAccountActivity.this, "Account Deleted", Toast.LENGTH_LONG).show();
                                                }
                                            });


                                } else {

                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                    user.delete()
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    //Log.d(TAG, "User account deleted.");
                                                    mAuth.signOut();
                                                    LoginManager.getInstance().logOut();

                                                    startLoginActivity();

                                                    dismissProgressBar();
                                                    Toast.makeText(MyAccountActivity.this, "Account Deleted", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                }

                            });


                        } else {

                            dismissProgressBar();
                            Toast.makeText(MyAccountActivity.this, "Failed to delete this account", Toast.LENGTH_LONG).show();
                        }


                    });


                } else {

                    Toast.makeText(MyAccountActivity.this, "Failed to delete this account", Toast.LENGTH_LONG).show();
                }


            });


        });
        block.setNegativeButton("No, don't", (dialog14, which14) -> {
            //finish();
        });
        block.show();


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
