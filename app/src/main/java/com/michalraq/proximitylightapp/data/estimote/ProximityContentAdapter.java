package com.michalraq.proximitylightapp.data.estimote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import  com.michalraq.proximitylightapp.R;
import com.michalraq.proximitylightapp.Util.Utils;

import java.util.ArrayList;
import java.util.List;


public class ProximityContentAdapter extends BaseAdapter {

    private Context context;

    public ProximityContentAdapter(Context context) {
        this.context = context;
    }

    private List<ProximityContent> nearbyContent = new ArrayList<>();

    public void setNearbyContent(List<ProximityContent> nearbyContent) {
        this.nearbyContent = nearbyContent;
    }

    @Override
    public int getCount() {
        return nearbyContent.size();
    }

    @Override
    public Object getItem(int position) {
        return nearbyContent.get(position);
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

        ProximityContent content = nearbyContent.get(position);

        TextView title = convertView.findViewById(R.id.title);
        TextView attachment = convertView.findViewById(R.id.attachment);

        title.setText(content.getTitle());
        attachment.setText(content.getAttachment());

        convertView.setBackgroundColor(Utils.getEstimoteColor(content.getTitle()));

        return convertView;
    }
}
