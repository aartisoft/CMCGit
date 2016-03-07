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
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.R;
import com.clubmycab.model.RideDetailsModel;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.viewpagerindicator.CirclePageIndicator;

public class OfferPoolOrCabShare extends FragmentActivity {

	public static ArrayList<RideDetailsModel> arrayRideDetailsModels = new ArrayList<RideDetailsModel>();

	public static ViewPager viewPagerHome;
	private FragmentStatePagerAdapter mFragmentStatePagerAdapter;
	private TextView tvViewpagerHeading;
	private TextView offercabridetextview;
	private CirclePageIndicator circlePageIndicator;
	// private View viewHome;

	boolean playAnimation = true;
	boolean isRunning = false;

	private String rideInvitationsResponse = "";

	boolean exceptioncheck = false;

	String FullName;
	public static String MobileNumber;

	// public static final String OFFER_CAR_POOL = "OFFER_CAR_POOL";
	// public static final String OFFER_SHARE_CAB = "OFFER_SHARE_CAB";

	@Override
	protected void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);

		setContentView(R.layout.activity_offer_cab_pool_share);

		RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.homepagerl);
		relativeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

			}
		});

		SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
		FullName = mPrefs.getString("FullName", "");
		MobileNumber = mPrefs.getString("MobileNumber", "");

		GoogleAnalytics analytics = GoogleAnalytics
				.getInstance(OfferPoolOrCabShare.this);
		Tracker tracker = analytics
				.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);

		// All subsequent hits will be send with screen name = "main screen"
		tracker.setScreenName("OfferPoolOrCabShare");
		UniversalDrawer drawer = new UniversalDrawer(this, tracker);
		drawer.createDrawer();

		tvViewpagerHeading = (TextView) findViewById(R.id.tvViewpagerHeading);
		// viewHome = findViewById(R.id.viewHome);
		offercabridetextview = (TextView) findViewById(R.id.offercabridetextview);

		setPagger();

		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.offercabridell);
		linearLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String screentoopen = getIntent()
						.getStringExtra("screentoopen");
				if (screentoopen.equals(HomeCarPoolActivity.OFFER_CAR_POOL)) {
					Intent mainIntent = new Intent(OfferPoolOrCabShare.this,
							HomeActivity.class);
					mainIntent.putExtra("screentoopen",
							HomeActivity.HOME_ACTIVITY_CAR_POOL);
					startActivityForResult(mainIntent, 500);
					overridePendingTransition(R.anim.slide_in_right,
							R.anim.slide_out_left);
				} else if (screentoopen
						.equals(HomeCarPoolActivity.OFFER_SHARE_CAB)) {
					Intent mainIntent = new Intent(OfferPoolOrCabShare.this,
							HomeActivity.class);
					mainIntent.putExtra("screentoopen",
							HomeActivity.HOME_ACTIVITY_SHARE_CAB);
					startActivityForResult(mainIntent, 500);
					overridePendingTransition(R.anim.slide_in_right,
							R.anim.slide_out_left);
				}
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		playAnimation = true;
		if (!isRunning) {

			String screentoopen = getIntent().getStringExtra("screentoopen");

			if (screentoopen.equals(HomeCarPoolActivity.OFFER_CAR_POOL)) {
				offercabridetextview.setText("Offer ride");
				tvViewpagerHeading
						.setText("If you have open ride invitation(s), they will appear here");
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new ConnectionTaskForFetchCarPool()
							.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					new ConnectionTaskForFetchCarPool().execute();
				}
			} else if (screentoopen.equals(HomeCarPoolActivity.OFFER_SHARE_CAB)) {
				offercabridetextview.setText("Start a cab share");
				tvViewpagerHeading
						.setText("If you have open cab share invitation(s), they will appear here");
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new ConnectionTaskForFetchPool()
							.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					new ConnectionTaskForFetchPool().execute();
				}
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		playAnimation = false;
	}

	// Set ViewPagger Adapter
	private void setPagger() {
		viewPagerHome = (ViewPager) findViewById(R.id.viewPagerHome);

		// Get Rides
		mFragmentStatePagerAdapter = new FragmentStatePagerAdapter(
				getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return arrayRideDetailsModels.size();
			}

			@Override
			public Fragment getItem(int position) {

				return HomeRidePageFragment.newInstance(arrayRideDetailsModels
						.get(position),
						getIntent().getStringExtra("screentoopen"));

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

				if (index < (arrayRideDetailsModels.size() - 1)) {
					viewPagerHome.setCurrentItem(index + 1);

				} else
					viewPagerHome.setCurrentItem(0);
				if (playAnimation)

					swipeAutomaticViewPagger();

			}
		}, 4000);
	}

	private void ConnectionTaskForFetchPoolPostExecute() {
		String response = "";
		String msg = "";
		JSONObject obj = null;
		try {
			obj = new JSONObject(rideInvitationsResponse);
			response = obj.getString("status");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// 07-30 16:42:37.665: D/poolresponse(13451): {status:"fail",
		// message:"No Records Found"}

		if (response.equalsIgnoreCase("fail")) {
			// Toast.makeText(HomeActivity.this, ""+msg,
			// Toast.LENGTH_LONG).show();
			// tvViewpagerHeading.setVisibility(View.GONE);
			circlePageIndicator.setVisibility(View.GONE);
			// viewHome.setVisibility(View.GONE);
			arrayRideDetailsModels.clear();

			mFragmentStatePagerAdapter.notifyDataSetChanged();

		}

		else {

			if (response.equalsIgnoreCase("success")) {
				// tvViewpagerHeading.setVisibility(View.VISIBLE);

				circlePageIndicator.setVisibility(View.VISIBLE);
				// viewHome.setVisibility(View.VISIBLE);

				try {
					arrayRideDetailsModels.clear();
					Gson gson = new Gson();

					ArrayList<RideDetailsModel> arrayRideLocal = gson.fromJson(
							obj.getJSONArray("data").toString(),
							new TypeToken<ArrayList<RideDetailsModel>>() {
							}.getType());

					if (arrayRideLocal.size() > 0) {

						for (int i = 0; i < arrayRideLocal.size(); i++) {

							arrayRideDetailsModels.add(arrayRideLocal.get(i));

						}
					}

					// if(arrayRideDetailsModels.size()>0){
					// for (int i = 0; i < arrayRideDetailsModels.size(); i++) {
					//
					//
					// OwnerName.add(arrayRideDetailsModels.get(i).getOwnerName());
					// FromShortName.add(arrayRideDetailsModels.get(i)
					// .getFromShortName());
					// ToShortName.add(arrayRideDetailsModels.get(i)
					// .getToShortName());
					// TravelDate.add(arrayRideDetailsModels.get(i)
					// .getTravelDate());
					// TravelTime.add(arrayRideDetailsModels.get(i)
					// .getTravelTime());
					// Seat_Status.add(arrayRideDetailsModels.get(i)
					// .getSeat_Status());
					// imagename.add(arrayRideDetailsModels.get(i).getImagename());
					// }
					// }
					Log.d("MyRidesActivity",
							"GSON pools : "
									+ gson.toJson(arrayRideDetailsModels)
											.toString());

					mFragmentStatePagerAdapter.notifyDataSetChanged();

					viewPagerHome.setCurrentItem(0);

					if (arrayRideDetailsModels.size() > 1) {

						swipeAutomaticViewPagger();

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}

	// ///////

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
				Toast.makeText(OfferPoolOrCabShare.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (rideInvitationsResponse.contains("Unauthorized Access")) {
				Log.e("HomeActivity",
						"rideInvitationsResponse Unauthorized Access");
				Toast.makeText(OfferPoolOrCabShare.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			ConnectionTaskForFetchPoolPostExecute();
			isRunning = false;
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
				rideInvitationsResponse = stringBuilder
						.append(bufferedStrChunk).toString();
			}

			Log.d("rideInvitationsResponse", "" + stringBuilder.toString()
					+ " mobileNumber : " + MobileNumber);
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
				Toast.makeText(OfferPoolOrCabShare.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (rideInvitationsResponse.contains("Unauthorized Access")) {
				Log.e("HomeActivity",
						"rideInvitationsResponse Unauthorized Access");
				Toast.makeText(OfferPoolOrCabShare.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			ConnectionTaskForFetchPoolPostExecute();
			isRunning = false;
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
				rideInvitationsResponse = stringBuilder
						.append(bufferedStrChunk).toString();
			}

			Log.d("rideInvitationsResponse", "" + stringBuilder.toString()
					+ " mobileNumber : " + MobileNumber);
		}
	}

}
