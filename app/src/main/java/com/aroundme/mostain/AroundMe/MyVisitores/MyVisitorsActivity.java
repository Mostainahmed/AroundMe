package com.aroundme.mostain.AroundMe.MyVisitores;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angopapo.aroundme2.App.BaseActivity;
import com.angopapo.aroundme2.App.NavigationDrawer;
import com.angopapo.aroundme2.Class.User;
import com.angopapo.aroundme2.R;
import com.angopapo.aroundme2.Utils.Firebase.FilterableFirebaseArray;
import com.angopapo.aroundme2.Utils.Helper.ActivityWithToolbar;
import com.angopapo.aroundme2.Utils.Internet.CheckServer;
import com.angopapo.aroundme2.ViewHolder.VisitorsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.Drawer;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.Locale;

//import com.angopapo.aroundme2.Class.User;

public class MyVisitorsActivity extends BaseActivity implements ActivityWithToolbar {

    private Toolbar mToolbar;
    private Drawer drawer;
    private LinearLayout mNoUsersFound;
    private LinearLayout mLoadingMessages;
    private Button mRetryButton;

    private User mCurrentUser;

    private CheckServer mCheckServer;

    // Show users list

    private RelativeLayout mProgressLayout;
    private RelativeLayout mEmptyLayout;
    private LinearLayout mInternet;
    private TextView mIntenetText;



    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter<VisitorsViewHolder> mAdapter;

    private FilterableFirebaseArray filterableFirebaseArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_visitors);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.drawer_item_visitor);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawer = NavigationDrawer.createDrawer(this);

        getActivity().registerReceiver(this.mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        mInternet = (LinearLayout) findViewById(R.id.linearLayout22);
        mIntenetText = (TextView) findViewById(R.id.mIntenetText);
        mProgressLayout = (RelativeLayout) findViewById(R.id.prograss_layout);
        mEmptyLayout = (RelativeLayout) findViewById(R.id.empty_layout);
        //swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefreshlayout);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        mCheckServer = (CheckServer)findViewById(R.id.wait_for_internet_connection);

        //swipeRefreshLayout.setRefreshing(true);
        mProgressLayout.setVisibility(View.VISIBLE);
        mEmptyLayout.setVisibility(View.GONE);




        mInternet = (LinearLayout)findViewById(R.id.linearLayout22);

        mNoUsersFound = (LinearLayout) findViewById(R.id.noUsersFound);
        mRetryButton = (Button) findViewById(R.id.button_retry);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setBackgroundResource(R.color.white);
        mRecyclerView.setBackgroundColor(Color.WHITE);

        mCurrentUser = new User();

        /*final DatabaseReference userFacebook = FirebaseDatabase.getInstance().getReference("users").child(User.getCurrentUserId());
        userFacebook.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mCurrentUser = dataSnapshot.getValue(User.class);

                assert mCurrentUser != null;
                if (mCurrentUser.getlat() != null && mCurrentUser.getlog() != null){

                    //QueryWithAll();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;
        DatabaseReference visitores = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("visitors");

        visitores.addValueEventListener(new ValueEventListener() {
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

        Query query = visitores.orderByChild("lastseenView").endAt(System.currentTimeMillis());

        mAdapter = getFirebaseRecyclerAdapter(query);


        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);


            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);


            }
        });

        mRecyclerView.setAdapter(mAdapter);


    }

    private FirebaseRecyclerAdapter<User, VisitorsViewHolder> getFirebaseRecyclerAdapter(Query query) {
        return new FirebaseRecyclerAdapter<User, VisitorsViewHolder>(
                User.class, R.layout.visitors_item, VisitorsViewHolder.class, query) {
            @Override
            public void populateViewHolder(final VisitorsViewHolder visitorsViewHolder,
                                           final User visitors, final int position) {
                setupPost(visitorsViewHolder, visitors, position, null);
            }

            @Override
            public void onViewRecycled(VisitorsViewHolder holder) {
                super.onViewRecycled(holder);
//                FirebaseUtil.getLikesRef().child(holder.mPostKey).removeEventListener(holder.mLikeListener);
            }
        };
    }

    private void setupPost(final VisitorsViewHolder visitorsViewHolder, final User visitors, final int position, final String inPostKey) {


        if (visitors.getPhotoThumb() != null){
            visitorsViewHolder.setPhoto(visitors.getPhotoThumb());

        } else {

            visitorsViewHolder.setPhoto(visitors.getPhotoUrl());
        }


        if (visitors.getFirstname() != null){

            visitorsViewHolder.setUser(visitors.getFirstname(), visitors.getUid());
        } else {

            visitorsViewHolder.setUser(visitors.getName(), visitors.getUid());
        }

        if (visitors.getbirthdate() != 0){

            LocalDate birthdate = new LocalDate(visitors.getbirthdate());          //Birth date
            LocalDate now = new LocalDate();                                         //Today's date
            Period period = new Period(birthdate,now, PeriodType.yearMonthDay());

            //int ages = period.getYears();
            final Integer ageInt = period.getYears();
            final String ageS = ageInt.toString();

            visitorsViewHolder.setAge(ageS);
        } else {

            visitorsViewHolder.setAge("18+");
        }



        // Particular for visitor begin here

        if (visitors.getCountViews() != 0){

            visitorsViewHolder.setmViewsCount(String.valueOf(visitors.getCountViews()));

        } else {
            visitorsViewHolder.setmViewsCount("1");
        }

        if (visitors.getLastseenView() != 0){

            DateTime senttime = new DateTime(visitors.getLastseenView());

            String time = new PrettyTime().format( senttime.toDate());

            visitorsViewHolder.setmLastSeen(time);

        }

        if (visitors.getLastseenView() != 0) {

            DateTime visitdate = new DateTime(visitors.getLastseenView());          //Birth date
            DateTime now = new DateTime();                                         //Today's date
            Period period = new Period(visitdate,now, PeriodType.yearMonthDayTime());

            final Integer daysInt = period.getDays();

            if (daysInt <= 1){

                visitorsViewHolder.setmNewVisitor(1);

            } else {

                visitorsViewHolder.setmNewVisitor(0);
            }

        } else {

            visitorsViewHolder.setmNewVisitor(0);
        }

        // Particular for visitor ends here

        if (mCurrentUser.getlog() != null && mCurrentUser.getlat() != null){

            Location loc1 = new Location("");

            loc1.setLatitude(mCurrentUser.getlog());
            loc1.setLongitude(mCurrentUser.getlat());

            if (visitors.getlog() != null && visitors.getlat() != null){

                Location loc2 = new Location("");
                loc2.setLatitude(visitors.getlog());
                loc2.setLongitude(visitors.getlat());

                float distanceInMeters = loc1.distanceTo(loc2);

                visitorsViewHolder.setDistance(String.format(Locale.ENGLISH, "%.2f km", distanceInMeters));
            }


        } else {


            visitorsViewHolder.setDistance(String.format(Locale.ENGLISH, "%.2f km", 0.00));
        }



        /*if (System.currentTimeMillis() - visitors.getTimestamp() > ServiceUtils.TIME_TO_OFFLINE) {

            visitorsViewHolder.setOffline(String.valueOf(R.drawable.ic_offline_15_0_alizarin));

        } else if (System.currentTimeMillis() - visitors.getTimestamp() > ServiceUtils.TIME_TO_SOON) {

            visitorsViewHolder.setSoon(String.valueOf(R.drawable.last_min));
        } else {

            visitorsViewHolder.setOnline(String.valueOf(R.drawable.ic_online_15_0_alizarin));
        }*/


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    @Override
    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public int getDriwerId() {
        return 4;
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {

    }

    @Override
    public void onSizeChanged(int height) {

    }

    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
            boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

            NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

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

    /*@Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent profileIntent = new Intent(this, ProfileUerActivity.class);
        profileIntent.putExtra(ProfileUerActivity.EXTRA_USER_ID, mMyVisitorsAdapter.getItem(position).getObjectId());
        startActivity(profileIntent);
    }*/

    // Verify every 45s if user still online tell the server.



}
