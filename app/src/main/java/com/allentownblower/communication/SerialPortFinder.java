package com.allentownblower.communication;

import android.util.Log;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Created by Mayur Yadav on 01 Sep 2019.
 */
public class SerialPortFinder {

    public class Driver {
        public Driver(String name, String root) {
            mDriverName = name;
            mDeviceRoot = root;
        }
        private String mDriverName;
        private String mDeviceRoot;
        Vector<File> mDevices = null;

        public Vector<File> getDevices() {
            if (mDevices == null) {
                mDevices = new Vector<File>();
                File dev = new File("/dev");
                File[] files = dev.listFiles();
                if(files != null) {
                    int i;
                    for (i = 0; i < files.length; i++) {
                        if (files[i].getAbsolutePath().startsWith(mDeviceRoot)) {
                            Log.d(TAG, "Found new device: " + files[i]);
                            mDevices.add(files[i]);
                        }
                    }
                }
            }
            return mDevices;
        }
        public String getName() {
            return mDriverName;
        }
    }

    private static final String TAG = "SerialPort";

    private Vector<Driver> mDrivers = null;

    Vector<Driver> getDrivers() throws IOException {
        if (mDrivers == null) {
            mDrivers = new Vector<Driver>();
            portList = new ArrayList<String>();
            LineNumberReader r = new LineNumberReader(new FileReader("/proc/tty/drivers"));
            String l;
            while((l = r.readLine()) != null) {
                String[] w = l.split(" +");
                if ((w.length == 5) && (w[4].equals("serial"))) {
                    Log.d(TAG, "Found new driver: " + w[1]);
                    portList.add(w[1]);
                    mDrivers.add(new Driver(w[0], w[1]));
                }
            }
            r.close();
        }
        return mDrivers;
    }

    private ArrayList<String> portList = null;

    ArrayList<String> getPortLists() throws IOException {
        if (portList == null) {
            portList = new ArrayList<String>();
            LineNumberReader r = new LineNumberReader(new FileReader("/proc/tty/drivers"));
            String l;
            while((l = r.readLine()) != null) {
                String[] w = l.split(" +");
                if ((w.length == 5) && (w[4].equals("serial"))) {
                    Log.d(TAG, "Found new driver: " + w[1]);
                    portList.add(w[1]);
                }
            }
            r.close();
        }
        return portList;
    }

    public List<String> getAllDevices() {
        List<String> devices = new Vector<String>();
        // Parse each driver
        Iterator<Driver> itdriv;
        try {
            itdriv = getDrivers().iterator();
            while(itdriv.hasNext()) {
                Driver driver = itdriv.next();
                Iterator<File> itdev = driver.getDevices().iterator();
                while(itdev.hasNext()) {
                    String device = itdev.next().getName();
                    String value = String.format("%s (%s)", device, driver.getName());
                    Log.d(TAG, value);
                    devices.add(value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return devices;
    }

    public List<String> getAllDevicesPath() {
        List<String> devices = new Vector<String>();
        // Parse each driver
        Iterator<Driver> itdriv;
        try {
            itdriv = getDrivers().iterator();
            while(itdriv.hasNext()) {
                Driver driver = itdriv.next();
                Iterator<File> itdev = driver.getDevices().iterator();
                while(itdev.hasNext()) {
                    String device = itdev.next().getAbsolutePath();
                    devices.add(device);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return devices;
    }
}

