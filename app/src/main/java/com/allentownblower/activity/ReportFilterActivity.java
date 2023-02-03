package com.allentownblower.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.allentownblower.R;
import com.allentownblower.adapter.TableViewAdapter;
import com.allentownblower.adapter.TableViewListener;
import com.allentownblower.application.AllentownBlowerApplication;
import com.allentownblower.common.ApiHandler;
import com.allentownblower.common.CodeReUse;
import com.allentownblower.common.ObserverActionID;
import com.allentownblower.common.PendingID;
import com.allentownblower.common.PrefManager;
import com.allentownblower.common.ResponseHandler;
import com.allentownblower.common.Utility;
import com.allentownblower.database.SqliteHelper;
import com.allentownblower.module.Cell;
import com.allentownblower.module.ColumnHeader;
import com.allentownblower.module.RackDetailsModel;
import com.allentownblower.module.RowHeader;
import com.allentownblower.module.TableViewModel;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.evrencoskun.tableview.TableView;
import com.koushikdutta.async.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

public class ReportFilterActivity extends AppCompatActivity implements Observer {

    private static final String TAG = "ReportFilterActivity";
    private TextView mTxtStartDate, mTxtEndDate, mTxtViewReport, mTxtResetFilter, mTxtExportData, mTxtSendEmail, mTxtExportReport, mTxtEmail;
    private TableView mTableView;
    private LinearLayout mLinearLayoutDate, mLinearLayoutTable, mLinearLayoutReset, mLinearLayoutBack, mLinearLayoutEmail;
    private Activity act;
    private RelativeLayout mRelativeLayoutTable, relative_progress_report;
    private String mStartDate, mEndDate;
    private TableViewModel tableViewModel;
    private RadioGroup mRadioGroup;
    private String mRadioButtonValue = "1";
    private RadioButton mRadioButton_Max_Min_Avg, mRadioButton_All;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private Dialog alertview_selection;
    // Dailog CommunicationSettingActivity
    private EditText edit_EnterTxt_alartview_box;
    private TextView txt_Title_alartview_box, btn_Cancel_alartview_box, btn_Ok_alartview_box;
    private Handler mHandler = new Handler();
    private AllentownBlowerApplication allentownBlowerApplication;
    private TableViewAdapter tableViewAdapter;
    private boolean isEmailButtonClicked = false, isExportButtonClicked = false, isViewReportClicked = false;
    private String email;
    private int currentApiVersion;
    private ResponseHandler responseHandler;
    private SqliteHelper sqliteHelper;
    private boolean isUSBDetected;
    private PrefManager prefManager;
    private RackDetailsModel rackDetailsModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_filter);
        act = this;
        Utility.hideNavigationBar(act);
        // Common
        allentownBlowerApplication = (AllentownBlowerApplication) act.getApplication();
        allentownBlowerApplication.getObserver().addObserver(this);
        sqliteHelper = new SqliteHelper(act);
        responseHandler = new ResponseHandler(act);
        prefManager = new PrefManager(act);
        rackDetailsModel = sqliteHelper.getDataFromRackBlowerDetails();
        initializeMethod();

        mClickEvent();
        ResetCounter(1);
        Utility.getBroadCast(act);

    }

    private void initializeMethod() {
        String currentDt = Utility.getshortDateInString(new Date());
        mTxtStartDate = findViewById(R.id.txt_value_start_date);
        mTxtStartDate.setText(currentDt);

        mTxtEndDate = findViewById(R.id.txt_value_end_date);
        mTxtEndDate.setText(currentDt);
        mTxtViewReport = findViewById(R.id.txt_view_report);
        mTxtSendEmail = findViewById(R.id.txt_send_email);
        mTxtExportReport = findViewById(R.id.txt_export_report);
        mTxtEmail = findViewById(R.id.txt_value_email);

        mTableView = findViewById(R.id.tableview);
        mLinearLayoutDate = findViewById(R.id.linear_layout_date);
        mTxtResetFilter = findViewById(R.id.txt_reset_filter);
        mTxtExportData = findViewById(R.id.txt_export_data);
        mRelativeLayoutTable = findViewById(R.id.relative_layout_table);
        relative_progress_report = findViewById(R.id.relative_progress_report);
        mRadioGroup = findViewById(R.id.radioGroup_Report);
        mRadioButton_Max_Min_Avg = findViewById(R.id.radioButton_Max_Min_Avg);
        mRadioButton_All = findViewById(R.id.radioButton_All);
        mLinearLayoutTable = findViewById(R.id.linear_table);
        mLinearLayoutReset = findViewById(R.id.linear_reset_export);
        mLinearLayoutBack = findViewById(R.id.linear_layout_buttons_back);
        mLinearLayoutEmail = findViewById(R.id.linear_layout_email);

        String hostAddress = prefManager.getHostName();
        if (!TextUtils.isEmpty(hostAddress)) {
            mTxtSendEmail.setVisibility(View.VISIBLE);
            mLinearLayoutEmail.setVisibility(View.VISIBLE);
        } else {
            mTxtSendEmail.setVisibility(View.VISIBLE);
            mLinearLayoutEmail.setVisibility(View.VISIBLE);
        }
    }

    private void mClickEvent() {

        mTxtStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetCounter(1);
                startDateDialog();
            }
        });

        mTxtEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetCounter(1);
                endDateDialog();
            }
        });

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                mRadioButtonValue = String.valueOf(checkedRadioButton.getTag());
                ResetCounter(1);
            }
        });

        mTxtViewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetCounter(1);
//                isEmailButtonClicked = false;
//                isExportButtonClicked = false;
                isViewReportClicked = true;
                boolean check = true;
                if (TextUtils.isEmpty(mTxtStartDate.getText().toString())) {
                    Toast.makeText(ReportFilterActivity.this, "Please select Start date", Toast.LENGTH_LONG).show();
                    check = false;
                } else if (TextUtils.isEmpty(mTxtEndDate.getText().toString())) {
                    Toast.makeText(ReportFilterActivity.this, "Please select End date", Toast.LENGTH_LONG).show();
                    check = false;
                } else if (Utility.getshortDateFromString(mTxtStartDate.getText().toString()).after(Utility.getshortDateFromString(mTxtEndDate.getText().toString())))
                {
                    Utility.ShowMessage(act, "Warning", "End Date must be higher than Start Date.","Ok");
                    check = false;
                }
                else if (!mRadioButtonValue.equals("1"))
                {
                    if ((int) Utility.getDateDiff(new SimpleDateFormat("yyyy-MM-dd"), mTxtStartDate.getText().toString(), mTxtEndDate.getText().toString())>1)
                    {
                        Utility.ShowMessage(act, "Warning", "Please select the date range as 1 day to view report. \nOr\nChange the selection to Max/Min/Avg Data.", "Ok");
                        check = false;
                    }
                }
                if (check) {
                    alertDialogBox();
                    mStartDate = mTxtStartDate.getText().toString();
                    mEndDate = mTxtEndDate.getText().toString();
                    tableViewModel = new TableViewModel(act, mStartDate, mEndDate,sqliteHelper);
                    // Create TableView Adapter
                    tableViewAdapter = new TableViewAdapter(tableViewModel);
                    mTableView.setAdapter(tableViewAdapter);
                    mTableView.setTableViewListener(new TableViewListener(mTableView));
                    Log.e("TAG","First");
                    final Runnable r = new Runnable() {
                        public void run() {
                            List<ColumnHeader> listColumnHeader = tableViewModel.getColumnHeaderList(mRadioButtonValue);
                            Log.e("TAG","second");
                            HashMap<String, Object> objHashMap = tableViewModel.getCellList(mRadioButtonValue, false);
                            Log.e("TAG","third");
                            List<RowHeader> rowHeaderList = (List<RowHeader>) objHashMap.get("rowHeader");
                            Log.e("TAG","forth");
                            List<List<Cell>> cellList = (List<List<Cell>>) objHashMap.get("cell");
                            Log.e("TAG","Fifth");
                            tableViewAdapter.setAllItems(listColumnHeader, rowHeaderList, cellList);
                            Log.e("TAG","sixth");
                        }
                    };
                    mHandler.postDelayed(r, 1000);

                }
            }
        });

        mTxtExportReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetCounter(1);
//                isExportButtonClicked = true;
//                isEmailButtonClicked = false;

                if (TextUtils.isEmpty(mTxtStartDate.getText().toString())) {
                    Toast.makeText(ReportFilterActivity.this, "Please select start date", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(mTxtEndDate.getText().toString())) {
                    Toast.makeText(ReportFilterActivity.this, "Please select end date", Toast.LENGTH_LONG).show();
                } else if (Utility.getshortDateFromString(mTxtStartDate.getText().toString()).after(Utility.getshortDateFromString(mTxtEndDate.getText().toString())))
                {
                    Utility.ShowMessage(act, "Warning", "End Date must be higher than Start Date.","Ok");
                }
                else {
                    Utility.ShowMessageReport(act, "Please wait while exporting data...");
                    mStartDate = mTxtStartDate.getText().toString();
                    mEndDate = mTxtEndDate.getText().toString();
                    tableViewModel = new TableViewModel(act, mStartDate, mEndDate, sqliteHelper);
                    isUSBDetected = Utility.checkUSB(act);

                    // Create TableView Adapter
                    final Runnable r = new Runnable() {
                        @SuppressLint("LongLogTag")
                        public void run() {
                            try {
                                tableViewModel.csvFileExportFunction(mRadioButtonValue, false, isUSBDetected);
                            } catch (Exception e) {
                                Log.e("Error Click Export Button :- ", e.getMessage());
                            }
                        }
                    };
                    mHandler.postDelayed(r, 1000);
                }

            }
        });

        mTxtSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetCounter(1);
//                isEmailButtonClicked = true;
//                isExportButtonClicked = false;
                email = mTxtEmail.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Please Enter Email Address.", Toast.LENGTH_SHORT).show();

                } else if (TextUtils.isEmpty(mTxtStartDate.getText().toString())) {
                    Toast.makeText(ReportFilterActivity.this, "Please select start date", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(mTxtEndDate.getText().toString())) {
                    Toast.makeText(ReportFilterActivity.this, "Please select end date", Toast.LENGTH_LONG).show();
                } else if (Utility.getshortDateFromString(mTxtStartDate.getText().toString()).after(Utility.getshortDateFromString(mTxtEndDate.getText().toString())))
                {
                    Utility.ShowMessage(act, "Warning", "End Date must be higher than Start Date.","Ok");
                }
                else {
                    if (email.matches(emailPattern)) {
                        if (act == null) {
                            Log.e("LOG_TAG", "Activity context is null");
                        } else if (prefManager.getHostName() == null || !prefManager.getHostName().contains("http")){
                            Log.e("HostName :- ", "Host Name is Not Available");
                            Utility.ShowMessage(act, "Warning", "Server has not been configured to send email. \nPlease contact administrator.","Ok");
                            return;
                        }
                        else {
                            alertDialogBoxEmail();
                            mStartDate = mTxtStartDate.getText().toString();
                            mEndDate = mTxtEndDate.getText().toString();
                            tableViewModel = new TableViewModel(act, mStartDate, mEndDate,sqliteHelper);
                            final Runnable r = new Runnable() {
                                public void run() {
                                    if (rackDetailsModel != null) {
                                        if (mRadioButtonValue.equals("1")) {
                                            tableViewModel.getSendReportEmail_Api(mRadioButtonValue, email, rackDetailsModel, allentownBlowerApplication, prefManager, act, false);
                                        }
                                        else {
                                            tableViewModel.csvFileSendEmailFunction_1(mRadioButtonValue, email, rackDetailsModel, allentownBlowerApplication, act, prefManager);
                                        }
                                    }else {
                                        Utility.dismissAlertDialog();
                                    }
                                }
                            };
                            mHandler.postDelayed(r, 1000);
                            mLinearLayoutTable.setVisibility(View.GONE);
                            mLinearLayoutReset.setVisibility(View.GONE);
                            relative_progress_report.setVisibility(View.GONE);
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid email address.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mTxtResetFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetCounter(1);
                resetFunction();
            }
        });

        mTxtEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetCounter(1);
                AlertDialogBox("Email", mTxtEmail);
            }
        });

        mLinearLayoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isViewReportClicked) {
                    isViewReportClicked = false;
                    mLinearLayoutDate.setVisibility(View.VISIBLE);
                    mLinearLayoutTable.setVisibility(View.GONE);
                    mLinearLayoutReset.setVisibility(View.GONE);
//                    resetFunction();
                } else {
                    isViewReportClicked = false;
                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nBackPressedReportAndRack);
                    ResetCounter(0);
                    finish();
                }

            }
        });

    }

    private void resetFunction() {
        isEmailButtonClicked = false;
        isExportButtonClicked = false;
        isViewReportClicked = false;
        mLinearLayoutDate.setVisibility(View.VISIBLE);
//                mRelativeLayoutTable.setVisibility(View.GONE);
        mLinearLayoutTable.setVisibility(View.GONE);
        mLinearLayoutReset.setVisibility(View.GONE);
        mTxtStartDate.setText("");
        mTxtEndDate.setText("");
//                mRadioGroup.setOnCheckedChangeListener(null);
        mRadioButton_Max_Min_Avg.setChecked(true);
        mRadioButtonValue = "1";
    }

    public void startDateDialog() {
        Utility.hideNavigationBar(act);
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {


//                        if (dayOfMonth <= 9) {
//                            mTxtStartDate.setText(year + "-" + (monthOfYear + 1) + "-" + "0" + dayOfMonth);
//                        } else {
//                            mTxtStartDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
//                        }

                        if (monthOfYear <=8 )
                        {
                            if (dayOfMonth <= 9) {
                                mTxtStartDate.setText(year + "-" + "0" +(monthOfYear + 1) + "-" + "0" + dayOfMonth);
                            } else {
                                mTxtStartDate.setText(year + "-" + "0" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }
                        else
                        {
                            if (dayOfMonth <= 9) {
                                mTxtStartDate.setText(year + "-" + (monthOfYear + 1) + "-" + "0" + dayOfMonth);
                            } else {
                                mTxtStartDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }
                        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd"); // hh:mm:ss aa
                        Date now = new Date();
                        String strDate = sdfDate.format(now);
                        Date pdate = Utility.getshortDateFromString(strDate);

                        if (Utility.getshortDateFromString(mTxtStartDate.getText().toString()).after(pdate))
                        {
                            Utility.ShowMessage(act, "Warning", "Start Date can not be higher than Today's date. \nSelected Start Date : " + mTxtStartDate.getText(),"Ok");
                            mTxtStartDate.setText("");
                            return;
                        }


                    }
                }, mYear, mMonth, mDay);

        Calendar mincalendar = Calendar.getInstance();
        mincalendar.setTime(Calendar.getInstance().getTime());
        mincalendar.add(Calendar.DATE,-90);
        datePickerDialog.getDatePicker().setMinDate(mincalendar.getTimeInMillis());

        Calendar maxcalendar = Calendar.getInstance();
        maxcalendar.setTime(Calendar.getInstance().getTime());
        datePickerDialog.getDatePicker().setMaxDate(maxcalendar.getTimeInMillis());

        datePickerDialog.show();

        datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Utility.hideNavigationBar(act);
                dialog.cancel();
            }
        });

        datePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Utility.hideNavigationBar(act);
                dialog.dismiss();
            }
        });
    }

    public void endDateDialog() {
        Utility.hideNavigationBar(act);
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

//                        if (dayOfMonth <= 9) {
//                            mTxtEndDate.setText(year + "-" + (monthOfYear + 1) + "-" + "0" + dayOfMonth);
//                        } else {
//                            mTxtEndDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
//                        }


                        if (monthOfYear <=8 ){
                            if (dayOfMonth <= 9) {
                                mTxtEndDate.setText(year + "-" + "0" + (monthOfYear + 1) + "-" + "0" + dayOfMonth);
                            } else {
                                mTxtEndDate.setText(year + "-" + "0" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }
                        else
                        {
                            if (dayOfMonth <= 9) {
                                mTxtEndDate.setText(year + "-" + (monthOfYear + 1) + "-" + "0" + dayOfMonth);
                            } else {
                                mTxtEndDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }

                        if (Utility.getshortDateFromString(mTxtStartDate.getText().toString()).after(Utility.getshortDateFromString(mTxtEndDate.getText().toString())))
                        {
                            Utility.ShowMessage(act, "Warning", "End Date must be higher than Start Date. \nSelected End Date : " + mTxtEndDate.getText(),"Ok");
                            mTxtEndDate.setText("");
                            return;
                        }

                    }
                }, mYear, mMonth, mDay);
        Calendar mincalendar = Calendar.getInstance();
        mincalendar.setTime(Calendar.getInstance().getTime());
        mincalendar.add(Calendar.DATE,-90);
        datePickerDialog.getDatePicker().setMinDate(mincalendar.getTimeInMillis());

        Calendar maxcalendar = Calendar.getInstance();
        maxcalendar.setTime(Calendar.getInstance().getTime());
        datePickerDialog.getDatePicker().setMaxDate(maxcalendar.getTimeInMillis());

        datePickerDialog.show();

        datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Utility.hideNavigationBar(act);
                dialog.cancel();
            }
        });

        datePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Utility.hideNavigationBar(act);
                dialog.dismiss();
            }
        });
    }

    // EditTextAlertDailogBox AlertDailogBox
    public void AlertDialogBox(final String type, final TextView textView) {

        alertview_selection = new Dialog(act);
        alertview_selection.setCancelable(false);
//        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
//        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);
//        alertview_selection.getWindow().setLayout(width, height);

        alertview_selection.setContentView(R.layout.alertview_edittext_layout_rack_setup); // EditText Screen
        alertview_selection.getWindow().setGravity(Gravity.TOP);
        alertview_selection.show();

        txt_Title_alartview_box = alertview_selection.findViewById(R.id.txt_Title_alartview_box);
        edit_EnterTxt_alartview_box = alertview_selection.findViewById(R.id.edit_EnterTxt_alartview_box);
        btn_Ok_alartview_box = alertview_selection.findViewById(R.id.btn_Save_alartview_box_report);
        btn_Cancel_alartview_box = alertview_selection.findViewById(R.id.btn_Cancel_alartview_box_report);

        txt_Title_alartview_box.setText(type);

        btn_Cancel_alartview_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertview_selection.dismiss();
            }
        });

        edit_EnterTxt_alartview_box.requestFocus();

        btn_Ok_alartview_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals("Email")) {
                    if (edit_EnterTxt_alartview_box.getText().toString().equals(" ") || TextUtils.isEmpty(edit_EnterTxt_alartview_box.getText().toString())) {
                        Toast.makeText(act, "Please Enter Email.", Toast.LENGTH_SHORT).show();
                    } else {
                        if (edit_EnterTxt_alartview_box.getText().toString().length() == 0) {
                            Utility.ShowMessage(act, "Warning!", "Please Enter Valid Email Address.", "OK");
                        } else {
                            textView.setText(edit_EnterTxt_alartview_box.getText().toString());
                            alertview_selection.dismiss();
                        }
                    }
                } else {

                }
            }
        });

    }

    @Override
    public void update(Observable o, Object arg) {
        if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nReportViewBindSuccessfully) {
//            if (isEmailButtonClicked) {
//                isEmailButtonClicked = false;
//                isViewReportClicked = false;
//                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nReportEmailSuccessfully);
//            } else if (isExportButtonClicked) {
//                isExportButtonClicked = false;
//                isViewReportClicked = false;
//                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nReportExportSuccessfully);
//            } else {
                relative_progress_report.setVisibility(View.GONE);
                mLinearLayoutDate.setVisibility(View.GONE);
                mLinearLayoutTable.setVisibility(View.VISIBLE);
                mLinearLayoutReset.setVisibility(View.VISIBLE);
                Utility.dismissAlertDialog();
//            }

        } else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nReportEmailSuccessfully) {
            isEmailButtonClicked = false;
            mLinearLayoutDate.setVisibility(View.VISIBLE);
            mLinearLayoutTable.setVisibility(View.GONE);
            mLinearLayoutReset.setVisibility(View.GONE);
            Utility.dismissAlertDialog();
            try {
                tableViewModel.csvFileSendEmailFunction(mRadioButtonValue, email);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
        } else if (allentownBlowerApplication.getObserver().getValue() == ObserverActionID.nReportExportSuccessfully) {
            isExportButtonClicked = false;
            mLinearLayoutDate.setVisibility(View.VISIBLE);
            mLinearLayoutTable.setVisibility(View.GONE);
            mLinearLayoutReset.setVisibility(View.GONE);
            try {
//                tableViewModel.csvFileExportFunction(mRadioButtonValue,false, isUSBDetected, listColumnHeader);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }

        }
    }

    public void alertDialogBox() {
        Utility.ShowMessageReport(act, "Please wait while loading data...");
    }

    public void alertDialogBoxEmail() {
        Utility.ShowMessageReport(act, "Please wait while sending mail...");
    }

    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus) {
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
//        registerReceiver(broadcast_reciever, new IntentFilter(ACTION_USB_PERMISSION));
    }

    @Override
    protected void onPause() {
        super.onPause();
//        registerReceiver(broadcast_reciever, new IntentFilter(ACTION_USB_PERMISSION));
//        if (Utility.broadcast_reciever != null){
//            unregisterReceiver(Utility.broadcast_reciever);
//        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

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

//                    try {
//                        if (alertview_pre_filter_reset.isShowing()) {
//                            alertview_pre_filter_reset.dismiss();
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }

//                    try {
//                        if (alertview_selection.isShowing()) {
//                            alertview_selection.dismiss();
//                        }
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }

//                    try {
//                        if (Utility.alertview_setting_password.isShowing()) {
//                            Utility.alertview_setting_password.dismiss();
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }


                    // ServiceCommunication
                    /*if (!isMyServiceRunning(MyService.class)) {*/
//                    if (service_myservice == null) {
//                        if (!alertview_diagnostics.isShowing() && !alertview_diagnostics_details.isShowing()) {
//                            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
//                        }
//                    }
//
//                    final int sdk = android.os.Build.VERSION.SDK_INT;
//                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
//                        layout_menubar.setBackgroundDrawable(ContextCompat.getDrawable(act, R.drawable.dark_green_home_screen_box));
//                    } else {
//                        layout_menubar.setBackground(ContextCompat.getDrawable(act, R.drawable.dark_green_home_screen_box));
//                    }

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
}