package com.michalraq.proximitylightapp;

import android.app.Application;
import android.os.Bundle;

import com.estimote.proximity_sdk.api.EstimoteCloudCredentials;

public class Places extends Application {

    public EstimoteCloudCredentials cloudCredentials =
            new EstimoteCloudCredentials("proximity-light-4nu", "d25c41d6bc5b7cb0fe1f394be8ccf46d");


}



