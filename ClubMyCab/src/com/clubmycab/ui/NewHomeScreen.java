package com.clubmycab.ui;

import java.io.BufferedReader;
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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.R;
import com.clubmycab.adapter.HomeScreenPagerAdapter;
import com.clubmycab.asynctasks.GlobalAsyncTask;
import com.clubmycab.asynctasks.GlobalAsyncTask.AsyncTaskResultListener;
import com.clubmycab.model.RideDetailsModel;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.clubmycab.utility.slider.SlidingTabLayout;
import com.clubmycab.xmlhandler.FetchUnreadNotificationCountHandler;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class NewHomeScreen extends FragmentActivity implements
		AsyncTaskResultListener{// Declaring Your View and
									// Variables
	private ViewPager pager;
	private HomeScreenPagerAdapter adapter;
	private SlidingTabLayout tabs;
	private CharSequence Titles[] = { "Rides Available", "My Rides", "Cabs" };
	private int Numboftabs = 3;

	public static String MobileNumber;
	public static final String OFFER_CAR_POOL = "OFFER_CAR_POOL";
	public static final String OFFER_SHARE_CAB = "OFFER_SHARE_CAB";
	public static final String CAR_POOL_ID = "1";
	public static final String SHARE_CAB_ID = "2";
	public static ArrayList<RideDetailsModel> arrayListInvitations;
	private static long back_pressed;
	private String FullName;
	private RelativeLayout unreadnoticountrl;
	private TextView unreadnoticount;
	private ImageView notificationimg;
	boolean exceptioncheck = false;
	private String myprofileresp;
	private Tracker tracker;
	private String rideInvitationsResponseCarPool = "";
	private Boolean isRunning = false, playAnimation = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences mPrefs1 = getSharedPreferences("QuitApplication", 0);
		boolean shouldQuitApp = mPrefs1.getBoolean("quitapplication", false);
		if (shouldQuitApp) {
			SharedPreferences.Editor editor = mPrefs1.edit();
			editor.putBoolean("quitapplication", false);
			editor.commit();
			finish();
		}
		setContentView(R.layout.activity_newhomescreen);
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setOffscreenPageLimit(3);
		adapter = new HomeScreenPagerAdapter(getSupportFragmentManager(),
				Titles, Numboftabs);
		pager.setAdapter(adapter);
		tabs = (SlidingTabLayout) findViewById(R.id.tabs);
		tabs.setDistributeEvenly(true);
		tabs.setViewPager(pager);
		addNavigationDrawer();
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
		new GlobalAsyncTask(NewHomeScreen.this, endpoint, params,
				new FetchUnreadNotificationCountHandler(), NewHomeScreen.this,
				false, "FetchUnreadNotificationCount", false);

		notificationimg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent mainIntent = new Intent(NewHomeScreen.this,
						NotificationListActivity.class);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);

			}
		});
	}

	private void addNavigationDrawer() {
		GoogleAnalytics analytics = GoogleAnalytics
				.getInstance(NewHomeScreen.this);
		tracker = analytics
				.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);

		// All subsequent hits will be send with screen name = "main screen"
		tracker.setScreenName("HomeCarPool");
		UniversalDrawer drawer = new UniversalDrawer(NewHomeScreen.this,
				tracker);
		drawer.createDrawer();
		GlobalVariables.ActivityName = "HomeCarPoolActivity";
		
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

				Intent mainIntent = new Intent(NewHomeScreen.this,
						MyRidesActivity.class);
				mainIntent.putExtra("PoolResponseSplash", PoolResponseSplash);
				startActivity(mainIntent);

				finish(); // to ensure when there are active rides to display,
							// the user simply exits the app rather than
							// returning to this page

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
				Toast.makeText(NewHomeScreen.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (rideInvitationsResponseCarPool.contains("Unauthorized Access")) {
				Log.e("HomeActivity",
						"rideInvitationsResponse Unauthorized Access");
				Toast.makeText(NewHomeScreen.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				//new ConnectionTaskForFetchPool()
					//	.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				//new ConnectionTaskForFetchPool().execute();
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
	
	
	
	

	@Override
	public void getResult(String response, String uniqueID) {
		if (uniqueID.equals("FetchUnreadNotificationCount")) {

			if (response != null && response.length() > 0
					&& response.contains("Unauthorized Access")) {
				Log.e("HomeActivity",
						"FetchUnreadNotificationCount Unauthorized Access");
				Toast.makeText(NewHomeScreen.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

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
