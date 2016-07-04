package com.clubmycab.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.PhoneListener;
import com.clubmycab.R;
import com.clubmycab.SmsReciever;
import com.clubmycab.utility.AppConstants;
import com.clubmycab.utility.CustomDialog;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.clubmycab.utility.Utility;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class OTPActivity extends Activity implements View.OnClickListener{

	TextView otphardtext;
	TextView enterotp;
	EditText otpedittext;
	Button resendotp;
	Button continuewithotp;

	// String FullName;
	// String MobileNumberstr;

	String verifyotpresp;
	String resendotpresp;
	private String socialId;

	boolean exceptioncheck = false;
	private String from, fullName, mobNum, regId;
	private String imageuploadresp;
	private String profilePicUrl;
	private EditText etReferralCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_otp);
		findViewById(R.id.btnSubmit).setOnClickListener(this);
		try {
			if(getIntent() != null){
				Bundle bundle = getIntent().getExtras();
                SharedPreferences sharedPreferences = getSharedPreferences(
                        "FacebookData", 0);
				if(bundle.containsKey("from")){
					from = getIntent().getExtras().getString("from");
				}
				if(bundle.containsKey("mobnum")){
					mobNum = getIntent().getExtras().getString("mobnum");
				}
				if(bundle.containsKey("profilepic")){
					profilePicUrl = getIntent().getExtras().getString("profilepic");
				}
				if(bundle.containsKey("fullname")){
					fullName = getIntent().getExtras().getString("fullname");
				}else {
                    fullName = sharedPreferences.getString("FullName","");

                }
				if(bundle.containsKey("regid")){
					regId = getIntent().getExtras().getString("regid");
				}
				if(bundle.containsKey("socialid")){
					socialId = getIntent().getExtras().getString("socialid");
				}else{

					socialId = sharedPreferences.getString("SocialId","");
				}
                if(bundle.containsKey("isregisterwithphone")){
                    if(bundle.getBoolean("isregisterwithphone")){
                        findViewById(R.id.llMobileNumber).setVisibility(View.GONE);
                        findViewById(R.id.llOTP).setVisibility(View.VISIBLE);
                    }else {
                        findViewById(R.id.llMobileNumber).setVisibility(View.VISIBLE);
                        findViewById(R.id.llOTP).setVisibility(View.GONE);
                    }
                }else {
                    findViewById(R.id.llMobileNumber).setVisibility(View.VISIBLE);
                    findViewById(R.id.llOTP).setVisibility(View.GONE);
                }

			}
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
		etReferralCode = (EditText)findViewById(R.id.etReferralCode);
		enterotp.setTypeface(Typeface.createFromAsset(getAssets(),
				AppConstants.HELVITICA));
		otphardtext.setTypeface(Typeface.createFromAsset(getAssets(),
				AppConstants.HELVITICA));
		otpedittext.setTypeface(Typeface.createFromAsset(getAssets(),
				AppConstants.HELVITICA));
		resendotp.setTypeface(Typeface.createFromAsset(getAssets(),
				AppConstants.HELVITICA));
		continuewithotp.setTypeface(Typeface.createFromAsset(getAssets(),
				AppConstants.HELVITICA));

		// SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
		// FullName = mPrefs.getString("FullName", "");
		// mobNum = mPrefs.getString("MobileNumber", "");

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

	/*	Toast.makeText(
				OTPActivity.this,
				"We will try to automatically verfiy your OTP, if you want you can enter it manually",
				Toast.LENGTH_LONG).show();*/
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.btnSubmit:
				if(!isOnline()){
					Toast.makeText(OTPActivity.this,"Please check internet connection", Toast.LENGTH_LONG).show();
					return;
				}
				Utility.hideSoftKeyboard(OTPActivity.this);

				mobNum = ((EditText)findViewById(R.id.etMobileNumber)).getText().toString().trim();
				if(TextUtils.isEmpty(mobNum)){
					((EditText)findViewById(R.id.etMobileNumber)).setError("Please enter mobile number");

				}else {
					if(mobNum.length() != 10){
						((EditText)findViewById(R.id.etMobileNumber)).setError("Please enter a valid mobile number");
						return;
					}
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						mobNum = "0091"+mobNum;
						new ConnectionTaskForResendOTP()
								.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					} else {
						new ConnectionTaskForResendOTP().execute();
					}
				}
				break;
		}
	}



	// /////// FOR REGISTRATION

	private class ConnectionTaskForVerifyOTP extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(OTPActivity.this);

		@Override
		protected void onPreExecute() {
			try{
				if(dialog != null){
					dialog.setMessage("Please Wait...");
					dialog.setCancelable(false);
					dialog.setCanceledOnTouchOutside(false);
					dialog.show();
				}
			}catch (Exception e){
				e.printStackTrace();
			}
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
			try{

				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}

				if (exceptioncheck) {
					exceptioncheck = false;
					Toast.makeText(OTPActivity.this,
							getResources().getString(R.string.exceptionstring),
							Toast.LENGTH_LONG).show();
					return;
				}

				if(verifyotpresp == null)
					return;
				if (verifyotpresp.contains("Unauthorized Access")) {
					Log.e("OTPActivity", "verifyotpresp Unauthorized Access");
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

					Hashtable<String, Object> extraParams = new Hashtable<String, Object>();
					extraParams.put("FullName", fullName);
					extraParams.put("MobileNumber", mobNum);

					editor.putString("FullName", fullName);
					editor.putString("MobileNumber", mobNum);
					editor.commit();

					// Intent mainIntent = new Intent(OTPActivity.this,
					// HomeActivity.class);

					if(!TextUtils.isEmpty(profilePicUrl)){
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							new ConnectionTaskForImageUpload().executeOnExecutor(
									AsyncTask.THREAD_POOL_EXECUTOR, profilePicUrl);
						} else {
							new ConnectionTaskForImageUpload().execute(profilePicUrl);
						}
					}else {
						Intent mainIntent = new Intent(OTPActivity.this,
								NewHomeScreen.class);
						mainIntent.putExtra("NotFromRegistration", false);
						startActivityForResult(mainIntent, 500);
						overridePendingTransition(R.anim.slide_in_right,
								R.anim.slide_out_left);
						finish();
					}

				} else if (verifyotpresp.equalsIgnoreCase("OTPEXPIRE")) {
					Toast.makeText(OTPActivity.this,
							"Entered OTP has expired. Please click resend OTP",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(OTPActivity.this, "Entered OTP is not valid",
							Toast.LENGTH_SHORT).show();
				}
			}catch (Exception e){
				e.printStackTrace();
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

			String authString = mobNum
					+ otpedittext.getText().toString().trim();
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MobileNumberBasicNameValuePair);
			nameValuePairList.add(singleusepasswordBasicNameValuePair);
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
			try{
				if(dialog != null){
					dialog.setMessage("Please Wait...");
					dialog.setCancelable(false);
					dialog.setCanceledOnTouchOutside(false);
					dialog.show();
				}
			}catch (Exception e){
				e.printStackTrace();
			}
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

			try{
				if ( dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}

				if (exceptioncheck) {
					exceptioncheck = false;
					Toast.makeText(OTPActivity.this,
							getResources().getString(R.string.exceptionstring),
							Toast.LENGTH_LONG).show();
					return;
				}

				if (verifyotpresp.contains("Unauthorized Access")) {
					Log.e("OTPActivity", "verifyotpresp Unauthorized Access");
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

					if(!TextUtils.isEmpty(profilePicUrl)){
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							new ConnectionTaskForImageUpload().executeOnExecutor(
									AsyncTask.THREAD_POOL_EXECUTOR, profilePicUrl);
						} else {
							new ConnectionTaskForImageUpload().execute(profilePicUrl);
						}
					}else {
						Intent mainIntent = new Intent(OTPActivity.this,
								NewHomeScreen.class);
						mainIntent.putExtra("from", "normal");
						mainIntent.putExtra("message", "null");
						startActivityForResult(mainIntent, 500);
						overridePendingTransition(R.anim.slide_in_right,
								R.anim.slide_out_left);
						finish();
					}


				} else if (verifyotpresp.equalsIgnoreCase("OTPEXPIRE")) {
					Toast.makeText(OTPActivity.this,
							"Entered OTP has expired. Please click resend OTP",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(OTPActivity.this, "Entered OTP is not valid",
							Toast.LENGTH_SHORT).show();
				}
			}catch (Exception e){
				e.printStackTrace();
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

			String authString = regId + mobNum + "A"
					+ otpedittext.getText().toString().trim();
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			Log.d("MobileNumber", "mobNum " + mobNum + " signleusepasss "
					+ otpedittext.getText().toString().trim() + " DeviceToken "
					+ regId);
			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MobileNumberBasicNameValuePair);
			nameValuePairList.add(singleusepasswordBasicNameValuePair);
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
			try{
				if(dialog != null){
					dialog.setMessage("Please Wait...");
					dialog.setCancelable(false);
					dialog.setCanceledOnTouchOutside(false);
					dialog.show();
				}
			}catch (Exception e){
				e.printStackTrace();
			}
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

			try{
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

                if (TextUtils.isEmpty(resendotpresp) || exceptioncheck) {
                    exceptioncheck = false;
                    Toast.makeText(OTPActivity.this,
                            getResources().getString(R.string.exceptionstring),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                JSONObject jsonObject = new JSONObject(resendotpresp);
                if(!jsonObject.isNull("status") ){
                    if(jsonObject.optString("status").equalsIgnoreCase("success")){
                        findViewById(R.id.llMobileNumber).setVisibility(View.GONE);
                        findViewById(R.id.llOTP).setVisibility(View.VISIBLE);
                    }else {
                        CustomDialog.showDialog(OTPActivity.this,jsonObject.optString("message"));
                    }
                }





            }catch (Exception e){
                e.printStackTrace();
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
			BasicNameValuePair socialIdBasicNameValuePair = new BasicNameValuePair(
					"socialId", socialId);
            String referralCode = etReferralCode.getText().toString().trim();
            BasicNameValuePair referralCodeBasicNameValuePair = new BasicNameValuePair(
                    "referralCode", referralCode);
			String authString = mobNum+referralCode+socialId;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MobileNumberBasicNameValuePair);
			nameValuePairList.add(socialIdBasicNameValuePair);
            nameValuePairList.add(referralCodeBasicNameValuePair);
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

	private class ConnectionTaskForImageUpload extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(
				OTPActivity.this);

		@Override
		protected void onPreExecute() {
			try{
				if(dialog != null){
					dialog.setMessage("Please Wait...");
					dialog.setCancelable(false);
					dialog.setCanceledOnTouchOutside(false);
					dialog.show();
				}
			}catch (Exception e){
				e.printStackTrace();
			}

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionImageUpload mAuth1 = new AuthenticateConnectionImageUpload();
			try {
				mAuth1.picturePath = args[0];
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
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}

				if (exceptioncheck) {
					exceptioncheck = false;
					Toast.makeText(OTPActivity.this,
							getResources().getString(R.string.exceptionstring),
							Toast.LENGTH_LONG).show();
					return;
				}

				if (!TextUtils.isEmpty(imageuploadresp) && imageuploadresp.equalsIgnoreCase("Error")) {

					Toast.makeText(
							OTPActivity.this,
							"Error uploading Image, Please try again or use a different image",
							Toast.LENGTH_SHORT).show();
				} else if (imageuploadresp.contains("Unauthorized Access")) {
					Log.e("OTPActivity",
							"imageuploadresp Unauthorized Access");
					Toast.makeText(OTPActivity.this,
							getResources().getString(R.string.exceptionstring),
							Toast.LENGTH_LONG).show();
					return;
				} else {
					// Call New Home Screen
					Intent mainIntent = new Intent(OTPActivity.this, NewHomeScreen.class);
					mainIntent.putExtra("from", "normal");
					mainIntent.putExtra("message", "null");
					startActivityForResult(mainIntent, 500);
					overridePendingTransition(R.anim.slide_in_right,
							R.anim.slide_out_left);
					finish();
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}

	}

	public class AuthenticateConnectionImageUpload {

		String picturePath;

		public AuthenticateConnectionImageUpload() {

		}

		public void connection() throws Exception {

			String imagestr = null;

			URL url = new URL(picturePath);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			com.clubmycab.utility.Log.d("img width", "" + myBitmap.getWidth());
			com.clubmycab.utility.Log.d("img height", "" + myBitmap.getHeight());

			int width = 0;
			int height = 0;

			if (myBitmap.getWidth() <= myBitmap.getHeight()) {

				width = 200;
				height = (myBitmap.getHeight() * 200) / myBitmap.getWidth();
			} else {
				width = (myBitmap.getWidth() * 200) / myBitmap.getHeight();
				height = 200;
			}

			com.clubmycab.utility.Log.d("width", "" + width);
			com.clubmycab.utility.Log.d("height", "" + height);

			myBitmap = scaleBitmap(myBitmap, width, height);

			com.clubmycab.utility.Log.d("resize width", "" + myBitmap.getWidth());
			com.clubmycab.utility.Log.d("resize height", "" + myBitmap.getHeight());
			Bitmap mainbmp = myBitmap;
			if (myBitmap != null) {
				myBitmap = null;
			}
			imagestr = Base64.encodeToString(getBytesFromBitmap(mainbmp),
					Base64.NO_WRAP);



			String url_select = GlobalVariables.ServiceUrl + "/imageupload.php";

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
					"MobileNumber", mobNum);
			BasicNameValuePair ImageBasicNameValuePair = new BasicNameValuePair(
					"imagestr", imagestr);

			String authString = imagestr + mobNum;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MobileNumberBasicNameValuePair);
			nameValuePairList.add(ImageBasicNameValuePair);
			nameValuePairList.add(authValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			com.clubmycab.utility.Log.d("httpResponse", "" + httpResponse);

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				imageuploadresp = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			com.clubmycab.utility.Log.d("imageuploadresp", "" + stringBuilder.toString());

			if (imageuploadresp.equalsIgnoreCase("Error")) {

			} else if (imageuploadresp.contains("Unauthorized Access")) {

			} else {

				String urldisplay = GlobalVariables.ServiceUrl
						+ "/ProfileImages/" + imageuploadresp.trim();
				Bitmap mIcon11 = null;
				String imgString = null;
				try {
					InputStream in = new URL(urldisplay).openStream();
					mIcon11 = BitmapFactory.decodeStream(in);

					imgString = Base64.encodeToString(
							getBytesFromBitmap(mIcon11), Base64.NO_WRAP);

				} catch (Exception e) {
					com.clubmycab.utility.Log.e("Error", e.getMessage());
					e.printStackTrace();
				}

				SharedPreferences sharedPreferences1 = getSharedPreferences(
						"userimage", 0);
				SharedPreferences.Editor editor1 = sharedPreferences1.edit();
				editor1.putString("imgname", imageuploadresp.trim());
				editor1.putString("imagestr", imgString);
				editor1.commit();
			}
		}
	}


	public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight) {
		Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight,
				Bitmap.Config.ARGB_8888);

		float scaleX = newWidth / (float) bitmap.getWidth();
		float scaleY = newHeight / (float) bitmap.getHeight();
		float pivotX = 0;
		float pivotY = 0;

		Matrix scaleMatrix = new Matrix();
		scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);

		Canvas canvas = new Canvas(scaledBitmap);
		canvas.setMatrix(scaleMatrix);
		canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));

		return scaledBitmap;
	}
	public byte[] getBytesFromBitmap(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
		return stream.toByteArray();
	}

}
