package com.gitpro.gitidea.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ImageInstance {

    private static Retrofit retrofit;
    private static final String URL="https://pixabay.com/api/";
    public static synchronized Retrofit getImageClient(){
        if (retrofit==null){
            HttpLoggingInterceptor loggingInterceptor=new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client=new OkHttpClient.Builder().addInterceptor(loggingInterceptor).build();

            retrofit=new Retrofit.Builder().client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(URL).build();

        }
        return retrofit;
    }


}
