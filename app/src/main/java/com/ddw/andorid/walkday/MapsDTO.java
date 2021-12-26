package com.ddw.andorid.walkday;

import java.io.Serializable;

public class MapsDTO implements Serializable {
    private long id;
    private long walkId;
    private double lat;
    private double lng;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getWalkId() {
        return walkId;
    }

    public void setWalkId(long walkId) {
        this.walkId = walkId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
