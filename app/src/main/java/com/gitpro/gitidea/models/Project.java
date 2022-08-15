package com.gitpro.gitidea.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Project implements Serializable {

    public String projectId;
    public String mUser;
    public String url;
    public int numOfPeopleWhoLiked;
    public String urlPreview;
    public String pDescription;
    public String projectLanguage;
    @SerializedName("comments")
    public List<Comment> comments;
    public int commentsNum;
    public String pDate;
    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public Project(String projectId,String mUser,String projectLanguage ,String pDate,String url,int numOfPeopleWhoLiked, String urlPreview, String pDescription, List<Comment> comments, int commentsNum) {
        this.projectId = projectId;
        this.mUser = mUser;
        this.projectLanguage=projectLanguage;
        this.url=url;
        this.pDate=pDate;
        this.numOfPeopleWhoLiked = numOfPeopleWhoLiked;
        this.urlPreview = urlPreview;
        this.pDescription = pDescription;
        this.comments = comments;
        this.commentsNum = commentsNum;
    }

    public Project() {
    }
}
