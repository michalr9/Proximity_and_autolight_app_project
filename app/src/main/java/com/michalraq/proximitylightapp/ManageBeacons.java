package com.michalraq.proximitylightapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import com.estimote.internal_plugins_api.scanning.Beacon;
import com.estimote.proximity_sdk.internals.proximity.cloud.EstimoteProximityCloud;
import com.michalraq.proximitylightapp.estimote.ProximityContent;

import java.util.ArrayList;
import java.util.List;

public class ManageBeacons extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_beacons);

        List<ProximityContent> beacons = new ArrayList<>();

    ManageBeaconsAdapter beaconsAdapter = new ManageBeaconsAdapter(this);
    List<Integer> images = new ArrayList<>();
    for (int i = 0;i<3;i++){
        images.add(i, getResources().getIdentifier("beacon.png","drawable",getPackageName()));
    }
    List<ProximityContent> beaconsData = new ArrayList<>();

    beaconsAdapter.setBeaconContent(beaconsData);

        GridView gridView = findViewById(R.id.grid_view_beacons_managment);
        gridView.setAdapter(beaconsAdapter);


    }

    private void createBeaconsData(){
        ProximityContent beacon1 = new ProximityContent("ice","");
        ProximityContent beacon2 = new ProximityContent("mint","");
        ProximityContent beacon3 = new ProximityContent("blueberry","");
    }

}
