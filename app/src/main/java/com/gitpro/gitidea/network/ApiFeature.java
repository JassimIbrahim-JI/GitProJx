package com.gitpro.gitidea.network;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiFeature {

    private static final String BASE_URL="https://api.github.com/";
    private static ApiFeature apiFeature;
    private Retrofit mRetrofit;

    public ApiFeature(){
        OkHttpClient.Builder httpClient=new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @NonNull
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request original=chain.request();

                Request request=original.newBuilder()
                        .header("Accept","application/vnd.github+json")
                        .header("Authorization","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")
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });
          OkHttpClient client= httpClient.build();
        mRetrofit=new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(client).
                build();
    }

    public static synchronized ApiFeature getInstance(){
        if (apiFeature==null){
        apiFeature=new ApiFeature();
        }
        return apiFeature;
    }

    public DashboardApi getApi(){
        return mRetrofit.create(DashboardApi.class);
    }
}
