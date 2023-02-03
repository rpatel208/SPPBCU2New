package com.allentownblower.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.allentownblower.R;
import com.allentownblower.adapter.SpinnerACHAdapter;
import com.allentownblower.adapter.SpinnerExhaustAdapter;
import com.allentownblower.adapter.SpinnerModelNoAdapter;
import com.allentownblower.adapter.SpinnerPolarityAdapter;
import com.allentownblower.adapter.SpinnerSupplyAdapter;
import com.allentownblower.application.AllentownBlowerApplication;
import com.allentownblower.common.CodeReUse;
import com.allentownblower.common.ObserverActionID;
import com.allentownblower.common.PrefManager;
import com.allentownblower.common.ResponseHandler;
import com.allentownblower.common.Utility;
import com.allentownblower.communication.SerialPortConversion;
import com.allentownblower.database.DatabaseTable;
import com.allentownblower.database.SqliteHelper;
import com.allentownblower.module.RackDetailsModel;
import com.allentownblower.module.RackModel;
import com.allentownblower.module.RackSetupModel;
import com.allentownblower.module.SetPointCommand;
import com.allentownblower.module.SpinnerObject;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class RackSetUpNewActivity extends AppCompatActivity implements Observer {
    private static final String TAG = "RackSetUpActivity";
    private TextView mTextViewModelNumber, mEditTextSupply, mEditTextExhaust, mTextViewCompanyName, mTextAC, mTextDC;
    private Spinner mSpinnerModelNumber;
    private Spinner mSpinnerACH, mSpinnerPolarity, mSpinnerSupply, mSpinnerExhaust;
    //    private Button mButtonSave, mButtonFind, mButtonReset, btn_W;
    private TextView mButtonSave, mButtonFind, mButtonReset, btn_W;
    private SqliteHelper dpHelper;
    private Activity act;
    private int mCount;
    private ArrayList<SpinnerObject> arrayListACH = new ArrayList<>();
    private ArrayList<SpinnerObject> arrayListPolarity = new ArrayList<>();
    private ArrayList<SpinnerObject> arrayListSupply = new ArrayList<>();
    private ArrayList<SpinnerObject> arrayListExhaust = new ArrayList<>();
    private SpinnerACHAdapter mSpinnerACHAdapter;
    private SpinnerModelNoAdapter mSpinnerModelNoAdapter;
    private SpinnerPolarityAdapter mSpinnerPolarityAdapter;
    private SpinnerExhaustAdapter mSpinnerExhaustAdapter;
    private SpinnerSupplyAdapter mSpinnerSupplyAdapter;
    private String subModelNo;
    private String ACHValue = "", polarityValue = "", supplyValue = "", exhaustValue = "", exhaustSelectedSpinnerValue = "", supplySelectedSpinnerValue = "";
    public RelativeLayout mRelativeProgressBarLayoutRackScreen;
    private String userEnterSupplyValue, userEnterExhaustValue, mStrCompanyName;
    private String modelNo, mStrBlowerName, mStrBuildingName, mStrRoomName;
    private boolean isEditTextExhaust = false;
    private boolean isEditTextSupply = false;
    private TextView mTextViewSupplyValue, mTextViewExhaustValue, btn_back;
    private LinearLayout mLinearLayoutLock;
    private RadioGroup mRadioGroupLock;
    private boolean isSpinnerExhaustShow;
    private boolean isSpinnerSupplyShow;
    private int posPolarity = 0;
    private int posACH = 0;
    private RadioButton radioLockButton;
    private String mLockValue = "";
    private Intent service_diagnostic, service_myservice;
    private SerialPortConversion portConversion;
    private ResponseHandler responseHandler;
    private PrefManager prefManager;
    private AllentownBlowerApplication allentownBlowerApplication;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<SetPointCommand> setpointArrayList = new ArrayList<>(); // "S01","S02","S03","S04","S05","S06","S07","S08","S09","S10","S11","S12","S13","S14","S15","S16","S17","S18","S19","S20","S21"
    private String polarityValueId = "";
    private Dialog alertview;
    private int isSetUpCompleted;
    private LinearLayout mLinerLayoutError, mLinearLayoutButtons, linear_main_layout, linear_layout_buttons_back;
    private TextView txt_ach, txt_polarity, txt_supply, txt_exhaust, txt_Room_Name, txt_Blower_Name, txt_building_name;
    private ImageView img_ach, img_polarity, img_supply, img_exhaust,img_model_no;
    private Dialog alertview_selection;
    // Dailog CommunicationSettingActivity
    private EditText edit_EnterTxt_alartview_box;
    private TextView txt_Title_alartview_box, btn_Cancel_alartview_box, btn_Ok_alartview_box;
    private LinearLayout layout_dailog_S01_X1_X0, layout_dailog_timer, linear_layout_dialog_buttons;
    private TextView txt_dailog_S01_X1_X0, txt_dailog_timmer;
    private String ipAddress = "";
    private Dialog alertview_Save_Dialog;
    private TextView txt_dailogMassage, btn_dailogYes, btn_dailogNo;
    private boolean isFromSettingScreen = false;
    private RackModel rackModel = null;
    private RadioButton radioButtonWithOut, radioButtonWith;
    private int currentApiVersion;
    private String spinnerSelectedACHValue = "";
    private RackDetailsModel rackDetailsModel = null;
    private RackSetupModel rackSetupModel = null;
    private ArrayList<String> arrModelNo=new ArrayList<>();
    ArrayAdapter<String> spinnerArrayAdapter;
    ArrayList<RackSetupModel> arrRackSetupList = new ArrayList<>();

    String ac="";
    String dc="";

    @SuppressLint("WifiManagerLeak")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;
//        Utility.setSoftInputAlwaysHide(act);
        Utility.hideNavigationBar(act);
        //Utility.getBroadCast(act);
        allentownBlowerApplication = (AllentownBlowerApplication) act.getApplication();
        allentownBlowerApplication.getObserver().addObserver(this);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        dpHelper = new SqliteHelper(act);
//        isSetUpCompleted = dpHelper.isSetupCompletedCheckFromDataBase();
        ArrayList<RackModel> arrRackList = new ArrayList<>();

        arrRackList = dpHelper.getDataFromRackSetUpTable();
        arrRackSetupList = dpHelper.getAllDataFromRackSetUpTableNew();

        if (arrRackList.size() > 0) {
            rackModel = arrRackList.get(0);
        }

        if(arrRackSetupList.size() > 0) {
            rackSetupModel = arrRackSetupList.get(0);

            for(int i=0;i<arrRackSetupList.size();i++) {
                arrModelNo.add(arrRackSetupList.get(i).getModelNo());
            }
        }

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            isFromSettingScreen = false;
        } else {
            isFromSettingScreen = extras.getBoolean("isFromSettingScreen", false);
            responseHandler = (ResponseHandler) getIntent().getSerializableExtra("responseHandler");
            portConversion = (SerialPortConversion) getIntent().getSerializableExtra("portConversion");
            prefManager = (PrefManager) getIntent().getSerializableExtra("prefManager");
        }

        if (!isFromSettingScreen) {
            if (arrRackList.size() > 0) {
                Intent intent = new Intent(act, HomeActivity.class);
                startActivity(intent);
                finish();
                allentownBlowerApplication.getObserver().deleteObserver(this);
                return;
            }
        }
        setContentView(R.layout.activity_new_rack_set_up);

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available!", Toast.LENGTH_SHORT).show();
            //finish();
        }

        initMethod();

        /*spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrModelNo);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinnerModelNumber.setAdapter(spinnerArrayAdapter);
        mSpinnerModelNumber.setSelection(0);*/

        mTextAC.setText(String.valueOf(rackSetupModel.getACsetpt()));
        mTextDC.setText(String.valueOf(rackSetupModel.getDCsetpt()));

        responseHandler = new ResponseHandler(act);
        portConversion = new SerialPortConversion(act, responseHandler);
        prefManager = new PrefManager(act);

        if (CodeReUse.isTestingMode) {
            mTextViewCompanyName.setText(getResources().getString(R.string.comapny_name));
            txt_Blower_Name.setText(getResources().getString(R.string.blower_name));
            txt_building_name.setText(getResources().getString(R.string.building_name));
            txt_Room_Name.setText(getResources().getString(R.string.room_name));
//            mTextViewModelNumber.setText(getResources().getString(R.string.model_name));
        }

//        mVisibilityShowHideFunction();
        mClickEventFunction();
        connectionToBlower();

        WifiManager wm = (WifiManager) act.getSystemService(WIFI_SERVICE);
        ipAddress = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        Log.e("ipAddress", ipAddress);

        if (arrRackList.size() > 0) {
            mTextViewCompanyName.setText(rackModel.getCompanyName());
            txt_Blower_Name.setText(rackModel.getBlowerName());
            txt_building_name.setText(rackModel.getBuildingName());
            txt_Room_Name.setText(rackModel.getRoomName());
//            mTextViewModelNumber.setText("ME" + rackModel.getModelNo());

            mVisibilityShowHideFunction();
            mSpinnerACH.setSelection(getIndex(mSpinnerACH, String.valueOf(rackModel.getACH())));
        }

        if (isFromSettingScreen){
            linear_layout_buttons_back.setVisibility(View.VISIBLE);
            mButtonFind.setVisibility(View.GONE);
            img_model_no.setVisibility(View.VISIBLE);
            ResetCounter(1);
        }else {
            linear_layout_buttons_back.setVisibility(View.GONE);
            mButtonFind.setVisibility(View.VISIBLE);
            img_model_no.setVisibility(View.GONE);
            ResetCounter(0);
        }

//        if (isFromSettingScreen){
//            isFromSettingScreenShowAllFields();
//        }

//        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointDataOnly);
    }

    //private method of your class
    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            SpinnerObject obj = (SpinnerObject) spinner.getItemAtPosition(i);
            if (obj.getName().equalsIgnoreCase(myString)) {
                return i;
            }
        }
        return 0;
    }

    private void initMethod() {

        mTextDC = findViewById(R.id.val_dc);
        mTextAC = findViewById(R.id.val_ac);

        mSpinnerModelNumber = findViewById(R.id.spinner_model_no);
        mTextViewModelNumber = findViewById(R.id.txt_model_no);
        mTextViewCompanyName = findViewById(R.id.txt_company_name);

        txt_Blower_Name = findViewById(R.id.txt_Blower_Name);
        txt_building_name = findViewById(R.id.txt_building_name);
        txt_Room_Name = findViewById(R.id.txt_Room_Name);

        mLinearLayoutButtons = findViewById(R.id.linear_layout_buttons);
        linear_main_layout = findViewById(R.id.linear_main_layout);

        mTextViewSupplyValue = findViewById(R.id.text_supply_value);
        mTextViewExhaustValue = findViewById(R.id.text_exhaust_value);

        txt_ach = findViewById(R.id.txt_ach);
        txt_polarity = findViewById(R.id.txt_polarity);
        txt_supply = findViewById(R.id.txt_supply);
        txt_exhaust = findViewById(R.id.txt_exhaust);

        img_ach = findViewById(R.id.img_ach);
        img_polarity = findViewById(R.id.img_polarity);
        img_supply = findViewById(R.id.img_supply);
        img_exhaust = findViewById(R.id.img_exhaust);
        img_model_no = findViewById(R.id.img_model_no);

        mSpinnerSupply = findViewById(R.id.spinner_supply);
        mSpinnerExhaust = findViewById(R.id.spinner_exhaust);

        mSpinnerACH = findViewById(R.id.spinner_ach);
        mSpinnerPolarity = findViewById(R.id.spinner_polarity);
        mButtonSave = findViewById(R.id.btn_save);
        mButtonReset = findViewById(R.id.btn_reset);
        mButtonFind = findViewById(R.id.btn_find);
        mLinearLayoutLock = findViewById(R.id.linear_layout_w_o_lock);
        mRelativeProgressBarLayoutRackScreen = findViewById(R.id.relative_progress_rack_screen);
        mRadioGroupLock = findViewById(R.id.radioGroup);
        radioButtonWith = findViewById(R.id.radioButtonWith);
        radioButtonWithOut = findViewById(R.id.radioButtonWithOut);

        mLinerLayoutError = findViewById(R.id.linear_layout_error);
        btn_back = findViewById(R.id.txt_back);
        linear_layout_buttons_back = findViewById(R.id.linear_layout_buttons_back);

        btn_W = findViewById(R.id.btn_W);
    }

    private void mClickEventFunction() {

        /*mTextDC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogBox("DC", mTextDC);
            }
        });

        mTextAC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogBox("AC", mTextAC);
            }
        });*/

        mSpinnerModelNoAdapter = new SpinnerModelNoAdapter(act, arrModelNo);
        mSpinnerModelNoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerModelNumber.setAdapter(mSpinnerModelNoAdapter);
        mSpinnerModelNumber.setSelection(0);
        mSpinnerModelNumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG,"ID : "+id+"Position : "+position);
                if(position!=0) {
                    mTextDC.setText(String.valueOf(arrRackSetupList.get(position - 1).getDCsetpt()));
                    mTextAC.setText(String.valueOf(arrRackSetupList.get(position - 1).getACsetpt()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mButtonFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFromSettingScreen){
                    resetFunction();
                    if (!validationFunction()) {
//                        mVisibilityShowHideFunction();
                        mVisibilityShowHideFn();
                    }
                }
//                hideKeyBoardMethod();
            }
        });

        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFunction();
            }
        });

        mTextViewCompanyName.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    hideKeyBoardMethod();
                    return true;
                }
                return false;
            }
        });

//        mEditTextExhaust.setOnEditorActionListener(new EditText.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
////                    hideKeyBoardMethod();
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        mEditTextSupply.setOnEditorActionListener(new EditText.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
////                    hideKeyBoardMethod();
//                    return true;
//                }
//                return false;
//            }
//        });

        mTextViewModelNumber.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    hideKeyBoardMethod();
                    return true;
                }
                return false;
            }
        });


        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFromSettingScreen) {
                    if (!validationOnSaveButton()) {
                        ShowRackSetUpDetailSaveDialog("Do you want to setup new blower?\n" +
                                "All existing data will be deleted.", "YES", "NO");
                    }
                } else {
                    saveButtonFunction();
                }
            }
        });

        btn_W.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallReadWriteFuncation("W", 0);
            }
        });

        mTextViewCompanyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialogBox("Company Name", mTextViewCompanyName);
            }
        });

        txt_Blower_Name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialogBox("Blower Name", txt_Blower_Name);
            }
        });

        txt_building_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialogBox("Building Name", txt_building_name);
            }
        });

        txt_Room_Name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialogBox("Room Name", txt_Room_Name);
            }
        });

        mTextViewModelNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialogBox("Model Number", mTextViewModelNumber);
            }
        });

        mTextViewSupplyValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogBox("Supply(CFM)", mTextViewSupplyValue);
            }
        });

        img_supply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialogBox("Supply(CFM)", mTextViewSupplyValue);
            }
        });

        mTextViewExhaustValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogBox("Exhaust(WC)", mTextViewExhaustValue);
            }
        });

        img_exhaust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialogBox("Exhaust(WC)", mTextViewExhaustValue);
            }
        });

        linear_layout_buttons_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nBackPressedReportAndRack);
                ResetCounter(0);
                finish();
            }
        });
    }

    private void saveButtonFunction() {

//        mStrCompanyName = mTextViewCompanyName.getText().toString().trim();
//        mStrBlowerName = txt_Blower_Name.getText().toString().trim();
//        mStrBuildingName = txt_building_name.getText().toString().trim();
//        mStrRoomName = txt_Room_Name.getText().toString().trim();
//        modelNo = mTextViewModelNumber.getText().toString().trim().toUpperCase();
//        if (TextUtils.isEmpty(mStrCompanyName)) {
//            Toast.makeText(act, "Please enter company name", Toast.LENGTH_SHORT).show();
//            return;
//        } else if (TextUtils.isEmpty(mStrBlowerName)) {
//            Toast.makeText(act, "Please enter blower name", Toast.LENGTH_SHORT).show();
//            return;
//        } else if (TextUtils.isEmpty(mStrBuildingName)) {
//            Toast.makeText(act, "Please enter building name", Toast.LENGTH_SHORT).show();
//            return;
//        } else if (TextUtils.isEmpty(mStrRoomName)) {
//            Toast.makeText(act, "Please enter room number", Toast.LENGTH_SHORT).show();
//            return;
//        } else {
//            mRelativeProgressBarLayoutRackScreen.setVisibility(View.VISIBLE);
////            supplyValue = mTextViewSupplyValue.getText().toString().trim();
////            Log.e("Values", "SupplyValue : " + supplyValue);
//        }
//
//        if (isEditTextSupply) {
//            userEnterSupplyValue = mTextViewSupplyValue.getText().toString().trim();
//            boolean isNumber = isValidNumber(userEnterSupplyValue);
//            if (TextUtils.isEmpty(userEnterSupplyValue) || !isNumber) {
//                Toast.makeText(act, "Please enter Supply(CFM) value", Toast.LENGTH_SHORT).show();
//                mRelativeProgressBarLayoutRackScreen.setVisibility(View.GONE);
//                return;
//            } else {
//                mRelativeProgressBarLayoutRackScreen.setVisibility(View.VISIBLE);
//                supplyValue = userEnterSupplyValue;
//            }
//            Log.e("Values", "SupplyValue : " + supplyValue);
//        } else if (isSpinnerSupplyShow) {
//            supplyValue = supplySelectedSpinnerValue;
//            Log.e("Values", "SupplyValue : " + supplyValue);
//        } else{
//            supplyValue = mTextViewSupplyValue.getText().toString().trim();
//            Log.e("Values", "SupplyValue : " + supplyValue);
//        }
//
//        if (isEditTextExhaust) {
//            userEnterExhaustValue = mTextViewExhaustValue.getText().toString().trim();
//            boolean isNumber = isValidNumber(userEnterExhaustValue);
//            if (TextUtils.isEmpty(userEnterExhaustValue) || !isNumber) {
//                Toast.makeText(act, "Please enter Exhaust(WC) value", Toast.LENGTH_SHORT).show();
//                mRelativeProgressBarLayoutRackScreen.setVisibility(View.GONE);
//                return;
//            } else {
//                exhaustValue = userEnterExhaustValue;
//            }
//            Log.e("Values", "ExhaustValue : " + exhaustValue);
//        } else if (isSpinnerExhaustShow) {
//            exhaustValue = exhaustSelectedSpinnerValue;
//            Log.e("Values", "ExhaustValue : " + exhaustValue);
//        } else {
//            exhaustValue = mTextViewExhaustValue.getText().toString().trim();
//            Log.e("Values", "ExhaustValue : " + exhaustValue);
//        }
//
//        if(ACHValue.isEmpty() || ACHValue.equalsIgnoreCase("Choose ACH Value") || ACHValue.equalsIgnoreCase("")){
//            Toast.makeText(act, "Please Select ACH value", Toast.LENGTH_SHORT).show();
//            mRelativeProgressBarLayoutRackScreen.setVisibility(View.GONE);
//            return;
//        }
//
//        if(polarityValue.isEmpty() || polarityValue.equalsIgnoreCase("")){
//            Toast.makeText(act, "Please Select polarity value", Toast.LENGTH_SHORT).show();
//            mRelativeProgressBarLayoutRackScreen.setVisibility(View.GONE);
//            return;
//        }
//
//        if (mLinearLayoutLock.getVisibility() == View.VISIBLE) {
//                int selectedId = mRadioGroupLock.getCheckedRadioButtonId();
//                radioLockButton = (RadioButton) findViewById(selectedId);
//                mLockValue = String.valueOf(radioLockButton.getTag());
//                Log.e("Values", "Lock Value :- " + mLockValue);
//        }
//        Log.e("Values", "PolarityValue : " + polarityValue);
//                if (!userEnterSupplyValue.isEmpty() || !userEnterSupplyValue.equalsIgnoreCase("")){

        if (!validationOnSaveButton()){

            /*if (isEditTextSupply) {
                userEnterSupplyValue = mTextViewSupplyValue.getText().toString().trim();
                boolean isNumber = isValidNumber(userEnterSupplyValue);
                if (TextUtils.isEmpty(userEnterSupplyValue) || !isNumber) {
                    Toast.makeText(act, "Please enter Supply(CFM) value", Toast.LENGTH_SHORT).show();
                    mRelativeProgressBarLayoutRackScreen.setVisibility(View.GONE);
                    return;
                } else {
                    mRelativeProgressBarLayoutRackScreen.setVisibility(View.VISIBLE);
                    supplyValue = userEnterSupplyValue;
                }
                Log.e("Values", "SupplyValue : " + supplyValue);
            } else if (isSpinnerSupplyShow) {
                supplyValue = supplySelectedSpinnerValue;
                Log.e("Values", "SupplyValue : " + supplyValue);
            } else{
                supplyValue = mTextViewSupplyValue.getText().toString().trim();
                Log.e("Values", "SupplyValue : " + supplyValue);
            }*/

            /*if (isEditTextExhaust) {
                userEnterExhaustValue = mTextViewExhaustValue.getText().toString().trim();
                boolean isNumber = isValidNumber(userEnterExhaustValue);
                if (TextUtils.isEmpty(userEnterExhaustValue) || !isNumber) {
                    Toast.makeText(act, "Please enter Exhaust(WC) value", Toast.LENGTH_SHORT).show();
                    mRelativeProgressBarLayoutRackScreen.setVisibility(View.GONE);
                    return;
                } else {
                    exhaustValue = userEnterExhaustValue;
                }
                Log.e("Values", "ExhaustValue : " + exhaustValue);
            } else if (isSpinnerExhaustShow) {
                exhaustValue = exhaustSelectedSpinnerValue;
                Log.e("Values", "ExhaustValue : " + exhaustValue);
            } else {
                exhaustValue = mTextViewExhaustValue.getText().toString().trim();
                Log.e("Values", "ExhaustValue : " + exhaustValue);
            }*/

            /*if (isEditTextSupply) {
                dpHelper.updateValueIntoDatabase(subModelNo, Utility.getCurrentTimeStamp(), "", Utility.getCurrentTimeStamp(), 1,
                        ACHValue, polarityValue, supplyValue, exhaustValue, mLockValue, 1, 0, mStrCompanyName, mStrBlowerName, mStrBuildingName, mStrRoomName, ipAddress, rackModel != null ? String.valueOf(rackModel.getSupplyCFM()) : "", rackModel != null ? rackModel.getExhaustWC() : "");
//                }else if (!userEnterExhaustValue.isEmpty() || !userEnterExhaustValue.equalsIgnoreCase("")){
            } else if (isEditTextExhaust) {
                dpHelper.updateValueIntoDatabase(subModelNo, Utility.getCurrentTimeStamp(), "", Utility.getCurrentTimeStamp(), 1,
                        ACHValue, polarityValue, supplyValue, exhaustValue, mLockValue, 0, 1, mStrCompanyName, mStrBlowerName, mStrBuildingName, mStrRoomName, ipAddress, rackModel != null ? String.valueOf(rackModel.getSupplyCFM()) : "", rackModel != null ? rackModel.getExhaustWC() : "");
            } else {*/
            ac = mTextAC.getText().toString().trim();
            dc = mTextDC.getText().toString().trim();
            dpHelper.updateValueIntoDatabase(modelNo, Utility.getCurrentTimeStamp(), "", Utility.getCurrentTimeStamp(), 1,
                    mStrCompanyName, mStrBlowerName, mStrBuildingName, mStrRoomName, ipAddress, ac,dc);
//            }

//                dpHelper.updateValueIntoDatabase(subModelNo, Utility.getCurrentTimeStamp(), "", Utility.getCurrentTimeStamp(), 1,
//                        ACHValue, polarityValue, supplyValue, exhaustValue, mLockValue);
//                Toast.makeText(act, "Updated Succesfully", Toast.LENGTH_SHORT).show();
            if (!isFromSettingScreen){
                prefManager.setModelName(modelNo);
                //AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetACCommand);
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetDCCommand);
            } else {

                rackDetailsModel = dpHelper.getDataFromRackBlowerDetails();
                if (rackDetailsModel != null) {
                    if (!prefManager.getModelName().equalsIgnoreCase(modelNo)){
                        if (prefManager.getHostName() != null || prefManager.getHostName().contains("http")){
                            prefManager.setModelName(modelNo);
                            responseHandler.getUpdateRackBlowerNumber_Api(act,prefManager,allentownBlowerApplication,rackDetailsModel,mStrBlowerName,mStrBuildingName,mStrRoomName,subModelNo,dpHelper);
                        }
                    }
                }
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nRackSetUp_ACH_Value_Write_Only_From_Setting_Screen);
            }
        }

    }

    private boolean validationOnSaveButton(){
        mStrCompanyName = mTextViewCompanyName.getText().toString().trim();
        mStrBlowerName = txt_Blower_Name.getText().toString().trim();
        mStrBuildingName = txt_building_name.getText().toString().trim();
        mStrRoomName = txt_Room_Name.getText().toString().trim();
        modelNo = mSpinnerModelNumber.getSelectedItem().toString();
//        modelNo = mTextViewModelNumber.getText().toString().trim().toUpperCase();
        if (TextUtils.isEmpty(mStrCompanyName)) {
            Toast.makeText(act, "Please enter company name", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(mStrBlowerName)) {
            Toast.makeText(act, "Please enter blower name", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(mStrBuildingName)) {
            Toast.makeText(act, "Please enter building name", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(mStrRoomName)) {
            Toast.makeText(act, "Please enter room number", Toast.LENGTH_SHORT).show();
            return true;
        } else {
//            return false;
//            mRelativeProgressBarLayoutRackScreen.setVisibility(View.VISIBLE);
//            supplyValue = mTextViewSupplyValue.getText().toString().trim();
//            Log.e("Values", "SupplyValue : " + supplyValue);
        }

        /*if(ACHValue.isEmpty() || ACHValue.equalsIgnoreCase("Choose ACH Value") || ACHValue.equalsIgnoreCase("")){
            Toast.makeText(act, "Please Select ACH value", Toast.LENGTH_SHORT).show();
            mRelativeProgressBarLayoutRackScreen.setVisibility(View.GONE);
            return true;
        }else if(polarityValue.isEmpty() || polarityValue.equalsIgnoreCase("Choose Polarity Value") || polarityValue.equalsIgnoreCase("")){
            Toast.makeText(act, "Please Select polarity value", Toast.LENGTH_SHORT).show();
            mRelativeProgressBarLayoutRackScreen.setVisibility(View.GONE);
            return true;
        }*/

//        if (isEditTextSupply) {
//            userEnterSupplyValue = mTextViewSupplyValue.getText().toString().trim();
//            boolean isNumber = isValidNumber(userEnterSupplyValue);
//            if (TextUtils.isEmpty(userEnterSupplyValue) || !isNumber) {
//                Toast.makeText(act, "Please enter Supply(CFM) value", Toast.LENGTH_SHORT).show();
//                mRelativeProgressBarLayoutRackScreen.setVisibility(View.GONE);
//                return true;
//            } else {
//                mRelativeProgressBarLayoutRackScreen.setVisibility(View.VISIBLE);
//                supplyValue = userEnterSupplyValue;
//            }
//            Log.e("Values", "SupplyValue : " + supplyValue);
//        } else if (isSpinnerSupplyShow) {
//            supplyValue = supplySelectedSpinnerValue;
//            Log.e("Values", "SupplyValue : " + supplyValue);
//        } else{
//            supplyValue = mTextViewSupplyValue.getText().toString().trim();
//            Log.e("Values", "SupplyValue : " + supplyValue);
//        }
//
//        if (isEditTextExhaust) {
//            userEnterExhaustValue = mTextViewExhaustValue.getText().toString().trim();
//            boolean isNumber = isValidNumber(userEnterExhaustValue);
//            if (TextUtils.isEmpty(userEnterExhaustValue) || !isNumber) {
//                Toast.makeText(act, "Please enter Exhaust(WC) value", Toast.LENGTH_SHORT).show();
//                mRelativeProgressBarLayoutRackScreen.setVisibility(View.GONE);
//                return true;
//            } else {
//                exhaustValue = userEnterExhaustValue;
//            }
//            Log.e("Values", "ExhaustValue : " + exhaustValue);
//        } else if (isSpinnerExhaustShow) {
//            exhaustValue = exhaustSelectedSpinnerValue;
//            Log.e("Values", "ExhaustValue : " + exhaustValue);
//        } else {
//            exhaustValue = mTextViewExhaustValue.getText().toString().trim();
//            Log.e("Values", "ExhaustValue : " + exhaustValue);
//        }



        /*if (mLinearLayoutLock.getVisibility() == View.VISIBLE) {
            int selectedId = mRadioGroupLock.getCheckedRadioButtonId();
            radioLockButton = (RadioButton) findViewById(selectedId);
            mLockValue = String.valueOf(radioLockButton.getTag());
            Log.e("Values", "Lock Value :- " + mLockValue);
        }
        Log.e("Values", "PolarityValue : " + polarityValue);*/
        return false;
    }

    private boolean isValidNumber(String editText) {
        return editText.matches("-?\\d+(.\\d+)?");
    }

    private boolean validationFunction() {
        mStrCompanyName = mTextViewCompanyName.getText().toString().trim();
//        modelNo = mTextViewModelNumber.getText().toString().trim().toUpperCase();
        modelNo = mSpinnerModelNumber.getSelectedItem().toString();
        mStrBlowerName = txt_Blower_Name.getText().toString().trim();
        mStrBuildingName = txt_building_name.getText().toString().trim();
        mStrRoomName = txt_Room_Name.getText().toString().trim();

        if (TextUtils.isEmpty(mStrCompanyName) || mStrCompanyName.equalsIgnoreCase("Enter Company Name")) {
            Toast.makeText(act, "Please enter company name", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(mStrBlowerName) || mStrBlowerName.equalsIgnoreCase("Enter Blower Name")) {
            Toast.makeText(act, "Please enter blower name", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(mStrBuildingName) || mStrBuildingName.equalsIgnoreCase("Enter Building")) {
            Toast.makeText(act, "Please enter building name", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(mStrRoomName) || mStrRoomName.equalsIgnoreCase("Enter Room")) {
            Toast.makeText(act, "Please enter room number", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(modelNo) || modelNo.equalsIgnoreCase("Enter Model Number")) {
            Toast.makeText(act, "Please enter model number", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

//    public void hideKeyBoardMethod(){
//        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(mRelativeLayoutMain.getWindowToken(), 0);
//    }

    @SuppressLint("HandlerLeak")
    public void connectionToBlower() {
        // ServiceCommunication
        /*if (!isMyServiceRunning(MyService.class)) {*/
        if (!isFromSettingScreen) {
            if (service_myservice == null) {
                // check record on setPoint data is exits or not in database
                Log.e(TAG, "ServiceCall");
                if (!prefManager.getOpenNode())
                    portConversion.openNode(act);
                else
                    Utility.AlertShowMessage(act, "Alert", "serial port not found.1", "OK");
            }

            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
        }

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

    private void mVisibilityShowHideFn() {
//        resetFunction();
        ac = mTextAC.getText().toString().trim();
        dc = mTextDC.getText().toString().trim();
        modelNo = mSpinnerModelNumber.getSelectedItem().toString();

        if (TextUtils.isEmpty(ac)) {
            Toast.makeText(act, "Please enter AC value", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(dc)) {
            Toast.makeText(act, "Please enter DC value", Toast.LENGTH_SHORT).show();
        } else {
            arrayListACH.clear();
            arrayListExhaust.clear();
            arrayListPolarity.clear();
            arrayListSupply.clear();

            mCount = dpHelper.newrackTableCheckModelNo(DatabaseTable.COL_BLSPPBCURACKSETUP_MODEL_NO, DatabaseTable.COL_BLSPPBCURACKSETUP_MODEL_NO, modelNo, "", "", "", "", "", null, "", null);
            if (mCount > 0) {
//                    mLinerLayoutACH.setVisibility(View.VISIBLE);
//                    txt_ach.setVisibility(View.VISIBLE);
//                    mSpinnerACH.setVisibility(View.VISIBLE);
//                    img_ach.setVisibility(View.INVISIBLE);
                mLinerLayoutError.setVisibility(View.GONE);
                mLinearLayoutButtons.setVisibility(View.VISIBLE);
                linear_main_layout.setVisibility(View.VISIBLE);
                mLinearLayoutLock.setVisibility(View.GONE);
//                    arrayListACH.addAll(dpHelper.getAllACHValueFromDatabase(subModelNo));
//
//                    mSpinnerACHAdapter = new SpinnerACHAdapter(act, arrayListACH);
//                    mSpinnerACHAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    mSpinnerACH.setAdapter(mSpinnerACHAdapter);
//                    mSpinnerACH.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                            achResetFunction();
//                            posACH = position;
//                            spinnerSelectedACHValue = String.valueOf(arrayListACH.get(position).getName());
//                            Log.e("spinnerSelectedACHValue", ACHValue);
//                            ACHValue = spinnerSelectedACHValue;
//                            Log.e("ACHValue", ACHValue);
//                            PolaritySetDataFunction(subModelNo, ACHValue);
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> parent) {
//
//                        }
//                    });
            } else {
                txt_ach.setVisibility(View.GONE);
                mSpinnerACH.setVisibility(View.GONE);
                img_ach.setVisibility(View.INVISIBLE);
                mLinerLayoutError.setVisibility(View.VISIBLE);
                mLinearLayoutButtons.setVisibility(View.GONE);

            }

        }
    }

    private void mVisibilityShowHideFunction() {
//        resetFunction();
        String companyName = mTextViewCompanyName.getText().toString().trim().toUpperCase();
        String blowerName = txt_Blower_Name.getText().toString().trim().toUpperCase();
        String buildingName = txt_building_name.getText().toString().trim().toUpperCase();
        String roomNo = txt_Room_Name.getText().toString().trim().toUpperCase();
        String modelNo = mTextViewModelNumber.getText().toString().trim().toUpperCase();
        if (TextUtils.isEmpty(companyName)) {
            Toast.makeText(act, "Please enter company name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(blowerName)) {
            Toast.makeText(act, "Please enter blower name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(buildingName)) {
            Toast.makeText(act, "Please enter building number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(roomNo)) {
            Toast.makeText(act, "Please enter room number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(modelNo)) {
            Toast.makeText(act, "Please enter model number", Toast.LENGTH_SHORT).show();
        } else {
            if (modelNo.length() < 8) {
                mLinerLayoutError.setVisibility(View.VISIBLE);
                txt_ach.setVisibility(View.GONE);
                mSpinnerACH.setVisibility(View.GONE);
                img_ach.setVisibility(View.INVISIBLE);
                mLinearLayoutButtons.setVisibility(View.GONE);
                linear_main_layout.setVisibility(View.GONE);
                linear_layout_buttons_back.setVisibility(View.GONE);
            } else {
                arrayListACH.clear();
                arrayListExhaust.clear();
                arrayListPolarity.clear();
                arrayListSupply.clear();
//                mEditTextSupply.setText("");
//                mEditTextExhaust.setText("");
                subModelNo = modelNo.substring(2, 8);
                Log.e("SubString", subModelNo);
                mCount = dpHelper.rackTableCheckModelNo(DatabaseTable.COL_BLRACKSETUP_MODEL_NO, DatabaseTable.COL_BLRACKSETUP_MODEL_NO, subModelNo, "", "", "", "", "", null, "", null);
                if (mCount > 0) {
//                    mLinerLayoutACH.setVisibility(View.VISIBLE);
                    txt_ach.setVisibility(View.VISIBLE);
                    mSpinnerACH.setVisibility(View.VISIBLE);
                    img_ach.setVisibility(View.INVISIBLE);
                    mLinerLayoutError.setVisibility(View.GONE);
                    mLinearLayoutButtons.setVisibility(View.VISIBLE);
                    linear_main_layout.setVisibility(View.VISIBLE);
                    mLinearLayoutLock.setVisibility(View.GONE);
                    arrayListACH.addAll(dpHelper.getAllACHValueFromDatabase(subModelNo));

                    mSpinnerACHAdapter = new SpinnerACHAdapter(act, arrayListACH);
                    mSpinnerACHAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinnerACH.setAdapter(mSpinnerACHAdapter);
                    mSpinnerACH.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            achResetFunction();
                            posACH = position;
                            spinnerSelectedACHValue = String.valueOf(arrayListACH.get(position).getName());
                            Log.e("spinnerSelectedACHValue", ACHValue);
                            ACHValue = spinnerSelectedACHValue;
                            Log.e("ACHValue", ACHValue);
                            PolaritySetDataFunction(subModelNo, ACHValue);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                } else {
                    txt_ach.setVisibility(View.GONE);
                    mSpinnerACH.setVisibility(View.GONE);
                    img_ach.setVisibility(View.INVISIBLE);
                    mLinerLayoutError.setVisibility(View.VISIBLE);
                    mLinearLayoutButtons.setVisibility(View.GONE);

                }
            }
        }
    }

    private void PolaritySetDataFunction(final String subModelNo, final String spinnerSelectedACHValue) {
        arrayListPolarity.clear();
        mCount = dpHelper.rackTableCheckModelNo(DatabaseTable.COL_BLRACKSETUP_ACH, DatabaseTable.COL_BLRACKSETUP_MODEL_NO, subModelNo, DatabaseTable.COL_BLRACKSETUP_ACH, spinnerSelectedACHValue, "", "", "", null, "", null);
        if (mCount > 0) {
//            mLinearLayoutPolarity.setVisibility(View.VISIBLE);
            txt_polarity.setVisibility(View.VISIBLE);
            mSpinnerPolarity.setVisibility(View.VISIBLE);
            img_polarity.setVisibility(View.INVISIBLE);
//            mRelativeLayoutExhaust.setVisibility(View.VISIBLE);
//            mRelativeLayoutSupply.setVisibility(View.VISIBLE);
            arrayListPolarity.addAll(dpHelper.getAllPolarityValueFromDatabase(subModelNo, spinnerSelectedACHValue));
            Log.e("PolarityValue", arrayListPolarity.get(0).getName());

            mSpinnerPolarityAdapter = new SpinnerPolarityAdapter(act, arrayListPolarity);
            mSpinnerPolarityAdapter.setDropDownViewResource(R.layout.row_language_spinner);
            mSpinnerPolarity.setAdapter(mSpinnerPolarityAdapter);

            mSpinnerPolarity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    polarityResetFunction();
                    posPolarity = position;
                    final String spinnerSelectedPolarityValue = String.valueOf(arrayListPolarity.get(position).getName());
                    final String spinnerSelectedPolarityId = arrayListPolarity.get(position).getId();
                    polarityValueId = spinnerSelectedPolarityId;
                    polarityValue = spinnerSelectedPolarityValue;
                    Log.e("PolarityValue", spinnerSelectedPolarityValue);
//                    mRadioGroupLock.clearCheck();
                    SupplyCFMSetDataFunction(subModelNo, spinnerSelectedACHValue, spinnerSelectedPolarityValue, null);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            if (rackModel != null) {
                mSpinnerPolarity.setSelection(getIndex(mSpinnerPolarity, String.valueOf(rackModel.getPolarity())));
            }

        } else {
//            mLinearLayoutPolarity.setVisibility(View.GONE);
            txt_polarity.setVisibility(View.GONE);
            mSpinnerPolarity.setVisibility(View.GONE);
            img_polarity.setVisibility(View.GONE);
        }
    }

    private void SupplyCFMSetDataFunction(final String subModelNo, final String spinnerSelectedACHValue, final String spinnerSelectedPolarityValue, final String lock) {
        arrayListSupply.clear();
        mCount = dpHelper.rackTableCheckModelNo(DatabaseTable.COL_BLRACKSETUP_POLARITY, DatabaseTable.COL_BLRACKSETUP_MODEL_NO, subModelNo, DatabaseTable.COL_BLRACKSETUP_ACH, spinnerSelectedACHValue, DatabaseTable.COL_BLRACKSETUP_POLARITY, spinnerSelectedPolarityValue, "", null, DatabaseTable.COL_BLRACKSETUP_WITH_WITHOUT_LOCK, lock);
        Log.e("Count", "" + mCount);
        if (mCount > 0) {
//            mRelativeLayoutSupply.setVisibility(View.VISIBLE);
//            mRelativeLayoutExhaust.setVisibility(View.VISIBLE);
//            mLinerLayoutSupply.setVisibility(View.VISIBLE);
            arrayListSupply.addAll(dpHelper.getAllSupplyValueFromDatabase(subModelNo, spinnerSelectedACHValue, spinnerSelectedPolarityValue, lock));
            Log.e("SupplyValue", arrayListSupply.get(0).getName());

            if (arrayListSupply.size() <= 1) {
                String spinnerSelectedSupplyValue = arrayListSupply.size() == 1 ? arrayListSupply.get(0).getName() : "";
                if (spinnerSelectedSupplyValue.isEmpty() || spinnerSelectedSupplyValue.equalsIgnoreCase("") || spinnerSelectedSupplyValue == null || spinnerSelectedSupplyValue.equalsIgnoreCase("0")) {
                    userEnterSupplyValue = mTextViewSupplyValue.getText().toString().trim();
                    ExhaustWCSetDataFunction(subModelNo, spinnerSelectedACHValue, spinnerSelectedPolarityValue, userEnterSupplyValue, lock);
                } else {
                    ExhaustWCSetDataFunction(subModelNo, spinnerSelectedACHValue, spinnerSelectedPolarityValue, spinnerSelectedSupplyValue, lock);
                }
            } else {
                isSpinnerSupplyShow = true;
//                mRelativeLayoutExhaust.setVisibility(View.VISIBLE);
//                mRelativeLayoutSupply.setVisibility(View.VISIBLE);
//                mLinerLayoutSupply.setVisibility(View.VISIBLE);
//                mLinerLayoutSupplyEditText.setVisibility(View.GONE);
//                mEditTextSupply.setVisibility(View.GONE);
                txt_supply.setVisibility(View.VISIBLE);
                img_supply.setVisibility(View.INVISIBLE);
                mSpinnerSupply.setVisibility(View.VISIBLE);
                mTextViewSupplyValue.setVisibility(View.GONE);
                mSpinnerSupplyAdapter = new SpinnerSupplyAdapter(act, arrayListSupply);
                mSpinnerSupplyAdapter.setDropDownViewResource(R.layout.row_language_spinner);
                mSpinnerSupply.setAdapter(mSpinnerSupplyAdapter);

                mSpinnerSupply.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String spinnerSelectedSupplyValue = arrayListSupply.get(position).getName();
                        supplySelectedSpinnerValue = spinnerSelectedSupplyValue;
                        ExhaustWCSetDataFunction(subModelNo, spinnerSelectedACHValue, spinnerSelectedPolarityValue, spinnerSelectedSupplyValue, lock);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        } else {
//            mLinerLayoutSupply.setVisibility(View.GONE);
            txt_supply.setVisibility(View.GONE);
            img_supply.setVisibility(View.GONE);
            mTextViewSupplyValue.setVisibility(View.INVISIBLE);
        }

    }

    private void ExhaustWCSetDataFunction(final String subModelNo, String spinnerSelectedACHValue, String spinnerSelectedPolarityValue, String userEnteredSupplyValue, String lock) {
        arrayListExhaust.clear();
        mCount = dpHelper.rackTableCheckModelNo(DatabaseTable.COL_BLRACKSETUP_SUPPLYCFM, DatabaseTable.COL_BLRACKSETUP_MODEL_NO, subModelNo, DatabaseTable.COL_BLRACKSETUP_ACH, spinnerSelectedACHValue, DatabaseTable.COL_BLRACKSETUP_POLARITY, spinnerSelectedPolarityValue, DatabaseTable.COL_BLRACKSETUP_SUPPLYCFM, userEnteredSupplyValue, DatabaseTable.COL_BLRACKSETUP_WITH_WITHOUT_LOCK, lock);
        Log.e("Count", "" + mCount);
        if (mCount > 1) {
            mLinearLayoutLock.setVisibility(View.VISIBLE);
            if (isFromSettingScreen) {
                mLockValue = String.valueOf(rackModel.getWithLock());
                String withLock = String.valueOf(radioButtonWith.getTag());
                Log.e("Values", "Lock Value From DataBase:- " + withLock);
                SupplyCFMSetDataFunction(subModelNo, ACHValue, polarityValue, mLockValue);
                if (mLockValue.equals(withLock)) {
                    radioButtonWith.setChecked(true);
                } else {
                    radioButtonWithOut.setChecked(true);
                }
            }
            mRadioGroupLock.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // This will get the radiobutton that has changed in its check state
                    RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                    mLockValue = String.valueOf(checkedRadioButton.getTag());
                    SupplyCFMSetDataFunction(subModelNo, ACHValue, polarityValue, mLockValue);
                }
            });
            Log.e("Values", "Lock Value :- " + mLockValue);
        } else if (mCount <= 1) {
//            mRelativeLayoutSupply.setVisibility(View.VISIBLE);
//            mRelativeLayoutExhaust.setVisibility(View.VISIBLE);
//            mLinerLayoutExhaust.setVisibility(View.VISIBLE);

            if (arrayListSupply.size() <= 1) {
                if (userEnteredSupplyValue.isEmpty() || userEnteredSupplyValue.equalsIgnoreCase("") || userEnteredSupplyValue == null || userEnteredSupplyValue.equalsIgnoreCase("0")) {
//                    mLinerLayoutSupply.setVisibility(View.VISIBLE);
//                    mLinerLayoutSupplyEditText.setVisibility(View.VISIBLE);
                    img_supply.setVisibility(View.VISIBLE);
                    txt_supply.setVisibility(View.VISIBLE);
//                    mRelativeLayoutSupply.setVisibility(View.VISIBLE);
                    mTextViewSupplyValue.setVisibility(View.VISIBLE);
//                    mRelativeLayoutExhaust.setVisibility(View.VISIBLE);
//                    mLinerLayoutExhaust.setVisibility(View.VISIBLE);.
                    mTextViewSupplyValue.setText("Enter Supply(CFM) value");
//                    userEnterSupplyValue = mEditTextSupply.getText().toString().trim();
                    isEditTextSupply = true;
                } else {
                    isEditTextSupply = false;

//                    mRelativeLayoutSupply.setVisibility(View.VISIBLE);
//                    mLinerLayoutSupply.setVisibility(View.VISIBLE);
                    mTextViewSupplyValue.setVisibility(View.VISIBLE);
//                    mLinerLayoutSupplyEditText.setVisibility(View.GONE);
//                    mEditTextSupply.setVisibility(View.GONE);
                    img_supply.setVisibility(View.INVISIBLE);
                    txt_supply.setVisibility(View.VISIBLE);
//                    mRelativeLayoutExhaust.setVisibility(View.VISIBLE);
//                    mTextViewExhaustValue.setVisibility(View.VISIBLE);
//                    mSpinnerSupply.setVisibility(View.GONE);

                    mTextViewSupplyValue.setText("" + arrayListSupply.get(0).getName());
                }
            }

            arrayListExhaust.addAll(dpHelper.getAllExhaustValueFromDatabase(subModelNo, spinnerSelectedACHValue, spinnerSelectedPolarityValue, userEnteredSupplyValue, lock));
            Log.e("ExhaustValue", arrayListExhaust.get(0).getName());
            if (arrayListExhaust.size() <= 1) {
                String strExhaustValue = arrayListExhaust.size() == 1 ? arrayListExhaust.get(0).getName() : "";
                if (strExhaustValue.isEmpty() || strExhaustValue.equalsIgnoreCase("") || strExhaustValue == null) {
//                    mLinerLayoutExhaust.setVisibility(View.VISIBLE);
                    mTextViewExhaustValue.setVisibility(View.VISIBLE);
                    mTextViewExhaustValue.setText("Enter Exhaust(WC) value");
                    txt_exhaust.setVisibility(View.VISIBLE);
                    img_exhaust.setVisibility(View.VISIBLE);
//                    mRelativeLayoutExhaust.setVisibility(View.VISIBLE);
//                    mLinerLayoutExhaustEditText.setVisibility(View.VISIBLE);
//                    mEditTextExhaust.setVisibility(View.VISIBLE);
                    isEditTextExhaust = true;
                } else {
                    isEditTextExhaust = false;
//                    mRelativeLayoutExhaust.setVisibility(View.VISIBLE);
//                    mLinerLayoutExhaust.setVisibility(View.VISIBLE);
//                    mLinerLayoutExhaustEditText.setVisibility(View.GONE);
//                    mEditTextExhaust.setVisibility(View.GONE);
                    mTextViewExhaustValue.setVisibility(View.VISIBLE);
//                    mSpinnerExhaust.setVisibility(View.GONE);
                    txt_exhaust.setVisibility(View.VISIBLE);
                    img_exhaust.setVisibility(View.INVISIBLE);
                    mTextViewExhaustValue.setText("" + arrayListExhaust.get(0).getName());
                }
            } else {
                isSpinnerExhaustShow = true;
//                mRelativeLayoutExhaust.setVisibility(View.VISIBLE);
//                mLinerLayoutExhaust.setVisibility(View.VISIBLE);
//                mLinerLayoutExhaustEditText.setVisibility(View.GONE);
                mTextViewExhaustValue.setVisibility(View.GONE);
//                mEditTextExhaust.setVisibility(View.GONE);
                txt_exhaust.setVisibility(View.VISIBLE);
                img_exhaust.setVisibility(View.INVISIBLE);
                mSpinnerExhaust.setVisibility(View.VISIBLE);
                mTextViewExhaustValue.setVisibility(View.GONE);
                mSpinnerExhaustAdapter = new SpinnerExhaustAdapter(act, arrayListExhaust);
                mSpinnerExhaustAdapter.setDropDownViewResource(R.layout.row_language_spinner);
                mSpinnerExhaust.setAdapter(mSpinnerExhaustAdapter);

                mSpinnerExhaust.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        final String spinnerSelectedExhaustValue = String.valueOf(arrayListExhaust.get(position).getName());
                        if (spinnerSelectedExhaustValue.equalsIgnoreCase("Edit Value")) {
                            exhaustSelectedSpinnerValue = "";
//                            mLinerLayoutExhaust.setVisibility(View.GONE);
//                            mLinerLayoutExhaustEditText.setVisibility(View.VISIBLE);
//                            mTextViewExhaustValue.setVisibility(View.VISIBLE);
                            mEditTextExhaust.setVisibility(View.VISIBLE);
                            isEditTextExhaust = true;
                        } else {
                            exhaustSelectedSpinnerValue = spinnerSelectedExhaustValue;
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        } else {
//            mLinerLayoutExhaust.setVisibility(View.GONE);
            mLinearLayoutLock.setVisibility(View.GONE);
            mTextViewExhaustValue.setVisibility(View.GONE);
            txt_exhaust.setVisibility(View.GONE);
            img_exhaust.setVisibility(View.GONE);
        }
    }

    public void resetFunction() {
       /* mTextDC.setText("");
        mTextAC.setText("");*/
        /*arrayListACH.clear();
        arrayListPolarity.clear();
        arrayListSupply.clear();
        arrayListExhaust.clear();

        mTextViewSupplyValue.setText("");
        mTextViewExhaustValue.setText("");
        txt_exhaust.setVisibility(View.GONE);
        txt_supply.setVisibility(View.GONE);
        txt_polarity.setVisibility(View.GONE);
        txt_ach.setVisibility(View.GONE);
        mSpinnerACH.setVisibility(View.GONE);
        mSpinnerExhaust.setVisibility(View.GONE);
        img_exhaust.setVisibility(View.INVISIBLE);
        img_supply.setVisibility(View.INVISIBLE);
        img_polarity.setVisibility(View.INVISIBLE);
        img_ach.setVisibility(View.INVISIBLE);
        linear_main_layout.setVisibility(View.GONE);

//        mEditTextSupply.setText("");
//        mEditTextExhaust.setText("");
//        mEditTextModelNumber.setText("");


//        mLinerLayoutSupplyEditText.setVisibility(View.GONE);
        mTextViewSupplyValue.setVisibility(View.GONE);
//        mEditTextSupply.setVisibility(View.GONE);
//        mLinerLayoutExhaustEditText.setVisibility(View.GONE);
        mTextViewExhaustValue.setVisibility(View.GONE);
//        mEditTextExhaust.setVisibility(View.GONE);

        mLinearLayoutButtons.setVisibility(View.GONE);
        mLinearLayoutLock.setVisibility(View.GONE);

        mRadioGroupLock.setOnCheckedChangeListener(null);
        mRadioGroupLock.clearCheck();
        mLockValue = "";
        ACHValue = "";
        polarityValue = "";
        supplyValue = "";
        exhaustValue = "";
        exhaustSelectedSpinnerValue = "";
        supplySelectedSpinnerValue = "";*/
    }

    private void polarityResetFunction() {

//        mLinerLayoutExhaustEditText.setVisibility(View.GONE);
        mTextViewExhaustValue.setVisibility(View.GONE);
        txt_supply.setVisibility(View.GONE);
        txt_exhaust.setVisibility(View.GONE);
        img_supply.setVisibility(View.INVISIBLE);
        img_exhaust.setVisibility(View.INVISIBLE);
//        mEditTextExhaust.setVisibility(View.GONE);
//        mLinerLayoutSupplyEditText.setVisibility(View.GONE);
        mTextViewSupplyValue.setVisibility(View.GONE);
//        mEditTextSupply.setVisibility(View.GONE);
        mLinearLayoutLock.setVisibility(View.GONE);
        arrayListSupply.clear();
        arrayListExhaust.clear();
        mRadioGroupLock.setOnCheckedChangeListener(null);
        mRadioGroupLock.clearCheck();
    }

    private void achResetFunction() {
//        mLinearLayoutPolarity.setVisibility(View.GONE);
//        mRelativeLayoutExhaust.setVisibility(View.GONE);
//        mLinerLayoutExhaust.setVisibility(View.GONE);
//        mLinerLayoutExhaustEditText.setVisibility(View.GONE);
        mTextViewExhaustValue.setVisibility(View.GONE);
        mTextViewSupplyValue.setVisibility(View.GONE);
//        mEditTextExhaust.setVisibility(View.GONE);
//        mRelativeLayoutSupply.setVisibility(View.GONE);
//        mLinerLayoutSupply.setVisibility(View.GONE);
//        mLinerLayoutSupplyEditText.setVisibility(View.GONE);
//        mEditTextSupply.setVisibility(View.GONE);
        txt_supply.setVisibility(View.GONE);
        txt_exhaust.setVisibility(View.GONE);
        img_supply.setVisibility(View.INVISIBLE);
        img_exhaust.setVisibility(View.INVISIBLE);
        mLinearLayoutLock.setVisibility(View.GONE);
        arrayListSupply.clear();
        arrayListExhaust.clear();
        mRadioGroupLock.setOnCheckedChangeListener(null);
        mRadioGroupLock.clearCheck();
    }

    // CommunicationSettingAlertDailogBox AlertDailogBox
    public void AlertDialogBox(final String typr, final TextView textView) {
        Utility.hideNavigationBar(act);
        alertview_selection = new Dialog(act);
        alertview_selection.setCancelable(false);
//        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
//        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);
//        alertview_selection.getWindow().setLayout(width, height);

        alertview_selection.setContentView(R.layout.alertview_edittext_layout_rack_setup); // EditText Screen
//        alertview_selection.show();
        alertview_selection.getWindow().setGravity(Gravity.TOP);
        Log.e("TAG","Setting rack setup dialog");
        txt_Title_alartview_box = alertview_selection.findViewById(R.id.txt_Title_alartview_box);
        edit_EnterTxt_alartview_box = alertview_selection.findViewById(R.id.edit_EnterTxt_alartview_box);
        btn_Ok_alartview_box = alertview_selection.findViewById(R.id.btn_Save_alartview_box_report);
        btn_Cancel_alartview_box = alertview_selection.findViewById(R.id.btn_Cancel_alartview_box_report);

        if(typr.equals("AC") || typr.equals("DC")) {
            edit_EnterTxt_alartview_box.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }

        txt_Title_alartview_box.setText(typr);

        btn_Cancel_alartview_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.hideNavigationBar(act);
                if (alertview_selection != null && alertview_selection.isShowing()) {
                    alertview_selection.dismiss();
                }
//                alertview_selection.dismiss();
            }
        });

        edit_EnterTxt_alartview_box.requestFocus();

        btn_Ok_alartview_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.hideNavigationBar(act);
                if (typr.equals("Company Name")) {
                    if (TextUtils.isEmpty(edit_EnterTxt_alartview_box.getText().toString())) {
                        Toast.makeText(act, "Please enter company name", Toast.LENGTH_SHORT).show();
                    } else {
                        if (edit_EnterTxt_alartview_box.getText().toString().length() == 0) {
                            Utility.ShowMessage(act, "Warning!", "Please Enter Vaild Text", "OK");
                        } else {
                            if (alertview_selection != null && alertview_selection.isShowing()) {
                                alertview_selection.dismiss();
                            }
                            textView.setText(edit_EnterTxt_alartview_box.getText().toString());
//                            alertview_selection.dismiss();
                        }
                    }
                } else if (typr.equals("Blower Name")) {
                    if (TextUtils.isEmpty(edit_EnterTxt_alartview_box.getText().toString())) {
                        Toast.makeText(act, "Please enter blower name", Toast.LENGTH_SHORT).show();
                    } else {
                        if (edit_EnterTxt_alartview_box.getText().toString().length() == 0) {
                            Utility.ShowMessage(act, "Warning!", "Please Enter Vaild Text", "OK");
                        } else {
                            if (alertview_selection != null && alertview_selection.isShowing()) {
                                alertview_selection.dismiss();
                            }
                            textView.setText(edit_EnterTxt_alartview_box.getText().toString());
//                            alertview_selection.dismiss();
                        }
                    }
                } else if (typr.equals("Building Name")) {
                    if (TextUtils.isEmpty(edit_EnterTxt_alartview_box.getText().toString())) {
                        Toast.makeText(act, "Please enter building name", Toast.LENGTH_SHORT).show();
                    } else {
                        if (edit_EnterTxt_alartview_box.getText().toString().length() == 0) {
                            Utility.ShowMessage(act, "Warning!", "Please Enter Vaild Text", "OK");
                        } else {
                            if (alertview_selection != null && alertview_selection.isShowing()) {
                                alertview_selection.dismiss();
                            }
                            textView.setText(edit_EnterTxt_alartview_box.getText().toString());
//                            alertview_selection.dismiss();
                        }
                    }
                } else if (typr.equals("Room Name")) {
                    if (TextUtils.isEmpty(edit_EnterTxt_alartview_box.getText().toString())) {
                        Toast.makeText(act, "Please enter room name", Toast.LENGTH_SHORT).show();
                    } else {
                        if (edit_EnterTxt_alartview_box.getText().toString().length() == 0) {
                            Utility.ShowMessage(act, "Warning!", "Please Enter Vaild Text", "OK");
                        } else {
                            if (alertview_selection != null && alertview_selection.isShowing()) {
                                alertview_selection.dismiss();
                            }
                            textView.setText(edit_EnterTxt_alartview_box.getText().toString());
//                            alertview_selection.dismiss();
                        }
                    }
                } else if (typr.equals("Model Number")) {
                    if (edit_EnterTxt_alartview_box.getText().toString().contains(" ") || TextUtils.isEmpty(edit_EnterTxt_alartview_box.getText().toString())) {
                        Toast.makeText(act, "Please enter model number", Toast.LENGTH_SHORT).show();
                    } else {
                        if (edit_EnterTxt_alartview_box.getText().toString().length() == 0) {
                            Utility.ShowMessage(act, "Warning!", "Please Enter Vaild Text", "OK");
                        } else {
                            if (alertview_selection != null && alertview_selection.isShowing()) {
                                alertview_selection.dismiss();
                            }
                            textView.setText(edit_EnterTxt_alartview_box.getText().toString());
//                            alertview_selection.dismiss();
                            mButtonFind.setVisibility(View.GONE);
                            resetFunction();
                            if (!validationFunction()) {
                                mVisibilityShowHideFunction();
                            }
                        }
                    }
                } else if (typr.equals("Supply(CFM)")) {
                    if (edit_EnterTxt_alartview_box.getText().toString().contains(" ") || TextUtils.isEmpty(edit_EnterTxt_alartview_box.getText().toString())) {
                        Toast.makeText(act, "Please enter Supply(CFM) value", Toast.LENGTH_SHORT).show();
                    } else {
                        if (edit_EnterTxt_alartview_box.getText().toString().length() == 0) {
                            Utility.ShowMessage(act, "Warning!", "Please Enter Vaild Text", "OK");
                        } else {
                            if (alertview_selection != null && alertview_selection.isShowing()) {
                                alertview_selection.dismiss();
                            }
                            isEditTextSupply = true;
                            textView.setText(edit_EnterTxt_alartview_box.getText().toString());
//                            alertview_selection.dismiss();
                        }
                    }
                } else if (typr.equals("Exhaust(WC)")) {
                    if (edit_EnterTxt_alartview_box.getText().toString().contains(" ") || TextUtils.isEmpty(edit_EnterTxt_alartview_box.getText().toString())) {
                        Toast.makeText(act, "Please enter Exhaust(WC) value", Toast.LENGTH_SHORT).show();
                    } else {
                        if (edit_EnterTxt_alartview_box.getText().toString().length() == 0) {
                            Utility.ShowMessage(act, "Warning!", "Please Enter Vaild Text", "OK");
                        } else {
                            if (alertview_selection != null && alertview_selection.isShowing()) {
                                alertview_selection.dismiss();
                            }
                            isEditTextExhaust = true;
                            textView.setText(edit_EnterTxt_alartview_box.getText().toString());
//                            alertview_selection.dismiss();
                        }
                    }
                } else if (typr.equals("AC")) {

                    if (TextUtils.isEmpty(edit_EnterTxt_alartview_box.getText().toString())) {
                        Toast.makeText(act, "Please enter AC value", Toast.LENGTH_SHORT).show();
                    } else {
                        if (edit_EnterTxt_alartview_box.getText().toString().length() == 0) {
                            Utility.ShowMessage(act, "Warning!", "Please Enter Vaild Text", "OK");
                        } else {
                            if (alertview_selection != null && alertview_selection.isShowing()) {
                                alertview_selection.dismiss();
                            }
                            textView.setText(edit_EnterTxt_alartview_box.getText().toString());
//                            alertview_selection.dismiss();
                        }
                    }
                } else if (typr.equals("DC")) {

                    if (TextUtils.isEmpty(edit_EnterTxt_alartview_box.getText().toString())) {
                        Toast.makeText(act, "Please enter DC value", Toast.LENGTH_SHORT).show();
                    } else {
                        if (edit_EnterTxt_alartview_box.getText().toString().length() == 0) {
                            Utility.ShowMessage(act, "Warning!", "Please Enter Vaild Text", "OK");
                        } else {
                            if (alertview_selection != null && alertview_selection.isShowing()) {
                                alertview_selection.dismiss();
                            }
                            textView.setText(edit_EnterTxt_alartview_box.getText().toString());
//                            alertview_selection.dismiss();
                        }
                    }
                } else {

                }
            }
        });

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // show dialog here
                if (!isFinishing()) {
                    alertview_selection.show();
                }
            }
        });

    }

    // RackSetUp Save Click From Setting Screen AlertDailogBox
    public void ShowRackSetUpDetailSaveDialog(final String title, String btn1, String btn2) {
        Utility.hideNavigationBar(act);
        alertview_Save_Dialog = new Dialog(act);

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);

        alertview_Save_Dialog.getWindow().setLayout(width, height);

        alertview_Save_Dialog.setCancelable(true);
        alertview_Save_Dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        alertview_Save_Dialog.setContentView(R.layout.alertview_racksetup_save_button_layout);

//        alertview_Save_Dialog.show();

        txt_dailogMassage = alertview_Save_Dialog.findViewById(R.id.txt_dailogMassage);
        btn_dailogYes = alertview_Save_Dialog.findViewById(R.id.btn_dailogYes);
        btn_dailogNo = alertview_Save_Dialog.findViewById(R.id.btn_dailogNo);

        txt_dailogMassage.setText(title);
        btn_dailogYes.setText(btn1);
        btn_dailogNo.setText(btn2);

        btn_dailogNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.hideNavigationBar(act);
                if (alertview_Save_Dialog != null && alertview_Save_Dialog.isShowing()) {
                    alertview_Save_Dialog.dismiss();
                }
//                alertview_Save_Dialog.dismiss();
            }
        });

        btn_dailogYes.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {
                Utility.hideNavigationBar(act);
                if (alertview_Save_Dialog != null && alertview_Save_Dialog.isShowing()) {
                    alertview_Save_Dialog.dismiss();
                }
                dpHelper.setIsSetUpCompletedColoumn();
                dpHelper.deleteAllRecordFromAllTable(false);
                saveButtonFunction();
//                alertview_Save_Dialog.dismiss();
            }
        });

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // show dialog here
                if (!isFinishing()) {
                    alertview_Save_Dialog.show();
                }
            }
        });

    }

    public void AlertDialogBoxSuccessfully(){
        alertview = new Dialog(act);

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);

        alertview.getWindow().setLayout(width, height);

        alertview.setCancelable(false);
        alertview.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        alertview.setContentView(R.layout.alertview_racksetup_layout); // Seekbar Selection
//        if (!isFromSettingScreen) {
//            if (prefManager != null) {
//                if (prefManager.getOpenNode()) {
//                    portConversion.closeNode();
//                }
//            }
//        }
        TextView txt_dailogTitle = alertview.findViewById(R.id.txt_dailogTitle);
        TextView txt_dailogDesc = alertview.findViewById(R.id.txt_dailogDesc);
        TextView btn_Ok_selection = alertview.findViewById(R.id.btn_Ok_Selection);

        if (!isFromSettingScreen) {
            if (prefManager != null) {
                if (prefManager.getOpenNode()) {
                    portConversion.closeNode();
                }
            }
        }

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // show dialog here
                if (!isFinishing()) {
                    alertview.show();
                }
            }
        });

        btn_Ok_selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.hideNavigationBar(act);
                if (isFromSettingScreen) {
                    if (alertview != null && alertview.isShowing()) {
                        alertview.dismiss();
                    }
                    ResetCounter(0);
                    finish();
                    allentownBlowerApplication.getObserver().deleteObserver(RackSetUpNewActivity.this);
//                        alertview.dismiss();
                } else {
                    if (alertview != null && alertview.isShowing()) {
                        alertview.dismiss();
                    }
                    String bType="";
                    if(Float.parseFloat(ac)<0)
                        bType =Utility.BCU2;
                    else
                        bType =Utility.SPP;
                    Intent intent = new Intent(act, HomeActivity.class);
                    intent.putExtra(Utility.STR_BLOWER_TYPE, bType);
                    startActivity(intent);
                    ResetCounter(0);
//                    finish();
                    allentownBlowerApplication.getObserver().deleteObserver(RackSetUpNewActivity.this);
//                        alertview.dismiss();
                }
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isFromSettingScreen) {
            if (prefManager != null) {
                if (prefManager.getOpenNode()) {
                    portConversion.closeNode();
                }
            }
        }

        if (alertview != null && alertview.isShowing()) {
            alertview.dismiss();
            alertview = null;
        }

        if (alertview_selection != null && alertview_selection.isShowing()) {
            alertview_selection.dismiss();
            alertview_selection = null;
        }

        if (alertview_Save_Dialog != null && alertview_Save_Dialog.isShowing()) {
            alertview_Save_Dialog.dismiss();
            alertview_Save_Dialog = null;
        }


    }

    @Override
    protected void onPause() {
        super.onPause();

        if (alertview != null && alertview.isShowing()) {
            alertview.dismiss();
        }

        if (alertview_selection != null && alertview_selection.isShowing()) {
            alertview_selection.dismiss();
        }

        if (alertview_Save_Dialog != null && alertview_Save_Dialog.isShowing()) {
            alertview_Save_Dialog.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (alertview != null && alertview.isShowing()) {
            alertview.dismiss();
        }

        if (alertview_selection != null && alertview_selection.isShowing()) {
            alertview_selection.dismiss();
        }

        if (alertview_Save_Dialog != null && alertview_Save_Dialog.isShowing()) {
            alertview_Save_Dialog.dismiss();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        Log.e(TAG, "response RackScreen....");
        if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nReConnectNode) {
            // ServiceCommunication
            /*if (!isMyServiceRunning(MyService.class)) {*/
            if (!isFromSettingScreen) {
                if (service_myservice == null) {
                    // check record on setPoint data is exits or not in database
                    if (!prefManager.getOpenNode())
                        portConversion.openNode(act);
                    else
                        Utility.AlertShowMessage(act, "Alert", "serial port not found.", "OK");
                }
            }
        }
//        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nRackSetUp_ACH_Value_Write_Only) {
//            Log.e(TAG, spinnerSelectedACHValue);
//            Log.e(TAG, supplyValue);
//            ACHValue = spinnerSelectedACHValue;
//            ArrayList<SetPointCommand> setpointArrayList = responseHandler.getLastSetPointData();
//            String S07_YY = responseHandler.stringToHex(ACHValue, true);
//            String S07_XX = responseHandler.stringToHex(supplyValue, true);
////            String S07_XX = responseHandler.getHexString(setpointArrayList.get(0).getS07(), true);
//            String command = "S07=" + S07_XX.concat(S07_YY).toUpperCase();
//            Utility.Log(TAG, "Sending S07_XXYY ==> " + command);
//            CallReadWriteFuncation(command, 101);
//        } else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nRackSetUp_Polarity_Value_Write_Only) {
//            Log.e(TAG, polarityValue);
//            ArrayList<SetPointCommand> setpointArrayList = responseHandler.getLastSetPointData();
//            String bin = responseHandler.hexToBinary(setpointArrayList.get(0).getS01());
//            Utility.Log(TAG, "hexToBinary => " + bin);
//            //0000111100001000 //0F08
//            //change to 0F00
//            String command = "";
//
//            String S01 = responseHandler.binaryToHex(bin.substring(0, 12).concat(polarityValueId).concat(bin.substring(13))).toUpperCase();
//
//            if (S01.length() == 1)
//                S01 = "000" + S01;
//            else if (S01.length() == 2)
//                S01 = "00" + S01;
//            else if (S01.length() == 3)
//                S01 = "0" + S01;
//
//
//            command = "S01=" + S01;
//
//            Utility.Log(TAG, "Sending S01 ==> " + command);
//
//            CallReadWriteFuncation(command, 102);
//        }
////        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nRackSetUp_Supply_CFM_Value_Write_Only) {
////            Log.e(TAG, supplyValue);
////            ArrayList<SetPointCommand> setpointArrayList = responseHandler.getLastSetPointData();
////            String S07_XX = responseHandler.stringToHex(supplyValue, true);
////            String S07_YY = responseHandler.getHexString(setpointArrayList.get(0).getS07(), false);
////            String command = "S07=" + S07_XX.concat(S07_YY).toUpperCase();
////            Utility.Log(TAG, "Sending S07_YY ==> " + command);
////            CallReadWriteFuncation(command, 103);
////        }
//        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nRackSetUp_Exhaust_WC_Value_Write_Only) {
//            Log.e(TAG, exhaustValue);
//            String barvalue = exhaustValue;
//
//            int S10XYYY = (int) (Float.parseFloat(barvalue.replace("-", "")) * 1000);
//
//            String S10_XYYY = "";
//            String hexvalue = Integer.toHexString(S10XYYY);
//
//            if (hexvalue.length() == 0)
//                hexvalue = "000";
//            else if (hexvalue.length() == 1)
//                hexvalue = "00" + hexvalue;
//            else if (hexvalue.length() == 2)
//                hexvalue = "0" + hexvalue;
//
//            Utility.Log(TAG, "Hex String value =>> " + hexvalue);
//            if (barvalue.startsWith("-")) {
//                S10_XYYY = "8" + hexvalue;
//            } else {
//                S10_XYYY = "0" + hexvalue;
//            }
//
//            String command = "S10=" + S10_XYYY.toUpperCase();
//
//            Utility.Log(TAG, "Sending S10_XYYY ==> " + command);
//            CallReadWriteFuncation(command, 103);
//        }
        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nRackSetUp_Dialog) {
            mRelativeProgressBarLayoutRackScreen.setVisibility(View.GONE);

            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialogBoxSuccessfully();
                }
            });
        }
//        else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nCloseProgressBar) {
////            Toast.makeText(act, "Something is wrong with the blower response", Toast.LENGTH_SHORT).show();
//            mRelativeProgressBarLayoutRackScreen.setVisibility(View.GONE);
//        }
        if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nSetDCCommand) {

            String dcvalue = dc;
            int multiple=0;
            Utility.DCPOINT =dc;

            Float a = Float.parseFloat(dcvalue.replace("-", ""));
            String[] div = a.toString().split("\\.");
            multiple=1000;
           /* if(div[1].length() ==2)
                multiple =100;
            else if(div[1].length() ==3)
                multiple=1000;
            else if(div[1].length() ==1)
                multiple=10;*/

            int S10XYYY = (int) (Float.parseFloat(dcvalue.replace("-", "")) * multiple);

            String S10_XYYY = "";
            String hexvalue = Integer.toHexString(S10XYYY);

            if (hexvalue.length() == 0)
                hexvalue = "000";
            else if (hexvalue.length() == 1)
                hexvalue = "00" + hexvalue;
            else if (hexvalue.length() == 2)
                hexvalue = "0" + hexvalue;

            Utility.Log(TAG, "Hex String value =>> " + hexvalue);
            if (dcvalue.startsWith("-")) {
                S10_XYYY = "8" + hexvalue;
            } else {
                S10_XYYY = "0" + hexvalue;
            }

            String command = "S10=" + S10_XYYY.toUpperCase();
            //command = "S10=81A4";
            Utility.Log(TAG, "Sending S10_XYYY ==> " + command);
            CallReadWriteFuncation(command, 208);

        }
        else if(allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nSetACCommand) {
            String acvalue = ac;
            Utility.ACPOINT = ac;
            Float d = Float.parseFloat(acvalue.replace("-", ""));
            String[] div = d.toString().split("\\.");
            int multiple=1000;
          /*  if(div[1].length() ==2)
                multiple =100;
            else if(div[1].length() ==3)
                multiple=1000;
            else if(div[1].length() ==1)
                multiple=10;*/

            int S09XYYY = (int) (Float.parseFloat(acvalue.replace("-", "")) * multiple);

            String S09_XYYY = "";
            String hexvalueS09 = Integer.toHexString(S09XYYY);

            if (hexvalueS09.length() == 0)
                hexvalueS09 = "000";
            else if (hexvalueS09.length() == 1)
                hexvalueS09 = "00" + hexvalueS09;
            else if (hexvalueS09.length() == 2)
                hexvalueS09 = "0" + hexvalueS09;

            Utility.Log(TAG, "Hex String value =>> " + hexvalueS09);
            if (acvalue.startsWith("-")) {
                S09_XYYY = "8" + hexvalueS09;
            } else {
                S09_XYYY = "0" + hexvalueS09;
            }

            String command = "S09=" + S09_XYYY.toUpperCase();
            //command = "S09=81A4";
            Utility.Log(TAG, "Sending S09_XYYY ==> " + command);
            CallReadWriteFuncation(command, 209);
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus)
        {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utility.hideNavigationBar(act);
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
                    allentownBlowerApplication.getObserver().deleteObserver(RackSetUpNewActivity.this);
                    finish();
                }
            } else {
                Utility.Log(TAG, "onFinish");
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nTimerFinishedReportAndRack);
                allentownBlowerApplication.getObserver().deleteObserver(RackSetUpNewActivity.this);
                finish();
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
}