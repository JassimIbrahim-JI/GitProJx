package com.gitpro.gitidea.models;

public class Notification {
    public String userId;
    public String nText;
    public String userImage;
    public String topicId;
    public String projectId;
    public boolean isItTopic;
    public boolean isItProject;

    public Notification(){

    }

    public Notification(String userId, String userImage,String nText, String topicId, String projectId, boolean isItTopic, boolean isItProject) {
        this.userId = userId;
        this.nText = nText;
        this.userImage=userImage;
        this.topicId = topicId;
        this.projectId = projectId;
        this.isItTopic = isItTopic;
        this.isItProject = isItProject;
    }
}
