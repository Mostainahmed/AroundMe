package com.aroundme.mostain.AroundMe.Profile;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aroundme.mostain.App.BaseActivity;
import com.aroundme.mostain.App.NavigationDrawer;
import com.aroundme.mostain.AroundMe.Messaging.Activity.ImageViewerActivity;
import com.aroundme.mostain.Class.User;
import com.aroundme.mostain.R;
import com.aroundme.mostain.Utils.Helper.ActivityWithToolbar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.MenuSheetView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.Drawer;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class MyProfile extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, ActivityWithToolbar, View.OnClickListener {

    @BindView(R.id.fullname)
    TextView fullname;
    @BindView(R.id.username)
    TextView username;
    //@BindView(R.id.header_cover_image) ImageView ivUserCoverPhoto;
    @BindView(R.id.ivUserProfilePhoto)
    CircleImageView ivUserProfilePhoto;
    @BindView(R.id.description)
    TextView gender;
    @BindView(R.id.posts_count) TextView posts_count;
    @BindView(R.id.followers_count)
    TextView followers_count;
    @BindView(R.id.following_count)
    TextView following_count;

    @BindView(R.id.imageView6)
    ImageView imLocation;

    @BindView(R.id.imageView7)
    ImageView imStatus;

    @BindView(R.id.imageView10)
    ImageView imOrientation;

    @BindView(R.id.imageView8)
    ImageView imSexuality;


    @BindView(R.id.desc)
    TextView mDescriptionText;

    @BindView(R.id.birthday)
    TextView mBirthday;

    @BindView(R.id.location)
    TextView mlocation;

    @BindView(R.id.status)
    TextView mStatus;

    @BindView(R.id.oriantation)
    TextView mOriantation;

    @BindView(R.id.sexuality)
    TextView msexuality;

    User mCurrentUser;
    private Toolbar mToolbar;
    private Drawer drawer;
    private Dialog progressDialog;
    FirebaseAuth mAuth;

    private DatabaseReference userDB;

    private GoogleApiClient mGoogleApiClient;
    private int PLACE_PICKER_REQUEST = 1;

    protected BottomSheetLayout bottomSheetLayout;


    public MyProfile() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_my_profile);

        ButterKnife.bind(this);

        mCurrentUser = new User();

        mToolbar = (Toolbar)findViewById(R.id.toolbar);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();

        bottomSheetLayout = (BottomSheetLayout) findViewById(R.id.bottomsheet);
        bottomSheetLayout.setPeekOnDismiss(true);

        mAuth = FirebaseAuth.getInstance();


        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.profile_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawer = NavigationDrawer.createDrawer(this);

        userDB = FirebaseDatabase.getInstance().getReference("users").child(User.getCurrentUserId());
        //userDB.addListenerForSingleValueEvent(userListener);


        final DatabaseReference userFacebook = FirebaseDatabase.getInstance().getReference("users").child(User.getCurrentUserId());
        userFacebook.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mCurrentUser = dataSnapshot.getValue(User.class);

                if (mCurrentUser != null){

                    if (mAuth.getCurrentUser().getPhotoUrl() == null) {

                        ivUserProfilePhoto.setImageResource(R.drawable.profile_default_photo);

                    } else {

                        Glide.with(getApplicationContext())
                                .load(mCurrentUser.getPhotoUrl())
                                .asBitmap()
                                .error(R.drawable.profile_default_photo)
                                .placeholder(R.drawable.profile_default_photo)
                                .fitCenter()
                                .into(new SimpleTarget<Bitmap>(1024, 1024) {
                                    @Override
                                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                        ivUserProfilePhoto.setImageBitmap(resource);
                                    }
                                });

                    }

                    // User profile info


                    fullname.setText(mCurrentUser.getName());

                    if (mCurrentUser.getUsername() != null){

                        username.setText(String.format("@%s",mCurrentUser.getUsername().trim()));
                    }


                    if (mCurrentUser.getisMale()){

                        gender.setText("Male");

                    } else if (!mCurrentUser.getisMale()){

                        gender.setText("Female");
                    }

                    mDescriptionText.setText(mCurrentUser.getdesc());


                    if (mCurrentUser.getbirthdate() != 0){

                        LocalDate birthdate = new LocalDate(mCurrentUser.getbirthdate());          //Birth date
                        LocalDate now = new LocalDate();                                         //Today's date
                        Period period = new Period(birthdate,now, PeriodType.yearMonthDay());

                        //int ages = period.getYears();
                        final Integer ageInt = period.getYears();
                        final String ageS = ageInt.toString();


                        Date date = new Date(mCurrentUser.getbirthdate()); // *1000 is to convert seconds to milliseconds
                        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy "); // the format of your date

                        String birthday = sdf.format(date);

                        mBirthday.setText(birthday);
                        followers_count.setText(ageS);

                       // mCurrentUser.setAge(Integer.parseInt((ageS)));

                        //mBirthday.setText(mCurrentUser.getbirthday());



                    } else {

                        mBirthday.setText(R.string.indefined);
                        followers_count.setText("18 +");
                    }

                    following_count.setText(String.valueOf( mCurrentUser.getCredits()));

                    ivUserProfilePhoto.setOnClickListener(new Button.OnClickListener() {
                        public void onClick(View v) {


                            if (mCurrentUser.getUid() != null){

                                Intent imageViewerIntent = new Intent(MyProfile.this, ImageViewerActivity.class);
                                imageViewerIntent.putExtra(ImageViewerActivity.EXTRA_IMAGE_URL, mCurrentUser.getPhotoUrl());
                                MyProfile.this.startActivity(imageViewerIntent);


                            } else {

                                Toast.makeText(MyProfile.this, "No Image found", Toast.LENGTH_SHORT).show();
                            }


                        }
                    });

                    /*if (mCurrentUser.getAge() < 0) {

                        //followers_count.setText(ageS);
                        followers_count.setText("18 +");

                    } *///else followers_count.setText("18 +");

                    // Country and City
                    /*if (mCurrentUser.getcountry() != null && mCurrentUser.getactualcity() != null ){

                        mlocation.setText(mCurrentUser.getcountry() + ", " + mCurrentUser.getactualcity());
                    }

                    // Only country
                    else if (mCurrentUser.getcountry() != null && mCurrentUser.getactualcity() == null ) {

                        mlocation.setText(mCurrentUser.getcountry());

                        // Only city
                    } else if (mCurrentUser.getcountry() == null && mCurrentUser.getactualcity() != null ){

                        mlocation.setText(mCurrentUser.getactualcity());

                        // Both are null
                    } else mlocation.setText("Ask me");*/

                    if (mCurrentUser.getactualcity() != null ){

                        mlocation.setText(mCurrentUser.getactualcity());

                        // Both are null
                    } else mlocation.setText(R.string.indefined);


                    if(mCurrentUser.getstatus() == 0){

                        mStatus.setText(R.string.married);

                    } else if(mCurrentUser.getstatus() == 1)

                    {
                        mStatus.setText(R.string.dating);

                    }
                    else if(mCurrentUser.getstatus() == 2)

                    {
                        mStatus.setText(R.string.sigle);

                    } else {

                        mStatus.setText(R.string.indefined);
                    }
                    // Orientation

                    if(mCurrentUser.getorientation() == 0){

                        mOriantation.setText(R.string.heterosexual);

                    } else if(mCurrentUser.getorientation() == 1)

                    {
                        mOriantation.setText(R.string.homosexual);

                    }
                    else if(mCurrentUser.getorientation() == 2)

                    {
                        mOriantation.setText(R.string.bisexual);

                    }
                    else {

                        mOriantation.setText(R.string.indefined);

                    }

                    // Sexuality

                    if(mCurrentUser.getsexuality() == 0){

                        msexuality.setText(R.string.mans);

                    } else if(mCurrentUser.getsexuality() == 1)

                    {
                        msexuality.setText(R.string.girls);

                    }
                    else if(mCurrentUser.getsexuality() == 2)

                    {
                        msexuality.setText(R.string.both);

                    } else {

                        msexuality.setText(R.string.indefined);


                    }
                }



                // Edit profile data

                imLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        displayPlacePicker();

                    }
                });

                imStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        showMenuSheetStatus(MenuSheetView.MenuType.LIST);

                    }
                });

                imOrientation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        showMenuSheetOrientation(MenuSheetView.MenuType.LIST);

                    }
                });

                imSexuality.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        showMenuSheetSexuality(MenuSheetView.MenuType.LIST);

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void displayPlacePicker() {
        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected())
            return;

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this),PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            Log.d("PlacesAPI Demo","GooglePlayServicesRepairableException thrown");
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.d("PlacesAPI Demo","GooglePlayServicesNotAvailableException thrown");
        }
    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            displayPlace(PlacePicker.getPlace(data,this));
        }

    }

    private void displayPlace(Place place) {
        if (place == null)
            return;

        if (!place.getName().toString().matches("[a-zA-Z.? ]*")) {

            //Toast.makeText(MyProfile.this, place.getAddress(), Toast.LENGTH_SHORT).show();
            userDB.child("actualcity").setValue(place.getAddress());
            //userDB.child("actualcity").setValue(place.getAddress().toString());
            //userDB.child("country").setValue(place.getAddress().toString());

        } else {

            userDB.child("actualcity").setValue(place.getAddress());
            //userDB.child("country").setValue(place.getLocale().getDisplayCountry());

        }

    }

    private void showMenuSheetStatus(final MenuSheetView.MenuType menuType) {
        MenuSheetView menuSheetView =
                new MenuSheetView(MyProfile.this, menuType, "What is your marital status ?", new MenuSheetView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(MyProfile.this, "Your status is now" + " " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        if (bottomSheetLayout.isSheetShowing()) {
                            bottomSheetLayout.dismissSheet();
                        }
                        if (item.getItemId() == R.id.married) {

                            userDB.child("status").setValue(0);

                        } else if (item.getItemId() == R.id.dating) {

                            userDB.child("status").setValue(1);

                        } else if (item.getItemId() == R.id.sigle) {

                            userDB.child("status").setValue(2);
                        }

                        return true;
                    }
                });
        menuSheetView.inflateMenu(R.menu.create_status);
        bottomSheetLayout.showWithSheetView(menuSheetView);
    }

    private void showMenuSheetOrientation(final MenuSheetView.MenuType menuType) {
        MenuSheetView menuSheetView =
                new MenuSheetView(MyProfile.this, menuType, "What is your sexual orientation ?", new MenuSheetView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(MyProfile.this, "Your sexual orientation is now" + " " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        if (bottomSheetLayout.isSheetShowing()) {
                            bottomSheetLayout.dismissSheet();
                        }
                        if (item.getItemId() == R.id.heterosexual) {

                            userDB.child("orientation").setValue(0);

                        } else if (item.getItemId() == R.id.homosexual) {

                            userDB.child("orientation").setValue(1);

                        } else if (item.getItemId() == R.id.bisexual) {

                            userDB.child("orientation").setValue(2);
                        }

                        return true;
                    }
                });
        menuSheetView.inflateMenu(R.menu.create_orientation);
        bottomSheetLayout.showWithSheetView(menuSheetView);
    }

    private void showMenuSheetSexuality(final MenuSheetView.MenuType menuType) {
        MenuSheetView menuSheetView =
                new MenuSheetView(MyProfile.this, menuType, "What is your sexual preferences", new MenuSheetView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(MyProfile.this, "Your sexual preferences is now" + " " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        if (bottomSheetLayout.isSheetShowing()) {
                            bottomSheetLayout.dismissSheet();
                        }
                        if (item.getItemId() == R.id.mans) {

                            userDB.child("sexuality").setValue(0);

                        } else if (item.getItemId() == R.id.girls) {

                            userDB.child("sexuality").setValue(1);

                        } else if (item.getItemId() == R.id.both) {

                            userDB.child("sexuality").setValue(2);
                        }

                        return true;
                    }
                });
        menuSheetView.inflateMenu(R.menu.create_sexuality);
        bottomSheetLayout.showWithSheetView(menuSheetView);
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

    public void showProgressBar(String message){
        progressDialog = ProgressDialog.show(this, "", message, true);
    }
    public void dismissProgressBar() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_profile:
                // User chose the "Settings" item, show the app settings UI...

                Intent i2 = new Intent(getActivity(),EditProfileActivity.class);
                startActivity(i2);

                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override protected void onPause() {
        super.onPause();
        setUserOnline(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUserOnline(true);
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

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
    }
}
