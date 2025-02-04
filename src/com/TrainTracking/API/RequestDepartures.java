package com.TrainTracking.API;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.FileIO.FileLoggers.DepartureInfoLogger;
import com.Info.DepartureInfo;

public class RequestDepartures {

	private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

	public ArrayList<DepartureInfo> getDeparturesFromStation(String station) {
		if (station == null || station.isEmpty()) {
			System.out.println("Please give a valid station code");
			return null;
		}

		ArrayList<DepartureInfo> departureList = new ArrayList<>();

		String baseURL = "https://gateway.apiportal.ns.nl/reisinformatie-api/api/v2/departures?station=";
		String requestURL = baseURL + station;
		String response = NSAPI.talkToAPI(requestURL);

		if (response == null) {
			System.err.println("Problem with departure API");
			return null;
		}

		if (response.equals("NOT FOUND")) { // HTTP CODE 404, means station wasn't found?! What have you done?
			throw new RuntimeException("WTF have you done?!, that station doesn't exist");
		}

		JSONObject jsonResponse = new JSONObject(response.toString());

		JSONArray departures = jsonResponse.getJSONObject("payload").getJSONArray("departures");

		for (int i = 0; i < departures.length(); i++) {
			JSONObject departure = departures.getJSONObject(i);

			String direction;
			try {
				direction = departure.getString("direction");
			} catch (@SuppressWarnings("unused") JSONException e) {
				direction = "UNKOWN";
			}

			String trainNumber;
			try {
				trainNumber = departure.getString("name");
			} catch (@SuppressWarnings("unused") JSONException e) {
				trainNumber = "UNKOWN";
			}

			String plannedDateTime;
			try {
				plannedDateTime = fixDateTimeFormat(departure.getString("plannedDateTime"));
			} catch (@SuppressWarnings("unused") JSONException e) {
				plannedDateTime = "UNKOWN";
			}

			String actualDateTime;
			try {
				actualDateTime = fixDateTimeFormat(departure.getString("actualDateTime"));
			} catch (@SuppressWarnings("unused") JSONException e) {
				actualDateTime = "UNKOWN";
			}

			String plannedTrack;
			try {
				plannedTrack = departure.getString("plannedTrack");
			} catch (@SuppressWarnings("unused") JSONException e) {
				plannedTrack = "UNKOWN";
			}

			String actualTrack;
			try {
				actualTrack = departure.getString("actualTrack");
			} catch (@SuppressWarnings("unused") JSONException e) {
				actualTrack = "UNKOWN";
			}

			JSONObject product = departure.getJSONObject("product");

			String number;
			try {
				number = product.getString("number");
			} catch (@SuppressWarnings("unused") JSONException e) {
				number = "UNKOWN";
			}

			String categoryCode;
			try {
				categoryCode = product.getString("categoryCode");
			} catch (@SuppressWarnings("unused") JSONException e) {
				categoryCode = "UNKOWN";
			}

			String shortCategoryName;
			try {
				shortCategoryName = product.getString("shortCategoryName");
			} catch (@SuppressWarnings("unused") JSONException e) {
				shortCategoryName = "UNKOWN";
			}

			String longCategoryName;
			try {
				longCategoryName = product.getString("longCategoryName");
			} catch (@SuppressWarnings("unused") JSONException e) {
				longCategoryName = "UNKOWN";
			}

			String operatorName;
			try {
				operatorName = product.getString("operatorName");
			} catch (@SuppressWarnings("unused") JSONException e) {
				operatorName = "UNKOWN";
			}

			String operatorCode;
			try {
				operatorCode = product.getString("operatorCode");
			} catch (@SuppressWarnings("unused") JSONException e) {
				operatorCode = "UNKOWN";
			}

			String type;
			try {
				type = product.getString("type");
			} catch (@SuppressWarnings("unused") JSONException e) {
				type = "UNKOWN";
			}

			String trainCategory;
			try {
				trainCategory = departure.getString("trainCategory");
			} catch (@SuppressWarnings("unused") JSONException e) {
				trainCategory = "UNKOWN";
			}

			boolean cancelled;
			try {
				cancelled = departure.getBoolean("cancelled");
			} catch (@SuppressWarnings("unused") JSONException e) {
				cancelled = false;
			}

			// Route Stations as JSONArray
			JSONArray routeStations = departure.getJSONArray("routeStations");
			StringBuilder routeBuilder = new StringBuilder();
			for (int j = 0; j < routeStations.length(); j++) {
				JSONObject routeStation = routeStations.getJSONObject(j);
				String mediumName = routeStation.getString("mediumName");
				routeBuilder.append(mediumName);
				if (j < routeStations.length() - 1) {
					routeBuilder.append(" -> "); // Adding arrows between stations
				}
			}

			JSONArray messages = departure.getJSONArray("messages");
			List<Object> messagesList = new ArrayList<>(messages.toList());

			String departureStatus = departure.getString("departureStatus");

			DepartureInfo departureInfo = new DepartureInfo(direction, trainNumber, plannedDateTime, actualDateTime, plannedTrack, actualTrack, number, categoryCode, shortCategoryName, longCategoryName, operatorName, operatorCode, type, trainCategory, cancelled, messagesList, departureStatus, routeBuilder.toString());
			DepartureInfoLogger.writeToFile(departureInfo);
			departureList.add(departureInfo);
		}

		return departureList;
	}

	// Utility method to fix the timezone offset format from +0200 to +02:00
	private static String fixDateTimeFormat(String dateTime) {
		dateTime = dateTime.substring(0, 22) + ":" + dateTime.substring(22);

		return ZonedDateTime.parse(dateTime, formatter).toString();
	}
}