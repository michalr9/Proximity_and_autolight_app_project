package com.michalraq.proximitylightapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.TextView;


public class ServerManager extends AppCompatActivity {
//TODO TWORZENIE KLASY SERWERA , ZACHOWYWANIE STANU EDYTOWANYCH TEKSTOW

    Button buttonSave ;
    TextView tvIPServer ;
    TextView tvPortNumber ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_manager);

         buttonSave = findViewById(R.id.buttonSave);
         tvIPServer = findViewById(R.id.textViewServerIP);
         tvPortNumber = findViewById(R.id.editTextPortAddress);


        tvIPServer.addTextChangedListener(settingsTextWatcher);
        tvPortNumber.addTextChangedListener(settingsTextWatcher);


    }

    private TextWatcher settingsTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String portNumber = tvPortNumber.getText().toString().trim();
            String ipServer = tvIPServer.getText().toString().trim();

            buttonSave.setEnabled(!portNumber.isEmpty()&&!ipServer.isEmpty());
            if(!portNumber.isEmpty() && !ipServer.isEmpty())
            buttonSave.setBackgroundColor(getResources().getColor(R.color.colorAccent));

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
