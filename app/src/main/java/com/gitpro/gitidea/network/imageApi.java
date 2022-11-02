package com.gitpro.gitidea.network;

import com.gitpro.gitidea.models.Hit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface imageApi {

    //USING PIXAPAY API,MY APIKEY:30631856-599272286b144ff356083732d
    @GET("?key=30631856-599272286b144ff356083732d")
    Call<Hit>callAllImage(
            @Query("image_type")String imageType,
            @Query("q")String query,
            @Query("page")int page,
            @Query("per_page")int perPage
            );

}
