package com.gitpro.gitidea.network;

import com.gitpro.gitidea.models.languages.TrendingLanguage;
import com.gitpro.gitidea.models.repos.Repo;
import com.gitpro.gitidea.models.repos.TrendingRepositories;
import com.gitpro.gitidea.models.topics.TopicBase;
import com.gitpro.gitidea.models.users.FollowersAndFollowing;
import com.gitpro.gitidea.models.users.QuestionBank;
import com.gitpro.gitidea.models.users.UserBase;
import com.gitpro.gitidea.models.users.Developer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface AllApiService {
    //Call for trending repositories
    @GET
    Call<List<TrendingRepositories>> getTrendingRepos(@Url String url);

    //Call for trending languages
    @GET
    Call<ArrayList<TrendingLanguage>> getTrendingLanguages(@Url String url);

    //Call for trending developers
    @GET
    Call<UserBase> getTrendingDevelopers(@Url String url, @Query("q") String q);

    //Call for topics android
    @GET
    Call<TopicBase> getAllTopics(@Url String url, @Query("q") String q);

    //Call for followers and following list
    @GET
    Call<ArrayList<FollowersAndFollowing>> getFollowersAndFollowing(@Url String url);

    //Call for question bank
    @GET
    Call<QuestionBank> getAllQuestionAndAnswer(@Url String url);

    //Get user details
    @GET
    Call<Developer> getSingleUser(@Url String url);

    //Get user repositories
    @GET
    Call<List<Repo>> getAllRepositories(@Url String url);
}