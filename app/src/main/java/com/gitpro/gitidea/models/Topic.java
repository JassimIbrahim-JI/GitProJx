package com.gitpro.gitidea.models;


import com.google.firebase.firestore.Exclude;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Topic implements Serializable {

    public String userName;
    public String pDescription;
    public String imageProfile;
    public String date;
    public  String pImage;
    public int commentsNum;
    public int numOfPeopleWhoLiked;
    public String publisherBy;
    @Exclude
    public String topicId;
    @SerializedName("comments")
    public List<Comment> comments;

    public Topic(String userName, String pDescription, String imageProfile, String pImage, int commentsNum, int numOfPeopleWhoLiked, String topicId,String publisherBy ,List<Comment>comments,String date) {
        this.userName = userName;
        this.pDescription = pDescription;
        this.pImage=pImage;
        this.commentsNum=commentsNum;
        this.numOfPeopleWhoLiked=numOfPeopleWhoLiked;
        this.topicId=topicId;
        this.publisherBy=publisherBy;
        this.comments=comments;
        this.imageProfile=imageProfile;
        this.date=date;
    }




    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public Topic(){

    }

}
