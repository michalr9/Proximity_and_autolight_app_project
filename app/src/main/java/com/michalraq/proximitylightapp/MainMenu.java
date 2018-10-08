package com.michalraq.proximitylightapp;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainMenu extends AppCompatActivity {

    BluetoothAdapter mBtAdapter;
    Boolean isBTActive;


    @Override
    protected void onStart(){
        enableBT();
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        /*BLUETOOTH*/
        isBTActive = false;
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        /*WIFI*/


    }

    public void onButtonLightClick(View v){
        Toast myToast = Toast.makeText(getApplicationContext(),"Zarządzaj światłem !",Toast.LENGTH_SHORT);

        myToast.show();
        startActivity(new Intent(this,Places.class));

    }


    public void enableBT(){
         mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBtAdapter == null) {
            Toast.makeText(this, "Twoje urządzenie nie umożliwia korzystania z BT", Toast.LENGTH_SHORT).show();
            isBTActive=false;
        }

        if (! mBtAdapter.isEnabled()) {
              mBtAdapter.enable();

            isBTActive=true;
            Toast.makeText(this, "Aktywowano Bluetooth !", Toast.LENGTH_SHORT).show();
        }

        if(mBtAdapter.isEnabled()){
            isBTActive = true;
        }
    }

    public void enableWiFi(){

    }


    @Override
    protected void onDestroy(){
        Log.d("MainMenu","Wywolano destroy");
        super.onDestroy();
    }

}
