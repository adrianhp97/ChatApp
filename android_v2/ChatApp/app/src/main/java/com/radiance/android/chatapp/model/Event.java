package com.radiance.android.chatapp.model;

import java.io.Serializable;

/**
 * Created by ASUS PC on 18/02/2018.
 */

public class Event implements Serializable {
    String id, name, desc, startAt, endAt, location, timestamp;

    public Event() {
    }

    public Event(String id, String name, String desc, String startAt, String endAt, String location, String timestamp) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.startAt = startAt;
        this.endAt = endAt;
        this.location = location;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getStartAt() {
        return startAt;
    }

    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }

    public String getEndAt() {
        return endAt;
    }

    public void setEndAt(String endAt) {
        this.endAt = endAt;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
