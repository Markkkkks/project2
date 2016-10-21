package com.example.zeus.project1014;

import java.io.Serializable;

/**
 * Created by zeus on 2016/10/16.
 */
public class friend_data implements Serializable{
    private String name;
    private double latititue;
    private double longtitue;
    private String Tel_num;
    private double  distence=0;

    public friend_data(String s, String s1) {
        this.name=s;
        this.Tel_num=s1;
    }


    public void setDistence(double distence) {
        this.distence = distence;
    }

    public double getLatititue() {
        return latititue;
    }

    public String getTel_num() {
        return Tel_num;
    }

    public double getLongtitue() {
        return longtitue;
    }

    public String getName() {
        return name;
    }

    public void setLatititue(double latititue) {
        this.latititue = latititue;
    }

    public void setLongtitue(double longtitue) {
        this.longtitue = longtitue;
    }
    public void setlocation(double latititue,double longtitue) {
        this.latititue = latititue;
        this.longtitue = longtitue;
    }

    public double getDistence() {
        return distence;
    }
}
