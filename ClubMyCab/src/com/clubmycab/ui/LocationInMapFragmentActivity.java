package com.clubmycab.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.clubmycab.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationInMapFragmentActivity extends FragmentActivity {

	String address;
	String latlongmap;

	GoogleMap sharelocationmap;
	TextView sharelocationtext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_in_map);

		// Check if Internet present
		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					LocationInMapFragmentActivity.this);
			builder.setMessage("No Internet Connection. Please check and try again!");
			builder.setCancelable(false);

			builder.setPositiveButton("Retry",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = getIntent();
							intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

							finish();

							startActivity(intent);

						}
					});

			builder.show();
			return;
		}

		Bundle extras = getIntent().getExtras();
		address = extras.getString("address");
		latlongmap = extras.getString("latlongmap");

		sharelocationtext = (TextView) findViewById(R.id.sharelocationtext);
		sharelocationtext.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));

		sharelocationtext.setText(address);

		sharelocationmap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.sharelocationmap)).getMap();
		sharelocationmap.setMyLocationEnabled(true);

		String[] arr = latlongmap.split(",");

		LatLng pos = new LatLng(Double.parseDouble(arr[0]),
				Double.parseDouble(arr[1]));

		sharelocationmap.addMarker(new MarkerOptions().position(pos).icon(
				BitmapDescriptorFactory
						.fromResource(R.drawable.sharelocationmarker)));

		// Showing the current location in Google Map
		sharelocationmap.moveCamera(CameraUpdateFactory.newLatLng(pos));

		// Zoom in the Google Map
		sharelocationmap.animateCamera(CameraUpdateFactory.zoomTo(15));
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}
}
