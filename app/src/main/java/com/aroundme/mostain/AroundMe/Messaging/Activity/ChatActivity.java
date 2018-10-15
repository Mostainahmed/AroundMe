package com.aroundme.mostain.AroundMe.Messaging.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aroundme.mostain.App.Application;
import com.aroundme.mostain.App.BaseActivity;
import com.aroundme.mostain.AroundMe.Messaging.Fragment.ChatFragment;
import com.aroundme.mostain.Class.Report;
import com.aroundme.mostain.Class.User;
import com.aroundme.mostain.R;
import com.aroundme.mostain.Utils.service.Constants;
import com.aroundme.mostain.Utils.service.ServiceUtils;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.ocpsoft.prettytime.PrettyTime;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends BaseActivity {

   // private Toolbar mToolbar;
    private TextView mName, mStatus;
    private CircleImageView mProfile;
    private ImageView mback, mMore;
    private String toUserID;

    private String reported;
    private String reportedUid;
    private boolean Userblocked = false;


    private DatabaseReference database;

    public static void startActivity(Context context, String receiver, String receiverUid, String firebaseToken) {



        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(Constants.ARG_RECEIVER, receiver);
        intent.putExtra(Constants.ARG_RECEIVER_UID, receiverUid);
        intent.putExtra(Constants.ARG_FIREBASE_TOKEN, firebaseToken);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        bindViews();
        toUserID = getIntent().getExtras().getString(Constants.ARG_RECEIVER_UID);

        reported = getIntent().getExtras().getString(Constants.ARG_RECEIVER);
        reportedUid = getIntent().getExtras().getString(Constants.ARG_RECEIVER_UID);

        init();



    }

    private void bindViews() {

        mName =  findViewById(R.id.textView3);
        mStatus =  findViewById(R.id.textView5);
        mProfile =  findViewById(R.id.ivLogo);
        mMore =  findViewById(R.id.send_private);
        mback = findViewById(R.id.new_post);
    }

    private void init() {

        DatabaseReference blocked = FirebaseDatabase.getInstance().getReference()
                .child(Constants.ARG_USERS_BLOCK)
                .child(User.getUser().getUid());

        blocked.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(reportedUid)){

                    Userblocked = true;

                    android.support.v7.app.AlertDialog.Builder block = new android.support.v7.app.AlertDialog.Builder(ChatActivity.this, R.style.DialogStyle);
                    block.setTitle("You blocked" + reported);
                    block.setCancelable(false);
                    block.setMessage(reported + " " + "can't send you messages, do you want to unblock this user ?");
                    block.setPositiveButton("Unblock", (dialog13, which13) -> {

                        DatabaseReference block1 = FirebaseDatabase.getInstance().getReference()
                                .child(Constants.ARG_USERS_BLOCK)
                                .child(User.getUser().getUid())
                                .child(reportedUid);

                        block1.removeValue().addOnCompleteListener(task -> {

                            Userblocked = false;
                            Toast.makeText(ChatActivity.this, "You unblocked" + " " + reported, Toast.LENGTH_LONG).show();


                        });


                    });
                    block.setNegativeButton("No, don't", (dialog14, which14) -> {
                        //finish();
                    });
                    block.show();

                    //Toast.makeText(UserProfile.this, " You blocked" + reported, Toast.LENGTH_LONG).show();
                } else {

                    Userblocked = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });

        mMore.setOnClickListener(view -> {

            if (reportedUid != null) {


                if (Userblocked){

                    //setup the alert builder
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                    builder.setTitle("What you want to do ?");
                    builder.setCancelable(false);
                    // add a list
                    String[] animals = {"Report", "Unblock", "Cancel"};
                    builder.setItems(animals, (dialog, which) -> {
                        switch (which) {
                            case 0: {
                                android.support.v7.app.AlertDialog.Builder report = new android.support.v7.app.AlertDialog.Builder(ChatActivity.this, R.style.DialogStyle);
                                report.setTitle("Report" + " " + reported);
                                report.setCancelable(false);
                                report.setMessage("You are about to report" + " " + reported + " " + "We will verify if there is anything wrong.");
                                report.setPositiveButton(" Report", (dialog12, which12) -> {

                                    Report report1 = new Report();

                                    report1.setReporterUid(User.getUser().getUid());
                                    report1.setReporter(User.getUser().getDisplayName());

                                    report1.setReportedUid(reportedUid);
                                    report1.setReported(reported);

                                    report1.setReportMessage("Please check this user profile!");
                                    report1.setReportTime(System.currentTimeMillis());

                                    DatabaseReference report2 = FirebaseDatabase.getInstance().getReference()
                                            .child(Constants.ARG_CHAT_REPORT)
                                            .child(String.valueOf(System.currentTimeMillis()));

                                    report2.setValue(report1).addOnCompleteListener(task -> Toast.makeText(ChatActivity.this, "You reported" + " " + reported, Toast.LENGTH_LONG).show());


                                });
                                report.setNegativeButton(getString(R.string.conf_4), (dialog1, which1) -> {
                                    //finish();
                                });
                                report.show();
                            }
                            break;

                            case 1: {
                                android.support.v7.app.AlertDialog.Builder block = new android.support.v7.app.AlertDialog.Builder(ChatActivity.this, R.style.DialogStyle);
                                block.setTitle("Unblock" + " " + reported);
                                block.setCancelable(false);
                                block.setMessage(" You are about to unblock" + " " + reported + " " + "if unbloked you can chat, do you want to unblock this user ?");
                                block.setPositiveButton("Unblock", (dialog13, which13) -> {

                                    DatabaseReference block1 = FirebaseDatabase.getInstance().getReference()
                                            .child(Constants.ARG_USERS_BLOCK)
                                            .child(User.getUser().getUid());

                                    block1.removeValue().addOnCompleteListener(task -> {

                                        Toast.makeText(ChatActivity.this, "You unblocked" + " " + reported, Toast.LENGTH_LONG).show();
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                    builder.setTitle("What you want to do ?");
                    builder.setCancelable(false);
                    // add a list
                    String[] animals = {"Report", "Block", "Cancel"};
                    builder.setItems(animals, (dialog, which) -> {
                        switch (which) {
                            case 0: {
                                android.support.v7.app.AlertDialog.Builder report = new android.support.v7.app.AlertDialog.Builder(ChatActivity.this, R.style.DialogStyle);
                                report.setTitle("Report" + " " + reported);
                                report.setCancelable(false);
                                report.setMessage("You are about to report" + " " + reported + " " + "We will verify if there is anything wrong.");
                                report.setPositiveButton(" Report", (dialog12, which12) -> {

                                    Report report1 = new Report();

                                    report1.setReporterUid(User.getUser().getUid());
                                    report1.setReporter(User.getUser().getDisplayName());

                                    report1.setReportedUid(reportedUid);
                                    report1.setReported(reported);

                                    report1.setReportMessage("Please check this user profile!");
                                    report1.setReportTime(System.currentTimeMillis());

                                    DatabaseReference report2 = FirebaseDatabase.getInstance().getReference()
                                            .child(Constants.ARG_CHAT_REPORT)
                                            .child(String.valueOf(System.currentTimeMillis()));

                                    report2.setValue(report1).addOnCompleteListener(task -> Toast.makeText(ChatActivity.this, "You reported" + " " + reported, Toast.LENGTH_LONG).show());


                                });
                                report.setNegativeButton(getString(R.string.conf_4), (dialog1, which1) -> {
                                    //finish();
                                });
                                report.show();
                            }
                            break;

                            case 1: {
                                android.support.v7.app.AlertDialog.Builder block = new android.support.v7.app.AlertDialog.Builder(ChatActivity.this, R.style.DialogStyle);
                                block.setTitle("Block" + " " + reported);
                                block.setCancelable(false);
                                block.setMessage("You are about to block this user" + ", " + reported + " " + "will not be able to send you a message.");
                                block.setPositiveButton(" Block", (dialog13, which13) -> {

                                    DatabaseReference block1 = FirebaseDatabase.getInstance().getReference()
                                            .child(Constants.ARG_USERS_BLOCK)
                                            .child(User.getUser().getUid())
                                            .child(reportedUid);

                                    block1.setValue("yes").addOnCompleteListener(task -> {

                                        Toast.makeText(ChatActivity.this, "You blocked" + " " + reported, Toast.LENGTH_LONG).show();
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

                Toast.makeText(ChatActivity.this, "Wait a minute please...", Toast.LENGTH_LONG).show();
            }
        });

        database = FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).child(toUserID);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                if ((user != null ? user.photoUrl : null) != null){

                    Glide.with(getApplicationContext())
                            .load(user.photoUrl)
                            .placeholder(R.drawable.profile_default_photo)
                            .dontAnimate()
                            .fitCenter()
                            .into(mProfile);

                } else {


                    Glide.with(getApplicationContext())
                            .load(R.drawable.profile_default_photo)
                            .placeholder(R.drawable.profile_default_photo)
                            .dontAnimate()
                            .fitCenter()
                            .into(mProfile);

                }

                if (user != null) {
                    if (user.getName() != null){

                        mName.setText(user.getName());
                    } else {

                        mName.setText("No_name");
                    }
                }

                if (user != null) {

                    if (user.getisOnline()){

                        mStatus.setText("Online");

                    } else {

                        if (System.currentTimeMillis() - user.getTimestamp() > ServiceUtils.TIME_TO_OFFLINE) {

                            DateTime senttime = new DateTime(user.getTimestamp());

                            String time = new PrettyTime().format( senttime.toDate());

                            mStatus.setText(time);

                        } else if (System.currentTimeMillis() - user.getTimestamp() > ServiceUtils.TIME_TO_SOON) {

                            mStatus.setText("Moment ago");
                        }
                    }

                    mProfile.setOnClickListener(new Button.OnClickListener() {
                        public void onClick(View v) {


                            if (user.getUid() != null){

                                Intent imageViewerIntent = new Intent(ChatActivity.this, ImageViewerActivity.class);
                                imageViewerIntent.putExtra(ImageViewerActivity.EXTRA_IMAGE_URL, user.getPhotoUrl());
                                ChatActivity.this.startActivity(imageViewerIntent);


                            } else {

                                Toast.makeText(ChatActivity.this, "No Image found", Toast.LENGTH_SHORT).show();
                            }


                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        // set the register screen fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_content_chat,
                ChatFragment.newInstance(
                        getIntent().getExtras().getString(Constants.ARG_RECEIVER),
                        getIntent().getExtras().getString(Constants.ARG_RECEIVER_UID),
                        getIntent().getExtras().getString(Constants.ARG_FIREBASE_TOKEN)),
                ChatFragment.class.getSimpleName());
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Application.setChatActivityOpen(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Application.setChatActivityOpen(false);
    }
}
