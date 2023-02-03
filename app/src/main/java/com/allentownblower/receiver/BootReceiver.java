package com.allentownblower.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.allentownblower.activity.HomeActivity;
import com.allentownblower.activity.RackSetUpNewActivity;

/**
 * Created by Mayur Yadav on 01 Sep 2019.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT < 30)
        {
            Intent myIntent = new Intent(context, RackSetUpNewActivity.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(myIntent);
        }
    }

}