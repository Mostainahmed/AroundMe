package com.aroundme.mostain.AroundMe.Settings;

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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aroundme.mostain.App.Application;
import com.aroundme.mostain.App.BaseActivity;
import com.aroundme.mostain.AroundMe.Profile.EditProfileActivity;
import com.aroundme.mostain.AroundMe.VipAccount.StoreActivity;
import com.aroundme.mostain.Class.User;
import com.aroundme.mostain.R;
import com.aroundme.mostain.Utils.Helper.ActivityWithToolbar;
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


public  class PrivateModeActivity extends BaseActivity implements ActivityWithToolbar, View.OnClickListener {

    final Context context = this;

    TextView  mGetVipButton;

    Button mPrivateButton, mButtonBack;

    Toolbar mToolbar;

    OnClick onClickListener;

    User mCurrentUser;
    Typeface fonts1;

    private Dialog progressDialog;
    private Date startDate;
    private Date expiate;

    private RelativeLayout mLocationLayout;
    private Drawer drawer;

    TextView mActivated, mFirst, mCost;

    // Database
    private DatabaseReference userDB;

    // Ads
    private InterstitialAd mInterstitialAd;

    private ValueEventListener userListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            //Lấy thông tin của user về và cập nhật lên giao diện

            mCurrentUser = dataSnapshot.getValue(User.class);

            if (mCurrentUser != null){

                checkUserInfo();
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
        setContentView(R.layout.activity_private_mode);

        userDB = FirebaseDatabase.getInstance().getReference("users").child(User.getUser().getUid());
        userDB.addValueEventListener(userListener);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Profile Private Mode");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        mInterstitialAd.loadAd(new AdRequest.
                Builder()
                //.addTestDevice("E1B16FD83EF27F4B1E5D5F6AC91B63BF")
                .build());

        mPrivateButton = (Button) findViewById(R.id.button_travel);
        mGetVipButton = (TextView) findViewById(R.id.button_get_vip);
        mLocationLayout = (RelativeLayout) findViewById(R.id.layout_location);

        mActivated = (TextView)findViewById(R.id.textView32);
        mFirst = (TextView)findViewById(R.id.textView41);
        mCost = (TextView)findViewById(R.id.cost);

        mPrivateButton.setOnClickListener(this);
        mGetVipButton.setOnClickListener(this);

        showProgressBar("Loading...");

    }

    public void checkUserInfo (){

        if (mCurrentUser.isPrivate()){

            dismissProgressBar();


            mActivated.setText(R.string.you_have);
            mFirst.setText(R.string.you_can);

            if (mCurrentUser.isPrivateActived()){

                mPrivateButton.setText(R.string.private_active);
                mCost.setText(R.string.clickprivate);

            } else {

                mPrivateButton.setText(R.string.publicmode);
                mCost.setText(R.string.clickpublic);
                mPrivateButton.setBackgroundResource(R.drawable.buy_button_bg_green);

            }


        } else if (mCurrentUser.getIsVip().equals("vip")) {

            dismissProgressBar();

            mActivated.setText(R.string.you_have);
            mFirst.setText(R.string.you_can);

            if (mCurrentUser.isPrivateActived()) {

                mPrivateButton.setText(R.string.private_active);
                mCost.setText(R.string.clickprivate);

            } else {

                mPrivateButton.setText(R.string.publicmode);
                mCost.setText(R.string.clickpublic);
                mPrivateButton.setBackgroundResource(R.drawable.buy_button_bg_green);
            }

        } else {

            dismissProgressBar();
        }


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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {


            case R.id.button_travel:


                if (mCurrentUser.isPrivate() && mCurrentUser.isPrivateActived()) {

                    setPublicmode();

                } else if (mCurrentUser.isPrivate() && !mCurrentUser.isPrivateActived()) {

                    setGhostmode();

                } else if (mCurrentUser.getIsVip().equals("vip") && mCurrentUser.isPrivateActived()) {

                    setPublicmode();

                } else if ((mCurrentUser.getIsVip().equals("vip") && !mCurrentUser.isPrivateActived())){

                    setGhostmode();

                }  else if  (mCurrentUser.getCredits() < 100) {

                        // ask the user to confirm a deduction and to activate service

                        android.support.v7.app.AlertDialog.Builder notifyLocationServices = new android.support.v7.app.AlertDialog.Builder(PrivateModeActivity.this);
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


                    } else {

                        android.support.v7.app.AlertDialog.Builder notifyLocationServices = new android.support.v7.app.AlertDialog.Builder(PrivateModeActivity.this);
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

                                userDB.child("privateEnd").setValue(expDate.getTime()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()){

                                            userDB.child("credits").runTransaction(new Transaction.Handler() {
                                                @Override
                                                public Transaction.Result doTransaction(MutableData CurrentCredits) {

                                                    int credits = CurrentCredits.getValue(Integer.class);

                                                    if (CurrentCredits.getValue() == null){

                                                        CurrentCredits.setValue(0);

                                                    } else {

                                                        CurrentCredits.setValue( credits -100);
                                                    }

                                                    return Transaction.success(CurrentCredits);

                                                }

                                                @Override
                                                public void onComplete(DatabaseError databaseError, boolean success, DataSnapshot dataSnapshot) {
                                                    // Analyse databaseError for any error during increment

                                                    if (success){

                                                        userDB.child("IsPrivate").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isComplete()){

                                                                    mActivated.setText(R.string.you_have);
                                                                    mFirst.setText(R.string.you_can);
                                                                    mPrivateButton.setText(R.string.private_active2);
                                                                    mPrivateButton.setBackgroundResource(R.drawable.buy_button_bg_green);
                                                                    mPrivateButton.setEnabled(false);
                                                                    dismissProgressBar();
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

            case R.id.button_get_vip: {


                Intent privacyIntent = new Intent(this, StoreActivity.class);
                startActivity(privacyIntent);

            }
            break;


        }

    }

    private class OnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {


        }

    }

    protected void setGhostmode() {

        showProgressBar("PRIVATE MODE...");

        userDB.child("privateActive").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    dismissProgressBar();

                    mPrivateButton.setText(R.string.publicmode);
                    mCost.setText(R.string.clickpublic);
                    mActivated.setText(R.string.you_have);
                    mFirst.setText(R.string.you_can);
                    mPrivateButton.setBackgroundResource(R.drawable.buy_button_bg_red);
                    mPrivateButton.setEnabled(true);

                    dismissProgressBar();
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

    protected void setPublicmode() {

        showProgressBar("PUBLIC MODE...");


        userDB.child("privateActive").setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    dismissProgressBar();

                    mPrivateButton.setText(R.string.pu);
                    mActivated.setText(R.string.you);
                    mCost.setText(R.string.click1);
                    mCost.setText(R.string.click2);
                    mPrivateButton.setBackgroundResource(R.drawable.buy_button_bg_green);
                    mPrivateButton.setEnabled(true);

                    dismissProgressBar();
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
}