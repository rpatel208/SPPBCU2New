package com.allentownblower.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.allentownblower.application.AllentownBlowerApplication;
import com.allentownblower.common.CodeReUse;
import com.allentownblower.common.ObserverActionID;
import com.allentownblower.common.Utility;

import java.util.Timer;
import java.util.TimerTask;

public class DiagnosticsCommandService extends Service {

    private static final String TAG = "DiagnosticsCommandService";

    //interval between two services(Here Service run every 5 seconds)
    public static int notify = CodeReUse.DiagnosticsServiceTimerIntervalWithFeedBack;
    //run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    //timer handling
    private Timer mTimer = null;

    String parseValue;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
//        if (mTimer != null) // Cancel if already existed
//            mTimer.cancel();
//        else
//            mTimer = new Timer();   //recreate new
//        mTimer.scheduleAtFixedRate(new DiagnosticsCommandService.TimeDisplay(), CodeReUse.PostDelayedReadData, notify);   //Schedule task

        /*if (parseValue.equalsIgnoreCase("alertview_diagnostics_details")) {
            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nDiagnosticsDataWithFeedbackData);
        } else {
            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nDiagnosticsDataOnly);
        }*/

//        if (parseValue.equalsIgnoreCase("alertview_diagnostics")) {
//            notify = CodeReUse.DiagnosticsServiceTimerIntervalWithFeedBack;
//        } else {
//            notify = CodeReUse.DiagnosticsServiceTimerIntervalDataonly;
//        }

//        else if (parseValue.equalsIgnoreCase("alertview_diagnostics_details")){
//
//        }

    }

    @Override
    public void onDestroy() {
//        mTimer.cancel();    //For Cancel Timer
        Utility.Log(TAG, "Service is Destroyed");
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
                    //Toast.makeText(DiagnosticsCommandService.this, "Service is running", Toast.LENGTH_SHORT).show();


                }
            });

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        // this getter is just for example purpose, can differ
        if (intent != null && intent.getExtras() != null) {
            parseValue = intent.getExtras().getString("parseValue");
            Log.e("parseValue", parseValue);
        }

//        if (parseValue.equalsIgnoreCase("alertview_diagnostics_details")) {
//            notify = CodeReUse.DiagnosticsServiceTimerIntervalDataonly;
//        } else {
//            notify = CodeReUse.DiagnosticsServiceTimerIntervalWithFeedBack;
//        }

        if (parseValue.equalsIgnoreCase("alertview_diagnostics_details")) {
            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nDiagnosticsDataWithFeedbackData);
        } else {
            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nDiagnosticsDataOnly);
        }

        return super.onStartCommand(intent, flags, startId);


    }
}
