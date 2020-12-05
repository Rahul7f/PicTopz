package com.example.pictopz.models;

public class UserProfileObject {
    public String username;
    public String email;
    public String phone;
    public String profileURL;
    public int followers;
    public int following;

    public UserProfileObject(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public UserProfileObject(String username, String email, String phone) {
        this.username = username;
        this.email = email;
        this.phone = phone;
    }


    public UserProfileObject(String username) {
        this.username = username;
    }

    public UserProfileObject() {
    }
}
