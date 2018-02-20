package com.radiance.android.chatapp.model;

import java.io.Serializable;

/**
 * Created by ASUS PC on 18/02/2018.
 */

public class Event implements Serializable {
    String id, name, startAt, timestamp;

    public Event() {
    }

    public Event(String id, String name, String startAt, String timestamp) {
        this.id = id;
        this.name = name;
        this.startAt = startAt;
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

    public String getStartAt() {
        return startAt;
    }

    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
