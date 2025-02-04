package com.TrainTracking.API;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.FileIO.FileLoggers.ArrivalInfoLogger;
import com.Info.ArrivalInfo;

public class RequestArrivals {

	private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

	public ArrayList<ArrivalInfo> getArrivalsFromStation(String station) {
		if (station == null || station.isEmpty()) {
			System.out.println("Please give a valid station code");
			return null;
		}

		ArrayList<ArrivalInfo> arrivalsList = new ArrayList<>();

		String baseURL = "https://gateway.apiportal.ns.nl/reisinformatie-api/api/v2/arrivals?station=";
		String requestURL = baseURL + station;
		String response = NSAPI.talkToAPI(requestURL);

		if (response == null) {
			System.err.println("Problem with arrival API");
			return null;
		}

		if (response.equals("NOT FOUND")) { // HTTP CODE 404, means station wasn't found?! What have you done?
			throw new RuntimeException("WTF have you done?!, that station doesn't exist");
		}

		JSONObject jsonResponse = new JSONObject(response);
		JSONArray arrivals = jsonResponse.getJSONObject("payload").getJSONArray("arrivals");

		for (int i = 0; i < arrivals.length(); i++) {
			JSONObject arrival = arrivals.getJSONObject(i);

			String origin;
			try {
				origin = arrival.getString("origin");
			} catch (@SuppressWarnings("unused") JSONException e) {
				origin = "UNKOWN";
			}

			String trainNumber;
			try {
				trainNumber = arrival.getString("name");
			} catch (@SuppressWarnings("unused") JSONException e) {
				trainNumber = "UNKOWN";
			}

			String plannedDateTime;
			try {
				plannedDateTime = fixDateTimeFormat(arrival.getString("plannedDateTime"));
			} catch (@SuppressWarnings("unused") JSONException e) {
				plannedDateTime = "UNKOWN";
			}

			String actualDateTime;
			try {
				actualDateTime = fixDateTimeFormat(arrival.getString("actualDateTime"));
			} catch (@SuppressWarnings("unused") JSONException e) {
				actualDateTime = "UNKOWN";
			}

			String plannedTrack;
			try {
				plannedTrack = arrival.getString("plannedTrack");
			} catch (@SuppressWarnings("unused") JSONException e) {
				plannedTrack = "UNKOWN";
			}

			String actualTrack;
			try {
				actualTrack = arrival.getString("actualTrack");
			} catch (@SuppressWarnings("unused") JSONException e) {
				actualTrack = "UNKOWN";
			}

			JSONObject product = arrival.getJSONObject("product");

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
				trainCategory = arrival.getString("trainCategory");
			} catch (@SuppressWarnings("unused") JSONException e) {
				trainCategory = "UNKOWN";
			}

			boolean cancelled;
			try {
				cancelled = arrival.getBoolean("cancelled");
			} catch (@SuppressWarnings("unused") JSONException e) {
				cancelled = false;
			}

			JSONArray messages = arrival.getJSONArray("messages");
			List<Object> messagesList = new ArrayList<>(messages.toList());

			String arrivalStatus = arrival.getString("arrivalStatus");

			ArrivalInfo arrivalInfo = new ArrivalInfo(origin, trainNumber, plannedDateTime, actualDateTime, plannedTrack, actualTrack, number, categoryCode, shortCategoryName, longCategoryName, operatorName, operatorCode, type, trainCategory, cancelled, messagesList, arrivalStatus);
			ArrivalInfoLogger.writeToFile(arrivalInfo);
			arrivalsList.add(arrivalInfo);
		}

		return arrivalsList;
	}

	// Utility method to fix the timezone offset format from +0200 to +02:00
	private static String fixDateTimeFormat(String dateTime) {
		dateTime = dateTime.substring(0, 22) + ":" + dateTime.substring(22);

		return ZonedDateTime.parse(dateTime, formatter).toString();
	}
}