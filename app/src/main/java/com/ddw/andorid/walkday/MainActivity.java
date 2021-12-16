package com.ddw.andorid.walkday;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends Activity {

    final static String TAG = "MainActivity";
    final static int PERMISSION_REQ_CODE = 100;

    /* weather UI */
//    TextView weatherRegion;
    TextView weatherDate;
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
    String x;
    String y;

    WeatherParser parser;
    WeatherDTO nowWeather;

    /*recent walk*/
    RecyclerView lvRecentWalk;
    WalkDBHelper helper;
    Cursor cursor;
//    MyCursorAdapter adapter;
//    int resultCode = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherDate = (TextView) findViewById(R.id.weatherDate);
        weatherNowTemp = (TextView) findViewById(R.id.weatherNowTemp);
        weatherMax = (TextView) findViewById(R.id.weatherMax);
        weatherMin = (TextView) findViewById(R.id.weatherMin);;
        tvPOP = (TextView) findViewById(R.id.tvPOP);;
        weatherIcon = (ImageView) findViewById(R.id.weatherIcon);
        tvWeatherComment = (TextView) findViewById(R.id.tvComment);
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

        setTimeInfo(); //날짜 관련 정보 설정
        setLocationInfo(); //위치 관련 정보 설정

        /* 날씨 api 호출 */
        WeatherApiAddress = getResources().getString(R.string.weather_api_uri);
        query = "&pageNo=1"
                + "&numOfRows=1000"
                + "&dataType=XML"
                + "&base_date=" + base_date
                + "&base_time=" + base_time
                + "&nx=" + x
                + "&ny=" + y;
        Log.d(TAG, "api uri:" + WeatherApiAddress + query);
        new WeatherAsyncTask().execute(WeatherApiAddress, query);

        /* 최근 산책 기록 출력 */
        lvRecentWalk = (RecyclerView)findViewById(R.id.lvRecentWalk);
        helper = new WalkDBHelper(this);
    }

    /* 현재 시간 및 base_time 설정 */
    private void setTimeInfo(){
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd"); //20211214
        SimpleDateFormat textDateFormat = new SimpleDateFormat("yy/MM/dd"); //21/12/14
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh00"); //2311

        today = dateFormat.format(date);
        now = timeFormat.format(date);
        Log.d(TAG, "today: " + today + ", now: " + now);

        if(Integer.parseInt(now) < 300){ //am12-am3일때 전날 자료를 요청해야함
            base_date = String.valueOf(Integer.parseInt(today) - 1);
            base_time = "2300";
        }else{
            base_date = today;
            base_time = "0200";
        }
        Log.d(TAG, "base_date: " + base_date + ", base_time: " + base_time);

        weatherDate.setText(textDateFormat.format(date)); //날짜 text

    }

    /* 현재 위치 및 위치 변환 */
    private void setLocationInfo(){
        GPSTransfer gpsTransfer;
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        double latitude = 37.605509;
        double longitude = 127.041427;
        Location userLocation = getMyLocation();
        if( userLocation != null ) {
            latitude = userLocation.getLatitude();
            longitude = userLocation.getLongitude();
            Log.d(TAG, "lat: " + latitude + " lng: " + longitude);
        }

        //좌표 변환
        gpsTransfer = new GPSTransfer(latitude, longitude);
        gpsTransfer.transfer(gpsTransfer, 0);
        x = Integer.toString((int)gpsTransfer.getxLat());
        y = Integer.toString((int)gpsTransfer.getyLon());

        Log.d(TAG, "x, y : " + x + ", " + y);
    }
    /*현재 나의 위치 반환*/
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

            Log.d(TAG, "onPostExecute: " + today + ", " + now);
            nowWeather = parser.parse(result, today, now);
            if(nowWeather == null){
                Log.e(TAG, "nowWeather is null");
            }

            /*main activity 에 날씨 정보 저장*/
            weatherNowTemp.setText(Integer.toString(nowWeather.getTmp()));
            weatherMax.setText(Integer.toString(nowWeather.getMax()));
            weatherMin.setText(Integer.toString(nowWeather.getMin()));
            tvPOP.setText(Integer.toString(nowWeather.getPop()));

            if(nowWeather.getPty() == 0){
                switch (nowWeather.getSky()){
                    case 1: //맑음
                        weatherIcon.setImageResource(R.drawable.sun);
                        break;
                    case 2: //약간흐림
                        weatherIcon.setImageResource(R.drawable.sunandcloud);
                        break;
                    case 3: //흐림
                        weatherIcon.setImageResource(R.drawable.clouds);
                        break;
                }
            } else{
                switch(nowWeather.getPty()){
                    case 1: //비
                        weatherIcon.setImageResource(R.drawable.rain);
                        break;
                    case 2: //비 or 눈
                        weatherIcon.setImageResource(R.drawable.sleet);
                        break;
                    case 3: //눈
                        weatherIcon.setImageResource(R.drawable.snow);
                        break;
                    case 4: //소나기
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

    /* 주소(address)에 접속하여 문자열 데이터를 수신한 후 반환 */
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

    /* URLConnection 을 전달받아 연결정보 설정 후 연결, 연결 후 수신한 InputStream 반환 */
    private InputStream getNetworkConnection(HttpURLConnection conn) throws Exception {
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(10000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        if (conn.getResponseCode() != HttpsURLConnection.HTTP_OK) {
            throw new IOException("HTTP error code: " + conn.getResponseCode());
        }

        return conn.getInputStream();
    }

    /* InputStream을 전달받아 문자열로 변환 후 반환 */
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



    @Override
    protected void onDestroy() {
        super.onDestroy();
//        cursor 사용 종료
        if (cursor != null) cursor.close();
    }
}