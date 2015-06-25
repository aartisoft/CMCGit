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
import org.json.JSONArray;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.clubmycab.utility.GlobalVariables;
import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	static int notificationID = 1;
	private static final String TAG = "GCMIntentService";
	
	String gotopoolresp;

	public GCMIntentService() {
		super("145246375713");
	}

	/**
	 * Method called on device registered
	 **/
	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i(TAG, "Device registered: regId = " + registrationId);
	}

	/**
	 * Method called on device un registred
	 * */
	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "Device unregistered");
	}

	/**
	 * Method called on Receiving a new message
	 * */
	@Override
	protected void onMessage(Context context, Intent intent) {

		String message = intent.getExtras().getString("gcmText");
		Log.i("message", "" + message);
		String pushfrom = intent.getExtras().getString("pushfrom");
		String CabId = intent.getExtras().getString("CabId");
		String MemberName = intent.getExtras().getString("MemberName");
		String oname = intent.getExtras().getString("oname");
		String onumber = intent.getExtras().getString("onumber");

		if (pushfrom != null && pushfrom.equalsIgnoreCase("groupchat")) {
			generatechatnotification(context, message, pushfrom, CabId,
					MemberName, oname, onumber);
		} else if (pushfrom != null
				&& pushfrom.equalsIgnoreCase("upcomingtrip")) {
			generateUpComingTrip(context, message, pushfrom, CabId);
		} else if (pushfrom != null
				&& pushfrom.equalsIgnoreCase("genericnotification")) {
			generateGenericnotification(context, message);
		} else {
			generateNewMsgNotification(context, message);
		}
	}

	/**
	 * Method called on receiving a deleted message
	 * */
	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "Received deleted messages notification");
	}

	/**
	 * Method called on Error
	 * */
	@Override
	public void onError(Context context, String errorId) {
		Log.i(TAG, "Received error: " + errorId);
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
		Log.i(TAG, "Received recoverable error: " + errorId);
		return super.onRecoverableError(context, errorId);
	}

	// ////////////////////
	public void generateNewMsgNotification(Context context, String message) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new ConnectionTaskForFetchMyClubs()
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			new ConnectionTaskForFetchMyClubs().execute();
		}

		int icon = R.drawable.cabappicon;
		notificationID++;

		Intent intent = new Intent(this, AllNotificationRequest.class);
		PendingIntent pIntent = PendingIntent.getActivity(this, notificationID,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context);
		Notification notification = mBuilder
				.setSmallIcon(icon)
				.setTicker("ClubMyCab")
				.setWhen(System.currentTimeMillis())
				.setAutoCancel(true)
				.setContentTitle("ClubMyCab")
				.setStyle(
						new NotificationCompat.BigTextStyle().bigText(message))
				.setContentIntent(pIntent)
				.setSound(
						RingtoneManager
								.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
				.setContentText(message).build();

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(notificationID, notification);

	}

	// ////////////////////
	public void generateGenericnotification(Context context, String message) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new ConnectionTaskForFetchMyClubs()
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			new ConnectionTaskForFetchMyClubs().execute();
		}

		int icon = R.drawable.cabappicon;
		notificationID++;

		Intent intent = new Intent(this, Splash.class);
		PendingIntent pIntent = PendingIntent.getActivity(this, notificationID,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context);
		Notification notification = mBuilder
				.setSmallIcon(icon)
				.setTicker("ClubMyCab")
				.setWhen(System.currentTimeMillis())
				.setAutoCancel(true)
				.setContentTitle("ClubMyCab")
				.setStyle(
						new NotificationCompat.BigTextStyle().bigText(message))
				.setContentIntent(pIntent)
				.setSound(
						RingtoneManager
								.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
				.setContentText(message).build();

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(notificationID, notification);

	}

	// ////////////////////
	public void generateUpComingTrip(Context context, String message,
			String pushfrom, String cabId) {

		TripCompletedtask val = new TripCompletedtask();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			val.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, cabId);
		} else {
			val.execute(cabId);
		}

		try {
			gotopoolresp = val.get();

			if (gotopoolresp.equalsIgnoreCase("This Ride no longer exist")) {
				int icon = R.drawable.cabappicon;
				notificationID++;

				Intent intent = new Intent(this, MyRides.class);
				PendingIntent pIntent = PendingIntent.getActivity(this,
						notificationID, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);

				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						context);
				Notification notification = mBuilder
						.setSmallIcon(icon)
						.setTicker("ClubMyCab")
						.setWhen(0)
						.setAutoCancel(true)
						.setContentTitle("ClubMyCab")
						.setStyle(
								new NotificationCompat.BigTextStyle()
										.bigText(message))
						.setContentIntent(pIntent)
						.setSound(
								RingtoneManager
										.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
						.setContentText(message).build();

				NotificationManager notificationManager = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);
				notificationManager.notify(notificationID, notification);
			} else {

				String CabIdstr = null;
				String MobileNumber = null;
				String OwnerName = null;
				String OwnerImage = null;
				String FromLocation = null;
				String ToLocation = null;
				String FromShortName = null;
				String ToShortName = null;
				String TravelDate = null;
				String TravelTime = null;
				String Seats = null;
				String RemainingSeats = null;
				String Distance = null;
				String OpenTime = null;
				String CabStatus = null;
				String Seat_Status = null;

				JSONArray subArray = new JSONArray(gotopoolresp);
				for (int i = 0; i < subArray.length(); i++) {

					CabIdstr = subArray.getJSONObject(i).getString("CabId")
							.toString();
					MobileNumber = subArray.getJSONObject(i)
							.getString("MobileNumber").toString();
					OwnerName = subArray.getJSONObject(i)
							.getString("OwnerName").toString();
					OwnerImage = subArray.getJSONObject(i)
							.getString("imagename").toString();
					FromLocation = subArray.getJSONObject(i)
							.getString("FromLocation").toString();
					ToLocation = subArray.getJSONObject(i)
							.getString("ToLocation").toString();

					FromShortName = subArray.getJSONObject(i)
							.getString("FromShortName").toString();
					ToShortName = subArray.getJSONObject(i)
							.getString("ToShortName").toString();

					TravelDate = subArray.getJSONObject(i)
							.getString("TravelDate").toString();
					TravelTime = subArray.getJSONObject(i)
							.getString("TravelTime").toString();
					Seats = subArray.getJSONObject(i).getString("Seats")
							.toString();
					RemainingSeats = subArray.getJSONObject(i)
							.getString("RemainingSeats").toString();
					Distance = subArray.getJSONObject(i).getString("Distance")
							.toString();
					OpenTime = subArray.getJSONObject(i).getString("OpenTime")
							.toString();
					CabStatus = subArray.getJSONObject(i)
							.getString("CabStatus").toString();
					Seat_Status = subArray.getJSONObject(i)
							.getString("Seat_Status").toString();

				}

				// ///////////////////////

				SharedPreferences mPrefs = getSharedPreferences("FacebookData",
						0);
				String MobileNumberstr = mPrefs.getString("MobileNumber", "");

				if (MobileNumber.equalsIgnoreCase(MobileNumberstr)) {

					int icon = R.drawable.cabappicon;
					notificationID++;

					Intent mainIntent = new Intent(this, CheckPool.class);
					mainIntent.putExtra("CabId", CabIdstr);
					mainIntent.putExtra("MobileNumber", MobileNumber);
					mainIntent.putExtra("OwnerName", OwnerName);
					mainIntent.putExtra("OwnerImage", OwnerImage);
					mainIntent.putExtra("FromLocation", FromLocation);
					mainIntent.putExtra("ToLocation", ToLocation);

					mainIntent.putExtra("FromShortName", FromShortName);
					mainIntent.putExtra("ToShortName", ToShortName);

					mainIntent.putExtra("TravelDate", TravelDate);
					mainIntent.putExtra("TravelTime", TravelTime);
					mainIntent.putExtra("Seats", Seats);
					mainIntent.putExtra("RemainingSeats", RemainingSeats);
					mainIntent.putExtra("Seat_Status", Seat_Status);
					mainIntent.putExtra("Distance", Distance);
					mainIntent.putExtra("OpenTime", OpenTime);
					mainIntent.putExtra("CabStatus", CabStatus);
					mainIntent.putExtra("comefrom", "fromupcomingtrip");
					PendingIntent pIntent = PendingIntent.getActivity(this,
							notificationID, mainIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);

					NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
							context);
					Notification notification = mBuilder
							.setSmallIcon(icon)
							.setTicker("ClubMyCab")
							.setWhen(0)
							.setAutoCancel(true)
							.setContentTitle("ClubMyCab")
							.setStyle(
									new NotificationCompat.BigTextStyle()
											.bigText(message))
							.setContentIntent(pIntent)
							.setSound(
									RingtoneManager
											.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
							.setContentText(message).build();

					NotificationManager notificationManager = (NotificationManager) context
							.getSystemService(Context.NOTIFICATION_SERVICE);
					notificationManager.notify(notificationID, notification);

				} else {

					int icon = R.drawable.cabappicon;
					notificationID++;

					Intent mainIntent = new Intent(this, JoinPool.class);
					mainIntent.putExtra("CabId", CabIdstr);
					mainIntent.putExtra("MobileNumber", MobileNumber);
					mainIntent.putExtra("OwnerName", OwnerName);
					mainIntent.putExtra("OwnerImage", OwnerImage);

					mainIntent.putExtra("FromLocation", FromLocation);
					mainIntent.putExtra("ToLocation", ToLocation);

					mainIntent.putExtra("FromShortName", FromShortName);
					mainIntent.putExtra("ToShortName", ToShortName);

					mainIntent.putExtra("TravelDate", TravelDate);
					mainIntent.putExtra("TravelTime", TravelTime);
					mainIntent.putExtra("Seats", Seats);
					mainIntent.putExtra("RemainingSeats", RemainingSeats);
					mainIntent.putExtra("Seat_Status", Seat_Status);
					mainIntent.putExtra("Distance", Distance);
					mainIntent.putExtra("OpenTime", OpenTime);
					mainIntent.putExtra("CabStatus", CabStatus);
					mainIntent.putExtra("comefrom", "fromupcomingtrip");
					PendingIntent pIntent = PendingIntent.getActivity(this,
							notificationID, mainIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);

					NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
							context);
					Notification notification = mBuilder
							.setSmallIcon(icon)
							.setTicker("ClubMyCab")
							.setWhen(0)
							.setAutoCancel(true)
							.setContentTitle("ClubMyCab")
							.setStyle(
									new NotificationCompat.BigTextStyle()
											.bigText(message))
							.setContentIntent(pIntent)
							.setSound(
									RingtoneManager
											.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
							.setContentText(message).build();

					NotificationManager notificationManager = (NotificationManager) context
							.getSystemService(Context.NOTIFICATION_SERVICE);
					notificationManager.notify(notificationID, notification);
				}
				// //////////////////////
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ////////////////////
	public void generatechatnotification(Context context, String message,
			String pushfrom, String cabId, String mname, String oname,
			String onumber) {

		// DatabaseHandler db = new DatabaseHandler(this);
		// String datetime = String.valueOf(System.currentTimeMillis());
		// db.addContact(new Contact(cabId, mname, message.toString().trim(),
		// datetime));

		TripCompletedtask val = new TripCompletedtask();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			val.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, cabId);
		} else {
			val.execute(cabId);
		}

		try {
			gotopoolresp = val.get();

			if (gotopoolresp.equalsIgnoreCase("This Ride no longer exist")) {
				int icon = R.drawable.cabappicon;
				notificationID++;

				Intent intent = new Intent(this, MyRides.class);
				PendingIntent pIntent = PendingIntent.getActivity(this,
						notificationID, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);

				String mskd = "message from " + mname;
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						context);
				Notification notification = mBuilder
						.setSmallIcon(icon)
						.setTicker(mskd)
						.setWhen(0)
						.setAutoCancel(true)
						.setContentTitle(mname)
						.setStyle(
								new NotificationCompat.BigTextStyle()
										.bigText(message))
						.setContentIntent(pIntent)
						.setSound(
								RingtoneManager
										.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
						.setContentText(message).build();

				NotificationManager notificationManager = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);
				notificationManager.notify(notificationID, notification);
			} else {

				String CabIdstr = null;
				String MobileNumber = null;
				String OwnerName = null;
				String OwnerImage = null;
				String FromLocation = null;
				String ToLocation = null;
				String FromShortName = null;
				String ToShortName = null;
				String TravelDate = null;
				String TravelTime = null;
				String Seats = null;
				String RemainingSeats = null;
				String Distance = null;
				String OpenTime = null;
				String CabStatus = null;
				String Seat_Status = null;

				JSONArray subArray = new JSONArray(gotopoolresp);
				for (int i = 0; i < subArray.length(); i++) {

					CabIdstr = subArray.getJSONObject(i).getString("CabId")
							.toString();
					MobileNumber = subArray.getJSONObject(i)
							.getString("MobileNumber").toString();
					OwnerName = subArray.getJSONObject(i)
							.getString("OwnerName").toString();
					OwnerImage = subArray.getJSONObject(i)
							.getString("imagename").toString();
					FromLocation = subArray.getJSONObject(i)
							.getString("FromLocation").toString();
					ToLocation = subArray.getJSONObject(i)
							.getString("ToLocation").toString();

					FromShortName = subArray.getJSONObject(i)
							.getString("FromShortName").toString();
					ToShortName = subArray.getJSONObject(i)
							.getString("ToShortName").toString();

					TravelDate = subArray.getJSONObject(i)
							.getString("TravelDate").toString();
					TravelTime = subArray.getJSONObject(i)
							.getString("TravelTime").toString();
					Seats = subArray.getJSONObject(i).getString("Seats")
							.toString();
					RemainingSeats = subArray.getJSONObject(i)
							.getString("RemainingSeats").toString();
					Distance = subArray.getJSONObject(i).getString("Distance")
							.toString();
					OpenTime = subArray.getJSONObject(i).getString("OpenTime")
							.toString();
					CabStatus = subArray.getJSONObject(i)
							.getString("CabStatus").toString();
					Seat_Status = subArray.getJSONObject(i)
							.getString("Seat_Status").toString();

				}

				// ///////////////////////

				SharedPreferences mPrefs = getSharedPreferences("FacebookData",
						0);
				String MobileNumberstr = mPrefs.getString("MobileNumber", "");

				if (MobileNumber.equalsIgnoreCase(MobileNumberstr)) {

					int icon = R.drawable.cabappicon;
					notificationID++;

					Intent mainIntent = new Intent(this, CheckPool.class);
					mainIntent.putExtra("CabId", CabIdstr);
					mainIntent.putExtra("MobileNumber", MobileNumber);
					mainIntent.putExtra("OwnerName", OwnerName);
					mainIntent.putExtra("OwnerImage", OwnerImage);
					mainIntent.putExtra("FromLocation", FromLocation);
					mainIntent.putExtra("ToLocation", ToLocation);

					mainIntent.putExtra("FromShortName", FromShortName);
					mainIntent.putExtra("ToShortName", ToShortName);

					mainIntent.putExtra("TravelDate", TravelDate);
					mainIntent.putExtra("TravelTime", TravelTime);
					mainIntent.putExtra("Seats", Seats);
					mainIntent.putExtra("RemainingSeats", RemainingSeats);
					mainIntent.putExtra("Seat_Status", Seat_Status);
					mainIntent.putExtra("Distance", Distance);
					mainIntent.putExtra("OpenTime", OpenTime);
					mainIntent.putExtra("CabStatus", CabStatus);
					mainIntent.putExtra("comefrom", "fromchatdirect");
					PendingIntent pIntent = PendingIntent.getActivity(this,
							notificationID, mainIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);

					String mskd = "message from " + mname;

					NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
							context);
					Notification notification = mBuilder
							.setSmallIcon(icon)
							.setTicker(mskd)
							.setWhen(0)
							.setAutoCancel(true)
							.setContentTitle(mname)
							.setStyle(
									new NotificationCompat.BigTextStyle()
											.bigText(message))
							.setContentIntent(pIntent)
							.setSound(
									RingtoneManager
											.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
							.setContentText(message).build();

					NotificationManager notificationManager = (NotificationManager) context
							.getSystemService(Context.NOTIFICATION_SERVICE);
					notificationManager.notify(notificationID, notification);

				} else {

					int icon = R.drawable.cabappicon;
					notificationID++;

					Intent mainIntent = new Intent(this, JoinPool.class);
					mainIntent.putExtra("CabId", CabIdstr);
					mainIntent.putExtra("MobileNumber", MobileNumber);
					mainIntent.putExtra("OwnerName", OwnerName);
					mainIntent.putExtra("OwnerImage", OwnerImage);

					mainIntent.putExtra("FromLocation", FromLocation);
					mainIntent.putExtra("ToLocation", ToLocation);

					mainIntent.putExtra("FromShortName", FromShortName);
					mainIntent.putExtra("ToShortName", ToShortName);

					mainIntent.putExtra("TravelDate", TravelDate);
					mainIntent.putExtra("TravelTime", TravelTime);
					mainIntent.putExtra("Seats", Seats);
					mainIntent.putExtra("RemainingSeats", RemainingSeats);
					mainIntent.putExtra("Seat_Status", Seat_Status);
					mainIntent.putExtra("Distance", Distance);
					mainIntent.putExtra("OpenTime", OpenTime);
					mainIntent.putExtra("CabStatus", CabStatus);
					mainIntent.putExtra("comefrom", "fromchatdirect");
					PendingIntent pIntent = PendingIntent.getActivity(this,
							notificationID, mainIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);

					String mskd = "message from " + mname;

					NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
							context);
					Notification notification = mBuilder
							.setSmallIcon(icon)
							.setTicker(mskd)
							.setWhen(0)
							.setAutoCancel(true)
							.setContentTitle(mname)
							.setStyle(
									new NotificationCompat.BigTextStyle()
											.bigText(message))
							.setContentIntent(pIntent)
							.setSound(
									RingtoneManager
											.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
							.setContentText(message).build();

					NotificationManager notificationManager = (NotificationManager) context
							.getSystemService(Context.NOTIFICATION_SERVICE);
					notificationManager.notify(notificationID, notification);
				}
				// //////////////////////
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// ////////////////////
	// ///////
	private class ConnectionTaskForFetchMyClubs extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionFetchMyClubs mAuth1 = new AuthenticateConnectionFetchMyClubs();
			try {
				mAuth1.connection();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
		}
	}

	public class AuthenticateConnectionFetchMyClubs {

		public AuthenticateConnectionFetchMyClubs() {

		}

		public void connection() throws Exception {

			SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
			String MobileNumber = mPrefs.getString("MobileNumber", "");
			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl + "/Fetch_Club.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair UserNumberBasicNameValuePair = new BasicNameValuePair(
					"OwnerNumber", MobileNumber.toString().trim());

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(UserNumberBasicNameValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			Log.e("httpResponse", "" + httpResponse);

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;
			String myclubsresp = null;

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				myclubsresp = stringBuilder.append(bufferedStrChunk).toString();
			}

			SharedPreferences sharedPreferences1 = getSharedPreferences(
					"MyClubs", 0);
			SharedPreferences.Editor editor1 = sharedPreferences1.edit();
			editor1.putString("clubs", myclubsresp.toString().trim());
			editor1.commit();

			// ///////////////
		}
	}

	// /////////////////
	public class TripCompletedtask extends AsyncTask<String, Void, String> {

		String result;

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(String... args) {

			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl + "/GoToPool.php";
			HttpPost httpPost = new HttpPost(url_select);

			try {
				BasicNameValuePair CabIdBasicNameValuePair = new BasicNameValuePair(
						"CabId", args[0].toString().trim());

				List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
				nameValuePairList.add(CabIdBasicNameValuePair);

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

				while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
					result = stringBuilder.append(bufferedStrChunk).toString();
				}

			} catch (Exception e) {
				Log.e("Fetch_RequestId.java", e.getMessage());
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {

		}
	}
}