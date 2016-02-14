package com.example.firebasepoc;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by robin on 2/10/16.
 */
public class App extends Application {
    public static final String TAG = "FirebasePoC";

    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);
    }

    protected Firebase createFirebase() {
        return new Firebase(getResources().getString(R.string.firebase_url));
    }

    public final Firebase initFirebase(Firebase.AuthResultHandler callback) {
        Firebase firebase = createFirebase();

        firebase.authWithPassword(getResources().getString(R.string.firebase_email), getResources().getString(R.string.firebase_password), callback);

        return firebase;
    }

    public Firebase getFbPeopleRef() {
        return createFirebase().child("people");
    }
}
