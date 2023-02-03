package com.allentownblower.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.allentownblower.application.AllentownBlowerApplication;
import com.allentownblower.common.CodeReUse;
import com.allentownblower.common.ObserverActionID;
import com.allentownblower.common.Utility;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Mayur Yadav on 01 Sep 2019.
 */
public class MyService extends Service {

    private static final String TAG = "MyService";

    //interval between two services(Here Service run every 5 seconds)
    public static final int notify = CodeReUse.ServiceTimerInterval;
    //run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    //timer handling
    private Timer mTimer = null;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        /*if (mTimer != null) // Cancel if already existed
            mTimer.cancel();
        else
            mTimer = new Timer();   //recreate new
        mTimer.scheduleAtFixedRate(new TimeDisplay(), CodeReUse.PostDelayedReadData, notify);   //Schedule task*/
        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nFeedbackData);
    }

    @Override
    public void onDestroy() {
//        mTimer.cancel();    //For Cancel Timer
        Utility.Log(TAG,"Service is Destroyed");
        //Toast.makeText(this, "Service is Destroyed", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    //class TimeDisplay for handling task
    class TimeDisplay extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    // display toast
                    //Toast.makeText(MyService.this, "Service is running", Toast.LENGTH_SHORT).show();
//                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nFeedbackData);
                }
            });

        }
    }

}