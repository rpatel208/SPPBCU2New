package com.allentownblower.common;

/**
 * Created by Mayur Yadav on 01 Sep 2019.
 */
public class ObserverActionID {

    // Re Connect Node
    public static int nReConnectNode = 0;

    // Starting the service and calling F and S continously
    public static int nStartService = 1;

    // Stoping the service
    public static int nStopService = 2;

    // Call S command one time and F command multiple time
    public static int nSetPointData = 3;

    // Call S command only one time
    public static int nSetPointDataOnly = 4;



    // Call S01 and S02 Some Command one time and F Commmand Multiple Time
    public static int nSetPointCommand = 5;

    // Call S01 and S02 Some Command one time
    public static int nSetPointCommandOnly = 6;

    // Update S Command Data On Screen
    public static int nSetPointDataUpdate = 7;

    // Call F command one time and S command one time
    public static int nFeedbackData = 8;

    // Update F Command Data On Screen
    public static int nFeedbackDataUpdate = 9;

    // Update D12 Command Data On Screen
    public static int nDiagnosticsDataStart = 10;

    // Call D command one time
    public static int nDiagnosticsDataWithFeedbackData = 11;

    // Update Plus Command On HomeScreen
    public static int nPlus = 12;

    // Update Minus Command On HomeScreen
    public static int nMinus = 13;

    // Update Up and Down Command On HomeScreen
    public static int nUp_Down = 14;

    // ReDirect To Settings Screen To Home Screen
    public static int nRedirectHome = 15;

    // ReDirect To SubSettings Screen To Settings Screen
    public static int nRedirectSettings = 16;

    // ReDirect To Bluetooth Search Screen To Settings Screen
    public static int nBluetoothSearch = 17;

    // ReDirect To Bluetooth Disconnect Screen To Settings Screen
    public static int nBluetoothDisconnect = 18;

    // ReDirect To Bluetooth OldDevices Screen To Settings Screen
    public static int nBluetoothOldDevices = 19;

    // Change Password
    public static int nChangePassword = 20;

    // Open Diagnostics Setting Screen
    public static int nDiagnosticsSettings = 21;

    // Open Diagnostics Setting Screen
    public static int nDiagnosticsDetailsSettings = 22;

    //  Update W01 or W02 Command Data On Screen
    public static int nWiFiDataUpdate = 23;

    // Call F command only after D command in Diagnosis details screen
    public static int nFeedbackDataOnly = 24;

    // Call D command one time
    public static int nDiagnosticsDataOnly = 25;

    //Response D31 Command
    public static int mD31CommandResponse = 26;

    // Call S command one time and F command multiple time
    public static int nFeedbackDataSingleForSetting = 27;

    //Single F and S finish for Setting Screen
    public static int nSingle_S_F_got_response = 28;

    // Call S command only one time
    public static int nSetWifiDataOnly = 29;

    // Call W command Response
    public static int nSetWifiDataOnlyResponse = 30;

    // Call W command Only
    public static int nSetWifiCommandOnly = 31;

    // ReDirect To SubSettings Screen To Report Screen
    public static int nRedirectReport = 32;

    //AC Value for Write RackSetUp Screen
    public static int mRackSetUp_AC_Value_Write_Only = 50;
    public static int mRackSetUp_AC_Value_Write_Only_From_Setting_Screen = 51;

    //ACH Value Write for RackSetUp Screen
    public static int nRackSetUp_ACH_Value_Write_Only = 101;
    public static int nRackSetUp_ACH_Value_Write_Only_From_Setting_Screen = 301;

    //Polarity Value Write for RackSetUp Screen
    public static int nRackSetUp_Polarity_Value_Write_Only = 102;
    public static int nRackSetUp_Polarity_Value_Write_Only_From_Setting_Screen = 302;

    //Supply(CFM) Value Write for RackSetUp Screen
    public static int nRackSetUp_Supply_CFM_Value_Write_Only = 103;
    public static int nRackSetUp_Supply_CFM_Value_Write_Only_From_Setting_Screen = 303;

    //Exhaust(WC) Value Write for RackSetUp Screen
    public static int nRackSetUp_Exhaust_WC_Value_Write_Only = 104;
    public static int nRackSetUp_Exhaust_WC_Value_Write_Only_From_Setting_Screen = 304;

    //RackSetUp Screen Completed
    public static int nRackSetUp_Dialog = 105;
    public static int nRackSetUp_Dialog_From_Setting_Screen = 305;

    public static int nCloseProgressBar = 106;

    //ReportView Bind Successfully
    public static int nReportViewBindSuccessfully = 201;

    //ReportView Bind Successfully
    public static int nReportEmailSuccessfully = 202;

    //ReportView Bind Successfully
    public static int nReportExportSuccessfully = 203;

    //onBackPressed From Report and Rack Screen
    public static int nBackPressedReportAndRack = 204;

    //Timer Finish From Report and Rack Screen
    public static int nTimerFinishedReportAndRack = 205;

    //ReportView Bind Successfully
    public static int nCallUnitChangeObserver = 206;

    public static int nSetPointCommandOnly_Api = 207;

    public static int nSetACCommand = 209;
    public static int nSetDCCommand =208;

    public static int nSetS26OffCommand = 210;//S26=0001
    public static int nSetS26OnCommand = 211;//S26=0000

}
