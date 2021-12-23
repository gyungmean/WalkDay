package com.ddw.andorid.walkday;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DogDBHelper extends SQLiteOpenHelper {

    final static String TAG = "DogDBHelper";

    private final static String DB_NAME = "walkday_db";
    public final static String TABLE_NAME = "dog_table";
    public final static String COL_ID = "_id";
    public final static String COL_NAME = "name";
    public final static String COL_BIRTHY = "birth_y";
    public final static String COL_BIRTHM = "birth_m";
    public final static String COL_BIRTHD = "birth_d";
    public final static String COL_WEIGHT = "weight";
    public final static String COL_TYPE = "type";
    public final static String COL_GENDER = "gender";
    public final static String COL_PATH = "path";

    public DogDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + TABLE_NAME + " ( " + COL_ID + " integer primary key autoincrement, "
                + COL_NAME + " TEXT, " + COL_BIRTHY + " TEXT, " + COL_BIRTHM + " TEXT, " + COL_BIRTHD +  " TEXT, " + COL_WEIGHT + " FLOAT, "
                + COL_TYPE + " TEXT, " + COL_GENDER + " INTEGER, "+ COL_PATH + " TEXT);";
        Log.d(TAG, sql);
        db.execSQL(sql);

        /*샘플 데이터 추가*/
//        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '쫑', '2018', '05', '10', 3.8, '말티즈', 100, null);");
//        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '쫑2', '2021', '07', '10', 2.4, '말티즈', 100, null);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + TABLE_NAME);
        onCreate(db);
    }
}
