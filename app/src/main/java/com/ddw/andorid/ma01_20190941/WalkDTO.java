package com.ddw.andorid.ma01_20190941;


import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.List;

public class WalkDTO implements Serializable {

    private long id;
    private String date;
    private String people;
    private String distance;
    private String time;
    private String memo;
    private List<Integer> dogs;
    private List<LatLng> maps;

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

    public List<Integer> getDogs() { return dogs; }
    public void setDogs(List<Integer> dogs) { this.dogs = dogs; }

    public List<LatLng> getMaps() { return maps; }
    public void setMaps(List<LatLng> maps) { this.maps = maps; }
}
