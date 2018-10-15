/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aroundme.mostain.ViewHolder;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.angopapo.aroundme2.App.Application;
import com.angopapo.aroundme2.AroundMe.Messaging.Activity.ChatActivity;
import com.angopapo.aroundme2.AroundMe.Messaging.Activity.ImageViewerActivity;
import com.angopapo.aroundme2.Class.User;
import com.angopapo.aroundme2.R;
import com.angopapo.aroundme2.Utils.Image.GlideUtil;
import com.angopapo.aroundme2.Utils.service.Constants;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vanniktech.emoji.EmojiTextView;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

public class ListChatViewHolder extends RecyclerView.ViewHolder {
    private final View mView;
    public DatabaseReference mUsersRef;
    public ValueEventListener mUserListener;

    private Dialog progressDialog;

    private static final int POST_TEXT_MAX_LINES = 6;

    private CircleImageView profileImage;
    private ImageView online;
    private TextView name;
    private EmojiTextView body;
    private TextView timeChat;
    private ImageView status;
    private RelativeLayout messageView;
    private RelativeLayout RView;

    public String mPostKey;
    public ValueEventListener mLikeListener;

    public ListChatViewHolder(View itemView) {
        super(itemView);
        mView = itemView;

        RView = mView.findViewById(R.id.rViewe);
        profileImage =  mView.findViewById(R.id.profile_photo);
        name =  mView.findViewById(R.id.username);
        body =  mView.findViewById(R.id.message_txt);
        timeChat =  mView.findViewById(R.id.time);
        status =  mView.findViewById(R.id.imageView6);
        online =  mView.findViewById(R.id.image_online_status);
        messageView =  mView.findViewById(R.id.list);

    }

    public void setPhoto(String url) {
        GlideUtil.loadImage(url, profileImage);


        profileImage.setOnClickListener(view -> {

            if (url != null){

                ShowImageView(url);
            } else {

                Toast.makeText(Application.getInstance().getApplicationContext(), "No profile picture to show", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setTime (String time){

        if (time == null || time.isEmpty()){
            time = mView.getResources().getString(R.string.no_location);
        }

        timeChat.setText(time);

    }

    public void setLastMessage (String lastMessage){

        if (lastMessage == null || lastMessage.isEmpty()){
            lastMessage = mView.getResources().getString(R.string.no_location);
        }

        body.setText(lastMessage);

    }

    public void setOnline(String url) {
        GlideUtil.checkOnline(url, online);
    }

    public void setOffline(String url) {
        GlideUtil.checkOffline(url, online);
    }

    public void setSoon(String url) {
        GlideUtil.checkSoon(url, online);
    }



    public void setRead() {

        status.setImageResource(R.drawable.ic_read_24dp);
    }

    public void setUnRead() {

        status.setImageResource(R.drawable.ic_sent_24dp);
    }

    public void sethideStatus() {

        status.setVisibility(View.GONE);
    }

    public void setLastMessageBold (String lastMessage){

        if (lastMessage == null || lastMessage.isEmpty()){
            lastMessage = mView.getResources().getString(R.string.no_location);
        }


        SpannableString span2 = new SpannableString(lastMessage);
        span2.setSpan(new StyleSpan(Typeface.BOLD),0,lastMessage.length(),SPAN_INCLUSIVE_INCLUSIVE); // set Style

        // let's put both spans together with a separator and all
        CharSequence finalText = TextUtils.concat(span2);

        body.setText(finalText);
        body.setTextColor(Color.RED);

    }


    public void setUser(String users, final String selectedUserId, final String getFcmUserDeviceId) {
        if (users == null || users.isEmpty()) {
            users = mView.getResources().getString(R.string.user_info_no_name);
        }
        name.setText(users);


        String finalUsers = users;

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showUserDetail(finalUsers, selectedUserId, getFcmUserDeviceId);

            }
        });


        messageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserDetail(finalUsers, selectedUserId, getFcmUserDeviceId);
            }
        });

        messageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                RView.setBackgroundResource(R.color.gray1);

                // setup the alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(mView.getContext());
                builder.setTitle("Delete this conversation ?");
                builder.setCancelable(false);
                // add a list
                String[] animals = {"Delete", "Cancel"};
                builder.setItems(animals, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:

                                // setup the alert builder
                                AlertDialog.Builder builder = new AlertDialog.Builder(mView.getContext());
                                builder.setTitle("This will only delete your copy");
                                builder.setCancelable(false);
                                // add a list
                                String[] animals = {"Delete", "Cancel"};
                                builder.setItems(animals, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case 0:

                                                showProgressBar("Deleting...");
                                                DatabaseReference chat = FirebaseDatabase.getInstance().getReference()
                                                        .child(Constants.ARG_USERS) // Users
                                                        .child(User.getUser().getUid()) // Me
                                                        .child(Constants.ARG_CHAT_ROOMS) // Chat
                                                        .child(selectedUserId); // Chat ID

                                                chat.removeValue((databaseError, databaseReference) -> {

                                                    DatabaseReference chatList = FirebaseDatabase.getInstance().getReference()
                                                            .child(Constants.ARG_USERS) // Users
                                                            .child(User.getUser().getUid()) // Me
                                                            .child(Constants.ARG_CHAT_LIST) // Chat
                                                            .child(selectedUserId); // Chat List ID

                                                    chatList.removeValue((databaseError1, databaseReference1) -> {

                                                        dismissProgressBar();
                                                        RView.setBackgroundResource(R.color.transparent);

                                                        Toast.makeText(mView.getContext(), "Conversation with" + " " + "with" + finalUsers + " " + "deleted.", Toast.LENGTH_LONG).show();
                                                    });


                                                });

                                            case 1:
                                                RView.setBackgroundResource(R.color.transparent);

                                        }
                                    }
                                });

                                // create and show the alert dialog
                                AlertDialog dialog2 = builder.create();
                                dialog2.show();



                            case 1:
                                RView.setBackgroundResource(R.color.transparent);
                        }
                    }
                });

                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
                return false;
            }
        });
    }

    private void showUserDetail(String getName, String selectedUserId, String getFcmUserDeviceId) {
        Context context = mView.getContext();

        ChatActivity.startActivity(context, getName, selectedUserId, getFcmUserDeviceId);

    }

    public void ShowImageView(String imageUrl){

        Intent imageViewerIntent = new Intent(Application.getInstance().getApplicationContext(), ImageViewerActivity.class);
        imageViewerIntent.putExtra(ImageViewerActivity.EXTRA_IMAGE_URL, imageUrl);
        Application.getInstance().getApplicationContext().startActivity(imageViewerIntent);
    }

    public void showProgressBar(String message){
        progressDialog = ProgressDialog.show(mView.getContext(), "", message, true);
    }
    public void dismissProgressBar() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }


}