package com.clubmycab;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;

import com.clubmycab.maps.MapUtilityMethods;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class LocationShareService extends Service implements LocationListener {

	private static String TAG = "LocationShareService";

	ArrayList<String> recpnames = new ArrayList<String>();
	ArrayList<String> recpnumbers = new ArrayList<String>();
	String sharetilltype;
	long timetilvalue;
	LatLng destinationlatlong;
	long destinationtimevalue;
	private boolean isResendingLocation;

	// final Handler handler = new Handler();
	private LocationManager locationManager;
	// private LocationListener locListener = new MyLocationListener();

	Location mycurrentlocation;
	servicesinasyntask mytask;
	Boolean keepsharing;

	Boolean locationsend;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {

		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		Log.d(TAG, "LocationServiceStarted");
		locationsend = true;

		try {
			mytask = new servicesinasyntask();

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				mytask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				mytask.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private class servicesinasyntask extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			Connectionservicesinasyntask mAuth1 = new Connectionservicesinasyntask();
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

	public class Connectionservicesinasyntask {

		public Connectionservicesinasyntask() {

		}

		public void connection() throws Exception {

			if (Looper.myLooper() == null) { // check already Looper is
												// associated or not.
				Looper.prepare(); // No Looper is defined So define a new one
			}
			SharedPreferences locationshapref = getSharedPreferences(
					"ShareLocationShared", 0);
			Gson gson = new Gson();
			String json = locationshapref.getString("ShareLocationObject", "");
			ShareLocationObject obj = gson.fromJson(json,
					ShareLocationObject.class);

			recpnames = obj.recipientsnames;
			recpnumbers = obj.recipientsnumbers;
			sharetilltype = obj.sharetilltype;

			keepsharing = true;
			Log.d(TAG, "keepsharing = true; connection");
			do {

				if (sharetilltype.equalsIgnoreCase("duration")) {
					timetilvalue = obj.timetillvalue;

					if (System.currentTimeMillis() < timetilvalue) {
						sendlocation(recpnames, recpnumbers);
					} else {
						sendlocation(recpnames, recpnumbers);

						ShareLocationObject myObject = new ShareLocationObject();
						Gson gson1 = new Gson();
						String json1 = gson1.toJson(myObject);
						SharedPreferences locationshapref1 = getSharedPreferences(
								"ShareLocationShared", 0);
						Editor prefsEditor = locationshapref1.edit();
						prefsEditor.putString("ShareLocationObject", json1);
						prefsEditor.commit();

						keepsharing = false;
						Log.d(TAG, "keepsharing = false; timetilvalue");
						resetRouteID();
					}
				} else {
					destinationtimevalue = obj.destinationtimevalue;
					destinationlatlong = obj.destinationlatlong;

					Location mycurrentlocation = getLocation();

					if (mycurrentlocation != null && destinationlatlong != null) {

						double latdiff = mycurrentlocation.getLatitude()
								- destinationlatlong.latitude;
						double longdiff = mycurrentlocation.getLongitude()
								- destinationlatlong.longitude;

						double abs_latdiff = (latdiff < 0) ? -latdiff : latdiff;
						double abs_longdiff = (longdiff < 0) ? -longdiff
								: longdiff;

						Log.d("abs_latdiff", "" + abs_latdiff);
						Log.d("abs_longdiff", "" + abs_longdiff);

						double dist = Math.sqrt((abs_latdiff * abs_latdiff)
								+ (abs_longdiff * abs_longdiff));

						Log.d("dist", "" + dist);

						if ((System.currentTimeMillis() < destinationtimevalue)
								&& (dist > GlobalVariables.GEOFENCING_RADIUS)) {

							sendlocation(recpnames, recpnumbers);
						} else {

							sendlocation(recpnames, recpnumbers);

							ShareLocationObject myObject = new ShareLocationObject();
							Gson gson1 = new Gson();
							String json1 = gson1.toJson(myObject);
							SharedPreferences locationshapref1 = getSharedPreferences(
									"ShareLocationShared", 0);
							Editor prefsEditor = locationshapref1.edit();
							prefsEditor.putString("ShareLocationObject", json1);
							prefsEditor.commit();

							keepsharing = false;
							Log.d(TAG,
									"keepsharing = false; destinationtimevalue");
							resetRouteID();
						}
					} else {
						if (System.currentTimeMillis() > destinationtimevalue) {
							keepsharing = false;
							Log.d(TAG,
									"keepsharing = false; destinationtimevalue 2");
							resetRouteID();
						} else {
							sendlocation(recpnames, recpnumbers);
						}
					}
				}

				int interval;
				SharedPreferences mPrefs1111 = getSharedPreferences(
						"usersettings", 0);
				String sharelocationinterval = mPrefs1111.getString(
						"sharelocationinterval", "");
				if (sharelocationinterval.isEmpty()
						|| sharelocationinterval == null
						|| sharelocationinterval.equalsIgnoreCase("")) {
					interval = 2;
				} else {
					String[] arr = sharelocationinterval.trim().split(" ");
					interval = Integer.parseInt(arr[0].toString().trim());
				}

				if (keepsharing) {

					if (locationsend) {

						SystemClock.sleep(60000 * interval);
						//SystemClock.sleep(15000);
					} else {
						//SystemClock.sleep(15000);
						SystemClock.sleep(60000 * interval);
					}
				}

			} while (keepsharing);

			mytask.cancel(true);
			stopSelf();

			Log.d(TAG, "out of while loop keepsharing : " + keepsharing
					+ " locationsend : " + locationsend);
		}
	}
	
	private void resetRouteID() {
		Log.d(TAG, "reset routeId");
		SharedPreferences sharedPreferences = getSharedPreferences(
				"routeId", 0);
		Editor editor = sharedPreferences.edit();
		editor.putString("routeId", "");
		editor.commit();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		keepsharing = false;
		mytask.cancel(true);
		Log.d(TAG, "LocationShareService destroyed");

		locationManager.removeUpdates(this);
	}

	public void sendlocation(ArrayList<String> renames, ArrayList<String> renums) {

		Location location = getLocation();

		if (location != null) {
			// This needs to stop getting the location data and save the
			// battery power.
			mycurrentlocation = location;
			// locationManager.removeUpdates(this);

			String lcladdress = MapUtilityMethods.getAddress(
					getApplicationContext(), mycurrentlocation.getLatitude(),
					mycurrentlocation.getLongitude());

			if (lcladdress.length() == 0 || lcladdress.isEmpty()) {

				locationsend = false;

			} else {

				locationsend = true;

				double lat = mycurrentlocation.getLatitude();
				double longi = mycurrentlocation.getLongitude();

				String latlong = String.valueOf(lat) + ","
						+ String.valueOf(longi);

				SharedPreferences mPrefs = getSharedPreferences("FacebookData",
						0);
				String FullName = mPrefs.getString("FullName", "");
				String MobileNumber = mPrefs.getString("MobileNumber", "");

				// Connect to google.com
				HttpClient httpClient = new DefaultHttpClient();
				String url_select = GlobalVariables.ServiceUrl
						+ "/sharelocationtomembers.php";
				HttpPost httpPost = new HttpPost(url_select);
				BasicNameValuePair MembersNumberBasicNameValuePair = new BasicNameValuePair(
						"MembersNumber", recpnumbers.toString().trim());
				BasicNameValuePair MembersNameBasicNameValuePair = new BasicNameValuePair(
						"MembersName", recpnames.toString().trim());
				BasicNameValuePair FullNameBasicNameValuePair = new BasicNameValuePair(
						"FullName", FullName.toString().trim());
				BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
						"MobileNumber", MobileNumber.toString().trim());
				/*FullName + " is at - "
						+ lcladdress.replaceAll("-", "")*/
				String msgStr =  FullName + " has started sharing location with you. Click here to track the ride";
				if(isResendingLocation){
					msgStr = "";
				}
				BasicNameValuePair MessageBasicNameValuePair = new BasicNameValuePair(
						"Message",msgStr);


				BasicNameValuePair latlongstrBasicNameValuePair = new BasicNameValuePair(
						"latlongstr", latlong);

				SharedPreferences sharedPreferences = getSharedPreferences(
						"routeId", 0);
				String routeId = sharedPreferences.getString("routeId", "");

				BasicNameValuePair routeIdNameValuePair = new BasicNameValuePair(
						"routeId", routeId);

				String authString;
				if (routeId != null && routeId.length() > 0) {
					authString = FullName.toString().trim() + latlong
							+ recpnames.toString().trim()
							+ recpnumbers.toString().trim() + msgStr
							+ MobileNumber.toString().trim() + routeId;
				} else {
					authString = FullName.toString().trim() + latlong
							+ recpnames.toString().trim()
							+ recpnumbers.toString().trim() +msgStr
							+ MobileNumber.toString().trim();
				}

				BasicNameValuePair authValuePair = new BasicNameValuePair(
						"auth",
						GlobalMethods.calculateCMCAuthString(authString));

				List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
				nameValuePairList.add(MembersNumberBasicNameValuePair);
				nameValuePairList.add(MembersNameBasicNameValuePair);
				nameValuePairList.add(FullNameBasicNameValuePair);
				nameValuePairList.add(MobileNumberBasicNameValuePair);
				nameValuePairList.add(MessageBasicNameValuePair);
				nameValuePairList.add(latlongstrBasicNameValuePair);
				nameValuePairList.add(authValuePair);
				nameValuePairList.add(routeIdNameValuePair);

				UrlEncodedFormEntity urlEncodedFormEntity = null;
				try {
					urlEncodedFormEntity = new UrlEncodedFormEntity(
							nameValuePairList);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				httpPost.setEntity(urlEncodedFormEntity);
				HttpResponse httpResponse = null;
				try {
					httpResponse = httpClient.execute(httpPost);

					InputStream inputStream = httpResponse.getEntity()
							.getContent();
					InputStreamReader inputStreamReader = new InputStreamReader(
							inputStream);

					BufferedReader bufferedReader = new BufferedReader(
							inputStreamReader);

					StringBuilder stringBuilder = new StringBuilder();

					String bufferedStrChunk = null;
					String resp = null;

					while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
						resp = stringBuilder.append(bufferedStrChunk)
								.toString();
					}
					if(!isResendingLocation){
						isResendingLocation = true;
						msgStr = "";
					}

					Log.d("sharelocationtomembers resp", "" + resp);

					if (resp != null && resp.length() > 0
							&& resp.contains("Unauthorized Access")) {
						Log.e("sharelocationtomembers",
								"sharelocationtomembers Unauthorized Access");

						return;
					} else if (resp != null && resp.length() > 0
							&& !resp.contains("no one in database")) {
						Editor editor = sharedPreferences
								.edit();
						editor.putString("routeId", resp);
						editor.commit();
					}
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} else {
			locationsend = false;
		}
	}

	public Location getLocation() {
		Location location = null;
		try {
			locationManager = (LocationManager) this
					.getSystemService(LOCATION_SERVICE);

			// getting GPS status
			boolean isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			boolean isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
				AlertDialog.Builder dialog = new AlertDialog.Builder(this);
				dialog.setMessage("Please check your location services");
				dialog.setNegativeButton("Settings",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(
									DialogInterface paramDialogInterface,
									int paramInt) {
								Intent myIntent = new Intent(
										Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								startActivity(myIntent);
								// get gps

							}
						});
				dialog.show();
				return null;
			} else {

				double lat = 0;
				double lng = 0;
				// get the location by gps
				if (isGPSEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER, 20000, 1, this);
						Log.d("GPS Enabled", "GPS Enabled");
						if (locationManager != null) {
							location = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								lat = location.getLatitude();
								lng = location.getLongitude();
							}
						}
					}
				}

				// First get location from Network Provider
				if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER, 20000, 1, this);
					Log.d("Network", "Network");
					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							lat = location.getLatitude();
							lng = location.getLongitude();
						}
					}
				}

				Log.d("lat", "" + lat);
				Log.d("lng", "" + lng);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return location;



	}

	// ///////

	private class ConnectionTaskForsendlocation extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionsendlocation mAuth1 = new AuthenticateConnectionsendlocation();
			try {
				mAuth1.msg = args[0];
				mAuth1.latlongstr = args[1];
				mAuth1.names = args[2];
				mAuth1.numbers = args[3];
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

	public class AuthenticateConnectionsendlocation {

		public String msg;
		public String latlongstr;
		public String names;
		public String numbers;

		public AuthenticateConnectionsendlocation() {

		}

		public void connection() throws Exception {

			SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
			String FullName = mPrefs.getString("FullName", "");
			String MobileNumber = mPrefs.getString("MobileNumber", "");

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/sharelocationtomembers.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair MembersNumberBasicNameValuePair = new BasicNameValuePair(
					"MembersNumber", numbers);
			BasicNameValuePair MembersNameBasicNameValuePair = new BasicNameValuePair(
					"MembersName", names);
			BasicNameValuePair FullNameBasicNameValuePair = new BasicNameValuePair(
					"FullName", FullName.toString().trim());
			BasicNameValuePair MobileNumberBasicNameValuePair = new BasicNameValuePair(
					"MobileNumber", MobileNumber.toString().trim());
			BasicNameValuePair MessageBasicNameValuePair = new BasicNameValuePair(
					"Message", FullName + " is at - " + msg.replaceAll("-", ""));

			BasicNameValuePair latlongstrBasicNameValuePair = new BasicNameValuePair(
					"latlongstr", latlongstr);

			String authString = FullName.toString().trim() + latlongstr
					+ recpnames.toString().trim()
					+ recpnumbers.toString().trim() + FullName + " is at - "
					+ msg.replaceAll("-", "") + MobileNumber.toString().trim();
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(MembersNumberBasicNameValuePair);
			nameValuePairList.add(MembersNameBasicNameValuePair);
			nameValuePairList.add(FullNameBasicNameValuePair);
			nameValuePairList.add(MobileNumberBasicNameValuePair);
			nameValuePairList.add(MessageBasicNameValuePair);
			nameValuePairList.add(latlongstrBasicNameValuePair);
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
				String shareresponse = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("shareresponse", "" + stringBuilder.toString());
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}
}
