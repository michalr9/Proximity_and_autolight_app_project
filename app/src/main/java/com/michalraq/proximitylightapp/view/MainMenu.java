package com.michalraq.proximitylightapp.view;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.estimote.mustard.rx_goodness.rx_requirements_wizard.Requirement;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory;
import com.estimote.proximity_sdk.api.EstimoteCloudCredentials;
import com.michalraq.proximitylightapp.R;
import com.michalraq.proximitylightapp.data.DatabaseHandler;
import com.michalraq.proximitylightapp.data.estimote.ProximityContentManager;

import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

/**
 * Główna klasa sterująca, główna aktywność.
 */
public class MainMenu extends AppCompatActivity {

    private static final String PREFERENCES = "myPreferences";
    private static final String OFFICE = "biuro";
    private static final String KITCHEN = "kuchnia";
    private static final String SALOON = "salon";
    private ProximityContentManager proximityContentManager;
    private Boolean office,kitchen,saloon;
    private ToggleButton buttonOffice,buttonKitchen,buttonSaloon;
    private SharedPreferences sharedPreferences;
    BluetoothAdapter mBtAdapter;
    Boolean isBTActive;
    WifiManager wifiManager;
    public EstimoteCloudCredentials cloudCredentials = new EstimoteCloudCredentials("proximity-light-4nu", "d25c41d6bc5b7cb0fe1f394be8ccf46d");

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
                Intent editServer = new Intent(this, ServerManager.class);
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

        setButtonsStatus();
        checkRequirements();
        enableWiFi();
    }

    /**
     * Ustawienie statusu przycisków odpowiadających pomieszczeniom.
     */
    private void setButtonsStatus() {
        office = sharedPreferences.getBoolean(OFFICE,false);
        kitchen = sharedPreferences.getBoolean(KITCHEN,false);
        saloon = sharedPreferences.getBoolean(SALOON,false);

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
