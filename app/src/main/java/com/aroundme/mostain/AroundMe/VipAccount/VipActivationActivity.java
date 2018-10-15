package com.aroundme.mostain.AroundMe.VipAccount;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;
import com.angopapo.aroundme2.App.BaseActivity;
import com.angopapo.aroundme2.AroundMe.Profile.EditProfileActivity;
import com.angopapo.aroundme2.Class.User;
import com.angopapo.aroundme2.R;
import com.angopapo.aroundme2.Utils.Helper.ActivityWithToolbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class VipActivationActivity extends BaseActivity implements ActivityWithToolbar, View.OnClickListener {

    // in-billing start here
    private IInAppBillingService mService;


    Toolbar mToolbar;
    private User mCurrentUser;

    private TextView mDateTravel, mDateVisitor, mDateAds, mDatePrivate ;

    private RelativeLayout mLocationLayout;

    private Dialog progressDialog;
    private Date startDate;
    private Date expiate;

     Button travel ;
     Button visitor ;
     Button remove ;
     Button privatemode ;

    // Database
    private DatabaseReference userDB;

    private ValueEventListener userListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            //Lấy thông tin của user về và cập nhật lên giao diện

            mCurrentUser = dataSnapshot.getValue(User.class);

            if (mCurrentUser != null){

                ckeckInfo();
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            //Có lỗi xảy ra, không lấy đc dữ liệu
            Log.e(EditProfileActivity.class.getName(), "loadPost:onCancelled", databaseError.toException());
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_activation);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        userDB = FirebaseDatabase.getInstance().getReference("users").child(User.getCurrentUserId());
        userDB.addValueEventListener(userListener);

        
        travel = (Button) findViewById(R.id.button_travel);
        visitor = (Button) findViewById(R.id.button_visitor);
        remove = (Button) findViewById(R.id.button_remove);
        privatemode = (Button) findViewById(R.id.button_private);

        mDateAds = (TextView) findViewById(R.id.date1);
        mDateTravel = (TextView) findViewById(R.id.date2);
        mDateVisitor = (TextView) findViewById(R.id.date4);
        mDatePrivate = (TextView) findViewById(R.id.date3);



        mLocationLayout = (RelativeLayout) findViewById(R.id.layout_location);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.vip_features_titlte);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // set green to all activated services

        assert travel != null;
        travel.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if  (mCurrentUser.getCredits() < 100){

                    // ask the user to confirm a deduction and to activate service

                    AlertDialog.Builder notifyLocationServices = new AlertDialog.Builder(VipActivationActivity.this);
                    notifyLocationServices.setTitle(getString(R.string.sorry_vip));
                    notifyLocationServices.setMessage(getString(R.string.cost_vip) + (mCurrentUser.getCredits() + " " + getString(R.string.vip_act_expl)));
                    notifyLocationServices.setPositiveButton(getString(R.string.recg_vip), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // buy the service and deduct 100 Credits here

                            Intent intent = new Intent(getApplicationContext(), StoreActivity.class);
                            startActivity(intent);
                        }
                    });
                    notifyLocationServices.setNegativeButton(getString(R.string.conf_4), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //finish();
                        }
                    });
                    notifyLocationServices.show();


                }

                else {

                    AlertDialog.Builder notifyLocationServices = new AlertDialog.Builder(VipActivationActivity.this);
                    notifyLocationServices.setTitle(getString(R.string.conf_1));
                    notifyLocationServices.setMessage(getString(R.string.conf_2));
                    notifyLocationServices.setPositiveButton(getString(R.string.conf_3), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // deduct 100 credits in the user account and activated a service for 30 days
                            showProgressBar(getString(R.string.active_vip));

                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.DATE, 30);
                            Date expDate = calendar.getTime();

                            userDB.child("passportEnd").setValue(expDate.getTime()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        userDB.child("credits").runTransaction(new Transaction.Handler() {
                                            @Override
                                            public Transaction.Result doTransaction(MutableData mutableData) {

                                                int credits = mutableData.getValue(Integer.class);

                                                if (mutableData.getValue() == null){

                                                    mutableData.setValue(0);

                                                } else {

                                                    mutableData.setValue( credits -100);
                                                }

                                                return Transaction.success(mutableData);
                                            }

                                            @Override
                                            public void onComplete(DatabaseError databaseError, boolean success, DataSnapshot dataSnapshot) {
                                                // Analyse databaseError for any error during increment

                                                if (success){

                                                    userDB.child("isTravel").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isComplete()){

                                                                Snackbar.make(mLocationLayout, R.string.cong_vip, Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {

                                                                        /*Intent loginIntent = new Intent(VipActivationActivity.this, MapsActivity.class);
                                                                        VipActivationActivity.this.startActivity(loginIntent);
                                                                        VipActivationActivity.this.finish();*/

                                                                    }
                                                                }).setActionTextColor(Color.WHITE).show();

                                                                dismissProgressBar();
                                                                travel.setText(getString(R.string.vip_activated));
                                                                travel.setBackgroundResource(R.drawable.buy_button_bg_green);
                                                                travel.setEnabled(false);

                                                            }

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                        }
                                                    });
                                                }
                                            }
                                        });


                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    dismissProgressBar();

                                    Snackbar.make(mLocationLayout, R.string.loc_cant, Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    }).setActionTextColor(Color.WHITE).show();
                                }
                            });

                        }
                    });
                    notifyLocationServices.setNegativeButton(getString(R.string.conf_4), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //finish();
                        }
                    });
                    notifyLocationServices.show();

                }
            }
        });

        assert privatemode != null;
        privatemode.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if  (mCurrentUser.getCredits() < 100){

                    // ask the user to confirm a deduction and to activate service

                    AlertDialog.Builder notifyLocationServices = new AlertDialog.Builder(VipActivationActivity.this);
                    notifyLocationServices.setTitle(getString(R.string.sorry_vip));
                    notifyLocationServices.setMessage(getString(R.string.cost_vip) + (mCurrentUser.getCredits() + " " + getString(R.string.vip_act_expl)));
                    notifyLocationServices.setPositiveButton(getString(R.string.recg_vip), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // buy the service and deduct 100 Credits here

                            Intent intent = new Intent(getApplicationContext(), StoreActivity.class);
                            startActivity(intent);
                        }
                    });
                    notifyLocationServices.setNegativeButton(getString(R.string.conf_4), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //finish();
                        }
                    });
                    notifyLocationServices.show();


                }

                else {

                    AlertDialog.Builder notifyLocationServices = new AlertDialog.Builder(VipActivationActivity.this);
                    notifyLocationServices.setTitle(getString(R.string.conf_1));
                    notifyLocationServices.setMessage(getString(R.string.conf_2));
                    notifyLocationServices.setPositiveButton(getString(R.string.conf_3), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // deduct 100 credits in the user account and activated a service for 30 days
                            showProgressBar(getString(R.string.active_vip));

                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.DATE, 30);
                            Date expDate = calendar.getTime();

                            userDB.child("privateEnd").setValue(expDate.getTime()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        userDB.child("credits").runTransaction(new Transaction.Handler() {
                                            @Override
                                            public Transaction.Result doTransaction(MutableData mutableData) {
                                                int credits = mutableData.getValue(Integer.class);

                                                if (mutableData.getValue() == null){

                                                    mutableData.setValue(0);

                                                } else {

                                                    mutableData.setValue( credits -100);
                                                }

                                                return Transaction.success(mutableData);

                                            }

                                            @Override
                                            public void onComplete(DatabaseError databaseError, boolean success, DataSnapshot dataSnapshot) {
                                                // Analyse databaseError for any error during increment

                                                if (success){

                                                    userDB.child("IsPrivate").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isComplete()){




                                                                Snackbar.make(mLocationLayout, R.string.cong_vip, Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {

                                                                        /*Intent loginIntent = new Intent(VipActivationActivity.this, AroundMeActivity.class);
                                                                        VipActivationActivity.this.startActivity(loginIntent);
                                                                        VipActivationActivity.this.finish();*/

                                                                    }
                                                                }).setActionTextColor(Color.WHITE).show();

                                                                dismissProgressBar();
                                                                privatemode.setText(getString(R.string.vip_activated));
                                                                privatemode.setBackgroundResource(R.drawable.buy_button_bg_green);
                                                                privatemode.setEnabled(false);


                                                            }

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                        }
                                                    });
                                                }
                                            }
                                        });


                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    dismissProgressBar();

                                    Snackbar.make(mLocationLayout, R.string.loc_cant, Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    }).setActionTextColor(Color.WHITE).show();
                                }
                            });

                        }

                    });
                    notifyLocationServices.setNegativeButton(getString(R.string.conf_4), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //finish();
                        }
                    });
                    notifyLocationServices.show();

                }
            }
        });

        assert visitor != null;
        visitor.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                // change the color to signify a click
                if  (mCurrentUser.getCredits() < 100){

                    // ask the user to confirm a deduction and to activate service

                    AlertDialog.Builder notifyLocationServices = new AlertDialog.Builder(VipActivationActivity.this);
                    notifyLocationServices.setTitle(getString(R.string.sorry_vip));
                    notifyLocationServices.setMessage(getString(R.string.cost_vip) + (mCurrentUser.getCredits() + " " + getString(R.string.vip_act_expl)));
                    notifyLocationServices.setPositiveButton(getString(R.string.recg_vip), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // buy the service and deduct 100 Credits here
                            Intent intent = new Intent(getApplicationContext(), StoreActivity.class);
                            startActivity(intent);

                        }
                    });
                    notifyLocationServices.setNegativeButton(getString(R.string.conf_4), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //finish();
                        }
                    });
                    notifyLocationServices.show();


                }

                else {

                    AlertDialog.Builder notifyLocationServices = new AlertDialog.Builder(VipActivationActivity.this);
                    notifyLocationServices.setTitle(getString(R.string.conf_1));
                    notifyLocationServices.setMessage(getString(R.string.conf_2));
                    notifyLocationServices.setPositiveButton(getString(R.string.conf_3), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // deduct 100 credits in the user account and activated a service for 30 days
                            showProgressBar(getString(R.string.active_vip));

                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.DATE, 30);
                            Date expDate = calendar.getTime();

                            userDB.child("visitorEnd").setValue(expDate.getTime()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        userDB.child("credits").runTransaction(new Transaction.Handler() {
                                            @Override
                                            public Transaction.Result doTransaction(MutableData mutableData) {

                                                int credits = mutableData.getValue(Integer.class);

                                                if (mutableData.getValue() == null){

                                                    mutableData.setValue(0);

                                                } else {

                                                    mutableData.setValue( credits -100);
                                                }

                                                return Transaction.success(mutableData);


                                            }

                                            @Override
                                            public void onComplete(DatabaseError databaseError, boolean success, DataSnapshot dataSnapshot) {
                                                // Analyse databaseError for any error during increment

                                                if (success){

                                                    userDB.child("isVisitor").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isComplete()){

                                                                Snackbar.make(mLocationLayout, R.string.cong_vip, Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {

                                                                        /*Intent loginIntent = new Intent(VipActivationActivity.this, MyVisitorsActivity.class);
                                                                        VipActivationActivity.this.startActivity(loginIntent);
                                                                        VipActivationActivity.this.finish();*/

                                                                    }
                                                                }).setActionTextColor(Color.WHITE).show();

                                                                dismissProgressBar();
                                                                visitor.setText(getString(R.string.vip_activated));
                                                                visitor.setBackgroundResource(R.drawable.buy_button_bg_green);
                                                                visitor.setEnabled(false);
                                                            }

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                        }
                                                    });
                                                }
                                            }
                                        });


                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    dismissProgressBar();

                                    Snackbar.make(mLocationLayout, R.string.loc_cant, Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    }).setActionTextColor(Color.WHITE).show();
                                }
                            });

                        }
                    });
                    notifyLocationServices.setNegativeButton(getString(R.string.conf_4), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //finish();
                        }
                    });
                    notifyLocationServices.show();

                }
            }
        });
        

        assert remove != null;
        remove.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if  (mCurrentUser.getCredits() < 100){

                    // ask the user to confirm a deduction and to activate service

                    AlertDialog.Builder notifyLocationServices = new AlertDialog.Builder(VipActivationActivity.this);
                    notifyLocationServices.setTitle(getString(R.string.sorry_vip));
                    notifyLocationServices.setMessage(getString(R.string.cost_vip) + (mCurrentUser.getCredits() + " " + getString(R.string.vip_act_expl)));
                    notifyLocationServices.setPositiveButton(getString(R.string.recg_vip), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // buy the service and deduct 100 Credits here

                            Intent intent = new Intent(getApplicationContext(), StoreActivity.class);
                            startActivity(intent);
                        }
                    });
                    notifyLocationServices.setNegativeButton(getString(R.string.conf_4), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //finish();
                        }
                    });
                    notifyLocationServices.show();


                }

                else {

                    AlertDialog.Builder notifyLocationServices = new AlertDialog.Builder(VipActivationActivity.this);
                    notifyLocationServices.setTitle(getString(R.string.conf_1));
                    notifyLocationServices.setMessage(getString(R.string.conf_2));
                    notifyLocationServices.setPositiveButton(getString(R.string.conf_3), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // deduct 100 credits in the user account and activated a service for 30 days
                            showProgressBar(getString(R.string.active_vip));

                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.DATE, 30);
                            Date expDate = calendar.getTime();

                            userDB.child("adsEnd").setValue(expDate.getTime()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        userDB.child("credits").runTransaction(new Transaction.Handler() {
                                            @Override
                                            public Transaction.Result doTransaction(MutableData mutableData) {
                                                int credits = mutableData.getValue(Integer.class);

                                                if (mutableData.getValue() == null){

                                                    mutableData.setValue(0);

                                                } else {

                                                    mutableData.setValue( credits -100);
                                                }

                                                return Transaction.success(mutableData);
                                            }

                                            @Override
                                            public void onComplete(DatabaseError databaseError, boolean success, DataSnapshot dataSnapshot) {
                                                // Analyse databaseError for any error during increment

                                                if (success){

                                                    userDB.child("FreeAds").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isComplete()){



                                                                Snackbar.make(mLocationLayout, R.string.cong_vip, Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {

                                                                        /*Intent loginIntent = new Intent(VipActivationActivity.this, AroundMeActivity.class);
                                                                        VipActivationActivity.this.startActivity(loginIntent);
                                                                        VipActivationActivity.this.finish();*/

                                                                    }
                                                                }).setActionTextColor(Color.WHITE).show();

                                                                dismissProgressBar();
                                                                remove.setText(getString(R.string.vip_activated));
                                                                remove.setBackgroundResource(R.drawable.buy_button_bg_green);
                                                                remove.setEnabled(false);

                                                            }

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                        }
                                                    });
                                                }
                                            }
                                        });


                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    dismissProgressBar();

                                    Snackbar.make(mLocationLayout, R.string.loc_cant, Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    }).setActionTextColor(Color.WHITE).show();
                                }
                            });

                        }
                    });
                    notifyLocationServices.setNegativeButton(getString(R.string.conf_4), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //finish();
                        }
                    });
                    notifyLocationServices.show();

                }
            }
        });

    }

    public void ckeckInfo(){

        if(mCurrentUser.getIsVip().equals("vip")) {


            remove.setText(R.string.VIP);
            travel.setText(R.string.VIP);
            visitor.setText(R.string.VIP);
            privatemode.setText(R.string.VIP);

            remove.setEnabled(false);
            travel.setEnabled(false);
            visitor.setEnabled(false);
            privatemode.setEnabled(false);

            remove.setBackgroundResource(R.drawable.buy_button_bg_green);
            travel.setBackgroundResource(R.drawable.buy_button_bg_green);
            visitor.setBackgroundResource(R.drawable.buy_button_bg_green);
            privatemode.setBackgroundResource(R.drawable.buy_button_bg_green);

        } else {

        if (mCurrentUser.getFreeAds()){

            assert remove != null;
            remove.setText(R.string.vip_activated);
            //promote.setTextColor(Color.GREEN);
            remove.setBackgroundResource(R.drawable.buy_button_bg_green);
            remove.setEnabled(false);

            Date date = new Date(mCurrentUser.getadsEnd()); // *1000 is to convert seconds to milliseconds
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy "); // the format of your date

            String EndDate = sdf.format(date);

            //assert mDateAds != null;
             mDateAds.setText(getString(R.string.expirein) + " " + EndDate);

            }

        if (mCurrentUser.getIsTravel()){

            //assert travel != null;
            travel.setText(R.string.vip_activated);
            // Travel End Date

            Date date = new Date(mCurrentUser.getpassportEnd()); // *1000 is to convert seconds to milliseconds
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy "); // the format of your date

            String EndDate = sdf.format(date);

            //promote.setTextColor(Color.GREEN);
            travel.setBackgroundResource(R.drawable.buy_button_bg_green);
            travel.setEnabled(false);

            //assert mDateTravel != null;
             mDateTravel.setText(getString(R.string.expirein) + " " + EndDate);

            }

        if (mCurrentUser.getIsVisitor()){

            assert visitor != null;
            visitor.setText(R.string.vip_activated);
            //promote.setTextColor(Color.GREEN);
            visitor.setBackgroundResource(R.drawable.buy_button_bg_green);
            visitor.setEnabled(false);

            Date date = new Date(mCurrentUser.getvisitorEnd()); // *1000 is to convert seconds to milliseconds
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy "); // the format of your date

            String EndDate = sdf.format(date);

            //assert mDateVisitor != null;
             mDateVisitor.setText(getString(R.string.expirein) + " " + EndDate);

            }

        if (mCurrentUser.isPrivate()){

            assert visitor != null;
            privatemode.setText(R.string.vip_activated);
            //promote.setTextColor(Color.GREEN);
            privatemode.setBackgroundResource(R.drawable.buy_button_bg_green);
            privatemode.setEnabled(false);

            Date date = new Date(mCurrentUser.getprivateEnd()); // *1000 is to convert seconds to milliseconds
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy "); // the format of your date

            String EndDate = sdf.format(date);

            //assert mDateVisitor != null;
            mDatePrivate.setText(getString(R.string.expirein) + " " + EndDate);

            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    @Override
    public Toolbar getToolbar() {
        return null;
    }

    @Override
    public Activity getActivity() {
        return null;
    }

    @Override
    public int getDriwerId() {
        return 7;
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {

    }

    @Override
    public void onSizeChanged(int height) {

    }

    @Override
    public void onClick(View v) {

    }

    public void showProgressBar(String message){
        progressDialog = ProgressDialog.show(this, "", message, true);
    }
    public void dismissProgressBar() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("main111", "onPause");
    }
}
