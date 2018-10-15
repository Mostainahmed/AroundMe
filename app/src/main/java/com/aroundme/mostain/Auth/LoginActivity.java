package com.aroundme.mostain.Auth;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.angopapo.aroundme2.AroundMe.NearMe.AroundMeActivity;
import com.angopapo.aroundme2.Class.User;
import com.angopapo.aroundme2.R;
import com.angopapo.aroundme2.Utils.service.Constants;
import com.angopapo.aroundme2.Utils.service.SharedPrefUtil;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;


public class LoginActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    public static int APP_REQUEST_CODE = 99;

    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;


    private LoginButton facebookLogin;
    private EditText _emailText;
    private EditText _passwordText;
    private Button _loginButton;
    private TextView _SignUpButton;
    private Button _loginPhone;
    private TextView _forgotLink;

    Profile mFbProfile;

    private RelativeLayout mLoginLayout;

    private Dialog progressDialog;

    private boolean doubleBackToExitPressedOnce;

    //String name = null, email = null, usernamee = null, gender = null, genderSelected = null, emailSelected = null, firstName = null, lastName = null, profilePic = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginLayout = (RelativeLayout) findViewById(R.id.layout_login);

        facebookLogin = (LoginButton) findViewById(R.id.btn_fb);
        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _loginButton = (Button) findViewById(R.id.btn_login);
        _SignUpButton = (TextView) findViewById(R.id.btn_signup);
        _loginPhone = (Button) findViewById(R.id.btn_phone);
        _forgotLink = (TextView) findViewById(R.id.link_forgot);


        mAuth = FirebaseAuth.getInstance();

        String text1 = "Forgot your details?";
        String text2 = "Get help signing in.";


        SpannableString span1 = new SpannableString(text1);
        span1.setSpan(new RelativeSizeSpan(1f),0,text1.length(),SPAN_INCLUSIVE_INCLUSIVE);


        SpannableString span2 = new SpannableString(text2);
        span2.setSpan(new StyleSpan(Typeface.BOLD),0,text2.length(),SPAN_INCLUSIVE_INCLUSIVE); // set Style

        // let's put both spans together with a separator and all
        CharSequence finalText = TextUtils.concat(span1," ",span2);


        _forgotLink.setText(finalText);


        // Forgot text

        String text3 = "Don't have an account? ";
        String text4 = "Sign up.";


        SpannableString span3 = new SpannableString(text3);
        span3.setSpan(new RelativeSizeSpan(1f),0,text3.length(),SPAN_INCLUSIVE_INCLUSIVE);

        SpannableString span4 = new SpannableString(text4);
        span4.setSpan(new StyleSpan(Typeface.BOLD),0,text4.length(),SPAN_INCLUSIVE_INCLUSIVE); // set Style


        // let's put both spans together with a separator and all
        CharSequence finalText2 = TextUtils.concat(span3," ",span4);

        _SignUpButton.setText(finalText2);


        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _loginPhone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //onLoginPhone(v);
                Intent mainIntent = new Intent(LoginActivity.this, PhoneAuthActivity.class);
                startActivity(mainIntent);
            }
        });

        _SignUpButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(mainIntent);
            }
        });
        _forgotLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the forgot password activity
                Intent intent = new Intent(getApplicationContext(), ForgotActivity.class);
                startActivity(intent);
            }
        });

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        facebookLogin.setReadPermissions("email", "public_profile");
        facebookLogin.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");

            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);

            }
        });

    }

    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        // [START_EXCLUDE silent]
        //showProgressDialog();
        // [END_EXCLUDE]
        showProgressBar(getString(R.string.login_auth2));

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        String desc = getString(R.string.signup_yh);
        String isNoVip = "novip";


        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            assert user != null;
                            final DatabaseReference userFacebook = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
                            userFacebook.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.child("isNew").getValue() == null ) {

                                            User newUSer = new User(
                                                    user.getPhotoUrl().toString(),
                                                    user.getPhotoUrl().toString(),
                                                    user.getUid(),
                                                    user.getDisplayName().replace(" ", "").toLowerCase().trim(),
                                                    user.getDisplayName(),
                                                    user.getEmail(),
                                                    0, isNoVip, desc, 18, 4, 4, 4, false,"false");
                                            DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference();

                                            firebaseRef.child(User.Class).child(user.getUid()).setValue(newUSer).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if(task.isSuccessful()){

                                                        updateFirebaseToken(user.getUid(),
                                                                new SharedPrefUtil(getApplicationContext()).getString(Constants.ARG_FIREBASE_TOKEN, null));

                                                        dismissProgressBar();

                                                        Intent loginIntent = new Intent(LoginActivity.this, LoginActivity.class);
                                                        LoginActivity.this.startActivity(loginIntent);
                                                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        LoginActivity.this.finish();
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                    dismissProgressBar();

                                                    Snackbar.make(mLoginLayout, "Facebook error 1", Snackbar.LENGTH_INDEFINITE).setAction(R.string.login_ok, new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {

                                                        }
                                                    }).setActionTextColor(Color.WHITE).show();
                                                }
                                            });

                                        }  else {

                                        if (dataSnapshot.child("isNew").getValue().equals("false")) {

                                            dismissProgressBar();

                                            updateFirebaseToken(user.getUid(),
                                                    new SharedPrefUtil(getApplicationContext()).getString(Constants.ARG_FIREBASE_TOKEN, null));

                                            Intent loginIntent = new Intent(LoginActivity.this, LoginActivity.class);
                                            LoginActivity.this.startActivity(loginIntent);
                                            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            LoginActivity.this.finish();
                                        }

                                    }


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.d(TAG, "Facebook error 2" + databaseError.getMessage());

                                    dismissProgressBar();

                                    Snackbar.make(mLoginLayout, "Facebook error 3", Snackbar.LENGTH_INDEFINITE).setAction(R.string.login_ok, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    }).setActionTextColor(Color.WHITE).show();
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            Snackbar.make(mLoginLayout, "Facebook error 5", Snackbar.LENGTH_INDEFINITE).setAction(R.string.login_ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }).setActionTextColor(Color.WHITE).show();

                            dismissProgressBar();
                        }

                    }
                });
    }

    private void updateFirebaseToken(String uid, String token) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.ARG_USERS)
                .child(uid)
                .child(Constants.ARG_FIREBASE_TOKEN)
                .setValue(token);
    }


    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }
        if (isInternetAvailable()) {
        _loginButton.setEnabled(true);


            showProgressBar(getString(R.string.login_auth2));

        // Login credecials by user

        Log.d("OnClick", "SignInStart");
        String email = _emailText.getText().toString().toLowerCase().trim();
        String password = _passwordText.getText().toString();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");

                                updateFirebaseToken(task.getResult().getUser().getUid(),
                                        new SharedPrefUtil(getApplicationContext()).getString(Constants.ARG_FIREBASE_TOKEN, null));

                                dismissProgressBar();

                                // Start an intent for the dispatch activity
                                Intent intent = new Intent(LoginActivity.this, AroundMeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);


                            } else {
                                Snackbar.make(mLoginLayout, "Login error", Snackbar.LENGTH_INDEFINITE).setAction(R.string.login_ok, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                }).setActionTextColor(Color.WHITE).show();

                                dismissProgressBar();
                            }

                        }
                    });

        }else {

            showInternetConnectionLostMessage();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Log.d("myapp:LoginActivity","currentUser exist");
            Intent mainIntent = new Intent(LoginActivity.this, AroundMeActivity.class);
            startActivity(mainIntent);
            LoginActivity.this.finish();
        }
        Log.d("myapp:LoginActivity", "onResume");

    }

    public void showInternetConnectionLostMessage(){
        Snackbar.make(mLoginLayout, R.string.login_no_int, Snackbar.LENGTH_SHORT).show();

    }

    public boolean isInternetAvailable(){
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

// Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }


    @Override
    public void onBackPressed() {

        if(doubleBackToExitPressedOnce)
        {
            super.onBackPressed();
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.login_press_again, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

    }

    @Override
    public void onClick(View v) {

    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), R.string.login_missi, Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;



        String username = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (username.isEmpty() || username.length() < 3) {
            _emailText.setError(getString(R.string.login_email_at));
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 ) {
            _passwordText.setError(getString(R.string.login_pass_at));
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
    public void showProgressBar(String message){
        progressDialog = ProgressDialog.show(LoginActivity.this, "", message, true);
    }
    public void dismissProgressBar() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if ( progressDialog!=null && progressDialog.isShowing() ){
            progressDialog.cancel();
        }
    }
}
