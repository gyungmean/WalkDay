package com.ddw.andorid.walkday;


import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class WalkDTO implements Serializable {

    private long id;
    private String date;
    private String people;
    private String distance;
    private String time;
    private String memo;
    private String[] dogs;
    private LatLng[] maps;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getPeople() { return people; }
    public void setPeople(String people) { this.people = people; }

    public String getDistance() { return distance;}
    public void setDistance(String distance) { this.distance = distance; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }

    public String[] getDogs() { return dogs; }
    public void setDogs(String[] dogs) { this.dogs = dogs; }

    public LatLng[] getMaps() { return maps; }
    public void setMaps(LatLng[] maps) { this.maps = maps; }
}