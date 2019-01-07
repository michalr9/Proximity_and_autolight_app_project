package com.michalraq.proximitylightapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.michalraq.proximitylightapp.database.DatabaseHandler;

public class DetailsView extends AppCompatActivity {
    private static final String PREFERENCES = "myPreferences";
    private static final String OFFICEDETAILS = "officeDetails";
    private static final String KITCHENDETAILS = "kitchenDetails";
    private static final String SALOONDETAILS = "saloonDetails";
    private static final String OFFICEDETAILSSTRING = "officeDetailsStr";
    private static final String KITCHENDETAILSSTRING = "kitchenDetailsStr";
    private static final String SALOONDETAILSSTRING = "saloonDetailsStr";
    private SharedPreferences preferences;
    private String saloonStr,kitchenStr,officeStr;
    private TextView tvOffice;
    private TextView tvSaloon;
    private TextView tvKitchen;

    private Button buttonDays, buttonMinutes, buttonHours;

    @Override
    protected void onStart() {
        super.onStart();
        startDatabaseOperation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        restoreData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_view);
        preferences = getSharedPreferences(PREFERENCES, Activity.MODE_PRIVATE);

        tvKitchen = findViewById(R.id.tvKitchenDetailsNumber);
        tvOffice = findViewById(R.id.tvOfficeDetailsNumber);
        tvSaloon = findViewById(R.id.tvSaloonDetailsNumber);

        buttonHours = findViewById(R.id.buttonInHours);
        buttonMinutes = findViewById(R.id.buttonInMinutes);
        buttonDays = findViewById(R.id.buttonInDays);

        initButtonMinuteListener();
        initButtonHoursListener();
        initButtonDaysListener();

    }

    private void initButtonMinuteListener() {
        buttonMinutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonMinuteClick();
            }
        });

    }

    private void initButtonHoursListener() {
        buttonHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonHoursClick();
            }
        });

    }

    private void initButtonDaysListener() {
        buttonDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onButtonDaysClick();
            }
        });

    }

    private void onButtonMinuteClick() {

        Long saloonLong = preferences.getLong(SALOONDETAILS, 0L);
        Long kitchenLong = preferences.getLong(KITCHENDETAILS, 0L);
        Long officeLong = preferences.getLong(OFFICEDETAILS, 0L);

        Long minutes=0L, seconds=0L;

        if(saloonLong!=0L) {
            /*Obliczenia dla salonu*/
            minutes = saloonLong / 60;
            seconds = saloonLong % 60;
        }
        tvSaloon.setText(String.format("%d:%d min", minutes, seconds));
        minutes=0L;
        seconds=0L;
        /*Obliczenia dla kuchni*/
        if(kitchenLong!=0L) {
            minutes = kitchenLong / 60;
            seconds = kitchenLong % 60;
        }
        tvKitchen.setText(String.format("%d:%d min", minutes, seconds));
        minutes=0L;
        seconds=0L;
        /*Obliczenia dla biura*/
        if(officeLong!=0L) {
            minutes = officeLong / 60;
            seconds = officeLong % 60;
        }
            tvOffice.setText(String.format("%d:%d min", minutes, seconds));

    }
    private void onButtonHoursClick() {
        Long saloonLong = preferences.getLong(SALOONDETAILS, 0L);
        Long kitchenLong = preferences.getLong(KITCHENDETAILS, 0L);
        Long officeLong = preferences.getLong(OFFICEDETAILS, 0L);

            Long hours=0L;

            /*Obliczenia dla salonu*/
        if(saloonLong!=0L) {
            hours = saloonLong / 3600;
        }
            tvSaloon.setText(String.format("%d h", hours));
            /*Obliczenia dla kuchni*/
        hours=0L;
            if(kitchenLong!=0L) {
                hours = kitchenLong / 3600;
            }
            tvKitchen.setText(String.format("%d h", hours));
            /*Obliczenia dla biura*/
        hours=0L;
                if(officeLong!=0L) {
                    hours = officeLong / 3600;
                }
            tvOffice.setText(String.format("%d h", hours));

    }
    private void onButtonDaysClick(){
        Long saloonLong = preferences.getLong(SALOONDETAILS, 0L);
        Long kitchenLong = preferences.getLong(KITCHENDETAILS, 0L);
        Long officeLong = preferences.getLong(OFFICEDETAILS, 0L);

        Long daysSaloon,daysKitchen,daysOffice;
        if(saloonLong!=0L) {
             daysSaloon = (saloonLong / (60 * 60 * 24));
        }else{
            daysSaloon=0L;
        }

            if(kitchenLong!=0L) {
                 daysKitchen = (kitchenLong / (60 * 60 * 24));
            }else{
                daysKitchen=0L;
            }

            if(officeLong!=0L) {
                 daysOffice = (officeLong / (60 * 60 * 24));
            }else
            {
                daysOffice=0L;
            }

            if(daysSaloon==1L){
                tvSaloon.setText(String.format("%d dzień", daysSaloon));
            }else{
                tvSaloon.setText(String.format("%d dni", daysSaloon));
            }
            if(daysKitchen==1L){
                tvKitchen.setText(String.format("%d dzień", daysKitchen));
            }else{
                tvKitchen.setText(String.format("%d dni", daysKitchen));
            }
            if(daysOffice==1L){
                tvOffice.setText(String.format("%d dzień", daysOffice));
            }else{
                tvOffice.setText(String.format("%d dni", daysOffice));
            }


    }

    private void startDatabaseOperation() {
          try
    {
        new DatabaseHandler(this, this).execute("widok2");
    }catch(Exception e)
    {
        Log.e("DetailsView", "Błąd podczas uruchamiania wątku polaczenia z baza");
    }
}

    @Override
    protected void onPause() {
        super.onPause();
        saveData();
    }

    private void saveData(){
        saloonStr = tvSaloon.getText().toString();
        kitchenStr = tvKitchen.getText().toString();
        officeStr = tvOffice.getText().toString();

        SharedPreferences.Editor preferencesEditor = preferences.edit();
        preferencesEditor.putString(OFFICEDETAILSSTRING, officeStr);
        preferencesEditor.putString(KITCHENDETAILSSTRING, kitchenStr);
        preferencesEditor.putString(SALOONDETAILSSTRING, saloonStr);
        preferencesEditor.apply();
    }

    private void restoreData(){
        tvSaloon.setText(preferences.getString(SALOONDETAILSSTRING,""));
        tvOffice.setText(preferences.getString(OFFICEDETAILSSTRING,""));
        tvKitchen.setText(preferences.getString(KITCHENDETAILSSTRING,""));
    }

}
