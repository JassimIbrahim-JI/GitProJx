package com.gitpro.gitidea.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.gitpro.gitidea.utils.FireStoreQueries;
import com.gitpro.gitidea.R;
import com.gitpro.gitidea.ui.DetailsProjectActivity;
import com.gitpro.gitidea.adapters.ProjectAdapter;
import com.gitpro.gitidea.models.Project;
import com.google.firebase.auth.FirebaseAuth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ProjectFragment extends Fragment implements ProjectAdapter.ItemClickProjectListener{

    RecyclerView project_rv;
    SwipeRefreshLayout refreshLayout;
    List<Project> projectList=new ArrayList<>();
    RelativeLayout relativeLayout;
    ProjectAdapter adapter;
    ShimmerFrameLayout frameLayout;
    TextView noDataTv;
    ImageView noDataIm;
    private FirebaseAuth mAuth;

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
        project_rv =view.findViewById(R.id.project_rv);
        refreshLayout=view.findViewById(R.id.refresh_project);
        relativeLayout=view.findViewById(R.id.project_container);
        noDataIm=view.findViewById(R.id.no_data_iv_p);
        noDataTv=view.findViewById(R.id.no_data_tv_p);
        frameLayout=view.findViewById(R.id.shimmerFrameLayout_fra_project);
        mAuth=FirebaseAuth.getInstance();
        frameLayout.setVisibility(View.VISIBLE);
        frameLayout.startShimmerAnimation();

        if (mAuth.getCurrentUser()!=null){
            FireStoreQueries.getProjects(new FireStoreQueries.FirestoreProjectCallback() {
                @Override
                public void onCallback(List<Project> projects) {
                    frameLayout.setVisibility(View.GONE);
                    frameLayout.stopShimmerAnimation();
                    adapter=null;
                    projectList=projects;
                    if (projectList!=null) {
                        adapter = new ProjectAdapter(activity, projectList, ProjectFragment.this::onCallBackItem);
                        project_rv.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
                        project_rv.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                      noDataVisibility(false);
                    }
                    else {
                        Toast.makeText(getActivity(), getString(R.string.no_data_message), Toast.LENGTH_LONG).show();
                        noDataVisibility(true);
                    }

                    if (refreshLayout.isRefreshing()){
                        refreshLayout.setRefreshing(false);
                    }
                }
            });
        }
    }

    private void noDataVisibility(boolean shouldVisible){
        if (shouldVisible) {
            project_rv.setVisibility(View.GONE);
            frameLayout.setVisibility(View.GONE);
            frameLayout.stopShimmerAnimation();
            noDataTv.setVisibility(View.VISIBLE);
            noDataIm.setVisibility(View.VISIBLE);
        } else {
            project_rv.setVisibility(View.VISIBLE);
            noDataTv.setVisibility(View.GONE);
            noDataIm.setVisibility(View.GONE);
        }
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


    @Override
    public void onCallBackItem(Project project) {
        addTransitionEffect();
        Intent intent=new Intent(getActivity(), DetailsProjectActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("usernameP",project.mUser);
        bundle.putString("descP",project.pDescription);
        bundle.putString("urlPreview",project.urlPreview);
        bundle.putString("urlP",project.url);
        bundle.putString("projectId",project.projectId);
        bundle.putString("userIdP", ProjectAdapter.userId);
        bundle.putString("dateP",project.pDate);
        bundle.putInt("commentNumP", project.commentsNum);
        bundle.putString("ivCommentP",ProjectAdapter.tTag);
        bundle.putString("ivlikeP", ProjectAdapter.mTag);
        bundle.putString("likeNumP", ProjectAdapter.cTag);
        bundle.putSerializable("commentsP", (Serializable) project.comments);
        intent.putExtras(bundle);
        getActivity().startActivity(intent);
    }
}