package com.ddw.andorid.ma01_20190941;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AllWalkActivity extends Activity {

    static final String TAG = "AllWalkActivity";

    RecyclerView lvAllWalk = null;
    ArrayList<WalkDTO> mData = new ArrayList<WalkDTO>();
    WalkDayDBHelper helper;
    SQLiteDatabase db;
    Cursor cursor;
    WalkAdapter walkAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_walk);

        Log.d(TAG, "AllWalkActivity START!");

        lvAllWalk = (RecyclerView) findViewById(R.id.lvAllWalk);
        helper = new WalkDayDBHelper(getApplicationContext());

    }

    @Override
    protected void onResume() {
        super.onResume();
        mData = new ArrayList<>();

        String[] columns = {"_id", "date", "people", "distance", "time"};
        db = helper.getReadableDatabase();
        cursor = db.query(WalkDayDBHelper.TABLE_WALK, columns, null, null,
                null, null, null, null);
        mData.clear();

        ArrayList<Integer> walkId = new ArrayList<>();

        if(cursor != null){
            cursor.moveToFirst();
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String people = cursor.getString(cursor.getColumnIndexOrThrow("people"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                String distance = cursor.getString(cursor.getColumnIndexOrThrow("distance"));

                WalkDTO walk = new WalkDTO();
                walkId.add(id);
                walk.setId(id);
                walk.setDate(date);
                walk.setPeople(people);
                walk.setTime(time);
                walk.setDistance(distance);

                mData.add(walk);

            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        /*개정보 출력을 위한 db 가져오기*/
        List<Integer> result = new ArrayList<>();
        for(int i : walkId){
            String selection = "walk_id=?";
            String[] selectionArgs = {Integer.toString(i)};
            db = helper.getWritableDatabase();
            cursor = db.query(WalkDayDBHelper.TABLE_WALK_DOG, null, selection, selectionArgs,
                    null, null, null, null);

            result.clear();
            if(cursor != null){
                if(cursor.moveToFirst()) {
                    do{
                        result.add(cursor.getInt(cursor.getColumnIndexOrThrow("dog_id")));
                    }while(cursor.moveToNext());
                }
                for(WalkDTO w : mData){
                    if(w.getId() == i){
                        w.setDogs(result);
                    }
                }
            }
        }

        walkAdapter = new WalkAdapter(getApplicationContext(), mData);
        lvAllWalk.setAdapter(walkAdapter);
        lvAllWalk.setLayoutManager(new LinearLayoutManager(this));
    }

    public void onClick(View v){
        Intent intent = null;

        switch (v.getId()) {
            case R.id.btnWrite2:
                intent = new Intent(this, WriteActivity.class);
                break;
        }

        if (intent != null) startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        cursor 사용 종료
        if (cursor != null) cursor.close();
    }
}
