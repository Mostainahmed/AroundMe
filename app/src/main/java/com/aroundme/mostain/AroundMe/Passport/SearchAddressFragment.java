package com.aroundme.mostain.AroundMe.Passport;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


import com.aroundme.mostain.R;
import com.aroundme.mostain.Utils.Location.GeocoderResultAdapter;
import com.aroundme.mostain.Utils.Location.GeocoderService;
import com.aroundme.mostain.Utils.Location.models.GeocoderResponse;
import com.aroundme.mostain.Utils.Location.models.GeocoderResult;

import java.util.List;
import java.util.Locale;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;


/**
 * Created by Angopapo, LDA on 25.09.16.
 */
public class SearchAddressFragment extends Fragment implements ListView.OnItemClickListener {
    public static final String ARG_ADDRESS = "address";
    private static final String TAG = "myapp:SearchAddressFragment";
    private ListView mAddressList;
    private String mAddressString;
    private GeocoderResultAdapter mGeocoderResultAdapter;
    private Geocoder mGeocoder;
    private Bundle mArguments = null;
    private List<Address> mAddresses;
    private List<String> mAddressesStringList;
    private GeocoderService mGeocoderService;
    private Callback<GeocoderResponse> mGeocoderResponseCallback;
    private List<GeocoderResult> mGeocoderResults;
    private SearchAddressListener mListener;

    public void log(String debugMessage) {
        //Log.d(TAG, debugMessage);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArguments = getArguments();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(getResources().getString(R.string.google_api)).addConverterFactory(GsonConverterFactory.create()).build();
        log(retrofit.baseUrl().url().toString());
        log(getResources().getString(R.string.google_api));
        mGeocoderService = retrofit.create(GeocoderService.class);
        mGeocoder = new Geocoder(getActivity(),Locale.getDefault());

        if (mArguments != null && mArguments.getString(ARG_ADDRESS) != null) {
            mAddressString = mArguments.getString(ARG_ADDRESS);
        }
        mGeocoderResponseCallback = new Callback<GeocoderResponse>() {
            @Override
            public void onResponse(Response<GeocoderResponse> response) {
                if (response.isSuccess()) {
                    GeocoderResponse geocoderResponse = response.body();
                    log("Status = " + geocoderResponse.getStatus());
                    if (geocoderResponse.isRequestDenied()) {
                        log(geocoderResponse.getErrorMessage());
                    }
                    if (geocoderResponse.isOk()) {
                        mGeocoderResults = geocoderResponse.getResults();
                        if (mGeocoderResultAdapter == null) {
                            mGeocoderResultAdapter = new GeocoderResultAdapter(getActivity(),geocoderResponse.getResults());
                            mAddressList.setAdapter(mGeocoderResultAdapter);
                        } else {
                            log("isOK & adapter not null");
                            log("result count = " + mGeocoderResults.size());
                            mGeocoderResultAdapter.clear();
                            mGeocoderResultAdapter.addAll(geocoderResponse.getResults());
                            mAddressList.setAdapter(mGeocoderResultAdapter);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                log("failure");
            }
        };
    }

    public void setSearchAddressListener(SearchAddressListener listener) {
        mListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_address,container,false);
        log("onCreateView");
        mAddressList = (ListView) v.findViewById(R.id.list_address);
        mAddressList.setOnItemClickListener(this);
        if (mGeocoderResultAdapter == null) getAddresses();
        return v;
    }

    public void getAddresses() {
        Call<GeocoderResponse> geocoderResponseCall = mGeocoderService.getLocationByName(mAddressString);
        geocoderResponseCall.enqueue(mGeocoderResponseCallback);
    }

    public void getAddresses(String addressString) {
        Call<GeocoderResponse> geocoderResponseCall = mGeocoderService.getLocationByName(addressString);
        geocoderResponseCall.enqueue(mGeocoderResponseCallback);
    }

    @Override
    public void onItemClick(AdapterView<?> parent,View view,int position,long id) {
        if (mListener != null) {
            mListener.AddressSelected(mGeocoderResults.get(position));
            getFragmentManager().beginTransaction().remove(this).commit();
        }
    }

    public interface SearchAddressListener {
        void AddressSelected(GeocoderResult geocoderResult);
    }
}
