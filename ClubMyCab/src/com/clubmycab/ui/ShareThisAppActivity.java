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
import org.json.JSONObject;

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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.CircularImageView;
import com.clubmycab.R;
import com.clubmycab.asynctasks.GlobalAsyncTask;
import com.clubmycab.asynctasks.GlobalAsyncTask.AsyncTaskResultListener;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class ShareThisAppActivity extends Activity implements
		AsyncTaskResultListener {

	CircularImageView profilepic;
	TextView username;
	ImageView notificationimg;
	CircularImageView drawerprofilepic;
	TextView drawerusername;
	RelativeLayout unreadnoticountrl;
	TextView unreadnoticount;

	Tracker tracker;

	private String result;

	private String referral, amount, maxUseLimit, totalReferrals;

	private boolean exceptioncheck;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_share_app);

		// Check if Internet present
		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					ShareThisAppActivity.this);
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

		GoogleAnalytics analytics = GoogleAnalytics
				.getInstance(ShareThisAppActivity.this);
		tracker = analytics
				.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);

		// All subsequent hits will be send with screen name = "main screen"
		tracker.setScreenName("MyProfile");

		RelativeLayout shareapprl = (RelativeLayout) findViewById(R.id.shareapprl);
		shareapprl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Log.d("shareapprl", "shareapprl");
			}
		});

		UniversalDrawer drawer = new UniversalDrawer(this, tracker);
		drawer.createDrawer();

		profilepic = (CircularImageView) findViewById(R.id.profilepic);
		notificationimg = (ImageView) findViewById(R.id.notificationimg);
		drawerprofilepic = (CircularImageView) findViewById(R.id.drawerprofilepic);

		SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
		String FullName = mPrefs.getString("FullName", "");
		// String MobileNumberstr = mPrefs.getString("MobileNumber", "");

		username = (TextView) findViewById(R.id.username);
		username.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		username.setText(FullName);

		drawerusername = (TextView) findViewById(R.id.drawerusername);
		drawerusername.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));
		drawerusername.setText(FullName);

		notificationimg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent mainIntent = new Intent(ShareThisAppActivity.this,
						NotificationListActivity.class);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);

			}
		});

		unreadnoticountrl = (RelativeLayout) findViewById(R.id.unreadnoticountrl);
		unreadnoticount = (TextView) findViewById(R.id.unreadnoticount);

		if (GlobalVariables.UnreadNotificationCount.equalsIgnoreCase("0")) {

			unreadnoticountrl.setVisibility(View.GONE);

		} else {

			unreadnoticountrl.setVisibility(View.VISIBLE);
			unreadnoticount.setText(GlobalVariables.UnreadNotificationCount);
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new ConnectionTaskForReferralCode()
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			new ConnectionTaskForReferralCode().execute();
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

		Intent mainIntent = new Intent(ShareThisAppActivity.this,
				HomeActivity.class);
		mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivityForResult(mainIntent, 500);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	private class ConnectionTaskForReferralCode extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(
				ShareThisAppActivity.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionReferralCode mAuth1 = new AuthenticateConnectionReferralCode();
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
				Toast.makeText(ShareThisAppActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (result.contains("Unauthorized Access")) {
				Log.e("ShareThisAppActivity", "result Unauthorized Access");
				Toast.makeText(ShareThisAppActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			try {
				if (result != null && !result.isEmpty()) {
					JSONObject jsonObject = new JSONObject(result);

					if (jsonObject.get("status").toString()
							.equalsIgnoreCase("success")) {

						JSONObject jsonObject2 = new JSONObject(jsonObject.get(
								"data").toString());
						referral = jsonObject2.get("referralCode").toString();
						amount = jsonObject2.get("amount").toString();
						maxUseLimit = jsonObject2.get("maxUseLimit").toString();
						totalReferrals = jsonObject2.get("totalReferrals")
								.toString();

						TextView textView = (TextView) findViewById(R.id.offershareapptext);
						textView.setVisibility(View.VISIBLE);
						textView.setText("Share this app with your friends and earn Rs."
								+ amount
								+ "! Ask them to register using referral code : "
								+ referral
								+ ". Offer valid upto a maximum of Rs."
								+ (String.format(
										"%1.0f",
										Float.parseFloat(amount)
												* Float.parseFloat(maxUseLimit)))
								+ ", you have earned Rs."
								+ (String.format(
										"%1.0f",
										Float.parseFloat(amount)
												* Float.parseFloat(totalReferrals)))
								+ " so far");

						Button button = (Button) findViewById(R.id.shareappbutton);
						button.setVisibility(View.VISIBLE);
						button.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {

								SharedPreferences mPrefs = getSharedPreferences(
										"FacebookData", 0);
								String MemberNumberstr = mPrefs.getString(
										"MobileNumber", "");

								querywallet(MemberNumberstr.substring(4));
							}
						});
					} else {
						TextView textView = (TextView) findViewById(R.id.offershareapptext);
						textView.setVisibility(View.VISIBLE);
						textView.setText("Share this app with your friends");

						Button button = (Button) findViewById(R.id.shareappbutton);
						button.setVisibility(View.VISIBLE);
						button.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								shareAppWithoutReferralCode();
							}
						});
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public class AuthenticateConnectionReferralCode {

		public AuthenticateConnectionReferralCode() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/referralCode.php";

			HttpPost httpPost = new HttpPost(url_select);

			SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
			String MemberNumberstr = mPrefs.getString("MobileNumber", "");

			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
					"mobileNumber", MemberNumberstr);

			String authString = MemberNumberstr;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MobileNumberBasicNameValuePair);
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

	private void querywallet(String mobilenumber) {

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
		new GlobalAsyncTask(this, endpoint, params, null, this, false,
				"querywallet", true);

	}

	@Override
	public void getResult(String response, String uniqueID) {

		if (uniqueID.equals("querywallet")) {

			try {
				JSONObject jsonObject = new JSONObject(response);
				Log.d("ShareThisAppActivity", "querywallet jsonObject :"
						+ jsonObject);
				if (!GlobalMethods.checkResponseChecksum(response)) {
					checksumInvalidToast();
					return;
				}

				if (jsonObject.getString("status").equals("SUCCESS")) {
					Intent sendIntent = new Intent();
					sendIntent.setAction(Intent.ACTION_SEND);
					sendIntent
							.putExtra(
									Intent.EXTRA_TEXT,
									"I am using this cool app 'ClubMyCab' to share & book cabs. Check it out @ https://play.google.com/store/apps/details?id=com.clubmycab. Use my referral code "
											+ referral
											+ " and earn Rs."
											+ amount);
					sendIntent.setType("text/plain");
					startActivity(Intent.createChooser(sendIntent, "Share Via"));
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							ShareThisAppActivity.this);
					builder.setMessage("You do not have a Mobikwik wallet yet, you'll need it to receive the cashback. Would you like to create one now?");
					builder.setCancelable(false);

					builder.setPositiveButton("YES",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent mainIntent = new Intent(
											ShareThisAppActivity.this,
											FirstLoginWalletsActivity.class);
									mainIntent.putExtra("from", "wallet");
									startActivity(mainIntent);
								}
							});

					builder.setNegativeButton("NO",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									shareAppWithoutReferralCode();
								}
							});

					builder.show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private void checksumInvalidToast() {
		Log.e("ShareThisAppActivity", "Response checksum does not match!!");
		Toast.makeText(ShareThisAppActivity.this,
				"Something went wrong, please try again", Toast.LENGTH_LONG)
				.show();
	}

	private void shareAppWithoutReferralCode() {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent
				.putExtra(
						Intent.EXTRA_TEXT,
						"I am using this cool app 'ClubMyCab' to share & book cabs. Check it out @ https://play.google.com/store/apps/details?id=com.clubmycab.");
		sendIntent.setType("text/plain");
		startActivity(Intent.createChooser(sendIntent, "Share Via"));
	}

}
