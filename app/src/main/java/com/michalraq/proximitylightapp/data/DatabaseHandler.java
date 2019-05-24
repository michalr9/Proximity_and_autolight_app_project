package com.michalraq.proximitylightapp.data;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.ToggleButton;

import com.michalraq.proximitylightapp.R;

import java.util.concurrent.TimeUnit;

public class DatabaseHandler extends AsyncTask<String,Void,Void> {
    private static final String PREFERENCES = "myPreferences";
    private static final String OFFICE = "biuro";
    private static final String KITCHEN = "kuchnia";
    private static final String SALOON = "salon";
    private static final String OFFICEDETAILS = "officeDetails";
    private static final String KITCHENDETAILS = "kitchenDetails";
    private static final String SALOONDETAILS = "saloonDetails";

    private Context context;
    private Activity activity;
    private DatabaseManager databaseManager;
    private ProgressDialog dialog;
    private SharedPreferences preferences;
    private Boolean office, kitchen, saloon;
    private Boolean view1,view2;

    /**
     * Konstruktor
     * @param context kontekst aplikacji
     * @param activity
     */
    public DatabaseHandler(Context context,Activity activity){
        this.context=context;
        this.activity=activity;
        databaseManager = new DatabaseManager(context);
    }

    /**
     * Funkcja wywoływana przed uruchomieniem wątku, gdzie następuje inicjalizacja zmiennych oraz dialogu.
     */
    @Override
    protected void onPreExecute() {
        view1=false;view2=false;
        preferences = context.getSharedPreferences(PREFERENCES, Activity.MODE_PRIVATE);
        dialog = new ProgressDialog(context);
        dialog.setMessage(context.getString(R.string.dialogMessageWait));
        dialog.show();
        }

    /**
     * Główna funkcjonalność wątku. Wykonanie zapytania w bazie danych na podstawie otrzymanych parametrów.
     * @param fetch parametry
     * @return
     */
    @Override
    protected Void doInBackground(String... fetch) {
        if(!databaseManager.connectDatabase()){
            try {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.setMessage(context.getString(R.string.dialogMessageConnectWithDatabaseFailure));
                }
            });
                TimeUnit.SECONDS.sleep(4);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                dialog.dismiss();
                this.cancel(true);
            }
        }else {
            switch (fetch[0]) {
                case "widok1":
                    view1=true;
                    office = databaseManager.checkStatusOfLight("biuro");
                    kitchen = databaseManager.checkStatusOfLight("kuchnia");
                    saloon = databaseManager.checkStatusOfLight("salon");

                    break;
                case "widok2":
                    view2=true;
                    databaseManager.checkTimeWork(fetch[1],fetch[2]);

                    break;
                default:
                    return null;
            }

        }
        return null;
    }

    /**
     * Funkcja gdzie po zakończeniu odpytywania bazy danych wyniki sa zapisywane w pamięci wewnętrznej oraz zmiennych.
     * @param aVoid
     */
    @Override
    protected void onPostExecute(Void aVoid) {
        if(view1) {
            SharedPreferences.Editor preferencesEditor = preferences.edit();
            preferencesEditor.putBoolean(OFFICE, office);
            preferencesEditor.putBoolean(KITCHEN, kitchen);
            preferencesEditor.putBoolean(SALOON, saloon);
            preferencesEditor.apply();

            ToggleButton kitchenBut = activity.findViewById(R.id.button_status_kitchen);
            kitchenBut.setChecked(this.kitchen);
            ToggleButton officeBut = activity.findViewById(R.id.button_status_office);
            officeBut.setChecked(this.office);
            ToggleButton saloonBut = activity.findViewById(R.id.button_status_saloon);
            saloonBut.setChecked(this.saloon);
        }
        if(view2){

            Long officeDet = LightContent.getValueInSec("biuro");
            Long kitchenDet = LightContent.getValueInSec("kuchnia");
            Long saloonDet = LightContent.getValueInSec("salon");

            SharedPreferences.Editor preferencesEditor = preferences.edit();
            preferencesEditor.putLong(OFFICEDETAILS, officeDet);
            preferencesEditor.putLong(KITCHENDETAILS, kitchenDet);
            preferencesEditor.putLong(SALOONDETAILS, saloonDet);
            preferencesEditor.apply();

        }
/*
  Rozłączenie z bazą i zamknięcie okna dialogowego.
 */
        if(databaseManager.getConnection()!=null)
        databaseManager.disconnectDatabase();
        dialog.dismiss();

    }
}
