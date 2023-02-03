package com.allentownblower.application;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.provider.Settings;

import androidx.multidex.MultiDex;

import com.allentownblower.common.AppObserver;
import com.allentownblower.common.PrefManager;
import com.allentownblower.common.Utility;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.allentownblower.R;
import com.allentownblower.common.CodeReUse;
import com.allentownblower.volley.LruBitmapCache;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Mayur Yadav on 01 Sep 2019.
 */
public class AllentownBlowerApplication extends Application {

    public static final String TAG = AllentownBlowerApplication.class.getSimpleName();

    private static AllentownBlowerApplication act;

    public static Typeface openBold, openRegular, openSemiBold;

    private AppObserver observer;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private PrefManager prefManager;

    @SuppressLint({"SdCardPath", "NewApi", "HardwareIds"})
    @Override
    public void onCreate() {
        super.onCreate();

        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));

        initializeFonts();

        act = this;

        prefManager = new PrefManager(act);
        prefManager.setDefaultLockScreen(false);
        prefManager.setOpenNode(false);//added this line on 9/27/19

        Utility.Log("TAG",prefManager.getOpenNode()+" From AllentownBlowerApplication.java");

        observer = new AppObserver(act);
        mRequestQueue = Volley.newRequestQueue(act);

        MultiDex.install(this);

        try {
            CodeReUse.strDeviceID = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            CodeReUse.strAppversion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            CodeReUse.strPackageName = getPackageName();
            CodeReUse.strAppName = getResources().getString(R.string.app_name);
            CodeReUse.strTimezone = TimeZone.getDefault().getID();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initializeFonts() {
        openBold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold.ttf");
        openRegular = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        openSemiBold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-SemiBold.ttf");
    }

    public AppObserver getObserver() {
        return observer;
    }

    public static synchronized AllentownBlowerApplication getInstance() {
        return act;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, int tag) {
        req.setShouldCache(false);
        req.setRetryPolicy(new DefaultRetryPolicy(50 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        req.setTag(tag == 0 ? 111 : tag);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    SharedPreferences sharedPreferences;

    public SharedPreferences getPreferences() {
        return sharedPreferences = getSharedPreferences("StoreCookie", MODE_PRIVATE);
    }

    public void saveCookie(String cookie) {
        if (cookie == null) {
            return;
        }

        SharedPreferences prefs = getPreferences();
        if (null == prefs) {
            return;
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("cookie", cookie);
        editor.apply();
    }

    public String getCookie() {
        SharedPreferences prefs = getPreferences();
        return prefs.getString("cookie", "");
    }

    public void removeCookie() {
        SharedPreferences prefs = getPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("cookie");
        editor.apply();
    }

    public Map getHeader() {
        Map headers = new HashMap();
        headers.put("Content-Type", "application/json");
        return headers;
    }

}