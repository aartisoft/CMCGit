package com.clubmycab;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
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
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.navdrawer.SimpleSideDrawer;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

public class Invite extends FragmentActivity implements LocationListener,
		OnDateSetListener, TimePickerDialog.OnTimeSetListener {

	TextView textFrom;
	TextView textTo;
	TextView date;
	TextView time;
	TextView seats;

	AutoCompleteTextView from_places;
	AutoCompleteTextView to_places;
	EditText dateedittext;
	EditText timeedittext;
	EditText seatsedittext;

	Button invite;

	Calendar myCalendar = Calendar.getInstance();

	CircularImageView profilepic;
	TextView username;
	ImageView notificationimg;

	ImageView sidemenu;

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

	Button threedotsfrom;
	Button threedotsto;

	RelativeLayout fromrelative;
	TextView fromlocation;
	Button fromdone;
	Button cancel;

	private GoogleMap myMap;

	String whichdotclick;
	LocationManager locationManager;

	String FullName;
	String MobileNumber;
	String CabId;

	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";

	// private static final String API_KEY =
	// "AIzaSyBRKgFt6KHJrpcyYjKXHmnUpU6E6HWODGI";
	private static final String API_KEY = "AIzaSyBqd05mV8c2VTIAKhYP1mFKF7TRueU2-Z0";
	private static final String LOG_TAG = "ExampleApp";

	Location mycurrentlocationobject;

	RelativeLayout unreadnoticountrl;
	TextView unreadnoticount;

	LatLng invitemapcenter;

	LatLng mapfromlatlng;
	LatLng maptolatlng;

	Boolean flagchk;

	String fromshortname;
	String toshortname;
	String readunreadnotiresp;
	String imagenameresp;
	Bitmap mIcon11;

	private TextView mTextViewSetHomeFavFrom, mTextViewSetOfficeFavFrom,
			mTextViewSetHomeFavTo, mTextViewSetOfficeFavTo;

	LinearLayout homeofficellvalues;
	ImageView homeimg;
	ImageView officeimg;

	LinearLayout homeofficellvaluesto;
	ImageView homeimgto;
	ImageView officeimgto;

	JSONArray saveasjsonarray;
	ArrayList<String> type = new ArrayList<String>();
	ArrayList<String> Latitude = new ArrayList<String>();
	ArrayList<String> Longitude = new ArrayList<String>();
	ArrayList<String> Address = new ArrayList<String>();
	ArrayList<String> Locality = new ArrayList<String>();
	ArrayList<String> ShortAddress = new ArrayList<String>();

	Address fAddress, tAddress;

	UrlConstant checkurl;
	RelativeLayout inviterl;
	Tracker tracker;

	ImageView clearedittextimgfrom;
	ImageView clearedittextimgto;

	boolean exceptioncheck = false;

	public static final String DATEPICKER_TAG = "datepicker";
	public static final String TIMEPICKER_TAG = "timepicker";

	String activeridesresp;
	String archieveridesresp;

	ArrayList<String> FromLocation = new ArrayList<String>();
	ArrayList<String> ToLocation = new ArrayList<String>();
	ArrayList<String> FromShortName = new ArrayList<String>();
	ArrayList<String> ToShortName = new ArrayList<String>();
	ArrayList<String> Seats = new ArrayList<String>();
	ArrayList<String> TravelDate = new ArrayList<String>();
	ArrayList<String> TravelTime = new ArrayList<String>();
	ArrayList<String> Seat_Status = new ArrayList<String>();

	ArrayList<String> FromLocationNew = new ArrayList<String>();
	ArrayList<String> ToLocationNew = new ArrayList<String>();
	ArrayList<String> FromShortNameNew = new ArrayList<String>();
	ArrayList<String> ToShortNameNew = new ArrayList<String>();
	ArrayList<String> SeatsNew = new ArrayList<String>();
	ArrayList<String> TravelDateNew = new ArrayList<String>();
	ArrayList<String> TravelTimeNew = new ArrayList<String>();
	ArrayList<String> Seat_StatusNew = new ArrayList<String>();

	LinearLayout inviteloadingll;
	LinearLayout topthreeridesll;
	ListView topthreerideslist;

	TopThreeRidesAdaptor topthreeadaptor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invite);

		// Check if Internet present
		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(Invite.this);
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

		inviteloadingll = (LinearLayout) findViewById(R.id.inviteloadingll);
		inviteloadingll.setVisibility(View.VISIBLE);

		topthreeridesll = (LinearLayout) findViewById(R.id.topthreeridesll);
		topthreeridesll.setVisibility(View.GONE);

		topthreerideslist = (ListView) findViewById(R.id.topthreerideslist);
		
		LinearLayout topthreeridesll = (LinearLayout)findViewById(R.id.topthreeridesll);
		topthreeridesll.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				topthreerideslist.setVisibility(View.VISIBLE);
			}
		});

		GoogleAnalytics analytics = GoogleAnalytics.getInstance(Invite.this);
		tracker = analytics.newTracker("UA-63477985-1");

		// All subsequent hits will be send with screen name = "main screen"
		tracker.setScreenName("Invitation Details");

		inviterl = (RelativeLayout) findViewById(R.id.inviterl);
		inviterl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Log.e("inviterl", "inviterl");
			}
		});

		checkurl = new UrlConstant();

		flagchk = true;

		mNav = new SimpleSideDrawer(this);
		mNav.setLeftBehindContentView(R.layout.activity_behind_left_simple);

		findViewById(R.id.sidemenu).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// mainhomepagerl.setAlpha((float) 0.3);
				mNav.toggleLeftDrawer();

			}
		});

		myprofile = (TextView) findViewById(R.id.myprofile);
		myprofile.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));
		myrides = (TextView) findViewById(R.id.myrides);
		myrides.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));
		bookacab = (TextView) findViewById(R.id.bookacab);
		bookacab.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));
		sharemylocation = (TextView) findViewById(R.id.sharemylocation);
		sharemylocation.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));
		myclubs = (TextView) findViewById(R.id.myclubs);
		myclubs.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));
		sharethisapp = (TextView) findViewById(R.id.sharethisapp);
		sharethisapp.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));
		mypreferences = (TextView) findViewById(R.id.mypreferences);
		mypreferences.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));
		about = (TextView) findViewById(R.id.about);
		about.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

		myprofile.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				mNav.toggleDrawer();

				tracker.send(new HitBuilders.EventBuilder()
						.setCategory("MyProfile Click")
						.setAction("MyProfile Click")
						.setLabel("MyProfile Click").build());

				Intent mainIntent = new Intent(Invite.this, MyProfile.class);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
		});

		myrides.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				mNav.toggleDrawer();

				tracker.send(new HitBuilders.EventBuilder()
						.setCategory("MyRides Click")
						.setAction("MyRides Click").setLabel("MyRides Click")
						.build());

				Intent mainIntent = new Intent(Invite.this, MyRides.class);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
		});

		bookacab.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				mNav.toggleDrawer();

				tracker.send(new HitBuilders.EventBuilder()
						.setCategory("BookaCab Click")
						.setAction("BookaCab Click").setLabel("BookaCab Click")
						.build());

				Intent mainIntent = new Intent(Invite.this, BookaCab.class);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
		});

		sharemylocation.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				mNav.toggleDrawer();

				tracker.send(new HitBuilders.EventBuilder()
						.setCategory("ShareLocation Click")
						.setAction("ShareLocation Click")
						.setLabel("ShareLocation Click").build());

				Intent mainIntent = new Intent(Invite.this, ShareLocation.class);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
		});

		myclubs.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				mNav.toggleDrawer();

				tracker.send(new HitBuilders.EventBuilder()
						.setCategory("MyClubs Click")
						.setAction("MyClubs Click").setLabel("MyClubs Click")
						.build());

				Intent mainIntent = new Intent(Invite.this, MyClubs.class);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
		});

		sharethisapp.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				mNav.toggleDrawer();

				tracker.send(new HitBuilders.EventBuilder()
						.setCategory("ShareApp Click")
						.setAction("ShareApp Click").setLabel("ShareApp Click")
						.build());

				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent
						.putExtra(
								Intent.EXTRA_TEXT,
								"I am using this cool app 'ClubMyCab' to share & book cabs. Check it out @ http://tinyurl.com/n7j6chq");
				sendIntent.setType("text/plain");
				startActivity(Intent.createChooser(sendIntent, "Share Via"));

			}
		});

		mypreferences.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				mNav.toggleDrawer();

				tracker.send(new HitBuilders.EventBuilder()
						.setCategory("Settings Click")
						.setAction("Settings Click").setLabel("Settings Click")
						.build());

				Intent mainIntent = new Intent(Invite.this,
						SettingDetails.class);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
		});

		about.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				mNav.toggleDrawer();

				tracker.send(new HitBuilders.EventBuilder()
						.setCategory("About Click").setAction("About Click")
						.setLabel("About Click").build());

				Intent mainIntent = new Intent(Invite.this, MainActivity.class);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
		});

		profilepic = (CircularImageView) findViewById(R.id.profilepic);
		notificationimg = (ImageView) findViewById(R.id.notificationimg);
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

				Intent mainIntent = new Intent(Invite.this,
						AllNotificationRequest.class);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);

			}
		});

		homeofficellvalues = (LinearLayout) findViewById(R.id.homeofficellvalues);
		homeimg = (ImageView) findViewById(R.id.homeimg);
		officeimg = (ImageView) findViewById(R.id.officeimg);

		homeofficellvaluesto = (LinearLayout) findViewById(R.id.homeofficellvaluesto);
		homeimgto = (ImageView) findViewById(R.id.homeimgto);
		officeimgto = (ImageView) findViewById(R.id.officeimgto);

		homeofficellvalues.setVisibility(View.GONE);
		homeofficellvaluesto.setVisibility(View.GONE);

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

						fromshortname = ShortAddress.get(i).toString().trim();
					}
				}

				homeofficellvalues.setVisibility(View.GONE);
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

						fromshortname = ShortAddress.get(i).toString().trim();
					}
				}

				homeofficellvalues.setVisibility(View.GONE);
			}
		});

		homeimgto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				for (int i = 0; i < type.size(); i++) {

					if (type.get(i).toString().trim().equalsIgnoreCase("Home")) {
						to_places.setText(Address.get(i).toString().trim());
						tAddress = new Address(null);
						tAddress.setLatitude(Double.parseDouble(Latitude.get(i)
								.toString().trim()));
						tAddress.setLongitude(Double.parseDouble(Longitude
								.get(i).toString().trim()));
						tAddress.setLocality(Locality.get(i).toString().trim());

						toshortname = ShortAddress.get(i).toString().trim();
					}
				}

				homeofficellvaluesto.setVisibility(View.GONE);
			}
		});

		officeimgto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				for (int i = 0; i < type.size(); i++) {

					if (type.get(i).toString().trim()
							.equalsIgnoreCase("Office")) {
						to_places.setText(Address.get(i).toString().trim());
						tAddress = new Address(null);
						tAddress.setLatitude(Double.parseDouble(Latitude.get(i)
								.toString().trim()));
						tAddress.setLongitude(Double.parseDouble(Longitude
								.get(i).toString().trim()));
						tAddress.setLocality(Locality.get(i).toString().trim());

						toshortname = ShortAddress.get(i).toString().trim();
					}
				}

				homeofficellvaluesto.setVisibility(View.GONE);
			}
		});

		unreadnoticountrl = (RelativeLayout) findViewById(R.id.unreadnoticountrl);
		unreadnoticount = (TextView) findViewById(R.id.unreadnoticount);

		textFrom = (TextView) findViewById(R.id.textFrom);
		textTo = (TextView) findViewById(R.id.textTo);
		date = (TextView) findViewById(R.id.date);
		time = (TextView) findViewById(R.id.time);
		seats = (TextView) findViewById(R.id.seats);

		from_places = (AutoCompleteTextView) findViewById(R.id.from_places);
		to_places = (AutoCompleteTextView) findViewById(R.id.to_places);
		dateedittext = (EditText) findViewById(R.id.dateedittext);
		timeedittext = (EditText) findViewById(R.id.timeedittext);
		seatsedittext = (EditText) findViewById(R.id.seatsedittext);

		clearedittextimgfrom = (ImageView) findViewById(R.id.clearedittextimgfrom);
		clearedittextimgfrom.setVisibility(View.GONE);

		clearedittextimgto = (ImageView) findViewById(R.id.clearedittextimgto);
		clearedittextimgto.setVisibility(View.GONE);

		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.list_item);
		adapter.setNotifyOnChange(true);

		from_places.setAdapter(new PlacesAutoCompleteAdapter(this,
				R.layout.list_item));

		to_places.setAdapter(new PlacesAutoCompleteAdapter(this,
				R.layout.list_item));

		invite = (Button) findViewById(R.id.invitebtn);

		textFrom.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		from_places.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

		textTo.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		to_places.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

		date.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		dateedittext.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

		time.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		timeedittext.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

		seats.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		seatsedittext.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

		invite.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

		threedotsfrom = (Button) findViewById(R.id.threedotsfrom);
		threedotsfrom.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

		threedotsto = (Button) findViewById(R.id.threedotsto);
		threedotsto.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

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

		from_places.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View view, MotionEvent motionEvent) {

				homeofficellvaluesto.setVisibility(View.GONE);

				FavoritesLocationReadWrite favoritesLocationReadWrite = new FavoritesLocationReadWrite(
						Invite.this);
				JSONArray saveasjsonarray;
				try {
					saveasjsonarray = favoritesLocationReadWrite.readFromFile();
					if (saveasjsonarray.length() > 0) {
						Log.e("saveasjsonarray", "" + saveasjsonarray);
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
										.getString("Latitude").toString()
										.trim());
								Longitude.add(saveasjsonarray.getJSONObject(i)
										.getString("Longitude").toString()
										.trim());
								Address.add(saveasjsonarray.getJSONObject(i)
										.getString("Address").toString().trim());
								Locality.add(saveasjsonarray.getJSONObject(i)
										.getString("Locality").toString()
										.trim());
								ShortAddress.add(saveasjsonarray
										.getJSONObject(i)
										.getString("shortaddress").toString()
										.trim());
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} else {
						Log.e("saveasjsonarray", "null");
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

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
		from_places.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				// When user changed the Text

				String text = from_places.getText().toString().trim();
				if (text.isEmpty() || text.equalsIgnoreCase("")) {
					clearedittextimgfrom.setVisibility(View.GONE);
				} else {
					clearedittextimgfrom.setVisibility(View.VISIBLE);
				}

				Log.d("from onTextChanged", "from onTextChanged");

				if (flagchk) {
					flagchk = false;
				} else {
					fromshortname = getaddressfromautoplace(Invite.this,
							from_places.getText().toString().trim());
				}
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

		to_places.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View view, MotionEvent motionEvent) {

				homeofficellvalues.setVisibility(View.GONE);

				FavoritesLocationReadWrite favoritesLocationReadWrite = new FavoritesLocationReadWrite(
						Invite.this);
				JSONArray saveasjsonarray;
				try {
					saveasjsonarray = favoritesLocationReadWrite.readFromFile();
					if (saveasjsonarray.length() > 0) {
						Log.e("saveasjsonarray", "" + saveasjsonarray);
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
										.getString("Latitude").toString()
										.trim());
								Longitude.add(saveasjsonarray.getJSONObject(i)
										.getString("Longitude").toString()
										.trim());
								Address.add(saveasjsonarray.getJSONObject(i)
										.getString("Address").toString().trim());
								Locality.add(saveasjsonarray.getJSONObject(i)
										.getString("Locality").toString()
										.trim());
								ShortAddress.add(saveasjsonarray
										.getJSONObject(i)
										.getString("shortaddress").toString()
										.trim());
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} else {
						Log.e("saveasjsonarray", "null");
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				if (type.contains("Home") && type.contains("Office")) {
					homeofficellvaluesto.setVisibility(View.VISIBLE);
					homeimgto.setVisibility(View.VISIBLE);
					officeimgto.setVisibility(View.VISIBLE);
				} else if (type.contains("Home") && !type.contains("Office")) {
					homeofficellvaluesto.setVisibility(View.VISIBLE);
					homeimgto.setVisibility(View.VISIBLE);
					officeimgto.setVisibility(View.GONE);
				} else if (!type.contains("Home") && type.contains("Office")) {
					homeofficellvaluesto.setVisibility(View.VISIBLE);
					homeimgto.setVisibility(View.GONE);
					officeimgto.setVisibility(View.VISIBLE);
				}
				return false;
			}
		});

		to_places.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Log.d(TAG, "mAutoFrom onItemClick");
				tAddress = null; // reset previous
				InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				in.hideSoftInputFromWindow(
						to_places.getApplicationWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);

				tAddress = geocodeAddress(to_places.getText().toString());

			}
		});
		to_places.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				// When user changed the Text

				String text = to_places.getText().toString().trim();
				if (text.isEmpty() || text.equalsIgnoreCase("")) {
					clearedittextimgto.setVisibility(View.GONE);
				} else {
					clearedittextimgto.setVisibility(View.VISIBLE);
				}

				Log.d("to onTextChanged", "to onTextChanged");

				if (flagchk) {
					flagchk = false;
				} else {
					toshortname = getaddressfromautoplace(Invite.this,
							to_places.getText().toString().trim());
				}
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

		clearedittextimgfrom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				from_places.setText("");
			}
		});

		clearedittextimgto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				to_places.setText("");
			}
		});

		myMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.frommap)).getMap();

		myMap.setMyLocationEnabled(true);

		myMap.setOnCameraChangeListener(new OnCameraChangeListener() {

			@Override
			public void onCameraChange(CameraPosition cameraPosition) {

				invitemapcenter = cameraPosition.target;

				String address = getAddress(Invite.this,
						invitemapcenter.latitude, invitemapcenter.longitude);
				Log.e("address", "" + address);

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
				flagchk = true;
				if (whichdotclick.equalsIgnoreCase("fromdot")) {

					mapfromlatlng = invitemapcenter;
					fromshortname = getAddressshort(Invite.this,
							mapfromlatlng.latitude, mapfromlatlng.longitude);

					fAddress = null; // reset previous

					from_places.setText(fromlocationname);

					String jnd = from_places.getText().toString().trim();

					Geocoder fcoder = new Geocoder(Invite.this);
					try {
						ArrayList<Address> adresses = (ArrayList<Address>) fcoder
								.getFromLocationName(jnd, 50);

						for (Address add : adresses) {
							fAddress = add;
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				} else if (whichdotclick.equalsIgnoreCase("todot")) {

					maptolatlng = invitemapcenter;
					toshortname = getAddressshort(Invite.this,
							maptolatlng.latitude, maptolatlng.longitude);

					tAddress = null; // reset previous

					to_places.setText(fromlocationname);

					String jnd2 = to_places.getText().toString().trim();

					Geocoder tcoder = new Geocoder(Invite.this);
					try {
						ArrayList<Address> adresses = (ArrayList<Address>) tcoder
								.getFromLocationName(jnd2, 50);

						for (Address add : adresses) {
							tAddress = add;
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		});

		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				fromrelative.setVisibility(View.GONE);
			}
		});

		threedotsfrom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				whichdotclick = "fromdot";

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
						myMap.moveCamera(CameraUpdateFactory
								.newLatLng(currentlatLng));

						// Zoom in the Google Map
						myMap.animateCamera(CameraUpdateFactory.zoomTo(15));

						String address = getAddress(Invite.this, latitude,
								longitude);

						fromlocation.setText(address);
						fromrelative.setVisibility(View.VISIBLE);

					} else {

						// no network provider is enabled
						AlertDialog.Builder dialog = new AlertDialog.Builder(
								Invite.this);
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

					Geocoder coder = new Geocoder(Invite.this);
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

		mTextViewSetHomeFavFrom = (TextView) findViewById(R.id.textViewHome);
		mTextViewSetHomeFavFrom.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// Log.d("RateMyClub", "onClick mTextViewSetHomeFav : ");

				try {
					if (from_places.getText().toString().isEmpty()) {
						Toast.makeText(Invite.this,
								"Please enter an address to save",
								Toast.LENGTH_LONG).show();
					} else {
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("type", "Home");
						jsonObject.put("Latitude",
								Double.toString(fAddress.getLatitude()));
						jsonObject.put("Longitude",
								Double.toString(fAddress.getLongitude()));
						jsonObject.put("Address", from_places.getText()
								.toString());
						jsonObject.put("Locality", fAddress.getLocality()
								.toString());
						jsonObject.put("shortaddress", fromshortname);

						FavoritesLocationReadWrite favoritesLocationReadWrite = new FavoritesLocationReadWrite(
								Invite.this);

						if (favoritesLocationReadWrite.saveToFile(jsonObject
								.toString())) {
							Toast.makeText(Invite.this, "Saved!",
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(Invite.this,
									"Error saving. Please try again!",
									Toast.LENGTH_LONG).show();
						}
						Log.d("RateMyClub", "onClick mTextViewSetHomeFav : "
								+ favoritesLocationReadWrite.readFromFile());
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		});

		mTextViewSetOfficeFavFrom = (TextView) findViewById(R.id.textViewOffice);
		mTextViewSetOfficeFavFrom
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						// Log.d("RateMyClub",
						// "onClick mTextViewSetOfficeFav : ");

						try {
							if (from_places.getText().toString().isEmpty()
									|| fAddress == null) {
								Toast.makeText(Invite.this,
										"Please enter an address to save",
										Toast.LENGTH_LONG).show();
							} else {
								JSONObject jsonObject = new JSONObject();
								jsonObject.put("type", "Office");
								jsonObject.put("Latitude",
										Double.toString(fAddress.getLatitude()));
								jsonObject.put("Longitude", Double
										.toString(fAddress.getLongitude()));
								jsonObject.put("Address", from_places.getText()
										.toString());
								jsonObject.put("Locality", fAddress
										.getLocality().toString());
								jsonObject.put("shortaddress", fromshortname);

								FavoritesLocationReadWrite favoritesLocationReadWrite = new FavoritesLocationReadWrite(
										Invite.this);
								if (favoritesLocationReadWrite
										.saveToFile(jsonObject.toString())) {
									Toast.makeText(Invite.this, "Saved!",
											Toast.LENGTH_LONG).show();
								} else {
									Toast.makeText(Invite.this,
											"Error saving. Please try again!",
											Toast.LENGTH_LONG).show();
								}

								Log.d("RateMyClub",
										"onClick mTextViewSetOfficeFav : "
												+ favoritesLocationReadWrite
														.readFromFile());
							}

						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
					}
				});

		mTextViewSetHomeFavTo = (TextView) findViewById(R.id.textViewHomeTo);
		mTextViewSetHomeFavTo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// Log.d("RateMyClub", "onClick mTextViewSetHomeFav : ");

				try {
					if (to_places.getText().toString().isEmpty()
							|| tAddress == null) {
						Toast.makeText(Invite.this,
								"Please enter an address to save",
								Toast.LENGTH_LONG).show();
					} else {
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("type", "Home");
						jsonObject.put("Latitude",
								Double.toString(tAddress.getLatitude()));
						jsonObject.put("Longitude",
								Double.toString(tAddress.getLongitude()));
						jsonObject.put("Address", to_places.getText()
								.toString());
						jsonObject.put("Locality", tAddress.getLocality()
								.toString());
						jsonObject.put("shortaddress", toshortname);

						FavoritesLocationReadWrite favoritesLocationReadWrite = new FavoritesLocationReadWrite(
								Invite.this);
						if (favoritesLocationReadWrite.saveToFile(jsonObject
								.toString())) {
							Toast.makeText(Invite.this, "Saved!",
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(Invite.this,
									"Error saving. Please try again!",
									Toast.LENGTH_LONG).show();
						}

						Log.d("RateMyClub", "onClick mTextViewSetHomeFavTo : "
								+ favoritesLocationReadWrite.readFromFile());
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		});

		mTextViewSetOfficeFavTo = (TextView) findViewById(R.id.textViewOfficeTo);
		mTextViewSetOfficeFavTo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// Log.d("RateMyClub", "onClick mTextViewSetOfficeFav : ");

				try {
					if (to_places.getText().toString().isEmpty()
							|| tAddress == null) {
						Toast.makeText(Invite.this,
								"Please enter an address to save",
								Toast.LENGTH_LONG).show();
					} else {
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("type", "Office");
						jsonObject.put("Latitude",
								Double.toString(tAddress.getLatitude()));
						jsonObject.put("Longitude",
								Double.toString(tAddress.getLongitude()));
						jsonObject.put("Address", to_places.getText()
								.toString());
						jsonObject.put("Locality", tAddress.getLocality()
								.toString());
						jsonObject.put("shortaddress", toshortname);

						FavoritesLocationReadWrite favoritesLocationReadWrite = new FavoritesLocationReadWrite(
								Invite.this);
						if (favoritesLocationReadWrite.saveToFile(jsonObject
								.toString())) {
							Toast.makeText(Invite.this, "Saved!",
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(Invite.this,
									"Error saving. Please try again!",
									Toast.LENGTH_LONG).show();
						}

						Log.d("RateMyClub",
								"onClick mTextViewSetOfficeFavTo : "
										+ favoritesLocationReadWrite
												.readFromFile());
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		});

		threedotsto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				whichdotclick = "todot";

				if (to_places.getText().toString().trim().isEmpty()
						|| to_places.getText().toString().equalsIgnoreCase("")) {

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

						String address = getAddress(Invite.this, latitude,
								longitude);

						fromlocation.setText(address);
						fromrelative.setVisibility(View.VISIBLE);
					} else {

						// no network provider is enabled
						AlertDialog.Builder dialog = new AlertDialog.Builder(
								Invite.this);
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

					String jnd = to_places.getText().toString().trim();

					Geocoder coder = new Geocoder(Invite.this);
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

		final Calendar calendar = Calendar.getInstance();

		final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
				Invite.this, calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH), isVibrate());

		calendar.add(Calendar.MINUTE, 30);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);

		final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
				Invite.this, hour, minute, false, false);

		if (savedInstanceState != null) {
			DatePickerDialog dpd = (DatePickerDialog) getSupportFragmentManager()
					.findFragmentByTag(DATEPICKER_TAG);
			if (dpd != null) {
				dpd.setOnDateSetListener(Invite.this);
			}

			TimePickerDialog tpd = (TimePickerDialog) getSupportFragmentManager()
					.findFragmentByTag(TIMEPICKER_TAG);
			if (tpd != null) {
				tpd.setOnTimeSetListener(Invite.this);
			}
		}

		dateedittext.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				datePickerDialog.setVibrate(isVibrate());
				datePickerDialog.setYearRange(1985, 2028);
				datePickerDialog
						.setCloseOnSingleTapDay(isCloseOnSingleTapDay());
				datePickerDialog.show(getSupportFragmentManager(),
						DATEPICKER_TAG);
			}
		});

		dateedittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {

					datePickerDialog.setVibrate(isVibrate());
					datePickerDialog.setYearRange(1985, 2028);
					datePickerDialog
							.setCloseOnSingleTapDay(isCloseOnSingleTapDay());
					datePickerDialog.show(getSupportFragmentManager(),
							DATEPICKER_TAG);
				}
			}

		});

		// time selection
		timeedittext.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				timePickerDialog.setVibrate(isVibrate());
				timePickerDialog
						.setCloseOnSingleTapMinute(isCloseOnSingleTapMinute());
				timePickerDialog.show(getSupportFragmentManager(),
						TIMEPICKER_TAG);
			}
		});

		timeedittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {

					timePickerDialog.setVibrate(isVibrate());
					timePickerDialog
							.setCloseOnSingleTapMinute(isCloseOnSingleTapMinute());
					timePickerDialog.show(getSupportFragmentManager(),
							TIMEPICKER_TAG);
				}
			}

		});

		seatsedittext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				AlertDialog dialog;
				AlertDialog.Builder builder = new AlertDialog.Builder(
						Invite.this);
				builder.setTitle("Select No of Seats");

				final CharSequence str[] = { "1", "2", "3", "4", "5", "6" };

				builder.setItems(str, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int position) {
						// TODO Auto-generated method stub
						seatsedittext.setText(str[position]);
					}
				});

				dialog = builder.create();
				dialog.show();

			}
		});

		seatsedittext
				.setOnFocusChangeListener(new View.OnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {

							// TODO Auto-generated method stub
							AlertDialog dialog;
							AlertDialog.Builder builder = new AlertDialog.Builder(
									Invite.this);
							builder.setTitle("Select No of Seats");

							final CharSequence str[] = { "1", "2", "3", "4",
									"5", "6" };

							builder.setItems(str,
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int position) {
											// TODO Auto-generated method stub
											seatsedittext
													.setText(str[position]);
										}
									});

							dialog = builder.create();
							dialog.show();

						}
					}

				});

		invite.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Animation animScale = AnimationUtils.loadAnimation(Invite.this,
						R.anim.button_click_anim);
				invite.startAnimation(animScale);

				Handler mHandler2 = new Handler();
				Runnable mRunnable2 = new Runnable() {
					@Override
					public void run() {

						if (from_places.getText().toString().trim().isEmpty()
								|| fAddress == null) {

							from_places.requestFocus();

							AlertDialog.Builder builder = new AlertDialog.Builder(
									Invite.this);

							builder.setMessage("Please Enter From Location. If you have already selected a location on map please try again by selecting a nearby location");
							builder.setPositiveButton("OK", null);
							AlertDialog dialog = builder.show();
							TextView messageText = (TextView) dialog
									.findViewById(android.R.id.message);
							messageText.setGravity(Gravity.CENTER);
							dialog.show();

						} else if (to_places.getText().toString().trim()
								.isEmpty()
								|| tAddress == null) {

							to_places.requestFocus();

							AlertDialog.Builder builder = new AlertDialog.Builder(
									Invite.this);

							builder.setMessage("Please Enter To Location. If you have already selected a location on map please try again by selecting a nearby location");
							builder.setPositiveButton("OK", null);
							AlertDialog dialog = builder.show();
							TextView messageText = (TextView) dialog
									.findViewById(android.R.id.message);
							messageText.setGravity(Gravity.CENTER);
							dialog.show();

						} else if (dateedittext.getText().toString().trim()
								.isEmpty()) {

							dateedittext.requestFocus();

							AlertDialog.Builder builder = new AlertDialog.Builder(
									Invite.this);

							builder.setMessage("Please Enter Date");
							builder.setPositiveButton("OK", null);
							AlertDialog dialog = builder.show();
							TextView messageText = (TextView) dialog
									.findViewById(android.R.id.message);
							messageText.setGravity(Gravity.CENTER);
							dialog.show();

						}

						else if (timeedittext.getText().toString().trim()
								.isEmpty()) {

							timeedittext.requestFocus();

							AlertDialog.Builder builder = new AlertDialog.Builder(
									Invite.this);

							builder.setMessage("Please Enter Time");
							builder.setPositiveButton("OK", null);
							AlertDialog dialog = builder.show();
							TextView messageText = (TextView) dialog
									.findViewById(android.R.id.message);
							messageText.setGravity(Gravity.CENTER);
							dialog.show();

						} else if (seatsedittext.getText().toString().trim()
								.isEmpty()) {

							seatsedittext.requestFocus();

							AlertDialog.Builder builder = new AlertDialog.Builder(
									Invite.this);

							builder.setMessage("Please Enter Seats Available");
							builder.setPositiveButton("OK", null);
							AlertDialog dialog = builder.show();
							TextView messageText = (TextView) dialog
									.findViewById(android.R.id.message);
							messageText.setGravity(Gravity.CENTER);
							dialog.show();

						} else if (!dateedittext.getText().toString().trim()
								.isEmpty()
								&& !timeedittext.getText().toString().trim()
										.isEmpty()) {

							SimpleDateFormat dateFormat = new SimpleDateFormat(
									"dd/MM/yyyy hh:mm aa");

							Date currentTime = new Date();
							Date rideTime = new Date();
							try {
								rideTime = dateFormat.parse(dateedittext
										.getText().toString().trim()
										+ " "
										+ timeedittext.getText().toString()
												.trim());
							} catch (Exception e) {
								e.printStackTrace();
							}

							if (rideTime.compareTo(currentTime) < 0) {
								timeedittext.requestFocus();

								AlertDialog.Builder builder = new AlertDialog.Builder(
										Invite.this);

								builder.setMessage("The time entered is before the current time");
								builder.setPositiveButton("OK", null);
								AlertDialog dialog = builder.show();
								TextView messageText = (TextView) dialog
										.findViewById(android.R.id.message);
								messageText.setGravity(Gravity.CENTER);
								dialog.show();
							} else {

								Log.i("fromshortname", "" + fromshortname);
								Log.i("toshortname", "" + toshortname);

								CabId = MobileNumber
										+ System.currentTimeMillis();
								String OwnerName = FullName;

								Intent mainIntent = new Intent(Invite.this,
										ContactsMyClub.class);
								mainIntent.putExtra("fromcome", "invite");
								mainIntent.putExtra("CabId", CabId);
								mainIntent.putExtra("MobileNumber",
										MobileNumber);
								mainIntent.putExtra("OwnerName", OwnerName);
								mainIntent.putExtra("FromLocation", from_places
										.getText().toString().trim());
								mainIntent.putExtra("ToLocation", to_places
										.getText().toString().trim());
								mainIntent.putExtra("TravelDate", dateedittext
										.getText().toString().trim());
								mainIntent.putExtra("TravelTime", timeedittext
										.getText().toString().trim());
								mainIntent.putExtra("Seats", seatsedittext
										.getText().toString().trim());
								mainIntent.putExtra("fromshortname",
										fromshortname);
								mainIntent.putExtra("toshortname", toshortname);
								startActivityForResult(mainIntent, 500);
								overridePendingTransition(
										R.anim.slide_in_right,
										R.anim.slide_out_left);
							}

							// Log.d("Invite : ", "Date comparison : " +
							// rideTime.compareTo(currentTime) +
							// " currentTime : " + currentTime.toString() +
							// " rideTime : " + rideTime.toString());

						}
					}
				};
				mHandler2.postDelayed(mRunnable2, 500);

			}
		});

		// ///////////////
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new fetchtopthreerides()
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			new fetchtopthreerides().execute();
		}

		// ///////////////
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new ConnectionTaskForreadunreadnotification()
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			new ConnectionTaskForreadunreadnotification().execute();
		}

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
	}

	// ///////
	private class ConnectionTaskForreadunreadnotification extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionreadunreadnotification mAuth1 = new AuthenticateConnectionreadunreadnotification();
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
				Toast.makeText(Invite.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (readunreadnotiresp.equalsIgnoreCase("0")) {

				unreadnoticountrl.setVisibility(View.GONE);

			} else {

				unreadnoticountrl.setVisibility(View.VISIBLE);
				unreadnoticount.setText(readunreadnotiresp);
			}
		}

	}

	public class AuthenticateConnectionreadunreadnotification {

		public AuthenticateConnectionreadunreadnotification() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = checkurl.GetServiceUrl()
					+ "/FetchUnreadNotificationCount.php";

			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
					"MobileNumber", MobileNumber);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MobileNumberBasicNameValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			Log.e("httpResponse", "" + httpResponse);

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				readunreadnotiresp = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.e("readunreadnotiresp", "" + readunreadnotiresp);
		}
	}

	// ////////////////////////

	// ///////
	private class fetchtopthreerides extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionfetchtopthreerides mAuth1 = new AuthenticateConnectionfetchtopthreerides();
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
				Toast.makeText(Invite.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			// //////////////////////////
			if (activeridesresp.equalsIgnoreCase("No Pool Created Yet!!")
					|| activeridesresp.equalsIgnoreCase("[]")) {

				new ConnectionTaskForShowRidesHistory().executeOnExecutor(
						AsyncTask.THREAD_POOL_EXECUTOR, "");
			} else {

				try {
					JSONArray subArray = new JSONArray(activeridesresp);
					for (int i = 0; i < subArray.length(); i++) {
						FromLocation.add(subArray.getJSONObject(i)
								.getString("FromLocation").toString().trim());
						ToLocation.add(subArray.getJSONObject(i)
								.getString("ToLocation").toString().trim());
						FromShortName.add(subArray.getJSONObject(i)
								.getString("FromShortName").toString().trim());
						ToShortName.add(subArray.getJSONObject(i)
								.getString("ToShortName").toString().trim());
						Seats.add(subArray.getJSONObject(i).getString("Seats")
								.toString().trim());

						TravelDate.add(subArray.getJSONObject(i)
								.getString("TravelDate").toString().trim());
						TravelTime.add(subArray.getJSONObject(i)
								.getString("TravelTime").toString().trim());
						Seat_Status.add(subArray.getJSONObject(i)
								.getString("Seat_Status").toString().trim());
					}

					if (FromLocation.size() >= 3) {

						inviteloadingll.setVisibility(View.GONE);
						topthreeridesll.setVisibility(View.VISIBLE);

						topthreeadaptor = new TopThreeRidesAdaptor(Invite.this,
								FromShortName, ToShortName, TravelDate,
								TravelTime, Seat_Status);
						topthreerideslist.setAdapter(topthreeadaptor);
						Helper.getListViewSize(topthreerideslist);

						topthreerideslist
								.setOnItemClickListener(new OnItemClickListener() {

									@Override
									public void onItemClick(
											AdapterView<?> parent, View view,
											int position, long id) {
										// TODO Auto-generated method stub

										from_places.setText(FromLocation
												.get(position).toString()
												.trim());
										to_places.setText(ToLocation
												.get(position).toString()
												.trim());
										dateedittext.setText("");
										timeedittext.setText("");
										seatsedittext.setText(Seats
												.get(position).toString()
												.trim());

										fromshortname = FromShortName
												.get(position).toString()
												.trim();

										toshortname = ToShortName.get(position)
												.toString().trim();
									}
								});

					} else {

						new ConnectionTaskForShowRidesHistory()
								.executeOnExecutor(
										AsyncTask.THREAD_POOL_EXECUTOR, "");
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}

	public class AuthenticateConnectionfetchtopthreerides {

		public AuthenticateConnectionfetchtopthreerides() {

		}

		public void connection() throws Exception {

			FromLocation.clear();
			ToLocation.clear();
			FromShortName.clear();
			ToShortName.clear();
			Seats.clear();
			TravelDate.clear();
			TravelTime.clear();
			Seat_Status.clear();

			// Connect to google.com
			HttpClient httpClient1 = new DefaultHttpClient();
			String url_select11 = checkurl.GetServiceUrl()
					+ "/FetchMyPools.php";
			HttpPost httpPost1 = new HttpPost(url_select11);
			BasicNameValuePair MobileNumberBasicNameValuePair1 = new BasicNameValuePair(
					"MobileNumber", MobileNumber);

			List<NameValuePair> nameValuePairList1 = new ArrayList<NameValuePair>();
			nameValuePairList1.add(MobileNumberBasicNameValuePair1);

			UrlEncodedFormEntity urlEncodedFormEntity1 = new UrlEncodedFormEntity(
					nameValuePairList1);
			httpPost1.setEntity(urlEncodedFormEntity1);
			HttpResponse httpResponse1 = httpClient1.execute(httpPost1);

			Log.e("httpResponse FetchMyPools", "" + httpResponse1);

			InputStream inputStream1 = httpResponse1.getEntity().getContent();
			InputStreamReader inputStreamReader1 = new InputStreamReader(
					inputStream1);

			BufferedReader bufferedReader1 = new BufferedReader(
					inputStreamReader1);

			StringBuilder stringBuilder1 = new StringBuilder();

			String bufferedStrChunk1 = null;

			while ((bufferedStrChunk1 = bufferedReader1.readLine()) != null) {
				activeridesresp = stringBuilder1.append(bufferedStrChunk1)
						.toString();
			}

			Log.e("activeridesresp", "" + stringBuilder1.toString());

		}
	}

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
				Toast.makeText(Invite.this,
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
			String url_select = checkurl.GetServiceUrl()
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

			Log.e("httpResponse", "" + httpResponse11);

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

			Log.e("imagenameresp", "" + imagenameresp);

			if (imagenameresp == null) {

			} else {
				String url1 = checkurl.GetServiceUrl() + "/ProfileImages/"
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

	// convert from bitmap to byte array
	public byte[] getBytesFromBitmap(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 70, stream);
		return stream.toByteArray();
	}

	// //////////////////////

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

	public String getAddressshort(Context ctx, double latitude, double longitude) {
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

	public String getAddress(Context ctx, double latitude, double longitude) {
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

	@Override
	public void onLocationChanged(Location location) {

		mycurrentlocationobject = location;
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

	ArrayList<String> autocomplete(String input) {
		ArrayList<String> resultList = null;

		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			StringBuilder sb = new StringBuilder(PLACES_API_BASE
					+ TYPE_AUTOCOMPLETE + OUT_JSON);
			sb.append("?sensor=false&key=" + API_KEY);
			sb.append("&components=country:ind");
			sb.append("&input=" + URLEncoder.encode(input, "utf8"));

			URL url = new URL(sb.toString());
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, "Error processing Places API URL", e);
			return resultList;
		} catch (IOException e) {
			Log.e(LOG_TAG, "Error connecting to Places API", e);
			return resultList;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		try {
			// Create a JSON object hierarchy from the results
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

			// Extract the Place descriptions from the results
			resultList = new ArrayList<String>(predsJsonArray.length());
			for (int i = 0; i < predsJsonArray.length(); i++) {
				resultList.add(predsJsonArray.getJSONObject(i).getString(
						"description"));
			}
		} catch (JSONException e) {
			Log.e("LOG_TAG", "Cannot process JSON results", e);
		}

		return resultList;
	}

	@Override
	public void onBackPressed() {

		if (!fromrelative.isShown()) {

			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
			Invite.this.finish();
		} else {
			fromrelative.setVisibility(View.GONE);
		}
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

	// ///////

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

	private boolean isVibrate() {
		return false;
	}

	private boolean isCloseOnSingleTapDay() {
		return false;
	}

	private boolean isCloseOnSingleTapMinute() {
		return false;
	}

	@Override
	public void onDateSet(DatePickerDialog datePickerDialog, int year,
			int month, int day) {

		int finalmnt = month + 1;
		String selecteddate = day + "/" + finalmnt + "/" + year;
		dateedittext.setText(selecteddate.toString().trim());
	}

	@Override
	public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
		// Toast.makeText(Invite.this, "new time:" + hourOfDay + "-" + minute,
		// Toast.LENGTH_LONG).show();

		updateTime(hourOfDay, minute);
	}

	private void updateTime(int hours, int mins) {

		String timeSet = "";
		if (hours > 12) {
			hours -= 12;
			timeSet = "PM";
		} else if (hours == 0) {
			hours += 12;
			timeSet = "AM";
		} else if (hours == 12)
			timeSet = "PM";
		else
			timeSet = "AM";

		String minutes = "";
		if (mins < 10)
			minutes = "0" + mins;
		else
			minutes = String.valueOf(mins);

		// Append in a StringBuilder
		String aTime = new StringBuilder().append(hours).append(':')
				.append(minutes).append(" ").append(timeSet).toString();

		timeedittext.setText(aTime);
	}

	// /////////////////////////////
	// ///////

	private class ConnectionTaskForShowRidesHistory extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionShowRidesHistory mAuth1 = new AuthenticateConnectionShowRidesHistory();
			try {
				mAuth1.cid = args[0];
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
				Toast.makeText(Invite.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (archieveridesresp.equalsIgnoreCase("No Pool Created Yet!!")
					|| archieveridesresp.equalsIgnoreCase("[]")) {

				if (FromLocation.size() > 0) {

					inviteloadingll.setVisibility(View.GONE);
					topthreeridesll.setVisibility(View.VISIBLE);

					topthreeadaptor = new TopThreeRidesAdaptor(Invite.this,
							FromShortName, ToShortName, TravelDate, TravelTime,
							Seat_Status);
					topthreerideslist.setAdapter(topthreeadaptor);
					Helper.getListViewSize(topthreerideslist);

					topthreerideslist
							.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {
									// TODO Auto-generated method stub

									from_places.setText(FromLocation
											.get(position).toString().trim());
									to_places.setText(ToLocation.get(position)
											.toString().trim());
									dateedittext.setText("");
									timeedittext.setText("");
									seatsedittext.setText(Seats.get(position)
											.toString().trim());

									fromshortname = FromShortName.get(position)
											.toString().trim();

									toshortname = ToShortName.get(position)
											.toString().trim();
								}
							});

				} else {
					inviteloadingll.setVisibility(View.GONE);
					topthreeridesll.setVisibility(View.GONE);
				}

			} else {

				try {
					JSONArray subArray = new JSONArray(archieveridesresp);
					for (int i = 0; i < subArray.length(); i++) {

						FromLocation.add(subArray.getJSONObject(i)
								.getString("FromLocation").toString().trim());
						ToLocation.add(subArray.getJSONObject(i)
								.getString("ToLocation").toString().trim());
						FromShortName.add(subArray.getJSONObject(i)
								.getString("FromShortName").toString().trim());
						ToShortName.add(subArray.getJSONObject(i)
								.getString("ToShortName").toString().trim());
						TravelDate.add(subArray.getJSONObject(i)
								.getString("TravelDate").toString().trim());
						TravelTime.add(subArray.getJSONObject(i)
								.getString("TravelTime").toString().trim());
						Seats.add(subArray.getJSONObject(i).getString("Seats")
								.toString().trim());
						Seat_Status.add(subArray.getJSONObject(i)
								.getString("Seat_Status").toString().trim());
					}

					if (FromLocation.size() >= 3) {

						inviteloadingll.setVisibility(View.GONE);
						topthreeridesll.setVisibility(View.VISIBLE);

						FromLocationNew.clear();
						ToLocationNew.clear();
						FromShortNameNew.clear();
						ToShortNameNew.clear();
						SeatsNew.clear();
						TravelDateNew.clear();
						TravelTimeNew.clear();
						Seat_StatusNew.clear();

						for (int i = 0; i < 3; i++) {
							FromLocationNew.add(FromLocation.get(i).toString()
									.trim());
							ToLocationNew.add(ToLocation.get(i).toString()
									.trim());
							FromShortNameNew.add(FromShortName.get(i)
									.toString().trim());
							ToShortNameNew.add(ToShortName.get(i).toString()
									.trim());
							SeatsNew.add(Seats.get(i).toString().trim());
							TravelDateNew.add(TravelDate.get(i).toString()
									.trim());
							TravelTimeNew.add(TravelTime.get(i).toString()
									.trim());
							Seat_StatusNew.add(Seat_Status.get(i).toString()
									.trim());
						}

						topthreeadaptor = new TopThreeRidesAdaptor(Invite.this,
								FromShortNameNew, ToShortNameNew,
								TravelDateNew, TravelTimeNew, Seat_StatusNew);
						topthreerideslist.setAdapter(topthreeadaptor);
						Helper.getListViewSize(topthreerideslist);

						topthreerideslist
								.setOnItemClickListener(new OnItemClickListener() {

									@Override
									public void onItemClick(
											AdapterView<?> parent, View view,
											int position, long id) {
										// TODO Auto-generated method stub

										from_places.setText(FromLocationNew
												.get(position).toString()
												.trim());
										to_places.setText(ToLocationNew
												.get(position).toString()
												.trim());
										dateedittext.setText("");
										timeedittext.setText("");
										seatsedittext.setText(SeatsNew
												.get(position).toString()
												.trim());

										fromshortname = FromShortNameNew
												.get(position).toString()
												.trim();

										toshortname = ToShortNameNew
												.get(position).toString()
												.trim();
									}
								});

					} else {

						inviteloadingll.setVisibility(View.GONE);
						topthreeridesll.setVisibility(View.VISIBLE);

						FromLocationNew.clear();
						ToLocationNew.clear();
						FromShortNameNew.clear();
						ToShortNameNew.clear();
						SeatsNew.clear();
						TravelDateNew.clear();
						TravelTimeNew.clear();
						Seat_StatusNew.clear();

						for (int i = 0; i < FromLocation.size(); i++) {
							FromLocationNew.add(FromLocation.get(i).toString()
									.trim());
							ToLocationNew.add(ToLocation.get(i).toString()
									.trim());
							FromShortNameNew.add(FromShortName.get(i)
									.toString().trim());
							ToShortNameNew.add(ToShortName.get(i).toString()
									.trim());
							SeatsNew.add(Seats.get(i).toString().trim());
							TravelDateNew.add(TravelDate.get(i).toString()
									.trim());
							TravelTimeNew.add(TravelTime.get(i).toString()
									.trim());
							Seat_StatusNew.add(Seat_Status.get(i).toString()
									.trim());
						}

						topthreeadaptor = new TopThreeRidesAdaptor(Invite.this,
								FromShortNameNew, ToShortNameNew,
								TravelDateNew, TravelTimeNew, Seat_StatusNew);
						topthreerideslist.setAdapter(topthreeadaptor);
						Helper.getListViewSize(topthreerideslist);

						topthreerideslist
								.setOnItemClickListener(new OnItemClickListener() {

									@Override
									public void onItemClick(
											AdapterView<?> parent, View view,
											int position, long id) {
										// TODO Auto-generated method stub

										from_places.setText(FromLocationNew
												.get(position).toString()
												.trim());
										to_places.setText(ToLocationNew
												.get(position).toString()
												.trim());
										dateedittext.setText("");
										timeedittext.setText("");
										seatsedittext.setText(SeatsNew
												.get(position).toString()
												.trim());

										fromshortname = FromShortNameNew
												.get(position).toString()
												.trim();

										toshortname = ToShortNameNew
												.get(position).toString()
												.trim();
									}
								});

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}

	public class AuthenticateConnectionShowRidesHistory {

		public String cid;

		public AuthenticateConnectionShowRidesHistory() {

		}

		@SuppressWarnings("deprecation")
		public void connection() throws Exception {

			HttpClient httpclient = new DefaultHttpClient();
			String url_select11 = checkurl.GetServiceUrl()
					+ "/fetchmypoolshistory.php";
			HttpPost httpPost = new HttpPost(url_select11);

			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
					"MobileNumber", MobileNumber);
			BasicNameValuePair CabIdBasicNameValuePair = new BasicNameValuePair(
					"LastCabId", cid.toString().trim());

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MobileNumberBasicNameValuePair);
			nameValuePairList.add(CabIdBasicNameValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);

			Log.e("url_select11", "" + url_select11);
			HttpResponse httpResponse = httpclient.execute(httpPost);

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				archieveridesresp = stringBuilder.append(bufferedStrChunk)
						.toString();
			}
		}
	}

	// //////////////////////////
}
