package com.michalraq.proximitylightapp.data;

import android.content.Context;
import android.os.AsyncTask;
import java.util.HashMap;
import java.util.Map;

public class DatabaseHandlerCheckout extends AsyncTask<String, Void, Map<Integer,String> > {
    private DatabaseManager databaseManager;
    private Context context;

    /**
     * Kontruktor
     * @param ctx kontekst aplikacji
     */
    public DatabaseHandlerCheckout (Context ctx){
        context = ctx;
    }

    /**
     * Funkcja wykonywana przed rozpoczęciem głównego zadania wątku. Wykonanie próby połączenia z bazą danych.
     */
    @Override
    protected void onPreExecute() {
        databaseManager= new DatabaseManager(context);

    }

    /**
     * Główne zadanie wątku, uzyskanie wyników z bazy danych.
     * @param strings parametry.
     * @return Mapa z wartościami odpowiadającymi konkretnym pomieszczeniom. W przypadku wystąpienia błędu zwracana jest pusta mapa.
     */
    @Override
    protected Map<Integer,String>  doInBackground(String... strings) {
        Map<Integer,String> empty;
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

    /**
     * Rozłączenie połączenia z bazą danych po wykonaniu zadania.
     * @param aVoid
     */
    @Override
    protected void onPostExecute(Map<Integer,String>  aVoid) {


        if(databaseManager.getConnection()!=null)
            databaseManager.disconnectDatabase();

    }

}
