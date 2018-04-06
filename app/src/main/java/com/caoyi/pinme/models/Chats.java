package com.caoyi.pinme.models;

/**
 * Created by A.C. on 3/25/18.
 */

public class Chats {

    private long timestamp;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Chats(long timestamp) {

        this.timestamp = timestamp;
    }

    public Chats() {

    }
}
