package com.clubmycab;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
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
import com.clubmycab.model.ContactData;
import com.clubmycab.model.GroupDataModel;
import com.clubmycab.model.MemberModel;
import com.clubmycab.ui.ContactsInviteForRideActivity;
import com.clubmycab.ui.FavoritePlaceFindActivity;
import com.clubmycab.ui.NewHomeScreen;
import com.clubmycab.ui.NotificationListActivity;
import com.clubmycab.ui.SendInvitesToOtherScreen;
import com.clubmycab.utility.AppConstants;
import com.clubmycab.utility.CustomDialog;
import com.clubmycab.utility.FontTypeface;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.L;
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

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class ShareLocationFragmentActivity extends FragmentActivity implements
		LocationListener {

	private ImageView notificationimg;
	private ImageView poolinfoimg;
	private String FullName;
	private String MobileNumber;
	private LocationManager locationManager;
	private RelativeLayout selectrecprll;
	private RelativeLayout watchmeforrll;
	private TextView selectrecipientsvalue;
	private TextView watchmeforvalue;
	private AutoCompleteTextView from_places;
	private Button threedotsfrom;
	private Button shartsharing;
	private Button stopsharing;
	private Boolean chk;
	private RelativeLayout unreadnoticountrl;
	private TextView unreadnoticount;
	private Button contactsbtn;
	private Button appFrends;
	private Button myclubbtn;
	private EditText searchfromlist;
	private ContactsAdapter objAdapter;
	private LinearLayout clubcontactslistll;
	private ListView contactslist;
	private LinearLayout mainclublistll;
	private ListView listMyclubs;
	private ListView listMembersclubs;
	private int flag = 1;
	private Button donebtn;
	private TextView validmobiletxt1;
	private ArrayList<String> selectednames = new ArrayList<String>();
	private ArrayList<String> selectednumbers = new ArrayList<String>();
	private Boolean clubcreated;
	private Bitmap mIcon11;
	private String sharechk;
	private RelativeLayout fromrelative;
	private TextView fromlocation;
	private Button fromdone;
	private Button cancel;
	private GoogleMap frommap;
	private String imagenameresp;
	private RelativeLayout sharelocationrl;
	private LinearLayout homeofficellvalues;
	private ImageView homeimg;
	private ImageView officeimg;
	private Address fAddress;
	private Tracker tracker;
	private ImageView clearedittextimg;
	private boolean exceptioncheck = false;
	private Location mycurrentlocationobject;
	public static final int INVITE_FRIEND_REQUEST = 500;
	private ArrayList<String> type = new ArrayList<String>();
	private ArrayList<String> Latitude = new ArrayList<String>();
	private ArrayList<String> Longitude = new ArrayList<String>();
	private ArrayList<String> Address = new ArrayList<String>();
	private ArrayList<String> Locality = new ArrayList<String>();
	private ArrayList<String> ShortAddress = new ArrayList<String>();
	private ArrayList<String> ClubName = new ArrayList<String>();
	private ArrayList<String> NewClub = new ArrayList<String>();
	private ArrayList<String> ClubMember = new ArrayList<String>();
	private ArrayList<String> MembersNumber = new ArrayList<String>();
	private ArrayList<String> namearray = new ArrayList<String>();
	private ArrayList<String> phonenoarray = new ArrayList<String>();
	private ArrayList<String> imagearray = new ArrayList<String>();
	private ArrayList<String> namearraynew = new ArrayList<String>();
	private ArrayList<String> phonenoarraynew = new ArrayList<String>();
	private ArrayList<String> imagearraynew = new ArrayList<String>();
	private ArrayList<String> AppUsersfullnamearr = new ArrayList<String>();
	private ArrayList<String> AppUsersmobilenumberarr = new ArrayList<String>();
	private ArrayList<String> AppUsersimagearr = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_location);
		GoogleAnalytics analytics = GoogleAnalytics
				.getInstance(ShareLocationFragmentActivity.this);
		tracker = analytics
				.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);

		// All subsequent hits will be send with screen name = "main screen"
		tracker.setScreenName("Share Location");

		findViewById(R.id.flInfo).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tracker.send(new HitBuilders.EventBuilder()
						.setCategory("Share my location info")
						.setAction("Share my location info")
						.setLabel("Share my location info").build());
				CustomDialog.showDialog(ShareLocationFragmentActivity.this,"You can invite friends and family members to track your ride. Press the \"STOP\" button when you want to stop tracking");
			}
		});
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
		((TextView)findViewById(R.id.selectrecipientsvalue)).setTypeface(FontTypeface.getTypeface(ShareLocationFragmentActivity.this, AppConstants.HELVITICA));
		((Button)findViewById(R.id.shartsharing)).setTypeface(FontTypeface.getTypeface(ShareLocationFragmentActivity.this, AppConstants.HELVITICA));
		((Button)findViewById(R.id.stopsharing)).setTypeface(FontTypeface.getTypeface(ShareLocationFragmentActivity.this, AppConstants.HELVITICA));


		sharelocationrl = (RelativeLayout) findViewById(R.id.sharelocationrl);
		sharelocationrl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Log.d("sharelocationrl", "sharelocationrl");
			}
		});
		notificationimg = (ImageView) findViewById(R.id.notificationimg);
		poolinfoimg = (ImageView) findViewById(R.id.poolinfoimg);
		SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
		FullName = mPrefs.getString("FullName", "");
		MobileNumber = mPrefs.getString("MobileNumber", "");
		findViewById(R.id.flNotifications).setOnClickListener(new OnClickListener() {

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
				AppConstants.HELVITICA));
		fromdone.setTypeface(Typeface.createFromAsset(getAssets(),
				AppConstants.HELVITICA));

		cancel = (Button) findViewById(R.id.cancel);
		cancel.setTypeface(Typeface.createFromAsset(getAssets(),
				AppConstants.HELVITICA));

		unreadnoticountrl = (RelativeLayout) findViewById(R.id.unreadnoticountrl);
		unreadnoticount = (TextView) findViewById(R.id.unreadnoticount);

		if (!TextUtils.isEmpty(GlobalVariables.UnreadNotificationCount) && GlobalVariables.UnreadNotificationCount.equalsIgnoreCase("0")) {

			unreadnoticountrl.setVisibility(View.GONE);

		} else {

			unreadnoticountrl.setVisibility(View.VISIBLE);
			unreadnoticount.setText(GlobalVariables.UnreadNotificationCount);
		}

		selectrecprll = (RelativeLayout) findViewById(R.id.selectrecprll);
		selectrecprll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent mainIntent = new Intent(
						ShareLocationFragmentActivity.this,
						SendInvitesToOtherScreen.class);
				mainIntent.putExtra("activity_id",
						SendInvitesToOtherScreen.INVITE_FRAGMENT_ACTIVTY_ID);
				startActivityForResult(mainIntent, 500);

				// showAlertDialog();

			}
		});

		watchmeforrll = (RelativeLayout) findViewById(R.id.watchmeforrll);
		watchmeforrll.setOnClickListener(new OnClickListener() {

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
		from_places = (AutoCompleteTextView) findViewById(R.id.from_places1);
		clearedittextimg = (ImageView) findViewById(R.id.clearedittextimg);
		clearedittextimg.setVisibility(View.GONE);

		threedotsfrom = (Button) findViewById(R.id.threedotsfrom);

		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.list_item);
		adapter.setNotifyOnChange(true);

		from_places.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// isCallresetIntentParams = true;
				watchmeforvalue.setText("Select Time");
				Intent intent = new Intent(ShareLocationFragmentActivity.this,
						FavoritePlaceFindActivity.class);

				startActivityForResult(intent, 5);
			}
		});
		((TextView)findViewById(R.id.tvHeading)).setTypeface(FontTypeface.getTypeface(ShareLocationFragmentActivity.this, AppConstants.HELVITICA));
		((TextView)findViewById(R.id.tvHeading)).setText("Share My Location");
		findViewById(R.id.flBackArrow).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent mainIntent = new Intent(ShareLocationFragmentActivity.this,
						NewHomeScreen.class);
				mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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

		threedotsfrom.setOnClickListener(new OnClickListener() {

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

			String action = getIntent().getAction();
			if (action != null && action == "CLOSE_ACTION") {
				Log.d("onCreate", "action CLOSE_ACTION : " + NOTIFICATION_ID);
				// to be stopped from onResume(), not here otherwise 2 calls to
				// stop background service are generated resulting in crash
			} else {
				stopService(new Intent(this, LocationShareService.class));

				setnamesandnumbersintext(obj.recipientsnames,
						obj.recipientsnumbers);

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
			}

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

		shartsharing.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				tracker.send(new HitBuilders.EventBuilder()
						.setCategory("Share my location start")
						.setAction("Share my location start")
						.setLabel("Share my location start").build());

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

									// Toast.makeText(getBaseContext(),
									// "Please Select Destination",
									// Toast.LENGTH_SHORT).show();

									sharemylocationmethod("destination");

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
															Editor editor1 = sharedPreferences1
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
															Editor editor1 = sharedPreferences1
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
															Editor editor1 = sharedPreferences1
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
															Editor editor1 = sharedPreferences1
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
															Editor editor1 = sharedPreferences1
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
															Editor editor1 = sharedPreferences1
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
															Editor editor1 = sharedPreferences1
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
															Editor editor1 = sharedPreferences1
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

				SharedPreferences sharedPreferences = getSharedPreferences(
						"routeId", 0);
				Editor editor = sharedPreferences.edit();
				editor.putString("routeId", "");
				editor.commit();

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

				NotificationManager notificationManager = (NotificationManager) ShareLocationFragmentActivity.this
						.getSystemService(Context.NOTIFICATION_SERVICE);
				notificationManager.cancel(NOTIFICATION_ID);

			}
		});

		frommap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.frommap)).getMap();

		frommap.setMyLocationEnabled(true);

		frommap.setOnCameraChangeListener(new OnCameraChangeListener() {

			@Override
			public void onCameraChange(CameraPosition cameraPosition) {

				LatLng mapcenter = cameraPosition.target;

				String address = MapUtilityMethods.getAddress(
						ShareLocationFragmentActivity.this, mapcenter.latitude,
						mapcenter.longitude);
				Log.d("address", "" + address);

				fromlocation.setText(address);

			}
		});

		fromdone.setOnClickListener(new OnClickListener() {

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

		cancel.setOnClickListener(new OnClickListener() {

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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {

			if (requestCode == 5) {

				String value = (String) data.getExtras().getString("address");

				Log.d("from_place:::", value);

				// from_places.append(value);
				from_places.setText(value);

				if (value.equalsIgnoreCase("") || value.isEmpty()) {

				} else {
					fAddress = null; // reset previous

					fAddress = geocodeAddress(from_places.getText().toString());
					if (fAddress == null) {
						Toast.makeText(
								ShareLocationFragmentActivity.this,
								"Could not locate the address, please try using the map or a different address",
								Toast.LENGTH_LONG).show();
					} else {
						// fAddress = null; // reset previous
						InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						in.hideSoftInputFromWindow(
								from_places.getApplicationWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);

						// fAddress = geocodeAddress(from_places.getText()
						// .toString());
					}
				}

			} else if (requestCode == INVITE_FRIEND_REQUEST) {
				// Make sure the request was successful
				if (resultCode == RESULT_OK) {
					if (data.getExtras().getBoolean("iscontactslected")) {
						Log.d("", "");
						ArrayList<ContactData> myList = data.getExtras()
								.getParcelableArrayList("Contact_list");
						if (myList != null && myList.size() > 0) {
							sendInviteRequest(
									data.getExtras().getBoolean(
											"iscontactslected"), myList, null);
						}
					} else {
						L.mesaage("");
						ArrayList<GroupDataModel> myList = data.getExtras()
								.getParcelableArrayList("Group_list");
						if (myList != null && myList.size() > 0) {
							sendInviteRequest(
									data.getExtras().getBoolean(
											"iscontactslected"), null, myList);
						}

					}

				}
			}
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
	// Toast.makeText(ShareLocationFragmentActivity.this,
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
	//
	// }
	// }

	private void sendInviteRequest(final boolean isGrpFrmContact,
			final ArrayList<ContactData> contactList,
			final ArrayList<GroupDataModel> groupList) {
		Handler mHandler2 = new Handler();
		Runnable mRunnable2 = new Runnable() {
			@Override
			public void run() {
				selectednames.clear();
				selectednumbers.clear();
				if (isGrpFrmContact) {
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
				} else {
					HashMap<String, String> map = new HashMap<String, String>();
					for (GroupDataModel bean : groupList) {
						if (!bean.getOwnerNumber().equals(MobileNumber)) {
							map.put(bean.getOwnerNumber(), bean.getOwnerName());
						}
						if (bean.getMemberList() != null) {
							ArrayList<MemberModel> subArray = bean
									.getMemberList();
							for (int i = 0; i < subArray.size(); i++) {
								if (!subArray.get(i).getMemberNumber()
										.equals(MobileNumber)) {
									map.put(subArray.get(i).getMemberNumber(),
											subArray.get(i).getMemberName());
								}
							}
						}
					}
					Iterator it = map.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry pair = (Map.Entry) it.next();
						System.out.println(pair.getKey() + " = "
								+ pair.getValue());
						it.remove(); // avoids a ConcurrentModificationException
						selectednames.add((String) pair.getValue());
						selectednumbers.add(((String) pair.getKey()));

					}
					L.mesaage(selectednames.toString() + " , "
							+ selectednumbers.toString());

				}

				if (selectednames.size() > 0) {

					/*
					 * setnamesandnumbersintext(selectednames, selectednumbers);
					 */

					// dialog.dismiss();
					String str = "";
					if (isGrpFrmContact) {
						for (int i = 0; i < selectednames.size(); i++) {

							if (selectednames.get(i).toString().trim() == null
									|| selectednames.get(i).toString().trim()
											.equalsIgnoreCase("null")) {
								str = str + selectednames.get(i) + "\n";
							} else {
								str = str + selectednames.get(i) + "\n";
							}
						}
						str = str.substring(0, str.length() - 1);
						Log.d("str", "" + str);
						selectrecipientsvalue.setText(str);
					} else {
						for (int i = 0; i < groupList.size(); i++) {

							if (!TextUtils.isEmpty(groupList.get(i)
									.getPoolName())) {
								str = str + groupList.get(i).getPoolName()
										+ "\n";
							}
						}
						str = str.substring(0, str.length() - 1);
						Log.d("str", "" + str);
						selectrecipientsvalue.setText(str);
					}
				} else {
					Toast.makeText(ShareLocationFragmentActivity.this,
							"Please select contact(s)", Toast.LENGTH_LONG)
							.show();
				}

			}
		};
		mHandler2.postDelayed(mRunnable2, 500);

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

			if (from_places.getText().toString().trim().length() > 0) {
				myObject.destinationlongname = from_places.getText().toString()
						.trim();
				LatLng invitemapcenter = new LatLng(fAddress.getLatitude(),
						fAddress.getLongitude());
				myObject.destinationlatlong = invitemapcenter;
			} else {
				myObject.destinationlongname = "";
				// LatLng invitemapcenter = new LatLng(fAddress.getLatitude(),
				// fAddress.getLongitude());
				myObject.destinationlatlong = null;
			}

			myObject.destinationtimevalue = System.currentTimeMillis()
					+ (60000 * 2400);

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

		generatePersistentNotification();

		SharedPreferences sharedPreferences = getSharedPreferences("routeId", 0);
		Editor editor = sharedPreferences.edit();
		editor.putString("routeId", "");
		editor.commit();
	}

	// static int notificationID = 1;
	final int NOTIFICATION_ID = 111;

	private void generatePersistentNotification() {
		int icon = R.drawable.cabappicon;
		// notificationID++;

		String message = "You are sharing your location";

		Intent intent = new Intent();
		intent = new Intent(ShareLocationFragmentActivity.this,
				ShareLocationFragmentActivity.class);

		PendingIntent pIntent = PendingIntent.getActivity(
				ShareLocationFragmentActivity.this, NOTIFICATION_ID, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		PendingIntent pendingCloseIntent = PendingIntent.getActivity(
				this,
				0,
				new Intent(this, ShareLocationFragmentActivity.class).setFlags(
						Intent.FLAG_ACTIVITY_CLEAR_TOP
								| Intent.FLAG_ACTIVITY_SINGLE_TOP).setAction(
						"CLOSE_ACTION"), 0);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				ShareLocationFragmentActivity.this);
		Notification notification = mBuilder
				.setSmallIcon(icon)
				.setTicker("iShareRyde")
				.setWhen(System.currentTimeMillis())
				.setOngoing(true)
				.addAction(android.R.drawable.ic_menu_close_clear_cancel,
						"Stop sharing", pendingCloseIntent)
				.setContentTitle("iShareRyde")
				.setStyle(
						new NotificationCompat.BigTextStyle().bigText(message))
				.setContentIntent(pIntent)
				.setSound(
						RingtoneManager
								.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
				.setContentText(message).build();

		NotificationManager notificationManager = (NotificationManager) ShareLocationFragmentActivity.this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(NOTIFICATION_ID, notification);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		String action = intent.getAction();
		if (action == null) {
			return;
		}
		Log.d("onNewIntent", "action CLOSE_ACTION : " + NOTIFICATION_ID);
		if (action == "CLOSE_ACTION") {
			NotificationManager notificationManager = (NotificationManager) ShareLocationFragmentActivity.this
					.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.cancel(NOTIFICATION_ID);

			stopsharing.performClick();
		}
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

		contactsbtn = (Button) dialog.findViewById(R.id.contactsbtn1);
		appFrends = (Button) dialog.findViewById(R.id.appFrends1);
		myclubbtn = (Button) dialog.findViewById(R.id.myclubbtn1);
		donebtn = (Button) dialog.findViewById(R.id.donebtn);
		validmobiletxt1 = (TextView) dialog.findViewById(R.id.validmobiletxt1);

		clubcontactslistll = (LinearLayout) dialog
				.findViewById(R.id.clubcontactslistll1);
		contactslist = (ListView) dialog.findViewById(R.id.contactslist1);

		mainclublistll = (LinearLayout) dialog
				.findViewById(R.id.mainclublistll1);
		listMyclubs = (ListView) dialog.findViewById(R.id.listMyclubs1);
		listMembersclubs = (ListView) dialog
				.findViewById(R.id.listMembersclubs1);

		searchfromlist = (EditText) dialog.findViewById(R.id.searchfromlist1);

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

				validmobiletxt1
						.setText("(Please select contacts with valid Indian mobile numbers)");

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

		myclubbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				flag = 0;
				validmobiletxt1.setText("(Select a club from below)");
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

								if (subArray.getJSONObject(i)
										.getString("NoofMembers").toString()
										.equalsIgnoreCase("null"))
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
						}

						// ClubsAdaptor adapter = new ClubsAdaptor(
						// ShareLocationFragmentActivity.this,
						// ClubListClass.ClubList);
						// listMyclubs.setAdapter(adapter);
						// listMyclubs
						// .setOnItemClickListener(new OnItemClickListener() {
						//
						// @Override
						// public void onItemClick(
						// AdapterView<?> parent, View v,
						// int position, long id) {
						// // TODO Auto-generated method stub
						// CheckBox chk = (CheckBox) v
						// .findViewById(R.id.myclubcheckBox);
						// ClubObject bean = ClubListClass.ClubList
						// .get(position);
						//
						// if (bean.isSelected()) {
						// bean.setSelected(false);
						// chk.setChecked(false);
						// } else {
						// bean.setSelected(true);
						// chk.setChecked(true);
						// }
						//
						// }
						// });

						// Pawan
						ContactsInviteForRideActivity.adapterClubMy = new ClubsAdaptor(
								ShareLocationFragmentActivity.this,
								ClubListClass.ClubList, false);
						listMyclubs
								.setAdapter(ContactsInviteForRideActivity.adapterClubMy);
						// listMyclubs.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
						listMyclubs
								.setOnItemClickListener(new OnItemClickListener() {

									@Override
									public void onItemClick(
											AdapterView<?> parent, View v,
											int position, long id) {
										// TODO Auto-generated method stub

										ClubObject bean = ClubListClass.ClubList
												.get(position);
										bean.setSelected(true);

										for (int i = 0; i < ClubListClass.ClubList
												.size(); i++) {

											if (i == position)
												continue;

											bean = ClubListClass.ClubList
													.get(i);
											bean.setSelected(false);
										}
										// Remove all memberclib list selection
										for (int i = 0; i < ClubListClass.MemberClubList
												.size(); i++) {

											bean = ClubListClass.MemberClubList
													.get(i);
											bean.setSelected(false);
										}

										ContactsInviteForRideActivity.adapterClubMember
												.setSelectedIndex(-1);
										ContactsInviteForRideActivity.adapterClubMember
												.notifyDataSetChanged();

										// if (bean.isSelected()) {
										// bean.setSelected(false);
										// chk.setChecked(false);
										// } else {
										// bean.setSelected(true);
										// chk.setChecked(true);
										// }
										ContactsInviteForRideActivity.adapterClubMy
												.setSelectedIndex(position);
										ContactsInviteForRideActivity.adapterClubMy
												.notifyDataSetChanged();

									}
								});

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

							// ClubsAdaptor adapter = new ClubsAdaptor(
							// ShareLocationFragmentActivity.this,
							// ClubListClass.MemberClubList);
							//
							// listMembersclubs.setAdapter(adapter);
							// listMembersclubs
							// .setOnItemClickListener(new OnItemClickListener()
							// {
							//
							// @Override
							// public void onItemClick(
							// AdapterView<?> parent, View v,
							// int position, long id) {
							// // TODO Auto-generated method stub
							// CheckBox chk = (CheckBox) v
							// .findViewById(R.id.myclubcheckBox);
							// ClubObject bean = ClubListClass.MemberClubList
							// .get(position);
							//
							// if (bean.isSelected()) {
							// bean.setSelected(false);
							// chk.setChecked(false);
							// } else {
							// bean.setSelected(true);
							// chk.setChecked(true);
							// }
							//
							// }
							// });

							ContactsInviteForRideActivity.adapterClubMember = new ClubMemberAdapter(
									ShareLocationFragmentActivity.this,
									ClubListClass.MemberClubList, false);

							listMembersclubs
									.setAdapter(ContactsInviteForRideActivity.adapterClubMember);
							listMembersclubs
									.setOnItemClickListener(new OnItemClickListener() {

										@Override
										public void onItemClick(
												AdapterView<?> parent, View v,
												int position, long id) {
											// TODO Auto-generated method stub

											ClubObject bean = ClubListClass.MemberClubList
													.get(position);
											bean.setSelected(true);

											for (int i = 0; i < ClubListClass.MemberClubList
													.size(); i++) {

												if (i == position)
													continue;

												bean = ClubListClass.MemberClubList
														.get(i);
												bean.setSelected(false);
											}

											// Unselect all MyClub
											for (int i = 0; i < ClubListClass.ClubList
													.size(); i++) {

												bean = ClubListClass.ClubList
														.get(i);
												bean.setSelected(false);

											}

											ContactsInviteForRideActivity.adapterClubMy
													.setSelectedIndex(-1);
											ContactsInviteForRideActivity.adapterClubMy
													.notifyDataSetChanged();
											// if (bean.isSelected()) {
											// bean.setSelected(false);
											// chk.setChecked(false);
											// } else {
											// bean.setSelected(true);
											// chk.setChecked(true);
											// }

											ContactsInviteForRideActivity.adapterClubMember
													.setSelectedIndex(position);
											ContactsInviteForRideActivity.adapterClubMember
													.notifyDataSetChanged();

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

						// ClubsAdaptor adapter = new ClubsAdaptor(
						// ShareLocationFragmentActivity.this,
						// ClubListClass.ClubList);
						// listMyclubs.setAdapter(adapter);
						// listMyclubs
						// .setOnItemClickListener(new OnItemClickListener() {
						//
						// @Override
						// public void onItemClick(
						// AdapterView<?> parent, View v,
						// int position, long id) {
						// // TODO Auto-generated method stub
						// CheckBox chk = (CheckBox) v
						// .findViewById(R.id.myclubcheckBox);
						// ClubObject bean = ClubListClass.ClubList
						// .get(position);
						//
						// if (bean.isSelected()) {
						// bean.setSelected(false);
						// chk.setChecked(false);
						// } else {
						// bean.setSelected(true);
						// chk.setChecked(true);
						// }
						//
						// }
						// });

						// Pawan
						ContactsInviteForRideActivity.adapterClubMy = new ClubsAdaptor(
								ShareLocationFragmentActivity.this,
								ClubListClass.ClubList, false);
						listMyclubs
								.setAdapter(ContactsInviteForRideActivity.adapterClubMy);
						// listMyclubs.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
						listMyclubs
								.setOnItemClickListener(new OnItemClickListener() {

									@Override
									public void onItemClick(
											AdapterView<?> parent, View v,
											int position, long id) {
										// TODO Auto-generated method stub

										ClubObject bean = ClubListClass.ClubList
												.get(position);
										bean.setSelected(true);

										for (int i = 0; i < ClubListClass.ClubList
												.size(); i++) {

											if (i == position)
												continue;

											bean = ClubListClass.ClubList
													.get(i);
											bean.setSelected(false);
										}
										// Remove all memberclib list selection
										for (int i = 0; i < ClubListClass.MemberClubList
												.size(); i++) {

											bean = ClubListClass.MemberClubList
													.get(i);
											bean.setSelected(false);
										}

										ContactsInviteForRideActivity.adapterClubMember
												.setSelectedIndex(-1);
										ContactsInviteForRideActivity.adapterClubMember
												.notifyDataSetChanged();

										// if (bean.isSelected()) {
										// bean.setSelected(false);
										// chk.setChecked(false);
										// } else {
										// bean.setSelected(true);
										// chk.setChecked(true);
										// }
										ContactsInviteForRideActivity.adapterClubMy
												.setSelectedIndex(position);
										ContactsInviteForRideActivity.adapterClubMy
												.notifyDataSetChanged();

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

						// ClubsAdaptor adapter = new ClubsAdaptor(
						// ShareLocationFragmentActivity.this,
						// ClubListClass.MemberClubList);

						// listMembersclubs.setAdapter(adapter);
						// listMembersclubs
						// .setOnItemClickListener(new OnItemClickListener() {
						//
						// @Override
						// public void onItemClick(
						// AdapterView<?> parent, View v,
						// int position, long id) {
						// // TODO Auto-generated method stub
						// CheckBox chk = (CheckBox) v
						// .findViewById(R.id.myclubcheckBox);
						// ClubObject bean = ClubListClass.MemberClubList
						// .get(position);
						//
						// if (bean.isSelected()) {
						// bean.setSelected(false);
						// chk.setChecked(false);
						// } else {
						// bean.setSelected(true);
						// chk.setChecked(true);
						// }
						//
						// }
						// });

						ContactsInviteForRideActivity.adapterClubMember = new ClubMemberAdapter(
								ShareLocationFragmentActivity.this,
								ClubListClass.MemberClubList, false);

						listMembersclubs
								.setAdapter(ContactsInviteForRideActivity.adapterClubMember);
						listMembersclubs
								.setOnItemClickListener(new OnItemClickListener() {

									@Override
									public void onItemClick(
											AdapterView<?> parent, View v,
											int position, long id) {
										// TODO Auto-generated method stub

										ClubObject bean = ClubListClass.MemberClubList
												.get(position);
										bean.setSelected(true);

										for (int i = 0; i < ClubListClass.MemberClubList
												.size(); i++) {

											if (i == position)
												continue;

											bean = ClubListClass.MemberClubList
													.get(i);
											bean.setSelected(false);
										}

										// Unselect all MyClub
										for (int i = 0; i < ClubListClass.ClubList
												.size(); i++) {

											bean = ClubListClass.ClubList
													.get(i);
											bean.setSelected(false);

										}

										ContactsInviteForRideActivity.adapterClubMy
												.setSelectedIndex(-1);
										ContactsInviteForRideActivity.adapterClubMy
												.notifyDataSetChanged();
										// if (bean.isSelected()) {
										// bean.setSelected(false);
										// chk.setChecked(false);
										// } else {
										// bean.setSelected(true);
										// chk.setChecked(true);
										// }

										ContactsInviteForRideActivity.adapterClubMember
												.setSelectedIndex(position);
										ContactsInviteForRideActivity.adapterClubMember
												.notifyDataSetChanged();

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
					NewHomeScreen.class);
			 mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
			 | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivityForResult(mainIntent, 500);
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
			finish();
		} else {
			fromrelative.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Log.d("onResume", "onResume");
		super.onResume();

		String action = getIntent().getAction();
		if (action == null) {
			return;
		}
		Log.d("onResume", "action CLOSE_ACTION : " + NOTIFICATION_ID);
		if (action == "CLOSE_ACTION") {
			NotificationManager notificationManager = (NotificationManager) ShareLocationFragmentActivity.this
					.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.cancel(NOTIFICATION_ID);
			if(stopsharing != null){
				stopsharing.performClick();
			}else {
				stopSharingLocation();
			}
		}
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

	private void stopSharingLocation(){
		try{
			if (isMyServiceRunning(LocationShareService.class)) {
				stopService(new Intent(ShareLocationFragmentActivity.this,
						LocationShareService.class));
			}

			SharedPreferences sharedPreferences = getSharedPreferences(
					"routeId", 0);
			Editor editor = sharedPreferences.edit();
			editor.putString("routeId", "");
			editor.commit();

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

			NotificationManager notificationManager = (NotificationManager) ShareLocationFragmentActivity.this
					.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.cancel(NOTIFICATION_ID);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

}
