package com.aroundme.mostain.Utils.Location.models;

import android.text.TextUtils;

import com.aroundme.mostain.Utils.Location.GeocoderService;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Angopapo, LDA on 25.09.16.
 */
public class GeocoderResponse {
    @SerializedName("results")
    @Expose
    List<GeocoderResult> results;
    @SerializedName("status")
    @Expose
    String status;
    @SerializedName("error_message")
    @Expose
    String errorMessage;

    public List<GeocoderResult> getResults(){
        return results;
    }

    public String getStatus(){
        return status;
    }

    public String getErrorMessage(){
        return errorMessage;
    }

    public boolean isRequestDenied(){
        return TextUtils.equals(status, GeocoderService.REQUEST_DENIED);
    }

    public boolean isOk(){
        return TextUtils.equals(status, GeocoderService.STATUS_OK);
    }

    public GeocoderResponse(){
        results = new ArrayList<GeocoderResult>();
    }
}
