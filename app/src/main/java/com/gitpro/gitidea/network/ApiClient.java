package com.gitpro.gitidea.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String URL="https://newsapi.org/v2/";
    private static ApiClient apiClient;
    private Retrofit mRetrofit;

    public ApiClient (){
        mRetrofit=new Retrofit.Builder().baseUrl(URL).addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static synchronized ApiClient getInstance(){
        if (apiClient==null){
            apiClient=new ApiClient();
        }
        return apiClient;
    }
public NewsApi getApi(){
        return mRetrofit.create(NewsApi.class);
}

}
