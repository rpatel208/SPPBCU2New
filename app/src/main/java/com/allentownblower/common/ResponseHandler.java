package com.allentownblower.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;

import com.allentownblower.R;
import com.allentownblower.application.AllentownBlowerApplication;
import com.allentownblower.database.DatabaseTable;
import com.allentownblower.database.SqliteHelper;
import com.allentownblower.module.Cell;
import com.allentownblower.module.DiagnosticsCommand;
import com.allentownblower.module.FeedbackCommand;
import com.allentownblower.module.RackDetailsModel;
import com.allentownblower.module.RackModel;
import com.allentownblower.module.RowHeader;
import com.allentownblower.module.SetPointCommand;
import com.allentownblower.module.WiFiCommand;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mayur Yadav on 01 Sep 2019.
 */
public class ResponseHandler {

    private static final String TAG = ResponseHandler.class.getSimpleName();

//    private static MyDbSource myDb;

    private PrefManager prefManager;

    private String blowerName, blowerAddress, modelNo, fromDate, toDate, blowerAlarm, hepaFilterAlarm, preFilterAlarm, hoseAlarm, supTempAlarm, supHMDAlarm, extTempAlarm, extHMDAlarm;
    private int maxACH = 0, minACH = 0, maxSupplyTemp = 0, minSupplyTemp = 0, maxSupplyHumidity = 0, minSupplyHumidity = 0, maxExhaustTemp = 0, minExhaustTemp = 0, maxExhaustHumidity = 0, minExhaustHumidity = 0;
    private float avgACH = 0, avgSupplyTemp = 0, avgSupplyHumidity = 0, avgExhaustTemp = 0, avgExhaustHumidity = 0;
    public RackDetailsModel rackDetailsModels;
    private Activity act;
    public AllentownBlowerApplication allentownBlowerApplication;
    public SqliteHelper myDb;

    public String APKVersion = "";
    public String LocalAPKVersion = "";
    public ResponseHandler(Activity activity) {
        if (myDb == null) {
            myDb = new SqliteHelper(activity);
        }

        prefManager = new PrefManager(activity);
    }

    public ResponseHandler(Activity activity, SqliteHelper sqliteHelper) {
        if (sqliteHelper == null) {
            myDb = new SqliteHelper(activity);
        } else {
            myDb = sqliteHelper;
        }

        prefManager = new PrefManager(activity);
    }

    public ResponseHandler(Activity activity, RackDetailsModel rackDetailsModel, AllentownBlowerApplication alnBlowerApplication, SqliteHelper sql) {
        if (sql == null) {
            myDb = new SqliteHelper(activity);
        } else {
            myDb = sql;
        }
        prefManager = new PrefManager(activity);
        rackDetailsModels = rackDetailsModel;
        act = activity;
        allentownBlowerApplication = alnBlowerApplication;
    }

    // BLFeedback Table
    public void InsertBLFeedbackTable(HashMap<String, String> stringHashMap, int needToCall_S_command) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseTable.COL_BLFEED_F01, stringHashMap.get("F01"));
        contentValues.put(DatabaseTable.COL_BLFEED_F02, stringHashMap.get("F02"));
        contentValues.put(DatabaseTable.COL_BLFEED_F03, stringHashMap.get("F03"));
        contentValues.put(DatabaseTable.COL_BLFEED_F04, stringHashMap.get("F04"));
        contentValues.put(DatabaseTable.COL_BLFEED_F05, stringHashMap.get("F05"));
        contentValues.put(DatabaseTable.COL_BLFEED_F06, stringHashMap.get("F06"));
        contentValues.put(DatabaseTable.COL_BLFEED_F07, stringHashMap.get("F07"));
        contentValues.put(DatabaseTable.COL_BLFEED_F08, stringHashMap.get("F08"));
        contentValues.put(DatabaseTable.COL_BLFEED_F09, stringHashMap.get("F09"));
        contentValues.put(DatabaseTable.COL_BLFEED_F10, stringHashMap.get("F10"));
        contentValues.put(DatabaseTable.COL_BLFEED_F11, stringHashMap.get("F11"));
        contentValues.put(DatabaseTable.COL_BLFEED_F12, stringHashMap.get("F12"));
        contentValues.put(DatabaseTable.COL_BLFEED_F13, stringHashMap.get("F13"));
        contentValues.put(DatabaseTable.COL_BLFEED_F14, stringHashMap.get("F14"));
        contentValues.put(DatabaseTable.COL_BLFEED_F15, stringHashMap.get("F15"));
        contentValues.put(DatabaseTable.COL_BLFEED_F16, stringHashMap.get("F16"));
        contentValues.put(DatabaseTable.COL_BLFEED_F17, stringHashMap.get("F17"));

        contentValues.put(DatabaseTable.COL_BLFEED_MODIFIEDON, Utility.getCurrentTimeStamp());
        contentValues.put(DatabaseTable.COL_BLFEED_CREATEDON, Utility.getCurrentTimeStamp());

        String strF12 = (String) stringHashMap.get("F12");

        // Red Screen Alarm
        String strZ0 = String.valueOf(hexToBinary(strF12).charAt(15));
        String strZ2 = String.valueOf(hexToBinary(strF12).charAt(13));
        String strX0 = String.valueOf(hexToBinary(strF12).charAt(7));
        String strX2 = String.valueOf(hexToBinary(strF12).charAt(5));
        // Yellow Screen Alarm
        String strZ1 = String.valueOf(hexToBinary(strF12).charAt(14));
        String strX1 = String.valueOf(hexToBinary(strF12).charAt(6));
        String strPreFilterAlarm = String.valueOf(hexToBinary(strF12).charAt(8)); //Y3 = 1

        String strF15 = (String) stringHashMap.get("F15");

        String prefStrZ0 = prefManager.getStrZ0();
        String prefStrZ2 = prefManager.getStrZ2();
        String prefStrX0 = prefManager.getStrX0();
        String prefStrX2 = prefManager.getStrX2();
        String prefStrZ1 = prefManager.getStrZ1();
        String prefStrX1 = prefManager.getStrX1();
        String prefStrPreFilterAlarm = prefManager.getStrPreFilterAlarm();

        String prefF15 = prefManager.getStrF15();

        String strCurrentDate = Utility.getCurrentTimeStamp();
        String strLastSavedDateForFCommand = prefManager.getCurrentDate();
        Date currentDate = Utility.getDateFromString(strCurrentDate);
        Date lastSavedDateForFCommand = Utility.getDateFromString(strLastSavedDateForFCommand);
        int intervalForFCommand = prefManager.getMinute();
        long minutes = 0;
        if (!strLastSavedDateForFCommand.isEmpty()) {
            long diff = currentDate.getTime() - lastSavedDateForFCommand.getTime();
            long seconds = diff / 1000;
            minutes = seconds / 60;

            if ((minutes >= intervalForFCommand) || (!prefF15.equals(strF15)) || (!prefStrZ0.equals(strZ0)) || (!prefStrZ2.equals(strZ2)) || (!prefStrX0.equals(strX0))
                    || (!prefStrX2.equals(strX2)) || (!prefStrZ1.equals(strZ1)) || (!prefStrX1.equals(strX1)) || (!prefStrPreFilterAlarm.equals(strPreFilterAlarm))) {
                myDb.insert(DatabaseTable.TBL_BLFEEDBACK, contentValues);
                //save now in shared preference here
                prefManager.setCurrentDate(strCurrentDate);

                prefManager.setStrZ0(strZ0);
                prefManager.setStrZ2(strZ2);
                prefManager.setStrX0(strX0);
                prefManager.setStrX2(strX2);
                prefManager.setStrZ1(strZ1);
                prefManager.setStrX1(strX1);
                prefManager.setStrPreFilterAlarm(strPreFilterAlarm);
                prefManager.setStrF15(strF15);

            }
        } else {
            myDb.insert(DatabaseTable.TBL_BLFEEDBACK, contentValues);
            //save now in shared preference here
            prefManager.setCurrentDate(strCurrentDate);

            prefManager.setStrZ0(strZ0);
            prefManager.setStrZ2(strZ2);
            prefManager.setStrX0(strX0);
            prefManager.setStrX2(strX2);
            prefManager.setStrZ1(strZ1);
            prefManager.setStrX1(strX1);
            prefManager.setStrPreFilterAlarm(strPreFilterAlarm);
            prefManager.setStrF15(strF15);
        }

        if (myDb.getQueryResultCount("Select * from BLRecentFeedback") == 0) {
            if (myDb.insert(DatabaseTable.TBL_BLRECENTFEEDBACK, contentValues)) {
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nFeedbackDataUpdate);
                if (needToCall_S_command == 1) {
                    if ((minutes >= intervalForFCommand) || (!prefF15.equals(strF15)) || (!prefStrZ0.equals(strZ0)) || (!prefStrZ2.equals(strZ2)) || (!prefStrX0.equals(strX0))
                            || (!prefStrX2.equals(strX2)) || (!prefStrZ1.equals(strZ1)) || (!prefStrX1.equals(strX1)) || (!prefStrPreFilterAlarm.equals(strPreFilterAlarm))) {
                        if (rackDetailsModels != null) {
                            try {
                                getFCommandCalling_Api(stringHashMap);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointData);
                        }
                    } else {
                        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointData);
                    }

                } else if (needToCall_S_command == 0) {
                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nDiagnosticsDataWithFeedbackData);
                } else if (needToCall_S_command == 3) {
                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSingle_S_F_got_response);
                }
            }
        } else {
            if (myDb.update(DatabaseTable.TBL_BLRECENTFEEDBACK, contentValues)) {
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nFeedbackDataUpdate);
                if (needToCall_S_command == 1) {
                    if ((minutes >= intervalForFCommand) || (!prefF15.equals(strF15)) || (!prefStrZ0.equals(strZ0)) || (!prefStrZ2.equals(strZ2)) || (!prefStrX0.equals(strX0))
                            || (!prefStrX2.equals(strX2)) || (!prefStrZ1.equals(strZ1)) || (!prefStrX1.equals(strX1)) || (!prefStrPreFilterAlarm.equals(strPreFilterAlarm))) {
                        if (rackDetailsModels != null) {
                            try {
                                getFCommandCalling_Api(stringHashMap);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointData);
                        }
                    } else {
                        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointData);
                    }
                } else if (needToCall_S_command == 0) {
                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nDiagnosticsDataWithFeedbackData);
                } else if (needToCall_S_command == 3) {
                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSingle_S_F_got_response);
                }
            }
        }
    }

    public void UpdateCommandCompleted_Api() {
        myDb.getUpdateCommandCompleted_Api(act, prefManager, allentownBlowerApplication, rackDetailsModels);
    }

    public void resetFAndSDataForBlower_Api() throws JSONException {

        if (prefManager.getHostName() == null || !prefManager.getHostName().contains("http")) {
            Log.e("HostName :- ", "Host Name is Not Available");
            return;
        }

        JSONObject objParam = new JSONObject();
        try {
            objParam.put(ApiHandler.strUpdateRackBlowerDetailsId, rackDetailsModels.getmId());
            objParam.put(ApiHandler.strUpdateRackBlowerCustomerID, rackDetailsModels.getmRackBlowerCustomerID());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ErrorFParameter", "" + e.getMessage());
        }


        Log.e("TAG", "resetFAndSDataForBlower_Api Parameters : " + objParam.toString());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                prefManager.getHostName() + ApiHandler.strURLResetFAndSData, objParam,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        Utility.Log("resetFAndSDataForBlower_Api Response : " + jsonObject);
                        try {
                            if (jsonObject.getBoolean("result")) {
                                Log.e("TAG", "Records have been deleted from Server : true");
                            } else {
                                Log.e("TAG", "Error while deleting records from Server : false");


                            }
                        } catch (JSONException e) {
                            Utility.Log("resetFAndSDataForBlower_Api Error : " + e.toString());
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utility.Log("resetFAndSDataForBlower_Api Error : " + error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return allentownBlowerApplication.getInstance().getHeader();
            }
        };

        allentownBlowerApplication.getInstance().cancelPendingRequests(PendingID.nResetRackBlowerFAndSData);
        allentownBlowerApplication.getInstance().addToRequestQueue(request, PendingID.nResetRackBlowerFAndSData);

    }

    @SuppressLint("LongLogTag")
    public void getUpdateRackBlowerNumber_Api(Activity act, PrefManager prefManager, AllentownBlowerApplication allentownBlowerApplication, RackDetailsModel rackDetailsModel, String BlowerName, String BuildingName, String RoomName, String ModelNo, SqliteHelper db) {
        if (prefManager.getHostName() == null || !prefManager.getHostName().contains("http")) {
            Log.e("HostName :- ", "Host Name is Not Available");
            return;
        }
//        relative_progress_rack_detail_screen.setVisibility(View.VISIBLE);
        // if (NetworkUtil.getConnectivityStatus(act)) {
        JSONObject objParam = new JSONObject();
        try {
            objParam.put(ApiHandler.strUpdateRackBlowerDetailsId, rackDetailsModel.getmId());
            objParam.put(ApiHandler.strUpdateRackBlowerCustomerID, rackDetailsModel.getmRackBlowerCustomerID());
            objParam.put(ApiHandler.strUpdatedRackBlowerABlowerName, BlowerName);
            objParam.put(ApiHandler.strUpdatedRackBlowerABlowerBuilding, BuildingName);
            objParam.put(ApiHandler.strUpdatedRackBlowerABlowerRoom, RoomName);
            objParam.put(ApiHandler.strUpdatedRackBlowerRackModel, ModelNo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray jsonArr = new JSONArray();
        jsonArr.put(objParam);
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("RackBlowerDetails", jsonArr);
        } catch (JSONException e) {
            Log.e(TAG, "Json exception : " + e.toString());
        }
        Log.e("ObjParam_getUpdateRackBlowerNumber", "" + jsonObj);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                prefManager.getHostName() + ApiHandler.strUrlUpdateRackBlowerNumber, jsonObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
//                        relative_progress_rack_detail_screen.setVisibility(View.GONE);
                        Utility.Log("getUpdateRackBlowerNumber_Api_Response : " + jsonObject);
                        try {
                            if (jsonObject.getBoolean("result")) {
                                Utility.Log("getUpdateRackBlowerNumber_Api_Result : " + jsonObject.getBoolean("result"));
                            } else {
                                if (jsonObject.has("message")) {
                                    Utility.Log("getUpdateRackBlowerNumber_Api_Response Fail : " + jsonObject.getString("message"));
                                    Utility.showAlertDialog(act, jsonObject.getString("message"), "Ok");
                                }
                                else
                                    Utility.showAlertDialog(act, act.getString(R.string.error), act.getString(R.string.ok));

                            }
//                            showDetailWhenScreenLoad();
                        } catch (JSONException e) {
                            Utility.Log("getUpdateRackBlowerNumber_Api Error : " + e.toString());
                            e.printStackTrace();
//                            showDetailWhenScreenLoad();
                            Utility.showAlertDialog(act, act.getString(R.string.error), act.getString(R.string.ok));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        relative_progress_rack_detail_screen.setVisibility(View.GONE);
//                        showDetailWhenScreenLoad();
                        Utility.Log("getUpdateRackBlowerNumber_Api Error : " + error.toString());
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

    private void getFCommandCalling_Api(HashMap<String, String> stringHashMap) throws JSONException {
        if (prefManager.getHostName() == null || !prefManager.getHostName().contains("http")) {
            Log.e("HostName :- ", "Host Name is Not Available");
            return;
        }
        if (!CodeReUse.isCustomerActive)
        {
            Log.e("TAG", "Feedback Data WebAPI call Customer Status Check : Customer is not active on the server.");
            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointData);
            return;
        }
        // if (NetworkUtil.getConnectivityStatus(act)) {
        String strF01 = (String) stringHashMap.get("F01");
        String strF02 = (String) stringHashMap.get("F02");
        String strF03 = (String) stringHashMap.get("F03");
        String strF04 = (String) stringHashMap.get("F04");
        String strF05 = (String) stringHashMap.get("F05");
        String strF06 = (String) stringHashMap.get("F06");
        String strF07 = (String) stringHashMap.get("F07");
        String strF08 = (String) stringHashMap.get("F08");
        String strF09 = (String) stringHashMap.get("F09");
        String strF10 = (String) stringHashMap.get("F10");
        String strF11 = (String) stringHashMap.get("F11");
        String strF12 = (String) stringHashMap.get("F12");
        String strF13 = (String) stringHashMap.get("F13");
        String strF14 = (String) stringHashMap.get("F14");
        String strF15 = (String) stringHashMap.get("F15");
        String strF16 = (String) stringHashMap.get("F16");
        String strF17 = (String) stringHashMap.get("F17") + LocalAPKVersion.replace(".","");


        JSONObject objParam = new JSONObject();
        try {
            objParam.put(ApiHandler.strUpdateRackBlowerDetailsId, rackDetailsModels.getmId());
            objParam.put(ApiHandler.strUpdateRackBlowerCustomerID, rackDetailsModels.getmRackBlowerCustomerID());
            objParam.put(ApiHandler.strParamF01, strF01);
            objParam.put(ApiHandler.strParamF02, strF02);
            objParam.put(ApiHandler.strParamF03, strF03);
            objParam.put(ApiHandler.strParamF04, strF04);
            objParam.put(ApiHandler.strParamF05, strF05);
            objParam.put(ApiHandler.strParamF06, strF06);
            objParam.put(ApiHandler.strParamF07, strF07);
            objParam.put(ApiHandler.strParamF08, strF08);
            objParam.put(ApiHandler.strParamF09, strF09);
            objParam.put(ApiHandler.strParamF10, strF10);
            objParam.put(ApiHandler.strParamF11, strF11);
            objParam.put(ApiHandler.strParamF12, strF12);
            objParam.put(ApiHandler.strParamF13, strF13);
            objParam.put(ApiHandler.strParamF14, strF14);
            objParam.put(ApiHandler.strParamF15, strF15);
            objParam.put(ApiHandler.strParamF16, strF16);
            objParam.put(ApiHandler.strParamF17, strF17);


        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ErrorFParameter", "" + e.getMessage());
        }


        JSONArray jsonArr = new JSONArray();
        jsonArr.put(objParam);
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("FeedbackData", jsonArr);
        } catch (JSONException e) {
            Log.e(TAG, "Json exception : " + e.getMessage());
        }

        Log.e("FCommand_Api_ObjParam", "" + jsonObj);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                prefManager.getHostName() + ApiHandler.strUrlInsertRackBlowerFeedbackData, jsonObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Utility.Log("F_Command_Api_response : " + jsonObject);
                        try {
                            if (jsonObject.getBoolean("result")) {
                                //Utility.Log("F_Command_Api_response", "" + jsonObject.toString());
                                //String message = jsonObject.getString("message");
                                Boolean isUpdatedByWebApp = jsonObject.getBoolean("isUpdatedByWebApp");
                                String SetCommand = jsonObject.getString("SetCommand");
                                CodeReUse.isCustomerActive = true;
                                if (isUpdatedByWebApp) {
                                    myDb.getUpdateRackBlowerDetails_Api(act, prefManager, allentownBlowerApplication, rackDetailsModels);
                                }
                                if (!SetCommand.equals("") || SetCommand.length() != 0) {
                                    Log.e("TAG", "Set Multi Commands : " + SetCommand);
                                    int count = 0;
                                    if (SetCommand.contains(",")) {
                                        String[] multiCommand = SetCommand.split(",");
                                        prefManager.saveArray(multiCommand);
                                        commandCallingFromApi(count);
                                        //testing
                                    } else {
                                        String[] multiCommand = new String[]{SetCommand};
                                        prefManager.saveArray(multiCommand);
                                        commandCallingFromApi(count);
                                    }

                                } else {
                                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointData);
                                }
                            } else {
                                if (jsonObject.has("message")) {
                                    //Utility.showAlertDialog(act, jsonObject.getString("message"), act.getString(R.string.ok));
                                    Utility.Log("getFCommandCallingresponse_Api_Response Fail : " + jsonObject.getString("message"));
                                    if (jsonObject.has("IsCustomerActive"))
                                    {
                                        if (!jsonObject.getBoolean("IsCustomerActive"))
                                        {
                                            //stop sending the data
                                            CodeReUse.isCustomerActive = false;
                                        }
                                        else
                                        {
                                            CodeReUse.isCustomerActive = true;
                                        }
                                    }
                                } else {
                                    Utility.showAlertDialog(act, act.getString(R.string.error), act.getString(R.string.ok));
                                }
                                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointData);
                            }
                        } catch (JSONException e) {
                            Utility.Log("getFCommandCallingresponse_Api Error : " + e.toString());
                            e.printStackTrace();
                            //Utility.showAlertDialog(act, act.getString(R.string.error), act.getString(R.string.ok));
                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointData);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utility.Log("getFCommandCallingresponse_Api Error : " + error.getMessage());
                        //check here with abhi. this is what i did when the server is not reachable and we get the error
                        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointData);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return allentownBlowerApplication.getInstance().getHeader();
            }
        };

        allentownBlowerApplication.getInstance().cancelPendingRequests(PendingID.nstrUrlInsertRackBlowerSetPointData);
        allentownBlowerApplication.getInstance().addToRequestQueue(request, PendingID.nstrUrlInsertRackBlowerSetPointData);

    }

    public void commandCallingFromApi(int count) {
        String[] SetCommand = prefManager.loadArray();
        String cmd = SetCommand[count];
        count++;
        prefManager.setSendCommandS(cmd);
        prefManager.setCount(count);
        Log.e("TAG", "Set Multi cmds : " + cmd);
        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly_Api);
    }

    public void set_S_Command_Value_From_Api(String SetCommand) {
        prefManager.setSendCommandS(SetCommand);
        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly_Api);
    }

    // BLSetPoint Table
    public void InsertBLSetPointTable(HashMap<String, String> stringHashMap, int isStart) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseTable.COL_BLSET_S01, stringHashMap.get("S01"));
        contentValues.put(DatabaseTable.COL_BLSET_S02, stringHashMap.get("S02"));
        contentValues.put(DatabaseTable.COL_BLSET_S03, stringHashMap.get("S03"));
        contentValues.put(DatabaseTable.COL_BLSET_S04, stringHashMap.get("S04"));
        contentValues.put(DatabaseTable.COL_BLSET_S05, stringHashMap.get("S05"));
        contentValues.put(DatabaseTable.COL_BLSET_S06, stringHashMap.get("S06"));
        contentValues.put(DatabaseTable.COL_BLSET_S07, stringHashMap.get("S07"));
        contentValues.put(DatabaseTable.COL_BLSET_S08, stringHashMap.get("S08"));
        contentValues.put(DatabaseTable.COL_BLSET_S09, stringHashMap.get("S09"));
        contentValues.put(DatabaseTable.COL_BLSET_S10, stringHashMap.get("S10"));
        contentValues.put(DatabaseTable.COL_BLSET_S11, stringHashMap.get("S11"));
        contentValues.put(DatabaseTable.COL_BLSET_S12, stringHashMap.get("S12"));
        contentValues.put(DatabaseTable.COL_BLSET_S13, stringHashMap.get("S13"));
        contentValues.put(DatabaseTable.COL_BLSET_S14, stringHashMap.get("S14"));
        contentValues.put(DatabaseTable.COL_BLSET_S15, stringHashMap.get("S15"));
        contentValues.put(DatabaseTable.COL_BLSET_S16, stringHashMap.get("S16"));
        contentValues.put(DatabaseTable.COL_BLSET_S17, stringHashMap.get("S17"));
        contentValues.put(DatabaseTable.COL_BLSET_S18, stringHashMap.get("S18"));
        contentValues.put(DatabaseTable.COL_BLSET_S19, stringHashMap.get("S19"));
        contentValues.put(DatabaseTable.COL_BLSET_S20, stringHashMap.get("S20"));
        contentValues.put(DatabaseTable.COL_BLSET_S21, stringHashMap.get("S21"));

        contentValues.put(DatabaseTable.COL_BLSET_S22, stringHashMap.get("S22"));
        contentValues.put(DatabaseTable.COL_BLSET_S23, stringHashMap.get("S23"));
        contentValues.put(DatabaseTable.COL_BLSET_S24, stringHashMap.get("S24"));
        contentValues.put(DatabaseTable.COL_BLSET_S25, stringHashMap.get("S25"));
        contentValues.put(DatabaseTable.COL_BLSET_S26, stringHashMap.get("S26"));
        contentValues.put(DatabaseTable.COL_BLSET_S27, stringHashMap.get("S27"));

        contentValues.put(DatabaseTable.COL_BLSET_MODIFIEDON, Utility.getCurrentTimeStamp());
        contentValues.put(DatabaseTable.COL_BLSET_CREATEDON, Utility.getCurrentTimeStamp());

        boolean isDataNotSame = returnDataIsMatchOrNot(stringHashMap);

        if (isDataNotSame) {
            myDb.insert(DatabaseTable.TBL_BLSETPOINT, contentValues);
            if (rackDetailsModels != null) {
                getSCommandCalling_Api(stringHashMap);
            }
        }
        else if (CodeReUse.SendScmdDataFirstTime)
        {
            getSCommandCalling_Api(stringHashMap);
        }

        if (myDb.getQueryResultCount("Select * from BLRecentSetPoint") == 0) {
            if (myDb.insert(DatabaseTable.TBL_BLRECENTSETPOINT, contentValues)) {
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointDataUpdate);
                if (isStart == -1) {
                    /*if (rackDetailsModels != null){
                      getSCommandCalling_Api(stringHashMap);
                    }*/
                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nFeedbackData);
                } else if (isStart == 1) {
                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nFeedbackData);
                } else if (isStart == -2) {
                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
                } else if (isStart == 0) {
                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nFeedbackDataSingleForSetting);
                }
            }
        } else {
            if (myDb.update(DatabaseTable.TBL_BLRECENTSETPOINT, contentValues)) {
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointDataUpdate);
                if (isStart == -1) {
                    /*if (rackDetailsModels != null){
                        getSCommandCalling_Api(stringHashMap);
                    }*/
                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nFeedbackData);
                } else if (isStart == 1) {
                    // getSCommandCalling_Api(stringHashMap);
                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nFeedbackData);
                } else if (isStart == -2) {
                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
                } else if (isStart == 0) {
                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nFeedbackDataSingleForSetting);
                }
            }
        }

    }

    private void getSCommandCalling_Api(HashMap<String, String> stringHashMap) {
        if (prefManager.getHostName() == null || !prefManager.getHostName().contains("http")) {
            Log.e("HostName :- ", "Host Name is Not Available");
            return;
        }
        // if (NetworkUtil.getConnectivityStatus(act)) {

        if (!CodeReUse.isCustomerActive)
        {
            Log.e("TAG", "Setpoint Data WebAPI call Customer Status Check : Customer is not active on the server.");
            return;
        }

        String strS01 = (String) stringHashMap.get("S01");
        String strS02 = (String) stringHashMap.get("S02");
        String strS03 = (String) stringHashMap.get("S03");
        String strS04 = (String) stringHashMap.get("S04");
        String strS05 = (String) stringHashMap.get("S05");
        String strS06 = (String) stringHashMap.get("S06");
        String strS07 = (String) stringHashMap.get("S07");
        String strS08 = (String) stringHashMap.get("S08");
        String strS09 = (String) stringHashMap.get("S09");
        String strS10 = (String) stringHashMap.get("S10");
        String strS11 = (String) stringHashMap.get("S11");
        String strS12 = (String) stringHashMap.get("S12");
        String strS13 = (String) stringHashMap.get("S13");
        String strS14 = (String) stringHashMap.get("S14");
        String strS15 = (String) stringHashMap.get("S15");
        String strS16 = (String) stringHashMap.get("S16");
        String strS17 = (String) stringHashMap.get("S17");
        String strS18 = (String) stringHashMap.get("S18");
        String strS19 = (String) stringHashMap.get("S19");
        String strS20 = (String) stringHashMap.get("S20");
        String strS21 = (String) stringHashMap.get("S21");
        String strS22 = (String) stringHashMap.get("S22");
        String strS23 = (String) stringHashMap.get("S23");
        String strS24 = (String) stringHashMap.get("S24");
        String strS25 = (String) stringHashMap.get("S25");
        String strS26 = (String) stringHashMap.get("S26");
        String strS27 = (String) stringHashMap.get("S27");

        JSONObject objParam = new JSONObject();
        try {
            objParam.put(ApiHandler.strUpdateRackBlowerDetailsId, rackDetailsModels.getmId());
            objParam.put(ApiHandler.strUpdateRackBlowerCustomerID, rackDetailsModels.getmRackBlowerCustomerID());
            objParam.put(ApiHandler.strParamS01, strS01);
            objParam.put(ApiHandler.strParamS02, strS02);
            objParam.put(ApiHandler.strParamS03, strS03);
            objParam.put(ApiHandler.strParamS04, strS04);
            objParam.put(ApiHandler.strParamS05, strS05);
            objParam.put(ApiHandler.strParamS06, strS06);
            objParam.put(ApiHandler.strParamS07, strS07);
            objParam.put(ApiHandler.strParamS08, strS08);
            objParam.put(ApiHandler.strParamS09, strS09);
            objParam.put(ApiHandler.strParamS10, strS10);
            objParam.put(ApiHandler.strParamS11, strS11);
            objParam.put(ApiHandler.strParamS12, strS12);
            objParam.put(ApiHandler.strParamS13, strS13);
            objParam.put(ApiHandler.strParamS14, strS14);
            objParam.put(ApiHandler.strParamS15, strS15);
            objParam.put(ApiHandler.strParamS16, strS16);
            objParam.put(ApiHandler.strParamS17, strS17);
            objParam.put(ApiHandler.strParamS18, strS18);
            objParam.put(ApiHandler.strParamS19, strS19);
            objParam.put(ApiHandler.strParamS20, strS20);
            objParam.put(ApiHandler.strParamS21, strS21);

            objParam.put(ApiHandler.strParamS22, strS22);
            objParam.put(ApiHandler.strParamS23, strS23);
            objParam.put(ApiHandler.strParamS24, strS24);
            objParam.put(ApiHandler.strParamS25, strS25);
            objParam.put(ApiHandler.strParamS26, strS26);
            objParam.put(ApiHandler.strParamS27, strS27);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray jsonArr = new JSONArray();
        jsonArr.put(objParam);
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("SetPointData", jsonArr);
        } catch (JSONException e) {
            Log.e(TAG, "Json exception : " + e.toString());
        }
        Log.e("ObjParam_S_Command_Api", "" + jsonObj);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                prefManager.getHostName() + ApiHandler.strUrlInsertRackBlowerSetPointData, jsonObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Utility.Log("S_Command_Api_response : " + jsonObject);
                        try {
                            if (jsonObject.getBoolean("result")) {
                                Utility.Log("S_Command_Api_responser", "" + jsonObject.toString());
                                CodeReUse.isCustomerActive = true;
                                CodeReUse.SendScmdDataFirstTime = false;
                            } else {
                                if (jsonObject.has("message")) {
                                    //Utility.showAlertDialog(act, jsonObject.getString("message"), act.getString(R.string.ok));
                                    Utility.Log("S_Command_Api_Response Fail : " + jsonObject.getString("message"));
                                    if (jsonObject.has("IsCustomerActive"))
                                    {
                                        if (!jsonObject.getBoolean("IsCustomerActive"))
                                        {
                                            //stop sending the data
                                            CodeReUse.isCustomerActive = false;
                                        }
                                        else
                                        {
                                            CodeReUse.isCustomerActive = true;
                                        }
                                    }
                                }
                                else
                                    Utility.showAlertDialog(act, act.getString(R.string.error), act.getString(R.string.ok));

                            }
                        } catch (JSONException e) {
                            Utility.Log("getSCommandCallingresponse_Api Error : " + e.toString());
                            e.printStackTrace();
                            Utility.showAlertDialog(act, act.getString(R.string.error), act.getString(R.string.ok));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utility.Log("getSCommandCallingresponse Error : " + error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return allentownBlowerApplication.getInstance().getHeader();
            }
        };

        allentownBlowerApplication.getInstance().cancelPendingRequests(PendingID.nstrUrlInsertRackBlowerSetPointData);
        allentownBlowerApplication.getInstance().addToRequestQueue(request, PendingID.nstrUrlInsertRackBlowerSetPointData);
    }

    public boolean returnDataIsMatchOrNot(HashMap<String, String> stringHashMap) {
        ArrayList<SetPointCommand> arrayList = new ArrayList<>();
        arrayList = lastRecordFromBLSetPointTable();
        if (arrayList.size() > 0) {
            SetPointCommand setPointCommand = arrayList.get(0);
            if (!setPointCommand.getS01().equals(stringHashMap.get("S01"))) {
                return true;
            }

            if (!setPointCommand.getS02().equals(stringHashMap.get("S02"))) {
                return true;
            }

            if (!setPointCommand.getS03().equals(stringHashMap.get("S03"))) {
                return true;
            }

            if (!setPointCommand.getS04().equals(stringHashMap.get("S04"))) {
                return true;
            }

            if (!setPointCommand.getS05().equals(stringHashMap.get("S05"))) {
                return true;
            }

            if (!setPointCommand.getS06().equals(stringHashMap.get("S06"))) {
                return true;
            }

            if (!setPointCommand.getS07().equals(stringHashMap.get("S07"))) {
                return true;
            }

            if (!setPointCommand.getS08().equals(stringHashMap.get("S08"))) {
                return true;
            }

            if (!setPointCommand.getS09().equals(stringHashMap.get("S09"))) {
                return true;
            }

            if (!setPointCommand.getS10().equals(stringHashMap.get("S10"))) {
                return true;
            }

            if (!setPointCommand.getS11().equals(stringHashMap.get("S11"))) {
                return true;
            }

            if (!setPointCommand.getS12().equals(stringHashMap.get("S12"))) {
                return true;
            }

            if (!setPointCommand.getS13().equals(stringHashMap.get("S13"))) {
                return true;
            }

            if (!setPointCommand.getS14().equals(stringHashMap.get("S14"))) {
                return true;
            }

            if (!setPointCommand.getS15().equals(stringHashMap.get("S15"))) {
                return true;
            }

            if (!setPointCommand.getS16().equals(stringHashMap.get("S16"))) {
                return true;
            }

            if (!setPointCommand.getS17().equals(stringHashMap.get("S17"))) {
                return true;
            }

            if (!setPointCommand.getS18().equals(stringHashMap.get("S18"))) {
                return true;
            }

            if (!setPointCommand.getS19().equals(stringHashMap.get("S19"))) {
                return true;
            }

            if (!setPointCommand.getS20().equals(stringHashMap.get("S20"))) {
                return true;
            }

            if (!setPointCommand.getS21().equals(stringHashMap.get("S21"))) {
                return true;
            }


            if (!setPointCommand.getS22().equals(stringHashMap.get("S22"))) {
                return true;
            }

            if (!setPointCommand.getS23().equals(stringHashMap.get("S23"))) {
                return true;
            }

            if (!setPointCommand.getS24().equals(stringHashMap.get("S24"))) {
                return true;
            }

            if (!setPointCommand.getS25().equals(stringHashMap.get("S25"))) {
                return true;
            }

            if (!setPointCommand.getS26().equals(stringHashMap.get("S26"))) {
                return true;
            }

            if (!setPointCommand.getS27().equals(stringHashMap.get("S27"))) {
                return true;
            }


        }
        return false;
    }

    // BLDiagnostics Table
    public void InsertBLDiagnosticsTable(HashMap<String, String> stringHashMap, int isStart) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseTable.COL_BLDIAGNOSTICS_D01, stringHashMap.get("D01"));
        contentValues.put(DatabaseTable.COL_BLDIAGNOSTICS_D02, stringHashMap.get("D02"));
        contentValues.put(DatabaseTable.COL_BLDIAGNOSTICS_D03, stringHashMap.get("D03"));
        contentValues.put(DatabaseTable.COL_BLDIAGNOSTICS_D04, stringHashMap.get("D04"));
        contentValues.put(DatabaseTable.COL_BLDIAGNOSTICS_D05, stringHashMap.get("D05"));
        contentValues.put(DatabaseTable.COL_BLDIAGNOSTICS_D06, stringHashMap.get("D06"));
        contentValues.put(DatabaseTable.COL_BLDIAGNOSTICS_D07, stringHashMap.get("D07"));
        contentValues.put(DatabaseTable.COL_BLDIAGNOSTICS_D08, stringHashMap.get("D08"));
        contentValues.put(DatabaseTable.COL_BLDIAGNOSTICS_D09, stringHashMap.get("D09"));
        contentValues.put(DatabaseTable.COL_BLDIAGNOSTICS_D10, stringHashMap.get("D10"));
        contentValues.put(DatabaseTable.COL_BLDIAGNOSTICS_D11, stringHashMap.get("D11"));
        contentValues.put(DatabaseTable.COL_BLDIAGNOSTICS_D12, stringHashMap.get("D12"));
        contentValues.put(DatabaseTable.COL_BLDIAGNOSTICS_D13, stringHashMap.get("D13"));
        contentValues.put(DatabaseTable.COL_BLDIAGNOSTICS_D14, stringHashMap.get("D14"));
        contentValues.put(DatabaseTable.COL_BLDIAGNOSTICS_D15, stringHashMap.get("D15"));
        contentValues.put(DatabaseTable.COL_BLDIAGNOSTICS_D16, stringHashMap.get("D16"));
        contentValues.put(DatabaseTable.COL_BLDIAGNOSTICS_D17, stringHashMap.get("D17"));
        contentValues.put(DatabaseTable.COL_BLDIAGNOSTICS_D18, stringHashMap.get("D18"));
        contentValues.put(DatabaseTable.COL_BLDIAGNOSTICS_D19, stringHashMap.get("D19"));
        contentValues.put(DatabaseTable.COL_BLDIAGNOSTICS_D20, stringHashMap.get("D20"));
        contentValues.put(DatabaseTable.COL_BLDIAGNOSTICS_D21, stringHashMap.get("D21"));
        contentValues.put(DatabaseTable.COL_BLDIAGNOSTICS_D22, stringHashMap.get("D22"));
        contentValues.put(DatabaseTable.COL_BLDIAGNOSTICS_D23, stringHashMap.get("D23"));
        contentValues.put(DatabaseTable.COL_BLDIAGNOSTICS_D24, stringHashMap.get("D24"));
        contentValues.put(DatabaseTable.COL_BLDIAGNOSTICS_D25, stringHashMap.get("D25"));
        contentValues.put(DatabaseTable.COL_BLDIAGNOSTICS_D26, stringHashMap.get("D26"));
        contentValues.put(DatabaseTable.COL_BLDIAGNOSTICS_D27, stringHashMap.get("D27"));
        contentValues.put(DatabaseTable.COL_BLDIAGNOSTICS_D28, stringHashMap.get("D28"));
        contentValues.put(DatabaseTable.COL_BLDIAGNOSTICS_D29, stringHashMap.get("D29"));
        contentValues.put(DatabaseTable.COL_BLDIAGNOSTICS_D30, stringHashMap.get("D30"));
        contentValues.put(DatabaseTable.COL_BLDIAGNOSTICS_MODIFIEDON, Utility.getCurrentTimeStamp());
        contentValues.put(DatabaseTable.COL_BLDIAGNOSTICS_CREATEDON, Utility.getCurrentTimeStamp());

        if (myDb.getQueryResultCount("Select * from BLDiagnostics") == 0) {
            if (myDb.insert(DatabaseTable.TBL_BLDIAGNOSTICS, contentValues)) {
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointDataUpdate);
                if (isStart == 0) {
                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nFeedbackDataOnly);
                } else if (isStart == -3) {
                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nDiagnosticsDataOnly);
                }
            }
        } else {
            if (myDb.update(DatabaseTable.TBL_BLDIAGNOSTICS, contentValues)) {
//                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointDataUpdate);
//                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nFeedbackDataOnly);
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointDataUpdate);
                if (isStart == 0) {
                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nFeedbackDataOnly);
                } else if (isStart == -3) {
                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nDiagnosticsDataOnly);
                }
            }
        }
    }

    // BLWiFi Table
    public void InsertBLWiFiTable(HashMap<String, String> stringHashMap, int isStart) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseTable.COL_BLWIFI_W01, stringHashMap.get("W01"));
        contentValues.put(DatabaseTable.COL_BLWIFI_W02, stringHashMap.get("W02"));
        contentValues.put(DatabaseTable.COL_BLWIFI_W03, stringHashMap.get("W03"));
        contentValues.put(DatabaseTable.COL_BLWIFI_W04, stringHashMap.get("W04"));
        contentValues.put(DatabaseTable.COL_BLWIFI_W05, stringHashMap.get("W05"));
        contentValues.put(DatabaseTable.COL_BLWIFI_W06, stringHashMap.get("W06"));
        contentValues.put(DatabaseTable.COL_BLWIFI_W07, stringHashMap.get("W07"));
        contentValues.put(DatabaseTable.COL_BLWIFI_W08, stringHashMap.get("W08"));
        contentValues.put(DatabaseTable.COL_BLWIFI_W09, stringHashMap.get("W09"));
        contentValues.put(DatabaseTable.COL_BLWIFI_W10, stringHashMap.get("W10"));
        contentValues.put(DatabaseTable.COL_BLWIFI_W11, stringHashMap.get("W11"));
        contentValues.put(DatabaseTable.COL_BLWIFI_W12, stringHashMap.get("W12"));
        contentValues.put(DatabaseTable.COL_BLWIFI_MODIFIEDON, Utility.getCurrentTimeStamp());
        contentValues.put(DatabaseTable.COL_BLWIFI_CREATEDON, Utility.getCurrentTimeStamp());

        if (myDb.getQueryResultCount("Select * from BLWiFi") == 0) {
            if (myDb.insert(DatabaseTable.TBL_BLWIFI, contentValues)) {
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nWiFiDataUpdate);
//                if (isStart == 4){
//                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetWifiDataOnlyResponse);
//                }
            }
        } else {
            if (myDb.update(DatabaseTable.TBL_BLWIFI, contentValues)) {
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nWiFiDataUpdate);
//                if (isStart == 4){
//                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetWifiDataOnlyResponse);
//                }
            }
        }
    }

    // BLFeedback Table
    public void InsertBLFeedbackTable(JSONArray jsonArray) {

        try {
            JSONObject stringHashMap = jsonArray.getJSONObject(0);

            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseTable.COL_BLFEED_F01, stringHashMap.getString("F01"));
            contentValues.put(DatabaseTable.COL_BLFEED_F02, stringHashMap.getString("F02"));
            contentValues.put(DatabaseTable.COL_BLFEED_F03, stringHashMap.getString("F03"));
            contentValues.put(DatabaseTable.COL_BLFEED_F04, stringHashMap.getString("F04"));
            contentValues.put(DatabaseTable.COL_BLFEED_F05, stringHashMap.getString("F05"));
            contentValues.put(DatabaseTable.COL_BLFEED_F06, stringHashMap.getString("F06"));
            contentValues.put(DatabaseTable.COL_BLFEED_F07, stringHashMap.getString("F07"));
            contentValues.put(DatabaseTable.COL_BLFEED_F08, stringHashMap.getString("F08"));
            contentValues.put(DatabaseTable.COL_BLFEED_F09, stringHashMap.getString("F09"));
            contentValues.put(DatabaseTable.COL_BLFEED_F10, stringHashMap.getString("F10"));
            contentValues.put(DatabaseTable.COL_BLFEED_F11, stringHashMap.getString("F11"));
            contentValues.put(DatabaseTable.COL_BLFEED_F12, stringHashMap.getString("F12"));
            contentValues.put(DatabaseTable.COL_BLFEED_F13, stringHashMap.getString("F13"));
            contentValues.put(DatabaseTable.COL_BLFEED_F14, stringHashMap.getString("F14"));

            contentValues.put(DatabaseTable.COL_BLFEED_F15, stringHashMap.getString("F15"));
            contentValues.put(DatabaseTable.COL_BLFEED_F16, stringHashMap.getString("F16"));
            contentValues.put(DatabaseTable.COL_BLFEED_F17, stringHashMap.getString("F17"));


            contentValues.put(DatabaseTable.COL_BLFEED_MODIFIEDON, stringHashMap.getString("ModifiedOn"));
            contentValues.put(DatabaseTable.COL_BLFEED_CREATEDON, stringHashMap.getString("CreatedOn"));

            if (myDb.getQueryResultCount("Select * from BLRecentFeedback") == 0) {
                if (myDb.insert(DatabaseTable.TBL_BLRECENTFEEDBACK, contentValues)) {
                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nFeedbackDataUpdate);
                }
            } else {
                if (myDb.update(DatabaseTable.TBL_BLRECENTFEEDBACK, contentValues)) {
                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nFeedbackDataUpdate);
                }
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    // BLSetPoint Table
    public void InsertBLSetPointTable(JSONArray jsonArray) {

        try {
            JSONObject stringHashMap = jsonArray.getJSONObject(0);

            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseTable.COL_BLSET_S01, stringHashMap.getString("S01"));
            contentValues.put(DatabaseTable.COL_BLSET_S02, stringHashMap.getString("S02"));
            contentValues.put(DatabaseTable.COL_BLSET_S03, stringHashMap.getString("S03"));
            contentValues.put(DatabaseTable.COL_BLSET_S04, stringHashMap.getString("S04"));
            contentValues.put(DatabaseTable.COL_BLSET_S05, stringHashMap.getString("S05"));
            contentValues.put(DatabaseTable.COL_BLSET_S06, stringHashMap.getString("S06"));
            contentValues.put(DatabaseTable.COL_BLSET_S07, stringHashMap.getString("S07"));
            contentValues.put(DatabaseTable.COL_BLSET_S08, stringHashMap.getString("S08"));
            contentValues.put(DatabaseTable.COL_BLSET_S09, stringHashMap.getString("S09"));
            contentValues.put(DatabaseTable.COL_BLSET_S10, stringHashMap.getString("S10"));
            contentValues.put(DatabaseTable.COL_BLSET_S11, stringHashMap.getString("S11"));
            contentValues.put(DatabaseTable.COL_BLSET_S12, stringHashMap.getString("S12"));
            contentValues.put(DatabaseTable.COL_BLSET_S13, stringHashMap.getString("S13"));
            contentValues.put(DatabaseTable.COL_BLSET_S14, stringHashMap.getString("S14"));
            contentValues.put(DatabaseTable.COL_BLSET_S15, stringHashMap.getString("S15"));
            contentValues.put(DatabaseTable.COL_BLSET_S16, stringHashMap.getString("S16"));
            contentValues.put(DatabaseTable.COL_BLSET_S17, stringHashMap.getString("S17"));
            contentValues.put(DatabaseTable.COL_BLSET_S18, stringHashMap.getString("S18"));
            contentValues.put(DatabaseTable.COL_BLSET_S19, stringHashMap.getString("S19"));
            contentValues.put(DatabaseTable.COL_BLSET_S20, stringHashMap.getString("S20"));
            contentValues.put(DatabaseTable.COL_BLSET_S21, stringHashMap.getString("S21"));

            contentValues.put(DatabaseTable.COL_BLSET_S22, stringHashMap.getString("S22"));
            contentValues.put(DatabaseTable.COL_BLSET_S23, stringHashMap.getString("S23"));
            contentValues.put(DatabaseTable.COL_BLSET_S24, stringHashMap.getString("S24"));
            contentValues.put(DatabaseTable.COL_BLSET_S25, stringHashMap.getString("S25"));
            contentValues.put(DatabaseTable.COL_BLSET_S26, stringHashMap.getString("S26"));
            contentValues.put(DatabaseTable.COL_BLSET_S27, stringHashMap.getString("S27"));
            contentValues.put(DatabaseTable.COL_BLSET_MODIFIEDON, stringHashMap.getString("ModifiedOn"));
            contentValues.put(DatabaseTable.COL_BLSET_CREATEDON, stringHashMap.getString("CreatedOn"));

            if (myDb.getQueryResultCount("Select * from BLRecentSetPoint") == 0) {
                if (myDb.insert(DatabaseTable.TBL_BLRECENTSETPOINT, contentValues)) {
                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointDataUpdate);
                }
            } else {
                if (myDb.update(DatabaseTable.TBL_BLRECENTSETPOINT, contentValues)) {
                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointDataUpdate);
                }
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public void updateDBfile() {
        myDb.db.execSQL("Vacuum");
    }

    // TODO : DELETE ALL TABLE
    public void deleteAllTablesData() {
        myDb.delete(DatabaseTable.TBL_BLRECENTFEEDBACK);
        myDb.delete(DatabaseTable.TBL_BLRECENTSETPOINT);
        updateDBfile();
    }

    // TODO :- get BLFeedBack Table Data
    public ArrayList<FeedbackCommand> getBLFeedbackData(String startDate, String endDate) {

        ArrayList<FeedbackCommand> arrayList = new ArrayList<>();

        try {
            String strQuery = "SELECT * FROM BLFeedback WHERE date(CreatedOn) >= '" + startDate + "' " + "AND date(CreatedOn) <= '" + endDate + "' limit 190000";
            Log.e("Query", strQuery);
            Cursor cursor = myDb.getQueryResult(strQuery);

            if (cursor.getCount() > 0) {
                Log.e("FB Q printed started", "-");
                arrayList.clear();
                int i = 0;
                while (cursor.moveToNext()) {
                    FeedbackCommand feedbackCommand = new FeedbackCommand();
                    //feedbackCommand.setID(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_ID)));
                    feedbackCommand.setF01(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F01)));
                    feedbackCommand.setF02(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F02)));
                    //feedbackCommand.setF03(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F03)));
                    feedbackCommand.setF04(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F04)));
                    feedbackCommand.setF05(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F05)));
                    feedbackCommand.setF06(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F06)));
                    feedbackCommand.setF07(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F07)));
                    feedbackCommand.setF08(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F08)));
                    feedbackCommand.setF09(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F09)));
                    //feedbackCommand.setF10(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F10)));
                    //feedbackCommand.setF11(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F11)));
                    feedbackCommand.setF12(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12)));
                    feedbackCommand.setF15(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F15)));
                    //feedbackCommand.setF13(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F13)));
                    //feedbackCommand.setF14(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F14)));
                    feedbackCommand.setCreatedOn(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_CREATEDON)));
                    feedbackCommand.setModifiedOn(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_MODIFIEDON)));
                    //feedbackCommand.setIsSynced(cursor.getInt(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_ISSYNCED)));
                    arrayList.add(feedbackCommand);
                }
            }

            cursor.close();
            Log.e("FB Q printed finished", "-");
        } catch (Exception e) {
            e.getMessage();
        }

        return arrayList;
    }

    // TODO :- get BLFeedBack Table Data
    public HashMap<String, Object> getBLFeedbackDataAll(String startDate, String endDate, ArrayList<SetPointCommand> arrSetpointData, ArrayList<RackModel> arrRackSetUpData, ResponseHandler responseHandler, boolean isFromChangeUnit) {
        HashMap<String, Object> objHasMap = new HashMap<>();
        List<List<Cell>> arrayList = new ArrayList<>();
        List<RowHeader> rowHeaderList = new ArrayList<>();
        String strQuery = "";
        try {
//            if (isFromChangeUnit){
//                strQuery = "SELECT F01,F02,F08,F12,CreatedOn FROM BLFeedback limit 100000";
//            }else {
//                strQuery = "SELECT F01,F02,F08,F12,CreatedOn FROM BLFeedback WHERE date(CreatedOn) >= '" + startDate + "' " + "AND date(CreatedOn) <= '" + endDate + "' limit 100000";
//            }
            Log.e("Query", strQuery);
            Cursor cursor = getQueryResultsExport(isFromChangeUnit, startDate, endDate, false);

            if (cursor.getCount() > 0) {
                Log.e("FB Q printed started..", "Row Count : " + cursor.getCount());
                arrayList.clear();
                int i = 0;
                while (cursor.moveToNext()) {

                    int j = 0;
                    int rownumber;
                    if (i == 0) {
                        rownumber = i + 1;
                        RowHeader header = new RowHeader("" + rownumber, "" + rownumber);
                        rowHeaderList.add(header);

                        List<Cell> cellList = new ArrayList<>();
                        Cell cellModeName = new Cell(j + "-" + i, "Mode :");
                        cellList.add(cellModeName);
                        if (responseHandler.hexToBinary(arrSetpointData.get(0).getS01()).charAt(12) == '1') {
                            Cell cellMode = new Cell((j + 1) + "-" + i, "Pos");
                            cellList.add(cellMode);
                        } else {
                            Cell cellMode = new Cell((j + 1) + "-" + i, "Neg");
                            cellList.add(cellMode);
                        }
                        arrayList.add(cellList);
                        i++;

                        rownumber = i + 1;

                        RowHeader header1 = new RowHeader("" + rownumber, "" + rownumber);
                        rowHeaderList.add(header1);

                        List<Cell> cellList1 = new ArrayList<>();
                        Cell cellACHName = new Cell(j + "-" + i, "ACH :");
                        cellList1.add(cellACHName);

                        int S07_YY = Integer.parseInt(responseHandler.hexToString(arrSetpointData.get(0).getS07(), false));
                        int S08_XXXX = Integer.parseInt(responseHandler.hexToString(arrSetpointData.get(0).getS08()));
                        int sumS07_S08 = S07_YY + S08_XXXX;

                        Cell cellACHValue = new Cell((j + 1) + "-" + i, sumS07_S08);
                        cellList1.add(cellACHValue);
                        arrayList.add(cellList1);
                        i++;

                        rownumber = i + 1;

                        RowHeader header2 = new RowHeader("" + rownumber, "" + rownumber);
                        rowHeaderList.add(header2);

                        List<Cell> cellList2 = new ArrayList<>();
                        Cell cellCFMName = new Cell(j + "-" + i, "CFM :");
                        cellList2.add(cellCFMName);

                        Cell cellCFMValue = new Cell((j + 1) + "-" + i, responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F07)), false));
                        cellList2.add(cellCFMValue);
                        arrayList.add(cellList2);
                        i++;

                        rownumber = i + 1;

                        RowHeader header3 = new RowHeader("" + rownumber, "" + rownumber);
                        rowHeaderList.add(header3);

                        List<Cell> cellList3 = new ArrayList<>();
                        Cell cellPressureName = new Cell(j + "-" + i, "Pressure :");
                        cellList3.add(cellPressureName);

//                        String F09 = feedbackArrayList.get(0).getF09();
                        //String F09 = responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F09)));

                        String F09 = cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F09));
                        String a = F09.substring(1, 4);
                        a = F09.substring(1);

                        Integer F09Value = Integer.parseInt(F09.substring(1), 16);

                        String F09_XXXX = CodeReUse.formatter3Digit.format(Float.valueOf(F09Value) / 1000);

                        if (F09.startsWith("8"))
                            F09_XXXX = "-" + F09_XXXX;
                        else
                            F09_XXXX = "+" + F09_XXXX;

                        Cell cellPressureValue = new Cell((j + 1) + "-" + i, F09_XXXX);
                        cellList3.add(cellPressureValue);
                        arrayList.add(cellList3);
                        i++;
                    }

                    rownumber = i + 1;

                    RowHeader header = new RowHeader("" + rownumber, "" + rownumber);
                    rowHeaderList.add(header);

                    List<Cell> cellList = new ArrayList<>();
                    Cell cellBlowerName = new Cell(j + "-" + i, arrRackSetUpData.get(0).getBlowerName());
                    cellList.add(cellBlowerName);

                    Cell cellBlowerAddress = new Cell((j + 1) + "-" + (i), getSerialNumber());
                    cellList.add(cellBlowerAddress);

                    Cell cellModelNumber = new Cell((j + 2) + "-" + i, arrRackSetUpData.get(0).getModelNo());
                    cellList.add(cellModelNumber);

                    Cell cellCreateOn = new Cell((j + 3) + "-" + i, cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_CREATEDON)));
                    cellList.add(cellCreateOn);

                    Cell cellACH = new Cell((j + 4) + "-" + i, responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F08))));
                    cellList.add(cellACH);

                    if (responseHandler.hexToBinary(arrSetpointData.get(0).getS01()).charAt(11) == '1') {
                        Cell cellSupplyTemp = new Cell((j + 5) + "-" + i, responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F01)), true) + " C");
                        cellList.add(cellSupplyTemp);
                    } else {
                        Cell cellSupplyTemp = new Cell((j + 5) + "-" + i, responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F01)), true) + " F");
                        cellList.add(cellSupplyTemp);
                    }

                    Cell cellSupplyHumidity = new Cell((j + 6) + "-" + i, responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F02)), true) + " %");
                    cellList.add(cellSupplyHumidity);

                    if (responseHandler.hexToBinary(arrSetpointData.get(0).getS01()).charAt(11) == '1') {
                        Cell cellExhaustTemp = new Cell((j + 7) + "-" + i, responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F01)), false) + " C");
                        cellList.add(cellExhaustTemp);
                    } else {
                        Cell cellExhaustTemp = new Cell((j + 7) + "-" + i, responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F01)), false) + " F");
                        cellList.add(cellExhaustTemp);
                    }

                    Cell cellExhaustHumidity = new Cell((j + 8) + "-" + i, responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F02)), false) + " %");
                    cellList.add(cellExhaustHumidity);

                    Cell cellRPMS = new Cell((j + 9) + "-" + i, responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F05))));
                    cellList.add(cellRPMS);

                    Cell cellRPME = new Cell((j + 10) + "-" + i, responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F06))));
                    cellList.add(cellRPME);

                    if (responseHandler.hexToBinary(arrSetpointData.get(0).getS01()).charAt(9) == '1') {
                        Cell cellAirFlow = new Cell((j + 11) + "-" + i, responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F07)), true) + " CMH");
                        cellList.add(cellAirFlow);
                    } else {
                        Cell cellAirFlow = new Cell((j + 11) + "-" + i, responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F07)), true) + " CFM");
                        cellList.add(cellAirFlow);
                    }

                    String F04 = responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F04)));

                    if (F04.charAt(0) == '0') {
                        // +
                        float decimal = Float.parseFloat(String.valueOf(responseHandler.binaryToDecimal(Integer.parseInt(F04.substring(2, 16))))) / 1000;
                        Cell cellPressure = new Cell((j + 12) + "-" + i, "+" + CodeReUse.formatter3Digit.format(decimal) + " WC");
                        cellList.add(cellPressure);
                    } else {
                        // -
                        float decimal = Float.parseFloat(String.valueOf(responseHandler.binaryToDecimal(Integer.parseInt(F04.substring(2, 16))))) / 1000;
                        Cell cellPressure = new Cell((j + 12) + "-" + i, "-" + CodeReUse.formatter3Digit.format(decimal) + " WC");
                        cellList.add(cellPressure);
                    }

                    if ((responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(15) == '1') || (responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(7) == '1')) {
                        Cell cellBlowerAlarm = new Cell((j + 13) + "-" + i, "X");
                        cellList.add(cellBlowerAlarm);
                    } else {
                        Cell cellBlowerAlarm = new Cell((j + 13) + "-" + i, "");
                        cellList.add(cellBlowerAlarm);
                    }

                    if ((responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(14) == '1') || (responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(6) == '1')) {
                        Cell cellHepaFilterAlarm = new Cell((j + 14) + "-" + i, "X");
                        cellList.add(cellHepaFilterAlarm);
                    } else {
                        Cell cellHepaFilterAlarm = new Cell((j + 14) + "-" + i, "");
                        cellList.add(cellHepaFilterAlarm);
                    }

                    if (responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(8) == '1') {
                        Cell cellPreFilterAlarm = new Cell((j + 15) + "-" + i, "X");
                        cellList.add(cellPreFilterAlarm);
                    } else {
                        Cell cellPreFilterAlarm = new Cell((j + 15) + "-" + i, "");
                        cellList.add(cellPreFilterAlarm);
                    }

                    if ((responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(13) == '8') || (responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(5) == '1')) {
                        Cell cellHoseAlarm = new Cell((j + 16) + "-" + i, "X");
                        cellList.add(cellHoseAlarm);
                    } else {
                        Cell cellHoseAlarm = new Cell((j + 16) + "-" + i, "");
                        cellList.add(cellHoseAlarm);
                    }

                    if (cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F15)).charAt(0) == '1') {
                        Cell cellSupTmpAlarm = new Cell((j + 17) + "-" + i, "X");
                        cellList.add(cellSupTmpAlarm);
                    } else {
                        Cell cellSupTmpAlarm = new Cell((j + 17) + "-" + i, "");
                        cellList.add(cellSupTmpAlarm);
                    }
                    if (cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F15)).charAt(1) == '1') {
                        Cell cellSupHMDAlarm = new Cell((j + 18) + "-" + i, "X");
                        cellList.add(cellSupHMDAlarm);
                    } else {
                        Cell cellSupHMDAlarm = new Cell((j + 18) + "-" + i, "");
                        cellList.add(cellSupHMDAlarm);
                    }
                    if (cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F15)).charAt(2) == '1') {
                        Cell cellExhaustTmpAlarm = new Cell((j + 19) + "-" + i, "X");
                        cellList.add(cellExhaustTmpAlarm);
                    } else {
                        Cell cellExhaustTmpAlarm = new Cell((j + 19) + "-" + i, "");
                        cellList.add(cellExhaustTmpAlarm);
                    }
                    if (cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F15)).charAt(3) == '1') {
                        Cell cellExhaustHMDAlarm = new Cell((j + 20) + "-" + i, "X");
                        cellList.add(cellExhaustHMDAlarm);
                    } else {
                        Cell cellExhaustHMDAlarm = new Cell((j + 20) + "-" + i, "");
                        cellList.add(cellExhaustHMDAlarm);
                    }


                    arrayList.add(cellList);

                    i++;
                }
            }

            cursor.close();
            Log.e("FB Q printed finished", "-");
        } catch (Exception e) {
            e.getMessage();
        }
        objHasMap.put("rowHeader", rowHeaderList);
        objHasMap.put("cell", arrayList);
        return objHasMap;
    }

    public StringBuilder getBLFeedbackDataAllExport(String startDate, String endDate, ArrayList<SetPointCommand> arrSetpointData, ArrayList<RackModel> arrRackSetUpData, ResponseHandler responseHandler, boolean isFromChangeUnit, ArrayList<String> columnNameListAll) {
        StringBuilder data = new StringBuilder();
        int i = 0;
        try {
            Cursor cursor = getQueryResultsExport(isFromChangeUnit, startDate, endDate, false);
//            if (responseHandler.hexToBinary(arrSetpointData.get(0).getS01()).charAt(12) == '1') {
//                data.append("Mode, Pos");
//            } else {
//                data.append("Mode, Neg");
//            }
//            data.append("\n");
//
//            String S07_YY = String.valueOf(responseHandler.hexToString(arrSetpointData.get(0).getS07(), false));
//            String S08_XXXX = String.valueOf(responseHandler.hexToString(arrSetpointData.get(0).getS08()));
//            String mStrACH = "ACH, " + S07_YY + S08_XXXX;
//            data.append(mStrACH);
//            data.append("\n");
//
//            String F07_YY = responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F07)), false);
//            String strCMF = "CFM, " + F07_YY;
//            data.append(strCMF);
//            data.append("\n");
//
//
//            String F09 = responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F09)));
//
//            Integer F09Value = Integer.parseInt(F09.substring(1), 16);
//
//            String F09_XXXX = CodeReUse.formatter3Digit.format(Float.valueOf(F09Value) / 1000);
//
//            if (F09.startsWith("8"))
//                F09_XXXX = "-" + F09_XXXX;
//            else
//                F09_XXXX = "+" + F09_XXXX;
//            String strPressure = "Pressure, " + F09_XXXX;
//            data.append(strPressure);
//            data.append("\n");
//            data.append("\n");
//            data.append(columnNameListAll.toString().replace("[", "").replace("]", ""));
//            data.append("\n");
            if (cursor.getCount() > 0) {
                Log.e("FB Q printed started..", "Row Count : " + cursor.getCount());
                while (cursor.moveToNext()) {

                    if (i == 0) {
                        if (responseHandler.hexToBinary(arrSetpointData.get(0).getS01()).charAt(12) == '1') {
                            data.append("Mode, Pos");
                        } else {
                            data.append("Mode, Neg");
                        }
                        data.append("\n");

                        int S07_YY = Integer.parseInt(responseHandler.hexToString(arrSetpointData.get(0).getS07(), false));
                        int S08_XXXX = Integer.parseInt(responseHandler.hexToString(arrSetpointData.get(0).getS08()));
                        int sumS07_S08 = S07_YY + S08_XXXX;
                        String mStrACH = "ACH, " + sumS07_S08;
                        data.append(mStrACH);
                        data.append("\n");

                        String F07_YY = responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F07)), false);
                        String strCMF = "CFM, " + F07_YY;
                        data.append(strCMF);
                        data.append("\n");

//                        String F09 = feedbackArrayList.get(0).getF09();
                        //String F09 = responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F09)));
                        String F09 = cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F09));
                        String a = F09.substring(1, 4);
                        a = F09.substring(1);

                        Integer F09Value = Integer.parseInt(F09.substring(1), 16);

                        String F09_XXXX = CodeReUse.formatter3Digit.format(Float.valueOf(F09Value) / 1000);

                        if (F09.startsWith("8"))
                            F09_XXXX = "-" + F09_XXXX;
                        else
                            F09_XXXX = "+" + F09_XXXX;
                        String strPressure = "Pressure, " + F09_XXXX;
                        data.append(strPressure);
                        data.append("\n");
                        data.append("\n");
                        data.append("\n");
                        data.append(columnNameListAll.toString().replace("[", "").replace("]", ""));
                        data.append("\n");
                    }

                    data.append(arrRackSetUpData.get(0).getBlowerName() + ",");
                    data.append(getSerialNumber() + ",");
                    data.append(arrRackSetUpData.get(0).getModelNo() + ",");
                    data.append(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_CREATEDON)) + ",");
                    data.append(responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F08))) + ",");

                    if (responseHandler.hexToBinary(arrSetpointData.get(0).getS01()).charAt(11) == '1') {
                        data.append(responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F01)), true) + " C" + ",");
                    } else {
                        data.append(responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F01)), true) + " F" + ",");
                    }

                    data.append(responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F02)), true) + " %" + ",");

                    if (responseHandler.hexToBinary(arrSetpointData.get(0).getS01()).charAt(11) == '1') {
                        data.append(responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F01)), false) + " C" + ",");
                    } else {
                        data.append(responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F01)), false) + " F" + ",");
                    }

                    data.append(responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F02)), false) + " %" + ",");

                    data.append(responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F05))) + ",");

                    data.append(responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F06))) + ",");

                    if (responseHandler.hexToBinary(arrSetpointData.get(0).getS01()).charAt(9) == '1') {
                        data.append(responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F07)), true) + " CMH" + ",");
                    } else {
                        data.append(responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F07)), true) + " CFM" + ",");
                    }

                    String F04 = responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F04)));

                    if (F04.charAt(0) == '0') {
                        // +
                        float decimal = Float.parseFloat(String.valueOf(responseHandler.binaryToDecimal(Integer.parseInt(F04.substring(2, 16))))) / 1000;
                        data.append("+" + CodeReUse.formatter3Digit.format(decimal) + " WC" + ",");
                    } else {
                        // -
                        float decimal = Float.parseFloat(String.valueOf(responseHandler.binaryToDecimal(Integer.parseInt(F04.substring(2, 16))))) / 1000;
                        data.append("-" + CodeReUse.formatter3Digit.format(decimal) + " WC" + ",");
                    }

                    if ((responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(15) == '1') || (responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(7) == '1')) {
                        data.append("X" + ",");
                    } else {
                        data.append("" + ",");
                    }

                    if ((responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(14) == '1') || (responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(6) == '1')) {
                        data.append("X" + ",");
                    } else {
                        data.append("" + ",");
                    }

                    if (responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(8) == '1') {
                        data.append("X" + ",");
                    } else {
                        data.append("" + ",");
                    }

                    if ((responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(13) == '8') || (responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(5) == '1')) {
                        data.append("X" + ",");
                    } else {
                        data.append("" + ",");
                    }

                    if (cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F15)).charAt(0) == '1') {
                        data.append("X" + ",");
                    } else {
                        data.append("" + ",");
                    }
                    if (cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F15)).charAt(1) == '1') {
                        data.append("X" + ",");
                    } else {
                        data.append("" + ",");
                    }
                    if (cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F15)).charAt(2) == '1') {
                        data.append("X" + ",");
                    } else {
                        data.append("" + ",");
                    }
                    if (cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F15)).charAt(3) == '1') {
                        data.append("X" + ",");
                    } else {
                        data.append("" + ",");
                    }

                    data.append("\n");

                    i++;
                }


            }

            cursor.close();
            Log.e("FB Q printed finished", "-");
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        return data;
    }

    public Cursor getQueryResultsExport(boolean isFromChangeUnit, String startDate, String endDate, boolean isFromMaxMinAvg) {
        String strQuery = "";
        if (isFromChangeUnit) {
            strQuery = "SELECT F01,F02,F08,F12,F15 CreatedOn FROM BLFeedback";
        } else {
            if (isFromMaxMinAvg) {
                strQuery = "SELECT F01,F02,F08,F12,F15,CreatedOn,ModifiedOn FROM BLFeedback WHERE date(CreatedOn) >= '" + startDate + "' " + "AND date(CreatedOn) <= '" + endDate + "' limit 100000";
            } else {
                strQuery = "SELECT F01,F02,F04,F05,F06,F07,F08,F09,F12,F15,CreatedOn FROM BLFeedback WHERE date(CreatedOn) >= '" + startDate + "' " + "AND date(CreatedOn) <= '" + endDate + "' limit 100000";
            }

        }
        Log.e("Query", strQuery);
        Cursor cursor = myDb.getQueryResult(strQuery);
        return cursor;
    }

    /*
    Max min avg total count 107,096 took 34 seconds to view
    10-22 11:42:56.327 10606-10606/com.allentownblower E/FB MaxMin started: -107096
    10-22 11:43:29.130 10606-10606/com.allentownblower E/FB MaxMin finished: -

    All to view total count 107,096 took 58 seconds
    10-22 11:45:14.330 10606-10606/com.allentownblower E/FB Q printed started: -107096
    10-22 11:46:11.221 10606-10606/com.allentownblower E/FB Q printed finished: -
        */
    // TODO :- get BLFeedBack Table Data
    public HashMap<String, Object> getBLFeedbackDataMaxMinAvg(String startDate, String endDate, ArrayList<SetPointCommand> arrSetpointData, ArrayList<RackModel> arrRackSetUpData, ResponseHandler responseHandler, boolean isFromChangeUnit) {

        HashMap<String, Object> objHasMap = new HashMap<>();

        List<RowHeader> rowHeaderList = new ArrayList<>();
        RowHeader header = new RowHeader(String.valueOf(1), "" + 1);
        rowHeaderList.add(header);

        List<List<Cell>> arrayList = new ArrayList<>();

        List<Cell> cellList = new ArrayList<>();
        try {
//            String strQuery = "SELECT F01,F02,F08,F12,CreatedOn,ModifiedOn FROM BLFeedback WHERE date(CreatedOn) >= '" + startDate + "' " + "AND date(CreatedOn) <= '" + endDate + "' limit 100000";
//            Log.e("Query", strQuery);
//            Cursor cursor = myDb.getQueryResult(strQuery);
            Cursor cursor = getQueryResultsExport(isFromChangeUnit, startDate, endDate, true);
            int totalCount = cursor.getCount();
            if (cursor.getCount() > 0) {
                Log.e("FB MaxMin started..", "Row Count : " + totalCount);
                arrayList.clear();
                while (cursor.moveToNext()) {

                    fromDate = startDate;//cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_CREATEDON));
                    toDate = endDate;//cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_MODIFIEDON));

                    getACHMaxMinAvg(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F08)), responseHandler);

                    getSupplyTempMaxMinAvg(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F01)), responseHandler);

                    getSupplyHumidityMaxMinAvg(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F02)), responseHandler);

                    getExhaustTempMaxMinAvg(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F01)), responseHandler);

                    getExhaustHumidityMaxMinAvg(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F02)), responseHandler);

                    if (blowerAlarm != "X") {
                        if ((responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(15) == '1') || (responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(7) == '1')) {
                            blowerAlarm = "X";
                        } else {
                            blowerAlarm = "";
                        }
                    }

                    if (hepaFilterAlarm != "X") {
                        if ((responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(14) == '1') || (responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(6) == '1')) {
                            hepaFilterAlarm = "X";
                        } else {
                            hepaFilterAlarm = "";
                        }
                    }

                    if (preFilterAlarm != "X") {
                        if (responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(8) == '1') {
                            preFilterAlarm = "X";
                        } else {
                            preFilterAlarm = "";
                        }
                    }

                    if (hoseAlarm != "X") {
                        if ((responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(13) == '8') || (responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(5) == '1')) {
                            hoseAlarm = "X";
                        } else {
                            hoseAlarm = "";
                        }
                    }

                    if (supTempAlarm != "X") {
                        if ((cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F15))).charAt(0) == '1') {
                            supTempAlarm = "X";
                        } else {
                            supTempAlarm = "";
                        }
                    }

                    if (supHMDAlarm != "X") {
                        if ((cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F15))).charAt(1) == '1') {
                            supHMDAlarm = "X";
                        } else {
                            supHMDAlarm = "";
                        }
                    }

                    if (extTempAlarm != "X") {
                        if ((cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F15))).charAt(2) == '1') {
                            extTempAlarm = "X";
                        } else {
                            extTempAlarm = "";
                        }
                    }

                    if (extHMDAlarm != "X") {
                        if ((cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F15))).charAt(3) == '1') {
                            extHMDAlarm = "X";
                        } else {
                            extHMDAlarm = "";
                        }
                    }


                }

                DecimalFormat df = new DecimalFormat("#");
                blowerName = arrRackSetUpData.get(0).getBlowerName();
                blowerAddress = getSerialNumber();//arrRackSetUpData.get(0).getBlowerAddress();
                modelNo = arrRackSetUpData.get(0).getModelNo();

                Cell cellSingleBlowerName = new Cell("0", blowerName);
                cellList.add(cellSingleBlowerName);

                Cell cellSingleBlowerAddress = new Cell("1", blowerAddress);
                cellList.add(cellSingleBlowerAddress);

                Cell cellSingleModelNumber = new Cell("2", modelNo);
                cellList.add(cellSingleModelNumber);

                Cell cellSingleFromDate = new Cell("3", fromDate);
                cellList.add(cellSingleFromDate);

                Cell cellSingleToDate = new Cell("4", toDate);
                cellList.add(cellSingleToDate);

                Cell cellSingleMaxACH = new Cell("5", String.valueOf(maxACH));
                cellList.add(cellSingleMaxACH);

                Cell cellSingleMinACH = new Cell("6", String.valueOf(minACH));
                cellList.add(cellSingleMinACH);

                Cell cellSingleAvgACH = new Cell("7", String.valueOf(df.format(avgACH / totalCount)));
                cellList.add(cellSingleAvgACH);

                String tempUnit = " C";
                if (responseHandler.hexToBinary(arrSetpointData.get(0).getS01()).charAt(11) == '1') {
                    tempUnit = " C";
                } else {
                    tempUnit = " F";
                }

                Cell cellSingleMaxSupplyTemp = new Cell("8", maxSupplyTemp + tempUnit);
                cellList.add(cellSingleMaxSupplyTemp);

                Cell cellSingleMinSupplyTemp = new Cell("9", minSupplyTemp + tempUnit);
                cellList.add(cellSingleMinSupplyTemp);

                Cell cellSingleAvgSupplyTemp = new Cell("10", df.format(avgSupplyTemp / totalCount) + tempUnit);
                cellList.add(cellSingleAvgSupplyTemp);

                Cell cellSingleMaxSupplyHumidity = new Cell("11", maxSupplyHumidity + " %");
                cellList.add(cellSingleMaxSupplyHumidity);

                Cell cellSingleMinSupplyHumidity = new Cell("12", minSupplyHumidity + " %");
                cellList.add(cellSingleMinSupplyHumidity);

                Cell cellSingleAvgSupplyHumidity = new Cell("13", df.format(avgSupplyHumidity / totalCount) + " %");
                cellList.add(cellSingleAvgSupplyHumidity);

                Cell cellSingleMaxExhaustTemp = new Cell("14", maxExhaustTemp + tempUnit);
                cellList.add(cellSingleMaxExhaustTemp);

                Cell cellSingleMinExhaustTemp = new Cell("15", minExhaustTemp + tempUnit);
                cellList.add(cellSingleMinExhaustTemp);

                Cell cellSingleAvgExhaustTemp = new Cell("16", df.format(avgExhaustTemp / totalCount) + tempUnit);
                cellList.add(cellSingleAvgExhaustTemp);

                Cell cellSingleMaxExhaustHumidity = new Cell("17", maxExhaustHumidity + " %");
                cellList.add(cellSingleMaxExhaustHumidity);

                // minExhaustHumidity
                Cell cellSingleMinExhaustHumidity = new Cell("18", minExhaustHumidity + " %");
                cellList.add(cellSingleMinExhaustHumidity);

                // avgExhaustHumidity
                Cell cellSingleAvgExhaustHumidity = new Cell("19", df.format(avgExhaustHumidity / totalCount) + " %");
                cellList.add(cellSingleAvgExhaustHumidity);

                // Blower Alarm
                Cell cellSingleBlowerAlarm = new Cell("20", blowerAlarm);
                cellList.add(cellSingleBlowerAlarm);

                // Hepa Filter Alarm
                Cell cellSingleHepaFilterAlarm = new Cell("21", hepaFilterAlarm);
                cellList.add(cellSingleHepaFilterAlarm);

                // Pre Filter Alarm
                Cell cellSinglePreFilterAlarm = new Cell("22", preFilterAlarm);
                cellList.add(cellSinglePreFilterAlarm);

                // Hose Alarm
                Cell cellSingleHoseAlarm = new Cell("23", hoseAlarm);
                cellList.add(cellSingleHoseAlarm);

                Cell cellSingleSupTmpAlarm = new Cell("24", supTempAlarm);
                cellList.add(cellSingleSupTmpAlarm);

                Cell cellSingleSupHMDAlarm = new Cell("25", supHMDAlarm);
                cellList.add(cellSingleSupHMDAlarm);

                Cell cellSingleExtTmpAlarm = new Cell("26", extTempAlarm);
                cellList.add(cellSingleExtTmpAlarm);

                Cell cellSingleExtHMDAlarm = new Cell("27", extHMDAlarm);
                cellList.add(cellSingleExtHMDAlarm);

                arrayList.add(cellList);
            }

            cursor.close();
            Log.e("FB MaxMin finished", "-");
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        objHasMap.put("rowHeader", rowHeaderList);
        objHasMap.put("cell", arrayList);
        return objHasMap;
    }

    @SuppressLint("LongLogTag")
    public StringBuilder getBLFeedbackDataMaxMinAvgExport(String startDate, String endDate, ArrayList<SetPointCommand> arrSetpointData, ArrayList<RackModel> arrRackSetUpData, ResponseHandler responseHandler, boolean isFromChangeUnit, ArrayList<String> columnNameListSingle) {
        StringBuilder data = new StringBuilder();
        try {
            Cursor cursor = getQueryResultsExport(isFromChangeUnit, startDate, endDate, true);
            data.append(columnNameListSingle.toString().replace("[", "").replace("]", ""));
            data.append("\n");
            int totalCount = cursor.getCount();
            if (cursor.getCount() > 0) {
                Log.e("FB MaxMin started..", "Row Count : " + totalCount);
                while (cursor.moveToNext()) {

                    fromDate = startDate;//cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_CREATEDON));
                    toDate = endDate;//cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_MODIFIEDON));

                    getACHMaxMinAvg(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F08)), responseHandler);

                    getSupplyTempMaxMinAvg(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F01)), responseHandler);

                    getSupplyHumidityMaxMinAvg(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F02)), responseHandler);

                    getExhaustTempMaxMinAvg(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F01)), responseHandler);

                    getExhaustHumidityMaxMinAvg(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F02)), responseHandler);

                    if (blowerAlarm != "X") {
                        if ((responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(15) == '1') || (responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(7) == '1')) {
                            blowerAlarm = "X";
                        } else {
                            blowerAlarm = "";
                        }
                    }

                    if (hepaFilterAlarm != "X") {
                        if ((responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(14) == '1') || (responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(6) == '1')) {
                            hepaFilterAlarm = "X";
                        } else {
                            hepaFilterAlarm = "";
                        }
                    }

                    if (preFilterAlarm != "X") {
                        if (responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(8) == '1') {
                            preFilterAlarm = "X";
                        } else {
                            preFilterAlarm = "";
                        }
                    }

                    if (hoseAlarm != "X") {
                        if ((responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(13) == '8') || (responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(5) == '1')) {
                            hoseAlarm = "X";
                        } else {
                            hoseAlarm = "";
                        }
                    }

                    if (supTempAlarm != "X") {
                        if ((cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F15))).charAt(0) == '1') {
                            supTempAlarm = "X";
                        } else {
                            supTempAlarm = "";
                        }
                    }

                    if (supHMDAlarm != "X") {
                        if ((cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F15))).charAt(1) == '1') {
                            supHMDAlarm = "X";
                        } else {
                            supHMDAlarm = "";
                        }
                    }

                    if (extTempAlarm != "X") {
                        if ((cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F15))).charAt(2) == '1') {
                            extTempAlarm = "X";
                        } else {
                            extTempAlarm = "";
                        }
                    }

                    if (extHMDAlarm != "X") {
                        if ((cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F15))).charAt(3) == '1') {
                            extHMDAlarm = "X";
                        } else {
                            extHMDAlarm = "";
                        }
                    }

                }

                DecimalFormat df = new DecimalFormat("#");
                String tempUnit = " C";
                blowerName = arrRackSetUpData.get(0).getBlowerName();
                blowerAddress = getSerialNumber();//arrRackSetUpData.get(0).getBlowerAddress();
                modelNo = arrRackSetUpData.get(0).getModelNo();


                data.append(blowerName + ",");
                data.append(blowerAddress + ",");
                data.append(modelNo + ",");
                data.append(fromDate + ",");
                data.append(toDate + ",");
                data.append(String.valueOf(maxACH) + ",");
                data.append(String.valueOf(minACH) + ",");
                data.append(String.valueOf(df.format(avgACH / totalCount)) + ",");

                if (responseHandler.hexToBinary(arrSetpointData.get(0).getS01()).charAt(11) == '1') {
                    tempUnit = " C";
                } else {
                    tempUnit = " F";
                }

                data.append(maxSupplyTemp + tempUnit + ",");
                data.append(minSupplyTemp + tempUnit + ",");
                data.append(df.format(avgSupplyTemp / totalCount) + tempUnit + ",");
                data.append(maxSupplyHumidity + " %" + ",");
                data.append(minSupplyHumidity + " %" + ",");
                data.append(df.format(avgSupplyHumidity / totalCount) + " %" + ",");
                data.append(maxExhaustTemp + tempUnit + ",");
                data.append(minExhaustTemp + tempUnit + ",");
                data.append(df.format(avgExhaustTemp / totalCount) + tempUnit + ",");
                data.append(maxExhaustHumidity + " %" + ",");
                data.append(minExhaustHumidity + " %" + ",");
                data.append(df.format(avgExhaustHumidity / totalCount) + " %" + ",");
                data.append(blowerAlarm + ",");
                data.append(hepaFilterAlarm + ",");
                data.append(preFilterAlarm + ",");
                data.append(hoseAlarm + ",");
                data.append(supTempAlarm + ",");
                data.append(supHMDAlarm + ",");
                data.append(extTempAlarm + ",");
                data.append(extHMDAlarm + ",");
                data.append("\n");
            }

            cursor.close();
            Log.e("FB MaxMin finished", "-");
        } catch (Exception e) {
            Log.e("Error Max Min Export :- ", e.getMessage());
        }
        return data;
    }

    public JSONArray getBLFeedbackDataMaxMinAvg_Email(String startDate, String endDate, ArrayList<SetPointCommand> arrSetpointData, ArrayList<RackModel> arrRackSetUpData, ResponseHandler responseHandler, boolean isFromChangeUnit, RackDetailsModel rackDetailsModel, String email) {
        JSONArray jsArr = new JSONArray();
        JSONObject jsObj = null;
        try {
            Cursor cursor = getQueryResultsExport(isFromChangeUnit, startDate, endDate, true);
            int totalCount = cursor.getCount();
            if (cursor.getCount() > 0) {
                Log.e("FB MaxMin started..", "Row Count : " + totalCount);
                while (cursor.moveToNext()) {
                    jsObj = new JSONObject();
                    fromDate = startDate;//cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_CREATEDON));
                    toDate = endDate;//cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_MODIFIEDON));

                    getACHMaxMinAvg(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F08)), responseHandler);

                    getSupplyTempMaxMinAvg(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F01)), responseHandler);

                    getSupplyHumidityMaxMinAvg(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F02)), responseHandler);

                    getExhaustTempMaxMinAvg(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F01)), responseHandler);

                    getExhaustHumidityMaxMinAvg(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F02)), responseHandler);

                    if (blowerAlarm != "X") {
                        if ((responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(15) == '1') || (responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(7) == '1')) {
                            blowerAlarm = "X";
                        } else {
                            blowerAlarm = "";
                        }
                    }

                    if (hepaFilterAlarm != "X") {
                        if ((responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(14) == '1') || (responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(6) == '1')) {
                            hepaFilterAlarm = "X";
                        } else {
                            hepaFilterAlarm = "";
                        }
                    }

                    if (preFilterAlarm != "X") {
                        if (responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(8) == '1') {
                            preFilterAlarm = "X";
                        } else {
                            preFilterAlarm = "";
                        }
                    }

                    if (hoseAlarm != "X") {
                        if ((responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(13) == '8') || (responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(5) == '1')) {
                            hoseAlarm = "X";
                        } else {
                            hoseAlarm = "";
                        }
                    }

                    if (supTempAlarm != "X") {
                        if ((cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F15))).charAt(0) == '1') {
                            supTempAlarm = "X";
                        } else {
                            supTempAlarm = "";
                        }
                    }

                    if (supHMDAlarm != "X") {
                        if ((cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F15))).charAt(1) == '1') {
                            supHMDAlarm = "X";
                        } else {
                            supHMDAlarm = "";
                        }
                    }

                    if (extTempAlarm != "X") {
                        if ((cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F15))).charAt(2) == '1') {
                            extTempAlarm = "X";
                        } else {
                            extTempAlarm = "";
                        }
                    }

                    if (extHMDAlarm != "X") {
                        if ((cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F15))).charAt(3) == '1') {
                            extHMDAlarm = "X";
                        } else {
                            extHMDAlarm = "";
                        }
                    }

                }

                DecimalFormat df = new DecimalFormat("#");
                String tempUnit = " C";
                blowerName = arrRackSetUpData.get(0).getBlowerName();
                blowerAddress = arrRackSetUpData.get(0).getBlowerAddress();
                modelNo = arrRackSetUpData.get(0).getModelNo();

                jsObj.put(ApiHandler.strGetSendEmailFromDate, fromDate);
                jsObj.put(ApiHandler.strGetSendEmailToDate, toDate);
                jsObj.put(ApiHandler.strGetSendEmail_EmailIDs, email);
                jsObj.put(ApiHandler.strUpdateRackBlowerDetailsId, rackDetailsModel.getmId());
                jsObj.put(ApiHandler.strUpdateRackBlowerCustomerID, rackDetailsModel.getmRackBlowerCustomerID());

                jsObj.put("Blower Name", blowerName);
                jsObj.put("Blower Address", blowerAddress);
                jsObj.put("Rack Model", modelNo);
                jsObj.put("From Date", fromDate);
                jsObj.put("To Date", toDate);
                jsObj.put("Max ACH", String.valueOf(maxACH));
                jsObj.put("Min ACH", String.valueOf(minACH));
                jsObj.put("Avg ACH", String.valueOf(df.format(avgACH / totalCount)));
                if (responseHandler.hexToBinary(arrSetpointData.get(0).getS01()).charAt(11) == '1') {
                    tempUnit = " C";
                } else {
                    tempUnit = " F";
                }

                jsObj.put("Max Supply Temp", maxSupplyTemp + tempUnit);
                jsObj.put("Min Supply Temp", minSupplyTemp + tempUnit);
                jsObj.put("Avg Supply Temp", df.format(avgSupplyTemp / totalCount) + tempUnit);
                jsObj.put("Max Supply Humidity", maxSupplyHumidity + " %");
                jsObj.put("Min Supply Humidity", minSupplyHumidity + " %");
                jsObj.put("Avg Supply Humidity", df.format(avgSupplyHumidity / totalCount) + " %");
                jsObj.put("Max Exhaust Temp", maxExhaustTemp + tempUnit);
                jsObj.put("Min Exhaust Temp", minExhaustTemp + tempUnit);
                jsObj.put("Avg Exhaust Temp", df.format(avgExhaustTemp / totalCount) + tempUnit);
                jsObj.put("Max Exhaust Humidity", maxExhaustHumidity + " %");
                jsObj.put("Min Exhaust Humidity", minExhaustHumidity + " %");
                jsObj.put("Avg Exhaust Humidity", df.format(avgExhaustHumidity / totalCount) + " %");
                jsObj.put("Blower Alarm", blowerAlarm);
                jsObj.put("Hepa Filter Alarm", hepaFilterAlarm);
                jsObj.put("PreFilter Alarm", preFilterAlarm);
                jsObj.put("Hose Alarm", hoseAlarm);
                jsObj.put("Supply Temp Alarm", supTempAlarm);
                jsObj.put("Supply Humidity Alarm", supHMDAlarm);
                jsObj.put("Exhaust Temp Alarm", extTempAlarm);
                jsObj.put("Exhaust Humidity Alarm", extHMDAlarm);

                jsArr.put(jsObj);
            }

            cursor.close();
            Log.e("FB MaxMin finished", "-");
        } catch (Exception e) {
            Log.e("Error MaxMin Email :- ", e.getMessage());
        }
        return jsArr;
    }

    public JSONArray getBLFeedbackDataAll_Email(String startDate, String endDate, ArrayList<SetPointCommand> arrSetpointData, ArrayList<RackModel> arrRackSetUpData, ResponseHandler responseHandler, boolean isFromChangeUnit, RackDetailsModel rackDetailsModel, String email) {
        JSONArray jsArr = new JSONArray();
        JSONObject jsObj = null;
        int i = 0;
        try {
            Cursor cursor = getQueryResultsExport(isFromChangeUnit, startDate, endDate, false);
//            if (responseHandler.hexToBinary(arrSetpointData.get(0).getS01()).charAt(12) == '1') {
//                data.append("Mode, Pos");
//            } else {
//                data.append("Mode, Neg");
//            }
//            data.append("\n");
//
//            String S07_YY = String.valueOf(responseHandler.hexToString(arrSetpointData.get(0).getS07(), false));
//            String S08_XXXX = String.valueOf(responseHandler.hexToString(arrSetpointData.get(0).getS08()));
//            String mStrACH = "ACH, " + S07_YY + S08_XXXX;
//            data.append(mStrACH);
//            data.append("\n");
//
//            String F07_YY = responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F07)), false);
//            String strCMF = "CFM, " + F07_YY;
//            data.append(strCMF);
//            data.append("\n");
//
//
//            String F09 = responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F09)));
//
//            Integer F09Value = Integer.parseInt(F09.substring(1), 16);
//
//            String F09_XXXX = CodeReUse.formatter3Digit.format(Float.valueOf(F09Value) / 1000);
//
//            if (F09.startsWith("8"))
//                F09_XXXX = "-" + F09_XXXX;
//            else
//                F09_XXXX = "+" + F09_XXXX;
//            String strPressure = "Pressure, " + F09_XXXX;
//            data.append(strPressure);
//            data.append("\n");
//            data.append("\n");
//            data.append(columnNameListAll.toString().replace("[", "").replace("]", ""));
//            data.append("\n");
            if (cursor.getCount() > 0) {
                Log.e("FB Q printed started..", "Row Count : " + cursor.getCount());
                while (cursor.moveToNext()) {

                    if (i == 0) {
                        jsObj = new JSONObject();

                        jsObj.put(ApiHandler.strGetSendEmailFromDate, startDate);
                        jsObj.put(ApiHandler.strGetSendEmailToDate, endDate);
                        jsObj.put(ApiHandler.strGetSendEmail_EmailIDs, email);
                        jsObj.put(ApiHandler.strUpdateRackBlowerDetailsId, rackDetailsModel.getmId());
                        jsObj.put(ApiHandler.strUpdateRackBlowerCustomerID, rackDetailsModel.getmRackBlowerCustomerID());

                        if (responseHandler.hexToBinary(arrSetpointData.get(0).getS01()).charAt(12) == '1') {

                            jsObj.put("Mode", "Pos");
                        } else {
                            jsObj.put("Mode", "Neg");
                        }

                        int S07_YY = Integer.parseInt(responseHandler.hexToString(arrSetpointData.get(0).getS07(), false));
                        int S08_XXXX = Integer.parseInt(responseHandler.hexToString(arrSetpointData.get(0).getS08()));
                        int sumS07_S08 = S07_YY + S08_XXXX;
                        String mStrACH = String.valueOf(sumS07_S08);
                        jsObj.put("ACH", mStrACH);

                        String F07_YY = responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F07)), false);
                        String strCMF = F07_YY + " CFM";
                        jsObj.put("CFM", strCMF);

//                        String F09 = feedbackArrayList.get(0).getF09();
                        //String F09 = responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F09)));
                        String F09 = cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F09));
                        String a = F09.substring(1, 4);
                        a = F09.substring(1);

                        Integer F09Value = Integer.parseInt(F09.substring(1), 16);

                        String F09_XXXX = CodeReUse.formatter3Digit.format(Float.valueOf(F09Value) / 1000);

                        if (F09.startsWith("8"))
                            F09_XXXX = "-" + F09_XXXX;
                        else
                            F09_XXXX = "+" + F09_XXXX;
                        String strPressure = F09_XXXX;
                        jsObj.put("Pressure", strPressure);
                        jsArr.put(jsObj);
                    }

                    jsObj = new JSONObject();

                    jsObj.put("Blower Name", arrRackSetUpData.get(0).getBlowerName());
                    jsObj.put("Blower Address", arrRackSetUpData.get(0).getBlowerAddress());
                    jsObj.put("Rack Model", arrRackSetUpData.get(0).getModelNo());
                    jsObj.put("Date", cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_CREATEDON)));
                    jsObj.put("ACH", responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F08))));

                    if (responseHandler.hexToBinary(arrSetpointData.get(0).getS01()).charAt(11) == '1') {
                        jsObj.put("Supply Temp", responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F01)), true) + " C");
                    } else {
                        jsObj.put("Supply Temp", responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F01)), true) + " F");
                    }

                    jsObj.put("Supply Humidity", responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F02)), true) + " %");

                    if (responseHandler.hexToBinary(arrSetpointData.get(0).getS01()).charAt(11) == '1') {
                        jsObj.put("Exhaust Temp", responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F01)), false) + " C");
                    } else {
                        jsObj.put("Exhaust Temp", responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F01)), false) + " F");
                    }

                    jsObj.put("Exhaust Humidity", responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F02)), false) + " %");

                    jsObj.put("RPM -S", responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F05))));

                    jsObj.put("RPM -E", responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F06))));

                    if (responseHandler.hexToBinary(arrSetpointData.get(0).getS01()).charAt(9) == '1') {
                        jsObj.put("AirFlow", responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F07)), true) + " CMH");
                    } else {
                        jsObj.put("AirFlow", responseHandler.hexToString(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F07)), true) + " CFM");
                    }

                    String F04 = responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F04)));

                    if (F04.charAt(0) == '0') {
                        // +
                        float decimal = Float.parseFloat(String.valueOf(responseHandler.binaryToDecimal(Integer.parseInt(F04.substring(2, 16))))) / 1000;
                        jsObj.put("Pressure", "+" + CodeReUse.formatter3Digit.format(decimal) + " WC");
                    } else {
                        // -
                        float decimal = Float.parseFloat(String.valueOf(responseHandler.binaryToDecimal(Integer.parseInt(F04.substring(2, 16))))) / 1000;
                        jsObj.put("Pressure", "-" + CodeReUse.formatter3Digit.format(decimal) + " WC");
                    }

                    if (responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(15) == '1') {
                        jsObj.put("Supply Blower Alarm", "X");
                    } else {
                        jsObj.put("Supply Blower Alarm", "");
                    }

                    if (responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(7) == '1') {
                        jsObj.put("Exhuast Blower Alarm", "X");
                    } else {
                        jsObj.put("Exhuast Blower Alarm", "");
                    }

                    if ((responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(14) == '1')) {
                        jsObj.put("Supply Hepa Filter Alarm", "X");
                    } else {
                        jsObj.put("Supply Hepa Filter Alarm", "");
                    }

                    if (responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(6) == '1') {
                        jsObj.put("Exhaust Hepa Filter Alarm", "X");
                    } else {
                        jsObj.put("Exhaust Hepa Filter Alarm", "");
                    }

                    if ((responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(5) == '1')) {
                        jsObj.put("Exhaust Hose Alarm", "X");
                    } else {
                        jsObj.put("Exhaust Hose Alarm", "");
                    }

                    if (responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(8) == '1') {
                        jsObj.put("PreFilter Alarm", "X");
                    } else {
                        jsObj.put("PreFilter Alarm", "");
                    }

//                    if ((responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(13) == '8') || (responseHandler.hexToBinary(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12))).charAt(5) == '1')) {
//                        jsObj.put("Hose Alarm","X");
//                    } else {
//                        jsObj.put("Hose Alarm","");
//                    }
                    if (cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F15)).charAt(0) == '1') {
                        jsObj.put("Supply Temp Alarm", "X");
                    } else {
                        jsObj.put("Supply Temp Alarm", "");
                    }
                    if (cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F15)).charAt(1) == '1') {
                        jsObj.put("Supply Humidity Alarm", "X");
                    } else {
                        jsObj.put("Supply Humidity Alarm", "");
                    }
                    if (cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F15)).charAt(2) == '1') {
                        jsObj.put("Exhaust Temp Alarm", "X");
                    } else {
                        jsObj.put("Exhaust Temp Alarm", "");
                    }
                    if (cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F15)).charAt(3) == '1') {
                        jsObj.put("Exhaust Humidity Alarm", "X");
                    } else {
                        jsObj.put("Exhaust Humidity Alarm", "");
                    }
                    i++;
                    jsArr.put(jsObj);
                }
            }

            cursor.close();
            Log.e("FB Q printed finished", "-");
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        return jsArr;
    }

    private void getExhaustHumidityMaxMinAvg(String string, ResponseHandler responseHandler) {
        int exhaustValueHumidity = Integer.parseInt(responseHandler.hexToString(string, false));

        avgExhaustHumidity = avgExhaustHumidity + exhaustValueHumidity;

        if (maxExhaustHumidity == 0) {
            maxExhaustHumidity = exhaustValueHumidity;
        } else if (exhaustValueHumidity > maxExhaustHumidity) {
            maxExhaustHumidity = exhaustValueHumidity;
        }

        if (minExhaustHumidity == 0) {
            minExhaustHumidity = exhaustValueHumidity;
        } else if (exhaustValueHumidity < minExhaustHumidity) {
            minExhaustHumidity = exhaustValueHumidity;
        }
    }

    private void getExhaustTempMaxMinAvg(String string, ResponseHandler responseHandler) {
        int exhaustValueTemp = Integer.parseInt(responseHandler.hexToString(string, false));

        avgExhaustTemp = avgExhaustTemp + exhaustValueTemp;

        if (maxExhaustTemp == 0) {
            maxExhaustTemp = exhaustValueTemp;
        } else if (exhaustValueTemp > maxExhaustTemp) {
            maxExhaustTemp = exhaustValueTemp;
        }

        int minExhaustValueTemp = 0;

        if (minExhaustTemp == 0) {
            minExhaustTemp = exhaustValueTemp;
        } else if (exhaustValueTemp < minExhaustTemp) {
            minExhaustTemp = exhaustValueTemp;
        }
    }

    private void getSupplyHumidityMaxMinAvg(String string, ResponseHandler responseHandler) {
        int supplyValueHumidity = Integer.parseInt(responseHandler.hexToString(string, true));
        avgSupplyHumidity = avgSupplyHumidity + supplyValueHumidity;

        if (maxSupplyHumidity == 0) {
            maxSupplyHumidity = supplyValueHumidity;
        } else if (supplyValueHumidity > maxSupplyHumidity) {
            maxSupplyHumidity = supplyValueHumidity;
        }

        if (minSupplyHumidity == 0) {
            minSupplyHumidity = supplyValueHumidity;
        } else if (supplyValueHumidity < minSupplyHumidity) {
            minSupplyHumidity = supplyValueHumidity;
        }
    }

    private void getSupplyTempMaxMinAvg(String string, ResponseHandler responseHandler) {

        int supplyValueTemp = Integer.parseInt(responseHandler.hexToString(string, true));
        avgSupplyTemp = avgSupplyTemp + supplyValueTemp;

        if (maxSupplyTemp == 0) {
            maxSupplyTemp = supplyValueTemp;
        } else if (supplyValueTemp > maxSupplyTemp) {
            maxSupplyTemp = supplyValueTemp;
        }

        if (minSupplyTemp == 0) {
            minSupplyTemp = supplyValueTemp;
        } else if (supplyValueTemp < minSupplyTemp) {
            minSupplyTemp = supplyValueTemp;
        }
    }

    public void getACHMaxMinAvg(String str, ResponseHandler responseHandler) {
        int achValue = Integer.parseInt(responseHandler.hexToString(str));
        avgACH = avgACH + achValue;

        if (maxACH == 0) {
            maxACH = achValue;
        } else if (achValue > maxACH) {
            maxACH = achValue;
        }

        if (minACH == 0) {
            minACH = achValue;
        } else if (achValue < minACH) {
            minACH = achValue;
        }
    }

    public int getBLFeedBackCount() {
        String strQuery = "Select count(ID) as totalRecords FROM " + DatabaseTable.TBL_BLFEEDBACK;
        Log.e("strQuery", strQuery);
        Cursor cursor = myDb.getQueryResult(strQuery);
        int totalRecords = 0;
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                totalRecords = cursor.getInt(cursor.getColumnIndex("totalRecords"));
            }
        }
        Log.e("totalRecords", "" + totalRecords);
        return totalRecords;
    }

    // TODO :- get BLRackSetUp Table Data
    public ArrayList<RackModel> getBLRackSetUpData(String startDate, String endDate) {

        ArrayList<RackModel> arrayList = new ArrayList<>();

        try {
            String strQuery = "Select * FROM " + DatabaseTable.TBL_BLRACKSETUP + " where " + DatabaseTable.COL_BLRACKSETUP_ISSETUPCOMPLETED + " = 1";
//            String strQuery = "SELECT * FROM BLFeedback WHERE date(CreatedOn)  >= '" + startDate + "' " + "AND date(CreatedOn) <= '" + endDate + "'";
            Log.e("Query", strQuery);
            Cursor cursor = myDb.getQueryResult(strQuery);

            if (cursor.getCount() > 0) {
                arrayList.clear();
                while (cursor.moveToNext()) {
                    RackModel rackModel = new RackModel();
                    rackModel.setId(cursor.getInt(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_ID)));
                    rackModel.setModelNo(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_MODEL_NO)));
                    rackModel.setACH(cursor.getInt(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_ACH)));
                    rackModel.setPolarity(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_POLARITY)));
                    rackModel.setSupplyCFM(cursor.getInt(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_SUPPLYCFM)));
                    rackModel.setExhaustWC(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_EXHAUSTWC)));
                    rackModel.setExhaustCFM(cursor.getInt(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_EXHAUSTCFM)));
                    rackModel.setSpc1(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_SPC1)));
                    rackModel.setSpc2(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_SPC2)));
                    rackModel.setSpc3(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_SPC3)));
                    rackModel.setSpc4(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_SPC4)));
                    rackModel.setIsSetupCompleted(cursor.getInt(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_ISSETUPCOMPLETED)));
                    rackModel.setSetupCompletedDateTime(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_SETUPCOMPLETED_DATETIME)));
                    rackModel.setSetupModifiedDateTime(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_SETUPMODIFIED_DATETIME)));
                    rackModel.setCreatedOn(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_CREATED_ON)));
                    rackModel.setCompanyName(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_COMPANY_NAME)));
                    rackModel.setBlowerName(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_BLOWER_NAME)));
                    rackModel.setBuildingName(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_BUILDING_NAME)));
                    rackModel.setRoomName(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_ROOM_NAME)));
                    rackModel.setBlowerAddress(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLRACKSETUP_IP_ADDRESS)));
                    arrayList.add(rackModel);
                }
            }

            cursor.close();

        } catch (Exception e) {
            e.getMessage();
        }

        return arrayList;
    }

    // TODO :- get BLRecentSetPoint
    public ArrayList<SetPointCommand> getBLSetPointData(String startDate, String endDate) {

        ArrayList<SetPointCommand> arrayList = new ArrayList<>();

        try {
            String strQuery = "SELECT * FROM BLRecentSetPoint";// WHERE date(CreatedOn) >= '" + startDate + "' " + "AND date(CreatedOn) <= '" + endDate + "'";
            Cursor cursor = myDb.getQueryResult(strQuery);

            if (cursor.getCount() > 0) {
                arrayList.clear();
                while (cursor.moveToNext()) {
                    SetPointCommand setPointCommand = new SetPointCommand();
                    setPointCommand.setID(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_ID)));
                    setPointCommand.setS01(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S01)));
                    setPointCommand.setS02(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S02)));
                    setPointCommand.setS03(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S03)));
                    setPointCommand.setS04(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S04)));
                    setPointCommand.setS05(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S05)));
                    setPointCommand.setS06(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S06)));
                    setPointCommand.setS07(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S07)));
                    setPointCommand.setS08(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S08)));
                    setPointCommand.setS09(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S09)));
                    setPointCommand.setS10(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S10)));
                    setPointCommand.setS11(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S11)));
                    setPointCommand.setS12(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S12)));
                    setPointCommand.setS13(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S13)));
                    setPointCommand.setS14(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S14)));
                    setPointCommand.setS15(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S15)));
                    setPointCommand.setS16(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S16)));
                    setPointCommand.setS17(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S17)));
                    setPointCommand.setS18(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S18)));
                    setPointCommand.setS19(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S19)));
                    setPointCommand.setS20(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S20)));
                    setPointCommand.setS21(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S21)));
                    setPointCommand.setS22(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S22)));
                    setPointCommand.setS23(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S23)));
                    setPointCommand.setS24(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S24)));
                    setPointCommand.setS25(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S25)));
                    setPointCommand.setS26(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S26)));
                    setPointCommand.setS27(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S27)));
                    setPointCommand.setCreatedOn(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_CREATEDON)));
                    setPointCommand.setModifiedOn(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_MODIFIEDON)));
                    arrayList.add(setPointCommand);
                }
            }

            cursor.close();
        } catch (Exception e) {
            e.getMessage();
        }

        return arrayList;
    }

    // Last Record BLRecentFeedback
    public ArrayList<FeedbackCommand> getLastFeedbackData() {

        ArrayList<FeedbackCommand> arrayList = new ArrayList<>();

        try {
            String strQuery = "Select * from BLRecentFeedback ORDER BY CreatedOn DESC LIMIT 1";
            Cursor cursor = myDb.getQueryResult(strQuery);

            if (cursor.getCount() > 0) {
                arrayList.clear();
                while (cursor.moveToNext()) {
                    FeedbackCommand feedbackCommand = new FeedbackCommand();
                    feedbackCommand.setID(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_ID)));
                    feedbackCommand.setF01(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F01)));
                    feedbackCommand.setF02(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F02)));
                    feedbackCommand.setF03(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F03)));
                    feedbackCommand.setF04(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F04)));
                    feedbackCommand.setF05(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F05)));
                    feedbackCommand.setF06(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F06)));
                    feedbackCommand.setF07(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F07)));
                    feedbackCommand.setF08(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F08)));
                    feedbackCommand.setF09(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F09)));
                    feedbackCommand.setF10(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F10)));
                    feedbackCommand.setF11(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F11)));
                    feedbackCommand.setF12(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F12)));
                    feedbackCommand.setF13(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F13)));
                    feedbackCommand.setF14(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F14)));
                    feedbackCommand.setF15(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F15)));
                    feedbackCommand.setF16(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F16)));
                    feedbackCommand.setF17(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_F17)));
                    feedbackCommand.setCreatedOn(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_CREATEDON)));
                    feedbackCommand.setModifiedOn(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_MODIFIEDON)));
                    feedbackCommand.setIsSynced(cursor.getInt(cursor.getColumnIndex(DatabaseTable.COL_BLFEED_ISSYNCED)));
                    arrayList.add(feedbackCommand);
                }
            }

            cursor.close();
        } catch (Exception e) {
            e.getMessage();
        }

        return arrayList;
    }

    // Last Record BLRecentSetPoint
    public ArrayList<SetPointCommand> getLastSetPointData() {

        ArrayList<SetPointCommand> arrayList = new ArrayList<>();

        try {
            String strQuery = "Select * from BLRecentSetPoint ORDER BY CreatedOn DESC LIMIT 1";
            Cursor cursor = myDb.getQueryResult(strQuery);

            if (cursor.getCount() > 0) {
                arrayList.clear();
                while (cursor.moveToNext()) {
                    SetPointCommand setPointCommand = new SetPointCommand();
                    setPointCommand.setID(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_ID)));
                    setPointCommand.setS01(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S01)));
                    setPointCommand.setS02(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S02)));
                    setPointCommand.setS03(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S03)));
                    setPointCommand.setS04(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S04)));
                    setPointCommand.setS05(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S05)));
                    setPointCommand.setS06(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S06)));
                    setPointCommand.setS07(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S07)));
                    setPointCommand.setS08(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S08)));
                    setPointCommand.setS09(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S09)));
                    setPointCommand.setS10(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S10)));
                    setPointCommand.setS11(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S11)));
                    setPointCommand.setS12(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S12)));
                    setPointCommand.setS13(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S13)));
                    setPointCommand.setS14(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S14)));
                    setPointCommand.setS15(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S15)));
                    setPointCommand.setS16(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S16)));
                    setPointCommand.setS17(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S17)));
                    setPointCommand.setS18(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S18)));
                    setPointCommand.setS19(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S19)));
                    setPointCommand.setS20(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S20)));
                    setPointCommand.setS21(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S21)));
                    setPointCommand.setS22(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S22)));
                    setPointCommand.setS23(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S23)));
                    setPointCommand.setS24(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S24)));
                    setPointCommand.setS25(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S25)));
                    setPointCommand.setS26(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S26)));
                    setPointCommand.setS27(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S27)));
                    setPointCommand.setCreatedOn(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_CREATEDON)));
                    setPointCommand.setModifiedOn(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_MODIFIEDON)));
                    arrayList.add(setPointCommand);
                }
            }

            cursor.close();
        } catch (Exception e) {
            e.getMessage();
        }

        return arrayList;
    }

    public ArrayList<SetPointCommand> lastRecordFromBLSetPointTable() {
        ArrayList<SetPointCommand> arrayList = new ArrayList<>();
        try {
            String query = "SELECT * FROM BLRecentSetPoint ORDER BY ID DESC LIMIT 1;";
            Cursor cursor = myDb.getQueryResult(query);

            if (cursor.getCount() > 0) {
                arrayList.clear();
                while (cursor.moveToNext()) {
                    SetPointCommand setPointCommand = new SetPointCommand();
                    setPointCommand.setID(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_ID)));
                    setPointCommand.setS01(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S01)));
                    setPointCommand.setS02(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S02)));
                    setPointCommand.setS03(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S03)));
                    setPointCommand.setS04(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S04)));
                    setPointCommand.setS05(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S05)));
                    setPointCommand.setS06(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S06)));
                    setPointCommand.setS07(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S07)));
                    setPointCommand.setS08(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S08)));
                    setPointCommand.setS09(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S09)));
                    setPointCommand.setS10(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S10)));
                    setPointCommand.setS11(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S11)));
                    setPointCommand.setS12(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S12)));
                    setPointCommand.setS13(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S13)));
                    setPointCommand.setS14(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S14)));
                    setPointCommand.setS15(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S15)));
                    setPointCommand.setS16(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S16)));
                    setPointCommand.setS17(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S17)));
                    setPointCommand.setS18(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S18)));
                    setPointCommand.setS19(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S19)));
                    setPointCommand.setS20(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S20)));
                    setPointCommand.setS21(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S21)));
                    setPointCommand.setS22(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S22)));
                    setPointCommand.setS23(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S23)));
                    setPointCommand.setS24(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S24)));
                    setPointCommand.setS25(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S25)));
                    setPointCommand.setS26(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S26)));
                    setPointCommand.setS27(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_S27)));
                    setPointCommand.setCreatedOn(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_CREATEDON)));
                    setPointCommand.setModifiedOn(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLSET_MODIFIEDON)));
                    arrayList.add(setPointCommand);
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return arrayList;
    }

    // Last Record BLDiagnostics
    public ArrayList<DiagnosticsCommand> getLastDiagnosticsData() {

        ArrayList<DiagnosticsCommand> arrayList = new ArrayList<>();

        try {
            String strQuery = "Select * from BLDiagnostics ORDER BY CreatedOn DESC LIMIT 1";
            Cursor cursor = myDb.getQueryResult(strQuery);

            if (cursor.getCount() > 0) {
                arrayList.clear();
                while (cursor.moveToNext()) {
                    DiagnosticsCommand diagnosticsCommand = new DiagnosticsCommand();
                    diagnosticsCommand.setID(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLDIAGNOSTICS_ID)));
                    diagnosticsCommand.setD01(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLDIAGNOSTICS_D01)));
                    diagnosticsCommand.setD02(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLDIAGNOSTICS_D02)));
                    diagnosticsCommand.setD03(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLDIAGNOSTICS_D03)));
                    diagnosticsCommand.setD04(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLDIAGNOSTICS_D04)));
                    diagnosticsCommand.setD05(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLDIAGNOSTICS_D05)));
                    diagnosticsCommand.setD06(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLDIAGNOSTICS_D06)));
                    diagnosticsCommand.setD07(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLDIAGNOSTICS_D07)));
                    diagnosticsCommand.setD08(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLDIAGNOSTICS_D08)));
                    diagnosticsCommand.setD09(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLDIAGNOSTICS_D09)));
                    diagnosticsCommand.setD10(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLDIAGNOSTICS_D10)));
                    diagnosticsCommand.setD11(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLDIAGNOSTICS_D11)));
                    diagnosticsCommand.setD12(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLDIAGNOSTICS_D12)));
                    diagnosticsCommand.setD13(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLDIAGNOSTICS_D13)));
                    diagnosticsCommand.setD14(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLDIAGNOSTICS_D14)));
                    diagnosticsCommand.setD15(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLDIAGNOSTICS_D15)));
                    diagnosticsCommand.setD16(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLDIAGNOSTICS_D16)));
                    diagnosticsCommand.setD17(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLDIAGNOSTICS_D17)));
                    diagnosticsCommand.setD18(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLDIAGNOSTICS_D18)));
                    diagnosticsCommand.setD19(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLDIAGNOSTICS_D19)));
                    diagnosticsCommand.setD20(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLDIAGNOSTICS_D20)));
                    diagnosticsCommand.setD21(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLDIAGNOSTICS_D21)));
                    diagnosticsCommand.setD22(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLDIAGNOSTICS_D22)));
                    diagnosticsCommand.setD23(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLDIAGNOSTICS_D23)));
                    diagnosticsCommand.setD24(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLDIAGNOSTICS_D24)));
                    diagnosticsCommand.setD25(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLDIAGNOSTICS_D25)));
                    diagnosticsCommand.setD26(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLDIAGNOSTICS_D26)));
                    diagnosticsCommand.setD27(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLDIAGNOSTICS_D27)));
                    diagnosticsCommand.setD28(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLDIAGNOSTICS_D28)));
                    diagnosticsCommand.setD29(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLDIAGNOSTICS_D29)));
                    diagnosticsCommand.setD30(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLDIAGNOSTICS_D30)));
                    diagnosticsCommand.setCreatedOn(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLDIAGNOSTICS_CREATEDON)));
                    diagnosticsCommand.setModifiedOn(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLDIAGNOSTICS_MODIFIEDON)));
                    arrayList.add(diagnosticsCommand);
                }
            }

            cursor.close();
        } catch (Exception e) {
            e.getMessage();
        }

        return arrayList;
    }

    // Last Record BLWiFi
    public ArrayList<WiFiCommand> getLastWiFiData() {

        ArrayList<WiFiCommand> arrayList = new ArrayList<>();

        try {
            String strQuery = "Select * from BLWiFi ORDER BY CreatedOn DESC LIMIT 1";
            Cursor cursor = myDb.getQueryResult(strQuery);

            if (cursor.getCount() > 0) {
                arrayList.clear();
                while (cursor.moveToNext()) {
                    WiFiCommand wiFiCommand = new WiFiCommand();
                    wiFiCommand.setID(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLWIFI_ID)));
                    wiFiCommand.setW01(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLWIFI_W01)));
                    wiFiCommand.setW02(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLWIFI_W02)));
                    wiFiCommand.setW03(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLWIFI_W03)));
                    wiFiCommand.setW04(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLWIFI_W04)));
                    wiFiCommand.setW05(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLWIFI_W05)));
                    wiFiCommand.setW06(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLWIFI_W06)));
                    wiFiCommand.setW07(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLWIFI_W07)));
                    wiFiCommand.setW08(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLWIFI_W08)));
                    wiFiCommand.setW09(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLWIFI_W09)));
                    wiFiCommand.setW10(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLWIFI_W10)));
                    wiFiCommand.setW11(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLWIFI_W11)));
                    wiFiCommand.setW12(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLWIFI_W12)));
                    wiFiCommand.setCreatedOn(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLWIFI_CREATEDON)));
                    wiFiCommand.setModifiedOn(cursor.getString(cursor.getColumnIndex(DatabaseTable.COL_BLWIFI_MODIFIEDON)));
                    arrayList.add(wiFiCommand);
                }
            }

            cursor.close();
        } catch (Exception e) {
            e.getMessage();
        }

        return arrayList;
    }

    // Convert Hexadecimal to Decimal
    public String hexToString(String hex, boolean isFirst) {
        String data;
        if (isFirst) {
            if (hex.equals("FF")) {
                data = "";
            } else {
                data = String.valueOf(Integer.parseInt(hex.substring(0, 2), 16));
            }
            return data;
        } else {
            if (hex.equals("FF")) {
                data = "";
            } else {
                data = String.valueOf(Integer.parseInt(hex.substring(2, 4), 16));
            }
            return data;
        }
    }

    // Convert Hexadecimal
    public String getHexString(String hex, boolean isFirst) {
        if (isFirst) {
            return hex.substring(0, 2);
        } else {
            return hex.substring(2, 4);
        }
    }

    // Convert Hexadecimal to Decimal
    public String hexToString(String hex) {
        if (hex.equals("FFFF")) {
            return "0";
        } else {
            return String.valueOf(Integer.parseInt(hex, 16));
        }
    }

    // Convert Decimal to Hexadecimal
    public String stringToHex(String hex, boolean isTwoDigit) {
        String data = String.valueOf(Integer.toHexString(Integer.parseInt(hex)));

        if (isTwoDigit) {
            if (data.length() == 1)
                return "0" + data;
            else
                return data;
        } else {
            if (data.length() == 0)
                return "0000";
            else if (data.length() == 1)
                return "000" + data;
            else if (data.length() == 2)
                return "00" + data;
            else if (data.length() == 3)
                return "0" + data;
            else
                return data;
        }
    }

    public String stringToHexForPaaword(String hex) {
        String data = String.valueOf(Integer.toHexString(Integer.parseInt(hex)));
        return data;
    }

    // Convert Hexadecimal to Binary
    public String hexToBinary(String hex) {
        int i = Integer.parseInt(hex, 16);
        String Bin = Integer.toBinaryString(i);
        if (Bin.length() < 16) {
            Bin = "0" + Bin;
            for (i = 0; i < 16; i++) {
                if (Bin.length() == 16)
                    break;
                else
                    Bin = "0" + Bin;
            }
        }
        return Bin;
    }

    // Convert Binary to Hexadecimal
    public String binaryToHex(String binOutput) {
        return Integer.toHexString(Integer.parseInt(binOutput, 2));
    }

    // Convert Binary to Decimal
    public int binaryToDecimal(int binary) {
        int decimal = 0;
        int n = 0;
        while (true) {
            if (binary == 0) {
                break;
            } else {
                int temp = binary % 10;
                decimal += temp * Math.pow(2, n);
                binary = binary / 10;
                n++;
            }
        }
        return decimal;
    }

    // Convert Time to Millisecond
    public long strToMilli(String strTime) {
        long retVal = 0;
        String hour = strTime.substring(0, 2);
        String min = strTime.substring(3, 5);
        int h = Integer.parseInt(hour);
        int m = Integer.parseInt(min);

        long lH = h * 60 * 60 * 1000;
        long lM = m * 60 * 1000;

        retVal = (lH + lM) / 1000;
        return retVal;
    }

    // Convert Millisecond to Time
    public String milliToStr(long seconds) {
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        return String.format("%02d:%02d", h, m);
    }

    // Convert Millisecond to Time
    public String milliToStrHours(long seconds) {
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        if (h == 12)
            return String.format("%02d:%02d", 12, m) + " PM";
        else if (h > 11)
            return String.format("%02d:%02d", h - 12, m) + " PM";
        else if (h == 00)
            return String.format("%02d:%02d", 12, m) + " AM";
        else
            return String.format("%02d:%02d", h, m) + " AM";
    }

    public static String getString(JSONObject jObj, String strKey) {
        try {
            return (jObj.has(strKey) && !jObj.isNull(strKey)) ? jObj.getString(strKey) : "";
        } catch (Exception e) {
            return "";
        }
    }

    public static int getInt(JSONObject jObj, String strKey) {
        try {
            return (jObj.has(strKey) && !jObj.isNull(strKey)) ? jObj.getInt(strKey) : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public static float getFloat(JSONObject jObj, String strKey) {
        try {
            return (jObj.has(strKey) && !jObj.isNull(strKey)) ? (float) jObj.getDouble(strKey) : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean getBool(JSONObject jObj, String strKey) {
        try {
            return (jObj.has(strKey) && !jObj.isNull(strKey)) && jObj.getBoolean(strKey);
        } catch (Exception e) {
            return false;
        }
    }

    public static JSONObject getJSONObject(JSONObject jObj, String strKey) {
        try {
            return (jObj.has(strKey) && !jObj.isNull(strKey)) ? jObj.getJSONObject(strKey) : new JSONObject();
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    public static JSONArray getJSONArray(JSONObject jObj, String strKey) {
        try {
            return (jObj.has(strKey) && !jObj.isNull(strKey)) ? jObj.getJSONArray(strKey) : new JSONArray();
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    public static String getSerialNumber() {
        String serialNumber;

        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);

            // (?) Lenovo Tab (https://stackoverflow.com/a/34819027/1276306)
            serialNumber = (String) get.invoke(c, "gsm.sn1");

            if (serialNumber.equals(""))
                // Samsung Galaxy S5 (SM-G900F) : 6.0.1
                // Samsung Galaxy S6 (SM-G920F) : 7.0
                // Samsung Galaxy Tab 4 (SM-T530) : 5.0.2
                // (?) Samsung Galaxy Tab 2 (https://gist.github.com/jgold6/f46b1c049a1ee94fdb52)
                serialNumber = (String) get.invoke(c, "ril.serialnumber");

            if (serialNumber.equals(""))
                // Archos 133 Oxygen : 6.0.1
                // Google Nexus 5 : 6.0.1
                // Hannspree HANNSPAD 13.3" TITAN 2 (HSG1351) : 5.1.1
                // Honor 5C (NEM-L51) : 7.0
                // Honor 5X (KIW-L21) : 6.0.1
                // Huawei M2 (M2-801w) : 5.1.1
                // (?) HTC Nexus One : 2.3.4 (https://gist.github.com/tetsu-koba/992373)
                serialNumber = (String) get.invoke(c, "ro.serialno");

            if (serialNumber.equals(""))
                // (?) Samsung Galaxy Tab 3 (https://stackoverflow.com/a/27274950/1276306)
                serialNumber = (String) get.invoke(c, "sys.serialnumber");

            if (serialNumber.equals(""))
                // Archos 133 Oxygen : 6.0.1
                // Hannspree HANNSPAD 13.3" TITAN 2 (HSG1351) : 5.1.1
                // Honor 9 Lite (LLD-L31) : 8.0
                // Xiaomi Mi 8 (M1803E1A) : 8.1.0
                serialNumber = Build.SERIAL;

            // If none of the methods above worked
            if (serialNumber.equals(Build.UNKNOWN))
                serialNumber = "";
        } catch (Exception e) {
            e.printStackTrace();
            serialNumber = "";
        }

        return serialNumber.toUpperCase();
    }

    public String GetServerAPKVersionAPI(String localversion)
    {

        Utility.Log("Local Version from GetServerAPKVersionAPI Call : " + localversion);

            JSONObject objParam = new JSONObject();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    "https://Wicom.Allentownllc.com" + ApiHandler.strUrlGetAPKVersion, objParam,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {

                            Utility.Log("GetAndroidAPKVersion_API Response : " + jsonObject);
                            try {
                                if (jsonObject.has("Version")) {
                                    APKVersion =  jsonObject.getString("Version").toString();
                                    Utility.Log("Server apk version in responsehandler: " + APKVersion);
//                                    if (!APKVersion.equals(""))
//                                    {
//                                        double local= Double.parseDouble(localversion);
//                                        double server = Double.parseDouble(APKVersion);
//                                        if (server > local)
//                                        {
//                                            Utility.showAlertDialog(act,"Newer Version "+ APKVersion + " is available.\nPlease contact Administrator to update the App.", "Ok" );
//                                        }
//                                        else
//                                        {
//                                            Utility.showAlertDialog(act,"No updates avaialble.", "Ok" );
//                                        }
//                                    }
//                                    else
//                                    {
//                                        Utility.Log("Getting empty version in responsehandler: " + APKVersion);
//                                    }

                                }
                            } catch (JSONException e) {
                                Utility.Log("GetAndroidAPKVersion_API Error : " + e.toString());
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Utility.Log("GetAndroidAPKVersionAPI_API Error : " + error.toString());
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    return allentownBlowerApplication.getInstance().getHeader();
                }
            };
            allentownBlowerApplication.getInstance().cancelPendingRequests(PendingID.nGetServerAPKVersion);
            allentownBlowerApplication.getInstance().addToRequestQueue(request, PendingID.nGetServerAPKVersion);


        return APKVersion;
    }



}
