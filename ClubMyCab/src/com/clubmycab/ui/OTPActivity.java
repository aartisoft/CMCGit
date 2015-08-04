package com.clubmycab.ui;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.affle.affleinapptracker.AffleInAppTracker;
import com.clubmycab.PhoneListener;
import com.clubmycab.R;
import com.clubmycab.SmsReciever;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;

public class OTPActivity extends Activity {

	TextView otphardtext;
	TextView enterotp;
	EditText otpedittext;
	Button resendotp;
	Button continuewithotp;

//	String FullName;
	// String MobileNumberstr;

	String verifyotpresp;
	String resendotpresp;

	boolean exceptioncheck = false;
	private String from, fullName, mobNum, regId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_otp);

		try {
			from = getIntent().getExtras().getString("from");
			mobNum = getIntent().getExtras().getString("mobnum");

			fullName = getIntent().getExtras().getString("fullname");
			regId = getIntent().getExtras().getString("regid");
		} catch (Exception e) {

		}

		Log.d("OTPActivity", "onCreate from : " + from + " mobNum : " + mobNum
				+ " fullName : " + fullName + " regId : " + regId);
		setOTPListener();

		// Check if Internet present
		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					OTPActivity.this);
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

		otphardtext = (TextView) findViewById(R.id.otphardtext);
		enterotp = (TextView) findViewById(R.id.enterotp);
		otpedittext = (EditText) findViewById(R.id.otpedittext);
		resendotp = (Button) findViewById(R.id.resendotp);
		continuewithotp = (Button) findViewById(R.id.continuewithotp);

		enterotp.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		otphardtext.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));
		otpedittext.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));
		resendotp.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));
		continuewithotp.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

//		SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
//		FullName = mPrefs.getString("FullName", "");
//		mobNum = mPrefs.getString("MobileNumber", "");

		resendotp.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new ConnectionTaskForResendOTP()
							.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					new ConnectionTaskForResendOTP().execute();
				}

			}
		});

		continuewithotp.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (from.equalsIgnoreCase("login")) {

					if (otpedittext.getText().toString().trim().length() > 0) {

						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							new ConnectionTaskForVerifyOTPLogin()
									.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						} else {
							new ConnectionTaskForVerifyOTPLogin().execute();
						}

					} else {

						Toast.makeText(getApplicationContext(),
								"Please enter OTP", Toast.LENGTH_LONG).show();
					}

				} else if (from.equalsIgnoreCase("reg")) {
					if (otpedittext.getText().toString().trim().length() > 0) {

						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							new ConnectionTaskForVerifyOTP()
									.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						} else {
							new ConnectionTaskForVerifyOTP().execute();
						}

					} else {

						Toast.makeText(getApplicationContext(),
								"Please enter OTP", Toast.LENGTH_LONG).show();
					}
				}

			}
		});

		Toast.makeText(
				OTPActivity.this,
				"We will try to automatically verfiy your OTP, if you want you can enter it manually",
				Toast.LENGTH_LONG).show();
	}

	// /////// FOR REGISTRATION

	private class ConnectionTaskForVerifyOTP extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(OTPActivity.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionVerifyOTP mAuth1 = new AuthenticateConnectionVerifyOTP();
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
				Toast.makeText(OTPActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (verifyotpresp.equalsIgnoreCase("SUCCESS")) {
				
				SharedPreferences sharedPreferences = getSharedPreferences(
						"FacebookData", 0);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putString("verifyotp", "true");
				// editor.commit();

				editor.putString("FullName", fullName);
				editor.putString("MobileNumber", mobNum);
				editor.commit();

				// Intent mainIntent = new Intent(OTPActivity.this,
				// HomeActivity.class);
				Intent mainIntent = new Intent(OTPActivity.this,
						FavoriteLocationsAcivity.class);
				mainIntent.putExtra("NotFromRegistration", false);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
				finish();

			} else if (verifyotpresp.equalsIgnoreCase("OTPEXPIRE")) {
				Toast.makeText(OTPActivity.this,
						"Entered OTP has expired. Please click resend OTP",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(OTPActivity.this, "Entered OTP is not valid",
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	public class AuthenticateConnectionVerifyOTP {

		public AuthenticateConnectionVerifyOTP() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl + "/verifyotp.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
					"MobileNumber", mobNum);
			BasicNameValuePair singleusepasswordBasicNameValuePair = new BasicNameValuePair(
					"singleusepassword", otpedittext.getText().toString()
							.trim());

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MobileNumberBasicNameValuePair);
			nameValuePairList.add(singleusepasswordBasicNameValuePair);

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
				verifyotpresp = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("verifyotpresp", "" + verifyotpresp);
		}
	}

	// FOR LOGIN
	private class ConnectionTaskForVerifyOTPLogin extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(OTPActivity.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionVerifyOTPLogin mAuth1 = new AuthenticateConnectionVerifyOTPLogin();
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
				Toast.makeText(OTPActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (verifyotpresp.equalsIgnoreCase("SUCCESS")) {
				
				Hashtable<String, Object> extraParams = new Hashtable<String, Object>();
				extraParams.put("FullName", fullName);
				extraParams.put("MobileNumber",mobNum);
				AffleInAppTracker.inAppTrackerViewName(OTPActivity.this, "OTPActivity", "OTP verified", "User Registered", extraParams);

				SharedPreferences sharedPreferences = getSharedPreferences(
						"FacebookData", 0);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putString("verifyotp", "true");
				// editor.commit();

				editor.putString("FullName", fullName);
				editor.putString("MobileNumber", mobNum);
				editor.commit();

				Intent mainIntent = new Intent(OTPActivity.this,
						HomeActivity.class);
				mainIntent.putExtra("from", "normal");
				mainIntent.putExtra("message", "null");
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
				finish();

			} else if (verifyotpresp.equalsIgnoreCase("OTPEXPIRE")) {
				Toast.makeText(OTPActivity.this,
						"Entered OTP has expired. Please click resend OTP",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(OTPActivity.this, "Entered OTP is not valid",
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	public class AuthenticateConnectionVerifyOTPLogin {

		public AuthenticateConnectionVerifyOTPLogin() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/verifyloginotp.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
					"MobileNumber", mobNum);
			BasicNameValuePair singleusepasswordBasicNameValuePair = new BasicNameValuePair(
					"singleusepassword", otpedittext.getText().toString()
							.trim());

			BasicNameValuePair DeviceTokenBasicNameValuePair = new BasicNameValuePair(
					"DeviceToken", regId);
			BasicNameValuePair platformBasicNameValuePair = new BasicNameValuePair(
					"Platform", "A");

			Log.d("MobileNumber", "mobNum " + mobNum + " signleusepasss "
					+ otpedittext.getText().toString().trim() + " DeviceToken "
					+ regId);
			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MobileNumberBasicNameValuePair);
			nameValuePairList.add(singleusepasswordBasicNameValuePair);
			nameValuePairList.add(DeviceTokenBasicNameValuePair);
			nameValuePairList.add(platformBasicNameValuePair);

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
				verifyotpresp = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("verifyotpresp", "" + verifyotpresp);
		}
	}

	// ////////////////////
	// ///////

	private class ConnectionTaskForResendOTP extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(OTPActivity.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionResendOTP mAuth1 = new AuthenticateConnectionResendOTP();
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
				Toast.makeText(OTPActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (resendotpresp.equalsIgnoreCase("FAILURE")) {
				Toast.makeText(OTPActivity.this,
						"Something went wrong please try again.",
						Toast.LENGTH_SHORT).show();
			}

		}
	}

	public class AuthenticateConnectionResendOTP {

		public AuthenticateConnectionResendOTP() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl + "/resendotp.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
					"MobileNumber", mobNum);

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
				resendotpresp = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("resendotpresp", "" + resendotpresp);
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

	private void setOTPListener() {
		Log.d("EnterOTP", "setOTPListener");
		try {
			SmsReciever smsReceiver = new SmsReciever();

			smsReceiver.setOnPhoneListener(new PhoneListener() {
				@Override
				public void onOtpConfirmed(String target) {
					Log.d("EnterOTP", "onOtpConfirmed : " + target);
					otpedittext.setText(target);
					continuewithotp.performClick();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
