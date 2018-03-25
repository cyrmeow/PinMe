package com.caoyi.pinme.models;

/**
 * Created by A.C. on 3/24/18.
 */

public class Friends {

    long timestamp;

    public Friends() {
    }

    public Friends(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
