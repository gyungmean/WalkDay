package com.ddw.andorid.walkday;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class WalkDBHelper extends SQLiteOpenHelper {

    final static String TAG = "WalkDBHelper";

    private final static String DB_NAME = "walk_db";
    public final static String TABLE_NAME = "walk_table";
    public final static String COL_ID = "_id";
    public final static String COL_DATE = "date";
    public final static String COL_PEOPLE = "people";
    public final static String COL_TIME = "time";
    public final static String COL_MEMO = "memo";

    public WalkDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + TABLE_NAME + " ( " + COL_ID + " integer primary key autoincrement, "
                + COL_DATE + " TEXT, " + COL_PEOPLE + " TEXT, " + COL_TIME +  " TEXT, "
                + COL_MEMO + " TEXT);";
        Log.d(TAG, sql);
        db.execSQL(sql);

//		샘플 데이터

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + TABLE_NAME);
        onCreate(db);
    }
}
