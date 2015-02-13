package com.piotrrosa.fotoradaralert_ver3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends ActionBarActivity {

    public static SimpleDateFormat dateFormat = new SimpleDateFormat(Settings.DATE_FORMAT);
    public static SimpleDateFormat shortDateFormat = new SimpleDateFormat(Settings.SHORT_DATE_FORMAT);
    public static SimpleDateFormat dateFormatWholeDay = new SimpleDateFormat(Settings.DATE_FORMAT_WHOLE_DAY);
    public boolean loadingData;
    Location location;
    ArrayList<Location> locations = new ArrayList<Location>();
    ListView locationsListView;
    //define fields
    TextView lastUpdateTextView;
    TextView streetTextView;
    TextView cityTextView;
    TextView additionalDescriptionTextView;
    Button showMoreDays;
    String[][] xmlPullParserArray = {
            {"deviceId", "0"},
            {"street", "0"},
            {"city", "0"},
            {"additionalDescription", "0"},
            {"startDate", "0"},
            {"endDate", "0"}
    };

    static final String KEY_ITEM = "locations";


    int parserArrayIncrement = 0;
    String text ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        loadingData=false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationsListView = (ListView) findViewById(R.id.locationsListView);
        locationsListView.addHeaderView(new View(this));
        locationsListView.addFooterView(new View(this));

        //check updatedData
        SharedPreferences settings = getSharedPreferences(Settings.PREFERENCES, MODE_PRIVATE);

        //init fields
        lastUpdateTextView = (TextView) findViewById(R.id.main_activity_status_textView);

        if(settings!=null) {

            Gson gson = new Gson();
            String json = settings.getString(Settings.LOCATION_LIST, null);
            if(json!=null) {
                Type type = new TypeToken<ArrayList<Location>>() {
                }.getType();
                ArrayList<Location> locations1 = new ArrayList<Location>();

                locations1 = gson.fromJson(json, type);

                locations = locations1;
                Log.d(Settings.DEBUG_TAG, "Locations from SharePref: " + locations1.toString());
                showResult(locations1);

                String prefLastUpdateString = settings.getString(Settings.LAST_UPDATE_PREF, null);
                if (prefLastUpdateString != null) {
                    lastUpdateTextView.setText(getApplicationContext().getString(R.string.last_update) + prefLastUpdateString);
                }
            }
        }

//        refreshData();
        showMoreDays = (Button) findViewById(R.id.showMoreDays);
        showMoreDays.setOnClickListener(showMore);
    }

    private void showResult(ArrayList<Location> locations1) {

        Context context = getApplicationContext();
        ArrayList<Location> actualLocations = new ArrayList<Location>();
        actualLocations = checkActualLocation(locations1);

        if(actualLocations.size()>0) {
            ListAdapter adapter = new ListAdapter(getApplicationContext(), actualLocations);


            locationsListView.setAdapter(adapter);
        }
    }

    public View.OnClickListener showMore = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("locationsList",locations);
            Intent intent = new Intent(view.getContext(), LocationList.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };
    private void refreshData() {
        Log.d(Settings.DEBUG_TAG,"refreshing:"+loadingData);
        if(loadingData==false) {
            Log.d(Settings.DEBUG_TAG,"isLoading: "+loadingData);
            parserArrayIncrement = 0;
            Context context = getApplicationContext();
            CharSequence appStatus = context.getString(R.string.refreshing);
            CustomToast.show(appStatus, context);
            //create sync task
            loadingData=true;
            Log.d(Settings.DEBUG_TAG,"isLoading: "+loadingData);
            new MyAsyncTask().execute(Settings.REQUEST_URL);
        }

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
            Location actualLocation = new Location();
            ArrayList<Location> actualLocations = new ArrayList<Location>();
            actualLocations = checkActualLocation(locations);

            if(actualLocations.size()>0) {
                ListAdapter adapter = new ListAdapter(getApplicationContext(), actualLocations);
                appStatus = context.getString(R.string.updated);
                CustomToast.show(appStatus, context);
                loadingData = false;

                locationsListView.setAdapter(adapter);
            }
            else {
                appStatus = context.getString(R.string.not_active_device);
                CustomToast.show(appStatus, context);
                loadingData = false;

            }
            // locations = filterLocations(locations);


            String lastUpdateString = dateFormat.format(cal.getTime());
            lastUpdateTextView.setText(context.getString(R.string.last_update) + " " + lastUpdateString);
            saveSettings(locations, lastUpdateString);

        }



    }

    


    private ArrayList<Location> checkActualLocation(ArrayList<Location> locations) {
        ArrayList<Location> actualLocations = new ArrayList<Location>();

        Location location = new Location();
        Calendar now = new GregorianCalendar();
        for (Location loc:locations) {

            if(isSameDay(now, loc.getStartDate())) {
                actualLocations.add(loc);
            }
            else {
            }
        }
        return actualLocations;
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {

        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    private void saveSettings(ArrayList<Location> locations, String lastUpdateString)  {

        //prepare locations to save
        Gson gson = new Gson();
        String json = gson.toJson(locations);
        Log.d(Settings.DEBUG_TAG,"Locations:"+locations.toString());
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