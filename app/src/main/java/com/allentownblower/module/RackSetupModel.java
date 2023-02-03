package com.allentownblower.module;

public class RackSetupModel {


    private float ACsetpt, DCsetpt;
    private String ModelNo,Spc1, Spc2, Spc3, Spc4, SetupCompletedDateTime, SetupModifiedDateTime, CreatedOn, CompanyName, BlowerName, BuildingName, RoomName, BlowerAddress;
    private int Id, IsSetupCompleted;



    public RackSetupModel() {

    }

    public String getSpc1() {
        return Spc1;
    }

    public void setSpc1(String spc1) {
        Spc1 = spc1;
    }

    public String getSpc2() {
        return Spc2;
    }

    public void setSpc2(String spc2) {
        Spc2 = spc2;
    }

    public String getSpc3() {
        return Spc3;
    }

    public void setSpc3(String spc3) {
        Spc3 = spc3;
    }

    public String getSpc4() {
        return Spc4;
    }

    public void setSpc4(String spc4) {
        Spc4 = spc4;
    }

    public String getSetupCompletedDateTime() {
        return SetupCompletedDateTime;
    }

    public void setSetupCompletedDateTime(String setupCompletedDateTime) {
        SetupCompletedDateTime = setupCompletedDateTime;
    }

    public String getSetupModifiedDateTime() {
        return SetupModifiedDateTime;
    }

    public void setSetupModifiedDateTime(String setupModifiedDateTime) {
        SetupModifiedDateTime = setupModifiedDateTime;
    }

    public String getCreatedOn() {
        return CreatedOn;
    }

    public void setCreatedOn(String createdOn) {
        CreatedOn = createdOn;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }

    public String getBlowerName() {
        return BlowerName;
    }

    public void setBlowerName(String blowerName) {
        BlowerName = blowerName;
    }

    public String getBuildingName() {
        return BuildingName;
    }

    public void setBuildingName(String buildingName) {
        BuildingName = buildingName;
    }

    public String getRoomName() {
        return RoomName;
    }

    public void setRoomName(String roomName) {
        RoomName = roomName;
    }

    public String getBlowerAddress() {
        return BlowerAddress;
    }

    public void setBlowerAddress(String blowerAddress) {
        BlowerAddress = blowerAddress;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getIsSetupCompleted() {
        return IsSetupCompleted;
    }

    public void setIsSetupCompleted(int isSetupCompleted) {
        IsSetupCompleted = isSetupCompleted;
    }

    public String getModelNo() {
        return ModelNo;
    }

    public void setModelNo(String modelNo) {
        ModelNo = modelNo;
    }

    public float getACsetpt() {
        return ACsetpt;
    }

    public void setACsetpt(float ACsetpt) {
        this.ACsetpt = ACsetpt;
    }

    public float getDCsetpt() {
        return DCsetpt;
    }

    public void setDCsetpt(float DCsetpt) {
        this.DCsetpt = DCsetpt;
    }
}
