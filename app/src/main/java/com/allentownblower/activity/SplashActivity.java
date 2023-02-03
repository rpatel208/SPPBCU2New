package com.allentownblower.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.allentownblower.R;
import com.allentownblower.application.AllentownBlowerApplication;
import com.allentownblower.bluetooth.Constants;
import com.allentownblower.common.PrefManager;
import com.allentownblower.common.ResponseHandler;
import com.allentownblower.common.Utility;
import com.allentownblower.database.SqliteHelper;
import com.allentownblower.module.RackModel;
import com.allentownblower.module.RackSetupModel;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    private AllentownBlowerApplication allentownBlowerApplication;
    private SqliteHelper dpHelper;
    private Activity act;
    private RackSetupModel rackModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        act = this;
        Utility.setSoftInputAlwaysHide(act);
//        allentownBlowerApplication = (AllentownBlowerApplication) act.getApplication();
//        allentownBlowerApplication.getObserver().addObserver(this);
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        dpHelper = new SqliteHelper(act);
//        isSetUpCompleted = dpHelper.isSetupCompletedCheckFromDataBase();
        ArrayList<RackSetupModel> arrRackList = new ArrayList<>();
        arrRackList = dpHelper.getDataFromRackSetUpTableNew();

        if (arrRackList.size() > 0) {
            rackModel = arrRackList.get(0);
        }
        Log.e("TAG", "Before SDK Version : " + String.valueOf(Build.VERSION.SDK_INT));
        if (Build.VERSION.SDK_INT >= 30) {
            Log.e("TAG", "After SDK Version : " + String.valueOf(Build.VERSION.SDK_INT));
            Intent mintent = new Intent();
            mintent.setAction("android.intent.action.WM_Property_Set_Notify");
            mintent.putExtra("SetPropertyValue", "persist.sys.pkg.name;com.allentownblower");
            this.sendBroadcast(mintent);


            Intent mintent2 = new Intent();
            mintent2.setAction("android.intent.action.WM_Property_Set_Notify");
            mintent2.putExtra("SetPropertyValue", "persist.sys.class.name;com.allentownblower.activity.SplashActivity");
            this.sendBroadcast(mintent2);
        }
//        Intent mintent = new Intent();
//        mintent.setAction("android.intent.action.WM_Property_Set_Notify");
//        mintent.putExtra("SetPropertyValue","persist.sys.pkg.name;com.allentownblower");
//        //mintent.putExtra("SetPropertyValue","persist.sys.pkg.name;com.android.chrome");
//        this.sendBroadcast(mintent);
//
//
//        Intent mintent2 = new Intent();
//        mintent2.setAction("android.intent.action.WM_Property_Set_Notify");
//        mintent2.putExtra("SetPropertyValue","persist.sys.class.name;com.allentownblower.activity.SplashActivity");
//        //mintent2.putExtra("SetPropertyValue","persist.sys.class.name;com.google.android.apps.chrome.Main");
//        this.sendBroadcast(mintent2);
        ArrayList<RackSetupModel> finalArrRackList = dpHelper.getDataFromRackSetUpTableNew();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (finalArrRackList.size() > 0) {
                    String bType="";
                    if(finalArrRackList.get(0).getACsetpt()<0)
                        bType =Utility.BCU2;
                    else
                        bType =Utility.SPP;
                    Intent intent = new Intent(act, HomeActivity.class);
                    intent.putExtra(Utility.STR_BLOWER_TYPE, bType);
                    startActivity(intent);
//                  finish();
                } else {
                    Intent intent = new Intent(act, RackSetUpNewActivity.class);
                    startActivity(intent);
//                  finish();
              }
//                }
            }
        }, 1000);
    }
}