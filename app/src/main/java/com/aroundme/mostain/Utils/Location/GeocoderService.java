package com.aroundme.mostain.Utils.Location;


import com.aroundme.mostain.Utils.Location.models.GeocoderResponse;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;


/**
 * Created by Angopapo, LDA on 25.09.16.
 */
public interface GeocoderService {
    String STATUS_OK = "OK";
    String REQUEST_DENIED = "REQUEST_DENIED";

    @GET("/maps/api/geocode/json")
    Call<GeocoderResponse> getLocationByName(@Query("address") String address);

    @GET("/maps/api/geocode/json")
    Call<GeocoderResponse> getLocationByName();
}
