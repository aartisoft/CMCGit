package com.clubmycab.model;

import android.app.Activity;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by newpc on 23/6/16.
 */
public class LocationModel {
    private boolean isProgressBarEnabled;

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    private Activity activity;

    public boolean isLocationByAddress() {
        return isLocationByAddress;
    }

    public void setLocationByAddress(boolean locationByAddress) {
        isLocationByAddress = locationByAddress;
    }

    public boolean isProgressBarEnabled() {
        return isProgressBarEnabled;
    }

    public void setProgressBarEnabled(boolean progressBarEnabled) {
        isProgressBarEnabled = progressBarEnabled;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    private boolean isLocationByAddress;
    private String address;
    private LatLng latLng;
}
