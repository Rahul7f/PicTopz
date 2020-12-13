package com.example.pictopz.models;

public class ContestObject {
    public String imageUrl;
    public long timeStart;
	public long timeEnd;
    public String category;
    public String contestID;

    public ContestObject(String imageUrl,String category,long timeStart,long timeEnd,String contestID){
        this.imageUrl=imageUrl;
        this.timeStart=timeStart;
	    this.timeEnd=timeEnd;
        this.category=category;
        this.contestID=contestID;
    }
    public ContestObject(){}
}
