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
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
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

public class LoginActivity extends Activity {

	EditText countrycodelogin;
	EditText numberedittext;
	EditText numberpasswordedittext;

	Button loginnumberbtn;
	Button numberregisterbtn;
	TextView resetpwdtext;

	String result;

	String FullName;
	String MobileNumber;
	String regid = null;

	
	boolean exceptioncheck = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_via_phone_number);

		// Check if Internet present
		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					LoginActivity.this);
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

		

		countrycodelogin = (EditText) findViewById(R.id.countrycodelogin);
		numberedittext = (EditText) findViewById(R.id.numberedittext);
		numberpasswordedittext = (EditText) findViewById(R.id.numberpasswordedittext);

		loginnumberbtn = (Button) findViewById(R.id.loginnumberbtn);
		numberregisterbtn = (Button) findViewById(R.id.numberregisterbtn);

		resetpwdtext = (TextView) findViewById(R.id.resetpwdtext);

		countrycodelogin.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));
		numberedittext.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));
		numberpasswordedittext.setTypeface(Typeface.createFromAsset(
				getAssets(), "NeutraText-Light.ttf"));
		loginnumberbtn.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));
		numberregisterbtn.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));
		resetpwdtext.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Light.ttf"));

		resetpwdtext.setPaintFlags(resetpwdtext.getPaintFlags()
				| Paint.UNDERLINE_TEXT_FLAG);

		resetpwdtext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Intent mainIntent = new Intent(LoginActivity.this,
						ResetPasswordActivity.class);
				mainIntent.putExtra("source", "phonenumber");
				LoginActivity.this.startActivity(mainIntent);

			}
		});

		numberregisterbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Intent mainIntent = new Intent(LoginActivity.this,RegistrationActivity.class);
						//TermsAndConditionsActivity.class);
				mainIntent.putExtra("mob",numberedittext.getText().toString().trim());
				LoginActivity.this.startActivity(mainIntent);

			}
		});

		loginnumberbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (numberedittext.getText().toString().trim().isEmpty()) {

					numberedittext.requestFocus();

					AlertDialog.Builder builder = new AlertDialog.Builder(
							LoginActivity.this);

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
							LoginActivity.this);

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
							LoginActivity.this);
					builder.setMessage("Please enter a valid mobile number");
					builder.setPositiveButton("OK", null);
					AlertDialog dialog = builder.show();
					TextView messageText = (TextView) dialog
							.findViewById(android.R.id.message);
					messageText.setGravity(Gravity.CENTER);
					dialog.show();

				}// else if (numberpasswordedittext.getText().toString().trim()
//						.isEmpty()) {
//
//					numberpasswordedittext.requestFocus();
//
//					AlertDialog.Builder builder = new AlertDialog.Builder(
//							LoginActivity.this);
//
//					builder.setMessage("Please enter password");
//					builder.setPositiveButton("OK", null);
//					AlertDialog dialog = builder.show();
//					TextView messageText = (TextView) dialog
//							.findViewById(android.R.id.message);
//					messageText.setGravity(Gravity.CENTER);
//					dialog.show();
//
//				}

				else {
					// successfull for all
					if (!isOnline()) {

						AlertDialog.Builder builder = new AlertDialog.Builder(
								LoginActivity.this);
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

	private class ConnectionTaskForLogin extends AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(
				LoginActivity.this);

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
				Toast.makeText(LoginActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (result.equalsIgnoreCase("login error")) {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						LoginActivity.this);

				builder.setMessage("You are not registered, please click on Register");
				builder.setPositiveButton("OK", null);
				AlertDialog dialog = builder.show();
				TextView messageText = (TextView) dialog
						.findViewById(android.R.id.message);
				messageText.setGravity(Gravity.CENTER);
				dialog.show();

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
//
//				SharedPreferences sharedPreferences = getSharedPreferences(
//						"FacebookData", 0);
//				SharedPreferences.Editor editor = sharedPreferences.edit();
//				editor.putString("FullName", FullName);
//				editor.putString("MobileNumber", MobileNumber);
//				editor.commit();

//				Intent mainIntent = new Intent(LoginActivity.this,
//						HomeActivity.class);
//				mainIntent.putExtra("from", "normal");
//				mainIntent.putExtra("message", "null");
//				startActivityForResult(mainIntent, 500);
//				overridePendingTransition(R.anim.slide_in_right,
//						R.anim.slide_out_left);
//				finish();
				
				Intent mainIntent = new Intent(LoginActivity.this,
						OTPActivity.class);
				mainIntent.putExtra("from", "login");
				mainIntent.putExtra("fullname", FullName);
				mainIntent.putExtra("mobnum", MobileNumber);
				
				mainIntent.putExtra("regid", regid);


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
					"MobileNumber", countrycodelogin.getText().toString()
							.trim()
							+ numberedittext.getText().toString().trim());
			
//			BasicNameValuePair PasswordBasicNameValuePair = new BasicNameValuePair(
//					"Password", "");
//			numberpasswordedittext.getText().toString()
//							.trim());

			BasicNameValuePair DeviceTokenBasicNameValuePair = new BasicNameValuePair(
					"DeviceToken", regid);
			BasicNameValuePair platformBasicNameValuePair = new BasicNameValuePair(
					"Platform", "A");

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MobileNumberBasicNameValuePair);
		//	nameValuePairList.add(PasswordBasicNameValuePair);
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
				result = stringBuilder.append(bufferedStrChunk).toString();
			}

			Log.d("result", "" + stringBuilder.toString());
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("LoginViaPhonenumber", "onActivityResult : " + resultCode);
		if (requestCode == 1) {
			if (resultCode == Activity.RESULT_OK) {
				Intent mainIntent = new Intent(LoginActivity.this,
						RegistrationActivity.class);
				mainIntent.putExtra("source", "phonenumber");
				LoginActivity.this.startActivity(mainIntent);
			}
		}
	}
}
