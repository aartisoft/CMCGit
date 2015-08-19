package com.clubmycab.ui;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.clubmycab.R;
import com.clubmycab.asynctasks.GlobalAsyncTask;
import com.clubmycab.asynctasks.GlobalAsyncTask.AsyncTaskResultListener;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;

public class RateCabActivity extends Activity implements AsyncTaskResultListener{

	private JSONArray cabsJSONArray;
	private JSONObject selectedJsonObject;
	private String mobileNumber;
	private String cabIDIntent;
	private String notificationIDIntent;

	

	private String cabratingresp;
	private HashMap<String, JSONObject> hashMap;
	ArrayList<String> arrayListCabs;

	private String currentRating;
	boolean exceptioncheck = false;

	// private TextView TextViewRateCab;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_rate_cab);

		setResult(Activity.RESULT_CANCELED);

		ImageView imageView = (ImageView) findViewById(R.id.sidemenu);
		imageView.setVisibility(View.GONE);
		imageView = (ImageView) findViewById(R.id.notificationimg);
		imageView.setVisibility(View.GONE);

		
		mobileNumber = getIntent().getStringExtra("CabsRatingMobileNumber");
		cabIDIntent = getIntent().getStringExtra("cabIDIntent");
		notificationIDIntent = getIntent().getStringExtra(
				"notificationIDString");
		
		
String comefrom = getIntent().getStringExtra("comefrom");
		
		
		if (comefrom != null) {

			if (comefrom.equalsIgnoreCase("GCM")) {

				String params = "rnum=" + "&nid=" + notificationIDIntent;
				String endpoint = GlobalVariables.ServiceUrl
						+ "/UpdateNotificationStatusToRead.php";
				Log.d("RateCabActivity",
						"UpdateNotificationStatusToRead endpoint : " + endpoint
								+ " params : " + params);
				new GlobalAsyncTask(this, endpoint, params, null, this, false, "UpdateNotificationStatusToRead", false);

			}

		}
		
		

		hashMap = new HashMap<String, JSONObject>();

		try {
			cabsJSONArray = new JSONArray(getIntent().getStringExtra(
					"CabsJSONArrayString"));
			for (int i = 0; i < cabsJSONArray.length(); i++) {
				Log.d("RateCab", "cabsJSONArray : "
						+ cabsJSONArray.getJSONObject(i).get("CabName")
								.toString()
						+ " "
						+ cabsJSONArray.getJSONObject(i).get("CarType")
								.toString());
				try {
					String cabName = cabsJSONArray.getJSONObject(i)
							.get("CabName").toString();
					String carType = cabsJSONArray.getJSONObject(i)
							.get("CarType").toString();
					if (!cabName.isEmpty() && !cabName.equalsIgnoreCase("null")) {
						if (!carType.isEmpty()
								&& !carType.equalsIgnoreCase("null")) {
							hashMap.put(
									cabsJSONArray.getJSONObject(i)
											.get("CabName").toString()
											+ " ("
											+ cabsJSONArray.getJSONObject(i)
													.get("CarType").toString()
											+ ")",
									cabsJSONArray.getJSONObject(i));
						} else {
							hashMap.put(
									cabsJSONArray.getJSONObject(i)
											.get("CabName").toString(),
									cabsJSONArray.getJSONObject(i));
						}
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		ArrayList<String> arrayList = new ArrayList<String>();
		for (String string : hashMap.keySet()) {
			arrayList.add(string);
		}

		arrayListCabs = arrayList;

		Spinner spinner = (Spinner) findViewById(R.id.spinnerRateCab);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
				R.layout.list_item_rate_cab, arrayListCabs);
		arrayAdapter.setDropDownViewResource(R.layout.list_item_rate_cab);
		spinner.setAdapter(arrayAdapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d("RateCab",
						"spinner onItemSelected : "
								+ arrayListCabs.get(position));
				selectedJsonObject = hashMap.get(arrayListCabs.get(position)
						.toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

		RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBarRateCab);
		ratingBar
				.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

					@Override
					public void onRatingChanged(RatingBar ratingBar,
							float progress, boolean fromUser) {
						Log.d("RateCab", "ratingBar onRatingChanged : "
								+ progress);
						currentRating = Float.toString(progress);
					}
				});

		// TextViewRateCab = (TextView)findViewById(R.id.textViewRateCabRating);
		//
		// SeekBar seekBar = (SeekBar)findViewById(R.id.seekBarRateCab);
		// seekBar.getProgressDrawable().setColorFilter(new
		// PorterDuffColorFilter(R.color.seek_bar_color,
		// PorterDuff.Mode.MULTIPLY));
		// seekBar.setOnSeekBarChangeListener(new
		// SeekBar.OnSeekBarChangeListener() {
		//
		// @Override
		// public void onStopTrackingTouch(SeekBar seekBar) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onStartTrackingTouch(SeekBar seekBar) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onProgressChanged(SeekBar seekBar, int progress, boolean
		// fromUser) {
		// // TODO Auto-generated method stub
		// // Log.d("RateCab", "seekBar onProgressChanged : " + progress);
		// TextViewRateCab.setText(String.format("%1.1f", (1.0f * progress /
		// 10)));
		// }
		// });

		Button button = (Button) findViewById(R.id.buttonRateCab);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

				try {
					String cabDetailID = selectedJsonObject.get("CabDetailID")
							.toString();
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new ConnectionTaskForCabRating().executeOnExecutor(
								AsyncTask.THREAD_POOL_EXECUTOR, cabDetailID,
								cabIDIntent, currentRating);
					} else {
						new ConnectionTaskForCabRating().execute(cabDetailID,
								cabIDIntent, currentRating);
					}
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(RateCabActivity.this,
							"Something went wrong, please try again",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

	}

	private class ConnectionTaskForCabRating extends
			AsyncTask<String, Void, Void> {

		private ProgressDialog dialog = new ProgressDialog(RateCabActivity.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionCabRating mAuth1 = new AuthenticateConnectionCabRating();
			try {
				mAuth1.cabDetailID = args[0];
				mAuth1.cabID = args[1];
				mAuth1.Rating = args[2];
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
				Toast.makeText(RateCabActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (cabratingresp.isEmpty() || cabratingresp == null
					|| !cabratingresp.toLowerCase().contains("success")) {
				Toast.makeText(RateCabActivity.this,
						"Something went wrong, please try again",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(RateCabActivity.this, "Thank you for the feedback!",
						Toast.LENGTH_SHORT).show();

				if(notificationIDIntent.equalsIgnoreCase("")){
					
				}else{
				Intent intent = new Intent();
				intent.putExtra("notificationID", notificationIDIntent);
				setResult(Activity.RESULT_OK, intent);
				}

				finish();
			}

		}

	}

	public class AuthenticateConnectionCabRating {

		public String cabID;
		public String cabDetailID;
		public String Rating;

		public AuthenticateConnectionCabRating() {

		}

		public void connection() throws Exception {
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl + "/cmcCabRating.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair CabDetailIDNameValuePair = new BasicNameValuePair(
					"CabDetailID", cabDetailID);
			BasicNameValuePair CabIDNameValuePair = new BasicNameValuePair(
					"CabID", cabID);
			BasicNameValuePair RatingNameValuePair = new BasicNameValuePair(
					"Rating", Rating);
			BasicNameValuePair MobileNameValuePair = new BasicNameValuePair(
					"MobileNumber", mobileNumber);
			// Log.d("AllNotificationRequest",
			// "AuthenticateConnectionCabRating cabID : " + cabID);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(CabDetailIDNameValuePair);
			nameValuePairList.add(CabIDNameValuePair);
			nameValuePairList.add(RatingNameValuePair);
			nameValuePairList.add(MobileNameValuePair);

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
				cabratingresp = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("cabratingresp", "" + stringBuilder.toString());
		}
	}

	@Override
	public void getResult(String response, String uniqueID) {
		// TODO Auto-generated method stub
		
	}

}
