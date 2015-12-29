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
	// LayoutInflater inflater;

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
	// private ListView lvFavorateLocation;
	private Context context;
	private ArrayList<String> favoriteTag;
	private ArrayList<String> favoriteAddress;

	// int pos = 0;
	// setting ll
	private LinearLayout ll1, ll2, ll3, ll4, ll5;
	public AutoCompleteTextView aTvLocationAutoComplete1,
			aTvLocationAutoComplete2, aTvLocationAutoComplete3,
			aTvLocationAutoComplete4, aTvLocationAutoComplete5;
	public ImageView ivClearedittextimg1, ivClearedittextimg2,
			ivClearedittextimg3, ivClearedittextimg4, ivClearedittextimg5;
	public Button btnMap1, btnMap2, btnMap3, btnMap4, btnMap5;
	private Button btnDelete2, btnDelete3, btnDelete4, btnDelete5;
	public TextView tvLocationTagTextView1, tvLocationTagTextView2,
			tvLocationTagTextView3, tvLocationTagTextView4,
			tvLocationTagTextView5;
	public int index = 0;

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
		// inflater = (LayoutInflater)
		// getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// lvFavorateLocation = (ListView)
		// findViewById(R.id.lvFavorateLocation);

		btnSkip = (Button) findViewById(R.id.btnSkip);
		if (notfromregistration) {
			btnSkip.setText("Cancel");

		} else {
			btnSkip.setText("Skip");

		}
		findIds();

		LinearLayout dummy = (LinearLayout) findViewById(R.id.dummy);
		// dummy.requestFocus();
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
				// pos = (Integer) viewHolder.btnMap.getTag();
				fromrelative.setVisibility(View.GONE);
				String fromlocationname = fromlocation.getText().toString()
						.trim();
				flagchk = true;

				latlong = invitemapcenter;
				shortname = MapUtilityMethods.getAddressshort(
						FavoriteLocationsAcivity.this, latlong.latitude,
						latlong.longitude);

				locationAddress = null; // reset previous

				if (index == 0) {
					aTvLocationAutoComplete1.setText(fromlocationname);

				}

				else if (index == 1) {
					aTvLocationAutoComplete2.setText(fromlocationname);

				} else if (index == 2)
					aTvLocationAutoComplete3.setText(fromlocationname);
				else if (index == 3)
					aTvLocationAutoComplete4.setText(fromlocationname);
				else if (index == 4)
					aTvLocationAutoComplete5.setText(fromlocationname);

				// viewHolder.aTvLocationAutoComplete.setText(fromlocationname);
				// viewHolder.aTvLocationAutoComplete.setFocusable(true);
				// viewHolder.aTvLocationAutoComplete.setFocusableInTouchMode(true);

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

						// String tag = favoriteTag.get(index);

						favoriteAddress.set(index, fromlocationname);
						addressModelList.set(index, addressModel);

					} else {
						Toast.makeText(
								FavoriteLocationsAcivity.this,
								"Sorry, we could not find the location you entered, please try again",
								Toast.LENGTH_LONG).show();
					}

				} catch (Exception e) {
					e.printStackTrace();
					// favoriteAddress.set(index, "");
					// addressModelList.set(pos, null);
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

	public void findIds() {

		ll1 = (LinearLayout) findViewById(R.id.ll1);
		ll2 = (LinearLayout) findViewById(R.id.ll2);
		ll3 = (LinearLayout) findViewById(R.id.ll3);
		ll4 = (LinearLayout) findViewById(R.id.ll4);
		ll5 = (LinearLayout) findViewById(R.id.ll5);

		aTvLocationAutoComplete1 = (AutoCompleteTextView) findViewById(R.id.aTvLocationAutoComplete1);
		aTvLocationAutoComplete2 = (AutoCompleteTextView) findViewById(R.id.aTvLocationAutoComplete2);
		aTvLocationAutoComplete3 = (AutoCompleteTextView) findViewById(R.id.aTvLocationAutoComplete3);
		aTvLocationAutoComplete4 = (AutoCompleteTextView) findViewById(R.id.aTvLocationAutoComplete4);
		aTvLocationAutoComplete5 = (AutoCompleteTextView) findViewById(R.id.aTvLocationAutoComplete5);

		ivClearedittextimg1 = (ImageView) findViewById(R.id.ivClearedittextimg1);
		ivClearedittextimg2 = (ImageView) findViewById(R.id.ivClearedittextimg2);
		ivClearedittextimg3 = (ImageView) findViewById(R.id.ivClearedittextimg3);
		ivClearedittextimg4 = (ImageView) findViewById(R.id.ivClearedittextimg4);
		ivClearedittextimg5 = (ImageView) findViewById(R.id.ivClearedittextimg5);

		btnMap1 = (Button) findViewById(R.id.btnMap1);
		btnMap2 = (Button) findViewById(R.id.btnMap2);
		btnMap3 = (Button) findViewById(R.id.btnMap3);
		btnMap4 = (Button) findViewById(R.id.btnMap4);
		btnMap5 = (Button) findViewById(R.id.btnMap5);

		btnDelete2 = (Button) findViewById(R.id.btnDelete2);
		btnDelete3 = (Button) findViewById(R.id.btnDelete3);
		btnDelete4 = (Button) findViewById(R.id.btnDelete4);
		btnDelete5 = (Button) findViewById(R.id.btnDelete5);

		tvLocationTagTextView1 = (TextView) findViewById(R.id.tvLocationTagTextView1);
		tvLocationTagTextView2 = (TextView) findViewById(R.id.tvLocationTagTextView2);
		tvLocationTagTextView3 = (TextView) findViewById(R.id.tvLocationTagTextView3);
		tvLocationTagTextView4 = (TextView) findViewById(R.id.tvLocationTagTextView4);
		tvLocationTagTextView5 = (TextView) findViewById(R.id.tvLocationTagTextView5);

		setAutoConpteletTextview1();
		setAutoConpteletTextview2();
		setAutoConpteletTextview3();
		setAutoConpteletTextview4();
		setAutoConpteletTextview5();
		openMap1();
		openMap2();
		openMap3();
		openMap4();
		openMap5();

		setClearandDeleteClick();

	}

	public void setClearandDeleteClick() {

		ivClearedittextimg1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				index = 0;
				aTvLocationAutoComplete1.setText("");

				favoriteAddress.set(index, "");
				addressModelList.set(index, null);

			}
		});

		ivClearedittextimg2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				index = 1;

				aTvLocationAutoComplete2.setText("");

				favoriteAddress.set(index, "");
				addressModelList.set(index, null);

			}
		});

		ivClearedittextimg3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				index = 2;

				aTvLocationAutoComplete3.setText("");

				favoriteAddress.set(index, "");
				addressModelList.set(index, null);

			}
		});

		btnDelete3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				index = 2;

				// Toast.makeText(context, "delete pos="+position,
				// Toast.LENGTH_SHORT).show();

				// viewHolder.aTvLocationAutoComplete.setText("");

				favLocationHashMap.remove(favoriteTag.get(index));
				favoriteAddress.remove(index);
				addressModelList.remove(index);
				favoriteTag.remove(index);
				SharedPreferences sharedprefernce = getSharedPreferences(
						"FavoriteLocations", 0);

				Gson gson = new Gson();
				String json = gson.toJson(favLocationHashMap);
				SharedPreferences.Editor editor = sharedprefernce.edit();
				editor.putString("favoritelocation", json);
				editor.commit();
				remainigfavorites = remainigfavorites + 1;
				if (remainigfavorites == 0)
					addMoreButton.setVisibility(View.GONE);
				else
					addMoreButton.setVisibility(View.VISIBLE);

				addMoreButton.setText("Add More (" + remainigfavorites
						+ " remaining )");
				deleteFavoriteLocaiton();

			}
		});

		ivClearedittextimg4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				index = 3;
				aTvLocationAutoComplete4.setText("");

				favoriteAddress.set(index, "");
				addressModelList.set(index, null);

			}
		});

		btnDelete4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				index = 3;

				// Toast.makeText(context, "delete pos="+position,
				// Toast.LENGTH_SHORT).show();

				// viewHolder.aTvLocationAutoComplete.setText("");

				favLocationHashMap.remove(favoriteTag.get(index));
				favoriteAddress.remove(index);
				addressModelList.remove(index);
				favoriteTag.remove(index);
				SharedPreferences sharedprefernce = getSharedPreferences(
						"FavoriteLocations", 0);

				Gson gson = new Gson();
				String json = gson.toJson(favLocationHashMap);
				SharedPreferences.Editor editor = sharedprefernce.edit();
				editor.putString("favoritelocation", json);
				editor.commit();
				remainigfavorites = remainigfavorites + 1;
				if (remainigfavorites == 0)
					addMoreButton.setVisibility(View.GONE);
				else
					addMoreButton.setVisibility(View.VISIBLE);
				addMoreButton.setText("Add More (" + remainigfavorites
						+ " remaining )");

				deleteFavoriteLocaiton();

			}
		});

		ivClearedittextimg5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				index = 4;

				aTvLocationAutoComplete5.setText("");

				favoriteAddress.set(index, "");
				addressModelList.set(index, null);

			}
		});

		btnDelete5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				index = 4;

				// Toast.makeText(context, "delete pos="+position,
				// Toast.LENGTH_SHORT).show();

				// viewHolder.aTvLocationAutoComplete.setText("");

				favLocationHashMap.remove(favoriteTag.get(index));
				favoriteAddress.remove(index);
				addressModelList.remove(index);
				favoriteTag.remove(index);
				SharedPreferences sharedprefernce = getSharedPreferences(
						"FavoriteLocations", 0);

				Gson gson = new Gson();
				String json = gson.toJson(favLocationHashMap);
				SharedPreferences.Editor editor = sharedprefernce.edit();
				editor.putString("favoritelocation", json);
				editor.commit();
				remainigfavorites = remainigfavorites + 1;
				if (remainigfavorites == 0)
					addMoreButton.setVisibility(View.GONE);
				else
					addMoreButton.setVisibility(View.VISIBLE);
				addMoreButton.setText("Add More (" + remainigfavorites
						+ " remaining )");

				deleteFavoriteLocaiton();

			}
		});
	}

	public void setAutoConpteletTextview1() {

		aTvLocationAutoComplete1.setAdapter(new PlacesAutoCompleteAdapter(
				context, R.layout.list_item));
		// aTvLocationAutoComplete1
		// .setOnFocusChangeListener(new OnFocusChangeListener() {
		//
		// @Override
		// public void onFocusChange(final View v, boolean hasFocus) {
		// v.post(new Runnable() {
		// @Override
		// public void run() {
		// if (!v.hasFocus()) {
		// v.requestFocus();
		// //v.requestFocusFromTouch();
		// }
		// }
		// });
		// }
		// });

		aTvLocationAutoComplete1.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				// pos=(Integer)v.getTag();

				return false;
			}
		});

		aTvLocationAutoComplete1
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int p, long id) {

						index = 0;

						String jnd = aTvLocationAutoComplete1.getText()
								.toString().trim();

						locationAddress = MapUtilityMethods.geocodeAddress(jnd,
								FavoriteLocationsAcivity.this);

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

							// Log.d("position::::::", "" + pos);
							addressModelList.set(index, addressModel);

							favoriteAddress.set(index, jnd);
							locationAddress = null; // reset previous
							InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							in.hideSoftInputFromWindow(aTvLocationAutoComplete1
									.getApplicationWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);

						} else {
							Toast.makeText(
									FavoriteLocationsAcivity.this,
									"Sorry, we could not find the location you entered, please try again",
									Toast.LENGTH_LONG).show();
						}

					}
				});
		aTvLocationAutoComplete1.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				// When user changed the Text

				String text = aTvLocationAutoComplete1.getText().toString()
						.trim();
				if (text.isEmpty() || text.equalsIgnoreCase("")) {
					ivClearedittextimg1.setVisibility(View.GONE);
				} else {
					ivClearedittextimg1.setVisibility(View.VISIBLE);
				}

				Log.d("from onTextChanged", "from onTextChanged");

				if (flagchk) {
					flagchk = false;
				} else {
					shortName = MapUtilityMethods.getaddressfromautoplace(
							FavoriteLocationsAcivity.this,
							aTvLocationAutoComplete1.getText().toString()
									.trim());
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		});

	}

	public void getAllFavoriteAddress() {

		// favoriteAddress.clear();
		// favoriteTag.clear();
		// addressModelList.clear();

		remainigfavorites = 5 - favLocationHashMap.size();
		if (remainigfavorites == 0)
			addMoreButton.setVisibility(View.GONE);
		else
			addMoreButton.setText("Add More (" + remainigfavorites
					+ " remaining )");

		if (favLocationHashMap.size() > 0) {

			if (favLocationHashMap.size() == 2) {

				ll3.setVisibility(View.GONE);
				ll4.setVisibility(View.GONE);
				ll5.setVisibility(View.GONE);
			} else if (favLocationHashMap.size() == 3) {
				ll4.setVisibility(View.GONE);
				ll5.setVisibility(View.GONE);
			} else if (favLocationHashMap.size() == 4) {
				ll5.setVisibility(View.GONE);
			} else {

			}
			SortedSet<String> keys = new TreeSet<String>(
					favLocationHashMap.keySet());
			// Set keys = favLocationHashMap.keySet();

			Gson gson = new Gson();

			for (Iterator i = keys.iterator(); i.hasNext();) {
				String key = (String) i.next();

				if (key.equalsIgnoreCase(StringTags.TAG_WHERE_LIVE_KEY)) {
					favoriteTag.add(StringTags.TAG_WHERE_LIVE);
					// tvLocationTagTextView1.setText(StringTags.TAG_WHERE_LIVE);
				} else if (key.equalsIgnoreCase(StringTags.TAG_WHERE_WORK_KEY)) {
					favoriteTag.add(StringTags.TAG_WHERE_WORK);
					// tvLocationTagTextView2.setText(StringTags.TAG_WHERE_WORK);

				} else
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
					// addressModelList.add(null);
				}

			}

			for (int i = 0; i < favoriteTag.size(); i++) {

				if (i == 0) {

					tvLocationTagTextView1.setText(favoriteTag.get(i));
					aTvLocationAutoComplete1.setText(favoriteAddress.get(i));

				} else if (i == 1) {

					tvLocationTagTextView2.setText(favoriteTag.get(i));
					aTvLocationAutoComplete2.setText(favoriteAddress.get(i));

				}

				else if (i == 2) {
					tvLocationTagTextView3.setText(favoriteTag.get(i));
					aTvLocationAutoComplete3.setText(favoriteAddress.get(i));

				} else if (i == 3) {

					tvLocationTagTextView4.setText(favoriteTag.get(i));
					aTvLocationAutoComplete4.setText(favoriteAddress.get(i));
				} else if (i == 4) {
					tvLocationTagTextView5.setText(favoriteTag.get(i));
					aTvLocationAutoComplete5.setText(favoriteAddress.get(i));

				}

			}

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

			if (favoriteTag.get(i).equalsIgnoreCase(StringTags.TAG_WHERE_LIVE) && json.length() > 0)
				favLocationHashMap.put(StringTags.TAG_WHERE_LIVE_KEY, json);
			else if (favoriteTag.get(i).equalsIgnoreCase(
					StringTags.TAG_WHERE_WORK) && json.length() > 0)
				favLocationHashMap.put(StringTags.TAG_WHERE_WORK_KEY, json);
			else if (json.length() > 0)
				favLocationHashMap.put(favoriteTag.get(i), json);

		}

		// }

		Log.d("onDoneButtonClick",
				"favLocationHashMap : " + favLocationHashMap.size()
						+ " favoriteAddress : " + favoriteAddress.size());
		if (favLocationHashMap.size() == 0) {
			Toast.makeText(getApplicationContext(),
					"Fill atleast one location ..", Toast.LENGTH_SHORT).show();
			return;
		}

		// SortedSet<String> keys = new TreeSet<String>(
		// favLocationHashMap.keySet());
		Set keys = favLocationHashMap.keySet();

		for (Iterator i = keys.iterator(); i.hasNext();) {
			String key = (String) i.next();

			Log.d(key + "::::", favLocationHashMap.get(key));

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
			mainIntent.putExtra("from", "reg");
			startActivity(mainIntent);
		}

	}

	public void onSkipButtonClick(View v) {

		if (notfromregistration) {
			finish();
		} else {
			Intent mainIntent = new Intent(FavoriteLocationsAcivity.this,
					FirstLoginWalletsActivity.class);
			mainIntent.putExtra("from", "reg");
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

		for (int i = 0; i < favoriteTag.size(); i++) {

			if (i == 0) {

				tvLocationTagTextView1.setText(favoriteTag.get(i));
				aTvLocationAutoComplete1.setText(favoriteAddress.get(i));

			} else if (i == 1) {

				tvLocationTagTextView2.setText(favoriteTag.get(i));
				aTvLocationAutoComplete2.setText(favoriteAddress.get(i));

			}

			else if (i == 2) {
				tvLocationTagTextView3.setText(favoriteTag.get(i));
				aTvLocationAutoComplete3.setText(favoriteAddress.get(i));
				ll3.setVisibility(View.VISIBLE);

			} else if (i == 3) {

				tvLocationTagTextView4.setText(favoriteTag.get(i));
				aTvLocationAutoComplete4.setText(favoriteAddress.get(i));
				ll4.setVisibility(View.VISIBLE);
			} else if (i == 4) {
				tvLocationTagTextView5.setText(favoriteTag.get(i));
				aTvLocationAutoComplete5.setText(favoriteAddress.get(i));
				ll5.setVisibility(View.VISIBLE);

			}

		}

	}

	public void deleteFavoriteLocaiton() {

		// favoriteAddress.clear();
		// favoriteTag.clear();
		// addressModelList.clear();

		remainigfavorites = 5 - favoriteTag.size();
		if (remainigfavorites == 0)
			addMoreButton.setVisibility(View.GONE);
		else
			addMoreButton.setText("Add More (" + remainigfavorites
					+ " remaining )");

		if (favoriteTag.size() > 0) {

			if (favoriteTag.size() == 2) {

				ll3.setVisibility(View.GONE);
				ll4.setVisibility(View.GONE);
				ll5.setVisibility(View.GONE);
			} else if (favoriteTag.size() == 3) {
				ll4.setVisibility(View.GONE);
				ll5.setVisibility(View.GONE);
			} else if (favoriteTag.size() == 4) {
				ll5.setVisibility(View.GONE);
			} else {

			}
			// SortedSet<String> keys = new TreeSet<String>(
			// favLocationHashMap.keySet());
			// // Set keys = favLocationHashMap.keySet();
			//
			// Gson gson = new Gson();
			//
			// for (Iterator i = keys.iterator(); i.hasNext();) {
			// String key = (String) i.next();
			//
			// if (key.equalsIgnoreCase(StringTags.TAG_WHERE_LIVE_KEY)){
			// favoriteTag.add(StringTags.TAG_WHERE_LIVE);
			// //tvLocationTagTextView1.setText(StringTags.TAG_WHERE_LIVE);
			// }
			// else if (key.equalsIgnoreCase(StringTags.TAG_WHERE_WORK_KEY)){
			// favoriteTag.add(StringTags.TAG_WHERE_WORK);
			// //tvLocationTagTextView2.setText(StringTags.TAG_WHERE_WORK);
			//
			// }
			// else
			// favoriteTag.add(key);
			//
			// Log.d("Key value::", key);
			// // Get long address from location
			//
			// try {
			// AddressModel addressModel = (AddressModel) gson.fromJson(
			// favLocationHashMap.get(key), AddressModel.class);
			// ;
			// addressModelList.add(addressModel);
			// favoriteAddress.add(addressModel.getLongname());
			// } catch (Exception e) {
			//
			// favoriteAddress.add("");
			// //addressModelList.add(null);
			// }
			//
			// }

			for (int i = 0; i < favoriteTag.size(); i++) {

				if (i == 0) {

					tvLocationTagTextView1.setText(favoriteTag.get(i));
					aTvLocationAutoComplete1.setText(favoriteAddress.get(i));

				} else if (i == 1) {

					tvLocationTagTextView2.setText(favoriteTag.get(i));
					aTvLocationAutoComplete2.setText(favoriteAddress.get(i));

				}

				else if (i == 2) {
					tvLocationTagTextView3.setText(favoriteTag.get(i));
					aTvLocationAutoComplete3.setText(favoriteAddress.get(i));

				} else if (i == 3) {

					tvLocationTagTextView4.setText(favoriteTag.get(i));
					aTvLocationAutoComplete4.setText(favoriteAddress.get(i));
				} else if (i == 4) {
					tvLocationTagTextView5.setText(favoriteTag.get(i));
					aTvLocationAutoComplete5.setText(favoriteAddress.get(i));

				}

			}

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
				String value = input.getText().toString().trim();

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

					for (int i = 0; i < favoriteTag.size(); i++) {

						if (value.equalsIgnoreCase(favoriteTag.get(i))) {
							Toast.makeText(getApplicationContext(),
									"This tag already exists",
									Toast.LENGTH_SHORT).show();
							InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(input.getWindowToken(),
									0);

							return;

						}
					}

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

	public void setAutoConpteletTextview2() {

		aTvLocationAutoComplete2.setAdapter(new PlacesAutoCompleteAdapter(
				context, R.layout.list_item));

		aTvLocationAutoComplete2.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				// pos=(Integer)v.getTag();

				return false;
			}
		});

		aTvLocationAutoComplete2
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int p, long id) {

						index = 1;

						String jnd = aTvLocationAutoComplete2.getText()
								.toString().trim();

						locationAddress = MapUtilityMethods.geocodeAddress(jnd,
								FavoriteLocationsAcivity.this);

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

							// Log.d("position::::::", "" + pos);
							addressModelList.set(index, addressModel);

							favoriteAddress.set(index, jnd);
							locationAddress = null; // reset previous
							InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							in.hideSoftInputFromWindow(aTvLocationAutoComplete2
									.getApplicationWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);

						} else {
							Toast.makeText(
									FavoriteLocationsAcivity.this,
									"Sorry, we could not find the location you entered, please try again",
									Toast.LENGTH_LONG).show();
						}

					}
				});
		aTvLocationAutoComplete2.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				// When user changed the Text

				String text = aTvLocationAutoComplete2.getText().toString()
						.trim();
				if (text.isEmpty() || text.equalsIgnoreCase("")) {
					ivClearedittextimg2.setVisibility(View.GONE);
				} else {
					ivClearedittextimg2.setVisibility(View.VISIBLE);
				}

				Log.d("from onTextChanged", "from onTextChanged");

				if (flagchk) {
					flagchk = false;
				} else {
					shortName = MapUtilityMethods.getaddressfromautoplace(
							FavoriteLocationsAcivity.this,
							aTvLocationAutoComplete2.getText().toString()
									.trim());
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		});

	}

	public void setAutoConpteletTextview3() {

		aTvLocationAutoComplete3.setAdapter(new PlacesAutoCompleteAdapter(
				context, R.layout.list_item));

		aTvLocationAutoComplete3.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				// pos=(Integer)v.getTag();

				return false;
			}
		});

		aTvLocationAutoComplete3
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int p, long id) {

						index = 2;

						String jnd = aTvLocationAutoComplete3.getText()
								.toString().trim();

						locationAddress = MapUtilityMethods.geocodeAddress(jnd,
								FavoriteLocationsAcivity.this);

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

							// Log.d("position::::::", "" + pos);
							addressModelList.set(index, addressModel);

							favoriteAddress.set(index, jnd);
							locationAddress = null; // reset previous
							InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							in.hideSoftInputFromWindow(aTvLocationAutoComplete3
									.getApplicationWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);

						} else {
							Toast.makeText(
									FavoriteLocationsAcivity.this,
									"Sorry, we could not find the location you entered, please try again",
									Toast.LENGTH_LONG).show();
						}

					}
				});
		aTvLocationAutoComplete3.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				// When user changed the Text

				String text = aTvLocationAutoComplete3.getText().toString()
						.trim();
				if (text.isEmpty() || text.equalsIgnoreCase("")) {
					ivClearedittextimg3.setVisibility(View.GONE);
				} else {
					ivClearedittextimg3.setVisibility(View.VISIBLE);
				}

				Log.d("from onTextChanged", "from onTextChanged");

				if (flagchk) {
					flagchk = false;
				} else {
					shortName = MapUtilityMethods.getaddressfromautoplace(
							FavoriteLocationsAcivity.this,
							aTvLocationAutoComplete3.getText().toString()
									.trim());
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		});

	}

	public void setAutoConpteletTextview4() {

		aTvLocationAutoComplete4.setAdapter(new PlacesAutoCompleteAdapter(
				context, R.layout.list_item));

		aTvLocationAutoComplete4.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				// pos=(Integer)v.getTag();

				return false;
			}
		});

		aTvLocationAutoComplete4
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int p, long id) {

						index = 3;

						String jnd = aTvLocationAutoComplete4.getText()
								.toString().trim();

						locationAddress = MapUtilityMethods.geocodeAddress(jnd,
								FavoriteLocationsAcivity.this);

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

							// Log.d("position::::::", "" + pos);
							addressModelList.set(index, addressModel);

							favoriteAddress.set(index, jnd);
							locationAddress = null; // reset previous
							InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							in.hideSoftInputFromWindow(aTvLocationAutoComplete4
									.getApplicationWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);

						} else {
							Toast.makeText(
									FavoriteLocationsAcivity.this,
									"Sorry, we could not find the location you entered, please try again",
									Toast.LENGTH_LONG).show();
						}

					}
				});
		aTvLocationAutoComplete4.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				// When user changed the Text

				String text = aTvLocationAutoComplete4.getText().toString()
						.trim();
				if (text.isEmpty() || text.equalsIgnoreCase("")) {
					ivClearedittextimg4.setVisibility(View.GONE);
				} else {
					ivClearedittextimg4.setVisibility(View.VISIBLE);
				}

				Log.d("from onTextChanged", "from onTextChanged");

				if (flagchk) {
					flagchk = false;
				} else {
					shortName = MapUtilityMethods.getaddressfromautoplace(
							FavoriteLocationsAcivity.this,
							aTvLocationAutoComplete4.getText().toString()
									.trim());
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		});

	}

	public void setAutoConpteletTextview5() {

		aTvLocationAutoComplete5.setAdapter(new PlacesAutoCompleteAdapter(
				context, R.layout.list_item));

		aTvLocationAutoComplete5.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				// pos=(Integer)v.getTag();

				return false;
			}
		});

		aTvLocationAutoComplete5
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int p, long id) {

						index = 4;

						String jnd = aTvLocationAutoComplete5.getText()
								.toString().trim();

						locationAddress = MapUtilityMethods.geocodeAddress(jnd,
								FavoriteLocationsAcivity.this);

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

							// Log.d("position::::::", "" + pos);
							addressModelList.set(index, addressModel);

							favoriteAddress.set(index, jnd);
							locationAddress = null; // reset previous
							InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							in.hideSoftInputFromWindow(aTvLocationAutoComplete5
									.getApplicationWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);

						} else {
							Toast.makeText(
									FavoriteLocationsAcivity.this,
									"Sorry, we could not find the location you entered, please try again",
									Toast.LENGTH_LONG).show();
						}

					}
				});
		aTvLocationAutoComplete5.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				// When user changed the Text

				String text = aTvLocationAutoComplete5.getText().toString()
						.trim();
				if (text.isEmpty() || text.equalsIgnoreCase("")) {
					ivClearedittextimg5.setVisibility(View.GONE);
				} else {
					ivClearedittextimg5.setVisibility(View.VISIBLE);
				}

				Log.d("from onTextChanged", "from onTextChanged");

				if (flagchk) {
					flagchk = false;
				} else {
					shortName = MapUtilityMethods.getaddressfromautoplace(
							FavoriteLocationsAcivity.this,
							aTvLocationAutoComplete5.getText().toString()
									.trim());
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		});

	}

	public void openMap1() {
		btnMap1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				index = 0;

				locationAddress = null; // reset previous
				InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				in.hideSoftInputFromWindow(
						aTvLocationAutoComplete1.getApplicationWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				// Toast.makeText(context,
				// "pos="+pos,Toast.LENGTH_LONG).show();

				if (aTvLocationAutoComplete1.getText().toString().trim()
						.isEmpty()
						|| aTvLocationAutoComplete1.getText().toString()
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

					String jnd = aTvLocationAutoComplete1.getText().toString()
							.trim();

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
	}

	public void openMap2() {

		btnMap2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				index = 1;
				locationAddress = null; // reset previous
				InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				in.hideSoftInputFromWindow(
						aTvLocationAutoComplete2.getApplicationWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				// Toast.makeText(context,
				// "pos="+pos,Toast.LENGTH_LONG).show();

				if (aTvLocationAutoComplete2.getText().toString().trim()
						.isEmpty()
						|| aTvLocationAutoComplete2.getText().toString()
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

					String jnd = aTvLocationAutoComplete2.getText().toString()
							.trim();

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
	}

	public void openMap3() {

		btnMap3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				index = 2;
				locationAddress = null; // reset previous
				InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				in.hideSoftInputFromWindow(
						aTvLocationAutoComplete3.getApplicationWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				// Toast.makeText(context,
				// "pos="+pos,Toast.LENGTH_LONG).show();

				if (aTvLocationAutoComplete3.getText().toString().trim()
						.isEmpty()
						|| aTvLocationAutoComplete3.getText().toString()
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

					String jnd = aTvLocationAutoComplete3.getText().toString()
							.trim();

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
	}

	public void openMap4() {

		btnMap4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				index = 3;
				locationAddress = null; // reset previous
				InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				in.hideSoftInputFromWindow(
						aTvLocationAutoComplete4.getApplicationWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				// Toast.makeText(context,
				// "pos="+pos,Toast.LENGTH_LONG).show();

				if (aTvLocationAutoComplete4.getText().toString().trim()
						.isEmpty()
						|| aTvLocationAutoComplete4.getText().toString()
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

					String jnd = aTvLocationAutoComplete4.getText().toString()
							.trim();

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
	}

	public void openMap5() {

		btnMap5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				index = 4;
				locationAddress = null; // reset previous
				InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				in.hideSoftInputFromWindow(
						aTvLocationAutoComplete5.getApplicationWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				// Toast.makeText(context,
				// "pos="+pos,Toast.LENGTH_LONG).show();

				if (aTvLocationAutoComplete5.getText().toString().trim()
						.isEmpty()
						|| aTvLocationAutoComplete5.getText().toString()
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

					String jnd = aTvLocationAutoComplete5.getText().toString()
							.trim();

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
	}

}
