package com.jshvarts.flatstanley.data.remote;

import com.jshvarts.flatstanley.model.FlatStanleyItems;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Allows searches against Firebase REST urls.
 */
public interface FlatStanleyRestApiClient {
    @GET("/items.json?orderBy=\"caption\"")
    Call<FlatStanleyItems> pics(@Query("equalTo") String equalTo);
}
