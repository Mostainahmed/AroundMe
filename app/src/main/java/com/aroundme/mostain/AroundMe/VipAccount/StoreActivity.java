package com.aroundme.mostain.AroundMe.VipAccount;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;

import com.aroundme.mostain.App.BaseActivity;
import com.aroundme.mostain.AroundMe.Profile.EditProfileActivity;
import com.aroundme.mostain.Class.User;
import com.aroundme.mostain.R;
import com.aroundme.mostain.Utils.Helper.ActivityWithToolbar;
import com.aroundme.mostain.Utils.Purchase.IabHelper;
import com.aroundme.mostain.Utils.Purchase.IabResult;
import com.aroundme.mostain.Utils.Purchase.Inventory;
import com.aroundme.mostain.Utils.Purchase.Purchase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.Drawer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class StoreActivity extends BaseActivity implements ActivityWithToolbar, View.OnClickListener, IabHelper.OnIabPurchaseFinishedListener {

    private Dialog progressDialog;

    // in-billing start here
    private IInAppBillingService mService;

    public static final int RC_VIP_SELL = 2001;

    // Subscription list VIP
    private static final String SKU_VIP_1 = "aroundme.vip.1m";  // vip for 1 month
    private static final String SKU_VIP_3 = "aroundme.vip.3m"; // vip for 3 months

    // Credits id list
    private static final String SKU_100 = "aroundme.100.credits";  // 100 credits consumable
    private static final String SKU_350 = "aroundme.350.credits";  // 350 credits consumable






    private List<String> mSkuList;
    private Button  button_subscribe1, button_subscribe2, buy_2, buy_1;
    private IabHelper mIabHelper;
    private Inventory mInventory = null;

    protected void createSkuList(){
        mSkuList = new ArrayList<String>();
        mSkuList.add(SKU_VIP_1);
        mSkuList.add(SKU_VIP_3);


        mSkuList.add(SKU_100);
        mSkuList.add(SKU_350);


    }

    // Database
    private DatabaseReference userDB;

    private ValueEventListener userListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            //Lấy thông tin của user về và cập nhật lên giao diện

            mCurrentUser = dataSnapshot.getValue(User.class);

             if (mCurrentUser != null){

                 loadInicial();
             }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            //Có lỗi xảy ra, không lấy đc dữ liệu
            Log.e(EditProfileActivity.class.getName(), "loadPost:onCancelled", databaseError.toException());
        }
    };

    public void consumeVip(){
        if(mIabHelper != null && mSkuList != null && mQueryInventoryFinishedListener != null){
            mIabHelper.queryInventoryAsync(true, mSkuList, mQueryInventoryFinishedListener);
        }
    }

    private QueryInventoryFinishedListener mQueryInventoryFinishedListener = null;

    public class QueryInventoryFinishedListener implements IabHelper.QueryInventoryFinishedListener{

        @Override
        public void onQueryInventoryFinished(IabResult result, final Inventory inv) {
            if(result.isFailure()){
                Snackbar.make(mActivityVipLayout, R.string.vip_coulnot, Snackbar.LENGTH_SHORT).show();
            } else {

                if(inv.getPurchase(SKU_100) == null){
                    String vipPrice = inv.getSkuDetails(SKU_100).getPrice();
                    mTextPrice_100.setText(inv.getSkuDetails(SKU_100).getPrice());
                    buy_1.setOnClickListener(StoreActivity.this);
                } else {
                    mIabHelper.consumeAsync(inv.getPurchase(SKU_100), new IabHelper.OnConsumeFinishedListener() {
                        @Override
                        public void onConsumeFinished(Purchase purchase, IabResult result) {
                            if (result.isSuccess()) {

                                // add 100 credit to user account poits!

                                userDB.child("credits").runTransaction(new Transaction.Handler() {
                                    @Override
                                    public Transaction.Result doTransaction(MutableData mutableData) {
                                        int credits = mutableData.getValue(Integer.class);

                                        if (mutableData.getValue() == null){

                                            mutableData.setValue(0);

                                        } else {

                                            mutableData.setValue( credits +100);
                                        }

                                        return Transaction.success(mutableData);
                                    }
                                    @Override
                                    public void onComplete(DatabaseError databaseError, boolean success, DataSnapshot dataSnapshot) {
                                        // Analyse databaseError for any error during increment

                                    }
                                });

                                updateTextView();

                                //mCurrentUser.setVip();
                               // mCurrentUser.saveInBackground();
                                return;

                            } else {

                                String vipPrice = inv.getSkuDetails(SKU_100).getPrice();
                                mTextPrice_100.setText(inv.getSkuDetails(SKU_100).getPrice());
                               // mSellVipButton.setOnClickListener(StoreActivity.this);
                            }
                        }
                    });
                }

                if (inv.getPurchase(SKU_350) == null){
                    String vipPrice = inv.getSkuDetails(SKU_350).getPrice();
                    mTextPrice_350.setText(inv.getSkuDetails(SKU_350).getPrice());
                    buy_2.setOnClickListener(StoreActivity.this);
                } else {
                    mIabHelper.consumeAsync(inv.getPurchase(SKU_350), new IabHelper.OnConsumeFinishedListener() {
                        @Override
                        public void onConsumeFinished(Purchase purchase, IabResult result) {
                            if (result.isSuccess()) {

                                // add 350 credit to user account poits!

                                userDB.child("credits").runTransaction(new Transaction.Handler() {
                                    @Override
                                    public Transaction.Result doTransaction(MutableData mutableData) {
                                        int credits = mutableData.getValue(Integer.class);

                                        if (mutableData.getValue() == null){

                                            mutableData.setValue(0);

                                        } else {

                                            mutableData.setValue( credits + 350);
                                        }

                                        return Transaction.success(mutableData);
                                    }
                                    @Override
                                    public void onComplete(DatabaseError databaseError, boolean success, DataSnapshot dataSnapshot) {
                                        // Analyse databaseError for any error during increment

                                    }
                                });
                                updateTextView();

                                //mCurrentUser.setVip();
                                // mCurrentUser.saveInBackground();
                                return;

                            } else {

                                String vipPrice = inv.getSkuDetails(SKU_350).getPrice();
                                mTextPrice_350.setText(inv.getSkuDetails(SKU_350).getPrice());
                                //mSellVipButton.setOnClickListener(StoreActivity.this);
                            }
                        }
                    });
                }

                if(inv.getPurchase(SKU_VIP_1) == null){
                    String vipPrice = inv.getSkuDetails(SKU_VIP_1).getPrice();
                    mTextPrice_VIP_1.setText(inv.getSkuDetails(SKU_VIP_1).getPrice());
                    button_subscribe1.setOnClickListener(StoreActivity.this);
                } else {
                    mIabHelper.consumeAsync(inv.getPurchase(SKU_VIP_1), new IabHelper.OnConsumeFinishedListener() {
                        @Override
                        public void onConsumeFinished(Purchase purchase, IabResult result) {
                            if (result.isSuccess()) {

                                // add 1180 credit to user account poits!

                                Calendar calendar = Calendar.getInstance();
                                calendar.add(Calendar.DATE, 30);
                                Date expDate = calendar.getTime();

                                userDB.child("membership").setValue("vip");
                                userDB.child("vipEnd").setValue(expDate.getTime());

                                updateTextView();
                                return;

                            } else {

                                String vipPrice = inv.getSkuDetails(SKU_VIP_1).getPrice();
                                mTextPrice_VIP_1.setText(inv.getSkuDetails(SKU_VIP_1).getPrice());
                                //mSellVipButton.setOnClickListener(StoreActivity.this);
                            }
                        }
                    });
                }

                if(inv.getPurchase(SKU_VIP_3) == null){
                    String vipPrice = inv.getSkuDetails(SKU_VIP_3).getPrice();
                    mTextPrice_VIP_3.setText(inv.getSkuDetails(SKU_VIP_3).getPrice());
                    button_subscribe2.setOnClickListener(StoreActivity.this);
                } else {
                    mIabHelper.consumeAsync(inv.getPurchase(SKU_VIP_3), new IabHelper.OnConsumeFinishedListener() {
                        @Override
                        public void onConsumeFinished(Purchase purchase, IabResult result) {
                            if (result.isSuccess()) {

                                // add 1180 credit to user account poits!

                                Calendar calendar = Calendar.getInstance();
                                calendar.add(Calendar.DATE, 90);
                                Date expDate = calendar.getTime();

                                userDB.child("membership").setValue("vip");
                                userDB.child("vipEnd").setValue(expDate.getTime());

                                updateTextView();
                                return;

                            } else {

                                String vipPrice = inv.getSkuDetails(SKU_VIP_3).getPrice();
                                mTextPrice_VIP_3.setText(inv.getSkuDetails(SKU_VIP_3).getPrice());
                                //mSellVipButton.setOnClickListener(StoreActivity.this);
                            }
                        }
                    });
                }


            }
        }
    }


    private Toolbar mToolbar;
    private Drawer drawer;

    private TextView mCredit, mSubscribe;

    private RelativeLayout mActivityVipLayout;

    private  TextView mTextPrice_100, mTextPrice_350, mTextPrice_VIP_1, mTextPrice_VIP_3;

    User mCurrentUser;

    /**
     Current Activity instance will go through its lifecycle to onDestroy() and a new instance then created after it.
     */
    @SuppressLint("NewApi")
    public static final void recreateActivityCompat(final Activity a) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            a.recreate();
        } else {
            final Intent intent = a.getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            a.finish();
            a.overridePendingTransition(0, 0);
            a.startActivity(intent);
            a.overridePendingTransition(0, 0);
        }
    }

    private void updateTextView() {

        recreateActivityCompat(this);

        //Intent refresh = new Intent(this, StoreActivity.class);
        //startActivity(refresh);//Start the same Activity
        //finish(); //finish Activity.

        // set type of account and credits here



    }

    private void loadInicial() {

         if(mCurrentUser.getCredits() > 0) {
            mCredit.setText(mCurrentUser.getCredits()  + getString(R.string.credits_main));
        }
        else {

            mCredit.setText("0" + getString(R.string.credits_main));
        }

        if(mCurrentUser.getIsVip().equals("vip")) {

            mSubscribe.setText(R.string.VIP);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        userDB = FirebaseDatabase.getInstance().getReference("users").child(User.getCurrentUserId());
        userDB.addValueEventListener(userListener);

        mActivityVipLayout = (RelativeLayout) findViewById(R.id.layout_activity_vip);

        mTextPrice_100 = (TextView) findViewById(R.id.text_prince_100);
        mTextPrice_350 = (TextView) findViewById(R.id.text_prince_350);
        mTextPrice_VIP_1 = (TextView) findViewById(R.id.text_prince_vip_1);
        mTextPrice_VIP_3 = (TextView) findViewById(R.id.text_prince_vip_3);



        final Button mFeatturesButton = (Button) findViewById(R.id.button_features);


        buy_1 = (Button)findViewById(R.id.buy_1);
        buy_2 = (Button)findViewById(R.id.buy_2);
        button_subscribe1 = (Button)findViewById(R.id.button_subscribe1);
        button_subscribe2 = (Button)findViewById(R.id.button_subscribe2);

        mCredit = (TextView) findViewById(R.id.text_credit);
        mSubscribe = (TextView) findViewById(R.id.text_subscribe);




        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.vip_store);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setIcon(R.drawable.vip_icon);

        //drawer = NavigationDrawer.createDrawer(this);

        mQueryInventoryFinishedListener = new QueryInventoryFinishedListener();
        




        

        assert mFeatturesButton != null;
        mFeatturesButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), VipActivationActivity.class);
                startActivity(intent);
                finish();

            }
        });



                    String base64EncodedPublicKey = getResources().getString(R.string.play_market_open_key);
                    mIabHelper = new IabHelper(StoreActivity.this, base64EncodedPublicKey);
                    Log.d("myapp","iab constructor done");

                    mIabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                        public void onIabSetupFinished(IabResult result) {
                            Log.d("myapp","iab setup done");
                            if (!result.isSuccess()) {
                                Log.d("myapp", "Problem setting up In-app Billing: " + result);
                                Snackbar.make(mActivityVipLayout, result.toString(), Snackbar.LENGTH_SHORT).show();
                                return;
                            }
                            Log.d("myapp","iab setup done, result success");
                            createSkuList();
                            mIabHelper.queryInventoryAsync(true, mSkuList, new IabHelper.QueryInventoryFinishedListener() {
                                @Override
                                public void onQueryInventoryFinished(IabResult result, Inventory inventory1) {
                                    if (result.isFailure()) {
                                        Snackbar.make(mActivityVipLayout, R.string.vip_srry, Snackbar.LENGTH_SHORT).show();// handle error
                                        return;
                                    } else if (result.isSuccess()) {
//                                        Log.d("myapp", String.format("%s: %s", mSkuList.get(0), inventory1.getSkuDetails(SKU_100).getPrice()));
                                        final Inventory inventory = inventory1;
                                        if (inventory.hasDetails(SKU_100)) {
                                            Log.d("myapp", "get vip price");
                                            if(inventory.getPurchase(SKU_100) != null) {
                                                mIabHelper.consumeAsync(inventory.getPurchase(SKU_100), new IabHelper.OnConsumeFinishedListener() {
                                                    @Override
                                                    public void onConsumeFinished(Purchase purchase, IabResult result) {
                                                        if (result.isSuccess()) {

                                                            userDB.child("credits").runTransaction(new Transaction.Handler() {
                                                                @Override
                                                                public Transaction.Result doTransaction(MutableData mutableData) {
                                                                    int credits = mutableData.getValue(Integer.class);

                                                                    if (mutableData.getValue() == null){

                                                                        mutableData.setValue(0);

                                                                    } else {

                                                                        mutableData.setValue( credits + 100);
                                                                    }

                                                                    return Transaction.success(mutableData);
                                                                }
                                                                @Override
                                                                public void onComplete(DatabaseError databaseError, boolean success, DataSnapshot dataSnapshot) {
                                                                    // Analyse databaseError for any error during increment

                                                                }
                                                            });

                                                            updateTextView();

                                                            // add 100 credit to user account

                                                            //mCurrentUser.setVip();
                                                            //mCurrentUser.saveInBackground();
                                                            return;
                                                        } else {
                                                            String vipPrice = inventory.getSkuDetails(SKU_100).getPrice();
                                                            mTextPrice_100.setText(inventory.getSkuDetails(SKU_100).getPrice());
                                                            buy_1.setOnClickListener(StoreActivity.this);
                                                        }
                                                    }
                                                });
                                            } else {
                                                String vipPrice = inventory.getSkuDetails(SKU_100).getPrice();
                                                mTextPrice_100.setText(inventory.getSkuDetails(SKU_100).getPrice());
                                                buy_1.setOnClickListener(StoreActivity.this);
                                            }
                                        }
                                        if (inventory.hasDetails(SKU_350)) {
                                            Log.d("myapp", "get vip price");
                                            if(inventory.getPurchase(SKU_350) != null) {
                                                mIabHelper.consumeAsync(inventory.getPurchase(SKU_350), new IabHelper.OnConsumeFinishedListener() {
                                                    @Override
                                                    public void onConsumeFinished(Purchase purchase, IabResult result) {
                                                        if (result.isSuccess()) {

                                                            userDB.child("credits").runTransaction(new Transaction.Handler() {
                                                                @Override
                                                                public Transaction.Result doTransaction(MutableData mutableData) {
                                                                    int credits = mutableData.getValue(Integer.class);

                                                                    if (mutableData.getValue() == null){

                                                                        mutableData.setValue(0);

                                                                    } else {

                                                                        mutableData.setValue( credits +350);
                                                                    }

                                                                    return Transaction.success(mutableData);
                                                                }
                                                                @Override
                                                                public void onComplete(DatabaseError databaseError, boolean success, DataSnapshot dataSnapshot) {
                                                                    // Analyse databaseError for any error during increment

                                                                }
                                                            });

                                                            updateTextView();

                                                            // add 350 credit to user account

                                                            //mCurrentUser.setVip();
                                                           // mCurrentUser.saveInBackground();
                                                            return;
                                                        } else {
                                                            String vipPrice = inventory.getSkuDetails(SKU_350).getPrice();
                                                            mTextPrice_350.setText(inventory.getSkuDetails(SKU_350).getPrice());
                                                            buy_2.setOnClickListener(StoreActivity.this);
                                                        }
                                                    }
                                                });
                                            } else {
                                                String vipPrice = inventory.getSkuDetails(SKU_350).getPrice();
                                                mTextPrice_350.setText(inventory.getSkuDetails(SKU_350).getPrice());
                                                buy_2.setOnClickListener(StoreActivity.this);
                                            }
                                        }

                                        if (inventory.hasDetails(SKU_VIP_1)) {
                                            Log.d("myapp", "get vip price");
                                            if(inventory.getPurchase(SKU_VIP_1) != null) {
                                                mIabHelper.consumeAsync(inventory.getPurchase(SKU_VIP_1), new IabHelper.OnConsumeFinishedListener() {
                                                    @Override
                                                    public void onConsumeFinished(Purchase purchase, IabResult result) {
                                                        if (result.isSuccess()) {

                                                            // set user pro, and set end date if possible

                                                            Calendar calendar = Calendar.getInstance();
                                                            calendar.add(Calendar.DATE, 30);
                                                            Date expDate = calendar.getTime();

                                                            userDB.child("membership").setValue("vip");
                                                            userDB.child("vipEnd").setValue(expDate.getTime());

                                                            updateTextView();
                                                            return;
                                                        } else {
                                                            String vipPrice = inventory.getSkuDetails(SKU_VIP_1).getPrice();
                                                            mTextPrice_VIP_1.setText(inventory.getSkuDetails(SKU_VIP_1).getPrice());
                                                            button_subscribe1.setOnClickListener(StoreActivity.this);
                                                        }
                                                    }
                                                });
                                            } else {
                                                String vipPrice = inventory.getSkuDetails(SKU_VIP_1).getPrice();
                                                mTextPrice_VIP_1.setText(inventory.getSkuDetails(SKU_VIP_1).getPrice());
                                                button_subscribe1.setOnClickListener(StoreActivity.this);
                                            }
                                        }

                                        if (inventory.hasDetails(SKU_VIP_3)) {
                                            Log.d("myapp", "get vip price");
                                            if(inventory.getPurchase(SKU_VIP_3) != null) {
                                                mIabHelper.consumeAsync(inventory.getPurchase(SKU_VIP_3), new IabHelper.OnConsumeFinishedListener() {
                                                    @Override
                                                    public void onConsumeFinished(Purchase purchase, IabResult result) {
                                                        if (result.isSuccess()) {

                                                            // set user pro, and set end date if possible

                                                            Calendar calendar = Calendar.getInstance();
                                                            calendar.add(Calendar.DATE, 90);
                                                            Date expDate = calendar.getTime();

                                                            userDB.child("membership").setValue("vip");
                                                            userDB.child("vipEnd").setValue(expDate.getTime());

                                                            updateTextView();
                                                            return;
                                                        } else {
                                                            String vipPrice = inventory.getSkuDetails(SKU_VIP_3).getPrice();
                                                            mTextPrice_VIP_3.setText(inventory.getSkuDetails(SKU_VIP_3).getPrice());
                                                            button_subscribe2.setOnClickListener(StoreActivity.this);
                                                        }
                                                    }
                                                });
                                            } else {
                                                String vipPrice = inventory.getSkuDetails(SKU_VIP_3).getPrice();
                                                mTextPrice_VIP_3.setText(inventory.getSkuDetails(SKU_VIP_3).getPrice());
                                                button_subscribe2.setOnClickListener(StoreActivity.this);
                                            }
                                        }


                                    }
                                }
                            });
                        }
                    });
        }

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        super.onStop();
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
        return 9;
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {

    }

    @Override
    public void onSizeChanged(int height) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
     if (mIabHelper != null) mIabHelper.dispose();
        mIabHelper = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            
            case R.id.buy_1:

                mIabHelper.launchPurchaseFlow(this, SKU_100, RC_VIP_SELL,
                        this, mCurrentUser.getUid());
                break;

            case R.id.buy_2:

                mIabHelper.launchPurchaseFlow(this, SKU_350, RC_VIP_SELL,
                        this, mCurrentUser.getUid());
                break;

            // subscription here

            case R.id.button_subscribe1:

                mIabHelper.launchSubscriptionPurchaseFlow(this, SKU_VIP_1, RC_VIP_SELL,
                        this, mCurrentUser.getUid());
                break;

            case R.id.button_subscribe2:

                mIabHelper.launchSubscriptionPurchaseFlow(this, SKU_VIP_3, RC_VIP_SELL,
                        this, mCurrentUser.getUid());
                break;

        }
    }

    public void onIabPurchaseFinished(IabResult result, Purchase info) {

        if(result.isFailure()){
            Log.d("myapp", "Error purchasing: " + result);
            return;
        }

        if(TextUtils.equals(info.getSku(), SKU_100)){

            showProgressBar("Purchasing...");

            userDB.child("credits").runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    int credits = mutableData.getValue(Integer.class);

                    if (mutableData.getValue() == null){

                        mutableData.setValue(0);

                    } else {

                        mutableData.setValue( credits +100);
                    }

                    return Transaction.success(mutableData);
                }
                @Override
                public void onComplete(DatabaseError databaseError, boolean success, DataSnapshot dataSnapshot) {
                    // Analyse databaseError for any error during increment

                    if (success) {

                        dismissProgressBar();

                        Snackbar.make(mActivityVipLayout, R.string.credit_100_buyed, Snackbar.LENGTH_SHORT).show();
                    }

                }
            });

        }
        if(TextUtils.equals(info.getSku(), SKU_350)){

            showProgressBar("Purchasing...");


            userDB.child("credits").runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    int credits = mutableData.getValue(Integer.class);

                    if (mutableData.getValue() == null){

                        mutableData.setValue(0);

                    } else {

                        mutableData.setValue( credits +350);
                    }

                    return Transaction.success(mutableData);
                }
                @Override
                public void onComplete(DatabaseError databaseError, boolean success, DataSnapshot dataSnapshot) {
                    // Analyse databaseError for any error during increment

                    if (success) {

                        dismissProgressBar();

                        Snackbar.make(mActivityVipLayout, R.string.credit_350_buyed, Snackbar.LENGTH_SHORT).show();
                    }

                }
            });

        }


        if(TextUtils.equals(info.getSku(), SKU_VIP_1)){

            showProgressBar("Purchasing...");

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 30);
            Date expDate = calendar.getTime();

            userDB.child("membership").setValue("vip");
            userDB.child("vipEnd").setValue(expDate.getTime()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()){

                        dismissProgressBar();

                        Snackbar.make(mActivityVipLayout, R.string.vip_now_con, Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if(TextUtils.equals(info.getSku(), SKU_VIP_3)){

            showProgressBar("Purchasing...");

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 90);
            Date expDate = calendar.getTime();

            userDB.child("membership").setValue("vip");
            userDB.child("vipEnd").setValue(expDate.getTime()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()){

                        dismissProgressBar();

                        Snackbar.make(mActivityVipLayout, R.string.vip_now_con, Snackbar.LENGTH_SHORT).show();
                    }
                }
            });

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
    protected void onPause() {
        super.onPause();
        Log.d("main111", "onPause");
    }

    public void showProgressBar(String message){
        progressDialog = ProgressDialog.show(this, "", message, true);
    }
    public void dismissProgressBar() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }



}
