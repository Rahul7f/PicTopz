package com.example.pictopz.models;

public class UnApprovedDataObject {
    public String imgURL;
    public String dataID;
    public String userName;
    public String userUID;
    public String contestID;
    public int likesNo;
    public int commentsNo;


    public UnApprovedDataObject(){

    }

    public UnApprovedDataObject(String imgURL, String dataID, String userName, String userUID,String contestID ,int likesNo ,int commentsNo) {
        this.imgURL = imgURL;
        this.dataID = dataID;
        this.userName = userName;
        this.userUID = userUID;
        this.contestID=contestID;
        this.likesNo=likesNo;
        this.commentsNo=commentsNo;
    }
}
