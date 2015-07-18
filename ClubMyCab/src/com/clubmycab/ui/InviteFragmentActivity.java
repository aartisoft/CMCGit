package com.clubmycab.ui;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

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
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

import com.clubmycab.CircularImageView;
import com.clubmycab.Helper;
import com.clubmycab.PlacesAutoCompleteAdapter;
import com.clubmycab.R;
import com.clubmycab.TopThreeRidesAdaptor;
import com.clubmycab.model.AddressModel;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.navdrawer.SimpleSideDrawer;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

public class InviteFragmentActivity extends FragmentActivity implements
		OnDateSetListener, TimePickerDialog.OnTimeSetListener {

	TextView textFrom;
	TextView textTo;
	TextView date;
	TextView time;
	TextView seats;
	private String seatCount="3";
	private String seatSuffix=" seat(s) to share";

	AutoCompleteTextView from_places;
	AutoCompleteTextView to_places;
private 	TextView datetextview;
private	TextView timetextview;
	Button seatsbutton;

private	LinearLayout inviteLl;
private TextView  datechoose, timehalfhour,timeonehour;// timechoose,datetoday;

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
	
	private int hour,minute;
private TimePickerDialog timePickerDialog;
private TextView tvNext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invite);
		

		// Check if Internet present
		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					InviteFragmentActivity.this);
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
		tvNext=(TextView)findViewById(R.id.tvNext1);
		inviteloadingll = (LinearLayout) findViewById(R.id.inviteloadingll);
		inviteloadingll.setVisibility(View.GONE);

		topthreeridesll = (LinearLayout) findViewById(R.id.topthreeridesll);
		topthreeridesll.setVisibility(View.GONE);

		topthreerideslist = (ListView) findViewById(R.id.topthreerideslist);

		LinearLayout topthreeridesll = (LinearLayout) findViewById(R.id.topthreeridesll);
		topthreeridesll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				topthreerideslist.setVisibility(View.VISIBLE);
			}
		});

		GoogleAnalytics analytics = GoogleAnalytics
				.getInstance(InviteFragmentActivity.this);
		tracker = analytics
				.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);

		// All subsequent hits will be send with screen name = "main screen"
		tracker.setScreenName("Invitation Details");

		inviterl = (RelativeLayout) findViewById(R.id.inviterl);
		inviterl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Log.d("inviterl", "inviterl");
			}
		});

		flagchk = true;

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
		// Intent mainIntent = new Intent(InviteFragmentActivity.this,
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
		// Intent mainIntent = new Intent(InviteFragmentActivity.this,
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
		// Intent mainIntent = new Intent(InviteFragmentActivity.this,
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
		//
		// tracker.send(new HitBuilders.EventBuilder()
		// .setCategory("ShareLocation Click")
		// .setAction("ShareLocation Click")
		// .setLabel("ShareLocation Click").build());
		//
		// Intent mainIntent = new Intent(InviteFragmentActivity.this,
		// ShareLocationFragmentActivity.class);
		// startActivityForResult(mainIntent, 500);
		// overridePendingTransition(R.anim.slide_in_right,
		// R.anim.slide_out_left);
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
		// Intent mainIntent = new Intent(InviteFragmentActivity.this,
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
		// Intent mainIntent = new Intent(InviteFragmentActivity.this,
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
		// Intent mainIntent = new Intent(InviteFragmentActivity.this,
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

				Intent mainIntent = new Intent(InviteFragmentActivity.this,
						NotificationListActivity.class);
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

		unreadnoticountrl = (RelativeLayout) findViewById(R.id.unreadnoticountrl);
		unreadnoticount = (TextView) findViewById(R.id.unreadnoticount);

		if (GlobalVariables.UnreadNotificationCount.equalsIgnoreCase("0")) {

			unreadnoticountrl.setVisibility(View.GONE);

		} else {

			unreadnoticountrl.setVisibility(View.VISIBLE);
			unreadnoticount.setText(GlobalVariables.UnreadNotificationCount);
		}

		textFrom = (TextView) findViewById(R.id.textFrom);
		textTo = (TextView) findViewById(R.id.textTo);
		date = (TextView) findViewById(R.id.date);
		time = (TextView) findViewById(R.id.time);
		seats = (TextView) findViewById(R.id.seats);

		from_places = (AutoCompleteTextView) findViewById(R.id.from_places);
		to_places = (AutoCompleteTextView) findViewById(R.id.to_places);
		datetextview = (TextView) findViewById(R.id.dateetextview);
		timetextview = (TextView) findViewById(R.id.timetextview);
		seatsbutton = (Button) findViewById(R.id.seatsbutton);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		datetextview.setText(simpleDateFormat.format(new Date()));

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

		inviteLl = (LinearLayout) findViewById(R.id.inviteLl);

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
		datetextview.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

		time.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		timetextview.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

		seats.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
//	pawan	seatsedittext.setTypeface(Typeface.createFromAsset(getAssets(),
//				"NeutraText-Light.ttf"));

		tvNext.setTypeface(Typeface.createFromAsset(getAssets(),
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

		final Calendar calendar = Calendar.getInstance();

		final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
				InviteFragmentActivity.this, calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH), isVibrate());

		calendar.add(Calendar.MINUTE, 30);
		hour = calendar.get(Calendar.HOUR_OF_DAY);
	 minute = calendar.get(Calendar.MINUTE);

	 timePickerDialog = TimePickerDialog.newInstance(
				InviteFragmentActivity.this, hour, minute, false, false);
		TimePickerDialog tpd = (TimePickerDialog) getSupportFragmentManager()
				.findFragmentByTag(TIMEPICKER_TAG);

		if (savedInstanceState != null) {
			DatePickerDialog dpd = (DatePickerDialog) getSupportFragmentManager()
					.findFragmentByTag(DATEPICKER_TAG);
			if (dpd != null) {
				dpd.setOnDateSetListener(InviteFragmentActivity.this);
			}

//			TimePickerDialog tpd = (TimePickerDialog) getSupportFragmentManager()
//					.findFragmentByTag(TIMEPICKER_TAG);
			if (tpd != null) {
				tpd.setOnTimeSetListener(InviteFragmentActivity.this);
			}
		}

		timeonehour = (TextView) findViewById(R.id.timeonehour);
		timeonehour.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				timeonehour.setBackgroundColor(getResources().getColor(
						R.color.color_dark_blue));
				timeonehour.setTextColor(getResources().getColor(
						R.color.white));
				
				
				datechoose.setBackgroundColor(getResources().getColor(
						R.color.white));
				timehalfhour.setBackgroundColor(getResources().getColor(
						R.color.white));
				timehalfhour.setTextColor(getResources().getColor(
						R.color.black));
				datechoose.setTextColor(getResources().getColor(
								R.color.black));
				
				

			Calendar calendar = Calendar.getInstance();

				

				calendar.add(Calendar.HOUR, 1);
				hour = calendar.get(Calendar.HOUR_OF_DAY);
			 minute = calendar.get(Calendar.MINUTE);
			updateTime(hour, minute);
			}
		});

		datechoose = (TextView) findViewById(R.id.datechoose);
		
		datechoose.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				datechoose.setBackgroundColor(getResources().getColor(
						R.color.color_dark_blue));
				datechoose.setTextColor(getResources().getColor(
						R.color.white));
				
				timeonehour.setBackgroundColor(getResources().getColor(
						R.color.white));
				timehalfhour.setBackgroundColor(getResources().getColor(
						R.color.white));
				
				timehalfhour.setTextColor(getResources().getColor(
						R.color.black));
				timeonehour.setTextColor(getResources().getColor(
								R.color.black));

				datePickerDialog.setVibrate(isVibrate());
				datePickerDialog.setYearRange(1985, 2028);
				datePickerDialog
						.setCloseOnSingleTapDay(isCloseOnSingleTapDay());
				datePickerDialog.show(getSupportFragmentManager(),
						DATEPICKER_TAG);
			}
		});
		
		//Added pawan

		timehalfhour = (TextView) findViewById(R.id.timehalfhour);
		updateTime(hour, minute);
		timehalfhour.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				timehalfhour.setBackgroundColor(getResources().getColor(
						R.color.color_dark_blue));
				timehalfhour.setTextColor(getResources().getColor(
						R.color.white));
				timeonehour.setBackgroundColor(getResources().getColor(
						R.color.white));
				datechoose.setBackgroundColor(getResources().getColor(
						R.color.white));
				
				
				datechoose.setTextColor(getResources().getColor(
						R.color.black));
				timeonehour.setTextColor(getResources().getColor(
								R.color.black));
	Calendar calendar = Calendar.getInstance();

				

				calendar.add(Calendar.MINUTE, 30);
				hour = calendar.get(Calendar.HOUR_OF_DAY);
			 minute = calendar.get(Calendar.MINUTE);
			updateTime(hour, minute);
				updateTime(hour, minute);

			}
		});
		
//		timehalfhour = (Button) findViewById(R.id.timehalfhour);
//		updateTime(hour, minute);
//		timehalfhour.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				timehalfhour.setBackgroundColor(getResources().getColor(
//						R.color.color_dark_blue));
////		pawan		timechoose.setBackgroundColor(getResources().getColor(
////						R.color.color_light_blue));
//
//				updateTime(hour, minute);
//
//			}
//		});
//		
		
		//Commented for time choose marge with  choose date  
		//Pawan

//		timechoose = (Button) findViewById(R.id.timechoose);
//		timechoose.setBackgroundColor(getResources().getColor(
//				R.color.color_light_blue));
//		timechoose.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//
//				timehalfhour.setBackgroundColor(getResources().getColor(
//						R.color.color_light_blue));
//				timechoose.setBackgroundColor(getResources().getColor(
//						R.color.color_dark_blue));
//
//				timePickerDialog.setVibrate(isVibrate());
//				timePickerDialog
//						.setCloseOnSingleTapMinute(isCloseOnSingleTapMinute());
//				timePickerDialog.show(getSupportFragmentManager(),
//						TIMEPICKER_TAG);
//			}
//		});

		//End Pawan
		
		
		// dateedittext.setOnClickListener(new OnClickListener() {
		//
		// @SuppressWarnings("deprecation")
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		//
		// datePickerDialog.setVibrate(isVibrate());
		// datePickerDialog.setYearRange(1985, 2028);
		// datePickerDialog
		// .setCloseOnSingleTapDay(isCloseOnSingleTapDay());
		// datePickerDialog.show(getSupportFragmentManager(),
		// DATEPICKER_TAG);
		// }
		// });
		//
		// dateedittext.setOnFocusChangeListener(new
		// View.OnFocusChangeListener() {
		//
		// @SuppressWarnings("deprecation")
		// @Override
		// public void onFocusChange(View v, boolean hasFocus) {
		// if (hasFocus) {
		//
		// datePickerDialog.setVibrate(isVibrate());
		// datePickerDialog.setYearRange(1985, 2028);
		// datePickerDialog
		// .setCloseOnSingleTapDay(isCloseOnSingleTapDay());
		// datePickerDialog.show(getSupportFragmentManager(),
		// DATEPICKER_TAG);
		// }
		// }
		//
		// });

		// time selection
		// timeedittext.setOnClickListener(new OnClickListener() {
		//
		// @SuppressWarnings("deprecation")
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		//
		// timePickerDialog.setVibrate(isVibrate());
		// timePickerDialog
		// .setCloseOnSingleTapMinute(isCloseOnSingleTapMinute());
		// timePickerDialog.show(getSupportFragmentManager(),
		// TIMEPICKER_TAG);
		// }
		// });
		//
		// timeedittext.setOnFocusChangeListener(new
		// View.OnFocusChangeListener() {
		//
		// @SuppressWarnings("deprecation")
		// @Override
		// public void onFocusChange(View v, boolean hasFocus) {
		// if (hasFocus) {
		//
		// timePickerDialog.setVibrate(isVibrate());
		// timePickerDialog
		// .setCloseOnSingleTapMinute(isCloseOnSingleTapMinute());
		// timePickerDialog.show(getSupportFragmentManager(),
		// TIMEPICKER_TAG);
		// }
		// }
		//
		// });

		seatsbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				AlertDialog dialog;
				AlertDialog.Builder builder = new AlertDialog.Builder(
						InviteFragmentActivity.this);
				builder.setTitle("Select No of Seats");

				final CharSequence str[] = { "1", "2", "3", "4", "5", "6" };

				builder.setItems(str, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int position) {
						// TODO Auto-generated method stub
						seats.setText(str[position]+seatSuffix);
						seatCount=""+str[position];
						
					}
				});

				dialog = builder.create();
				dialog.show();

			}
		});

//	Pawan	seatsedittext
//				.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//
//					@Override
//					public void onFocusChange(View v, boolean hasFocus) {
//						if (hasFocus) {
//
//							// TODO Auto-generated method stub
//							AlertDialog dialog;
//							AlertDialog.Builder builder = new AlertDialog.Builder(
//									InviteFragmentActivity.this);
//							builder.setTitle("Select No of Seats");
//
//							final CharSequence str[] = { "1", "2", "3", "4",
//									"5", "6" };
//
//							builder.setItems(str,
//									new DialogInterface.OnClickListener() {
//
//										@Override
//										public void onClick(
//												DialogInterface dialog,
//												int position) {
//											// TODO Auto-generated method stub
//											seatsedittext
//													.setText(str[position]);
//										}
//									});
//
//							dialog = builder.create();
//							dialog.show();
//
//						}
//					}
//
//				});

		inviteLl.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Animation animScale = AnimationUtils.loadAnimation(
						InviteFragmentActivity.this, R.anim.button_click_anim);
				inviteLl.startAnimation(animScale);

				Handler mHandler2 = new Handler();
				Runnable mRunnable2 = new Runnable() {
					@Override
					public void run() {

						if (fAddress == null) {

							from_places.requestFocus();

							AlertDialog.Builder builder = new AlertDialog.Builder(
									InviteFragmentActivity.this);

							builder.setMessage("Please Enter From Location. If you have already selected a location on map please try again by selecting a nearby location");
							builder.setPositiveButton("OK", null);
							AlertDialog dialog = builder.show();
							TextView messageText = (TextView) dialog
									.findViewById(android.R.id.message);
							messageText.setGravity(Gravity.CENTER);
							dialog.show();

						} else if (tAddress == null) {

							to_places.requestFocus();

							AlertDialog.Builder builder = new AlertDialog.Builder(
									InviteFragmentActivity.this);

							builder.setMessage("Please Enter To Location. If you have already selected a location on map please try again by selecting a nearby location");
							builder.setPositiveButton("OK", null);
							AlertDialog dialog = builder.show();
							TextView messageText = (TextView) dialog
									.findViewById(android.R.id.message);
							messageText.setGravity(Gravity.CENTER);
							dialog.show();

						} else if (datetextview.getText().toString().trim()
								.isEmpty()) {

							datetextview.requestFocus();

							AlertDialog.Builder builder = new AlertDialog.Builder(
									InviteFragmentActivity.this);

							builder.setMessage("Please Enter Date");
							builder.setPositiveButton("OK", null);
							AlertDialog dialog = builder.show();
							TextView messageText = (TextView) dialog
									.findViewById(android.R.id.message);
							messageText.setGravity(Gravity.CENTER);
							dialog.show();

						}

						else if (timetextview.getText().toString().trim()
								.isEmpty()) {

						//	timetextview.requestFocus();

							AlertDialog.Builder builder = new AlertDialog.Builder(
									InviteFragmentActivity.this);

							builder.setMessage("Please Enter Time");
							builder.setPositiveButton("OK", null);
							AlertDialog dialog = builder.show();
							TextView messageText = (TextView) dialog
									.findViewById(android.R.id.message);
							messageText.setGravity(Gravity.CENTER);
							dialog.show();

					}
						//Commit becuase seatsEdittext change with button
							//else if (seatsedittext.getText().toString().trim()
//								.isEmpty()) {
//
//							seatsedittext.requestFocus();
//
//							AlertDialog.Builder builder = new AlertDialog.Builder(
//									InviteFragmentActivity.this);
//
//							builder.setMessage("Please Enter Seats Available");
//							builder.setPositiveButton("OK", null);
//							AlertDialog dialog = builder.show();
//							TextView messageText = (TextView) dialog
//									.findViewById(android.R.id.message);
//							messageText.setGravity(Gravity.CENTER);
//							dialog.show();
//
//						} 
							else if (!datetextview.getText().toString().trim()
								.isEmpty()
								&& !timetextview.getText().toString().trim()
										.isEmpty()) {

							SimpleDateFormat dateFormat = new SimpleDateFormat(
									"dd/MM/yyyy hh:mm aa");

							Date currentTime = new Date();
							Date rideTime = new Date();
							try {
								rideTime = dateFormat.parse(datetextview
										.getText().toString().trim()
										+ " "
										+ timetextview.getText().toString()
												.trim());
							} catch (Exception e) {
								e.printStackTrace();
							}

							Log.d("InviteFragmentActivity",
									"Invite click currentTime : " + currentTime
											+ " rideTime : " + rideTime);

							if (rideTime.compareTo(currentTime) < 0) {
								timetextview.requestFocus();

								AlertDialog.Builder builder = new AlertDialog.Builder(
										InviteFragmentActivity.this);

								builder.setMessage("The time entered is before the current time");
								builder.setPositiveButton("OK", null);
								AlertDialog dialog = builder.show();
								TextView messageText = (TextView) dialog
										.findViewById(android.R.id.message);
								messageText.setGravity(Gravity.CENTER);
								dialog.show();
							} else {

								Log.d("fromshortname", "" + fromshortname);
								Log.d("toshortname", "" + toshortname);

								CabId = MobileNumber
										+ System.currentTimeMillis();
								String OwnerName = FullName;

								Intent mainIntent = new Intent(
										InviteFragmentActivity.this,
										ContactsInviteForRideActivity.class);
								mainIntent.putExtra("fromcome", "invite");
								mainIntent.putExtra("CabId", CabId);
								mainIntent.putExtra("MobileNumber",
										MobileNumber);
								mainIntent.putExtra("OwnerName", OwnerName);
								mainIntent.putExtra("FromLocation", from_places
										.getText().toString().trim());
								mainIntent.putExtra("ToLocation", to_places
										.getText().toString().trim());
								mainIntent.putExtra("TravelDate", datetextview
										.getText().toString().trim());
								mainIntent.putExtra("TravelTime", timetextview
										.getText().toString().trim());
								mainIntent.putExtra("Seats", seatCount
										);
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
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		// new fetchtopthreerides()
		// .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		// } else {
		// new fetchtopthreerides().execute();
		// }

		// ///////////////
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		// new ConnectionTaskForreadunreadnotification()
		// .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		// } else {
		// new ConnectionTaskForreadunreadnotification().execute();
		// }

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

		Intent fromToIntent = getIntent();
		String startString = fromToIntent.getStringExtra("StartAddressModel");
		String endString = fromToIntent.getStringExtra("EndAddressModel");

		if (startString != null && endString != null) {

			Log.d("InviteFragment", "StartAddressModel : " + startString
					+ " EndAddressModel : " + endString);

			Gson gson = new Gson();
			AddressModel startAddressModel = (AddressModel) gson.fromJson(
					startString, AddressModel.class);
			fAddress = startAddressModel.getAddress();
			fromshortname = startAddressModel.getShortname();
			from_places.setText(startAddressModel.getLongname());

			AddressModel endAddressModel = (AddressModel) gson.fromJson(
					endString, AddressModel.class);
			tAddress = endAddressModel.getAddress();
			toshortname = endAddressModel.getShortname();
			to_places.setText(endAddressModel.getLongname());

			from_places.setEnabled(false);
			to_places.setEnabled(false);
			threedotsfrom.setEnabled(false);
			threedotsto.setEnabled(false);
			clearedittextimgfrom.setVisibility(View.GONE);
			clearedittextimgto.setVisibility(View.GONE);

			// String[] RowData = fromToIntent.getStringExtra("StartAddLatLng")
			// .toString().split(",");
			//
			// Double startLat = Double.parseDouble(RowData[0]);
			// Double startLng = Double.parseDouble(RowData[1]);
			//
			// String address = MapUtilityMethods.getAddress(
			// InviteFragmentActivity.this, startLat.doubleValue(),
			// startLng.doubleValue());
			// from_places.setText(address);
			// fAddress = geocodeAddress(address);
			//
			// RowData = fromToIntent.getStringExtra("EndAddLatLng").toString()
			// .split(",");
			//
			// Double endLat = Double.parseDouble(RowData[0]);
			// Double endLng = Double.parseDouble(RowData[1]);
			//
			// address =
			// MapUtilityMethods.getAddress(InviteFragmentActivity.this,
			// endLat.doubleValue(), endLng.doubleValue());
			// to_places.setText(address);
			// tAddress = geocodeAddress(address);
			//

		}
	}

	// ///////
	// private class ConnectionTaskForreadunreadnotification extends
	// AsyncTask<String, Void, Void> {
	//
	// @Override
	// protected void onPreExecute() {
	//
	// }
	//
	// @Override
	// protected Void doInBackground(String... args) {
	// AuthenticateConnectionreadunreadnotification mAuth1 = new
	// AuthenticateConnectionreadunreadnotification();
	// try {
	// mAuth1.connection();
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	//
	// exceptioncheck = true;
	// e.printStackTrace();
	// }
	// return null;
	// }
	//
	// @Override
	// protected void onPostExecute(Void v) {
	//
	// if (exceptioncheck) {
	// exceptioncheck = false;
	// Toast.makeText(InviteFragmentActivity.this,
	// getResources().getString(R.string.exceptionstring),
	// Toast.LENGTH_LONG).show();
	// return;
	// }
	//
	// if (readunreadnotiresp.equalsIgnoreCase("0")) {
	//
	// unreadnoticountrl.setVisibility(View.GONE);
	//
	// } else {
	//
	// unreadnoticountrl.setVisibility(View.VISIBLE);
	// unreadnoticount.setText(readunreadnotiresp);
	// }
	// }
	//
	// }
	//
	// public class AuthenticateConnectionreadunreadnotification {
	//
	// public AuthenticateConnectionreadunreadnotification() {
	//
	// }
	//
	// public void connection() throws Exception {
	//
	// // Connect to google.com
	// HttpClient httpClient = new DefaultHttpClient();
	// String url_select = GlobalVariables.ServiceUrl
	// + "/FetchUnreadNotificationCount.php";
	//
	// HttpPost httpPost = new HttpPost(url_select);
	// BasicNameValuePair MobileNumberBasicNameValuePair = new
	// BasicNameValuePair(
	// "MobileNumber", MobileNumber);
	//
	// List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
	// nameValuePairList.add(MobileNumberBasicNameValuePair);
	//
	// UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
	// nameValuePairList);
	// httpPost.setEntity(urlEncodedFormEntity);
	// HttpResponse httpResponse = httpClient.execute(httpPost);
	//
	// Log.d("httpResponse", "" + httpResponse);
	//
	// InputStream inputStream = httpResponse.getEntity().getContent();
	// InputStreamReader inputStreamReader = new InputStreamReader(
	// inputStream);
	//
	// BufferedReader bufferedReader = new BufferedReader(
	// inputStreamReader);
	//
	// StringBuilder stringBuilder = new StringBuilder();
	//
	// String bufferedStrChunk = null;
	//
	// while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
	// readunreadnotiresp = stringBuilder.append(bufferedStrChunk)
	// .toString();
	// }
	//
	// Log.d("readunreadnotiresp", "" + readunreadnotiresp);
	// }
	// }

	// ////////////////////////

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
				Toast.makeText(InviteFragmentActivity.this,
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

	// convert from bitmap to byte array
	public byte[] getBytesFromBitmap(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 70, stream);
		return stream.toByteArray();
	}

	// //////////////////////

	@Override
	public void onBackPressed() {

		if (!fromrelative.isShown()) {

			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
			InviteFragmentActivity.this.finish();
		} else {
			fromrelative.setVisibility(View.GONE);
		}
	}

	// ///////

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
		datetextview.setText(selecteddate.toString().trim());
		timePickerDialog.setVibrate(isVibrate());
		timePickerDialog
				.setCloseOnSingleTapMinute(isCloseOnSingleTapMinute());
		timePickerDialog.show(getSupportFragmentManager(),
				TIMEPICKER_TAG);
	}

	@Override
	public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
		// Toast.makeText(Invite.this, "new time:" + hourOfDay + "-" + minute,
		// Toast.LENGTH_LONG).show();
		hour=hourOfDay;
		this.minute=minute;

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

		timetextview.setText(aTime);
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
				Toast.makeText(InviteFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (archieveridesresp.equalsIgnoreCase("No Pool Created Yet!!")
					|| archieveridesresp.equalsIgnoreCase("[]")) {

				if (FromLocation.size() > 0) {

					inviteloadingll.setVisibility(View.GONE);
					topthreeridesll.setVisibility(View.GONE);

					topthreeadaptor = new TopThreeRidesAdaptor(
							InviteFragmentActivity.this, FromShortName,
							ToShortName, TravelDate, TravelTime, Seat_Status);
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
									datetextview.setText("");
									timetextview.setText("");
									seats.setText(Seats.get(position)
											.toString().trim()+seatSuffix);

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
						topthreeridesll.setVisibility(View.GONE);

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

						topthreeadaptor = new TopThreeRidesAdaptor(
								InviteFragmentActivity.this, FromShortNameNew,
								ToShortNameNew, TravelDateNew, TravelTimeNew,
								Seat_StatusNew);
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
										datetextview.setText("");
										timetextview.setText("");
										seats.setText(SeatsNew
												.get(position).toString()
												.trim()+seatSuffix);

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
						topthreeridesll.setVisibility(View.GONE);

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

						topthreeadaptor = new TopThreeRidesAdaptor(
								InviteFragmentActivity.this, FromShortNameNew,
								ToShortNameNew, TravelDateNew, TravelTimeNew,
								Seat_StatusNew);
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
										datetextview.setText("");
										timetextview.setText("");
										seats.setText(SeatsNew
												.get(position).toString()
												.trim()+seatSuffix);

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
			String url_select11 = GlobalVariables.ServiceUrl
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

			Log.d("url_select11", "" + url_select11);
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
