package com.aroundme.mostain.Utils.Location.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Angopapo, LDA on 25.09.16.
 */
public class GeocoderLocation {
    @SerializedName("lat")
    double lat;
    @SerializedName("lng")
    double lng;

    public double getLat(){
        return lat;
    }

    public double getLng(){
        return lng;
    }
}
