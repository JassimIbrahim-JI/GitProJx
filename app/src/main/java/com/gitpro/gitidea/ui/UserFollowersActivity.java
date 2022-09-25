package com.gitpro.gitidea.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gitpro.gitidea.R;
import com.gitpro.gitidea.adapters.UserAdapter;
import com.gitpro.gitidea.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserFollowersActivity extends AppCompatActivity {

   private UserAdapter userAdapter;
    private List<User> mUsers;
    private FirebaseFirestore db;
    RecyclerView following_rv;
    List<String>idList;
    String id,title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_followers);

        db =FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        id = intent.getStringExtra("userId");
        title = intent.getStringExtra("tag");

        Toolbar toolbar = findViewById(R.id.followers_tb);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        following_rv = findViewById(R.id.followers_rv);
        following_rv.setHasFixedSize(true);
        following_rv.setLayoutManager(new LinearLayoutManager(this));
        mUsers = new ArrayList<>();
        userAdapter = new UserAdapter( mUsers, this,false);
        following_rv.setAdapter(userAdapter);

        idList = new ArrayList<>();

        switch (title) {
            case "followerPage" :
                getFollowers();
                break;

            case "followingPage":
                getFollowings();
                break;

        }
    }

    private void getFollowers() {

        db.collection("users/"+id+"/followers")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                idList.clear();
                for (QueryDocumentSnapshot snapshot:task.getResult()){
                    idList.add(snapshot.getId());
                }
                showUsers();
            }
        });
        
    }

    private void getFollowings() {

        db.collection("users/"+id+"/followings")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                  idList.clear();
                  for (QueryDocumentSnapshot snapshot:task.getResult()){
                      idList.add(snapshot.getId());
                  }
                  showUsers();
            }
        });
    
    }

    private void showUsers() {
        db.collection("users")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
           mUsers.clear();
           for (QueryDocumentSnapshot snapshot:task.getResult()){
               User user=snapshot.toObject(User.class);
               for (String id:idList){
                   if (user.userId.equals(id)) {
                       mUsers.add(user);
                   }
               }
           }
                Log.d("list Followers", mUsers.toString());
                userAdapter.notifyDataSetChanged();
            }
        });
    }

}