package com.ddw.andorid.ma01_20190941;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

public class AllWalkActivity extends Activity {

    static final String TAG = "AllWalkActivity";

    ListView lvWalk = null;
    WalkDBHelper helper;
    Cursor cursor;
//    MyCursorAdapter adapter;
    int resultCode = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_walk);

//        lvWalk = (ListView)findViewById(R.id.lvRecentWalk);
//        helper = new WalkDBHelper(this);
//        adapter = new MyCursorAdapter(this, R.layout.listview_walk_layout, null);
//        lvWalk.setAdapter(adapter);
//
//        lvWalk.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                Intent intent = new Intent(view.getContext(), UpdateActivity.class);
//                intent.putExtra("ID", id);
//                startActivityForResult(intent, 0);
//            }
//        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.d(TAG, "onActivityResult : " + resultCode);
//        this.resultCode = resultCode;
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
