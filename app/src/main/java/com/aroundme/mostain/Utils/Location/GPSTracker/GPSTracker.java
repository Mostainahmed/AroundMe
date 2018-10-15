package com.aroundme.mostain.Utils.Location.GPSTracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by Angopapo, LDA on 09.09.16.
 */
public class GPSTracker {
    private static GPSTrackerListener mListener = null;
    private static GPSTrackerLocationListener mLocationListener = null;
    private static boolean isEnabled = false;
    private static Context mContext;
    // flag for GPS status
    private static boolean isGPSEnabled = false;

    // flag for network status
    private static boolean isNetworkEnabled = false;

    // flag for GPS status
    private static boolean canGetLocation = false;

    private static Location location; // location
    private static double latitude; // latitude
    private static double longitude; // longitude

    private static boolean initialized = false;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 10; // 10 seconds

    // Declaring a Location Manager
    protected static LocationManager mLocationManager;

    public static void setGPSTrackerListener(GPSTrackerListener listener){
        mListener = listener;
        getLocation();
    }


    public static void init(Context context){
        mContext = context;
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new GPSTrackerLocationListener();
    }

    public static boolean isInitialized(){
        return initialized;
    }

    public static boolean isGPSEnabled(){
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    private static class GPSTrackerLocationListener implements LocationListener{

        @Override
        public void onLocationChanged(Location location) {
            if(mListener != null)
                mListener.onLocationChanged(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            if(mListener != null) mListener.onProviderEnabled(provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            if(mListener != null) mListener.onProviderDisabled(provider);
        }
    }

    public static Location getLocation() {
        try {
            // getting GPS status
            isGPSEnabled = mLocationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!isGPSEnabled) {
                Log.d("myapp:GPSTracker", "not enabled");
                if(mListener != null) {
                    Log.d("myapp:GPSTracker", "listenerIsNotNull");
                    mListener.onGpsIsNotEnabled();
                }
            }

            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, mLocationListener);
            isEnabled = true;

        } catch (Exception e) {
            Log.d("myapp:GPSTracker:err", e.toString());
        }
        return location;
    }

    public static void stopUsingGPS(){
        if(mLocationManager != null){
            mLocationManager.removeUpdates(mLocationListener);
        }
        isEnabled = false;
    }

    public static void startUsingGPS(){
        if(mLocationManager != null){
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, mLocationListener);
        }
        isEnabled = true;
    }

    public static boolean isEnabled(){
        return isEnabled;
    }


    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    public static boolean canGetLocation() {
        return canGetLocation;
    }

    public static void showSettingsAlert(Context context1){
        final Context context = context1;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

}
