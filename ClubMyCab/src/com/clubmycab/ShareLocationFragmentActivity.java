package com.clubmycab;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.maps.MapUtilityMethods;
import com.clubmycab.ui.HomeActivity;
import com.clubmycab.ui.NotificationListActivity;
import com.clubmycab.ui.UniversalDrawer;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.navdrawer.SimpleSideDrawer;

public class ShareLocationFragmentActivity extends FragmentActivity implements
		LocationListener {

	CircularImageView profilepic;
	TextView username;
	ImageView notificationimg;

	ImageView sidemenu;
	ImageView poolinfoimg;

	private SimpleSideDrawer mNav;
	CircularImageView drawerprofilepic;
	TextView drawerusername;

	TextView myprofile;
	TextView myrides;
	TextView bookacab;
	TextView sharemylocation;
	TextView myclubs;
	TextView sharethisapp;
	TextView mypreferences;
	TextView about;

	String FullName;
	String MobileNumber;

	LocationManager locationManager;

	RelativeLayout selectrecprll;
	RelativeLayout watchmeforrll;
	// RelativeLayout sharetodestinationll;

	TextView selectrecipientsvalue;
	TextView watchmeforvalue;

	AutoCompleteTextView from_places;
	Button threedotsfrom;

	Button shartsharing;
	Button stopsharing;

	Boolean chk;

	RelativeLayout unreadnoticountrl;
	TextView unreadnoticount;

	// /////////////////
	Button contactsbtn;
	Button appFrends;
	Button myclubbtn;

	private ArrayList<String> ClubName = new ArrayList<String>();
	private ArrayList<String> NewClub = new ArrayList<String>();
	private ArrayList<String> ClubMember = new ArrayList<String>();

	private ArrayList<String> MembersNumber = new ArrayList<String>();

	ArrayList<String> namearray = new ArrayList<String>();
	ArrayList<String> phonenoarray = new ArrayList<String>();
	ArrayList<String> imagearray = new ArrayList<String>();

	ArrayList<String> namearraynew = new ArrayList<String>();
	ArrayList<String> phonenoarraynew = new ArrayList<String>();
	ArrayList<String> imagearraynew = new ArrayList<String>();

	ArrayList<String> AppUsersfullnamearr = new ArrayList<String>();
	ArrayList<String> AppUsersmobilenumberarr = new ArrayList<String>();
	ArrayList<String> AppUsersimagearr = new ArrayList<String>();

	EditText searchfromlist;
	ContactsAdapter objAdapter;

	LinearLayout clubcontactslistll;
	ListView contactslist;

	LinearLayout mainclublistll;
	ListView listMyclubs;
	ListView listMembersclubs;

	int flag = 1;
	Button donebtn;

	String appusers;

	ArrayList<String> selectednames = new ArrayList<String>();
	ArrayList<String> selectednumbers = new ArrayList<String>();

	Boolean clubcreated;
	Boolean appusersavailable;

	String readunreadnotiresp;
	Bitmap mIcon11;
	String fetchclubresp;
	String fetchappusersresp;

	String sharechk;

	RelativeLayout fromrelative;
	TextView fromlocation;
	Button fromdone;
	Button cancel;

	GoogleMap frommap;
	String imagenameresp;

	RelativeLayout sharelocationrl;

	LinearLayout homeofficellvalues;
	ImageView homeimg;
	ImageView officeimg;

	JSONArray saveasjsonarray;
	ArrayList<String> type = new ArrayList<String>();
	ArrayList<String> Latitude = new ArrayList<String>();
	ArrayList<String> Longitude = new ArrayList<String>();
	ArrayList<String> Address = new ArrayList<String>();
	ArrayList<String> Locality = new ArrayList<String>();
	ArrayList<String> ShortAddress = new ArrayList<String>();

	Address fAddress;
	Tracker tracker;

	ImageView clearedittextimg;
	boolean exceptioncheck = false;

	Location mycurrentlocationobject;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_location);

		// Check if Internet present
		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					ShareLocationFragmentActivity.this);
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

		GoogleAnalytics analytics = GoogleAnalytics
				.getInstance(ShareLocationFragmentActivity.this);
		tracker = analytics.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);

		// All subsequent hits will be send with screen name = "main screen"
		tracker.setScreenName("Share Location");

		sharelocationrl = (RelativeLayout) findViewById(R.id.sharelocationrl);
		sharelocationrl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Log.d("sharelocationrl", "sharelocationrl");
			}
		});

		// mNav = new SimpleSideDrawer(this);
		// mNav.setLeftBehindContentView(R.layout.activity_behind_left_simple);
		//
		// findViewById(R.id.sidemenu).setOnClickListener(new OnClickListener()
		// {
		// @Override
		// public void onClick(View v) {
		//
		// // mainhomepagerl.setAlpha((float) 0.3);
		// mNav.toggleLeftDrawer();
		//
		// }
		// });
		//
		// myprofile = (TextView) findViewById(R.id.myprofile);
		// myprofile.setTypeface(Typeface.createFromAsset(getAssets(),
		// "NeutraText-Light.ttf"));
		// myrides = (TextView) findViewById(R.id.myrides);
		// myrides.setTypeface(Typeface.createFromAsset(getAssets(),
		// "NeutraText-Light.ttf"));
		// bookacab = (TextView) findViewById(R.id.bookacab);
		// bookacab.setTypeface(Typeface.createFromAsset(getAssets(),
		// "NeutraText-Light.ttf"));
		// sharemylocation = (TextView) findViewById(R.id.sharemylocation);
		// sharemylocation.setTypeface(Typeface.createFromAsset(getAssets(),
		// "NeutraText-Light.ttf"));
		// myclubs = (TextView) findViewById(R.id.myclubs);
		// myclubs.setTypeface(Typeface.createFromAsset(getAssets(),
		// "NeutraText-Light.ttf"));
		// sharethisapp = (TextView) findViewById(R.id.sharethisapp);
		// sharethisapp.setTypeface(Typeface.createFromAsset(getAssets(),
		// "NeutraText-Light.ttf"));
		// mypreferences = (TextView) findViewById(R.id.mypreferences);
		// mypreferences.setTypeface(Typeface.createFromAsset(getAssets(),
		// "NeutraText-Light.ttf"));
		// about = (TextView) findViewById(R.id.about);
		// about.setTypeface(Typeface.createFromAsset(getAssets(),
		// "NeutraText-Light.ttf"));
		//
		// myprofile.setOnClickListener(new View.OnClickListener() {
		// @SuppressWarnings("deprecation")
		// @Override
		// public void onClick(View arg0) {
		// mNav.toggleDrawer();
		//
		// tracker.send(new HitBuilders.EventBuilder()
		// .setCategory("MyProfile Click")
		// .setAction("MyProfile Click")
		// .setLabel("MyProfile Click").build());
		//
		// Intent mainIntent = new Intent(ShareLocationFragmentActivity.this,
		// MyProfileActivity.class);
		// startActivityForResult(mainIntent, 500);
		// overridePendingTransition(R.anim.slide_in_right,
		// R.anim.slide_out_left);
		// }
		// });
		//
		// myrides.setOnClickListener(new View.OnClickListener() {
		// @SuppressWarnings("deprecation")
		// @Override
		// public void onClick(View arg0) {
		// mNav.toggleDrawer();
		//
		// tracker.send(new HitBuilders.EventBuilder()
		// .setCategory("MyRides Click")
		// .setAction("MyRides Click").setLabel("MyRides Click")
		// .build());
		//
		// Intent mainIntent = new Intent(ShareLocationFragmentActivity.this,
		// MyRidesActivity.class);
		// startActivityForResult(mainIntent, 500);
		// overridePendingTransition(R.anim.slide_in_right,
		// R.anim.slide_out_left);
		// }
		// });
		//
		// bookacab.setOnClickListener(new View.OnClickListener() {
		// @SuppressWarnings("deprecation")
		// @Override
		// public void onClick(View arg0) {
		// mNav.toggleDrawer();
		//
		// tracker.send(new HitBuilders.EventBuilder()
		// .setCategory("BookaCab Click")
		// .setAction("BookaCab Click").setLabel("BookaCab Click")
		// .build());
		//
		// Intent mainIntent = new Intent(ShareLocationFragmentActivity.this,
		// BookaCabFragmentActivity.class);
		// startActivityForResult(mainIntent, 500);
		// overridePendingTransition(R.anim.slide_in_right,
		// R.anim.slide_out_left);
		// }
		// });
		//
		// sharemylocation.setOnClickListener(new View.OnClickListener() {
		// @SuppressWarnings("deprecation")
		// @Override
		// public void onClick(View arg0) {
		// mNav.toggleDrawer();
		// }
		// });
		//
		// myclubs.setOnClickListener(new View.OnClickListener() {
		// @SuppressWarnings("deprecation")
		// @Override
		// public void onClick(View arg0) {
		// mNav.toggleDrawer();
		//
		// tracker.send(new HitBuilders.EventBuilder()
		// .setCategory("MyClubs Click")
		// .setAction("MyClubs Click").setLabel("MyClubs Click")
		// .build());
		//
		// Intent mainIntent = new Intent(ShareLocationFragmentActivity.this,
		// MyClubsActivity.class);
		// startActivityForResult(mainIntent, 500);
		// overridePendingTransition(R.anim.slide_in_right,
		// R.anim.slide_out_left);
		// }
		// });
		//
		// sharethisapp.setOnClickListener(new View.OnClickListener() {
		// @SuppressWarnings("deprecation")
		// @Override
		// public void onClick(View arg0) {
		// mNav.toggleDrawer();
		//
		// tracker.send(new HitBuilders.EventBuilder()
		// .setCategory("ShareApp Click")
		// .setAction("ShareApp Click").setLabel("ShareApp Click")
		// .build());
		//
		// Intent sendIntent = new Intent();
		// sendIntent.setAction(Intent.ACTION_SEND);
		// sendIntent
		// .putExtra(
		// Intent.EXTRA_TEXT,
		// "I am using this cool app 'ClubMyCab' to share & book cabs. Check it out @ http://tinyurl.com/n7j6chq");
		// sendIntent.setType("text/plain");
		// startActivity(Intent.createChooser(sendIntent, "Share Via"));
		//
		// }
		// });
		//
		// mypreferences.setOnClickListener(new View.OnClickListener() {
		// @SuppressWarnings("deprecation")
		// @Override
		// public void onClick(View arg0) {
		// mNav.toggleDrawer();
		//
		// tracker.send(new HitBuilders.EventBuilder()
		// .setCategory("Settings Click")
		// .setAction("Settings Click").setLabel("Settings Click")
		// .build());
		//
		// Intent mainIntent = new Intent(ShareLocationFragmentActivity.this,
		// SettingActivity.class);
		// startActivityForResult(mainIntent, 500);
		// overridePendingTransition(R.anim.slide_in_right,
		// R.anim.slide_out_left);
		// }
		// });
		//
		// about.setOnClickListener(new View.OnClickListener() {
		// @SuppressWarnings("deprecation")
		// @Override
		// public void onClick(View arg0) {
		// mNav.toggleDrawer();
		//
		// tracker.send(new HitBuilders.EventBuilder()
		// .setCategory("About Click").setAction("About Click")
		// .setLabel("About Click").build());
		//
		// Intent mainIntent = new Intent(ShareLocationFragmentActivity.this,
		// AboutPagerFragmentActivity.class);
		// startActivityForResult(mainIntent, 500);
		// overridePendingTransition(R.anim.slide_in_right,
		// R.anim.slide_out_left);
		// }
		// });

		UniversalDrawer drawer = new UniversalDrawer(this, tracker);
		drawer.createDrawer();

		profilepic = (CircularImageView) findViewById(R.id.profilepic);
		notificationimg = (ImageView) findViewById(R.id.notificationimg);
		poolinfoimg = (ImageView) findViewById(R.id.poolinfoimg);
		drawerprofilepic = (CircularImageView) findViewById(R.id.drawerprofilepic);

		SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
		FullName = mPrefs.getString("FullName", "");
		MobileNumber = mPrefs.getString("MobileNumber", "");

		username = (TextView) findViewById(R.id.username);
		username.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		username.setText(FullName);

		drawerusername = (TextView) findViewById(R.id.drawerusername);
		drawerusername.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		drawerusername.setText(FullName);

		notificationimg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent mainIntent = new Intent(
						ShareLocationFragmentActivity.this,
						NotificationListActivity.class);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);

			}
		});

		fromrelative = (RelativeLayout) findViewById(R.id.fromrelative);

		fromlocation = (TextView) findViewById(R.id.fromlocation);
		fromdone = (Button) findViewById(R.id.fromdone);

		fromlocation.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		fromdone.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

		cancel = (Button) findViewById(R.id.cancel);
		cancel.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

		unreadnoticountrl = (RelativeLayout) findViewById(R.id.unreadnoticountrl);
		unreadnoticount = (TextView) findViewById(R.id.unreadnoticount);
		
		if (GlobalVariables.UnreadNotificationCount.equalsIgnoreCase("0")) {

			unreadnoticountrl.setVisibility(View.GONE);

		} else {

			unreadnoticountrl.setVisibility(View.VISIBLE);
			unreadnoticount.setText(GlobalVariables.UnreadNotificationCount);
		}

		selectrecprll = (RelativeLayout) findViewById(R.id.selectrecprll);
		selectrecprll.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				showAlertDialog();
			}
		});

		watchmeforrll = (RelativeLayout) findViewById(R.id.watchmeforrll);
		watchmeforrll.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				AlertDialog dialog;
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ShareLocationFragmentActivity.this);
				builder.setTitle(null);

				final CharSequence str[] = { "10 minutes", "20 minutes",
						"30 minutes", "40 minutes", "50 minutes", "60 minutes" };

				builder.setItems(str, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int position) {
						// TODO Auto-generated method stub
						watchmeforvalue.setText(str[position]);
						from_places.setText("");
						from_places.setHint("Select Destination");
					}
				});

				dialog = builder.create();
				dialog.show();

			}
		});

		selectrecipientsvalue = (TextView) findViewById(R.id.selectrecipientsvalue);
		watchmeforvalue = (TextView) findViewById(R.id.watchmeforvalue);
		from_places = (AutoCompleteTextView) findViewById(R.id.from_places);
		clearedittextimg = (ImageView) findViewById(R.id.clearedittextimg);
		clearedittextimg.setVisibility(View.GONE);

		threedotsfrom = (Button) findViewById(R.id.threedotsfrom);

		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.list_item);
		adapter.setNotifyOnChange(true);

		from_places.setAdapter(new PlacesAutoCompleteAdapter(this,
				R.layout.list_item));

		from_places
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (event != null
								&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
							InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							in.hideSoftInputFromWindow(
									from_places.getApplicationWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
						}

						return false;
					}
				});

		from_places
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						fAddress = null; // reset previous
						InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						in.hideSoftInputFromWindow(
								from_places.getApplicationWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);

						fAddress = geocodeAddress(from_places.getText()
								.toString());
					}
				});

		from_places.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View view, MotionEvent motionEvent) {

				watchmeforvalue.setText("Select Time");
				return false;

			}
		});

		from_places.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

				String text = from_places.getText().toString().trim();
				if (text.isEmpty() || text.equalsIgnoreCase("")) {
					clearedittextimg.setVisibility(View.GONE);
				} else {
					clearedittextimg.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		clearedittextimg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				from_places.setText("");
			}
		});

		threedotsfrom.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub

				if (from_places.getText().toString().trim().isEmpty()
						|| from_places.getText().toString()
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
						frommap.moveCamera(CameraUpdateFactory
								.newLatLng(currentlatLng));

						// Zoom in the Google Map
						frommap.animateCamera(CameraUpdateFactory.zoomTo(15));

						String address = MapUtilityMethods.getAddress(
								ShareLocationFragmentActivity.this, latitude,
								longitude);

						fromlocation.setText(address);
						fromrelative.setVisibility(View.VISIBLE);

					} else {

						// no network provider is enabled
						AlertDialog.Builder dialog = new AlertDialog.Builder(
								ShareLocationFragmentActivity.this);
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

					String jnd = from_places.getText().toString().trim();

					Geocoder coder = new Geocoder(
							ShareLocationFragmentActivity.this);
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
						frommap.moveCamera(CameraUpdateFactory
								.newLatLng(currentlatLng));

						// Zoom in the Google Map
						frommap.animateCamera(CameraUpdateFactory.zoomTo(15));

						fromlocation.setText(jnd);

					} catch (IOException e) {
						e.printStackTrace();
					}

					fromrelative.setVisibility(View.VISIBLE);

				}
			}
		});

		homeofficellvalues = (LinearLayout) findViewById(R.id.homeofficellvalues);
		homeimg = (ImageView) findViewById(R.id.homeimg);
		officeimg = (ImageView) findViewById(R.id.officeimg);

		FavoritesLocationReadWrite favoritesLocationReadWrite = new FavoritesLocationReadWrite(
				ShareLocationFragmentActivity.this);
		JSONArray saveasjsonarray = null;
		try {
			saveasjsonarray = favoritesLocationReadWrite.readFromFile();
			if (saveasjsonarray.length() > 0) {
				Log.d("saveasjsonarray", "" + saveasjsonarray);
				type.clear();
				Latitude.clear();
				Longitude.clear();
				Address.clear();
				Locality.clear();
				ShortAddress.clear();
				for (int i = 0; i < saveasjsonarray.length(); i++) {

					try {
						type.add(saveasjsonarray.getJSONObject(i)
								.getString("type").toString().trim());
						Latitude.add(saveasjsonarray.getJSONObject(i)
								.getString("Latitude").toString().trim());
						Longitude.add(saveasjsonarray.getJSONObject(i)
								.getString("Longitude").toString().trim());
						Address.add(saveasjsonarray.getJSONObject(i)
								.getString("Address").toString().trim());
						Locality.add(saveasjsonarray.getJSONObject(i)
								.getString("Locality").toString().trim());
						ShortAddress.add(saveasjsonarray.getJSONObject(i)
								.getString("shortaddress").toString().trim());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				Log.d("saveasjsonarray", "null");
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (saveasjsonarray.length() > 0) {
			if (type.contains("Home") && type.contains("Office")) {
				homeofficellvalues.setVisibility(View.VISIBLE);
				homeimg.setVisibility(View.VISIBLE);
				officeimg.setVisibility(View.VISIBLE);
			} else if (type.contains("Home") && !type.contains("Office")) {
				homeofficellvalues.setVisibility(View.VISIBLE);
				homeimg.setVisibility(View.VISIBLE);
				officeimg.setVisibility(View.GONE);
			} else if (!type.contains("Home") && type.contains("Office")) {
				homeofficellvalues.setVisibility(View.VISIBLE);
				homeimg.setVisibility(View.GONE);
				officeimg.setVisibility(View.VISIBLE);
			}
		} else {

		}

		homeimg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				for (int i = 0; i < type.size(); i++) {

					if (type.get(i).toString().trim().equalsIgnoreCase("Home")) {

						from_places.setText(Address.get(i).toString().trim());
						fAddress = new Address(null);
						fAddress.setLatitude(Double.parseDouble(Latitude.get(i)
								.toString().trim()));
						fAddress.setLongitude(Double.parseDouble(Longitude
								.get(i).toString().trim()));
						fAddress.setLocality(Locality.get(i).toString().trim());
					}
				}
			}
		});

		officeimg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				for (int i = 0; i < type.size(); i++) {

					if (type.get(i).toString().trim()
							.equalsIgnoreCase("Office")) {

						from_places.setText(Address.get(i).toString().trim());
						fAddress = new Address(null);
						fAddress.setLatitude(Double.parseDouble(Latitude.get(i)
								.toString().trim()));
						fAddress.setLongitude(Double.parseDouble(Longitude
								.get(i).toString().trim()));
						fAddress.setLocality(Locality.get(i).toString().trim());
					}
				}
			}
		});

		shartsharing = (Button) findViewById(R.id.shartsharing);
		stopsharing = (Button) findViewById(R.id.stopsharing);

		SharedPreferences locationshapref = getSharedPreferences(
				"ShareLocationShared", 0);
		Gson gson = new Gson();
		String json = locationshapref.getString("ShareLocationObject", "");
		ShareLocationObject obj = gson
				.fromJson(json, ShareLocationObject.class);

		if (isMyServiceRunning(LocationShareService.class)) {
			Log.d("service", "service running");

			stopService(new Intent(this, LocationShareService.class));

			setnamesandnumbersintext(obj.recipientsnames, obj.recipientsnumbers);

			if (obj.sharetilltype.toString().trim()
					.equalsIgnoreCase("duration")) {
				long sharemins = (obj.timetillvalue - System
						.currentTimeMillis()) / 60000;

				watchmeforvalue.setText("" + sharemins + " minutes");
				from_places.setText("");
				from_places.setHint("Select Destination");
			} else {
				from_places.setText(obj.destinationlongname);
				watchmeforvalue.setText("Select Time");
			}

			shartsharing.setVisibility(View.GONE);
			stopsharing.setVisibility(View.VISIBLE);
			poolinfoimg.setVisibility(View.VISIBLE);

			selectrecprll.setEnabled(false);
			watchmeforrll.setEnabled(false);
			from_places.setEnabled(false);
			threedotsfrom.setEnabled(false);

			startService(new Intent(this, LocationShareService.class));

		} else {
			Log.d("service", "service not running");

			shartsharing.setVisibility(View.VISIBLE);
			stopsharing.setVisibility(View.GONE);

			poolinfoimg.setVisibility(View.GONE);

			selectednames.clear();
			selectednumbers.clear();
			from_places.setText("");
			from_places.setHint("Select Destination");
			watchmeforvalue.setText("Select Time");
			selectrecipientsvalue.setText("Select Recipients");

			selectrecprll.setEnabled(true);
			watchmeforrll.setEnabled(true);
			from_places.setEnabled(true);
			threedotsfrom.setEnabled(true);
		}

		shartsharing.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Animation animScale = AnimationUtils.loadAnimation(
						ShareLocationFragmentActivity.this,
						R.anim.button_click_anim);
				shartsharing.startAnimation(animScale);

				Handler mHandler2 = new Handler();
				Runnable mRunnable2 = new Runnable() {
					@Override
					public void run() {

						if (selectednumbers.size() > 0) {

							Location location = getLocation();

							if (location != null) {

								if (watchmeforvalue.getText().toString()
										.equalsIgnoreCase("Select Time")
										&& from_places
												.getHint()
												.toString()
												.equalsIgnoreCase(
														"Select Destination")
										&& from_places.getText().toString()
												.equalsIgnoreCase("")) {

									Toast.makeText(
											getBaseContext(),
											"Please Select Time Or Destination",
											Toast.LENGTH_SHORT).show();

								} else {
									if (from_places
											.getHint()
											.toString()
											.equalsIgnoreCase(
													"Select Destination")
											&& !watchmeforvalue
													.getText()
													.toString()
													.equalsIgnoreCase(
															"Select Time")) {

										SharedPreferences mPrefs1111 = getSharedPreferences(
												"sharelocation", 0);
										String chk = mPrefs1111.getString(
												"sharechk", "");

										if (chk.isEmpty() || chk == null
												|| chk.equalsIgnoreCase("")) {

											AlertDialog.Builder builder = new AlertDialog.Builder(
													ShareLocationFragmentActivity.this);
											builder.setMessage("You need to keep location services and data plan running for this to work. Your location may not be shared while you are on a call");
											builder.setCancelable(true);
											builder.setPositiveButton(
													"Ok",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int id) {

															sharechk = "Understood";

															SharedPreferences sharedPreferences1 = getSharedPreferences(
																	"sharelocation",
																	0);
															SharedPreferences.Editor editor1 = sharedPreferences1
																	.edit();
															editor1.putString(
																	"sharechk",
																	sharechk);
															editor1.commit();

															sharemylocationmethod("duration");
														}
													});
											builder.setNegativeButton(
													"Remind me again",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int id) {

															sharechk = "Remind me again";

															SharedPreferences sharedPreferences1 = getSharedPreferences(
																	"sharelocation",
																	0);
															SharedPreferences.Editor editor1 = sharedPreferences1
																	.edit();
															editor1.putString(
																	"sharechk",
																	sharechk);
															editor1.commit();

															sharemylocationmethod("duration");
														}
													});
											AlertDialog dialogseats = builder
													.show();
											TextView messageText = (TextView) dialogseats
													.findViewById(android.R.id.message);
											messageText
													.setGravity(Gravity.CENTER);
											dialogseats.show();

										}

										else if (chk
												.equalsIgnoreCase("Remind me again")) {

											AlertDialog.Builder builder = new AlertDialog.Builder(
													ShareLocationFragmentActivity.this);
											builder.setMessage("You need to keep location services and data plan running for this to work. Your location may not be shared while you are on a call");
											builder.setCancelable(true);
											builder.setPositiveButton(
													"Understood",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int id) {

															sharechk = "Understood";

															SharedPreferences sharedPreferences1 = getSharedPreferences(
																	"sharelocation",
																	0);
															SharedPreferences.Editor editor1 = sharedPreferences1
																	.edit();
															editor1.putString(
																	"sharechk",
																	sharechk);
															editor1.commit();

															sharemylocationmethod("duration");
														}
													});
											builder.setNegativeButton(
													"Remind me again",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int id) {

															sharechk = "Remind me again";

															SharedPreferences sharedPreferences1 = getSharedPreferences(
																	"sharelocation",
																	0);
															SharedPreferences.Editor editor1 = sharedPreferences1
																	.edit();
															editor1.putString(
																	"sharechk",
																	sharechk);
															editor1.commit();

															sharemylocationmethod("duration");
														}
													});
											AlertDialog dialogseats = builder
													.show();
											TextView messageText = (TextView) dialogseats
													.findViewById(android.R.id.message);
											messageText
													.setGravity(Gravity.CENTER);
											dialogseats.show();

										}

										else {

											sharemylocationmethod("duration");
										}

									}

									// //////////////////////
									else {

										SharedPreferences mPrefs1111 = getSharedPreferences(
												"sharelocation", 0);
										String chk = mPrefs1111.getString(
												"sharechk", "");

										if (chk.isEmpty() || chk == null
												|| chk.equalsIgnoreCase("")) {

											AlertDialog.Builder builder = new AlertDialog.Builder(
													ShareLocationFragmentActivity.this);
											builder.setMessage("You need to keep location services and data plan running for this to work. Your location may not be shared while you are on a call");
											builder.setCancelable(true);
											builder.setPositiveButton(
													"Understood",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int id) {

															sharechk = "Understood";

															SharedPreferences sharedPreferences1 = getSharedPreferences(
																	"sharelocation",
																	0);
															SharedPreferences.Editor editor1 = sharedPreferences1
																	.edit();
															editor1.putString(
																	"sharechk",
																	sharechk);
															editor1.commit();

															sharemylocationmethod("destination");
														}
													});
											builder.setNegativeButton(
													"Remind me again",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int id) {

															sharechk = "Remind me again";

															SharedPreferences sharedPreferences1 = getSharedPreferences(
																	"sharelocation",
																	0);
															SharedPreferences.Editor editor1 = sharedPreferences1
																	.edit();
															editor1.putString(
																	"sharechk",
																	sharechk);
															editor1.commit();

															sharemylocationmethod("destination");
														}
													});
											AlertDialog dialogseats = builder
													.show();
											TextView messageText = (TextView) dialogseats
													.findViewById(android.R.id.message);
											messageText
													.setGravity(Gravity.CENTER);
											dialogseats.show();

										}

										else if (chk
												.equalsIgnoreCase("Remind me again")) {

											AlertDialog.Builder builder = new AlertDialog.Builder(
													ShareLocationFragmentActivity.this);
											builder.setMessage("You need to keep location services and data plan running for this to work. Your location may not be shared while you are on a call");
											builder.setCancelable(true);
											builder.setPositiveButton(
													"Understood",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int id) {

															sharechk = "Understood";

															SharedPreferences sharedPreferences1 = getSharedPreferences(
																	"sharelocation",
																	0);
															SharedPreferences.Editor editor1 = sharedPreferences1
																	.edit();
															editor1.putString(
																	"sharechk",
																	sharechk);
															editor1.commit();

															sharemylocationmethod("destination");
														}
													});
											builder.setNegativeButton(
													"Remind me again",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int id) {

															sharechk = "Remind me again";

															SharedPreferences sharedPreferences1 = getSharedPreferences(
																	"sharelocation",
																	0);
															SharedPreferences.Editor editor1 = sharedPreferences1
																	.edit();
															editor1.putString(
																	"sharechk",
																	sharechk);
															editor1.commit();

															sharemylocationmethod("destination");
														}
													});
											AlertDialog dialogseats = builder
													.show();
											TextView messageText = (TextView) dialogseats
													.findViewById(android.R.id.message);
											messageText
													.setGravity(Gravity.CENTER);
											dialogseats.show();

										}

										else {

											sharemylocationmethod("destination");
										}

									}
								}

							} else {

							}

						} else {
							Toast.makeText(getBaseContext(),
									"Please select recipients",
									Toast.LENGTH_SHORT).show();
						}

					}
				};
				mHandler2.postDelayed(mRunnable2, 1000);

			}
		});

		stopsharing.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (isMyServiceRunning(LocationShareService.class)) {
					stopService(new Intent(ShareLocationFragmentActivity.this,
							LocationShareService.class));
				}

				tracker.send(new HitBuilders.EventBuilder()
						.setCategory("Stop Location Sharing")
						.setAction("Stop Location Sharing")
						.setLabel("Stop Location Sharing").build());

				shartsharing.setVisibility(View.VISIBLE);
				stopsharing.setVisibility(View.GONE);

				poolinfoimg.setVisibility(View.GONE);

				selectednames.clear();
				selectednumbers.clear();
				from_places.setText("");
				from_places.setHint("Select Destination");
				watchmeforvalue.setText("Select Time");
				selectrecipientsvalue.setText("Select Recipients");

				selectrecprll.setEnabled(true);
				watchmeforrll.setEnabled(true);
				from_places.setEnabled(true);
				threedotsfrom.setEnabled(true);

			}
		});

		frommap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.frommap)).getMap();

		frommap.setMyLocationEnabled(true);

		frommap.setOnCameraChangeListener(new OnCameraChangeListener() {

			@Override
			public void onCameraChange(CameraPosition cameraPosition) {

				LatLng mapcenter = cameraPosition.target;

				String address = MapUtilityMethods.getAddress(ShareLocationFragmentActivity.this,
						mapcenter.latitude, mapcenter.longitude);
				Log.d("address", "" + address);

				fromlocation.setText(address);

			}
		});

		fromdone.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				fromrelative.setVisibility(View.GONE);
				String fromlocationname = fromlocation.getText().toString()
						.trim();
				from_places.setText(fromlocationname);
				fAddress = geocodeAddress(fromlocationname);
				watchmeforvalue.setText("Select Time");
			}
		});

		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				fromrelative.setVisibility(View.GONE);
			}
		});

		// ///
		// /// For contacts list

		Cursor cursor = null;
		try {
			cursor = ShareLocationFragmentActivity.this.getContentResolver()
					.query(Phone.CONTENT_URI, null, null, null, null);
			int nameIdx = cursor.getColumnIndex(Phone.DISPLAY_NAME);
			int phoneNumberIdx = cursor.getColumnIndex(Phone.NUMBER);
			int imageIdx = cursor.getColumnIndex(Phone.CONTACT_ID);

			cursor.moveToFirst();
			do {

				if (cursor.getString(phoneNumberIdx).length() > 10) {
					namearray.add(cursor.getString(nameIdx));
					String phonenumbercapt = cursor.getString(phoneNumberIdx);

					String phoneStr = phonenumbercapt.replaceAll("\\D+", "");

					String phonesub = null;
					if (phoneStr.length() > 10) {
						phonesub = phoneStr.substring(phoneStr.length() - 10,
								phoneStr.length());
					} else {
						phonesub = phoneStr;
					}

					phonenoarray.add(phonesub);

					imagearray.add(cursor.getString(imageIdx));
				}

			} while (cursor.moveToNext());

			Log.d("name", "" + namearray);
			Log.d("phoneNumber", "" + phonenoarray);
			Log.d("imagearray", "" + imagearray);

			Log.d("name count", "" + namearray.size());
			Log.d("phoneNumber count", "" + phonenoarray.size());
			Log.d("imagearray count", "" + imagearray.size());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		HashSet set = new HashSet();
		for (int i = 0; i < phonenoarray.size(); i++) {
			boolean val = set.add(phonenoarray.get(i));
			if (val == false) {

			} else {
				namearraynew.add(namearray.get(i));
				phonenoarraynew.add(phonenoarray.get(i));
				imagearraynew.add(imagearray.get(i));
			}
		}

		// ///////////////
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//			new ConnectionTaskForreadunreadnotification()
//					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//		} else {
//			new ConnectionTaskForreadunreadnotification().execute();
//		}

		// ///////////////
		SharedPreferences mPrefs111 = getSharedPreferences("userimage", 0);
		String imgname = mPrefs111.getString("imgname", "");
		String imagestr = mPrefs111.getString("imagestr", "");

		if (imagestr.isEmpty() || imagestr == null
				|| imagestr.equalsIgnoreCase("")) {

			// new ConnectionTaskForfetchimagename().execute();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForfetchimagename()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new ConnectionTaskForfetchimagename().execute();
			}

		} else {

			byte[] b = Base64.decode(imagestr, Base64.DEFAULT);
			InputStream is = new ByteArrayInputStream(b);
			Bitmap yourSelectedImage = BitmapFactory.decodeStream(is);

			profilepic.setImageBitmap(yourSelectedImage);
			drawerprofilepic.setImageBitmap(yourSelectedImage);
		}

		// ///////////
		SharedPreferences mPrefs11111 = getSharedPreferences("MyClubs", 0);
		String clubs = mPrefs11111.getString("clubs", "");

		if (clubs.isEmpty() || clubs == null || clubs.equalsIgnoreCase("")) {

		} else {
			// //////////////
			if (clubs.equalsIgnoreCase("No Users of your Club")) {

				clubcreated = false;

			} else {

				clubcreated = true;
			}
		}
	}

	// ///////
//	private class ConnectionTaskForreadunreadnotification extends
//			AsyncTask<String, Void, Void> {
//
//		@Override
//		protected void onPreExecute() {
//
//		}
//
//		@Override
//		protected Void doInBackground(String... args) {
//			AuthenticateConnectionreadunreadnotification mAuth1 = new AuthenticateConnectionreadunreadnotification();
//			try {
//				mAuth1.connection();
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				exceptioncheck = true;
//				e.printStackTrace();
//			}
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void v) {
//
//			if (exceptioncheck) {
//				exceptioncheck = false;
//				Toast.makeText(ShareLocationFragmentActivity.this,
//						getResources().getString(R.string.exceptionstring),
//						Toast.LENGTH_LONG).show();
//				return;
//			}
//
//			if (readunreadnotiresp.equalsIgnoreCase("0")) {
//
//				unreadnoticountrl.setVisibility(View.GONE);
//
//			} else {
//
//				unreadnoticountrl.setVisibility(View.VISIBLE);
//				unreadnoticount.setText(readunreadnotiresp);
//			}
//		}
//
//	}
//
//	public class AuthenticateConnectionreadunreadnotification {
//
//		public AuthenticateConnectionreadunreadnotification() {
//
//		}
//
//		public void connection() throws Exception {
//
//			// Connect to google.com
//			HttpClient httpClient = new DefaultHttpClient();
//			String url_select = GlobalVariables.ServiceUrl
//					+ "/FetchUnreadNotificationCount.php";
//
//			HttpPost httpPost = new HttpPost(url_select);
//			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
//					"MobileNumber", MobileNumber);
//
//			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
//			nameValuePairList.add(MobileNumberBasicNameValuePair);
//
//			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
//					nameValuePairList);
//			httpPost.setEntity(urlEncodedFormEntity);
//			HttpResponse httpResponse = httpClient.execute(httpPost);
//
//			Log.d("httpResponse", "" + httpResponse);
//
//			InputStream inputStream = httpResponse.getEntity().getContent();
//			InputStreamReader inputStreamReader = new InputStreamReader(
//					inputStream);
//
//			BufferedReader bufferedReader = new BufferedReader(
//					inputStreamReader);
//
//			StringBuilder stringBuilder = new StringBuilder();
//
//			String bufferedStrChunk = null;
//
//			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
//				readunreadnotiresp = stringBuilder.append(bufferedStrChunk)
//						.toString();
//			}
//
//			Log.d("readunreadnotiresp", "" + readunreadnotiresp);
//
//		}
//	}

	// ////////////////////////
	// ///////
	private class ConnectionTaskForfetchimagename extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionfetchimagename mAuth1 = new AuthenticateConnectionfetchimagename();
			try {
				mAuth1.connection();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				exceptioncheck = true;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {

			if (exceptioncheck) {
				exceptioncheck = false;
				Toast.makeText(ShareLocationFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (imagenameresp == null) {

				profilepic.setImageResource(R.drawable.cabappicon);
				drawerprofilepic.setImageResource(R.drawable.cabappicon);

			} else {

				profilepic.setImageBitmap(mIcon11);
				drawerprofilepic.setImageBitmap(mIcon11);
			}

		}
	}

	public class AuthenticateConnectionfetchimagename {

		public AuthenticateConnectionfetchimagename() {

		}

		public void connection() throws Exception {

			// /////////////
			HttpClient httpClient11 = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/fetchimagename.php";

			HttpPost httpPost11 = new HttpPost(url_select);
			BasicNameValuePair MobileNumberBasicNameValuePair11 = new BasicNameValuePair(
					"MobileNumber", MobileNumber);

			List<NameValuePair> nameValuePairList11 = new ArrayList<NameValuePair>();
			nameValuePairList11.add(MobileNumberBasicNameValuePair11);

			UrlEncodedFormEntity urlEncodedFormEntity11 = new UrlEncodedFormEntity(
					nameValuePairList11);
			httpPost11.setEntity(urlEncodedFormEntity11);
			HttpResponse httpResponse11 = httpClient11.execute(httpPost11);

			Log.d("httpResponse", "" + httpResponse11);

			InputStream inputStream11 = httpResponse11.getEntity().getContent();
			InputStreamReader inputStreamReader11 = new InputStreamReader(
					inputStream11);

			BufferedReader bufferedReader11 = new BufferedReader(
					inputStreamReader11);

			StringBuilder stringBuilder11 = new StringBuilder();

			String bufferedStrChunk11 = null;

			while ((bufferedStrChunk11 = bufferedReader11.readLine()) != null) {
				imagenameresp = stringBuilder11.append(bufferedStrChunk11)
						.toString();
			}

			Log.d("imagenameresp", "" + imagenameresp);

			if (imagenameresp == null) {

			} else {

				String url1 = GlobalVariables.ServiceUrl + "/ProfileImages/"
						+ imagenameresp;

				String urldisplay = url1.toString().trim();
				mIcon11 = null;
				String imgString = null;
				try {
					InputStream in = new java.net.URL(urldisplay).openStream();
					mIcon11 = BitmapFactory.decodeStream(in);

					imgString = Base64.encodeToString(
							getBytesFromBitmap(mIcon11), Base64.NO_WRAP);

				} catch (Exception e) {
					Log.e("Error", e.getMessage());
					e.printStackTrace();
				}

				SharedPreferences sharedPreferences1 = getSharedPreferences(
						"userimage", 0);
				SharedPreferences.Editor editor1 = sharedPreferences1.edit();
				editor1.putString("imgname", imagenameresp.trim());
				editor1.putString("imagestr", imgString);
				editor1.commit();
			}

		}
	}

	// ////////////////////
	// ///////
	private class ConnectionTaskForFetchMyClubs extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionFetchMyClubs mAuth1 = new AuthenticateConnectionFetchMyClubs();
			try {
				mAuth1.connection();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
		}
	}

	public class AuthenticateConnectionFetchMyClubs {

		public AuthenticateConnectionFetchMyClubs() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl + "/Fetch_Club.php";

			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair UserNumberBasicNameValuePair = new BasicNameValuePair(
					"OwnerNumber", MobileNumber);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(UserNumberBasicNameValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			Log.d("httpResponse", "" + httpResponse);

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;
			String myclubsresp = null;

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				myclubsresp = stringBuilder.append(bufferedStrChunk).toString();
			}

			Log.d("myclubsresp", "" + myclubsresp);

			SharedPreferences sharedPreferences1 = getSharedPreferences(
					"MyClubs", 0);
			SharedPreferences.Editor editor1 = sharedPreferences1.edit();
			editor1.putString("clubs", myclubsresp.toString().trim());
			editor1.commit();

			// ///////////////
		}
	}

	// convert from bitmap to byte array
	public byte[] getBytesFromBitmap(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 70, stream);
		return stream.toByteArray();
	}

	private boolean isMyServiceRunning(Class<?> serviceClass) {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public void sharemylocationmethod(String str) {

		Log.d("str", "" + str);

		if (str.equalsIgnoreCase("duration")) {

			tracker.send(new HitBuilders.EventBuilder()
					.setCategory("Start Location Sharing (Time)")
					.setAction("Start Location Sharing (Time)")
					.setLabel("Start Location Sharing (Time)").build());

			String[] arr = watchmeforvalue.getText().toString().trim()
					.split(" ");
			Integer min = Integer.parseInt(arr[0]);
			Log.d("min", "" + min);

			ShareLocationObject myObject = new ShareLocationObject();
			myObject.recipientsnames = selectednames;
			myObject.recipientsnumbers = selectednumbers;
			myObject.sharetilltype = "Duration";
			myObject.timetillvalue = System.currentTimeMillis() + (60000 * min);

			Gson gson = new Gson();
			String json = gson.toJson(myObject);

			SharedPreferences locationshapref = getSharedPreferences(
					"ShareLocationShared", 0);
			Editor prefsEditor = locationshapref.edit();
			prefsEditor.putString("ShareLocationObject", json);
			prefsEditor.commit();
		} else {

			tracker.send(new HitBuilders.EventBuilder()
					.setCategory("Start Location Sharing (Destination)")
					.setAction("Start Location Sharing (Destination)")
					.setLabel("Start Location Sharing (Destination)").build());

			ShareLocationObject myObject = new ShareLocationObject();
			myObject.recipientsnames = selectednames;
			myObject.recipientsnumbers = selectednumbers;
			myObject.sharetilltype = "Destination";
			myObject.destinationlongname = from_places.getText().toString()
					.trim();
			LatLng invitemapcenter = new LatLng(fAddress.getLatitude(),
					fAddress.getLongitude());
			myObject.destinationlatlong = invitemapcenter;
			myObject.destinationtimevalue = System.currentTimeMillis()
					+ (60000 * 240);

			Gson gson = new Gson();
			String json = gson.toJson(myObject);

			SharedPreferences locationshapref = getSharedPreferences(
					"ShareLocationShared", 0);
			Editor prefsEditor = locationshapref.edit();
			prefsEditor.putString("ShareLocationObject", json);
			prefsEditor.commit();
		}

		shartsharing.setVisibility(View.GONE);
		stopsharing.setVisibility(View.VISIBLE);
		poolinfoimg.setVisibility(View.VISIBLE);

		selectrecprll.setEnabled(false);
		watchmeforrll.setEnabled(false);
		from_places.setEnabled(false);
		threedotsfrom.setEnabled(false);

		startService(new Intent(this, LocationShareService.class));

	}

	// ///////
	@Override
	public void onLocationChanged(Location location) {

		mycurrentlocationobject = location;

		// if (fromlocation.getText().toString().trim().isEmpty()
		// || fromlocation.getText().toString().equalsIgnoreCase("")) {
		// // Getting latitude of the current location
		// double latitude = location.getLatitude();
		//
		// // Getting longitude of the current location
		// double longitude = location.getLongitude();
		//
		// // Creating a LatLng object for the current location
		// LatLng currentlatLng = new LatLng(latitude, longitude);
		//
		// // Showing the current location in Google Map
		// frommap.moveCamera(CameraUpdateFactory.newLatLng(currentlatLng));
		//
		// // Zoom in the Google Map
		// frommap.animateCamera(CameraUpdateFactory.zoomTo(15));
		// }
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
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

	private void showAlertDialog() {

		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.sharelocationlistshowpopup);

		// //////////////////

		contactsbtn = (Button) dialog.findViewById(R.id.contactsbtn);
		appFrends = (Button) dialog.findViewById(R.id.appFrends);
		myclubbtn = (Button) dialog.findViewById(R.id.myclubbtn);
		donebtn = (Button) dialog.findViewById(R.id.donebtn);

		clubcontactslistll = (LinearLayout) dialog
				.findViewById(R.id.clubcontactslistll);
		contactslist = (ListView) dialog.findViewById(R.id.contactslist);

		mainclublistll = (LinearLayout) dialog
				.findViewById(R.id.mainclublistll);
		listMyclubs = (ListView) dialog.findViewById(R.id.listMyclubs);
		listMembersclubs = (ListView) dialog
				.findViewById(R.id.listMembersclubs);

		searchfromlist = (EditText) dialog.findViewById(R.id.searchfromlist);

		contactsbtn.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));
		appFrends.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));
		myclubbtn.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));
		donebtn.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

		contactsbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				flag = 1;

				contactsbtn.setBackgroundColor(Color.parseColor("#B1C8E6"));
				contactsbtn.setTextColor(Color.BLACK);

				appFrends.setBackgroundColor(Color.parseColor("#4279bd"));
				appFrends.setTextColor(Color.WHITE);

				myclubbtn.setBackgroundColor(Color.parseColor("#4279bd"));
				myclubbtn.setTextColor(Color.WHITE);

				clubcontactslistll.setVisibility(View.VISIBLE);
				searchfromlist.setVisibility(View.VISIBLE);
				mainclublistll.setVisibility(View.GONE);

				ContactsListClass.phoneList.clear();

				for (int i = 0; i < namearraynew.size(); i++) {

					ContactObject cp = new ContactObject();

					cp.setName(namearraynew.get(i));
					cp.setNumber(phonenoarraynew.get(i));
					cp.setImage(imagearraynew.get(i));
					cp.setAppUserimagename("contacticon.png");

					ContactsListClass.phoneList.add(cp);
				}

				Collections.sort(ContactsListClass.phoneList,
						new Comparator<ContactObject>() {
							@Override
							public int compare(ContactObject lhs,
									ContactObject rhs) {
								return lhs.getName().compareTo(rhs.getName());
							}
						});

				objAdapter = new ContactsAdapter(
						ShareLocationFragmentActivity.this,
						ContactsListClass.phoneList);
				contactslist.setAdapter(objAdapter);
				contactslist.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {

						CheckBox chk = (CheckBox) view
								.findViewById(R.id.contactcheck);
						ContactObject bean = ContactsListClass.phoneList
								.get(position);
						if (bean.isSelected()) {
							bean.setSelected(false);
							chk.setChecked(false);
						} else {
							bean.setSelected(true);
							chk.setChecked(true);
						}

					}
				});

				searchfromlist.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence cs, int arg1,
							int arg2, int arg3) {
						// When user changed the Text
						String text = searchfromlist.getText().toString()
								.toLowerCase(Locale.getDefault());
						objAdapter.filter(text);
					}

					@Override
					public void beforeTextChanged(CharSequence arg0, int arg1,
							int arg2, int arg3) {
						// TODO Auto-generated method stub

					}

					@Override
					public void afterTextChanged(Editable arg0) {
						// TODO Auto-generated method stub
					}
				});

			}
		});

		myclubbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				flag = 0;

				contactsbtn.setBackgroundColor(Color.parseColor("#4279bd"));
				contactsbtn.setTextColor(Color.WHITE);

				appFrends.setBackgroundColor(Color.parseColor("#4279bd"));
				appFrends.setTextColor(Color.WHITE);

				myclubbtn.setBackgroundColor(Color.parseColor("#B1C8E6"));
				myclubbtn.setTextColor(Color.BLACK);

				clubcontactslistll.setVisibility(View.GONE);
				searchfromlist.setVisibility(View.GONE);
				mainclublistll.setVisibility(View.VISIBLE);

				ClubListClass.ClubList.clear();
				ClubListClass.MemberClubList.clear();

				SharedPreferences mPrefs111111 = getSharedPreferences(
						"MyClubs", 0);
				String clubs1 = mPrefs111111.getString("clubs", "");

				if (clubs1.equalsIgnoreCase("No Users of your Club")) {
					Toast.makeText(ShareLocationFragmentActivity.this,
							"No Clubs Created Yet!!", Toast.LENGTH_LONG).show();
				} else {

					try {

						ArrayList<String> MyClubPoolId = new ArrayList<String>();
						ArrayList<String> MyClubPoolName = new ArrayList<String>();
						ArrayList<String> MyClubNoofMembers = new ArrayList<String>();
						ArrayList<String> MyClubOwnerName = new ArrayList<String>();
						ArrayList<String> MyClubMembers = new ArrayList<String>();

						ArrayList<String> MemberClubPoolId = new ArrayList<String>();
						ArrayList<String> MemberClubPoolName = new ArrayList<String>();
						ArrayList<String> MemberClubNoofMembers = new ArrayList<String>();
						ArrayList<String> MemberClubOwnerName = new ArrayList<String>();
						ArrayList<String> MemberClubMembers = new ArrayList<String>();

						JSONArray subArray = new JSONArray(clubs1);

						for (int i = 0; i < subArray.length(); i++) {

							if (subArray.getJSONObject(i)
									.getString("IsPoolOwner").toString().trim()
									.equalsIgnoreCase("1")) {
								MyClubPoolId.add(subArray.getJSONObject(i)
										.getString("PoolId").toString());
								MyClubPoolName.add(subArray.getJSONObject(i)
										.getString("PoolName").toString());
								MyClubNoofMembers.add(subArray.getJSONObject(i)
										.getString("NoofMembers").toString());
								MyClubOwnerName.add(subArray.getJSONObject(i)
										.getString("OwnerName").toString());
								MyClubMembers.add(subArray.getJSONObject(i)
										.getString("Members").toString());
							} else {
								MemberClubPoolId.add(subArray.getJSONObject(i)
										.getString("PoolId").toString());
								MemberClubPoolName.add(subArray
										.getJSONObject(i).getString("PoolName")
										.toString());
								MemberClubNoofMembers.add(subArray
										.getJSONObject(i)
										.getString("NoofMembers").toString());
								MemberClubOwnerName.add(subArray
										.getJSONObject(i)
										.getString("OwnerName").toString());
								MemberClubMembers.add(subArray.getJSONObject(i)
										.getString("Members").toString());
							}
						}

						Log.d("MyClubPoolId", "" + MyClubPoolId);
						Log.d("MyClubPoolName", "" + MyClubPoolName);
						Log.d("MyClubNoofMembers", "" + MyClubNoofMembers);
						Log.d("MyClubOwnerName", "" + MyClubOwnerName);
						Log.d("MyClubMembers", "" + MyClubMembers);

						Log.d("MemberClubPoolId", "" + MemberClubPoolId);
						Log.d("MemberClubPoolName", "" + MemberClubPoolName);
						Log.d("MemberClubNoofMembers", ""
								+ MemberClubNoofMembers);
						Log.d("MemberClubOwnerName", "" + MemberClubOwnerName);
						Log.d("MemberClubMembers", "" + MemberClubMembers);

						if (MyClubPoolName.size() > 0) {

							for (int i = 0; i < MyClubPoolName.size(); i++) {

								ClubObject cp = new ClubObject();

								cp.setName(MyClubPoolName.get(i).toString()
										.trim());
								cp.setClubmembers(MyClubMembers.get(i)
										.toString().trim());

								cp.setNoofMembers(MyClubNoofMembers.get(i)
										.toString().trim());

								cp.setClubOwnerName("");

								ClubListClass.ClubList.add(cp);
							}

							ClubsAdaptor adapter = new ClubsAdaptor(
									ShareLocationFragmentActivity.this,
									ClubListClass.ClubList);
							listMyclubs.setAdapter(adapter);
							listMyclubs
									.setOnItemClickListener(new OnItemClickListener() {

										@Override
										public void onItemClick(
												AdapterView<?> parent, View v,
												int position, long id) {
											// TODO Auto-generated method stub
											CheckBox chk = (CheckBox) v
													.findViewById(R.id.myclubcheckBox);
											ClubObject bean = ClubListClass.ClubList
													.get(position);

											if (bean.isSelected()) {
												bean.setSelected(false);
												chk.setChecked(false);
											} else {
												bean.setSelected(true);
												chk.setChecked(true);
											}

										}
									});
						}

						if (MemberClubPoolName.size() > 0) {

							for (int i = 0; i < MemberClubPoolName.size(); i++) {

								ClubObject cp = new ClubObject();

								cp.setName(MemberClubPoolName.get(i).toString()
										.trim());
								cp.setClubmembers(MemberClubMembers.get(i)
										.toString().trim());

								cp.setNoofMembers(MemberClubNoofMembers.get(i)
										.toString().trim());

								cp.setClubOwnerName(MemberClubOwnerName.get(i)
										.toString().trim());

								ClubListClass.MemberClubList.add(cp);
							}

							ClubsAdaptor adapter = new ClubsAdaptor(
									ShareLocationFragmentActivity.this,
									ClubListClass.MemberClubList);

							listMembersclubs.setAdapter(adapter);
							listMembersclubs
									.setOnItemClickListener(new OnItemClickListener() {

										@Override
										public void onItemClick(
												AdapterView<?> parent, View v,
												int position, long id) {
											// TODO Auto-generated method stub
											CheckBox chk = (CheckBox) v
													.findViewById(R.id.myclubcheckBox);
											ClubObject bean = ClubListClass.MemberClubList
													.get(position);

											if (bean.isSelected()) {
												bean.setSelected(false);
												chk.setChecked(false);
											} else {
												bean.setSelected(true);
												chk.setChecked(true);
											}

										}
									});
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		});

		donebtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Animation animScale = AnimationUtils.loadAnimation(
						ShareLocationFragmentActivity.this,
						R.anim.button_click_anim);
				donebtn.startAnimation(animScale);

				Handler mHandler2 = new Handler();
				Runnable mRunnable2 = new Runnable() {
					@Override
					public void run() {

						searchfromlist.setText("");

						selectednames.clear();
						selectednumbers.clear();

						// Retrive Data from list
						if (flag == 1) {
							for (ContactObject bean : ContactsListClass.phoneList) {

								if (bean.isSelected()) {
									selectednames.add(bean.getName());
									selectednumbers.add("0091"
											+ bean.getNumber());
								}
							}
						}

						if (flag == 0) {

							for (ClubObject bean : ClubListClass.ClubList) {

								if (bean.isSelected()) {

									JSONArray subArray;
									try {
										subArray = new JSONArray(bean
												.getClubmembers().toString()
												.trim());
										for (int i = 0; i < subArray.length(); i++) {
											selectednames.add(subArray
													.getJSONObject(i)
													.getString("FullName")
													.toString().trim());
											selectednumbers.add(subArray
													.getJSONObject(i)
													.getString("MemberNumber")
													.toString().trim());
										}

									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								}
							}

							for (ClubObject bean1 : ClubListClass.MemberClubList) {

								if (bean1.isSelected()) {

									JSONArray subArray;
									try {
										subArray = new JSONArray(bean1
												.getClubmembers().toString()
												.trim());
										for (int i = 0; i < subArray.length(); i++) {

											if (subArray.getJSONObject(i)
													.getString("FullName")
													.toString().trim() == null
													|| subArray
															.getJSONObject(i)
															.getString(
																	"FullName")
															.toString()
															.trim()
															.equalsIgnoreCase(
																	"null")) {

											} else {
												selectednames.add(subArray
														.getJSONObject(i)
														.getString("FullName")
														.toString().trim());
												selectednumbers.add(subArray
														.getJSONObject(i)
														.getString(
																"MemberNumber")
														.toString().trim());
											}
										}

										selectednames.add(subArray
												.getJSONObject(0)
												.getString("OwnerName")
												.toString().trim());
										selectednumbers.add(subArray
												.getJSONObject(0)
												.getString("OwnerNumber")
												.toString().trim());

									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								}
							}

							Object[] st = selectednumbers.toArray();
							for (Object s : st) {

								Log.d("selectednumbers.indexOf(s)", ""
										+ selectednumbers.indexOf(s));
								Log.d("selectednumbers.lastIndexOf(s)", ""
										+ selectednumbers.lastIndexOf(s));

								if (selectednumbers.indexOf(s) != selectednumbers
										.lastIndexOf(s)) {
									selectednames.remove(selectednumbers
											.lastIndexOf(s));
									selectednumbers.remove(selectednumbers
											.lastIndexOf(s));
								}
							}

							if (selectednumbers.indexOf(MobileNumber) != -1) {
								selectednames.remove(selectednumbers
										.indexOf(MobileNumber));
								selectednumbers.remove(selectednumbers
										.indexOf(MobileNumber));
							}

						}

						if (selectednames.size() > 0) {

							setnamesandnumbersintext(selectednames,
									selectednumbers);
							dialog.dismiss();

						} else {
							Toast.makeText(ShareLocationFragmentActivity.this,
									"Please select contact(s)",
									Toast.LENGTH_LONG).show();
						}

					}
				};
				mHandler2.postDelayed(mRunnable2, 500);

			}
		});

		SharedPreferences mPrefs11111 = getSharedPreferences("MyClubs", 0);
		String clubs = mPrefs11111.getString("clubs", "");

		if (clubs.equalsIgnoreCase("No Users of your Club")) {
			clubcreated = false;
		} else {
			clubcreated = true;
		}

		// ////////////////////
		if (clubcreated) {

			flag = 0;

			contactsbtn.setBackgroundColor(Color.parseColor("#4279bd"));
			contactsbtn.setTextColor(Color.WHITE);

			appFrends.setBackgroundColor(Color.parseColor("#4279bd"));
			appFrends.setTextColor(Color.WHITE);

			myclubbtn.setBackgroundColor(Color.parseColor("#B1C8E6"));
			myclubbtn.setTextColor(Color.BLACK);

			clubcontactslistll.setVisibility(View.GONE);
			searchfromlist.setVisibility(View.GONE);
			mainclublistll.setVisibility(View.VISIBLE);

			ClubListClass.ClubList.clear();
			ClubListClass.MemberClubList.clear();

			SharedPreferences mPrefs111111 = getSharedPreferences("MyClubs", 0);
			String clubs1 = mPrefs111111.getString("clubs", "");

			if (clubs1.equalsIgnoreCase("No Users of your Club")) {
				Toast.makeText(ShareLocationFragmentActivity.this,
						"No clubs created yet!", Toast.LENGTH_LONG).show();
			} else {

				try {

					ArrayList<String> MyClubPoolId = new ArrayList<String>();
					ArrayList<String> MyClubPoolName = new ArrayList<String>();
					ArrayList<String> MyClubNoofMembers = new ArrayList<String>();
					ArrayList<String> MyClubOwnerName = new ArrayList<String>();
					ArrayList<String> MyClubMembers = new ArrayList<String>();

					ArrayList<String> MemberClubPoolId = new ArrayList<String>();
					ArrayList<String> MemberClubPoolName = new ArrayList<String>();
					ArrayList<String> MemberClubNoofMembers = new ArrayList<String>();
					ArrayList<String> MemberClubOwnerName = new ArrayList<String>();
					ArrayList<String> MemberClubMembers = new ArrayList<String>();

					JSONArray subArray = new JSONArray(clubs1);

					for (int i = 0; i < subArray.length(); i++) {

						if (subArray.getJSONObject(i).getString("IsPoolOwner")
								.toString().trim().equalsIgnoreCase("1")) {
							MyClubPoolId.add(subArray.getJSONObject(i)
									.getString("PoolId").toString());
							MyClubPoolName.add(subArray.getJSONObject(i)
									.getString("PoolName").toString());
							MyClubNoofMembers.add(subArray.getJSONObject(i)
									.getString("NoofMembers").toString());
							MyClubOwnerName.add(subArray.getJSONObject(i)
									.getString("OwnerName").toString());
							MyClubMembers.add(subArray.getJSONObject(i)
									.getString("Members").toString());
						} else {
							MemberClubPoolId.add(subArray.getJSONObject(i)
									.getString("PoolId").toString());
							MemberClubPoolName.add(subArray.getJSONObject(i)
									.getString("PoolName").toString());
							MemberClubNoofMembers.add(subArray.getJSONObject(i)
									.getString("NoofMembers").toString());
							MemberClubOwnerName.add(subArray.getJSONObject(i)
									.getString("OwnerName").toString());
							MemberClubMembers.add(subArray.getJSONObject(i)
									.getString("Members").toString());
						}
					}

					Log.d("MyClubPoolId", "" + MyClubPoolId);
					Log.d("MyClubPoolName", "" + MyClubPoolName);
					Log.d("MyClubNoofMembers", "" + MyClubNoofMembers);
					Log.d("MyClubOwnerName", "" + MyClubOwnerName);
					Log.d("MyClubMembers", "" + MyClubMembers);

					Log.d("MemberClubPoolId", "" + MemberClubPoolId);
					Log.d("MemberClubPoolName", "" + MemberClubPoolName);
					Log.d("MemberClubNoofMembers", "" + MemberClubNoofMembers);
					Log.d("MemberClubOwnerName", "" + MemberClubOwnerName);
					Log.d("MemberClubMembers", "" + MemberClubMembers);

					if (MyClubPoolName.size() > 0) {

						for (int i = 0; i < MyClubPoolName.size(); i++) {

							ClubObject cp = new ClubObject();

							cp.setName(MyClubPoolName.get(i).toString().trim());
							cp.setClubmembers(MyClubMembers.get(i).toString()
									.trim());

							cp.setNoofMembers(MyClubNoofMembers.get(i)
									.toString().trim());

							cp.setClubOwnerName("");

							ClubListClass.ClubList.add(cp);
						}

						ClubsAdaptor adapter = new ClubsAdaptor(
								ShareLocationFragmentActivity.this,
								ClubListClass.ClubList);
						listMyclubs.setAdapter(adapter);
						listMyclubs
								.setOnItemClickListener(new OnItemClickListener() {

									@Override
									public void onItemClick(
											AdapterView<?> parent, View v,
											int position, long id) {
										// TODO Auto-generated method stub
										CheckBox chk = (CheckBox) v
												.findViewById(R.id.myclubcheckBox);
										ClubObject bean = ClubListClass.ClubList
												.get(position);

										if (bean.isSelected()) {
											bean.setSelected(false);
											chk.setChecked(false);
										} else {
											bean.setSelected(true);
											chk.setChecked(true);
										}

									}
								});
					}

					if (MemberClubPoolName.size() > 0) {

						for (int i = 0; i < MemberClubPoolName.size(); i++) {

							ClubObject cp = new ClubObject();

							cp.setName(MemberClubPoolName.get(i).toString()
									.trim());
							cp.setClubmembers(MemberClubMembers.get(i)
									.toString().trim());

							cp.setNoofMembers(MemberClubNoofMembers.get(i)
									.toString().trim());

							cp.setClubOwnerName(MemberClubOwnerName.get(i)
									.toString().trim());

							ClubListClass.MemberClubList.add(cp);
						}

						ClubsAdaptor adapter = new ClubsAdaptor(
								ShareLocationFragmentActivity.this,
								ClubListClass.MemberClubList);

						listMembersclubs.setAdapter(adapter);
						listMembersclubs
								.setOnItemClickListener(new OnItemClickListener() {

									@Override
									public void onItemClick(
											AdapterView<?> parent, View v,
											int position, long id) {
										// TODO Auto-generated method stub
										CheckBox chk = (CheckBox) v
												.findViewById(R.id.myclubcheckBox);
										ClubObject bean = ClubListClass.MemberClubList
												.get(position);

										if (bean.isSelected()) {
											bean.setSelected(false);
											chk.setChecked(false);
										} else {
											bean.setSelected(true);
											chk.setChecked(true);
										}

									}
								});
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		else {

			flag = 1;

			contactsbtn.setBackgroundColor(Color.parseColor("#B1C8E6"));
			contactsbtn.setTextColor(Color.BLACK);

			appFrends.setBackgroundColor(Color.parseColor("#4279bd"));
			appFrends.setTextColor(Color.WHITE);

			myclubbtn.setBackgroundColor(Color.parseColor("#4279bd"));
			myclubbtn.setTextColor(Color.WHITE);

			clubcontactslistll.setVisibility(View.VISIBLE);
			searchfromlist.setVisibility(View.VISIBLE);
			mainclublistll.setVisibility(View.GONE);

			ContactsListClass.phoneList.clear();

			for (int i = 0; i < namearraynew.size(); i++) {

				ContactObject cp = new ContactObject();

				cp.setName(namearraynew.get(i));
				cp.setNumber(phonenoarraynew.get(i));
				cp.setImage(imagearraynew.get(i));
				cp.setAppUserimagename("contacticon.png");

				ContactsListClass.phoneList.add(cp);
			}

			Collections.sort(ContactsListClass.phoneList,
					new Comparator<ContactObject>() {
						@Override
						public int compare(ContactObject lhs, ContactObject rhs) {
							return lhs.getName().compareTo(rhs.getName());
						}
					});

			objAdapter = new ContactsAdapter(
					ShareLocationFragmentActivity.this,
					ContactsListClass.phoneList);
			contactslist.setAdapter(objAdapter);
			contactslist.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					CheckBox chk = (CheckBox) view
							.findViewById(R.id.contactcheck);
					ContactObject bean = ContactsListClass.phoneList
							.get(position);
					if (bean.isSelected()) {
						bean.setSelected(false);
						chk.setChecked(false);
					} else {
						bean.setSelected(true);
						chk.setChecked(true);
					}

				}
			});

			searchfromlist.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence cs, int arg1, int arg2,
						int arg3) {
					// When user changed the Text
					String text = searchfromlist.getText().toString()
							.toLowerCase(Locale.getDefault());
					objAdapter.filter(text);
				}

				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1,
						int arg2, int arg3) {
					// TODO Auto-generated method stub

				}

				@Override
				public void afterTextChanged(Editable arg0) {
					// TODO Auto-generated method stub
				}
			});

		}

		dialog.show();
	}

	private void setnamesandnumbersintext(ArrayList<String> names,
			ArrayList<String> numbers) {

		String str = "";

		for (int i = 0; i < numbers.size(); i++) {

			if (names.get(i).toString().trim() == null
					|| names.get(i).toString().trim().equalsIgnoreCase("null")) {
				str = str + numbers.get(i) + "\n";
			} else {
				str = str + names.get(i) + "\n";
			}
		}

		str = str.substring(0, str.length() - 1);

		Log.d("str", "" + str);
		selectrecipientsvalue.setText(str);

	}

	public boolean checkuserexist(String name, String number) {
		for (int j = 0; j < AppUsersmobilenumberarr.size(); j++) {

			if (AppUsersmobilenumberarr.get(j).toString().trim()
					.equals(number.trim())) {
				return true;
			}
		}
		return false;
	}

	// ///////

	@Override
	public void onBackPressed() {

		if (!fromrelative.isShown()) {

			Intent mainIntent = new Intent(ShareLocationFragmentActivity.this,
					HomeActivity.class);
			mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivityForResult(mainIntent, 500);
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
		} else {
			fromrelative.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Log.d("onResume", "onResume");
		super.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();
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

	}

	@Override
	protected void onStop() {
		super.onStop();
		// TODO Auto-generated method stub
		Log.d("onStop", "onStop");

		if (locationManager != null) {
			locationManager.removeUpdates(this);

		}

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		Log.d("onPause", "onPause");

		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.d("onDestroy", "onDestroy");

		super.onDestroy();
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

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

}
