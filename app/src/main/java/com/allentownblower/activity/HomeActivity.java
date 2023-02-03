package com.allentownblower.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Html;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.allentownblower.BuildConfig;
import com.allentownblower.R;
import com.allentownblower.adapter.MultipleSelectionAdapter;
import com.allentownblower.adapter.MultipleSelectionForMinutesAdapter;
import com.allentownblower.application.AllentownBlowerApplication;
import com.allentownblower.application.BaseActivity;
import com.allentownblower.bluetooth.BluetoothListDialogs;
import com.allentownblower.bluetooth.CommunicationController;
import com.allentownblower.bluetooth.Constants;
import com.allentownblower.common.ApiHandler;
import com.allentownblower.common.CodeReUse;
import com.allentownblower.common.ObserverActionID;
import com.allentownblower.common.PendingID;
import com.allentownblower.common.PrefManager;
import com.allentownblower.common.ResponseHandler;
import com.allentownblower.common.Utility;
import com.allentownblower.communication.SerialPortConversion;
import com.allentownblower.database.SqliteHelper;
import com.allentownblower.module.DiagnosticsCommand;
import com.allentownblower.module.FeedbackCommand;
import com.allentownblower.module.MultipleSelection;
import com.allentownblower.module.RackDetailsModel;
import com.allentownblower.module.RackModel;
import com.allentownblower.module.SetPointCommand;
import com.allentownblower.module.TableViewModel;
import com.allentownblower.module.WiFiCommand;
import com.allentownblower.service.DiagnosticsCommandService;
import com.allentownblower.service.MyService;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mayur Yadav on 01 Sep 2019.
 */
public class HomeActivity extends BaseActivity implements Observer {

    private static final String TAG = "HomeActivity";

    // Common Initialization
    private Activity act;
    private AllentownBlowerApplication allentownBlowerApplication;
    private SerialPortConversion portConversion;
    private ResponseHandler responseHandler;
    private PrefManager prefManager;
    private Handler handler = new Handler();
    private Dialog alertview_selection, alertview_wifiCommunication, alertView_Export_Delete_Record, alertview_dialog_for_s_command;
    private Dialog alertview_diagnostics, alertview_diagnostics_details;
    private Dialog alertview_pre_filter_reset;

    // CommonViews
    private RelativeLayout bluetooth_layout, main_layout, setting_layout, sub_setting_layout, report_layout;
    private TextView txt_SearchNearBlower_Android, txt_SearchNearBlower_Bluetooth, txt_timer, txt_version, txt_isDecon, txt_isBluetooth;

    // AllMainViews
    private LinearLayout layout_filter_screen, layout_blwrdetl_screen, layout_alarm_screen, layout_home_screen;

    // AllMainMenus
    private LinearLayout layout_menubar;
    private RelativeLayout layout_filter_menu, layout_blwrdetl_menu, layout_alert_menu, layout_home_menu, layout_setting_menu;

    // FilterActivity
    private ImageView img_Pre_Filter_Reset;
    private TextView txt_F11_XX, txt_S12_XXYY_F10_XXYY, txt_F11_YY;
    private TextView txt_F10_XXYY;
    private TextView txt_Yes_Pre_Filter_Reset, txt_No_Pre_Filter_Reset;

    // BlwrDetlActivity
    private TextView txt_F04_XXYY, txt_F05_XXYY, txt_F06_XXYY, txt_F07_XX;

    // AlarmActivity
    private TextView txt_F12_Z0, txt_F12_Z1, txt_F12_Z2, txt_F12_X0, txt_F12_X1, txt_F12_X2;
    private TextView txt_F13_X2, txt_F12_Z0X0, txt_F13_W3, txt_F13_W0X3, txt_F13_W1W2;

    // HomeActivity
    private ImageView img_logo_one, img_logo_two, img_home_screen_plus, img_home_screen_minus, img_home_screen_up, img_home_screen_down, img_screen_type_logo;
    private ImageView img_home_screen_one, img_home_screen_two, img_home_screen_three, img_home_screen_four;
    private ImageView img_home_menu_one, img_home_menu_two, img_home_menu_three, img_home_menu_four, img_home_menu_five;
    private View view_home_screen_one, view_home_screen_two, view_home_screen_three;
    private View view_home_menu_one, view_home_menu_two, view_home_menu_three, view_home_menu_four;
    private LinearLayout txt_S01_Z3_Plus, txt_S01_Z3_Minus, txt_S08_Up, txt_S08_Down;
    private TextView txt_F01_XX, txt_F01_YY, txt_F02_XX, txt_F02_YY, txt_S01_Z3, txt_F08_XXYY;

    // AlertDailogBox
    private TextView txt_dailogTitle, btn_Cancel_Selection, btn_Save_Selection, txt_dailogMassage, btn_dailogTitle1, btn_dailogTitle2;
    private IndicatorSeekBar seekbar_Progress_Selection;
    private TextView txt_MinSeekbar_Progress, txt_MaxSeekbar_Progress, txt_SelectedSeekbar_Progress;
    private ImageView txt_Minus_Progress, txt_Plus_Progress;
    private RecyclerView rv_MultipleSelection;
    private MultipleSelectionAdapter multipleSelectionAdapter;
    private MultipleSelectionForMinutesAdapter multipleSelectionForMinutesAdapter;

    // SettingsActivity
    private ImageView main_supplyBlower_menu, main_exhaustBlower_menu, main_filter_menu, main_communication_menu, main_unit_menu, main_decon_menu, main_password_menu, main_nightMode_menu, main_home_menu;

    //ReportSettingActivity
    private ImageView main_home_menu_report, main_password_menu_report, main_report_menu, main_racksetup_screen_1, main_communication_menu_report_timer_1, main_rack_detail_report_1;

    // ReportSetting TextView
    private TextView main_communication_menu_report_timer;

    //ReportSetting RelativeLayout
    private RelativeLayout main_racksetup_screen, main_rack_detail_report;

    // Sub SettingsActivity
    private RelativeLayout layout_backTOhome_menu, layout_backTOsetting_menu;
    private TextView txt_ApplyChange_SettingScreen, txt_isBluetooth_SettingScreen;
    private EditText edit_EnterTxt_alartview_box_min, edit_EnterTxt_alartview_box_max;
    private LinearLayout supplyBlowerSetting_layout, exhaustBlowerSetting_layout, filterSetting_layout, communicationSetting_layout, unitSetting_layout, deconSetting_layout, passwordSetting_layout, nightModeSetting_layout;

    // SupplyBlowerSettingActivity
    private TextView txt_F14_XXXX, txt_S09_SB_XYYY, txt_F07_YY, txt_S10_SB_XYYY, txt_F07_XX_subSetting, txt_S06_SB_XXXX, txt_S01_Y3;

    // ExhaustBlowerSettingActivity
    private TextView txt_F09_XXXX, txt_S13_XXXX, txt_S14_XXXX, txt_S10_XYYY, txt_S06_XXXX, txt_S15_Z0, txt_S16_XXYY, txt_S17_XXYY;

    // FilterSettingActivity
    private TextView txt_S03_XXXX, txt_S04_XXXX, txt_S05_XXXX, txt_S11_YY, txt_S11_XX, txt_F10_XXXX, txt_S12_XXXX;

    // CommunicationSettingActivity
    private LinearLayout communicationSetting_wifiData_layout;
    private TextView txt_WIFI_id, txt_S01_X1_X0;
    private TextView txt_NETWORK_SSID, txt_SECURITY_MODE, txt_SECURITY_KEY, txt_SECURITY_KEY_VALUE, txt_COUNTRY_CODE, txt_DHCP, txt_IP_ADDRESS, txt_SUBNET, txt_DEFAULT_GATEWAY, txt_DNS_SERVER, txt_GATEWAY_PING;

    // Dailog CommunicationSettingActivity
    private EditText edit_EnterTxt_alartview_box;
    private TextView txt_Title_alartview_box, btn_Cancel_alartview_box, btn_Save_alartview_box;
    private LinearLayout layout_dailog_S01_X1_X0, layout_dailog_timer, linear_layout_dialog_buttons;
    private TextView txt_dailog_S01_X1_X0, txt_dailog_timmer;

    // UnitSettingActivity
    private TextView txt_S01_Y0, txt_S01_Y1, txt_S01_Y2;

    // DeconSettingActivity
    private TextView txt_S02_XX, txt_S02_YY, txt_S21_XX_YY;

    // PasswordSettingActivity
    private RelativeLayout layout_SettingPassword, layout_ReportPassword, layout_DiagnosticsPassword, layout_DiagnosticsDetailsPassword, layout_BluetoothDisconnectPassword;
    private TextView txt_SettingPassword, txt_ReportPassword, txt_DiagnosticsPassword, txt_DiagnosticsDetailsPassword, txt_BluetoothDisconnectPassword;
    private TextView txt_ChangePassword_Setting, txt_DiagnosticsPassword_Setting, txt_DiagnosticsDetailsPassword_Setting, txt_BluetoothDisconnectPassword_Setting, txt_ChangePassword_Report;

    // NightModeSettingActivity
    private TextView txt_currentTime_nightModeSetting, txt_OnTime_nightModeSetting, txt_OffTime_nightModeSetting;
    private boolean nightModeStatus = false;

    // DiagnosticsSettingActivity
    private TextView txt_D22_XXXX, txt_D23_XXXX, txt_D24_XXXX, txt_D25_XXXX, txt_D26_XXXX, txt_D27_XXXX, txt_D28_XXXX, txt_D29_XXXX, txt_D30_XXXX, txt_D25_D24_XXXX, txt_D29_D28_XXXX, txt_D31;

    private ProgressBar mProgressBar;

    // DiagnosticsDetailsSettingActivity
    private TextView txt_D10_XXXX, txt_D11_XXXX, txt_F13_Z2, txt_diagnostics_details_close, txt_F16_XXXX;
    private IndicatorSeekBar seekbar_Progress_SupplyPWMValue, seekbar_Progress_ExhaustPWMValue;

    // GetData
    private ArrayList<FeedbackCommand> feedbackArrayList = new ArrayList<>(); // "F01","F02","F03","F04","F05","F06","F07","F08","F09","F10","F11","F12","F13","F14"
    private ArrayList<SetPointCommand> setpointArrayList = new ArrayList<>(); // "S01","S02","S03","S04","S05","S06","S07","S08","S09","S10","S11","S12","S13","S14","S15","S16","S17","S18","S19","S20","S21"
    private ArrayList<DiagnosticsCommand> diagnosticsArrayList = new ArrayList<>(); // "D01","D02","D03","D04","D05","D06","D07","D08","D09","D10","D11","D12","D13","D14","D15","D16","D17","D18","D19","D20","D21","D22","D23","D24","D25","D26","D27","D28","D29","D30","D31"
    private ArrayList<WiFiCommand> wifiArrayList = new ArrayList<>(); // "W01","W02","W03","W04","W05","W06","W07","W08","W09","W10","W11","W12"
    private ArrayList<MultipleSelection> multipleSelections = new ArrayList<>();  // 000 = PITOT-R , 001 = PITOT-C , 010 = CAGE-R , 011 = CAGE-C , 100 = VEL-R , 101 = VEL-C //   *** // 0 = Neg(-) , 1 = Pos(+)
    private ArrayList<MultipleSelection> multipleSelections0 = new ArrayList<>(); // 0 = F , 1 = C
    private ArrayList<MultipleSelection> multipleSelections1 = new ArrayList<>(); // 0 = WC , 1 = Pa
    private ArrayList<MultipleSelection> multipleSelections2 = new ArrayList<>(); // 0 = CFM , 1 = CMH
    private ArrayList<MultipleSelection> multipleSelections3 = new ArrayList<>(); // 0 = disabled , 1 = enabled
    private ArrayList<MultipleSelection> multipleSelections4 = new ArrayList<>(); // 00 = None , 10 = Bluetooth , 01 = WiFi , 11 = Roving
    private ArrayList<MultipleSelection> multipleSelections5 = new ArrayList<>(); // 0 = no IP address , 1 = module connected, IP obtained
    private ArrayList<MultipleSelection> multipleSelections6 = new ArrayList<>(); // 0 = initializing the module , 1 = initialize complete
    private ArrayList<MultipleSelection> multipleSelections7 = new ArrayList<>(); // 1 = NO ENCRYPTION , 2 = WEP64 , 3 = WEP128 , 4 = WPA , 5 = WPA2
    private ArrayList<MultipleSelection> multipleSelections8 = new ArrayList<>(); // 0 = STATIC , 1 = DYNAMIC
    private ArrayList<MultipleSelection> multipleSelections9 = new ArrayList<>(); // usa = 0, canada = 1, europe = 2, spain = 3, france = 4 and japan = 5
    private ArrayList<MultipleSelection> multipleSelections10 = new ArrayList<>(); // usa = 0, canada = 1, europe = 2, spain = 3, france = 4 and japan = 5

    boolean isInnerFunction = true;

    // Bluetooth Connection
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mDevice;
    private Handler mHandler_Android, mHandler_Bluetooth;
    private CommunicationController communicationController;

    private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    private Intent service_diagnostic, service_myservice;

    private Handler mHandler = new Handler();

    private RelativeLayout mRelativeProgressBarLayoutSetting, mRelativeProgressBarLayoutSubSetting, mRelativeProgressBarLayoutDialog;

    private boolean hasS_F_got_response = false;
    private boolean isRedYellowColorHomeScreen;
    private boolean isPreFilterResetYesButtonClicked;
    private boolean isPreFilterResetClicked = false;
    private SqliteHelper sqliteHelper;
    private ProgressBar progress_Pre_Filter_Reset;
    private boolean isWifiYesButtonClicked = false;
    CountDownTimer countDownTimer;
    private boolean isCommunicationButtonClicked;
    private int currentApiVersion;
    private TableViewModel tableViewModel;
    private String typeUnitValue = "";
    private TextView btn_Ok_alartview_box;
    private Dialog alertview_reset_password;
    private static final String DEBUG_TAG = "Gestures";
    private GestureDetector mDetector;
    private RackDetailsModel rackDetailsModel;
    private TextView txt_S022_Value, txt_S023_Value, txt_S024_Value, txt_S025_Value;
    private TextView txt_Supply_Temp, txt_Supply_Humidity, txt_Exhaust_Temp, txt_Exhaust_Humidity;
    private RackModel rackModel;

    private Dialog alertview_simple_setting_password;
    private String Flashingcolorvalue = "red";
    private ImageView img_wicom_cloud_logo, img_wicom_cloud_warning;
    private TextView txt_Warning_Alarm, txt_Critical_Alarm;
    private String mBlowerType;

    private String F12ForS26Command;
    private String F13ForS26Command;

    @SuppressLint({"HandlerLeak", "ServiceCast", "MissingPermission"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = getIntent();
        if(intent.hasExtra(Utility.STR_BLOWER_TYPE))
            mBlowerType = getIntent().getExtras().getString(Utility.STR_BLOWER_TYPE);
        Utility.BLOWER_TYPE = mBlowerType;
        if(mBlowerType.equals(Utility.BCU2))
            setContentView(R.layout.activity_home_bcu2);
        else
            setContentView(R.layout.activity_home_spp);


        Log.d(TAG,"Blower type: "+mBlowerType);
        act = this;


        Utility.setSoftInputAlwaysHide(act);

        alertview_diagnostics = new Dialog(act, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        alertview_diagnostics_details = new Dialog(act, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        //Below code is commented as it was there because we were trying to play with the touch screen brightness
        //to change the ts brightness we need to have this permission.
        //because of this it was opening up the setting page of OS on ts to allow ECoBlower app
        //we disable it now so it is not asking for that now. we have code in utility.java to change th system brightness which is also commented.
//        if (!hasWriteSettingsPermission(act)) {
//            changeWriteSettingsPermission(act);
//        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available!", Toast.LENGTH_SHORT).show();
            //finish();
        }

        if (CodeReUse.isBolwerAdmin) {
            //Broadcasts when bond state changes (ie:pairing)
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver, filter);
        }

        // Common
        allentownBlowerApplication = (AllentownBlowerApplication) act.getApplication();
        allentownBlowerApplication.getObserver().addObserver(this);

        prefManager = new PrefManager(act);
        sqliteHelper = new SqliteHelper(act);
        rackDetailsModel = sqliteHelper.getDataFromRackBlowerDetails();
        if (rackDetailsModel != null) {
            responseHandler = new ResponseHandler(act, rackDetailsModel, allentownBlowerApplication, sqliteHelper);
        } else {
            responseHandler = new ResponseHandler(act, sqliteHelper);
        }
        portConversion = new SerialPortConversion(act, responseHandler);

        mDetector = new GestureDetector(act, new MyGestureListener());
        //rackDetailsModel = sqliteHelper.getDataFromRackBlowerDetails();

        // TODO :- Delete Records From Both Table BLFEEDBACK and BLSETPOINT
//        sqliteHelper.deleteRecordOlderThan2Month();

        // variable Initialization means findViewById will be in this Method
        variableInitializationMethod();

        mHandler_Android = new Handler(new Handler.Callback() {
            @SuppressLint("NewApi")
            @Override
            public boolean handleMessage(Message msg) {
                Log.e("mHandler_Android", "mHandler_Android");
                switch (msg.what) {
                    case Constants.MESSAGE_STATE_CHANGE:
                        switch (msg.arg1) {
                            case Constants.STATE_CONNECTED:
                                txt_isBluetooth.setVisibility(View.VISIBLE);
                                txt_isBluetooth_SettingScreen.setVisibility(View.VISIBLE);
                                txt_isBluetooth.setText(mDevice.getName());
                                txt_isBluetooth_SettingScreen.setText(mDevice.getName());
                                if (CodeReUse.isBolwerAdmin) {
                                    bluetooth_layout.setVisibility(View.GONE);
                                    main_layout.setVisibility(View.VISIBLE);
                                    setting_layout.setVisibility(View.GONE);
                                    report_layout.setVisibility(View.GONE);
                                    sub_setting_layout.setVisibility(View.GONE);
                                }
                                CodeReUse.isBolwerConnected = true;

                                // ServiceCommunication
                                /*if (!isMyServiceRunning(MyService.class)) {*/
                                if (service_myservice == null) {
                                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
                                }

                                break;
                            case Constants.STATE_CONNECTING:
                                setTitle("Connecting...");
                                break;
                            case Constants.STATE_LISTEN:
                            case Constants.STATE_NONE:
                                txt_isBluetooth.setVisibility(View.GONE);
                                txt_isBluetooth_SettingScreen.setVisibility(View.GONE);
                                if (CodeReUse.isBolwerAdmin) {
                                    bluetooth_layout.setVisibility(View.VISIBLE);
                                    main_layout.setVisibility(View.GONE);
                                    setting_layout.setVisibility(View.GONE);
                                    report_layout.setVisibility(View.GONE);
                                    sub_setting_layout.setVisibility(View.GONE);
                                }
                                CodeReUse.isBolwerConnected = false;
                                setTitle("Not connected");
                                break;
                        }
                        break;
                    case Constants.MESSAGE_WRITE:
                        byte[] writeBuf = (byte[]) msg.obj;
                        String writeMessage = new String(writeBuf);
                        jsonMessage(writeMessage, true);
                        break;
                    case Constants.MESSAGE_READ:
                        byte[] readBuf = (byte[]) msg.obj;
                        String readMessage = new String(readBuf, 0, msg.arg1);
                        jsonMessage(readMessage, false);
                        try {
                            JSONObject messageJSON = new JSONObject(readMessage);
                            //Toast.makeText(act, messageJSON.getString("message"), Toast.LENGTH_SHORT).show();
                            if (messageJSON.getString("type").equals("feedbackArrayList"))
                                responseHandler.InsertBLFeedbackTable(new JSONArray(messageJSON.getString("message")));
                            else if (messageJSON.getString("type").equals("setpointArrayList"))
                                responseHandler.InsertBLSetPointTable(new JSONArray(messageJSON.getString("message")));
                            else if (messageJSON.getString("type").equals("Command")) {
                                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStopService);
                                Thread.sleep(1000);
                                CallReadWriteFuncation(messageJSON.getString("message"), Integer.parseInt(messageJSON.getString("isStart")));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case Constants.MESSAGE_DEVICE_OBJECT:
                        mDevice = msg.getData().getParcelable(Constants.DEVICE_OBJECT);
                        Toast.makeText(getApplicationContext(), "Connected to " + mDevice.getName(), Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.MESSAGE_TOAST:
                        Toast.makeText(getApplicationContext(), msg.getData().getString("toast"), Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.MESSAGE_LOST:
                        CommunicationController.sleep(500);
                        Toast.makeText(getApplicationContext(), "Reconnected", Toast.LENGTH_SHORT).show();
                        communicationController.connect(mDevice);
                        break;
                }
                return false;
            }
        });

        mHandler_Bluetooth = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == Constants.MESSAGE_READ_Client) {
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj);
                        Log.e(TAG, "Reading received message : " + readMessage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //mReadBuffer.setText(readMessage);
                }

                if (msg.what == Constants.CONNECTING_STATUS_Client) {
                    if (msg.arg1 == 1) {
                        Toast.makeText(act, "Connected to Device: " + msg.obj, Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(act, "Connected Failed", Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Start With Screen
        if (CodeReUse.isBolwerAdmin) {
            bluetooth_layout.setVisibility(View.VISIBLE);
        } else {


            setMenubarBackground();
            /*if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                layout_menubar.setBackgroundDrawable(ContextCompat.getDrawable(act, R.drawable.dark_green_home_screen_box));
            } else {
                layout_menubar.setBackground(ContextCompat.getDrawable(act, R.drawable.dark_green_home_screen_box));
            }*/

            bluetooth_layout.setVisibility(View.GONE);
            if(Utility.BLOWER_TYPE.equals(Utility.BCU2))
                main_layout.setBackgroundColor(ContextCompat.getColor(act, R.color.bg_bcu2));
            else
                main_layout.setBackgroundColor(ContextCompat.getColor(act, R.color.bg_spp));
//            main_layout.setBackgroundColor(ContextCompat.getColor(act, R.color.home_green));
            main_layout.setVisibility(View.VISIBLE);

            layout_home_screen.setVisibility(View.VISIBLE);
            setting_layout.setVisibility(View.GONE);
            report_layout.setVisibility(View.GONE);
            sub_setting_layout.setVisibility(View.GONE);

            NightModeView(nightModeStatus);
        }

        txt_SearchNearBlower_Android.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothSearch(true);
            }
        });

        txt_SearchNearBlower_Bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothSearch(false);
            }
        });

        txt_currentTime_nightModeSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
            }
        });

        //version check click on version text

        txt_version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Utility.ShowMessage(act, "Check Version :",txt_version.getText().toString() + "//" + responseHandler.APKVersion,"OK");
                Log.e("TAG","The Version Text on App Display : " + txt_version.getText().toString());
                String ver = txt_version.getText().toString();
                String appver = ver.substring(0,4);
                Log.e("TAG", "The app version : " + appver);
                //responseHandler.GetServerAPKVersionAPI(txt_version.getText().toString().replace("V",""));
                //responseHandler.GetServerAPKVersionAPI(appver.toString().replace("V",""));
                if (responseHandler.APKVersion.equals(""))
                {
                    responseHandler.GetServerAPKVersionAPI(appver.toString().replace("V",""));
                }
                String serverAPK = responseHandler.APKVersion;
                responseHandler.APKVersion = "";
                Log.e("TAG", "The Server apk version : " + serverAPK);
                double local= Double.parseDouble(appver.toString().replace("V",""));
                if (!serverAPK.equals(""))
                {
                    double server = Double.parseDouble(serverAPK);
                    if (server > local)
                    {
                        Utility.showAlertDialog(act,"Newer Version "+ serverAPK + " is available.\nPlease contact Administrator to update the App.", "Ok" );
                    }
                    else
                    {
                        Utility.showAlertDialog(act,"No updates avaialble.", "Ok" );
                    }
                }
                else
                {
                    Utility.showAlertDialog(act,"Server is not responding. Please try after sometime.", "Ok" );
                }

            }
        });



        // TODO :- Version Code Display
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            feedbackArrayList = responseHandler.getLastFeedbackData();
            String F14_XXXX ="";
            if (feedbackArrayList.size() != 0) {
                //Integer rev = Integer.parseInt(feedbackArrayList.get(0).getF14()) / 100;
                // Log.e("TAG","before ts version");
                F14_XXXX = feedbackArrayList.get(0).getF14();
                //Log.e("TAG","Version : " + F14_XXXX);  //0512
                String first = F14_XXXX.substring(0, 2);
                String second = F14_XXXX.substring(2, 4);
                F14_XXXX = first + "." + second;
            }
            txt_version.setText("V" + version + " / " + F14_XXXX);
            responseHandler.LocalAPKVersion = version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // Clock Timer
        handler.postDelayed(new Runnable() {
            public void run() {
                //txt_timer.setText(Utility.getCurrentTimeStamp());
                SimpleDateFormat frt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");
                Date nowtime = new Date();
                String strdt = frt.format(nowtime);
                txt_timer.setText(strdt);

                txt_currentTime_nightModeSetting.setText(Utility.getCurrentTimeOnly());

                // TODO :- Vacuum Fuction Called Here For Database
                try {


                    SimpleDateFormat sdffortime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa", Locale.getDefault());
                    SimpleDateFormat sdffordate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String ct = sdffordate.format(new Date());
                    ct = ct + " 02:00:00 AM";
                    Date setdt = sdffortime.parse(ct);

                    ct = ct.replace(" 02:00:00 AM", "") + " 02:00:04 AM";
                    Date added = sdffortime.parse(ct);

                    ct = ct.replace(" 02:00:04 AM", "") + " " + Utility.getCurrentTimeOnly();
                    Date now = sdffortime.parse(ct);
                    //Log.e("TAG", "Inside : Setdt : " + setdt + "  now: " + now + "  Added :   " + added);
                    //Inside : Setdt : Thu Feb 02 02:00:00 EST 2023  now: Thu Feb 02 14:39:52 EST 2023  Added :   Thu Feb 02 02:00:04 EST 2023
                    //Log.e("TAG","Outside : Setdt : " + setdt + "  now: " + now + "  Added :   " + added);
                    if (now.after(setdt) && now.before(added)) {
                        Thread.sleep(4000);
                        Log.e("TAG", "Inside : Setdt : " + setdt + "  now: " + now + "  Added :   " + added);
                        Log.e("TIME", "DB Vacuum started");
                        sqliteHelper.vacuum();
                        sqliteHelper.deleteRecordOlderThan2Month();
//                    sqliteHelper.deleteRecordOlderThan2Month();
                        Log.e("TIME", "Vacuum has been finished.");
                    }
                } catch (ParseException | InterruptedException ex) {
                    // handle parsing exception if date string was different from the pattern applying into the SimpleDateFormat contructor
                    ex.printStackTrace();
                }


//                if (Utility.getCurrentTimeOnly().equals("02:00:00 AM")) {
//                    Log.e("TIME", "DB Vacuum started");
//                    sqliteHelper.vacuum();
////                    sqliteHelper.deleteRecordOlderThan2Month();
//                    Log.e("TIME", "Vacuum has been finished.");
//                }

                if (layout_home_screen.getVisibility() == View.VISIBLE) {

                    try {
/*
                        SimpleDateFormat sdffordatetime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa", Locale.getDefault());
                        SimpleDateFormat sdffordate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String currentdt = sdffordatetime.format(new Date());

                        String onDt = sdffordate.format(new Date()) + " " + prefManager.getOnNightMode();
                        String offDt = sdffordate.format(new Date()) + " " + prefManager.getOffNightMode();

                        Date CurDttime = sdffordatetime.parse(currentdt);
                        Date OnDttime =  sdffordatetime.parse(onDt);
                        Date OffDttime =  sdffordatetime.parse(offDt);
                        Log.e("TAG", "New On time : " + String.valueOf(OnDttime) + " New Current Time : " + String.valueOf(CurDttime) + "  New Off time : " + String.valueOf(OffDttime));
                        Log.e("TAG", "New On time : " + String.valueOf(OnDttime.getTime()) + " New Current Time : " + String.valueOf(CurDttime.getTime()) + "  New Off time : " + String.valueOf(OffDttime.getTime()));
                        //as per ken touchscreen windows CE logic
                        if (OffDttime.getTime() == OnDttime.getTime())
                        {
                            nightModeStatus = false;
                            Log.e("TAG","As per ken night mode off when both are same");
                        }
                        else
                        {
                            if (OnDttime.getTime() > OffDttime.getTime())
                            {
                                if (!nightModeStatus)
                                {
                                    if (CurDttime.getTime() >= OnDttime.getTime() || CurDttime.getTime() < OffDttime.getTime())
                                    {
                                        nightModeStatus = true;
                                        Log.e("TAG","As per ken night mode On 1st");
                                    }
                                }
                                else
                                {
                                    if (CurDttime.getTime() < OnDttime.getTime() && CurDttime.getTime() >= OffDttime.getTime())
                                    {
                                        nightModeStatus = false;
                                        Log.e("TAG","As per ken night mode OFF 2nd");
                                    }
                                }
                            }
                            else
                            {
                                if (!nightModeStatus)
                                {
                                    if (CurDttime.getTime() >= OnDttime.getTime() && CurDttime.getTime() < OffDttime.getTime())
                                    {
                                        nightModeStatus = true;
                                        Log.e("TAG","As per ken night mode On 3rd");
                                    }
                                }
                                else
                                {
                                    if (CurDttime.getTime() < OnDttime.getTime() || CurDttime.getTime() >= OffDttime.getTime())
                                    {
                                        nightModeStatus = false;
                                        Log.e("TAG","As per ken night mode OFF 4th");
                                    }
                                }
                            }
                        }


                        if (OnDttime.getTime()<CurDttime.getTime())
                        {
                            //Log.e("TAG","Status is true with currenttime comparison as per ontime");
                        }
                        else if (OffDttime.after(CurDttime))
                        {
                            //Log.e("TAG","Status is true with currenttime comparison as per OFF OFF Time");
                        }
                        else
                        {
                            //Log.e("TAG","Status is FALSE FALSE comparison");
                        }

                        if (OffDttime.before(OnDttime))
                        {
                            Calendar c = Calendar.getInstance();
                            c.setTime(OffDttime);
                            c.add(Calendar.DATE, 1);
                            OffDttime = c.getTime();
                        }
                        //Log.e("TAG", "New On time : " + String.valueOf(OnDttime) + " New Current Time : " + String.valueOf(CurDttime) + "  New Off time : " + String.valueOf(OffDttime));
                        if (CurDttime.after(OnDttime) && CurDttime.before(OffDttime)) {
                            //Log.e("TAG","Status is true");
                            //nightModeStatus = true;
                        }
                        else
                        {
                            //Log.e("TAG","Status is false");
                            //nightModeStatus = false;
                        }

                        //Old logic and some help. This is not working when our off time is less than on time
                        //like 3:15 pm on time and 3:14 pm off time. As customer in canada wants to keep whole day in night mode..
//

//
////                        Below commented logic for night mode will not work. Checked this as of 2/2/2023
////                        Calendar calendar1 = Calendar.getInstance();
////                        calendar1.setTime(time1);
////                        calendar1.add(Calendar.DATE, 1);
////
////                        Calendar calendar2 = Calendar.getInstance();
////                        calendar2.setTime(time2);
////                        calendar2.add(Calendar.DATE, 1);
////
////                        Calendar calendar3 = Calendar.getInstance();
////                        calendar3.setTime(d);
////                        calendar3.add(Calendar.DATE, 1);
////
////                        Log.e("TAG","On Time : " + calendar1.getTime().toString());
////                        Log.e("TAG","Cu Time : " + calendar3.getTime().toString());
////                        Log.e("TAG","Of Time : " + calendar2.getTime().toString());
////
////                          2023-02-02 14:39:52.529 14901-14901/com.allentownblower E/TAG: On Time : Fri Jan 02 11:58:00 EST 1970
////                          2023-02-02 14:39:52.529 14901-14901/com.allentownblower E/TAG: Cu Time : Fri Jan 02 14:39:52 EST 1970
////                          2023-02-02 14:39:52.529 14901-14901/com.allentownblower E/TAG: Of Time : Fri Jan 02 11:55:00 EST 1970
//
*/
//
                        String string1 = prefManager.getOnNightMode();
                        Date time1 = CodeReUse.newTimeFormat.parse(string1);

                        String string2 = prefManager.getOffNightMode();
                        Date time2 = CodeReUse.newTimeFormat.parse(string2);


                        Date d = CodeReUse.newTimeFormat.parse(Utility.getCurrentTimeOnly());
                        Log.e("TAG", "On time : " + String.valueOf(time1) + " Current Time : " + String.valueOf(d) + "  Off time : " + String.valueOf(time2));
                        long mils = d.getTime() - time1.getTime();
                        double hourDiff = (double) (mils / (1000 * 60 * 60));
                        double minuteFor = (double) (mils / (1000 * 60) % 60);
                        double secondfor = (double) (mils / 1000) % 60;
                        Log.e("TAG", "Difference between now and Ontime : " + String.valueOf(hourDiff) + "   /   " + String.valueOf(minuteFor)+ "   /   " + String.valueOf(secondfor));
                        double x = hourDiff;
                        if (x < 0)
                        {
                            x = x + 24;
                        }
                        else if (minuteFor < 0)
                        {
                            x = x + 24;
                        }
                        else if (secondfor < 0)
                        {
                            x = x + 24;
                        }
                        mils = d.getTime() - time2.getTime();
                        hourDiff = (double) (mils / (1000 * 60 * 60));
                        minuteFor = (double) (mils / (1000 * 60) % 60);
                        secondfor = (double) (mils / 1000) % 60;
                        Log.e("TAG", "Difference between now and OFFtime : " + String.valueOf(hourDiff)+ "   /   " + String.valueOf(minuteFor)+ "   /   " + String.valueOf(secondfor));
                        double y = hourDiff;
                        if (y < 0)
                        {
                            y = y + 24;
                        }
                        else if (minuteFor < 0)
                        {
                            y = y + 24;
                        }
                        else if (secondfor < 0)
                        {
                            y = y + 24;
                        }
                        Log.e("TAG", "time X : " + String.valueOf(x) + " / time Y  : " + String.valueOf(y));

                        if (x < y)
                        {
                            nightModeStatus = true;
                        }
                        else
                        {
                            nightModeStatus = false;
                        }



                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                String[] OnTime = prefManager.getOnNightMode().split(" ");
                String[] OffTime = prefManager.getOffNightMode().split(" ");

                if (Utility.getCurrentTimeOnly().equals(OnTime[0] + " " + OnTime[1])) {
                    Toast.makeText(act, "On Night Mode", Toast.LENGTH_SHORT).show();
                } else if (Utility.getCurrentTimeOnly().equals(OffTime[0] + " " + OffTime[1])) {
                    Toast.makeText(act, "Off Night Mode", Toast.LENGTH_SHORT).show();
                }

                //do something
                handler.postDelayed(this, CodeReUse.TimerInterval);
            }
        }, CodeReUse.TimerInterval);

        Utility.Log("TAG", isMyServiceRunning(MyService.class) + " ==> Service Status");

        // ServiceCommunication
        /*if (!isMyServiceRunning(MyService.class)) {*/
        if (service_myservice == null) {
            // check record on setPoint data is exits or not in database
            if (!prefManager.getOpenNode())
                portConversion.openNode(act);
            else
                Utility.AlertShowMessage(act, "Alert", "serial port not found.", "OK");
        }

        //Check if WebAPI Customer is active or not
        if (prefManager.getHostName() == null || !prefManager.getHostName().contains("http")) {
            Log.e("TAG", "HostName Check on App Launch : Host is Not Available.");
            img_wicom_cloud_logo.setVisibility(View.INVISIBLE);
        }
        else
        {
            Log.e("TAG", prefManager.getHostName() + " Host is available on app launch.");
            img_wicom_cloud_logo.setVisibility(View.VISIBLE);
        }


        // FilterMenu ClickEvent
        FilterMenuClickEvent();

        // BlwrdetlMenu ClickEvent
        BlwrdetlMenuClickEvent();

        // AlertMenu ClickEvent
        AlertMenuClickEvent();

        // HomeMenu ClickEvent
        HomeMenuClickEvent();

        // SettingMenu ClickEvent
        SettingMenuClickEvent();

        // FilterActivity ClickEvent
        FilterClickEvent();

        // HomeActivity ClickEvent
        HomeClickEvent();

        // SettingsActivity ClickEvent
        SettingsClickEvent();

        // ReportsActivity ClickEvent
        ReportsClickEvent();

        // SubSettingsActivity ClickEvent
        SubSettingsClickEvent();

        // SupplyBlowerSettingActivity ClickEvent
        SupplyBlowerSettingClickEvent();

        // ExhaustBlowerSettingActivity ClickEvent
        ExhaustBlowerSettingClickEvent();

        // FilterSettingActivity ClickEvent
        FilterSettingClickEvent();

        // CommunicationSettingActivity ClickEvent
        CommunicationSettingClickEvent();

        // UnitSettingActivity ClickEvent
        UnitSettingClickEvent();

        // DeconSettingActivity ClickEvent
        DeconSettingClickEvent();

        // PasswordSettingActivity ClickEvent
        PasswordSettingClickEvent();

        // NightModeSettingActivity ClickEvent
        NightModeSettingClickEvent();

        // Read And Set AllData
        setAnalysisData();

        removeEcoflowIcon();
        Utility.changeScreenBrightness(getApplicationContext(),1);

        String serialNumber = null;
        if (!CodeReUse.isDebugMode){
            if (Build.VERSION.SDK_INT >= 30) {
                serialNumber = Build.getSerial();
                prefManager.setSerialNumber(serialNumber);
            }
            else
            {
                serialNumber = getSerialNumber();
                prefManager.setSerialNumber(serialNumber);
            }
        }
        else
        {
            serialNumber = "0D1221D4D5A8TEST";
            prefManager.setSerialNumber(serialNumber);
        }

        F12ForS26Command = "";
        F13ForS26Command = "";
        //Log.e("TAG","Touch Screen serial number:" + serialNumber);
        Utility.Log(TAG,"Touch Screen serial number:" + serialNumber + "");
        //Utility.showAlertDialog(act,"Touch Screen serial number:" + serialNumber + "  SDK Version : " + String.valueOf(Build.VERSION.SDK_INT) ,"Ok");
    }

    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.e("TAG","onwindow focust changed event in home activity");
        super.onWindowFocusChanged(hasFocus);
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus) {
            Log.e("TAG","This is inside: onwindow focust changed event in home activity");
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE
            );
        }
    }

    // All variable can be Ids can be Define in this Function
    public void variableInitializationMethod() {

        txt_SearchNearBlower_Android = findViewById(R.id.txt_SearchNearBlower_Android);
        txt_SearchNearBlower_Bluetooth = findViewById(R.id.txt_SearchNearBlower_Bluetooth);
        txt_timer = findViewById(R.id.txt_timer);
        txt_version = findViewById(R.id.txt_version);
        txt_isDecon = findViewById(R.id.txt_isDecon);
        txt_isBluetooth = findViewById(R.id.txt_isBluetooth);

        bluetooth_layout = findViewById(R.id.bluetooth_layout);
        main_layout = findViewById(R.id.main_layout);
        setting_layout = findViewById(R.id.setting_layout);
        report_layout = findViewById(R.id.report_layout);
        sub_setting_layout = findViewById(R.id.sub_setting_layout);

        // AllView FindViewById
        layout_filter_screen = findViewById(R.id.layout_filter_screen);
        layout_blwrdetl_screen = findViewById(R.id.layout_blwrdetl_screen);
        layout_home_screen = findViewById(R.id.layout_home_screen);
        layout_alarm_screen = findViewById(R.id.layout_alarm_screen);

        // AllMenu FindViewById
        layout_menubar = findViewById(R.id.layout_menubar);
        layout_filter_menu = findViewById(R.id.layout_filter_menu);
        layout_blwrdetl_menu = findViewById(R.id.layout_blwrdetl_menu);
        layout_alert_menu = findViewById(R.id.layout_alert_menu);
        layout_home_menu = findViewById(R.id.layout_home_menu);
        layout_setting_menu = findViewById(R.id.layout_setting_menu);

        // FilterActivity FindViewById
        img_Pre_Filter_Reset = findViewById(R.id.img_Pre_Filter_Reset);
        txt_F11_XX = findViewById(R.id.txt_F11_XX);
        txt_S12_XXYY_F10_XXYY = findViewById(R.id.txt_S12_XXYY_F10_XXYY);
        txt_F11_YY = findViewById(R.id.txt_F11_YY);

        // BlwrDetlActivity
        txt_F04_XXYY = findViewById(R.id.txt_F04_XXYY);
        txt_F05_XXYY = findViewById(R.id.txt_F05_XXYY);
        txt_F06_XXYY = findViewById(R.id.txt_F06_XXYY);
        txt_F07_XX = findViewById(R.id.txt_F07_XX);

        // AlarmActivity FindViewById
        txt_F12_Z0 = findViewById(R.id.txt_F12_Z0);
        txt_F12_Z1 = findViewById(R.id.txt_F12_Z1);
        txt_F12_Z2 = findViewById(R.id.txt_F12_Z2);
        txt_F12_X0 = findViewById(R.id.txt_F12_X0);
        txt_F12_X1 = findViewById(R.id.txt_F12_X1);
        txt_F12_X2 = findViewById(R.id.txt_F12_X2);
        txt_F13_X2 = findViewById(R.id.txt_F13_X2);
        txt_F12_Z0X0 = findViewById(R.id.txt_F12_Z0X0);
        txt_F13_W3 = findViewById(R.id.txt_F13_W3);
        txt_F13_W0X3 = findViewById(R.id.txt_F13_W0X3);
        txt_F13_W1W2 = findViewById(R.id.txt_F13_W1W2);

        txt_Supply_Temp = findViewById(R.id.txt_Supply_Temp);
        txt_Supply_Humidity = findViewById(R.id.txt_Supply_Humidity);
        txt_Exhaust_Temp = findViewById(R.id.txt_Exhaust_Temp);
        txt_Exhaust_Humidity = findViewById(R.id.txt_Exhaust_Humidity);

        // HomeActivity FindViewById
        img_wicom_cloud_logo = findViewById(R.id.wi_com_cloud_logo_wh);
        img_wicom_cloud_warning = findViewById(R.id.wi_com_cloud_warning);
        txt_Warning_Alarm = (TextView)findViewById(R.id.txt_Warning_Alarm);
        txt_Critical_Alarm = (TextView)findViewById(R.id.txt_Critical_Alarm);

        img_logo_one = findViewById(R.id.img_logo_one);
        img_logo_two = findViewById(R.id.img_logo_two);
        img_home_screen_plus = findViewById(R.id.img_home_screen_plus);
        img_home_screen_minus = findViewById(R.id.img_home_screen_minus);
        img_home_screen_up = findViewById(R.id.img_home_screen_up);
        img_home_screen_down = findViewById(R.id.img_home_screen_down);
        img_screen_type_logo = findViewById(R.id.img_screen_type_logo);

        img_home_screen_one = findViewById(R.id.img_home_screen_one);
        img_home_screen_two = findViewById(R.id.img_home_screen_two);
        img_home_screen_three = findViewById(R.id.img_home_screen_three);
        img_home_screen_four = findViewById(R.id.img_home_screen_four);

        img_home_menu_one = findViewById(R.id.img_home_menu_one);
        img_home_menu_two = findViewById(R.id.img_home_menu_two);
        img_home_menu_three = findViewById(R.id.img_home_menu_three);
        img_home_menu_four = findViewById(R.id.img_home_menu_four);
        img_home_menu_five = findViewById(R.id.img_home_menu_five);

        view_home_screen_one = findViewById(R.id.view_home_screen_one);
        view_home_screen_two = findViewById(R.id.view_home_screen_two);
        view_home_screen_three = findViewById(R.id.view_home_screen_three);
        view_home_menu_one = findViewById(R.id.view_home_menu_one);
        view_home_menu_two = findViewById(R.id.view_home_menu_two);
        view_home_menu_three = findViewById(R.id.view_home_menu_three);
        view_home_menu_four = findViewById(R.id.view_home_menu_four);

        txt_S01_Z3_Plus = findViewById(R.id.txt_S01_Z3_Plus);
        txt_S01_Z3_Minus = findViewById(R.id.txt_S01_Z3_Minus);
        txt_S08_Up = findViewById(R.id.txt_S08_Up);
        txt_S08_Down = findViewById(R.id.txt_S08_Down);
        txt_F01_XX = findViewById(R.id.txt_F01_XX);
        txt_F01_YY = findViewById(R.id.txt_F01_YY);
        txt_F02_XX = findViewById(R.id.txt_F02_XX);
        txt_F02_YY = findViewById(R.id.txt_F02_YY);
        txt_S01_Z3 = findViewById(R.id.txt_S01_Z3);
        txt_F08_XXYY = findViewById(R.id.txt_F08_XXYY);

        // SettingsActivity FindViewById
        main_supplyBlower_menu = findViewById(R.id.main_supplyBlower_menu);
        main_exhaustBlower_menu = findViewById(R.id.main_exhaustBlower_menu);
        main_filter_menu = findViewById(R.id.main_filter_menu);
        main_communication_menu = findViewById(R.id.main_communication_menu);
        main_unit_menu = findViewById(R.id.main_unit_menu);
        main_decon_menu = findViewById(R.id.main_decon_menu);
        main_password_menu = findViewById(R.id.main_password_menu);
        main_nightMode_menu = findViewById(R.id.main_nightMode_menu);
        main_home_menu = findViewById(R.id.main_home_menu);
        mRelativeProgressBarLayoutSetting = findViewById(R.id.relative_progress_setting);
        mRelativeProgressBarLayoutSubSetting = findViewById(R.id.relative_progress_subsetting);

        // Sub SettingsActivity FindViewById
        supplyBlowerSetting_layout = findViewById(R.id.supplyBlowerSetting_layout);
        exhaustBlowerSetting_layout = findViewById(R.id.exhaustBlowerSetting_layout);
        filterSetting_layout = findViewById(R.id.filterSetting_layout);
        communicationSetting_layout = findViewById(R.id.communicationSetting_layout);
        unitSetting_layout = findViewById(R.id.unitSetting_layout);
        deconSetting_layout = findViewById(R.id.deconSetting_layout);
        passwordSetting_layout = findViewById(R.id.passwordSetting_layout);
        nightModeSetting_layout = findViewById(R.id.nightModeSetting_layout);

        layout_backTOhome_menu = findViewById(R.id.layout_backTOhome_menu);
        layout_backTOsetting_menu = findViewById(R.id.layout_backTOsetting_menu);
        txt_ApplyChange_SettingScreen = findViewById(R.id.txt_ApplyChange_SettingScreen);
        txt_isBluetooth_SettingScreen = findViewById(R.id.txt_isBluetooth_SettingScreen);

        // SupplyBlowerSettingActivity FindViewById
        txt_F14_XXXX = findViewById(R.id.txt_F14_XXXX);
        txt_S09_SB_XYYY = findViewById(R.id.txt_S09_SB_XYYY);
        txt_F07_YY = findViewById(R.id.txt_F07_YY);
        txt_S10_SB_XYYY = findViewById(R.id.txt_S10_SB_XYYY);
        txt_F07_XX_subSetting = findViewById(R.id.txt_F07_XX_subSetting);
        txt_S06_SB_XXXX = findViewById(R.id.txt_S06_SB_XXXX);
        txt_S01_Y3 = findViewById(R.id.txt_S01_Y3);

        // ExhaustBlowerSettingActivity FindViewById
        txt_F09_XXXX = findViewById(R.id.txt_F09_XXXX);
        txt_S13_XXXX = findViewById(R.id.txt_S13_XXXX);
        txt_S14_XXXX = findViewById(R.id.txt_S14_XXXX);
        txt_S10_XYYY = findViewById(R.id.txt_S10_XYYY);
        txt_S06_XXXX = findViewById(R.id.txt_S06_XXXX);
        txt_S15_Z0 = findViewById(R.id.txt_S15_Z0);
        txt_S16_XXYY = findViewById(R.id.txt_S16_XXYY);
        txt_S17_XXYY = findViewById(R.id.txt_S17_XXYY);

        // FilterSettingActivity FindViewById
        txt_S03_XXXX = findViewById(R.id.txt_S03_XXXX);
        txt_S04_XXXX = findViewById(R.id.txt_S04_XXXX);
        txt_S05_XXXX = findViewById(R.id.txt_S05_XXXX);
        txt_S11_YY = findViewById(R.id.txt_S11_YY);
        txt_S11_XX = findViewById(R.id.txt_S11_XX);
        txt_F10_XXXX = findViewById(R.id.txt_F10_XXXX);
        txt_S12_XXXX = findViewById(R.id.txt_S12_XXXX);

        // CommunicationSettingActivity FindViewById
        communicationSetting_wifiData_layout = findViewById(R.id.communicationSetting_wifiData_layout);
        txt_WIFI_id = findViewById(R.id.txt_WIFI_id);
        txt_S01_X1_X0 = findViewById(R.id.txt_S01_X1_X0);

        txt_NETWORK_SSID = findViewById(R.id.txt_NETWORK_SSID);
        txt_SECURITY_MODE = findViewById(R.id.txt_SECURITY_MODE);
        txt_SECURITY_KEY = findViewById(R.id.txt_SECURITY_KEY);
        txt_SECURITY_KEY_VALUE = findViewById(R.id.txt_SECURITY_KEY_VALUE);
        txt_COUNTRY_CODE = findViewById(R.id.txt_COUNTRY_CODE);
        txt_DHCP = findViewById(R.id.txt_DHCP);
        txt_IP_ADDRESS = findViewById(R.id.txt_IP_ADDRESS);
        txt_SUBNET = findViewById(R.id.txt_SUBNET);
        txt_DEFAULT_GATEWAY = findViewById(R.id.txt_DEFAULT_GATEWAY);
        txt_DNS_SERVER = findViewById(R.id.txt_DNS_SERVER);
        txt_GATEWAY_PING = findViewById(R.id.txt_GATEWAY_PING);

        // UnitSettingActivity FindViewById
        txt_S01_Y0 = findViewById(R.id.txt_S01_Y0);
        txt_S01_Y1 = findViewById(R.id.txt_S01_Y1);
        txt_S01_Y2 = findViewById(R.id.txt_S01_Y2);
        txt_S022_Value = findViewById(R.id.txt_S022_Value);
        txt_S023_Value = findViewById(R.id.txt_S023_Value);
        txt_S024_Value = findViewById(R.id.txt_S024_Value);
        txt_S025_Value = findViewById(R.id.txt_S025_Value);

        // DeconSettingActivity FindViewById
        txt_S02_XX = findViewById(R.id.txt_S02_XX);//this is not in use. In view we have it as Gone..
        txt_S02_YY = findViewById(R.id.txt_S02_YY);
        txt_S21_XX_YY = findViewById(R.id.txt_S21_XX_YY);

        // PasswordSettingActivity FindViewById
        layout_SettingPassword = findViewById(R.id.layout_SettingPassword);
        layout_ReportPassword = findViewById(R.id.layout_ReportPassword);
        layout_DiagnosticsPassword = findViewById(R.id.layout_DiagnosticsPassword);
        layout_DiagnosticsDetailsPassword = findViewById(R.id.layout_DiagnosticsDetailsPassword);
        layout_BluetoothDisconnectPassword = findViewById(R.id.layout_BluetoothDisconnectPassword);
        txt_SettingPassword = findViewById(R.id.txt_SettingPassword);
        txt_ReportPassword = findViewById(R.id.txt_ReportPassword);
        txt_ChangePassword_Setting = findViewById(R.id.txt_ChangePassword_Setting);
        txt_ChangePassword_Report = findViewById(R.id.txt_ChangePassword_Report);
        txt_DiagnosticsPassword = findViewById(R.id.txt_DiagnosticsPassword);
        txt_DiagnosticsPassword_Setting = findViewById(R.id.txt_DiagnosticsPassword_Setting);
        txt_DiagnosticsDetailsPassword = findViewById(R.id.txt_DiagnosticsDetailsPassword);
        txt_DiagnosticsDetailsPassword_Setting = findViewById(R.id.txt_DiagnosticsDetailsPassword_Setting);
        txt_BluetoothDisconnectPassword = findViewById(R.id.txt_BluetoothDisconnectPassword);
        txt_BluetoothDisconnectPassword_Setting = findViewById(R.id.txt_BluetoothDisconnectPassword_Setting);

        // NightModeSettingActivity FindViewById
        txt_currentTime_nightModeSetting = findViewById(R.id.txt_currentTime_nightModeSetting);
        txt_OnTime_nightModeSetting = findViewById(R.id.txt_OnTime_nightModeSetting);
        txt_OffTime_nightModeSetting = findViewById(R.id.txt_OffTime_nightModeSetting);

        // ReportLayout FindViewById
        main_report_menu = findViewById(R.id.main_report_menu);
        main_home_menu_report = findViewById(R.id.main_home_menu_report);
        main_racksetup_screen = findViewById(R.id.main_racksetup_screen);
        main_communication_menu_report_timer = findViewById(R.id.main_communication_menu_report_timer);
        main_rack_detail_report = findViewById(R.id.main_rack_detail_report);

    }

    // Check whether this app has android write settings permission.
    @SuppressLint("NewApi")
    private boolean hasWriteSettingsPermission(Context context) {
        boolean ret = true;
        // Get the result from below code.
        ret = Settings.System.canWrite(context);
        return ret;
    }

    // Start can modify system settings panel to let user change the write settings permission.
    private void changeWriteSettingsPermission(Context context) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        context.startActivity(intent);
    }

    public static String getSerialNumber() {
        String serialNumber;

        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);

            serialNumber = (String) get.invoke(c, "gsm.sn1");

            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "ril.serialnumber");

            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "ro.serialno");

            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "sys.serialnumber");

            if (serialNumber.equals(""))
                serialNumber = Build.SERIAL;


            // If none of the methods above worked
            if (serialNumber.equals(Build.UNKNOWN))
                serialNumber = "";
            Log.e("TAG","Serial Number from HomeActivity getSerialNumber method: " + serialNumber);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG","HomeActivity getSerialNumber method Error: " + e.getMessage().toString());
            serialNumber = "";
        }

        return serialNumber.toUpperCase();
    }

    public static String getWiFiMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                //Log.e("TAG","Name : " + nif.getName());
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    //res1.append(Integer.toHexString(b & 0xFF) + ":");
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }


                return res1.toString();
            }
        } catch (Exception ex) {
            //handle exception
        }
        return "";
    }

    public static String getLANMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                //Log.e("TAG","Name : " + nif.getName());
                if (!nif.getName().equalsIgnoreCase("eth0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    //res1.append(Integer.toHexString(b & 0xFF) + ":");
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }


                return res1.toString();
            }
        } catch (Exception ex) {
            //handle exception
        }
        return "";
    }

    private String getBluetoothMacAddress() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String bluetoothMacAddress = "";
        try {
            Field mServiceField = bluetoothAdapter.getClass().getDeclaredField("mService");
            mServiceField.setAccessible(true);

            Object btManagerService = mServiceField.get(bluetoothAdapter);

            if (btManagerService != null) {
                bluetoothMacAddress = (String) btManagerService.getClass().getMethod("getAddress").invoke(btManagerService);
            }
        } catch (Exception ex) {

        }
        return bluetoothMacAddress;
    }

    // FilterMenu ClickEvent
    public void FilterMenuClickEvent() {

        layout_filter_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEcoflowIcon();
                Utility.changeScreenBrightness(getApplicationContext(),100);
                ResetCounter(1);
                main_layout.setBackgroundColor(ContextCompat.getColor(act, R.color.blwrdetl_blue));
                //layout_filter_menu.setBackgroundColor(0x00000000);
                layout_alert_menu.setBackgroundColor(0x00000000);
                final int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    layout_menubar.setBackgroundDrawable(ContextCompat.getDrawable(act, R.drawable.dark_blue_blwrdetl_screen_box));
                } else {
                    layout_menubar.setBackground(ContextCompat.getDrawable(act, R.drawable.dark_blue_blwrdetl_screen_box));
                }


                if (isPreFilterResetClicked) {
                    img_home_menu_one.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                    layout_filter_menu.setBackgroundColor(ContextCompat.getColor(act, R.color.home_yellow));
                } else {
                    img_home_menu_one.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                    layout_filter_menu.setBackgroundColor(0x00000000);
                }
//                img_home_menu_one.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                img_home_menu_two.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                img_home_menu_three.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                img_home_menu_four.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                img_home_menu_five.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

                view_home_menu_one.setBackgroundColor(ContextCompat.getColor(act, R.color.white));
                view_home_menu_two.setBackgroundColor(ContextCompat.getColor(act, R.color.white));
                view_home_menu_three.setBackgroundColor(ContextCompat.getColor(act, R.color.white));
                view_home_menu_four.setBackgroundColor(ContextCompat.getColor(act, R.color.white));

                img_logo_one.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                img_logo_two.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                img_screen_type_logo.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                img_wicom_cloud_logo.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                txt_Critical_Alarm.setTextColor(ContextCompat.getColor(act, R.color.white));
//                if (isPreFilterResetClicked) {
//                    img_home_menu_one.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
//                } else {
//                    img_home_menu_one.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
//                }
////                img_home_menu_one.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
//                img_home_menu_two.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
//                img_home_menu_three.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
//                img_home_menu_four.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
//                img_home_menu_five.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

                txt_timer.setTextColor(getResources().getColor(R.color.white));
                txt_version.setTextColor(getResources().getColor(R.color.white));

                layout_filter_screen.setVisibility(View.VISIBLE);
                layout_blwrdetl_screen.setVisibility(View.GONE);
                layout_alarm_screen.setVisibility(View.GONE);
                layout_home_screen.setVisibility(View.GONE);

                img_screen_type_logo.setVisibility(View.VISIBLE);
                txt_Critical_Alarm.setVisibility(View.GONE);
                txt_Warning_Alarm.setVisibility(View.GONE);
                setAnalysisData();
            }
        });
    }

    // BlwrdetlMenu ClickEvent
    public void BlwrdetlMenuClickEvent() {

        layout_blwrdetl_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEcoflowIcon();
                Utility.changeScreenBrightness(getApplicationContext(),200);
                ResetCounter(1);
                main_layout.setBackgroundColor(ContextCompat.getColor(act, R.color.blwrdetl_blue));
                //layout_filter_menu.setBackgroundColor(0x00000000);
                layout_alert_menu.setBackgroundColor(0x00000000);
                final int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    layout_menubar.setBackgroundDrawable(ContextCompat.getDrawable(act, R.drawable.dark_blue_blwrdetl_screen_box));
                } else {
                    layout_menubar.setBackground(ContextCompat.getDrawable(act, R.drawable.dark_blue_blwrdetl_screen_box));
                }

                if (isPreFilterResetClicked) {
                    img_home_menu_one.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                    layout_filter_menu.setBackgroundColor(ContextCompat.getColor(act, R.color.home_yellow));

                } else {
                    img_home_menu_one.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                    layout_filter_menu.setBackgroundColor(0x00000000);
                }
//                img_home_menu_one.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                img_home_menu_two.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                img_home_menu_three.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                img_home_menu_four.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                img_home_menu_five.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

                view_home_menu_one.setBackgroundColor(ContextCompat.getColor(act, R.color.white));
                view_home_menu_two.setBackgroundColor(ContextCompat.getColor(act, R.color.white));
                view_home_menu_three.setBackgroundColor(ContextCompat.getColor(act, R.color.white));
                view_home_menu_four.setBackgroundColor(ContextCompat.getColor(act, R.color.white));

                img_logo_one.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                img_logo_two.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                img_screen_type_logo.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                img_wicom_cloud_logo.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                txt_Critical_Alarm.setTextColor(ContextCompat.getColor(act, R.color.white));

//                if (isPreFilterResetClicked) {
//                    img_home_menu_one.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
//                } else {
//                    img_home_menu_one.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
//                }
////                img_home_menu_one.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
//                img_home_menu_two.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
//                img_home_menu_three.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
//                img_home_menu_four.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
//                img_home_menu_five.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

                txt_timer.setTextColor(getResources().getColor(R.color.white));
                txt_version.setTextColor(getResources().getColor(R.color.white));

                layout_filter_screen.setVisibility(View.GONE);
                layout_blwrdetl_screen.setVisibility(View.VISIBLE);
                layout_alarm_screen.setVisibility(View.GONE);
                layout_home_screen.setVisibility(View.GONE);

                img_screen_type_logo.setVisibility(View.VISIBLE);
                txt_Critical_Alarm.setVisibility(View.GONE);
                txt_Warning_Alarm.setVisibility(View.GONE);

                setAnalysisData();
            }
        });
    }

    // AlertMenu ClickEvent
    public void AlertMenuClickEvent() {

        layout_alert_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEcoflowIcon();
                SendS26ToTurnOffOnSound();
                Utility.changeScreenBrightness(getApplicationContext(),50);
                ResetCounter(1);
                main_layout.setBackgroundColor(ContextCompat.getColor(act, R.color.blwrdetl_blue));
                //layout_filter_menu.setBackgroundColor(0x00000000);
                layout_alert_menu.setBackgroundColor(0x00000000);
                final int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    layout_menubar.setBackgroundDrawable(ContextCompat.getDrawable(act, R.drawable.dark_blue_blwrdetl_screen_box));
                } else {
                    layout_menubar.setBackground(ContextCompat.getDrawable(act, R.drawable.dark_blue_blwrdetl_screen_box));
                }

                if (isPreFilterResetClicked) {
                    img_home_menu_one.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                    layout_filter_menu.setBackgroundColor(ContextCompat.getColor(act, R.color.home_yellow));

                } else {
                    img_home_menu_one.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                    layout_filter_menu.setBackgroundColor(0x00000000);
                }
//                img_home_menu_one.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                img_home_menu_two.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                img_home_menu_three.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                img_home_menu_four.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                img_home_menu_five.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

                view_home_menu_one.setBackgroundColor(ContextCompat.getColor(act, R.color.white));
                view_home_menu_two.setBackgroundColor(ContextCompat.getColor(act, R.color.white));
                view_home_menu_three.setBackgroundColor(ContextCompat.getColor(act, R.color.white));
                view_home_menu_four.setBackgroundColor(ContextCompat.getColor(act, R.color.white));

                img_logo_one.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                img_logo_two.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                img_screen_type_logo.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                img_wicom_cloud_logo.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                txt_Critical_Alarm.setTextColor(ContextCompat.getColor(act, R.color.white));
//                if (isPreFilterResetClicked) {
//                    img_home_menu_one.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
//                } else {
//                    img_home_menu_one.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
//                }
////                img_home_menu_one.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
//                img_home_menu_two.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
//                img_home_menu_three.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
//                img_home_menu_four.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
//                img_home_menu_five.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

                txt_timer.setTextColor(getResources().getColor(R.color.white));
                txt_version.setTextColor(getResources().getColor(R.color.white));

                layout_filter_screen.setVisibility(View.GONE);
                layout_blwrdetl_screen.setVisibility(View.GONE);
                layout_alarm_screen.setVisibility(View.VISIBLE);
                layout_home_screen.setVisibility(View.GONE);

                img_screen_type_logo.setVisibility(View.VISIBLE);
                txt_Critical_Alarm.setVisibility(View.GONE);
                txt_Warning_Alarm.setVisibility(View.GONE);
                setAnalysisData();
            }
        });
    }

    // HomeMenu ClickEvent
    public void HomeMenuClickEvent() {

        layout_home_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeEcoflowIcon();
//                String num = getSerialNumber();
//                Log.e("TAG","Serial : " + num);
//
//                num = getWiFiMacAddr();
//                Log.e("TAG","WiFi MAC : " + num);
//
//                num = getLANMacAddr();
//                Log.e("TAG","Lan MAC : " + num);
//
//                num = getBluetoothMacAddress();
//                Log.e("TAG","Bluetooth MAC : " + num);
                Utility.changeScreenBrightness(getApplicationContext(),255);
                if (nightModeStatus) {
                    main_layout.setBackgroundColor(ContextCompat.getColor(act, R.color.nightMode_red));
                    view_home_screen_one.setBackgroundColor(ContextCompat.getColor(act, R.color.black));
                    view_home_screen_two.setBackgroundColor(ContextCompat.getColor(act, R.color.black));
                    view_home_screen_three.setBackgroundColor(ContextCompat.getColor(act, R.color.black));
                    view_home_menu_one.setBackgroundColor(ContextCompat.getColor(act, R.color.black));
                    view_home_menu_two.setBackgroundColor(ContextCompat.getColor(act, R.color.black));
                    view_home_menu_three.setBackgroundColor(ContextCompat.getColor(act, R.color.black));
                    view_home_menu_four.setBackgroundColor(ContextCompat.getColor(act, R.color.black));

                    img_logo_one.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                    img_logo_two.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                    img_screen_type_logo.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                    img_wicom_cloud_logo.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                    txt_Critical_Alarm.setTextColor(ContextCompat.getColor(act, R.color.black));
//                    img_home_screen_plus.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
 //                   img_home_screen_minus.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
 //                   img_home_screen_up.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
 //                   img_home_screen_down.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                    img_home_screen_one.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                    img_home_screen_two.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                    img_home_screen_three.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
//                    img_home_screen_four.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);



                    img_home_menu_one.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                    img_home_menu_two.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                    img_home_menu_three.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                    img_home_menu_four.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                    img_home_menu_five.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);

//                    txt_S01_Z3_Plus.setBackgroundResource(R.drawable.red_home_screen_box);
 //                   txt_S01_Z3_Minus.setBackgroundResource(R.drawable.red_home_screen_box);
 //                   txt_S08_Up.setBackgroundResource(R.drawable.red_home_screen_box);
 //                   txt_S08_Down.setBackgroundResource(R.drawable.red_home_screen_box);
//                    txt_F01_XX.setBackgroundResource(R.drawable.black_home_screen_box);
                    txt_F01_YY.setBackgroundResource(R.drawable.black_home_screen_box);
//                    txt_F02_XX.setBackgroundResource(R.drawable.black_home_screen_box);
                    txt_F02_YY.setBackgroundResource(R.drawable.black_home_screen_box);
//                    txt_S01_Z3.setBackgroundResource(R.drawable.black_home_screen_box);
                    txt_F08_XXYY.setBackgroundResource(R.drawable.black_home_screen_box);

//                    txt_F01_XX.setTextColor(getResources().getColor(R.color.nightMode_red));
                    txt_F01_YY.setTextColor(getResources().getColor(R.color.nightMode_red));
//                    txt_F02_XX.setTextColor(getResources().getColor(R.color.nightMode_red));
                    txt_F02_YY.setTextColor(getResources().getColor(R.color.nightMode_red));
//                    txt_S01_Z3.setTextColor(getResources().getColor(R.color.nightMode_red));
                    txt_F08_XXYY.setTextColor(getResources().getColor(R.color.nightMode_red));
                    txt_timer.setTextColor(getResources().getColor(R.color.black));
                    txt_version.setTextColor(getResources().getColor(R.color.black));

                    final int sdk = android.os.Build.VERSION.SDK_INT;
                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        layout_menubar.setBackgroundDrawable(ContextCompat.getDrawable(act, R.drawable.red_home_screen_box));
                        layout_home_screen.setBackgroundDrawable(ContextCompat.getDrawable(act, R.drawable.red_home_screen_box));
                    } else {
                        layout_menubar.setBackground(ContextCompat.getDrawable(act, R.drawable.red_home_screen_box));
                        layout_home_screen.setBackground(ContextCompat.getDrawable(act, R.drawable.red_home_screen_box));
                    }

                    layout_filter_screen.setVisibility(View.GONE);
                    layout_blwrdetl_screen.setVisibility(View.GONE);
                    layout_alarm_screen.setVisibility(View.GONE);
                    layout_home_screen.setVisibility(View.VISIBLE);

//                    setAnalysisData();
                } else {
                    if (isRedYellowColorHomeScreen) {
//                        setAnalysisData();
                        getSupplyOrExhaustPassOrFail();
                    } else {
//                        main_layout.setBackgroundColor(ContextCompat.getColor(act, R.color.home_green));
                        if(Utility.BLOWER_TYPE.equals(Utility.BCU2))
                            main_layout.setBackgroundColor(ContextCompat.getColor(act, R.color.bg_bcu2));
                        else
                            main_layout.setBackgroundColor(ContextCompat.getColor(act, R.color.bg_spp));
                        view_home_screen_one.setBackgroundColor(ContextCompat.getColor(act, R.color.white));
                        view_home_screen_two.setBackgroundColor(ContextCompat.getColor(act, R.color.white));
                        view_home_screen_three.setBackgroundColor(ContextCompat.getColor(act, R.color.white));
                        view_home_menu_one.setBackgroundColor(ContextCompat.getColor(act, R.color.white));
                        view_home_menu_two.setBackgroundColor(ContextCompat.getColor(act, R.color.white));
                        view_home_menu_three.setBackgroundColor(ContextCompat.getColor(act, R.color.white));
                        view_home_menu_four.setBackgroundColor(ContextCompat.getColor(act, R.color.white));

                        img_logo_one.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                        img_logo_two.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                        img_screen_type_logo.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                        img_wicom_cloud_logo.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                        txt_Critical_Alarm.setTextColor(ContextCompat.getColor(act, R.color.white));
//                        img_home_screen_plus.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
//                        img_home_screen_minus.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
//                        img_home_screen_up.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
//                        img_home_screen_down.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                        img_home_screen_one.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                        img_home_screen_two.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                        img_home_screen_three.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
//                        img_home_screen_four.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

                        img_home_menu_one.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                        img_home_menu_two.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                        img_home_menu_three.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                        img_home_menu_four.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                        img_home_menu_five.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

//                        txt_F01_XX.setTextColor(getResources().getColor(R.color.white));
                        txt_F01_YY.setTextColor(getResources().getColor(R.color.white));
//                        txt_F02_XX.setTextColor(getResources().getColor(R.color.white));
                        txt_F02_YY.setTextColor(getResources().getColor(R.color.white));
                        //txt_S01_Z3.setTextColor(getResources().getColor(R.color.white));
//                        txt_F08_XXYY.setTextColor(getResources().getColor(R.color.white));

//                        txt_S01_Z3_Plus.setBackgroundResource(R.drawable.white_home_screen_textbox);
//                        txt_S01_Z3_Minus.setBackgroundResource(R.drawable.white_home_screen_textbox);
//                        txt_S08_Up.setBackgroundResource(R.drawable.white_home_screen_textbox);
//                        txt_S08_Down.setBackgroundResource(R.drawable.white_home_screen_textbox);
//                        txt_F01_XX.setBackgroundResource(R.drawable.black_home_screen_textbox);
                        txt_F01_YY.setBackgroundResource(R.drawable.black_home_screen_textbox);
//                        txt_F02_XX.setBackgroundResource(R.drawable.black_home_screen_textbox);
                        txt_F02_YY.setBackgroundResource(R.drawable.black_home_screen_textbox);
                        //txt_S01_Z3.setBackgroundResource(R.drawable.black_home_screen_textbox);
//                        txt_F08_XXYY.setBackgroundResource(R.drawable.black_home_screen_textbox);
                        txt_timer.setTextColor(getResources().getColor(R.color.white));
                        txt_version.setTextColor(getResources().getColor(R.color.white));

                        final int sdk = android.os.Build.VERSION.SDK_INT;
                        /*if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            layout_menubar.setBackgroundDrawable(ContextCompat.getDrawable(act, R.drawable.dark_green_home_screen_box));
                            layout_home_screen.setBackgroundDrawable(ContextCompat.getDrawable(act, R.drawable.dark_green_home_screen_box));
                        } else {
                            layout_menubar.setBackground(ContextCompat.getDrawable(act, R.drawable.dark_green_home_screen_box));
                            layout_home_screen.setBackground(ContextCompat.getDrawable(act, R.drawable.dark_green_home_screen_box));
                        }*/

                        setMenubarBackground();
                        setHomeScreenBackground();;

                        layout_filter_screen.setVisibility(View.GONE);
                        layout_blwrdetl_screen.setVisibility(View.GONE);
                        layout_alarm_screen.setVisibility(View.GONE);
                        layout_home_screen.setVisibility(View.VISIBLE);

//                        setAnalysisData();
                    }

                }

                setAnalysisData();
            }
        });
    }

    // SettingMenu ClickEvent
    public void SettingMenuClickEvent() {

        layout_setting_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.changeScreenBrightness(getApplicationContext(),1);
                ResetCounter(1);
                //Utility.ShowSettingPasswordDialog(act, "SettingsActivity");
                ShowSimpleSettingPasswordDialog(act,"SettingActivity");

            }
        });
    }

    // FilterActivity ClickEvent on filter view click event for resetting prefilter
    public void FilterClickEvent() {
        img_Pre_Filter_Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowPreFilterResetDialog();
            }
        });
    }

    // HomeActivity ClickEvent plus minus up down arrow click events
    @SuppressLint("ClickableViewAccessibility")
    public void HomeClickEvent() {
      /*  txt_S01_Z3_Plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isExhaustAvailable()) {
                    if (!txt_S01_Z3.getText().equals("+")) {
                        ResetCounter(1);
                        //Utility.ShowSettingPasswordDialog(act, "Minus_Command");
                        ShowSimpleSettingPasswordDialog(act,"Minus_Command");
                    }
                } else {
                    Toast.makeText(act, "Not Available.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        txt_S01_Z3_Minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isExhaustAvailable()) {
                    if (!txt_S01_Z3.getText().equals("-")) {
                        ResetCounter(1);
                        //Utility.ShowSettingPasswordDialog(act, "Plus_Command");
                        ShowSimpleSettingPasswordDialog(act,"Plus_Command");
                    }
                } else {
                    Toast.makeText(act, "Not Available.", Toast.LENGTH_SHORT).show();
                }
            }
        });*/

    /*    txt_S08_Up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetCounter(1);
                //Utility.ShowSettingPasswordDialog(act, "Up_Down");
                ShowSimpleSettingPasswordDialog(act,"Up_Down");
            }
        });

        txt_S08_Down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetCounter(1);
                //Utility.ShowSettingPasswordDialog(act, "Up_Down");
                ShowSimpleSettingPasswordDialog(act,"Up_Down");
            }
        });*/

        img_logo_one.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mDetector.onTouchEvent(event);
            }
        });

    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            AlertDialogBoxForResetPassword("Reset Master Password");
        }
    }

    // CommunicationSettingAlertDailogBox AlertDailogBox
    public void AlertDialogBoxForResetPassword(final String typr) {

        alertview_reset_password = new Dialog(act);
        alertview_reset_password.setCancelable(false);
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);
        alertview_reset_password.getWindow().setLayout(width, height);

        alertview_reset_password.setContentView(R.layout.alertview_edittext_layout_reset_password); // EditText Screen
//        alertview_selection.show();

        txt_Title_alartview_box = alertview_reset_password.findViewById(R.id.txt_Title_alartview_box);
        edit_EnterTxt_alartview_box = alertview_reset_password.findViewById(R.id.edit_EnterTxt_alartview_box);
        btn_Ok_alartview_box = alertview_reset_password.findViewById(R.id.btn_Save_alartview_box_report);
        btn_Cancel_alartview_box = alertview_reset_password.findViewById(R.id.btn_Cancel_alartview_box_report);

        txt_Title_alartview_box.setText(typr);

        btn_Cancel_alartview_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (alertview_reset_password != null && alertview_reset_password.isShowing()) {
                    alertview_reset_password.dismiss();
                }
//                alertview_selection.dismiss();
            }
        });

        edit_EnterTxt_alartview_box.requestFocus();

        String generatedPassword = GetResetCode();

        btn_Ok_alartview_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edit_EnterTxt_alartview_box.getText().toString().length() == 0) {
                    Utility.ShowMessage(act, "Warning!", "Please enter master password", "OK");
                } else {
                    if (!edit_EnterTxt_alartview_box.getText().toString().equals(generatedPassword)) {
                        Utility.ShowMessage(act, "Warning!", "Please enter valid password", "OK");
                    } else {
                        prefManager.setSettingPassword(String.valueOf(CodeReUse.DefaultSettingPassword));
                        prefManager.setReportPassword(String.valueOf(CodeReUse.DefaultReportPassword));
                        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nChangePassword);
                        Utility.ShowMessage(act, "Success!", "Your passwords has been resetted successfully...", "OK");
                        if (alertview_reset_password != null && alertview_reset_password.isShowing()) {
                            alertview_reset_password.dismiss();
                        }
                    }

//                            textView.setText(edit_EnterTxt_alartview_box.getText().toString());
//                            alertview_selection.dismiss();
                }
            }
        });

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // show dialog here
                if (!isFinishing()) {
                    alertview_reset_password.show();
                }
            }
        });

    }

    //Generate Master Password
    public String GetResetCode() {
        Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());
        int day = localCalendar.get(Calendar.DAY_OF_YEAR);
//        int day = DateTime.Now.DayOfYear;
        String code = "";
        switch (day) {
            case 1:
                code = "*+1wnw8e";
                break;
            case 2:
                code = "*+2enw8r";
                break;
            case 3:
                code = "*+3rnw8q";
                break;
            case 4:
                code = "*+4qnw8y";
                break;
            case 5:
                code = "*+5ynw8c";
                break;
            case 6:
                code = "*+6cnw8t";
                break;
            case 7:
                code = "*+7tnw8c";
                break;
            case 8:
                code = "*+8cnw8j";
                break;
            case 9:
                code = "*+9jnw8k";
                break;
            case 10:
                code = "*+aknw8j";
                break;
            case 11:
                code = "*+bjnw8l";
                break;
            case 12:
                code = "*+clnw8r";
                break;
            case 13:
                code = "*+drnw8n";
                break;
            case 14:
                code = "*+ennw8c";
                break;
            case 15:
                code = "*+fcnw8j";
                break;
            case 16:
                code = "*+0jnw8v";
                break;
            case 17:
                code = "*+1vnw8k";
                break;
            case 18:
                code = "*+2knw8y";
                break;
            case 19:
                code = "*+3ynw8t";
                break;
            case 20:
                code = "*+4tnw8y";
                break;
            case 21:
                code = "*+5ynw8e";
                break;
            case 22:
                code = "*+6enw8b";
                break;
            case 23:
                code = "*+7bnw8g";
                break;
            case 24:
                code = "*+8gnw8s";
                break;
            case 25:
                code = "*+9snw8v";
                break;
            case 26:
                code = "*+avnw8b";
                break;
            case 27:
                code = "*+bbnw8i";
                break;
            case 28:
                code = "*+cinw8i";
                break;
            case 29:
                code = "*+dinw8o";
                break;
            case 30:
                code = "*+eonw8b";
                break;
            case 31:
                code = "*+fbnw8v";
                break;
            case 32:
                code = "*+0vnw8y";
                break;
            case 33:
                code = "*+1ynw8d";
                break;
            case 34:
                code = "*+2dnw8e";
                break;
            case 35:
                code = "*+3enw8h";
                break;
            case 36:
                code = "*+4hnw8p";
                break;
            case 37:
                code = "*+5pnw8h";
                break;
            case 38:
                code = "*+6hnw8i";
                break;
            case 39:
                code = "*+7inw8k";
                break;
            case 40:
                code = "*+8knw8i";
                break;
            case 41:
                code = "*+9inw8l";
                break;
            case 42:
                code = "*+alnw8h";
                break;
            case 43:
                code = "*+bhnw8y";
                break;
            case 44:
                code = "*+cynw8c";
                break;
            case 45:
                code = "*+dcnw8p";
                break;
            case 46:
                code = "*+epnw8l";
                break;
            case 47:
                code = "*+flnw8e";
                break;
            case 48:
                code = "*+0enw8d";
                break;
            case 49:
                code = "*+1dnw8e";
                break;
            case 50:
                code = "*+2enw8r";
                break;
            case 51:
                code = "*+3rnw8d";
                break;
            case 52:
                code = "*+4dnw8k";
                break;
            case 53:
                code = "*+5knw8u";
                break;
            case 54:
                code = "*+6unw8j";
                break;
            case 55:
                code = "*+7jnw8v";
                break;
            case 56:
                code = "*+8vnw8j";
                break;
            case 57:
                code = "*+9jnw8g";
                break;
            case 58:
                code = "*+agnw8z";
                break;
            case 59:
                code = "*+bznw8z";
                break;
            case 60:
                code = "*+cznw8y";
                break;
            case 61:
                code = "*+dynw8j";
                break;
            case 62:
                code = "*+ejnw8d";
                break;
            case 63:
                code = "*+fdnw8j";
                break;
            case 64:
                code = "*+0jnw8e";
                break;
            case 65:
                code = "*+1enw8e";
                break;
            case 66:
                code = "*+2enw8k";
                break;
            case 67:
                code = "*+3knw8h";
                break;
            case 68:
                code = "*+4hnw8f";
                break;
            case 69:
                code = "*+5fnw8o";
                break;
            case 70:
                code = "*+6onw8t";
                break;
            case 71:
                code = "*+7tnw8d";
                break;
            case 72:
                code = "*+8dnw8w";
                break;
            case 73:
                code = "*+9wnw8e";
                break;
            case 74:
                code = "*+aenw8e";
                break;
            case 75:
                code = "*+benw8q";
                break;
            case 76:
                code = "*+cqnw8r";
                break;
            case 77:
                code = "*+drnw8t";
                break;
            case 78:
                code = "*+etnw8a";
                break;
            case 79:
                code = "*+fanw8w";
                break;
            case 80:
                code = "*+0wnw8v";
                break;
            case 81:
                code = "*+1vnw8p";
                break;
            case 82:
                code = "*+2pnw8u";
                break;
            case 83:
                code = "*+3unw8p";
                break;
            case 84:
                code = "*+4pnw8d";
                break;
            case 85:
                code = "*+5dnw8o";
                break;
            case 86:
                code = "*+6onw8i";
                break;
            case 87:
                code = "*+7inw8m";
                break;
            case 88:
                code = "*+8mnw8y";
                break;
            case 89:
                code = "*+9ynw8x";
                break;
            case 90:
                code = "*+axnw8d";
                break;
            case 91:
                code = "*+bdnw8w";
                break;
            case 92:
                code = "*+cwnw8q";
                break;
            case 93:
                code = "*+dqnw8h";
                break;
            case 94:
                code = "*+ehnw8t";
                break;
            case 95:
                code = "*+ftnw8y";
                break;
            case 96:
                code = "*+0ynw8u";
                break;
            case 97:
                code = "*+1unw8a";
                break;
            case 98:
                code = "*+2anw8s";
                break;
            case 99:
                code = "*+3snw8i";
                break;
            case 100:
                code = "*+4inw8b";
                break;
            case 101:
                code = "*+5bnw8o";
                break;
            case 102:
                code = "*+6onw8n";
                break;
            case 103:
                code = "*+7nnw8a";
                break;
            case 104:
                code = "*+8anw8h";
                break;
            case 105:
                code = "*+9hnw8b";
                break;
            case 106:
                code = "*+abnw8z";
                break;
            case 107:
                code = "*+bznw8x";
                break;
            case 108:
                code = "*+cxnw8b";
                break;
            case 109:
                code = "*+dbnw8t";
                break;
            case 110:
                code = "*+etnw8c";
                break;
            case 111:
                code = "*+fcnw8g";
                break;
            case 112:
                code = "*+0gnw8h";
                break;
            case 113:
                code = "*+1hnw8q";
                break;
            case 114:
                code = "*+2qnw8t";
                break;
            case 115:
                code = "*+3tnw8m";
                break;
            case 116:
                code = "*+4mnw8t";
                break;
            case 117:
                code = "*+5tnw8i";
                break;
            case 118:
                code = "*+6inw8o";
                break;
            case 119:
                code = "*+7onw8p";
                break;
            case 120:
                code = "*+8pnw8y";
                break;
            case 121:
                code = "*+9ynw8i";
                break;
            case 122:
                code = "*+ainw8u";
                break;
            case 123:
                code = "*+bunw8k";
                break;
            case 124:
                code = "*+cknw8p";
                break;
            case 125:
                code = "*+dpnw8i";
                break;
            case 126:
                code = "*+einw8u";
                break;
            case 127:
                code = "*+funw8o";
                break;
            case 128:
                code = "*+0onw8g";
                break;
            case 129:
                code = "*+1gnw8i";
                break;
            case 130:
                code = "*+2inw8k";
                break;
            case 131:
                code = "*+3knw8f";
                break;
            case 132:
                code = "*+4fnw8p";
                break;
            case 133:
                code = "*+5pnw8z";
                break;
            case 134:
                code = "*+6znw8s";
                break;
            case 135:
                code = "*+7snw8i";
                break;
            case 136:
                code = "*+8inw8g";
                break;
            case 137:
                code = "*+9gnw8l";
                break;
            case 138:
                code = "*+alnw8g";
                break;
            case 139:
                code = "*+bgnw8j";
                break;
            case 140:
                code = "*+cjnw8v";
                break;
            case 141:
                code = "*+dvnw8l";
                break;
            case 142:
                code = "*+elnw8y";
                break;
            case 143:
                code = "*+fynw8b";
                break;
            case 144:
                code = "*+0bnw8i";
                break;
            case 145:
                code = "*+1inw8o";
                break;
            case 146:
                code = "*+2onw8q";
                break;
            case 147:
                code = "*+3qnw8i";
                break;
            case 148:
                code = "*+4inw8b";
                break;
            case 149:
                code = "*+5bnw8q";
                break;
            case 150:
                code = "*+6qnw8x";
                break;
            case 151:
                code = "*+7xnw8z";
                break;
            case 152:
                code = "*+8znw8v";
                break;
            case 153:
                code = "*+9vnw8w";
                break;
            case 154:
                code = "*+awnw8f";
                break;
            case 155:
                code = "*+bfnw8b";
                break;
            case 156:
                code = "*+cbnw8h";
                break;
            case 157:
                code = "*+dhnw8u";
                break;
            case 158:
                code = "*+eunw8d";
                break;
            case 159:
                code = "*+fdnw8y";
                break;
            case 160:
                code = "*+0ynw8l";
                break;
            case 161:
                code = "*+1lnw8z";
                break;
            case 162:
                code = "*+2znw8w";
                break;
            case 163:
                code = "*+3wnw8z";
                break;
            case 164:
                code = "*+4znw8m";
                break;
            case 165:
                code = "*+5mnw8j";
                break;
            case 166:
                code = "*+6jnw8s";
                break;
            case 167:
                code = "*+7snw8d";
                break;
            case 168:
                code = "*+8dnw8s";
                break;
            case 169:
                code = "*+9snw8s";
                break;
            case 170:
                code = "*+asnw8g";
                break;
            case 171:
                code = "*+bgnw8y";
                break;
            case 172:
                code = "*+cynw8m";
                break;
            case 173:
                code = "*+dmnw8f";
                break;
            case 174:
                code = "*+efnw8v";
                break;
            case 175:
                code = "*+fvnw8b";
                break;
            case 176:
                code = "*+0bnw8x";
                break;
            case 177:
                code = "*+1xnw8p";
                break;
            case 178:
                code = "*+2pnw8z";
                break;
            case 179:
                code = "*+3znw8n";
                break;
            case 180:
                code = "*+4nnw8s";
                break;
            case 181:
                code = "*+5snw8h";
                break;
            case 182:
                code = "*+6hnw8a";
                break;
            case 183:
                code = "*+7anw8p";
                break;
            case 184:
                code = "*+8pnw8l";
                break;
            case 185:
                code = "*+9lnw8y";
                break;
            case 186:
                code = "*+aynw8a";
                break;
            case 187:
                code = "*+banw8i";
                break;
            case 188:
                code = "*+cinw8n";
                break;
            case 189:
                code = "*+dnnw8p";
                break;
            case 190:
                code = "*+epnw8o";
                break;
            case 191:
                code = "*+fonw8a";
                break;
            case 192:
                code = "*+0anw8o";
                break;
            case 193:
                code = "*+1onw8p";
                break;
            case 194:
                code = "*+2pnw8w";
                break;
            case 195:
                code = "*+3wnw8v";
                break;
            case 196:
                code = "*+4vnw8k";
                break;
            case 197:
                code = "*+5knw8t";
                break;
            case 198:
                code = "*+6tnw8g";
                break;
            case 199:
                code = "*+7gnw8w";
                break;
            case 200:
                code = "*+8wnw8o";
                break;
            case 201:
                code = "*+9onw8m";
                break;
            case 202:
                code = "*+amnw8n";
                break;
            case 203:
                code = "*+bnnw8r";
                break;
            case 204:
                code = "*+crnw8v";
                break;
            case 205:
                code = "*+dvnw8z";
                break;
            case 206:
                code = "*+eznw8i";
                break;
            case 207:
                code = "*+finw8n";
                break;
            case 208:
                code = "*+0nnw8z";
                break;
            case 209:
                code = "*+1znw8n";
                break;
            case 210:
                code = "*+2nnw8v";
                break;
            case 211:
                code = "*+3vnw8z";
                break;
            case 212:
                code = "*+4znw8z";
                break;
            case 213:
                code = "*+5znw8o";
                break;
            case 214:
                code = "*+6onw8p";
                break;
            case 215:
                code = "*+7pnw8m";
                break;
            case 216:
                code = "*+8mnw8f";
                break;
            case 217:
                code = "*+9fnw8t";
                break;
            case 218:
                code = "*+atnw8g";
                break;
            case 219:
                code = "*+bgnw8y";
                break;
            case 220:
                code = "*+cynw8u";
                break;
            case 221:
                code = "*+dunw8q";
                break;
            case 222:
                code = "*+eqnw8b";
                break;
            case 223:
                code = "*+fbnw8i";
                break;
            case 224:
                code = "*+0inw8w";
                break;
            case 225:
                code = "*+1wnw8r";
                break;
            case 226:
                code = "*+2rnw8q";
                break;
            case 227:
                code = "*+3qnw8j";
                break;
            case 228:
                code = "*+4jnw8v";
                break;
            case 229:
                code = "*+5vnw8c";
                break;
            case 230:
                code = "*+6cnw8o";
                break;
            case 231:
                code = "*+7onw8k";
                break;
            case 232:
                code = "*+8knw8t";
                break;
            case 233:
                code = "*+9tnw8q";
                break;
            case 234:
                code = "*+aqnw8h";
                break;
            case 235:
                code = "*+bhnw8d";
                break;
            case 236:
                code = "*+cdnw8x";
                break;
            case 237:
                code = "*+dxnw8l";
                break;
            case 238:
                code = "*+elnw8g";
                break;
            case 239:
                code = "*+fgnw8b";
                break;
            case 240:
                code = "*+0bnw8a";
                break;
            case 241:
                code = "*+1anw8w";
                break;
            case 242:
                code = "*+2wnw8n";
                break;
            case 243:
                code = "*+3nnw8m";
                break;
            case 244:
                code = "*+4mnw8w";
                break;
            case 245:
                code = "*+5wnw8c";
                break;
            case 246:
                code = "*+6cnw8d";
                break;
            case 247:
                code = "*+7dnw8l";
                break;
            case 248:
                code = "*+8lnw8l";
                break;
            case 249:
                code = "*+9lnw8o";
                break;
            case 250:
                code = "*+aonw8m";
                break;
            case 251:
                code = "*+bmnw8b";
                break;
            case 252:
                code = "*+cbnw8d";
                break;
            case 253:
                code = "*+ddnw8u";
                break;
            case 254:
                code = "*+eunw8h";
                break;
            case 255:
                code = "*+fhnw8z";
                break;
            case 256:
                code = "*+0znw8d";
                break;
            case 257:
                code = "*+1dnw8l";
                break;
            case 258:
                code = "*+2lnw8p";
                break;
            case 259:
                code = "*+3pnw8b";
                break;
            case 260:
                code = "*+4bnw8b";
                break;
            case 261:
                code = "*+5bnw8e";
                break;
            case 262:
                code = "*+6enw8g";
                break;
            case 263:
                code = "*+7gnw8z";
                break;
            case 264:
                code = "*+8znw8r";
                break;
            case 265:
                code = "*+9rnw8m";
                break;
            case 266:
                code = "*+amnw8v";
                break;
            case 267:
                code = "*+bvnw8t";
                break;
            case 268:
                code = "*+ctnw8h";
                break;
            case 269:
                code = "*+dhnw8b";
                break;
            case 270:
                code = "*+ebnw8q";
                break;
            case 271:
                code = "*+fqnw8q";
                break;
            case 272:
                code = "*+0qnw8i";
                break;
            case 273:
                code = "*+1inw8s";
                break;
            case 274:
                code = "*+2snw8d";
                break;
            case 275:
                code = "*+3dnw8t";
                break;
            case 276:
                code = "*+4tnw8g";
                break;
            case 277:
                code = "*+5gnw8q";
                break;
            case 278:
                code = "*+6qnw8u";
                break;
            case 279:
                code = "*+7unw8z";
                break;
            case 280:
                code = "*+8znw8f";
                break;
            case 281:
                code = "*+9fnw8w";
                break;
            case 282:
                code = "*+awnw8x";
                break;
            case 283:
                code = "*+bxnw8w";
                break;
            case 284:
                code = "*+cwnw8y";
                break;
            case 285:
                code = "*+dynw8m";
                break;
            case 286:
                code = "*+emnw8x";
                break;
            case 287:
                code = "*+fxnw8q";
                break;
            case 288:
                code = "*+0qnw8l";
                break;
            case 289:
                code = "*+1lnw8q";
                break;
            case 290:
                code = "*+2qnw8i";
                break;
            case 291:
                code = "*+3inw8s";
                break;
            case 292:
                code = "*+4snw8u";
                break;
            case 293:
                code = "*+5unw8o";
                break;
            case 294:
                code = "*+6onw8g";
                break;
            case 295:
                code = "*+7gnw8g";
                break;
            case 296:
                code = "*+8gnw8a";
                break;
            case 297:
                code = "*+9anw8y";
                break;
            case 298:
                code = "*+aynw8x";
                break;
            case 299:
                code = "*+bxnw8s";
                break;
            case 300:
                code = "*+csnw8e";
                break;
            case 301:
                code = "*+denw8i";
                break;
            case 302:
                code = "*+einw8v";
                break;
            case 303:
                code = "*+fvnw8g";
                break;
            case 304:
                code = "*+0gnw8b";
                break;
            case 305:
                code = "*+1bnw8m";
                break;
            case 306:
                code = "*+2mnw8r";
                break;
            case 307:
                code = "*+3rnw8a";
                break;
            case 308:
                code = "*+4anw8r";
                break;
            case 309:
                code = "*+5rnw8m";
                break;
            case 310:
                code = "*+6mnw8e";
                break;
            case 311:
                code = "*+7enw8z";
                break;
            case 312:
                code = "*+8znw8s";
                break;
            case 313:
                code = "*+9snw8l";
                break;
            case 314:
                code = "*+alnw8l";
                break;
            case 315:
                code = "*+blnw8n";
                break;
            case 316:
                code = "*+cnnw8x";
                break;
            case 317:
                code = "*+dxnw8o";
                break;
            case 318:
                code = "*+eonw8r";
                break;
            case 319:
                code = "*+frnw8z";
                break;
            case 320:
                code = "*+0znw8f";
                break;
            case 321:
                code = "*+1fnw8c";
                break;
            case 322:
                code = "*+2cnw8p";
                break;
            case 323:
                code = "*+3pnw8z";
                break;
            case 324:
                code = "*+4znw8g";
                break;
            case 325:
                code = "*+5gnw8l";
                break;
            case 326:
                code = "*+6lnw8t";
                break;
            case 327:
                code = "*+7tnw8g";
                break;
            case 328:
                code = "*+8gnw8n";
                break;
            case 329:
                code = "*+9nnw8u";
                break;
            case 330:
                code = "*+aunw8w";
                break;
            case 331:
                code = "*+bwnw8z";
                break;
            case 332:
                code = "*+cznw8d";
                break;
            case 333:
                code = "*+ddnw8p";
                break;
            case 334:
                code = "*+epnw8x";
                break;
            case 335:
                code = "*+fxnw8g";
                break;
            case 336:
                code = "*+0gnw8b";
                break;
            case 337:
                code = "*+1bnw8y";
                break;
            case 338:
                code = "*+2ynw8b";
                break;
            case 339:
                code = "*+3bnw8l";
                break;
            case 340:
                code = "*+4lnw8u";
                break;
            case 341:
                code = "*+5unw8n";
                break;
            case 342:
                code = "*+6nnw8l";
                break;
            case 343:
                code = "*+7lnw8m";
                break;
            case 344:
                code = "*+8mnw8z";
                break;
            case 345:
                code = "*+9znw8h";
                break;
            case 346:
                code = "*+ahnw8q";
                break;
            case 347:
                code = "*+bqnw8j";
                break;
            case 348:
                code = "*+cjnw8m";
                break;
            case 349:
                code = "*+dmnw8y";
                break;
            case 350:
                code = "*+eynw8m";
                break;
            case 351:
                code = "*+fmnw8a";
                break;
            case 352:
                code = "*+0anw8v";
                break;
            case 353:
                code = "*+1vnw8r";
                break;
            case 354:
                code = "*+2rnw8t";
                break;
            case 355:
                code = "*+3tnw8v";
                break;
            case 356:
                code = "*+4vnw8k";
                break;
            case 357:
                code = "*+5knw8i";
                break;
            case 358:
                code = "*+6inw8h";
                break;
            case 359:
                code = "*+7hnw8f";
                break;
            case 360:
                code = "*+8fnw8t";
                break;
            case 361:
                code = "*+9tnw8z";
                break;
            case 362:
                code = "*+aznw8i";
                break;
            case 363:
                code = "*+binw8a";
                break;
            case 364:
                code = "*+canw8l";
                break;
            case 365:
                code = "*+dlnw8x";
                break;
        }
        int year = localCalendar.get(Calendar.YEAR);
        String convertToHex = responseHandler.stringToHexForPaaword(String.valueOf(year));
        String yr = convertToHex.toLowerCase();
        String resetcode = code + yr;
        return resetcode;
    }

    CountDownTimer mTimer = new CountDownTimer(2000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            if (!CodeReUse.isBolwerAdmin) {
//                mRelativeProgressBarLayoutSetting.setVisibility(View.GONE);
                if (hasS_F_got_response) {
                    Log.e(TAG, "Called W Command when communication clicked");
                    mRelativeProgressBarLayoutSetting.setVisibility(View.GONE);
//                    CallReadWriteFuncation("W", 4);
                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetWifiDataOnly);
                    ShowWifiCommunicationDialog("Continue With Application", "Wivarium", "Android Blower");
                    return;
                }
            }
        }
    };

    // SettingsActivity ClickEvent
    public void SettingsClickEvent() {
        main_supplyBlower_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetooth_layout.setVisibility(View.GONE);
                main_layout.setVisibility(View.GONE);
                setting_layout.setVisibility(View.GONE);
                report_layout.setVisibility(View.GONE);
                sub_setting_layout.setVisibility(View.VISIBLE);
                mRelativeProgressBarLayoutSetting.setVisibility(View.GONE);
                supplyBlowerSetting_layout.setVisibility(View.VISIBLE);
                exhaustBlowerSetting_layout.setVisibility(View.GONE);
                filterSetting_layout.setVisibility(View.GONE);
                communicationSetting_layout.setVisibility(View.GONE);
                unitSetting_layout.setVisibility(View.GONE);
                deconSetting_layout.setVisibility(View.GONE);
                passwordSetting_layout.setVisibility(View.GONE);
                nightModeSetting_layout.setVisibility(View.GONE);
                txt_ApplyChange_SettingScreen.setVisibility(View.GONE);

                // ReadAllData
                setAnalysisData();
            }
        });

        main_exhaustBlower_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetooth_layout.setVisibility(View.GONE);
                main_layout.setVisibility(View.GONE);
                report_layout.setVisibility(View.GONE);
                setting_layout.setVisibility(View.GONE);
                sub_setting_layout.setVisibility(View.VISIBLE);
                mRelativeProgressBarLayoutSetting.setVisibility(View.GONE);
                supplyBlowerSetting_layout.setVisibility(View.GONE);
                exhaustBlowerSetting_layout.setVisibility(View.VISIBLE);
                filterSetting_layout.setVisibility(View.GONE);
                communicationSetting_layout.setVisibility(View.GONE);
                unitSetting_layout.setVisibility(View.GONE);
                deconSetting_layout.setVisibility(View.GONE);
                passwordSetting_layout.setVisibility(View.GONE);
                nightModeSetting_layout.setVisibility(View.GONE);
                txt_ApplyChange_SettingScreen.setVisibility(View.GONE);

                // ReadAllData
                setAnalysisData();
            }
        });

        main_filter_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetooth_layout.setVisibility(View.GONE);
                main_layout.setVisibility(View.GONE);
                report_layout.setVisibility(View.GONE);
                setting_layout.setVisibility(View.GONE);
                sub_setting_layout.setVisibility(View.VISIBLE);
                mRelativeProgressBarLayoutSetting.setVisibility(View.GONE);
                supplyBlowerSetting_layout.setVisibility(View.GONE);
                exhaustBlowerSetting_layout.setVisibility(View.GONE);
                filterSetting_layout.setVisibility(View.VISIBLE);
                communicationSetting_layout.setVisibility(View.GONE);
                unitSetting_layout.setVisibility(View.GONE);
                deconSetting_layout.setVisibility(View.GONE);
                passwordSetting_layout.setVisibility(View.GONE);
                nightModeSetting_layout.setVisibility(View.GONE);
                txt_ApplyChange_SettingScreen.setVisibility(View.GONE);

                // ReadAllData
                setAnalysisData();
            }
        });

        main_communication_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.ShowMessage(act, "Information", "Please use Android Network Setting.","Ok");
//                mRelativeProgressBarLayoutSetting.setVisibility(View.VISIBLE);
//                isCommunicationButtonClicked = true;
//                if (!CodeReUse.isBolwerAdmin) {
////                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSingle_S_F_got_response);
//                    if (hasS_F_got_response) {
//                        Log.e(TAG, "Called W Command when communication clicked");
//                        mRelativeProgressBarLayoutSetting.setVisibility(View.GONE);
////                        CallReadWriteFuncation("W", 4);
//                        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetWifiDataOnly);
//                        ShowWifiCommunicationDialog("Continue With Application", "Wivarium", "Android Blower");
//                    } else {
////                        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSingle_S_F_got_response);
//                        mTimer.start();
//                    }
//
//                } else {
//                    Utility.ShowMessage(act, "Alert", "Feature is not available.", "OK");
//                }
            }
        });


        main_unit_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetooth_layout.setVisibility(View.GONE);
                main_layout.setVisibility(View.GONE);
                report_layout.setVisibility(View.GONE);
                setting_layout.setVisibility(View.GONE);
                sub_setting_layout.setVisibility(View.VISIBLE);
                mRelativeProgressBarLayoutSetting.setVisibility(View.GONE);
                supplyBlowerSetting_layout.setVisibility(View.GONE);
                exhaustBlowerSetting_layout.setVisibility(View.GONE);
                filterSetting_layout.setVisibility(View.GONE);
                communicationSetting_layout.setVisibility(View.GONE);
                unitSetting_layout.setVisibility(View.VISIBLE);
                deconSetting_layout.setVisibility(View.GONE);
                passwordSetting_layout.setVisibility(View.GONE);
                nightModeSetting_layout.setVisibility(View.GONE);
                txt_ApplyChange_SettingScreen.setVisibility(View.GONE);

                // ReadAllData
                setAnalysisData();
            }
        });

        main_decon_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetooth_layout.setVisibility(View.GONE);
                main_layout.setVisibility(View.GONE);
                report_layout.setVisibility(View.GONE);
                setting_layout.setVisibility(View.GONE);
                sub_setting_layout.setVisibility(View.VISIBLE);
                mRelativeProgressBarLayoutSetting.setVisibility(View.GONE);
                supplyBlowerSetting_layout.setVisibility(View.GONE);
                exhaustBlowerSetting_layout.setVisibility(View.GONE);
                filterSetting_layout.setVisibility(View.GONE);
                communicationSetting_layout.setVisibility(View.GONE);
                unitSetting_layout.setVisibility(View.GONE);
                deconSetting_layout.setVisibility(View.VISIBLE);
                passwordSetting_layout.setVisibility(View.GONE);
                nightModeSetting_layout.setVisibility(View.GONE);
                txt_ApplyChange_SettingScreen.setVisibility(View.GONE);

                // ReadAllData
                setAnalysisData();
            }
        });

        main_password_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetooth_layout.setVisibility(View.GONE);
                main_layout.setVisibility(View.GONE);
                report_layout.setVisibility(View.GONE);
                setting_layout.setVisibility(View.GONE);
                sub_setting_layout.setVisibility(View.VISIBLE);
                mRelativeProgressBarLayoutSetting.setVisibility(View.GONE);
                supplyBlowerSetting_layout.setVisibility(View.GONE);
                exhaustBlowerSetting_layout.setVisibility(View.GONE);
                filterSetting_layout.setVisibility(View.GONE);
                communicationSetting_layout.setVisibility(View.GONE);
                unitSetting_layout.setVisibility(View.GONE);
                deconSetting_layout.setVisibility(View.GONE);
                passwordSetting_layout.setVisibility(View.VISIBLE);
                nightModeSetting_layout.setVisibility(View.GONE);
                txt_ApplyChange_SettingScreen.setVisibility(View.GONE);

                if (CodeReUse.isBolwerAdmin) {
                    layout_SettingPassword.setVisibility(View.GONE);
                    layout_ReportPassword.setVisibility(View.GONE);
                    layout_DiagnosticsPassword.setVisibility(View.GONE);
                    layout_DiagnosticsDetailsPassword.setVisibility(View.GONE);
                    layout_BluetoothDisconnectPassword.setVisibility(View.VISIBLE);
                } else {
                    layout_SettingPassword.setVisibility(View.VISIBLE);
//                    layout_ReportPassword.setVisibility(View.VISIBLE);
//                    layout_DiagnosticsPassword.setVisibility(View.VISIBLE);
//                    layout_DiagnosticsDetailsPassword.setVisibility(View.VISIBLE);
                    layout_BluetoothDisconnectPassword.setVisibility(View.GONE);
                    layout_DiagnosticsPassword.setVisibility(View.GONE);
                    layout_DiagnosticsDetailsPassword.setVisibility(View.GONE);
                }

                // ReadAllData
                setAnalysisData();
            }
        });

        main_nightMode_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!CodeReUse.isBolwerAdmin) {
                    bluetooth_layout.setVisibility(View.GONE);
                    main_layout.setVisibility(View.GONE);
                    report_layout.setVisibility(View.GONE);
                    setting_layout.setVisibility(View.GONE);
                    sub_setting_layout.setVisibility(View.VISIBLE);
                    mRelativeProgressBarLayoutSetting.setVisibility(View.GONE);
                    supplyBlowerSetting_layout.setVisibility(View.GONE);
                    exhaustBlowerSetting_layout.setVisibility(View.GONE);
                    filterSetting_layout.setVisibility(View.GONE);
                    communicationSetting_layout.setVisibility(View.GONE);
                    unitSetting_layout.setVisibility(View.GONE);
                    deconSetting_layout.setVisibility(View.GONE);
                    passwordSetting_layout.setVisibility(View.GONE);
                    nightModeSetting_layout.setVisibility(View.VISIBLE);
                    txt_ApplyChange_SettingScreen.setVisibility(View.GONE);

                    // ReadAllData
                    setAnalysisData();
                } else {
                    Utility.ShowMessage(act, "Alert", "Feature is not available.", "OK");
                }
            }
        });

        main_home_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRelativeProgressBarLayoutSetting.setVisibility(View.VISIBLE);

               /* AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nRedirectHome);*/
                if (hasS_F_got_response) {
                    Log.e("if call", "setting hasS_F_got_response");
//                    mRelativeProgressBarLayoutSetting.setVisibility(View.GONE);
//                    bluetooth_layout.setVisibility(View.GONE);
//                    main_layout.setVisibility(View.VISIBLE);
//                    setting_layout.setVisibility(View.GONE);
//                    sub_setting_layout.setVisibility(View.GONE);
//                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nRedirectHome);
//                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
                    reDirectHomeScreenFunction(false);
                } else {
                    Log.e("else call", "setting hasS_F_got_response");
                    final Runnable r = new Runnable() {
                        public void run() {
//                            mRelativeProgressBarLayoutSetting.setVisibility(View.GONE);
//                            bluetooth_layout.setVisibility(View.GONE);
//                            main_layout.setVisibility(View.VISIBLE);
//                            setting_layout.setVisibility(View.GONE);
//                            sub_setting_layout.setVisibility(View.GONE);
//                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nRedirectHome);
//                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
                            reDirectHomeScreenFunction(false);
                        }
                    };
                    mHandler.postDelayed(r, 3000);

                }


            }
        });
    }

    // ReportsActivity ClickEvent
    public void ReportsClickEvent() {

        main_home_menu_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 /* AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nRedirectHome);*/
                if (hasS_F_got_response) {
                    Log.e("if call", "setting hasS_F_got_response");
//                    mRelativeProgressBarLayoutSetting.setVisibility(View.GONE);
//                    bluetooth_layout.setVisibility(View.GONE);
//                    main_layout.setVisibility(View.VISIBLE);
//                    setting_layout.setVisibility(View.GONE);
//                    sub_setting_layout.setVisibility(View.GONE);
//                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nRedirectHome);
//                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
                    reDirectHomeScreenFunction(false);
                } else {
                    Log.e("else call", "setting hasS_F_got_response");
                    final Runnable r = new Runnable() {
                        public void run() {
//                            mRelativeProgressBarLayoutSetting.setVisibility(View.GONE);
//                            bluetooth_layout.setVisibility(View.GONE);
//                            main_layout.setVisibility(View.VISIBLE);
//                            setting_layout.setVisibility(View.GONE);
//                            sub_setting_layout.setVisibility(View.GONE);
//                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nRedirectHome);
//                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
                            reDirectHomeScreenFunction(false);
                        }
                    };
                    mHandler.postDelayed(r, 3000);

                }
            }
        });

        main_report_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                csvFileExportFunction();
                Intent intent = new Intent(act, ReportFilterActivity.class);
                startActivity(intent);
                ResetCounter(0);
            }
        });

        main_racksetup_screen.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View v) {
                Log.e("RackActivityOpen... :---", "RackActivityOpen");
                Intent intent = new Intent(act, RackSetUpNewActivity.class);
//                Intent intent = new Intent(act, TestingActivity.class);
                intent.putExtra("isFromSettingScreen", true);
                startActivity(intent);
                ResetCounter(0);
            }
        });

        main_communication_menu_report_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMultipleSelections("set_array_minutes_for_f_Command");
                ShowMultipleSelectionDialogFor_F_Command_Minute("SET TIMER FOR F COMMAND");
                ResetCounter(0);
            }
        });

        main_rack_detail_report.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View v) {
                Log.e("RackDetailActivityOpen... :---", "RackDetailActivityOpen");
                Intent intent = new Intent(act, RackDetailScreenActivity.class);
                startActivity(intent);
                ResetCounter(0);
            }
        });
    }

    // Report File Export Function
    private void csvFileExportFunction() {
        StringBuilder data = new StringBuilder();
        data.append("Time,Distance");
        for (int i = 0; i < 5; i++) {
            data.append("\n" + String.valueOf(i) + "," + String.valueOf(i * i));
        }
//        File sdCard = Environment.getExternalStorageDirectory();
//        File dir = new File(sdCard.getAbsolutePath() + File.separator + "Allentown Blower");
        Context context = getApplicationContext();
        File fileLocation = new File(getFilesDir(), "data.csv");
        Uri path = FileProvider.getUriForFile(context, "com.allentownblower.fileprovider", fileLocation);

        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        Boolean isSDSupportedDevice = Environment.isExternalStorageRemovable();

        if (isSDSupportedDevice && isSDPresent) {
            // yes SD-card is present
            //writing the data to a CSV file
            try {
                FileOutputStream output = openFileOutput("data.csv", Context.MODE_PRIVATE);
                output.write((data.toString().getBytes()));
                output.close();

                //exporting
                Intent fileIntent = new Intent(Intent.ACTION_SEND);
                fileIntent.setType("*/*");
                fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Calendar Data");
                fileIntent.putExtra(Intent.EXTRA_STREAM, path);
                fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                startActivity(Intent.createChooser(fileIntent, "Send mail"));
            } catch (Exception e) {
                System.out.println(e);
            }
        } else {
            // no SD-card is not Present
            Toast.makeText(context, "Please insert SD card into your device", Toast.LENGTH_LONG).show();
        }

    }

    // SubSettingsActivity ClickEvent
    public void SubSettingsClickEvent() {
        layout_backTOhome_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRelativeProgressBarLayoutSubSetting.setVisibility(View.VISIBLE);

                /*AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nRedirectHome);*/

                if (hasS_F_got_response) {
                    Log.e("if call", "subsetting hasS_F_got_response");
//                    mRelativeProgressBarLayoutSubSetting.setVisibility(View.GONE);
//                    bluetooth_layout.setVisibility(View.GONE);
//                    main_layout.setVisibility(View.VISIBLE);
//                    setting_layout.setVisibility(View.GONE);
//                    sub_setting_layout.setVisibility(View.GONE);
//                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
//                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nRedirectHome);
//                    ResetCounter(0);
                    reDirectHomeScreenFunction(true);
                } else {
                    Log.e("else call", "subsetting hasS_F_got_response");
                    final Runnable r = new Runnable() {
                        public void run() {
//                            mRelativeProgressBarLayoutSubSetting.setVisibility(View.GONE);
//                            bluetooth_layout.setVisibility(View.GONE);
//                            main_layout.setVisibility(View.VISIBLE);
//                            setting_layout.setVisibility(View.GONE);
//                            sub_setting_layout.setVisibility(View.GONE);
//                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
//                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nRedirectHome);
//                            ResetCounter(0);
                            reDirectHomeScreenFunction(true);

                        }
                    };
                    mHandler.postDelayed(r, 3000);
                }

            }
        });

        layout_backTOsetting_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetooth_layout.setVisibility(View.GONE);
                main_layout.setVisibility(View.GONE);
                setting_layout.setVisibility(View.VISIBLE);
                setting_layout.setBackgroundColor(ContextCompat.getColor(act, R.color.black));
                sub_setting_layout.setVisibility(View.GONE);
                report_layout.setVisibility(View.GONE);
                //AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointDataOnly);
                ResetCounter(1);
            }
        });

        txt_ApplyChange_SettingScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Re-Start Counter
                ResetCounter(1);
                // W13 Command Sending.....
                CallReadWriteFuncation("W13", 0);
                // Start Progress DailogBox for 30 Seconds
                ProgressDialogBox();
            }
        });
    }

    // SupplyBlowerSettingActivity ClickEvent
    public void SupplyBlowerSettingClickEvent() {
        txt_S09_SB_XYYY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setpointArrayList.size() != 0)
                    //ShowMultipleSelectionDialog("txt_S09_SB_XYYY", "");//this would be S09 for bcu2 and spp
                    ShowMultipleSelectionDialog("txt_S09_SB_XYYY", "");
                else
                    Utility.ShowMessage(act, "Alert", "Data Not Found.", "OK");
            }
        });

        txt_S10_SB_XYYY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setpointArrayList.size() != 0)
                    //ShowMultipleSelectionDialog("txt_S10_SB_XYYY", ""); //this would be S10 for bcu2 and spp
                    ShowMultipleSelectionDialog("txt_S10_SB_XYYY", "");
                else
                    Utility.ShowMessage(act, "Alert", "Data Not Found.", "OK");
            }
        });

        txt_S06_SB_XXXX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setpointArrayList.size() != 0)
                    //ShowMultipleSelectionDialog("txt_S06_SB_XXXX", ""); //this would be S06 for BCU2 and spp
                    ShowMultipleSelectionDialog("txt_S06_SB_XXXX", "");
                else
                    Utility.ShowMessage(act, "Alert", "Data Not Found.", "OK");
            }
        });

        txt_S01_Y3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setpointArrayList.size() != 0)
                    ShowMultipleSelectionDialog("txt_S01_Y3", ""); //this would be S01 Y3 for BCU2 and spp
                else
                    Utility.ShowMessage(act, "Alert", "Data Not Found.", "OK");
            }
        });
    }

    // ExhaustBlowerSettingActivity ClickEvent
    public void ExhaustBlowerSettingClickEvent() {
        txt_S15_Z0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setpointArrayList.size() != 0)
                    ShowMultipleSelectionDialog("txt_S15_Z0", "");
                else
                    Utility.ShowMessage(act, "Alert", "Data Not Found.", "OK");
            }
        });

        txt_S13_XXXX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setpointArrayList.size() != 0)
                    ShowMultipleSelectionDialog("txt_S13_XXXX", "");
                else
                    Utility.ShowMessage(act, "Alert", "Data Not Found.", "OK");
            }
        });

        txt_S14_XXXX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setpointArrayList.size() != 0)
                    ShowMultipleSelectionDialog("txt_S14_XXXX", "");
                else
                    Utility.ShowMessage(act, "Alert", "Data Not Found.", "OK");
            }
        });

        txt_S16_XXYY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowEditTextDialogToUpdateValueBox("Temp", txt_S16_XXYY, "S16");
            }
        });

        txt_S17_XXYY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowEditTextDialogToUpdateValueBox("Humidity", txt_S17_XXYY, "S17");
            }
        });

        /*txt_S10_XYYY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setpointArrayList.size() != 0)
                    ShowMultipleSelectionDialog("txt_S10_XYYY", "");
                else
                    Utility.ShowMessage(act, "Alert", "Data Not Found.", "OK");
            }
        });*/

        /*txt_S06_XXXX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setpointArrayList.size() != 0)
                    ShowMultipleSelectionDialog("txt_S06_XXXX", "");
                else
                    Utility.ShowMessage(act, "Alert", "Data Not Found.", "OK");
            }
        });*/
    }

    // FilterSettingActivity ClickEvent
    public void FilterSettingClickEvent() {
        txt_S03_XXXX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setpointArrayList.size() != 0)
                    ShowMultipleSelectionDialog("txt_S03_XXXX", "");
                else
                    Utility.ShowMessage(act, "Alert", "Data Not Found.", "OK");
            }
        });

        txt_S04_XXXX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setpointArrayList.size() != 0)
                    ShowMultipleSelectionDialog("txt_S04_XXXX", "");
                else
                    Utility.ShowMessage(act, "Alert", "Data Not Found.", "OK");
            }
        });

        txt_S05_XXXX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setpointArrayList.size() != 0)
                    ShowMultipleSelectionDialog("txt_S05_XXXX", "");
                else
                    Utility.ShowMessage(act, "Alert", "Data Not Found.", "OK");
            }
        });

        txt_S11_YY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setpointArrayList.size() != 0)
                    ShowMultipleSelectionDialog("txt_S11_YY", "");
                else
                    Utility.ShowMessage(act, "Alert", "Data Not Found.", "OK");
            }
        });

        txt_S11_XX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setpointArrayList.size() != 0)
                    ShowMultipleSelectionDialog("txt_S11_XX", "");
                else
                    Utility.ShowMessage(act, "Alert", "Data Not Found.", "OK");
            }
        });

        txt_S12_XXXX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setpointArrayList.size() != 0)
                    ShowMultipleSelectionDialog("txt_S12_XXXX", "");
                else
                    Utility.ShowMessage(act, "Alert", "Data Not Found.", "OK");
            }
        });
    }

    // CommunicationSettingActivity ClickEvent
    public void CommunicationSettingClickEvent() {
        txt_NETWORK_SSID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommunicationSettingAlertDialogBox("NETWORK SSID", txt_NETWORK_SSID, "W01");
            }
        });

        txt_SECURITY_MODE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowMultipleSelectionDialog("SECURITY MODE", "W10");
            }
        });

        txt_SECURITY_KEY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommunicationSettingAlertDialogBox("SECURITY KEY", txt_SECURITY_KEY, "W02");
            }
        });

        //txt_SECURITY_KEY_VALUE

//        txt_COUNTRY_CODE.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                CommunicationSettingAlertDialogBox("COUNTRY CODE", txt_COUNTRY_CODE, "W09");
//            }
//        });

        txt_COUNTRY_CODE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                CommunicationSettingAlertDialogBox("COUNTRY CODE", txt_COUNTRY_CODE, "W09");
                ShowMultipleSelectionDialog("COUNTRY CODE", "W09");
            }
        });

        txt_DHCP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowMultipleSelectionDialog("DHCP", "W08");
            }
        });

        txt_IP_ADDRESS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommunicationSettingAlertDialogBox("IP ADDRESS", txt_IP_ADDRESS, "W03");
            }
        });

        txt_SUBNET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommunicationSettingAlertDialogBox("SUBNET", txt_SUBNET, "W04");
            }
        });

        txt_DEFAULT_GATEWAY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommunicationSettingAlertDialogBox("DEFAULT GATEWAY", txt_DEFAULT_GATEWAY, "W05");
            }
        });

        txt_DNS_SERVER.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommunicationSettingAlertDialogBox("DNS SERVER", txt_DNS_SERVER, "W06");
            }
        });

        txt_GATEWAY_PING.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommunicationSettingAlertDialogBox("GATEWAY PING", txt_GATEWAY_PING, "W07");
            }
        });

    }

    // UnitSettingActivity ClickEvent
    public void UnitSettingClickEvent() {
        txt_S01_Y0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setpointArrayList.size() != 0)
                    ShowMultipleSelectionDialog("txt_S01_Y0", "");
                else
                    Utility.ShowMessage(act, "Alert", "Data Not Found.", "OK");
            }
        });

        txt_S01_Y1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setpointArrayList.size() != 0)
                    ShowMultipleSelectionDialog("txt_S01_Y1", "");
                else
                    Utility.ShowMessage(act, "Alert", "Data Not Found.", "OK");
            }
        });

        txt_S01_Y2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setpointArrayList.size() != 0)
                    ShowMultipleSelectionDialog("txt_S01_Y2", "");
                else
                    Utility.ShowMessage(act, "Alert", "Data Not Found.", "OK");
            }
        });

        txt_S022_Value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowEditTextDialogToUpdateValueBox("Supply Temp", txt_S022_Value, "S22");
            }
        });

        txt_S023_Value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowEditTextDialogToUpdateValueBox("Supply Humidity", txt_S023_Value, "S23");
            }
        });

        txt_S024_Value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowEditTextDialogToUpdateValueBox("Exhaust Temp", txt_S024_Value, "S24");
            }
        });

        txt_S025_Value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowEditTextDialogToUpdateValueBox("Exhaust Humidity", txt_S025_Value, "S25");
            }
        });
    }

    // DeconSettingActivity ClickEvent
    public void DeconSettingClickEvent() {
        txt_S02_XX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setpointArrayList.size() != 0)
                    ShowMultipleSelectionDialog("txt_S02_XX", "");
                else
                    Utility.ShowMessage(act, "Alert", "Data Not Found.", "OK");
            }
        });

        txt_S02_YY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setpointArrayList.size() != 0)
                    ShowMultipleSelectionDialog("txt_S02_YY", "");
                else
                    Utility.ShowMessage(act, "Alert", "Data Not Found.", "OK");
            }
        });

        txt_S21_XX_YY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setpointArrayList.size() != 0)
                    ShowMultipleSelectionDialog("txt_S21_XX_YY", "");
                else
                    Utility.ShowMessage(act, "Alert", "Data Not Found.", "OK");
            }
        });
    }

    // PasswordSettingActivity ClickEvent
    public void PasswordSettingClickEvent() {
        txt_SettingPassword.setText(prefManager.getSettingPassword());
        txt_ReportPassword.setText(prefManager.getReportPassword());
        txt_DiagnosticsPassword.setText(prefManager.getDiagnosticsPassword());
        txt_DiagnosticsDetailsPassword.setText(prefManager.getDiagnosticsDetailPassword());
        txt_BluetoothDisconnectPassword.setText(prefManager.getBluetoothDisconnectPassword());

        txt_ChangePassword_Setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Utility.ShowSettingPasswordDialog(act, "ChangePasswordSetting");
                ShowSimpleSettingPasswordDialog(act,"ChangePasswordSetting");
            }
        });

        txt_ChangePassword_Report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Utility.ShowSettingPasswordDialog(act, "ChangePasswordReport");
                ShowSimpleSettingPasswordDialog(act,"ChangePasswordReport");
            }
        });

        txt_DiagnosticsPassword_Setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Utility.ShowSettingPasswordDialog(act, "DiagnosticsPasswordSetting");
                ShowSimpleSettingPasswordDialog(act,"DiagnosticsPasswordSetting");
            }
        });

        txt_DiagnosticsDetailsPassword_Setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Utility.ShowSettingPasswordDialog(act, "DiagnosticsPasswordSetting");
                ShowSimpleSettingPasswordDialog(act,"DiagnosticsPasswordSetting");
            }
        });

        txt_BluetoothDisconnectPassword_Setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Utility.ShowSettingPasswordDialog(act, "BluetoothDisconnectPasswordSetting");
                ShowSimpleSettingPasswordDialog(act,"BluetoothDisconnectPasswordSetting");
            }
        });

    }

    // NightModeSettingActivity ClickEvent
    public void NightModeSettingClickEvent() {

        txt_OnTime_nightModeSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Date date = null;
                try {
                    date = CodeReUse.timeFormat.parse(txt_OnTime_nightModeSetting.getText().toString().trim());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                mcurrentTime.setTime(date);
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(act, new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String AM_PM;
                        if (selectedHour < 12) {
                            AM_PM = "AM";
                        } else {
                            AM_PM = "PM";
                            selectedHour = selectedHour - 12;
                        }

                        if (selectedHour == 00) {
                            selectedHour = 12;
                        }

                        txt_OnTime_nightModeSetting.setText(CodeReUse.formatter2Digit.format(selectedHour) + ":" + CodeReUse.formatter2Digit.format(selectedMinute) + " " + AM_PM);
                        prefManager.setOnNightMode(CodeReUse.formatter2Digit.format(selectedHour) + ":" + CodeReUse.formatter2Digit.format(selectedMinute) + ":00" + " " + AM_PM);
                    }
                }, hour, minute, false); // true 24 hour time false 12 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
//                alertview_selection = new Dialog(act);
//
//                int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
//                int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);
//
//                alertview_selection.getWindow().setLayout(width, height);
//
//                alertview_selection.setCancelable(false);
//                alertview_selection.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//
//                alertview_selection.setContentView(R.layout.alertview_seekbar_selection_layout); // Seekbar Selection
//
//                alertview_selection.show();
//
//                txt_dailogTitle = alertview_selection.findViewById(R.id.txt_dailogTitle);
//                txt_MinSeekbar_Progress = alertview_selection.findViewById(R.id.txt_MinSeekbar_Progress);
//                txt_MaxSeekbar_Progress = alertview_selection.findViewById(R.id.txt_MaxSeekbar_Progress);
//                seekbar_Progress_Selection = alertview_selection.findViewById(R.id.seekbar_Progress_Selection);
//                txt_SelectedSeekbar_Progress = alertview_selection.findViewById(R.id.txt_SelectedSeekbar_Progress);
//                txt_Minus_Progress = alertview_selection.findViewById(R.id.txt_Minus_Progress);
//                txt_Plus_Progress = alertview_selection.findViewById(R.id.txt_Plus_Progress);
//
//                txt_dailogTitle.setText("Start Night Mode (hh:mm)");
//                txt_MinSeekbar_Progress.setText("12:00 AM");
//                txt_MaxSeekbar_Progress.setText("11:59 AM");
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    seekbar_Progress_Selection.setMin(responseHandler.strToMilli("00:00"));
//                }
//
//                seekbar_Progress_Selection.setMax(responseHandler.strToMilli("23:59"));
//
////                String Time = txt_OnTime_nightModeSetting.getText().toString().replace(":00 ", " ").trim();
//                String Time = txt_OnTime_nightModeSetting.getText().toString().trim();
//                final String[] OnFullTime = Time.split(" ");
//
//                Integer hrs = 00, mins = 00;
//                String hours, minutes;
//
//                if (OnFullTime[1].equals("AM")) {
//                    String[] OnTime = OnFullTime[0].split(":");
//                    hrs = Integer.parseInt(OnTime[0]);
//                    mins = Integer.parseInt(OnTime[1]);
//                    txt_MinSeekbar_Progress.setText("12:00 AM");
//                    txt_MaxSeekbar_Progress.setText("11:59 AM");
//                } else {
//                    String[] OnTime = OnFullTime[0].split(":");
//                    hrs = Integer.parseInt(OnTime[0]) + 12;
//                    mins = Integer.parseInt(OnTime[1]);
//                    txt_MinSeekbar_Progress.setText("12:00 PM");
//                    txt_MaxSeekbar_Progress.setText("11:59 PM");
//                }
//
//                if (hrs < 10)
//                    hours = "0" + hrs.toString();
//                else
//                    hours = hrs.toString();
//
//                if (mins < 10)
//                    minutes = "0" + mins.toString();
//                else
//                    minutes = mins.toString();
//
//                seekbar_Progress_Selection.setProgress(responseHandler.strToMilli(hours + ":" + minutes));
//
//                String progress = responseHandler.milliToStrHours(responseHandler.strToMilli(hours + ":" + minutes));
//                txt_SelectedSeekbar_Progress.setText(progress);
//
//                seekbar_Progress_Selection.setOnSeekChangeListener(new OnSeekChangeListener() {
//                    @Override
//                    public void onSeeking(SeekParams seekParams) {
//                        seekbar_Progress_Selection.setProgress(seekParams.progress);
//                        String progress = responseHandler.milliToStrHours(seekParams.progress);
//                        txt_SelectedSeekbar_Progress.setText(progress);
//                        if (progress.endsWith("AM")) {
//                            txt_MinSeekbar_Progress.setText("12:00 AM");
//                            txt_MaxSeekbar_Progress.setText("11:59 AM");
//                        } else {
//                            txt_MinSeekbar_Progress.setText("12:00 PM");
//                            txt_MaxSeekbar_Progress.setText("11:59 PM");
//                        }
//                    }
//
//                    @Override
//                    public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
//
//                    }
//
//                    @Override
//                    public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
//
//                    }
//                });
//
//                txt_Minus_Progress.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        ResetCounter(1);
//                        if (seekbar_Progress_Selection.getProgress() > responseHandler.strToMilli("00:00")) {
//                            int setProgress = seekbar_Progress_Selection.getProgress() - 60;
//                            seekbar_Progress_Selection.setProgress(setProgress);
//                            String progress = responseHandler.milliToStrHours(seekbar_Progress_Selection.getProgress());
//                            txt_SelectedSeekbar_Progress.setText(progress);
//                            if (progress.endsWith("AM")) {
//                                txt_MinSeekbar_Progress.setText("12:00 AM");
//                                txt_MaxSeekbar_Progress.setText("11:59 AM");
//                            } else {
//                                txt_MinSeekbar_Progress.setText("12:00 PM");
//                                txt_MaxSeekbar_Progress.setText("11:59 PM");
//                            }
//                        }
//                    }
//                });
//
//                txt_Plus_Progress.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        ResetCounter(1);
//                        if (seekbar_Progress_Selection.getProgress() < responseHandler.strToMilli("23:59")) {
//                            int setProgress = seekbar_Progress_Selection.getProgress() + 60;
//                            seekbar_Progress_Selection.setProgress(setProgress);
//                            String progress = responseHandler.milliToStrHours(seekbar_Progress_Selection.getProgress());
//                            txt_SelectedSeekbar_Progress.setText(progress);
//                            if (progress.endsWith("AM")) {
//                                txt_MinSeekbar_Progress.setText("12:00 AM");
//                                txt_MaxSeekbar_Progress.setText("11:59 AM");
//                            } else {
//                                txt_MinSeekbar_Progress.setText("12:00 PM");
//                                txt_MaxSeekbar_Progress.setText("11:59 PM");
//                            }
//                        }
//                    }
//                });
//
//                btn_Cancel_Selection = alertview_selection.findViewById(R.id.btn_Cancel_Selection);
//                btn_Save_Selection = alertview_selection.findViewById(R.id.btn_Save_Selection);
//
//                btn_Cancel_Selection.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        ResetCounter(1);
//                        alertview_selection.dismiss();
//                    }
//                });
//
//                btn_Save_Selection.setOnClickListener(new View.OnClickListener() {
//                    @SuppressLint("LongLogTag")
//                    @Override
//                    public void onClick(View view) {
//                        ResetCounter(1);
//                        Log.e(TAG,"Selected On time : " + txt_SelectedSeekbar_Progress.getText().toString());
//                        String[] selectedTime = txt_SelectedSeekbar_Progress.getText().toString().split(" ");
//                        txt_OnTime_nightModeSetting.setText(selectedTime[0] + " " + selectedTime[1]);
//                        prefManager.setOnNightMode(selectedTime[0] + ":00 " + selectedTime[1]);
//                        alertview_selection.dismiss();
//                    }
//                });
            }
        });

        txt_OffTime_nightModeSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Date date = null;
                try {
                    date = CodeReUse.timeFormat.parse(txt_OffTime_nightModeSetting.getText().toString().trim());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                mcurrentTime.setTime(date);
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(act, new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String AM_PM;
                        if (selectedHour < 12) {
                            AM_PM = "AM";
                        } else {
                            AM_PM = "PM";
                            selectedHour = selectedHour - 12;
                        }

                        if (selectedHour == 00) {
                            selectedHour = 12;
                        }

                        txt_OffTime_nightModeSetting.setText(CodeReUse.formatter2Digit.format(selectedHour) + ":" + CodeReUse.formatter2Digit.format(selectedMinute) + " " + AM_PM);
                        prefManager.setOffNightMode(CodeReUse.formatter2Digit.format(selectedHour) + ":" + CodeReUse.formatter2Digit.format(selectedMinute) + ":00" + " " + AM_PM);
                    }
                }, hour, minute, false); // true 24 hour time false 12 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

    }

    // Communication Call Funcation
    public void CallReadWriteFuncation(final String command, final int isStart) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Example of a call to a native method
                if (prefManager.getOpenNode()) {
                    portConversion.writeNode(command, isStart);
                }
            }
        }).start();
    }

    // Check Background Service Start or Not
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    // Service Start or Stop
    public void StatusBackgroundService(boolean status, String isDiagnosticsService, String parsevalue) {
        String key = "parseValue";
        if (isDiagnosticsService.equals("DiagnosticsCommandService")) {
            if (status) {
                if (service_diagnostic == null) {
                    service_diagnostic = new Intent(act, DiagnosticsCommandService.class);
                }
                service_diagnostic.putExtra("parseValue", parsevalue);
                startService(service_diagnostic);
            } else {
                if (service_diagnostic != null) {
                    service_diagnostic.putExtra("parseValue", parsevalue);
                    stopService(service_diagnostic);
                    service_diagnostic = null;
                }
            }
        } else {
            if (status) {
                if (service_myservice == null) {
                    service_myservice = new Intent(act, MyService.class);
                }
                startService(service_myservice);
            } else {
                if (service_myservice != null) {
                    stopService(service_myservice);
                    service_myservice = null;
                }
            }
        }
    }

    // Read And Set AllData
    @SuppressLint("SetTextI18n")
    public void setAnalysisData() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isInnerFunction) {

                        isInnerFunction = false;

                        feedbackArrayList.clear();
                        setpointArrayList.clear();
                        diagnosticsArrayList.clear();
                        wifiArrayList.clear();

                        feedbackArrayList = responseHandler.getLastFeedbackData();
                        setpointArrayList = responseHandler.getLastSetPointData();
                        diagnosticsArrayList = responseHandler.getLastDiagnosticsData();
                        wifiArrayList = responseHandler.getLastWiFiData();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (feedbackArrayList.size() != 0) {

                                        if (setpointArrayList.size() != 0) {
                                            if (setpointArrayList.get(0).getS21().equals("0000")) {
                                                txt_isDecon.setVisibility(View.INVISIBLE);
                                            } else {
                                                if (txt_isDecon.getVisibility() == View.VISIBLE) {
                                                    txt_isDecon.setVisibility(View.INVISIBLE);
                                                } else {
                                                    txt_isDecon.setVisibility(View.VISIBLE);
                                                }
                                            }
                                        }

                                        // FilterActivity AnalysisData
                                        try {
                                            if (layout_filter_screen.getVisibility() == View.VISIBLE) {
                                                txt_F11_XX.setText(responseHandler.hexToString(feedbackArrayList.get(0).getF11(), true) + " %");
                                                txt_F11_YY.setText(responseHandler.hexToString(feedbackArrayList.get(0).getF11(), false) + " %");

                                                if (setpointArrayList.size() != 0) {
                                                    double runhrs = Double.valueOf(responseHandler.hexToString(feedbackArrayList.get(0).getF10())) / 10;
                                                    double pohhrs = Double.valueOf(responseHandler.hexToString(setpointArrayList.get(0).getS12()));
                                                    double remainhrs = pohhrs - runhrs;

                                                    if (remainhrs > 0)
                                                        txt_S12_XXYY_F10_XXYY.setText(String.valueOf(CodeReUse.formatterDecimal2Digit.format(remainhrs)));
                                                    else
                                                        txt_S12_XXYY_F10_XXYY.setText("0");

                                                    if (!isExhaustAvailable()) {
                                                        txt_F11_YY.setText("");
                                                    }
                                                }
                                            }
                                        } catch (Exception e) {
//                                            e.printStackTrace();
                                            Log.e(TAG, e.getMessage());
                                        }

                                        // BlwrDetlActivity AnalysisData
                                        try {
                                            if (layout_blwrdetl_screen.getVisibility() == View.VISIBLE) {



                                                txt_F05_XXYY.setText(responseHandler.hexToString(feedbackArrayList.get(0).getF05()));
                                                txt_F06_XXYY.setText(responseHandler.hexToString(feedbackArrayList.get(0).getF06()));

                                                if (setpointArrayList.size() != 0) {
                                                    if (responseHandler.hexToBinary(setpointArrayList.get(0).getS01()).charAt(9) == '1') {
                                                        txt_F07_XX.setText(responseHandler.hexToString(feedbackArrayList.get(0).getF07(), true) + " CMH");
                                                    } else {
                                                        txt_F07_XX.setText(responseHandler.hexToString(feedbackArrayList.get(0).getF07(), true) + " CFM");
                                                    }

                                                    if (responseHandler.hexToBinary(setpointArrayList.get(0).getS01()).charAt(10) == '1') {
                                                        String F04 = responseHandler.hexToBinary(feedbackArrayList.get(0).getF04());

                                                        if (F04.charAt(0) == '0') {
                                                            // +
                                                            float decimal = Float.parseFloat(String.valueOf(responseHandler.binaryToDecimal(Integer.parseInt(F04.substring(2, 16))))) / 10;
                                                            txt_F04_XXYY.setText("+" + CodeReUse.formatterDecimal1Digit.format(decimal) + " Pa");
                                                        } else {
                                                            // -
                                                            float decimal = Float.parseFloat(String.valueOf(responseHandler.binaryToDecimal(Integer.parseInt(F04.substring(2, 16))))) / 10;
                                                            txt_F04_XXYY.setText("-" + CodeReUse.formatterDecimal1Digit.format(decimal) + " Pa");
                                                        }
                                                    } else {
                                                        String F04 = responseHandler.hexToBinary(feedbackArrayList.get(0).getF04());

                                                        if (F04.charAt(0) == '0') {
                                                            // +
                                                            float decimal = Float.parseFloat(String.valueOf(responseHandler.binaryToDecimal(Integer.parseInt(F04.substring(2, 16))))) / 1000;
                                                            txt_F04_XXYY.setText("+" + CodeReUse.formatter3Digit.format(decimal) + " WC");
                                                        } else {
                                                            // -
                                                            float decimal = Float.parseFloat(String.valueOf(responseHandler.binaryToDecimal(Integer.parseInt(F04.substring(2, 16))))) / 1000;
                                                            txt_F04_XXYY.setText("-" + CodeReUse.formatter3Digit.format(decimal) + " WC");
                                                        }
                                                    }

                                                    if (!isExhaustAvailable()) {
                                                        txt_F04_XXYY.setText("");
                                                        txt_F06_XXYY.setText("");
                                                    }
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        // HomeActivity AnalysisData
                                        try {
                                            if (layout_home_screen.getVisibility() == View.VISIBLE) {
//                                                txt_F02_XX.setText(responseHandler.hexToString(feedbackArrayList.get(0).getF02(), true) + " %");
                                                txt_F02_YY.setText(responseHandler.hexToString(feedbackArrayList.get(0).getF02(), false) + " %");

                                                if (setpointArrayList.size() != 0) {

                                                    if (responseHandler.hexToBinary(setpointArrayList.get(0).getS01()).charAt(11) == '1') {
//                                                        txt_F01_XX.setText(responseHandler.hexToString(feedbackArrayList.get(0).getF01(), true) + " C");
                                                        txt_F01_YY.setText(responseHandler.hexToString(feedbackArrayList.get(0).getF01(), false) + " C");
                                                    } else {
//                                                        txt_F01_XX.setText(responseHandler.hexToString(feedbackArrayList.get(0).getF01(), true) + " F");
                                                        txt_F01_YY.setText(responseHandler.hexToString(feedbackArrayList.get(0).getF01(), false) + " F");
                                                    }

                                                    /*if (responseHandler.hexToBinary(setpointArrayList.get(0).getS01()).charAt(12) == '1') {
                                                        txt_S01_Z3.setText("+");
                                                    } else {
                                                        txt_S01_Z3.setText("-");
                                                    }

                                                    if (!isExhaustAvailable()) {
                                                        txt_F01_YY.setText("");
                                                        txt_F02_YY.setText("");
                                                    }*/
                                                }
                                                if (feedbackArrayList.get(0).getF08().equals("FFFF")) {
                                                    txt_F08_XXYY.setText("CAL");
                                                } else {
//                                                    txt_F08_XXYY.setText(responseHandler.hexToString(feedbackArrayList.get(0).getF04()));
                                                    //set wc and pa from blowerdetail
                                                    if (responseHandler.hexToBinary(setpointArrayList.get(0).getS01()).charAt(10) == '1') {
                                                        String F04 = responseHandler.hexToBinary(feedbackArrayList.get(0).getF04());

                                                        if (F04.charAt(0) == '0') {
                                                            // +
                                                            float decimal = Float.parseFloat(String.valueOf(responseHandler.binaryToDecimal(Integer.parseInt(F04.substring(2, 16))))) / 10;
                                                            txt_F08_XXYY.setText("+" + CodeReUse.formatterDecimal1Digit.format(decimal) + " Pa");
                                                        } else {
                                                            // -
                                                            float decimal = Float.parseFloat(String.valueOf(responseHandler.binaryToDecimal(Integer.parseInt(F04.substring(2, 16))))) / 10;
                                                            txt_F08_XXYY.setText("-" + CodeReUse.formatterDecimal1Digit.format(decimal) + " Pa");
                                                        }
                                                    } else {
                                                        String F04 = responseHandler.hexToBinary(feedbackArrayList.get(0).getF04());

                                                        if (F04.charAt(0) == '0') {
                                                            // +
                                                            float decimal = Float.parseFloat(String.valueOf(responseHandler.binaryToDecimal(Integer.parseInt(F04.substring(2, 16))))) / 1000;
                                                            txt_F08_XXYY.setText("+" + CodeReUse.formatter3Digit.format(decimal) + " WC");
                                                        } else {
                                                            // -
                                                            float decimal = Float.parseFloat(String.valueOf(responseHandler.binaryToDecimal(Integer.parseInt(F04.substring(2, 16))))) / 1000;
                                                            txt_F08_XXYY.setText("-" + CodeReUse.formatter3Digit.format(decimal) + " WC");
                                                        }
                                                    }
                                                }
                                                // TODO :- Check z0, x0, z2, x2 , z1, x1 is 1 or Not
                                                getSupplyOrExhaustPassOrFail();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        // AlarmActivity AnalysisData
                                        try {
                                            if (layout_alarm_screen.getVisibility() == View.VISIBLE) {
                                                
                                                if(responseHandler.hexToBinary(feedbackArrayList.get(0).getF12()).charAt(6) == '1') { //exhaust Filter box F12.X1
                                                    txt_F12_X1.setText("X");
                                                } else {
                                                    txt_F12_X1.setText("");
                                                }

                                                if (responseHandler.hexToBinary(feedbackArrayList.get(0).getF12()).charAt(7) == '1' || responseHandler.hexToBinary(feedbackArrayList.get(0).getF12()).charAt(15) == '1'
                                                   || responseHandler.hexToBinary(feedbackArrayList.get(0).getF12()).charAt(1) == '1' || responseHandler.hexToBinary(feedbackArrayList.get(0).getF12()).charAt(9) == '1') {
                                                    //F12 X0 exhaust blower or F12 Z0 supply blower or F12 W2 exhaust pvm or F12 Y2 supply pvm
                                                    txt_F12_Z0X0.setText("X");
                                                } else {
                                                    txt_F12_Z0X0.setText("");
                                                }

                                                if(responseHandler.hexToBinary(feedbackArrayList.get(0).getF12()).charAt(5) == '1') {
                                                    //F12 X2 exhaust hose
                                                    txt_F12_X2.setText("X");
                                                } else {
                                                    txt_F12_X2.setText("");
                                                }

                                                if(responseHandler.hexToBinary(feedbackArrayList.get(0).getF13()).charAt(0) == '1') {
                                                    //Cage box F13 W0
                                                    txt_F13_W3.setText("X");
                                                } else {
                                                    txt_F13_W3.setText("");
                                                }

                                                if(responseHandler.hexToBinary(feedbackArrayList.get(0).getF13()).charAt(5) == '1') {
                                                    //Power alarm box F13 X2
                                                    txt_F13_X2.setText("X");
                                                } else {
                                                    txt_F13_X2.setText("");
                                                }

                                                if (responseHandler.hexToBinary(feedbackArrayList.get(0).getF13()).charAt(3) == '1' || responseHandler.hexToBinary(feedbackArrayList.get(0).getF13()).charAt(4) == '1') {
                                                    //F13 W0 or F13 X3 temp alarm
                                                    txt_F13_W0X3.setText("X");
                                                } else {
                                                    txt_F13_W0X3.setText("");
                                                }

                                                if (responseHandler.hexToBinary(feedbackArrayList.get(0).getF13()).charAt(1) == '1' || responseHandler.hexToBinary(feedbackArrayList.get(0).getF13()).charAt(2) == '1') {
                                                    //F13 W2 or F13 W1 hmd alarm
                                                    txt_F13_W1W2.setText("X");
                                                } else {
                                                    txt_F13_W1W2.setText("");
                                                }

                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Log.e("HomeSetAnalysisError", "" + e.getMessage());
                                        }

                                        // Sub SettingActivity AnalysisData
                                        try {
                                            if (sub_setting_layout.getVisibility() == View.VISIBLE) {

                                                ResetCounter(1);

                                                // SupplyBlowerSettingActivity AnalysisData
                                                try {
                                                    if (supplyBlowerSetting_layout.getVisibility() == View.VISIBLE) {
                                                        //Log.e("TAG","Before feedback array check : ");
                                                        getMultipleSelections();
                                                        //Log.e("TAG","After multiselection");
                                                        if (feedbackArrayList.size() != 0) {
                                                            //Integer rev = Integer.parseInt(feedbackArrayList.get(0).getF14()) / 100;
                                                           // Log.e("TAG","before ts version");
                                                            String F14_XXXX = feedbackArrayList.get(0).getF14();
                                                            //Log.e("TAG","Version : " + F14_XXXX);  //0512
                                                            String first = F14_XXXX.substring(0,2);
                                                            String second = F14_XXXX.substring(2,4);
                                                            F14_XXXX = first + "." + second;
                                                            //Log.e("TAG","Version : " + F14_XXXX + " first "  + first + " second " + second);

                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                                                                //Log.e("TAG","Inside SDK Int : " + F14_XXXX);
                                                                txt_F14_XXXX.setText(Html.fromHtml("TS REV : <b>" + BuildConfig.VERSION_NAME + "</b><br>CTLR REV : <b>" + F14_XXXX + "</b>", Html.FROM_HTML_MODE_COMPACT));

//                                                                txt_F07_YY.setText(Html.fromHtml("CFM BASE SETPOINT<br>CFM CNTL SETPOINT : <b>" + responseHandler.hexToString(feedbackArrayList.get(0).getF07(), false) + "</b>", Html.FROM_HTML_MODE_COMPACT));
//                                                                txt_S09_SB_XYYY.setText(Utility.ACPOINT);
                                                                txt_S10_SB_XYYY.setText(Utility.DCPOINT);

                                                                String S09 = responseHandler.hexToBinary(setpointArrayList.get(0).getS09());

                                                                if (S09.charAt(0) == '0') {
                                                                    // +
                                                                    float decimal = Float.parseFloat(String.valueOf(responseHandler.binaryToDecimal(Integer.parseInt(S09.substring(2, 16))))) / 1000;
                                                                    txt_S09_SB_XYYY.setText("+" + CodeReUse.formatter3Digit.format(decimal));
                                                                } else {
                                                                    // -
                                                                    float decimal = Float.parseFloat(String.valueOf(responseHandler.binaryToDecimal(Integer.parseInt(S09.substring(2, 16))))) / 1000;
                                                                    txt_S09_SB_XYYY.setText("-" + CodeReUse.formatter3Digit.format(decimal));
                                                                }

                                                                String S10 = responseHandler.hexToBinary(setpointArrayList.get(0).getS10());

                                                                if (S10.charAt(0) == '0') {
                                                                    // +
                                                                    float decimal = Float.parseFloat(String.valueOf(responseHandler.binaryToDecimal(Integer.parseInt(S10.substring(2, 16))))) / 1000;
                                                                    txt_S10_SB_XYYY.setText("+" + CodeReUse.formatter3Digit.format(decimal));
                                                                } else {
                                                                    // -
                                                                    float decimal = Float.parseFloat(String.valueOf(responseHandler.binaryToDecimal(Integer.parseInt(S10.substring(2, 16))))) / 1000;
                                                                    txt_S10_SB_XYYY.setText("-" + CodeReUse.formatter3Digit.format(decimal));
                                                                }

                                                                txt_S06_SB_XXXX.setText(responseHandler.hexToString(setpointArrayList.get(0).getS06()));
                                                                String S01_Y3 = String.valueOf(responseHandler.hexToBinary(setpointArrayList.get(0).getS01()).charAt(8));
                                                                if (S01_Y3.equals("0"))
                                                                {
                                                                    txt_S01_Y3.setText("DISABLE");
                                                                }
                                                                else
                                                                {
                                                                    txt_S01_Y3.setText("ENABLE");
                                                                }

                                                                int S08_XXXX = Integer.parseInt(responseHandler.hexToString(setpointArrayList.get(0).getS08()));

                                                                if (S08_XXXX < 100) {
                                                                    int F07_XX = S08_XXXX + Integer.parseInt(responseHandler.hexToString(setpointArrayList.get(0).getS07(), false));
//                                                                    txt_F07_XX_subSetting.setText(Html.fromHtml("ACH BASE SETPOINT<br>ACH CNTL SETPOINT : <b>" + F07_XX + "</b>", Html.FROM_HTML_MODE_COMPACT));
                                                                } else {
                                                                    int F07_XX = S08_XXXX - 256 + Integer.parseInt(responseHandler.hexToString(setpointArrayList.get(0).getS07(), false));
//                                                                    txt_F07_XX_subSetting.setText(Html.fromHtml("ACH BASE SETPOINT<br>ACH CNTL SETPOINT : <b>" + F07_XX + "</b>", Html.FROM_HTML_MODE_COMPACT));
                                                                }

                                                            } else {
                                                                //Log.e("TAG","Not version sdk: " + F14_XXXX);
                                                                txt_F14_XXXX.setText(Html.fromHtml("TS REV : <b>" + BuildConfig.VERSION_NAME + "</b><br>CTLR REV : <b>" + F14_XXXX + "</b>"));
//                                                                txt_F07_YY.setText(Html.fromHtml("CFM BASE SETPOINT<br>CFM CNTL SETPOINT : <b>" + responseHandler.hexToString(feedbackArrayList.get(0).getF07(), false) + "</b>"));
//                                                                txt_S09_SB_XYYY.setText(Utility.ACPOINT);
//                                                                txt_S10_SB_XYYY.setText(Utility.DCPOINT);

                                                                String S09 = responseHandler.hexToBinary(setpointArrayList.get(0).getS09());

                                                                if (S09.charAt(0) == '0') {
                                                                    // +
                                                                    float decimal = Float.parseFloat(String.valueOf(responseHandler.binaryToDecimal(Integer.parseInt(S09.substring(2, 16))))) / 1000;
                                                                    txt_S09_SB_XYYY.setText("+" + CodeReUse.formatter3Digit.format(decimal));
                                                                } else {
                                                                    // -
                                                                    float decimal = Float.parseFloat(String.valueOf(responseHandler.binaryToDecimal(Integer.parseInt(S09.substring(2, 16))))) / 1000;
                                                                    txt_S09_SB_XYYY.setText("-" + CodeReUse.formatter3Digit.format(decimal));
                                                                }

                                                                String S10 = responseHandler.hexToBinary(setpointArrayList.get(0).getS10());

                                                                if (S10.charAt(0) == '0') {
                                                                    // +
                                                                    float decimal = Float.parseFloat(String.valueOf(responseHandler.binaryToDecimal(Integer.parseInt(S10.substring(2, 16))))) / 1000;
                                                                    txt_S10_SB_XYYY.setText("+" + CodeReUse.formatter3Digit.format(decimal));
                                                                } else {
                                                                    // -
                                                                    float decimal = Float.parseFloat(String.valueOf(responseHandler.binaryToDecimal(Integer.parseInt(S10.substring(2, 16))))) / 1000;
                                                                    txt_S10_SB_XYYY.setText("-" + CodeReUse.formatter3Digit.format(decimal));
                                                                }


                                                                txt_S06_SB_XXXX.setText(responseHandler.hexToString(setpointArrayList.get(0).getS06()));

                                                                String S01_Y3 = String.valueOf(responseHandler.hexToBinary(setpointArrayList.get(0).getS01()).charAt(8));
                                                                txt_S01_Y3.setText(S01_Y3);

                                                                int S08_XXXX = Integer.parseInt(responseHandler.hexToString(setpointArrayList.get(0).getS08()));

                                                                if (S08_XXXX < 100) {
                                                                    int F07_XX = S08_XXXX + Integer.parseInt(responseHandler.hexToString(setpointArrayList.get(0).getS07(), false));
//                                                                    txt_F07_XX_subSetting.setText(Html.fromHtml("ACH BASE SETPOINT<br>ACH CNTL SETPOINT : <b>" + F07_XX + "</b>"));
                                                                } else {
                                                                    int F07_XX = S08_XXXX - 256 + Integer.parseInt(responseHandler.hexToString(setpointArrayList.get(0).getS07(), false));
//                                                                    txt_F07_XX_subSetting.setText(Html.fromHtml("ACH BASE SETPOINT<br>ACH CNTL SETPOINT : <b>" + F07_XX + "</b>"));
                                                                }
                                                            }

                                                            if (setpointArrayList.size() != 0) {

                                                                for (int i = 0; i < multipleSelections.size(); i++) {
                                                                    if (multipleSelections.get(i).getId().equals(responseHandler.hexToBinary(setpointArrayList.get(0).getS01()).substring(13, 16))) {
//                                                                        txt_S09_SB_XYYY.setText(multipleSelections.get(i).getName());
                                                                        multipleSelections.get(i).setSelected(true);
                                                                    }
                                                                }
//                                                                txt_S10_SB_XYYY.setText(responseHandler.hexToString(setpointArrayList.get(0).getS07(), true));
//                                                                txt_S06_SB_XXXX.setText(responseHandler.hexToString(setpointArrayList.get(0).getS07(), false));
                                                            }
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    Log.e("TAG","Firmware Version Error: " + e.getMessage());
                                                    e.printStackTrace();
                                                }

                                                // ExhaustBlowerSettingActivity AnalysisData
                                                try {
                                                    if (exhaustBlowerSetting_layout.getVisibility() == View.VISIBLE) {
                                                        getMultipleSelections();

                                                        if (feedbackArrayList.size() != 0) {

                                                            /*String F09 = feedbackArrayList.get(0).getF09();

                                                            String a = F09.substring(1, 4);
                                                            a = F09.substring(1);

                                                            Integer F09Value = Integer.parseInt(F09.substring(1), 16);

                                                            String F09_XXXX = CodeReUse.formatter3Digit.format(Float.valueOf(F09Value) / 1000);

                                                            if (F09.startsWith("8"))
                                                                F09_XXXX = "-" + F09_XXXX;
                                                            else
                                                                F09_XXXX = "+" + F09_XXXX;

                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                                txt_F09_XXXX.setText(Html.fromHtml("Pressure Control Setpoint = " + F09_XXXX, Html.FROM_HTML_MODE_COMPACT));
                                                            } else {
                                                                txt_F09_XXXX.setText(Html.fromHtml("Pressure Control Setpoint = " + F09_XXXX));
                                                            }*/

                                                            if (setpointArrayList.size() != 0) {
                                                               /* for (int i = 0; i < multipleSelections.size(); i++) {
                                                                    String S01_Z3 = String.valueOf(responseHandler.hexToBinary(setpointArrayList.get(0).getS01()).charAt(12));
                                                                    if (multipleSelections.get(i).getId().equals(S01_Z3)) {
                                                                        txt_S13_XXXX.setText(multipleSelections.get(i).getName());
                                                                        multipleSelections.get(i).setSelected(true);
                                                                    }
                                                                }

                                                                String S09 = responseHandler.hexToBinary(setpointArrayList.get(0).getS09());

                                                                if (S09.charAt(0) == '0') {
                                                                    // +
                                                                    float decimal = Float.parseFloat(String.valueOf(responseHandler.binaryToDecimal(Integer.parseInt(S09.substring(2, 16))))) / 1000;
                                                                    txt_S14_XXXX.setText("+" + CodeReUse.formatter3Digit.format(decimal));
                                                                } else {
                                                                    // -
                                                                    float decimal = Float.parseFloat(String.valueOf(responseHandler.binaryToDecimal(Integer.parseInt(S09.substring(2, 16))))) / 1000;
                                                                    txt_S14_XXXX.setText("-" + CodeReUse.formatter3Digit.format(decimal));
                                                                }

                                                                String S10 = responseHandler.hexToBinary(setpointArrayList.get(0).getS10());

                                                                if (S10.charAt(0) == '0') {
                                                                    // +
                                                                    float decimal = Float.parseFloat(String.valueOf(responseHandler.binaryToDecimal(Integer.parseInt(S10.substring(2, 16))))) / 1000;
                                                                    txt_S10_XYYY.setText("+" + CodeReUse.formatter3Digit.format(decimal));
                                                                } else {
                                                                    // -
                                                                    float decimal = Float.parseFloat(String.valueOf(responseHandler.binaryToDecimal(Integer.parseInt(S10.substring(2, 16))))) / 1000;
                                                                    txt_S10_XYYY.setText("-" + CodeReUse.formatter3Digit.format(decimal));
                                                                }

                                                                txt_S06_XXXX.setText(responseHandler.hexToString(setpointArrayList.get(0).getS06()));*/

                                                                String S15_Z0 = String.valueOf(responseHandler.hexToBinary(setpointArrayList.get(0).getS15()).charAt(15));
                                                                //txt_S15_Z0.setText(S15_Z0);
                                                                if (S15_Z0.equals("0"))
                                                                {
                                                                    txt_S15_Z0.setText("DISABLE");
                                                                }
                                                                else
                                                                {
                                                                    txt_S15_Z0.setText("ENABLE");
                                                                }

                                                                txt_S13_XXXX.setText(responseHandler.hexToString(setpointArrayList.get(0).getS13()));
                                                                txt_S14_XXXX.setText(responseHandler.hexToString(setpointArrayList.get(0).getS14()));

                                                                String S16 = setpointArrayList.get(0).getS16();
                                                                int S16_Max = (Integer.parseInt(responseHandler.hexToString(S16, true)));
                                                                int S16_Min = (Integer.parseInt(responseHandler.hexToString(S16, false)));
                                                                String S16_XX_Supply_Temp_High_Max = String.valueOf(S16_Max);
                                                                String S16_YY_Supply_Temp_Low_Min = String.valueOf(S16_Min);
                                                                txt_S16_XXYY.setText(S16_XX_Supply_Temp_High_Max + " / " + S16_YY_Supply_Temp_Low_Min);

                                                                String S17 = setpointArrayList.get(0).getS17();
                                                                int S17_Max = (Integer.parseInt(responseHandler.hexToString(S17, true)));
                                                                int S17_Min = (Integer.parseInt(responseHandler.hexToString(S17, false)));
                                                                String S17_XX_Supply_Temp_High_Max = String.valueOf(S17_Max);
                                                                String S17_YY_Supply_Temp_Low_Min = String.valueOf(S17_Min);
                                                                txt_S17_XXYY.setText(S17_XX_Supply_Temp_High_Max + " / " + S17_YY_Supply_Temp_Low_Min);

                                                                if (!isExhaustAvailable()) {
                                                                    txt_S15_Z0.setText("");
                                                                    txt_S13_XXXX.setText("");
                                                                    txt_S14_XXXX.setText("");
                                                                    txt_S16_XXYY.setText("");
                                                                    txt_S17_XXYY.setText("");
                                                                }
                                                            }
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                                // FilterSettingActivity AnalysisData
                                                try {
                                                    if (filterSetting_layout.getVisibility() == View.VISIBLE) {
                                                        if (feedbackArrayList.size() != 0) {
                                                            //F10=0002
                                                            double F10_XXXX = Double.valueOf(responseHandler.hexToString(feedbackArrayList.get(0).getF10())) / 10;

                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                                txt_F10_XXXX.setText(Html.fromHtml("PREFILTER POH (Run hours : <b>" + F10_XXXX + "</b>)", Html.FROM_HTML_MODE_COMPACT));
                                                            } else {
                                                                txt_F10_XXXX.setText(Html.fromHtml("PREFILTER POH (Run hours : <b>" + F10_XXXX + "</b>)"));
                                                            }

                                                            if (setpointArrayList.size() != 0) {
                                                                txt_S03_XXXX.setText(responseHandler.hexToString(setpointArrayList.get(0).getS03()));
                                                                txt_S04_XXXX.setText(responseHandler.hexToString(setpointArrayList.get(0).getS04()));
                                                                txt_S05_XXXX.setText(responseHandler.hexToString(setpointArrayList.get(0).getS05()));

                                                                int S11_YY = (Integer.parseInt(responseHandler.hexToString(setpointArrayList.get(0).getS11(), false)) * 100) + 2500;
                                                                txt_S11_YY.setText(String.valueOf(S11_YY));

                                                                txt_S11_XX.setText(responseHandler.hexToString(setpointArrayList.get(0).getS11(), true));
                                                                txt_S12_XXXX.setText(responseHandler.hexToString(setpointArrayList.get(0).getS12()));
                                                            }
                                                            if (!isExhaustAvailable()) {
                                                                txt_S04_XXXX.setText("");
                                                                txt_S05_XXXX.setText("");

                                                            }
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                                // CommunicationSettingActivity AnalysisData
                                                try {
                                                    if (communicationSetting_layout.getVisibility() == View.VISIBLE) {
                                                        getMultipleSelections("txt_S01_X1_X0");
                                                        if (setpointArrayList.size() != 0) {
                                                            for (int i = 0; i < multipleSelections4.size(); i++) {
                                                                String S01_X1_X0 = String.valueOf(responseHandler.hexToBinary(setpointArrayList.get(0).getS01()).charAt(6)) + responseHandler.hexToBinary(setpointArrayList.get(0).getS01()).charAt(7);
                                                                if (multipleSelections4.get(i).getId().equals(S01_X1_X0)) {
                                                                    txt_S01_X1_X0.setText(multipleSelections4.get(i).getName());
                                                                    multipleSelections4.get(i).setSelected(true);
                                                                }
                                                            }
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                                // CommunicationSettingActivity AnalysisData
                                                try {
                                                    if (communicationSetting_wifiData_layout.getVisibility() == View.VISIBLE) {
                                                        /*getMultipleSelections("txt_S01_X1_X0");
                                                        getMultipleSelections("communicationSetting_wifiData_layout");
                                                        if (setpointArrayList.size() != 0) {
                                                            for (int i = 0; i < multipleSelections4.size(); i++) {
                                                                String S01_X1_X0 = String.valueOf(responseHandler.hexToBinary(setpointArrayList.get(0).getS01()).charAt(6)) + responseHandler.hexToBinary(setpointArrayList.get(0).getS01()).charAt(7);
                                                                if (multipleSelections4.get(i).getId().equals(S01_X1_X0)) {
                                                                    txt_S01_X1_X0.setText(multipleSelections4.get(i).getName());
                                                                    multipleSelections4.get(i).setSelected(true);
                                                                }
                                                            }
                                                        }*/
                                                        getMultipleSelections("communicationSetting_wifiData_layout");

                                                        if (wifiArrayList.size() != 0) {
                                                            txt_NETWORK_SSID.setText(wifiArrayList.get(0).getW01());

                                                            //txt_SECURITY_MODE.setText(wifiArrayList.get(0).getW10());
                                                            for (int i = 0; i < multipleSelections7.size(); i++) {
                                                                if (multipleSelections7.get(i).getId().equals(wifiArrayList.get(0).getW10())) {
                                                                    txt_SECURITY_MODE.setText(multipleSelections7.get(i).getName());
                                                                }
                                                            }

                                                            txt_SECURITY_KEY.setText(wifiArrayList.get(0).getW02());
                                                            txt_SECURITY_KEY_VALUE.setText(wifiArrayList.get(0).getW11());
//                                                            txt_COUNTRY_CODE.setText(wifiArrayList.get(0).getW09());

                                                            for (int i = 0; i < multipleSelections9.size(); i++) {
                                                                if (multipleSelections9.get(i).getId().equals(wifiArrayList.get(0).getW09())) {
                                                                    txt_COUNTRY_CODE.setText(multipleSelections9.get(i).getName());
                                                                }
                                                            }

                                                            //txt_DHCP.setText(wifiArrayList.get(0).getW08());
                                                            for (int i = 0; i < multipleSelections8.size(); i++) {
                                                                if (multipleSelections8.get(i).getId().equals(wifiArrayList.get(0).getW08())) {
                                                                    txt_DHCP.setText(multipleSelections8.get(i).getName());
                                                                }
                                                            }

                                                            if (txt_DHCP.getText().equals("STATIC")) {
                                                                txt_IP_ADDRESS.setEnabled(true);
                                                                txt_SUBNET.setEnabled(true);
                                                                txt_DEFAULT_GATEWAY.setEnabled(true);
                                                                txt_DNS_SERVER.setEnabled(true);

//                                                                txt_IP_ADDRESS.setTextColor(getResources().getColor(R.color.txt_skyblue));
//                                                                txt_SUBNET.setTextColor(getResources().getColor(R.color.txt_skyblue));
//                                                                txt_DEFAULT_GATEWAY.setTextColor(getResources().getColor(R.color.txt_skyblue));
//                                                                txt_DNS_SERVER.setTextColor(getResources().getColor(R.color.txt_skyblue));

                                                                txt_IP_ADDRESS.setTextColor(getResources().getColor(R.color.white));
                                                                txt_SUBNET.setTextColor(getResources().getColor(R.color.white));
                                                                txt_DEFAULT_GATEWAY.setTextColor(getResources().getColor(R.color.white));
                                                                txt_DNS_SERVER.setTextColor(getResources().getColor(R.color.white));
                                                            } else {
                                                                txt_IP_ADDRESS.setEnabled(false);
                                                                txt_SUBNET.setEnabled(false);
                                                                txt_DEFAULT_GATEWAY.setEnabled(false);
                                                                txt_DNS_SERVER.setEnabled(false);

//                                                                txt_IP_ADDRESS.setTextColor(getResources().getColor(R.color.white));
//                                                                txt_SUBNET.setTextColor(getResources().getColor(R.color.white));
//                                                                txt_DEFAULT_GATEWAY.setTextColor(getResources().getColor(R.color.white));
//                                                                txt_DNS_SERVER.setTextColor(getResources().getColor(R.color.white));

                                                                txt_IP_ADDRESS.setTextColor(getResources().getColor(R.color.txt_skyblue));
                                                                txt_SUBNET.setTextColor(getResources().getColor(R.color.txt_skyblue));
                                                                txt_DEFAULT_GATEWAY.setTextColor(getResources().getColor(R.color.txt_skyblue));
                                                                txt_DNS_SERVER.setTextColor(getResources().getColor(R.color.txt_skyblue));
                                                            }

                                                            txt_IP_ADDRESS.setText(wifiArrayList.get(0).getW03());
                                                            txt_SUBNET.setText(wifiArrayList.get(0).getW04());
                                                            txt_DEFAULT_GATEWAY.setText(wifiArrayList.get(0).getW05());
                                                            txt_DNS_SERVER.setText(wifiArrayList.get(0).getW06());
                                                            txt_GATEWAY_PING.setText(wifiArrayList.get(0).getW07());
                                                            txt_WIFI_id.setText("WIFI MAC ADDRESS : " + wifiArrayList.get(0).getW12());
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                                // UnitSettingActivity AnalysisData
                                                try {
                                                    if (unitSetting_layout.getVisibility() == View.VISIBLE) {
                                                        getMultipleSelections("txt_S01_Y0");
                                                        getMultipleSelections("txt_S01_Y1");
                                                        getMultipleSelections("txt_S01_Y2");

                                                        if (setpointArrayList.size() != 0) {

                                                            for (int i = 0; i < multipleSelections2.size(); i++) {
                                                                String S01_Y2 = String.valueOf(responseHandler.hexToBinary(setpointArrayList.get(0).getS01()).charAt(9));
                                                                if (multipleSelections2.get(i).getId().equals(S01_Y2)) {
                                                                    txt_S01_Y2.setText(multipleSelections2.get(i).getName());
                                                                    multipleSelections2.get(i).setSelected(true);
                                                                }
                                                            }

                                                            for (int i = 0; i < multipleSelections1.size(); i++) {
                                                                String S01_Y1 = String.valueOf(responseHandler.hexToBinary(setpointArrayList.get(0).getS01()).charAt(10));
                                                                if (multipleSelections1.get(i).getId().equals(S01_Y1)) {
                                                                    txt_S01_Y1.setText(multipleSelections1.get(i).getName());
                                                                    multipleSelections1.get(i).setSelected(true);
                                                                }
                                                            }

                                                            for (int i = 0; i < multipleSelections0.size(); i++) {
                                                                String S01_Y0 = String.valueOf(responseHandler.hexToBinary(setpointArrayList.get(0).getS01()).charAt(11));
                                                                if (multipleSelections0.get(i).getId().equals(S01_Y0)) {
                                                                    txt_S01_Y0.setText(multipleSelections0.get(i).getName());
                                                                    multipleSelections0.get(i).setSelected(true);
                                                                }
                                                            }
                                                        }

                                                        // TODO :- Show Supply And Exhaust Temp, Humidity Values
//                                                        int S11_YY = (Integer.parseInt(responseHandler.hexToString(setpointArrayList.get(0).getS11(), false)) * 100) + 2500;
//                                                        txt_S11_YY.setText(String.valueOf(S11_YY));

                                                        /*String S22 = setpointArrayList.get(0).getS22();
                                                        int S22_Max = (Integer.parseInt(responseHandler.hexToString(S22, true)));
                                                        int S22_Min = (Integer.parseInt(responseHandler.hexToString(S22, false)));
                                                        String S22_XX_Supply_Temp_High_Max = String.valueOf(S22_Max);
                                                        String S22_YY_Supply_Temp_Low_Min = String.valueOf(S22_Min);
                                                        txt_S022_Value.setText(S22_XX_Supply_Temp_High_Max + " / " + S22_YY_Supply_Temp_Low_Min);

                                                        String S23 = setpointArrayList.get(0).getS23();
                                                        int S23_Max = (Integer.parseInt(responseHandler.hexToString(S23, true)));
                                                        int S23_Min = (Integer.parseInt(responseHandler.hexToString(S23, false)));
                                                        String S23_XX_Supply_Hmd_High_Max = String.valueOf(S23_Max);
                                                        String S23_YY_Supply_Hmd_Low_Min = String.valueOf(S23_Min);
                                                        txt_S023_Value.setText(S23_XX_Supply_Hmd_High_Max + " / " + S23_YY_Supply_Hmd_Low_Min);

                                                        String S24 = setpointArrayList.get(0).getS24();
                                                        int S24_Max = (Integer.parseInt(responseHandler.hexToString(S24, true)));
                                                        int S24_Min = (Integer.parseInt(responseHandler.hexToString(S24, false)));
                                                        String S24_XX_Exhaust_Temp_High_Max = String.valueOf(S24_Max);
                                                        String S24_YY_Exhaust_Temp_Low_Min = String.valueOf(S24_Min);
                                                        txt_S024_Value.setText(S24_XX_Exhaust_Temp_High_Max + " / " + S24_YY_Exhaust_Temp_Low_Min);

                                                        String S25 = setpointArrayList.get(0).getS25();
                                                        int S25_Max = (Integer.parseInt(responseHandler.hexToString(S25, true)));
                                                        int S25_Min = (Integer.parseInt(responseHandler.hexToString(S25, false)));
                                                        String S25_XX_Exhaust_Hmd_High_Max = String.valueOf(S25_Max);
                                                        String S25_YY_Exhaust_Hmd_Low_Min = String.valueOf(S25_Min);
                                                        txt_S025_Value.setText(S25_XX_Exhaust_Hmd_High_Max + " / " + S25_YY_Exhaust_Hmd_Low_Min);*/
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                                // DeconSettingActivity AnalysisData
                                                try {
                                                    if (deconSetting_layout.getVisibility() == View.VISIBLE) {
                                                        if (setpointArrayList.size() != 0) {
                                                            int S02_XX = (Integer.parseInt(responseHandler.hexToString(setpointArrayList.get(0).getS02(), true)) * 10) + 800;
                                                            txt_S02_XX.setText(String.valueOf(S02_XX));

                                                            int S02_YY = (Integer.parseInt(responseHandler.hexToString(setpointArrayList.get(0).getS02(), false)) * 10) + 800;
                                                            txt_S02_YY.setText(String.valueOf(S02_YY));

                                                            String decontime = setpointArrayList.get(0).getS21();
                                                            Integer hours = Integer.parseInt(decontime.substring(0, 2), 16);
                                                            Integer minutes = Integer.parseInt(decontime.substring(2), 16);

                                                            String hrs = "0" + hours.toString();
                                                            String mins = "";
                                                            if (minutes < 10)
                                                                mins = "0" + minutes.toString();
                                                            else
                                                                mins = minutes.toString();

                                                            txt_S21_XX_YY.setText(hrs + ":" + mins);
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                                // NightModeSettingActivity AnalysisData
                                                try {
                                                    if (nightModeSetting_layout.getVisibility() == View.VISIBLE) {
                                                        //txt_OnTime_nightModeSetting.setText(prefManager.getOnNightMode().replace("00:00 "," "));
                                                        //txt_OffTime_nightModeSetting.setText(prefManager.getOffNightMode().replace("00:00 "," "));
                                                        txt_OnTime_nightModeSetting.setText(prefManager.getOnNightMode().replace(":00 ", " "));
                                                        txt_OffTime_nightModeSetting.setText(prefManager.getOffNightMode().replace(":00 ", " "));
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        // DiagnosticsSettingActivity AnalysisData
                                        try {
                                            if (alertview_diagnostics.isShowing()) {
                                                if (diagnosticsArrayList.size() != 0) {

                                                    double D22_XXXX = Double.valueOf(responseHandler.hexToString(diagnosticsArrayList.get(0).getD22())) / 4095 * 5;
                                                    double D23_XXXX = Double.valueOf(responseHandler.hexToString(diagnosticsArrayList.get(0).getD23())) / 4095 * 5;
                                                    double D24_XXXX = Double.valueOf(responseHandler.hexToString(diagnosticsArrayList.get(0).getD24())) / 4095 * 5;
                                                    double D25_XXXX = Double.valueOf(responseHandler.hexToString(diagnosticsArrayList.get(0).getD25())) / 4095 * 5;

                                                    double D25_D24_XXXX = D25_XXXX - D24_XXXX;

                                                    double D26_XXXX = Double.valueOf(responseHandler.hexToString(diagnosticsArrayList.get(0).getD26())) / 4095 * 5;
                                                    double D27_XXXX = Double.valueOf(responseHandler.hexToString(diagnosticsArrayList.get(0).getD27())) / 4095 * 5;
                                                    double D28_XXXX = Double.valueOf(responseHandler.hexToString(diagnosticsArrayList.get(0).getD28())) / 4095 * 5;
                                                    double D29_XXXX = Double.valueOf(responseHandler.hexToString(diagnosticsArrayList.get(0).getD29())) / 4095 * 5;

                                                    double D29_D28_XXXX = D29_XXXX - D28_XXXX;

                                                    txt_D22_XXXX.setText(CodeReUse.formatter4Digit.format(D22_XXXX) + "v");
                                                    txt_D23_XXXX.setText(CodeReUse.formatter4Digit.format(D23_XXXX) + "v");
                                                    txt_D24_XXXX.setText(CodeReUse.formatter4Digit.format(D24_XXXX) + "v");
                                                    txt_D25_XXXX.setText(CodeReUse.formatter4Digit.format(D25_XXXX) + "v");
                                                    txt_D25_D24_XXXX.setText(CodeReUse.formatter4Digit.format(D25_D24_XXXX) + "v");

                                                    txt_D26_XXXX.setText(CodeReUse.formatter4Digit.format(D26_XXXX) + "v");
                                                    txt_D27_XXXX.setText(CodeReUse.formatter4Digit.format(D27_XXXX) + "v");
                                                    txt_D28_XXXX.setText(CodeReUse.formatter4Digit.format(D28_XXXX) + "v");
                                                    txt_D29_XXXX.setText(CodeReUse.formatter4Digit.format(D29_XXXX) + "v");
                                                    txt_D29_D28_XXXX.setText(CodeReUse.formatter4Digit.format(D29_D28_XXXX) + "v");

                                                    double D30_XXXX = Double.valueOf(responseHandler.hexToString(diagnosticsArrayList.get(0).getD30()));

                                                    double hours = D30_XXXX / 3600;
                                                    double minute = (hours - (int) hours) * 60;
                                                    double seconds = (minute - (int) minute) * 60;

                                                    txt_D30_XXXX.setText(CodeReUse.formatter2Digit.format((int) hours) + ":" + CodeReUse.formatter2Digit.format((int) minute) + ":" + CodeReUse.formatter2Digit.format((int) seconds));

                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        // DiagnosticsDetailsSettingActivity AnalysisData
                                        try {
                                            if (alertview_diagnostics_details.isShowing()) {
                                                if (diagnosticsArrayList.size() != 0) {

                                                    int D10_XXXX = Integer.valueOf(responseHandler.hexToString(diagnosticsArrayList.get(0).getD10()));
                                                    int D11_XXXX = Integer.valueOf(responseHandler.hexToString(diagnosticsArrayList.get(0).getD11()));

                                                    txt_D10_XXXX.setText(String.valueOf(D10_XXXX));
                                                    txt_D11_XXXX.setText(String.valueOf(D11_XXXX));

                                                    seekbar_Progress_SupplyPWMValue.setMin(0);
                                                    seekbar_Progress_SupplyPWMValue.setMax(1023);

                                                    seekbar_Progress_SupplyPWMValue.setProgress(D10_XXXX);

                                                    seekbar_Progress_ExhaustPWMValue.setMin(0);
                                                    seekbar_Progress_ExhaustPWMValue.setMax(1023);

                                                    seekbar_Progress_ExhaustPWMValue.setProgress(D11_XXXX);

                                                    String F13_Z2 = String.valueOf(responseHandler.hexToBinary(feedbackArrayList.get(0).getF13()).charAt(13));

                                                    int F16_XXXX = Integer.valueOf(responseHandler.hexToString(feedbackArrayList.get(0).getF16()));
                                                    txt_F16_XXXX.setText("24-hr stored value = " + String.valueOf(F16_XXXX));

                                                    if (F13_Z2.equals("1")) {
                                                        txt_F13_Z2.setVisibility(View.VISIBLE);
                                                    } else {
                                                        txt_F13_Z2.setVisibility(View.GONE);
                                                    }
                                                    //txt_F13_Z2.setVisibility(View.VISIBLE);
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    } else {
                                        if (CodeReUse.isBolwerAdmin && CodeReUse.isBolwerConnected)
                                            Toast.makeText(act, "Please wait while communication is starting..", Toast.LENGTH_SHORT).show();
                                        else if (!CodeReUse.isBolwerAdmin && !CodeReUse.isBolwerConnected)
                                            Toast.makeText(act, "Please wait while communication is starting..", Toast.LENGTH_SHORT).show();
                                    }

                                    if (prefManager.getHostName() == null || !prefManager.getHostName().contains("http")) {
                                        Log.e("TAG", "HostName Check on setAnalysisData : Host is Not Available.");
                                        img_wicom_cloud_logo.setVisibility(View.INVISIBLE);
                                        img_wicom_cloud_warning.setVisibility(View.INVISIBLE);
                                    }
                                    else
                                    {
                                        img_wicom_cloud_logo.setVisibility(View.VISIBLE);
                                        Log.e("TAG","Checking customer in SetAnalyasisData : " + String.valueOf(CodeReUse.isCustomerActive));
                                        if (CodeReUse.isCustomerActive)
                                        {
                                            img_wicom_cloud_warning.setVisibility(View.INVISIBLE);
                                        }
                                        else
                                        {
                                            img_wicom_cloud_warning.setVisibility(View.VISIBLE);
                                        }
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!isInnerFunction)
                    isInnerFunction = true;
            }
        }).start();
    }

    // Supply OR Exhaust (Blower Or Hose) Pass/Fail Condition Function
    public void getSupplyOrExhaustPassOrFail() {

        String strF15 = feedbackArrayList.get(0).getF15();

        if(responseHandler.hexToBinary(feedbackArrayList.get(0).getF12()).charAt(15) == '1' ||
            responseHandler.hexToBinary(feedbackArrayList.get(0).getF12()).charAt(9) == '1' ||
            responseHandler.hexToBinary(feedbackArrayList.get(0).getF12()).charAt(7) == '1' ||
            responseHandler.hexToBinary(feedbackArrayList.get(0).getF12()).charAt(5) == '1' ||
            responseHandler.hexToBinary(feedbackArrayList.get(0).getF12()).charAt(1) == '1' ||
            responseHandler.hexToBinary(feedbackArrayList.get(0).getF13()).charAt(0) == '1' ||
            responseHandler.hexToBinary(feedbackArrayList.get(0).getF13()).charAt(5)=='1') {
            setAllColorToHomeScreenLayout("red");
        } else if(responseHandler.hexToBinary(feedbackArrayList.get(0).getF12()).charAt(6) == '1' ||
            responseHandler.hexToBinary(feedbackArrayList.get(0).getF13()).charAt(4) == '1' ||
        responseHandler.hexToBinary(feedbackArrayList.get(0).getF13()).charAt(3) == '1' ||
        responseHandler.hexToBinary(feedbackArrayList.get(0).getF13()).charAt(2) == '1' ||
        responseHandler.hexToBinary(feedbackArrayList.get(0).getF13()).charAt(1) == '1') {
            setAllColorToHomeScreenLayout("yellow");
        } else  {
            isRedYellowColorHomeScreen = false;
            Log.e(TAG, "Calling setAllColorToHomeScreenLayout from getSupplyOrExhaustPassOrFail");
            setAllColorToHomeScreenLayout("green");
        }

        /*if (responseHandler.hexToBinary(feedbackArrayList.get(0).getF12()).charAt(15) == '1') { // For z0 = 1 Red Screen
            setAllColorToHomeScreenLayout("red");
        } else if (responseHandler.hexToBinary(feedbackArrayList.get(0).getF12()).charAt(13) == '1') { // for z2 = 1 Red Screen
            setAllColorToHomeScreenLayout("red");
        } else if (responseHandler.hexToBinary(feedbackArrayList.get(0).getF12()).charAt(7) == '1') { // for x0 = 1 Red Screen
            setAllColorToHomeScreenLayout("red");
        } else if (responseHandler.hexToBinary(feedbackArrayList.get(0).getF12()).charAt(5) == '1') { // for x2 = 1 Red Screen
            setAllColorToHomeScreenLayout("red");
        } else if (responseHandler.hexToBinary(feedbackArrayList.get(0).getF12()).charAt(14) == '1') { // for z1 = 1 yellow Screen
            setAllColorToHomeScreenLayout("yellow");
        } else if (responseHandler.hexToBinary(feedbackArrayList.get(0).getF12()).charAt(6) == '1') { // for x1 = 1 yellow Screen
            setAllColorToHomeScreenLayout("yellow");
        } else if (strF15.charAt(0) == '1') {
            setAllColorToHomeScreenLayout("yellow");
        } else if (strF15.charAt(1) == '1') {
            setAllColorToHomeScreenLayout("yellow");
        } else if (strF15.charAt(2) == '1') {
            setAllColorToHomeScreenLayout("yellow");
        } else if (strF15.charAt(3) == '1') {
            setAllColorToHomeScreenLayout("yellow");
        } else {
            isRedYellowColorHomeScreen = false;
            Log.e(TAG, "Calling setAllColorToHomeScreenLayout from getSupplyOrExhaustPassOrFail");
            setAllColorToHomeScreenLayout("green");
        }*/

    }

    // TODO :- Manage & set Red, Yellow and Green Color in One Function
    public void setAllColorToHomeScreenLayout(String color) {

        if (color.equalsIgnoreCase("red")) {
            isRedYellowColorHomeScreen = true;
            main_layout.setBackgroundColor(ContextCompat.getColor(act, R.color.home_red));
            view_home_screen_one.setBackgroundColor(ContextCompat.getColor(act, R.color.white));
            view_home_screen_two.setBackgroundColor(ContextCompat.getColor(act, R.color.white));
            view_home_screen_three.setBackgroundColor(ContextCompat.getColor(act, R.color.white));
            view_home_menu_one.setBackgroundColor(ContextCompat.getColor(act, R.color.white));
            view_home_menu_two.setBackgroundColor(ContextCompat.getColor(act, R.color.white));
            view_home_menu_three.setBackgroundColor(ContextCompat.getColor(act, R.color.white));
            view_home_menu_four.setBackgroundColor(ContextCompat.getColor(act, R.color.white));

            img_logo_one.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_logo_two.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_screen_type_logo.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_wicom_cloud_logo.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            txt_Critical_Alarm.setTextColor(ContextCompat.getColor(act, R.color.white));
//            img_home_screen_plus.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
//            img_home_screen_minus.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
//            img_home_screen_up.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
//            img_home_screen_down.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_home_screen_one.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_home_screen_two.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_home_screen_three.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
//            img_home_screen_four.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

            img_home_menu_one.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_home_menu_two.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_home_menu_three.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_home_menu_four.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_home_menu_five.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

//            txt_F01_XX.setTextColor(getResources().getColor(R.color.white));
            txt_F01_YY.setTextColor(getResources().getColor(R.color.white));
//            txt_F02_XX.setTextColor(getResources().getColor(R.color.white));
            txt_F02_YY.setTextColor(getResources().getColor(R.color.white));
//            txt_S01_Z3.setTextColor(getResources().getColor(R.color.white));
            txt_F08_XXYY.setTextColor(getResources().getColor(R.color.white));

//            txt_S01_Z3_Plus.setBackgroundResource(R.drawable.red_home_screen_textbox);
//            txt_S01_Z3_Minus.setBackgroundResource(R.drawable.red_home_screen_textbox);
//            txt_S08_Up.setBackgroundResource(R.drawable.red_home_screen_textbox);
//            txt_S08_Down.setBackgroundResource(R.drawable.red_home_screen_textbox);
//            txt_F01_XX.setBackgroundResource(R.drawable.black_home_screen_textbox);
            txt_F01_YY.setBackgroundResource(R.drawable.black_home_screen_textbox);
//            txt_F02_XX.setBackgroundResource(R.drawable.black_home_screen_textbox);
            txt_F02_YY.setBackgroundResource(R.drawable.black_home_screen_textbox);
//            txt_S01_Z3.setBackgroundResource(R.drawable.black_home_screen_textbox);
            txt_F08_XXYY.setBackgroundResource(R.drawable.black_home_screen_textbox);
            txt_timer.setTextColor(getResources().getColor(R.color.white));
            txt_version.setTextColor(getResources().getColor(R.color.white));

            img_screen_type_logo.setVisibility(View.GONE);
            txt_Critical_Alarm.setVisibility(View.VISIBLE);
            txt_Warning_Alarm.setVisibility(View.GONE);

            final int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                layout_menubar.setBackgroundDrawable(ContextCompat.getDrawable(act, R.drawable.dark_red_home_screen_box));
                layout_home_screen.setBackgroundDrawable(ContextCompat.getDrawable(act, R.drawable.dark_red_home_screen_box));
            } else {
                layout_menubar.setBackground(ContextCompat.getDrawable(act, R.drawable.dark_red_home_screen_box));
                layout_home_screen.setBackground(ContextCompat.getDrawable(act, R.drawable.dark_red_home_screen_box));
            }
        }
        else if (color.equalsIgnoreCase("yellow")) {
            isRedYellowColorHomeScreen = true;
            main_layout.setBackgroundColor(ContextCompat.getColor(act, R.color.home_yellow));
            view_home_screen_one.setBackgroundColor(ContextCompat.getColor(act, R.color.white));
            view_home_screen_two.setBackgroundColor(ContextCompat.getColor(act, R.color.white));
            view_home_screen_three.setBackgroundColor(ContextCompat.getColor(act, R.color.white));
            view_home_menu_one.setBackgroundColor(ContextCompat.getColor(act, R.color.white));
            view_home_menu_two.setBackgroundColor(ContextCompat.getColor(act, R.color.white));
            view_home_menu_three.setBackgroundColor(ContextCompat.getColor(act, R.color.white));
            view_home_menu_four.setBackgroundColor(ContextCompat.getColor(act, R.color.white));

            img_logo_one.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_logo_two.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_screen_type_logo.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_wicom_cloud_logo.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            txt_Critical_Alarm.setTextColor(ContextCompat.getColor(act, R.color.white));
//            img_home_screen_plus.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
//            img_home_screen_minus.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
//            img_home_screen_up.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
//            img_home_screen_down.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_home_screen_one.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_home_screen_two.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_home_screen_three.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
//            img_home_screen_four.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

            img_home_menu_one.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_home_menu_two.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_home_menu_three.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_home_menu_four.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_home_menu_five.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

//            txt_F01_XX.setTextColor(getResources().getColor(R.color.white));
            txt_F01_YY.setTextColor(getResources().getColor(R.color.white));
//            txt_F02_XX.setTextColor(getResources().getColor(R.color.white));
            txt_F02_YY.setTextColor(getResources().getColor(R.color.white));
//            txt_S01_Z3.setTextColor(getResources().getColor(R.color.white));
            txt_F08_XXYY.setTextColor(getResources().getColor(R.color.white));

//            txt_S01_Z3_Plus.setBackgroundResource(R.drawable.yellow_home_screen_textbox);
//            txt_S01_Z3_Minus.setBackgroundResource(R.drawable.yellow_home_screen_textbox);
//            txt_S08_Up.setBackgroundResource(R.drawable.yellow_home_screen_textbox);
//            txt_S08_Down.setBackgroundResource(R.drawable.yellow_home_screen_textbox);
//            txt_F01_XX.setBackgroundResource(R.drawable.black_home_screen_textbox);
            txt_F01_YY.setBackgroundResource(R.drawable.black_home_screen_textbox);
//            txt_F02_XX.setBackgroundResource(R.drawable.black_home_screen_textbox);
            txt_F02_YY.setBackgroundResource(R.drawable.black_home_screen_textbox);
//            txt_S01_Z3.setBackgroundResource(R.drawable.black_home_screen_textbox);
            txt_F08_XXYY.setBackgroundResource(R.drawable.black_home_screen_textbox);
            txt_timer.setTextColor(getResources().getColor(R.color.white));
            txt_version.setTextColor(getResources().getColor(R.color.white));

            img_screen_type_logo.setVisibility(View.GONE);
            txt_Critical_Alarm.setVisibility(View.GONE);
            txt_Warning_Alarm.setVisibility(View.VISIBLE);

            final int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                layout_menubar.setBackgroundDrawable(ContextCompat.getDrawable(act, R.drawable.dark_yellow_home_screen_box));
                layout_home_screen.setBackgroundDrawable(ContextCompat.getDrawable(act, R.drawable.dark_yellow_home_screen_box));
            } else {
                layout_menubar.setBackground(ContextCompat.getDrawable(act, R.drawable.dark_yellow_home_screen_box));
                layout_home_screen.setBackground(ContextCompat.getDrawable(act, R.drawable.dark_yellow_home_screen_box));
            }
        }
        else {
//            main_layout.setBackgroundColor(ContextCompat.getColor(act, R.color.home_green));
            if(Utility.BLOWER_TYPE.equals(Utility.BCU2))
                main_layout.setBackgroundColor(ContextCompat.getColor(act, R.color.bg_bcu2));
            else
                main_layout.setBackgroundColor(ContextCompat.getColor(act, R.color.bg_spp));
            view_home_screen_one.setBackgroundColor(ContextCompat.getColor(act, R.color.white));
            view_home_screen_two.setBackgroundColor(ContextCompat.getColor(act, R.color.white));
            view_home_screen_three.setBackgroundColor(ContextCompat.getColor(act, R.color.white));
            view_home_menu_one.setBackgroundColor(ContextCompat.getColor(act, R.color.white));
            view_home_menu_two.setBackgroundColor(ContextCompat.getColor(act, R.color.white));
            view_home_menu_three.setBackgroundColor(ContextCompat.getColor(act, R.color.white));
            view_home_menu_four.setBackgroundColor(ContextCompat.getColor(act, R.color.white));

            img_logo_one.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_logo_two.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_screen_type_logo.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_wicom_cloud_logo.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            txt_Critical_Alarm.setTextColor(ContextCompat.getColor(act, R.color.white));
//            img_home_screen_plus.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
//            img_home_screen_minus.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
//            img_home_screen_up.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
//            img_home_screen_down.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_home_screen_one.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_home_screen_two.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_home_screen_three.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
//            img_home_screen_four.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

            img_home_menu_one.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_home_menu_two.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_home_menu_three.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_home_menu_four.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_home_menu_five.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

//            txt_F01_XX.setTextColor(getResources().getColor(R.color.white));
            txt_F01_YY.setTextColor(getResources().getColor(R.color.white));
//            txt_F02_XX.setTextColor(getResources().getColor(R.color.white));
            txt_F02_YY.setTextColor(getResources().getColor(R.color.white));
//            txt_S01_Z3.setTextColor(getResources().getColor(R.color.white));
            txt_F08_XXYY.setTextColor(getResources().getColor(R.color.white));

//            txt_S01_Z3_Plus.setBackgroundResource(R.drawable.white_home_screen_textbox);
//            txt_S01_Z3_Minus.setBackgroundResource(R.drawable.white_home_screen_textbox);
//            txt_S08_Up.setBackgroundResource(R.drawable.white_home_screen_textbox);
//            txt_S08_Down.setBackgroundResource(R.drawable.white_home_screen_textbox);
//            txt_F01_XX.setBackgroundResource(R.drawable.black_home_screen_textbox);
            txt_F01_YY.setBackgroundResource(R.drawable.black_home_screen_textbox);
//            txt_F02_XX.setBackgroundResource(R.drawable.black_home_screen_textbox);
            txt_F02_YY.setBackgroundResource(R.drawable.black_home_screen_textbox);
//            txt_S01_Z3.setBackgroundResource(R.drawable.black_home_screen_textbox);
            txt_F08_XXYY.setBackgroundResource(R.drawable.black_home_screen_textbox);
            txt_timer.setTextColor(getResources().getColor(R.color.white));
            txt_version.setTextColor(getResources().getColor(R.color.white));

            img_screen_type_logo.setVisibility(View.VISIBLE);
            txt_Critical_Alarm.setVisibility(View.GONE);
            txt_Warning_Alarm.setVisibility(View.GONE);

            /*final int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                layout_menubar.setBackgroundDrawable(ContextCompat.getDrawable(act, R.drawable.dark_green_home_screen_box));
                layout_home_screen.setBackgroundDrawable(ContextCompat.getDrawable(act, R.drawable.dark_green_home_screen_box));
            } else {
                layout_menubar.setBackground(ContextCompat.getDrawable(act, R.drawable.dark_green_home_screen_box));
                layout_home_screen.setBackground(ContextCompat.getDrawable(act, R.drawable.dark_green_home_screen_box));
            }*/
            setMenubarBackground();
            setHomeScreenBackground();
        }

        layout_filter_screen.setVisibility(View.GONE);
        layout_blwrdetl_screen.setVisibility(View.GONE);
        layout_alarm_screen.setVisibility(View.GONE);
        layout_home_screen.setVisibility(View.VISIBLE);

        feedbackArrayList = responseHandler.getLastFeedbackData();
        Log.e(TAG, "Here Checking the feedback arrary list..");
        if (feedbackArrayList.size() > 0) {
            if (responseHandler.hexToBinary(feedbackArrayList.get(0).getF12()).charAt(8) == '1') {
                // for y3 = 1 only change color for filter icon
                // isRedYellowColorHomeScreen = true;
                //Log.e(TAG,"Filter is true");
                //Log.e(TAG,"Value of PrefilterresetYesclicked is : " + isPreFilterResetYesButtonClicked);
                isPreFilterResetClicked = true;//this requires to be true so that we get the yellow and black icon for filter button
                //on all blower, alarm, filter screen.. see teh code for all three click event..

                //this is disable on 15.22 and enable on 16.160
                // so when i was not making this flag false, i was not getting the yellow icon.
                //i verified this on 15.22. this is because once we have yellow and we reset by click on
                //yes this becomes true all the time. and after that we never make it false.
                //as we have just below the code which checks this flag and if it's yes then it will
                //make button icon normal. but once i reset the alarm and after that it is true all the time
                //we nevermake isPreFilterResetYesButtonClicked this false when there is no alarm.
                //so if we reset the prefilter alarm and come here and we see there is no alarm
                //then we should make isPreFilterResetYesButtonClicked this false.
                //technically we do not need to make this false here but in else loop we should
                //make this flag isPreFilterResetYesButtonClicked false.

                //isPreFilterResetYesButtonClicked = false;
                //Log.e(TAG,"Here i made isPreFilterResetYesButtonClicked as false.");

                //layout_filter_menu.setBackgroundColor(ContextCompat.getColor(act, R.color.home_yellow));
                //img_home_menu_one.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);

                if (isPreFilterResetYesButtonClicked)
                {
                    isPreFilterResetClicked = false;
                    isPreFilterResetYesButtonClicked = false;
                    layout_filter_menu.setBackgroundColor(0x00000000);
                    img_home_menu_one.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                }
                else
                {
                    layout_filter_menu.setBackgroundColor(ContextCompat.getColor(act, R.color.home_yellow));
                    img_home_menu_one.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                }
            } else {
                isPreFilterResetClicked = false;
                isPreFilterResetYesButtonClicked = false;
                layout_filter_menu.setBackgroundColor(0x00000000);
                img_home_menu_one.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            }

//            if (isPreFilterResetYesButtonClicked) {
//                layout_filter_menu.setBackgroundColor(0x00000000);
//                img_home_menu_one.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
//            }
        }
        NightModeView(nightModeStatus);

        if (nightModeStatus)
        {
            //for prefilter icon flashing
            if (responseHandler.hexToBinary(feedbackArrayList.get(0).getF12()).charAt(8) == '1')
            {
                if (Flashingcolorvalue.equals("red"))
                {
                    layout_filter_menu.setBackgroundColor(ContextCompat.getColor(act, R.color.home_dark_red));
                    //check here tomorrow
                    img_home_menu_one.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                    //Flashingcolorvalue = "black";
                }
                else
                {
                    layout_filter_menu.setBackgroundColor(ContextCompat.getColor(act, R.color.black));
                    //check here tomorrow
                    img_home_menu_one.setColorFilter(ContextCompat.getColor(act, R.color.home_red), android.graphics.PorterDuff.Mode.MULTIPLY);

                    //Flashingcolorvalue = "red";
                }
            }
            else
            {
                layout_filter_menu.setBackgroundColor(0x00000000);
            }
            if (color.equalsIgnoreCase("red") || color.equalsIgnoreCase("yellow"))
            {
                if (Flashingcolorvalue.equals("red"))
                {
                    layout_alert_menu.setBackgroundColor(ContextCompat.getColor(act, R.color.home_dark_red));
                    img_home_menu_three.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                    //Flashingcolorvalue = "black";
                }
                else
                {
                    layout_alert_menu.setBackgroundColor(ContextCompat.getColor(act, R.color.black));
                    img_home_menu_three.setColorFilter(ContextCompat.getColor(act, R.color.home_red), android.graphics.PorterDuff.Mode.MULTIPLY);
                    //Flashingcolorvalue = "red";
                }
            }
            else
            {
                layout_alert_menu.setBackgroundColor(0x00000000);
            }
            if (Flashingcolorvalue.equals("red"))
            {
                Flashingcolorvalue = "black";
            }
            else
            {
                Flashingcolorvalue = "red";
            }

        }
        else
        {
            //layout_filter_menu.setBackgroundColor(0x00000000);
            layout_alert_menu.setBackgroundColor(0x00000000);
        }

    }

    // MultipleSelections ArrayList
    public void getMultipleSelections() {
        if (supplyBlowerSetting_layout.getVisibility() == View.VISIBLE) {
            multipleSelections.clear();

            MultipleSelection multipleSelection = new MultipleSelection();
            multipleSelection.setId("1");
            multipleSelection.setName("ENABLE");
            multipleSelection.setSelected(false);
            multipleSelections.add(multipleSelection);

            MultipleSelection multipleSelection_1 = new MultipleSelection();
            multipleSelection_1.setId("0");
            multipleSelection_1.setName("DISABLE");
            multipleSelection_1.setSelected(false);
            multipleSelections.add(multipleSelection_1);
        } else if (exhaustBlowerSetting_layout.getVisibility() == View.VISIBLE) {
            multipleSelections.clear();

            MultipleSelection multipleSelection = new MultipleSelection();
            multipleSelection.setId("1");
            multipleSelection.setName("ENABLE");
            multipleSelection.setSelected(false);
            multipleSelections.add(multipleSelection);

            MultipleSelection multipleSelection_1 = new MultipleSelection();
            multipleSelection_1.setId("0");
            multipleSelection_1.setName("DISABLE");
            multipleSelection_1.setSelected(false);
            multipleSelections.add(multipleSelection_1);
        }
    }

    // MultipleSelections ArrayList With Type
    public void getMultipleSelections(String type) {

        if (type.equals("txt_S01_Y0")) {
            multipleSelections0.clear();

            MultipleSelection multipleSelection = new MultipleSelection();
            multipleSelection.setId("0");
            multipleSelection.setName("F");
            multipleSelection.setSelected(false);
            multipleSelections0.add(multipleSelection);

            MultipleSelection multipleSelection_1 = new MultipleSelection();
            multipleSelection_1.setId("1");
            multipleSelection_1.setName("C");
            multipleSelection_1.setSelected(false);
            multipleSelections0.add(multipleSelection_1);
        } else if (type.equals("txt_S01_Y1")) {
            multipleSelections1.clear();

            MultipleSelection multipleSelection = new MultipleSelection();
            multipleSelection.setId("0");
            multipleSelection.setName("WC");
            multipleSelection.setSelected(false);
            multipleSelections1.add(multipleSelection);

            MultipleSelection multipleSelection_1 = new MultipleSelection();
            multipleSelection_1.setId("1");
            multipleSelection_1.setName("Pa");
            multipleSelection_1.setSelected(false);
            multipleSelections1.add(multipleSelection_1);
        } else if (type.equals("txt_S01_Y2")) {
            multipleSelections2.clear();

            MultipleSelection multipleSelection = new MultipleSelection();
            multipleSelection.setId("0");
            multipleSelection.setName("CFM");
            multipleSelection.setSelected(false);
            multipleSelections2.add(multipleSelection);

            MultipleSelection multipleSelection_1 = new MultipleSelection();
            multipleSelection_1.setId("1");
            multipleSelection_1.setName("CMH");
            multipleSelection_1.setSelected(false);
            multipleSelections2.add(multipleSelection_1);
        } else if (type.equals("txt_S01_X1_X0")) {
            multipleSelections4.clear();

            MultipleSelection multipleSelection = new MultipleSelection();
            multipleSelection.setId("00");
            multipleSelection.setName("None");
            multipleSelection.setSelected(false);
            multipleSelections4.add(multipleSelection);

            MultipleSelection multipleSelection_1 = new MultipleSelection();
            multipleSelection_1.setId("01");
            multipleSelection_1.setName("WiFi");
            multipleSelection_1.setSelected(false);
            multipleSelections4.add(multipleSelection_1);

            MultipleSelection multipleSelection_2 = new MultipleSelection();
            multipleSelection_2.setId("10");
            multipleSelection_2.setName("Bluetooth");
            multipleSelection_2.setSelected(false);
            multipleSelections4.add(multipleSelection_2);

            MultipleSelection multipleSelection_3 = new MultipleSelection();
            multipleSelection_3.setId("11");
            multipleSelection_3.setName("Roving");
            multipleSelection_3.setSelected(false);
            multipleSelections4.add(multipleSelection_3);
        } else if (type.equals("communicationSetting_wifiData_layout")) {
            // new
            multipleSelections7.clear();

            MultipleSelection multipleSelection = new MultipleSelection();
            multipleSelection.setId("0");
            multipleSelection.setName("NO ENCRYPTION");
            multipleSelection.setSelected(false);
            multipleSelections7.add(multipleSelection);

            MultipleSelection multipleSelection_1 = new MultipleSelection();
            multipleSelection_1.setId("1");
            multipleSelection_1.setName("WEP64");
            multipleSelection_1.setSelected(false);
            multipleSelections7.add(multipleSelection_1);

            MultipleSelection multipleSelection_2 = new MultipleSelection();
            multipleSelection_2.setId("2");
            multipleSelection_2.setName("WEP128");
            multipleSelection_2.setSelected(false);
            multipleSelections7.add(multipleSelection_2);

            MultipleSelection multipleSelection_3 = new MultipleSelection();
            multipleSelection_3.setId("3");
            multipleSelection_3.setName("WPA");
            multipleSelection_3.setSelected(false);
            multipleSelections7.add(multipleSelection_3);

            MultipleSelection multipleSelection_4 = new MultipleSelection();
            multipleSelection_4.setId("4");
            multipleSelection_4.setName("WPA2");
            multipleSelection_4.setSelected(false);
            multipleSelections7.add(multipleSelection_4);

            multipleSelections8.clear();

            MultipleSelection multipleSelection8_0 = new MultipleSelection();
            multipleSelection8_0.setId("0");
            multipleSelection8_0.setName("STATIC");
            multipleSelection8_0.setSelected(false);
            multipleSelections8.add(multipleSelection8_0);

            MultipleSelection multipleSelection8_1 = new MultipleSelection();
            multipleSelection8_1.setId("1");
            multipleSelection8_1.setName("DYNAMIC");
            multipleSelection8_1.setSelected(false);
            multipleSelections8.add(multipleSelection8_1);

            // Security Code array
            multipleSelections9.clear();

            MultipleSelection multipleSelection9_0 = new MultipleSelection();
            multipleSelection9_0.setId("0");
            multipleSelection9_0.setName("USA");
            multipleSelection9_0.setSelected(false);
            multipleSelections9.add(multipleSelection9_0);

            MultipleSelection multipleSelection9_1 = new MultipleSelection();
            multipleSelection9_1.setId("1");
            multipleSelection9_1.setName("CANADA");
            multipleSelection9_1.setSelected(false);
            multipleSelections9.add(multipleSelection9_1);

            MultipleSelection multipleSelection9_2 = new MultipleSelection();
            multipleSelection9_2.setId("2");
            multipleSelection9_2.setName("EUROPE");
            multipleSelection9_2.setSelected(false);
            multipleSelections9.add(multipleSelection9_2);

            MultipleSelection multipleSelection9_3 = new MultipleSelection();
            multipleSelection9_3.setId("3");
            multipleSelection9_3.setName("SPAIN");
            multipleSelection9_3.setSelected(false);
            multipleSelections9.add(multipleSelection9_3);

            MultipleSelection multipleSelection9_4 = new MultipleSelection();
            multipleSelection9_4.setId("4");
            multipleSelection9_4.setName("FRANCE");
            multipleSelection9_4.setSelected(false);
            multipleSelections9.add(multipleSelection9_4);

            MultipleSelection multipleSelection9_5 = new MultipleSelection();
            multipleSelection9_5.setId("5");
            multipleSelection9_5.setName("JAPAN");
            multipleSelection9_5.setSelected(false);
            multipleSelections9.add(multipleSelection9_5);
        } else if (type.equalsIgnoreCase("set_array_minutes_for_f_Command")) {
            multipleSelections10.clear();
            MultipleSelection multipleSelection10_0 = new MultipleSelection();
            multipleSelection10_0.setId("0");
            multipleSelection10_0.setName("5");
            multipleSelection10_0.setSelected(true);
            multipleSelections10.add(multipleSelection10_0);

            MultipleSelection multipleSelection10_1 = new MultipleSelection();
            multipleSelection10_1.setId("1");
            multipleSelection10_1.setName("10");
            multipleSelection10_1.setSelected(false);
            multipleSelections10.add(multipleSelection10_1);

            MultipleSelection multipleSelection10_2 = new MultipleSelection();
            multipleSelection10_2.setId("2");
            multipleSelection10_2.setName("30");
            multipleSelection10_2.setSelected(false);
            multipleSelections10.add(multipleSelection10_2);

            MultipleSelection multipleSelection10_3 = new MultipleSelection();
            multipleSelection10_3.setId("3");
            multipleSelection10_3.setName("60");
            multipleSelection10_3.setSelected(false);
            multipleSelections10.add(multipleSelection10_3);
        }
    }

    // FilterActivity AlertDailogBox
    public void ShowPreFilterResetDialog() {

        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStopService);

        ResetCounter(1);

        alertview_pre_filter_reset = new Dialog(act);

        Window window = alertview_pre_filter_reset.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.TOP;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        alertview_pre_filter_reset.setCancelable(false);
        alertview_pre_filter_reset.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertview_pre_filter_reset.setContentView(R.layout.alertview_pre_filter_reset_layout); // PRE-FILTER RESET
        alertview_pre_filter_reset.show();

        txt_F10_XXYY = alertview_pre_filter_reset.findViewById(R.id.txt_F10_XXYY);
        txt_Yes_Pre_Filter_Reset = alertview_pre_filter_reset.findViewById(R.id.txt_Yes_Pre_Filter_Reset);
        txt_No_Pre_Filter_Reset = alertview_pre_filter_reset.findViewById(R.id.txt_No_Pre_Filter_Reset);
        progress_Pre_Filter_Reset = alertview_pre_filter_reset.findViewById(R.id.ProgressBar);

        double F10 = Double.valueOf(responseHandler.hexToString(feedbackArrayList.get(0).getF10())) / 10;

        txt_F10_XXYY.setText(String.valueOf(F10));

        txt_Yes_Pre_Filter_Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress_Pre_Filter_Reset.setVisibility(View.VISIBLE);
                isPreFilterResetYesButtonClicked = true;
                if (setpointArrayList.size() != 0) {
                    String command = "S12=" + setpointArrayList.get(0).getS12().toUpperCase();

                    Utility.Log(TAG, "Sending S12 ==> " + command);

                    prefManager.setSendCommandS(command);

                    final Runnable r = new Runnable() {
                        public void run() {
                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommand);

                            ResetCounter(1);

                            progress_Pre_Filter_Reset.setVisibility(View.GONE);

                            alertview_pre_filter_reset.dismiss();

                        }
                    };
                    mHandler.postDelayed(r, 4000);
                }
            }
        });

        txt_No_Pre_Filter_Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //progress_Pre_Filter_Reset.setVisibility(View.VISIBLE);
                isPreFilterResetYesButtonClicked = false;
                ResetCounter(1);
                alertview_pre_filter_reset.dismiss();

                final Runnable r = new Runnable() {
                    public void run() {
                        Log.e(TAG,"Starting service after 4 seconds when no clicked on prefilter reset.");
                        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
                    }
                };
                mHandler.postDelayed(r, 4000);
            }
        });

    }

    // CommunicationActivity AlertDailogBox
    public void ShowWifiCommunicationDialog(final String title, String btn1, String btn2) {

        ResetCounter(1);

        alertview_wifiCommunication = new Dialog(act);

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);

        alertview_wifiCommunication.getWindow().setLayout(width, height);

        alertview_wifiCommunication.setCancelable(true);
        alertview_wifiCommunication.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        alertview_wifiCommunication.setContentView(R.layout.alertview_communication_layout); // Wifi Communication

        alertview_wifiCommunication.show();

        txt_dailogMassage = alertview_wifiCommunication.findViewById(R.id.txt_dailogMassage);
        btn_dailogTitle1 = alertview_wifiCommunication.findViewById(R.id.btn_dailogTitle1);
        btn_dailogTitle2 = alertview_wifiCommunication.findViewById(R.id.btn_dailogTitle2);
        layout_dailog_S01_X1_X0 = alertview_wifiCommunication.findViewById(R.id.layout_dailog_S01_X1_X0);
        layout_dailog_timer = alertview_wifiCommunication.findViewById(R.id.layout_dailog_timer);
        linear_layout_dialog_buttons = alertview_wifiCommunication.findViewById(R.id.linear_layout_dialog_buttons);
        txt_dailog_timmer = alertview_wifiCommunication.findViewById(R.id.txt_dailog_timmer);
        txt_dailog_S01_X1_X0 = alertview_wifiCommunication.findViewById(R.id.txt_dailog_S01_X1_X0);

        txt_dailogMassage.setText(title);
        btn_dailogTitle1.setText(btn1);
        btn_dailogTitle2.setText(btn2);

        if (title.equals("Continue With Application")) {
            layout_dailog_S01_X1_X0.setVisibility(View.GONE);
        } else if (title.equals("Continue With Android Blower Application")) {
            layout_dailog_S01_X1_X0.setVisibility(View.GONE);
        } else if (title.equals("Are you sure you want to change to WIFI?")) {
            layout_dailog_S01_X1_X0.setVisibility(View.GONE);
        } else if (title.equals("Are you sure you want to change to Roving?")) {
            layout_dailog_S01_X1_X0.setVisibility(View.GONE);
        } else if (title.equals("Please change the physical switch, antenna and reboot the blower and comeback.")) {
            layout_dailog_S01_X1_X0.setVisibility(View.GONE);
            linear_layout_dialog_buttons.setVisibility(View.GONE);
            layout_dailog_timer.setVisibility(View.VISIBLE);
            alertview_wifiCommunication.setCancelable(false);
            CallTimer();
        } else if (title.equals("Have you rebooted the blower?")) {
            linear_layout_dialog_buttons.setVisibility(View.VISIBLE);
            layout_dailog_timer.setVisibility(View.GONE);
            layout_dailog_S01_X1_X0.setVisibility(View.GONE);
        } else {
            layout_dailog_S01_X1_X0.setVisibility(View.VISIBLE);

            getMultipleSelections("txt_S01_X1_X0");
            if (setpointArrayList.size() != 0) {
                for (int i = 0; i < multipleSelections4.size(); i++) {
//                    String S01_X1_X0 = String.valueOf(responseHandler.hexToBinary(setpointArrayList.get(0).getS01()).charAt(6)) + responseHandler.hexToBinary(setpointArrayList.get(0).getS01()).charAt(7);
                    String S01_X1_X0 = "01";
                    if (multipleSelections4.get(i).getId().equals(S01_X1_X0)) {
                        txt_dailog_S01_X1_X0.setText(multipleSelections4.get(i).getName());
                        multipleSelections4.get(i).setSelected(true);
                    }
                }
            }
        }

        btn_dailogTitle1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn_dailogTitle1.getText().equals("Wivarium")) {
                    ResetCounter(1);
                    alertview_wifiCommunication.dismiss();
                    ShowWifiCommunicationDialog("Continue With Wivarium Application", "Roving", "WIFI");
                } else if (btn_dailogTitle1.getText().equals("Roving")) {
                    if (txt_dailog_S01_X1_X0.getText().equals("WiFi")) {
                        alertview_wifiCommunication.dismiss();
                        ShowWifiCommunicationDialog("Are you sure you want to change to Roving?", "No", "Yes");
                    } else {
                        mRelativeProgressBarLayoutSetting.setVisibility(View.GONE);
                        String status = "";

                        getMultipleSelections("txt_S01_X1_X0");

                        for (int i = 0; i < multipleSelections4.size(); i++) {
                            String S01_X1_X0 = String.valueOf(responseHandler.hexToBinary(setpointArrayList.get(0).getS01()).charAt(6)) + responseHandler.hexToBinary(setpointArrayList.get(0).getS01()).charAt(7);
                            if (multipleSelections4.get(i).getId().equals(S01_X1_X0)) {
                                status = multipleSelections4.get(i).getName();
                                multipleSelections4.get(i).setSelected(true);
                            }
                        }

                        if (!status.equals("Roving")) {

                            String bin = responseHandler.hexToBinary(setpointArrayList.get(0).getS01());
                            Utility.Log(TAG, "hexToBinary => " + bin);
//                        String firstPart = bin.substring(0, 6);
//                        String secondPart = bin.substring(8, 16);
//                        bin = firstPart.concat("11").concat(secondPart);
//                        Utility.Log(TAG, "hexToBinary => " + bin);

                            String command = "";

                            for (int i = 0; i < multipleSelections4.size(); i++) {
                                if (multipleSelections4.get(i).isSelected()) {
//                                String S01 = responseHandler.binaryToHex(bin.substring(0, 5).concat(multipleSelections4.get(i).getId()).concat(bin.substring(7, 15))).toUpperCase();
                                    String S01 = responseHandler.binaryToHex(bin.substring(0, 6).concat("11").concat(bin.substring(8, 16))).toUpperCase();

                                    if (S01.length() == 1)
                                        S01 = "000" + S01;
                                    else if (S01.length() == 2)
                                        S01 = "00" + S01;
                                    else if (S01.length() == 3)
                                        S01 = "0" + S01;


                                    command = "S01=" + S01;

                                    Utility.Log(TAG, "Sending S01 ==> " + command);

                                    prefManager.setSendCommandS(command);

                                }
                            }
                        }

                        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);

                        ResetCounter(1);
                        alertview_wifiCommunication.dismiss();

                        bluetooth_layout.setVisibility(View.GONE);
                        main_layout.setVisibility(View.GONE);
                        setting_layout.setVisibility(View.GONE);
                        report_layout.setVisibility(View.GONE);
                        sub_setting_layout.setVisibility(View.VISIBLE);

                        supplyBlowerSetting_layout.setVisibility(View.GONE);
                        exhaustBlowerSetting_layout.setVisibility(View.GONE);
                        filterSetting_layout.setVisibility(View.GONE);
                        communicationSetting_layout.setVisibility(View.VISIBLE);
                        communicationSetting_wifiData_layout.setVisibility(View.GONE);
                        txt_ApplyChange_SettingScreen.setVisibility(View.GONE);
                        unitSetting_layout.setVisibility(View.GONE);
                        deconSetting_layout.setVisibility(View.GONE);
                        passwordSetting_layout.setVisibility(View.GONE);
                        nightModeSetting_layout.setVisibility(View.GONE);

                        if (wifiArrayList.get(0).getW12().equalsIgnoreCase("") || wifiArrayList.get(0).getW12() == null) {
                            txt_WIFI_id.setText("Bluetooth MAC : 0005550CA04F");
                        } else {
                            txt_WIFI_id.setText("Bluetooth MAC : " + wifiArrayList.get(0).getW12());
                        }
                        txt_S01_X1_X0.setText("Roving");
                    }


                } else if (btn_dailogTitle1.getText().equals("Bluetooth\n(Android)")) {
                    ResetCounter(1);
                    alertview_wifiCommunication.dismiss();

                    final Intent intent = new Intent(Intent.ACTION_MAIN, null);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.bluetooth.BluetoothSettings");
                    intent.setComponent(cn);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else if (btn_dailogTitle1.getText().equals("No")) {
                    alertview_wifiCommunication.dismiss();
                }
            }
        });

        btn_dailogTitle2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {
                if (btn_dailogTitle2.getText().equals("Android Blower")) {
                    ResetCounter(1);
                    alertview_wifiCommunication.dismiss();
                    ShowWifiCommunicationDialog("Continue With Android Blower Application", "Bluetooth\n(Android)", "WIFI\n(Android)");
                } else if (btn_dailogTitle2.getText().equals("WIFI")) {

                    if (txt_dailog_S01_X1_X0.getText().equals("Roving")) {
                        alertview_wifiCommunication.dismiss();
                        ShowWifiCommunicationDialog("Are you sure you want to change to WIFI?", "No", "Yes");
                    } else {
                        mRelativeProgressBarLayoutSetting.setVisibility(View.GONE);
                        String status = "";

                        getMultipleSelections("txt_S01_X1_X0");

                        for (int i = 0; i < multipleSelections4.size(); i++) {
                            String S01_X1_X0 = String.valueOf(responseHandler.hexToBinary(setpointArrayList.get(0).getS01()).charAt(6)) + responseHandler.hexToBinary(setpointArrayList.get(0).getS01()).charAt(7);
                            if (multipleSelections4.get(i).getId().equals(S01_X1_X0)) {
                                status = multipleSelections4.get(i).getName();
                                multipleSelections4.get(i).setSelected(true);
                            }
                        }

                        if (!status.equals("WiFi")) {

                            String bin = responseHandler.hexToBinary(setpointArrayList.get(0).getS01());
                            Utility.Log(TAG, "hexToBinary => " + bin);

                            String command = "";

                            for (int i = 0; i < multipleSelections4.size(); i++) {
                                if (multipleSelections4.get(i).isSelected()) {
//                                    String S01 = responseHandler.binaryToHex(bin.substring(0, 5).concat(multipleSelections4.get(i).getId()).concat(bin.substring(7, 15))).toUpperCase();
                                    String S01 = responseHandler.binaryToHex(bin.substring(0, 6).concat("01").concat(bin.substring(8, 16))).toUpperCase();

                                    if (S01.length() == 1)
                                        S01 = "000" + S01;
                                    else if (S01.length() == 2)
                                        S01 = "00" + S01;
                                    else if (S01.length() == 3)
                                        S01 = "0" + S01;

                                    command = "S01=" + S01;

                                    Utility.Log(TAG, "Sending S01 ==> " + command);

                                    prefManager.setSendCommandS(command);

                                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);
                                }
                            }
                        }

                        ResetCounter(1);
                        alertview_wifiCommunication.dismiss();

                        bluetooth_layout.setVisibility(View.GONE);
                        main_layout.setVisibility(View.GONE);
                        setting_layout.setVisibility(View.GONE);
                        report_layout.setVisibility(View.GONE);
                        sub_setting_layout.setVisibility(View.VISIBLE);

                        supplyBlowerSetting_layout.setVisibility(View.GONE);
                        exhaustBlowerSetting_layout.setVisibility(View.GONE);
                        filterSetting_layout.setVisibility(View.GONE);
                        communicationSetting_layout.setVisibility(View.VISIBLE);
                        communicationSetting_wifiData_layout.setVisibility(View.VISIBLE);
                        txt_ApplyChange_SettingScreen.setVisibility(View.VISIBLE);
                        unitSetting_layout.setVisibility(View.GONE);
                        deconSetting_layout.setVisibility(View.GONE);
                        passwordSetting_layout.setVisibility(View.GONE);
                        nightModeSetting_layout.setVisibility(View.GONE);

                        txt_IP_ADDRESS.setEnabled(false);
                        txt_SUBNET.setEnabled(false);
                        txt_DEFAULT_GATEWAY.setEnabled(false);
                        txt_DNS_SERVER.setEnabled(false);

//                    // W Command Sending.....
//                    CallReadWriteFuncation("W", 0);

                        txt_WIFI_id.setText("WIFI ADDRESS : 0.0.0.0");
                        txt_S01_X1_X0.setText("WIFI");

                        // ReadAllData
                        setAnalysisData();
                    }


                } else if (btn_dailogTitle2.getText().equals("Yes")) {
                    if (title.equals("Have you rebooted the blower?")) {
                        isWifiYesButtonClicked = true;
                        mRelativeProgressBarLayoutSetting.setVisibility(View.VISIBLE);
                        alertview_wifiCommunication.dismiss();
                        String status = "";
                        getMultipleSelections("txt_S01_X1_X0");

                        for (int i = 0; i < multipleSelections4.size(); i++) {
                            String S01_X1_X0 = String.valueOf(responseHandler.hexToBinary(setpointArrayList.get(0).getS01()).charAt(6)) + responseHandler.hexToBinary(setpointArrayList.get(0).getS01()).charAt(7);
//                            String S01_X1_X0 = "01";
                            if (multipleSelections4.get(i).getId().equals(S01_X1_X0)) {
                                status = multipleSelections4.get(i).getName();
                                multipleSelections4.get(i).setSelected(true);
                            }
                        }

                        String bin = responseHandler.hexToBinary(setpointArrayList.get(0).getS01());
                        Utility.Log(TAG, "WIFI hexToBinary => " + bin);
                        if (!status.equals("WiFi")) {

                            String command = "";

                            for (int i = 0; i < multipleSelections4.size(); i++) {
                                if (multipleSelections4.get(i).isSelected()) {
//                                    String S01 = responseHandler.binaryToHex(bin.substring(0, 5).concat(multipleSelections4.get(i).getId()).concat(bin.substring(7, 15))).toUpperCase();
                                    String S01 = responseHandler.binaryToHex(bin.substring(0, 6).concat("01").concat(bin.substring(8, 16))).toUpperCase();

                                    if (S01.length() == 1)
                                        S01 = "000" + S01;
                                    else if (S01.length() == 2)
                                        S01 = "00" + S01;
                                    else if (S01.length() == 3)
                                        S01 = "0" + S01;

                                    command = "S01=" + S01;

                                    Utility.Log(TAG, "WIFI Sending S01 ==> " + command);

//                                    CallReadWriteFuncation(command, 4);
                                    prefManager.setSendCommandS(command);

                                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);
                                }
                            }
                        } else {
                            String command = "";

                            for (int i = 0; i < multipleSelections4.size(); i++) {
                                if (multipleSelections4.get(i).isSelected()) {
//                                String S01 = responseHandler.binaryToHex(bin.substring(0, 5).concat(multipleSelections4.get(i).getId()).concat(bin.substring(7, 15))).toUpperCase();
                                    String S01 = responseHandler.binaryToHex(bin.substring(0, 6).concat("11").concat(bin.substring(8, 16))).toUpperCase();

                                    if (S01.length() == 1)
                                        S01 = "000" + S01;
                                    else if (S01.length() == 2)
                                        S01 = "00" + S01;
                                    else if (S01.length() == 3)
                                        S01 = "0" + S01;


                                    command = "S01=" + S01;

                                    Utility.Log(TAG, "Sending S01 ==> " + command);

                                    prefManager.setSendCommandS(command);

                                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);

                                }
                            }
                        }

                    } else {
                        isWifiYesButtonClicked = false;
                        alertview_wifiCommunication.dismiss();
                        ShowWifiCommunicationDialog("Please change the physical switch, antenna and reboot the blower and comeback.", "No", "Yes");
                    }


                } else if (btn_dailogTitle2.getText().equals("WIFI\n(Android)")) {
                    ResetCounter(1);
                    alertview_wifiCommunication.dismiss();

                    final Intent intent = new Intent(Intent.ACTION_MAIN, null);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
                    intent.setComponent(cn);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

    }

    // 15 Second Timer Showing when user click on Communication on Setting Screen.
    //then click on wifi or roving button Dialog is for Yes and No
    // if User click On Yes Button then then alert message and time will be display on dialog.
    public void CallTimer() {
        if (countDownTimer != null)
            countDownTimer.cancel();

        countDownTimer = new CountDownTimer(16000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                txt_dailog_timmer.setText(String.format("%02d",
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            @Override
            public void onFinish() {
                alertview_wifiCommunication.dismiss();
                ShowWifiCommunicationDialog("Have you rebooted the blower?", "No", "Yes");
            }
        }.start();
    }

    // HomeActivity AlertDailogBox
    public void ShowSelectionDialog() {

        ResetCounter(1);

        alertview_selection = new Dialog(act);

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);

        alertview_selection.getWindow().setLayout(width, height);

        alertview_selection.setCancelable(false);
        alertview_selection.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        alertview_selection.setContentView(R.layout.alertview_seekbar_selection_layout); // Seekbar Selection

        alertview_selection.show();

        txt_dailogTitle = alertview_selection.findViewById(R.id.txt_dailogTitle);

        txt_dailogTitle.setText("Modified ACH");

        txt_MinSeekbar_Progress = alertview_selection.findViewById(R.id.txt_MinSeekbar_Progress);
        txt_MaxSeekbar_Progress = alertview_selection.findViewById(R.id.txt_MaxSeekbar_Progress);
        seekbar_Progress_Selection = alertview_selection.findViewById(R.id.seekbar_Progress_Selection);
        txt_SelectedSeekbar_Progress = alertview_selection.findViewById(R.id.txt_SelectedSeekbar_Progress);
        txt_Minus_Progress = alertview_selection.findViewById(R.id.txt_Minus_Progress);
        txt_Plus_Progress = alertview_selection.findViewById(R.id.txt_Plus_Progress);

        txt_MinSeekbar_Progress.setText("20");
        txt_MaxSeekbar_Progress.setText("100");

        seekbar_Progress_Selection.setProgress(Integer.parseInt(txt_F08_XXYY.getText().toString()) - 20);
        txt_SelectedSeekbar_Progress.setText(txt_F08_XXYY.getText().toString());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seekbar_Progress_Selection.setMin(0);
        }
        seekbar_Progress_Selection.setMax(80);

        seekbar_Progress_Selection.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                seekbar_Progress_Selection.setProgress(seekParams.progress);
                txt_SelectedSeekbar_Progress.setText(String.valueOf(seekParams.progress + 20));
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

            }
        });

        txt_Minus_Progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetCounter(1);
                if (seekbar_Progress_Selection.getProgress() > 0) {
                    int setProgress = seekbar_Progress_Selection.getProgress() - 1;
                    seekbar_Progress_Selection.setProgress(setProgress);
                    txt_SelectedSeekbar_Progress.setText(String.valueOf(setProgress + 20));
                }
            }
        });

        txt_Plus_Progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetCounter(1);
                if (seekbar_Progress_Selection.getProgress() < 80) {
                    int setProgress = seekbar_Progress_Selection.getProgress() + 1;
                    seekbar_Progress_Selection.setProgress(setProgress);
                    txt_SelectedSeekbar_Progress.setText(String.valueOf(setProgress + 20));
                }
            }
        });

        btn_Cancel_Selection = alertview_selection.findViewById(R.id.btn_Cancel_Selection);
        btn_Save_Selection = alertview_selection.findViewById(R.id.btn_Save_Selection);

        btn_Cancel_Selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
                ResetCounter(0);
                alertview_selection.dismiss();
            }
        });

        btn_Save_Selection.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {
                ResetCounter(0);
                // Seekbar Selection
                String S08_XXXX = txt_SelectedSeekbar_Progress.getText().toString();
                String S07_YY = responseHandler.getHexString(setpointArrayList.get(0).getS07(), false);

                Integer s07yy = Integer.parseInt(S07_YY, 16);
                Integer s08xxxx = Integer.parseInt(S08_XXXX);
                Integer s08new = 0;

                if ((s08xxxx - s07yy) == 0) {
                    s08new = 0;
                } else if ((s08xxxx - s07yy) > 0) {
                    //send positive offset
                    s08new = s08xxxx - s07yy;
                } else {
                    //send negative offset
                    s08new = s08xxxx - s07yy + 256;//42-60+256= 238
                }

                String s08New = responseHandler.stringToHex(String.valueOf(s08new), false);

                Utility.Log(TAG, "S08 => " + s08New);

                String command = "S08=" + s08New.toUpperCase();

                Utility.Log(TAG, "Sending S08_XX ==> " + command);

                prefManager.setSendCommandS(command);

                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommand);

                alertview_selection.dismiss();

            }
        });

    }

    // Sub SettingsActivity AlertDailogBox
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public void ShowMultipleSelectionDialog(final String type, String Command) {
        alertview_selection = new Dialog(act);

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);

        alertview_selection.getWindow().setLayout(width, height);

        alertview_selection.setCancelable(false);
        alertview_selection.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        if (supplyBlowerSetting_layout.getVisibility() == View.VISIBLE) {
            SupplyBlowerAlertDialogBox(alertview_selection, type);
        } else if (exhaustBlowerSetting_layout.getVisibility() == View.VISIBLE) {
            ExhaustBlowerAlertDialogBox(alertview_selection, type);
        }
        else if (filterSetting_layout.getVisibility() == View.VISIBLE) {
            FilterSettingAlertDialogBox(alertview_selection, type);
        }
        else if (unitSetting_layout.getVisibility() == View.VISIBLE) {
            UnitSettingAlertDialogBox(alertview_selection, type);
        }
        else if (deconSetting_layout.getVisibility() == View.VISIBLE) {
            DeconSettingAlertDialogBox(alertview_selection, type);
        }
        else if (communicationSetting_wifiData_layout.getVisibility() == View.VISIBLE) {
            CommunicationSettingAlertDialogBoxSelection(alertview_selection, type, Command);
        }

    }


    // Sub SettingsActivity AlertDailogBox
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public void ShowMultipleSelectionDialogFor_F_Command_Minute(final String type) {
        alertview_selection = new Dialog(act);

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);

        alertview_selection.getWindow().setLayout(width, height);

        alertview_selection.setCancelable(false);
        alertview_selection.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        if (report_layout.getVisibility() == View.VISIBLE) {
            Save_F_CommandAlertDialogBoxSelection(alertview_selection, type);
        }

    }

    // Save F command 5 every minites Dialog
    public void Save_F_CommandAlertDialogBoxSelection(final Dialog alertview_selection, final String type) {
        alertview_selection.setContentView(R.layout.alertview_multiple_selection_layout); // Multiple Selection
        alertview_selection.show();

        txt_dailogTitle = alertview_selection.findViewById(R.id.txt_dailogTitle);

        btn_Cancel_Selection = alertview_selection.findViewById(R.id.btn_Cancel_Selection);
        btn_Save_Selection = alertview_selection.findViewById(R.id.btn_Save_Selection);

        txt_dailogTitle.setText("Save F command @ every");

        // set up the RecyclerView
        rv_MultipleSelection = alertview_selection.findViewById(R.id.rv_MultipleSelection);
        rv_MultipleSelection.setLayoutManager(new GridLayoutManager(this, 2));

        multipleSelectionForMinutesAdapter = new MultipleSelectionForMinutesAdapter(act, multipleSelections10);
        rv_MultipleSelection.setAdapter(multipleSelectionForMinutesAdapter);

        for (int i = 0; i < multipleSelections10.size(); i++) {
            if (multipleSelections10.get(i).getName().equals(prefManager.getMinuteForCommandF())) {
                multipleSelections10.get(i).setSelected(true);
            } else {
                multipleSelections10.get(i).setSelected(false);
            }
            multipleSelectionForMinutesAdapter.notifyDataSetChanged();
        }


        btn_Cancel_Selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetCounter(1);
                alertview_selection.dismiss();
            }
        });

        btn_Save_Selection.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {
                // Multiple Selection
                String selectedMinutes = "";
                int minutes = 0;

                for (int i = 0; i < multipleSelections10.size(); i++) {
                    if (multipleSelections10.get(i).isSelected()) {
                        selectedMinutes = multipleSelections10.get(i).getName();
                        minutes = Integer.parseInt(multipleSelections10.get(i).getName());
                        Utility.Log(TAG, "Sending ==> " + selectedMinutes);

                        prefManager.setMinuteForCommandF(selectedMinutes);
                        prefManager.setMinute(minutes);

//                        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);
                    }
                }

                alertview_selection.dismiss();

                ResetCounter(1);
            }
        });

    }

    // SupplyBlowerSettingActivity AlertDailogBox
    public void SupplyBlowerAlertDialogBox(final Dialog alertview_selection, final String type) {
        if (type.equals("txt_S09_SB_XYYY"))
            alertview_selection.setContentView(R.layout.alertview_seekbar_nagetive_selection_layout);
        else if (type.equals("txt_S10_SB_XYYY"))
            alertview_selection.setContentView(R.layout.alertview_seekbar_nagetive_selection_layout);
        else if (type.equals("txt_S06_SB_XXXX"))
            alertview_selection.setContentView(R.layout.alertview_seekbar_selection_layout); // Seekbar Selection
        else if (type.equals("txt_S01_Y3"))
            alertview_selection.setContentView(R.layout.alertview_multiple_selection_layout); // Multiple Selection

        alertview_selection.show();

        txt_dailogTitle = alertview_selection.findViewById(R.id.txt_dailogTitle);

        if (type.equals("txt_S09_SB_XYYY")) {
            txt_dailogTitle.setText("AC Pressure Setpoint");

            txt_MinSeekbar_Progress = alertview_selection.findViewById(R.id.txt_MinSeekbar_Progress);
            txt_MaxSeekbar_Progress = alertview_selection.findViewById(R.id.txt_MaxSeekbar_Progress);
            seekbar_Progress_Selection = alertview_selection.findViewById(R.id.seekbar_Progress_Selection);
            txt_SelectedSeekbar_Progress = alertview_selection.findViewById(R.id.txt_SelectedSeekbar_Progress);
            txt_Minus_Progress = alertview_selection.findViewById(R.id.txt_Minus_Progress);
            txt_Plus_Progress = alertview_selection.findViewById(R.id.txt_Plus_Progress);

            txt_MinSeekbar_Progress.setText("-0.600");
            txt_MaxSeekbar_Progress.setText("+0.600");

            String S09 = responseHandler.hexToBinary(setpointArrayList.get(0).getS09());

            if (S09.charAt(0) == '0') {
                // +
                float decimal = Float.parseFloat(String.valueOf(responseHandler.binaryToDecimal(Integer.parseInt(S09.substring(2, 16))))) / 1000;
                txt_SelectedSeekbar_Progress.setText("+" + CodeReUse.formatter3Digit.format(decimal));

                seekbar_Progress_Selection.setProgress(responseHandler.binaryToDecimal(Integer.parseInt(S09.substring(2, 16))));
            } else {
                // -
                float decimal = Float.parseFloat(String.valueOf(responseHandler.binaryToDecimal(Integer.parseInt(S09.substring(2, 16))))) / 1000;
                txt_SelectedSeekbar_Progress.setText("-" + CodeReUse.formatter3Digit.format(decimal));

                seekbar_Progress_Selection.setProgress(-responseHandler.binaryToDecimal(Integer.parseInt(S09.substring(2, 16))));
            }

            seekbar_Progress_Selection.setOnSeekChangeListener(new OnSeekChangeListener() {
                @Override
                public void onSeeking(SeekParams seekParams) {
                    seekbar_Progress_Selection.setProgress(seekParams.progress);
                    txt_SelectedSeekbar_Progress.setText(CodeReUse.formatter3Digit.format(seekParams.progressFloat / 1000));
                }

                @Override
                public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

                }
            });

            txt_Minus_Progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResetCounter(1);
                    if (seekbar_Progress_Selection.getProgress() > -600) {
                        int setProgress = seekbar_Progress_Selection.getProgress() - 1;
                        seekbar_Progress_Selection.setProgress(setProgress);
                        txt_SelectedSeekbar_Progress.setText(CodeReUse.formatter3Digit.format(seekbar_Progress_Selection.getProgressFloat() / 1000));
                    }
                }
            });

            txt_Plus_Progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResetCounter(1);
                    if (seekbar_Progress_Selection.getProgress() < 600) {
                        int setProgress = seekbar_Progress_Selection.getProgress() + 1;
                        seekbar_Progress_Selection.setProgress(setProgress);
                        txt_SelectedSeekbar_Progress.setText(CodeReUse.formatter3Digit.format(seekbar_Progress_Selection.getProgressFloat() / 1000));
                    }
                }
            });
        }
        else if (type.equals("txt_S10_SB_XYYY")) {
            txt_dailogTitle.setText("Battery Pressure Setpoint");

            txt_MinSeekbar_Progress = alertview_selection.findViewById(R.id.txt_MinSeekbar_Progress);
            txt_MaxSeekbar_Progress = alertview_selection.findViewById(R.id.txt_MaxSeekbar_Progress);
            seekbar_Progress_Selection = alertview_selection.findViewById(R.id.seekbar_Progress_Selection);
            txt_SelectedSeekbar_Progress = alertview_selection.findViewById(R.id.txt_SelectedSeekbar_Progress);
            txt_Minus_Progress = alertview_selection.findViewById(R.id.txt_Minus_Progress);
            txt_Plus_Progress = alertview_selection.findViewById(R.id.txt_Plus_Progress);

            txt_MinSeekbar_Progress.setText("-0.600");
            txt_MaxSeekbar_Progress.setText("+0.600");

            String S10 = responseHandler.hexToBinary(setpointArrayList.get(0).getS10());

            if (S10.charAt(0) == '0') {
                // +
                float decimal = Float.parseFloat(String.valueOf(responseHandler.binaryToDecimal(Integer.parseInt(S10.substring(2, 16))))) / 1000;
                txt_SelectedSeekbar_Progress.setText("+" + CodeReUse.formatter3Digit.format(decimal));

                seekbar_Progress_Selection.setProgress(responseHandler.binaryToDecimal(Integer.parseInt(S10.substring(2, 16))));
            } else {
                // -
                float decimal = Float.parseFloat(String.valueOf(responseHandler.binaryToDecimal(Integer.parseInt(S10.substring(2, 16))))) / 1000;
                txt_SelectedSeekbar_Progress.setText("-" + CodeReUse.formatter3Digit.format(decimal));

                seekbar_Progress_Selection.setProgress(-responseHandler.binaryToDecimal(Integer.parseInt(S10.substring(2, 16))));
            }

            seekbar_Progress_Selection.setOnSeekChangeListener(new OnSeekChangeListener() {
                @Override
                public void onSeeking(SeekParams seekParams) {
                    seekbar_Progress_Selection.setProgress(seekParams.progress);
                    txt_SelectedSeekbar_Progress.setText(CodeReUse.formatter3Digit.format(seekParams.progressFloat / 1000));
                }

                @Override
                public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

                }
            });

            txt_Minus_Progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResetCounter(1);
                    if (seekbar_Progress_Selection.getProgress() > -600) {
                        int setProgress = seekbar_Progress_Selection.getProgress() - 1;
                        seekbar_Progress_Selection.setProgress(setProgress);
                        txt_SelectedSeekbar_Progress.setText(CodeReUse.formatter3Digit.format(seekbar_Progress_Selection.getProgressFloat() / 1000));
                    }
                }
            });

            txt_Plus_Progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResetCounter(1);
                    if (seekbar_Progress_Selection.getProgress() < 600) {
                        int setProgress = seekbar_Progress_Selection.getProgress() + 1;
                        seekbar_Progress_Selection.setProgress(setProgress);
                        txt_SelectedSeekbar_Progress.setText(CodeReUse.formatter3Digit.format(seekbar_Progress_Selection.getProgressFloat() / 1000));
                    }
                }
            });

        }
        else if (type.equals("txt_S06_SB_XXXX")) {
            txt_dailogTitle.setText("Blower Fail RPM");

            txt_MinSeekbar_Progress = alertview_selection.findViewById(R.id.txt_MinSeekbar_Progress);
            txt_MaxSeekbar_Progress = alertview_selection.findViewById(R.id.txt_MaxSeekbar_Progress);
            seekbar_Progress_Selection = alertview_selection.findViewById(R.id.seekbar_Progress_Selection);
            txt_SelectedSeekbar_Progress = alertview_selection.findViewById(R.id.txt_SelectedSeekbar_Progress);
            txt_Minus_Progress = alertview_selection.findViewById(R.id.txt_Minus_Progress);
            txt_Plus_Progress = alertview_selection.findViewById(R.id.txt_Plus_Progress);

            txt_MinSeekbar_Progress.setText("0");
            txt_MaxSeekbar_Progress.setText("800");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                seekbar_Progress_Selection.setMin(0);
            }

            seekbar_Progress_Selection.setMax(800 / 10);

            seekbar_Progress_Selection.setProgress(Integer.parseInt(responseHandler.hexToString(setpointArrayList.get(0).getS06())) / 10);
            txt_SelectedSeekbar_Progress.setText(responseHandler.hexToString(setpointArrayList.get(0).getS06()));

            seekbar_Progress_Selection.setOnSeekChangeListener(new OnSeekChangeListener() {
                @Override
                public void onSeeking(SeekParams seekParams) {
                    seekbar_Progress_Selection.setProgress(seekParams.progress);
                    txt_SelectedSeekbar_Progress.setText(String.valueOf(seekParams.progress * 10));
                }

                @Override
                public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

                }
            });

            txt_Minus_Progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResetCounter(1);
                    if (seekbar_Progress_Selection.getProgress() > 0) {
                        int setProgress = seekbar_Progress_Selection.getProgress() - 1;
                        seekbar_Progress_Selection.setProgress(setProgress);
                        txt_SelectedSeekbar_Progress.setText(String.valueOf(setProgress * 10));
                    }
                }
            });

            txt_Plus_Progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResetCounter(1);
                    if (seekbar_Progress_Selection.getProgress() < 800 / 10) {
                        int setProgress = seekbar_Progress_Selection.getProgress() + 1;
                        seekbar_Progress_Selection.setProgress(setProgress);
                        txt_SelectedSeekbar_Progress.setText(String.valueOf(setProgress * 10));
                    }
                }
            });
        }
        else if (type.equals("txt_S01_Y3"))
        {
            txt_dailogTitle.setText("Audible Alarm");

            getMultipleSelections();

            // set up the RecyclerView
            rv_MultipleSelection = alertview_selection.findViewById(R.id.rv_MultipleSelection);
            rv_MultipleSelection.setLayoutManager(new GridLayoutManager(this, 2));

            multipleSelectionAdapter = new MultipleSelectionAdapter(act, multipleSelections);
            rv_MultipleSelection.setAdapter(multipleSelectionAdapter);

            for (int i = 0; i < multipleSelections.size(); i++) {
                String S01_Y3 = String.valueOf(responseHandler.hexToBinary(setpointArrayList.get(0).getS01()).charAt(8));

                if (multipleSelections.get(i).getId().equals(S01_Y3)) {
                    multipleSelections.get(i).setSelected(true);
                    multipleSelectionAdapter.notifyDataSetChanged();
                }
            }
        }
        btn_Cancel_Selection = alertview_selection.findViewById(R.id.btn_Cancel_Selection);
        btn_Save_Selection = alertview_selection.findViewById(R.id.btn_Save_Selection);

        btn_Cancel_Selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetCounter(1);
                alertview_selection.dismiss();
            }
        });

        btn_Save_Selection.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {
                ResetCounter(1);
                if (type.equals("txt_S09_SB_XYYY")) {
                    String barvalue = txt_SelectedSeekbar_Progress.getText().toString();

                    int S09XYYY = (int) (Float.parseFloat(barvalue.replace("-", "")) * 1000);

                    String S09_XYYY = "";
                    String hexvalue = Integer.toHexString(S09XYYY);

                    if (hexvalue.length() == 0)
                        hexvalue = "000";
                    else if (hexvalue.length() == 1)
                        hexvalue = "00" + hexvalue;
                    else if (hexvalue.length() == 2)
                        hexvalue = "0" + hexvalue;

                    Utility.Log(TAG, "Hex String value =>> " + hexvalue);
                    if (barvalue.startsWith("-")) {
                        S09_XYYY = "8" + hexvalue;
                    } else {
                        S09_XYYY = "0" + hexvalue;
                    }

                    String command = "S09=" + S09_XYYY.toUpperCase();

                    Utility.Log(TAG, "Sending S09_XYYY ==> " + command);

                    prefManager.setSendCommandS(command);
                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);

                    alertview_selection.dismiss();

                }
                else if (type.equals("txt_S10_SB_XYYY")) {
                    String barvalue = txt_SelectedSeekbar_Progress.getText().toString();

                    int S10XYYY = (int) (Float.parseFloat(barvalue.replace("-", "")) * 1000);

                    String S10_XYYY = "";
                    String hexvalue = Integer.toHexString(S10XYYY);

                    if (hexvalue.length() == 0)
                        hexvalue = "000";
                    else if (hexvalue.length() == 1)
                        hexvalue = "00" + hexvalue;
                    else if (hexvalue.length() == 2)
                        hexvalue = "0" + hexvalue;

                    Utility.Log(TAG, "Hex String value =>> " + hexvalue);
                    if (barvalue.startsWith("-")) {
                        S10_XYYY = "8" + hexvalue;
                    } else {
                        S10_XYYY = "0" + hexvalue;
                    }

                    String command = "S10=" + S10_XYYY.toUpperCase();

                    Utility.Log(TAG, "Sending S10_XYYY ==> " + command);

                    prefManager.setSendCommandS(command);
                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);

                    alertview_selection.dismiss();

                }
                else if (type.equals("txt_S06_SB_XXXX")) {
                    // Seekbar Selection

                    String barvalue = txt_SelectedSeekbar_Progress.getText().toString();
                    int S06XXXX = (int) (Float.parseFloat(barvalue));

                    String hexvalue = responseHandler.stringToHex(String.valueOf(S06XXXX), false);

                    String command = "S06=" + hexvalue.toUpperCase();

                    Utility.Log(TAG, "Sending S06_XXXX ==> " + command);

                    prefManager.setSendCommandS(command);
                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);

                    alertview_selection.dismiss();
                }
                else if (type.equals("txt_S01_Y3"))
                {
                    Log.e("TAG","Clcik on ");
                    // Multiple Selection
                    String bin = responseHandler.hexToBinary(setpointArrayList.get(0).getS01());
                    Utility.Log(TAG, "hexToBinary => " + bin);
                    //0000111100001000 //0F08
                    //change to 0F00
                    String command = "";

                    for (int i = 0; i < multipleSelections.size(); i++) {
                        if (multipleSelections.get(i).isSelected()) {
                            //String S01 = responseHandler.binaryToHex(bin.substring(0,13).concat(multipleSelections.get(i).getId())).toUpperCase();
                            String S01 = responseHandler.binaryToHex(bin.substring(0, 8).concat(multipleSelections.get(i).getId()).concat(bin.substring(9))).toUpperCase();

                            if (S01.length() == 1)
                                S01 = "000" + S01;
                            else if (S01.length() == 2)
                                S01 = "00" + S01;
                            else if (S01.length() == 3)
                                S01 = "0" + S01;


                            command = "S01=" + S01;
                        }
                    }

                    Utility.Log(TAG, "Sending S01 ==> " + command);

                    prefManager.setSendCommandS(command);
                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);

                    alertview_selection.dismiss();

                }
            }
        });
    }

    // ExhaustBlowerSettingActivity AlertDailogBox
    public void ExhaustBlowerAlertDialogBox(final Dialog alertview_selection, final String type) {
        if (type.equals("txt_S15_Z0")) //cage alarm input
            alertview_selection.setContentView(R.layout.alertview_multiple_selection_layout); // Multiple Selection
        else if (type.equals("txt_S13_XXXX")) // cage alarm delay
            alertview_selection.setContentView(R.layout.alertview_seekbar_nagetive_selection_layout); // Nagetive Seekbar Selection
        else if (type.equals("txt_S14_XXXX")) //cage alarm duration
            alertview_selection.setContentView(R.layout.alertview_seekbar_nagetive_selection_layout); // Nagetive Seekbar Selection


        alertview_selection.show();

        txt_dailogTitle = alertview_selection.findViewById(R.id.txt_dailogTitle);

        if (type.equals("txt_S15_Z0")) {
            txt_dailogTitle.setText("Cage Alarm Input");

            getMultipleSelections();

            // set up the RecyclerView
            rv_MultipleSelection = alertview_selection.findViewById(R.id.rv_MultipleSelection);
            rv_MultipleSelection.setLayoutManager(new GridLayoutManager(this, 2));

            multipleSelectionAdapter = new MultipleSelectionAdapter(act, multipleSelections);
            rv_MultipleSelection.setAdapter(multipleSelectionAdapter);

            for (int i = 0; i < multipleSelections.size(); i++) {
                String S15_Z0 = String.valueOf(responseHandler.hexToBinary(setpointArrayList.get(0).getS15()).charAt(15));

                if (multipleSelections.get(i).getId().equals(S15_Z0)) {
                    multipleSelections.get(i).setSelected(true);
                    multipleSelectionAdapter.notifyDataSetChanged();
                }
            }

        }
        else if (type.equals("txt_S13_XXXX")) {
            txt_dailogTitle.setText("Cage Alarm Delay (sec)");

            txt_MinSeekbar_Progress = alertview_selection.findViewById(R.id.txt_MinSeekbar_Progress);
            txt_MaxSeekbar_Progress = alertview_selection.findViewById(R.id.txt_MaxSeekbar_Progress);
            seekbar_Progress_Selection = alertview_selection.findViewById(R.id.seekbar_Progress_Selection);
            txt_SelectedSeekbar_Progress = alertview_selection.findViewById(R.id.txt_SelectedSeekbar_Progress);
            txt_Minus_Progress = alertview_selection.findViewById(R.id.txt_Minus_Progress);
            txt_Plus_Progress = alertview_selection.findViewById(R.id.txt_Plus_Progress);

            txt_MinSeekbar_Progress.setText("0");
            txt_MaxSeekbar_Progress.setText("2000");

            String S13 = responseHandler.hexToBinary(setpointArrayList.get(0).getS13());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                seekbar_Progress_Selection.setMin(0);
            }

            seekbar_Progress_Selection.setMax(2000);

            seekbar_Progress_Selection.setProgress(Integer.parseInt(responseHandler.hexToString(setpointArrayList.get(0).getS13())));
            txt_SelectedSeekbar_Progress.setText(responseHandler.hexToString(setpointArrayList.get(0).getS13()));

            seekbar_Progress_Selection.setOnSeekChangeListener(new OnSeekChangeListener() {
                @Override
                public void onSeeking(SeekParams seekParams) {
                    seekbar_Progress_Selection.setProgress(seekParams.progress);
                    txt_SelectedSeekbar_Progress.setText(String.valueOf(seekParams.progress));
                }

                @Override
                public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

                }
            });

            txt_Minus_Progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResetCounter(1);
                    if (seekbar_Progress_Selection.getProgress() > 0) {
                        int setProgress = seekbar_Progress_Selection.getProgress() - 1;
                        seekbar_Progress_Selection.setProgress(setProgress);
                        txt_SelectedSeekbar_Progress.setText(String.valueOf(setProgress));
                    }
                }
            });

            txt_Plus_Progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResetCounter(1);
                    if (seekbar_Progress_Selection.getProgress() < 2000) {
                        int setProgress = seekbar_Progress_Selection.getProgress() + 1;
                        seekbar_Progress_Selection.setProgress(setProgress);
                        txt_SelectedSeekbar_Progress.setText(String.valueOf(setProgress));
                    }
                }
            });


        }
        else if (type.equals("txt_S14_XXXX")) {
            txt_dailogTitle.setText("Cage Alarm Duration (sec)");

            txt_MinSeekbar_Progress = alertview_selection.findViewById(R.id.txt_MinSeekbar_Progress);
            txt_MaxSeekbar_Progress = alertview_selection.findViewById(R.id.txt_MaxSeekbar_Progress);
            seekbar_Progress_Selection = alertview_selection.findViewById(R.id.seekbar_Progress_Selection);
            txt_SelectedSeekbar_Progress = alertview_selection.findViewById(R.id.txt_SelectedSeekbar_Progress);
            txt_Minus_Progress = alertview_selection.findViewById(R.id.txt_Minus_Progress);
            txt_Plus_Progress = alertview_selection.findViewById(R.id.txt_Plus_Progress);

            txt_MinSeekbar_Progress.setText("0");
            txt_MaxSeekbar_Progress.setText("2000");

            String S14 = responseHandler.hexToBinary(setpointArrayList.get(0).getS14());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                seekbar_Progress_Selection.setMin(0);
            }

            seekbar_Progress_Selection.setMax(2000);

            seekbar_Progress_Selection.setProgress(Integer.parseInt(responseHandler.hexToString(setpointArrayList.get(0).getS14())));
            txt_SelectedSeekbar_Progress.setText(responseHandler.hexToString(setpointArrayList.get(0).getS14()));

            seekbar_Progress_Selection.setOnSeekChangeListener(new OnSeekChangeListener() {
                @Override
                public void onSeeking(SeekParams seekParams) {
                    seekbar_Progress_Selection.setProgress(seekParams.progress);
                    txt_SelectedSeekbar_Progress.setText(String.valueOf(seekParams.progress));
                }

                @Override
                public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

                }
            });

            txt_Minus_Progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResetCounter(1);
                    if (seekbar_Progress_Selection.getProgress() > 0) {
                        int setProgress = seekbar_Progress_Selection.getProgress() - 1;
                        seekbar_Progress_Selection.setProgress(setProgress);
                        txt_SelectedSeekbar_Progress.setText(String.valueOf(setProgress));
                    }
                }
            });

            txt_Plus_Progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResetCounter(1);
                    if (seekbar_Progress_Selection.getProgress() < 2000) {
                        int setProgress = seekbar_Progress_Selection.getProgress() + 1;
                        seekbar_Progress_Selection.setProgress(setProgress);
                        txt_SelectedSeekbar_Progress.setText(String.valueOf(setProgress));
                    }
                }
            });


        }


        btn_Cancel_Selection = alertview_selection.findViewById(R.id.btn_Cancel_Selection);
        btn_Save_Selection = alertview_selection.findViewById(R.id.btn_Save_Selection);

        btn_Cancel_Selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetCounter(1);
                alertview_selection.dismiss();
            }
        });

        btn_Save_Selection.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {
                ResetCounter(1);
                if (type.equals("txt_S15_Z0")) {
                    // Multiple Selection
                    String bin = responseHandler.hexToBinary(setpointArrayList.get(0).getS15());
                    Utility.Log(TAG, "hexToBinary => " + bin);
                    //0000111100001000 //0F08
                    //change to 0F00
                    String command = "";

                    for (int i = 0; i < multipleSelections.size(); i++) {
                        if (multipleSelections.get(i).isSelected()) {
                            command = "S15=000" + multipleSelections.get(i).getId();
                        }
                    }

                    Utility.Log(TAG, "Sending S15 ==> " + command);

                    prefManager.setSendCommandS(command);

                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);

                    alertview_selection.dismiss();

                }
                else if (type.equals("txt_S13_XXXX")) {
                    // Nagetive Seekbar Selection

                    int S13XXXX = Integer.valueOf(txt_SelectedSeekbar_Progress.getText().toString());

                    String S13_XXXX = "";
                    String hexvalue = Integer.toHexString(S13XXXX);

                    if (hexvalue.length() == 0)
                        hexvalue = "0000";
                    else if (hexvalue.length() == 1)
                        hexvalue = "000" + hexvalue;
                    else if (hexvalue.length() == 2)
                        hexvalue = "00" + hexvalue;
                    else if (hexvalue.length() == 3)
                        hexvalue = "0" + hexvalue;

                    Utility.Log(TAG, "Hex String value =>> " + hexvalue);


                    String command = "S13=" + hexvalue.toUpperCase();

                    Utility.Log(TAG, "Sending S13 ==> " + command);

                    prefManager.setSendCommandS(command);

                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);

                    alertview_selection.dismiss();
                }
                else if (type.equals("txt_S14_XXXX")) {
                    // Nagetive Seekbar Selection
                    int S14XXXX = Integer.valueOf(txt_SelectedSeekbar_Progress.getText().toString());

                    String S14_XXXX = "";
                    String hexvalue = Integer.toHexString(S14XXXX);

                    if (hexvalue.length() == 0)
                        hexvalue = "0000";
                    else if (hexvalue.length() == 1)
                        hexvalue = "000" + hexvalue;
                    else if (hexvalue.length() == 2)
                        hexvalue = "00" + hexvalue;
                    else if (hexvalue.length() == 3)
                        hexvalue = "0" + hexvalue;

                    Utility.Log(TAG, "Hex String value =>> " + hexvalue);


                    String command = "S14=" + hexvalue.toUpperCase();

                    Utility.Log(TAG, "Sending S14 ==> " + command);

                    prefManager.setSendCommandS(command);

                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);

                    alertview_selection.dismiss();
                }


            }
        });
    }

    // FilterSettingActivity AlertDailogBox
    public void FilterSettingAlertDialogBox(final Dialog alertview_selection, final String type) {
        if (type.equals("txt_S03_XXXX"))
            alertview_selection.setContentView(R.layout.alertview_seekbar_selection_layout); // Seekbar Selection
        else if (type.equals("txt_S04_XXXX"))
            alertview_selection.setContentView(R.layout.alertview_seekbar_selection_layout); // Seekbar Selection
        else if (type.equals("txt_S05_XXXX"))
            alertview_selection.setContentView(R.layout.alertview_seekbar_selection_layout); // Seekbar Selection
        else if (type.equals("txt_S11_YY"))
            alertview_selection.setContentView(R.layout.alertview_seekbar_selection_layout); // Seekbar Selection
        else if (type.equals("txt_S11_XX"))
            alertview_selection.setContentView(R.layout.alertview_seekbar_selection_layout); // Seekbar Selection
        else if (type.equals("txt_S12_XXXX"))
            alertview_selection.setContentView(R.layout.alertview_seekbar_selection_layout); // Seekbar Selection

        alertview_selection.show();

        txt_dailogTitle = alertview_selection.findViewById(R.id.txt_dailogTitle);

        btn_Cancel_Selection = alertview_selection.findViewById(R.id.btn_Cancel_Selection);
        btn_Save_Selection = alertview_selection.findViewById(R.id.btn_Save_Selection);

        txt_MinSeekbar_Progress = alertview_selection.findViewById(R.id.txt_MinSeekbar_Progress);
        txt_MaxSeekbar_Progress = alertview_selection.findViewById(R.id.txt_MaxSeekbar_Progress);
        seekbar_Progress_Selection = alertview_selection.findViewById(R.id.seekbar_Progress_Selection);
        txt_SelectedSeekbar_Progress = alertview_selection.findViewById(R.id.txt_SelectedSeekbar_Progress);

        txt_Minus_Progress = alertview_selection.findViewById(R.id.txt_Minus_Progress);
        txt_Plus_Progress = alertview_selection.findViewById(R.id.txt_Plus_Progress);

        if (type.equals("txt_S03_XXXX")) {
            txt_dailogTitle.setText("Clean RPM (S)");

            txt_MinSeekbar_Progress.setText("0");
            txt_MaxSeekbar_Progress.setText("3500");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                seekbar_Progress_Selection.setMin(0);
            }

            seekbar_Progress_Selection.setMax(3500 / 10);

            seekbar_Progress_Selection.setProgress(Integer.parseInt(responseHandler.hexToString(setpointArrayList.get(0).getS03())) / 10);
            txt_SelectedSeekbar_Progress.setText(responseHandler.hexToString(setpointArrayList.get(0).getS03()));

            seekbar_Progress_Selection.setOnSeekChangeListener(new OnSeekChangeListener() {
                @Override
                public void onSeeking(SeekParams seekParams) {
                    seekbar_Progress_Selection.setProgress(seekParams.progress);
                    txt_SelectedSeekbar_Progress.setText(String.valueOf(seekParams.progress * 10));
                }

                @Override
                public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

                }
            });

            txt_Minus_Progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResetCounter(1);
                    if (seekbar_Progress_Selection.getProgress() > 0) {
                        int setProgress = seekbar_Progress_Selection.getProgress() - 1;
                        seekbar_Progress_Selection.setProgress(setProgress);
                        txt_SelectedSeekbar_Progress.setText(String.valueOf(setProgress * 10));
                    }
                }
            });

            txt_Plus_Progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResetCounter(1);
                    if (seekbar_Progress_Selection.getProgress() < 3500 / 10) {
                        int setProgress = seekbar_Progress_Selection.getProgress() + 1;
                        seekbar_Progress_Selection.setProgress(setProgress);
                        txt_SelectedSeekbar_Progress.setText(String.valueOf(setProgress * 10));
                    }
                }
            });

        } else if (type.equals("txt_S04_XXXX")) {
            txt_dailogTitle.setText("Clean RPM (E) +");

            txt_MinSeekbar_Progress.setText("0");
            txt_MaxSeekbar_Progress.setText("3500");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                seekbar_Progress_Selection.setMin(0);
            }

            seekbar_Progress_Selection.setMax(3500 / 10);

            seekbar_Progress_Selection.setProgress(Integer.parseInt(responseHandler.hexToString(setpointArrayList.get(0).getS04())) / 10);
            txt_SelectedSeekbar_Progress.setText(responseHandler.hexToString(setpointArrayList.get(0).getS04()));

            seekbar_Progress_Selection.setOnSeekChangeListener(new OnSeekChangeListener() {
                @Override
                public void onSeeking(SeekParams seekParams) {
                    seekbar_Progress_Selection.setProgress(seekParams.progress);
                    txt_SelectedSeekbar_Progress.setText(String.valueOf(seekParams.progress * 10));

                }

                @Override
                public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

                }
            });

            txt_Minus_Progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResetCounter(1);
                    if (seekbar_Progress_Selection.getProgress() > 0) {
                        int setProgress = seekbar_Progress_Selection.getProgress() - 1;
                        seekbar_Progress_Selection.setProgress(setProgress);
                        txt_SelectedSeekbar_Progress.setText(String.valueOf(setProgress * 10));
                    }
                }
            });

            txt_Plus_Progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResetCounter(1);
                    if (seekbar_Progress_Selection.getProgress() < 3500 / 10) {
                        int setProgress = seekbar_Progress_Selection.getProgress() + 1;
                        seekbar_Progress_Selection.setProgress(setProgress);
                        txt_SelectedSeekbar_Progress.setText(String.valueOf(setProgress * 10));
                    }
                }
            });
        } else if (type.equals("txt_S05_XXXX")) {
            txt_dailogTitle.setText("Clean RPM");

            txt_MinSeekbar_Progress.setText("0");
            txt_MaxSeekbar_Progress.setText("3500");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                seekbar_Progress_Selection.setMin(0);
            }

            seekbar_Progress_Selection.setMax(3500 / 10);

            seekbar_Progress_Selection.setProgress(Integer.parseInt(responseHandler.hexToString(setpointArrayList.get(0).getS05())) / 10);
            txt_SelectedSeekbar_Progress.setText(responseHandler.hexToString(setpointArrayList.get(0).getS05()));

            seekbar_Progress_Selection.setOnSeekChangeListener(new OnSeekChangeListener() {
                @Override
                public void onSeeking(SeekParams seekParams) {
                    seekbar_Progress_Selection.setProgress(seekParams.progress);
                    txt_SelectedSeekbar_Progress.setText(String.valueOf(seekParams.progress * 10));

                }

                @Override
                public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

                }
            });

            txt_Minus_Progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResetCounter(1);
                    if (seekbar_Progress_Selection.getProgress() > 0) {
                        int setProgress = seekbar_Progress_Selection.getProgress() - 1;
                        seekbar_Progress_Selection.setProgress(setProgress);
                        txt_SelectedSeekbar_Progress.setText(String.valueOf(setProgress * 10));
                    }
                }
            });

            txt_Plus_Progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResetCounter(1);
                    if (seekbar_Progress_Selection.getProgress() < 3500 / 10) {
                        int setProgress = seekbar_Progress_Selection.getProgress() + 1;
                        seekbar_Progress_Selection.setProgress(setProgress);
                        txt_SelectedSeekbar_Progress.setText(String.valueOf(setProgress * 10));
                    }
                }
            });
        } else if (type.equals("txt_S11_YY")) {
            txt_dailogTitle.setText("Filter max RPM");

            txt_MinSeekbar_Progress.setText("2500");
            txt_MaxSeekbar_Progress.setText("4000");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //seekbar_Progress_Selection.setMin(2500/100);
                seekbar_Progress_Selection.setMin(0);
            }

            seekbar_Progress_Selection.setMax(15);

            int S11_YY = Integer.parseInt(responseHandler.hexToString(setpointArrayList.get(0).getS11(), false));
            seekbar_Progress_Selection.setProgress(S11_YY);
            txt_SelectedSeekbar_Progress.setText(String.valueOf((S11_YY * 100) + 2500));

            seekbar_Progress_Selection.setOnSeekChangeListener(new OnSeekChangeListener() {
                @Override
                public void onSeeking(SeekParams seekParams) {
                    seekbar_Progress_Selection.setProgress(seekParams.progress);
                    txt_SelectedSeekbar_Progress.setText(String.valueOf((seekParams.progress * 100) + 2500));
                }

                @Override
                public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

                }
            });

            txt_Minus_Progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResetCounter(1);
                    if (seekbar_Progress_Selection.getProgress() > 0) {
                        int setProgress = seekbar_Progress_Selection.getProgress() - 1;
                        seekbar_Progress_Selection.setProgress(setProgress);
                        txt_SelectedSeekbar_Progress.setText(String.valueOf((setProgress * 100) + 2500));
                    }
                }
            });

            txt_Plus_Progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResetCounter(1);
                    if (seekbar_Progress_Selection.getProgress() < 15) {
                        int setProgress = seekbar_Progress_Selection.getProgress() + 1;
                        seekbar_Progress_Selection.setProgress(setProgress);
                        txt_SelectedSeekbar_Progress.setText(String.valueOf((setProgress * 100) + 2500));
                    }
                }
            });

        } else if (type.equals("txt_S11_XX")) {
            txt_dailogTitle.setText("Alarm trip %");

            txt_MinSeekbar_Progress.setText("0");
            txt_MaxSeekbar_Progress.setText("99");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                seekbar_Progress_Selection.setMin(0);
            }

            seekbar_Progress_Selection.setMax(99);

            seekbar_Progress_Selection.setProgress(Integer.parseInt(responseHandler.hexToString(setpointArrayList.get(0).getS11(), true)));
            txt_SelectedSeekbar_Progress.setText(responseHandler.hexToString(setpointArrayList.get(0).getS11(), true));

            seekbar_Progress_Selection.setOnSeekChangeListener(new OnSeekChangeListener() {
                @Override
                public void onSeeking(SeekParams seekParams) {
                    seekbar_Progress_Selection.setProgress(seekParams.progress);
                    txt_SelectedSeekbar_Progress.setText(String.valueOf(seekParams.progress));
                }

                @Override
                public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

                }
            });

            txt_Minus_Progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResetCounter(1);
                    if (seekbar_Progress_Selection.getProgress() > 0) {
                        int setProgress = seekbar_Progress_Selection.getProgress() - 1;
                        seekbar_Progress_Selection.setProgress(setProgress);
                        txt_SelectedSeekbar_Progress.setText(String.valueOf(setProgress));
                    }
                }
            });

            txt_Plus_Progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResetCounter(1);
                    if (seekbar_Progress_Selection.getProgress() < 99) {
                        int setProgress = seekbar_Progress_Selection.getProgress() + 1;
                        seekbar_Progress_Selection.setProgress(setProgress);
                        txt_SelectedSeekbar_Progress.setText(String.valueOf(setProgress));
                    }
                }
            });

        } else if (type.equals("txt_S12_XXXX")) {
            txt_dailogTitle.setText("PREFILTER POH");

            txt_MinSeekbar_Progress.setText("0");
            txt_MaxSeekbar_Progress.setText("999");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                seekbar_Progress_Selection.setMin(0);
            }

            seekbar_Progress_Selection.setMax(999);

            seekbar_Progress_Selection.setProgress(Integer.parseInt(responseHandler.hexToString(setpointArrayList.get(0).getS12())));

            txt_SelectedSeekbar_Progress.setText(responseHandler.hexToString(setpointArrayList.get(0).getS12()));

            seekbar_Progress_Selection.setOnSeekChangeListener(new OnSeekChangeListener() {
                @Override
                public void onSeeking(SeekParams seekParams) {
                    seekbar_Progress_Selection.setProgress(seekParams.progress);
                    txt_SelectedSeekbar_Progress.setText(String.valueOf(seekParams.progress));
                }

                @Override
                public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

                }
            });

            txt_Minus_Progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResetCounter(1);
                    if (seekbar_Progress_Selection.getProgress() > 0) {
                        int setProgress = seekbar_Progress_Selection.getProgress() - 1;
                        seekbar_Progress_Selection.setProgress(setProgress);
                        txt_SelectedSeekbar_Progress.setText(String.valueOf(setProgress));
                    }
                }
            });

            txt_Plus_Progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResetCounter(1);
                    if (seekbar_Progress_Selection.getProgress() < 999) {
                        int setProgress = seekbar_Progress_Selection.getProgress() + 1;
                        seekbar_Progress_Selection.setProgress(setProgress);
                        txt_SelectedSeekbar_Progress.setText(String.valueOf(setProgress));
                    }
                }
            });
        }

        btn_Cancel_Selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetCounter(1);
                alertview_selection.dismiss();
            }
        });

        btn_Save_Selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetCounter(1);
                if (type.equals("txt_S03_XXXX")) {
                    // Seekbar Selection
                    String S03_XXXX = responseHandler.stringToHex(txt_SelectedSeekbar_Progress.getText().toString(), false);

                    String command = "S03=" + S03_XXXX.toUpperCase();

                    Utility.Log(TAG, "Sending S03_XXXX ==> " + command);

                    prefManager.setSendCommandS(command);

                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);

                    alertview_selection.dismiss();
                } else if (type.equals("txt_S04_XXXX")) {
                    // Seekbar Selection
                    int S04XXXX = Integer.valueOf(txt_SelectedSeekbar_Progress.getText().toString()); // 2000
                    String S04_XXXX = responseHandler.stringToHex(String.valueOf(S04XXXX), false);

                    String command = "S04=" + S04_XXXX.toUpperCase();

                    Utility.Log(TAG, "Sending S04_XXXX ==> " + command);

                    prefManager.setSendCommandS(command);

                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);

                    alertview_selection.dismiss();
                } else if (type.equals("txt_S05_XXXX")) {
                    // Seekbar Selection
                    int S05XXXX = Integer.valueOf(txt_SelectedSeekbar_Progress.getText().toString());
                    String S05_XXXX = responseHandler.stringToHex(String.valueOf(S05XXXX), false);

                    String command = "S05=" + S05_XXXX.toUpperCase();

                    Utility.Log(TAG, "Sending S05_XXXX ==> " + command);

                    prefManager.setSendCommandS(command);

                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);

                    alertview_selection.dismiss();
                } else if (type.equals("txt_S11_YY")) {
                    // Seekbar Selection
                    int S11YY = (Integer.valueOf(txt_SelectedSeekbar_Progress.getText().toString()) - 2500) / 100;
                    String S11_YY = responseHandler.stringToHex(String.valueOf(S11YY), true);
                    String S11_XX = responseHandler.getHexString(setpointArrayList.get(0).getS11(), true);

                    String command = "S11=" + S11_XX.concat(S11_YY).toUpperCase();

                    Utility.Log(TAG, "Sending S11_YY ==> " + command);

                    prefManager.setSendCommandS(command);

                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);

                    alertview_selection.dismiss();
                } else if (type.equals("txt_S11_XX")) {
                    // Seekbar Selection
                    String S11_XX = responseHandler.stringToHex(txt_SelectedSeekbar_Progress.getText().toString(), true);
                    String S11_YY = responseHandler.getHexString(setpointArrayList.get(0).getS11(), false);

                    String command = "S11=" + S11_XX.concat(S11_YY).toUpperCase();

                    Utility.Log(TAG, "Sending S11_XX ==> " + command);

                    prefManager.setSendCommandS(command);

                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);

                    alertview_selection.dismiss();
                } else if (type.equals("txt_S12_XXXX")) {
                    // Seekbar Selection
                    String S12_XXXX = responseHandler.stringToHex(txt_SelectedSeekbar_Progress.getText().toString(), false);

                    String command = "S12=" + S12_XXXX.toUpperCase();

                    Utility.Log(TAG, "Sending S12_XXXX ==> " + command);

                    prefManager.setSendCommandS(command);

                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);

                    alertview_selection.dismiss();
                }

            }
        });
    }

    // CommunicationSettingAlertDailogBoxSelection AlertDailogBox
    public void CommunicationSettingAlertDialogBoxSelection(final Dialog alertview_selection, final String type, final String Command) {
        if (type.equals("SECURITY MODE"))
            alertview_selection.setContentView(R.layout.alertview_multiple_selection_layout); // Multiple Selection
        else if (type.equals("DHCP"))
            alertview_selection.setContentView(R.layout.alertview_multiple_selection_layout); // Multiple Selection
        else if (type.equals("COUNTRY CODE"))
            alertview_selection.setContentView(R.layout.alertview_multiple_selection_layout); // Multiple Selection
        alertview_selection.show();


        wifiArrayList.clear();
        wifiArrayList = responseHandler.getLastWiFiData();

        txt_dailogTitle = alertview_selection.findViewById(R.id.txt_dailogTitle);

        btn_Cancel_Selection = alertview_selection.findViewById(R.id.btn_Cancel_Selection);
        btn_Save_Selection = alertview_selection.findViewById(R.id.btn_Save_Selection);

        if (type.equals("SECURITY MODE")) {
            txt_dailogTitle.setText("SECURITY MODE");

            // set up the RecyclerView
            rv_MultipleSelection = alertview_selection.findViewById(R.id.rv_MultipleSelection);
            rv_MultipleSelection.setLayoutManager(new GridLayoutManager(this, 2));

            multipleSelectionAdapter = new MultipleSelectionAdapter(act, multipleSelections7);
            rv_MultipleSelection.setAdapter(multipleSelectionAdapter);

            for (int i = 0; i < multipleSelections7.size(); i++) {
                if (multipleSelections7.get(i).getId().equals(wifiArrayList.get(0).getW10())) {
                    multipleSelections7.get(i).setSelected(true);
                } else {
                    multipleSelections7.get(i).setSelected(false);
                }
                multipleSelectionAdapter.notifyDataSetChanged();
            }
        } else if (type.equals("DHCP")) {
            txt_dailogTitle.setText("DHCP");

            // set up the RecyclerView
            rv_MultipleSelection = alertview_selection.findViewById(R.id.rv_MultipleSelection);
            rv_MultipleSelection.setLayoutManager(new GridLayoutManager(this, 2));

            multipleSelectionAdapter = new MultipleSelectionAdapter(act, multipleSelections8);
            rv_MultipleSelection.setAdapter(multipleSelectionAdapter);

            for (int i = 0; i < multipleSelections8.size(); i++) {
                if (multipleSelections8.get(i).getId().equals(wifiArrayList.get(0).getW08())) {
                    multipleSelections8.get(i).setSelected(true);
                } else {
                    multipleSelections8.get(i).setSelected(false);
                }
                multipleSelectionAdapter.notifyDataSetChanged();
            }
        } else if (type.equals("COUNTRY CODE")) {
            txt_dailogTitle.setText("COUNTRY CODE");

            // set up the RecyclerView
            rv_MultipleSelection = alertview_selection.findViewById(R.id.rv_MultipleSelection);
            rv_MultipleSelection.setLayoutManager(new GridLayoutManager(this, 2));

            multipleSelectionAdapter = new MultipleSelectionAdapter(act, multipleSelections9);
            rv_MultipleSelection.setAdapter(multipleSelectionAdapter);

            for (int i = 0; i < multipleSelections9.size(); i++) {
                if (multipleSelections9.get(i).getId().equals(wifiArrayList.get(0).getW09())) {
                    multipleSelections9.get(i).setSelected(true);
                } else {
                    multipleSelections9.get(i).setSelected(false);
                }
                multipleSelectionAdapter.notifyDataSetChanged();
            }
        }

        btn_Cancel_Selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetCounter(1);
                alertview_selection.dismiss();
            }
        });

        btn_Save_Selection.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {
                ResetCounter(1);
                if (type.equals("SECURITY MODE")) {
                    // Multiple Selection
                    String command = "";

                    for (int i = 0; i < multipleSelections7.size(); i++) {
                        if (multipleSelections7.get(i).isSelected()) {
                            command = Command + "=" + multipleSelections7.get(i).getId();

                            Utility.Log(TAG, "Sending ==> " + command);

                            txt_SECURITY_MODE.setText(multipleSelections7.get(i).getName());

                            prefManager.setSendCommandS(command);

                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);
                        }
                    }

                    alertview_selection.dismiss();

                    ResetCounter(1);

                } else if (type.equals("DHCP")) {
                    // Multiple Selection
                    String command = "";

                    for (int i = 0; i < multipleSelections8.size(); i++) {
                        if (multipleSelections8.get(i).isSelected()) {
                            command = Command + "=" + multipleSelections8.get(i).getId();

                            Utility.Log(TAG, "Sending ==> " + command);

                            txt_DHCP.setText(multipleSelections8.get(i).getName());

                            prefManager.setSendCommandS(command);

                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);
                            if (multipleSelections8.get(i).getName().equals("STATIC")) {
//                                txt_IP_ADDRESS.setEnabled(false);
//                                txt_SUBNET.setEnabled(false);
//                                txt_DEFAULT_GATEWAY.setEnabled(false);
//                                txt_DNS_SERVER.setEnabled(false);
//
//                                txt_IP_ADDRESS.setTextColor(getResources().getColor(R.color.txt_skyblue));
//                                txt_SUBNET.setTextColor(getResources().getColor(R.color.txt_skyblue));
//                                txt_DEFAULT_GATEWAY.setTextColor(getResources().getColor(R.color.txt_skyblue));
//                                txt_DNS_SERVER.setTextColor(getResources().getColor(R.color.txt_skyblue));

                                txt_IP_ADDRESS.setEnabled(true);
                                txt_SUBNET.setEnabled(true);
                                txt_DEFAULT_GATEWAY.setEnabled(true);
                                txt_DNS_SERVER.setEnabled(true);

                                txt_IP_ADDRESS.setTextColor(getResources().getColor(R.color.white));
                                txt_SUBNET.setTextColor(getResources().getColor(R.color.white));
                                txt_DEFAULT_GATEWAY.setTextColor(getResources().getColor(R.color.white));
                                txt_DNS_SERVER.setTextColor(getResources().getColor(R.color.white));

                            } else {
//                                txt_IP_ADDRESS.setEnabled(true);
//                                txt_SUBNET.setEnabled(true);
//                                txt_DEFAULT_GATEWAY.setEnabled(true);
//                                txt_DNS_SERVER.setEnabled(true);
//
//                                txt_IP_ADDRESS.setTextColor(getResources().getColor(R.color.white));
//                                txt_SUBNET.setTextColor(getResources().getColor(R.color.white));
//                                txt_DEFAULT_GATEWAY.setTextColor(getResources().getColor(R.color.white));
//                                txt_DNS_SERVER.setTextColor(getResources().getColor(R.color.white));

                                txt_IP_ADDRESS.setEnabled(false);
                                txt_SUBNET.setEnabled(false);
                                txt_DEFAULT_GATEWAY.setEnabled(false);
                                txt_DNS_SERVER.setEnabled(false);

                                txt_IP_ADDRESS.setTextColor(getResources().getColor(R.color.txt_skyblue));
                                txt_SUBNET.setTextColor(getResources().getColor(R.color.txt_skyblue));
                                txt_DEFAULT_GATEWAY.setTextColor(getResources().getColor(R.color.txt_skyblue));
                                txt_DNS_SERVER.setTextColor(getResources().getColor(R.color.txt_skyblue));
                            }
                        }
                    }

                    alertview_selection.dismiss();

                    ResetCounter(1);

                } else if (type.equals("COUNTRY CODE")) {
                    // Multiple Selection
                    String command = "";

                    for (int i = 0; i < multipleSelections9.size(); i++) {
                        if (multipleSelections9.get(i).isSelected()) {
                            command = Command + "=" + multipleSelections9.get(i).getId();

                            Utility.Log(TAG, "Sending ==> " + command);

                            txt_COUNTRY_CODE.setText(multipleSelections9.get(i).getName());

                            prefManager.setSendCommandS(command);

                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);
                        }
                    }

                    alertview_selection.dismiss();

                    ResetCounter(1);
                }
            }
        });

    }

    // 10 Second Progress DailogBox For Send W13 Command on wifi Screen when Click on (Apply Change) Button
    public void ProgressDialogBox() {
        Utility.showProgress(act, "Please Wait........");
        mProgressTimer.start();
    }

    // 10 Second Timer For Shownig Progress DialogBox
    CountDownTimer mProgressTimer = new CountDownTimer(10000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            if (!CodeReUse.isBolwerAdmin) {
                Utility.dismissProgress();
            }
        }
    };

    // CommunicationSettingAlertDailogBox AlertDailogBox
    public void CommunicationSettingAlertDialogBox(final String typr, final TextView textView, final String Command) {

        ResetCounter(1);

        alertview_selection = new Dialog(act);
        alertview_selection.setCancelable(false);
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);
        alertview_selection.getWindow().setLayout(width, height);

        alertview_selection.setContentView(R.layout.alertview_edittext_layout); // EditText Screen
        alertview_selection.show();

        txt_Title_alartview_box = alertview_selection.findViewById(R.id.txt_Title_alartview_box);
        edit_EnterTxt_alartview_box = alertview_selection.findViewById(R.id.edit_EnterTxt_alartview_box);
        btn_Cancel_alartview_box = alertview_selection.findViewById(R.id.btn_Cancel_alartview_box);
        btn_Save_alartview_box = alertview_selection.findViewById(R.id.btn_Save_alartview_box);

        txt_Title_alartview_box.setText(typr);

        btn_Cancel_alartview_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetCounter(1);
                alertview_selection.dismiss();
            }
        });

        edit_EnterTxt_alartview_box.requestFocus();

        btn_Save_alartview_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (typr.equals("NETWORK SSID")) {
                    if (edit_EnterTxt_alartview_box.getText().toString().contains("&") || edit_EnterTxt_alartview_box.getText().toString().contains("$") || edit_EnterTxt_alartview_box.getText().toString().contains(" ")) {
                        Utility.ShowMessage(act, "Warning!", "The NETWORK SSID do NOT support the following characters: \"SPACE\", \"$\", \"&\".", "OK");
                    } else {
                        if (edit_EnterTxt_alartview_box.getText().toString().length() == 0) {
                            Utility.ShowMessage(act, "Warning!", "Please Enter Vaild Text", "OK");
                        } else {
                            textView.setText(edit_EnterTxt_alartview_box.getText().toString());

                            String command = Command + "=" + edit_EnterTxt_alartview_box.getText().toString();

                            Utility.Log(TAG, "Sending Command ==> " + command);

//                            txt_NETWORK_SSID.setText(edit_EnterTxt_alartview_box.getText().toString());

                            prefManager.setSendCommandS(command);

                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);

                            alertview_selection.dismiss();

                            ResetCounter(1);
                        }
                    }
                } else if (typr.equals("SECURITY KEY")) {
                    if (edit_EnterTxt_alartview_box.getText().toString().contains("&") || edit_EnterTxt_alartview_box.getText().toString().contains("$") || edit_EnterTxt_alartview_box.getText().toString().contains(" ")) {
                        Utility.ShowMessage(act, "Warning!", "The SECURITY KEY do NOT support the following characters: \"SPACE\", \"$\", \"&\".", "OK");
                    } else {
                        if (edit_EnterTxt_alartview_box.getText().toString().length() == 0) {
                            Utility.ShowMessage(act, "Warning!", "Please Enter Vaild Text", "OK");
                        } else {
                            textView.setText(edit_EnterTxt_alartview_box.getText().toString());

                            String command = Command + "=" + edit_EnterTxt_alartview_box.getText().toString();

                            Utility.Log(TAG, "Sending Command ==> " + command);

//                            txt_SECURITY_KEY.setText(edit_EnterTxt_alartview_box.getText().toString());

                            prefManager.setSendCommandS(command);

                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);

                            alertview_selection.dismiss();

                            ResetCounter(1);
                        }
                    }
                } else {
                    if (edit_EnterTxt_alartview_box.getText().toString().length() == 0) {
                        Utility.ShowMessage(act, "Warning!", "Please Enter Vaild Text", "OK");
                    } else {
                        textView.setText(edit_EnterTxt_alartview_box.getText().toString());

                        String command = Command + "=" + edit_EnterTxt_alartview_box.getText().toString();

                        Utility.Log(TAG, "Sending Command ==> " + command);

//                        txt_IP_ADDRESS.setText(edit_EnterTxt_alartview_box.getText().toString());

                        prefManager.setSendCommandS(command);

                        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);

                        alertview_selection.dismiss();

                        ResetCounter(1);
                    }
                }
            }
        });

    }

    // ShowEditTextDialogToUpdateValue
    public void ShowEditTextDialogToUpdateValueBox(final String type, final TextView textView, final String Command) {

        ResetCounter(1);

        Utility.hideNavigationBar(act);
        alertview_dialog_for_s_command = new Dialog(act);
        alertview_dialog_for_s_command.setCancelable(false);
//        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
//        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);
//        alertview_dialog_for_s_command.getWindow().setLayout(width, height);

        alertview_dialog_for_s_command.setContentView(R.layout.alertview_edittext_layout_s022_to_s025); // EditText Screen
        alertview_dialog_for_s_command.getWindow().setGravity(Gravity.TOP);
//        alertview_selection.show();

        txt_Title_alartview_box = alertview_dialog_for_s_command.findViewById(R.id.txt_Title_alartview_box);
        edit_EnterTxt_alartview_box_max = alertview_dialog_for_s_command.findViewById(R.id.edit_EnterTxt_alartview_box_max);
        edit_EnterTxt_alartview_box_min = alertview_dialog_for_s_command.findViewById(R.id.edit_EnterTxt_alartview_box_min);
        btn_Ok_alartview_box = alertview_dialog_for_s_command.findViewById(R.id.btn_Save_alartview_box_report);
        btn_Cancel_alartview_box = alertview_dialog_for_s_command.findViewById(R.id.btn_Cancel_alartview_box_report);

        txt_Title_alartview_box.setText(type);

        btn_Cancel_alartview_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (alertview_dialog_for_s_command != null && alertview_dialog_for_s_command.isShowing()) {
                    alertview_dialog_for_s_command.dismiss();
                }
            }
        });

        edit_EnterTxt_alartview_box_min.requestFocus();
//        edit_EnterTxt_alartview_box_max.requestFocus();

        btn_Ok_alartview_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals("Temp")) {
                    if (TextUtils.isEmpty(edit_EnterTxt_alartview_box_max.getText().toString().trim()) && TextUtils.isEmpty(edit_EnterTxt_alartview_box_min.getText().toString().trim())) {
                        Toast.makeText(act, "Please enter Min/Max value", Toast.LENGTH_SHORT).show();
                    } else {
                        if (edit_EnterTxt_alartview_box_max.getText().toString().length() == 0 && edit_EnterTxt_alartview_box_min.getText().toString().length() == 0) {
                            Utility.ShowMessage(act, "Warning!", "Please enter vaild text", "OK");
                        } else {
                            textView.setText(edit_EnterTxt_alartview_box_max.getText().toString() + "/" + edit_EnterTxt_alartview_box_min.getText().toString());

                            String commandNew = responseHandler.stringToHex(edit_EnterTxt_alartview_box_max.getText().toString(), true) +
                                    responseHandler.stringToHex(edit_EnterTxt_alartview_box_min.getText().toString(), true);

                            String command = Command + "=" + commandNew.toUpperCase();

                            Utility.Log(TAG, "Sending Command ==> " + command);

                            prefManager.setSendCommandS(command);

                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);

                            alertview_dialog_for_s_command.dismiss();

                            ResetCounter(1);

                        }
                    }
                } else if (type.equals("Humidity")) {
                    if (TextUtils.isEmpty(edit_EnterTxt_alartview_box_max.getText().toString().trim()) && TextUtils.isEmpty(edit_EnterTxt_alartview_box_min.getText().toString().trim())) {
                        Toast.makeText(act, "Please enter Min/Max value", Toast.LENGTH_SHORT).show();
                    } else {
                        if (edit_EnterTxt_alartview_box_max.getText().toString().length() == 0 && edit_EnterTxt_alartview_box_min.getText().toString().length() == 0) {
                            Utility.ShowMessage(act, "Warning!", "Please enter vaild text", "OK");
                        } else {
                            textView.setText(edit_EnterTxt_alartview_box_max.getText().toString() + "/" + edit_EnterTxt_alartview_box_min.getText().toString());

                            String commandNew = responseHandler.stringToHex(edit_EnterTxt_alartview_box_max.getText().toString(), true) +
                                    responseHandler.stringToHex(edit_EnterTxt_alartview_box_min.getText().toString(), true);
                            String command = Command + "=" + commandNew.toUpperCase();

                            Utility.Log(TAG, "Sending Command ==> " + command);

                            prefManager.setSendCommandS(command);

                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);

                            alertview_dialog_for_s_command.dismiss();

                            ResetCounter(1);

                        }
                    }
                } else if (type.equals("Exhaust Temp")) {
                    if (TextUtils.isEmpty(edit_EnterTxt_alartview_box_max.getText().toString().trim()) && TextUtils.isEmpty(edit_EnterTxt_alartview_box_min.getText().toString().trim())) {
                        Toast.makeText(act, "Please enter Min/Max value", Toast.LENGTH_SHORT).show();
                    } else {
                        if (edit_EnterTxt_alartview_box_max.getText().toString().length() == 0 && edit_EnterTxt_alartview_box_min.getText().toString().length() == 0) {
                            Utility.ShowMessage(act, "Warning!", "Please enter vaild text", "OK");
                        } else {
                            textView.setText(edit_EnterTxt_alartview_box_max.getText().toString() + "/" + edit_EnterTxt_alartview_box_min.getText().toString());

                            String commandNew = responseHandler.stringToHex(edit_EnterTxt_alartview_box_max.getText().toString(), true) +
                                    responseHandler.stringToHex(edit_EnterTxt_alartview_box_min.getText().toString(), true);
                            String command = Command + "=" + commandNew.toUpperCase();

                            Utility.Log(TAG, "Sending Command ==> " + command);

                            prefManager.setSendCommandS(command);

                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);

                            alertview_dialog_for_s_command.dismiss();

                            ResetCounter(1);

                        }
                    }
                } else if (type.equals("Exhaust Humidity")) {
                    if (TextUtils.isEmpty(edit_EnterTxt_alartview_box_max.getText().toString().trim()) && TextUtils.isEmpty(edit_EnterTxt_alartview_box_min.getText().toString().trim())) {
                        Toast.makeText(act, "Please enter Min/Max value", Toast.LENGTH_SHORT).show();
                    } else {
                        if (edit_EnterTxt_alartview_box_max.getText().toString().length() == 0 && edit_EnterTxt_alartview_box_min.getText().toString().length() == 0) {
                            Utility.ShowMessage(act, "Warning!", "Please enter vaild text", "OK");
                        } else {
                            textView.setText(edit_EnterTxt_alartview_box_max.getText().toString() + "/" + edit_EnterTxt_alartview_box_min.getText().toString());

                            String commandNew = responseHandler.stringToHex(edit_EnterTxt_alartview_box_max.getText().toString(), true) +
                                    responseHandler.stringToHex(edit_EnterTxt_alartview_box_min.getText().toString(), true);
                            String command = Command + "=" + commandNew.toUpperCase();

                            Utility.Log(TAG, "Sending Command ==> " + command);

                            prefManager.setSendCommandS(command);

                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);

                            alertview_dialog_for_s_command.dismiss();

                            ResetCounter(1);

                        }
                    }
                }
            }
        });

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // show dialog here
                if (!isFinishing()) {
                    alertview_dialog_for_s_command.show();
                }
            }
        });

    }

    // UnitSettingActivity AlertDailogBox
    public void UnitSettingAlertDialogBox(final Dialog alertview_selection, final String type) {
        if (type.equals("txt_S01_Y0"))
            alertview_selection.setContentView(R.layout.alertview_multiple_selection_layout); // Multiple Selection
        else if (type.equals("txt_S01_Y1"))
            alertview_selection.setContentView(R.layout.alertview_multiple_selection_layout); // Multiple Selection
        else if (type.equals("txt_S01_Y2"))
            alertview_selection.setContentView(R.layout.alertview_multiple_selection_layout); // Multiple Selection

        alertview_selection.show();

        txt_dailogTitle = alertview_selection.findViewById(R.id.txt_dailogTitle);

        if (type.equals("txt_S01_Y0")) {
            txt_dailogTitle.setText("Temperature Units");

            getMultipleSelections("txt_S01_Y0");

            // set up the RecyclerView
            rv_MultipleSelection = alertview_selection.findViewById(R.id.rv_MultipleSelection);
            rv_MultipleSelection.setLayoutManager(new GridLayoutManager(this, 2));

            multipleSelectionAdapter = new MultipleSelectionAdapter(act, multipleSelections0);
            rv_MultipleSelection.setAdapter(multipleSelectionAdapter);

            for (int i = 0; i < multipleSelections0.size(); i++) {
                String S01_Y0 = String.valueOf(responseHandler.hexToBinary(setpointArrayList.get(0).getS01()).charAt(11));
                if (multipleSelections0.get(i).getId().equals(S01_Y0)) {
                    multipleSelections0.get(i).setSelected(true);
                    multipleSelectionAdapter.notifyDataSetChanged();
                }
            }

        } else if (type.equals("txt_S01_Y1")) {
            txt_dailogTitle.setText("Pressure Units");

            getMultipleSelections("txt_S01_Y1");

            // set up the RecyclerView
            rv_MultipleSelection = alertview_selection.findViewById(R.id.rv_MultipleSelection);
            rv_MultipleSelection.setLayoutManager(new GridLayoutManager(this, 2));

            multipleSelectionAdapter = new MultipleSelectionAdapter(act, multipleSelections1);
            rv_MultipleSelection.setAdapter(multipleSelectionAdapter);

            for (int i = 0; i < multipleSelections1.size(); i++) {
                String S01_Y1 = String.valueOf(responseHandler.hexToBinary(setpointArrayList.get(0).getS01()).charAt(10));
                if (multipleSelections1.get(i).getId().equals(S01_Y1)) {
                    multipleSelections1.get(i).setSelected(true);
                    multipleSelectionAdapter.notifyDataSetChanged();
                }
            }

        } else if (type.equals("txt_S01_Y2")) {
            txt_dailogTitle.setText("Airflow Units");

            getMultipleSelections("txt_S01_Y2");

            // set up the RecyclerView
            rv_MultipleSelection = alertview_selection.findViewById(R.id.rv_MultipleSelection);
            rv_MultipleSelection.setLayoutManager(new GridLayoutManager(this, 2));

            multipleSelectionAdapter = new MultipleSelectionAdapter(act, multipleSelections2);
            rv_MultipleSelection.setAdapter(multipleSelectionAdapter);

            for (int i = 0; i < multipleSelections2.size(); i++) {
                String S01_Y2 = String.valueOf(responseHandler.hexToBinary(setpointArrayList.get(0).getS01()).charAt(9));
                if (multipleSelections2.get(i).getId().equals(S01_Y2)) {
                    multipleSelections2.get(i).setSelected(true);
                    multipleSelectionAdapter.notifyDataSetChanged();
                }
            }

        }

        btn_Cancel_Selection = alertview_selection.findViewById(R.id.btn_Cancel_Selection);
        btn_Save_Selection = alertview_selection.findViewById(R.id.btn_Save_Selection);

        btn_Cancel_Selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetCounter(1);
                alertview_selection.dismiss();
            }
        });

        btn_Save_Selection.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {
                ResetCounter(1);
                if (type.equals("txt_S01_Y0")) {

//                    Log.e("type","type-Y0");
                    ShowExportAndDeleteCommunicationDialog("All existing data will be deleted after keeping a report copy. Are you sure want to continue?", "Yes", "No", type);
                    alertview_selection.dismiss();
//                    // Multiple Selection Temp
//                    String bin = responseHandler.hexToBinary(setpointArrayList.get(0).getS01());
//                    Utility.Log(TAG, "hexToBinary => " + bin);
//                    String command = "";
//
//                    for (int i = 0; i < multipleSelections0.size(); i++) {
//                        if (multipleSelections0.get(i).isSelected()) {
//                            String S01 = responseHandler.binaryToHex(bin.substring(0, 13).concat(multipleSelections0.get(i).getId())).toUpperCase();
//                            S01 = responseHandler.binaryToHex(bin.substring(0, 11).concat(multipleSelections0.get(i).getId()).concat(bin.substring(12))).toUpperCase();
//
//                            if (S01.length() == 1)
//                                S01 = "000" + S01;
//                            else if (S01.length() == 2)
//                                S01 = "00" + S01;
//                            else if (S01.length() == 3)
//                                S01 = "0" + S01;
//
//                            command = "S01=" + S01;
//                        }
//                    }
//
//                    Utility.Log(TAG, "Sending S01 ==> " + command);
//
//                    prefManager.setSendCommandS(command);
//
//                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);
//
//                    alertview_selection.dismiss();

                } else if (type.equals("txt_S01_Y1")) {
//                    Log.e("type","type-Y1");
                    ShowExportAndDeleteCommunicationDialog("All existing data will be deleted after keeping a report copy. Are you sure want to continue?", "Yes", "No", type);
                    alertview_selection.dismiss();
//                    // Multiple Selection Pressure
//                    String bin = responseHandler.hexToBinary(setpointArrayList.get(0).getS01());
//                    Utility.Log(TAG, "hexToBinary => " + bin);
//                    String command = "";
//
//                    for (int i = 0; i < multipleSelections1.size(); i++) {
//                        if (multipleSelections1.get(i).isSelected()) {
//                            String S01 = responseHandler.binaryToHex(bin.substring(0, 13).concat(multipleSelections1.get(i).getId())).toUpperCase();
//                            S01 = responseHandler.binaryToHex(bin.substring(0, 10).concat(multipleSelections1.get(i).getId()).concat(bin.substring(11))).toUpperCase();
//
//                            if (S01.length() == 1)
//                                S01 = "000" + S01;
//                            else if (S01.length() == 2)
//                                S01 = "00" + S01;
//                            else if (S01.length() == 3)
//                                S01 = "0" + S01;
//
//                            command = "S01=" + S01;
//                        }
//                    }
//
//                    Utility.Log(TAG, "Sending S01 ==> " + command);
//
//                    prefManager.setSendCommandS(command);
//
//                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);
//
//                    alertview_selection.dismiss();
                } else if (type.equals("txt_S01_Y2")) {
//                    Log.e("type","type-Y2");
                    ShowExportAndDeleteCommunicationDialog("All existing data will be deleted after keeping a report copy. Are you sure want to continue?", "Yes", "No", type);
                    alertview_selection.dismiss();
//                    // Multiple Selection AirFlow
//                    String bin = responseHandler.hexToBinary(setpointArrayList.get(0).getS01());
//                    Utility.Log(TAG, "hexToBinary => " + bin);
//                    String command = "";
//
//                    for (int i = 0; i < multipleSelections2.size(); i++) {
//                        if (multipleSelections2.get(i).isSelected()) {
//                            String S01 = responseHandler.binaryToHex(bin.substring(0, 13).concat(multipleSelections2.get(i).getId())).toUpperCase();
//                            S01 = responseHandler.binaryToHex(bin.substring(0, 9).concat(multipleSelections2.get(i).getId()).concat(bin.substring(10))).toUpperCase();
//
//                            if (S01.length() == 1)
//                                S01 = "000" + S01;
//                            else if (S01.length() == 2)
//                                S01 = "00" + S01;
//                            else if (S01.length() == 3)
//                                S01 = "0" + S01;
//
//                            command = "S01=" + S01;
//                        }
//                    }
//
//                    Utility.Log(TAG, "Sending S01 ==> " + command);
//
//                    prefManager.setSendCommandS(command);
//
//                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);
//
//                    alertview_selection.dismiss();
                }
            }
        });
    }

    // Export and Delete Record AlertDialogBox
    public void ShowExportAndDeleteCommunicationDialog(final String title, String btn1, String btn2, String type) {

        ResetCounter(1);

        alertView_Export_Delete_Record = new Dialog(act);

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);

        alertView_Export_Delete_Record.getWindow().setLayout(width, height);

        alertView_Export_Delete_Record.setCancelable(true);
        alertView_Export_Delete_Record.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        alertView_Export_Delete_Record.setContentView(R.layout.alertview_export_and_delete_layout); // Wifi Communication

        alertView_Export_Delete_Record.show();

        txt_dailogMassage = alertView_Export_Delete_Record.findViewById(R.id.txt_dailogMassage);
        btn_dailogTitle1 = alertView_Export_Delete_Record.findViewById(R.id.btn_dailogTitle1);
        btn_dailogTitle2 = alertView_Export_Delete_Record.findViewById(R.id.btn_dailogTitle2);
        layout_dailog_timer = alertView_Export_Delete_Record.findViewById(R.id.layout_dailog_timer);
        linear_layout_dialog_buttons = alertView_Export_Delete_Record.findViewById(R.id.linear_layout_dialog_buttons);
        txt_dailog_timmer = alertView_Export_Delete_Record.findViewById(R.id.txt_dailog_timmer);

        txt_dailogMassage.setText(title);
        btn_dailogTitle1.setText(btn1);
        btn_dailogTitle2.setText(btn2);

        btn_dailogTitle1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.ShowMessageReport(act, "Please wait while we are exporting old data...");
                final Runnable r = new Runnable() {
                    public void run() {
                        ExportReportWhenUnitChange(type);
                    }
                };
                mHandler.postDelayed(r, 5000);
                alertView_Export_Delete_Record.dismiss();
                try {
                    responseHandler.resetFAndSDataForBlower_Api();
                } catch (JSONException e) {
                    Log.e("TAG", e.getMessage());
                }

            }
        });

        btn_dailogTitle2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {
                alertView_Export_Delete_Record.dismiss();
            }
        });

    }

    // Process to Export Old data
    public void ExportReportWhenUnitChange(String type) {
        try {
            String mStartDate = Utility.getCurrentTimeStamp();
            String mEndDate = Utility.getCurrentTimeStamp();
//            isUSBDetected = checkUSB();
            typeUnitValue = type;
            tableViewModel = new TableViewModel(act, mStartDate, mEndDate, sqliteHelper);
            // Create TableView Adapter
            try {
                tableViewModel.csvFileExportFunction("0", true, false);
//                sqliteHelper.deleteAllRecordFromAllTable(true);

            } catch (Exception e) {
                Log.e("ErrorTableHome", e.getMessage());
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

    }

    public void sendS01CommandAfterUnitChange(String type) {
        if (type.equals("txt_S01_Y0")) {
            Log.e("type", "subtype-Y0");
            // Multiple Selection Temp
            String bin = responseHandler.hexToBinary(setpointArrayList.get(0).getS01());
            Utility.Log(TAG, "hexToBinary => " + bin);
            String command = "";

            for (int i = 0; i < multipleSelections0.size(); i++) {
                if (multipleSelections0.get(i).isSelected()) {
                    String S01 = responseHandler.binaryToHex(bin.substring(0, 13).concat(multipleSelections0.get(i).getId())).toUpperCase();
                    S01 = responseHandler.binaryToHex(bin.substring(0, 11).concat(multipleSelections0.get(i).getId()).concat(bin.substring(12))).toUpperCase();

                    if (S01.length() == 1)
                        S01 = "000" + S01;
                    else if (S01.length() == 2)
                        S01 = "00" + S01;
                    else if (S01.length() == 3)
                        S01 = "0" + S01;

                    command = "S01=" + S01;
                }
            }

            Utility.Log(TAG, "Sending S01 ==> " + command);

            prefManager.setSendCommandS(command);

            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);

            Utility.dismissAlertDialog();
            Toast.makeText(act, "Report has been saved in a directory.!", Toast.LENGTH_LONG).show();
        } else if (type.equals("txt_S01_Y1")) {
            Log.e("type", "subtype-Y1");
            // Multiple Selection Pressure
            String bin = responseHandler.hexToBinary(setpointArrayList.get(0).getS01());
            Utility.Log(TAG, "hexToBinary => " + bin);
            String command = "";

            for (int i = 0; i < multipleSelections1.size(); i++) {
                if (multipleSelections1.get(i).isSelected()) {
                    String S01 = responseHandler.binaryToHex(bin.substring(0, 13).concat(multipleSelections1.get(i).getId())).toUpperCase();
                    S01 = responseHandler.binaryToHex(bin.substring(0, 10).concat(multipleSelections1.get(i).getId()).concat(bin.substring(11))).toUpperCase();

                    if (S01.length() == 1)
                        S01 = "000" + S01;
                    else if (S01.length() == 2)
                        S01 = "00" + S01;
                    else if (S01.length() == 3)
                        S01 = "0" + S01;

                    command = "S01=" + S01;
                }
            }

            Utility.Log(TAG, "Sending S01 ==> " + command);

            prefManager.setSendCommandS(command);

            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);

            Utility.dismissAlertDialog();
            Toast.makeText(act, "Report has been saved in a directory.!", Toast.LENGTH_LONG).show();
        } else if (type.equals("txt_S01_Y2")) {
            Log.e("type", "subtype-Y2");
            // Multiple Selection AirFlow
            String bin = responseHandler.hexToBinary(setpointArrayList.get(0).getS01());
            Utility.Log(TAG, "hexToBinary => " + bin);
            String command = "";

            for (int i = 0; i < multipleSelections2.size(); i++) {
                if (multipleSelections2.get(i).isSelected()) {
                    String S01 = responseHandler.binaryToHex(bin.substring(0, 13).concat(multipleSelections2.get(i).getId())).toUpperCase();
                    S01 = responseHandler.binaryToHex(bin.substring(0, 9).concat(multipleSelections2.get(i).getId()).concat(bin.substring(10))).toUpperCase();

                    if (S01.length() == 1)
                        S01 = "000" + S01;
                    else if (S01.length() == 2)
                        S01 = "00" + S01;
                    else if (S01.length() == 3)
                        S01 = "0" + S01;

                    command = "S01=" + S01;
                }
            }

            Utility.Log(TAG, "Sending S01 ==> " + command);

            prefManager.setSendCommandS(command);

            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);

            Utility.dismissAlertDialog();
            Toast.makeText(act, "Report has been saved in a directory.!", Toast.LENGTH_LONG).show();
        }
    }

    // DeconSettingActivity AlertDailogBox
    public void DeconSettingAlertDialogBox(final Dialog alertview_selection, final String type) {
        if (type.equals("txt_S02_XX"))
            alertview_selection.setContentView(R.layout.alertview_seekbar_selection_layout); // Seekbar Selection
        else if (type.equals("txt_S02_YY"))
            alertview_selection.setContentView(R.layout.alertview_seekbar_selection_layout); // Seekbar Selection
        else if (type.equals("txt_S21_XX_YY"))
            alertview_selection.setContentView(R.layout.alertview_seekbar_selection_layout); // Seekbar Selection

        alertview_selection.show();

        txt_dailogTitle = alertview_selection.findViewById(R.id.txt_dailogTitle);

        if (type.equals("txt_S02_XX")) {
            txt_dailogTitle.setText("SUPPLY RPM");

            txt_MinSeekbar_Progress = alertview_selection.findViewById(R.id.txt_MinSeekbar_Progress);
            txt_MaxSeekbar_Progress = alertview_selection.findViewById(R.id.txt_MaxSeekbar_Progress);
            seekbar_Progress_Selection = alertview_selection.findViewById(R.id.seekbar_Progress_Selection);
            txt_SelectedSeekbar_Progress = alertview_selection.findViewById(R.id.txt_SelectedSeekbar_Progress);
            txt_Minus_Progress = alertview_selection.findViewById(R.id.txt_Minus_Progress);
            txt_Plus_Progress = alertview_selection.findViewById(R.id.txt_Plus_Progress);

            txt_MinSeekbar_Progress.setText("800");
            txt_MaxSeekbar_Progress.setText("3350");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                seekbar_Progress_Selection.setMin(0);
            }

            seekbar_Progress_Selection.setMax(255);

            int S02_XX = Integer.parseInt(responseHandler.hexToString(setpointArrayList.get(0).getS02(), true));
            seekbar_Progress_Selection.setProgress(S02_XX);
            txt_SelectedSeekbar_Progress.setText(String.valueOf((S02_XX * 10) + 800));

            seekbar_Progress_Selection.setOnSeekChangeListener(new OnSeekChangeListener() {
                @Override
                public void onSeeking(SeekParams seekParams) {
                    seekbar_Progress_Selection.setProgress(seekParams.progress);
                    txt_SelectedSeekbar_Progress.setText(String.valueOf((seekParams.progress * 10) + 800));
                }

                @Override
                public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

                }
            });

            txt_Minus_Progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResetCounter(1);
                    if (seekbar_Progress_Selection.getProgress() > 0) {
                        int setProgress = seekbar_Progress_Selection.getProgress() - 1;
                        seekbar_Progress_Selection.setProgress(setProgress);
                        txt_SelectedSeekbar_Progress.setText(String.valueOf((setProgress * 10) + 800));
                    }
                }
            });

            txt_Plus_Progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResetCounter(1);
                    if (seekbar_Progress_Selection.getProgress() < 255) {
                        int setProgress = seekbar_Progress_Selection.getProgress() + 1;
                        seekbar_Progress_Selection.setProgress(setProgress);
                        txt_SelectedSeekbar_Progress.setText(String.valueOf((setProgress * 10) + 800));
                    }
                }
            });
        } else if (type.equals("txt_S02_YY")) {
            txt_dailogTitle.setText("Blower RPM");

            txt_MinSeekbar_Progress = alertview_selection.findViewById(R.id.txt_MinSeekbar_Progress);
            txt_MaxSeekbar_Progress = alertview_selection.findViewById(R.id.txt_MaxSeekbar_Progress);
            seekbar_Progress_Selection = alertview_selection.findViewById(R.id.seekbar_Progress_Selection);
            txt_SelectedSeekbar_Progress = alertview_selection.findViewById(R.id.txt_SelectedSeekbar_Progress);
            txt_Minus_Progress = alertview_selection.findViewById(R.id.txt_Minus_Progress);
            txt_Plus_Progress = alertview_selection.findViewById(R.id.txt_Plus_Progress);

            txt_MinSeekbar_Progress.setText("800");
            txt_MaxSeekbar_Progress.setText("3350");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                seekbar_Progress_Selection.setMin(0);
            }

            seekbar_Progress_Selection.setMax(255);

            int S02_YY = Integer.parseInt(responseHandler.hexToString(setpointArrayList.get(0).getS02(), false));
            seekbar_Progress_Selection.setProgress(S02_YY);
            txt_SelectedSeekbar_Progress.setText(String.valueOf((S02_YY * 10) + 800));

            seekbar_Progress_Selection.setOnSeekChangeListener(new OnSeekChangeListener() {
                @Override
                public void onSeeking(SeekParams seekParams) {
                    seekbar_Progress_Selection.setProgress(seekParams.progress);
                    txt_SelectedSeekbar_Progress.setText(String.valueOf((seekParams.progress * 10) + 800));
                }

                @Override
                public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

                }
            });

            txt_Minus_Progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResetCounter(1);
                    if (seekbar_Progress_Selection.getProgress() > 0) {
                        int setProgress = seekbar_Progress_Selection.getProgress() - 1;
                        seekbar_Progress_Selection.setProgress(setProgress);
                        txt_SelectedSeekbar_Progress.setText(String.valueOf((setProgress * 10) + 800));
                    }
                }
            });

            txt_Plus_Progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResetCounter(1);
                    if (seekbar_Progress_Selection.getProgress() < 255) {
                        int setProgress = seekbar_Progress_Selection.getProgress() + 1;
                        seekbar_Progress_Selection.setProgress(setProgress);
                        txt_SelectedSeekbar_Progress.setText(String.valueOf((setProgress * 10) + 800));
                    }
                }
            });
        } else if (type.equals("txt_S21_XX_YY")) {
            txt_dailogTitle.setText("Decon Duration (hh:mm)");

            txt_MinSeekbar_Progress = alertview_selection.findViewById(R.id.txt_MinSeekbar_Progress);
            txt_MaxSeekbar_Progress = alertview_selection.findViewById(R.id.txt_MaxSeekbar_Progress);
            seekbar_Progress_Selection = alertview_selection.findViewById(R.id.seekbar_Progress_Selection);
            txt_SelectedSeekbar_Progress = alertview_selection.findViewById(R.id.txt_SelectedSeekbar_Progress);
            txt_Minus_Progress = alertview_selection.findViewById(R.id.txt_Minus_Progress);
            txt_Plus_Progress = alertview_selection.findViewById(R.id.txt_Plus_Progress);

            txt_MinSeekbar_Progress.setText("00:00");
            txt_MaxSeekbar_Progress.setText("01:59");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                seekbar_Progress_Selection.setMin(responseHandler.strToMilli("00:00"));
            }

            seekbar_Progress_Selection.setMax(responseHandler.strToMilli("01:59"));

            String decontime = setpointArrayList.get(0).getS21();
            Integer hours = Integer.parseInt(decontime.substring(0, 2), 16);
            Integer minutes = Integer.parseInt(decontime.substring(2), 16);

            String hrs = "0" + hours.toString();
            String mins = "";
            if (minutes < 10)
                mins = "0" + minutes.toString();
            else
                mins = minutes.toString();

            seekbar_Progress_Selection.setProgress(responseHandler.strToMilli(hrs + ":" + mins));

            txt_SelectedSeekbar_Progress.setText(String.format(hrs + ":" + mins));

            seekbar_Progress_Selection.setOnSeekChangeListener(new OnSeekChangeListener() {
                @Override
                public void onSeeking(SeekParams seekParams) {
                    seekbar_Progress_Selection.setProgress(seekParams.progress);
                    String progress = responseHandler.milliToStr(seekParams.progress);
                    txt_SelectedSeekbar_Progress.setText(progress);
                }

                @Override
                public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

                }
            });

            txt_Minus_Progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResetCounter(1);
                    if (seekbar_Progress_Selection.getProgress() > responseHandler.strToMilli("00:00")) {
                        int setProgress = seekbar_Progress_Selection.getProgress() - 60;
                        seekbar_Progress_Selection.setProgress(setProgress);
                        String progress = responseHandler.milliToStr(seekbar_Progress_Selection.getProgress());
                        txt_SelectedSeekbar_Progress.setText(progress);
                    }
                }
            });

            txt_Plus_Progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResetCounter(1);
                    if (seekbar_Progress_Selection.getProgress() < responseHandler.strToMilli("01:59")) {
                        int setProgress = seekbar_Progress_Selection.getProgress() + 60;
                        seekbar_Progress_Selection.setProgress(setProgress);
                        String progress = responseHandler.milliToStr(seekbar_Progress_Selection.getProgress());
                        txt_SelectedSeekbar_Progress.setText(progress);
                    }
                }
            });
        }

        btn_Cancel_Selection = alertview_selection.findViewById(R.id.btn_Cancel_Selection);
        btn_Save_Selection = alertview_selection.findViewById(R.id.btn_Save_Selection);

        btn_Cancel_Selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetCounter(1);
                alertview_selection.dismiss();
            }
        });

        btn_Save_Selection.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {
                ResetCounter(1);
                if (type.equals("txt_S02_XX")) {
                    // Seekbar Selection
                    int S02XX = (Integer.parseInt(txt_SelectedSeekbar_Progress.getText().toString()) - 800) / 10;
                    String S02_XX = responseHandler.stringToHex(String.valueOf(S02XX), true);
                    String S02_YY = responseHandler.getHexString(setpointArrayList.get(0).getS02(), false);

                    String command = "S02=" + S02_XX.concat(S02_YY).toUpperCase();

                    Utility.Log(TAG, "Sending S11_XX ==> " + command);

                    prefManager.setSendCommandS(command);

                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);

                    alertview_selection.dismiss();
                }
                if (type.equals("txt_S02_YY")) {
                    // Seekbar Selection
                    int S02YY = (Integer.parseInt(txt_SelectedSeekbar_Progress.getText().toString()) - 800) / 10;
                    String S02_YY = responseHandler.stringToHex(String.valueOf(S02YY), true);
                    String S02_XX = responseHandler.getHexString(setpointArrayList.get(0).getS02(), true);

                    String command = "S02=" + S02_XX.concat(S02_YY).toUpperCase();

                    Utility.Log(TAG, "Sending S02_YY ==> " + command);

                    prefManager.setSendCommandS(command);

                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);

                    alertview_selection.dismiss();
                } else if (type.equals("txt_S21_XX_YY")) {
                    // Seekbar Selection
                    String[] S21XXYY = txt_SelectedSeekbar_Progress.getText().toString().split(":");
                    String S21_XX = responseHandler.stringToHex(S21XXYY[0], true);
                    String S21_YY = responseHandler.stringToHex(S21XXYY[1], true);

                    String command = "S21=" + S21_XX.concat(S21_YY).toUpperCase();

                    Utility.Log(TAG, "Sending S21_XXYY ==> " + command);

                    prefManager.setSendCommandS(command);

                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommandOnly);

                    alertview_selection.dismiss();
                }
            }
        });
    }

    // DiagnosticsSettingActivity AlertDailogBox
    public void DiagnosticsSettingAlertDialogBox() {

        ResetCounter(1);

        alertview_diagnostics.setCancelable(false);
        alertview_diagnostics.setContentView(R.layout.alertview_diagnostics_layout); // Diagnostics Setting Screen
        alertview_diagnostics.show();

        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nDiagnosticsDataStart);

        txt_D22_XXXX = alertview_diagnostics.findViewById(R.id.txt_D22_XXXX);
        txt_D23_XXXX = alertview_diagnostics.findViewById(R.id.txt_D23_XXXX);
        txt_D24_XXXX = alertview_diagnostics.findViewById(R.id.txt_D24_XXXX);
        txt_D25_XXXX = alertview_diagnostics.findViewById(R.id.txt_D25_XXXX);
        txt_D26_XXXX = alertview_diagnostics.findViewById(R.id.txt_D26_XXXX);
        txt_D27_XXXX = alertview_diagnostics.findViewById(R.id.txt_D27_XXXX);
        txt_D28_XXXX = alertview_diagnostics.findViewById(R.id.txt_D28_XXXX);
        txt_D29_XXXX = alertview_diagnostics.findViewById(R.id.txt_D29_XXXX);
        txt_D30_XXXX = alertview_diagnostics.findViewById(R.id.txt_D30_XXXX);
        txt_D25_D24_XXXX = alertview_diagnostics.findViewById(R.id.txt_D25_D24_XXXX);
        txt_D29_D28_XXXX = alertview_diagnostics.findViewById(R.id.txt_D29_D28_XXXX);
        txt_D31 = alertview_diagnostics.findViewById(R.id.txt_D31);
        mProgressBar = alertview_diagnostics.findViewById(R.id.ProgressBar);
        mRelativeProgressBarLayoutDialog = alertview_diagnostics.findViewById(R.id.relative_progress_dialog);

        // Read And Set AllData
        setAnalysisData();

        txt_D31.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRelativeProgressBarLayoutDialog.setVisibility(View.VISIBLE);
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStopService);
                Utility.setSoftInputAlwaysHide(act);
                /* CallReadWriteFuncation("D31", 0); // D31*/

                /*if (alertview_diagnostics.isShowing()) {
//                    alertview_diagnostics.dismiss();
                    txt_D31.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                }*/
            }
        });

    }

    // DiagnosticsDetailsSettingActivity AlertDailogBox
    public void DiagnosticsDetailsSettingAlertDialogBox() {

        ResetCounter(1);

        alertview_diagnostics_details.setCancelable(false);
        alertview_diagnostics_details.setContentView(R.layout.alertview_diagnostics_details_layout); // Diagnostics Setting Screen
        alertview_diagnostics_details.show();

        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);

        txt_D10_XXXX = alertview_diagnostics_details.findViewById(R.id.txt_D10_XXXX);
        txt_D11_XXXX = alertview_diagnostics_details.findViewById(R.id.txt_D11_XXXX);
        txt_F16_XXXX = alertview_diagnostics_details.findViewById(R.id.txt_F16_XXXX);
        seekbar_Progress_SupplyPWMValue = alertview_diagnostics_details.findViewById(R.id.seekbar_Progress_SupplyPWMValue);
        seekbar_Progress_ExhaustPWMValue = alertview_diagnostics_details.findViewById(R.id.seekbar_Progress_ExhaustPWMValue);
        txt_F13_Z2 = alertview_diagnostics_details.findViewById(R.id.txt_F13_Z2);
        txt_diagnostics_details_close = alertview_diagnostics_details.findViewById(R.id.txt_diagnostics_details_close);
        mProgressBar = alertview_diagnostics_details.findViewById(R.id.ProgressBar);
        mRelativeProgressBarLayoutDialog = alertview_diagnostics_details.findViewById(R.id.relative_progress_dialog);


        // Read And Set AllData
        setAnalysisData();

        txt_diagnostics_details_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRelativeProgressBarLayoutDialog.setVisibility(View.VISIBLE);
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStopService);
                Utility.setSoftInputAlwaysHide(act);
//                alertview_diagnostics_details.dismiss();

            }
        });

    }

    // Commomn Exhaust Available or Not
    public Boolean isExhaustAvailable() {
        return responseHandler.hexToBinary(feedbackArrayList.get(0).getF12()).charAt(2) == '1';
    }

    // Update Funcation
    @Override
    public void update(Observable observable, Object data) {
//        Log.e(TAG,"respone HomeActivity");
        if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nReConnectNode) {
            // ServiceCommunication
            /*if (!isMyServiceRunning(MyService.class)) {*/
            if (service_myservice == null) {
                // check record on setPoint data is exits or not in database
                if (!prefManager.getOpenNode())
                    portConversion.openNode(act);
                else
                    Utility.AlertShowMessage(act, "Alert", "serial port not found.", "OK");
            }
        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nSetPointData) {
            CallReadWriteFuncation("S", -1); // S and multiple F
        }
        /*else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nSetPointDataS) {
            CallReadWriteFuncation("S", 3); // S and multiple F
        } */
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nSetPointDataOnly) {
            hasS_F_got_response = false;
            CallReadWriteFuncation("S", 0); // S
//            if(isWifiYesButtonClicked){
//                CallReadWriteFuncation("S", 0); // S
//                reDirectHomeScreenFunction(false);
//            }else{
//                CallReadWriteFuncation("S", 0); // S
//            }
        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nDiagnosticsDataWithFeedbackData) {
            if (service_diagnostic != null) {
                CallReadWriteFuncation("D", 0); // D
            } else if (alertview_diagnostics_details.isShowing()) {
                mRelativeProgressBarLayoutDialog.setVisibility(View.GONE);
                alertview_diagnostics_details.dismiss();
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nRedirectHome);

            }

        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nDiagnosticsDataOnly) {
            if (service_diagnostic != null) {
                CallReadWriteFuncation("D", -3); // D
            } else if (alertview_diagnostics.isShowing()) {
                CallReadWriteFuncation("D31", 0); // D31
            }
        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nDiagnosticsDataStart) {
            CallReadWriteFuncation("D12", 0); // D12
        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.mD31CommandResponse) {
            Log.e(TAG, "D31 Response Got");
            mRelativeProgressBarLayoutDialog.setVisibility(View.GONE);
            alertview_diagnostics.dismiss();
            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nRedirectHome);
            ResetCounter(1);
        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nFeedbackData) {
            if (service_myservice != null) {
                CallReadWriteFuncation("F", 0); // F
            }

            if (!CodeReUse.isBolwerAdmin && CodeReUse.isBolwerConnected) {
                sendMessage(new Gson().toJson(setpointArrayList), "setpointArrayList", 0);
                sendMessage(new Gson().toJson(feedbackArrayList), "feedbackArrayList", 0);
            }
        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nFeedbackDataOnly) {
            CallReadWriteFuncation("F", 2); // 2 means no need to call S command after F command in SerialPortConversion.java
        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nFeedbackDataSingleForSetting) {
            CallReadWriteFuncation("F", 3); //  F command
        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nSingle_S_F_got_response) {
            hasS_F_got_response = true;
            Log.e("F Command Response", "F Command Response Got");
//            if(isCommunicationButtonClicked){
//                isCommunicationButtonClicked = false;
//                mRelativeProgressBarLayoutSetting.setVisibility(View.GONE);
//                Log.e(TAG, "Called W Command when communication clicked");
//                CallReadWriteFuncation("W", 4);
//                ShowWifiCommunicationDialog("Continue With Application", "Wivarium", "Android Blower");
//            }
            if (isWifiYesButtonClicked) {
                isWifiYesButtonClicked = false;
                reDirectHomeScreenFunction(false);
            }
        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nSetS26OffCommand)
        {
            String command = "S26=0001";
            CallReadWriteFuncation(command, 210);
        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nSetS26OnCommand)
        {
            String command = "S26=0000";
            CallReadWriteFuncation(command, 211);

        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nSetPointCommand) {
            if (CodeReUse.isBolwerAdmin) {
                sendMessage(prefManager.getSendCommandS().trim(), "Command", -2);
            } else {
                String command = prefManager.getSendCommandS().trim();
                CallReadWriteFuncation(command, -2);
            }
        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nSetWifiCommandOnly) {

        } else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nSetPointCommandOnly) {
            if (CodeReUse.isBolwerAdmin) {
                sendMessage(prefManager.getSendCommandS().trim(), "Command", -2);
            } else {
                String command = prefManager.getSendCommandS().trim();
                if (command.startsWith("W"))
                    CallReadWriteFuncation(command, 0); // W01 = 01442 or W02 = 0524
                else
                    CallReadWriteFuncation(command, 1); // S01 = 01442 and F01 = 0524
            }
        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nSetPointCommandOnly_Api) {
            String command = prefManager.getSendCommandS().trim();
            if (command.startsWith("W"))
                CallReadWriteFuncation(command, 202); // W01 = 01442 or W02 = 0524
            else
                CallReadWriteFuncation(command, 201);
        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nSetWifiDataOnly) {
            Log.e(TAG, "Response Ok");
//            reDirectHomeScreenFunction(false);
            CallReadWriteFuncation("W", 4);
        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nSetWifiDataOnlyResponse) {
            Log.e(TAG, "nSetWifiDataOnlyResponse W command Response Successfully");
        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nStartService) {
            if (alertview_diagnostics.isShowing())
                StatusBackgroundService(true, "DiagnosticsCommandService", "alertview_diagnostics");
            else if (alertview_diagnostics_details.isShowing())
                StatusBackgroundService(true, "DiagnosticsCommandService", "alertview_diagnostics_details");
            else
                StatusBackgroundService(true, "MyService", "");
        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nStopService) {
            if (alertview_diagnostics.isShowing()) {
                StatusBackgroundService(false, "DiagnosticsCommandService", "alertview_diagnostics");
            } else if (alertview_diagnostics_details.isShowing())
                StatusBackgroundService(false, "DiagnosticsCommandService", "alertview_diagnostics_details");
            else
                StatusBackgroundService(false, "MyService", "");
        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nFeedbackDataUpdate) {
            // ReadAllData
            setAnalysisData();
        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nSetPointDataUpdate) {
            // ReadAllData
            setAnalysisData();
        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nWiFiDataUpdate) {
            Log.e(TAG, "Got W command Response Suceesfully");
            // ReadAllData
            setAnalysisData();
        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nPlus) {
            String bin = responseHandler.hexToBinary(setpointArrayList.get(0).getS01());
            Utility.Log(TAG, "hexToBinary => " + bin);

            String command = "";

            String S01 = responseHandler.binaryToHex(bin.substring(0, 12).concat("0").concat(bin.substring(13))).toUpperCase();

            if (S01.length() == 1)
                S01 = "000" + S01;
            else if (S01.length() == 2)
                S01 = "00" + S01;
            else if (S01.length() == 3)
                S01 = "0" + S01;

            command = "S01=" + S01;

            Utility.Log(TAG, "Sending S01 ==> " + command);

            prefManager.setSendCommandS(command);

            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommand);

        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nMinus) {
            String bin = responseHandler.hexToBinary(setpointArrayList.get(0).getS01());
            Utility.Log(TAG, "hexToBinary => " + bin);

            String command = "";

            String S01 = responseHandler.binaryToHex(bin.substring(0, 12).concat("1").concat(bin.substring(13))).toUpperCase();

            if (S01.length() == 1)
                S01 = "000" + S01;
            else if (S01.length() == 2)
                S01 = "00" + S01;
            else if (S01.length() == 3)
                S01 = "0" + S01;

            command = "S01=" + S01;

            Utility.Log(TAG, "Sending S01 ==> " + command);

            prefManager.setSendCommandS(command);

            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointCommand);

        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nUp_Down) {
            ShowSelectionDialog();
        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nRedirectHome) {
            NightModeView(nightModeStatus);
            // ReadAllData
            setAnalysisData();
        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nRedirectSettings) {
            bluetooth_layout.setVisibility(View.GONE);
            main_layout.setVisibility(View.GONE);
            setting_layout.setVisibility(View.VISIBLE);
            sub_setting_layout.setVisibility(View.GONE);
            report_layout.setVisibility(View.GONE);

            //AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointDataOnly);
            // ResetCounter
            ResetCounter(1);
        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nRedirectReport)
        {
            bluetooth_layout.setVisibility(View.GONE);
            main_layout.setVisibility(View.GONE);
            setting_layout.setVisibility(View.GONE);
            sub_setting_layout.setVisibility(View.GONE);
            report_layout.setVisibility(View.VISIBLE);

            //AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointDataOnly);
            // ResetCounter
            //ResetCounter(1);
        } else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nBluetoothDisconnect)
        {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(act);
            builder1.setMessage("Are you sure to disconnect devices?");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            try {
                                communicationController.stop();
                                communicationController.connectionLost();
                                responseHandler.deleteAllTablesData();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

            builder1.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        } else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nBluetoothOldDevices) {
            BluetoothConn(prefManager.getBluetoothMacAddress());
        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nDiagnosticsSettings) {
            DiagnosticsSettingAlertDialogBox();
        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nDiagnosticsDetailsSettings) {
            DiagnosticsDetailsSettingAlertDialogBox();
        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nChangePassword) {
            txt_SettingPassword.setText(prefManager.getSettingPassword());
            txt_ReportPassword.setText(prefManager.getReportPassword());
            txt_DiagnosticsPassword.setText(prefManager.getDiagnosticsPassword());
            txt_DiagnosticsDetailsPassword.setText(prefManager.getDiagnosticsDetailPassword());
            txt_BluetoothDisconnectPassword.setText(prefManager.getBluetoothDisconnectPassword());
        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nBackPressedReportAndRack) {
            ResetCounter(1);
            rackDetailsModel = sqliteHelper.getDataFromRackBlowerDetails();
            if (rackDetailsModel != null) {
                responseHandler.rackDetailsModels = rackDetailsModel;
                responseHandler.allentownBlowerApplication = allentownBlowerApplication;
                responseHandler.myDb = sqliteHelper;
            }
        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nTimerFinishedReportAndRack)
        {
            reDirectHomeScreenFunction(false);
        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nCallUnitChangeObserver) {
            sendS01CommandAfterUnitChange(typeUnitValue);
        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nRackSetUp_ACH_Value_Write_Only_From_Setting_Screen) {
            initRackModelNullOrNot();
            String achValue = String.valueOf(rackModel.getACH());
            String supplyValue = String.valueOf(rackModel.getSupplyCFM());
            //ArrayList<SetPointCommand> setpointArrayList = responseHandler.getLastSetPointData();
            //commented above line on 8/12/21 as it is not in use at all in this loop..
            String S07_YY = responseHandler.stringToHex(achValue, true);
            String S07_XX = responseHandler.stringToHex(supplyValue, true);
            String command = "S07=" + S07_XX.concat(S07_YY).toUpperCase();
            Utility.Log(TAG, "Sending S07_XXYY ==> " + command);
            CallReadWriteFuncation(command, 301);
        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nRackSetUp_Polarity_Value_Write_Only_From_Setting_Screen) {
            initRackModelNullOrNot();
            String polarityValueId = "";
            String polarityValue = rackModel.getPolarity();
            if(polarityValue.equalsIgnoreCase("+")){
                polarityValueId = "1";
            }else{
                polarityValueId = "0";
            }
            ArrayList<SetPointCommand> setpointArrayList = responseHandler.getLastSetPointData();
            //Added below while loop in case we dont get the latest setpoint data from the table..
            while (setpointArrayList.size() == 0)
            {
                setpointArrayList = responseHandler.getLastSetPointData();
            }
            String bin = responseHandler.hexToBinary(setpointArrayList.get(0).getS01());
            Utility.Log(TAG, "hexToBinary => " + bin);
            //0000111100001000 //0F08
            //change to 0F00
            String command = "";

            String S01 = responseHandler.binaryToHex(bin.substring(0, 12).concat(polarityValueId).concat(bin.substring(13))).toUpperCase();

            if (S01.length() == 1)
                S01 = "000" + S01;
            else if (S01.length() == 2)
                S01 = "00" + S01;
            else if (S01.length() == 3)
                S01 = "0" + S01;


            command = "S01=" + S01;

            Utility.Log(TAG, "Sending S01 ==> " + command);

            CallReadWriteFuncation(command, 302);
        }
//        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nRackSetUp_Supply_CFM_Value_Write_Only_From_Setting_Screen) {
//            String supplyValue = prefManager.getSupplyValue().trim();
//            String S07_XX = responseHandler.stringToHex(supplyValue, true);
//            String S07_YY = responseHandler.getHexString(setpointArrayList.get(0).getS07(), false);
//            String command = "S07=" + S07_XX.concat(S07_YY).toUpperCase();
//            Utility.Log(TAG, "Sending S07_YY ==> " + command);
//            CallReadWriteFuncation(command, 303);
//
//        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nRackSetUp_Exhaust_WC_Value_Write_Only_From_Setting_Screen) {
            initRackModelNullOrNot();
            String exhaustValue = rackModel.getExhaustWC();

            String barvalue = exhaustValue;

            int S10XYYY = (int) (Float.parseFloat(barvalue.replace("-", "")) * 1000);

            String S10_XYYY = "";
            String hexvalue = Integer.toHexString(S10XYYY);

            if (hexvalue.length() == 0)
                hexvalue = "000";
            else if (hexvalue.length() == 1)
                hexvalue = "00" + hexvalue;
            else if (hexvalue.length() == 2)
                hexvalue = "0" + hexvalue;

            Utility.Log(TAG, "Hex String value =>> " + hexvalue);
            if (barvalue.startsWith("-")) {
                S10_XYYY = "8" + hexvalue;
            } else {
                S10_XYYY = "0" + hexvalue;
            }

            String command = "S10=" + S10_XYYY.toUpperCase();

            Utility.Log(TAG, "Sending S10_XYYY ==> " + command);
            CallReadWriteFuncation(command, 303);

        }
//        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nRackSetUp_Dialog_From_Setting_Screen) {
////            Toast.makeText(act, "Success..!!", Toast.LENGTH_LONG).show();
//            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nRackSetUp_Dialog);
//        }
//        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nRackSetUp_Polarity_Value_Write_Only_From_Setting_Screen) {
//            String command = prefManager.getSendCommandS().trim();
//            CallReadWriteFuncation(command, 102);
//        } else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nRackSetUp_Supply_CFM_Value_Write_Only_From_Setting_Screen) {
//            String command = prefManager.getSendCommandS().trim();
//            CallReadWriteFuncation(command, 103);
//        } else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nRackSetUp_Exhaust_WC_Value_Write_Only_From_Setting_Screen) {
//            String command = prefManager.getSendCommandS().trim();
//            CallReadWriteFuncation(command, 104);
//        }
    }

    public void initRackModelNullOrNot(){
        if (rackModel == null){
            ArrayList<RackModel> arrRackList = new ArrayList<>();
            arrRackList = sqliteHelper.getDataFromRackSetUpTable();

            if (arrRackList.size() > 0) {
                rackModel = arrRackList.get(0);
            }
        } else {
            Log.e("RackModel","Rack Model Already Field");
        }
    }

    public void reDirectHomeScreenFunction(boolean isSubSettingHomeButtonClicked) {
        if (isSubSettingHomeButtonClicked) {
            Log.e("TAG","Subsettinghomebutton clicked true");
            mRelativeProgressBarLayoutSetting.setVisibility(View.GONE);
            mRelativeProgressBarLayoutSubSetting.setVisibility(View.GONE);
            bluetooth_layout.setVisibility(View.GONE);
            main_layout.setVisibility(View.VISIBLE);
            setting_layout.setVisibility(View.GONE);
            report_layout.setVisibility(View.GONE);
            sub_setting_layout.setVisibility(View.GONE);
            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nRedirectHome);
            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
            //ResetCounter(0);
        } else {
            Log.e("TAG","Subsettinghomebutton clicked False FAlse FAlse");
            mRelativeProgressBarLayoutSetting.setVisibility(View.GONE);
            bluetooth_layout.setVisibility(View.GONE);
            main_layout.setVisibility(View.VISIBLE);
            setting_layout.setVisibility(View.GONE);
            report_layout.setVisibility(View.GONE);
            sub_setting_layout.setVisibility(View.GONE);
            report_layout.setVisibility(View.GONE);
            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nRedirectHome);
            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
//            ResetCounter(0);
        }
    }

    // milliseconds = minutes × 60,000  // eg. : 3 minuits * 60,000 = 1,80,000 milliseconds
    CountDownTimer mCountDownTimer = new CountDownTimer(180000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {

            if (CodeReUse.isBolwerAdmin) {

                if (CodeReUse.isBolwerConnected) {

                    Utility.Log(TAG, "onFinish");

                    try {
                        if (alertview_pre_filter_reset.isShowing()) {
                            alertview_pre_filter_reset.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        if (alertview_selection.isShowing()) {
                            alertview_selection.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        if (Utility.alertview_setting_password.isShowing()) {
                            Utility.alertview_setting_password.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        if (alertview_wifiCommunication.isShowing()) {
                            alertview_wifiCommunication.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        if (alertview_diagnostics.isShowing()) {
                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStopService);
                            mRelativeProgressBarLayoutDialog.setVisibility(View.VISIBLE);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {

                        if (alertview_diagnostics_details.isShowing()) {
                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStopService);
                            mRelativeProgressBarLayoutDialog.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (layout_filter_screen.getVisibility() == View.VISIBLE || layout_blwrdetl_screen.getVisibility() == View.VISIBLE || layout_alarm_screen.getVisibility() == View.VISIBLE) {
                        layout_filter_screen.setVisibility(View.GONE);
                        layout_blwrdetl_screen.setVisibility(View.GONE);
                        layout_alarm_screen.setVisibility(View.GONE);
                    } else {
                        if (!alertview_diagnostics.isShowing() && !alertview_diagnostics_details.isShowing()) {
                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
                        }
                    }

                    // ServiceCommunication
                    /*if (!isMyServiceRunning(MyService.class)) {*/
                    if (service_myservice == null) {
                        if (!alertview_diagnostics.isShowing() && !alertview_diagnostics_details.isShowing()) {
                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
                        }
                    }

                    final int sdk = android.os.Build.VERSION.SDK_INT;
                    /*if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        layout_menubar.setBackgroundDrawable(ContextCompat.getDrawable(act, R.drawable.dark_green_home_screen_box));
                    } else {
                        layout_menubar.setBackground(ContextCompat.getDrawable(act, R.drawable.dark_green_home_screen_box));
                    }*/
                    setMenubarBackground();

                    bluetooth_layout.setVisibility(View.GONE);
//                    main_layout.setBackgroundColor(ContextCompat.getColor(act, R.color.home_green));
                                        if(Utility.BLOWER_TYPE.equals(Utility.BCU2))
                        main_layout.setBackgroundColor(ContextCompat.getColor(act, R.color.bg_bcu2));
                    else
                        main_layout.setBackgroundColor(ContextCompat.getColor(act, R.color.bg_spp));
                    main_layout.setVisibility(View.VISIBLE);

                    layout_home_screen.setVisibility(View.VISIBLE);
                    setting_layout.setVisibility(View.GONE);
                    sub_setting_layout.setVisibility(View.GONE);
                    report_layout.setVisibility(View.GONE);

                    NightModeView(nightModeStatus);
                }
            } else {
                Utility.Log(TAG, "onFinish");

                try {
                    if (alertview_pre_filter_reset.isShowing()) {
                        alertview_pre_filter_reset.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if (alertview_selection.isShowing()) {
                        alertview_selection.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if (Utility.alertview_setting_password.isShowing()) {
                        Utility.alertview_setting_password.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if (alertview_simple_setting_password.isShowing()) {
                        alertview_simple_setting_password.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if (alertview_wifiCommunication.isShowing()) {
                        alertview_wifiCommunication.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {

                    if (alertview_diagnostics.isShowing()) {
                        Utility.setSoftInputAlwaysHide(act);
                        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStopService);
                        mRelativeProgressBarLayoutDialog.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                }

                try {
                    if (alertview_diagnostics_details.isShowing()) {
                        Utility.setSoftInputAlwaysHide(act);
                        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStopService);
                        mRelativeProgressBarLayoutDialog.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (layout_filter_screen.getVisibility() == View.VISIBLE || layout_blwrdetl_screen.getVisibility() == View.VISIBLE || layout_alarm_screen.getVisibility() == View.VISIBLE) {
                    layout_filter_screen.setVisibility(View.GONE);
                    layout_blwrdetl_screen.setVisibility(View.GONE);
                    layout_alarm_screen.setVisibility(View.GONE);
                } else {
                    if (!alertview_diagnostics.isShowing() && !alertview_diagnostics_details.isShowing()) {
                        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
                    }
                }

                // ServiceCommunication
                //if (!isMyServiceRunning(MyService.class)) {
                if (service_myservice == null) {
                    if (!alertview_diagnostics.isShowing() && !alertview_diagnostics_details.isShowing()) {
                        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
                    }

                }

                /*final int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    layout_menubar.setBackgroundDrawable(ContextCompat.getDrawable(act, R.drawable.dark_green_home_screen_box));
                } else {
                    layout_menubar.setBackground(ContextCompat.getDrawable(act, R.drawable.dark_green_home_screen_box));
                }*/
                setMenubarBackground();

                bluetooth_layout.setVisibility(View.GONE);
//                main_layout.setBackgroundColor(ContextCompat.getColor(act, R.color.home_green));
                if(Utility.BLOWER_TYPE.equals(Utility.BCU2))
                    main_layout.setBackgroundColor(ContextCompat.getColor(act, R.color.bg_bcu2));
                else
                    main_layout.setBackgroundColor(ContextCompat.getColor(act, R.color.bg_spp));
                main_layout.setVisibility(View.VISIBLE);

                layout_home_screen.setVisibility(View.VISIBLE);
                setting_layout.setVisibility(View.GONE);
                sub_setting_layout.setVisibility(View.GONE);
                report_layout.setVisibility(View.GONE);

                NightModeView(nightModeStatus);
            }
        }
    };

    private void removeEcoflowIcon(){
        img_logo_two.setVisibility(View.GONE);
        img_screen_type_logo.setVisibility(View.VISIBLE);
    }

    private void setEcoflowIcon(){
        img_logo_two.setVisibility(View.GONE);
        img_screen_type_logo.setVisibility(View.VISIBLE);
    }

    private void setMenubarBackground(){
        final int sdk = android.os.Build.VERSION.SDK_INT;
        if(Utility.BLOWER_TYPE.equals(Utility.BCU2)) {
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                layout_menubar.setBackgroundDrawable(ContextCompat.getDrawable(act, R.drawable.dark_orange_home_screen_box));
            } else {
                layout_menubar.setBackground(ContextCompat.getDrawable(act, R.drawable.dark_orange_home_screen_box));
            }
        } else{
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                layout_menubar.setBackgroundDrawable(ContextCompat.getDrawable(act, R.drawable.dark_blue_home_screen_box));
            } else {
                layout_menubar.setBackground(ContextCompat.getDrawable(act, R.drawable.dark_blue_home_screen_box));
            }
        }
    }

    private void setHomeScreenBackground(){
        final int sdk = android.os.Build.VERSION.SDK_INT;
        if(Utility.BLOWER_TYPE.equals(Utility.BCU2)) {
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                layout_home_screen.setBackgroundDrawable(ContextCompat.getDrawable(act, R.drawable.dark_orange_home_screen_box));
            } else {
                layout_home_screen.setBackground(ContextCompat.getDrawable(act, R.drawable.dark_orange_home_screen_box));
            }
        } else{
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                layout_home_screen.setBackgroundDrawable(ContextCompat.getDrawable(act, R.drawable.dark_blue_home_screen_box));
            } else {
                layout_home_screen.setBackground(ContextCompat.getDrawable(act, R.drawable.dark_blue_home_screen_box));
            }
        }

    }

    private void NightModeView(boolean status) {
        if (status) {

            main_layout.setBackgroundColor(ContextCompat.getColor(act, R.color.nightMode_red));
            view_home_screen_one.setBackgroundColor(ContextCompat.getColor(act, R.color.black));
            view_home_screen_two.setBackgroundColor(ContextCompat.getColor(act, R.color.black));
            view_home_screen_three.setBackgroundColor(ContextCompat.getColor(act, R.color.black));
            view_home_menu_one.setBackgroundColor(ContextCompat.getColor(act, R.color.black));
            view_home_menu_two.setBackgroundColor(ContextCompat.getColor(act, R.color.black));
            view_home_menu_three.setBackgroundColor(ContextCompat.getColor(act, R.color.black));
            view_home_menu_four.setBackgroundColor(ContextCompat.getColor(act, R.color.black));

            img_logo_one.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_logo_two.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_screen_type_logo.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_wicom_cloud_logo.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
            txt_Critical_Alarm.setTextColor(ContextCompat.getColor(act, R.color.black));

//            img_home_screen_plus.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
//            img_home_screen_minus.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
//            img_home_screen_up.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
//            img_home_screen_down.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_home_screen_one.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_home_screen_two.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_home_screen_three.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
//            img_home_screen_four.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);

            img_home_menu_one.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_home_menu_two.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_home_menu_three.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_home_menu_four.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
            img_home_menu_five.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);

//            txt_S01_Z3_Plus.setBackgroundResource(R.drawable.red_home_screen_box);
//            txt_S01_Z3_Minus.setBackgroundResource(R.drawable.red_home_screen_box);
//            txt_S08_Up.setBackgroundResource(R.drawable.red_home_screen_box);
//            txt_S08_Down.setBackgroundResource(R.drawable.red_home_screen_box);
//            txt_F01_XX.setBackgroundResource(R.drawable.black_home_screen_box);
            txt_F01_YY.setBackgroundResource(R.drawable.black_home_screen_box);
//            txt_F02_XX.setBackgroundResource(R.drawable.black_home_screen_box);
            txt_F02_YY.setBackgroundResource(R.drawable.black_home_screen_box);
//            txt_S01_Z3.setBackgroundResource(R.drawable.black_home_screen_box);
            txt_F08_XXYY.setBackgroundResource(R.drawable.black_home_screen_box);

//            txt_F01_XX.setTextColor(getResources().getColor(R.color.nightMode_red));
            txt_F01_YY.setTextColor(getResources().getColor(R.color.nightMode_red));
//            txt_F02_XX.setTextColor(getResources().getColor(R.color.nightMode_red));
            txt_F02_YY.setTextColor(getResources().getColor(R.color.nightMode_red));
//            txt_S01_Z3.setTextColor(getResources().getColor(R.color.nightMode_red));
            txt_F08_XXYY.setTextColor(getResources().getColor(R.color.nightMode_red));
            txt_timer.setTextColor(getResources().getColor(R.color.black));
            txt_version.setTextColor(getResources().getColor(R.color.black));

            final int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                layout_menubar.setBackgroundDrawable(ContextCompat.getDrawable(act, R.drawable.red_home_screen_box));
                layout_home_screen.setBackgroundDrawable(ContextCompat.getDrawable(act, R.drawable.red_home_screen_box));
            } else {
                layout_menubar.setBackground(ContextCompat.getDrawable(act, R.drawable.red_home_screen_box));
                layout_home_screen.setBackground(ContextCompat.getDrawable(act, R.drawable.red_home_screen_box));
            }

            if (isPreFilterResetClicked) {
                //layout_filter_menu.setBackgroundColor(ContextCompat.getColor(act, R.color.nightMode_red));

                layout_filter_menu.setBackgroundDrawable(act.getResources().getDrawable(R.drawable.dark_night_mode_red_home_screen_box));

                //img_home_menu_one.setColorFilter(ContextCompat.getColor(act, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
            }
            layout_filter_screen.setVisibility(View.GONE);
            layout_blwrdetl_screen.setVisibility(View.GONE);
            layout_alarm_screen.setVisibility(View.GONE);
            layout_home_screen.setVisibility(View.VISIBLE);
        }
//        else {
//            if (isRedYellowColorHomeScreen) {
//                return;
//            } else {
////                feedbackArrayList = responseHandler.getLastFeedbackData();
//                setAllColorToHomeScreenLayout("green");
//            }
//
//        }
    }

    // CountDownTimer Start or Stop
    private void ResetCounter(int isCounterRunning) { // 1 = start & restart // 0 = stop
        if (CodeReUse.isBolwerAdmin) {
            if (CodeReUse.isBolwerConnected) {
                if (isCounterRunning == 1) {
                    mCountDownTimer.cancel(); // cancel
                    mCountDownTimer.start();  // then restart
                    if (isPreFilterResetYesButtonClicked) {
                        isPreFilterResetClicked = false;
                        layout_filter_menu.setBackgroundColor(0x00000000);
                        img_home_menu_one.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                    }
                    Utility.Log(TAG, "onStart");
                } else if (isCounterRunning == 0) {
                    mCountDownTimer.cancel(); // cancel
                    Utility.Log(TAG, "onCancel");
                }
            }
        } else {
            if (isCounterRunning == 1) {
                mCountDownTimer.cancel(); // cancel
                mCountDownTimer.start();  // then restart
                if (isPreFilterResetYesButtonClicked) {
                    isPreFilterResetClicked = false;
                    layout_filter_menu.setBackgroundColor(0x00000000);
                    img_home_menu_one.setColorFilter(ContextCompat.getColor(act, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                }
                Utility.Log(TAG, "onStart");
            } else if (isCounterRunning == 0) {
                mCountDownTimer.cancel(); // cancel
                Utility.dismissAlertDialog();
                Utility.Log(TAG, "onCancel");
            }
        }
    }

    // mBluetoothAdapter
    private void bluetoothSearch(boolean isBluetooth) {
        BluetoothListDialogs display = new BluetoothListDialogs(mBluetoothAdapter, communicationController, isBluetooth);
        display.show(this, isBluetooth);
        communicationController = display.getChatController();
    }

    // onStart
    @Override
    public void onStart() {
        super.onStart();
        if (communicationController == null) {
            communicationController = new CommunicationController(this, mHandler_Android);
            communicationController.start();
        }
    }

    // sendMessage Using Bluetooth
    public void sendMessage(String message, String type, int isStart) {
        if (communicationController.getState() != Constants.STATE_CONNECTED) {
            Toast.makeText(this, "Connection was lost!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (message.length() > 0 && type.length() > 0) {
            byte[] send = makeJSON(message, type, isStart).getBytes();
            communicationController.write(send);
        }
    }

    // MakeJSON Bluetooth Data
    private String makeJSON(String message, String type, int isStart) {
        JSONObject json = new JSONObject();
        String random = randomKey();
        try {
            json.put("key", random);
            json.put("message", message);
            json.put("type", type);
            json.put("isStart", isStart);
            json.put("from", mBluetoothAdapter.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    // JsonMessage Bluetooth Data
    private void jsonMessage(String jsonData, boolean write) {
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = 2;  //you can also calculate your inSampleSize
            options.inJustDecodeBounds = false;
            options.inTempStorage = new byte[16 * 1024];
            Log.d("string test", jsonData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // RandomKey For Bluetooth Communication
    public String randomKey() {
        char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        String key = sb.toString();
        return key;
    }

    // onResume
    @Override
    public void onResume() {
        super.onResume();
        Utility.hideNavigationBar(act);
        if (communicationController == null) {
            if (communicationController.getState() == Constants.STATE_NONE)
                communicationController.start();
        }

        rackDetailsModel = sqliteHelper.getDataFromRackBlowerDetails();
        if (rackDetailsModel != null) {
            responseHandler.rackDetailsModels = rackDetailsModel;
            responseHandler.allentownBlowerApplication = allentownBlowerApplication;
            responseHandler.myDb = sqliteHelper;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Utility.getBroadCast(act);
//        registerReceiver(broadcast_reciever, new IntentFilter(ACTION_USB_PERMISSION));
    }

    // onDestroy
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (communicationController != null)
            communicationController.stop();
    }

    // Bluetooth Connectivity
    public void BluetoothConn(final String address) {
        // Spawn a new thread to avoid blocking the GUI one
        new Thread() {
            public void run() {
                boolean fail = false;

                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

                try {
                    mBTSocket = createBluetoothSocket(device);
                } catch (IOException e) {
                    fail = true;
                    Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                }
                // Establish the Bluetooth socket connection.
                try {
                    mBTSocket.connect();
                } catch (IOException e) {
                    try {
                        fail = true;
                        mBTSocket.close();
                        mHandler_Android.obtainMessage(Constants.CONNECTING_STATUS_Client, -1, -1).sendToTarget();
                    } catch (IOException e2) {
                        //insert code to deal with this
                        Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                    }
                }
                if (!fail) {
                    mConnectedThread = new ConnectedThread(mBTSocket);
                    mConnectedThread.start();
                }

                Log.e(TAG, "BroadcastReceiver: Command");

                if (mConnectedThread != null) { //First check to make sure thread created
                    mConnectedThread.write("tx\r\n");
                }

            }
        }.start();

    }

    // Broadcast Receiver that detects bond state changes (Pairing status changes)
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    final String address = mDevice.getAddress().substring(mDevice.getAddress().length() - 17);
                    //BluetoothListDialogs.connectToDevice(address,1);
                    unregisterReceiver(mBroadcastReceiver);
                    Log.e(TAG, "BroadcastReceiver: BOND_BONDED.");

                    // Spawn a new thread to avoid blocking the GUI one
                    new Thread() {
                        public void run() {
                            boolean fail = false;

                            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

                            try {
                                mBTSocket = createBluetoothSocket(device);
                            } catch (IOException e) {
                                fail = true;
                                Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                            }
                            // Establish the Bluetooth socket connection.
                            try {
                                mBTSocket.connect();
                            } catch (IOException e) {
                                try {
                                    fail = true;
                                    mBTSocket.close();
                                    mHandler_Bluetooth.obtainMessage(Constants.CONNECTING_STATUS_Client, -1, -1).sendToTarget();
                                } catch (IOException e2) {
                                    //insert code to deal with this
                                    Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                            if (!fail) {
                                mConnectedThread = new ConnectedThread(mBTSocket);
                                mConnectedThread.start();
                            }

                            Log.e(TAG, "BroadcastReceiver: Command");

                            if (mConnectedThread != null) { //First check to make sure thread created
                                mConnectedThread.write("tx\r\n");
                            }

                        }
                    }.start();

                }
                //case2: creating a bone
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.e(TAG, "BroadcastReceiver: BOND_BONDING.");
                }
                //case3: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.e(TAG, "BroadcastReceiver: BOND_NONE.");
                }
            }
        }
    };

    // Create Bluetooth Socket
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BTMODULEUUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection", e);
        }
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    // Bluetooth Connected Thread
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        private String convertStreamToString(InputStream is) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            byte[] readbyte;
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.available();
                    if (bytes != 0) {
                        //buffer = new byte[1024];
                        SystemClock.sleep(4000); //pause and wait for rest of data. Adjust this depending on your sending speed.
                        bytes = mmInStream.available(); // how many bytes are ready to be read?
                        buffer = new byte[bytes];
                        bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read

                        mHandler_Bluetooth.obtainMessage(Constants.MESSAGE_READ_Client, bytes, -1, buffer).sendToTarget(); // Send the obtained bytes to the UI activity
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }

        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            Log.e(TAG, "Write message : " + input);
            byte[] bytes = input.getBytes();           //converts entered String into bytes
            try {
                Log.e(TAG, "Write bytes : " + bytes);
                mmOutStream.write(bytes);
            } catch (IOException e) {
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }

    }

    public void ShowSimpleSettingPasswordDialog(final Activity act, final  String actString)
    {
        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStopService);
        prefManager = new PrefManager(act);
        alertview_simple_setting_password = new Dialog(act);
        alertview_simple_setting_password.setCancelable(false);


        alertview_simple_setting_password.setContentView(R.layout.alertview_simplepassword_layout); // EditText Screen
//        alertview_setting_password.setContentView(R.layout.alertview_edittext_layout); // EditText Screen
//        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
//        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);
//        //Width : 614 Height : 276
//        alertview_setting_password.getWindow().setLayout(width, height);
//        alertview_setting_password.getWindow().getAttributes().x = 30;
//        alertview_setting_password.getWindow().getAttributes().y = 10;
        alertview_simple_setting_password.getWindow().setGravity(Gravity.TOP);

        alertview_simple_setting_password.show();
        txt_Title_alartview_box = alertview_simple_setting_password.findViewById(R.id.txt_Title_alartview_box);
        edit_EnterTxt_alartview_box = alertview_simple_setting_password.findViewById(R.id.edit_EnterTxt_alartview_box);
        btn_Cancel_alartview_box = alertview_simple_setting_password.findViewById(R.id.btn_Cancel_alartview_box);
        btn_Save_alartview_box = alertview_simple_setting_password.findViewById(R.id.btn_Save_alartview_box);
        txt_Title_alartview_box.setText("Enter Password");
        btn_Cancel_alartview_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Log.e(TAG, "Clicked Cancel." + edit_EnterTxt_alartview_box.getText().toString());
                alertview_simple_setting_password.dismiss();

                final Runnable r = new Runnable() {
                    public void run() {
                        Log.e(TAG,"Starting service after 4 seconds when Cancel clicked on 7951 setting password.");
                        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
                    }
                };
                mHandler.postDelayed(r, 4000);

                if (actString.equals("Plus_Command")) {

                } else if (actString.equals("Minus_Command")) {

                } else if (actString.equals("Up_Down")) {

                } else {
                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nRedirectHome);
                }
            }
        });
        btn_Save_alartview_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertview_simple_setting_password.dismiss();
                //Log.e(TAG, "Clicked Save." + edit_EnterTxt_alartview_box.getText().toString());
                String enteredval = edit_EnterTxt_alartview_box.getText().toString();
                if (enteredval.toString().equals(""))
                {
                    Utility.ShowMessage(act, "Alert", "Passwrod can't be empty.", "OK");
                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
                }
                else
                {
                    if (actString.equals("ChangePasswordSetting"))
                    {
                        if (enteredval.length() != 4)
                        {
                            Utility.ShowMessage(act, "Error", "Entered Password must be 4 digit number. \r\nPlease enter the different password.", "OK");
                        }
                        else if (enteredval.equals(prefManager.getReportPassword()))
                        {
                            Utility.ShowMessage(act, "Error", "Setting and Report password can't be same. \r\nPlease enter the different password.", "OK");
                        }
                        else
                        {
                            prefManager.setSettingPassword(enteredval.toString());
                            Utility.ShowMessage(act, "Success", "Setting Password Changed Successfully.", "OK");
                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nChangePassword);
                        }

                    }
                    else if (actString.equals("ChangePasswordReport"))
                    {
                        if (enteredval.length() != 4)
                        {
                            Utility.ShowMessage(act, "Error", "Entered Password must be 4 digit number. \r\nPlease enter the different password.", "OK");
                        }
                        else if (enteredval.equals(prefManager.getSettingPassword()))
                        {
                            Utility.ShowMessage(act, "Error", "Report and Setting password can't be same. \r\nPlease enter the different password.", "OK");
                        }
                        else
                        {
                            prefManager.setReportPassword(enteredval.toString());
                            Utility.ShowMessage(act, "Success", "Report Password Changed Successfully.", "OK");
                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nChangePassword);
                        }
                    }
                    else
                    {
                        if (prefManager.getSettingPassword().equals(String.valueOf(enteredval.toString()))) {
                            if (actString.equals("Plus_Command")) {
                                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nPlus);
                            } else if (actString.equals("Minus_Command")) {
                                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nMinus);
                            } else if (actString.equals("Up_Down")) {
                                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nUp_Down);
                            } else {
                                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nRedirectSettings);
                            }
                        } else if (prefManager.getDiagnosticsPassword().equals(String.valueOf(enteredval.toString()))) {
                            if (!CodeReUse.isBolwerAdmin) {
                                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nDiagnosticsSettings);
                            } else {
                                Utility.ShowMessage(act, "Alert", "Feature is not available.", "OK");
                            }
                        } else if (prefManager.getDiagnosticsDetailPassword().equals(String.valueOf(enteredval.toString()))) {
                            if (!CodeReUse.isBolwerAdmin) {
                                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nDiagnosticsDetailsSettings);
                            } else {
                                Utility.ShowMessage(act, "Alert", "Feature is not available.", "OK");
                            }
                        } else if (prefManager.getBluetoothDisconnectPassword().equals(String.valueOf(enteredval.toString()))) {
                            if (CodeReUse.isBolwerAdmin)
                                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nBluetoothDisconnect);
                            else {
                                Utility.ShowMessage(act, "Alert", "Invalid Password.", "OK");
                                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
                            }
                        } else if (prefManager.getReportPassword().equals(String.valueOf(enteredval.toString()))) {
                            if (!CodeReUse.isBolwerAdmin) {
                                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nRedirectReport);
                            } else {
                                Utility.ShowMessage(act, "Alert", "Feature is not available.", "OK");
                            }
                        } else {
                            Utility.ShowMessage(act, "Alert", "Invalid Password.", "OK");
                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
                        }
                    }

                }
            }
        });
        edit_EnterTxt_alartview_box.requestFocus();
    }

    public void SendS26ToTurnOffOnSound()
    {
        if (feedbackArrayList.size() != 0) {
            if(     responseHandler.hexToBinary(feedbackArrayList.get(0).getF12()).charAt(15) == '1' ||
                    responseHandler.hexToBinary(feedbackArrayList.get(0).getF12()).charAt(9) == '1' ||
                    responseHandler.hexToBinary(feedbackArrayList.get(0).getF12()).charAt(7) == '1' ||
                    responseHandler.hexToBinary(feedbackArrayList.get(0).getF12()).charAt(6) == '1' ||
                    responseHandler.hexToBinary(feedbackArrayList.get(0).getF12()).charAt(5) == '1' ||
                    responseHandler.hexToBinary(feedbackArrayList.get(0).getF12()).charAt(1) == '1' ||
                    responseHandler.hexToBinary(feedbackArrayList.get(0).getF13()).charAt(5)=='1' ||
                    responseHandler.hexToBinary(feedbackArrayList.get(0).getF13()).charAt(4) == '1' ||
                    responseHandler.hexToBinary(feedbackArrayList.get(0).getF13()).charAt(3) == '1' ||
                    responseHandler.hexToBinary(feedbackArrayList.get(0).getF13()).charAt(2) == '1' ||
                    responseHandler.hexToBinary(feedbackArrayList.get(0).getF13()).charAt(1) == '1' ||
                    responseHandler.hexToBinary(feedbackArrayList.get(0).getF13()).charAt(0) == '1' )
            {
                boolean check = false;
                Log.e("TAG","F12 = " + feedbackArrayList.get(0).getF12());
                Log.e("TAG","F13 = " + feedbackArrayList.get(0).getF13());
                Log.e("TAG","F12ForS26Command = " + F12ForS26Command);
                Log.e("TAG","F13ForS26Command = " + F13ForS26Command);

                if (F12ForS26Command.equals("") && F13ForS26Command.equals(""))
                {
                    F12ForS26Command = feedbackArrayList.get(0).getF12();
                    F13ForS26Command = feedbackArrayList.get(0).getF13();
                    check = true;
                }
                else if (!feedbackArrayList.get(0).getF12().equals(F12ForS26Command) || !feedbackArrayList.get(0).getF13().equals(F13ForS26Command))
                {
                    F12ForS26Command = feedbackArrayList.get(0).getF12();
                    F13ForS26Command = feedbackArrayList.get(0).getF13();
                    check = true;

                }

                if (check)
                {
                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStopService);

                    final Runnable r = new Runnable() {
                        public void run() {
                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetS26OffCommand);
                        }
                    };
                    mHandler.postDelayed(r, 8000);
                }

            }
            else
            {
                F12ForS26Command = feedbackArrayList.get(0).getF12();
                F13ForS26Command = feedbackArrayList.get(0).getF13();
            }
        }



    }

    // For USB FlashDrive
//    public boolean checkUSB() {
//        boolean Usb = false;
//        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
//        // Get the list of attached devices
//        HashMap<String, UsbDevice> devices = manager.getDeviceList();
//        // Iterate over all devices
//        Iterator<String> it = devices.keySet().iterator();
//        while (it.hasNext()) {
//            String deviceName = it.next();
//            UsbDevice device = devices.get(deviceName);
//            String VID = Integer.toHexString(device.getVendorId()).toUpperCase();
//            String PID = Integer.toHexString(device.getProductId()).toUpperCase();
//            if (!manager.hasPermission(device)) {
//                PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
//                manager.requestPermission(device, mPermissionIntent);
//                Usb = true;
//                return Usb;
//            } else {
//                //user permission already granted; prceed to access USB device
//                Usb = true;
//                return Usb;
//            }
//        }
//        return Usb;
//    }
}