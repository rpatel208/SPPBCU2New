package com.allentownblower.communication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.allentownblower.application.AllentownBlowerApplication;
import com.allentownblower.common.CodeReUse;
import com.allentownblower.common.ObserverActionID;
import com.allentownblower.common.PrefManager;
import com.allentownblower.common.ResponseHandler;
import com.allentownblower.common.Utility;

import java.util.HashMap;

/**
 * Created by Mayur Yadav on 01 Sep 2019.
 */
public class SerialPortConversion {

    private static final String TAG = "SerialPortConversion";

    private Activity act;

    private int mfd = -1;
    private int ct = 0;

    // Used to load the 'comport_fun' library on application startup.
    static {
        System.loadLibrary("comport_fun");
    }

    public native int node_open(String nodePath, int BaudRate);

    public native int node_close(int handle);

    public native byte[] node_read(int handle, int delayTime);

    public native int node_write(int handle, byte[] cmd, int len);

    HashMap<String, String> stringHashMap = new HashMap<String, String>();



    private SerialPortFinder portFinder;
    private ResponseHandler responseHandler;
    private PrefManager prefManager;
    private PortListDialogs portListDialogs;
    private Handler mHandler = new Handler();

    private String[] BLFeedback = {"F01", "F02", "F03", "F04", "F05", "F06", "F07", "F08", "F09", "F10", "F11", "F12", "F13", "F14", "F15", "F16", "F17"};
    private String[] BLSetPoint = {"S01", "S02", "S03", "S04", "S05", "S06", "S07", "S08", "S09", "S10", "S11", "S12", "S13", "S14", "S15", "S16", "S17", "S18", "S19", "S20", "S21", "S22", "S23", "S24", "S25", "S26", "S27"};
    private String[] BLDiagnostics = {"D01", "D02", "D03", "D04", "D05", "D06", "D07", "D08", "D09", "D10", "D11", "D12", "D13", "D14", "D15", "D16", "D17", "D18", "D19", "D20", "D21", "D22", "D23", "D24", "D25", "D26", "D27", "D28", "D29", "D30", "D31"};
    private String[] BLWiFi = {"W01", "W02", "W03", "W04", "W05", "W06", "W07", "W08", "W09", "W10", "W11", "W12"};

    public SerialPortConversion(Activity activity) {
        act = activity;
        portFinder = new SerialPortFinder();
        responseHandler = new ResponseHandler(act);
        prefManager = new PrefManager(act);
        portListDialogs = new PortListDialogs();
    }

    public SerialPortConversion(Activity activity, ResponseHandler _responseHandler) {
        act = activity;
        portFinder = new SerialPortFinder();
        responseHandler = _responseHandler;
        prefManager = new PrefManager(act);
        portListDialogs = new PortListDialogs();
    }

    /******************************************************************
     * Open serial port
     * baud rate value: 4800, 9600, 19200, 38400, 57600, 115200
     *****************************************************************/
    public boolean openNode(Context context) {
        //Open serial port and get mfd value
        if (mfd == -1) {
            Toast.makeText(act, "Port : " + CodeReUse.DefaultSerialPort + ", baudrate = " + String.valueOf(CodeReUse.DefaultBaudRate), Toast.LENGTH_LONG).show();
            mfd = node_open(CodeReUse.DefaultSerialPort, CodeReUse.DefaultBaudRate);
            Utility.Log(TAG, "open node fd = " + mfd + " Port= "+CodeReUse.DefaultSerialPort +", baudrate = " + String.valueOf(CodeReUse.DefaultBaudRate));

            if (CodeReUse.isBolwerAdmin) {
                mfd = 0;
            } else {
                if (CodeReUse.isDebugMode) {
                    mfd = 21;
                }

                if (mfd == 0) {
                    prefManager.setOpenNode(false);
                    try {
                        if (portFinder.getPortLists().size() != 0) {
                            portListDialogs.show(context, portFinder.getPortLists());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    writeNode("S", -2);

                    prefManager.setOpenNode(true);
                }
            }
        } else if (mfd == 0) {

            Toast.makeText(context, "Sorry! Selected Serial Port Does Not Work", Toast.LENGTH_LONG).show();

            prefManager.setOpenNode(false);
            try {
                Toast.makeText(context, "List of Ports : " + String.valueOf(portFinder.getPortLists().size()), Toast.LENGTH_LONG).show();
                if (portFinder.getPortLists().size() != 0) {
                    portListDialogs.show(context, portFinder.getPortLists());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    /******************************************************************
     * Close serial port
     *****************************************************************/
    public void closeNode() {
        //Close serial port by mfd
        node_close(mfd);
        prefManager.setOpenNode(false);
        Utility.Log(TAG, "close node fd = " + mfd);
//        mfd = -1;
    }

    /******************************************************************
     * Write serial port
     * value type is byte array
     *****************************************************************/
    @SuppressLint("NewApi")
    public void writeNode(final String command, final int isStart) {
        //String rst = "F";
        String code = String.format(command + "\r\n");
        byte[] data = code.toString().getBytes();
//        byte[] data = new byte[0];
//        byte[] data = code.getBytes(StandardCharsets.US_ASCII);
//        for (int i=0; i<data.length; i++)
//        node_write(mfd, data, data[i]);

        Utility.Log(TAG, "write node = " + new String(data).trim());

        //Write data to serial port
        node_write(mfd, data, data.length);

        /*try {
            if (command.equals("S")) {
                Thread.sleep(CodeReUse.PostDelayedReadSetPointData);
            } else {
                Thread.sleep(CodeReUse.PostDelayedReadData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Do something after 1s = 1000ms
        readNode(command,isStart);*/

        final Runnable r = new Runnable() {
            public void run() {
                readNode(command, isStart);
            }
        };

//        if (command.startsWith("W")){
//            mHandler.postDelayed(r, 2000);
//        }else {
//            mHandler.postDelayed(r, 1000);
//        }


        //below i added on 08/12/21 for testing. as we want to get the data after reseting filter or changing the set point
        //the orginal was the line 186 and 187
        if (command.length() > 2)
            mHandler.postDelayed(r, 500);
        else
            mHandler.postDelayed(r, 2000); //for testing with tcp 4 gb unit

        Utility.Log(TAG, "IsStart Value : " + String.valueOf(isStart));
        //mHandler.postDelayed(r, 1000); //with old tcp 2 GB and new tcp 4 GB it was 1000 delay
        //mHandler.postDelayed(r, 2000); //with new utc touch screen i have to let it 2000 then i received all data.
    }

    /******************************************************************
     * Read serial port
     * delayTime is polling of read in jni layer
     * 0 = 0s, 1 = 1s, 2 = 2s, 3 = 3s�K
     *****************************************************************/
    @SuppressLint("SetTextI18n")
    public void readNode(final String command, int isStart) {

        //Get data from serial port
        int delayTime = 0;
        byte[] tmpValue = node_read(mfd, delayTime);

        String ResultData = "";

        if (CodeReUse.isDebugMode) {

            if (command.equals("F")) {
                if (Utility.getCurrentTimeOnlyInMin().equals("07:06 PM")) { // prefilter icon
                    ResultData = "F01=4B4B\n" +
                            "    F02=2929\n" +
                            "    F03=4010\n" +
                            "    F04=C03C\n" +
                            "    F05=0222\n" +
                            "    F06=01C7\n" +
                            "    F07=0F0F\n" +
                            "    F08=003E\n" +
                            "    F09=8032\n" +
//                        "    F10=0000\n" +
                            "    F10=0000\n" +
                            "    F11=6264\n" +
//                        "    F12=2020\n" +
//                        "    F12=C087\n" +
                            "    F12=C080\n" +
                            "    F13=0730\n" +
                            "    F14=0100\n" +
                            "    F15=0000\n" +
                            "    F16=0000\n" +
                            "    F17=0000";
                } else if (Utility.getCurrentTimeOnlyInMin().equals("07:07 PM")) { // Red Screen
                    ResultData = "F01=4B4B\n" +
                            "    F02=2929\n" +
                            "    F03=4010\n" +
                            "    F04=C03C\n" +
                            "    F05=0222\n" +
                            "    F06=01C7\n" +
                            "    F07=0F0F\n" +
                            "    F08=003E\n" +
                            "    F09=8032\n" +
//                        "    F10=0000\n" +
                            "    F10=0000\n" +
                            "    F11=6264\n" +
//                        "    F12=2020\n" +
//                        "    F12=C087\n" +
                            "    F12=0505\n" +
                            "    F13=0730\n" +
                            "    F14=0100\n"+
                            "    F15=0000\n" +
                            "    F16=0000\n" +
                            "    F17=0000";

                } else if (Utility.getCurrentTimeOnlyInMin().equals("07:08 PM")) { // yellow screen
                    ResultData = "F01=4B4B\n" +
                            "    F02=2929\n" +
                            "    F03=4010\n" +
                            "    F04=C03C\n" +
                            "    F05=0222\n" +
                            "    F06=01C7\n" +
                            "    F07=0F0F\n" +
                            "    F08=003E\n" +
                            "    F09=8032\n" +
//                        "    F10=0000\n" +
                            "    F10=0000\n" +
                            "    F11=6264\n" +
//                        "    F12=2020\n" +
                            "    F12=0202\n" +
//                            "    F12=C080\n" +
                            "    F13=0730\n" +
                            "    F14=0100\n"+
                            "    F15=0000\n" +
                            "    F16=0000\n" +
                            "    F17=0000";
                } else {
                    ResultData = "F01=4B4B\n" +
                            "    F02=2929\n" +
                            "    F03=4010\n" +
                            "    F04=C03C\n" +
                            "    F05=0222\n" +
                            "    F06=01C7\n" +
                            "    F07=0F0F\n" +
                            "    F08=003E\n" +
                            "    F09=8032\n" +
//                        "    F10=0000\n" +
                            "    F10=0000\n" +
                            "    F11=6264\n" +
                            "    F12=2020\n" +
//                        "    F12=C087\n" +
//                            "    F12=C080\n" +
                            "    F13=0730\n" +
                            "    F14=0100\n"+
                            "    F15=0000\n" +
                            "    F16=0000\n" +
                            "    F17=0000";
                }
            } else if (command.equals("S")) {
                ResultData = "S01=0F08\n" +
                        "    S02=7878\n" +
                        "    S03=01F4\n" +
                        "    S04=01F4\n" +
                        "    S05=03E8\n" +
                        "    S06=012C\n" +
                        "    S07=0F3C\n" +
                        "    S08=0000\n" +
                        "    S09=815E\n" +
                        "    S10=8032\n" +
                        "    S11=1400\n" +
//                        "    S12=0000\n" +
                        "    S12=0000\n" +
                        "    S13=000A\n" +
                        "    S14=003C\n" +
                        "    S15=0000\n" +
                        "    S16=4C3F\n" +
                        "    S17=4727\n" +
                        "    S18=0000\n" +
                        "    S19=0000\n" +
                        "    S20=0000\n" +
                        "    S21=0000\n" +
                        "    S22=4C42\n" +
                        "    S23=2D17\n" +
                        "    S24=4C42\n" +
                        "    S25=2D17\n" +
                        "    S26=0000\n" +
                        "    S27=0000";
            } else if (command.equals("D")) {
                ResultData = "D01=008C\n" +
                        "D02=000F\n" +
                        "D03=00CB\n" +
                        "D04=8019\n" +
                        "D05=813D\n" +
                        "D06=10F1\n" +
                        "D07=F6F3\n" +
                        "D08=1FC8\n" +
                        "D09=1700\n" +
                        "D10=01C6\n" +
                        "D11=016F\n" +
                        "D12=0811\n" +
                        "D13=081A\n" +
                        "D14=06B8\n" +
                        "D15=0453\n" +
                        "D16=06B0\n" +
                        "D17=06BF\n" +
                        "D18=067F\n" +
                        "D19=080A\n" +
                        "D20=067C\n" +
                        "D21=068A\n" +
                        "D22=081A\n" +
                        "D23=07F0\n" +
                        "D24=0819\n" +
                        "D25=081A\n" +
                        "D26=0811\n" +
                        "D27=082B\n" +
                        "D28=0811\n" +
                        "D29=0812\n" +
                        "D30=8D44";
            } else if (command.equals("W")) {
                ResultData = "W01=AllentownGuest\n" +
                        "    W02=starship\n" +
                        "    W03=10.1.30.9\n" +
                        "    W04=255.255.254.0\n" +
                        "    W05=10.1.30.1\n" +
                        "    W06=68.105.28.11\n" +
                        "    W07=192.168.1.20\n" +
                        "    W08=0\n" +
                        "    W09=0\n" +
                        "    W10=4\n" +
                        "    W11=1\n" +
                        "    W12=4C:55:CC:17:42:B0";
            } else {
                if (!command.equals("D12")) {
                    ResultData = "OK";
                }
            }
        } else {
            ResultData = new String(tmpValue).trim();
        }


        if(command.equals("W") || command.equals("D")|| command.equals("S")) {
            try {
                Thread.sleep(1000);
                if(command.equals("W") || command.equals("D"))
                    Utility.Log(TAG, "Command =  " + ResultData);
            }
            catch (Exception e)
            {
                Log.e(TAG,"Error : " + e.getLocalizedMessage());
            }
        }

//        if (command.equalsIgnoreCase("S07=0F3C")){
//            ResultData = "��";
//        }

       // Utility.Log(TAG, "ResultData =  " + ResultData.toString());
        //ResultData =  ��  we get like this two questionmarks when the serial cable is disconnected.
        Utility.Log(TAG, "Comport Received Data Length = " + ResultData.length() + " Counter value : " + String.valueOf(CodeReUse.logcounter));
        Utility.Log(TAG, "Command Finished");
        CodeReUse.logcounter = CodeReUse.logcounter + 1;
        if (CodeReUse.logcounter > 40000)
        {
            CodeReUse.logcounter = 0;
        }

        if (ResultData.equals("OK")) {
            if (isStart == -1) {
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointData);
            } else if (isStart == -2) {
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
            } else if (isStart == -3) {
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nDiagnosticsDataOnly);
            } else if (isStart == 1) {
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointDataOnly);
            } else if (isStart == 3) {
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointData);
//                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointDataS);
            } else if (isStart == 4) {
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetWifiDataOnlyResponse);
//                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointDataS);
            }
            else if (isStart == 201){
                responseHandler.UpdateCommandCompleted_Api();
                int count = prefManager.getCount();
                String[] setCommand = prefManager.loadArray();
                int multiCommandCount = setCommand.length - 1;
                if (count <= multiCommandCount){
                    responseHandler.commandCallingFromApi(count);
                }else {
                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointData);
                }

            } else if (isStart == 202) {
                responseHandler.UpdateCommandCompleted_Api();
                int count = prefManager.getCount();
                String[] setCommand = prefManager.loadArray();
                int multiCommandCount = setCommand.length - 1;
                if (count <= multiCommandCount){
                    responseHandler.commandCallingFromApi(count);
                }else {
                    AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetWifiDataOnly);
                }
            } else if (command.startsWith("W")) {
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetWifiDataOnly);
            } else if (command.equals("D31")) {
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.mD31CommandResponse);
            } else if (isStart == 101) {
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nRackSetUp_Polarity_Value_Write_Only);
            }
//            else if (isStart == 102) {
//                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nRackSetUp_Supply_CFM_Value_Write_Only);
//            }
            else if (isStart == 102) {
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nRackSetUp_Exhaust_WC_Value_Write_Only);
            } else if (isStart == 103) {
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nRackSetUp_Dialog);
            } else if (isStart == 301) {
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nRackSetUp_Polarity_Value_Write_Only_From_Setting_Screen);
            }
//            else if (isStart == 302) {
//                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nRackSetUp_Supply_CFM_Value_Write_Only_From_Setting_Screen);
//            }
            else if (isStart == 302) {
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nRackSetUp_Exhaust_WC_Value_Write_Only_From_Setting_Screen);
            } else if (isStart == 303) {
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nRackSetUp_Dialog);
            } else if(isStart == 208) {
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetACCommand);
            } else if(isStart == 209) {
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nRackSetUp_Dialog);
            }
            else if(isStart == 210)
            {
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetS26OnCommand);
            }
            else if (isStart == 211)
            {
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
            }
        } else if (command.equals("D12")) {
            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
        } else {
            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nCloseProgressBar);
        }

        /* if (ResultData.length() > 137) {*/
        stringHashMap.clear();

        switch (command) {
            case "F":
                if (command.equals("F") && ResultData.startsWith("F01") && ResultData.contains("F17")) {
                    /*String[] fieldWithValue = ResultData.replace("\n",",").trim().replace(" ","").split(",");*/

//                    String newrstdata = ResultData.replaceAll("F13=0600","F13=0000");
//                    ResultData = newrstdata;
//                    String nrst = ResultData.replaceAll("F12=2030","F12=0000");
//                    ResultData = nrst;
                    boolean isInsert = true;

                    isInsert = mCommanMethod(ResultData, BLFeedback);

                    if (isInsert) {
                        if (isStart == 2) {
                            responseHandler.InsertBLFeedbackTable(stringHashMap, 0);
                        } else if (isStart == 3) {
                            responseHandler.InsertBLFeedbackTable(stringHashMap, 3);
                        } else {
                            responseHandler.InsertBLFeedbackTable(stringHashMap, 1);
                        }
                    /*else if (isStart == 0){
                        responseHandler.InsertBLFeedbackTable(stringHashMap, true);
                    }
                    else if (isStart == 1) {
                        responseHandler.InsertBLFeedbackTable(stringHashMap, true);
                    }*/

                    } else {
                        failedCommandF(isStart);
                    }

                } else {
                    failedCommandF(isStart);
                }
                break;

            case "S":
                if (command.equals("S") && ResultData.startsWith("S01") && ResultData.contains("S27")) {
                    /* String[] fieldWithValue = ResultData.replace("\n",",").trim().replace(" ","").split(",");*/

                    boolean isInsert = true;

                    isInsert = mCommanMethod(ResultData, BLSetPoint);

                    if (isInsert) {
                        responseHandler.InsertBLSetPointTable(stringHashMap, isStart);
                    } else {
                        failedCommandS(isStart);
                    }
                } else {
                    failedCommandS(isStart);
                }
                break;

            case "D":
                if (command.equals("D") && ResultData.startsWith("D01") && ResultData.contains("D30")) {
//                        String[] fieldWithValue = ResultData.replace("\n",",").trim().replace(" ","").split(",");

                    boolean isInsert = true;

                    isInsert = mCommanMethod(ResultData, BLDiagnostics);

                    if (isInsert) {
                        responseHandler.InsertBLDiagnosticsTable(stringHashMap, isStart);
                    } else {
                        failedCommandD(isStart);
                    }
                } else {
                    failedCommandD(isStart);
                }
                break;

            case "W":
                if (command.equals("W") && ResultData.startsWith("W01") && ResultData.contains("W12")) {

//                    String ResultDataNew = "";

//                    if (ResultData.endsWith("W12="))
//                        ResultDataNew = ResultData.replace("W12=", "W12=4C:55:CC:17:42:B0");

                    /*String[] fieldWithValue = ResultDataNew.replace("\n",",").trim().replace(" ","").split(",");*/

                    boolean isInsert = true;

                    isInsert = mCommanMethodForW(ResultData, BLWiFi);

                    if (isInsert) {
                        responseHandler.InsertBLWiFiTable(stringHashMap, isStart);
                    } else {
//                        failedCommandW(isStart);
                    }
                } else {
                    failedCommandW(isStart);
                }
                break;

            default:
                if (command.startsWith("S")) {
                    if (command.equals("S")) {
                        writeNode(command, -1);
                    }
                }

                return;
        }
        /* } else {*/
            /*if (command.startsWith("S")) {
                if (command.equals("S")) {
                    writeNode(command, -1);
                }
            }*/
        /*  }*/
    }

    private void failedCommandF(int isStart) {
        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nFeedbackDataUpdate);
        if (isStart == 2) {
            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nDiagnosticsDataWithFeedbackData);
        } else if (isStart == 3) {
            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSingle_S_F_got_response);
        } else {
            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointData);
        }
        Toast.makeText(act, "Please wait while communication is starting..", Toast.LENGTH_SHORT).show();
    }

    private void failedCommandS(int isStart) {
        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointDataUpdate);
        if (isStart == -1)
            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nFeedbackData);
        else if (isStart == 1)
            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nFeedbackData);
        else if (isStart == -2)
            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStartService);
        else if (isStart == 0)
            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nFeedbackDataSingleForSetting);

        Toast.makeText(act, "Please wait while communication is starting..", Toast.LENGTH_SHORT).show();
    }

    private void failedCommandD(int isStart) {
        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetPointDataUpdate);
        if (isStart == 0) {
            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nFeedbackDataOnly);
        } else if (isStart == -3) {
            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nDiagnosticsDataOnly);
        }
    }

    private void failedCommandW(int isStart) {
        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nWiFiDataUpdate);
        if (isStart == 4) {
            AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nSetWifiDataOnly);
        }
    }

    private boolean mCommanMethod(String ResultData, String[] CommandArray) {
        String[] fieldWithValue = ResultData.replace("\n", ",").trim().replace(" ", "").split(",");

        for (int i = 0; i < fieldWithValue.length; i++) {
            try {
                String[] onlyValue = fieldWithValue[i].trim().split("=");
                if (onlyValue.length == 2) {
                    if ((onlyValue[0].trim().equals(CommandArray[i]) && onlyValue[1].trim().length() == 4)) {
                        stringHashMap.put(onlyValue[0].trim(), onlyValue[1].trim());
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.getStackTrace();
                return false;
            }
        }
        return true;
    }

    private boolean mCommanMethodForW(String ResultData, String[] CommandArray) {
        String[] fieldWithValue = ResultData.replace("\n", ",").trim().replace(" ", "").split(",");

        for (int i = 0; i < fieldWithValue.length; i++) {
            try {
                String[] onlyValue = fieldWithValue[i].trim().split("=");
                if (onlyValue.length == 2) {
                    if ((onlyValue[0].trim().equals(CommandArray[i]))) {
                        stringHashMap.put(onlyValue[0].trim(), onlyValue[1].trim());
                    } else {
                        return false;
                    }
                } else if (onlyValue.length == 1)
                {
                    stringHashMap.put(onlyValue[0].trim(), "XXX");
                }
                else {
                    return false;
                }
            } catch (Exception e) {
                e.getStackTrace();
                return false;
            }
        }
        return true;
    }

}
