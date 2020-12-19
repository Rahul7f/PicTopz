package com.example.pictopz.models;

public class ApprovedPostObject {
    public String imgURL; //URL of Image to be shown in Post
    public String dataID; //Unique ID of Post also parent of this node
    public String userName; //Name of user who uploaded the post
    public String userUID; //UID of User who uploaded Post
    public String contestID; //ID of Contest in which Photo is uploaded //ignore if not taking part
    public int likesNo=0; //number of likes
    public int commentsNo=0; //number of comments
    public boolean approved; //is picture approved for contest by admin //vales are true for approved or global posts and false for unapproved
    public long timestamp; //time at which post is uploaded
    public String filterID; //used in filtering // value is CONTEST or <ANY_UID>
    public int position;

    public ApprovedPostObject(String imgURL, String dataID, String userName, String userUID, String contestID, long timestamp) {
        //constructor for uploading picture in contest
        this.approved=false;
        this.imgURL = imgURL;
        this.dataID = dataID;
        this.userName = userName;
        this.userUID = userUID;
        this.contestID = contestID;
        this.timestamp = timestamp;
        this.filterID = "CONTEST";

    }

    //constructor for normal posts upload
    public ApprovedPostObject(String imgURL, String dataID, String userName, String userUID, long timestamp) {
        this.imgURL = imgURL;
        this.dataID = dataID;
        this.userName = userName;
        this.userUID = userUID;
        this.approved = true;
        this.timestamp = timestamp;
        this.filterID = userUID;
    }

    //for firebase object creation
    ApprovedPostObject(){

    }
}
