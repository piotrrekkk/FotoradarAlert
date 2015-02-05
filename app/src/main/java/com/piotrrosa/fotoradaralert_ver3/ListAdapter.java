package com.piotrrosa.fotoradaralert_ver3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by piotr on 04.02.15.
 */
public class ListAdapter extends ArrayAdapter<Location> {

    public ListAdapter(Context context, ArrayList<Location> locations) {

        super(context, 0, locations);
    }
    @Override
    public View getView (int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        Location location = getItem(position);
        if(convertView==null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        TextView streetTextView = (TextView) convertView.findViewById(R.id.streetOnListTextView);
        TextView cityTextView = (TextView) convertView.findViewById(R.id.cityOnListTextView);
        streetTextView.setText(location.getStreet());
        cityTextView.setText(location.getCity());

        return convertView;

    }
}
