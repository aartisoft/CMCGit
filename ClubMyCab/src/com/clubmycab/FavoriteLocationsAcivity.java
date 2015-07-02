package com.clubmycab;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.clubmycab.model.AddressModel;
import com.clubmycab.utility.Log;

public class FavoriteLocationsAcivity extends Activity {


	ArrayList<AddressModel> locationDetails = new ArrayList<AddressModel>();
	LinearLayout scrollViewLinear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorite_location);

		scrollViewLinear = (LinearLayout) findViewById(R.id.idScrollViewLinearLayout);
		LayoutInflater inflater =  (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.cutom_favorite_location, null);
		scrollViewLinear.addView(view);
		
		inflater =  (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.cutom_favorite_location, null);
		scrollViewLinear.addView(view);
		

	}

	private Address geocodeAddress(String addressString) {
		Address addressReturn = null;
		Geocoder geocoder = new Geocoder(this);
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

	public String getaddressfromautoplace(Context ctx, String str) {
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.favorite_locations_acivity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
