package com.clubmycab.maps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.clubmycab.utility.Log;

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

			if (addresses.size() > 0) {
				Address address = addresses.get(0);

				for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {

					if (i == 1) {
						if (address.getAddressLine(i) == null
								|| address.getAddressLine(i).isEmpty()) {

							result.append(address.getLocality().toString()
									.trim());

						} else {

							if (address.getAddressLine(i).contains(",")) {
								String[] arr = address.getAddressLine(i).split(
										",");
								result.append(arr[arr.length - 1] + ", "
										+ address.getLocality());
							} else {

								result.append(address.getAddressLine(i)
										.toString().trim()
										+ ", " + address.getLocality());
							}
						}
					}
				}
			}
		} catch (IOException e) {
			Log.e("tag", e.getMessage());
		}

		return result.toString().trim();
	}

}
