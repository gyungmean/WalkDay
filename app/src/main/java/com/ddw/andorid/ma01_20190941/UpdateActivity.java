package com.ddw.andorid.ma01_20190941;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

public class UpdateActivity extends Activity {

    static final String TAG = "UpdateActivity";

    long id;

    EditText etDistance;
    EditText etTime;
    EditText etKcal;
    EditText etName;

    Cursor cursor;
    WalkDBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "UpdateActivity");

    }

    @Override
    protected void onResume() {
//        super.onResume();
//
//        Intent intent = getIntent();
//        id = intent.getLongExtra("ID", 0);
//        helper = new WalkDBHelper(this);
//        SQLiteDatabase db = helper.getWritableDatabase();
//        cursor = db.rawQuery("select * from " + WalkDBHelper.TABLE_NAME, null);
//        cursor.moveToPosition((int)id - 1);
//
//        Log.d(TAG, "cursor : " + id);
//
//        TextView tvDate = findViewById(R.id.tvWalkDate);
//        etDistance = findViewById(R.id.etDistance);
//        etTime = findViewById(R.id.etTime);
//        etKcal = findViewById(R.id.etKcal);
//        etName = findViewById(R.id.etName);
//
//        tvDate.setText(cursor.getString(cursor.getColumnIndex(WalkDBHelper.COL_DATE)));
//        etDistance.setText(cursor.getString(cursor.getColumnIndex(WalkDBHelper.COL_DIS)));
//        etTime.setText(cursor.getString(cursor.getColumnIndex(WalkDBHelper.COL_TIME)));
//        etKcal.setText(cursor.getString(cursor.getColumnIndex(WalkDBHelper.COL_KCAL)));
//        etName.setText(cursor.getString(cursor.getColumnIndex(WalkDBHelper.COL_NAME)));
    }
//
//    public void onClick(View v) {
//        Log.d(TAG, "onClick");
//        switch(v.getId()) {
//            case R.id.btnUpdateWalk:
////                DB 데이터 업데이트 작업 수행
//                SQLiteDatabase db = helper.getWritableDatabase();
//                ContentValues row = new ContentValues();
//
//                row.put(helper.COL_DIS, etDistance.getText().toString());
//                row.put(helper.COL_TIME, etTime.getText().toString());
//                row.put(helper.COL_KCAL, etKcal.getText().toString());
//                row.put(helper.COL_NAME, etName.getText().toString());
//
//                String whereClause = "_id=?";
//                String[] whereArgs = new String[] { String.valueOf(id) };
//                db.update(helper.TABLE_NAME, row, whereClause, whereArgs);
//                Log.d(TAG, "updateDB : " + cursor.getString(cursor.getColumnIndex(WalkDBHelper.COL_DIS)));
//                helper.close();
//                Log.d(TAG, "helperClose");
//
//                setResult(RESULT_OK);
//                finish();
//                break;
//
//            case R.id.btnUpdateWalkClose:
////                DB 데이터 업데이트 작업 취소
//                setResult(RESULT_CANCELED);
//                finish();
//                break;
//        }
//    }
}
