package com.gitpro.gitidea.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.gitpro.gitidea.models.Gallery;
import com.gitpro.gitidea.models.Project;
import com.gitpro.gitidea.models.Topic;
import com.gitpro.gitidea.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FireStoreQueries {

//Store Queries
    private static final String TAG = "FirestoreQueries";

    //Firebase
    private static FirebaseAuth mFirebaseAuth;
    private static FirebaseFirestore mFirebaseFirestore;
    //CollectionRef used to adding document, getting document, querying for document
    private static CollectionReference users, topics,projects,galleries;
  private static boolean firstTopic=true;
    // getting data  user from firebase
    public static void getUser(final FirestoreUsersCallback firestoreCallback){
        //Initialize user permissions
        mFirebaseAuth = FirebaseAuth.getInstance();
        // Initialize cloud FireStore
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        //get document from FireStore user collection
        users = mFirebaseFirestore.collection("users");
        users.whereEqualTo("email",  Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getEmail()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                User mUser = queryDocumentSnapshots.getDocuments().get(0).toObject(User.class);
                firestoreCallback.onCallback(mUser);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.w(TAG,"Error Fetching config", e);
            }
        });
    }

    public static void getLimitProject(final FirestoreProjectCallback firestoreProjectCallback){
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        projects = mFirebaseFirestore.collection("projects");

        projects.orderBy("bNumOfPeopleWhoLiked", Query.Direction.DESCENDING).limit(3).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                  if (task.isSuccessful()){
                      List<Project> mProjects = new ArrayList<>();
                      for (QueryDocumentSnapshot document :  task.getResult()) {
                          Log.d("Projects = ",document.getData()+"");
                          Project project=document.toObject(Project.class);
                          project.setProjectId(document.getId());
                          mProjects.add(project);
                      }
                    firestoreProjectCallback.onCallback(mProjects);
                  }
                  else
                      Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });

    }


    public static void getTopics(final FirestoreTopicCallback firestoreTopicCallback){
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        topics = mFirebaseFirestore.collection("topics");
        //getting tours
        topics.orderBy("date", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    ArrayList<Topic> mTopics = new ArrayList<Topic>();

                    for (QueryDocumentSnapshot document :  task.getResult()) {
                        if (document.exists()) {
                            Topic topic=document.toObject(Topic.class);
                            topic.setTopicId(document.getId());
                            mTopics.add(0,topic);
                            Log.d("Topics = ",document.getData()+"");
                        }
                    }
                    firestoreTopicCallback.onCallback(mTopics);
                }
                else
                    Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }



public static void getProjects(final FirestoreProjectCallback firestoreProjectCallback){
    mFirebaseAuth = FirebaseAuth.getInstance();
    mFirebaseFirestore = FirebaseFirestore.getInstance();
    projects = mFirebaseFirestore.collection("projects");
    projects.orderBy("numOfPeopleWhoLiked", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
          if (task.isSuccessful()){
              List<Project>projects=new ArrayList<>();
              for (QueryDocumentSnapshot document: task.getResult()){
                  Log.d("Projects = ",document.getData()+"");
                  Project project=document.toObject(Project.class);
                  project.setProjectId(document.getId());
                  projects.add(project);
              }
              firestoreProjectCallback.onCallback(projects);
          }
          else {
              Log.d(TAG, "onFailure: Error getting Docs: ", task.getException());
          }
        }
    });
}

    public interface FirestoreUsersCallback {
        void onCallback(User user);
    }


    public interface FirestoreTopicCallback {
        void onCallback(ArrayList<Topic> topics);
    }

    public interface FirestoreProjectCallback {
        void onCallback(List<Project> projects);
    }

    public interface FirestoreImagesCallback {
        void onCallback(List<Gallery> images);
    }


}
