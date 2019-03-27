package com.michalraq.proximitylightapp.Views;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.michalraq.proximitylightapp.CheckoutDataAdapter;
import com.michalraq.proximitylightapp.R;
import com.michalraq.proximitylightapp.database.DatabaseHandlerCheckout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class CheckoutData extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static String TAG = "CheckoutData";
    private Spinner spinnerPlaces;
    private Map<Integer,String> places;
    private RecyclerView rvCheckoutData ;
    private CheckoutDataAdapter checkoutDataAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_data);

        spinnerPlaces = findViewById(R.id.spinnerPlaces);
        rvCheckoutData =  findViewById(R.id.checkoutRecyclerView);

        rvCheckoutData.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvCheckoutData.setLayoutManager(layoutManager);

        loadSpinnerData();
        spinnerPlaces.setOnItemSelectedListener(this);

        checkoutDataAdapter = new CheckoutDataAdapter();
        rvCheckoutData.setAdapter(checkoutDataAdapter);

        if(places.isEmpty() && checkoutDataAdapter.getItemCount() == 0){
            Toast.makeText(this,"Błąd połączenia z bazą! Sprawdź połączenie z internetem.",Toast.LENGTH_LONG).show();
            Intent menu = new Intent(this,MainMenu.class);
            startActivity(menu);
        }

    }

    private void loadSpinnerData() {
        places = new HashMap<>();
        try {
                places.putAll(new DatabaseHandlerCheckout(this).execute("places").get());

            List<String> places = new ArrayList<>( this.places.values());

            ArrayAdapter dataAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, places);
            dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spinnerPlaces.setAdapter(dataAdapter);

        }catch (InterruptedException e2){
            Log.e(TAG,"Blad podczas uzupelniania spinnera");
        }catch(ExecutionException e){
            Log.e(TAG,"Blad podczas uzupelniania spinnera");
        }
    }

    /**
     * Wywolanie sqla z wybranym miejscem odswiezenie dapatera z danymi
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
         String place = parent.getItemAtPosition(position).toString();
        try {
            checkoutDataAdapter.setList( new DatabaseHandlerCheckout(this).execute("data", place).get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        checkoutDataAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
