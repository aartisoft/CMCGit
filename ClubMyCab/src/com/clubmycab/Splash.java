package com.clubmycab;

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

import android.app.Activity;
import android.app.AlertDialog;
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
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class Splash extends Activity {

	String FullName;
	String MobileNumber;
	String verifyotp;

	String LastRegisteredAppVersion;
	String AppVersion;
	String forceupdateversion;

	UrlConstant checkurl;
	boolean exceptioncheck = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		// Check if Internet present
		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(Splash.this);
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

		checkurl = new UrlConstant();

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

			Intent mainIntent = new Intent(Splash.this, MainActivity.class);
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
				Toast.makeText(Splash.this,
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
						Splash.this);
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
					Intent mainIntent = new Intent(Splash.this, EnterOTP.class);
					startActivityForResult(mainIntent, 500);
					overridePendingTransition(R.anim.slide_in_right,
							R.anim.slide_out_left);
					finish();
				} else {

					Intent mainIntent = new Intent(Splash.this, HomePage.class);
					mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivityForResult(mainIntent, 500);
					overridePendingTransition(R.anim.slide_in_right,
							R.anim.slide_out_left);
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
					Log.i("GCM", "Device registered, ID is " + regid);

					HttpClient httpClient = new DefaultHttpClient();
					String url_select = checkurl.GetServiceUrl()
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
						Log.i("GCM", "Device registered, ID is " + regid);

						HttpClient httpClient = new DefaultHttpClient();
						String url_select = checkurl.GetServiceUrl()
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
			String url_select = checkurl.GetServiceUrl()
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

			Log.e("forceupdateversion", "" + forceupdateversion);

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
