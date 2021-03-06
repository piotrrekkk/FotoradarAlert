package com.piotrrosa.fotoradaralert_ver3;

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
    private String geoCoordinates;

    public String getGeoCoordinates() {
        return geoCoordinates;
    }

    public void setGeoCoordinates(String geoCoordinates) {
        this.geoCoordinates = geoCoordinates;
    }



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

    public void setEndDate(String endDate) throws ParseException {
        if(endDate!="0" && endDate!=null && endDate!="") {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat(Settings.DATE_FORMAT);
            cal.setTime(sdf.parse(endDate));
            this.endDate = cal;
        }
        else {
            this.endDate=null;}
    }

    public String toString() {
        return getDeviceId()+", "+
                getStreet()+", "+
                getCity()+", "+
                getAdditionalDescription();
    }
}
