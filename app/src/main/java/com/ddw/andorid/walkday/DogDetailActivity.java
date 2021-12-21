package com.ddw.andorid.walkday;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

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

                        File file = new File(dir, filename);
                        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                        imModiDog.setImageBitmap(bitmap);
                    } catch (Exception e){
                        Log.d(TAG, "Image set error!");
                    }

                }while(cursor.moveToNext());

        }
        cursor.close();
        db.close();
    }

    public void onClick(View v){
        switch (v.getId()) {
            case R.id.btnDogModify:
                //dog db 수정
                break;
        }
    }
}
