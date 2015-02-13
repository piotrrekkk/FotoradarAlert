package com.piotrrosa.fotoradaralert_ver3;

import java.text.SimpleDateFormat;

/**
 * Created by piotr on 03.02.15.
 */
public class Settings {
    public static final String DATE_FORMAT = "d.MM.yyyy HH:mm";
    public static final String SHORT_DATE_FORMAT = "HH:mm";
    public static final String DATE_FORMAT_WHOLE_DAY = "d.MM.yyyy";

    public static final String REQUEST_URL = "http://disparo.pl/android/locations.xml";
    public static final String DEBUG_TAG = "FOTORADAR_ALERT";
    public static final String LAST_UPDATE_PREF = "LAST_UPDATE_PREFERENCE";
    public static final String PREFERENCES = "com.piotrrosa.fotoradaralert.APP_PREFERENCES";
    public static final String LOCATION_LIST = "com.piotrrosa.fotoradaralert.LOCATION_LIST";
    public static final String LOCATIONS_BUNDLE = "com.piotrrosa.fotoradaralert.LOCATIONS_BUNDLE";

    public static SimpleDateFormat FULL_DATE_FORMAT = new SimpleDateFormat(Settings.DATE_FORMAT);
    public static SimpleDateFormat HOURS_DATE_FORMAT = new SimpleDateFormat(Settings.SHORT_DATE_FORMAT);
    public static SimpleDateFormat DAY_DATE_FORMAT = new SimpleDateFormat(Settings.DATE_FORMAT_WHOLE_DAY);

}
