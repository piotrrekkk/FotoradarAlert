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

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends ActionBarActivity {

    public static SimpleDateFormat dateFormat = new SimpleDateFormat(Settings.DATE_FORMAT);
    public static SimpleDateFormat dateFormatWholeDay = new SimpleDateFormat(Settings.DATE_FORMAT_WHOLE_DAY);

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationsListView = (ListView) findViewById(R.id.locationsListView);

        //check updatedData
        SharedPreferences settings = getSharedPreferences(Settings.PREFERENCES, MODE_PRIVATE);

        //init fields
        lastUpdateTextView = (TextView) findViewById(R.id.lastUpdateTextView);

        String prefLastUpdateString = settings.getString(Settings.LAST_UPDATE_PREF, null);
        if(prefLastUpdateString!=null) {
            lastUpdateTextView.setText(getApplicationContext().getString(R.string.last_update) + prefLastUpdateString);
        }
        else {

        }
        refreshData();
        showMoreDays = (Button) findViewById(R.id.showMoreDays);
        showMoreDays.setOnClickListener(showMore);
    }


    public View.OnClickListener showMore = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("locationsList",locations);
            Intent intent = new Intent(view.getContext(), LocationList.class);
            intent.putExtras(bundle);
            Log.d(Settings.DEBUG_TAG,"Locations length: "+locations.size());
            startActivity(intent);
        }
    };
    private void refreshData() {
        parserArrayIncrement = 0;
        Context context = getApplicationContext();
        CharSequence appStatus = context.getString(R.string.refreshing);
        CustomToast.show(appStatus, context);

        //create sync task
        new MyAsyncTask().execute(Settings.REQUEST_URL);
    }

    private class MyAsyncTask extends AsyncTask<String, String, String> {



        protected String doInBackground(String... args) {
            locations.clear();
            XmlPullParserFactory factory = null;
            XmlPullParser parser = null;
            try {
                factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                parser = factory.newPullParser();
                parser.setInput(new InputStreamReader(getUrlData(args[0])));
                int eventType = parser.getEventType();

                while (eventType!=XmlPullParser.END_DOCUMENT) {
                    String tagName = parser.getName();
                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            if(tagName.equalsIgnoreCase("location")) {
                                location = new Location();
                            }
                            break;
                        case XmlPullParser.TEXT:
                            text = parser.getText();
                            break;
                        case XmlPullParser.END_TAG:
                            if(tagName.equalsIgnoreCase("location")) {
                                locations.add(location);
                            }
                            else if(tagName.equalsIgnoreCase("deviceId")) {
                                location.setDeviceId(Integer.parseInt(text));
                            }
                            else if(tagName.equalsIgnoreCase("street")) {
                                location.setStreet(text);
                            }
                            else if(tagName.equalsIgnoreCase("city")) {
                                location.setCity(text);
                            }
                            else if(tagName.equalsIgnoreCase("additionalDescription")) {
                                location.setAdditionalDescription(text);
                            }
                            else if(tagName.equalsIgnoreCase("startDate")) {
                                location.setStartDate(text);
                            }
                            else if(tagName.equalsIgnoreCase("endDate")) {
                                if(text.length()<2){
                                    location.setEndDate(null);
                                }
                                else {
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
            }
            finally {

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
//            Log.d(Settings.DEBUG_TAG, "updated");
            Context context = getApplicationContext();
            CharSequence appStatus;
            Location actualLocation = new Location();
            ArrayList<Location> actualLocations = new ArrayList<Location>();
            actualLocations = checkActualLocation(locations);

            if(actualLocations.size()>0) {
                ListAdapter adapter = new ListAdapter(getApplicationContext(), actualLocations);
                locationsListView.setAdapter(adapter);
            }


            // locations = filterLocations(locations);
            if(actualLocation.getStreet()!=null) {
                appStatus = context.getString(R.string.updated);
                CustomToast.show(appStatus, context);
                streetTextView.setText(actualLocation.getStreet());
                cityTextView.setText(actualLocation.getCity());
                additionalDescriptionTextView.setText(actualLocation.getAdditionalDescription());
            }
            else {
                appStatus = context.getString(R.string.not_active_device);
                CustomToast.show(appStatus, context);
            }
            String lastUpdateString = dateFormat.format(cal.getTime());
            lastUpdateTextView.setText(context.getString(R.string.last_update) + " " + lastUpdateString);
            saveSettings(locations, lastUpdateString);
        }

    }

//    private ArrayList<Location> filterLocations(ArrayList<Location> locations) {
//        Calendar now = new GregorianCalendar();
//        for (Location loc:locations) {
//
//
//        }
//    }

    private ArrayList<Location> checkActualLocation(ArrayList<Location> locations) {
        ArrayList<Location> actualLocations = new ArrayList<Location>();

        Location location = new Location();
        Calendar now = new GregorianCalendar();
        for (Location loc:locations) {

            if(now.after(loc.getStartDate()) && now.before(loc.getEndDate())) {
                actualLocations.add(loc);
//                Log.d(Settings.DEBUG_TAG,"Found");
            }
            else if(isSameDay(now, loc.getStartDate())) {
                actualLocations.add(loc);
            }
            else {
//                Log.d(Settings.DEBUG_TAG,"Not found");
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

    private void saveSettings(ArrayList<Location> locations, String lastUpdateString) {
//        Log.d(Settings.DEBUG_TAG, "Save settings");
        SharedPreferences sharedPreferences = getSharedPreferences(Settings.PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Settings.LAST_UPDATE_PREF,lastUpdateString);
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
            case R.id.refresh_data:
                refreshData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}