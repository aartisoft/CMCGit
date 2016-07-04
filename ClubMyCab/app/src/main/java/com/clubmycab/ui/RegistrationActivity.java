package com.clubmycab.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.R;
import com.clubmycab.utility.AppConstants;
import com.clubmycab.utility.CustomDialog;
import com.clubmycab.utility.FontTypeface;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;

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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RegistrationActivity extends Activity {

	TextView registerheadertxt;
	TextView fullnametxt;
	TextView passwordtxt;
	TextView emailtxt;
	TextView confirmpasswordtxt;
	TextView mobiletxt;
	TextView referralcodetxt;

	EditText fullnameedittext;
	EditText emailedittext;
	EditText passwordedittext;
	EditText confirmpasswordedittext;
	EditText mobileedittext;
	EditText countrycode;
	EditText referralcodeedittext;
	EditText numberedittext;
	String MobileNumber;
	Button registerbtn;
	Button loginnumberbtn;

	Calendar myCalendar = Calendar.getInstance();

	String FullName;

	String result;
	private String mobNo = "";
	boolean exceptioncheck = false;
	private String regid;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		numberedittext = (EditText) findViewById(R.id.numberedittext);
		loginnumberbtn = (Button) findViewById(R.id.loginnumberbtn);
		loginnumberbtn.setTypeface(Typeface.createFromAsset(getAssets(), AppConstants.HELVITICA));
		// Check if Internet present
		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					RegistrationActivity.this);
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

		try {

			mobNo = getIntent().getExtras().getString("mob");

		} catch (Exception e) {
			mobNo = "";

		}

		registerheadertxt = (TextView) findViewById(R.id.registerheadertxt);
		fullnametxt = (TextView) findViewById(R.id.fullnametxt);
		emailtxt = (TextView) findViewById(R.id.emailtxt);
		passwordtxt = (TextView) findViewById(R.id.passwordtxt);
		confirmpasswordtxt = (TextView) findViewById(R.id.confirmpasswordtxt);
		mobiletxt = (TextView) findViewById(R.id.mobiletxt);
		referralcodetxt = (TextView) findViewById(R.id.referralcodetxt);

		fullnameedittext = (EditText) findViewById(R.id.fullnameedittext);
		emailedittext = (EditText) findViewById(R.id.emailedittext);
		passwordedittext = (EditText) findViewById(R.id.passwordedittext);
		confirmpasswordedittext = (EditText) findViewById(R.id.confirmpasswordedittext);
		mobileedittext = (EditText) findViewById(R.id.mobileedittext);
		mobileedittext.setText(mobNo);
		countrycode = (EditText) findViewById(R.id.countrycode);
		referralcodeedittext = (EditText) findViewById(R.id.referralcodeedittext);

		registerbtn = (Button) findViewById(R.id.registerbtn);
	

		registerheadertxt.setTypeface(Typeface.createFromAsset(getAssets(),
				AppConstants.HELVITICA));

		fullnametxt.setTypeface(Typeface.createFromAsset(getAssets(),
				AppConstants.HELVITICA));
		((TextView)findViewById(R.id.tvAlreadyReg)).setTypeface(FontTypeface.getTypeface(RegistrationActivity.this,AppConstants.HELVITICA));
		fullnameedittext.setTypeface(Typeface.createFromAsset(getAssets(),
				AppConstants.HELVITICA));

		passwordtxt.setTypeface(Typeface.createFromAsset(getAssets(),
				AppConstants.HELVITICA));
		passwordedittext.setTypeface(Typeface.createFromAsset(getAssets(),
				AppConstants.HELVITICA));

		emailtxt.setTypeface(Typeface.createFromAsset(getAssets(),
				AppConstants.HELVITICA));
		emailedittext.setTypeface(Typeface.createFromAsset(getAssets(),
				AppConstants.HELVITICA));

		confirmpasswordtxt.setTypeface(Typeface.createFromAsset(getAssets(),
				AppConstants.HELVITICA));
		confirmpasswordedittext.setTypeface(Typeface.createFromAsset(
				getAssets(), AppConstants.HELVITICA));

		mobiletxt.setTypeface(Typeface.createFromAsset(getAssets(),
				AppConstants.HELVITICA));
		mobileedittext.setTypeface(Typeface.createFromAsset(getAssets(),
				AppConstants.HELVITICA));
		countrycode.setTypeface(Typeface.createFromAsset(getAssets(),
				AppConstants.HELVITICA));

		registerbtn.setTypeface(Typeface.createFromAsset(getAssets(),
				AppConstants.HELVITICA));

		referralcodetxt.setTypeface(Typeface.createFromAsset(getAssets(),
				AppConstants.HELVITICA));
		referralcodeedittext.setTypeface(Typeface.createFromAsset(getAssets(),
				AppConstants.HELVITICA));

		registerbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				// TODO Auto-generated method

				if (fullnameedittext.getText().toString().trim().isEmpty()) {

					fullnameedittext.requestFocus();

					AlertDialog.Builder builder = new AlertDialog.Builder(
							RegistrationActivity.this);

					builder.setMessage("Please enter Full Name");
					builder.setPositiveButton("OK", null);
					AlertDialog dialog = builder.show();
					TextView messageText = (TextView) dialog
							.findViewById(android.R.id.message);
					messageText.setGravity(Gravity.CENTER);
					dialog.show();

				} else if (mobileedittext.getText().toString().trim().isEmpty()) {

					mobileedittext.requestFocus();

					AlertDialog.Builder builder = new AlertDialog.Builder(
							RegistrationActivity.this);

					builder.setMessage("Please enter mobile number");
					builder.setPositiveButton("OK", null);
					AlertDialog dialog = builder.show();
					TextView messageText = (TextView) dialog
							.findViewById(android.R.id.message);
					messageText.setGravity(Gravity.CENTER);
					dialog.show();

				} else if (mobileedittext.getText().toString().trim().length() < 10) {

					mobileedittext.requestFocus();
					AlertDialog.Builder builder = new AlertDialog.Builder(
							RegistrationActivity.this);

					builder.setMessage("Please enter valid mobile number");
					builder.setPositiveButton("OK", null);
					AlertDialog dialog = builder.show();
					TextView messageText = (TextView) dialog
							.findViewById(android.R.id.message);
					messageText.setGravity(Gravity.CENTER);
					dialog.show();

				} else if (!(mobileedittext.getText().toString()
						.substring(0, 1).matches("[7-9]"))) {

					mobileedittext.requestFocus();

					AlertDialog.Builder builder = new AlertDialog.Builder(
							RegistrationActivity.this);
					builder.setMessage("Please enter a valid mobile number");
					builder.setPositiveButton("OK", null);
					AlertDialog dialog = builder.show();
					TextView messageText = (TextView) dialog
							.findViewById(android.R.id.message);
					messageText.setGravity(Gravity.CENTER);
					dialog.show();

				} else if (emailedittext.getText().toString().trim().isEmpty()) {

					emailedittext.requestFocus();

					AlertDialog.Builder builder = new AlertDialog.Builder(
							RegistrationActivity.this);
					builder.setMessage("Please enter e-mail address");
					builder.setPositiveButton("OK", null);
					AlertDialog dialog = builder.show();
					TextView messageText = (TextView) dialog
							.findViewById(android.R.id.message);
					messageText.setGravity(Gravity.CENTER);
					dialog.show();

				} else if (!emailedittext.getText().toString().trim()
						.contains("@")
						|| !emailedittext.getText().toString().trim()
								.contains(".")) {

					emailedittext.requestFocus();

					AlertDialog.Builder builder = new AlertDialog.Builder(
							RegistrationActivity.this);
					builder.setMessage("Please enter a valid e-mail address");
					builder.setPositiveButton("OK", null);
					AlertDialog dialog = builder.show();
					TextView messageText = (TextView) dialog
							.findViewById(android.R.id.message);
					messageText.setGravity(Gravity.CENTER);
					dialog.show();
				}

				// } else if (passwordedittext.getText().toString().trim()
				// .isEmpty()) {
				//
				// passwordedittext.requestFocus();
				//
				// AlertDialog.Builder builder = new AlertDialog.Builder(
				// RegistrationActivity.this);
				//
				// builder.setMessage("Please enter Password");
				// builder.setPositiveButton("OK", null);
				// AlertDialog dialog = builder.show();
				// TextView messageText = (TextView) dialog
				// .findViewById(android.R.id.message);
				// messageText.setGravity(Gravity.CENTER);
				// dialog.show();
				//
				// }
				//
				// else if (confirmpasswordedittext.getText().toString().trim()
				// .isEmpty()) {
				//
				// confirmpasswordedittext.requestFocus();
				//
				// AlertDialog.Builder builder = new AlertDialog.Builder(
				// RegistrationActivity.this);
				//
				// builder.setMessage("Please re-type the Password");
				// builder.setPositiveButton("OK", null);
				// AlertDialog dialog = builder.show();
				// TextView messageText = (TextView) dialog
				// .findViewById(android.R.id.message);
				// messageText.setGravity(Gravity.CENTER);
				// dialog.show();
				//
				// } else if (!(passwordedittext.getText().toString().trim()
				// .equals(confirmpasswordedittext.getText().toString()))) {
				//
				// confirmpasswordedittext.requestFocus();
				//
				// AlertDialog.Builder builder = new AlertDialog.Builder(
				// RegistrationActivity.this);
				//
				// builder.setMessage("Password Mismatch");
				// builder.setPositiveButton("OK", null);
				// AlertDialog dialog = builder.show();
				// TextView messageText = (TextView) dialog
				// .findViewById(android.R.id.message);
				// messageText.setGravity(Gravity.CENTER);
				// dialog.show();
				//
				// }
				else {
					// successfull for all
					if (!isOnline()) {

						AlertDialog.Builder builder = new AlertDialog.Builder(
								RegistrationActivity.this);
						builder.setTitle("Internet Connection Error");
						builder.setMessage("iShareRyde requires Internet connection");
						builder.setPositiveButton("OK", null);
						AlertDialog dialog = builder.show();
						TextView messageText = (TextView) dialog
								.findViewById(android.R.id.message);
						messageText.setGravity(Gravity.CENTER);
						dialog.show();

						return;
					} else {

						Log.d("all set", "all set");

						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							new ConnectionTaskForRegister()
									.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						} else {
							new ConnectionTaskForRegister().execute();
						}
					}

				}

			}
		});

		loginnumberbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (numberedittext.getText().toString().trim().isEmpty()) {

					numberedittext.requestFocus();

					AlertDialog.Builder builder = new AlertDialog.Builder(
							RegistrationActivity.this);

					builder.setMessage("Please enter mobile number");
					builder.setPositiveButton("OK", null);
					AlertDialog dialog = builder.show();
					TextView messageText = (TextView) dialog
							.findViewById(android.R.id.message);
					messageText.setGravity(Gravity.CENTER);
					dialog.show();

				} else if (numberedittext.getText().toString().trim().length() < 10) {

					numberedittext.requestFocus();
					AlertDialog.Builder builder = new AlertDialog.Builder(
							RegistrationActivity.this);

					builder.setMessage("Please enter valid mobile number");
					builder.setPositiveButton("OK", null);
					AlertDialog dialog = builder.show();
					TextView messageText = (TextView) dialog
							.findViewById(android.R.id.message);
					messageText.setGravity(Gravity.CENTER);
					dialog.show();

				} else if (!(numberedittext.getText().toString()
						.substring(0, 1).matches("[7-9]"))) {

					numberedittext.requestFocus();

					AlertDialog.Builder builder = new AlertDialog.Builder(
							RegistrationActivity.this);
					builder.setMessage("Please enter a valid mobile number");
					builder.setPositiveButton("OK", null);
					AlertDialog dialog = builder.show();
					TextView messageText = (TextView) dialog
							.findViewById(android.R.id.message);
					messageText.setGravity(Gravity.CENTER);
					dialog.show();

				}// else if (numberpasswordedittext.getText().toString().trim()
				// .isEmpty()) {
				//
				// numberpasswordedittext.requestFocus();
				//
				// AlertDialog.Builder builder = new AlertDialog.Builder(
				// RegistrationActivity.this);
				//
				// builder.setMessage("Please enter password");
				// builder.setPositiveButton("OK", null);
				// AlertDialog dialog = builder.show();
				// TextView messageText = (TextView) dialog
				// .findViewById(android.R.id.message);
				// messageText.setGravity(Gravity.CENTER);
				// dialog.show();
				//
				// }

				else {
					// successfull for all
					if (!isOnline()) {

						AlertDialog.Builder builder = new AlertDialog.Builder(
								RegistrationActivity.this);
						builder.setTitle("Internet Connection Error");
						builder.setMessage("iShareRyde requires Internet connection");
						builder.setPositiveButton("OK", null);
						AlertDialog dialog = builder.show();
						TextView messageText = (TextView) dialog
								.findViewById(android.R.id.message);
						messageText.setGravity(Gravity.CENTER);
						dialog.show();

						return;
					} else {

						Log.d("all set", "all set");

						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							new ConnectionTaskForLogin()
									.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						} else {
							new ConnectionTaskForLogin().execute();
						}
					}

				}

			}
		});
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	// ///////

	private class ConnectionTaskForRegister extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(
				RegistrationActivity.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionRegister mAuth1 = new AuthenticateConnectionRegister();
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
				Toast.makeText(RegistrationActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (result != null && result.length() > 0
					&& result.contains("Unauthorized Access")) {
				Log.e("RegistrationActivity", "result Unauthorized Access");
				Toast.makeText(RegistrationActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			try {
				if (result != null && !result.isEmpty()) {
					JSONObject jsonObject = new JSONObject(result);

					if (jsonObject.get("status").toString()
							.equalsIgnoreCase("SUCCESS")) {
						if(!jsonObject.isNull("mobileNumber") && !TextUtils.isEmpty(jsonObject.getString("mobileNumber"))) {
                            CustomDialog.showDialog(RegistrationActivity.this,jsonObject.getString("message"));
							SharedPreferences sharedPreferences = getSharedPreferences(
									"FacebookData", 0);
							SharedPreferences.Editor editor = sharedPreferences
									.edit();
							//editor.putString("FullName", userName.toString().trim());
							//editor.putString("Email", userEmail.toString().trim());
							editor.putString("MobileNumber", jsonObject.getString("mobileNumber").toString().trim());
							//editor.putString("ProfilePicFb", profilePicUrl);
							editor.commit();

						}else{
							PackageInfo pInfo = null;
							try {
								pInfo = getPackageManager().getPackageInfo(
										getPackageName(), 0);
							} catch (NameNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							String version = pInfo.versionName;

							SharedPreferences sharedPreferences = getSharedPreferences(
									"FacebookData", 0);
							SharedPreferences.Editor editor = sharedPreferences
									.edit();
							editor.putString("FullName", fullnameedittext.getText()
									.toString().trim());
							editor.putString("MobileNumber", "0091"
									+ mobileedittext.getText().toString().trim());
							editor.putString("Email", emailedittext.getText()
									.toString().trim());
							editor.putString("verifyotp", "false");
							editor.putString("LastRegisteredAppVersion", version);
							editor.commit();

							Intent i = new Intent(RegistrationActivity.this,
									OTPActivity.class);
							i.putExtra("from", "reg");
							i.putExtra("mobnum", "0091"
									+ mobileedittext.getText().toString().trim());

							i.putExtra("fullname", fullnameedittext.getText()
									.toString().trim());
							i.putExtra("regid", "");
							i.putExtra("isregisterwithphone", true);
							i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
									| Intent.FLAG_ACTIVITY_CLEAR_TASK);
							startActivity(i);

						}

					} else {

						Toast.makeText(RegistrationActivity.this,
								jsonObject.get("message").toString(),
								Toast.LENGTH_LONG).show();

						// AlertDialog.Builder builder = new
						// AlertDialog.Builder(
						// RegistrationActivity.this);
						//
						// builder.setMessage("Mobile number already exists. Please try to login or register with a different mobile number");
						// builder.setPositiveButton("OK", null);
						// AlertDialog dialog = builder.show();
						// TextView messageText = (TextView) dialog
						// .findViewById(android.R.id.message);
						// messageText.setGravity(Gravity.CENTER);
						// dialog.show();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public class AuthenticateConnectionRegister {

		public AuthenticateConnectionRegister() {

		}

		public void connection() throws Exception {

			GoogleCloudMessaging gcm = null;
			String regid = null;
			String PROJECT_NUMBER = GlobalVariables.GCMProjectKey;

			try {
				if (gcm == null) {
					gcm = GoogleCloudMessaging
							.getInstance(getApplicationContext());
				}
				regid = gcm.register(PROJECT_NUMBER);
				Log.d("GCM", "Device registered, ID is " + regid);
			} catch (Exception e) {
				Log.e(" registerDevice()", e.getMessage());
			}

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/userregister.php";

			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair FullNameBasicNameValuePair = new BasicNameValuePair(
					"FullName", fullnameedittext.getText().toString().trim());
			// BasicNameValuePair PasswordBasicNameValuePair = new
			// BasicNameValuePair(
			// "Password", passwordedittext.getText().toString().trim());
			BasicNameValuePair PasswordBasicNameValuePair = new BasicNameValuePair(
					"Password", "");

			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
					"MobileNumber", "0091"
							+ mobileedittext.getText().toString().trim());
			BasicNameValuePair DeviceTokenBasicNameValuePair = new BasicNameValuePair(
					"DeviceToken", regid);
			BasicNameValuePair EmailBasicNameValuePair = new BasicNameValuePair(
					"Email", emailedittext.getText().toString().trim());
			BasicNameValuePair GenderBasicNameValuePair = new BasicNameValuePair(
					"Gender", "");
			BasicNameValuePair DOBBasicNameValuePair = new BasicNameValuePair(
					"DOB", "");
			BasicNameValuePair platformBasicNameValuePair = new BasicNameValuePair(
					"Platform", "A");

			Log.d("MobileNumber", "0091"
					+ mobileedittext.getText().toString().trim());

			Log.d("DeviceToken", regid);

			Log.d("Email", emailedittext.getText().toString().trim());

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(FullNameBasicNameValuePair);
			nameValuePairList.add(PasswordBasicNameValuePair);
			nameValuePairList.add(MobileNumberBasicNameValuePair);
			nameValuePairList.add(DeviceTokenBasicNameValuePair);
			nameValuePairList.add(EmailBasicNameValuePair);
			nameValuePairList.add(GenderBasicNameValuePair);
			nameValuePairList.add(DOBBasicNameValuePair);
			nameValuePairList.add(platformBasicNameValuePair);
			if (!referralcodeedittext.getText().toString().trim().isEmpty()) {
				BasicNameValuePair referralCodeBasicNameValuePair = new BasicNameValuePair(
						"referralCode", referralcodeedittext.getText()
								.toString().trim());
				nameValuePairList.add(referralCodeBasicNameValuePair);
			}

			String authString = regid
					+ emailedittext.getText().toString().trim()
					+ fullnameedittext.getText().toString().trim()
					+ "0091"
					+ mobileedittext.getText().toString().trim() + "A"
					+ referralcodeedittext.getText().toString().trim();
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

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

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				result = stringBuilder.append(bufferedStrChunk).toString();
			}

			Log.d("result", "" + stringBuilder.toString());
		}
	}

	private class ConnectionTaskForLogin extends AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(RegistrationActivity.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionLogin mAuth1 = new AuthenticateConnectionLogin();
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
				Toast.makeText(RegistrationActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (result.equalsIgnoreCase("login error")) {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						RegistrationActivity.this);

				builder.setMessage("You are not registered, please click on Register");
				builder.setPositiveButton("OK", null);
				AlertDialog dialog = builder.show();
				TextView messageText = (TextView) dialog
						.findViewById(android.R.id.message);
				messageText.setGravity(Gravity.CENTER);
				dialog.show();

			} else if (result.contains("Unauthorized Access")) {
				Log.e("RegistrationActivity", "result Unauthorized Access");
				Toast.makeText(RegistrationActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			} else {

				JSONArray subArray = null;
				try {

					subArray = new JSONArray(result);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				for (int i = 0; i < subArray.length(); i++) {

					try {
						FullName = subArray.getJSONObject(i)
								.getString("FullName").toString();
						MobileNumber = subArray.getJSONObject(i)
								.getString("MobileNumber").toString();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				Log.d("FullName", "" + FullName);
				Log.d("MobileNumber", "" + MobileNumber);

				Intent mainIntent = new Intent(RegistrationActivity.this,
						OTPActivity.class);
				mainIntent.putExtra("from", "login");
				mainIntent.putExtra("fullname", FullName);
				mainIntent.putExtra("mobnum", MobileNumber);
				mainIntent.putExtra("regid", regid);
				mainIntent.putExtra("isregisterwithphone", true);
				startActivity(mainIntent);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
				finish();
			}
		}

	}

	public class AuthenticateConnectionLogin {

		public AuthenticateConnectionLogin() {

		}

		public void connection() throws Exception {

			GoogleCloudMessaging gcm = null;
			String PROJECT_NUMBER = GlobalVariables.GCMProjectKey;

			try {
				if (gcm == null) {
					gcm = GoogleCloudMessaging
							.getInstance(getApplicationContext());
				}
				regid = gcm.register(PROJECT_NUMBER);
				Log.d("GCM", "Device registered, ID is " + regid);
			} catch (Exception e) {
				Log.e(" registerDevice()", e.getMessage());
			}

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl + "/login.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
					"MobileNumber", "0091"
					+ numberedittext.getText().toString().trim());

			// BasicNameValuePair PasswordBasicNameValuePair = new
			// BasicNameValuePair(
			// "Password", "");
			// numberpasswordedittext.getText().toString()
			// .trim());

			BasicNameValuePair DeviceTokenBasicNameValuePair = new BasicNameValuePair(
					"DeviceToken", regid);
			BasicNameValuePair platformBasicNameValuePair = new BasicNameValuePair(
					"Platform", "A");

			String authString = regid + "0091"
					+ numberedittext.getText().toString().trim() + "A";
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MobileNumberBasicNameValuePair);
			// nameValuePairList.add(PasswordBasicNameValuePair);
			nameValuePairList.add(DeviceTokenBasicNameValuePair);
			nameValuePairList.add(platformBasicNameValuePair);
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

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				result = stringBuilder.append(bufferedStrChunk).toString();
			}

			Log.d("result", "" + stringBuilder.toString());
		}
	}

	@Override
	public void onBackPressed() {
		Intent mainIntent = new Intent(
				RegistrationActivity.this,
				AppLoginScreen.class);
		startActivity(mainIntent);
		overridePendingTransition(R.anim.slide_in_right,
				R.anim.slide_out_left);
		finish();
	}
}
