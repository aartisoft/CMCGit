package com.clubmycab.ui;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.R;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class RegistrationActivity extends Activity {

	TextView registerheadertxt;
	TextView fullnametxt;
	TextView passwordtxt;
	TextView emailtxt;
	TextView confirmpasswordtxt;
	TextView mobiletxt;

	EditText fullnameedittext;
	EditText emailedittext;
	EditText passwordedittext;
	EditText confirmpasswordedittext;
	EditText mobileedittext;
	EditText countrycode;

	Button registerbtn;

	Calendar myCalendar = Calendar.getInstance();

	String FullName;

	String result;

	boolean exceptioncheck = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);

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

		registerheadertxt = (TextView) findViewById(R.id.registerheadertxt);
		fullnametxt = (TextView) findViewById(R.id.fullnametxt);
		emailtxt = (TextView) findViewById(R.id.emailtxt);
		passwordtxt = (TextView) findViewById(R.id.passwordtxt);
		confirmpasswordtxt = (TextView) findViewById(R.id.confirmpasswordtxt);
		mobiletxt = (TextView) findViewById(R.id.mobiletxt);

		fullnameedittext = (EditText) findViewById(R.id.fullnameedittext);
		emailedittext = (EditText) findViewById(R.id.emailedittext);
		passwordedittext = (EditText) findViewById(R.id.passwordedittext);
		confirmpasswordedittext = (EditText) findViewById(R.id.confirmpasswordedittext);
		mobileedittext = (EditText) findViewById(R.id.mobileedittext);
		countrycode = (EditText) findViewById(R.id.countrycode);

		registerbtn = (Button) findViewById(R.id.registerbtn);

		registerheadertxt.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));

		fullnametxt.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		fullnameedittext.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

		passwordtxt.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		passwordedittext.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

		emailtxt.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		emailedittext.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

		confirmpasswordtxt.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		confirmpasswordedittext.setTypeface(Typeface.createFromAsset(
				getAssets(), "NeutraText-Light.ttf"));

		mobiletxt.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		mobileedittext.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));
		countrycode.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

		registerbtn.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

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

				} else if (passwordedittext.getText().toString().trim()
						.isEmpty()) {

					passwordedittext.requestFocus();

					AlertDialog.Builder builder = new AlertDialog.Builder(
							RegistrationActivity.this);

					builder.setMessage("Please enter Password");
					builder.setPositiveButton("OK", null);
					AlertDialog dialog = builder.show();
					TextView messageText = (TextView) dialog
							.findViewById(android.R.id.message);
					messageText.setGravity(Gravity.CENTER);
					dialog.show();

				}

				else if (confirmpasswordedittext.getText().toString().trim()
						.isEmpty()) {

					confirmpasswordedittext.requestFocus();

					AlertDialog.Builder builder = new AlertDialog.Builder(
							RegistrationActivity.this);

					builder.setMessage("Please re-type the Password");
					builder.setPositiveButton("OK", null);
					AlertDialog dialog = builder.show();
					TextView messageText = (TextView) dialog
							.findViewById(android.R.id.message);
					messageText.setGravity(Gravity.CENTER);
					dialog.show();

				} else if (!(passwordedittext.getText().toString().trim()
						.equals(confirmpasswordedittext.getText().toString()))) {

					confirmpasswordedittext.requestFocus();

					AlertDialog.Builder builder = new AlertDialog.Builder(
							RegistrationActivity.this);

					builder.setMessage("Password Mismatch");
					builder.setPositiveButton("OK", null);
					AlertDialog dialog = builder.show();
					TextView messageText = (TextView) dialog
							.findViewById(android.R.id.message);
					messageText.setGravity(Gravity.CENTER);
					dialog.show();

				} else {
					// successfull for all
					if (!isOnline()) {

						AlertDialog.Builder builder = new AlertDialog.Builder(
								RegistrationActivity.this);
						builder.setTitle("Internet Connection Error");
						builder.setMessage("ClubMyCab requires Internet connection");
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

			if (result.toString().trim().equalsIgnoreCase("SUCCESS")) {

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
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putString("FullName", fullnameedittext.getText()
						.toString().trim());
				editor.putString("MobileNumber", countrycode.getText()
						.toString().trim()
						+ mobileedittext.getText().toString().trim());
				editor.putString("verifyotp", "false");
				editor.putString("LastRegisteredAppVersion", version);
				editor.commit();

				Intent i = new Intent(RegistrationActivity.this,
						OTPActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(i);

			} else {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						RegistrationActivity.this);

				builder.setMessage("Mobile Number Already Exists. Please try with different Mobile Number");
				builder.setPositiveButton("OK", null);
				AlertDialog dialog = builder.show();
				TextView messageText = (TextView) dialog
						.findViewById(android.R.id.message);
				messageText.setGravity(Gravity.CENTER);
				dialog.show();
			}
		}

	}

	public class AuthenticateConnectionRegister {

		public AuthenticateConnectionRegister() {

		}

		public void connection() throws Exception {

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
			BasicNameValuePair PasswordBasicNameValuePair = new BasicNameValuePair(
					"Password", passwordedittext.getText().toString().trim());
			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
					"MobileNumber", countrycode.getText().toString().trim()
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

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(FullNameBasicNameValuePair);
			nameValuePairList.add(PasswordBasicNameValuePair);
			nameValuePairList.add(MobileNumberBasicNameValuePair);
			nameValuePairList.add(DeviceTokenBasicNameValuePair);
			nameValuePairList.add(EmailBasicNameValuePair);
			nameValuePairList.add(GenderBasicNameValuePair);
			nameValuePairList.add(DOBBasicNameValuePair);
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
				result = stringBuilder.append(bufferedStrChunk).toString();
			}

			Log.d("result", "" + stringBuilder.toString());
		}
	}
}
