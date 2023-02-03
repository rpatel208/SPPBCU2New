package com.allentownblower.common;

import android.annotation.SuppressLint;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Mayur Yadav on 01 Sep 2019.
 */
public class CodeReUse {

    public static String strPackageName, strDeviceID, strAppversion, strAppName, strTimezone;

    //public static String DefaultSerialPort = "/dev/ttyUSB0"; // DefaultSerialPort DefaultSerialPort this is for TCP-71W ***********This is working one.
    //public static String DefaultSerialPort = "/dev/ttyUSB1"; // DefaultSerialPort this is for TCP-71W ***********This is working one.
    public static String DefaultSerialPort = "/dev/ttyUSB0";
    //public static String DefaultSerialPort = "/dev/ttyS1"; // DefaultSerialPort this is for UTC

    //ttyUSB0,1 , ttyS0,ttyS2,ttyS4 are the ports on new winmate 10.1" touch screen and ttyUSB0 is working fine.
    //ttymxc0 is the one on 7.1" winmate touch screen and it's working..
    //ttyUSB0 is on new 7" inch Winmate upgraded touchscreen....list of ports ttyUSB0, ttyUSB1,ttyS0,ttyS2,ttyS4,ttyFIQ0
    //ttymxc0, ttymxc1, ttyUSB0,ttyUSB1.. 7 Inch Advantech touch screen
    //i tried ttymxc0

    //New Winmate TS with RS485 Jonathan said to use this /dev/ttyS1
    //public static String DefaultSerialPort = "/dev/ttyS1";

    public static int DefaultBaudRate = 9600; // Default Baud Rate Value: 4800, 9600, 19200, 38400, 57600, 115200

    public static int TimerInterval = 1000; // 1 Second for Clock display on the screens
    public static int PostDelayedReadData = 1000; // 1 Second is the read delay after sending command
    public static int PostDelayedReadSetPointData = 2000; // 2 Second is the read delay after sending command
    public static int DiagnosticsServiceTimerIntervalWithFeedBack = 5000; // 5 Second service interval
    public static int DiagnosticsServiceTimerIntervalDataonly = 2000; // 2 Second service interval
    public static int ServiceTimerInterval = 5000; // 5 Second service interval

    public static int LogServiceTimerInterval = 3000; // 10 Second service interval
    public static int logcounter = 0;

    public static int DefaultSettingPassword = 7951; // Default Setting Password
    public static int DefaultDiagnosticsPassword = 999999; // Default Diagnostics Password
    public static int DefaultDiagnosticsDetailPassword = 999998; // Default Diagnostics Details Password
    public static int DefaultBluetoothMaster = 11111; // Default Bluetooth Master Password
    public static int DefaultReportPassword = 1968; // Default Report Password

    public static boolean isDebugMode = false; // isDebugMode
    public static boolean isBolwerAdmin = false; // isBolwerAdmin
    public static boolean isBolwerConnected = false; // isBolwerConnected
    public static boolean isTestingMode = true; // isBolwerConnected
    public static String folderName = "Allentown Blower Report"; // isBolwerConnected

    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat formatreport = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat formatreportTime = new SimpleDateFormat("hh:mm:ss");

    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat newTimeFormat = new SimpleDateFormat("hh:mm:ss aa");

    @SuppressLint("DefaultLocale")
    public static NumberFormat formatter = new DecimalFormat("00.00");

    @SuppressLint("DefaultLocale")
    public static NumberFormat formatter2Digit = new DecimalFormat("00");

    @SuppressLint("DefaultLocale")
    public static NumberFormat formatter3Digit = new DecimalFormat("0.000");

    @SuppressLint("DefaultLocale")
    public static NumberFormat formatter4Digit = new DecimalFormat("0.0000");

    @SuppressLint("DefaultLocale")
    public static NumberFormat formatterDecimal1Digit = new DecimalFormat("0.0");

    @SuppressLint("DefaultLocale")
    public static NumberFormat formatterDecimal2Digit = new DecimalFormat("00.0");

    //To check if customer is active on server then send the data if not then do not send
    public static boolean isCustomerActive = false; //

    //tosend the S command data very first time
    public static boolean SendScmdDataFirstTime = true;

}
