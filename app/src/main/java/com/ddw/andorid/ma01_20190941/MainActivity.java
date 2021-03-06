package com.ddw.andorid.ma01_20190941;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends Activity {

    final static String TAG = "MainActivity";
    final static int PERMISSION_REQ_CODE = 100;

    /* weather UI */
//    TextView weatherRegion;
    TextView weatherDate;
    TextView weatherRegion;
    TextView weatherNowTemp;
    TextView weatherMax;
    TextView weatherMin;
    TextView tvPOP;
    ImageView weatherIcon;
    TextView tvWeatherComment;

    /*weather*/
    LocationManager locationManager;

    String WeatherApiAddress;
    String query;

    String base_date;
    String base_time;
    String today;
    String now;
    String isAMorPm;
    String x;
    String y;

    WeatherParser parser;
    WeatherDTO nowWeather;

    Geocoder geocoder;

    /*recent walk*/
    RecyclerView lvRecentWalk;
    WalkDayDBHelper helper;
    Cursor cursor;
    SQLiteDatabase db;
    WalkAdapter walkAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherDate = (TextView) findViewById(R.id.weatherDate);
        weatherRegion = (TextView) findViewById(R.id.weatherRegion);
        weatherNowTemp = (TextView) findViewById(R.id.weatherNowTemp);
        weatherMax = (TextView) findViewById(R.id.weatherMax);
        weatherMin = (TextView) findViewById(R.id.weatherMin);;
        tvPOP = (TextView) findViewById(R.id.tvPOP);;
        weatherIcon = (ImageView) findViewById(R.id.weatherIcon);
        tvWeatherComment = (TextView) findViewById(R.id.tvComment);

        geocoder = new Geocoder(this);

        lvRecentWalk = (RecyclerView) findViewById(R.id.lvRecentWalk);
        helper = new WalkDayDBHelper(getApplicationContext());
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

        setTimeInfo(); //?????? ?????? ?????? ??????
        setLocationInfo(); //?????? ?????? ?????? ??????

        /* ?????? api ?????? */
//        WeatherApiAddress = getResources().getString(R.string.weather_api_uri);
//        query = "&pageNo=1"
//                + "&numOfRows=1000"
//                + "&dataType=XML"
//                + "&base_date=" + base_date
//                + "&base_time=" + base_time
//                + "&nx=" + x
//                + "&ny=" + y;
//        Log.d(TAG, "api uri:" + WeatherApiAddress + query);
//        new WeatherAsyncTask().execute(WeatherApiAddress, query);

        setRecentWalk(); //?????? ???????????? ??????

    }

    /* ?????? ?????? ??? base_time ?????? */
    private void setTimeInfo(){
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd"); //20211214
        SimpleDateFormat textDateFormat = new SimpleDateFormat("yy/MM/dd"); //21/12/14
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh00"); //2311
        SimpleDateFormat dayNightFormat = new SimpleDateFormat("a"); //AM PM

        today = dateFormat.format(date);
        now = timeFormat.format(date);
        isAMorPm = dayNightFormat.format(date);
        Log.d(TAG, "today: " + today + ", now: " + now + ", " + isAMorPm);

        if((isAMorPm.equals("??????")) && (Integer.parseInt(now) < 300 || Integer.parseInt(now) == 1200)){ //am12-am3?????? ?????? ????????? ???????????????
            base_date = String.valueOf(Integer.parseInt(today) - 1);
            base_time = "2300";
        }else{
            base_date = today;
            base_time = "0200";
        }
        Log.d(TAG, "base_date: " + base_date + ", base_time: " + base_time);

        weatherDate.setText(textDateFormat.format(date)); //?????? text

    }

    /* ?????? ?????? ??? ?????? ?????? */
    private void setLocationInfo(){
        GPSTransfer gpsTransfer;
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        /*?????? ?????? ?????? ????????????*/
        double latitude = 37.550966;
        double longitude = 126.990908;
        Location userLocation = getMyLocation();
        if( userLocation != null ) {
            latitude = userLocation.getLatitude();
            longitude = userLocation.getLongitude();

        }
        Log.d(TAG, "lat: " + latitude + " lng: " + longitude);
        String address = getAddress(latitude, longitude);
        Log.d(TAG, "address : " + address);
        if (address == null) {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }
        else{
            weatherRegion.setText(address);
        }

        //?????? ??????
        gpsTransfer = new GPSTransfer(latitude, longitude);
        gpsTransfer.transfer(gpsTransfer, 0);
        x = Integer.toString((int)gpsTransfer.getxLat());
        y = Integer.toString((int)gpsTransfer.getyLon());

        Log.d(TAG, "x, y : " + x + ", " + y);
    }
    /*?????? ?????? ?????? ??????*/
    private Location getMyLocation() {
        Location currentLocation = null;
        // Register the listener with the Location Manager to receive location updates
        if (checkPermission()) {
            String locationProvider = LocationManager.NETWORK_PROVIDER;
            currentLocation = locationManager.getLastKnownLocation(locationProvider);
            if (currentLocation != null) {
                double lng = currentLocation.getLongitude();
                double lat = currentLocation.getLatitude();
            }
        }
        return currentLocation;
    }

    private boolean checkPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQ_CODE);
                return false;
            }
        }
        return true;
    }

    class WeatherAsyncTask extends AsyncTask<String, String, String>{
        ProgressDialog progressDlg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(MainActivity.this, "Wait", "Downloading...");
        }

        @Override
        protected String doInBackground(String... strings) {
            String address = strings[0];
            String query = strings[1];

            String apiURL = address + query;

            String result = downloadWeatherContents(apiURL);
            if(result == null) Log.e(TAG, "result null");
            return result;
        }


        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, result);

            parser = new WeatherParser();
            nowWeather = new WeatherDTO();

            if(isAMorPm.equals("??????") && Integer.parseInt(now) != 1200){
                now = Integer.toString(Integer.parseInt(now) + 1200);
            }
            else if(isAMorPm.equals("??????") && Integer.parseInt(now) == 1200){
                now = "0000";
            }

            Log.d(TAG, "onPostExecute: " + today + ", " + now);

            nowWeather = parser.parse(result, today, now);
            if(nowWeather == null){
                Log.e(TAG, "nowWeather is null");
            }

            /*main activity ??? ?????? ?????? ??????*/
            weatherNowTemp.setText(Integer.toString(nowWeather.getTmp()));
            weatherMax.setText(Integer.toString(nowWeather.getMax()));
            weatherMin.setText(Integer.toString(nowWeather.getMin()));
            tvPOP.setText(Integer.toString(nowWeather.getPop()));

            Log.d(TAG, "weather: " + Integer.toString(nowWeather.getPty()));

            if(nowWeather.getPty() == 0){
                switch (nowWeather.getSky()){
                    case 1: //??????
                        if((500 >= Integer.parseInt(now) || Integer.parseInt(now) == 0000) ||
                                (Integer.parseInt(now) > 1900 && Integer.parseInt(now) <= 2300)){ //?????? 12~5???, ?????? 7~11??? ????????????
                            weatherIcon.setImageResource(R.drawable.sun_night);
                        }
                        else{
                            weatherIcon.setImageResource(R.drawable.sun);
                        }
                        break;
                    case 3: //????????????
                        if((isAMorPm.equals("??????") && (500 < Integer.parseInt(now) || Integer.parseInt(now) == 0000)) ||
                                (isAMorPm.equals("??????") && (Integer.parseInt(now) > 700 || Integer.parseInt(now) < 1200))){ //?????? 12~5???, ?????? 7~11???
                            weatherIcon.setImageResource(R.drawable.clouds_night);
                        }
                        else{
                            weatherIcon.setImageResource(R.drawable.sunandcloud);
                        }
                        break;
                    case 4: //??????
                        weatherIcon.setImageResource(R.drawable.clouds);
                        break;
                }
            } else{
                switch(nowWeather.getPty()){
                    case 1: //???
                        weatherIcon.setImageResource(R.drawable.rain);
                        break;
                    case 2: //??? or ???
                        weatherIcon.setImageResource(R.drawable.sleet);
                        break;
                    case 3: //???
                        weatherIcon.setImageResource(R.drawable.snow);
                        break;
                    case 4: //?????????
                        weatherIcon.setImageResource(R.drawable.shower);
                        break;
                }
            }

            setWeatherComment(nowWeather);

            progressDlg.dismiss();

        }
    }

    private void setWeatherComment(WeatherDTO w){
        if(w.getPty() != 0){
            tvWeatherComment.setText(R.string.rain_comment);
        }

        int tmp = w.getTmp();
        if(10 < tmp && tmp <= 18){
            tvWeatherComment.setText(R.string.safe_comment1);
        }else if((18 < tmp && tmp <= 21) || 10 > tmp && tmp >= 7){
            tvWeatherComment.setText(R.string.safe_comment2);
        }else if((21 < tmp && tmp <= 26) || 7 > tmp && tmp >= -1){
            tvWeatherComment.setText(R.string.danger_comment1);
        }else if((26 < tmp && tmp <= 29) || -1 > tmp && tmp >= -7){
            tvWeatherComment.setText(R.string.danger_comment2);
        }else if(29 < tmp || -7 > tmp){
            tvWeatherComment.setText(R.string.danger_comment3);
        }
    }

    /* ??????(address)??? ???????????? ????????? ???????????? ????????? ??? ?????? */
    protected String downloadWeatherContents(String address) {
        HttpURLConnection conn = null;
        InputStream stream = null;
        String result = null;

        try {
            URL url = new URL(address);
            conn = (HttpURLConnection)url.openConnection();
            stream = getNetworkConnection(conn);
            result = readStreamToString(stream);
            if (stream != null) stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) conn.disconnect();
        }
        return result;
    }

    /* URLConnection ??? ???????????? ???????????? ?????? ??? ??????, ?????? ??? ????????? InputStream ?????? */
    private InputStream getNetworkConnection(HttpURLConnection conn) throws Exception {
        conn.setReadTimeout(100000);
        conn.setConnectTimeout(100000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        if (conn.getResponseCode() != HttpsURLConnection.HTTP_OK) {
            throw new IOException("HTTP error code: " + conn.getResponseCode());
        }

        return conn.getInputStream();
    }

    /* InputStream??? ???????????? ???????????? ?????? ??? ?????? */
    protected String readStreamToString(InputStream stream){
        StringBuilder result = new StringBuilder();

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(stream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String readLine = bufferedReader.readLine();

            while (readLine != null) {
                result.append(readLine + "\n");
                readLine = bufferedReader.readLine();
            }

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }

    private String getAddress(double latitude, double longitude) {

        List<Address> addresses = null;
        String result = null;

//        ??????/????????? ???????????? ?????? ????????? Geocoder ?????? ??????
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        if (addresses == null || addresses.size()  == 0) {
            Log.d(TAG, "getAddress NULL");
            return null;
        } else {
            Address address = addresses.get(0);
            result = address.getAdminArea() + " ";
            if(address.getLocality() != null){
                result += address.getLocality() + " "; //??????????????? ???????????? ????????? ????????? ?????? ??????. ex)???????????? ?????????
            }
            if(address.getSubLocality() != null){ //??? ??????
                result += address.getSubLocality() + " ";
            }
            if(address.getThoroughfare() != null){
                result += address.getThoroughfare(); //??? ?????? or ?????? ?????? ?????? ??? ??????
            }

            Log.d(TAG, "address result: " + result);

        }
        return result;
    }

    private void setRecentWalk(){
        ArrayList<WalkDTO> mData = new ArrayList<>();

        String[] columns = {"_id", "date", "people", "time", "distance"};
        db = helper.getReadableDatabase();
        cursor = db.query(WalkDayDBHelper.TABLE_WALK, columns, null, null,
                null, null, null, null);
        mData.clear();

        ArrayList<Integer> walkId = new ArrayList<>();

        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
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
        }

        cursor.close();
        db.close();

        /*????????? ????????? ?????? db ????????????*/
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

        /*?????? ????????? ???????????? ?????? ?????? 4?????? ????????? ???????????? ??????*/
        ArrayList<WalkDTO> newMData = new ArrayList<>();
        int idx = mData.size() - 1;
        if(idx >= 4){
            int count = 4;
            while(count > 0){
                count--;
                newMData.add(mData.get(idx--));
            }
            walkAdapter = new WalkAdapter(getApplicationContext(), newMData);
        }
        else{
            walkAdapter = new WalkAdapter(getApplicationContext(), mData);
        }

        lvRecentWalk.setAdapter(walkAdapter);
        lvRecentWalk.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        cursor ?????? ??????
        if (cursor != null) cursor.close();
    }
}