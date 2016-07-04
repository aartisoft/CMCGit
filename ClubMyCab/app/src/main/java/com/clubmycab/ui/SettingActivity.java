package com.clubmycab.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.R;
import com.clubmycab.utility.AppConstants;
import com.clubmycab.utility.FontTypeface;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends Activity {

	private ImageView notificationimg;
	private TextView about;
	private String FullName;
	private String MobileNumber;
	private RelativeLayout unreadnoticountrl;
	private TextView unreadnoticount;
	private RelativeLayout pushnotificationsonoffrl;
	private ImageView pushonoffimg;
	private RelativeLayout sharelocationtimerl;
	private TextView sharelocationtimevalue;
	private RelativeLayout favoritelocationsrl;
	private LinearLayout settingsrl;
	private Tracker tracker;
	private boolean exceptioncheck = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_details);

		// Check if Internet present
		if (!isOnline()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
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
		GoogleAnalytics analytics = GoogleAnalytics.getInstance(SettingActivity.this);
		tracker = analytics.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);
		tracker.setScreenName("Settings");
        ((TextView)findViewById(R.id.tvHeading)).setTypeface(FontTypeface.getTypeface(SettingActivity.this, AppConstants.HELVITICA));
        ((TextView)findViewById(R.id.tvHeading)).setText("Settings");
        findViewById(R.id.flBackArrow).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(SettingActivity.this, NewHomeScreen.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivityForResult(mainIntent, 500);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
		((TextView)findViewById(R.id.tvPushNotification)).setTypeface(FontTypeface.getTypeface(SettingActivity.this, AppConstants.HELVITICA));
		((TextView)findViewById(R.id.sharelocationtimerlhereiam)).setTypeface(FontTypeface.getTypeface(SettingActivity.this, AppConstants.HELVITICA));
		((TextView)findViewById(R.id.tvSendLoc)).setTypeface(FontTypeface.getTypeface(SettingActivity.this, AppConstants.HELVITICA));
		((TextView)findViewById(R.id.sharelocationtimevalue)).setTypeface(FontTypeface.getTypeface(SettingActivity.this, AppConstants.HELVITICA));
		((TextView)findViewById(R.id.tvFavLoc)).setTypeface(FontTypeface.getTypeface(SettingActivity.this, AppConstants.HELVITICA));
		settingsrl = (LinearLayout) findViewById(R.id.settingsrl);
		settingsrl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("settingsrl", "settingsrl");
			}
		});
    	notificationimg = (ImageView) findViewById(R.id.notificationimg);
		SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
		FullName = mPrefs.getString("FullName", "");
		MobileNumber = mPrefs.getString("MobileNumber", "");
		findViewById(R.id.flNotifications).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent mainIntent = new Intent(SettingActivity.this,
						NotificationListActivity.class);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);

			}
		});
		unreadnoticountrl = (RelativeLayout) findViewById(R.id.unreadnoticountrl);
		unreadnoticount = (TextView) findViewById(R.id.unreadnoticount);
		if (!TextUtils.isEmpty(GlobalVariables.UnreadNotificationCount) && GlobalVariables.UnreadNotificationCount.equalsIgnoreCase("0")) {

			unreadnoticountrl.setVisibility(View.GONE);

		} else {

			unreadnoticountrl.setVisibility(View.VISIBLE);
			unreadnoticount.setText(GlobalVariables.UnreadNotificationCount);
		}

		SharedPreferences mPrefs1111 = getSharedPreferences("usersettings", 0);
		String switchstatus = mPrefs1111.getString("switchstatus", "");
		String sharelocationinterval = mPrefs1111.getString("sharelocationinterval", "");
		pushnotificationsonoffrl = (RelativeLayout) findViewById(R.id.pushnotificationsonoffrl);
		pushonoffimg = (ImageView) findViewById(R.id.pushonoffimg);
		sharelocationtimerl = (RelativeLayout) findViewById(R.id.sharelocationtimerl);
		sharelocationtimevalue = (TextView) findViewById(R.id.sharelocationtimevalue);
		favoritelocationsrl = (RelativeLayout) findViewById(R.id.favoritelocationsrl);
		if (switchstatus.isEmpty() || switchstatus == null || switchstatus.equalsIgnoreCase("")) {
			pushonoffimg.setImageDrawable(getResources().getDrawable(R.drawable.btn_on));
		} else if (switchstatus.trim().equalsIgnoreCase("on")) {
			pushonoffimg.setImageDrawable(getResources().getDrawable(R.drawable.btn_on));
		} else {
			pushonoffimg.setImageDrawable(getResources().getDrawable(R.drawable.btn_off));
		}

		if (sharelocationinterval.isEmpty() || sharelocationinterval == null || sharelocationinterval.equalsIgnoreCase("")) {
			sharelocationtimevalue.setText("2 minutes");
		} else {
			sharelocationtimevalue.setText(sharelocationinterval);
		}

		pushnotificationsonoffrl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				SharedPreferences mPrefs1111 = getSharedPreferences(
						"usersettings", 0);
				String switchstatus = mPrefs1111.getString("switchstatus", "");

				if (switchstatus.isEmpty() || switchstatus == null
						|| switchstatus.equalsIgnoreCase("")) {

					// //on hai
					pushonoffimg.setImageDrawable(getResources().getDrawable(
							R.drawable.btn_off));

					SharedPreferences.Editor editor = mPrefs1111.edit();
					editor.putString("switchstatus", "off");
					editor.commit();

					tracker.send(new HitBuilders.EventBuilder()
							.setCategory("Push Notification On")
							.setAction("Push Notification On")
							.setLabel("Push Notification On").build());

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new updatepushnotificationstatus().executeOnExecutor(
								AsyncTask.THREAD_POOL_EXECUTOR, "off");
					} else {
						new updatepushnotificationstatus().execute("off");
					}

				} else if (switchstatus.trim().equalsIgnoreCase("on")) {

					// //on hai
					pushonoffimg.setImageDrawable(getResources().getDrawable(
							R.drawable.btn_off));

					SharedPreferences.Editor editor = mPrefs1111.edit();
					editor.putString("switchstatus", "off");
					editor.commit();

					tracker.send(new HitBuilders.EventBuilder()
							.setCategory("Set Push Notifications Off")
							.setAction("Set Push Notifications Off")
							.setLabel("Set Push Notifications Off").build());

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new updatepushnotificationstatus().executeOnExecutor(
								AsyncTask.THREAD_POOL_EXECUTOR, "off");
					} else {
						new updatepushnotificationstatus().execute("off");
					}

				} else {

					// //off hai
					pushonoffimg.setImageDrawable(getResources().getDrawable(
							R.drawable.btn_on));

					SharedPreferences.Editor editor = mPrefs1111.edit();
					editor.putString("switchstatus", "on");
					editor.commit();

					tracker.send(new HitBuilders.EventBuilder()
							.setCategory("Set Push Notifications On")
							.setAction("Set Push Notifications On")
							.setLabel("Set Push Notifications On").build());

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new updatepushnotificationstatus().executeOnExecutor(
								AsyncTask.THREAD_POOL_EXECUTOR, "on");
					} else {
						new updatepushnotificationstatus().execute("on");
					}

				}
			}
		});

		sharelocationtimerl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				tracker.send(new HitBuilders.EventBuilder()
						.setCategory("Favourite locations")
						.setAction("Favourite locations")
						.setLabel("Favourite locations").build());
				AlertDialog dialog;
				AlertDialog.Builder builder = new AlertDialog.Builder(
						SettingActivity.this);
				builder.setTitle(null);

				final CharSequence str[] = { "2 minutes", "5 minutes",
						"10 minutes" };

				builder.setItems(str, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int position) {
						// TODO Auto-generated method stub

						sharelocationtimevalue.setText(str[position]);

						SharedPreferences sharedPreferences = getSharedPreferences(
								"usersettings", 0);
						SharedPreferences.Editor editor = sharedPreferences
								.edit();
						editor.putString("sharelocationinterval", str[position]
								.toString().trim());
						editor.commit();
					}
				});

				dialog = builder.create();
				dialog.show();

			}
		});

		favoritelocationsrl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Pawan change replace FavoriteLocationsActivity with
				// FavoriteLocaitonActivityUpdate
				Intent intent = new Intent(SettingActivity.this,
						FavoriteLocationsAcivity.class);
				intent.putExtra("NotFromRegistration", true);
				startActivity(intent);
			}
		});
	}

	private class updatepushnotificationstatus extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(String... args) {
			Authenticateupdatepushnotificationstatus mAuth1 = new Authenticateupdatepushnotificationstatus();
			try {
				mAuth1.status = args[0];
				mAuth1.connection();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			
			try{
				if (exceptioncheck) {
					exceptioncheck = false;
					Toast.makeText(SettingActivity.this,
							getResources().getString(R.string.exceptionstring),
							Toast.LENGTH_LONG).show();
					return;
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}

	}

	public class Authenticateupdatepushnotificationstatus {

		public String status;

		public Authenticateupdatepushnotificationstatus() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl + "/updatepushnotificationstatus.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair("MobileNumber", MobileNumber.trim());
			BasicNameValuePair PasswordBasicNameValuePair = new BasicNameValuePair("PushStatus", status);
			String authString = MobileNumber.trim() + status;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth", GlobalMethods.calculateCMCAuthString(authString));
			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MobileNumberBasicNameValuePair);
			nameValuePairList.add(PasswordBasicNameValuePair);
			nameValuePairList.add(authValuePair);
			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			StringBuilder stringBuilder = new StringBuilder();
			String bufferedStrChunk = null;
			String updatepushresponse = "";
			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				updatepushresponse = stringBuilder.append(bufferedStrChunk)
						.toString();
			}
			Log.d("updatepushresponse", "" + stringBuilder.toString());
			if (updatepushresponse != null && updatepushresponse.length() > 0 && updatepushresponse.contains("Unauthorized Access")) {
				Log.e("SettingActivity", "updatepushresponse Unauthorized Access");
				exceptioncheck = true;
				return;
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

		Intent mainIntent = new Intent(SettingActivity.this, NewHomeScreen.class);
		mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivityForResult(mainIntent, 500);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
}
