package com.aroundme.mostain.App;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.aroundme.mostain.Class.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.greysonparrelli.permiso.Permiso;

/**
 * Created by maravilhosinga on 07/10/17.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        Permiso.getInstance().setActivity(this);

        // since I can connect from multiple devices, we store each connection instance separately
// any time that connectionsRef's value is null (i.e. has no children) I am offline
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

// stores the timestamp of my last disconnect (the last time I was seen online)
        final DatabaseReference lastOnlineRef = database.getReference("users").child(User.getUser().getUid());

        final DatabaseReference connectedRef = database.getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {

                    // when I disconnect, update the last time I was seen online
                    lastOnlineRef.child(User.LastTime).onDisconnect().setValue(ServerValue.TIMESTAMP);
                    lastOnlineRef.child(User.Online).onDisconnect().setValue(false);
                    lastOnlineRef.child(User.Online).setValue(true);

                } else {

                    lastOnlineRef.child(User.Online).setValue(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled at .info/connected");
            }
        });

        // New online logic underdevelopment
        /*final Map<String, Object> taskMap = new HashMap<>();
        taskMap.put(Constant.ACTIVE_STATUS.IS_ONLINE, "false");
        new Firebase(Constant.FIRE_BASE_URL).child(Constant.ACTIVE_STATUS.ACTIVE_STATUS)
                .child(mUserId)
                .onDisconnect().updateChildren(taskMap, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
            }
        });
        isUserOnline();*/
    }

    @Override protected void onStart() {
        super.onStart();
        if (User.getCurrentUserId() != null){

            setUserOnline(true);
        }
    }

    @Override protected void onPause() {
        super.onPause();

        if (User.getCurrentUserId() != null){

            setUserOnline(false);
        }

    }

    private void setUserOnline(final boolean online) {
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(User.Class).child(User.getCurrentUserId());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    userRef.child(User.Online).setValue(online);
                    if (User.getCurrentUserId() != null) {
                        FirebaseDatabase.getInstance().getReference().child("users/" + User.getCurrentUserId() + "/isOnline").setValue(online);
                        FirebaseDatabase.getInstance().getReference().child("users/" + User.getCurrentUserId() + "/timestamp").setValue(System.currentTimeMillis());
                    }

                }
            }

            @Override public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    // New USer online Logic user development
    /**
     * check user is online or not
     * if user is online it will set text Online
     */
    /*private void isUserOnline() {
        Firebase query = new Firebase(Constant.FIRE_BASE_URL).child(Constant.ACTIVE_STATUS.ACTIVE_STATUS).child(otherUsersId);

        ValueEventListener valueEventListenerOnline = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ActiveStatus isOnline = dataSnapshot.getValue(ActiveStatus.class);
                if (otherUsersId.equals(dataSnapshot.getKey())) {
                    if (isOnline != null)
                        if (isOnline.getIsOnline().equals("true")) {
                            ctvLastOnlineTimeChatToolbar.setVisibility(View.VISIBLE);
                            ctvLastOnlineTimeChatToolbar.setText("Online");
                        } else {
                            ctvLastOnlineTimeChatToolbar.setVisibility(View.GONE);
                            ctvLastOnlineTimeChatToolbar.setText("Offline");
                        }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };
        query.addValueEventListener(valueEventListenerOnline);
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permiso.getInstance().onRequestPermissionResult(requestCode, permissions, grantResults);
    }
}
