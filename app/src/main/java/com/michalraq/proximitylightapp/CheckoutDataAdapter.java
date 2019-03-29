package com.michalraq.proximitylightapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Klasa adaptera pobierająca dane do wyświetlania historii wiadomości.
 */
public class CheckoutDataAdapter extends RecyclerView.Adapter<CheckoutDataAdapter.ViewHolder>{

    private final String TAG = "CheckoutDataAdapter";
    List<String> dataList;
    Map<Integer,String> list;
    public CheckoutDataAdapter(){
        dataList= new ArrayList<>();
        list = new HashMap<>();
    }

    /**
     * Klasa odpowiadajaca za uzupelnianie kolejnych wierszy na podstawie stworzonego szablonu
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.tvText);
        }

    }

    @NonNull
    @Override
    public CheckoutDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        final View item = inflater.inflate(R.layout.item_checkout_data, parent, false);
        return new ViewHolder(item);
    }

    /**
     * Przekazanie wartosci do item layout
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull CheckoutDataAdapter.ViewHolder holder, int position) {
        if(dataList!=null) {
            String currentItem = dataList.get(position);
            holder.text.setText(currentItem);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setList(Map<Integer, String> list) {
        this.list = list;
        dataList.clear();
        dataList.addAll(list.values());
        Collections.sort(dataList);
    }
}
