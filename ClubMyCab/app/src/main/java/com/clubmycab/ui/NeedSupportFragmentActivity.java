package com.clubmycab.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.R;
import com.clubmycab.adapter.SimpleSpinnerAdapter;
import com.clubmycab.asynctasks.GlobalAsyncTask;
import com.clubmycab.utility.AppConstants;
import com.clubmycab.utility.FontTypeface;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.clubmycab.xmlhandler.FetchUnreadNotificationCountHandler;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class NeedSupportFragmentActivity extends FragmentActivity implements GlobalAsyncTask.AsyncTaskResultListener{
	private Context ctx;
	static int containerId;
	private EditText etDescription;
	private Button btnSubmitReport;
	private String response;
	private Spinner spinnerNeedSupport;
	private CheckBox cbCallMe;
	private boolean exceptioncheck = false;
	private String cbSelected = "No";
	private String spinnerStr = "Ask a question";
	private Tracker tracker;
	private AppEventsLogger logger;
	private ImageView notificationimg;
	private String FullName, MobileNumber, myprofileresp, imagenameresp;
	private TextView  unreadnoticount;
	private RelativeLayout unreadnoticountrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_layout_needsupport);
        ((TextView) findViewById(R.id.tvHeading)).setTypeface(FontTypeface.getTypeface(NeedSupportFragmentActivity.this, AppConstants.HELVITICA));
        ((TextView) findViewById(R.id.tvHeading)).setText("Support");
        findViewById(R.id.flBackArrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(NeedSupportFragmentActivity.this, NewHomeScreen.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivityForResult(mainIntent, 500);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
		((TextView)findViewById(R.id.tvNeedSupport)).setTypeface(FontTypeface.getTypeface(NeedSupportFragmentActivity.this, AppConstants.HELVITICA));
		((TextView)findViewById(R.id.tvDescription)).setTypeface(FontTypeface.getTypeface(NeedSupportFragmentActivity.this, AppConstants.HELVITICA));
		((TextView)findViewById(R.id.tvWeWill)).setTypeface(FontTypeface.getTypeface(NeedSupportFragmentActivity.this, AppConstants.HELVITICA));
		ctx = this;
		setNotificationAndProfileImage();
		setLayoutParams();

	}

	private void setNotificationAndProfileImage() {
		GoogleAnalytics analytics = GoogleAnalytics.getInstance(NeedSupportFragmentActivity.this);
		tracker = analytics.newTracker(GlobalVariables.GoogleAnalyticsTrackerId);
		tracker.setScreenName("HomePage");
		logger = AppEventsLogger.newLogger(this);
		GlobalVariables.ActivityName = "NewRideCreationScreen";
		notificationimg = (ImageView) findViewById(R.id.notificationimg);
		SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
		FullName = mPrefs.getString("FullName", "");
		MobileNumber = mPrefs.getString("MobileNumber", "");
		unreadnoticountrl = (RelativeLayout) findViewById(R.id.unreadnoticountrl);
		unreadnoticount = (TextView) findViewById(R.id.unreadnoticount);
		findViewById(R.id.flNotifications).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent mainIntent = new Intent(NeedSupportFragmentActivity.this,
						NotificationListActivity.class);
				startActivityForResult(mainIntent, 500);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);

			}
		});
		String endpoint = GlobalVariables.ServiceUrl + "/FetchUnreadNotificationCount.php";
		String authString = MobileNumber;
		String params = "MobileNumber=" + MobileNumber + "&auth=" + GlobalMethods.calculateCMCAuthString(authString);
		new GlobalAsyncTask(this, endpoint, params, new FetchUnreadNotificationCountHandler(), this, false, "FetchUnreadNotificationCount", false);

	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();

		Intent mainIntent = new Intent(NeedSupportFragmentActivity.this,
				NewHomeScreen.class);
		mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivityForResult(mainIntent, 500);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	public void setLayoutParams() {

		cbCallMe = (CheckBox) findViewById(R.id.cbCallMe);

		spinnerNeedSupport = (Spinner) findViewById(R.id.spinnerNeedSupport);

		ArrayList<String> list = new ArrayList<String>();
		list.add("Ask a question");
		list.add("Report a problem");

		// spinnerNeedSupport.setAdapter(adapter);
		SimpleSpinnerAdapter adapterGame = new SimpleSpinnerAdapter(ctx,
				R.layout.item_spin_support, R.id.textview, list, true);
		adapterGame
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinnerNeedSupport.setAdapter(adapterGame);

		spinnerNeedSupport
				.setOnItemSelectedListener(new CustomOnItemSelectedListener());

		etDescription = (EditText) findViewById(R.id.etDescription);

		btnSubmitReport = (Button) findViewById(R.id.btnSubmitReport);
		btnSubmitReport.setTypeface(FontTypeface.getTypeface(NeedSupportFragmentActivity.this, AppConstants.HELVITICA));
		btnSubmitReport.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (isValid()) {
					if (cbCallMe.isChecked())
						cbSelected = "Yes";
					else
						cbSelected = "No";

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new ConnectionTaskForFetchPool()
								.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					} else {
						new ConnectionTaskForFetchPool().execute();
					}
				}

			}
		});



	}



	public class CustomOnItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {

			spinnerStr = parent.getItemAtPosition(pos).toString();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	public boolean isValid() {

		if (etDescription.getText().toString().trim().equals("")) {
			Toast.makeText(ctx, "Description must be set.", Toast.LENGTH_LONG)
					.show();

			return false;
		}
		return true;
	}

	private class ConnectionTaskForFetchPool extends
			AsyncTask<String, Void, Void> {
		private ProgressDialog dialog = new ProgressDialog(
				NeedSupportFragmentActivity.this);

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
			AuthenticateConnectionFetchPool mAuth1 = new AuthenticateConnectionFetchPool();
			try {
				mAuth1.connection();
			} catch (Exception e) {
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
					dialog = null;
				}

				if (exceptioncheck) {
					exceptioncheck = false;
					Toast.makeText(NeedSupportFragmentActivity.this,
							getResources().getString(R.string.exceptionstring),
							Toast.LENGTH_LONG).show();
					return;
				}

				if (response.contains("Unauthorized Access")) {
					Log.e("NeedSupportFragmentActivity",
							"response Unauthorized Access");
					Toast.makeText(NeedSupportFragmentActivity.this,
							getResources().getString(R.string.exceptionstring),
							Toast.LENGTH_LONG).show();
					return;
				}

				String status = "";
				try {
					JSONObject obj = new JSONObject(response);
					status = obj.getString("status");
				} catch (Exception e) {
					status = "";

				}
				if (status.equalsIgnoreCase("success")) {
					Toast.makeText(
							NeedSupportFragmentActivity.this,
							"We have received your request, and we will get back to you soon ",
							Toast.LENGTH_LONG).show();
					Intent mainIntent = new Intent(
							NeedSupportFragmentActivity.this, NewHomeScreen.class);
					mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivityForResult(mainIntent, 500);
					overridePendingTransition(R.anim.slide_in_right,
							R.anim.slide_out_left);
					finish();
				} else {
					Toast.makeText(NeedSupportFragmentActivity.this,
							getResources().getString(R.string.exceptionstring),
							Toast.LENGTH_LONG).show();

				}
			}catch (Exception e){
				e.printStackTrace();
			}

		}

	}

	public class AuthenticateConnectionFetchPool {

		public AuthenticateConnectionFetchPool() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
			String MobileNumber = mPrefs.getString("MobileNumber", "");
			String FullName = mPrefs.getString("FullName", "");

			HttpClient httpClient = new DefaultHttpClient();
			String url_select11 = GlobalVariables.ServiceUrl
					+ "/customerQuery.php";
			HttpPost httpPost = new HttpPost(url_select11);

			BasicNameValuePair DescriptionBasicNameValuePair = new BasicNameValuePair(
					"desciption", etDescription.getText().toString());
			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
					"mobileNumber", MobileNumber);

			BasicNameValuePair FullNameBasicNameValuePair = new BasicNameValuePair(
					"name", FullName);
			BasicNameValuePair TypeBasicNameValuePair = new BasicNameValuePair(
					"type", spinnerStr);

			BasicNameValuePair CallBasicNameValuePair = new BasicNameValuePair(
					"callback", cbSelected);

			String authString = cbSelected + etDescription.getText().toString()
					+ MobileNumber + FullName + spinnerStr;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();

			nameValuePairList.add(DescriptionBasicNameValuePair);
			nameValuePairList.add(MobileNumberBasicNameValuePair);
			nameValuePairList.add(FullNameBasicNameValuePair);
			nameValuePairList.add(TypeBasicNameValuePair);
			nameValuePairList.add(CallBasicNameValuePair);
			nameValuePairList.add(authValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			Log.d("httpResponse rideInvitations", "" + httpResponse);

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				response = stringBuilder.append(bufferedStrChunk).toString();
			}

			Log.d("customerQuery", "" + stringBuilder.toString());
		}
	}
	/**
	 * Networ Response-------------------------------------------------->
	 */
	@Override
	public void getResult(String response, String uniqueID) {
		if (uniqueID.equals("FetchUnreadNotificationCount")) {
			if (response != null && response.length() > 0
					&& response.contains("Unauthorized Access")) {
				Log.e("HomeActivity",
						"FetchUnreadNotificationCount Unauthorized Access");
				Toast.makeText(NeedSupportFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (!TextUtils.isEmpty(GlobalVariables.UnreadNotificationCount) && GlobalVariables.UnreadNotificationCount.equalsIgnoreCase("0")) {

				unreadnoticountrl.setVisibility(View.GONE);

			} else {

				unreadnoticountrl.setVisibility(View.VISIBLE);
				unreadnoticount
						.setText(GlobalVariables.UnreadNotificationCount);
			}
		}
	}
}
