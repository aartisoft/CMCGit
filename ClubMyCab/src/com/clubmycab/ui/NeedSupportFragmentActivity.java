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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.clubmycab.R;
import com.clubmycab.adapter.SimpleSpinnerAdapter;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;

public class NeedSupportFragmentActivity extends FragmentActivity {
	private Context ctx;
	static int containerId;

	private ImageView ivBackReportProblem;
	private EditText etDescription;
	private Button btnSubmitReport;
	private String response;
	private Spinner spinnerNeedSupport;
	private CheckBox cbCallMe;

	boolean exceptioncheck = false;
	private String cbSelected = "No";
	private String spinnerStr = "Ask a question";
	
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_layout_needsupport);
		ctx = this;

		setLayoutParams();

	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();

		Intent mainIntent = new Intent(NeedSupportFragmentActivity.this,
				HomeActivity.class);
		mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivityForResult(mainIntent, 500);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	public void setLayoutParams() {

		ivBackReportProblem = (ImageView) findViewById(R.id.ivBackReportProblem);
		cbCallMe = (CheckBox) findViewById(R.id.cbCallMe);

		spinnerNeedSupport = (Spinner) findViewById(R.id.spinnerNeedSupport);

		ArrayList<String> list = new ArrayList<String>();
		list.add("Ask a question");
		list.add("Report a problem");

		// spinnerNeedSupport.setAdapter(adapter);
		SimpleSpinnerAdapter adapterGame = new SimpleSpinnerAdapter(ctx,
				R.layout.layout_simple_snipper, R.id.textview, list, true);
		adapterGame
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinnerNeedSupport.setAdapter(adapterGame);

		spinnerNeedSupport
				.setOnItemSelectedListener(new CustomOnItemSelectedListener());

		etDescription = (EditText) findViewById(R.id.etDescription);

		btnSubmitReport = (Button) findViewById(R.id.btnSubmitReport);

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

		ivBackReportProblem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
			}
		});

	}

	public class CustomOnItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			// Toast.makeText(
			// parent.getContext(),
			// "OnItemSelectedListener : "
			// + parent.getItemAtPosition(pos).toString(),
			// Toast.LENGTH_SHORT).show();
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
			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionFetchPool mAuth1 = new AuthenticateConnectionFetchPool();
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

			String status = "";
			try {
				JSONObject obj = new JSONObject(response);
				status = obj.getString("status");
			} catch (Exception e) {
				status = "";

			}
			if (status.equalsIgnoreCase("success")) {
				Toast.makeText(NeedSupportFragmentActivity.this,
						"Your request sent successfully", Toast.LENGTH_LONG)
						.show();
				Intent mainIntent = new Intent(NeedSupportFragmentActivity.this,
						HomeActivity.class);
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

			String Email = mPrefs.getString("Email", "");

			HttpClient httpClient = new DefaultHttpClient();
			String url_select11 = GlobalVariables.ServiceUrl
					+ "/rideInvitations.php";
			HttpPost httpPost = new HttpPost(url_select11);

			BasicNameValuePair DescriptionBasicNameValuePair = new BasicNameValuePair(
					"description", etDescription.getText().toString());
			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
					"MobileNumber", MobileNumber);
			BasicNameValuePair EmailBasicNameValuePair = new BasicNameValuePair(
					"email", Email);
			BasicNameValuePair FullNameBasicNameValuePair = new BasicNameValuePair(
					"fullname", FullName);
			BasicNameValuePair TypeBasicNameValuePair = new BasicNameValuePair(
					"type", spinnerStr);

			BasicNameValuePair CallBasicNameValuePair = new BasicNameValuePair(
					"call me to explain", cbSelected);
			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();

			nameValuePairList.add(DescriptionBasicNameValuePair);
			nameValuePairList.add(MobileNumberBasicNameValuePair);
			nameValuePairList.add(EmailBasicNameValuePair);
			nameValuePairList.add(FullNameBasicNameValuePair);
			nameValuePairList.add(TypeBasicNameValuePair);
			nameValuePairList.add(CallBasicNameValuePair);

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

			Log.d("poolresponse", "" + stringBuilder.toString());
		}
	}

}
