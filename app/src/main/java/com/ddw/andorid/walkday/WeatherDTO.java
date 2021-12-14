package com.ddw.andorid.walkday;

import java.io.Serializable;

public class WeatherDTO implements Serializable {
    private int pop; //강수확률
    private String weather; //날씨정보 맑음, 흐림 등
    private int tmp; //현재기온
    private int max; //오늘 최고기온 float에서 변환
    private int min; //오늘 최저기온 float에서 변환

    public int getPop() {
        return pop;
    }

    public void setPop(int pop) {
        this.pop = pop;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public int getTmp() {
        return tmp;
    }

    public void setTmp(int tmp) {
        this.tmp = tmp;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }
}
