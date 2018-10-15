package com.aroundme.mostain.AroundMe.NearMe.Old;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aroundme.mostain.AroundMe.NearMe.Old.NewUsersViewHolder;
import com.aroundme.mostain.Class.User;
import com.aroundme.mostain.R;
import com.aroundme.mostain.Utils.Firebase.FirebaseUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.PropertyNamingStrategy;

public class UsersAdapter extends RecyclerView.Adapter<NewUsersViewHolder> {
    private final String TAG = "UsersQueryAdapter";
    private List<String> mUsersPaths;
    private OnSetupViewListener mOnSetupViewListener;

    public UsersAdapter(List<String> paths, OnSetupViewListener onSetupViewListener) {
        if (paths == null || paths.isEmpty()) {
            mUsersPaths = new ArrayList<>();
        } else {
            mUsersPaths = paths;
        }
        mOnSetupViewListener = onSetupViewListener;
    }

    @Override
    public NewUsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.users_item, parent, false);
        return new NewUsersViewHolder(v);
    }

    public void setPaths(List<String> postPaths) {
        mUsersPaths = postPaths;
        notifyDataSetChanged();
    }

    public void addItem(String path) {
        mUsersPaths.add(path);
        notifyItemInserted(mUsersPaths.size());
    }

    @Override
    public void onBindViewHolder(final NewUsersViewHolder holder, int position) {
        DatabaseReference ref = FirebaseUtil.getUsersRef().child(mUsersPaths.get(position));
        // TODO: Fix this so async event won't bind the wrong view post recycle.
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Log.d(TAG, "post key: " + dataSnapshot.getKey());
                mOnSetupViewListener.onSetupView(holder, user, holder.getAdapterPosition(), dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e(TAG, "Error occurred: " + firebaseError.getMessage());
            }
        };
        ref.addValueEventListener(postListener);
        holder.mUsersRef = ref;
        holder.mUserListener = postListener;
    }

    @Override
    public void onViewRecycled(NewUsersViewHolder holder) {
        super.onViewRecycled(holder);
        holder.mUsersRef.removeEventListener(holder.mUserListener);
    }

    @Override
    public int getItemCount() {
        return mUsersPaths.size();
    }

    public interface OnSetupViewListener {
        void onSetupView(NewUsersViewHolder holder, User user, int position, String postKey);
    }
}