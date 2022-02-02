package com.ddw.andorid.ma01_20190941;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class WalkDayDBHelper extends SQLiteOpenHelper {

    final static String TAG = "WalkDayDBHelper";

    private final static String DB_NAME = "walkday_db";

    public final static String TABLE_WALK = "walk_table";
    public final static String TABLE_DOG = "dog_table";
    public final static String TABLE_WALK_DOG = "walk_dog_table";
    public final static String TABLE_MAPS = "maps_table";

    public final static String COL_ID = "_id";

    /*TABLE_WALK*/
    public final static String COL_DATE = "date";
    public final static String COL_PEOPLE = "people";
    public final static String COL_DISTANCE = "distance";
    public final static String COL_TIME = "time";
    public final static String COL_MEMO = "memo";

    /*TABLE_DOG*/
    public final static String COL_NAME = "name";
    public final static String COL_BIRTHY = "birth_y";
    public final static String COL_BIRTHM = "birth_m";
    public final static String COL_BIRTHD = "birth_d";
    public final static String COL_WEIGHT = "weight";
    public final static String COL_TYPE = "type";
    public final static String COL_GENDER = "gender";
    public final static String COL_PATH = "path"; //image

    /*walk_dog db와 maps db 모두 사용되는 컬럼*/
    public final static String COL_WALKID = "walk_id";

    /*TABLE_WALK_DOG*/
    public final static String COL_DOGID = "dog_id";

    /*TABLE_MAPS*/;
    public final static String COL_LAT = "lat";
    public final static String COL_LNG = "lng";

    public WalkDayDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql_walk = "create table " + TABLE_WALK + " ( " + COL_ID + " integer primary key autoincrement, "
                + COL_DATE + " TEXT, " + COL_PEOPLE + " TEXT, " + COL_DISTANCE + " TEXT, "
                + COL_TIME +  " TEXT, " + COL_MEMO + " TEXT);";
        Log.d(TAG, sql_walk);
        db.execSQL(sql_walk);

        String sql_dog = "create table " + TABLE_DOG + " ( " + COL_ID + " integer primary key autoincrement, "
                + COL_NAME + " TEXT, " + COL_BIRTHY + " TEXT, " + COL_BIRTHM + " TEXT, " + COL_BIRTHD +  " TEXT, " + COL_WEIGHT + " FLOAT, "
                + COL_TYPE + " TEXT, " + COL_GENDER + " INTEGER, "+ COL_PATH + " TEXT);";
        Log.d(TAG, sql_dog);
        db.execSQL(sql_dog);

        String sql_walk_dogs = "create table " + TABLE_WALK_DOG + " ( " + COL_ID + " integer primary key autoincrement, "
                + COL_WALKID + " INTEGER, " + COL_DOGID + " INTEGER);";
        Log.d(TAG, sql_walk_dogs);
        db.execSQL(sql_walk_dogs);

        String sql_maps = "create table " + TABLE_MAPS + " ( " + COL_ID + " integer primary key autoincrement, "
                + COL_WALKID + " INTEGER, " + COL_LAT + " FLOAT, " + COL_LNG + " FLOAT);";
        Log.d(TAG, sql_maps);
        db.execSQL(sql_maps);

//		샘플 데이터

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + TABLE_WALK);
        db.execSQL("drop table " + TABLE_DOG);
        db.execSQL("drop table " + TABLE_WALK_DOG);
        db.execSQL("drop table " + TABLE_MAPS);
        onCreate(db);
    }
}
