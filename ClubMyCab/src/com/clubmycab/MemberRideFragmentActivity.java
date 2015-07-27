package com.clubmycab;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.AndroidConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
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
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.clubmycab.FareCalculator.FareCalculatorInterface;
import com.clubmycab.maps.MapUtilityMethods;
import com.clubmycab.model.AddressModel;
import com.clubmycab.ui.ContactsToInviteActivity;
import com.clubmycab.ui.HomeActivity;
import com.clubmycab.ui.UpdatePickupLocationFragmentActivity;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.clubmycab.utility.StringTags;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

public class MemberRideFragmentActivity extends FragmentActivity implements
		FareCalculatorInterface, LocationListener {

	String CabId;
	String MobileNumber;
	String OwnerName;
	String OwnerImage;
	String FromLocation;
	String ToLocation;

	String FromShortName;
	String ToShortName;

	String TravelDate;
	String TravelTime;
	String Seats;
	String RemainingSeats;
	String Seat_Status;
	String Distance;
	String OpenTime;
	String CabStatus;
	String BookingRefNo;
	String DriverName;
	String DriverNumber;
	String CarNumber;
	String CabName;

	String ExpTripDuration;
	String statusTrip;

	String comefrom;

	ArrayList<String> steps = new ArrayList<String>();
	ArrayList<String> Summary = new ArrayList<String>();
	ArrayList<String> startaddress = new ArrayList<String>();
	ArrayList<String> endaddress = new ArrayList<String>();
	ArrayList<LatLng> startaddlatlng = new ArrayList<LatLng>();
	ArrayList<LatLng> endaddlatlng = new ArrayList<LatLng>();
	ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();
	ArrayList<LatLng> via_waypoint = new ArrayList<LatLng>();
	ArrayList<String> via_waypointstrarr = new ArrayList<String>();

	ArrayList<PolylineOptions> rectlinesarr = new ArrayList<PolylineOptions>();

	// boolean markerClicked;
	// Marker memberlocationmarker;

	String memberlocationaddress;
	LatLng memberlocationlatlong;

	String FullName;
	String MemberNumberstr;

	String joinpoolresponse;
	String showmembersresp;
	String checkpoolalreadyjoinresp;
	String droppoolresp;
	String updatelocationpoolresp;
	String sendcustommessagefrompopupresp;

	ArrayList<String> ShowMemberName = new ArrayList<String>();
	ArrayList<String> ShowMemberNumber = new ArrayList<String>();
	ArrayList<String> ShowMemberLocationAddress = new ArrayList<String>();
	ArrayList<String> ShowMemberLocationLatLong = new ArrayList<String>();
	ArrayList<String> ShowMemberImageName = new ArrayList<String>();
	ArrayList<String> ShowMemberStatus = new ArrayList<String>();

	GoogleMap joinpoolmap;
	TextView joinpoolchangelocationtext;

	LinearLayout beforejoinpoolll;
	LinearLayout afterjoinpoolll;

	// // before pooljoin
	LinearLayout joinpoolbtn;
	LinearLayout refermorefriends;
	ImageView mydetailbtn;
	// ImageView mycalculatorbtn;

	// //after pooljoin
	LinearLayout updatelocation;
	LinearLayout messagetoall;
	LinearLayout refermorefriendsafterpool;
	LinearLayout tripcompleted;
	LinearLayout droppool;

	CircularImageView memimage;

	String usermemname = null;
	String usermemnumber = null;
	String usermemlocadd = null;
	String usermemloclatlong = null;
	String usermemimagename = null;
	String usermemst = null;

	String CompletePageResponse;

	ImageView locationmarker;
	ProgressDialog onedialog;

	ImageView refreshlocationbtn;

	Marker mylocationmarker;

	ArrayList<String> namearray = new ArrayList<String>();
	ArrayList<String> phonenoarray = new ArrayList<String>();
	ArrayList<String> imagearray = new ArrayList<String>();

	ArrayList<String> namearraynew = new ArrayList<String>();
	ArrayList<String> phonenoarraynew = new ArrayList<String>();
	ArrayList<String> imagearraynew = new ArrayList<String>();

	Tracker tracker;

	private final String IPADDRESS = GlobalVariables.IpAddress;
	private final int PORT = 5222;
	private ListView listViewMsg;
	private EditText editTextMsg;
	private ImageView imageViewSendBtn;
	private XMPPConnection xmppConnection = null;
	private Message message; // openfire
	private PacketListener packetListener;

	// private String fromJid;
	// private String toJid;
	private ArrayList<ChatBubble> chatBubbleList;
	private MessageAdapter messageAdapter;

	ArrayList<String> MemberName = new ArrayList<String>();
	ArrayList<String> MemberNumber = new ArrayList<String>();

	String OwnerMobileNumber;

	DatabaseHandler db;
	RelativeLayout chatlayoutmainrl;

	boolean exceptioncheck = false;

	String ownerlocation;
	Marker ownerMarker;

	LocationManager locationManager;
	Location mycurrentlocationobject;

	@SuppressLint("DefaultLocale")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_pool);

		// Check if Internet present
		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					MemberRideFragmentActivity.this);
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
				.getInstance(MemberRideFragmentActivity.this);
		tracker = analytics
				.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);

		// All subsequent hits will be send with screen name = "main screen"
		tracker.setScreenName("Join Pool");

		Intent intent = getIntent();
		CabId = intent.getStringExtra("CabId");
		MobileNumber = intent.getStringExtra("MobileNumber");
		OwnerName = intent.getStringExtra("OwnerName");
		OwnerImage = intent.getStringExtra("OwnerImage");
		FromLocation = intent.getStringExtra("FromLocation");
		ToLocation = intent.getStringExtra("ToLocation");

		FromShortName = intent.getStringExtra("FromShortName");
		ToShortName = intent.getStringExtra("ToShortName");

		TravelDate = intent.getStringExtra("TravelDate");
		TravelTime = intent.getStringExtra("TravelTime");
		Seats = intent.getStringExtra("Seats");
		RemainingSeats = intent.getStringExtra("RemainingSeats");
		Seat_Status = intent.getStringExtra("Seat_Status");
		Distance = intent.getStringExtra("Distance");
		OpenTime = intent.getStringExtra("OpenTime");
		CabStatus = intent.getStringExtra("CabStatus");
		BookingRefNo = intent.getStringExtra("BookingRefNo");
		DriverName = intent.getStringExtra("DriverName");
		DriverNumber = intent.getStringExtra("DriverNumber");
		CarNumber = intent.getStringExtra("CarNumber");
		CabName = intent.getStringExtra("CabName");

		comefrom = intent.getStringExtra("comefrom");

		ExpTripDuration = intent.getStringExtra("ExpTripDuration");
		statusTrip = intent.getStringExtra("status");

		Log.d("comefrom", "" + comefrom);

		Log.d("CabStatus", "" + CabStatus + " statusTrip : " + statusTrip);

		beforejoinpoolll = (LinearLayout) findViewById(R.id.beforejoinpoolll);
		afterjoinpoolll = (LinearLayout) findViewById(R.id.afterjoinpoolll);

		// / before
		mydetailbtn = (ImageView) findViewById(R.id.mydetailbtn);

		joinpoolbtn = (LinearLayout) findViewById(R.id.joinpoolbtn);
		refermorefriends = (LinearLayout) findViewById(R.id.refermorefriends);

		// / after
		updatelocation = (LinearLayout) findViewById(R.id.updatelocation);

		messagetoall = (LinearLayout) findViewById(R.id.messagetoall);

		refermorefriendsafterpool = (LinearLayout) findViewById(R.id.refermorefriendsafterpool);

		tripcompleted = (LinearLayout) findViewById(R.id.tripcompleted);

		droppool = (LinearLayout) findViewById(R.id.droppool);

		chatlayoutmainrl = (RelativeLayout) findViewById(R.id.chatlayoutmainrl);
		chatlayoutmainrl.setVisibility(View.GONE);

		memberlocationaddress = "";

		locationmarker = (ImageView) findViewById(R.id.locationmarker);
		locationmarker.setVisibility(View.GONE);

		SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
		FullName = mPrefs.getString("FullName", "");
		MemberNumberstr = mPrefs.getString("MobileNumber", "");

		OwnerMobileNumber = MobileNumber;

		db = new DatabaseHandler(this);

		// /////////// chat code

		refreshlocationbtn = (ImageView) findViewById(R.id.refreshlocationbtn);
		refreshlocationbtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new ConnectionTaskForOwnerLocation().executeOnExecutor(
							AsyncTask.THREAD_POOL_EXECUTOR, CabId);
				} else {
					new ConnectionTaskForOwnerLocation().execute(CabId);
				}
			}
		});

		listViewMsg = (ListView) findViewById(R.id.listViewMsg);
		editTextMsg = (EditText) findViewById(R.id.editTextMsg);
		imageViewSendBtn = (ImageView) findViewById(R.id.imageViewSendBtn);

		imageViewSendBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = editTextMsg.getText().toString().trim();
				if (text.length() > 0) {
					onSendMsg(text);
					editTextMsg.setText("");
				} else {
					Toast.makeText(MemberRideFragmentActivity.this,
							"Type some message to send!", Toast.LENGTH_SHORT)
							.show();
				}

			}
		});

		List<ChatObject> contacts = db.getAllCabIdChats(CabId);
		chatBubbleList = new ArrayList<ChatBubble>();
		for (ChatObject cn : contacts) {

			if (cn.getFullName().toString().trim().equalsIgnoreCase(FullName)) {
				chatBubbleList.add(new ChatBubble(false, cn.getText()
						.toString(), Long
						.parseLong(cn.getDatetime().toString()), FullName));
			} else {

				chatBubbleList.add(new ChatBubble(true,
						cn.getText().toString(), Long.parseLong(cn
								.getDatetime().toString()), cn.getFullName()
								.toString()));
			}

		}

		setListAdapter();

		// //////

		mycurrentlocationobject = getLocation();
		joinpoolbtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (locationmarker.getVisibility() == View.VISIBLE) {

					AlertDialog.Builder builder = new AlertDialog.Builder(
							MemberRideFragmentActivity.this);
					builder.setMessage(memberlocationaddress.toUpperCase());
					builder.setCancelable(false);
					builder.setPositiveButton("Confirm",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {

									onedialog = new ProgressDialog(
											MemberRideFragmentActivity.this);
									onedialog.setMessage("Please Wait...");
									onedialog.setCancelable(false);
									onedialog.setCanceledOnTouchOutside(false);
									onedialog.show();

									if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
										new ConnectionTaskForJoiningapool()
												.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
									} else {
										new ConnectionTaskForJoiningapool()
												.execute();
									}

								}
							});
					builder.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
					AlertDialog dialog = builder.show();
					TextView messageText = (TextView) dialog
							.findViewById(android.R.id.message);
					messageText.setGravity(Gravity.CENTER);
					dialog.show();
				} else {

					// Location location = joinpoolmap.getMyLocation();
					// LocationManager locationManager = (LocationManager)
					// getSystemService(Context.LOCATION_SERVICE);
					// Criteria criteria = new Criteria();
					//
					// Location location = locationManager
					// .getLastKnownLocation(locationManager
					// .getBestProvider(criteria, false));

					Location location = mycurrentlocationobject;
					Log.d("MemberRideFragmentActivity",
							"joinpoolmap location : " + location);
					if (location != null) {

						LatLng point = new LatLng(location.getLatitude(),
								location.getLongitude());
						joinpoolmap.animateCamera(CameraUpdateFactory
								.newLatLngZoom(point, 21));

						setOnMapClick(point);

						Toast.makeText(
								MemberRideFragmentActivity.this,
								"We have set your pick-up to your current location, please move the map around to select a different location & press join ride again",
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(
								MemberRideFragmentActivity.this,
								"Please select your location to join the ride, by clicking on map",
								Toast.LENGTH_LONG).show();
					}
				}
			}
		});

		refermorefriends.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (RemainingSeats.equalsIgnoreCase("0")) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							MemberRideFragmentActivity.this);
					builder.setMessage("The ride is already full");
					builder.setCancelable(false);
					builder.setNegativeButton("OK", null);
					AlertDialog dialog = builder.show();
					TextView messageText = (TextView) dialog
							.findViewById(android.R.id.message);
					messageText.setGravity(Gravity.CENTER);
					dialog.show();
				} else {
					Intent mainIntent = new Intent(
							MemberRideFragmentActivity.this,
							ContactsToInviteActivity.class);
					mainIntent.putExtra("fromcome", "joinpool");
					mainIntent.putExtra("CabId", CabId);
					mainIntent.putExtra("MobileNumber", MobileNumber);
					mainIntent.putExtra("OwnerName", OwnerName);
					mainIntent.putExtra("FromLocation", FromLocation);
					mainIntent.putExtra("ToLocation", ToLocation);
					mainIntent.putExtra("TravelDate", TravelDate);
					mainIntent.putExtra("TravelTime", TravelTime);
					mainIntent.putExtra("Seats", Seats);
					mainIntent.putExtra("fromshortname", FromShortName);
					mainIntent.putExtra("toshortname", ToShortName);
					startActivityForResult(mainIntent, 500);
					overridePendingTransition(R.anim.slide_in_right,
							R.anim.slide_out_left);
				}
			}
		});

		// /////////

		updatelocation.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent mainIntent = new Intent(MemberRideFragmentActivity.this,
						UpdatePickupLocationFragmentActivity.class);
				mainIntent.putExtra("CabId", CabId);
				mainIntent.putExtra("MobileNumber", MobileNumber);
				mainIntent.putExtra("OwnerName", OwnerName);
				mainIntent.putExtra("FromLocation", FromLocation);
				mainIntent.putExtra("ToLocation", ToLocation);
				mainIntent.putExtra("TravelDate", TravelDate);
				mainIntent.putExtra("TravelTime", TravelTime);
				mainIntent.putExtra("Seats", Seats);
				mainIntent.putExtra("RemainingSeats", RemainingSeats);
				mainIntent.putExtra("Seat_Status", Seat_Status);
				mainIntent.putExtra("Distance", Distance);
				mainIntent.putExtra("OpenTime", OpenTime);
				mainIntent.putExtra("FromShortName", FromShortName);
				mainIntent.putExtra("ToShortName", ToShortName);

				mainIntent.putExtra("CompletePageResponse",
						CompletePageResponse);
				mainIntent.putExtra("checkpoolalreadyjoinresp",
						checkpoolalreadyjoinresp);

				startActivityForResult(mainIntent, 1);

			}
		});

		tripcompleted.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!BookingRefNo.isEmpty()
						&& !BookingRefNo.equalsIgnoreCase("null")) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							MemberRideFragmentActivity.this);
					builder.setMessage("A cab has already been booked for the ride");
					builder.setCancelable(false);
					builder.setNegativeButton("OK", null);
					AlertDialog dialog = builder.show();
					TextView messageText = (TextView) dialog
							.findViewById(android.R.id.message);
					messageText.setGravity(Gravity.CENTER);
					dialog.show();
				} else {

					String addressString = MapUtilityMethods.getAddress(
							MemberRideFragmentActivity.this,
							startaddlatlng.get(0).latitude,
							startaddlatlng.get(0).longitude);
					Address address = geocodeAddress(addressString);

					AddressModel startAddressModel = new AddressModel();
					startAddressModel.setAddress(address);
					startAddressModel.setShortname(FromShortName);
					startAddressModel.setLongname(addressString);

					addressString = MapUtilityMethods.getAddress(
							MemberRideFragmentActivity.this,
							endaddlatlng.get(0).latitude,
							endaddlatlng.get(0).longitude);
					address = geocodeAddress(addressString);

					AddressModel endAddressModel = new AddressModel();
					endAddressModel.setAddress(address);
					endAddressModel.setShortname(ToShortName);
					endAddressModel.setLongname(addressString);

					// String StartAddLatLng = startaddlatlng.get(0).latitude
					// + "," + startaddlatlng.get(0).longitude;
					// String EndAddLatLng = endaddlatlng.get(0).latitude + ","
					// + endaddlatlng.get(0).longitude;

					final Intent mainIntent = new Intent(
							MemberRideFragmentActivity.this,
							BookaCabFragmentActivity.class);
					Gson gson = new Gson();
					mainIntent.putExtra("StartAddressModel",
							gson.toJson(startAddressModel).toString());
					mainIntent.putExtra("EndAddressModel",
							gson.toJson(endAddressModel).toString());
					mainIntent.putExtra("CabId", CabId);
					// mainIntent.putExtra("FromShortName", FromShortName);
					// mainIntent.putExtra("ToShortName", ToShortName);
					mainIntent.putExtra("TravelDate", TravelDate);
					mainIntent.putExtra("TravelTime", TravelTime);
					MemberRideFragmentActivity.this.startActivity(mainIntent);
				}
			}
		});

		refermorefriendsafterpool
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						if (RemainingSeats.equalsIgnoreCase("0")) {
							AlertDialog.Builder builder = new AlertDialog.Builder(
									MemberRideFragmentActivity.this);
							builder.setMessage("The ride is already full");
							builder.setCancelable(false);
							builder.setNegativeButton("OK", null);
							AlertDialog dialog = builder.show();
							TextView messageText = (TextView) dialog
									.findViewById(android.R.id.message);
							messageText.setGravity(Gravity.CENTER);
							dialog.show();
						} else {
							Intent mainIntent = new Intent(
									MemberRideFragmentActivity.this,
									ContactsToInviteActivity.class);
							mainIntent.putExtra("fromcome", "joinpool");
							mainIntent.putExtra("CabId", CabId);
							mainIntent.putExtra("MobileNumber", MobileNumber);
							mainIntent.putExtra("OwnerName", OwnerName);
							mainIntent.putExtra("FromLocation", FromLocation);
							mainIntent.putExtra("ToLocation", ToLocation);
							mainIntent.putExtra("TravelDate", TravelDate);
							mainIntent.putExtra("TravelTime", TravelTime);
							mainIntent.putExtra("Seats", Seats);
							mainIntent.putExtra("fromshortname", FromShortName);
							mainIntent.putExtra("toshortname", ToShortName);
							startActivityForResult(mainIntent, 500);
							overridePendingTransition(R.anim.slide_in_right,
									R.anim.slide_out_left);
						}
					}
				});

		mydetailbtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				joinPoolPopUp(FromShortName, ToShortName, TravelDate,
						TravelTime, Seat_Status, OwnerName, OwnerImage);

			}
		});

		// mycalculatorbtn = (ImageView) findViewById(R.id.mycalculatorbtn);
		// mycalculatorbtn.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		//
		// AlertDialog.Builder builder = new AlertDialog.Builder(
		// MemberRideFragmentActivity.this);
		// builder.setTitle("Fare Split");
		// builder.setMessage("Please enter fare to split :");
		// builder.setCancelable(false);
		// final EditText input = new EditText(
		// MemberRideFragmentActivity.this);
		// LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
		// LinearLayout.LayoutParams.MATCH_PARENT,
		// LinearLayout.LayoutParams.MATCH_PARENT);
		// input.setLayoutParams(lp);
		// input.setInputType(InputType.TYPE_CLASS_NUMBER
		// | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		// builder.setView(input);
		// builder.setPositiveButton("OK",
		// new DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog,
		// int which) {
		//
		// Double fare = 0.0;
		// if (input.getText().toString().isEmpty()) {
		// Toast.makeText(
		// MemberRideFragmentActivity.this,
		// "Please enter a valid fare",
		// Toast.LENGTH_LONG).show();
		// } else if (!input.getText().toString()
		// .isEmpty()) {
		// fare = Double.parseDouble(input.getText()
		// .toString());
		// if (fare <= 0.0) {
		// Toast.makeText(
		// MemberRideFragmentActivity.this,
		// "Please enter a valid fare",
		// Toast.LENGTH_LONG).show();
		// } else {
		// onedialog = new ProgressDialog(
		// MemberRideFragmentActivity.this);
		// onedialog.setMessage("Please Wait...");
		// onedialog.setCancelable(false);
		// onedialog
		// .setCanceledOnTouchOutside(false);
		// onedialog.show();
		//
		// InputMethodManager im = (InputMethodManager)
		// getSystemService(Context.INPUT_METHOD_SERVICE);
		// im.hideSoftInputFromWindow(
		// input.getWindowToken(), 0);
		//
		// if (checkpoolalreadyjoinresp
		// .equalsIgnoreCase("fresh pool")) {
		//
		// } else {
		//
		// try {
		// JSONObject ownerJsonObject = new JSONObject();
		// ownerJsonObject
		// .put(FareCalculator.JSON_NAME_OWNER_START_ADDRESS,
		// FromLocation);
		// ownerJsonObject
		// .put(FareCalculator.JSON_NAME_OWNER_END_ADDRESS,
		// ToLocation);
		// ownerJsonObject
		// .put(FareCalculator.JSON_NAME_OWNER_NAME,
		// OwnerName);
		//
		// ArrayList<JSONObject> memberArrayList = new ArrayList<JSONObject>();
		//
		// JSONObject memberJsonObject = new JSONObject();
		// memberJsonObject
		// .put(FareCalculator.JSON_NAME_MEMBER_LOCATION_ADDRESS,
		// usermemlocadd);
		//
		// String[] latlong = usermemloclatlong
		// .split(",");
		// LatLng lt = new LatLng(
		// Double.parseDouble(latlong[0]),
		// Double.parseDouble(latlong[1]));
		// memberJsonObject
		// .put(FareCalculator.JSON_NAME_MEMBER_LOCATION_LATLNG,
		// lt);
		//
		// memberJsonObject
		// .put(FareCalculator.JSON_NAME_MEMBER_NAME,
		// usermemname);
		//
		// memberArrayList
		// .add(memberJsonObject);
		//
		// if (showmembersresp
		// .equalsIgnoreCase("No Members joined yet")) {
		//
		// } else {
		//
		// try {
		// for (int i = 0; i < ShowMemberName
		// .size(); i++) {
		// memberJsonObject = new JSONObject();
		// memberJsonObject
		// .put
		//
		// (FareCalculator.JSON_NAME_MEMBER_LOCATION_ADDRESS,
		//
		// ShowMemberLocationAddress
		//
		// .get(i)
		//
		// .toString());
		//
		// latlong = ShowMemberLocationLatLong
		// .get(i)
		// .split(",");
		// lt = new LatLng(
		// Double.parseDouble(latlong[0]),
		// Double.parseDouble(latlong
		//
		// [1]));
		// memberJsonObject
		// .put
		//
		// (FareCalculator.JSON_NAME_MEMBER_LOCATION_LATLNG,
		// lt);
		//
		// memberJsonObject
		// .put
		//
		// (FareCalculator.JSON_NAME_MEMBER_NAME,
		// ShowMemberName
		//
		// .get(i)
		//
		// .toString());
		//
		// memberArrayList
		// .add(memberJsonObject);
		// }
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		// }
		//
		// FareCalculator fareCalculator = new FareCalculator(
		// MemberRideFragmentActivity.this,
		// ownerJsonObject,
		// memberArrayList);
		// fareCalculator
		// .calculateFareSplit(fare);
		//
		// } catch (Exception e) {
		// // TODO Auto-generated catch
		// // block
		// e.printStackTrace();
		// }
		// }
		// }
		// }
		// }
		// });
		// builder.setNegativeButton("Cancel", null);
		// AlertDialog dialog = builder.show();
		// TextView messageText = (TextView) dialog
		// .findViewById(android.R.id.message);
		// messageText.setGravity(Gravity.CENTER);
		// dialog.show();
		// }
		// });

		droppool.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						MemberRideFragmentActivity.this);
				builder.setMessage("Are you sure you want to leave this ride?");
				builder.setCancelable(true);
				builder.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								tracker.send(new HitBuilders.EventBuilder()
										.setCategory("Leave Ride")
										.setAction("Leave Ride")
										.setLabel("Leave Ride").build());

								if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
									new ConnectionTaskFordroppool()
											.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
								} else {
									new ConnectionTaskFordroppool().execute();
								}

							}
						});
				builder.setNegativeButton("NO",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});
				AlertDialog dialog = builder.show();
				TextView messageText = (TextView) dialog
						.findViewById(android.R.id.message);
				messageText.setGravity(Gravity.CENTER);
				dialog.show();
			}
		});

		messagetoall.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Log.d("MemberNumber", "" + MemberNumber);

				if (!chatlayoutmainrl.isShown()) {

					tracker.send(new HitBuilders.EventBuilder()
							.setCategory("Message All")
							.setAction("Message All").setLabel("Message All")
							.build());

					chatlayoutmainrl.setVisibility(View.VISIBLE);
				} else {
					chatlayoutmainrl.setVisibility(View.GONE);
				}

				// Intent mainIntent = new Intent(CheckPool.this,
				// ChatToAll.class);
				// mainIntent.putExtra("CabId", CabId);
				// mainIntent.putExtra("OwnerName", OwnerName);
				// mainIntent.putExtra("OwnerMobileNumber", MobileNumber);
				// CheckPool.this.startActivity(mainIntent);
			}
		});

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

			onedialog = new ProgressDialog(MemberRideFragmentActivity.this);
			onedialog.setMessage("Please Wait...");
			onedialog.setCancelable(false);
			onedialog.setCanceledOnTouchOutside(false);
			onedialog.show();

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForcheckpoolalreadyjoined()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new ConnectionTaskForcheckpoolalreadyjoined().execute();
			}

		}

		// /// For contacts list
		Cursor cursor = null;
		try {
			cursor = MemberRideFragmentActivity.this.getContentResolver()
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

		Log.d("namearraynew", "" + namearraynew);
		Log.d("phonenoarraynew", "" + phonenoarraynew);
		Log.d("imagearraynew", "" + imagearraynew);

		Log.d("namearraynew count", "" + namearraynew.size());
		Log.d("phonenoarraynew count", "" + phonenoarraynew.size());
		Log.d("imagearraynew count", "" + imagearraynew.size());

		if (comefrom != null) {

			if (comefrom.equalsIgnoreCase("fromchatdirect")) {

				if (!chatlayoutmainrl.isShown()) {

					tracker.send(new HitBuilders.EventBuilder()
							.setCategory("Message All")
							.setAction("Message All").setLabel("Message All")
							.build());

					chatlayoutmainrl.setVisibility(View.VISIBLE);
				} else {
					chatlayoutmainrl.setVisibility(View.GONE);
				}

			}
		}
	}

	private void showRideCompleteDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(
				MemberRideFragmentActivity.this);
		View builderView = (View) getLayoutInflater().inflate(
				R.layout.dialog_fare_ride_complete, null);

		builder.setView(builderView);
		final AlertDialog dialog = builder.create();

		LinearLayout linearLayout = (LinearLayout) builderView
				.findViewById(R.id.ridecompletesettledll);
		linearLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				dialog.dismiss();

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new ConnectionTaskForMarkTripCompleted().executeOnExecutor(
							AsyncTask.THREAD_POOL_EXECUTOR, CabId);
				} else {
					new ConnectionTaskForMarkTripCompleted().execute(CabId);
				}
			}
		});

		linearLayout = (LinearLayout) builderView
				.findViewById(R.id.ridecompletepaidelsell);
		linearLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				dialog.dismiss();

				Toast.makeText(
						MemberRideFragmentActivity.this,
						"We will let you know when your friend shares the fare details & the amount you owe",
						Toast.LENGTH_LONG).show();

				Intent mainIntent = new Intent(MemberRideFragmentActivity.this,
						HomeActivity.class);
				mainIntent.putExtra("from", "normal");
				mainIntent.putExtra("message", "null");
				mainIntent.putExtra("CabId", "null");
				mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(mainIntent);
			}
		});

		linearLayout = (LinearLayout) builderView
				.findViewById(R.id.ridecompletecalculatell);
		linearLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				dialog.dismiss();

				// showFareSplitDialog();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new ConnectionTaskForShowMembersFareCalculation()
							.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					new ConnectionTaskForShowMembersFareCalculation().execute();
				}
			}
		});

		dialog.show();

	}

	private void showFareSplitDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(
				MemberRideFragmentActivity.this);
		builder.setTitle("Fare Split");
		builder.setMessage("Please enter fare to split :");
		builder.setCancelable(false);
		final EditText input = new EditText(MemberRideFragmentActivity.this);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		input.setLayoutParams(lp);
		input.setInputType(InputType.TYPE_CLASS_NUMBER
				| InputType.TYPE_NUMBER_FLAG_DECIMAL);
		builder.setView(input);
		builder.setPositiveButton("Calculate split",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						Double fare = 0.0;
						if (input.getText().toString().isEmpty()) {
							Toast.makeText(MemberRideFragmentActivity.this,
									"Please enter a valid fare",
									Toast.LENGTH_LONG).show();
							showFareSplitDialog();
						} else if (!input.getText().toString().isEmpty()) {
							fare = Double.parseDouble(input.getText()
									.toString());
							if (fare <= 0.0) {
								Toast.makeText(MemberRideFragmentActivity.this,
										"Please enter a valid fare",
										Toast.LENGTH_LONG).show();
								showFareSplitDialog();
							} else {
								onedialog = new ProgressDialog(
										MemberRideFragmentActivity.this);
								onedialog.setMessage("Please Wait...");
								onedialog.setCancelable(false);
								onedialog.setCanceledOnTouchOutside(false);
								onedialog.show();

								InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
								im.hideSoftInputFromWindow(
										input.getWindowToken(), 0);

								if (showmembersresp
										.equalsIgnoreCase("No Members joined yet")) {

								} else {

									try {

										JSONObject ownerJsonObject = new JSONObject();
										ownerJsonObject
												.put(FareCalculator.JSON_NAME_OWNER_START_ADDRESS,
														FromLocation);
										ownerJsonObject
												.put(FareCalculator.JSON_NAME_OWNER_END_ADDRESS,
														ToLocation);
										ownerJsonObject
												.put(FareCalculator.JSON_NAME_OWNER_NAME,
														OwnerName);

										ArrayList<JSONObject> memberArrayList = new ArrayList<JSONObject>();

										for (int i = 0; i < ShowMemberName
												.size(); i++) {
											JSONObject memberJsonObject = new JSONObject();
											memberJsonObject
													.put

													(FareCalculator.JSON_NAME_MEMBER_LOCATION_ADDRESS,
															ShowMemberLocationAddress
																	.get(i)
																	.toString());

											String[] latlong = ShowMemberLocationLatLong
													.get(i).split(",");
											LatLng lt = new LatLng(
													Double.parseDouble(latlong[0]),
													Double.parseDouble(latlong[1]));
											memberJsonObject
													.put

													(FareCalculator.JSON_NAME_MEMBER_LOCATION_LATLNG,
															lt);

											memberJsonObject
													.put(FareCalculator.JSON_NAME_MEMBER_NAME,
															ShowMemberName.get(
																	i)
																	.toString());

											memberArrayList
													.add(memberJsonObject);
										}

										FareCalculator fareCalculator = new FareCalculator(
												MemberRideFragmentActivity.this,
												ownerJsonObject,
												memberArrayList);
										fareCalculator.calculateFareSplit(fare);

									} catch (Exception e) {
										e.printStackTrace();
									}

								}
							}
						}
					}
				});
		// builder.setNegativeButton("Cancel", null);
		AlertDialog dialog = builder.show();
		TextView messageText = (TextView) dialog
				.findViewById(android.R.id.message);
		messageText.setGravity(Gravity.CENTER);
		dialog.show();

	}

	private void showPaymentDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(
				MemberRideFragmentActivity.this);
		View builderView = (View) getLayoutInflater().inflate(
				R.layout.dialog_fare_ride_complete_payment, null);

		builder.setView(builderView);
		final AlertDialog dialog = builder.create();

		LinearLayout linearLayout = (LinearLayout) builderView
				.findViewById(R.id.ridecompletefaresettledll);
		linearLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				dialog.dismiss();

				// call update status for user

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new ConnectionTaskForMarkTripCompleted().executeOnExecutor(
							AsyncTask.THREAD_POOL_EXECUTOR, CabId,
							OwnerMobileNumber, MemberNumberstr);
				} else {
					new ConnectionTaskForMarkTripCompleted().execute(CabId,
							OwnerMobileNumber, MemberNumberstr);
				}
			}
		});

		linearLayout = (LinearLayout) builderView
				.findViewById(R.id.ridecompletefarewalletll);
		linearLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				dialog.dismiss();

				// Show coming soon
			}
		});

		dialog.show();

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

	// ///////////////////////
	// ///////

	private class ConnectionTaskForcheckpoolalreadyjoined extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectioncheckpoolalreadyjoined mAuth1 = new AuthenticateConnectioncheckpoolalreadyjoined();
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
				Toast.makeText(MemberRideFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (checkpoolalreadyjoinresp.equalsIgnoreCase("fresh pool")) {

				if (CabStatus.toString().trim().equalsIgnoreCase("A")) {
					beforejoinpoolll.setVisibility(View.VISIBLE);
					afterjoinpoolll.setVisibility(View.GONE);
				} else {
					beforejoinpoolll.setVisibility(View.GONE);
					afterjoinpoolll.setVisibility(View.GONE);
				}
			} else {

				if (CabStatus.toString().trim().equalsIgnoreCase("A")) {
					afterjoinpoolll.setVisibility(View.VISIBLE);
					beforejoinpoolll.setVisibility(View.GONE);
				} else {
					beforejoinpoolll.setVisibility(View.GONE);
					afterjoinpoolll.setVisibility(View.GONE);
				}
			}

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForDirections()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new ConnectionTaskForDirections().execute();
			}

		}
	}

	public class AuthenticateConnectioncheckpoolalreadyjoined {

		public AuthenticateConnectioncheckpoolalreadyjoined() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/checkpoolalreadyjoined.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair CabIdBasicNameValuePair = new BasicNameValuePair(
					"CabId", CabId);
			BasicNameValuePair MemberNumberBasicNameValuePair = new BasicNameValuePair(
					"MemberNumber", MemberNumberstr);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(CabIdBasicNameValuePair);
			nameValuePairList.add(MemberNumberBasicNameValuePair);

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
				checkpoolalreadyjoinresp = stringBuilder.append(
						bufferedStrChunk).toString();
			}

			Log.d("checkpoolalreadyjoinresp", "" + stringBuilder.toString());
		}
	}

	// ///////

	private class ConnectionTaskForDirections extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionGetDirection mAuth1 = new AuthenticateConnectionGetDirection();
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
				Toast.makeText(MemberRideFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			for (int i = 0; i < steps.size(); i++) {

				ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();

				JSONArray subArray123;
				try {
					subArray123 = new JSONArray(steps.get(i));
					for (int i111 = 0; i111 < subArray123.length(); i111++) {

						String locationstr = subArray123.getJSONObject(i111)
								.getString("start_location").toString();

						JSONObject jsonObject11 = new JSONObject(locationstr);
						double lat1 = Double.parseDouble(jsonObject11
								.getString("lat"));
						double lng1 = Double.parseDouble(jsonObject11
								.getString("lng"));

						listGeopoints.add(new LatLng(lat1, lng1));

						// /
						String locationstr1 = subArray123.getJSONObject(i111)
								.getString("polyline").toString();

						JSONObject jsonObject111 = new JSONObject(locationstr1);
						String points = jsonObject111.getString("points");
						ArrayList<LatLng> arr = decodePoly(points);
						for (int j = 0; j < arr.size(); j++) {
							listGeopoints.add(new LatLng(arr.get(j).latitude,
									arr.get(j).longitude));
						}
						// /
						String locationstr11 = subArray123.getJSONObject(i111)
								.getString("end_location").toString();

						JSONObject jsonObject1111 = new JSONObject(
								locationstr11);
						double lat11 = Double.parseDouble(jsonObject1111
								.getString("lat"));
						double lng11 = Double.parseDouble(jsonObject1111
								.getString("lng"));

						listGeopoints.add(new LatLng(lat11, lng11));

						Random rnd = new Random();
						int color = Color.argb(255, rnd.nextInt(256),
								rnd.nextInt(256), rnd.nextInt(256));

						PolylineOptions rectLine = new PolylineOptions().width(
								5).color(color);
						rectLine.addAll(listGeopoints);
						rectlinesarr.add(rectLine);

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			joinpoolmap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.joinpoolmap)).getMap();

			joinpoolmap.setMyLocationEnabled(true);

			joinpoolchangelocationtext = (TextView) findViewById(R.id.joinpoolchangelocationtext);
			joinpoolchangelocationtext.setTypeface(Typeface.createFromAsset(
					getAssets(), "NeutraText-Bold.ttf"));

			joinpoolmap.setOnMapClickListener(new OnMapClickListener() {

				@Override
				public void onMapClick(LatLng point) {

					setOnMapClick(point);
				}
			});

			LatLngBounds.Builder bc = null;

			for (int i = 0; i < rectlinesarr.size(); i++) {
				joinpoolmap.addPolyline(rectlinesarr.get(i));

				List<LatLng> points = rectlinesarr.get(i).getPoints();

				bc = new LatLngBounds.Builder();

				for (LatLng item : points) {
					bc.include(item);
				}
			}

			joinpoolmap.moveCamera(CameraUpdateFactory.newLatLngBounds(
					bc.build(), 50));

			joinpoolmap.addMarker(new MarkerOptions()
					.position(startaddlatlng.get(0))
					.title(startaddress.get(0))
					.snippet("start")
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.start)));

			joinpoolmap
					.addMarker(new MarkerOptions()
							.position(endaddlatlng.get(0))
							.title(endaddress.get(0))
							.snippet("end")
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.end)));

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForShowMembersOnMap()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new ConnectionTaskForShowMembersOnMap().execute();
			}

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForOwnerLocation().executeOnExecutor(
						AsyncTask.THREAD_POOL_EXECUTOR, CabId);
			} else {
				new ConnectionTaskForOwnerLocation().execute(CabId);
			}

		}

	}

	private void setOnMapClick(LatLng point) {
		joinpoolmap.animateCamera(CameraUpdateFactory.newLatLng(point));
		locationmarker.setVisibility(View.VISIBLE);

		memberlocationlatlong = point;
		memberlocationaddress = MapUtilityMethods.getAddress(
				MemberRideFragmentActivity.this, point.latitude,
				point.longitude);

		Log.d("memberlocationlatlong", "" + memberlocationlatlong);
		Log.d("memberlocationaddress", "" + memberlocationaddress);

		joinpoolchangelocationtext.setVisibility(View.VISIBLE);
		joinpoolchangelocationtext.setText(memberlocationaddress);

		joinpoolmap.setOnCameraChangeListener(new OnCameraChangeListener() {

			@Override
			public void onCameraChange(final CameraPosition cameraPosition) {

				LatLng mapcenter = cameraPosition.target;

				memberlocationlatlong = mapcenter;
				memberlocationaddress = MapUtilityMethods.getAddress(
						MemberRideFragmentActivity.this, mapcenter.latitude,
						mapcenter.longitude);

				Log.d("memberlocationlatlong", "" + memberlocationlatlong);
				Log.d("memberlocationaddress", "" + memberlocationaddress);

				joinpoolchangelocationtext.setText(memberlocationaddress);

			}
		});
	}

	public class AuthenticateConnectionGetDirection {

		public AuthenticateConnectionGetDirection() {

		}

		public void connection() throws Exception {

			String source = FromLocation.replaceAll(" ", "%20");
			String dest = ToLocation.replaceAll(" ", "%20");

			String url = "https://maps.googleapis.com/maps/api/directions/json?"
					+ "origin="
					+ source
					+ "&destination="
					+ dest
					+ "&sensor=false&units=metric&mode=driving&alternatives=true&key="
					+ GlobalVariables.GoogleMapsAPIKey;

			Log.d("url", "" + url);

			CompletePageResponse = new Communicator().executeHttpGet(url);

			CompletePageResponse = CompletePageResponse
					.replaceAll("\\\\/", "/");

			JSONObject jsonObject = new JSONObject(CompletePageResponse);

			String name = jsonObject.getString("routes");

			JSONArray subArray = new JSONArray(name);

			for (int i = 0; i < subArray.length(); i++) {

				Summary.add(subArray.getJSONObject(i).getString("summary")
						.toString());

				String name1 = subArray.getJSONObject(i).getString("legs")
						.toString();

				JSONArray subArray1 = new JSONArray(name1);

				for (int i1 = 0; i1 < subArray1.length(); i1++) {

					startaddress.add(subArray1.getJSONObject(i1)
							.getString("start_address").toString());
					endaddress.add(subArray1.getJSONObject(i1)
							.getString("end_address").toString());

					String startadd = subArray1.getJSONObject(i1)
							.getString("start_location").toString();

					JSONObject jsonObject1 = new JSONObject(startadd);
					double lat = Double.parseDouble(jsonObject1
							.getString("lat"));
					double lng = Double.parseDouble(jsonObject1
							.getString("lng"));

					startaddlatlng.add(new LatLng(lat, lng));

					//
					String endadd = subArray1.getJSONObject(i1)
							.getString("end_location").toString();

					JSONObject jsonObject41 = new JSONObject(endadd);
					double lat4 = Double.parseDouble(jsonObject41
							.getString("lat"));
					double lng4 = Double.parseDouble(jsonObject41
							.getString("lng"));

					endaddlatlng.add(new LatLng(lat4, lng4));

					// ////////////

					steps.add(subArray1.getJSONObject(i1).getString("steps")
							.toString());

					// //////////////
					String mska = subArray1.getJSONObject(i1)
							.getString("via_waypoint").toString();

					if (mska.equalsIgnoreCase("[]")) {
						via_waypoint.add(new LatLng(0, 0));
					} else {
						JSONArray subArray12 = new JSONArray(mska);

						for (int i11 = 0; i11 < subArray12.length(); i11++) {

							String locationstr = subArray12.getJSONObject(i11)
									.getString("location").toString();

							JSONObject jsonObject1111 = new JSONObject(
									locationstr);
							double lat1111 = Double.parseDouble(jsonObject1111
									.getString("lat"));
							double lng1111 = Double.parseDouble(jsonObject1111
									.getString("lng"));

							via_waypoint.add(new LatLng(lat1111, lng1111));

						}
					}
				}
			}

			// /////
			Log.d("Summary", "" + Summary);
			Log.d("startaddress", "" + startaddress);
			Log.d("endaddress", "" + endaddress);
			Log.d("startaddlatlng", "" + startaddlatlng);
			Log.d("endaddlatlng", "" + endaddlatlng);
			Log.d("via_waypoint", "" + via_waypoint);

			for (int i = 0; i < via_waypoint.size(); i++) {
				String asd = MapUtilityMethods.getAddress(
						MemberRideFragmentActivity.this,
						via_waypoint.get(i).latitude,
						via_waypoint.get(i).longitude);
				via_waypointstrarr.add(asd);
			}
			Log.d("via_waypointstrarr", "" + via_waypointstrarr);
		}
	}

	// ///////////////////////
	// ///////

	private class ConnectionTaskForShowMembersOnMap extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionShowMembers mAuth1 = new AuthenticateConnectionShowMembers();
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
				Toast.makeText(MemberRideFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (checkpoolalreadyjoinresp.equalsIgnoreCase("fresh pool")) {

			} else {

				// mycalculatorbtn.setVisibility(View.VISIBLE);

				try {
					JSONArray subArray = new JSONArray(checkpoolalreadyjoinresp);
					for (int i = 0; i < subArray.length(); i++) {
						try {
							usermemname = subArray.getJSONObject(i)
									.getString("MemberName").toString();
							usermemnumber = subArray.getJSONObject(i)
									.getString("MemberNumber").toString();
							usermemlocadd = subArray.getJSONObject(i)
									.getString("MemberLocationAddress")
									.toString();
							usermemloclatlong = subArray.getJSONObject(i)
									.getString("MemberLocationlatlong")
									.toString();
							usermemimagename = subArray.getJSONObject(i)
									.getString("MemberImageName").toString();
							usermemst = subArray.getJSONObject(i)
									.getString("Status").toString();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				String[] latlong = usermemloclatlong.split(",");
				LatLng lt = new LatLng(Double.parseDouble(latlong[0]),
						Double.parseDouble(latlong[1]));

				mylocationmarker = joinpoolmap
						.addMarker(new MarkerOptions()
								.position(lt)
								.snippet("mylocation")
								.title(usermemlocadd)
								.icon(BitmapDescriptorFactory
										.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

				memberlocationlatlong = lt;
				memberlocationaddress = usermemlocadd;

				locationmarker.setVisibility(View.GONE);
				joinpoolchangelocationtext.setVisibility(View.GONE);

				joinpoolmap.setOnMapClickListener(null);
				joinpoolmap.setOnCameraChangeListener(null);
			}

			if (showmembersresp.equalsIgnoreCase("No Members joined yet")) {

			} else {

				ShowMemberName.clear();
				ShowMemberNumber.clear();
				ShowMemberLocationAddress.clear();
				ShowMemberLocationLatLong.clear();
				ShowMemberImageName.clear();
				ShowMemberStatus.clear();

				try {
					JSONArray subArray = new JSONArray(showmembersresp);
					for (int i = 0; i < subArray.length(); i++) {
						try {
							ShowMemberName.add(subArray.getJSONObject(i)
									.getString("MemberName").toString());
							ShowMemberNumber.add(subArray.getJSONObject(i)
									.getString("MemberNumber").toString());
							ShowMemberLocationAddress.add(subArray
									.getJSONObject(i)
									.getString("MemberLocationAddress")
									.toString());
							ShowMemberLocationLatLong.add(subArray
									.getJSONObject(i)
									.getString("MemberLocationlatlong")
									.toString());
							ShowMemberImageName.add(subArray.getJSONObject(i)
									.getString("MemberImageName").toString());
							ShowMemberStatus.add(subArray.getJSONObject(i)
									.getString("Status").toString());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				for (int i = 0; i < ShowMemberName.size(); i++) {
					String[] latlong = ShowMemberLocationLatLong.get(i).split(
							",");
					LatLng lt = new LatLng(Double.parseDouble(latlong[0]),
							Double.parseDouble(latlong[1]));
					joinpoolmap
							.addMarker(new MarkerOptions()
									.position(lt)
									.snippet(String.valueOf(i))
									.title(ShowMemberLocationAddress.get(i))
									.icon(BitmapDescriptorFactory
											.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
				}
			}

			joinpoolmap.setOnMarkerClickListener(new OnMarkerClickListener() {

				@Override
				public boolean onMarkerClick(Marker arg0) {

					if (arg0.getSnippet().equals("start")) {

						if (CabStatus.toString().trim().equalsIgnoreCase("A")) {
							showAlertDialog(OwnerName, MobileNumber,
									FromLocation, MobileNumber + ".jpg");
						}

					} else if (arg0.getSnippet().equals("end")) {

						if (CabStatus.toString().trim().equalsIgnoreCase("A")) {
							showAlertDialog(OwnerName, MobileNumber,
									ToLocation, MobileNumber + ".jpg");
						}

					} else if (arg0.getSnippet().equals("mylocation")) {

						if (CabStatus.toString().trim().equalsIgnoreCase("A")) {
							showAlertDialogmylocation(usermemname,
									usermemnumber, usermemlocadd,
									usermemimagename);
						}

					} else if (arg0.getTitle().equals("Last updated at")) {
						// Log.d("MemberRideFragment",
						// "setOnMarkerClickListener OwnerLocation : ");
						arg0.showInfoWindow();
					} else {

						if (checkpoolalreadyjoinresp
								.equalsIgnoreCase("fresh pool")) {

							if (CabStatus.toString().trim()
									.equalsIgnoreCase("A")) {
								Toast.makeText(
										MemberRideFragmentActivity.this,
										"Join ride to see details of other members",
										Toast.LENGTH_LONG).show();
							}

						} else {

							if (CabStatus.toString().trim()
									.equalsIgnoreCase("A")) {
								final Integer index = Integer.parseInt(arg0
										.getSnippet());

								showAlertDialog(ShowMemberName.get(index),
										ShowMemberNumber.get(index),
										ShowMemberLocationAddress.get(index),
										ShowMemberImageName.get(index));
							}
						}

					}

					return true;
				}

			});

			if (checkpoolalreadyjoinresp.equalsIgnoreCase("fresh pool")) {

				joinPoolPopUp(FromShortName, ToShortName, TravelDate,
						TravelTime, Seat_Status, OwnerName, OwnerImage);

				if (RemainingSeats.equalsIgnoreCase("0")) {

					AlertDialog.Builder builder = new AlertDialog.Builder(
							MemberRideFragmentActivity.this);
					builder.setMessage("The ride is already full, you can join another ride or create your own.");
					builder.setCancelable(false);
					builder.setNegativeButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
									finish();
								}
							});
					AlertDialog dialog = builder.show();
					TextView messageText = (TextView) dialog
							.findViewById(android.R.id.message);
					messageText.setGravity(Gravity.CENTER);
					dialog.show();

				}
			}

			if (onedialog.isShowing()) {
				onedialog.dismiss();
			}

			// //////////////////////////
			if (CabStatus.equals("A") && statusTrip.equals("2")) {
				showRideCompleteDialog();
			} else if (CabStatus.equals("A") && statusTrip.equals("3")) {
				showPaymentDialog();
			} else if (CabStatus.equals("A")) {
				try {
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
							"dd/MM/yyyy hh:mm aa");
					Date date = simpleDateFormat.parse(TravelDate + " "
							+ TravelTime);

					long expDuration = Long.parseLong(ExpTripDuration);

					if (System.currentTimeMillis() >= (date.getTime() + expDuration * 1000)) {
						Log.d("MemberRideFragmentActivity",
								"ExpTripDuration trip completed");
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							new ConnectionTaskForTripCompleted()
									.executeOnExecutor(
											AsyncTask.THREAD_POOL_EXECUTOR,
											CabId);
						} else {
							new ConnectionTaskForTripCompleted().execute(CabId);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// //////////////////////////
		}
	}

	public class AuthenticateConnectionShowMembers {

		public AuthenticateConnectionShowMembers() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/ShowMemberOnMap.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair CabIdBasicNameValuePair = new BasicNameValuePair(
					"CabId", CabId);
			BasicNameValuePair MemberNumberBasicNameValuePair = new BasicNameValuePair(
					"MemberNumber", MemberNumberstr);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(CabIdBasicNameValuePair);
			nameValuePairList.add(MemberNumberBasicNameValuePair);

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
				showmembersresp = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("showmembersresp", "" + stringBuilder.toString());
		}
	}

	// ///////////////////////
	// ///////

	private class ConnectionTaskForcheckpoolalreadyjoinednew extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectioncheckpoolalreadyjoinednew mAuth1 = new AuthenticateConnectioncheckpoolalreadyjoinednew();
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
				Toast.makeText(MemberRideFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (checkpoolalreadyjoinresp.equalsIgnoreCase("fresh pool")) {
				beforejoinpoolll.setVisibility(View.VISIBLE);
				afterjoinpoolll.setVisibility(View.GONE);
			} else {
				afterjoinpoolll.setVisibility(View.VISIBLE);
				beforejoinpoolll.setVisibility(View.GONE);
			}

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForDirectionsnew()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new ConnectionTaskForDirectionsnew().execute();
			}

		}
	}

	public class AuthenticateConnectioncheckpoolalreadyjoinednew {

		public AuthenticateConnectioncheckpoolalreadyjoinednew() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/checkpoolalreadyjoined.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair CabIdBasicNameValuePair = new BasicNameValuePair(
					"CabId", CabId);
			BasicNameValuePair MemberNumberBasicNameValuePair = new BasicNameValuePair(
					"MemberNumber", MemberNumberstr);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(CabIdBasicNameValuePair);
			nameValuePairList.add(MemberNumberBasicNameValuePair);

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
				checkpoolalreadyjoinresp = stringBuilder.append(
						bufferedStrChunk).toString();
			}

			Log.d("checkpoolalreadyjoinresp", "" + stringBuilder.toString());
		}
	}

	private ArrayList<LatLng> decodePoly(String encoded) {
		ArrayList<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;
		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;
			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
			poly.add(position);
		}
		return poly;
	}

	// //////////////////
	// ///////

	private class ConnectionTaskForJoiningapool extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionFetchNotification mAuth1 = new AuthenticateConnectionFetchNotification();
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
				Toast.makeText(MemberRideFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			tracker.send(new HitBuilders.EventBuilder()
					.setCategory("Join Ride").setAction("Join Ride")
					.setLabel("Join Ride").build());

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForcheckpoolalreadyjoinednew()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new ConnectionTaskForcheckpoolalreadyjoinednew().execute();
			}
		}

	}

	public class AuthenticateConnectionFetchNotification {

		public AuthenticateConnectionFetchNotification() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl + "/joinpool.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair CabIdBasicNameValuePair = new BasicNameValuePair(
					"CabId", CabId);
			BasicNameValuePair OwnerNameBasicNameValuePair = new BasicNameValuePair(
					"OwnerName", OwnerName);
			BasicNameValuePair OwnerNumberBasicNameValuePair = new BasicNameValuePair(
					"OwnerNumber", MobileNumber);
			BasicNameValuePair MemberNameBasicNameValuePair = new BasicNameValuePair(
					"MemberName", FullName);
			BasicNameValuePair MemberNumberBasicNameValuePair = new BasicNameValuePair(
					"MemberNumber", MemberNumberstr);
			BasicNameValuePair MemberLocationAddressBasicNameValuePair = new BasicNameValuePair(
					"MemberLocationAddress", memberlocationaddress);
			double lat = memberlocationlatlong.latitude;
			double longi = memberlocationlatlong.longitude;

			String latlong = String.valueOf(lat) + "," + String.valueOf(longi);
			BasicNameValuePair MemberLocationlatlongBasicNameValuePair = new BasicNameValuePair(
					"MemberLocationlatlong", latlong);
			BasicNameValuePair StatusBasicNameValuePair = new BasicNameValuePair(
					"Status", "Nothing");
			BasicNameValuePair MessageBasicNameValuePair = new BasicNameValuePair(
					"Message", FullName + " has joined your ride from "
							+ FromShortName + " to " + ToShortName);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(CabIdBasicNameValuePair);
			nameValuePairList.add(OwnerNameBasicNameValuePair);
			nameValuePairList.add(OwnerNumberBasicNameValuePair);
			nameValuePairList.add(MemberNameBasicNameValuePair);
			nameValuePairList.add(MemberNumberBasicNameValuePair);
			nameValuePairList.add(MemberLocationAddressBasicNameValuePair);
			nameValuePairList.add(MemberLocationlatlongBasicNameValuePair);
			nameValuePairList.add(StatusBasicNameValuePair);
			nameValuePairList.add(MessageBasicNameValuePair);

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
				joinpoolresponse = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("joinpoolresponse", "" + stringBuilder.toString());
		}
	}

	// //////////////////
	// ///////

	private class ConnectionTaskFordroppool extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(
				MemberRideFragmentActivity.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectiondroppool mAuth1 = new AuthenticateConnectiondroppool();
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
				Toast.makeText(MemberRideFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			Intent mainIntent = new Intent(MemberRideFragmentActivity.this,
					HomeActivity.class);
			mainIntent.putExtra("from", "normal");
			mainIntent.putExtra("message", "null");
			mainIntent.putExtra("CabId", "null");
			mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(mainIntent);
		}

	}

	public class AuthenticateConnectiondroppool {

		public AuthenticateConnectiondroppool() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl + "/dropapool.php";
			HttpPost httpPost = new HttpPost(url_select);

			BasicNameValuePair CabIdBasicNameValuePair = new BasicNameValuePair(
					"CabId", CabId);
			BasicNameValuePair SentMemberNameBasicNameValuePair = new BasicNameValuePair(
					"SentMemberName", FullName);
			BasicNameValuePair SentMemberNumberBasicNameValuePair = new BasicNameValuePair(
					"SentMemberNumber", MemberNumberstr);
			BasicNameValuePair ReceiveMemberNameBasicNameValuePair = new BasicNameValuePair(
					"ReceiveMemberName", OwnerName);
			BasicNameValuePair ReceiveMemberNumberBasicNameValuePair = new BasicNameValuePair(
					"ReceiveMemberNumber", MobileNumber);
			BasicNameValuePair MessageBasicNameValuePair = new BasicNameValuePair(
					"Message", FullName + " left your ride from "
							+ FromShortName + " to " + ToShortName);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(CabIdBasicNameValuePair);
			nameValuePairList.add(SentMemberNameBasicNameValuePair);
			nameValuePairList.add(SentMemberNumberBasicNameValuePair);
			nameValuePairList.add(ReceiveMemberNameBasicNameValuePair);
			nameValuePairList.add(ReceiveMemberNumberBasicNameValuePair);
			nameValuePairList.add(MessageBasicNameValuePair);

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
				droppoolresp = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("droppoolresp", "" + stringBuilder.toString());
		}
	}

	private void showAlertDialog(String mname, final String mnum,
			String mlocadd, String mimgname) {

		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.memberdeatilspopupbyuser);

		memimage = (CircularImageView) dialog.findViewById(R.id.memimage);

		String url1 = GlobalVariables.ServiceUrl + "/ProfileImages/" + mimgname;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new DownloadImageTask().executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, url1);
		} else {
			new DownloadImageTask().execute(url1);
		}

		TextView memname = (TextView) dialog.findViewById(R.id.memname);
		memname.setText(mname.toUpperCase());
		dialog.show();

		TextView memlocationadd = (TextView) dialog
				.findViewById(R.id.memlocationadd);
		memlocationadd.setText(mlocadd);

		LinearLayout call = (LinearLayout) dialog.findViewById(R.id.call);
		call.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				tracker.send(new HitBuilders.EventBuilder()
						.setCategory("Call Member").setAction("Call Member")
						.setLabel("Call Member").build());

				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:" + mnum));
				startActivity(intent);

				dialog.dismiss();
			}
		});

		LinearLayout addtocontacts = (LinearLayout) dialog
				.findViewById(R.id.addtocontacts);

		if (phonenoarraynew.indexOf(mnum.toString().trim().substring(4)) != -1) {

			addtocontacts.setVisibility(View.GONE);
		} else {
			addtocontacts.setVisibility(View.VISIBLE);
		}

		// Boolean chk = null;
		// Log.i("number array", "" + phonenoarray.toString());
		//
		// for (int i = 0; i < phonenoarray.size(); i++) {
		//
		// if (phonenoarray.get(i).toString().trim()
		// .equalsIgnoreCase(mnum.toString().trim())) {
		// chk = true;
		// break;
		// } else {
		// chk = false;
		// }
		// }
		//
		// if (chk) {
		// addtocontacts.setVisibility(View.GONE);
		// } else {
		// addtocontacts.setVisibility(View.VISIBLE);
		// }

		addtocontacts.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(Intent.ACTION_INSERT,
						ContactsContract.Contacts.CONTENT_URI);
				intent = new Intent(
						ContactsContract.Intents.SHOW_OR_CREATE_CONTACT, Uri
								.parse("tel:" + mnum));
				startActivity(intent);

				dialog.dismiss();
			}
		});
		dialog.show();
	}

	private void joinPoolPopUp(String Fm, String tol, String td, String tt,
			String st, String ownname, String ownimg) {

		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.joinpoolpopup);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
		wmlp.gravity = Gravity.TOP | Gravity.LEFT;
		wmlp.x = 10; // x position
		wmlp.y = 10 + mydetailbtn.getHeight() + 10; // y position
		dialog.getWindow().setAttributes(wmlp);

		CircularImageView myridesbannerimage;
		TextView myridesbannerusername;
		TextView fromtolocationvalue;
		TextView datetext;
		TextView timetext;
		TextView seatstext;
		TextView tvAvSeats;

		LinearLayout joinedmembersll;
		ListView joinedmemberslist;

		myridesbannerimage = (CircularImageView) dialog
				.findViewById(R.id.myridesbannerimage);
		myridesbannerusername = (TextView) dialog
				.findViewById(R.id.myridesbannerusername);
		fromtolocationvalue = (TextView) dialog
				.findViewById(R.id.fromtolocationvalue);
		datetext = (TextView) dialog.findViewById(R.id.datetext);
		timetext = (TextView) dialog.findViewById(R.id.timetext);
		seatstext = (TextView) dialog.findViewById(R.id.seatstext1);
		tvAvSeats = (TextView) dialog.findViewById(R.id.tvAvSeats1);

		joinedmembersll = (LinearLayout) dialog
				.findViewById(R.id.joinedmembersll);
		joinedmemberslist = (ListView) dialog
				.findViewById(R.id.joinedmemberslist);

		AQuery aq = new AQuery(MemberRideFragmentActivity.this);

		if (ownimg == null || ownimg.toString().trim().isEmpty()) {

		} else {

			String url = GlobalVariables.ServiceUrl + "/ProfileImages/"
					+ ownimg.toString().trim();

			aq.id(myridesbannerimage).image(url, true, true);
		}
		myridesbannerusername.setText(ownname.toString().trim());
		fromtolocationvalue.setText(Fm.toString().trim() + " > "
				+ tol.toString().trim());
		datetext.setText(td.toString().trim());
		timetext.setText(tt.toString().trim());
		// seatstext.setText(st.toString().trim() + " Seats");
		try {
			String[] arr = st.toString().trim().split("/");

			int total = Integer.parseInt(arr[1]);
			int filled = Integer.parseInt(arr[0]);
			int ava = total - filled;
			seatstext.setText("Total seats : "
					+ (total + StringTags.TAT_ADD_TOTAL));
			tvAvSeats.setText("Available : " + ava);
		} catch (Exception e) {
			seatstext.setText("Total seats : ");
			tvAvSeats.setText("Available : ");
		}

		if (checkpoolalreadyjoinresp.equalsIgnoreCase("fresh pool")) {

			joinedmembersll.setVisibility(View.GONE);

		} else {

			ArrayList<String> JoinedMemberName = new ArrayList<String>();
			ArrayList<String> joinedMemberImageName = new ArrayList<String>();

			JoinedMemberName.add(FullName.toString().trim());

			SharedPreferences mPrefs111 = getSharedPreferences("userimage", 0);
			String imgname = mPrefs111.getString("imgname", "");

			if (imgname.isEmpty() || imgname == null
					|| imgname.equalsIgnoreCase("")) {

				joinedMemberImageName.add("");
			} else {

				joinedMemberImageName.add(imgname.toString().trim());
			}

			if (showmembersresp.equalsIgnoreCase("No Members joined yet")) {

			} else {

				try {
					JSONArray subArray = new JSONArray(showmembersresp);
					for (int i = 0; i < subArray.length(); i++) {
						try {
							JoinedMemberName.add(subArray.getJSONObject(i)
									.getString("MemberName").toString().trim());
							joinedMemberImageName.add(subArray.getJSONObject(i)
									.getString("MemberImageName").toString()
									.trim());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			joinedmembersll.setVisibility(View.VISIBLE);

			ListViewAdapterJoined adapter = new ListViewAdapterJoined(
					MemberRideFragmentActivity.this, joinedMemberImageName,
					JoinedMemberName);
			joinedmemberslist.setAdapter(adapter);

		}

		if (BookingRefNo.isEmpty() || BookingRefNo.equalsIgnoreCase("null")) {
			LinearLayout linearLayout = (LinearLayout) dialog
					.findViewById(R.id.cabbookingll);
			linearLayout.setVisibility(View.GONE);
		} else {
			TextView textView = (TextView) dialog
					.findViewById(R.id.cabbookingdriver);
			textView.setText("Driver : " + DriverName);

			textView = (TextView) dialog
					.findViewById(R.id.cabbookingdriverphone);
			textView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_CALL, Uri
							.parse("tel:" + DriverNumber));
					startActivity(intent);
				}
			});

			textView = (TextView) dialog.findViewById(R.id.cabbookingvehicle);
			textView.setText("Vehicle : " + CarNumber);

			textView = (TextView) dialog.findViewById(R.id.cabbookingrefno);
			textView.setText("Booking reference : " + BookingRefNo);

			Button button = (Button) dialog.findViewById(R.id.cancelBooking);
			button.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					AlertDialog.Builder builder = new AlertDialog.Builder(
							MemberRideFragmentActivity.this);
					builder.setMessage("Are you sure you want to cancel the booking?");

					builder.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									if (CabName.equalsIgnoreCase("Uber")) {

									} else if (CabName.equalsIgnoreCase("Mega")) {
										cancelMegaCab();
									} else if (CabName
											.equalsIgnoreCase("Taxi For Sure")) {
										cancelTFSCab();
									}
								}
							});

					builder.setNegativeButton("NO",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							});

					AlertDialog dialog = builder.show();
					TextView messageText = (TextView) dialog
							.findViewById(android.R.id.message);
					messageText.setGravity(Gravity.CENTER);
					dialog.show();
				}
			});
		}

		dialog.show();

	}

	public class ListViewAdapterJoined extends BaseAdapter {

		// Declare Variables
		Context context;
		ArrayList<String> memimagename;
		ArrayList<String> memusername;
		LayoutInflater inflater;
		AQuery aq;

		public ListViewAdapterJoined(Context context,
				ArrayList<String> mimgname, ArrayList<String> mname) {
			this.context = context;
			this.memimagename = mimgname;
			this.memusername = mname;
			this.aq = new AQuery(context);
		}

		@Override
		public int getCount() {
			return memusername.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@SuppressLint("ViewHolder")
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			CircularImageView memberjoinedimage;
			TextView memberjoinedname;

			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View itemView = inflater.inflate(R.layout.joinedmem_list_row,
					parent, false);

			// Locate the TextViews in listview_item.xml

			memberjoinedimage = (CircularImageView) itemView
					.findViewById(R.id.memberjoinedimage);
			memberjoinedname = (TextView) itemView
					.findViewById(R.id.memberjoinedname);

			if (memimagename.get(position).toString().trim().isEmpty()) {

				Log.d("image nahi hai", ""
						+ memimagename.get(position).toString().trim());

			} else {
				String url = GlobalVariables.ServiceUrl + "/ProfileImages/"
						+ memimagename.get(position).toString().trim();
				aq.id(memberjoinedimage).image(url, true, true);
			}
			memberjoinedname.setText(memusername.get(position).toString()
					.trim());

			return itemView;
		}
	}

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.d("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			memimage.setImageBitmap(result);
		}
	}

	// /////////////////////
	// ///////

	private class ConnectionTaskForDirectionsnew extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionGetDirectionnew mAuth1 = new AuthenticateConnectionGetDirectionnew();
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
				Toast.makeText(MemberRideFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			joinpoolmap.clear();

			for (int i = 0; i < steps.size(); i++) {

				ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();

				JSONArray subArray123;
				try {
					subArray123 = new JSONArray(steps.get(i));
					for (int i111 = 0; i111 < subArray123.length(); i111++) {

						String locationstr = subArray123.getJSONObject(i111)
								.getString("start_location").toString();

						JSONObject jsonObject11 = new JSONObject(locationstr);
						double lat1 = Double.parseDouble(jsonObject11
								.getString("lat"));
						double lng1 = Double.parseDouble(jsonObject11
								.getString("lng"));

						listGeopoints.add(new LatLng(lat1, lng1));

						// /
						String locationstr1 = subArray123.getJSONObject(i111)
								.getString("polyline").toString();

						JSONObject jsonObject111 = new JSONObject(locationstr1);
						String points = jsonObject111.getString("points");
						ArrayList<LatLng> arr = decodePoly(points);
						for (int j = 0; j < arr.size(); j++) {
							listGeopoints.add(new LatLng(arr.get(j).latitude,
									arr.get(j).longitude));
						}
						// /
						String locationstr11 = subArray123.getJSONObject(i111)
								.getString("end_location").toString();

						JSONObject jsonObject1111 = new JSONObject(
								locationstr11);
						double lat11 = Double.parseDouble(jsonObject1111
								.getString("lat"));
						double lng11 = Double.parseDouble(jsonObject1111
								.getString("lng"));

						listGeopoints.add(new LatLng(lat11, lng11));

						Random rnd = new Random();
						int color = Color.argb(255, rnd.nextInt(256),
								rnd.nextInt(256), rnd.nextInt(256));

						PolylineOptions rectLine = new PolylineOptions().width(
								5).color(color);
						rectLine.addAll(listGeopoints);
						rectlinesarr.add(rectLine);

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			LatLngBounds.Builder bc = null;

			for (int i = 0; i < rectlinesarr.size(); i++) {
				joinpoolmap.addPolyline(rectlinesarr.get(i));

				List<LatLng> points = rectlinesarr.get(i).getPoints();

				bc = new LatLngBounds.Builder();

				for (LatLng item : points) {
					bc.include(item);
				}
			}

			joinpoolmap.moveCamera(CameraUpdateFactory.newLatLngBounds(
					bc.build(), 50));

			joinpoolmap.addMarker(new MarkerOptions()
					.position(startaddlatlng.get(0))
					.title(startaddress.get(0))
					.snippet("start")
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.start)));

			joinpoolmap
					.addMarker(new MarkerOptions()
							.position(endaddlatlng.get(0))
							.title(endaddress.get(0))
							.snippet("end")
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.end)));

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForShowMembersOnMap()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new ConnectionTaskForShowMembersOnMap().execute();
			}

		}

	}

	public class AuthenticateConnectionGetDirectionnew {

		public AuthenticateConnectionGetDirectionnew() {

		}

		public void connection() throws Exception {

			steps.clear();
			Summary.clear();
			startaddress.clear();
			endaddress.clear();
			startaddlatlng.clear();
			endaddlatlng.clear();
			listGeopoints.clear();
			via_waypoint.clear();
			via_waypointstrarr.clear();
			rectlinesarr.clear();

			JSONObject jsonObject = new JSONObject(CompletePageResponse);

			String name = jsonObject.getString("routes");

			JSONArray subArray = new JSONArray(name);

			for (int i = 0; i < subArray.length(); i++) {

				Summary.add(subArray.getJSONObject(i).getString("summary")
						.toString());

				String name1 = subArray.getJSONObject(i).getString("legs")
						.toString();

				JSONArray subArray1 = new JSONArray(name1);

				for (int i1 = 0; i1 < subArray1.length(); i1++) {

					startaddress.add(subArray1.getJSONObject(i1)
							.getString("start_address").toString());
					endaddress.add(subArray1.getJSONObject(i1)
							.getString("end_address").toString());

					String startadd = subArray1.getJSONObject(i1)
							.getString("start_location").toString();

					JSONObject jsonObject1 = new JSONObject(startadd);
					double lat = Double.parseDouble(jsonObject1
							.getString("lat"));
					double lng = Double.parseDouble(jsonObject1
							.getString("lng"));

					startaddlatlng.add(new LatLng(lat, lng));

					//
					String endadd = subArray1.getJSONObject(i1)
							.getString("end_location").toString();

					JSONObject jsonObject41 = new JSONObject(endadd);
					double lat4 = Double.parseDouble(jsonObject41
							.getString("lat"));
					double lng4 = Double.parseDouble(jsonObject41
							.getString("lng"));

					endaddlatlng.add(new LatLng(lat4, lng4));

					// ////////////

					steps.add(subArray1.getJSONObject(i1).getString("steps")
							.toString());

					// //////////////
					String mska = subArray1.getJSONObject(i1)
							.getString("via_waypoint").toString();

					if (mska.equalsIgnoreCase("[]")) {
						via_waypoint.add(new LatLng(0, 0));
					} else {
						JSONArray subArray12 = new JSONArray(mska);

						for (int i11 = 0; i11 < subArray12.length(); i11++) {

							String locationstr = subArray12.getJSONObject(i11)
									.getString("location").toString();

							JSONObject jsonObject1111 = new JSONObject(
									locationstr);
							double lat1111 = Double.parseDouble(jsonObject1111
									.getString("lat"));
							double lng1111 = Double.parseDouble(jsonObject1111
									.getString("lng"));

							via_waypoint.add(new LatLng(lat1111, lng1111));

						}
					}
				}
			}

			// /////
			Log.d("Summary", "" + Summary);
			Log.d("startaddress", "" + startaddress);
			Log.d("endaddress", "" + endaddress);
			Log.d("startaddlatlng", "" + startaddlatlng);
			Log.d("endaddlatlng", "" + endaddlatlng);
			Log.d("via_waypoint", "" + via_waypoint);

			for (int i = 0; i < via_waypoint.size(); i++) {
				String asd = MapUtilityMethods.getAddress(
						MemberRideFragmentActivity.this,
						via_waypoint.get(i).latitude,
						via_waypoint.get(i).longitude);
				via_waypointstrarr.add(asd);
			}
			Log.d("via_waypointstrarr", "" + via_waypointstrarr);
		}
	}

	// //////////////////
	// ///////

	private class ConnectionTaskForsendcustommessage extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(
				MemberRideFragmentActivity.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionsendcustommessage mAuth1 = new AuthenticateConnectionsendcustommessage();
			try {
				mAuth1.memnum = args[0];
				mAuth1.mess = args[1];
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
				Toast.makeText(MemberRideFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}
		}

	}

	public class AuthenticateConnectionsendcustommessage {

		public String memnum;
		public String mess;

		public AuthenticateConnectionsendcustommessage() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/sendcustommessage.php";
			HttpPost httpPost = new HttpPost(url_select);

			BasicNameValuePair MemberNumberBasicNameValuePair = new BasicNameValuePair(
					"MemberNumber", memnum);
			BasicNameValuePair MessageBasicNameValuePair = new BasicNameValuePair(
					"Message", mess);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MemberNumberBasicNameValuePair);
			nameValuePairList.add(MessageBasicNameValuePair);

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
				sendcustommessagefrompopupresp = stringBuilder.append(
						bufferedStrChunk).toString();
			}

			Log.d("sendcustommessagefrompopupresp",
					"" + stringBuilder.toString());
		}
	}

	private void showAlertDialogmylocation(String mname, final String mnum,
			String mlocadd, String mimgname) {

		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.mylocationpopup);

		memimage = (CircularImageView) dialog.findViewById(R.id.memimage);

		String url1 = GlobalVariables.ServiceUrl + "/ProfileImages/" + mimgname;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new DownloadImageTask().executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, url1);
		} else {
			new DownloadImageTask().execute(url1);
		}

		TextView memname = (TextView) dialog.findViewById(R.id.memname);
		memname.setText(mname.toUpperCase());
		dialog.show();

		TextView memlocationadd = (TextView) dialog
				.findViewById(R.id.memlocationadd);
		memlocationadd.setText(mlocadd);

		dialog.show();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				final String updatedlocationaddress = data
						.getStringExtra("memberlocationaddress");
				String updatedlocationlatlong = data
						.getStringExtra("memberlocationlatlong");

				if (mylocationmarker != null) {
					mylocationmarker.remove();
				}

				String[] latlong = updatedlocationlatlong.split(",");
				LatLng lt = new LatLng(Double.parseDouble(latlong[0]),
						Double.parseDouble(latlong[1]));

				mylocationmarker = joinpoolmap
						.addMarker(new MarkerOptions()
								.position(lt)
								.snippet("mylocation")
								.title(updatedlocationaddress)
								.icon(BitmapDescriptorFactory
										.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

				joinpoolmap
						.setOnMarkerClickListener(new OnMarkerClickListener() {

							@Override
							public boolean onMarkerClick(Marker arg0) {

								if (arg0.getSnippet().equals("start")) {

									showAlertDialog(OwnerName, MobileNumber,
											FromLocation, OwnerImage);

								} else if (arg0.getSnippet().equals("end")) {

									showAlertDialog(OwnerName, MobileNumber,
											ToLocation, OwnerImage);

								} else if (arg0.getSnippet().equals(
										"mylocation")) {

									showAlertDialogmylocation(usermemname,
											usermemnumber,
											updatedlocationaddress,
											usermemimagename);
								} else {

									if (checkpoolalreadyjoinresp
											.equalsIgnoreCase("fresh pool")) {

										Toast.makeText(
												MemberRideFragmentActivity.this,
												"Join ride to see details of other members",
												Toast.LENGTH_LONG).show();
									} else {
										final Integer index = Integer
												.parseInt(arg0.getSnippet());

										showAlertDialog(ShowMemberName
												.get(index), ShowMemberNumber
												.get(index),
												ShowMemberLocationAddress
														.get(index),
												ShowMemberImageName.get(index));
									}

								}

								return true;
							}

						});
			}
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new ConnectionTaskForupdatestatus().executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, CabId, MemberNumberstr,
					"online");
		} else {
			new ConnectionTaskForupdatestatus().execute(CabId, MemberNumberstr,
					"online");
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new ConnectionTaskForTripCompletedtask().executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, CabId, MemberNumberstr);
		} else {
			new ConnectionTaskForTripCompletedtask().execute(CabId,
					MemberNumberstr);
		}

		Log.d("joinpool onStart", "joinpool onStart");
		if (xmppConnection != null && xmppConnection.isConnected()) {
			Log.d("12",
					"connection already connected : "
							+ xmppConnection.isConnected());

		} else {
			establishXmppConnection();
			onRecieveMsg();
		}

		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		Log.d("joinpool onStop", "joinpool onStop");

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new ConnectionTaskForupdatestatus().executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, CabId, MemberNumberstr,
					"offline");
		} else {
			new ConnectionTaskForupdatestatus().execute(CabId, MemberNumberstr,
					"offline");
		}

		if (locationManager != null)
			locationManager.removeUpdates(MemberRideFragmentActivity.this);

		super.onStop();
	}

	@Override
	public void onBackPressed() {

		if (comefrom != null) {

			if (!chatlayoutmainrl.isShown()) {
				Intent mainIntent = new Intent(MemberRideFragmentActivity.this,
						HomeActivity.class);
				mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			} else {
				chatlayoutmainrl.setVisibility(View.GONE);
			}

		} else {

			if (!chatlayoutmainrl.isShown()) {
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
				MemberRideFragmentActivity.this.finish();
			} else {
				chatlayoutmainrl.setVisibility(View.GONE);
			}
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

	// ///////////////////// chat code

	private void establishXmppConnection() {
		try {
			AndroidConnectionConfiguration connConfig = new AndroidConnectionConfiguration(
					IPADDRESS, PORT, IPADDRESS);
			SmackConfiguration.setDefaultPingInterval(360);
			SmackConfiguration.setKeepAliveInterval(1000 * 30);
			connConfig.setReconnectionAllowed(true);
			connConfig.setSASLAuthenticationEnabled(true);
			connConfig.setRosterLoadedAtLogin(true);
			xmppConnection = new XMPPConnection(connConfig);
			if (xmppConnection != null) {
				String uname = MemberNumberstr.toString().trim() + "_" + CabId, pwd = MemberNumberstr
						.toString().trim() + "_" + CabId, name = FullName, email = "";
				HashMap<String, String> hashMap = new HashMap<String, String>();
				hashMap.put("username", uname);
				hashMap.put("password", pwd);
				hashMap.put("name", name);
				hashMap.put("email", email);
				connect(hashMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void connect(final HashMap<String, String> hashMap) {
		try {

			final String uname = hashMap.get("username");
			final String pwd = hashMap.get("password");

			Thread XMPPConnect = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						xmppConnection.connect();
						// chatBubbleList = new ArrayList<ChatBubble>();
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}

					try {
						AccountManager am = new AccountManager(xmppConnection);
						am.createAccount(uname, pwd, hashMap);
						Log.d("Openfire", "Account Created");
					} catch (Exception e) {
						Log.e("Openfire", "Account already exist");
					}

					try {
						xmppConnection.login(uname, pwd);
						Presence presence = new Presence(
								Presence.Type.available);
						xmppConnection.sendPacket(presence);
						Roster roster = xmppConnection.getRoster();
						String[] urlArray = new String[] { "users" };

						try {
							roster.createEntry(uname, pwd, urlArray);
						} catch (Exception e) {
							e.printStackTrace();
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			XMPPConnect.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void onSendMsg(final String text) {
		try {

			for (int i = 0; i < MemberNumber.size(); i++) {
				String userid = MemberNumber.get(i).toString().trim() + "_"
						+ CabId + "@" + IPADDRESS;
				message = new Message(userid, Message.Type.chat);
				message.setBody(text);
				xmppConnection.sendPacket(message);
			}

			String datetime = String.valueOf(System.currentTimeMillis());
			db.addChattodb(new ChatObject(CabId, FullName, text, datetime));

			chatBubbleList.add(new ChatBubble(false, text, System
					.currentTimeMillis(), FullName));
			setListAdapter();

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForsendchatnotification().executeOnExecutor(
						AsyncTask.THREAD_POOL_EXECUTOR, CabId, MemberNumberstr,
						MemberNumber.toString().trim(), text, FullName,
						OwnerName, OwnerMobileNumber);
			} else {
				new ConnectionTaskForsendchatnotification().execute(CabId,
						MemberNumberstr, MemberNumber.toString().trim(), text,
						FullName, OwnerName, OwnerMobileNumber);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void onRecieveMsg() {

		try {
			PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
			packetListener = new PacketListener() {
				@Override
				public void processPacket(Packet packet) {

					message = (Message) packet;
					if (message.getBody() != null) {

						String msgReceivedFrom = StringUtils.parseName(message
								.getFrom());
						String text = message.getBody();

						String[] arr = msgReceivedFrom.split("@");

						String[] arr1 = arr[0].toString().trim().split("_");

						String usermobilenum = arr1[0].toString().trim();
						String username = null;

						for (int i = 0; i < MemberNumber.size(); i++) {

							if (usermobilenum.equalsIgnoreCase(MemberNumber
									.get(i).toString().trim())) {
								username = MemberName.get(i).toString().trim();
								break;
							} else {
								username = usermobilenum;
							}
						}

						String datetime = String.valueOf(System
								.currentTimeMillis());
						db.addChattodb(new ChatObject(CabId, username, text,
								datetime));

						chatBubbleList.add(new ChatBubble(true, text, System
								.currentTimeMillis(), username));
						setListAdapter();

					}
				}
			};
			xmppConnection.addPacketListener(packetListener, filter);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* setListAdapter */
	private void setListAdapter() {

		try {

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					messageAdapter = new MessageAdapter(chatBubbleList);
					listViewMsg.setAdapter(messageAdapter);
					listViewMsg.post(new Runnable() {
						@Override
						public void run() {
							listViewMsg.setSelection(messageAdapter.getCount() - 1);
						}
					});
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/* ViewHolder */
	public static class ViewHolder {
		TextView textViewMembernameLeft, textViewMembernameRight;
		TextView textViewMessageLeft, textViewMessageRight;
		TextView textViewDateTimeLeft, textViewDateTimeRight;
		LinearLayout linearLayoutMessageDateTimeRight,
				linearLayoutMessageDateTimeLeft;
	}

	/* Message Adapter */
	private class MessageAdapter extends BaseAdapter {
		private ArrayList<ChatBubble> chatBubbleListforAdapter;

		public MessageAdapter(ArrayList<ChatBubble> chatBubbleList) {
			this.chatBubbleListforAdapter = chatBubbleList;
		}

		@Override
		public int getCount() {
			return chatBubbleListforAdapter.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View view, ViewGroup viewGroup) {
			ViewHolder viewHolder = null;
			if (view == null) {
				viewHolder = new ViewHolder();
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.list_row_message, viewGroup,
						false);

				viewHolder.textViewMembernameLeft = (TextView) view
						.findViewById(R.id.textViewMembernameLeft);
				viewHolder.textViewMembernameRight = (TextView) view
						.findViewById(R.id.textViewMembernameRight);
				viewHolder.textViewMessageLeft = (TextView) view
						.findViewById(R.id.textViewMessageLeft);
				viewHolder.textViewMessageRight = (TextView) view
						.findViewById(R.id.textViewMessageRight);
				viewHolder.textViewDateTimeRight = (TextView) view
						.findViewById(R.id.textViewDateTimeRight);
				viewHolder.textViewDateTimeLeft = (TextView) view
						.findViewById(R.id.textViewDateTimeLeft);
				// layout user as a container of messsage
				viewHolder.linearLayoutMessageDateTimeRight = (LinearLayout) view
						.findViewById(R.id.linearLayoutMessageDateTimeRight);
				viewHolder.linearLayoutMessageDateTimeLeft = (LinearLayout) view
						.findViewById(R.id.linearLayoutMessageDateTimeLeft);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}
			if (viewHolder != null) {
				try {
					String dateString = convertTime(chatBubbleListforAdapter
							.get(position).timeStamp + "");

					if ((chatBubbleListforAdapter.get(position).left == false)
							&& (chatBubbleListforAdapter.get(position).timeStamp != 0)) {
						viewHolder.linearLayoutMessageDateTimeLeft
								.setVisibility(View.GONE);
						viewHolder.linearLayoutMessageDateTimeRight
								.setVisibility(View.VISIBLE);
						viewHolder.textViewMembernameRight
								.setText(chatBubbleListforAdapter.get(position).sendername);
						viewHolder.textViewMessageRight
								.setText(chatBubbleListforAdapter.get(position).message);
						viewHolder.textViewDateTimeRight.setText(dateString);

					} else if ((chatBubbleListforAdapter.get(position).left == true)
							&& (chatBubbleListforAdapter.get(position).timeStamp != 0)) {
						viewHolder.linearLayoutMessageDateTimeLeft
								.setVisibility(View.VISIBLE);
						viewHolder.linearLayoutMessageDateTimeRight
								.setVisibility(View.GONE);
						viewHolder.textViewMembernameLeft
								.setText(chatBubbleListforAdapter.get(position).sendername);
						viewHolder.textViewMessageLeft
								.setText(chatBubbleListforAdapter.get(position).message);
						viewHolder.textViewDateTimeLeft.setText(dateString);
					}
				} catch (Exception e) {
					e.printStackTrace();
					view = null;
				}
			}
			return view;
		}

	}// end of adapter class

	public String convertTime(String timeStamp) {
		try {
			Date date = new Date(Long.parseLong(timeStamp));
			Format format = new SimpleDateFormat("dd MMM, h:mm a");
			return format.format(date);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	// ///////
	private class ConnectionTaskForsendchatnotification extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionsendchatnotification mAuth1 = new AuthenticateConnectionsendchatnotification();
			try {
				mAuth1.cid = args[0];
				mAuth1.mnumstr = args[1];
				mAuth1.memnum = args[2];
				mAuth1.txtmsg = args[3];
				mAuth1.name = args[4];
				mAuth1.ownername = args[5];
				mAuth1.ownermnum = args[6];

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
				Toast.makeText(MemberRideFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}
		}

	}

	public class AuthenticateConnectionsendchatnotification {

		public String cid;
		public String mnumstr;
		public String memnum;
		public String txtmsg;
		public String name;
		public String ownername;
		public String ownermnum;

		public AuthenticateConnectionsendchatnotification() {

		}

		public void connection() throws Exception {

			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/sendchatnotification.php";
			HttpPost httpPost = new HttpPost(url_select);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();

			BasicNameValuePair CabIdValuePair = new BasicNameValuePair("CabId",
					cid);
			BasicNameValuePair MemberNumberValuePair = new BasicNameValuePair(
					"MemberNumber", mnumstr);
			BasicNameValuePair MemberNumberARRValuePair = new BasicNameValuePair(
					"MemberNumberARR", memnum);
			BasicNameValuePair MessageValuePair = new BasicNameValuePair(
					"Message", txtmsg);
			BasicNameValuePair MemberNameValuePair = new BasicNameValuePair(
					"MemberName", name);
			BasicNameValuePair ownernameValuePair = new BasicNameValuePair(
					"ownername", ownername);
			BasicNameValuePair ownernumberValuePair = new BasicNameValuePair(
					"ownernumber", ownermnum);

			nameValuePairList.add(CabIdValuePair);
			nameValuePairList.add(MemberNumberValuePair);
			nameValuePairList.add(MemberNumberARRValuePair);
			nameValuePairList.add(MessageValuePair);
			nameValuePairList.add(MemberNameValuePair);
			nameValuePairList.add(ownernameValuePair);
			nameValuePairList.add(ownernumberValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;
			String chatresp = null;

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				chatresp = stringBuilder.append(bufferedStrChunk).toString();
			}

			Log.d("chatresp", "" + chatresp);
		}
	}

	// /////

	private class ConnectionTaskForupdatestatus extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionupdatestatus mAuth1 = new AuthenticateConnectionupdatestatus();
			try {
				mAuth1.cabid = args[0];
				mAuth1.mnum = args[1];
				mAuth1.status = args[2];
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
				Toast.makeText(MemberRideFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}
		}

	}

	public class AuthenticateConnectionupdatestatus {

		public String cabid;
		public String mnum;
		public String status;

		public AuthenticateConnectionupdatestatus() {

		}

		public void connection() throws Exception {

			if (status.equalsIgnoreCase("offline")) {

				try {
					if (xmppConnection != null)
						xmppConnection.disconnect();
				} catch (Exception e) {

					e.printStackTrace();
				}
			}

			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/changeuserstatus.php";
			HttpPost httpPost = new HttpPost(url_select);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();

			BasicNameValuePair CabIdValuePair = new BasicNameValuePair("CabId",
					cabid);
			BasicNameValuePair MemberNumberValuePair = new BasicNameValuePair(
					"MemberNumber", mnum);
			BasicNameValuePair chatstatusValuePair = new BasicNameValuePair(
					"chatstatus", status);
			BasicNameValuePair IsOwnerValuePair;
			if (mnum.equalsIgnoreCase(OwnerMobileNumber)) {

				IsOwnerValuePair = new BasicNameValuePair("IsOwner", "Y");
			} else {
				IsOwnerValuePair = new BasicNameValuePair("IsOwner", "N");
			}

			nameValuePairList.add(CabIdValuePair);
			nameValuePairList.add(MemberNumberValuePair);
			nameValuePairList.add(chatstatusValuePair);
			nameValuePairList.add(IsOwnerValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

		}
	}

	// /////
	private class ConnectionTaskForTripCompletedtask extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionTripCompletedtask mAuth1 = new AuthenticateConnectionTripCompletedtask();
			try {
				mAuth1.cabid = args[0];
				mAuth1.mnum = args[1];
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
				Toast.makeText(MemberRideFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (MemberNumberstr.equalsIgnoreCase(OwnerMobileNumber)) {

			} else {
				MemberName.add(OwnerName);
				MemberNumber.add(OwnerMobileNumber);
			}
		}

	}

	public class AuthenticateConnectionTripCompletedtask {

		public String cabid;
		public String mnum;

		public AuthenticateConnectionTripCompletedtask() {

		}

		public void connection() throws Exception {

			MemberName.clear();
			MemberNumber.clear();

			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/tripcompletedmembers.php";
			HttpPost httpPost = new HttpPost(url_select);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();

			BasicNameValuePair CabIdValuePair = new BasicNameValuePair("CabId",
					cabid);
			BasicNameValuePair MemberNumberValuePair = new BasicNameValuePair(
					"MemberNumber", mnum);
			nameValuePairList.add(CabIdValuePair);
			nameValuePairList.add(MemberNumberValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;

			String result = null;
			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				result = stringBuilder.append(bufferedStrChunk).toString();
			}

			Log.d("result", "" + result);

			if (result.equalsIgnoreCase("No Members joined yet")) {

			} else {
				JSONArray subArray = new JSONArray(result);
				for (int i = 0; i < subArray.length(); i++) {
					MemberName.add(subArray.getJSONObject(i)
							.getString("MemberName").toString());
					MemberNumber.add(subArray.getJSONObject(i)
							.getString("MemberNumber").toString());

				}
			}
		}
	}

	// /////
	private class ConnectionTaskForOwnerLocation extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionOwnerLocationtask mAuth1 = new AuthenticateConnectionOwnerLocationtask();
			try {
				mAuth1.cabid = args[0];
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
				// Toast.makeText(MemberRideFragmentActivity.this,
				// getResources().getString(R.string.exceptionstring),
				// Toast.LENGTH_LONG).show();
				return;
			}

			try {
				JSONObject jsonObject = new JSONObject(ownerlocation);

				if (jsonObject.get("msg").toString()
						.equalsIgnoreCase("success")) {

					LatLng latLng = new LatLng(Double.parseDouble(jsonObject
							.get("ownerLat").toString()),
							Double.parseDouble(jsonObject.get("ownerLng")
									.toString()));

					String locationUpdatedAt = jsonObject.get(
							"locationUpdatedAt").toString();

					Log.d("MemberRideFragmentActivity", "Owner location : "
							+ latLng);

					if (!refreshlocationbtn.isShown()) {
						refreshlocationbtn.setVisibility(View.VISIBLE);
					}

					if (ownerMarker != null) {
						ownerMarker.remove();
					}

					// MarkerOptions markerOptions = new MarkerOptions()
					// .position(latLng);
					// markerOptions.icon(BitmapDescriptorFactory
					// .fromResource(R.drawable.owner_location_pin));
					// ownerMarker = joinpoolmap.addMarker(markerOptions);
					ownerMarker = joinpoolmap
							.addMarker(new MarkerOptions()
									.position(latLng)
									.title("Last updated at")
									.snippet(locationUpdatedAt)
									.icon(BitmapDescriptorFactory
											.fromResource(R.drawable.owner_location_pin)));

					// checkpoolmap.addMarker(new MarkerOptions()
					// .position(startaddlatlng.get(0))
					// .title(startaddress.get(0))
					// .snippet("start")
					// .icon(BitmapDescriptorFactory
					// .fromResource(R.drawable.start)));
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public class AuthenticateConnectionOwnerLocationtask {

		public String cabid;

		public AuthenticateConnectionOwnerLocationtask() {

		}

		public void connection() throws Exception {

			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/updateOwnerLocation.php";
			HttpPost httpPost = new HttpPost(url_select);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();

			BasicNameValuePair CabIdValuePair = new BasicNameValuePair("cabId",
					cabid);
			nameValuePairList.add(CabIdValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;

			String result = null;
			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				result = stringBuilder.append(bufferedStrChunk).toString();
			}

			Log.d("ownerlocation", "" + result);
			ownerlocation = result;
		}
	}

	@Override
	public void sendFareSplitHashMap(final HashMap<String, Double> hashMap) {
		Log.d("JoinPool", "sendFareSplitHashMap : " + hashMap);

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (onedialog.isShowing()) {
					onedialog.dismiss();
				}

				if (hashMap == null) {
					Toast.makeText(MemberRideFragmentActivity.this,
							getResources().getString(R.string.exceptionstring),
							Toast.LENGTH_LONG).show();
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							MemberRideFragmentActivity.this);
					View builderView = (View) getLayoutInflater().inflate(
							R.layout.fare_split_dialog, null);

					ListView listView = (ListView) builderView
							.findViewById(R.id.listViewFareSplit);

					Button button = (Button) builderView
							.findViewById(R.id.buttonFareSplit);

					builder.setView(builderView);
					final AlertDialog dialog = builder.create();

					ArrayList<String> arrayList = new ArrayList<String>();
					for (String key : hashMap.keySet()) {
						if (!key.equalsIgnoreCase("tripTotalFare")) {
							arrayList.add(key
									+ " : \u20B9 "
									+ String.format("%d%n", Math.round(Double
											.parseDouble(hashMap.get(key)
													.toString()))));
						}
					}
					arrayList.add(
							0,
							"Total Fare : \u20B9 "
									+ hashMap.get("tripTotalFare").toString());

					listView.setAdapter(new ListViewAdapterFareSplit(
							MemberRideFragmentActivity.this, arrayList));

					button.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View view) {
							dialog.dismiss();

							String numberfareString = "";
							for (String key : hashMap.keySet()) {
								if (!key.equalsIgnoreCase("tripTotalFare")) {
									int index = ShowMemberName.indexOf(key);
									if (index != -1) {
										numberfareString += (ShowMemberNumber
												.get(index).toString()
												+ "~"
												+ String.format(
														"%d%n",
														Math.round(Double
																.parseDouble(hashMap
																		.get(key)
																		.toString()))) + ",");
									}
								}

								if (key.equalsIgnoreCase(OwnerName)) {
									numberfareString += (OwnerMobileNumber
											+ "~"
											+ String.format(
													"%d%n",
													Math.round(Double
															.parseDouble(hashMap
																	.get(key)
																	.toString()))) + ",");
								}
							}

							numberfareString = numberfareString.substring(0,
									numberfareString.length() - 1);

							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
								new ConnectionTaskForSaveCalculatedFare()
										.executeOnExecutor(
												AsyncTask.THREAD_POOL_EXECUTOR,
												CabId,
												hashMap.get("tripTotalFare")
														.toString(),
												numberfareString,
												MemberNumberstr,
												OwnerMobileNumber);
							} else {
								new ConnectionTaskForSaveCalculatedFare()
										.execute(CabId,
												hashMap.get("tripTotalFare")
														.toString(),
												numberfareString,
												MemberNumberstr,
												OwnerMobileNumber);
							}
						}
					});

					dialog.show();
				}
			}
		});
	}

	public class ListViewAdapterFareSplit extends BaseAdapter {

		// Declare Variables
		Context context;
		// ArrayList<String> memimagename;
		ArrayList<String> memusername;
		LayoutInflater inflater;

		// AQuery aq;

		public ListViewAdapterFareSplit(Context context, ArrayList<String> mname) {
			this.context = context;
			// this.memimagename = mimgname;
			this.memusername = mname;
			// this.aq = new AQuery(context);
		}

		@Override
		public int getCount() {
			return memusername.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@SuppressLint("ViewHolder")
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			// CircularImageView memberjoinedimage;
			TextView memberjoinedname;

			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View itemView = inflater.inflate(R.layout.fare_split_list_row,
					parent, false);

			// memberjoinedimage = (CircularImageView) itemView
			// .findViewById(R.id.memberjoinedimage);
			memberjoinedname = (TextView) itemView
					.findViewById(R.id.memberfaresplit);

			// if (memimagename.get(position).toString().trim().isEmpty()) {
			//
			// Log.i("image nahi hai", ""
			// + memimagename.get(position).toString().trim());
			//
			// } else {
			// String url = GlobalVariables.ServiceUrl + "/ProfileImages/"
			// + memimagename.get(position).toString().trim();
			// aq.id(memberjoinedimage).image(url, true, true);
			// }
			memberjoinedname.setText(memusername.get(position).toString()
					.trim());

			return itemView;
		}
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

	private class ConnectionTaskForMarkTripCompleted extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			Log.d("MemberRideFragmentActivity",
					"AuthenticateConnectionMarkTripCompleted cabid : "
							+ args[0]);
			AuthenticateConnectionMarkTripCompleted mAuth1 = new AuthenticateConnectionMarkTripCompleted();
			try {
				if (args.length > 1) {
					mAuth1.cabid = args[0];
					mAuth1.owner = args[1];
					mAuth1.mobileNumber = args[2];
				} else {
					mAuth1.cabid = args[0];
				}

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
				Toast.makeText(MemberRideFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			finish();
		}

	}

	public class AuthenticateConnectionMarkTripCompleted {

		public String cabid;
		public String owner;
		public String mobileNumber;

		public AuthenticateConnectionMarkTripCompleted() {

		}

		public void connection() throws Exception {

			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/tripCompleted.php";
			HttpPost httpPost = new HttpPost(url_select);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();

			BasicNameValuePair CabIdValuePair = new BasicNameValuePair("cabId",
					cabid);

			nameValuePairList.add(CabIdValuePair);

			if (owner != null && mobileNumber != null && !owner.isEmpty()
					&& !mobileNumber.isEmpty()) {
				BasicNameValuePair ownerNameValuePair = new BasicNameValuePair(
						"owner", owner);
				BasicNameValuePair mobileNumberNameValuePair = new BasicNameValuePair(
						"mobileNumber", mobileNumber);

				nameValuePairList.add(ownerNameValuePair);
				nameValuePairList.add(mobileNumberNameValuePair);
			}

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;

			String result = null;
			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				result = stringBuilder.append(bufferedStrChunk).toString();
			}

			Log.d("tripCompleted", "tripCompleted : " + result);
		}
	}

	private class ConnectionTaskForTripCompleted extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionTripCompleted mAuth1 = new AuthenticateConnectionTripCompleted();
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
				Toast.makeText(MemberRideFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}
		}

	}

	public class AuthenticateConnectionTripCompleted {

		public String cid;

		public AuthenticateConnectionTripCompleted() {

		}

		public void connection() throws Exception {

			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/updateCabStatus.php";
			HttpPost httpPost = new HttpPost(url_select);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();

			BasicNameValuePair CabIdValuePair = new BasicNameValuePair("cabId",
					cid);

			nameValuePairList.add(CabIdValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;
			String startresp = null;

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				startresp = stringBuilder.append(bufferedStrChunk).toString();
			}

			Log.d("completedresp", "" + startresp);
		}
	}

	private class ConnectionTaskForSaveCalculatedFare extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			Log.d("CheckPoolFragmentActivity",
					"ConnectionTaskForSaveCalculatedFare cabid : " + args[0]
							+ " totalfare : " + args[1] + " numberandfare : "
							+ args[2] + " paidby : " + args[3] + " owner : "
							+ args[4]);
			AuthenticateConnectionSaveCalculatedFare mAuth1 = new AuthenticateConnectionSaveCalculatedFare();
			try {
				mAuth1.cabid = args[0];
				mAuth1.totalfare = args[1];
				mAuth1.numberandfare = args[2];
				mAuth1.paidby = args[3];
				mAuth1.owner = args[4];

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
				Toast.makeText(MemberRideFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}
		}

	}

	public class AuthenticateConnectionSaveCalculatedFare {

		public String cabid;
		public String totalfare;
		public String numberandfare;
		public String paidby;
		public String owner;

		public AuthenticateConnectionSaveCalculatedFare() {

		}

		public void connection() throws Exception {

			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/saveCalculatedFare.php";
			HttpPost httpPost = new HttpPost(url_select);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();

			BasicNameValuePair CabIdValuePair = new BasicNameValuePair("cabId",
					cabid);
			BasicNameValuePair MemberNumberValuePair = new BasicNameValuePair(
					"totalFare", totalfare);
			BasicNameValuePair NumberFairValuePair = new BasicNameValuePair(
					"numberAndFare", numberandfare);
			BasicNameValuePair PaidByValuePair = new BasicNameValuePair(
					"paidBy", paidby);
			BasicNameValuePair OwnerValuePair = new BasicNameValuePair("owner",
					owner);

			nameValuePairList.add(CabIdValuePair);
			nameValuePairList.add(MemberNumberValuePair);
			nameValuePairList.add(NumberFairValuePair);
			nameValuePairList.add(PaidByValuePair);
			nameValuePairList.add(OwnerValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;

			String result = null;
			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				result = stringBuilder.append(bufferedStrChunk).toString();
			}

			// saveCalculatedFare = result;

			Log.d("saveCalculatedFare", "saveCalculatedFare : " + result);
		}
	}

	// ///////////////////////
	// ///////

	private class ConnectionTaskForShowMembersFareCalculation extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionShowMembersFareCalculation mAuth1 = new AuthenticateConnectionShowMembersFareCalculation();
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
				Toast.makeText(MemberRideFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (showmembersresp.equalsIgnoreCase("No Members joined yet")) {

			} else {

				// mycalculatorbtn.setVisibility(View.VISIBLE);

				ShowMemberName.clear();
				ShowMemberNumber.clear();
				ShowMemberLocationAddress.clear();
				ShowMemberLocationLatLong.clear();
				ShowMemberImageName.clear();
				ShowMemberStatus.clear();

				try {
					JSONArray subArray = new JSONArray(showmembersresp);
					for (int i = 0; i < subArray.length(); i++) {
						try {
							ShowMemberName.add(subArray.getJSONObject(i)
									.getString("MemberName").toString());
							ShowMemberNumber.add(subArray.getJSONObject(i)
									.getString("MemberNumber").toString());
							ShowMemberLocationAddress.add(subArray
									.getJSONObject(i)
									.getString("MemberLocationAddress")
									.toString());
							ShowMemberLocationLatLong.add(subArray
									.getJSONObject(i)
									.getString("MemberLocationlatlong")
									.toString());
							ShowMemberImageName.add(subArray.getJSONObject(i)
									.getString("MemberImageName").toString());
							ShowMemberStatus.add(subArray.getJSONObject(i)
									.getString("Status").toString());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					showFareSplitDialog();

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			if (onedialog.isShowing()) {
				onedialog.dismiss();
			}

		}
	}

	public class AuthenticateConnectionShowMembersFareCalculation {

		public AuthenticateConnectionShowMembersFareCalculation() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/ShowMemberOnMap.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair CabIdBasicNameValuePair = new BasicNameValuePair(
					"CabId", CabId);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(CabIdBasicNameValuePair);

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
				showmembersresp = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("showmembersresp FareCalculation",
					"" + stringBuilder.toString());
		}
	}

	// /////////////////////
	// ///////

	private void cancelMegaCab() {

		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					MemberRideFragmentActivity.this);
			builder.setTitle("Internet Connection Error");
			builder.setMessage("ClubMyCab requires Internet connection");
			builder.setPositiveButton("OK", null);
			AlertDialog dialog = builder.show();
			TextView messageText = (TextView) dialog
					.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
			dialog.show();

			return;
		}

		CancelMegaCabAsync cancelMegaCabAsync = new CancelMegaCabAsync();
		String param = "type=CancelBooking" + "&mobile=" + MemberNumberstr
				+ "&bookingNo=" + BookingRefNo;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			cancelMegaCabAsync.executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, param);
		} else {
			cancelMegaCabAsync.execute(param);
		}
	}

	public class CancelMegaCabAsync extends AsyncTask<String, Void, String> {

		String result;

		private ProgressDialog dialog = new ProgressDialog(
				MemberRideFragmentActivity.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			Log.d("CancelMegaCabAsync",
					"CancelMegaCabAsync : " + args[0].toString());

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
					Log.d("CancelMegaCabAsync",
							"responseCode != HttpsURLConnection.HTTP_OK : "
									+ responseCode);
					result = response;
				}

				Log.d("CancelMegaCabAsync", "CancelMegaCabAsync response : "
						+ response);
				result = response;
			} catch (Exception e) {
				e.printStackTrace();
				result = "";
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(MemberRideFragmentActivity.this,
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

						// JSONObject jsonObjectData = new JSONObject(jsonObject
						// .get("data").toString());

						runOnUiThread(new Runnable() {

							@Override
							public void run() {

								AlertDialog.Builder builder = new AlertDialog.Builder(
										MemberRideFragmentActivity.this);
								builder.setMessage("Booking cancelled!");
								builder.setPositiveButton("OK",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
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
										MemberRideFragmentActivity.this);
								builder.setTitle("Cab could not be cancelled");
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
						Toast.makeText(MemberRideFragmentActivity.this,
								"Something went wrong, please try again",
								Toast.LENGTH_LONG).show();
					}
				});
			}

			return result;
		}

		@Override
		protected void onPostExecute(String result) {

			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}
	}

	private void cancelUberCab() {

		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					MemberRideFragmentActivity.this);
			builder.setTitle("Internet Connection Error");
			builder.setMessage("ClubMyCab requires Internet connection");
			builder.setPositiveButton("OK", null);
			AlertDialog dialog = builder.show();
			TextView messageText = (TextView) dialog
					.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
			dialog.show();

			return;
		}

		CancelMegaCabAsync cancelMegaCabAsync = new CancelMegaCabAsync();
		String param = "type=CancelBooking" + "&mobile=" + MemberNumberstr
				+ "&bookingNo=" + BookingRefNo;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			cancelMegaCabAsync.executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, param);
		} else {
			cancelMegaCabAsync.execute(param);
		}
	}

	public class CancelUberCabAsync extends AsyncTask<String, Void, String> {

		String result;

		private ProgressDialog dialog = new ProgressDialog(
				MemberRideFragmentActivity.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			Log.d("CancelMegaCabAsync",
					"CancelMegaCabAsync : " + args[0].toString());

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
					Log.d("CancelMegaCabAsync",
							"responseCode != HttpsURLConnection.HTTP_OK : "
									+ responseCode);
					result = response;
				}

				Log.d("CancelMegaCabAsync", "CancelMegaCabAsync response : "
						+ response);
				result = response;
			} catch (Exception e) {
				e.printStackTrace();
				result = "";
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(MemberRideFragmentActivity.this,
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

						// JSONObject jsonObjectData = new JSONObject(jsonObject
						// .get("data").toString());

						runOnUiThread(new Runnable() {

							@Override
							public void run() {

								AlertDialog.Builder builder = new AlertDialog.Builder(
										MemberRideFragmentActivity.this);
								builder.setMessage("Booking cancelled!");
								builder.setPositiveButton("OK",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
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
										MemberRideFragmentActivity.this);
								builder.setTitle("Cab could not be cancelled");
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
						Toast.makeText(MemberRideFragmentActivity.this,
								"Something went wrong, please try again",
								Toast.LENGTH_LONG).show();
					}
				});
			}

			return result;
		}

		@Override
		protected void onPostExecute(String result) {

			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}
	}

	private void cancelTFSCab() {

		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					MemberRideFragmentActivity.this);
			builder.setTitle("Internet Connection Error");
			builder.setMessage("ClubMyCab requires Internet connection");
			builder.setPositiveButton("OK", null);
			AlertDialog dialog = builder.show();
			TextView messageText = (TextView) dialog
					.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
			dialog.show();

			return;
		}

		CancelTFSCabAsync cancelTFSCabAsync = new CancelTFSCabAsync();
		String param = "type=cancellation" + "&booking_id=" + BookingRefNo
				+ "&cancellation_reason=";

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			cancelTFSCabAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
					param);
		} else {
			cancelTFSCabAsync.execute(param);
		}
	}

	public class CancelTFSCabAsync extends AsyncTask<String, Void, String> {

		String result;

		private ProgressDialog dialog = new ProgressDialog(
				MemberRideFragmentActivity.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			Log.d("CancelTFSCabAsync",
					"CancelTFSCabAsync : " + args[0].toString());

			try {
				URL url = new URL(GlobalVariables.ServiceUrl + "/tfs.php");
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
					Log.d("CancelTFSCabAsync",
							"responseCode != HttpsURLConnection.HTTP_OK : "
									+ responseCode);
					result = response;
				}

				Log.d("CancelTFSCabAsync", "CancelTFSCabAsync response : "
						+ response);
				result = response;
			} catch (Exception e) {
				e.printStackTrace();
				result = "";
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(MemberRideFragmentActivity.this,
								"Something went wrong, please try again",
								Toast.LENGTH_LONG).show();
					}
				});
			}

			if (!result.isEmpty()) {

				try {
					JSONObject jsonObject = new JSONObject(result);
					String status = jsonObject.get("status").toString();
					if (status.equalsIgnoreCase("success")) {

						// JSONObject jsonObjectData = new JSONObject(jsonObject
						// .get("data").toString());

						runOnUiThread(new Runnable() {

							@Override
							public void run() {

								AlertDialog.Builder builder = new AlertDialog.Builder(
										MemberRideFragmentActivity.this);
								builder.setMessage("Booking cancelled!");
								builder.setPositiveButton("OK",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
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
						final String reason = jsonObject.get("error_desc")
								.toString();

						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								AlertDialog.Builder builder = new AlertDialog.Builder(
										MemberRideFragmentActivity.this);
								builder.setTitle("Cab could not be cancelled");
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
						Toast.makeText(MemberRideFragmentActivity.this,
								"Something went wrong, please try again",
								Toast.LENGTH_LONG).show();
					}
				});
			}

			return result;
		}

		@Override
		protected void onPostExecute(String result) {

			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}
	}
}
