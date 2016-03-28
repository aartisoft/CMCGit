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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.R;
import com.clubmycab.asynctasks.GlobalAsyncTask;
import com.clubmycab.asynctasks.GlobalAsyncTask.AsyncTaskResultListener;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class LocationInMapFragmentActivity extends FragmentActivity implements
		AsyncTaskResultListener {

	String address;
	String latlongmap;
	String routeId;

	GoogleMap sharelocationmap;
	TextView sharelocationtext;

	boolean exceptioncheck = false;

	String routeresponse;
	String[] coordinatesArray, atTimeArray;

	String CompletePageResponse;
	ArrayList<PolylineOptions> rectlinesarr = new ArrayList<PolylineOptions>();
	ArrayList<String> steps = new ArrayList<String>();
	ArrayList<LatLng> startaddlatlng = new ArrayList<LatLng>();
	ArrayList<LatLng> endaddlatlng = new ArrayList<LatLng>();
	ArrayList<String> Summary = new ArrayList<String>();
	ArrayList<String> startaddress = new ArrayList<String>();
	ArrayList<String> endaddress = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_in_map);

		// Check if Internet present
		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					LocationInMapFragmentActivity.this);
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

		Bundle extras = getIntent().getExtras();
		address = extras.getString("address");
		latlongmap = extras.getString("latlongmap");
		routeId = extras.getString("routeId");

		// if (comefrom.equalsIgnoreCase("GCM")) {
		// This api Call for mark notification read
		String nid = extras.getString("nid");
		String params = "rnum=" + "&nid=" + nid + "&auth="
				+ GlobalMethods.calculateCMCAuthString(nid);
		String endpoint = GlobalVariables.ServiceUrl
				+ "/UpdateNotificationStatusToRead.php";
		Log.d("LocationInMapFragment",
				"UpdateNotificationStatusToRead endpoint : " + endpoint
						+ " params : " + params);
		new GlobalAsyncTask(this, endpoint, params, null, this, false,
				"UpdateNotificationStatusToRead", false);

		// }

		sharelocationtext = (TextView) findViewById(R.id.sharelocationtext);
		sharelocationtext.setTypeface(Typeface.createFromAsset(getAssets(),
				"NeutraText-Bold.ttf"));

		sharelocationtext.setText(address);

		sharelocationmap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.sharelocationmap)).getMap();
		sharelocationmap.setMyLocationEnabled(true);

		// routeId = "7089987551457697512";
		if (routeId != null && !routeId.equalsIgnoreCase("null")
				&& routeId.length() > 0) {
			Log.d("LocationInMapFragmentActivity", "routeId : " + routeId);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForGetRoute()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new ConnectionTaskForGetRoute().execute();
			}
		} else {
			plotPinWithoutRoute();
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
	public void getResult(String response, String uniqueID) {
		// TODO Auto-generated method stub

	}

	private void plotPinWithoutRoute() {
		String[] arr = latlongmap.split(",");

		LatLng pos = new LatLng(Double.parseDouble(arr[0]),
				Double.parseDouble(arr[1]));

		sharelocationmap.addMarker(new MarkerOptions().position(pos).icon(
				BitmapDescriptorFactory
						.fromResource(R.drawable.sharelocationmarker)));

		// Showing the current location in Google Map
		sharelocationmap.moveCamera(CameraUpdateFactory.newLatLng(pos));

		// Zoom in the Google Map
		sharelocationmap.animateCamera(CameraUpdateFactory.zoomTo(10));
	}

	private class ConnectionTaskForGetRoute extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionGetRoute mAuth1 = new AuthenticateConnectionGetRoute();
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

			if (exceptioncheck) {
				exceptioncheck = false;
				Toast.makeText(LocationInMapFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (routeresponse == null) {
				Toast.makeText(LocationInMapFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
			} else if (routeresponse.contains("Unauthorized Access")) {
				Log.e("LocationInMapFragmentActivity",
						"ConnectionTaskForGetRoute Unauthorized Access");
				Toast.makeText(LocationInMapFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
			} else {

				// {"status":"success","data":{"coordinates":"28.4338489,77.1033643~28.4336181,77.1045686~28.4339064,77.1035731~28.4338957,77.1035572~28.433955,77.1035755~28.4338994,77.1035566","routeId":"7089987551457697512","updatedOn":"2016-03-11 17:28:32"}}
				try {
					JSONObject jsonObject = new JSONObject(routeresponse);
					if (jsonObject.get("status").toString()
							.equalsIgnoreCase("success")) {
						JSONObject jsonObjectData = new JSONObject(jsonObject
								.get("data").toString());
						String coordinates = jsonObjectData.get("coordinates")
								.toString();
						String[] array = coordinates.split("~");

						String atTime = jsonObjectData.get("atTime").toString();
						String[] arrayAtTime = atTime.split("~");

						if (array.length > 2) { // atleast 3 points needed to
												// plot route
							coordinatesArray = array;
							atTimeArray = arrayAtTime;

							// if (Build.VERSION.SDK_INT >=
							// Build.VERSION_CODES.HONEYCOMB) {
							// new ConnectionTaskForSingleRoot()
							// .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
							// } else {
							// new ConnectionTaskForSingleRoot().execute();
							// }

							// for (int i = 0; i < rectlinesarr.size(); i++) {
							// sharelocationmap.addPolyline(rectlinesarr
							// .get(i));
							//
							// List<LatLng> points = rectlinesarr.get(i)
							// .getPoints();
							//
							// bc = new LatLngBounds.Builder();
							//
							// for (LatLng item : points) {
							// bc.include(item);
							// }
							// }

							LatLngBounds.Builder bc = new LatLngBounds.Builder();

							String[] arr = coordinatesArray[0].split(",");
							LatLng pos = new LatLng(Double.parseDouble(arr[0]),
									Double.parseDouble(arr[1]));
							sharelocationmap.addMarker(new MarkerOptions()
									.position(pos)
									.title(atTimeArray[0])
									.icon(BitmapDescriptorFactory
											.fromResource(R.drawable.start)));
							bc.include(pos);

							arr = coordinatesArray[coordinatesArray.length - 1]
									.split(",");
							pos = new LatLng(Double.parseDouble(arr[0]),
									Double.parseDouble(arr[1]));
							sharelocationmap
									.addMarker(new MarkerOptions()
											.position(pos)
											.title(atTimeArray[coordinatesArray.length - 1])
											.icon(BitmapDescriptorFactory
													.fromResource(R.drawable.sharelocationmarker)));
							bc.include(pos);

							for (int i = 1; i < (coordinatesArray.length - 1); i++) {
								String[] posArray = coordinatesArray[i]
										.split(",");
								sharelocationmap
										.addMarker(new MarkerOptions()
												.position(
														new LatLng(
																Double.parseDouble(posArray[0]),
																Double.parseDouble(posArray[1])))
												.title(atTimeArray[i])
												.icon(BitmapDescriptorFactory
														.fromResource(R.drawable.locationmarker)));
							}

							sharelocationmap.moveCamera(CameraUpdateFactory
									.newLatLngBounds(bc.build(), 50));
						} else {
							plotPinWithoutRoute();
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					Toast.makeText(LocationInMapFragmentActivity.this,
							getResources().getString(R.string.exceptionstring),
							Toast.LENGTH_LONG).show();
				}
			}

		}
	}

	public class AuthenticateConnectionGetRoute {

		public AuthenticateConnectionGetRoute() {

		}

		public void connection() throws Exception {

			// /////////////
			HttpClient httpClient11 = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/getRouteCoordinates.php";

			HttpPost httpPost11 = new HttpPost(url_select);
			BasicNameValuePair routeIDBasicNameValuePair = new BasicNameValuePair(
					"routeId", routeId);

			String authString = routeId;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList11 = new ArrayList<NameValuePair>();
			nameValuePairList11.add(routeIDBasicNameValuePair);
			nameValuePairList11.add(authValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity11 = new UrlEncodedFormEntity(
					nameValuePairList11);
			httpPost11.setEntity(urlEncodedFormEntity11);
			HttpResponse httpResponse11 = httpClient11.execute(httpPost11);

			Log.d("httpResponse", "" + httpResponse11);

			InputStream inputStream11 = httpResponse11.getEntity().getContent();
			InputStreamReader inputStreamReader11 = new InputStreamReader(
					inputStream11);

			BufferedReader bufferedReader11 = new BufferedReader(
					inputStreamReader11);

			StringBuilder stringBuilder11 = new StringBuilder();

			String bufferedStrChunk11 = null;

			while ((bufferedStrChunk11 = bufferedReader11.readLine()) != null) {
				routeresponse = stringBuilder11.append(bufferedStrChunk11)
						.toString();
			}

			Log.d("routeresponse", "" + routeresponse);

		}
	}

	// private class ConnectionTaskForSingleRoot extends
	// AsyncTask<String, Void, Void> {
	//
	// private ProgressDialog dialog = new ProgressDialog(
	// LocationInMapFragmentActivity.this);
	//
	// @Override
	// protected void onPreExecute() {
	// dialog.setMessage("Please Wait...");
	// dialog.setCancelable(false);
	// dialog.setCanceledOnTouchOutside(false);
	// dialog.show();
	// }
	//
	// @Override
	// protected Void doInBackground(String... args) {
	// AuthenticateConnectionSingleRoot mAuth1 = new
	// AuthenticateConnectionSingleRoot();
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
	// if (dialog.isShowing()) {
	// dialog.dismiss();
	// }
	//
	// if (exceptioncheck) {
	// exceptioncheck = false;
	// Toast.makeText(LocationInMapFragmentActivity.this,
	// getResources().getString(R.string.exceptionstring),
	// Toast.LENGTH_LONG).show();
	// return;
	// }
	//
	// // int index = 0;
	// rectlinesarr.clear();
	//
	// Random rnd = new Random();
	// int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256),
	// rnd.nextInt(256));
	// for (int i = 0; i < steps.size(); i++) {
	//
	// ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();
	//
	// JSONArray subArray123;
	// try {
	// subArray123 = new JSONArray(steps.get(i));
	// for (int i111 = 0; i111 < subArray123.length(); i111++) {
	// String locationstr = subArray123.getJSONObject(i111)
	// .getString("start_location").toString();
	//
	// JSONObject jsonObject11 = new JSONObject(locationstr);
	// double lat1 = Double.parseDouble(jsonObject11
	// .getString("lat"));
	// double lng1 = Double.parseDouble(jsonObject11
	// .getString("lng"));
	//
	// listGeopoints.add(new LatLng(lat1, lng1));
	//
	// // /
	// String locationstr1 = subArray123.getJSONObject(i111)
	// .getString("polyline").toString();
	//
	// JSONObject jsonObject111 = new JSONObject(locationstr1);
	// String points = jsonObject111.getString("points");
	// ArrayList<LatLng> arr = decodePoly(points);
	// for (int j = 0; j < arr.size(); j++) {
	// listGeopoints.add(new LatLng(arr.get(j).latitude,
	// arr.get(j).longitude));
	// }
	// // /
	// String locationstr11 = subArray123.getJSONObject(i111)
	// .getString("end_location").toString();
	//
	// JSONObject jsonObject1111 = new JSONObject(
	// locationstr11);
	// double lat11 = Double.parseDouble(jsonObject1111
	// .getString("lat"));
	// double lng11 = Double.parseDouble(jsonObject1111
	// .getString("lng"));
	//
	// listGeopoints.add(new LatLng(lat11, lng11));
	//
	// // Random rnd = new Random();
	// // int color = Color.argb(255, rnd.nextInt(256),
	// // rnd.nextInt(256), rnd.nextInt(256));
	//
	// PolylineOptions rectLine = new PolylineOptions().width(
	// 5).color(color);
	// rectLine.addAll(listGeopoints);
	// rectlinesarr.add(rectLine);
	//
	// }
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// LatLngBounds.Builder bc = null;
	//
	// for (int i = 0; i < rectlinesarr.size(); i++) {
	// sharelocationmap.addPolyline(rectlinesarr.get(i));
	//
	// List<LatLng> points = rectlinesarr.get(i).getPoints();
	//
	// bc = new LatLngBounds.Builder();
	//
	// for (LatLng item : points) {
	// bc.include(item);
	// }
	// }
	//
	// bc.include(startaddlatlng.get(0));
	// bc.include(endaddlatlng.get(endaddlatlng.size() - 1));
	// sharelocationmap.moveCamera(CameraUpdateFactory.newLatLngBounds(
	// bc.build(), 50));
	//
	// String[] arr = coordinatesArray[0].split(",");
	// LatLng pos = new LatLng(Double.parseDouble(arr[0]),
	// Double.parseDouble(arr[1]));
	// sharelocationmap.addMarker(new MarkerOptions()
	// .position(pos)
	// .snippet("start")
	// .icon(BitmapDescriptorFactory
	// .fromResource(R.drawable.start)));
	//
	// arr = coordinatesArray[coordinatesArray.length - 1].split(",");
	// pos = new LatLng(Double.parseDouble(arr[0]),
	// Double.parseDouble(arr[1]));
	// sharelocationmap.addMarker(new MarkerOptions().position(pos).icon(
	// BitmapDescriptorFactory
	// .fromResource(R.drawable.sharelocationmarker)));
	//
	// for (int i = 1; i < (coordinatesArray.length - 1); i++) {
	// String[] posArray = coordinatesArray[i].split(",");
	// sharelocationmap.addMarker(new MarkerOptions()
	// .position(
	// new LatLng(Double.parseDouble(posArray[0]),
	// Double.parseDouble(posArray[1])))
	// .title("Updated at")
	// .snippet(atTimeArray[i])
	// .icon(BitmapDescriptorFactory
	// .fromResource(R.drawable.locationmarker)));
	// }
	// }
	//
	// }
	//
	// public class AuthenticateConnectionSingleRoot {
	//
	// public AuthenticateConnectionSingleRoot() {
	//
	// }
	//
	// public void connection() throws Exception {
	// String src = coordinatesArray[0];
	// String des = coordinatesArray[coordinatesArray.length - 1];
	//
	// Log.d("src:", "" + src);
	// Log.d("des", "" + des);
	//
	// String wayPointUrl = "&waypoints=optimize:false";
	//
	// for (int i = 1; i < (coordinatesArray.length - 1); i++) {
	// wayPointUrl += ("%7C" + coordinatesArray[i]);
	// }
	//
	// //
	// http://maps.googleapis.com/maps/api/directions/json?origin=28.48971,77.062282&destination=28.6289146,77.2152869&waypoints=optimize:true|28.5440936,77.2359|28.549156,77.2527764|28.5252398,77.2543449&sensor=false
	//
	// String url = "https://maps.googleapis.com/maps/api/directions/json?"
	// + "origin="
	// + src
	// + "&destination="
	// + des
	// + wayPointUrl
	// + "&sensor=false&units=metric&mode=driving&alternatives=false&key="
	// + GlobalVariables.GoogleMapsAPIKey;
	//
	// Log.d("url single path", "" + url);
	//
	// CompletePageResponse = new Communicator().executeHttpGet(url);
	//
	// CompletePageResponse = CompletePageResponse
	// .replaceAll("\\\\/", "/");
	//
	// JSONObject jsonObject = new JSONObject(CompletePageResponse);
	//
	// String name = jsonObject.getString("routes");
	//
	// JSONArray subArray = new JSONArray(name);
	// Summary.clear();
	//
	// for (int i = 0; i < subArray.length(); i++) {
	//
	// Summary.add(subArray.getJSONObject(i).getString("summary")
	// .toString());
	//
	// String name1 = subArray.getJSONObject(i).getString("legs")
	// .toString();
	//
	// JSONArray subArray1 = new JSONArray(name1);
	//
	// for (int i1 = 0; i1 < subArray1.length(); i1++) {
	//
	// // int i1 = 0;
	// startaddress.add(subArray1.getJSONObject(i1)
	// .getString("start_address").toString());
	// endaddress.add(subArray1.getJSONObject(i1)
	// .getString("end_address").toString());
	//
	// String startadd = subArray1.getJSONObject(i1)
	// .getString("start_location").toString();
	//
	// JSONObject jsonObject1 = new JSONObject(startadd);
	// double lat = Double.parseDouble(jsonObject1
	// .getString("lat"));
	// double lng = Double.parseDouble(jsonObject1
	// .getString("lng"));
	//
	// startaddlatlng.add(new LatLng(lat, lng));
	//
	// //
	// String endadd = subArray1.getJSONObject(i1)
	// .getString("end_location").toString();
	//
	// JSONObject jsonObject41 = new JSONObject(endadd);
	// double lat4 = Double.parseDouble(jsonObject41
	// .getString("lat"));
	// double lng4 = Double.parseDouble(jsonObject41
	// .getString("lng"));
	//
	// endaddlatlng.add(new LatLng(lat4, lng4));
	//
	// steps.add(subArray1.getJSONObject(i1).getString("steps")
	// .toString());
	// }
	// }
	// }
	// }
	//
	// private ArrayList<LatLng> decodePoly(String encoded) {
	// ArrayList<LatLng> poly = new ArrayList<LatLng>();
	// int index = 0, len = encoded.length();
	// int lat = 0, lng = 0;
	// while (index < len) {
	// int b, shift = 0, result = 0;
	// do {
	// b = encoded.charAt(index++) - 63;
	// result |= (b & 0x1f) << shift;
	// shift += 5;
	// } while (b >= 0x20);
	// int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	// lat += dlat;
	// shift = 0;
	// result = 0;
	// do {
	// b = encoded.charAt(index++) - 63;
	// result |= (b & 0x1f) << shift;
	// shift += 5;
	// } while (b >= 0x20);
	// int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	// lng += dlng;
	//
	// LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
	// poly.add(position);
	// }
	// return poly;
	// }
}
