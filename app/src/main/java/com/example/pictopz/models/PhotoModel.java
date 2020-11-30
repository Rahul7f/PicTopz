package com.example.pictopz.models;

public class PhotoModel {

    public String photoURL;
    public String contestID;
    public String username;
    public long likes;
    public long comments;

    public PhotoModel(){

    }

    public PhotoModel(String photoURL, String contestID, String username, long likes, long comments) {
        this.photoURL = photoURL;
        this.contestID = contestID;
        this.username = username;
        this.likes = likes;
        this.comments = comments;
    }
}
