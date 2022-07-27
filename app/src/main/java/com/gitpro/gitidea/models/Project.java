package com.gitpro.gitidea.models;

import java.io.Serializable;
import java.util.List;

public class Project implements Serializable {

    public String projectId;
    public String mUser;
    public String url;
    public int numOfPeopleWhoLiked;
    public String urlPreview;
    public String pDescription;
    public List<Comment> comments;
    public int commentsNum;
    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public Project(String projectId,String mUser, String url,int numOfPeopleWhoLiked, String urlPreview, String pDescription, List<Comment> comments, int commentsNum) {
        this.projectId = projectId;
        this.mUser = mUser;
        this.url=url;
        this.numOfPeopleWhoLiked = numOfPeopleWhoLiked;
        this.urlPreview = urlPreview;
        this.pDescription = pDescription;
        this.comments = comments;
        this.commentsNum = commentsNum;
    }

    public Project() {
    }
}
