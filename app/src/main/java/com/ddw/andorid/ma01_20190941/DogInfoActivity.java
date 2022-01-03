package com.ddw.andorid.ma01_20190941;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DogInfoActivity extends Activity {
    final static String TAG = "DogInfoActivity";

    RecyclerView lvDogInfo;
    ArrayList<DogDTO> mData = new ArrayList<DogDTO>();
    WalkDayDBHelper helper;
    SQLiteDatabase db;
    Cursor cursor;
    DogAdapter dogAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_info);

        Log.d(TAG, "DogInfoActivity START!");

        lvDogInfo = (RecyclerView) findViewById(R.id.lvDogInfo);
        helper = new WalkDayDBHelper(getApplicationContext());


    }

    @Override
    protected void onResume() {
        super.onResume();
        //dog db의 내용 출력
        Log.d(TAG, "DogInfoActivity onResume");
        String[] columns = {"_id", "name", "birth_y", "type", "weight"};
        db = helper.getReadableDatabase();
        cursor = db.query(WalkDayDBHelper.TABLE_DOG, columns, null, null,
                null, null, null, null);
        mData.clear();

        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String birthY = cursor.getString(cursor.getColumnIndex("birth_y"));
                    float weight = cursor.getFloat(cursor.getColumnIndex("weight"));
                    String type = cursor.getString(cursor.getColumnIndex("type"));

                    DogDTO dog = new DogDTO();
                    dog.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                    dog.setName(name);
                    dog.setBirthY(birthY);
                    dog.setWeight(weight);
                    dog.setType(type);

                    mData.add(dog);
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();

        dogAdapter = new DogAdapter(getApplicationContext(), mData);
        lvDogInfo.setAdapter(dogAdapter);
        lvDogInfo.setLayoutManager(new LinearLayoutManager(this));
    }

    public void onClick(View v){
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btnNewDog:
                intent = new Intent(this, AddDogActivity.class);
                break;
        }

        if (intent != null) startActivity(intent);
    }
}
