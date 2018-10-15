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

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.angopapo.aroundme2.AroundMe.Profile.UserProfile;
import com.angopapo.aroundme2.R;
import com.angopapo.aroundme2.Utils.Image.GlideUtil;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class VisitorsViewHolder extends RecyclerView.ViewHolder {
    private final View mView;
    public DatabaseReference mUsersRef;
    public ValueEventListener mUserListener;

    private static final int POST_TEXT_MAX_LINES = 6;

    private ImageView userPhoto;
    private TextView username;
    private TextView distances;
    private TextView ages;

    private TextView mViewsCount, mLastSeen;
    private Button mNewVisitor;

    public String mPostKey;
    public ValueEventListener mLikeListener;

    public VisitorsViewHolder(View itemView) {
        super(itemView);
        mView = itemView;

        userPhoto = itemView.findViewById(R.id.user_photo);
        username = itemView.findViewById(R.id.username);
        distances = itemView.findViewById(R.id.text_distance);
        ages = itemView.findViewById(R.id.user_age);

        mViewsCount = itemView.findViewById(R.id.views_count);
        mLastSeen = itemView.findViewById(R.id.text_lastseen);
        mNewVisitor = itemView.findViewById(R.id.button3);

        mNewVisitor.setVisibility(View.GONE);

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

    public void setmViewsCount ( String viewsCount){

        if (viewsCount == null || viewsCount.isEmpty()){
            viewsCount = mView.getResources().getString(R.string.no_views);
        }

        mViewsCount.setText(viewsCount);

    }

    public void setmLastSeen ( String lastSeen){

        if (lastSeen == null || lastSeen.isEmpty()){
            lastSeen = mView.getResources().getString(R.string.no_views);
        }

        mLastSeen.setText(lastSeen);

    }

    public void setmNewVisitor ( int newVisitor){

        if (newVisitor == 1 ){

            mNewVisitor.setVisibility(View.VISIBLE);

        } else {

            mNewVisitor.setVisibility(View.GONE);
        }

    }



    public void setAge(String age) {
        if (age == null || age.isEmpty()) {
            age = mView.getResources().getString(R.string.normal_age);
        }
        ages.setText(age);
    }

    /*public void setOnline(String url) {
        GlideUtil.checkOnline(url, onlineStatusImage);
    }

    public void setOffline(String url) {
        GlideUtil.checkOffline(url, onlineStatusImage);
    }

    public void setSoon(String url) {
        GlideUtil.checkSoon(url, onlineStatusImage);
    }*/


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