package com.caoyi.pinme.models;

/**
 * Created by A.C. on 3/24/18.
 */

public class Messages {

    private String message, type, from;
    private long timestamp;

    public Messages() {
    }

    public Messages(String message, String type, String from, long timestamp) {
        this.message = message;
        this.type = type;
        this.from = from;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
