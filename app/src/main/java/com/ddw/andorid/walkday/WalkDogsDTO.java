package com.ddw.andorid.walkday;

import java.io.Serializable;

public class WalkDogsDTO implements Serializable {
    private long id;
    private long walkId;
    private int dogId;
    private String dogName;

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

    public String getDogName() {
        return dogName;
    }

    public void setDogName(String dogName) {
        this.dogName = dogName;
    }
}
