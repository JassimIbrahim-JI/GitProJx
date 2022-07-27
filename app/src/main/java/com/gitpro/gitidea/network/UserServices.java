package com.gitpro.gitidea.network;

import com.gitpro.gitidea.models.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface UserServices {

    @GET("/projects/{project_id}")
    Call<User> getUserById(@Path("id")String username);

}
