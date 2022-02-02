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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class DetailWalkActivity extends Activity {
    final static String TAG = "DetailActivity";
    String id;

    ArrayList<DogDTO> dogs = new ArrayList<>();

    WalkDogAdapter walkDogAdapter;

    TextView tvDetailDate;
    RecyclerView lvDetailDog;
    EditText etdetailPeople;
    EditText etdetailDistance;
    EditText etdetailTime;
    EditText etdetailMemo;

    WalkDayDBHelper helper;
    SQLiteDatabase db;
    Cursor cursor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_detail);
        Log.d(TAG, "DetailWalkActivity start!");
        helper = new WalkDayDBHelper(getApplicationContext());
        Intent intent = getIntent();

        id = intent.getStringExtra("id").toString();
        Log.d(TAG, "id: " + id);

        tvDetailDate = (TextView) findViewById(R.id.tvDetailDate);
        etdetailPeople = (EditText) findViewById(R.id.etdetailPeople);
        etdetailDistance = (EditText) findViewById(R.id.etdetailDistance);
        etdetailTime = (EditText) findViewById(R.id.etdetailTime);
        etdetailMemo = (EditText) findViewById(R.id.etdetailMemo);

        lvDetailDog = (RecyclerView) findViewById(R.id.lvDetailDog);

        helper = new WalkDayDBHelper(getApplicationContext());

        String[] columns = {"_id", "name"};
        db = helper.getReadableDatabase();
        cursor = db.query(helper.TABLE_DOG, columns, null, null,
                null, null, null, null);
        dogs.clear();
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    DogDTO dog = new DogDTO();
                    dog.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                    dog.setName(cursor.getString(cursor.getColumnIndex("name")));

                    Log.d(TAG, "dog name: " + dog.getName());
                    dogs.add(dog);
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();

        if (dogs.size() != 0) {
            walkDogAdapter = new WalkDogAdapter(getApplicationContext(), dogs);
            lvDetailDog.setAdapter(walkDogAdapter);
            lvDetailDog.setLayoutManager(new LinearLayoutManager(this));
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "DogDetailActivity onResume");
        String selection = "_id=?";
        String[] selectionArgs = {id};
        db = helper.getWritableDatabase();
        cursor = db.query(WalkDayDBHelper.TABLE_WALK, null, selection, selectionArgs,
                null, null, null, null);

        if(cursor.moveToNext()){
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String people = cursor.getString(cursor.getColumnIndexOrThrow("people"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                String distance = cursor.getString(cursor.getColumnIndexOrThrow("distance"));
                String memo = cursor.getString(cursor.getColumnIndexOrThrow("memo"));

                tvDetailDate.setText(date);
                etdetailPeople.setText(people);
                etdetailDistance.setText(memo);
                etdetailTime.setText(time);
                etdetailMemo.setText(distance);

        }else{
            Log.d(TAG, "cursor error");
        }
        cursor.close();
        db.close();

        //dog 정보도 가져와서 체크박스 표시해주기

        //map 정보 가져와서 폴리라인 그려주기

    }

    public void onClick(View v){
        String whereClause;
        String[] whereArgs;
        switch (v.getId()) {
            case R.id.btnWalkDel:
                //삭제
                whereClause = "_id=?";
                whereArgs = new String[] {id};
                db = helper.getWritableDatabase();
                db.delete(WalkDayDBHelper.TABLE_WALK, whereClause, whereArgs);

                Toast.makeText(getApplicationContext(), "삭제완료", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Delete");
                finish();
                break;
            case R.id.btnWalkModify:
                //db 수정
                ContentValues row = new ContentValues();
                row.put(WalkDayDBHelper.COL_PEOPLE, etdetailPeople.getText().toString());
                row.put(WalkDayDBHelper.COL_DISTANCE, etdetailDistance.getText().toString());
                row.put(WalkDayDBHelper.COL_TIME, etdetailTime.getText().toString());
                row.put(WalkDayDBHelper.COL_MEMO, etdetailMemo.getText().toString());

                whereClause = "_id=?";
                whereArgs = new String[] {id};

                db = helper.getWritableDatabase();
                db.update(WalkDayDBHelper.TABLE_DOG, row, whereClause, whereArgs);

                Toast.makeText(getApplicationContext(), "수정완료", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Modify");
                break;
        }
        db.close();
    }
}
