package com.allentownblower.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import static com.allentownblower.bluetooth.Constants.STATE_CONNECTED;

public class CommunicationController {

    private int mState;
    private Handler mHandler;
    private Accept mAcceptThread;
    private Connect mConnectThread;
    private ReadWrite mConnectedThread;
    private BluetoothAdapter mBluetoothAdapter;
    private static final UUID MY_UUID = UUID.fromString("4800fe5d-f6f1-4aab-9cd6-7aebd87eb306");

    public CommunicationController(Context context, Handler handler) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = Constants.STATE_NONE;
        this.mHandler = handler;
    }

    // Set the current state of the chat connection
    private void setState(int state) {
        this.mState = state;
        mHandler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    public int getState() {
        return mState;
    }

    public void start() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setState(Constants.STATE_LISTEN);
        if (mAcceptThread == null) {
            mAcceptThread = new Accept();
            mAcceptThread.start();
        }
    }

    public synchronized void connect(BluetoothDevice device) {

        if (mState == Constants.STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectThread = new Connect(device);
        mConnectThread.start();
        setState(Constants.STATE_CONNECTING);
    }

    public void connected(BluetoothDevice device) {

        Log.d("Make image", device.toString());

        if (mState == Constants.STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectThread = new Connect(device);
        mConnectThread.start();
        setState(Constants.STATE_CONNECTING);
    }

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        mConnectedThread = new ReadWrite(socket);
        mConnectedThread.start();

        Message msg = mHandler.obtainMessage(Constants.MESSAGE_DEVICE_OBJECT);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.DEVICE_OBJECT, device);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        setState(STATE_CONNECTED);
    }


    public void stop() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
        setState(Constants.STATE_NONE);
    }

    public void write(byte[] out) {
        ReadWrite r;
        synchronized (this) {
            if (mState != STATE_CONNECTED)
                return;
            r = mConnectedThread;
        }
        r.write(out);
    }

    public void connectionFailed() {
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString("toast", "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        CommunicationController.this.start();
    }

    public void connectionLost() {
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString("toast", "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        CommunicationController.this.start();
    }

    public class Accept extends Thread {
        private BluetoothServerSocket mServerSocket;

        public Accept() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("chat-chat", MY_UUID);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mServerSocket = tmp;
        }

        public void run() {
            setName("AcceptThread");
            BluetoothSocket socket;
            while (mState != STATE_CONNECTED) {
                try {
                    socket = mServerSocket.accept();
                } catch (Exception e) {
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (CommunicationController.this) {
                        switch (mState) {
                            case Constants.STATE_LISTEN:
                            case Constants.STATE_CONNECTING:
                                connected(socket, socket.getRemoteDevice());
                                break;
                            case Constants.STATE_NONE:
                            case STATE_CONNECTED:
                                try {
                                    socket.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                }
            }
        }

        public void cancel() {
            try {
                mServerSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class Connect extends Thread {
        private BluetoothSocket mSocket;
        private BluetoothDevice mDevice;

        public Connect(BluetoothDevice device) {
            this.mDevice = device;
            BluetoothSocket tmp = null;
            try {
                tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mSocket = tmp;
        }

        public void run() {
            setName("ConnectThread");
            mBluetoothAdapter.cancelDiscovery();

            try {
                mSocket.connect();
            } catch (Exception e) {
                try {
                    mSocket.close();
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
                connectionFailed();
                return;
            }

            synchronized (CommunicationController.this) {
                mConnectThread = null;
            }

            connected(mSocket, mDevice);
        }

        public void cancel() {
            try {
                mSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class ReadWrite extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public ReadWrite(BluetoothSocket socket) {
            this.bluetoothSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (Exception e) {
                e.printStackTrace();
            }

            inputStream = tmpIn;
            outputStream = tmpOut;
        }


        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            while (true) {
                try {
                    bytes = inputStream.read(buffer);
                    mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                } catch (Exception e) {
                    connectionLost();
                    CommunicationController.this.start();
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                outputStream.write(buffer);
                mHandler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
                outputStream.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
