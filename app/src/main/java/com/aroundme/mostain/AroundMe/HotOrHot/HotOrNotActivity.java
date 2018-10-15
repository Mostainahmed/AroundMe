package com.aroundme.mostain.AroundMe.HotOrHot;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aroundme.mostain.Adapters.HotOrnotAdapter;
import com.aroundme.mostain.App.BaseActivity;
import com.aroundme.mostain.App.NavigationDrawer;
import com.aroundme.mostain.AroundMe.Profile.EditProfileActivity;
import com.aroundme.mostain.AroundMe.Profile.UserProfile;
import com.aroundme.mostain.Class.User;
import com.aroundme.mostain.R;
import com.aroundme.mostain.Utils.Helper.ActivityWithToolbar;
import com.aroundme.mostain.Utils.Internet.CheckServer;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.mikepenz.materialdrawer.Drawer;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HotOrNotActivity extends BaseActivity implements ActivityWithToolbar {
    private User cards_data[];
    private HotOrnotAdapter hotOrnotAdapter;
    private int i;

    private FirebaseAuth mAuth;

    private CheckServer mCheckServer;

    private String currentUId;

    private DatabaseReference usersDb;

    String CurrentObject;


    ListView listView;
    List<User> rowItems;

    private Drawer drawer;
    private Toolbar mToolbar;
    private ImageView mAbortImage, mMatchImage, mMatcthInfo;
    private LinearLayout mUserNotFoundLayout;
    private LinearLayout control_wrapper;
    private RelativeLayout mSwiper;
    private TextView mLook;
    private CircleImageView imageView;
    private RippleBackground mRipple;

    private LinearLayout mInternet;
    private TextView mIntenetText;

    private User mCurrentUser;
    private DatabaseReference userDB;

    private ValueEventListener userListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Generally
            mCurrentUser = dataSnapshot.getValue(User.class);


                if (mCurrentUser.getPhotoUrl() == null){

                    imageView.setImageResource(R.drawable.profile_default_photo);


                } else {

                    Glide.with(getApplicationContext())
                            .load(mCurrentUser.getPhotoThumb())
                            .placeholder(R.drawable.profile_default_photo)
                            .crossFade()
                            .centerCrop()
                            .error(R.drawable.profile_default_photo)
                            .into(imageView);

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
        setContentView(R.layout.activity_hot_or_not);

        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mSwiper = (RelativeLayout) findViewById(R.id.swipe);
        mAbortImage = (ImageView) findViewById(R.id.image_abort);
        mMatchImage = (ImageView) findViewById(R.id.image_match);
        mMatcthInfo = (ImageView) findViewById(R.id.image_info);
        mUserNotFoundLayout = (LinearLayout) findViewById(R.id.noUsersFound);
        control_wrapper = (LinearLayout) findViewById(R.id.control_wrapper);
        mLook = (TextView) findViewById(R.id.look_id);
        mRipple = (RippleBackground) findViewById(R.id.content);
        mCheckServer = (CheckServer) findViewById(R.id.wait_for_internet_connection);
        mInternet = (LinearLayout) findViewById(R.id.linearLayout22);
        mIntenetText = (TextView) findViewById(R.id.mIntenetText);

        getActivity().registerReceiver(this.mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.drawer_item_hot_or_not);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawer = NavigationDrawer.createDrawer(this);

        //rippleBackground = (RippleBackground) findViewById(R.id.content);
        imageView = (CircleImageView) findViewById(R.id.profile_photo);

        userDB = FirebaseDatabase.getInstance().getReference("users").child(User.getCurrentUserId());
        userDB.addValueEventListener(userListener);

        mRipple.startRippleAnimation();


        usersDb = FirebaseDatabase.getInstance().getReference().child("users");

        mAuth = FirebaseAuth.getInstance();
        currentUId = mAuth.getCurrentUser().getUid();

        getpossibleUsers();

        rowItems = new ArrayList<User>();

        hotOrnotAdapter = new HotOrnotAdapter(this, R.layout.item, rowItems );

        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

        flingContainer.setAdapter(hotOrnotAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                hotOrnotAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {

                User obj = (User) dataObject;
                String userId = obj.getUid();
                CurrentObject = obj.getUid();

                usersDb.child(userId).child("connections").child(currentUId).setValue(false);

                usersDb.child(userId).child("connections").child("likesMe").child(User.getUser().getUid()).setValue(false);
                usersDb.child(User.getUser().getUid()).child("connections").child("iLikes").child(userId).setValue(false);
                //Toast.makeText(HotOrNotActivity.this, "Nope", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {

                User obj = (User) dataObject;
                String userId = obj.getUid();
                CurrentObject = obj.getUid();

                usersDb.child(userId).child("connections").child(currentUId).setValue(true);
                usersDb.child(userId).child("connections").child("likesMe").child(User.getUser().getUid()).setValue(true);
                usersDb.child(User.getUser().getUid()).child("connections").child("iLikes").child(userId).setValue(true);
                //Toast.makeText(HotOrNotActivity.this, "Like", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {

                /*mLook.setText("No user was found in your area please update location or use Passport");
                mLook.setTextColor(Color.RED);
                showRipple();
                hideCards();
                hideUserNotFound();
                hideControlWrapper();*/

            }

            @Override
            public void onScroll(float scrollProgressPercent) {


            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                //Toast.makeText(HotOrNotActivity.this, "Item Clicked", Toast.LENGTH_SHORT).show();
                User obj = (User) dataObject;
                String userId = obj.getUid();
                CurrentObject = obj.getUid();

                Intent userDetailIntent = new Intent(HotOrNotActivity.this, UserProfile.class);
                userDetailIntent.putExtra(UserProfile.USER_ID_EXTRA_NAME, userId);
                HotOrNotActivity.this.startActivity(userDetailIntent);

            }
        });

        mMatchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*usersDb.child(CurrentObject).child("connections").child(currentUId).setValue(true);
                Toast.makeText(HotOrNotActivity.this, "Like", Toast.LENGTH_SHORT).show();

                rowItems.remove(0);
                HotOrnotAdapter.notifyDataSetChanged();*/
            }
        });

        mAbortImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*usersDb.child(CurrentObject).child("connections").child(currentUId).setValue(false);
                Toast.makeText(HotOrNotActivity.this, "Nope", Toast.LENGTH_SHORT).show();

                rowItems.remove(0);
                HotOrnotAdapter.notifyDataSetChanged();*/
            }
        });

        mMatcthInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*Intent userDetailIntent = new Intent(HotOrNotActivity.this, UserProfile.class);
                userDetailIntent.putExtra(UserProfile.USER_ID_EXTRA_NAME, CurrentObject);
                HotOrNotActivity.this.startActivity(userDetailIntent);*/

            }
        });

        //checkUserSex();



    }

    public void getpossibleUsers(){

        DatabaseReference possibleUsers = FirebaseDatabase.getInstance().getReference().child("users");
        possibleUsers.keepSynced(true);
        possibleUsers.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists() ){

                    if (!dataSnapshot.child("connections").hasChild(currentUId)){

                        User item = dataSnapshot.getValue(User.class);

                        hideRipple();
                        hideUserNotFound();
                        showCards();
                        hideControlWrapper();

                        if (item != null && item.getPhotoUrl() != null) {

                            rowItems.add(item);
                            hotOrnotAdapter.notifyDataSetChanged();

                        }

                    } else {

                        mLook.setText("No user was found in your area please update location or use Passport");
                        mLook.setTextColor(Color.RED);
                        showRipple();
                        hideCards();
                        hideUserNotFound();
                        hideControlWrapper();
                    }


                } else {

                    mLook.setText("No user was found in your area please update location or use Passport");
                    mLook.setTextColor(Color.RED);
                    showRipple();
                    hideCards();
                    hideUserNotFound();
                    hideControlWrapper();
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {


                hotOrnotAdapter.notifyDataSetChanged();

            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                hotOrnotAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                hotOrnotAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
            boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

            NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

            if(currentNetworkInfo.isConnected()){

                mIntenetText.setText("Connecting...");
                mIntenetText.setTextColor(Color.WHITE);
                mInternet.setBackgroundColor(Color.GRAY);


                mCheckServer.checkInternetConnection(new CheckServer.OnConnectionIsAvailableListener() {
                    @Override
                    public void onConnectionIsAvailable() {

                        mIntenetText.setText("Connected");
                        mIntenetText.setTextColor(Color.WHITE);
                        mInternet.setBackgroundColor(Color.GREEN);
                        mInternet.setVisibility(View.GONE);

                        mCheckServer.close();

                    }
                });

            }else{

                mInternet.setBackgroundColor(Color.RED);
                mInternet.setVisibility(View.VISIBLE);
                mIntenetText.setText("No internet connection");
                mIntenetText.setTextColor(Color.WHITE);



            }
        }
    };


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
        return 2;
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {

    }

    @Override
    public void onSizeChanged(int height) {

    }

    public void showUserNotFound() {
        mUserNotFoundLayout.setVisibility(View.VISIBLE);
    }

    public void hideUserNotFound() {
        mUserNotFoundLayout.setVisibility(View.GONE);
    }

    public void showControlWrapper() {
        control_wrapper.setVisibility(View.VISIBLE);
    }

    public void hideControlWrapper() {
        control_wrapper.setVisibility(View.GONE);
    }

    public void showCards() {
        mSwiper.setVisibility(View.VISIBLE);
    }

    public void hideCards() {
        mSwiper.setVisibility(View.GONE);
    }

    public void showRipple() {
        mRipple.setVisibility(View.VISIBLE);
    }

    public void hideRipple() {
        mRipple.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.report_profile:
                // User chose the "Settings" item, show the app settings UI...

                /*Intent i2 = new Intent(getActivity(),EditProfileActivity.class);
                startActivity(i2);*/

                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onRestart() {
        super.onRestart();


        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);

    }


}