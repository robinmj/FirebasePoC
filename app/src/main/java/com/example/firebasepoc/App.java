package com.example.firebasepoc;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by robin on 2/10/16.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);
    }
}