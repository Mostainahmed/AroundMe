package com.aroundme.mostain.AroundMe.Passport;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angopapo.aroundme2.App.Application;
import com.angopapo.aroundme2.App.BaseActivity;
import com.angopapo.aroundme2.App.NavigationDrawer;
import com.angopapo.aroundme2.AroundMe.Profile.EditProfileActivity;
import com.angopapo.aroundme2.AroundMe.VipAccount.StoreActivity;
import com.angopapo.aroundme2.Class.User;
import com.angopapo.aroundme2.R;
import com.angopapo.aroundme2.Utils.Helper.ActivityWithToolbar;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.Drawer;

import java.util.Calendar;
import java.util.Date;


public  class TravelActivity extends BaseActivity implements ActivityWithToolbar, View.OnClickListener {

    final Context context = this;

    TextView  mGetVipButton;

    Button mTravelButton;

    Toolbar mToolbar;

    OnClick onClickListener;

    User mCurrentUser;
    Typeface fonts1;

    private Dialog progressDialog;
    private Date startDate;
    private Date expiate;

    private DatabaseReference userDB;

    // Ads
    private InterstitialAd mInterstitialAd;

    private RelativeLayout mLocationLayout;
    private Drawer drawer;

    private ValueEventListener userListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Generally
            mCurrentUser = dataSnapshot.getValue(User.class);

            if (mCurrentUser != null){

                // Ads logic verification
                if (mCurrentUser.getFreeAds()) {
                    // Hide ads
                    Log.d("TAG", "The interstitial will not show");

                } else if (mCurrentUser.getIsVip().equals("vip")){
                    // Hide ads
                    Log.d("TAG", "The interstitial will not show");

                } else {

                    // Show ads
                    if (Application.getInstance().getString(R.string.enable_ads).equals("true")){

                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        } else {
                            Log.d("TAG", "The interstitial wasn't loaded yet.");
                        }

                    }
                }
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            //Có lỗi xảy ra, không lấy đc dữ liệu
            Log.e(EditProfileActivity.class.getName(), "loadPost:onCancelled", databaseError.toException());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel);

        userDB = FirebaseDatabase.getInstance().getReference("users").child(User.getCurrentUserId());
        userDB.addValueEventListener(userListener);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        mInterstitialAd.loadAd(new AdRequest.
                Builder()
                //.addTestDevice("E1B16FD83EF27F4B1E5D5F6AC91B63BF")
                .build());


        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mTravelButton = (Button) findViewById(R.id.button_travel);
        mGetVipButton = (TextView) findViewById(R.id.button_get_vip);
        mLocationLayout = (RelativeLayout) findViewById(R.id.layout_location);

        mTravelButton.setOnClickListener(this);
        mGetVipButton.setOnClickListener(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Travel premium");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawer = NavigationDrawer.createDrawer(this);

        // Ads


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {


            case R.id.button_travel:

                if (mCurrentUser.getIsVip().equals("vip")) {

                    //Intent mapIntent = new Intent( this, MyVisitorsActivity.class);
                    //startActivity(mapIntent);
                    startMapsActivity();

                }   else if (mCurrentUser.getIsTravel()){

                    startMapsActivity();
                }

                else if (mCurrentUser.getCredits() < 100){

                    // ask the user to confirm a deduction and to activate service

                    android.support.v7.app.AlertDialog.Builder notifyLocationServices = new android.support.v7.app.AlertDialog.Builder(TravelActivity.this);
                    notifyLocationServices.setTitle(getString(R.string.sorry_vip));
                    notifyLocationServices.setMessage(getString(R.string.cost_vip) + (mCurrentUser.getCredits() + " " + getString(R.string.vip_act_expl)));
                    notifyLocationServices.setPositiveButton(getString(R.string.recg_vip), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // buy the service and deduct 100 Credits here

                            Intent intent = new Intent(getApplicationContext(), StoreActivity.class);
                            startActivity(intent);
                        }
                    });
                    notifyLocationServices.setNegativeButton(getString(R.string.conf_4), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //finish();
                        }
                    });
                    notifyLocationServices.show();


                }

                else {

                    android.support.v7.app.AlertDialog.Builder notifyLocationServices = new android.support.v7.app.AlertDialog.Builder(TravelActivity.this);
                    notifyLocationServices.setTitle(getString(R.string.conf_1));
                    notifyLocationServices.setMessage(getString(R.string.conf_2));
                    notifyLocationServices.setPositiveButton(getString(R.string.conf_3), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // deduct 100 credits in the user account and activated a service for 30 days
                            showProgressBar(getString(R.string.active_vip));

                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.DATE, 30);
                            Date expDate = calendar.getTime();

                            userDB.child("passportEnd").setValue(expDate.getTime()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        userDB.child("credits").runTransaction(new Transaction.Handler() {
                                            @Override
                                            public Transaction.Result doTransaction(MutableData mutableData) {
                                                    if (mutableData.getValue() == null) {
                                                        mutableData.setValue(0);
                                                    } else {
                                                        //int count = mutableData.getValue(Integer.class);
                                                        int count = mutableData.getValue(Integer.class);
                                                        mutableData.setValue(count -100);
                                                    }
                                                    return Transaction.success(mutableData);

                                            }

                                            @Override
                                            public void onComplete(DatabaseError databaseError, boolean success, DataSnapshot dataSnapshot) {
                                                // Analyse databaseError for any error during increment

                                                if (success){

                                                    userDB.child("isTravel").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isComplete()){

                                                                Snackbar.make(mLocationLayout, R.string.cong_vip, Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {

                                                                        startMapsActivity();

                                                                    }
                                                                }).setActionTextColor(Color.WHITE).show();

                                                                dismissProgressBar();
                                                                mTravelButton.setText(getString(R.string.vip_activated));
                                                                mTravelButton.setBackgroundResource(R.drawable.buy_button_bg_green);
                                                                mTravelButton.setEnabled(false);

                                                            }

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                        }
                                                    });
                                                }
                                            }
                                        });


                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    dismissProgressBar();

                                    Snackbar.make(mLocationLayout, R.string.loc_cant, Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    }).setActionTextColor(Color.WHITE).show();
                                }
                            });
                        }
                    });
                    notifyLocationServices.setNegativeButton(getString(R.string.conf_4), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //finish();
                        }
                    });
                    notifyLocationServices.show();

                }
                break;


            case R.id.button_get_vip:
            {


                    startVipActivity();

                }
                break;

        }

    }

    private class OnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {


        }

    }

    protected void startMapsActivity() {
        Intent whoSeeIntent = new Intent(this, PassportActivity.class);
        startActivity(whoSeeIntent);
    }

    protected void startVipActivity() {
        Intent vipIntent = new Intent(this, StoreActivity.class);
        startActivity(vipIntent);
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
        return 5;
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

        //finish();
        //android.os.Process.killProcess(android.os.Process.myPid());
         super.onBackPressed(); // Comment this super call to avoid calling finish()
    }

    public void showProgressBar(String message){
        progressDialog = ProgressDialog.show(this, "", message, true);
    }
    public void dismissProgressBar() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }
}