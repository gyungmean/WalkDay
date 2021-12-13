package com.ddw.andorid.walkday;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class WalkDTO implements Serializable {

    private long id;
    private String date;
    private String distance;
    private String time;
    private String kcal;
    private String name;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getDistance() { return distance; }
    public void setDistance(String distance) { this.distance = distance; }

    public String getTime() { return time;}
    public void setTime(String time) { this.time = time; }

    public String getKcal() { return kcal; }
    public void setKcal(String kcal) { this.kcal = kcal; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

}
