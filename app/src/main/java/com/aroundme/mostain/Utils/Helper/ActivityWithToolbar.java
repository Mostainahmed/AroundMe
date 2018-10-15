package com.aroundme.mostain.Utils.Helper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by MSinga Pro on 07.09.15.
 */
public interface ActivityWithToolbar {
    View onCreateView(LayoutInflater inflater, ViewGroup container,
                      Bundle savedInstanceState);

    Toolbar getToolbar();
    Activity getActivity();
    int getDriwerId();

    void didReceivedNotification(int id, Object... args);

    void onSizeChanged(int height);

    void startActivity(Intent intent);
}
