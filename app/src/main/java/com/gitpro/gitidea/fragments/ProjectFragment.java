package com.gitpro.gitidea.fragments;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gitpro.gitidea.FireStoreQueries;
import com.gitpro.gitidea.R;
import com.gitpro.gitidea.adapters.ProjectAdapter;
import com.gitpro.gitidea.models.Project;

import java.util.ArrayList;
import java.util.List;


public class ProjectFragment extends Fragment {

    RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    List<Project> projectList=new ArrayList<>();
    RelativeLayout relativeLayout;
    ProjectAdapter adapter;

    public ProjectFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.project_fragment, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
            initProjectList(getActivity(),view);
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    initProjectList(getActivity(),view);
                }
            });

    }


    public void initProjectList(Activity activity, View view){
        recyclerView=view.findViewById(R.id.project_rv);
        refreshLayout=view.findViewById(R.id.refresh_project);
        relativeLayout=view.findViewById(R.id.project_container);

        FireStoreQueries.getProjects(new FireStoreQueries.FirestoreProjectCallback() {
            @Override
            public void onCallback(List<Project> projects) {
                adapter=null;
                projectList=projects;
                adapter=new ProjectAdapter(activity,projectList);
                recyclerView.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false));
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                if (refreshLayout.isRefreshing()){
                    refreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void addTransitionEffect() {
        Animation animation=new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(400);
        getView().startAnimation(animation);
    }

    @Override
    public void onStart() {
        super.onStart();
// I'm using null here because drawing nothing is faster than drawing transparent pixels.
        getActivity().getWindow().setBackgroundDrawable(null);
        getView().setBackground(null);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(getActivity().getColor(R.color.black_soft)));
    }


}