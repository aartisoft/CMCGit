package com.clubmycab;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;

import com.clubmycab.utility.GlobalVariables;
import com.clubmycab.utility.Log;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class FareCalculator {

	private Context context;

	private JSONObject ownerJsonObject;
	private ArrayList<JSONObject> memberJsonArray;
	private double tripTotalFare;

	public HashMap<String, Double> hashMapMembersFareSplit;

	public FareCalculatorInterface fareCalculatorInterface;

	public static final String JSON_NAME_OWNER_START_ADDRESS = "startAddressOwner";
	public static final String JSON_NAME_OWNER_END_ADDRESS = "endAddressOwner";
	public static final String JSON_NAME_OWNER_START_LATLNG = "startLatLngOwner";
	public static final String JSON_NAME_OWNER_END_LATLNG = "endLatLngOwner";
	public static final String JSON_NAME_OWNER_NAME = "nameOwner";
	public static final String JSON_NAME_MEMBER_NAME = "nameMember";
	public static final String JSON_NAME_MEMBER_LOCATION_ADDRESS = "locationAddressMember";
	public static final String JSON_NAME_MEMBER_LOCATION_LATLNG = "locationLatLngMember";
	public static final String JSON_NAME_DISTANCE_PREV_POINT = "distancePrevPoint";
	public static final String JSON_NAME_DISTANCE_TOTAL = "distanceTotal";

	private boolean exceptioncheck;

	private ArrayList<String> steps = new ArrayList<String>();
	private ArrayList<String> Summary = new ArrayList<String>();
	private ArrayList<String> startaddress = new ArrayList<String>();
	private ArrayList<String> endaddress = new ArrayList<String>();
	private ArrayList<LatLng> startaddlatlng = new ArrayList<LatLng>();
	private ArrayList<LatLng> endaddlatlng = new ArrayList<LatLng>();
	private ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();
	private ArrayList<LatLng> via_waypoint = new ArrayList<LatLng>();
	private ArrayList<String> via_waypointstrarr = new ArrayList<String>();

	private ArrayList<PolylineOptions> rectlinesarr = new ArrayList<PolylineOptions>();

	public FareCalculator(Context context, JSONObject owner,
			ArrayList<JSONObject> members) {
		super();

		this.context = context;
		this.ownerJsonObject = owner;
		this.memberJsonArray = members;

		if (context instanceof FareCalculatorInterface) {
			this.fareCalculatorInterface = (FareCalculatorInterface) context;
		}
	}

	public interface FareCalculatorInterface {
		public void sendFareSplitHashMap(HashMap<String, Double> hashMap);
	}

	public void calculateFareSplit(Double totalFare) {

		Log.d("FareCalculator", "calculateFareSplit ownerJsonObject : "
				+ ownerJsonObject + " memberJsonArray : " + memberJsonArray
				+ " totalFare : " + totalFare);

		try {

			tripTotalFare = totalFare;

			ConnectionTaskForMinDistance connectionTaskForMinDistance = new ConnectionTaskForMinDistance();
			Address address = geocodeAddress(ownerJsonObject.get(
					JSON_NAME_OWNER_START_ADDRESS).toString());
			ownerJsonObject.put(JSON_NAME_OWNER_START_LATLNG, new LatLng(
					address.getLatitude(), address.getLongitude()));
			connectionTaskForMinDistance.startAddress = address;
			address = geocodeAddress(ownerJsonObject.get(
					JSON_NAME_OWNER_END_ADDRESS).toString());
			ownerJsonObject.put(JSON_NAME_OWNER_END_LATLNG,
					new LatLng(address.getLatitude(), address.getLongitude()));
//			connectionTaskForMinDistance.endAddress = address;

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				connectionTaskForMinDistance
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				connectionTaskForMinDistance.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
			fareCalculatorInterface.sendFareSplitHashMap(null);
		}
	}

	private class ConnectionTaskForMinDistance extends
			AsyncTask<String, Void, Void> {

		public Address startAddress;    //, endAddress;

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(String... args) {

			try {

				ArrayList<JSONObject> members = new ArrayList<JSONObject>();
				for (int i = 0; i < memberJsonArray.size(); i++) {
					members.add(i, memberJsonArray.get(i));
				}

				ArrayList<JSONObject> membersSequential = new ArrayList<JSONObject>();
				// adding owner start location
				JSONObject jsonObject = new JSONObject();
				jsonObject.put(JSON_NAME_MEMBER_LOCATION_ADDRESS,
						ownerJsonObject.get(JSON_NAME_OWNER_START_ADDRESS));
				jsonObject.put(JSON_NAME_MEMBER_LOCATION_LATLNG,
						ownerJsonObject.get(JSON_NAME_OWNER_START_LATLNG));
				jsonObject.put(JSON_NAME_MEMBER_NAME,
						ownerJsonObject.get(JSON_NAME_OWNER_NAME));
				membersSequential.add(jsonObject);

				do {
					LatLng start;
					if (membersSequential.size() > 1) { // first element is
														// owner
						start = (LatLng) membersSequential.get(
								membersSequential.size() - 1).get(
								JSON_NAME_MEMBER_LOCATION_LATLNG);
					} else {
						start = new LatLng(startAddress.getLatitude(),
								startAddress.getLongitude());
					}

					// LatLng end = new LatLng(endAddress.getLatitude(),
					// endAddress.getLongitude());
					ArrayList<Double> memberDistance = new ArrayList<Double>();

					for (int i = 0; i < members.size(); i++) {

						LatLng end = (LatLng) members.get(i).get(
								JSON_NAME_MEMBER_LOCATION_LATLNG);

						Log.d("FareCalculator", "start : " + start + " end : "
								+ end);

						AuthenticateConnectionGetMinDistance mAuth1 = new AuthenticateConnectionGetMinDistance(
								start, end);

						Double distance = mAuth1.connection();
						memberDistance.add(i, distance);
					}

					Double minDist = Collections.min(memberDistance);
					int minDistIndex = memberDistance.indexOf(minDist);
					jsonObject = members.get(minDistIndex);
					jsonObject.put(JSON_NAME_DISTANCE_PREV_POINT, minDist);
					membersSequential.add(jsonObject);
					members.remove(minDistIndex);

				} while (members.size() > 0);

				LatLng lastMemLatLng = (LatLng) membersSequential.get(
						membersSequential.size() - 1).get(
						JSON_NAME_MEMBER_LOCATION_LATLNG);
				LatLng endAddLatLng = (LatLng) ownerJsonObject
						.get(JSON_NAME_OWNER_END_LATLNG);
				AuthenticateConnectionGetMinDistance mAuth1 = new AuthenticateConnectionGetMinDistance(
						lastMemLatLng, endAddLatLng);

				Double distance = mAuth1.connection();

				// adding owner end location
				jsonObject = new JSONObject();
				jsonObject.put(JSON_NAME_MEMBER_LOCATION_ADDRESS,
						ownerJsonObject.get(JSON_NAME_OWNER_END_ADDRESS));
				jsonObject.put(JSON_NAME_MEMBER_LOCATION_LATLNG,
						ownerJsonObject.get(JSON_NAME_OWNER_END_LATLNG));
				jsonObject.put(JSON_NAME_DISTANCE_PREV_POINT, distance);
				membersSequential.add(jsonObject);

				// calculating total distance travelled
				for (int i = 0; i < membersSequential.size(); i++) {

					Double totalDistance = 0.0;
					for (int j = (i + 1); j < membersSequential.size(); j++) {
						totalDistance += ((Double) membersSequential.get(j)
								.get(JSON_NAME_DISTANCE_PREV_POINT));
					}
					membersSequential.get(i).put(JSON_NAME_DISTANCE_TOTAL,
							totalDistance);
				}

				// calculating fare split
				Double cumulativeManMeters = 0.0;
				for (int i = 0; i < membersSequential.size(); i++) {
					cumulativeManMeters += ((Double) membersSequential.get(i)
							.get(JSON_NAME_DISTANCE_TOTAL));
				}

				hashMapMembersFareSplit = new HashMap<String, Double>();
				for (int i = 0; i < (membersSequential.size() - 1); i++) {
					Double fareProportionate = ((Double) membersSequential.get(
							i).get(JSON_NAME_DISTANCE_TOTAL) / cumulativeManMeters)
							* tripTotalFare;

					hashMapMembersFareSplit.put(
							membersSequential.get(i).get(JSON_NAME_MEMBER_NAME)
									.toString(), fareProportionate);

				}

				hashMapMembersFareSplit.put("tripTotalFare", tripTotalFare);

				Log.d("FareCalculator", "memberJsonArray : " + memberJsonArray);
				Log.d("FareCalculator", "membersSequential : "
						+ membersSequential);
//				Log.d("FareCalculator", "hashMapMembersFareSplit : "
//						+ hashMapMembersFareSplit);

				fareCalculatorInterface
						.sendFareSplitHashMap(hashMapMembersFareSplit);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				exceptioncheck = true;
				e.printStackTrace();
				fareCalculatorInterface.sendFareSplitHashMap(null);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {

			if (exceptioncheck) {
				exceptioncheck = false;
				// Toast.makeText(CheckPool.this,
				// "Something went wrong, please try again!",
				// Toast.LENGTH_LONG).show();
				return;
			}

		}

	}

	public class AuthenticateConnectionGetMinDistance {

		private LatLng startLatLng, endLatLng;

		public AuthenticateConnectionGetMinDistance(LatLng start, LatLng end) {
			startLatLng = start;
			endLatLng = end;
		}

		public Double connection() throws Exception {

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

			String url = "https://maps.googleapis.com/maps/api/directions/json?"
					+ "origin="
					+ Double.toString(startLatLng.latitude)
					+ ","
					+ Double.toString(startLatLng.longitude)
					+ "&destination="
					+ Double.toString(endLatLng.latitude)
					+ ","
					+ Double.toString(endLatLng.longitude)
					+ "&sensor=false&units=metric&mode=driving&alternatives=true&key="+GlobalVariables.GoogleMapsAPIKey;

			Log.d("url", "" + url);

			String CompletePageResponse = new Communicator()
					.executeHttpGet(url);

			CompletePageResponse = CompletePageResponse
					.replaceAll("\\\\/", "/");

			JSONObject jsonObject = new JSONObject(CompletePageResponse);

			String name = jsonObject.getString("routes");

			JSONArray subArray = new JSONArray(name);

			ArrayList<Double> distanceArrayList = new ArrayList<Double>();

			for (int i = 0; i < subArray.length(); i++) {

				Summary.add(subArray.getJSONObject(i).getString("summary")
						.toString());

				String name1 = subArray.getJSONObject(i).getString("legs")
						.toString();

				JSONArray subArray1 = new JSONArray(name1);

				Double legTotalDist = 0.0;

				for (int i1 = 0; i1 < subArray1.length(); i1++) {

					String distanceString = subArray1.getJSONObject(i1)
							.getString("distance");
					JSONObject distanceObject = new JSONObject(distanceString);
					legTotalDist += Double.parseDouble(distanceObject.get(
							"value").toString());
				}

				distanceArrayList.add(i, legTotalDist);
			}

			Double minDistance = Collections.min(distanceArrayList);
			int minDistIndex = distanceArrayList.indexOf(Collections
					.min(distanceArrayList));

			Log.d("FareCalculator", "distanceArrayList : " + distanceArrayList
					+ " minDistance : " + minDistance + " minDistIndex : "
					+ minDistIndex);

			return minDistance;

			// // /////
			// Log.d("Summary", "" + Summary);
			// Log.d("startaddress", "" + startaddress);
			// Log.d("endaddress", "" + endaddress);
			// Log.d("startaddlatlng", "" + startaddlatlng);
			// Log.d("endaddlatlng", "" + endaddlatlng);
			// Log.d("via_waypoint", "" + via_waypoint);
			//
			// for (int i = 0; i < via_waypoint.size(); i++) {
			// String asd = getAddress(context,
			// via_waypoint.get(i).latitude,
			// via_waypoint.get(i).longitude);
			// via_waypointstrarr.add(asd);
			// }
			// Log.d("via_waypointstrarr", "" + via_waypointstrarr);
		}
	}

	public String getAddress(Context ctx, double latitude, double longitude) {
		StringBuilder result = new StringBuilder();
		try {
			Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
			List<Address> addresses = geocoder.getFromLocation(latitude,
					longitude, 1);

			if (addresses.size() > 0) {
				Address address = addresses.get(0);

				for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
					result.append(address.getAddressLine(i) + " ");
				}
			}
		} catch (IOException e) {
			Log.e("tag", e.getMessage());
		}

		return result.toString();
	}

//	private ArrayList<LatLng> decodePoly(String encoded) {
//		ArrayList<LatLng> poly = new ArrayList<LatLng>();
//		int index = 0, len = encoded.length();
//		int lat = 0, lng = 0;
//		while (index < len) {
//			int b, shift = 0, result = 0;
//			do {
//				b = encoded.charAt(index++) - 63;
//				result |= (b & 0x1f) << shift;
//				shift += 5;
//			} while (b >= 0x20);
//			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
//			lat += dlat;
//			shift = 0;
//			result = 0;
//			do {
//				b = encoded.charAt(index++) - 63;
//				result |= (b & 0x1f) << shift;
//				shift += 5;
//			} while (b >= 0x20);
//			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
//			lng += dlng;
//
//			LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
//			poly.add(position);
//		}
//		return poly;
//	}

	private Address geocodeAddress(String addressString) {
		Address addressReturn = null;
		Geocoder geocoder = new Geocoder(context);
		try {
			ArrayList<Address> arrayList = (ArrayList<Address>) geocoder
					.getFromLocationName(addressString, 1);
			Log.d("geocodeAddress", "geocodeAddress : " + arrayList.toString());
			if (arrayList.size() > 0) {
				addressReturn = arrayList.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return addressReturn;
	}

}
