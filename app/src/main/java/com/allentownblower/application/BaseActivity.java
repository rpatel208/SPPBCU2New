package com.allentownblower.application;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.allentownblower.bluetooth.Constants;
import com.allentownblower.common.CodeReUse;
import com.allentownblower.common.LogWatcher;
import com.allentownblower.common.ObserverActionID;
import com.allentownblower.common.PrefManager;
import com.allentownblower.common.Utility;
import com.allentownblower.communication.SerialPortConversion;

import org.tinylog.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Mayur Yadav on 01 Sep 2019.
 */
public abstract class BaseActivity extends Activity {

    private static final String TAG = "BaseActivity";

    private Activity act;
    private PrefManager prefManager;

    private SerialPortConversion portConversion;

    //interval between two services(Here Service run every 5 seconds)
    public static final int notify = CodeReUse.LogServiceTimerInterval;
    //run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    //timer handling
    private Timer mTimer = null;

    public static final int MULTIPLE_PERMISSIONS = 10;

    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    // Bluetooth Connection
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        act = this;

        prefManager = new PrefManager(act);
        portConversion = new SerialPortConversion(act);

        prefManager.setOpenNode(false);//added this line on 9/27/19

        Utility.Log("TAG", prefManager.getOpenNode() + " From BaseActivity.java");

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available!", Toast.LENGTH_SHORT).show();
            //finish();
        }

        if (ContextCompat.checkSelfPermission(act, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(act, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(act, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(act, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(act, permissions, MULTIPLE_PERMISSIONS);
            return;
        } else {
            //startLogWatcher(this); //ruei add

            if (mTimer != null) // Cancel if already existed
                mTimer.cancel();
            else
                mTimer = new Timer();   //recreate new
            mTimer.scheduleAtFixedRate(new TimeDisplay(), 0, notify);   //Schedule task

            try {
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, Constants.REQUEST_ENABLE_BLUETOOTH);
                }
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
        deleteOldReportFiles();

    }

    //ruei add
    private void startLogWatcher(Context mContext) {
        LogWatcher.getInstance().init(mContext).startWatch();
    }
    private void stopLogWatcher(Context mContext) {
        LogWatcher.getInstance().stopWatch();
    }
    private void checkLogFileSize(Context mContext){
        LogWatcher.getInstance().checkLogFileSize();
    }
    //ruei end

    //class TimeDisplay for handling task
    class TimeDisplay extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    writeLogCat();
                    //checkLogFileSize(act); //ruei add
                }
            });

        }
    }

    @SuppressLint("LongLogTag")
    protected void writeLogCat() {

        Log.e("***************************","writeLogCat Called");
        try {
            //File[] externalDirs = getApplicationContext().getExternalFilesDirs(null);
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + File.separator + "Allentown Blower");

            if (!dir.exists()) {
                dir.mkdirs();
            } else {
                String path = sdCard.getAbsolutePath() + File.separator + "Allentown Blower";
                //Log.d("Files", "Path: " + path);
                File directory = new File(path);
                File[] files = directory.listFiles();
                //Log.d("Files", "Size: "+ files.length);
                for (int i = 0; i < files.length; i++) {
                    //Log.d("Files", "FileName:" + files[i].getName());
                    Calendar time = Calendar.getInstance();
                    time.add(Calendar.DAY_OF_YEAR, -6);
                    //I store the required attributes here and delete them
                    Date lastModified = new Date(files[i].lastModified());
                    if (lastModified.before(time.getTime())) {
                        //file is older than a week
                        if (files[i].getName().contains("txt")) {
                            files[i].delete();
                        }
                    }
                }
            }

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
            Date date;
            String createon = null;
            try {
                date = formatter.parse(calendar.getTime().toString());
                createon = CodeReUse.format.format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }

            File file = new File(dir, createon + "_logfile.txt");

            if (file.exists()) {
                String cmd = "logcat -d *:E";
//                String cmd = "logcat *:E";
                Process process = Runtime.getRuntime().exec(cmd);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));


                StringBuilder log = new StringBuilder();
                String line;
                int i = 0;
                while ((line = bufferedReader.readLine()) != null) {
                    i++;
                    if (!line.contains("beginning of main")) {
                        log.append(line);
                        log.append("\n");
                        //process.waitFor();
                    }
                }

                //Convert log to string
                final String logString = new String(log.toString());
                if (!logString.equals("")) {
                    BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));
                    buf.append(logString);
//                    buf.append("************************************** i  = " + i);
                    buf.newLine();
                    Logger.info(buf);
                    buf.close();
                }
                Runtime.getRuntime().exec(new String[]{"logcat", "-c"});

            } else {
                CodeReUse.logcounter = 0;
                String cmd = "logcat -d *:E";
                Process process = Runtime.getRuntime().exec(cmd);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));


                StringBuilder log = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    log.append(line);
                    if (line.length() != 0) {
                        log.append("\n");
                        //process.waitFor();
                    }
                }


                //Convert log to string
                final String logString = new String(log.toString());

                //To write logcat in text file
                FileOutputStream fout = new FileOutputStream(file);
                OutputStreamWriter osw = new OutputStreamWriter(fout);

                //Writing the string to file
                osw.write(logString);
                osw.flush();
                osw.close();
                Runtime.getRuntime().exec(new String[]{"logcat", "-c"});
            }

//            try {
//                Runtime.getRuntime().exec(new String[]{"logcat", "-c"});
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("LongLogTag")
    protected void deleteOldReportFiles(){
        Log.e("***************************","deleteOldReportFiles Called");
        try {
            String sdCard = Environment.getExternalStorageDirectory().getAbsolutePath();
            File dir = new File(sdCard + File.separator + CodeReUse.folderName);
            if (dir.exists()){
                File[] files = dir.listFiles();
                //Log.d("Files", "Size: "+ files.length);
                for (int i = 0; i < files.length; i++) {
                    //Log.d("Files", "FileName:" + files[i].getName());
                    Calendar time = Calendar.getInstance();
                    time.add(Calendar.DAY_OF_YEAR, -6);
                    //I store the required attributes here and delete them
                    Date lastModified = new Date(files[i].lastModified());
                    if (lastModified.before(time.getTime())) {
                        //file is older than a week
                        if (files[i].getName().contains("csv")) {
                            files[i].delete();
                        }
                    }
                }
            }

        } catch (Exception e){
            Log.e("deleteOldReportFiles Exception", ""+ e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS:
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        && (grantResults[1] == PackageManager.PERMISSION_GRANTED)
                        && (grantResults[2] == PackageManager.PERMISSION_GRANTED)
                        && (grantResults[3] == PackageManager.PERMISSION_GRANTED)) {
                    if (mTimer != null) // Cancel if already existed
                        mTimer.cancel();
                    else
                        mTimer = new Timer();   //recreate new
                    mTimer.scheduleAtFixedRate(new TimeDisplay(), 0, notify);   //Schedule task

                    try {
                        if (!mBluetoothAdapter.isEnabled()) {
                            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableIntent, Constants.REQUEST_ENABLE_BLUETOOTH);
                        }
                    } catch (Exception e) {
                        e.getStackTrace();
                    }

                }
                return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Data) {
        // Check which request we're responding to
        if (requestCode == Constants.REQUEST_ENABLE_BLUETOOTH) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
            } else {
                Toast.makeText(act, "Restart Application", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*@Override
    protected void onPause() {
        super.onPause();
        if(prefManager.getDefaultLockScreen()) {
            ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.moveTaskToFront(getTaskId(), 0);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(prefManager.getDefaultLockScreen()) {
            if ((event.getKeyCode() == KeyEvent.KEYCODE_HOME)) {
                return false;
            }
            return false;
        } else {
            return false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(prefManager.getDefaultLockScreen()) {
            if ((keyCode== KeyEvent.KEYCODE_BACK)) {
                return false;
            }
            return false;
        } else {
            return false;
        }
    }

    @Override
    public void onAttachedToWindow() {
        if(prefManager.getDefaultLockScreen()) {
            super.onAttachedToWindow();
        }
    }*/

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onDestroy() {
        //prefManager.setOpenNode(false);
        Utility.Log(TAG, prefManager.getOpenNode() + "");

        if (prefManager.getOpenNode()) {
            portConversion.closeNode();
        }

        AllentownBlowerApplication.getInstance().getObserver().setValue(ObserverActionID.nStopService);

        try {
            mTimer.cancel();
        } catch (Exception e) {
            e.getStackTrace();
        }

        super.onDestroy();
    }

}