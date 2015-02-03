package com.piotrrosa.fotoradaralert_ver3;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class LocationList extends ActionBarActivity {
    ListView locationsListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);
        ArrayList<Location> locations = new ArrayList<Location>();
        locationsListView = (ListView) findViewById(R.id.locationsListView);
        locations = (ArrayList<Location>) getIntent().getSerializableExtra("locationsList");
        Log.d(Settings.DEBUG_TAG, "Locations length on second activity: " + locations.size());

        if(locations.size()>0) {
            ArrayAdapter<Location> adapter = new ArrayAdapter<Location>(this,R.layout.list_item, locations);
            locationsListView.setAdapter(adapter);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_location_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
