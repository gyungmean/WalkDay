package com.ddw.andorid.ma01_20190941;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MapsDBHelper extends SQLiteOpenHelper {

    final static String TAG = "MapsDBHelper";

    private final static String DB_NAME = "walkday_db";
    public final static String TABLE_NAME = "maps_table";
    public final static String COL_ID = "_id";
    public final static String COL_WALKID = "walkId";
    public final static String COL_LAT = "lat";
    public final static String COL_LNG = "lng";


    public MapsDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + TABLE_NAME + " ( " + COL_ID + " integer primary key autoincrement, "
                + COL_WALKID + " INTEGER, " + COL_LAT + " DOUBLE, " + COL_LNG + " DOUBLE);";
        Log.d(TAG, sql);
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + TABLE_NAME);
        onCreate(db);
    }
}
