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
import android.widget.Toast;

import com.clubmycab.model.RideDetailsModel;
import com.clubmycab.ui.LocationInMapFragmentActivity;
import com.clubmycab.ui.MyClubsActivity;
import com.clubmycab.ui.MyRidesActivity;
import com.clubmycab.ui.NotificationListActivity;
import com.clubmycab.ui.RateCabActivity;
import com.clubmycab.ui.SplashActivity;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GCMIntentService extends GCMBaseIntentService {

	static int notificationID = 1;
	boolean exceptioncheck = false;
	private Context mcontext;
	String MobileNumberstr;
	// private String NotificationId;
	private String cabratingresp;

	private static final String TAG = "GCMIntentService";

	String gotopoolresp;

	public GCMIntentService() {
		super(GlobalVariables.GCMProjectKey);
	}

	/**
	 * Method called on device registered
	 **/
	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.d(TAG, "Device registered: regId = " + registrationId);
	}

	/**
	 * Method called on device un registred
	 * */
	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.d(TAG, "Device unregistered");
	}

	/**
	 * Method called on Receiving a new message
	 * */
	@Override
	protected void onMessage(Context context, Intent intent) {
		mcontext = context;
		String message = intent.getExtras().getString("gcmText");
		Log.d("message", "" + message);
		String pushfrom = intent.getExtras().getString("pushfrom");
		String CabId = intent.getExtras().getString("CabId");
		String MemberName = intent.getExtras().getString("MemberName");
		String oname = intent.getExtras().getString("oname");
		String onumber = intent.getExtras().getString("onumber");
		String NotificationId = intent.getExtras().getString("notificationId");
		String latLong = intent.getExtras().getString("latLong");
		Log.d(TAG, "pushfrom : " + (pushfrom == null ? "null" : pushfrom));

		if (pushfrom != null && pushfrom.equalsIgnoreCase("groupchat")) {
			generatechatnotification(context, message, pushfrom, CabId,
					MemberName, oname, onumber);
		} else if (pushfrom != null
				&& pushfrom.equalsIgnoreCase("upcomingtrip")) {
			generateUpComingTrip(context, message, pushfrom, CabId);
		} else if (pushfrom != null
				&& pushfrom.equalsIgnoreCase("genericnotification")) {
			generateGenericnotification(context, message);
		} else if (pushfrom != null && pushfrom.equalsIgnoreCase("TripStart")) {
			generateTripNotification(context, CabId, message,NotificationId);
		} else if (pushfrom != null
				&& pushfrom.equalsIgnoreCase("ownerTripCompleted")) {
			generateTripNotification(context, CabId, message,NotificationId);
		} else if (pushfrom != null
				&& pushfrom.equalsIgnoreCase("tripcompleted")) {
			generateTripNotification(context, CabId, message,NotificationId);
		} else if (pushfrom != null && pushfrom.equalsIgnoreCase("CabId_")) {

			SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
			// FullName = mPrefs.getString("FullName", "");
			MobileNumberstr = mPrefs.getString("MobileNumber", "");

			generateCabIdNotificaiton(context, CabId, message, NotificationId);

		} else if (pushfrom != null
				&& pushfrom.equalsIgnoreCase("Share_LocationUpdate")) {

			// SharedPreferences mPrefs = getSharedPreferences("FacebookData",
			// 0);
			// // FullName = mPrefs.getString("FullName", "");
			// MobileNumberstr = mPrefs.getString("MobileNumber", "");

			generateSharedLocationNotificaiton(context, message, latLong,
					NotificationId);

		}

		else if (pushfrom != null && pushfrom.equalsIgnoreCase("PoolId_")) {
			genrateMyClubNotificaton(context, message, NotificationId);
		} else if (pushfrom != null && pushfrom.equalsIgnoreCase("Cab_Rating")) {
			
			genrateCabRating(CabId, message, NotificationId);

		}

		else {
			generateNewMsgNotification(context, message);
		}
	}

	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.d(TAG, "Received deleted messages notification");
	}

	@Override
	public void onError(Context context, String errorId) {
		Log.e(TAG, "Received error: " + errorId);
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
		Log.e(TAG, "Received recoverable error: " + errorId);
		return super.onRecoverableError(context, errorId);
	}

	public void generateCabIdNotificaiton(Context context, String CabId,
			String message, String NotificationId) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new ConnectionTaskForseenotification().executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, CabId, NotificationId,
					message);
		} else {
			new ConnectionTaskForseenotification().execute(CabId,
					NotificationId, message);
		}

	}

	public void generateSharedLocationNotificaiton(Context context,
			String message, String latLong, String NotificationId) {

		String[] arr = message.split("-");
		Log.d("arr", "" + arr[1].toString().trim());

		String address = arr[1].toString().trim();

		int icon = R.drawable.cabappicon;
		notificationID++;

		Intent intent = new Intent(this, LocationInMapFragmentActivity.class);
		intent.putExtra("address", address);
		intent.putExtra("latlongmap", latLong);
		intent.putExtra("nid", NotificationId);

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

	public void genrateMyClubNotificaton(Context context, String message,
			String NotificationId) {

		// Intent mainIntent = new Intent(
		// NotificationListActivity.this,
		// MyClubsActivity.class);
		// mainIntent.putExtra("comefrom",
		// "comefrom");
		// startActivityForResult(mainIntent, 500);

		int icon = R.drawable.cabappicon;
		notificationID++;

		Intent intent = new Intent(this, MyClubsActivity.class);
		intent.putExtra("comefrom", "GCM");
		intent.putExtra("nid", NotificationId);

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

//	public void opneNotificationListActivity(String message) {
//		String[] arr = message.split("-");
//		Log.d("arr", "" + arr[1].toString().trim());
//
//		String address = arr[1].toString().trim();
//		String latlongmap = "";
//
//		Intent mainIntent = new Intent(mcontext,
//				LocationInMapFragmentActivity.class);
//		mainIntent.putExtra("address", address);
//		mainIntent.putExtra("latlongmap", latlongmap);
//		mcontext.startActivity(mainIntent);
//	}

//	public void openNotificationMyclubActivity(String message) {
//
//		Intent mainIntent = new Intent(mcontext, MyClubsActivity.class);
//		mainIntent.putExtra("comefrom", "comefrom");
//		mcontext.startActivity(mainIntent);
//
//	}

	public void genrateCabRating(String cabId, String message,
			String NotificationId) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new ConnectionTaskForCabRating().executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, cabId, message,
					NotificationId);
		} else {
			new ConnectionTaskForCabRating().execute(cabId, message,
					NotificationId);
		}
	}

	public void generateNewMsgNotification(Context context, String message) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new ConnectionTaskForFetchMyClubs()
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			new ConnectionTaskForFetchMyClubs().execute();
		}

		int icon = R.drawable.cabappicon;
		notificationID++;

		Intent intent = new Intent(this, NotificationListActivity.class);
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

		Intent intent = new Intent(this, SplashActivity.class);
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

				Intent intent = new Intent(this, MyRidesActivity.class);
				PendingIntent pIntent = PendingIntent.getActivity(this,
						notificationID, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);

				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						context);
				Notification notification = mBuilder
						.setSmallIcon(icon)
						.setTicker("ClubMyCab")
						.setWhen(System.currentTimeMillis())
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

				// String CabIdstr = null;
				// String MobileNumber = null;
				// String OwnerName = null;
				// String OwnerImage = null;
				// String FromLocation = null;
				// String ToLocation = null;
				// String FromShortName = null;
				// String ToShortName = null;
				// String TravelDate = null;
				// String TravelTime = null;
				// String Seats = null;
				// String RemainingSeats = null;
				// String Distance = null;
				// String OpenTime = null;
				// String CabStatus = null;
				// String Seat_Status = null;
				// String BookingRefNo = null;
				// String DriverName = null;
				// String DriverNumber = null;
				// String CarNumber = null;
				// String CabName = null;
				// String ExpTripDuration = null;
				// String status = null;

				Gson gson = new Gson();
				ArrayList<RideDetailsModel> arrayRideDetailsModels = gson
						.fromJson(gotopoolresp,
								new TypeToken<ArrayList<RideDetailsModel>>() {
								}.getType());
				RideDetailsModel rideDetailsModel = arrayRideDetailsModels
						.get(0);

				// JSONArray subArray = new JSONArray(gotopoolresp);
				// for (int i = 0; i < subArray.length(); i++) {
				//
				// CabIdstr = subArray.getJSONObject(i).getString("CabId")
				// .toString();
				// MobileNumber = subArray.getJSONObject(i)
				// .getString("MobileNumber").toString();
				// OwnerName = subArray.getJSONObject(i)
				// .getString("OwnerName").toString();
				// OwnerImage = subArray.getJSONObject(i)
				// .getString("imagename").toString();
				// FromLocation = subArray.getJSONObject(i)
				// .getString("FromLocation").toString();
				// ToLocation = subArray.getJSONObject(i)
				// .getString("ToLocation").toString();
				//
				// FromShortName = subArray.getJSONObject(i)
				// .getString("FromShortName").toString();
				// ToShortName = subArray.getJSONObject(i)
				// .getString("ToShortName").toString();
				//
				// TravelDate = subArray.getJSONObject(i)
				// .getString("TravelDate").toString();
				// TravelTime = subArray.getJSONObject(i)
				// .getString("TravelTime").toString();
				// Seats = subArray.getJSONObject(i).getString("Seats")
				// .toString();
				// RemainingSeats = subArray.getJSONObject(i)
				// .getString("RemainingSeats").toString();
				// Distance = subArray.getJSONObject(i).getString("Distance")
				// .toString();
				// OpenTime = subArray.getJSONObject(i).getString("OpenTime")
				// .toString();
				// CabStatus = subArray.getJSONObject(i)
				// .getString("CabStatus").toString();
				// Seat_Status = subArray.getJSONObject(i)
				// .getString("Seat_Status").toString();
				//
				// BookingRefNo = subArray.getJSONObject(i)
				// .getString("BookingRefNo").toString();
				// DriverName = subArray.getJSONObject(i)
				// .getString("DriverName").toString();
				// DriverNumber = subArray.getJSONObject(i)
				// .getString("DriverNumber").toString();
				// CarNumber = subArray.getJSONObject(i)
				// .getString("CarNumber").toString();
				// CabName = subArray.getJSONObject(i)
				// .getString("CabName").toString();
				//
				// ExpTripDuration = subArray.getJSONObject(i)
				// .getString("ExpTripDuration").toString();
				// status = subArray.getJSONObject(i).getString("status")
				// .toString();
				//
				// }

				// ///////////////////////

				SharedPreferences mPrefs = getSharedPreferences("FacebookData",
						0);
				String MobileNumberstr = mPrefs.getString("MobileNumber", "");

				if (rideDetailsModel.getMobileNumber().equalsIgnoreCase(
						MobileNumberstr)) {

					int icon = R.drawable.cabappicon;
					notificationID++;

					Intent mainIntent = new Intent(this,
							CheckPoolFragmentActivity.class);

					mainIntent.putExtra("RideDetailsModel",
							gson.toJson(rideDetailsModel));

					mainIntent.putExtra("comefrom", "fromupcomingtrip");
					PendingIntent pIntent = PendingIntent.getActivity(this,
							notificationID, mainIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);

					NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
							context);
					Notification notification = mBuilder
							.setSmallIcon(icon)
							.setTicker("ClubMyCab")
							.setWhen(System.currentTimeMillis())
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

					Intent mainIntent = new Intent(this,
							MemberRideFragmentActivity.class);

					mainIntent.putExtra("RideDetailsModel",
							gson.toJson(rideDetailsModel));

					mainIntent.putExtra("comefrom", "fromupcomingtrip");
					PendingIntent pIntent = PendingIntent.getActivity(this,
							notificationID, mainIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);

					NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
							context);
					Notification notification = mBuilder
							.setSmallIcon(icon)
							.setTicker("ClubMyCab")
							.setWhen(System.currentTimeMillis())
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
			}

		} catch (Exception e) {
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

				Intent intent = new Intent(this, MyRidesActivity.class);
				PendingIntent pIntent = PendingIntent.getActivity(this,
						notificationID, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);

				String mskd = "message from " + mname;
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						context);
				Notification notification = mBuilder
						.setSmallIcon(icon)
						.setTicker(mskd)
						.setWhen(System.currentTimeMillis())
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

				Gson gson = new Gson();
				ArrayList<RideDetailsModel> arrayRideDetailsModels = gson
						.fromJson(gotopoolresp,
								new TypeToken<ArrayList<RideDetailsModel>>() {
								}.getType());
				RideDetailsModel rideDetailsModel = arrayRideDetailsModels
						.get(0);

				// ///////////////////////

				SharedPreferences mPrefs = getSharedPreferences("FacebookData",
						0);
				String MobileNumberstr = mPrefs.getString("MobileNumber", "");

				if (rideDetailsModel.getMobileNumber().equalsIgnoreCase(
						MobileNumberstr)) {

					int icon = R.drawable.cabappicon;
					notificationID++;

					Intent mainIntent = new Intent(this,
							CheckPoolFragmentActivity.class);

					mainIntent.putExtra("RideDetailsModel",
							gson.toJson(rideDetailsModel));

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
							.setWhen(System.currentTimeMillis())
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

					Intent mainIntent = new Intent(this,
							MemberRideFragmentActivity.class);

					mainIntent.putExtra("RideDetailsModel",
							gson.toJson(rideDetailsModel));

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
							.setWhen(System.currentTimeMillis())
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

			Log.d("httpResponse", "" + httpResponse);

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

	private void generateTripNotification(Context context, String cabID,
			String message,String NotificationId) {

		int icon = R.drawable.cabappicon;
		notificationID++;

		Intent intent = new Intent();
		intent = new Intent(context, MyRidesActivity.class);
		intent.putExtra("comefrom", "TripStart");
		intent.putExtra("cabID", cabID);
		intent.putExtra("nid", NotificationId);
		Log.d("MyRideNotificationId::", NotificationId);


		PendingIntent pIntent = PendingIntent.getActivity(context,
				notificationID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

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

	// /////////////////
	// ///////

	private class ConnectionTaskForseenotification extends
			AsyncTask<String, Void, Void> {

		private String notificationId;
		private String message;

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionseenotification mAuth1 = new AuthenticateConnectionseenotification();
			try {
				mAuth1.cid = args[0];
				notificationId = args[1];
				message = args[2];

				mAuth1.connection();
			} catch (Exception e) {
				exceptioncheck = true;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {

			if (exceptioncheck) {
				exceptioncheck = false;
				Toast.makeText(mcontext,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (gotopoolresp.equalsIgnoreCase("This Ride no longer exist")) {

			} else {

				try {

					Gson gson = new Gson();
					ArrayList<RideDetailsModel> arrayRideDetailsModels = gson
							.fromJson(
									gotopoolresp,
									new TypeToken<ArrayList<RideDetailsModel>>() {
									}.getType());
					RideDetailsModel rideDetailsModel = arrayRideDetailsModels
							.get(0);

					if (rideDetailsModel.getMobileNumber().equalsIgnoreCase(
							MobileNumberstr)) {

						// final Intent mainIntent = new Intent(
						// mcontext,
						// CheckPoolFragmentActivity.class);
						// mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						//
						// mainIntent.putExtra("RideDetailsModel",
						// gson.toJson(rideDetailsModel));
						//
						// mainIntent.putExtra("comefrom", "GCM");
						//
						// mcontext.startActivity(mainIntent);

						int icon = R.drawable.cabappicon;
						notificationID++;

						Intent intent = new Intent(mcontext,
								CheckPoolFragmentActivity.class);
						intent.putExtra("RideDetailsModel",
								gson.toJson(rideDetailsModel));

						intent.putExtra("comefrom", "GCM");
						intent.putExtra("nid", notificationId);
						PendingIntent pIntent = PendingIntent.getActivity(
								mcontext, notificationID, intent,
								PendingIntent.FLAG_UPDATE_CURRENT);

						NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
								mcontext);
						Notification notification = mBuilder
								.setSmallIcon(icon)
								.setTicker("ClubMyCab")
								.setWhen(System.currentTimeMillis())
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

						NotificationManager notificationManager = (NotificationManager) mcontext
								.getSystemService(Context.NOTIFICATION_SERVICE);
						notificationManager
								.notify(notificationID, notification);

					} else {
						// final Intent mainIntent = new Intent(
						// mcontext,
						// MemberRideFragmentActivity.class);
						// mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						// mainIntent.putExtra("comefrom", "GCM");
						//
						// mainIntent.putExtra("RideDetailsModel",
						// gson.toJson(rideDetailsModel));
						//
						//
						//
						// mcontext.startActivity(mainIntent);

						int icon = R.drawable.cabappicon;
						notificationID++;

						Intent intent = new Intent(mcontext,
								MemberRideFragmentActivity.class);
						intent.putExtra("RideDetailsModel",
								gson.toJson(rideDetailsModel));

						intent.putExtra("comefrom", "GCM");
						intent.putExtra("nid", notificationId);

						PendingIntent pIntent = PendingIntent.getActivity(
								mcontext, notificationID, intent,
								PendingIntent.FLAG_UPDATE_CURRENT);

						NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
								mcontext);
						Notification notification = mBuilder
								.setSmallIcon(icon)
								.setTicker("ClubMyCab")
								.setWhen(System.currentTimeMillis())
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

						NotificationManager notificationManager = (NotificationManager) mcontext
								.getSystemService(Context.NOTIFICATION_SERVICE);
						notificationManager
								.notify(notificationID, notification);

					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}

	}

	public class AuthenticateConnectionseenotification {

		public String cid;
		public String NotificationId;

		public AuthenticateConnectionseenotification() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl + "/GoToPool.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair CabIdBasicNameValuePair = new BasicNameValuePair(
					"CabId", cid);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(CabIdBasicNameValuePair);

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
				gotopoolresp = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("gotopoolresp", "" + stringBuilder.toString());
		}
	}

	// Connection task for cab rating

	private class ConnectionTaskForCabRating extends
			AsyncTask<String, Void, Void> {

		private String cabIDString;
		private String notificationIDString;
		private String message;

		@Override
		protected void onPreExecute() {
			// dialog.setMessage("Please Wait...");
			// dialog.setCancelable(false);
			// dialog.setCanceledOnTouchOutside(false);
			// dialog.show();
		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionCabRating mAuth1 = new AuthenticateConnectionCabRating();
			try {
				cabIDString = args[0];
				message = args[1];
				notificationIDString = args[2];
				mAuth1.cabID = args[0];
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
			// if (dialog.isShowing()) {
			// dialog.dismiss();
			// }

			if (exceptioncheck) {
				exceptioncheck = false;
				Toast.makeText(mcontext,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (cabratingresp.isEmpty() || cabratingresp == null
					|| cabratingresp.toLowerCase().contains("no cabs found")) {
				Toast.makeText(mcontext, "Sorry, no cabs were found",
						Toast.LENGTH_SHORT).show();
			} else {

				// Intent intent = new Intent(mcontext, RateCabActivity.class);
				//
				// intent.putExtra("CabsJSONArrayString", cabratingresp);
				// intent.putExtra("CabsRatingMobileNumber", MobileNumberstr);
				// intent.putExtra("cabIDIntent", cabIDString);
				// intent.putExtra("notificationIDString", "");
				// mcontext.startActivity(intent);

				int icon = R.drawable.cabappicon;
				notificationID++;

				Intent intent = new Intent(mcontext, RateCabActivity.class);

				intent.putExtra("comefrom", "GCM");

				intent.putExtra("CabsJSONArrayString", cabratingresp);
				intent.putExtra("CabsRatingMobileNumber", MobileNumberstr);
				intent.putExtra("cabIDIntent", cabIDString);
				intent.putExtra("notificationIDString", notificationIDString);

				// intent.putExtra("nid", notificationId);

				PendingIntent pIntent = PendingIntent.getActivity(mcontext,
						notificationID, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);

				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						mcontext);
				Notification notification = mBuilder
						.setSmallIcon(icon)
						.setTicker("ClubMyCab")
						.setWhen(System.currentTimeMillis())
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

				NotificationManager notificationManager = (NotificationManager) mcontext
						.getSystemService(Context.NOTIFICATION_SERVICE);
				notificationManager.notify(notificationID, notification);

			}

		}

	}

	public class AuthenticateConnectionCabRating {

		public String cabID;

		public AuthenticateConnectionCabRating() {

		}

		public void connection() throws Exception {
			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl + "/getCabs.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair CabIDNameValuePair = new BasicNameValuePair(
					"CabID", cabID);
			// Log.d("AllNotificationRequest",
			// "AuthenticateConnectionCabRating cabID : " + cabID);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(CabIDNameValuePair);

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
}