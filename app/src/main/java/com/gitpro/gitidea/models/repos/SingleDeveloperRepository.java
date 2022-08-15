package com.gitpro.gitidea.models.repos;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.gitpro.gitidea.models.users.Developer;
import com.gitpro.gitidea.network.AllApiService;
import com.gitpro.gitidea.utils.UtilsManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SingleDeveloperRepository {
    private static SingleDeveloperRepository instance = null;
    private UtilsManager utilsManager;
    private AllApiService apiService;
    private static final String SINGLE_USER_PROFILE_AND_REPOSITORIES_URL="https://api.github.com/users/";

    public static SingleDeveloperRepository getInstance(){
        if (instance == null){
            instance = new SingleDeveloperRepository();
        }
        return instance;
    }

    public MutableLiveData<Developer> getDeveloper(Context context, String url){
        final MutableLiveData<Developer> developer = new MutableLiveData<>();
        utilsManager = new UtilsManager(context);
        apiService = utilsManager.getClient(SINGLE_USER_PROFILE_AND_REPOSITORIES_URL).create(AllApiService.class);
        final Call<Developer> trendingDevelopersCall = apiService.getSingleUser(url);
        trendingDevelopersCall.enqueue(new Callback<Developer>() {
            @Override
            public void onResponse(Call<Developer> call, Response<Developer> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        developer.setValue(response.body());
                    }
                }
            }

            @Override
            public void onFailure(Call<Developer> call, Throwable t) {
                developer.setValue(null);
            }
        });
        return developer;
    }


}
