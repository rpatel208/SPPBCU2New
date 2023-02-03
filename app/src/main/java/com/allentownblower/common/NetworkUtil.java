package com.allentownblower.common;

/**
 * Created by krishnakant on 15/09/15.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {
    @SuppressLint("MissingPermission")
    public static boolean getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

         NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return true;

            return activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }
}
