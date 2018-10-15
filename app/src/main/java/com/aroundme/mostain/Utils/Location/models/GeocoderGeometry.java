package com.aroundme.mostain.Utils.Location.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Angopapo, LDA on 25.09.16.
 */
public class GeocoderGeometry {
    @SerializedName("location")
    GeocoderLocation location;
    @SerializedName("location_type")
    String location_type;
    @SerializedName("viewport")
    GeocoderViewPort viewport;

    public LatLng getLocation(){
        return new LatLng(location.getLat(), location.getLng());
    }
}
