package com.gitpro.gitidea.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gitpro.gitidea.R;
import com.gitpro.gitidea.activities.ExploreActivity;
import com.gitpro.gitidea.adapters.GroupAdapter;
import com.gitpro.gitidea.models.Articles;
import com.gitpro.gitidea.models.Group;
import com.gitpro.gitidea.models.topics.Item;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements GroupAdapter.itemClickListener {

RecyclerView recyclerView;
List<Group>groupList;
GroupAdapter groupAdapter;
ArrayList<Item>recommendedList;
List<Articles>featuredList;
SearchView searchView;
ExploreActivity exploreActivity;
SwipeRefreshLayout swipeRefreshLayout;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

     setGroupAdapterType(view);
     setSearchView(view);

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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        groupAdapter=new GroupAdapter(exploreActivity,initGroupData(),initFeaturedData(),initRecommendedData(),this);
        recyclerView.setAdapter(groupAdapter);

        if (swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public List<Group>initGroupData(){
        groupList=new ArrayList<>();
        groupList.add(new Group("Top Rated","Discover"));
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

    public void setSearchView(View view){

        searchView=view.findViewById(R.id.search_items);
              searchView.setQueryHint("Search Here... ");
              searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                  @Override
                  public boolean onQueryTextSubmit(String query) {
                      return false;
                  }

                  @Override
                  public boolean onQueryTextChange(String newText) {
                      groupAdapter.getFilter().filter(newText);
                      return false;
                  }
              });
    }





    @Override
    public void itemSelected(Articles groupModel) {

    }
}