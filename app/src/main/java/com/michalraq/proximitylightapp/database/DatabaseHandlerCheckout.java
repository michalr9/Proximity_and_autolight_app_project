package com.michalraq.proximitylightapp.database;

import android.content.Context;
import android.os.AsyncTask;

import java.util.Map;

public class DatabaseHandlerCheckout extends AsyncTask<String, Void, Map<Integer,String> > {
    private DatabaseManager databaseManager;
    private Context context;


    public DatabaseHandlerCheckout (Context ctx){
        context = ctx;
    }

    @Override
    protected void onPreExecute() {
        databaseManager= new DatabaseManager(context);
    }


    @Override
    protected Map<Integer,String>  doInBackground(String... strings) {
        if (databaseManager.connectDatabase()) {

            switch (strings[0]) {
                case "data":
                    return databaseManager.getCheckoutData(strings[1]);
                case "places":
                    return databaseManager.getPlaces();
            }

        }

        return null;
    }

    @Override
    protected void onPostExecute(Map<Integer,String>  aVoid) {


        if(databaseManager.getConnection()!=null)
            databaseManager.disconnectDatabase();

    }

}
