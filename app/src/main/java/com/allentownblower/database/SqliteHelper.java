package com.allentownblower.database;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.allentownblower.R;
import com.allentownblower.application.AllentownBlowerApplication;
import com.allentownblower.common.ApiHandler;
import com.allentownblower.common.ObserverActionID;
import com.allentownblower.common.PendingID;
import com.allentownblower.common.PrefManager;
import com.allentownblower.common.Utility;
import com.allentownblower.module.RackDetailsModel;
import com.allentownblower.module.RackModel;
import com.allentownblower.module.RackSetupModel;
import com.allentownblower.module.SpinnerObject;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Mayur Yadav on 01 Sep 2019.
 */
public class SqliteHelper extends SQLiteOpenHelper {

    private static final String TAG = SqliteHelper.class.getSimpleName();

    private String DATABASE_PATH;

    private static String DATABASE_NAME = "Allentown_Blower.db";

    private static final Integer Version = 1;
    public SQLiteDatabase db;
    Context mcontext;

    public SqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, Version);
        mcontext = context;
        DATABASE_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        createDB();
    }

    @Override
    public void onCreate(SQLiteDatabase arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @SuppressLint("ObsoleteSdkInt")
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            db.disableWriteAheadLogging();
        }
    }

    public void createDB() {
        boolean dbExist = DbExists();
        if (!dbExist) {
            this.getReadableDatabase();
            copyDataBase();
            alterDataBase();
        } else {
            Utility.Log(TAG, "Database Exist");
        }
    }

    @SuppressWarnings("deprecation")
    private boolean DbExists() {
        SQLiteDatabase db = null;
        try {
            String databasePath = "";
            if (android.os.Build.VERSION.SDK_INT >= 4.2) {
                databasePath = mcontext.getApplicationInfo().dataDir + "/databases/" + DATABASE_NAME;
            } else {
                databasePath = "/data/data/" + mcontext.getPackageName() + "/databases/" + DATABASE_NAME;
            }

            File file = new File(databasePath);
            if (!file.exists())
                return false;

            //String databasePath = DATABASE_PATH + DATABASE_NAME;
            db = SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READWRITE);
            db.setLocale(Locale.getDefault());
            db.setLockingEnabled(true);
            db.setVersion(Version);

        } catch (Exception e) {
            Utility.Log("SqlHelper", "Database Not Found");
            Utility.Log("SqlHelper_e", "" + e.getMessage());
//            e.printStackTrace();
        }

        if (db != null) {
            db.close();
        }

        return db != null;
    }

    private void copyDataBase() {
        InputStream iStream = null;
        OutputStream oStream = null;
        String outFilePath = DATABASE_PATH + DATABASE_NAME;
        try {
            iStream = mcontext.getAssets().open(DATABASE_NAME);
            oStream = new FileOutputStream(outFilePath);
            byte[] buffer = new byte[2048];
            int length;
            while ((length = iStream.read(buffer)) > 0) {
                oStream.write(buffer, 0, length);
            }
            oStream.flush();
            oStream.close();
            iStream.close();
            Utility.Log(TAG, "Database copied");
        } catch (Exception e) {
            Utility.Log(TAG, "not copied" + e.getMessage());
//            e.printStackTrace();
        }
    }

    private void alterDataBase() {
//        db = this.getWritableDatabase();
//
//        String BLDiagnostics = "CREATE TABLE IF NOT EXISTS " + DatabaseTable.TBL_BLDIAGNOSTICS + " ( " + DatabaseTable.COL_BLDIAGNOSTICS_ID + " INTEGER PRIMARY KEY UNIQUE, " + DatabaseTable.COL_BLDIAGNOSTICS_D01 + " VARCHAR, " + DatabaseTable.COL_BLDIAGNOSTICS_D02 + " VARCHAR, " + DatabaseTable.COL_BLDIAGNOSTICS_D03 + " VARCHAR, " + DatabaseTable.COL_BLDIAGNOSTICS_D04 + " VARCHAR, " + DatabaseTable.COL_BLDIAGNOSTICS_D05 + " VARCHAR, " + DatabaseTable.COL_BLDIAGNOSTICS_D06 + " VARCHAR, " + DatabaseTable.COL_BLDIAGNOSTICS_D07 + " VARCHAR, " + DatabaseTable.COL_BLDIAGNOSTICS_D08 + " VARCHAR, " + DatabaseTable.COL_BLDIAGNOSTICS_D09 + " VARCHAR, " + DatabaseTable.COL_BLDIAGNOSTICS_D10 + " VARCHAR, " + DatabaseTable.COL_BLDIAGNOSTICS_D11 + " VARCHAR, " + DatabaseTable.COL_BLDIAGNOSTICS_D12 + " VARCHAR, " + DatabaseTable.COL_BLDIAGNOSTICS_D13 + " VARCHAR, " + DatabaseTable.COL_BLDIAGNOSTICS_D14 + " VARCHAR, " + DatabaseTable.COL_BLDIAGNOSTICS_D15 + " VARCHAR, " + DatabaseTable.COL_BLDIAGNOSTICS_D16 + " VARCHAR, " + DatabaseTable.COL_BLDIAGNOSTICS_D17 + " VARCHAR, " + DatabaseTable.COL_BLDIAGNOSTICS_D18 + " VARCHAR, " + DatabaseTable.COL_BLDIAGNOSTICS_D19 + " VARCHAR, " + DatabaseTable.COL_BLDIAGNOSTICS_D20 + " VARCHAR, " + DatabaseTable.COL_BLDIAGNOSTICS_D21 + " VARCHAR, " + DatabaseTable.COL_BLDIAGNOSTICS_D22 + " VARCHAR, " + DatabaseTable.COL_BLDIAGNOSTICS_D23 + " VARCHAR, " + DatabaseTable.COL_BLDIAGNOSTICS_D24 + " VARCHAR, " + DatabaseTable.COL_BLDIAGNOSTICS_D25 + " VARCHAR, " + DatabaseTable.COL_BLDIAGNOSTICS_D26 + " VARCHAR, " + DatabaseTable.COL_BLDIAGNOSTICS_D27 + " VARCHAR, " + DatabaseTable.COL_BLDIAGNOSTICS_D28 + " VARCHAR, " + DatabaseTable.COL_BLDIAGNOSTICS_D29 + " VARCHAR, " + DatabaseTable.COL_BLDIAGNOSTICS_D30 + " VARCHAR, " + DatabaseTable.COL_BLDIAGNOSTICS_D31 + " VARCHAR, " + DatabaseTable.COL_BLDIAGNOSTICS_CREATEDON + " VARCHAR, " + DatabaseTable.COL_BLDIAGNOSTICS_MODIFIEDON + " VARCHAR )";
//        Log.e("BLDiagnostics", BLDiagnostics);
//        //db.execSQL(BLDiagnostics);
//
//        String BLWiFi = "CREATE TABLE IF NOT EXISTS " + DatabaseTable.TBL_BLWIFI + " ( " + DatabaseTable.COL_BLWIFI_ID + " INTEGER PRIMARY KEY UNIQUE, " + DatabaseTable.COL_BLWIFI_W01 + " VARCHAR, " + DatabaseTable.COL_BLWIFI_W02 + " VARCHAR, " + DatabaseTable.COL_BLWIFI_W03 + " VARCHAR, " + DatabaseTable.COL_BLWIFI_W04 + " VARCHAR, " + DatabaseTable.COL_BLWIFI_W05 + " VARCHAR, " + DatabaseTable.COL_BLWIFI_W06 + " VARCHAR, " + DatabaseTable.COL_BLWIFI_W07 + " VARCHAR, " + DatabaseTable.COL_BLWIFI_W08 + " VARCHAR, " + DatabaseTable.COL_BLWIFI_W09 + " VARCHAR, " + DatabaseTable.COL_BLWIFI_W10 + " VARCHAR, " + DatabaseTable.COL_BLWIFI_W11 + " VARCHAR, " + DatabaseTable.COL_BLWIFI_W12 + " VARCHAR, " + DatabaseTable.COL_BLWIFI_CREATEDON + " VARCHAR, " + DatabaseTable.COL_BLWIFI_MODIFIEDON + " VARCHAR )";
//        Log.e("BLDiagnostics", BLWiFi);
//        //db.execSQL(BLWiFi);

//        db.execSQL("Vacuum");
//        Utility.Log(TAG, "After Alter Function Run");
    }

    public void vacuum() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("Vacuum");
        Utility.Log("TIME", "Vacuum Function Called SuccessFully..!!");
    }

    public int rackTableCheckModelNo(String displayColumn, String column1, String value1, String column2, String value2, String column3, String value3, String column4, String value4, String column5, String value5) {
        String strQuery = "Select " + displayColumn + " from " + DatabaseTable.TBL_BLRACKSETUP + " where " + column1 + " = " + "'" + value1 + "'";
        if (!column2.equalsIgnoreCase("")) {
            strQuery = strQuery + " and " + column2 + " = " + "'" + value2 + "' ";
        }
        if (!column3.equalsIgnoreCase("")) {
            strQuery = strQuery + " and " + column3 + " = " + "'" + value3 + "'";
        }

        if (value4 != null && !value4.equalsIgnoreCase("")) {
            strQuery = strQuery + " and " + column4 + " = " + "'" + value4 + "'";
        } else if (value4 != null && value4.equalsIgnoreCase("")) {
            strQuery = strQuery + " and " + column4 + " is NULL ";
        }

        if (value5 != null && !value5.equalsIgnoreCase("")) {
            strQuery = strQuery + " and " + column5 + " = " + "'" + value5 + "'";
        }
//        if(column2 != null){
//            strQuery = strQuery + "and ";
//        }
        Log.e("strQuery", strQuery);
        Cursor BLRackSetUp = getQueryResult(strQuery);
        Utility.Log("Model No Count", "count" + BLRackSetUp.getCount());
        return BLRackSetUp.getCount();
    }

    public int newrackTableCheckModelNo(String displayColumn, String column1, String value1, String column2, String value2, String column3, String value3, String column4, String value4, String column5, String value5) {
        String strQuery = "Select " + displayColumn + " from " + DatabaseTable.TBL_BLSPPBCURACKSETUP + " where " + column1 + " = " + "'" + value1 + "'";
        if (!column2.equalsIgnoreCase("")) {
            strQuery = strQuery + " and " + column2 + " = " + "'" + value2 + "' ";
        }
        if (!column3.equalsIgnoreCase("")) {
            strQuery = strQuery + " and " + column3 + " = " + "'" + value3 + "'";
        }

        if (value4 != null && !value4.equalsIgnoreCase("")) {
            strQuery = strQuery + " and " + column4 + " = " + "'" + value4 + "'";
        } else if (value4 != null && value4.equalsIgnoreCase("")) {
            strQuery = strQuery + " and " + column4 + " is NULL ";
        }

        if (value5 != null && !value5.equalsIgnoreCase("")) {
            strQuery = strQuery + " and " + column5 + " = " + "'" + value5 + "'";
        }
//        if(column2 != null){
//            strQuery = strQuery + "and ";
//        }
        Log.e("strQuery", strQuery);
        Cursor BLRackSetUp = getQueryResult(strQuery);
        Utility.Log("Model No Count", "count" + BLRackSetUp.getCount());
        return BLRackSetUp.getCount();
    }

    public int isSetupCompletedCheckFromDataBase() {
        String strQuery = "Select * FROM " + DatabaseTable.TBL_BLRACKSETUP + " where " + DatabaseTable.COL_BLRACKSETUP_ISSETUPCOMPLETED + " = 1";
        Log.e("strQuery", strQuery);
        Cursor isSetUpCompleted = getQueryResult(strQuery);
        Utility.Log("Count isSetUpCompleted", "count" + isSetUpCompleted.getCount());
        return isSetUpCompleted.getCount();
    }

    public ArrayList<RackSetupModel> getAllDataFromRackSetUpTableNew() {
        ArrayList<RackSetupModel> arrayList = new ArrayList<>();
        String strQuery = "Select * FROM " + DatabaseTable.TBL_BLSPPBCURACKSETUP;
        Log.e("strQuery", strQuery);
        Cursor cursor = getQueryResult(strQuery);
        Utility.Log("Count TBL_BLSPPBCURACKSETUP", "count" + cursor.getCount());

        try {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    RackSetupModel rackModel = new RackSetupModel();
                    rackModel.setACsetpt(Float.parseFloat(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSPPBCURACKSETUP_ACSETPT))));
                    rackModel.setDCsetpt(Float.parseFloat(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSPPBCURACKSETUP_DCSETPT))));
                    rackModel.setBlowerName(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSPPBCURACKSETUP_BLOWER_NAME)));
                    rackModel.setModelNo(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSPPBCURACKSETUP_MODEL_NO)));


                    rackModel.setCompanyName(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSPPBCURACKSETUP_COMPANY_NAME)));

                    rackModel.setBuildingName(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSPPBCURACKSETUP_BUILDING_NAME)));
                    rackModel.setRoomName(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSPPBCURACKSETUP_ROOM_NAME)));


                    rackModel.setIsSetupCompleted(cursor.getInt(cursor.getColumnIndex(DatabaseTable.COL_BLSPPBCURACKSETUP_ISSETUPCOMPLETED)));
                    rackModel.setSpc1(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSPPBCURACKSETUP_SPC1)));
                    rackModel.setSpc2(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSPPBCURACKSETUP_SPC2)));
                    rackModel.setSpc3(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSPPBCURACKSETUP_SPC3)));
                    rackModel.setSpc4(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSPPBCURACKSETUP_SPC4)));
                    rackModel.setSetupCompletedDateTime(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSPPBCURACKSETUP_SETUPCOMPLETED_DATETIME)));
                    rackModel.setSetupModifiedDateTime(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSPPBCURACKSETUP_SETUPMODIFIED_DATETIME)));
                    rackModel.setCreatedOn(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSPPBCURACKSETUP_CREATED_ON)));

                    arrayList.add(rackModel);
                }
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        return arrayList;
    }

    public ArrayList<RackSetupModel> getDataFromRackSetUpTableNew() {
        ArrayList<RackSetupModel> arrayList = new ArrayList<>();
        String strQuery = "Select * FROM " + DatabaseTable.TBL_BLSPPBCURACKSETUP + " where " + DatabaseTable.COL_BLRACKSETUP_ISSETUPCOMPLETED + " = 1";
        Log.e("strQuery", strQuery);
        Cursor cursor = getQueryResult(strQuery);
        Utility.Log("Count TBL_BLSPPBCURACKSETUP", "count" + cursor.getCount());

        try {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    RackSetupModel rackModel = new RackSetupModel();
                    rackModel.setACsetpt(Float.parseFloat(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSPPBCURACKSETUP_ACSETPT))));
                    rackModel.setDCsetpt(Float.parseFloat(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSPPBCURACKSETUP_DCSETPT))));
                    rackModel.setBlowerName(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSPPBCURACKSETUP_BLOWER_NAME)));
                    rackModel.setModelNo(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSPPBCURACKSETUP_MODEL_NO)));


                    rackModel.setCompanyName(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSPPBCURACKSETUP_COMPANY_NAME)));

                    rackModel.setBuildingName(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSPPBCURACKSETUP_BUILDING_NAME)));
                    rackModel.setRoomName(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSPPBCURACKSETUP_ROOM_NAME)));


                    rackModel.setIsSetupCompleted(cursor.getInt(cursor.getColumnIndex(DatabaseTable.COL_BLSPPBCURACKSETUP_ISSETUPCOMPLETED)));
                    rackModel.setSpc1(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSPPBCURACKSETUP_SPC1)));
                    rackModel.setSpc2(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSPPBCURACKSETUP_SPC2)));
                    rackModel.setSpc3(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSPPBCURACKSETUP_SPC3)));
                    rackModel.setSpc4(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSPPBCURACKSETUP_SPC4)));
                    rackModel.setSetupCompletedDateTime(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSPPBCURACKSETUP_SETUPCOMPLETED_DATETIME)));
                    rackModel.setSetupModifiedDateTime(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSPPBCURACKSETUP_SETUPMODIFIED_DATETIME)));
                    rackModel.setCreatedOn(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSPPBCURACKSETUP_CREATED_ON)));
                    Log.i(TAG, "ACSetpt : "+rackModel.getACsetpt());
                    Log.i(TAG,"DCSetpt : "+rackModel.getDCsetpt());
                    arrayList.add(rackModel);
                }
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        return arrayList;
    }

    public ArrayList<RackModel> getDataFromRackSetUpTable() {
        ArrayList<RackModel> arrayList = new ArrayList<>();
        String strQuery = "Select * FROM " + DatabaseTable.TBL_BLSPPBCURACKSETUP + " where " + DatabaseTable.COL_BLSPPBCURACKSETUP_ISSETUPCOMPLETED + " = 1";
        Log.e("strQuery", strQuery);
        Cursor cursor = getQueryResult(strQuery);
        Utility.Log("Count isSetUpCompleted", "count" + cursor.getCount());

        try {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    RackModel rackModel = new RackModel();
                    rackModel.setCompanyName(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_COMPANY_NAME)));
                    rackModel.setBlowerName(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_BLOWER_NAME)));
                    rackModel.setBuildingName(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_BUILDING_NAME)));
                    rackModel.setRoomName(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_ROOM_NAME)));
                    rackModel.setModelNo(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_MODEL_NO)));
                    rackModel.setACH(cursor.getInt(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_ACH)));
                    rackModel.setPolarity(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_POLARITY)));
                    rackModel.setSupplyCFM(cursor.getInt(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_SUPPLYCFM)));
                    rackModel.setExhaustWC(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_EXHAUSTWC)));
                    rackModel.setExhaustCFM(cursor.getInt(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_EXHAUSTCFM)));
                    rackModel.setIsSetupCompleted(cursor.getInt(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_ISSETUPCOMPLETED)));
                    rackModel.setSpc1(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_SPC1)));
                    rackModel.setSpc2(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_SPC2)));
                    rackModel.setSpc3(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_SPC3)));
                    rackModel.setSpc4(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_SPC4)));
                    rackModel.setSetupCompletedDateTime(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_SETUPCOMPLETED_DATETIME)));
                    rackModel.setSetupModifiedDateTime(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_SETUPMODIFIED_DATETIME)));
                    rackModel.setCreatedOn(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_CREATED_ON)));
                    rackModel.setWithLock(cursor.getInt(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_WITH_WITHOUT_LOCK)));
                    arrayList.add(rackModel);
                }
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        return arrayList;
    }

    public RackDetailsModel getDataFromRackBlowerDetails() {
        RackDetailsModel rackDetailsModel = null;
        String strQuery = "Select * FROM " + DatabaseTable.TBL_RACKBLOWERDETAILS;
        Log.e("strQuery", strQuery);
        Cursor cursor = getQueryResult(strQuery);
        Utility.Log("Count isSetUpCompleted", "count" + cursor.getCount());

        try {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    rackDetailsModel = new RackDetailsModel();
                    rackDetailsModel.setmId(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_ID)));
                    rackDetailsModel.setmRackBlowerCustomerID(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_RACKBLOWERCUSTOMER_ID)));
                    rackDetailsModel.setmABlowerSerial(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_ABLOWER_SERIAL)));
                    rackDetailsModel.setmABlowerWiFiMAC(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_ABLOWER_WIFI_MAC)));
                    rackDetailsModel.setmABlowerLANMAC(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_ABLOWER_LAN_MAC)));
                    rackDetailsModel.setmABlowerBluetoothMAC(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_ABLOWER_BLUETOOTHMAC)));
                    rackDetailsModel.setmABlowerLanIPAddress(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_ABLOWER_LANIP_ADDRESS)));
                    rackDetailsModel.setmABlowerWiFiIPAddress(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_ABLOWER_WiFiIP_ADDRESS)));
                    rackDetailsModel.setmABlowerName(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_ABLOWER_NAME)));
                    rackDetailsModel.setmABlowerBuilding(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_ABLOWER_BUILDING)));
                    rackDetailsModel.setmABlowerRoom(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_ABLOWER_ROOM)));
                    rackDetailsModel.setmRackModel(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_RACK_MODEL)));
                    rackDetailsModel.setmRackSerial(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_RACK_SERAIL)));
                    rackDetailsModel.setmBlowerModel(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_BLOWER_MODEL)));
                    rackDetailsModel.setmSupplyBlowerSerial(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_SUPPLY_BLOWER_SERIAL)));
                    rackDetailsModel.setmExhaustBlowerSerial(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_EXHAUST_BLOWER_SERIAL)));
                    rackDetailsModel.setmIsCommModuleExist(cursor.getInt(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_IS_COMMMODULE_EXIST)));
                    rackDetailsModel.setmAWiViWiFiMAC(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_AWIVI_WIFI_MAC)));
                    rackDetailsModel.setmAWiViIPAddress(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_AWIVI_IP_ADDRESS)));
                    rackDetailsModel.setmAWiViBluetoothMAC(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_AWIVI_BLUETOOTHMAC)));
                    rackDetailsModel.setmIsUpdateCMD(cursor.getInt(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_IS_UPDATE_CMD)));
                    rackDetailsModel.setmUpdateCMD(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_UPDATE_CMD)));
                    rackDetailsModel.setmIsUpdateCMDCompleted(cursor.getInt(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_IS_UPDATE_CMD_COMPLETED)));
                    rackDetailsModel.setmUseAlnEmailService(cursor.getInt(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_USE_ALN_EMAIL_SERVICE)));
                    rackDetailsModel.setmAlertEmailIDs(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_ALERT_EMAIL_IDS)));
                    rackDetailsModel.setmReportEmailIDs(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_REPORT_EMAIL_IDS)));
                    rackDetailsModel.setmIsRegAlarmOn(cursor.getInt(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_IS_REG_ALARM_ON)));
                    rackDetailsModel.setmLastAlarm(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_LAST_ALARM)));
                    rackDetailsModel.setmIsTmpHMDAlarmOn(cursor.getInt(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_IS_TMP_HMD_ALARM_ON)));
                    rackDetailsModel.setmLasTmpHMDtAlarm(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_LAST_TMP_HMDT_ALARM)));
                    rackDetailsModel.setmTempUnit(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_TEMP_UNIT)));
                    rackDetailsModel.setmPressureUnit(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_PRESSURE_UNIT)));
                    rackDetailsModel.setmAirFlowUnit(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_AIR_FLOW_UNIT)));
                    rackDetailsModel.setmLastSyncDateTime(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_LAST_SYNC_DATE_TIME)));
                    rackDetailsModel.setmModifiedOn(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_MODIFIED_ON)));
                    rackDetailsModel.setmModifiedBy(cursor.getInt(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_MODIFIED_BY)));
                    rackDetailsModel.setmCreatedOn(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_CREATED_ON)));
                    rackDetailsModel.setmCreatedBy(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_CREATED_BY)));
                    rackDetailsModel.setmIsDeleted(cursor.getInt(cursor.getColumnIndex(DatabaseTable.COL_RACKBLOWERDETAILS_IS_DELETED)));
                }
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        return rackDetailsModel;
    }

    public ArrayList<SpinnerObject> getAllACHValueFromDatabase(String strModelNo) {
        ArrayList<SpinnerObject> arrayListACH = new ArrayList<>();
        String strQuery = "Select distinct(ACH) FROM " + DatabaseTable.TBL_BLRACKSETUP + " where " + DatabaseTable.COL_BLRACKSETUP_MODEL_NO + " = " + "'" + strModelNo + "'";
//        String strQuery ="SELECT ACH,count(ACH) as totlenumberof FROM BLRackSetup WHERE ModelNo = '0JV042' GROUP BY ACH";
        Cursor cursor = getQueryResult(strQuery);

        try {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    SpinnerObject spinnerObject = new SpinnerObject();
                    spinnerObject.setName(String.valueOf(cursor.getInt(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_ACH))));
                    arrayListACH.add(spinnerObject);
                }
//                SpinnerObject object = new SpinnerObject();
//                object.setIntValue("Choose ACH value");
//                arrayListACH.add(0, object);
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        return arrayListACH;
    }

    public ArrayList<SpinnerObject> getAllPolarityValueFromDatabase(String strModelNo, String spinnerACHValue) {
        ArrayList<SpinnerObject> arrayListPolarity = new ArrayList<>();
        String strQuery = "Select distinct(Polarity) FROM " + DatabaseTable.TBL_BLRACKSETUP + " where " + DatabaseTable.COL_BLRACKSETUP_MODEL_NO + " = " + "'" + strModelNo + "'" + " and " + DatabaseTable.COL_BLRACKSETUP_ACH + " = " + "'" + spinnerACHValue + "'";
//        String strQuery ="SELECT Polarity,count(Polarity) as totlenumberof FROM BLRackSetup WHERE ModelNo = '0JV042' GROUP BY Polarity";
        Log.e("PolarityQuery", strQuery);
        Cursor cursor = getQueryResult(strQuery);

        try {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    SpinnerObject spinnerObject = new SpinnerObject();
                    if (cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_POLARITY)).equalsIgnoreCase("+")) {
                        spinnerObject.setId("1");
                    } else {
                        spinnerObject.setId("0");
                    }
                    spinnerObject.setName(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_POLARITY)));
                    arrayListPolarity.add(spinnerObject);
                }
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        return arrayListPolarity;
    }

    public ArrayList<SpinnerObject> getAllSupplyValueFromDatabase(String strModelNo, String spinnerACHValue, String spinnerPolarityValue, String lock) {
        ArrayList<SpinnerObject> arrayListSupply = new ArrayList<>();
        String strQuery = "";
        if (lock == null) {
            strQuery = "Select distinct(SupplyCFM) FROM " + DatabaseTable.TBL_BLRACKSETUP + " where "
                    + DatabaseTable.COL_BLRACKSETUP_MODEL_NO + " = " + "'" + strModelNo + "'" + " and "
                    + DatabaseTable.COL_BLRACKSETUP_ACH + " = " + "'" + spinnerACHValue + "'" + " and "
                    + DatabaseTable.COL_BLRACKSETUP_POLARITY + " = " + "'" + spinnerPolarityValue + "'";
        } else {
            strQuery = "Select distinct(SupplyCFM) FROM " + DatabaseTable.TBL_BLRACKSETUP + " where "
                    + DatabaseTable.COL_BLRACKSETUP_MODEL_NO + " = " + "'" + strModelNo + "'" + " and "
                    + DatabaseTable.COL_BLRACKSETUP_ACH + " = " + "'" + spinnerACHValue + "'" + " and "
                    + DatabaseTable.COL_BLRACKSETUP_POLARITY + " = " + "'" + spinnerPolarityValue + "'" + " and "
                    + DatabaseTable.COL_BLRACKSETUP_WITH_WITHOUT_LOCK + " = " + "'" + lock + "'";
        }

        Log.e("query", strQuery);
//        String strQuery ="SELECT Polarity,count(Polarity) as totlenumberof FROM BLRackSetup WHERE ModelNo = '0JV042' GROUP BY Polarity";
        Cursor cursor = getQueryResult(strQuery);

        try {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    SpinnerObject spinnerObject = new SpinnerObject();
                    spinnerObject.setName(String.valueOf(cursor.getInt(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_SUPPLYCFM))));
                    arrayListSupply.add(spinnerObject);
                }
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        return arrayListSupply;
    }

    public ArrayList<SpinnerObject> getAllExhaustValueFromDatabase(String strModelNo, String spinnerACHValue, String spinnerPolarityValue, String spinnerSupplyValue, String lock) {
        ArrayList<SpinnerObject> arrayListExhaust = new ArrayList<>();
        String strQuery1 = "";
        if (spinnerSupplyValue.equalsIgnoreCase("0") || spinnerSupplyValue.equalsIgnoreCase("")) {
            strQuery1 = "Select distinct(ExhaustWC) FROM " + DatabaseTable.TBL_BLRACKSETUP + " where "
                    + DatabaseTable.COL_BLRACKSETUP_MODEL_NO + " = " + "'" + strModelNo + "'" + " and "
                    + DatabaseTable.COL_BLRACKSETUP_ACH + " = " + "'" + spinnerACHValue + "'" + " and "
                    + DatabaseTable.COL_BLRACKSETUP_POLARITY + " = " + "'" + spinnerPolarityValue + "'" + " and "
                    + DatabaseTable.COL_BLRACKSETUP_SUPPLYCFM + " is NULL";
//            Select distinct(ExhaustWC) FROM BLRackSetup where ModelNo = 'NGB048' and ACH = '40' AND Polarity = '-'  AND SupplyCFM is NULL
        } else {
            if (lock != null) {
                strQuery1 = "Select distinct(ExhaustWC) FROM " + DatabaseTable.TBL_BLRACKSETUP + " where "
                        + DatabaseTable.COL_BLRACKSETUP_MODEL_NO + " = " + "'" + strModelNo + "'" + " and "
                        + DatabaseTable.COL_BLRACKSETUP_ACH + " = " + "'" + spinnerACHValue + "'" + " and "
                        + DatabaseTable.COL_BLRACKSETUP_POLARITY + " = " + "'" + spinnerPolarityValue + "'" + " and "
                        + DatabaseTable.COL_BLRACKSETUP_SUPPLYCFM + " = " + "'" + spinnerSupplyValue + "'" + " and "
                        + DatabaseTable.COL_BLRACKSETUP_WITH_WITHOUT_LOCK + " = " + "'" + lock + "'";
            } else {
                strQuery1 = "Select distinct(ExhaustWC) FROM " + DatabaseTable.TBL_BLRACKSETUP + " where "
                        + DatabaseTable.COL_BLRACKSETUP_MODEL_NO + " = " + "'" + strModelNo + "'" + " and "
                        + DatabaseTable.COL_BLRACKSETUP_ACH + " = " + "'" + spinnerACHValue + "'" + " and "
                        + DatabaseTable.COL_BLRACKSETUP_POLARITY + " = " + "'" + spinnerPolarityValue + "'" + " and "
                        + DatabaseTable.COL_BLRACKSETUP_SUPPLYCFM + " = " + "'" + spinnerSupplyValue + "'";
            }

        }

        Log.e("query", strQuery1);
//        String strQuery ="SELECT Polarity,count(Polarity) as totlenumberof FROM BLRackSetup WHERE ModelNo = '0JV042' GROUP BY Polarity";
        Cursor cursor1 = getQueryResult(strQuery1);
        arrayListExhaust.clear();
        try {
            if (cursor1.getCount() > 0) {
                while (cursor1.moveToNext()) {
                    SpinnerObject spinnerObject = new SpinnerObject();
//                    if (cursor1.getString(cursor1.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_EXHAUSTWC)).equalsIgnoreCase("")){
//                        spinnerObject.setName("Edit Value");
//                    }else {
                    spinnerObject.setName(cursor1.getString(cursor1.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_EXHAUSTWC)));
//                    }

                    arrayListExhaust.add(spinnerObject);
                }
            }
            cursor1.close();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        return arrayListExhaust;
    }

    public Cursor getQueryResult(String strQuery) {
        if (db == null || !db.isOpen())
            db = getWritableDatabase();
//        MyUtils.Log(strQuery + " : " + (db == null) + " : " + db.isOpen());
        return db.rawQuery(strQuery, null);
    }

    public int getQueryResultCount(String strQuery) {
        int nCount = 0;
        if (db == null || !db.isOpen())
            db = getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery(strQuery, null);
            nCount = cursor.getCount();
            cursor.close();
        } catch (SQLiteException e) {
            Log.e("SQLiteException ==> ", "" + e.getMessage());
        }
        return nCount;
    }

    public void insert(String tabName, String colName, ContentValues cv) {
        if (db == null || !db.isOpen())
            db = getWritableDatabase();
        db.insert(tabName, colName, cv);
        //closeDB();
    }

    public void update(String tabName, ContentValues cv, String whereCondition) {
        if (db == null || !db.isOpen())
            db = getWritableDatabase();
        db.update(tabName, cv, whereCondition, null);
//        closeDB();
    }

    public void updateValueIntoDatabase(String modelNumber, String setUpCompletedDateTime, String setUpModifiedDateTime, String createdON, int setUpCompletedValue,
                                        String mStrCompanyName, String mStrBlowerName, String mStrBuildingName,
                                        String mStrRoomName, String mStrIpAddress, String mAcSetpt, String mDcSetpt) {
        String updateQuery = "";

            updateQuery = "UPDATE " + DatabaseTable.TBL_BLSPPBCURACKSETUP + " SET " +
//                    DatabaseTable.COL_BLRACKSETUP_SUPPLYCFM + " = " + "'" + strSupplyValue + "'" + ", " +
//                    DatabaseTable.COL_BLRACKSETUP_EXHAUSTWC + " = " + "'" + strExhaustValue + "'" + ", " +
                    DatabaseTable.COL_BLSPPBCURACKSETUP_ISSETUPCOMPLETED + " = " + "'" + setUpCompletedValue + "'" + ", " +
                    DatabaseTable.COL_BLSPPBCURACKSETUP_SETUPCOMPLETED_DATETIME + " = " + "'" + setUpCompletedDateTime + "'" + ", " +
                    DatabaseTable.COL_BLSPPBCURACKSETUP_COMPANY_NAME + " = " + "'" + mStrCompanyName + "'" + ", " +
                    DatabaseTable.COL_BLSPPBCURACKSETUP_BLOWER_NAME + " = " + "'" + mStrBlowerName + "'" + ", " +
                    DatabaseTable.COL_BLSPPBCURACKSETUP_BUILDING_NAME + " = " + "'" + mStrBuildingName + "'" + ", " +
                    DatabaseTable.COL_BLSPPBCURACKSETUP_ROOM_NAME + " = " + "'" + mStrRoomName + "'" + ", " +
                    DatabaseTable.COL_BLSPPBCURACKSETUP_IP_ADDRESS + " = " + "'" + mStrIpAddress + "'" + ", " +

                    DatabaseTable.COL_BLSPPBCURACKSETUP_ACSETPT + " = " + "'" + mAcSetpt + "'" + ", " +
                    DatabaseTable.COL_BLSPPBCURACKSETUP_DCSETPT + " = " + "'" + mDcSetpt + "'" + ", " +

                    DatabaseTable.COL_BLSPPBCURACKSETUP_CREATED_ON + " = " + "'" + createdON + "'" + " where " +
                    DatabaseTable.COL_BLSPPBCURACKSETUP_MODEL_NO + " = " + "'" + modelNumber + "'";

        try {
//            String updateQuery = "UPDATE BLRackSetup SET ExhaustWC='-0.011' ,IsSetupcompleted ='0' ,SetupCompletedDateTime = '', CreatedOn = '' WHERE ModelNo = '0JV042' AND ACH = '50' AND Polarity = '+' ";
            Log.e("query", updateQuery);
            db.execSQL(updateQuery);
            Log.e("Updated : - ", "Updated SPPBCU2RackSetUpTable Succesfully");
        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }

    }

    public void saveNewIDForRackBlowerInDataBase(JSONObject jsonObject) {
        if (jsonObject.length() > 0) {
            db.beginTransaction();
            try {
//                for (int i = 0; i < arrList.length(); i++) {
//                    JSONObject jObj = arrList.getJSONObject(i);

                ContentValues cv = new ContentValues();
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_ID, getString(jsonObject, ApiHandler.strRackSerialNumberId));
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_RACKBLOWERCUSTOMER_ID, getString(jsonObject, ApiHandler.strRackBlowerCustomerID));
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_ABLOWER_SERIAL, getString(jsonObject, ApiHandler.strRackBlowerABlowerSerial));

//                public static String strUpdateABlowerWifiMac = "ABlowerWiFiMAC";
//                public static String strUpdateABlowerLANMac = "ABlowerLANMAC";
//                public static String strUpdateABlowerBluetoothMac = "ABlowerBluetoothMAC";
//                public static String strUpdateABlowerLANIPAddress = "ABlowerLANIPAddress";
//                public static String strUpdateABlowerWiFiIPAddress = "ABlowerWiFiIPAddress";
//
//                public static final String COL_RACKBLOWERDETAILS_ABLOWER_WIFI_MAC = "ABlowerWiFiMAC";
//                public static final String COL_RACKBLOWERDETAILS_ABLOWER_LAN_MAC = "ABlowerLANMAC";
//                public static final String COL_RACKBLOWERDETAILS_ABLOWER_BLUETOOTHMAC = "ABlowerBluetoothMAC";
//                public static final String COL_RACKBLOWERDETAILS_ABLOWER_WiFiIP_ADDRESS = "ABlowerWiFiIPAddress";
//                public static final String COL_RACKBLOWERDETAILS_ABLOWER_LANIP_ADDRESS = "ABlowerLANIPAddress";

                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_ABLOWER_WIFI_MAC, getString(jsonObject, ApiHandler.strUpdateABlowerWifiMac));
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_ABLOWER_LAN_MAC, getString(jsonObject, ApiHandler.strUpdateABlowerLANMac));
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_ABLOWER_BLUETOOTHMAC, getString(jsonObject, ApiHandler.strUpdateABlowerBluetoothMac));
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_ABLOWER_LANIP_ADDRESS, getString(jsonObject, ApiHandler.strUpdateABlowerLANIPAddress));
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_ABLOWER_WiFiIP_ADDRESS, getString(jsonObject, ApiHandler.strUpdateABlowerWiFiIPAddress));

                if (getQueryResultCount("select * from " + DatabaseTable.TBL_RACKBLOWERDETAILS) <= 0) {
                    db.insert(DatabaseTable.TBL_RACKBLOWERDETAILS, null, cv);
                    Utility.Log("inserted Successfully");
                    cv.clear();
                } else {
                    db.update(DatabaseTable.TBL_RACKBLOWERDETAILS, cv, DatabaseTable.COL_RACKBLOWERDETAILS_ID + " = " + getInt(jsonObject, ApiHandler.strRackSerialNumberId), null);
                    cv.clear();
                }
//                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                Log.e("jsonError", "" + e.getMessage());
            } finally {
                db.endTransaction();
            }
        }
    }

    public void updateRackBlowerDetailsInDataBase(JSONObject jsonObject) {
        if (jsonObject.length() > 0) {
            db.beginTransaction();
            try {
                ContentValues cv = new ContentValues();
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_ID, getString(jsonObject, ApiHandler.strUpdateRackBlowerDetailsID));
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_RACKBLOWERCUSTOMER_ID, getString(jsonObject, ApiHandler.strUpdateRackCustomerID));
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_ABLOWER_SERIAL, getString(jsonObject, ApiHandler.strUpdateABlowerSerial));
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_ABLOWER_WIFI_MAC, getString(jsonObject, ApiHandler.strUpdateABlowerWifiMac));
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_ABLOWER_LAN_MAC, getString(jsonObject, ApiHandler.strUpdateABlowerLANMac));
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_ABLOWER_BLUETOOTHMAC, getString(jsonObject, ApiHandler.strUpdateABlowerBluetoothMac));
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_ABLOWER_LANIP_ADDRESS, getString(jsonObject, ApiHandler.strUpdateABlowerLANIPAddress));
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_ABLOWER_WiFiIP_ADDRESS, getString(jsonObject, ApiHandler.strUpdateABlowerWiFiIPAddress));
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_ABLOWER_NAME, getString(jsonObject, ApiHandler.strUpdateABlowerName));
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_ABLOWER_BUILDING, getString(jsonObject, ApiHandler.strUpdateABlowerBuilding));
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_ABLOWER_ROOM, getString(jsonObject, ApiHandler.strUpdateABlowerRoom));
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_RACK_MODEL, getString(jsonObject, ApiHandler.strUpdateRackModel));
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_RACK_SERAIL, getString(jsonObject, ApiHandler.strUpdateRackSerial));
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_BLOWER_MODEL, getString(jsonObject, ApiHandler.strUpdateBlowerModel));
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_SUPPLY_BLOWER_SERIAL, getString(jsonObject, ApiHandler.strUpdateSupplyBlowerSerial));
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_EXHAUST_BLOWER_SERIAL, getString(jsonObject, ApiHandler.strUpdateExhaustBlowerSerial));
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_IS_COMMMODULE_EXIST, getString(jsonObject, ApiHandler.strUpdateIsCommoduleExit) == "true" ? 1 : 0);
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_AWIVI_WIFI_MAC, getString(jsonObject, ApiHandler.strUpdateAwiviWifiMac));
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_AWIVI_IP_ADDRESS, getString(jsonObject, ApiHandler.strUpdateAwiviWifiIpAddress));
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_AWIVI_BLUETOOTHMAC, getString(jsonObject, ApiHandler.strUpdateAwiviBluetoothMac));
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_IS_UPDATE_CMD, getString(jsonObject, ApiHandler.strUpdateIsUpdateCmd) == "true" ? 1 : 0);
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_UPDATE_CMD,getString(jsonObject, ApiHandler.strUpdateCmd));
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_IS_UPDATE_CMD_COMPLETED, getString(jsonObject, ApiHandler.strUpdateIsUpdateCmdCompleted) == "true" ? 1 : 0);
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_USE_ALN_EMAIL_SERVICE, getString(jsonObject, ApiHandler.strUpdateUseAlnEmailService) == "true" ? 1 : 0);
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_ALERT_EMAIL_IDS, getString(jsonObject, ApiHandler.strUpdateAlertEmailIds));
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_REPORT_EMAIL_IDS, getString(jsonObject, ApiHandler.strUpdateReportEmailIds));
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_IS_REG_ALARM_ON, getString(jsonObject, ApiHandler.strUpdateIsRegAlarmOn) == "true" ? 1 : 0);
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_LAST_ALARM, getString(jsonObject, ApiHandler.strUpdateLastAlarm));
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_IS_TMP_HMD_ALARM_ON, getString(jsonObject, ApiHandler.strUpdateIsTmpHMDAlarmOn) == "true" ? 1 : 0);
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_LAST_TMP_HMDT_ALARM, getString(jsonObject, ApiHandler.strUpdateLasTmpHMDtAlarm));
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_TEMP_UNIT, getString(jsonObject, ApiHandler.strUpdateTempUnit));
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_PRESSURE_UNIT, getString(jsonObject, ApiHandler.strUpdatePressureUnit));
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_AIR_FLOW_UNIT, getString(jsonObject, ApiHandler.strUpdateAirFlowUnit));
                cv.put(DatabaseTable.COL_RACKBLOWERDETAILS_IS_UPDATED_BY_WEB_APP, getString(jsonObject, ApiHandler.strUpdateIsUpdatedByWebApp) == "true" ? 1 : 0);

//                db.update(DatabaseTable.TBL_RACKBLOWERDETAILS, cv, DatabaseTable.COL_RACKBLOWERDETAILS_ID + " = " + "'" + getString(jsonObject, ApiHandler.strUpdateRackBlowerDetailsID)+ "'" + " and "+
//                        DatabaseTable.COL_RACKBLOWERDETAILS_RACKBLOWERCUSTOMER_ID + " = " + "'" + getString(jsonObject, ApiHandler.strUpdateRackCustomerID) + "'", null);
                db.update(DatabaseTable.TBL_RACKBLOWERDETAILS, cv, null, null);
                Utility.Log("Update Successfully");
                cv.clear();
                db.setTransactionSuccessful();
            } catch (Exception e) {
                Log.e("jsonError", "" + e.getMessage());
            } finally {
                db.endTransaction();
            }
        }
    }

    public static int getInt(JSONObject jObj, String strKey) {
        try {
            return (jObj.has(strKey) && !jObj.isNull(strKey)) ? jObj.getInt(strKey) : 0;
        } catch (JSONException e) {
            return 0;
        }
    }

    public static String getString(JSONObject jObj, String strKey) {
        try {
            return (jObj.has(strKey) && !jObj.isNull(strKey)) ? jObj.getString(strKey) : "";
        } catch (JSONException e) {
            return "";
        }
    }


    public void exportDatabase() {
        File dbFile = new File(Environment.getDataDirectory() + "/data/" + mcontext.getPackageName() + "/databases/" + DATABASE_NAME);

        File exportDir = new File(Environment.getExternalStorageDirectory() + "/Ringtones", "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        File file = new File(exportDir, dbFile.getName());

        try {
            file.createNewFile();
            this.copyFile(dbFile, file);
        } catch (Exception e) {
            Utility.Log("mypck", e.getMessage());
            e.printStackTrace();
        }
    }

    void copyFile(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }

    @SuppressLint("LongLogTag")
    public void deleteRecordOlderThan2Month() {
//        delete from BLFeedback where ModifiedOn <= date('now', '-2 month') AND isSynced = 1
        db = getWritableDatabase();
        String sqldeleteFeedBackRecords = "DELETE FROM " + DatabaseTable.TBL_BLFEEDBACK + " WHERE " + DatabaseTable.COL_BLFEED_MODIFIEDON + " <= date('now','-3 month')" + " AND " + DatabaseTable.COL_BLFEED_ISSYNCED + " = 0";
        Log.e("DeleteRecordsQuery(F) :- ", sqldeleteFeedBackRecords);
        db.execSQL(sqldeleteFeedBackRecords);

        String sqldeleteSetPointRecords = "DELETE FROM " + DatabaseTable.TBL_BLSETPOINT + " WHERE " + DatabaseTable.COL_BLSET_MODIFIEDON + " <= date('now','-3 month')" + " AND " + DatabaseTable.COL_BLSET_ISSYNCED + " = 0";
        Log.e("DeleteRecordsQuery(S) :- ", sqldeleteSetPointRecords);
        db.execSQL(sqldeleteSetPointRecords);
    }

    public void deleteAllRecordFromAllTable(boolean isFromChangeUnit) {

        db = getWritableDatabase();
        db.execSQL("delete from " + DatabaseTable.TBL_BLFEEDBACK);
        db.execSQL("delete from " + DatabaseTable.TBL_BLSETPOINT);

        if (isFromChangeUnit) {
            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nCallUnitChangeObserver);
        }
    }

    public void setIsSetUpCompletedColoumn() {
        db = getWritableDatabase();
        String updateRackSetupTable = "UPDATE " + DatabaseTable.TBL_BLSPPBCURACKSETUP + " SET " + DatabaseTable.COL_BLSPPBCURACKSETUP_ISSETUPCOMPLETED + " = " + "'" + "0" + "'";
        Log.e("strQuery",updateRackSetupTable);
        db.execSQL("UPDATE " + DatabaseTable.TBL_BLSPPBCURACKSETUP + " SET " + DatabaseTable.COL_BLSPPBCURACKSETUP_ISSETUPCOMPLETED + " = " + "'" + "0" + "'");
    }

    public void getUpdateRackBlowerDetails_Api(Activity act, PrefManager prefManager, AllentownBlowerApplication allentownBlowerApplication, RackDetailsModel model) {
        if (prefManager.getHostName() == null || !prefManager.getHostName().contains("http")){
            Log.e("HostName :- ", "Host Name is Not Available");
            return;
        }
        //Utility.ShowMessageReport(act, "Please wait...");
        //Log.e("TAG","getUpdateRackBlowerDetails_Api method");
        // if (NetworkUtil.getConnectivityStatus(act)) {
        JSONObject objParam = new JSONObject();
        try {
            objParam.put(ApiHandler.strUpdateRackBlowerDetailsId, model.getmId());
            objParam.put(ApiHandler.strUpdateRackBlowerCustomerID, model.getmRackBlowerCustomerID());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                prefManager.getHostName() + ApiHandler.strUrlGetUpdateRackBlowerDetails, objParam,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        //Utility.dismissProgress();
                        //Utility.dismissAlertDialog();
                        Utility.Log("getUpdateRackBlowerDetails_Api_Response : " + jsonObject);
                        try {
                            if (jsonObject.getBoolean("result")) {
                                updateRackBlowerDetailsInDataBase(jsonObject);
                                getUpdatedByWebAppCompleted_Api(act, prefManager, allentownBlowerApplication, model);
                            } else {
                                if (jsonObject.has("message")) {
                                    Utility.Log("UpdateRackBlowerDetails_Api_Response Fail : " + jsonObject.getString("message"));
                                    //Utility.showAlertDialog(act, jsonObject.getString("message"), act.getString(R.string.ok));
                                }
                                else{
                                    //Utility.showAlertDialog(act, act.getString(R.string.error), act.getString(R.string.ok));
                                }

                            }
                        } catch (JSONException e) {
                            //Utility.dismissAlertDialog();
                            Utility.Log("geUpdateRackBlowerDetailstresponse_Api Error : " + e.toString());
                            e.printStackTrace();
                            //Utility.showAlertDialog(act, act.getString(R.string.error), act.getString(R.string.ok));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Utility.dismissProgress();
                        //Utility.dismissAlertDialog();
                        Utility.Log("UpdateRackBlowerDetails_Api Error : " + error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return allentownBlowerApplication.getInstance().getHeader();
            }
        };

        allentownBlowerApplication.getInstance().cancelPendingRequests(PendingID.nUpdateRackBlowerDetails);
        allentownBlowerApplication.getInstance().addToRequestQueue(request, PendingID.nUpdateRackBlowerDetails);
    }

    public void getUpdatedByWebAppCompleted_Api(Activity act, PrefManager prefManager, AllentownBlowerApplication allentownBlowerApplication, RackDetailsModel model) {
        if (prefManager.getHostName() == null || !prefManager.getHostName().contains("http")){
            Log.e("HostName :- ", "Host Name is Not Available");
            return;
        }
        //Utility.ShowMessageReport(act, "Please wait...");
        //Log.e("TAG","getUpdatedByWebAppCompleted_Api method");
        // if (NetworkUtil.getConnectivityStatus(act)) {
        JSONObject objParam = new JSONObject();
        try {
            objParam.put(ApiHandler.strUpdateRackBlowerDetailsId, model.getmId());
            objParam.put(ApiHandler.strUpdateRackBlowerCustomerID, model.getmRackBlowerCustomerID());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                prefManager.getHostName() + ApiHandler.strUrlUpdatedByWebAppCompleted, objParam,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        //Utility.dismissProgress();
                        //Utility.dismissAlertDialog();
                        Utility.Log("getUpdatedByWebAppCompleted_Api_Response : " + jsonObject);
                        try {
                            if (jsonObject.getBoolean("result")) {
                                Utility.Log("UpdatedByWebAppCompleted_Api_Response : " + jsonObject.toString());
                                //api
                            } else {
                                if (jsonObject.has("message"))
                                    //Utility.showAlertDialog(act, jsonObject.getString("message"), act.getString(R.string.ok));
                                    Utility.Log("UpdatedByWebAppCompleted_Api_Response Fail : " + jsonObject.getString("message"));
                                else
                                    Utility.Log("UpdatedByWebAppCompleted_Api_Response: Message element is not in response.");
                            }
                        } catch (JSONException e) {
                            //Utility.dismissAlertDialog();
                            Utility.Log("getUpdatedByWebAppCompletedresponse_Api Error : " + e.toString());
                            e.printStackTrace();
                            //Utility.showAlertDialog(act, act.getString(R.string.error), act.getString(R.string.ok));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Utility.dismissProgress();
                        //Utility.dismissAlertDialog();
                        Utility.Log("UpdatedByWebAppCompleted_Api Error : " + error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return allentownBlowerApplication.getInstance().getHeader();
            }
        };

        allentownBlowerApplication.getInstance().cancelPendingRequests(PendingID.nUpdatedByWebAppCompleted);
        allentownBlowerApplication.getInstance().addToRequestQueue(request, PendingID.nUpdatedByWebAppCompleted);
    }

    public void getUpdateCommandCompleted_Api(Activity act, PrefManager prefManager, AllentownBlowerApplication allentownBlowerApplication, RackDetailsModel model) {
        if (prefManager.getHostName() == null || !prefManager.getHostName().contains("http")){
            Log.e("HostName :- ", "Host Name is Not Available");
            return;
        }
        String command = prefManager.getSendCommandS().trim();
        //Utility.ShowMessageReport(act, "Please wait...");
        //Log.e("TAG","getUpdateCommandCompleted_Api method");
        // if (NetworkUtil.getConnectivityStatus(act)) {
        JSONObject objParam = new JSONObject();
        try {
            objParam.put(ApiHandler.strUpdateRackBlowerDetailsId, model.getmId());
            objParam.put(ApiHandler.strUpdateRackBlowerCustomerID, model.getmRackBlowerCustomerID());
            objParam.put(ApiHandler.strUpdatecompletedCMD, command);
        } catch (JSONException e) {
            e.printStackTrace();

        }
        Log.e("ObjParams",objParam.toString());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                prefManager.getHostName() + ApiHandler.strUrlUpdateCommandCompleted, objParam,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        //Utility.dismissProgress();
                        //Utility.dismissAlertDialog();
                        Utility.Log("getUpdateCommandCompleted_Api_Response : " + jsonObject);
                        try {
                            if (jsonObject.getBoolean("result")) {
                                //Utility.Log("UpdateCommandCompleted_Api_Response : " + jsonObject.toString());
                                //api
                            } else {
                                if (jsonObject.has("message"))
                                    //Utility.showAlertDialog(act, jsonObject.getString("message"), act.getString(R.string.ok));
                                    Utility.Log("UpdateCommandCompleted_Api_Response Fail : " + jsonObject.getString("message"));
                                else
                                    Utility.showAlertDialog(act, act.getString(R.string.error), act.getString(R.string.ok));
                            }
                        } catch (JSONException e) {
                            Utility.dismissAlertDialog();
                            Utility.Log("getUpdateCommandCompletedresponse_Api Error : " + e.toString());
                            e.printStackTrace();
                            Utility.showAlertDialog(act, act.getString(R.string.error), act.getString(R.string.ok));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Utility.dismissProgress();
                        Utility.dismissAlertDialog();
                        Utility.Log("UpdateCommandCompleted_Api Error : " + error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return allentownBlowerApplication.getInstance().getHeader();
            }
        };

        allentownBlowerApplication.getInstance().cancelPendingRequests(PendingID.nUpdateCommandCompleted);
        allentownBlowerApplication.getInstance().addToRequestQueue(request, PendingID.nUpdateCommandCompleted);
    }


    public boolean insert(String tabName, ContentValues cv) {
        db = this.getWritableDatabase();
        long result = db.insert(tabName,null ,cv);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean update(String tabName, ContentValues cv) {
        db = this.getWritableDatabase();
        long result = db.update(tabName,cv,null,null);
        if(result == -1)
            return false;
        else
            return true;
    }

    public void delete(String tabName) {
        try {
            db = this.getWritableDatabase();
            db.delete(tabName, null, null);
        } catch (Exception e) {
            Utility.Log(TAG,"delete SQLiteException : " + e.toString());
        }
        //closeDB();
    }

    public void executeSQL(String strQuery) {
        db = this.getWritableDatabase();
        db.execSQL(strQuery);
    }
}