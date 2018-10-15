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

package com.aroundme.mostain.AroundMe.NearMe.Old;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angopapo.aroundme2.AroundMe.Profile.UserProfile;
import com.angopapo.aroundme2.R;
import com.angopapo.aroundme2.Utils.Image.GlideUtil;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class NewUsersViewHolder extends RecyclerView.ViewHolder {
    private final View mView;
    public DatabaseReference mUsersRef;
    public ValueEventListener mUserListener;

    private static final int POST_TEXT_MAX_LINES = 6;

    private ImageView userPhoto;
    private ImageView onlineStatusImage;
    private TextView username;
    private TextView distances;
    private TextView ages;
    private RelativeLayout cardView;

    public String mPostKey;
    public ValueEventListener mLikeListener;

    public NewUsersViewHolder(View itemView) {
        super(itemView);
        mView = itemView;

        userPhoto = (ImageView) itemView.findViewById(R.id.user_photo);
        username = (TextView) itemView.findViewById(R.id.username);
        onlineStatusImage = (ImageView) itemView.findViewById(R.id.image_online_status);
        distances = (TextView) itemView.findViewById(R.id.text_distance);
        ages = (TextView) itemView.findViewById(R.id.user_age);
        cardView = (RelativeLayout) itemView.findViewById(R.id.cv);

    }

    public void setPhoto(String url) {
        GlideUtil.loadImage(url, userPhoto);
    }

    public void setDistance (String distance){

        if (distance == null || distance.isEmpty()){
            distance = mView.getResources().getString(R.string.no_location);
        }

        distances.setText(distance);

    }



    public void setAge(String age) {
        if (age == null || age.isEmpty()) {
            age = mView.getResources().getString(R.string.normal_age);
        }
        ages.setText(age);
    }

    public void setOnline(String url) {
        GlideUtil.checkOnline(url, onlineStatusImage);
    }

    public void setOffline(String url) {
        GlideUtil.checkOffline(url, onlineStatusImage);
    }

    public void setSoon(String url) {
        GlideUtil.checkSoon(url, onlineStatusImage);
    }


    public void setUser(String users, final String selectedUserId) {
        if (users == null || users.isEmpty()) {
            users = mView.getResources().getString(R.string.user_info_no_name);
        }
        username.setText(users);
        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserDetail(selectedUserId);
            }
        });

        userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserDetail(selectedUserId);
            }
        });
    }

    private void showUserDetail(String selectedUserId) {
        Context context = mView.getContext();

        Intent userDetailIntent = new Intent(context, UserProfile.class);
        userDetailIntent.putExtra(UserProfile.USER_ID_EXTRA_NAME, selectedUserId);
        context.startActivity(userDetailIntent);
    }


}