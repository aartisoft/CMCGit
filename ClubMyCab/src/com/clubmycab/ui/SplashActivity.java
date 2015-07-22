package com.clubmycab.ui;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.R;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class SplashActivity extends Activity {

	String FullName;
	String MobileNumber;
	String verifyotp;

	String LastRegisteredAppVersion;
	String AppVersion;
	String forceupdateversion;
	String poolresponse;
	private TextView tvRandom;

	boolean exceptioncheck = false;
	private ArrayList<String> ans1;
	private long timeStart;
	int delay=4;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		timeStart= System.currentTimeMillis();

		
		ans1 = new ArrayList<String>();
		ans1.add("Sharing a cab with friends can reduce cost to as low as Rs. 2/km when 4 friends travel in a hatchback");
		ans1.add("Sharing works best with people you trust - create a \"club\" of such people who usually travel same routes as you and start sharing rides");
		ans1.add("Clubs are your closed groups where only known people are added	");
		ans1.add("You can create clubs and add friends or refer friends to a club that you are a member of");
		
		ans1.add("Creating clubs makes it easier to share rides with people who travel similar routes");


		
		ans1.add("Bigger clubs increase chances of ride sharing. Add more friends to your clubs");
		ans1.add("After adding friends to clubs, make sure to ask them to join ClubMyCab. Sharing begins only when they join");
		ans1.add("When you add friends to a club, we send them a message on your behalf. But, nothing like telling them about ClubMyCab yourself");
		ans1.add("Start rides even with a few friends - let the word spread");
		ans1.add("Keep clubs focussed to friends who travel same route as you");
		ans1.add("Name your clubs such to let your club members know what the purpose is. Or just choose a fun name");
		ans1.add("Add a profile image - helps your friends identify you");
		
		
		ans1.add("Add your home, office/college location and upto 3 more preferences in Settings to make ride creation easy");
		ans1.add("When you want to share a ride, first check your notifications to see if any club member has invited you for one and seats are available");
		ans1.add("If no existing rides are available, create your own and invite club members");

		ans1.add("See suggested routes and location of other members to decide if the route suits you  ");
		
		
		ans1.add("When the ride starts, we let you know.");
		ans1.add("Track the location of the cab on ride page to see the progress of the journey");
		ans1.add("You can communicate with ride members using inbuilt Instant Messenger");
		ans1.add("Fare split calculation is done on basis of distance traveled assuming same destination");
		
		
		ans1.add("We show you multiple choices of cabs to be booked with estimated time and cost");
		ans1.add("You can sort the cabs on Book a Cab page by nearest and cheapest. Click the icons that appear on top");
		ans1.add("Book the cab when you are ready to leave - ClubMyCab is about sharing on-the-go");
		ans1.add("You can book a cab even if you are riding alone");
		
		ans1.add("We do not share phone numbers of club members till they join a ride with you to protect privacy");
		ans1.add("Instant Messenger history is deleted on ride completion");
		ans1.add("\"Here, I am\" can be used to let your loved ones know where you are through the journey. Activate this when you want");

		
		tvRandom=(TextView)findViewById(R.id.tvRandom);
		
		Random r = new Random();
		int answer = r.nextInt(ans1.size()-1) + 0;
		tvRandom.setText(ans1.get(answer));
		

		// Check if Internet present
		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					SplashActivity.this);
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

		SharedPreferences mPrefs1 = getSharedPreferences("FacebookData", 0);
		FullName = mPrefs1.getString("FullName", "");
		MobileNumber = mPrefs1.getString("MobileNumber", "");
		verifyotp = mPrefs1.getString("verifyotp", "");
		LastRegisteredAppVersion = mPrefs1.getString(
				"LastRegisteredAppVersion", "");

		PackageInfo pInfo = null;
		try {

			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			AppVersion = pInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.d("Splash", "LastRegisteredAppVersion : "
				+ LastRegisteredAppVersion + " AppVersion : " + AppVersion);

		if ((FullName.isEmpty() || FullName == null)
				&& (MobileNumber.isEmpty() || MobileNumber == null)) {

			Intent mainIntent = new Intent(SplashActivity.this,
					AboutPagerFragmentActivity.class);
			mainIntent.putExtra("mStartedFrom", "mStartedFrom");
			startActivity(mainIntent);
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
			finish();

			// Intent mainIntent = new Intent(Splash.this,
			// LoginViaPhoneNumber.class);
			// startActivityForResult(mainIntent, 500);
			// overridePendingTransition(R.anim.slide_in_right,
			// R.anim.slide_out_left);
			// finish();
		} else {

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForForceUpdate()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new ConnectionTaskForForceUpdate().execute();
			}
		}
	}

	// /////

	private class ConnectionTaskForForceUpdate extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionForceUpdate mAuth1 = new AuthenticateConnectionForceUpdate();
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
				Toast.makeText(SplashActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			Double latestappversion = Double.parseDouble(AppVersion.toString()
					.trim());
			Double forceappversion = Double.parseDouble(forceupdateversion
					.toString().trim());
			if (latestappversion < forceappversion) {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						SplashActivity.this);
				builder.setMessage("Newer version of the app is available. You need to update before proceeding");
				builder.setCancelable(false);
				builder.setPositiveButton("Update",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								final String appPackageName = getPackageName();
								try {
									startActivity(new Intent(
											Intent.ACTION_VIEW,
											Uri.parse("market://details?id="
													+ appPackageName)));
								} catch (android.content.ActivityNotFoundException anfe) {
									startActivity(new Intent(
											Intent.ACTION_VIEW,
											Uri.parse("http://play.google.com/store/apps/details?id="
													+ appPackageName)));
								}
								finish();

							}
						});
				builder.setNegativeButton("Later",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								finish();
							}
						});
				AlertDialog dialog = builder.show();
				TextView messageText = (TextView) dialog
						.findViewById(android.R.id.message);
				messageText.setGravity(Gravity.CENTER);
				dialog.show();

			} else {
				if (verifyotp.equalsIgnoreCase("false")) {
					Intent mainIntent = new Intent(SplashActivity.this,
							OTPActivity.class);
					startActivityForResult(mainIntent, 500);
					overridePendingTransition(R.anim.slide_in_right,
							R.anim.slide_out_left);
					finish();
				} else {

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new ConnectionTaskForFetchPool()
								.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					} else {
						new ConnectionTaskForFetchPool().execute();
					}
				}
			}

		}

	}

	public class AuthenticateConnectionForceUpdate {

		public AuthenticateConnectionForceUpdate() {

		}

		public void connection() throws Exception {

			if (LastRegisteredAppVersion.toString().trim().isEmpty()
					|| LastRegisteredAppVersion.toString().trim()
							.equalsIgnoreCase("")
					|| LastRegisteredAppVersion.toString().trim() == null) {

				GoogleCloudMessaging gcm = null;
				String regid = null;
				String PROJECT_NUMBER = "145246375713";

				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging
								.getInstance(getApplicationContext());
					}
					regid = gcm.register(PROJECT_NUMBER);
					Log.d("GCM", "Device registered, ID is " + regid);

					HttpClient httpClient = new DefaultHttpClient();
					String url_select = GlobalVariables.ServiceUrl
							+ "/updateregid.php";

					HttpPost httpPost = new HttpPost(url_select);

					BasicNameValuePair MembersNumberBasicNameValuePair = new BasicNameValuePair(
							"MobileNumber", MobileNumber.toString().trim());
					BasicNameValuePair DeviceTokenBasicNameValuePair = new BasicNameValuePair(
							"DeviceToken", regid);

					List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
					nameValuePairList.add(MembersNumberBasicNameValuePair);
					nameValuePairList.add(DeviceTokenBasicNameValuePair);

					UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
							nameValuePairList);
					httpPost.setEntity(urlEncodedFormEntity);
					HttpResponse httpResponse = httpClient.execute(httpPost);

					SharedPreferences sharedPreferences = getSharedPreferences(
							"FacebookData", 0);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putString("LastRegisteredAppVersion", AppVersion
							.toString().trim());
					editor.commit();

				} catch (Exception e) {
					Log.e(" registerDevice()", e.getMessage());
				}
			} else {
				Double savedversion = Double
						.parseDouble(LastRegisteredAppVersion.toString().trim());
				Double latestversion = Double.parseDouble(AppVersion.toString()
						.trim());

				Log.d("Splash", "savedversion : " + savedversion
						+ " latestversion : " + latestversion);

				if (latestversion > savedversion) {

					GoogleCloudMessaging gcm = null;
					String regid = null;
					String PROJECT_NUMBER = "145246375713";

					try {
						if (gcm == null) {
							gcm = GoogleCloudMessaging
									.getInstance(getApplicationContext());
						}
						regid = gcm.register(PROJECT_NUMBER);
						Log.d("GCM", "Device registered, ID is " + regid);

						HttpClient httpClient = new DefaultHttpClient();
						String url_select = GlobalVariables.ServiceUrl
								+ "/updateregid.php";

						HttpPost httpPost = new HttpPost(url_select);

						BasicNameValuePair MembersNumberBasicNameValuePair = new BasicNameValuePair(
								"MobileNumber", MobileNumber.toString().trim());
						BasicNameValuePair DeviceTokenBasicNameValuePair = new BasicNameValuePair(
								"DeviceToken", regid);

						List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
						nameValuePairList.add(MembersNumberBasicNameValuePair);
						nameValuePairList.add(DeviceTokenBasicNameValuePair);

						UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
								nameValuePairList);
						httpPost.setEntity(urlEncodedFormEntity);
						HttpResponse httpResponse = httpClient
								.execute(httpPost);

						SharedPreferences sharedPreferences = getSharedPreferences(
								"FacebookData", 0);
						SharedPreferences.Editor editor = sharedPreferences
								.edit();
						editor.putString("LastRegisteredAppVersion", AppVersion
								.toString().trim());
						editor.commit();

					} catch (Exception e) {
						Log.e(" registerDevice()", e.getMessage());
					}
				}

			}

			HttpClient httpClient1 = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/changeuserstatus.php";

			HttpPost httpPost1 = new HttpPost(url_select);

			List<NameValuePair> nameValuePairList1 = new ArrayList<NameValuePair>();

			BasicNameValuePair CabIdValuePair = new BasicNameValuePair("CabId",
					"");
			BasicNameValuePair MemberNumberValuePair = new BasicNameValuePair(
					"MemberNumber", MobileNumber.toString().trim());
			BasicNameValuePair chatstatusValuePair = new BasicNameValuePair(
					"chatstatus", "offline");
			BasicNameValuePair IsOwnerValuePair = new BasicNameValuePair(
					"IsOwner", "");

			nameValuePairList1.add(CabIdValuePair);
			nameValuePairList1.add(MemberNumberValuePair);
			nameValuePairList1.add(chatstatusValuePair);
			nameValuePairList1.add(IsOwnerValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity1 = new UrlEncodedFormEntity(
					nameValuePairList1);
			httpPost1.setEntity(urlEncodedFormEntity1);
			HttpResponse httpResponse1 = httpClient1.execute(httpPost1);

			InputStream inputStream = httpResponse1.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				forceupdateversion = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("forceupdateversion", "" + forceupdateversion);

		}
	}

	private class ConnectionTaskForFetchPool extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(SplashActivity.this);

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

			if (dialog.isShowing()) {
				dialog.dismiss();
			}

			if (exceptioncheck) {
				exceptioncheck = false;
				Toast.makeText(SplashActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}
			


			SharedPreferences sharedPreferences = getSharedPreferences(
					"HomeActivityDisplayRides", 0);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putBoolean("DisplayRides", true);
			editor.commit();
			
			
			long timeEnd=System.currentTimeMillis();
			long diff=timeEnd-timeStart;
			
			long Sec = TimeUnit.MILLISECONDS.toSeconds(diff);
			int diffSeconds = (int) Sec;
			
if(diffSeconds>=delay){
			Intent mainIntent = new Intent(SplashActivity.this,
					HomeActivity.class);
			mainIntent.putExtra("PoolResponseSplash", poolresponse);
			mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivityForResult(mainIntent, 500);
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
}
else{
int milliseconds=delay-diffSeconds;	
	new Handler().postDelayed(new Runnable(){
         @Override
         public void run() {
        	 
        		Intent mainIntent = new Intent(SplashActivity.this,
    					HomeActivity.class);
    			mainIntent.putExtra("PoolResponseSplash", poolresponse);
    			mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
    					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
    			startActivityForResult(mainIntent, 500);
    			overridePendingTransition(R.anim.slide_in_right,
    					R.anim.slide_out_left);
        	 
        	 
         }
     },milliseconds*1000 );
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
					+ "/FetchMyPools.php";
			HttpPost httpPost = new HttpPost(url_select11);
			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
					"MobileNumber", MobileNumber.toString().trim());

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

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}
}
