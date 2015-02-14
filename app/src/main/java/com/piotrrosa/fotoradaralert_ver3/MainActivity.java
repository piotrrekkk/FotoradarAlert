package com.piotrrosa.fotoradaralert_ver3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    Data data = new Data();
    Location location;
    ArrayList<Location> locations = new ArrayList<Location>();
    ListView locationsListView;
    TextView lastUpdateTextView;
    Button showMoreDays;

    int parserArrayIncrement = 0;
    String text ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        data.setDataStatus(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Analitycs
        Tracker t = ((FotoAlertApplication) getApplication()).getTracker();
        t.setScreenName("Main Activity");
        t.send(new HitBuilders.AppViewBuilder().build());

        locationsListView = (ListView) findViewById(R.id.locationsListView);
        locationsListView.addHeaderView(new View(this));
        locationsListView.addFooterView(new View(this));


        //get data from Shared Preferences
        SharedPreferences sharedPreferences = getSharedPreferences(Settings.PREFERENCES, MODE_PRIVATE);

        lastUpdateTextView = (TextView) findViewById(R.id.main_activity_status_textView);
        showMoreDays = (Button) findViewById(R.id.showMoreDays);
        showMoreDays.setOnClickListener(showMore);

        if(sharedPreferences!=null) {
            Gson gson = new Gson();
            String locations_json = sharedPreferences.getString(Settings.LOCATION_LIST, null);
            if(locations_json!=null) {
                Type type = new TypeToken<ArrayList<Location>>() {
                }.getType();
                ArrayList<Location> locations_sharedPreferences = new ArrayList<Location>();

                locations_sharedPreferences = gson.fromJson(locations_json, type);

                locations = locations_sharedPreferences;
                updateList(locations_sharedPreferences);

                String lastUpdateInfo = sharedPreferences.getString(Settings.LAST_UPDATE_PREF, null);
                if (lastUpdateInfo != null) {
                    lastUpdateTextView.setText(getApplicationContext().getString(R.string.last_update) + lastUpdateInfo);
                }
            }
        }
        else {
            refreshData();
        }
    }

    private void updateList(ArrayList<Location> locations_sharedPreferences) {

        ArrayList<Location> actualLocations = new ArrayList<Location>();
        actualLocations = checkActualLocations(locations_sharedPreferences);

        if(actualLocations.size()>0) {
            ListAdapter adapter = new ListAdapter(getApplicationContext(), actualLocations);
            locationsListView.setAdapter(adapter);
            locationsListView.setOnItemClickListener(this);
        }
    }

    public View.OnClickListener showMore = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Settings.LOCATIONS_BUNDLE,locations);
            Intent intent = new Intent(view.getContext(), LocationList.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };
    private void refreshData() {
        if(data.isLoading()==false) {
            parserArrayIncrement = 0;
            Context context = getApplicationContext();
            CharSequence appStatus = context.getString(R.string.refreshing);
            CustomToast.show(appStatus, context);

            //create sync task
            data.setDataStatus(true);
            new MyAsyncTask().execute(Settings.REQUEST_URL);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        System.out.println("row Number Clicked "+ i);
    }

    private class MyAsyncTask extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {

                XmlPullParserFactory factory = null;
                XmlPullParser parser = null;
                try {
                    factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    parser = factory.newPullParser();
                    parser.setInput(new InputStreamReader(getUrlData(args[0])));

                    int eventType = parser.getEventType();
                    locations.clear();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        String tagName = parser.getName();
                        switch (eventType) {
                            case XmlPullParser.START_TAG:
                                if (tagName.equalsIgnoreCase("location")) {
                                    location = new Location();
                                }
                                break;
                            case XmlPullParser.TEXT:
                                text = parser.getText();
                                break;
                            case XmlPullParser.END_TAG:
                                if (tagName.equalsIgnoreCase("location")) {
                                    locations.add(location);
                                } else if (tagName.equalsIgnoreCase("deviceId")) {
                                    location.setDeviceId(Integer.parseInt(text));
                                } else if (tagName.equalsIgnoreCase("street")) {
                                    location.setStreet(text);
                                } else if (tagName.equalsIgnoreCase("city")) {
                                    location.setCity(text);
                                } else if (tagName.equalsIgnoreCase("additionalDescription")) {
                                    location.setAdditionalDescription(text);
                                } else if (tagName.equalsIgnoreCase("startDate")) {
                                    location.setStartDate(text);
                                } else if (tagName.equalsIgnoreCase("endDate")) {
                                    if (text.length() < 2) {
                                        location.setEndDate(null);
                                    } else {
                                        location.setEndDate(text);
                                    }
                                }
                                else if (tagName.equalsIgnoreCase("coordinates")) {
                                    location.setGeoCoordinates(text);
                                }

                                break;
                            default:
                                break;
                        }
                        eventType = parser.next();
                    }


                } catch (XmlPullParserException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } finally {

                }
                return null;

        }
        private InputStream getUrlData(String url) throws URISyntaxException, IOException {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet method = new HttpGet(new URI(url));
            HttpResponse response = httpClient.execute(method);
            return response.getEntity().getContent();
        }

        protected void onPostExecute(String result) {

            Calendar cal = new GregorianCalendar();
            Context context = getApplicationContext();
            CharSequence appStatus;
            ArrayList<Location> actualLocations = new ArrayList<Location>();
            actualLocations = checkActualLocations(locations);

            if(actualLocations.size()>0) {
                ListAdapter adapter = new ListAdapter(getApplicationContext(), actualLocations);
                appStatus = context.getString(R.string.updated);
                CustomToast.show(appStatus, context);
                data.setDataStatus(false);
                locationsListView.setAdapter(adapter);
                locationsListView.setOnItemClickListener((AdapterView.OnItemClickListener) this);
            }
            else {
                appStatus = context.getString(R.string.not_active_device);
                CustomToast.show(appStatus, context);
                data.setDataStatus(false);
            }

            String lastUpdateString = Settings.FULL_DATE_FORMAT.format(cal.getTime());
            lastUpdateTextView.setText(context.getString(R.string.last_update) + " " + lastUpdateString);
            saveSettings(locations, lastUpdateString);
        }
    }

    


    private ArrayList<Location> checkActualLocations(ArrayList<Location> locations) {
        ArrayList<Location> actualLocations = new ArrayList<Location>();

        Calendar now = new GregorianCalendar();
        for (Location loc:locations) {
            if(Dates.isSameDay(now, loc.getStartDate())) {
                actualLocations.add(loc);
            }
        }
        return actualLocations;
    }

    private void saveSettings(ArrayList<Location> locations, String lastUpdateString)  {
        Gson gson = new Gson();
        String json = gson.toJson(locations);
        SharedPreferences sharedPreferences = getSharedPreferences(Settings.PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Settings.LAST_UPDATE_PREF,lastUpdateString);
        editor.putString(Settings.LOCATION_LIST,json);
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.refresh_data_item:

                refreshData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}