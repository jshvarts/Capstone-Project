package com.jshvarts.flatstanley.data.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jshvarts.flatstanley.Constants;
import com.jshvarts.flatstanley.model.FlatStanleyItems;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import retrofit.GsonConverterFactory;
import retrofit.Call;
import retrofit.Retrofit;
import retrofit.http.Query;

public class FlatStanleyRetrofitRestApiClient implements FlatStanleyRestApiClient {

    private OkHttpClient client;
    private Retrofit retrofitInstance;

    public FlatStanleyRetrofitRestApiClient() {
        client = new OkHttpClient();

        // Add logging interceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client.interceptors().add(loggingInterceptor);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(FlatStanleyItems.class, new FlatStanleyDeserializer());
        Gson gson = gsonBuilder.create();

        retrofitInstance = new Retrofit.Builder()
                .baseUrl(Constants.FIREBASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
    }

    @Override
    public Call<FlatStanleyItems> pics(@Query("equalTo") String equalTo) {
        return retrofitInstance.create(FlatStanleyRestApiClient.class).pics(equalTo);
    }
}
