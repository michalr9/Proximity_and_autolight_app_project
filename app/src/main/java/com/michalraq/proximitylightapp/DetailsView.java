package com.michalraq.proximitylightapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.michalraq.proximitylightapp.database.DatabaseHandler;

public class DetailsView extends AppCompatActivity {
    private TextView tvOffice;
    private TextView tvSaloon;
    private TextView tvKitchen;
    private Button buttonSeconds,buttonMinutes,buttonHours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_view);

        tvKitchen = findViewById(R.id.tvKitchenDetailsNumber);
        tvOffice = findViewById(R.id.tvOfficeDetailsNumber);
        tvSaloon = findViewById(R.id.tvSaloonDetailsNumber);

        buttonHours = findViewById(R.id.buttonInHours);
        buttonMinutes = findViewById(R.id.buttonInMinutes);
        buttonSeconds = findViewById(R.id.buttonInSeconds);
        initButtonMinuteListener();

    }

    private void initButtonMinuteListener() {
        buttonMinutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            onButtonMinuteClick();
            }
        });
    }
    private void onButtonMinuteClick(){
            new DatabaseHandler(this,this).execute("widok2");
    }
//TODO przechowywanie wartosci okien ze statusem w pamieci
    //TODO obsluga pozostalych buttonow
}
