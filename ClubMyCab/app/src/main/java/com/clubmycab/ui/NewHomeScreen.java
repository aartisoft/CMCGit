package com.clubmycab.ui;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.CabApplication;
import com.clubmycab.R;
import com.clubmycab.asynctasks.GlobalAsyncTask;
import com.clubmycab.asynctasks.GlobalAsyncTask.AsyncTaskResultListener;
import com.clubmycab.fragment.CabsFragment;
import com.clubmycab.fragment.MyRidesFragment;
import com.clubmycab.fragment.RidesAvail;
import com.clubmycab.interfaces.CurrentLocatListenerRidesAvail;
import com.clubmycab.interfaces.CurrentLocationListener;
import com.clubmycab.model.RideDetailsModel;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.clubmycab.utility.SPreference;
import com.clubmycab.utility.slider.SlidingTabLayout;
import com.clubmycab.xmlhandler.FetchUnreadNotificationCountHandler;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewHomeScreen extends FragmentActivity implements
		AsyncTaskResultListener, OnClickListener,LocationListener{// Declaring Your View and
									// Variables
	public static final int OFFER_RIDE_REQUEST = 1001;
	public static final int RIDES_AVALIBLE = 0;
	public static final int MY_RIDES = 1;
	public static final int CABS = 2;
	private ViewPager pager;
	private HomeScreenPagerAdapter adapter;
	private SlidingTabLayout tabs;
	private CharSequence Titles[] = { "CarPool", "My Rides", "Cabs" };
	private int Numboftabs = 3;
	public static int CURRENT_TAB;
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
	private ImageView bookcabresultsort;
	private String PoolResponseSplash;
	private static final int LOCATION_REQUEST = 101;
	private LocationManager locationManager;
	public CurrentLocationListener fragmentCommunicator;
	public CurrentLocatListenerRidesAvail listenerRideAvailLoc;
	private CircleImageView drawerprofilepic;
	private String imagenameresp;
	private Bitmap mIcon11;
    private AlertDialog locationServiceDialog;

    public interface TestInterFace{
		public void testMethod(int value);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newhomescreen);
		Log.d("LifeCycle", "onCreateActiviy");
		registerReceiver(receiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
		getLocation();
		findViewById(R.id.flSearch).setVisibility(View.VISIBLE);

		SharedPreferences mPrefs1 = getSharedPreferences("QuitApplication", 0);
		boolean shouldQuitApp = mPrefs1.getBoolean("quitapplication", false);
		if (shouldQuitApp) {
			SharedPreferences.Editor editor = mPrefs1.edit();
			editor.putBoolean("quitapplication", false);
			editor.commit();
			finish();
		}
		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					NewHomeScreen.this);
			builder.setMessage("No Internet Connection. Please check and try again!");
			builder.setCancelable(false);

			builder.setPositiveButton("Retry",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							/*Intent intent = getIntent();
							intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

							finish();

							startActivity(intent);*/
							Intent mainIntent = new Intent(
									NewHomeScreen.this,
									NewHomeScreen.class);

							mainIntent.putExtra("comefrom", "comefrom");
							mainIntent.putExtra("cabID", "-1");// false condition can be improved

							mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
									| Intent.FLAG_ACTIVITY_CLEAR_TASK);
							startActivityForResult(mainIntent, 500);
							overridePendingTransition(R.anim.slide_in_right,
									R.anim.slide_out_left);

						}
					});

			builder.show();
			return;
		}

		Log.d("time", System.currentTimeMillis()+"");
        addNavigationDrawer();


		findViewById(R.id.bookcabresultsort).setOnClickListener(this);
		findViewById(R.id.ivRefersh).setOnClickListener(this);
		findViewById(R.id.flSearch).setOnClickListener(this);

		notificationimg = (ImageView) findViewById(R.id.notificationimg);
		unreadnoticountrl = (RelativeLayout) findViewById(R.id.unreadnoticountrl);
		unreadnoticount = (TextView) findViewById(R.id.unreadnoticount);
		bookcabresultsort = (ImageView) findViewById(R.id.bookcabresultsort);
		drawerprofilepic = (CircleImageView) findViewById(R.id.drawerprofilepic);

		pager = (ViewPager) findViewById(R.id.pager);

		pager.setOffscreenPageLimit(3);
		//pager.setOnPageChangeListener(onPageChangeListener);

		adapter = new HomeScreenPagerAdapter(getSupportFragmentManager(),
				Titles, Numboftabs);
		pager.setAdapter(adapter);
		tabs = (SlidingTabLayout) findViewById(R.id.tabs);
		tabs.setDistributeEvenly(true);
		tabs.setViewPager(pager);
		tabs.setOnPageChangeListener(onPageChangeListener);

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
        getUserData();
		findViewById(R.id.flNotifications).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tracker.send(new HitBuilders.EventBuilder().setCategory("Notification").setAction("Notification icon pressed").setLabel("Notification icon pressed").build());

				Intent mainIntent = new Intent(NewHomeScreen.this,
						NotificationListActivity.class);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);

			}
		});
		SharedPreferences mPrefs111 = getSharedPreferences("userimage", 0);
		String imagestr = mPrefs111.getString("imagestr", "");

		if (imagestr.isEmpty() || imagestr == null
				|| imagestr.equalsIgnoreCase("")) {

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
			drawerprofilepic.setImageBitmap(yourSelectedImage);
			if (yourSelectedImage != null) {
				yourSelectedImage = null;
			}
		}

		try{
			if(getIntent() != null){
				Bundle bundle = getIntent().getExtras();

				if((bundle.containsKey("comefrom") && bundle.containsKey("cabID"))
						|| bundle.containsKey("PoolResponseSplash")){
					//refreshData(getIntent());
					if(bundle.containsKey("PoolResponseSplash") && bundle.getString("PoolResponseSplash").equalsIgnoreCase("No Pool Created Yet!!")){
						// Do Nothing
					}else {
						pager.setCurrentItem(MY_RIDES);
					}

				}
				if(bundle.containsKey("notificationid")){
					NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
					manager.cancel(bundle.getInt("notificationid"));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int arg0) {
			CURRENT_TAB = arg0;
			switch (arg0) {
			case CABS:
				if(CabsFragment.getInstance() != null && CabsFragment.getInstance().isListVisible()){
					setSortIconVisible();
				}else {
					setSortIconGone();
					
				}
				if(MyRidesFragment.getInstance() != null){
					MyRidesFragment.getInstance().refreshDataOnPageChange();
				}
				if(CabsFragment.getInstance() != null){
					CabsFragment.getInstance().refreshDataOnPageChange();
				}
				findViewById(R.id.flSearch).setVisibility(View.GONE);

				break;
				
			case MY_RIDES:
				setSortIconGone();
				/*if(RidesAvail.getInstance() != null){
					RidesAvail.getInstance().collapseRideList();
				}*/
				findViewById(R.id.flSearch).setVisibility(View.GONE);

				break;
				
			case RIDES_AVALIBLE:
				setSortIconGone();
				if(MyRidesFragment.getInstance() != null){
					MyRidesFragment.getInstance().refreshDataOnPageChange();
				}
				findViewById(R.id.flSearch).setVisibility(View.VISIBLE);
				
				break;

			default:
				break;
			}
			
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}
	};


	private void addNavigationDrawer() {
		try{
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
		}catch (Exception e){
			e.printStackTrace();

		}
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		GlobalVariables.ActivityName = "HomeCarPoolActivity";

		/* PoolResponseSplash = getIntent().getStringExtra(
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
				pager.setCurrentItem(MY_RIDES);


			} else {
				fetchInvitations();
			}

		}*/
	}

	@Override
	protected void onPause() {
		super.onPause();

		playAnimation = false;
	}

	@Override
	public void onBackPressed() {

		if(CURRENT_TAB == CABS){
			if(CabsFragment.getInstance() != null){
				if(CabsFragment.getInstance().isMapVisible){
					CabsFragment.getInstance().hideMapView();
					return;
				}
			}
		}

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
			try{
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
			}catch (Exception e){
				e.printStackTrace();
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

			if (GlobalVariables.UnreadNotificationCount != null && GlobalVariables.UnreadNotificationCount.equalsIgnoreCase("0")) {

				unreadnoticountrl.setVisibility(View.GONE);

			} else {

				unreadnoticountrl.setVisibility(View.VISIBLE);
				unreadnoticount
						.setText(GlobalVariables.UnreadNotificationCount);
			}
		}else if (uniqueID.equals("userData")) {
			Log.d("OffersListActivity", "userData response : " + response);
			if(response == null){
				return;
			}

			if (response != null && response.length() > 0
					&& response.contains("Unauthorized Access")) {
				Log.e("OffersListActivity", "userData Unauthorized Access");
				Toast.makeText(NewHomeScreen.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			try {
				JSONObject jsonObject = new JSONObject(response);
				if (jsonObject.get("status").toString().equals("success")) {
					JSONObject jsonObject2 = new JSONObject(jsonObject.get("data").toString());
					if(!jsonObject2.isNull("type"))	{
						SPreference.getPref(NewHomeScreen.this).edit().putInt(SPreference.USER_TYPE,Integer.valueOf(jsonObject2.optString("type"))).commit();
					}
					// fetch user type

					/*if(!jsonObject2.isNull("referralCode") && !TextUtils.isEmpty(jsonObject2.getString("referralCode"))){
						findViewById(R.id.card_view2).setVisibility(View.VISIBLE);
						((TextView)findViewById(R.id.tvReferCodeNumber)).setText(jsonObject2.get("referralCode").toString());
					}else {
						findViewById(R.id.card_view2).setVisibility(View.GONE);
					}*/


				}

			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(NewHomeScreen.this, getResources().getString(R.string.exceptionstring), Toast.LENGTH_LONG).show();
			}
		}
	}
	
	public void setRupeeIcon(){
		bookcabresultsort.setVisibility(View.VISIBLE);
		bookcabresultsort.setImageDrawable(getResources().getDrawable(R.drawable.bookacab_sorticon_rupee));
		findViewById(R.id.ivRefersh).setVisibility(View.VISIBLE);

	}
	public void setTimeIcon(){
		bookcabresultsort.setVisibility(View.VISIBLE);
		bookcabresultsort.setImageDrawable(getResources().getDrawable(R.drawable.bookacab_sorticon_time));
		findViewById(R.id.ivRefersh).setVisibility(View.VISIBLE);
	}
	
	public void setSortIconVisible(){
		bookcabresultsort.setVisibility(View.VISIBLE);
		findViewById(R.id.ivRefersh).setVisibility(View.VISIBLE);

	}
	
	public void setSortIconGone(){
		bookcabresultsort.setVisibility(View.GONE);
		findViewById(R.id.ivRefersh).setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bookcabresultsort:
			if(CabsFragment.getInstance() != null){
				CabsFragment.getInstance().sortCabSerachList();
			}
			break;
			
		case R.id.ivRefersh:
			if(CabsFragment.getInstance() != null){
				CabsFragment.getInstance().refershCabList();
			}

			break;
		case R.id.flSearch:
			if(RidesAvail.getInstance() != null){
				RidesAvail.getInstance().showSearchDialog();
			}
			break;

		default:
			break;
		}
		
	}
	public class HomeScreenPagerAdapter extends FragmentStatePagerAdapter {

		CharSequence Titles[];
		int NumbOfTabs;

		public HomeScreenPagerAdapter(FragmentManager fm, CharSequence mTitles[],
				int mNumbOfTabsumb) {
			super(fm);
			this.Titles = mTitles;
			this.NumbOfTabs = mNumbOfTabsumb;
		}
		// This method return the fragment for the every position in the View Pager
		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				
				return RidesAvail.newInstance(null);

			case 1:
				Bundle bundle = null;
				if(getIntent() != null){
					bundle = getIntent().getExtras();
				}
				
				return MyRidesFragment.newInstance(bundle);
			case 2:
				return CabsFragment.newInstance(null);
			}
			return null;
		}
		// This method return the titles for the Tabs in the Tab Strip
		@Override
		public CharSequence getPageTitle(int position) {
			return Titles[position];
		}
		// This method return the Number of tabs for the tabs Strip
		@Override
		public int getCount() {
			return NumbOfTabs;
		}
	}
	
	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	// TODO Auto-generated method stub
	super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			if(requestCode == NewHomeScreen.OFFER_RIDE_REQUEST){// It is a bug it shoul OFFER_RIDE_REQUEST
				if(data != null && data.getExtras().containsKey("cabID")){
					pager.setCurrentItem(MY_RIDES);
					MyRidesFragment fragment = MyRidesFragment.newInstance(null);
					if(fragment != null){
						fragment.refershRideListData(data.getExtras().getString("cabID"));
					}
				}
		
			}
		}
	}*/
	
	public void refreshData(Intent data ){
		if(data != null && data.getExtras().containsKey("cabID")){
			pager.setCurrentItem(MY_RIDES);
			MyRidesFragment fragment = MyRidesFragment.newInstance(null);
			if(fragment != null){
				fragment.refershRideListData(data.getExtras().getString("cabID"));
			}
		}
	}
	
	public Location getLocation() {
		Location location = null;
		try {
			locationManager = (LocationManager)getSystemService(
					LOCATION_SERVICE);
			
			// getting GPS status
			boolean isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			boolean isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
                locationServiceDialog = new AlertDialog.Builder(NewHomeScreen.this).create();
                locationServiceDialog.setMessage("Please check your location services");
                locationServiceDialog.setCancelable(false);
				/*dialog.setPositiveButton("Retry",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(
									DialogInterface paramDialogInterface,
									int paramInt) {
								
								 * Intent intent = getActivity().getIntent();
								 * intent
								 * .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
								 * getActivity().finish();
								 * startActivity(intent);
								 

							}
						});*/
                locationServiceDialog.setButton("Setting", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent myIntent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(myIntent,
                                LOCATION_REQUEST);
                    }
                });
                locationServiceDialog.show();
				return null;
			} else {

				double lat = 0;
				double lng = 0;
				// get the location by gps
				if (isGPSEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER, 10000, 1, this);
						Log.d("GPS Enabled", "GPS Enabled");
						if (locationManager != null) {
							location = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								lat = location.getLatitude();
								lng = location.getLongitude();
								if(fragmentCommunicator != null)
									fragmentCommunicator.getLatLong(location);
								if(listenerRideAvailLoc !=null)
									listenerRideAvailLoc.getLatLong(location);

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
				/*if (mycurrentlocationobject != null)
					LocationAddress.getAddressFromLocation(
							mycurrentlocationobject.getLatitude(),
							mycurrentlocationobject.getLongitude(),
							getActivity(), new GeocoderHandler());*/

				Log.d("lat", "" + lat);
				Log.d("lng", "" + lng);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return location;
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
	// TODO Auto-generated method stub
	super.onActivityResult(arg0, arg1, arg2);
	 if( arg0 == LOCATION_REQUEST){
			getLocation();
	 }
	}

	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
			if(fragmentCommunicator !=null){
				fragmentCommunicator.getLatLong(location);
			}
			if(listenerRideAvailLoc !=null){
				listenerRideAvailLoc.getLatLong(location);
			}
			CabApplication.getInstance().setFirstLocation(location);
			Log.d("latlong", location.getLatitude()+","+location.getLongitude());
		}		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		
	}

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

			try{
				if (exceptioncheck) {
					exceptioncheck = false;
					Toast.makeText(NewHomeScreen.this,
							getResources().getString(R.string.exceptionstring),
							Toast.LENGTH_LONG).show();
					return;
				}

				if (imagenameresp == null) {

					drawerprofilepic.setImageResource(R.drawable.cabappicon);

				} else if (imagenameresp.contains("Unauthorized Access")) {
					Log.e("HomeActivity", "imagenameresp Unauthorized Access");
					Toast.makeText(NewHomeScreen.this,
							getResources().getString(R.string.exceptionstring),
							Toast.LENGTH_LONG).show();
					return;
				} else {

					drawerprofilepic.setImageBitmap(mIcon11);
				}
			}catch (Exception e){
				e.printStackTrace();
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
					"MobileNumber", MobileNumber);

			String authString = MobileNumber;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList11 = new ArrayList<NameValuePair>();
			nameValuePairList11.add(MobileNumberBasicNameValuePair11);
			nameValuePairList11.add(authValuePair);

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

			} else if (imagenameresp.contains("Unauthorized Access")) {

			} else {

				String url1 = GlobalVariables.ServiceUrl + "/ProfileImages/"
						+ imagenameresp;
				String urldisplay = url1.toString().trim();
				mIcon11 = null;
				String imgString = null;
				try {
					InputStream in = new URL(urldisplay).openStream();
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

	public byte[] getBytesFromBitmap(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
		return stream.toByteArray();
	}

	@Override
	protected void onDestroy() {
	super.onDestroy();
		try{
			CabApplication.getInstance().setFirstLocation(null);
			if(locationManager != null){
				locationManager.removeUpdates(this);
			}
			Log.d("LifeCycle", "onDestroyActiviy");
			adapter =null;
			if(receiver != null){
				unregisterReceiver(receiver);
			}
		}catch (Exception e){
			e.printStackTrace();
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

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {

                if(locationServiceDialog != null){
                    locationServiceDialog.dismiss();
                    getLocation();
                }

			}
		}
	};

	public void refreshMyRideData(){
		pager.setCurrentItem(MY_RIDES);
		if(MyRidesFragment.getInstance() != null){
            MyRidesFragment.getInstance().refershRideListData("");
        }
	}

	private void getUserData() {
		String endpoint = GlobalVariables.ServiceUrl + "/userData.php";
		String authString = MobileNumber;
		String params = "mobileNumber=" + MobileNumber + "&auth=" + GlobalMethods.calculateCMCAuthString(authString);
		Log.d("userData", "userData endpoint : " + endpoint + " params : " + params);
		new GlobalAsyncTask(NewHomeScreen.this, endpoint, params, null, NewHomeScreen.this, false, "userData", false);
	}

    public  void hideSearch(){
        findViewById(R.id.flSearch).setVisibility(View.GONE);
    }
    public  void showSearch(){
        findViewById(R.id.flSearch).setVisibility(View.VISIBLE);
    }

}
