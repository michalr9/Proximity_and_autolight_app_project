package com.michalraq.proximitylightapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainMenu extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

    }

    public void onButtonLightClick(View v){
        Toast myToast = Toast.makeText(getApplicationContext(),"Zarządzaj światłem !",Toast.LENGTH_SHORT);

        myToast.show();
        startActivity(new Intent(this,Places.class));

    }
}
