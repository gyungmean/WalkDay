package com.ddw.andorid.walkday;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class AddDogActivity extends Activity {

    final static String TAG = "AddDogActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dog);

        Log.d(TAG, "AddDogActivity START!");
    }

    public void onClick(View v){
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btnAddFinish:
                //dog db에 저장
                break;
            case R.id.btnAddForm:
                //입력 칸 추가
                break;
        }

        if (intent != null) startActivity(intent);
    }
}
