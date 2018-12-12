package com.michalraq.proximitylightapp.database;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

public class DatabaseHandler extends AsyncTask<String,Void,Void> {
    private static final String PREFERENCES = "myPreferences";
    private static final String OFFICE = "biuro";
    private static final String KITCHEN = "kuchnia";
    private static final String SALOON = "salon";

    Context context;
    DatabaseManager databaseManager;
    private ProgressDialog dialog;
    private SharedPreferences preferences;
    private Boolean office, kitchen, saloon;

    public DatabaseHandler(Context context){
        this.context=context;
        databaseManager = new DatabaseManager(context);
    }
    @Override
    protected void onPreExecute() {
        preferences = context.getSharedPreferences(PREFERENCES, Activity.MODE_PRIVATE);
//        dialog = new ProgressDialog(context);
//        dialog.setMessage("Czekaj...");
//        dialog.show();

        }

    @Override
    protected Void doInBackground(String... fetch) {
        if(!databaseManager.connectDatabase()){
            this.cancel(true);
        }else {
            switch (fetch[0]) {
                case "widok1":

                    office = databaseManager.checkStatusOfLight("biuro");
                    kitchen = databaseManager.checkStatusOfLight("kuchnia");
                    saloon = databaseManager.checkStatusOfLight("salon");

                    break;
                case "widok2":
                    break;
                case "widok3":
                    break;
                default:
                    return null;
            }

        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        SharedPreferences.Editor preferencesEditor = preferences.edit();

        preferencesEditor.putBoolean(OFFICE, office);
        preferencesEditor.putBoolean(KITCHEN, kitchen);
        preferencesEditor.putBoolean(SALOON, saloon);
        preferencesEditor.apply();

        if(databaseManager.getConnection()!=null)
        databaseManager.disconnectDatabase();
    }
}
