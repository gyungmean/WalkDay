package com.ddw.andorid.ma01_20190941;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.List;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class geocoderUnitTest {
    final static String TAG = "geocoderUnitTest";
    private Geocoder geocoder;
    private Context context = ApplicationProvider.getApplicationContext();
    double latitude, longitude;

    @Before
    public void setup(){
        geocoder = new Geocoder(context);

    }

    @Test
    public void test(){
        List<Address> addresses = null;
        String result = null;

        latitude = 37.546299;
        longitude = 126.920220;

//        위도/경도에 해당하는 주소 정보를 Geocoder 에게 요청
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        if (addresses == null || addresses.size()  == 0) {

        } else {
            Address address = addresses.get(0);
            result = address.getAdminArea() + " ";
            if(address.getLocality() != null){
                result += address.getLocality() + " "; //ㅇㅇㅇ도로 시작하는 지역은 여기서 시가 나옴. ex)충청북도 청주시
            }
            result += address.getSubLocality() + " " + address.getThoroughfare(); //시 + 동

        }
        Log.d(TAG, result);
    }
}
