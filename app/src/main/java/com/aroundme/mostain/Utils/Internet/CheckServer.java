package com.aroundme.mostain.Utils.Internet;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angopapo.aroundme2.R;

import java.net.URL;
import java.net.URLConnection;

public class CheckServer extends RelativeLayout implements View.OnClickListener{

    private Button mRefreshButton;
    private ProgressBar mProgressBar;
    private TextView mWaitMessageText;
    private Context mContext;
    private OnConnectionIsAvailableListener mOnConnectionIsAvailableListener;

    public CheckServer(Context context, AttributeSet attrs){
        super(context, attrs);
        mContext = context;
        init();
    }

    public void init(){
        inflate(getContext(), R.layout.view_wait_for_internet_connection, this);
        mWaitMessageText = (TextView) findViewById(R.id.text_wait_message);
        mRefreshButton = (Button)findViewById(R.id.button_refresh);
        mRefreshButton.setVisibility(View.GONE);
        mRefreshButton.setOnClickListener(this);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_refresh:
                setWaitMessageNormal();
                if(mOnConnectionIsAvailableListener != null)
                    checkInternetConnection(mOnConnectionIsAvailableListener);
                break;
        }
    }

    private void setWaitMessageNegative(){
        mProgressBar.setVisibility(INVISIBLE);
        mWaitMessageText.setText(R.string.wait_int);
    }

    private void setWaitMessageNormal(){
        mProgressBar.setVisibility(VISIBLE);
        mWaitMessageText.setText(R.string.wait_wait);
    }

    public void checkInternetConnection(OnConnectionIsAvailableListener listener){
        setVisibility(VISIBLE);
        mOnConnectionIsAvailableListener = listener;
        new AsyncCheckInternetConnection(listener).execute();
    }


    private class AsyncCheckInternetConnection extends AsyncTask<Void, Void, Boolean>{
        OnConnectionIsAvailableListener mListener;

        public AsyncCheckInternetConnection(OnConnectionIsAvailableListener listener){
            mListener = listener;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try{
                URL myUrl = new URL("https://console.firebase.google.com/");
                URLConnection connection = myUrl.openConnection();
                connection.setConnectTimeout(5000);
                connection.connect();
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isConnected) {
            if(isConnected){
                mListener.onConnectionIsAvailable();
            } else {
                setWaitMessageNegative();
                mRefreshButton.setVisibility(VISIBLE);
            }
        }
    }

    public void close(){
        setVisibility(GONE);
    }


    public interface OnConnectionIsAvailableListener{
        void onConnectionIsAvailable();
    }
}
