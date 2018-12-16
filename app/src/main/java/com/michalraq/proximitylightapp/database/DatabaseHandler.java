package com.michalraq.proximitylightapp.database;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.michalraq.proximitylightapp.R;
import com.michalraq.proximitylightapp.StateOfLight;

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
    private Boolean view1,view2,view3;
    public DatabaseHandler(Context context,Activity activity){
        this.context=context;
        this.activity=activity;
        databaseManager = new DatabaseManager(context);
    }
    @Override
    protected void onPreExecute() {
        view1=false;view2=false;view3=false;
        preferences = context.getSharedPreferences(PREFERENCES, Activity.MODE_PRIVATE);
        dialog = new ProgressDialog(context);
        dialog.setMessage("Czekaj...\nPobieram aktualne dane.");
        dialog.show();
        }

    @Override
    protected Void doInBackground(String... fetch) {
        if(!databaseManager.connectDatabase()){
            Toast.makeText(context, "Wystąpił błąd połączenia z bazą, spróbuj ponownie później.", Toast.LENGTH_SHORT).show();
            this.cancel(true);
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
                    databaseManager.checkTimeWork();
                    break;
                case "widok3":
                    view3=true;
                    break;
                default:
                    return null;
            }

        }
        return null;
    }

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

            Long officeDet = StateOfLight.getValueInSec("biuro");
            Long kitchenDet = StateOfLight.getValueInSec("kuchnia");
            Long saloonDet = StateOfLight.getValueInSec("salon");

            SharedPreferences.Editor preferencesEditor = preferences.edit();
            preferencesEditor.putLong(OFFICEDETAILS, officeDet);
            preferencesEditor.putLong(KITCHENDETAILS, kitchenDet);
            preferencesEditor.putLong(SALOONDETAILS, saloonDet);
            preferencesEditor.apply();

        }



        if(databaseManager.getConnection()!=null)
        databaseManager.disconnectDatabase();
        dialog.dismiss();

    }
}
