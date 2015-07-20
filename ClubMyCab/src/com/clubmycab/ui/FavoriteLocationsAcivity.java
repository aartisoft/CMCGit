package com.clubmycab.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.PlacesAutoCompleteAdapter;
import com.clubmycab.R;
import com.clubmycab.maps.MapUtilityMethods;
import com.clubmycab.model.AddressModel;
import com.clubmycab.ui.FavoriteLocationsAcivity.FavoriteLocationAdapter.ViewHolder;
import com.clubmycab.utility.Log;
import com.clubmycab.utility.StringTags;
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
	View view;
	LayoutInflater inflater;

	Address locationAddress;
	Boolean flagchk;
	String shortName;
	private GoogleMap myMap;
	// Button mapButton;
	LatLng invitemapcenter;
	Location mycurrentlocationobject;
	TextView fromlocation;
	RelativeLayout fromrelative;
	Button doneButtonMap;
	Button cancelButtonMap;
	Button addMoreButton, btnSkip;
	LocationManager locationManager;
	LatLng latlong;
	String shortname;
	
	int remainigfavorites = 3;
	private HashMap<String, String> favLocationHashMap = new HashMap<String, String>();

	private ArrayList<AddressModel> addressModelList;

	boolean notfromregistration = false;

	// Pawan
	private ListView lvFavorateLocation;
	private Context context;
	private ArrayList<String> favoriteTag;
	private ArrayList<String> favoriteAddress;
	private FavoriteLocationAdapter adapter;
	private ViewHolder viewHolder;

	int pos = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorite_location);
		favoriteTag = new ArrayList<String>();
		favoriteAddress = new ArrayList<String>();
		addressModelList = new ArrayList<AddressModel>();

		notfromregistration = getIntent().getBooleanExtra(
				"NotFromRegistration", false);
		context = this;
		flagchk = true;
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		lvFavorateLocation = (ListView) findViewById(R.id.lvFavorateLocation);
		adapter = new FavoriteLocationAdapter(favoriteTag, context);
		lvFavorateLocation.setAdapter(adapter);

		btnSkip = (Button) findViewById(R.id.btnSkip);
		if (notfromregistration) {
			btnSkip.setText("Cancel");

		} else {
			btnSkip.setText("Skip");

		}

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
				//pos = (Integer) viewHolder.btnMap.getTag();
				fromrelative.setVisibility(View.GONE);
				String fromlocationname = fromlocation.getText().toString()
						.trim();
				flagchk = true;

				latlong = invitemapcenter;
				shortname = MapUtilityMethods.getAddressshort(
						FavoriteLocationsAcivity.this, latlong.latitude,
						latlong.longitude);

				locationAddress = null; // reset previous

				viewHolder.aTvLocationAutoComplete.setText(fromlocationname);

				// String jnd =viewHolder.aTvLocationAutoComplete.getText()
				// .toString().trim();

				Geocoder fcoder = new Geocoder(FavoriteLocationsAcivity.this);
				try {
					ArrayList<Address> adresses = (ArrayList<Address>) fcoder
							.getFromLocationName(fromlocationname, 50);

					for (Address add : adresses) {
						locationAddress = add;
					}

					if (locationAddress != null) {
						AddressModel addressModel = new AddressModel();
						addressModel.setAddress(locationAddress);
						addressModel.setShortname(MapUtilityMethods
								.getAddressshort(FavoriteLocationsAcivity.this,
										locationAddress.getLatitude(),
										locationAddress.getLongitude()));
						addressModel.setLongname(fromlocationname);

						// Gson gson = new Gson();
						// String json = gson.toJson(addressModel);

						String tag = favoriteTag.get(pos);

						// if(tag.equalsIgnoreCase(StringTags.TAG_WHERE_LIVE))
						//
						// favLocationHashMap.put(StringTags.TAG_WHERE_LIVE_KEY,
						// json);
						//
						// else
						// if(tag.equalsIgnoreCase(StringTags.TAG_WHERE_WORK))
						//
						// favLocationHashMap.put(StringTags.TAG_WHERE_WORK_key,
						// json);
						// else
						// favLocationHashMap.put(tag, json);

					

						favoriteAddress.set(pos, fromlocationname);
						addressModelList.set(pos, addressModel);

						adapter.notifyDataSetChanged();
					} else {
						Toast.makeText(
								FavoriteLocationsAcivity.this,
								"Sorry, we could not find the location you entered, please try again",
								Toast.LENGTH_LONG).show();
					}

				} catch (Exception e) {
					e.printStackTrace();
					favoriteAddress.set(pos, "");
					//addressModelList.set(pos, null);
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

		SharedPreferences mPrefs11111 = getSharedPreferences(
				"FavoriteLocations", 0);
		final String favoritelocation = mPrefs11111.getString(
				"favoritelocation", "");
		Log.d("FavoriteLocationsAcivity", "favoritelocation : "
				+ favoritelocation);

		if (!favoritelocation.isEmpty()) {

			Gson gson = new Gson();
			HashMap<String, String> hashMap = gson.fromJson(favoritelocation,
					HashMap.class);

			if (hashMap.size() > 0) {
				favLocationHashMap = hashMap;
			}
		}

		if (favLocationHashMap.size() == 0) {

			favLocationHashMap.put("1", "");
			favLocationHashMap.put("2", "");

			SharedPreferences sharedprefernce = getSharedPreferences(
					"FavoriteLocations", 0);

			Gson gson = new Gson();
			String json = gson.toJson(favLocationHashMap);
			SharedPreferences.Editor editor = sharedprefernce.edit();
			editor.putString("favoritelocation", json);
			editor.commit();
			// adapter.notifyDataSetChanged();

		}

		getAllFavoriteAddress();
	}

	public void getAllFavoriteAddress() {

		remainigfavorites = 5 - favLocationHashMap.size();
		if (remainigfavorites == 0)
			addMoreButton.setVisibility(View.GONE);
		else
			addMoreButton.setText("Add More (" + remainigfavorites
					+ " remaining )");

		if (favLocationHashMap.size() > 0) {
			SortedSet<String> keys = new TreeSet<String>(
					favLocationHashMap.keySet());
			// Set keys = favLocationHashMap.keySet();

			Gson gson = new Gson();

			for (Iterator i = keys.iterator(); i.hasNext();) {
				String key = (String) i.next();

				if (key.equalsIgnoreCase(StringTags.TAG_WHERE_LIVE_KEY))
					favoriteTag.add(StringTags.TAG_WHERE_LIVE);
				else if (key.equalsIgnoreCase(StringTags.TAG_WHERE_WORK_KEY))
					favoriteTag.add(StringTags.TAG_WHERE_WORK);
				else
					favoriteTag.add(key);

				Log.d("Key value::", key);
				// Get long address from location

				try {
					AddressModel addressModel = (AddressModel) gson.fromJson(
							favLocationHashMap.get(key), AddressModel.class);
					;
					addressModelList.add(addressModel);
					favoriteAddress.add(addressModel.getLongname());
				} catch (Exception e) {

					favoriteAddress.add("");
					//addressModelList.add(null);
				}

			}
			adapter.notifyDataSetChanged();

		}
		// lvFavorateLocation.setAdapter(adapter);
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

	}

	public void onDoneButtonClick(View v) {

		// if(favoriteAddress.size()>=favLocationHashMap.size()){
		favLocationHashMap.clear();

		for (int i = 0; i < favoriteAddress.size(); i++) {
			
			

			Gson gson = new Gson();
			String json = "";
			if (addressModelList.get(i) == null)
				json = "";
			else
				json = gson.toJson(addressModelList.get(i));

			if (favoriteTag.get(i).equalsIgnoreCase(StringTags.TAG_WHERE_LIVE))
				favLocationHashMap.put(StringTags.TAG_WHERE_LIVE_KEY, json);
			else if (favoriteTag.get(i).equalsIgnoreCase(
					StringTags.TAG_WHERE_WORK))
				favLocationHashMap.put(StringTags.TAG_WHERE_WORK_KEY, json);
			else
				favLocationHashMap.put(favoriteTag.get(i), json);

		}

		// }

		if (favLocationHashMap.size() == 0) {
			Toast.makeText(getApplicationContext(),
					"Fill atleast one location ..", Toast.LENGTH_SHORT).show();
			return;
		}

//		SortedSet<String> keys = new TreeSet<String>(
//				favLocationHashMap.keySet());
		Set keys = favLocationHashMap.keySet();

		for (Iterator i = keys.iterator(); i.hasNext();) {
			String key = (String) i.next();
			
			Log.d(key+"::::",favLocationHashMap.get(key) );

			if (key.equalsIgnoreCase(StringTags.TAG_WHERE_LIVE_KEY)
					|| key.equalsIgnoreCase(StringTags.TAG_WHERE_WORK_KEY)) {

				continue;
			}

			else {

				if (favLocationHashMap.get(key).isEmpty()
						|| favLocationHashMap.get(key).equalsIgnoreCase("")) {

					Toast.makeText(FavoriteLocationsAcivity.this,
							"Please enter valid address for " + key,
							Toast.LENGTH_LONG).show();
					// viewHolder.aTvLocationAutoComplete.setFocusable(true);

					return;
				}
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

	public void addFavoriteLocation(String s) {

		favoriteTag.add(s);
		favoriteAddress.add("");
		addressModelList.add(null);
		// favLocationHashMap.put(s,"");
		adapter.notifyDataSetChanged();

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
				}

				else if (value.equalsIgnoreCase(StringTags.TAG_WHERE_LIVE_KEY)
						|| value.equalsIgnoreCase("0")
						|| value.equalsIgnoreCase(StringTags.TAG_WHERE_WORK_KEY)
						|| value.equalsIgnoreCase(StringTags.TAG_WHERE_LIVE)
						|| value.equalsIgnoreCase(StringTags.TAG_WHERE_WORK)) {

					Toast.makeText(getApplicationContext(),
							"This tag already exists", Toast.LENGTH_SHORT)
							.show();

				} else {
					addFavoriteLocation(value);
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

	// Adapter for show favorite location in listview
	public class FavoriteLocationAdapter extends BaseAdapter {

		private ArrayList<String> data;
		private Context context;
		private LayoutInflater layoutInflater;

		public FavoriteLocationAdapter(ArrayList<String> data, Context context) {
			super();
			this.data = data;
			this.context = context;
			layoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {

			return data.size();
		}

		@Override
		public Object getItem(int position) {

			return data.get(position);
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			//pos = position;

			if (convertView == null) {
				convertView = layoutInflater.inflate(
						R.layout.cutom_favorite_location, null);
				viewHolder = new ViewHolder();
				viewHolder.aTvLocationAutoComplete = (AutoCompleteTextView) convertView
						.findViewById(R.id.aTvLocationAutoComplete);
				viewHolder.ivClearedittextimg = (ImageView) convertView
						.findViewById(R.id.ivClearedittextimg);

				viewHolder.tvLocationTagTextView = (TextView) convertView
						.findViewById(R.id.tvLocationTagTextView);
				viewHolder.btnMap = (Button) convertView
						.findViewById(R.id.btnMap);
				viewHolder.btnDelete = (Button) convertView
						.findViewById(R.id.btnDelete);

				// viewHolder.aTvLocationAutoComplete.setId(position);

				viewHolder.ivClearedittextimg
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								pos = (Integer) v.getTag();

								// Toast.makeText(context, "pos="+pos,
								// Toast.LENGTH_SHORT).show();
								// if(position!=0&&position!=1){
								viewHolder.aTvLocationAutoComplete.setText("");

								favoriteAddress.set(pos, "");
								addressModelList.set(pos,null);

								// map.put(key, map.get(key) + 1);
								// if(favoriteTag.get(position).equalsIgnoreCase(StringTags.TAG_WHERE_LIVE)){
								//
								// }
								// //favLocationHashMap.put(StringTags.TAG_WHERE_LIVE_KEY,"");
								// else
								// if(favoriteTag.get(position).equalsIgnoreCase(StringTags.TAG_WHERE_WORK)){
								//
								// }
								// //favLocationHashMap.put(StringTags.TAG_WHERE_WORK_key,"");
								// else{
								//
								// }

								notifyDataSetChanged();

							}
						});
				viewHolder.btnDelete.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						pos = (Integer) v.getTag();

						// Toast.makeText(context, "delete pos="+position,
						// Toast.LENGTH_SHORT).show();
						if (pos != 0 && pos != 1) {

							// viewHolder.aTvLocationAutoComplete.setText("");

							favLocationHashMap.remove(favoriteTag.get(pos));
							favoriteAddress.remove(pos);
							addressModelList.remove(pos);
							favoriteTag.remove(pos);
							SharedPreferences sharedprefernce = getSharedPreferences(
									"FavoriteLocations", 0);

							Gson gson = new Gson();
							String json = gson.toJson(favLocationHashMap);
							SharedPreferences.Editor editor = sharedprefernce
									.edit();
							editor.putString("favoritelocation", json);
							editor.commit();
							remainigfavorites = remainigfavorites + 1;
							if (remainigfavorites == 0)
								addMoreButton.setVisibility(View.GONE);
							addMoreButton.setText("Add More ("
									+ remainigfavorites + " remaining )");

							notifyDataSetChanged();
						} else {
							Toast.makeText(context,
									"You can't delete this entry",
									Toast.LENGTH_SHORT).show();

						}

					}
				});

				openMap(viewHolder, position);
				setAutoConpteletTextview(viewHolder);
				convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolder) convertView.getTag();
				// viewHolder.aTvLocationAutoComplete.setId(position);

			}
			viewHolder.aTvLocationAutoComplete.setTag(position);

			viewHolder.btnMap.setTag(position);
			viewHolder.btnDelete.setTag(position);
			viewHolder.ivClearedittextimg.setTag(position);
			viewHolder.tvLocationTagTextView.setText(data.get(position));
			viewHolder.aTvLocationAutoComplete.setText(favoriteAddress
					.get(position));

			// if(data.get(position).equalsIgnoreCase(StringTags.TAG_WHERE_LIVE)||data.get(position).equalsIgnoreCase(StringTags.TAG_WHERE_WORK)){
			// viewHolder.btnDelete.setVisibility(View.GONE);
			// }
			if (position == 1 || position == 0) {
				viewHolder.btnDelete.setVisibility(View.GONE);
			} else {
				viewHolder.btnDelete.setVisibility(View.VISIBLE);

			}

			return convertView;
		}

		public class ViewHolder {
			public AutoCompleteTextView aTvLocationAutoComplete;
			public ImageView ivClearedittextimg;
			public Button btnMap, btnDelete;
			public TextView tvLocationTagTextView;

		}

		public void setAutoConpteletTextview(final ViewHolder viewholder) {
			viewholder.aTvLocationAutoComplete
					.setAdapter(new PlacesAutoCompleteAdapter(context,
							R.layout.list_item));
			viewholder.aTvLocationAutoComplete
					.setOnFocusChangeListener(new OnFocusChangeListener() {

						@Override
						public void onFocusChange(final View v, boolean hasFocus) {
							v.post(new Runnable() {
								@Override
								public void run() {
									if (!v.hasFocus()) {
										v.requestFocus();
										v.requestFocusFromTouch();
									}
								}
							});
						}
					});

			viewholder.aTvLocationAutoComplete
					.setOnTouchListener(new View.OnTouchListener() {

						@Override
						public boolean onTouch(View v, MotionEvent event) {

							// pos=(Integer)v.getTag();

							return false;
						}
					});

			viewholder.aTvLocationAutoComplete
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int p, long id) {

							pos = (Integer) viewholder.aTvLocationAutoComplete
									.getTag();

							locationAddress = null; // reset previous
							InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							in.hideSoftInputFromWindow(
									viewholder.aTvLocationAutoComplete
											.getApplicationWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);

							String jnd = viewholder.aTvLocationAutoComplete
									.getText().toString().trim();

							locationAddress = MapUtilityMethods.geocodeAddress(
									jnd, FavoriteLocationsAcivity.this);

							if (locationAddress != null) {
								AddressModel addressModel = new AddressModel();
								addressModel.setAddress(locationAddress);
								addressModel.setShortname(MapUtilityMethods
										.getAddressshort(
												FavoriteLocationsAcivity.this,
												locationAddress.getLatitude(),
												locationAddress.getLongitude()));
								addressModel.setLongname(jnd);

								Gson gson = new Gson();
								String json = gson.toJson(addressModel);

								// if(viewholder.tvLocationTagTextView
								// .getText().toString().equalsIgnoreCase(StringTags.TAG_WHERE_LIVE))
								// favLocationHashMap.put(StringTags.TAG_WHERE_LIVE_KEY,json);
								// else if(viewholder.tvLocationTagTextView
								// .getText().toString().equalsIgnoreCase(StringTags.TAG_WHERE_WORK))
								// favLocationHashMap.put(StringTags.TAG_WHERE_WORK_key,json);
								// else
								// favLocationHashMap.put(viewholder.tvLocationTagTextView
								// .getText().toString(),json);
								//

								Log.d("position::::::", "" + pos);
								addressModelList.set(pos, addressModel);

//								Toast.makeText(context,
//										"on saved index " + pos,
//										Toast.LENGTH_LONG).show();
								favoriteAddress.set(pos, jnd);
								notifyDataSetChanged();

								// invalidAddressHashMap.put(
								// Integer.valueOf(pos), "");
							} else {
								Toast.makeText(
										FavoriteLocationsAcivity.this,
										"Sorry, we could not find the location you entered, please try again",
										Toast.LENGTH_LONG).show();
							}

						}
					});
			viewholder.aTvLocationAutoComplete
					.addTextChangedListener(new TextWatcher() {

						@Override
						public void onTextChanged(CharSequence cs, int arg1,
								int arg2, int arg3) {
							// When user changed the Text

							String text = viewholder.aTvLocationAutoComplete
									.getText().toString().trim();
							if (text.isEmpty() || text.equalsIgnoreCase("")) {
								viewholder.ivClearedittextimg
										.setVisibility(View.GONE);
							} else {
								viewholder.ivClearedittextimg
										.setVisibility(View.VISIBLE);
							}

							Log.d("from onTextChanged", "from onTextChanged");

							if (flagchk) {
								flagchk = false;
							} else {
								shortName = MapUtilityMethods
										.getaddressfromautoplace(
												FavoriteLocationsAcivity.this,
												viewholder.aTvLocationAutoComplete
														.getText().toString()
														.trim());
							}
						}

						@Override
						public void beforeTextChanged(CharSequence arg0,
								int arg1, int arg2, int arg3) {
							// TODO Auto-generated method stub
						}

						@Override
						public void afterTextChanged(Editable editable) {
							// Log.d("afterTextChanged", "editable : " +
							// editable.toString());
							// invalidAddressHashMap.put(
							// Integer.valueOf(pos),
							// editable.toString());
						}
					});

		}

		public void openMap(final ViewHolder viewholder, int w) {

			viewholder.btnMap.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					pos = (Integer) v.getTag();

					// Toast.makeText(context,
					// "pos="+pos,Toast.LENGTH_LONG).show();

					if (viewholder.aTvLocationAutoComplete.getText().toString()
							.trim().isEmpty()
							|| viewholder.aTvLocationAutoComplete.getText()
									.toString().equalsIgnoreCase("")) {

						if (mycurrentlocationobject != null) {

							// Getting latitude of the current location
							double latitude = mycurrentlocationobject
									.getLatitude();

							// Getting longitude of the current location
							double longitude = mycurrentlocationobject
									.getLongitude();

							// Creating a LatLng object for the current location
							LatLng currentlatLng = new LatLng(latitude,
									longitude);

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

						String jnd = viewholder.aTvLocationAutoComplete
								.getText().toString().trim();

						Geocoder coder = new Geocoder(
								FavoriteLocationsAcivity.this);
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
							LatLng currentlatLng = new LatLng(latitude,
									longitude);

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
		}
	}
}
