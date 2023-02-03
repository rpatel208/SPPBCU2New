package com.allentownblower.bluetooth;

public class DevicesObject {

    private int layoutType;
    private String deviceName,deviceAddress;
    private int IsPair; // 0 - none , 1 - pair , 2 - unpair

    public int getLayoutType() {
        return layoutType;
    }

    public void setLayoutType(int layoutType) {
        this.layoutType = layoutType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public int isPair() {
        return IsPair;
    }

    public void setPair(int pair) {
        IsPair = pair;
    }
}
