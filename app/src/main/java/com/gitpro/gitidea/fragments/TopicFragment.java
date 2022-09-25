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
import com.gitpro.gitidea.R;
import com.gitpro.gitidea.adapters.TopicAdapter;
import com.gitpro.gitidea.models.Topic;
import com.gitpro.gitidea.ui.DetailsTopicActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TopicFragment extends Fragment implements TopicAdapter.ItemClickListener, TopicAdapter.mClickListener{

    RecyclerView topic_rv;
    private TopicAdapter featuredAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<Topic> mTopics = new ArrayList<>();
    RelativeLayout containers;
    ShimmerFrameLayout frameLayout;
    private FirebaseAuth mAuth;
    TextView noDataTv;
    ImageView noDataIm;
    List<String>followingList;
    private FirebaseFirestore fStore;
    View v;

    public TopicFragment() {
        // Required empty public constructor

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            v=inflater.inflate(R.layout.topic_fragment,container,false);
            fStore=FirebaseFirestore.getInstance();
             mAuth=FirebaseAuth.getInstance();



        noDataIm=v.findViewById(R.id.no_data_iv);
        noDataTv=v.findViewById(R.id.no_data_tv);
        topic_rv=v.findViewById(R.id.featured_recycler);
        swipeRefreshLayout = v.findViewById(R.id.refresh_topic);
        containers=v.findViewById(R.id.topic_container);
        frameLayout=v.findViewById(R.id.shimmerFrameLayout_fra);
        followingList=new ArrayList<>();

        checkFollowing();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkFollowing();
            }
        });

        return v;

    }

    public void checkFollowing(){

        fStore.collection("users/"+mAuth.getCurrentUser().getUid()+"/followings")
               .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                followingList.clear();
               for (QueryDocumentSnapshot snapshot:task.getResult()){
                   followingList.add(snapshot.getId());
               }
               followingList.add(mAuth.getCurrentUser().getUid());
               attachFeaturedItems(getActivity());

            }
        });

    }

    public void attachFeaturedItems(Activity activity){
        frameLayout.setVisibility(View.VISIBLE);
        frameLayout.startShimmerAnimation();

        if (mAuth.getCurrentUser()!=null) {
            fStore.collection("topics")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                       frameLayout.setVisibility(View.GONE);
                       frameLayout.stopShimmerAnimation();
                       featuredAdapter = null;
                       mTopics.clear();
                       for (QueryDocumentSnapshot snapshot: task.getResult()) {
                           Topic topic = snapshot.toObject(Topic.class);
                           mTopics.add(topic);
                       }

                       if (mTopics!=null){
                           featuredAdapter = new TopicAdapter(activity, mTopics, TopicFragment.this, TopicFragment.this::longClick);
                           topic_rv.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
                           featuredAdapter.notifyDataSetChanged();
                           topic_rv.setAdapter(featuredAdapter);
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
        Intent intent=new Intent(getActivity(), DetailsTopicActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("username",topic.userName);
        bundle.putString("desc",topic.pDescription);
        bundle.putString("image",topic.pImage);
        bundle.putString("url",topic.imageProfile);
        bundle.putString("topicId",topic.topicId);
        bundle.putString("userId",TopicAdapter.userId);
        bundle.putString("date",topic.date);
        bundle.putInt("commentNum", topic.commentsNum);
        bundle.putString("ivComment",TopicAdapter.tTag);
        bundle.putString("ivlike", TopicAdapter.mTag);
        bundle.putString("likeNum", TopicAdapter.cTag);
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