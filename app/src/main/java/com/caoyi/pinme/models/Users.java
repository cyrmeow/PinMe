package com.caoyi.pinme.models;

/**
 * Created by A.C. on 3/23/18.
 */

public class Users {

    // Match Firebase Database keys
    public String name;
    public String image;
    public String status;

    public Users() {

    }

    public Users(String name, String image, String status) {
        this.name = name;
        this.image = image;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
