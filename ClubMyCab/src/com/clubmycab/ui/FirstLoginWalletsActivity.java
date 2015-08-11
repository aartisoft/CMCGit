package com.clubmycab.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class FirstLoginWalletsActivity extends Activity implements
		AsyncTaskResultListener {

	private String FullName, MobileNumber;

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

		try {
			from = getIntent().getExtras().getString("from");

		} catch (Exception e) {
			from = "";

		}
		SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
		FullName = mPrefs.getString("FullName", "");
		MobileNumber = mPrefs.getString("MobileNumber", "");

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
					createWallet();
				} else {
					Toast.makeText(
							FirstLoginWalletsActivity.this,
							"Your Mobikwik wallet is already linked with the app",
							Toast.LENGTH_LONG).show();

				}
			}
		});

		if (from.equalsIgnoreCase("reg")) {
			querywallet();

		} else {
			SharedPreferences sharedPreferences = getSharedPreferences(
					"MobikwikToken", 0);
			token = sharedPreferences.getString("token", "");
			if (!token.isEmpty() && !token.equalsIgnoreCase("")) {

				otphardtext
						.setText("Your Mobikwik wallet is already linked with the app");
				walletLinearLayout.setVisibility(View.INVISIBLE);
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
				+ GlobalVariables.Mobikwik_Mid + "''" + msgcode + "''",
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

	private boolean checkResponseChecksum(String response) {

		try {
			JSONObject jsonObject = new JSONObject(response);

			Iterator<String> iterator = jsonObject.keys();
			String responseValues = "";

			HashMap<String, String> hashMap = new HashMap<String, String>();

			while (iterator.hasNext()) {
				String key = iterator.next();
				String value = jsonObject.get(key).toString();

				if (!value.isEmpty() && value.length() > 0
						&& !key.equalsIgnoreCase("checksum")) {
					hashMap.put(key, value);
				}

				// if (!value.isEmpty() && value.length() > 0 &&
				// !key.equalsIgnoreCase("checksum")) {
				// responseValues += (value + "''");
				// }
			}
			// Log.d("checkResponseChecksum", "hashMap : " + hashMap);
			Map<String, String> map = new TreeMap<String, String>(hashMap);
			List<String> list = new ArrayList<String>(map.keySet());
			Log.d("checkResponseChecksum",
					"map : " + map + " keySet : " + map.keySet() + " list : "
							+ list);

			for (int i = 0; i < list.size(); i++) {
				responseValues += (map.get(list.get(i)) + "''");
			}

			responseValues = responseValues.substring(0,
					responseValues.length() - 2);
			String responseValuesFinal = "'" + responseValues + "'";

			// Log.d("checkResponseChecksum", "responseValuesFinal : "
			// + responseValuesFinal);

			String checkSumGenerated = GlobalMethods
					.calculateCheckSumForService(responseValuesFinal,
							GlobalVariables.Mobikwik_14SecretKey);
			Log.d("checkResponseChecksum", checkSumGenerated);

			if (checkSumGenerated.equals(jsonObject.get("checksum").toString())) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
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
				if (!checkResponseChecksum(response)) {
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
					// AlertDialog.Builder builder = new AlertDialog.Builder(
					// FirstLoginWalletsActivity.this);
					// builder.setMessage("You already have a Mobikwik wallet registered with your number, would you like to link it with the app?");
					// builder.setCancelable(false);
					//
					// builder.setPositiveButton("Yes",
					// new DialogInterface.OnClickListener() {
					//
					// @Override
					// public void onClick(DialogInterface dialog,
					// int which) {
					// walletLinearLayout
					// .setVisibility(View.VISIBLE);
					// emailEditText.setVisibility(View.GONE);
					//
					// walletAction = LINK_WALLET;
					// continuewithotp.setText("Link Wallet");
					// Toast.makeText(
					// FirstLoginWalletsActivity.this,
					// "Press the 'Send OTP' button to proceed",
					// Toast.LENGTH_LONG).show();
					// }
					// });
					//
					// builder.setNegativeButton("Maybe later",
					// new DialogInterface.OnClickListener() {
					//
					// @Override
					// public void onClick(DialogInterface dialog,
					// int which) {
					// Intent mainIntent = new Intent(
					// FirstLoginWalletsActivity.this,
					// FirstLoginClubsActivity.class);
					// startActivity(mainIntent);
					// finish();
					// }
					// });
					//
					// builder.show();
				} else {

					otphardtext.setText(getResources().getString(
							R.string.mobikwik_nothave_account));
					emailEditText.setVisibility(View.VISIBLE);

					walletAction = CREATE_WALLET;

					continuewithotp.setText("Create Wallet");
					Toast.makeText(FirstLoginWalletsActivity.this,
							"Press the 'Send OTP' button to proceed",
							Toast.LENGTH_LONG).show();
					// AlertDialog.Builder builder = new AlertDialog.Builder(
					// FirstLoginWalletsActivity.this);
					// builder.setMessage("You do not have a Mobikwik wallet registered with your number, would you like to create one?");
					// builder.setCancelable(false);
					//
					// builder.setPositiveButton("Yes",
					// new DialogInterface.OnClickListener() {
					//
					// @Override
					// public void onClick(DialogInterface dialog,
					// int which) {
					// walletLinearLayout
					// .setVisibility(View.VISIBLE);
					// emailEditText.setVisibility(View.VISIBLE);
					//
					// walletAction = CREATE_WALLET;
					//
					// continuewithotp.setText("Create Wallet");
					// Toast.makeText(
					// FirstLoginWalletsActivity.this,
					// "Press the 'Send OTP' button to proceed",
					// Toast.LENGTH_LONG).show();
					// }
					// });
					//
					// builder.setNegativeButton("Maybe later",
					// new DialogInterface.OnClickListener() {
					//
					// @Override
					// public void onClick(DialogInterface dialog,
					// int which) {
					// Intent mainIntent = new Intent(
					// FirstLoginWalletsActivity.this,
					// FirstLoginClubsActivity.class);
					// startActivity(mainIntent);
					// finish();
					// }
					// });
					//
					// builder.show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (uniqueID.equals("otpgenerate")) {
			Log.d("FirstLoginWalletActivity", "otpgenerate response : "
					+ response);
			if (!checkResponseChecksum(response)) {
				checksumInvalidToast();
				return;
			}

			try {
				JSONObject jsonObject = new JSONObject(response);
				if (jsonObject.getString("status").equals("SUCCESS")) {

					walletLinearLayout.setVisibility(View.VISIBLE);
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
			if (!checkResponseChecksum(response)) {
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

					if (from.equalsIgnoreCase("reg")) {
						Intent mainIntent = new Intent(
								FirstLoginWalletsActivity.this,
								FirstLoginClubsActivity.class);
						startActivity(mainIntent);
						finish();
					} else {
						otphardtext
								.setText("Your Mobikwik wallet is already linked with the app");
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
			if (!checkResponseChecksum(response)) {
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
					linkWallet();

				} else {
					Toast.makeText(FirstLoginWalletsActivity.this,
							jsonObject.getString("statusdescription"),
							Toast.LENGTH_LONG).show();
					// {"statusdescription":"EIther OTP mismatch or Invalid OTP due to mismatch in order id and transaction amount","checksum":"d7e1b38d51db8d5b0b7f0b431e7a95fe9450fc8ea477132e5405f27e84b01aeb","messagecode":"502","statuscode":"164","status":"FAILURE"}

				}
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
				HomeActivity.class);
		mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivityForResult(mainIntent, 500);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

}
