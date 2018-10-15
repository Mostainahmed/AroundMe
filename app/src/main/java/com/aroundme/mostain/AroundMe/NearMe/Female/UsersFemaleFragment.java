package com.aroundme.mostain.AroundMe.NearMe.Female;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aroundme.mostain.Adapters.UsersAdapter;
import com.aroundme.mostain.Class.User;
import com.aroundme.mostain.R;
import com.aroundme.mostain.Utils.Internet.CheckServer;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class UsersFemaleFragment extends Fragment {

    //private User mCurrentUser;

    private static final String LOG_TAG = "UsersBothFragment";

    private CheckServer mCheckServer;

    // Show users list

    private RelativeLayout mProgressLayout;
    private RelativeLayout mEmptyLayout;
    private LinearLayout mInternet;
    private TextView mIntenetText;
    private RecyclerView mRecyclerView;


    // New Adapter
    //private UsersAdapter NewusersAdapter;
    private List<User> users;

    // GeoFire
    private DatabaseReference database;
    private Query query;
    private GeoFire geofire;
    private Set<GeoQuery> geoQueries = new HashSet<>();

    //private List<User> users = new ArrayList<>();
    private ValueEventListener userValueListener;
    private boolean fetchedUserIds = false;
    private Set<String> userIdsWithListeners = new HashSet<>();

    private UsersAdapter adapter;
    private int initialListSize;
    private int iterationCount;
    private Location me;
    private Map<String, Location> userIdsToLocations = new HashMap<>();



    private User mCurrentUser;

    // endregion
    private OnFragmentInteractionListener mListener2;

    public UsersFemaleFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_users,container,false);

        getActivity().registerReceiver(this.mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        mInternet = (LinearLayout) view.findViewById(R.id.linearLayout22);
        mIntenetText = (TextView) view.findViewById(R.id.mIntenetText);
        mProgressLayout = (RelativeLayout) view.findViewById(R.id.prograss_layout);
        mEmptyLayout = (RelativeLayout) view.findViewById(R.id.empty_layout);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv);

        mCheckServer = (CheckServer)view.findViewById(R.id.wait_for_internet_connection);



        mProgressLayout.setVisibility(View.VISIBLE);
        mEmptyLayout.setVisibility(View.GONE);

        mCurrentUser = new User();

        final DatabaseReference userFacebook = FirebaseDatabase.getInstance().getReference("users").child(User.getUser().getUid());
        userFacebook.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mCurrentUser = dataSnapshot.getValue(User.class);

                assert mCurrentUser != null;
                if (mCurrentUser.getlat() != null && mCurrentUser.getlog() != null){

                    //Toast.makeText(getActivity(), mCurrentUser.getlat().toString() + " " + mCurrentUser.getlog().toString(), Toast.LENGTH_LONG).show();
                    fetchUsers();

                    me = new Location("me");
                    me.setLatitude(mCurrentUser.getlat());
                    me.setLongitude(mCurrentUser.getlog());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        setupFirebase();

        setupList();



        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

       /* StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setBackgroundResource(R.color.white);
        mRecyclerView.setBackgroundColor(Color.WHITE);

        users = new ArrayList<>();

        NewusersAdapter = new UsersAdapter(getActivity(), users);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setAdapter(NewusersAdapter);*/



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

    /////////////////////////////////////////

    private void fetchUsers() {

        removeListeners();
        GeoQuery geoQuery = geofire.queryAtLocation(new GeoLocation(mCurrentUser.getlat(), mCurrentUser.getlog()), 10);
        //geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                //Toast.makeText(getApplicationContext(), key + " found at " + location , Toast.LENGTH_LONG).show();

                Location to = new Location("to");
                to.setLatitude(location.latitude);
                to.setLongitude(location.longitude);
                if (!fetchedUserIds) {
                    userIdsToLocations.put(key, to);
                    //addUserListener(key);
                } else {
                    userIdsToLocations.put(key, to);
                   // addUserListener(key);
                }
                addUserListener(key);


            }

            @Override
            public void onKeyExited(String key) {
                Log.d(LOG_TAG, "onKeyExited: ");
                if (userIdsWithListeners.contains(key) && !userIdsWithListeners.contains(User.getUser().getUid())) {
                    int position = getUserPosition(key);
                    users.remove(position);
                    adapter.notifyItemRemoved(position);
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                Log.d(LOG_TAG, "onKeyMoved: ");
            }

            @Override
            public void onGeoQueryReady() {
                Log.d(LOG_TAG, "onGeoQueryReady: ");
                initialListSize = userIdsToLocations.size();
                if (initialListSize == 0) {
                    fetchedUserIds = true;
                }
                iterationCount = 0;

//                userIdsToLocations.keySet().forEach(this::addUserListener);

            }

            private void addUserListener(String userId) {

                DatabaseReference usersKey = FirebaseDatabase.getInstance().getReference("users").child(userId);
                usersKey.addListenerForSingleValueEvent(userValueListener);
                userIdsWithListeners.add(userId);
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.e(LOG_TAG, "onGeoQueryError: ", error.toException());
            }
        });

        geoQueries.add(geoQuery);
    }

    private void setupList() {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setBackgroundResource(R.color.white);
        mRecyclerView.setBackgroundColor(Color.WHITE);

        users = new ArrayList<>();

        adapter = new UsersAdapter(getActivity(), users);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setAdapter(adapter);

    }

    private void setupFirebase() {
        database = FirebaseDatabase.getInstance().getReference();
        geofire = new GeoFire(database.child("geofire"));

        setupListeners();

    }

    private void setupListeners() {
        userValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                /*if (dataSnapshot.getChildrenCount() == 0 ){

                    mProgressLayout.setVisibility(View.GONE);
                    mEmptyLayout.setVisibility(View.GONE);

                } else {

                    mProgressLayout.setVisibility(View.GONE);
                    mEmptyLayout.setVisibility(View.VISIBLE);

                }*/


                if (dataSnapshot.exists() && !dataSnapshot.getKey().equals(User.getUser().getUid())){

                        //if (dataSnapshot.child("isMale").toString().equals("false")){

                            mProgressLayout.setVisibility(View.GONE);
                            mEmptyLayout.setVisibility(View.GONE);

                            User user = dataSnapshot.getValue(User.class);
                            assert user != null;
                            user.setuid(dataSnapshot.getKey());


                            Location location = userIdsToLocations.get(dataSnapshot.getKey());
                            user.setlat(location.getLatitude());
                            user.setlog(location.getLongitude());

                            removeListeners();

                            if (users.contains(user)) {
                                userUpdated(user);
                                removeListeners();
                            } else {
                                newUser(user);
                                //removeListeners();
                           // }


                       // mRecyclerView.setAdapter(adapter);

                    }

                } else {

                    mProgressLayout.setVisibility(View.GONE);
                    mEmptyLayout.setVisibility(View.VISIBLE);

                }


            }

            private void newUser(User user) {
                Log.d(LOG_TAG, "onDataChange: new user");
                iterationCount++;
                //users.clear();
                //users.add(0, user);
                users.add(user);
                adapter.notifyDataSetChanged();

                if (!fetchedUserIds && iterationCount == initialListSize) {
                    fetchedUserIds = true;

                    //sortByDistanceFromMe();

                    adapter.setUsers(users);

                } else if (fetchedUserIds) {
                    //sortByDistanceFromMe();
                    adapter.notifyItemInserted(getIndexOfNewUser(user));
                }
            }

            private void userUpdated(User u) {
                Log.d(LOG_TAG, "onDataChange: update");
                int position = getUserPosition(u.getUid());
                users.set(position, u);
                adapter.notifyItemChanged(position);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG_TAG, "onCancelled: ", databaseError.toException());
            }
        };
    }

    private int getIndexOfNewUser(User u) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUid().equals(u.getUid())) {
                Log.d(LOG_TAG, "getIndexOfNewUser: " + i);
                return i;
            }
        }
        throw new RuntimeException();
    }

    private void sortByDistanceFromMe() {
        Collections.sort(users, (u1, u2) -> {
            Location first = new Location("");
            first.setLatitude(u1.getlat());
            first.setLongitude(u1.getlog());

            Location second = new Location("");
            second.setLatitude(u2.getlat());
            second.setLongitude(u2.getlog());

            if (me.distanceTo(first) > me.distanceTo(second)) {
                return 1;
            } else if (me.distanceTo(first) < me.distanceTo(second)) {
                return -1;
            } else {
                return 0;
            }
        });

        for (User user : users) {
            Location location = new Location("");
            location.setLatitude(user.getlat());
            location.setLongitude(user.getlog());

            Log.d(LOG_TAG, "newUser: distance " + me.distanceTo(location));
        }
    }

    private void removeListeners() {
        for (GeoQuery geoQuery : geoQueries) {
            geoQuery.removeAllListeners();
        }

        for (String userId : userIdsWithListeners) {
            database.child("users").child(userId)
                    .removeEventListener(userValueListener);
        }
    }

    private int getUserPosition(String id) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUid().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    /////////////////////////////////////////

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeListeners();

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
