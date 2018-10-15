package com.aroundme.mostain.AroundMe.NearMe;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.angopapo.aroundme2.App.BaseActivity;
import com.angopapo.aroundme2.App.DispatchActivity;
import com.angopapo.aroundme2.App.NavigationDrawer;
import com.angopapo.aroundme2.AroundMe.Location.LocationActivity;
import com.angopapo.aroundme2.AroundMe.NearMe.Both.UsersBothFragment;
import com.angopapo.aroundme2.AroundMe.NearMe.Female.UsersFemaleFragment;
import com.angopapo.aroundme2.AroundMe.NearMe.Male.UsersMaleFragment;
import com.angopapo.aroundme2.AroundMe.NearMe.Matchs.UsersMatchsFragment;
import com.angopapo.aroundme2.AroundMe.Profile.EditProfileActivity;
import com.angopapo.aroundme2.BuildConfig;
import com.angopapo.aroundme2.Class.User;
import com.angopapo.aroundme2.R;
import com.angopapo.aroundme2.Utils.Helper.ActivityWithToolbar;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.mikepenz.materialdrawer.Drawer;


public class AroundMeActivity extends BaseActivity implements ActivityWithToolbar,
        UsersFemaleFragment.OnFragmentInteractionListener,
        UsersMatchsFragment.OnFragmentInteractionListener,
        UsersBothFragment.OnFragmentInteractionListener,
        UsersMaleFragment.OnFragmentInteractionListener,
        NearMeFragment.OnFragmentInteractionListener
{

    User CurrentUser;
    private Fragment fragment;
    private FragmentManager fragmentManager;
    private boolean doubleBackToExitPressedOnce;

    private Toolbar mToolbar;
    private Drawer drawer;

    public CountDownTimer updateOnline;

    //protected BottomSheetLayout bottomSheetLayout;

    // Remote Config keys
    private static final String LOADING_PHRASE_CONFIG_KEY = "loading_phrase";
    private static final String WELCOME_MESSAGE_KEY = "welcome_message";
    private static final String WELCOME_MESSAGE_CAPS_KEY = "welcome_message_caps";

    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private TextView mWelcomeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aroundme);


        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        //bottomSheetLayout = (BottomSheetLayout) findViewById(R.id.bottomsheet);

        CurrentUser = new User();

       ////////////

        /*updateOnline = new CountDownTimer(System.currentTimeMillis(), ServiceUtils.TIME_TO_REFRESH) {
            @Override
            public void onTick(long l) {
                ServiceUtils.updateUserStatus(getApplicationContext());
            }

            @Override
            public void onFinish() {

            }
        };
        updateOnline.start();*/

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.drawer_item_around_me);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawer = NavigationDrawer.createDrawer(this);


        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        //fetchWelcome();


        /////////////



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        final DatabaseReference userFacebook = FirebaseDatabase.getInstance().getReference("users").child(User.getCurrentUserId());
        userFacebook.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                CurrentUser = dataSnapshot.getValue(User.class);



                if (CurrentUser != null){

                    if (CurrentUser.getUid() == null){

                        userFacebook.child(User.getuid).setValue(user.getUid());

                    }

                    checkInfo ();

                } else {

                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    mAuth.signOut();
                    LoginManager.getInstance().logOut();

                    startLoginActivity();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        fragmentManager = getSupportFragmentManager();
        fragment = new NearMeFragment();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.main_container,fragment).commit();



    }

    /**
     * Fetch a welcome message from the Remote Config service, and then activate it.
     */
    /*private void fetchWelcome() {
        mWelcomeTextView.setText(mFirebaseRemoteConfig.getString(LOADING_PHRASE_CONFIG_KEY));

        long cacheExpiration = 3600; // 1 hour in seconds.
        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AroundMeActivity.this, "Fetch Succeeded", Toast.LENGTH_SHORT).show();

                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            Toast.makeText(AroundMeActivity.this, "Fetch Failed", Toast.LENGTH_SHORT).show();
                        }
                        displayWelcomeMessage();
                    }
                });

    }*/


  /*  // [START display_welcome_message]
    private void displayWelcomeMessage() {
        // [START get_config_values]
        String welcomeMessage = mFirebaseRemoteConfig.getString(WELCOME_MESSAGE_KEY);
        // [END get_config_values]
        if (mFirebaseRemoteConfig.getBoolean(WELCOME_MESSAGE_CAPS_KEY)) {
            mWelcomeTextView.setAllCaps(true);
        } else {
            mWelcomeTextView.setAllCaps(false);
        }
        mWelcomeTextView.setText(welcomeMessage);
    }*/
    // [END display_welcome_message]

    private void startLoginActivity() {
        Intent intent = new Intent(this, DispatchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void checkInfo (){

        if (!AroundMeActivity.this.isFinishing()){

        assert CurrentUser != null;
        if (CurrentUser.getlat() == null && CurrentUser.getlog() == null) {

            new android.support.v7.app.AlertDialog.Builder(AroundMeActivity.this)
                    .setTitle("No location found")
                    .setMessage("This app needs the Location updated, please update your to use this app in full.")
                    .setCancelable(false)
                    .setPositiveButton("OK",(dialogInterface,i) -> {

                        Intent mainIntent = new Intent(AroundMeActivity.this, LocationActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);
                        finish();
                    })
                    .create()
                    .show();

        } else

        if (CurrentUser.getName() == null) {

            new android.support.v7.app.AlertDialog.Builder(AroundMeActivity.this)
                    .setTitle("Profile Incomplete")
                    .setMessage("Please complete your profile to continue using to app.")
                    .setCancelable(false)
                    .setPositiveButton("OK",(dialogInterface,i) -> {

                        Intent mainIntent = new Intent(AroundMeActivity.this, EditProfileActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);
                        finish();


                    })
                    .create()
                    .show();
            }

        }


    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();

        } else {

            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
            }
            this.doubleBackToExitPressedOnce = true;

            Toast.makeText(this,R.string.login_press_again,Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false,2000);
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
        return 1;
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {

    }

    @Override
    public void onSizeChanged(int height) {

    }

    /*@Override protected void onStart() {
        super.onStart();
        setUserOnline(true);
    }

    @Override protected void onPause() {
        super.onPause();
        setUserOnline(false);
    }

    private void setUserOnline(final boolean online) {
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(User.Class).child(User.getCurrentUserId());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    userRef.child(User.Online).setValue(online);

                }
            }

            @Override public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_aroundme, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update_location:
                // User chose the "Settings" item, show the app settings UI...

                Intent i2 = new Intent(getActivity(), LocationActivity.class);
                startActivity(i2);

                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}
