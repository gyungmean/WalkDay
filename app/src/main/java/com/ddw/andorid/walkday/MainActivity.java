package com.ddw.andorid.walkday;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends Activity {

    final static String TAG = "MainActivity";

    TextView tvWeatherComment;

    RecyclerView lvRecentWalk = null;
    WalkDBHelper helper;
    Cursor cursor;
    MyCursorAdapter adapter;
    int resultCode = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        날씨 api 연결
        * */
        tvWeatherComment = (TextView) findViewById(R.id.tvComment);

        lvRecentWalk = (RecyclerView)findViewById(R.id.lvRecentWalk);
        helper = new WalkDBHelper(this);

    }

    public void onClick(View v){
        Intent intent = null;

        switch (v.getId()) {
            case R.id.btnDogInfo:
                intent = new Intent(this, DogInfoActivity.class);
                break;
            case R.id.btnMore:
                intent = new Intent(this, AllWalkActivity.class);
                break;
            case R.id.btnWrite:
                intent = new Intent(this, WriteActivity.class);
                break;
        }

        if (intent != null) startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        DB에서 데이터를 읽어와 Adapter에 설정
//        if(resultCode != RESULT_CANCELED) {
//            Log.d(TAG, "CHANGE CURSOR");
//            Log.d(TAG, "onActivityResult : " + resultCode);
//            SQLiteDatabase db = helper.getReadableDatabase();
//            cursor = db.rawQuery("select * from " + WalkDBHelper.TABLE_NAME, null);
//            adapter.changeCursor(cursor);
//            helper.close();
//        }
//        if(resultCode == RESULT_CANCELED){
//            Toast.makeText(this, "수정취소", Toast.LENGTH_SHORT).show();
//            Log.d(TAG, "RESULT_CANCELED");
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        cursor 사용 종료
        if (cursor != null) cursor.close();
    }
}