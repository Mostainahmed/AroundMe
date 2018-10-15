package com.aroundme.mostain.AroundMe.NearMe;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.aroundme.mostain.App.Application;
import com.aroundme.mostain.AroundMe.NearMe.Both.UsersBothFragment;
import com.aroundme.mostain.AroundMe.NearMe.Matchs.UsersMatchsFragment;
import com.aroundme.mostain.Class.User;
import com.aroundme.mostain.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NearMeFragment extends Fragment {

    ViewPager viewPager;
    TabLayout tabLayout;
    private AdView mAdView;
    User CurrentUser;
    private OnFragmentInteractionListener mListener;
    private LinearLayout mTabsLinearLayout;

    // region Listeners
    private ViewPager.OnPageChangeListener mTabsOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position,float positionOffset,int positionOffsetPixels) {
            // mTabs.setTranslationY(0);
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public NearMeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_explore,container,false);

        setHasOptionsMenu(true);

        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        tabLayout = (TabLayout) v.findViewById(R.id.tab);
        mAdView = (AdView) v.findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest
                .Builder()
                //.addTestDevice("E1B16FD83EF27F4B1E5D5F6AC91B63BF")
                .build();

        final DatabaseReference userFacebook = FirebaseDatabase.getInstance().getReference("users").child(User.getCurrentUserId());
        userFacebook.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                CurrentUser = dataSnapshot.getValue(User.class);

                if (CurrentUser != null){

                    // Ads logic verification
                    if (CurrentUser.getFreeAds()) {
                        // Hide ads
                        mAdView.setVisibility(View.GONE);

                    } else if (CurrentUser.getIsVip().equals("vip")){
                        // Hide ads
                        mAdView.setVisibility(View.GONE);

                    } else {

                        // Show ads
                        if (Application.getInstance().getString(R.string.enable_ads).equals("true")){

                            mAdView.loadAd(adRequest);

                        } else {

                            mAdView.setVisibility(View.GONE);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        //setupTabIcons();

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());

        viewPagerAdapter.addFragment(new UsersBothFragment(),"Nearby");
        viewPagerAdapter.addFragment(new UsersMatchsFragment(),"Matches");
        //viewPagerAdapter.addFragment(new UsersFemaleFragment(),"Girls");

        viewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public OnFragmentInteractionListener getmListener() {
        return mListener;
    }

    public void setmListener(OnFragmentInteractionListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

    @Override
    public void onResume() {
        super.onResume();


    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        List<Fragment> fragmentList = new ArrayList<>();
        List<String> fragmentTitles = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitles.get(position);

            // return null to display only the icon
            //return null;
        }

        public void addFragment(Fragment fragment, String name) {
            fragmentList.add(fragment);
            fragmentTitles.add(name);
        }
    }
}
