package com.aroundme.mostain.Utils.Location.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Angopapo, LDA on 25.09.16.
 */
public class GeocoderResult {
    @SerializedName("address_components")
    List<AddressComponent> addressComponents;
    @SerializedName("formatted_address")
    String formattedAddress;
    @SerializedName("geometry")
    GeocoderGeometry geometry;
    @SerializedName("place_id")
    String place_id;
    @SerializedName("types")
    List<String> types;

    public GeocoderResult(){
        addressComponents = new ArrayList<AddressComponent>();
        types =  new ArrayList<String>();
    }

    public String getFormattedAddress(){
        return formattedAddress;
    }

    public String getCountry(){
        for(AddressComponent currentAddressComponent : addressComponents){
            if(currentAddressComponent.types.contains("country")){
                return String.format(Locale.getDefault(), "%s, %s", currentAddressComponent.long_name, currentAddressComponent.short_name);
            }
        }
        return "<undefined>";
    }

    public LatLng getLocation(){
        if(geometry != null){
            return geometry.getLocation();
        }
        return null;
    }
}
