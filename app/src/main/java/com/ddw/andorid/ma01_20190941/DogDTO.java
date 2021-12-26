package com.ddw.andorid.ma01_20190941;

import java.io.Serializable;

public class DogDTO implements Serializable {
    private int id;
    private String name;
    private String birthY;
    private String birthM;
    private String birthD;
    private float weight;
    private String type;
    private int[] gender = new int[3];
    private String path;//사진

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthY() { return birthY; }

    public void setBirthY(String birthY) { this.birthY = birthY; }

    public String getBirthM() { return birthM; }

    public void setBirthM(String birthM) { this.birthM = birthM; }

    public String getBirthD() { return birthD; }

    public void setBirthD(String birthD) { this.birthD = birthD; }

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

    public int getGender() {
        int result = 0;
        if(gender[0] == 1) {result += 100;}
        if(gender[1] == 1) {result += 10;}
        if(gender[2] == 1) {result += 1;}
        return result;
    }

    public void setGender(int[] gender) {
        this.gender = gender;
    }

    public String getPath() { return path; }

    public void setPath(String path) { this.path = path; }
}
