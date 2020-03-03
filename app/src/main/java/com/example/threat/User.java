package com.example.threat;

import java.util.Date;

public class User {
    User(String id, String name) {
        this.id = id;
        this.name = name;
        type="THREAT";
    }

    public String id;
    public String name;
    public Date date;
    public String type;
    public double latitude;
    public double longitude;
    public boolean isHandled;
}
