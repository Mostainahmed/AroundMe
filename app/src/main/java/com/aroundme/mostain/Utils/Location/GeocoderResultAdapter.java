package com.aroundme.mostain.Utils.Location;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.angopapo.aroundme2.R;
import com.angopapo.aroundme2.Utils.Location.models.GeocoderResult;

import java.util.List;

/**
 * Created by Angopapo, LDA on 28.09.16.
 */
public class GeocoderResultAdapter extends ArrayAdapter<GeocoderResult> {
    private Context mContext;
    private List<GeocoderResult> mObjects;

    public GeocoderResultAdapter(Context context, List<GeocoderResult> objects) {
        super(context, R.layout.item_geocoder_result, objects);
        mContext = context;
        mObjects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_geocoder_result, parent, false);
            vh = new ViewHolder();
            vh.addressText = (TextView) convertView.findViewById(R.id.text_address);
            vh.countryText = (TextView) convertView.findViewById(R.id.text_country);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.addressText.setText(mObjects.get(position).getFormattedAddress());
        vh.countryText.setText(mObjects.get(position).getCountry());
        return convertView;
    }

    public class ViewHolder{
        TextView countryText, addressText;
    }
}
