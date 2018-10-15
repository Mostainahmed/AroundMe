package com.aroundme.mostain.App;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;


import com.aroundme.mostain.AroundMe.HotOrHot.HotOrNotActivity;
import com.aroundme.mostain.AroundMe.Messaging.ChatList.ListChatActivity;
import com.aroundme.mostain.AroundMe.MyVisitores.MyVisitorsActivity;
import com.aroundme.mostain.AroundMe.MyVisitores.VisitorActivity;
import com.aroundme.mostain.AroundMe.NearMe.AroundMeActivity;
import com.aroundme.mostain.AroundMe.Passport.PassportActivity;
import com.aroundme.mostain.AroundMe.Passport.TravelActivity;
import com.aroundme.mostain.AroundMe.PrivateProfile.PrivateProfileActivity;
import com.aroundme.mostain.AroundMe.Profile.MyProfile;
import com.aroundme.mostain.AroundMe.Settings.SettingsActivity;
import com.aroundme.mostain.Auth.LoginActivity;
import com.aroundme.mostain.Class.User;
import com.aroundme.mostain.R;

import com.aroundme.mostain.App.BaseActivity;
import com.aroundme.mostain.Utils.Helper.ActivityWithToolbar;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.view.BezelImageView;


/**
 * Created by Angopapo, LDA on 07.09.15.
 */
public class NavigationDrawer extends BaseActivity {



    public static Drawer createDrawer(final ActivityWithToolbar activity){


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null){

            Intent intent = new Intent();
            intent.setClass((Context) activity, LoginActivity.class);
            activity.startActivity(intent);

        }

        // Here is where we are showing the user credits in header of Drawer

        //String getCredits = String.valueOf(mCurrentUser.getCredits());

        ProfileDrawerItem profileDrawerItem = null;
        /*if (currentUser != null) {
            if (!getCredits.isEmpty()) {
                profileDrawerItem = new ProfileDrawerItem().withEmail(getCredits + " " + "Credits" ).withName(currentUser.getDisplayName());
            } else profileDrawerItem = new ProfileDrawerItem().withEmail("0,00" + " " + "Credits" ).withName(currentUser.getDisplayName());
        }*/

        if (currentUser != null){

            profileDrawerItem = new ProfileDrawerItem().withEmail(currentUser.getEmail()).withName(currentUser.getDisplayName());
        }

        // Drawer features settings

        AccountHeader accountHeader = new AccountHeaderBuilder()
                .withActivity(activity.getActivity())
                .withHeaderBackground(R.color.alizarin)
                .addProfiles(profileDrawerItem)
                .withDividerBelowHeader(true)
                .withProfileImagesClickable(true)
                .withOnAccountHeaderProfileImageListener(new AccountHeader.OnAccountHeaderProfileImageListener() {
                    @Override
                    public boolean onProfileImageClick(View view, IProfile profile, boolean current) {

                        Intent intent = new Intent();
                        intent.setClass((Context) activity, MyProfile.class);
                        activity.startActivity(intent);
                        return false;
                    }

                    @Override
                    public boolean onProfileImageLongClick(View view, IProfile profile, boolean current) {
                        return false;
                    }
                })
                .withAlternativeProfileHeaderSwitching(false)
                .build();

        // Here is where we are showing the cover picture in header of Drawer

        if (currentUser != null) {
            if (currentUser.getPhotoUrl() == null) {

                ImageView ConverPhotoThumb = (ImageView) accountHeader.getView().findViewById(R.id.material_drawer_account_header_background);


                Glide.with(activity.getActivity()).load(R.drawable.profile_default_cover).into(ConverPhotoThumb);

            } else

            {

                ImageView ConverPhotoThumb = (ImageView) accountHeader.getView().findViewById(R.id.material_drawer_account_header_background);


                Glide.with(activity.getActivity()).load(currentUser.getPhotoUrl()).centerCrop().placeholder(R.drawable.profile_default_cover).error(R.drawable.profile_default_cover).into(ConverPhotoThumb);
                //Glide.with(activity.getActivity()).load(mCurrentUser.getCoverPhoto()).centerCrop().resize(250, 148).placeholder(R.drawable.profile_default_cover).error(R.drawable.profile_default_cover).into(ConverPhotoThumb);

            }
        }

        // Here is where we are showing the profile picture in header of Drawer

        assert currentUser != null;
        if (currentUser.getPhotoUrl() != null){

            String photo = String.valueOf(R.drawable.profile_default_photo);

        } else
        {
            currentUser.getPhotoUrl();
        }

        BezelImageView profilePhotoThumb = (BezelImageView) accountHeader.getView().findViewById(R.id.material_drawer_account_header_current);
        if (currentUser.getPhotoUrl() != null) {
            Glide.with(activity.getActivity())
                    .load(currentUser.getPhotoUrl())
                    .placeholder(R.drawable.profile_default_photo)
                    .error(R.drawable.profile_default_photo)
                    .into(profilePhotoThumb);
        } else if (currentUser.getPhotoUrl() != null) {
            Glide.with(activity.getActivity())
                    .load(R.drawable.profile_default_photo)
                    .placeholder(R.drawable.profile_default_photo)
                    .error(R.drawable.profile_default_photo)
                    .into(profilePhotoThumb);
        }

        Drawer drawer = new DrawerBuilder()
                .withActivity(activity.getActivity())
                .withToolbar(activity.getToolbar())
                .withAccountHeader(accountHeader)
                .withDisplayBelowStatusBar(true)
                .withActionBarDrawerToggle(true)

                // Menu drawer items scrollable
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_around_me).withIcon(R.drawable.menu_around).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_hot_or_not).withIcon(R.drawable.menu_hot).withIdentifier(2),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_messaging).withIcon(R.drawable.menu_messaging).withIdentifier(3),


                        new SectionDrawerItem().withName(R.string.vip_features_titlte),

                        new PrimaryDrawerItem().withName(R.string.drawer_item_visitors).withIcon(R.drawable.menu_visitor).withIdentifier(4),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_passport).withIcon(R.drawable.menu_travel).withIdentifier(5),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_private).withIcon(R.drawable.private_prifile).withIdentifier(6)

                )


                // Menu drawer items Fixed
                .addStickyDrawerItems(
                        new SecondaryDrawerItem().withName(R.string.drawer_item_profile).withIcon(R.drawable.menu_profile).withIdentifier(7),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(R.drawable.menu_settingss).withIdentifier(8)
                )
                .build();
        drawer.setSelection(activity.getDriwerId());
        drawer.setOnDrawerItemClickListener(new NavigationDrawerItemClickListener(activity.getActivity()));
        return drawer;


    }

    private static class NavigationDrawerItemClickListener implements Drawer.OnDrawerItemClickListener{
        private Activity mActivity;
        private User mCurrentUser;


        NavigationDrawerItemClickListener(Activity activity){
            mActivity = activity;

            mCurrentUser = new User();

            final DatabaseReference userFacebook = FirebaseDatabase.getInstance().getReference("users").child(User.getCurrentUserId());
            userFacebook.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    mCurrentUser = dataSnapshot.getValue(User.class);

                    //Toast.makeText(getActivity(), dataSnapshot.child("desc").getValue().toString(), Toast.LENGTH_LONG).show();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        @Override
        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {


            if (drawerItem != null) {
                //Intent intent = null;
                if (drawerItem.getIdentifier() == 1) {
                    Intent userNearMeIntent = new Intent(mActivity, AroundMeActivity.class);
                    mActivity.startActivity(userNearMeIntent);
                } else if (drawerItem.getIdentifier() == 2) {

                    Intent startMatchIntent = new Intent(mActivity, HotOrNotActivity.class);
                    mActivity.startActivity(startMatchIntent);
                    mActivity.finish();

                } else if (drawerItem.getIdentifier() == 3) {

                    Intent messageListIntent = new Intent(mActivity, ListChatActivity.class);
                    mActivity.startActivity(messageListIntent);

                }  else if (drawerItem.getIdentifier() == 4) {
                    if(mCurrentUser != null) {
                        if (mCurrentUser.getIsVip().equals("vip")) {

                            Intent mapslIntent = new Intent(mActivity, MyVisitorsActivity.class);
                            mActivity.startActivity(mapslIntent);
                        }

                        else if (mCurrentUser.getIsVisitor()) {

                            Intent mapslIntent = new Intent(mActivity, MyVisitorsActivity.class);
                            mActivity.startActivity(mapslIntent);

                        } else {
                            Intent travelIntent = new Intent(mActivity, VisitorActivity.class);
                            mActivity.startActivity(travelIntent);
                        }
                    }
                } else if (drawerItem.getIdentifier() == 5) {
                    if(mCurrentUser != null) {
                        if (mCurrentUser.getIsVip().equals("vip")) {

                            Intent WhoseeIntent = new Intent(mActivity, PassportActivity.class);
                            mActivity.startActivity(WhoseeIntent);
                        }

                        else if (mCurrentUser.getIsTravel()) {

                            Intent WhoseeIntent = new Intent(mActivity, PassportActivity.class);
                            mActivity.startActivity(WhoseeIntent);

                        } else {

                            Intent visitorIntent = new Intent(mActivity, TravelActivity.class);
                            mActivity.startActivity(visitorIntent);
                        }

                    }
                }

                 else if (drawerItem.getIdentifier() == 6) {


                    Intent PrivateIntent = new Intent(mActivity, PrivateProfileActivity.class);
                    mActivity.startActivity(PrivateIntent);

                }else if (drawerItem.getIdentifier() == 7) {

                    Intent profileIntent = new Intent(mActivity, MyProfile.class);
                    mActivity.startActivity(profileIntent);

                } else if (drawerItem.getIdentifier() == 8) {

                    Intent settingsIntent = new Intent(mActivity, SettingsActivity.class);
                    mActivity.startActivity(settingsIntent);
                }
            }

            return false;
        }
    }

    // Menu icons are inflated just as they were with actionbar


}
