package com.aroundme.mostain.AroundMe.NearMe.Matchs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
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

import com.angopapo.aroundme2.Adapters.UsersAdapter;
import com.angopapo.aroundme2.Class.User;
import com.angopapo.aroundme2.R;
import com.angopapo.aroundme2.Utils.Internet.CheckServer;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoQuery;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class UsersMatchsFragment extends Fragment {

    private CheckServer mCheckServer;

    private RelativeLayout mProgressLayout;
    private RelativeLayout mEmptyLayout;
    private LinearLayout mInternet;
    private TextView mIntenetText;
    private RecyclerView mRecyclerView;

    // New Adapter
    private UsersAdapter newusersAdapter;
    private List<User> users;

    // GeoFire
    private GeoFire geofire;
    private Set<GeoQuery> geoQueries = new HashSet<>();
    DatabaseReference database;

    private User mCurrentUser;

    // endregion
    private OnFragmentInteractionListener mListener2;

    public UsersMatchsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_matches_users,container,false);

        getActivity().registerReceiver(this.mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        mInternet =  view.findViewById(R.id.linearLayout22);
        mIntenetText =  view.findViewById(R.id.mIntenetText);
        mProgressLayout = view.findViewById(R.id.prograss_layout);
        mEmptyLayout =  view.findViewById(R.id.empty_layout);
        mRecyclerView =  view.findViewById(R.id.rv);

        mCheckServer = view.findViewById(R.id.wait_for_internet_connection);

        mProgressLayout.setVisibility(View.GONE);
        mEmptyLayout.setVisibility(View.VISIBLE);

        mCurrentUser = new User();

        final DatabaseReference userFacebook = FirebaseDatabase.getInstance().getReference("users").child(User.getCurrentUserId());
        userFacebook.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mCurrentUser = dataSnapshot.getValue(User.class);

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

        users = new ArrayList<>();

        newusersAdapter = new UsersAdapter(getActivity(), users);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setAdapter(newusersAdapter);

        database = FirebaseDatabase.getInstance().getReference().child("users");
        database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot.exists() && !dataSnapshot.getKey().equals(User.getUser().getUid())) {
                    if (dataSnapshot.child("connections").hasChild(User.getUser().getUid())) {

                        //mProgressLayout.setVisibility(View.GONE);
                        //mEmptyLayout.setVisibility(View.GONE);

                        User user = dataSnapshot.getValue(User.class);

                        users.add(user);
                        newusersAdapter.notifyDataSetChanged();

                        mRecyclerView.setAdapter(newusersAdapter);

                    }
                }

                if (newusersAdapter.getItemCount() > 0){

                    mProgressLayout.setVisibility(View.GONE);
                    mEmptyLayout.setVisibility(View.GONE);
                }

            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                newusersAdapter.notifyDataSetChanged();
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                newusersAdapter.notifyDataSetChanged();
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                newusersAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            NetworkInfo currentNetworkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if(currentNetworkInfo.isConnected()){

                mIntenetText.setText("Connecting...");
                mIntenetText.setTextColor(Color.WHITE);
                mInternet.setBackgroundColor(Color.GRAY);

                mCheckServer.checkInternetConnection(new CheckServer.OnConnectionIsAvailableListener() {
                  @Override
                  public void onConnectionIsAvailable() {

                      mIntenetText.setText("Connected");
                      mIntenetText.setTextColor(Color.WHITE);
                      mInternet.setBackgroundColor(Color.GREEN);
                      mInternet.setVisibility(View.GONE);


                mCheckServer.close();

                   }
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

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
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
