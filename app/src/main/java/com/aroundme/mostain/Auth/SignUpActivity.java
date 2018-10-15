package com.aroundme.mostain.Auth;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aroundme.mostain.AroundMe.Location.LocationActivity;
import com.aroundme.mostain.Class.User;
import com.aroundme.mostain.R;
import com.aroundme.mostain.Utils.service.Constants;
import com.aroundme.mostain.Utils.service.SharedPrefUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;


public class SignUpActivity extends Activity {


    //private static final String TAG = "SignUpActivity";
    public static final String TAG = "myapp";

    private static final int REQUEST_EXTERNAL_STORAGE = 123;
    private static final int REQUEST_CAMERA = 124 ;




    protected String gender = null;
    @BindView(R.id.input_name_first)
    EditText _nameFisrtText;
    @BindView(R.id.input_name_last)
    EditText _nameLastText;
    @BindView(R.id.input_birthday)
    TextView _userbirthday;
    @BindView(R.id.input_email)
    EditText _emailText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.btn_signup)
    Button _signupButton;
    @BindView(R.id.link_login)
    TextView _loginLink;
    @BindView(R.id.genderGroup)
    RadioGroup mGenderLayout;

    Date date;

    long Birthdate;
    int AgeString;
    private int year;
    private int month;
    private int day;
    String dateCheck = null;


    private NestedScrollView mSignUpActivity;



    Intent mLoginIntent;

    private Dialog progressDialog;

    public Context context;

    public String provider;

    private Uri picUri;
    private Uri picUri2;

    private TextView _TextAccept;

    /////////////////////////

    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    /////////////////////////

    protected boolean genderString = true;

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_male:
                if (checked)
                    gender = User.GENDER_MALE;
                    genderString = true;

                break;
            case R.id.radio_femele:
                if (checked)
                    gender = User.GENDER_FEMALE;
                    genderString = false;
                break;
        }
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int mYear, int monthOfYear, int dayOfMonth) {

            year = mYear;
            month = monthOfYear;
            day = dayOfMonth;

            Calendar calendar = Calendar.getInstance();
            calendar.set(year,month,day);

            Date BirDate = calendar.getTime();

            Calendar dob = Calendar.getInstance();
            Calendar today = Calendar.getInstance();


            dob.set(year,month,day);

            int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

            if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            Integer ageInt = age;
            String ageS = ageInt.toString();

            date = BirDate;

            dateCheck = ("true");




            //String Birthdate = String.valueOf((new StringBuilder().append(day).append("/").append(month + 1).append("/").append(year).append("")));

            Birthdate = BirDate.getTime();

            AgeString = ageInt;

           // _userbirthday.setText(new StringBuilder().append(day).append("/").append(month + 1).append("/").append(year).append(" ") + " " + "(" + ageS + " " + "year old" + ")");

            Date date = new Date(BirDate.getTime());
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy "); // the format of your date

            String birthday = sdf.format(date);

            _userbirthday.setText(birthday + " " + "(" + ageS + " " + "year old" + ")");


        }

    };


    protected void showMessage(String message) {
        Snackbar.make(mSignUpActivity, message, Snackbar.LENGTH_SHORT).show();
    }

    private void updateFirebaseToken(String uid, String token) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.ARG_USERS)
                .child(uid)
                .child(Constants.ARG_FIREBASE_TOKEN)
                .setValue(token);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        ButterKnife.bind(this);

        _userbirthday.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                DatePickerDialog dpd = new DatePickerDialog(SignUpActivity.this,mDateSetListener,year,month,day);

                final Calendar calendar = Calendar.getInstance();
                final Calendar calendar2 = Calendar.getInstance();

                calendar2.add(Calendar.YEAR,-18);

                dpd.getDatePicker().setMaxDate(calendar2.getTimeInMillis());

                // Subtract 6 days from Calendar updated date
                //calendar.add(Calendar.DATE, -6);

                calendar.add(Calendar.YEAR,-65);

                // Set the Calendar new date as minimum date of date picker
                dpd.getDatePicker().setMinDate(calendar.getTimeInMillis());


                //dpd.getDatePicker().setMaxDate(new Date().getTime());

                dpd.show();
            }
        });


        String text1 = "Already have an account?";
        String text2 = "Login.";

        //holder.PostPhoto.setBackgroundColor(Color.alpha(0));

        SpannableString span1 = new SpannableString(text1);
        span1.setSpan(new RelativeSizeSpan(1f),0,text1.length(),SPAN_INCLUSIVE_INCLUSIVE);


        SpannableString span2 = new SpannableString(text2);
        span2.setSpan(new StyleSpan(Typeface.BOLD),0,text2.length(),SPAN_INCLUSIVE_INCLUSIVE); // set Style

        // let's put both spans together with a separator and all
        CharSequence finalText = TextUtils.concat(span1," ",span2);


        _loginLink.setText(finalText);


        mAuth = FirebaseAuth.getInstance();



        mLoginIntent = this.getIntent();

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });


        mSignUpActivity = (NestedScrollView) findViewById(R.id.layout_signup);

        //Initialize ImageView
        //mImageView = (BezelImageView) findViewById(R.id.profilePhoto);




        mLoginIntent = this.getIntent();


        TextView textView =(TextView)findViewById(R.id.text_accept);
        textView.setClickable(true);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        String text = String.format(getString(R.string.signup_accept) + getString(R.string.terms) + getString(R.string.and) + getString(R.string.privacy));
        textView.setText(Html.fromHtml(text));

    }


    public void signup() {
        Log.d(TAG, "Signup");



        if (!validate()) {
            onSignupFailed();
            return;
        }

        if (isInternetAvailable()) {
            _signupButton.setEnabled(true);

            showProgressBar(getString(R.string.signup_creat));


            String username1 = _nameFisrtText.getText().toString().trim() + _nameLastText.getText().toString().trim();

            String usernameFinal = username1.replace(" ","");


            String email = _emailText.getText().toString();
            String password = _passwordText.getText().toString();
            String firstname = _nameFisrtText.getText().toString();
            String lastname = _nameLastText.getText().toString();
            String fullname = _nameFisrtText.getText().toString() + " " + _nameLastText.getText().toString();
            String username = usernameFinal.toLowerCase();
            String desc = getString(R.string.signup_yh);
            String isNoVip = "novip";



            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                sendEmailVerification();
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");


                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null) {

                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(fullname)
                                            .build();

                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        //Log.d(TAG, "User profile updated.");

                                                        String userID = User.getCurrentUserId();
                                                        User newUSer = new User(username, user.getUid(), user.getDisplayName(), firstname, lastname,  user.getEmail(), Birthdate,  genderString, false, 0, isNoVip, desc, AgeString, 4, 4, 4);
                                                        DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference();

                                                        assert userID != null;
                                                        firebaseRef.child(User.Class).child(userID).setValue(newUSer).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if(task.isSuccessful()){

                                                                    updateFirebaseToken(user.getUid(),
                                                                            new SharedPrefUtil(getApplicationContext()).getString(Constants.ARG_FIREBASE_TOKEN, null));

                                                                    dismissProgressBar();

                                                                    Intent loginIntent = new Intent(SignUpActivity.this, LocationActivity.class);
                                                                    SignUpActivity.this.startActivity(loginIntent);
                                                                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                    SignUpActivity.this.finish();
                                                                }
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {

                                                                dismissProgressBar();

                                                                Snackbar.make(mSignUpActivity, "SignUp error", Snackbar.LENGTH_INDEFINITE).setAction(R.string.login_ok, new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {

                                                                    }
                                                                }).setActionTextColor(Color.WHITE).show();
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                }

                            } else {

                                Snackbar.make(mSignUpActivity, "SignUp error", Snackbar.LENGTH_INDEFINITE).setAction(R.string.login_ok, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                }).setActionTextColor(Color.WHITE).show();

                                dismissProgressBar();
                            }

                        }
                    });

        }else{

            showInternetConnectionLostMessage();
        }
    }

    private void sendEmailVerification() {

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification failed", task.getException());
                            //Toast.makeText(SignUpActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), R.string.login_missi, Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String namefisrt = _nameFisrtText.getText().toString();
        String nameLast = _nameLastText.getText().toString();
        //String birthday = _userbirthday.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();


        if (namefisrt.isEmpty() || namefisrt.length() < 3) {
            _nameFisrtText.setError(getString(R.string.signup_name_at));
            valid = false;
        } else {
            _nameFisrtText.setError(null);
        }

        if (nameLast.isEmpty() || nameLast.length() < 3) {
            _nameLastText.setError(getString(R.string.signup_name_at));
            valid = false;
        } else {
            _nameLastText.setError(null);
        }

        if (dateCheck == null) {
            _userbirthday.setError(getString(R.string.signup_user_date));
            valid = false;
        } else {
            _userbirthday.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError(getString(R.string.signup_valid_email));
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 6) {
            _passwordText.setError(getString(R.string.signup_pass_atl));

            valid = false;
        } else {
            _passwordText.setError(null);

        }


        if (gender == null) {

            valid = false;


            new android.support.v7.app.AlertDialog.Builder(SignUpActivity.this)
                    .setTitle(getString(R.string.signup_gender))
                    .setIcon(android.R.drawable.stat_notify_error)
                    .setMessage(getString(R.string.signup_gender_explzin))
                    .setCancelable(false)
                    .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,int which) {
                            // Whatever...
                        }
                    }).create().show();

        }
        //else
        //{
        //    mGenderLayout.setError(null);
        // }

        return valid;
    }

    public void showInternetConnectionLostMessage() {

        Snackbar.make(mSignUpActivity, R.string.login_no_int, Snackbar.LENGTH_SHORT).show();
    }

    public boolean isInternetAvailable(){
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    public void showProgressBar(String message){
        progressDialog = ProgressDialog.show(this, "", message, true);
    }
    public void dismissProgressBar() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        // Do Here what ever you want do on back press;
    }

    //previewing Image

}
