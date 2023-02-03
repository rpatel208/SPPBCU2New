package com.allentownblower.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allentownblower.R;
import com.allentownblower.application.AllentownBlowerApplication;
import com.allentownblower.communication.SerialPortConversion;
import com.github.mjdev.libaums.UsbMassStorageDevice;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import android.provider.Settings;


/**
 * Created by Mayur Yadav on 01 Sep 2019.
 */
public class Utility {

    public static ProgressDialog progressDialog;
    public static AlertDialog alertDialog;

    public static Dialog progressBar_Dialog;

    public static LinearLayout layout_setting_password_txtType, layout_setting_password_imgType;
    public static TextView txt_digit_one_setting_password, txt_digit_two_setting_password, txt_digit_three_setting_password, txt_digit_four_setting_password, txt_digit_five_setting_password, txt_digit_six_setting_password;
    public static ImageView digit_one_setting_password, digit_two_setting_password, digit_three_setting_password, digit_four_setting_password, digit_five_setting_password, digit_six_setting_password;
    public static LinearLayout layout_one_setting_password, layout_two_setting_password, layout_three_setting_password, layout_four_setting_password, layout_five_setting_password, layout_six_setting_password, layout_seven_setting_password, layout_eight_setting_password, layout_nine_setting_password, layout_zero_setting_password;
    public static LinearLayout layout_backspace_setting_password, layout_home_setting_password;
    public static LinearLayout layout_enter_setting_password, layout_change_setting_password;

    public static ArrayList<String> digitList = new ArrayList<>();

    public static PrefManager prefManager;
    public static SerialPortConversion portConversion;

    public static Dialog alertview_setting_password;
    private static int currentApiVersion;
    private static UsbDevice device;
    private static UsbManager usbManager;
    private static List<String> tmpMessage = new ArrayList<String>();//ruei add

    public static String SPP ="SPP";
    public static String BCU2 ="BCU2";
    public static String BLOWER_TYPE="";
    public static String STR_BLOWER_TYPE="BLOWERTYPE";

    public static String ACPOINT;
    public static String DCPOINT;


//    public static void Log(String title, String massage) {
//        Log.e(title, massage);
//    }
    public static void Log(String title, String massage) {
        //ruei modified
//        if(LogWatcher.isChangingLogFile) {
//            tmpMessage.add(massage);
//        } else {
//            if(tmpMessage.size() > 0){
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    tmpMessage.forEach((t) -> Log.e(title, t));
//                    tmpMessage.clear();
//                }
//            }
//            Log.e(title, massage);
//        }
        Log.e(title, massage);
    }

    public static void showProgress(Activity act, String strMsg) {
        if (progressDialog != null && progressDialog.isShowing())
            return;

        progressDialog = new ProgressDialog(act, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(strMsg);
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Showing Alert Message
                try {
                    if (progressDialog != null && !progressDialog.isShowing())
                        progressDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void showProgressReport(Activity act, String strMsg) {
        if (progressDialog != null && progressDialog.isShowing())
            return;

//        progressDialog = new ProgressDialog(act, R.style.AppTheme_Dark_Dialog);
        progressDialog = new ProgressDialog(act, R.style.AppTheme_Dark_Dialog);
//        progressDialog.setIndeterminate(false);
        progressDialog.setIcon(act.getResources().getDrawable(R.drawable.ic_alert_icon));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(strMsg);
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Showing Alert Message
                try {
                    if (progressDialog != null && !progressDialog.isShowing())
                        progressDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void ShowMessageReport(Activity activity, String strMessage) {
        alertDialog = new AlertDialog.Builder(activity).create();

//        // Setting Dialog Title
//        alertDialog.setTitle(strTitle);

        // Setting Dialog Message
        alertDialog.setMessage(strMessage);
        alertDialog.setCancelable(false);

        // Setting OK Button
//        alertDialog.setButton(strBtn.toUpperCase(), new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                // Write your code here to execute after dialog closed
//            }
//        });

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Showing Alert Message
                try {
                    if (alertDialog != null && !alertDialog.isShowing())
                        alertDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void dismissAlertDialog() {
        try {
            if (alertDialog != null && alertDialog.isShowing())
                alertDialog.dismiss();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static void dismissProgress() {
        try {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static void ShowMessage(Activity activity, String strTitle, String strMessage, String strBtn) {
        final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();

        // Setting Dialog Title
        alertDialog.setTitle(strTitle);

        // Setting Dialog Message
        alertDialog.setMessage(strMessage);

        // Setting OK Button
        alertDialog.setButton(strBtn.toUpperCase(), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
            }
        });

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Showing Alert Message
                try {
                    if (alertDialog != null && !alertDialog.isShowing())
                        alertDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void AlertShowMessage(final Activity activity, String strTitle, String strMessage, String strBtn) {

        prefManager = new PrefManager(activity);
        portConversion = new SerialPortConversion(activity);

        final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();

        // Setting Dialog Title
        alertDialog.setTitle(strTitle);

        // Setting Dialog Message
        alertDialog.setMessage(strMessage);

        // Setting Dialog Auto Cancelable
        alertDialog.setCancelable(false);

        // Setting OK Button
        alertDialog.setButton(strBtn.toUpperCase(), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed

                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStopService);

                prefManager.setOpenNode(false);

                //prefManager.setOpenNode(true);
                Utility.Log("TAG", prefManager.getOpenNode() + "");

                if (prefManager.getOpenNode()) {
                    portConversion.closeNode();
                }

                alertDialog.dismiss();
                //android.os.Process.killProcess(android.os.Process.myPid());
                activity.finishAffinity();
                System.exit(0);
            }
        });

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Showing Alert Message
                try {
                    if (alertDialog != null && !alertDialog.isShowing())
                        alertDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void ShowSettingPasswordDialog(final Activity act, final String actString) {

        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStopService);

        prefManager = new PrefManager(act);

        digitList.clear();

        alertview_setting_password = new Dialog(act);

        alertview_setting_password.setCancelable(false);
        alertview_setting_password.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertview_setting_password.setContentView(R.layout.alertview_setting_password_layout);
        alertview_setting_password.show();

        layout_setting_password_txtType = alertview_setting_password.findViewById(R.id.layout_setting_password_txtType);
        layout_setting_password_imgType = alertview_setting_password.findViewById(R.id.layout_setting_password_imgType);

        txt_digit_one_setting_password = alertview_setting_password.findViewById(R.id.txt_digit_one_setting_password);
        txt_digit_two_setting_password = alertview_setting_password.findViewById(R.id.txt_digit_two_setting_password);
        txt_digit_three_setting_password = alertview_setting_password.findViewById(R.id.txt_digit_three_setting_password);
        txt_digit_four_setting_password = alertview_setting_password.findViewById(R.id.txt_digit_four_setting_password);
        txt_digit_five_setting_password = alertview_setting_password.findViewById(R.id.txt_digit_five_setting_password);
        txt_digit_six_setting_password = alertview_setting_password.findViewById(R.id.txt_digit_six_setting_password);

        digit_one_setting_password = alertview_setting_password.findViewById(R.id.digit_one_setting_password);
        digit_two_setting_password = alertview_setting_password.findViewById(R.id.digit_two_setting_password);
        digit_three_setting_password = alertview_setting_password.findViewById(R.id.digit_three_setting_password);
        digit_four_setting_password = alertview_setting_password.findViewById(R.id.digit_four_setting_password);
        digit_five_setting_password = alertview_setting_password.findViewById(R.id.digit_five_setting_password);
        digit_six_setting_password = alertview_setting_password.findViewById(R.id.digit_six_setting_password);

        layout_one_setting_password = alertview_setting_password.findViewById(R.id.layout_one_setting_password);
        layout_two_setting_password = alertview_setting_password.findViewById(R.id.layout_two_setting_password);
        layout_three_setting_password = alertview_setting_password.findViewById(R.id.layout_three_setting_password);
        layout_four_setting_password = alertview_setting_password.findViewById(R.id.layout_four_setting_password);
        layout_five_setting_password = alertview_setting_password.findViewById(R.id.layout_five_setting_password);
        layout_six_setting_password = alertview_setting_password.findViewById(R.id.layout_six_setting_password);
        layout_seven_setting_password = alertview_setting_password.findViewById(R.id.layout_seven_setting_password);
        layout_eight_setting_password = alertview_setting_password.findViewById(R.id.layout_eight_setting_password);
        layout_nine_setting_password = alertview_setting_password.findViewById(R.id.layout_nine_setting_password);
        layout_zero_setting_password = alertview_setting_password.findViewById(R.id.layout_zero_setting_password);
        layout_backspace_setting_password = alertview_setting_password.findViewById(R.id.layout_backspace_setting_password);
        layout_home_setting_password = alertview_setting_password.findViewById(R.id.layout_home_setting_password);

        layout_enter_setting_password = alertview_setting_password.findViewById(R.id.layout_enter_setting_password);
        layout_change_setting_password = alertview_setting_password.findViewById(R.id.layout_change_setting_password);

        if (actString.equals("ChangePasswordSetting") || actString.equals("ChangePasswordReport") || actString.equals("DiagnosticsPasswordSetting") ||
                actString.equals("DiagnosticsDetailsPasswordSetting") || actString.equals("BluetoothDisconnectPasswordSetting")) {
            layout_setting_password_imgType.setVisibility(View.GONE);
            layout_setting_password_txtType.setVisibility(View.VISIBLE);
            layout_enter_setting_password.setVisibility(View.GONE);
            layout_change_setting_password.setVisibility(View.VISIBLE);
        } else {
            layout_setting_password_imgType.setVisibility(View.VISIBLE);
            layout_setting_password_txtType.setVisibility(View.GONE);
            layout_enter_setting_password.setVisibility(View.VISIBLE);
            layout_change_setting_password.setVisibility(View.GONE);
        }

        layout_one_setting_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDigitClick("1", actString);
            }
        });

        layout_two_setting_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDigitClick("2", actString);
            }
        });

        layout_three_setting_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDigitClick("3", actString);
            }
        });

        layout_four_setting_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDigitClick("4", actString);
            }
        });

        layout_five_setting_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDigitClick("5", actString);
            }
        });

        layout_six_setting_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDigitClick("6", actString);
            }
        });

        layout_seven_setting_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDigitClick("7", actString);
            }
        });

        layout_eight_setting_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDigitClick("8", actString);
            }
        });

        layout_nine_setting_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDigitClick("9", actString);
            }
        });

        layout_zero_setting_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDigitClick("0", actString);
            }
        });

        layout_backspace_setting_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackspaceClick(actString);
            }
        });

        layout_home_setting_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
                alertview_setting_password.dismiss();

                if (actString.equals("Plus_Command")) {

                } else if (actString.equals("Minus_Command")) {

                } else if (actString.equals("Up_Down")) {

                } else {
                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nRedirectHome);
                }
            }
        });

        layout_enter_setting_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (digitList.size() != 0) {
                    StringBuilder password_digit = new StringBuilder();
                    password_digit.setLength(0);
                    for (int i = 0; i < digitList.size(); i++) {
                        password_digit.append(digitList.get(i));
                    }

                    alertview_setting_password.dismiss();

                    if (prefManager.getSettingPassword().equals(String.valueOf(password_digit.toString()))) {
                        if (actString.equals("Plus_Command")) {
                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nPlus);
                        } else if (actString.equals("Minus_Command")) {
                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nMinus);
                        } else if (actString.equals("Up_Down")) {
                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nUp_Down);
                        } else {
                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nRedirectSettings);
                        }
                    } else if (prefManager.getDiagnosticsPassword().equals(String.valueOf(password_digit.toString()))) {
                        if (!CodeReUse.isBolwerAdmin) {
                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nDiagnosticsSettings);
                        } else {
                            ShowMessage(act, "Alert", "Feature is not available.", "OK");
                        }
                    } else if (prefManager.getDiagnosticsDetailPassword().equals(String.valueOf(password_digit.toString()))) {
                        if (!CodeReUse.isBolwerAdmin) {
                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nDiagnosticsDetailsSettings);
                        } else {
                            ShowMessage(act, "Alert", "Feature is not available.", "OK");
                        }
                    } else if (prefManager.getBluetoothDisconnectPassword().equals(String.valueOf(password_digit.toString()))) {
                        if (CodeReUse.isBolwerAdmin)
                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nBluetoothDisconnect);
                        else {
                            ShowMessage(act, "Alert", "Invaild Password", "OK");
                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
                        }
                    } else if (prefManager.getReportPassword().equals(String.valueOf(password_digit.toString()))) {
                        if (!CodeReUse.isBolwerAdmin) {
                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nRedirectReport);
                        } else {
                            ShowMessage(act, "Alert", "Feature is not available.", "OK");
                        }
                    } else {
                        ShowMessage(act, "Alert", "Invaild Password", "OK");
                        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
                    }
                } else {
                    ShowMessage(act, "Alert", "Invaild Password", "OK");
                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
                }
            }
        });

        layout_change_setting_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (digitList.size() != 0) {
                    StringBuilder password_digit = new StringBuilder();
                    password_digit.setLength(0);
                    for (int i = 0; i < digitList.size(); i++) {
                        password_digit.append(digitList.get(i));
                    }

                    alertview_setting_password.dismiss();

                    if (actString.equals("ChangePasswordSetting")) {
                        prefManager.setSettingPassword(password_digit.toString());
                        ShowMessage(act, "Alert", "Password Changed Successfully", "OK");
                        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nChangePassword);
                    } else if (actString.equals("ChangePasswordReport")) {
                        prefManager.setReportPassword(password_digit.toString());
                        ShowMessage(act, "Alert", "Password Changed Successfully", "OK");
                        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nChangePassword);
                    } else if (actString.equals("DiagnosticsPasswordSetting")) {
                        prefManager.setDiagnosticsPassword(password_digit.toString());
                        ShowMessage(act, "Alert", "Password Changed Successfully", "OK");
                        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nChangePassword);
                    } else if (actString.equals("DiagnosticsDetailsPasswordSetting")) {
                        prefManager.setDiagnosticsDetailPassword(password_digit.toString());
                        ShowMessage(act, "Alert", "Password Changed Successfully", "OK");
                        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nChangePassword);
                    } else if (actString.equals("BluetoothDisconnectPasswordSetting")) {
                        prefManager.setBluetoothDisconnectPassword(password_digit.toString());
                        ShowMessage(act, "Alert", "Password Changed Successfully", "OK");
                        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nChangePassword);
                    }
                } else {
                    ShowMessage(act, "Alert", "Invaild Password", "OK");
                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
                }
            }
        });

    }

    private static void onDigitClick(String digit, String actString) {

        if (actString.equals("ChangePasswordSetting") || actString.equals("ChangePasswordReport") || actString.equals("DiagnosticsPasswordSetting")
                || actString.equals("DiagnosticsDetailsPasswordSetting")  || actString.equals("BluetoothDisconnectPasswordSetting")) {

            if (actString.equals("ChangePasswordSetting")) {
                if (digitList.size() < 4) {
                    digitList.add(digit);
                }

                if (txt_digit_one_setting_password.getVisibility() == View.VISIBLE && txt_digit_two_setting_password.getVisibility() == View.VISIBLE
                        && txt_digit_three_setting_password.getVisibility() == View.VISIBLE) {
                    txt_digit_one_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_one_setting_password.setText(digitList.get(0));
                    txt_digit_two_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_two_setting_password.setText(digitList.get(1));
                    txt_digit_three_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_three_setting_password.setText(digitList.get(2));
                    txt_digit_four_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_four_setting_password.setText(digitList.get(3));
                    txt_digit_five_setting_password.setVisibility(View.GONE);
                    txt_digit_six_setting_password.setVisibility(View.GONE);
                } else if (txt_digit_one_setting_password.getVisibility() == View.VISIBLE && txt_digit_two_setting_password.getVisibility() == View.VISIBLE) {
                    txt_digit_one_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_one_setting_password.setText(digitList.get(0));
                    txt_digit_two_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_two_setting_password.setText(digitList.get(1));
                    txt_digit_three_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_three_setting_password.setText(digitList.get(2));
                    txt_digit_four_setting_password.setVisibility(View.GONE);
                    txt_digit_five_setting_password.setVisibility(View.GONE);
                    txt_digit_six_setting_password.setVisibility(View.GONE);
                } else if (txt_digit_one_setting_password.getVisibility() == View.VISIBLE) {
                    txt_digit_one_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_one_setting_password.setText(digitList.get(0));
                    txt_digit_two_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_two_setting_password.setText(digitList.get(1));
                    txt_digit_three_setting_password.setVisibility(View.GONE);
                    txt_digit_four_setting_password.setVisibility(View.GONE);
                    txt_digit_five_setting_password.setVisibility(View.GONE);
                    txt_digit_six_setting_password.setVisibility(View.GONE);
                } else {
                    txt_digit_one_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_one_setting_password.setText(digitList.get(0));
                    txt_digit_two_setting_password.setVisibility(View.GONE);
                    txt_digit_three_setting_password.setVisibility(View.GONE);
                    txt_digit_four_setting_password.setVisibility(View.GONE);
                    txt_digit_five_setting_password.setVisibility(View.GONE);
                    txt_digit_six_setting_password.setVisibility(View.GONE);
                }

            } else if (actString.equals("ChangePasswordReport")) {
                if (digitList.size() < 4) {
                    digitList.add(digit);
                }

                if (txt_digit_one_setting_password.getVisibility() == View.VISIBLE && txt_digit_two_setting_password.getVisibility() == View.VISIBLE
                        && txt_digit_three_setting_password.getVisibility() == View.VISIBLE) {
                    txt_digit_one_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_one_setting_password.setText(digitList.get(0));
                    txt_digit_two_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_two_setting_password.setText(digitList.get(1));
                    txt_digit_three_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_three_setting_password.setText(digitList.get(2));
                    txt_digit_four_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_four_setting_password.setText(digitList.get(3));
                    txt_digit_five_setting_password.setVisibility(View.GONE);
                    txt_digit_six_setting_password.setVisibility(View.GONE);
                } else if (txt_digit_one_setting_password.getVisibility() == View.VISIBLE && txt_digit_two_setting_password.getVisibility() == View.VISIBLE) {
                    txt_digit_one_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_one_setting_password.setText(digitList.get(0));
                    txt_digit_two_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_two_setting_password.setText(digitList.get(1));
                    txt_digit_three_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_three_setting_password.setText(digitList.get(2));
                    txt_digit_four_setting_password.setVisibility(View.GONE);
                    txt_digit_five_setting_password.setVisibility(View.GONE);
                    txt_digit_six_setting_password.setVisibility(View.GONE);
                } else if (txt_digit_one_setting_password.getVisibility() == View.VISIBLE) {
                    txt_digit_one_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_one_setting_password.setText(digitList.get(0));
                    txt_digit_two_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_two_setting_password.setText(digitList.get(1));
                    txt_digit_three_setting_password.setVisibility(View.GONE);
                    txt_digit_four_setting_password.setVisibility(View.GONE);
                    txt_digit_five_setting_password.setVisibility(View.GONE);
                    txt_digit_six_setting_password.setVisibility(View.GONE);
                } else {
                    txt_digit_one_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_one_setting_password.setText(digitList.get(0));
                    txt_digit_two_setting_password.setVisibility(View.GONE);
                    txt_digit_three_setting_password.setVisibility(View.GONE);
                    txt_digit_four_setting_password.setVisibility(View.GONE);
                    txt_digit_five_setting_password.setVisibility(View.GONE);
                    txt_digit_six_setting_password.setVisibility(View.GONE);
                }

            }
            else if (actString.equals("BluetoothDisconnectPasswordSetting")) {
                if (digitList.size() < 5) {
                    digitList.add(digit);
                }

                if (txt_digit_one_setting_password.getVisibility() == View.VISIBLE && txt_digit_two_setting_password.getVisibility() == View.VISIBLE && txt_digit_three_setting_password.getVisibility() == View.VISIBLE && txt_digit_four_setting_password.getVisibility() == View.VISIBLE) {
                    txt_digit_one_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_one_setting_password.setText(digitList.get(0));
                    txt_digit_two_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_two_setting_password.setText(digitList.get(1));
                    txt_digit_three_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_three_setting_password.setText(digitList.get(2));
                    txt_digit_four_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_four_setting_password.setText(digitList.get(3));
                    txt_digit_five_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_five_setting_password.setText(digitList.get(4));
                    txt_digit_six_setting_password.setVisibility(View.GONE);
                } else if (txt_digit_one_setting_password.getVisibility() == View.VISIBLE && txt_digit_two_setting_password.getVisibility() == View.VISIBLE
                        && txt_digit_three_setting_password.getVisibility() == View.VISIBLE) {
                    txt_digit_one_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_one_setting_password.setText(digitList.get(0));
                    txt_digit_two_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_two_setting_password.setText(digitList.get(1));
                    txt_digit_three_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_three_setting_password.setText(digitList.get(2));
                    txt_digit_four_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_four_setting_password.setText(digitList.get(3));
                    txt_digit_five_setting_password.setVisibility(View.GONE);
                    txt_digit_six_setting_password.setVisibility(View.GONE);
                } else if (txt_digit_one_setting_password.getVisibility() == View.VISIBLE && txt_digit_two_setting_password.getVisibility() == View.VISIBLE) {
                    txt_digit_one_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_one_setting_password.setText(digitList.get(0));
                    txt_digit_two_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_two_setting_password.setText(digitList.get(1));
                    txt_digit_three_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_three_setting_password.setText(digitList.get(2));
                    txt_digit_four_setting_password.setVisibility(View.GONE);
                    txt_digit_five_setting_password.setVisibility(View.GONE);
                    txt_digit_six_setting_password.setVisibility(View.GONE);
                } else if (txt_digit_one_setting_password.getVisibility() == View.VISIBLE) {
                    txt_digit_one_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_one_setting_password.setText(digitList.get(0));
                    txt_digit_two_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_two_setting_password.setText(digitList.get(1));
                    txt_digit_three_setting_password.setVisibility(View.GONE);
                    txt_digit_four_setting_password.setVisibility(View.GONE);
                    txt_digit_five_setting_password.setVisibility(View.GONE);
                    txt_digit_six_setting_password.setVisibility(View.GONE);
                } else {
                    txt_digit_one_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_one_setting_password.setText(digitList.get(0));
                    txt_digit_two_setting_password.setVisibility(View.GONE);
                    txt_digit_three_setting_password.setVisibility(View.GONE);
                    txt_digit_four_setting_password.setVisibility(View.GONE);
                    txt_digit_five_setting_password.setVisibility(View.GONE);
                    txt_digit_six_setting_password.setVisibility(View.GONE);
                }

            }
            else if (actString.equals("DiagnosticsPasswordSetting")) {
                if (digitList.size() < 6) {
                    digitList.add(digit);
                }

                if (txt_digit_one_setting_password.getVisibility() == View.VISIBLE && txt_digit_two_setting_password.getVisibility() == View.VISIBLE
                        && txt_digit_three_setting_password.getVisibility() == View.VISIBLE && txt_digit_four_setting_password.getVisibility() == View.VISIBLE
                        && txt_digit_five_setting_password.getVisibility() == View.VISIBLE) {
                    txt_digit_one_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_one_setting_password.setText(digitList.get(0));
                    txt_digit_two_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_two_setting_password.setText(digitList.get(1));
                    txt_digit_three_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_three_setting_password.setText(digitList.get(2));
                    txt_digit_four_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_four_setting_password.setText(digitList.get(3));
                    txt_digit_five_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_five_setting_password.setText(digitList.get(4));
                    txt_digit_six_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_six_setting_password.setText(digitList.get(5));
                } else if (txt_digit_one_setting_password.getVisibility() == View.VISIBLE && txt_digit_two_setting_password.getVisibility() == View.VISIBLE && txt_digit_three_setting_password.getVisibility() == View.VISIBLE && txt_digit_four_setting_password.getVisibility() == View.VISIBLE) {
                    txt_digit_one_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_one_setting_password.setText(digitList.get(0));
                    txt_digit_two_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_two_setting_password.setText(digitList.get(1));
                    txt_digit_three_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_three_setting_password.setText(digitList.get(2));
                    txt_digit_four_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_four_setting_password.setText(digitList.get(3));
                    txt_digit_five_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_five_setting_password.setText(digitList.get(4));
                    txt_digit_six_setting_password.setVisibility(View.GONE);
                } else if (txt_digit_one_setting_password.getVisibility() == View.VISIBLE && txt_digit_two_setting_password.getVisibility() == View.VISIBLE
                        && txt_digit_three_setting_password.getVisibility() == View.VISIBLE) {
                    txt_digit_one_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_one_setting_password.setText(digitList.get(0));
                    txt_digit_two_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_two_setting_password.setText(digitList.get(1));
                    txt_digit_three_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_three_setting_password.setText(digitList.get(2));
                    txt_digit_four_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_four_setting_password.setText(digitList.get(3));
                    txt_digit_five_setting_password.setVisibility(View.GONE);
                    txt_digit_six_setting_password.setVisibility(View.GONE);
                } else if (txt_digit_one_setting_password.getVisibility() == View.VISIBLE && txt_digit_two_setting_password.getVisibility() == View.VISIBLE) {
                    txt_digit_one_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_one_setting_password.setText(digitList.get(0));
                    txt_digit_two_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_two_setting_password.setText(digitList.get(1));
                    txt_digit_three_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_three_setting_password.setText(digitList.get(2));
                    txt_digit_four_setting_password.setVisibility(View.GONE);
                    txt_digit_five_setting_password.setVisibility(View.GONE);
                    txt_digit_six_setting_password.setVisibility(View.GONE);
                } else if (txt_digit_one_setting_password.getVisibility() == View.VISIBLE) {
                    txt_digit_one_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_one_setting_password.setText(digitList.get(0));
                    txt_digit_two_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_two_setting_password.setText(digitList.get(1));
                    txt_digit_three_setting_password.setVisibility(View.GONE);
                    txt_digit_four_setting_password.setVisibility(View.GONE);
                    txt_digit_five_setting_password.setVisibility(View.GONE);
                    txt_digit_six_setting_password.setVisibility(View.GONE);
                } else {
                    txt_digit_one_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_one_setting_password.setText(digitList.get(0));
                    txt_digit_two_setting_password.setVisibility(View.GONE);
                    txt_digit_three_setting_password.setVisibility(View.GONE);
                    txt_digit_four_setting_password.setVisibility(View.GONE);
                    txt_digit_five_setting_password.setVisibility(View.GONE);
                    txt_digit_six_setting_password.setVisibility(View.GONE);
                }
            }
            else {
                if (digitList.size() < 6) {
                    digitList.add(digit);
                }

                if (txt_digit_one_setting_password.getVisibility() == View.VISIBLE && txt_digit_two_setting_password.getVisibility() == View.VISIBLE
                        && txt_digit_three_setting_password.getVisibility() == View.VISIBLE && txt_digit_four_setting_password.getVisibility() == View.VISIBLE
                        && txt_digit_five_setting_password.getVisibility() == View.VISIBLE) {
                    txt_digit_one_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_one_setting_password.setText(digitList.get(0));
                    txt_digit_two_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_two_setting_password.setText(digitList.get(1));
                    txt_digit_three_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_three_setting_password.setText(digitList.get(2));
                    txt_digit_four_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_four_setting_password.setText(digitList.get(3));
                    txt_digit_five_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_five_setting_password.setText(digitList.get(4));
                    txt_digit_six_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_six_setting_password.setText(digitList.get(5));
                } else if (txt_digit_one_setting_password.getVisibility() == View.VISIBLE && txt_digit_two_setting_password.getVisibility() == View.VISIBLE && txt_digit_three_setting_password.getVisibility() == View.VISIBLE && txt_digit_four_setting_password.getVisibility() == View.VISIBLE) {
                    txt_digit_one_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_one_setting_password.setText(digitList.get(0));
                    txt_digit_two_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_two_setting_password.setText(digitList.get(1));
                    txt_digit_three_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_three_setting_password.setText(digitList.get(2));
                    txt_digit_four_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_four_setting_password.setText(digitList.get(3));
                    txt_digit_five_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_five_setting_password.setText(digitList.get(4));
                    txt_digit_six_setting_password.setVisibility(View.GONE);
                } else if (txt_digit_one_setting_password.getVisibility() == View.VISIBLE && txt_digit_two_setting_password.getVisibility() == View.VISIBLE
                        && txt_digit_three_setting_password.getVisibility() == View.VISIBLE) {
                    txt_digit_one_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_one_setting_password.setText(digitList.get(0));
                    txt_digit_two_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_two_setting_password.setText(digitList.get(1));
                    txt_digit_three_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_three_setting_password.setText(digitList.get(2));
                    txt_digit_four_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_four_setting_password.setText(digitList.get(3));
                    txt_digit_five_setting_password.setVisibility(View.GONE);
                    txt_digit_six_setting_password.setVisibility(View.GONE);
                } else if (txt_digit_one_setting_password.getVisibility() == View.VISIBLE && txt_digit_two_setting_password.getVisibility() == View.VISIBLE) {
                    txt_digit_one_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_one_setting_password.setText(digitList.get(0));
                    txt_digit_two_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_two_setting_password.setText(digitList.get(1));
                    txt_digit_three_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_three_setting_password.setText(digitList.get(2));
                    txt_digit_four_setting_password.setVisibility(View.GONE);
                    txt_digit_five_setting_password.setVisibility(View.GONE);
                    txt_digit_six_setting_password.setVisibility(View.GONE);
                } else if (txt_digit_one_setting_password.getVisibility() == View.VISIBLE) {
                    txt_digit_one_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_one_setting_password.setText(digitList.get(0));
                    txt_digit_two_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_two_setting_password.setText(digitList.get(1));
                    txt_digit_three_setting_password.setVisibility(View.GONE);
                    txt_digit_four_setting_password.setVisibility(View.GONE);
                    txt_digit_five_setting_password.setVisibility(View.GONE);
                    txt_digit_six_setting_password.setVisibility(View.GONE);
                } else {
                    txt_digit_one_setting_password.setVisibility(View.VISIBLE);
                    txt_digit_one_setting_password.setText(digitList.get(0));
                    txt_digit_two_setting_password.setVisibility(View.GONE);
                    txt_digit_three_setting_password.setVisibility(View.GONE);
                    txt_digit_four_setting_password.setVisibility(View.GONE);
                    txt_digit_five_setting_password.setVisibility(View.GONE);
                    txt_digit_six_setting_password.setVisibility(View.GONE);
                }
            }
        } else {

            if (digitList.size() < 6) {
                digitList.add(digit);
            }

            if (digit_one_setting_password.getVisibility() == View.VISIBLE && digit_two_setting_password.getVisibility() == View.VISIBLE
                    && digit_three_setting_password.getVisibility() == View.VISIBLE && digit_four_setting_password.getVisibility() == View.VISIBLE
                    && digit_five_setting_password.getVisibility() == View.VISIBLE) {
                digit_one_setting_password.setVisibility(View.VISIBLE);
                digit_two_setting_password.setVisibility(View.VISIBLE);
                digit_three_setting_password.setVisibility(View.VISIBLE);
                digit_four_setting_password.setVisibility(View.VISIBLE);
                digit_five_setting_password.setVisibility(View.VISIBLE);
                digit_six_setting_password.setVisibility(View.VISIBLE);
            } else if (digit_one_setting_password.getVisibility() == View.VISIBLE && digit_two_setting_password.getVisibility() == View.VISIBLE
                    && digit_three_setting_password.getVisibility() == View.VISIBLE && digit_four_setting_password.getVisibility() == View.VISIBLE) {
                digit_one_setting_password.setVisibility(View.VISIBLE);
                digit_two_setting_password.setVisibility(View.VISIBLE);
                digit_three_setting_password.setVisibility(View.VISIBLE);
                digit_four_setting_password.setVisibility(View.VISIBLE);
                digit_five_setting_password.setVisibility(View.VISIBLE);
                digit_six_setting_password.setVisibility(View.GONE);
            } else if (digit_one_setting_password.getVisibility() == View.VISIBLE && digit_two_setting_password.getVisibility() == View.VISIBLE
                    && digit_three_setting_password.getVisibility() == View.VISIBLE) {
                digit_one_setting_password.setVisibility(View.VISIBLE);
                digit_two_setting_password.setVisibility(View.VISIBLE);
                digit_three_setting_password.setVisibility(View.VISIBLE);
                digit_four_setting_password.setVisibility(View.VISIBLE);
                digit_five_setting_password.setVisibility(View.GONE);
                digit_six_setting_password.setVisibility(View.GONE);
            } else if (digit_one_setting_password.getVisibility() == View.VISIBLE && digit_two_setting_password.getVisibility() == View.VISIBLE) {
                digit_one_setting_password.setVisibility(View.VISIBLE);
                digit_two_setting_password.setVisibility(View.VISIBLE);
                digit_three_setting_password.setVisibility(View.VISIBLE);
                digit_four_setting_password.setVisibility(View.GONE);
                digit_five_setting_password.setVisibility(View.GONE);
                digit_six_setting_password.setVisibility(View.GONE);
            } else if (digit_one_setting_password.getVisibility() == View.VISIBLE) {
                digit_one_setting_password.setVisibility(View.VISIBLE);
                digit_two_setting_password.setVisibility(View.VISIBLE);
                digit_three_setting_password.setVisibility(View.GONE);
                digit_four_setting_password.setVisibility(View.GONE);
                digit_five_setting_password.setVisibility(View.GONE);
                digit_six_setting_password.setVisibility(View.GONE);
            } else {
                digit_one_setting_password.setVisibility(View.VISIBLE);
                digit_two_setting_password.setVisibility(View.GONE);
                digit_three_setting_password.setVisibility(View.GONE);
                digit_four_setting_password.setVisibility(View.GONE);
                digit_five_setting_password.setVisibility(View.GONE);
                digit_six_setting_password.setVisibility(View.GONE);
            }
        }
    }

    private static void onBackspaceClick(String actString) {

        if (digitList.size() != 0) {
            digitList.remove(digitList.size() - 1);
        }

        if (actString.equals("ChangePasswordSetting") || actString.equals("ChangePasswordReport") || actString.equals("DiagnosticsPasswordSetting") || actString.equals("BluetoothDisconnectPasswordSetting")) {
            if (txt_digit_one_setting_password.getVisibility() == View.VISIBLE && txt_digit_two_setting_password.getVisibility() == View.VISIBLE
                    && txt_digit_three_setting_password.getVisibility() == View.VISIBLE && txt_digit_four_setting_password.getVisibility() == View.VISIBLE
                    && txt_digit_five_setting_password.getVisibility() == View.VISIBLE && txt_digit_six_setting_password.getVisibility() == View.VISIBLE) {
                txt_digit_one_setting_password.setVisibility(View.VISIBLE);
                txt_digit_one_setting_password.setText(digitList.get(0));
                txt_digit_two_setting_password.setVisibility(View.VISIBLE);
                txt_digit_two_setting_password.setText(digitList.get(1));
                txt_digit_three_setting_password.setVisibility(View.VISIBLE);
                txt_digit_three_setting_password.setText(digitList.get(2));
                txt_digit_four_setting_password.setVisibility(View.VISIBLE);
                txt_digit_four_setting_password.setText(digitList.get(3));
                txt_digit_five_setting_password.setVisibility(View.VISIBLE);
                txt_digit_five_setting_password.setText(digitList.get(4));
                txt_digit_six_setting_password.setVisibility(View.GONE);
            } else if (txt_digit_one_setting_password.getVisibility() == View.VISIBLE
                    && txt_digit_two_setting_password.getVisibility() == View.VISIBLE
                    && txt_digit_three_setting_password.getVisibility() == View.VISIBLE && txt_digit_four_setting_password.getVisibility() == View.VISIBLE
                    && txt_digit_five_setting_password.getVisibility() == View.VISIBLE) {
                txt_digit_one_setting_password.setVisibility(View.VISIBLE);
                txt_digit_one_setting_password.setText(digitList.get(0));
                txt_digit_two_setting_password.setVisibility(View.VISIBLE);
                txt_digit_two_setting_password.setText(digitList.get(1));
                txt_digit_three_setting_password.setVisibility(View.VISIBLE);
                txt_digit_three_setting_password.setText(digitList.get(2));
                txt_digit_four_setting_password.setVisibility(View.VISIBLE);
                txt_digit_four_setting_password.setText(digitList.get(3));
                txt_digit_five_setting_password.setVisibility(View.GONE);
                txt_digit_six_setting_password.setVisibility(View.GONE);
            } else if (txt_digit_one_setting_password.getVisibility() == View.VISIBLE
                    && txt_digit_two_setting_password.getVisibility() == View.VISIBLE
                    && txt_digit_three_setting_password.getVisibility() == View.VISIBLE && txt_digit_four_setting_password.getVisibility() == View.VISIBLE) {
                txt_digit_one_setting_password.setVisibility(View.VISIBLE);
                txt_digit_one_setting_password.setText(digitList.get(0));
                txt_digit_two_setting_password.setVisibility(View.VISIBLE);
                txt_digit_two_setting_password.setText(digitList.get(1));
                txt_digit_three_setting_password.setVisibility(View.VISIBLE);
                txt_digit_three_setting_password.setText(digitList.get(2));
                txt_digit_four_setting_password.setVisibility(View.GONE);
                txt_digit_five_setting_password.setVisibility(View.GONE);
                txt_digit_six_setting_password.setVisibility(View.GONE);
            } else if (txt_digit_one_setting_password.getVisibility() == View.VISIBLE && txt_digit_two_setting_password.getVisibility() == View.VISIBLE
                    && txt_digit_three_setting_password.getVisibility() == View.VISIBLE) {
                txt_digit_one_setting_password.setVisibility(View.VISIBLE);
                txt_digit_one_setting_password.setText(digitList.get(0));
                txt_digit_two_setting_password.setVisibility(View.VISIBLE);
                txt_digit_two_setting_password.setText(digitList.get(1));
                txt_digit_three_setting_password.setVisibility(View.GONE);
                txt_digit_four_setting_password.setVisibility(View.GONE);
                txt_digit_five_setting_password.setVisibility(View.GONE);
                txt_digit_six_setting_password.setVisibility(View.GONE);
            } else if (txt_digit_one_setting_password.getVisibility() == View.VISIBLE && txt_digit_two_setting_password.getVisibility() == View.VISIBLE) {
                txt_digit_one_setting_password.setVisibility(View.VISIBLE);
                txt_digit_one_setting_password.setText(digitList.get(0));
                txt_digit_two_setting_password.setVisibility(View.GONE);
                txt_digit_three_setting_password.setVisibility(View.GONE);
                txt_digit_four_setting_password.setVisibility(View.GONE);
                txt_digit_five_setting_password.setVisibility(View.GONE);
                txt_digit_six_setting_password.setVisibility(View.GONE);
            } else if (txt_digit_one_setting_password.getVisibility() == View.VISIBLE) {
                txt_digit_one_setting_password.setVisibility(View.GONE);
                txt_digit_two_setting_password.setVisibility(View.GONE);
                txt_digit_three_setting_password.setVisibility(View.GONE);
                txt_digit_four_setting_password.setVisibility(View.GONE);
                txt_digit_five_setting_password.setVisibility(View.GONE);
                txt_digit_six_setting_password.setVisibility(View.GONE);
            }
        } else {
            if (digit_one_setting_password.getVisibility() == View.VISIBLE && digit_two_setting_password.getVisibility() == View.VISIBLE
                    && digit_three_setting_password.getVisibility() == View.VISIBLE && digit_four_setting_password.getVisibility() == View.VISIBLE
                    && digit_five_setting_password.getVisibility() == View.VISIBLE && digit_six_setting_password.getVisibility() == View.VISIBLE) {
                digit_one_setting_password.setVisibility(View.VISIBLE);
                digit_two_setting_password.setVisibility(View.VISIBLE);
                digit_three_setting_password.setVisibility(View.VISIBLE);
                digit_four_setting_password.setVisibility(View.VISIBLE);
                digit_five_setting_password.setVisibility(View.VISIBLE);
                digit_six_setting_password.setVisibility(View.GONE);
            } else if (digit_one_setting_password.getVisibility() == View.VISIBLE && digit_two_setting_password.getVisibility() == View.VISIBLE
                    && digit_three_setting_password.getVisibility() == View.VISIBLE && digit_four_setting_password.getVisibility() == View.VISIBLE
                    && digit_five_setting_password.getVisibility() == View.VISIBLE) {
                digit_one_setting_password.setVisibility(View.VISIBLE);
                digit_two_setting_password.setVisibility(View.VISIBLE);
                digit_three_setting_password.setVisibility(View.VISIBLE);
                digit_four_setting_password.setVisibility(View.VISIBLE);
                digit_five_setting_password.setVisibility(View.GONE);
                digit_six_setting_password.setVisibility(View.GONE);
            } else if (digit_one_setting_password.getVisibility() == View.VISIBLE && digit_two_setting_password.getVisibility() == View.VISIBLE && digit_three_setting_password.getVisibility() == View.VISIBLE && digit_four_setting_password.getVisibility() == View.VISIBLE) {
                digit_one_setting_password.setVisibility(View.VISIBLE);
                digit_two_setting_password.setVisibility(View.VISIBLE);
                digit_three_setting_password.setVisibility(View.VISIBLE);
                digit_four_setting_password.setVisibility(View.GONE);
                digit_five_setting_password.setVisibility(View.GONE);
                digit_six_setting_password.setVisibility(View.GONE);
            } else if (digit_one_setting_password.getVisibility() == View.VISIBLE && digit_two_setting_password.getVisibility() == View.VISIBLE && digit_three_setting_password.getVisibility() == View.VISIBLE) {
                digit_one_setting_password.setVisibility(View.VISIBLE);
                digit_two_setting_password.setVisibility(View.VISIBLE);
                digit_three_setting_password.setVisibility(View.GONE);
                digit_four_setting_password.setVisibility(View.GONE);
                digit_five_setting_password.setVisibility(View.GONE);
                digit_six_setting_password.setVisibility(View.GONE);
            } else if (digit_one_setting_password.getVisibility() == View.VISIBLE && digit_two_setting_password.getVisibility() == View.VISIBLE) {
                digit_one_setting_password.setVisibility(View.VISIBLE);
                digit_two_setting_password.setVisibility(View.GONE);
                digit_three_setting_password.setVisibility(View.GONE);
                digit_four_setting_password.setVisibility(View.GONE);
                digit_five_setting_password.setVisibility(View.GONE);
                digit_six_setting_password.setVisibility(View.GONE);
            } else if (digit_one_setting_password.getVisibility() == View.VISIBLE) {
                digit_one_setting_password.setVisibility(View.GONE);
                digit_two_setting_password.setVisibility(View.GONE);
                digit_three_setting_password.setVisibility(View.GONE);
                digit_four_setting_password.setVisibility(View.GONE);
                digit_five_setting_password.setVisibility(View.GONE);
                digit_six_setting_password.setVisibility(View.GONE);
            }
        }
    }



    public static String getCurrentTimeStamp() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // yyyy-MM-dd HH:mm:ss
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    public static String getCurrentTimeOnly() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdfDate = new SimpleDateFormat("hh:mm:ss aa"); // hh:mm:ss aa
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    public static String getCurrentTimeOnlyInMin() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdfDate = new SimpleDateFormat("hh:mm aa"); // hh:mm:ss aa
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    public static Date getDateFromString(String date){
        Date mDate = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            mDate = sdf.parse(date);
        } catch (ParseException ex) {
            Log.v("Exception", ex.getLocalizedMessage());
        }
        return mDate;
    }

    public static Date getshortDateFromString(String date)
    {
        Date mDate = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            mDate = sdf.parse(date);
        } catch (ParseException ex) {
            Log.v("Exception", ex.getLocalizedMessage());
        }
        return mDate;
    }

    public static long getDateDiff(SimpleDateFormat format, String oldDate, String newDate) {
        try {
            return TimeUnit.DAYS.convert(format.parse(newDate).getTime() - format.parse(oldDate).getTime(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String getDateInString(Date date) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // yyyy-MM-dd HH:mm:ss
        String strDate = sdfDate.format(date);
        return strDate;
    }

    public static String getshortDateInString(Date date) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd"); // yyyy-MM-dd HH:mm:ss
        String strDate = sdfDate.format(date);
        return strDate;
    }

    public static void setSoftInputAlwaysHide(Activity act){
//        act.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        act.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        act.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    public static void hideNavigationBar(Activity act) {
        currentApiVersion = android.os.Build.VERSION.SDK_INT;
        //Just disable this line (| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) in below flags to show the navigation bar always

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE;

        // This work only for android 4.4+
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT)
        {

            act.getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = act.getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
                    {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility)
                        {
                            if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                            {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }
    }

    public static final String ACTION_USB_PERMISSION = "com.allentownblower.USB_PERMISSION";
    public static final BroadcastReceiver broadcast_reciever = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(device != null){
                            //call method to set up device communication
                            Toast.makeText(context, "Permission granted for device.!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(context, "permission denied for device", Toast.LENGTH_SHORT).show();
//                        Log.d("TAG", "Permission denied for device.!" + device);
                    }
                }
            }
        }
    };
    public static void getBroadCast(Activity act){
        boolean isUSBDetected = checkUSB(act);
        if (isUSBDetected){
            Log.e("TAG", "ACTION_USB_PERMISSION " + ACTION_USB_PERMISSION);
            try
            {
                usbManager = (UsbManager) act.getSystemService(Context.USB_SERVICE);
                PendingIntent permissionIntent = PendingIntent.getBroadcast(act, 0, new Intent(ACTION_USB_PERMISSION), 0);
                IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                act.unregisterReceiver(broadcast_reciever);
                act.registerReceiver(broadcast_reciever, filter);
                usbManager.requestPermission(device, permissionIntent);
            }
            catch (Exception ex)
            {
                Log.e("TAG","USB Permission Check Error : "+ex.getMessage().toString());
            }

        }
    }

    public static boolean checkUSB(Activity act) {

        boolean Usb = false;
        UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(act);

        if (devices.length == 1)
            Usb = true;

//        boolean Usb = false;
//        UsbManager manager = (UsbManager) act.getSystemService(Context.USB_SERVICE);
//        // Get the list of attached devices
//        HashMap<String, UsbDevice> devices = manager.getDeviceList();
//        // Iterate over all devices
//        Iterator<String> it = devices.keySet().iterator();
//        while (it.hasNext()) {
//            String deviceName = it.next();
//            device = devices.get(deviceName);
////            String VID = Integer.toHexString(device.getVendorId()).toUpperCase();
////            String PID = Integer.toHexString(device.getProductId()).toUpperCase();
//            Usb = true;
////            if (!manager.hasPermission(device)) {
////                PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
////                manager.requestPermission(device, mPermissionIntent);
////                registerReceiver(broadcast_reciever, new IntentFilter(ACTION_USB_PERMISSION));
////                Usb = true;
////                return Usb;
////            } else {
////                //user permission already granted; prceed to access USB device
////                Usb = true;
////                registerReceiver(broadcast_reciever, new IntentFilter(ACTION_USB_PERMISSION));
////                return Usb;
////            }
//        }
        return Usb;
    }

    public static void showAlertDialog(Activity activity, String strMessage, String strBtn) {
        final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();

        // Setting Dialog Title
        alertDialog.setTitle(R.string.app_name);

        // Setting Dialog Message
        alertDialog.setMessage(strMessage);

        // Setting OK Button
        alertDialog.setButton(strBtn, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
            }
        });

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Showing Alert Message
                try {
                    if (alertDialog != null && !alertDialog.isShowing())
                        alertDialog.show();
                } catch (WindowManager.BadTokenException e) {
//                    MyUtils.Log(e.toString());
                }
            }
        });
    }

    public static void Log(Object msg) {
        Log.e("AllenTown App : ", msg + "");
    }

    public static byte[] convertFileToByteArray(File f) {
        byte[] byteArray = null;
        try {
            InputStream inputStream = new FileInputStream(f);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024 * 8];
            int bytesRead = 0;

            while ((bytesRead = inputStream.read(b)) != -1) {
                bos.write(b, 0, bytesRead);
            }

            byteArray = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    //below method we do not need to use as it doesn't make any difference. we have it just for knowledge purpose
    public static void changeScreenBrightness(Context context, int screenBrightnessValue)
    {
        //Log("Brightness value : " + String.valueOf(screenBrightnessValue));
        // Change the screen brightness change mode to manual.
        //Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        // Apply the screen brightness value to the system, this will change the value in Settings ---> Display ---> Brightness level.
        // It will also change the screen brightness for the device.
        //Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, screenBrightnessValue);

        /*
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.screenBrightness = screenBrightnessValue / 255f;
        window.setAttributes(layoutParams);
        */
    }

}
