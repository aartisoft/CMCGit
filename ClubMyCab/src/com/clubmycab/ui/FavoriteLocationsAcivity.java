package com.clubmycab.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.PlacesAutoCompleteAdapter;
import com.clubmycab.R;
import com.clubmycab.maps.MapUtilityMethods;
import com.clubmycab.model.AddressModel;
import com.clubmycab.utility.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

public class FavoriteLocationsAcivity extends FragmentActivity implements
		LocationListener {

	ArrayList<AddressModel> locationDetails = new ArrayList<AddressModel>();
	LinearLayout scrollViewLinear;
	View view;
	LayoutInflater inflater;
	TextView locationTagName;
	AutoCompleteTextView locationAutoCompleteTextView;
	Address locationAddress;
	Boolean flagchk;
	ImageView clearedittextImage;
	String shortName;
	private GoogleMap myMap;
	Button mapButton;
	LatLng invitemapcenter;
	Location mycurrentlocationobject;
	TextView fromlocation;
	RelativeLayout fromrelative;
	Button doneButtonMap;
	Button cancelButtonMap;
	Button addMoreButton;
	LocationManager locationManager;
	LatLng latlong;
	String shortname;
	int currentIndex = 0;
	int currentSelectedIndex;
	int remainigfavorites = 3;
	HashMap<String, String> favLocationHashMap = new HashMap<String, String>();

	HashMap<Integer, String> invalidAddressHashMap = new HashMap<Integer, String>();

	AddressModel addressModel;

	boolean notfromregistration = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorite_location);

		notfromregistration = getIntent().getBooleanExtra(
				"NotFromRegistration", false);

		flagchk = true;

		fromrelative = (RelativeLayout) findViewById(R.id.fromrelative);

		fromlocation = (TextView) findViewById(R.id.fromlocation);
		fromlocation.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));

		doneButtonMap = (Button) findViewById(R.id.idDoneButtonMap);
		doneButtonMap.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

		cancelButtonMap = (Button) findViewById(R.id.idCancelButtonMap);
		cancelButtonMap.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

		addMoreButton = (Button) findViewById(R.id.addMore);
		addMoreButton.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

		doneButtonMap.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				fromrelative.setVisibility(View.GONE);
				String fromlocationname = fromlocation.getText().toString()
						.trim();
				flagchk = true;

				latlong = invitemapcenter;
				shortname = MapUtilityMethods.getAddressshort(
						FavoriteLocationsAcivity.this, latlong.latitude,
						latlong.longitude);

				locationAddress = null; // reset previous

				String newTag = "customlocationAutoCompleteTextView"
						+ Integer.toString(currentSelectedIndex);
				View parentview = FavoriteLocationsAcivity.this
						.findViewById(android.R.id.content);
				View childView = parentview.findViewWithTag(newTag);

				Log.d("Value", "" + childView.getTag());

				((AutoCompleteTextView) childView).setText(fromlocationname);

				String jnd = ((AutoCompleteTextView) childView).getText()
						.toString().trim();

				Geocoder fcoder = new Geocoder(FavoriteLocationsAcivity.this);
				try {
					ArrayList<Address> adresses = (ArrayList<Address>) fcoder
							.getFromLocationName(jnd, 50);

					for (Address add : adresses) {
						locationAddress = add;
					}

					if (locationAddress != null) {
						addressModel = new AddressModel();
						addressModel.setAddress(locationAddress);
						addressModel.setShortname(MapUtilityMethods
								.getAddressshort(FavoriteLocationsAcivity.this,
										locationAddress.getLatitude(),
										locationAddress.getLongitude()));
						addressModel.setLongname(fromlocationname);

						newTag = "customlocationTagNameTextView"
								+ Integer.toString(currentSelectedIndex);
						parentview = FavoriteLocationsAcivity.this
								.findViewById(android.R.id.content);
						childView = parentview.findViewWithTag(newTag);

						Log.d("Value", "" + childView.getTag());

						Gson gson = new Gson();
						String json = gson.toJson(addressModel);

						favLocationHashMap.put(((TextView) childView).getText()
								.toString(), json);

						invalidAddressHashMap.put(
								Integer.valueOf(currentSelectedIndex), "");
					} else {
						Toast.makeText(
								FavoriteLocationsAcivity.this,
								"Sorry, we could not find the location you entered, please try again",
								Toast.LENGTH_LONG).show();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		cancelButtonMap.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				fromrelative.setVisibility(View.GONE);
			}
		});

		if (notfromregistration) {
			SharedPreferences mPrefs11111 = getSharedPreferences(
					"FavoriteLocations", 0);
			final String favoritelocation = mPrefs11111.getString(
					"favoritelocation", "");
			Log.d("FavoriteLocationsAcivity", "favoritelocation : "
					+ favoritelocation);

			if (!favoritelocation.isEmpty()) {

				Gson gson = new Gson();
				HashMap<String, String> hashMap = gson.fromJson(
						favoritelocation, HashMap.class);

				if (hashMap.size() > 0) {
					favLocationHashMap = hashMap;
				}
			}
		}

		addFavoriteLocationView("Where do you live?");
		addFavoriteLocationView("Where do you work?");

		if (favLocationHashMap.size() > 0) {
			for (String key : favLocationHashMap.keySet()) {
				if (!key.equals("Where do you live?")
						&& !key.equals("Where do you work?")) {
					addFavoriteLocationView(key);
				}
			}
		}

	}

	public void onDoneButtonClick(View v) {

		if (favLocationHashMap.size() == 0) {
			Toast.makeText(getApplicationContext(),
					"Fill atleast one location ..", Toast.LENGTH_SHORT).show();
			return;
		}

		for (Integer key : invalidAddressHashMap.keySet()) {
			String string = invalidAddressHashMap.get(key);
			if (!string.isEmpty() && string != "") {
				Toast.makeText(
						FavoriteLocationsAcivity.this,
						string
								+ " is not a valid address, please tap on one of the suggested places or try with a different address",
						Toast.LENGTH_LONG).show();
				return;
			}
		}

		SharedPreferences sharedprefernce = getSharedPreferences(
				"FavoriteLocations", 0);

		Gson gson = new Gson();
		String json = gson.toJson(favLocationHashMap);
		SharedPreferences.Editor editor = sharedprefernce.edit();
		editor.putString("favoritelocation", json);
		editor.commit();

		if (notfromregistration) {
			finish();
		} else {
			Intent mainIntent = new Intent(FavoriteLocationsAcivity.this,
					FirstLoginWalletsActivity.class);
			startActivity(mainIntent);
		}

		// String jsonstr = sharedprefernce.getString("favoritelocation", "");
		// HashMap<String, String> hashmap = gson.fromJson(jsonstr,
		// HashMap.class);
		// AddressModel addressModel = (AddressModel) gson.fromJson(
		// hashmap.get("Where do you live?"), AddressModel.class);
		//
		// Log.d("Data::", addressModel.toString());
	}

	public void onSkipButtonClick(View v) {

		if (notfromregistration) {
			finish();
		} else {
			Intent mainIntent = new Intent(FavoriteLocationsAcivity.this,
					FirstLoginWalletsActivity.class);
			startActivity(mainIntent);
		}
	}

	public void addFavoriteLocationView(String tagname) {
		currentIndex++;
		scrollViewLinear = (LinearLayout) findViewById(R.id.idScrollViewLinearLayout);
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.cutom_favorite_location, null);

		locationTagName = (TextView) view
				.findViewById(R.id.idLocationTagTextView);
		locationTagName.setText(tagname);
		locationTagName.setTag("customlocationTagNameTextView" + currentIndex);

		clearedittextImage = (ImageView) view
				.findViewById(R.id.idclearedittextimg);
		clearedittextImage.setVisibility(View.GONE);
		clearedittextImage.setTag("customlocationClearButton" + currentIndex);

		locationAutoCompleteTextView = (AutoCompleteTextView) view
				.findViewById(R.id.idLocationAutoComplete);
		if (favLocationHashMap.size() > 0) {
			Gson gson = new Gson();
			AddressModel addressModel = (AddressModel) gson.fromJson(
					favLocationHashMap.get(tagname), AddressModel.class);
			if (addressModel != null && !addressModel.getShortname().isEmpty()) {
				locationAutoCompleteTextView.setText(addressModel
						.getShortname());
			}
		}
		locationAutoCompleteTextView.setAdapter(new PlacesAutoCompleteAdapter(
				this, R.layout.list_item));
		locationAutoCompleteTextView
				.setTag("customlocationAutoCompleteTextView" + currentIndex);

		locationAutoCompleteTextView
				.setOnTouchListener(new View.OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						String tag = (String) v.getTag();
						currentSelectedIndex = Integer.parseInt(tag
								.substring(tag.length() - 1));
						return false;
					}
				});

		locationAutoCompleteTextView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						locationAddress = null; // reset previous
						InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						in.hideSoftInputFromWindow(locationAutoCompleteTextView
								.getApplicationWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);

						String newTag = "customlocationAutoCompleteTextView"
								+ Integer.toString(currentSelectedIndex);
						View parentview = FavoriteLocationsAcivity.this
								.findViewById(android.R.id.content);
						View childView = parentview.findViewWithTag(newTag);

						Log.d("Value", "" + childView.getTag());

						String jnd = ((AutoCompleteTextView) childView)
								.getText().toString().trim();

						locationAddress = MapUtilityMethods.geocodeAddress(jnd,
								FavoriteLocationsAcivity.this);

						if (locationAddress != null) {
							addressModel = new AddressModel();
							addressModel.setAddress(locationAddress);
							addressModel.setShortname(MapUtilityMethods
									.getAddressshort(
											FavoriteLocationsAcivity.this,
											locationAddress.getLatitude(),
											locationAddress.getLongitude()));
							addressModel.setLongname(jnd);

							newTag = "customlocationTagNameTextView"
									+ Integer.toString(currentSelectedIndex);
							// parentview = FavoriteLocationsAcivity.this
							// .findViewById(android.R.id.content);
							childView = parentview.findViewWithTag(newTag);

							Log.d("Value", "" + childView.getTag());

							Gson gson = new Gson();
							String json = gson.toJson(addressModel);

							favLocationHashMap.put(((TextView) childView)
									.getText().toString(), json);

							invalidAddressHashMap.put(
									Integer.valueOf(currentSelectedIndex), "");
						} else {
							Toast.makeText(
									FavoriteLocationsAcivity.this,
									"Sorry, we could not find the location you entered, please try again",
									Toast.LENGTH_LONG).show();
						}

					}
				});
		locationAutoCompleteTextView.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				// When user changed the Text

				String text = locationAutoCompleteTextView.getText().toString()
						.trim();
				if (text.isEmpty() || text.equalsIgnoreCase("")) {
					clearedittextImage.setVisibility(View.GONE);
				} else {
					clearedittextImage.setVisibility(View.VISIBLE);
				}

				Log.d("from onTextChanged", "from onTextChanged");

				if (flagchk) {
					flagchk = false;
				} else {
					shortName = MapUtilityMethods.getaddressfromautoplace(
							FavoriteLocationsAcivity.this,
							locationAutoCompleteTextView.getText().toString()
									.trim());
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable editable) {
				// Log.d("afterTextChanged", "editable : " +
				// editable.toString());
				invalidAddressHashMap.put(
						Integer.valueOf(currentSelectedIndex),
						editable.toString());
			}
		});

		mapButton = (Button) view.findViewById(R.id.idMapButton);
		mapButton.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));
		mapButton.setTag("customlocationMapButton" + currentIndex);

		mapButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String tag = (String) v.getTag();
				currentSelectedIndex = Integer.parseInt(tag.substring(tag
						.length() - 1));
				Log.d("currentSelectedIndex", "" + currentSelectedIndex);

				if (locationAutoCompleteTextView.getText().toString().trim()
						.isEmpty()
						|| locationAutoCompleteTextView.getText().toString()
								.equalsIgnoreCase("")) {

					if (mycurrentlocationobject != null) {

						// Getting latitude of the current location
						double latitude = mycurrentlocationobject.getLatitude();

						// Getting longitude of the current location
						double longitude = mycurrentlocationobject
								.getLongitude();

						// Creating a LatLng object for the current location
						LatLng currentlatLng = new LatLng(latitude, longitude);

						// Showing the current location in Google Map
						myMap.moveCamera(CameraUpdateFactory
								.newLatLng(currentlatLng));

						// Zoom in the Google Map
						myMap.animateCamera(CameraUpdateFactory.zoomTo(15));

						String address = MapUtilityMethods.getAddress(
								FavoriteLocationsAcivity.this, latitude,
								longitude);

						fromlocation.setText(address);
						fromrelative.setVisibility(View.VISIBLE);

					} else {

						// no network provider is enabled
						AlertDialog.Builder dialog = new AlertDialog.Builder(
								FavoriteLocationsAcivity.this);
						dialog.setMessage("Please check your location services");
						dialog.setPositiveButton("Retry",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(
											DialogInterface paramDialogInterface,
											int paramInt) {
										Intent intent = getIntent();
										intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

										finish();

										startActivity(intent);

									}
								});
						dialog.setNegativeButton("Settings",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(
											DialogInterface paramDialogInterface,
											int paramInt) {
										Intent myIntent = new Intent(
												Settings.ACTION_LOCATION_SOURCE_SETTINGS);
										startActivity(myIntent);
										// get gps

									}
								});
						dialog.show();

					}

				} else {

					String jnd = locationAutoCompleteTextView.getText()
							.toString().trim();

					Geocoder coder = new Geocoder(FavoriteLocationsAcivity.this);
					try {
						ArrayList<Address> adresses = (ArrayList<Address>) coder
								.getFromLocationName(jnd, 50);
						double longitude = 0;
						double latitude = 0;
						for (Address add : adresses) {
							longitude = add.getLongitude();
							latitude = add.getLatitude();
						}

						// Creating a LatLng object for the current location
						LatLng currentlatLng = new LatLng(latitude, longitude);

						// Showing the current location in Google Map
						myMap.moveCamera(CameraUpdateFactory
								.newLatLng(currentlatLng));

						// Zoom in the Google Map
						myMap.animateCamera(CameraUpdateFactory.zoomTo(15));

						fromlocation.setText(jnd);

					} catch (IOException e) {
						e.printStackTrace();
					}

					fromrelative.setVisibility(View.VISIBLE);

				}
			}
		});

		myMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.frommap)).getMap();

		myMap.setMyLocationEnabled(true);

		myMap.setOnCameraChangeListener(new OnCameraChangeListener() {

			@Override
			public void onCameraChange(CameraPosition cameraPosition) {

				invitemapcenter = cameraPosition.target;

				String address = MapUtilityMethods.getAddress(
						FavoriteLocationsAcivity.this,
						invitemapcenter.latitude, invitemapcenter.longitude);
				Log.d("address", "" + address);

				fromlocation.setText(address);

			}
		});

		scrollViewLinear.addView(view);
	}

	public Location getLocation() {
		Location location = null;
		try {
			locationManager = (LocationManager) this
					.getSystemService(LOCATION_SERVICE);

			// getting GPS status
			boolean isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			boolean isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
				AlertDialog.Builder dialog = new AlertDialog.Builder(this);
				dialog.setMessage("Please check your location services");
				dialog.setPositiveButton("Retry",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(
									DialogInterface paramDialogInterface,
									int paramInt) {
								Intent intent = getIntent();
								intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

								finish();

								startActivity(intent);

							}
						});
				dialog.setNegativeButton("Settings",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(
									DialogInterface paramDialogInterface,
									int paramInt) {
								Intent myIntent = new Intent(
										Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								startActivity(myIntent);
								// get gps

							}
						});
				dialog.show();
				return null;
			} else {

				double lat = 0;
				double lng = 0;
				// get the location by gps
				if (isGPSEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER, 20000, 1, this);
						Log.d("GPS Enabled", "GPS Enabled");
						if (locationManager != null) {
							location = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								lat = location.getLatitude();
								lng = location.getLongitude();
							}
						}
					}
				}

				// First get location from Network Provider
				if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER, 20000, 1, this);
					Log.d("Network", "Network");
					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							lat = location.getLatitude();
							lng = location.getLongitude();
						}
					}
				}

				Log.d("lat", "" + lat);
				Log.d("lng", "" + lng);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return location;
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		Log.d("onStart", "onStart");

		// Check if Internet present
		if (!isOnline()) {
			return;
		}

		Location location = getLocation();

		if (location != null) {
			onLocationChanged(location);
		}

		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		Log.d("onStop", "onStop");

		if (locationManager != null)
			locationManager.removeUpdates(this);

		super.onStop();
	}

	@Override
	public void onBackPressed() {

		if (fromrelative.getVisibility() == View.VISIBLE) {
			fromrelative.setVisibility(View.GONE);
		} else {
			super.onBackPressed();
		}
	}

	public void addMoreClick(View v) {

		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("");
		alert.setMessage("What do you want to call this location? e.g. Airport, MyAdda");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();

				if (input.getText().toString().equals("")) {
					Toast.makeText(getApplicationContext(), "Enter tag name.",
							Toast.LENGTH_SHORT).show();
					return;
				} else {
					addFavoriteLocationView(value);
					// Do something with value!
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
					remainigfavorites = remainigfavorites - 1;
					if (remainigfavorites == 0)
						addMoreButton.setVisibility(View.GONE);
					addMoreButton.setText("Add More (" + remainigfavorites
							+ " remaining )");
				}
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
					}
				});

		alert.show();
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

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

		mycurrentlocationobject = location;

	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}
}
