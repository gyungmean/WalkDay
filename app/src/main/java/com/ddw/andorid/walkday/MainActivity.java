package com.ddw.andorid.walkday;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v){
        Intent intent = null;

        switch (v.getId()) {
            case R.id.btnOpenAllWalk:
                intent = new Intent(this, AllWalkActivity.class);
                break;
            case R.id.btnAddNewWalk:
                intent = new Intent(this, InsertWalkActivity.class);
                break;
        }

        if (intent != null) startActivity(intent);
    }
}