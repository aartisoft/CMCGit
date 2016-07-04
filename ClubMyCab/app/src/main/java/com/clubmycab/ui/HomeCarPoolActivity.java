package com.clubmycab.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.R;
import com.clubmycab.asynctasks.GlobalAsyncTask;
import com.clubmycab.asynctasks.GlobalAsyncTask.AsyncTaskResultListener;
import com.clubmycab.model.RideDetailsModel;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.clubmycab.xmlhandler.FetchUnreadNotificationCountHandler;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.viewpagerindicator.CirclePageIndicator;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class HomeCarPoolActivity extends FragmentActivity implements
		AsyncTaskResultListener {
	// test5467

	String FullName;
	public static String MobileNumber;

	RelativeLayout unreadnoticountrl;
	TextView unreadnoticount;

	private ImageView notificationimg;

	boolean exceptioncheck = false;

	private static long back_pressed;

	String myprofileresp;

	Tracker tracker;

	// public static ArrayList<RideDetailsModel> arrayRideDetailsModelsCarPool =
	// new ArrayList<RideDetailsModel>();
	private String rideInvitationsResponseCarPool = "";

	// public static ArrayList<RideDetailsModel> arrayRideDetailsModelsCabShare
	// = new ArrayList<RideDetailsModel>();
	private String rideInvitationsResponseCabShare = "";

	public static final String OFFER_CAR_POOL = "OFFER_CAR_POOL";
	public static final String OFFER_SHARE_CAB = "OFFER_SHARE_CAB";

	public static ArrayList<RideDetailsModel> arrayListInvitations;

	private Boolean isRunning = false, playAnimation = true;
	public static ViewPager viewPagerHome;
	private FragmentStatePagerAdapter mFragmentStatePagerAdapter;
	// private TextView tvViewpagerHeading;
	// private TextView offercabridetextview;
	private CirclePageIndicator circlePageIndicator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		SharedPreferences mPrefs1 = getSharedPreferences("QuitApplication", 0);
		boolean shouldQuitApp = mPrefs1.getBoolean("quitapplication", false);
		if (shouldQuitApp) {

			SharedPreferences.Editor editor = mPrefs1.edit();
			editor.putBoolean("quitapplication", false);
			editor.commit();

			finish();
		}

		setContentView(R.layout.activity_home_page_car_pool);

		RelativeLayout homepagerl = (RelativeLayout) findViewById(R.id.homepagerl);
		homepagerl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Log.d("homepagerl", "homepagerl");
			}
		});

		notificationimg = (ImageView) findViewById(R.id.notificationimg);
		unreadnoticountrl = (RelativeLayout) findViewById(R.id.unreadnoticountrl);
		unreadnoticount = (TextView) findViewById(R.id.unreadnoticount);

		SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
		FullName = mPrefs.getString("FullName", "");
		MobileNumber = mPrefs.getString("MobileNumber", "");

		String endpoint = GlobalVariables.ServiceUrl
				+ "/FetchUnreadNotificationCount.php";
		String authString = MobileNumber;
		String params = "MobileNumber=" + MobileNumber + "&auth="
				+ GlobalMethods.calculateCMCAuthString(authString);
		new GlobalAsyncTask(this, endpoint, params,
				new FetchUnreadNotificationCountHandler(), this, false,
				"FetchUnreadNotificationCount", false);

		findViewById(R.id.flNotifications).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent mainIntent = new Intent(HomeCarPoolActivity.this,
						NotificationListActivity.class);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);

			}
		});

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new ConnectionTaskForFetchClubs()
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			new ConnectionTaskForFetchClubs().execute();
		}

		GoogleAnalytics analytics = GoogleAnalytics
				.getInstance(HomeCarPoolActivity.this);
		tracker = analytics
				.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);

		// All subsequent hits will be send with screen name = "main screen"
		tracker.setScreenName("HomeCarPool");

		UniversalDrawer drawer = new UniversalDrawer(this, tracker);
		drawer.createDrawer();
		GlobalVariables.ActivityName = "HomeCarPoolActivity";

		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.carpoolll);
		linearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent mainIntent = new Intent(HomeCarPoolActivity.this,
						HomeActivity.class);
				mainIntent.putExtra("screentoopen",
						HomeActivity.HOME_ACTIVITY_CAR_POOL);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);

				// Intent mainIntent = new Intent(HomeCarPoolActivity.this,
				// OfferPoolOrCabShare.class);
				// mainIntent.putExtra("screentoopen", OFFER_CAR_POOL);
				// startActivityForResult(mainIntent, 500);
				// overridePendingTransition(R.anim.slide_in_right,
				// R.anim.slide_out_left);

			}
		});

		linearLayout = (LinearLayout) findViewById(R.id.sharecabll);
		linearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent mainIntent = new Intent(HomeCarPoolActivity.this,
						HomeActivity.class);
				mainIntent.putExtra("screentoopen",
						HomeActivity.HOME_ACTIVITY_SHARE_CAB);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);

				// Intent mainIntent = new Intent(HomeCarPoolActivity.this,
				// OfferPoolOrCabShare.class);
				// mainIntent.putExtra("screentoopen", OFFER_SHARE_CAB);
				// startActivityForResult(mainIntent, 500);
				// overridePendingTransition(R.anim.slide_in_right,
				// R.anim.slide_out_left);
			}
		});

		TextView textView = (TextView) findViewById(R.id.textViewBookACab);
		textView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent mainIntent = new Intent(HomeCarPoolActivity.this,
						HomeActivity.class);
				mainIntent.putExtra("screentoopen",
						HomeActivity.HOME_ACTIVITY_BOOK_CAB);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
		});

		// tvViewpagerHeading = (TextView)
		// findViewById(R.id.tvViewpagerHeading);
		// viewHome = findViewById(R.id.viewHome);
		// offercabridetextview = (TextView)
		// findViewById(R.id.offercabridetextview);

		arrayListInvitations = new ArrayList<RideDetailsModel>();

		setPagger();
	}

	@Override
	public void onBackPressed() {

		if (back_pressed + 2000 > System.currentTimeMillis()) {
			super.onBackPressed();
		} else {
			Toast.makeText(getBaseContext(), "Press once again to exit!",
					Toast.LENGTH_SHORT).show();
			back_pressed = System.currentTimeMillis();
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
				Toast.makeText(HomeCarPoolActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (myprofileresp != null && myprofileresp.length() > 0
					&& myprofileresp.contains("Unauthorized Access")) {
				Log.e("HomeActivity", "myclubsresp Unauthorized Access");
				Toast.makeText(HomeCarPoolActivity.this,
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
			String authString = MobileNumber;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(UserNumberBasicNameValuePair);
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
			String myprofileresp = null;

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				myprofileresp = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("myprofileresp", "" + myprofileresp);

			if (!myprofileresp.contains("Unauthorized Access")) {
				SharedPreferences sharedPreferences1 = getSharedPreferences(
						"MyClubs", 0);
				SharedPreferences.Editor editor1 = sharedPreferences1.edit();
				editor1.putString("clubs", myprofileresp.toString().trim());
				editor1.commit();
			}
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
	public void getResult(String response, String uniqueID) {
		if (uniqueID.equals("FetchUnreadNotificationCount")) {

			if (response != null && response.length() > 0
					&& response.contains("Unauthorized Access")) {
				Log.e("HomeActivity",
						"FetchUnreadNotificationCount Unauthorized Access");
				Toast.makeText(HomeCarPoolActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (!TextUtils.isEmpty(GlobalVariables.UnreadNotificationCount) && GlobalVariables.UnreadNotificationCount.equalsIgnoreCase("0")) {

				unreadnoticountrl.setVisibility(View.GONE);

			} else {

				unreadnoticountrl.setVisibility(View.VISIBLE);
				unreadnoticount
						.setText(GlobalVariables.UnreadNotificationCount);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		String PoolResponseSplash = getIntent().getStringExtra(
				"PoolResponseSplash");

		Log.d("HomeCarPoolActivity", "onResume PoolResponseSplash : "
				+ PoolResponseSplash);

		if (PoolResponseSplash == null || PoolResponseSplash.isEmpty()
				|| PoolResponseSplash.equalsIgnoreCase("null")
				|| PoolResponseSplash.equalsIgnoreCase("No Pool Created Yet!!")
				|| PoolResponseSplash.equalsIgnoreCase("[]")) {
			fetchInvitations();
		} else {

			SharedPreferences sharedPreferences = getSharedPreferences(
					"HomeActivityDisplayRides", 0);
			boolean shouldDisplay = sharedPreferences.getBoolean(
					"DisplayRides", false);

			if (shouldDisplay) {
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putBoolean("DisplayRides", false);
				editor.commit();

				Intent mainIntent = new Intent(HomeCarPoolActivity.this,
						NewHomeScreen.class);
				mainIntent.putExtra("PoolResponseSplash", PoolResponseSplash);
				mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(mainIntent);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);

				finish();
			} else {
				fetchInvitations();
			}

		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		playAnimation = false;
	}

	// ///////

	private void fetchInvitations() {

		playAnimation = true;
		if (!isRunning) {

			// tvViewpagerHeadingCarPool
			// .setText("If you have open ride invitation(s), they will appear here");
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForFetchCarPool()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new ConnectionTaskForFetchCarPool().execute();
			}

		}

	}

	private class ConnectionTaskForFetchPool extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

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

			if (exceptioncheck) {
				exceptioncheck = false;
				Toast.makeText(HomeCarPoolActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (rideInvitationsResponseCabShare.contains("Unauthorized Access")) {
				Log.e("HomeActivity",
						"rideInvitationsResponse Unauthorized Access");
				Toast.makeText(HomeCarPoolActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			ArrayList<RideDetailsModel> arrayList = new ArrayList<RideDetailsModel>();

			try {
				JSONObject obj = new JSONObject(rideInvitationsResponseCarPool);
				String response = obj.getString("status");

				if (!response.equalsIgnoreCase("fail")) {
					Gson gson = new Gson();

					ArrayList<RideDetailsModel> arrayRideLocal = gson.fromJson(
							obj.getJSONArray("data").toString(),
							new TypeToken<ArrayList<RideDetailsModel>>() {
							}.getType());

					if (arrayRideLocal.size() > 0) {
						for (int i = 0; i < arrayRideLocal.size(); i++) {
							arrayList.add(arrayRideLocal.get(i));
						}
					}
				}

			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				JSONObject obj = new JSONObject(rideInvitationsResponseCabShare);
				String response = obj.getString("status");

				if (!response.equalsIgnoreCase("fail")) {
					Gson gson = new Gson();

					ArrayList<RideDetailsModel> arrayRideLocal = gson.fromJson(
							obj.getJSONArray("data").toString(),
							new TypeToken<ArrayList<RideDetailsModel>>() {
							}.getType());

					if (arrayRideLocal.size() > 0) {
						for (int i = 0; i < arrayRideLocal.size(); i++) {
							arrayList.add(arrayRideLocal.get(i));
						}
					}
				}

			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			TextView textView = (TextView) findViewById(R.id.textViewRideInvitations);

			if (arrayList.size() > 0) {

				textView.setVisibility(View.VISIBLE);

				arrayListInvitations = arrayList;

				isRunning = false;

				// tvViewpagerHeading.setVisibility(View.VISIBLE);

				circlePageIndicator.setVisibility(View.VISIBLE);
				// viewHome.setVisibility(View.VISIBLE);
				// tvViewpagerHeading.setVisibility(View.GONE);

				mFragmentStatePagerAdapter.notifyDataSetChanged();

				viewPagerHome.setCurrentItem(0);

				if (arrayListInvitations.size() > 1) {

					swipeAutomaticViewPagger();

				}
			} else {

				textView.setVisibility(View.INVISIBLE);

				// Toast.makeText(HomeActivity.this, ""+msg,
				// Toast.LENGTH_LONG).show();
				// tvViewpagerHeading.setVisibility(View.GONE);
				circlePageIndicator.setVisibility(View.INVISIBLE);
				// viewHome.setVisibility(View.GONE);

				mFragmentStatePagerAdapter.notifyDataSetChanged();
				// tvViewpagerHeading.setVisibility(View.VISIBLE);
			}

		}

	}

	public class AuthenticateConnectionFetchPool {

		public AuthenticateConnectionFetchPool() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select11 = GlobalVariables.ServiceUrl
					+ "/rideInvitations.php";
			HttpPost httpPost = new HttpPost(url_select11);
			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
					"mobileNumber", MobileNumber);
			String authString = MobileNumber;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MobileNumberBasicNameValuePair);
			nameValuePairList.add(authValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			Log.d("httpResponse rideInvitations", "" + httpResponse);

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				rideInvitationsResponseCabShare = stringBuilder.append(
						bufferedStrChunk).toString();
			}

			Log.d("rideInvitationsResponseCabShare",
					"" + stringBuilder.toString() + " mobileNumber : "
							+ MobileNumber);
		}
	}

	// ///////

	private class ConnectionTaskForFetchCarPool extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionFetchCarPool mAuth1 = new AuthenticateConnectionFetchCarPool();
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
				Toast.makeText(HomeCarPoolActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (rideInvitationsResponseCarPool.contains("Unauthorized Access")) {
				Log.e("HomeActivity",
						"rideInvitationsResponse Unauthorized Access");
				Toast.makeText(HomeCarPoolActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForFetchPool()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new ConnectionTaskForFetchPool().execute();
			}

		}

	}

	public class AuthenticateConnectionFetchCarPool {

		public AuthenticateConnectionFetchCarPool() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select11 = GlobalVariables.ServiceUrl
					+ "/rideInvitationsCarpool.php";
			HttpPost httpPost = new HttpPost(url_select11);
			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
					"mobileNumber", MobileNumber);
			String authString = MobileNumber;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MobileNumberBasicNameValuePair);
			nameValuePairList.add(authValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			Log.d("httpResponse rideInvitations", "" + httpResponse);

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				rideInvitationsResponseCarPool = stringBuilder.append(
						bufferedStrChunk).toString();
			}

			Log.d("rideInvitationsResponseCarPool",
					"" + stringBuilder.toString() + " mobileNumber : "
							+ MobileNumber);
		}
	}

	// Set ViewPagger Adapter
	private void setPagger() {
		viewPagerHome = (ViewPager) findViewById(R.id.viewPagerHome);

		// Get Rides
		mFragmentStatePagerAdapter = new FragmentStatePagerAdapter(
				getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return arrayListInvitations.size();
			}

			@Override
			public Fragment getItem(int position) {

				return HomeRidePageFragment
						.newInstance(
								arrayListInvitations.get(position),
								(arrayListInvitations.get(position)
										.getRideType().equals("1") ? OFFER_CAR_POOL
										: OFFER_SHARE_CAB));

			}

			@Override
			public int getItemPosition(Object object) {
				return PagerAdapter.POSITION_NONE;
			}

		};

		viewPagerHome.setAdapter(mFragmentStatePagerAdapter);

		circlePageIndicator = (CirclePageIndicator) findViewById(R.id.indicatorHome);
		circlePageIndicator.setViewPager(viewPagerHome, 0);
		circlePageIndicator
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

					@Override
					public void onPageSelected(int position) {
						// Log.d(TAG,
						// "circlePageIndicator onPageSelected : " +
						// position);
						// mCurrentIndex = position;
					}

					@Override
					public void onPageScrolled(int position,
							float positionOffset, int positionOffsetPixels) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onPageScrollStateChanged(int state) {
						// TODO Auto-generated method stub

					}
				});

	}

	private void swipeAutomaticViewPagger() {

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				int index = viewPagerHome.getCurrentItem();

				if (index < (arrayListInvitations.size() - 1)) {
					viewPagerHome.setCurrentItem(index + 1);

				} else
					viewPagerHome.setCurrentItem(0);
				if (playAnimation)

					swipeAutomaticViewPagger();

			}
		}, 4000);
	}
}
