package com.gitpro.gitidea.models.viewmodels;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.gitpro.gitidea.models.repos.SingleDeveloperRepository;
import com.gitpro.gitidea.models.users.Developer;

public class SingleDeveloperViewModel extends AndroidViewModel {
    private MutableLiveData<Developer> developer;
    private SingleDeveloperRepository developerRepository;

    public SingleDeveloperViewModel(@NonNull Application application) {
        super(application);
    }

    public void getData(Context context, String url){
        developerRepository = SingleDeveloperRepository.getInstance();
        developer = developerRepository.getDeveloper(context, url);
    }

    public MutableLiveData<Developer> getDeveloper() {
        return developer;
    }
}