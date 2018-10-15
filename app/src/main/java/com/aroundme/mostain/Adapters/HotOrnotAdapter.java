package com.aroundme.mostain.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.angopapo.aroundme2.Class.User;
import com.angopapo.aroundme2.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.util.List;


public class HotOrnotAdapter extends ArrayAdapter<User> {

    Context context;

    public HotOrnotAdapter(Context context, int resourceId, List<User> items){
        super(context, resourceId, items);
    }


    public View getView(int position, View convertView, ViewGroup parent){
        User card_item = getItem(position);

        if (convertView == null){
            //convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);

            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item, parent, false);
        }

        TextView mUsernameText = (TextView) convertView.findViewById(R.id.text_username);
        TextView mGenderText = (TextView) convertView.findViewById(R.id.genderAge);
        TextView mNoPhoto = (TextView) convertView.findViewById(R.id.textView4);
        ImageView mProfilePhotoImage = (ImageView) convertView.findViewById(R.id.profileImageView);
        ProgressBar mProgressBar = (ProgressBar) convertView.findViewById(R.id.progressBar3);

        mProgressBar.setVisibility(View.VISIBLE);
        mNoPhoto.setVisibility(View.GONE);

        assert card_item != null;

        if (card_item.firstname != null){

            if (card_item.birthdate != 0){

                LocalDate birthdate = new LocalDate(card_item.birthdate);          //Birth date
                LocalDate now = new LocalDate();                                         //Today's date
                Period period = new Period(birthdate,now, PeriodType.yearMonthDay());

                //int ages = period.getYears();
                final Integer ageInt = period.getYears();
                final String ageS = ageInt.toString();

                mUsernameText.setText(card_item.firstname + ", " + ageS);

            } else {

                mUsernameText.setText(card_item.firstname);


            }


        } else if (card_item.name != null){

                if (card_item.birthdate != 0){

                LocalDate birthdate = new LocalDate(card_item.birthdate);          //Birth date
                LocalDate now = new LocalDate();                                         //Today's date
                Period period = new Period(birthdate,now, PeriodType.yearMonthDay());

                //int ages = period.getYears();
                final Integer ageInt = period.getYears();
                final String ageS = ageInt.toString();

                mUsernameText.setText(card_item.name + ", " + ageS);

            } else {

                    mUsernameText.setText(card_item.name);



            }

        } else if (card_item.birthdate != 0){

                LocalDate birthdate = new LocalDate(card_item.birthdate);          //Birth date
                LocalDate now = new LocalDate();                                         //Today's date
                Period period = new Period(birthdate,now, PeriodType.yearMonthDay());

                //int ages = period.getYears();
                final Integer ageInt = period.getYears();
                final String ageS = ageInt.toString();

                mUsernameText.setText("no_name" + ", " + ageS);

        } else {

            mUsernameText.setText("no_name");
        }




        if (card_item.isMale){

            mGenderText.setText("Male");
        } else {

            mGenderText.setText("Female");
        }


        if (card_item.getPhotoUrl() == null) {

            mProfilePhotoImage.setImageResource(R.color.gray1);

            mProgressBar.setVisibility(View.GONE);
            mNoPhoto.setVisibility(View.VISIBLE);
            mNoPhoto.setText("No photo available!");

        } else {

            Glide.with(getContext())
                    .load(card_item.photoUrl)
                    .error(R.color.gray1)
                    //.fitCenter()
                    .crossFade()
                    //.centerCrop()
                    .placeholder(R.color.gray1)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {

                            mProfilePhotoImage.setBackgroundResource(R.color.gray1);

                            mProgressBar.setVisibility(View.GONE);
                            mNoPhoto.setVisibility(View.VISIBLE);
                            mNoPhoto.setText("Error geting available photo!");

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource,String model,Target<GlideDrawable> target,boolean isFromMemoryCache,boolean isFirstResource) {
                            mProgressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(mProfilePhotoImage);

        }

        /*mProfilePhotoImage.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                Toast.makeText(getContext(), "Left", Toast.LENGTH_SHORT).show();

                    Intent imageViewerIntent = new Intent(getActivity(), ImageViewerActivity.class);
                    imageViewerIntent.putExtra(ImageViewerActivity.EXTRA_IMAGE_URL, mArguments.getString(ARG_PROFILE_PHOTO_URL));
                    getActivity().startActivity(imageViewerIntent);

            }
        });*/

        return convertView;

    }
}
