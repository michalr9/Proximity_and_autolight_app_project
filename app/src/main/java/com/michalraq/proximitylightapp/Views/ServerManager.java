package com.michalraq.proximitylightapp.Views;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.michalraq.proximitylightapp.R;
import com.michalraq.proximitylightapp.Service.Client;
import com.michalraq.proximitylightapp.Service.ServerContent;

/**
 * Klasa widok, odpowiedzialna za zarządzanie ustawieniami usługi i jej działania.
 */
public class ServerManager extends AppCompatActivity {

    private static final String PREFERENCES = "myPreferences";
    private static final String SERVER_IP = "ipServer";
    private static final String PORT_NUMBER = "portNumber";
    private static final String IS_SERVICE_STARTED = "isServiceStarted";
    private static final String IS_DATA_CHANGED = "isDataChanged";
    private EditText etIPServer;
    private  EditText etPortNumber;
    private SharedPreferences preferences;
    public static boolean isServiceStarted,isDataChanged;
    public Button buttonSave,buttonStart,buttonStop ;

    /**
     * Metoda odbierająca powiadomienia z klasy Client
     */
    private BroadcastReceiver bReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
          Boolean fail = intent.getBooleanExtra("fail",false);
          if(fail){
              manageButtonsStatus("stop");
          }
        }

    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_manager);
        preferences = getSharedPreferences(PREFERENCES, Activity.MODE_PRIVATE);

         buttonSave = findViewById(R.id.buttonSave);
         etIPServer = findViewById(R.id.editTextServerIP);
         etPortNumber = findViewById(R.id.editTextPortAddress);
         buttonStart = findViewById(R.id.buttonStartService);
         buttonStop = findViewById(R.id.buttonStopService);

         initButtonSaveListener();
         initButtonStartListener();
         initButtonStopListener();

         etIPServer.addTextChangedListener(settingsTextWatcher);
         etPortNumber.addTextChangedListener(settingsTextWatcher);

    }
    @Override
    protected void onPause() {
        super.onPause();
        saveData();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(bReceiver); //uzywany do otrzymywania powiadomien z uruchomionej uslugi
    }

    @Override
    protected void onResume() {
        super.onResume();
        restoreData();
        LocalBroadcastManager.getInstance(this).registerReceiver(bReceiver, new IntentFilter("message"));

    }

    private void initButtonSaveListener(){
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDataChanged = false;
                setServerContent();
                manageButtonsStatus("save");
                Toast.makeText(ServerManager.this, "Dane zapisano!", Toast.LENGTH_SHORT).show();
            }
        });
    }  private void initButtonStartListener(){
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageButtonsStatus("start");
                startService(new Intent(getApplicationContext(),Client.class));

            }
        });
    }
    private void initButtonStopListener(){
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopService(new Intent(getApplicationContext(),Client.class));
                isServiceStarted=false;
                manageButtonsStatus("stop");
            }
        });
    }

    /**
     * Metoda zapisująca dane do pamięci wewnętrznej urządzenia.
     */
   private void saveData(){

        SharedPreferences.Editor preferencesEditor = preferences.edit();
        String ipServerToSave = etIPServer.getText().toString().trim();
        String portServerToSave = etPortNumber.getText().toString().trim();

        preferencesEditor.putString(SERVER_IP,ipServerToSave);
        preferencesEditor.putString(PORT_NUMBER,portServerToSave);

        preferencesEditor.putBoolean(IS_SERVICE_STARTED,isServiceStarted);
        preferencesEditor.putBoolean(IS_DATA_CHANGED,isDataChanged);
        preferencesEditor.apply();
    }

    private void setServerContent(){
        String ipServerToSave = etIPServer.getText().toString().trim();
        String portServerToSave = etPortNumber.getText().toString().trim();
        ServerContent.SERVER_IP = ipServerToSave;
        ServerContent.PORT_NUMBER = Integer.parseInt(portServerToSave);
    }
    /**
     * Metoda odzyskująca zapisane dane z pamięci wewnętrznej urządzenia.
     */
   private void restoreData(){
            String ipServerSaved = preferences.getString(SERVER_IP, "");
            String portServerSaved = preferences.getString(PORT_NUMBER, "");

            isServiceStarted = preferences.getBoolean(IS_SERVICE_STARTED,false);
            isDataChanged = preferences.getBoolean(IS_DATA_CHANGED,false);

            etIPServer.setText(ipServerSaved);
            etPortNumber.setText(portServerSaved);
            ServerContent.SERVER_IP = ipServerSaved;
       if(!portServerSaved.equals("")) {
           ServerContent.PORT_NUMBER = Integer.parseInt(portServerSaved);
       }else{
           ServerContent.PORT_NUMBER = (-1);
       }

            if(!ipServerSaved.isEmpty() && !portServerSaved.isEmpty()){
                buttonSave.setBackgroundColor(getResources().getColor(R.color.colorAccepted));
            }else
            {
                buttonSave.setBackgroundColor(getResources().getColor(R.color.colorError));
            }

            if(isServiceStarted) {
              manageButtonsStatus("start");
            }else{
              manageButtonsStatus("stop");
            }


   }

    /**
     * Obserwator pól edycyjnych.
     */
    private TextWatcher settingsTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
        }

        /**
         * W zależności od zmienionego tekstu, dane zostają zapisane, a przyciski w aktywności są zarządzane.
         * @param s
         * @param start
         * @param before
         * @param count
         */
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String portNumber = etPortNumber.getText().toString().trim();
            String ipServer = etIPServer.getText().toString().trim();

            if(!portNumber.isEmpty() && !ipServer.isEmpty()) {
                buttonSave.setEnabled(true);
                buttonSave.setBackgroundColor(getResources().getColor(R.color.colorAccepted));
            }else{
                buttonSave.setBackgroundColor(getResources().getColor(R.color.colorError));
                buttonSave.setEnabled(false);
            }
        }

        /**
         * Metoda ustawiająca zmienne po zmianie edycji tekstu.
         * @param s
         */
        @Override
        public void afterTextChanged(Editable s) {
            buttonStart.setEnabled(false);
            isDataChanged=true;
        }
    };

    /**
     * Metoda zarządzająca statusem przycisków na ekranie.
     * @param buttonFunction
     */
    private void manageButtonsStatus(String buttonFunction){
        switch(buttonFunction){
            case "start":
                buttonStart.setEnabled(false);
                buttonStop.setEnabled(true);
                buttonSave.setEnabled(false);
                etIPServer.setEnabled(false);
                etPortNumber.setEnabled(false);
                break;
            case "stop":
                if(!ServerContent.SERVER_IP.equals("") && ServerContent.PORT_NUMBER !=(-1)) {
                    buttonStart.setEnabled(true);
                }
                buttonStop.setEnabled(false);
                buttonSave.setEnabled(false);

                etIPServer.setEnabled(true);
                etPortNumber.setEnabled(true);
                break;
            case "save":
                buttonSave.setEnabled(false);
                buttonStart.setEnabled(true);
                buttonStop.setEnabled(false);
                break;
        }
    }

}
