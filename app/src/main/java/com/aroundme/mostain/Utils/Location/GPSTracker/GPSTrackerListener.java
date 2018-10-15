package com.aroundme.mostain.Utils.Location.GPSTracker;

import android.location.Location;

/**
 * Created by Angopapo, LDA on 09.09.16.
 */
public interface GPSTrackerListener {
    void onGpsIsNotEnabled();
    void onLocationChanged(Location location);
    void onProviderEnabled(String provider);
    void onProviderDisabled(String provider);
}
