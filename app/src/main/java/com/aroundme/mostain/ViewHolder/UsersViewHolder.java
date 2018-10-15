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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aroundme.mostain.App.Application;
import com.aroundme.mostain.AroundMe.Profile.UserProfile;
import com.aroundme.mostain.Class.User;
import com.aroundme.mostain.R;
import com.aroundme.mostain.Utils.Image.GlideUtil;

import java.util.List;
public class UsersViewHolder extends RecyclerView.ViewHolder{

    private View mView;
    private ImageView userPhoto;
    private ImageView onlineStatusImage;
    private TextView username;
    private TextView distances;
    private TextView ages;

    private List<User> users;
    public UsersViewHolder(final View itemView, final List<User> users) {
        super(itemView);
        mView = itemView;

        this.users = users;

        userPhoto = itemView.findViewById(R.id.user_photo);
        username = itemView.findViewById(R.id.username);
        onlineStatusImage = itemView.findViewById(R.id.image_online_status);
        distances = itemView.findViewById(R.id.text_distance);
        ages = itemView.findViewById(R.id.user_age);

    }

    public void setPhoto(String url) {
        GlideUtil.loadImage(url, userPhoto);
    }

    public void setPhotoPrivate(String url) {
        GlideUtil.loadImageBlur(url, userPhoto);
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
        username.setOnClickListener(view -> {


            if (selectedUserId.equals("private")){

                showUSerprofilePrivate();

            } else {

                showUserDetail(selectedUserId);
            }
        });

        userPhoto.setOnClickListener(view -> {

            if (selectedUserId.equals("private")){

                showUSerprofilePrivate();

            } else {

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

    public void showUSerprofilePrivate(){
        Context context = mView.getContext();

        Toast.makeText(Application.getInstance().getApplicationContext(), "This profile is private for now, try later", Toast.LENGTH_LONG).show();

    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }


}