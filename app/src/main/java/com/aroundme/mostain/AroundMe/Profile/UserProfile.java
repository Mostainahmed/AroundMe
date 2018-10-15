package com.aroundme.mostain.AroundMe.Profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.aroundme.mostain.App.BaseActivity;
import com.aroundme.mostain.AroundMe.Messaging.Activity.ChatActivity;
import com.aroundme.mostain.AroundMe.Messaging.Activity.ImageViewerActivity;
import com.aroundme.mostain.Class.Report;
import com.aroundme.mostain.Class.User;
import com.aroundme.mostain.R;
import com.aroundme.mostain.Utils.Helper.ActivityWithToolbar;
import com.aroundme.mostain.Utils.service.Constants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
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


public class UserProfile extends BaseActivity implements ActivityWithToolbar, View.OnClickListener {

    public static final String USER_ID_EXTRA_NAME = "userID";
    private String mUserId;

    @BindView(R.id.fullname)
    TextView fullname;
    @BindView(R.id.username)
    TextView username;
    //@BindView(R.id.header_cover_image) ImageView ivUserCoverPhoto;
    @BindView(R.id.ivUserProfilePhoto)
    CircleImageView ivUserProfilePhoto;
    @BindView(R.id.chat) Button mButtonChat;
    @BindView(R.id.posts_count) TextView posts_count;
    @BindView(R.id.followers_count)
    TextView followers_count;
    @BindView(R.id.following_count)
    TextView following_count;

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
    private boolean Userblocked = false;


    public UserProfile() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user_profile);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        mUserId = intent.getStringExtra(USER_ID_EXTRA_NAME);

        mCurrentUser = new User();

        mToolbar = (Toolbar)findViewById(R.id.toolbar);


        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.profile_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final DatabaseReference userFacebook = FirebaseDatabase.getInstance().getReference("users").child(mUserId);
        userFacebook.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mCurrentUser = dataSnapshot.getValue(User.class);

                if (mCurrentUser != null){

                    if (User.getUser().getPhotoUrl() == null) {

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

                    mButtonChat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (mCurrentUser.getUid() != null){

                                ChatActivity.startActivity(getActivity(), mCurrentUser.getName(), mCurrentUser.getUid(), mCurrentUser.getFcmUserDeviceId());
                                

                            } else {

                                Toast.makeText(UserProfile.this, "Can't chat, try again...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    ivUserProfilePhoto.setOnClickListener(new Button.OnClickListener() {
                        public void onClick(View v) {


                            if (mCurrentUser.getUid() != null){

                                Intent imageViewerIntent = new Intent(UserProfile.this, ImageViewerActivity.class);
                                imageViewerIntent.putExtra(ImageViewerActivity.EXTRA_IMAGE_URL, mCurrentUser.getPhotoUrl());
                                UserProfile.this.startActivity(imageViewerIntent);


                            } else {

                                Toast.makeText(UserProfile.this, "No Image found", Toast.LENGTH_SHORT).show();
                            }


                        }
                    });


                    fullname.setText(mCurrentUser.getName());
                    username.setText(String.format("@%s",mCurrentUser.getUsername().trim()));
                    //ivLogo.setText(mCurrentUser.getUsername().trim());
                    //ivLogo.setText("Profile");

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

                        //mBirthday.setText(mCurrentUser.getbirthday());

                    } else {

                        mBirthday.setText(R.string.indefined);
                        followers_count.setText("18 +");
                    }

                    if (mCurrentUser.getisMale()){

                        following_count.setText("Male");

                    } else if (!mCurrentUser.getisMale()){

                        following_count.setText("Female");
                    }

                    /*if (mCurrentUser.getAge() > 0) {

                        followers_count.setText(String.valueOf(mCurrentUser.getAge()));

                    } else followers_count.setText("18 +");*/

                    // Country and City
                    if (mCurrentUser.getcountry() != null && mCurrentUser.getactualcity() != null ){

                        mlocation.setText(mCurrentUser.getcountry() + ", " + mCurrentUser.getactualcity());
                    }

                    // Only country
                    else if (mCurrentUser.getcountry() != null && mCurrentUser.getactualcity() == null ) {

                        mlocation.setText(mCurrentUser.getcountry());

                        // Only city
                    } else if (mCurrentUser.getcountry() == null && mCurrentUser.getactualcity() != null ){

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

                DatabaseReference blocked = FirebaseDatabase.getInstance().getReference()
                        .child(Constants.ARG_USERS_BLOCK)
                        .child(User.getUser().getUid());

                blocked.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(mCurrentUser.getUid())){

                            Userblocked = true;

                            android.support.v7.app.AlertDialog.Builder block = new android.support.v7.app.AlertDialog.Builder(UserProfile.this, R.style.DialogStyle);
                            block.setTitle("You blocked" + " " + mCurrentUser.getName());
                            block.setCancelable(false);
                            block.setMessage(mCurrentUser.getName() + " " + "can't send you messages, do you want to unblock this user ?");
                            block.setPositiveButton("Unblock", (dialog13, which13) -> {

                                DatabaseReference block1 = FirebaseDatabase.getInstance().getReference()
                                        .child(Constants.ARG_USERS_BLOCK)
                                        .child(User.getUser().getUid());

                                block1.removeValue().addOnCompleteListener(task -> {

                                    Toast.makeText(UserProfile.this, "You unblocked" + " " + mCurrentUser.getName(), Toast.LENGTH_LONG).show();
                                    Userblocked = false;

                                });


                            });
                            block.setNegativeButton("No, don't", (dialog14, which14) -> {
                                //finish();
                            });
                            block.show();

                            //Toast.makeText(UserProfile.this, " You blocked" + mCurrentUser.getName(), Toast.LENGTH_LONG).show();
                        } else {

                            Userblocked = false;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        SetVisitor ();




    }

    public void SetVisitor (){

        // Setup visitor logic here

        final DatabaseReference userFacebook = FirebaseDatabase.getInstance().getReference("users").child(User.getUser().getUid());
        userFacebook.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                String name = dataSnapshot.getValue(User.class).getName();
                String firstname = dataSnapshot.getValue(User.class).getFirstname();
                String Lastname = dataSnapshot.getValue(User.class).getLastname();
                String uid = dataSnapshot.getValue(User.class).getUid();
                String photoUrl = dataSnapshot.getValue(User.class).getPhotoUrl();
                long birthdate = dataSnapshot.getValue(User.class).getbirthdate();
                Double lat = dataSnapshot.getValue(User.class).getlat();
                Double log = dataSnapshot.getValue(User.class).getlog();


                DatabaseReference usersVisitors = FirebaseDatabase.getInstance().getReference("users").child(mUserId).child("visitors");

                usersVisitors.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (!dataSnapshot.child(User.getUser().getUid()).child("countViews").exists()){

                            usersVisitors.child(User.getUser().getUid()).child("countViews").setValue(0);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                usersVisitors.child(User.getUser().getUid()).child("name").setValue(name);
                usersVisitors.child(User.getUser().getUid()).child("firstname").setValue(firstname);
                usersVisitors.child(User.getUser().getUid()).child("lastname").setValue(Lastname);
                usersVisitors.child(User.getUser().getUid()).child("uid").setValue(uid);
                usersVisitors.child(User.getUser().getUid()).child("photoUrl").setValue(photoUrl);
                usersVisitors.child(User.getUser().getUid()).child("birthdate").setValue(birthdate);
                //usersVisitors.child(User.getUser().getUid()).child("lastseenView").setValue(System.currentTimeMillis());
                usersVisitors.child(User.getUser().getUid()).child("lastseenView").setValue(ServerValue.TIMESTAMP);
                //usersVisitors.child(User.getUser().getUid()).child("countViews").setValue(CountViews);
                usersVisitors.child(User.getUser().getUid()).child("lat").setValue(lat);
                usersVisitors.child(User.getUser().getUid()).child("log").setValue(log).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){

                            usersVisitors.child(User.getUser().getUid()).child("countViews").runTransaction(new Transaction.Handler() {
                                @Override
                                public Transaction.Result doTransaction(MutableData CountView) {

                                    int credits = CountView.getValue(Integer.class);

                                    if (CountView.getValue() == null){

                                        CountView.setValue(0);

                                    } else {

                                        CountView.setValue( credits + 1);
                                    }

                                    return Transaction.success(CountView);
                                }

                                @Override
                                public void onComplete(DatabaseError databaseError, boolean success, DataSnapshot dataSnapshot) {
                                    // Analyse databaseError for any error during increment


                                }
                            });
                        }
                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.report_profile:
                // User chose the "Settings" item, show the app settings UI...

                if (mCurrentUser.getUid() != null) {


                    if (Userblocked){

                        //setup the alert builder
                        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
                        builder.setTitle("What you want to do ?");
                        builder.setCancelable(false);
                        // add a list
                        String[] animals = {"Report", "Unblock", "Cancel"};
                        builder.setItems(animals, (dialog, which) -> {
                            switch (which) {
                                case 0: {
                                    android.support.v7.app.AlertDialog.Builder report = new android.support.v7.app.AlertDialog.Builder(UserProfile.this, R.style.DialogStyle);
                                    report.setTitle("Report" + " " + mCurrentUser.getName());
                                    report.setCancelable(false);
                                    report.setMessage("You are about to report" + " " + mCurrentUser.getName() + " " + "We will verify if there is anything wrong.");
                                    report.setPositiveButton(" Report", (dialog12, which12) -> {

                                        Report report1 = new Report();

                                        report1.setReporterUid(User.getUser().getUid());
                                        report1.setReporter(User.getUser().getDisplayName());

                                        report1.setReportedUid(mCurrentUser.getUid());
                                        report1.setReported(mCurrentUser.getName());

                                        report1.setReportMessage("Please check this user profile!");
                                        report1.setReportTime(System.currentTimeMillis());

                                        DatabaseReference report2 = FirebaseDatabase.getInstance().getReference()
                                                .child(Constants.ARG_CHAT_REPORT)
                                                .child(String.valueOf(System.currentTimeMillis()));

                                        report2.setValue(report1).addOnCompleteListener(task -> Toast.makeText(UserProfile.this, "You reported" + " " + mCurrentUser.getName(), Toast.LENGTH_LONG).show());


                                    });
                                    report.setNegativeButton(getString(R.string.conf_4), (dialog1, which1) -> {
                                        //finish();
                                    });
                                    report.show();
                                }
                                break;

                                case 1: {
                                    android.support.v7.app.AlertDialog.Builder block = new android.support.v7.app.AlertDialog.Builder(UserProfile.this, R.style.DialogStyle);
                                    block.setTitle("Unblock" + " " + mCurrentUser.getName());
                                    block.setCancelable(false);
                                    block.setMessage(" You are about to unblock" + " " + mCurrentUser.getName() + " " + "if unbloked you can chat, do you want to unblock this user ?");
                                    block.setPositiveButton("Unblock", (dialog13, which13) -> {

                                        DatabaseReference block1 = FirebaseDatabase.getInstance().getReference()
                                                .child(Constants.ARG_USERS_BLOCK)
                                                .child(User.getUser().getUid());

                                        block1.removeValue().addOnCompleteListener(task -> {

                                            Toast.makeText(UserProfile.this, "You unblocked" + " " + mCurrentUser.getName(), Toast.LENGTH_LONG).show();
                                            Userblocked = false;

                                        });


                                    });
                                    block.setNegativeButton("No, don't", (dialog14, which14) -> {
                                        //finish();
                                    });
                                    block.show();

                                }
                                break;

                                case 2:{

                                    dialog.dismiss();
                                }
                                break;
                            }
                        });

                        // create and show the alert dialog
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    } else {


                        //setup the alert builder
                        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
                        builder.setTitle("What you want to do ?");
                        builder.setCancelable(false);
                        // add a list
                        String[] animals = {"Report", "Block", "Cancel"};
                        builder.setItems(animals, (dialog, which) -> {
                            switch (which) {
                                case 0: {
                                    android.support.v7.app.AlertDialog.Builder report = new android.support.v7.app.AlertDialog.Builder(UserProfile.this, R.style.DialogStyle);
                                    report.setTitle("Report" + " " + mCurrentUser.getName());
                                    report.setCancelable(false);
                                    report.setMessage("You are about to report" + " " + mCurrentUser.getName() + " " + "We will verify if there is anything wrong.");
                                    report.setPositiveButton(" Report", (dialog12, which12) -> {

                                        Report report1 = new Report();

                                        report1.setReporterUid(User.getUser().getUid());
                                        report1.setReporter(User.getUser().getDisplayName());

                                        report1.setReportedUid(mCurrentUser.getUid());
                                        report1.setReported(mCurrentUser.getName());

                                        report1.setReportMessage("Please check this user profile!");
                                        report1.setReportTime(System.currentTimeMillis());

                                        DatabaseReference report2 = FirebaseDatabase.getInstance().getReference()
                                                .child(Constants.ARG_CHAT_REPORT)
                                                .child(String.valueOf(System.currentTimeMillis()));

                                        report2.setValue(report1).addOnCompleteListener(task -> Toast.makeText(UserProfile.this, "You reported" + " " + mCurrentUser.getName(), Toast.LENGTH_LONG).show());


                                    });
                                    report.setNegativeButton(getString(R.string.conf_4), (dialog1, which1) -> {
                                        //finish();
                                    });
                                    report.show();
                                }
                                break;

                                case 1: {
                                    android.support.v7.app.AlertDialog.Builder block = new android.support.v7.app.AlertDialog.Builder(UserProfile.this, R.style.DialogStyle);
                                    block.setTitle("Block" + " " + mCurrentUser.getName());
                                    block.setCancelable(false);
                                    block.setMessage("You are about to block this user" + ", " + mCurrentUser.getName() + " " + "will not be able to send you a message.");
                                    block.setPositiveButton(" Block", (dialog13, which13) -> {

                                        DatabaseReference block1 = FirebaseDatabase.getInstance().getReference()
                                                .child(Constants.ARG_USERS_BLOCK)
                                                .child(User.getUser().getUid())
                                                .child(mCurrentUser.getUid());

                                        block1.setValue("yes").addOnCompleteListener(task -> {

                                            Toast.makeText(UserProfile.this, "You blocked" + " " + mCurrentUser.getName(), Toast.LENGTH_LONG).show();
                                            Userblocked = true;

                                        });


                                    });
                                    block.setNegativeButton(getString(R.string.conf_4), (dialog14, which14) -> {
                                        //finish();
                                    });
                                    block.show();

                                }
                                break;

                                case 2:{

                                    dialog.dismiss();
                                }
                                break;
                            }
                        });

                        // create and show the alert dialog
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }


                } else {

                    Toast.makeText(UserProfile.this, "Wait a minute please...", Toast.LENGTH_LONG).show();
                }

                return true;

            case android.R.id.home:
                onBackPressed();

                return  true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
