package com.gitpro.gitidea.models.repos;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.gitpro.gitidea.models.topics.Item;
import com.gitpro.gitidea.models.topics.TopicBase;
import com.gitpro.gitidea.network.AllApiService;
import com.gitpro.gitidea.utils.UtilsManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AndroidGitRepository {
    private AllApiService apiService;
    private UtilsManager utilsManager;
    public static String ALL_TOPICS_BASE_URL = "https://api.github.com/search/";
    //region singleton instance
    private static AndroidGitRepository instance = null;

    public static AndroidGitRepository getInstance(){
        if (instance == null){
            instance = new AndroidGitRepository();
        }
        return instance;
    }
    //endregion

    public MutableLiveData<List<Item>> getAndroidRepos(Context context, String url, String queryString){
        final MutableLiveData<List<Item>> androidRepos = new MutableLiveData<>();
        utilsManager = new UtilsManager(context);
        apiService = utilsManager.getClient(ALL_TOPICS_BASE_URL).create(AllApiService.class);
        final Call<TopicBase> androidTopicCall=apiService.getAllTopics(url+"repositories",queryString);
        androidTopicCall.enqueue(new Callback<TopicBase>() {
            @Override
            public void onResponse(Call<TopicBase> call, Response<TopicBase> response) {
                if (response.isSuccessful()){
                    if (response.body().getItems() != null){
                        if (response.body().getItems().size() > 0){
                            androidRepos.setValue(response.body().getItems());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<TopicBase> call, Throwable t) {
                androidRepos.setValue(null);
            }
        });
        return androidRepos;
    }
}