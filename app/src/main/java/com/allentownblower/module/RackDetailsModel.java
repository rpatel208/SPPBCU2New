package com.allentownblower.module;

import android.bluetooth.BluetoothAdapter;
import android.os.Build;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class RackDetailsModel {

    private String mid, mRackBlowerCustomerID,mRackBlowerCustomerName, mABlowerSerial, mABlowerWiFiMAC, mABlowerLANMAC, mABlowerBluetoothMAC, mABlowerWiFiIPAddress, mABlowerLanIPAddress, mABlowerName, mABlowerBuilding, mABlowerRoom, mRackModel,
            mRackSerial, mBlowerModel, mSupplyBlowerSerial, mExhaustBlowerSerial, mAWiViWiFiMAC, mAWiViIPAddress, mAWiViBluetoothMAC, mUpdateCMD,
            mAlertEmailIDs, mReportEmailIDs, mLastAlarm, mLasTmpHMDtAlarm, mTempUnit, mPressureUnit, mAirFlowUnit, mLastSyncDateTime, mModifiedOn, mCreatedOn, mCreatedBy;
    private int  mIsCommModuleExist, mIsUpdateCMD, mIsUpdateCMDCompleted, mUseAlnEmailService,
            mIsRegAlarmOn, mIsTmpHMDAlarmOn, mIsUpdatedByWebApp, mModifiedBy, mIsDeleted;

    public RackDetailsModel() {
    }

    public String getmABlowerSerial() {
        return mABlowerSerial;
    }

    public void setmABlowerSerial(String mABlowerSerial) {
        this.mABlowerSerial = mABlowerSerial;
    }

    public String getmABlowerWiFiMAC() {
        return mABlowerWiFiMAC;
    }

    public void setmABlowerWiFiMAC(String mABlowerWiFiMAC) {
        this.mABlowerWiFiMAC = mABlowerWiFiMAC;
    }

    public String getmABlowerLANMAC() {
        return mABlowerLANMAC;
    }

    public void setmABlowerLANMAC(String mABlowerLANMAC) {
        this.mABlowerLANMAC = mABlowerLANMAC;
    }

    public String getmABlowerBluetoothMAC() {
        return mABlowerBluetoothMAC;
    }

    public void setmABlowerBluetoothMAC(String mABlowerBluetoothMAC) {
        this.mABlowerBluetoothMAC = mABlowerBluetoothMAC;
    }

    public String getmABlowerWiFiIPAddress() {
        return mABlowerWiFiIPAddress;
    }

    public void setmABlowerWiFiIPAddress(String mABlowerWiFiIPAddress) {
        this.mABlowerWiFiIPAddress = mABlowerWiFiIPAddress;
    }

    public String getmABlowerLanIPAddress() {
        return mABlowerLanIPAddress;
    }

    public void setmABlowerLanIPAddress(String mABlowerLanIPAddress) {
        this.mABlowerLanIPAddress = mABlowerLanIPAddress;
    }

    public String getmABlowerName() {
        return mABlowerName;
    }

    public void setmABlowerName(String mABlowerName) {
        this.mABlowerName = mABlowerName;
    }

    public String getmABlowerBuilding() {
        return mABlowerBuilding;
    }

    public void setmABlowerBuilding(String mABlowerBuilding) {
        this.mABlowerBuilding = mABlowerBuilding;
    }

    public String getmABlowerRoom() {
        return mABlowerRoom;
    }

    public void setmABlowerRoom(String mABlowerRoom) {
        this.mABlowerRoom = mABlowerRoom;
    }

    public String getmRackModel() {
        return mRackModel;
    }

    public void setmRackModel(String mRackModel) {
        this.mRackModel = mRackModel;
    }

    public String getmRackSerial() {
        return mRackSerial;
    }

    public void setmRackSerial(String mRackSerial) {
        this.mRackSerial = mRackSerial;
    }

    public String getmBlowerModel() {
        return mBlowerModel;
    }

    public void setmBlowerModel(String mBlowerModel) {
        this.mBlowerModel = mBlowerModel;
    }

    public String getmSupplyBlowerSerial() {
        return mSupplyBlowerSerial;
    }

    public void setmSupplyBlowerSerial(String mSupplyBlowerSerial) {
        this.mSupplyBlowerSerial = mSupplyBlowerSerial;
    }

    public String getmExhaustBlowerSerial() {
        return mExhaustBlowerSerial;
    }

    public void setmExhaustBlowerSerial(String mExhaustBlowerSerial) {
        this.mExhaustBlowerSerial = mExhaustBlowerSerial;
    }

    public String getmAWiViWiFiMAC() {
        return mAWiViWiFiMAC;
    }

    public void setmAWiViWiFiMAC(String mAWiViWiFiMAC) {
        this.mAWiViWiFiMAC = mAWiViWiFiMAC;
    }

    public String getmAWiViIPAddress() {
        return mAWiViIPAddress;
    }

    public void setmAWiViIPAddress(String mAWiViIPAddress) {
        this.mAWiViIPAddress = mAWiViIPAddress;
    }

    public String getmAWiViBluetoothMAC() {
        return mAWiViBluetoothMAC;
    }

    public void setmAWiViBluetoothMAC(String mAWiViBluetoothMAC) {
        this.mAWiViBluetoothMAC = mAWiViBluetoothMAC;
    }

    public String getmUpdateCMD() {
        return mUpdateCMD;
    }

    public void setmUpdateCMD(String mUpdateCMD) {
        this.mUpdateCMD = mUpdateCMD;
    }

    public String getmAlertEmailIDs() {
        return mAlertEmailIDs;
    }

    public void setmAlertEmailIDs(String mAlertEmailIDs) {
        this.mAlertEmailIDs = mAlertEmailIDs;
    }

    public String getmReportEmailIDs() {
        return mReportEmailIDs;
    }

    public void setmReportEmailIDs(String mReportEmailIDs) {
        this.mReportEmailIDs = mReportEmailIDs;
    }

    public String getmLastAlarm() {
        return mLastAlarm;
    }

    public void setmLastAlarm(String mLastAlarm) {
        this.mLastAlarm = mLastAlarm;
    }

    public String getmLasTmpHMDtAlarm() {
        return mLasTmpHMDtAlarm;
    }

    public void setmLasTmpHMDtAlarm(String mLasTmpHMDtAlarm) {
        this.mLasTmpHMDtAlarm = mLasTmpHMDtAlarm;
    }

    public String getmTempUnit() {
        return mTempUnit;
    }

    public void setmTempUnit(String mTempUnit) {
        this.mTempUnit = mTempUnit;
    }

    public String getmPressureUnit() {
        return mPressureUnit;
    }

    public void setmPressureUnit(String mPressureUnit) {
        this.mPressureUnit = mPressureUnit;
    }

    public String getmAirFlowUnit() {
        return mAirFlowUnit;
    }

    public void setmAirFlowUnit(String mAirFlowUnit) {
        this.mAirFlowUnit = mAirFlowUnit;
    }

    public String getmId() {
        return mid;
    }

    public void setmId(String mid) {
        this.mid = mid;
    }

    public String getmRackBlowerCustomerID() {
        return mRackBlowerCustomerID;
    }

    public void setmRackBlowerCustomerID(String mRackBlowerCustomerID) {
        this.mRackBlowerCustomerID = mRackBlowerCustomerID;
    }
    public String getmRackBlowerCustomerName() {
        return mRackBlowerCustomerName;
    }

    public void setmRackBlowerCustomerName(String mRackBlowerCustomerName) {
        this.mRackBlowerCustomerName = mRackBlowerCustomerName;
    }

    public int getmIsCommModuleExist() {
        return mIsCommModuleExist;
    }

    public void setmIsCommModuleExist(int mIsCommModuleExist) {
        this.mIsCommModuleExist = mIsCommModuleExist;
    }

    public int getmIsUpdateCMD() {
        return mIsUpdateCMD;
    }

    public void setmIsUpdateCMD(int mIsUpdateCMD) {
        this.mIsUpdateCMD = mIsUpdateCMD;
    }

    public int getmIsUpdateCMDCompleted() {
        return mIsUpdateCMDCompleted;
    }

    public void setmIsUpdateCMDCompleted(int mIsUpdateCMDCompleted) {
        this.mIsUpdateCMDCompleted = mIsUpdateCMDCompleted;
    }

    public int getmUseAlnEmailService() {
        return mUseAlnEmailService;
    }

    public void setmUseAlnEmailService(int mUseAlnEmailService) {
        this.mUseAlnEmailService = mUseAlnEmailService;
    }

    public int getmIsRegAlarmOn() {
        return mIsRegAlarmOn;
    }

    public void setmIsRegAlarmOn(int mIsRegAlarmOn) {
        this.mIsRegAlarmOn = mIsRegAlarmOn;
    }

    public int getmIsTmpHMDAlarmOn() {
        return mIsTmpHMDAlarmOn;
    }

    public void setmIsTmpHMDAlarmOn(int mIsTmpHMDAlarmOn) {
        this.mIsTmpHMDAlarmOn = mIsTmpHMDAlarmOn;
    }

    public int getmIsUpdatedByWebApp() {
        return mIsUpdatedByWebApp;
    }

    public void setmIsUpdatedByWebApp(int mIsUpdatedByWebApp) {
        this.mIsUpdatedByWebApp = mIsUpdatedByWebApp;
    }

    public int getmModifiedBy() {
        return mModifiedBy;
    }

    public void setmModifiedBy(int mModifiedBy) {
        this.mModifiedBy = mModifiedBy;
    }

    public int getmIsDeleted() {
        return mIsDeleted;
    }

    public void setmIsDeleted(int mIsDeleted) {
        this.mIsDeleted = mIsDeleted;
    }

    public String getmLastSyncDateTime() {
        return mLastSyncDateTime;
    }

    public void setmLastSyncDateTime(String mLastSyncDateTime) {
        this.mLastSyncDateTime = mLastSyncDateTime;
    }

    public String getmModifiedOn() {
        return mModifiedOn;
    }

    public void setmModifiedOn(String mModifiedOn) {
        this.mModifiedOn = mModifiedOn;
    }

    public String getmCreatedOn() {
        return mCreatedOn;
    }

    public void setmCreatedOn(String mCreatedOn) {
        this.mCreatedOn = mCreatedOn;
    }

    public String getmCreatedBy() {
        return mCreatedBy;
    }

    public void setmCreatedBy(String mCreatedBy) {
        this.mCreatedBy = mCreatedBy;
    }


}
