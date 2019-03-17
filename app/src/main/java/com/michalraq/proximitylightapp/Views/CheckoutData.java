package com.michalraq.proximitylightapp.Views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.michalraq.proximitylightapp.R;
import com.michalraq.proximitylightapp.database.DatabaseHandlerCheckout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class CheckoutData extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static String TAG = "CheckoutData";
    Spinner spinnerPlaces;
    private Map<Integer,String> places,checkoutDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_data);

        spinnerPlaces = findViewById(R.id.spinnerPlaces);

        loadSpinnerData();

    }

    private void loadSpinnerData() {
        places = new HashMap<>();
        try {
                places.putAll(new DatabaseHandlerCheckout(this).execute("places").get());


            List<String> odbiorcyString = new ArrayList<>( places.values());

            ArrayAdapter dataAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, odbiorcyString);
            dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

            spinnerPlaces.setAdapter(dataAdapter);
        }catch (InterruptedException e2){
            Log.e(TAG,"Blad podczas uzupelniania spinnera");
        }catch(ExecutionException e){
            Log.e(TAG,"Blad podczas uzupelniania spinnera");
        }
    }

    /**
     * Wywolanie sqla z wybranym miejscem
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String place = parent.getItemAtPosition(position).toString();
        try {
           checkoutDataList.putAll( new DatabaseHandlerCheckout(this).execute("data",place).get());


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
