package com.ddw.andorid.walkday;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WalkDBHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "walk_db";
    public final static String TABLE_NAME = "walk_table";
    public final static String COL_ID = "_id";
    public final static String COL_DATE = "date";
    public final static String COL_DIS = "distance";
    public final static String COL_TIME = "time";

    public final static String COL_NAME = "name";

    public WalkDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " ( " + COL_ID + " integer primary key autoincrement,"
                + COL_DATE + " TEXT, " + COL_DIS + " TEXT, " + COL_TIME + " TEXT, "+ COL_KCAL + " TEXT, " + COL_NAME + " TEXT);");

//		샘플 데이터
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '21년/09월/15일', '500', '20', '10', '김경민');");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '21년/09월/17일', '990', '35','19', '김현민');");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '21년/09월/20일', '1200', '60','22', '김지민');");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '21년/09월/21일', '500', '25','10', '김지민');");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '21년/09월/22일', '750', '25', '37', '김지민');");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '21년/09월/23일', '1500', '75','30', '김경민');");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '21년/09월/24일', '800', '40','17', '김경민');");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '21년/09월/25일', '1000', '55','20', '김현민');");
        
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + TABLE_NAME);
        onCreate(db);
    }
}
