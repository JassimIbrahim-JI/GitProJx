package com.gitpro.gitidea.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gitpro.gitidea.R;
import com.gitpro.gitidea.adapters.ActivitiesAdapter;
import com.gitpro.gitidea.adapters.TopicAdapter;
import com.gitpro.gitidea.models.Group;
import com.gitpro.gitidea.models.Project;
import com.gitpro.gitidea.models.Topic;
import com.gitpro.gitidea.ui.DetailsTopicActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActivitiesFragment extends Fragment implements TopicAdapter.mClickListener, TopicAdapter.ItemClickListener {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager mManger;
    List<Topic>topics=new ArrayList<>();
    List<Project>mProjectList=new ArrayList<Project>();
    List<Group>activities;
    ActivitiesAdapter mActivitiesAdapter;
    private String profileId;
    private FirebaseFirestore db;
    private FirebaseUser mUser;

    public ActivitiesFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       db=FirebaseFirestore.getInstance();
       mUser= FirebaseAuth.getInstance().getCurrentUser();
        return inflater.inflate(R.layout.fragment_activities,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout=view.findViewById(R.id.refresh_activities);
        recyclerView=view.findViewById(R.id.rv_activities);

        String data=getContext().getSharedPreferences("PROFILE",Context.MODE_PRIVATE).getString("profileId","none");
        if (data.equals("none")){
            profileId=mUser.getUid();
        }
        else {
            profileId=data;
            getContext().getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit().clear().apply();
        }

       firebase_connection();
        mActivitiesAdapter = new ActivitiesAdapter(initGroupData(),topics,mProjectList,getActivity());
        mManger = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mManger);
        recyclerView.setAdapter(mActivitiesAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               firebase_connection();
            }
        });
    }

    public List<Group>initGroupData() {
        activities = new ArrayList<>();
        activities.add(new Group("Topics",""));
        activities.add(new Group("Projects",""));
        return activities;
    }


//display user topics and projects in profile activity
   public void firebase_connection(){

        db.collection("topics").get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                      topics.clear();
                      for (QueryDocumentSnapshot snapshot:task.getResult()){
                          Topic topic=snapshot.toObject(Topic.class);
                          if (topic.publisherBy.equals(ProfileFragment.profileId)){
                              topics.add(topic);
                          }
                      }
                        Collections.reverse(topics);
                        mActivitiesAdapter.notifyDataSetChanged();

                    }
                });


   if (swipeRefreshLayout.isRefreshing()){
       swipeRefreshLayout.setRefreshing(false);
      }

   }//END OF FUN

    @Override
    public boolean longClick(boolean Click) {

        return Click;
    }

    @Override
    public void onCallBackItem(Topic topic) {
        addTransitionEffect();
        Intent intent=new Intent(getActivity(), DetailsTopicActivity.class);
        getActivity().startActivity(intent);
    }
    private void addTransitionEffect() {
        Animation animation=new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(400);
        getView().startAnimation(animation);
    }

}
