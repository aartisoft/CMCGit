package com.clubmycab.ui;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.R;
import com.clubmycab.asynctasks.GlobalAsyncTask;
import com.clubmycab.asynctasks.GlobalAsyncTask.AsyncTaskResultListener;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class FirstLoginWalletsActivity extends Activity implements
		AsyncTaskResultListener {

	private String FullName, MobileNumber, Email;

	private LinearLayout walletLinearLayout;

	private EditText otpEditText, mobileEditText, emailEditText;
	private Button continuewithotp, sendOTP;
	private TextView otphardtext;

	private final String LINK_WALLET = "LinkWallet";
	private final String CREATE_WALLET = "CreateWallet";
	private String walletAction = "";

	private String mobilenumber;
	private String from = "";
	private String token = "";

	Tracker tracker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_first_login_wallet);

		// Check if Internet present
		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					FirstLoginWalletsActivity.this);
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

		// ////////////////////
		GoogleAnalytics analytics = GoogleAnalytics
				.getInstance(FirstLoginWalletsActivity.this);
		tracker = analytics
				.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);

		// All subsequent hits will be send with screen name = "main screen"
		tracker.setScreenName("WalletsPage");

		try {
			from = getIntent().getExtras().getString("from");

		} catch (Exception e) {
			from = "";

		}
		SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
		FullName = mPrefs.getString("FullName", "");
		MobileNumber = mPrefs.getString("MobileNumber", "");
		Email = mPrefs.getString("Email", "");

		// mobilenumber = "8200012345";
		mobilenumber = MobileNumber.substring(4);

		// editor.putString("token", token);
		otphardtext = (TextView) findViewById(R.id.otphardtext);
		// otphardtext.setVisibility(View.INVISIBLE);

		walletLinearLayout = (LinearLayout) findViewById(R.id.walletFLLinearLayout);

		// LinearLayout linearLayout = (LinearLayout)
		// findViewById(R.id.walletMobikwikLinearLayout);
		// linearLayout.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		//
		// if (from.equalsIgnoreCase("reg")) {
		// querywallet();
		//
		// } else {
		// SharedPreferences sharedPreferences = getSharedPreferences(
		// "MobikwikToken", 0);
		// token = sharedPreferences.getString("token", "");
		// if (!token.isEmpty() && !token.equalsIgnoreCase("")) {
		//
		// otphardtext
		// .setText("Your Mobikwik wallet is already linked with the app");
		// walletLinearLayout.setVisibility(View.INVISIBLE);
		// } else {
		// querywallet();
		// }
		//
		// }
		// }
		// });

		// otpLinearLayout = (LinearLayout)
		// findViewById(R.id.walletFLLinearLayout);

		mobileEditText = (EditText) findViewById(R.id.editTextWalletMobile);
		mobileEditText.setText(MobileNumber.substring(4));

		emailEditText = (EditText) findViewById(R.id.editTextWalletEmail);

		otpEditText = (EditText) findViewById(R.id.otpedittext);

		sendOTP = (Button) findViewById(R.id.buttonSendOTP);
		sendOTP.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				generateOTP();
			}
		});

		continuewithotp = (Button) findViewById(R.id.continuewithotp);
		continuewithotp.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (walletAction.equals(LINK_WALLET)) {

					if (otpEditText.getText().toString().trim().isEmpty()) {
						Toast.makeText(FirstLoginWalletsActivity.this,
								"Please enter the OTP", Toast.LENGTH_LONG)
								.show();
						return;
					}

					tracker.send(new HitBuilders.EventBuilder()
							.setCategory("LinkExistingWallet")
							.setAction("LinkExistingWallet")
							.setLabel("LinkExistingWallet").build());

					linkWallet();
				}

				else if (walletAction.equals(CREATE_WALLET)) {
					if (emailEditText.getText().toString().trim().isEmpty()) {
						Toast.makeText(FirstLoginWalletsActivity.this,
								"Please enter your e-mail", Toast.LENGTH_LONG)
								.show();
						return;
					}

					if (otpEditText.getText().toString().trim().isEmpty()) {
						Toast.makeText(FirstLoginWalletsActivity.this,
								"Please enter the OTP", Toast.LENGTH_LONG)
								.show();
						return;
					}

					tracker.send(new HitBuilders.EventBuilder()
							.setCategory("CreateNewWallet")
							.setAction("CreateNewWallet")
							.setLabel("CreateNewWallet").build());

					createWallet();
				} else {
					Toast.makeText(
							FirstLoginWalletsActivity.this,
							"Your Mobikwik wallet is already linked with the app",
							Toast.LENGTH_LONG).show();

				}
			}
		});

		TextView textView = (TextView) findViewById(R.id.textViewTNCMobikwik);
		textView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						FirstLoginWalletsActivity.this);
				builder.setMessage(getResources().getString(
						R.string.mobikwik_signup_tnc));
				builder.setCancelable(false);
				builder.setPositiveButton("OK", null);
				builder.show();

			}
		});

		if (from.equalsIgnoreCase("reg")) {

			querywallet();

			Button button = (Button) findViewById(R.id.maybelaterbutton);
			button.setVisibility(View.VISIBLE);
			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent mainIntent = new Intent(
							FirstLoginWalletsActivity.this,
							FirstLoginClubsActivity.class);
					startActivity(mainIntent);
					finish();
				}
			});
		} else {
			SharedPreferences sharedPreferences = getSharedPreferences(
					"MobikwikToken", 0);
			token = sharedPreferences.getString("token", "");
			if (!token.isEmpty() && !token.equalsIgnoreCase("")) {

				// otphardtext
				// .setText("Your Mobikwik wallet is already linked with the app");
				checkUserBalance();
				walletLinearLayout.setVisibility(View.INVISIBLE);

				SharedPreferences sharedPreferences2 = getSharedPreferences(
						"MobikwikCoupon", 0);
				String couponCode = sharedPreferences2.getString("couponCode",
						"");
				if (!couponCode.isEmpty() && couponCode.length() > 0) {
					TextView textView2 = (TextView) findViewById(R.id.couponcodemobikwik);
					textView2.setVisibility(View.VISIBLE);
					textView2.setText("Your 50 on 50 offer coupon code : "
							+ couponCode);

					textView2 = (TextView) findViewById(R.id.couponcodemobikwiktnc);
					textView2.setVisibility(View.VISIBLE);
				}
			} else {
				querywallet();
			}
		}

	}

	private void querywallet() {

		String msgcode = "500";
		String action = "existingusercheck";

		String checksumstring = GlobalMethods.calculateCheckSumForService("'"
				+ action + "''" + mobilenumber + "''"
				+ GlobalVariables.Mobikwik_MerchantName + "''"
				+ GlobalVariables.Mobikwik_Mid + "''" + msgcode + "'",
				GlobalVariables.Mobikwik_14SecretKey);
		String endpoint = GlobalVariables.Mobikwik_ServerURL + "/querywallet";
		String params = "cell=" + mobilenumber + "&msgcode=" + msgcode
				+ "&action=" + action + "&mid=" + GlobalVariables.Mobikwik_Mid
				+ "&merchantname=" + GlobalVariables.Mobikwik_MerchantName
				+ "&checksum=" + checksumstring;
		Log.d("WalletsActivity", "querywallet endpoint : " + endpoint
				+ " params : " + params);
		new GlobalAsyncTask(this, endpoint, params, null, this, true,
				"querywallet", true);

	}

	private void generateOTP() {
		String msgcode = "504";
		String amount = "10000";
		String tokenType = "1";

		String checksumstring = GlobalMethods.calculateCheckSumForService("'"
				+ amount + "''" + mobilenumber + "''"
				+ GlobalVariables.Mobikwik_MerchantName + "''"
				+ GlobalVariables.Mobikwik_Mid + "''" + msgcode + "''"
				+ tokenType + "'", GlobalVariables.Mobikwik_14SecretKey);
		String endpoint = GlobalVariables.Mobikwik_ServerURL + "/otpgenerate";
		String params = "cell=" + mobilenumber + "&amount=" + amount
				+ "&msgcode=" + msgcode + "&mid="
				+ GlobalVariables.Mobikwik_Mid + "&merchantname="
				+ GlobalVariables.Mobikwik_MerchantName + "&tokentype="
				+ tokenType + "&checksum=" + checksumstring;
		Log.d("otpgenerate", "querywallet endpoint : " + endpoint
				+ " params : " + params);
		new GlobalAsyncTask(FirstLoginWalletsActivity.this, endpoint, params,
				null, FirstLoginWalletsActivity.this, true, "otpgenerate", true);
		Toast.makeText(FirstLoginWalletsActivity.this,
				getResources().getString(R.string.otpsent), Toast.LENGTH_LONG)
				.show();
	}

	private void linkWallet() {
		String msgcode = "507";
		String amount = "10000";
		String tokenType = "1";
		String otp = otpEditText.getText().toString().trim();

		String checksumstring = GlobalMethods.calculateCheckSumForService("'"
				+ amount + "''" + mobilenumber + "''"
				+ GlobalVariables.Mobikwik_MerchantName + "''"
				+ GlobalVariables.Mobikwik_Mid + "''" + msgcode + "''" + otp
				+ "''" + tokenType + "'", GlobalVariables.Mobikwik_14SecretKey);
		String endpoint = GlobalVariables.Mobikwik_ServerURL + "/tokengenerate";
		String params = "cell=" + mobilenumber + "&amount=" + amount + "&otp="
				+ otp + "&msgcode=" + msgcode + "&mid="
				+ GlobalVariables.Mobikwik_Mid + "&merchantname="
				+ GlobalVariables.Mobikwik_MerchantName + "&tokentype="
				+ tokenType + "&checksum=" + checksumstring;
		Log.d("otpgenerate", "tokengenerate endpoint : " + endpoint
				+ " params : " + params);
		new GlobalAsyncTask(FirstLoginWalletsActivity.this, endpoint, params,
				null, FirstLoginWalletsActivity.this, true, "tokengenerate",
				true);
	}

	private void createWallet() {
		String msgcode = "502";
		String email = emailEditText.getText().toString().trim();
		String otp = otpEditText.getText().toString().trim();

		String checksumstring = GlobalMethods.calculateCheckSumForService("'"
				+ mobilenumber + "''" + email + "''"
				+ GlobalVariables.Mobikwik_MerchantName + "''"
				+ GlobalVariables.Mobikwik_Mid + "''" + msgcode + "''" + otp
				+ "'", GlobalVariables.Mobikwik_14SecretKey);
		String endpoint = GlobalVariables.Mobikwik_ServerURL
				+ "/createwalletuser";
		String params = "email=" + email + "&cell=" + mobilenumber + "&otp="
				+ otp + "&msgcode=" + msgcode + "&mid="
				+ GlobalVariables.Mobikwik_Mid + "&merchantname="
				+ GlobalVariables.Mobikwik_MerchantName + "&checksum="
				+ checksumstring;
		Log.d("createwalletuser", "createwalletuser endpoint : " + endpoint
				+ " params : " + params);
		new GlobalAsyncTask(FirstLoginWalletsActivity.this, endpoint, params,
				null, FirstLoginWalletsActivity.this, true, "createwalletuser",
				true);
	}

	private void getCoupons() {

		String endpoint = GlobalVariables.ServiceUrl + "/getCoupons.php";
		String authString = MobileNumber + "mobikwik" + "newWallet";
		String params = "type=newWallet&provider=mobikwik&mobileNumber="
				+ MobileNumber + "&auth="
				+ GlobalMethods.calculateCMCAuthString(authString);
		Log.d("getCoupons", "getCoupons endpoint : " + endpoint + " params : "
				+ params);
		new GlobalAsyncTask(FirstLoginWalletsActivity.this, endpoint, params,
				null, FirstLoginWalletsActivity.this, false, "getCoupons",
				false);
	}

	private void checkUserBalance() {
		String msgcode = "501";

		String checksumstring = GlobalMethods.calculateCheckSumForService("'"
				+ mobilenumber + "''" + GlobalVariables.Mobikwik_MerchantName
				+ "''" + GlobalVariables.Mobikwik_Mid + "''" + msgcode + "''"
				+ token + "'", GlobalVariables.Mobikwik_14SecretKey);
		String endpoint = GlobalVariables.Mobikwik_ServerURL + "/userbalance";
		String params = "cell=" + mobilenumber + "&token=" + token
				+ "&msgcode=" + msgcode + "&mid="
				+ GlobalVariables.Mobikwik_Mid + "&merchantname="
				+ GlobalVariables.Mobikwik_MerchantName + "&checksum="
				+ checksumstring;
		Log.d("checkUserBalance", "checkUserBalance endpoint : " + endpoint
				+ " params : " + params);
		new GlobalAsyncTask(FirstLoginWalletsActivity.this, endpoint, params,
				null, FirstLoginWalletsActivity.this, true, "userbalance", true);
	}

	private void tokenRegenerate() {
		String msgcode = "507";

		String checksumstring = GlobalMethods.calculateCheckSumForService("'"
				+ mobilenumber + "''" + GlobalVariables.Mobikwik_MerchantName
				+ "''" + GlobalVariables.Mobikwik_Mid + "''" + msgcode + "''"
				+ token + "''1'",
				GlobalVariables.Mobikwik_14SecretKey_TokenRegenerate);
		String endpoint = GlobalVariables.Mobikwik_ServerURL
				+ "/tokenregenerate";
		String params = "cell=" + mobilenumber + "&token=" + token
				+ "&tokentype=1" + "&msgcode=" + msgcode + "&mid="
				+ GlobalVariables.Mobikwik_Mid + "&merchantname="
				+ GlobalVariables.Mobikwik_MerchantName + "&checksum="
				+ checksumstring;
		Log.d("FirstLoginWalletActivity", "tokenRegenerate endpoint : "
				+ endpoint + " params : " + params);

		new GlobalAsyncTask(this, endpoint, params, null, this, true,
				"tokenregenerate", true);
	}
	
	private void openAppOrMSite() {

		String packageName = "com.mobikwik_new";
		String mSite = "https://m.mobikwik.com";

		if (checkIfAppInstalled(packageName)) {

			Intent launchIntent = getPackageManager()
					.getLaunchIntentForPackage(packageName);
			startActivity(launchIntent);

		} else {

			Intent intent = new Intent(this, MobileSiteActivity.class);
			intent.putExtra(MobileSiteFragment.ARGUMENTS_MOBILE_SITE_URL, mSite);
			startActivity(intent);
		}
	}

	private boolean checkIfAppInstalled(String packageName) {

		PackageManager packageManager = getPackageManager();

		try {
			packageManager.getPackageInfo(packageName,
					PackageManager.GET_ACTIVITIES);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private void checksumInvalidToast() {
		Log.e("FirstLoginWalletsActivity", "Response checksum does not match!!");
		Toast.makeText(FirstLoginWalletsActivity.this,
				"Something went wrong, please try again", Toast.LENGTH_LONG)
				.show();
	}

	@Override
	public void getResult(String response, String uniqueID) {

		if (uniqueID.equals("querywallet")) {
			try {
				JSONObject jsonObject = new JSONObject(response);
				Log.d("FirstLoginWalletActivity", "querywallet jsonObject :"
						+ jsonObject);
				if (!GlobalMethods.checkResponseChecksum(response)) {
					checksumInvalidToast();
					return;
				}

				if (jsonObject.getString("status").equals("SUCCESS")) {

					otphardtext.setText(getResources().getString(
							R.string.mobikwik_already_account));

					emailEditText.setVisibility(View.GONE);

					walletAction = LINK_WALLET;
					continuewithotp.setText("Link Wallet");
					Toast.makeText(FirstLoginWalletsActivity.this,
							"Press the 'Send OTP' button to proceed",
							Toast.LENGTH_LONG).show();
				} else {

					otphardtext.setText(getResources().getString(
							R.string.mobikwik_nothave_account));
					emailEditText.setVisibility(View.VISIBLE);
					if (Email != null && !Email.isEmpty() && Email.length() > 0) {
						emailEditText.setText(Email);
					}

					walletAction = CREATE_WALLET;

					continuewithotp.setText("Create Wallet");
					Toast.makeText(FirstLoginWalletsActivity.this,
							"Press the 'Send OTP' button to proceed",
							Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (uniqueID.equals("otpgenerate")) {
			Log.d("FirstLoginWalletActivity", "otpgenerate response : "
					+ response);
			if (!GlobalMethods.checkResponseChecksum(response)) {
				checksumInvalidToast();
				return;
			}

			try {
				JSONObject jsonObject = new JSONObject(response);
				if (jsonObject.getString("status").equals("SUCCESS")) {

					walletLinearLayout.setVisibility(View.VISIBLE);

					otpEditText.setVisibility(View.VISIBLE);
					continuewithotp.setVisibility(View.VISIBLE);
					TextView textView = (TextView) findViewById(R.id.textViewbyclicking);
					textView.setVisibility(View.VISIBLE);

				} else {

					Toast.makeText(FirstLoginWalletsActivity.this,
							jsonObject.getString("statusdescription"),
							Toast.LENGTH_LONG).show();

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (uniqueID.equals("tokengenerate")) {
			Log.d("FirstLoginWalletActivity", "tokengenerate response : "
					+ response);
			if (!GlobalMethods.checkResponseChecksum(response)) {
				checksumInvalidToast();
				return;
			}

			try {
				JSONObject jsonObject = new JSONObject(response);
				if (jsonObject.getString("status").equals("SUCCESS")) {
					String token = jsonObject.getString("token");

					SharedPreferences sharedPreferences = getSharedPreferences(
							"MobikwikToken", 0);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putString("token", token);
					editor.commit();
					Toast.makeText(FirstLoginWalletsActivity.this,
							"Account linked successfully!", Toast.LENGTH_LONG)
							.show();

					SharedPreferences sharedPreferences2 = getSharedPreferences(
							"MobikwikCoupon", 0);
					String couponCode = sharedPreferences2.getString(
							"couponCode", "");
					if (!couponCode.isEmpty() && couponCode.length() > 0) {
						TextView textView2 = (TextView) findViewById(R.id.couponcodemobikwik);
						textView2.setVisibility(View.VISIBLE);
						textView2.setText("Your 50 on 50 offer coupon code : "
								+ couponCode);

						textView2 = (TextView) findViewById(R.id.couponcodemobikwiktnc);
						textView2.setVisibility(View.VISIBLE);
					}

					if (from.equalsIgnoreCase("reg")) {
						Intent mainIntent = new Intent(
								FirstLoginWalletsActivity.this,
								FirstLoginClubsActivity.class);
						startActivity(mainIntent);
						finish();
					} else {
						// otphardtext
						// .setText("Your Mobikwik wallet is already linked with the app");
						checkUserBalance();
						walletLinearLayout.setVisibility(View.INVISIBLE);
					}
				} else {
					Toast.makeText(FirstLoginWalletsActivity.this,
							jsonObject.getString("statusdescription"),
							Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (uniqueID.equals("createwalletuser")) {
			Log.d("FirstLoginWalletActivity", "createwalletuser response : "
					+ response);
			if (!GlobalMethods.checkResponseChecksum(response)) {
				checksumInvalidToast();
				return;
			}

			try {
				JSONObject jsonObject = new JSONObject(response);
				if (jsonObject.getString("status").equals("SUCCESS")) {

					Toast.makeText(FirstLoginWalletsActivity.this,
							"User created succesfully!", Toast.LENGTH_LONG)
							.show();

					Log.d("Response:::", response);
					// linkWallet(); moved to getCoupons

					getCoupons();
				} else {
					Toast.makeText(FirstLoginWalletsActivity.this,
							jsonObject.getString("statusdescription"),
							Toast.LENGTH_LONG).show();
					// {"statusdescription":"EIther OTP mismatch or Invalid OTP due to mismatch in order id and transaction amount","checksum":"d7e1b38d51db8d5b0b7f0b431e7a95fe9450fc8ea477132e5405f27e84b01aeb","messagecode":"502","statuscode":"164","status":"FAILURE"}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (uniqueID.equals("getCoupons")) {
			Log.d("FirstLoginWalletActivity", "getCoupons response : "
					+ response);

			if (response != null && response.length() > 0
					&& response.contains("Unauthorized Access")) {
				Log.e("FirstLoginWalletsActivity",
						"getCoupons Unauthorized Access");
				Toast.makeText(FirstLoginWalletsActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			try {
				JSONObject jsonObject = new JSONObject(response);
				if (jsonObject.getString("status").equals("success")) {
					JSONObject jsonObject2 = new JSONObject(jsonObject.get(
							"data").toString());
					String couponName = jsonObject2.get("couponName")
							.toString();
					Log.d("FirstLoginWalletActivity",
							"getCoupons jsonObject2 : " + couponName);

					SharedPreferences sharedPreferences = getSharedPreferences(
							"MobikwikCoupon", 0);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putString("couponCode", couponName);
					editor.commit();

					linkWallet();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (uniqueID.equals("userbalance")) {
			Log.d("FirstLoginWalletActivity", "userbalance response : "
					+ response);

			try {
				JSONObject jsonObject = new JSONObject(response);
				if (jsonObject.getString("status").equalsIgnoreCase("success")) {
					Log.d("FirstLoginWalletActivity", "userbalance response : ");
					otphardtext.setText("Your wallet balance : \u20B9"
							+ jsonObject.getString("balanceamount"));
					tokenRegenerate();
					Button button = (Button)findViewById(R.id.buttonTopUpWallet);
					button.setVisibility(View.VISIBLE);
					button.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							openAppOrMSite();
						}
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (uniqueID.equals("tokenregenerate")) {
			Log.d("FirstLoginWalletActivity", "tokenregenerate response : "
					+ response);
			try {

				JSONObject jsonObject = new JSONObject(response);
				String token = jsonObject.get("token").toString();

				SharedPreferences sharedPreferences = getSharedPreferences(
						"MobikwikToken", 0);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putString("token", token);
				editor.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}

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

	@Override
	public void onBackPressed() {

		super.onBackPressed();

		Intent mainIntent = new Intent(FirstLoginWalletsActivity.this,
				NewHomeScreen.class);
		mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivityForResult(mainIntent, 500);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

}
