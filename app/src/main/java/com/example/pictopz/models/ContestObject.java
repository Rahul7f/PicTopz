package com.example.pictopz.models;

public class ContestObject {
    public String imageUrl;
    public long time;
    public String category;
    public String contestID;

    public ContestObject(String imageUrl,String category,long time,String contestID){
        this.imageUrl=imageUrl;
        this.time=time;
        this.category=category;
        this.contestID=contestID;
    }
    public ContestObject(){}
}
