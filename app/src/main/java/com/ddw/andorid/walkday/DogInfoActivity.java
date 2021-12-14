package com.ddw.andorid.walkday;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class DogInfoActivity extends Activity {
    final static String TAG = "DogInfoActivity";

    RecyclerView lvDogInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_info);

        Log.d(TAG, "DogInfoActivity START!");

        //dog db의 내용 출력

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
