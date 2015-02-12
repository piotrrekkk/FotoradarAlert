package com.piotrrosa.fotoradaralert_ver3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

        TextView descriptionTextView = (TextView) convertView.findViewById(R.id.additional_description_text_view);
        ImageView wholeDayImageView = (ImageView) convertView.findViewById(R.id.whole_day_image_view);

        TextView streetTextView = (TextView) convertView.findViewById(R.id.streetOnListTextView);
        TextView cityTextView = (TextView) convertView.findViewById(R.id.cityOnListTextView);

        ImageView startTimeImageView = (ImageView) convertView.findViewById(R.id.start_time_ImageView);
        TextView  startTextView = (TextView) convertView.findViewById(R.id.start_time_textView);

        ImageView endTimeImageView = (ImageView) convertView.findViewById(R.id.end_time_imageView);
        TextView endTextView = (TextView) convertView.findViewById(R.id.end_time_textView);

        Calendar startDate = location.getStartDate();
        Calendar endDate = location.getEndDate();
        if(endDate==null) {
            wholeDayImageView.setVisibility(View.VISIBLE);
            startTimeImageView.setVisibility(View.GONE);
            endTextView.setVisibility(View.GONE);
            endTimeImageView.setVisibility(View.GONE);
            startTextView.setText(getContext().getString(R.string.not_specified_time));

        }
        else {
            wholeDayImageView.setVisibility(View.GONE);
            startTimeImageView.setVisibility(View.VISIBLE);
            endTextView.setVisibility(View.VISIBLE);
            endTimeImageView.setVisibility(View.VISIBLE);
            startTextView.setText(MainActivity.shortDateFormat.format(startDate.getTime()));
            endTextView.setText(MainActivity.shortDateFormat.format(endDate.getTime()));
        }

        descriptionTextView.setText(location.getAdditionalDescription());
        streetTextView.setText(location.getStreet());
        cityTextView.setText(location.getCity());

        return convertView;

    }
}
