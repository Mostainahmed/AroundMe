package com.aroundme.mostain.AroundMe.NearMe.Old.Male;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aroundme.mostain.AroundMe.NearMe.Old.NewUsersViewHolder;
import com.aroundme.mostain.Class.User;
import com.aroundme.mostain.R;
import com.aroundme.mostain.Utils.Firebase.FilterableFirebaseArray;
import com.aroundme.mostain.Utils.Internet.CheckServer;
import com.aroundme.mostain.Utils.service.ServiceUtils;
import com.firebase.ui.database.ClassSnapshotParser;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.util.Locale;

public class UsersMaleFragment extends Fragment {

    private OnPostSelectedListener mListener;

    private User mUser;
    private User mCurrentUser;

    private CheckServer mCheckServer;

    // Show users list

    private RelativeLayout mProgressLayout;
    private RelativeLayout mEmptyLayout;
    private LinearLayout mInternet;
    private TextView mIntenetText;
    //private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter<NewUsersViewHolder> mAdapter;

    // endregion
    private OnFragmentInteractionListener mListener2;

    public UsersMaleFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_users,container,false);

        getActivity().registerReceiver(this.mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

//
        mInternet = view.findViewById(R.id.linearLayout22);
        mIntenetText = view.findViewById(R.id.mIntenetText);
        mProgressLayout = view.findViewById(R.id.prograss_layout);
        mEmptyLayout = view.findViewById(R.id.empty_layout);
        mRecyclerView =  view.findViewById(R.id.rv);

        mCheckServer = view.findViewById(R.id.wait_for_internet_connection);

        mProgressLayout.setVisibility(View.VISIBLE);
        mEmptyLayout.setVisibility(View.GONE);

        mCurrentUser = new User();

        final DatabaseReference userFacebook = FirebaseDatabase.getInstance().getReference("users").child(User.getUser().getUid());
        userFacebook.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mCurrentUser = dataSnapshot.getValue(User.class);

                //Toast.makeText(getActivity(), dataSnapshot.child("desc").getValue().toString(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setBackgroundResource(R.color.white);
        mRecyclerView.setBackgroundColor(Color.WHITE);

        DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");

        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() >= 1 ){

                    mProgressLayout.setVisibility(View.GONE);
                    mEmptyLayout.setVisibility(View.GONE);

                } else {

                    mProgressLayout.setVisibility(View.GONE);
                    mEmptyLayout.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Query query = users.orderByChild("isMale").equalTo(true);
        ClassSnapshotParser parser = new ClassSnapshotParser<>(User.class);
        FilterableFirebaseArray filterableFirebaseArray = new FilterableFirebaseArray(query, parser);

        // remove current user from query result
        filterableFirebaseArray.addExclude(User.getCurrentUserId());

        FirebaseRecyclerAdapter mAdapter = new FirebaseRecyclerAdapter<User, NewUsersViewHolder>(filterableFirebaseArray, R.layout.users_item, NewUsersViewHolder.class) {
            @Override
            protected void populateViewHolder(NewUsersViewHolder viewHolder, User post, int position) {

                setupPost(viewHolder, post);
            }
        };

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                if (itemCount > 0){

                    mProgressLayout.setVisibility(View.GONE);
                    mEmptyLayout.setVisibility(View.GONE);

                } else if (itemCount == 0){

                    mProgressLayout.setVisibility(View.GONE);
                    mEmptyLayout.setVisibility(View.VISIBLE);

                }

            }
        });

        mRecyclerView.setAdapter(mAdapter);

    }

    private void setupPost(final NewUsersViewHolder newUsersViewHolder, final User user) {
        if (user.getPhotoThumb() != null){
            newUsersViewHolder.setPhoto(user.getPhotoThumb());

        } else {

            newUsersViewHolder.setPhoto(user.getPhotoUrl());
        }


        if (user.getFirstname() != null){

            newUsersViewHolder.setUser(user.getFirstname(), user.getUid());
        } else {

            newUsersViewHolder.setUser(user.getName(), user.getUid());
        }
        if (user.getbirthdate() != 0){

            LocalDate birthdate = new LocalDate(user.getbirthdate());              //Birth date
            LocalDate now = new LocalDate();                                       //Today's date
            Period period = new Period(birthdate,now, PeriodType.yearMonthDay());

            //int ages = period.getYears();
            final Integer ageInt = period.getYears();
            final String ageS = ageInt.toString();

            newUsersViewHolder.setAge(ageS);
        } else {

            newUsersViewHolder.setAge("18+");
        }

        if (System.currentTimeMillis() - user.getTimestamp() > ServiceUtils.TIME_TO_OFFLINE) {

            newUsersViewHolder.setOffline(String.valueOf(R.drawable.ic_offline_15_0_alizarin));

        } else if (System.currentTimeMillis() - user.getTimestamp() > ServiceUtils.TIME_TO_SOON) {

            newUsersViewHolder.setSoon(String.valueOf(R.drawable.last_min));
        } else {

            newUsersViewHolder.setOnline(String.valueOf(R.drawable.ic_online_15_0_alizarin));
        }

        if (mCurrentUser.getlog() != null && mCurrentUser.getlat() != null){

            Location loc1 = new Location("");

            loc1.setLatitude(mCurrentUser.getlog());
            loc1.setLongitude(mCurrentUser.getlat());

            if (user.getlog() != null && user.getlat() != null){

                Location loc2 = new Location("");
                loc2.setLatitude(user.getlog());
                loc2.setLongitude(user.getlat());

                float distanceInMeters = loc1.distanceTo(loc2);

                newUsersViewHolder.setDistance(String.format(Locale.ENGLISH, "%.2f km", distanceInMeters));
            }


        } else {


            newUsersViewHolder.setDistance(String.format(Locale.ENGLISH, "%.2f km", 0.00));
        }


    }

    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            NetworkInfo currentNetworkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if(currentNetworkInfo.isConnected()){

                mIntenetText.setText("Connecting...");
                mIntenetText.setTextColor(Color.WHITE);
                mInternet.setBackgroundColor(Color.GRAY);

                mCheckServer.checkInternetConnection(() -> {

                    mIntenetText.setText("Connected");
                    mIntenetText.setTextColor(Color.WHITE);
                    mInternet.setBackgroundColor(Color.GREEN);
                    mInternet.setVisibility(View.GONE);


                    mCheckServer.close();

                });

            }else{

                //Toast.makeText(getApplicationContext(), "Not Connected", Toast.LENGTH_LONG).show();

                mInternet.setBackgroundColor(Color.RED);
                mInternet.setVisibility(View.VISIBLE);
                mIntenetText.setText("No internet connection");
                mIntenetText.setTextColor(Color.WHITE);

            }
        }
    };

    public interface OnPostSelectedListener {
        void onPostComment(String postKey);
        void onPostLike(String postKey);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null && mAdapter instanceof FirebaseRecyclerAdapter) {
            ((FirebaseRecyclerAdapter) mAdapter).cleanup();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        /*int recyclerViewScrollPosition = getRecyclerViewScrollPosition();
        Log.d(TAG, "Recycler view scroll position: " + recyclerViewScrollPosition);
        savedInstanceState.putSerializable(KEY_LAYOUT_POSITION, recyclerViewScrollPosition);*/

        super.onSaveInstanceState(savedInstanceState);
    }



    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener2 = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

    }

    public OnFragmentInteractionListener getmListener() {
        return mListener2;
    }

    public void setmListener(OnFragmentInteractionListener mListener2) {
        this.mListener2 = mListener2;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
