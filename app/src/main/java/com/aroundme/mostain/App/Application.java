package com.aroundme.mostain.App;

import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.facebook.FacebookSdk;
import com.google.firebase.database.FirebaseDatabase;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.twitter.TwitterEmojiProvider;


/**
 * Created by Angopapo, LDA on 01.09.17.
 */
public class Application extends MultiDexApplication {

    public static final String MY_GEOFIRE = "geofire";

    private static Application mInstance;

    private static boolean sIsChatActivityOpen = false;

    public static boolean isChatActivityOpen() {
        return sIsChatActivityOpen;
    }

    public static void setChatActivityOpen(boolean isChatActivityOpen) {
        Application.sIsChatActivityOpen = isChatActivityOpen;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(getBaseContext());
        mInstance = this;

        EmojiManager.install(new TwitterEmojiProvider());

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FacebookSdk.sdkInitialize(getApplicationContext());

    }

    public static synchronized Application getInstance() {
        return mInstance;
    }

}
