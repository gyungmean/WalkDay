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

    /* map 관련 */
    private GoogleMap mGoogleMap;
    private LocationManager locationManager;
    Marker currentMarker;
    private MarkerOptions markerOptions;
    PolylineOptions pOptions;
    Polyline polyline;
    List<LatLng> moveRecord = new ArrayList<LatLng>();

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
        pOptions.width(7);

        //mapLoad();

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

        /*dog 전체 정보*/
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

        /*산책 정보*/
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

        /*dog 정보*/
        selection = "walk_id=?";
        db = helper.getWritableDatabase();
        cursor = db.query(WalkDayDBHelper.TABLE_WALK_DOG, null, selection, selectionArgs,
                null, null, null, null);
        //dog 정보도 가져와서 체크박스 표시해주기
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

    /*구글맵을 멤버변수로 로딩*/
    private void mapLoad() {
        SupportMapFragment mapFragment =
                (SupportMapFragment)  getSupportFragmentManager().findFragmentById(R.id.writeMap);
        mapFragment.getMapAsync(this);      // 매배변수 this: MainActivity 가 OnMapReadyCallback 을 구현하므로
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        Log.d(TAG, "Map ready");

        Location userLocation = getMyLocation();
        LatLng currentLog  = new LatLng(37.606320, 127.041758); //기본 위도 경도 저장
        if( userLocation != null ) {
            Log.d(TAG, "lat: " + userLocation.getLatitude() + " lng: " + userLocation.getLongitude());
            currentLog = new LatLng(userLocation.getLatitude(), userLocation.getLongitude()); //위도 경도 저장
        }

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLog, 17)); //지정한 위치로 이동 후 17의 배율로 확대
        //marker 표시
        markerOptions = new MarkerOptions();
        markerOptions.position(currentLog);
//            options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)); //디폴트 마커 사용

        //지도에 마커 추가 후 추가한 마커 정보 기록
        currentMarker = mGoogleMap.addMarker(markerOptions);
        currentMarker.showInfoWindow();


        if (checkPermission())
            mGoogleMap.setMyLocationEnabled(true);

        mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Toast.makeText(DetailWalkActivity.this, "현재 위치로 이동", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        mGoogleMap.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
            @Override
            public void onMyLocationClick(@NonNull Location location) {
                Toast.makeText(DetailWalkActivity.this,
                        String.format("현재 위치: (%f, %f)", location.getLatitude(), location.getLongitude()),
                        Toast.LENGTH_SHORT).show();
            }
        });

        // TODO: 맵 로딩 후 초기에 해야 할 작업 구현
        markerOptions = new MarkerOptions();
    }

    /*구글맵을 멤버변수로 로딩*/
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) { //위치 정보를 수신할 때마다 위치로 지도의 중심 변경
            Log.d(TAG, "location changed");
            LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 17));

            //움직이는 마커 표시 이동중 중심
            currentMarker.setPosition(currentLoc);

            //현재 위치를 선 그리기 위치로 지정
            pOptions.add(currentLoc);

            moveRecord.add(currentLoc);
            Log.d(TAG, "Add Location : " + "lat: " + location.getLatitude() + " lng: " + location.getLongitude());

            //선 그리기 수행
            polyline = mGoogleMap.addPolyline(pOptions);
            moveRecord.add(currentLoc);

        }

        //안쓰는 함수들
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras){ }
        @Override
        public void onProviderEnabled(String provider){ }
        @Override
        public void onProviderDisabled(String provider){ }
    };

    private Location getMyLocation() {
        Location currentLocation = null;
        // Register the listener with the Location Manager to receive location updates
        if (checkPermission()) {
            String locationProvider = LocationManager.GPS_PROVIDER;
            currentLocation = locationManager.getLastKnownLocation(locationProvider);
            if (currentLocation != null) {
                double lng = currentLocation.getLongitude();
                double lat = currentLocation.getLatitude();
            }
        }
        return currentLocation;
    }

    /* 필요 permission 요청 */
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
                // 퍼미션을 획득하였을 경우 맵 로딩 실행
                mapLoad();
            } else {
                // 퍼미션 미획득 시 액티비티 종료
                Toast.makeText(this, "앱 실행을 위해 권한 허용이 필요함", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
