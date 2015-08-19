package com.clubmycab;

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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;

public class StartTripPHPAlarm extends WakefulBroadcastReceiver {

	private static final String CAB_ID_KEY = "CabIDKey";
	private static final int ALARM_ID = 3;

	private boolean exceptioncheck = false;

	@Override
	public void onReceive(Context context, Intent intent) {

		String CabId = intent.getStringExtra(CAB_ID_KEY);

		Log.d("StartTripPHPAlarm", "onReceive CabId : " + CabId);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new ConnectionTaskForStartTrip().executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, CabId);
		} else {
			new ConnectionTaskForStartTrip().execute(CabId);
		}

	}

	public void setAlarm(Context context, String cabID, int callAfter) {

		Log.d("StartTripPHPAlarm", "setAlarm callAfter : " + callAfter
				+ " cabID : " + cabID);

		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent(context, StartTripPHPAlarm.class);
		intent.putExtra(CAB_ID_KEY, cabID);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
				ALARM_ID, intent, 0);
		alarmManager.set(AlarmManager.RTC_WAKEUP,
				(System.currentTimeMillis() + (callAfter * 60 * 1000)),
				pendingIntent);
	}

	public void cancelAlarm(Context context) {
		Intent intent = new Intent(context, StartTripPHPAlarm.class);

		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		PendingIntent sender = PendingIntent.getBroadcast(context, ALARM_ID,
				intent, 0);
		alarmManager.cancel(sender);
	}

	private class ConnectionTaskForStartTrip extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionStartTrip mAuth1 = new AuthenticateConnectionStartTrip();
			try {
				mAuth1.cid = args[0];

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

			if (exceptioncheck) {
				exceptioncheck = false;
				// Toast.makeText(CheckPoolFragmentActivity.this,
				// getResources().getString(R.string.exceptionstring),
				// Toast.LENGTH_LONG).show();
				return;
			}
		}

	}

	public class AuthenticateConnectionStartTrip {

		public String cid;

		public AuthenticateConnectionStartTrip() {

		}

		public void connection() throws Exception {

			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/startTripNotification.php";
			HttpPost httpPost = new HttpPost(url_select);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();

			BasicNameValuePair CabIdValuePair = new BasicNameValuePair("cabId",
					cid);

			nameValuePairList.add(CabIdValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;
			String startresp = null;

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				startresp = stringBuilder.append(bufferedStrChunk).toString();
			}

			Log.d("startresp", "" + startresp);
		}
	}

}
