package com.michalraq.proximitylightapp;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.Requirement;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory;
import com.estimote.proximity_sdk.api.EstimoteCloudCredentials;
import com.michalraq.proximitylightapp.estimote.ProximityContentAdapter;
import com.michalraq.proximitylightapp.estimote.ProximityContentManager;

import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

public class MainMenu extends AppCompatActivity {

    private ProximityContentManager proximityContentManager;
    private ProximityContentAdapter proximityContentAdapter;
    public EstimoteCloudCredentials cloudCredentials =
            new EstimoteCloudCredentials("proximity-light-4nu", "d25c41d6bc5b7cb0fe1f394be8ccf46d");

    BluetoothAdapter mBtAdapter;
    Boolean isBTActive;
     final Boolean enableWiFi = true;
    WifiManager wifiManager;

    @Override
    protected void onStart(){
    //    enableBT();
        enableWiFi();
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        /*BLUETOOTH*/
        isBTActive = false;
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        /*WIFI*/
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        /*Beacons*/
        proximityContentAdapter = new ProximityContentAdapter(this);
        GridView gridView = findViewById(R.id.gridView);
        gridView.setAdapter(proximityContentAdapter);


        RequirementsWizardFactory
                .createEstimoteRequirementsWizard()
                .fulfillRequirements(this,
                        new Function0<Unit>() {
                            @Override
                            public Unit invoke() {
                                Log.d("app", "requirements fulfilled");
                                startProximityContentManager();
                                return null;
                            }
                        },
                        new Function1<List<? extends Requirement>, Unit>() {
                            @Override
                            public Unit invoke(List<? extends Requirement> requirements) {
                                Log.e("app", "requirements missing: " + requirements);
                                return null;
                            }
                        },
                        new Function1<Throwable, Unit>() {
                            @Override
                            public Unit invoke(Throwable throwable) {
                                Log.e("app", "requirements error: " + throwable);
                                return null;
                            }
                        });


    }

    private void startProximityContentManager() {
        proximityContentManager = new ProximityContentManager(this, proximityContentAdapter, cloudCredentials);
        proximityContentManager.start();
    }


    public void enableBT(){
         mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBtAdapter == null) {
            Toast.makeText(this, "Twoje urządzenie nie umożliwia korzystania z BT", Toast.LENGTH_SHORT).show();
            isBTActive=false;
        }

        if (! mBtAdapter.isEnabled()) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    mBtAdapter.enable();
                }
            }).start();

            isBTActive=true;
            Toast.makeText(this, "Aktywowano Bluetooth !", Toast.LENGTH_SHORT).show();
        }

        if(mBtAdapter.isEnabled()){
            isBTActive = true;
        }
    }

    public void enableWiFi(){
        if(wifiManager == null){
            Toast.makeText(this, "Twoje urządzenie nie umożliwia korzystania z WiFi", Toast.LENGTH_SHORT).show();
        }

        if (!wifiManager.isWifiEnabled()) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                        wifiManager.setWifiEnabled(enableWiFi);
                }
            }).start();

            Toast.makeText(this, "Aktywowano WiFi !", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onDestroy(){
        Log.d("MainMenu","Wywolano destroy");
        super.onDestroy();
        if (proximityContentManager != null)
            proximityContentManager.stop();
    }

}
