package com.clubmycab.ui;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.CheckPoolFragmentActivity;
import com.clubmycab.CircularImageView;
import com.clubmycab.DatabaseHandler;
import com.clubmycab.ListViewAdapter;
import com.clubmycab.MemberRideFragmentActivity;
import com.clubmycab.MyRidesListClass;
import com.clubmycab.MyRidesObject;
import com.clubmycab.PagingListView;
import com.clubmycab.R;
import com.clubmycab.SafeAsyncTask;
import com.clubmycab.ShowHistoryRidesAdaptor;
import com.clubmycab.UpcomingStartTripAlarm;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.navdrawer.SimpleSideDrawer;

public class MyRidesActivity extends Activity {

	String FullName;
	String MobileNumberstr;

	String poolresponse;
	String rideshistoryresponse;

	ListView mypoollist;
	PagingListView listView;

	boolean exceptioncheck = false;

	Tracker tracker;

	ArrayList<String> CabId = new ArrayList<String>();
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
	ArrayList<String> CabStatus = new ArrayList<String>();
	ArrayList<String> imagename = new ArrayList<String>();
	ArrayList<String> BookingRefNo = new ArrayList<String>();
	ArrayList<String> DriverName = new ArrayList<String>();
	ArrayList<String> DriverNumber = new ArrayList<String>();
	ArrayList<String> CarNumber = new ArrayList<String>();

	ListViewAdapter adapter;
	ShowHistoryRidesAdaptor showhisadaptor;

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

	RelativeLayout unreadnoticountrl;
	TextView unreadnoticount;

	String readunreadnotiresp;
	Bitmap mIcon11;

	RelativeLayout myridesrl;

	Button showhistory;
	String latestcabid;
	boolean cabchk;

	boolean shouldGoBack = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_pool);

		// Check if Internet present
		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					MyRidesActivity.this);
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

		myridesrl = (RelativeLayout) findViewById(R.id.myridesrl);
		myridesrl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Log.d("myridesrl", "myridesrl");
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
		// Intent mainIntent = new Intent(MyRidesActivity.this,
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
		// }
		// });
		//
		// bookacab.setOnClickListener(new View.OnClickListener() {
		// @SuppressWarnings("deprecation")
		// @Override
		// public void onClick(View arg0) {
		// mNav.toggleDrawer();
		//
		// Intent mainIntent = new Intent(MyRidesActivity.this,
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
		// Intent mainIntent = new Intent(MyRidesActivity.this,
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
		// Intent mainIntent = new Intent(MyRidesActivity.this,
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
		// Intent mainIntent = new Intent(MyRidesActivity.this,
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
		// Intent mainIntent = new Intent(MyRidesActivity.this,
		// AboutPagerFragmentActivity.class);
		// startActivityForResult(mainIntent, 500);
		// overridePendingTransition(R.anim.slide_in_right,
		// R.anim.slide_out_left);
		// }
		// });

		GoogleAnalytics analytics = GoogleAnalytics
				.getInstance(MyRidesActivity.this);
		tracker = analytics
				.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);

		// All subsequent hits will be send with screen name = "main screen"
		tracker.setScreenName("MyRides");

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

				Intent mainIntent = new Intent(MyRidesActivity.this,
						NotificationListActivity.class);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);

			}
		});

		unreadnoticountrl = (RelativeLayout) findViewById(R.id.unreadnoticountrl);
		unreadnoticount = (TextView) findViewById(R.id.unreadnoticount);

		if (GlobalVariables.UnreadNotificationCount.equalsIgnoreCase("0")) {

			unreadnoticountrl.setVisibility(View.GONE);

		} else {

			unreadnoticountrl.setVisibility(View.VISIBLE);
			unreadnoticount.setText(GlobalVariables.UnreadNotificationCount);
		}

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
		} else {

			byte[] b = Base64.decode(imagestr, Base64.DEFAULT);
			InputStream is = new ByteArrayInputStream(b);
			Bitmap yourSelectedImage = BitmapFactory.decodeStream(is);

			profilepic.setImageBitmap(yourSelectedImage);
			drawerprofilepic.setImageBitmap(yourSelectedImage);
		}

		mypoollist = (ListView) findViewById(R.id.mypoollist);

		String comefrom = getIntent().getStringExtra("comefrom");

		Log.d("MyRidesActivity", "comefrom : " + comefrom);

		if (comefrom == null || comefrom.isEmpty()
				|| comefrom.equalsIgnoreCase("null")) {
			String PoolResponseSplash = getIntent().getStringExtra(
					"PoolResponseSplash");

			Log.d("MyRidesActivity", "PoolResponseSplash : "
					+ PoolResponseSplash);

			if (PoolResponseSplash == null || PoolResponseSplash.isEmpty()
					|| PoolResponseSplash.equalsIgnoreCase("null")) {

				shouldGoBack = true;

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new ConnectionTaskForFetchPool()
							.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					new ConnectionTaskForFetchPool().execute();
				}

			} else {

				shouldGoBack = false;

				poolresponse = PoolResponseSplash;
				ConnectionTaskForFetchPoolPostExecute();
				Log.d("MyRidesActivity", "PoolResponseSplash OwnerName size : "
						+ OwnerName.size());
				if (OwnerName.size() == 1) {
					mypoollist.performItemClick(mypoollist.getAdapter()
							.getView(0, null, null), 0, mypoollist.getAdapter()
							.getItemId(0));
				}
			}
		} else {

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForFetchPool()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new ConnectionTaskForFetchPool().execute();
			}

		}
	}

	// ///////

	private class ConnectionTaskForFetchPool extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(MyRidesActivity.this);

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
				Toast.makeText(MyRidesActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			ConnectionTaskForFetchPoolPostExecute();

			String comefrom = getIntent().getStringExtra("comefrom");

			if (comefrom != null && !comefrom.isEmpty()
					&& !comefrom.equalsIgnoreCase("null")) {

				String cabID = getIntent().getStringExtra("cabID");
				int index = CabId.indexOf(cabID);

				Log.d("MyRidesActivity", "onPostExecute comefrom : " + comefrom
						+ " cabID : " + cabID + " index : " + index);

				mypoollist.performItemClick(
						mypoollist.getAdapter().getView(index, null, null),
						index, mypoollist.getAdapter().getItemId(index));

				// if
				// (comefrom.equals(UpcomingStartTripAlarm.ALARM_TYPE_UPCOMING))
				// {
				//
				// mypoollist.performItemClick(mypoollist.getAdapter()
				// .getView(index, null, null), index, mypoollist
				// .getAdapter().getItemId(index));
				//
				// } else if (comefrom
				// .equals(UpcomingStartTripAlarm.ALARM_TYPE_START_TRIP)) {
				//
				// mypoollist.performItemClick(mypoollist.getAdapter()
				// .getView(index, null, null), index, mypoollist
				// .getAdapter().getItemId(index));
				// }

			}
		}

	}

	private void ConnectionTaskForFetchPoolPostExecute() {
		if (poolresponse.equalsIgnoreCase("No Pool Created Yet!!")
				|| poolresponse.equalsIgnoreCase("[]")) {
			Toast.makeText(MyRidesActivity.this, "No active rides!",
					Toast.LENGTH_LONG).show();
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
			CabStatus.clear();
			imagename.clear();

			BookingRefNo.clear();
			DriverName.clear();
			DriverNumber.clear();
			CarNumber.clear();

			try {
				JSONArray subArray = new JSONArray(poolresponse);
				String allcabids = "s";
				for (int i = 0; i < subArray.length(); i++) {
					try {
						CabId.add(subArray.getJSONObject(i).getString("CabId")
								.toString());

						allcabids += "'"
								+ subArray.getJSONObject(i).getString("CabId")
										.toString().trim() + "',";

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
						Seats.add(subArray.getJSONObject(i).getString("Seats")
								.toString());
						RemainingSeats.add(subArray.getJSONObject(i)
								.getString("RemainingSeats").toString());
						Seat_Status.add(subArray.getJSONObject(i)
								.getString("Seat_Status").toString());
						Distance.add(subArray.getJSONObject(i)
								.getString("Distance").toString());
						OpenTime.add(subArray.getJSONObject(i)
								.getString("OpenTime").toString());
						CabStatus.add(subArray.getJSONObject(i)
								.getString("CabStatus").toString());
						imagename.add(subArray.getJSONObject(i)
								.getString("imagename").toString());

						BookingRefNo.add(subArray.getJSONObject(i)
								.getString("BookingRefNo").toString());
						DriverName.add(subArray.getJSONObject(i)
								.getString("DriverName").toString());
						DriverNumber.add(subArray.getJSONObject(i)
								.getString("DriverNumber").toString());
						CarNumber.add(subArray.getJSONObject(i)
								.getString("CarNumber").toString());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				DatabaseHandler db = new DatabaseHandler(MyRidesActivity.this);
				allcabids = allcabids.substring(1, allcabids.length() - 1);
				db.deleteArchieveChats(allcabids);

				adapter = new ListViewAdapter(MyRidesActivity.this,
						FromShortName, ToShortName, TravelDate, TravelTime,
						Seat_Status, OwnerName, imagename);
				mypoollist.setAdapter(adapter);

				mypoollist.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {

						Log.d("arg2", "" + arg2);

						if (MobileNumber.get(arg2).equalsIgnoreCase(
								MobileNumberstr)) {

							final Intent mainIntent = new Intent(
									MyRidesActivity.this,
									CheckPoolFragmentActivity.class);
							mainIntent.putExtra("CabId", CabId.get(arg2));
							mainIntent.putExtra("MobileNumber",
									MobileNumber.get(arg2));
							mainIntent.putExtra("OwnerName",
									OwnerName.get(arg2));
							mainIntent.putExtra("OwnerImage",
									imagename.get(arg2));
							mainIntent.putExtra("FromLocation",
									FromLocation.get(arg2));
							mainIntent.putExtra("ToLocation",
									ToLocation.get(arg2));

							mainIntent.putExtra("FromShortName",
									FromShortName.get(arg2));
							mainIntent.putExtra("ToShortName",
									ToShortName.get(arg2));

							mainIntent.putExtra("TravelDate",
									TravelDate.get(arg2));
							mainIntent.putExtra("TravelTime",
									TravelTime.get(arg2));
							mainIntent.putExtra("Seats", Seats.get(arg2));
							mainIntent.putExtra("RemainingSeats",
									RemainingSeats.get(arg2));
							mainIntent.putExtra("Seat_Status",
									Seat_Status.get(arg2));
							mainIntent.putExtra("Distance", Distance.get(arg2));
							mainIntent.putExtra("OpenTime", OpenTime.get(arg2));

							mainIntent.putExtra("CabStatus",
									CabStatus.get(arg2));

							mainIntent.putExtra("BookingRefNo",
									BookingRefNo.get(arg2));
							mainIntent.putExtra("DriverName",
									DriverName.get(arg2));
							mainIntent.putExtra("DriverNumber",
									DriverNumber.get(arg2));
							mainIntent.putExtra("CarNumber",
									CarNumber.get(arg2));

							MyRidesActivity.this.startActivity(mainIntent);

						} else {

							final Intent mainIntent = new Intent(
									MyRidesActivity.this,
									MemberRideFragmentActivity.class);
							mainIntent.putExtra("CabId", CabId.get(arg2));
							mainIntent.putExtra("MobileNumber",
									MobileNumber.get(arg2));
							mainIntent.putExtra("OwnerName",
									OwnerName.get(arg2));
							mainIntent.putExtra("OwnerImage",
									imagename.get(arg2));
							mainIntent.putExtra("FromLocation",
									FromLocation.get(arg2));
							mainIntent.putExtra("ToLocation",
									ToLocation.get(arg2));

							mainIntent.putExtra("FromShortName",
									FromShortName.get(arg2));
							mainIntent.putExtra("ToShortName",
									ToShortName.get(arg2));

							mainIntent.putExtra("TravelDate",
									TravelDate.get(arg2));
							mainIntent.putExtra("TravelTime",
									TravelTime.get(arg2));
							mainIntent.putExtra("Seats", Seats.get(arg2));
							mainIntent.putExtra("RemainingSeats",
									RemainingSeats.get(arg2));
							mainIntent.putExtra("Seat_Status",
									Seat_Status.get(arg2));
							mainIntent.putExtra("Distance", Distance.get(arg2));
							mainIntent.putExtra("OpenTime", OpenTime.get(arg2));

							mainIntent.putExtra("CabStatus",
									CabStatus.get(arg2));

							mainIntent.putExtra("BookingRefNo",
									BookingRefNo.get(arg2));
							mainIntent.putExtra("DriverName",
									DriverName.get(arg2));
							mainIntent.putExtra("DriverNumber",
									DriverNumber.get(arg2));
							mainIntent.putExtra("CarNumber",
									CarNumber.get(arg2));

							MyRidesActivity.this.startActivity(mainIntent);
						}
					}
				});

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		showhistory = (Button) findViewById(R.id.showhistory);
		showhistory.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				showhistory.setVisibility(View.GONE);
				mypoollist.setVisibility(View.GONE);
				listView = (PagingListView) findViewById(R.id.paging_list_view);
				listView.setVisibility(View.VISIBLE);

				showhisadaptor = new ShowHistoryRidesAdaptor(
						MyRidesActivity.this);

				latestcabid = "";
				cabchk = false;

				listView.setAdapter(showhisadaptor);
				listView.setHasMoreItems(true);
				listView.setPagingableListener(new PagingListView.Pagingable() {
					@Override
					public void onLoadMoreItems() {
						if (!cabchk) {
							// new CountryAsyncTask().execute();
							new ConnectionTaskForShowRidesHistory()
									.executeOnExecutor(
											AsyncTask.THREAD_POOL_EXECUTOR,
											latestcabid);
						} else {
							listView.onFinishLoading(false, null);
						}
					}
				});

				listView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {

						Log.d("arg2", "" + arg2);

						if (MobileNumber.get(arg2).equalsIgnoreCase(
								MobileNumberstr)) {

							final Intent mainIntent = new Intent(
									MyRidesActivity.this,
									CheckPoolFragmentActivity.class);
							mainIntent.putExtra("CabId", CabId.get(arg2));
							mainIntent.putExtra("MobileNumber",
									MobileNumber.get(arg2));
							mainIntent.putExtra("OwnerName",
									OwnerName.get(arg2));
							mainIntent.putExtra("OwnerImage",
									imagename.get(arg2));
							mainIntent.putExtra("FromLocation",
									FromLocation.get(arg2));
							mainIntent.putExtra("ToLocation",
									ToLocation.get(arg2));

							mainIntent.putExtra("FromShortName",
									FromShortName.get(arg2));
							mainIntent.putExtra("ToShortName",
									ToShortName.get(arg2));

							mainIntent.putExtra("TravelDate",
									TravelDate.get(arg2));
							mainIntent.putExtra("TravelTime",
									TravelTime.get(arg2));
							mainIntent.putExtra("Seats", Seats.get(arg2));
							mainIntent.putExtra("RemainingSeats",
									RemainingSeats.get(arg2));
							mainIntent.putExtra("Seat_Status",
									Seat_Status.get(arg2));
							mainIntent.putExtra("Distance", Distance.get(arg2));
							mainIntent.putExtra("OpenTime", OpenTime.get(arg2));

							mainIntent.putExtra("CabStatus",
									CabStatus.get(arg2));

							mainIntent.putExtra("BookingRefNo",
									BookingRefNo.get(arg2));
							mainIntent.putExtra("DriverName",
									DriverName.get(arg2));
							mainIntent.putExtra("DriverNumber",
									DriverNumber.get(arg2));
							mainIntent.putExtra("CarNumber",
									CarNumber.get(arg2));

							MyRidesActivity.this.startActivity(mainIntent);

						} else {

							final Intent mainIntent = new Intent(
									MyRidesActivity.this,
									MemberRideFragmentActivity.class);
							mainIntent.putExtra("CabId", CabId.get(arg2));
							mainIntent.putExtra("MobileNumber",
									MobileNumber.get(arg2));
							mainIntent.putExtra("OwnerName",
									OwnerName.get(arg2));
							mainIntent.putExtra("OwnerImage",
									imagename.get(arg2));
							mainIntent.putExtra("FromLocation",
									FromLocation.get(arg2));
							mainIntent.putExtra("ToLocation",
									ToLocation.get(arg2));

							mainIntent.putExtra("FromShortName",
									FromShortName.get(arg2));
							mainIntent.putExtra("ToShortName",
									ToShortName.get(arg2));

							mainIntent.putExtra("TravelDate",
									TravelDate.get(arg2));
							mainIntent.putExtra("TravelTime",
									TravelTime.get(arg2));
							mainIntent.putExtra("Seats", Seats.get(arg2));
							mainIntent.putExtra("RemainingSeats",
									RemainingSeats.get(arg2));
							mainIntent.putExtra("Seat_Status",
									Seat_Status.get(arg2));
							mainIntent.putExtra("Distance", Distance.get(arg2));
							mainIntent.putExtra("OpenTime", OpenTime.get(arg2));

							mainIntent.putExtra("CabStatus",
									CabStatus.get(arg2));

							mainIntent.putExtra("BookingRefNo",
									BookingRefNo.get(arg2));
							mainIntent.putExtra("DriverName",
									DriverName.get(arg2));
							mainIntent.putExtra("DriverNumber",
									DriverNumber.get(arg2));
							mainIntent.putExtra("CarNumber",
									CarNumber.get(arg2));

							MyRidesActivity.this.startActivity(mainIntent);
						}
					}
				});

			}
		});
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
				Toast.makeText(MyRidesActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (rideshistoryresponse.equalsIgnoreCase("No Pool Created Yet!!")
					|| rideshistoryresponse.equalsIgnoreCase("[]")) {
				Toast.makeText(MyRidesActivity.this, "No More history",
						Toast.LENGTH_LONG).show();

				latestcabid = "khatam";
				cabchk = true;
				listView.onFinishLoading(false, null);

			} else {

				MyRidesListClass.listmyrides.clear();

				if (latestcabid.equalsIgnoreCase("") || latestcabid.isEmpty()) {

					for (int i = 0; i < CabId.size(); i++) {

						MyRidesObject ride = new MyRidesObject();

						ride.setCabId(CabId.get(i).toString().trim());
						ride.setMobileNumber(MobileNumber.get(i).toString()
								.trim());
						ride.setOwnerName(OwnerName.get(i).toString().trim());
						ride.setFromLocation(FromLocation.get(i).toString()
								.trim());
						ride.setToLocation(ToLocation.get(i).toString().trim());
						ride.setFromShortName(FromShortName.get(i).toString()
								.trim());
						ride.setToShortName(ToShortName.get(i).toString()
								.trim());

						ride.setTravelDate(TravelDate.get(i).toString().trim());
						ride.setTravelTime(TravelTime.get(i).toString().trim());
						ride.setSeats(Seats.get(i).toString().trim());
						ride.setRemainingSeats(RemainingSeats.get(i).toString()
								.trim());
						ride.setSeat_Status(Seat_Status.get(i).toString()
								.trim());
						ride.setDistance(Distance.get(i).toString().trim());
						ride.setOpenTime(OpenTime.get(i).toString().trim());
						ride.setCabStatus(CabStatus.get(i).toString().trim());
						ride.setImagename(imagename.get(i).toString().trim());

						ride.setBookingRefNo(BookingRefNo.get(i).toString()
								.trim());
						ride.setDriverName(DriverName.get(i).toString().trim());
						ride.setDriverNumber(DriverNumber.get(i).toString()
								.trim());
						ride.setCarNumber(CarNumber.get(i).toString().trim());

						MyRidesListClass.listmyrides.add(ride);
					}
				}

				try {
					JSONArray subArray = new JSONArray(rideshistoryresponse);
					for (int i = 0; i < subArray.length(); i++) {

						MyRidesObject ride = new MyRidesObject();

						ride.setCabId(subArray.getJSONObject(i)
								.getString("CabId").toString().trim());
						ride.setMobileNumber(subArray.getJSONObject(i)
								.getString("MobileNumber").toString());
						ride.setOwnerName(subArray.getJSONObject(i)
								.getString("OwnerName").toString().trim());
						ride.setFromLocation(subArray.getJSONObject(i)
								.getString("FromLocation").toString().trim());
						ride.setToLocation(subArray.getJSONObject(i)
								.getString("ToLocation").toString().trim());
						ride.setFromShortName(subArray.getJSONObject(i)
								.getString("FromShortName").toString().trim());
						ride.setToShortName(subArray.getJSONObject(i)
								.getString("ToShortName").toString().trim());

						ride.setTravelDate(subArray.getJSONObject(i)
								.getString("TravelDate").toString().trim());
						ride.setTravelTime(subArray.getJSONObject(i)
								.getString("TravelTime").toString());
						ride.setSeats(subArray.getJSONObject(i)
								.getString("Seats").toString().trim());
						ride.setRemainingSeats(subArray.getJSONObject(i)
								.getString("RemainingSeats").toString().trim());
						ride.setSeat_Status(subArray.getJSONObject(i)
								.getString("Seat_Status").toString().trim());
						ride.setDistance(subArray.getJSONObject(i)
								.getString("Distance").toString().trim());
						ride.setOpenTime(subArray.getJSONObject(i)
								.getString("OpenTime").toString().trim());

						ride.setCabStatus(subArray.getJSONObject(i)
								.getString("CabStatus").toString().trim());

						ride.setImagename(subArray.getJSONObject(i)
								.getString("imagename").toString().trim());

						ride.setBookingRefNo(subArray.getJSONObject(i)
								.getString("BookingRefNo").toString());
						ride.setDriverName(subArray.getJSONObject(i)
								.getString("DriverName").toString().trim());
						ride.setDriverNumber(subArray.getJSONObject(i)
								.getString("DriverNumber").toString().trim());
						ride.setCarNumber(subArray.getJSONObject(i)
								.getString("CarNumber").toString().trim());

						MyRidesListClass.listmyrides.add(ride);

						CabId.add(subArray.getJSONObject(i).getString("CabId")
								.toString());

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
						Seats.add(subArray.getJSONObject(i).getString("Seats")
								.toString());
						RemainingSeats.add(subArray.getJSONObject(i)
								.getString("RemainingSeats").toString());
						Seat_Status.add(subArray.getJSONObject(i)
								.getString("Seat_Status").toString());
						Distance.add(subArray.getJSONObject(i)
								.getString("Distance").toString());
						OpenTime.add(subArray.getJSONObject(i)
								.getString("OpenTime").toString());
						CabStatus.add(subArray.getJSONObject(i)
								.getString("CabStatus").toString());
						imagename.add(subArray.getJSONObject(i)
								.getString("imagename").toString());

						BookingRefNo.add(subArray.getJSONObject(i)
								.getString("BookingRefNo").toString());
						DriverName.add(subArray.getJSONObject(i)
								.getString("DriverName").toString());
						DriverNumber.add(subArray.getJSONObject(i)
								.getString("DriverNumber").toString());
						CarNumber.add(subArray.getJSONObject(i)
								.getString("CarNumber").toString());

					}

					Log.d("MyRidesListClass.listmyrides", ""
							+ MyRidesListClass.listmyrides);
					Log.d("MyRidesListClass.listmyrides", ""
							+ MyRidesListClass.listmyrides.size());

					new CountryAsyncTask().execute();

					// ///////////////////

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
					"MobileNumber", MobileNumberstr);
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
				rideshistoryresponse = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("rideshistoryresponse", "" + stringBuilder.toString());
		}
	}

	// //////////////////////////

	private class CountryAsyncTask extends SafeAsyncTask<List<MyRidesObject>> {

		@Override
		public List<MyRidesObject> call() throws Exception {
			List<MyRidesObject> result = null;
			result = MyRidesListClass.listmyrides;
			Thread.sleep(3000);
			return result;
		}

		@Override
		protected void onSuccess(List<MyRidesObject> newItems) throws Exception {
			super.onSuccess(newItems);
			if (newItems.size() > 0) {
				latestcabid = newItems.get(newItems.size() - 1).getCabId();
				cabchk = false;
				listView.onFinishLoading(true, newItems);
			} else {
				latestcabid = "khatam";
				cabchk = true;
				listView.onFinishLoading(false, null);
			}
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		if (shouldGoBack) {
			Intent mainIntent = new Intent(MyRidesActivity.this,
					HomeActivity.class);
			mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivityForResult(mainIntent, 500);
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
		} else {
			finish();
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
	// Toast.makeText(MyRidesActivity.this,
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
	// String url_select11 = GlobalVariables.ServiceUrl
	// + "/FetchUnreadNotificationCount.php";
	// HttpPost httpPost = new HttpPost(url_select11);
	// BasicNameValuePair MobileNumberBasicNameValuePair = new
	// BasicNameValuePair(
	// "MobileNumber", MobileNumberstr);
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

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}
}
