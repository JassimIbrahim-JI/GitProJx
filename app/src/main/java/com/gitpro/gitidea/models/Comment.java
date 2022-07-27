package com.gitpro.gitidea.models;



import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.Serializable;

// @IgnoreExtraProperties Properties that don't map to class fields are ignored when serializing to a class annotated with this annotation.
   @IgnoreExtraProperties
    public class Comment implements Serializable {

        public String user;
        public String comment;
        public String date;
        public String photoProfile;


        public Comment(String user, String photoProfile,String comment, String date) {
            this.user = user;
            this.comment = comment;
            this.date = date;
            this.photoProfile=photoProfile;

        }

        public Comment() { }//needed to firebase

    }

