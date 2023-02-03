/*
 * Copyright (c) 2018. Evren Coşkun
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.allentownblower.module;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.os.EnvironmentCompat;

import com.allentownblower.R;
import com.allentownblower.application.AllentownBlowerApplication;
import com.allentownblower.common.ApiHandler;
import com.allentownblower.common.CodeReUse;
import com.allentownblower.common.ObserverActionID;
import com.allentownblower.common.PendingID;
import com.allentownblower.common.PrefManager;
import com.allentownblower.common.ResponseHandler;
import com.allentownblower.common.Utility;
import com.allentownblower.database.SqliteHelper;
import com.allentownblower.volley.VolleyMultipartRequest;
import com.allentownblower.volley.VolleySingleton;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileOutputStream;
import com.koushikdutta.async.http.AsyncHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

//import com.evrencoskun.tableviewsample.R;
//import com.evrencoskun.tableviewsample.tableview.model.Cell;
//import com.evrencoskun.tableviewsample.tableview.model.ColumnHeader;
//import com.evrencoskun.tableviewsample.tableview.model.RowHeader;

/**
 * Created by evrencoskun on 4.02.2018.
 */

public class TableViewModel {
    SqliteHelper sqliteHelper;
    private Activity act;
    private String startDate, endDate;
    ArrayList<String> columnNameListSingle = new ArrayList<String>();
    ArrayList<String> columnNameListAll = new ArrayList<String>();
    private ArrayList<SetPointCommand> setpointArrayList = new ArrayList<>();
    private ArrayList<RackModel> rackModelArrayList = new ArrayList<>();
    List<List<Cell>> listSingle = new ArrayList<>();
    List<List<Cell>> listAll = new ArrayList<>();

    public TableViewModel(Activity activity, String mStartDate, String mEndDate) {
        act = activity;
        startDate = mStartDate;
        endDate = mEndDate;
    }

    public TableViewModel(Activity activity, String mStartDate, String mEndDate, SqliteHelper mSqliteHelper) {
        act = activity;
        startDate = mStartDate;
        endDate = mEndDate;
        sqliteHelper = mSqliteHelper;
    }

    @NonNull
    private List<ColumnHeader> getRandomColumnHeaderListForAllRecord() {
        List<ColumnHeader> list = new ArrayList<>();
//        columnNameListAll.add("ID");
        columnNameListAll.add("Blower Name");
        columnNameListAll.add("Blower Address");
        columnNameListAll.add("Rack Model");
        columnNameListAll.add("Date");
        columnNameListAll.add("ACH");
        columnNameListAll.add("Supply Temp");
        columnNameListAll.add("Supply Humidity");
        columnNameListAll.add("Exhaust Temp");
        columnNameListAll.add("Exhaust Humidity");
        columnNameListAll.add("RPM -S");
        columnNameListAll.add("RPM -E");
        columnNameListAll.add("AirFlow");
        columnNameListAll.add("Pressure");
        columnNameListAll.add("Blower Alarm");
        columnNameListAll.add("Hepa Filter Alarm");
        columnNameListAll.add("PreFilter Alarm");
        columnNameListAll.add("Hose Alarm");
        columnNameListAll.add("Supply Temp Alarm");
        columnNameListAll.add("Supply Humidity Alarm");
        columnNameListAll.add("Exhaust Temp Alarm");
        columnNameListAll.add("Exhaust Humidity Alarm");
        String title = "";
        for (int i = 0; i <= columnNameListAll.size() - 1; i++) {
            ColumnHeader header = new ColumnHeader(String.valueOf(i), columnNameListAll.get(i));
            list.add(header);
        }

        return list;
    }

    @NonNull
    private List<ColumnHeader> getRandomColumnHeaderListForMaxMinAvgRecord() {
        List<ColumnHeader> list = new ArrayList<>();
        columnNameListSingle.add("Blower Name");
        columnNameListSingle.add("Blower Address");
        columnNameListSingle.add("Rack Model");
        columnNameListSingle.add("From Date");
        columnNameListSingle.add("To Date");
        columnNameListSingle.add("Max ACH");
        columnNameListSingle.add("Min ACH");
        columnNameListSingle.add("Avg ACH");
        columnNameListSingle.add("Max Supply Temp");
        columnNameListSingle.add("Min Supply Temp");
        columnNameListSingle.add("Avg Supply Temp");
        columnNameListSingle.add("Max Supply Humidity");
        columnNameListSingle.add("Min Supply Humidity");
        columnNameListSingle.add("Avg Supply Humidity");
        columnNameListSingle.add("Max Exhaust Temp");
        columnNameListSingle.add("Min Exhaust Temp");
        columnNameListSingle.add("Avg Exhaust Temp");
        columnNameListSingle.add("Max Exhaust Humidity");
        columnNameListSingle.add("Min Exhaust Humidity");
        columnNameListSingle.add("Avg Exhaust Humidity");
        columnNameListSingle.add("Blower Alarm");
        columnNameListSingle.add("Hepa Filter Alarm");
        columnNameListSingle.add("PreFilter Alarm");
        columnNameListSingle.add("Hose Alarm");
        columnNameListSingle.add("Supply Temp Alarm");
        columnNameListSingle.add("Supply Humidity Alarm");
        columnNameListSingle.add("Exhaust Temp Alarm");
        columnNameListSingle.add("Exhaust Humidity Alarm");
        for (int i = 0; i <= columnNameListSingle.size() - 1; i++) {
            ColumnHeader header = new ColumnHeader(String.valueOf(i), columnNameListSingle.get(i));
            list.add(header);
        }

        return list;
    }

    /**
     * This is a dummy model list test some cases.
     */
    @NonNull
    private HashMap<String, Object> getCellListForAllRecord() {

        ResponseHandler responseHandler = new ResponseHandler(act);
//        feedbackArrayList = responseHandler.getBLFeedbackData(startDate, endDate);
        Log.e("TAG","in first");
        setpointArrayList = responseHandler.getBLSetPointData(startDate, endDate);
        Log.e("TAG","in second");
        rackModelArrayList = responseHandler.getBLRackSetUpData(startDate, endDate);
        Log.e("TAG","in third");
        HashMap<String, Object> listForRowHeaderAndCell = responseHandler.getBLFeedbackDataAll(startDate, endDate, setpointArrayList, rackModelArrayList, responseHandler, false);
        Log.e("TAG","in forth");
        listAll = (List<List<Cell>>) listForRowHeaderAndCell.get("cell");
        Log.e("TAG","in fifth");
        Log.e("Event END", "getCellListForSortingTest");
        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nReportViewBindSuccessfully);
        Log.e("TAG","in sixth");
        return listForRowHeaderAndCell;
    }

    @NonNull
    private HashMap<String, Object> getCellListForMaxMinAvgRecord() {
        ResponseHandler responseHandler = new ResponseHandler(act);

        setpointArrayList = responseHandler.getBLSetPointData(startDate, endDate);
        rackModelArrayList = responseHandler.getBLRackSetUpData(startDate, endDate);
        HashMap<String, Object> listForRowHeaderAndCell = responseHandler.getBLFeedbackDataMaxMinAvg(startDate, endDate, setpointArrayList, rackModelArrayList, responseHandler, false);
        listSingle = (List<List<Cell>>) listForRowHeaderAndCell.get("cell");
        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nReportViewBindSuccessfully);
        return listForRowHeaderAndCell;
    }

    @NonNull
    public HashMap<String, Object> getCellList(String tag, boolean isFromChangeUnit) {
        //tag == 1 means Max/Min/Avg
        if (tag.equals("1")) {
            return getCellListForMaxMinAvgRecord();
        } else {
            return getCellListForAllRecord();
        }
    }

    @NonNull
    public List<ColumnHeader> getColumnHeaderList(String tag) {
        //tag == 1 means Max/Min/Avg
        if (tag.equals("1")) {
            return getRandomColumnHeaderListForMaxMinAvgRecord();
        } else {
            return getRandomColumnHeaderListForAllRecord();
        }
    }

    // Report File Export Function
    @SuppressLint("NewApi")
    public void csvFileExportFunction(String tag, boolean isFromChangeUnit, boolean isUSBDetected) {
        try {
            StringBuilder data = new StringBuilder();
            ResponseHandler responseHandler = new ResponseHandler(act);
            //feedbackArrayList = responseHandler.getBLFeedbackData(startDate, endDate);
            setpointArrayList = responseHandler.getBLSetPointData(startDate, endDate);
            rackModelArrayList = responseHandler.getBLRackSetUpData(startDate, endDate);
            if (tag.equals("1")) {
                getColumnHeaderList(tag);
                data = responseHandler.getBLFeedbackDataMaxMinAvgExport(startDate, endDate, setpointArrayList, rackModelArrayList, responseHandler, isFromChangeUnit, columnNameListSingle);
            } else {
                getColumnHeaderList(tag);
                data = responseHandler.getBLFeedbackDataAllExport(startDate, endDate, setpointArrayList, rackModelArrayList, responseHandler, isFromChangeUnit, columnNameListAll);
            }
            Boolean isSDPresent = false;
            File[] filedirs = act.getApplicationContext().getExternalFilesDirs(null);
            if (filedirs.length > 0) {
                for (File file : filedirs) {
                    String sdCard = Environment.getExternalStorageState().toString();
                    if (file != null) {
                        if (sdCard.equals(Environment.MEDIA_MOUNTED)) {
                            sdCard = file.toString();
                            String sdCardPath = file.getPath();
                            if (!sdCardPath.contains("emulated")) {
                                isSDPresent = true;
                                break;
                            } else {
                                isSDPresent = false;
                            }
                        }
                    }
                }
            }
            if (isUSBDetected) {
                UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(act);
                FileSystem currentFs = null;
                if (devices.length == 1)
                {
                    for (UsbMassStorageDevice device : devices) {
                        // before interacting with a device you need to call init()!
                        // Only uses the first partition on the device
                        try {
                            device.init();
                            currentFs = device.getPartitions().get(0).getFileSystem();
                            UsbFile root = currentFs.getRootDirectory();
                            String folderName = "";
                            UsbFile directory = null;

                            folderName = CodeReUse.folderName;
                            directory = root.search(folderName);
                            if (directory != null) {
                                directory = root.search(folderName);
                            } else {
                                directory = root.createDirectory(folderName);
                            }

                            Calendar calendar = Calendar.getInstance();
                            String createon = null;
                            createon = CodeReUse.formatreport.format(calendar.getTime());
                            //createon = createon.replace(" ", "_");
                            createon = createon.replace(":", "");
                            UsbFile file = directory.createFile(createon + "_reportfile.csv");
                            OutputStream os = new UsbFileOutputStream(file);
                            os.write(data.toString().getBytes());
                            os.close();
//                            File files[] = new File(directory.getAbsolutePath()).listFiles();
//                            File reportfile = new File(directory.getAbsolutePath()+createon + "_reportfile.csv");
//                            JSONHttpClient jsonHttpClient = new JSONHttpClient();
//                            AsyncHttpClient client = new AsyncHttpClient();


                            Utility.ShowMessage(act,"Success",createon + "_reportfile.csv" + "  File has been exported to USB.","Ok");
                            Utility.dismissAlertDialog();
                            //Toast.makeText(act, "File exported successfully..!", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            if (e.getMessage().equals("Index: 0, Size: 0")) {
                                Toast.makeText(act, "Please make sure pendrive is MS-DOS (FAT) (Master Boot record) formatted", Toast.LENGTH_LONG).show();
                            } else {
                                Log.e("Error USB Export", e.getMessage());
                                Toast.makeText(act, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                            Utility.dismissAlertDialog();
                        }
                    }
                }
            }
            else if (isSDPresent)
            {
                if (filedirs.length > 0)
                {
                    for (File file : filedirs) {
                        String sdCard = Environment.getExternalStorageState().toString();
                        if (file != null) {
                            if (sdCard.equals(Environment.MEDIA_MOUNTED)) {
                                sdCard = file.toString();
                                String sdCardPath = file.getPath();

                                if (!sdCardPath.contains("emulated")) {

                                    File dir = new File(sdCardPath + File.separator + CodeReUse.folderName + File.separator);
                                    Calendar calendar = Calendar.getInstance();
                                    String createon = null;
                                    try {
                                        createon = CodeReUse.formatreport.format(calendar.getTime());
                                        createon = createon.replace(":", "");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    //verifyStoragePermissions(act);
                                    File fileLocation = new File(dir, createon + "_reportfile.csv");
//                                String path = file.getPath().split("/Android")[0];
//                                Log.e("TAG","Inside path : " + path); //Inside path : /storage/47C4-1719 i tried to create directory here but it doesn't allow. so i will keep it as default path..
                                    if (!dir.exists()) {
                                        try {
                                            if (dir.mkdir())
                                                Log.e("Directory Created.", "" + dir.toString());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (fileLocation.exists()) {
                                        try {
                                            final String dataString = new String(data.toString().getBytes());
                                            FileOutputStream fout = new FileOutputStream(fileLocation);
                                            OutputStreamWriter osw = new OutputStreamWriter(fout);

                                            //Writing the string to file
                                            osw.write(dataString);
                                            osw.flush();
                                            osw.close();

                                            if (isFromChangeUnit) {
                                                sqliteHelper.deleteAllRecordFromAllTable(true);
                                            } else {
                                                Utility.dismissAlertDialog();
                                                if (!isUSBDetected)
                                                    Utility.ShowMessage(act, "Success", createon + "_reportfile.csv" + "  File has been exported to External SD Card.", "Ok");
                                                //Toast.makeText(act, "File exported successfully..!", Toast.LENGTH_LONG).show();
                                            }

                                        } catch (Exception e) {
                                            Log.e("ErrorEx", e.getMessage());
                                            Utility.dismissAlertDialog();
                                        }
                                    } else {
                                        try {
                                            final String dataString = new String(data.toString().getBytes());
                                            if (fileLocation.createNewFile())
                                                Log.e("File Created.", "" + fileLocation.toString());


                                            FileOutputStream fout = new FileOutputStream(fileLocation);
                                            OutputStreamWriter osw = new OutputStreamWriter(fout);

                                            //Writing the string to file
                                            osw.write(dataString);
                                            osw.flush();
                                            osw.close();
                                            //exporting
                                            if (isFromChangeUnit) {
                                                sqliteHelper.deleteAllRecordFromAllTable(true);
                                            } else {
                                                Utility.dismissAlertDialog();
                                                if (!isUSBDetected)
                                                    Utility.ShowMessage(act, "Success", createon + "_reportfile.csv" + "  File has been exported to External SD Card.", "Ok");
                                                //Toast.makeText(act, "File exported successfully..!", Toast.LENGTH_LONG).show();
                                            }

                                        } catch (Exception e) {
                                            Log.e("ErrorEx", e.getMessage());
                                            Utility.dismissAlertDialog();
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
                Utility.dismissAlertDialog();
//                String sdCard = Environment.getExternalStorageState().toString();
//                if (sdCard.equals(Environment.MEDIA_MOUNTED)) {
//                    String sdCardPath = Environment.getExternalStorageDirectory().getPath();
//                    //String sdCardPath = getExternalSdCardPath();
//                    Log.e("sdCardPath", ""+sdCardPath);
//                    File dir = new File(sdCardPath + File.separator + CodeReUse.folderName);
//                    Calendar calendar = Calendar.getInstance();
//
//                    String createon = null;
//                    try {
//                        createon = CodeReUse.formatreport.format(calendar.getTime());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                    File fileLocation = new File(dir, createon + "_reportfile.csv");
//                    if (!dir.exists()) {
//                        dir.mkdirs();
//                    }
//
//                    if (fileLocation.exists()) {
//                        try {
//                            final String dataString = new String(data.toString().getBytes());
//                            FileOutputStream fout = new FileOutputStream(fileLocation);
//                            OutputStreamWriter osw = new OutputStreamWriter(fout);
//
//                            osw.write(dataString);
//                            osw.flush();
//                            osw.close();
//
//                            if (isFromChangeUnit) {
//                                sqliteHelper.deleteAllRecordFromAllTable(true);
//                            } else {
//                                Utility.dismissAlertDialog();
//                                Toast.makeText(act, "File exported successfully..!", Toast.LENGTH_LONG).show();
//                            }
//
//                        } catch (Exception e) {
//                            Log.e("ErrorEx", e.getMessage());
//                            Utility.dismissAlertDialog();
//                        }
//                    } else {
//                        try {
//                            final String dataString = new String(data.toString().getBytes());
//                            FileOutputStream fout = new FileOutputStream(fileLocation);
//                            OutputStreamWriter osw = new OutputStreamWriter(fout);
//
//                            osw.write(dataString);
//                            osw.flush();
//                            osw.close();
//                            if (isFromChangeUnit) {
//                                sqliteHelper.deleteAllRecordFromAllTable(true);
//                            } else {
//                                Utility.dismissAlertDialog();
//                                Toast.makeText(act, "File exported successfully..!", Toast.LENGTH_LONG).show();
//                            }
//
//                        } catch (Exception e) {
//                            Log.e("ErrorEx", e.getMessage());
//                            Utility.dismissAlertDialog();
//                        }
//                    }
//
//                }
//                else
//                    {
//                    String sdCardPath = getExternalSdCardPath();
//                    Log.e("sdCardPath", ""+sdCardPath);
//                    File dir = new File(sdCardPath + File.separator + CodeReUse.folderName);
//                    Calendar calendar = Calendar.getInstance();
////                    SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
////                    Date date;
//                    String createon = null;
//                    try {
////                        date = formatter.parse(calendar.getTime().toString());
//                        createon = CodeReUse.formatreport.format(calendar.getTime());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                    File fileLocation = new File(dir, createon + "_reportfile.csv");
//                    if (!dir.exists()) {
//                        dir.mkdirs();
//                    }
//
//                    if (fileLocation.exists()) {
//                        try {
//                            final String dataString = new String(data.toString().getBytes());
//                            FileOutputStream fout = new FileOutputStream(fileLocation);
//                            OutputStreamWriter osw = new OutputStreamWriter(fout);
//
//                            //Writing the string to file
//                            osw.write(dataString);
//                            osw.flush();
//                            osw.close();
//
//                            if (isFromChangeUnit) {
//                                sqliteHelper.deleteAllRecordFromAllTable(true);
//                            } else {
//                                Utility.dismissAlertDialog();
//                                Toast.makeText(act, "File exported successfully..!", Toast.LENGTH_LONG).show();
//
//                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                                intent.setType("*/*");
//                                act.startActivityForResult(intent, 1);
//                            }
//
//                        } catch (Exception e) {
//                            Log.e("ErrorEx", e.getMessage());
//                            Utility.dismissAlertDialog();
//                        }
//                    } else {
//                        try {
//                            final String dataString = new String(data.toString().getBytes());
//                            FileOutputStream fout = new FileOutputStream(fileLocation);
//                            OutputStreamWriter osw = new OutputStreamWriter(fout);
//
//                            //Writing the string to file
//                            osw.write(dataString);
//                            osw.flush();
//                            osw.close();
//                            //exporting
//                            if (isFromChangeUnit) {
//                                sqliteHelper.deleteAllRecordFromAllTable(true);
//                            } else {
//                                Utility.dismissAlertDialog();
//                                Toast.makeText(act, "File exported successfully..!", Toast.LENGTH_LONG).show();
//
//                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                                intent.setType("*/*");
//                                act.startActivityForResult(intent, 1);
//                            }
//
//                        } catch (Exception e) {
//                            Log.e("ErrorEx", e.getMessage());
//                            Utility.dismissAlertDialog();
//                        }
//                    }
//                }

            }
            else
            {
                String sdCard = Environment.getExternalStorageDirectory().getAbsolutePath();
                File dir = new File(sdCard + File.separator + CodeReUse.folderName);
                Calendar calendar = Calendar.getInstance();
//                SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
//                Date date;
                String createon = null;
                try {
//                    date = formatter.parse(calendar.getTime().toString());
                    createon = CodeReUse.formatreport.format(calendar.getTime());
                    createon = createon.replace(":","");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                File fileLocation = new File(dir, createon + "_reportfile.csv");
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                if (fileLocation.exists()) {
                    try {
                        final String dataString = new String(data.toString().getBytes());
                        FileOutputStream fout = new FileOutputStream(fileLocation);
                        OutputStreamWriter osw = new OutputStreamWriter(fout);

                        //Writing the string to file
                        osw.write(dataString);
                        osw.flush();
                        osw.close();

                        if (isFromChangeUnit) {
                            sqliteHelper.deleteAllRecordFromAllTable(true);
                        } else {
                            //Utility.dismissAlertDialog();
                            if (!isUSBDetected || !isSDPresent)
                                Utility.ShowMessage(act,"Success",createon + "_reportfile.csv" + "  File has been exported to Internal Storage.","Ok");
                            //Toast.makeText(act, "File exported successfully..!", Toast.LENGTH_LONG).show();
                            Utility.dismissAlertDialog();
                        }

                    } catch (Exception e) {
                        Log.e("ErrorEx", e.getMessage());
                        Utility.dismissAlertDialog();
                    }
                } else {
                    try {
                        final String dataString = new String(data.toString().getBytes());
                        FileOutputStream fout = new FileOutputStream(fileLocation);
                        OutputStreamWriter osw = new OutputStreamWriter(fout);

                        //Writing the string to file
                        osw.write(dataString);
                        osw.flush();
                        osw.close();
                        //exporting
                        if (isFromChangeUnit) {
                            sqliteHelper.deleteAllRecordFromAllTable(true);
                        } else {
                            //Utility.dismissAlertDialog();
                            if (!isUSBDetected || !isSDPresent)
                                Utility.ShowMessage(act,"Success",createon + "_reportfile.csv" + "  File has been exported to Internal Storage.","Ok");
                            //Toast.makeText(act, "File exported successfully..!", Toast.LENGTH_LONG).show();
                            Utility.dismissAlertDialog();
                        }

                    } catch (Exception e) {
                        Log.e("ErrorEx", e.getMessage());
                        Utility.dismissAlertDialog();
                    }
                }
            }
            //******
        } catch (Exception e) {
            if (e.getMessage().equals("Index: 0, Size: 0")) {
                Toast.makeText(act, "Please make sure pendrive is MS-DOS (FAT) (Master Boot record) formatted", Toast.LENGTH_LONG).show();
            } else {
                Log.e("Error Main TryCatch :- ", e.getMessage());
                Toast.makeText(act, "" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            Utility.dismissAlertDialog();
        }
    }

    // Report File Export Function
    @SuppressLint("NewApi")
    public void csvFileSendEmailFunction(String tag, String email) {
        StringBuilder data = new StringBuilder();

//        data.append("\n" + columnNameList.toString());
        if (tag.equals("1")) {
            data.append(columnNameListSingle.toString().replace("[", "").replace("]", ""));
            for (int i = 0; i < listSingle.size(); i++) {
                List<Cell> cells = listSingle.get(i);
                data.append("\n");
                for (int j = 0; j < cells.size(); j++) {
                    Cell cellObject = cells.get(j);
//                Log.e("CellObject Data", cellObject.getData().toString());
                    data.append(cellObject.getData().toString() + ",");
                }
            }
        } else {
            data.append(columnNameListAll.toString().replace("[", "").replace("]", ""));
            for (int i = 0; i < listAll.size(); i++) {
                List<Cell> cells = listAll.get(i);
                data.append("\n");
                for (int j = 0; j < cells.size(); j++) {
                    Cell cellObject = cells.get(j);
//                Log.e("CellObject Data", cellObject.getData().toString());
                    data.append(cellObject.getData().toString() + ",");
                }
            }
        }

        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + File.separator + "AllentownEmail");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        Date date;
        String createon = null;
        try {
            date = formatter.parse(calendar.getTime().toString());
            createon = CodeReUse.formatreport.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        File fileLocation = new File(dir, createon + "_reportfile.csv");
        if (!dir.exists()) {
            dir.mkdirs();
        }
//        Uri path = FileProvider.getUriForFile(context, "com.allentownblower.fileprovider", fileLocation);

        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        Boolean isSDSupportedDevice = Environment.isExternalStorageRemovable();
        if (fileLocation.exists()) {
            try {
                final String dataString = new String(data.toString().getBytes());
                FileOutputStream fout = new FileOutputStream(fileLocation);
                OutputStreamWriter osw = new OutputStreamWriter(fout);

                //Writing the string to file
                osw.write(dataString);
                osw.flush();
                osw.close();

                //exporting
//            Intent fileIntent = new Intent(Intent.ACTION_SEND);
//            fileIntent.setType("*/*");
//            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Calendar Data");
//            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
//            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//
//            act.startActivity(Intent.createChooser(fileIntent, "Send mail"));
            } catch (Exception e) {
                System.out.println(e);
            }
        } else {
            try {
                final String dataString = new String(data.toString().getBytes());
                FileOutputStream fout = new FileOutputStream(fileLocation);
                OutputStreamWriter osw = new OutputStreamWriter(fout);

                //Writing the string to file
                osw.write(dataString);
                osw.flush();
                osw.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        Uri path = null;
//        u1 = Uri.fromFile(fileLocation);
        path = FileProvider.getUriForFile(act, act.getApplicationContext().getPackageName() + ".fileprovider", fileLocation);

        if (act == null) {
            Log.e("LOG_TAG", "Activity context is null");
        } else {
            if (!act.isFinishing()) {
//                Intent sendIntent = new Intent(Intent.ACTION_SEND);
//                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Report Details");
//                sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                sendIntent.putExtra(Intent.EXTRA_STREAM, u1);
//                sendIntent.setType("text/html");
//                act.startActivity(sendIntent);

//                Intent emailIntent = new Intent(Intent.ACTION_SEND);
//                emailIntent.setType("text/plain");
//                emailIntent.putExtra(Intent.EXTRA_CHOOSER_TARGETS, email);
//                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Report Data");
//                emailIntent.putExtra(Intent.EXTRA_TEXT, "This is the body");
//                emailIntent.putExtra(Intent.EXTRA_STREAM, path);
//                emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                act.startActivity(Intent.createChooser(emailIntent, "Send mail..."));

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                // set the type to 'email'
                emailIntent.setDataAndType(path, "plain/text");

                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Report Data");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                emailIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                // the attachment
                emailIntent.putExtra(Intent.EXTRA_STREAM, path);

//                ArrayList<Uri> uris = new ArrayList<>();
                //convert from paths to Android friendly Parcelable Uri's
//                uris.add(path);
//                emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

                //this line is where an exception occurs and "Error" is displayed on my phone
                act.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            }


        }


    }

    //Old email api where we send json to server.
    public void getSendReportEmail_Api(String tag, String email, RackDetailsModel rackDetailsModel, AllentownBlowerApplication allentownBlowerApplication, PrefManager prefManager, Activity act, boolean isFromChangeUnit) {
        // if (NetworkUtil.getConnectivityStatus(act)) {
        if (prefManager.getHostName() == null || !prefManager.getHostName().contains("http")){
            Log.e("HostName :- ", "Host Name is Not Available");
            return;
        }

        ResponseHandler responseHandler = new ResponseHandler(act);
        setpointArrayList = responseHandler.getBLSetPointData(startDate, endDate);
        rackModelArrayList = responseHandler.getBLRackSetUpData(startDate, endDate);

        JSONObject objParam = new JSONObject();
        JSONArray jsonArr = new JSONArray();
        //objParam.put(ApiHandler.strGetSendEmailFromDate, startDate);
        //objParam.put(ApiHandler.strGetSendEmailToDate, endDate);
        if (tag.equals("1")){
            jsonArr = responseHandler.getBLFeedbackDataMaxMinAvg_Email(startDate,endDate,setpointArrayList,rackModelArrayList,responseHandler,isFromChangeUnit,rackDetailsModel,email);
            //objParam.put(ApiHandler.strGetSendEmailAvgData, jsArrAvgData);
            //objParam.put(ApiHandler.strGetSendEmailAllData, "");
        }else {
            jsonArr = responseHandler.getBLFeedbackDataAll_Email(startDate,endDate,setpointArrayList,rackModelArrayList,responseHandler,isFromChangeUnit,rackDetailsModel,email);
            //objParam.put(ApiHandler.strGetSendEmailAvgData, "");
            //objParam.put(ApiHandler.strGetSendEmailAllData, jsArrAllData);
        }
        //objParam.put(ApiHandler.strGetSendEmail_EmailIDs, email);
        //objParam.put(ApiHandler.strUpdateRackBlowerDetailsId, rackDetailsModel.getmId());
        //objParam.put(ApiHandler.strUpdateRackBlowerCustomerID, rackDetailsModel.getmRackBlowerCustomerID());

        //JSONArray jsonArr = new JSONArray();
        //jsonArr.put(objParam);
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("ReportDetails", jsonArr);
        }
        catch (JSONException e){
            Log.e("TAG","Json exception : "  + e.toString());
        }
        Log.e("send_email_ObjParam",""+ jsonObj);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                prefManager.getHostName() + ApiHandler.strUrlSendReportEmail, jsonObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Utility.dismissAlertDialog();
                        Utility.Log("getSendReportEmail_Api_Response : " + jsonObject);
                        try {
                            if (jsonObject.getBoolean("result")) {
                                //Toast.makeText(act, "Email has been sent successfully..!", Toast.LENGTH_LONG).show();
                                Utility.ShowMessage(act,"Success","Report File has been emailed.","Ok");
                            } else {

                                if (jsonObject.has("message")) {
                                    //Utility.showAlertDialog(act, jsonObject.getString("message"), getString(R.string.ok));
                                    Utility.Log("SendReportEmail_Api_Response Fail : " + jsonObject.getString("message"));
                                }else {
                                    Utility.showAlertDialog(act, act.getString(R.string.error), act.getString(R.string.ok));
                                }

                            }
                        } catch (JSONException e) {
                            Utility.Log("SendReportEmail_Api_Error : " + e.toString());
                            e.printStackTrace();
                            Utility.showAlertDialog(act, act.getString(R.string.error), act.getString(R.string.ok));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utility.dismissProgress();
                        Utility.Log("SendReportEmail_Api Error : " + error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return allentownBlowerApplication.getInstance().getHeader();
            }
        };

        allentownBlowerApplication.getInstance().cancelPendingRequests(PendingID.nSendReportEmail);
        allentownBlowerApplication.getInstance().addToRequestQueue(request, PendingID.nSendReportEmail);
    }


    // Report File Export Function
    @SuppressLint("NewApi")
    public void csvFileSendEmailFunction_1(String tag, String email, RackDetailsModel rackDetailsModel, AllentownBlowerApplication allentownBlowerApplication, Activity act, PrefManager prefManager) {
        StringBuilder data = new StringBuilder();
        ResponseHandler responseHandler = new ResponseHandler(act);
//        data.append("\n" + columnNameList.toString());
        setpointArrayList = responseHandler.getBLSetPointData(startDate, endDate);
        rackModelArrayList = responseHandler.getBLRackSetUpData(startDate, endDate);
        if (tag.equals("1")) {
            getColumnHeaderList(tag);
            data = responseHandler.getBLFeedbackDataMaxMinAvgExport(startDate, endDate, setpointArrayList, rackModelArrayList, responseHandler, false, columnNameListSingle);
        } else {
            getColumnHeaderList(tag);
            data = responseHandler.getBLFeedbackDataAllExport(startDate, endDate, setpointArrayList, rackModelArrayList, responseHandler, false, columnNameListAll);
        }

        String sdCard = Environment.getExternalStorageDirectory().getAbsolutePath();
        File dir = new File(sdCard + File.separator + CodeReUse.folderName);
        Calendar calendar = Calendar.getInstance();
//                SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
//                Date date;
        String createon = null;
        try {
//                    date = formatter.parse(calendar.getTime().toString());
            createon = CodeReUse.formatreport.format(calendar.getTime());
            createon = createon.replace(":","");
        } catch (Exception e) {
            e.printStackTrace();
        }

        File fileLocation = new File(dir, createon + "_reportfile.csv");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        Log.e("fileLocation",""+ fileLocation);
        if (fileLocation.exists()) {
            try {
                final String dataString = new String(data.toString().getBytes());
                FileOutputStream fout = new FileOutputStream(fileLocation);
                OutputStreamWriter osw = new OutputStreamWriter(fout);

                //Writing the string to file
                osw.write(dataString);
                osw.flush();
                osw.close();

//                if (isFromChangeUnit) {
//                    sqliteHelper.deleteAllRecordFromAllTable(true);
//                } else {
//                    //Utility.dismissAlertDialog();
////                    if (!isUSBDetected || !isSDPresent)
//                        Utility.ShowMessage(act,"Success",createon + "_reportfile.csv" + "  File has been exported to Internal Storage.","Ok");
//                    //Toast.makeText(act, "File exported successfully..!", Toast.LENGTH_LONG).show();
//                    Utility.dismissAlertDialog();
//                }

            } catch (Exception e) {
                Log.e("ErrorEx", e.getMessage());
                Utility.dismissAlertDialog();
            }
        } else {
            try {
                final String dataString = new String(data.toString().getBytes());
                FileOutputStream fout = new FileOutputStream(fileLocation);
                OutputStreamWriter osw = new OutputStreamWriter(fout);

                //Writing the string to file
                osw.write(dataString);
                osw.flush();
                osw.close();
                //exporting
//                if (isFromChangeUnit) {
//                    sqliteHelper.deleteAllRecordFromAllTable(true);
//                } else {
//                    //Utility.dismissAlertDialog();
//                    if (!isUSBDetected || !isSDPresent)
//                        Utility.ShowMessage(act,"Success",createon + "_reportfile.csv" + "  File has been exported to Internal Storage.","Ok");
//                    //Toast.makeText(act, "File exported successfully..!", Toast.LENGTH_LONG).show();
//                    Utility.dismissAlertDialog();
//                }

            } catch (Exception e) {
                Log.e("ErrorEx", e.getMessage());
                Utility.dismissAlertDialog();
            }
        }
        Uri path = null;
//        u1 = Uri.fromFile(fileLocation);
        path = FileProvider.getUriForFile(act, act.getApplicationContext().getPackageName() + ".fileprovider", fileLocation);
        String filePath = String.valueOf(fileLocation);
        getSendReportEmail_Api_1(email, rackDetailsModel, allentownBlowerApplication, prefManager, act, filePath);
    }

    //New API with sending the file to email server
    public void getSendReportEmail_Api_1(String email, RackDetailsModel rackDetailsModel, AllentownBlowerApplication allentownBlowerApplication, PrefManager prefManager, Activity act, String filePath) {
        if (prefManager.getHostName() == null || !prefManager.getHostName().contains("http")){
            Log.e("HostName :- ", "Host Name is Not Available");
            return;
        }
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST,
                prefManager.getHostName() + ApiHandler.strUrlSendReportFileToEmail, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                try {
                    JSONObject obj = new JSONObject(new String(response.data));
                    Utility.dismissAlertDialog();
                    Utility.Log("getSendReportEmail_Api_Response : " + obj.toString());
                    if (ResponseHandler.getBool(obj, "result")) {
                        //Toast.makeText(act, "Email has been sent successfully..!", Toast.LENGTH_LONG).show();
                        Utility.ShowMessage(act,"Success","Report File has been emailed.","Ok");
                    } else {
                        if (obj.has("message")) {
                            Utility.Log("SendReportEmail_Api_Response Fail : " + obj.getString("message"));
                        }else {
                            Utility.showAlertDialog(act, act.getString(R.string.error), act.getString(R.string.ok));
                        }
                    }
                } catch (JSONException e) {
                    Utility.Log("SendReportEmail_Api_Error : " + e.toString());
                    e.printStackTrace();
                    Utility.showAlertDialog(act, act.getString(R.string.error), act.getString(R.string.ok));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utility.dismissProgress();
                Utility.dismissAlertDialog();
                Utility.Log("SendReportEmail_Api Error : " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("FromDate", startDate);
                params.put("ToDate", endDate);
                params.put("RackBlowerDetailsID", rackDetailsModel.getmId());
                params.put("RackBlowerCustomerID", rackDetailsModel.getmRackBlowerCustomerID());
                params.put("EmailIDs", email);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                File file = new File(filePath);
                byte[] data = Utility.convertFileToByteArray(file);

                Log.e("File Name : ","" + file.getName());
                params.put("file", new DataPart(file.getName(), data));
                return params;
            }
        };

        //adding the request to volley
        VolleySingleton.getInstance(act).cancelPendingRequests(PendingID.SendReportFileToEmail);
        VolleySingleton.getInstance(act).addToRequestQueue(volleyMultipartRequest, PendingID.SendReportFileToEmail);
    }
}
