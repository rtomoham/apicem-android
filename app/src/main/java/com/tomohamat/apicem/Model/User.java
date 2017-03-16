package com.tomohamat.apicem.Model;

/**
 * Created by Robert on 3/12/2017.
 */

public class User {

    private static final String TAG = "User";

    private String username;

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
