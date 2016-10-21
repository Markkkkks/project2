package com.example.zeus.project1014;

import android.app.Application;

import java.util.ArrayList;

/**
 * Created by zeus on 2016/10/17.
 */
public class Data extends Application {
    public ArrayList<Object> friends_Data;

    private static final String VALUE = "Harvey";

    private String value;

    @Override
    public void onCreate()
    {
        super.onCreate();

    }

    public void setFriends_Data(ArrayList<Object> friends_Data) {
        this.friends_Data = friends_Data;
    }

    public ArrayList<Object> getFriends_Data() {
        return friends_Data;
    }
}
