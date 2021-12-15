package com.ddw.andorid.walkday;

import android.util.Log;

public class GPSTransfer {

    final static String TAG = "GPSTransfer";

    private double lat; //gps 위도
    private double lon; //경도

    private double xLat; //변환
    private double yLon;

    public GPSTransfer(){}

    public GPSTransfer(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getxLat() {
        return xLat;
    }

    public void setxLat(double xLat) {
        this.xLat = xLat;
    }

    public double getyLon() {
        return yLon;
    }

    public void setyLon(double yLon) {
        this.yLon = yLon;
    }

    public void transfer(GPSTransfer gpt, int mode){ //mode 0 : 격자 -> 위경도 1 : 위경도 -> 격자
        double RE = 6371.00877;
        double GRID = 5.0;
        double SLAT1 = 30.0;
        double SLAT2 = 60.0;
        double OLON = 126.0;
        double OLAT = 38.0;
        double XO = 43;
        double YO = 136;

        double DEGRAD = Math.PI / 180.0;
        double RADDEG = 180.0 / Math.PI;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;
        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5); ro = re * sf / Math.pow(ro, sn);

        if (mode == 0) {
            Log.d(TAG, "mode0 start!");
            double ra = Math.tan(Math.PI * 0.25 + (gpt.getLat()) * DEGRAD * 0.5);
            ra = re * sf / Math.pow(ra, sn);
            double theta = gpt.getLon() * DEGRAD - olon;

            if (theta > Math.PI) theta -= 2.0 * Math.PI;
            if (theta < -Math.PI) theta += 2.0 * Math.PI; theta *= sn;

            double x = Math.floor(ra * Math.sin(theta) + XO + 0.5);
            double y = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);
            Log.d(TAG, "result x : " + Double.toString(x));
            Log.d(TAG, "result y : " + Double.toString(y));
            gpt.setxLat(x);
            gpt.setyLon(y);
        }
        else {
            Log.d(TAG, "mode1 start!");
            double xlat = gpt.getxLat();
            double ylon = gpt.getyLon();
            double xn = xlat - XO;
            double yn = ro - ylon + YO;
            double ra = Math.sqrt(xn * xn + yn * yn);
            if (sn < 0.0) { ra = -ra; }

            double alat = Math.pow((re * sf / ra), (1.0 / sn)); alat = 2.0 * Math.atan(alat) - Math.PI * 0.5;
            double theta = 0.0;
            if (Math.abs(xn) <= 0.0) { theta = 0.0; }
            else {
                if (Math.abs(yn) <= 0.0) {
                    theta = Math.PI * 0.5;
                    if (xn < 0.0) { theta = -theta; }
                } else theta = Math.atan2(xn, yn);
            }

            double alon = theta / sn + olon;
            Log.d(TAG, "result alat : " + Double.toString(alat));
            Log.d(TAG, "result alon : " + Double.toString(alon));
            gpt.setLat(alat * RADDEG);
            gpt.setLon(alon * RADDEG); }
    }

    @Override
    public String toString() { return "GpsTransfer{" + "lat=" + lat + ", lon=" + lon + ", xLat=" + xLat + ", yLon=" + yLon + '}'; }
}

