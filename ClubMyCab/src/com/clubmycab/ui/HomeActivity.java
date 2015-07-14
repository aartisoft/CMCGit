package com.clubmycab.ui;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.BookaCabFragmentActivity;
import com.clubmycab.CircularImageView;
import com.clubmycab.PlacesAutoCompleteAdapter;
import com.clubmycab.R;
import com.clubmycab.asynctasks.GlobalAsyncTask;
import com.clubmycab.asynctasks.GlobalAsyncTask.AsyncTaskResultListener;
import com.clubmycab.maps.MapUtilityMethods;
import com.clubmycab.model.AddressModel;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.clubmycab.xmlhandler.FetchUnreadNotificationCountHandler;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.navdrawer.SimpleSideDrawer;

public class HomeActivity extends FragmentActivity implements
		AsyncTaskResultListener, LocationListener {

	CircularImageView profilepic;
	TextView username;
	ImageView notificationimg;

	LinearLayout homeclubmycabll;
	LinearLayout homebookacabll;
	// LinearLayout homehereiamll;
	LinearLayout homebtnsll, officetohomell, hometoofficell;

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

	AutoCompleteTextView from_places;
	AutoCompleteTextView to_places;
	Button threedotsfrom;
	Button threedotsto;
	ImageView clearedittextimgfrom;
	ImageView clearedittextimgto;
	RelativeLayout fromrelative;
	RelativeLayout contentrelativehomepage;
	TextView fromlocation;
	Button fromdone;
	Button cancel;
	private GoogleMap myMap;
	String whichdotclick;
	LocationManager locationManager;
	Location mycurrentlocationobject;

	Address fAddress, tAddress;

	String FullName;
	String MobileNumber;

	Bitmap mainbmp = null;

	String imageuploadresp;

	String readunreadnotiresp;
	String imagenameresp;
	String myclubsresp;

	private static long back_pressed;

	RelativeLayout unreadnoticountrl;
	TextView unreadnoticount;

	Bitmap mIcon11 = null;

	RelativeLayout homepagerl;

	String imageuploadchkstr;

	Tracker tracker;

	AppEventsLogger logger;
	boolean exceptioncheck = false;

	// String StartAddLatLngIntent;
	// String EndAddLatLngIntent;
	// String StartAddShortNameIntent;
	// String EndAddShortNameIntent;

	AddressModel addressModelFrom, addressModelTo, home, work;

	Boolean flagchk;
	String fromshortname;
	String toshortname;
	LatLng invitemapcenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_page);

		SharedPreferences mPrefs1 = getSharedPreferences("QuitApplication", 0);
		boolean shouldQuitApp = mPrefs1.getBoolean("quitapplication", false);
		if (shouldQuitApp) {

			SharedPreferences.Editor editor = mPrefs1.edit();
			editor.putBoolean("quitapplication", false);
			editor.commit();

			finish();
		}

		// Check if Internet present
		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					HomeActivity.this);
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

		// ////////////////////

		GoogleAnalytics analytics = GoogleAnalytics
				.getInstance(HomeActivity.this);
		tracker = analytics
				.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);

		// All subsequent hits will be send with screen name = "main screen"
		tracker.setScreenName("HomePage");

		logger = AppEventsLogger.newLogger(this);

		// tracker.send(new HitBuilders.EventBuilder().setCategory("UX")
		// .setAction("click").setLabel("submit").build());
		//
		// // Builder parameters can overwrite the screen name set on the
		// tracker.
		// tracker.send(new HitBuilders.EventBuilder().setCategory("UX")
		// .setAction("click").setLabel("help popup")
		// .setScreenName("help popup dialog").build());

		// ///////////////////

		homepagerl = (RelativeLayout) findViewById(R.id.homepagerl);
		homepagerl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Log.d("homepagerl", "homepagerl");
			}
		});

		UniversalDrawer drawer = new UniversalDrawer(this, tracker);
		drawer.createDrawer();
		GlobalVariables.ActivityName = "HomeActivity";

		// mNav = new SimpleSideDrawer(this);
		// mNav.setLeftBehindContentView(R.layout.activity_behind_left_simple);
		//
		// findViewById(R.id.sidemenu).setOnClickListener(new OnClickListener()
		// {
		// @Override
		// public void onClick(View v) {
		//
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
		// Intent mainIntent = new Intent(HomeActivity.this,
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
		// Intent mainIntent = new Intent(HomeActivity.this,
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
		// Intent mainIntent = new Intent(HomeActivity.this,
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
		// Intent mainIntent = new Intent(HomeActivity.this,
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
		// Intent mainIntent = new Intent(HomeActivity.this,
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
		// Intent mainIntent = new Intent(HomeActivity.this,
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
		// Intent mainIntent = new Intent(HomeActivity.this,
		// AboutPagerFragmentActivity.class);
		// startActivityForResult(mainIntent, 500);
		// overridePendingTransition(R.anim.slide_in_right,
		// R.anim.slide_out_left);
		// }
		// });

		// homeclubmycabll = (LinearLayout) findViewById(R.id.homeclubmycabll);
		// homebookacabll = (LinearLayout) findViewById(R.id.homebookacabll);
		// homehereiamll = (LinearLayout) findViewById(R.id.homehereiamll);

		profilepic = (CircularImageView) findViewById(R.id.profilepic);
		notificationimg = (ImageView) findViewById(R.id.notificationimg);
		drawerprofilepic = (CircularImageView) findViewById(R.id.drawerprofilepic);

		SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
		FullName = mPrefs.getString("FullName", "");
		MobileNumber = mPrefs.getString("MobileNumber", "");

		username = (TextView) findViewById(R.id.username);
		username.setText(FullName);

		drawerusername = (TextView) findViewById(R.id.drawerusername);
		drawerusername.setText(FullName);

		drawerusername.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));

		unreadnoticountrl = (RelativeLayout) findViewById(R.id.unreadnoticountrl);
		unreadnoticount = (TextView) findViewById(R.id.unreadnoticount);

		// homeclubmycabll.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		//
		// Animation animScale = AnimationUtils.loadAnimation(
		// HomeActivity.this, R.anim.button_click_anim);
		// homeclubmycabll.startAnimation(animScale);
		//
		// Handler mHandler2 = new Handler();
		// Runnable mRunnable2 = new Runnable() {
		// @Override
		// public void run() {
		//
		// tracker.send(new HitBuilders.EventBuilder()
		// .setCategory("ClubMyCab Click")
		// .setAction("ClubMyCab Click")
		// .setLabel("ClubMyCab Click").build());
		//
		// logger.logEvent("HomePage ClubMyCab Click");
		//
		// Log.d("HomeActivity",
		// "homeclubmycabll click StartAddLatLngIntent : "
		// + StartAddLatLngIntent
		// + " EndAddLatLngIntent : "
		// + EndAddLatLngIntent);
		//
		// Intent mainIntent = new Intent(HomeActivity.this,
		// InviteFragmentActivity.class);
		// if (!StartAddLatLngIntent.isEmpty()
		// && !EndAddLatLngIntent.isEmpty()) {
		// mainIntent.putExtra("StartAddLatLng",
		// StartAddLatLngIntent);
		// mainIntent.putExtra("EndAddLatLng",
		// EndAddLatLngIntent);
		// mainIntent.putExtra("FromShortName",
		// StartAddShortNameIntent);
		// mainIntent.putExtra("ToShortName",
		// EndAddShortNameIntent);
		// StartAddLatLngIntent = "";
		// EndAddLatLngIntent = "";
		// StartAddShortNameIntent = "";
		// EndAddShortNameIntent = "";
		//
		// startActivityForResult(mainIntent, 500);
		// overridePendingTransition(R.anim.slide_in_right,
		// R.anim.slide_out_left);
		// } else {
		// Toast.makeText(HomeActivity.this,
		// "Please enter both from & to locations",
		// Toast.LENGTH_LONG).show();
		// }
		//
		// }
		// };
		// mHandler2.postDelayed(mRunnable2, 500);
		//
		// }
		// });
		//
		// homebookacabll.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		//
		// Animation animScale = AnimationUtils.loadAnimation(
		// HomeActivity.this, R.anim.button_click_anim);
		// homebookacabll.startAnimation(animScale);
		//
		// Handler mHandler2 = new Handler();
		// Runnable mRunnable2 = new Runnable() {
		// @Override
		// public void run() {
		//
		// tracker.send(new HitBuilders.EventBuilder()
		// .setCategory("Book A Cab (HomePage)")
		// .setAction("BookaCab Click")
		// .setLabel("BookaCab Click").build());
		//
		// logger.logEvent("HomePage BookaCab Click");
		//
		// Intent mainIntent = new Intent(HomeActivity.this,
		// BookaCabFragmentActivity.class);
		// if (!StartAddLatLngIntent.isEmpty()
		// && !EndAddLatLngIntent.isEmpty()) {
		// mainIntent.putExtra("StartAddLatLng",
		// StartAddLatLngIntent);
		// mainIntent.putExtra("EndAddLatLng",
		// EndAddLatLngIntent);
		// mainIntent.putExtra("FromShortName",
		// StartAddShortNameIntent);
		// mainIntent.putExtra("ToShortName",
		// EndAddShortNameIntent);
		// StartAddLatLngIntent = "";
		// EndAddLatLngIntent = "";
		// StartAddShortNameIntent = "";
		// EndAddShortNameIntent = "";
		//
		// startActivityForResult(mainIntent, 500);
		// overridePendingTransition(R.anim.slide_in_right,
		// R.anim.slide_out_left);
		// } else {
		// Toast.makeText(HomeActivity.this,
		// "Please enter both from & to locations",
		// Toast.LENGTH_LONG).show();
		// }
		// }
		// };
		// mHandler2.postDelayed(mRunnable2, 500);
		//
		// }
		// });

		// homehereiamll.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		//
		// Animation animScale = AnimationUtils.loadAnimation(
		// HomeActivity.this, R.anim.button_click_anim);
		// homehereiamll.startAnimation(animScale);
		//
		// Handler mHandler2 = new Handler();
		// Runnable mRunnable2 = new Runnable() {
		// @Override
		// public void run() {
		//
		// tracker.send(new HitBuilders.EventBuilder()
		// .setCategory("ShareLocation (HomePage)")
		// .setAction("ShareLocation (HomePage)")
		// .setLabel("ShareLocation (HomePage)").build());
		//
		// logger.logEvent("HomePage ShareLocation Click");
		//
		// Intent mainIntent = new Intent(HomeActivity.this,
		// ShareLocationFragmentActivity.class);
		// startActivityForResult(mainIntent, 500);
		// overridePendingTransition(R.anim.slide_in_right,
		// R.anim.slide_out_left);
		//
		// }
		// };
		// mHandler2.postDelayed(mRunnable2, 500);
		//
		// }
		// });

		profilepic.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				selectImage();
			}
		});

		notificationimg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent mainIntent = new Intent(HomeActivity.this,
						NotificationListActivity.class);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);

			}
		});

		// // ///////////////
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		// new ConnectionTaskForreadunreadnotification()
		// .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		// } else {
		// new ConnectionTaskForreadunreadnotification().execute();
		// }

		String endpoint = GlobalVariables.ServiceUrl
				+ "/FetchUnreadNotificationCount.php";
		;
		String params = "MobileNumber=" + MobileNumber;
		new GlobalAsyncTask(this, endpoint, params,
				new FetchUnreadNotificationCountHandler(), this, false,
				"FetchUnreadNotificationCount", false);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new ConnectionTaskForFetchClubs()
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			new ConnectionTaskForFetchClubs().execute();
		}

		// ///////////////
		SharedPreferences mPrefs111 = getSharedPreferences("userimage", 0);
		String imagestr = mPrefs111.getString("imagestr", "");

		if (imagestr.isEmpty() || imagestr == null
				|| imagestr.equalsIgnoreCase("")) {

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
			if (yourSelectedImage != null) {
				yourSelectedImage = null;
			}
		}

		// ///////////
		SharedPreferences mPrefs1111 = getSharedPreferences("MyProfile", 0);
		String myprofile = mPrefs1111.getString("myprofile", "");

		if (myprofile.isEmpty() || myprofile == null
				|| myprofile.equalsIgnoreCase("")) {

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForFetchMyProfile()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new ConnectionTaskForFetchMyProfile().execute();
			}
		}

		// homebtnsll = (LinearLayout) findViewById(R.id.homebtnsll);
		// homebtnsll.setVisibility(View.GONE);

		hometoofficell = (LinearLayout) findViewById(R.id.hometoofficell);
		hometoofficell.setOnClickListener(new View.OnClickListener() {

			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			@Override
			public void onClick(View v) {
				Log.d("HomeActivity", "home : " + home + " work : " + work);

				if (home != null && work != null) {
					resetIntentParams(true);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
						hometoofficell.setBackground(getResources()
								.getDrawable(R.drawable.border_selected));
						officetohomell.setBackground(getResources()
								.getDrawable(R.drawable.border));
					} else {
						hometoofficell.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.border_selected));
						officetohomell.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.border));
					}

					addressModelFrom = home;
					addressModelTo = work;

					// homebtnsll.setVisibility(View.VISIBLE);
					showButtonsDialog();
				} else {
					showNoFavoritesDialog();
				}
			}
		});

		officetohomell = (LinearLayout) findViewById(R.id.officetohomell);
		officetohomell.setOnClickListener(new View.OnClickListener() {

			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			@Override
			public void onClick(View v) {
				if (home != null && work != null) {
					resetIntentParams(true);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
						officetohomell.setBackground(getResources()
								.getDrawable(R.drawable.border_selected));
						hometoofficell.setBackground(getResources()
								.getDrawable(R.drawable.border));
					} else {
						officetohomell.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.border_selected));
						hometoofficell.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.border));
					}

					addressModelFrom = work;
					addressModelTo = home;

					// homebtnsll.setVisibility(View.VISIBLE);
					showButtonsDialog();
				} else {
					showNoFavoritesDialog();
				}
			}
		});

		from_places = (AutoCompleteTextView) findViewById(R.id.from_places);
		from_places.setAdapter(new PlacesAutoCompleteAdapter(this,
				R.layout.list_item));

		from_places.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View view, MotionEvent motionEvent) {

				resetIntentParams(false);

				addressModelFrom = null;

				if (to_places.getText().toString().isEmpty()
						&& tAddress == null) {
					addressModelTo = null;
				}

				return false;
			}
		});

		from_places
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (event != null
								&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
							dismissKeyboard();
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

						dismissKeyboard();

						fAddress = geocodeAddress(from_places.getText()
								.toString());
						if (fAddress == null) {
							Toast.makeText(
									HomeActivity.this,
									"Could not locate the address, please try using the map or a different address",
									Toast.LENGTH_LONG).show();
						} else {
							addressModelFrom = new AddressModel();
							addressModelFrom.setAddress(fAddress);
							addressModelFrom.setShortname(fromshortname);
							addressModelFrom.setLongname(from_places.getText()
									.toString());

							if (fAddress != null && tAddress != null) {
								// homebtnsll.setVisibility(View.VISIBLE);
								showButtonsDialog();
							}
						}
					}
				});

		clearedittextimgfrom = (ImageView) findViewById(R.id.clearedittextimgfrom);
		clearedittextimgfrom.setVisibility(View.GONE);

		to_places = (AutoCompleteTextView) findViewById(R.id.to_places);
		to_places.setAdapter(new PlacesAutoCompleteAdapter(this,
				R.layout.list_item));

		to_places.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View view, MotionEvent motionEvent) {

				resetIntentParams(false);

				addressModelTo = null;

				if (from_places.getText().toString().isEmpty()
						&& fAddress == null) {
					addressModelFrom = null;
				}

				return false;
			}
		});
		to_places
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (event != null
								&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
							dismissKeyboard();
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

				dismissKeyboard();

				tAddress = geocodeAddress(to_places.getText().toString());
				if (tAddress == null) {
					Toast.makeText(
							HomeActivity.this,
							"Could not locate the address, please try using the map or a different address",
							Toast.LENGTH_LONG).show();
				} else {
					addressModelTo = new AddressModel();
					addressModelTo.setAddress(tAddress);
					addressModelTo.setShortname(toshortname);
					addressModelTo.setLongname(to_places.getText().toString());

					if (fAddress != null && tAddress != null) {
						// homebtnsll.setVisibility(View.VISIBLE);
						showButtonsDialog();
					}
				}

			}
		});

		clearedittextimgto = (ImageView) findViewById(R.id.clearedittextimgto);
		clearedittextimgto.setVisibility(View.GONE);

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
					fromshortname = MapUtilityMethods.getaddressfromautoplace(
							HomeActivity.this, from_places.getText().toString()
									.trim());
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
					toshortname = MapUtilityMethods.getaddressfromautoplace(
							HomeActivity.this, to_places.getText().toString()
									.trim());
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
				fAddress = null;
				fromshortname = "";
			}
		});

		clearedittextimgto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				to_places.setText("");
				tAddress = null;
				toshortname = "";
			}
		});

		flagchk = true;

		threedotsfrom = (Button) findViewById(R.id.threedotsfrom);
		threedotsfrom.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

		threedotsto = (Button) findViewById(R.id.threedotsto);
		threedotsto.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

		fromrelative = (RelativeLayout) findViewById(R.id.fromrelative);
		contentrelativehomepage = (RelativeLayout) findViewById(R.id.contentrelativehomepage);

		fromlocation = (TextView) findViewById(R.id.fromlocation);
		fromdone = (Button) findViewById(R.id.fromdone);

		fromlocation.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		fromdone.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

		cancel = (Button) findViewById(R.id.cancel);
		cancel.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

		// Getting Google Play availability status
		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getBaseContext());

		// Showing status
		if (status != ConnectionResult.SUCCESS) { // Google Play Services are
													// not available

			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
					requestCode);
			dialog.show();

		} else {
			myMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.frommap)).getMap();

			myMap.setMyLocationEnabled(true);

			myMap.setOnCameraChangeListener(new OnCameraChangeListener() {

				@Override
				public void onCameraChange(CameraPosition cameraPosition) {

					invitemapcenter = cameraPosition.target;

					String address = MapUtilityMethods.getAddress(
							HomeActivity.this, invitemapcenter.latitude,
							invitemapcenter.longitude);
					Log.d("address", "" + address);

					fromlocation.setText(address);

				}
			});
		}
		// from_places.setText("Select Source");
		fromdone.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				fromrelative.setVisibility(View.GONE);
				contentrelativehomepage.setVisibility(View.VISIBLE);
				String fromlocationname = fromlocation.getText().toString()
						.trim();
				flagchk = true;
				if (whichdotclick.equalsIgnoreCase("fromdot")) {

					LatLng mapfromlatlng = invitemapcenter;
					fromshortname = MapUtilityMethods.getAddressshort(
							HomeActivity.this, mapfromlatlng.latitude,
							mapfromlatlng.longitude);

					fAddress = null; // reset previous

					from_places.setText(fromlocationname);

					String jnd = from_places.getText().toString().trim();

					Geocoder fcoder = new Geocoder(HomeActivity.this);
					try {
						ArrayList<Address> adresses = (ArrayList<Address>) fcoder
								.getFromLocationName(jnd, 50);

						for (Address add : adresses) {
							// flongitude = (float) add.getLongitude();
							// flatitude = (float) add.getLatitude();
							fAddress = add;
						}

						addressModelFrom = new AddressModel();
						addressModelFrom.setAddress(fAddress);
						addressModelFrom.setShortname(fromshortname);
						addressModelFrom.setLongname(from_places.getText()
								.toString());

					} catch (Exception e) {
						e.printStackTrace();
					}

				}

				else if (whichdotclick.equalsIgnoreCase("todot")) {

					LatLng maptolatlng = invitemapcenter;
					toshortname = MapUtilityMethods.getAddressshort(
							HomeActivity.this, maptolatlng.latitude,
							maptolatlng.longitude);

					tAddress = null; // reset previous

					to_places.setText(fromlocationname);

					String jnd2 = to_places.getText().toString().trim();

					Geocoder tcoder = new Geocoder(HomeActivity.this);
					try {
						ArrayList<Address> adresses = (ArrayList<Address>) tcoder
								.getFromLocationName(jnd2, 50);

						for (Address add : adresses) {
							// tlongitude = (float) add.getLongitude();
							// tlatitude = (float) add.getLatitude();
							tAddress = add;
						}

						addressModelTo = new AddressModel();
						addressModelTo.setAddress(tAddress);
						addressModelTo.setShortname(toshortname);
						addressModelTo.setLongname(to_places.getText()
								.toString());

					} catch (Exception e) {
						e.printStackTrace();
					}

				}

				if (fAddress != null && tAddress != null) {
					// homebtnsll.setVisibility(View.VISIBLE);
					showButtonsDialog();
				}
			}
		});

		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				fromrelative.setVisibility(View.GONE);
				contentrelativehomepage.setVisibility(View.VISIBLE);
			}
		});

		threedotsfrom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismissKeyboard();
				whichdotclick = "fromdot";

				double latitude, longitude;
				if (!from_places.getText().toString().trim().isEmpty()
						&& fAddress != null) {
					// Getting latitude of the current location
					latitude = fAddress.getLatitude();

					// Getting longitude of the current location
					longitude = fAddress.getLongitude();
				} else {
					// Getting latitude of the current location
					latitude = mycurrentlocationobject.getLatitude();

					// Getting longitude of the current location
					longitude = mycurrentlocationobject.getLongitude();
				}

				// Creating a LatLng object for the current location
				LatLng currentlatLng = new LatLng(latitude, longitude);

				// Showing the current location in Google Map
				myMap.moveCamera(CameraUpdateFactory.newLatLng(currentlatLng));

				// Zoom in the Google Map
				myMap.animateCamera(CameraUpdateFactory.zoomTo(15));

				String address = MapUtilityMethods.getAddress(
						HomeActivity.this, latitude, longitude);

				fromlocation.setText(address);

				fromrelative.setVisibility(View.VISIBLE);
				contentrelativehomepage.setVisibility(View.GONE);
			}
		});

		threedotsto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				dismissKeyboard();
				whichdotclick = "todot";

				double latitude, longitude;
				if (!to_places.getText().toString().trim().isEmpty()
						&& tAddress != null) {
					// Getting latitude of the current location
					latitude = tAddress.getLatitude();

					// Getting longitude of the current location
					longitude = tAddress.getLongitude();
				} else {
					// Getting latitude of the current location
					latitude = mycurrentlocationobject.getLatitude();

					// Getting longitude of the current location
					longitude = mycurrentlocationobject.getLongitude();
				}

				// Creating a LatLng object for the current location
				LatLng currentlatLng = new LatLng(latitude, longitude);

				// Showing the current location in Google Map
				myMap.moveCamera(CameraUpdateFactory.newLatLng(currentlatLng));

				// Zoom in the Google Map
				myMap.animateCamera(CameraUpdateFactory.zoomTo(15));

				String address = MapUtilityMethods.getAddress(
						HomeActivity.this, latitude, longitude);

				fromlocation.setText(address);

				fromrelative.setVisibility(View.VISIBLE);
				contentrelativehomepage.setVisibility(View.GONE);

			}
		});
	}

	private void showButtonsDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
		View builderView = (View) getLayoutInflater().inflate(
				R.layout.dialog_home_page, null);

		builder.setView(builderView);
		final AlertDialog dialog = builder.create();

		LinearLayout linearLayout = (LinearLayout) builderView
				.findViewById(R.id.homeclubmycabll);
		linearLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

				tracker.send(new HitBuilders.EventBuilder()
						.setCategory("ClubMyCab Click")
						.setAction("ClubMyCab Click")
						.setLabel("ClubMyCab Click").build());

				logger.logEvent("HomePage ClubMyCab Click");

				Log.d("HomeActivity",
						"homeclubmycabll click addressModelFrom : "
								+ addressModelFrom.getShortname()
								+ " addressModelTo : "
								+ addressModelTo.getShortname());

				Intent mainIntent = new Intent(HomeActivity.this,
						InviteFragmentActivity.class);
				if (addressModelFrom != null && addressModelTo != null) {

					Gson gson = new Gson();

					mainIntent.putExtra("StartAddressModel",
							gson.toJson(addressModelFrom).toString());
					mainIntent.putExtra("EndAddressModel",
							gson.toJson(addressModelTo).toString());
					addressModelFrom = null;
					addressModelTo = null;

					startActivityForResult(mainIntent, 500);
					overridePendingTransition(R.anim.slide_in_right,
							R.anim.slide_out_left);
				} else {
					Toast.makeText(HomeActivity.this,
							"Please enter both from & to locations",
							Toast.LENGTH_LONG).show();
				}

				dialog.dismiss();
			}
		});

		linearLayout = (LinearLayout) builderView
				.findViewById(R.id.homebookacabll);
		linearLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

				tracker.send(new HitBuilders.EventBuilder()
						.setCategory("Book A Cab (HomePage)")
						.setAction("BookaCab Click").setLabel("BookaCab Click")
						.build());

				logger.logEvent("HomePage BookaCab Click");

				Intent mainIntent = new Intent(HomeActivity.this,
						BookaCabFragmentActivity.class);
				if (addressModelFrom != null && addressModelTo != null) {

					Gson gson = new Gson();

					mainIntent.putExtra("StartAddressModel",
							gson.toJson(addressModelFrom).toString());
					mainIntent.putExtra("EndAddressModel",
							gson.toJson(addressModelTo).toString());
					addressModelFrom = null;
					addressModelTo = null;

					startActivityForResult(mainIntent, 500);
					overridePendingTransition(R.anim.slide_in_right,
							R.anim.slide_out_left);
				} else {
					Toast.makeText(HomeActivity.this,
							"Please enter both from & to locations",
							Toast.LENGTH_LONG).show();
				}

				dialog.dismiss();
			}
		});

		dialog.show();
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void resetIntentParams(boolean clearAutocomplete) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			officetohomell.setBackground(getResources().getDrawable(
					R.drawable.border));
			hometoofficell.setBackground(getResources().getDrawable(
					R.drawable.border));
		} else {
			officetohomell.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.border));
			hometoofficell.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.border));
		}

		if (clearAutocomplete) {
			from_places.setText("");
			fAddress = null;
			fromshortname = "";

			to_places.setText("");
			tAddress = null;
			toshortname = "";

			addressModelFrom = null;
			addressModelTo = null;
		}
	}

	private void getWorkHomeAddress() {
		SharedPreferences mPrefs11111 = getSharedPreferences(
				"FavoriteLocations", 0);
		final String favoritelocation = mPrefs11111.getString(
				"favoritelocation", "");
		Log.d("HomeActivity", "favoritelocation : " + favoritelocation);

		AddressModel homeAddressModel = null, workAddressModel = null;

		if (!favoritelocation.isEmpty()) {

			Gson gson = new Gson();
			HashMap<String, String> hashMap = gson.fromJson(favoritelocation,
					HashMap.class);

			if (hashMap.size() > 0) {

				homeAddressModel = (AddressModel) gson.fromJson(
						hashMap.get("Where do you live?"), AddressModel.class);
				workAddressModel = (AddressModel) gson.fromJson(
						hashMap.get("Where do you work?"), AddressModel.class);

				Log.d("HomeActivity", "hashMap : " + hashMap);
				Log.d("HomeActivity", "homeAddressModel : " + homeAddressModel
						+ " workAddressModel : " + workAddressModel);
			}
		}

		home = homeAddressModel;
		work = workAddressModel;
	}

	private void showNoFavoritesDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
		builder.setMessage("You have not saved your home & office locations. Save them in favorites to activate these options, would you like to do that now?");
		builder.setCancelable(false);

		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(HomeActivity.this,
						FavoriteLocationsAcivity.class);
				intent.putExtra("NotFromRegistration", true);
				startActivity(intent);
			}
		});

		builder.setNegativeButton("Later", null);

		builder.show();
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

	private void dismissKeyboard() {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus()
				.getWindowToken(), 0);
	}

	private void selectImage() {

		final CharSequence[] options = { "Take Photo", "Choose from Gallery",
				"Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
		builder.setTitle("Add Photo!");
		builder.setItems(options, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (options[item].equals("Take Photo")) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File f = new File(android.os.Environment
							.getExternalStorageDirectory(), "temp.jpg");
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
					startActivityForResult(intent, 1);
				} else if (options[item].equals("Choose from Gallery")) {
					Intent intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, 2);

				} else if (options[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {

			mainbmp = null;
			if (requestCode == 1) {
				File f = new File(Environment.getExternalStorageDirectory()
						.toString());
				for (File temp : f.listFiles()) {
					if (temp.getName().equals("temp.jpg")) {
						f = temp;
						break;
					}
				}
				try {

					Log.d("f.getAbsolutePath()", "" + f.getAbsolutePath());
					imageuploadchkstr = "fromcamera";

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new ConnectionTaskForImageUpload().executeOnExecutor(
								AsyncTask.THREAD_POOL_EXECUTOR,
								f.getAbsolutePath());
					} else {
						new ConnectionTaskForImageUpload().execute(f
								.getAbsolutePath());
					}

					/*
					 * Bitmap bitmapOrg; BitmapFactory.Options bitmapOptions =
					 * new BitmapFactory.Options();
					 * 
					 * bitmapOrg = BitmapFactory.decodeFile(f.getAbsolutePath(),
					 * bitmapOptions);
					 * 
					 * int width = bitmapOrg.getWidth(); int height =
					 * bitmapOrg.getHeight(); int newWidth = 200; int newHeight
					 * = 200;
					 * 
					 * // calculate the scale - in this case = 0.4f float
					 * scaleWidth = ((float) newWidth) / width; float
					 * scaleHeight = ((float) newHeight) / height;
					 * 
					 * // createa matrix for the manipulation Matrix matrix =
					 * new Matrix(); // resize the bit map
					 * matrix.postScale(scaleWidth, scaleHeight); // rotate the
					 * Bitmap // matrix.postRotate(45);
					 * 
					 * // recreate the new Bitmap Bitmap resizedBitmap =
					 * Bitmap.createBitmap(bitmapOrg, 0, 0, width, height,
					 * matrix, true);
					 * 
					 * mainbmp = resizedBitmap;
					 * 
					 * profilepic.setImageBitmap(mainbmp);
					 * 
					 * String imgString = Base64.encodeToString(
					 * getBytesFromBitmap(mainbmp), Base64.NO_WRAP);
					 * 
					 * if (Build.VERSION.SDK_INT >=
					 * Build.VERSION_CODES.HONEYCOMB) { new
					 * ConnectionTaskForImageUpload().executeOnExecutor(
					 * AsyncTask.THREAD_POOL_EXECUTOR, imgString); } else { new
					 * ConnectionTaskForImageUpload().execute(imgString); }
					 */} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (requestCode == 2) {

				Uri selectedImage = data.getData();
				String[] filePath = { MediaStore.Images.Media.DATA };
				Cursor c = getContentResolver().query(selectedImage, filePath,
						null, null, null);
				c.moveToFirst();
				int columnIndex = c.getColumnIndex(filePath[0]);
				String picturePath = c.getString(columnIndex);
				c.close();

				if (picturePath.toString().trim().contains("http")) {
					Log.d("picturePath", "" + picturePath);
					imageuploadchkstr = "fromurl";

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new ConnectionTaskForImageUpload().executeOnExecutor(
								AsyncTask.THREAD_POOL_EXECUTOR, picturePath);
					} else {
						new ConnectionTaskForImageUpload().execute(picturePath);
					}
				} else {
					Log.d("picturePath", "" + picturePath);
					imageuploadchkstr = "fromgallerynormal";

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new ConnectionTaskForImageUpload().executeOnExecutor(
								AsyncTask.THREAD_POOL_EXECUTOR, picturePath);
					} else {
						new ConnectionTaskForImageUpload().execute(picturePath);
					}
				}
			}
		} else {
			mainbmp = null;
			Log.d("Result not ok", "Result not ok");
		}

	}

	// ///////

	private class ConnectionTaskForImageUpload extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(HomeActivity.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionImageUpload mAuth1 = new AuthenticateConnectionImageUpload();
			try {
				mAuth1.picturePath = args[0];
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
			if (dialog.isShowing()) {
				dialog.dismiss();
			}

			if (exceptioncheck) {
				exceptioncheck = false;
				Toast.makeText(HomeActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (imageuploadresp.equalsIgnoreCase("Error")) {

				Toast.makeText(
						HomeActivity.this,
						"Error uploading Image, Please try again or use a different image",
						Toast.LENGTH_SHORT).show();
			} else {

				profilepic.setImageBitmap(mIcon11);
				drawerprofilepic.setImageBitmap(mIcon11);

				Toast.makeText(HomeActivity.this, "Image Uploaded",
						Toast.LENGTH_SHORT).show();

				if (mIcon11 != null) {
					mIcon11 = null;
				}
			}
		}

	}

	public class AuthenticateConnectionImageUpload {

		String picturePath;

		public AuthenticateConnectionImageUpload() {

		}

		public void connection() throws Exception {

			tracker.send(new HitBuilders.EventBuilder()
					.setCategory("ImageUpload Click")
					.setAction("ImageUpload Click")
					.setLabel("ImageUpload Click").build());

			String imagestr = null;

			if (imageuploadchkstr.toString().trim().equalsIgnoreCase("fromurl")) {
				URL url = new URL(picturePath);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setDoInput(true);
				connection.connect();
				InputStream input = connection.getInputStream();
				Bitmap myBitmap = BitmapFactory.decodeStream(input);
				Log.d("img width", "" + myBitmap.getWidth());
				Log.d("img height", "" + myBitmap.getHeight());

				int width = 0;
				int height = 0;

				if (myBitmap.getWidth() <= myBitmap.getHeight()) {

					width = 200;
					height = (myBitmap.getHeight() * 200) / myBitmap.getWidth();
				} else {
					width = (myBitmap.getWidth() * 200) / myBitmap.getHeight();
					height = 200;
				}

				Log.d("width", "" + width);
				Log.d("height", "" + height);

				myBitmap = scaleBitmap(myBitmap, width, height);

				Log.d("resize width", "" + myBitmap.getWidth());
				Log.d("resize height", "" + myBitmap.getHeight());

				mainbmp = myBitmap;
				if (myBitmap != null) {
					myBitmap = null;
				}

				imagestr = Base64.encodeToString(getBytesFromBitmap(mainbmp),
						Base64.NO_WRAP);
			} else if (imageuploadchkstr.toString().trim()
					.equalsIgnoreCase("fromgallerynormal")) {
				Bitmap bitmapOrg = (BitmapFactory.decodeFile(picturePath));
				Log.d("img width", "" + bitmapOrg.getWidth());
				Log.d("img height", "" + bitmapOrg.getHeight());

				int width = 0;
				int height = 0;

				if (bitmapOrg.getWidth() <= bitmapOrg.getHeight()) {

					width = 200;
					height = (bitmapOrg.getHeight() * 200)
							/ bitmapOrg.getWidth();
				} else {
					width = (bitmapOrg.getWidth() * 200)
							/ bitmapOrg.getHeight();
					height = 200;
				}

				Log.d("width", "" + width);
				Log.d("height", "" + height);

				bitmapOrg = scaleBitmap(bitmapOrg, width, height);

				mainbmp = bitmapOrg;
				if (bitmapOrg != null) {
					bitmapOrg = null;
				}

				imagestr = Base64.encodeToString(getBytesFromBitmap(mainbmp),
						Base64.NO_WRAP);
			}

			else if (imageuploadchkstr.toString().trim()
					.equalsIgnoreCase("fromcamera")) {
				Bitmap bitmapOrg = (BitmapFactory.decodeFile(picturePath));
				Log.d("img width", "" + bitmapOrg.getWidth());
				Log.d("img height", "" + bitmapOrg.getHeight());

				int width = 0;
				int height = 0;

				if (bitmapOrg.getWidth() <= bitmapOrg.getHeight()) {

					width = 200;
					height = (bitmapOrg.getHeight() * 200)
							/ bitmapOrg.getWidth();
				} else {
					width = (bitmapOrg.getWidth() * 200)
							/ bitmapOrg.getHeight();
					height = 200;
				}

				Log.d("width", "" + width);
				Log.d("height", "" + height);

				bitmapOrg = scaleBitmap(bitmapOrg, width, height);

				mainbmp = bitmapOrg;
				if (bitmapOrg != null) {
					bitmapOrg = null;
				}

				imagestr = Base64.encodeToString(getBytesFromBitmap(mainbmp),
						Base64.NO_WRAP);
			}

			if (mainbmp != null) {
				mainbmp = null;
			}

			String url_select = GlobalVariables.ServiceUrl + "/imageupload.php";

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
					"MobileNumber", MobileNumber);
			BasicNameValuePair ImageBasicNameValuePair = new BasicNameValuePair(
					"imagestr", imagestr);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MobileNumberBasicNameValuePair);
			nameValuePairList.add(ImageBasicNameValuePair);

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

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				imageuploadresp = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("imageuploadresp", "" + stringBuilder.toString());

			if (imageuploadresp.equalsIgnoreCase("Error")) {

			} else {

				String urldisplay = GlobalVariables.ServiceUrl
						+ "/ProfileImages/" + imageuploadresp.trim();
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
				editor1.putString("imgname", imageuploadresp.trim());
				editor1.putString("imagestr", imgString);
				editor1.commit();
			}
		}
	}

	public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight) {
		Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight,
				Config.ARGB_8888);

		float scaleX = newWidth / (float) bitmap.getWidth();
		float scaleY = newHeight / (float) bitmap.getHeight();
		float pivotX = 0;
		float pivotY = 0;

		Matrix scaleMatrix = new Matrix();
		scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);

		Canvas canvas = new Canvas(scaledBitmap);
		canvas.setMatrix(scaleMatrix);
		canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));

		return scaledBitmap;
	}

	// convert from bitmap to byte array
	public byte[] getBytesFromBitmap(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 80, stream);
		return stream.toByteArray();
	}

	@Override
	public void onBackPressed() {

		if (fromrelative.getVisibility() == View.VISIBLE) {
			fromrelative.setVisibility(View.GONE);
			contentrelativehomepage.setVisibility(View.VISIBLE);
		} else {
			if (back_pressed + 2000 > System.currentTimeMillis()) {
				super.onBackPressed();
			} else {
				Toast.makeText(getBaseContext(), "Press once again to exit!",
						Toast.LENGTH_SHORT).show();
				back_pressed = System.currentTimeMillis();
			}
		}

	}

	// // ///////
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
	// Toast.makeText(HomeActivity.this,
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
	//
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
	//
	// }
	// }

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
				Toast.makeText(HomeActivity.this,
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

	// ///////
	private class ConnectionTaskForFetchMyProfile extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionFetchMyProfile mAuth1 = new AuthenticateConnectionFetchMyProfile();
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
				Toast.makeText(HomeActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}
		}
	}

	public class AuthenticateConnectionFetchMyProfile {

		public AuthenticateConnectionFetchMyProfile() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/fetchmyprofile.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair UserNumberBasicNameValuePair = new BasicNameValuePair(
					"UserNumber", MobileNumber);

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
			String myprofileresp = null;

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				myprofileresp = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("myprofileresp", "" + myprofileresp);

			SharedPreferences sharedPreferences1 = getSharedPreferences(
					"MyProfile", 0);
			SharedPreferences.Editor editor1 = sharedPreferences1.edit();
			editor1.putString("myprofile", myprofileresp.toString().trim());
			editor1.commit();

			// ///////////////
		}
	}

	// ///////
	private class ConnectionTaskForFetchClubs extends
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
				exceptioncheck = true;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {

			if (exceptioncheck) {
				exceptioncheck = false;
				Toast.makeText(HomeActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}
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
			String myprofileresp = null;

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				myprofileresp = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("myclubsresp", "" + myprofileresp);

			SharedPreferences sharedPreferences1 = getSharedPreferences(
					"MyClubs", 0);
			SharedPreferences.Editor editor1 = sharedPreferences1.edit();
			editor1.putString("clubs", myprofileresp.toString().trim());
			editor1.commit();

			// ///////////////
		}
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
	protected void onResume() {
		super.onResume();
		AppEventsLogger.activateApp(this);

		getWorkHomeAddress();

		String PoolResponseSplash = getIntent().getStringExtra(
				"PoolResponseSplash");

		Log.d("HomeActivity", "onResume PoolResponseSplash : "
				+ PoolResponseSplash);

		if (PoolResponseSplash == null || PoolResponseSplash.isEmpty()
				|| PoolResponseSplash.equalsIgnoreCase("null")
				|| PoolResponseSplash.equalsIgnoreCase("No Pool Created Yet!!")
				|| PoolResponseSplash.equalsIgnoreCase("[]")) {
			resetIntentParams(true);
		} else {

			SharedPreferences sharedPreferences = getSharedPreferences(
					"HomeActivityDisplayRides", 0);
			boolean shouldDisplay = sharedPreferences.getBoolean(
					"DisplayRides", false);

			if (shouldDisplay) {
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putBoolean("DisplayRides", false);
				editor.commit();

				Intent mainIntent = new Intent(HomeActivity.this,
						MyRidesActivity.class);
				mainIntent.putExtra("PoolResponseSplash", PoolResponseSplash);
				startActivity(mainIntent);

				finish(); // to ensure when there are active rides to display,
							// the user simply exits the app rather than
							// returning to this page

			} else {
				resetIntentParams(true);
			}

		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		AppEventsLogger.deactivateApp(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
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

	@Override
	public void getResult(String response, String uniqueID) {
		if (uniqueID.equals("FetchUnreadNotificationCount")) {
			if (GlobalVariables.UnreadNotificationCount.equalsIgnoreCase("0")) {

				unreadnoticountrl.setVisibility(View.GONE);

			} else {

				unreadnoticountrl.setVisibility(View.VISIBLE);
				unreadnoticount
						.setText(GlobalVariables.UnreadNotificationCount);
			}
		}
	}
}
