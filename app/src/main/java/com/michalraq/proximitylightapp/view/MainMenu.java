package com.michalraq.proximitylightapp.view;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.estimote.mustard.rx_goodness.rx_requirements_wizard.Requirement;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory;
import com.estimote.proximity_sdk.api.EstimoteCloudCredentials;
import com.michalraq.proximitylightapp.R;
import com.michalraq.proximitylightapp.data.DatabaseHandler;
import com.michalraq.proximitylightapp.data.estimote.ProximityContentManager;
import com.michalraq.proximitylightapp.service.Client;

import java.text.DecimalFormat;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

/**
 * Główna klasa sterująca, główna aktywność.
 */
public class MainMenu extends AppCompatActivity implements SensorEventListener {

    private static final String PREFERENCES = "myPreferences";
    private static final String OFFICE = "biuro";
    private static final String KITCHEN = "kuchnia";
    private static final String SALOON = "salon";
    private ProximityContentManager proximityContentManager;
    private ToggleButton buttonOffice,buttonKitchen,buttonSaloon;
    private SharedPreferences sharedPreferences;
    private SensorManager sensorManager;
    Sensor accelerometer;
    BluetoothAdapter mBtAdapter;
    Boolean isBTActive;
    WifiManager wifiManager;
    public EstimoteCloudCredentials cloudCredentials = new EstimoteCloudCredentials("proximity-light-4nu", "d25c41d6bc5b7cb0fe1f394be8ccf46d");
    private static DecimalFormat df = new DecimalFormat("0.0000");

    /**
     * Tworzenie górnego menu.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);

    }

    /**
     * Obsługa wybrania opcji z górnego menu.
     * @param item wybrany item z menu.
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.acction_settings:
                Intent editServer = new Intent(this, ServiceManager.class);
                startActivity(editServer);
            return true;
            case R.id.acction_refresh:
                new DatabaseHandler(this,this).execute("widok1");
                return true;
            case R.id.acction_details:
                Intent details = new Intent(this, DetailsView.class);
                startActivity(details);
                return true;
            case R.id.acction_checkoutData:
                Intent checkoutData = new Intent(this,CheckoutData.class);
                startActivity(checkoutData);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setButtonsStatus();
    }

    /**
     * Inicjalizacja danych w metodzie.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        /*PREFERENCES*/
        sharedPreferences = getSharedPreferences(PREFERENCES, Activity.MODE_PRIVATE);

        /*BLUETOOTH*/
        isBTActive = false;
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        /*WIFI*/
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        /*Buttons*/
        buttonOffice = findViewById(R.id.button_status_office);
        buttonKitchen = findViewById(R.id.button_status_kitchen);
        buttonSaloon = findViewById(R.id.button_status_saloon);

        initButtonKitchenListener();
        initButtonOfficeListener();
        initButtonSaloonListener();

        checkRequirements();
        enableWiFi();
        registerAccelerometer();
    }

    private void registerAccelerometer() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        assert sensorManager != null;
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                if(com.michalraq.proximitylightapp.data.Activity.activityBiuro){
                    if ((event.values[0] <= 10.75 && event.values[0] >= 8.75) && ((event.values[1] <= 0.84 && event.values[1] >= -0.76))) {
                        showActivity(com.michalraq.proximitylightapp.data.Activity.WORK);
                    } else {
                        showActivity(com.michalraq.proximitylightapp.data.Activity.RUN);
                    }
                }else if(com.michalraq.proximitylightapp.data.Activity.activityKuchnia){
                    if ((event.values[0] <= 10 && event.values[0] >= 7.75) && ((event.values[1] <= 1.84 && event.values[1] >= -1.76))) {
                        showActivity(com.michalraq.proximitylightapp.data.Activity.EAT);
                    } else if((event.values[0] <= 2 && event.values[0] >= -2) && ((event.values[1] <= -7.5 && event.values[1] >= -10.76))) {
                        showActivity(com.michalraq.proximitylightapp.data.Activity.COOK);
                    }else
                     {
                         showActivity(com.michalraq.proximitylightapp.data.Activity.RUN);
                    }
                }else if (com.michalraq.proximitylightapp.data.Activity.activitySalon){

                    if ((event.values[0] <= 10.75 && event.values[0] >= 7.5) && ((event.values[1] <= 0 && event.values[1] >= -3.76))) {
                        showActivity(com.michalraq.proximitylightapp.data.Activity.WATCHING_TV);
                    }else
                    {
                        showActivity(com.michalraq.proximitylightapp.data.Activity.RUN);
                    }
                }
            }
        });
        thread.start();
    }

    static String oldText="";
    public void showActivity(final String text){

        if (oldText.equals("")) {
            oldText = text;
        } else if(!oldText.equals(text)) {
            oldText=text;

            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainMenu.this,"Rozpoznaję aktywność: "+ text,Toast.LENGTH_LONG).show();
                }
            });

        }else{
            Log.d("MainMenu", "- - -");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void initButtonOfficeListener(){
        buttonOffice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remoteButtonControl("office",buttonOffice.isChecked());
            }
        });}
    private void initButtonKitchenListener(){
        buttonKitchen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remoteButtonControl("kitchen",buttonKitchen.isChecked());
            }
        });}
    private void initButtonSaloonListener(){
        buttonSaloon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remoteButtonControl("saloon",buttonSaloon.isChecked());
            }
        });}

    /**
     * Ustawienie statusu przycisków odpowiadających pomieszczeniom.
     */
    private void setButtonsStatus() {
        Boolean office = sharedPreferences.getBoolean(OFFICE, false);
        Boolean kitchen = sharedPreferences.getBoolean(KITCHEN, false);
        Boolean saloon = sharedPreferences.getBoolean(SALOON, false);

        if(office){
            buttonOffice.setChecked(true);
        }else{
            buttonOffice.setChecked(false);
        }
        if(kitchen){
            buttonKitchen.setChecked(true);

        }else{
            buttonKitchen.setChecked(false);
        }
        if(saloon){
            buttonSaloon.setChecked(true);

        }else{
            buttonSaloon.setChecked(false);
        }
    }

    /**
     * Metoda uruchamiająca obserwatora nadajników.
     */
    private void startProximityContentManager() {
        proximityContentManager = new ProximityContentManager(this, cloudCredentials);
        proximityContentManager.start();
    }

    /**
     * Metoda odpowiedzialna za uruchomienie Wi-Fi.
     */
    public void enableWiFi(){

        if(wifiManager == null){
            Toast.makeText(this, "Twoje urządzenie nie umożliwia korzystania z WiFi", Toast.LENGTH_SHORT).show();
        }else {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);

            alertDialogBuilder.setTitle("Ustawienia WiFi");

            // ustawienie wiadomości
            alertDialogBuilder
                    .setMessage("Czy mogę włączyć WiFi ?")
                    .setCancelable(false)
                    .setPositiveButton("TAK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //enable wifi
                            wifiManager.setWifiEnabled(true);
                        }
                    })
                    .setNegativeButton("NIE", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //disable wifi
                            wifiManager.setWifiEnabled(false);
                        }
                    });

            // stworzenie okna dialoqowego
            AlertDialog alertDialog = alertDialogBuilder.create();

            //wyswietlenie
        if (!wifiManager.isWifiEnabled()) {
                    alertDialog.show(); }
        }
    }

   private void remoteButtonControl(String buttonName,Boolean stat){
 String message = "";
 String status;
 if(stat){
     status="1";
 }else
     status="0";

        switch(buttonName){
            case "office":
                message = status + "biuro";
                break;
                case "kitchen":
                    message = status+"kuchnia";
                break;
                case "saloon":
                    message = status+"salon";
                break;
        }

       if(ServiceManager.isServiceStarted)
           Client.sendMessage(message);
   }

    /**
     * Metoda sprawdzająca czy urządzenie sprełnia wymagania do obsługi nadajników.
     */
    public void checkRequirements(){
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

    private void saveData(){

        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();

        preferencesEditor.putBoolean(OFFICE,buttonOffice.isChecked());
        preferencesEditor.putBoolean(KITCHEN,buttonKitchen.isChecked());
        preferencesEditor.putBoolean(SALOON,buttonSaloon.isChecked());
        preferencesEditor.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveData();
    }

    /**
     * Metoda obsługująca wyłączenie aplikacji.
     */
    @Override
    protected void onDestroy(){
        Log.d("MainMenu","Wywolano destroy");
        super.onDestroy();
        if (proximityContentManager != null)
            proximityContentManager.stop();
    }


}
