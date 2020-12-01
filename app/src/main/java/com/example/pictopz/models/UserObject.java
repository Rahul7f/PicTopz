package com.example.pictopz.models;

public class UserObject {
    public String username;

    public UserObject()
    {

    }

    public UserObject(String username){
        this.username=username;
    }


    public UserObject(String username, String email, String phone) {
        this.username = username;
    }
}
