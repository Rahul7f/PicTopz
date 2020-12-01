package com.example.pictopz.models;

public class UnApprovedDataObject {
    public String imgURL;
    public String dataID;
    public String userName;
    public String userUID;
    public String contestID;

    public UnApprovedDataObject(){

    }

    public UnApprovedDataObject(String imgURL, String dataID, String userName, String userUID,String contestID) {
        this.imgURL = imgURL;
        this.dataID = dataID;
        this.userName = userName;
        this.userUID = userUID;
        this.contestID=contestID;
    }
}
