package com.jshvarts.flatstanley;

import android.support.multidex.MultiDexApplication;

import com.firebase.client.Firebase;

public class FlatStanleyApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
