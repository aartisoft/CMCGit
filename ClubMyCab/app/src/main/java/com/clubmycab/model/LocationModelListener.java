package com.clubmycab.model;

import android.location.Address;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by newpc on 23/6/16.
 */
public interface LocationModelListener {
        void getAddress(Address address);
        void getLatLong(LatLng latLng);
        void getStringAddress(String address);
        void getError(String error);
        void isLoading(boolean isLoading);
}
