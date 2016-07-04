package com.clubmycab.maps;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.text.TextUtils;

import com.clubmycab.utility.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapUtilityMethods {

    public static String getAddress(Context ctx, double latitude,
                                    double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude,
                    longitude, 1);

            if (addresses.size() > 0) {
                Address address = addresses.get(0);

                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    result.append(address.getAddressLine(i) + " ");
                }
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        return result.toString();
    }

    public static Address geocodeAddress(String addressString, Context context) {
        Address addressReturn = null;
        Geocoder geocoder = new Geocoder(context);
        try {
            ArrayList<Address> arrayList = (ArrayList<Address>) geocoder
                    .getFromLocationName(addressString, 1);
            Log.d("geocodeAddress", "geocodeAddress : " + arrayList.toString());
            if (arrayList.size() > 0) {
                addressReturn = arrayList.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return addressReturn;
    }

    public static String getaddressfromautoplace(Context ctx, String str) {
        StringBuilder result = new StringBuilder();
        StringBuilder result1 = new StringBuilder();
        String totext = str;

        if (totext.contains(",")) {
            String[] arr = totext.split(",");

            if (arr.length <= 2) {
                result.append(totext + ", ");
            } else {
                for (int i = 0; i < arr.length; i++) {

                    if (i == arr.length - 1 || i == arr.length - 2) {

                    } else {
                        result.append(arr[i].toString().trim() + ", ");
                    }
                }
            }

            result = result.deleteCharAt(result.length() - 2);

            String[] arr1 = result.toString().split(",");

            result1 = new StringBuilder();
            for (int i1 = 0; i1 < arr1.length; i1++) {

                if (i1 == arr1.length - 1 || i1 == arr1.length - 2) {

                    result1.append(arr1[i1].toString().trim() + ", ");
                }
            }

            result1 = result1.deleteCharAt(result1.length() - 2);
        } else {
            result1.append(totext);
        }
        return result1.toString();
    }

    public static String getAddressshort(Context ctx, double latitude,
                                         double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude,
                    longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                if (address != null) {
                    if (address.getMaxAddressLineIndex() < 2)

                    {
                        if (!TextUtils.isEmpty(address.getAddressLine(0))) {
                            result.append(address.getAddressLine(0).toString().trim());
                            if (!TextUtils.isEmpty(address.getAddressLine(1))) {
                                result.append(" , " + address.getAddressLine(1).toString().trim());

                            }
                        } else if (!TextUtils.isEmpty(address.getAddressLine(1))) {
                            result.append(address.getAddressLine(1).toString().trim());

                        }
                    } else

                    {
                        if (address.getMaxAddressLineIndex() == 2) {
                            if (address.getAddressLine(0) == null || address.getAddressLine(0).isEmpty()) {
                                result.append(address.getLocality().toString().trim());
                            } else {
                                if (address.getAddressLine(0).contains(",")) {
                                    String[] arr = address.getAddressLine(0).split(",");
                                    result.append(arr[arr.length - 1] + ", " + address.getLocality());
                                } else {
                                    result.append(address.getAddressLine(0).toString().trim() + ", " + address.getLocality());
                                }
                            }

                        } else {
                            if (address.getAddressLine(1) == null || address.getAddressLine(1).isEmpty()) {
                                result.append(address.getLocality().toString().trim());
                            } else {
                                if (address.getAddressLine(1).contains(",")) {
                                    String[] arr = address.getAddressLine(1).split(",");
                                    result.append(arr[arr.length - 1] + ", " + address.getLocality());
                                } else {
                                    result.append(address.getAddressLine(1).toString().trim() + ", " + address.getLocality());
                                }
                            }
                        }
                    }
                }
            } else {
                result.append("NA");
            }


        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        return result.toString().trim();
    }

    public static String getAddressshort(Context ctx, Address address) {
        StringBuilder result = new StringBuilder();
        try {
                   if (address != null) {
                    if (address.getMaxAddressLineIndex() < 2)

                    {
                        if (!TextUtils.isEmpty(address.getAddressLine(0))) {
                            result.append(address.getAddressLine(0).toString().trim());
                            if (!TextUtils.isEmpty(address.getAddressLine(1))) {
                                result.append(" , " + address.getAddressLine(1).toString().trim());

                            }
                        } else if (!TextUtils.isEmpty(address.getAddressLine(1))) {
                            result.append(address.getAddressLine(1).toString().trim());

                        }
                    } else

                    {
                        if (address.getMaxAddressLineIndex() == 2) {
                            if (address.getAddressLine(0) == null || address.getAddressLine(0).isEmpty()) {
                                result.append(address.getLocality().toString().trim());
                            } else {
                                if (address.getAddressLine(0).contains(",")) {
                                    String[] arr = address.getAddressLine(0).split(",");
                                    result.append(arr[arr.length - 1] + ", " + address.getLocality());
                                } else {
                                    result.append(address.getAddressLine(0).toString().trim() + ", " + address.getLocality());
                                }
                            }

                        } else {
                            if (address.getAddressLine(1) == null || address.getAddressLine(1).isEmpty()) {
                                result.append(address.getLocality().toString().trim());
                            } else {
                                if (address.getAddressLine(1).contains(",")) {
                                    String[] arr = address.getAddressLine(1).split(",");
                                    result.append(arr[arr.length - 1] + ", " + address.getLocality());
                                } else {
                                    result.append(address.getAddressLine(1).toString().trim() + ", " + address.getLocality());
                                }
                            }
                        }
                    }
                }else {
                    result.append("NA");
                }



        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

        return result.toString().trim();
    }



}
