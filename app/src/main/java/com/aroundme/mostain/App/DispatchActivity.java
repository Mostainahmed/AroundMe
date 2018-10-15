package com.aroundme.mostain.App;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.aroundme.mostain.AroundMe.NearMe.AroundMeActivity;
import com.aroundme.mostain.Auth.LoginActivity;
import com.aroundme.mostain.Class.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DispatchActivity extends Activity {

  private FirebaseAuth mAuth;
  private FirebaseAuth.AuthStateListener mAuthListener;
  private FirebaseUser user;

  public DispatchActivity() {
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (User.getUser() != null){
      Intent mainIntent = new Intent(DispatchActivity.this, AroundMeActivity.class);
      mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
      startActivity(mainIntent);

    } else {

      Intent mainIntent = new Intent(DispatchActivity.this, LoginActivity.class);
      mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
      startActivity(mainIntent);

    }

  }

}
