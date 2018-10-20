package com.michalraq.proximitylightapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.michalraq.proximitylightapp.estimote.ProximityContent;
import com.michalraq.proximitylightapp.estimote.Utils;

import java.util.ArrayList;
import java.util.List;

public class ManageBeaconsAdapter extends BaseAdapter {
    Context context;

    public ManageBeaconsAdapter(Context context){
        this.context=context;
    }

    private List<ProximityContent> beaconContent = new ArrayList<>();

    public void setBeaconContent(List<ProximityContent> allContents) {
        this.beaconContent = allContents;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;

            convertView = inflater.inflate(R.layout.content_view, parent, false);
        }

        ProximityContent content = beaconContent.get(position);

        TextView title = convertView.findViewById(R.id.title);

        title.setText(content.getTitle());

        convertView.setBackgroundColor(Utils.getEstimoteColor(content.getTitle()));

        return convertView;    }
}
