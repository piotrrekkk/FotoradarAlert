package com.piotrrosa.fotoradaralert_ver3;

import android.util.Log;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by piotr on 03.02.15.
 */
public class Location implements Serializable{

    private int deviceId;
    private String street;
    private String city;
    private String additionalDescription;
    private Calendar startDate;
    private Calendar endDate;

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAdditionalDescription() {
        return additionalDescription;
    }

    public void setAdditionalDescription(String additionalDescription) {
        this.additionalDescription = additionalDescription;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) throws ParseException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(Settings.DATE_FORMAT);
        cal.setTime(sdf.parse(startDate));
        this.startDate = cal;
    }

    public Calendar getEndDate() {
        return endDate;
    }
    public String endDateToSring() {
        String endDateText ="";
        return endDateText;
    }

    public String startDateToSring() {
        String startDateText ="";
        return startDateText;
    }

    public void setEndDate(String endDate) throws ParseException {
        if(endDate!="0" && endDate!=null && endDate!="") {
            Log.d(Settings.DEBUG_TAG, "NOT Empty or 0");
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat(Settings.DATE_FORMAT);
            cal.setTime(sdf.parse(endDate));
            this.endDate = cal;
        }
        else {
            Log.d(Settings.DEBUG_TAG, "Empty or 0");
            this.endDate=null;}
    }

    public String toString() {
        return getDeviceId()+", "+
                getStreet()+", "+
                getCity()+", "+
                getAdditionalDescription();
    }
}
