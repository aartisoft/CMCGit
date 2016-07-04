package com.clubmycab;

import java.util.ArrayList;
import java.util.HashMap;

import android.location.Location;

import com.clubmycab.utility.Log;
import com.google.android.gms.maps.model.LatLng;

public class FareCalculatorNewCarPool {

	Double perKmFare;

	ArrayList<String> memberNumbers;
	ArrayList<LatLng> pickupLatLng, dropLatLng, routePointsLatLng;
	ArrayList<Double> routePointsDistance;

	HashMap<String, Double> fareSplitHashMap;

	public FareCalculatorNewCarPool(ArrayList<String> numbers,
			ArrayList<LatLng> pickup, ArrayList<LatLng> drop,
			ArrayList<LatLng> routePoints, ArrayList<Double> distance,
			Double perKm) {
		super();

		this.memberNumbers = numbers;
		this.pickupLatLng = pickup;
		this.dropLatLng = drop;
		this.routePointsLatLng = routePoints;
		this.routePointsDistance = distance;
		this.perKmFare = perKm;

		this.fareSplitHashMap = new HashMap<String, Double>();
	}

	public HashMap<String, Double> getFareSplit() {

		HashMap<String, Double> distanceTravelledHashMap = new HashMap<String, Double>();

		for (int i = 0; i < memberNumbers.size(); i++) {
			String memberNum = memberNumbers.get(i);

			LatLng memberPickup = pickupLatLng.get(i);
			LatLng memberDrop = dropLatLng.get(i);

			// int memberPickupIndex = routePointsLatLng.indexOf(memberPickup);
			// int memberDropIndex = routePointsLatLng.indexOf(memberDrop);
			int memberPickupIndex = -1;
			int memberDropIndex = -1;

			Location pickLocation = new Location("");
			pickLocation.setLatitude(memberPickup.latitude);
			pickLocation.setLongitude(memberPickup.longitude);
			Location dropLocation = new Location("");
			dropLocation.setLatitude(memberDrop.latitude);
			dropLocation.setLongitude(memberDrop.longitude);

			for (int j = 0; j < routePointsLatLng.size(); j++) {
				Location routePointLocation = new Location("");
				routePointLocation
						.setLatitude(routePointsLatLng.get(j).latitude);
				routePointLocation
						.setLongitude(routePointsLatLng.get(j).longitude);

				float distance = pickLocation.distanceTo(routePointLocation);
				Log.d("FareCalculatorNew", "Distance pick : " + distance);
				if (distance <= 1000) {
					memberPickupIndex = j;
				}

				distance = dropLocation.distanceTo(routePointLocation);
				Log.d("FareCalculatorNew", "Distance drop : " + distance);
				if (distance <= 1000) {
					memberDropIndex = j;
				}
			}

			Log.d("FareCalculatorNew", "memberPickupIndex : "
					+ memberPickupIndex + " memberDropIndex : "
					+ memberDropIndex);
			if (memberPickupIndex != -1 && memberDropIndex != -1) {

				Double memberDistanceTravelled = 0.0;

				for (int j = (memberPickupIndex + 1); j <= memberDropIndex; j++) {
					memberDistanceTravelled += routePointsDistance.get(j);
				}

				distanceTravelledHashMap
						.put(memberNum, memberDistanceTravelled);
			}
		}

		Double totalDistance = 0.0;
		for (String member : distanceTravelledHashMap.keySet()) {
			totalDistance += distanceTravelledHashMap.get(member);
		}

		Log.d("FareCalculatorNew", "distanceTravelledHashMap : "
				+ distanceTravelledHashMap + " totalDistance : "
				+ totalDistance);

		for (String member : distanceTravelledHashMap.keySet()) {
			fareSplitHashMap.put(member, distanceTravelledHashMap.get(member)
					* perKmFare / 1000.0);
		}

		fareSplitHashMap.put("tripTotalFare", perKmFare * totalDistance / 1000.0);

		return fareSplitHashMap;
	}

}
