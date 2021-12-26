package com.ddw.andorid.ma01_20190941;

import java.io.Serializable;

public class WalkDogsDTO implements Serializable {
    private long id;
    private long walkId;
    private int dogId;

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

    public int getDogId() {
        return dogId;
    }

    public void setDogId(int dogId) {
        this.dogId = dogId;
    }
}
