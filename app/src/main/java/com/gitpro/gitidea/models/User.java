package com.gitpro.gitidea.models;


import java.util.List;

public class User {

    public String photoUrl;
    public String userName;
    public String email;
    public List<String>bookMark;
    public List<String>bookMark2;
    public boolean isAdmin;
    public List<String> toursCommentedOn;
    public String userId;
    public List<String>contentUser;
    public List<String>userImages;
    public String userBio;
    public String userHeader;



    public User(String userName, String email, String photoUrl, String userHeader,List<String>bookMark,List<String>bookMark2 ,List<String> toursCommentedOn,String userId, boolean isAdmin,List<String>contentUser,List<String>userImages,String userBio) {
        this.userName=userName;
        this.email=email;
       this.photoUrl=photoUrl;
        this.bookMark=bookMark;
        this.bookMark2=bookMark2;
        this.toursCommentedOn = toursCommentedOn;
        this.userId = userId;
        this.isAdmin=isAdmin;
        this.contentUser=contentUser;
        this.userBio=userBio;
        this.userHeader=userHeader;
        this.userImages=userImages;
    }

//    @Exclude
//    public Map<String, Object> toMap() {
//        HashMap<String, Object> result = new HashMap<>();
//        if(userName != null)
//            result.put("userName", userName);
//        if(userBio != null)
//            result.put("userBio", userBio);
//        if(contentUser != null)
//            result.put("contentUser", contentUser);
//        if(email != null)
//            result.put("email", email);
//        if(isAdmin != false)
//            result.put("isAvailable", isAvailable);
//        if(where != null)
//            result.put("where", where);
//        if(uriImage != null)
//            result.put("uriImage", uriImage);
//
//        return result;
//    }


    public User(){}
}
