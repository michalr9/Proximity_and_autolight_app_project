package com.michalraq.proximitylightapp.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import com.michalraq.proximitylightapp.R;
import com.michalraq.proximitylightapp.data.DatabaseHandler;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class DetailsView extends AppCompatActivity {
    private static final String PREFERENCES = "myPreferences";
    private static final String OFFICEDETAILS = "officeDetails";
    private static final String KITCHENDETAILS = "kitchenDetails";
    private static final String SALOONDETAILS = "saloonDetails";
    private static final String OFFICEDETAILSSTRING = "officeDetailsStr";
    private static final String KITCHENDETAILSSTRING = "kitchenDetailsStr";
    private static final String SALOONDETAILSSTRING = "saloonDetailsStr";
    private final String TAG= "DetailsView";
    private DatePickerDialog.OnDateSetListener mStartDateSetListener;
    private DatePickerDialog.OnDateSetListener mEndDateSetListener;
    private SharedPreferences preferences;
    private TextView tvOffice;
    private TextView tvSaloon;
    private TextView tvKitchen;
    private TextView tvStartDate, tvEndDate;

    private Button buttonDays, buttonMinutes, buttonHours;

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
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);


        buttonHours = findViewById(R.id.buttonInHours);
        buttonMinutes = findViewById(R.id.buttonInMinutes);
        buttonDays = findViewById(R.id.buttonInDays);

        initButtonMinuteListener();
        initButtonHoursListener();
        initButtonDaysListener();
        initStartDateListener();
        initEndDateListener();
    }

    /**
     * Metoda inicjalizująca przycisk.
     */
    private void initStartDateListener() {
        tvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(DetailsView.this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mStartDateSetListener,
                        year,month,day);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mStartDateSetListener = new DatePickerDialog.OnDateSetListener() {
            /**
             * Metoda definiująca zachowanie aplikacji w przypadku wybrania daty.
             * @param datePicker
             * @param year
             * @param month
             * @param day
             */
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String data = year + "/" + (month+1) + "/" + day;
                tvStartDate.setText(data);
            }
        };
    }
    private void initEndDateListener() {
        tvEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(DetailsView.this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mEndDateSetListener,
                        year,month,day);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mEndDateSetListener = new DatePickerDialog.OnDateSetListener() {
            /**
             * Metoda definiująca zachowanie aplikacji w przypadku wybrania daty.
             * @param datePicker
             * @param year
             * @param month
             * @param day
             */
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String data = year + "/" + (month+1) + "/" + day;
                Log.d("DetailsView",data+ " wartosc: " +  isBefore(data));
                if(!isBefore(data) ) {
                    tvEndDate.setText(data);
                    String startdate = tvStartDate.getText().toString();
                    startDatabaseOperation(startdate,data);
                }else
                {
                    showToast("Podaj datę początkową będącą przed datą końcową !");
                }
            }
        };
    }

    private void showToast(String text) {
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show();
    }

    /**
     * Metoda sprawdzająca czy podana data jako początkowa jest wcześniejsza niż podana końcowa.
     * @param endDate
     * @return
     */
    private Boolean isBefore(String endDate){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date startDate ;
        Date endDate2 ;
        int length = tvStartDate.getText().length();
        if(length!=0) {
            try {
                startDate = formatter.parse(tvStartDate.getText().toString());
                endDate2 = formatter.parse(endDate);
            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }
        }
        else{
            return true;
        }

        return endDate2.before(startDate);
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
        Log.e(TAG, "Błąd podczas uruchamiania wątku polaczenia z baza");
    }
}

    /**
     * Metoda wyciągająca dane z bazy odnośnie czasu zużycia światła.
     * @param startDate
     * @param endDate
     */
    private void startDatabaseOperation(String startDate,String endDate) {
          try
    {
        new DatabaseHandler(this, this).execute("widok2",startDate,endDate);
    }catch(Exception e)
    {
        Log.e(TAG, "Błąd podczas uruchamiania wątku polaczenia z baza");
    }
}

    @Override
    protected void onPause() {
        super.onPause();
        saveData();
    }

    /**
     * Metoda zapisująca dane zmiennych do pamięci wewnętrznej.
     */
    private void saveData(){
        String saloonStr = tvSaloon.getText().toString();
        String kitchenStr = tvKitchen.getText().toString();
        String officeStr = tvOffice.getText().toString();

        SharedPreferences.Editor preferencesEditor = preferences.edit();
        preferencesEditor.putString(OFFICEDETAILSSTRING, officeStr);
        preferencesEditor.putString(KITCHENDETAILSSTRING, kitchenStr);
        preferencesEditor.putString(SALOONDETAILSSTRING, saloonStr);
        preferencesEditor.apply();
    }

    /**
     * Metoda przywracająca dane z pamięci wewnętrznej.
     */
    private void restoreData(){
        tvSaloon.setText(preferences.getString(SALOONDETAILSSTRING,""));
        tvOffice.setText(preferences.getString(OFFICEDETAILSSTRING,""));
        tvKitchen.setText(preferences.getString(KITCHENDETAILSSTRING,""));
    }

    public void goBack(View view) {
            Intent mainMenu = new Intent(this, MainMenu.class);
            startActivity(mainMenu);
        }
}
