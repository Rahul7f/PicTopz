package com.example.pictopz.models;

public class ContestObject {
    public String imageUrl;
    public long time;
    public String category;

    public ContestObject(String imageUrl,String category,long time){
        this.imageUrl=imageUrl;
        this.time=time;
        this.category=category;
    }
    public ContestObject(){}
}
