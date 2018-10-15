package com.aroundme.mostain.Auth;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.angopapo.aroundme2.Class.User;
import com.angopapo.aroundme2.R;
import com.angopapo.aroundme2.Utils.Helper.ActivityWithToolbar;
import com.angopapo.aroundme2.Utils.phonefield.PhoneEditText;
import com.angopapo.aroundme2.Utils.service.Constants;
import com.angopapo.aroundme2.Utils.service.SharedPrefUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.greysonparrelli.permiso.Permiso;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;
import static android.view.View.VISIBLE;

public class PhoneAuthActivity extends AppCompatActivity implements ActivityWithToolbar {

  private static final String TAG = "PhoneAuthActivity";

  private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

  private static final int STATE_VERIFY_SUCCESS = 4;

  private FirebaseAuth mAuth;

  private boolean mVerificationInProgress = false;
  private String mVerificationId;
  private PhoneAuthProvider.ForceResendingToken mResendToken;
  private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

  private Toolbar mToolbar;
  private Dialog progressDialog;
  private RelativeLayout mPhoneLayout, layoutVerify, layoutInsert;
  private TextView mExplain, mDetailText, mValidNumber, mResend;
  private PhoneEditText phoneEditText;
  private EditText mVerificationField;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_phone_auth);

    Permiso.getInstance().setActivity(this);

    /*mToolbar = (Toolbar) findViewById(R.id.toolbar);

    setSupportActionBar(mToolbar);
    getSupportActionBar().setTitle("Back to login");
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/


    phoneEditText = (PhoneEditText) findViewById(R.id.edit_text);
    mExplain = (TextView) findViewById(R.id.textView10);
    mDetailText = (TextView) findViewById(R.id.textView13);
    mResend = (TextView) findViewById(R.id.button_resend);
    mValidNumber = (TextView) findViewById(R.id.validnumber);
    mVerificationField = (EditText) findViewById(R.id.field_verification_code);

    layoutVerify = (RelativeLayout) findViewById(R.id.layout_verify);
    layoutInsert = (RelativeLayout) findViewById(R.id.layout_insert);

    String text1 = getString(R.string.tap_next_to_verify);
    String text2 = getString(R.string.app_name).replace(" Firebase","");


    SpannableString span1 = new SpannableString(text1);
    span1.setSpan(new RelativeSizeSpan(1f),0,text1.length(),SPAN_INCLUSIVE_INCLUSIVE);


    SpannableString span2 = new SpannableString(text2);
    span2.setSpan(new StyleSpan(Typeface.BOLD),0,text2.length(),SPAN_INCLUSIVE_INCLUSIVE); // set Style

    // let's put both spans together with a separator and all
    CharSequence finalText = TextUtils.concat(span1," ",span2);

    mExplain.setText(finalText);

    TextView textView =(TextView)findViewById(R.id.textView12);
    textView.setClickable(true);
    textView.setMovementMethod(LinkMovementMethod.getInstance());
    String text = String.format(getString(R.string.auth_accept) + getString(R.string.terms) + getString(R.string.and) + getString(R.string.privacy));
    textView.setText(Html.fromHtml(text));

    layoutVerify.setVisibility(View.GONE);
    layoutInsert.setVisibility(VISIBLE);

    Button button = (Button) findViewById(R.id.submit_button);
    Button button2 = (Button) findViewById(R.id.submit_button2);

    mPhoneLayout = (RelativeLayout) findViewById(R.id.phone_activity);


    assert phoneEditText != null;
    assert button != null;

    TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
    String countryCode = tm.getNetworkCountryIso();
    //String Numbers = tm.getLine1Number();

    if (Build.VERSION.SDK_INT >= 23) {

      Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult() {
        @Override
        public void onPermissionResult(Permiso.ResultSet resultSet) {
          if (resultSet.areAllPermissionsGranted()) {


            String main_data[] = {"data1", "is_primary", "data3", "data2", "data1", "is_primary", "photo_uri", "mimetype"};
            Object object = getContentResolver().query(Uri.withAppendedPath(android.provider.ContactsContract.Profile.CONTENT_URI, "data"),
                    main_data, "mimetype=?",
                    new String[]{"vnd.android.cursor.item/phone_v2"},
                    "is_primary DESC");
            if (object != null) {
              do {
                if (!((Cursor) (object)).moveToNext())
                  break;
                // This is the phoneNumber
                final String s1 = ((Cursor) (object)).getString(4);

                phoneEditText.setPhoneNumber(s1);

              } while (true);
              ((Cursor) (object)).close();
            } else {

              phoneEditText.setHint(R.string.phone_hint);
            }

          } else {

            Toast.makeText(PhoneAuthActivity.this, getString(R.string.msg_permission_required), Toast.LENGTH_LONG).show();
          }
        }

        @Override
        public void onRationaleRequested(Permiso.IOnRationaleProvided callback, String... permissions) {
          Permiso.getInstance().showRationaleInDialog(null,
                  getString(R.string.msg_permission_required),
                  null, callback);
        }
      }, Manifest.permission.READ_CONTACTS);


    } else {

      String main_data[] = {"data1", "is_primary", "data3", "data2", "data1", "is_primary", "photo_uri", "mimetype"};
      Object object = getContentResolver().query(Uri.withAppendedPath(android.provider.ContactsContract.Profile.CONTENT_URI, "data"),
              main_data, "mimetype=?",
              new String[]{"vnd.android.cursor.item/phone_v2"},
              "is_primary DESC");

      if (object != null) {
        do {
          if (!((Cursor) (object)).moveToNext())
            break;
          // This is the phoneNumber
          final String s1 = ((Cursor) (object)).getString(4);

          phoneEditText.setPhoneNumber(s1);

        } while (true);
        ((Cursor) (object)).close();
      } else {

        phoneEditText.setHint(R.string.phone_hint);
      }
    }

    //Toast.makeText(PhoneAuthActivity.this, " SIM is: " + countryCode, Toast.LENGTH_LONG).show();

    String localCountryCode = Locale.getDefault().getCountry();

    //Toast.makeText(PhoneAuthActivity.this, " Local is: " + localCountryCode, Toast.LENGTH_LONG).show();

    if (countryCode != null){

      phoneEditText.setDefaultCountry(countryCode.toUpperCase());

    } else if (localCountryCode != null){

      phoneEditText.setDefaultCountry(localCountryCode.toUpperCase());

    } else {

      phoneEditText.setDefaultCountry("US");

    }

    /*if (Numbers != null) {

      phoneEditText.setPhoneNumber(Numbers);
    } else {

      phoneEditText.setHint(R.string.phone_hint);
    }*/




    // Phone auth callbacks

    // [START initialize_auth]
    mAuth = FirebaseAuth.getInstance();
    // [END initialize_auth]

    // Initialize phone auth callbacks
    // [START phone_auth_callbacks]
    mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

      @Override
      public void onVerificationCompleted(PhoneAuthCredential credential) {
        // This callback will be invoked in two situations:
        // 1 - Instant verification. In some cases the phone number can be instantly
        //     verified without needing to send or enter a verification code.
        // 2 - Auto-retrieval. On some devices Google Play services can automatically
        //     detect the incoming verification SMS and perform verificaiton without
        //     user action.
        Log.d(TAG, "onVerificationCompleted:" + credential);
        // [START_EXCLUDE silent]
        mVerificationInProgress = false;
        showProgressBar("Processing...");

        // Update the UI and attempt sign in with the phone credential
        updateUI(STATE_VERIFY_SUCCESS, credential);
        layoutVerify.setVisibility(VISIBLE);
        layoutInsert.setVisibility(View.GONE);
        signInWithPhoneAuthCredential(credential);
        //dismissProgressBar();
      }

      @Override
      public void onVerificationFailed(FirebaseException e) {
        // This callback is invoked in an invalid request for verification is made,
        // for instance if the the phone number format is not valid.
        Log.w(TAG, "onVerificationFailed", e);
        // [START_EXCLUDE silent]
        mVerificationInProgress = false;
        dismissProgressBar();
        // [END_EXCLUDE]

        if (e instanceof FirebaseAuthInvalidCredentialsException) {
          // Invalid request
          // [START_EXCLUDE]
          phoneEditText.setError("Invalid phone number.");
          dismissProgressBar();
          // [END_EXCLUDE]
        } else if (e instanceof FirebaseTooManyRequestsException) {
          // The SMS quota for the project has been exceeded
          // [START_EXCLUDE]
          Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.", Snackbar.LENGTH_SHORT).show();
          dismissProgressBar();
          // [END_EXCLUDE]
        }

        // Show a message and update the UI
        // [START_EXCLUDE]
        //updateUI(STATE_VERIFY_FAILED);
        dismissProgressBar();
        mDetailText.setText(R.string.status_verification_failed);
        // [END_EXCLUDE]
      }

      @Override
      public void onCodeSent(String verificationId,
                             PhoneAuthProvider.ForceResendingToken token) {
        // The SMS verification code has been sent to the provided phone number, we
        // now need to ask the user to enter the code and then construct a credential
        // by combining the code with a verification ID.
        Log.d(TAG, "onCodeSent:" + verificationId);

        // Save verification ID and resending token so we can use them later
        mVerificationId = verificationId;
        mResendToken = token;

        layoutVerify.setVisibility(VISIBLE);
        layoutInsert.setVisibility(View.GONE);


        //updateUI(STATE_CODE_SENT);
        mValidNumber.setText(phoneEditText.getPhoneNumber());
        mDetailText.setText(R.string.status_code_sent);
        dismissProgressBar();


      }
    };

    mResend.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        showProgressBar("Resending code...");

        resendVerificationCode(phoneEditText.getPhoneNumber(), mResendToken);
      }
    });

    button2.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        String code = mVerificationField.getText().toString();
        if (TextUtils.isEmpty(code)) {
          mVerificationField.setError("Cannot be empty.");
          return;
        }

        verifyPhoneNumberWithCode(mVerificationId, code);
      }
    });

    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {


        boolean valid = true;

        if (isInternetAvailable()){

          if (phoneEditText.isValid()) {
            phoneEditText.setError(null);
          } else {
            phoneEditText.setError(getString(R.string.invalid_phone_number));
            valid = false;
          }

          if (valid) {

            //Toast.makeText(PhoneAuthActivity.this, R.string.valid_phone_number, Toast.LENGTH_LONG).show();
            showProgressBar("Sending verification code...");
            startPhoneNumberVerification(phoneEditText.getPhoneNumber());
          } else {
            phoneEditText.setError(getString(R.string.invalid_phone_number));
            //Toast.makeText(PhoneAuthActivity.this, R.string.invalid_phone_number, Toast.LENGTH_LONG).show();
          }

        } else {

          showInternetConnectionLostMessage();
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

  private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {
    switch (uiState) {

      case STATE_VERIFY_SUCCESS:

        mDetailText.setText(R.string.status_verification_succeeded);
        showProgressBar("Processing...");

        // Set the verification text based on the credential
        if (cred != null) {
          if (cred.getSmsCode() != null) {
            mVerificationField.setText(cred.getSmsCode());
            //showProgressBar("Verifying...");
          } else {
            mVerificationField.setText(R.string.instant_validation);
          }
        }
        break;

    }

  }

  private void updateUI(int uiState, FirebaseUser user) {
    updateUI(uiState, user, null);
  }

  private void updateUI(int uiState, PhoneAuthCredential cred) {
    updateUI(uiState, null, cred);
  }

  // [START on_start_check_user]
  @Override
  public void onStart() {
    super.onStart();

    // [START_EXCLUDE]
    if (mVerificationInProgress && validatePhoneNumber()) {
      startPhoneNumberVerification(phoneEditText.getPhoneNumber());
    }
    // [END_EXCLUDE]
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
  }


  private void startPhoneNumberVerification(String phoneNumber) {
    // [START start_phone_auth]
    PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,        // Phone number to verify
            60,                 // Timeout duration
            TimeUnit.SECONDS,   // Unit of timeout
            this,               // Activity (for callback binding)
            mCallbacks);        // OnVerificationStateChangedCallbacks
    // [END start_phone_auth]

    mVerificationInProgress = true;
  }

  private void verifyPhoneNumberWithCode(String verificationId, String code) {

    showProgressBar("Verifying...");
    // [START verify_with_code]
    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
    // [END verify_with_code]
    signInWithPhoneAuthCredential(credential);
  }

  // [START resend_verification]
  private void resendVerificationCode(String phoneNumber,
                                      PhoneAuthProvider.ForceResendingToken token) {
    PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,        // Phone number to verify
            60,                 // Timeout duration
            TimeUnit.SECONDS,   // Unit of timeout
            this,               // Activity (for callback binding)
            mCallbacks,         // OnVerificationStateChangedCallbacks
            token);             // ForceResendingToken from callbacks
  }
  // [END resend_verification]

  // [START sign_in_with_phone]
  private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

    //dismissProgressBar();

    mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                  //dismissProgressBar();
                  // Sign in success, update UI with the signed-in user's information
                  Log.d(TAG, "signInWithCredential:success");

                  String desc = getString(R.string.signup_yh);
                  String isNoVip = "novip";

                  FirebaseUser user = task.getResult().getUser();

                  final DatabaseReference userFacebook = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
                  userFacebook.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                      if (dataSnapshot.child("isNewPhone").getValue() == null ) {

                        User newUSer = new User(false, user.getUid(), 0, isNoVip, desc, 18, 4, 4, 4, "false");
                        DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference();

                        firebaseRef.child(User.Class).child(user.getUid()).setValue(newUSer).addOnCompleteListener(new OnCompleteListener<Void>() {
                          @Override
                          public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                              updateFirebaseToken(user.getUid(),
                                      new SharedPrefUtil(getApplicationContext()).getString(Constants.ARG_FIREBASE_TOKEN, null));

                              dismissProgressBar();

                              Intent loginIntent = new Intent(PhoneAuthActivity.this, LoginActivity.class);
                              PhoneAuthActivity.this.startActivity(loginIntent);
                              loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                              PhoneAuthActivity.this.finish();
                            }
                          }
                        }).addOnFailureListener(new OnFailureListener() {
                          @Override
                          public void onFailure(@NonNull Exception e) {

                            dismissProgressBar();

                            Snackbar.make(mPhoneLayout, "Phone Login error 1", Snackbar.LENGTH_INDEFINITE).setAction(R.string.login_ok, new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {

                              }
                            }).setActionTextColor(Color.WHITE).show();
                          }
                        });

                      }  else {

                        if (dataSnapshot.child("isNewPhone").getValue().equals("false")) {

                          updateFirebaseToken(user.getUid(),
                                  new SharedPrefUtil(getApplicationContext()).getString(Constants.ARG_FIREBASE_TOKEN, null));

                          dismissProgressBar();

                          Intent loginIntent = new Intent(PhoneAuthActivity.this, LoginActivity.class);
                          PhoneAuthActivity.this.startActivity(loginIntent);
                          loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                          PhoneAuthActivity.this.finish();
                        }

                      }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                      Log.d(TAG, "Phone Login error 2" + databaseError.getMessage());

                      dismissProgressBar();

                      Snackbar.make(mPhoneLayout, "Phone Login error 3", Snackbar.LENGTH_INDEFINITE).setAction(R.string.login_ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                      }).setActionTextColor(Color.WHITE).show();
                    }
                  });

                } else {
                  // Sign in failed, display a message and update the UI
                  Log.w(TAG, "signInWithCredential:failure", task.getException());
                  if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                    mVerificationField.setError("Invalid code.");
                    dismissProgressBar();

                  }

                  //updateUI(STATE_SIGNIN_FAILED);
                  mDetailText.setText(R.string.status_sign_in_failed);
                  dismissProgressBar();

                }
              }
            });
  }

  private boolean validatePhoneNumber() {
    String phoneNumber = phoneEditText.getPhoneNumber();
    if (TextUtils.isEmpty(phoneNumber)) {
      phoneEditText.setError("Invalid phone number.");
      dismissProgressBar();
      return false;
    }

    return true;
  }



  @Override
  public void onBackPressed(){

    if (layoutVerify.getVisibility() == View.VISIBLE || layoutInsert .getVisibility() == View.GONE){

      layoutVerify.setVisibility(View.GONE);
      layoutInsert.setVisibility(VISIBLE);
    } else if (layoutInsert.isShown()){

      super.onBackPressed();
    }


  }

  /*@Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    switch (id){
      case android.R.id.home:
        super.onBackPressed();
        break;
    }

    return super.onOptionsItemSelected(item);
  }*/

  public void showProgressBar(String message){
    progressDialog = ProgressDialog.show(PhoneAuthActivity.this, "", message, true);
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

  public void showInternetConnectionLostMessage(){
    Snackbar.make(mPhoneLayout, R.string.login_no_int, Snackbar.LENGTH_SHORT).show();

  }

  public boolean isInternetAvailable(){
    ConnectivityManager cm =
            (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo netInfo = cm.getActiveNetworkInfo();
    return netInfo != null && netInfo.isConnectedOrConnecting();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return null;
  }

  @Override
  public Toolbar getToolbar() {
    return mToolbar;
  }

  @Override
  public Activity getActivity() {
    return this;
  }

  @Override
  public int getDriwerId() {
    return 0;
  }

  @Override
  public void didReceivedNotification(int id, Object... args) {

  }

  @Override
  public void onSizeChanged(int height) {

  }

  @Override
  protected void onResume() {
    super.onResume();
    Permiso.getInstance().setActivity(this);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    Permiso.getInstance().onRequestPermissionResult(requestCode, permissions, grantResults);
  }
}
