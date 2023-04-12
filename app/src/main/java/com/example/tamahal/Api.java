package com.example.tamahal;

import retrofit2.Call;
import retrofit2.http.GET;

public interface Api {

    @GET("update.txt")
    public Call<Post> getPost ();

}
