package com.ddw.andorid.ma01_20190941;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;

public class DogDetailActivity extends Activity {
    final static String TAG = "DogDetailActivity";

    String id;

    EditText etModiName;
    EditText etModiBirthY;
    EditText etModiBirthM;
    EditText etModiBirthD;
    EditText etModiWeight;
    EditText etModiType;
    CheckBox cbModiWo;
    CheckBox chModiMa;
    CheckBox chModiNone;

    ImageView imModiDog;

    DogDBHelper helper;
    SQLiteDatabase db;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Log.d(TAG, "DogDetailActivity start!");
        helper = new DogDBHelper(getApplicationContext());
        Intent intent = getIntent();

        id = intent.getStringExtra("id").toString();
        Log.d(TAG, "id: " + id);

        etModiName = (EditText) findViewById(R.id.etModiName);
        etModiBirthY = (EditText) findViewById(R.id.etModiBirthY);
        etModiBirthM = (EditText) findViewById(R.id.etModiBirthM);
        etModiBirthD = (EditText) findViewById(R.id.etModiBirthD);
        etModiWeight = (EditText) findViewById(R.id.etModiWeight);
        etModiType = (EditText) findViewById(R.id.etModiType);
        cbModiWo = (CheckBox) findViewById(R.id.cbModiWo);
        chModiMa = (CheckBox) findViewById(R.id.chModiMa);
        chModiNone = (CheckBox) findViewById(R.id.chModiNone);
        imModiDog = (ImageView) findViewById(R.id.imModiDog);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "DogDetailActivity onResume");
        String selection = "_id=?";
        String[] selectionArgs = {id};
        db = helper.getWritableDatabase();
        cursor = db.query(DogDBHelper.TABLE_NAME, null, selection, selectionArgs,
                null, null, null, null);

        if(cursor.moveToNext()){
                do{
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String birthY = cursor.getString(cursor.getColumnIndex("birth_y"));
                    String birthM = cursor.getString(cursor.getColumnIndex("birth_m"));
                    String birthD = cursor.getString(cursor.getColumnIndex("birth_d"));
                    float weight = cursor.getFloat(cursor.getColumnIndex("weight"));
                    String type = cursor.getString(cursor.getColumnIndex("type"));
                    int gender = cursor.getInt(cursor.getColumnIndex("gender"));
                    String path = cursor.getString(cursor.getColumnIndex("path"));
                    Log.d(TAG, "name: " + name);

                    etModiName.setText(name);
                    etModiBirthY.setText(birthY);
                    etModiBirthM.setText(birthM);
                    etModiBirthD.setText(birthD);
                    etModiWeight.setText(Float.toString(weight));
                    etModiType.setText(type);

                    if(gender / 100 == 1){
                        cbModiWo.setChecked(true);
                    }
                    if(gender % 100 / 10 == 1){
                        chModiMa.setChecked(true);
                    }
                    if(gender % 100 % 10 / 1 == 1){
                        chModiNone.setChecked(true);
                    }

                    try{
                        File dir = new File(getFilesDir() + "/dogImage");
                        String filename = cursor.getString(cursor.getColumnIndex("path"));
                        Log.d(TAG, "file name : " + filename);
                        File file = new File(dir, filename);
                        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                        imModiDog.setImageBitmap(bitmap);
                    } catch (Exception e){
                        Log.d(TAG, "Image set error!");
                    }

                }while(cursor.moveToNext());

        }else{
            Log.d(TAG, "cursor error");
        }
        cursor.close();
        db.close();
    }

    public void onClick(View v){
        String whereClause;
        String[] whereArgs;
        switch (v.getId()) {
            case R.id.btnDogDel:
                //dog 삭제
                whereClause = "_id=?";
                whereArgs = new String[] {id};
                db = helper.getWritableDatabase();
                db.delete(DogDBHelper.TABLE_NAME, whereClause, whereArgs);

                Toast.makeText(getApplicationContext(), "삭제완료", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Delete");
                finish();
                break;
            case R.id.btnDogModify:
                //dog db 수정
                ContentValues row = new ContentValues();
                row.put(DogDBHelper.COL_NAME, etModiName.getText().toString());
                row.put(DogDBHelper.COL_BIRTHY, etModiBirthY.getText().toString());
                row.put(DogDBHelper.COL_BIRTHM, etModiBirthM.getText().toString());
                row.put(DogDBHelper.COL_BIRTHD, etModiBirthD.getText().toString());
                row.put(DogDBHelper.COL_WEIGHT, etModiWeight.getText().toString());
                row.put(DogDBHelper.COL_TYPE, etModiType.getText().toString());

                int temp = 0;
                if(cbModiWo.isChecked()) {
                    temp += 100;
                }
                if(chModiMa.isChecked()) {
                    temp += 10;
                }
                if(chModiNone.isChecked()){
                    temp += 1;
                }

                row.put(DogDBHelper.COL_GENDER, temp);

                whereClause = "_id=?";
                whereArgs = new String[] {id};

                db = helper.getWritableDatabase();
                db.update(DogDBHelper.TABLE_NAME, row, whereClause, whereArgs);

                Toast.makeText(getApplicationContext(), "수정완료", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Modify");
                break;
        }
        db.close();
    }
}
