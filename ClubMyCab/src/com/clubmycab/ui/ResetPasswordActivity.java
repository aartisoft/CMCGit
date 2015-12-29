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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.R;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;

public class ResetPasswordActivity extends Activity {
	Button Send, Change;
	LinearLayout toplayout, Botttomlayout;
	TextView mobtxt, otptxt, newpwdtxt, confirmpwdtxt;
	EditText mobilenumber, otp, newpwd, confirmpwd, countrycoderesetpass;
	String result, otps, pwd, res;
	
	boolean exceptioncheck = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resetpassword);

		// Check if Internet present
		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					ResetPasswordActivity.this);
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

		

		Send = (Button) findViewById(R.id.resetpwdsendbtn);
		toplayout = (LinearLayout) findViewById(R.id.resetpwdtoplayout);
		Botttomlayout = (LinearLayout) findViewById(R.id.resetpwdbottom);
		mobilenumber = (EditText) findViewById(R.id.resetpwdnumberedittext);
		countrycoderesetpass = (EditText) findViewById(R.id.countrycoderesetpass);
		otp = (EditText) findViewById(R.id.resetpwdotpedittext);
		newpwd = (EditText) findViewById(R.id.resetpwdnewpwdedittext);
		confirmpwd = (EditText) findViewById(R.id.resetpwdconfirmedittext);
		Change = (Button) findViewById(R.id.resetpwdchangebtn);

		Change.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		Send.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		mobilenumber.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		countrycoderesetpass.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		otp.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		newpwd.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		confirmpwd.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));

		Send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mobilenumber.getText().toString().trim().isEmpty()) {

					mobilenumber.requestFocus();

					AlertDialog.Builder builder = new AlertDialog.Builder(
							ResetPasswordActivity.this);

					builder.setMessage("Please enter mobile number");
					builder.setPositiveButton("OK", null);
					AlertDialog dialog = builder.show();
					TextView messageText = (TextView) dialog
							.findViewById(android.R.id.message);
					messageText.setGravity(Gravity.CENTER);
					dialog.show();

				} else if (mobilenumber.getText().toString().trim().length() < 10) {

					mobilenumber.requestFocus();
					AlertDialog.Builder builder = new AlertDialog.Builder(
							ResetPasswordActivity.this);

					builder.setMessage("Please enter valid mobile number");
					builder.setPositiveButton("OK", null);
					AlertDialog dialog = builder.show();
					TextView messageText = (TextView) dialog
							.findViewById(android.R.id.message);
					messageText.setGravity(Gravity.CENTER);
					dialog.show();

				} else if (!(mobilenumber.getText().toString().substring(0, 1)
						.matches("[7-9]"))) {

					mobilenumber.requestFocus();

					AlertDialog.Builder builder = new AlertDialog.Builder(
							ResetPasswordActivity.this);
					builder.setMessage("Please enter a valid mobile number");
					builder.setPositiveButton("OK", null);
					AlertDialog dialog = builder.show();
					TextView messageText = (TextView) dialog
							.findViewById(android.R.id.message);
					messageText.setGravity(Gravity.CENTER);
					dialog.show();

				}

				else {
					// successfull for all
					if (!isOnline()) {

						AlertDialog.Builder builder = new AlertDialog.Builder(
								ResetPasswordActivity.this);
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
						new ConnectionTaskForSendOTP().execute();

					}

				}

			}
		});

		Change.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (otp.getText().toString().trim().isEmpty()) {

					otp.requestFocus();

					AlertDialog.Builder builder = new AlertDialog.Builder(
							ResetPasswordActivity.this);

					builder.setMessage("Please enter otp");
					builder.setPositiveButton("OK", null);
					AlertDialog dialog = builder.show();
					TextView messageText = (TextView) dialog
							.findViewById(android.R.id.message);
					messageText.setGravity(Gravity.CENTER);
					dialog.show();

				} else if (newpwd.getText().toString().trim().isEmpty()) {

					newpwd.requestFocus();

					AlertDialog.Builder builder = new AlertDialog.Builder(
							ResetPasswordActivity.this);

					builder.setMessage("Please enter ur new Password");
					builder.setPositiveButton("OK", null);
					AlertDialog dialog = builder.show();
					TextView messageText = (TextView) dialog
							.findViewById(android.R.id.message);
					messageText.setGravity(Gravity.CENTER);
					dialog.show();

				} else if (confirmpwd.getText().toString().trim().isEmpty()) {

					confirmpwd.requestFocus();

					AlertDialog.Builder builder = new AlertDialog.Builder(
							ResetPasswordActivity.this);

					builder.setMessage("Please enter ur confirm Password");
					builder.setPositiveButton("OK", null);
					AlertDialog dialog = builder.show();
					TextView messageText = (TextView) dialog
							.findViewById(android.R.id.message);
					messageText.setGravity(Gravity.CENTER);
					dialog.show();

				}

				else {
					// successfull for all
					if (!isOnline()) {

						AlertDialog.Builder builder = new AlertDialog.Builder(
								ResetPasswordActivity.this);
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
						if (newpwd.getText().toString()
								.equals(confirmpwd.getText().toString())) {

							Log.d("all change set", "all set");
							otps = otp.getText().toString().trim();
							pwd = newpwd.getText().toString().trim();
							new ConnectionTaskForResetPassword().execute();

						} else {
							Toast.makeText(getApplicationContext(),
									"New & confirm passwords do not match!",
									Toast.LENGTH_LONG).show();
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

	private class ConnectionTaskForResetPassword extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(ResetPasswordActivity.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionResetpwd mAuth1 = new AuthenticateConnectionResetpwd();
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
				Toast.makeText(ResetPasswordActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (result.equalsIgnoreCase("FAILURE")) {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						ResetPasswordActivity.this);

				builder.setMessage("Invalid Mobile number or OTP");
				builder.setPositiveButton("OK", null);
				AlertDialog dialog = builder.show();
				TextView messageText = (TextView) dialog
						.findViewById(android.R.id.message);
				messageText.setGravity(Gravity.CENTER);
				dialog.show();

			} else {
				Toast.makeText(getApplicationContext(),
						"Your password has been changed!", Toast.LENGTH_LONG)
						.show();
				ResetPasswordActivity.this.finish();
			}
		}

		public class AuthenticateConnectionResetpwd {

			public AuthenticateConnectionResetpwd() {

			}

			public void connection() throws Exception {

				// Connect to google.com
				HttpClient httpClient = new DefaultHttpClient();
				String url_select = GlobalVariables.ServiceUrl
						+ "/verifyresetpasswordotp.php";

				HttpPost httpPost = new HttpPost(url_select);
				BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
						"MobileNumber", countrycoderesetpass.getText()
								.toString().trim()
								+ mobilenumber.getText().toString().trim());
				BasicNameValuePair resetpasswordotpBasicNameValuePair = new BasicNameValuePair(
						"resetpasswordotp", otps);
				BasicNameValuePair userpasswordBasicNameValuePair = new BasicNameValuePair(
						"userpassword", pwd);

				List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
				nameValuePairList.add(MobileNumberBasicNameValuePair);
				nameValuePairList.add(resetpasswordotpBasicNameValuePair);
				nameValuePairList.add(userpasswordBasicNameValuePair);

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

	private class ConnectionTaskForSendOTP extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(ResetPasswordActivity.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionSendOTP mAuth1 = new AuthenticateConnectionSendOTP();
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
				Toast.makeText(ResetPasswordActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (res.equalsIgnoreCase("FAILURE")) {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						ResetPasswordActivity.this);

				builder.setMessage("Mobile Number does not exist");
				builder.setPositiveButton("OK", null);
				AlertDialog dialog = builder.show();
				TextView messageText = (TextView) dialog
						.findViewById(android.R.id.message);
				messageText.setGravity(Gravity.CENTER);
				dialog.show();

			} else {
				Toast.makeText(getApplicationContext(),
						"OTP has been sent to your mobile!", Toast.LENGTH_LONG)
						.show();
				toplayout.setVisibility(View.GONE);
				Botttomlayout.setVisibility(View.VISIBLE);
			}
		}

		public class AuthenticateConnectionSendOTP {

			public AuthenticateConnectionSendOTP() {

			}

			public void connection() throws Exception {

				// Connect to google.com
				HttpClient httpClient = new DefaultHttpClient();
				String url_select = GlobalVariables.ServiceUrl
						+ "/sendresetpasswordotp.php";

				HttpPost httpPost = new HttpPost(url_select);
				BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
						"MobileNumber", countrycoderesetpass.getText()
								.toString().trim()
								+ mobilenumber.getText().toString().trim());

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
					res = stringBuilder.append(bufferedStrChunk).toString();
				}

				Log.d("res", "" + stringBuilder.toString());
			}
		}
	}
}
