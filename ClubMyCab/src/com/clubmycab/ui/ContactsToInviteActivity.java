package com.clubmycab.ui;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import org.json.JSONObject;

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
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.BookaCabFragmentActivity;
import com.clubmycab.CheckPoolFragmentActivity;
import com.clubmycab.CircularImageView;
import com.clubmycab.ClubListClass;
import com.clubmycab.ClubObject;
import com.clubmycab.ClubsAdaptor;
import com.clubmycab.Communicator;
import com.clubmycab.ContactObject;
import com.clubmycab.ContactsAdapter;
import com.clubmycab.ContactsListClass;
import com.clubmycab.R;
import com.clubmycab.ShareLocationFragmentActivity;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.navdrawer.SimpleSideDrawer;

public class ContactsToInviteActivity extends Activity {

	LinearLayout clubcontactslistll;
	ListView contactslist;

	LinearLayout mainclublistll;
	ListView listMyclubs;
	ListView listMembersclubs;

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

	Button contactsbtn;
	Button appFrends;
	Button myclubbtn;
	TextView validmobiletxt;

	Button sendtocontacts;

	static Typeface NeutraTextLight;

	String FullName;
	String MobileNumberstr;

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

	String fromcome;
	String CabId;
	String OwnerMobileNumber;
	String OwnerName;
	String FromLocation;
	String ToLocation;
	String TravelDate;
	String TravelTime;
	String Seats;
	String fromshortname;
	String toshortname;

	int flag = 1;

	String sendres;
	String appusers;

	ArrayList<String> selectednames = new ArrayList<String>();
	ArrayList<String> selectednumbers = new ArrayList<String>();

	RelativeLayout unreadnoticountrl;
	TextView unreadnoticount;

	Boolean clubcreated;
	Boolean appusersavailable;

	String readunreadnotiresp;
	Bitmap mIcon11;
	String fetchclubresp;
	String fetchappusersresp;

	Boolean conitnuechk = false;

	AlertDialog dialogseats;

	RelativeLayout contexthelpcontacts;
	String distancetext;

	String imagenameresp;

	RelativeLayout contactsmyclubrl;
	Tracker tracker;

	boolean exceptioncheck = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts_my_club);

		// Check if Internet present
		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					ContactsToInviteActivity.this);
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
				.getInstance(ContactsToInviteActivity.this);
		tracker = analytics.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);

		// All subsequent hits will be send with screen name = "main screen"
		tracker.setScreenName("Create Invitation");

		contactsmyclubrl = (RelativeLayout) findViewById(R.id.contactsmyclubrl);
		contactsmyclubrl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Log.d("contactsmyclubrl", "contactsmyclubrl");
			}
		});

		Bundle extras = getIntent().getExtras();
		fromcome = extras.getString("fromcome");
		CabId = extras.getString("CabId");
		OwnerName = extras.getString("OwnerName");
		OwnerMobileNumber = extras.getString("MobileNumber");
		FromLocation = extras.getString("FromLocation");
		ToLocation = extras.getString("ToLocation");
		TravelDate = extras.getString("TravelDate");
		TravelTime = extras.getString("TravelTime");
		Seats = extras.getString("Seats");
		fromshortname = extras.getString("fromshortname");
		toshortname = extras.getString("toshortname");

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
		// Intent mainIntent = new Intent(ContactsToInviteActivity.this,
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
		// Intent mainIntent = new Intent(ContactsToInviteActivity.this,
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
		// Intent mainIntent = new Intent(ContactsToInviteActivity.this,
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
		// Intent mainIntent = new Intent(ContactsToInviteActivity.this,
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
		// Intent mainIntent = new Intent(ContactsToInviteActivity.this,
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
		// Intent mainIntent = new Intent(ContactsToInviteActivity.this,
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
		// Intent mainIntent = new Intent(ContactsToInviteActivity.this,
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
		MobileNumberstr = mPrefs.getString("MobileNumber", "");

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

				Intent mainIntent = new Intent(ContactsToInviteActivity.this,
						NotificationListActivity.class);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);

			}
		});

		unreadnoticountrl = (RelativeLayout) findViewById(R.id.unreadnoticountrl);
		unreadnoticount = (TextView) findViewById(R.id.unreadnoticount);

		NeutraTextLight = Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf");

		// //////////////////

		contactsbtn = (Button) findViewById(R.id.contactsbtn);
		appFrends = (Button) findViewById(R.id.appFrends);
		myclubbtn = (Button) findViewById(R.id.myclubbtn);
		sendtocontacts = (Button) findViewById(R.id.sendbtn);
		validmobiletxt = (TextView) findViewById(R.id.validmobiletxt);

		clubcontactslistll = (LinearLayout) findViewById(R.id.clubcontactslistll);
		contactslist = (ListView) findViewById(R.id.contactslist);

		mainclublistll = (LinearLayout) findViewById(R.id.mainclublistll);
		listMyclubs = (ListView) findViewById(R.id.listMyclubs);
		listMembersclubs = (ListView) findViewById(R.id.listMembersclubs);

		searchfromlist = (EditText) findViewById(R.id.searchfromlist);

		contactsbtn.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));
		appFrends.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));
		myclubbtn.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));
		sendtocontacts.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

		if (fromcome.equalsIgnoreCase("invite")
				|| fromcome.equalsIgnoreCase("checkpool")) {

			contactsbtn.setVisibility(View.VISIBLE);
			validmobiletxt.setVisibility(View.VISIBLE);
			myclubbtn.setVisibility(View.VISIBLE);
		} else {
			contactsbtn.setVisibility(View.VISIBLE);
			validmobiletxt.setVisibility(View.VISIBLE);
			myclubbtn.setVisibility(View.GONE);
		}

		// /// For contacts list

		Cursor cursor = null;
		try {
			cursor = ContactsToInviteActivity.this.getContentResolver().query(
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
				validmobiletxt.setVisibility(View.VISIBLE);
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

				objAdapter = new ContactsAdapter(ContactsToInviteActivity.this,
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
				validmobiletxt.setVisibility(View.GONE);
				mainclublistll.setVisibility(View.VISIBLE);

				ClubListClass.ClubList.clear();
				ClubListClass.MemberClubList.clear();

				SharedPreferences mPrefs111111 = getSharedPreferences(
						"MyClubs", 0);
				String clubs1 = mPrefs111111.getString("clubs", "");

				if (clubs1.equalsIgnoreCase("No Users of your Club")) {
					Toast.makeText(
							ContactsToInviteActivity.this,
							"You are not yet a member of any club. Create your own club through My Clubs option",
							Toast.LENGTH_LONG).show();
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
									ContactsToInviteActivity.this,
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
									ContactsToInviteActivity.this,
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

		sendtocontacts.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Animation animScale = AnimationUtils
						.loadAnimation(ContactsToInviteActivity.this,
								R.anim.button_click_anim);
				sendtocontacts.startAnimation(animScale);

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

								if (selectednumbers.indexOf(s) != selectednumbers
										.lastIndexOf(s)) {
									selectednames.remove(selectednumbers
											.lastIndexOf(s));
									selectednumbers.remove(selectednumbers
											.lastIndexOf(s));
								}
							}

							if (selectednumbers.indexOf(MobileNumberstr) != -1) {
								selectednames.remove(selectednumbers
										.indexOf(MobileNumberstr));
								selectednumbers.remove(selectednumbers
										.indexOf(MobileNumberstr));
							}

						}

						if (selectednames.size() > 0) {

							Log.d("selectednames", "" + selectednames);
							Log.d("selectednumbers", "" + selectednumbers);

							if (fromcome.equalsIgnoreCase("invite")) {

								tracker.send(new HitBuilders.EventBuilder()
										.setCategory("Invite")
										.setAction("Invite").setLabel("Invite")
										.build());

								if (selectednames.size() >= Integer
										.parseInt(Seats)) {
									conitnuechk = false;

									if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
										new ConnectionTaskForSendInvite()
												.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
									} else {
										new ConnectionTaskForSendInvite()
												.execute();
									}

								} else {

									conitnuechk = true;

									AlertDialog.Builder builder = new AlertDialog.Builder(
											ContactsToInviteActivity.this);
									builder.setMessage("You have "
											+ Seats
											+ " seats to share and have selected only "
											+ selectednames.size()
											+ " friend(s)");
									builder.setCancelable(true);
									builder.setPositiveButton(
											"Continue Anyways",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int id) {

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
										.setLabel("Refer Friend (Ride)")
										.build());

								if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
									new ConnectionTaskForReferfriends()
											.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
								} else {
									new ConnectionTaskForReferfriends()
											.execute();
								}

							}

							else if (fromcome.equalsIgnoreCase("checkpool")) {

								tracker.send(new HitBuilders.EventBuilder()
										.setCategory("Invite")
										.setAction("Invite").setLabel("Invite")
										.build());

								if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
									new ConnectionTaskForOwnerInviteFriends()
											.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
								} else {
									new ConnectionTaskForOwnerInviteFriends()
											.execute();
								}

							} else {
								Log.d("kahi se nahi", "kahi se nahi");
							}

						} else {
							Toast.makeText(ContactsToInviteActivity.this,
									"Please select Clubs/Contacts to invite",
									Toast.LENGTH_LONG).show();
						}

					}
				};
				mHandler2.postDelayed(mRunnable2, 500);

			}
		});

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

		contexthelpcontacts = (RelativeLayout) findViewById(R.id.contexthelpcontacts);

		SharedPreferences mPrefs1 = getSharedPreferences("ContextHelp", 0);
		String whichtimeforcontactsmyclub = mPrefs1.getString(
				"whichtimeforcontactsmyclub", "");

		if (whichtimeforcontactsmyclub.isEmpty()
				|| whichtimeforcontactsmyclub == null
				|| whichtimeforcontactsmyclub.equalsIgnoreCase("")) {
			contexthelpcontacts.setVisibility(View.VISIBLE);
		}

		contexthelpcontacts.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				contexthelpcontacts.setVisibility(View.GONE);

				SharedPreferences sharedPreferences = getSharedPreferences(
						"ContextHelp", 0);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putString("whichtimeforcontactsmyclub", "second");
				editor.commit();
			}
		});

		SharedPreferences mPrefs11111 = getSharedPreferences("MyClubs", 0);
		String clubs = mPrefs11111.getString("clubs", "");

		if (clubs.equalsIgnoreCase("No Users of your Club")) {
			clubcreated = false;
		} else {
			clubcreated = true;
		}

		if (fromcome.equalsIgnoreCase("invite")
				|| fromcome.equalsIgnoreCase("checkpool")) {

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
				validmobiletxt.setVisibility(View.GONE);
				mainclublistll.setVisibility(View.VISIBLE);

				ClubListClass.ClubList.clear();

				ClubListClass.MemberClubList.clear();

				SharedPreferences mPrefs111111 = getSharedPreferences(
						"MyClubs", 0);
				String clubs1 = mPrefs111111.getString("clubs", "");

				if (clubs1.equalsIgnoreCase("No Users of your Club")) {
					Toast.makeText(
							ContactsToInviteActivity.this,
							"You are not yet a member of any club. Create your own club through My Clubs option",
							Toast.LENGTH_LONG).show();
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
									ContactsToInviteActivity.this,
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
									ContactsToInviteActivity.this,
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
				validmobiletxt.setVisibility(View.VISIBLE);
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

				objAdapter = new ContactsAdapter(ContactsToInviteActivity.this,
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
		} else if (fromcome.equalsIgnoreCase("joinpool")) {

			flag = 1;

			contactsbtn.setBackgroundColor(Color.parseColor("#B1C8E6"));
			contactsbtn.setTextColor(Color.BLACK);

			appFrends.setBackgroundColor(Color.parseColor("#4279bd"));
			appFrends.setTextColor(Color.WHITE);

			myclubbtn.setBackgroundColor(Color.parseColor("#4279bd"));
			myclubbtn.setTextColor(Color.WHITE);

			clubcontactslistll.setVisibility(View.VISIBLE);
			searchfromlist.setVisibility(View.VISIBLE);
			validmobiletxt.setVisibility(View.VISIBLE);
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

			objAdapter = new ContactsAdapter(ContactsToInviteActivity.this,
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
				Toast.makeText(ContactsToInviteActivity.this,
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
			String url_select = GlobalVariables.ServiceUrl
					+ "/FetchUnreadNotificationCount.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
					"MobileNumber", MobileNumberstr);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MobileNumberBasicNameValuePair);

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
				readunreadnotiresp = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("readunreadnotiresp", "" + readunreadnotiresp);

		}
	}

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
				Toast.makeText(ContactsToInviteActivity.this,
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

	// convert from bitmap to byte array
	public byte[] getBytesFromBitmap(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 70, stream);
		return stream.toByteArray();
	}

	// ///////

	private class ConnectionTaskForSendInvite extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(
				ContactsToInviteActivity.this);

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
				Toast.makeText(ContactsToInviteActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (flag == 0) {

				Intent mainIntent = new Intent(ContactsToInviteActivity.this,
						CheckPoolFragmentActivity.class);
				mainIntent.putExtra("CabId", CabId);
				mainIntent.putExtra("MobileNumber", MobileNumberstr);
				mainIntent.putExtra("OwnerName", OwnerName);
				SharedPreferences mPrefs111 = getSharedPreferences("userimage",
						0);
				String imgname = mPrefs111.getString("imgname", "");
				mainIntent.putExtra("OwnerImage", imgname);
				mainIntent.putExtra("FromLocation", FromLocation);
				mainIntent.putExtra("ToLocation", ToLocation);

				mainIntent.putExtra("FromShortName", fromshortname);
				mainIntent.putExtra("ToShortName", toshortname);

				mainIntent.putExtra("TravelDate", TravelDate);
				mainIntent.putExtra("TravelTime", TravelTime);
				mainIntent.putExtra("Seats", Seats);
				mainIntent.putExtra("RemainingSeats", Seats);
				mainIntent.putExtra("Seat_Status", "0/" + Seats);
				mainIntent.putExtra("Distance", distancetext);
				mainIntent.putExtra("OpenTime", "");
				mainIntent.putExtra("CabStatus", "A");
				mainIntent.putExtra("comefrom", "fromcontactsmyclub");

				mainIntent.putExtra("BookingRefNo", "");
				mainIntent.putExtra("DriverName", "");
				mainIntent.putExtra("DriverNumber", "");
				mainIntent.putExtra("CarNumber", "");

				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);

			} else {
				showAlertDialog(selectednames, selectednumbers);
			}
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
		yesadd.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (yesclubname.getVisibility() == View.GONE) {
					yesclubname.setVisibility(View.VISIBLE);
				} else {

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new ConnectionTaskForNewClub().executeOnExecutor(
								AsyncTask.THREAD_POOL_EXECUTOR, FullName,
								MobileNumberstr, yesclubname.getText()
										.toString().trim(), names.toString(),
								numbers.toString());
					} else {
						new ConnectionTaskForNewClub().execute(FullName,
								MobileNumberstr, yesclubname.getText()
										.toString().trim(), names.toString(),
								numbers.toString());
					}
				}

			}
		});

		Button noadd = (Button) dialog.findViewById(R.id.noadd);
		noadd.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent mainIntent = new Intent(ContactsToInviteActivity.this,
						CheckPoolFragmentActivity.class);
				mainIntent.putExtra("CabId", CabId);
				mainIntent.putExtra("MobileNumber", MobileNumberstr);
				mainIntent.putExtra("OwnerName", OwnerName);
				SharedPreferences mPrefs111 = getSharedPreferences("userimage",
						0);
				String imgname = mPrefs111.getString("imgname", "");
				mainIntent.putExtra("OwnerImage", imgname);
				mainIntent.putExtra("FromLocation", FromLocation);
				mainIntent.putExtra("ToLocation", ToLocation);

				mainIntent.putExtra("FromShortName", fromshortname);
				mainIntent.putExtra("ToShortName", toshortname);

				mainIntent.putExtra("TravelDate", TravelDate);
				mainIntent.putExtra("TravelTime", TravelTime);
				mainIntent.putExtra("Seats", Seats);
				mainIntent.putExtra("RemainingSeats", Seats);
				mainIntent.putExtra("Seat_Status", "0/" + Seats);
				mainIntent.putExtra("Distance", distancetext);
				mainIntent.putExtra("OpenTime", "");
				mainIntent.putExtra("CabStatus", "A");
				mainIntent.putExtra("comefrom", "fromcontactsmyclub");

				mainIntent.putExtra("BookingRefNo", "");
				mainIntent.putExtra("DriverName", "");
				mainIntent.putExtra("DriverNumber", "");
				mainIntent.putExtra("CarNumber", "");

				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
		});

		dialog.show();
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
					+ "&sensor=false&units=metric&mode=driving&alternatives=true";

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
					"MobileNumber", MobileNumberstr);
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

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		if (conitnuechk) {

			dialogseats.dismiss();
		} else {

			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
			ContactsToInviteActivity.this.finish();
		}
	}

	// ///////

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
				Toast.makeText(ContactsToInviteActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			Intent mainIntent = new Intent(ContactsToInviteActivity.this,
					CheckPoolFragmentActivity.class);

			mainIntent.putExtra("CabId", CabId);
			mainIntent.putExtra("MobileNumber", MobileNumberstr);
			mainIntent.putExtra("OwnerName", OwnerName);
			SharedPreferences mPrefs111 = getSharedPreferences("userimage", 0);
			String imgname = mPrefs111.getString("imgname", "");
			mainIntent.putExtra("OwnerImage", imgname);
			mainIntent.putExtra("FromLocation", FromLocation);
			mainIntent.putExtra("ToLocation", ToLocation);

			mainIntent.putExtra("FromShortName", fromshortname);
			mainIntent.putExtra("ToShortName", toshortname);

			mainIntent.putExtra("TravelDate", TravelDate);
			mainIntent.putExtra("TravelTime", TravelTime);
			mainIntent.putExtra("Seats", Seats);
			mainIntent.putExtra("RemainingSeats", Seats);
			mainIntent.putExtra("Seat_Status", "0/" + Seats);
			mainIntent.putExtra("Distance", distancetext);
			mainIntent.putExtra("OpenTime", "");
			mainIntent.putExtra("CabStatus", "A");
			mainIntent.putExtra("comefrom", "fromcontactsmyclub");

			mainIntent.putExtra("BookingRefNo", "");
			mainIntent.putExtra("DriverName", "");
			mainIntent.putExtra("DriverNumber", "");
			mainIntent.putExtra("CarNumber", "");

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
		}
	}

	private class ConnectionTaskForReferfriends extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(
				ContactsToInviteActivity.this);

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
				Toast.makeText(ContactsToInviteActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			ContactsToInviteActivity.this.finish();
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
					"CabId", CabId);
			BasicNameValuePair MemberNameBasicNameValuePair = new BasicNameValuePair(
					"MemberName", FullName);
			BasicNameValuePair MemberNumberBasicNameValuePair = new BasicNameValuePair(
					"MemberNumber", MobileNumberstr);
			BasicNameValuePair ReferedUserNameBasicNameValuePair = new BasicNameValuePair(
					"ReferedUserName", selectednames.toString());
			BasicNameValuePair ReferedUserNumberBasicNameValuePair = new BasicNameValuePair(
					"ReferedUserNumber", selectednumbers.toString());

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(CabIdBasicNameValuePair);
			nameValuePairList.add(MemberNameBasicNameValuePair);
			nameValuePairList.add(MemberNumberBasicNameValuePair);
			nameValuePairList.add(ReferedUserNameBasicNameValuePair);
			nameValuePairList.add(ReferedUserNumberBasicNameValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			Log.d("httpResponse", "" + httpResponse);
		}
	}

	// /////////////////////////
	private class ConnectionTaskForOwnerInviteFriends extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(
				ContactsToInviteActivity.this);

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
				Toast.makeText(ContactsToInviteActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			ContactsToInviteActivity.this.finish();
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
					"CabId", CabId);
			BasicNameValuePair MembersNumberBasicNameValuePair = new BasicNameValuePair(
					"MembersNumber", selectednumbers.toString());
			BasicNameValuePair MembersNameBasicNameValuePair = new BasicNameValuePair(
					"MembersName", selectednames.toString());
			BasicNameValuePair OwnerNameBasicNameValuePair = new BasicNameValuePair(
					"OwnerName", OwnerName);
			BasicNameValuePair OwnerNumberBasicNameValuePair = new BasicNameValuePair(
					"OwnerNumber", OwnerMobileNumber);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(CabIdBasicNameValuePair);
			nameValuePairList.add(MembersNumberBasicNameValuePair);
			nameValuePairList.add(MembersNameBasicNameValuePair);
			nameValuePairList.add(OwnerNameBasicNameValuePair);
			nameValuePairList.add(OwnerNumberBasicNameValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			Log.d("httpResponse", "" + httpResponse);
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
}
