package com.gitpro.gitidea.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gitpro.gitidea.R;
import com.gitpro.gitidea.adapters.GroupAdapter;
import com.gitpro.gitidea.models.Articles;
import com.gitpro.gitidea.models.Group;
import com.gitpro.gitidea.models.topics.Item;
import com.gitpro.gitidea.ui.ExploreActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements GroupAdapter.itemClickListener {

    RecyclerView recyclerView;
   private List<Group>groupList;
    private GroupAdapter groupAdapter;
    ArrayList<Item>recommendedList;
    private List<Articles>featuredList;
    ExploreActivity exploreActivity;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView textDev;
    private FirebaseUser mUsr;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mUsr= FirebaseAuth.getInstance().getCurrentUser();
        return inflater.inflate(R.layout.fragment_blank, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

     setGroupAdapterType(view);

    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
         @Override
         public void onRefresh() {
             setGroupAdapterType(view);
         }
     });

        //onScrollHide();

    }


    public void setGroupAdapterType(View view){

        swipeRefreshLayout=view.findViewById(R.id.refresh_group);
       recyclerView=view.findViewById(R.id.home_rv);
       textDev=view.findViewById(R.id.texDev);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                  if (mUsr!=null) {
                 textDev.setText("Hi " + mUsr.getDisplayName());
                    }


        groupAdapter=new GroupAdapter(exploreActivity,initGroupData(),initFeaturedData(),initRecommendedData(),this);
        recyclerView.setAdapter(groupAdapter);

        if (swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public List<Group>initGroupData(){
        groupList=new ArrayList<>();
        groupList.add(new Group("Repositories","Discover"));
        groupList.add(new Group("News","Explore"));
        return groupList;
    }
    public List<Articles>initFeaturedData(){
      featuredList=new ArrayList<>();

        return featuredList;
    }
    public ArrayList<Item>initRecommendedData(){
        recommendedList=new ArrayList<>();

        return recommendedList;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        exploreActivity =(ExploreActivity) context;
    }

    @Override
    public void itemSelected(Articles groupModel) {

    }
}