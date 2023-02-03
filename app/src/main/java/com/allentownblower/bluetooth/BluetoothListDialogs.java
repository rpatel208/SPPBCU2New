package com.allentownblower.bluetooth;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.allentownblower.R;
import com.allentownblower.application.AllentownBlowerApplication;
import com.allentownblower.common.ObserverActionID;
import com.allentownblower.common.PrefManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothListDialogs {

    private Dialog mDialog;
    private static BluetoothAdapter mBluetoothAdapter;
    private BroadcastReceiver mDiscovery;
    private static BluetoothDevice mBluetoothDevice;
    private static CommunicationController mChatController;

    private RecyclerView rvDevicesList;
    private List<DevicesObject> devicesObjects = new ArrayList<>();
    private DevicesObject devicesObject;
    private RecyclerView.LayoutManager layoutManager;
    private DevicesListAdapter devicesListAdapter;

    private Set<BluetoothDevice> pairedDevices;
    private BluetoothDevice device;
    private IntentFilter filter;

    @SuppressLint("StaticFieldLeak")
    private static PrefManager prefManager;

    private int i = 0;

    public void show(Context context, boolean isBluetooth) {

        prefManager = new PrefManager(context);

        mDialog = new Dialog(context,android.R.style.Theme_Holo_NoActionBar);
        mDialog.setContentView(R.layout.layout_bluetooth);
        mDialog.setCancelable(true);

        devicesObjects.clear();

        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();

        rvDevicesList = mDialog.findViewById(R.id.rv_DevicesList);

        layoutManager = new LinearLayoutManager(context);
        rvDevicesList.setLayoutManager(layoutManager);
        rvDevicesList.setHasFixedSize(true);

        /*filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(mDiscovery, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(mDiscovery, filter);*/

        filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(mDiscovery, filter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = mBluetoothAdapter.getBondedDevices();

        devicesObject = new DevicesObject();
        devicesObject.setDeviceName("Recently Connected Devices");
        devicesObject.setDeviceAddress("");
        devicesObject.setLayoutType(1);
        devicesObject.setPair(0);

        devicesObjects.add(devicesObject);

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if(isBluetooth) {
                    if(!device.getName().startsWith("Allentown")) {
                        devicesObject = new DevicesObject();
                        devicesObject.setDeviceName(device.getName());
                        devicesObject.setDeviceAddress(device.getAddress());
                        devicesObject.setLayoutType(2);
                        devicesObject.setPair(1);

                        devicesObjects.add(devicesObject);
                    }
                } else {
                    if(device.getName().startsWith("Allentown")) {
                        devicesObject = new DevicesObject();
                        devicesObject.setDeviceName(device.getName());
                        devicesObject.setDeviceAddress(device.getAddress());
                        devicesObject.setLayoutType(2);
                        devicesObject.setPair(1);

                        devicesObjects.add(devicesObject);
                    }
                }
            }

            devicesObject = new DevicesObject();
            devicesObject.setDeviceName("New Search Devices");
            devicesObject.setDeviceAddress("");
            devicesObject.setLayoutType(1);
            devicesObject.setPair(0);

            devicesObjects.add(devicesObject);

        } else {
            devicesObject = new DevicesObject();
            devicesObject.setDeviceName("No devices have been paired");
            devicesObject.setDeviceAddress("");
            devicesObject.setLayoutType(3);
            devicesObject.setPair(0);

            devicesObjects.add(devicesObject);

            devicesObject = new DevicesObject();
            devicesObject.setDeviceName("New Search Devices");
            devicesObject.setDeviceAddress("");
            devicesObject.setLayoutType(1);
            devicesObject.setPair(0);

            devicesObjects.add(devicesObject);
        }

        devicesListAdapter = new DevicesListAdapter(context,devicesObjects);
        rvDevicesList.setAdapter(devicesListAdapter);

        mDialog.show();
    }

    public BluetoothListDialogs(BluetoothAdapter mBluetoothAdapter, CommunicationController chatController, final Boolean isBluetooth) {

        this.mBluetoothAdapter = mBluetoothAdapter;
        this.mChatController = chatController;

        mDiscovery = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device.getBondState() != BluetoothDevice.BOND_BONDED) {

                        if(isBluetooth) {
                            i = i + 1;
                            devicesObject = new DevicesObject();
                            if (device.getName() == null) {
                                devicesObject.setDeviceName("null");

                                devicesObject.setDeviceAddress(device.getAddress());
                                devicesObject.setLayoutType(2);
                                devicesObject.setPair(2);

                                devicesObjects.add(devicesObject);

                                devicesListAdapter.notifyDataSetChanged();
                            } else {
                                devicesObject.setDeviceName(device.getName());

                                if(!device.getName().startsWith("Allentown")) {
                                    devicesObject.setDeviceAddress(device.getAddress());
                                    devicesObject.setLayoutType(2);
                                    devicesObject.setPair(2);

                                    devicesObjects.add(devicesObject);

                                    devicesListAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            devicesObject = new DevicesObject();
                            if (device.getName() == null) {
                                /*devicesObject.setDeviceName("null");

                                devicesObject.setDeviceAddress(device.getAddress());
                                devicesObject.setLayoutType(2);
                                devicesObject.setPair(2);

                                devicesObjects.add(devicesObject);

                                devicesListAdapter.notifyDataSetChanged();*/
                            } else {
                                devicesObject.setDeviceName(device.getName());

                                if(device.getName().startsWith("Allentown")) {
                                    i = i + 1;

                                    devicesObject.setDeviceAddress(device.getAddress());
                                    devicesObject.setLayoutType(2);
                                    devicesObject.setPair(2);

                                    devicesObjects.add(devicesObject);

                                    devicesListAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    devicesObject = new DevicesObject();
                    devicesObject.setDeviceName("No devices found");
                    devicesObject.setDeviceAddress("");
                    devicesObject.setLayoutType(3);
                    devicesObject.setPair(0);

                    devicesObjects.add(devicesObject);

                    devicesListAdapter.notifyDataSetChanged();
                }
            }
        };
    }

    private static void connectToDevice(String deviceName, String deviceAddress, int isPair) {
        if(isPair == 2){
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(deviceAddress);
            if(deviceName.startsWith("Allentown")) {
                //create the bond.
                //NOTE: Requires API 17+? I think this is JellyBean
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    mBluetoothDevice.createBond();
                }
            } else {
                mBluetoothAdapter.cancelDiscovery();
                mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(deviceAddress);
                mChatController.connect(mBluetoothDevice);
            }
        } else if(isPair == 1){
            if(deviceName.startsWith("Allentown")){
                prefManager.setBluetoothMacAddress(deviceAddress);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nBluetoothOldDevices);
            } else {
                mBluetoothAdapter.cancelDiscovery();
                mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(deviceAddress);
                mChatController.connect(mBluetoothDevice);
            }
        }
    }

    public BluetoothDevice getBluetoothDevice() {
        return mBluetoothDevice;
    }

    public CommunicationController getChatController() {
        return mChatController;
    }

    public class DevicesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_HEADER = 1;
        private static final int TYPE_DETAIL = 2;
        private static final int TYPE_FINISH = 3;

        private Context context;
        private List<DevicesObject> devicesObjects;

        DevicesListAdapter(Context context, List<DevicesObject> devicesObjects) {
            super();
            this.devicesObjects = devicesObjects;
            this.context = context;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == TYPE_HEADER) {
                View view = LayoutInflater.from(context).inflate(R.layout.row_header_items, parent, false);
                return new HeaderViewHolder(view);
            } else if (viewType == TYPE_DETAIL) {
                View view = LayoutInflater.from(context).inflate(R.layout.row_detail_items, parent, false);
                return new DetailViewHolder(view);
            } else {
                View view = LayoutInflater.from(context).inflate(R.layout.row_finish_items, parent, false);
                return new FinishViewHolder(view);
            }
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof HeaderViewHolder) {
                final DevicesObject obj = devicesObjects.get(position);

                if (obj.getDeviceName().length() != 0) {
                    ((HeaderViewHolder) holder).txtTitle.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).txtTitle.setText(obj.getDeviceName());
                } else
                    ((HeaderViewHolder) holder).txtTitle.setText("");

            } else if (holder instanceof DetailViewHolder) {
                final DevicesObject obj = devicesObjects.get(position);

                if (obj.getDeviceName().length() != 0) {
                    ((DetailViewHolder) holder).txtName.setVisibility(View.VISIBLE);
                    ((DetailViewHolder) holder).txtName.setText(obj.getDeviceName());
                } else
                    ((DetailViewHolder) holder).txtName.setText("");

                if (obj.getDeviceAddress().length() != 0) {
                    ((DetailViewHolder) holder).txtAddress.setVisibility(View.VISIBLE);
                    ((DetailViewHolder) holder).txtAddress.setText(obj.getDeviceAddress());
                } else
                    ((DetailViewHolder) holder).txtAddress.setText("");

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mBluetoothAdapter.cancelDiscovery();
                        String name = obj.getDeviceName().trim();
                        String address = obj.getDeviceAddress().substring(obj.getDeviceAddress().length() - 17);
                        connectToDevice(name,address,obj.isPair());
                        mDialog.dismiss();
                    }
                });
            } else if (holder instanceof FinishViewHolder) {
                final DevicesObject obj = devicesObjects.get(position);

                if (obj.getDeviceName().length() != 0) {
                    ((FinishViewHolder) holder).txtTitle.setVisibility(View.VISIBLE);
                    ((FinishViewHolder) holder).txtTitle.setText(obj.getDeviceName());
                } else
                    ((FinishViewHolder) holder).txtTitle.setText("");

            }
        }

        @Override
        public int getItemCount() {
            return devicesObjects.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (getItem(position).getLayoutType() == 1)
                return TYPE_HEADER;
            else if (getItem(position).getLayoutType() == 2)
                return TYPE_DETAIL;
            else
                return TYPE_FINISH;
        }

        private DevicesObject getItem(int position) {
            return devicesObjects.get(position);
        }

        class HeaderViewHolder extends RecyclerView.ViewHolder {
            private TextView txtTitle;

            HeaderViewHolder(View itemView) {
                super(itemView);
                txtTitle = itemView.findViewById(R.id.txt_Row_Title);
            }
        }

        class DetailViewHolder extends RecyclerView.ViewHolder {
            private TextView txtName, txtAddress;

            DetailViewHolder(View itemView) {
                super(itemView);
                txtName = itemView.findViewById(R.id.txt_Row_Name);
                txtAddress = itemView.findViewById(R.id.txt_Row_Address);
            }
        }

        class FinishViewHolder extends RecyclerView.ViewHolder {
            private TextView txtTitle;

            FinishViewHolder(View itemView) {
                super(itemView);
                txtTitle = itemView.findViewById(R.id.txt_Row_Title);
            }
        }

    }

}