package com.example.cashledger.modelClasses;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class NetworkSecurity extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
