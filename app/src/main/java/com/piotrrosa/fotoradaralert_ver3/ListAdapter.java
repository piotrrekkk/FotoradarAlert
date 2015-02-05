package com.piotrrosa.fotoradaralert_ver3;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

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

        TextView  dateTextView = (TextView) convertView.findViewById(R.id.dateOnListTextView);
        TextView hoursTextView = (TextView) convertView.findViewById(R.id.hoursOnListTextView);

        Calendar startDate = location.getStartDate();
        Calendar endDate = location.getEndDate();
        Log.d(Settings.DEBUG_TAG, "Start DATE:" + startDate);
        Log.d(Settings.DEBUG_TAG, "END DATE:" + endDate);
        if(endDate==null) {
            String date = MainActivity.dateFormatWholeDay.format(startDate.getTime());
            dateTextView.setText(date);
            hoursTextView.setText(getContext().getString(R.string.whole_day));
        }
        else {
            dateTextView.setText("Od: "+MainActivity.dateFormat.format(startDate.getTime()));
            hoursTextView.setText("Do: "+MainActivity.dateFormat.format(endDate.getTime()));
        }
        streetTextView.setText(location.getStreet());
        cityTextView.setText(location.getCity());

        return convertView;

    }
}
