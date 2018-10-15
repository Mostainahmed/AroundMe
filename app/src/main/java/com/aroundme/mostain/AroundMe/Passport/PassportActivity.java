package com.aroundme.mostain.AroundMe.Passport;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.angopapo.aroundme2.App.BaseActivity;
import com.angopapo.aroundme2.App.NavigationDrawer;
import com.angopapo.aroundme2.AroundMe.NearMe.AroundMeActivity;
import com.angopapo.aroundme2.AroundMe.Profile.EditProfileActivity;
import com.angopapo.aroundme2.Class.User;
import com.angopapo.aroundme2.R;
import com.angopapo.aroundme2.Utils.Helper.ActivityWithToolbar;
import com.angopapo.aroundme2.Utils.Location.models.GeocoderResult;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.Drawer;

import java.util.ArrayList;

import butterknife.ButterKnife;

import static com.angopapo.aroundme2.App.Application.MY_GEOFIRE;

public class PassportActivity extends BaseActivity implements ActivityWithToolbar, OnMapReadyCallback, SearchView.OnQueryTextListener, SearchAddressFragment.SearchAddressListener, View.OnClickListener {

    public static final int REQUEST_CODE_LOCATION = 1001;
    private static final String TAG = "myapp:PassportActivity";
    private static final int REQUEST_EXTERNAL_LOCATION = 12386;
    private static View v;
    int YOUR_FRAGMENT_POSITION = 4;
    RelativeLayout mSignUpActivity;
    private Toolbar mToolbar;
    private MenuItem mSearchMenuItem;
    private SearchView mSearchView;
    private SearchAddressFragment mSearchAddressFragment;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Marker mMarker;
    private Circle mCircle;
    Location mLastLocation;
    private GeocoderResult mGeocoderResult;
    //private ParseGeoPoint mParseGeoPoint;
    private ArrayList<LatLng> latlngs = new ArrayList<>();
    private Dialog progressDialog;
    private RelativeLayout mSettingsLayout;
    private User mCurrentUser;
    private Drawer drawer;

    private DatabaseReference userDB;

    public PassportActivity() {
        // Required empty public constructor
    }

    protected void log(String message) {
        Log.d(TAG,message);
    }

    protected void showMessage(String message) {
        Snackbar.make(mSignUpActivity,message, Snackbar.LENGTH_SHORT).show();
    }


    private ValueEventListener userListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Generally
            mCurrentUser = dataSnapshot.getValue(User.class);


        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            //Có lỗi xảy ra, không lấy đc dữ liệu
            Log.e(EditProfileActivity.class.getName(), "loadPost:onCancelled", databaseError.toException());
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_maps);

        ButterKnife.bind(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mSettingsLayout = (RelativeLayout) findViewById(R.id.layout_settings);
        Button mSaveLocationButton = (Button) findViewById(R.id.button_save_location);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Tape the loop to search");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawer = NavigationDrawer.createDrawer(this);


        userDB = FirebaseDatabase.getInstance().getReference("users").child(User.getCurrentUserId());
        userDB.addValueEventListener(userListener);

        mSaveLocationButton.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= 23) {


            // Marshmallow+
            PermissionRequestLocation();

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);


        } else {
            // Pre-Marshmallow

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            checkGPS();


        }

    }

    public void checkGPS() {


        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        //boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        /*try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}*/

        if (!gps_enabled
            //  && !network_enabled
                ) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage("GPS not enabled");
            dialog.setCancelable(false);

            dialog.setPositiveButton("Enable Now",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface,int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    getActivity().startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton("Don't ",new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface,int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();

        } else {

   /* PassportActivity fragment = (PassportActivity) getFragmentManager().findFragmentById(R.id.main_container);

    getActivity().getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();*/
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_maps, menu);
        mSearchMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView)mSearchMenuItem.getActionView();
        mSearchView.setOnQueryTextListener(this);
        return true;
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

    protected void searchLocation(String addressString) {
        if (mSearchAddressFragment == null) {
            mSearchAddressFragment = new SearchAddressFragment();
            Bundle args = new Bundle();
            args.putString(SearchAddressFragment.ARG_ADDRESS,addressString);
            mSearchAddressFragment.setArguments(args);
            mSearchAddressFragment.setSearchAddressListener(this);
            getSupportFragmentManager().beginTransaction().add(R.id.container,mSearchAddressFragment).commit();

        } else if (mSearchAddressFragment != null && !mSearchAddressFragment.isVisible()) {

            getSupportFragmentManager().beginTransaction().add(R.id.container,mSearchAddressFragment).commit();
            mSearchAddressFragment.getAddresses(addressString);
        } else {
            mSearchAddressFragment.getAddresses(addressString);
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */

    @Override
    public void onMapReady(GoogleMap map) {
        // Add a marker in Sydney, Australia, and move the camera.

        Log.d("Startmap","Mapping started");

        mMap = map;

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                //buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);

                checkGPS();
            } else {
                //Request Location Permission
                PermissionRequestLocation();


            }
        } else {
            //buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        //mMap.setMyLocationEnabled(true);


        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location loc) {
                if (mMarker != null) mMarker.remove();
                if (mCircle != null) mCircle.remove();

                mLastLocation = loc;
                //mLastLocation = new ParseGeoPoint(loc.getLatitude(),loc.getLongitude());
                //  LatLng location = new LatLng(loc.getLatitude(), loc.getLongitude());
                //  mMarker = mMap.addMarker(new MarkerOptions().position(location).title("My location"));
                //  mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12.0f));
                //  Log.d("Startmap","Mapping started2");

                /*DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("users");

                database.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        User user = dataSnapshot.getValue(User.class);

                        if (dataSnapshot.getChildrenCount() < 2) {

                            LatLng latLng = (new LatLng(mCurrentUser.getlat(),mCurrentUser.getlog()));
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(latLng);
                            markerOptions.title(User.getUser().getDisplayName() + " " + "(Me)");
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                            mMarker = mMap.addMarker(markerOptions);

                            //move map camera
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));
                        }



                        SharedPreferences prefs = getSharedPreferences(MY_GEOFIRE, MODE_PRIVATE);

                        Double lat = Double.valueOf(prefs.getString("lat", "0")); // "0" is the default value.
                        Double log = Double.valueOf(prefs.getString("log", "0")); // "0" is the default value.


                                //for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            for (int i = 0; i < dataSnapshot.getChildrenCount(); i++) {


                                LatLng latLngCurrent = (new LatLng(lat,log));

                                latlngs.add(new LatLng(user.getlat(), user.getlog()));

                                LatLng latLng = latlngs.get(i);
                                MarkerOptions markerOptions = new MarkerOptions();
                                //markerOptions.position(latLngCurrent);

                                if (dataSnapshot.getKey().equals(User.getUser().getUid())) {

                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                    markerOptions.title(User.getUser().getDisplayName() + " " + "(Me)");

                                } else {
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                    markerOptions.title(user.getName());
                                }
                                markerOptions.position(latLng);
                                //markerOptions.title(objects.get(i).getColFirstName() + " " + objects.get(i).getColLastName());
                                markerOptions.snippet(user.getdesc());
                                //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                                mMarker = mMap.addMarker(markerOptions);


                                //move map camera
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngCurrent,18));

                                }




                    }
                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }
                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                    }
                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });*/


                // get Current USes location

                LatLng latLng = (new LatLng(mCurrentUser.getlat(), mCurrentUser.getlog()));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(User.getUser().getDisplayName() + " " + "(Me)");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mMarker = mMap.addMarker(markerOptions);

                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
            }
        });
        //}

    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        log(String.format("start search - %s",query));
        //getActivity().getCurrentFocus().clearFocus();
        searchLocation(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void AddressSelected(GeocoderResult geocoderResult) {
        if (mMap != null) {
            mGeocoderResult = geocoderResult;
            LatLng location = geocoderResult.getLocation();

            mLastLocation = new Location("");

            mLastLocation.setLatitude(location.latitude);
            mLastLocation.setLongitude(location.longitude);

            //mParseGeoPoint = new ParseGeoPoint(location.latitude,location.longitude);

            if (mMarker != null) mMarker.remove();
            if (mCircle != null) mCircle.remove();
            mMarker = mMap.addMarker(new MarkerOptions().position(location).title(geocoderResult.getFormattedAddress()));
            //  mCircle = mMap.addCircle(new CircleOptions().center(location).radius(100).strokeColor(Color.RED).fillColor(getColor()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location,15));

            if (android.support.v4.app.ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && android.support.v4.app.ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_save_location:

                if (isInternetAvailable()) {

                    if (mLastLocation != null) {

                            showProgressBar(getString(R.string.maps_save2));

                           // showProgressBar(getString(R.string.maps_save3));

                            userDB.child("lat").setValue(mLastLocation.getLatitude());

                            userDB.child("log").setValue(mLastLocation.getLongitude()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){

                                        SharedPreferences.Editor editor =  getSharedPreferences(MY_GEOFIRE, MODE_PRIVATE).edit();
                                        editor.putString("lat", String.valueOf(mLastLocation.getLatitude()));
                                        editor.putString("log", String.valueOf(mLastLocation.getLongitude()));
                                        editor.apply();

                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("geofire");
                                        GeoFire geoFire = new GeoFire(ref);

                                        geoFire.setLocation(User.getUser().getUid(), new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), new GeoFire.CompletionListener() {
                                            @Override
                                            public void onComplete(String key, DatabaseError error) {
                                                if (error != null) {

                                                    dismissProgressBar();

                                                    Snackbar.make(mSettingsLayout,R.string.error_saving, Snackbar.LENGTH_INDEFINITE).setAction("OK",new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {

                                                        }
                                                    }).setActionTextColor(Color.WHITE).show();

                                                    System.err.println("There was an error saving the location to GeoFire: " + error);
                                                } else {

                                                    dismissProgressBar();

                                                    Intent mainIntent = new Intent(PassportActivity.this, AroundMeActivity.class);
                                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(mainIntent);

                                                    System.out.println("Location saved on server successfully!");
                                                }
                                            }
                                        });

                                    }


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    dismissProgressBar();

                                    Snackbar.make(mSettingsLayout,R.string.error_saving, Snackbar.LENGTH_INDEFINITE).setAction("OK",new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    }).setActionTextColor(Color.WHITE).show();

                                }
                            });


                    } else {

                        dismissProgressBar();

                        Snackbar.make(mSettingsLayout,R.string.settings_no_new_location, Snackbar.LENGTH_INDEFINITE).setAction("OK",new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).setActionTextColor(Color.WHITE).show();
                    }
                } else {

                    showInternetConnectionLostMessage();
                }
        }
    }

    public void showProgressBar(String message) {
        progressDialog = ProgressDialog.show(getActivity(),"",message,true);
    }

    public void dismissProgressBar() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    public void showInternetConnectionLostMessage() {
        Snackbar.make(mSettingsLayout,R.string.settings_no_inte, Snackbar.LENGTH_SHORT).show();

    }

    public boolean isInternetAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /*@Override
    protected void onPause() {
        super.onPause();
        Log.d("main111", "onPause");
    }*/

    // Verify every 45s if user still online tell the server.


    public void PermissionRequestLocation() {

        if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)) {

                new android.app.AlertDialog.Builder(getActivity())
                        .setTitle("Permission Needed")
                        .setMessage("Storage location is needed to access your gallery to update your profile picture")
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.yes,new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {

                                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_EXTERNAL_LOCATION);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            } else {

                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_EXTERNAL_LOCATION);
            }
        } else {

            checkGPS();


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_LOCATION: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

                    mapFragment.getMapAsync(this);

                    checkGPS();


                } else {

                    Context context = getActivity();
                    CharSequence text = "You denied the location permission, We Disabled the function. Grant the permission to use this function !";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context,text,duration);
                    toast.show();
                }
                return;
            }

        }
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
        return 5;
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {

    }

    @Override
    public void onSizeChanged(int height) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("main111", "onPause");
    }

}
