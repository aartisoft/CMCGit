package com.clubmycab;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.maps.MapUtilityMethods;
import com.clubmycab.ui.MobileSiteActivity;
import com.clubmycab.ui.MobileSiteFragment;
import com.clubmycab.ui.MyRidesActivity;
import com.clubmycab.ui.NotificationListActivity;
import com.clubmycab.ui.UniversalDrawer;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.navdrawer.SimpleSideDrawer;

public class BookaCabFragmentActivity extends FragmentActivity implements
		LocationListener {

	// private static ProgressDialog progressDialog;
	RelativeLayout fromrelative;
	TextView fromlocation;
	Button fromdone;
	Button cancel;
	String FromLat;
	String ToLat;
	String Fromlng;
	String Tolng;
	String Rlatlong = null;
	BufferedReader in;
	int temp = 0;
	float flongitude;
	float flatitude;
	float tlongitude;
	float tlatitude;
	Address fAddress, tAddress;
	int size;

	final int RQS_GooglePlayServices = 1;
	private GoogleMap myMap;
	Tracker tracker;

	Location myLocation;
	String FullName, MobileNumberstr;
	boolean markerClicked;
	PolygonOptions polygonOptions;
	Polygon polygon;
	Marker marker;

	String whichdotclick;
	LocationManager locationManager;
	ArrayList<String> CabName;
	ArrayList<String> CabCity;
	ArrayList<String> CabRate;
	ArrayList<String> Cabcontact;
	ArrayList<String> CabNRate;
	ListView mypoollist, lvtime;
	LinearLayout let;
	ArrayAdapter<String> adapterFrom, adapterTo;

	ArrayList<String> CabId = new ArrayList<String>();
	ArrayList<String> ImageId = new ArrayList<String>();

	ArrayList<String> CabTitle = new ArrayList<String>();
	ArrayList<String> ProductId = new ArrayList<String>();

	ArrayList<String> MobileNumber = new ArrayList<String>();
	ArrayList<String> OwnerName = new ArrayList<String>();
	ArrayList<String> FromLocation = new ArrayList<String>();
	ArrayList<String> ToLocation = new ArrayList<String>();
	ArrayList<String> FromShortName = new ArrayList<String>();
	ArrayList<String> ToShortName = new ArrayList<String>();
	ArrayList<String> TravelDate = new ArrayList<String>();
	ArrayList<String> TravelTime = new ArrayList<String>();
	ArrayList<String> Seats = new ArrayList<String>();
	ArrayList<String> RemainingSeats = new ArrayList<String>();
	ArrayList<String> Seat_Status = new ArrayList<String>();
	ArrayList<String> Distance = new ArrayList<String>();
	ArrayList<String> OpenTime = new ArrayList<String>();
	ArrayList<String> imagename = new ArrayList<String>();
	ArrayList<String> EstimatePrice = new ArrayList<String>();
	ArrayList<String> EstimateTime = new ArrayList<String>();
	ArrayList<String> CabNameT = new ArrayList<String>();
	ArrayList<String> BookingRefNo = new ArrayList<String>();
	String poolresponse;

	AutoCompleteTextView from_places;
	AutoCompleteTextView to_places;
	Button threedotsfrom;
	Button threedotsto;
	// Button Search;
	private JSONArray mCabSearchArray;
	private GridView mGridViewCabSearch;
	private String mUberBookingInputParams, mUberUsername, mUberPassword;
	private int cabBookUberMegaPosition;

	private ProgressDialog dialog12;

	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";
	String[] Imag = { "R.drawable.olacab", "R.drawable.mreucab",
			"R.drawable.easycab" };

	private static final String LOG_TAG = "ExampleApp";
	Location mycurrentlocationobject;

	String distancevalue = null;
	String distancetext = null;
	TextView Estimatetimetext, cabTitle;
	// Button MyRide;

	CircularImageView profilepic;
	TextView username;
	ImageView notificationimg;
	ImageView bookcabresultsort;

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

	RelativeLayout unreadnoticountrl;
	TextView unreadnoticount;
	String readunreadnotiresp;
	String imagenameresp;
	Bitmap mIcon11;

	private Button mButtonSearch;

	private boolean shouldSearchForCurrentLocation;
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
	ArrayList<String> ShortAddress = new ArrayList<String>();
	ArrayList<String> Locality = new ArrayList<String>();

	private boolean togglePriceTimeSort;

	Boolean flagchk;
	String fromshortname;
	String toshortname;
	LatLng invitemapcenter;

	ImageView clearedittextimgfrom;
	ImageView clearedittextimgto;

	private BookaCabFragmentActivity.RideObject rideObject;

	boolean exceptioncheck = false;

	public void setMycurrentlocationobject(Location mycurrentlocationobject) {
		this.mycurrentlocationobject = mycurrentlocationobject;

		try {
			if (shouldSearchForCurrentLocation) {
				shouldSearchForCurrentLocation = false;

				double latitude = mycurrentlocationobject.getLatitude();
				double longitude = mycurrentlocationobject.getLongitude();

				String address = MapUtilityMethods.getAddress(BookaCabFragmentActivity.this,
						latitude, longitude);
				from_places.setText(address);
				fAddress = geocodeAddress(address);
				Log.d("AutoSearchCabAsync", "AutoSearchCabAsync address : "
						+ address.toString());

				performCabSearch();
			}
		} catch (Exception e) {
			shouldSearchForCurrentLocation = false;
			e.printStackTrace();
		}
	}

	@SuppressLint({ "NewApi", "ClickableViewAccessibility" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.d("onCreate", "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ratecharges);

		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		// Check if Internet present
		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					BookaCabFragmentActivity.this);
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

		flagchk = true;

		RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayoutRateCharge);
		relativeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

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
		// Intent mainIntent = new Intent(BookaCabFragmentActivity.this,
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
		// Intent mainIntent = new Intent(BookaCabFragmentActivity.this,
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
		// }
		// });
		//
		// sharemylocation.setOnClickListener(new View.OnClickListener() {
		// @SuppressWarnings("deprecation")
		// @Override
		// public void onClick(View arg0) {
		// mNav.toggleDrawer();
		//
		// Intent mainIntent = new Intent(BookaCabFragmentActivity.this,
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
		// Intent mainIntent = new Intent(BookaCabFragmentActivity.this,
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
		// Intent mainIntent = new Intent(BookaCabFragmentActivity.this,
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
		// Intent mainIntent = new Intent(BookaCabFragmentActivity.this,
		// AboutPagerFragmentActivity.class);
		// startActivityForResult(mainIntent, 500);
		// overridePendingTransition(R.anim.slide_in_right,
		// R.anim.slide_out_left);
		// }
		// });

		GoogleAnalytics analytics = GoogleAnalytics
				.getInstance(BookaCabFragmentActivity.this);
		tracker = analytics.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);

		// All subsequent hits will be send with screen name = "main screen"
		tracker.setScreenName("BookACab");

		UniversalDrawer drawer = new UniversalDrawer(this, tracker);
		drawer.createDrawer();

		profilepic = (CircularImageView) findViewById(R.id.profilepic);
		notificationimg = (ImageView) findViewById(R.id.notificationimg);
		bookcabresultsort = (ImageView) findViewById(R.id.bookcabresultsort);
		drawerprofilepic = (CircularImageView) findViewById(R.id.drawerprofilepic);

		SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
		FullName = mPrefs.getString("FullName", "");
		MobileNumberstr = mPrefs.getString("MobileNumber", "");

		username = (TextView) findViewById(R.id.username);
		username.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		username.setText(FullName);

		drawerusername = (TextView) findViewById(R.id.drawerusername);
		drawerusername.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		drawerusername.setText(FullName);

		if (!isOnline()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					BookaCabFragmentActivity.this);

			builder.setMessage("No Network Available");
			builder.setPositiveButton("OK", null);
			AlertDialog dialog = builder.show();
			TextView messageText = (TextView) dialog
					.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
			dialog.show();

			return;
		}

		unreadnoticountrl = (RelativeLayout) findViewById(R.id.unreadnoticountrl);
		unreadnoticount = (TextView) findViewById(R.id.unreadnoticount);
		
		if (GlobalVariables.UnreadNotificationCount.equalsIgnoreCase("0")) {

			unreadnoticountrl.setVisibility(View.GONE);

		} else {

			unreadnoticountrl.setVisibility(View.VISIBLE);
			unreadnoticount.setText(GlobalVariables.UnreadNotificationCount);
		}

		profilepic.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				selectImage();
			}
		});

		// ///////////////
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//			new ConnectionTaskForreadunreadnotification()
//					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//		} else {
//			new ConnectionTaskForreadunreadnotification().execute();
//		}

		notificationimg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent mainIntent = new Intent(BookaCabFragmentActivity.this,
						NotificationListActivity.class);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);

			}
		});

		togglePriceTimeSort = true;
		bookcabresultsort.setImageDrawable(getResources().getDrawable(
				R.drawable.bookacab_sorticon_rupee));
		bookcabresultsort.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Log.d("BookaCab", "bookcabresultsort onClick : "
						+ togglePriceTimeSort);

				if (togglePriceTimeSort) {
					togglePriceTimeSort = false;
					bookcabresultsort.setImageDrawable(getResources()
							.getDrawable(R.drawable.bookacab_sorticon_time));
					JSONArray jsonArray = performSortOnResults(mCabSearchArray,
							"low_estimate");
					mCabSearchArray = jsonArray;
				} else {
					togglePriceTimeSort = true;
					bookcabresultsort.setImageDrawable(getResources()
							.getDrawable(R.drawable.bookacab_sorticon_rupee));
					JSONArray jsonArray = performSortOnResults(mCabSearchArray,
							"timeEstimate");
					mCabSearchArray = jsonArray;
				}

				updateGridView();

			}
		});

		// ///////////
		SharedPreferences mPrefs11 = getSharedPreferences("notificationcount",
				0);
		String noticount = mPrefs11.getString("Count", "");

		if (noticount.isEmpty() || noticount == null
				|| noticount.equalsIgnoreCase("")) {

		} else {

			if (noticount.equalsIgnoreCase("0")) {

				unreadnoticountrl.setVisibility(View.GONE);

			} else {

				unreadnoticountrl.setVisibility(View.VISIBLE);
				unreadnoticount.setText(noticount);
			}
		}

		// ///////////////
		SharedPreferences mPrefs111 = getSharedPreferences("userimage", 0);
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

		// ///////////////////
		Intent intent = getIntent();
		String fromloc = intent.getStringExtra("fromloc");

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
						fromshortname = ShortAddress.get(i).toString().trim();
						fAddress = new Address(null);
						fAddress.setLatitude(Double.parseDouble(Latitude.get(i)
								.toString().trim()));
						fAddress.setLongitude(Double.parseDouble(Longitude
								.get(i).toString().trim()));
						fAddress.setLocality(Locality.get(i).toString().trim());
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
						fromshortname = ShortAddress.get(i).toString().trim();
						fAddress = new Address(null);
						fAddress.setLatitude(Double.parseDouble(Latitude.get(i)
								.toString().trim()));
						fAddress.setLongitude(Double.parseDouble(Longitude
								.get(i).toString().trim()));
						fAddress.setLocality(Locality.get(i).toString().trim());
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
						toshortname = ShortAddress.get(i).toString().trim();
						tAddress = new Address(null);
						tAddress.setLatitude(Double.parseDouble(Latitude.get(i)
								.toString().trim()));
						tAddress.setLongitude(Double.parseDouble(Longitude
								.get(i).toString().trim()));
						tAddress.setLocality(Locality.get(i).toString().trim());
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
						toshortname = ShortAddress.get(i).toString().trim();
						tAddress = new Address(null);
						tAddress.setLatitude(Double.parseDouble(Latitude.get(i)
								.toString().trim()));
						tAddress.setLongitude(Double.parseDouble(Longitude
								.get(i).toString().trim()));
						tAddress.setLocality(Locality.get(i).toString().trim());
					}
				}

				homeofficellvaluesto.setVisibility(View.GONE);
			}
		});

		FavoritesLocationReadWrite favoritesLocationReadWrite = new FavoritesLocationReadWrite(
				BookaCabFragmentActivity.this);
		JSONArray saveasjsonarray;
		try {
			saveasjsonarray = favoritesLocationReadWrite.readFromFile();
			if (saveasjsonarray.length() > 0) {
				Log.d("saveasjsonarray", "" + saveasjsonarray);
				type.clear();
				Latitude.clear();
				Longitude.clear();
				Address.clear();
				ShortAddress.clear();
				Locality.clear();
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
						ShortAddress.add(saveasjsonarray.getJSONObject(i)
								.getString("shortaddress").toString().trim());
						Locality.add(saveasjsonarray.getJSONObject(i)
								.getString("Locality").toString().trim());
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

		from_places = (AutoCompleteTextView) findViewById(R.id.from_places);

		clearedittextimgfrom = (ImageView) findViewById(R.id.clearedittextimgfrom);
		clearedittextimgfrom.setVisibility(View.GONE);

		from_places.setAdapter(new PlacesAutoCompleteAdapter(this,
				R.layout.list_item));

		from_places.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View view, MotionEvent motionEvent) {

				homeofficellvaluesto.setVisibility(View.GONE);

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
									BookaCabFragmentActivity.this,
									"Could not locate the address, please try using the map or a different address",
									Toast.LENGTH_LONG).show();
						}
					}
				});

		to_places = (AutoCompleteTextView) findViewById(R.id.to_places);

		clearedittextimgto = (ImageView) findViewById(R.id.clearedittextimgto);
		clearedittextimgto.setVisibility(View.GONE);

		to_places.setAdapter(new PlacesAutoCompleteAdapter(this,
				R.layout.list_item));

		to_places.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View view, MotionEvent motionEvent) {

				homeofficellvalues.setVisibility(View.GONE);

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
							BookaCabFragmentActivity.this,
							"Could not locate the address, please try using the map or a different address",
							Toast.LENGTH_LONG).show();
				}

			}
		});

		// MyRide = (Button) findViewById(R.id.textMyRide);

		CabName = new ArrayList<String>();
		CabCity = new ArrayList<String>();
		CabRate = new ArrayList<String>();
		Cabcontact = new ArrayList<String>();
		CabNRate = new ArrayList<String>();

		from_places.setText(fromloc);

		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.list_item);
		adapter.setNotifyOnChange(true);

		from_places.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));
		to_places.setTypeface(Typeface.createFromAsset(getAssets(),
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

					String address = MapUtilityMethods.getAddress(BookaCabFragmentActivity.this,
							invitemapcenter.latitude, invitemapcenter.longitude);
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
				String fromlocationname = fromlocation.getText().toString()
						.trim();
				flagchk = true;
				if (whichdotclick.equalsIgnoreCase("fromdot")) {

					LatLng mapfromlatlng = invitemapcenter;
					fromshortname = MapUtilityMethods.getAddressshort(
							BookaCabFragmentActivity.this,
							mapfromlatlng.latitude, mapfromlatlng.longitude);

					fAddress = null; // reset previous

					from_places.setText(fromlocationname);

					String jnd = from_places.getText().toString().trim();

					Geocoder fcoder = new Geocoder(
							BookaCabFragmentActivity.this);
					try {
						ArrayList<Address> adresses = (ArrayList<Address>) fcoder
								.getFromLocationName(jnd, 50);

						for (Address add : adresses) {
							flongitude = (float) add.getLongitude();
							flatitude = (float) add.getLatitude();
							fAddress = add;
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				}

				else if (whichdotclick.equalsIgnoreCase("todot")) {

					LatLng maptolatlng = invitemapcenter;
					toshortname = MapUtilityMethods.getAddressshort(
							BookaCabFragmentActivity.this,
							maptolatlng.latitude, maptolatlng.longitude);

					tAddress = null; // reset previous

					to_places.setText(fromlocationname);

					String jnd2 = to_places.getText().toString().trim();

					Geocoder tcoder = new Geocoder(
							BookaCabFragmentActivity.this);
					try {
						ArrayList<Address> adresses = (ArrayList<Address>) tcoder
								.getFromLocationName(jnd2, 50);

						for (Address add : adresses) {
							tlongitude = (float) add.getLongitude();
							tlatitude = (float) add.getLatitude();
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

				String address = MapUtilityMethods.getAddress(BookaCabFragmentActivity.this,
						latitude, longitude);

				fromlocation.setText(address);

				fromrelative.setVisibility(View.VISIBLE);
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

				String address = MapUtilityMethods.getAddress(BookaCabFragmentActivity.this,
						latitude, longitude);

				fromlocation.setText(address);

				fromrelative.setVisibility(View.VISIBLE);

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
					fromshortname = MapUtilityMethods.getaddressfromautoplace(
							BookaCabFragmentActivity.this, from_places
									.getText().toString().trim());
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
							BookaCabFragmentActivity.this, to_places.getText()
									.toString().trim());
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

		mTextViewSetHomeFavFrom = (TextView) findViewById(R.id.textViewHome);
		mTextViewSetHomeFavFrom.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// Log.d("BookaCab", "onClick mTextViewSetHomeFav : ");

				try {
					if (from_places.getText().toString().isEmpty()
							|| fAddress == null) {
						Toast.makeText(BookaCabFragmentActivity.this,
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
								BookaCabFragmentActivity.this);

						if (favoritesLocationReadWrite.saveToFile(jsonObject
								.toString())) {
							Toast.makeText(BookaCabFragmentActivity.this,
									"Saved!", Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(BookaCabFragmentActivity.this,
									"Error saving!", Toast.LENGTH_LONG).show();
						}
						Log.d("BookaCab", "onClick mTextViewSetHomeFav : "
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
						// Log.d("BookaCab",
						// "onClick mTextViewSetOfficeFav : ");

						try {
							if (from_places.getText().toString().isEmpty()
									|| fAddress == null) {
								Toast.makeText(BookaCabFragmentActivity.this,
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
										BookaCabFragmentActivity.this);
								if (favoritesLocationReadWrite
										.saveToFile(jsonObject.toString())) {
									Toast.makeText(
											BookaCabFragmentActivity.this,
											"Saved!", Toast.LENGTH_LONG).show();
								} else {
									Toast.makeText(
											BookaCabFragmentActivity.this,
											"Error saving!", Toast.LENGTH_LONG)
											.show();
								}

								Log.d("BookaCab",
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
				// Log.d("BookaCab", "onClick mTextViewSetHomeFav : ");

				try {
					if (to_places.getText().toString().isEmpty()
							|| tAddress == null) {
						Toast.makeText(BookaCabFragmentActivity.this,
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
								BookaCabFragmentActivity.this);
						if (favoritesLocationReadWrite.saveToFile(jsonObject
								.toString())) {
							Toast.makeText(BookaCabFragmentActivity.this,
									"Saved!", Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(BookaCabFragmentActivity.this,
									"Error saving!", Toast.LENGTH_LONG).show();
						}

						Log.d("BookaCab", "onClick mTextViewSetHomeFavTo : "
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
				// Log.d("BookaCab", "onClick mTextViewSetOfficeFav : ");

				try {
					if (to_places.getText().toString().isEmpty()
							|| tAddress == null) {
						Toast.makeText(BookaCabFragmentActivity.this,
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
								BookaCabFragmentActivity.this);
						if (favoritesLocationReadWrite.saveToFile(jsonObject
								.toString())) {
							Toast.makeText(BookaCabFragmentActivity.this,
									"Saved!", Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(BookaCabFragmentActivity.this,
									"Error saving!", Toast.LENGTH_LONG).show();
						}

						Log.d("BookaCab", "onClick mTextViewSetOfficeFavTo : "
								+ favoritesLocationReadWrite.readFromFile());
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		});

		mButtonSearch = (Button) findViewById(R.id.buttonSearch);
		mButtonSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				bookcabresultsort.setVisibility(View.GONE);
				mCabSearchArray = new JSONArray(); // reset grid view
				updateGridView();
				performCabSearch();
			}
		});

		rideObject = null;

		Intent fromToIntent = getIntent();
		if (fromToIntent.getStringExtra("StartAddLatLng") == null
				&& fromToIntent.getStringExtra("EndAddLatLng") == null) {

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForFetchPool()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new ConnectionTaskForFetchPool().execute();
			}

		} else {

			Log.d("BookaCab",
					"StartAddLatLng : "
							+ fromToIntent.getStringExtra("StartAddLatLng")
							+ " EndAddLatLng : "
							+ fromToIntent.getStringExtra("EndAddLatLng"));
			String[] RowData = fromToIntent.getStringExtra("StartAddLatLng")
					.toString().split(",");

			Double startLat = Double.parseDouble(RowData[0]);
			Double startLng = Double.parseDouble(RowData[1]);

			String address = MapUtilityMethods.getAddress(BookaCabFragmentActivity.this,
					startLat.doubleValue(), startLng.doubleValue());
			from_places.setText(address);
			fAddress = geocodeAddress(address);

			RowData = fromToIntent.getStringExtra("EndAddLatLng").toString()
					.split(",");

			Double endLat = Double.parseDouble(RowData[0]);
			Double endLng = Double.parseDouble(RowData[1]);

			address = MapUtilityMethods.getAddress(BookaCabFragmentActivity.this,
					endLat.doubleValue(), endLng.doubleValue());
			to_places.setText(address);
			tAddress = geocodeAddress(address);

			from_places.setEnabled(false);
			to_places.setEnabled(false);
			threedotsfrom.setEnabled(false);
			threedotsto.setEnabled(false);
			clearedittextimgfrom.setVisibility(View.GONE);
			clearedittextimgto.setVisibility(View.GONE);

			rideObject = new RideObject(fromToIntent.getStringExtra("CabId"),
					fromToIntent.getStringExtra("TravelDate"),
					fromToIntent.getStringExtra("TravelTime"),
					fromToIntent.getStringExtra("FromShortName"),
					fromToIntent.getStringExtra("ToShortName"), from_places
							.getText().toString(), to_places.getText()
							.toString());

			performCabSearch();
		}

		mGridViewCabSearch = (GridView) findViewById(R.id.gridViewBookCab);
	}

	private class RideObject extends Object {

		public String cabID = "", travelDate = "", travelTime = "",
				fromShortName = "", toShortName = "", fromLongName = "",
				toLongName = "", estDistance = "", estDuration = "",
				driverName = "", driverPhone = "", vehicle = "";

		public RideObject() {
			super();
		}

		public RideObject(String cab, String tDate, String tTime,
				String fShort, String tShort, String fLong, String tLong) {

			super();

			this.cabID = cab;
			this.travelDate = tDate;
			this.travelTime = tTime;
			this.fromShortName = fShort;
			this.toShortName = tShort;
			this.fromLongName = fLong;
			this.toLongName = tLong;

		}
	}

	private class ConnectionTaskForFetchPool extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(
				BookaCabFragmentActivity.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionFetchPool mAuth1 = new AuthenticateConnectionFetchPool();
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

			if (dialog.isShowing()) {
				dialog.dismiss();
			}

			if (exceptioncheck) {
				exceptioncheck = false;
				Toast.makeText(BookaCabFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (poolresponse.equalsIgnoreCase("No Pool Created Yet!!")
					|| poolresponse.equalsIgnoreCase("[]")) {
				// Toast.makeText(BookaCab.this, "No active rides!",
				// Toast.LENGTH_LONG).show();
			} else {

				CabId.clear();
				MobileNumber.clear();
				OwnerName.clear();
				FromLocation.clear();
				ToLocation.clear();

				FromShortName.clear();
				ToShortName.clear();

				TravelDate.clear();
				TravelTime.clear();
				Seats.clear();
				RemainingSeats.clear();
				Seat_Status.clear();
				Distance.clear();
				OpenTime.clear();
				imagename.clear();

				BookingRefNo.clear();

				try {
					JSONArray subArray = new JSONArray(poolresponse);
					String allcabids = "s";
					for (int i = 0; i < subArray.length(); i++) {
						try {
							CabId.add(subArray.getJSONObject(i)
									.getString("CabId").toString());

							allcabids += "'"
									+ subArray.getJSONObject(i)
											.getString("CabId").toString()
											.trim() + "',";

							MobileNumber.add(subArray.getJSONObject(i)
									.getString("MobileNumber").toString());
							OwnerName.add(subArray.getJSONObject(i)
									.getString("OwnerName").toString());
							FromLocation.add(subArray.getJSONObject(i)
									.getString("FromLocation").toString());
							ToLocation.add(subArray.getJSONObject(i)
									.getString("ToLocation").toString());

							FromShortName.add(subArray.getJSONObject(i)
									.getString("FromShortName").toString());
							ToShortName.add(subArray.getJSONObject(i)
									.getString("ToShortName").toString());

							TravelDate.add(subArray.getJSONObject(i)
									.getString("TravelDate").toString());
							TravelTime.add(subArray.getJSONObject(i)
									.getString("TravelTime").toString());
							Seats.add(subArray.getJSONObject(i)
									.getString("Seats").toString());
							RemainingSeats.add(subArray.getJSONObject(i)
									.getString("RemainingSeats").toString());
							Seat_Status.add(subArray.getJSONObject(i)
									.getString("Seat_Status").toString());
							Distance.add(subArray.getJSONObject(i)
									.getString("Distance").toString());
							OpenTime.add(subArray.getJSONObject(i)
									.getString("OpenTime").toString());
							imagename.add(subArray.getJSONObject(i)
									.getString("imagename").toString());

							BookingRefNo.add(subArray.getJSONObject(i)
									.getString("BookingRefNo").toString());

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					ArrayList<String> arrayListTrip = new ArrayList<String>();
					ArrayList<String> arrayListDate = new ArrayList<String>();
					ArrayList<String> arrayListTime = new ArrayList<String>();
					ArrayList<String> arrayListSeat = new ArrayList<String>();
					for (int j = 0; j < FromShortName.size(); j++) {

						if (BookingRefNo.get(j).isEmpty()
								|| BookingRefNo.get(j).equalsIgnoreCase("null")) {
							arrayListTrip.add(FromShortName.get(j) + " > "
									+ ToShortName.get(j));
							arrayListDate.add(TravelDate.get(j));
							arrayListTime.add(TravelTime.get(j));
							arrayListSeat.add(Seat_Status.get(j) + " Seats");
						}
					}
					if (arrayListTrip.size() > 0) {

						AlertDialog.Builder builder = new AlertDialog.Builder(
								BookaCabFragmentActivity.this);
						View builderView = (View) getLayoutInflater().inflate(
								R.layout.exisiting_rides_dialog, null);

						ListView listView = (ListView) builderView
								.findViewById(R.id.listViewExistingRides);

						Button button = (Button) builderView
								.findViewById(R.id.buttonExistingRides);

						builder.setView(builderView);
						final AlertDialog dialog = builder.create();

						listView.setAdapter(new CustomListViewAdapter(
								BookaCabFragmentActivity.this, arrayListTrip,
								arrayListDate, arrayListTime, arrayListSeat));
						listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {

								String address = FromLocation.get(position);
								from_places.setText(address);
								fAddress = geocodeAddress(address);

								address = ToLocation.get(position);
								to_places.setText(address);
								tAddress = geocodeAddress(address);

								from_places.setEnabled(false);
								to_places.setEnabled(false);
								threedotsfrom.setEnabled(false);
								threedotsto.setEnabled(false);
								clearedittextimgfrom.setVisibility(View.GONE);
								clearedittextimgto.setVisibility(View.GONE);

								rideObject = new RideObject(
										CabId.get(position), TravelDate
												.get(position), TravelTime
												.get(position), FromShortName
												.get(position), ToShortName
												.get(position), from_places
												.getText().toString(),
										to_places.getText().toString());

								performCabSearch();

								dialog.dismiss();
							}
						});

						button.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View view) {
								dialog.dismiss();
							}
						});

						dialog.show();

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}

	private class CustomListViewAdapter extends ArrayAdapter<String> {

		private final Activity context;
		private ArrayList<String> mDataSourceTripNames;
		private ArrayList<String> mDataSourceDates;
		private ArrayList<String> mDataSourceTimes;
		private ArrayList<String> mDataSourceSeats;

		public CustomListViewAdapter(Activity context,
				ArrayList<String> listDataSourceTripNames,
				ArrayList<String> listDataSourceDates,
				ArrayList<String> listDataSourceTimes,
				ArrayList<String> listDataSourceSeats) {

			super(context, 0, listDataSourceTripNames);

			this.context = context;
			this.mDataSourceTripNames = listDataSourceTripNames;
			this.mDataSourceDates = listDataSourceDates;
			this.mDataSourceTimes = listDataSourceTimes;
			this.mDataSourceSeats = listDataSourceSeats;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = (View) context.getLayoutInflater().inflate(
						R.layout.list_row_existing_rides, null);
			}

			TextView textView = (TextView) convertView
					.findViewById(R.id.textViewRowExistingRides);
			textView.setText(mDataSourceTripNames.get(position));
			textView = (TextView) convertView.findViewById(R.id.datetext);
			textView.setText(mDataSourceDates.get(position));
			textView = (TextView) convertView.findViewById(R.id.timetext);
			textView.setText(mDataSourceTimes.get(position));
			textView = (TextView) convertView.findViewById(R.id.seatstext);
			textView.setText(mDataSourceSeats.get(position));

			return convertView;
		}

	}

	public class AuthenticateConnectionFetchPool {

		public AuthenticateConnectionFetchPool() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select11 = GlobalVariables.ServiceUrl
					+ "/FetchMyPools.php";
			HttpPost httpPost = new HttpPost(url_select11);
			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
					"MobileNumber", MobileNumberstr);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MobileNumberBasicNameValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			Log.d("httpResponse FetchMyPools", "" + httpResponse);

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				poolresponse = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("poolresponse", "" + stringBuilder.toString());
		}
	}

	private void performCabSearch() {

		if (from_places.getText().toString().trim().isEmpty()
				|| fAddress == null) {

			from_places.requestFocus();

			AlertDialog.Builder builder = new AlertDialog.Builder(
					BookaCabFragmentActivity.this);

			builder.setMessage("Please Enter From Location. If you have already selected a location on map please try again by selecting a nearby location");
			builder.setPositiveButton("OK", null);
			AlertDialog dialog = builder.show();
			TextView messageText = (TextView) dialog
					.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
			dialog.show();

		} else {
			// successfull for all
			if (!isOnline()) {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						BookaCabFragmentActivity.this);
				builder.setTitle("Internet Connection Error");
				builder.setMessage("ClubMyCab requires Internet connection");
				builder.setPositiveButton("OK", null);
				AlertDialog dialog = builder.show();
				TextView messageText = (TextView) dialog
						.findViewById(android.R.id.message);
				messageText.setGravity(Gravity.CENTER);
				dialog.show();

				return;
			} else {
				Log.d("BookaCab", "performCabSearch");
				if (to_places.getText().toString().trim().isEmpty()
						|| tAddress == null) {
					PerformCabSearchTimeAsync performCabSearchTimeAsync = new PerformCabSearchTimeAsync();
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						performCabSearchTimeAsync
								.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					} else {
						performCabSearchTimeAsync.execute();
					}
				} else {
					DistanceTimeEstimateAsync distanceTimeEstimateAsync = new DistanceTimeEstimateAsync();
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						distanceTimeEstimateAsync
								.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					} else {
						distanceTimeEstimateAsync.execute();
					}
				}

			}
		}

	}

	public class PerformCabSearchTimeAsync extends
			AsyncTask<String, Void, String> {

		String result;

		@Override
		protected void onPreExecute() {
			Log.d("PerformCabSearchTimeAsync", "onPreExecute");
			dialog12 = new ProgressDialog(BookaCabFragmentActivity.this);

			dialog12.setMessage("Please Wait...");
			dialog12.setCancelable(false);
			dialog12.setCanceledOnTouchOutside(false);
			dialog12.show();
		}

		@Override
		protected String doInBackground(String... args) {
			Log.d("PerformCabSearchTimeAsync", "doInBackground : "
					+ GlobalVariables.ServiceUrl + "/fetchCabDetailsNew.php?"
					+ "FromCity=" + fAddress.getLocality().toString()
					+ "&slat=" + String.valueOf(fAddress.getLatitude())
					+ "&slon=" + String.valueOf(fAddress.getLongitude()));

			try {
				URL url = new URL(GlobalVariables.ServiceUrl
						+ "/fetchCabDetailsNew.php");
				String response = "";

				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				urlConnection.setReadTimeout(30000);
				urlConnection.setConnectTimeout(30000);
				urlConnection.setRequestMethod("POST");
				urlConnection.setDoInput(true);
				urlConnection.setDoOutput(true);

				OutputStream outputStream = urlConnection.getOutputStream();
				BufferedWriter bufferedWriter = new BufferedWriter(
						new OutputStreamWriter(outputStream, "UTF-8"));
				bufferedWriter.write("FromCity="
						+ fAddress.getLocality().toString() + "&slat="
						+ String.valueOf(fAddress.getLatitude()) + "&slon="
						+ String.valueOf(fAddress.getLongitude()));
				bufferedWriter.flush();
				bufferedWriter.close();
				outputStream.close();

				int responseCode = urlConnection.getResponseCode();

				if (responseCode == HttpsURLConnection.HTTP_OK) {

					String line = "";
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(
									urlConnection.getInputStream()));
					while ((line = bufferedReader.readLine()) != null) {
						response += line;
					}

				} else {
					response = "";
					Log.d("PerformCabSearchTimeAsync",
							"responseCode != HttpsURLConnection.HTTP_OK : "
									+ responseCode);
					result = response;
				}

				Log.d("PerformCabSearchTimeAsync",
						"performCabSearchTime response : " + response);
				result = response;
			} catch (Exception e) {
				e.printStackTrace();
				result = "";
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(BookaCabFragmentActivity.this,
								"Something went wrong, please try again",
								Toast.LENGTH_LONG).show();
					}
				});
			}

			return result;
		}

		@Override
		protected void onPostExecute(String result) {

			if (!result.toLowerCase().contains("error")) {

				try {
					JSONArray responseArray = new JSONArray(result);
					if (responseArray.length() > 0) {
						mCabSearchArray = new JSONArray();
						for (int i = 0; i < responseArray.length(); i++) {

							mCabSearchArray.put(responseArray.getJSONObject(i));
							// JSONArray temp = responseArray.getJSONArray(i);
							// for (int j = 0; j < temp.length(); j++) {
							// mCabSearchArray.put(temp.getJSONObject(j));
							// }
						}

						runOnUiThread(new Runnable() {

							@Override
							public void run() {

								if (mCabSearchArray.length() > 0) {
									// Default sort by time
									togglePriceTimeSort = true;
									bookcabresultsort
											.setImageDrawable(getResources()
													.getDrawable(
															R.drawable.bookacab_sorticon_rupee));
									JSONArray jsonArray = performSortOnResults(
											mCabSearchArray, "timeEstimate");
									mCabSearchArray = jsonArray;
								}

								updateGridView();
							}
						});

					} else {

					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(BookaCabFragmentActivity.this,
									"Something went wrong, please try again",
									Toast.LENGTH_LONG).show();
						}
					});
				}

			} else {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(
								BookaCabFragmentActivity.this,
								"No cabs found for your location, we are continuously adding new locations",
								Toast.LENGTH_LONG).show();
					}
				});
			}

			if (dialog12.isShowing()) {
				dialog12.dismiss();
			}
		}
	}

	public class DistanceTimeEstimateAsync extends
			AsyncTask<String, Void, String> {

		String result;

		@Override
		protected void onPreExecute() {
			dialog12 = new ProgressDialog(BookaCabFragmentActivity.this);

			dialog12.setMessage("Please Wait...");
			dialog12.setCancelable(false);
			dialog12.setCanceledOnTouchOutside(false);
			dialog12.show();
		}

		@Override
		protected String doInBackground(String... args) {
			Log.d("DistanceTimeEstimateAsync",
					"fAddress : " + fAddress.toString() + " tAddress : "
							+ tAddress.toString());

			try {
				URL url = new URL(
						"https://maps.googleapis.com/maps/api/directions/xml?origin="
								+ Double.toString(fAddress.getLatitude())
								+ ","
								+ Double.toString(fAddress.getLongitude())
								+ "&destination="
								+ Double.toString(tAddress.getLatitude())
								+ ","
								+ Double.toString(tAddress.getLongitude())
								+ "&sensor=false&units=metric&alternatives=false&mode=driving&key="+GlobalVariables.GoogleMapsAPIKey);
				String response = "";

				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				urlConnection.setReadTimeout(30000);
				urlConnection.setConnectTimeout(30000);
				urlConnection.setRequestMethod("POST");
				urlConnection.setDoInput(true);
				urlConnection.setDoOutput(true);

				int responseCode = urlConnection.getResponseCode();

				if (responseCode == HttpsURLConnection.HTTP_OK) {

					String line = "";
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(
									urlConnection.getInputStream()));
					while ((line = bufferedReader.readLine()) != null) {
						response += line;
					}

				} else {
					response = "";
					Log.d("DistanceTimeEstimateAsync",
							"responseCode != HttpsURLConnection.HTTP_OK : "
									+ responseCode);
					result = response;
				}

				Log.d("DistanceTimeEstimateAsync",
						"DistanceTimeEstimateAsync response : " + response);
				result = response;
			} catch (Exception e) {
				e.printStackTrace();
				result = "";
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(BookaCabFragmentActivity.this,
								"Something went wrong, please try again",
								Toast.LENGTH_LONG).show();
					}
				});
			}

			if (!result.isEmpty()) {

				try {
					DistanceTimeEstimateHandler distanceTimeEstimateHandler = new DistanceTimeEstimateHandler();
					XMLReader xmlReader = SAXParserFactory.newInstance()
							.newSAXParser().getXMLReader();
					xmlReader.setContentHandler(distanceTimeEstimateHandler);
					xmlReader.parse(new InputSource(new StringReader(result)));

					ArrayList<Double> distance = distanceTimeEstimateHandler.distanceArray;
					ArrayList<Double> duration = distanceTimeEstimateHandler.durationArray;

					performCabSearchPrice(fAddress, tAddress,
							(distance.get(distance.size() - 1) / 1000),
							(duration.get(duration.size() - 1) / 60));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(BookaCabFragmentActivity.this,
								"Something went wrong, please try again",
								Toast.LENGTH_LONG).show();
					}
				});
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {

			if (dialog12.isShowing()) {
				dialog12.dismiss();
			}
		}
	}

	private void performCabSearchPrice(Address fromAddress, Address toAddress,
			Double estDistance, Double estDuration) {

		try {
			URL url = new URL(GlobalVariables.ServiceUrl
					+ "/fetchCabDetailsNew.php");
			String response = "";

			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			urlConnection.setReadTimeout(30000);
			urlConnection.setConnectTimeout(30000);
			urlConnection.setRequestMethod("POST");
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);

			OutputStream outputStream = urlConnection.getOutputStream();
			BufferedWriter bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(outputStream, "UTF-8"));
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"dd/MM/yyyy kk:mm:ss");
			bufferedWriter.write("FromCity="
					+ fromAddress.getLocality().toString() + "&ToCity="
					+ toAddress.getLocality().toString() + "&slat="
					+ String.valueOf(fromAddress.getLatitude()) + "&slon="
					+ String.valueOf(fromAddress.getLongitude()) + "&elat="
					+ String.valueOf(toAddress.getLatitude()) + "&elon="
					+ String.valueOf(toAddress.getLongitude()) + "&dist="
					+ estDistance.toString() + "&stime="
					+ simpleDateFormat.format(new Date()) + "&etime="
					+ estDuration.toString());
			bufferedWriter.flush();
			bufferedWriter.close();
			outputStream.close();

			Log.d("performCabSearchPrice", "performCabSearchPrice query : "
					+ GlobalVariables.ServiceUrl + "/fetchCabDetailsNew.php?"
					+ "FromCity=" + fromAddress.getLocality().toString()
					+ "&ToCity=" + toAddress.getLocality().toString()
					+ "&slat=" + String.valueOf(fromAddress.getLatitude())
					+ "&slon=" + String.valueOf(fromAddress.getLongitude())
					+ "&elat=" + String.valueOf(toAddress.getLatitude())
					+ "&elon=" + String.valueOf(toAddress.getLongitude())
					+ "&dist=" + estDistance.toString() + "&stime="
					+ simpleDateFormat.format(new Date()) + "&etime="
					+ estDuration.toString());

			int responseCode = urlConnection.getResponseCode();

			if (responseCode == HttpsURLConnection.HTTP_OK) {

				String line = "";
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(urlConnection.getInputStream()));
				while ((line = bufferedReader.readLine()) != null) {
					response += line;
				}

			} else {
				response = "";
				Log.d("performCabSearchPrice",
						"responseCode != HttpsURLConnection.HTTP_OK : "
								+ responseCode);
				return;
			}

			Log.d("performCabSearchPrice", "performCabSearchPrice response : "
					+ response);

			if (!response.toLowerCase().contains("error")) {

				try {
					JSONArray responseArray = new JSONArray(response);
					if (responseArray.length() > 0) {

						if (rideObject != null) {
							rideObject.estDistance = estDistance.toString();
							rideObject.estDuration = estDuration.toString();
						} else {
							rideObject = new RideObject();
							rideObject.estDistance = estDistance.toString();
							rideObject.estDuration = estDuration.toString();
						}

						mCabSearchArray = new JSONArray();
						for (int i = 0; i < responseArray.length(); i++) {
							mCabSearchArray.put(responseArray.getJSONObject(i));
							// JSONArray temp = responseArray.getJSONArray(i);
							// for (int j = 0; j < temp.length(); j++) {
							// mCabSearchArray.put(temp.getJSONObject(j));
							// }
						}

						runOnUiThread(new Runnable() {

							@Override
							public void run() {

								if (mCabSearchArray.length() > 0) {
									// Default sort by time
									togglePriceTimeSort = true;
									bookcabresultsort
											.setImageDrawable(getResources()
													.getDrawable(
															R.drawable.bookacab_sorticon_rupee));
									JSONArray jsonArray = performSortOnResults(
											mCabSearchArray, "timeEstimate");
									mCabSearchArray = jsonArray;
								}

								updateGridView();
							}
						});
					} else {

					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(BookaCabFragmentActivity.this,
									"Something went wrong, please try again",
									Toast.LENGTH_LONG).show();
						}
					});
				}

			} else {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(
								BookaCabFragmentActivity.this,
								"No cabs found for your location, we are continuously adding new locations",
								Toast.LENGTH_LONG).show();
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(BookaCabFragmentActivity.this,
							"Something went wrong, please try again",
							Toast.LENGTH_LONG).show();
				}
			});
		}

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

	/*
	 * @Override public void onMapClick(LatLng point) {
	 * myMap.animateCamera(CameraUpdateFactory.newLatLng(point)); if (marker !=
	 * null) { marker.remove(); } marker = myMap.addMarker(new
	 * MarkerOptions().position(point).draggable( false)); markerClicked =
	 * false;
	 * 
	 * String address = getAddress(Invite.this, point.latitude,
	 * point.longitude); Log.d("address", "" + address);
	 * 
	 * fromlocation.setText(address); }
	 */


	@Override
	public void onLocationChanged(Location location) {

		Log.d("BookaCab", "onLocationChanged : " + location.toString());

		// mycurrentlocationobject = location;
		setMycurrentlocationobject(location);
		/*
		 * 
		 * if (fromlocation.getText().toString().trim().isEmpty() ||
		 * fromlocation.getText().toString().equalsIgnoreCase("")) { // Getting
		 * latitude of the current location double latitude =
		 * location.getLatitude();
		 * 
		 * // Getting longitude of the current location double longitude =
		 * location.getLongitude();
		 * 
		 * // Creating a LatLng object for the current location LatLng latLng =
		 * new LatLng(latitude, longitude);
		 * 
		 * // Showing the current location in Google Map
		 * myMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		 * 
		 * // Zoom in the Google Map
		 * myMap.animateCamera(CameraUpdateFactory.zoomTo(15));
		 * 
		 * String address = getAddress(Invite.this, latitude, longitude);
		 * Log.d("address", "" + address);
		 * 
		 * ImageView imgbtn = (ImageView) findViewById(R.id.mylocbtn); // your
		 * // button imgbtn.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View view) {
		 * myMap.animateCamera(CameraUpdateFactory.newLatLngZoom( new
		 * LatLng(location.getLatitude(), location .getLongitude()), 15)); } });
		 * 
		 * fromlocation.setText(address);
		 * 
		 * } else {
		 * 
		 * }
		 */}

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
			sb.append("?sensor=false&key=" + GlobalVariables.GoogleMapsAPIKey);
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
			Log.e(LOG_TAG, "Error processing Places API URL" + e);
			return resultList;
		} catch (IOException e) {
			Log.e(LOG_TAG, "Error connecting to Places API" + e);
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
			Log.e("LOG_TAG", "Cannot process JSON results" + e);
		}

		return resultList;
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	protected void ShowAlert() {
		// TODO Auto-generated method stub

		ListViewAdapter adapter;

		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.myridepopup);

		mypoollist = (ListView) dialog.findViewById(R.id.mypoollist);

		adapter = new ListViewAdapter(BookaCabFragmentActivity.this,
				FromLocation, ToLocation, TravelDate, TravelTime, Seat_Status,
				OwnerName, imagename);
		mypoollist.setAdapter(adapter);

		mypoollist.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				from_places.setText(FromLocation.get(pos).toString());
				to_places.setText(ToLocation.get(pos).toString());
				dialog.dismiss();

				fAddress = geocodeAddress(from_places.getText().toString());
				tAddress = geocodeAddress(to_places.getText().toString());

				mButtonSearch.performClick();
			}
		});

		dialog.show();

	}

	/*
	 * public class Listadapters extends BaseAdapter {
	 * 
	 * private ArrayList<String> CabName; private ArrayList<String> CabTime;
	 * private ArrayList<String> CabRate; private ArrayList<String> CabTitle;
	 * private ArrayList<String> CabContact;
	 * 
	 * //private ArrayList<String> CabNRate; // private ArrayList<String>
	 * Cabcontact;
	 * 
	 * // private int[] colors = new int[] { 0x30000000, 0x30FFFFFF };
	 * 
	 * public Listadapters(ArrayList<String> Cabname, ArrayList<String> cabRate,
	 * ArrayList<String> estimateTime,ArrayList<String> CabTitle,
	 * ArrayList<String> cabcontact) {
	 * 
	 * // TODO Auto-generated constructor stub
	 * 
	 * this.CabName = Cabname; this.CabTime = estimateTime; this.CabRate =
	 * cabRate; this.CabTitle = CabTitle; this.CabContact=CabContact;
	 * //Cabcontact = cabcontact;
	 * 
	 * }
	 * 
	 * @Override public View getView(final int position, View convertView,
	 * ViewGroup parent) { // TODO Auto-generated method stub final int pos =
	 * position; LayoutInflater inflater = (LayoutInflater)
	 * getSystemService(Context.LAYOUT_INFLATER_SERVICE); View single_row =
	 * inflater.inflate(R.layout.searchcab_listitem, null, true);
	 * 
	 * 
	 * int colorPos = position % colors.length;
	 * single_row.setBackgroundColor(colors[colorPos]);
	 * 
	 * 
	 * TextView tCabname = (TextView) single_row.findViewById(R.id.CabName);
	 * TextView tPrice = (TextView) single_row.findViewById(R.id.Price);
	 * TextView tcontact = (TextView) single_row.findViewById(R.id.Call);
	 * TextView ttime = (TextView) single_row.findViewById(R.id.Time); TextView
	 * tbook = (TextView) single_row.findViewById(R.id.Bookcab); TextView
	 * cabTitle=(TextView) single_row.findViewById(R.id.CabTitle);
	 * cabTitle.setText(CabTitle.get(position).toString()+" Cabs");
	 * tCabname.setText(CabName.get(position));
	 * tPrice.setText(CabRate.get(position).toString()+" Rs. Approx");
	 * if(Integer.parseInt(CabTime.get(position).toString())>60) { int
	 * t=(Integer.parseInt(CabTime.get(position).toString()))/60;
	 * ttime.setText(t+" Minutes Away ..."); } else { //
	 * ttime.setText(CabTime.get(position).toString()+" Seconds Away ...");
	 * ttime.setText("Not Available ...."); }
	 * 
	 * // tcontact.setText(Cabcontact.get(position)); Double price; //
	 * price=Distance*Integer.parseInt(CabRate.get(position).toString());
	 * 
	 * String[] Dparts = distancetext.split(" "); String distance = Dparts[0];
	 * price = Double.parseDouble(distance) * 15; // String sprice =
	 * Double.toString(price);
	 * 
	 * tcontact.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { // TODO Auto-generated method
	 * stub Intent intent = new Intent(Intent.ACTION_CALL);
	 * intent.setData(Uri.parse("tel:" + Cabcontact.get(pos).toString()));
	 * startActivity(intent); } }); tcontact.setOnClickListener(new
	 * OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { // TODO Auto-generated method
	 * stub Intent intent = new Intent(Intent.ACTION_CALL);
	 * intent.setData(Uri.parse("tel:123456789")); startActivity(intent); } });
	 * 
	 * tbook.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { // TODO Auto-generated method
	 * stub Intent intent = new Intent(BookaCab.this, WebActivity.class);
	 * intent.putExtra("POS", CabName.get(position).toString());
	 * 
	 * startActivity(intent); } });
	 * 
	 * Log.w("MyPool.FriendNamegetview:- ", CabName.get(position)); return
	 * single_row; }
	 * 
	 * @Override public int getCount() { // TODO Auto-generated method stub
	 * return CabName.size(); }
	 * 
	 * @Override public Object getItem(int position) { // TODO Auto-generated
	 * method stub return null; }
	 * 
	 * @Override public long getItemId(int position) { // TODO Auto-generated
	 * method stub return 0; } }
	 */
	/*
	 * public class ListadaptersTime extends BaseAdapter {
	 * 
	 * private ArrayList<String> CabNameT; private ArrayList<String> CabTime;
	 * 
	 * 
	 * // private int[] colors = new int[] { 0x30000000, 0x30FFFFFF };
	 * 
	 * public ListadaptersTime(ArrayList<String> Cabname, ArrayList<String>
	 * cabtime) {
	 * 
	 * // TODO Auto-generated constructor stub
	 * 
	 * this.CabNameT = Cabname; this.CabTime = cabtime;
	 * 
	 * 
	 * }
	 * 
	 * @Override public View getView(final int position, View convertView,
	 * ViewGroup parent) { // TODO Auto-generated method stub final int pos =
	 * position; LayoutInflater inflater = (LayoutInflater)
	 * getSystemService(Context.LAYOUT_INFLATER_SERVICE); View single_row =
	 * inflater.inflate(R.layout.estimatetime_item, null, true);
	 * 
	 * 
	 * int colorPos = position % colors.length;
	 * single_row.setBackgroundColor(colors[colorPos]);
	 * 
	 * TextView tCabName = (TextView)
	 * single_row.findViewById(R.id.nameestimatedtime);
	 * 
	 * TextView tCabtime = (TextView)
	 * single_row.findViewById(R.id.textestimatedtime);
	 * 
	 * 
	 * tCabName.setText(CabNameT.get(position)); int
	 * t=(Integer.parseInt(CabTime.get(position).toString()))/60;
	 * tCabtime.setText(t+"Minutes Away");
	 * 
	 * 
	 * 
	 * Log.w("MyPool.FriendNamegetview:- ", CabNameT.get(position)); return
	 * single_row; }
	 * 
	 * @Override public int getCount() { // TODO Auto-generated method stub
	 * return CabNameT.size(); }
	 * 
	 * @Override public Object getItem(int position) { // TODO Auto-generated
	 * method stub return null; }
	 * 
	 * @Override public long getItemId(int position) { // TODO Auto-generated
	 * method stub return 0; } }
	 */

	// /////////////

	// ///////

	private class ConnectionTaskForDistanceCalculation extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionSendInvite mAuth1 = new AuthenticateConnectionSendInvite();
			try {
				mAuth1.fromloc = args[0];
				mAuth1.toloc = args[1];
				mAuth1.connection();
			} catch (Exception e) {
				// TODO Auto-generated catch block

				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {

			/*
			 * s adapter2 = new Listadapters(CabName, CabCity, CabRate,
			 * CabNRate, Cabcontact); lv.setAdapter(adapter2);
			 */
		}

	}

	public class AuthenticateConnectionSendInvite {

		public String fromloc;
		public String toloc;

		public AuthenticateConnectionSendInvite() {

		}

		public void connection() throws Exception {

			String source = fromloc.replaceAll(" ", "%20");
			String dest = toloc.replaceAll(" ", "%20");

			String url = "https://maps.googleapis.com/maps/api/directions/json?"
					+ "origin="
					+ source
					+ "&destination="
					+ dest
					+ "&sensor=false&units=metric&mode=driving&alternatives=true&key="+GlobalVariables.GoogleMapsAPIKey;

			Log.d("url", "" + url);

			String CompletePageResponse = new Communicator()
					.executeHttpGet(url);

			CompletePageResponse = CompletePageResponse
					.replaceAll("\\\\/", "/");

			JSONObject jsonObject = new JSONObject(CompletePageResponse);

			String name = jsonObject.getString("routes");

			JSONArray subArray = new JSONArray(name);

			for (int i = 0; i < subArray.length(); i++) {

				String name1 = subArray.getJSONObject(i).getString("legs")
						.toString();

				JSONArray subArray1 = new JSONArray(name1);

				for (int i1 = 0; i1 < subArray1.length(); i1++) {

					String startadd = subArray1.getJSONObject(i1)
							.getString("distance").toString();

					JSONObject jsonObject1 = new JSONObject(startadd);
					distancevalue = jsonObject1.getString("value");
					distancetext = jsonObject1.getString("text");
				}
			}

			Log.d("distancevalue", "" + distancevalue);
			Log.d("distancetext", "" + distancetext);
		}
	}

	/*
	 * public StringBuilder getLatLongFromGivenAddress(String youraddress) {
	 * StringBuilder res = null; String uri =
	 * "http://maps.google.com/maps/api/geocode/json?address=" + youraddress +
	 * "&sensor=false"; HttpGet httpGet = new HttpGet(uri); HttpClient client =
	 * new DefaultHttpClient(); HttpResponse response; StringBuilder
	 * stringBuilder = new StringBuilder();
	 * 
	 * try { response = client.execute(httpGet); HttpEntity entity =
	 * response.getEntity(); InputStream stream = entity.getContent(); int b;
	 * while ((b = stream.read()) != -1) { stringBuilder.append((char) b); } }
	 * catch (ClientProtocolException e) { e.printStackTrace(); } catch
	 * (IOException e) { e.printStackTrace(); }
	 * 
	 * JSONObject jsonObject = new JSONObject(); try { jsonObject = new
	 * JSONObject(stringBuilder.toString());
	 * 
	 * Double lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
	 * .getJSONObject("geometry").getJSONObject("location") .getDouble("lng");
	 * 
	 * Double lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
	 * .getJSONObject("geometry").getJSONObject("location") .getDouble("lat");
	 * 
	 * Log.d("latitude", String.valueOf(lng)); Log.d("longitude",
	 * String.valueOf(lat));
	 * 
	 * res.append(lat); res.append('|'); res.append(lng); }
	 * 
	 * catch (JSONException e) { e.printStackTrace(); } return res; }
	 */

	public class Fetch_EstimatePrice extends AsyncTask<String, Void, String> {

		String result;

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(String... args) {

			DefaultHttpClient httpclient = new DefaultHttpClient();

			Log.d("FLT", "" + tlatitude);
			String url = GlobalVariables.ServiceUrl
					+ "/uberConnect.php?type=priceestimates&lat=" + flatitude
					+ "&lon=" + flongitude + "&elat=" + tlatitude + "&elon="
					+ tlongitude + "";
			Log.d("url", url);
			HttpGet httpget = new HttpGet(GlobalVariables.ServiceUrl
					+ "/uberConnect.php?type=priceestimates&lat=" + flatitude
					+ "&lon=" + flongitude + "&elat=" + tlatitude + "&elon="
					+ tlongitude + "");
			HttpResponse response = null;
			BufferedReader in = null;
			try {
				response = httpclient.execute(httpget);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				in = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			try {
				while ((line = in.readLine()) != null) {
					sb.append(line + NL);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String result = sb.toString();
			Log.d("My Response :: ", result);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {

		}
	}

	public class Fetch_EstimateTime extends AsyncTask<String, Void, String> {

		String result;

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(String... args) {

			DefaultHttpClient httpclient = new DefaultHttpClient();

			Log.d("FLT", "" + tlatitude);
			String url = GlobalVariables.ServiceUrl
					+ "/uberConnect.php?type=timeestimates&lat=" + flatitude
					+ "&lon=" + flongitude + "";
			Log.d("url", url);
			HttpGet httpget = new HttpGet(GlobalVariables.ServiceUrl
					+ "/uberConnect.php?type=timeestimates&lat=" + flatitude
					+ "&lon=" + flongitude + "");
			HttpResponse response = null;
			BufferedReader in = null;
			try {
				response = httpclient.execute(httpget);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				in = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			try {
				while ((line = in.readLine()) != null) {
					sb.append(line + NL);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String result = sb.toString();
			Log.d("My Response :: ", result);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {

		}
	}

	public class ListadaptersTime extends BaseAdapter {

		private ArrayList<String> CabName;
		private ArrayList<String> EstimateTime;
		private ArrayList<String> CabTitle;
		private ArrayList<String> Cabcontact;
		private ArrayList<String> ImageId;

		// private ArrayList<String> CabNRate;
		// private ArrayList<String> Cabcontact;

		// private int[] colors = new int[] { 0x30000000, 0x30FFFFFF };

		public ListadaptersTime(ArrayList<String> Cabname,
				ArrayList<String> estimateTime, ArrayList<String> CabTitle,
				ArrayList<String> cabcontact, ArrayList<String> imageId) {

			// TODO Auto-generated constructor stub

			this.CabName = Cabname;
			this.EstimateTime = estimateTime;
			this.CabTitle = CabTitle;
			this.Cabcontact = cabcontact;
			this.ImageId = imageId;

		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			final int pos = position;
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View single_row = inflater.inflate(R.layout.searchcab_listitem,
					null, true);

			/*
			 * int colorPos = position % colors.length;
			 * single_row.setBackgroundColor(colors[colorPos]);
			 */

			TextView tCabname = (TextView) single_row
					.findViewById(R.id.CabName);
			// TextView tPrice = (TextView) single_row.findViewById(R.id.Price);
			TextView tcontact = (TextView) single_row.findViewById(R.id.Call);
			TextView ttime = (TextView) single_row.findViewById(R.id.Time);
			TextView tbook = (TextView) single_row.findViewById(R.id.Bookcab);
			TextView cabTitle = (TextView) single_row
					.findViewById(R.id.CabTitle);
			ImageView img = (ImageView) single_row.findViewById(R.id.CabImage);
			// img.setBackgroundDrawable(ImageId.get(position).toString());
			cabTitle.setText(CabTitle.get(position).toString() + " Cabs");
			tCabname.setText(CabName.get(position));

			if (Integer.parseInt(EstimateTime.get(position).toString()) > 60) {
				int t = (Integer
						.parseInt(EstimateTime.get(position).toString())) / 60;
				ttime.setText(t + " Minutes Away ...");
			} else {
				int i = 10;
				// ttime.setText(EstimatePrice.get(position).toString()+" Seconds Away ...");
				ttime.setText(i + " Minutes Away ...");
				i = i * 2;
			}

			tcontact.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(Intent.ACTION_CALL);
					intent.setData(Uri.parse("tel:"
							+ Cabcontact.get(position).toString()));
					startActivity(intent);
				}
			});

			tbook.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Toast.makeText(
							getApplicationContext(),
							"Please Select Detinaton Place Before Book a Cab !!",
							Toast.LENGTH_LONG).show();
				}
			});

			Log.d("MyPool.FriendNamegetview:- ", CabName.get(position));
			return single_row;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return CabName.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
	}

	private void updateGridView() {

		if (mCabSearchArray.length() > 0
				&& to_places.getText().toString().length() > 0) {
			bookcabresultsort.setVisibility(View.VISIBLE);
		}

		mGridViewCabSearch.setAdapter(new CustomGridViewCabSearchAdapter(this,
				mCabSearchArray));
		mGridViewCabSearch
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							final int position, long id) {
						// Log.d("BookaCab",
						// "mGridViewCabSearch onItemClick position : " +
						// position);

						AlertDialog.Builder builder = new AlertDialog.Builder(
								BookaCabFragmentActivity.this);
						View builderView = (View) getLayoutInflater().inflate(
								R.layout.book_cab_detail_dialog, null);

						final EditText editTextUserName = (EditText) builderView
								.findViewById(R.id.editTextBookCabUserName);
						final EditText editTextPassword = (EditText) builderView
								.findViewById(R.id.editTextBookCabPassword);

						String jsonString = "";
						TextView textView = (TextView) builderView
								.findViewById(R.id.textViewCabName);
						try {
							jsonString = mCabSearchArray
									.getJSONObject(position).get("CabName")
									.toString();
							textView.setText((jsonString.isEmpty() || jsonString
									.equalsIgnoreCase("null")) ? "-"
									: jsonString);
						} catch (Exception e) {
							textView.setText("-");
						}
						textView = (TextView) builderView
								.findViewById(R.id.textViewCabType);
						try {
							jsonString = mCabSearchArray
									.getJSONObject(position).get("CarType")
									.toString();
							textView.setText((jsonString.isEmpty() || jsonString
									.equalsIgnoreCase("null")) ? "-"
									: jsonString);
						} catch (Exception e) {
							textView.setText("-");
						}
						textView = (TextView) builderView
								.findViewById(R.id.textViewEstTime);
						try {
							textView.setText("Est. time: "
									+ String.format("%d%n", Math.round(Double
											.parseDouble(mCabSearchArray
													.getJSONObject(position)
													.get("timeEstimate")
													.toString()) / 60))
									+ "mins");
						} catch (Exception e) {
							textView.setText("Est. time: -");
						}
						textView = (TextView) builderView
								.findViewById(R.id.textViewEstPrice);
						try {
							String lowString = mCabSearchArray
									.getJSONObject(position)
									.get("low_estimate").toString();
							String highString = mCabSearchArray
									.getJSONObject(position)
									.get("high_estimate").toString();
							if (lowString.isEmpty() || highString.isEmpty()
									|| lowString.equalsIgnoreCase("na")
									|| lowString.equalsIgnoreCase("null")
									|| highString.equalsIgnoreCase("na")
									|| highString.equalsIgnoreCase("null")) {
								textView.setText("Est. price: " + "-");
							} else {
								textView.setText("Est. price: \u20B9"
										+ mCabSearchArray
												.getJSONObject(position)
												.get("low_estimate").toString()
										+ "-"
										+ mCabSearchArray
												.getJSONObject(position)
												.get("high_estimate")
												.toString());
							}

						} catch (Exception e) {
							textView.setText("Est. price: " + "-");
						}
						textView = (TextView) builderView
								.findViewById(R.id.textViewBookCabBaseFare);
						try {
							String baseFareString = mCabSearchArray
									.getJSONObject(position).get("BaseFare")
									.toString();
							String baseKmString = mCabSearchArray
									.getJSONObject(position).get("BaseFareKM")
									.toString();
							if (baseFareString.isEmpty()
									|| baseFareString.equalsIgnoreCase("na")
									|| baseFareString.equalsIgnoreCase("null")
									|| baseKmString.isEmpty()
									|| baseKmString.equalsIgnoreCase("na")
									|| baseKmString.equalsIgnoreCase("null")) {
								textView.setText("-");
							} else {
								textView.setText("\u20B9"
										+ mCabSearchArray
												.getJSONObject(position)
												.get("BaseFare").toString()
										+ " for first "
										+ mCabSearchArray
												.getJSONObject(position)
												.get("BaseFareKM").toString()
										+ "Kms");
							}
						} catch (Exception e) {
							textView.setText("-");
						}
						textView = (TextView) builderView
								.findViewById(R.id.textViewBookCabRatePerKM);
						try {
							jsonString = mCabSearchArray
									.getJSONObject(position)
									.get("RatePerKMAfterBaseFare").toString();
							if (jsonString.isEmpty()
									|| jsonString.equalsIgnoreCase("na")
									|| jsonString.equalsIgnoreCase("null")) {
								textView.setText("-");
							} else {
								textView.setText("\u20B9" + jsonString
										+ " per Km");
							}
						} catch (Exception e) {
							textView.setText("-");
						}
						textView = (TextView) builderView
								.findViewById(R.id.textViewBookCabNightBase);
						try {
							Double multiplier = Double
									.parseDouble(mCabSearchArray
											.getJSONObject(position)
											.get("NightTimeRateMultiplier")
											.toString());
							Long nightRate = Math.round(multiplier
									* Double.parseDouble(mCabSearchArray
											.getJSONObject(position)
											.get("BaseFare").toString()));

							textView.setText("\u20B9"
									+ Long.toString(nightRate)
									+ " for first "
									+ mCabSearchArray.getJSONObject(position)
											.get("BaseFareKM").toString()
									+ "Kms");
						} catch (Exception e) {
							textView.setText("-");
						}
						textView = (TextView) builderView
								.findViewById(R.id.textViewBookCabNightPer);
						try {
							Double multiplier = Double
									.parseDouble(mCabSearchArray
											.getJSONObject(position)
											.get("NightTimeRateMultiplier")
											.toString());
							Long nightRate = Math.round(multiplier
									* Double.parseDouble(mCabSearchArray
											.getJSONObject(position)
											.get("RatePerKMAfterBaseFare")
											.toString()));

							textView.setText("\u20B9"
									+ Long.toString(nightRate) + " per Km");
						} catch (Exception e) {
							textView.setText("-");
						}
						textView = (TextView) builderView
								.findViewById(R.id.textViewBookCabNightTime);
						try {
							String startString = mCabSearchArray
									.getJSONObject(position)
									.get("NightTimeStartHours").toString();
							String endString = mCabSearchArray
									.getJSONObject(position)
									.get("NightTimeEndHours").toString();
							if (startString.isEmpty()
									|| startString.equalsIgnoreCase("na")
									|| startString.equalsIgnoreCase("null")
									|| endString.isEmpty()
									|| endString.equalsIgnoreCase("na")
									|| endString.equalsIgnoreCase("null")) {
								textView.setText("-");
							} else {
								textView.setText(mCabSearchArray
										.getJSONObject(position)
										.get("NightTimeStartHours").toString()
										+ " hrs - "
										+ mCabSearchArray
												.getJSONObject(position)
												.get("NightTimeEndHours")
												.toString() + " hrs");
							}
						} catch (Exception e) {
							textView.setText("-");
						}

						RatingBar ratingBar = (RatingBar) builderView
								.findViewById(R.id.ratingBarBookCab);
						try {

							ratingBar.setRating(Float
									.parseFloat(mCabSearchArray
											.getJSONObject(position)
											.get("Rating").toString()));

						} catch (Exception e) {
							e.printStackTrace();
						}

						textView = (TextView) builderView
								.findViewById(R.id.textViewNumberOfRatings);

						try {
							if (mCabSearchArray.getJSONObject(position)
									.get("NoofReviews").toString().equals("0")) {
								textView.setText("");
							} else {
								textView.setText("("
										+ mCabSearchArray
												.getJSONObject(position)
												.get("NoofReviews").toString()
										+ ")");
							}
						} catch (Exception e) {
							textView.setText("");
						}

						ImageView imageView = (ImageView) builderView
								.findViewById(R.id.imageButtonCallNow);
						imageView
								.setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View view) {
										try {
											String phoneString = mCabSearchArray
													.getJSONObject(position)
													.get("CabContactNo")
													.toString();
											if (phoneString.isEmpty()
													|| phoneString
															.equalsIgnoreCase("null")
													|| phoneString
															.equalsIgnoreCase("na")) {
												Toast.makeText(
														BookaCabFragmentActivity.this,
														"Phone number could not be retrieved",
														Toast.LENGTH_LONG)
														.show();
											} else {
												updateCMCRecords(
														mCabSearchArray
																.getJSONObject(
																		position)
																.get("CabNameID")
																.toString(),
														mCabSearchArray
																.getJSONObject(
																		position)
																.get("CarType")
																.toString(),
														"3", fAddress,
														tAddress, "", "", true,
														false, phoneString, "",
														"", false);
											}
										} catch (Exception e) {
											// TODO: handle exception
											e.printStackTrace();
											Toast.makeText(
													BookaCabFragmentActivity.this,
													"Phone number could not be retrieved",
													Toast.LENGTH_LONG).show();
										}
									}
								});

						try {
							CabUserCredentialsReadWrite cabUserCredentialsReadWrite = new CabUserCredentialsReadWrite(
									BookaCabFragmentActivity.this);
							JSONArray jsonArray = cabUserCredentialsReadWrite
									.readArrayFromFile();
							JSONObject jsonObject = new JSONObject();

							if (mCabSearchArray.getJSONObject(position)
									.get("CabName").toString().toLowerCase()
									.contains("uber")) {
								// editTextUserName.setVisibility(View.VISIBLE);
								// editTextPassword.setVisibility(View.VISIBLE);

								// Log.d("BookaCab", "contains(uber) : " +
								// cabUserCredentialsReadWrite.readArrayFromFile());

								// try {
								// for (int i = 0; i < jsonArray.length(); i++)
								// {
								// if (jsonArray
								// .getJSONObject(i)
								// .get("CabName")
								// .toString()
								// .equals(CabUserCredentialsReadWrite.KEY_JSON_CAB_NAME_UBER))
								// {
								// jsonObject = jsonArray
								// .getJSONObject(i);
								// editTextUserName
								// .setText(jsonObject.get(
								// "Username")
								// .toString());
								// editTextPassword
								// .setText(jsonObject.get(
								// "Password")
								// .toString());
								// }
								// }
								// } catch (Exception e) {
								// // TODO: handle exception
								// }
							} else if (mCabSearchArray.getJSONObject(position)
									.get("CabName").toString().toLowerCase()
									.contains("mega")) {
								// editTextUserName.setVisibility(View.VISIBLE);
								// editTextPassword.setVisibility(View.VISIBLE);
								//
								// // Log.d("BookaCab", "contains(uber) : " +
								// //
								// cabUserCredentialsReadWrite.readArrayFromFile());
								//
								// try {
								// for (int i = 0; i < jsonArray.length(); i++)
								// {
								// if (jsonArray
								// .getJSONObject(i)
								// .get("CabName")
								// .toString()
								// .equals(CabUserCredentialsReadWrite.KEY_JSON_CAB_NAME_MEGA))
								// {
								// jsonObject = jsonArray
								// .getJSONObject(i);
								// editTextUserName
								// .setText(jsonObject.get(
								// "Username")
								// .toString());
								// editTextPassword
								// .setText(jsonObject.get(
								// "Password")
								// .toString());
								// }
								// }
								// } catch (Exception e) {
								// // TODO: handle exception
								// }
							}
						} catch (Exception e) {

						}
						Button button = (Button) builderView
								.findViewById(R.id.buttonBookNow);
						button.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View view) {

								dismissKeyboard();

								try {

									if (mCabSearchArray.getJSONObject(position)
											.get("CabName").toString()
											.toLowerCase().contains("uber")) {

										// if (editTextUserName.getText()
										// .toString().isEmpty()
										// || editTextPassword.getText()
										// .toString().isEmpty()) {
										// Toast.makeText(
										// BookaCab.this,
										// "Please enter Username/Password",
										// Toast.LENGTH_LONG).show();
										// } else {
										// JSONObject jsonObject = new
										// JSONObject();
										// jsonObject
										// .put("CabName",
										// CabUserCredentialsReadWrite.KEY_JSON_CAB_NAME_UBER);
										// jsonObject.put("Username",
										// editTextUserName.getText()
										// .toString());
										// jsonObject.put("Password",
										// editTextPassword.getText()
										// .toString());
										//
										// CabUserCredentialsReadWrite
										// cabUserCredentialsReadWrite = new
										// CabUserCredentialsReadWrite(
										// BookaCab.this);
										// cabUserCredentialsReadWrite
										// .saveToFile(jsonObject
										// .toString());
										//
										// mUberUsername = editTextUserName
										// .getText().toString();
										// mUberPassword = editTextPassword
										// .getText().toString();
										// }

										cabBookUberMegaPosition = position;
										bookUberCab(
												mCabSearchArray
														.getJSONObject(position)
														.get("CabName")
														.toString(),
												mCabSearchArray
														.getJSONObject(position)
														.get("productId")
														.toString(), fAddress,
												tAddress);

									} else if (mCabSearchArray
											.getJSONObject(position)
											.get("CabName").toString()
											.toLowerCase().contains("mega")) {

										// if (editTextUserName.getText()
										// .toString().isEmpty()
										// || editTextPassword.getText()
										// .toString().isEmpty()) {
										// Toast.makeText(
										// BookaCab.this,
										// "Please enter Username/Password",
										// Toast.LENGTH_LONG).show();
										// } else {
										// JSONObject jsonObject = new
										// JSONObject();
										// jsonObject
										// .put("CabName",
										// CabUserCredentialsReadWrite.KEY_JSON_CAB_NAME_MEGA);
										// jsonObject.put("Username",
										// editTextUserName.getText()
										// .toString());
										// jsonObject.put("Password",
										// editTextPassword.getText()
										// .toString());
										//
										// CabUserCredentialsReadWrite
										// cabUserCredentialsReadWrite = new
										// CabUserCredentialsReadWrite(
										// BookaCab.this);
										// cabUserCredentialsReadWrite
										// .saveToFile(jsonObject
										// .toString());
										//
										//
										// }
										cabBookUberMegaPosition = position;
										bookMegaCab(fAddress, tAddress);

									} else {

										updateCMCRecords(
												mCabSearchArray
														.getJSONObject(position)
														.get("CabNameID")
														.toString(),
												mCabSearchArray
														.getJSONObject(position)
														.get("CarType")
														.toString(),
												"2",
												fAddress,
												tAddress,
												"",
												"",
												false,
												true,
												"",
												"http://"
														+ mCabSearchArray
																.getJSONObject(
																		position)
																.get("CabMobileSite")
																.toString(),
												mCabSearchArray
														.getJSONObject(position)
														.get("CabPackageName")
														.toString(), false);
									}

								} catch (Exception e) {
									// TODO: handle exception
									e.printStackTrace();
								}
							}
						});

						builder.setView(builderView);
						AlertDialog dialog = builder.create();

						dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {

							@Override
							public void onDismiss(DialogInterface dialog) {
								Log.d("BookaCab", "dialog onDismiss");
								mGridViewCabSearch
										.setFocusableInTouchMode(true);
								mGridViewCabSearch.requestFocus();
								dismissKeyboard();
							}
						});

						dialog.show();
					}
				});

		mGridViewCabSearch.setFocusableInTouchMode(true);
		mGridViewCabSearch.requestFocus();
	}

	private class CustomGridViewCabSearchAdapter extends BaseAdapter {

		private Activity mContext;
		private JSONArray mEntries;

		public CustomGridViewCabSearchAdapter(Activity context,
				JSONArray entries) {
			this.mContext = context;
			this.mEntries = entries;
		}

		@Override
		public int getCount() {
			return mEntries.length();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// Log.d(TAG, "getView : " + position);
			// if (convertView == null) {
			// convertView =
			// (View)mContext.getLayoutInflater().inflate(R.layout.cell_grid_view_cabsearch,
			// parent, false);
			// }
			convertView = (View) mContext.getLayoutInflater().inflate(
					R.layout.cell_grid_view_cabsearch, parent, false);
			String jsonString = "";
			try {
				TextView textView = (TextView) convertView
						.findViewById(R.id.textViewCabName);
				jsonString = mEntries.getJSONObject(position).get("CabName")
						.toString();
				textView.setText((jsonString.isEmpty() || jsonString
						.equalsIgnoreCase("null")) ? "-" : jsonString);

				textView = (TextView) convertView
						.findViewById(R.id.textViewCabType);
				try {
					jsonString = mEntries.getJSONObject(position)
							.get("CarType").toString();
					textView.setText((jsonString.isEmpty() || jsonString
							.equalsIgnoreCase("null")) ? "-" : jsonString);
				} catch (Exception e) {
					textView.setText("-");
				}

				textView = (TextView) convertView
						.findViewById(R.id.textViewEstTime);
				try {
					textView.setText("Est. time: "
							+ String.format("%d%n",
									Math.round(Double
											.parseDouble(mCabSearchArray
													.getJSONObject(position)
													.get("timeEstimate")
													.toString()) / 60))
							+ "mins");
				} catch (Exception e) {
					textView.setText("Est. time: -");
				}

				textView = (TextView) convertView
						.findViewById(R.id.textViewEstPrice);
				try {
					String lowString = mEntries.getJSONObject(position)
							.get("low_estimate").toString();
					String highString = mEntries.getJSONObject(position)
							.get("high_estimate").toString();
					if (lowString.isEmpty() || highString.isEmpty()
							|| lowString.equalsIgnoreCase("na")
							|| lowString.equalsIgnoreCase("null")
							|| highString.equalsIgnoreCase("na")
							|| highString.equalsIgnoreCase("null")) {
						textView.setText("Est. price: " + "-");
					} else {
						textView.setText("Est. price: \u20B9"
								+ mEntries.getJSONObject(position)
										.get("low_estimate").toString()
								+ "-"
								+ mEntries.getJSONObject(position)
										.get("high_estimate").toString());
					}

				} catch (Exception e) {
					textView.setText("Est. price: " + "-");
				}

				RatingBar ratingBar = (RatingBar) convertView
						.findViewById(R.id.ratingBarBookCab);
				try {

					ratingBar.setRating(Float.parseFloat(mEntries
							.getJSONObject(position).get("Rating").toString()));

				} catch (Exception e) {
					// e.printStackTrace();
				}

				textView = (TextView) convertView
						.findViewById(R.id.textViewNumberOfRatings);

				try {
					if (mEntries.getJSONObject(position).get("NoofReviews")
							.toString().equals("0")) {
						textView.setText("");
					} else {
						textView.setText("("
								+ mEntries.getJSONObject(position)
										.get("NoofReviews").toString() + ")");
					}
				} catch (Exception e) {
					textView.setText("");
				}

				// Button button = (Button) convertView
				// .findViewById(R.id.buttonBookNow);
				// button.setTag("BookNowButton" + position);
				// button.setOnClickListener(new View.OnClickListener() {
				//
				// @Override
				// public void onClick(View view) {
				// int pos = Integer.parseInt(view.getTag().toString()
				// .replace("BookNowButton", ""));
				// try {
				//
				// if (mEntries.getJSONObject(pos).get("CabName")
				// .toString().toLowerCase().contains("uber")) {
				// // Log.d("BookaCab",
				// // "BookNowButton fAddress : " +
				// // fAddress.toString() + " tAddress : " +
				// // tAddress.toString());
				// bookUberCab(
				// mEntries.getJSONObject(pos)
				// .get("CabName").toString(),
				// mEntries.getJSONObject(pos)
				// .get("productId").toString(),
				// fAddress, tAddress);
				// } else {
				// openAppOrMSite(
				// mEntries.getJSONObject(pos)
				// .get("CabPackageName")
				// .toString(), "http://"
				// + mEntries.getJSONObject(pos)
				// .get("CabMobileSite")
				// .toString());
				// }
				//
				// } catch (Exception e) {
				// // TODO: handle exception
				// e.printStackTrace();
				// }
				// }
				// });
				//
				// ImageView imageButton = (ImageView) convertView
				// .findViewById(R.id.imageButtonCallNow);
				// imageButton.setTag("CallNowButton" + position);
				// imageButton.setOnClickListener(new View.OnClickListener() {
				//
				// @Override
				// public void onClick(View view) {
				// int pos = Integer.parseInt(view.getTag().toString()
				// .replace("CallNowButton", ""));
				// try {
				// String phoneString = mEntries.getJSONObject(pos)
				// .get("CabContactNo").toString();
				// if (phoneString.isEmpty()
				// || phoneString.equalsIgnoreCase("null")
				// || phoneString.equalsIgnoreCase("na")) {
				// Toast.makeText(BookaCab.this,
				// "Phone number could not be retrieved",
				// Toast.LENGTH_LONG).show();
				// } else {
				// Intent intent = new Intent(Intent.ACTION_CALL,
				// Uri.parse("tel:" + phoneString));
				// startActivity(intent);
				// }
				// } catch (Exception e) {
				// // TODO: handle exception
				// e.printStackTrace();
				// Toast.makeText(BookaCab.this,
				// "Phone number could not be retrieved",
				// Toast.LENGTH_LONG).show();
				// }
				// }
				// });
				//
				// LinearLayout linearLayout = (LinearLayout) convertView
				// .findViewById(R.id.linearLayoutFareChart);
				// linearLayout.setVisibility(View.GONE);
				// linearLayout.setTag("FareChartLayout" + position);
				//
				// imageButton = (ImageView) convertView
				// .findViewById(R.id.imageButtonFareChart);
				// imageButton.setTag("FareChartButton" + position);
				// imageButton.setOnClickListener(new View.OnClickListener() {
				//
				// @Override
				// public void onClick(View view) {
				// // Log.d(TAG, "convertView onClick : " + view.getTag() +
				// // " getParent : " + view.getParent().toString() +
				// // " getRootView : " + view.getRootView().toString());
				// // Log.d(TAG, "" +
				// //
				// view.getRootView().findViewWithTag(view.getTag().toString().replace("FareChartButton",
				// // "FareChartLayout")));
				// LinearLayout linearLayout = (LinearLayout) view
				// .getRootView().findViewWithTag(
				// view.getTag()
				// .toString()
				// .replace("FareChartButton",
				// "FareChartLayout"));
				// if (linearLayout.isShown()) {
				// linearLayout.setVisibility(View.GONE);
				// } else {
				// linearLayout.setVisibility(View.VISIBLE);
				// }
				// }
				// });
				//
				// textView = (TextView) convertView
				// .findViewById(R.id.textViewBookCabBaseFare);
				// try {
				// String baseFareString = mEntries.getJSONObject(position)
				// .get("BaseFare").toString();
				// String baseKmString = mEntries.getJSONObject(position)
				// .get("BaseFareKM").toString();
				// if (baseFareString.isEmpty()
				// || baseFareString.equalsIgnoreCase("na")
				// || baseFareString.equalsIgnoreCase("null")
				// || baseKmString.isEmpty()
				// || baseKmString.equalsIgnoreCase("na")
				// || baseKmString.equalsIgnoreCase("null")) {
				// textView.setText("-");
				// } else {
				// textView.setText("\u20B9"
				// + mEntries.getJSONObject(position)
				// .get("BaseFare").toString()
				// + " for first "
				// + mEntries.getJSONObject(position)
				// .get("BaseFareKM").toString() + "Kms");
				// }
				// } catch (Exception e) {
				// textView.setText("-");
				// }
				//
				// textView = (TextView) convertView
				// .findViewById(R.id.textViewBookCabRatePerKM);
				// try {
				// jsonString = mEntries.getJSONObject(position)
				// .get("RatePerKMAfterBaseFare").toString();
				// if (jsonString.isEmpty()
				// || jsonString.equalsIgnoreCase("na")
				// || jsonString.equalsIgnoreCase("null")) {
				// textView.setText("-");
				// } else {
				// textView.setText("\u20B9" + jsonString + " per Km");
				// }
				// } catch (Exception e) {
				// textView.setText("-");
				// }
				//
				// textView = (TextView) convertView
				// .findViewById(R.id.textViewBookCabNightBase);
				// try {
				// Double multiplier = Double.parseDouble(mEntries
				// .getJSONObject(position)
				// .get("NightTimeRateMultiplier").toString());
				// Long nightRate = Math.round(multiplier
				// * Double.parseDouble(mEntries
				// .getJSONObject(position).get("BaseFare")
				// .toString()));
				//
				// textView.setText("\u20B9"
				// + Long.toString(nightRate)
				// + " for first "
				// + mEntries.getJSONObject(position)
				// .get("BaseFareKM").toString() + "Kms");
				// } catch (Exception e) {
				// textView.setText("-");
				// }
				//
				// textView = (TextView) convertView
				// .findViewById(R.id.textViewBookCabNightPer);
				// try {
				// Double multiplier = Double.parseDouble(mEntries
				// .getJSONObject(position)
				// .get("NightTimeRateMultiplier").toString());
				// Long nightRate = Math.round(multiplier
				// * Double.parseDouble(mEntries
				// .getJSONObject(position)
				// .get("RatePerKMAfterBaseFare").toString()));
				//
				// textView.setText("\u20B9" + Long.toString(nightRate)
				// + " per Km");
				// } catch (Exception e) {
				// textView.setText("-");
				// }
				//
				// textView = (TextView) convertView
				// .findViewById(R.id.textViewBookCabNightTime);
				// try {
				// String startString = mEntries.getJSONObject(position)
				// .get("NightTimeStartHours").toString();
				// String endString = mEntries.getJSONObject(position)
				// .get("NightTimeEndHours").toString();
				// if (startString.isEmpty()
				// || startString.equalsIgnoreCase("na")
				// || startString.equalsIgnoreCase("null")
				// || endString.isEmpty()
				// || endString.equalsIgnoreCase("na")
				// || endString.equalsIgnoreCase("null")) {
				// textView.setText("-");
				// } else {
				// textView.setText(mEntries.getJSONObject(position)
				// .get("NightTimeStartHours").toString()
				// + " hrs - "
				// + mEntries.getJSONObject(position)
				// .get("NightTimeEndHours").toString()
				// + " hrs");
				// }
				// } catch (Exception e) {
				// textView.setText("-");
				// }

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			return convertView;
		}

	}

	private void openAppOrMSite(final String packageName, String mSite) {

		if (checkIfAppInstalled(packageName)) {

			Intent launchIntent = getPackageManager()
					.getLaunchIntentForPackage(packageName);
			startActivity(launchIntent);

		} else {

			if (mSite.isEmpty() || mSite.equalsIgnoreCase("null")
					|| mSite.equalsIgnoreCase("na")) {
				Toast.makeText(this, "Sorry, no booking information available",
						Toast.LENGTH_LONG).show();
			} else {
				Intent intent = new Intent(this, MobileSiteActivity.class);
				intent.putExtra(MobileSiteFragment.ARGUMENTS_MOBILE_SITE_URL,
						mSite);
				startActivity(intent);
			}
		}
	}

	private boolean checkIfAppInstalled(String packageName) {

		PackageManager packageManager = getPackageManager();

		try {
			packageManager.getPackageInfo(packageName,
					PackageManager.GET_ACTIVITIES);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private void bookUberCab(final String cabType, final String productID,
			final Address startAddress, final Address endAddress) {

		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					BookaCabFragmentActivity.this);
			builder.setTitle("Internet Connection Error");
			builder.setMessage("ClubMyCab requires Internet connection");
			builder.setPositiveButton("OK", null);
			AlertDialog dialog = builder.show();
			TextView messageText = (TextView) dialog
					.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
			dialog.show();

			return;
		} else if (startAddress == null || endAddress == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					BookaCabFragmentActivity.this);
			builder.setTitle("");
			builder.setMessage("Please provide both From & To locations to make a booking.");
			builder.setPositiveButton("OK", null);
			AlertDialog dialog = builder.show();
			TextView messageText = (TextView) dialog
					.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
			dialog.show();

			return;
		}

		GetUberRequestIDAsync getUberRequestIDAsync = new GetUberRequestIDAsync();
		String param = "cabType=" + cabType + "&productid=" + productID
				+ "&lat=" + String.valueOf(startAddress.getLatitude())
				+ "&lon=" + String.valueOf(startAddress.getLongitude())
				+ "&elat=" + String.valueOf(endAddress.getLatitude())
				+ "&elon=" + String.valueOf(endAddress.getLongitude())
				+ "&cabID=";
		mUberBookingInputParams = "&productid=" + productID + "&lat="
				+ String.valueOf(startAddress.getLatitude()) + "&lon="
				+ String.valueOf(startAddress.getLongitude()) + "&elat="
				+ String.valueOf(endAddress.getLatitude()) + "&elon="
				+ String.valueOf(endAddress.getLongitude());
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getUberRequestIDAsync.executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, param);
		} else {
			getUberRequestIDAsync.execute(param);
		}

	}

	public class GetUberRequestIDAsync extends AsyncTask<String, Void, String> {

		String result;

		@Override
		protected void onPreExecute() {
			dialog12 = new ProgressDialog(BookaCabFragmentActivity.this);

			dialog12.setMessage("Please Wait...");
			dialog12.setCancelable(false);
			dialog12.setCanceledOnTouchOutside(false);
			dialog12.show();
		}

		@Override
		protected String doInBackground(String... args) {
			Log.d("GetUberRequestIDAsync",
					"GetUberRequestIDAsync : " + args[0].toString());

			try {
				URL url = new URL(GlobalVariables.ServiceUrl
						+ "/cabbookrequest.php");
				String response = "";

				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				urlConnection.setReadTimeout(30000);
				urlConnection.setConnectTimeout(30000);
				urlConnection.setRequestMethod("POST");
				urlConnection.setDoInput(true);
				urlConnection.setDoOutput(true);

				OutputStream outputStream = urlConnection.getOutputStream();
				BufferedWriter bufferedWriter = new BufferedWriter(
						new OutputStreamWriter(outputStream, "UTF-8"));
				bufferedWriter.write(args[0].toString());
				bufferedWriter.flush();
				bufferedWriter.close();
				outputStream.close();

				int responseCode = urlConnection.getResponseCode();

				if (responseCode == HttpsURLConnection.HTTP_OK) {

					String line = "";
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(
									urlConnection.getInputStream()));
					while ((line = bufferedReader.readLine()) != null) {
						response += line;
					}

				} else {
					response = "";
					Log.d("PerformCabSearchTimeAsync",
							"responseCode != HttpsURLConnection.HTTP_OK : "
									+ responseCode);
					result = response;
				}

				Log.d("PerformCabSearchTimeAsync",
						"performCabSearchTime response : " + response);
				result = response;
			} catch (Exception e) {
				e.printStackTrace();
				result = "";
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(BookaCabFragmentActivity.this,
								"Something went wrong, please try again",
								Toast.LENGTH_LONG).show();
					}
				});
			}

			if (!result.isEmpty()) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Intent intent = new Intent(
								BookaCabFragmentActivity.this,
								MobileSiteActivity.class);
						intent.putExtra(
								MobileSiteFragment.ARGUMENTS_MOBILE_SITE_URL,
								GlobalVariables.ServiceUrl
										+ "/uberapi.php?type=oauth");
						intent.putExtra(
								MobileSiteFragment.ARGUMENTS_UBER_REQUEST_ID,
								result);
						intent.putExtra(
								MobileSiteFragment.ARGUMENTS_UBER_USERNAME,
								mUberUsername);
						intent.putExtra(
								MobileSiteFragment.ARGUMENTS_UBER_PASSWORD,
								mUberPassword);

						// startActivity(intent);
						startActivityForResult(intent, 1);
					}
				});

			} else {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(BookaCabFragmentActivity.this,
								"Something went wrong, please try again",
								Toast.LENGTH_LONG).show();
					}
				});
			}

			return result;
		}

		@Override
		protected void onPostExecute(String result) {

			if (dialog12.isShowing()) {
				dialog12.dismiss();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 1 && resultCode == RESULT_OK) {
			Log.d("onActivityResult",
					"onActivityResult requestCode : "
							+ requestCode
							+ " resultCode : "
							+ resultCode
							+ " data : "
							+ data.getStringExtra(MobileSiteFragment.ARGUMENTS_UBER_REQUEST_ID));
			GetUberAccessTokenAsync getUberAccessTokenAsync = new GetUberAccessTokenAsync();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				getUberAccessTokenAsync
						.executeOnExecutor(
								AsyncTask.THREAD_POOL_EXECUTOR,
								data.getStringExtra(MobileSiteFragment.ARGUMENTS_UBER_REQUEST_ID));
			} else {
				getUberAccessTokenAsync
						.execute(data
								.getStringExtra(MobileSiteFragment.ARGUMENTS_UBER_REQUEST_ID));
			}
		}
	}

	public class GetUberAccessTokenAsync extends
			AsyncTask<String, Void, String> {

		String result;

		@Override
		protected void onPreExecute() {
			dialog12 = new ProgressDialog(BookaCabFragmentActivity.this);

			dialog12.setMessage("Please Wait...");
			dialog12.setCancelable(false);
			dialog12.setCanceledOnTouchOutside(false);
			dialog12.show();
		}

		@Override
		protected String doInBackground(String... args) {
			Log.d("GetUberAccessTokenAsync", "GetUberAccessTokenAsync : "
					+ args[0].toString());

			try {
				URL url = new URL(GlobalVariables.ServiceUrl
						+ "/cabbookrequeststatus.php?requestid="
						+ args[0].toString());
				String response = "";

				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				urlConnection.setReadTimeout(30000);
				urlConnection.setConnectTimeout(30000);
				urlConnection.setRequestMethod("POST");
				urlConnection.setDoInput(true);
				urlConnection.setDoOutput(true);

				// OutputStream outputStream = urlConnection.getOutputStream();
				// BufferedWriter bufferedWriter = new BufferedWriter(new
				// OutputStreamWriter(outputStream, "UTF-8"));
				// bufferedWriter.write("requestid=" + args[0].toString());
				// bufferedWriter.flush();
				// bufferedWriter.close();
				// outputStream.close();

				int responseCode = urlConnection.getResponseCode();

				if (responseCode == HttpsURLConnection.HTTP_OK) {

					String line = "";
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(
									urlConnection.getInputStream()));
					while ((line = bufferedReader.readLine()) != null) {
						response += line;
					}

				} else {
					response = "";
					Log.d("GetUberAccessTokenAsync",
							"responseCode != HttpsURLConnection.HTTP_OK : "
									+ responseCode);
					result = response;
				}

				Log.d("GetUberAccessTokenAsync",
						"GetUberAccessTokenAsync response : " + response);
				result = response;
			} catch (Exception e) {
				e.printStackTrace();
				result = "";
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(BookaCabFragmentActivity.this,
								"Something went wrong, please try again",
								Toast.LENGTH_LONG).show();
					}
				});
			}

			if (!result.isEmpty()) {
				try {
					JSONArray jsonArray = new JSONArray(result);
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						Log.d("GetUberAccessTokenAsync",
								"GetUberAccessTokenAsync jsonArray : "
										+ jsonObject.get("access_token"));
						String paramString = "type=bookuber"
								+ mUberBookingInputParams + "&accesstoken="
								+ jsonObject.get("access_token").toString();
						getUberBookingStatus(paramString);
					}
					// Log.d("GetUberAccessTokenAsync",
					// "GetUberAccessTokenAsync access_token : " + jsonArray);

				} catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(BookaCabFragmentActivity.this,
									"Something went wrong, please try again",
									Toast.LENGTH_LONG).show();
						}
					});
				}

			} else {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(BookaCabFragmentActivity.this,
								"Something went wrong, please try again",
								Toast.LENGTH_LONG).show();
					}
				});
			}

			return result;
		}

		@Override
		protected void onPostExecute(String result) {

			if (dialog12.isShowing()) {
				dialog12.dismiss();
			}
		}
	}

	private void getUberBookingStatus(String params) {

		try {
			URL url = new URL(GlobalVariables.ServiceUrl + "/uberConnect.php?"
					+ params);
			String response = "";
			Log.d("BookaCab", "getUberBookingStatus : "
					+ GlobalVariables.ServiceUrl + "/uberConnect.php?" + params);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			urlConnection.setReadTimeout(30000);
			urlConnection.setConnectTimeout(30000);
			urlConnection.setRequestMethod("POST");
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);

			// OutputStream outputStream = urlConnection.getOutputStream();
			// BufferedWriter bufferedWriter = new BufferedWriter(new
			// OutputStreamWriter(outputStream, "UTF-8"));
			// bufferedWriter.write(params);
			// bufferedWriter.flush();
			// bufferedWriter.close();
			// outputStream.close();

			int responseCode = urlConnection.getResponseCode();

			if (responseCode == HttpsURLConnection.HTTP_OK) {

				String line = "";
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(urlConnection.getInputStream()));
				while ((line = bufferedReader.readLine()) != null) {
					response += line;
				}

			} else {
				response = "";
				Log.d("getUberBookingStatus",
						"responseCode != HttpsURLConnection.HTTP_OK : "
								+ responseCode);
				return;
			}

			Log.d("getUberBookingStatus", "getUberBookingStatus response : "
					+ response);

			if (!response.toLowerCase().contains("error")) {

				try {
					JSONObject responseObject = new JSONObject(response);
					if (responseObject.length() > 0) {

						final String status = responseObject.get("status")
								.toString();
						if (status.equalsIgnoreCase("accepted")) {
							String driverName = "", driverPhone = "", eta = "", vehicleMake = "", vehicleLicense = "", requestID = "";
							try {
								JSONObject jsonObject2 = new JSONObject(
										responseObject.get("driver").toString());
								driverName = jsonObject2.get("name").toString();
								driverPhone = jsonObject2.get("phone_number")
										.toString();
							} catch (Exception e) {
								e.printStackTrace();
							}

							try {
								JSONObject jsonObject2 = new JSONObject(
										responseObject.get("vehicle")
												.toString());
								vehicleMake = jsonObject2.get("make")
										.toString();
								vehicleLicense = jsonObject2.get(
										"license_plate").toString();
							} catch (Exception e) {
								e.printStackTrace();
							}

							try {
								eta = responseObject.get("eta").toString();
							} catch (Exception e) {
								e.printStackTrace();
							}

							try {
								requestID = responseObject.get("request_id")
										.toString();
							} catch (Exception e) {
								e.printStackTrace();
							}

							if (rideObject != null) {
								rideObject.driverName = driverName;
								rideObject.driverPhone = driverPhone;
								rideObject.vehicle = vehicleLicense + " ("
										+ vehicleMake + ")";
							} else {
								rideObject = new RideObject();
								rideObject.driverName = driverName;
								rideObject.driverPhone = driverPhone;
								rideObject.vehicle = vehicleLicense + " ("
										+ vehicleMake + ")";
							}

							final String driverNameFinal = driverName, driverPhoneFinal = driverPhone, etaFinal = eta, vehicleMakeFinal = vehicleMake, vehicleLicenseFinal = vehicleLicense, requestIDFinal = requestID;
							runOnUiThread(new Runnable() {

								@Override
								public void run() {

									AlertDialog.Builder builder = new AlertDialog.Builder(
											BookaCabFragmentActivity.this);
									builder.setTitle("Success");
									builder.setMessage("Cab booked succesfully!\r\n"
											+ "Driver : "
											+ driverNameFinal
											+ " ("
											+ driverPhoneFinal
											+ ")\r\n"
											+ "Vehicle : "
											+ vehicleMakeFinal
											+ " ("
											+ vehicleLicenseFinal
											+ ")\r\n"
											+ "Est. Time : "
											+ etaFinal + " mins");
									builder.setPositiveButton(
											"OK",
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {

													try {
														updateCMCRecords(
																mCabSearchArray
																		.getJSONObject(
																				cabBookUberMegaPosition)
																		.get("CabNameID")
																		.toString(),
																mCabSearchArray
																		.getJSONObject(
																				cabBookUberMegaPosition)
																		.get("CarType")
																		.toString(),
																"1", fAddress,
																tAddress, "",
																requestIDFinal,
																false, false,
																"", "", "",
																true);
													} catch (Exception e) {
														e.printStackTrace();
													}
												}
											});
									AlertDialog dialog = builder.show();
									TextView messageText = (TextView) dialog
											.findViewById(android.R.id.message);
									messageText.setGravity(Gravity.CENTER);
									dialog.show();
								}
							});
						} else {
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									AlertDialog.Builder builder = new AlertDialog.Builder(
											BookaCabFragmentActivity.this);
									builder.setTitle("Cab could not be booked");
									builder.setMessage(status);
									builder.setPositiveButton("OK", null);
									AlertDialog dialog = builder.show();
									TextView messageText = (TextView) dialog
											.findViewById(android.R.id.message);
									messageText.setGravity(Gravity.CENTER);
									dialog.show();
								}
							});
						}

					} else {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(
										BookaCabFragmentActivity.this,
										"Something went wrong, please try again",
										Toast.LENGTH_LONG).show();
							}
						});
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(BookaCabFragmentActivity.this,
									"Something went wrong, please try again",
									Toast.LENGTH_LONG).show();
						}
					});
				}

			} else {

				try {
					JSONObject responseObject = new JSONObject(response);
					Log.d("getUberBookingStatus", "responseObject : "
							+ responseObject.toString());
					if (responseObject.length() > 0) {
						String dialogTitle = "", dialogMessage = "";

						// Log.d("getUberBookingStatus", "errors : "
						// + (new
						// JSONArray(responseObject.get("errors").toString())));
						JSONArray jsonArray = new JSONArray(responseObject.get(
								"errors").toString());
						JSONObject jsonObject = new JSONObject(jsonArray.get(0)
								.toString());

						dialogTitle = jsonObject.get("code").toString();
						dialogMessage = jsonObject.get("title").toString();

						// Iterator<String> iter = responseObject.keys();
						// while (iter.hasNext()) {
						// String key = iter.next();
						// Log.d("getUberBookingStatus", "values : "
						// + responseObject.get(key));
						//
						// String[] strings = responseObject.get("errors")
						// .toString().split(",");
						//
						// for (int i = 0; i < strings.length; i++) {
						// Log.d("getUberBookingStatus", strings[i]);
						// if (strings[i].contains("code")) {
						// String[] tempStrings = strings[i]
						// .split(":");
						// dialogTitle = tempStrings[1]
						// .replaceAll("\"", "")
						// .replaceAll("\\}", "")
						// .replaceAll("]", "");
						// }
						// if (strings[i].contains("title")) {
						// String[] tempStrings = strings[i]
						// .split(":");
						// dialogMessage = tempStrings[1]
						// .replaceAll("\"", "")
						// .replaceAll("\\}", "")
						// .replaceAll("]", "");
						// }
						// }
						// }

						final String finalDialogTitle = dialogTitle;
						final String finalDialogMessage = dialogMessage;
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								AlertDialog.Builder builder = new AlertDialog.Builder(
										BookaCabFragmentActivity.this);
								builder.setTitle(finalDialogTitle);
								builder.setMessage(finalDialogMessage);
								builder.setPositiveButton("OK", null);
								AlertDialog dialog = builder.show();
								TextView messageText = (TextView) dialog
										.findViewById(android.R.id.message);
								messageText.setGravity(Gravity.CENTER);
								dialog.show();
							}
						});

					} else {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(
										BookaCabFragmentActivity.this,
										"Something went wrong, please try again",
										Toast.LENGTH_LONG).show();
							}
						});
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(BookaCabFragmentActivity.this,
									"Something went wrong, please try again",
									Toast.LENGTH_LONG).show();
						}
					});
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(BookaCabFragmentActivity.this,
							"Something went wrong, please try again",
							Toast.LENGTH_LONG).show();
				}
			});
		}

	}

	private void bookMegaCab(final Address startAddress,
			final Address endAddress) {

		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					BookaCabFragmentActivity.this);
			builder.setTitle("Internet Connection Error");
			builder.setMessage("ClubMyCab requires Internet connection");
			builder.setPositiveButton("OK", null);
			AlertDialog dialog = builder.show();
			TextView messageText = (TextView) dialog
					.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
			dialog.show();

			return;
		} else if (startAddress == null || endAddress == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					BookaCabFragmentActivity.this);
			builder.setTitle("");
			builder.setMessage("Please provide both From & To locations to make a booking.");
			builder.setPositiveButton("OK", null);
			AlertDialog dialog = builder.show();
			TextView messageText = (TextView) dialog
					.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
			dialog.show();

			return;
		}

		BookMegaCabAsync bookMegaCabAsync = new BookMegaCabAsync();
		String param = "type=CreateBooking" + "&mobile=" + MobileNumberstr
				+ "&slat=" + String.valueOf(startAddress.getLatitude())
				+ "&slon=" + String.valueOf(startAddress.getLongitude())
				+ "&elat=" + String.valueOf(endAddress.getLatitude())
				+ "&elon=" + String.valueOf(endAddress.getLongitude())
				+ "&stime=";

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			bookMegaCabAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
					param);
		} else {
			bookMegaCabAsync.execute(param);
		}
	}

	public class BookMegaCabAsync extends AsyncTask<String, Void, String> {

		String result;

		@Override
		protected void onPreExecute() {
			dialog12 = new ProgressDialog(BookaCabFragmentActivity.this);

			dialog12.setMessage("Please Wait...");
			dialog12.setCancelable(false);
			dialog12.setCanceledOnTouchOutside(false);
			dialog12.show();
		}

		@Override
		protected String doInBackground(String... args) {
			Log.d("BookMegaCabAsync",
					"BookMegaCabAsync : " + args[0].toString());

			try {
				URL url = new URL(GlobalVariables.ServiceUrl + "/MegaApi.php");
				String response = "";

				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				urlConnection.setReadTimeout(30000);
				urlConnection.setConnectTimeout(30000);
				urlConnection.setRequestMethod("POST");
				urlConnection.setDoInput(true);
				urlConnection.setDoOutput(true);

				OutputStream outputStream = urlConnection.getOutputStream();
				BufferedWriter bufferedWriter = new BufferedWriter(
						new OutputStreamWriter(outputStream, "UTF-8"));
				bufferedWriter.write(args[0].toString());
				bufferedWriter.flush();
				bufferedWriter.close();
				outputStream.close();

				int responseCode = urlConnection.getResponseCode();

				if (responseCode == HttpsURLConnection.HTTP_OK) {

					String line = "";
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(
									urlConnection.getInputStream()));
					while ((line = bufferedReader.readLine()) != null) {
						response += line;
					}

				} else {
					response = "";
					Log.d("BookMegaCabAsync",
							"responseCode != HttpsURLConnection.HTTP_OK : "
									+ responseCode);
					result = response;
				}

				Log.d("BookMegaCabAsync", "BookMegaCabAsync response : "
						+ response);
				result = response;
			} catch (Exception e) {
				e.printStackTrace();
				result = "";
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(BookaCabFragmentActivity.this,
								"Something went wrong, please try again",
								Toast.LENGTH_LONG).show();
					}
				});
			}

			if (!result.isEmpty()) {

				try {
					JSONObject jsonObject = new JSONObject(result);
					String status = jsonObject.get("status").toString();
					if (status.equalsIgnoreCase("SUCCESS")) {

						JSONObject jsonObjectData = new JSONObject(jsonObject
								.get("data").toString());
						String driverName = "", driverPhone = "", vehicleLicense = "", requestID = "";
						try {
							driverName = jsonObjectData.get("DriverName")
									.toString();
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							driverPhone = jsonObjectData.get("DriverNumber")
									.toString();
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							vehicleLicense = jsonObjectData.get("VehicleNo")
									.toString();
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							requestID = jsonObject.get("Jobno").toString();
						} catch (Exception e) {
							e.printStackTrace();
						}

						if (rideObject != null) {
							rideObject.driverName = driverName;
							rideObject.driverPhone = driverPhone;
							rideObject.vehicle = vehicleLicense;
						} else {
							rideObject.driverName = driverName;
							rideObject.driverPhone = driverPhone;
							rideObject.vehicle = vehicleLicense;
						}

						final String driverNameFinal = driverName, driverPhoneFinal = driverPhone, vehicleLicenseFinal = vehicleLicense, requestIDFinal = requestID;

						runOnUiThread(new Runnable() {

							@Override
							public void run() {

								AlertDialog.Builder builder = new AlertDialog.Builder(
										BookaCabFragmentActivity.this);
								builder.setTitle("Success");
								builder.setMessage("Cab booked succesfully!\r\n"
										+ "Driver : "
										+ driverNameFinal
										+ " ("
										+ driverPhoneFinal
										+ ")\r\n"
										+ "Vehicle : " + vehicleLicenseFinal);
								builder.setPositiveButton("OK",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {

												try {
													updateCMCRecords(
															mCabSearchArray
																	.getJSONObject(
																			cabBookUberMegaPosition)
																	.get("CabNameID")
																	.toString(),
															mCabSearchArray
																	.getJSONObject(
																			cabBookUberMegaPosition)
																	.get("CarType")
																	.toString(),
															"1", fAddress,
															tAddress, "",
															requestIDFinal,
															false, false, "",
															"", "", true);
												} catch (Exception e) {
													e.printStackTrace();
												}
											}
										});
								AlertDialog dialog = builder.show();
								TextView messageText = (TextView) dialog
										.findViewById(android.R.id.message);
								messageText.setGravity(Gravity.CENTER);
								dialog.show();

							}
						});

					} else {
						final String reason = jsonObject.get("data").toString();

						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								AlertDialog.Builder builder = new AlertDialog.Builder(
										BookaCabFragmentActivity.this);
								builder.setTitle("Cab could not be booked");
								builder.setMessage(reason);
								builder.setPositiveButton("OK", null);
								AlertDialog dialog = builder.show();
								TextView messageText = (TextView) dialog
										.findViewById(android.R.id.message);
								messageText.setGravity(Gravity.CENTER);
								dialog.show();
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(BookaCabFragmentActivity.this,
								"Something went wrong, please try again",
								Toast.LENGTH_LONG).show();
					}
				});
			}

			return result;
		}

		@Override
		protected void onPostExecute(String result) {

			if (dialog12.isShowing()) {
				dialog12.dismiss();
			}
		}
	}

	private void updateCMCRecords(String cabName, String cabType,
			String modeID, Address from, Address to, String cabCoUsername,
			String bookingRef, boolean shouldCall, boolean shouldOpenSite,
			String phone, String mSite, String packName,
			boolean shouldOpenBookedCab) {

		if (!isOnline()) {
			return;
		}

		UpdateCMCRecordsAsync updateCMCRecordsAsync = new UpdateCMCRecordsAsync(
				shouldCall, shouldOpenSite, phone, mSite, packName,
				shouldOpenBookedCab);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm aa");
		Date date = Calendar.getInstance().getTime();
		String dateString, timeString;
		try {
			dateString = simpleDateFormat.format(date);
			timeString = simpleTimeFormat.format(date);
			// Log.d("BookaCab", "updateCMCRecords dateString : " + dateString
			// + " timeString : " + timeString);
		} catch (Exception e) {
			e.printStackTrace();
			dateString = "";
			timeString = "";
		}
		String param = "CabNameID="
				+ cabName
				+ "&CabType="
				+ cabType
				+ "&ModeID="
				+ modeID
				+ "&FromLat="
				+ String.valueOf(from.getLatitude())
				+ ","
				+ String.valueOf(from.getLongitude())
				+ "&ToLat="
				+ String.valueOf((to == null) ? "" : to.getLatitude())
				+ ","
				+ String.valueOf((to == null) ? "" : to.getLongitude())
				+ "&MobileNumber="
				+ MobileNumberstr
				+ "&CabUserName="
				+ cabCoUsername
				+ "&BookingRefNo="
				+ bookingRef
				+ "&FromLocation="
				+ ((rideObject != null) ? ((rideObject.fromLongName == null || rideObject.fromLongName
						.isEmpty()) ? from_places.getText().toString()
						: rideObject.fromLongName) : "")
				+ "&ToLocation="
				+ ((rideObject != null) ? ((rideObject.toLongName == null || rideObject.toLongName
						.isEmpty()) ? to_places.getText().toString()
						: rideObject.toLongName) : "")
				+ "&FromShortName="
				+ ((rideObject != null) ? ((rideObject.fromShortName == null || rideObject.fromShortName
						.isEmpty()) ? fromshortname : rideObject.fromShortName)
						: "")
				+ "&ToShortName="
				+ ((rideObject != null) ? ((rideObject.toShortName == null || rideObject.toShortName
						.isEmpty()) ? toshortname : rideObject.toShortName)
						: "")
				+ "&TravelDate="
				+ ((rideObject != null) ? ((rideObject.travelDate == null || rideObject.travelDate
						.isEmpty()) ? dateString : rideObject.travelDate) : "")
				+ "&TravelTime="
				+ ((rideObject != null) ? ((rideObject.travelTime == null || rideObject.travelTime
						.isEmpty()) ? timeString : rideObject.travelTime) : "")
				+ "&Distance="
				+ ((rideObject != null) ? rideObject.estDistance : "")
				+ "&ExpTripDuration="
				+ ((rideObject != null) ? rideObject.estDuration : "")
				+ "&CabId=" + ((rideObject != null) ? rideObject.cabID : "")
				+ "&DriverName="
				+ ((rideObject != null) ? rideObject.driverName : "")
				+ "&DriverNumber="
				+ ((rideObject != null) ? rideObject.driverPhone : "")
				+ "&CarNumber="
				+ ((rideObject != null) ? rideObject.vehicle : "")
				+ "&CarType=";

		// Log.d("BookaCab", "updateCMCRecords param : " + param);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			updateCMCRecordsAsync.executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, param);
		} else {
			updateCMCRecordsAsync.execute(param);
		}
	}

	public class UpdateCMCRecordsAsync extends AsyncTask<String, Void, String> {

		String result;
		boolean shouldCall = false, shouldOpenSite = false,
				shouldOpenBookedCabPage = false;
		String phoneNumber = "", mSiteAddress = "", packageName = "";

		public UpdateCMCRecordsAsync(boolean call, boolean site, String phone,
				String mSite, String packName, boolean shouldOpenBookedCab) {
			shouldCall = call;
			shouldOpenSite = site;
			phoneNumber = phone;
			mSiteAddress = mSite;
			packageName = packName;
			shouldOpenBookedCabPage = shouldOpenBookedCab;
		}

		@Override
		protected void onPreExecute() {
			dialog12 = new ProgressDialog(BookaCabFragmentActivity.this);

			dialog12.setMessage("Please Wait...");
			dialog12.setCancelable(false);
			dialog12.setCanceledOnTouchOutside(false);
			dialog12.show();
		}

		@Override
		protected String doInBackground(String... args) {
			Log.d("UpdateCMCRecordsAsync",
					"UpdateCMCRecordsAsync : " + args[0].toString()
							+ " boolean call : " + shouldCall
							+ " boolean site : " + shouldOpenSite);

			try {
				URL url = new URL(GlobalVariables.ServiceUrl
						+ "/cmcRecords.php");
				String response = "";

				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				urlConnection.setReadTimeout(30000);
				urlConnection.setConnectTimeout(30000);
				urlConnection.setRequestMethod("POST");
				urlConnection.setDoInput(true);
				urlConnection.setDoOutput(true);

				OutputStream outputStream = urlConnection.getOutputStream();
				BufferedWriter bufferedWriter = new BufferedWriter(
						new OutputStreamWriter(outputStream, "UTF-8"));
				bufferedWriter.write(args[0].toString());
				bufferedWriter.flush();
				bufferedWriter.close();
				outputStream.close();

				int responseCode = urlConnection.getResponseCode();

				if (responseCode == HttpsURLConnection.HTTP_OK) {

					String line = "";
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(
									urlConnection.getInputStream()));
					while ((line = bufferedReader.readLine()) != null) {
						response += line;
					}

				} else {
					response = "";
					Log.d("UpdateCMCRecordsAsync",
							"responseCode != HttpsURLConnection.HTTP_OK : "
									+ responseCode);
					result = response;
				}

				Log.d("UpdateCMCRecordsAsync",
						"UpdateCMCRecordsAsync response : " + response);
				result = response;
			} catch (Exception e) {
				e.printStackTrace();
				result = "";
				// runOnUiThread(new Runnable() {
				//
				// @Override
				// public void run() {
				// Toast.makeText(BookaCab.this,
				// "Something went wrong, please try again", Toast.LENGTH_LONG)
				// .show();
				// }
				// });
			}

			return result;
		}

		@Override
		protected void onPostExecute(String result) {

			if (dialog12.isShowing()) {
				dialog12.dismiss();
			}

			runOnUiThread(new Runnable() {

				@Override
				public void run() {

					if (shouldCall) {
						Intent intent = new Intent(Intent.ACTION_CALL,
								Uri.parse("tel:" + phoneNumber));
						startActivity(intent);
					}

					if (shouldOpenSite) {
						openAppOrMSite(packageName, mSiteAddress);
					}

					if (shouldOpenBookedCabPage) {
						Intent intent = new Intent(
								BookaCabFragmentActivity.this,
								MyRidesActivity.class);
						startActivity(intent);
					}
				}
			});
		}
	}

	private JSONArray performSortOnResults(JSONArray arrayToSort,
			String sortString) {

		JSONArray arraySorted = new JSONArray();

		HashMap<Double, JSONObject> hashMap = new HashMap<Double, JSONObject>();

		ArrayList<Integer> arrayListNoValues = new ArrayList<Integer>();

		JSONObject jsonObject = new JSONObject();

		// String logString = "", logString2 = "";

		for (int i = 0; i < arrayToSort.length(); i++) {

			try {
				jsonObject = arrayToSort.getJSONObject(i);
				// Log.d("BookaCab", "performSortOnResults low_estimate : " +
				// jsonObject.get("low_estimate").toString() +
				// " timeEstimate : " +
				// jsonObject.get("timeEstimate").toString());
				String string = jsonObject.get(sortString).toString();
				if (string.isEmpty() || string.equalsIgnoreCase("null")
						|| string.equalsIgnoreCase("na")) {
					arrayListNoValues.add(Integer.valueOf(i));
					// logString2 += (jsonObject.get("CabName").toString() +
					// " ");
				} else {
					hashMap.put(Double.parseDouble(jsonObject.get(sortString)
							.toString()), jsonObject);
					// logString += (jsonObject.get("CabName").toString() +
					// " ");
				}
			} catch (Exception e) {
				// e.printStackTrace();
				arrayListNoValues.add(Integer.valueOf(i));
				try {
					jsonObject = arrayToSort.getJSONObject(i);
					// logString2 += (jsonObject.get("CabName").toString() +
					// " ");
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		}

		// Log.d("BookaCab",
		// "performSortOnResults hashMap keySet : " + hashMap.keySet());

		Map<Double, JSONObject> map = new TreeMap<Double, JSONObject>(hashMap);

		// Log.d("BookaCab", "performSortOnResults map : " + map);

		Object[] array = map.keySet().toArray(new Object[map.keySet().size()]);

		for (int i = 0; i < array.length; i++) {
			Log.d("BookaCab", "performSortOnResults array : " + array[i]);
			arraySorted.put(hashMap.get(array[i]));
		}

		for (int i = 0; i < arrayListNoValues.size(); i++) {
			try {
				JSONObject jsonObject2 = arrayToSort
						.getJSONObject(arrayListNoValues.get(i).intValue());
				arraySorted.put(jsonObject2);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		Log.d("BookaCab",
				"performSortOnResults arraySorted : " + arraySorted.length()
						+ " arrayToSort : " + arrayToSort.length());
		// Log.d("BookaCab",
		// "performSortOnResults logString : " + logString);
		// Log.d("BookaCab",
		// "performSortOnResults logString2 : " + logString2);
		// Log.d("BookaCab", "performSortOnResults arrayToSort : " +
		// arrayToSort);
		// Log.d("BookaCab", "performSortOnResults arraySorted : " +
		// arraySorted);

		return arraySorted;
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

	private void selectImage() {

		final CharSequence[] options = { "Take Photo", "Choose from Gallery",
				"Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(
				BookaCabFragmentActivity.this);
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
//				Toast.makeText(BookaCabFragmentActivity.this,
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
//
//			String url_select = GlobalVariables.ServiceUrl
//					+ "/FetchUnreadNotificationCount.php";
//
//			HttpPost httpPost = new HttpPost(url_select);
//			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
//					"MobileNumber", MobileNumberstr);
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
				Toast.makeText(BookaCabFragmentActivity.this,
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
					"MobileNumber", MobileNumberstr);

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
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
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
					"UserNumber", MobileNumberstr);

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

	// convert from bitmap to byte array
	public byte[] getBytesFromBitmap(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 70, stream);
		return stream.toByteArray();
	}
}
