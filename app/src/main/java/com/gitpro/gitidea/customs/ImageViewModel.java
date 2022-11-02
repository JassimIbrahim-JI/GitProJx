package com.gitpro.gitidea.customs;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.gitpro.gitidea.models.Gallery;
import com.gitpro.gitidea.models.Hit;
import com.gitpro.gitidea.network.ImageInstance;
import com.gitpro.gitidea.network.imageApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageViewModel extends AndroidViewModel {

    private MutableLiveData<List<Gallery>>liveDataGallery;

    public MutableLiveData<List<Gallery>> getLiveDataGallery() {
        return liveDataGallery;
    }

    public ImageViewModel(@NonNull Application application) {
        super(application);
        liveDataGallery=new MutableLiveData<>();
    }

    public void makeCallApi(){
        imageApi api= ImageInstance.getImageClient().create(imageApi.class);
        Call<Hit>call=api.callAllImage("photo","night",1,50);
        call.enqueue(new Callback<Hit>() {
            @Override
            public void onResponse(Call<Hit> call, Response<Hit> response) {
                //200 its mean response return successful
                if (response.code()==200) {
                    List<Gallery>galleries=response.body().getHits();
                    if (galleries!=null&&response.body()!=null) {
                        Log.v("TAG","DownloadedImage: "+response.body().getHits().get(0).getLargeImageURL());
                        liveDataGallery.postValue(response.body().getHits());
                    }
                }
            }

            @Override
            public void onFailure(Call<Hit> call, Throwable t) {
                Log.v("TAG","onFailure: "+t.getMessage());
                liveDataGallery.setValue(null);
            }
        });
    }

}
