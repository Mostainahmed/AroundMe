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

package com.aroundme.mostain.Utils.Image;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;

import com.aroundme.mostain.R;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.IOException;


import jp.wasabeef.picasso.transformations.BlurTransformation;

import static com.facebook.FacebookSdk.getApplicationContext;


public class GlideUtil {
    public static void loadImage(String url, ImageView imageView) {
        Context context = imageView.getContext();
        //ColorDrawable cd = new ColorDrawable(ContextCompat.getColor(context, R.color.gray));
        Glide.with(getApplicationContext())
                .load(url)
                .placeholder(R.drawable.profile_default_photo)
                .crossFade()
                .centerCrop()
                .into(imageView);
    }

    public static void loadImageBlur(String url, ImageView imageView) {
        Context context = imageView.getContext();
        Picasso.with(getApplicationContext())
                .load(url)
                .placeholder(R.drawable.profile_default_photo)
                .transform(new BlurTransformation(context, 80))
                .centerCrop()
                .resize(640, 640)
                .into(imageView);
    }


    public static void loadProfileIcon(String url, ImageView imageView) {
        Context context = imageView.getContext();
        Glide.with(getApplicationContext())
                .load(url)
                .placeholder(R.drawable.profile_default_photo)
                .dontAnimate()
                .fitCenter()
                .into(imageView);
    }
        public static void checkOnline(String url, ImageView imageView) {
            Context context = imageView.getContext();
            Glide.with(getApplicationContext())
                    .load(R.drawable.ic_online_15_0_alizarin)
                    .placeholder(R.drawable.ic_online_15_0_alizarin)
                    .crossFade()
                    .centerCrop()
                    .into(imageView);
        }

    public static void checkOffline(String url, ImageView imageView) {
        Context context = imageView.getContext();
        Glide.with(getApplicationContext())
                .load(R.drawable.ic_offline_15_0_alizarin)
                .placeholder(R.drawable.ic_offline_15_0_alizarin)
                .crossFade()
                .centerCrop()
                .into(imageView);
    }

    public static void checkSoon(String url, ImageView imageView) {
        Context context = imageView.getContext();
        Glide.with(getApplicationContext())
                .load(R.drawable.last_min)
                .placeholder(R.drawable.last_min)
                .crossFade()
                .centerCrop()
                .into(imageView);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void enterCircularReveal(View v) {
        int cx = v.getMeasuredWidth() / 2;
        int cy = v.getMeasuredHeight() / 2;

        int finalRadius = Math.max(v.getWidth(), v.getHeight()) / 2;
        Animator anim = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, finalRadius);
        v.setVisibility(View.VISIBLE);
        anim.start();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void exitCircularReveal(final View v) {
        int cx = v.getMeasuredWidth() / 2;
        int cy = v.getMeasuredHeight() / 2;

        int initialRadius = v.getWidth() / 2;
        Animator anim = ViewAnimationUtils.createCircularReveal(v, cx, cy, initialRadius, 0);

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                v.setVisibility(View.INVISIBLE);
            }
        });
        anim.start();
    }

    public static Bitmap getBitmapFromUri(Context context, String uri) throws IOException {
        return MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(uri));
    }

    public static Bitmap getScaledBitmap(Bitmap bitmap, int maxSize) {
        float ratio = Math.min(
                (float) maxSize / bitmap.getWidth(),
                (float) maxSize / bitmap.getHeight());
        int width = Math.round(ratio * bitmap.getWidth());
        int height = Math.round(ratio * bitmap.getHeight());
        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }


}