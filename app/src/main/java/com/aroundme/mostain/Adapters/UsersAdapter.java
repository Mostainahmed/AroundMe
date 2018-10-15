package com.aroundme.mostain.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.angopapo.aroundme2.Class.User;
import com.angopapo.aroundme2.R;
import com.angopapo.aroundme2.Utils.service.ServiceUtils;
import com.angopapo.aroundme2.ViewHolder.UsersViewHolder;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.angopapo.aroundme2.App.Application.MY_GEOFIRE;

public class UsersAdapter extends RecyclerView.Adapter<UsersViewHolder> {


    private List<User> user;
    protected Context context;
    public UsersAdapter(Context context, List<User> users) {
        this.user = users;
        this.context = context;
    }
    @Override
    public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        UsersViewHolder viewHolder = null;
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_item, parent, false);
        viewHolder = new UsersViewHolder(layoutView, user);

        return viewHolder;
    }
    @Override
    public void onBindViewHolder(UsersViewHolder usersViewHolder, int position) {

        //holder.categoryTitle.setText(user.get(position).getTask());

        SharedPreferences prefs = context.getSharedPreferences(MY_GEOFIRE, MODE_PRIVATE);

            Double lat = Double.valueOf(prefs.getString("lat", "0")); // "0" is the default value.
            Double log = Double.valueOf(prefs.getString("log", "0")); // "0" is the default value.

        // Check if user is private or not.

        if (user.get(position).isPrivateActived()){

            usersViewHolder.setAge("PP");
            usersViewHolder.setDistance("PP");
            usersViewHolder.setUser("Private Profile", "private");
            usersViewHolder.setPhotoPrivate(user.get(position).getPhotoThumb());

            if (user.get(position).getisOnline()){

                usersViewHolder.setOnline(String.valueOf(R.drawable.ic_online_15_0_alizarin));

            } else {

                if (System.currentTimeMillis() - user.get(position).getTimestamp() > ServiceUtils.TIME_TO_OFFLINE) {

                    usersViewHolder.setOffline(String.valueOf(R.drawable.ic_offline_15_0_alizarin));

                } else if (System.currentTimeMillis() - user.get(position).getTimestamp() > ServiceUtils.TIME_TO_SOON) {

                    usersViewHolder.setSoon(String.valueOf(R.drawable.last_min));
                }
            }

        } else {


            if (user.get(position).getPhotoThumb() != null){
                usersViewHolder.setPhoto(user.get(position).getPhotoThumb());

            } else {


                usersViewHolder.setPhoto(user.get(position).getPhotoUrl());
            }


            if (user.get(position).getFirstname() != null){

                usersViewHolder.setUser(user.get(position).getFirstname(), user.get(position).getUid());
            } else {

                usersViewHolder.setUser(user.get(position).getName(), user.get(position).getUid());
            }


            if (user.get(position).getbirthdate() != 0){

                LocalDate birthdate = new LocalDate(user.get(position).getbirthdate());          //Birth date
                LocalDate now = new LocalDate();                                         //Today's date
                Period period = new Period(birthdate,now, PeriodType.yearMonthDay());

                final Integer ageInt = period.getYears();
                final String ageS = ageInt.toString();

                usersViewHolder.setAge(ageS);
            } else {

                usersViewHolder.setAge("18+");
            }


            if (user.get(position).getisOnline()){

                usersViewHolder.setOnline(String.valueOf(R.drawable.ic_online_15_0_alizarin));

            } else {

                if (System.currentTimeMillis() - user.get(position).getTimestamp() > ServiceUtils.TIME_TO_OFFLINE) {

                    usersViewHolder.setOffline(String.valueOf(R.drawable.ic_offline_15_0_alizarin));

                } else if (System.currentTimeMillis() - user.get(position).getTimestamp() > ServiceUtils.TIME_TO_SOON) {

                    usersViewHolder.setSoon(String.valueOf(R.drawable.last_min));
                }
            }

            if (log != null && lat != null){

                Location loc1 = new Location("");

                loc1.setLatitude(lat);
                loc1.setLongitude(log);

                if (user.get(position).getlat() != null && user.get(position).getlog() != null){

                    Location loc2 = new Location("");
                    loc2.setLatitude(user.get(position).getlat());
                    loc2.setLongitude(user.get(position).getlog());

                    float distanceInMeters = loc1.distanceTo(loc2);


                    usersViewHolder.setDistance(String.format(Locale.ENGLISH, "%.2f km", distanceInMeters));
                }


            } else {


                usersViewHolder.setDistance(String.format(Locale.ENGLISH, "%.2f km", 0.00));
            }
        }



    }
    @Override
    public int getItemCount() {
        return this.user.size();
    }

    public void setUsers(List<User> list) {
        user = list;
        notifyDataSetChanged();
    }

    public User getUser(int position) {
        if (position > user.size() - 1) {
            return new User();
        } else {
            return user.get(position);
        }
    }
}