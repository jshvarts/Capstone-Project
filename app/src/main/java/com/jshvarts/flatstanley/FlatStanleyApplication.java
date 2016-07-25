package com.jshvarts.flatstanley;

import android.app.Application;

import com.firebase.client.Firebase;

public class FlatStanleyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
