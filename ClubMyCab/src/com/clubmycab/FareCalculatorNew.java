package com.clubmycab;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gms.maps.model.LatLng;

public class FareCalculatorNew {

	Double totalFare;

	ArrayList<String> memberNumbers;
	ArrayList<LatLng> pickupLatLng, dropLatLng, routePointsLatLng;
	ArrayList<Double> routePointsDistance;

	HashMap<String, Double> fareSplitHashMap;

	public FareCalculatorNew(ArrayList<String> numbers,
			ArrayList<LatLng> pickup, ArrayList<LatLng> drop,
			ArrayList<LatLng> routePoints, ArrayList<Double> distance,
			Double total) {
		super();

		this.memberNumbers = numbers;
		this.pickupLatLng = pickup;
		this.dropLatLng = drop;
		this.routePointsLatLng = routePoints;
		this.routePointsDistance = distance;
		this.totalFare = total;

		this.fareSplitHashMap = new HashMap<String, Double>();
	}

	public HashMap<String, Double> getFareSplit() {

		HashMap<String, Double> distanceTravelledHashMap = new HashMap<String, Double>();

		for (int i = 0; i < memberNumbers.size(); i++) {
			String memberNum = memberNumbers.get(i);

			LatLng memberPickup = pickupLatLng.get(i);
			LatLng memberDrop = dropLatLng.get(i);

			int memberPickupIndex = routePointsLatLng.indexOf(memberPickup);
			int memberDropIndex = routePointsLatLng.indexOf(memberDrop);

			if (memberPickupIndex != -1 && memberDropIndex != -1) {

				Double memberDistanceTravelled = 0.0;

				for (int j = memberPickupIndex; j <= memberDropIndex; j++) {
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

		for (String member : distanceTravelledHashMap.keySet()) {
			fareSplitHashMap.put(member,
					(distanceTravelledHashMap.get(member) / totalDistance)
							* totalFare);
		}

		return fareSplitHashMap;
	}
}
