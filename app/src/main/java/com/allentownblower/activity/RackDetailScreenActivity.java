package com.allentownblower.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.allentownblower.R;
import com.allentownblower.application.AllentownBlowerApplication;
import com.allentownblower.common.ApiHandler;
import com.allentownblower.common.CodeReUse;
import com.allentownblower.common.JSONObjectHandler;
import com.allentownblower.common.ObserverActionID;
import com.allentownblower.common.PendingID;
import com.allentownblower.common.PrefManager;
import com.allentownblower.common.ResponseHandler;
import com.allentownblower.common.Utility;
import com.allentownblower.database.SqliteHelper;
import com.allentownblower.module.RackDetailsModel;
import com.allentownblower.module.RackModel;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import android.os.SystemProperties;
//import android.os.SystemProperties;

public class RackDetailScreenActivity extends AppCompatActivity {

    private static final String TAG = "RackDetailScreenActivity";
    private TextView mTxtSerialNo, mTxtCustomerName, mTxtHostName, mTxtGetDetails, mTxtBlowerDetailTab, mTxtOtherDetailsTab, mTxtEmailTab, mTxtHostAddressTab, mTxtTouchScreenDetailTab;
    private Activity act;
    private Dialog edittext_alertview_selection;
    private TextView txt_Title_alartview_box, btn_Cancel_alartview_box, btn_Ok_alartview_box, edit_EnterTxt_alartview_box, mTxtUpdateHostName, mTxtUpdateButton;
    private RackModel rackModel = null;
    private RackDetailsModel rackDetailsModel = null;
    private ResponseHandler responseHandler;
    private PrefManager prefManager;
    private AllentownBlowerApplication allentownBlowerApplication;
    private static SqliteHelper dpHelper;
    private ArrayList<RackModel> arrRackList = new ArrayList<>();
    private ArrayList<RackDetailsModel> arrRackBlowerDetailList = new ArrayList<>();
    private JSONObjectHandler jsonObjectHandler;
    private RelativeLayout mRelativeLayoutSerialCustomerMain, mRelativeLayoutBlowerOtherServerDetailsMain;
    private LinearLayout mLinearLayoutUpdateButton, mLinearLayoutBackButton, mLinearLayoutTab, mLinearLayoutBlowerDetails, mLinearLayoutOtherDetails, mLinearLayoutHostAddress, mLinearLayoutEmail, mLinearLayoutTouchScreen;
    private RadioGroup radioGroup_aln_email_service_value, radioGroup_is_reg_alarm_on_value, radioGroup_is_temp_hmd_alarm_value;
    private RadioButton radioButton_aln_Yes, radioButton_aln_No, radioButton_is_reg_On, radioButton_is_reg_Off, radioButton_is_temp_hmd_Yes, radioButton_is_temp_hmd_No;
    private TextView txt_rack_serial_value, txt_blower_model_value, txt_supply_blower_serial_value, txt_exhaust_blower_serial_value, txt_ablower_serial_value,
            txt_ablower_wifimac_value, txt_ablower_bluetooth_mac_value, txt_ablower_ip_address_value, txt_ablower_lanmac_value; // TODO :- blower details
    private TextView txt_alert_email_ds_value, txt_report_email_ds_value, txt_last_alarm_value, txt_last_hmdt_alarm_value,
            txt_temp_unit_value, txt_pressure_unit_value, txt_air_flow_unit_value; // TODO:- others details
    private String strAlnEmailServiceSelectValue, strIsRegAlarmOnSelectValue, strIsTempHmdAlarmSelectValue;
    private String mSerialNumber, mCustomerName, hostAddress;
    private RelativeLayout relative_progress_rack_detail_screen;
    private TextView txt_serial_no_host, txt_customer_name_host, txt_host_name_host, txt_blower_name, txt_building_name, txt_room_name, txt_rack_model_name, txt_rack_serial_name;
    private String mWiFiMac, mLanMac, mBluetoothMac, mWiFiIPAddress, mLanIPAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rack_detail_screen);
        act = this;
        Utility.hideNavigationBar(act);
        //Utility.getBroadCast(act);
        dpHelper = new SqliteHelper(act);
        prefManager = new PrefManager(act);
        jsonObjectHandler = new JSONObjectHandler(act);
        allentownBlowerApplication = (AllentownBlowerApplication) act.getApplication();
//        allentownBlowerApplication.getObserver().addObserver(this);
//        Log.e("TAG", "Getting Winmate WiFi " + SystemProperties.get("persist.sys.wifi.mac"));
        Log.e("TAG", "Getting Winmate WiFi " + SystemProperties.get("persist.sys.wifi.mac"));
        if (Build.VERSION.SDK_INT >= 30) {
            mWiFiMac = SystemProperties.get("persist.sys.wifi.mac");
        }
        else
        {
            mWiFiMac = getWiFiMacAddr();
        }
        Log.e("TAG", "Getting Serial");
        mSerialNumber = getSerialNumber();
        if (mSerialNumber.equals("")) {
            mSerialNumber = prefManager.getSerialNumber();
        }
        else
        {
            prefManager.setSerialNumber(mSerialNumber);
        }
        Log.e("TAG", "Getting LAN");
        mLanMac = getLANMacAddr();
//        Log.e("TAG", "Getting Winmate BL " + SystemProperties.get("persist.sys.bt.mac"));
        Log.e("TAG", "Getting Winmate BL " + SystemProperties.get("persist.sys.bt.mac"));
        mBluetoothMac = getBluetoothMacAddress();
        if (mBluetoothMac.equals("")) {
            mBluetoothMac = SystemProperties.get("persist.sys.bt.mac");
        }

        Log.e("TAG", "Getting WiFi IP");
        mWiFiIPAddress = getWiFiIPAddr();
        Log.e("TAG", "Getting LAN IP");
        mLanIPAddress = getLANIPAddr();
        Log.e("TAG", "WiFiMac : " + mWiFiMac);
        Log.e("TAG", "Serial : " + mSerialNumber);
        Log.e("TAG", "LanMac : " + mLanMac);
        Log.e("TAG", "BluetoothMac : " + mBluetoothMac);
        Log.e("TAG", "WiFiIPAddress : " + mWiFiIPAddress);
        Log.e("TAG", "LanIPAddress : " + mLanIPAddress);
        /*
        Winmate 2 mac details
        serial number : 15225w026603
        wifi mac: 80d21dbcc435
        bl mac: 80d21dbcc434
        LAN mac: 0003e19aaeeb

        */

        initMethod();
        mClickListerMethod();
        loadScreen();
        ResetCounter(1);
    }

    private void loadScreen() {
        rackDetailsModel = dpHelper.getDataFromRackBlowerDetails();
        if (rackDetailsModel != null) {
            getRackBlowerDetails_Api(rackDetailsModel.getmId(), rackDetailsModel.getmRackBlowerCustomerID());
        } else {
            mRelativeLayoutSerialCustomerMain.setVisibility(View.VISIBLE);
            mLinearLayoutTab.setVisibility(View.GONE);
            mLinearLayoutOtherDetails.setVisibility(View.GONE);
            mRelativeLayoutBlowerOtherServerDetailsMain.setVisibility(View.GONE);
            mLinearLayoutBlowerDetails.setVisibility(View.GONE);
            mLinearLayoutHostAddress.setVisibility(View.GONE);
            arrRackList = dpHelper.getDataFromRackSetUpTable();
            if (arrRackList.size() > 0) {
                rackModel = arrRackList.get(0);
            }

            mTxtSerialNo.setText(mSerialNumber);
            //mTxtCustomerName.setText(rackModel.getCompanyName());
            mTxtCustomerName.setText("");
        }
    }

    private void initMethod() {
        mTxtSerialNo = findViewById(R.id.txt_serial_no);
        mTxtCustomerName = findViewById(R.id.txt_customer_name);
        mTxtHostName = findViewById(R.id.txt_host_name);
        mTxtGetDetails = findViewById(R.id.txt_get_detail);

        mTxtBlowerDetailTab = findViewById(R.id.txt_blower_detail);
        mTxtTouchScreenDetailTab = findViewById(R.id.txt_touchscreen_detail);
        mTxtOtherDetailsTab = findViewById(R.id.txt_other_detail);

        mRelativeLayoutSerialCustomerMain = findViewById(R.id.relative_layout_serial_customer_main);
        mRelativeLayoutBlowerOtherServerDetailsMain = findViewById(R.id.relative_layout_blower_other_server_details_main);

        mLinearLayoutUpdateButton = findViewById(R.id.linear_layout_buttons_Update);
        mLinearLayoutBackButton = findViewById(R.id.linear_layout_buttons_Back);

        mLinearLayoutTab = findViewById(R.id.linear_layout_tab_title);
        mLinearLayoutBlowerDetails = findViewById(R.id.blower_linear_main_layout);
        mLinearLayoutOtherDetails = findViewById(R.id.other_linear_main_layout);

        // blower details variable defined
        txt_rack_serial_value = findViewById(R.id.txt_rack_serial_value);
        txt_blower_model_value = findViewById(R.id.txt_blower_model_value);
        txt_supply_blower_serial_value = findViewById(R.id.txt_supply_blower_serial_value);
        txt_exhaust_blower_serial_value = findViewById(R.id.txt_exhaust_blower_serial_value);


        txt_blower_name = findViewById(R.id.txt_blower_name);
        txt_building_name = findViewById(R.id.txt_building_name);
        txt_room_name = findViewById(R.id.txt_room_name);
        txt_rack_model_name = findViewById(R.id.txt_rack_model_name);
        txt_rack_serial_name = findViewById(R.id.txt_rack_serial_name);

        // other details variable defined
        radioGroup_aln_email_service_value = findViewById(R.id.radioGroup_aln_email_service_value);
        radioButton_aln_Yes = findViewById(R.id.radioButton_aln_Yes);
        radioButton_aln_No = findViewById(R.id.radioButton_aln_No);

        txt_alert_email_ds_value = findViewById(R.id.txt_alert_email_ds_value);
        txt_report_email_ds_value = findViewById(R.id.txt_report_email_ds_value);

        radioGroup_is_reg_alarm_on_value = findViewById(R.id.radioGroup_is_reg_alarm_on_value);
        radioButton_is_reg_On = findViewById(R.id.radioButton_is_reg_On);
        radioButton_is_reg_Off = findViewById(R.id.radioButton_is_reg_Off);

        txt_last_alarm_value = findViewById(R.id.txt_last_alarm_value);

        radioGroup_is_temp_hmd_alarm_value = findViewById(R.id.radioGroup_is_temp_hmd_alarm_value);
        radioButton_is_temp_hmd_Yes = findViewById(R.id.radioButton_is_temp_hmd_Yes);
        radioButton_is_temp_hmd_No = findViewById(R.id.radioButton_is_temp_hmd_No);

        txt_last_hmdt_alarm_value = findViewById(R.id.txt_last_hmdt_alarm_value);
        txt_temp_unit_value = findViewById(R.id.txt_temp_unit_value);
        txt_pressure_unit_value = findViewById(R.id.txt_pressure_unit_value);
        txt_air_flow_unit_value = findViewById(R.id.txt_air_flow_unit_value);

        relative_progress_rack_detail_screen = findViewById(R.id.relative_progress_rack_detail_screen);

        // Host Details Variable
        mLinearLayoutHostAddress = findViewById(R.id.host_linear_main_layout);
        mTxtUpdateHostName = findViewById(R.id.txt_host_name_host);
        mTxtHostAddressTab = findViewById(R.id.txt_host_address_tab);
        txt_serial_no_host = findViewById(R.id.txt_serial_no_host);
        txt_customer_name_host = findViewById(R.id.txt_customer_name_host);
//        txt_host_name_host = findViewById(R.id.txt_host_name_host);
        mTxtUpdateButton = findViewById(R.id.txt_update);

        // Email Details Variable
        mLinearLayoutEmail = findViewById(R.id.email_linear_main_layout);
        mTxtEmailTab = findViewById(R.id.txt_email_detail);

        //TouchScreen Details Variable
        mLinearLayoutTouchScreen = findViewById(R.id.touch_screen_main_linear_layout);
        txt_ablower_serial_value = findViewById(R.id.txt_ablower_serial_value);
        txt_ablower_wifimac_value = findViewById(R.id.txt_ablower_wifimac_value);
        txt_ablower_lanmac_value = findViewById(R.id.txt_ablower_lanmac_value);
        txt_ablower_bluetooth_mac_value = findViewById(R.id.txt_ablower_bluetooth_mac_value);
        txt_ablower_ip_address_value = findViewById(R.id.txt_ablower_ip_address_value);

    }

    public void showDetailWhenScreenLoad() {
        mLinearLayoutTab.setVisibility(View.VISIBLE);
        mRelativeLayoutSerialCustomerMain.setVisibility(View.GONE);
        mLinearLayoutOtherDetails.setVisibility(View.GONE);
        mRelativeLayoutBlowerOtherServerDetailsMain.setVisibility(View.VISIBLE);
        mLinearLayoutBlowerDetails.setVisibility(View.GONE);
        mLinearLayoutHostAddress.setVisibility(View.VISIBLE);

        txt_rack_serial_value.setText(rackDetailsModel.getmRackSerial());
        txt_blower_model_value.setText(rackDetailsModel.getmBlowerModel());
        txt_supply_blower_serial_value.setText(rackDetailsModel.getmSupplyBlowerSerial());
        txt_exhaust_blower_serial_value.setText(rackDetailsModel.getmExhaustBlowerSerial());
        txt_ablower_serial_value.setText(rackDetailsModel.getmABlowerSerial());
        txt_ablower_wifimac_value.setText(rackDetailsModel.getmABlowerWiFiMAC());
        txt_ablower_lanmac_value.setText(rackDetailsModel.getmABlowerLANMAC());
        txt_ablower_bluetooth_mac_value.setText(rackDetailsModel.getmABlowerBluetoothMAC());
        if (!rackDetailsModel.getmABlowerWiFiIPAddress().equals("")) {
            txt_ablower_ip_address_value.setText(rackDetailsModel.getmABlowerWiFiIPAddress());
        } else {
            txt_ablower_ip_address_value.setText(rackDetailsModel.getmABlowerLanIPAddress());
        }

        txt_rack_serial_name.setText(rackDetailsModel.getmRackSerial());

        txt_blower_name.setText(rackDetailsModel.getmABlowerName());
//        txt_building_name.setText(rackDetailsModel.getmABlowerBuilding());
        txt_room_name.setText(rackDetailsModel.getmABlowerBuilding() + "/" + rackDetailsModel.getmABlowerRoom());
        txt_rack_model_name.setText(rackDetailsModel.getmRackModel());

        if (rackDetailsModel.getmUseAlnEmailService() == 1) {
            radioButton_aln_Yes.setChecked(true);
        } else {
            radioButton_aln_No.setChecked(true);
        }

        txt_alert_email_ds_value.setText(rackDetailsModel.getmAlertEmailIDs());
        txt_report_email_ds_value.setText(rackDetailsModel.getmReportEmailIDs());

        if (rackDetailsModel.getmIsRegAlarmOn() == 1) {
            radioButton_is_reg_On.setChecked(true);
        } else {
            radioButton_is_reg_Off.setChecked(true);
        }

        txt_last_alarm_value.setText(rackDetailsModel.getmLastAlarm());

        if (rackDetailsModel.getmIsTmpHMDAlarmOn() == 1) {
            radioButton_is_temp_hmd_Yes.setChecked(true);
        } else {
            radioButton_is_temp_hmd_No.setChecked(true);
        }

        txt_last_hmdt_alarm_value.setText(rackDetailsModel.getmLasTmpHMDtAlarm());
        txt_temp_unit_value.setText(rackDetailsModel.getmTempUnit());
        txt_pressure_unit_value.setText(rackDetailsModel.getmPressureUnit());
        txt_air_flow_unit_value.setText(rackDetailsModel.getmAirFlowUnit());

        txt_serial_no_host.setText(rackDetailsModel.getmABlowerSerial());
        txt_customer_name_host.setText(rackDetailsModel.getmRackBlowerCustomerName());
        //txt_customer_name_host.setText("");
        mTxtUpdateHostName.setText(prefManager.getHostName());
    }

    private void mClickListerMethod() {
        mTxtSerialNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Utility.showAlertDialog(act,"Please contact administrator.","Ok");
                //EditTextAlertDialogBox("Serial No", mTxtSerialNo);
                if (mTxtSerialNo.getText().toString().equals("")) {
                    EditTextAlertDialogBox("Serial No", mTxtSerialNo);
                }
            }
        });

        mTxtHostName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTextAlertDialogBox("Host Name", mTxtHostName);
            }
        });

        txt_customer_name_host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTextAlertDialogBox("Customer Name", txt_customer_name_host);
            }
        });

        mTxtCustomerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTextAlertDialogBox("Customer Name", mTxtCustomerName);
            }
        });

        txt_serial_no_host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTextAlertDialogBox("Touch Screen Serial", txt_serial_no_host);
            }
        });
        //txt_serial_no_host


        mTxtUpdateHostName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTextAlertDialogBox("Update Host Name", mTxtUpdateHostName);
            }
        });

        mTxtGetDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hostAddress = mTxtHostName.getText().toString().trim();
                mSerialNumber = mTxtSerialNo.getText().toString().trim();
                mCustomerName = mTxtCustomerName.getText().toString().trim();
                if (TextUtils.isEmpty(hostAddress)) {
                    Toast.makeText(act, "Please enter server address", Toast.LENGTH_SHORT).show();
                } else if (!hostAddress.matches("((http)[s]?(://).*)")) {
                    Toast.makeText(act, "Server address is not valid..!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(mSerialNumber)) {
                    Toast.makeText(act, "Please enter serial number", Toast.LENGTH_SHORT).show();
                } else {
                    prefManager.setHostName(hostAddress);
                    getRackNewId_Api(mSerialNumber, mCustomerName);
                }
            }
        });

        mTxtBlowerDetailTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTxtBlowerDetailTab.setBackground(getResources().getDrawable(R.drawable.dark_blue_blwrdetl_screen_box));
                mLinearLayoutBlowerDetails.setVisibility(View.VISIBLE);
                mTxtOtherDetailsTab.setBackground(getResources().getDrawable(R.drawable.black_home_screen_textbox));
                mLinearLayoutOtherDetails.setVisibility(View.GONE);
                mTxtHostAddressTab.setBackground(getResources().getDrawable(R.drawable.black_home_screen_textbox));
                mLinearLayoutHostAddress.setVisibility(View.GONE);
                mTxtEmailTab.setBackground(getResources().getDrawable(R.drawable.black_home_screen_textbox));
                mLinearLayoutEmail.setVisibility(View.GONE);
                mTxtTouchScreenDetailTab.setBackground(getResources().getDrawable(R.drawable.black_home_screen_textbox));
                mLinearLayoutTouchScreen.setVisibility(View.GONE);
            }
        });

        mTxtTouchScreenDetailTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTxtTouchScreenDetailTab.setBackground(getResources().getDrawable(R.drawable.dark_blue_blwrdetl_screen_box));
                mLinearLayoutTouchScreen.setVisibility(View.VISIBLE);
                mTxtOtherDetailsTab.setBackground(getResources().getDrawable(R.drawable.black_home_screen_textbox));
                mLinearLayoutOtherDetails.setVisibility(View.GONE);
                mTxtHostAddressTab.setBackground(getResources().getDrawable(R.drawable.black_home_screen_textbox));
                mLinearLayoutHostAddress.setVisibility(View.GONE);
                mTxtEmailTab.setBackground(getResources().getDrawable(R.drawable.black_home_screen_textbox));
                mLinearLayoutEmail.setVisibility(View.GONE);
                mTxtBlowerDetailTab.setBackground(getResources().getDrawable(R.drawable.black_home_screen_textbox));
                mLinearLayoutBlowerDetails.setVisibility(View.GONE);
            }
        });

        mTxtOtherDetailsTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTxtOtherDetailsTab.setBackground(getResources().getDrawable(R.drawable.dark_blue_blwrdetl_screen_box));
                mLinearLayoutOtherDetails.setVisibility(View.VISIBLE);
                mTxtBlowerDetailTab.setBackground(getResources().getDrawable(R.drawable.black_home_screen_textbox));
                mLinearLayoutBlowerDetails.setVisibility(View.GONE);
                mTxtHostAddressTab.setBackground(getResources().getDrawable(R.drawable.black_home_screen_textbox));
                mLinearLayoutHostAddress.setVisibility(View.GONE);
                mTxtEmailTab.setBackground(getResources().getDrawable(R.drawable.black_home_screen_textbox));
                mLinearLayoutEmail.setVisibility(View.GONE);
                mTxtTouchScreenDetailTab.setBackground(getResources().getDrawable(R.drawable.black_home_screen_textbox));
                mLinearLayoutTouchScreen.setVisibility(View.GONE);

            }
        });

        mTxtHostAddressTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTxtHostAddressTab.setBackground(getResources().getDrawable(R.drawable.dark_blue_blwrdetl_screen_box));
                mLinearLayoutHostAddress.setVisibility(View.VISIBLE);
                mTxtOtherDetailsTab.setBackground(getResources().getDrawable(R.drawable.black_home_screen_textbox));
                mLinearLayoutOtherDetails.setVisibility(View.GONE);
                mTxtBlowerDetailTab.setBackground(getResources().getDrawable(R.drawable.black_home_screen_textbox));
                mLinearLayoutBlowerDetails.setVisibility(View.GONE);
                mTxtEmailTab.setBackground(getResources().getDrawable(R.drawable.black_home_screen_textbox));
                mLinearLayoutEmail.setVisibility(View.GONE);
                mTxtTouchScreenDetailTab.setBackground(getResources().getDrawable(R.drawable.black_home_screen_textbox));
                mLinearLayoutTouchScreen.setVisibility(View.GONE);
            }
        });

        mTxtEmailTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTxtEmailTab.setBackground(getResources().getDrawable(R.drawable.dark_blue_blwrdetl_screen_box));
                mLinearLayoutEmail.setVisibility(View.VISIBLE);
                mTxtHostAddressTab.setBackground(getResources().getDrawable(R.drawable.black_home_screen_textbox));
                mLinearLayoutHostAddress.setVisibility(View.GONE);
                mTxtOtherDetailsTab.setBackground(getResources().getDrawable(R.drawable.black_home_screen_textbox));
                mLinearLayoutOtherDetails.setVisibility(View.GONE);
                mTxtBlowerDetailTab.setBackground(getResources().getDrawable(R.drawable.black_home_screen_textbox));
                mLinearLayoutBlowerDetails.setVisibility(View.GONE);
                mTxtTouchScreenDetailTab.setBackground(getResources().getDrawable(R.drawable.black_home_screen_textbox));
                mLinearLayoutTouchScreen.setVisibility(View.GONE);
            }
        });

        mTxtUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hostAddress = mTxtUpdateHostName.getText().toString().trim();
                mCustomerName = txt_customer_name_host.getText().toString().trim();
                if (!hostAddress.matches("((http)[s]?(://).*)")) {
                    Toast.makeText(act, "Server address is not valid..!", Toast.LENGTH_SHORT).show();
                } else if (!TextUtils.isEmpty(hostAddress)) {
                    prefManager.setHostName(hostAddress);
                    Toast.makeText(act, "Server Address updated successfully..!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(act, "Please enter Host Address..!", Toast.LENGTH_SHORT).show();
                }

                getRackNewId_Api(rackDetailsModel.getmABlowerSerial(), mCustomerName);

            }
        });

        mLinearLayoutBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // onBackPressed();
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nBackPressedReportAndRack);
                ResetCounter(0);
                finish();
            }
        });

        radioGroup_aln_email_service_value.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                strAlnEmailServiceSelectValue = String.valueOf(checkedRadioButton.getTag());
                rackDetailsModel.setmUseAlnEmailService(Integer.parseInt(strAlnEmailServiceSelectValue));
            }
        });

        radioGroup_is_reg_alarm_on_value.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                strIsRegAlarmOnSelectValue = String.valueOf(checkedRadioButton.getTag());
                rackDetailsModel.setmIsRegAlarmOn(Integer.parseInt(strIsRegAlarmOnSelectValue));
            }
        });

        radioGroup_is_temp_hmd_alarm_value.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                strIsTempHmdAlarmSelectValue = String.valueOf(checkedRadioButton.getTag());
                rackDetailsModel.setmIsTmpHMDAlarmOn(Integer.parseInt(strIsTempHmdAlarmSelectValue));
            }
        });

    }

    // EditTextAlertDailogBox AlertDailogBox
    public void EditTextAlertDialogBox(final String type, final TextView textView) {
        Utility.hideNavigationBar(act);
        edittext_alertview_selection = new Dialog(act);
        edittext_alertview_selection.setCancelable(false);
//        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
//        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);
//        edittext_alertview_selection.getWindow().setLayout(width, height);

        edittext_alertview_selection.setContentView(R.layout.alertview_edittext_layout_rack_setup); // EditText Screen
        edittext_alertview_selection.getWindow().setGravity(Gravity.TOP);
//        alertview_selection.show();

        txt_Title_alartview_box = edittext_alertview_selection.findViewById(R.id.txt_Title_alartview_box);
        edit_EnterTxt_alartview_box = edittext_alertview_selection.findViewById(R.id.edit_EnterTxt_alartview_box);
        btn_Ok_alartview_box = edittext_alertview_selection.findViewById(R.id.btn_Save_alartview_box_report);
        btn_Cancel_alartview_box = edittext_alertview_selection.findViewById(R.id.btn_Cancel_alartview_box_report);

        txt_Title_alartview_box.setText(type);

        btn_Cancel_alartview_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edittext_alertview_selection != null && edittext_alertview_selection.isShowing()) {
                    edittext_alertview_selection.dismiss();
                    Utility.hideNavigationBar(act);
                }
//                alertview_selection.dismiss();
            }
        });

        edit_EnterTxt_alartview_box.requestFocus();

        btn_Ok_alartview_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals("Serial No")) {
                    if (TextUtils.isEmpty(edit_EnterTxt_alartview_box.getText().toString())) {
                        Toast.makeText(act, "Please enter serial no", Toast.LENGTH_SHORT).show();
                    } else {
                        if (edit_EnterTxt_alartview_box.getText().toString().length() == 0) {
                            Utility.ShowMessage(act, "Warning!", "Please Enter Vaild Text", "OK");
                        } else {
                            if (edittext_alertview_selection != null && edittext_alertview_selection.isShowing()) {
                                edittext_alertview_selection.dismiss();
                            }
                            textView.setText(edit_EnterTxt_alartview_box.getText().toString());
//                            edittext_alertview_selection.dismiss();
                        }
                    }
                } else if (type.equals("Host Name")) {
                    if (TextUtils.isEmpty(edit_EnterTxt_alartview_box.getText().toString())) {
                        Toast.makeText(act, "Please enter host name", Toast.LENGTH_SHORT).show();
                    } else {
                        if (edit_EnterTxt_alartview_box.getText().toString().length() == 0) {
                            Utility.ShowMessage(act, "Warning!", "Please Enter Vaild Text", "OK");
                        } else {
                            if (edittext_alertview_selection != null && edittext_alertview_selection.isShowing()) {
                                edittext_alertview_selection.dismiss();
                            }
                            textView.setText(edit_EnterTxt_alartview_box.getText().toString());
                            prefManager.setHostName(edit_EnterTxt_alartview_box.getText().toString());
//                            edittext_alertview_selection.dismiss();
                        }
                    }
                } else if (type.equals("Update Host Name")) {
                    if (TextUtils.isEmpty(edit_EnterTxt_alartview_box.getText().toString())) {
                        Toast.makeText(act, "Please enter host name", Toast.LENGTH_SHORT).show();
                    } else {
                        if (edit_EnterTxt_alartview_box.getText().toString().length() == 0) {
                            Utility.ShowMessage(act, "Warning!", "Please Enter Vaild Text", "OK");
                        } else {
                            if (edittext_alertview_selection != null && edittext_alertview_selection.isShowing()) {
                                edittext_alertview_selection.dismiss();
                            }
                            textView.setText(edit_EnterTxt_alartview_box.getText().toString());
                            prefManager.setHostName(edit_EnterTxt_alartview_box.getText().toString());
//                            edittext_alertview_selection.dismiss();
                        }
                    }
                } else if (type.equals("Customer Name")) {
                    if (TextUtils.isEmpty(edit_EnterTxt_alartview_box.getText().toString())) {
                        Toast.makeText(act, "Please enter Customer Name", Toast.LENGTH_SHORT).show();
                    } else {
                        if (edit_EnterTxt_alartview_box.getText().toString().length() == 0) {
                            Utility.ShowMessage(act, "Warning!", "Please Enter Vaild Text", "OK");
                        } else {
                            if (edittext_alertview_selection != null && edittext_alertview_selection.isShowing()) {
                                edittext_alertview_selection.dismiss();
                            }
                            textView.setText(edit_EnterTxt_alartview_box.getText().toString());
                            //prefManager.setHostName(edit_EnterTxt_alartview_box.getText().toString());
//                            edittext_alertview_selection.dismiss();
                        }
                    }
                }
                Utility.hideNavigationBar(act);
            }
        });

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // show dialog here
                if (!isFinishing()) {
                    edittext_alertview_selection.show();
                }
            }
        });

    }

    private void getRackNewId_Api(String serialNumber, String customerName) {
        if (prefManager.getHostName() == null || !prefManager.getHostName().contains("http")) {
            Log.e("HostName :- ", "Host Name is Not Available");
            return;
        }
        relative_progress_rack_detail_screen.setVisibility(View.VISIBLE);
        // if (NetworkUtil.getConnectivityStatus(act)) {
        JSONObject objParam = new JSONObject();
        try {
            objParam.put(ApiHandler.strGetNewIDForRackBlowerSerialNumber, serialNumber);
            objParam.put(ApiHandler.strGetNewIDForRackBlowerCustomerName, customerName);


            //string SerialNumber, string Customer, string ABlowerWiFiMAC, string ABlowerLANMAC, string ABlowerBluetoothMAC, string ABlowerWiFiIPAddress, string ABlowerLANIPAddress)
            objParam.put(ApiHandler.strUpdateABlowerWifiMac, mWiFiMac);
            objParam.put(ApiHandler.strUpdateABlowerLANMac, mLanMac);
            objParam.put(ApiHandler.strUpdateABlowerBluetoothMac, mBluetoothMac);
            objParam.put(ApiHandler.strUpdateABlowerWiFiIPAddress, mWiFiIPAddress);
            objParam.put(ApiHandler.strUpdateABlowerLANIPAddress, mLanIPAddress);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("objParam For New RackId", "" + objParam.toString());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                prefManager.getHostName() + ApiHandler.strUrlGetNewIDForRackBlower, objParam,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        relative_progress_rack_detail_screen.setVisibility(View.GONE);
                        Utility.Log("getRackNewId_Api_Response : " + jsonObject);
                        try {
                            if (jsonObject.getBoolean("result")) {
                                dpHelper.saveNewIDForRackBlowerInDataBase(jsonObject);
                                CodeReUse.isCustomerActive = true;
                                getRackBlowerDetails_Api(dpHelper.getString(jsonObject, ApiHandler.strRackSerialNumberId), dpHelper.getString(jsonObject, ApiHandler.strRackBlowerCustomerID));
                            } else {

                                if (jsonObject.has("message")) {
                                    Utility.Log("RackNewId_Api_Response Fail : " + jsonObject.getString("message"));
                                    Utility.showAlertDialog(act, jsonObject.getString("message"), getString(R.string.ok));
                                    if (jsonObject.has("IsCustomerActive")) {
                                        if (!jsonObject.getBoolean("IsCustomerActive")) {
                                            //stop sending the data
                                            CodeReUse.isCustomerActive = false;
                                        } else {
                                            CodeReUse.isCustomerActive = true;
                                        }
                                    }
                                } else
                                    Utility.showAlertDialog(act, getString(R.string.error), getString(R.string.ok));

                            }
                        } catch (JSONException e) {
                            Utility.Log("getRackNewIdresponse Error : " + e.toString());
                            e.printStackTrace();
                            Utility.showAlertDialog(act, getString(R.string.error), getString(R.string.ok));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        relative_progress_rack_detail_screen.setVisibility(View.GONE);
                        Utility.Log("RackNewId_Api Error : " + error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return allentownBlowerApplication.getInstance().getHeader();
            }
        };

        allentownBlowerApplication.getInstance().cancelPendingRequests(PendingID.nGetNewIDForRackBlower);
        allentownBlowerApplication.getInstance().addToRequestQueue(request, PendingID.nGetNewIDForRackBlower);
    }

    @SuppressLint("LongLogTag")
    public void getRackBlowerDetails_Api(String id, String customerId) {
        if (prefManager.getHostName() == null || !prefManager.getHostName().contains("http")) {
            Log.e("HostName :- ", "Host Name is Not Available");
            return;
        }
        relative_progress_rack_detail_screen.setVisibility(View.VISIBLE);
        // if (NetworkUtil.getConnectivityStatus(act)) {
        JSONObject objParam = new JSONObject();
        try {
            objParam.put(ApiHandler.strUpdateRackBlowerDetailsId, id);
            objParam.put(ApiHandler.strUpdateRackBlowerCustomerID, customerId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("getRackBlowerDetailsobjParam", "" + objParam.toString());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                prefManager.getHostName() + ApiHandler.strUrlGetUpdateRackBlowerDetails, objParam,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        relative_progress_rack_detail_screen.setVisibility(View.GONE);
                        Utility.Log("getRackBlowerDetails_Api_Response : " + jsonObject);
                        try {
                            if (jsonObject.getBoolean("result")) {
                                dpHelper.updateRackBlowerDetailsInDataBase(jsonObject);
                                rackDetailsModel = dpHelper.getDataFromRackBlowerDetails();
                                rackDetailsModel.setmRackBlowerCustomerName(jsonObject.getString("CustomerName"));
                            } else {
                                if (jsonObject.has("message")) {
                                    Utility.Log("getRackBlowerDetails_Api_Response Fail : " + jsonObject.getString("message"));
                                    Utility.showAlertDialog(act, jsonObject.getString("message"), getString(R.string.ok));
                                } else
                                    Utility.showAlertDialog(act, getString(R.string.error), getString(R.string.ok));

                            }
                            showDetailWhenScreenLoad();
                        } catch (JSONException e) {
                            Utility.Log("getRackBlowerDetailsresponse_Api Error : " + e.toString());
                            e.printStackTrace();
                            showDetailWhenScreenLoad();
                            Utility.showAlertDialog(act, getString(R.string.error), getString(R.string.ok));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        relative_progress_rack_detail_screen.setVisibility(View.GONE);
                        showDetailWhenScreenLoad();
                        Utility.Log("getRackBlowerDetails_Api Error : " + error.toString());
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
                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nTimerFinishedReportAndRack);
                    finish();
                }
            } else {
                Utility.Log(TAG, "onFinish");
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nTimerFinishedReportAndRack);
                finish();
//                try {
//                    if (alertview_pre_filter_reset.isShowing()) {
//                        alertview_pre_filter_reset.dismiss();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

//                try {
//                    if (alertview_selection.isShowing()) {
//                        alertview_selection.dismiss();
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

//                try {
//                    if (Utility.alertview_setting_password.isShowing()) {
//                        Utility.alertview_setting_password.dismiss();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                // ServiceCommunication
                //if (!isMyServiceRunning(MyService.class)) {
//                if (service_myservice == null) {
//                    if (!alertview_diagnostics.isShowing() && !alertview_diagnostics_details.isShowing()) {
//                        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
//                    }
//
//                }

//                final int sdk = android.os.Build.VERSION.SDK_INT;
//                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
//                    layout_menubar.setBackgroundDrawable(ContextCompat.getDrawable(act, R.drawable.dark_green_home_screen_box));
//                } else {
//                    layout_menubar.setBackground(ContextCompat.getDrawable(act, R.drawable.dark_green_home_screen_box));
//                }
            }
        }
    };

    // CountDownTimer Start or Stop
    private void ResetCounter(int isCounterRunning) { // 1 = start & restart // 0 = stop
        if (CodeReUse.isBolwerAdmin) {
            if (CodeReUse.isBolwerConnected) {
                if (isCounterRunning == 1) {
                    mCountDownTimer.cancel(); // cancel
                    mCountDownTimer.start();  // then restart
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
                Utility.Log(TAG, "onStart");
            } else if (isCounterRunning == 0) {
                mCountDownTimer.cancel(); // cancel
                Utility.Log(TAG, "onCancel");
            }
        }
    }

    public String getSerialNumber() {
        String serialNumber;
        //return "";/*
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

            if (serialNumber.equals("")) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return "";
                    }
                    serialNumber = Build.getSerial();
                }
            }



            // If none of the methods above worked
            if (serialNumber.equals(Build.UNKNOWN))
                serialNumber = "";

            Log.e("TAG","Serial Number from rackdetailscreenactivity getSerialNumber method: " + serialNumber);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG","RackDetails getSerialNumber method Error: " + e.getMessage().toString());
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

    public static String getWiFiIPAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                //Log.e("TAG","Name : " + nif.getName());
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                for (Enumeration<InetAddress> enumIpAddr = nif.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }

                return "";

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

    public static String getLANIPAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                //Log.e("TAG","Name : " + nif.getName());
                if (!nif.getName().equalsIgnoreCase("eth0")) continue;

                for (Enumeration<InetAddress> enumIpAddr = nif.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }

                return "";
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
        } catch (Exception ex){

        }
        return bluetoothMacAddress;
    }
}