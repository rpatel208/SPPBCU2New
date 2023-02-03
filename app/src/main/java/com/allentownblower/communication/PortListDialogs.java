package com.allentownblower.communication;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.allentownblower.R;
import com.allentownblower.application.AllentownBlowerApplication;
import com.allentownblower.bluetooth.CommunicationController;
import com.allentownblower.common.CodeReUse;
import com.allentownblower.common.ObserverActionID;

import java.util.ArrayList;
import java.util.Set;

public class PortListDialogs {

    private Dialog mDialog;
    private ArrayAdapter<String> portDevicesAdapter;
    private ListView portDevicesList;

    public void show(final Context context, ArrayList<String> portList) {

        mDialog = new Dialog(context,android.R.style.Theme_Holo_NoActionBar);
        mDialog.setContentView(R.layout.layout_port);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);

        portDevicesAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =super.getView(position, convertView, parent);

                TextView textView=(TextView) view.findViewById(android.R.id.text1);
                textView.setTextColor(Color.WHITE);

                return view;
            }
        };

        portDevicesList = mDialog.findViewById(R.id.portDevicesList);
        portDevicesList.setAdapter(portDevicesAdapter);

        portDevicesAdapter.addAll(portList);

        portDevicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDialog.dismiss();
                CodeReUse.DefaultSerialPort = portDevicesAdapter.getItem(position);
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nReConnectNode);
            }

        });

        mDialog.show();
    }

}