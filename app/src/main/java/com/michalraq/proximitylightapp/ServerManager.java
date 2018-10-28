package com.michalraq.proximitylightapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class ServerManager extends AppCompatActivity {
    private static final String PREFERENCES = "myPreferences";
    private static final String SERVER_IP = "ipServer";
    private static final String PORT_NUMBER = "portNumber";
    private Button buttonSave ;
    private EditText etIPServer;
    private  EditText etPortNumber;
    private SharedPreferences preferences;
    public ServerContent server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_manager);
        preferences = getSharedPreferences(PREFERENCES, Activity.MODE_PRIVATE); //nazwa pliku do przechowywania ustawien i tryb

         buttonSave = findViewById(R.id.buttonSave);
         etIPServer = findViewById(R.id.editTextServerIP);
         etPortNumber = findViewById(R.id.editTextPortAddress);

         restoreData();

         initButtonListener();
         server = new ServerContent();

         etIPServer.addTextChangedListener(settingsTextWatcher);
         etPortNumber.addTextChangedListener(settingsTextWatcher);

    }

    private void initButtonListener(){
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                Toast.makeText(ServerManager.this, "Dane zapisano!", Toast.LENGTH_SHORT).show();
            }
        });
    }

   private void saveData(){
    SharedPreferences.Editor preferencesEditor = preferences.edit();
    String ipServerToSave = etIPServer.getText().toString().trim();
    String portServerToSave = etPortNumber.getText().toString().trim();

    preferencesEditor.putString(SERVER_IP,ipServerToSave);
    preferencesEditor.putString(PORT_NUMBER,portServerToSave);
    server.setServerIP(ipServerToSave);
    server.setPortNumber(Integer.parseInt(portServerToSave));
    preferencesEditor.commit();

    this.finish();
   }

   private void restoreData(){
            String ipServerSaved = preferences.getString(SERVER_IP, "");
            String portServerSaved = preferences.getString(PORT_NUMBER, "");

            etIPServer.setText(ipServerSaved);
            etPortNumber.setText(portServerSaved);

            if(!ipServerSaved.isEmpty() && !portServerSaved.isEmpty()){
                buttonSave.setBackgroundColor(getResources().getColor(R.color.colorAccepted));
            }
   }

    private TextWatcher settingsTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
        }

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

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
}
