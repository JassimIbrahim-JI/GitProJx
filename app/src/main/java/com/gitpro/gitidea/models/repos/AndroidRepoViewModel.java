package com.gitpro.gitidea.models.repos;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.gitpro.gitidea.models.topics.Item;

import java.util.List;

public class AndroidRepoViewModel extends AndroidViewModel {
    private MutableLiveData<List<Item>> androidRepos;
    private AndroidGitRepository androidRepository;

    public AndroidRepoViewModel(@NonNull Application application) {
        super(application);
    }

    public void getData(Context context, String url, String queryString){
        androidRepository = AndroidGitRepository.getInstance();
        androidRepos = androidRepository.getAndroidRepos(context, url, queryString);
    }

    public MutableLiveData<List<Item>> getAndroidRepos() {
        return androidRepos;
    }
}
