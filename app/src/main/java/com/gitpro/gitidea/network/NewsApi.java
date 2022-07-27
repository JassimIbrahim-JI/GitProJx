package com.gitpro.gitidea.network;

import com.gitpro.gitidea.models.News;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApi {

    @GET("top-headlines")
    Call<News>getHeadlines(
      @Query("country")
      String country,
      @Query("apiKey")
      String apiKey

      );


}
