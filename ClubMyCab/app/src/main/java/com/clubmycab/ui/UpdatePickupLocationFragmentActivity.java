package com.clubmycab.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clubmycab.Communicator;
import com.clubmycab.R;
import com.clubmycab.maps.MapUtilityMethods;
import com.clubmycab.utility.AppConstants;
import com.clubmycab.utility.GlobalMethods;
import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UpdatePickupLocationFragmentActivity extends FragmentActivity
		implements LocationListener {

	String CabId;
	String CabStatus;
	String MobileNumber;
	String OwnerName;
	String FromLocation;
	String ToLocation;
	String TravelDate;
	String TravelTime;
	String Seats;
	String RemainingSeats;
	String Seat_Status;
	String Distance;
	String OpenTime;
	String CompletePageResponse;
	String FromShortName;
	String ToShortName;

	boolean exceptioncheck = false;

	ArrayList<String> steps = new ArrayList<String>();
	ArrayList<String> Summary = new ArrayList<String>();
	ArrayList<String> startaddress = new ArrayList<String>();
	ArrayList<String> endaddress = new ArrayList<String>();
	ArrayList<LatLng> startaddlatlng = new ArrayList<LatLng>();
	ArrayList<LatLng> endaddlatlng = new ArrayList<LatLng>();
	ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();
	ArrayList<LatLng> via_waypoint = new ArrayList<LatLng>();
	ArrayList<String> via_waypointstrarr = new ArrayList<String>();
	ArrayList<PolylineOptions> rectlinesarr = new ArrayList<PolylineOptions>();

	String memberlocationaddressFrom;
	LatLng memberlocationlatlongFrom;
	String memberlocationaddressTo;
	LatLng memberlocationlatlongTo;
	private boolean isPick = false;
	Marker mylocationmarker;

	String FullName;
	String MemberNumberstr;

	String showmembersresp;
	String checkpoolalreadyjoinresp;
	String updatelocationpoolresp;

	ArrayList<String> ShowMemberName = new ArrayList<String>();
	ArrayList<String> ShowMemberNumber = new ArrayList<String>();
	ArrayList<String> ShowMemberLocationAddress = new ArrayList<String>();
	ArrayList<String> ShowMemberLocationLatLong = new ArrayList<String>();
	ArrayList<String> ShowMemberImageName = new ArrayList<String>();
	ArrayList<String> ShowMemberStatus = new ArrayList<String>();
	ArrayList<String> ShowMemberLocationAddressEnd = new ArrayList<String>();
	ArrayList<String> ShowMemberLocationLatLongEnd = new ArrayList<String>();
	GoogleMap updatepoolmap;

	LinearLayout beforejoinpoolll;
	LinearLayout afterjoinpoolll;

	// //after pooljoin
	Button updatepickuplocationbtn;

	ImageView memimage;

	String usermemname = null;
	String usermemnumber = null;
	String usermemlocadd = null;
	String usermemloclatlong = null;
	String usermemimagename = null;
	String usermemst = null;
	String usermemlocaddEnd = null;
	String usermemloclatlongEnd = null;

	ImageView updatelocationmarker;
	TextView joinpoollocationtext;

	ProgressDialog onedialog;

	LocationManager locationManager;
	Location mycurrentlocationobject;

	Button buttonUpdateLocationMarker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_pickup_location);

		// Check if Internet present
		if (!isOnline()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					UpdatePickupLocationFragmentActivity.this);
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

		Intent intent = getIntent();
		CabId = intent.getStringExtra("CabId");
		CabStatus = intent.getStringExtra("CabStatus");
		MobileNumber = intent.getStringExtra("MobileNumber");
		OwnerName = intent.getStringExtra("OwnerName");
		FromLocation = intent.getStringExtra("FromLocation");
		ToLocation = intent.getStringExtra("ToLocation");
		TravelDate = intent.getStringExtra("TravelDate");
		TravelTime = intent.getStringExtra("TravelTime");
		Seats = intent.getStringExtra("Seats");
		RemainingSeats = intent.getStringExtra("RemainingSeats");
		Seat_Status = intent.getStringExtra("Seat_Status");
		Distance = intent.getStringExtra("Distance");
		OpenTime = intent.getStringExtra("OpenTime");
		CompletePageResponse = intent.getStringExtra("CompletePageResponse");
		checkpoolalreadyjoinresp = intent
				.getStringExtra("checkpoolalreadyjoinresp");
		FromShortName = intent.getStringExtra("FromShortName");
		ToShortName = intent.getStringExtra("ToShortName");

		memberlocationaddressFrom = "";
		memberlocationaddressTo = "";

		updatelocationmarker = (ImageView) findViewById(R.id.updatelocationmarker);
		joinpoollocationtext = (TextView) findViewById(R.id.joinpoollocationtext);
		joinpoollocationtext.setTypeface(Typeface.createFromAsset(getAssets(),
				AppConstants.HELVITICA));
		buttonUpdateLocationMarker = (Button) findViewById(R.id.buttonUpdateLocationMarker);

		joinpoollocationtext.setText(FromLocation);

		updatelocationmarker.setVisibility(View.GONE);
		buttonUpdateLocationMarker.setVisibility(View.GONE);

		SharedPreferences mPrefs = getSharedPreferences("FacebookData", 0);
		FullName = mPrefs.getString("FullName", "");
		MemberNumberstr = mPrefs.getString("MobileNumber", "");

		// / after
		updatepickuplocationbtn = (Button) findViewById(R.id.updatepickuplocationbtn);
		updatepickuplocationbtn.setTypeface(Typeface.createFromAsset(
				getAssets(), AppConstants.HELVITICA));

		mycurrentlocationobject = getLocation();
		updatepickuplocationbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// TODO Auto-generated method stub

				if (updatelocationmarker.getVisibility() == View.VISIBLE) {

					if (!isPick) {

						AlertDialog.Builder builder = new AlertDialog.Builder(
								UpdatePickupLocationFragmentActivity.this);
						builder.setMessage(memberlocationaddressFrom
								.toUpperCase());
						builder.setCancelable(false);
						builder.setPositiveButton("Update Location",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {

										isPick = true;

										mylocationmarker = updatepoolmap
												.addMarker(new MarkerOptions()
														.position(
																memberlocationlatlongFrom)
														.snippet("mylocation")
														.title(usermemlocadd)
														.icon(BitmapDescriptorFactory
																.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
										updatepickuplocationbtn
												.setText("Update Drop Location");
										buttonUpdateLocationMarker
												.setText("Tap to update drop location");
										joinpoollocationtext
												.setText(memberlocationaddressFrom);

									}
								});
						builder.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});

						AlertDialog dialog = builder.show();
						TextView messageText = (TextView) dialog
								.findViewById(android.R.id.message);
						messageText.setGravity(Gravity.CENTER);
						dialog.show();
					} else {

						AlertDialog.Builder builder = new AlertDialog.Builder(
								UpdatePickupLocationFragmentActivity.this);
						builder.setMessage(memberlocationaddressTo
								.toUpperCase());
						builder.setCancelable(false);
						builder.setPositiveButton("Update Location",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										joinpoollocationtext
												.setText(memberlocationaddressTo);

										onedialog = new ProgressDialog(
												UpdatePickupLocationFragmentActivity.this);
										onedialog.setMessage("Please Wait...");
										onedialog.setCancelable(false);
										onedialog
												.setCanceledOnTouchOutside(false);
										onedialog.show();

										if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
											new ConnectionTaskForupdatingapool()
													.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
										} else {
											new ConnectionTaskForupdatingapool()
													.execute();
										}
									}
								});
						builder.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});

						AlertDialog dialog = builder.show();
						TextView messageText = (TextView) dialog
								.findViewById(android.R.id.message);
						messageText.setGravity(Gravity.CENTER);
						dialog.show();
					}

				} else {

					Location location = mycurrentlocationobject;
					Log.d("UpdatePickupLocationFragmentActivity", "location : "
							+ location);
					if (location != null) {

						LatLng point = new LatLng(location.getLatitude(),
								location.getLongitude());
						updatepoolmap.animateCamera(CameraUpdateFactory
								.newLatLngZoom(point, 21));

						Toast.makeText(
								UpdatePickupLocationFragmentActivity.this,
								"We have set your pick-up to your current location, please move the map around to select a different location",
								Toast.LENGTH_LONG).show();

						updatelocationmarker.setVisibility(View.VISIBLE);
						buttonUpdateLocationMarker.setVisibility(View.VISIBLE);
						buttonUpdateLocationMarker
								.setText("Tap to update pickup location");
						// tvJoinRide.setText("Select Pickup Location");
						// buttonLocationMarker
						// .setText("Tap to select Pickup Location");
					} else {
						Toast.makeText(
								UpdatePickupLocationFragmentActivity.this,
								"Please update your pickup location by clicking on map",
								Toast.LENGTH_LONG).show();
					}
				}

			}
		});

		buttonUpdateLocationMarker
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						updatepickuplocationbtn.performClick();
					}
				});

		onedialog = new ProgressDialog(
				UpdatePickupLocationFragmentActivity.this);
		onedialog.setMessage("Please Wait...");
		onedialog.setCancelable(false);
		onedialog.setCanceledOnTouchOutside(false);
		onedialog.show();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new ConnectionTaskForDirectionsnew()
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			new ConnectionTaskForDirectionsnew().execute();
		}

	}

	// /////////////////////
	// ///////

	private class ConnectionTaskForDirectionsnew extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionGetDirectionnew mAuth1 = new AuthenticateConnectionGetDirectionnew();
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
				Toast.makeText(UpdatePickupLocationFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}
			Random rnd = new Random();
			int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256),
					rnd.nextInt(256));
			for (int i = 0; i < steps.size(); i++) {

				ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();

				JSONArray subArray123;
				try {
					subArray123 = new JSONArray(steps.get(i));
					for (int i111 = 0; i111 < subArray123.length(); i111++) {

						String locationstr = subArray123.getJSONObject(i111)
								.getString("start_location").toString();

						JSONObject jsonObject11 = new JSONObject(locationstr);
						double lat1 = Double.parseDouble(jsonObject11
								.getString("lat"));
						double lng1 = Double.parseDouble(jsonObject11
								.getString("lng"));

						listGeopoints.add(new LatLng(lat1, lng1));

						// /
						String locationstr1 = subArray123.getJSONObject(i111)
								.getString("polyline").toString();

						JSONObject jsonObject111 = new JSONObject(locationstr1);
						String points = jsonObject111.getString("points");
						ArrayList<LatLng> arr = decodePoly(points);
						for (int j = 0; j < arr.size(); j++) {
							listGeopoints.add(new LatLng(arr.get(j).latitude,
									arr.get(j).longitude));
						}
						// /
						String locationstr11 = subArray123.getJSONObject(i111)
								.getString("end_location").toString();

						JSONObject jsonObject1111 = new JSONObject(
								locationstr11);
						double lat11 = Double.parseDouble(jsonObject1111
								.getString("lat"));
						double lng11 = Double.parseDouble(jsonObject1111
								.getString("lng"));

						listGeopoints.add(new LatLng(lat11, lng11));

						PolylineOptions rectLine = new PolylineOptions().width(
								5).color(color);
						rectLine.addAll(listGeopoints);
						rectlinesarr.add(rectLine);

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			updatepoolmap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.updatepoolmap)).getMap();

			updatepoolmap.setMyLocationEnabled(true);

			updatepoolmap.setOnMapClickListener(new OnMapClickListener() {

				@Override
				public void onMapClick(LatLng point) {
					updatepoolmap.animateCamera(CameraUpdateFactory
							.newLatLng(point));
					updatelocationmarker.setVisibility(View.VISIBLE);
					buttonUpdateLocationMarker.setVisibility(View.VISIBLE);

					if (!isPick) {
						memberlocationlatlongFrom = point;
						memberlocationaddressFrom = MapUtilityMethods
								.getAddress(
										UpdatePickupLocationFragmentActivity.this,
										point.latitude, point.longitude);
						joinpoollocationtext.setText(memberlocationaddressFrom);

						Log.d("memberlocationlatlongfrom", ""
								+ memberlocationlatlongFrom);
						Log.d("memberlocationaddressfrom", ""
								+ memberlocationaddressFrom);

					} else {
						memberlocationlatlongTo = point;
						memberlocationaddressTo = MapUtilityMethods.getAddress(
								UpdatePickupLocationFragmentActivity.this,
								point.latitude, point.longitude);
						joinpoollocationtext.setText(memberlocationaddressTo);

						Log.d("memberlocationlatlongTo", ""
								+ memberlocationlatlongTo);
						Log.d("memberlocationaddressTo", ""
								+ memberlocationaddressTo);

					}

				}
			});

			updatepoolmap
					.setOnCameraChangeListener(new OnCameraChangeListener() {

						@Override
						public void onCameraChange(
								final CameraPosition cameraPosition) {

							LatLng mapcenter = cameraPosition.target;

							if (!isPick) {

								memberlocationlatlongFrom = mapcenter;
								memberlocationaddressFrom = MapUtilityMethods
										.getAddress(
												UpdatePickupLocationFragmentActivity.this,
												mapcenter.latitude,
												mapcenter.longitude);
								joinpoollocationtext
										.setText(memberlocationaddressFrom);

								Log.d("memberlocationlatlongfrom", ""
										+ memberlocationlatlongFrom);
								Log.d("memberlocationaddressfrom", ""
										+ memberlocationaddressFrom);
							} else {
								memberlocationlatlongTo = mapcenter;
								memberlocationaddressTo = MapUtilityMethods
										.getAddress(
												UpdatePickupLocationFragmentActivity.this,
												mapcenter.latitude,
												mapcenter.longitude);
								joinpoollocationtext
										.setText(memberlocationaddressTo);

								Log.d("memberlocationlatlongTo", ""
										+ memberlocationlatlongTo);
								Log.d("memberlocationaddressTo", ""
										+ memberlocationaddressTo);

							}

						}
					});

			LatLngBounds.Builder bc = null;

			for (int i = 0; i < rectlinesarr.size(); i++) {
				updatepoolmap.addPolyline(rectlinesarr.get(i));

				List<LatLng> points = rectlinesarr.get(i).getPoints();

				bc = new LatLngBounds.Builder();

				for (LatLng item : points) {
					bc.include(item);
				}
			}
			bc.include(startaddlatlng.get(0));
			bc.include(endaddlatlng.get(endaddlatlng.size() - 1));
			updatepoolmap.moveCamera(CameraUpdateFactory.newLatLngBounds(
					bc.build(), 50));

			// Marker marker = updatepoolmap.addMarker(new MarkerOptions()
			// .position(startaddlatlng.get(0))
			// .title(startaddress.get(0))
			// .snippet("start")
			// .icon(BitmapDescriptorFactory
			// .fromResource(R.drawable.start)));
			//
			// Marker marker1 = updatepoolmap
			// .addMarker(new MarkerOptions()
			// .position(endaddlatlng.get(0))
			// .title(endaddress.get(0))
			// .snippet("end")
			// .icon(BitmapDescriptorFactory
			// .fromResource(R.drawable.end)));

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForShowMembersOnMap()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new ConnectionTaskForShowMembersOnMap().execute();
			}
		}

	}

	public class AuthenticateConnectionGetDirectionnew {

		public AuthenticateConnectionGetDirectionnew() {

		}

		public void connection() throws Exception {

			steps.clear();
			Summary.clear();
			startaddress.clear();
			endaddress.clear();
			startaddlatlng.clear();
			endaddlatlng.clear();
			listGeopoints.clear();
			via_waypoint.clear();
			via_waypointstrarr.clear();
			rectlinesarr.clear();

			JSONObject jsonObject = new JSONObject(CompletePageResponse);

			String name = jsonObject.getString("routes");

			JSONArray subArray = new JSONArray(name);

			for (int i = 0; i < subArray.length(); i++) {

				Summary.add(subArray.getJSONObject(i).getString("summary")
						.toString());

				String name1 = subArray.getJSONObject(i).getString("legs")
						.toString();

				JSONArray subArray1 = new JSONArray(name1);

				for (int i1 = 0; i1 < subArray1.length(); i1++) {

					startaddress.add(subArray1.getJSONObject(i1)
							.getString("start_address").toString());
					endaddress.add(subArray1.getJSONObject(i1)
							.getString("end_address").toString());

					String startadd = subArray1.getJSONObject(i1)
							.getString("start_location").toString();

					JSONObject jsonObject1 = new JSONObject(startadd);
					double lat = Double.parseDouble(jsonObject1
							.getString("lat"));
					double lng = Double.parseDouble(jsonObject1
							.getString("lng"));

					startaddlatlng.add(new LatLng(lat, lng));

					//
					String endadd = subArray1.getJSONObject(i1)
							.getString("end_location").toString();

					JSONObject jsonObject41 = new JSONObject(endadd);
					double lat4 = Double.parseDouble(jsonObject41
							.getString("lat"));
					double lng4 = Double.parseDouble(jsonObject41
							.getString("lng"));

					endaddlatlng.add(new LatLng(lat4, lng4));

					// ////////////

					steps.add(subArray1.getJSONObject(i1).getString("steps")
							.toString());

					// //////////////
					String mska = subArray1.getJSONObject(i1)
							.getString("via_waypoint").toString();

					if (mska.equalsIgnoreCase("[]")) {
						via_waypoint.add(new LatLng(0, 0));
					} else {
						JSONArray subArray12 = new JSONArray(mska);

						for (int i11 = 0; i11 < subArray12.length(); i11++) {

							String locationstr = subArray12.getJSONObject(i11)
									.getString("location").toString();

							JSONObject jsonObject1111 = new JSONObject(
									locationstr);
							double lat1111 = Double.parseDouble(jsonObject1111
									.getString("lat"));
							double lng1111 = Double.parseDouble(jsonObject1111
									.getString("lng"));

							via_waypoint.add(new LatLng(lat1111, lng1111));

						}
					}
				}
			}

			// /////
			Log.d("Summary", "" + Summary);
			Log.d("startaddress", "" + startaddress);
			Log.d("endaddress", "" + endaddress);
			Log.d("startaddlatlng", "" + startaddlatlng);
			Log.d("endaddlatlng", "" + endaddlatlng);
			Log.d("via_waypoint", "" + via_waypoint);

			for (int i = 0; i < via_waypoint.size(); i++) {
				String asd = MapUtilityMethods.getAddress(
						UpdatePickupLocationFragmentActivity.this,
						via_waypoint.get(i).latitude,
						via_waypoint.get(i).longitude);
				via_waypointstrarr.add(asd);
			}
			Log.d("via_waypointstrarr", "" + via_waypointstrarr);
		}
	}

	private ArrayList<LatLng> decodePoly(String encoded) {
		ArrayList<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;
		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;
			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
			poly.add(position);
		}
		return poly;
	}

	// ///////////////////////
	// ///////

	// private class ConnectionTaskForShowMembersOnMap extends
	// AsyncTask<String, Void, Void> {
	//
	// @Override
	// protected void onPreExecute() {
	// }
	//
	// @Override
	// protected Void doInBackground(String... args) {
	// AuthenticateConnectionShowMembers mAuth1 = new
	// AuthenticateConnectionShowMembers();
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
	// if (exceptioncheck) {
	// exceptioncheck = false;
	// Toast.makeText(UpdatePickupLocationFragmentActivity.this,
	// getResources().getString(R.string.exceptionstring),
	// Toast.LENGTH_LONG).show();
	// return;
	// }
	//
	// if (checkpoolalreadyjoinresp.equalsIgnoreCase("fresh pool")) {
	//
	// } else {
	//
	// try {
	// JSONArray subArray = new JSONArray(checkpoolalreadyjoinresp);
	// for (int i = 0; i < subArray.length(); i++) {
	// try {
	// usermemname = subArray.getJSONObject(i)
	// .getString("MemberName").toString();
	// usermemnumber = subArray.getJSONObject(i)
	// .getString("MemberNumber").toString();
	// usermemlocadd = subArray.getJSONObject(i)
	// .getString("MemberLocationAddress")
	// .toString();
	// usermemloclatlong = subArray.getJSONObject(i)
	// .getString("MemberLocationlatlong")
	// .toString();
	// usermemimagename = subArray.getJSONObject(i)
	// .getString("MemberImageName").toString();
	// usermemst = subArray.getJSONObject(i)
	// .getString("Status").toString();
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// String[] latlong = usermemloclatlong.split(",");
	// LatLng lt = new LatLng(Double.parseDouble(latlong[0]),
	// Double.parseDouble(latlong[1]));
	// updatepoolmap.animateCamera(CameraUpdateFactory.newLatLng(lt));
	// updatelocationmarker.setVisibility(View.VISIBLE);
	// memberlocationlatlong = lt;
	// memberlocationaddress = usermemlocadd;
	//
	// joinpoollocationtext.setText(memberlocationaddress);
	// }
	//
	// if (showmembersresp.equalsIgnoreCase("No Members joined yet")) {
	//
	// } else {
	//
	// ShowMemberName.clear();
	// ShowMemberNumber.clear();
	// ShowMemberLocationAddress.clear();
	// ShowMemberLocationLatLong.clear();
	// ShowMemberImageName.clear();
	// ShowMemberStatus.clear();
	//
	// try {
	// JSONArray subArray = new JSONArray(showmembersresp);
	// for (int i = 0; i < subArray.length(); i++) {
	// try {
	// ShowMemberName.add(subArray.getJSONObject(i)
	// .getString("MemberName").toString());
	// ShowMemberNumber.add(subArray.getJSONObject(i)
	// .getString("MemberNumber").toString());
	// ShowMemberLocationAddress.add(subArray
	// .getJSONObject(i)
	// .getString("MemberLocationAddress")
	// .toString());
	// ShowMemberLocationLatLong.add(subArray
	// .getJSONObject(i)
	// .getString("MemberLocationlatlong")
	// .toString());
	// ShowMemberImageName.add(subArray.getJSONObject(i)
	// .getString("MemberImageName").toString());
	// ShowMemberStatus.add(subArray.getJSONObject(i)
	// .getString("Status").toString());
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// for (int i = 0; i < ShowMemberName.size(); i++) {
	// String[] latlong = ShowMemberLocationLatLong.get(i).split(
	// ",");
	// LatLng lt = new LatLng(Double.parseDouble(latlong[0]),
	// Double.parseDouble(latlong[1]));
	// updatepoolmap
	// .addMarker(new MarkerOptions()
	// .position(lt)
	// .snippet(String.valueOf(i))
	// .title(ShowMemberLocationAddress.get(i))
	// .icon(BitmapDescriptorFactory
	// .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
	// }
	// }
	//
	// updatepoolmap.setOnMarkerClickListener(new OnMarkerClickListener() {
	//
	// @Override
	// public boolean onMarkerClick(Marker arg0) {
	//
	// if (arg0.getSnippet().equals("start")) {
	//
	// } else if (arg0.getSnippet().equals("end")) {
	//
	// } else {
	// final Integer index = Integer.parseInt(arg0
	// .getSnippet());
	//
	// showAlertDialog(ShowMemberName.get(index),
	// ShowMemberNumber.get(index),
	// ShowMemberLocationAddress.get(index),
	// ShowMemberLocationLatLong.get(index),
	// ShowMemberImageName.get(index),
	// ShowMemberStatus.get(index));
	// }
	//
	// return true;
	// }
	//
	// });
	//
	// if (checkpoolalreadyjoinresp.equalsIgnoreCase("fresh pool")) {
	//
	// if (Seats.equalsIgnoreCase("0")) {
	//
	// AlertDialog.Builder builder = new AlertDialog.Builder(
	// UpdatePickupLocationFragmentActivity.this);
	// builder.setMessage("Sorry For Inconvience. Cab is Full");
	// builder.setCancelable(false);
	// builder.setNegativeButton("OK",
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog,
	// int id) {
	// dialog.cancel();
	// finish();
	// }
	// });
	// AlertDialog dialog = builder.show();
	// TextView messageText = (TextView) dialog
	// .findViewById(android.R.id.message);
	// messageText.setGravity(Gravity.CENTER);
	// dialog.show();
	//
	// }
	// }
	//
	// if (onedialog.isShowing()) {
	// onedialog.dismiss();
	// }
	//
	// }
	// }

	private class ConnectionTaskForShowMembersOnMap extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionShowMembers mAuth1 = new AuthenticateConnectionShowMembers();
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
				Toast.makeText(UpdatePickupLocationFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (showmembersresp.contains("Unauthorized Access")) {
				Log.e("UpdatePickupLocationFragmentActivity",
						"showmembersresp Unauthorized Access");
				Toast.makeText(UpdatePickupLocationFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (checkpoolalreadyjoinresp.equalsIgnoreCase("fresh pool")) {

			} else {

				// mycalculatorbtn.setVisibility(View.VISIBLE);

				try {

					JSONArray subArray = new JSONArray(checkpoolalreadyjoinresp);
					for (int i = 0; i < subArray.length(); i++) {
						try {
							usermemname = subArray.getJSONObject(i)
									.getString("MemberName").toString();
							usermemnumber = subArray.getJSONObject(i)
									.getString("MemberNumber").toString();
							usermemlocadd = subArray.getJSONObject(i)
									.getString("MemberLocationAddress")
									.toString();
							usermemloclatlong = subArray.getJSONObject(i)
									.getString("MemberLocationlatlong")
									.toString();

							usermemlocaddEnd = subArray.getJSONObject(i)
									.getString("MemberEndLocationAddress")
									.toString();
							usermemloclatlongEnd = subArray.getJSONObject(i)
									.getString("MemberEndLocationlatlong")
									.toString();

							usermemimagename = subArray.getJSONObject(i)
									.getString("MemberImageName").toString();
							usermemst = subArray.getJSONObject(i)
									.getString("Status").toString();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				String[] latlong = usermemloclatlong.split(",");
				LatLng lt = new LatLng(Double.parseDouble(latlong[0]),
						Double.parseDouble(latlong[1]));

				// mylocationmarker = updatepoolmap
				// .addMarker(new MarkerOptions()
				// .position(lt)
				// .snippet("mylocation")
				// .title(usermemlocadd)
				// .icon(BitmapDescriptorFactory
				// .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

				memberlocationlatlongFrom = lt;
				memberlocationaddressFrom = usermemlocadd;

				updatelocationmarker.setVisibility(View.GONE);
				buttonUpdateLocationMarker.setVisibility(View.GONE);
				// joinpoolchangelocationtext.setVisibility(View.GONE);
				joinpoollocationtext.setText(memberlocationaddressFrom);
				// updatepoolmap.setOnMapClickListener(null);
				// updatepoolmap.setOnCameraChangeListener(null);
			}

			if (showmembersresp.equalsIgnoreCase("No Members joined yet")) {

			} else {

				ShowMemberName.clear();
				ShowMemberNumber.clear();
				ShowMemberLocationAddress.clear();
				ShowMemberLocationLatLong.clear();
				ShowMemberImageName.clear();
				ShowMemberStatus.clear();
				ShowMemberLocationAddressEnd.clear();
				ShowMemberLocationLatLongEnd.clear();

				try {
					JSONArray subArray = new JSONArray(showmembersresp);
					for (int i = 0; i < subArray.length(); i++) {
						try {
							ShowMemberName.add(subArray.getJSONObject(i)
									.getString("MemberName").toString());
							ShowMemberNumber.add(subArray.getJSONObject(i)
									.getString("MemberNumber").toString());
							ShowMemberLocationAddress.add(subArray
									.getJSONObject(i)
									.getString("MemberLocationAddress")
									.toString());
							ShowMemberLocationAddressEnd.add(subArray
									.getJSONObject(i)
									.getString("MemberEndLocationAddress")
									.toString());

							ShowMemberLocationLatLong.add(subArray
									.getJSONObject(i)
									.getString("MemberLocationlatlong")
									.toString());

							ShowMemberLocationLatLongEnd.add(subArray
									.getJSONObject(i)
									.getString("MemberEndLocationlatlong")
									.toString());
							ShowMemberImageName.add(subArray.getJSONObject(i)
									.getString("MemberImageName").toString());
							ShowMemberStatus.add(subArray.getJSONObject(i)
									.getString("Status").toString());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

				// for (int i = 0; i < ShowMemberName.size(); i++) {
				// String[] latlongStart =
				// ShowMemberLocationLatLong.get(i).split(
				// ",");
				// String[] latlongEnd =
				// ShowMemberLocationLatLongEnd.get(i).split(
				// ",");
				// LatLng ltStart = new
				// LatLng(Double.parseDouble(latlongStart[0]),
				// Double.parseDouble(latlongStart[1]));
				// LatLng ltEnd = new LatLng(Double.parseDouble(latlongEnd[0]),
				// Double.parseDouble(latlongEnd[1]));
				// updatepoolmap .addMarker(new MarkerOptions()
				// .position(ltStart)
				// .snippet(String.valueOf(i))
				// .title(ShowMemberLocationAddress.get(i))
				// .icon(BitmapDescriptorFactory
				// .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
				// updatepoolmap
				// .addMarker(new MarkerOptions()
				// .position(ltEnd)
				// .snippet(String.valueOf(i))
				// .title(ShowMemberLocationAddressEnd.get(i))
				// .icon(BitmapDescriptorFactory
				// .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
				// }
			}
			// For single root

			// https://maps.googleapis.com/maps/api/directions/json?origin=-23.3246,-51.1489&destination=-23.2975,-51.2007&%20waypoints=optimize:true|-23.3246,-51.1489|-23.3206,-51.1459|-23.2975,-51.2007&sensor=false
			// String wayPoint="&waypoints=optimize:true";
			//
			// if(!showmembersresp.equalsIgnoreCase("No Members joined yet")||!checkpoolalreadyjoinresp.equalsIgnoreCase("fresh pool"))
			// {
			//
			// steps.clear();
			// Summary.clear();
			// startaddress.clear();
			// endaddress.clear();
			// startaddlatlng.clear();
			// endaddlatlng.clear();
			// listGeopoints.clear();
			// via_waypoint.clear();
			// via_waypointstrarr.clear();
			//
			// for(int i=0;i<rectlinesarr.size();i++){
			//
			// Polyline polyline =
			// updatepoolmap.addPolyline(rectlinesarr.get(i));
			// polyline.remove();
			//
			// }
			// updatepoolmap.clear();
			//
			// rectlinesarr.clear();
			//
			// //
			// http://maps.googleapis.com/maps/api/directions/json?origin=28.48971,77.062282&destination=28.6289146,77.2152869&waypoints=optimize:true|28.5440936,77.2359|28.549156,77.2527764|28.5252398,77.2543449&sensor=false
			//
			// if(!showmembersresp.equalsIgnoreCase("No Members joined yet")){
			// for(int i=0;i<ShowMemberLocationLatLong.size();i++){
			//
			// String latlong[]=ShowMemberLocationLatLong.get(i).split(",");
			// String latlong1[]=ShowMemberLocationLatLongEnd.get(i).split(",");
			//
			// wayPoint+="%7C"+latlong[0]+","+latlong[1]+"%7C"+latlong1[0]+","+latlong1[1];
			//
			//
			// }
			//
			// }
			// if(!checkpoolalreadyjoinresp.equalsIgnoreCase("fresh pool")){
			// String latlong[]=usermemloclatlong .split(",");
			// String latlong1[]=usermemloclatlongEnd .split(",");
			//
			// wayPoint+="%7C"+latlong[0]+","+latlong[1]+"%7C"+latlong1[0]+","+latlong1[1];
			//
			//
			// }
			//
			//
			// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// new ConnectionTaskForSingleRoot()
			// .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,wayPoint);
			// } else {
			// new ConnectionTaskForSingleRoot().execute(wayPoint);
			// }
			//
			// }
			// else{
			if (onedialog.isShowing()) {
				onedialog.dismiss();
			}
			LatLngBounds.Builder bc = null;

			for (int i = 0; i < rectlinesarr.size(); i++) {
				updatepoolmap.addPolyline(rectlinesarr.get(i));

				List<LatLng> points = rectlinesarr.get(i).getPoints();

				bc = new LatLngBounds.Builder();

				for (LatLng item : points) {
					bc.include(item);
				}
			}

			updatepoolmap.moveCamera(CameraUpdateFactory.newLatLngBounds(
					bc.build(), 50));

			// updatepoolmap.addMarker(new MarkerOptions()
			// .position(startaddlatlng.get(0))
			// .title(startaddress.get(0))
			// .snippet("start")
			// .icon(BitmapDescriptorFactory
			// .fromResource(R.drawable.start)));
			//
			// updatepoolmap
			// .addMarker(new MarkerOptions()
			// .position(endaddlatlng.get(0))
			// .title(endaddress.get(0))
			// .snippet("end")
			// .icon(BitmapDescriptorFactory
			// .fromResource(R.drawable.end)));
			// //}

			for (int i = 0; i < startaddlatlng.size(); i++) {

				if (i == 0) {
					updatepoolmap.addMarker(new MarkerOptions()
							.position(startaddlatlng.get(i))
							.title(startaddress.get(i))
							.snippet("start")
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.start)));
				} else if (i == (startaddlatlng.size() - 1)) {
					updatepoolmap.addMarker(new MarkerOptions()
							.position(endaddlatlng.get(i))
							.title(endaddress.get(i))
							.snippet("end")
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.end)));
				}

				// else{
				//
				// updatepoolmap
				// .addMarker(new MarkerOptions()
				// .position(startaddlatlng.get(i))
				// .title(startaddress.get(i))
				// .snippet("mylocation")
				// .icon(BitmapDescriptorFactory
				// .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
				//
				//
				// updatepoolmap
				// .addMarker(new MarkerOptions()
				// .position(endaddlatlng.get(i))
				// .title(endaddress.get(i))
				// .snippet("myEndlocation")
				// .icon(BitmapDescriptorFactory
				// .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
				//
				//
				//
				// }

			}

			updatepoolmap.setOnMarkerClickListener(new OnMarkerClickListener() {

				@Override
				public boolean onMarkerClick(Marker arg0) {

					if (arg0.getSnippet().equals("start")) {

					} else if (arg0.getSnippet().equals("end")) {

					} else {

						// final Integer index = Integer.parseInt(arg0
						// .getSnippet());
						//
						// showAlertDialog(ShowMemberName.get(index),
						// ShowMemberNumber.get(index),
						// ShowMemberLocationAddress.get(index),
						// ShowMemberLocationLatLong.get(index),
						// ShowMemberImageName.get(index),
						// ShowMemberStatus.get(index));
					}

					return true;
				}

			});

			if (checkpoolalreadyjoinresp.equalsIgnoreCase("fresh pool")) {

				if (Seats.equalsIgnoreCase("0")) {

					AlertDialog.Builder builder = new AlertDialog.Builder(
							UpdatePickupLocationFragmentActivity.this);
					builder.setMessage("Sorry For Inconvience. Cab is Full");
					builder.setCancelable(false);
					builder.setNegativeButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
									finish();
								}
							});
					AlertDialog dialog = builder.show();
					TextView messageText = (TextView) dialog
							.findViewById(android.R.id.message);
					messageText.setGravity(Gravity.CENTER);
					dialog.show();

				}
			}

		}
	}

	public class AuthenticateConnectionShowMembers {

		public AuthenticateConnectionShowMembers() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/ShowMemberOnMap.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair CabIdBasicNameValuePair = new BasicNameValuePair(
					"CabId", CabId);
			BasicNameValuePair MemberNumberBasicNameValuePair = new BasicNameValuePair(
					"MemberNumber", MemberNumberstr);

			String authString = CabId + MemberNumberstr;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(CabIdBasicNameValuePair);
			nameValuePairList.add(MemberNumberBasicNameValuePair);
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
				showmembersresp = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("showmembersresp", "" + stringBuilder.toString());
		}
	}

	// /

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		if (memberlocationlatlongFrom != null
				&& memberlocationlatlongTo != null) {
			double lat = memberlocationlatlongFrom.latitude;
			double longi = memberlocationlatlongFrom.longitude;
			String latlong = String.valueOf(lat) + "," + String.valueOf(longi);

			double latTo = memberlocationlatlongTo.latitude;
			double longiTo = memberlocationlatlongTo.longitude;
			String latlongTo = String.valueOf(latTo) + ","
					+ String.valueOf(longiTo);

			Intent intent = new Intent();
			intent.putExtra("memberlocationaddress", memberlocationaddressFrom);
			intent.putExtra("memberlocationlatlong", latlong);
			intent.putExtra("memberlocationaddressTo", memberlocationaddressTo);
			intent.putExtra("memberlocationlatlongTo", latlongTo);
			setResult(RESULT_OK, intent);
		}

		finish();

		super.onBackPressed();
	}

	// ///////////////////////
	// ///////

	private void showAlertDialog(String mname, String mnum, String mlocadd,
			String mloclatlon, String mimgname, String mstatus) {

		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.memberdeatilspopupbyuser);

		memimage = (ImageView) dialog.findViewById(R.id.memimage);

		// show The Image
		String url1 = GlobalVariables.ServiceUrl + "/ProfileImages/" + mimgname;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new DownloadImageTask().executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, url1);
		} else {
			new DownloadImageTask().execute(url1);
		}

		TextView memname = (TextView) dialog.findViewById(R.id.memname);
		memname.setText(mname.toUpperCase());
		dialog.show();

		TextView memlocationadd = (TextView) dialog
				.findViewById(R.id.memlocationadd);
		memlocationadd.setText(mlocadd);
		dialog.show();
	}

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			memimage.setImageBitmap(result);
		}
	}

	// //////////////////
	// ///////

	private class ConnectionTaskForupdatingapool extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionupdatingapool mAuth1 = new AuthenticateConnectionupdatingapool();
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

			if (onedialog.isShowing()) {
				onedialog.dismiss();
			}

			if (exceptioncheck) {
				exceptioncheck = false;
				Toast.makeText(UpdatePickupLocationFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (updatelocationpoolresp.contains("Unauthorized Access")) {
				Log.e("UpdatePickupLocationFragmentActivity",
						"updatelocationpoolresp Unauthorized Access");
				Toast.makeText(UpdatePickupLocationFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			try {
				JSONObject jsonObject = new JSONObject(updatelocationpoolresp);
				if (jsonObject.get("status").toString().equals("success")) {
					double lat = memberlocationlatlongFrom.latitude;
					double longi = memberlocationlatlongFrom.longitude;
					String latlong = String.valueOf(lat) + ","
							+ String.valueOf(longi);

					double latTo = memberlocationlatlongTo.latitude;
					double longiTo = memberlocationlatlongTo.longitude;
					String latlongTo = String.valueOf(latTo) + ","
							+ String.valueOf(longiTo);

					Intent intent = new Intent();
					intent.putExtra("memberlocationaddress",
							memberlocationaddressFrom);
					intent.putExtra("memberlocationlatlong", latlong);
					intent.putExtra("memberlocationaddressTo",
							memberlocationaddressTo);
					intent.putExtra("memberlocationlatlongTo", latlongTo);
					setResult(RESULT_OK, intent);
					finish();
				} else {
					Toast.makeText(UpdatePickupLocationFragmentActivity.this,
							"Something went wrong, please try again",
							Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(UpdatePickupLocationFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
			}

			// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// new ConnectionTaskForcheckpoolalreadyjoinednew()
			// .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			// } else {
			// new ConnectionTaskForcheckpoolalreadyjoinednew().execute();
			// }

		}

	}

	public class AuthenticateConnectionupdatingapool {

		public AuthenticateConnectionupdatingapool() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/updatelocationpool.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair CabIdBasicNameValuePair = new BasicNameValuePair(
					"CabId", CabId);
			BasicNameValuePair MemberNameBasicNameValuePair = new BasicNameValuePair(
					"MemberName", FullName);
			BasicNameValuePair MemberNumberBasicNameValuePair = new BasicNameValuePair(
					"MemberNumber", MemberNumberstr);
			BasicNameValuePair OwnerNameBasicNameValuePair = new BasicNameValuePair(
					"OwnerName", OwnerName);
			BasicNameValuePair OwnerNumberBasicNameValuePair = new BasicNameValuePair(
					"OwnerNumber", MobileNumber);
			BasicNameValuePair MemberLocationAddressBasicNameValuePair = new BasicNameValuePair(
					"MemberLocationAddress", memberlocationaddressFrom);
			BasicNameValuePair MemberEndLocationAddressBasicNameValuePair = new BasicNameValuePair(
					"MemberEndLocationAddress", memberlocationaddressTo);
			double lat = memberlocationlatlongFrom.latitude;
			double longi = memberlocationlatlongFrom.longitude;
			String latlong = String.valueOf(lat) + "," + String.valueOf(longi);
			BasicNameValuePair MemberLocationlatlongBasicNameValuePair = new BasicNameValuePair(
					"MemberLocationlatlong", latlong);

			double latEnd = memberlocationlatlongTo.latitude;
			double longiEnd = memberlocationlatlongTo.longitude;

			String latlongEnd = String.valueOf(latEnd) + ","
					+ String.valueOf(longiEnd);

			BasicNameValuePair MemberEndLocationlatlongBasicNameValuePair = new BasicNameValuePair(
					"MemberEndLocationlatlong", latlongEnd);

			BasicNameValuePair MessageBasicNameValuePair = new BasicNameValuePair(
					"Message", FullName
							+ " updated pickup location for the trip from "
							+ FromShortName + " to " + ToShortName);

			String authString = CabId + memberlocationaddressTo + latlongEnd
					+ memberlocationaddressFrom + latlong + FullName
					+ MemberNumberstr + FullName
					+ " updated pickup location for the trip from "
					+ FromShortName + " to " + ToShortName + OwnerName
					+ MobileNumber;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(CabIdBasicNameValuePair);
			nameValuePairList.add(MemberNameBasicNameValuePair);
			nameValuePairList.add(MemberNumberBasicNameValuePair);
			nameValuePairList.add(OwnerNameBasicNameValuePair);
			nameValuePairList.add(OwnerNumberBasicNameValuePair);
			nameValuePairList.add(MemberLocationAddressBasicNameValuePair);
			nameValuePairList.add(MemberLocationlatlongBasicNameValuePair);
			nameValuePairList.add(MessageBasicNameValuePair);
			nameValuePairList.add(MemberEndLocationAddressBasicNameValuePair);
			nameValuePairList.add(MemberEndLocationlatlongBasicNameValuePair);
			nameValuePairList.add(authValuePair);

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);
			Log.d("tagCabId", "" + CabIdBasicNameValuePair);
			Log.d("tagMemberNameBasicNameValuePair", ""
					+ MemberNameBasicNameValuePair);
			Log.d("tagMemberNumberBasicNameValuePair", ""
					+ MemberNumberBasicNameValuePair);
			Log.d("tagOwnerNameBasicNameValuePair", ""
					+ OwnerNameBasicNameValuePair);
			Log.d("tagOwnerNumberBasicNameValuePair", ""
					+ OwnerNumberBasicNameValuePair);
			Log.d("tagMemberLocationAddressBasicNameValuePair", ""
					+ MemberLocationAddressBasicNameValuePair);
			Log.d("tagMemberLocationlatlongBasicNameValuePair", ""
					+ MemberLocationlatlongBasicNameValuePair);
			Log.d("tagMessageBasicNameValuePair", ""
					+ MessageBasicNameValuePair);
			Log.d("tagMemberEndLocationAddressBasicNameValuePair", ""
					+ MemberEndLocationAddressBasicNameValuePair);
			Log.d("tagMemberEndLocationlatlongBasicNameValuePair", ""
					+ MemberEndLocationlatlongBasicNameValuePair);
			Log.d("tagMemberEndLocationAddressBasicNameValuePair", ""
					+ MemberEndLocationAddressBasicNameValuePair);

			httpPost.setEntity(urlEncodedFormEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);
			Log.d("httpResponseupdated", "" + inputStream.toString());

			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;

			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				updatelocationpoolresp = stringBuilder.append(bufferedStrChunk)
						.toString();
			}

			Log.d("updatelocationpoolresp", "" + stringBuilder.toString());
		}
	}

	// //////////////////
	// ///////

	// ///////////////////////
	// ///////

	private class ConnectionTaskForcheckpoolalreadyjoinednew extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectioncheckpoolalreadyjoinednew mAuth1 = new AuthenticateConnectioncheckpoolalreadyjoinednew();
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
				Toast.makeText(UpdatePickupLocationFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (checkpoolalreadyjoinresp.contains("Unauthorized Access")) {
				Log.e("UpdatePickupLocationFragmentActivity",
						"checkpoolalreadyjoinresp Unauthorized Access");
				Toast.makeText(UpdatePickupLocationFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new ConnectionTaskForDirectionsnew()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new ConnectionTaskForDirectionsnew().execute();
			}

		}
	}

	public class AuthenticateConnectioncheckpoolalreadyjoinednew {

		public AuthenticateConnectioncheckpoolalreadyjoinednew() {

		}

		public void connection() throws Exception {

			// Connect to google.com
			HttpClient httpClient = new DefaultHttpClient();
			String url_select = GlobalVariables.ServiceUrl
					+ "/checkpoolalreadyjoined.php";
			HttpPost httpPost = new HttpPost(url_select);
			BasicNameValuePair CabIdBasicNameValuePair = new BasicNameValuePair(
					"CabId", CabId);
			BasicNameValuePair MemberNumberBasicNameValuePair = new BasicNameValuePair(
					"MemberNumber", MemberNumberstr);

			String authString = CabId + MemberNumberstr;
			BasicNameValuePair authValuePair = new BasicNameValuePair("auth",
					GlobalMethods.calculateCMCAuthString(authString));

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			nameValuePairList.add(CabIdBasicNameValuePair);
			nameValuePairList.add(MemberNumberBasicNameValuePair);
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
				checkpoolalreadyjoinresp = stringBuilder.append(
						bufferedStrChunk).toString();
			}

			Log.d("checkpoolalreadyjoinresp", "" + stringBuilder.toString());
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

	private class ConnectionTaskForSingleRoot extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... args) {
			AuthenticateConnectionSingleRoot mAuth1 = new AuthenticateConnectionSingleRoot();
			try {
				mAuth1.wayPointUrl = args[0];
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

			if (onedialog.isShowing()) {
				onedialog.dismiss();
			}

			if (exceptioncheck) {
				exceptioncheck = false;
				Toast.makeText(UpdatePickupLocationFragmentActivity.this,
						getResources().getString(R.string.exceptionstring),
						Toast.LENGTH_LONG).show();
				return;
			}

			// int index = 0;
			rectlinesarr.clear();

			Random rnd = new Random();
			int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256),
					rnd.nextInt(256));
			for (int i = 0; i < steps.size(); i++) {

				ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();

				JSONArray subArray123;
				try {
					subArray123 = new JSONArray(steps.get(i));
					for (int i111 = 0; i111 < subArray123.length(); i111++) {
						String locationstr = subArray123.getJSONObject(i111)
								.getString("start_location").toString();

						JSONObject jsonObject11 = new JSONObject(locationstr);
						double lat1 = Double.parseDouble(jsonObject11
								.getString("lat"));
						double lng1 = Double.parseDouble(jsonObject11
								.getString("lng"));

						listGeopoints.add(new LatLng(lat1, lng1));

						// /
						String locationstr1 = subArray123.getJSONObject(i111)
								.getString("polyline").toString();

						JSONObject jsonObject111 = new JSONObject(locationstr1);
						String points = jsonObject111.getString("points");
						ArrayList<LatLng> arr = decodePoly(points);
						for (int j = 0; j < arr.size(); j++) {
							listGeopoints.add(new LatLng(arr.get(j).latitude,
									arr.get(j).longitude));
						}
						// /
						String locationstr11 = subArray123.getJSONObject(i111)
								.getString("end_location").toString();

						JSONObject jsonObject1111 = new JSONObject(
								locationstr11);
						double lat11 = Double.parseDouble(jsonObject1111
								.getString("lat"));
						double lng11 = Double.parseDouble(jsonObject1111
								.getString("lng"));

						listGeopoints.add(new LatLng(lat11, lng11));

						// Random rnd = new Random();
						// int color = Color.argb(255, rnd.nextInt(256),
						// rnd.nextInt(256), rnd.nextInt(256));

						PolylineOptions rectLine = new PolylineOptions().width(
								5).color(color);
						rectLine.addAll(listGeopoints);
						rectlinesarr.add(rectLine);

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			updatepoolmap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.joinpoolmap)).getMap();

			updatepoolmap.setMyLocationEnabled(true);

			// updatepoolmap.setOnMapClickListener(new OnMapClickListener() {
			//
			// @Override
			// public void onMapClick(LatLng point) {
			//
			//
			//
			// setOnMapClick(point);
			// }
			// });

			for (int i = 0; i < startaddlatlng.size(); i++) {

				// Log.d("duration:distance",
				// ""+durationList.get(i)+":"+distanceList.get(i));

				if (i == 0) {
					updatepoolmap.addMarker(new MarkerOptions()
							.position(startaddlatlng.get(i))
							.title(startaddress.get(i))
							.snippet("start")
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.start)));
				} else if (i == (startaddlatlng.size() - 1)) {
					updatepoolmap.addMarker(new MarkerOptions()
							.position(endaddlatlng.get(i))
							.title(endaddress.get(i))
							.snippet("end")
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.end)));
				} else {

					updatepoolmap
							.addMarker(new MarkerOptions()
									.position(startaddlatlng.get(i))
									.title(startaddress.get(i))
									.snippet("mylocation")
									.icon(BitmapDescriptorFactory
											.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

					updatepoolmap
							.addMarker(new MarkerOptions()
									.position(endaddlatlng.get(i))
									.title(endaddress.get(i))
									.snippet("myEndlocation")
									.icon(BitmapDescriptorFactory
											.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

				}

			}
			LatLngBounds.Builder bc = null;

			for (int i = 0; i < rectlinesarr.size(); i++) {
				updatepoolmap.addPolyline(rectlinesarr.get(i));

				List<LatLng> points = rectlinesarr.get(i).getPoints();

				bc = new LatLngBounds.Builder();

				for (LatLng item : points) {
					bc.include(item);
				}
			}

			bc.include(startaddlatlng.get(0));
			bc.include(endaddlatlng.get(endaddlatlng.size() - 1));
			updatepoolmap.moveCamera(CameraUpdateFactory.newLatLngBounds(
					bc.build(), 50));

			// if(usermemlocadd!=null){
			//
			// String[] latlong = usermemloclatlong.split(",");
			// LatLng lt = new LatLng(Double.parseDouble(latlong[0]),
			// Double.parseDouble(latlong[1]));
			// mylocationmarker = joinpoolmap.addMarker(new MarkerOptions()
			// .position(lt)
			// .snippet("mylocation")
			// .title(usermemlocadd)
			// .icon(BitmapDescriptorFactory
			// .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
			//
			//
			// String[] latlong1 = usermemloclatlongEnd.split(",");
			// LatLng lt1 = new LatLng(Double.parseDouble(latlong1[0]),
			// Double.parseDouble(latlong1[1]));
			// mylocationmarker = joinpoolmap.addMarker(new MarkerOptions()
			// .position(lt1)
			// .snippet("myEndlocation")
			// .title(usermemlocaddEnd)
			// .icon(BitmapDescriptorFactory
			// .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
			// }

			// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// new ConnectionTaskForShowMembersOnMap()
			// .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			// } else {
			// new ConnectionTaskForShowMembersOnMap().execute();
			// }
			//
			// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// new ConnectionTaskForOwnerLocation().executeOnExecutor(
			// AsyncTask.THREAD_POOL_EXECUTOR,
			// rideDetailsModel.getCabId());
			// } else {
			// new ConnectionTaskForOwnerLocation().execute(rideDetailsModel
			// .getCabId());
			// }

		}

	}

	public class AuthenticateConnectionSingleRoot {
		private String wayPointUrl;

		public AuthenticateConnectionSingleRoot() {

		}

		public void connection() throws Exception {

			String source = FromLocation.replaceAll(" ", "%20");
			String dest = ToLocation.replaceAll(" ", "%20");
			Address locationAddressFrom = null, locationAddressTo = null;

			String fromAdd = FromShortName;
			String toAdd = ToLocation;
			Geocoder fcoder = new Geocoder(
					UpdatePickupLocationFragmentActivity.this);
			try {
				ArrayList<Address> adresses = (ArrayList<Address>) fcoder
						.getFromLocationName(fromAdd, 50);

				for (Address add : adresses) {
					locationAddressFrom = add;
				}

				adresses = (ArrayList<Address>) fcoder.getFromLocationName(
						toAdd, 50);
				for (Address add : adresses) {
					locationAddressTo = add;
				}

			} catch (Exception e) {
				e.printStackTrace();

			}

			String src = locationAddressFrom.getLatitude() + ","
					+ locationAddressFrom.getLongitude();
			String des = locationAddressTo.getLatitude() + ","
					+ locationAddressTo.getLongitude();

			Log.d("src:", "" + src);
			Log.d("des", "" + des);

			// http://maps.googleapis.com/maps/api/directions/json?origin=28.48971,77.062282&destination=28.6289146,77.2152869&waypoints=optimize:true|28.5440936,77.2359|28.549156,77.2527764|28.5252398,77.2543449&sensor=false

			String url = "https://maps.googleapis.com/maps/api/directions/json?"
					+ "origin="
					+ src
					+ "&destination="
					+ des
					+ wayPointUrl
					+ "&sensor=false&units=metric&mode=driving&alternatives=true&key="
					+ GlobalVariables.GoogleMapsAPIKey;

			Log.d("url single path", "" + url);

			CompletePageResponse = new Communicator().executeHttpGet(url);

			CompletePageResponse = CompletePageResponse
					.replaceAll("\\\\/", "/");

			JSONObject jsonObject = new JSONObject(CompletePageResponse);

			String name = jsonObject.getString("routes");

			JSONArray subArray = new JSONArray(name);
			Summary.clear();

			for (int i = 0; i < subArray.length(); i++) {

				Summary.add(subArray.getJSONObject(i).getString("summary")
						.toString());

				String name1 = subArray.getJSONObject(i).getString("legs")
						.toString();

				JSONArray subArray1 = new JSONArray(name1);

				for (int i1 = 0; i1 < subArray1.length(); i1++) {

					// int i1 = 0;
					startaddress.add(subArray1.getJSONObject(i1)
							.getString("start_address").toString());
					endaddress.add(subArray1.getJSONObject(i1)
							.getString("end_address").toString());

					String startadd = subArray1.getJSONObject(i1)
							.getString("start_location").toString();

					JSONObject jsonObject1 = new JSONObject(startadd);
					double lat = Double.parseDouble(jsonObject1
							.getString("lat"));
					double lng = Double.parseDouble(jsonObject1
							.getString("lng"));

					startaddlatlng.add(new LatLng(lat, lng));

					//
					String endadd = subArray1.getJSONObject(i1)
							.getString("end_location").toString();

					JSONObject jsonObject41 = new JSONObject(endadd);
					double lat4 = Double.parseDouble(jsonObject41
							.getString("lat"));
					double lng4 = Double.parseDouble(jsonObject41
							.getString("lng"));

					endaddlatlng.add(new LatLng(lat4, lng4));

					// Code fro get distance and duration

					String duration = subArray1.getJSONObject(i1)
							.getString("duration").toString();
					JSONObject jsonObjectDuraton = new JSONObject(duration);

					// durationList.add(jsonObjectDuraton.getInt("value"));

					String distance = subArray1.getJSONObject(i1)
							.getString("distance").toString();
					JSONObject jsonObjectDistance = new JSONObject(distance);

					// distanceList.add(jsonObjectDistance.getInt("value"));
					// ////////////

					steps.add(subArray1.getJSONObject(i1).getString("steps")
							.toString());

					// //////////////
					String mska = subArray1.getJSONObject(i1)
							.getString("via_waypoint").toString();

					if (mska.equalsIgnoreCase("[]")) {
						via_waypoint.add(new LatLng(0, 0));
					} else {
						JSONArray subArray12 = new JSONArray(mska);

						for (int i11 = 0; i11 < subArray12.length(); i11++) {

							String locationstr = subArray12.getJSONObject(i11)
									.getString("location").toString();

							JSONObject jsonObject1111 = new JSONObject(
									locationstr);
							double lat1111 = Double.parseDouble(jsonObject1111
									.getString("lat"));
							double lng1111 = Double.parseDouble(jsonObject1111
									.getString("lng"));

							via_waypoint.add(new LatLng(lat1111, lng1111));

						}
					}
				}
			}

			// /////
			Log.d("Summary", "" + Summary);
			Log.d("startaddress", "" + startaddress);
			Log.d("endaddress", "" + endaddress);
			Log.d("startaddlatlng", "" + startaddlatlng);
			Log.d("endaddlatlng", "" + endaddlatlng);
			Log.d("via_waypoint", "" + via_waypoint);

			for (int i = 0; i < via_waypoint.size(); i++) {
				String asd = MapUtilityMethods.getAddress(
						UpdatePickupLocationFragmentActivity.this,
						via_waypoint.get(i).latitude,
						via_waypoint.get(i).longitude);
				via_waypointstrarr.add(asd);
			}
			Log.d("via_waypointstrarr", "" + via_waypointstrarr);
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		mycurrentlocationobject = location;
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

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
				dialog.setPositiveButton("Retry",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(
									DialogInterface paramDialogInterface,
									int paramInt) {
								Intent intent = getIntent();
								intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

								finish();

								startActivity(intent);

							}
						});
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
}
