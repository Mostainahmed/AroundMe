package com.aroundme.mostain.App;

/*
  Created by Maravilho on 23/12/15.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.aroundme.mostain.R;

public class SplashScreen extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.splashscreen);


        // uncoment the these lines in you want to play an audio on start
        //mp = MediaPlayer.create(this, R.raw.in_chat_alert);
        //mp.start();


        int splashInterval = 2500;
        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {
                // TODO Auto-generated method stub

                Intent i = new Intent(SplashScreen.this, DispatchActivity.class);
                startActivity(i);

                // finish splash
                this.finish();
            }

            private void finish() {
                // TODO Auto-generated method stub

            }
        }, splashInterval);

    }

}