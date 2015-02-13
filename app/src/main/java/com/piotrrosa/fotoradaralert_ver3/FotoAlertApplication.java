package com.piotrrosa.fotoradaralert_ver3;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by piotr on 13.02.15.
 */
public class FotoAlertApplication extends Application {
    private Tracker appTracker = null;
    private static final String TRACKING_ID = "UA-15594235-13";

    synchronized Tracker getTracker() {
        if (appTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            appTracker = analytics.newTracker(TRACKING_ID);
        }
        return appTracker;
    }

}
