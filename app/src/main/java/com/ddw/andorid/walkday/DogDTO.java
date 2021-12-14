package com.ddw.andorid.walkday;

import java.io.Serializable;

public class DogDTO implements Serializable {
    private String name;
    private String birth;
    private float weight;
    private String type;
    private int[] gender;
    //사진


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int[] getGender() {
        return gender;
    }

    public void setGender(int[] gender) {
        this.gender = gender;
    }
}
