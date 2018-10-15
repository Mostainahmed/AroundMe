package com.aroundme.mostain.AroundMe.Location;


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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.angopapo.aroundme2.App.BaseActivity;
import com.angopapo.aroundme2.AroundMe.NearMe.AroundMeActivity;
import com.angopapo.aroundme2.Class.User;
import com.angopapo.aroundme2.R;
import com.angopapo.aroundme2.Utils.Helper.ActivityWithToolbar;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.angopapo.aroundme2.App.Application.MY_GEOFIRE;

public class LocationActivity extends BaseActivity implements ActivityWithToolbar, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    RelativeLayout mSettingsLayout;
    Toolbar mToolbar;
    int YOUR_FRAGMENT_POSITION = 3;
    private Dialog progressDialog;
    private ArrayList<LatLng> latlngs = new ArrayList<>();

    // Database
    private DatabaseReference userDB;
    private User mCurrentUser;

    private String latLog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_maps);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Location Update");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        userDB = FirebaseDatabase.getInstance().getReference().child(User.Class).child(User.getCurrentUserId());


        mSettingsLayout = (RelativeLayout) findViewById(R.id.layout_settings);

        Button mSaveLocationButton = (Button) findViewById(R.id.button_save_location);

        mSaveLocationButton.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= 23) {

            mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFrag.getMapAsync(this);


            // Marshmallow+
            checkLocationPermission();


        } else {
            // Pre-Marshmallow

            mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFrag.getMapAsync(this);

            checkGPS();


        }


    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();

            // update location here

        }

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        assert firebaseUser != null;
        if (firebaseUser.getDisplayName() != null){
        markerOptions.title(firebaseUser.getDisplayName()  + " " + "(Me)");
        }
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);


        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));

    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setCancelable(false)
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface,int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(LocationActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST_LOCATION);
            }
        } else {

            checkGPS();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                        checkGPS();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this,"Permission denied, you will not use this app, close and open again!",Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void checkGPS() {


        LocationManager lm = (LocationManager) LocationActivity.this.getSystemService(Context.LOCATION_SERVICE);
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
            //&& !network_enabled
                ) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(LocationActivity.this);
            dialog.setMessage("GPS not enabled");
            dialog.setCancelable(false);
            dialog.setPositiveButton("Enable Now",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface,int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    LocationActivity.this.startActivity(myIntent);
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

            onRestart();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_save_location:

                if (isInternetAvailable()) {

                    if (mLastLocation != null) {

                        showProgressBar(getString(R.string.maps_save3));

                        userDB.child("log").setValue(mLastLocation.getLongitude());

                        userDB.child("lat").setValue(mLastLocation.getLatitude()).addOnCompleteListener(new OnCompleteListener<Void>() {
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

                                                //System.err.println("There was an error saving the location to GeoFire: " + error);
                                            } else {

                                                dismissProgressBar();

                                                Intent mainIntent = new Intent(LocationActivity.this, AroundMeActivity.class);
                                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(mainIntent);

                                                //System.out.println("Location saved on server successfully!");
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
        progressDialog = ProgressDialog.show(this,"",message,true);
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
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        return null;
    }

    @Override
    public Toolbar getToolbar() {
        return null;
    }

    @Override
    public Activity getActivity() {
        return null;
    }

    @Override
    public int getDriwerId() {
        return 0;
    }

    @Override
    public void didReceivedNotification(int id,Object... args) {

    }

    @Override
    public void onSizeChanged(int height) {

    }
}
