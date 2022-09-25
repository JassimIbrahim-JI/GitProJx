package com.gitpro.gitidea.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gitpro.gitidea.utils.FireStoreQueries;
import com.gitpro.gitidea.R;
import com.gitpro.gitidea.ui.DetailsProjectActivity;
import com.gitpro.gitidea.ui.ExploreActivity;
import com.gitpro.gitidea.adapters.ProjectAdapter;
import com.gitpro.gitidea.models.Project;
import com.gitpro.gitidea.models.User;

import java.util.ArrayList;
import java.util.List;

public class BookmarkProject extends Fragment implements ProjectAdapter.ItemClickProjectListener {

    List<Project> mProjects = new ArrayList<>();
    RecyclerView mRecyclerViewRecommended;
    RecyclerView.LayoutManager mManger;
    View mView;
    int countNum=0;
    TextView textView;
    ProjectAdapter bookmarkAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    ExploreActivity mActivity;
    Toolbar toolbar;
    User mUser;
    TextView itemCounter;
    public boolean isActionModeEnabled=false;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity=(ExploreActivity) getActivity();

    }

    public BookmarkProject(){

    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView=inflater.inflate(R.layout.fragment_bookmark_project,container,false);
        swipeRefreshLayout = mView.findViewById(R.id.refresh_recommendedp);

        firebase_connection();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                firebase_connection();
            }
        });

        return mView;
    }

    private void firebase_connection() {

        textView=mView.findViewById(R.id.test_project);
        FireStoreQueries.getUser(new FireStoreQueries.FirestoreUsersCallback() {
            @Override
            public void onCallback(User user) {
                mUser=user;
            }
        });

        FireStoreQueries.getProjects(new FireStoreQueries.FirestoreProjectCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onCallback(List<Project> projects) {
                mProjects = null;
                mProjects = projects;
                ArrayList<String> userKeywords =(ArrayList<String>) mUser.bookMark2;
                List<Project> bookmarkTopics = new ArrayList<>();
                for (int i = 0; i <userKeywords.size(); i++) {
                    for (int j = 0; j < mProjects.size(); j++) {
                        if (userKeywords.get(i).contains(mProjects.get(j).projectId))
                            if (!bookmarkTopics.contains(mProjects.get(j))) {
                                bookmarkTopics.add(mProjects.get(j));
                            }

                    }
                }
                bookmarkAdapter = new ProjectAdapter(mActivity,bookmarkTopics,BookmarkProject.this);
                mManger = new LinearLayoutManager(mActivity);
                mRecyclerViewRecommended = mView.findViewById(R.id.rv_recommendedp);
                mRecyclerViewRecommended.setLayoutManager(mManger);
                mRecyclerViewRecommended.setAdapter(bookmarkAdapter);
                bookmarkAdapter.notifyDataSetChanged();


                if (bookmarkTopics.size() == 0) {
                    textView.setVisibility(View.VISIBLE);
                }
                else {
                    textView.setVisibility(View.INVISIBLE);
                }

                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

    }

    public  void  upDateText(int counter){
        if (counter==0){
            itemCounter.setText(0 + "  Selected");
        }
        else {
            itemCounter.setText(counter + "  Selected");
        }
    }




    private void addTransitionEffect() {
        Animation animation=new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(400);
        getView().startAnimation(animation);
    }

    @Override
    public void onCallBackItem(Project project) {
        addTransitionEffect();
        Intent intent=new Intent(getActivity(), DetailsProjectActivity.class);
        getActivity().startActivity(intent);
    }
}
