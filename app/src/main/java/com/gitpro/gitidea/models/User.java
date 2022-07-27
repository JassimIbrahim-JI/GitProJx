package com.gitpro.gitidea.models;


import java.util.List;

public class User {

    public String photoUrl;
    public String userName;
    public String email;
    public List<String>bookMark;
    public boolean isAdmin;
    public List<String> toursCommentedOn;
    public String userId;



    public User(String userName, String email, String photoUrl, List<String>bookMark, List<String> toursCommentedOn,String userId, boolean isAdmin) {
        this.userName=userName;
        this.email=email;
       this.photoUrl=photoUrl;
        this.bookMark=bookMark;
        this.toursCommentedOn = toursCommentedOn;
        this.userId = userId;
        this.isAdmin=isAdmin;
    }



    public User(){}
}
