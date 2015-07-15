package com.clubmycab;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.clubmycab.FareCalculator.FareCalculatorInterface;
import com.clubmycab.maps.MapUtilityMethods;
import com.clubmycab.model.AddressModel;
import com.clubmycab.ui.ContactsToInviteActivity;
import com.clubmycab.ui.HomeActivity;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

public class CheckPoolFragmentActivity extends FragmentActivity implements
		FareCalculatorInterface {

	String CompletePageResponse;

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

	LinearLayout ownermessage;
	LinearLayout ownerinvite;
	LinearLayout ownerbookacab;
	LinearLayout ownercancel;

	String ownercancelpoolresp;
	CircularImageView memimage;

	String dropuserfrompopupresp;

	String sendcustommessagefrompopupresp;

	ImageView mydetailbtn;
	ImageView mycalculatorbtn;
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

		GoogleAnalytics analytics = GoogleAnalytics
				.getInstance(CheckPoolFragmentActivity.this);
		tracker = analytics
				.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);

		// All subsequent hits will be send with screen name = "main screen"
		tracker.setScreenName("Owner Created Pool");

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

		comefrom = intent.getStringExtra("comefrom");
		Log.d("comefrom", "" + comefrom);

		Log.d("CabStatus", "" + CabStatus);

		checkpoolbottomtabsll = (LinearLayout) findViewById(R.id.checkpoolbottomtabsll);

		if (CabStatus.toString().trim().equalsIgnoreCase("A")) {
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
		mydetailbtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				joinPoolPopUp(FromShortName, ToShortName, TravelDate,
						TravelTime, Seat_Status, OwnerName, OwnerImage);

			}
		});

		mycalculatorbtn = (ImageView) findViewById(R.id.mycalculatorbtn);
		mycalculatorbtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						CheckPoolFragmentActivity.this);
				builder.setTitle("Fare Split");
				builder.setMessage("Please enter fare to split :");
				builder.setCancelable(false);
				final EditText input = new EditText(
						CheckPoolFragmentActivity.this);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.MATCH_PARENT);
				input.setLayoutParams(lp);
				input.setInputType(InputType.TYPE_CLASS_NUMBER
						| InputType.TYPE_NUMBER_FLAG_DECIMAL);
				builder.setView(input);
				builder.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								Double fare = 0.0;
								if (input.getText().toString().isEmpty()) {
									Toast.makeText(
											CheckPoolFragmentActivity.this,
											"Please enter a valid fare",
											Toast.LENGTH_LONG).show();
								} else if (!input.getText().toString()
										.isEmpty()) {
									fare = Double.parseDouble(input.getText()
											.toString());
									if (fare <= 0.0) {
										Toast.makeText(
												CheckPoolFragmentActivity.this,
												"Please enter a valid fare",
												Toast.LENGTH_LONG).show();
									} else {
										onedialog = new ProgressDialog(
												CheckPoolFragmentActivity.this);
										onedialog.setMessage("Please Wait...");
										onedialog.setCancelable(false);
										onedialog
												.setCanceledOnTouchOutside(false);
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
																	ShowMemberName
																			.get(i)
																			.toString());

													memberArrayList
															.add(memberJsonObject);
												}

												FareCalculator fareCalculator = new FareCalculator(
														CheckPoolFragmentActivity.this,
														ownerJsonObject,
														memberArrayList);
												fareCalculator
														.calculateFareSplit(fare);

											} catch (Exception e) {
												e.printStackTrace();
											}

										}
									}
								}
							}
						});
				builder.setNegativeButton("Cancel", null);
				AlertDialog dialog = builder.show();
				TextView messageText = (TextView) dialog
						.findViewById(android.R.id.message);
				messageText.setGravity(Gravity.CENTER);
				dialog.show();
			}
		});

		ownermessage.setOnClickListener(new View.OnClickListener() {

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

		ownerinvite.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (RemainingSeats.equalsIgnoreCase("0")) {
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
							ContactsToInviteActivity.class);
					mainIntent.putExtra("fromcome", "checkpool");
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

		ownerbookacab.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!BookingRefNo.isEmpty()
						&& !BookingRefNo.equalsIgnoreCase("null")) {
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
				} else {

					openBookCabPage();
				}
			}
		});

		ownercancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				cancelTrip();
			}
		});

		SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
		FullName = mPrefs.getString("FullName", "");
		MemberNumberstr = mPrefs.getString("MobileNumber", "");

		OwnerMobileNumber = MobileNumber;

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

			onedialog = new ProgressDialog(CheckPoolFragmentActivity.this);
			onedialog.setMessage("Please Wait...");
			onedialog.setCancelable(false);
			onedialog.setCanceledOnTouchOutside(false);
			onedialog.show();

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForDirections()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new ConnectionTaskForDirections().execute();
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

					if (phonesub.equalsIgnoreCase("9920981334")) {
						Log.d("phonesub", "" + phonesub);
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

		contexthelpcheckpool.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				contexthelpcheckpool.setVisibility(View.GONE);

				SharedPreferences sharedPreferences = getSharedPreferences(
						"ContextHelp", 0);
				SharedPreferences.Editor editor = sharedPreferences.edit();
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

		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"dd/MM/yyyy hh:mm aa");
			Date date = simpleDateFormat.parse(TravelDate + " " + TravelTime);

			// Log.d("CheckPoolFragmentActivity", "startTime : " +
			// date.getTime());

			if ((date.getTime() - System.currentTimeMillis()) <= (UpcomingStartTripAlarm.START_TRIP_NOTIFICATION_TIME * 60 * 1000)) {
				if (BookingRefNo.isEmpty()
						|| BookingRefNo.equalsIgnoreCase("null")) {
					showCabBookingDialog(true);
				} else {
					showTripStartDialog();
				}
			} else if ((date.getTime() - System.currentTimeMillis()) <= (UpcomingStartTripAlarm.UPCOMING_TRIP_NOTIFICATION_TIME * 60 * 1000)) {
				if (BookingRefNo.isEmpty()
						|| BookingRefNo.equalsIgnoreCase("null")) {
					showCabBookingDialog(false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showCabBookingDialog(final boolean shouldShowTripStartDialog) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				CheckPoolFragmentActivity.this);
		View builderView = (View) getLayoutInflater().inflate(
				R.layout.dialog_trip_start_book_cab, null);

		builder.setView(builderView);
		final AlertDialog dialog = builder.create();

		LinearLayout linearLayout = (LinearLayout) builderView
				.findViewById(R.id.tripstartbookcabll);
		linearLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				dialog.dismiss();

				openBookCabPage();
			}
		});

		linearLayout = (LinearLayout) builderView
				.findViewById(R.id.tripstartalreadybookedll);
		linearLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				dialog.dismiss();

				if (shouldShowTripStartDialog) {
					showTripStartDialog();
				}
			}
		});

		linearLayout = (LinearLayout) builderView
				.findViewById(R.id.tripstartowncarll);
		linearLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				dialog.dismiss();

				if (shouldShowTripStartDialog) {
					showTripStartDialog();
				}
			}
		});

		linearLayout = (LinearLayout) builderView
				.findViewById(R.id.tripstartcanceltripll);
		linearLayout.setOnClickListener(new View.OnClickListener() {

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
		linearLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				dialog.dismiss();

				tripStarted(0);
			}
		});

		linearLayout = (LinearLayout) builderView
				.findViewById(R.id.tripstartfivell);
		linearLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				dialog.dismiss();

				tripStarted(5);
			}
		});

		linearLayout = (LinearLayout) builderView
				.findViewById(R.id.tripstarttenll);
		linearLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				dialog.dismiss();

				tripStarted(10);
			}
		});

		dialog.show();
	}

	private void tripStarted(final int duration) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				CheckPoolFragmentActivity.this);
		builder.setMessage("Your trip member(s) will now receive your location updates, would you like to share your location with others?");
		builder.setCancelable(false);

		builder.setPositiveButton("Select receipients",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						showContactsDialog();
					}
				});

		builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});

		builder.show();
	}

	private void openBookCabPage() {
		String addressString = MapUtilityMethods.getAddress(
				CheckPoolFragmentActivity.this, startaddlatlng.get(0).latitude,
				startaddlatlng.get(0).longitude);
		Address address = geocodeAddress(addressString);

		AddressModel startAddressModel = new AddressModel();
		startAddressModel.setAddress(address);
		startAddressModel.setShortname(FromShortName);
		startAddressModel.setLongname(addressString);

		addressString = MapUtilityMethods.getAddress(
				CheckPoolFragmentActivity.this, endaddlatlng.get(0).latitude,
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

		final Intent mainIntent = new Intent(CheckPoolFragmentActivity.this,
				BookaCabFragmentActivity.class);
		Gson gson = new Gson();
		mainIntent.putExtra("StartAddressModel", gson.toJson(startAddressModel)
				.toString());
		mainIntent.putExtra("EndAddressModel", gson.toJson(endAddressModel)
				.toString());
		mainIntent.putExtra("CabId", CabId);
		// mainIntent.putExtra("FromShortName", FromShortName);
		// mainIntent.putExtra("ToShortName", ToShortName);
		mainIntent.putExtra("TravelDate", TravelDate);
		mainIntent.putExtra("TravelTime", TravelTime);
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

			checkpoolmap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.checkpoolmap)).getMap();

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
				Toast.makeText(CheckPoolFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (showmembersresp.equalsIgnoreCase("No Members joined yet")) {

			} else {

				mycalculatorbtn.setVisibility(View.VISIBLE);

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
					checkpoolmap
							.addMarker(new MarkerOptions()
									.position(lt)
									.snippet(String.valueOf(i))
									.title(ShowMemberLocationAddress.get(i))
									.icon(BitmapDescriptorFactory
											.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
				}

				checkpoolmap
						.setOnMarkerClickListener(new OnMarkerClickListener() {

							@Override
							public boolean onMarkerClick(Marker arg0) {

								if (arg0.getSnippet().equals("start")) {

								} else if (arg0.getSnippet().equals("end")) {

								} else {

									if (CabStatus.toString().trim()
											.equalsIgnoreCase("A")) {

										final Integer index = Integer
												.parseInt(arg0.getSnippet());

										showAlertDialog(ShowMemberName
												.get(index), ShowMemberNumber
												.get(index),
												ShowMemberLocationAddress
														.get(index),
												ShowMemberLocationLatLong
														.get(index),
												ShowMemberImageName.get(index),
												ShowMemberStatus.get(index));
									}
								}

								return true;
							}

						});

			}

			if (onedialog.isShowing()) {
				onedialog.dismiss();
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

			Log.d("showmembersresp", "" + stringBuilder.toString());
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

			Intent mainIntent = new Intent(CheckPoolFragmentActivity.this,
					HomeActivity.class);
			mainIntent.putExtra("from", "normal");
			mainIntent.putExtra("message", "null");
			mainIntent.putExtra("CabId", "null");
			mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(mainIntent);

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
					"CabId", CabId);
			BasicNameValuePair OwnerNameBasicNameValuePair = new BasicNameValuePair(
					"OwnerName", OwnerName);
			BasicNameValuePair OwnerNumberBasicNameValuePair = new BasicNameValuePair(
					"OwnerNumber", MobileNumber);
			BasicNameValuePair MessageBasicNameValuePair = new BasicNameValuePair(
					"Message", OwnerName + " cancelled the ride from "
							+ FromShortName + " to " + ToShortName);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(CabIdBasicNameValuePair);
			nameValuePairList.add(OwnerNameBasicNameValuePair);
			nameValuePairList.add(OwnerNumberBasicNameValuePair);
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
				ownercancelpoolresp = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("ownercancelpoolresp", "" + stringBuilder.toString());
		}
	}

	private void showAlertDialog(final String mname, final String mnum,
			String mlocadd, String mloclatlon, String mimgname, String mstatus) {

		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.memberdeatilspopup);

		memimage = (CircularImageView) dialog.findViewById(R.id.memimage);

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

		TextView memname = (TextView) dialog.findViewById(R.id.memname);
		memname.setText(mname.toUpperCase());
		dialog.show();

		TextView memlocationadd = (TextView) dialog
				.findViewById(R.id.memlocationadd);
		memlocationadd.setText(mlocadd);

		LinearLayout dropuser = (LinearLayout) dialog
				.findViewById(R.id.dropuser);
		dropuser.setOnClickListener(new View.OnClickListener() {

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

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForDirectionsnew()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new ConnectionTaskForDirectionsnew().execute();
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
					"CabId", CabId);
			BasicNameValuePair OwnerNameBasicNameValuePair = new BasicNameValuePair(
					"OwnerName", OwnerName);
			BasicNameValuePair OwnerNumberBasicNameValuePair = new BasicNameValuePair(
					"OwnerNumber", MobileNumber);
			BasicNameValuePair MemberNameBasicNameValuePair = new BasicNameValuePair(
					"MemberName", memname);
			BasicNameValuePair MemberNumberBasicNameValuePair = new BasicNameValuePair(
					"MemberNumber", memnum);
			BasicNameValuePair MessageBasicNameValuePair = new BasicNameValuePair(
					"Message", OwnerName
							+ " has removed you from the ride from "
							+ FromShortName + " to " + ToShortName);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(CabIdBasicNameValuePair);
			nameValuePairList.add(OwnerNameBasicNameValuePair);
			nameValuePairList.add(OwnerNumberBasicNameValuePair);
			nameValuePairList.add(MemberNameBasicNameValuePair);
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
				InputStream in = new java.net.URL(urldisplay).openStream();
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
		seatstext = (TextView) dialog.findViewById(R.id.seatstext);

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
		seatstext.setText(st.toString().trim() + " Seats");

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
		}

		dialog.show();

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

		Log.d("checkpool onStart", "checkpool onStart");
		if (xmppConnection != null) {
			Log.d("12", "connection already connected");

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
					AsyncTask.THREAD_POOL_EXECUTOR, CabId, MemberNumberstr,
					"offline");
		} else {
			new ConnectionTaskForupdatestatus().execute(CabId, MemberNumberstr,
					"offline");
		}

		super.onStop();
	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();

		if (comefrom != null) {

			if (!chatlayoutmainrl.isShown()) {
				Intent mainIntent = new Intent(CheckPoolFragmentActivity.this,
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
						+ CabId + GlobalVariables.ServerNameForChat;
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
				Toast.makeText(CheckPoolFragmentActivity.this,
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

					button.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View view) {
							dialog.dismiss();
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
					Toast.makeText(CheckPoolFragmentActivity.this,
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
									CheckPoolFragmentActivity.this,
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
									CheckPoolFragmentActivity.this,
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

							if (selectednumbers.indexOf(MobileNumber) != -1) {
								selectednames.remove(selectednumbers
										.indexOf(MobileNumber));
								selectednumbers.remove(selectednumbers
										.indexOf(MobileNumber));
							}

						}

						if (selectednames.size() > 0) {

							// setnamesandnumbersintext(selectednames,
							// selectednumbers);
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
								CheckPoolFragmentActivity.this,
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
								CheckPoolFragmentActivity.this,
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

	// private void setnamesandnumbersintext(ArrayList<String> names,
	// ArrayList<String> numbers) {
	//
	// String str = "";
	//
	// for (int i = 0; i < numbers.size(); i++) {
	//
	// if (names.get(i).toString().trim() == null
	// || names.get(i).toString().trim().equalsIgnoreCase("null")) {
	// str = str + numbers.get(i) + "\n";
	// } else {
	// str = str + names.get(i) + "\n";
	// }
	// }
	//
	// str = str.substring(0, str.length() - 1);
	//
	// Log.d("str", "" + str);
	// selectrecipientsvalue.setText(str);
	//
	// }
}
