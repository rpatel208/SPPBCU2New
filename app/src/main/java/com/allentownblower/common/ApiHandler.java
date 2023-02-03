package com.allentownblower.common;

public class ApiHandler {

    /*Production Domain Address*/
    private static String strDomain = "/WiComSensusWebAPI";

    /*GetNewIDForRackBlowerUrl with All param*/
    public static String strUrlGetNewIDForRackBlower = strDomain + "/RackBlower/GetNewIDForRackBlower";
    public static String strUrlGetUpdateRackBlowerDetails = strDomain + "/RackBlower/GetRackBlowerDetails";
    public static String strUrlInsertRackBlowerSetPointData = strDomain + "/RackBlower/InsertRackBlowerSetPointData";
    public static String strUrlInsertRackBlowerFeedbackData = strDomain + "/RackBlower/InsertRackBlowerFeedbackData";
    public static String strUrlUpdatedByWebAppCompleted = strDomain + "/RackBlower/UpdatedByWebAppCompleted";
    public static String strUrlUpdateCommandCompleted = strDomain + "/RackBlower/UpdateCommandCompleted";
    public static String strUrlSendReportEmail = strDomain + "/RackBlower/SendReportEmail";
    public static String strURLResetFAndSData = strDomain + "/RackBlower/ResetRackBlowerFAndSData";
    public static String strUrlUpdateRackBlowerNumber = strDomain + "/RackBlower/UpdateRackModelNumber";
    public static String strUrlSendReportFileToEmail = strDomain + "/api/download/SendReportFileToEmail";

    public static String strUrlGetAPKVersion = strDomain + "/RackBlower/GetAndroidAPKVersion";

    /*Parameter*/
    public static String strGetNewIDForRackBlowerSerialNumber = "SerialNumber";
    public static String strGetNewIDForRackBlowerCustomerName = "Customer";


    /*Parameter For Send Email*/
    public static String strGetSendEmailFromDate = "FromDate";
    public static String strGetSendEmailToDate = "ToDate";
    public static String strGetSendEmailAvgData = "AvgData";
    public static String strGetSendEmailAllData = "AllData";
    public static String strGetSendEmail_EmailIDs = "EmailIDs";

    /*Parameter GetUpdateRackBlowerDetails*/
    public static String strUpdateRackBlowerDetailsId = "RackBlowerDetailsID";
    public static String strUpdateRackBlowerCustomerID = "RackBlowerCustomerID";
    public static String strUpdatecompletedCMD = "completedCMD";

    public static String strRackSerialNumberId = "RackBlowerDetailsID";
    public static String strRackBlowerCustomerID = "RackBlowerCustomerID";
    public static String strRackBlowerABlowerSerial = "ABlowerSerial";

    /*Parameter UpdateRackBlowerDetails*/
    public static String strUpdatedRackBlowerABlowerName = "ABlowerName";
    public static String strUpdatedRackBlowerABlowerBuilding = "ABlowerBuilding";
    public static String strUpdatedRackBlowerABlowerRoom = "ABlowerRoom";
    public static String strUpdatedRackBlowerRackModel = "RackModel";

    //string SerialNumber, string Customer, string ABlowerWiFiMAC, string ABlowerLANMAC, string ABlowerBluetoothMAC, string ABlowerWiFiIPAddress, string ABlowerLANIPAddress)


    public static String strUpdateRackCustomerID = "RackBlowerCustomerID";
    public static String strUpdateRackBlowerDetailsID = "RackBlowerDetailsID";
    public static String strUpdateABlowerSerial = "ABlowerSerial";
    public static String strUpdateABlowerWifiMac = "ABlowerWiFiMAC";
    public static String strUpdateABlowerLANMac = "ABlowerLANMAC";
    public static String strUpdateABlowerBluetoothMac = "ABlowerBluetoothMAC";
    public static String strUpdateABlowerLANIPAddress = "ABlowerLANIPAddress";
    public static String strUpdateABlowerWiFiIPAddress = "ABlowerWiFiIPAddress";
    public static String strUpdateABlowerName = "ABlowerName";
    public static String strUpdateABlowerBuilding = "ABlowerBuilding";
    public static String strUpdateABlowerRoom = "ABlowerRoom";
    public static String strUpdateRackModel = "RackModel";
    public static String strUpdateRackSerial = "RackSerial";
    public static String strUpdateBlowerModel = "BlowerModel";
    public static String strUpdateSupplyBlowerSerial = "SupplyBlowerSerial";
    public static String strUpdateExhaustBlowerSerial = "ExhaustBlowerSerial";
    public static String strUpdateIsCommoduleExit = "IsCommModuleExist";
    public static String strUpdateAwiviWifiMac = "AWiViWiFiMAC";
    public static String strUpdateAwiviWifiIpAddress = "AWiViIPAddress";
    public static String strUpdateAwiviBluetoothMac = "AWiViBluetoothMAC";
    public static String strUpdateIsUpdateCmd = "IsUpdateCMD";
    public static String strUpdateCmd = "UpdateCMD";
    public static String strUpdateIsUpdateCmdCompleted = "IsUpdateCMDCompleted";
    public static String strUpdateUseAlnEmailService = "UseAlnEmailService";
    public static String strUpdateAlertEmailIds = "AlertEmailIDs";
    public static String strUpdateReportEmailIds = "ReportEmailIDs";
    public static String strUpdateIsRegAlarmOn = "IsRegAlarmOn";
    public static String strUpdateLastAlarm = "LastAlarm";
    public static String strUpdateIsTmpHMDAlarmOn = "IsTmpHMDAlarmOn";
    public static String strUpdateLasTmpHMDtAlarm = "LasTmpHMDtAlarm";
    public static String strUpdateTempUnit = "TempUnit";
    public static String strUpdatePressureUnit = "PressureUnit";
    public static String strUpdateAirFlowUnit = "AirFlowUnit";
    public static String strUpdateIsUpdatedByWebApp = "IsUpdatedByWebApp";

    public static String strParamS01 = "S01";
    public static String strParamS02 = "S02";
    public static String strParamS03 = "S03";
    public static String strParamS04 = "S04";
    public static String strParamS05 = "S05";
    public static String strParamS06 = "S06";
    public static String strParamS07 = "S07";
    public static String strParamS08 = "S08";
    public static String strParamS09 = "S09";
    public static String strParamS10 = "S10";
    public static String strParamS11 = "S11";
    public static String strParamS12 = "S12";
    public static String strParamS13 = "S13";
    public static String strParamS14 = "S14";
    public static String strParamS15 = "S15";
    public static String strParamS16 = "S16";
    public static String strParamS17 = "S17";
    public static String strParamS18 = "S18";
    public static String strParamS19 = "S19";
    public static String strParamS20 = "S20";
    public static String strParamS21 = "S21";
    public static String strParamS22 = "S22";
    public static String strParamS23 = "S23";
    public static String strParamS24 = "S24";
    public static String strParamS25 = "S25";
    public static String strParamS26 = "S26";
    public static String strParamS27 = "S27";

    public static String strParamF01 = "F01";
    public static String strParamF02 = "F02";
    public static String strParamF03 = "F03";
    public static String strParamF04 = "F04";
    public static String strParamF05 = "F05";
    public static String strParamF06 = "F06";
    public static String strParamF07 = "F07";
    public static String strParamF08 = "F08";
    public static String strParamF09 = "F09";
    public static String strParamF10 = "F10";
    public static String strParamF11 = "F11";
    public static String strParamF12 = "F12";
    public static String strParamF13 = "F13";
    public static String strParamF14 = "F14";
    public static String strParamF15 = "F15";
    public static String strParamF16 = "F16";
    public static String strParamF17 = "F17";

}
