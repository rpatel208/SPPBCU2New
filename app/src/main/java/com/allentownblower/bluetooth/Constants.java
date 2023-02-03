package com.allentownblower.bluetooth;

public class Constants {
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_OBJECT = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_ERROR = 6;
    public static final int MESSAGE_LOST = 7;

    public static final String DEVICE_OBJECT = "device_name";
    public static final int REQUEST_ENABLE_BLUETOOTH = 1;

    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    // Client Side
    // #defines for identifying shared types between calling functions
    public static final int REQUEST_ENABLE_BT_Client = 8; // used to identify adding bluetooth names
    public static final int MESSAGE_READ_Client = 9; // used in bluetooth handler to identify message update
    public static final int MESSAGE_WRITE_Client = 10; // used in bluetooth handler to identify message update
    public static final int CONNECTING_STATUS_Client = 11; // used in bluetooth handler to identify message status


}
