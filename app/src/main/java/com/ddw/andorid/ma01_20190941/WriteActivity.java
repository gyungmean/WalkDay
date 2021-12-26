package com.ddw.andorid.ma01_20190941;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
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

import java.util.ArrayList;
import java.util.List;

public class WriteActivity extends AppCompatActivity implements OnMapReadyCallback {

    final static String TAG = "WriteActivity";
    final static int PERMISSION_REQ_CODE = 100;

    WalkDTO newWalk;
    ArrayList<DogDTO> dogs = new ArrayList<>();

    /* ui */
    TextView tvUpdateDate;
    RecyclerView lvUpdateDog;
    EditText etUpdatePeople;
    EditText etUpdateDistance;
    EditText etUpdateTime;
    EditText etUpdateMemo;

    WalkDBHelper walkHelper;
    DogDBHelper dogHelper;
    WalkDogsDBHelper walkDogsDBHelper;
    MapsDBHelper mapsDBHelper;

    SQLiteDatabase db;
    Cursor cursor;
    WalkDogAdapter walkDogAdapter;

    /* map 관련 */
    private GoogleMap mGoogleMap;
    private LocationManager locationManager;
    Marker currentMarker;
    private MarkerOptions markerOptions;
    PolylineOptions pOptions;
    Polyline polyline;
    List<LatLng> moveRecord = new ArrayList<LatLng>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        Log.d(TAG, "WriteActivity START!");

        newWalk = new WalkDTO();

        pOptions = new PolylineOptions();
        pOptions.color(Color.RED);
        pOptions.width(7);

        mapLoad();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        tvUpdateDate = (TextView) findViewById(R.id.tvUpdateDate);
        lvUpdateDog = (RecyclerView) findViewById(R.id.lvUpdateDog);
        etUpdatePeople = (EditText) findViewById(R.id.etUpdatePeople);
        etUpdateDistance = (EditText) findViewById(R.id.etUpdateDistance);
        etUpdateTime = (EditText) findViewById(R.id.etUpdateTime);
        etUpdateMemo = (EditText) findViewById(R.id.etUpdateMemo);

        dogHelper = new DogDBHelper(getApplicationContext());

        String[] columns = {"_id", "name"};
        db = dogHelper.getReadableDatabase();
        cursor = db.query(DogDBHelper.TABLE_NAME, columns, null, null,
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
            lvUpdateDog.setAdapter(walkDogAdapter);
            lvUpdateDog.setLayoutManager(new LinearLayoutManager(this));
        }else{
            Toast.makeText(this, "반려견 정보를 먼저 등록하세요.", Toast.LENGTH_SHORT).show();
            finish();
        }

    }



    public void onClick(View v){
        switch(v.getId()){
            case R.id.btnDateSelect:
                Log.d(TAG, "btnDateSelect clicked!");
                DialogFragment newFragment = new DatePickFragment();
                newFragment.show(getSupportFragmentManager(),"datePicker");
                break;

            case R.id.btnMapStart:
                Log.d(TAG, "btnMapStart clicked!");
                Toast.makeText(this, "산책 기록 시작", Toast.LENGTH_SHORT).show();
                if(checkPermission()) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            5000, 0, locationListener);
                }
                break;
            case R.id.btnMapStop:
                Log.d(TAG, "btnMapStop clicked!");
                Toast.makeText(this, "산책 기록 종료", Toast.LENGTH_SHORT).show();
                locationManager.removeUpdates(locationListener);
                break;

            case R.id.btnUpdateCancel:
                Log.d(TAG, "btnUpdateCancel clicked!");
                finish();
                break;

            case R.id.btnUpdateAdd:
                Log.d(TAG, "btnUpdateAdd clicked!");
                walkHelper = new WalkDBHelper(getApplicationContext());
                db = walkHelper.getWritableDatabase();
                ContentValues row = new ContentValues();
                try{
                    newWalk.setPeople(etUpdatePeople.getText().toString());
                    newWalk.setDistance(etUpdateDistance.getText().toString());
                    newWalk.setTime(etUpdateTime.getText().toString());
                    newWalk.setMemo(etUpdateMemo.getText().toString());
                    newWalk.setMaps(moveRecord);
                    newWalk.setDogs(walkDogAdapter.checkList());

                    row.put(WalkDBHelper.COL_DATE, newWalk.getDate());
                    row.put(WalkDBHelper.COL_PEOPLE, newWalk.getPeople());
                    row.put(WalkDBHelper.COL_TIME, newWalk.getTime());
                    row.put(WalkDBHelper.COL_MEMO, newWalk.getMemo());
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "입력되지 않은 부분이 있습니다.", Toast.LENGTH_SHORT).show();
                    break;
                }

                long resultId = db.insert(WalkDBHelper.TABLE_NAME, null, row);
                Log.d(TAG, "resultId: " + Long.toString(resultId));
                walkHelper.close();
                db.close();

                //참조 테이블 정보 저장
                walkDogsDBHelper = new WalkDogsDBHelper(getApplicationContext());
                db = walkDogsDBHelper.getWritableDatabase();

                ContentValues row2 = new ContentValues();

                for(int i : newWalk.getDogs()){
                    row2.put(WalkDogsDBHelper.COL_WALKID, resultId);
                    row2.put(WalkDogsDBHelper.COL_DOGID, i);
                    db.insert(WalkDogsDBHelper.TABLE_NAME, null, row2);
                    Log.d(TAG, "add dog id: " + Integer.toString(i));
                }

                walkDogsDBHelper.close();
                db.close();

                mapsDBHelper = new MapsDBHelper(getApplicationContext());
                db = mapsDBHelper.getWritableDatabase();

                ContentValues row3 = new ContentValues();

                for(LatLng l : newWalk.getMaps()){
                    row3.put(MapsDBHelper.COL_WALKID, resultId);
                    row3.put(MapsDBHelper.COL_LAT, l.latitude);
                    row3.put(MapsDBHelper.COL_LNG, l.longitude);
                    db.insert(WalkDogsDBHelper.TABLE_NAME, null, row3);
                    Log.d(TAG, "add lat: " + Double.toString(l.latitude) + " lng: " + Double.toString(l.longitude));
                }

                mapsDBHelper.close();
                db.close();

                Toast.makeText(getApplicationContext(), "추가완료", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    public void processDatePickerResult(int year, int month, int day){
        String month_string = Integer.toString(month+1);
        String day_string = Integer.toString(day);
        String year_string = Integer.toString(year);
        String dateMessage = year_string + "-" + month_string + "-" + day_string;

        tvUpdateDate.setText(dateMessage);
        newWalk.setDate(dateMessage);//날짜 정보 저장

        Toast.makeText(this,"날짜 선택", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(WriteActivity.this, "현재 위치로 이동", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        mGoogleMap.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
            @Override
            public void onMyLocationClick(@NonNull Location location) {
                Toast.makeText(WriteActivity.this,
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
        if(requestCode == PERMISSION_REQ_CODE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
