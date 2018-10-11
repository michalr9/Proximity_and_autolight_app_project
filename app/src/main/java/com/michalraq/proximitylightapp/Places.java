package com.michalraq.proximitylightapp;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;

import com.estimote.proximity_sdk.api.EstimoteCloudCredentials;

public class Places extends AppCompatActivity {

    public EstimoteCloudCredentials cloudCredentials =
            new EstimoteCloudCredentials("proximity-light-4nu", "d25c41d6bc5b7cb0fe1f394be8ccf46d");


}



