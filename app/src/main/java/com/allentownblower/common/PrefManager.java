package com.allentownblower.common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Mayur Yadav on 01 Sep 2019.
 */
public class PrefManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "androidblower";

    private static final String DefaultLockScreen = "isLockScreen";
    private static final String DefaultOpenNode = "isOpenNode";
    private static final String SendCommandS = "SendCommandS";
    private static final String saveMinutesForCommandF = "saveMinutesForCommandF";
    private static final String saveMinutesForCommandFName = "saveMinutesForCommandFName";
    private static final String BluetoothMacAddress = "BluetoothMacAddress";
    private static final String isServiceRunning = "isServiceRunning";
    private static final String isFirstTime = "isFirstTime";
    private static final String currentDate = "currentDate";
    private static final String setCount = "setCount";
    private static final String setKeyFromApi = "setKeyFromApi";
    private static final String setMultiCommandCount = "setMultiCommandCount";
    private static final String setIsStart = "setIsStart";
    private static final String setAchValue = "setAchValue";
    private static final String setPolarityValue = "setPolarityValue";
    private static final String setSupplyValue = "setSupplyValue";
    private static final String setExhaustValue = "setExhaustValue";
    private static final String setPolarityIdValue = "setPolarityIdValue";
    private static final String setModelName = "setModelName";

    private static final String str_Z0 = "str_Z0";
    private static final String str_Z2 = "str_Z2";
    private static final String str_X0 = "str_X0";
    private static final String str_X2 = "str_X2";
    private static final String str_Z1 = "str_Z1";
    private static final String str_X1 = "str_X1";
    private static final String str_PreFilterAlarm = "str_PreFilterAlarm";

    private static final String str_F15 = "str_F15";

    // Passwords
    private static final String SettingPassword = "SettingPassword";
    private static final String DiagnosticsPassword = "DiagnosticsPassword";
    private static final String DiagnosticsDetailPassword = "DiagnosticsDetailPassword";
    private static final String BluetoothDisconnectPassword = "BluetoothDisconnectPassword";
    private static final String ReportPassword = "ReportPassword";
    private static final String HostName = "HostName";
    private static final String SerialNumber = "SerialNumber";
    // NightMode
    private static final String OnNightMode = "OnNightMode";
    private static final String OffNightMode = "OffNightMode";
    private static final String AC ="AcSetPt";
    private static final String DC ="DcSetPt";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setDefaultLockScreen(boolean isLockScreen) {
        editor.putBoolean(DefaultLockScreen, isLockScreen);
        editor.commit();
    }

    public String getACSetPt() {
        return pref.getString(AC, "");
    }

    public void setACSetPt(String acSetPt) {
        editor.putString(AC, acSetPt);
        editor.commit();
    }

    public String getDCSetPt() {
        return pref.getString(DC, "");
    }

    public void setDCSetPt(String dcSetPt) {
        editor.putString(AC, dcSetPt);
        editor.commit();
    }

    public boolean getDefaultLockScreen() {
        return pref.getBoolean(DefaultLockScreen, false);
    }

    public void setOpenNode(boolean isOpenNode) {
        editor.putBoolean(DefaultOpenNode, isOpenNode);
        editor.commit();
    }

    public boolean getOpenNode() {
        return pref.getBoolean(DefaultOpenNode, false);
    }

    public void setSendCommandS(String command) {
        editor.putString(SendCommandS, command);
        editor.commit();
    }

    public String getSendCommandS() {
        return pref.getString(SendCommandS, "S");
    }

    public void setIsStart(int isStart) {
        editor.putInt(setIsStart, isStart);
        editor.commit();
    }

    public int getIsStart() {
        return pref.getInt(setIsStart, 0);
    }

    public void setCount(int count){
        editor.putInt(setCount,count);
        editor.commit();
    }

    public int getCount(){
        return pref.getInt(setCount,0);
    }

    public void setKeyFromApi(String setCommand){
        editor.putString(setKeyFromApi,setCommand);
        editor.commit();
    }

    public boolean saveArray(String[] array) {
        editor.putInt(setKeyFromApi +"_size", array.length);
        for(int i=0;i<array.length;i++){
            editor.putString(setKeyFromApi + "_" + i, array[i]);
        }

        return editor.commit();
    }

    public String[] loadArray() {
        int size = pref.getInt(setKeyFromApi + "_size", 0);
        String array[] = new String[size];
        for(int i=0;i<size;i++)
            array[i] = pref.getString(setKeyFromApi + "_" + i, null);
        return array;
    }

    public String getSetKeyFromApi(){
        return pref.getString(setKeyFromApi,"");
    }

    public void setMultiCommandCount(int multiCommandCount){
        editor.putInt(setMultiCommandCount,multiCommandCount);
        editor.commit();
    }

    public int getMultiCommandCount(){
        return pref.getInt(setMultiCommandCount,0);
    }

    public void setServiceRunning(boolean ServiceRunning) {
        editor.putBoolean(isServiceRunning, ServiceRunning);
        editor.commit();
    }

    public boolean getServiceRunning() {
        return pref.getBoolean(isServiceRunning, false);
    }

    public void setFirstTime(boolean FirstTime) {
        editor.putBoolean(isFirstTime, FirstTime);
        editor.commit();
    }

    public boolean getFirstTime() {
        return pref.getBoolean(isFirstTime, false);
    }

    public void setBluetoothMacAddress(String MacAddress) {
        editor.putString(BluetoothMacAddress, MacAddress);
        editor.commit();
    }

    public String getBluetoothMacAddress() {
        return pref.getString(BluetoothMacAddress, "");
    }

    public void setSettingPassword(String password) {
        editor.putString(SettingPassword, password);
        editor.commit();
    }

    public String getSettingPassword() {
        return pref.getString(SettingPassword, "7951");
    }

    public void setReportPassword(String password) {
        editor.putString(ReportPassword, password);
        editor.commit();
    }

    public String getReportPassword() {
        return pref.getString(ReportPassword, "1968");
    }

    public void setDiagnosticsPassword(String password) {
        editor.putString(DiagnosticsPassword, password);
        editor.commit();
    }

    public String getDiagnosticsPassword() {
        return pref.getString(DiagnosticsPassword, "999999");
    }

    public void setDiagnosticsDetailPassword(String password) {
        editor.putString(DiagnosticsDetailPassword, password);
        editor.commit();
    }

    public String getDiagnosticsDetailPassword() {
        return pref.getString(DiagnosticsDetailPassword, "999998");
    }

    public void setBluetoothDisconnectPassword(String password) {
        editor.putString(BluetoothDisconnectPassword, password);
        editor.commit();
    }

    public String getBluetoothDisconnectPassword() {
        return pref.getString(BluetoothDisconnectPassword, "11111");
    }

    public void setOnNightMode(String time) {
        editor.putString(OnNightMode, time);
        editor.commit();
    }

    public String getOnNightMode() {
        return pref.getString(OnNightMode, "12:00:00 AM");
    }

    public void setOffNightMode(String time) {
        editor.putString(OffNightMode, time);
        editor.commit();
    }

    public String getOffNightMode() {
        return pref.getString(OffNightMode, "12:10:00 AM");
    }

    public void setMinuteForCommandF(String minutes) {
        editor.putString(saveMinutesForCommandF, minutes);
        editor.commit();
    }

    public String getMinuteForCommandF() {
        return pref.getString(saveMinutesForCommandF, "30");
    }

    public void setMinute(int minutes) {
        editor.putInt(saveMinutesForCommandFName, minutes);
        editor.commit();
    }

    public int getMinute() {
        return pref.getInt(saveMinutesForCommandFName, 30);
    }

    public void setCurrentDate(String date) {
        editor.putString(currentDate, date);
        editor.commit();
    }

    public String getCurrentDate() {
        return pref.getString(currentDate, "");
    }

    public void setStrZ0(String strZ0) {
        editor.putString(str_Z0, strZ0);
        editor.commit();
    }

    public String getStrZ0() {
        return pref.getString(str_Z0, "");
    }

    public void setStrZ2(String strZ2) {
        editor.putString(str_Z2, strZ2);
        editor.commit();
    }

    public String getStrZ2() {
        return pref.getString(str_Z2, "");
    }

    public void setStrX0(String strX0) {
        editor.putString(str_X0, strX0);
        editor.commit();
    }

    public String getStrX0() {
        return pref.getString(str_X0, "");
    }

    public void setStrX2(String strX2) {
        editor.putString(str_X2, strX2);
        editor.commit();
    }

    public String getStrX2() {
        return pref.getString(str_X2, "");
    }

    public void setStrZ1(String strZ1) {
        editor.putString(str_Z1, strZ1);
        editor.commit();
    }

    public String getStrZ1() {
        return pref.getString(str_Z1, "");
    }

    public void setStrX1(String strX1) {
        editor.putString(str_X1, strX1);
        editor.commit();
    }

    public String getStrX1() {
        return pref.getString(str_X1, "");
    }

    public void setStrPreFilterAlarm(String strPreFilterAlarm) {
        editor.putString(str_PreFilterAlarm, strPreFilterAlarm);
        editor.commit();
    }

    public String getStrPreFilterAlarm() {
        return pref.getString(str_PreFilterAlarm, "");
    }

    public void setHostName(String hostName){
        editor.putString(HostName,hostName);
        editor.commit();
    }

    public String getStrF15() { return  pref.getString(str_F15,""); }

    public void setStrF15(String F15)
    {
        editor.putString(str_F15,F15);
        editor.commit();
    }

    public String getHostName(){
        return pref.getString(HostName,"");
    }

    public void setSerialNumber(String serialNumber){
        editor.putString(SerialNumber,serialNumber);
        editor.commit();
    }

    public String getSerialNumber(){
        return pref.getString(SerialNumber,"");
    }

    public void setModelName(String modelName){
        editor.putString(setModelName,modelName);
        editor.commit();
    }

    public String getModelName(){
        return pref.getString(setModelName,"");
    }

}
