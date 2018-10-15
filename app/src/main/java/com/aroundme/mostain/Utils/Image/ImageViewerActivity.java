package com.aroundme.mostain.Utils.Image;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.angopapo.aroundme2.App.BaseActivity;
import com.angopapo.aroundme2.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.greysonparrelli.permiso.Permiso;


public class ImageViewerActivity extends BaseActivity {

    public static String EXTRA_IMAGE_URL = "image_url";

    ImageView mImage;
    FrameLayout progressBar;
    //Button mButtonClose;
    FloatingActionButton fabShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_show_image);

        Permiso.getInstance().setActivity(this);

        progressBar = findViewById(R.id.fl_progress_bar);
        mImage = findViewById(R.id.iv_image);
        //mButtonClose = findViewById(R.id.image_close);
        fabShare = findViewById(R.id.fab_share);

        String url = "";
        if(getIntent() != null){
            url = getIntent().getStringExtra(EXTRA_IMAGE_URL);
        }

        progressBar.setVisibility(View.VISIBLE);


        if (url != null && !url.isEmpty()) {
            Glide.with(ImageViewerActivity.this)
                    .load(url)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(mImage);
        }

        fabShare.setImageResource(R.drawable.ic_share);
        String finalUrl = url;
        fabShare.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult() {
                            @Override
                            public void onPermissionResult(Permiso.ResultSet resultSet) {
                                if (resultSet.areAllPermissionsGranted()) {
                                    try {
                                        progressBar.setVisibility(View.VISIBLE);
                                        Glide.with(getApplicationContext())
                                                .load(finalUrl)
                                                .asBitmap()
                                                .into(new SimpleTarget<Bitmap>() {
                                                    @Override
                                                    public void onResourceReady(final Bitmap bmp, GlideAnimation glideAnimation) {initializeShareIntent(bmp, progressBar);
                                                    }
                                                });
                                    } catch (Exception e) {
                                        progressBar.setVisibility(View.GONE);
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(ImageViewerActivity.this, getString(R.string.msg_permission_required), Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onRationaleRequested(Permiso.IOnRationaleProvided callback, String... permissions) {
                                Permiso.getInstance().showRationaleInDialog(null,
                                        getString(R.string.msg_permission_required),
                                        null, callback);
                            }
                        }, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }
                }
        );


        /*mButtonClose.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                ImageViewerActivity.super.onBackPressed();

            }
        });*/
    }

    private void initializeShareIntent(Bitmap bmp, FrameLayout flProgress) {
        String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(),
                bmp, "title", "description");
        Intent shareImageIntent = new Intent();
        shareImageIntent.setAction(Intent.ACTION_SEND);
        shareImageIntent.setType("image/*");
        shareImageIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
        flProgress.setVisibility(View.GONE);
        startActivity(Intent.createChooser(shareImageIntent, "Share this image"));
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permiso.getInstance().onRequestPermissionResult(requestCode, permissions, grantResults);
    }

}