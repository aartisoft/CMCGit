package com.clubmycab.ui;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.clubmycab.model.LocationModel;
import com.clubmycab.model.LocationModelListener;

import java.io.IOException;
import java.util.List;

/**
 * Created by newpc on 23/6/16.
 */
public class GetLocationTaskHandler extends AsyncTask<Void, Void, List<Address>>{
    private LocationModelListener listener;
    private  Context context;
    private LocationModel locationModel;
    private boolean isNetworkErro;

    public GetLocationTaskHandler(Context context, LocationModelListener listener,  LocationModel locationModel){
        this.context = context;
        this.listener = listener;
        this.locationModel = locationModel;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        isNetworkErro = false;
        listener.isLoading(true);
    }

    @Override
    protected List<Address> doInBackground(Void... params) {
        Geocoder geo = new Geocoder(context);

        List<Address> listAddresses = null;
        int count = 0;

        while (listAddresses == null && count <3){
            count++;
            try {
                isNetworkErro = false;
                if (locationModel.getLatLng() != null) {
                    listAddresses = geo.getFromLocation(locationModel.getLatLng().latitude,
                            locationModel.getLatLng().longitude, 1);
                }
            } catch (IOException e) {
                e.printStackTrace();
                isNetworkErro = true;

            }
        }



        return listAddresses;
    }

    @Override
    protected void onPostExecute(List<Address> listAddresses) {
        super.onPostExecute(listAddresses);
       try{
           listener.isLoading(false);
           Address _address = null;
           StringBuilder result = new StringBuilder();

           if ((listAddresses != null) && (listAddresses.size() > 0)) {
               _address = listAddresses.get(0);
               _address.setLatitude(locationModel.getLatLng().latitude);
               _address.setLongitude(locationModel.getLatLng().longitude);
               listener.getAddress(_address);

               for (int i = 0; i <= _address.getMaxAddressLineIndex(); i++) {
                   result.append(_address.getAddressLine(i) + " ");
               }
               // joinpoolchangelocationtext.setText(result.toString());
               listener.getStringAddress(result.toString());

           }else if(isNetworkErro){
               listener.getStringAddress("");
               listener.getError("Please check internet connection!!!");
           }
       }catch (Exception e){
           e.printStackTrace();
       }
    }
}
