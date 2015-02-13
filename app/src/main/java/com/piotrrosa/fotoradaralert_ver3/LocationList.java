package com.piotrrosa.fotoradaralert_ver3;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class LocationList extends ActionBarActivity {
    ListView locationsListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);
        ArrayList<Location> locations = new ArrayList<Location>();
        locationsListView = (ListView) findViewById(R.id.locationsListView);
        locations = (ArrayList<Location>) getIntent().getSerializableExtra(Settings.LOCATIONS_BUNDLE);

        ArrayList<Location> currentLocations = new ArrayList<Location>();
        Calendar now = new GregorianCalendar();
        now.add(Calendar.DAY_OF_YEAR, -1);
        for(Location loc:locations) {
            if(loc.getStartDate().after(now)) {
                currentLocations.add(loc);
            }
        }

        if(currentLocations.size()>0) {
            NextDaysListAdapter adapter = new NextDaysListAdapter(this, currentLocations);
            locationsListView.addHeaderView(new View(this));
            locationsListView.addFooterView(new View(this));
            locationsListView.setAdapter(adapter);
        }
        else {
            CharSequence dataStatus = getString(R.string.list_is_empty);
            CustomToast.show(dataStatus, this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_location_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
