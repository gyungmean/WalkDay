package com.ddw.andorid.ma01_20190941;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class DetailWalkActivity extends AppCompatActivity implements OnMapReadyCallback {
    final static String TAG = "DetailActivity";
    final static int PERMISSION_REQ_CODE = 100;

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

    /* map ?????? */
    private GoogleMap mGoogleMap;
    private LocationManager locationManager;
    Marker currentMarker;
    private MarkerOptions markerOptions;
    PolylineOptions pOptions;
    Polyline polyline;
    ArrayList<LatLng> latLngs = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_detail);
        Log.d(TAG, "DetailWalkActivity start!");
        helper = new WalkDayDBHelper(getApplicationContext());
        Intent intent = getIntent();

        id = intent.getStringExtra("id").toString();
        Log.d(TAG, "extra id: " + id);

        pOptions = new PolylineOptions();
        pOptions.color(Color.RED);
        pOptions.width(15);

        mapLoad();

        tvDetailDate = (TextView) findViewById(R.id.tvDetailDate);
        etdetailPeople = (EditText) findViewById(R.id.etdetailPeople);
        etdetailDistance = (EditText) findViewById(R.id.etdetailDistance);
        etdetailTime = (EditText) findViewById(R.id.etdetailTime);
        etdetailMemo = (EditText) findViewById(R.id.etdetailMemo);

        lvDetailDog = (RecyclerView) findViewById(R.id.lvDetailDog);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "DogDetailActivity onResume");

        /*dog ?????? ??????*/
        String[] columns = {"_id", "name"};
        db = helper.getReadableDatabase();
        cursor = db.query(helper.TABLE_DOG, columns, null, null,
                null, null, null, null);
        dogs.clear();
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    DogDTO dog = new DogDTO();
                    dog.setId(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
                    dog.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));

                    Log.d(TAG, "dog name: " + dog.getName());
                    dogs.add(dog);
                }while(cursor.moveToNext());
            }
        }

        cursor.close();

        if (dogs.size() != 0) {
            walkDogAdapter = new WalkDogAdapter(getApplicationContext(), dogs);
            lvDetailDog.setAdapter(walkDogAdapter);
            lvDetailDog.setLayoutManager(new LinearLayoutManager(this));
        }

        /*?????? ??????*/
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
                etdetailDistance.setText(distance);
                etdetailTime.setText(time);
                etdetailMemo.setText(memo);

        }else{
            Log.d(TAG, "cursor error");
        }
        cursor.close();
        db.close();

        /*dog ??????*/
        selection = "walk_id=?";
        db = helper.getWritableDatabase();
        cursor = db.query(WalkDayDBHelper.TABLE_WALK_DOG, null, selection, selectionArgs,
                null, null, null, null);
        //dog ????????? ???????????? ???????????? ???????????????
        ArrayList<Integer> dogId = new ArrayList<>();
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    dogId.add(cursor.getInt(cursor.getColumnIndexOrThrow("dog_id")));
                }while(cursor.moveToNext());
            }
        }
        else{
            Log.d(TAG, "cursor error");
        }
        walkDogAdapter.dogCheck(dogId);
        cursor.close();
        db.close();

        //map ?????? ???????????? ???????????? ????????????
        selection = "walk_id=?";
        db = helper.getWritableDatabase();
        cursor = db.query(WalkDayDBHelper.TABLE_MAPS, null, selection, selectionArgs,
                null, null, null, null);

        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    latLngs.add(new LatLng(cursor.getDouble(cursor.getColumnIndexOrThrow("lat")), cursor.getDouble(cursor.getColumnIndexOrThrow("lng"))));
                }while(cursor.moveToNext());
            }
        }
        else{
            Log.d(TAG, "cursor error");
        }
        cursor.close();
        db.close();

    }

    public void drawRoute(ArrayList<LatLng> latLngs){
        for(LatLng latLng : latLngs){
            Log.d(TAG, "Add Location : " + latLng.toString());
            pOptions.add(latLng);
            polyline = mGoogleMap.addPolyline(pOptions);
        }
    }

    public void onClick(View v){
        String whereClause;
        String[] whereArgs;
        switch (v.getId()) {
            case R.id.btnWalkDel:
                //??????
                Log.d(TAG, "Delete button clicked");
                whereClause = "_id=?";
                whereArgs = new String[] {id};
                db = helper.getWritableDatabase();
                db.delete(WalkDayDBHelper.TABLE_WALK, whereClause, whereArgs);
                whereClause = "walk_id=?";
                db.delete(WalkDayDBHelper.TABLE_WALK_DOG, whereClause, whereArgs);
                db.delete(WalkDayDBHelper.TABLE_MAPS, whereClause, whereArgs);
                db.close();
                Toast.makeText(getApplicationContext(), "????????????", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Delete");
                db.close();
                finish();
                break;
            case R.id.btnWalkModify:
                //db ??????
                ContentValues row = new ContentValues();
                row.put(WalkDayDBHelper.COL_PEOPLE, etdetailPeople.getText().toString());
                row.put(WalkDayDBHelper.COL_DISTANCE, etdetailDistance.getText().toString());
                row.put(WalkDayDBHelper.COL_TIME, etdetailTime.getText().toString());
                row.put(WalkDayDBHelper.COL_MEMO, etdetailMemo.getText().toString());

                whereClause = "_id=?";
                whereArgs = new String[] {id};

                db = helper.getWritableDatabase();
                db.update(WalkDayDBHelper.TABLE_WALK, row, whereClause, whereArgs);

                //???????????????
                db = helper.getWritableDatabase();
                whereClause = "walk_id=?";
                db.delete(WalkDayDBHelper.TABLE_WALK_DOG, whereClause, whereArgs);

                ContentValues row2 = new ContentValues();

                for(int i : walkDogAdapter.checkList()){
                    row2.put(helper.COL_WALKID, id);
                    row2.put(helper.COL_DOGID, i);
                    db.insert(helper.TABLE_WALK_DOG, null, row2);
                    Log.d(TAG, "modify dog id: " + Integer.toString(i));
                }

                db.close();

                Toast.makeText(getApplicationContext(), "????????????", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Modify");
                break;
        }
        db.close();
    }

    /*???????????? ??????????????? ??????*/
    private void mapLoad() {
        SupportMapFragment mapFragment =
                (SupportMapFragment)  getSupportFragmentManager().findFragmentById(R.id.writeMap);
        mapFragment.getMapAsync(this);      //
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        Log.d(TAG, "Map ready");

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get(0), 17));
        if (checkPermission())
            mGoogleMap.setMyLocationEnabled(true);

        //map ?????? ???????????? ???????????? ????????????
        drawRoute(latLngs);
    }

    /* ?????? permission ?????? */
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQ_CODE);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // ???????????? ??????????????? ?????? ??? ?????? ??????
                mapLoad();
            } else {
                // ????????? ????????? ??? ???????????? ??????
                Toast.makeText(this, "??? ????????? ?????? ?????? ????????? ?????????", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
