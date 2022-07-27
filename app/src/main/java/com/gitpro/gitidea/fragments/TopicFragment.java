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
import com.gitpro.gitidea.FireStoreQueries;
import com.gitpro.gitidea.R;
import com.gitpro.gitidea.activities.DetailedActivity;
import com.gitpro.gitidea.adapters.TopicAdapter;
import com.gitpro.gitidea.models.Topic;
import com.google.firebase.auth.FirebaseAuth;

import java.io.Serializable;
import java.util.ArrayList;

public class TopicFragment extends Fragment implements TopicAdapter.ItemClickListener, TopicAdapter.mClickListener{

    RecyclerView topic_rv;
    private TopicAdapter featuredAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<Topic> mTopics = new ArrayList<>();
    RelativeLayout container;
    ShimmerFrameLayout frameLayout;
    private FirebaseAuth mAuth;
    TextView noDataTv;
    ImageView noDataIm;

    public TopicFragment() {
        // Required empty public constructor

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        return inflater.inflate(R.layout.topic_fragment,container,false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        attachFeaturedItems(getActivity(),view);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                attachFeaturedItems(getActivity(),view);
            }
        });
    }
    public void attachFeaturedItems(Activity activity, View v){

        noDataIm=v.findViewById(R.id.no_data_iv);
        noDataTv=v.findViewById(R.id.no_data_tv);
        topic_rv=v.findViewById(R.id.featured_recycler);
        swipeRefreshLayout = v.findViewById(R.id.refresh_topic);
        container=v.findViewById(R.id.topic_container);
       frameLayout=v.findViewById(R.id.shimmerFrameLayout_fra);
        mAuth=FirebaseAuth.getInstance();
        frameLayout.setVisibility(View.VISIBLE);
        frameLayout.startShimmerAnimation();

        if (mAuth.getCurrentUser()!=null) {
            FireStoreQueries.getTopics(new FireStoreQueries.FirestoreTopicCallback() {
                @Override
                public void onCallback(ArrayList<Topic> topics) {
                    frameLayout.setVisibility(View.GONE);
                    frameLayout.stopShimmerAnimation();
                    featuredAdapter = null;
                    mTopics = topics;
                    if (mTopics!=null){
                        featuredAdapter = new TopicAdapter(activity, mTopics, TopicFragment.this, TopicFragment.this::longClick);
                        topic_rv.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
                        topic_rv.setAdapter(featuredAdapter);
                        featuredAdapter.notifyDataSetChanged();
                          noDataVisibility(false);
                    }
                    else {
                        Toast.makeText(getActivity(), getString(R.string.no_data_message), Toast.LENGTH_LONG).show();
                        noDataVisibility(true);
                    }

               if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
                }
               }
            });
         }
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

    private void noDataVisibility(boolean shouldVisible){
        if (shouldVisible) {
            topic_rv.setVisibility(View.GONE);
            frameLayout.setVisibility(View.GONE);
            frameLayout.stopShimmerAnimation();
            noDataTv.setVisibility(View.VISIBLE);
            noDataIm.setVisibility(View.VISIBLE);
        } else {
            topic_rv.setVisibility(View.VISIBLE);
            noDataTv.setVisibility(View.GONE);
            noDataIm.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCallBackItem(Topic topic) {
        addTransitionEffect();
        Intent intent=new Intent(getActivity(), DetailedActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("username",topic.userName);
        bundle.putString("desc",topic.pDescription);
        bundle.putString("image",topic.pImage);
        bundle.putString("url",topic.imageProfile);
        bundle.putString("bTopicId",topic.topicId);
        bundle.putString("userId",TopicAdapter.userId);
        bundle.putString("date",topic.date);
        bundle.putInt("commentNum", topic.commentsNum);
        bundle.putString("likeNum", TopicAdapter.cTag);
        bundle.putString("ivlike", TopicAdapter.mTag);
        bundle.putString("ivComment",TopicAdapter.tTag);
        bundle.putSerializable("comments", (Serializable) topic.comments);
        intent.putExtras(bundle);
        getActivity().startActivity(intent);
    }

    private void addTransitionEffect() {
        Animation animation=new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(400);
        getView().startAnimation(animation);
    }

    @Override
    public boolean longClick(boolean click) {
        return false;
    }
}