package com.aroundme.mostain.Utils.Location.models;

import com.aroundme.mostain.Utils.Location.models.GeocoderLocation;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Angopapo, LDA on 25.09.16.
 */
public class GeocoderViewPort {
    @SerializedName("northeast")
    GeocoderLocation northeast;
    @SerializedName("southwest")
    GeocoderLocation southwest;
}
