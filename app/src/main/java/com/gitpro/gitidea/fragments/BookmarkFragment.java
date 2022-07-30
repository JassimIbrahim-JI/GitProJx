package com.gitpro.gitidea.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gitpro.gitidea.FireStoreQueries;
import com.gitpro.gitidea.R;
import com.gitpro.gitidea.activities.DetailedActivity;
import com.gitpro.gitidea.activities.ExploreActivity;
import com.gitpro.gitidea.adapters.TopicAdapter;
import com.gitpro.gitidea.models.Topic;
import com.gitpro.gitidea.models.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BookmarkFragment extends Fragment implements TopicAdapter.mClickListener, TopicAdapter.ItemClickListener {



    List<Topic> mTopics = new ArrayList<>();
    RecyclerView mRecyclerViewRecommended;
    RecyclerView.LayoutManager mManger;
    View mView;
    int countNum=0;
    TextView textView;
    TopicAdapter bookmarkAdapter;
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

    public BookmarkFragment(){

    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       mView=inflater.inflate(R.layout.fragment_bookmark,container,false);

        firebase_connection();

        swipeRefreshLayout = mView.findViewById(R.id.refresh_recommended);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                firebase_connection();
            }
        });

        return mView;
    }

    private void firebase_connection() {

        textView=mView.findViewById(R.id.test_topic);
        FireStoreQueries.getUser(new FireStoreQueries.FirestoreUsersCallback() {
            @Override
            public void onCallback(User user) {
                mUser=user;
            }
        });

                FireStoreQueries.getTopics(new FireStoreQueries.FirestoreTopicCallback() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onCallback(ArrayList<Topic> topics) {
                       mTopics = null;
                        mTopics = topics;
                        List<String> userKeywords = (List<String>) mUser.bookMark;
                        List<Topic> bookmarkTopics = new ArrayList<>();
                        for (int i = 0; i < userKeywords.size(); i++) {
                            for (int j = 0; j < mTopics.size(); j++) {
                                   if (userKeywords.get(i).contains(topics.get(j).topicId))
                                    if (!bookmarkTopics.contains(mTopics.get(j)))
                                        bookmarkTopics.add(mTopics.get(j));
                            }
                        }
                        if (bookmarkTopics.size() == 0) {
                            textView.setVisibility(View.VISIBLE);
                        }
                        else {
                            textView.setVisibility(View.INVISIBLE);

                        }

                        bookmarkAdapter = new TopicAdapter(mActivity,bookmarkTopics,BookmarkFragment.this::onCallBackItem, BookmarkFragment.this::longClick);
                        mManger = new LinearLayoutManager(mActivity);
                        mRecyclerViewRecommended = mView.findViewById(R.id.rv_recommended);
                        mRecyclerViewRecommended.setLayoutManager(mManger);
                        mRecyclerViewRecommended.setAdapter(bookmarkAdapter);
                        bookmarkAdapter.notifyDataSetChanged();

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

    public void setSelection(ImageView selection,int pos,View itemView){
     Topic topic=mTopics.get(pos);
     if (selection.getVisibility()==View.GONE){
         selection.setVisibility(View.VISIBLE);
         mTopics.add(topic);
         countNum++;
         upDateText(countNum);
         itemView.setBackgroundColor(Color.LTGRAY);
     }

     else {
         selection.setVisibility(View.GONE);
         mTopics.remove(topic);
         countNum--;
         upDateText(countNum);
         itemView.setBackgroundColor(Color.WHITE);

     }

    }

    @Override
    public boolean longClick(boolean Click) {

        return Click;
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
        bundle.putString("date",topic.date);
        bundle.putInt("commentNum", topic.commentsNum);
        bundle.putString("likeNum", TopicAdapter.cTag);
        bundle.putString("ivlike",TopicAdapter.mTag);
        bundle.putSerializable("comments", (Serializable) topic.comments);
        intent.putExtras(bundle);
        getActivity().startActivity(intent);
    }
    private void addTransitionEffect() {
        Animation animation=new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(400);
        getView().startAnimation(animation);
    }
}
