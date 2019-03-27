package com.michalraq.proximitylightapp.database;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.michalraq.proximitylightapp.R;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
        Map empty;
        if(!databaseManager.connectDatabase()){  empty = new HashMap();
        }else {switch (strings[0]) {
                case "data":
                    return databaseManager.getCheckoutData(strings[1]);
                case "places":
                    return databaseManager.getPlaces();
                default:
                    return null;
            }

        }

        return empty;
    }

    @Override
    protected void onPostExecute(Map<Integer,String>  aVoid) {


        if(databaseManager.getConnection()!=null)
            databaseManager.disconnectDatabase();

    }

}
