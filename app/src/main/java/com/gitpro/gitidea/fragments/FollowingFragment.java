package com.gitpro.gitidea.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gitpro.gitidea.R;
import com.gitpro.gitidea.adapters.FollowersAndFollowingAdapter;
import com.gitpro.gitidea.models.users.FollowersAndFollowing;
import com.gitpro.gitidea.network.AllApiService;
import com.gitpro.gitidea.utils.UtilsManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowingFragment  extends Fragment {

    private static final FollowersFragment FOLLOWING_FRAGMENT = null;
    private static String FOLLOWERS_AND_FOLLOWING = "https://api.github.com/users/";
    private static String username = "";
    private ArrayList<FollowersAndFollowing> followingList;
    private RecyclerView followersRecyclerView;
    private AllApiService apiService;
    private UtilsManager utilsManager;
    private TextView NoData;
    private ImageView NoDataIV;
    private static ProgressBar progressBar;


    public FollowingFragment(){

    }

    public static Fragment getInstance(String userName) {
        setData(userName);
        if (FOLLOWING_FRAGMENT == null) return new FollowingFragment();
        else return FOLLOWING_FRAGMENT;
    }

    public static void setData(String userName) {
        username = userName.split("/")[0];
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_following, container, false);
        init(view);
        return view;
    }
    private void init(View view) {
        progressBar = view.findViewById(R.id.progress);
        NoData = view.findViewById(R.id.NoDataMessage);
        followersRecyclerView = view.findViewById(R.id.mRecyclerView);
        NoDataIV = view.findViewById(R.id.NoDataIV);
        followingList = new ArrayList<>();
        utilsManager = new UtilsManager(getContext());
    }

    private void bindUIWithComponents() {
        new BackgroundDataLoad(FOLLOWERS_AND_FOLLOWING).execute();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) bindUIWithComponents();
    }

    private void loadListView(){
        FollowersAndFollowingAdapter followersAdapter = new FollowersAndFollowingAdapter(getContext(), followingList,new FollowersAndFollowingAdapter.onItemClickListener() {
            @Override
            public void respond(FollowersAndFollowing followersAndFollowing) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse(followersAndFollowing.getHtmlUrl()));
                startActivity(browserIntent);
            }
        });
        followersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        followersRecyclerView.setAdapter(followersAdapter);
        followersAdapter.notifyDataSetChanged();
    }

    private class BackgroundDataLoad extends AsyncTask<String, Void, String> {
        String url ;

        public BackgroundDataLoad( String url) {
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            loadRecord(url);
            return "done";
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("done")){
                Log.v("result async task :: ",result);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        if (followingList.size()>0){
                            loadListView();
                            NoData.setVisibility(View.GONE);
                            NoDataIV.setVisibility(View.GONE);
                        }
                        else {
                            NoData.setVisibility(View.VISIBLE);
                            NoDataIV.setVisibility(View.VISIBLE);
                            Toast.makeText(getContext(), R.string.no_following_found,Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                }, 3000);
            }
        }

    }

    private void loadRecord(String url) {
        followingList.clear();
        //Creating the instance for api service from AllApiService interface
        apiService=utilsManager.getClient(url).create(AllApiService.class);
        final Call<ArrayList<FollowersAndFollowing>> followersAndFollowingCall=apiService.getFollowersAndFollowing(url+username+"/following");
        //handling user requests and their interactions with the application.
        followersAndFollowingCall.enqueue(new Callback<ArrayList<FollowersAndFollowing>>() {
            @Override
            public void onResponse(Call<ArrayList<FollowersAndFollowing>> call, Response<ArrayList<FollowersAndFollowing>> response) {
                try{
                    for (int start=0;start<response.body().size();start++) {
                        FollowersAndFollowing followers = response.body().get(start);
                        followingList.add(followers);
                    }
                }
                catch (Exception e){
                    Log.v("Error::::",e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<FollowersAndFollowing>> call, Throwable t) {
            }
        });

    }

}
