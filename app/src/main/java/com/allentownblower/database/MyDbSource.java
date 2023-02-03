package com.allentownblower.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;

import com.allentownblower.common.Utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by Mayur Yadav on 01 Sep 2019.
 */
public class MyDbSource {

    private static final String TAG = MyDbSource.class.getSimpleName();

    private String DATABASE_PATH;
    private static String DATABASE_NAME = "Allentown_Blower.db";
    public SQLiteDatabase db;

    public SqliteHelper dbHelper;

    public MyDbSource(Context ctx) {

        if (android.os.Build.VERSION.SDK_INT >= 4.2) {
            DATABASE_PATH = ctx.getApplicationInfo().dataDir + "/databases/" + DATABASE_NAME;
        } else {
            DATABASE_PATH = "/data/data/" + ctx.getPackageName() + "/databases/" + DATABASE_NAME;
        }

        try {
            dbHelper = new SqliteHelper(ctx);

            db = dbHelper.getWritableDatabase();
            if (db == null) {
                String path = DATABASE_PATH + DATABASE_NAME;
                db = SQLiteDatabase.openOrCreateDatabase(path, null);
            }
        } catch (Exception e) {
            Utility.Log(TAG,"ERROR ===> "+e.toString());
        }

    }

    public boolean insert(String tabName, ContentValues cv) {
        db = dbHelper.getWritableDatabase();
        long result = db.insert(tabName,null ,cv);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean update(String tabName, ContentValues cv) {
        db = dbHelper.getWritableDatabase();
        long result = db.update(tabName,cv,null,null);
        if(result == -1)
            return false;
        else
            return true;
    }

    public void delete(String tabName) {
        try {
            db = dbHelper.getWritableDatabase();
            db.delete(tabName, null, null);
        } catch (Exception e) {
            Utility.Log(TAG,"delete SQLiteException : " + e.toString());
        }
        //closeDB();
    }

    public boolean deleteRecord(String tabName, String strWhere) {
        try {
            db = dbHelper.getWritableDatabase();
            return db.delete(tabName, strWhere, null) > 0;
        } catch (Exception e) {
            Utility.Log(TAG,"delete SQLiteException : " + e.toString());
            return false;
        }
        //closeDB();
    }

    public void deleteWithWhere(String tabName, String strWhere) {
        db = dbHelper.getWritableDatabase();
        db.delete(tabName, strWhere, null);
        //closeDB();
    }

    public boolean closeDB() {
        if (db.isOpen())
            db.close();
        return true;
    }

    public Cursor getQueryResult(String strQuery) {
        //	Utility.Log(TAG,strQuery);
        return db.rawQuery(strQuery, null);
    }

    public int getQueryResultCount(String strQuery) {
        int nCount = 0;
        try {
            Cursor cursor = db.rawQuery(strQuery, null);
            nCount = cursor.getCount();
            cursor.close();
        } catch (Exception e) {
            nCount = 0;
        }
        return nCount;
    }

    public void executeSQL(String strQuery) {
        db = dbHelper.getWritableDatabase();
        db.execSQL(strQuery);
    }

    public void exportDB(Context ctx){
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source=null;
        FileChannel destination=null;
        String currentDBPath = "/data/"+ ctx.getPackageName() +"/databases/"+DATABASE_NAME;
        String backupDBPath = DATABASE_NAME;
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd.getAbsolutePath() +File.separator + "Android Blower", backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
        } catch(Exception e) {
            Utility.Log(TAG, e.getMessage());
            e.printStackTrace();
        }
    }
}
