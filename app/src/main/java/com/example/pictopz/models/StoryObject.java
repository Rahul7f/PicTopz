package com.example.pictopz.models;

public class StoryObject {
    public String imageURL;
    public String storyID;
    public long uploadTime;
    public String uploaderUID;


    public StoryObject(String imageURL, String storyID, long uploadTime, String uploaderUID) {
        this.imageURL = imageURL;
        this.storyID = storyID;
        this.uploadTime = uploadTime;
        this.uploaderUID = uploaderUID;
    }

    public StoryObject(){

    }
}
