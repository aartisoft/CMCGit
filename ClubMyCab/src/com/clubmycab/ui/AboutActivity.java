package com.clubmycab.ui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.clubmycab.CircularImageView;
import com.clubmycab.R;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.navdrawer.SimpleSideDrawer;

public class AboutActivity extends Activity {
	//
	CircularImageView profilepic;
	TextView username;
	ImageView notificationimg;

	ImageView sidemenu;

	private SimpleSideDrawer mNav;
	CircularImageView drawerprofilepic;
	TextView drawerusername;

	TextView myprofile;
	TextView myrides;
	TextView bookacab;
	TextView sharemylocation;
	TextView myclubs;
	TextView sharethisapp;
	TextView mypreferences;
	TextView about;

	String FullName;
	String MobileNumber;

	RelativeLayout unreadnoticountrl;
	TextView unreadnoticount;

	String readunreadnotiresp;

	RelativeLayout aboutrl;

	Tracker tracker;
	boolean exceptioncheck = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		// Check if Internet present
		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					AboutActivity.this);
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
				.getInstance(AboutActivity.this);
		tracker = analytics
				.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);

		// All subsequent hits will be send with screen name = "main screen"
		tracker.setScreenName("About");

		aboutrl = (RelativeLayout) findViewById(R.id.aboutrl);
		aboutrl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Log.d("aboutrl", "aboutrl");
			}
		});

		UniversalDrawer drawer = new UniversalDrawer(this, tracker);
		drawer.createDrawer();

		profilepic = (CircularImageView) findViewById(R.id.profilepic);
		notificationimg = (ImageView) findViewById(R.id.notificationimg);
		drawerprofilepic = (CircularImageView) findViewById(R.id.drawerprofilepic);

		SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
		FullName = mPrefs.getString("FullName", "");
		MobileNumber = mPrefs.getString("MobileNumber", "");

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

				Intent mainIntent = new Intent(AboutActivity.this,
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

		// ///////////////
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		// new ConnectionTaskForreadunreadnotification()
		// .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		// } else {
		// new ConnectionTaskForreadunreadnotification().execute();
		// }

		// ///////////////
		SharedPreferences mPrefs111 = getSharedPreferences("userimage", 0);
		String imgname = mPrefs111.getString("imgname", "");
		String imagestr = mPrefs111.getString("imagestr", "");

		if (imagestr.isEmpty() || imagestr == null
				|| imagestr.equalsIgnoreCase("")) {
		} else {

			byte[] b = Base64.decode(imagestr, Base64.DEFAULT);
			InputStream is = new ByteArrayInputStream(b);
			Bitmap yourSelectedImage = BitmapFactory.decodeStream(is);

			profilepic.setImageBitmap(yourSelectedImage);
			drawerprofilepic.setImageBitmap(yourSelectedImage);
		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		Intent mainIntent = new Intent(AboutActivity.this, NewHomeScreen.class);
		mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivityForResult(mainIntent, 500);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	// ///////
	// private class ConnectionTaskForreadunreadnotification extends
	// AsyncTask<String, Void, Void> {
	//
	// @Override
	// protected void onPreExecute() {
	//
	// }
	//
	// @Override
	// protected Void doInBackground(String... args) {
	// AuthenticateConnectionreadunreadnotification mAuth1 = new
	// AuthenticateConnectionreadunreadnotification();
	// try {
	// mAuth1.connection();
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// exceptioncheck = true;
	// e.printStackTrace();
	// }
	// return null;
	// }
	//
	// @Override
	// protected void onPostExecute(Void v) {
	//
	// if (exceptioncheck) {
	// exceptioncheck = false;
	// Toast.makeText(AboutActivity.this,
	// getResources().getString(R.string.exceptionstring),
	// Toast.LENGTH_LONG).show();
	// return;
	// }
	//
	// if (readunreadnotiresp.equalsIgnoreCase("0")) {
	//
	// unreadnoticountrl.setVisibility(View.GONE);
	//
	// } else {
	//
	// unreadnoticountrl.setVisibility(View.VISIBLE);
	// unreadnoticount.setText(readunreadnotiresp);
	// }
	// }
	//
	// }
	//
	// public class AuthenticateConnectionreadunreadnotification {
	//
	// public AuthenticateConnectionreadunreadnotification() {
	//
	// }
	//
	// public void connection() throws Exception {
	//
	// // Connect to google.com
	// HttpClient httpClient = new DefaultHttpClient();
	// String url_select = GlobalVariables.ServiceUrl
	// + "/FetchUnreadNotificationCount.php";
	// HttpPost httpPost = new HttpPost(url_select);
	// BasicNameValuePair MobileNumberBasicNameValuePair = new
	// BasicNameValuePair(
	// "MobileNumber", MobileNumber);
	//
	// List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
	// nameValuePairList.add(MobileNumberBasicNameValuePair);
	//
	// UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
	// nameValuePairList);
	// httpPost.setEntity(urlEncodedFormEntity);
	// HttpResponse httpResponse = httpClient.execute(httpPost);
	//
	// Log.d("httpResponse", "" + httpResponse);
	//
	// InputStream inputStream = httpResponse.getEntity().getContent();
	// InputStreamReader inputStreamReader = new InputStreamReader(
	// inputStream);
	//
	// BufferedReader bufferedReader = new BufferedReader(
	// inputStreamReader);
	//
	// StringBuilder stringBuilder = new StringBuilder();
	//
	// String bufferedStrChunk = null;
	//
	// while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
	// readunreadnotiresp = stringBuilder.append(bufferedStrChunk)
	// .toString();
	// }
	//
	// Log.d("readunreadnotiresp", "" + readunreadnotiresp);
	//
	// }
	// }

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}
}
