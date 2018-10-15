package com.aroundme.mostain.AroundMe.Messaging.ChatList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.angopapo.aroundme2.Class.Chat;
import com.angopapo.aroundme2.Class.User;
import com.angopapo.aroundme2.R;
import com.angopapo.aroundme2.Utils.Firebase.FilterableFirebaseArray;
import com.angopapo.aroundme2.Utils.Helper.ActivityWithToolbar;
import com.angopapo.aroundme2.Utils.Internet.CheckServer;
import com.angopapo.aroundme2.Utils.service.Constants;
import com.angopapo.aroundme2.Utils.service.ServiceUtils;
import com.angopapo.aroundme2.ViewHolder.ListChatViewHolder;
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
import org.ocpsoft.prettytime.PrettyTime;

//import com.angopapo.aroundme2.Class.User;

public class ListChatActivity extends BaseActivity implements ActivityWithToolbar {

    private Toolbar mToolbar;
    private Drawer drawer;


    private User mCurrentUser;

    private CheckServer mCheckServer;

    // Show users list

    private RelativeLayout mProgressLayout;
    private RelativeLayout mEmptyLayout;
    private LinearLayout mInternet;
    private TextView mIntenetText;



    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter<ListChatViewHolder> mAdapter;

    private FilterableFirebaseArray filterableFirebaseArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat_list);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.drawer_item_messaging);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawer = NavigationDrawer.createDrawer(this);

        getActivity().registerReceiver(this.mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        mInternet =  findViewById(R.id.linearLayout22);
        mIntenetText =  findViewById(R.id.mIntenetText);
        mProgressLayout =  findViewById(R.id.prograss_layout);
        mEmptyLayout =  findViewById(R.id.empty_layout);
        //swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefreshlayout);
        mRecyclerView =  findViewById(R.id.rv);
        mCheckServer = findViewById(R.id.wait_for_internet_connection);

        //swipeRefreshLayout.setRefreshing(true);
        mProgressLayout.setVisibility(View.VISIBLE);
        mEmptyLayout.setVisibility(View.GONE);




        mInternet = findViewById(R.id.linearLayout22);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setBackgroundResource(R.color.white);
        //mRecyclerView.setBackgroundColor(Color.WHITE);

        mCurrentUser = new User();

        initiate();

        mRecyclerView.setAdapter(mAdapter);


    }

    public void initiate(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;

        DatabaseReference visitores = FirebaseDatabase.getInstance().getReference(Constants.ARG_USERS).child(User.getUser().getUid()).child(Constants.ARG_CHAT_LIST);

        visitores.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() > 0 ){

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

        Query query = visitores.orderByChild("timestamp").endAt(System.currentTimeMillis());

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

    }

    private FirebaseRecyclerAdapter<Chat, ListChatViewHolder> getFirebaseRecyclerAdapter(Query query) {
        return new FirebaseRecyclerAdapter<Chat, ListChatViewHolder>(Chat.class, R.layout.chat_list_item, ListChatViewHolder.class, query) {
            @Override
            public void populateViewHolder(final ListChatViewHolder visitorsViewHolder, final Chat visitors, final int position) {
                setupPost(visitorsViewHolder, visitors, position, null);
            }

            @Override
            public void onViewRecycled(ListChatViewHolder holder) {
                super.onViewRecycled(holder);
//                FirebaseUtil.getLikesRef().child(holder.mPostKey).removeEventListener(holder.mLikeListener);
            }
        };
    }

    private void setupPost(final ListChatViewHolder chatListViewHolder, final Chat chat, final int position, final String inPostKey) {

        if (chat.getSenderUid().equals(User.getUser().getUid())) {

            // Text Message
            DatabaseReference database = FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).child(chat.getReceiverUid());
            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);

                    assert user != null;
                    if (user.getisOnline()) {

                        chatListViewHolder.setOnline(String.valueOf(R.drawable.ic_online_15_0_alizarin));

                    } else {

                        if (System.currentTimeMillis() - user.getTimestamp() > ServiceUtils.TIME_TO_OFFLINE) {

                            chatListViewHolder.setOffline(String.valueOf(R.drawable.ic_offline_15_0_alizarin));

                        } else if (System.currentTimeMillis() - user.getTimestamp() > ServiceUtils.TIME_TO_SOON) {

                            chatListViewHolder.setSoon(String.valueOf(R.drawable.last_min));
                        }
                    }


                    if (user.getPhotoThumb() != null) {
                        chatListViewHolder.setPhoto(user.getPhotoThumb());

                    } else {


                        chatListViewHolder.setPhoto(user.getPhotoUrl());
                    }


                    if (user.getName() != null) {

                        chatListViewHolder.setUser(user.getName(), user.getUid(), user.getFcmUserDeviceId());
                    } else {

                        chatListViewHolder.setUser("no_name", user.getUid(), user.getFcmUserDeviceId());
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            if (chat.getType() == 1) {


                chatListViewHolder.setLastMessage(chat.getMessage());

            } else if (chat.getType() == 2) {


                chatListViewHolder.setLastMessage("Audio message");

            } else if (chat.getType() == 3) {

                chatListViewHolder.setLastMessage("Image message");

            }

            DateTime senttime = new DateTime(chat.getTimestamp());

            String time = new PrettyTime().format(senttime.toDate());

            chatListViewHolder.setTime(time);

            if (chat.isIsread()) {

                chatListViewHolder.setRead();
            } else {
                chatListViewHolder.setUnRead();

            }


        } else if (chat.getReceiverUid().equals(User.getUser().getUid())) {

            // Text Message
            DatabaseReference database = FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).child(chat.getSenderUid());
            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);

                    assert user != null;
                    if (user.getPhotoThumb() != null) {

                        chatListViewHolder.setPhoto(user.getPhotoThumb());

                    } else {


                        chatListViewHolder.setPhoto(user.getPhotoUrl());
                    }


                    if (user.getisOnline()) {

                        chatListViewHolder.setOnline(String.valueOf(R.drawable.ic_online_15_0_alizarin));

                    } else {

                        if (System.currentTimeMillis() - user.getTimestamp() > ServiceUtils.TIME_TO_OFFLINE) {

                            chatListViewHolder.setOffline(String.valueOf(R.drawable.ic_offline_15_0_alizarin));

                        } else if (System.currentTimeMillis() - user.getTimestamp() > ServiceUtils.TIME_TO_SOON) {

                            chatListViewHolder.setSoon(String.valueOf(R.drawable.last_min));
                        }
                    }


                    if (user.getName() != null) {

                        chatListViewHolder.setUser(user.getName(), user.getUid(), user.getFcmUserDeviceId());
                    } else {

                        chatListViewHolder.setUser("no_name", user.getUid(), user.getFcmUserDeviceId());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            if (chat.getType() == 1) {

                if (chat.isIsread()) {

                    chatListViewHolder.setLastMessage(chat.getMessage());

                } else {

                    chatListViewHolder.setLastMessageBold(chat.getMessage());
                }


            } else if (chat.getType() == 2) {

                if (chat.isIsread()) {

                    chatListViewHolder.setLastMessage("Image message");

                } else {

                    chatListViewHolder.setLastMessageBold("Image message");
                }


            } else if (chat.getType() == 3) {

                if (chat.isIsread()) {

                    chatListViewHolder.setLastMessage("Audio message");

                } else {

                    chatListViewHolder.setLastMessageBold("Audio message");
                }

            }

            DateTime senttime = new DateTime(chat.getTimestamp());

            String time = new PrettyTime().format(senttime.toDate());

            chatListViewHolder.setTime(time);

            chatListViewHolder.sethideStatus();

        }


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

    @Override
    public void recreate() {

        //initiate();

        super.recreate();
        this.onCreate(null);
    }

    @Override
    public void onResume() {

        //initiate();
        super.onResume();



        /*Intent intent = getIntent();
        finish();
        startActivity(intent);*/
    }

    @Override
    public void onRestart() {

        //initiate();
        super.onRestart();


        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);

        /*Intent intent = getIntent();
        finish();
        startActivity(intent);*/

    }

}
