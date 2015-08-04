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
import android.app.ProgressDialog;
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
import com.clubmycab.ui.NotificationListActivity.AuthenticateConnectionCabRating;
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
	private String NotificationId;
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
		mcontext=context;
		String message = intent.getExtras().getString("gcmText");
		Log.d("message", "" + message);
		String pushfrom = intent.getExtras().getString("pushfrom");
		String CabId = intent.getExtras().getString("CabId");
		String MemberName = intent.getExtras().getString("MemberName");
		String oname = intent.getExtras().getString("oname");
		String onumber = intent.getExtras().getString("onumber");
		 NotificationId = intent.getExtras().getString("notificationid");


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
			generateTripNotification(context, CabId, message);
		} else if (pushfrom != null
				&& pushfrom.equalsIgnoreCase("ownerTripCompleted")) {
			generateTripNotification(context, CabId, message);
		} else if (pushfrom != null
				&& pushfrom.equalsIgnoreCase("tripcompleted")) {
			generateTripNotification(context, CabId, message);
		} 
		else if(pushfrom != null
				&& pushfrom.equalsIgnoreCase("cabid_")){
			
			SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
			//FullName = mPrefs.getString("FullName", "");
			MobileNumberstr = mPrefs.getString("MobileNumber", "");
			
			generateCabIdNotificaiton(context, CabId, message);
			
		}
			
			else {
			generateNewMsgNotification(context, message);
		}
	}

	/**
	 * Method called on receiving a deleted message
	 * */
	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.d(TAG, "Received deleted messages notification");
	}

	/**
	 * Method called on Error
	 * */
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
	
	
	public void generateCabIdNotificaiton(Context context,String cabid, String message){
Toast.makeText(mcontext, "Notification Id "+NotificationId, Toast.LENGTH_SHORT).show();		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new ConnectionTaskForseenotification()
					.executeOnExecutor(
							AsyncTask.THREAD_POOL_EXECUTOR,
							cabid);
		} else {
			new ConnectionTaskForseenotification()
					.execute(cabid);
		}
	}
	
	
	
	public void opneNotificationListActivity(String message){
		String[] arr = message.split(
				"-");
		Log.d("arr", ""
				+ arr[1].toString().trim());

	String	address = arr[1].toString().trim();
	String	latlongmap ="";

		Intent mainIntent = new Intent(
			mcontext,
				LocationInMapFragmentActivity.class);
		mainIntent.putExtra("address", address);
		mainIntent.putExtra("latlongmap",
				latlongmap);
		mcontext
				.startActivity(mainIntent);
	}
	
	public void openMyclubActivity(String message){

		Intent mainIntent = new Intent(
			mcontext,
				MyClubsActivity.class);
		mainIntent.putExtra("comefrom",
				"comefrom");
	mcontext.startActivity(mainIntent);
		
	}
	
	public void genrateCabRating(String cabId){
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new ConnectionTaskForCabRating()
					.executeOnExecutor(
							AsyncTask.THREAD_POOL_EXECUTOR,
							cabId,
							"");
		} else {
			new ConnectionTaskForCabRating()
					.execute(
							cabId,
							"");
		}
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

					// mainIntent.putExtra("CabId", CabIdstr);
					// mainIntent.putExtra("MobileNumber", MobileNumber);
					// mainIntent.putExtra("OwnerName", OwnerName);
					// mainIntent.putExtra("OwnerImage", OwnerImage);
					// mainIntent.putExtra("FromLocation", FromLocation);
					// mainIntent.putExtra("ToLocation", ToLocation);
					//
					// mainIntent.putExtra("FromShortName", FromShortName);
					// mainIntent.putExtra("ToShortName", ToShortName);
					//
					// mainIntent.putExtra("TravelDate", TravelDate);
					// mainIntent.putExtra("TravelTime", TravelTime);
					// mainIntent.putExtra("Seats", Seats);
					// mainIntent.putExtra("RemainingSeats", RemainingSeats);
					// mainIntent.putExtra("Seat_Status", Seat_Status);
					// mainIntent.putExtra("Distance", Distance);
					// mainIntent.putExtra("OpenTime", OpenTime);
					// mainIntent.putExtra("CabStatus", CabStatus);
					//
					// mainIntent.putExtra("BookingRefNo", BookingRefNo);
					// mainIntent.putExtra("DriverName", DriverName);
					// mainIntent.putExtra("DriverNumber", DriverNumber);
					// mainIntent.putExtra("CarNumber", CarNumber);
					// mainIntent.putExtra("CabName", CabName);
					//
					// mainIntent.putExtra("ExpTripDuration", ExpTripDuration);
					// mainIntent.putExtra("status", status);

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

					// mainIntent.putExtra("CabId", CabIdstr);
					// mainIntent.putExtra("MobileNumber", MobileNumber);
					// mainIntent.putExtra("OwnerName", OwnerName);
					// mainIntent.putExtra("OwnerImage", OwnerImage);
					//
					// mainIntent.putExtra("FromLocation", FromLocation);
					// mainIntent.putExtra("ToLocation", ToLocation);
					//
					// mainIntent.putExtra("FromShortName", FromShortName);
					// mainIntent.putExtra("ToShortName", ToShortName);
					//
					// mainIntent.putExtra("TravelDate", TravelDate);
					// mainIntent.putExtra("TravelTime", TravelTime);
					// mainIntent.putExtra("Seats", Seats);
					// mainIntent.putExtra("RemainingSeats", RemainingSeats);
					// mainIntent.putExtra("Seat_Status", Seat_Status);
					// mainIntent.putExtra("Distance", Distance);
					// mainIntent.putExtra("OpenTime", OpenTime);
					// mainIntent.putExtra("CabStatus", CabStatus);
					//
					// mainIntent.putExtra("BookingRefNo", BookingRefNo);
					// mainIntent.putExtra("DriverName", DriverName);
					// mainIntent.putExtra("DriverNumber", DriverNumber);
					// mainIntent.putExtra("CarNumber", CarNumber);
					// mainIntent.putExtra("CabName", CabName);
					//
					// mainIntent.putExtra("ExpTripDuration", ExpTripDuration);
					// mainIntent.putExtra("status", status);

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

					// mainIntent.putExtra("CabId", CabIdstr);
					// mainIntent.putExtra("MobileNumber", MobileNumber);
					// mainIntent.putExtra("OwnerName", OwnerName);
					// mainIntent.putExtra("OwnerImage", OwnerImage);
					//
					// mainIntent.putExtra("FromLocation", FromLocation);
					// mainIntent.putExtra("ToLocation", ToLocation);
					//
					// mainIntent.putExtra("FromShortName", FromShortName);
					// mainIntent.putExtra("ToShortName", ToShortName);
					//
					// mainIntent.putExtra("TravelDate", TravelDate);
					// mainIntent.putExtra("TravelTime", TravelTime);
					// mainIntent.putExtra("Seats", Seats);
					// mainIntent.putExtra("RemainingSeats", RemainingSeats);
					// mainIntent.putExtra("Seat_Status", Seat_Status);
					// mainIntent.putExtra("Distance", Distance);
					// mainIntent.putExtra("OpenTime", OpenTime);
					// mainIntent.putExtra("CabStatus", CabStatus);
					//
					// mainIntent.putExtra("ExpTripDuration", ExpTripDuration);
					// mainIntent.putExtra("status", status);

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
			
			
			
			
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new updatenotificationstatusasread()
							.executeOnExecutor(
									AsyncTask.THREAD_POOL_EXECUTOR,
									"",
									NotificationId);
				} else {
					new updatenotificationstatusasread()
							.execute(
									"",
									NotificationId
											);
				}
			
			
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
			String message) {

		int icon = R.drawable.cabappicon;
		notificationID++;

		Intent intent = new Intent();
		intent = new Intent(context, MyRidesActivity.class);
		intent.putExtra("comefrom", "TripStart");
		intent.putExtra("cabID", cabID);

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

		private class updatenotificationstatusasread extends
				AsyncTask<String, Void, Void> {

			@Override
			protected void onPreExecute() {

			}

			@Override
			protected Void doInBackground(String... args) {
				Authenticateupdatenotificationstatusasread mAuth1 = new Authenticateupdatenotificationstatusasread();
				try {
					mAuth1.rnum = args[0];
					mAuth1.nid = args[1];
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
					Toast.makeText(mcontext,
							getResources().getString(R.string.exceptionstring),
							Toast.LENGTH_LONG).show();
					return;
				}
			}

		}

		public class Authenticateupdatenotificationstatusasread {

			public String rnum;
			public String nid;

			public Authenticateupdatenotificationstatusasread() {

			}

			public void connection() throws Exception {

				// Connect to google.com
				HttpClient httpClient = new DefaultHttpClient();
				String url_select = GlobalVariables.ServiceUrl
						+ "/UpdateNotificationStatusToRead.php";
				HttpPost httpPost = new HttpPost(url_select);
				BasicNameValuePair rnumBasicNameValuePair = new BasicNameValuePair(
						"rnum", rnum);
				BasicNameValuePair nidBasicNameValuePair = new BasicNameValuePair(
						"nid", nid);

				List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
				nameValuePairList.add(rnumBasicNameValuePair);
				nameValuePairList.add(nidBasicNameValuePair);

				UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
						nameValuePairList);
				httpPost.setEntity(urlEncodedFormEntity);
				HttpResponse httpResponse = httpClient.execute(httpPost);

				Log.d("httpResponse", "" + httpResponse);
			}
		}
		
		// /////////////////
		// ///////

		private class ConnectionTaskForseenotification extends
				AsyncTask<String, Void, Void> {
			private ProgressDialog dialog = new ProgressDialog(
					mcontext);

			@Override
			protected void onPreExecute() {
				dialog.setMessage("Please Wait...");
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				dialog.show();

			}

			@Override
			protected Void doInBackground(String... args) {
				AuthenticateConnectionseenotification mAuth1 = new AuthenticateConnectionseenotification();
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

				if (dialog.isShowing()) {
					dialog.dismiss();
				}

				if (exceptioncheck) {
					exceptioncheck = false;
					Toast.makeText(mcontext,
							getResources().getString(R.string.exceptionstring),
							Toast.LENGTH_LONG).show();
					return;
				}

				if (gotopoolresp.equalsIgnoreCase("This Ride no longer exist")) {
					Toast.makeText(mcontext,
							"This ride no longer exists!", Toast.LENGTH_LONG)
							.show();
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

							final Intent mainIntent = new Intent(
									mcontext,
									CheckPoolFragmentActivity.class);

							mainIntent.putExtra("RideDetailsModel",
									gson.toJson(rideDetailsModel));

							// mainIntent.putExtra("CabId", CabIdstr);
							// mainIntent.putExtra("MobileNumber", MobileNumber);
							// mainIntent.putExtra("OwnerName", OwnerName);
							// mainIntent.putExtra("OwnerImage", OwnerImage);
							// mainIntent.putExtra("FromLocation", FromLocation);
							// mainIntent.putExtra("ToLocation", ToLocation);
							//
							// mainIntent.putExtra("FromShortName", FromShortName);
							// mainIntent.putExtra("ToShortName", ToShortName);
							//
							// mainIntent.putExtra("TravelDate", TravelDate);
							// mainIntent.putExtra("TravelTime", TravelTime);
							// mainIntent.putExtra("Seats", Seats);
							// mainIntent.putExtra("RemainingSeats",
							// RemainingSeats);
							// mainIntent.putExtra("Seat_Status", Seat_Status);
							// mainIntent.putExtra("Distance", Distance);
							// mainIntent.putExtra("OpenTime", OpenTime);
							//
							// mainIntent.putExtra("CabStatus", CabStatus);
							//
							// mainIntent.putExtra("BookingRefNo", BookingRefNo);
							// mainIntent.putExtra("DriverName", DriverName);
							// mainIntent.putExtra("DriverNumber", DriverNumber);
							// mainIntent.putExtra("CarNumber", CarNumber);
							// mainIntent.putExtra("CabName", CabName);
							//
							// mainIntent.putExtra("ExpTripDuration",
							// ExpTripDuration);
							// mainIntent.putExtra("status", status);

							mcontext.startActivity(mainIntent);

						} else {
							final Intent mainIntent = new Intent(
									mcontext,
									MemberRideFragmentActivity.class);

							mainIntent.putExtra("RideDetailsModel",
									gson.toJson(rideDetailsModel));

						

							mcontext.startActivity(mainIntent);
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

		}

		public class AuthenticateConnectionseenotification {

			public String cid;

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
		//Connection task for cab rating
		
		private class ConnectionTaskForCabRating extends
		AsyncTask<String, Void, Void> {

	private ProgressDialog dialog = new ProgressDialog(
			mcontext);
	private String cabIDString;
	private String notificationIDString;

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
			cabIDString = args[0];
			notificationIDString = args[1];
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
		if (dialog.isShowing()) {
			dialog.dismiss();
		}

		if (exceptioncheck) {
			exceptioncheck = false;
			Toast.makeText(mcontext,
					getResources().getString(R.string.exceptionstring),
					Toast.LENGTH_LONG).show();
			return;
		}

		if (cabratingresp.isEmpty() || cabratingresp == null
				|| cabratingresp.toLowerCase().contains("no cabs found")) {
			Toast.makeText(mcontext,
					"Sorry, no cabs were found", Toast.LENGTH_SHORT).show();
		} else {

			Intent intent = new Intent(mcontext,
					RateCabActivity.class);
			
			intent.putExtra("CabsJSONArrayString", cabratingresp);
			intent.putExtra("CabsRatingMobileNumber", MobileNumberstr);
			intent.putExtra("cabIDIntent", cabIDString);
			intent.putExtra("notificationIDString", "");
			mcontext.startActivity(intent);

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