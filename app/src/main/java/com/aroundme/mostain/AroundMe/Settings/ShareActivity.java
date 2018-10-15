package com.aroundme.mostain.AroundMe.Settings;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.angopapo.aroundme2.R;

/**
 * Invites friends to use your application with Facebook, WhatsApp or the standard share intent.
 */
public class ShareActivity extends Activity {

    private static final String TAG = ShareActivity.class.getCanonicalName();

    //private SimpleFacebook simpleFacebook;

    private OnTouchListener shareButtonTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View arg0, MotionEvent arg1) {
            switch (arg1.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                ((ImageView) arg0).getDrawable().setColorFilter(Color.argb(150, 155, 155, 155),
                        PorterDuff.Mode.DST_IN);
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                ((ImageView) arg0).getDrawable().clearColorFilter();
                arg0.invalidate();
                break;
            }
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_share);

        //configureFacebook();

        findViewById(R.id.invite_friends_close).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //FlurryAgent.logEvent(Tracking.Events.INVITE,
                       // Tracking.build(Tracking.Properties.METHOD, Tracking.Values.NONE,
                              //  Tracking.Properties.STATUS, Tracking.Values.OK));

                finish();
            }
        });

        ImageView imageButtonOther = (ImageView) findViewById(R.id.invite_friends_other);
        imageButtonOther.setOnTouchListener(shareButtonTouchListener);
        
       // FlurryAgent.logEvent(Tracking.Events.INVITE_SHOWN);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //FlurryAgent.onStartSession(this, getResources().getString(R.string.flurry_key));
    }

    @Override
    protected void onStop() {
        super.onStop();
       // FlurryAgent.onEndSession(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        //simpleFacebook = SimpleFacebook.getInstance(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //simpleFacebook.onActivityResult(this, requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onWhatsAppInvite(View v) {
        Toast.makeText(this, R.string.invite_friends_toast_after_share, Toast.LENGTH_LONG).show();

        final String marketUrl = getResources().getString(R.string.invite_friends_market_url);
        final String shareBody = String.format(getResources().getString(R.string.invite_friends_share_other_message), marketUrl);
        try {
            Intent shareToWhatsApp = new Intent(Intent.ACTION_SEND);
            shareToWhatsApp.setType("text/plain");

            shareToWhatsApp.putExtra(Intent.EXTRA_TEXT, shareBody);

            shareToWhatsApp.setClassName("com.whatsapp", "com.whatsapp.ContactPicker");
            startActivity(shareToWhatsApp);
        } catch (Exception e) {
            Intent shareGeneric = new Intent(Intent.ACTION_SEND);
            shareGeneric.setType("text/plain");
            shareGeneric.putExtra(Intent.EXTRA_TEXT, shareBody);

            startActivity(Intent.createChooser(shareGeneric, getResources().getString(R.string.invite_friends_share_chooser)));
        }

        //FlurryAgent.logEvent(Tracking.Events.INVITE,
           //     Tracking.build(Tracking.Properties.METHOD, Tracking.Values.WHATSAPP,
               //         Tracking.Properties.STATUS, Tracking.Values.OK));
    }

    public void onOtherInvite(View v) {
        final String marketUrl = getResources().getString(R.string.invite_friends_market_url);
        final String shareBody = String.format(getResources().getString(R.string.invite_friends_share_other_message), marketUrl);
        
        Intent shareGeneric = new Intent(Intent.ACTION_SEND);
        shareGeneric.setType("text/plain");
        shareGeneric.putExtra(Intent.EXTRA_TEXT, shareBody);

        startActivity(Intent.createChooser(shareGeneric, getResources().getString(R.string.choose_to_share)));

        //FlurryAgent.logEvent(Tracking.Events.INVITE,
              //  Tracking.build(Tracking.Properties.METHOD, Tracking.Values.OTHER,
                //        Tracking.Properties.STATUS, Tracking.Values.OK));
    }
}
