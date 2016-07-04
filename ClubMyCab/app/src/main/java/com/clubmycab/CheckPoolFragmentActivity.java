package com.clubmycab;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.clubmycab.FareCalculator.FareCalculatorInterface;
import com.clubmycab.asynctasks.GlobalAsyncTask;
import com.clubmycab.asynctasks.GlobalAsyncTask.AsyncTaskResultListener;
import com.clubmycab.maps.MapUtilityMethods;
import com.clubmycab.model.AddressModel;
import com.clubmycab.model.ContactData;
import com.clubmycab.model.RideDetailsModel;
import com.clubmycab.ui.ContactsToInviteActivity;
import com.clubmycab.ui.FirstLoginWalletsActivity;
import com.clubmycab.ui.MobileSiteActivity;
import com.clubmycab.ui.MobileSiteFragment;
import com.clubmycab.ui.NewHomeScreen;
import com.clubmycab.ui.SendInvitesToOtherScreen;
import com.clubmycab.utility.AppConstants;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.L;
import com.clubmycab.utility.Log;
import com.clubmycab.utility.StringTags;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

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
import org.jivesoftware.smack.SASLAuthentication;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

public class CheckPoolFragmentActivity extends FragmentActivity implements
		FareCalculatorInterface, AsyncTaskResultListener {

	String CompletePageResponse;

	RideDetailsModel rideDetailsModel;
	ArrayList<Integer> durationList = new ArrayList<Integer>();
	ArrayList<Integer> distanceList = new ArrayList<Integer>();
	String comefrom;

	GoogleMap checkpoolmap;

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

	String FullName;
	String MemberNumberstr;

	String showmembersresp;
	String updatestatusresp;

	ArrayList<String> ShowMemberName = new ArrayList<String>();
	ArrayList<String> ShowMemberNumber = new ArrayList<String>();
	ArrayList<String> ShowMemberLocationAddress = new ArrayList<String>();
	ArrayList<String> ShowMemberLocationLatLong = new ArrayList<String>();
	ArrayList<String> ShowMemberImageName = new ArrayList<String>();
	ArrayList<String> ShowMemberStatus = new ArrayList<String>();
	ArrayList<String> ShowMemberLocationAddressEnd = new ArrayList<String>();
	ArrayList<String> ShowMemberLocationLatLongEnd = new ArrayList<String>();

	LinearLayout ownermessage;
	LinearLayout ownerinvite;
	LinearLayout ownerbookacab;
	LinearLayout ownercancel;

	String ownercancelpoolresp;
	CircularImageView memimage;

	String dropuserfrompopupresp;

	String sendcustommessagefrompopupresp;

	ImageView mydetailbtn;
	// ImageView mycalculatorbtn;
	ProgressDialog onedialog;

	RelativeLayout contexthelpcheckpool;

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

	LinearLayout checkpoolbottomtabsll;
	boolean exceptioncheck = false;

	// /////////////////
	Button contactsbtn;
	Button appFrends;
	Button myclubbtn;

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

	String saveCalculatedFare;
	String amountToPay;
	String payToPerson;
	String totalFare;

	String totalCredits, storeclubres;

	private ArrayList<Double> FaredistanceList = new ArrayList<Double>();
	private ArrayList<LatLng> FareLocationList = new ArrayList<LatLng>();
	private ArrayList<String> FareMobNoList = new ArrayList<String>();
	private ArrayList<LatLng> FareMemberPickLocaton = new ArrayList<LatLng>();
	private ArrayList<LatLng> FareMemberDropLocaton = new ArrayList<LatLng>();
	private String ownerinviteres, referfriendres, sendres, distancetext;
	private String fromcome, CabId, OwnerName, FromLocation, ToLocation,
			TravelDate, TravelTime, Seats, fromshortname, toshortname;
	private static final int INVITE_FRIEND_REQUEST = 500;
	private static final int CHECK_POOL_FRAGMENT_ID = 501;

	// /////////////////

	@SuppressLint("DefaultLocale")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check_pool);

		// Check if Internet present
		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					CheckPoolFragmentActivity.this);
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

		GoogleAnalytics analytics = GoogleAnalytics.getInstance(CheckPoolFragmentActivity.this);
		tracker = analytics.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);
		// All subsequent hits will be send with screen name = "main screen"
		tracker.setScreenName("Owner Created Pool");

		Intent intent = getIntent();
		Gson gson = new Gson();
		rideDetailsModel = gson.fromJson(intent.getStringExtra("RideDetailsModel"), RideDetailsModel.class);
		checkpoolmap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.checkpoolmap)).getMap();

		// CabId = intent.getStringExtra("CabId");
		// MobileNumber = intent.getStringExtra("MobileNumber");
		// OwnerName = intent.getStringExtra("OwnerName");
		// OwnerImage = intent.getStringExtra("OwnerImage");
		// FromLocation = intent.getStringExtra("FromLocation");
		// ToLocation = intent.getStringExtra("ToLocation");
		//
		// FromShortName = intent.getStringExtra("FromShortName");
		// ToShortName = intent.getStringExtra("ToShortName");
		//
		// TravelDate = intent.getStringExtra("TravelDate");
		// TravelTime = intent.getStringExtra("TravelTime");
		// Seats = intent.getStringExtra("Seats");
		// RemainingSeats = intent.getStringExtra("RemainingSeats");
		// Seat_Status = intent.getStringExtra("Seat_Status");
		// Distance = intent.getStringExtra("Distance");
		// OpenTime = intent.getStringExtra("OpenTime");
		// CabStatus = intent.getStringExtra("CabStatus");
		//
		// BookingRefNo = intent.getStringExtra("BookingRefNo");
		// DriverName = intent.getStringExtra("DriverName");
		// DriverNumber = intent.getStringExtra("DriverNumber");
		// CarNumber = intent.getStringExtra("CarNumber");
		// CabName = intent.getStringExtra("CabName");
		//
		// ExpTripDuration = intent.getStringExtra("ExpTripDuration");
		// statusTrip = intent.getStringExtra("status");

		comefrom = intent.getStringExtra("comefrom");

		if (comefrom != null) {

			if (comefrom.equalsIgnoreCase("GCM")) {

				String nid = intent.getStringExtra("nid");
				String params = "rnum=" + "&nid=" + nid + "&auth="
						+ GlobalMethods.calculateCMCAuthString(nid);
				String endpoint = GlobalVariables.ServiceUrl
						+ "/UpdateNotificationStatusToRead.php";
				Log.d("CheckPoolFragmentActivity",
						"UpdateNotificationStatusToRead endpoint : " + endpoint
								+ " params : " + params);
				new GlobalAsyncTask(this, endpoint, params, null, this, false,
						"UpdateNotificationStatusToRead", false);

			}

		}

		Log.d("comefrom", "" + comefrom);

		Log.d("CabStatus", "" + rideDetailsModel.getCabStatus());

		Log.d("status", "" + rideDetailsModel.getStatus());

		checkpoolbottomtabsll = (LinearLayout) findViewById(R.id.checkpoolbottomtabsll);

		if (rideDetailsModel.getCabStatus().toString().trim().equalsIgnoreCase("A")) {
			checkpoolbottomtabsll.setVisibility(View.VISIBLE);
		} else {
			checkpoolbottomtabsll.setVisibility(View.GONE);
		}

		ownermessage = (LinearLayout) findViewById(R.id.ownermessage);
		ownerinvite = (LinearLayout) findViewById(R.id.ownerinvite);
		ownerbookacab = (LinearLayout) findViewById(R.id.ownerbookacab);
		ownercancel = (LinearLayout) findViewById(R.id.ownercancel);

		chatlayoutmainrl = (RelativeLayout) findViewById(R.id.chatlayoutmainrl);
		chatlayoutmainrl.setVisibility(View.GONE);

		mydetailbtn = (ImageView) findViewById(R.id.mydetailbtn);
		mydetailbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				joinPoolPopUp(rideDetailsModel.getFromShortName(),
						rideDetailsModel.getToShortName(),
						rideDetailsModel.getTravelDate(),
						rideDetailsModel.getTravelTime(),
						rideDetailsModel.getSeat_Status(),
						rideDetailsModel.getOwnerName(),
						rideDetailsModel.getImagename());

			}
		});

		// mycalculatorbtn = (ImageView) findViewById(R.id.mycalculatorbtn);
		// mycalculatorbtn.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// showFareSplitDialog();
		// }
		// });

		ownermessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (ShowMemberNumber.size() > 0) {

					if (!chatlayoutmainrl.isShown()) {

						tracker.send(new HitBuilders.EventBuilder()
								.setCategory("Message All")
								.setAction("Message All")
								.setLabel("Message All").build());

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
				} else {
					Toast.makeText(CheckPoolFragmentActivity.this,
							"No Members Have Joined Yet", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

		ownerinvite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (rideDetailsModel.getRemainingSeats().equalsIgnoreCase("0")) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							CheckPoolFragmentActivity.this);
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
							CheckPoolFragmentActivity.this,
							SendInvitesToOtherScreen.class);
					fromcome = "checkpool";
					CabId = rideDetailsModel.getCabId();
					OwnerName = rideDetailsModel.getOwnerName();
					OwnerMobileNumber = rideDetailsModel.getMobileNumber();
					FromLocation = rideDetailsModel.getFromLocation();
					ToLocation = rideDetailsModel.getToLocation();
					TravelDate = rideDetailsModel.getTravelDate();
					TravelTime = rideDetailsModel.getTravelTime();
					Seats = rideDetailsModel.getSeats();
					fromshortname = rideDetailsModel.getFromShortName();
					toshortname = rideDetailsModel.getToShortName();

					/*
					 * mainIntent.putExtra("fromcome", "checkpool");
					 * mainIntent.putExtra("CabId",
					 * rideDetailsModel.getCabId());
					 * mainIntent.putExtra("MobileNumber",
					 * rideDetailsModel.getMobileNumber());
					 * mainIntent.putExtra("OwnerName",
					 * rideDetailsModel.getOwnerName());
					 * mainIntent.putExtra("FromLocation",
					 * rideDetailsModel.getFromLocation());
					 * mainIntent.putExtra("ToLocation",
					 * rideDetailsModel.getToLocation());
					 * mainIntent.putExtra("TravelDate",
					 * rideDetailsModel.getTravelDate());
					 * mainIntent.putExtra("TravelTime",
					 * rideDetailsModel.getTravelTime());
					 * mainIntent.putExtra("Seats",
					 * rideDetailsModel.getSeats());
					 * mainIntent.putExtra("fromshortname",
					 * rideDetailsModel.getFromShortName());
					 * mainIntent.putExtra("toshortname",
					 * rideDetailsModel.getToShortName());
					 */
					mainIntent.putExtra("activity_id",
							SendInvitesToOtherScreen.CHECK_POOL_FRAGMENT_ID);
					startActivityForResult(mainIntent, CHECK_POOL_FRAGMENT_ID);
					overridePendingTransition(R.anim.slide_in_right,
							R.anim.slide_out_left);
				}
			}
		});

		ownerbookacab.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (rideDetailsModel.getBookingRefNo() != null
						&& !rideDetailsModel.getBookingRefNo().isEmpty()
						&& !rideDetailsModel.getBookingRefNo()
								.equalsIgnoreCase("null")) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							CheckPoolFragmentActivity.this);
					builder.setMessage("A cab has already been booked for the ride");
					builder.setCancelable(false);
					builder.setNegativeButton("OK", null);
					AlertDialog dialog = builder.show();
					TextView messageText = (TextView) dialog
							.findViewById(android.R.id.message);
					messageText.setGravity(Gravity.CENTER);
					dialog.show();
				} else if (rideDetailsModel.getRideType().equals("1")) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							CheckPoolFragmentActivity.this);
					builder.setMessage("This is a car pool ride, you can book a cab from home page");
					builder.setCancelable(false);
					builder.setNegativeButton("OK", null);
					AlertDialog dialog = builder.show();
					TextView messageText = (TextView) dialog
							.findViewById(android.R.id.message);
					messageText.setGravity(Gravity.CENTER);
					dialog.show();
				} else {

					openBookCabPage();
				}
			}
		});

		ownercancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cancelTrip();
			}
		});

		SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
		FullName = mPrefs.getString("FullName", "");
		MemberNumberstr = mPrefs.getString("MobileNumber", "");

		OwnerMobileNumber = rideDetailsModel.getMobileNumber();

		db = new DatabaseHandler(this);

		// /////////// chat code

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
					Toast.makeText(CheckPoolFragmentActivity.this,
							"Type some message to send!", Toast.LENGTH_SHORT)
							.show();
				}

			}
		});

		List<ChatObject> contacts = db.getAllCabIdChats(rideDetailsModel.getCabId());
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

		// Getting Google Play availability status
		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getBaseContext());

		// Showing status
		if (status != ConnectionResult.SUCCESS) { // Google Play Services are
													// not available

			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
			dialog.show();

		} else {

			onedialog = new ProgressDialog(CheckPoolFragmentActivity.this);
			onedialog.setMessage("Please Wait...");
			onedialog.setCancelable(false);
			onedialog.setCanceledOnTouchOutside(false);
			onedialog.show();

			/*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForDirections()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new ConnectionTaskForDirections().execute();
			}*/
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForShowMembersOnMap()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new ConnectionTaskForShowMembersOnMap().execute();
			}

		}

		// /// For contacts list
		Cursor cursor = null;
		try {
			cursor = CheckPoolFragmentActivity.this.getContentResolver().query(
					Phone.CONTENT_URI, null, null, null, null);
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

		contexthelpcheckpool = (RelativeLayout) findViewById(R.id.contexthelpcheckpool);

		SharedPreferences mPrefs1 = getSharedPreferences("ContextHelp", 0);
		String whichtimeforcheckpool = mPrefs1.getString(
				"whichtimeforcheckpool", "");

		if (whichtimeforcheckpool.isEmpty() || whichtimeforcheckpool == null
				|| whichtimeforcheckpool.equalsIgnoreCase("")) {
			contexthelpcheckpool.setVisibility(View.VISIBLE);
		}

		contexthelpcheckpool.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				contexthelpcheckpool.setVisibility(View.GONE);

				SharedPreferences sharedPreferences = getSharedPreferences(
						"ContextHelp", 0);
				Editor editor = sharedPreferences.edit();
				editor.putString("whichtimeforcheckpool", "second");
				editor.commit();
			}
		});

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

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new ConnectionTaskForGetMyFare().executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR,
					rideDetailsModel.getCabId(), OwnerMobileNumber, "");
		} else {
			new ConnectionTaskForGetMyFare().execute(
					rideDetailsModel.getCabId(), OwnerMobileNumber, "");
		}
	}

	private void showRideCompleteDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(
				CheckPoolFragmentActivity.this);
		View builderView = (View) getLayoutInflater().inflate(
				R.layout.dialog_fare_ride_complete, null);

		builder.setView(builderView);
		final AlertDialog dialog = builder.create();

		LinearLayout linearLayout = (LinearLayout) builderView
				.findViewById(R.id.ridecompletesettledll);
		linearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				tracker.send(new HitBuilders.EventBuilder()
						.setCategory("Fare already settled")
						.setAction("Fare already settled")
						.setLabel("Fare already settled").build());

				dialog.dismiss();

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new ConnectionTaskForMarkTripCompleted().executeOnExecutor(
							AsyncTask.THREAD_POOL_EXECUTOR,
							rideDetailsModel.getCabId());
				} else {
					new ConnectionTaskForMarkTripCompleted()
							.execute(rideDetailsModel.getCabId());
				}
			}
		});

		linearLayout = (LinearLayout) builderView
				.findViewById(R.id.ridecompletepaidelsell);
		linearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				tracker.send(new HitBuilders.EventBuilder()
						.setCategory("Fare paid by other")
						.setAction("Fare paid by other")
						.setLabel("Fare paid by other").build());

				dialog.dismiss();

				Toast.makeText(
						CheckPoolFragmentActivity.this,
						"We will let you know when your friend shares the fare details & the amount you owe",
						Toast.LENGTH_LONG).show();

				Intent mainIntent = new Intent(CheckPoolFragmentActivity.this,
						NewHomeScreen.class);
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
		linearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				tracker.send(new HitBuilders.EventBuilder()
						.setCategory("Fare calculate")
						.setAction("Fare calculate").setLabel("Fare calculate")
						.build());

				dialog.dismiss();

				showFareSplitDialog();
			}
		});

		dialog.show();

	}

	// private void showWhoPaidDialog() {
	// AlertDialog.Builder builder = new AlertDialog.Builder(
	// CheckPoolFragmentActivity.this);
	// View builderView = (View) getLayoutInflater().inflate(
	// R.layout.fare_split_dialog, null);
	//
	// TextView textView = (TextView) builderView
	// .findViewById(R.id.textViewFareSplit);
	// textView.setText("Please let us know who paid for the trip by selecting the member below:");
	//
	// ListView listView = (ListView) builderView
	// .findViewById(R.id.listViewFareSplit);
	//
	// Button button = (Button) builderView.findViewById(R.id.buttonFareSplit);
	// button.setVisibility(View.GONE);
	//
	// builder.setView(builderView);
	// final AlertDialog dialog = builder.create();
	//
	// listView.setAdapter(new ListViewAdapterFareSplit(
	// CheckPoolFragmentActivity.this, MemberName));
	// listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	//
	// @Override
	// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
	// long arg3) {
	//
	// }
	//
	// });
	//
	// dialog.show();
	// }

	private void showFareSplitDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(
				CheckPoolFragmentActivity.this);
		builder.setTitle("Fare Split");
		builder.setMessage("Please enter fare to split :");
		builder.setCancelable(false);
		final EditText input = new EditText(CheckPoolFragmentActivity.this);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		input.setLayoutParams(lp);
		input.setInputType(InputType.TYPE_CLASS_NUMBER
				| InputType.TYPE_NUMBER_FLAG_DECIMAL);
		builder.setView(input);
		builder.setPositiveButton("Split by distance",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						Double fare = 0.0;
						if (input.getText().toString().isEmpty()) {
							Toast.makeText(CheckPoolFragmentActivity.this,
									"Please enter a valid fare",
									Toast.LENGTH_LONG).show();
							showFareSplitDialog();
						} else if (!input.getText().toString().isEmpty()) {
							fare = Double.parseDouble(input.getText()
									.toString());
							if (fare <= 0.0) {
								Toast.makeText(CheckPoolFragmentActivity.this,
										"Please enter a valid fare",
										Toast.LENGTH_LONG).show();
								showFareSplitDialog();
							} else {

								tracker.send(new HitBuilders.EventBuilder()
										.setCategory("Fare Split by distance")
										.setAction("Fare Split by distance")
										.setLabel("Fare Split by distance")
										.build());

								onedialog = new ProgressDialog(
										CheckPoolFragmentActivity.this);
								onedialog.setMessage("Please Wait...");
								onedialog.setCancelable(false);
								onedialog.setCanceledOnTouchOutside(false);
								onedialog.show();

								InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
								im.hideSoftInputFromWindow(
										input.getWindowToken(), 0);

								FareCalculatorNew fareCalculatorNew = new FareCalculatorNew(
										FareMobNoList, FareMemberPickLocaton,
										FareMemberDropLocaton,
										FareLocationList, FaredistanceList,
										fare);

								HashMap<String, Double> hashMap = fareCalculatorNew
										.getFareSplit();

								Log.d("CheckPoolFragmentActivity",
										"fareCalculatorNew : " + hashMap);

								sendFareSplitToMembers(hashMap);

								// if (showmembersresp
								// .equalsIgnoreCase("No Members joined yet")) {
								//
								// } else {
								//
								// try {
								//
								// JSONObject ownerJsonObject = new
								// JSONObject();
								// ownerJsonObject
								// .put(FareCalculator.JSON_NAME_OWNER_START_ADDRESS,
								// rideDetailsModel
								// .getFromLocation());
								// ownerJsonObject
								// .put(FareCalculator.JSON_NAME_OWNER_END_ADDRESS,
								// rideDetailsModel
								// .getToLocation());
								// ownerJsonObject
								// .put(FareCalculator.JSON_NAME_OWNER_NAME,
								// rideDetailsModel
								// .getOwnerName());
								//
								// ArrayList<JSONObject> memberArrayList = new
								// ArrayList<JSONObject>();
								//
								// for (int i = 0; i < ShowMemberName
								// .size(); i++) {
								// JSONObject memberJsonObject = new
								// JSONObject();
								// memberJsonObject
								// .put
								//
								// (FareCalculator.JSON_NAME_MEMBER_LOCATION_ADDRESS,
								// ShowMemberLocationAddress
								// .get(i)
								// .toString());
								//
								// String[] latlong = ShowMemberLocationLatLong
								// .get(i).split(",");
								// LatLng lt = new LatLng(
								// Double.parseDouble(latlong[0]),
								// Double.parseDouble(latlong[1]));
								// memberJsonObject
								// .put
								//
								// (FareCalculator.JSON_NAME_MEMBER_LOCATION_LATLNG,
								// lt);
								//
								// memberJsonObject
								// .put(FareCalculator.JSON_NAME_MEMBER_NAME,
								// ShowMemberName.get(
								// i)
								// .toString());
								//
								// memberArrayList
								// .add(memberJsonObject);
								// }
								//
								// FareCalculator fareCalculator = new
								// FareCalculator(
								// CheckPoolFragmentActivity.this,
								// ownerJsonObject,
								// memberArrayList);
								// fareCalculator.calculateFareSplit(fare);
								//
								// } catch (Exception e) {
								// e.printStackTrace();
								// }
								//
								// }
							}
						}
					}
				});
		builder.setNegativeButton("Split equally",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Double fare = 0.0;
						if (input.getText().toString().isEmpty()) {
							Toast.makeText(CheckPoolFragmentActivity.this,
									"Please enter a valid fare",
									Toast.LENGTH_LONG).show();
							showFareSplitDialog();
						} else if (!input.getText().toString().isEmpty()) {
							fare = Double.parseDouble(input.getText()
									.toString());
							if (fare <= 0.0) {
								Toast.makeText(CheckPoolFragmentActivity.this,
										"Please enter a valid fare",
										Toast.LENGTH_LONG).show();
								showFareSplitDialog();
							} else {
								tracker.send(new HitBuilders.EventBuilder()
										.setCategory("Fare Split equal")
										.setAction("Fare Split equal")
										.setLabel("Fare Split equal").build());

								showEqualFareSplitDialog(fare);
							}
						}
					}
				});
		AlertDialog dialog = builder.show();
		TextView messageText = (TextView) dialog
				.findViewById(android.R.id.message);
		messageText.setGravity(Gravity.CENTER);
		dialog.show();

	}

	private void sendFareSplitToMembers(final HashMap<String, Double> hashMap) {
		if (onedialog.isShowing()) {
			onedialog.dismiss();
		}

		if (hashMap == null) {
			Toast.makeText(CheckPoolFragmentActivity.this,
					getResources().getString(R.string.exceptionstring),
					Toast.LENGTH_LONG).show();
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					CheckPoolFragmentActivity.this);
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

					String displayName = "";
					if (key.equals(OwnerMobileNumber)) {
						displayName = rideDetailsModel.getOwnerName();
					} else {
						int index = ShowMemberNumber.indexOf(key);
						if (index != -1) {
							displayName = ShowMemberName.get(index);
						}
					}

					arrayList.add(displayName
							+ " : \u20B9 "
							+ String.format(
									"%d%n",
									Math.round(Double.parseDouble(hashMap.get(
											key).toString()))));
				}
			}
			arrayList.add(
					0,
					"Total Fare : \u20B9 "
							+ Math.round(Double.parseDouble(hashMap.get(
									"tripTotalFare").toString())));

			listView.setAdapter(new ListViewAdapterFareSplit(
					CheckPoolFragmentActivity.this, arrayList));

			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					dialog.dismiss();

					String numberfareString = "";
					for (String key : hashMap.keySet()) {
						if (!key.equalsIgnoreCase("tripTotalFare")) {
							numberfareString += (key
									+ "~"
									+ String.format("%d%n", Math.round(Double
											.parseDouble(hashMap.get(key)
													.toString()))) + ",");
						}
					}

					numberfareString = numberfareString.substring(0,
							numberfareString.length() - 1);

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new ConnectionTaskForSaveCalculatedFare()
								.executeOnExecutor(
										AsyncTask.THREAD_POOL_EXECUTOR,
										rideDetailsModel.getCabId(), hashMap
												.get("tripTotalFare")
												.toString(), numberfareString,
										OwnerMobileNumber, OwnerMobileNumber);
					} else {
						new ConnectionTaskForSaveCalculatedFare().execute(
								rideDetailsModel.getCabId(),
								hashMap.get("tripTotalFare").toString(),
								numberfareString, OwnerMobileNumber,
								OwnerMobileNumber);
					}
				}
			});

			dialog.show();
		}
	}

	private void showEqualFareSplitDialog(final double fare) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				CheckPoolFragmentActivity.this);
		View builderView = (View) getLayoutInflater().inflate(
				R.layout.fare_split_dialog, null);

		ListView listView = (ListView) builderView
				.findViewById(R.id.listViewFareSplit);

		Button button = (Button) builderView.findViewById(R.id.buttonFareSplit);

		builder.setView(builderView);
		final AlertDialog dialog = builder.create();

		ArrayList<String> arrayList = new ArrayList<String>();
		final double fareSplit = fare / (ShowMemberName.size() + 1); // +1 for
																		// owner

		arrayList.add(rideDetailsModel.getOwnerName() + " : \u20B9 "
				+ String.format("%d%n", Math.round(fareSplit)));
		for (int i = 0; i < ShowMemberName.size(); i++) {
			arrayList.add(ShowMemberName.get(i) + " : \u20B9 "
					+ String.format("%d%n", Math.round(fareSplit)));
		}

		arrayList.add(0, "Total Fare : \u20B9 " + Double.toString(fare));

		listView.setAdapter(new ListViewAdapterFareSplit(
				CheckPoolFragmentActivity.this, arrayList));

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				dialog.dismiss();

				String numberfareString = "";
				for (int i = 0; i < ShowMemberNumber.size(); i++) {
					numberfareString += (ShowMemberNumber.get(i).toString()
							+ "~"
							+ String.format("%d%n", Math.round(fareSplit)) + ",");
				}
				numberfareString += (OwnerMobileNumber + "~"
						+ String.format("%d%n", Math.round(fareSplit)) + ",");

				numberfareString = numberfareString.substring(0,
						numberfareString.length() - 1);

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new ConnectionTaskForSaveCalculatedFare()
							.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
									rideDetailsModel.getCabId(),
									Double.toString(fare), numberfareString,
									OwnerMobileNumber, OwnerMobileNumber);
				} else {
					new ConnectionTaskForSaveCalculatedFare().execute(
							rideDetailsModel.getCabId(), Double.toString(fare),
							numberfareString, OwnerMobileNumber,
							OwnerMobileNumber);
				}
			}
		});

		dialog.show();
	}

	private void showPaymentDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(
				CheckPoolFragmentActivity.this);
		View builderView = (View) getLayoutInflater().inflate(
				R.layout.dialog_fare_ride_complete_payment, null);

		builder.setView(builderView);
		final AlertDialog dialog = builder.create();

		LinearLayout linearLayout = (LinearLayout) builderView
				.findViewById(R.id.ridecompletefaresettledll);
		linearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				tracker.send(new HitBuilders.EventBuilder()
						.setCategory("Fare settled in cash")
						.setAction("Fare settled in cash")
						.setLabel("Fare settled in cash").build());

				dialog.dismiss();

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new ConnectionTaskForMarkTripCompleted().executeOnExecutor(
							AsyncTask.THREAD_POOL_EXECUTOR,
							rideDetailsModel.getCabId(), OwnerMobileNumber,
							OwnerMobileNumber);
				} else {
					new ConnectionTaskForMarkTripCompleted().execute(
							rideDetailsModel.getCabId(), OwnerMobileNumber,
							OwnerMobileNumber);
				}
			}
		});

		linearLayout = (LinearLayout) builderView
				.findViewById(R.id.ridecompletefarewalletll);
		linearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				tracker.send(new HitBuilders.EventBuilder()
						.setCategory("Fare settled by wallet")
						.setAction("Fare settled by wallet")
						.setLabel("Fare settled by wallet").build());

				dialog.dismiss();

				SharedPreferences sharedPreferences = getSharedPreferences(
						"MobikwikToken", 0);
				String token = sharedPreferences.getString("token", "");

				if (token != null && !token.isEmpty()) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new ConnectionTaskForGetMyFare().executeOnExecutor(
								AsyncTask.THREAD_POOL_EXECUTOR,
								rideDetailsModel.getCabId(), OwnerMobileNumber,
								"isWalletToWallet");
					} else {
						new ConnectionTaskForGetMyFare().execute(
								rideDetailsModel.getCabId(), OwnerMobileNumber,
								"isWalletToWallet");
					}
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							CheckPoolFragmentActivity.this);
					builder.setMessage("You cannot make a transfer as you do not have a wallet integrated yet, would you like to add a wallet now?");
					builder.setCancelable(false);

					builder.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent mainIntent = new Intent(
											CheckPoolFragmentActivity.this,
											FirstLoginWalletsActivity.class);
									mainIntent.putExtra("from", "wallet");
									startActivity(mainIntent);
								}
							});

					builder.setNegativeButton("NO",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							});

					builder.show();
				}
			}
		});

		linearLayout = (LinearLayout) builderView
				.findViewById(R.id.ridecompletefareclubcreditsll);
		linearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				tracker.send(new HitBuilders.EventBuilder()
						.setCategory("Fare settled in Credits")
						.setAction("Fare settled in Credits")
						.setLabel("Fare settled in Credits").build());

				dialog.dismiss();

				userData(OwnerMobileNumber);
			}
		});

		dialog.show();

	}

	private void userData(String mobileNumber) {

		String endpoint = GlobalVariables.ServiceUrl + "/userData.php";
		String authString = mobileNumber;
		String params = "mobileNumber=" + mobileNumber + "&auth="
				+ GlobalMethods.calculateCMCAuthString(authString);
		Log.d("CheckPoolFragmentActivity", "userData endpoint : " + endpoint
				+ " params : " + params);
		new GlobalAsyncTask(CheckPoolFragmentActivity.this, endpoint, params,
				null, CheckPoolFragmentActivity.this, true, "userData", false);
	}

	private void payUsingCredits(String mobileNumber, String sender,
			String amount) {

		String endpoint = GlobalVariables.ServiceUrl + "/payUsingCredits.php";
		String authString = amount + rideDetailsModel.getCabId() + mobileNumber
				+ OwnerMobileNumber + sender;
		String params = "mobileNumber=" + mobileNumber + "&sender=" + sender
				+ "&amount=" + amount + "&owner=" + OwnerMobileNumber
				+ "&cabId=" + rideDetailsModel.getCabId() + "&auth="
				+ GlobalMethods.calculateCMCAuthString(authString);
		Log.d("CheckPoolFragmentActivity", "payUsingCredits endpoint : "
				+ endpoint + " params : " + params);
		new GlobalAsyncTask(CheckPoolFragmentActivity.this, endpoint, params,
				null, CheckPoolFragmentActivity.this, true, "payUsingCredits",
				false);
	}

	private void getMyFare(String cabID, String mobileNumber) {

		String endpoint = GlobalVariables.ServiceUrl + "/getMyFare.php";
		String authString = cabID + mobileNumber;
		String params = "mobileNumber=" + mobileNumber + "&cabId=" + cabID
				+ "&auth=" + GlobalMethods.calculateCMCAuthString(authString);
		Log.d("CheckPoolFragmentActivity", "getMyFare endpoint : " + endpoint
				+ " params : " + params);
		new GlobalAsyncTask(CheckPoolFragmentActivity.this, endpoint, params,
				null, CheckPoolFragmentActivity.this, false, "getMyFare", false);
	}

	private void checkTransactionLimit(String mobilenumber, String amount) {

		String checksumstring = GlobalMethods.calculateCheckSumForService("'"
				+ amount + "''" + mobilenumber + "''"
				+ GlobalVariables.Mobikwik_MerchantName + "''"
				+ GlobalVariables.Mobikwik_Mid + "'",
				GlobalVariables.Mobikwik_14SecretKey);
		String endpoint = GlobalVariables.ServiceUrl
				+ "/checkTransactionLimit";
		String params = "cell=" + mobilenumber + "&amount=" + amount + "&mid="
				+ GlobalVariables.Mobikwik_Mid + "&merchantname="
				+ GlobalVariables.Mobikwik_MerchantName + "&checksum="
				+ checksumstring;
		Log.d("CheckPoolFragmentActivity", "checkTransactionLimit endpoint : "
				+ endpoint + " params : " + params);
		new GlobalAsyncTask(this, endpoint, params, null, this, true,
				"checkTransactionLimit", true);
	}

	private void logTransaction(String amount, String fee, String merchantname,
			String mid, String token, String sendercell, String receivercell,
			String cabID) {

		String endpoint = GlobalVariables.ServiceUrl + "/logTransaction.php";
		String authString = amount + cabID + fee + merchantname + mid
				+ receivercell + sendercell + token;
		String params = "amount=" + amount + "&fee=" + fee + "&merchantname="
				+ merchantname + "&mid=" + mid + "&token=" + token
				+ "&sendercell=" + sendercell + "&receivercell=" + receivercell
				+ "&cabId=" + cabID + "&auth="
				+ GlobalMethods.calculateCMCAuthString(authString);
		Log.d("CheckPoolFragmentActivity", "logTransaction endpoint : "
				+ endpoint + " params : " + params);
		new GlobalAsyncTask(this, endpoint, params, null, this, true,
				"logTransaction", false);
	}

	private void initiatePeerTransfer(String sendercell, String receivercell,
			String amount, String fee, String orderid, String token) {

		String checksumstring = GlobalMethods.calculateCheckSumForService("'"
				+ amount + "''" + fee + "''"
				+ GlobalVariables.Mobikwik_MerchantName + "''"
				+ GlobalVariables.Mobikwik_Mid + "''" + orderid + "''"
				+ receivercell + "''" + sendercell + "''" + token + "'",
				GlobalVariables.Mobikwik_14SecretKey);
		String endpoint = GlobalVariables.Mobikwik_ServerURL
				+ "/initiatePeerTransfer";
		String params = "sendercell=" + sendercell + "&receivercell="
				+ receivercell + "&amount=" + amount + "&fee=" + fee
				+ "&orderid=" + orderid + "&token=" + token + "&mid="
				+ GlobalVariables.Mobikwik_Mid + "&merchantname="
				+ GlobalVariables.Mobikwik_MerchantName + "&checksum="
				+ checksumstring;
		Log.d("CheckPoolFragmentActivity", "initiatePeerTransfer endpoint : "
				+ endpoint + " params : " + params);
		new GlobalAsyncTask(this, endpoint, params, null, this, true,
				"initiatePeerTransfer", true);
	}

	private void tokenRegenerate(String mobilenumber, String token,
			boolean attemptReTransfer) {
		String msgcode = "507";

		String checksumstring = GlobalMethods.calculateCheckSumForService("'"
				+ mobilenumber + "''" + GlobalVariables.Mobikwik_MerchantName
				+ "''" + GlobalVariables.Mobikwik_Mid + "''" + msgcode + "''"
				+ token + "''1'",
				GlobalVariables.Mobikwik_14SecretKey_TokenRegenerate);
		String endpoint = GlobalVariables.Mobikwik_ServerURL
				+ "/tokenregenerate";
		String params = "cell=" + mobilenumber + "&token=" + token
				+ "&tokentype=1" + "&msgcode=" + msgcode + "&mid="
				+ GlobalVariables.Mobikwik_Mid + "&merchantname="
				+ GlobalVariables.Mobikwik_MerchantName + "&checksum="
				+ checksumstring;
		Log.d("CheckPoolFragmentActivity", "tokenRegenerate endpoint : "
				+ endpoint + " params : " + params);
		if (attemptReTransfer) {
			new GlobalAsyncTask(this, endpoint, params, null, this, true,
					"tokenRegenerateReTransfer", true);
		} else {
			new GlobalAsyncTask(this, endpoint, params, null, this, true,
					"tokenregenerate", true);
		}
	}

	@Override
	public void getResult(String response, String uniqueID) {
		// if (uniqueID.equals("querywallet")) {
		// try {
		// JSONObject jsonObject = new JSONObject(response);
		// Log.d("CheckPoolFragmentActivity", "querywallet jsonObject : "
		// + jsonObject);
		// if (!checkResponseChecksum(response)) {
		// checksumInvalidToast();
		// return;
		// }
		//
		// if (jsonObject.getString("status").equals("SUCCESS")) {
		// checkTransactionLimit(OwnerMobileNumber.substring(4),
		// amountToPay);
		// } else {
		// Toast.makeText(
		// CheckPoolFragmentActivity.this,
		// "The transfer cannot be made as the person you wish to transfer to does not have an active wallet",
		// Toast.LENGTH_LONG).show();
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// } else
		if (uniqueID.equals("checkTransactionLimit")) {
			Log.d("CheckPoolFragmentActivity",
					"checkTransactionLimit response : " + response);
			if (!GlobalMethods.checkResponseChecksum(response)) {
				checksumInvalidToast();
				return;
			}

			try {
				JSONObject jsonObject = new JSONObject(response);
				if (jsonObject.getString("status").equals("SUCCESS")) {
					SharedPreferences sharedPreferences = getSharedPreferences(
							"MobikwikToken", 0);
					String token = sharedPreferences.getString("token", "");

					logTransaction(amountToPay, "0",
							GlobalVariables.Mobikwik_MerchantName,
							GlobalVariables.Mobikwik_Mid, token,
							OwnerMobileNumber.substring(4),
							payToPerson.substring(4),
							rideDetailsModel.getCabId());
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							CheckPoolFragmentActivity.this);
					builder.setMessage("You do not have sufficient balance in your wallet to make this transfer, would you like to top-up your wallet?");
					builder.setCancelable(false);

					builder.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									openAppOrMSite();
								}
							});

					builder.setNegativeButton("No",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							});

					builder.show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (uniqueID.equals("logTransaction")) {
			Log.d("CheckPoolFragmentActivity", "logTransaction response : "
					+ response);

			if (response != null && response.length() > 0
					&& response.contains("Unauthorized Access")) {
				Log.e("CheckPoolFragmentActivity",
						"logTransaction Unauthorized Access");
				Toast.makeText(CheckPoolFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			try {
				JSONObject jsonObject = new JSONObject(response);
				if (jsonObject.get("status").toString()
						.equalsIgnoreCase("success")) {
					SharedPreferences sharedPreferences = getSharedPreferences(
							"MobikwikToken", 0);
					String token = sharedPreferences.getString("token", "");

					initiatePeerTransfer(OwnerMobileNumber.substring(4),
							payToPerson.substring(4), amountToPay, "0",
							jsonObject.get("orderId").toString(), token);
				} else {
					Toast.makeText(CheckPoolFragmentActivity.this,
							"Something went wrong, please try again",
							Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(CheckPoolFragmentActivity.this,
						"Something went wrong, please try again",
						Toast.LENGTH_LONG).show();
			}
		} else if (uniqueID.equals("initiatePeerTransfer")) {
			Log.d("CheckPoolFragmentActivity",
					"initiatePeerTransfer response : " + response);

			SharedPreferences sharedPreferences = getSharedPreferences(
					"MobikwikToken", 0);
			String token = sharedPreferences.getString("token", "");

			try {
				JSONObject jsonObject = new JSONObject(response);
				if (jsonObject.get("status").toString()
						.equalsIgnoreCase("SUCCESS")) {
					Toast.makeText(CheckPoolFragmentActivity.this,
							jsonObject.get("statusdescription").toString(),
							Toast.LENGTH_LONG).show();

					tokenRegenerate(OwnerMobileNumber.substring(4), token,
							false);

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new ConnectionTaskForMarkTripCompleted()
								.executeOnExecutor(
										AsyncTask.THREAD_POOL_EXECUTOR,
										rideDetailsModel.getCabId(),
										OwnerMobileNumber, OwnerMobileNumber);
					} else {
						new ConnectionTaskForMarkTripCompleted().execute(
								rideDetailsModel.getCabId(), OwnerMobileNumber,
								OwnerMobileNumber);
					}
				} else {
					if (jsonObject.get("statusdescription").toString()
							.contains("Invalid Token")
							|| jsonObject.get("statusdescription").toString()
									.contains("Token Expired")) {
						tokenRegenerate(OwnerMobileNumber.substring(4), token,
								true);
					} else {
						Toast.makeText(CheckPoolFragmentActivity.this,
								jsonObject.get("statusdescription").toString(),
								Toast.LENGTH_LONG).show();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (uniqueID.equals("tokenregenerate")) {
			Log.d("CheckPoolFragmentActivity", "tokenregenerate response : "
					+ response);
			try {

				JSONObject jsonObject = new JSONObject(response);
				String token = jsonObject.get("token").toString();

				SharedPreferences sharedPreferences = getSharedPreferences(
						"MobikwikToken", 0);
				Editor editor = sharedPreferences.edit();
				editor.putString("token", token);
				editor.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (uniqueID.equals("tokenRegenerateReTransfer")) {
			Log.d("CheckPoolFragmentActivity",
					"tokenRegenerateReTransfer response : " + response);
			try {

				JSONObject jsonObject = new JSONObject(response);
				String token = jsonObject.get("token").toString();

				SharedPreferences sharedPreferences = getSharedPreferences(
						"MobikwikToken", 0);
				Editor editor = sharedPreferences.edit();
				editor.putString("token", token);
				editor.commit();

				logTransaction(amountToPay, "0",
						GlobalVariables.Mobikwik_MerchantName,
						GlobalVariables.Mobikwik_Mid, token,
						OwnerMobileNumber.substring(4),
						payToPerson.substring(4), rideDetailsModel.getCabId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (uniqueID.equals("userData")) {
			Log.d("CheckPoolFragmentActivity", "userData response : "
					+ response);

			if (response != null && response.length() > 0
					&& response.contains("Unauthorized Access")) {
				Log.e("CheckPoolFragmentActivity",
						"userData Unauthorized Access");
				Toast.makeText(CheckPoolFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			try {
				JSONObject jsonObject = new JSONObject(response);
				if (jsonObject.get("status").toString().equals("success")) {
					JSONObject jsonObject2 = new JSONObject(jsonObject.get(
							"data").toString());
					totalCredits = jsonObject2.get("totalCredits").toString();

					getMyFare(rideDetailsModel.getCabId(), OwnerMobileNumber);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (uniqueID.equals("getMyFare")) {
			Log.d("CheckPoolFragmentActivity", "getMyFare response : "
					+ response);

			if (response != null && response.length() > 0
					&& response.contains("Unauthorized Access")) {
				Log.e("CheckPoolFragmentActivity",
						"getMyFare Unauthorized Access");
				Toast.makeText(CheckPoolFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			try {

				JSONObject jsonObject = new JSONObject(response);
				amountToPay = jsonObject.get("fareToPay").toString();
				payToPerson = jsonObject.get("paidBy").toString();
				totalFare = jsonObject.get("totalFare").toString();

				if (Double.parseDouble(totalCredits) >= Double
						.parseDouble(totalFare)) {
					payUsingCredits(payToPerson, OwnerMobileNumber, amountToPay);
				} else {
					Toast.makeText(
							CheckPoolFragmentActivity.this,
							"You do not have sufficient reward Points to pay for your share!",
							Toast.LENGTH_LONG).show();
					showPaymentDialog();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (uniqueID.equals("payUsingCredits")) {
			Log.d("CheckPoolFragmentActivity", "payUsingCredits response : "
					+ response);

			if (response != null && response.length() > 0
					&& response.contains("Unauthorized Access")) {
				Log.e("CheckPoolFragmentActivity",
						"payUsingCredits Unauthorized Access");
				Toast.makeText(CheckPoolFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			try {
				JSONObject jsonObject = new JSONObject(response);
				Toast.makeText(CheckPoolFragmentActivity.this,
						jsonObject.get("message").toString(), Toast.LENGTH_LONG)
						.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void checksumInvalidToast() {
		Log.e("FirstLoginWalletsActivity", "Response checksum does not match!!");
		Toast.makeText(CheckPoolFragmentActivity.this,
				"Something went wrong, please try again", Toast.LENGTH_LONG)
				.show();
	}

	private void openAppOrMSite() {

		String packageName = "com.mobikwik_new";
		String mSite = "https://m.mobikwik.com";

		if (checkIfAppInstalled(packageName)) {

			Intent launchIntent = getPackageManager()
					.getLaunchIntentForPackage(packageName);
			startActivity(launchIntent);

		} else {

			Intent intent = new Intent(this, MobileSiteActivity.class);
			intent.putExtra(MobileSiteFragment.ARGUMENTS_MOBILE_SITE_URL, mSite);
			startActivity(intent);
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

	private class ConnectionTaskForGetMyFare extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionGetMyFare mAuth1 = new AuthenticateConnectionGetMyFare();
			try {
				mAuth1.cid = args[0];
				mAuth1.mnum = args[1];
				mAuth1.isWalletToWallet = args[2];

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
				Toast.makeText(CheckPoolFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}
		}

	}

	public class AuthenticateConnectionGetMyFare {

		public String cid, mnum, isWalletToWallet;

		public AuthenticateConnectionGetMyFare() {

		}

		public void connection() throws Exception {

			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl + "/getMyFare.php";
			HttpPost httpPost = new HttpPost(url_select);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();

			BasicNameValuePair CabIdValuePair = new BasicNameValuePair("cabId",
					cid);
			BasicNameValuePair MobileValuePair = new BasicNameValuePair(
					"mobileNumber", mnum);

			String authString = cid + mnum;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			nameValuePairList.add(CabIdValuePair);
			nameValuePairList.add(MobileValuePair);
			nameValuePairList.add(authValuePair);

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
			String resp = null;

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				resp = stringBuilder.append(bufferedStrChunk).toString();
			}

			Log.d("AuthenticateConnectionGetMyFare resp", "" + resp);

			if (resp != null && resp.length() > 0
					&& resp.contains("Unauthorized Access")) {
				Log.e("CheckPoolFragmentActivity",
						"AuthenticateConnectionGetMyFare Unauthorized Access");
				exceptioncheck = true;
				// Toast.makeText(CheckPoolFragmentActivity.this,
				// getResources().getString(R.string.exceptionstring),
				// Toast.LENGTH_LONG).show();
				return;
			}

			try {
				JSONObject jsonObject = new JSONObject(resp);
				amountToPay = jsonObject.get("fareToPay").toString();
				payToPerson = jsonObject.get("paidBy").toString();
				totalFare = jsonObject.get("totalFare").toString();

				if (isWalletToWallet.equals("isWalletToWallet")) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {

							ArrayList<String> JoinedMemberName = new ArrayList<String>();
							ArrayList<String> joinedMemberNumber = new ArrayList<String>();

							try {
								JSONArray subArray = new JSONArray(
										showmembersresp);
								for (int i = 0; i < subArray.length(); i++) {
									try {
										JoinedMemberName.add(subArray
												.getJSONObject(i)
												.getString("MemberName")
												.toString().trim());
										joinedMemberNumber.add(subArray
												.getJSONObject(i)
												.getString("MemberNumber")
												.toString().trim());
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							int index = joinedMemberNumber.indexOf(payToPerson);
							if (index != -1) {
								String membName = JoinedMemberName.get(index);
								AlertDialog.Builder builder = new AlertDialog.Builder(
										CheckPoolFragmentActivity.this);
								builder.setMessage(rideDetailsModel
										.getOwnerName()
										+ " ("
										+ rideDetailsModel.getMobileNumber()
												.substring(4)
										+ ") agrees to transfer \u20B9"
										+ amountToPay
										+ " towards trip cost, undertaken between "
										+ rideDetailsModel.getFromShortName()
										+ " to "
										+ rideDetailsModel.getToShortName()
										+ ", to "
										+ membName
										+ " ("
										+ payToPerson.substring(4) + ")");
								builder.setCancelable(false);

								builder.setPositiveButton("Yes",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												// checkWalletExists(payToPerson.substring(4));
												checkTransactionLimit(
														OwnerMobileNumber
																.substring(4),
														amountToPay);
											}
										});

								builder.setNegativeButton("No",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
											}
										});

								builder.show();
							}
						}
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private ArrayList<String> readBookedOrCarPreference() {
		SharedPreferences sharedPreferences = getSharedPreferences(
				"AlreadyBookedOrOwnCar", 0);
		String arrayListString = sharedPreferences.getString("arraylist", "");

		ArrayList<String> arrayList;
		if (arrayListString == null || arrayListString.isEmpty()) {
			arrayList = null;
		} else {
			Gson gson = new Gson();
			arrayList = gson.fromJson(arrayListString, ArrayList.class);
		}

		return arrayList;
	}

	private void writeBookedOrCarPreference(String cabID) {

		ArrayList<String> arrayList = readBookedOrCarPreference();

		if (arrayList == null) {
			arrayList = new ArrayList<String>();
		}

		arrayList.add(cabID);

		Gson gson = new Gson();

		String string = gson.toJson(arrayList).toString();

		SharedPreferences sharedPreferences = getSharedPreferences(
				"AlreadyBookedOrOwnCar", 0);
		Editor editor = sharedPreferences.edit();
		editor.putString("arraylist", string.trim());
		editor.commit();

	}

	private void showCabBookingDialog(final boolean shouldShowTripStartDialog) {

		if (rideDetailsModel.getRideType().equals("1")) {
			if (shouldShowTripStartDialog) {
				showTripStartDialog();
			}
			return;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(
				CheckPoolFragmentActivity.this);
		View builderView = (View) getLayoutInflater().inflate(
				R.layout.dialog_trip_start_book_cab, null);

		builder.setView(builderView);
		final AlertDialog dialog = builder.create();

		LinearLayout linearLayout = (LinearLayout) builderView
				.findViewById(R.id.tripstartbookcabll);
		linearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				dialog.dismiss();

				openBookCabPage();
			}
		});

		linearLayout = (LinearLayout) builderView
				.findViewById(R.id.tripstartalreadybookedll);
		linearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				tracker.send(new HitBuilders.EventBuilder()
						.setCategory("Cab already booked ride")
						.setAction("Cab already booked ride")
						.setLabel("Cab already booked ride").build());

				dialog.dismiss();

				writeBookedOrCarPreference(rideDetailsModel.getCabId());

				if (shouldShowTripStartDialog) {
					showTripStartDialog();
				}
			}
		});

		linearLayout = (LinearLayout) builderView
				.findViewById(R.id.tripstartowncarll);
		linearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				tracker.send(new HitBuilders.EventBuilder()
						.setCategory("Driving car ride")
						.setAction("Driving car ride")
						.setLabel("Driving car ride").build());

				dialog.dismiss();

				writeBookedOrCarPreference(rideDetailsModel.getCabId());

				if (shouldShowTripStartDialog) {
					showTripStartDialog();
				}
			}
		});

		linearLayout = (LinearLayout) builderView
				.findViewById(R.id.tripstartcanceltripll);
		linearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				dialog.dismiss();

				cancelTrip();
			}
		});

		dialog.show();
	}

	private void showTripStartDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(
				CheckPoolFragmentActivity.this);
		View builderView = (View) getLayoutInflater().inflate(
				R.layout.dialog_trip_start, null);

		builder.setView(builderView);
		final AlertDialog dialog = builder.create();

		LinearLayout linearLayout = (LinearLayout) builderView
				.findViewById(R.id.tripstartnowll);
		linearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				dialog.dismiss();

				tripStarted(0);
			}
		});

		linearLayout = (LinearLayout) builderView
				.findViewById(R.id.tripstartfivell);
		linearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				dialog.dismiss();

				tripStarted(5);
			}
		});

		linearLayout = (LinearLayout) builderView
				.findViewById(R.id.tripstarttenll);
		linearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				dialog.dismiss();

				tripStarted(10);
			}
		});

		dialog.show();
	}

	private void tripStarted(final int duration) {

		tracker.send(new HitBuilders.EventBuilder()
				.setCategory("Trip start " + duration)
				.setAction("Trip start " + duration)
				.setLabel("Trip start " + duration).build());

		switch (duration) {

		case 0: {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForStartTrip().executeOnExecutor(
						AsyncTask.THREAD_POOL_EXECUTOR,
						rideDetailsModel.getCabId());
			} else {
				new ConnectionTaskForStartTrip().execute(rideDetailsModel
						.getCabId());
			}
			break;
		}

		default: {
			StartTripPHPAlarm startTripPHPAlarm = new StartTripPHPAlarm();
			startTripPHPAlarm.setAlarm(CheckPoolFragmentActivity.this,
					rideDetailsModel.getCabId(), duration);
			break;
		}

		}

		shareLocation(false);

		// AlertDialog.Builder builder = new AlertDialog.Builder(
		// CheckPoolFragmentActivity.this);
		// //
		// builder.setMessage("Member(s) of your trip will now receive your location updates, would you like to share your location with others?");
		// builder.setMessage("Member(s) of your trip will now receive your location updates.");
		// builder.setCancelable(false);

		// builder.setPositiveButton("Select receipients",
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int which) {
		// showContactsDialog();
		// }
		// });

		// builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
		// {
		// public void onClick(DialogInterface dialog, int which) {
		// if (MemberName.size() > 0) {
		// shareLocation(false);
		// }
		// }
		// });

		// builder.setNegativeButton("NO", new DialogInterface.OnClickListener()
		// {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// if (MemberName.size() > 0) {
		// shareLocation(false);
		// }
		//
		// }
		// });

		// builder.show();
	}

	private void shareLocation(boolean hasAdditionalReceipients) {

		if (hasAdditionalReceipients) {
			// for (int i = 0; i < MemberName.size(); i++) {
			// selectednames.add(MemberName.get(i));
			// }
			// for (int i = 0; i < MemberNumber.size(); i++) {
			// selectednumbers.add(MemberNumber.get(i));
			// }
		} else {
			selectednames.clear();
			selectednumbers.clear();

			// selectednames = MemberName;
			// selectednumbers = MemberNumber;
		}
		ShareLocationObject myObject = new ShareLocationObject();
		myObject.recipientsnames = selectednames;
		myObject.recipientsnumbers = selectednumbers;
		myObject.sharetilltype = "Destination";
		myObject.destinationlongname = rideDetailsModel.getToLocation();
		LatLng latLng = endaddlatlng.get(endaddlatlng.size() - 1);
		myObject.destinationlatlong = latLng;
		myObject.destinationtimevalue = System.currentTimeMillis()
				+ (60000 * 2400);
		myObject.cabID = rideDetailsModel.getCabId();

		Gson gson = new Gson();
		String json = gson.toJson(myObject);

		SharedPreferences locationshapref = getSharedPreferences(
				"ShareLocationShared", 0);
		Editor prefsEditor = locationshapref.edit();
		prefsEditor.putString("ShareLocationObject", json);
		prefsEditor.commit();

		startService(new Intent(CheckPoolFragmentActivity.this,
				LocationShareForRideService.class));
	}

	private void openBookCabPage() {

		tracker.send(new HitBuilders.EventBuilder()
				.setCategory("Book a cab ride").setAction("Book a cab ride")
				.setLabel("Book a cab ride").build());

		String addressString = MapUtilityMethods.getAddress(
				CheckPoolFragmentActivity.this, startaddlatlng.get(0).latitude,
				startaddlatlng.get(0).longitude);
		Address address = geocodeAddress(addressString);

		AddressModel startAddressModel = new AddressModel();
		startAddressModel.setAddress(address);
		startAddressModel.setShortname(rideDetailsModel.getFromShortName());
		startAddressModel.setLongname(addressString);

		addressString = MapUtilityMethods.getAddress(
				CheckPoolFragmentActivity.this, endaddlatlng.get(0).latitude,
				endaddlatlng.get(0).longitude);
		address = geocodeAddress(addressString);

		AddressModel endAddressModel = new AddressModel();
		endAddressModel.setAddress(address);
		endAddressModel.setShortname(rideDetailsModel.getToShortName());
		endAddressModel.setLongname(addressString);

		// String StartAddLatLng = startaddlatlng.get(0).latitude
		// + "," + startaddlatlng.get(0).longitude;
		// String EndAddLatLng = endaddlatlng.get(0).latitude + ","
		// + endaddlatlng.get(0).longitude;

		final Intent mainIntent = new Intent(CheckPoolFragmentActivity.this,
				BookaCabFragmentActivity.class);
		Gson gson = new Gson();
		mainIntent.putExtra("StartAddressModel", gson.toJson(startAddressModel)
				.toString());
		mainIntent.putExtra("EndAddressModel", gson.toJson(endAddressModel)
				.toString());
		mainIntent.putExtra("CabId", rideDetailsModel.getCabId());
		// mainIntent.putExtra("FromShortName", FromShortName);
		// mainIntent.putExtra("ToShortName", ToShortName);
		mainIntent.putExtra("TravelDate", rideDetailsModel.getTravelDate());
		mainIntent.putExtra("TravelTime", rideDetailsModel.getTravelTime());
		CheckPoolFragmentActivity.this.startActivity(mainIntent);
	}

	private void cancelTrip() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				CheckPoolFragmentActivity.this);
		builder.setMessage("Are you sure you want to cancel the ride?");
		builder.setCancelable(true);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				tracker.send(new HitBuilders.EventBuilder()
						.setCategory("Cancel Ride").setAction("Cancel Ride")
						.setLabel("Cancel Ride").build());

				UpcomingStartTripAlarm upcomingStartTripAlarm = new UpcomingStartTripAlarm();
				upcomingStartTripAlarm
						.cancelBothAlarms(CheckPoolFragmentActivity.this);

				StartTripPHPAlarm startTripPHPAlarm = new StartTripPHPAlarm();
				startTripPHPAlarm.cancelAlarm(CheckPoolFragmentActivity.this);

				stopService(new Intent(CheckPoolFragmentActivity.this,
						LocationShareForRideService.class));

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new ConnectionTaskForownercancelpool()
							.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					new ConnectionTaskForownercancelpool().execute();
				}
			}
		});
		builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			}
		});
		AlertDialog dialog = builder.show();
		TextView messageText = (TextView) dialog
				.findViewById(android.R.id.message);
		messageText.setGravity(Gravity.CENTER);
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
				Toast.makeText(CheckPoolFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}
			// For affle traking view
			Hashtable<String, Object> extraParams = new Hashtable<String, Object>();
			extraParams.put("cabid", rideDetailsModel.getCabId());


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

			String authString = cid;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			nameValuePairList.add(CabIdValuePair);
			nameValuePairList.add(authValuePair);

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

			if (startresp != null && startresp.length() > 0
					&& startresp.contains("Unauthorized Access")) {
				Log.e("CheckPoolFragmentActivity",
						"AuthenticateConnectionTripCompleted Unauthorized Access");
				exceptioncheck = true;
				// Toast.makeText(CheckPoolFragmentActivity.this,
				// getResources().getString(R.string.exceptionstring),
				// Toast.LENGTH_LONG).show();
				return;
			}
		}
	}

	private class ConnectionTaskForStartTrip extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionStartTrip mAuth1 = new AuthenticateConnectionStartTrip();
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
				Toast.makeText(CheckPoolFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}
		}

	}

	public class AuthenticateConnectionStartTrip {

		public String cid;

		public AuthenticateConnectionStartTrip() {

		}

		public void connection() throws Exception {

			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/startTripNotification.php";
			HttpPost httpPost = new HttpPost(url_select);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();

			BasicNameValuePair CabIdValuePair = new BasicNameValuePair("cabId",
					cid);

			String authString = cid;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			nameValuePairList.add(CabIdValuePair);
			nameValuePairList.add(authValuePair);

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

			Log.d("startresp", "" + startresp);

			if (startresp != null && startresp.length() > 0
					&& startresp.contains("Unauthorized Access")) {
				Log.e("CheckPoolFragmentActivity",
						"AuthenticateConnectionStartTrip Unauthorized Access");
				exceptioncheck = true;
				// Toast.makeText(CheckPoolFragmentActivity.this,
				// getResources().getString(R.string.exceptionstring),
				// Toast.LENGTH_LONG).show();
				return;
			}
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
				Toast.makeText(CheckPoolFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			for (int i = 0; i < rectlinesarr.size(); i++) {

				Polyline polyline = checkpoolmap.addPolyline(rectlinesarr
						.get(i));
				polyline.remove();

			}
			checkpoolmap.clear();

			rectlinesarr.clear();

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

			// Enabling MyLocation Layer of Google Map
			checkpoolmap.setMyLocationEnabled(true);

			LatLngBounds.Builder bc = null;

			for (int i = 0; i < rectlinesarr.size(); i++) {
				checkpoolmap.addPolyline(rectlinesarr.get(i));

				List<LatLng> points = rectlinesarr.get(i).getPoints();

				bc = new LatLngBounds.Builder();

				for (LatLng item : points) {
					bc.include(item);
				}
			}

			checkpoolmap.moveCamera(CameraUpdateFactory.newLatLngBounds(
					bc.build(), 50));

			checkpoolmap.addMarker(new MarkerOptions()
					.position(startaddlatlng.get(0))
					.title(startaddress.get(0))
					.snippet("start")
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.start)));

			checkpoolmap
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

	public class AuthenticateConnectionGetDirection {

		public AuthenticateConnectionGetDirection() {

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

			String source = rideDetailsModel.getFromLocation().replaceAll(" ",
					"%20");
			String dest = rideDetailsModel.getToLocation().replaceAll(" ",
					"%20");

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
						CheckPoolFragmentActivity.this,
						via_waypoint.get(i).latitude,
						via_waypoint.get(i).longitude);
				via_waypointstrarr.add(asd);
			}
			Log.d("via_waypointstrarr", "" + via_waypointstrarr);
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

	// ///////////////////////
	// ///////

	private class ConnectionTaskForShowMembersOnMap extends AsyncTask<String, Void, Void> {

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
				Toast.makeText(CheckPoolFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (showmembersresp != null && showmembersresp.length() > 0
					&& showmembersresp.contains("Unauthorized Access")) {
				Log.e("CheckPoolFragmentActivity",
						"showmembersresp Unauthorized Access");
				Toast.makeText(CheckPoolFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			FaredistanceList.clear();
			FareLocationList.clear();
			FareMemberDropLocaton.clear();
			FareMemberPickLocaton.clear();
			FareMobNoList.clear();

			// Added Owner no and start end location for fare
			FareMobNoList.add(rideDetailsModel.getMobileNumber());

			Address locationAddressFrom = null, locationAddressTo = null;

			String fromAdd = rideDetailsModel.getFromLocation();
			String toAdd = rideDetailsModel.getToLocation();
			Geocoder fcoder = new Geocoder(CheckPoolFragmentActivity.this);
			try {
				ArrayList<Address> adresses = (ArrayList<Address>) fcoder
						.getFromLocationName(fromAdd, 50);

				for (Address add : adresses) {
					locationAddressFrom = add;
				}

				adresses = (ArrayList<Address>) fcoder.getFromLocationName(
						toAdd, 50);
				for (Address add : adresses) {
					locationAddressTo = add;
				}

			} catch (Exception e) {
				e.printStackTrace();

			}
			if(locationAddressFrom == null || locationAddressTo == null)
				return;

			String src = locationAddressFrom.getLatitude() + ","
					+ locationAddressFrom.getLongitude();
			String des = locationAddressTo.getLatitude() + ","
					+ locationAddressTo.getLongitude();

			LatLng llFrom = new LatLng(locationAddressFrom.getLatitude(),
					locationAddressFrom.getLongitude());
			LatLng llTo = new LatLng(locationAddressTo.getLatitude(),
					locationAddressTo.getLongitude());

			FareMemberPickLocaton.add(llFrom);
			FareMemberDropLocaton.add(llTo);

			if (showmembersresp.equalsIgnoreCase("No Members joined yet")) {

				if (onedialog.isShowing()) {
					onedialog.dismiss();
				}

			} else {

				// mycalculatorbtn.setVisibility(View.VISIBLE);

				ShowMemberName.clear();
				ShowMemberNumber.clear();
				ShowMemberLocationAddress.clear();
				ShowMemberLocationLatLong.clear();
				ShowMemberImageName.clear();
				ShowMemberStatus.clear();
				ShowMemberLocationAddressEnd.clear();
				ShowMemberLocationLatLongEnd.clear();

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

							ShowMemberLocationAddressEnd.add(subArray
									.getJSONObject(i)
									.getString("MemberEndLocationAddress")
									.toString());
							ShowMemberLocationLatLongEnd.add(subArray
									.getJSONObject(i)
									.getString("MemberEndLocationlatlong")
									.toString());

							// Added for fare calculation
							FareMobNoList.add(subArray.getJSONObject(i)
									.getString("MemberNumber").toString());

							String arr[] = subArray.getJSONObject(i)
									.getString("MemberLocationlatlong")
									.toString().split(",");

							LatLng l = new LatLng(Double.parseDouble(arr[0]),
									Double.parseDouble(arr[1]));
							FareMemberPickLocaton.add(l);
							String arr1[] = subArray.getJSONObject(i)
									.getString("MemberEndLocationlatlong")
									.toString().split(",");

							LatLng l1 = new LatLng(Double.parseDouble(arr1[0]),
									Double.parseDouble(arr1[1]));
							FareMemberDropLocaton.add(l1);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// for (int i = 0; i < ShowMemberName.size(); i++) {
				//
				//
				// String[] latlongStart =
				// ShowMemberLocationLatLong.get(i).split(
				// ",");
				// String[] latlongEnd =
				// ShowMemberLocationLatLongEnd.get(i).split(
				// ",");
				// LatLng ltStart = new
				// LatLng(Double.parseDouble(latlongStart[0]),
				// Double.parseDouble(latlongStart[1]));
				// LatLng ltEnd = new LatLng(Double.parseDouble(latlongEnd[0]),
				// Double.parseDouble(latlongEnd[1]));
				// checkpoolmap
				// .addMarker(new MarkerOptions()
				// .position(ltStart)
				// .snippet(String.valueOf(i))
				// .title(ShowMemberLocationAddress.get(i))
				// .icon(BitmapDescriptorFactory
				// .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
				// checkpoolmap
				// .addMarker(new MarkerOptions()
				// .position(ltEnd)
				// .snippet("myEndlocation")
				// .title(ShowMemberLocationAddressEnd.get(i))
				// .icon(BitmapDescriptorFactory
				// .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
				//
				//
				// // String[] latlong = ShowMemberLocationLatLong.get(i).split(
				// // ",");
				// // LatLng lt = new LatLng(Double.parseDouble(latlong[0]),
				// // Double.parseDouble(latlong[1]));
				// // checkpoolmap
				// // .addMarker(new MarkerOptions()
				// // .position(lt)
				// // .snippet(String.valueOf(i))
				// // .title(ShowMemberLocationAddress.get(i))
				// // .icon(BitmapDescriptorFactory
				// // .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
				// //
				// }

				String wayPoint = "&waypoints=optimize:true";

				if (!showmembersresp.equalsIgnoreCase("No Members joined yet")) {

					steps.clear();
					Summary.clear();
					startaddress.clear();
					endaddress.clear();
					startaddlatlng.clear();
					endaddlatlng.clear();
					listGeopoints.clear();
					via_waypoint.clear();
					via_waypointstrarr.clear();

					for (int i = 0; i < rectlinesarr.size(); i++) {

						Polyline polyline = checkpoolmap
								.addPolyline(rectlinesarr.get(i));
						polyline.remove();

					}
					checkpoolmap.clear();

					rectlinesarr.clear();

					// http://maps.googleapis.com/maps/api/directions/json?origin=28.48971,77.062282&destination=28.6289146,77.2152869&waypoints=optimize:true|28.5440936,77.2359|28.549156,77.2527764|28.5252398,77.2543449&sensor=false

					if (!showmembersresp
							.equalsIgnoreCase("No Members joined yet")) {
						for (int i = 0; i < ShowMemberLocationLatLong.size(); i++) {

							String latlong[] = ShowMemberLocationLatLong.get(i)
									.split(",");

							wayPoint += "%7C" + latlong[0] + "," + latlong[1];
							if (!ShowMemberLocationLatLongEnd.get(i)
									.equalsIgnoreCase("")) {
								String latlong1[] = ShowMemberLocationLatLongEnd
										.get(i).split(",");
								wayPoint += "%7C" + latlong1[0] + ","
										+ latlong1[1];
							}

						}

					}

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new ConnectionTaskForSingleRoot().executeOnExecutor(
								AsyncTask.THREAD_POOL_EXECUTOR, wayPoint);
					} else {
						new ConnectionTaskForSingleRoot().execute(wayPoint);
					}

				} else {
					if (onedialog.isShowing()) {
						onedialog.dismiss();
					}

					// //////////////////////////
					if (rideDetailsModel.getCabStatus().equals("A")
							&& rideDetailsModel.getStatus().equals("0")) {
						try {
							SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
									"dd/MM/yyyy hh:mm aa");
							Date date = simpleDateFormat.parse(rideDetailsModel
									.getTravelDate()
									+ " "
									+ rideDetailsModel.getTravelTime());

							ArrayList<String> arrayList = readBookedOrCarPreference();

							// Log.d("CheckPoolFragmentActivity", "startTime : "
							// +
							// date.getTime());

							/*if ((date.getTime() - System.currentTimeMillis()) <= (UpcomingStartTripAlarm.START_TRIP_NOTIFICATION_TIME * 60 * 1000)) {
								if (rideDetailsModel.getBookingRefNo() == null
										|| rideDetailsModel.getBookingRefNo()
												.isEmpty()
										|| rideDetailsModel.getBookingRefNo()
												.equalsIgnoreCase("null")) {
									if (arrayList == null) {
										showCabBookingDialog(true);
									} else if (arrayList != null
											&& arrayList
													.indexOf(rideDetailsModel
															.getCabId()) == -1) {
										showCabBookingDialog(true);
									} else {
										showTripStartDialog();
									}
								} else {
									showTripStartDialog();
								}
							} else */
							if ((date.getTime() - System
									.currentTimeMillis()) <= (UpcomingStartTripAlarm.UPCOMING_TRIP_NOTIFICATION_TIME * 60 * 1000)) {
								/*if (rideDetailsModel.getBookingRefNo() == null
										|| rideDetailsModel.getBookingRefNo()
												.isEmpty()
										|| rideDetailsModel.getBookingRefNo()
												.equalsIgnoreCase("null")) {
									if (arrayList == null) {
										showCabBookingDialog(false);
									} else if (arrayList != null
											&& arrayList
													.indexOf(rideDetailsModel
															.getCabId()) == -1) {
										showCabBookingDialog(false);
									}
								}*/
								showTripStartDialog();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (rideDetailsModel.getCabStatus().equals("A")
							&& rideDetailsModel.getStatus().equals("2")) {
						// For affle traking view
						Hashtable<String, Object> extraParams = new Hashtable<String, Object>();
						extraParams.put("cabid", rideDetailsModel.getCabId());

						if (rideDetailsModel.getRideType().equals("1")) {
							FareCalculatorNewCarPool fareCalculatorNewCarPool = new FareCalculatorNewCarPool(
									FareMobNoList, FareMemberPickLocaton,
									FareMemberDropLocaton, FareLocationList,
									FaredistanceList,
									Double.valueOf(rideDetailsModel
											.getPerKmCharge()));

							HashMap<String, Double> hashMap = fareCalculatorNewCarPool
									.getFareSplit();

							Log.d("CheckPoolFragmentActivity",
									"fareCalculatorNew : " + hashMap);

							sendFareSplitToMembers(hashMap);
						} else {
							showRideCompleteDialog();
						}
					} else if (rideDetailsModel.getCabStatus().equals("A")
							&& rideDetailsModel.getStatus().equals("3")) {
						showPaymentDialog();
					} else if (rideDetailsModel.getCabStatus().equals("A")) {
						try {
							SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
									"dd/MM/yyyy hh:mm aa");
							Date date = simpleDateFormat.parse(rideDetailsModel
									.getTravelDate()
									+ " "
									+ rideDetailsModel.getTravelTime());

							long expDuration = Long.parseLong(rideDetailsModel
									.getExpTripDuration());

							if (System.currentTimeMillis() >= (date.getTime() + expDuration * 1000)) {
								Log.d("CheckPoolFragmentActivity",
										"ExpTripDuration trip completed");
								if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
									new ConnectionTaskForTripCompleted()
											.executeOnExecutor(
													AsyncTask.THREAD_POOL_EXECUTOR,
													rideDetailsModel.getCabId());
								} else {
									new ConnectionTaskForTripCompleted()
											.execute(rideDetailsModel
													.getCabId());
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					// //////////////////////////

				}

				checkpoolmap
						.setOnMarkerClickListener(new OnMarkerClickListener() {

							@Override
							public boolean onMarkerClick(Marker arg0) {

								if (arg0.getSnippet().equalsIgnoreCase("start")) {

									arg0.showInfoWindow();
								} else if (arg0.getSnippet().equalsIgnoreCase(
										"end")) {
									arg0.showInfoWindow();

								}

								else {

									if (rideDetailsModel.getCabStatus()
											.toString().trim()
											.equalsIgnoreCase("A")) {
										String[] arr = arg0.getSnippet().split(
												",");

										final Integer index = Integer
												.parseInt(arr[0]) - 1;
										String pickdrop = arr[1];

										if (pickdrop.equalsIgnoreCase("Pick"))
											showAlertDialog(
													ShowMemberName.get(index),
													ShowMemberNumber.get(index),
													ShowMemberLocationAddress
															.get(index),
													ShowMemberLocationLatLong
															.get(index),
													ShowMemberImageName
															.get(index),
													ShowMemberStatus.get(index),
													pickdrop);

										else if (pickdrop
												.equalsIgnoreCase("Drop")) {

											if (!ShowMemberLocationLatLongEnd
													.get(index)
													.equalsIgnoreCase("")) {

												showAlertDialog(ShowMemberName
														.get(index),
														ShowMemberNumber
																.get(index),
														ShowMemberLocationAddressEnd
																.get(index),
														ShowMemberLocationLatLongEnd
																.get(index),
														ShowMemberImageName
																.get(index),
														ShowMemberStatus
																.get(index),
														pickdrop);

											}

										}
									}
								}

								return true;
							}

						});

			}

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
					"CabId", rideDetailsModel.getCabId());

			String authString = rideDetailsModel.getCabId();
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(CabIdBasicNameValuePair);
			nameValuePairList.add(authValuePair);

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

	// /////////////////////
	// ///////

	private class ConnectionTaskForSingleRoot extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionSingleRoot mAuth1 = new AuthenticateConnectionSingleRoot();
			try {
				mAuth1.wayPointUrl = args[0];
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

			if (onedialog.isShowing()) {
				onedialog.dismiss();
			}

			if (exceptioncheck) {
				exceptioncheck = false;
				Toast.makeText(CheckPoolFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			// int index = 0;
			rectlinesarr.clear();

			Random rnd = new Random();
			int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256),
					rnd.nextInt(256));
			for (int i = 0; i < steps.size(); i++) {

				ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();

				JSONArray subArray123;
				try {
					subArray123 = new JSONArray(steps.get(i));
					for (int i111 = 0; i111 < subArray123.length(); i111++) {
						String locationstr = subArray123.getJSONObject(i111).getString("start_location").toString();
						JSONObject jsonObject11 = new JSONObject(locationstr);
						double lat1 = Double.parseDouble(jsonObject11.getString("lat"));
						double lng1 = Double.parseDouble(jsonObject11.getString("lng"));
						listGeopoints.add(new LatLng(lat1, lng1));
						String locationstr1 = subArray123.getJSONObject(i111).getString("polyline").toString();
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

						// Random rnd = new Random();
						// int color = Color.argb(255, rnd.nextInt(256),
						// rnd.nextInt(256), rnd.nextInt(256));

						PolylineOptions rectLine = new PolylineOptions().width(5).color(color);
						rectLine.addAll(listGeopoints);
						rectlinesarr.add(rectLine);

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			checkpoolmap.setMyLocationEnabled(true);

			checkpoolmap.setOnMapClickListener(new OnMapClickListener() {

				@Override
				public void onMapClick(LatLng point) {

					// setOnMapClick(point);
				}
			});

			for (int i = 0; i < startaddlatlng.size(); i++) {

				Log.d("startaddlatlng", "" + startaddlatlng.size());

				if (i == 0)
					checkpoolmap.addMarker(new MarkerOptions()
							.position(startaddlatlng.get(i))
							.title(startaddress.get(i))
							.snippet("start")
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.start)));

				else if (i == (startaddlatlng.size() - 1))

					checkpoolmap.addMarker(new MarkerOptions()
							.position(endaddlatlng.get(i))
							.title(endaddress.get(i))
							.snippet("end")
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.end)));
				else {

					checkpoolmap
							.addMarker(new MarkerOptions()
									.position(startaddlatlng.get(i))
									.title(startaddress.get(i))
									.snippet(String.valueOf(i) + "," + "Pick")
									.icon(BitmapDescriptorFactory
											.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

					checkpoolmap
							.addMarker(new MarkerOptions()
									.position(endaddlatlng.get(i))
									.title(endaddress.get(i))
									.snippet(String.valueOf(i) + "," + "Drop")
									.icon(BitmapDescriptorFactory
											.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
				}

			}
			LatLngBounds.Builder bc = null;

			for (int i = 0; i < rectlinesarr.size(); i++) {
				checkpoolmap.addPolyline(rectlinesarr.get(i));

				List<LatLng> points = rectlinesarr.get(i).getPoints();

				bc = new LatLngBounds.Builder();

				for (LatLng item : points) {
					bc.include(item);
				}
			}

			bc.include(startaddlatlng.get(0));
			bc.include(endaddlatlng.get(endaddlatlng.size() - 1));
			checkpoolmap.moveCamera(CameraUpdateFactory.newLatLngBounds(
					bc.build(), 50));

			Log.d("FareMobno", "" + FareMobNoList);
			Log.d("FareDistance", "" + FaredistanceList);
			Log.d("FareLocation", "" + FareLocationList);
			Log.d("FarePick", "" + FareMemberPickLocaton);
			Log.d("FareDrop", "" + FareMemberDropLocaton);

			// //////////////////////////
			if (rideDetailsModel.getCabStatus().equals("A")
					&& rideDetailsModel.getStatus().equals("0")) {
				try {
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
							"dd/MM/yyyy hh:mm aa");
					Date date = simpleDateFormat.parse(rideDetailsModel
							.getTravelDate()
							+ " "
							+ rideDetailsModel.getTravelTime());

					ArrayList<String> arrayList = readBookedOrCarPreference();

					// Log.d("CheckPoolFragmentActivity", "startTime : " +
					// date.getTime());

					if ((date.getTime() - System.currentTimeMillis()) <= (UpcomingStartTripAlarm.START_TRIP_NOTIFICATION_TIME * 60 * 1000)) {
						if (rideDetailsModel.getBookingRefNo() == null
								|| rideDetailsModel.getBookingRefNo().isEmpty()
								|| rideDetailsModel.getBookingRefNo()
										.equalsIgnoreCase("null")) {
							if (arrayList == null) {
								showCabBookingDialog(true);
							} else if (arrayList != null
									&& arrayList.indexOf(rideDetailsModel
											.getCabId()) == -1) {
								showCabBookingDialog(true);
							} else {
								showTripStartDialog();
							}
						} else {
							showTripStartDialog();
						}
					} else if ((date.getTime() - System.currentTimeMillis()) <= (UpcomingStartTripAlarm.UPCOMING_TRIP_NOTIFICATION_TIME * 60 * 1000)) {
						if (rideDetailsModel.getBookingRefNo() == null
								|| rideDetailsModel.getBookingRefNo().isEmpty()
								|| rideDetailsModel.getBookingRefNo()
										.equalsIgnoreCase("null")) {
							if (arrayList == null) {
								showCabBookingDialog(false);
							} else if (arrayList != null
									&& arrayList.indexOf(rideDetailsModel
											.getCabId()) == -1) {
								showCabBookingDialog(false);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (rideDetailsModel.getCabStatus().equals("A")
					&& rideDetailsModel.getStatus().equals("2")) {
				// For affle traking view
				Hashtable<String, Object> extraParams = new Hashtable<String, Object>();
				extraParams.put("cabid", rideDetailsModel.getCabId());

				if (rideDetailsModel.getRideType().equals("1")) {
					FareCalculatorNewCarPool fareCalculatorNewCarPool = new FareCalculatorNewCarPool(
							FareMobNoList, FareMemberPickLocaton,
							FareMemberDropLocaton, FareLocationList,
							FaredistanceList, Double.valueOf(rideDetailsModel
									.getPerKmCharge()));

					HashMap<String, Double> hashMap = fareCalculatorNewCarPool
							.getFareSplit();

					Log.d("CheckPoolFragmentActivity", "fareCalculatorNew : "
							+ hashMap);

					sendFareSplitToMembers(hashMap);
				} else {
					showRideCompleteDialog();
				}
			} else if (rideDetailsModel.getCabStatus().equals("A")
					&& rideDetailsModel.getStatus().equals("3")) {
				showPaymentDialog();
			} else if (rideDetailsModel.getCabStatus().equals("A")) {
				try {
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
							"dd/MM/yyyy hh:mm aa");
					Date date = simpleDateFormat.parse(rideDetailsModel
							.getTravelDate()
							+ " "
							+ rideDetailsModel.getTravelTime());

					long expDuration = Long.parseLong(rideDetailsModel
							.getExpTripDuration());

					if (System.currentTimeMillis() >= (date.getTime() + expDuration * 1000)) {
						Log.d("CheckPoolFragmentActivity",
								"ExpTripDuration trip completed");
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							new ConnectionTaskForTripCompleted()
									.executeOnExecutor(
											AsyncTask.THREAD_POOL_EXECUTOR,
											rideDetailsModel.getCabId());
						} else {
							new ConnectionTaskForTripCompleted()
									.execute(rideDetailsModel.getCabId());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// //////////////////////////

		}

	}

	public class AuthenticateConnectionSingleRoot {
		private String wayPointUrl;

		public AuthenticateConnectionSingleRoot() {

		}

		public void connection() throws Exception {

			String source = rideDetailsModel.getFromLocation().replaceAll(" ",
					"%20");
			String dest = rideDetailsModel.getToLocation().replaceAll(" ",
					"%20");
			Address locationAddressFrom = null, locationAddressTo = null;

			String fromAdd = rideDetailsModel.getFromLocation();
			String toAdd = rideDetailsModel.getToLocation();
			Geocoder fcoder = new Geocoder(CheckPoolFragmentActivity.this);
			try {
				ArrayList<Address> adresses = (ArrayList<Address>) fcoder
						.getFromLocationName(fromAdd, 50);

				for (Address add : adresses) {
					locationAddressFrom = add;
				}

				adresses = (ArrayList<Address>) fcoder.getFromLocationName(
						toAdd, 50);
				for (Address add : adresses) {
					locationAddressTo = add;
				}

			} catch (Exception e) {
				e.printStackTrace();

			}

			String src = locationAddressFrom.getLatitude() + ","
					+ locationAddressFrom.getLongitude();
			String des = locationAddressTo.getLatitude() + ","
					+ locationAddressTo.getLongitude();

			Log.d("src:", "" + src);
			Log.d("des", "" + des);

			// http://maps.googleapis.com/maps/api/directions/json?origin=28.48971,77.062282&destination=28.6289146,77.2152869&waypoints=optimize:true|28.5440936,77.2359|28.549156,77.2527764|28.5252398,77.2543449&sensor=false

			String url = "https://maps.googleapis.com/maps/api/directions/json?"
					+ "origin="
					+ src
					+ "&destination="
					+ des
					+ wayPointUrl
					+ "&sensor=false&units=metric&mode=driving&alternatives=true&key="
					+ GlobalVariables.GoogleMapsAPIKey;

			Log.d("url single path", "" + url);

			CompletePageResponse = new Communicator().executeHttpGet(url);

			CompletePageResponse = CompletePageResponse
					.replaceAll("\\\\/", "/");

			JSONObject jsonObject = new JSONObject(CompletePageResponse);

			String name = jsonObject.getString("routes");

			JSONArray subArray = new JSONArray(name);
			Summary.clear();

			for (int i = 0; i < subArray.length(); i++) {

				Summary.add(subArray.getJSONObject(i).getString("summary")
						.toString());

				String name1 = subArray.getJSONObject(i).getString("legs")
						.toString();

				JSONArray subArray1 = new JSONArray(name1);

				for (int i1 = 0; i1 < subArray1.length(); i1++) {

					// int i1 = 0;
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

					// Code fro get distance and duration

					String duration = subArray1.getJSONObject(i1)
							.getString("duration").toString();
					JSONObject jsonObjectDuraton = new JSONObject(duration);

					durationList.add(jsonObjectDuraton.getInt("value"));

					String distance = subArray1.getJSONObject(i1)
							.getString("distance").toString();
					JSONObject jsonObjectDistance = new JSONObject(distance);

					distanceList.add(jsonObjectDistance.getInt("value"));
					// ////////////

					if (i1 == 0) {
						FareLocationList.add(new LatLng(lat, lng));
						FareLocationList.add(new LatLng(lat4, lng4));
						FaredistanceList.add(0.0);
						FaredistanceList.add(Double.parseDouble(""
								+ jsonObjectDistance.getInt("value")));

					} else {
						FareLocationList.add(new LatLng(lat4, lng4));
						FaredistanceList.add(Double.parseDouble(""
								+ jsonObjectDistance.getInt("value")));

					}

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
						CheckPoolFragmentActivity.this,
						via_waypoint.get(i).latitude,
						via_waypoint.get(i).longitude);
				via_waypointstrarr.add(asd);
			}
			Log.d("via_waypointstrarr", "" + via_waypointstrarr);
		}
	}

	// //////////
	// ///

	private class ConnectionTaskForDirectionsnew extends AsyncTask<String, Void, Void> {

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
				Toast.makeText(CheckPoolFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			checkpoolmap.clear();

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
				checkpoolmap.addPolyline(rectlinesarr.get(i));

				List<LatLng> points = rectlinesarr.get(i).getPoints();

				bc = new LatLngBounds.Builder();

				for (LatLng item : points) {
					bc.include(item);
				}
			}

			checkpoolmap.moveCamera(CameraUpdateFactory.newLatLngBounds(
					bc.build(), 50));

			Marker marker = checkpoolmap.addMarker(new MarkerOptions()
					.position(startaddlatlng.get(0))
					.title(startaddress.get(0))
					.snippet("start")
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.start)));

			Marker marker1 = checkpoolmap
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
						CheckPoolFragmentActivity.this,
						via_waypoint.get(i).latitude,
						via_waypoint.get(i).longitude);
				via_waypointstrarr.add(asd);
			}
			Log.d("via_waypointstrarr", "" + via_waypointstrarr);
		}
	}

	// //////////////////
	// ///////

	private class ConnectionTaskForownercancelpool extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(
				CheckPoolFragmentActivity.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionownercancelpool mAuth1 = new AuthenticateConnectionownercancelpool();
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
				Toast.makeText(CheckPoolFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (ownercancelpoolresp != null && ownercancelpoolresp.length() > 0
					&& ownercancelpoolresp.contains("Unauthorized Access")) {
				Log.e("CheckPoolFragmentActivity",
						"ownercancelpoolresp Unauthorized Access");
				Toast.makeText(CheckPoolFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			 Intent mainIntent = new Intent(CheckPoolFragmentActivity.this,
			NewHomeScreen.class);
			
			 mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
			 | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			 startActivity(mainIntent);

			finish();

		}

	}

	public class AuthenticateConnectionownercancelpool {

		public AuthenticateConnectionownercancelpool() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/cancelpoolbyowner.php";
			HttpPost httpPost = new HttpPost(url_select);

			BasicNameValuePair CabIdBasicNameValuePair = new BasicNameValuePair(
					"CabId", rideDetailsModel.getCabId());
			BasicNameValuePair OwnerNameBasicNameValuePair = new BasicNameValuePair(
					"OwnerName", rideDetailsModel.getOwnerName());
			BasicNameValuePair OwnerNumberBasicNameValuePair = new BasicNameValuePair(
					"OwnerNumber", rideDetailsModel.getMobileNumber());
			BasicNameValuePair MessageBasicNameValuePair = new BasicNameValuePair(
					"Message", rideDetailsModel.getOwnerName()
							+ " cancelled the ride from "
							+ rideDetailsModel.getFromShortName() + " to "
							+ rideDetailsModel.getToShortName());

			String authString = rideDetailsModel.getCabId()
					+ rideDetailsModel.getOwnerName()
					+ " cancelled the ride from "
					+ rideDetailsModel.getFromShortName() + " to "
					+ rideDetailsModel.getToShortName()
					+ rideDetailsModel.getOwnerName()
					+ rideDetailsModel.getMobileNumber();
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(CabIdBasicNameValuePair);
			nameValuePairList.add(OwnerNameBasicNameValuePair);
			nameValuePairList.add(OwnerNumberBasicNameValuePair);
			nameValuePairList.add(MessageBasicNameValuePair);
			nameValuePairList.add(authValuePair);

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
				ownercancelpoolresp = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("ownercancelpoolresp", "" + stringBuilder.toString());
		}
	}

	private void showAlertDialog(final String mname, final String mnum,
			String mlocadd, String mloclatlon, String mimgname, String mstatus,
			String pickdrop) {

		final Dialog dialogMain = new Dialog(this);
		dialogMain.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogMain.setContentView(R.layout.memberdeatilspopup);

		memimage = (CircularImageView) dialogMain.findViewById(R.id.memimage);

		String url1 = null;

		if (mimgname.toString().trim().isEmpty()) {

		} else {

			url1 = GlobalVariables.ServiceUrl + "/ProfileImages/" + mimgname;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new DownloadImageTask().executeOnExecutor(
						AsyncTask.THREAD_POOL_EXECUTOR, url1);
			} else {
				new DownloadImageTask().execute(url1);
			}
		}

		TextView memname = (TextView) dialogMain.findViewById(R.id.memname);
		memname.setText(mname.toUpperCase());
		dialogMain.show();

		TextView memlocationadd = (TextView) dialogMain
				.findViewById(R.id.memlocationadd);
		memlocationadd.setText(pickdrop + " Location: " + mlocadd);

		LinearLayout dropuser = (LinearLayout) dialogMain
				.findViewById(R.id.dropuser);
		dropuser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				AlertDialog.Builder builder = new AlertDialog.Builder(
						CheckPoolFragmentActivity.this);
				builder.setMessage("Are you sure you want to remove this person from the ride?");
				builder.setCancelable(true);
				builder.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								dialog.dismiss();
								dialogMain.dismiss();
								onedialog = new ProgressDialog(
										CheckPoolFragmentActivity.this);
								onedialog.setMessage("Please Wait...");
								onedialog.setCancelable(false);
								onedialog.setCanceledOnTouchOutside(false);
								onedialog.show();

								if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
									new ConnectionTaskFordropuserfrompopup()
											.executeOnExecutor(
													AsyncTask.THREAD_POOL_EXECUTOR,
													mnum, mname);
								} else {
									new ConnectionTaskFordropuserfrompopup()
											.execute(mnum, mname);
								}
							}
						});
				builder.setNegativeButton("NO",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialogMain.dismiss();

								dialog.dismiss();

							}
						});
				AlertDialog dialog = builder.show();
				TextView messageText = (TextView) dialog
						.findViewById(android.R.id.message);
				messageText.setGravity(Gravity.CENTER);
				dialog.show();
			}
		});

		LinearLayout call = (LinearLayout) dialogMain.findViewById(R.id.call);
		call.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				tracker.send(new HitBuilders.EventBuilder()
						.setCategory("Call Member").setAction("Call Member")
						.setLabel("Call Member").build());

				Intent intent = new Intent(Intent.ACTION_DIAL);
				intent.setData(Uri.parse("tel:" + mnum));
				startActivity(intent);

				dialogMain.dismiss();
			}
		});

		LinearLayout addtocontacts = (LinearLayout) dialogMain
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

		addtocontacts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(Intent.ACTION_INSERT,
						ContactsContract.Contacts.CONTENT_URI);
				intent = new Intent(
						ContactsContract.Intents.SHOW_OR_CREATE_CONTACT, Uri
								.parse("tel:" + mnum));
				startActivity(intent);

				dialogMain.dismiss();
			}
		});

		dialogMain.show();
	}

	// //////////////////
	// ///////

	private class ConnectionTaskFordropuserfrompopup extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectiondropuserfrompopup mAuth1 = new AuthenticateConnectiondropuserfrompopup();
			try {
				mAuth1.memnum = args[0];
				mAuth1.memname = args[1];
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
				Toast.makeText(CheckPoolFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (dropuserfrompopupresp != null
					&& dropuserfrompopupresp.length() > 0
					&& dropuserfrompopupresp.contains("Unauthorized Access")) {
				Log.e("CheckPoolFragmentActivity",
						"dropuserfrompopupresp Unauthorized Access");
				Toast.makeText(CheckPoolFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForDirections()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new ConnectionTaskForDirections().execute();
			}

		}

	}

	public class AuthenticateConnectiondropuserfrompopup {

		public String memnum;
		public String memname;

		public AuthenticateConnectiondropuserfrompopup() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/dropuserfrompopup.php";
			HttpPost httpPost = new HttpPost(url_select);

			BasicNameValuePair CabIdBasicNameValuePair = new BasicNameValuePair(
					"CabId", rideDetailsModel.getCabId());
			BasicNameValuePair OwnerNameBasicNameValuePair = new BasicNameValuePair(
					"OwnerName", rideDetailsModel.getOwnerName());
			BasicNameValuePair OwnerNumberBasicNameValuePair = new BasicNameValuePair(
					"OwnerNumber", rideDetailsModel.getOwnerName());
			BasicNameValuePair MemberNameBasicNameValuePair = new BasicNameValuePair(
					"MemberName", memname);
			BasicNameValuePair MemberNumberBasicNameValuePair = new BasicNameValuePair(
					"MemberNumber", memnum);
			BasicNameValuePair MessageBasicNameValuePair = new BasicNameValuePair(
					"Message", rideDetailsModel.getOwnerName()
							+ " has removed you from the ride from "
							+ rideDetailsModel.getFromShortName() + " to "
							+ rideDetailsModel.getToShortName());

			String authString = rideDetailsModel.getCabId() + memname + memnum
					+ rideDetailsModel.getOwnerName()
					+ " has removed you from the ride from "
					+ rideDetailsModel.getFromShortName() + " to "
					+ rideDetailsModel.getToShortName()
					+ rideDetailsModel.getOwnerName()
					+ rideDetailsModel.getOwnerName();
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(CabIdBasicNameValuePair);
			nameValuePairList.add(OwnerNameBasicNameValuePair);
			nameValuePairList.add(OwnerNumberBasicNameValuePair);
			nameValuePairList.add(MemberNameBasicNameValuePair);
			nameValuePairList.add(MemberNumberBasicNameValuePair);
			nameValuePairList.add(MessageBasicNameValuePair);
			nameValuePairList.add(authValuePair);

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
				dropuserfrompopupresp = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("dropuserfrompopupresp", "" + stringBuilder.toString());
		}
	}

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			memimage.setImageBitmap(result);
		}
	}

	// //////////////////
	// ///////

	private class ConnectionTaskForsendcustommessage extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(
				CheckPoolFragmentActivity.this);

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
				Toast.makeText(CheckPoolFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (sendcustommessagefrompopupresp != null
					&& sendcustommessagefrompopupresp.length() > 0
					&& sendcustommessagefrompopupresp
							.contains("Unauthorized Access")) {
				Log.e("CheckPoolFragmentActivity",
						"sendcustommessagefrompopupresp Unauthorized Access");
				Toast.makeText(CheckPoolFragmentActivity.this,
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

			String authString = memnum + mess;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MemberNumberBasicNameValuePair);
			nameValuePairList.add(MessageBasicNameValuePair);
			nameValuePairList.add(authValuePair);

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

	private void joinPoolPopUp(String Fm, String tol, String td, String tt,
			String st, String ownname, String ownimg) {

		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.joinpoolpopup);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.TRANSPARENT));

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

		AQuery aq = new AQuery(CheckPoolFragmentActivity.this);

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

		TextView textViewCharges = (TextView) dialog
				.findViewById(R.id.carpoolchargestext);
		if (rideDetailsModel.getRideType().equals("1")) {
			textViewCharges.setVisibility(View.VISIBLE);
			textViewCharges.setText("Per seat charge :  \u20B9"
					+ rideDetailsModel.getPerKmCharge() + "/km");
		} else {
			textViewCharges.setVisibility(View.GONE);
		}

		if (showmembersresp.equalsIgnoreCase("No Members joined yet")) {

			joinedmembersll.setVisibility(View.GONE);

		} else {

			ArrayList<String> JoinedMemberName = new ArrayList<String>();
			ArrayList<String> joinedMemberImageName = new ArrayList<String>();

			try {
				JSONArray subArray = new JSONArray(showmembersresp);
				for (int i = 0; i < subArray.length(); i++) {
					try {
						JoinedMemberName.add(subArray.getJSONObject(i)
								.getString("MemberName").toString().trim());
						joinedMemberImageName
								.add(subArray.getJSONObject(i)
										.getString("MemberImageName")
										.toString().trim());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				joinedmembersll.setVisibility(View.VISIBLE);

				ListViewAdapterJoined adapter = new ListViewAdapterJoined(
						CheckPoolFragmentActivity.this, joinedMemberImageName,
						JoinedMemberName);
				joinedmemberslist.setAdapter(adapter);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (rideDetailsModel.getBookingRefNo() == null
				|| rideDetailsModel.getBookingRefNo().isEmpty()
				|| rideDetailsModel.getBookingRefNo().equalsIgnoreCase("null")) {
			LinearLayout linearLayout = (LinearLayout) dialog
					.findViewById(R.id.cabbookingll);
			linearLayout.setVisibility(View.GONE);
		} else {
			TextView textView = (TextView) dialog
					.findViewById(R.id.cabbookingdriver);
			textView.setText("Driver : " + rideDetailsModel.getDriverName());

			textView = (TextView) dialog
					.findViewById(R.id.cabbookingdriverphone);
			textView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_DIAL, Uri
							.parse("tel:" + rideDetailsModel.getDriverNumber()));
					startActivity(intent);
				}
			});

			textView = (TextView) dialog.findViewById(R.id.cabbookingvehicle);
			textView.setText("Vehicle : " + rideDetailsModel.getCarNumber());

			textView = (TextView) dialog.findViewById(R.id.cabbookingrefno);
			textView.setText("Booking reference : "
					+ rideDetailsModel.getBookingRefNo());

			Button button = (Button) dialog.findViewById(R.id.cancelBooking);
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					AlertDialog.Builder builder = new AlertDialog.Builder(
							CheckPoolFragmentActivity.this);
					builder.setMessage("Are you sure you want to cancel the booking?");

					builder.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									if (rideDetailsModel.getCabName()
											.equalsIgnoreCase("Uber")) {
										cancelUberCab();
									} else if (rideDetailsModel.getCabName()
											.equalsIgnoreCase("Mega")) {
										cancelMegaCab();
									} else if (rideDetailsModel.getCabName()
											.equalsIgnoreCase("TaxiForSure")) {
										cancelTFSCab();
									} else if (rideDetailsModel.getCabName()
											.equalsIgnoreCase("ola")) {
										cancelOlaCab();
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

		if (totalFare != null && !totalFare.isEmpty() && amountToPay != null
				&& !amountToPay.isEmpty()) {
			LinearLayout linearLayout = (LinearLayout) dialog
					.findViewById(R.id.faredetailsll);
			linearLayout.setVisibility(View.VISIBLE);

			TextView textView = (TextView) dialog
					.findViewById(R.id.faredetailstotal);
			textView.setText("Total fare: \u20B9" + totalFare);
			textView = (TextView) dialog.findViewById(R.id.faredetailsshare);
			textView.setText("Your share: \u20B9" + amountToPay);
		}

		dialog.show();

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new ConnectionTaskForupdatestatus().executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR,
					rideDetailsModel.getCabId(), MemberNumberstr, "online");
		} else {
			new ConnectionTaskForupdatestatus().execute(
					rideDetailsModel.getCabId(), MemberNumberstr, "online");
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new ConnectionTaskForTripCompletedtask().executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR,
					rideDetailsModel.getCabId(), MemberNumberstr);
		} else {
			new ConnectionTaskForTripCompletedtask().execute(
					rideDetailsModel.getCabId(), MemberNumberstr);
		}

		Log.d("checkpool onStart", "checkpool onStart");
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
		Log.d("checkpool onStop", "checkpool onStop");

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new ConnectionTaskForupdatestatus().executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR,
					rideDetailsModel.getCabId(), MemberNumberstr, "offline");
		} else {
			new ConnectionTaskForupdatestatus().execute(
					rideDetailsModel.getCabId(), MemberNumberstr, "offline");
		}

		super.onStop();
	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();

		if (comefrom != null) {

			if (!chatlayoutmainrl.isShown()) {
				Intent mainIntent = new Intent(CheckPoolFragmentActivity.this,
						NewHomeScreen.class);
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
				CheckPoolFragmentActivity.this.finish();
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
				String uname = MemberNumberstr.toString().trim() + "_"
						+ rideDetailsModel.getCabId(), pwd = MemberNumberstr
						.toString().trim() + "_" + rideDetailsModel.getCabId(), name = FullName, email = "";
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
						SASLAuthentication.supportSASLMechanism("PLAIN", 0);

						xmppConnection.connect();
						// chatBubbleList = new ArrayList<ChatBubble>();
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}

					try {
						// xmppConnection.login("cmcadmin", "clubmycab");

						AccountManager am = new AccountManager(xmppConnection);
						am.createAccount(uname, pwd, hashMap);
						Log.d("Openfire", "Account Created");
					} catch (Exception e) {
						e.printStackTrace();
						Log.e("Openfire", "Account already exist");
					}

					try {
						xmppConnection.login(uname, pwd);
						// xmppConnection.login("cmc@" + IPADDRESS,
						// "clubmycab");
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
						+ rideDetailsModel.getCabId() + "@" + IPADDRESS;
				message = new Message(userid, Message.Type.chat);
				message.setBody(text);
				xmppConnection.sendPacket(message);
			}

			String datetime = String.valueOf(System.currentTimeMillis());
			db.addChattodb(new ChatObject(rideDetailsModel.getCabId(),
					FullName, text, datetime));

			chatBubbleList.add(new ChatBubble(false, text, System
					.currentTimeMillis(), FullName));
			setListAdapter();

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForsendchatnotification().executeOnExecutor(
						AsyncTask.THREAD_POOL_EXECUTOR,
						rideDetailsModel.getCabId(), MemberNumberstr,
						MemberNumber.toString().trim(), text, FullName,
						rideDetailsModel.getOwnerName(), OwnerMobileNumber);
			} else {
				new ConnectionTaskForsendchatnotification().execute(
						rideDetailsModel.getCabId(), MemberNumberstr,
						MemberNumber.toString().trim(), text, FullName,
						rideDetailsModel.getOwnerName(), OwnerMobileNumber);
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
						db.addChattodb(new ChatObject(rideDetailsModel
								.getCabId(), username, text, datetime));

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
				Toast.makeText(CheckPoolFragmentActivity.this,
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

			String authString = cid + name + mnumstr + memnum + txtmsg
					+ ownername + ownermnum;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			Log.d("CheckPoolFragmentActivity",
					"AuthenticateConnectionsendchatnotification CabId : " + cid
							+ " MemberNumber : " + mnumstr
							+ " MemberNumberARR : " + memnum + " Message : "
							+ txtmsg + " MemberName : " + name
							+ " ownername : " + ownername + " ownernumber : "
							+ ownermnum);

			nameValuePairList.add(CabIdValuePair);
			nameValuePairList.add(MemberNumberValuePair);
			nameValuePairList.add(MemberNumberARRValuePair);
			nameValuePairList.add(MessageValuePair);
			nameValuePairList.add(MemberNameValuePair);
			nameValuePairList.add(ownernameValuePair);
			nameValuePairList.add(ownernumberValuePair);
			nameValuePairList.add(authValuePair);

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

			if (chatresp != null && chatresp.length() > 0
					&& chatresp.contains("Unauthorized Access")) {
				Log.e("CheckPoolFragmentActivity",
						"chatresp Unauthorized Access");
				exceptioncheck = true;
				// Toast.makeText(CheckPoolFragmentActivity.this,
				// getResources().getString(R.string.exceptionstring),
				// Toast.LENGTH_LONG).show();
				return;
			}
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
				Toast.makeText(CheckPoolFragmentActivity.this,
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

			Log.d("CheckPoolFragmentActivity", "changeuserstatus status : "
					+ status);

			if (status.equalsIgnoreCase("offline")) {

				try {
					if (xmppConnection != null) {
						Log.d("CheckPoolFragmentActivity",
								"changeuserstatus xmppConnection.disconnect()");
						xmppConnection.disconnect();
					}
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
				Toast.makeText(CheckPoolFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (MemberNumberstr.equalsIgnoreCase(OwnerMobileNumber)) {

			} else {
				MemberName.add(rideDetailsModel.getOwnerName());
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

			String authString = cabid + mnum;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			nameValuePairList.add(CabIdValuePair);
			nameValuePairList.add(MemberNumberValuePair);
			nameValuePairList.add(authValuePair);

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

			if (result != null && result.length() > 0
					&& result.contains("Unauthorized Access")) {
				Log.e("CheckPoolFragmentActivity",
						"AuthenticateConnectionTripCompletedtask Unauthorized Access");
				exceptioncheck = true;
				// Toast.makeText(CheckPoolFragmentActivity.this,
				// getResources().getString(R.string.exceptionstring),
				// Toast.LENGTH_LONG).show();
				return;
			}

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

	@Override
	public void sendFareSplitHashMap(final HashMap<String, Double> hashMap) {
		Log.d("CheckPool", "sendFareSplitHashMap : " + hashMap);

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (onedialog.isShowing()) {
					onedialog.dismiss();
				}

				if (hashMap == null) {
					Toast.makeText(CheckPoolFragmentActivity.this,
							getResources().getString(R.string.exceptionstring),
							Toast.LENGTH_LONG).show();
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							CheckPoolFragmentActivity.this);
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
							CheckPoolFragmentActivity.this, arrayList));

					button.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View view) {
							dialog.dismiss();

							String numberfareString = "";
							for (String key : hashMap.keySet()) {
								if (!key.equalsIgnoreCase("tripTotalFare")) {
									int index = MemberName.indexOf(key);
									if (index != -1) {
										numberfareString += (MemberNumber.get(
												index).toString()
												+ "~"
												+ String.format(
														"%d%n",
														Math.round(Double
																.parseDouble(hashMap
																		.get(key)
																		.toString()))) + ",");
									}
								}

								if (key.equalsIgnoreCase(rideDetailsModel
										.getOwnerName())) {
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
												rideDetailsModel.getCabId(),
												hashMap.get("tripTotalFare")
														.toString(),
												numberfareString,
												OwnerMobileNumber,
												OwnerMobileNumber);
							} else {
								new ConnectionTaskForSaveCalculatedFare()
										.execute(rideDetailsModel.getCabId(),
												hashMap.get("tripTotalFare")
														.toString(),
												numberfareString,
												OwnerMobileNumber,
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
				Toast.makeText(CheckPoolFragmentActivity.this,
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

			String authString = cabid + numberandfare + owner + paidby
					+ totalfare;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			nameValuePairList.add(CabIdValuePair);
			nameValuePairList.add(MemberNumberValuePair);
			nameValuePairList.add(NumberFairValuePair);
			nameValuePairList.add(PaidByValuePair);
			nameValuePairList.add(OwnerValuePair);
			nameValuePairList.add(authValuePair);

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

			saveCalculatedFare = result;

			Log.d("saveCalculatedFare", "saveCalculatedFare : " + result);

			if (saveCalculatedFare != null && saveCalculatedFare.length() > 0
					&& saveCalculatedFare.contains("Unauthorized Access")) {
				Log.e("CheckPoolFragmentActivity",
						"saveCalculatedFare Unauthorized Access");
				exceptioncheck = true;
				// Toast.makeText(CheckPoolFragmentActivity.this,
				// getResources().getString(R.string.exceptionstring),
				// Toast.LENGTH_LONG).show();
				return;
			}

			final JSONObject jsonObject = new JSONObject(result);
			if (jsonObject.get("status").toString().equalsIgnoreCase("fail")) {
				runOnUiThread(new Runnable() {
					public void run() {
						try {
							Toast.makeText(CheckPoolFragmentActivity.this,
									jsonObject.get("message").toString(),
									Toast.LENGTH_LONG).show();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		}
	}

	private class ConnectionTaskForMarkTripCompleted extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			Log.d("CheckPoolFragmentActivity",
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
				Toast.makeText(CheckPoolFragmentActivity.this,
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

				String authString = cabid + mobileNumber + owner;
				BasicNameValuePair authValuePair = new BasicNameValuePair(
						"auth",
						GlobalMethods.calculateCMCAuthString(authString));

				nameValuePairList.add(ownerNameValuePair);
				nameValuePairList.add(mobileNumberNameValuePair);
				nameValuePairList.add(authValuePair);
			} else {
				String authString = cabid;
				BasicNameValuePair authValuePair = new BasicNameValuePair(
						"auth",
						GlobalMethods.calculateCMCAuthString(authString));
				nameValuePairList.add(authValuePair);
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

			if (result != null && result.length() > 0
					&& result.contains("Unauthorized Access")) {
				Log.e("CheckPoolFragmentActivity",
						"tripCompleted Unauthorized Access");
				exceptioncheck = true;
				// Toast.makeText(CheckPoolFragmentActivity.this,
				// getResources().getString(R.string.exceptionstring),
				// Toast.LENGTH_LONG).show();
				return;
			}
		}
	}

	private void showContactsDialog() {

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
				AppConstants.HELVITICA));
		appFrends.setTypeface(Typeface.createFromAsset(getAssets(),
				AppConstants.HELVITICA));
		myclubbtn.setTypeface(Typeface.createFromAsset(getAssets(),
				AppConstants.HELVITICA));
		donebtn.setTypeface(Typeface.createFromAsset(getAssets(),
				AppConstants.HELVITICA));

		contactsbtn.setOnClickListener(new OnClickListener() {
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
						CheckPoolFragmentActivity.this,
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

		myclubbtn.setOnClickListener(new OnClickListener() {
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
					Toast.makeText(CheckPoolFragmentActivity.this,
							"No Groups Created Yet!!", Toast.LENGTH_LONG)
							.show();
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
								// Pawan cheks NoofMember value for null
								if (subArray.getJSONObject(i)
										.getString("NoofMembers").toString() == null)
									MyClubNoofMembers.add("1");

								else
									MyClubNoofMembers.add(subArray
											.getJSONObject(i)
											.getString("NoofMembers")
											.toString());

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
								// Pawan cheks NoofMember value for null
								if (subArray.getJSONObject(i)
										.getString("NoofMembers").toString()
										.equalsIgnoreCase("null"))
									MemberClubNoofMembers.add("1");

								else
									MemberClubNoofMembers.add(subArray
											.getJSONObject(i)
											.getString("NoofMembers")
											.toString());

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
									CheckPoolFragmentActivity.this,
									ClubListClass.ClubList, true);
							listMyclubs.setAdapter(adapter);
							listMyclubs
									.setOnItemClickListener(new OnItemClickListener() {

										@Override
										public void onItemClick(
												AdapterView<?> parent, View v,
												int position, long id) {
											// TODO Auto-generated method stub
											RadioButton chk = (RadioButton) v
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
									CheckPoolFragmentActivity.this,
									ClubListClass.MemberClubList, true);

							listMembersclubs.setAdapter(adapter);
							listMembersclubs
									.setOnItemClickListener(new OnItemClickListener() {

										@Override
										public void onItemClick(
												AdapterView<?> parent, View v,
												int position, long id) {
											// TODO Auto-generated method stub
											RadioButton chk = (RadioButton) v
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

		donebtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Animation animScale = AnimationUtils.loadAnimation(
						CheckPoolFragmentActivity.this,
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

							if (selectednumbers.indexOf(rideDetailsModel
									.getMobileNumber()) != -1) {
								selectednames.remove(selectednumbers
										.indexOf(rideDetailsModel
												.getMobileNumber()));
								selectednumbers.remove(selectednumbers
										.indexOf(rideDetailsModel
												.getMobileNumber()));
							}

						}

						if (selectednames.size() > 0) {

							// setnamesandnumbersintext(selectednames,
							// selectednumbers);

							shareLocation(true);

							dialog.dismiss();

						} else {
							Toast.makeText(CheckPoolFragmentActivity.this,
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
				Toast.makeText(CheckPoolFragmentActivity.this,
						"No groups created yet!", Toast.LENGTH_LONG).show();
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

							if (subArray.getJSONObject(i)
									.getString("NoofMembers").toString()
									.equalsIgnoreCase("null"))
								MyClubNoofMembers.add("1");
							else
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

							if (subArray.getJSONObject(i)
									.getString("NoofMembers").toString()
									.equalsIgnoreCase("null"))
								MemberClubNoofMembers.add("1");
							else
								MemberClubNoofMembers.add(subArray
										.getJSONObject(i)
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
								CheckPoolFragmentActivity.this,
								ClubListClass.ClubList, true);
						listMyclubs.setAdapter(adapter);
						listMyclubs
								.setOnItemClickListener(new OnItemClickListener() {

									@Override
									public void onItemClick(
											AdapterView<?> parent, View v,
											int position, long id) {
										// TODO Auto-generated method stub
										RadioButton chk = (RadioButton) v
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
								CheckPoolFragmentActivity.this,
								ClubListClass.MemberClubList, true);

						listMembersclubs.setAdapter(adapter);
						listMembersclubs
								.setOnItemClickListener(new OnItemClickListener() {

									@Override
									public void onItemClick(
											AdapterView<?> parent, View v,
											int position, long id) {
										// TODO Auto-generated method stub
										RadioButton chk = (RadioButton) v
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

			objAdapter = new ContactsAdapter(CheckPoolFragmentActivity.this,
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

	private void cancelMegaCab() {

		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					CheckPoolFragmentActivity.this);
			builder.setTitle("Internet Connection Error");
			builder.setMessage("iShareRyde requires Internet connection");
			builder.setPositiveButton("OK", null);
			AlertDialog dialog = builder.show();
			TextView messageText = (TextView) dialog
					.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
			dialog.show();

			return;
		}

		CancelMegaCabAsync cancelMegaCabAsync = new CancelMegaCabAsync();
		String authString = rideDetailsModel.getBookingRefNo()
				+ rideDetailsModel.getMobileNumber() + "CancelBooking";
		String param = "type=CancelBooking" + "&mobile="
				+ rideDetailsModel.getMobileNumber() + "&bookingNo="
				+ rideDetailsModel.getBookingRefNo() + "&auth="
				+ GlobalMethods.calculateCMCAuthString(authString);

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
				CheckPoolFragmentActivity.this);

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
						Toast.makeText(CheckPoolFragmentActivity.this,
								"Something went wrong, please try again",
								Toast.LENGTH_LONG).show();
					}
				});
			}

			if (result != null && result.length() > 0
					&& result.contains("Unauthorized Access")) {

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Log.e("CheckPoolFragmentActivity",
								"CancelMegaCabAsync Unauthorized Access");
						Toast.makeText(
								CheckPoolFragmentActivity.this,
								getResources().getString(
										R.string.exceptionstring),
								Toast.LENGTH_LONG).show();
					}
				});

				return "";
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
										CheckPoolFragmentActivity.this);
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
										CheckPoolFragmentActivity.this);
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
						Toast.makeText(CheckPoolFragmentActivity.this,
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
					CheckPoolFragmentActivity.this);
			builder.setTitle("Internet Connection Error");
			builder.setMessage("iShareRyde requires Internet connection");
			builder.setPositiveButton("OK", null);
			AlertDialog dialog = builder.show();
			TextView messageText = (TextView) dialog
					.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
			dialog.show();

			return;
		}

		CancelUberCabAsync cancelUberCabAsync = new CancelUberCabAsync();
		String param = ("bookingRefNo=" + rideDetailsModel.getBookingRefNo());

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			cancelUberCabAsync.executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, param);
		} else {
			cancelUberCabAsync.execute(param);
		}
	}

	public class CancelUberCabAsync extends AsyncTask<String, Void, String> {

		String result;

		private ProgressDialog dialog = new ProgressDialog(
				CheckPoolFragmentActivity.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			Log.d("CancelUberCabAsync",
					"CancelUberCabAsync : " + args[0].toString());

			try {
				URL url = new URL(GlobalVariables.ServiceUrl
						+ "/cancelUberBooking.php");
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
					Log.d("CancelUberCabAsync",
							"responseCode != HttpsURLConnection.HTTP_OK : "
									+ responseCode);
					result = response;
				}

				Log.d("CancelUberCabAsync", "CancelUberCabAsync response : "
						+ response);
				result = response;
			} catch (Exception e) {
				e.printStackTrace();
				result = "";
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(CheckPoolFragmentActivity.this,
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
										CheckPoolFragmentActivity.this);
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
						final String reason = jsonObject.get("message")
								.toString();

						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								AlertDialog.Builder builder = new AlertDialog.Builder(
										CheckPoolFragmentActivity.this);
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
						Toast.makeText(CheckPoolFragmentActivity.this,
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
					CheckPoolFragmentActivity.this);
			builder.setTitle("Internet Connection Error");
			builder.setMessage("iShareRyde requires Internet connection");
			builder.setPositiveButton("OK", null);
			AlertDialog dialog = builder.show();
			TextView messageText = (TextView) dialog
					.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
			dialog.show();

			return;
		}

		CancelTFSCabAsync cancelTFSCabAsync = new CancelTFSCabAsync();
		String authString = rideDetailsModel.getBookingRefNo() + "cancellation";
		String param = "type=cancellation" + "&booking_id="
				+ rideDetailsModel.getBookingRefNo()
				+ "&cancellation_reason=&auth="
				+ GlobalMethods.calculateCMCAuthString(authString);

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
				CheckPoolFragmentActivity.this);

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
						Toast.makeText(CheckPoolFragmentActivity.this,
								"Something went wrong, please try again",
								Toast.LENGTH_LONG).show();
					}
				});
			}

			if (result != null && result.length() > 0
					&& result.contains("Unauthorized Access")) {

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Log.e("CheckPoolFragmentActivity",
								"CancelTFSCabAsync Unauthorized Access");
						Toast.makeText(
								CheckPoolFragmentActivity.this,
								getResources().getString(
										R.string.exceptionstring),
								Toast.LENGTH_LONG).show();
					}
				});

				return "";
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
										CheckPoolFragmentActivity.this);
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
										CheckPoolFragmentActivity.this);
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
						Toast.makeText(CheckPoolFragmentActivity.this,
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

	private void cancelOlaCab() {

		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					CheckPoolFragmentActivity.this);
			builder.setTitle("Internet Connection Error");
			builder.setMessage("iShareRyde requires Internet connection");
			builder.setPositiveButton("OK", null);
			AlertDialog dialog = builder.show();
			TextView messageText = (TextView) dialog
					.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
			dialog.show();

			return;
		}

		CancelOlaCabAsync cancelOlaCabAsync = new CancelOlaCabAsync();
		String authString = rideDetailsModel.getBookingRefNo() + "cancellation";
		String param = "type=cancellation" + "&booking_id="
				+ rideDetailsModel.getBookingRefNo() + "&auth="
				+ GlobalMethods.calculateCMCAuthString(authString);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			cancelOlaCabAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
					param);
		} else {
			cancelOlaCabAsync.execute(param);
		}
	}

	public class CancelOlaCabAsync extends AsyncTask<String, Void, String> {

		String result;

		private ProgressDialog dialog = new ProgressDialog(
				CheckPoolFragmentActivity.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			Log.d("CancelOlaCabAsync",
					"CancelOlaCabAsync : " + args[0].toString());

			try {
				URL url = new URL(GlobalVariables.ServiceUrl + "/olaApi.php");
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
					Log.d("CancelOlaCabAsync",
							"responseCode != HttpsURLConnection.HTTP_OK : "
									+ responseCode);
					result = response;
				}

				Log.d("CancelOlaCabAsync", "CancelOlaCabAsync response : "
						+ response);
				result = response;
			} catch (Exception e) {
				e.printStackTrace();
				result = "";
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(CheckPoolFragmentActivity.this,
								"Something went wrong, please try again",
								Toast.LENGTH_LONG).show();
					}
				});
			}

			if (result.contains("Unauthorized Access")) {

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Log.e("MemberRideFragmentActivity",
								"CancelOlaCabAsync Unauthorized Access");
						Toast.makeText(
								CheckPoolFragmentActivity.this,
								getResources().getString(
										R.string.exceptionstring),
								Toast.LENGTH_LONG).show();
					}
				});

				return "";
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
										CheckPoolFragmentActivity.this);
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
						final String reason = jsonObject.get("reason")
								.toString();

						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								AlertDialog.Builder builder = new AlertDialog.Builder(
										CheckPoolFragmentActivity.this);
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
						Toast.makeText(CheckPoolFragmentActivity.this,
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

	private AlertDialog dialogseats;

	/**
	 */
	private void sendInvitesToFriends(final ArrayList<ContactData> contactList) {
		Handler mHandler2 = new Handler();
		Runnable mRunnable2 = new Runnable() {
			private String fromcome = "checkpool";

			@Override
			public void run() {

				selectednames.clear();
				selectednumbers.clear();
				HashMap<String, String> map = new HashMap<String, String>();
				for (ContactData bean : contactList) {
					// duplicacy check, my number check is left currently
					map.put(bean.getPhoneNumber().replace(" ", ""),
							bean.getName());
					L.mesaage(bean.getPhoneNumber().length() + "");
				}
				Iterator it = map.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pair = (Map.Entry) it.next();
					String number = String.valueOf(pair.getKey());
					int length = number.length();
					L.mesaage(length + "");
					it.remove(); // avoids a ConcurrentModificationException
					selectednames.add((String) pair.getValue());
					selectednumbers.add("0091"
							+ number.substring(number.length() - 10));

				}
				L.mesaage(selectednames.toString() + " , "
						+ selectednumbers.toString());

				// --------------------------------------------

				if (selectednames.size() > 0) {

					Log.d("selectednames", "" + selectednames);
					Log.d("selectednumbers", "" + selectednumbers);

					if (fromcome.equalsIgnoreCase("invite")) {

						tracker.send(new HitBuilders.EventBuilder()
								.setCategory("Invite").setAction("Invite")
								.setLabel("Invite").build());

						if (selectednames.size() >= Integer
								.parseInt(rideDetailsModel.getSeats())) {
							// conitnuechk = false;

							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
								new ConnectionTaskForSendInvite()
										.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
							} else {
								new ConnectionTaskForSendInvite().execute();
							}

						} else {

							// conitnuechk = true;

							AlertDialog.Builder builder = new AlertDialog.Builder(
									CheckPoolFragmentActivity.this);
							builder.setMessage("You have "
									+ rideDetailsModel.getSeats()
									+ " seats to share and have selected only "
									+ selectednames.size() + " friend(s)");
							builder.setCancelable(true);
							builder.setPositiveButton("Continue Anyways",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {

											if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
												new ConnectionTaskForSendInvite()
														.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
											} else {
												new ConnectionTaskForSendInvite()
														.execute();
											}
										}
									});
							dialogseats = builder.show();
							TextView messageText = (TextView) dialogseats
									.findViewById(android.R.id.message);
							messageText.setGravity(Gravity.CENTER);
							dialogseats.show();
						}

					}

					else if (fromcome.equalsIgnoreCase("joinpool")) {

						tracker.send(new HitBuilders.EventBuilder()
								.setCategory("Refer Friend (Ride)")
								.setAction("Refer Friend (Ride)")
								.setLabel("Refer Friend (Ride)").build());

						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							new ConnectionTaskForReferfriends()
									.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						} else {
							new ConnectionTaskForReferfriends().execute();
						}

					}

					else if (fromcome.equalsIgnoreCase("checkpool")) {

						tracker.send(new HitBuilders.EventBuilder()
								.setCategory("Invite").setAction("Invite")
								.setLabel("Invite").build());

						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							new ConnectionTaskForOwnerInviteFriends()
									.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						} else {
							new ConnectionTaskForOwnerInviteFriends().execute();
						}

					} else {
						Log.d("kahi se nahi", "kahi se nahi");
					}

				} else {
					Toast.makeText(CheckPoolFragmentActivity.this,
							"Please select Groups/Contacts to invite",
							Toast.LENGTH_LONG).show();
				}

			}
		};
		mHandler2.postDelayed(mRunnable2, 500);
	}

	private class ConnectionTaskForOwnerInviteFriends extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(
				CheckPoolFragmentActivity.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionOwnerInviteFriends mAuth1 = new AuthenticateConnectionOwnerInviteFriends();
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
				Toast.makeText(CheckPoolFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (ownerinviteres != null && ownerinviteres.length() > 0
					&& ownerinviteres.contains("Unauthorized Access")) {
				Log.e("CheckPoolFragmentActivity",
						"ownerinviteres Unauthorized Access");
				Toast.makeText(CheckPoolFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			} else {
				Toast.makeText(CheckPoolFragmentActivity.this,
						"Invite sent successfully!", Toast.LENGTH_LONG).show();
			}

			// CheckPoolFragmentActivity.this.finish();
		}

	}

	public class AuthenticateConnectionOwnerInviteFriends {

		public AuthenticateConnectionOwnerInviteFriends() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/ownerinvitefriends.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair CabIdBasicNameValuePair = new BasicNameValuePair(
					"CabId", rideDetailsModel.getCabId());
			BasicNameValuePair MembersNumberBasicNameValuePair = new BasicNameValuePair(
					"MembersNumber", selectednumbers.toString());
			BasicNameValuePair MembersNameBasicNameValuePair = new BasicNameValuePair(
					"MembersName", selectednames.toString());
			BasicNameValuePair OwnerNameBasicNameValuePair = new BasicNameValuePair(
					"OwnerName", rideDetailsModel.getOwnerName());
			BasicNameValuePair OwnerNumberBasicNameValuePair = new BasicNameValuePair(
					"OwnerNumber", OwnerMobileNumber);

			String authString = rideDetailsModel.getCabId()
					+ selectednames.toString() + selectednumbers.toString()
					+ rideDetailsModel.getOwnerName() + OwnerMobileNumber;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(CabIdBasicNameValuePair);
			nameValuePairList.add(MembersNumberBasicNameValuePair);
			nameValuePairList.add(MembersNameBasicNameValuePair);
			nameValuePairList.add(OwnerNameBasicNameValuePair);
			nameValuePairList.add(OwnerNumberBasicNameValuePair);
			nameValuePairList.add(authValuePair);

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
				ownerinviteres = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("ownerinviteres", "" + stringBuilder.toString());

		}
	}

	private class ConnectionTaskForReferfriends extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(
				CheckPoolFragmentActivity.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionReferfriends mAuth1 = new AuthenticateConnectionReferfriends();
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
				Toast.makeText(CheckPoolFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (ownerinviteres != null && ownerinviteres.length() > 0
					&& ownerinviteres.contains("Unauthorized Access")) {

				Log.e("CheckPoolFragmentActivity",
						"referfriendres Unauthorized Access");
				Toast.makeText(CheckPoolFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			CheckPoolFragmentActivity.this.finish();
		}

	}

	public class AuthenticateConnectionReferfriends {

		public AuthenticateConnectionReferfriends() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/referFriendRideStepOne.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair CabIdBasicNameValuePair = new BasicNameValuePair(
					"CabId", rideDetailsModel.getCabId());
			BasicNameValuePair MemberNameBasicNameValuePair = new BasicNameValuePair(
					"MemberName", FullName);
			BasicNameValuePair MemberNumberBasicNameValuePair = new BasicNameValuePair(
					"MemberNumber", OwnerMobileNumber);
			BasicNameValuePair ReferedUserNameBasicNameValuePair = new BasicNameValuePair(
					"ReferedUserName", selectednames.toString());
			BasicNameValuePair ReferedUserNumberBasicNameValuePair = new BasicNameValuePair(
					"ReferedUserNumber", selectednumbers.toString());

			String authString = rideDetailsModel.getCabId() + FullName
					+ OwnerMobileNumber + selectednames.toString()
					+ selectednumbers.toString();
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(CabIdBasicNameValuePair);
			nameValuePairList.add(MemberNameBasicNameValuePair);
			nameValuePairList.add(MemberNumberBasicNameValuePair);
			nameValuePairList.add(ReferedUserNameBasicNameValuePair);
			nameValuePairList.add(ReferedUserNumberBasicNameValuePair);
			nameValuePairList.add(authValuePair);

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
				referfriendres = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("referfriendres", "" + stringBuilder.toString());
		}
	}

	private class ConnectionTaskForSendInvite extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(
				CheckPoolFragmentActivity.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionSendInvite mAuth1 = new AuthenticateConnectionSendInvite();
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
				Toast.makeText(CheckPoolFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (ownerinviteres != null && ownerinviteres.length() > 0
					&& ownerinviteres.contains("Unauthorized Access")) {

				Log.e("CheckPoolFragmentActivity",
						"SendInvite Unauthorized Access");
				Toast.makeText(CheckPoolFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (flag == 0) {

				Intent mainIntent = new Intent(CheckPoolFragmentActivity.this,
						CheckPoolFragmentActivity.class);

				RideDetailsModel rideDetailsModel = new RideDetailsModel();
				rideDetailsModel.setCabId(rideDetailsModel.getCabId());
				rideDetailsModel.setMobileNumber(OwnerMobileNumber);
				rideDetailsModel.setOwnerName(rideDetailsModel.getOwnerName());
				SharedPreferences mPrefs111 = getSharedPreferences("userimage",
						0);
				String imgname = mPrefs111.getString("imgname", "");
				rideDetailsModel.setImagename(imgname);
				rideDetailsModel.setFromLocation(rideDetailsModel
						.getFromLocation());
				rideDetailsModel
						.setToLocation(rideDetailsModel.getToLocation());
				rideDetailsModel.setFromShortName(rideDetailsModel
						.getFromShortName());
				rideDetailsModel.setToShortName(rideDetailsModel
						.getToShortName());
				rideDetailsModel
						.setTravelDate(rideDetailsModel.getTravelDate());
				rideDetailsModel
						.setTravelTime(rideDetailsModel.getTravelTime());
				rideDetailsModel.setSeats(rideDetailsModel.getSeats());
				// Confusion of seats and remain seats are same ask later rohit
				rideDetailsModel.setRemainingSeats(rideDetailsModel.getSeats());
				rideDetailsModel.setSeat_Status("0/"
						+ rideDetailsModel.getSeats());
				rideDetailsModel.setDistance(distancetext);
				rideDetailsModel.setOpenTime("");
				rideDetailsModel.setCabStatus("A");
				mainIntent.putExtra("comefrom", "fromcontactsmyclub");
				rideDetailsModel.setBookingRefNo("");
				rideDetailsModel.setDriverName("");
				rideDetailsModel.setDriverNumber("");
				rideDetailsModel.setCarNumber("");
				rideDetailsModel.setCabName("");

				mainIntent.putExtra("RideDetailsModel",
						(new Gson()).toJson(rideDetailsModel));

				// mainIntent.putExtra("CabId", CabId);
				// mainIntent.putExtra("MobileNumber", OwnerMobileNumber);
				// mainIntent.putExtra("OwnerName", OwnerName);
				// SharedPreferences mPrefs111 =
				// getSharedPreferences("userimage",
				// 0);
				// String imgname = mPrefs111.getString("imgname", "");
				// mainIntent.putExtra("OwnerImage", imgname);
				// mainIntent.putExtra("FromLocation", FromLocation);
				// mainIntent.putExtra("ToLocation", ToLocation);
				//
				// mainIntent.putExtra("FromShortName", fromshortname);
				// mainIntent.putExtra("ToShortName", toshortname);
				//
				// mainIntent.putExtra("TravelDate", TravelDate);
				// mainIntent.putExtra("TravelTime", TravelTime);
				// mainIntent.putExtra("Seats", Seats);
				// mainIntent.putExtra("RemainingSeats", Seats);
				// mainIntent.putExtra("Seat_Status", "0/" + Seats);
				// mainIntent.putExtra("Distance", distancetext);
				// mainIntent.putExtra("OpenTime", "");
				// mainIntent.putExtra("CabStatus", "A");
				// mainIntent.putExtra("comefrom", "fromcontactsmyclub");
				//
				// mainIntent.putExtra("BookingRefNo", "");
				// mainIntent.putExtra("DriverName", "");
				// mainIntent.putExtra("DriverNumber", "");
				// mainIntent.putExtra("CarNumber", "");
				// mainIntent.putExtra("CabName", "");

				// mainIntent.putExtra("ExpTripDuration",
				// ExpTripDuration.get(arg2));

				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);

			} else {
				showAlertDialog(selectednames, selectednumbers);
			}
		}

	}

	public class AuthenticateConnectionSendInvite {

		public AuthenticateConnectionSendInvite() {

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

			String CompletePageResponse = new Communicator()
					.executeHttpGet(url);

			CompletePageResponse = CompletePageResponse
					.replaceAll("\\\\/", "/");

			JSONObject jsonObject = new JSONObject(CompletePageResponse);

			String name = jsonObject.getString("routes");

			JSONArray subArray = new JSONArray(name);

			String distancevalue = null;
			distancetext = null;

			String durationvalue = null;
			String durationtext = null;

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

					String startadd1 = subArray1.getJSONObject(i1)
							.getString("duration").toString();

					JSONObject jsonObject11 = new JSONObject(startadd1);
					durationvalue = jsonObject11.getString("value");
					durationtext = jsonObject11.getString("text");
				}
			}

			Log.d("distancevalue", "" + distancevalue);
			Log.d("distancetext", "" + distancetext);

			Log.d("durationvalue", "" + durationvalue);
			Log.d("durationtext", "" + durationtext);

			String msg = FullName + " invited you to share a cab from "
					+ fromshortname + " to " + toshortname;

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl + "/openacab.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair CabIdBasicNameValuePair = new BasicNameValuePair(
					"CabId", CabId);
			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
					"MobileNumber", OwnerMobileNumber);
			BasicNameValuePair OwnerNameBasicNameValuePair = new BasicNameValuePair(
					"OwnerName", OwnerName);
			BasicNameValuePair FromLocationBasicNameValuePair = new BasicNameValuePair(
					"FromLocation", FromLocation);
			BasicNameValuePair ToLocationBasicNameValuePair = new BasicNameValuePair(
					"ToLocation", ToLocation);

			BasicNameValuePair FromShortNameBasicNameValuePair;
			BasicNameValuePair ToShortNameBasicNameValuePair;

			if (fromshortname == null || fromshortname.equalsIgnoreCase("")
					|| fromshortname.isEmpty()) {

				FromShortNameBasicNameValuePair = new BasicNameValuePair(
						"FromShortName", FromLocation);
			} else {

				FromShortNameBasicNameValuePair = new BasicNameValuePair(
						"FromShortName", fromshortname);
			}

			if (toshortname == null || toshortname.equalsIgnoreCase("")
					|| toshortname.isEmpty()) {

				ToShortNameBasicNameValuePair = new BasicNameValuePair(
						"ToShortName", ToLocation);
			} else {

				ToShortNameBasicNameValuePair = new BasicNameValuePair(
						"ToShortName", toshortname);
			}

			BasicNameValuePair TravelDateBasicNameValuePair = new BasicNameValuePair(
					"TravelDate", TravelDate);
			BasicNameValuePair TravelTimeBasicNameValuePair = new BasicNameValuePair(
					"TravelTime", TravelTime);
			BasicNameValuePair SeatsBasicNameValuePair = new BasicNameValuePair(
					"Seats", Seats);
			BasicNameValuePair RemainingSeatsBasicNameValuePair = new BasicNameValuePair(
					"RemainingSeats", Seats);
			BasicNameValuePair DistanceBasicNameValuePair = new BasicNameValuePair(
					"Distance", distancetext);

			BasicNameValuePair durationvalueBasicNameValuePair = new BasicNameValuePair(
					"ExpTripDuration", durationvalue);

			BasicNameValuePair MembersNumberBasicNameValuePair = new BasicNameValuePair(
					"MembersNumber", selectednumbers.toString());
			BasicNameValuePair MembersNameBasicNameValuePair = new BasicNameValuePair(
					"MembersName", selectednames.toString());
			BasicNameValuePair MessageBasicNameValuePair = new BasicNameValuePair(
					"Message", msg);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(CabIdBasicNameValuePair);
			nameValuePairList.add(MobileNumberBasicNameValuePair);
			nameValuePairList.add(OwnerNameBasicNameValuePair);
			nameValuePairList.add(FromLocationBasicNameValuePair);
			nameValuePairList.add(ToLocationBasicNameValuePair);

			nameValuePairList.add(FromShortNameBasicNameValuePair);
			nameValuePairList.add(ToShortNameBasicNameValuePair);

			nameValuePairList.add(TravelDateBasicNameValuePair);
			nameValuePairList.add(TravelTimeBasicNameValuePair);
			nameValuePairList.add(SeatsBasicNameValuePair);
			nameValuePairList.add(RemainingSeatsBasicNameValuePair);
			nameValuePairList.add(DistanceBasicNameValuePair);
			nameValuePairList.add(durationvalueBasicNameValuePair);
			nameValuePairList.add(MembersNumberBasicNameValuePair);
			nameValuePairList.add(MembersNameBasicNameValuePair);
			nameValuePairList.add(MessageBasicNameValuePair);

			String authString = CabId
					+ distancetext
					+ durationvalue
					+ FromLocation
					+ ((fromshortname == null
							|| fromshortname.equalsIgnoreCase("") || fromshortname
								.isEmpty()) ? FromLocation : fromshortname)
					+ selectednames.toString()
					+ selectednumbers.toString()
					+ msg
					+ OwnerMobileNumber
					+ OwnerName
					+ Seats
					+ Seats
					+ ToLocation
					+ ((toshortname == null || toshortname.equalsIgnoreCase("") || toshortname
							.isEmpty()) ? ToLocation : toshortname)
					+ TravelDate + TravelTime;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			nameValuePairList.add(authValuePair);

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
				sendres = stringBuilder.append(bufferedStrChunk).toString();
			}

			Log.d("sendres", "" + stringBuilder.toString());
		}
	}

	private void showAlertDialog(final ArrayList<String> names,
			final ArrayList<String> numbers) {

		final Dialog dialog = new Dialog(this);
		dialog.setCanceledOnTouchOutside(false);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.addmemberstoclubpopup);

		final EditText yesclubname = (EditText) dialog
				.findViewById(R.id.yesclubname);
		yesclubname.setVisibility(View.GONE);

		final Button yesadd = (Button) dialog.findViewById(R.id.yesadd);
		yesadd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (yesclubname.getVisibility() == View.GONE) {
					yesclubname.setVisibility(View.VISIBLE);
				} else {

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new ConnectionTaskForNewClub().executeOnExecutor(
								AsyncTask.THREAD_POOL_EXECUTOR, FullName,
								OwnerMobileNumber, yesclubname.getText()
										.toString().trim(), names.toString(),
								numbers.toString());
					} else {
						new ConnectionTaskForNewClub().execute(FullName,
								OwnerMobileNumber, yesclubname.getText()
										.toString().trim(), names.toString(),
								numbers.toString());
					}
				}

			}
		});

		Button noadd = (Button) dialog.findViewById(R.id.noadd);
		noadd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent mainIntent = new Intent(CheckPoolFragmentActivity.this,
						ContactsToInviteActivity.class);

				RideDetailsModel rideDetailsModel = new RideDetailsModel();
				rideDetailsModel.setCabId(CabId);
				rideDetailsModel.setMobileNumber(OwnerMobileNumber);
				rideDetailsModel.setOwnerName(OwnerName);
				SharedPreferences mPrefs111 = getSharedPreferences("userimage",
						0);
				String imgname = mPrefs111.getString("imgname", "");
				rideDetailsModel.setImagename(imgname);
				rideDetailsModel.setFromLocation(FromLocation);
				rideDetailsModel.setToLocation(ToLocation);
				rideDetailsModel.setFromShortName(fromshortname);
				rideDetailsModel.setToShortName(toshortname);
				rideDetailsModel.setTravelDate(TravelDate);
				rideDetailsModel.setTravelTime(TravelTime);
				rideDetailsModel.setSeats(Seats);
				rideDetailsModel.setRemainingSeats(Seats);
				rideDetailsModel.setSeat_Status("0/" + Seats);
				rideDetailsModel.setDistance(distancetext);
				rideDetailsModel.setOpenTime("");
				rideDetailsModel.setCabStatus("A");
				mainIntent.putExtra("comefrom", "fromcontactsmyclub");
				rideDetailsModel.setBookingRefNo("");
				rideDetailsModel.setDriverName("");
				rideDetailsModel.setDriverNumber("");
				rideDetailsModel.setCarNumber("");
				rideDetailsModel.setCabName("");

				mainIntent.putExtra("RideDetailsModel",
						(new Gson()).toJson(rideDetailsModel));

				// mainIntent.putExtra("CabId", CabId);
				// mainIntent.putExtra("MobileNumber", OwnerMobileNumber);
				// mainIntent.putExtra("OwnerName", OwnerName);
				// SharedPreferences mPrefs111 =
				// getSharedPreferences("userimage",
				// 0);
				// String imgname = mPrefs111.getString("imgname", "");
				// mainIntent.putExtra("OwnerImage", imgname);
				// mainIntent.putExtra("FromLocation", FromLocation);
				// mainIntent.putExtra("ToLocation", ToLocation);
				//
				// mainIntent.putExtra("FromShortName", fromshortname);
				// mainIntent.putExtra("ToShortName", toshortname);
				//
				// mainIntent.putExtra("TravelDate", TravelDate);
				// mainIntent.putExtra("TravelTime", TravelTime);
				// mainIntent.putExtra("Seats", Seats);
				// mainIntent.putExtra("RemainingSeats", Seats);
				// mainIntent.putExtra("Seat_Status", "0/" + Seats);
				// mainIntent.putExtra("Distance", distancetext);
				// mainIntent.putExtra("OpenTime", "");
				// mainIntent.putExtra("CabStatus", "A");
				// mainIntent.putExtra("comefrom", "fromcontactsmyclub");
				//
				// mainIntent.putExtra("BookingRefNo", "");
				// mainIntent.putExtra("DriverName", "");
				// mainIntent.putExtra("DriverNumber", "");
				// mainIntent.putExtra("CarNumber", "");
				// mainIntent.putExtra("CabName", "");

				// mainIntent.putExtra("ExpTripDuration",
				// ExpTripDuration.get(arg2));

				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
		});

		dialog.show();
	}

	private class ConnectionTaskForNewClub extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionForNewClub mAuth1 = new AuthenticateConnectionForNewClub();
			try {
				mAuth1.fname = args[0];
				mAuth1.munum = args[1];
				mAuth1.cname = args[2];
				mAuth1.namesarr = args[3];
				mAuth1.numbersarr = args[4];
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
				Toast.makeText(CheckPoolFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (storeclubres.contains("Unauthorized Access")) {
				Log.e("CheckPoolFragmentActivity",
						"storeclubres Unauthorized Access");
				Toast.makeText(CheckPoolFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			Intent mainIntent = new Intent(CheckPoolFragmentActivity.this,
					CheckPoolFragmentActivity.class);

			RideDetailsModel rideDetailsModel = new RideDetailsModel();
			rideDetailsModel.setCabId(CabId);
			rideDetailsModel.setMobileNumber(OwnerMobileNumber);
			rideDetailsModel.setOwnerName(OwnerName);
			SharedPreferences mPrefs111 = getSharedPreferences("userimage", 0);
			String imgname = mPrefs111.getString("imgname", "");
			rideDetailsModel.setImagename(imgname);
			rideDetailsModel.setFromLocation(FromLocation);
			rideDetailsModel.setToLocation(ToLocation);
			rideDetailsModel.setFromShortName(fromshortname);
			rideDetailsModel.setToShortName(toshortname);
			rideDetailsModel.setTravelDate(TravelDate);
			rideDetailsModel.setTravelTime(TravelTime);
			rideDetailsModel.setSeats(Seats);
			rideDetailsModel.setRemainingSeats(Seats);
			rideDetailsModel.setSeat_Status("0/" + Seats);
			rideDetailsModel.setDistance(distancetext);
			rideDetailsModel.setOpenTime("");
			rideDetailsModel.setCabStatus("A");
			mainIntent.putExtra("comefrom", "fromcontactsmyclub");
			rideDetailsModel.setBookingRefNo("");
			rideDetailsModel.setDriverName("");
			rideDetailsModel.setDriverNumber("");
			rideDetailsModel.setCarNumber("");
			rideDetailsModel.setCabName("");

			mainIntent.putExtra("RideDetailsModel",
					(new Gson()).toJson(rideDetailsModel));

			// mainIntent.putExtra("CabId", CabId);
			// mainIntent.putExtra("MobileNumber", MobileNumberstr);
			// mainIntent.putExtra("OwnerName", OwnerName);
			// SharedPreferences mPrefs111 = getSharedPreferences("userimage",
			// 0);
			// String imgname = mPrefs111.getString("imgname", "");
			// mainIntent.putExtra("OwnerImage", imgname);
			// mainIntent.putExtra("FromLocation", FromLocation);
			// mainIntent.putExtra("ToLocation", ToLocation);
			//
			// mainIntent.putExtra("FromShortName", fromshortname);
			// mainIntent.putExtra("ToShortName", toshortname);
			//
			// mainIntent.putExtra("TravelDate", TravelDate);
			// mainIntent.putExtra("TravelTime", TravelTime);
			// mainIntent.putExtra("Seats", Seats);
			// mainIntent.putExtra("RemainingSeats", Seats);
			// mainIntent.putExtra("Seat_Status", "0/" + Seats);
			// mainIntent.putExtra("Distance", distancetext);
			// mainIntent.putExtra("OpenTime", "");
			// mainIntent.putExtra("CabStatus", "A");
			// mainIntent.putExtra("comefrom", "fromcontactsmyclub");
			//
			// mainIntent.putExtra("BookingRefNo", "");
			// mainIntent.putExtra("DriverName", "");
			// mainIntent.putExtra("DriverNumber", "");
			// mainIntent.putExtra("CarNumber", "");
			// mainIntent.putExtra("CabName", "");

			// mainIntent.putExtra("ExpTripDuration",
			// ExpTripDuration.get(arg2));

			startActivityForResult(mainIntent, 500);
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
		}

	}

	public class AuthenticateConnectionForNewClub {

		public String fname;
		public String munum;
		public String cname;
		public String namesarr;
		public String numbersarr;

		public AuthenticateConnectionForNewClub() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl + "/Store_Club.php";
			HttpPost httpPost = new HttpPost(url_select);

			BasicNameValuePair OwnerNameBasicNameValuePair = new BasicNameValuePair(
					"OwnerName", fname);
			BasicNameValuePair OwnerNumberBasicNameValuePair = new BasicNameValuePair(
					"OwnerNumber", munum);
			BasicNameValuePair ClubNameBasicNameValuePair = new BasicNameValuePair(
					"ClubName", cname);
			BasicNameValuePair ClubMembersNameBasicNameValuePair = new BasicNameValuePair(
					"ClubMembersName", namesarr);
			BasicNameValuePair ClubMembersNumberBasicNameValuePair = new BasicNameValuePair(
					"ClubMembersNumber", numbersarr);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(OwnerNameBasicNameValuePair);
			nameValuePairList.add(OwnerNumberBasicNameValuePair);
			nameValuePairList.add(ClubNameBasicNameValuePair);
			nameValuePairList.add(ClubMembersNameBasicNameValuePair);
			nameValuePairList.add(ClubMembersNumberBasicNameValuePair);

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
				storeclubres = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("storeclubres", "" + stringBuilder.toString());
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CHECK_POOL_FRAGMENT_ID) {
			if (resultCode == RESULT_OK) {
				if (data.getExtras().getBoolean("iscontactslected")) {
					Log.d("", "");
					ArrayList<ContactData> myList = data.getExtras()
							.getParcelableArrayList("Contact_list");
					if (myList != null && myList.size() > 0) {
						sendInvitesToFriends(myList);
					}
				}
			}
		}
	}



}
